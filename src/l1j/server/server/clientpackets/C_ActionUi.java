package l1j.server.server.clientpackets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.GameSystem.AttendanceController;
import l1j.server.server.ActionCodes;
import l1j.server.server.GameClient;
import l1j.server.server.Controller.WarTimeController;
import l1j.server.server.datatables.ClanTable;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.NpcActionTable;
import l1j.server.server.datatables.RankTable;
import l1j.server.server.datatables.WeekQuestTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1AccountAttendance;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1ClanJoin;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Party;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1Rank;
import l1j.server.server.model.L1Spawn;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1War;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.npc.L1NpcHtml;
import l1j.server.server.model.npc.action.L1NpcAction;
import l1j.server.server.serverpackets.S_ACTION_UI;
import l1j.server.server.serverpackets.S_ACTION_UI2;
import l1j.server.server.serverpackets.S_ChangeShape;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_DoActionShop;
import l1j.server.server.serverpackets.S_MatizCloudia;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_NewCreateItem;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_Ranking2;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_TamWindow;
import l1j.server.server.serverpackets.S_WeekQuest;
import l1j.server.server.templates.L1PrivateShopBuyList;
import l1j.server.server.templates.L1PrivateShopSellList;
import l1j.server.server.utils.SQLUtil;

public class C_ActionUi extends ClientBasePacket {

    Random _Random = new Random(System.nanoTime());

    private static final String C_ACTION_UI = "[C] C_ActionUi";
    private static final int CRAFT_ITEM = 0x36;
    private static final int CRAFT_ITEMLIST = 0x38; // 0x38;
    private static final int CRAFT_OK = 0x3a; // 最初のタイプ
    private static final int ACCOUNT_TAM = 0x01cc;// タムチャン
    private static final int ACCOUNT_TAM_CANCEL = 0x01e0;// 乗車キャンセル
    private static final int ACCOUNT_TAM_UPDATE = 0x013d;// 乗車
    private static final int ACTION = 0x013F;
    private static final int SKY_GARDEN = 0x84;
    /*
     * private static final int血盟加入= 0x0142; private static final int
     * 血盟加入申請受信設定= 0x0146; private static final int 血盟募集セッティング = 0x014C;
     */
    private static final int WAIT_REGISTRATION = 0x44;
    private static final int SIEGE_RELATION = 0x45;
    private static final int PORTAL_SETTING = 0x0152;
    public static final int LANK_UI = 135;
    public static final int ATTEND = 0x222;
    private static final int WEEK_QUEST_COMPENSATION = 811;
    private static final int WEEK_QUEST_TEL = 815;
    private static final int PERSONAL_STORE = 817;
    private static final int ALARM = 143;
    private static final int CLAUDIA = 524;

    public C_ActionUi(byte abyte0[], GameClient client) {
        super(abyte0);
        int type = readH();
        L1PcInstance pc = client.getActiveChar();
        if (pc == null || pc.isGhost())
            return;
        int objectId = 0, itemtype = 0, itemcount = 0;
        L1Object obj;
        String s = null;
        L1NpcInstance npc;
        L1NpcAction action;
        // System.out.println("アクション > " + type);
        switch (type) {
        case CLAUDIA:
            // 0000: f1 0c 02 03 00 08 80 02 4a 0e b9 36
            // 0000: f1 0c 02 03 00 08 b4 02 82 ae 02 78
            int step = readC();
            if (step == 3 && pc.cL == 0) {
                pc.sendPackets(new S_MatizCloudia(2));
                pc.sendPackets(new S_MatizCloudia(3));
                pc.sendPackets(new S_MatizCloudia(1, 0));
                pc.getInventory().storeItem(3000162, 1);
                pc.getInventory().storeItem(3000163, 1);
                if (pc.getLevel() < 8) {
                    pc.setExp(ExpTable.getExpByLevel(8));
                }
            } else {
                pc.sendPackets(new S_MatizCloudia(4));
                if (pc.getLevel() < 10) {
                    pc.setExp(ExpTable.getExpByLevel(10));
                }
            }
            break;
        case ALARM:
            readH();
            readC();
            int num = readC(); // エルジャベ1、サンドワーム2、騎士団3等S_MatizAlarmで追加されたcase通り出てきます。
            readC();
            switch (num) {
            case 1:// エルジャベ
                new L1Teleport().teleport(pc, 32899, 33244, (short) 4, pc.getMoveState().getHeading(), true);
                break;
            case 2: // サンドワーム
                new L1Teleport().teleport(pc, 32899, 33244, (short) 4, pc.getMoveState().getHeading(), true);
                break;
            case 3:
                break;

            }
            break;
        case PERSONAL_STORE:
            L1ItemInstance sellitem = null;
            L1ItemInstance buyitem = null;
            ArrayList<L1PrivateShopSellList> sellList = pc.getSellList();
            ArrayList<L1PrivateShopBuyList> buyList = pc.getBuyList();
            boolean tradable = true;
            int sellTotalCount = 0;
            int buyTotalCount = 0;
            readC();
            readC();
            readC();
            int start = readC();
            if (start == 0) {
                boolean next = true;
                while (next) {
                    int subtype = readC();
                    if (subtype == 0x22) {
                        next = false;
                        continue;
                    }
                    if (subtype == 0x12) {
                        sellTotalCount++;
                        int totallen = readC();
                        int len = readC();
                        int sellObjectId = readK(len - 3);
                        readC();
                        int pricelen = totallen - (len + 1);
                        int sellPrice = readK(pricelen);
                        readC();
                        int sellCount = readC();

                        L1ItemInstance checkItem = pc.getInventory().getItem(sellObjectId);
                        if (checkItem == null) {
                            continue;
                        }
                        if (sellObjectId != checkItem.getId()) {
                            tradable = false;
                            pc.sendPackets(new S_SystemMessage("異常アイテムです。再試行してください。"), true);
                        }

                        if ((!checkItem.isStackable()) && (sellCount != 1)) {
                            tradable = false;
                            pc.sendPackets(new S_SystemMessage("異常アイテムです。再試行してください。"), true);
                        }

                        if (sellCount > checkItem.getCount()) {
                            sellCount = checkItem.getCount();
                        }
                        if ((checkItem.getCount() < sellCount) || (checkItem.getCount() <= 0) || (sellCount <= 0)) {
                            tradable = false;
                            pc.sendPackets(new S_SystemMessage("異常アイテムです。再試行してください。"), true);
                        }

                        if (checkItem.getBless() >= 128) {
                            tradable = false;
                            pc.sendPackets(new S_ServerMessage(210, checkItem.getItem().getName()));
                        }

                        if (checkItem.getEndTime() != null) {
                            pc.sendPackets(new S_SystemMessage("時間制アイテムは商店登録ができません。"), true);
                            return;
                        }
                        L1DollInstance doll = null;
                        for (Object dollObject : pc.getDollList()) {
                            if (dollObject instanceof L1DollInstance) {
                                doll = (L1DollInstance) dollObject;
                                if (checkItem.getId() == doll.getItemObjId()) {

                                    tradable = false;
                                    pc.sendPackets(new S_SystemMessage("召喚中の人形は、お店に上げることができません。"), true);
                                }
                            }
                        }

                        if (!checkItem.getItem().isTradable()) {
                            tradable = false;
                            pc.sendPackets(new S_ServerMessage(166, checkItem.getItem().getName(), "取引は不可能です。"), true);
                        }
                        for (Object petObject : pc.getPetList().values().toArray()) {
                            if (petObject instanceof L1PetInstance) {
                                L1PetInstance pet = (L1PetInstance) petObject;
                                if (checkItem.getId() == pet.getItemObjId()) {
                                    tradable = false;
                                    pc.sendPackets(new S_ServerMessage(166, checkItem.getItem().getName(), "取引は不可能です。"),
                                            true);
                                    break;
                                }
                            }
                        }
                        L1PrivateShopSellList pssl = new L1PrivateShopSellList();
                        pssl.setItemObjectId(sellObjectId);
                        pssl.setItemId(checkItem.getItemId());
                        pssl.setSellPrice(sellPrice);
                        pssl.setSellTotalCount(sellCount);
                        pssl.setUserName(pc.getName());
                        sellList.add(pssl);

                    } else if (subtype == 0x1a) {
                        buyTotalCount++;
                        int totallen = readC();
                        int len = readC();
                        int buyObjectId = readK(len - 3);
                        readC();
                        int pricelen = totallen - (len + 1);
                        int buyPrice = readK(pricelen);
                        readC();
                        int buyCount = readC();

                        L1ItemInstance checkItem = pc.getInventory().getItem(buyObjectId);
                        if (checkItem == null) {
                            continue;
                        }
                        if (buyObjectId != checkItem.getId()) {
                            tradable = false;
                            pc.sendPackets(new S_SystemMessage("異常アイテムです。再試行してください。"), true);
                        }

                        if ((!checkItem.isStackable()) && (buyCount != 1)) {
                            tradable = false;
                            pc.sendPackets(new S_SystemMessage("異常アイテムです。再試行してください。"), true);
                        }

                        if (buyCount > checkItem.getCount()) {
                            buyCount = checkItem.getCount();
                        }
                        if ((checkItem.getCount() < buyCount) || (checkItem.getCount() <= 0) || (buyCount <= 0)) {
                            tradable = false;
                            pc.sendPackets(new S_SystemMessage("異常アイテムです。再試行してください。"), true);
                        }

                        if (checkItem.getBless() >= 128) {
                            tradable = false;
                            pc.sendPackets(new S_ServerMessage(210, checkItem.getItem().getName()));
                        }

                        if (checkItem.getEndTime() != null) {
                            pc.sendPackets(new S_SystemMessage("時間制アイテムは商店登録ができません。"), true);
                            return;
                        }
                        L1DollInstance doll = null;
                        for (Object dollObject : pc.getDollList()) {
                            if (dollObject instanceof L1DollInstance) {
                                doll = (L1DollInstance) dollObject;
                                if (checkItem.getId() == doll.getItemObjId()) {

                                    tradable = false;
                                    pc.sendPackets(new S_SystemMessage("召喚中の人形は、お店に上げることができません。"), true);
                                }
                            }
                        }

                        if (!checkItem.getItem().isTradable()) {
                            tradable = false;
                            pc.sendPackets(new S_ServerMessage(166, checkItem.getItem().getName(), "取引は不可能です。"), true);
                        }
                        for (Object petObject : pc.getPetList().values().toArray()) {
                            if (petObject instanceof L1PetInstance) {
                                L1PetInstance pet = (L1PetInstance) petObject;
                                if (checkItem.getId() == pet.getItemObjId()) {
                                    tradable = false;
                                    pc.sendPackets(new S_ServerMessage(166, checkItem.getItem().getName(), "取引は不可能です。"),
                                            true);
                                    break;
                                }
                            }
                        }

                        L1PrivateShopBuyList psbl = new L1PrivateShopBuyList();
                        psbl.setItemObjectId(buyObjectId);
                        psbl.setItemId(checkItem.getItemId());
                        psbl.setBuyPrice(buyPrice);
                        psbl.setBuyTotalCount(buyCount);
                        psbl.setUserName(pc.getName());
                        buyList.add(psbl);
                    }
                }
                int len = readC();

                byte[] chat = readByte(len);
                readC();
                int polylen = readC();

                if ((sellTotalCount == 0) && (buyTotalCount == 0)) {
                    pc.sendPackets(new S_ServerMessage(908), true);
                    pc.setPrivateShop(false);
                    pc.sendPackets(new S_DoActionGFX(pc.getId(), 3), true);
                    Broadcaster.broadcastPacket(pc, new S_DoActionGFX(pc.getId(), 3), true);
                    return;
                }
                if (!tradable) {
                    sellList.clear();
                    buyList.clear();
                    pc.setPrivateShop(false);
                    pc.sendPackets(new S_DoActionGFX(pc.getId(), 3), true);
                    Broadcaster.broadcastPacket(pc, new S_DoActionGFX(pc.getId(), 3), true);
                    return;
                }
                pc.getNetConnection().getAccount().updateShopOpenCount();
                pc.sendPackets(new S_PacketBox(198, pc.getNetConnection().getAccount().Shop_open_count), true);

                pc.setShopChat(chat);
                pc.setPrivateShop(true);

                pc.sendPackets(new S_DoActionShop(pc.getId(), 70, chat), true);
                Broadcaster.broadcastPacket(pc, new S_DoActionShop(pc.getId(), 70, chat), true);
                try {
                    for (L1PrivateShopSellList pss : pc.getSellList()) {
                        int sellp = pss.getSellPrice();
                        int sellc = pss.getSellTotalCount();
                        sellitem = pc.getInventory().getItem(pss.getItemObjectId());
                        if (sellitem == null)
                            continue;
                        pc.SaveShop(pc, sellitem, sellp, sellc, 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    for (L1PrivateShopBuyList psb : pc.getBuyList()) {
                        int buyp = psb.getBuyPrice();
                        int buyc = psb.getBuyTotalCount();
                        buyitem = pc.getInventory().getItem(psb.getItemObjectId());
                        if (buyitem == null)
                            continue;
                        pc.SaveShop(pc, buyitem, buyp, buyc, 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    String polyName = readS2(polylen);
                    int polyId = 0;
                    if (polyName.equalsIgnoreCase("tradezone1"))
                        polyId = 11480;
                    else if (polyName.equalsIgnoreCase("tradezone2"))
                        polyId = 11486;
                    else if (polyName.equalsIgnoreCase("tradezone3"))
                        polyId = 10047;
                    else if (polyName.equalsIgnoreCase("tradezone4"))
                        polyId = 11481;
                    else if (polyName.equalsIgnoreCase("tradezone5"))
                        polyId = 11486;
                    else if (polyName.equalsIgnoreCase("tradezone6"))
                        polyId = 10069;
                    else if (polyName.equalsIgnoreCase("tradezone7"))
                        polyId = 10034;
                    else if (polyName.equalsIgnoreCase("tradezone8")) {
                        polyId = 10047;
                    }
                    pc.shopPoly = polyId;
                    if (polyId != 0) {
                        pc.killSkillEffectTimer(67);
                        L1PolyMorph.undoPoly(pc);
                        L1ItemInstance weapon = pc.getWeapon();
                        if (weapon != null)
                            pc.getInventory().setEquipped(weapon, false, false, false, false);
                        pc.setTempCharGfx(polyId);
                        pc.sendPackets(new S_ChangeShape(pc.getId(), polyId, pc.getCurrentWeapon()));
                        if ((!pc.isGmInvis()) && (!pc.isInvisble())) {
                            Broadcaster.broadcastPacket(pc, new S_ChangeShape(pc.getId(), polyId));
                        }
                        S_CharVisualUpdate charVisual = new S_CharVisualUpdate(pc, 0x46);
                        pc.sendPackets(charVisual);
                        Broadcaster.broadcastPacket(pc, charVisual);
                    }
                } catch (Exception e) {
                    pc.deleteStoreItem(pc.getId());
                    sellList.clear();
                    buyList.clear();
                    pc.setPrivateShop(false);
                    pc.sendPackets(new S_DoActionGFX(pc.getId(), 3), true);
                    Broadcaster.broadcastPacket(pc, new S_DoActionGFX(pc.getId(), 3), true);

                    clear();

                    return;
                }
            } else if (start == 1) {
                sellList.clear();
                buyList.clear();
                pc.setPrivateShop(false);
                pc.shopPoly = 0;
                pc.sendPackets(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Idle));
                pc.broadcastPacket(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Idle));
                L1PolyMorph.undoPolyPrivateShop(pc);
            }

            break;
        case WEEK_QUEST_TEL:
            readH(); // 2byte
            readC(); // 1byte
            int line_index = readC(); // 1byte
            readC(); // 1byte
            int index = readC();
            int queue_index = line_index * 3 + index;
            int pcLevel = pc.getLevel();

            int npc_id;
            switch (pc.getWeekType()) {
            case 1:
                npc_id = WeekQuestTable.getInstance().NpcidList.get(queue_index);
                break;
            case 2:
                npc_id = WeekQuestTable.getInstance().NpcidList2.get(queue_index);
                break;
            case 3:
                npc_id = WeekQuestTable.getInstance().NpcidList3.get(queue_index);
                break;
            default:
                npc_id = 0;
                break;
            }

            if (!pc.getInventory().consumeItem(140100, 1)) {
                S_SystemMessage sm = new S_SystemMessage("\\aG 祝福のテレポートスクロールが不足します。");
                pc.sendPackets(sm, true);
                return;
            }

            L1Spawn spawndata = WeekQuestTable.getInstance().SpawnData.get(npc_id);
            if (spawndata != null) {

                new L1Teleport().teleport(pc, spawndata.getLocX(), spawndata.getLocY(), spawndata.getMapId(),
                        pc.getHeading(), true);
                S_SystemMessage sm = new S_SystemMessage("\\aGしばらくして週間クエストモンスターの地域に移動します。");

                pc.sendPackets(sm, true);
            } else {
                System.out.println("図鑑クエストモンスター：" + npc_id + "のテレポート位置が見つかりません。");

                pc.sendPackets(new S_WeekQuest(566));
            }

            break;
        case WEEK_QUEST_COMPENSATION:
            readH();
            readC();
            int line = readC();
            // System.out.println("LINE : "+line);
            switch (line) {
            case 0:
                pc.setReward(0, true);
                break;
            case 1:
                pc.setReward(1, true);
                break;
            case 2:
                pc.setReward(2, true);
                break;

            }
            pc.getInventory().storeItem(500001, 1); // ギュンターの引張
            pc.sendPackets(new S_WeekQuest(pc));
            break;
        case ATTEND:
            readC();
            readH();
            int daycheck = readC();
            readC();
            int pcbang = readC();
            if (pc != null) {
                L1AccountAttendance acc = AttendanceController.findacc(pc.getAccountName());
                if (acc != null) {

                    AttendanceController.clear(acc, pc, daycheck, pcbang);
                }
            }

            break;
        case WAIT_REGISTRATION: {
            pc.sendPackets(new S_ACTION_UI2(S_ACTION_UI2.CLAN_JOIN_WAIT, true));
        }
            break;
        case LANK_UI:
            int subtype = readH();
            readC();
            int classType = readC();
            if (subtype == 4) {
                LinkedList<L1Rank> list = RankTable.getInstance().getMapByClass(classType);
                int size = list.size();
                if (size > 100) {
                    List<L1Rank> list1 = list.subList(0, 100);
                    if (size > 200)
                        size = 200;
                    List<L1Rank> list2 = list.subList(100, size);
                    pc.sendPackets(new S_Ranking2(list1, classType, 2, 1));
                    pc.sendPackets(new S_Ranking2(list2, classType, 2, 2));
                } else {
                    pc.sendPackets(new S_Ranking2(list, classType, 1, 1));
                }
            } else {
                pc.sendPackets(new S_Ranking2(S_Ranking2.SHOW_RANK_UI));
            }
            break;
        /*
         * case 血盟加入申請受信設定： if (pc.getClanid() == 0 || (!pc.isCrown() &&
         * pc.getClanRank() != L1Clan.守護）） return; readC(); readH(); int setting
         * = readC(); readC(); int setting2 = readC(); if (setting2 == 2) {
         * pc.sendPackets(new S_SystemMessage（「現在のパスワード登録のタイプに設定することができません。 "））;
         * setting2 = 1; }
         *
         * pc.getClan().setJoinSetting(setting);
         * pc.getClan().setJoinType(setting2); pc.sendPackets(new
         * S_ACTION_UI2(S_ACTION_UI2.CLAN_JOIN_SETTING, setting, setting2));
         * ClanTable.getInstance().updateClan(pc.getClan()); pc.sendPackets(new
         * S_ServerMessage(3980)); break;
         */
        /*
         * case 血盟加入： try { readC(); readH(); int length = readC(); byte[] BYTE2
         * = readByte();
         *
         * if (pc.isCrown()) { pc.sendPackets(new
         * S_ACTION_UI(S_ACTION_UI.CLAN_JOIN_MESSAGE, 13)); return; }
         *
         * if (pc.getClanid() != 0) { pc.sendPackets(new
         * S_ACTION_UI(S_ACTION_UI.CLAN_JOIN_MESSAGE, 9)); return; }
         *
         * String clanname = new String(BYTE2, 0, length, "MS932");
         *
         * L1Clan clan = L1World.getInstance().getClan(clanname); if (clan ==
         * null) { pc.sendPackets(new S_SystemMessage（「存在しない血盟です。 "））;
         * pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.CLAN_JOIN_MESSAGE, 4));
         * return; }
         *
         * if (clan.getJoinSetting() == 0) { pc.sendPackets(new
         * S_ACTION_UI(S_ACTION_UI.CLAN_JOIN_MESSAGE, 8)); return; }
         *
         *
         * for(L1PcInstance cra : clan.getOnlineClanMember()){
         * if(cra.getClanRank() >= 9){ if (clan.getJoinSetting() == 0) {
         * pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.CLAN_JOIN_MESSAGE, 8));
         * return;
         *
         * } else if (clan.getJoinType() == 0) { pc.sendPackets(new
         * S_ACTION_UI(S_ACTION_UI.CLAN_JOIN_MESSAGE, 0)); return; } else {
         * cra.setTempID(pc.getId()); //相手のオブジェクトIDを保存しておく S_Message_YN myn =
         * new S_Message_YN(97, pc.getName()); cra.sendPackets(myn, true);
         * pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.CLAN_JOIN_MESSAGE, 1)); }
         * } else { pc.sendPackets(new
         * S_SystemMessage（「血盟の君主や守護階級血盟員が接続されている必要があり使用可能です。 "））;
         * pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.CLAN_JOIN_MESSAGE, 11));
         * return; } } } catch (Exception e) { } finally { clear(); } break;
         */
        /*
         * case血盟募集セッティング： if (pc.getClanid() == 0) return; pc.sendPackets(new
         * S_ACTION_UI2(S_ACTION_UI2.CLAN_JOIN_SETTING,
         * pc.getClan().getJoinSetting(), pc.getClan().getJoinType())); break;
         */
        case SKY_GARDEN:
            if (!pc.PCRoom_Buff) {
                pc.sendPackets(new S_SystemMessage("PC部屋利用権を使用中のみ使用可能なアクションです。"));
                return;
            }
            if (pc.getMapId() == 99 || pc.getMapId() == 6202) {
                pc.sendPackets(new S_SystemMessage("周囲の魔力によって瞬間移動を使用することができません。"));
                return;
            }

            if (!pc.getMap().isTeleportable()) {
                pc.sendPackets(new S_SystemMessage("周囲の魔力によって瞬間移動を使用することができません。"));
                return;
            }

            int ran = _Random.nextInt(4);

            if (ran == 0) {
                new L1Teleport().teleport(pc, 32779, 32825, (short) 622, pc.getHeading(), true);
            } else if (ran == 1) {
                new L1Teleport().teleport(pc, 32761, 32819, (short) 622, pc.getHeading(), true);
            } else if (ran == 2) {
                new L1Teleport().teleport(pc, 32756, 32837, (short) 622, pc.getHeading(), true);
            } else {
                new L1Teleport().teleport(pc, 32770, 32839, (short) 622, pc.getHeading(), true);
            }
            break;
        case CRAFT_ITEM: // 制作システム
            pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.CRAFT_ITEM));
            break;
        case CRAFT_ITEMLIST:
            readH(); // size;
            readC(); // dummy
            objectId = read4(read_size());
            obj = L1World.getInstance().findObject(objectId);
            if (obj instanceof L1NpcInstance) {
                npc = (L1NpcInstance) obj;
                pc.sendPackets(new S_ACTION_UI(pc, npc));
            }
            break;
        case CRAFT_OK:
        case 92:
            readH(); // size;
            readC(); // dummy
            objectId = read4(read_size());
            readC(); // dummy
            itemtype = read4(read_size());
            readC(); // dummy
            itemcount = read4(read_size());
            if (itemcount < 1 || itemcount > 99) {
                return;
            }
            s = Integer.toString(itemtype);
            obj = L1World.getInstance().findObject(objectId);
            if (obj instanceof L1NpcInstance) {
                npc = (L1NpcInstance) obj;
                if (itemtype == 116) {
                    if ((pc.getInventory().checkEnchantItem(5, 8, 1) // [+7]馬力の
                            // 短剣
                            || pc.getInventory().checkEnchantItem(6, 8, 1)
                            || pc.getInventory().checkEnchantItem(32, 8, 1)
                            || pc.getInventory().checkEnchantItem(37, 8, 1)
                            || pc.getInventory().checkEnchantItem(41, 8, 1)
                            || pc.getInventory().checkEnchantItem(42, 8, 1)
                            || pc.getInventory().checkEnchantItem(52, 8, 1)
                            || pc.getInventory().checkEnchantItem(64, 8, 1)
                            || pc.getInventory().checkEnchantItem(99, 8, 1)
                            || pc.getInventory().checkEnchantItem(104, 8, 1)
                            || pc.getInventory().checkEnchantItem(125, 8, 1)
                            || pc.getInventory().checkEnchantItem(129, 8, 1)
                            || pc.getInventory().checkEnchantItem(131, 8, 1)
                            || pc.getInventory().checkEnchantItem(145, 8, 1)
                            || pc.getInventory().checkEnchantItem(148, 8, 1)
                            || pc.getInventory().checkEnchantItem(180, 8, 1)
                            || pc.getInventory().checkEnchantItem(181, 8, 1))
                            && pc.getInventory().checkItem(40308, 5000000)) {
                        if (pc.getInventory().consumeEnchantItem(5, 8, 1)
                                || pc.getInventory().consumeEnchantItem(6, 8, 1)
                                || pc.getInventory().consumeEnchantItem(32, 8, 1)
                                || pc.getInventory().consumeEnchantItem(37, 8, 1)
                                || pc.getInventory().consumeEnchantItem(41, 8, 1)
                                || pc.getInventory().consumeEnchantItem(42, 8, 1)
                                || pc.getInventory().consumeEnchantItem(52, 8, 1)
                                || pc.getInventory().consumeEnchantItem(64, 8, 1)
                                || pc.getInventory().consumeEnchantItem(99, 8, 1)
                                || pc.getInventory().consumeEnchantItem(104, 8, 1)
                                || pc.getInventory().consumeEnchantItem(125, 8, 1)
                                || pc.getInventory().consumeEnchantItem(129, 8, 1)
                                || pc.getInventory().consumeEnchantItem(131, 8, 1)
                                || pc.getInventory().consumeEnchantItem(145, 8, 1)
                                || pc.getInventory().consumeEnchantItem(148, 8, 1)
                                || pc.getInventory().consumeEnchantItem(180, 8, 1)
                                || pc.getInventory().consumeEnchantItem(181, 8, 1)) {
                            ;
                        }
                        pc.getInventory().consumeItem(40308, 5000000);
                        supplyEnchant(pc, 602, 1, 7);
                    }
                } else if (itemtype == 123) {
                    if ((pc.getInventory().checkEnchantItem(5, 9, 1) // [+8]馬力の
                            // 短剣
                            || pc.getInventory().checkEnchantItem(6, 9, 1)
                            || pc.getInventory().checkEnchantItem(32, 9, 1)
                            || pc.getInventory().checkEnchantItem(37, 9, 1)
                            || pc.getInventory().checkEnchantItem(41, 9, 1)
                            || pc.getInventory().checkEnchantItem(42, 9, 1)
                            || pc.getInventory().checkEnchantItem(52, 9, 1)
                            || pc.getInventory().checkEnchantItem(64, 9, 1)
                            || pc.getInventory().checkEnchantItem(99, 9, 1)
                            || pc.getInventory().checkEnchantItem(104, 9, 1)
                            || pc.getInventory().checkEnchantItem(125, 9, 1)
                            || pc.getInventory().checkEnchantItem(129, 9, 1)
                            || pc.getInventory().checkEnchantItem(131, 9, 1)
                            || pc.getInventory().checkEnchantItem(145, 9, 1)
                            || pc.getInventory().checkEnchantItem(148, 9, 1)
                            || pc.getInventory().checkEnchantItem(180, 9, 1)
                            || pc.getInventory().checkEnchantItem(181, 9, 1))
                            && pc.getInventory().checkItem(40308, 10000000)) {
                        if (pc.getInventory().consumeEnchantItem(5, 9, 1)
                                || pc.getInventory().consumeEnchantItem(6, 9, 1)
                                || pc.getInventory().consumeEnchantItem(32, 9, 1)
                                || pc.getInventory().consumeEnchantItem(37, 9, 1)
                                || pc.getInventory().consumeEnchantItem(41, 9, 1)
                                || pc.getInventory().consumeEnchantItem(42, 9, 1)
                                || pc.getInventory().consumeEnchantItem(52, 9, 1)
                                || pc.getInventory().consumeEnchantItem(64, 9, 1)
                                || pc.getInventory().consumeEnchantItem(99, 9, 1)
                                || pc.getInventory().consumeEnchantItem(104, 9, 1)
                                || pc.getInventory().consumeEnchantItem(125, 9, 1)
                                || pc.getInventory().consumeEnchantItem(129, 9, 1)
                                || pc.getInventory().consumeEnchantItem(131, 9, 1)
                                || pc.getInventory().consumeEnchantItem(145, 9, 1)
                                || pc.getInventory().consumeEnchantItem(148, 9, 1)
                                || pc.getInventory().consumeEnchantItem(180, 9, 1)
                                || pc.getInventory().consumeEnchantItem(181, 9, 1)) {
                            ;
                        }
                        pc.getInventory().consumeItem(40308, 10000000);
                        supplyEnchant(pc, 602, 1, 8);
                    }

                    /** エリクサー製作アイテム確率 **/
                } else if (itemtype == 1043) {// エリクサー[STR]
                    Random random = new Random();
                    if ((pc.getInventory().checkItem(820018, 35) && pc.getInventory().checkItem(410061, 2))) {// 材料アイテム
                        if (pc.getInventory().consumeItem(820018, 35) && pc.getInventory().consumeItem(410061, 2)) {// アイテム回収
                            ;
                        }
                        if (random.nextInt(100) < 10) { // 20％の確率で成功
                            supplyEnchant(pc, 40033, 1, 0);
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に成功して \\aG「エリクサー[STR]「\\aAを獲得"));
                        } else { // 残りの確率が失敗
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に失敗しました。"));
                        }
                    } else {
                        pc.sendPackets(new S_SystemMessage("材料が不足して"));
                    }
                } else if (itemtype == 1044) {// エリクサー [STR]
                    Random random = new Random();
                    if ((pc.getInventory().checkItem(820018, 35) && pc.getInventory().checkItem(410061, 2))) {// 材料アイテム
                        if (pc.getInventory().consumeItem(820018, 35) && pc.getInventory().consumeItem(410061, 2)) {// アイテム回収
                            ;
                        }
                        if (random.nextInt(100) < 30) { // 20％の確率で成功
                            supplyEnchant(pc, 40034, 1, 0);
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に成功して \\aG「エリクサー[STR]「\\aAを獲得"));
                        } else { // 残りの確率が失敗
                            // pc.getInventory().consumeEnchantItem(40308, 0,
                            // 1);
                            // pc.getInventory().consumeEnchantItem(40308, 0,
                            // 1);
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に失敗しました。"));
                        }
                    } else {
                        pc.sendPackets(new S_SystemMessage("材料が不足して"));
                    }
                } else if (itemtype == 1045) {// エリクサー[DEX]
                    Random random = new Random();
                    if ((pc.getInventory().checkItem(820018, 35) && pc.getInventory().checkItem(410061, 2))) {// 材料アイテム
                        if (pc.getInventory().consumeItem(820018, 35) && pc.getInventory().consumeItem(410061, 2)) {// アイテム回収
                            ;
                        }
                        if (random.nextInt(100) < 30) { // 20％の確率で成功
                            supplyEnchant(pc, 40035, 1, 0);
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に成功して \\aG「エリクサー[DEX]」\\aAを獲得"));
                        } else { // 残りの確率が失敗
                            // pc.getInventory().consumeEnchantItem(40308, 0,
                            // 1);
                            // pc.getInventory().consumeEnchantItem(40308, 0,
                            // 1);
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に失敗しました."));
                        }
                    } else {
                        pc.sendPackets(new S_SystemMessage("材料が不足して"));
                    }
                } else if (itemtype == 1046) {// エリクサー[INT]
                    Random random = new Random();
                    if ((pc.getInventory().checkItem(820018, 35) && pc.getInventory().checkItem(410061, 2))) {// 材料アイテム
                        if (pc.getInventory().consumeItem(820018, 35) && pc.getInventory().consumeItem(410061, 2)) {// アイテム回収
                            ;
                        }
                        if (random.nextInt(100) < 30) { // 20％の確率で成功
                            supplyEnchant(pc, 40036, 1, 0);
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に成功して \\aG「エリクサー[INT]」\\aAを獲得"));
                        } else { // 残りの確率が失敗
                            // pc.getInventory().consumeEnchantItem(40308, 0,
                            // 1);
                            // pc.getInventory().consumeEnchantItem(40308, 0,
                            // 1);
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に失敗しました。"));
                        }
                    } else {
                        pc.sendPackets(new S_SystemMessage("材料が不足して"));
                    }
                } else if (itemtype == 1047) {// エリクサー[WIS]
                    Random random = new Random();
                    if ((pc.getInventory().checkItem(820018, 35) && pc.getInventory().checkItem(410061, 2))) {// 材料アイテム
                        if (pc.getInventory().consumeItem(820018, 35) && pc.getInventory().consumeItem(410061, 2)) {// アイテム回収
                            ;
                        }
                        if (random.nextInt(100) < 30) { // 20％の確率で成功
                            supplyEnchant(pc, 40037, 1, 0);
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に成功して \\aG「エリクサー[WIS]」\\aAを獲得"));
                        } else { // 残りの確率が失敗
                            // pc.getInventory().consumeEnchantItem(40308, 0,
                            // 1);
                            // pc.getInventory().consumeEnchantItem(40308, 0,
                            // 1);
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に失敗しました。"));
                        }
                    } else {
                        pc.sendPackets(new S_SystemMessage("材料が不足して"));
                    }
                } else if (itemtype == 1048) {// エリクサー[CHA]
                    Random random = new Random();
                    if ((pc.getInventory().checkItem(820018, 35) && pc.getInventory().checkItem(410061, 2))) {// 材料アイテム
                        if (pc.getInventory().consumeItem(820018, 35) && pc.getInventory().consumeItem(410061, 2)) {// アイテム回収
                            ;
                        }
                        if (random.nextInt(100) < 30) { // 20％の確率で成功
                            supplyEnchant(pc, 40038, 1, 0);
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に成功して \\aG「エリクサー[CHA]」\\aAを獲得"));
                        } else { // 残りの確率が失敗
                            // pc.getInventory().consumeEnchantItem(40308, 0,
                            // 1);
                            // pc.getInventory().consumeEnchantItem(40308, 0,
                            // 1);
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に失敗しました。"));
                        }
                    } else {
                        pc.sendPackets(new S_SystemMessage("材料が不足して"));
                    }

                } else if (itemtype == 2747) {// クリップボード書
                    Random random = new Random();
                    if (pc.getInventory().checkItem(810012, 3)) {// 材料アイテム
                        if (pc.getInventory().consumeItem(810012, 3)) {// アイテム回収
                            ;
                        }
                        if (random.nextInt(100) < 5) { // 20％の確率で成功
                            supplyEnchant(pc, 810013, 1, 0);
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
                            pc.sendPackets(new S_SystemMessage("製作に成功しました。"));
                        } else { // 残りの確率が失敗
                            // pc.getInventory().consumeEnchantItem(40308, 0,
                            // 1);
                            // pc.getInventory().consumeEnchantItem(40308, 0,
                            // 1);
                            pc.sendPackets(new S_SystemMessage("製作に失敗しました。"));
                        }
                    } else {
                        pc.sendPackets(new S_SystemMessage("材料が不足しています。"));
                    }

                    /** アーノルド製作アイテム **/
                } else if (itemtype == 1647) {// 両手剣
                    if ((pc.getInventory().checkEnchantItem(307, 10, 1))) {
                        pc.getInventory().consumeEnchantItem(307, 10, 1);
                        supplyEnchant(pc, 30148, 1, 0);
                    }
                } else if (itemtype == 1648) {// 杖
                    if ((pc.getInventory().checkEnchantItem(308, 10, 1))) {
                        pc.getInventory().consumeEnchantItem(308, 10, 1);
                        supplyEnchant(pc, 30148, 1, 0);
                    }
                } else if (itemtype == 1649) {// 剣
                    if ((pc.getInventory().checkEnchantItem(309, 10, 1))) {
                        pc.getInventory().consumeEnchantItem(309, 10, 1);
                        supplyEnchant(pc, 30148, 1, 0);
                    }
                } else if (itemtype == 1650) {// 弓
                    if ((pc.getInventory().checkEnchantItem(310, 10, 1))) {
                        pc.getInventory().consumeEnchantItem(310, 10, 1);
                        supplyEnchant(pc, 30148, 1, 0);
                    }
                } else if (itemtype == 1651) {// 二刀流
                    if ((pc.getInventory().checkEnchantItem(311, 10, 1))) {
                        pc.getInventory().consumeEnchantItem(311, 10, 1);
                        supplyEnchant(pc, 30148, 1, 0);
                    }
                } else if (itemtype == 1652) {// チェーンソード
                    if ((pc.getInventory().checkEnchantItem(312, 10, 1))) {
                        pc.getInventory().consumeEnchantItem(312, 10, 1);
                        supplyEnchant(pc, 30148, 1, 0);
                    }
                } else if (itemtype == 1653) {// キーリンク
                    if ((pc.getInventory().checkEnchantItem(313, 10, 1))) {
                        pc.getInventory().consumeEnchantItem(313, 10, 1);
                        supplyEnchant(pc, 30148, 1, 0);
                    }
                } else if (itemtype == 1654) {// 斧
                    if ((pc.getInventory().checkEnchantItem(314, 10, 1))) {
                        pc.getInventory().consumeEnchantItem(314, 10, 1);
                        supplyEnchant(pc, 30148, 1, 0);
                    }
                } else if (itemtype == 1655) {// 手袋
                    if ((pc.getInventory().checkEnchantItem(21095, 10, 1))) {
                        pc.getInventory().consumeEnchantItem(21095, 10, 1);
                        supplyEnchant(pc, 30148, 1, 0);
                    }

                    /** タラス製作アイテム **/
                } else if (itemtype == 214) {// 大魔法使いの帽子
                    Random random = new Random();
                    if ((pc.getInventory().checkItem(20040, 1) || pc.getInventory().checkItem(20025, 1)
                            || pc.getInventory().checkItem(20018, 1) || pc.getInventory().checkItem(20029, 1)
                            || pc.getInventory().checkItem(410061, 200)
                            || pc.getInventory().checkItem(41246, 100000))) {// 材料アイテム

                        if (pc.getInventory().consumeItem(20040, 1) && pc.getInventory().consumeItem(20025, 1)
                                && pc.getInventory().consumeItem(20018, 1) && pc.getInventory().consumeItem(20029, 1)
                                && pc.getInventory().consumeItem(410061, 200)
                                && pc.getInventory().consumeItem(41246, 100000)) {// アイテム回収
                            ;
                        }
                        if (random.nextInt(100) < 100) { // 100％の確率で成功
                            supplyEnchant(pc, 202022, 1, 0);
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に成功して\\aG「大魔法使いの帽子」\\aAを獲得"));
                        } else { // 残りの確率が失敗
                            // pc.getInventory().consumeEnchantItem(40308, 0,
                            // 1);
                            // pc.getInventory().consumeEnchantItem(40308, 0,
                            // 1);
                        }
                    } else {
                        pc.sendPackets(new S_SystemMessage("材料が不足して"));
                    }
                } else if (itemtype == 2057) {// グレースアバター
                    Random random = new Random();
                    if ((pc.getInventory().checkItem(3000114, 1))) {// 材料アイテム
                        if (pc.getInventory().consumeItem(3000114, 1)) {// アイテム回収
                            ;
                        }
                        if (random.nextInt(100) < 100) { // 100％の確率で成功
                            supplyEnchant(pc, 3000090, 1, 0);
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に成功して \\aG「グレースアバター」\\aAを獲得"));
                        } else { // 残りの確率が失敗
                            // pc.getInventory().consumeEnchantItem(40308, 0,
                            // 1);
                            // pc.getInventory().consumeEnchantItem(40308, 0,
                            // 1);
                        }
                    } else {
                        pc.sendPackets(new S_SystemMessage("材料が不足して"));
                    }
                } else if (itemtype == 2058) {// アブソリュートブレード
                    Random random = new Random();
                    if ((pc.getInventory().checkItem(3000110, 1))) {// 材料アイテム
                        if (pc.getInventory().consumeItem(3000110, 1)) {// アイテム回収
                            ;
                        }
                        if (random.nextInt(100) < 100) { // 100％の確率で成功
                            supplyEnchant(pc, 3000092, 1, 0);
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に成功して \\aG「アブソリュートブレード」\\aAを獲得"));
                        } else { // 残りの確率が失敗
                            // pc.getInventory().consumeEnchantItem(40308, 0,
                            // 1);
                            // pc.getInventory().consumeEnchantItem(40308, 0,
                            // 1);
                        }
                    } else {
                        pc.sendPackets(new S_SystemMessage("材料が不足して"));
                    }
                } else if (itemtype == 2059) {// ソウルバリア
                    Random random = new Random();
                    if ((pc.getInventory().checkItem(3000116, 1))) {// 材料アイテム
                        if (pc.getInventory().consumeItem(3000116, 1)) {// アイテム回収
                            ;
                        }
                        if (random.nextInt(100) < 100) { // 100％の確率で成功
                            supplyEnchant(pc, 3000091, 1, 0);
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に成功して \\aG「ソウルバリア」\\aAを獲得"));
                        } else { // 残りの確率が失敗
                            // pc.getInventory().consumeEnchantItem(40308, 0,
                            // 1);
                            // pc.getInventory().consumeEnchantItem(40308, 0,
                            // 1);
                        }
                    } else {
                        pc.sendPackets(new S_SystemMessage("材料が不足して"));
                    }
                } else if (itemtype == 2060) {// （デス・ヒル）
                    Random random = new Random();
                    if ((pc.getInventory().checkItem(3000111, 1))) {// 材料アイテム
                        if (pc.getInventory().consumeItem(3000111, 1)) {// アイテム回収
                            ;
                        }
                        if (random.nextInt(100) < 100) { // 100％の確率で成功
                            supplyEnchant(pc, 3000095, 1, 0);
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に成功して\\aG「デス・ヒル」\\aAを獲得"));
                        } else { // 残りの確率が失敗
                            // pc.getInventory().consumeEnchantItem(40308, 0,
                            // 1);
                            // pc.getInventory().consumeEnchantItem(40308, 0,
                            // 1);
                        }
                    } else {
                        pc.sendPackets(new S_SystemMessage("材料が不足して "));
                    }
                } else if (itemtype == 2061) {// アサシン
                    Random random = new Random();
                    if ((pc.getInventory().checkItem(3000117, 1))) {// 材料アイテム
                        if (pc.getInventory().consumeItem(3000117, 1)) {// アイテム回収
                            ;
                        }
                        if (random.nextInt(100) < 100) { // 100％の確率で成功
                            supplyEnchant(pc, 3000089, 1, 0);
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に成功して \\aG「闇精霊の水晶（アサシン）」\\aAを獲得"));
                        } else { // 残りの確率が失敗
                            // pc.getInventory().consumeEnchantItem(40308, 0,
                            // 1);
                            // pc.getInventory().consumeEnchantItem(40308, 0,
                            // 1);
                        }
                    } else {
                        pc.sendPackets(new S_SystemMessage("材料が不足して"));
                    }
                } else if (itemtype == 2062) {// ブレイジング
                    Random random = new Random();
                    if ((pc.getInventory().checkItem(3000117, 1))) {// 材料アイテム
                        if (pc.getInventory().consumeItem(3000117, 1)) {// アイテム回収
                            ;
                        }
                        if (random.nextInt(100) < 100) { // 100％の確率で成功
                            supplyEnchant(pc, 3000097, 1, 0);
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に成功して\\aG「ブレイジングスピリッツ」\\aAを獲得"));
                        } else { // 残りの確率が失敗
                            // pc.getInventory().consumeEnchantItem(40308, 0,
                            // 1);
                            // pc.getInventory().consumeEnchantItem(40308, 0,
                            // 1);
                        }
                    } else {
                        pc.sendPackets(new S_SystemMessage("材料が不足して"));
                    }
                } else if (itemtype == 2064) {// デストロイ
                    Random random = new Random();
                    if ((pc.getInventory().checkItem(3000113, 1))) {// 材料アイテム
                        if (pc.getInventory().consumeItem(3000113, 1)) {// アイテム回収
                            ;
                        }
                        if (random.nextInt(100) < 100) { // 100％の確率で成功
                            supplyEnchant(pc, 3000093, 1, 0);
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に成功して\\aG「容器買っ版（デストロイ）」\\aAを獲得"));
                        } else { // 残り確率失敗
                            // pc.getInventory().consumeEnchantItem(40308, 0,
                            // 1);
                            // pc.getInventory().consumeEnchantItem(40308, 0,
                            // 1);
                        }
                    } else {
                        pc.sendPackets(new S_SystemMessage("材料が不足して"));
                    }
                } else if (itemtype == 2063) {// インパクト
                    Random random = new Random();
                    if ((pc.getInventory().checkItem(3000112, 1))) {// 材料アイテム
                        if (pc.getInventory().consumeItem(3000112, 1)) {// アイテム回収
                            ;
                        }
                        if (random.nextInt(100) < 100) { // 100％の確率で成功
                            supplyEnchant(pc, 3000096, 1, 0);
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に成功して\\aG「記憶の修正（インパクト）」\\aAを獲得"));
                        } else { // 残りの確率が失敗
                            // pc.getInventory().consumeEnchantItem(40308, 0,
                            // 1);
                            // pc.getInventory().consumeEnchantItem(40308, 0,
                            // 1);
                        }
                    } else {
                        pc.sendPackets(new S_SystemMessage("材料が不足して"));
                    }
                } else if (itemtype == 2065) {// タイタンライジング
                    Random random = new Random();
                    if ((pc.getInventory().checkItem(3000115, 1))) {// 材料アイテム
                        if (pc.getInventory().consumeItem(3000115, 1)) {// アイテム回収
                            ;
                        }
                        if (random.nextInt(100) < 100) { // 100％の確率で成功
                            supplyEnchant(pc, 3000094, 1, 0);
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に成功して\\aG「戦士の引張（タイタン：ライジング）」\\aAを獲得"));
                        } else { // 残りの確率が失敗
                            // pc.getInventory().consumeEnchantItem(40308, 0,
                            // 1);
                            // pc.getInventory().consumeEnchantItem(40308, 0,
                            // 1);
                        }
                    } else {
                        pc.sendPackets(new S_SystemMessage("材料が不足して"));
                    }

                    /** レオン製作アイテム確率 **/
                } else if (itemtype == 1763) {// 古代の岩ゲートル
                    Random random = new Random();
                    if ((pc.getInventory().checkItem(3000065, 1) || pc.getInventory().checkItem(3000064, 1))) {// 材料アイテム
                        if (pc.getInventory().consumeItem(3000065, 1) && pc.getInventory().consumeItem(3000064, 1)) {// アイテム回収
                            ;
                        }
                        if (random.nextInt(100) < 20) { // 10％の確率で成功
                            supplyEnchant(pc, 900011, 1, 0);
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に成功して\\aG「古代の岩ゲートル」\\aAを獲得"));
                        } else { // 残りの確率が失敗
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に失敗しました。"));
                        }
                    } else {
                        pc.sendPackets(new S_SystemMessage("材料が不足して"));
                    }
                } else if (itemtype == 1764) {// 古代の岩ブーツ
                    Random random = new Random();
                    if ((pc.getInventory().checkItem(3000065, 1) || pc.getInventory().checkItem(3000064, 1))) {// 材料アイテム
                        if (pc.getInventory().consumeItem(3000065, 1) && pc.getInventory().consumeItem(3000064, 1)) {// アイテム回収
                            ;
                        }
                        if (random.nextInt(100) < 20) { // 10％の確率で成功
                            supplyEnchant(pc, 900012, 1, 0);
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に成功して\\aG「古代の岩ブーツ」\\aAを獲得!"));
                        } else { // 残りの確率が失敗
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に失敗しました。"));
                        }
                    } else {
                        pc.sendPackets(new S_SystemMessage("材料が不足して"));
                    }
                } else if (itemtype == 1765) {// 古代の岩の岬
                    Random random = new Random();
                    if ((pc.getInventory().checkItem(3000065, 1) || pc.getInventory().checkItem(3000064, 1))) {// 材料アイテム
                        if (pc.getInventory().consumeItem(3000065, 1) && pc.getInventory().consumeItem(3000064, 1)) {// アイテム回収
                            ;
                        }
                        if (random.nextInt(100) < 20) { // 10％の確率で成功
                            supplyEnchant(pc, 900013, 1, 0);
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に成功して\\aG「古代の岩の岬」\\aAを獲得!"));
                        } else { // 残りの確率が失敗
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に失敗しました。"));
                        }
                    } else {
                        pc.sendPackets(new S_SystemMessage("材料が不足して"));
                    }
                } else if (itemtype == 1766) {// 古代の岩の手袋
                    Random random = new Random();
                    if ((pc.getInventory().checkItem(3000065, 1) || pc.getInventory().checkItem(3000064, 1))) {// 材料アイテム
                        if (pc.getInventory().consumeItem(3000065, 1) && pc.getInventory().consumeItem(3000064, 1)) {// アイテム回収
                            ;
                        }
                        if (random.nextInt(100) < 20) { // 10%の確率で成功
                            supplyEnchant(pc, 900014, 1, 0);
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に成功して\\aG「古代の岩の手袋」\\aAを獲得！"));
                        } else { // 残りの確率が失敗
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に失敗しました。"));
                        }
                    } else {
                        pc.sendPackets(new S_SystemMessage("材料が不足して"));
                    }
                } else if (itemtype == 1767) {// 古代魔物のゲートル
                    Random random = new Random();
                    if ((pc.getInventory().checkItem(3000065, 1) || pc.getInventory().checkItem(3000075, 1))) {// 材料アイテム
                        if (pc.getInventory().consumeItem(3000065, 1) && pc.getInventory().consumeItem(3000075, 1)) {// アイテム回収
                            ;
                        }
                        if (random.nextInt(100) < 10) { // 10％の確率で成功
                            supplyEnchant(pc, 900015, 1, 0);
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
                            pc.sendPackets(new S_SystemMessage("\\aA注意：あなたは、\\aG「古代魔物のゲートル」\\aAを獲得！"));
                        } else { // 残りの確率が失敗
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に失敗しました。"));
                        }
                    } else {
                        pc.sendPackets(new S_SystemMessage("材料が不足して"));
                    }
                } else if (itemtype == 1768) {// 古代魔物のブーツ
                    Random random = new Random();
                    if ((pc.getInventory().checkItem(3000065, 1) || pc.getInventory().checkItem(3000075, 1))) {// 材料アイテム
                        if (pc.getInventory().consumeItem(3000065, 1) && pc.getInventory().consumeItem(3000075, 1)) {// アイテム回収
                            ;
                        }
                        if (random.nextInt(100) < 10) { // 10％の確率で成功
                            supplyEnchant(pc, 900018, 1, 0);
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
                            pc.sendPackets(new S_SystemMessage("\\aA注意：あなたは、 \\aG「古代魔物のブーツ」\\aAを獲得!"));
                        } else { // 残りの確率が失敗
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に失敗しました。"));
                        }
                    } else {
                        pc.sendPackets(new S_SystemMessage("材料が不足して"));
                    }
                } else if (itemtype == 1769) {// 古代魔物のマント
                    Random random = new Random();
                    if ((pc.getInventory().checkItem(3000065, 1) || pc.getInventory().checkItem(3000075, 1))) {// 材料アイテム
                        if (pc.getInventory().consumeItem(3000065, 1) && pc.getInventory().consumeItem(3000075, 1)) {// アイテム回収
                            ;
                        }
                        if (random.nextInt(100) < 10) { // 10％の確率で成功
                            supplyEnchant(pc, 900017, 1, 0);
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
                            pc.sendPackets(new S_SystemMessage("\\aA注意：あなたは、 \\aG「古代魔物のマント」\\aAを獲得!"));
                        } else { // 残りの確率が失敗
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に失敗しました。"));
                        }
                    } else {
                        pc.sendPackets(new S_SystemMessage("材料が不足して"));
                    }
                } else if (itemtype == 1770) {// 古代魔物の手袋
                    Random random = new Random();
                    if ((pc.getInventory().checkItem(3000065, 1) || pc.getInventory().checkItem(3000075, 1))) {// 材料アイテム
                        if (pc.getInventory().consumeItem(3000065, 1) && pc.getInventory().consumeItem(3000075, 1)) {// アイテム回収
                            ;
                        }
                        if (random.nextInt(100) < 10) { // 10％の確率で成功
                            supplyEnchant(pc, 900016, 1, 0);
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
                            pc.sendPackets(new S_SystemMessage("\\aA注意：あなたは、 \\aG「古代魔物の手袋」\\aAを獲得！"));
                        } else { // 残りの確率が失敗
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に失敗しました。"));
                        }
                    } else {
                        pc.sendPackets(new S_SystemMessage("材料が不足して"));
                    }

                    /** ラッキー製作アイテム確率 **/
                } else if (itemtype == 1771) {// 古代技術書
                    Random random = new Random();
                    if ((pc.getInventory().checkItem(3000065, 1))) {// 材料アイテム
                        if (pc.getInventory().consumeItem(3000065, 1)) {// アイテム回収
                            ;
                        }
                        if (random.nextInt(100) < 20) { // 20％の確率で成功
                            supplyEnchant(pc, 3000110, 1, 0);
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に成功して\\aG「古代技術書」\\aAを獲得!"));
                        } else { // 残りの確率が失敗
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に失敗しました。"));
                        }
                    } else {
                        pc.sendPackets(new S_SystemMessage("材料が不足して"));
                    }
                } else if (itemtype == 1772) {// 古代タブレット
                    Random random = new Random();
                    if ((pc.getInventory().checkItem(3000065, 1))) {// 材料アイテム
                        if (pc.getInventory().consumeItem(3000065, 1)) {// アイテム回収
                            ;
                        }
                        if (random.nextInt(100) < 20) { // 20％の確率で成功
                            supplyEnchant(pc, 3000113, 1, 0);
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に成功して\\aG「古代タブレット」\\aAを獲得！"));
                        } else { // 残りの確率が失敗
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に失敗しました。"));
                        }
                    } else {
                        pc.sendPackets(new S_SystemMessage("材料が不足して"));
                    }
                } else if (itemtype == 1773) {// 古代の記憶の修正
                    Random random = new Random();
                    if ((pc.getInventory().checkItem(3000065, 1))) {// 材料アイテム
                        if (pc.getInventory().consumeItem(3000065, 1)) {// アイテム回収
                            ;
                        }
                        if (random.nextInt(100) < 20) { // 20％の確率で成功
                            supplyEnchant(pc, 3000112, 1, 0);
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に成功して\\aG「古代の記憶の修正」\\aAを獲得！"));
                        } else { // 残りの確率が失敗
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に失敗しました。"));
                        }
                    } else {
                        pc.sendPackets(new S_SystemMessage("材料が不足して"));
                    }
                } else if (itemtype == 1774) {// 古代精霊の水晶
                    Random random = new Random();
                    if ((pc.getInventory().checkItem(3000065, 1))) {// 材料アイテム
                        if (pc.getInventory().consumeItem(3000065, 1)) {// アイテム回収
                            ;
                        }
                        if (random.nextInt(100) < 20) { // 20％の確率で成功
                            supplyEnchant(pc, 3000116, 1, 0);
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に成功して\\aG「古代精霊の水晶」\\aAを獲得！"));
                        } else { // 残りの確率が失敗
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に失敗しました。"));
                        }
                    } else {
                        pc.sendPackets(new S_SystemMessage("材料が不足して"));
                    }
                } else if (itemtype == 1775) {// 古代闇精霊の水晶
                    Random random = new Random();
                    if ((pc.getInventory().checkItem(3000065, 1))) {// 材料アイテム
                        if (pc.getInventory().consumeItem(3000065, 1)) {// アイテム回収
                            ;
                        }
                        if (random.nextInt(100) < 20) { // 20％の確率で成功
                            supplyEnchant(pc, 3000117, 1, 0);
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に成功して\\aG「古代闇精霊の水晶」\\aAを獲得！"));
                        } else { // 残りの確率が失敗
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に失敗しました。"));
                        }
                    } else {
                        pc.sendPackets(new S_SystemMessage("材料が不足して"));
                    }
                } else if (itemtype == 1776) {// 古代戦士の印章
                    Random random = new Random();
                    if ((pc.getInventory().checkItem(3000065, 1))) {// 材料アイテム
                        if (pc.getInventory().consumeItem(3000065, 1)) {// アイテム回収
                            ;
                        }
                        if (random.nextInt(100) < 20) { // 20％の確率で成功
                            supplyEnchant(pc, 3000115, 1, 0);
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に成功して\\aG「古代の戦士の印章」\\aAを獲得！"));
                        } else { // 残りの確率が失敗
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に失敗しました。"));
                        }
                    } else {
                        pc.sendPackets(new S_SystemMessage("材料が不足して"));
                    }
                } else if (itemtype == 1777) {// 古代魔法書
                    Random random = new Random();
                    if ((pc.getInventory().checkItem(3000065, 1))) {// 材料アイテム
                        if (pc.getInventory().consumeItem(3000065, 1)) {// アイテム回収
                            ;
                        }
                        if (random.nextInt(100) < 20) { // 20％の確率で成功
                            supplyEnchant(pc, 3000111, 1, 0);
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に成功して\\aG「古代魔法書」\\aAを獲得!"));
                        } else { // 残りの確率が失敗
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に失敗しました。"));
                        }
                    } else {
                        pc.sendPackets(new S_SystemMessage("材料が不足して"));
                    }
                } else if (itemtype == 1778) {// 古代オーラ
                    Random random = new Random();
                    if ((pc.getInventory().checkItem(3000065, 1))) {// 材料アイテム
                        if (pc.getInventory().consumeItem(3000065, 1)) {// アイテム回収
                            ;
                        }
                        if (random.nextInt(100) < 20) { // 20％の確率で成功
                            supplyEnchant(pc, 3000114, 1, 0);
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に成功して\\aG「古代オーラ」\\aAを獲得!"));
                        } else { // 残りの確率が失敗
                            pc.sendPackets(new S_SystemMessage("\\aA通知：製作に失敗しました。"));
                        }
                    } else {
                        pc.sendPackets(new S_SystemMessage("材料が不足して"));
                    }

                } else if (itemtype == 159) {
                    if ((pc.getInventory().checkEnchantItem(500, 8, 1) // [+7]歓迎のチェーンソード
                            || pc.getInventory().checkEnchantItem(501, 8, 1))
                            && pc.getInventory().checkItem(40308, 5000000)) {
                        if (pc.getInventory().consumeEnchantItem(500, 8, 1)
                                || pc.getInventory().consumeEnchantItem(501, 8, 1)) {
                            ;
                        }
                        pc.getInventory().consumeItem(40308, 5000000);
                        supplyEnchant(pc, 202001, 1, 7);
                    }
                } else if (itemtype == 160) {
                    if ((pc.getInventory().checkEnchantItem(500, 9, 1) // [+8]歓迎のチェーンソード
                            || pc.getInventory().checkEnchantItem(501, 9, 1))
                            && pc.getInventory().checkItem(40308, 10000000)) {
                        if (pc.getInventory().consumeEnchantItem(500, 9, 1)
                                || pc.getInventory().consumeEnchantItem(501, 9, 1)) {
                            ;
                        }
                        pc.getInventory().consumeItem(40308, 10000000);
                        supplyEnchant(pc, 202001, 1, 8);
                    }
                } else if (itemtype == 161) {
                    if ((pc.getInventory().checkEnchantItem(503, 8, 1) // [+7]共鳴の
                            // キーリンク
                            || pc.getInventory().checkEnchantItem(504, 8, 1))
                            && pc.getInventory().checkItem(40308, 5000000)) {
                        if (pc.getInventory().consumeEnchantItem(503, 8, 1)
                                || pc.getInventory().consumeEnchantItem(504, 8, 1)) {
                            ;
                        }
                        pc.getInventory().consumeItem(40308, 5000000);
                        supplyEnchant(pc, 1135, 1, 7);
                    }
                } else if (itemtype == 162) {
                    if ((pc.getInventory().checkEnchantItem(503, 9, 1) // [+8]共鳴の
                            // キーリンク
                            || pc.getInventory().checkEnchantItem(504, 9, 1))
                            && pc.getInventory().checkItem(40308, 10000000)) {
                        if (pc.getInventory().consumeEnchantItem(503, 9, 1)
                                || pc.getInventory().consumeEnchantItem(504, 9, 1)) {
                            ;
                        }
                        pc.getInventory().consumeItem(40308, 10000000);
                        supplyEnchant(pc, 1135, 1, 8);
                    }
                } else if (itemtype == 287) {
                    if ((pc.getInventory().checkEnchantItem(81, 8, 1) // [+7]破壊のクロウ
                            || pc.getInventory().checkEnchantItem(162, 8, 1)
                            || pc.getInventory().checkEnchantItem(177, 8, 1)
                            || pc.getInventory().checkEnchantItem(194, 8, 1)
                            || pc.getInventory().checkEnchantItem(13, 8, 1))

                            && pc.getInventory().checkItem(40308, 5000000)) {
                        if (pc.getInventory().consumeEnchantItem(81, 8, 1)
                                || pc.getInventory().consumeEnchantItem(162, 8, 1)
                                || pc.getInventory().consumeEnchantItem(177, 8, 1)
                                || pc.getInventory().consumeEnchantItem(194, 8, 1)
                                || pc.getInventory().consumeEnchantItem(13, 8, 1)) {
                            ;
                        }
                        pc.getInventory().consumeItem(40308, 5000000);
                        supplyEnchant(pc, 1124, 1, 7);
                    }
                } else if (itemtype == 288) {
                    if ((pc.getInventory().checkEnchantItem(81, 9, 1) // [+8]破壊のクロウ
                            || pc.getInventory().consumeEnchantItem(162, 8, 1)
                            || pc.getInventory().checkEnchantItem(177, 9, 1)
                            || pc.getInventory().checkEnchantItem(194, 9, 1)
                            || pc.getInventory().checkEnchantItem(13, 9, 1))
                            && pc.getInventory().checkItem(40308, 10000000)) {
                        if (pc.getInventory().consumeEnchantItem(81, 9, 1)
                                || pc.getInventory().consumeEnchantItem(162, 9, 1)
                                || pc.getInventory().consumeEnchantItem(177, 9, 1)
                                || pc.getInventory().consumeEnchantItem(194, 9, 1)
                                || pc.getInventory().consumeEnchantItem(13, 9, 1)) {
                            ;
                        }
                        pc.getInventory().consumeItem(40308, 10000000);
                        supplyEnchant(pc, 1124, 1, 8);
                    }
                } else if (itemtype == 289) {
                    if ((pc.getInventory().checkEnchantItem(81, 8, 1) // [+7]破壊の二刀流
                            || pc.getInventory().checkEnchantItem(162, 8, 1)
                            || pc.getInventory().checkEnchantItem(177, 8, 1)
                            || pc.getInventory().checkEnchantItem(194, 8, 1)
                            || pc.getInventory().checkEnchantItem(13, 8, 1))
                            && pc.getInventory().checkItem(40308, 5000000)) {
                        if (pc.getInventory().consumeEnchantItem(81, 8, 1)
                                || pc.getInventory().consumeEnchantItem(162, 8, 1)
                                || pc.getInventory().consumeEnchantItem(177, 8, 1)
                                || pc.getInventory().consumeEnchantItem(194, 8, 1)
                                || pc.getInventory().consumeEnchantItem(13, 8, 1)) {
                            ;
                        }
                        pc.getInventory().consumeItem(40308, 5000000);
                        supplyEnchant(pc, 1125, 1, 7);
                    }
                } else if (itemtype == 290) {
                    if ((pc.getInventory().checkEnchantItem(81, 9, 1) // [+8]破壊の二刀流
                            || pc.getInventory().checkEnchantItem(162, 9, 1)
                            || pc.getInventory().checkEnchantItem(177, 9, 1)
                            || pc.getInventory().checkEnchantItem(194, 9, 1)
                            || pc.getInventory().checkEnchantItem(13, 9, 1))
                            && pc.getInventory().checkItem(40308, 10000000)) {
                        if (pc.getInventory().consumeEnchantItem(81, 9, 1)
                                || pc.getInventory().consumeEnchantItem(162, 9, 1)
                                || pc.getInventory().consumeEnchantItem(177, 9, 1)
                                || pc.getInventory().consumeEnchantItem(194, 9, 1)
                                || pc.getInventory().consumeEnchantItem(13, 9, 1)) {
                            ;
                        }
                        pc.getInventory().consumeItem(40308, 10000000);
                        supplyEnchant(pc, 1125, 1, 8);
                    }
                } else if (itemtype == 577) { // 力のルーンポケット
                    if (pc.getInventory().checkItem(60033, 1) && pc.getInventory().checkItem(60034, 1)
                            && pc.getInventory().checkItem(40087, 70)) {
                        pc.getInventory().consumeItem(60033, 1);
                        pc.getInventory().consumeItem(60034, 1);
                        pc.getInventory().consumeItem(40087, 70);
                    }
                    supplyEnchant(pc, 60041, 1, 0);
                } else if (itemtype == 578) { // アジャイルのルーンポケット
                    if (pc.getInventory().checkItem(60033, 1) && pc.getInventory().checkItem(60034, 1)
                            && pc.getInventory().checkItem(40087, 70)) {
                        pc.getInventory().consumeItem(60033, 1);
                        pc.getInventory().consumeItem(60034, 1);
                        pc.getInventory().consumeItem(40087, 70);
                    }
                    supplyEnchant(pc, 60042, 1, 0);
                } else if (itemtype == 579) { // 体力のルーンポケット
                    if (pc.getInventory().checkItem(60033, 1) && pc.getInventory().checkItem(60034, 1)
                            && pc.getInventory().checkItem(40087, 70)) {
                        pc.getInventory().consumeItem(60033, 1);
                        pc.getInventory().consumeItem(60034, 1);
                        pc.getInventory().consumeItem(40087, 70);
                    }
                    supplyEnchant(pc, 60043, 1, 0);
                } else if (itemtype == 580) { // 知識のルーンポケット
                    if (pc.getInventory().checkItem(60033, 1) && pc.getInventory().checkItem(60034, 1)
                            && pc.getInventory().checkItem(40087, 70)) {
                        pc.getInventory().consumeItem(60033, 1);
                        pc.getInventory().consumeItem(60034, 1);
                        pc.getInventory().consumeItem(40087, 70);
                    }
                    supplyEnchant(pc, 60044, 1, 0);
                } else if (itemtype == 581) { // 知恵ののルーンポケット
                    if (pc.getInventory().checkItem(60033, 1) && pc.getInventory().checkItem(60034, 1)
                            && pc.getInventory().checkItem(40087, 70)) {
                        pc.getInventory().consumeItem(60033, 1);
                        pc.getInventory().consumeItem(60034, 1);
                        pc.getInventory().consumeItem(40087, 70);
                    }
                    supplyEnchant(pc, 60045, 1, 0);

                } else if (itemtype == 3385) { // 腕力遺物
                    if (pc.getInventory().checkItem(30072, 4) && pc.getInventory().checkItem(40308, 7000)) {
                        pc.getInventory().consumeItem(30072, 4);
                        pc.getInventory().consumeItem(40308, 7000);
                    }
                    supplyEnchant(pc, 8023, 1, 0);
                } else if (itemtype == 3386) { // アジャイル遺物
                    if (pc.getInventory().checkItem(30072, 4) && pc.getInventory().checkItem(40308, 7000)) {
                        pc.getInventory().consumeItem(30072, 4);
                        pc.getInventory().consumeItem(40308, 7000);
                    }
                    supplyEnchant(pc, 8024, 1, 0);
                } else if (itemtype == 3387) { // 知識の遺物
                    if (pc.getInventory().checkItem(30072, 4) && pc.getInventory().checkItem(40308, 7000)) {
                        pc.getInventory().consumeItem(30072, 4);
                        pc.getInventory().consumeItem(40308, 7000);
                    }
                    supplyEnchant(pc, 8025, 1, 0);

                } else if (itemtype == 3387) { // ドラゴン高級ボックス
                    if (pc.getInventory().checkEnchantItem(1000006, 0, 4)) {
                        supplyEnchant(pc, 1000008, 1, 0);
                        pc.getInventory().consumeEnchantItem(1000006, 0, 4);
                    }

                } else if (itemtype == 107) { // 誕生魔眼
                    int chance = _Random.nextInt(100) + 1;
                    if (pc.getInventory().checkItem(410032, 1)
                            || pc.getInventory().checkItem(410034, 1) && pc.getInventory().checkItem(40308, 200000)) {
                        if (chance <= 30) {
                            supplyEnchant(pc, 410036, 1, 0);
                        } else {
                            pc.sendPackets(new S_SystemMessage("魔眼の組み合わせに失敗しました。"));
                        }
                        pc.getInventory().consumeItem(410032, 1);
                        pc.getInventory().consumeItem(410034, 1);
                        pc.getInventory().consumeItem(40308, 200000);

                    }

                } else if (itemtype == 108) { // 形状魔眼
                    int chance = _Random.nextInt(100) + 1;
                    if (pc.getInventory().checkItem(410033, 1)
                            || pc.getInventory().checkItem(410036, 1) && pc.getInventory().checkItem(40308, 200000)) {

                        if (chance <= 30) {
                            supplyEnchant(pc, 410037, 1, 0);
                        } else {
                            pc.sendPackets(new S_SystemMessage("魔眼の組み合わせに失敗しました。"));
                        }
                        pc.getInventory().consumeItem(410033, 1);
                        pc.getInventory().consumeItem(410036, 1);
                        pc.getInventory().consumeItem(40308, 200000);
                    }

                } else if (itemtype == 109) { // 生命の魔眼
                    int chance = _Random.nextInt(100) + 1;
                    if (pc.getInventory().checkItem(410037, 1)
                            || pc.getInventory().checkItem(410035, 1) && pc.getInventory().checkItem(40308, 200000)) {

                        if (chance <= 30) {
                            supplyEnchant(pc, 410038, 1, 0);
                        } else {
                            pc.sendPackets(new S_SystemMessage("魔眼の組み合わせに失敗しました。"));
                        }
                        pc.getInventory().consumeItem(410037, 1);
                        pc.getInventory().consumeItem(410035, 1);
                        pc.getInventory().consumeItem(40308, 200000);

                    }

                    /** 真冥王執行剣 **/
                } else if (itemtype == 46) {
                    if (pc.getInventory().checkItem(49, 1) // 関係ソード
                            && pc.getInventory().checkItem(40965, 1) // 米スペルブック
                            && pc.getInventory().checkItem(40445, 10) // ミスリルプレート
                            && pc.getInventory().checkItem(40677, 50) // 闇のインゴット
                            && pc.getInventory().checkItem(40525, 10) // グランカインの涙
                            && pc.getInventory().checkItem(40969, 300) // 多ヨウンギョル
                            && pc.getInventory().checkItem(40967, 100) // 聖地の遺物
                            && pc.getInventory().checkItem(40964, 50)) { // 黒魔法の粉
                        pc.getInventory().consumeItem(40965, 1);
                        pc.getInventory().consumeItem(40445, 10);
                        pc.getInventory().consumeItem(40677, 50);
                        pc.getInventory().consumeItem(40525, 10);
                        pc.getInventory().consumeItem(40969, 300);
                        pc.getInventory().consumeItem(40967, 100);
                        pc.getInventory().consumeItem(40964, 50);
                    }
                    supplyEnchant(pc, 61, 1, 0);
                } else if (itemtype == 198) {
                    if (pc.getInventory().checkItem(111, 1) && pc.getInventory().checkItem(40318, 100)
                            && pc.getInventory().checkItem(40090, 2) && pc.getInventory().checkItem(40091, 2)
                            && pc.getInventory().checkItem(40092, 2) && pc.getInventory().checkItem(40093, 2)
                            && pc.getInventory().checkItem(40094, 2)) {
                        pc.getInventory().consumeItem(111, 1);
                        pc.getInventory().consumeItem(40318, 100);
                        pc.getInventory().consumeItem(40090, 2);
                        pc.getInventory().consumeItem(40091, 2);
                        pc.getInventory().consumeItem(40092, 2);
                        pc.getInventory().consumeItem(40093, 2);
                        pc.getInventory().consumeItem(40094, 2);
                    }
                    supplyEnchant(pc, 121, 1, 0);
                } else if (itemtype == 49) {
                    if (pc.getInventory().checkItem(111, 1) && pc.getInventory().checkItem(40318, 100)
                            && pc.getInventory().checkItem(40090, 2) && pc.getInventory().checkItem(40091, 2)
                            && pc.getInventory().checkItem(40092, 2) && pc.getInventory().checkItem(40093, 2)
                            && pc.getInventory().checkItem(40094, 2)) {
                        pc.getInventory().consumeItem(111, 1);
                        pc.getInventory().consumeItem(40318, 100);
                        pc.getInventory().consumeItem(40090, 2);
                        pc.getInventory().consumeItem(40091, 2);
                        pc.getInventory().consumeItem(40092, 2);
                        pc.getInventory().consumeItem(40093, 2);
                        pc.getInventory().consumeItem(40094, 2);
                    }
                    supplyEnchant(pc, 134, 1, 0);

                    // 古代の武器箱
                } else if (itemtype == 2652) {
                    int chance = _Random.nextInt(100) + 1;
                    if (pc.getInventory().checkItem(40087, 5) && pc.getInventory().checkItem(40308, 100000)) {
                        if (chance <= 20) {
                            supplyEnchant(pc, 31093, 1, 0);
                        } else {
                            pc.sendPackets(new S_SystemMessage("古代の武器箱製作に失敗しました。"));
                        }
                        pc.getInventory().consumeItem(40087, 5);
                        pc.getInventory().consumeItem(40308, 100000);
                    }
                    // 古代の防具箱
                } else if (itemtype == 2653) {
                    int chance = _Random.nextInt(100) + 1;
                    if (pc.getInventory().checkItem(40074, 5) && pc.getInventory().checkItem(40308, 100000)) {
                        if (chance <= 20) {
                            supplyEnchant(pc, 31094, 1, 0);
                        } else {
                            pc.sendPackets(new S_SystemMessage("古代の武器箱製作に失敗しました。"));
                        }
                        pc.getInventory().consumeItem(40074, 5);
                        pc.getInventory().consumeItem(40308, 100000);
                    }

                    /** 転生の宝石 **/
                } else if (itemtype == 2739) {
                    if (pc.getInventory().checkItem(40052, 1) && pc.getInventory().checkItem(40055, 1)
                            && pc.getInventory().checkItem(40053, 1) && pc.getInventory().checkItem(40054, 1)
                            && pc.getInventory().checkItem(410061, 1)) {
                        supplyEnchant(pc, 31096, 1, 0);
                        pc.getInventory().consumeItem(40052, 1);
                        pc.getInventory().consumeItem(40055, 1);
                        pc.getInventory().consumeItem(40053, 1);
                        pc.getInventory().consumeItem(40054, 1);
                        pc.getInventory().consumeItem(410061, 1);
                    }

                    /** 神聖なエルヴンプレートメイル **/
                } else if (itemtype == 2864) {
                    if (pc.getInventory().checkItem(40396, 5) && pc.getInventory().checkItem(31096, 50)
                            || pc.getInventory().checkItem(820018, 5)) {
                        supplyEnchant(pc, 222351, 1, 0);
                        pc.getInventory().consumeItem(40396, 5);
                        pc.getInventory().consumeItem(31096, 50);
                        pc.getInventory().consumeItem(820018, 5);
                    }
                    /** 神聖なエルヴンシールド **/
                } else if (itemtype == 2865) {
                    if (pc.getInventory().checkItem(40396, 2) && pc.getInventory().checkItem(31096, 25)
                            || pc.getInventory().checkItem(820018, 2)) {
                        supplyEnchant(pc, 222355, 1, 0);
                        pc.getInventory().consumeItem(40396, 2);
                        pc.getInventory().consumeItem(31096, 25);
                        pc.getInventory().consumeItem(820018, 2);
                    }
                    /** 6ウイングパワーグローブ **/
                } else if (itemtype == 2784) {
                    if (pc.getInventory().checkEnchantItem(20187, 7, 1) && pc.getInventory().checkItem(31096, 75)
                            || pc.getInventory().checkEnchantItem(22212, 6, 1)) {
                        supplyEnchant(pc, 222345, 1, 6);
                        pc.getInventory().consumeEnchantItem(20187, 7, 1);
                        pc.getInventory().consumeEnchantItem(22212, 6, 1);
                        pc.getInventory().consumeItem(31096, 75);
                    }
                    /** 7ウイングパワーグローブ **/
                } else if (itemtype == 2785) {
                    if (pc.getInventory().checkEnchantItem(20187, 8, 1) && pc.getInventory().checkItem(31096, 100)
                            || pc.getInventory().checkEnchantItem(22212, 7, 1)) {
                        supplyEnchant(pc, 222345, 1, 7);
                        pc.getInventory().consumeEnchantItem(20187, 8, 1);
                        pc.getInventory().consumeEnchantItem(22212, 7, 1);
                        pc.getInventory().consumeItem(31096, 100);
                    }
                    /** 8ウイングパワーグローブ **/
                } else if (itemtype == 2786) {
                    if (pc.getInventory().checkEnchantItem(20187, 9, 1) && pc.getInventory().checkItem(31096, 125)
                            || pc.getInventory().checkEnchantItem(22212, 8, 1)) {
                        supplyEnchant(pc, 222345, 1, 8);
                        pc.getInventory().consumeEnchantItem(20187, 9, 1);
                        pc.getInventory().consumeEnchantItem(22212, 8, 1);
                        pc.getInventory().consumeItem(31096, 125);
                    }
                    /** 9ウイングパワーグローブ **/
                } else if (itemtype == 2787) {
                    if (pc.getInventory().checkEnchantItem(20187, 10, 1) && pc.getInventory().checkItem(31096, 150)
                            || pc.getInventory().checkEnchantItem(22212, 9, 1)) {
                        supplyEnchant(pc, 222345, 1, 9);
                        pc.getInventory().consumeEnchantItem(20187, 10, 1);
                        pc.getInventory().consumeEnchantItem(22212, 9, 1);
                        pc.getInventory().consumeItem(31096, 150);
                    }
                    /** 輝くボディベルト **/
                } else if (itemtype == 232) {
                    if (pc.getInventory().checkItem(40308, 100000) && pc.getInventory().checkItem(40458, 50)
                            && pc.getInventory().checkItem(40049, 30) && pc.getInventory().checkItem(20306, 1)
                            || pc.getInventory().checkItem(20312, 2)) {
                        supplyEnchant(pc, 20309, 1, 0);
                        pc.getInventory().consumeItem(40308, 100000);
                        pc.getInventory().consumeItem(40458, 50);
                        pc.getInventory().consumeItem(40049, 30);
                        pc.getInventory().consumeItem(20306, 1);
                        pc.getInventory().consumeItem(20312, 1);
                    }
                    /** 輝く精神のベルト **/
                } else if (itemtype == 233) {
                    if (pc.getInventory().checkItem(40308, 100000) && pc.getInventory().checkItem(40458, 50)
                            && pc.getInventory().checkItem(40050, 30) && pc.getInventory().checkItem(20308, 1)
                            || pc.getInventory().checkItem(20319, 2)) {
                        supplyEnchant(pc, 20309, 1, 0);
                        pc.getInventory().consumeItem(40308, 100000);
                        pc.getInventory().consumeItem(40458, 50);
                        pc.getInventory().consumeItem(40050, 30);
                        pc.getInventory().consumeItem(20308, 1);
                        pc.getInventory().consumeItem(20319, 1);
                    }
                    /** 輝く魂のベルト **/
                } else if (itemtype == 234) {
                    if (pc.getInventory().checkItem(40308, 100000) && pc.getInventory().checkItem(40458, 50)
                            && pc.getInventory().checkItem(40050, 20) && pc.getInventory().checkItem(40049, 20)
                            && pc.getInventory().checkItem(20307, 1) || pc.getInventory().checkItem(20316, 1)) {
                        supplyEnchant(pc, 20309, 1, 0);
                        pc.getInventory().consumeItem(40308, 100000);
                        pc.getInventory().consumeItem(40458, 50);
                        pc.getInventory().consumeItem(40050, 20);
                        pc.getInventory().consumeItem(40049, 20);
                        pc.getInventory().consumeItem(20307, 1);
                        pc.getInventory().consumeItem(20316, 1);
                    }
                    /** 9ジンと戦う **/
                } else if (itemtype == 2876) {
                    if (pc.getInventory().checkEnchantItem(57, 11, 1) && pc.getInventory().checkItem(67, 1)
                            || pc.getInventory().checkItem(68, 1)) {
                        supplyEnchant(pc, 203025, 1, 9);
                        pc.getInventory().consumeEnchantItem(57, 11, 1);
                        pc.getInventory().consumeItem(67, 1);
                        pc.getInventory().consumeItem(68, 1);
                    }
                    /** 7ジンと戦う **/
                } else if (itemtype == 2877) {
                    if (pc.getInventory().checkEnchantItem(57, 10, 1) && pc.getInventory().checkItem(67, 1)
                            || pc.getInventory().checkItem(68, 1)) {
                        supplyEnchant(pc, 203025, 1, 7);
                        pc.getInventory().consumeEnchantItem(57, 10, 1);
                        pc.getInventory().consumeItem(67, 1);
                        pc.getInventory().consumeItem(68, 1);
                    }

                    // 神聖な腕力のモゴルが
                } else if (itemtype == 2775) {
                    if (pc.getInventory().checkItem(40393, 15) && pc.getInventory().checkItem(31096, 10)
                            || pc.getInventory().checkItem(31095, 30)) {
                        supplyEnchant(pc, 222346, 1, 0);
                        pc.getInventory().consumeItem(40393, 15);
                        pc.getInventory().consumeItem(31096, 10);
                        pc.getInventory().consumeItem(31095, 30);
                    }
                    // 神聖なアジャイルのネッ、
                } else if (itemtype == 2776) {
                    if (pc.getInventory().checkItem(40394, 15) && pc.getInventory().checkItem(31096, 10)
                            || pc.getInventory().checkItem(31095, 30)) {
                        supplyEnchant(pc, 222347, 1, 0);
                        pc.getInventory().consumeItem(40394, 15);
                        pc.getInventory().consumeItem(31096, 10);
                        pc.getInventory().consumeItem(31095, 30);
                    }
                    // 神聖な知識のネッ、
                } else if (itemtype == 2777) {
                    if (pc.getInventory().checkItem(40395, 15) && pc.getInventory().checkItem(31096, 10)
                            || pc.getInventory().checkItem(31095, 30)) {
                        supplyEnchant(pc, 222348, 1, 0);
                        pc.getInventory().consumeItem(40395, 15);
                        pc.getInventory().consumeItem(31096, 10);
                        pc.getInventory().consumeItem(31095, 30);
                    }
                    // 神聖な永遠のネッ、
                } else if (itemtype == 2778) {
                    if (pc.getInventory().checkItem(40396, 25) && pc.getInventory().checkItem(31096, 50)
                            || pc.getInventory().checkItem(31095, 50)) {
                        supplyEnchant(pc, 222349, 1, 0);
                        pc.getInventory().consumeItem(40396, 25);
                        pc.getInventory().consumeItem(31096, 50);
                        pc.getInventory().consumeItem(31095, 50);
                    }

                    /** 10）神聖なネックレス彫刻 **/
                } else if (itemtype == 2779) {
                    if (pc.getInventory().checkEnchantItem(22194, 5, 1)
                            || pc.getInventory().checkEnchantItem(900008, 5, 1)
                            || pc.getInventory().checkEnchantItem(22195, 5, 1)
                            || pc.getInventory().checkEnchantItem(20264, 5, 1)
                            || pc.getInventory().checkEnchantItem(20422, 5, 1)
                            || pc.getInventory().checkEnchantItem(20256, 5, 1)
                            || pc.getInventory().checkEnchantItem(20257, 5, 1)
                            || pc.getInventory().checkEnchantItem(20266, 5, 1)
                            || pc.getInventory().checkEnchantItem(20411, 5, 1)
                            || pc.getInventory().checkEnchantItem(22361, 5, 1)
                            || pc.getInventory().checkEnchantItem(20412, 5, 1)
                            || pc.getInventory().checkEnchantItem(22362, 5, 1)) {
                        supplyEnchant(pc, 31095, 10, 0);
                        pc.getInventory().consumeEnchantItem(22194, 5, 1);
                        pc.getInventory().consumeEnchantItem(22195, 5, 1);
                        pc.getInventory().consumeEnchantItem(20422, 5, 1);
                        pc.getInventory().consumeEnchantItem(20257, 5, 1);
                        pc.getInventory().consumeEnchantItem(20411, 5, 1);
                        pc.getInventory().consumeEnchantItem(20412, 5, 1);
                        pc.getInventory().consumeEnchantItem(900008, 5, 1);
                        pc.getInventory().consumeEnchantItem(20264, 5, 1);
                        pc.getInventory().consumeEnchantItem(20256, 5, 1);
                        pc.getInventory().consumeEnchantItem(20266, 5, 1);
                        pc.getInventory().consumeEnchantItem(22361, 5, 1);
                        pc.getInventory().consumeEnchantItem(22362, 5, 1);
                    }
                    /** 20）神聖なネックレス彫刻 **/
                } else if (itemtype == 2780) {
                    if (pc.getInventory().checkEnchantItem(22194, 6, 1)
                            || pc.getInventory().checkEnchantItem(900008, 6, 1)
                            || pc.getInventory().checkEnchantItem(22195, 6, 1)
                            || pc.getInventory().checkEnchantItem(20264, 6, 1)
                            || pc.getInventory().checkEnchantItem(20422, 6, 1)
                            || pc.getInventory().checkEnchantItem(20256, 6, 1)
                            || pc.getInventory().checkEnchantItem(20257, 6, 1)
                            || pc.getInventory().checkEnchantItem(20266, 6, 1)
                            || pc.getInventory().checkEnchantItem(20411, 6, 1)
                            || pc.getInventory().checkEnchantItem(22361, 6, 1)
                            || pc.getInventory().checkEnchantItem(20412, 6, 1)
                            || pc.getInventory().checkEnchantItem(22362, 6, 1)) {
                        supplyEnchant(pc, 31095, 20, 0);
                        pc.getInventory().consumeEnchantItem(22194, 6, 1);
                        pc.getInventory().consumeEnchantItem(22195, 6, 1);
                        pc.getInventory().consumeEnchantItem(20422, 6, 1);
                        pc.getInventory().consumeEnchantItem(20257, 6, 1);
                        pc.getInventory().consumeEnchantItem(20411, 6, 1);
                        pc.getInventory().consumeEnchantItem(20412, 6, 1);
                        pc.getInventory().consumeEnchantItem(900008, 6, 1);
                        pc.getInventory().consumeEnchantItem(20264, 6, 1);
                        pc.getInventory().consumeEnchantItem(20256, 6, 1);
                        pc.getInventory().consumeEnchantItem(20266, 6, 1);
                        pc.getInventory().consumeEnchantItem(22361, 6, 1);
                        pc.getInventory().consumeEnchantItem(22362, 6, 1);
                    }
                    /** 40)神聖なネックレス彫刻 **/
                } else if (itemtype == 2781) {
                    if (pc.getInventory().checkEnchantItem(22194, 7, 1)
                            || pc.getInventory().checkEnchantItem(900008, 7, 1)
                            || pc.getInventory().checkEnchantItem(22195, 7, 1)
                            || pc.getInventory().checkEnchantItem(20264, 7, 1)
                            || pc.getInventory().checkEnchantItem(20422, 7, 1)
                            || pc.getInventory().checkEnchantItem(20256, 7, 1)
                            || pc.getInventory().checkEnchantItem(20257, 7, 1)
                            || pc.getInventory().checkEnchantItem(20266, 7, 1)
                            || pc.getInventory().checkEnchantItem(20411, 7, 1)
                            || pc.getInventory().checkEnchantItem(22361, 7, 1)
                            || pc.getInventory().checkEnchantItem(20412, 7, 1)
                            || pc.getInventory().checkEnchantItem(22362, 7, 1)) {
                        supplyEnchant(pc, 31095, 40, 0);
                        pc.getInventory().consumeEnchantItem(22194, 7, 1);
                        pc.getInventory().consumeEnchantItem(22195, 7, 1);
                        pc.getInventory().consumeEnchantItem(20422, 7, 1);
                        pc.getInventory().consumeEnchantItem(20257, 7, 1);
                        pc.getInventory().consumeEnchantItem(20411, 7, 1);
                        pc.getInventory().consumeEnchantItem(20412, 7, 1);
                        pc.getInventory().consumeEnchantItem(900008, 7, 1);
                        pc.getInventory().consumeEnchantItem(20264, 7, 1);
                        pc.getInventory().consumeEnchantItem(20256, 7, 1);
                        pc.getInventory().consumeEnchantItem(20266, 7, 1);
                        pc.getInventory().consumeEnchantItem(22361, 7, 1);
                        pc.getInventory().consumeEnchantItem(22362, 7, 1);
                    }
                    /** 80)神聖なネックレス彫刻 **/
                } else if (itemtype == 2782) {
                    if (pc.getInventory().checkEnchantItem(22194, 8, 1)
                            || pc.getInventory().checkEnchantItem(900008, 8, 1)
                            || pc.getInventory().checkEnchantItem(22195, 8, 1)
                            || pc.getInventory().checkEnchantItem(20264, 8, 1)
                            || pc.getInventory().checkEnchantItem(20422, 8, 1)
                            || pc.getInventory().checkEnchantItem(20256, 8, 1)
                            || pc.getInventory().checkEnchantItem(20257, 8, 1)
                            || pc.getInventory().checkEnchantItem(20266, 8, 1)
                            || pc.getInventory().checkEnchantItem(20411, 8, 1)
                            || pc.getInventory().checkEnchantItem(22361, 8, 1)
                            || pc.getInventory().checkEnchantItem(20412, 8, 1)
                            || pc.getInventory().checkEnchantItem(22362, 8, 1)) {
                        supplyEnchant(pc, 31095, 80, 0);
                        pc.getInventory().consumeEnchantItem(22194, 8, 1);
                        pc.getInventory().consumeEnchantItem(22195, 8, 1);
                        pc.getInventory().consumeEnchantItem(20422, 8, 1);
                        pc.getInventory().consumeEnchantItem(20257, 8, 1);
                        pc.getInventory().consumeEnchantItem(20411, 8, 1);
                        pc.getInventory().consumeEnchantItem(20412, 8, 1);
                        pc.getInventory().consumeEnchantItem(900008, 8, 1);
                        pc.getInventory().consumeEnchantItem(20264, 8, 1);
                        pc.getInventory().consumeEnchantItem(20256, 8, 1);
                        pc.getInventory().consumeEnchantItem(20266, 8, 1);
                        pc.getInventory().consumeEnchantItem(22361, 8, 1);
                        pc.getInventory().consumeEnchantItem(22362, 8, 1);
                    }

                    // 寛容のイヤリング
                } else if (itemtype == 305) {
                    if (pc.getInventory().checkItem(40651, 60) && pc.getInventory().checkItem(40643, 60)
                            && pc.getInventory().checkItem(40645, 60) && pc.getInventory().checkItem(40618, 60)
                            && pc.getInventory().checkItem(40676, 60) || pc.getInventory().checkItem(40961, 9)
                            || pc.getInventory().checkItem(40960, 9) || pc.getInventory().checkItem(40962, 9)
                            || pc.getInventory().checkItem(40959, 9) || pc.getInventory().checkItem(40638, 90)
                            || pc.getInventory().checkItem(40635, 90) || pc.getInventory().checkItem(40667, 90)
                            || pc.getInventory().checkItem(40642, 90)

                            || pc.getInventory().checkItem(21012, 1)

                    ) {
                        supplyEnchant(pc, 21013, 1, 0);
                        pc.getInventory().consumeItem(40651, 60);
                        pc.getInventory().consumeItem(40643, 60);
                        pc.getInventory().consumeItem(40645, 60);
                        pc.getInventory().consumeItem(40618, 60);
                        pc.getInventory().consumeItem(40676, 60);
                        pc.getInventory().consumeItem(40961, 9);
                        pc.getInventory().consumeItem(40960, 9);
                        pc.getInventory().consumeItem(40962, 9);
                        pc.getInventory().consumeItem(40959, 9);
                        pc.getInventory().consumeItem(40638, 90);
                        pc.getInventory().consumeItem(40635, 90);
                        pc.getInventory().consumeItem(40667, 90);
                        pc.getInventory().consumeItem(40642, 90);
                        pc.getInventory().consumeItem(21012, 1);
                    }
                    // 不死のイヤリング
                } else if (itemtype == 309) {
                    if (pc.getInventory().checkItem(40651, 60) && pc.getInventory().checkItem(40643, 60)
                            && pc.getInventory().checkItem(40645, 60) && pc.getInventory().checkItem(40618, 60)
                            && pc.getInventory().checkItem(40676, 60) || pc.getInventory().checkItem(40961, 9)
                            || pc.getInventory().checkItem(40960, 9) || pc.getInventory().checkItem(40962, 9)
                            || pc.getInventory().checkItem(40959, 9) || pc.getInventory().checkItem(40638, 90)
                            || pc.getInventory().checkItem(40635, 90) || pc.getInventory().checkItem(40667, 90)
                            || pc.getInventory().checkItem(40642, 90)

                            || pc.getInventory().checkItem(21010, 1)

                    ) {
                        supplyEnchant(pc, 21011, 1, 0);
                        pc.getInventory().consumeItem(40651, 60);
                        pc.getInventory().consumeItem(40643, 60);
                        pc.getInventory().consumeItem(40645, 60);
                        pc.getInventory().consumeItem(40618, 60);
                        pc.getInventory().consumeItem(40676, 60);
                        pc.getInventory().consumeItem(40961, 9);
                        pc.getInventory().consumeItem(40960, 9);
                        pc.getInventory().consumeItem(40962, 9);
                        pc.getInventory().consumeItem(40959, 9);
                        pc.getInventory().consumeItem(40638, 90);
                        pc.getInventory().consumeItem(40635, 90);
                        pc.getInventory().consumeItem(40667, 90);
                        pc.getInventory().consumeItem(40642, 90);
                        pc.getInventory().consumeItem(21010, 1);
                    }
                    // 支配のイヤリング
                } else if (itemtype == 313) {
                    if (pc.getInventory().checkItem(40651, 60) && pc.getInventory().checkItem(40643, 60)
                            && pc.getInventory().checkItem(40645, 60) && pc.getInventory().checkItem(40618, 60)
                            && pc.getInventory().checkItem(40676, 60) || pc.getInventory().checkItem(40961, 9)
                            || pc.getInventory().checkItem(40960, 9) || pc.getInventory().checkItem(40962, 9)
                            || pc.getInventory().checkItem(40959, 9) || pc.getInventory().checkItem(40638, 90)
                            || pc.getInventory().checkItem(40635, 90) || pc.getInventory().checkItem(40667, 90)
                            || pc.getInventory().checkItem(40642, 90)

                            || pc.getInventory().checkItem(21016, 1)

                    ) {
                        supplyEnchant(pc, 21017, 1, 0);
                        pc.getInventory().consumeItem(40651, 60);
                        pc.getInventory().consumeItem(40643, 60);
                        pc.getInventory().consumeItem(40645, 60);
                        pc.getInventory().consumeItem(40618, 60);
                        pc.getInventory().consumeItem(40676, 60);
                        pc.getInventory().consumeItem(40961, 9);
                        pc.getInventory().consumeItem(40960, 9);
                        pc.getInventory().consumeItem(40962, 9);
                        pc.getInventory().consumeItem(40959, 9);
                        pc.getInventory().consumeItem(40638, 90);
                        pc.getInventory().consumeItem(40635, 90);
                        pc.getInventory().consumeItem(40667, 90);
                        pc.getInventory().consumeItem(40642, 90);
                        pc.getInventory().consumeItem(21016, 1);
                    }

                } else if (itemtype == 2619) { // ドゥスル
                    if (pc.getInventory().checkAttrEnchantItem(1121, 10, 3, 1) && pc.getInventory().checkItem(40346, 3)
                            && pc.getInventory().checkItem(40354, 3) && pc.getInventory().checkItem(40362, 3)
                            && pc.getInventory().checkItem(40370, 3) && pc.getInventory().checkItem(40308, 10000000)) {
                        pc.getInventory().consumeAttrItem(1121, 10, 3, 1);
                        pc.getInventory().consumeItem(40346, 3);
                        pc.getInventory().consumeItem(40354, 3);
                        pc.getInventory().consumeItem(40362, 3);
                        pc.getInventory().consumeItem(40370, 3);
                        pc.getInventory().consumeItem(40308, 10000000);
                        supplyEnchant(pc, 66, 1, 0);
                    }
                } else if (itemtype == 2626) {// ドゥスル+5
                    if (pc.getInventory().checkAttrEnchantItem(66, 4, 3, 1) && pc.getInventory().checkItem(40346, 1)
                            && pc.getInventory().checkItem(40354, 1) && pc.getInventory().checkItem(40362, 1)
                            && pc.getInventory().checkItem(40370, 1)) {
                        pc.getInventory().consumeAttrItem(66, 4, 3, 1);
                        pc.getInventory().consumeItem(40346, 1);
                        pc.getInventory().consumeItem(40354, 1);
                        pc.getInventory().consumeItem(40362, 1);
                        pc.getInventory().consumeItem(40370, 1);
                        supplyEnchant(pc, 66, 1, 5);
                    }
                } else if (itemtype == 2625) {// ドゥスル+4
                    if (pc.getInventory().checkAttrEnchantItem(66, 3, 3, 1) && pc.getInventory().checkItem(40346, 1)
                            && pc.getInventory().checkItem(40354, 1) && pc.getInventory().checkItem(40362, 1)
                            && pc.getInventory().checkItem(40370, 1)) {
                        pc.getInventory().consumeAttrItem(66, 3, 3, 1);
                        pc.getInventory().consumeItem(40346, 1);
                        pc.getInventory().consumeItem(40354, 1);
                        pc.getInventory().consumeItem(40362, 1);
                        pc.getInventory().consumeItem(40370, 1);
                        supplyEnchant(pc, 66, 1, 4);
                    }
                } else if (itemtype == 2624) {// ドゥスル+3
                    if (pc.getInventory().checkAttrEnchantItem(66, 2, 3, 1) && pc.getInventory().checkItem(40346, 1)
                            && pc.getInventory().checkItem(40354, 1) && pc.getInventory().checkItem(40362, 1)
                            && pc.getInventory().checkItem(40370, 1)) {
                        pc.getInventory().consumeAttrItem(66, 2, 3, 1);
                        pc.getInventory().consumeItem(40346, 1);
                        pc.getInventory().consumeItem(40354, 1);
                        pc.getInventory().consumeItem(40362, 1);
                        pc.getInventory().consumeItem(40370, 1);
                        supplyEnchant(pc, 66, 1, 3);
                    }
                } else if (itemtype == 2623) {// ドゥスル+2
                    if (pc.getInventory().checkAttrEnchantItem(66, 1, 3, 1) && pc.getInventory().checkItem(40346, 1)
                            && pc.getInventory().checkItem(40354, 1) && pc.getInventory().checkItem(40362, 1)
                            && pc.getInventory().checkItem(40370, 1)) {
                        pc.getInventory().consumeAttrItem(66, 1, 3, 1);
                        pc.getInventory().consumeItem(40346, 1);
                        pc.getInventory().consumeItem(40354, 1);
                        pc.getInventory().consumeItem(40362, 1);
                        pc.getInventory().consumeItem(40370, 1);
                        supplyEnchant(pc, 66, 1, 2);
                    }
                } else if (itemtype == 2622) {// ドゥスル+1
                    if (pc.getInventory().checkAttrEnchantItem(66, 0, 3, 1) && pc.getInventory().checkItem(40346, 1)
                            && pc.getInventory().checkItem(40354, 1) && pc.getInventory().checkItem(40362, 1)
                            && pc.getInventory().checkItem(40370, 1)) {
                        pc.getInventory().consumeAttrItem(66, 0, 3, 1);
                        pc.getInventory().consumeItem(40346, 1);
                        pc.getInventory().consumeItem(40354, 1);
                        pc.getInventory().consumeItem(40362, 1);
                        pc.getInventory().consumeItem(40370, 1);
                        supplyEnchant(pc, 66, 1, 1);
                    }

                } else if (itemtype == 3541) { // 3腕力成長
                    if (pc.getInventory().checkEnchantItem(900020, 3, 1)
                            && pc.getInventory().checkEnchantItem(222352, 3, 1)) {
                        supplyEnchant(pc, 232355, 1, 3);
                        pc.getInventory().consumeEnchantItem(900020, 3, 1);
                        pc.getInventory().consumeEnchantItem(222352, 3, 1);
                    }
                } else if (itemtype == 3542) { // 4腕力成長
                    if (pc.getInventory().checkEnchantItem(900020, 4, 1)
                            && pc.getInventory().checkEnchantItem(222352, 4, 1)) {
                        supplyEnchant(pc, 232355, 1, 4);
                        pc.getInventory().consumeEnchantItem(900020, 4, 1);
                        pc.getInventory().consumeEnchantItem(222352, 4, 1);
                    }
                } else if (itemtype == 3543) { // 5腕力成長
                    if (pc.getInventory().checkEnchantItem(900020, 5, 1)
                            && pc.getInventory().checkEnchantItem(222352, 5, 1)) {
                        supplyEnchant(pc, 232355, 1, 5);
                        pc.getInventory().consumeEnchantItem(900020, 5, 1);
                        pc.getInventory().consumeEnchantItem(222352, 5, 1);
                    }
                } else if (itemtype == 3544) { // 6腕力成長
                    if (pc.getInventory().checkEnchantItem(900020, 6, 1)
                            && pc.getInventory().checkEnchantItem(222352, 6, 1)) {
                        supplyEnchant(pc, 232355, 1, 6);
                        pc.getInventory().consumeEnchantItem(900020, 6, 1);
                        pc.getInventory().consumeEnchantItem(222352, 6, 1);
                    }
                } else if (itemtype == 3545) { // 7腕力成長
                    if (pc.getInventory().checkEnchantItem(900020, 7, 1)
                            && pc.getInventory().checkEnchantItem(222352, 7, 1)) {
                        supplyEnchant(pc, 232355, 1, 7);
                        pc.getInventory().consumeEnchantItem(900020, 7, 1);
                        pc.getInventory().consumeEnchantItem(222352, 7, 1);
                    }
                } else if (itemtype == 3546) { // 8腕力成長
                    if (pc.getInventory().checkEnchantItem(900020, 8, 1)
                            && pc.getInventory().checkEnchantItem(222352, 8, 1)) {
                        supplyEnchant(pc, 232355, 1, 8);
                        pc.getInventory().consumeEnchantItem(900020, 8, 1);
                        pc.getInventory().consumeEnchantItem(222352, 8, 1);
                    }
                } else if (itemtype == 3547) { // 9腕力成長
                    if (pc.getInventory().checkEnchantItem(900020, 9, 1)
                            && pc.getInventory().checkEnchantItem(222352, 9, 1)) {
                        supplyEnchant(pc, 232355, 1, 9);
                        pc.getInventory().consumeEnchantItem(900020, 9, 1);
                        pc.getInventory().consumeEnchantItem(222352, 9, 1);
                    }
                } else if (itemtype == 3548) { // 10腕力成長
                    if (pc.getInventory().checkEnchantItem(900020, 10, 1)
                            && pc.getInventory().checkEnchantItem(222352, 10, 1)) {
                        supplyEnchant(pc, 232355, 1, 10);
                        pc.getInventory().consumeEnchantItem(900020, 10, 1);
                        pc.getInventory().consumeEnchantItem(222352, 10, 1);
                    }

                } else if (itemtype == 3549) { // 3アジャイル成長
                    if (pc.getInventory().checkEnchantItem(900020, 3, 1)
                            && pc.getInventory().checkEnchantItem(222353, 3, 1)) {
                        supplyEnchant(pc, 232356, 1, 3);
                        pc.getInventory().consumeEnchantItem(900020, 3, 1);
                        pc.getInventory().consumeEnchantItem(222353, 3, 1);
                    }
                } else if (itemtype == 3550) { // 4アジャイル成長
                    if (pc.getInventory().checkEnchantItem(900020, 4, 1)
                            && pc.getInventory().checkEnchantItem(222353, 4, 1)) {
                        supplyEnchant(pc, 232356, 1, 4);
                        pc.getInventory().consumeEnchantItem(900020, 4, 1);
                        pc.getInventory().consumeEnchantItem(222353, 4, 1);
                    }
                } else if (itemtype == 3551) { // 5アジャイル成長
                    if (pc.getInventory().checkEnchantItem(900020, 5, 1)
                            && pc.getInventory().checkEnchantItem(222353, 5, 1)) {
                        supplyEnchant(pc, 232356, 1, 5);
                        pc.getInventory().consumeEnchantItem(900020, 5, 1);
                        pc.getInventory().consumeEnchantItem(222353, 5, 1);
                    }
                } else if (itemtype == 3552) { // 6アジャイル成長
                    if (pc.getInventory().checkEnchantItem(900020, 6, 1)
                            && pc.getInventory().checkEnchantItem(222353, 6, 1)) {
                        supplyEnchant(pc, 232356, 1, 6);
                        pc.getInventory().consumeEnchantItem(900020, 6, 1);
                        pc.getInventory().consumeEnchantItem(222353, 6, 1);
                    }
                } else if (itemtype == 3553) { // 7速成長
                    if (pc.getInventory().checkEnchantItem(900020, 7, 1)
                            && pc.getInventory().checkEnchantItem(222353, 7, 1)) {
                        supplyEnchant(pc, 232356, 1, 7);
                        pc.getInventory().consumeEnchantItem(900020, 7, 1);
                        pc.getInventory().consumeEnchantItem(222353, 7, 1);
                    }
                } else if (itemtype == 3554) { // 8アジャイル成長
                    if (pc.getInventory().checkEnchantItem(900020, 8, 1)
                            && pc.getInventory().checkEnchantItem(222353, 8, 1)) {
                        supplyEnchant(pc, 232356, 1, 8);
                        pc.getInventory().consumeEnchantItem(900020, 8, 1);
                        pc.getInventory().consumeEnchantItem(222353, 8, 1);
                    }
                } else if (itemtype == 3555) { // 9アジャイル成長
                    if (pc.getInventory().checkEnchantItem(900020, 9, 1)
                            && pc.getInventory().checkEnchantItem(222353, 9, 1)) {
                        supplyEnchant(pc, 232356, 1, 9);
                        pc.getInventory().consumeEnchantItem(900020, 9, 1);
                        pc.getInventory().consumeEnchantItem(222353, 9, 1);
                    }
                } else if (itemtype == 3556) { // 10機敏成長
                    if (pc.getInventory().checkEnchantItem(900020, 10, 1)
                            && pc.getInventory().checkEnchantItem(222353, 10, 1)) {
                        supplyEnchant(pc, 232356, 1, 10);
                        pc.getInventory().consumeEnchantItem(900020, 10, 1);
                        pc.getInventory().consumeEnchantItem(222353, 10, 1);
                    }

                } else if (itemtype == 3557) { // 3知識成長
                    if (pc.getInventory().checkEnchantItem(900020, 3, 1)
                            && pc.getInventory().checkEnchantItem(222354, 3, 1)) {
                        supplyEnchant(pc, 232356, 1, 3);
                        pc.getInventory().consumeEnchantItem(900020, 3, 1);
                        pc.getInventory().consumeEnchantItem(222354, 3, 1);
                    }
                } else if (itemtype == 3558) { // 4知識成長
                    if (pc.getInventory().checkEnchantItem(900020, 4, 1)
                            && pc.getInventory().checkEnchantItem(222354, 4, 1)) {
                        supplyEnchant(pc, 232356, 1, 4);
                        pc.getInventory().consumeEnchantItem(900020, 4, 1);
                        pc.getInventory().consumeEnchantItem(222354, 4, 1);
                    }
                } else if (itemtype == 3559) { // 5知識成長
                    if (pc.getInventory().checkEnchantItem(900020, 5, 1)
                            && pc.getInventory().checkEnchantItem(222354, 5, 1)) {
                        supplyEnchant(pc, 232356, 1, 5);
                        pc.getInventory().consumeEnchantItem(900020, 5, 1);
                        pc.getInventory().consumeEnchantItem(222354, 5, 1);
                    }
                } else if (itemtype == 3560) { // 6知識成長
                    if (pc.getInventory().checkEnchantItem(900020, 6, 1)
                            && pc.getInventory().checkEnchantItem(222354, 6, 1)) {
                        supplyEnchant(pc, 232356, 1, 6);
                        pc.getInventory().consumeEnchantItem(900020, 6, 1);
                        pc.getInventory().consumeEnchantItem(222354, 6, 1);
                    }
                } else if (itemtype == 3561) { // 7知識成長
                    if (pc.getInventory().checkEnchantItem(900020, 7, 1)
                            && pc.getInventory().checkEnchantItem(222354, 7, 1)) {
                        supplyEnchant(pc, 232356, 1, 7);
                        pc.getInventory().consumeEnchantItem(900020, 7, 1);
                        pc.getInventory().consumeEnchantItem(222354, 7, 1);
                    }
                } else if (itemtype == 3562) { // 8知識成長
                    if (pc.getInventory().checkEnchantItem(900020, 8, 1)
                            && pc.getInventory().checkEnchantItem(222354, 8, 1)) {
                        supplyEnchant(pc, 232356, 1, 8);
                        pc.getInventory().consumeEnchantItem(900020, 8, 1);
                        pc.getInventory().consumeEnchantItem(222354, 8, 1);
                    }
                } else if (itemtype == 3563) { // 9知識成長
                    if (pc.getInventory().checkEnchantItem(900020, 9, 1)
                            && pc.getInventory().checkEnchantItem(222354, 9, 1)) {
                        supplyEnchant(pc, 232356, 1, 9);
                        pc.getInventory().consumeEnchantItem(900020, 9, 1);
                        pc.getInventory().consumeEnchantItem(222354, 9, 1);
                    }
                } else if (itemtype == 3564) { // 10知識成長
                    if (pc.getInventory().checkEnchantItem(900020, 10, 1)
                            && pc.getInventory().checkEnchantItem(222354, 10, 1)) {
                        supplyEnchant(pc, 232356, 1, 10);
                        pc.getInventory().consumeEnchantItem(900020, 10, 1);
                        pc.getInventory().consumeEnchantItem(222354, 10, 1);
                    }

                } else if (itemtype == 3566) { // 3腕力回復
                    if (pc.getInventory().checkEnchantItem(900021, 3, 1)
                            && pc.getInventory().checkEnchantItem(222352, 3, 1)) {
                        supplyEnchant(pc, 232356, 1, 3);
                        pc.getInventory().consumeEnchantItem(900020, 3, 1);
                        pc.getInventory().consumeEnchantItem(222354, 3, 1);
                    }
                } else if (itemtype == 3567) { // 4腕力回復
                    if (pc.getInventory().checkEnchantItem(900021, 4, 1)
                            && pc.getInventory().checkEnchantItem(222352, 4, 1)) {
                        supplyEnchant(pc, 232356, 1, 4);
                        pc.getInventory().consumeEnchantItem(900020, 4, 1);
                        pc.getInventory().consumeEnchantItem(222354, 4, 1);
                    }
                } else if (itemtype == 3568) { // 5腕力回復
                    if (pc.getInventory().checkEnchantItem(900021, 5, 1)
                            && pc.getInventory().checkEnchantItem(222352, 5, 1)) {
                        supplyEnchant(pc, 232356, 1, 5);
                        pc.getInventory().consumeEnchantItem(900020, 5, 1);
                        pc.getInventory().consumeEnchantItem(222354, 5, 1);
                    }
                } else if (itemtype == 3569) { // 6腕力回復
                    if (pc.getInventory().checkEnchantItem(900021, 6, 1)
                            && pc.getInventory().checkEnchantItem(222352, 6, 1)) {
                        supplyEnchant(pc, 232356, 1, 6);
                        pc.getInventory().consumeEnchantItem(900020, 6, 1);
                        pc.getInventory().consumeEnchantItem(222354, 6, 1);
                    }
                } else if (itemtype == 3570) { // 7腕力回復
                    if (pc.getInventory().checkEnchantItem(900021, 7, 1)
                            && pc.getInventory().checkEnchantItem(222352, 7, 1)) {
                        supplyEnchant(pc, 232356, 1, 7);
                        pc.getInventory().consumeEnchantItem(900020, 7, 1);
                        pc.getInventory().consumeEnchantItem(222354, 7, 1);
                    }
                } else if (itemtype == 3571) { // 8腕力回復
                    if (pc.getInventory().checkEnchantItem(900021, 8, 1)
                            && pc.getInventory().checkEnchantItem(222352, 8, 1)) {
                        supplyEnchant(pc, 232356, 1, 8);
                        pc.getInventory().consumeEnchantItem(900020, 8, 1);
                        pc.getInventory().consumeEnchantItem(222354, 8, 1);
                    }
                } else if (itemtype == 3572) { // 9腕力回復
                    if (pc.getInventory().checkEnchantItem(900021, 9, 1)
                            && pc.getInventory().checkEnchantItem(222352, 9, 1)) {
                        supplyEnchant(pc, 232356, 1, 9);
                        pc.getInventory().consumeEnchantItem(900020, 9, 1);
                        pc.getInventory().consumeEnchantItem(222354, 9, 1);
                    }
                } else if (itemtype == 3573) { // 10腕力回復
                    if (pc.getInventory().checkEnchantItem(900021, 10, 1)
                            && pc.getInventory().checkEnchantItem(222352, 10, 1)) {
                        supplyEnchant(pc, 232356, 1, 10);
                        pc.getInventory().consumeEnchantItem(900020, 10, 1);
                        pc.getInventory().consumeEnchantItem(222354, 10, 1);
                    }

                } else if (itemtype == 3574) { // 3アジャイル回復
                    if (pc.getInventory().checkEnchantItem(900021, 3, 1)
                            && pc.getInventory().checkEnchantItem(222353, 3, 1)) {
                        supplyEnchant(pc, 232356, 1, 3);
                        pc.getInventory().consumeEnchantItem(900020, 3, 1);
                        pc.getInventory().consumeEnchantItem(222354, 3, 1);
                    }
                } else if (itemtype == 3575) { // 4アジャイル回復
                    if (pc.getInventory().checkEnchantItem(900021, 4, 1)
                            && pc.getInventory().checkEnchantItem(222353, 4, 1)) {
                        supplyEnchant(pc, 232356, 1, 4);
                        pc.getInventory().consumeEnchantItem(900020, 4, 1);
                        pc.getInventory().consumeEnchantItem(222354, 4, 1);
                    }
                } else if (itemtype == 3576) { // 5アジャイル回復
                    if (pc.getInventory().checkEnchantItem(900021, 5, 1)
                            && pc.getInventory().checkEnchantItem(222353, 5, 1)) {
                        supplyEnchant(pc, 232356, 1, 5);
                        pc.getInventory().consumeEnchantItem(900020, 5, 1);
                        pc.getInventory().consumeEnchantItem(222354, 5, 1);
                    }
                } else if (itemtype == 3577) { // 6速回復
                    if (pc.getInventory().checkEnchantItem(900021, 6, 1)
                            && pc.getInventory().checkEnchantItem(222353, 6, 1)) {
                        supplyEnchant(pc, 232356, 1, 6);
                        pc.getInventory().consumeEnchantItem(900020, 6, 1);
                        pc.getInventory().consumeEnchantItem(222354, 6, 1);
                    }
                } else if (itemtype == 3578) { // 7速回復
                    if (pc.getInventory().checkEnchantItem(900021, 7, 1)
                            && pc.getInventory().checkEnchantItem(222353, 7, 1)) {
                        supplyEnchant(pc, 232356, 1, 7);
                        pc.getInventory().consumeEnchantItem(900020, 7, 1);
                        pc.getInventory().consumeEnchantItem(222354, 7, 1);
                    }
                } else if (itemtype == 3579) { // 8機敏回復
                    if (pc.getInventory().checkEnchantItem(900021, 8, 1)
                            && pc.getInventory().checkEnchantItem(222353, 8, 1)) {
                        supplyEnchant(pc, 232356, 1, 8);
                        pc.getInventory().consumeEnchantItem(900020, 8, 1);
                        pc.getInventory().consumeEnchantItem(222354, 8, 1);
                    }
                } else if (itemtype == 3580) { // 9機敏回復
                    if (pc.getInventory().checkEnchantItem(900021, 9, 1)
                            && pc.getInventory().checkEnchantItem(222353, 9, 1)) {
                        supplyEnchant(pc, 232356, 1, 9);
                        pc.getInventory().consumeEnchantItem(900020, 9, 1);
                        pc.getInventory().consumeEnchantItem(222354, 9, 1);
                    }
                } else if (itemtype == 3581) { // 10機敏回復
                    if (pc.getInventory().checkEnchantItem(900021, 10, 1)
                            && pc.getInventory().checkEnchantItem(222353, 10, 1)) {
                        supplyEnchant(pc, 232356, 1, 10);
                        pc.getInventory().consumeEnchantItem(900020, 10, 1);
                        pc.getInventory().consumeEnchantItem(222354, 10, 1);
                    }

                } else if (itemtype == 3582) { // 3知識回復
                    if (pc.getInventory().checkEnchantItem(900021, 3, 1)
                            && pc.getInventory().checkEnchantItem(222354, 3, 1)) {
                        supplyEnchant(pc, 232356, 1, 3);
                        pc.getInventory().consumeEnchantItem(900020, 3, 1);
                        pc.getInventory().consumeEnchantItem(222354, 3, 1);
                    }
                } else if (itemtype == 3583) { // 4知識回復
                    if (pc.getInventory().checkEnchantItem(900021, 4, 1)
                            && pc.getInventory().checkEnchantItem(222354, 4, 1)) {
                        supplyEnchant(pc, 232356, 1, 4);
                        pc.getInventory().consumeEnchantItem(900020, 4, 1);
                        pc.getInventory().consumeEnchantItem(222354, 4, 1);
                    }
                } else if (itemtype == 3584) { // 5知識回復
                    if (pc.getInventory().checkEnchantItem(900021, 5, 1)
                            && pc.getInventory().checkEnchantItem(222354, 5, 1)) {
                        supplyEnchant(pc, 232356, 1, 5);
                        pc.getInventory().consumeEnchantItem(900020, 5, 1);
                        pc.getInventory().consumeEnchantItem(222354, 5, 1);
                    }
                } else if (itemtype == 3585) { // 6知識回復
                    if (pc.getInventory().checkEnchantItem(900021, 6, 1)
                            && pc.getInventory().checkEnchantItem(222354, 6, 1)) {
                        supplyEnchant(pc, 232356, 1, 6);
                        pc.getInventory().consumeEnchantItem(900020, 6, 1);
                        pc.getInventory().consumeEnchantItem(222354, 6, 1);
                    }
                } else if (itemtype == 3586) { // 7知識回復
                    if (pc.getInventory().checkEnchantItem(900021, 7, 1)
                            && pc.getInventory().checkEnchantItem(222354, 3, 1)) {
                        supplyEnchant(pc, 232356, 1, 7);
                        pc.getInventory().consumeEnchantItem(900020, 7, 1);
                        pc.getInventory().consumeEnchantItem(222354, 7, 1);
                    }
                } else if (itemtype == 3587) { // 8知識回復
                    if (pc.getInventory().checkEnchantItem(900021, 8, 1)
                            && pc.getInventory().checkEnchantItem(222354, 8, 1)) {
                        supplyEnchant(pc, 232356, 1, 8);
                        pc.getInventory().consumeEnchantItem(900020, 8, 1);
                        pc.getInventory().consumeEnchantItem(222354, 8, 1);
                    }
                } else if (itemtype == 3588) { // 9知識回復
                    if (pc.getInventory().checkEnchantItem(900021, 9, 1)
                            && pc.getInventory().checkEnchantItem(222354, 9, 1)) {
                        supplyEnchant(pc, 232356, 1, 9);
                        pc.getInventory().consumeEnchantItem(900020, 9, 1);
                        pc.getInventory().consumeEnchantItem(222354, 9, 1);
                    }
                } else if (itemtype == 3589) { // 10知識回復
                    if (pc.getInventory().checkEnchantItem(900021, 10, 1)
                            && pc.getInventory().checkEnchantItem(222354, 10, 1)) {
                        supplyEnchant(pc, 232356, 1, 10);
                        pc.getInventory().consumeEnchantItem(900020, 10, 1);
                        pc.getInventory().consumeEnchantItem(222354, 10, 1);
                    }

                } else if (itemtype == 3590) { // 3回復成長
                    if (pc.getInventory().checkEnchantItem(900021, 3, 1)
                            && pc.getInventory().checkEnchantItem(900020, 3, 1)) {
                        supplyEnchant(pc, 232356, 1, 3);
                        pc.getInventory().consumeEnchantItem(900020, 3, 1);
                        pc.getInventory().consumeEnchantItem(900020, 3, 1);
                    }
                } else if (itemtype == 3591) { // 4回復成長
                    if (pc.getInventory().checkEnchantItem(900021, 4, 1)
                            && pc.getInventory().checkEnchantItem(900020, 4, 1)) {
                        supplyEnchant(pc, 232356, 1, 4);
                        pc.getInventory().consumeEnchantItem(900020, 4, 1);
                        pc.getInventory().consumeEnchantItem(900020, 4, 1);
                    }
                } else if (itemtype == 3592) { // 5回復成長
                    if (pc.getInventory().checkEnchantItem(900021, 5, 1)
                            && pc.getInventory().checkEnchantItem(900020, 5, 1)) {
                        supplyEnchant(pc, 232356, 1, 5);
                        pc.getInventory().consumeEnchantItem(900020, 5, 1);
                        pc.getInventory().consumeEnchantItem(900020, 5, 1);
                    }
                } else if (itemtype == 3593) { // 6回復成長
                    if (pc.getInventory().checkEnchantItem(900021, 6, 1)
                            && pc.getInventory().checkEnchantItem(900020, 6, 1)) {
                        supplyEnchant(pc, 232356, 1, 6);
                        pc.getInventory().consumeEnchantItem(900020, 6, 1);
                        pc.getInventory().consumeEnchantItem(900020, 6, 1);
                    }
                } else if (itemtype == 3594) { // 7回復成長
                    if (pc.getInventory().checkEnchantItem(900021, 7, 1)
                            && pc.getInventory().checkEnchantItem(900020, 7, 1)) {
                        supplyEnchant(pc, 232356, 1, 7);
                        pc.getInventory().consumeEnchantItem(900020, 7, 1);
                        pc.getInventory().consumeEnchantItem(900020, 7, 1);
                    }
                } else if (itemtype == 3595) { // 8回復成長
                    if (pc.getInventory().checkEnchantItem(900021, 8, 1)
                            && pc.getInventory().checkEnchantItem(900020, 8, 1)) {
                        supplyEnchant(pc, 232356, 1, 8);
                        pc.getInventory().consumeEnchantItem(900020, 8, 1);
                        pc.getInventory().consumeEnchantItem(900020, 8, 1);
                    }
                } else if (itemtype == 3596) { // 9回復成長
                    if (pc.getInventory().checkEnchantItem(900021, 9, 1)
                            && pc.getInventory().checkEnchantItem(900020, 9, 1)) {
                        supplyEnchant(pc, 232356, 1, 9);
                        pc.getInventory().consumeEnchantItem(900020, 9, 1);
                        pc.getInventory().consumeEnchantItem(900020, 9, 1);
                    }
                } else if (itemtype == 3597) { // 10回復成長
                    if (pc.getInventory().checkEnchantItem(900021, 10, 1)
                            && pc.getInventory().checkEnchantItem(900020, 10, 1)) {
                        supplyEnchant(pc, 232356, 1, 10);
                        pc.getInventory().consumeEnchantItem(900020, 10, 1);
                        pc.getInventory().consumeEnchantItem(900020, 10, 1);
                    }

                } else if (itemtype == 2871) { // +3成長文章
                    if (pc.getInventory().checkEnchantItem(3000099, 0, 6)) {
                        supplyEnchant(pc, 900020, 1, 3);
                        pc.getInventory().consumeEnchantItem(3000099, 0, 6);
                    }
                } else if (itemtype == 2872) { // +3回復文章
                    if (pc.getInventory().checkEnchantItem(3000098, 0, 6)) {
                        supplyEnchant(pc, 900021, 1, 3);
                        pc.getInventory().consumeEnchantItem(3000098, 0, 6);
                    }
                } else if (itemtype == 2873) { // +3腕力文章
                    if (pc.getInventory().checkEnchantItem(61523, 0, 6)) {
                        supplyEnchant(pc, 222352, 1, 3);
                        pc.getInventory().consumeEnchantItem(61523, 0, 6);
                    }
                } else if (itemtype == 2874) { // +3機敏文章
                    if (pc.getInventory().checkEnchantItem(61524, 0, 6)) {
                        supplyEnchant(pc, 222353, 1, 3);
                        pc.getInventory().consumeEnchantItem(61524, 0, 6);
                    }
                } else if (itemtype == 2875) { // +3知識の文章
                    if (pc.getInventory().checkEnchantItem(61525, 0, 6)) {
                        supplyEnchant(pc, 222352, 1, 3);
                        pc.getInventory().consumeEnchantItem(61525, 0, 6);
                    }

                }

            } else {
                pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.CRAFT_OK));
                return;
            }
            action = NpcActionTable.getInstance().get(s, pc, npc);

            if (action != null) {
                L1NpcHtml result = action.executeWithAmount(s, pc, npc, itemcount);
                if (result != null) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), result));
                } else {
                    pc.sendPackets(new S_SystemMessage("その製作アイテムはサーバーに準備ができていません。"));
                }
                pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.CRAFT_OK));
                pc.sendPackets(new S_SkillSound(pc.getId(), 7976));
                pc.broadcastPacket(new S_SkillSound(pc.getId(), 7976));
            }
            break;
        case 0x0146: // 血盟加入申請受信設定
            if (pc.getClanid() == 0 || (!pc.isCrown() && pc.getClanRank() != L1Clan.GUARDIAN))
                return;
            readC();
            readH();
            int setting = readC();
            readC();
            int setting2 = readC();
            if (setting2 == 2) {
                pc.sendPackets(new S_SystemMessage("現在のパスワード登録のタイプに設定することができません。"), true);
                setting2 = 1;
            }

            pc.getClan().setJoinSetting(setting);
            pc.getClan().setJoinType(setting2);
            pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.CLAN_JOIN_SETTING, setting, setting2), true);
            ClanTable.getInstance().updateClan(pc.getClan());
            pc.sendPackets(new S_ServerMessage(3980), true);
            break;
        case 0x014C: // 血盟募集セッティング
            if (pc.getClanid() == 0)
                return;
            pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.CLAN_JOIN_SETTING, pc.getClan().getJoinSetting(),
                    pc.getClan().getJoinType()), true);
            break;
        case 322: // 血盟加入
        {
            readC();
            readH();
            int length = readC();
            byte[] BYTE = readByte();

            // 存在しない血盟です。
            if (pc.isCrown()) {
                pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.CLAN_JOIN_MESSAGE, 4), true);
                break;
            }

            // すでに血盟に加入した状態です。
            if (pc.getClanid() != 0) {
                pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.CLAN_JOIN_MESSAGE, 9), true);
                break;
            }

            // 君主に会って登録してください。
            try {
                String clanname = new String(BYTE, 0, length, "MS932");
                L1Clan clan = L1World.getInstance().getClan(clanname);
                //
                if (clan == null) {
                    pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.CLAN_JOIN_MESSAGE, 13), true);
                    break;
                }
                //
                L1PcInstance crown = clan.getOnlineLeaders();
                if (crown == null) {
                    pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.CLAN_JOIN_MESSAGE, 11), true);
                    break;
                }
                //
                if (clan.getJoinSetting() == 0) {
                    pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.CLAN_JOIN_MESSAGE, 8), true);

                } else if (clan.getJoinType() == 0) {
                    L1ClanJoin.getInstance().ClanJoin(crown, pc);
                    pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.CLAN_JOIN_MESSAGE, 0), true);
                } else {
                    pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.CLAN_JOIN_MESSAGE, 13), true);
                }
            } catch (Exception e) {
            }
            break;
        }
        case PORTAL_SETTING: // パーティー標識設定
            int size = readH();
            byte[] flag = new byte[size];
            for (int i = 0; i < size; ++i)
                flag[i] = (byte) readC();
            //
            L1Party party = pc.getParty();
            if (party == null)
                return;
            //
            for (L1PcInstance member : party.getMembers())
                member.sendPackets(new S_ACTION_UI(flag));
            break;
        case ACCOUNT_TAM_UPDATE:// タムチャンオフセット
            // pc.sendPackets(new S_TamWindow(pc.getAccountName()));
            break;
        case ACCOUNT_TAM:// タムチャンオフセット
            pc.sendPackets(new S_TamWindow(pc.getAccountName()));
            break;
        case ACCOUNT_TAM_CANCEL:// 乗車
            readC();
            readH();
            byte[] BYTE = readByte();
            byte[] temp = new byte[BYTE.length - 1];
            for (int i = 0; i < temp.length; i++) {
                temp[i] = BYTE[i];
            }
            StringBuffer sb = new StringBuffer();
            for (byte zzz : temp) {
                sb.append(String.valueOf(zzz));
            }
            int day = Nexttam(sb.toString());
            int charobjid = TamCharid(sb.toString());
            if (charobjid != pc.getId()) {
                pc.sendPackets(new S_SystemMessage("通知：このキャラクターのみキャンセルをすることができます。"));
                return;
            }
            int itemid = 0;
            if (day != 0) {
                if (day == 3) {// 期間3日
                    itemid = 600226;
                } else if (day == 30) {// 期間30日
                    itemid = 600227;
                }
                L1ItemInstance item = pc.getInventory().storeItem(itemid, 1);
                if (item != null) {
                    pc.sendPackets(new S_ServerMessage(403, item.getName() + " (1)"));
                    tamcancle(sb.toString());
                    pc.sendPackets(new S_TamWindow(pc.getAccountName()));
                }
            }
            break;
        case ACTION: // ソーシャルアクション
            readD();
            readC();
            int action1 = readC();
            if (action1 >= 1 && action1 <= 11) {
                pc.sendPackets(new S_ACTION_UI2(S_ACTION_UI2.EMOTICON, action1, pc.getId()), true);
                Broadcaster.broadcastPacket(pc, new S_ACTION_UI2(S_ACTION_UI2.EMOTICON, action1, pc.getId()), true);
            }
            break;
        case SIEGE_RELATION:
            try {
                readH();
                readC();
                int castleType = readC();
                // 1ケント2ギラン4オーク要塞
                for (L1Clan cc : L1World.getInstance().getAllClans()) {
                    if (castleType == cc.getCastleId()) {
                        s = cc.getClanName();
                        break;
                    }
                }

                if (s.equalsIgnoreCase("")) {
                    return;
                }

                L1PcInstance player = pc;
                String clanName = player.getClanname();
                int clanId = player.getClanid();

                if (!player.isCrown()) { // 君主以外
                    S_ServerMessage sm = new S_ServerMessage(478);
                    player.sendPackets(sm, true);
                    return;
                }
                if (clanId == 0) { // クラン笑顔の中
                    S_ServerMessage sm = new S_ServerMessage(272);
                    player.sendPackets(sm, true);
                    return;
                }
                L1Clan clan = L1World.getInstance().getClan(clanName);
                if (clan == null) { // ジャックとは、この検出されない
                    S_SystemMessage sm = new S_SystemMessage("対象血盟が見つかりません。");
                    player.sendPackets(sm, true);
                    return;
                }
                if (player.getId() != clan.getLeaderId()) { // 血盟主
                    S_ServerMessage sm = new S_ServerMessage(478);
                    player.sendPackets(sm, true);
                    return;
                }
                if (clanName.toLowerCase().equals(s.toLowerCase())) { // ジャックとを指定
                    S_SystemMessage sm = new S_SystemMessage("自分の血に攻城宣言はできません。");
                    player.sendPackets(sm, true);
                    return;
                }
                L1Clan enemyClan = null;
                String enemyClanName = null;
                for (L1Clan checkClan : L1World.getInstance().getAllClans()) { // クラン名をチェック
                    if (checkClan.getClanName().toLowerCase().equals(s.toLowerCase())) {
                        enemyClan = checkClan;
                        enemyClanName = checkClan.getClanName();
                        break;
                    }
                }
                if (enemyClan == null) { // 相手クランが発見されなかった
                    S_SystemMessage sm = new S_SystemMessage("対象血盟が見つかりません。");
                    player.sendPackets(sm, true);
                    return;
                }
                // if (clan.getAlliance() == enemyClan.getAlliance()) {
                // S_ServerMessage sm = new S_ServerMessage(1205);
                // player.sendPackets(sm, true);
                // return;
                // }
                List<L1War> warList = L1World.getInstance().getWarList(); // 戦争のリストを取得
                if (clan.getCastleId() != 0) { // ジャックとは城主
                    S_ServerMessage sm = new S_ServerMessage(474);
                    player.sendPackets(sm, true);
                    return;
                }
                if (enemyClan.getCastleId() != 0 && player.getLevel() < Config.DECLARATION_LEVEL) {
                    player.sendPackets(new S_SystemMessage("レベル" + Config.DECLARATION_LEVEL + "から宣言することができます。"));
                    return;
                }

                if (clan.getOnlineClanMember().length <= Config.CLAN_CONNECT_COUNT) {
                    player.sendPackets(new S_SystemMessage("接続した血盟員が" + Config.CLAN_CONNECT_COUNT + "人以上であれば宣言が可能です。"));
                    return;
                }

                /*
                 * if (clan.getHouseId() > 0) { S_SystemMessage sm = new
                 * S_SystemMessage("アジトがある状態では、宣戦布告をすることができません。 "）;
                 * player.sendPackets(sm, true); return; }
                 */
                if (enemyClan.getCastleId() != 0) { // 相手クランが城主
                    int castle_id = enemyClan.getCastleId();
                    if (WarTimeController.getInstance().isNowWar(castle_id)) { // 戦争の時間内
                        L1PcInstance clanMember[] = clan.getOnlineClanMember();
                        for (int k = 0; k < clanMember.length; k++) {
                            if (L1CastleLocation.checkInWarArea(castle_id, clanMember[k])) {
                                int[] loc = new int[3];
                                Random _rnd = new Random(System.nanoTime());
                                loc = L1CastleLocation.getGetBackLoc(castle_id);
                                int locx = loc[0] + (_rnd.nextInt(4) - 2);
                                int locy = loc[1] + (_rnd.nextInt(4) - 2);
                                short mapid = (short) loc[2];
                                new L1Teleport().teleport(clanMember[k], locx, locy, mapid, clanMember[k].getHeading(),
                                        true);
                            }
                        }
                        boolean enemyInWar = false;
                        for (L1War war : warList) {
                            if (war.CheckClanInWar(enemyClanName)) { // 相手クランが既に戦争中
                                war.DeclareWar(clanName, enemyClanName);
                                war.AddAttackClan(clanName);
                                enemyInWar = true;
                                break;
                            }
                        }
                        if (!enemyInWar) { // 相手クランが戦争中以外で、宣戦布告
                            L1War war = new L1War();
                            war.handleCommands(1, clanName, enemyClanName); // 包囲開始
                        }
                    } else { // 戦争時間外
                        S_ServerMessage sm = new S_ServerMessage(476);
                        player.sendPackets(sm, true); // まだ攻城戦の時間がありません。
                    }
                } else { // 相手クランが城主ではない
                    return;
                }
            } catch (Exception e) {
            } finally {
                clear();
            }
            break;
        default:
            // System.out.println(type);
            break;
        }
    }

    private boolean supplyEnchant(L1PcInstance pc, int item_id, int count, int EnchantLevel) {
        L1ItemInstance item = ItemTable.getInstance().createItem(item_id);
        if (item != null) {
            item.setCount(count);
            item.setEnchantLevel(EnchantLevel);
            item.setIdentified(true);
            if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
                pc.getInventory().storeItem(item);
            } else {
                pc.sendPackets(new S_ServerMessage(82));// 重量ゲージが不足したり、インベントリがいっぱいよりにできません。
                return false;
            }
            pc.sendPackets(new S_SystemMessage("アイテム製作に成功しました。"));
            pc.sendPackets(new S_ServerMessage(143, item.getLogName())); // %0を手に入れました。
            pc.sendPackets(new S_SkillSound(pc.getId(), 7976));
            pc.broadcastPacket(new S_SkillSound(pc.getId(), 7976));
            return true;
        } else {
            return false;
        }
    }

    public int Nexttam(String encobj) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        int day = 0;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT day FROM `tam` WHERE encobjid = ? order by id asc limit 1"); // キャラクターテーブルで君主だけを選んで来て
            pstm.setString(1, encobj);
            rs = pstm.executeQuery();
            while (rs.next()) {
                day = rs.getInt("Day");
            }
        } catch (SQLException e) {
            // _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return day;
    }

    public int TamCharid(String encobj) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        int objid = 0;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT objid FROM `tam` WHERE encobjid = ? order by id asc limit 1"); // キャラクターテーブルで君主だけを選んで来て
            pstm.setString(1, encobj);
            rs = pstm.executeQuery();
            while (rs.next()) {
                objid = rs.getInt("objid");
            }
        } catch (SQLException e) {
            // _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return objid;
    }

    public void tamcancle(String objectId) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("delete from tam where encobjid = ? order by id asc limit 1");
            pstm.setString(1, objectId);
            pstm.executeUpdate();
        } catch (SQLException e) {
            // _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    @Override
    public String getType() {
        return C_ACTION_UI;
    }

}
