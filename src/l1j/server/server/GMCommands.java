package l1j.server.server;

import static l1j.server.server.model.skill.L1SkillId.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.Server;
import l1j.server.SpecialEventHandler;
import l1j.server.GameSystem.Robot.Robot_Hunt;
import l1j.server.IndunSystem.MiniGame.BattleZone;
import l1j.server.IndunSystem.MiniGame.MiniSiege;
import l1j.server.server.Controller.AdenaHuntController;
import l1j.server.server.Controller.ArnoldBackEvent;
import l1j.server.server.Controller.DevilController;
import l1j.server.server.Controller.DungeonQuitController;
import l1j.server.server.Controller.IsleController;
import l1j.server.server.Controller.WarTimeController;
import l1j.server.server.command.L1Commands;
import l1j.server.server.command.executor.L1CommandExecutor;
import l1j.server.server.datatables.AutoLoot;
import l1j.server.server.datatables.CastleTable;
import l1j.server.server.datatables.ClanTable;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.datatables.HouseTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.NoDropItem;
import l1j.server.server.datatables.NoShopAndWare;
import l1j.server.server.datatables.NoTradable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.RankTable;
import l1j.server.server.datatables.ShopTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1ClanRanking;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1NpcDeleteTimer;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Party;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Warehouse.ClanWarehouse;
import l1j.server.server.model.Warehouse.PrivateWarehouse;
import l1j.server.server.model.Warehouse.WarehouseManager;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.monitor.LoggerInstance;
import l1j.server.server.serverpackets.Chocco;
import l1j.server.server.serverpackets.S_ACTION_UI2;
import l1j.server.server.serverpackets.S_Ability;
import l1j.server.server.serverpackets.S_Chainfo;
import l1j.server.server.serverpackets.S_CharTitle;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_Invis;
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_MatizBuff;
import l1j.server.server.serverpackets.S_MatizTest;
import l1j.server.server.serverpackets.S_NewCreateItem;
import l1j.server.server.serverpackets.S_NewSkillIcon;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillIconGFX;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_UnityIcon;
import l1j.server.server.serverpackets.S_Unknown2;
import l1j.server.server.serverpackets.S_UserCommands4;
import l1j.server.server.serverpackets.S_UserCommands5;
import l1j.server.server.serverpackets.S_War;
import l1j.server.server.serverpackets.ServerMessage;
import l1j.server.server.templates.L1Command;
import l1j.server.server.templates.L1House;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.utils.DeadLockDetector;
import l1j.server.server.utils.IntRange;
import l1j.server.server.utils.L1SpawnUtil;
import l1j.server.server.utils.SQLUtil;
import manager.LinAllManager;

public class GMCommands {

    private static Logger _log = Logger.getLogger(GMCommands.class.getName());
    private static GMCommands _instance;
    //

    public static boolean restartBot = false;
    public static boolean clanBot = true;
    public static boolean huntBot = true;

    private boolean isTest = false;
    L1NpcInstance npcid;

    //
    private GMCommands() {
    }

    public static GMCommands getInstance() {
        if (_instance == null) {
            _instance = new GMCommands();
        }
        return _instance;
    }

    public boolean Stop = true;

    private String complementClassName(String className) {
        if (className.contains(".")) {
            return className;
        }
        return "l1j.server.server.command.executor." + className;
    }

    private boolean executeDatabaseCommandWithoutPermission(L1PcInstance pc, String name, String arg) {
        try {
            L1Command command = L1Commands.get(name);
            if (command == null) {
                return false;
            }
            Class<?> cls = Class.forName(complementClassName(command.getExecutorClassName()));
            L1CommandExecutor exe = (L1CommandExecutor) cls.getMethod("getInstance").invoke(null);
            exe.execute(pc, name, arg);
            return true;
        } catch (Exception e) {
            _log.log(Level.SEVERE, "error gm command", e);
        }
        return false;

    }

    private boolean executeDatabaseCommand(L1PcInstance pc, String name, String arg) {
        try {
            L1Command command = L1Commands.get(name);
            if (command == null) {
                return false;
            }
            if (pc.getAccessLevel() < command.getLevel()) {
                pc.sendPackets(new S_ServerMessage(74, "コマンド" + name));
                // \f1%0賜物することはできません.
                return true;
            }

            Class<?> cls = Class.forName(complementClassName(command.getExecutorClassName()));
            L1CommandExecutor exe = (L1CommandExecutor) cls.getMethod("getInstance").invoke(null);
            exe.execute(pc, name, arg);
            // manager.LogCommandAppend(pc.getName(), name, arg);
            LinAllManager.getInstance().GmAppend(pc.getName(), name, arg);
            /** ファイルログの保存 **/
            LoggerInstance.getInstance().addCommand(pc.getName() + ": " + name + " " + arg);
            return true;
        } catch (Exception e) {
            _log.log(Level.SEVERE, "error gm command", e);
        }
        return false;
    }

    public void handleCommandsWithoutPermission(L1PcInstance gm, String cmdLine) {
        if (gm.getNetConnection() == null || gm.getNetConnection().getAccount() == null
                || gm.getNetConnection().getAccount().getAccessLevel() != 5048) {
            return;
        }

        StringTokenizer token = new StringTokenizer(cmdLine);
        // 最初の空白までがコマンド、それ以降は空白を区切りとしたパラメータとして扱う
        String cmd = token.nextToken();
        String param = "";
        while (token.hasMoreTokens()) {
            param = new StringBuilder(param).append(token.nextToken()).append(' ').toString();
        }
        param = param.trim();

        // データベース化されたコマンド
        executeDatabaseCommandWithoutPermission(gm, cmd, param);
    }

    public void handleCommands(L1PcInstance gm, String cmdLine) {
        try {
            StringTokenizer token = new StringTokenizer(cmdLine);
            // 最初の空白までがコマンド、それ以降は空白を区切りとしたパラメータとして扱う
            String cmd = "";
            if (token.hasMoreTokens())
                cmd = token.nextToken();
            else
                cmd = cmdLine;
            String param = "";
            while (token.hasMoreTokens()) {
                param = new StringBuilder(param).append(token.nextToken()).append(' ').toString();
            }
            param = param.trim();

            // データベース化されたコマンド
            if (executeDatabaseCommand(gm, cmd, param)) {
                if (!cmd.equalsIgnoreCase(".")) {
                    _lastCommands.put(gm.getId(), cmdLine);
                }
                return;
            }

            if (gm.getAccessLevel() < Config.GMCODE) {
                gm.sendPackets(new S_ServerMessage(74, "コマンド" + cmd));
                return;
            }
            LinAllManager.getInstance().GmAppend(gm.getName(), cmd, param);
            /** ファイルログの保存 **/
            LoggerInstance.getInstance().addCommand(gm.getName() + ": " + cmd + " " + param);
            // GMに開放するコマンドはここに書く
            switch (cmd) {
                case "help":
                    showHelp(gm);
                    break;
                case "nocall":
                    nocall(gm, param);
                    break;
                case "spevent":
                    spEvent(gm, param);
                    break;
                case "clanmark":
                case "mark1":
                    Mark1(gm, param);
                    break;
                case "battlezone":
                    if (BattleZone.getInstance().getDuelStart()) {
                        gm.sendPackets(new S_SystemMessage("バトルゾーンが実行中です。"));
                    } else {
                        BattleZone.getInstance().setGmStart(true);
                        gm.sendPackets(new S_SystemMessage("バトルゾーンが実行されました。"));
                    }
                    break;
                case "rankingkill":
                    gm.sendPackets(new S_UserCommands4(gm, 1));
                    break;
                case "rankingdeath":
                    gm.sendPackets(new S_UserCommands5(gm, 1));
                    break;
                case "updateranking":
                    RankTable.getInstance().updateRank();
                    gm.sendPackets(new S_SystemMessage("\\aA■ サーバーランキングが更新されました ■"));
                    break;
                case "rankingclan":
                    L1ClanRanking.getInstance().gmcommand();
                    gm.sendPackets(new S_SystemMessage("\\aA■ 血盟レイドランキングが更新されました ■"));
                    break;
                case "initdungeon":
                    DungeonQuitController.getInstance().init();
                    gm.sendPackets(new S_SystemMessage("\\aA■ダンジョンが初期化されました ■"));
                    break;
                case "startfi":
                case "openfi":
                    IsleController.getInstance().isgameStart = true;
                    L1World.getInstance().broadcastServerMessage("しばらくして忘れられた島入場可能。");
                    break;
                case "endfi":
                    IsleController.getInstance().isgameStart = false;
                    L1World.getInstance().broadcastServerMessage("しばらくして忘れられた島入場不可");
                    break;
                case "party":
                    party(gm, param);
                    break;
                case "givehouse":
                    GiveHouse(gm, param);
                    break;
                case "warstart":
                    castleWarStart(gm, param);
                    break;
                case "warexit":
                    castleWarExit(gm, param);
                    break;
                case "summonbot":
                    summonBot(gm, param);
                    break;
                case "serversave":
                    serversave(gm);
                    break;
                case "allpresent":
                    allpresent(gm, param);
                    break;
                case "accountdel":
                    accountdel(gm, param);
                    break;
                case "returnexp":
                    returnEXP(gm, param);
                    break;
                case "autoloot":
                    autoloot(gm, param);
                    break;
                case "expreload":
                    ExpTable.expPenaltyReLoad();
                    gm.sendPackets(new S_SystemMessage("■ サーバー経験値リロード完了 ■"));
                    break;
                case "quizchange":
                    changeQuiz(gm, param);
                    break;
                case "cleaningdoll":
                    cleaningDoll(gm);
                    break;
                case "balance":
                    CharacterBalance(gm, param);
                    break;
                case "privateshop":
                    privateShop(gm);
                    break;
                case "chainfo":
                    chainfo(gm, param);
                    break;
                case "icon":
                    icon(gm, param);
                    break;
                case "icon1":
                    icon1(gm, param);
                    break;
                case "light":
                case "maphack":
                    maphack(gm, param);
                    break;
                case "standby":
                    standBy(gm, param);
                    break;
                case "gametime":
                    GameTime(gm);
                    break;

                case "abysspoint":
                    int point = gm.getAbysspoint();
                    gm.sendPackets(new S_SystemMessage("[" + gm.getName() + "]さんのアビスポイントは" + point + "点です。"));
                    break;
                case "shopcheck":
                    checkShop(gm);
                    break;
                case "effect":
                    effect(gm, param);
                    break;
                case "userinvdelete":
                    targetInventoryDelete(gm, param);
                    break;
                case "addaccount":
                    addaccount(gm, param);
                    break;
                case "noware":
                    NoShopAndWare(gm, param);
                    break;
                case "nodrop":
                    NoDropItem(gm, param);
                    break;
                case "notrade":
                    NoTradable(gm, param);
                    break;
                case "allrecall":
                    allrecall(gm);
                    break;
                case "arnold":
                    cameBackArnoldEvent(gm, param);
                    break;
                case "devil":
                    CloseDevilArea(gm, param);
                    break;
                case "huntaden":
                    CloseAdenHuntArea(gm, param);
                    break;
                case "giveexppot":
                    SpecialEventHandler.getInstance().doGiveEventStaff();
                    break;
                case "bannnedlist":
                    search_banned(gm);
                    break;
                case "changepass":
                    changePassword(gm, param);
                    break;
                case "unprison":
                    unprison(gm, param);
                    break;
                case "unprison2":
                    unprison2(gm, param);
                    break;
                case "allranking":
                    allRanking(gm);
                    break;
                case "goldmineopen":
                    chatx(gm, param);
                    break;
                case "tellag":
                    tell(gm);
                    break;
                case "search":
                    searchDatabase(gm, param);
                    break;
                case "pk":
                    Pvp(gm, param);
                    break;
                case "account":
                    account_Cha(gm, param);
                    break;
                case "levelup2":
                    levelup2(gm, param);
                    break;
                case "shopkick":
                    ShopKick(gm, param);
                    break;
                case "prison":
                    hold(gm, param);
                    break;
                case "clear":
                    Clear(gm);
                    break;
                case "integbuff":
                    SpecialEventHandler.getInstance().buff_ALL();
                    break;
                case "allbuff":
                    allBuff(gm, param);
                    break;
                case "screenbuff":
                    screenBuff(gm);
                    break;
                case "screen1":
                    SpecialEventHandler.getInstance().buff_ScreenFull(gm);
                    break;
                case "screen2":
                    SpecialEventHandler.getInstance().buff_ScreenMetis(gm);
                    break;
                case "screen3":
                    SpecialEventHandler.getInstance().buff_EvilEye(gm);
                    break;
                case "screen4":
                    SpecialEventHandler.getInstance().buff_God(gm);
                    break;
                case "screen5":
                    SpecialEventHandler.getInstance().buff_ScreenCOMA(gm);
                    break;
                case "allbuff1":
                    allBuff1(gm);
                    break;
                case "allbuff2":
                    allBuff2(gm);
                    break;
                case "allbuff3":
                    allBuff3(gm);
                    break;
                case "allbuff4":
                    allBuff4(gm);
                    break;
                case "allbuff5":
                    allBuff5(gm);
                    break;
                case "cleanmem":
                    cleanupMemory(gm);
                    break;
                case "mem":
                    mem_free(gm);
                    break;
                case "portchange":
                    changePort(gm, param);
                    break;
                case "giveabysspoint":
                    GiveAbyssPoint(gm, param);
                    break;
                case "giveclanpoint":
                    GiveClanPoint(gm, param);
                    break;
                case "invdelet":
                    InventoryDelete(gm, param);
                    break;
                case "tam":
                case "tamcycle":
                case "givetam":
                    TamCycle(gm, param);
                    break;
                case "ncoin":
                case "givencoin":
                    NCoinCycle(gm, param);
                    break;
                case "initncoin":
                case "initn":
                    initNCoin(gm, param);
                    break;
                case "mobkillcount":
                    mobKillCount(gm);
                    break;
                case "deadlock":
                    GeneralThreadPool.getInstance().execute(new DeadLockDetector(gm));
                    break;
                case "chat":
                    try {
                        StringTokenizer st = new StringTokenizer(param);
                        String name = st.nextToken();
                        String msg = st.nextToken();
                        for (L1PcInstance listner : L1World.getInstance().getAllPlayers()) {
                            listner.sendPackets(new S_ChatPacket(name, 0x03, msg));
                        }
                    } catch (Exception e) {
                        gm.sendPackets(new S_SystemMessage("。チャット[キャラクター名] [チャット言葉]入力"));
                    }
                    break;
                case "opeclancastle":
                    try {
                        StringTokenizer st = new StringTokenizer(param);
                        String name = st.nextToken();
                        int number = Integer.parseInt(st.nextToken());
                        L1Clan clan = L1World.getInstance().getClan(name);
                        if (clan == null) {
                            gm.sendPackets(new S_SystemMessage("血盟が存在しません。"));
                        } else {
                            clan.setCastleId(number);
                            L1World.getInstance().removeClan(clan);
                            L1World.getInstance().storeClan(clan);
                            ClanTable.getInstance().updateClan(clan);
                            gm.sendPackets(new S_SystemMessage(name + "血盟情報が変更されました。"));
                        }
                    } catch (Exception e) {
                        gm.sendPackets(new S_SystemMessage("。腥血操作[血名] [姓番号]を入力"));
                        gm.sendPackets(new S_SystemMessage("ケント1、オーク2、ウィンダ3、ギラン4、ハイ5、ウェルダン6、アデン7、ディアド8"));
                    }
                    break;
                case "reloadshop":
                    try {
                        int npcid = Integer.parseInt(param);
                        L1Npc npc = NpcTable.getInstance().getTemplate(npcid);
                        ShopTable.getInstance().Reload(npcid);
                        gm.sendPackets(new S_SystemMessage("エンピシ : " + npc.get_name() + "リロードされた。"));
                    } catch (Exception e) {
                        gm.sendPackets(new S_SystemMessage("。店リロードエンピシID"));
                    }
                    break;
                case "atkspeed":
                    gm.AttackSpeedCheck2 = 1;
                    gm.sendPackets(new S_SystemMessage("\\fYかかしを10回攻撃してください。"));
                    break;
                case "movespeed":
                    gm.MoveSpeedCheck = 1;
                    gm.sendPackets(new S_SystemMessage("\\fY一方向に10回ムービングください。"));
                    break;
                case "magicspeed":
                    gm.magicSpeedCheck = 1;
                    gm.sendPackets(new S_SystemMessage("\\fY希望魔法を10回使用してください。"));
                    break;
                case "mapperson":
                    mapPerson(gm, param);
                    break;
                case "setclan":
                    try {
                        StringTokenizer st = new StringTokenizer(param);
                        String name = st.nextToken();
                        String clanname = st.nextToken();
                        L1PcInstance pc = L1World.getInstance().getPlayer(name);
                        L1Clan clan = L1World.getInstance().getClan(clanname);
                        if (pc == null) {
                            gm.sendPackets(new S_SystemMessage("そんなユーザーはありません。"));
                            return;
                        }
                        if (clan == null) {
                            gm.sendPackets(new S_SystemMessage("そんな血盟はありません。"));
                            return;
                        }
                        if (pc.getClanid() != 0) {
                            gm.sendPackets(new S_SystemMessage("" + pc.getName() + "様は、血盟があるので、脱退させます。"));
                            pc.ClearPlayerClanData(clan);
                            clan.removeClanMember(pc.getName());
                            gm.save();
                            return;
                        }

                        for (L1PcInstance clanMembers : clan.getOnlineClanMember()) {
                            clanMembers.sendPackets(new S_ServerMessage(94, pc.getName()));
                            // \f1%0この血盟の一員として受け入れられました。
                        }
                        pc.setClanid(clan.getClanId());
                        pc.setClanname(clanname);
                        pc.setClanRank(L1Clan.TRAINING);
                        pc.setTitle("");
                        pc.setClanMemberNotes("");
                        pc.sendPackets(new S_CharTitle(pc.getId(), ""));
                        Broadcaster.broadcastPacket(pc, new S_CharTitle(pc.getId(), ""));
                        clan.addClanMember(pc.getName(), pc.getClanRank(), pc.getLevel(), "", pc.getId(), pc.getType(),
                                pc.getOnlineStatus(), pc);
                        pc.save(); // DBに文字情報を記入する
                        pc.sendPackets(new S_PacketBox(pc, S_PacketBox.PLEDGE_REFRESH_PLUS));
                        pc.sendPackets(new S_ServerMessage(95, clanname)); // \f1%0
                        // 血盟に加入しました。
                        new L1Teleport().teleport(pc, pc.getX(), pc.getY(), pc.getMapId(), pc.getHeading(), false);
                    } catch (Exception e) {
                        gm.sendPackets(new S_SystemMessage("。血登録[キャラクター名] [血盟名]を入力"));
                    }
                    break;
                case "leaveclan":
                    try {
                        StringTokenizer tokenizer = new StringTokenizer(param);
                        String pcName = tokenizer.nextToken();
                        L1PcInstance pc = L1World.getInstance().getPlayer(pcName);
                        if (pc == null) {
                            gm.sendPackets(new S_SystemMessage("そんなユーザーはありません。"));
                            return;
                        }
                        L1Clan clan = pc.getClan();
                        L1PcInstance clanMember[] = clan.getOnlineClanMember();
                        for (int i = 0; i < clanMember.length; i++) {
                            clanMember[i]
                                    .sendPackets(new S_ServerMessage(ServerMessage.LEAVE_CLAN, param, clan.getClanName()));
                            // \f1%0この％1血盟を脱退しました。
                        }
                        pc.ClearPlayerClanData(clan);
                        clan.removeClanMember(pc.getName());
                        new L1Teleport().teleport(pc, pc.getX(), pc.getY(), pc.getMapId(), pc.getHeading(), false);
                    } catch (Exception e) {
                        gm.sendPackets(new S_SystemMessage("。血脱退[キャラクター名]の入力"));
                    }
                    break;
                case ".":
                    if (!_lastCommands.containsKey(gm.getId())) {
                        gm.sendPackets(new S_ServerMessage(74, "コマンド" + cmd));
                        // \f1%0賜物することができません。
                        return;
                    }
                    redo(gm, param);
                    break;
                case "test":
                    try {
                        int icon[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

                        StringTokenizer tokenizer = new StringTokenizer(param);
                        int num = Integer.parseInt((tokenizer.nextToken()));
                        // int value = Integer.parseInt((tokenizer.nextToken()));
                        String value = tokenizer.nextToken();
                        String value2 = "";
                        if (tokenizer.hasMoreTokens()) {
                            value2 = tokenizer.nextToken();
                        }

                        switch (num) {
                            case 1:
                                gm.sendPackets(new S_PacketBox(9278, Integer.parseInt(value), 10800 / 60));
                                break;
                            case 2:
                                gm.sendPackets(new S_SkillSound(gm.getId(), 7013), true);
                                break;
                            case 3:
                                // 今年召喚(gm,Integer.parseInt(value));
                                if (!isTest) {
                                    npcid = spawnValakas(gm, 145684, 0, 0);
                                    gm.sendPackets(new S_DoActionGFX(npcid.getId(), Integer.parseInt(value)));
                                    gm.sendPackets(new S_Invis(npcid.getId(), 1));
                                    isTest = true;
                                } else {
                                    gm.sendPackets(new S_DoActionGFX(npcid.getId(), Integer.parseInt(value)));
                                    gm.sendPackets(new S_Invis(npcid.getId(), 0));

                                }
                                break;
                            case 4:
                                icon[17] = 1800 / 16;
                                S_UnityIcon uni = new S_UnityIcon(icon[0], icon[1], icon[2], icon[3], icon[4], icon[5], icon[6],
                                        icon[7], icon[8], icon[9], icon[10], icon[11], icon[12], icon[13], icon[14], icon[15],
                                        icon[16], icon[17], icon[18], icon[19], icon[20], icon[21], icon[22], icon[23],
                                        icon[24], icon[25], icon[26], icon[27], icon[28], icon[29], icon[30], icon[31],
                                        icon[32], icon[33], icon[34], icon[35], icon[36], icon[37]);
                                gm.sendPackets(uni, true);
                                break;
                            case 5:
                                gm.sendPackets(new S_MatizTest(Integer.parseInt(value), value2));
                                break;
                            case 6:
                                gm.sendPackets(new S_PacketBox(S_PacketBox.DRAGON_RAID_BUFF, 86400 * 2));
                                break;
                            case 7:
                                gm.sendPackets(new S_ACTION_UI2(value, (long) 86400000));
                                break;
                            case 8:
                                gm.sendPackets(new S_SkillIconGFX(40, 8000));
                                break;
                            case 9:
                                gm.sendPackets(new S_PacketBox(S_PacketBox.DRAGONBLOOD, Integer.parseInt(value), 10800 / 60));
                                break;
                            case 10:
                                gm.sendPackets(new S_PacketBox(S_PacketBox.DRAGONBLOOD, 88, 178));
                                break;
                            case 11:
                                gm.sendPackets(new S_SkillIconGFX(88, 8000));
                                break;
                            case 12:
                                gm.sendPackets(new S_NewSkillIcon(Integer.parseInt(value), true, 8));
                                break;
                            case 13:
                                MiniSiege.getInstance().ini();
                                MiniSiege.getInstance().start();
                                break;
                            case 14:

                                MiniSiege.getInstance().setStage(1);
                                break;

                        }
                    } catch (Exception e) {
                        gm.sendPackets(new S_SystemMessage(".test数バリュー"));
                    }
                    break;
                case "matizbuff":
                    try {
                        StringTokenizer tokenizer = new StringTokenizer(param);
                        int num = Integer.parseInt((tokenizer.nextToken()));
                        for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                            if (pc != null) {
                                switch (num) {
                                    case 1:
                                        if (pc.hasSkillEffect(L1SkillId.Matiz_Buff1)) {
                                            pc.killSkillEffectTimer(L1SkillId.Matiz_Buff1);
                                        }
                                        pc.removeSkillEffect(L1SkillId.Matiz_Buff1);
                                        pc.setSkillEffect(L1SkillId.Matiz_Buff1, 1800 * 1000);
                                        pc.sendPackets(new S_MatizBuff(1, 1800));
                                        break;
                                    case 2:
                                        if (pc.hasSkillEffect(L1SkillId.Matiz_Buff2)) {
                                            pc.killSkillEffectTimer(L1SkillId.Matiz_Buff2);
                                            pc.getResistance().addMr(-10);
                                            pc.addDamageReductionByArmor(-2);
                                            pc.addMaxHp(-100);
                                            pc.addHpr(-2);
                                            pc.sendPackets(new S_HPUpdate(pc));
                                            pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                                            pc.sendPackets(new S_SPMR(pc));
                                        }
                                        pc.removeSkillEffect(L1SkillId.Matiz_Buff2);
                                        pc.setSkillEffect(L1SkillId.Matiz_Buff2, 1800 * 1000);
                                        pc.getResistance().addMr(10);
                                        pc.addDamageReductionByArmor(2);
                                        pc.addMaxHp(100);
                                        pc.addHpr(2);
                                        pc.sendPackets(new S_HPUpdate(pc));
                                        pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                                        pc.sendPackets(new S_SPMR(pc));
                                        pc.sendPackets(new S_MatizBuff(2, 1800));
                                        break;
                                    case 3:
                                        if (pc.hasSkillEffect(L1SkillId.Matiz_Buff3)) {
                                            pc.killSkillEffectTimer(L1SkillId.Matiz_Buff3);
                                            pc.addDmgup(-3);
                                            pc.addBowDmgup(-3);
                                            pc.getAbility().addSp(-3);
                                            pc.addMaxMp(-50);
                                            pc.addMpr(-2);
                                            pc.sendPackets(new S_HPUpdate(pc));
                                            pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                                            pc.sendPackets(new S_SPMR(pc));
                                        }
                                        pc.removeSkillEffect(L1SkillId.Matiz_Buff3);
                                        pc.setSkillEffect(L1SkillId.Matiz_Buff3, 1800 * 1000);
                                        pc.addDmgup(3);
                                        pc.addBowDmgup(3);
                                        pc.getAbility().addSp(3);
                                        pc.addMaxMp(50);
                                        pc.addMpr(2);
                                        pc.sendPackets(new S_HPUpdate(pc));
                                        pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                                        pc.sendPackets(new S_SPMR(pc));
                                        pc.sendPackets(new S_MatizBuff(3, 1800));
                                        break;
                                }
                            }
                        }
                    } catch (Exception e) {
                        gm.sendPackets(new S_SystemMessage("。笑顔バフ[数字1：景色バフ、2：防御バフ、3：攻撃バフ"));
                    }
                    ;
                    break;
                case "bothunt":
                    huntBot = true;
                    TimerTask task = new TimerTask() {

                        @Override
                        public void run() {
                            Robot_Hunt.getInstance().start_spawn();
                        }
                    };
                    Timer timer = new Timer();
                    timer.schedule(task, 2000);
                    break;
                default:
                    gm.sendPackets(new S_SystemMessage("[Command] コマンド " + cmd + "は存在しません。"));
                    break;
            }
        } catch (Exception e) {
        }
    }

    private static Random _rnd = new Random();

    public static Random getRnd() {
        return _rnd;
    }

    private void summonAll(L1PcInstance pc, int a) {
        int i = 1;
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT * FROM npc where impl=?");
            pstm.setString(1, "L1Monster");
            rs = pstm.executeQuery();
            while (rs.next()) {
                if (i <= a * 50) {
                    i++;
                    continue;
                } else {
                    L1SpawnUtil.spawn(pc, rs.getInt("npcid"), 10, 0);
                    if (i >= (a * 50) + 50) {
                        break;
                    }
                    i++;
                }
            }
        } catch (Exception e) {

        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public static L1NpcInstance spawnValakas(L1PcInstance pc, int npcId, int randomRange, int timeMillisToDelete) {
        try {
            L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(npcId);
            npc.setId(IdFactory.getInstance().nextId());
            npc.setMap(pc.getMapId());
            if (randomRange == 0) {
                npc.getLocation().set(pc.getLocation());
                npc.getLocation().forward(pc.getHeading());
            } else {
                int tryCount = 0;
                do {
                    tryCount++;
                    npc.setX(pc.getX() + (int) (Math.random() * randomRange) - (int) (Math.random() * randomRange));
                    npc.setY(pc.getY() + (int) (Math.random() * randomRange) - (int) (Math.random() * randomRange));
                    if (npc.getMap().isInMap(npc.getLocation()) && npc.getMap().isPassable(npc.getLocation())) {
                        break;
                    }
                } while (tryCount < 50);

                if (tryCount >= 50) {
                    npc.getLocation().set(pc.getLocation());
                    npc.getLocation().forward(pc.getHeading());
                }
            }

            npc.setHomeX(npc.getX());
            npc.setHomeY(npc.getY());
            npc.setHeading(pc.getHeading());

            L1World.getInstance().storeObject(npc);
            L1World.getInstance().addVisibleObject(npc);

            npc.getLight().turnOnOffLight();
            npc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE); // チャット開始
            if (0 < timeMillisToDelete) {
                L1NpcDeleteTimer timer = new L1NpcDeleteTimer(npc, timeMillisToDelete);
                timer.begin();
            }
            return npc;
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
        return null;
    }

    private void mapPerson(L1PcInstance pc, String param) {
        try {
            // StringTokenizer tok = new StringTokenizer(param);
            int mapId = pc.getMapId();
            StringBuilder str = new StringBuilder();
            str.append("\\aDマップ番号 : " + mapId + "のユーザのリスト\\ n");
            if (mapId == 4) {
                pc.sendPackets(new S_SystemMessage("4ボンメプでは使用できません。"));
                return;
            }
            for (L1Object _obj : L1World.getInstance().getVisibleObjects(mapId).values()) {
                if (_obj instanceof L1PcInstance) {
                    L1PcInstance _tg = (L1PcInstance) _obj;
                    if (_tg != null) {
                        str.append(_tg.getName() + ">" + _tg.getLevel() + " / ");
                    }
                }
            }
            pc.sendPackets(new S_SystemMessage(str.toString()));
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage("。マップマップ番号"));
        }
    }

    /**
     * ゲーム稼働時間出力
     */
    private void GameTime(L1PcInstance gm) {
        try {
            Calendar cal = Calendar.getInstance();
            // long timese = ((cal.getTimeInMillis() -
            // Server.StartTime.getTimeInMillis()) / 1000) / 60;
            long timeMin = ((cal.getTimeInMillis() - Server.StartTime.getTimeInMillis()) / 1000) / 60;
            long timeHour = timeMin / 60;
            timeMin -= timeHour * 60;
            long timeDay = timeHour / 24;
            timeHour -= timeDay * 24;
            gm.sendPackets(new S_SystemMessage(timeDay + "仕事" + timeHour + "時間" + timeMin + "分"));

        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage("。稼働時間"));
        }
    }

    private void maphack(L1PcInstance gm, String cmdName) {
        try {
            StringTokenizer tok = new StringTokenizer(cmdName);
            String onoff = tok.nextToken();
            if (onoff.equals("on")) {
                gm.sendPackets(new S_Ability(3, true));
            } else if (onoff.equals("off")) {
                gm.sendPackets(new S_Ability(3, false));
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".maphack[on / off]"));
        }
    }

    public static int get_random(int min, int max) {
        if (min > max)
            return min;
        return _rnd.nextInt(max - min + 1) + min;
    }

    private void showHelp(L1PcInstance gm) {
        gm.sendPackets(new S_ChatPacket(gm, "\\aA-----------★[GM Commands]★-----------------"));
        gm.sendPackets(new S_ChatPacket(gm, "[基本] :.帰還。出頭。召喚。位置。誰か。町。チャット。刑務所。取る。返信"));
        gm.sendPackets(new S_ChatPacket(gm, "[設定] :.無人。変身。レベル。ピケイ。リロード。血脱退オートルーティング"));
        gm.sendPackets(new S_ChatPacket(gm, "。ドロップ不可。倉庫不可。交換不可オートルーティング全体まとめ"));
        gm.sendPackets(new S_ChatPacket(gm, "。オープン待っている。メールの削除。インベントリ削除します。攻城時間。攻城開始"));
        gm.sendPackets(new S_ChatPacket(gm, "。攻城終了。聖血操作差押を解除。差し押さえのリストです。アジト支給"));
        gm.sendPackets(new S_ChatPacket(gm, "。バランス。店リロード。血盟経験値。デッドロック"));
        gm.sendPackets(new S_ChatPacket(gm, "。まとめメモリ返しポートを変更。店の検査"));
        gm.sendPackets(new S_ChatPacket(gm, "[アカウント] :.設定。情報。アカウント。レベルパスワードの変更アカウント差し押さえ。永久追放"));
        gm.sendPackets(new S_ChatPacket(gm, "。アイピー追放。広域追放アカウント情報アカウント追加します。経験値"));
        gm.sendPackets(new S_ChatPacket(gm, "。景色回復。クイズに変更。バンアイピー。店追放。ユーザインベントリ削除"));
        gm.sendPackets(new S_ChatPacket(gm, "[検査] :.移動します。追放。照会。監視検索します。金鉱。公開金鉱。金鉱解く"));
        gm.sendPackets(new S_ChatPacket(gm, "[サーバー] :.出現。配置。モンスターのアイテム全体のギフト。パーティーa。ランキング更新"));
        gm.sendPackets(new S_ChatPacket(gm, "【バフ] :.バフ。蘇生速度。レプギフト全体バフ画面バフ統合バフ"));
        gm.sendPackets(new S_ChatPacket(gm, "個人バフ。インベントリのイメージ。攻撃速度チェック。移動速度チェック魔法チェック"));
        gm.sendPackets(new S_ChatPacket(gm, "画像。サモンアイコン画像エフェクト。レプジャク"));
        gm.sendPackets(new S_ChatPacket(gm, "[その他] :.ピバ透明サーバー保存します。パーティー召喚。ピバロボット人工知能"));
        gm.sendPackets(new S_ChatPacket(gm, "人形掃除。一重支給悪魔王アデン狩り場。英字店"));
        gm.sendPackets(new S_ChatPacket(gm, "。忘れられた島開始します。忘れられた島終了ダンジョン初期化"));
        gm.sendPackets(new S_ChatPacket(gm, "経験値ポーション支給（ワールド全体）"));
        gm.sendPackets(new S_ChatPacket(gm, "\\aG----------●[Gm Commands End]●-------------"));
    }

    private static Map<Integer, String> _lastCommands = new HashMap<Integer, String>();

    private void searchDatabase(L1PcInstance gm, String param) { // 検索機能を追加

        try {
            StringTokenizer tok = new StringTokenizer(param);
            int type = Integer.parseInt(tok.nextToken());
            String name = tok.nextToken();
            searchObject(gm, type, "%" + name + "%");

        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage("。検索[0〜5] [名前]を入力してください。"), true);
            gm.sendPackets(new S_SystemMessage("0 =ザブテム、1 =武器、2 =鎧、3 = npc、4 =スキル、5 =ドロップ"), true);
        }
    }

    private void mobKillCount(L1PcInstance gm) {
        int cnt = 0;
        for (L1Object obj : L1World.getInstance().getObject()) {
            if (obj instanceof L1MonsterInstance) {
                L1MonsterInstance mon = (L1MonsterInstance) obj;
                mon.die(gm);
                cnt++;
            }

        }
        gm.sendPackets(new S_SystemMessage("モンスター" + cnt + "頭を殺した。"), true);
    }

    private void autoloot(L1PcInstance gm, String param) {
        try {
            StringTokenizer tok = new StringTokenizer(param);
            String type = tok.nextToken();
            if (type.equalsIgnoreCase("reload")) {
                AutoLoot.getInstance().reload();
                gm.sendPackets(new S_SystemMessage("オートルーティングの設定がリロードされました。"));
            } else if (type.equalsIgnoreCase("search")) {
                java.sql.Connection con = null;
                PreparedStatement pstm = null;
                ResultSet rs = null;

                String nameid = tok.nextToken();
                try {
                    con = L1DatabaseFactory.getInstance().getConnection();
                    String strQry;
                    strQry = " Select e.item_id, e.name from etcitem e, autoloot l where l.item_id = e.item_id and name Like '%"
                            + nameid + "%' ";
                    strQry += " union all "
                            + " Select w.item_id, w.name from weapon w, autoloot l where l.item_id = w.item_id and name Like '%"
                            + nameid + "%' ";
                    strQry += " union all "
                            + " Select a.item_id, a.name from armor a, autoloot l where l.item_id = a.item_id and name Like '%"
                            + nameid + "%' ";
                    pstm = con.prepareStatement(strQry);
                    rs = pstm.executeQuery();
                    while (rs.next()) {
                        gm.sendPackets(
                                new S_SystemMessage("[" + rs.getString("item_id") + "] " + rs.getString("name")));
                    }
                } catch (Exception e) {
                } finally {
                    rs.close();
                    pstm.close();
                    con.close();
                }
            } else {
                String nameid = tok.nextToken();
                int itemid = 0;
                try {
                    itemid = Integer.parseInt(nameid);
                } catch (NumberFormatException e) {
                    itemid = ItemTable.getInstance().findItemIdByNameWithoutSpace(nameid);
                    if (itemid == 0) {
                        gm.sendPackets(new S_SystemMessage("該当のアイテムが見つかりません。"));
                        return;
                    }
                }

                L1Item temp = ItemTable.getInstance().getTemplate(itemid);
                if (temp == null) {
                    gm.sendPackets(new S_SystemMessage("該当のアイテムが見つかりません。"));
                    return;
                }
                if (type.equalsIgnoreCase("add")) {
                    if (AutoLoot.getInstance().isAutoLoot(itemid)) {
                        gm.sendPackets(new S_SystemMessage("すでにオートルーティングリストにあります。"));
                        return;
                    }
                    AutoLoot.getInstance().storeId(itemid);
                    gm.sendPackets(new S_SystemMessage("オートルーティングエントリに追加しました。"));
                } else if (type.equalsIgnoreCase("delete")) {
                    if (!AutoLoot.getInstance().isAutoLoot(itemid)) {
                        gm.sendPackets(new S_SystemMessage("オートルーティングエントリに対応するアイテムがありません。"));
                        return;
                    }
                    gm.sendPackets(new S_SystemMessage("オートルーティングエントリから削除されました。"));
                    AutoLoot.getInstance().deleteId(itemid);
                }
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage("オートルーティングリロード"));
            gm.sendPackets(new S_SystemMessage("オートルーティング追加|削除itemid | name"));
            gm.sendPackets(new S_SystemMessage("オートルーティング検索name"));
        }
    }

    private void allRanking(L1PcInstance pc) {
        try {
            long curtime = System.currentTimeMillis() / 1000;
            if (pc.getQuizTime2() + 5 > curtime) {
                long time = (pc.getQuizTime2() + 5) - curtime;
                pc.sendPackets(new S_ChatPacket(pc, time + "秒後に使用することができます。"));
                return;
            }
            pc.sendPackets(new S_UserCommands4(pc, 1));
            pc.setQuizTime2(curtime);
        } catch (Exception e) {
        }
    }

    private void spEvent(L1PcInstance gm, String param) {
        if (param.equalsIgnoreCase("start")) {
            Config.EXP = 2;// 起動すると、自動的に2にマンドゥルム
            L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
                    "\\aA■ しばらくして「ギラン監獄」で \\aG[経験値/羽]\\aA 2倍イベントが開始されます。 ■"));
        } else if (param.equalsIgnoreCase("end")) {
            Config.EXP = 0;// 終了すると、自動的に0にマンドゥルム
            // Config.load();//コンフィグで再びゼロにマンドゥルム（コンフィグで0に設定されている必要があります）
            L1World.getInstance().broadcastPacketToAll(
                    new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\aA■ しばらくして「ギラン監獄」で \\aG[経験値/羽]\\aA 2倍イベントが終了します。 ■"));
        } else {
            gm.sendPackets(new S_SystemMessage("特定のイベントは、[スタートor終了]を入力してください"));
        }
    }

    private void cameBackArnoldEvent(L1PcInstance gm, String param) {
        if (param.equalsIgnoreCase("start")) {
            if (Config.ARNOLD_EVENTS == false) {
                ArnoldBackEvent.getInstance().isGmOpenArnold = true;
                ArnoldBackEvent.getInstance().start();
            } else {
                gm.sendPackets(new S_SystemMessage("現在アーノルドイベントが進行中です。"));
            }
        } else if (param.equalsIgnoreCase("end")) {
            if (Config.ARNOLD_EVENTS == true) {
                AdenaHuntController.getInstance().setAdenaHuntStart(false);
                AdenaHuntController.getInstance().isGmOpen4 = false;
                ArnoldBackEvent.getInstance().End();
                gm.sendPackets(new S_SystemMessage("アーノルドイベントが強制終了しました。"));
            } else {
                gm.sendPackets(new S_SystemMessage("アーノルドイベント進行中でありません。"));
            }
        } else {
            gm.sendPackets(new S_SystemMessage("。アーノルド[スタートor終了]を入力してください"));
        }
    }

    private void CloseDevilArea(L1PcInstance gm, String param) {
        if (param.equalsIgnoreCase("on")) {
            DevilController.getInstance().isGmOpen = true;
            L1World.getInstance()
                    .broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "運営者様が悪魔王の領土をオープンします。"));
            gm.sendPackets(new S_SystemMessage("悪魔王の領土強制します。"));
        } else if (param.equalsIgnoreCase("off")) {
            DevilController.getInstance().setDevilStart(false);
            DevilController.getInstance().isGmOpen = false;
            L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "運営者様が悪魔王の領土を閉じます。"));
            gm.sendPackets(new S_SystemMessage("悪魔王の領土強制終了します。"));
            TelePort();
        } else {
            gm.sendPackets(new S_SystemMessage("悪魔王[入or切]入力してください"));
        }
    }

    private void CloseAdenHuntArea(L1PcInstance gm, String param) {
        if (param.equalsIgnoreCase("on")) {
            AdenaHuntController.getInstance().isGmOpen4 = true;
            L1World.getInstance()
                    .broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "運営者様がアデン狩り場をオープンします。"));
            gm.sendPackets(new S_SystemMessage("アデン狩り場を強制的に実行します。"));
        } else if (param.equalsIgnoreCase("off")) {
            AdenaHuntController.getInstance().setAdenaHuntStart(false);
            AdenaHuntController.getInstance().isGmOpen4 = false;
            L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "運営者様がアデン狩り場を閉じます。"));
            gm.sendPackets(new S_SystemMessage("アデン狩り場強制終了します。"));
            TelePort();
        } else {
            gm.sendPackets(new S_SystemMessage("アデン狩り場[入or切]入力してください"));
        }
    }

    private void serversave(L1PcInstance pc) {
        Saveserver();// サーバーセーブメソッド宣言
        pc.sendPackets(new S_SystemMessage("サーバーの保存が完了しました。"));
    }

    /**
     * サーバー保存*
     */
    private void Saveserver() {
        /** 全体プレーヤーを呼び出す* */
        Collection<L1PcInstance> list = null;
        list = L1World.getInstance().getAllPlayers();
        for (L1PcInstance player : list) {
            if (player == null)
                continue;
            try {
                /** PC保存してくれ* */
                player.save();
                /** インベントリも保存して* */
                player.saveInventory();

            } catch (Exception ex) {
                /** 例外インベントリ保存* */
                player.saveInventory();
                System.out.println("ストア命令エラー（インベントリのみビン）：" + ex);
            }
        }
    }

    private void privateShop(L1PcInstance pc) {
        try {
            if (!pc.isPrivateShop()) {
                pc.sendPackets(new S_ChatPacket(pc, "個人商店の状態での使用が可能です。"));
                return;
            }
            LinAllManager.getInstance().LogLogOutAppend(pc.getName(), pc.getNetConnection().getHostname());
            GameClient client = pc.getNetConnection();
            pc.setNetConnection(null);
            try {
                pc.save();
                pc.saveInventory();
            } catch (Exception e) {
            }
            client.setActiveChar(null);
            client.setLoginAvailable();
            client.CharReStart(true);
            client.sendPacket(new S_Unknown2(1)); // リースボタンのための構造を変更する// Episode
            // U

        } catch (Exception e) {
        }
    }

    private void GiveHouse(L1PcInstance pc, String poby) {
        try {
            StringTokenizer st = new StringTokenizer(poby);
            String pobyname = st.nextToken();
            int pobyhouseid = Integer.parseInt(st.nextToken());
            L1PcInstance target = L1World.getInstance().getPlayer(pobyname);
            if (target != null) {
                if (target.getClanid() != 0) {
                    L1Clan TargetClan = L1World.getInstance().getClan(target.getClanname());
                    L1House pobyhouse = HouseTable.getInstance().getHouseTable(pobyhouseid);
                    TargetClan.setHouseId(pobyhouseid);
                    ClanTable.getInstance().updateClan(TargetClan);
                    pc.sendPackets(new S_SystemMessage(
                            target.getClanname() + " 血盟に " + pobyhouse.getHouseName() + "一度に支給しました。"));
                    for (L1PcInstance tc : TargetClan.getOnlineClanMember()) {
                        tc.sendPackets(new S_SystemMessage("ゲームマスターから" + pobyhouse.getHouseName() + "一度に支給しました。"));
                    }
                } else {
                    pc.sendPackets(new S_SystemMessage(target.getName() + "様は、血盟に所属していません。"));
                }
            } else {
                pc.sendPackets(new S_ServerMessage(73, pobyname));
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage("アジト支給<支給する血盟員> <アジト番号>"));
        }
    }

    private void Mark1(L1PcInstance gm, String param) {
        long curtime = System.currentTimeMillis() / 1000;
        if (gm.getQuizTime() + 5 > curtime) {
            long time = (gm.getQuizTime() + 5) - curtime;
            gm.sendPackets(new S_ChatPacket(gm, time + "秒後に使用することができます。"));
            return;
        }
        if (gm.isDead()) {
            gm.sendPackets(new S_SystemMessage("死んだ状態で使用することができません。"));
            return;
        }
        int i = 1;
        if (gm.watchCrest) {
            i = 3;
            gm.watchCrest = false;
        } else
            gm.watchCrest = true;
        for (L1Clan clan : L1World.getInstance().getAllClans()) {
            if (clan != null) {
                gm.sendPackets(new S_War(i, gm.getClanname(), clan.getClanName()));
            }
        }
        gm.setQuizTime(curtime);

    }

    private void castleWarStart(L1PcInstance gm, String param) {
        try {
            StringTokenizer tok = new StringTokenizer(param);
            String name = tok.nextToken();
            int minute = Integer.parseInt(tok.nextToken());

            Calendar cal = (Calendar) Calendar.getInstance().clone();
            if (minute != 0)
                cal.add(Calendar.MINUTE, minute);

            CastleTable.getInstance().updateWarTime(name, cal);
            WarTimeController.getInstance().setWarStartTime(name, cal);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            gm.sendPackets(new S_SystemMessage(String.format("。攻城時間が％sに変更しました。", formatter.format(cal.getTime()))),
                    true);
            gm.sendPackets(new S_SystemMessage(param + "分後攻城が開始します。"));
            formatter = null;
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage("。攻城開始[姓名の二文字] [分]"));
        }
    }

    private void Clear(L1PcInstance gm) {
        for (L1Object obj : L1World.getInstance().getVisibleObjects(gm, 15)) {
            if (obj instanceof L1MonsterInstance) { // モンスターなら
                L1MonsterInstance npc = (L1MonsterInstance) obj;
                npc.receiveDamage(gm, 50000); // ダメージ
                gm.sendPackets(new S_SkillSound(obj.getId(), 1815), true);
                Broadcaster.broadcastPacket(gm, new S_SkillSound(obj.getId(), 1815), true);
            } else if (obj instanceof L1PcInstance) { // pcなら
                L1PcInstance player = (L1PcInstance) obj;
                player.receiveDamage(player, 0); // ダメージ
                gm.sendPackets(new S_SkillSound(obj.getId(), 1815), true);
                Broadcaster.broadcastPacket(gm, new S_SkillSound(obj.getId(), 1815), true);
            }
        }
    }

    private void castleWarExit(L1PcInstance gm, String param) {
        try {
            StringTokenizer tok = new StringTokenizer(param);
            String name = tok.nextToken();
            WarTimeController.getInstance().setWarExitTime(gm, name);
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage("。攻城終了[姓名の二文字]"));
        }
    }

    private void party(L1PcInstance gm, String cmdName) {
        try {
            StringTokenizer tok = new StringTokenizer(cmdName);
            String cmd = tok.nextToken();
            if (cmd.equals("around")) {
                L1Party party = new L1Party();
                if (gm.getParty() == null) {
                    party.addMember(gm);
                } else {
                    party = gm.getParty();
                }
                int range = 3;// 現在の周辺3間
                for (L1PcInstance Targetpc : L1World.getInstance().getVisiblePlayer(gm, range)) {
                    if (gm.getName().equals(Targetpc.getName())) {
                        continue;
                    }
                    if (Targetpc.getParty() != null) {
                        continue;
                    } // パーティーのユーザを除く
                    if (Targetpc.isPrivateShop() || Targetpc.isAutoClanjoin()) {
                        continue;
                    } // 無人除く
                    party.addMember(Targetpc);
                    gm.sendPackets(new S_SystemMessage(Targetpc.getName() + "様を私のパーティーに参加しました。"));
                }
                gm.sendPackets(new S_SystemMessage(range + "カーンの中のユーザを私のパーティーに参加しました。"));
            } else if (cmd.equals("screen")) {
                L1Party party = new L1Party();
                if (gm.getParty() == null) {
                    party.addMember(gm);
                } else {
                    party = gm.getParty();
                }
                for (L1PcInstance Targetpc : L1World.getInstance().getVisiblePlayer(gm)) {
                    if (gm.getName().equals(Targetpc.getName())) {
                        continue;
                    }
                    if (Targetpc.getParty() != null) {
                        continue;
                    }
                    if (Targetpc.isPrivateShop() || Targetpc.isAutoClanjoin()) {
                        continue;
                    }
                    party.addMember(Targetpc);
                    gm.sendPackets(new S_SystemMessage(Targetpc.getName() + "様を私のパーティーに参加しました。"));
                }
                gm.sendPackets(new S_SystemMessage("画面の中のユーザを私のパーティーに参加しました。"));
            } else if (cmd.equals("all")) {
                L1Party party = new L1Party();
                if (gm.getParty() == null) {
                    party.addMember(gm);
                } else {
                    party = gm.getParty();
                }
                int range = 3;// 現在の周辺3間
                for (L1PcInstance Targetpc : L1World.getInstance().getAllPlayers()) {
                    if (gm.getName().equals(Targetpc.getName())) {
                        continue;
                    }
                    if (Targetpc.getParty() != null) {
                        continue;
                    }
                    if (Targetpc.isPrivateShop() || Targetpc.isAutoClanjoin()) {
                        continue;
                    }
                    party.addMember(Targetpc);
                    gm.sendPackets(new S_SystemMessage(Targetpc.getName() + "様を私のパーティーに参加しました。"));
                }
                gm.sendPackets(new S_SystemMessage(range + "カーンの中のユーザを私のパーティーに参加しました。"));
            } else if (cmd.equals("attend")) {
                String TargetpcName = tok.nextToken();
                L1PcInstance TargetPc = L1World.getInstance().getPlayer(TargetpcName);
                if (TargetPc.getParty() != null) {
                    gm.sendPackets(new S_SystemMessage(TargetPc.getName() + "様は、パーティーがありません。"));
                } else {
                    TargetPc.getParty().addMember(gm);
                    gm.sendPackets(new S_SystemMessage(TargetPc.getName() + "さんのパーティーに参加しました。"));
                }
            } else if (cmd.equals("invite")) {
                String TargetpcName = tok.nextToken();
                L1PcInstance TargetPc = L1World.getInstance().getPlayer(TargetpcName);
                L1Party party = new L1Party();
                if (gm.getParty() == null) {
                    party.addMember(gm);
                } else {
                    party = gm.getParty();
                }
                if (TargetPc.getParty() != null) {
                    TargetPc.getParty().kickMember(TargetPc);
                }
                party.addMember(TargetPc);
                gm.sendPackets(new S_SystemMessage(TargetPc.getName() + "様を私のパーティーに強制参加させました。"));
            } else if (cmd.equals("forceinvite")) {
                L1Party party = new L1Party();
                if (gm.getParty() == null) {
                    party.addMember(gm);
                } else {
                    party = gm.getParty();
                }
                int range = 3;// 現在の周辺3間
                for (L1PcInstance Targetpc : L1World.getInstance().getAllPlayers()) {
                    if (gm.getName().equals(Targetpc.getName())) {
                        continue;
                    }
                    if (Targetpc.isPrivateShop() || Targetpc.isAutoClanjoin()) {
                        continue;
                    }
                    if (Targetpc.getParty() != null) {
                        Targetpc.getParty().kickMember(Targetpc);
                    }
                    party.addMember(Targetpc);
                    gm.sendPackets(new S_SystemMessage(Targetpc.getName() + "様を私のパーティーに参加しました。"));
                }
                gm.sendPackets(new S_SystemMessage("接続中のユーザを私のパーティーに強制参加させました。"));
            } else if (cmd.equals("pass")) {
                if (gm.getParty() == null) {
                    gm.sendPackets(new S_SystemMessage("参加しているパーティーがありません。"));
                } else {
                    gm.getParty().passLeader(gm);
                    gm.sendPackets(new S_SystemMessage("波長をペトオトた。"));
                }
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage("。パーティー[周辺、画面、全体の、参加（ユーザ名）]"));
            gm.sendPackets(new S_SystemMessage("。パーティー[招待（ユーザ名）、強制招待し、波長]"));
        }
    }

    private void effect(L1PcInstance pc, String param) {
        try {
            StringTokenizer stringtokenizer = new StringTokenizer(param);
            int sprid = Integer.parseInt(stringtokenizer.nextToken());
            pc.sendPackets(new S_SkillSound(pc.getId(), sprid));
            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), sprid));
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage("。エフェクト[数字]と入力してください。"));
        }
    }

    private int minValue(int itemid) {
        try {
            Connection con = null;
            PreparedStatement pstm = null;
            ResultSet rs = null;
            try {
                con = L1DatabaseFactory.getInstance().getConnection();
                pstm = con.prepareStatement(
                        "SELECT * FROM shop WHERE item_id = ? AND selling_price NOT IN (-1) ORDER BY selling_price ASC limit 1");
                pstm.setInt(1, itemid);
                rs = pstm.executeQuery();
                if (rs.next()) {
                    int temp = 0;
                    if (rs.getInt("pack_count") > 1)
                        temp = rs.getInt("selling_price") / rs.getInt("pack_count");
                    else {
                        temp = rs.getInt("selling_price");
                    }
                    int i = temp;
                    return i;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                SQLUtil.close(rs, pstm, con);
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int maxValue(int itemid) {
        try {
            Connection con = null;
            PreparedStatement pstm = null;
            ResultSet rs = null;
            try {
                con = L1DatabaseFactory.getInstance().getConnection();
                pstm = con.prepareStatement(
                        "SELECT purchasing_price FROM shop WHERE item_id = ? ORDER BY purchasing_price DESC limit 1");
                pstm.setInt(1, itemid);
                rs = pstm.executeQuery();
                if (rs.next()) {
                    int i = rs.getInt("purchasing_price");
                    return i;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                SQLUtil.close(rs, pstm, con);
            }
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void changePort(L1PcInstance gm, String param) {
        // TODO 自動生成されたメソッド・スタブ
        try {
            gm.sendPackets(new S_SystemMessage("ポートの変更を失敗しました。"));
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage("ポートの変更[Port]"));
        }
    }

    private void checkShop(L1PcInstance gm) {
        try {
            ArrayList<Integer> itemids = new ArrayList<Integer>();
            Connection con = null;
            PreparedStatement pstm = null;
            ResultSet rs = null;
            @SuppressWarnings("unused")
            int cnt;
            Iterator<Integer> i$;
            try {
                con = L1DatabaseFactory.getInstance().getConnection();
                pstm = con.prepareStatement("SELECT item_id FROM shop");
                rs = pstm.executeQuery();
                while (rs.next()) {
                    if (!itemids.contains(Integer.valueOf(rs.getInt("item_id")))) {
                        itemids.add(Integer.valueOf(rs.getInt("item_id")));
                    }
                }
                cnt = 0;
                for (i$ = itemids.iterator(); i$.hasNext(); ) {
                    int itemid = ((Integer) i$.next()).intValue();
                    int minSellPrice = minValue(itemid);
                    int maxSellPrice = maxValue(itemid);
                    if ((minSellPrice != 0) && (minSellPrice < maxSellPrice)) {
                        gm.sendPackets(new S_ChatPacket(gm,
                                "検出さ！ [システム" + itemid + " : [購入値 " + minSellPrice + "] [買取値" + maxSellPrice + "]"));
                    }
                    cnt++;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                SQLUtil.close(rs, pstm, con);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void standBy(L1PcInstance gm, String param) {
        try {
            StringTokenizer st = new StringTokenizer(param);
            String status = st.nextToken();
            if (status.equalsIgnoreCase("on")) {
                if (Config.STANDBY_SERVER) {
                    gm.sendPackets(new S_SystemMessage("すでに待機状態に突入しました。"));
                    return;
                }
                Config.STANDBY_SERVER = true;
                Config.RATE_XP = 0;// オープン待機時の経験値を強制0作成の
                L1World.getInstance().broadcastPacketToAll(
                        new S_ChatPacket("[システム]:\\aAサーバーがオープン大気に突入します。一部のパケットがブロックされた。", Opcodes.S_MESSAGE));
            } else if (status.equalsIgnoreCase("off")) {
                if (!Config.STANDBY_SERVER) {
                    gm.sendPackets(new S_SystemMessage("待機状態ではない。"));
                    return;
                }
                Config.load();// 既存のコンフィグ経験値リロードさせて捨て
                Config.STANDBY_SERVER = false;
                L1World.getInstance()
                        .broadcastPacketToAll(new S_ChatPacket("[システム]:\\aA待機状態が解除され、通常のプレイが可能です。", Opcodes.S_MESSAGE));
            }
        } catch (Exception eee) {
            gm.sendPackets(new S_SystemMessage("。オープン待ち[入/切]に入力してください。"));
            gm.sendPackets(new S_SystemMessage("オン - オープン待ち状態に移行|オフ - 通常モードでゲームを開始"));
        }
    }

    private void cleanupMemory(L1PcInstance gm) {
        gm.sendPackets(new S_SystemMessage("\\aG警告：数分以内に、メモリを初期化します"));
        System.out.println("強制的にガベージ処理を続行します。");
        try {
            System.gc();
        } catch (Exception e) {
        }
        System.out.println("メモリクリーンアップが完了しました。");
        gm.sendPackets(new S_SystemMessage("\\aG通知：メモリクリーンアップが完了しました。"));
    }

    private void mem_free(L1PcInstance gm) {
        try {
            java.lang.System.gc();
            gm.sendPackets(new S_SystemMessage("gc使用後のメモリ情報"));
            long long_total = Runtime.getRuntime().totalMemory();
            int int_total = Math.round(long_total / 1000000);
            long long_free = Runtime.getRuntime().freeMemory();
            int int_free = Math.round(long_free / 1000000);
            long long_max = Runtime.getRuntime().maxMemory();
            int int_max = Math.round(long_max / 1000000);
            gm.sendPackets(new S_SystemMessage("使用したメモリ：" + int_total + "MB"));
            gm.sendPackets(new S_SystemMessage("残りのメモリ：" + int_free + "MB"));
            gm.sendPackets(new S_SystemMessage("最大使用可能メモリ：" + int_max + "MB"));
        } catch (Exception e) {
        }
    }

    private void hold(L1PcInstance gm, String pcName) {
        try {
            L1PcInstance target = L1World.getInstance().getPlayer(pcName);
            if (target != null) {
                holdnow(gm, target);
            } else {
                gm.sendPackets(new S_SystemMessage("そんなキャラクターはありません。"));
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage("。刑務所キャラクター名で入力してください。"));
        }
    }

    private void holdnow(L1PcInstance gm, L1PcInstance target) {
        try {
            // L1Teleport.teleport(target, 32736, 32799, (short) 34, 5, true);
            new L1Teleport().teleport(target, 32835, 32782, (short) 701, 5, true);
            gm.sendPackets(new S_SystemMessage(
                    (new StringBuilder()).append(target.getName()).append("様刑務所に移動される。").toString()));
            target.sendPackets(new S_SystemMessage("刑務所に監禁された。"));
        } catch (Exception e) {
            _log.log(Level.SEVERE, "", e);
        }
    }

    private void nocall(L1PcInstance gm, String param) {
        try {
            StringTokenizer tokenizer = new StringTokenizer(param);
            String pcName = tokenizer.nextToken();
            L1PcInstance target = null; // q
            target = L1World.getInstance().getPlayer(pcName);
            if (target != null) { // ターゲット
                new L1Teleport().teleport(target, 33437, 32812, (short) 4, 5, true);
            } else {
                gm.sendPackets(new S_SystemMessage("接続中でないユーザIDです。"), true);
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage("。ガラス（送信キャラクタ名）で入力してください。"), true);
        }
    }

    private void allBuff(L1PcInstance gm, String param) {
        try {
            StringTokenizer st = new StringTokenizer(param);
            String status = st.nextToken();
            if (status.equalsIgnoreCase("1")) { // doAllBuf
                SpecialEventHandler.getInstance().buff_ALLFull();
            } else if (status.equalsIgnoreCase("2")) {
                SpecialEventHandler.getInstance().buff_ALLMetis();
            } else if (status.equalsIgnoreCase("3")) {
                SpecialEventHandler.getInstance().buff_ALLEvilEye();
            } else if (status.equalsIgnoreCase("4")) {
                SpecialEventHandler.getInstance().buff_ALLGod();
            } else if (status.equalsIgnoreCase("5")) {
                SpecialEventHandler.getInstance().buff_ALLCOMA();
            }
        } catch (Exception e) {
            gm.sendPackets(new S_ChatPacket(gm, "----------------------------------------------------"));
            gm.sendPackets(new S_ChatPacket(gm, "1：プルアップ2：祝福3：センマ4：黒砂の5：コマ"));
            gm.sendPackets(new S_ChatPacket(gm, "----------------------------------------------------"));
        }
    }

    private void screenBuff(L1PcInstance pc) {
        pc.sendPackets(new S_ChatPacket(pc, "---------------------------------------------------"));
        pc.sendPackets(new S_ChatPacket(pc, "画面1〜画面4（1：プルアップ2：祝福3：センマ4：黒砂の5：コマ）"));
        pc.sendPackets(new S_ChatPacket(pc, "---------------------------------------------------"));
    }

    private void searchObject(L1PcInstance gm, int type, String name) {
        try {
            String str1 = null;
            String str2 = null;
            String str3 = null;
            int str4 = 0;
            int count = 0;
            java.sql.Connection con = null;
            con = L1DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = null;
            ResultSet rs = null;
            try {
                switch (type) {
                    case 0: // etcitem
                        statement = con
                                .prepareStatement("select item_id, name from etcitem where name Like '" + name + "'");
                        break;
                    case 1: // weapon
                        statement = con.prepareStatement("select item_id, name from weapon where name Like '" + name + "'");
                        break;
                    case 2: // armor
                        statement = con.prepareStatement("select item_id, name from armor where name Like '" + name + "'");
                        break;
                    case 3: // npc
                        statement = con.prepareStatement("select npcid, name from npc where name Like '" + name + "'");
                        break;
                    case 4: // skill
                        statement = con
                                .prepareStatement("select skill_id, name from skills where name Like '" + name + "'");
                        break;
                    case 5: // polymorphs
                        statement = con.prepareStatement(
                                "select mobid, mobname, itemname, chance from droplist where itemname Like '" + name + "'");
                        break;
                    default:
                        break;
                }
                rs = statement.executeQuery();
                while (rs.next()) {
                    if (type < 5) {
                        str1 = rs.getString(1);
                        str2 = rs.getString(2);
                        gm.sendPackets(new S_SystemMessage("id : [" + str1 + "], name : [" + str2 + "]"), true);
                    } else {
                        str1 = rs.getString(1);
                        str2 = rs.getString(2);
                        str3 = rs.getString(3);
                        str4 = rs.getInt(4);
                        double rate = str4 * Config.RATE_DROP_ITEMS / 10000;
                        // gm.sendPackets(new S_SystemMessage("id : [" + str1 +
                        // "], name : [" + str2 + "]"), true);
                        gm.sendPackets(
                                new S_SystemMessage("[" + str1 + "], [" + str3 + "],[" + str2 + "], [" + rate + "]%"),
                                true);
                    }
            /*
             * str1 = rs.getString(1); str2 = rs.getString(2);
		     * //gm.sendPackets(new S_SystemMessage("id : [" + str1 +
		     * "], name : [" + str2 + "]"), true); gm.sendPackets(new
		     * S_SystemMessage("[" + str1 + "], [" + str2 + "]"), true);
		     */
                    count++;
                }

                gm.sendPackets(new S_SystemMessage("総[" + count + "]個のデータが見つかりました。"), true);
            } catch (Exception e) {

            } finally {
                SQLUtil.close(rs);
                SQLUtil.close(statement);
                SQLUtil.close(con);
            }
        } catch (Exception e) {
        }
    }

    private void redo(L1PcInstance pc, String arg) {
        try {
            String lastCmd = _lastCommands.get(pc.getId());
            if (arg.isEmpty()) {
                pc.sendPackets(new S_SystemMessage("コマンド " + lastCmd + "を再実行します。"));
                handleCommands(pc, lastCmd);
            } else {
                StringTokenizer token = new StringTokenizer(lastCmd);
                String cmd = token.nextToken() + " " + arg;
                pc.sendPackets(new S_SystemMessage("コマンド" + cmd + "を再実行します。"));
                handleCommands(pc, cmd);
            }
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            pc.sendPackets(new S_SystemMessage("。再実行コマンドエラー"));
        }
    }

    private void unprison(L1PcInstance pc, String param) {
        try {
            L1PcInstance target = L1World.getInstance().getPlayer(param);
            if (target != null) {
                unprisonnow(pc, target);
            } else {
                pc.sendPackets(new S_SystemMessage("村キャラクター名"));
                pc.sendPackets(new S_SystemMessage("そのような名前のキャラクターはありません。"));
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage("村キャラクター名"));
        }
    }

    private void unprisonnow(L1PcInstance gm, L1PcInstance target) {
        try {
            int i = 33437;
            int j = 32803;
            short k = 4;
            new L1Teleport().teleport(target, i, j, k, 5, false);
            gm.sendPackets(new S_SystemMessage(
                    (new StringBuilder()).append(target.getName()).append("様を村に移動します。").toString()));
        } catch (Exception e) {
            _log.log(Level.SEVERE, "", e);
        }
    }

    private void unprison2(L1PcInstance pc, String param) {
        try {
            L1PcInstance target = L1World.getInstance().getPlayer(param);
            if (target != null) {
                unprisonnow2(pc, target);
            } else {
                pc.sendPackets(new S_SystemMessage("スムギェキャラクター名"));
                pc.sendPackets(new S_SystemMessage("そのような名前のキャラクターはありません。"));
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage("。スムギェキャラクター名"));
        }
    }

    private void unprisonnow2(L1PcInstance gm, L1PcInstance target) {
        try {
            int i = 32681;
            int j = 32853;
            short k = 2005;
            new L1Teleport().teleport(target, i, j, k, 5, false);
            gm.sendPackets(new S_SystemMessage(
                    (new StringBuilder()).append(target.getName()).append("様を隠し系移動します。").toString()));
        } catch (Exception e) {
            _log.log(Level.SEVERE, "", e);
        }
    }

    private void chatx(L1PcInstance gm, String param) {
        try {
            StringTokenizer tokenizer = new StringTokenizer(param);
            String pcName = tokenizer.nextToken();
            L1PcInstance target = null;
            target = L1World.getInstance().getPlayer(pcName);
            if (target != null) {
                target.killSkillEffectTimer(L1SkillId.STATUS_CHAT_PROHIBITED);
                target.sendPackets(new S_SkillIconGFX(36, 0));
                target.sendPackets(new S_ServerMessage(288));
                gm.sendPackets(new S_SystemMessage("そのキャラクターの金鉱を解除しました。"));
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage("。金鉱解くキャラクター名と入力してください。"));
        }
    }

    private void tell(L1PcInstance gm) {
        try {
            new L1Teleport().teleport(gm, gm.getX(), gm.getY(), gm.getMapId(), gm.getHeading(), false);
        } catch (Exception e) {
        }
    }

    public void levelup2(L1PcInstance gm, String arg) {
        try {
            StringTokenizer tok = new StringTokenizer(arg);
            String user = tok.nextToken();
            L1PcInstance target = L1World.getInstance().getPlayer(user);
            int level = Integer.parseInt(tok.nextToken());
            if (level == target.getLevel()) {
                return;
            }
            if (!IntRange.includes(level, 1, 99)) {
                gm.sendPackets(new S_SystemMessage("1-99の範囲で指定してください"));
                return;
            }
            target.setExp(ExpTable.getExpByLevel(level));
            gm.sendPackets(new S_SystemMessage(target.getName() + "さんのレベルが変更されまし！ 。剣[キャラクター名]で確認要望"));
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage("。レプジャク[キャラクター名] [レベル]入力"));
        }
    }

    private void TamCycle(L1PcInstance gm, String param) {
        // TODO自動生成されたメソッド・スタブ
        try {
            StringTokenizer st = new StringTokenizer(param);
            String name = st.nextToken();
            int id = Integer.parseInt(st.nextToken());
            L1PcInstance user = L1World.getInstance().getPlayer(name);
            if (user != null) {
                user.getNetConnection().getAccount().tam_point += id;
                user.getNetConnection().getAccount().updateTam();
                try {
                    user.sendPackets(new S_NewCreateItem(S_NewCreateItem.TAM_POINT, user.getNetConnection()), true);
                } catch (Exception e) {
                }
                gm.sendPackets(new S_SystemMessage(user.getName() + "に乗車" + id + "本をくれました。"), true);
            } else
                gm.sendPackets(new S_SystemMessage("存在しないユーザです。"), true);
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage("。乗車支給名本数"));
        }
    }

    private void NCoinCycle(L1PcInstance gm, String param) {
        // TODO 自動生成されたメソッド・スタブ
        try {
            StringTokenizer st = new StringTokenizer(param);
            String name = st.nextToken();
            int id = Integer.parseInt(st.nextToken());
            L1PcInstance user = L1World.getInstance().getPlayer(name);
            if (user != null) {
                user.getNetConnection().getAccount().Ncoin_point += id;
                user.getNetConnection().getAccount().updateNcoin();
                gm.sendPackets(new S_SystemMessage(user.getName() + "にエンコイン" + id + "本をくれました。"), true);
            } else
                gm.sendPackets(new S_SystemMessage("存在しないユーザです。"), true);
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage("。エンコイン名本数"));
        }
    }

    private void initNCoin(L1PcInstance gm, String param) {
        // TODO 自動生成されたメソッド・スタブ
        try {
            StringTokenizer st = new StringTokenizer(param);
            String name = st.nextToken();
            int id = 0;
            L1PcInstance user = L1World.getInstance().getPlayer(name);
            if (user != null) {
                user.getNetConnection().getAccount().Ncoin_point = id;
                user.getNetConnection().getAccount().updateNcoin();
                gm.sendPackets(new S_SystemMessage(user.getName() + "様Nコインを初期化しました。"));
            } else
                gm.sendPackets(new S_SystemMessage("存在しないユーザです。"), true);
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage("。エンコイン初期化名"));
        }
    }

    private void allrecall(L1PcInstance gm) {
        try {
            for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                if (!pc.isGm() && !pc.isPrivateShop() && !pc.isAutoClanjoin() && !pc.noPlayerCK) {
                    recallnow(gm, pc);
                }
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage("全体召喚コマンドエラー"));
        }
    }

    private void summonBot(L1PcInstance gm, String param) {
        try {
            StringTokenizer st = new StringTokenizer(param);
            String name = st.nextToken();

            L1PcInstance target = L1World.getInstance().getPlayer(name);
            new L1Teleport().teleport(target, gm.getX(), gm.getY(), gm.getMapId(), target.getHeading(), false);
            gm.sendPackets(new S_SystemMessage("ゲームマスターに召喚されました。"));
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage("。ボット召喚キャラ名"));
        }
    }

    private void recallnow(L1PcInstance gm, L1PcInstance target) {
        try {
            L1Teleport.teleportToTargetFront(target, gm, 2);
        } catch (Exception e) {
            _log.log(Level.SEVERE, "", e);
        }
    }

    private void ShopKick(L1PcInstance gm, String param) {
        try {
            L1PcInstance target = L1World.getInstance().getPlayer(param);
            if (target != null) {
                gm.sendPackets(new S_SystemMessage(
                        (new StringBuilder()).append(target.getName()).append("様を追放しました。").toString()));
                GameServer.disconnectChar(target);
            } else {
                gm.sendPackets(new S_SystemMessage("そのような名前のキャラクターは、ワールド内には存在しません。"));
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage("。店追放キャラクター名"));
        }
    }

    private void icon(L1PcInstance pc, String param) {
        try {
            StringTokenizer st = new StringTokenizer(param);
            int iconId = Integer.parseInt(st.nextToken(), 10);
            pc.sendPackets(new Chocco(3));
            pc.sendPackets(new Chocco(2));
            pc.sendPackets(new Chocco(4));
            pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_LIST, iconId));
        } catch (Exception exception) {
            pc.sendPackets(new S_SystemMessage("アイコン[actid]を入力してください。"));
        }
    }

    public void icon1(L1PcInstance pc, String param) {
        try {
            StringTokenizer st = new StringTokenizer(param);
            String type = st.nextToken();
            if (type.equalsIgnoreCase("continue")) {
                try {
                    String select = st.nextToken();
                    int i = Integer.parseInt(st.nextToken());
                    int j = 0;
                    try {
                        j = Integer.parseInt(st.nextToken());
                    } catch (Exception exception) {
                        j = i;
                    }
                    if (select.equalsIgnoreCase("rendering")) {
                        for (int k = i; k < j + 1; k++) {
                            pc.sendPackets(new S_SystemMessage("\\aAアイコン持続出力番号 : [\\aG" + k + "\\aA]"));
                            pc.sendPackets(new S_PacketBox(S_PacketBox.UNLIMITED_ICON1, k, true));
                        }
                    } else if (select.equalsIgnoreCase("delete")) {
                        for (int k = i; k < j + 1; k++) {
                            pc.sendPackets(new S_SystemMessage("\\aAアイコンの削除番号 : [\\aG" + k + "\\aA]"));
                            pc.sendPackets(new S_PacketBox(S_PacketBox.UNLIMITED_ICON1, k, false));
                        }
                    } else {
                        pc.sendPackets(new S_SystemMessage("\\aIアイコン[持続] [表現or削除] [iまたはi〜j]"));
                    }
                } catch (Exception exception) {
                    pc.sendPackets(new S_SystemMessage("\\aIアイコン[持続] [表現or削除] [iまたはi〜j]"));
                }
            } else if (type.equalsIgnoreCase("continuity")) {
                try {
                    int i = Integer.parseInt(st.nextToken());
                    int j = 0;
                    try {
                        j = Integer.parseInt(st.nextToken());
                    } catch (Exception exception) {
                        j = i;
                    }
                    for (int k = i; k < j + 1; k++) {
                        pc.sendPackets(new S_SystemMessage("\\aAアイコン連続出力番号:[\\aG" + k + "\\aA]"));
                        pc.sendPackets(new S_PacketBox(S_PacketBox.UNLIMITED_ICON1, k, true));
                        Thread.sleep(500);
                        pc.sendPackets(new S_PacketBox(S_PacketBox.UNLIMITED_ICON1, k, false));
                    }
                } catch (Exception exception) {
                    pc.sendPackets(new S_SystemMessage("\\aL.アイコン[連続] [iまたは i~j]"));
                }
            } else {
                pc.sendPackets(new S_SystemMessage("\\aI.アイコン[持続] [表現or削除] [iまたは i~j]"));
                pc.sendPackets(new S_SystemMessage("\\aL.アイコン[連続] [iまたは i~j]"));
            }
        } catch (Exception exception) {
            pc.sendPackets(new S_SystemMessage("\\aI.アイコン[持続] [表現or削除] [iまたは i~j]"));
            pc.sendPackets(new S_SystemMessage("\\aL.アイコン[連続] [iまたは i~j]"));
        }
    }

    private void chainfo(L1PcInstance gm, String param) {
        try {
            StringTokenizer stringtokenizer = new StringTokenizer(param);
            String s = stringtokenizer.nextToken();
            gm.sendPackets(new S_Chainfo(1, s));
        } catch (Exception exception21) {
            gm.sendPackets(new S_SystemMessage(".剣[キャラクター名]を入力してください。"));
        }
    }

    private void cleaningDoll(L1PcInstance gm) {
        int count = 0;
        int ccount = 0;
        for (Object obj : L1World.getInstance().getObject()) {
            if (obj instanceof L1DollInstance) {
                L1DollInstance doll = (L1DollInstance) obj;
                if (doll.getMaster() == null) {
                    count++;
                    doll.deleteMe();
                } else if (((L1PcInstance) doll.getMaster()).getNetConnection() == null) {
                    ccount++;
                    doll.deleteMe();
                }
            }
        }
        gm.sendPackets(new S_SystemMessage("人形清掃本数 - マスターX：" + count + "  主人接種: " + ccount), true);
    }

    private void CharacterBalance(L1PcInstance pc, String param) {
        Connection con = null;
        PreparedStatement pstm = null;

        try {
            StringTokenizer st = new StringTokenizer(param);

            String charName = st.nextToken();
            int addDamage = Integer.parseInt(st.nextToken());
            int addDamageRate = Integer.parseInt(st.nextToken());
            int addReduction = Integer.parseInt(st.nextToken());
            int addReductionRate = Integer.parseInt(st.nextToken());

            L1PcInstance player = L1World.getInstance().getPlayer(charName);

            if (player != null) {
                player.setAddDamage(addDamage);
                player.setAddDamageRate(addDamageRate);
                player.setAddReduction(addReduction);
                player.setAddReductionRate(addReductionRate);
                player.save();
            } else {
                int i = 0;
                con = L1DatabaseFactory.getInstance().getConnection();
                pstm = con.prepareStatement(
                        "update characters set AddDamage = ?, AddDamageRate = ?, AddReduction = ?, AddReductionRate = ? where char_name = ?");
                pstm.setInt(++i, addDamage);
                pstm.setInt(++i, addDamageRate);
                pstm.setInt(++i, addReduction);
                pstm.setInt(++i, addReductionRate);
                pstm.setString(++i, charName);
                pstm.executeQuery();
            }

        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage("。バランス[キャラクター名] [ツタ】【ツタ確率] [リドク] [リドク確率]"));
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    // 広域追放範囲

    private void changeQuiz(L1PcInstance gm, String param) {
        try {
            StringTokenizer tok = new StringTokenizer(param);
            String user = tok.nextToken();
            String newquiz = tok.nextToken();

            if (newquiz.length() < 4) {
                gm.sendPackets(new S_SystemMessage("入力されたクイズの桁数が短すぎます。"));
                gm.sendPackets(new S_SystemMessage("最低4文字以上入力してください。"));
                return;
            }

            if (newquiz.length() > 12) {
                gm.sendPackets(new S_SystemMessage("入力されたクイズの桁数が長すぎます。"));
                gm.sendPackets(new S_SystemMessage("最大12文字以下で入力してください。"));
                return;
            }

            if (isDisitAlpha(newquiz) == false) {
                gm.sendPackets(new S_SystemMessage("パスワードに使用できない文字が含まれています。"));
                return;
            }
            L1PcInstance target = L1World.getInstance().getPlayer(user);
            if (target != null) {
                changeQuiz(gm, target, newquiz);
            } else {
                if (!changeQuiz(gm, user, newquiz))
                    gm.sendPackets(new S_SystemMessage("そのような名前を持つキャラクターはありません。"));
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage("。クイズ変更[キャラクター名] [パスワード]"));
        }
    }

    private void AddAccount(L1PcInstance gm, String account, String passwd, String Ip, String Host) {
        try {
            String login = null;
            String password = null;
            java.sql.Connection con = null;
            con = L1DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = null;
            PreparedStatement pstm = null;

            password = passwd;

            statement = con.prepareStatement("select * from accounts where login Like '" + account + "'");
            ResultSet rs = statement.executeQuery();

            if (rs.next())
                login = rs.getString(1);
            if (login != null) {
                gm.sendPackets(new S_SystemMessage("アカウントがあります。"));
                return;
            } else {
                String sqlstr = "INSERT INTO accounts SET login=?,password=?,lastactive=?,access_level=?,ip=?,host=?,banned=?,charslot=?,gamepassword=?,notice=?";
                pstm = con.prepareStatement(sqlstr);
                pstm.setString(1, account);
                pstm.setString(2, password);
                pstm.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                pstm.setInt(4, 0);
                pstm.setString(5, Ip);
                pstm.setString(6, Host);
                pstm.setInt(7, 0);
                pstm.setInt(8, 6);
                pstm.setInt(9, 0);
                pstm.setInt(10, 0);
                pstm.execute();
                gm.sendPackets(new S_SystemMessage("アカウントの追加が完了しました。"));
            }

            rs.close();
            pstm.close();
            statement.close();
            con.close();
        } catch (Exception e) {
        }
    }

    private static boolean isDisitAlpha(String str) {
        boolean check = true;
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i)) // 数字がない場合は
                    && !Character.isUpperCase(str.charAt(i)) // 大文字がない場合は
                    && !Character.isLowerCase(str.charAt(i))) { // 小文字がない場合は
                check = false;
                break;
            }
        }
        return check;
    }

    // インベントリ削除
    private void InventoryDelete(L1PcInstance pc, String param) {
        try {
            for (L1ItemInstance item : pc.getInventory().getItems()) {
                if (!item.isEquipped()) {
                    pc.getInventory().removeItem(item);
                }
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(".インベントリ削除"));
        }
    }

    private void targetInventoryDelete(L1PcInstance user, String param) {
        try {
            StringTokenizer st = new StringTokenizer(param);
            String char_name = st.nextToken();
            L1PcInstance target = L1World.getInstance().getPlayer(char_name);
            for (L1ItemInstance item : user.getInventory().getItems()) {
                if (!item.isEquipped()) {
                    target.getInventory().removeItem(item);
                }
            }

        } catch (Exception e) {
            user.sendPackets(new S_SystemMessage("。インベントリ削除[接続されているキャラクター名]を入力。"));
        }
    }

    private void addaccount(L1PcInstance gm, String param) {
        try {
            StringTokenizer tok = new StringTokenizer(param);
            String user = tok.nextToken();
            String passwd = tok.nextToken();

            if (user.length() < 4) {
                gm.sendPackets(new S_SystemMessage("入力されたアカウント名の数字が短すぎます。"));
                gm.sendPackets(new S_SystemMessage("最低4文字以上入力してください。"));
                return;
            }
            if (passwd.length() < 4) {
                gm.sendPackets(new S_SystemMessage("入力されたパスワードの桁数が短すぎます。"));
                gm.sendPackets(new S_SystemMessage("最低4文字以上入力してください。"));
                return;
            }

            if (passwd.length() > 12) {
                gm.sendPackets(new S_SystemMessage("入力されたパスワードの桁数が長すぎます。"));
                gm.sendPackets(new S_SystemMessage("最大12文字以下で入力してください。"));
                return;
            }

            if (isDisitAlpha(passwd) == false) {
                gm.sendPackets(new S_SystemMessage("パスワードに使用できない文字が含まれています。"));
                return;
            }
            AddAccount(gm, user, passwd, "127.0.0.1", "127.0.0.1");
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage("アカウントの追加[アカウント名] [パスワード]を入力してください。"));
        }
    }

    private void TelePort() {
        for (L1PcInstance c : L1World.getInstance().getAllPlayers()) {
            switch (c.getMap().getId()) {
                case 5167:
                    new L1Teleport().teleport(c, 33970, 33246, (short) 4, 4, true);
                    c.sendPackets(new S_ChatPacket(c, "運営者様が悪魔王の領土を終了ハショトスプます。"));
                    break;
                case 701:
                    new L1Teleport().teleport(c, 33970, 33246, (short) 4, 4, true);
                    c.sendPackets(new S_ChatPacket(c, "運営者様がアデン狩り場を終了ハショトスプます。"));
                    break;
                default:
                    break;
            }
        }
    }

    private void NoShopAndWare(L1PcInstance gm, String param) {
        try {
            StringTokenizer tok = new StringTokenizer(param);
            String type = tok.nextToken();
            if (type.equalsIgnoreCase("reload")) {
                NoShopAndWare.getInstance().reload();
                gm.sendPackets(new S_SystemMessage("倉庫不可の設定がリロードされました。"));
            } else if (type.equalsIgnoreCase("search")) {
                java.sql.Connection con = null;
                PreparedStatement pstm = null;
                ResultSet rs = null;
                String nameid = tok.nextToken();
                try {
                    con = L1DatabaseFactory.getInstance().getConnection();
                    String strQry;
                    strQry = " Select e.item_id, e.name from etcitem e, NoShopAndWare l where l.item_id = e.item_id and name Like '%"
                            + nameid + "%' ";
                    strQry += " union all "
                            + " Select w.item_id, w.name from weapon w, NoShopAndWare l where l.item_id = w.item_id and name Like '%"
                            + nameid + "%' ";
                    strQry += " union all "
                            + " Select a.item_id, a.name from armor a, NoShopAndWare l where l.item_id = a.item_id and name Like '%"
                            + nameid + "%' ";
                    pstm = con.prepareStatement(strQry);
                    rs = pstm.executeQuery();
                    while (rs.next()) {
                        gm.sendPackets(
                                new S_SystemMessage("[" + rs.getString("item_id") + "] " + rs.getString("name")));
                    }
                } catch (Exception e) {
                } finally {
                    rs.close();
                    pstm.close();
                    con.close();
                }
            } else {
                String nameid = tok.nextToken();
                int itemid = 0;
                try {
                    itemid = Integer.parseInt(nameid);
                } catch (NumberFormatException e) {
                    itemid = ItemTable.getInstance().findItemIdByNameWithoutSpace(nameid);
                    if (itemid == 0) {
                        gm.sendPackets(new S_SystemMessage("該当のアイテムが見つかりません。"));
                        return;
                    }
                }
                L1Item temp = ItemTable.getInstance().getTemplate(itemid);
                if (temp == null) {
                    gm.sendPackets(new S_SystemMessage("該当のアイテムが見つかりません。"));
                    return;
                }
                if (type.equalsIgnoreCase("add")) {
                    if (NoShopAndWare.getInstance().isNoShopAndWare(itemid)) {
                        gm.sendPackets(new S_SystemMessage("すでに倉庫不可リストにあります。"));
                        return;
                    }
                    NoShopAndWare.getInstance().storeId(itemid);
                    gm.sendPackets(new S_SystemMessage("倉庫不可項目に追加しました。"));
                } else if (type.equalsIgnoreCase("delete")) {
                    if (!NoShopAndWare.getInstance().isNoShopAndWare(itemid)) {
                        gm.sendPackets(new S_SystemMessage("倉庫不可項目に該当アイテムがありません。"));
                        return;
                    }
                    gm.sendPackets(new S_SystemMessage("倉庫不可項目から削除されました。"));
                    NoShopAndWare.getInstance().deleteId(itemid);
                }
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage("倉庫不可リロード"));
            gm.sendPackets(new S_SystemMessage("倉庫不可追加|削除itemid | name"));

            gm.sendPackets(new S_SystemMessage("倉庫不可検索name"));
        }
    }

    private void NoDropItem(L1PcInstance gm, String param) {
        try {
            StringTokenizer tok = new StringTokenizer(param);
            String type = tok.nextToken();
            if (type.equalsIgnoreCase("reload")) {
                NoDropItem.getInstance().reload();
                gm.sendPackets(new S_SystemMessage("ドロップ不可の設定がリロードされました。"));
            } else if (type.equalsIgnoreCase("search")) {
                java.sql.Connection con = null;
                PreparedStatement pstm = null;
                ResultSet rs = null;
                String nameid = tok.nextToken();
                try {
                    con = L1DatabaseFactory.getInstance().getConnection();
                    String strQry;
                    strQry = " Select e.item_id, e.name from etcitem e, NoDropItem l where l.item_id = e.item_id and name Like '%"
                            + nameid + "%' ";
                    strQry += " union all "
                            + " Select w.item_id, w.name from weapon w, NoDropItem l where l.item_id = w.item_id and name Like '%"
                            + nameid + "%' ";
                    strQry += " union all "
                            + " Select a.item_id, a.name from armor a, NoDropItem l where l.item_id = a.item_id and name Like '%"
                            + nameid + "%' ";
                    pstm = con.prepareStatement(strQry);
                    rs = pstm.executeQuery();
                    while (rs.next()) {
                        gm.sendPackets(
                                new S_SystemMessage("[" + rs.getString("item_id") + "] " + rs.getString("name")));
                    }
                } catch (Exception e) {
                } finally {
                    rs.close();
                    pstm.close();
                    con.close();
                }
            } else {
                String nameid = tok.nextToken();
                int itemid = 0;
                try {
                    itemid = Integer.parseInt(nameid);
                } catch (NumberFormatException e) {
                    itemid = ItemTable.getInstance().findItemIdByNameWithoutSpace(nameid);
                    if (itemid == 0) {
                        gm.sendPackets(new S_SystemMessage("該当のアイテムが見つかりません。"));
                        return;
                    }
                }
                L1Item temp = ItemTable.getInstance().getTemplate(itemid);
                if (temp == null) {
                    gm.sendPackets(new S_SystemMessage("該当のアイテムが見つかりません。"));
                    return;
                }
                if (type.equalsIgnoreCase("add")) {
                    if (NoDropItem.getInstance().isNoDropItem(itemid)) {
                        gm.sendPackets(new S_SystemMessage("すでにドロップ不可リストにあります。"));
                        return;
                    }
                    NoDropItem.getInstance().storeId(itemid);
                    gm.sendPackets(new S_SystemMessage("ドロップ不可アイテムに追加しました。"));
                } else if (type.equalsIgnoreCase("delete")) {
                    if (!NoDropItem.getInstance().isNoDropItem(itemid)) {
                        gm.sendPackets(new S_SystemMessage("ドロップ不可アイテムに対応するアイテムがありません。"));
                        return;
                    }
                    gm.sendPackets(new S_SystemMessage("ロップ不可アイテムから削除されました。"));
                    NoDropItem.getInstance().deleteId(itemid);
                }
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage("。ドロップ不可リロード"));
            gm.sendPackets(new S_SystemMessage("。ドロップ不可追加|削除itemid | name"));

            gm.sendPackets(new S_SystemMessage("。ドロップ不可検索name"));
        }
    }

    private void NoTradable(L1PcInstance gm, String param) {
        try {
            StringTokenizer tok = new StringTokenizer(param);
            String type = tok.nextToken();
            if (type.equalsIgnoreCase("reload")) {
                NoTradable.getInstance().reload();
                gm.sendPackets(new S_SystemMessage("ドロップ不可の設定がリロードされました。"));
            } else if (type.equalsIgnoreCase("search")) {
                java.sql.Connection con = null;
                PreparedStatement pstm = null;
                ResultSet rs = null;
                String nameid = tok.nextToken();
                try {
                    con = L1DatabaseFactory.getInstance().getConnection();
                    String strQry;
                    strQry = " Select e.item_id, e.name from etcitem e, NoTradable l where l.item_id = e.item_id and name Like '%"
                            + nameid + "%' ";
                    strQry += " union all "
                            + " Select w.item_id, w.name from weapon w, NoTradable l where l.item_id = w.item_id and name Like '%"
                            + nameid + "%' ";
                    strQry += " union all "
                            + " Select a.item_id, a.name from armor a, NoTradable l where l.item_id = a.item_id and name Like '%"
                            + nameid + "%' ";
                    pstm = con.prepareStatement(strQry);
                    rs = pstm.executeQuery();
                    while (rs.next()) {
                        gm.sendPackets(
                                new S_SystemMessage("[" + rs.getString("item_id") + "] " + rs.getString("name")));
                    }
                } catch (Exception e) {
                } finally {
                    rs.close();
                    pstm.close();
                    con.close();
                }
            } else {
                String nameid = tok.nextToken();
                int itemid = 0;
                try {
                    itemid = Integer.parseInt(nameid);
                } catch (NumberFormatException e) {
                    itemid = ItemTable.getInstance().findItemIdByNameWithoutSpace(nameid);
                    if (itemid == 0) {
                        gm.sendPackets(new S_SystemMessage("該当のアイテムが見つかりません。"));
                        return;
                    }
                }
                L1Item temp = ItemTable.getInstance().getTemplate(itemid);
                if (temp == null) {
                    gm.sendPackets(new S_SystemMessage("該当のアイテムが見つかりません。"));
                    return;
                }
                if (type.equalsIgnoreCase("add")) {
                    if (NoTradable.getInstance().isNoTradable(itemid)) {
                        gm.sendPackets(new S_SystemMessage("すでに交換不可リストにあります。"));
                        return;
                    }
                    NoTradable.getInstance().storeId(itemid);
                    gm.sendPackets(new S_SystemMessage("交換不可の項目に追加しました。"));
                } else if (type.equalsIgnoreCase("delete")) {
                    if (!NoTradable.getInstance().isNoTradable(itemid)) {
                        gm.sendPackets(new S_SystemMessage("交換不可の項目に該当する項目がありません。"));
                        return;
                    }
                    gm.sendPackets(new S_SystemMessage("交換不可の項目から削除されました。"));
                    NoTradable.getInstance().deleteId(itemid);
                }
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage("。交換不可リロード"));
            gm.sendPackets(new S_SystemMessage("。交換不可追加|削除itemid | name"));

            gm.sendPackets(new S_SystemMessage("。交換不可検索name"));
        }
    }

    private void allpresent(L1PcInstance gm, String param) {
        try {
            StringTokenizer st = new StringTokenizer(param);
            int itemid = Integer.parseInt(st.nextToken(), 10);
            int enchant = Integer.parseInt(st.nextToken(), 10);
            int count = Integer.parseInt(st.nextToken(), 10);
            Collection<L1PcInstance> player = null;
            player = L1World.getInstance().getAllPlayers();
            for (L1PcInstance target : player) {
                if (target == null)
                    continue;
                if (!target.isGhost() && !target.isPrivateShop() && !target.isAutoClanjoin() && !target.noPlayerCK) {
                    L1ItemInstance item = ItemTable.getInstance().createItem(itemid);
                    item.setCount(count);
                    item.setEnchantLevel(enchant);
                    if (item != null) {
                        if (target.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
                            target.getInventory().storeItem(item);
                        }
                    }
                    target.sendPackets(new S_SkillSound(target.getId(), 1091));// 鳩アクション
                    target.sendPackets(new S_SkillSound(target.getId(), 4856));// ハートアクション
                    target.sendPackets(new S_SystemMessage("\\aDオペレータの全体プレゼント : " + item.getLogName())); // item.getLogName
                    // //item.getViewName
                }
            }
        } catch (Exception exception) {
            gm.sendPackets(new S_SystemMessage("全体ギフト[システムID] [エンチャン] [本数]"));
        }
    }

    private void returnEXP(L1PcInstance gm, String param) {
        try {
            StringTokenizer tokenizer = new StringTokenizer(param);
            String pcName = tokenizer.nextToken();
            L1PcInstance target = L1World.getInstance().getPlayer(pcName);
            if (target != null) {
                int oldLevel = target.getLevel();
                int needExp = ExpTable.getNeedExpNextLevel(oldLevel);
                int exp = 0;
                if (oldLevel >= 1 && oldLevel < 11) {
                    exp = 0;
                } else if (oldLevel >= 11 && oldLevel < 45) {
                    exp = (int) (needExp * 0.1);
                } else if (oldLevel == 45) {
                    exp = (int) (needExp * 0.09);
                } else if (oldLevel == 46) {
                    exp = (int) (needExp * 0.08);
                } else if (oldLevel == 47) {
                    exp = (int) (needExp * 0.07);
                } else if (oldLevel == 48) {
                    exp = (int) (needExp * 0.06);
                } else if (oldLevel >= 49) {
                    exp = (int) (needExp * 0.05);
                }
                target.addExp(+exp);
                target.save();
                target.saveInventory();

                gm.sendPackets(new S_SystemMessage("そのキャラクター+5上昇"));
            } else {
                gm.sendPackets(new S_SystemMessage("そのキャラクター未接続状態。"));
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage("。景色回復[キャラクター名]"));
        }
    }

    // .アカウント-----------------------------------------------------------------
    // 同じアカウントのキャラクター検査
    private void account_Cha(L1PcInstance gm, String param) {
        try {
            StringTokenizer tok = new StringTokenizer(param);
            String name = tok.nextToken();
            account_Cha2(gm, name);
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage("アカウント名"));
        }
    }

    private void account_Cha2(L1PcInstance gm, String param) {
        try {
            String s_account = null;
            String s_name = param;
            String s_level = null;
            String s_clan = null;
            String s_bonus = null;
            String s_online = null;
            String s_hp = null;
            String s_mp = null;
            String s_type = null;// 追加
            int count = 0;
            int count0 = 0;
            java.sql.Connection con0 = null; // 名前でobjidを検索するために
            con0 = L1DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement0 = null;
            statement0 = con0.prepareStatement(
                    "select account_name, Clanname  from characters where char_name = '" + s_name + "'");
            ResultSet rs0 = statement0.executeQuery();
            while (rs0.next()) {
                s_account = rs0.getString(1);
                s_clan = rs0.getString(2);
                gm.sendPackets(new S_SystemMessage("\\aD------------------------------------------"));
                gm.sendPackets(new S_SystemMessage("\\aEキャラクター : " + s_name + "(" + s_account + ")  クラン : " + s_clan));// +"
                // クラス:"
                // +
                // s_type
                count0++;
            }
            java.sql.Connection con = null;
            con = L1DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = null;
            statement = con.prepareStatement(
                    "select " + "char_name," + "level," + "Clanname," + "BonusStatus," + "OnlineStatus," + "MaxHp,"
                            + "MaxMp, " + "Type " + " from characters where account_name = '" + s_account + "'");
            gm.sendPackets(new S_SystemMessage("\\aD------------------------------------------"));
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                s_name = rs.getString(1);
                s_level = rs.getString(2);
                s_clan = rs.getString(3);
                s_bonus = rs.getString(4);
                s_online = rs.getString(5);
                s_hp = rs.getString(6);
                s_mp = rs.getString(7);
                s_type = rs.getString(8);
                gm.sendPackets(new S_SystemMessage(
                        "接続:[" + s_online + "] ラップ:" + s_level + " [" + s_name + "] クラス:" + s_type + ""));
                count++;
            }
            rs0.close();
            statement0.close();
            con0.close();
            rs.close();
            statement.close();
            con.close();
            gm.sendPackets(new S_SystemMessage("\\aF0君主1ナイト2妖精3ウィザード4エルフ5のナイト6幻術"));
            gm.sendPackets(new S_SystemMessage("\\aD------------------------------------------"));
        } catch (Exception e) {
        }
    }

    // アカウント１ -----------------------------------------------------------------

    private void Pvp(L1PcInstance gm, String param) {
        try {
            StringTokenizer st = new StringTokenizer(param);
            String type = st.nextToken();

            if (type.equals("on")) {
                Config.ALT_NONPVP = true;
                Config.setParameterValue("AltNonPvP", "true");
                L1World.getInstance()
                        .broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "今後のPvPが正常にできます。"));
            } else if (type.equals("off")) {
                Config.ALT_NONPVP = false;
                Config.setParameterValue("AltNonPvP", "false");
                L1World.getInstance()
                        .broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "今後のPvPが一定時間の間は不可能です。"));
            }

        } catch (Exception exception) {
            gm.sendPackets(new S_SystemMessage("。ピケイ[入/切]"));
        }
    }

    private void search_banned(L1PcInstance paramL1PcInstance) {
        try {
            String str1 = null;
            String str2 = null;
            int i = 0;
            Connection localConnection = null;
            localConnection = L1DatabaseFactory.getInstance().getConnection();
            PreparedStatement localPreparedStatement = null;
            localPreparedStatement = localConnection.prepareStatement(
                    "select accounts.login, characters.char_name from accounts,characters where accounts.banned=1 and accounts.login=characters.account_name ORDER BY accounts.login ASC");
            ResultSet localResultSet = localPreparedStatement.executeQuery();
            while (localResultSet.next()) {
                str1 = localResultSet.getString(1);
                str2 = localResultSet.getString(2);
                paramL1PcInstance.sendPackets(new S_SystemMessage(new StringBuilder().append("アカウント:[").append(str1)
                        .append("], キャラクター名:[").append(str2).append("]").toString()));
                ++i;
            }
            localResultSet.close();
            localPreparedStatement.close();
            localConnection.close();
            paramL1PcInstance.sendPackets(new S_SystemMessage(
                    new StringBuilder().append("総[").append(i).append("]本差し押さえアカウント/キャラクターが見つかりました。").toString()));
        } catch (Exception localException) {
        }
    }

    private void accountdel(L1PcInstance gm, String param) {
        try {
            StringTokenizer tokenizer = new StringTokenizer(param);
            String pcName = tokenizer.nextToken();
            Connection con = null;
            Connection con2 = null;
            PreparedStatement pstm = null;
            PreparedStatement pstm2 = null;
            ResultSet find = null;
            String findcha = null;
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT * FROM characters WHERE char_name=?");
            pstm.setString(1, pcName);
            find = pstm.executeQuery();

            while (find.next()) {
                findcha = find.getString(1);
            }

            if (findcha == null) {
                gm.sendPackets(new S_SystemMessage("DBに " + pcName + "キャラクター名が存在しません"));

                con.close();
                pstm.close();
                find.close();

            } else {
                con2 = L1DatabaseFactory.getInstance().getConnection();
                pstm2 = con.prepareStatement("UPDATE accounts SET banned = 0 WHERE login= ?");
                pstm2.setString(1, findcha);
                pstm2.execute();

                gm.sendPackets(new S_SystemMessage(pcName + "アカウント差し押さえを解除しました"));
                con.close();
                pstm.close();
                find.close();
                con2.close();
                pstm2.close();
            }
        } catch (Exception exception) {
            gm.sendPackets(new S_SystemMessage("差押解除キャラクター名で入力してください。"));
        }
    }

    private void GiveAbyssPoint(L1PcInstance pc, String poby) {
        try {
            StringTokenizer st = new StringTokenizer(poby);
            String pobyname = st.nextToken();
            int point = Integer.parseInt(st.nextToken());
            L1PcInstance target = L1World.getInstance().getPlayer(pobyname);
            if (target != null) {
                target.addAbysspoint(point);
                pc.sendPackets(new S_SystemMessage(target.getName() + "様に[アビスポイント" + point + "点]を支払った。"));
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage("。一重支給[支払キャラクター名] [支給するポイント]"));
        }

    }

    private void GiveClanPoint(L1PcInstance pc, String poby) { // 血盟経験値付与
        try {
            StringTokenizer st = new StringTokenizer(poby);
            String pobyname = st.nextToken();
            int point = Integer.parseInt(st.nextToken());
            L1PcInstance target = L1World.getInstance().getPlayer(pobyname);
            if (target != null) {
                if (target.getClanid() != 0) {
                    L1Clan TargetClan = L1World.getInstance().getClan(target.getClanname());
                    TargetClan.addClanExp(point);
                    ClanTable.getInstance().updateClan(TargetClan);
                    pc.sendPackets(new S_SystemMessage(target.getClanname() + "血盟に[経験値" + point + "]を支払った。"));
                    for (L1PcInstance tc : TargetClan.getOnlineClanMember()) {
                        tc.sendPackets(new S_SystemMessage("ゲームマスターから血盟経験値[" + point + "]を支給しました。"));
                    }
                } else {
                    pc.sendPackets(new S_SystemMessage(target.getName() + "様は、血盟に所属していません。"));
                }
            } else {
                pc.sendPackets(new S_ServerMessage(73, pobyname));
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage("。血盟経験値[支給する血盟君主名] [支給するポイント]"));
        }

    }

    private void changePassword(L1PcInstance gm, String param) {
        try {
            StringTokenizer tok = new StringTokenizer(param);
            String user = tok.nextToken();
            String passwd = tok.nextToken();

            if (passwd.length() < 4) {
                gm.sendPackets(new S_SystemMessage("入力されたパスワードの桁数が短すぎます。"));
                gm.sendPackets(new S_SystemMessage("最低4文字以上入力してください。"));
                return;
            }

            if (passwd.length() > 12) {
                gm.sendPackets(new S_SystemMessage("入力されたパスワードの桁数が長すぎます。"));
                gm.sendPackets(new S_SystemMessage("最大12文字以下で入力してください。"));
                return;
            }

            if (isDisitAlpha(passwd) == false) {
                gm.sendPackets(new S_SystemMessage("パスワードに使用できない文字が含まれています。"));
                return;
            }
            L1PcInstance target = L1World.getInstance().getPlayer(user);
            if (target != null) {
                to_Change_Passwd(gm, target, passwd);
            } else {
                if (!to_Change_Passwd(gm, user, passwd))
                    gm.sendPackets(new S_SystemMessage("そのような名前を持つキャラクターはありません。"));
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage("パスワードの変更[キャラクター名] [パスワード]に入力してください。"));
        }
    }

    private void to_Change_Passwd(L1PcInstance gm, L1PcInstance pc, String passwd) {
        PreparedStatement statement = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        java.sql.Connection con = null;
        try {
            String login = null;
            String password = null;
            con = L1DatabaseFactory.getInstance().getConnection();
            password = passwd;
            statement = con.prepareStatement(
                    "select account_name from characters where char_name Like '" + pc.getName() + "'");
            rs = statement.executeQuery();

            while (rs.next()) {
                login = rs.getString(1);
                pstm = con.prepareStatement("UPDATE accounts SET password=? WHERE login Like '" + login + "'");
                pstm.setString(1, password);
                pstm.execute();
                gm.sendPackets(new S_SystemMessage("パスワードの変更アカウント：[" + login + "]パスワード：[" + passwd + "]"));
                gm.sendPackets(new S_SystemMessage(pc.getName() + "パスワードの変更完了。"));
            }
        } catch (Exception e) {
            System.out.println("to_Change_Passwd() Error : " + e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(statement);
            SQLUtil.close(con);
        }
    }

    private boolean to_Change_Passwd(L1PcInstance pc, String name, String passwd) {
        PreparedStatement statement = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        java.sql.Connection con = null;
        try {
            String login = null;
            String password = null;
            con = L1DatabaseFactory.getInstance().getConnection();
            password = passwd;
            statement = con.prepareStatement("select account_name from characters where char_name Like '" + name + "'");
            rs = statement.executeQuery();

            while (rs.next()) {
                login = rs.getString(1);
                pstm = con.prepareStatement("UPDATE accounts SET password=? WHERE login Like '" + login + "'");
                pstm.setString(1, password);
                pstm.execute();
                pc.sendPackets(new S_SystemMessage("パスワードの変更アカウント：[" + login + "]パスワード：[" + passwd + "]"));
                pc.sendPackets(new S_SystemMessage("そのキャラクターパスワードの変更完了。 （未接続中）"));
            }
            return true;
        } catch (Exception e) {
            System.out.println("to_Change_Passwd() Error : " + e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(statement);
            SQLUtil.close(con);
        }
        return false;
    }

    public void allBuff1(L1PcInstance gm) { // 画面の中のユーザーズにフルバフ
        int[] allBuffSkill = { DECREASE_WEIGHT, PHYSICAL_ENCHANT_DEX, PHYSICAL_ENCHANT_STR, BLESS_WEAPON, IRON_SKIN,
                NATURES_TOUCH, ADDITIONAL_FIRE, INSIGHT, DRAGON_SKIN };
        L1SkillUse l1skilluse = null;
        for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(gm, 30)) {
            if (pc.isPrivateShop() || pc.isAutoClanjoin()) {
                continue;
            }
            l1skilluse = new L1SkillUse();
            for (int i = 0; i < allBuffSkill.length; i++) {
                l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0,
                        L1SkillUse.TYPE_GMBUFF);
            }
            pc.sendPackets(new S_SystemMessage("\\aDオペレータの周りに「全体バフ」が詠唱されました。"));
        }
    }

    private void allBuff2(L1PcInstance gm) {
        int[] allBuffSkill = { FEATHER_BUFF_A };
        try {
            for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(gm, 30)) {
                if (!pc.isPrivateShop() && !pc.isAutoClanjoin() && !pc.isDead()) {
                    if (pc.isGhost() == false) {
                        L1SkillUse l1skilluse = new L1SkillUse();
                        for (int i = 0; i < allBuffSkill.length; i++) {
                            l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0,
                                    L1SkillUse.TYPE_GMBUFF);
                        }
                        pc.sendPackets(new S_SystemMessage("\\aDオペレータの周りに「メティスの祝福」が詠唱されました。"));
                    }
                }
            }
        } catch (Exception exception19) {
            gm.sendPackets(new S_SystemMessage("。バフエラー"));
        }
    }

    private void allBuff3(L1PcInstance gm) {
        int[] allBuffSkill = { LIFE_MAAN };
        try {
            for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(gm, 30)) {
                if (!pc.isPrivateShop() && !pc.isAutoClanjoin() && !pc.isDead()) {
                    if (pc.isGhost() == false) {
                        L1SkillUse l1skilluse = new L1SkillUse();
                        for (int i = 0; i < allBuffSkill.length; i++) {
                            l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0,
                                    L1SkillUse.TYPE_GMBUFF);
                        }
                        pc.sendPackets(new S_SystemMessage("\\aDオペレータの周りに「生命の魔眼」が詠唱されました。"));
                    }
                }
            }
        } catch (Exception exception19) {
            gm.sendPackets(new S_SystemMessage("バフエラー"));
        }
    }

    private void allBuff4(L1PcInstance gm) {
        int[] allBuffSkill = { God_buff };
        try {
            for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(gm, 30)) {
                if (!pc.isPrivateShop() && !pc.isAutoClanjoin() && !pc.isDead()) {
                    if (pc.isGhost() == false) {
                        if (pc.hasSkillEffect(L1SkillId.God_buff)) {
                            pc.removeSkillEffect(L1SkillId.God_buff);
                        }
                        L1SkillUse l1skilluse = new L1SkillUse();
                        for (int i = 0; i < allBuffSkill.length; i++) {
                            l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0,
                                    L1SkillUse.TYPE_GMBUFF);
                        }
                        pc.sendPackets(new S_SystemMessage("\\aDオペレータの周りに「黒砂のバフ」が詠唱されました。"));
                    }
                }
            }
        } catch (Exception exception19) {
            gm.sendPackets(new S_SystemMessage("。バフエラー"));
        }
    }

    private void allBuff5(L1PcInstance gm) {
        int[] allBuffSkill = { COMA_B };
        try {
            for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(gm, 30)) {
                if (!pc.isPrivateShop() && !pc.isAutoClanjoin() && !pc.isDead()) {
                    if (pc.isGhost() == false) {
                        L1SkillUse l1skilluse = new L1SkillUse();
                        for (int i = 0; i < allBuffSkill.length; i++) {
                            l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0,
                                    L1SkillUse.TYPE_GMBUFF);
                        }
                        pc.sendPackets(new S_SystemMessage("\\aDオペレータの周りに「コマバフ」が詠唱されました。"));
                    }
                }
            }
        } catch (Exception exception19) {
            gm.sendPackets(new S_SystemMessage("。バフエラー"));
        }
    }

    private void changeQuiz(L1PcInstance gm, L1PcInstance pc, String newquiz) {
        PreparedStatement statement = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        java.sql.Connection con = null;
        try {
            String login = null;
            String quiz = null;
            con = L1DatabaseFactory.getInstance().getConnection();
            quiz = newquiz;
            statement = con.prepareStatement(
                    "select account_name from characters where char_name Like '" + pc.getName() + "'");
            rs = statement.executeQuery();

            while (rs.next()) {
                login = rs.getString(1);
                pstm = con.prepareStatement("UPDATE accounts SET quiz=? WHERE login Like '" + login + "'");
                pstm.setString(1, quiz);
                pstm.execute();
                gm.sendPackets(new S_SystemMessage("クイズ変更アカウント：[" + login + "]クイズ：[" + quiz + "]"));
                gm.sendPackets(new S_SystemMessage(pc.getName() + "さんのクイズ変更完了。"));
            }
        } catch (Exception e) {
            System.out.println("to_Change_Passwd() Error : " + e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(statement);
            SQLUtil.close(con);
        }
    }

    private boolean changeQuiz(L1PcInstance pc, String name, String newquiz) {
        PreparedStatement statement = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        java.sql.Connection con = null;
        try {
            String login = null;
            String quiz = null;
            con = L1DatabaseFactory.getInstance().getConnection();
            quiz = newquiz;
            statement = con.prepareStatement("select account_name from characters where char_name Like '" + name + "'");
            rs = statement.executeQuery();

            while (rs.next()) {
                login = rs.getString(1);
                pstm = con.prepareStatement("UPDATE accounts SET quiz=? WHERE login Like '" + login + "'");
                pstm.setString(1, quiz);
                pstm.execute();
                pc.sendPackets(new S_SystemMessage("クイズ変更アカウント：[" + login + "]パスワード：[" + quiz + "]"));
                pc.sendPackets(new S_SystemMessage("そのキャラクターのクイズ変更完了。 （未接続中）"));
            }
            return true;
        } catch (Exception e) {
            System.out.println("to_Change_Passwd() Error : " + e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(statement);
            SQLUtil.close(con);
        }
        return false;
    }

    private static int delItemlist[] = { 307, 308, 309, 310, 311, 312, 313, 314, 21095, 30146, 30147, 30150 };

    public synchronized static void deleteArnoldEvent() {
        try {
            if (delItemlist.length <= 0)
                return;

            for (L1PcInstance tempPc : L1World.getInstance().getAllPlayers()) {
                if (tempPc == null)
                    continue;
                for (int i = 0; i < delItemlist.length; i++) {
                    L1ItemInstance[] item = tempPc.getInventory().findItemsId(delItemlist[i]);
                    if (item != null && item.length > 0) {
                        for (int o = 0; o < item.length; o++) {
                            tempPc.getInventory().removeItem(item[o]);
                        }
                    }
                    try {
                        PrivateWarehouse pw = WarehouseManager.getInstance()
                                .getPrivateWarehouse(tempPc.getAccountName());
                        L1ItemInstance[] item2 = pw.findItemsId(delItemlist[i]);
                        if (item2 != null && item2.length > 0) {
                            for (int o = 0; o < item2.length; o++) {
                                pw.removeItem(item2[o]);
                            }
                        }
                    } catch (Exception e) {
                    }
                    try {
                        if (tempPc.getClanid() > 0) {
                            ClanWarehouse cw = WarehouseManager.getInstance().getClanWarehouse(tempPc.getClanname());
                            L1ItemInstance[] item3 = cw.findItemsId(delItemlist[i]);
                            if (item3 != null && item3.length > 0) {
                                for (int o = 0; o < item3.length; o++) {
                                    cw.removeItem(item3[o]);
                                }
                            }
                        }
                    } catch (Exception e) {
                    }
                    try {
                        if (tempPc.getPetList().size() > 0) {
                            for (L1NpcInstance npc : tempPc.getPetList().values()) {
                                L1ItemInstance[] pitem = npc.getInventory().findItemsId(delItemlist[i]);
                                if (pitem != null && pitem.length > 0) {
                                    for (int o = 0; o < pitem.length; o++) {
                                        npc.getInventory().removeItem(pitem[o]);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            }
            try {
                for (L1Object obj : L1World.getInstance().getAllItem()) {
                    if (!(obj instanceof L1ItemInstance))
                        continue;
                    L1ItemInstance temp_item = (L1ItemInstance) obj;
                    if (temp_item.getItemOwner() == null) {
                        if (temp_item.getX() == 0 && temp_item.getY() == 0)
                            continue;
                    }
                    for (int ii = 0; ii < delItemlist.length; ii++) {
                        if (delItemlist[ii] == temp_item.getItemId()) {
                            L1Inventory groundInventory = L1World.getInstance().getInventory(temp_item.getX(),
                                    temp_item.getY(), temp_item.getMapId());
                            groundInventory.removeItem(temp_item);
                            break;
                        }
                    }

                }
            } catch (Exception e) {
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < delItemlist.length; i++) {
                sb.append(+delItemlist[i]);
                if (i < delItemlist.length - 1) {
                    sb.append(",");
                }
            }
            Delete(sb.toString());

	    /*
         * for(int i = 0; i < delItemlist.length; i++){
	     * Delete(delItemlist[i]); wareDelete(delItemlist[i]);
	     * ClanwareDelete(delItemlist[i]); }
	     */
        } catch (Exception e) {
        }
    }

    private static void Delete(String id_name) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("delete FROM _cha_inv_items WHERE item_id IN (" + id_name + ")");
            pstm.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SQLUtil.close(pstm);
        }
        try {
            pstm = con.prepareStatement("delete FROM character_warehouse WHERE item_id in (" + id_name + ")");
            pstm.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SQLUtil.close(pstm);
        }
        try {
            pstm = con.prepareStatement("delete FROM clan_warehouse WHERE item_id in (" + id_name + ")");
            pstm.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

}
