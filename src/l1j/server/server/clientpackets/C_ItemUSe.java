package l1j.server.server.clientpackets;

import static l1j.server.server.model.skill.L1SkillId.*;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.SpecialEventHandler;
import l1j.server.GameSystem.valakas.ValaRaidSystem;
import l1j.server.IndunSystem.DragonRaid.Anta.AntarasRaidSystem;
import l1j.server.IndunSystem.DragonRaid.Fafu.FafurionRaidSystem;
import l1j.server.IndunSystem.DragonRaid.Rind.RindSystem;
import l1j.server.RobotSystem.RobotAIThread;
import l1j.server.server.ActionCodes;
import l1j.server.server.GameClient;
import l1j.server.server.IdFactory;
import l1j.server.server.Controller.CrockController;
import l1j.server.server.Controller.WarTimeController;
import l1j.server.server.datatables.ArmorEnchantList;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.datatables.FurnitureSpawnTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.LogEnchantTable;
import l1j.server.server.datatables.NpcSpawnTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.PetTable;
import l1j.server.server.datatables.PolyTable;
import l1j.server.server.datatables.ResolventTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.datatables.WeaponEnchantList;
import l1j.server.server.model.AcceleratorChecker;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.Getback;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Cooking;
import l1j.server.server.model.L1EffectSpawn;
import l1j.server.server.model.L1HouseLocation;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1ItemDelay;
import l1j.server.server.model.L1NpcDeleteTimer;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1PinkName;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1Quest;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1FurnitureInstance;
import l1j.server.server.model.Instance.L1GuardianInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.Instance.L1SupportInstance;
import l1j.server.server.model.Instance.L1TowerInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.item.L1TreasureBox;
import l1j.server.server.model.item.function.ChangeItem;
import l1j.server.server.model.item.function.Fishitem;
import l1j.server.server.model.item.function.L1HealingPotion;
import l1j.server.server.model.item.function.LeafItem;
import l1j.server.server.model.item.function.OmanAmulet;
import l1j.server.server.model.item.function.OmanRandomAmulet;
import l1j.server.server.model.item.function.RestoreItem;
import l1j.server.server.model.item.function.TelBook;
import l1j.server.server.model.item.function.Telbookitem;
import l1j.server.server.model.item.function.additem;
import l1j.server.server.model.item.function.omanTel;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.model.poison.L1DamagePoison;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.monitor.LoggerInstance;
import l1j.server.server.serverpackets.S_ACTION_UI;
import l1j.server.server.serverpackets.S_ACTION_UI2;
import l1j.server.server.serverpackets.S_AddSkill;
import l1j.server.server.serverpackets.S_AttackPacket;
import l1j.server.server.serverpackets.S_AttackStatus;
import l1j.server.server.serverpackets.S_Board;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_ClanName;
import l1j.server.server.serverpackets.S_CurseBlind;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_IdentifyDesc;
import l1j.server.server.serverpackets.S_ItemName;
import l1j.server.server.serverpackets.S_ItemStatus;
import l1j.server.server.serverpackets.S_Liquor;
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_MatizCloudia;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_NewCreateItem;
import l1j.server.server.serverpackets.S_NewSkillIcon;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_OwnCharStatus2;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_ReturnedStat;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_Serchdrop2;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_ShowPolyList;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillIconBlessOfEva;
import l1j.server.server.serverpackets.S_SkillIconGFX;
import l1j.server.server.serverpackets.S_SkillIconWisdomPotion;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_Sound;
import l1j.server.server.serverpackets.S_SummonPack;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_TamWindow;
import l1j.server.server.serverpackets.S_UseAttackSkill;
import l1j.server.server.templates.L1Armor;
import l1j.server.server.templates.L1BookMark;
import l1j.server.server.templates.L1EtcItem;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1Pet;
import l1j.server.server.templates.L1Skills;
import l1j.server.server.types.Point;
import l1j.server.server.utils.CommonUtil;
import l1j.server.server.utils.L1SpawnUtil;
import l1j.server.server.utils.SQLUtil;
import manager.LinAllManager;

public class C_ItemUSe extends ClientBasePacket {

    private static final String C_ITEM_USE = "[C] C_ItemUSe";
    private static Logger _log = Logger.getLogger(C_ItemUSe.class.getName());

    private static Random _random = new Random(System.nanoTime());

    private static final int[] robots = { -2, -1, 0, 1, 2 };

    private static boolean _randomInit = false;
    /**
     * 日付と時刻の記録
     **/
    Calendar rightNow = Calendar.getInstance();
    int day = rightNow.get(Calendar.DATE);
    int hour = rightNow.get(Calendar.HOUR);
    int min = rightNow.get(Calendar.MINUTE);
    int sec = rightNow.get(Calendar.SECOND);
    int year = rightNow.get(Calendar.YEAR);
    int month = rightNow.get(Calendar.MONTH) + 1;
    String totime = "[" + year + ":" + month + ":" + day + "]";
    String totime1 = "[" + hour + ":" + min + ":" + sec + "]";
    String date = +year + "_" + month + "_" + day;

    public C_ItemUSe(byte abyte0[], GameClient client) throws Exception {
        super(abyte0);
        int itemObjid = readD();

        L1PcInstance pc = client.getActiveChar();
        if (pc == null || pc.isGhost() || isTwoLogin(pc) || pc.getMapId() == 5166) {
            return;
        }

        if (!_randomInit) {// きれいな分布のためにいくつかの返す。
            for (int i = 0; i < 100000; ++i) {
                _random.nextInt(2000000000);
            }
            _randomInit = true;
        }

        L1ItemInstance l1iteminstance = pc.getInventory().getItem(itemObjid);

        if (l1iteminstance == null || l1iteminstance.getItem() == null) {
            return;
        }

        if (l1iteminstance.getItem().getUseType() == -1) { // none:使用できないアイテム
            pc.sendPackets(new S_ServerMessage(74, l1iteminstance.getLogName())); // \f1%0は使用できません。
            return;
        }

        int pcObjid = pc.getId();
        if (pc.isTeleport()) { // テレポート処理中
            return;
        }

        // 存在バグ関連を追加
        L1PcInstance jonje = L1World.getInstance().getPlayer(pc.getName());
        if (jonje == null && pc.getAccessLevel() != 200) {
            pc.sendPackets(new S_SystemMessage("存在バグ強制終了！再接続してください"));
            client.kick();
            return;
        }

        if (pc.isDead() == true) {
            return;
        }

        if (!pc.getMap().isUsableItem()) {
            pc.sendPackets(new S_ServerMessage(563)); // \f1ここでは、使用することができません。
            return;
        }
        int itemId;
        try {
            itemId = l1iteminstance.getItem().getItemId();
        } catch (Exception e) {
            return;
        }
        int l = 0;

        String s = "";

        int blanksc_skillid = 0;
        int spellsc_objid = 0;
        int spellsc_x = 0;
        int spellsc_y = 0;
        int resid = 0;
        int cookStatus = 0;
        int cookNo = 0;
        int fishX = 0;
        int fishY = 0;
        short bookmark_mapid = 0;
        int bookmark_x = 0;
        int bookmark_y = 0;
        int BookTel = 0;
        int use_objid = 0;

        int use_type = l1iteminstance.getItem().getUseType();

        /** 商品番号を追加しなければならない、または使用しない **/
        switch (itemId) {
            case 40088:
            case 40096:
            case 140088:
            case 210112:
                s = readS();
                break;
            case 40074:
            case 40087:
            case 40660:
            case 40128:
            case 40127:
            case 30027:
            case 30028:
            case 30068:
            case 30069:
            case 40077:
            case 40078:
            case 40126:
            case 40098:
            case 40129:
            case 40130:
            case 140129:
            case 140130:
            case 140074:
            case 140087:
            case 240074:
            case 240087:
            case 41029:
            case 40317:
            case 41036:
            case 41245:
            case 30087:
            case 210073:
            case 210077:
            case 500205:
            case 500207:
            case 210082:
            case 210084:
            case 210085:
            case 40964:
            case 41030:
            case 50020:
            case 50021:
            case 210064:
            case 210065:
            case 210066:
            case 210067:
            case 210068:
            case 41048:
            case 41049:
            case 41050:
            case 41051:
            case 41052:
            case 41053:
            case 41054:
            case 41055:
            case 41056:
            case 41057:
            case 40925:
            case 40926:
            case 40927:
            case 40928:
            case 40929:
            case 40931:
            case 40932:
            case 40933:
            case 40934:
            case 40935:
            case 40936:
            case 40937:
            case 40938:
            case 40939:
            case 40940:
            case 40941:
            case 40942:
            case 40943:
            case 40944:
            case 40945:
            case 40946:
            case 40947:
            case 40948:
            case 40949:
            case 40950:
            case 40951:
            case 40952:
            case 40953:
            case 40954:
            case 40955:
            case 40956:
            case 40957:
            case 40958:
            case 410016:
            case 410017:
            case 100001:
            case 1000021:
            case 1000022:
            case 1000023:
            case 1000024:
            case 1000025:
            case 1000026:
            case 1000027:
            case 1000028:
            case 1000029:
            case 1000030:
            case 1000031:
            case 1000032:
            case 1000033:
            case 1000034:
            case 1000035:
            case 1000036:
            case 1000037:
            case 1000038:
            case 1000039:
            case 1000040:
            case 1000012:
            case 410066:
            case 410067:
            case 410068:
            case 410083:
            case 410089:
            case 410094:
            case 30107:
            case 30108:
            case 30109:
            case 30110:
            case 30111:
            case 30112:
            case 30113:
            case 30114:
            case 30115:
            case 30116:
            case 30117:
            case 60035:
            case 68076:
            case 30146:
            case 68077:
            case 68078:
            case 68079:
            case 810003:
            case 127000:
            case 560030:
            case 560031:
            case 560032:
            case 560033:
            case 410140:
            case 410141:
            case 410142:
            case 810012:
            case 810013:
            case 7024:
            case 600228:
            case 3000065:
            case 3000100:// 文章強化石
            case 31086:
            case 30147:
            case 68080:
            case 68081:
            case 7011:
            case 7010:
            case 3000123:
            case 3000124:
            case 3000125:
            case 3000130:
            case 3000131:
            case 3000132:
            case 3000154:
                l = readD();
                break;
            case 40090:
            case 40091:
            case 40092:
            case 40093:
            case 40094:
                blanksc_skillid = readC();
                break;
            case 40870:
            case 40879:
                spellsc_objid = readD();
                break;
            case 40089:
            case 140089:
            case 30074:
                resid = readD();
                break;
            case 560025:
            case 560027:
            case 560028:
            case 560029:
                BookTel = readC();
                break;
            case 140100:
            case 40100:
            case 40099:
            case 40086:
            case 40863:
                bookmark_mapid = (short) readH();
                bookmark_x = readH();
                bookmark_y = readH();
                pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
                break;
            case 41293:
            case 41294:
            case 41305:
            case 41306:
            case 600229:
            case 9991:
                fishX = readH();
                fishY = readH();
                break;
            case 40008:
            case 140008:
                spellsc_objid = readD();
                s = readS();
                break;
            case 3000155: // 機器の修復
                RestoreItem.ClickItem(pc);
                break;
            case 3000156: // 機器変更
                l = readD();
                L1ItemInstance l1iteminstance1 = pc.getInventory().getItem(l); // ゲルデーをバーアイテム
                ChangeItem.ClickToItem(pc, l1iteminstance1);
                break;
            case 3000159: // メティススープ
            case 3000160:// メティスの料理
                L1Cooking.useCookingItem(pc, l1iteminstance);
                break;
            case 3000161:// メティスの祝福書
                SpecialEventHandler.getInstance().buff_ALL(pc);
                pc.getInventory().removeItem(l1iteminstance.getId(), 1);
                break;
            default:
                if ((use_type == 30)) { // spell_buff
                    spellsc_objid = readC();
                } else if (itemId == 600226 || itemId == 600227) {
                    use_objid = readD();
                } else if ((use_type == 5) || (use_type == 17)) { // spell_long、spell_short
                    spellsc_objid = readD();
                    spellsc_x = readH();
                    spellsc_y = readH();
                } else if ((itemId >= 41255) && (itemId <= 41259)) { // 料理本
                    cookStatus = readC();
                    cookNo = readC();
                } else {
                    l = readC();
                }
                break;
        }

        if (pc.getCurrentHp() > 0) {
            int delay_id = 0;
            if (l1iteminstance.getItem().getType2() == 0) { // 種別：その他のアイテム
                if (l1iteminstance.getItem() instanceof L1EtcItem) {
                    delay_id = ((L1EtcItem) l1iteminstance.getItem()).get_delayid();
                }
            }
            if (delay_id != 0) { // 遅延設定あり
                if (pc.hasItemDelay(delay_id) == true) {
                    return;
                }
            }
            // 再チェック
            boolean isDelayEffect = false;

            // チケットならリターン
            if (itemId >= 8000000 && itemId <= 9000000) {
                return;
            }

            if (l1iteminstance.getItem().getType2() == 0) {
                int delayEffect = ((L1EtcItem) l1iteminstance.getItem()).get_delayEffect();
                if (delayEffect > 0) {
                    isDelayEffect = true;
                    Timestamp lastUsed = l1iteminstance.getLastUsed();
                    if (lastUsed != null) {
                        Calendar cal = Calendar.getInstance();
                        if ((cal.getTimeInMillis() - lastUsed.getTime()) / 1000 <= delayEffect) {
                            pc.sendPackets(new S_SystemMessage(
                                    ((delayEffect - (cal.getTimeInMillis() - lastUsed.getTime()) / 1000) / 60) + "分"
                                            + ((delayEffect - (cal.getTimeInMillis() - lastUsed.getTime()) / 1000) % 60)
                                            + "秒後に使用することができます。"));
                            return;
                        }
                    }
                }
            }
            L1ItemInstance l1iteminstance1 = pc.getInventory().getItem(l); // ゲルデーをバーアイテム
            _log.finest("request item use (obj) = " + itemObjid + " action = " + l + " value = " + s);

            if (l1iteminstance.getItem().getType2() == 0) { // 種別：その他のアイテム
                int item_minlvl = ((L1EtcItem) l1iteminstance.getItem()).getMinLevel();
                int item_maxlvl = ((L1EtcItem) l1iteminstance.getItem()).getMaxLevel();
                if (item_minlvl != 0 && item_minlvl > pc.getLevel() && !pc.isGm()) {
                    pc.sendPackets(new S_SystemMessage(item_minlvl + "レベル以上になると使用することができるアイテムです。"));
                    pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
                    return;
                } else if (item_maxlvl != 0 && item_maxlvl < pc.getLevel() && !pc.isGm()) {
                    pc.sendPackets(new S_SystemMessage(item_maxlvl + "レベル以下にのみ使用できるアイテムです。"));
                    pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
                    return;
                }
                if ((itemId == 40576 && !pc.isElf()) || (itemId == 40577 && !pc.isWizard()) // 魂の結晶の破片（黒）
                        || (itemId == 40578 && !pc.isKnight())) { // 魂の結晶の破片（赤）
                    pc.sendPackets(new S_ServerMessage(264)); // \f1あなたのクラスでは、このアイテムは使用できません。
                    return;
                }

                if (l1iteminstance.getItem().getType() == 0) { // アロー
                    pc.getInventory().setArrow(l1iteminstance.getItem().getItemId());
                    pc.sendPackets(new S_ServerMessage(452, l1iteminstance.getLogName())); // %0が選択された。
                } else if (l1iteminstance.getItem().getType() == 15) { // スティング
                    pc.getInventory().setSting(l1iteminstance.getItem().getItemId());
                    pc.sendPackets(new S_ServerMessage(452, l1iteminstance.getLogName()));
                } else if (l1iteminstance.getItem().getType() == 16) { // treasure_box
                    L1TreasureBox box = L1TreasureBox.get(itemId);
                    if (pc.getInventory().getSize() > 170) {
                        pc.sendPackets(new S_ChatPacket(pc, "所持しているアイテムが多すぎます。"));
                        return;
                    }
                    if (pc.getInventory().getWeight100() > 82) { // この部分修正すると、エラー出る
                        pc.sendPackets(new S_ChatPacket(pc, "持ち物が重すぎて使用することはできません。"));
                        return;
                    }
                    if (box != null) {
                        if (box.open(pc)) {
                            L1EtcItem temp = (L1EtcItem) l1iteminstance.getItem();
                            if (temp.get_delayEffect() > 0) {
                                isDelayEffect = true;
                            } else {
                                pc.getInventory().removeItem(l1iteminstance.getId(), 1);
                            }
                        }
                    }

                } else if (l1iteminstance.getItem().getType() == 2) { // light
                    if (l1iteminstance.getRemainingTime() <= 0 && itemId != 40004) {
                        return;
                    }
                    if (l1iteminstance.isNowLighting()) {
                        l1iteminstance.setNowLighting(false);
                        pc.getLight().turnOnOffLight();
                    } else {
                        l1iteminstance.setNowLighting(true);
                        pc.getLight().turnOnOffLight();
                    }
                    pc.sendPackets(new S_ItemName(l1iteminstance));
                }
                // 6478までのスイッチドア
                switch (itemId) {
                    // switch文の開始
                    case 40003: // ランタンオイル
                        for (L1ItemInstance lightItem : pc.getInventory().getItems()) {
                            if (lightItem.getItem().getItemId() == 40002) {
                                lightItem.setRemainingTime(l1iteminstance.getItem().getLightFuel());
                                pc.sendPackets(new S_ItemName(lightItem));
                                pc.sendPackets(new S_ServerMessage(230));
                                break;
                            }
                        }
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        break;

                    case 100001: { // 人形変更秘伝書
                        int dollId = l1iteminstance1.getItem().getItemId();
                        boolean isAppear = true;
                        L1DollInstance doll = null;
                        L1ItemInstance item = null;
                        for (Object dollObject : pc.getDollList()) {
                            doll = (L1DollInstance) dollObject;
                            if (doll.getItemObjId() == itemId) {
                                isAppear = false;
                                break;
                            }
                        }
                        if (isAppear) {
                            if (pc.getDollListSize() >= 1) {
                                pc.sendPackets(new S_SystemMessage("人形を召喚した状態に変更することができません。"));
                                return;
                            }
                        }
                        if (dollId == 41248 // マジックドール：バグベアー
                                || dollId == 41250 // マジックドール：狼
                                || dollId == 210086 // マジックドール：時のダンサー
                                || dollId == 210072 // マジックドール：クラスタシアン
                                || dollId == 210070 // マジックドール：ストーンゴーレム
                                || dollId == 210096 // マジックドール：イエティ
                                || dollId == 500213 // マジックドール：ブルート
                                || dollId == 41249 // マジックドール：サキュバス
                                || dollId == 210071 // マジックドール：エルダー
                                || dollId == 210105 // マジックドール：コカトリス
                                || dollId == 447012 // マジックドール：チャンピオン
                                || dollId == 447013 // マジックドール：新しい
                                || dollId == 447014 // マジックドール：江南スタイル
                                || dollId == 500215 // マジックドール：かかし
                                || dollId == 447016 // マジックドール：リッチ
                                || dollId == 447015 // マジックドール：グレムリン
                                || dollId == 500214 // マジックドール：スパルトイ
                                || dollId == 447017 // マジックドール：ドレイク
                                || dollId == 510216 // マジックドール：雪だるまA
                                || dollId == 510217 // マジックドール：雪だるまB
                                || dollId == 510218 // マジックドール：雪だるまC
                                || dollId == 510219 // マジックドール：ジャイアント
                                || dollId == 510220 // マジックドール：サイクロプス
                                || dollId == 510221 // マジックドール：ブラックエルダー
                                || dollId == 510222) { // マジックドール：サキュバスクイーン

                            pc.getInventory().removeItem(l1iteminstance1, 1);
                            pc.getInventory().removeItem(l1iteminstance, 1);

                            int i = _random.nextInt(1060) + 1;
                            if (i <= 150) { // 15%
                                item = pc.getInventory().storeItem(41248, 1); // バグベア
                            } else if (i <= 260) { // 11%
                                item = pc.getInventory().storeItem(41250, 1); // 狼
                            } else if (i <= 370) { // 11%
                                item = pc.getInventory().storeItem(210086, 1); // シーダンサー
                            } else if (i <= 480) { // 11%
                                item = pc.getInventory().storeItem(210072, 1); // クラスタシアン
                            } else if (i <= 590) { // 11%
                                item = pc.getInventory().storeItem(210070, 1); // ストーンゴーレム
                            } else if (i <= 680) { // 9%
                                item = pc.getInventory().storeItem(210096, 1); // エティ
                            } else if (i <= 770) { // 9%
                                item = pc.getInventory().storeItem(500213, 1); // ブルート
                            } else if (i <= 810) { // 4%
                                item = pc.getInventory().storeItem(41249, 1); // サキュバス
                            } else if (i <= 850) { // 4%
                                item = pc.getInventory().storeItem(210071, 1); // 長老
                            } else if (i <= 880) { // 3%
                                item = pc.getInventory().storeItem(210105, 1); // コカトリス
                            } else if (i <= 900) { // 2%
                                item = pc.getInventory().storeItem(447012, 1); // チャンピオン
                            } else if (i <= 920) { // 2%
                                item = pc.getInventory().storeItem(447013, 1); // 新しい
                            } else if (i <= 940) { // 2%
                                item = pc.getInventory().storeItem(447014, 1); // 江南スタイル
                            } else if (i <= 960) { // 2%
                                item = pc.getInventory().storeItem(500215, 1); // かかし
                            } else if (i <= 970) { // 1%
                                item = pc.getInventory().storeItem(447016, 1); // リッチ
                            } else if (i <= 980) { // 1%
                                item = pc.getInventory().storeItem(447015, 1); // グレムリン
                            } else if (i <= 990) { // 1%
                                item = pc.getInventory().storeItem(500214, 1); // スパルトイ
                            } else if (i <= 1000) { // 1%
                                item = pc.getInventory().storeItem(447017, 1); // ドレイク
                            } else if (i <= 1010) { // 1%
                                item = pc.getInventory().storeItem(510216, 1); // 雪だるまA
                            } else if (i <= 1020) { // 1%
                                item = pc.getInventory().storeItem(510217, 1); // 雪だるまB
                            } else if (i <= 1030) { // 1%
                                item = pc.getInventory().storeItem(510218, 1); // 雪だるまC
                            } else if (i <= 1040) { // 1%
                                item = pc.getInventory().storeItem(510221, 1); // ブラックエルダー
                            } else if (i <= 1050) { // 1%
                                item = pc.getInventory().storeItem(510222, 1); // サキュバスクイーン
                            } else if (i <= 1055) { // 0.5%
                                item = pc.getInventory().storeItem(510219, 1); // ジャイアント
                            } else if (i <= 1060) { // 1%
                                item = pc.getInventory().storeItem(510220, 1); // サイクロプス
                            }
                            pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
                        } else {
                            pc.sendPackets(new S_SystemMessage("変更することができないアイテムです。"));
                        }
                    }
                    break;

                    case 600230:
                        einhasadGift(pc, itemId);
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        break;

                    case 50020: // 封印ジュムソ
                        if (l1iteminstance1.getBless() == 0 || l1iteminstance1.getBless() == 1
                                || l1iteminstance1.getBless() == 2 || l1iteminstance1.getBless() == 3) {
                            if (l1iteminstance1 != null && l1iteminstance1.getItem().getType2() != 1
                                    && l1iteminstance1.getItem().getType2() != 2) {
                                pc.sendPackets(new S_SystemMessage("武器、防具のみ使用可能です。"));
                                return;
                            }
                            int Bless = 0;
                            switch (l1iteminstance1.getBless()) {
                                case 0:
                                    Bless = 128;
                                    break; // 軸
                                case 1:
                                    Bless = 129;
                                    break; // 通常
                                case 2:
                                    Bless = 130;
                                    break; // 呪い
                                case 3:
                                    Bless = 131;
                                    break; // 未確認
                            }
                            l1iteminstance1.setBless(Bless);
                            int st = 0;
                            if (l1iteminstance1.isIdentified())
                                st += 1;
                            if (!l1iteminstance1.getItem().isTradable())
                                st += 2;
                            if (l1iteminstance1.getItem().isCantDelete())
                                st += 4;
                            if (l1iteminstance1.getItem().get_safeenchant() < 0)
                                st += 8;
                            if (l1iteminstance1.getBless() >= 128) {
                                st = 32;
                                if (l1iteminstance1.isIdentified()) {
                                    st += 15;
                                } else {
                                    st += 14;
                                }
                            }
                            pc.sendPackets(new S_PacketBox(S_PacketBox.ITEM_STATUS, l1iteminstance1, st));
                            pc.getInventory().updateItem(l1iteminstance1, L1PcInventory.COL_IS_ID);
                            pc.getInventory().saveItem(l1iteminstance1, L1PcInventory.COL_IS_ID);
                            pc.getInventory().removeItem(l1iteminstance, 1);
                        } else
                            pc.sendPackets(new S_ServerMessage(79)); // \f1何も起こらなかった。
                        break;

                    case 50021:// 封印解除ジュムソ
                        if (l1iteminstance1.getBless() == 128 || l1iteminstance1.getBless() == 129
                                || l1iteminstance1.getBless() == 130 || l1iteminstance1.getBless() == 131) {
                            int Bless = 0;
                            switch (l1iteminstance1.getBless()) {
                                case 128:
                                    Bless = 0;
                                    break;// 軸
                                case 129:
                                    Bless = 1;
                                    break;// 通常
                                case 130:
                                    Bless = 2;
                                    break;// 呪い
                                case 131:
                                    Bless = 3;
                                    break; // 未確認
                            }
                            l1iteminstance1.setBless(Bless);
                            int st = 0;
                            if (l1iteminstance1.isIdentified())
                                st += 1;
                            if (!l1iteminstance1.getItem().isTradable())
                                st += 2;
                            if (l1iteminstance1.getItem().isCantDelete())
                                st += 4;
                            if (l1iteminstance1.getItem().get_safeenchant() < 0)
                                st += 8;
                            if (l1iteminstance1.getBless() >= 128) {
                                st = 32;
                                if (l1iteminstance1.isIdentified()) {
                                    st += 15;
                                } else {
                                    st += 14;
                                }
                            }
                            pc.sendPackets(new S_PacketBox(S_PacketBox.ITEM_STATUS, l1iteminstance1, st));
                            pc.getInventory().updateItem(l1iteminstance1, L1PcInventory.COL_IS_ID);
                            pc.getInventory().saveItem(l1iteminstance1, L1PcInventory.COL_IS_ID);
                            pc.getInventory().removeItem(l1iteminstance, 1);
                        } else
                            pc.sendPackets(new S_ServerMessage(79));// \f1何も起こらなかった。
                        break;

                    case L1ItemId.DRAGON_PEARL:// ドラゴンパール
                    case L1ItemId.DRAGON_PEARL1:// ドラゴンパール交換不可
                        useDragonPearl(pc, itemId);
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        break;

                    case 410064:
                    case 410139:
                    case 410138:
                    case 1000002:
                    case 1000003:
                    case 1000004:
                    case 1000007:
                    case 3000153:
                    case 7241:
                    case 60255:
            /*
		     * * ドラゴンのエメラルド、象牙の塔のドラゴンのダイヤモンド、象牙の塔のドラゴンのエメラルドドラゴンのルビー、
		     * ドラゴンのサファイア、ドラゴンのダイヤモンド、祝福されたドラゴンのダイヤモンド
		     */
                        if (itemId == L1ItemId.DRAGON_DIAMOND || itemId == L1ItemId.DRAGON_DIAMOND1 || itemId == 1000007) {
                            // if (pc.hasSkillEffect(L1SkillId.EMERALD_YES) == true)
                            // {
                            // pc.sendPackets(new S_ServerMessage(2146));
                            // return;
                            // }
                            if (pc.getEinhasad() <= 15000000) {
                                if (itemId == 1000007) {
                                    pc.calEinhasad(5000000);
                                } else {
                                    pc.calEinhasad(1000000);
                                }
                                pc.sendPackets(new S_PacketBox(S_PacketBox.EINHASAD, pc.getEinhasad()));
                                if (itemId == 1000007) {
                                    pc.sendPackets(new S_SystemMessage("アインハザードの祝福が500％追加されました。"));
                                } else {
                                    pc.sendPackets(new S_SystemMessage("アインハザードの祝福が100％追加されました。"));
                                }
                                pc.getInventory().removeItem(l1iteminstance, 1);
                            } else {
                                pc.sendPackets(new S_SystemMessage("アインハザードの祝福：残っている祝福指数が多く使用することはできません。"));

                            }
                        } else if (itemId == 3000153) {
                            if (pc.hasSkillEffect(L1SkillId.EMERALD_YES) == true) {
                                pc.sendPackets(new S_ServerMessage(2146));
                                return;
                            }
                            if (pc.getEinhasad() <= 15000000) {
                                pc.calEinhasad(13000000);
                                pc.sendPackets(new S_PacketBox(S_PacketBox.EINHASAD, pc.getEinhasad()));
                                pc.getInventory().removeItem(l1iteminstance, 1);
                            } else {
                                pc.sendPackets(new S_SystemMessage("アインハザードの祝福：残っている祝福指数が多く使用することはできません。"));
                            }

                        } else if (itemId == L1ItemId.DRAGON_SAPPHIRE) {
                            if (pc.hasSkillEffect(L1SkillId.EMERALD_YES) == true) {
                                pc.sendPackets(new S_ServerMessage(2146));
                                return;
                            }
                            if (pc.getEinhasad() <= 15000000) {
                                pc.calEinhasad(500000);
                                pc.sendPackets(new S_PacketBox(S_PacketBox.EINHASAD, pc.getEinhasad()));
                                pc.sendPackets(new S_SystemMessage("アインハザードの祝福：50％追加。"));
                                pc.getInventory().removeItem(l1iteminstance, 1);
                            } else {
                                pc.sendPackets(new S_SystemMessage("アインハザードの祝福：残っている祝福指数が多く使用することはできません。"));
                            }

                        } else if (itemId == L1ItemId.DRAGON_RUBY) {
                            if (pc.hasSkillEffect(L1SkillId.EMERALD_YES) == true) {
                                pc.sendPackets(new S_ServerMessage(2146));
                                return;
                            }
                            if (pc.getEinhasad() <= 15000000) {
                                pc.calEinhasad(300000);
                                pc.sendPackets(new S_PacketBox(S_PacketBox.EINHASAD, pc.getEinhasad()));
                                pc.sendPackets(new S_SystemMessage("アインハザードの祝福：30％追加。"));
                                pc.getInventory().removeItem(l1iteminstance, 1);
                            } else {
                                pc.sendPackets(new S_SystemMessage("アインハザードの祝福：残っている祝福指数が多く使用することはできません。"));
                            }

                        } else if (itemId == L1ItemId.EMERALD || itemId == L1ItemId.EMERALD1) {
                            if (pc.hasSkillEffect(L1SkillId.EMERALD_NO) == true) {
                                pc.sendPackets(new S_ServerMessage(2145));
                                return;
                            } else if (pc.hasSkillEffect(L1SkillId.EMERALD_YES) == true) {
                                pc.sendPackets(new S_ServerMessage(2147));
                                return;
                            } else if (pc.hasSkillEffect(L1SkillId.DRAGON_PUPLE)
                                    || pc.hasSkillEffect(L1SkillId.DRAGON_TOPAZ)) {
                                pc.sendPackets(new S_ServerMessage(2147));
                                return;
                            }
                            pc.calEinhasad(1000000);
                            // pc.setSkillEffect(L1SkillId.EMERALD_YES, 10800 *
                            // 1000); //本サーバー3時間なのにドゥダため、30分に下方の下
                            pc.setSkillEffect(L1SkillId.EMERALD_YES, 1800 * 1000);
                            // pc.sendPackets(new
                            // S_PacketBox(S_PacketBox.EMERALD_ICON, 0x02, 10800));
                            pc.sendPackets(new S_PacketBox(S_PacketBox.EMERALD_ICON, 0x02, 1800)); // 本サーバー3時間なのにドゥダため、30分に下方の下
                            pc.sendPackets(new S_PacketBox(S_PacketBox.EINHASAD, pc.getEinhasad()));
                            pc.sendPackets(new S_ServerMessage(2140));
                            pc.getInventory().removeItem(l1iteminstance, 1);
                        } else if (itemId == 60255) {
                            if (pc.hasSkillEffect(L1SkillId.EMERALD_YES) == true) {
                                pc.sendPackets(new S_ServerMessage(2146));
                                return;
                            }
                            if (pc.getEinhasad() <= 10000) {
                                pc.sendPackets(new S_SystemMessage("祝福指数が必要使用出来ます。"));
                                return;
                            }
                            if (pc.hasSkillEffect(L1SkillId.DRAGON_TOPAZ)) {
                                pc.removeSkillEffect(L1SkillId.DRAGON_TOPAZ);
                            }

                            pc.setSkillEffect(DRAGON_PUPLE, 1800 * 1000);
                            pc.sendPackets(new S_PacketBox(1800, 1, true, true));
                            pc.sendPackets(new S_SkillSound(pc.getId(), 197));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 197));
                            pc.getInventory().removeItem(l1iteminstance, 1);

                        } else if (itemId == 7241) {
                            if (pc.hasSkillEffect(L1SkillId.EMERALD_YES) == true) {
                                pc.sendPackets(new S_ServerMessage(2146));
                                return;
                            }
                            if (pc.getEinhasad() <= 10000) {
                                pc.sendPackets(new S_SystemMessage("祝福指数が必要使用出来ます。"));
                                return;
                            }
                            if (pc.hasSkillEffect(L1SkillId.DRAGON_PUPLE)) {
                                pc.removeSkillEffect(L1SkillId.DRAGON_PUPLE);
                            }

                            pc.setSkillEffect(DRAGON_TOPAZ, 1800 * 1000);
                            pc.sendPackets(new S_PacketBox(1800, 2, true, true));
                            pc.sendPackets(new S_SkillSound(pc.getId(), 197));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 197));
                            pc.getInventory().removeItem(l1iteminstance, 1);
                        }

                        break;

                    case 410032:
                    case 410033:
                    case 410034:
                    case 410035:
                    case 410036:
                    case 410037:
                    case 410038:
		    /*
		     * 水竜の魔眼、プンニョンの魔眼、地竜の魔眼、誕生の魔眼、形状の魔眼、生命の魔眼
		     *
		     */
                        if (l1iteminstance.getLastUsed() == null) {
                            Timestamp ts1 = new Timestamp(System.currentTimeMillis());
                            l1iteminstance.setLastUsed(ts1);
                        }
                        Timestamp lastUsed = l1iteminstance.getLastUsed();
                        Calendar cal = Calendar.getInstance();
                        if ((cal.getTimeInMillis() - lastUsed.getTime()) / 1000 >= 0) {
                            if (itemId == 410032) { // 水竜
                                L1SkillUse l1Skilluse = new L1SkillUse();
                                l1Skilluse.handleCommands(pc, FAFU_MAAN, pc.getId(), pc.getX(), pc.getY(), null, 0,
                                        L1SkillUse.TYPE_GMBUFF);
                                Timestamp ts = new Timestamp(System.currentTimeMillis());
                                l1iteminstance.setLastUsed(ts);
                            }
                            if (itemId == 410033) { // 風竜
                                L1SkillUse l1Skilluse = new L1SkillUse();
                                l1Skilluse.handleCommands(pc, LIND_MAAN, pc.getId(), pc.getX(), pc.getY(), null, 0,
                                        L1SkillUse.TYPE_GMBUFF);
                                Timestamp ts = new Timestamp(System.currentTimeMillis());
                                l1iteminstance.setLastUsed(ts);
                            }
                            if (itemId == 410034) { // 地竜
                                L1SkillUse l1Skilluse = new L1SkillUse();
                                l1Skilluse.handleCommands(pc, ANTA_MAAN, pc.getId(), pc.getX(), pc.getY(), null, 0,
                                        L1SkillUse.TYPE_GMBUFF);
                                Timestamp ts = new Timestamp(System.currentTimeMillis());
                                l1iteminstance.setLastUsed(ts);
                            }
                            if (itemId == 410035) { // 火竜
                                L1SkillUse l1Skilluse = new L1SkillUse();
                                l1Skilluse.handleCommands(pc, VALA_MAAN, pc.getId(), pc.getX(), pc.getY(), null, 0,
                                        L1SkillUse.TYPE_GMBUFF);
                                Timestamp ts = new Timestamp(System.currentTimeMillis());
                                l1iteminstance.setLastUsed(ts);
                            }
                            if (itemId == 410036) { // 誕生
                                L1SkillUse l1Skilluse = new L1SkillUse();
                                l1Skilluse.handleCommands(pc, BIRTH_MAAN, pc.getId(), pc.getX(), pc.getY(), null, 0,
                                        L1SkillUse.TYPE_GMBUFF);
                                Timestamp ts = new Timestamp(System.currentTimeMillis());
                                l1iteminstance.setLastUsed(ts);
                            }
                            if (itemId == 410037) { // 形状
                                L1SkillUse l1Skilluse = new L1SkillUse();
                                l1Skilluse.handleCommands(pc, SHAPE_MAAN, pc.getId(), pc.getX(), pc.getY(), null, 0,
                                        L1SkillUse.TYPE_GMBUFF);
                                Timestamp ts = new Timestamp(System.currentTimeMillis());
                                l1iteminstance.setLastUsed(ts);
                            }
                            if (itemId == 410038) { // 生命
                                L1SkillUse l1Skilluse = new L1SkillUse();
                                l1Skilluse.handleCommands(pc, LIFE_MAAN, pc.getId(), pc.getX(), pc.getY(), null, 0,
                                        L1SkillUse.TYPE_GMBUFF);
                                Timestamp ts = new Timestamp(System.currentTimeMillis());
                                l1iteminstance.setLastUsed(ts);
                            }
                        }
                        break;

                    case 410010:
                    case 410011:
                    case 410012:
                    case 30063:
		    /*
		     * 体力増強のスクロール、魔力増強のスクロール、戦闘強化のスクロール、ドラゴンの石
		     */
                        if (itemId == 30063) { // ドラゴンの石
                            if (!(pc.getMapId() >= 1005 && pc.getMapId() <= 1022
                                    || pc.getMapId() > 6000 && pc.getMapId() < 6999)) {
                                pc.sendPackets(new S_ServerMessage(1891));// ドラゴンの息吹が込められた地でのみ使用することができます。
                                return;
                            }
                        }
                        useCashScroll(pc, itemId);
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        break;

                    case 700000:// 経験値ポーション
                        if (Config.EXP_POT_LIMIT == true) {
                            if (pc.getLevel() >= Config.LIMITLEVEL) {// 経験値
                                pc.sendPackets(new S_SystemMessage("レベル上限に達したため、使用することができません。"));
                                return;
                            }

                            if (pc.getLevel() >= 1 && pc.getLevel() <= 48) {
                                pc.setExp(pc.getExp() + 326144);
                            } else if (pc.getLevel() >= 49 && pc.getLevel() <= 64) {
                                pc.setExp(pc.getExp() + 2609152);
                            } else if (pc.getLevel() >= 65 && pc.getLevel() <= 69) {
                                pc.setExp(pc.getExp() + 1304576);
                            } else if (pc.getLevel() >= 70 && pc.getLevel() <= 74) {
                                pc.setExp(pc.getExp() + 652288);
                            } else if (pc.getLevel() >= 75 && pc.getLevel() <= 78) {
                                pc.setExp(pc.getExp() + 326144);
                            } else if (pc.getLevel() == 79) {
                                pc.setExp(pc.getExp() + 163072);
                            } else if (pc.getLevel() >= 80 && pc.getLevel() <= 81) {
                                pc.setExp(pc.getExp() + 81536);
                            } else if (pc.getLevel() >= 82 && pc.getLevel() <= 83) {
                                pc.setExp(pc.getExp() + 40768);
                            } else if (pc.getLevel() >= 84 && pc.getLevel() <= 85) {
                                pc.setExp(pc.getExp() + 20384);
                            } else if (pc.getLevel() == 86) {
                                pc.setExp(pc.getExp() + 10192);
                            } else if (pc.getLevel() == 87) {
                                pc.setExp(pc.getExp() + 5096);
                            } else if (pc.getLevel() == 88) {
                                pc.setExp(pc.getExp() + 2048);
                            } else if (pc.getLevel() == 89) {
                                pc.setExp(pc.getExp() + 1024);
                            } else if (pc.getLevel() >= 90) {
                                pc.setExp(pc.getExp() + 512);
                            }
                        } else {
                            if (pc.getLevel() >= 1 && pc.getLevel() <= 48) {
                                pc.setExp(pc.getExp() + 326144);
                            } else if (pc.getLevel() >= 49 && pc.getLevel() <= 64) {
                                pc.setExp(pc.getExp() + 2609152);
                            } else if (pc.getLevel() >= 65 && pc.getLevel() <= 69) {
                                pc.setExp(pc.getExp() + 1304576);
                            } else if (pc.getLevel() >= 70 && pc.getLevel() <= 74) {
                                pc.setExp(pc.getExp() + 652288);
                            } else if (pc.getLevel() >= 75 && pc.getLevel() <= 78) {
                                pc.setExp(pc.getExp() + 326144);
                            } else if (pc.getLevel() == 79) {
                                pc.setExp(pc.getExp() + 163072);
                            } else if (pc.getLevel() >= 80 && pc.getLevel() <= 81) {
                                pc.setExp(pc.getExp() + 81536);
                            } else if (pc.getLevel() >= 82 && pc.getLevel() <= 83) {
                                pc.setExp(pc.getExp() + 40768);
                            } else if (pc.getLevel() >= 84 && pc.getLevel() <= 85) {
                                pc.setExp(pc.getExp() + 20384);
                            } else if (pc.getLevel() == 86) {
                                pc.setExp(pc.getExp() + 10192);
                            } else if (pc.getLevel() == 87) {
                                pc.setExp(pc.getExp() + 5096);
                            } else if (pc.getLevel() == 88) {
                                pc.setExp(pc.getExp() + 2048);
                            } else if (pc.getLevel() == 89) {
                                pc.setExp(pc.getExp() + 1024);
                            } else if (pc.getLevel() >= 90) {
                                pc.setExp(pc.getExp() + 512);
                            }
                        }

                        // int exptotal = 1304576 / exp;
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        break;

                    case 700001:
                        if (pc.getLevel() >= Config.LIMITLEVEL) {// 経験値
                            pc.sendPackets(new S_SystemMessage("レベル上限に達したため、使用することができません。"));
                            return;
                        }
                        supplyEXP(pc);
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        break;
                    case 43000:// 生まれ変わりのポーション
                        pc.setExp(1);
                        pc.resetLevel();
                        pc.setBonusStats(0);
                        pc.sendPackets(new S_SkillSound(pcObjid, 191));
                        pc.broadcastPacket(new S_SkillSound(pcObjid, 191));
                        pc.sendPackets(new S_OwnCharStatus(pc));
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        pc.sendPackets(new S_ServerMessage(822)); // 読者アイテムなので、メッセージは適しています。
                        pc.save();
                        break;

                    case 40033: // エリクサー力
                        if (pc.getElixirStats() < 5) {
                            if (pc.getAbility().getStr() < 45) {
                                pc.getAbility().addStr((byte) 1);
                                pc.setElixirStats(pc.getElixirStats() + 1);
                                pc.getInventory().removeItem(l1iteminstance, 1);
                                pc.sendPackets(new S_OwnCharStatus2(pc));
                                pc.sendPackets(new S_ACTION_UI2(S_ACTION_UI2.Elixir, pc.getElixirStats()));
                                pc.save(); // DBに文字情報を記入する

                            } else {
                                pc.sendPackets(new S_ServerMessage(481));
                                // \f1 一つの能力値の最大値は25です。他の能力値を選択してください。
                            }
                        } else {
                            pc.sendPackets(new S_SystemMessage("エリクサー使用上限に達したため、使用できません。"));
                            // \f1 一つの能力値の最大値は25です。他の能力値を選択してください。
                        }
                        break;

                    case 40034:// エリクサーコーン
                        if (pc.getElixirStats() < 5) {
                            if (pc.getAbility().getCon() < 45) {

                                pc.getAbility().addCon((byte) 1);
                                pc.setElixirStats(pc.getElixirStats() + 1);
                                pc.getInventory().removeItem(l1iteminstance, 1);
                                pc.sendPackets(new S_OwnCharStatus2(pc));
                                pc.sendPackets(new S_ACTION_UI2(S_ACTION_UI2.Elixir, pc.getElixirStats()));
                                pc.save(); // DBに文字情報を記入する
                            } else {
                                pc.sendPackets(new S_ServerMessage(481));
                                // \f1一つの能力値の最大値は25です。他の能力値を選択してください。
                            }
                        } else {
                            pc.sendPackets(new S_SystemMessage("エリクサー使用上限に達したため、使用できません。"));
                            // \f1一つの能力値の最大値は25です。他の能力値を選択してください。
                        }

                        break;

                    case 40035:// エリクサーデックス
                        if (pc.getElixirStats() < 5) {
                            if (pc.getAbility().getDex() < 45) {

                                pc.getAbility().addDex((byte) 1);
                                pc.resetBaseAc();
                                pc.setElixirStats(pc.getElixirStats() + 1);
                                pc.getInventory().removeItem(l1iteminstance, 1);
                                pc.sendPackets(new S_OwnCharStatus2(pc));
                                pc.sendPackets(new S_ACTION_UI2(S_ACTION_UI2.Elixir, pc.getElixirStats()));
                                pc.save();
                                ; // DBに文字情報を記入する
                                break;
                            } else {
                                pc.sendPackets(new S_ServerMessage(481));
                                // \f1 一つの能力値の最大値は25です。他の能力値を選択してください。
                            }
                        } else {
                            pc.sendPackets(new S_SystemMessage("エリクサー使用上限に達したため、使用できません。"));
                            // \f1 一つの能力値の最大値は25です。他の能力値を選択してください。
                        }

                        break;

                    case 40036:// エリクサーポイント
                        if (pc.getElixirStats() < 5) {
                            if (pc.getAbility().getInt() < 45) {

                                pc.getAbility().addInt((byte) 1);
                                pc.setElixirStats(pc.getElixirStats() + 1);
                                pc.getInventory().removeItem(l1iteminstance, 1);
                                pc.sendPackets(new S_OwnCharStatus2(pc));
                                pc.sendPackets(new S_ACTION_UI2(S_ACTION_UI2.Elixir, pc.getElixirStats()));
                                pc.save();
                                ; // DBに文字情報を記入する
                            } else {
                                pc.sendPackets(new S_ServerMessage(481));
                                // \f1 一つの能力値の最大値は25です。他の能力値を選択してください。
                            }
                        } else {
                            pc.sendPackets(new S_SystemMessage("エリクサー使用上限に達したため、使用できません。"));
                            // \f1 一つの能力値の最大値は25です。他の能力値を選択してください。
                        }

                        break;

                    case 40037:// エリクサーウィズ
                        if (pc.getElixirStats() < 5) {
                            if (pc.getAbility().getWis() < 45) {

                                pc.getAbility().addWis((byte) 1);
                                pc.resetBaseMr();
                                pc.setElixirStats(pc.getElixirStats() + 1);
                                pc.getInventory().removeItem(l1iteminstance, 1);
                                pc.sendPackets(new S_OwnCharStatus2(pc));
                                pc.sendPackets(new S_ACTION_UI2(S_ACTION_UI2.Elixir, pc.getElixirStats()));
                                pc.save();
                                ; // DBに文字情報を記入する
                            } else {
                                pc.sendPackets(new S_ServerMessage(481));
                                // \f1一つの能力値の最大値は25です。他の能力値を選択してください。
                            }
                        } else {
                            pc.sendPackets(new S_SystemMessage("エリクサー使用上限に達したため、使用できません。"));
                            // \f1一つの能力値の最大値は25です。他の能力値を選択してください。
                        }

                        break;

                    case 40038:// エリクサーカリ
                        if (pc.getElixirStats() < 5) {
                            if (pc.getAbility().getCha() < 45) {

                                pc.getAbility().addCha((byte) 1);
                                pc.setElixirStats(pc.getElixirStats() + 1);
                                pc.getInventory().removeItem(l1iteminstance, 1);
                                pc.sendPackets(new S_OwnCharStatus2(pc));
                                pc.sendPackets(new S_ACTION_UI2(S_ACTION_UI2.Elixir, pc.getElixirStats()));
                                pc.save();
                                ; // DBに文字情報を記入する
                            } else {
                                pc.sendPackets(new S_ServerMessage(481));
                                // \f1一つの能力値の最大値は25です。他の能力値を選択してください。
                            }
                        } else {
                            pc.sendPackets(new S_SystemMessage("エリクサー使用上限に達したため、使用できません。"));
                        }

                        break;

                    case 350198:// ティカルカレンダー
                        if (CrockController.getInstance().isTimeCrock()) {
                            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "tcalendaro"));
                        } else {
                            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "tcalendarc"));
                        }
                        break;
                    ////////////////////////////////////////////////////////////////////////////////////////////
                    /************************** ポーション回復戦略処理関連 ***********************************************/
                    ///////////////////////////////////////////////////////////////////////////////////////////
                    case 40010:
                    case 40011:
                    case 40012:
                    case 40019:
                    case 40020:
                    case 40021:
                    case 40022:
                    case 40023:
                    case 40024:
                    case 40026:
                    case 40027:
                    case 40028:
                    case 40029:
                    case 40043:
                    case 40058:
                    case 40071:
                    case 40506:
                    case 40930:
                    case 41141:
                    case 41337:
                    case 60029:
                    case 60030:
                    case 140010:
                    case 140011:
                    case 140012:
                    case 140506:
                    case 240010:
                    case 41403:
                    case 410000:
                    case 410003:
                    case 30062:
                    case 30056:
                        if (itemId == 30062) {
                            if (!(pc.getMapId() >= 1005 && pc.getMapId() <= 1022
                                    || pc.getMapId() > 6000 && pc.getMapId() < 6999)) {
                                pc.sendPackets(new S_ServerMessage(1891));
                                return;
                            }
                        }
                        if (itemId == 30056) {
                            if (!(pc.getMapId() >= 2101 && pc.getMapId() <= 2151
                                    || pc.getMapId() >= 2151 && pc.getMapId() <= 2201)) {
                                pc.sendPackets(new S_SystemMessage("特定の場所のみ使用可能です。"));
                                return;
                            }
                        }
                        L1HealingPotion healingPotion = L1HealingPotion.get(itemId);
                        healingPotion.use(pc, l1iteminstance);
                        break;
                    // //////////////////////////////////////////////////////////////////////////////////////////
                    /************************** ポーション回復戦略処理関連 ***********************************************/
                    // /////////////////////////////////////////////////////////////////////////////////////////

                    case 40858:// 術
                        pc.setDrink(true);
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        break;

                    case 30105:// 戦闘のポーション
                    case 210094:// 天上のポーション
                        UseExpPotion(pc, itemId);
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        break;

                    case 40017:// シアンポーション
                    case 40507:// エントの幹
                    case 30084:// 象牙の塔のシアンポーション
                        if (pc.hasSkillEffect(71) == true) { // ディケイポーションの状態
                            pc.sendPackets(new S_ServerMessage(698)); // 魔力によって何も飲むことができません。
                        } else {
                            cancelAbsoluteBarrier(pc); // アブ小ガルトバリアの解除
                            pc.sendPackets(new S_SkillSound(pc.getId(), 192));
                            pc.broadcastPacket(new S_SkillSound(pc.getId(), 192));
                            pc.getInventory().removeItem(l1iteminstance, 1);
                            pc.curePoison();
                        }
                        break;

                    case 40013:
                    case 40018:
                    case 40039:
                    case 40040:
                    case 40030:
                    case 41338:
                    case 41261:
                    case 41262:
                    case 41268:
                    case 41269:
                    case 41271:
                    case 41272:
                    case 41273:
                    case 41342:
                    case 30067:
                    case 140013:
                    case 140018:
                    case 3000162:
		    /*
		     * グリーンポーション、強化グリーンポーション、ワイン、ウイスキー、象牙の塔のグリーンポーション祝福されたワイン、
		     * おにぎり、焼き鳥、彫刻ピザ、トウモロコシ焼きあら、おでん、ワッフル、メデューサの血、勇士の速度向上ポーション、
		     */
                        useGreenPotion(pc, itemId);
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        break;

                    case 40014:
                    case 140014:
                    case 41415:
                    case 30073:
                        // ブレイブポーション、福祉ブレイブポーション、象牙の塔のブレイブポーション
                        if (pc.isKnight() || pc.isWarrior()) {
                            useBravePotion(pc, itemId);
                        } else {
                            pc.sendPackets(new S_ServerMessage(79));
                            // \f1 何も起こらなかった。
                        }
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        break;

                    case 40068:
                    case 140068:
                    case 210110:
                    case 30076:
                        // エルヴンワッフル、福祉エルヴンワッフル、象牙の塔のエルヴンワッフル
                        if (pc.isElf()) {
                            useBravePotion(pc, itemId);
                        } else {
                            pc.sendPackets(new S_ServerMessage(79));
                            // \f1 何も起こらなかった。
                        }
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        break;

                    case 40031:
                    case 30075:
                    case 210115:
                        // 悪魔の血、象牙の塔の悪魔の血、福祉悪魔の血
                        if (pc.isCrown()) {
                            useBravePotion(pc, itemId);
                        } else {
                            pc.sendPackets(new S_ServerMessage(79));
                            // \f1 何も起こらなかった。
                        }
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        break;

                    case 210036:
                    case 30077:
                        // ユグドラの実、象牙の塔のユグドラの実
                        if (pc.isBlackwizard()) {
                            useFruit(pc, itemId);
                        } else {
                            pc.sendPackets(new S_ServerMessage(79));// \f1
                            // 何も起こらなかった。
                        }
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        break;

                    case 40733:// 名誉のコイン
                        useBravePotion(pc, itemId);
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        break;

                    case 40066:// ソンピョン
                    case 41413:// 月餅
                        pc.sendPackets(new S_ServerMessage(338, "$1084")); // あなたの％0が回復していきます。
                        pc.setCurrentMp(pc.getCurrentMp() + (7 + _random.nextInt(6))); // 7~12
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        break;

                    case 40067:// ヨモギソンピョン
                    case 41414:// 福月餅
                        pc.sendPackets(new S_ServerMessage(338, "$1084")); // あなたの％0が回復していきます。
                        pc.setCurrentMp(pc.getCurrentMp() + (15 + _random.nextInt(16))); // 15~30
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        break;

                    case 410002:// 輝く葉
                        pc.sendPackets(new S_ServerMessage(338, "$1084")); // あなたの％0が回復していきます。
                        pc.setCurrentMp(pc.getCurrentMp() + 44);
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        break;

                    case 40735:// 容器のコイン
                        pc.sendPackets(new S_ServerMessage(338, "$1084")); // あなたの％0が回復していきます。
                        pc.setCurrentMp(pc.getCurrentMp() + 60);
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        break;

                    case 40042:// 精神力のポーション
                        pc.sendPackets(new S_ServerMessage(338, "$1084")); // あなたの％0が回復していきます。
                        pc.setCurrentMp(pc.getCurrentMp() + 50);
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        break;

                    case 41404:// クジャクの霊薬
                        pc.sendPackets(new S_ServerMessage(338, "$1084")); // あなたの％0が回復していきます。
                        pc.setCurrentMp(pc.getCurrentMp() + (80 + _random.nextInt(21))); // 80~100
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        break;

                    case 41412:// 金のちまき
                        pc.sendPackets(new S_ServerMessage(338, "$1084")); // あなたの％0が回復していきます。
                        pc.setCurrentMp(pc.getCurrentMp() + (5 + _random.nextInt(16))); // 5~20
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        break;

                    case 40032:// エヴァの祝福
                    case 40041:// 人魚の鱗
                    case 41344:// 水の整数
                        useBlessOfEva(pc, itemId);
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        break;

                    case 40015:
                    case 140015:
                    case 30083:// ブルーポーション
                    case 40736:// 知恵コイン
                    case 41142:// ピクシーのマナポーション
                    case 210114:// 福祉ブルーポーション
                        useBluePotion(pc, itemId);
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        break;

                    case 40016:
                    case 30089:// 知恵のポーション
                    case 140016:
                    case 210113:
                        if (pc.isWizard() || pc.isBlackwizard()) {
                            useWisdomPotion(pc, itemId);
                        } else {
                            pc.sendPackets(new S_ServerMessage(79));
                            // \f1 何も起こらなかった。
                        }
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        break;

                    case 40025:// 不透明ポーション
                        useBlindPotion(pc);
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        break;

                    case 30060:// ピクシー変身バー
                        pc.sendPackets(new S_ShowPolyList(pc.getId(), "pixies"));
                        if (!pc.isMagicItem()) {
                            pc.setMagicItem(true);
                            pc.setMagicItemId(itemId);
                        }
                        break;
                    case 600212:
                    case 600213:
                    case 600214:
                    case 600215:
                    case 600216:
                    case 600217:
                    case 600259:
                    case 600260:
                    case 600261:
                        reinforceBuff(pc, itemId, l1iteminstance);
                        break;
                    case 600218:
                        initReinforceBuff(pc, itemId, l1iteminstance);

                        break;
                    case 600223:
                        internetCafeCoin(pc, itemId, l1iteminstance, 7);
                        break;
                    case 600225:

                        internetCafeCoin(pc, itemId, l1iteminstance, 30);
                        break;
                    case 600226:// 乗車欲しい成長の果実（3日）
                    case 600227:// 乗車欲しい成長の果実（30日）
                        // System.out.println("objid "+ pc.getId());
                        if (pc.getId() == 0) {
                            return;
                        }
                        int day = 0;
                        if (itemId == 600226)
                            day = 3;// 期間
                        if (itemId == 600227)
                            day = 30;
                        TAMFruit(pc, use_objid, l1iteminstance, day);
                        break;
                    case 40088:// 変身スクロール
                    case 40096:// 象牙の塔の変身スクロール
                    case 210112:// 福祉変身スクロール
                    case 140088:// 祝福変身スクロール
                        if (usePolyScroll(pc, itemId, s)) {
                            pc.getInventory().removeItem(l1iteminstance, 1);
                        } else {
                            pc.sendPackets(new S_ServerMessage(181));
                            // \f1 そのようなmonsterは変身することができません。
                        }
                        break;
                    /** サイ変身スクロール */
                    case 220001:
                    case 220002:
                    case 220003:
                        usePolyScale2(pc, itemId);
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        break;
                    /** サイ変身スクロール */
                    case 41154:// 闇の鱗
                    case 41155:// 劣化の鱗
                    case 41156:// 背徳の鱗
                    case 41157:// 憎悪の鱗
                        usePolyScale(pc, itemId);
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        break;

                    case 41143:// ラバーボーンヘッド変身ポーション
                    case 41144:// ラバーボーンソルジャー変身ポーション
                    case 41145:// ラバーボーンナイフ変身ポーション
                    case 30057:// ココマ（青）変身ハンマー
                    case 30058:// ココマ（黄）変身ハンマー
                    case 30059:// ココマ（ピンク）変身ハンマー
                    case 8000: // ジンデスナイト変身
                    case 8001: // ジンランスマスター変身
                    case 8002: // 82レベルジンデスナイト変身
                    case 3000066: // 新規変身スクロール
                    case 3000067: // 新規変身スクロール
                    case 3000068: // 新規変身スクロール
                    case 3000069: // 新規変身スクロール
                    case 8003: // レンコン変身
                    case 3000163:
                        usePolyPotion(pc, itemId);
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        break;

                    case 210097:// シャルの変身スクロール（レベル30）
                    case 210098:// シャルの変身スクロール（レベル40）
                    case 210099:// シャルの変身スクロール（レベル52）
                    case 210100:// シャルの変身スクロール（レベル55）
                    case 210101:// シャルの変身スクロール（レベル60）
                    case 210102:// シャルの変身スクロール（レベル65）
                    case 210103:// シャルの変身スクロール（レベル70）
                    case 210116:// シャルの変身スクロール（レベル75）
                    case 210117:// シャルの変身スクロール（レベル80）
                        useLevelPolyScroll(pc, itemId);
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        break;

                    case 40317:// 砥石
                    case 30087:
                        // 武器や防具の場合のみ
                        if (l1iteminstance1.getItem().getType2() != 0 && l1iteminstance1.get_durability() > 0) {
                            String msg0;
                            if (l1iteminstance1.getItem().getType2() == 2 && l1iteminstance1.isEquipped()) {
                                pc.getAC().addAc(-1);
                                pc.sendPackets(new S_OwnCharAttrDef(pc));
                                pc.getInventory().recoveryDamage(l1iteminstance1);
                            } else {
                                pc.getInventory().recoveryDamage(l1iteminstance1);
                            }
                            msg0 = l1iteminstance1.getLogName();
                            if (l1iteminstance1.get_durability() == 0) {
                                pc.sendPackets(new S_ServerMessage(464, msg0)); // %0%sは新品のような状態になりました。
                            } else {
                                pc.sendPackets(new S_ServerMessage(463, msg0)); // %0
                                // 状態が良くなりました。
                            }
                        } else {
                            pc.sendPackets(new S_ServerMessage(79));// 何も起こらなかった。
                            return;
                        }
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        break;

                    case 210073:// 下級オシリスの宝箱の欠片（下）
                    case 210077:// 上級オシリスの宝箱の欠片（下）
                    case 500205:// 下級ククルカンの宝箱の欠片（下）
                    case 500207: {// 上級ククルカンの宝箱の欠片（下）
                        int itemId2 = l1iteminstance1.getItem().getItemId();
                        if (itemId == 210073 && itemId2 == 210074) {
                            if (pc.getInventory().checkItem(210074)) {
                                pc.getInventory().removeItem(l1iteminstance1, 1);
                                pc.getInventory().removeItem(l1iteminstance, 1);
                                pc.getInventory().storeItem(210075, 1);
                            }
                        } else if (itemId == 210077 && itemId2 == 210078) {
                            if (pc.getInventory().checkItem(210078)) {
                                pc.getInventory().removeItem(l1iteminstance1, 1);
                                pc.getInventory().removeItem(l1iteminstance, 1);
                                pc.getInventory().storeItem(210079, 1);
                            }
                        }
                        if (itemId == 500205 && itemId2 == 500204) {
                            if (pc.getInventory().checkItem(500204)) {
                                pc.getInventory().removeItem(l1iteminstance1, 1);
                                pc.getInventory().removeItem(l1iteminstance, 1);
                                pc.getInventory().storeItem(500208, 1);
                            }
                        } else if (itemId == 500207 && itemId2 == 500206) {
                            if (pc.getInventory().checkItem(500206)) {
                                pc.getInventory().removeItem(l1iteminstance1, 1);
                                pc.getInventory().removeItem(l1iteminstance, 1);
                                pc.getInventory().storeItem(500209, 1);
                            }
                        } else {
                            pc.sendPackets(new S_ServerMessage(79)); // \f1
                            // 何も起こらなかった。
                        }
                    }
                    break;

                    case 210083:// 太古の玉砕
                        if (client.getAccount().getCharSlot() < 8) {
                            client.getAccount().setCharSlot(client, client.getAccount().getCharSlot() + 1);
                            pc.getInventory().removeItem(l1iteminstance, 1);
                            pc.sendPackets(new S_SystemMessage("キャラクタースロットを拡張しました。（再起動後適用）"));
                        } else {
                            pc.sendPackets(new S_SystemMessage("キャラクタースロットが既にいっぱいです。"));
                        }
                        break;

                    case 210082: {// 亀裂の核
                        int itemId2 = l1iteminstance1.getItem().getItemId();
                        if (itemId2 == 210075) {
                            if (pc.getInventory().checkItem(210075)) {
                                pc.getInventory().removeItem(l1iteminstance1, 1);
                                pc.getInventory().removeItem(l1iteminstance, 1);
                                pc.getInventory().storeItem(210076, 1);
                            }
                        } else if (itemId2 == 210079) {
                            if (pc.getInventory().checkItem(210079)) {
                                pc.getInventory().removeItem(l1iteminstance1, 1);
                                pc.getInventory().removeItem(l1iteminstance, 1);
                                pc.getInventory().storeItem(210080, 1);
                            }
                        } else if (itemId2 == 500208) {
                            if (pc.getInventory().checkItem(500208)) {
                                pc.getInventory().removeItem(l1iteminstance1, 1);
                                pc.getInventory().removeItem(l1iteminstance, 1);
                                pc.getInventory().storeItem(500202, 1);
                            }
                        } else if (itemId2 == 500209) {
                            if (pc.getInventory().checkItem(500209)) {
                                pc.getInventory().removeItem(l1iteminstance1, 1);
                                pc.getInventory().removeItem(l1iteminstance, 1);
                                pc.getInventory().storeItem(500203, 1);
                            }
                        } else {
                            pc.sendPackets(new S_ServerMessage(79)); // \f1
                            // 何も起こらなかった。
                        }
                    }
                    break;

                    case 60035: // ルーン馬力剤
                        if (pc.getInventory().checkItem(60035, 1)) {
                            int i = 0;
                            int choiceItem = l1iteminstance1.getItem().getItemId();
                            switch (choiceItem) { //
                                case 222295:
                                    i = 0;
                                    break;
                                case 222296:
                                    i = 1;
                                    break;
                                case 222297:
                                    i = 2;
                                    break;
                                case 222298:
                                    i = 3;
                                    break;
                                case 222299:
                                    i = 4;
                                    break;
                                case 222312:
                                    i = 5;
                                    break;
                                case 222313:
                                    i = 6;
                                    break;
                                case 222314:
                                    i = 7;
                                    break;
                                case 222315:
                                    i = 8;
                                    break;
                                case 222316:
                                    i = 9;
                                    break;
                                default:
                                    i = 10;
                                    break;
                            }
                            if (i == 10) {
                                pc.sendPackets(new S_SystemMessage("エリクサールーンに使用することができます。"));
                                return;
                            } else {
                                pc.getInventory().consumeItem(60035, 1);
                                pc.getInventory().consumeItem(choiceItem, 1);
                                pc.getInventory().storeItem(60034, 1);
                                pc.sendPackets(new S_SystemMessage("魔力を失ったルーンに変更されました。"));
                            }
                        }
                        break;

                    case 410094:// 魔力の息吹
                        if (pc.getInventory().checkItem(L1ItemId.MAGIC_BREATH, 1)) {
                            int[] last = { 22232, 22233, 22234, 22235, 22236, 22237, 22238, 22239, 22240, 22241, 22242,
                                    22243, 22244, 22245, 22246, 22247, 22248, 22249 };
                            int j = 0;
                            int choiceItem = l1iteminstance1.getItem().getItemId();
                            switch (choiceItem) {
                                case 410114:
                                    j = 0;
                                    break;
                                case 410115:
                                    j = 1;
                                    break;
                                case 410116:
                                    j = 2;
                                    break;
                                case 410117:
                                    j = 3;
                                    break;
                                case 410118:
                                    j = 4;
                                    break;
                                case 410119:
                                    j = 5;
                                    break;
                                case 410109:
                                    j = 6;
                                    break;
                                case 410124:
                                    j = 7;
                                    break;
                                case 410110:
                                    j = 8;
                                    break;
                                case 410125:
                                    j = 9;
                                    break;
                                case 410111:
                                    j = 10;
                                    break;
                                case 410126:
                                    j = 11;
                                    break;
                                case 410112:
                                    j = 12;
                                    break;
                                case 410113:
                                    j = 13;
                                    break;
                                case 410120:
                                    j = 14;
                                    break;
                                case 410121:
                                    j = 15;
                                    break;
                                case 410122:
                                    j = 16;
                                    break;
                                case 410123:
                                    j = 17;
                                    break;
                                default:
                                    j = 18;
                                    break;
                            }
                            if (j == 18) {
                                pc.sendPackets(new S_ServerMessage(79));
                                return;
                            } else {
                                pc.getInventory().consumeItem(L1ItemId.MAGIC_BREATH, 1);
                                pc.getInventory().consumeItem(choiceItem, 1);
                                pc.getInventory().storeItem(last[j], 1);
                                pc.sendPackets(
                                        new S_SystemMessage("" + l1iteminstance1.getItem().getName() + "の封印が解除されました。"));
                            }
                        }
                        break;

                    case 40097:// 象牙の塔の解呪スクロール
                    case 40119:// 解呪スクロール
                    case 140119:
                    case 140329:// 先住民のトーテム
                        L1Item template = null;
                        for (L1ItemInstance eachItem : pc.getInventory().getItems()) {
                            if (eachItem.getItem().getBless() != 2) {
                                continue;
                            }
                            if (!eachItem.isEquipped() && (itemId == 40119 || itemId == 40097)) {
                                // nくれる装備していることしかしてくれない
                                continue;
                            }
                            int id_normal = eachItem.getItemId() - 200000;
                            template = ItemTable.getInstance().getTemplate(id_normal);
                            if (template == null) {
                                continue;
                            }
                            if (pc.getInventory().checkItem(id_normal) && template.isStackable()) {
                                pc.getInventory().storeItem(id_normal, eachItem.getCount());
                                pc.getInventory().removeItem(eachItem, eachItem.getCount());
                            } else {
                                eachItem.setItem(template);
                                pc.getInventory().updateItem(eachItem, L1PcInventory.COL_ITEMID);
                                pc.getInventory().saveItem(eachItem, L1PcInventory.COL_ITEMID);
                                eachItem.setBless(eachItem.getBless() - 1);
                                pc.getInventory().updateItem(eachItem, L1PcInventory.COL_BLESS);
                                pc.getInventory().saveItem(eachItem, L1PcInventory.COL_BLESS);
                            }
                        }
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        pc.sendPackets(new S_ServerMessage(155)); // \f1誰かが助けてくれたようです。
                        break;

                    case 40126:// 確認スクロール
                    case 40098: {// 象牙の塔の確認スクロール
                        int add_mpr = l1iteminstance1.getItem().get_addmpr();
                        int add_hpr = l1iteminstance1.getItem().get_addhpr();
                        int safe_enchant = l1iteminstance1.getItem().get_safeenchant();
                        if (!l1iteminstance1.isIdentified()) {
                            l1iteminstance1.setIdentified(true);
                            pc.getInventory().updateItem(l1iteminstance1, L1PcInventory.COL_IS_ID);
                        }
                        pc.sendPackets(new S_IdentifyDesc(l1iteminstance1));
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        StringBuffer sb = new StringBuffer();
                        if (l1iteminstance1.getItem().getType2() == 1 || l1iteminstance1.getItem().getType2() == 2) {
                            if (safe_enchant == -1) {
                                sb.append("\\aD HPR: " + add_hpr + " /");
                                sb.append("\\aD MPR: " + add_mpr + " /");
                                sb.append("\\aD 基本エンチャント：不可能");
                            } else if (safe_enchant == 0) {
                                sb.append("\\aD HPR: " + add_hpr + " /");
                                sb.append("\\aD MPR: " + add_mpr + " /");
                                sb.append("\\aD 基本エンチャント: 0");
                            } else {
                                sb.append("\\aD HPR: " + add_hpr + " /");
                                sb.append("\\aD MPR: " + add_mpr + " /");
                                sb.append("\\aD 基本エンチャント: " + safe_enchant + "");
                            }
                        }
                        pc.sendPackets(new S_SystemMessage(sb.toString()));
                        sb = null;
                    }
                    break;

                    case 41036:// フル
                        int diaryId = l1iteminstance1.getItem().getItemId();
                        if (diaryId >= 41038 && 41047 >= diaryId) {
                            if ((_random.nextInt(99) + 1) <= Config.CREATE_CHANCE_DIARY) {
                                createNewItem(pc, diaryId + 10, 1);
                            } else {
                                pc.sendPackets(new S_ServerMessage(158, l1iteminstance1.getName())); // \f1%0この蒸発していなくなりました。
                            }
                            pc.getInventory().removeItem(l1iteminstance1, 1);
                            pc.getInventory().removeItem(l1iteminstance, 1);
                        } else {
                            pc.sendPackets(new S_ServerMessage(79)); // \f1
                            // 何も起こらなかった。
                        }
                        break;

                    case 40964:// 黒魔法の粉
                        int historybookId = l1iteminstance1.getItem().getItemId();
                        if (historybookId >= 41011 && 41018 >= historybookId) {
                            if ((_random.nextInt(99) + 1) <= Config.CREATE_CHANCE_HISTORY_BOOK) {
                                createNewItem(pc, historybookId + 8, 1);
                            } else {
                                pc.sendPackets(new S_ServerMessage(158, l1iteminstance1.getName()));
                            }
                            pc.getInventory().removeItem(l1iteminstance1, 1);
                            pc.getInventory().removeItem(l1iteminstance, 1);
                        } else {
                            pc.sendPackets(new S_ServerMessage(79));
                        }
                        break;

                    case 41048:
                    case 41049:
                    case 41050:
                    case 41051:
                    case 41052:
                    case 41053:
                    case 41054:
                    case 41055: {
                        // 糊付けされた航海日誌のページ：1〜8ページ
                        int logbookId = l1iteminstance1.getItem().getItemId();
                        if (logbookId == (itemId + 8034)) {
                            createNewItem(pc, logbookId + 2, 1);
                            pc.getInventory().removeItem(l1iteminstance1, 1);
                            pc.getInventory().removeItem(l1iteminstance, 1);
                        } else {
                            pc.sendPackets(new S_ServerMessage(79)); // \f1
                            // 何も起こらなかった。
                        }
                    }
                    break;

                    case 41056:
                    case 41057: {
                        // 糊付けされた航海日誌ページ：9〜10ページ
                        int logbookId = l1iteminstance1.getItem().getItemId();
                        if (logbookId == (itemId + 8034)) {
                            createNewItem(pc, 41058, 1);
                            pc.getInventory().removeItem(l1iteminstance1, 1);
                            pc.getInventory().removeItem(l1iteminstance, 1);
                        } else {
                            pc.sendPackets(new S_ServerMessage(79)); // \f1
                            // 何も起こらなかった。
                        }
                    }
                    break;

                    case 40925:// 浄化のポーション
                        int earingId = l1iteminstance1.getItem().getItemId();
                        if (earingId >= 40987 && 40989 >= earingId) { // 呪われたブラック耳リング
                            if (_random.nextInt(100) < Config.CREATE_CHANCE_RECOLLECTION) {
                                createNewItem(pc, earingId + 186, 1);
                            } else {
                                pc.sendPackets(new S_ServerMessage(158, l1iteminstance1.getName())); // \f1%0この蒸発していなくなりました。
                            }
                            pc.getInventory().removeItem(l1iteminstance1, 1);
                            pc.getInventory().removeItem(l1iteminstance, 1);
                        } else {
                            pc.sendPackets(new S_ServerMessage(79)); // \f1
                            // 何も起こらなかった。
                        }
                        break;

                    case 40926:
                    case 40927:
                    case 40928:
                    case 40929:
                        // 神秘的なポーション：1段階（1〜4段階）
                        int earing2Id = l1iteminstance1.getItem().getItemId();
                        int potion1 = 0;
                        int potion2 = 0;
                        if (earing2Id >= 41173 && 41184 >= earing2Id) {
                            // 耳リンリュ
                            if (itemId == 40926) {
                                potion1 = 247;
                                potion2 = 249;
                            } else if (itemId == 40927) {
                                potion1 = 249;
                                potion2 = 251;
                            } else if (itemId == 40928) {
                                potion1 = 251;
                                potion2 = 253;
                            } else if (itemId == 40929) {
                                potion1 = 253;
                                potion2 = 255;
                            }
                            if (earing2Id >= (itemId + potion1) && (itemId + potion2) >= earing2Id) {
                                if ((_random.nextInt(99) + 1) < Config.CREATE_CHANCE_MYSTERIOUS) {
                                    createNewItem(pc, (earing2Id - 12), 1);
                                    pc.getInventory().removeItem(l1iteminstance1, 1);
                                    pc.getInventory().removeItem(l1iteminstance, 1);
                                } else {
                                    pc.sendPackets(new S_ServerMessage(160, l1iteminstance1.getName()));
                                    // \f1％0％2強烈に％1輝きましたが、幸いなことに無事に住んでいました。
                                    pc.getInventory().removeItem(l1iteminstance, 1);
                                }
                            } else {
                                pc.sendPackets(new S_ServerMessage(79)); // \f1、何も起こらなかった。
                            }
                        } else {
                            pc.sendPackets(new S_ServerMessage(79)); // \f1何も起こらなかった。
                        }
                        break;

                    case 40931:
                    case 40932:
                    case 40933:
                    case 40934:
                    case 40935:
                    case 40936:
                    case 40937:
                    case 40938:
                    case 40939:
                    case 40940:
                    case 40941:
                    case 40942:
                        // 加工されたジュエリー（サファイア・ルビー・エメラルド）
                        int earing3Id = l1iteminstance1.getItem().getItemId();
                        int earinglevel = 0;
                        if (earing3Id >= 41161 && 41172 >= earing3Id) {
                            // 神秘的な耳リンリュ
                            if (earing3Id == (itemId + 230)) {
                                if ((_random.nextInt(99) + 1) < Config.CREATE_CHANCE_PROCESSING) {
                                    switch (earing3Id) {
                                        case 41161:
                                            earinglevel = 21014;
                                            break;
                                        case 41162:
                                            earinglevel = 21006;
                                            break;
                                        case 41163:
                                            earinglevel = 21007;
                                            break;
                                        case 41164:
                                            earinglevel = 21015;
                                            break;
                                        case 41165:
                                            earinglevel = 21009;
                                            break;
                                        case 41166:
                                            earinglevel = 21008;
                                            break;
                                        case 41167:
                                            earinglevel = 21016;
                                            break;
                                        case 41168:
                                            earinglevel = 21012;
                                            break;
                                        case 41169:
                                            earinglevel = 21010;
                                            break;
                                        case 41170:
                                            earinglevel = 21017;
                                            break;
                                        case 41171:
                                            earinglevel = 21013;
                                            break;
                                        case 41172:
                                            earinglevel = 21011;
                                            break;
                                    }
                                    createNewItem(pc, earinglevel, 1);
                                } else {
                                    pc.sendPackets(new S_ServerMessage(158, l1iteminstance1.getName()));
                                    // \f1%0この蒸発していなくなりました。
                                }
                                pc.getInventory().removeItem(l1iteminstance1, 1);
                                pc.getInventory().removeItem(l1iteminstance, 1);
                            } else {
                                pc.sendPackets(new S_ServerMessage(79)); // \f1何も起こらなかった。
                            }
                        } else {
                            pc.sendPackets(new S_ServerMessage(79)); // \f1何も起こらなかった。
                        }
                        break;

                    case 40943:
                    case 40944:
                    case 40945:
                    case 40946:
                    case 40947:
                    case 40948:
                    case 40949:
                    case 40950:
                    case 40951:
                    case 40952:
                    case 40953:
                    case 40954:
                    case 40955:
                    case 40956:
                    case 40957:
                    case 40958:
                        // 加工されたダイヤモンド（ウォーター・地球・ファイア・ウィンド）
                        int ringId = l1iteminstance1.getItem().getItemId();
                        int ringlevel = 0;
                        int gmas = 0;
                        int gmam = 0;
                        if (ringId >= 41185 && 41200 >= ringId) {
                            // 細工されたリンリュ
                            if (itemId == 40943 || itemId == 40947 || itemId == 40951 || itemId == 40955) {
                                gmas = 443;
                                gmam = 447;
                            } else if (itemId == 40944 || itemId == 40948 || itemId == 40952 || itemId == 40956) {
                                gmas = 442;
                                gmam = 446;
                            } else if (itemId == 40945 || itemId == 40949 || itemId == 40953 || itemId == 40957) {
                                gmas = 441;
                                gmam = 445;
                            } else if (itemId == 40946 || itemId == 40950 || itemId == 40954 || itemId == 40958) {
                                gmas = 444;
                                gmam = 448;
                            }
                            if (ringId == (itemId + 242)) {
                                if ((_random.nextInt(99) + 1) < Config.CREATE_CHANCE_PROCESSING_DIAMOND) {
                                    switch (ringId) {
                                        case 41185:
                                            ringlevel = 20435;
                                            break;
                                        case 41186:
                                            ringlevel = 20436;
                                            break;
                                        case 41187:
                                            ringlevel = 20437;
                                            break;
                                        case 41188:
                                            ringlevel = 20438;
                                            break;
                                        case 41189:
                                            ringlevel = 20439;
                                            break;
                                        case 41190:
                                            ringlevel = 20440;
                                            break;
                                        case 41191:
                                            ringlevel = 20441;
                                            break;
                                        case 41192:
                                            ringlevel = 20442;
                                            break;
                                        case 41193:
                                            ringlevel = 20443;
                                            break;
                                        case 41194:
                                            ringlevel = 20444;
                                            break;
                                        case 41195:
                                            ringlevel = 20445;
                                            break;
                                        case 41196:
                                            ringlevel = 20446;
                                            break;
                                        case 41197:
                                            ringlevel = 20447;
                                            break;
                                        case 41198:
                                            ringlevel = 20448;
                                            break;
                                        case 41199:
                                            ringlevel = 20449;
                                            break;
                                        case 41200:
                                            ringlevel = 20450;
                                            break;
                                    }
                                    pc.sendPackets(new S_ServerMessage(gmas, l1iteminstance1.getName()));
                                    createNewItem(pc, ringlevel, 1);
                                    pc.getInventory().removeItem(l1iteminstance1, 1);
                                    pc.getInventory().removeItem(l1iteminstance, 1);
                                } else {
                                    pc.sendPackets(new S_ServerMessage(gmam, l1iteminstance.getName()));
                                    pc.getInventory().removeItem(l1iteminstance, 1);
                                }
                            } else {
                                pc.sendPackets(new S_ServerMessage(79));
                                // \f1 何も起こらなかった。
                            }
                        } else {
                            pc.sendPackets(new S_ServerMessage(79));
                            // \f1 何も起こらなかった。
                        }
                        break;

                    case 41029:// 召喚球切れ
                        int dantesId = l1iteminstance1.getItem().getItemId();
                        if (dantesId >= 41030 && 41034 >= dantesId) {
                            // 召喚球のコア・各段階
                            if ((_random.nextInt(99) + 1) < Config.CREATE_CHANCE_DANTES) {
                                createNewItem(pc, dantesId + 1, 1);
                            } else {
                                pc.sendPackets(new S_ServerMessage(158, l1iteminstance1.getName()));
                                // \\ f1％0が蒸発していなくなりました。
                            }
                            pc.getInventory().removeItem(l1iteminstance1, 1);
                            pc.getInventory().removeItem(l1iteminstance, 1);
                        } else {
                            pc.sendPackets(new S_ServerMessage(79));
                            // \f1 何も起こらなかった。
                        }
                        break;

                    case 40090:
                    case 40091:
                    case 40092:
                    case 40093:
                    case 40094:// ブランクスクロール（レベル1）〜ブランクスクロール（レベル5）
                        if (pc.isWizard()) { // ウィザード
                            if (itemId == 40090 && blanksc_skillid <= 7 || // ブランク
                                    // スクロール（Lv1）でレベル1以下の魔法
                                    itemId == 40091 && blanksc_skillid <= 15 || // ブランク
                                    // スクロール（Lv2）でレベル2以下の魔法
                                    itemId == 40092 && blanksc_skillid <= 22 || // ブランク
                                    // スクロール（Lv3）でレベル3以下の魔法
                                    itemId == 40093 && blanksc_skillid <= 31 || // ブランク
                                    // スクロール（Lv4）にレベル4以下の魔法
                                    itemId == 40094 && blanksc_skillid <= 39) { // ブランク
                                // スクロール（Lv5）でレベル5以下の魔法
                                L1ItemInstance spellsc = ItemTable.getInstance().createItem(40859 + blanksc_skillid);
                                if (spellsc != null) {
                                    if (pc.getInventory().checkAddItem(spellsc, 1) == L1Inventory.OK) {
                                        L1Skills l1skills = SkillsTable.getInstance().getTemplate(blanksc_skillid + 1);
                                        // blanksc_skillidは0開始
                                        if (pc.getCurrentHp() + 1 < l1skills.getHpConsume() + 1) {
                                            pc.sendPackets(new S_ServerMessage(279));
                                            // \f1HPが不足魔法を使用することができません。
                                            return;
                                        }
                                        if (pc.getCurrentMp() < l1skills.getMpConsume()) {
                                            pc.sendPackets(new S_ServerMessage(278));
                                            // \f1MPが不足魔法を使用することができません。
                                            return;
                                        }
                                        if (l1skills.getItemConsumeId() != 0) {
                                            // 材料が必要
                                            if (!pc.getInventory().checkItem(l1skills.getItemConsumeId(),
                                                    l1skills.getItemConsumeCount())) {
                                                // 必要材料をチェック
                                                pc.sendPackets(new S_ServerMessage(299));
                                                // \f1魔法を詠唱するための材料が足りません。
                                                return;
                                            }
                                        }
                                        pc.setCurrentHp(pc.getCurrentHp() - l1skills.getHpConsume());
                                        pc.setCurrentMp(pc.getCurrentMp() - l1skills.getMpConsume());
                                        int lawful = pc.getLawful() + l1skills.getLawful();
                                        if (lawful > 32767) {
                                            lawful = 32767;
                                        }
                                        if (lawful < -32767) {
                                            lawful = -32767;
                                        }
                                        pc.setLawful(lawful);
                                        if (l1skills.getItemConsumeId() != 0) {
                                            // 材料が必要
                                            pc.getInventory().consumeItem(l1skills.getItemConsumeId(),
                                                    l1skills.getItemConsumeCount());
                                        }
                                        pc.getInventory().removeItem(l1iteminstance, 1);
                                        pc.getInventory().storeItem(spellsc);
                                        pc.sendPackets(new S_SystemMessage(spellsc.getName() + "獲得"));
                                    }
                                }
                            } else {
                                pc.sendPackets(new S_ServerMessage(591));
                                // \f1スクロールがその強い魔法を記録するには、あまりにも弱いです。
                            }
                        } else {
                            pc.sendPackets(new S_ServerMessage(264));
                            // \f1あなたのクラスでは、このアイテムは使用できません。
                        }
                        break;

                    case 40314:// ペットネックレス
                    case 40316:// ハイペットネックレス
                        if (pc.getInventory().checkItem(41160)) {
                            // 召喚の笛
                            if (withdrawPet(pc, itemObjid)) {
                                pc.getInventory().consumeItem(41160, 1);
                            }
                        } else {
                            pc.sendPackets(new S_ServerMessage(79));
                            // \f1 何も起こらなかった。
                        }
                        break;

                    case 40315:// ペット笛
                        pc.sendPackets(new S_Sound(437));
                        pc.broadcastPacket(new S_Sound(437));
                        Object[] petList = pc.getPetList().values().toArray();
                        for (Object petObject : petList) {
                            if (petObject instanceof L1PetInstance) { // ペット
                                L1PetInstance pet = (L1PetInstance) petObject;
                                pet.call();
                            }
                        }
                        break;

                    case 40493:// 魔法のフルート
                        pc.sendPackets(new S_Sound(165));
                        pc.broadcastPacket(new S_Sound(165));
                        L1GuardianInstance guardian = null;
                        for (L1Object visible : pc.getKnownObjects()) {
                            if (visible instanceof L1GuardianInstance) {
                                guardian = (L1GuardianInstance) visible;
                                if (guardian.getNpcTemplate().get_npcId() == 70850) { // パン
                                    if (createNewItem(pc, 88, 1)) {
                                        pc.getInventory().removeItem(l1iteminstance, 1);
                                    }
                                }
                            }
                        }
                        break;

                    case 40325:// 2段階の魔法のサイコロ
                        if (pc.getInventory().checkItem(40318, 1)) {
                            int gfxid = 3237 + _random.nextInt(2);
                            pc.sendPackets(new S_SkillSound(pc.getId(), gfxid));
                            pc.broadcastPacket(new S_SkillSound(pc.getId(), gfxid));
                            pc.getInventory().consumeItem(40318, 1);
                        } else {
                            pc.sendPackets(new S_ServerMessage(79)); // \f1
                            // 何も起こらなかった。
                        }
                        break;

                    case 40326:// 3段階の魔法のサイコロ
                        if (pc.getInventory().checkItem(40318, 1)) {
                            int gfxid = 3229 + _random.nextInt(3);
                            pc.sendPackets(new S_SkillSound(pc.getId(), gfxid));
                            pc.broadcastPacket(new S_SkillSound(pc.getId(), gfxid));
                            pc.getInventory().consumeItem(40318, 1);
                        } else {
                            pc.sendPackets(new S_ServerMessage(79)); // \f1何も起こらなかった。
                        }
                        break;

                    case 40327:// 4段階の魔法のサイコロ
                        if (pc.getInventory().checkItem(40318, 1)) {
                            int gfxid = 3241 + _random.nextInt(4);
                            pc.sendPackets(new S_SkillSound(pc.getId(), gfxid));
                            pc.broadcastPacket(new S_SkillSound(pc.getId(), gfxid));
                            pc.getInventory().consumeItem(40318, 1);
                        } else {
                            pc.sendPackets(new S_ServerMessage(79)); // \f1、何も起こらなかった。
                        }
                        break;

                    case 40328:// 6段階魔法のサイコロ
                        if (pc.getInventory().checkItem(40318, 1)) {
                            int gfxid = 3204 + _random.nextInt(6);
                            pc.sendPackets(new S_SkillSound(pc.getId(), gfxid));
                            pc.broadcastPacket(new S_SkillSound(pc.getId(), gfxid));
                            pc.getInventory().consumeItem(40318, 1);
                        } else {
                            // \f1何も起こらなかった。
                            pc.sendPackets(new S_ServerMessage(79));
                        }
                        break;

                    case 210104:
                        Connection connection = null;
                        connection = L1DatabaseFactory.getInstance().getConnection();
                        PreparedStatement preparedstatement = connection.prepareStatement(
                                "UPDATE characters SET LocX=33432,LocY=32807,MapID=4 WHERE account_name=? and MapID not in (5001,99,997,5166,39,34,701,2000)"); // オペレータの部屋、刑務所、バトルゾーン控室
                        // を除く
                        preparedstatement.setString(1, client.getAccountName());
                        preparedstatement.execute();
                        preparedstatement.close();
                        connection.close();
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        pc.sendPackets(new S_SystemMessage("アカウント内のすべてのキャラクターの座標がギラン村に移動されました"));
                        break;

                    case 40089:// 復活スクロール
                    case 140089:// 祝福復活スクロール
                    case 30074:// 象牙の塔の祝福復活
                        // 復活スクロール、祝福された復活スクロール
                        L1Character resobject = (L1Character) L1World.getInstance().findObject(resid);
                        if (resobject != null) {
                            if (resobject instanceof L1PcInstance) {
                                L1PcInstance target = (L1PcInstance) resobject;
                                if (pc.getId() == target.getId()) {
                                    return;
                                }
                                /** ボール成長は復活できないように **/
                                int castle_id = L1CastleLocation.getCastleIdByArea(pc);
                                if (castle_id != 0) {
                                    pc.sendPackets(new S_SystemMessage("利用できない地域です。"));
                                    return;
                                }
                                /** ボール成長は復活できないように **/
                                if (L1World.getInstance().getVisiblePlayer(target, 0).size() > 0) {
                                    for (L1PcInstance visiblePc : L1World.getInstance().getVisiblePlayer(target, 0)) {
                                        if (!visiblePc.isDead()) {
                                            // \f1その場所に他の人が立っているので復活させることができません。
                                            pc.sendPackets(new S_ServerMessage(592));
                                            return;
                                        }
                                    }
                                }
                                if (target.getCurrentHp() == 0 && target.isDead() == true) {
                                    if (pc.getMap().isUseResurrection()) {
                                        target.setTempID(pc.getId());
                                        if (itemId == 40089 || itemId == 30074) {
                                            // また、復活したいですか？ （Y / N）
                                            target.sendPackets(new S_Message_YN(321, ""));
                                        } else if (itemId == 140089) {
                                            // また、復活したいですか？ （Y / N）
                                            target.sendPackets(new S_Message_YN(322, ""));
                                        }
                                    } else {
                                        return;
                                    }
                                }
                            } else if (resobject instanceof L1NpcInstance) {
                                if (!(resobject instanceof L1TowerInstance)) {
                                    L1NpcInstance npc = (L1NpcInstance) resobject;
                                    if (npc instanceof L1PetInstance
                                            && L1World.getInstance().getVisiblePlayer(npc, 0).size() > 0) {
                                        for (L1PcInstance visiblePc : L1World.getInstance().getVisiblePlayer(npc, 0)) {
                                            if (!visiblePc.isDead()) {
                                                // \f1その場所に他の人が立っているので復活させることが
                                                // ありません。
                                                pc.sendPackets(new S_ServerMessage(592));
                                                return;
                                            }
                                        }
                                    } else if (npc.getNpcTemplate().isCantResurrect() && !(npc instanceof L1PetInstance)) {
                                        pc.getInventory().removeItem(l1iteminstance, 1);
                                        return;
                                    }
                                    if (npc instanceof L1PetInstance
                                            && L1World.getInstance().getVisiblePlayer(npc, 0).size() > 0) {
                                        for (L1PcInstance visiblePc : L1World.getInstance().getVisiblePlayer(npc, 0)) {
                                            if (!visiblePc.isDead()) {
                                                // \f1その場所に他の人が立っているので復活させることができません。
                                                pc.sendPackets(new S_ServerMessage(592));
                                                return;
                                            }
                                        }
                                    }
                                    if (npc.getCurrentHp() == 0 && npc.isDead()) {
                                        npc.resurrect(npc.getMaxHp() / 4);
                                        npc.setResurrect(true);
                                    }
                                }
                            }
                        }
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        break;

                    case 40079:// 帰還スクロール
                    case 40095:// 象牙の塔の帰還スクロール
                    case 40521:// フェアリーの羽
                        if (pc.get_DuelLine() != 0) {
                            pc.sendPackets(new S_SystemMessage("バトルゾーン地域で使用することができません。"));
                            return;
                        }
                        if (pc.isDead())
                            return;
                        if (pc.getCurrentHp() < 1)
                            return;

                        if (pc.getMap().isEscapable() || pc.isGm()) {
                            int[] loc = Getback.GetBack_Location(pc, true);
                            new L1Teleport().teleport(pc, loc[0], loc[1], (short) loc[2], pc.getHeading(), true);
                            pc.getInventory().removeItem(l1iteminstance, 1);
                        } else {
                            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
                            pc.sendPackets(new S_ServerMessage(647));
                        }
                        cancelAbsoluteBarrier(pc); // アブ小ガルトバリアの解除
                        break;
                    case 3000106:// 修練ケイブ1階テレポートスクロール
                        if (pc.getMap().isEscapable() || pc.isGm()) {
                            int rx = _random.nextInt(2);
                            int ry = _random.nextInt(2);
                            int ux = 32809 + rx;
                            int uy = 32727 + rx;
                            if (itemId == 3000106) {
                                if (pc.getLevel() >= Config.SKTC_ENTRY_LEVEL & pc.getLevel() <= Config.SKTC_LIMIT_LEVEL) {
                                    new L1Teleport().teleport(pc, ux, uy, (short) 25, pc.getHeading(), true);
                                    pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\fQ[通知]: \\f3[Lv."
                                            + Config.SKTC_ENTRY_LEVEL + "~ " + Config.SKTC_LIMIT_LEVEL + "]\\fQ 適切狩り場です。"));

                                } else {
                                    pc.sendPackets(new S_ChatPacket(pc, "レベル" + Config.SKTC_LIMIT_LEVEL + "以上から入場が不可能です。"));
                                }

                            }
                            pc.setSkillEffect(L1SkillId.ABSOLUTE_BARRIER, 3000);
                            pc.getInventory().removeItem(l1iteminstance, 1);
                        } else {
                            pc.sendPackets(new S_ServerMessage(647));
                        }
                        cancelAbsoluteBarrier(pc); // アブ小ガルトバリアの解除
                        break;
                    case 40824:// 修練ケイブ2階テレポートスクロール
		    /*
		     * if (pc.geticedungeonTime() >= 29) { pc.sendPackets(new
		     * S_ChatPacket(pc, "スクロールでダンジョンタイマークリックしようとしたときにチェックする "））;
		     * return;}
		     */
                        if (pc.getMap().isEscapable() || pc.isGm()) {
                            int rx = _random.nextInt(2);
                            int ry = _random.nextInt(2);
                            int ux = 32807 + rx;
                            int uy = 32747 + rx;
                            if (itemId == 40824) {
                                new L1Teleport().teleport(pc, ux, uy, (short) 26, pc.getHeading(), true);
                            }
                            pc.setSkillEffect(L1SkillId.ABSOLUTE_BARRIER, 3000);
                            pc.getInventory().removeItem(l1iteminstance, 1);
                        } else {
                            pc.sendPackets(new S_ServerMessage(647));
                        }
                        cancelAbsoluteBarrier(pc); // アブ小ガルトバリアの解除
                        break;
                    case 40825:// 修練ケイブ3階テレポートスクロール
		    /*
		     * if (pc.geticedungeonTime() >= 29) { pc.sendPackets(new
		     * S_ChatPacket(pc, "スクロールでダンジョンタイマークリックしようとしたときにチェックする "））;
		     * return;}
		     */
                        if (pc.getMap().isEscapable() || pc.isGm()) {
                            int rx = _random.nextInt(2);
                            int ry = _random.nextInt(2);
                            int ux = 32810 + rx;
                            int uy = 32765 + rx;
                            if (itemId == 40825) {
                                new L1Teleport().teleport(pc, ux, uy, (short) 27, pc.getHeading(), true);
                            }
                            pc.setSkillEffect(L1SkillId.ABSOLUTE_BARRIER, 3000);
                            pc.getInventory().removeItem(l1iteminstance, 1);
                        } else {
                            pc.sendPackets(new S_ServerMessage(647));
                        }
                        cancelAbsoluteBarrier(pc); // アブ小ガルトバリアの解除
                        break;
                    case 40826:// 修練ケイブ4階テレポートスクロール
		    /*
		     * if (pc.geticedungeonTime() >= 29) { pc.sendPackets(new
		     * S_ChatPacket(pc, "スクロールでダンジョンタイマークリックしようとしたときにチェックする "））;
		     * return;}
		     */
                        if (pc.getMap().isEscapable() || pc.isGm()) {
                            int rx = _random.nextInt(2);
                            int ry = _random.nextInt(2);
                            int ux = 32799 + rx;
                            int uy = 32798 + rx;
                            if (itemId == 40826) {
                                new L1Teleport().teleport(pc, ux, uy, (short) 28, pc.getHeading(), true);
                            }
                            pc.setSkillEffect(L1SkillId.ABSOLUTE_BARRIER, 3000);
                            pc.getInventory().removeItem(l1iteminstance, 1);
                        } else {
                            pc.sendPackets(new S_ServerMessage(647));
                        }
                        cancelAbsoluteBarrier(pc); // アブ小ガルトバリアの解除
                        break;
                    case 41159:// ピクシーの羽
                    case 41921:// ピクシーの金色の羽
                        pc.sendPackets(new S_SystemMessage("\\aA警告: ギラン村 \\aG'ピアル'\\aA に移動すること。"));
		    /*
		     * if (pc.getMapId() == 1700 || pc.getMapId() == 1701 ||
		     * pc.getMapId() == 1702 || pc.getMapId() == 1703 ||
		     * pc.getMapId() == 1704 || pc.getMapId() == 1705) {
		     * pc.sendPackets(new
		     * S_SystemMessage("警告：忘れられた島では使用できません。 "））; return; } if
		     * (pc.getMap().isEscapable() || pc.isGm()) { int rx =
		     * _random.nextInt(7); int ux = 32768 + rx; int uy = 32834 +
		     * rx; //象牙の塔 if (itemId == 41159 || itemId == 41921) { new
		     * L1Teleport().teleport(pc, ux, uy, (short) 622,
		     * pc.getHeading(), true); }
		     * pc.setSkillEffect(L1SkillId.ABSOLUTE_BARRIER, 3000);
		     * pc.getInventory().removeItem(l1iteminstance, 1); } else {
		     * pc.sendPackets(new S_ServerMessage(647)); }
		     * cancelAbsoluteBarrier(pc); // アブ小ガルトバリアの解除
		     */
                        break;
                    case 40100:// テレポートスクロール
                    case 40099:// 象牙の塔のテレポートスクロール
                    case 40086:// マステレポートスクロール
                    case 40863:// 強化スクロール（テレポート）
                    case 140100:// 祝福テレポートスクロール
                        Telbookitem.clickItem(pc, itemId, bookmark_x, bookmark_y, bookmark_mapid, l1iteminstance);
                        break;
                    /** 傲慢の塔テレポートスクロール **/
                    case 830001:
                    case 830002:
                    case 830003:
                    case 830004:
                    case 830005:
                    case 830006:
                    case 830007:
                    case 830008:
                    case 830009:
                    case 830010:
                    case 830011:
                        omanTel.clickItem(pc, itemId, l1iteminstance);
                        break;
                    /** 傲慢の塔テレポートスクロール **/

                    /** 傲慢の塔テレポートアミュレット、支配お守り **/
                    case 830012:
                    case 830013:
                    case 830014:
                    case 830015:
                    case 830016:
                    case 830017:
                    case 830018:
                    case 830019:
                    case 830020:
                    case 830021:
                    case 830022:
                    case 830023:
                    case 830024:
                    case 830025:
                    case 830026:
                    case 830027:
                    case 830028:
                    case 830029:
                    case 830030:
                    case 830031:
                        OmanAmulet.clickItem(pc, itemId, l1iteminstance);
                        break;
                    /** 傲慢の塔テレポートアミュレット、支配お守り **/

                    /** 生命の葉 **/
                    case 31086:
                        LeafItem.clickItem(pc, itemId, l1iteminstance, l1iteminstance1);
                        break;

                    /** 傲慢の塔混沌お守り、変異されたお守り **/
                    case 830042:
                    case 830043:
                    case 830044:
                    case 830045:
                    case 830046:
                    case 830047:
                    case 830048:
                    case 830049:
                    case 830050:
                    case 830051:
                    case 830052:
                    case 830053:
                    case 830054:
                    case 830055:
                    case 830056:
                    case 830057:
                    case 830058:
                    case 830059:
                    case 830060:
                    case 830061:
                        OmanRandomAmulet.clickItem(pc, itemId, l1iteminstance);
                        break;

                    case 40901:
                    case 40902:
                    case 40903:
                    case 40904:
                    case 40905:
                    case 40906:
                    case 40907:
                    case 40908: { // 各種の婚約指輪
                        L1PcInstance partner = null;
                        boolean partner_stat = false;
                        if (pc.getHellTime() > 0 || pc.get_DuelLine() != 0) {
                            pc.sendPackets(new S_ChatPacket(pc, "地獄では使用できません。"));
                            return;
                        }
                        int chargeCount = l1iteminstance.getChargeCount();
                        if (pc.getPartnerId() != 0) { // 結婚中
                            partner = (L1PcInstance) L1World.getInstance().findObject(pc.getPartnerId());
                            if (chargeCount > 0) {
                                if (partner != null && partner.getPartnerId() != 0 && pc.getPartnerId() == partner.getId()
                                        && partner.getPartnerId() == pc.getId()) {
                                    partner_stat = true;
                                    l1iteminstance.setChargeCount(l1iteminstance.getChargeCount() - 1);
                                    pc.getInventory().updateItem(l1iteminstance, L1PcInventory.COL_CHARGE_COUNT);
                                }
                            }
                        } else {
                            pc.sendPackets(new S_ServerMessage(662)); // \f1あなたは結婚していない。
                            return;
                        }

                        if (partner_stat) {
                            boolean castle_area = L1CastleLocation.checkInAllWarArea(// いくつかの性リア
                                    partner.getX(), partner.getY(), partner.getMapId());
                            if (castle_area == true || partner.isDead() || partner.getMapId() == 603
                                    || partner.getMapId() == 255 || partner.getMapId() == 777 || partner.getMapId() == 778
                                    || partner.getMapId() == 39 || partner.getMapId() == 5167 || partner.getMapId() == 5153
                                    || partner.getMapId() == 5001 || (partner.getMapId() > 190 && partner.getMapId() < 201)
                                    || (partner.getMapId() > 255 && partner.getMapId() < 260) || partner.getMapId() == 23
                                    || partner.getMapId() == 5153 || partner.getMapId() == 5001 || partner.getMapId() == 24
                                    || (partner.getMapId() > 239 && partner.getMapId() < 244)
                                    || (partner.getMapId() > 247 && partner.getMapId() < 252)) {
                                pc.sendPackets(new S_SystemMessage("あなたのパートナーは、死んでいるか、行くことができないところでプレイしています。"));
                            }
                        } else if (l1iteminstance.getChargeCount() > 0) {
                            pc.sendPackets(new S_ServerMessage(546));
                            // \f1あなたのパートナーは、今すぐプレーをしていません。
                        } else {
                            pc.sendPackets(new S_ServerMessage(79));
                            // \f1 何も起こらなかった。
                        }
                    }
                    break;

                    case 40555:// 秘密の部屋の鍵
                        if (pc.isKnight()
                                && (pc.getX() >= 32806 && // クリップボード部屋
                                pc.getX() <= 32814)
                                && (pc.getY() >= 32798 && pc.getY() <= 32807) && pc.getMapId() == 13) {
                            short mapid = 13;
                            new L1Teleport().teleport(pc, 32815, 32810, mapid, 5, false);
                        } else {
                            pc.sendPackets(new S_ServerMessage(79));
                            // \f1 何も起こらなかった。
                        }
                        break;

                    case 40417:// ソウルクリスタル
                        if ((pc.getX() >= 32667 && pc.getX() <= 32673)// 海賊島
                                && (pc.getY() >= 32978 && pc.getY() <= 32984) && pc.getMapId() == 440) {
                            short mapid = 430;
                            new L1Teleport().teleport(pc, 32922, 32812, mapid, 5, true);
                        } else {
                            pc.sendPackets(new S_ServerMessage(79));
                            // \f1 何も起こらなかった。
                        }
                        break;

                    case 40566:// 神秘的な空の殻
                        if (pc.isElf() && (pc.getX() >= 33971 &&
                                // 象牙の塔の村の南にある魔方陣の座標
                                pc.getX() <= 33975) && (pc.getY() >= 32324 && pc.getY() <= 32328) && pc.getMapId() == 4
                                && !pc.getInventory().checkItem(40548)) { // 亡霊の袋
                            boolean found = false;
                            L1MonsterInstance mob = null;
                            for (L1Object obj : L1World.getInstance().getObject()) {
                                if (obj instanceof L1MonsterInstance) {
                                    mob = (L1MonsterInstance) obj;
                                    if (mob != null) {
                                        if (mob.getNpcTemplate().get_npcId() == 45300) {
                                            found = true;
                                            break;
                                        }
                                    }
                                }
                            }
                            if (found) {
                                pc.sendPackets(new S_ServerMessage(79)); // \f1何も起こらなかった。
                            } else {
                                L1SpawnUtil.spawn(pc, 45300, 0, 0); // 古代人の亡霊
                            }
                        } else {
                            pc.sendPackets(new S_ServerMessage(79)); // \f1
                            // 何も起こらなかった。
                        }
                        break;

                    case 40557:// 暗殺リスト（グルーディン村）
                        if (pc.getX() == 32620 && pc.getY() == 32641 && pc.getMapId() == 4) {
                            for (L1Object object : L1World.getInstance().getObject()) {
                                if (object instanceof L1NpcInstance) {
                                    L1NpcInstance npc = (L1NpcInstance) object;
                                    if (npc.getNpcTemplate().get_npcId() == 45883) {
                                        pc.sendPackets(new S_ServerMessage(79));
                                        return;
                                    }
                                }
                            }
                            L1SpawnUtil.spawn(pc, 45883, 0, 300000);
                        } else {
                            pc.sendPackets(new S_ServerMessage(79));
                        }
                        break;
                    case 40558:// 暗殺リスト（ギラン村）
                        if (pc.getX() == 33513 && pc.getY() == 32890 && pc.getMapId() == 4) {
                            L1NpcInstance npc = null;
                            for (L1Object object : L1World.getInstance().getObject()) {
                                if (object instanceof L1NpcInstance) {
                                    npc = (L1NpcInstance) object;
                                    if (npc.getNpcTemplate().get_npcId() == 45889) {
                                        pc.sendPackets(new S_ServerMessage(79));
                                        return;
                                    }
                                }
                            }
                            L1SpawnUtil.spawn(pc, 45889, 0, 300000);
                        } else {
                            pc.sendPackets(new S_ServerMessage(79));
                        }
                        break;
                    case 40559:// 暗殺リスト（アデン）
                        if (pc.getX() == 34215 && pc.getY() == 33195 && pc.getMapId() == 4) {
                            L1NpcInstance npc = null;
                            for (L1Object object : L1World.getInstance().getObject()) {
                                if (object instanceof L1NpcInstance) {
                                    npc = (L1NpcInstance) object;
                                    if (npc.getNpcTemplate().get_npcId() == 45888) {
                                        pc.sendPackets(new S_ServerMessage(79));
                                        return;
                                    }
                                }
                            }
                            L1SpawnUtil.spawn(pc, 45888, 0, 300000);
                        } else {
                            pc.sendPackets(new S_ServerMessage(79));
                        }
                        break;
                    case 40560:// 暗殺リスト（ウッドベック村）
                        if (pc.getX() == 32580 && pc.getY() == 33260 && pc.getMapId() == 4) {
                            for (L1Object object : L1World.getInstance().getObject()) {
                                if (object instanceof L1NpcInstance) {
                                    L1NpcInstance npc = (L1NpcInstance) object;
                                    if (npc.getNpcTemplate().get_npcId() == 45886) {
                                        pc.sendPackets(new S_ServerMessage(79));
                                        return;
                                    }
                                }
                            }
                            L1SpawnUtil.spawn(pc, 45886, 0, 300000);
                        } else {
                            pc.sendPackets(new S_ServerMessage(79));
                        }
                        break;
                    case 40561:// 暗殺リスト（ケント村）
                        if (pc.getX() == 33046 && pc.getY() == 32806 && pc.getMapId() == 4) {
                            for (L1Object object : L1World.getInstance().getObject()) {
                                if (object instanceof L1NpcInstance) {
                                    L1NpcInstance npc = (L1NpcInstance) object;
                                    if (npc.getNpcTemplate().get_npcId() == 45885) {
                                        pc.sendPackets(new S_ServerMessage(79));
                                        return;
                                    }
                                }
                            }
                            L1SpawnUtil.spawn(pc, 45885, 0, 300000);
                        } else {
                            pc.sendPackets(new S_ServerMessage(79));
                        }
                        break;
                    case 40562:// 暗殺リスト（ハイネ）
                        if (pc.getX() == 33447 && pc.getY() == 33476 && pc.getMapId() == 4) {
                            for (L1Object object : L1World.getInstance().getObject()) {
                                if (object instanceof L1NpcInstance) {
                                    L1NpcInstance npc = (L1NpcInstance) object;
                                    if (npc.getNpcTemplate().get_npcId() == 45887) {
                                        pc.sendPackets(new S_ServerMessage(79));
                                        return;
                                    }
                                }
                            }
                            L1SpawnUtil.spawn(pc, 45887, 0, 300000);
                        } else {
                            pc.sendPackets(new S_ServerMessage(79));
                        }
                        break;
                    case 40563:// 暗殺リスト（火田村）
                        if (pc.getX() == 32730 && pc.getY() == 32426 && pc.getMapId() == 4) {
                            for (L1Object object : L1World.getInstance().getObject()) {
                                if (object instanceof L1NpcInstance) {
                                    L1NpcInstance npc = (L1NpcInstance) object;
                                    if (npc.getNpcTemplate().get_npcId() == 45884) {
                                        pc.sendPackets(new S_ServerMessage(79));
                                        return;
                                    }
                                }
                            }
                            L1SpawnUtil.spawn(pc, 45884, 0, 300000);
                        } else {
                            pc.sendPackets(new S_ServerMessage(79));
                        }
                        break;

                    case 40572:// アサシンの印
                        if (pc.getX() == 32778 && pc.getY() == 32738 && pc.getMapId() == 21) {
                            new L1Teleport().teleport(pc, 32781, 32728, (short) 21, 5, true);
                        } else if (pc.getX() == 32781 && pc.getY() == 32728 && pc.getMapId() == 21) {
                            new L1Teleport().teleport(pc, 32778, 32738, (short) 21, 5, true);
                        } else {
                            pc.sendPackets(new S_ServerMessage(79));
                        }
                        break;

                    // case 410040: //ドラゴンキー
                    // pc.sendPackets(new S_ServerMessage（625））; //移動ポータルをクリック
                    // pc.sendPackets(new S_PacketBox(S_PacketBox.DragonMenu,
                    // l1iteminstance));
                    // break;
                    case 490012:
                    case 490013:
                    case 490014:// ドラゴンキー
                        // if (pc.getMapId() != 445) {
                        // pc.sendPackets(new
                        // S_SystemMessage("システムメッセージ：ドラゴンキーは隠された船着場でのみ使用可能です。"));
                        // return;
                        // }
                        int Castle_Id = L1CastleLocation.getCastleIdByArea(pc);
                        if (Castle_Id != 0) {
                            pc.sendPackets(new S_ServerMessage(1892));
                            return;
                        }
                        if (pc.getMapId() == 1005 || pc.getMapId() == 1011 || pc.getMapId() > 6000 && pc.getMapId() < 6999
                                || pc.getMapId() >= 1017 && pc.getMapId() <= 1022) {
                            pc.sendPackets(new S_ServerMessage(1892));
                            return;
                        }
                        switch (itemId) {
                            case 490012:
                                if (AntarasRaidSystem.getInstance().countRaidPotal() >= 99) {
                                    pc.sendPackets(new S_SystemMessage("システムメッセージ：まだドラゴンがすべて目を覚ましています。"));
                                    return;
                                }
                                AntarasRaidSystem.getInstance().startRaid(pc);
                                pc.getInventory().consumeItem(490012, 1);
                                L1World.getInstance().broadcastPacketToAll(new S_ChatPacket(pc, "誰かがドラゴンキーアンタラスポータルを開設しました。"));
                                break;
                            case 490013:
                                if (FafurionRaidSystem.getInstance().countRaidPotal1() >= 99) {
                                    pc.sendPackets(new S_SystemMessage("システムメッセージ：まだドラゴンがすべて目を覚ましています。"));
                                    return;
                                }
                                FafurionRaidSystem.getInstance().startRaid(pc);
                                pc.getInventory().consumeItem(490013, 1);
                                L1World.getInstance().broadcastPacketToAll(new S_ChatPacket(pc, "誰かがドラゴンキーパプリオンポータルを開設しました。"));
                                break;
                            case 490014:
                                if (RindSystem.getInstance().countRind() >= 99) {
                                    pc.sendPackets(new S_SystemMessage("システムメッセージ：まだドラゴンがすべて目を覚ましています。"));
                                    return;
                                }
                                RindSystem.getInstance().startRind(pc);
                                pc.getInventory().consumeItem(490014, 1);
                                L1World.getInstance().broadcastPacketToAll(new S_ChatPacket(pc, "誰かがドラゴンキーリンドビオルポータルを開設しました。"));
                                break;
                            default:
                                break;
                        }
                        break;
                    case 490015:
                        if (ValaRaidSystem.getInstance().countVala() >= 5) {
                            pc.sendPackets(new S_SystemMessage("システムメッセージ：まだドラゴンがすべて目を覚ましています。"));
                            return;
                        }
                        ValaRaidSystem.getInstance().startValakas(pc);
                        pc.getInventory().consumeItem(490015, 1);
                        L1World.getInstance().broadcastPacketToAll(new S_ChatPacket(pc, "誰かがドラゴンキーヴァラカスポータルを開設しました。"));
                        break;

                    case 700022:// 記憶拡張玉
                        if (pc.getMark_count() < 100) {
                            int booksize = pc.getMark_count() + 10;
                            pc.setMark_count(booksize);
                            pc.sendPackets(new S_PacketBox(S_PacketBox.BOOKMARK_SIZE_PLUS_10, booksize));
                            pc.getInventory().removeItem(l1iteminstance, 1);
                            pc.save();
                        } else {
                            pc.sendPackets(new S_ServerMessage(2930));
                        }
                        break;

                    case 3000048: // エルフの心
                        pc.sendPackets(new S_ChatPacket(pc, "誕生日のお祝いの妖精に「コマ」を一度受けることができる。"));
                        break;
                    case 60032: // 古い古書
                        pc.sendPackets(
                                new S_ChatPacket(pc, "55レベル以上になると、ウィザードの研究室歳成し遂げたと尋ねると、エリクサールーンと交換することができる。 （賢者の石が必要）"));
                        break;
                    case 60033: // 色あせた古書
                        pc.sendPackets(new S_ChatPacket(pc, "70レベル以上になると、ウィザードの研究室ネルウァに訪ねて行けばエリクサールーンと交換することができる。"));
                        break;
                    case 60028: // 馬力の古書
                        pc.sendPackets(new S_ChatPacket(pc, "？レベル以上になると、ウィザードの研究室で上位エリクサールーンと交換することができる。 （賢者の石が必要）"));
                        break;

                    case 3000035:// 戦闘支援箱
                    case 3000046:
                        additem.clickItem(pc, itemId, l1iteminstance);
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        break;
		/*
		 * case 800200:// パッケージ1次 if (pc.getInventory().getSize() > 120)
		 * { pc.sendPackets(new S_ChatPacket(pc, "所持しているアイテムが多すぎます。 "））;
		 * return; } if (pc.getInventory().getWeight100() > 82) {
		 * //この部分を変更すると、エラーが発生する。 pc.sendPackets(new S_ChatPacket(pc,
		 * "持ち物が重すぎて使用することはできません。 "））; return; } if
		 * (pc.getInventory().checkItem(447011, 1)) { //
		 * チェックされているアイテムと数量 pc.getInventory().removeItem(l1iteminstance,
		 * 1); createNewItem2(pc, 800001, 1, 0); // 武器コイン
		 * createNewItem2(pc, 800002, 1, 0); // 投球コイン createNewItem2(pc,
		 * 800003, 1, 0); // Tシャツコイン createNewItem2(pc, 800004, 1, 0);
		 * // マントコイン createNewItem2(pc, 800005, 1, 0); // 鎧コイン
		 * createNewItem2(pc, 800006, 1, 0); // 盾コイン createNewItem2(pc,
		 * 800007, 1, 0); // 手袋コイン createNewItem2(pc, 800008, 1, 0); //
		 * ブーツコイン createNewItem2(pc, 800009, 1, 0); // ネックレスコイン
		 * createNewItem2(pc, 800010, 1, 0); // イヤリングコイン
		 * createNewItem2(pc, 800011, 1, 0); // リングコイン
		 * createNewItem2(pc, 800012, 1, 0); // ベルトコイン
		 * createNewItem2(pc, 800013, 1, 0); // 人形コイン createNewItem2(pc,
		 * 800014, 1, 0); //ゲートルコイン createNewItem2(pc, 40308, 50000000,
		 * 0); // アデナ createNewItem2(pc, 3000119, 1, 0); //パッケージの移動お守り
		 * if (pc.is転写（））{ createNewItem2(pc, 203006, 1, 9); // 台風の斧 } }
		 * break;
		 */
                    case 447011:// 新規装備箱
                        if (pc.getInventory().getSize() > 120) {
                            pc.sendPackets(new S_ChatPacket(pc, "所持しているアイテムが多すぎます。"));
                            return;
                        }
                        if (pc.getInventory().getWeight100() > 82) { // この部分を変更すると、エラーが発生する。
                            pc.sendPackets(new S_ChatPacket(pc, "持ち物が重すぎて使用することはできません。"));
                            return;
                        }
                        if (pc.getInventory().checkItem(447011, 1)) { // チェックされているアイテムと数量
                            pc.getInventory().removeItem(l1iteminstance, 1);
                            if (pc.isKnight()) {
                                createNewItem2(pc, 40014, 10, 0); // 修練者のブレイブポーション
                                createNewItem2(pc, 35, 1, 9); // +0修練者の片手剣
                                createNewItem2(pc, 7, 1, 9); // +0修練者の片手剣
                                createNewItem2(pc, 48, 1, 9); // +0修練者の片手剣
                            }
                            if (pc.isDragonknight()) {
                                createNewItem2(pc, 210035, 10, 0); // 修練者の刻印のボーンピース
                                createNewItem2(pc, 48, 1, 9); // +0修練者の両手剣
                            }
                            if (pc.isCrown()) {
                                createNewItem2(pc, 40031, 10, 0); // 修練者の悪魔の血
                                createNewItem2(pc, 35, 1, 9); // +0修練者の片手剣
                            }
                            if (pc.isWizard()) {
                                createNewItem2(pc, 40016, 5, 0); // 修練者の知恵のポーション
                                createNewItem2(pc, 40015, 5, 0); // 修練者のマナ回復ポーション
                                createNewItem2(pc, 120, 1, 9); // +0修練者の杖
                            }
                            if (pc.isBlackwizard()) {
                                createNewItem2(pc, 210036, 10, 0); // 修練者のユグドラの実
                                createNewItem2(pc, 147, 1, 9); // +0修練者の斧
                            }
                            if (pc.isElf()) {
                                createNewItem2(pc, 40068, 10, 0); // 修練者のエルヴンワッフル
                                createNewItem2(pc, 40319, 10, 0); // 修練者の精霊の玉
                                createNewItem2(pc, 40747, 2000, 0);// ブラックミスリルアロー
                                createNewItem2(pc, 174, 1, 9); // +0修練者の弓
                            }
                            if (pc.isDarkelf()) {
                                createNewItem2(pc, 30080, 10, 0); // 修練者の黒曜石
                                createNewItem2(pc, 156, 1, 9); // +0修練者のクロウ
                            }
                            if (pc.isWarrior()) {
                                createNewItem2(pc, 40014, 10, 0); // 修練者のブレイブポーション
                                createNewItem2(pc, 147, 1, 9); // +0修練者の斧
                                createNewItem2(pc, 147, 1, 9); // +0修練者の斧
                            }
                            if (pc.isKnight() || pc.isCrown() || pc.isDarkelf() || pc.isDragonknight() || pc.isWarrior()) {
                                createNewItem2(pc, 22300, 1, 6); // +0修練者のレザーヘルム
                                createNewItem2(pc, 22301, 1, 6); // +0修練者のレザーアーマー
                                createNewItem2(pc, 22302, 1, 6); // +0修練者のマント
                                createNewItem2(pc, 22303, 1, 6); // +0修練者のレザーグローブ
                                createNewItem2(pc, 22304, 1, 6); // +0修練者のレザーサンダル
                            }
                            if (pc.isElf() || pc.isBlackwizard() || pc.isWizard()) {
                                createNewItem2(pc, 22306, 1, 6); // +0修練者のレザーヘルム
                                createNewItem2(pc, 22307, 1, 6); // +0修練者のレザーアーマー
                                createNewItem2(pc, 22308, 1, 6); // +0修練者のマント
                                createNewItem2(pc, 22309, 1, 6); // +0修練者のレザーグローブ
                                createNewItem2(pc, 22310, 1, 6); // +0修練者のレザーサンダル
                            }
                            createNewItem2(pc, 40022, 100, 0); // 迅速体力回復剤
                            createNewItem2(pc, 40013, 10, 0); // 速度向上ポーション
                            createNewItem2(pc, 40100, 100, 0); // テレポートスクロール
                            createNewItem2(pc, 22337, 1, 0); // 修練者のベルト
                            createNewItem2(pc, 22312, 1, 5); // 修練者のTシャツ
                            createNewItem2(pc, 321515, 1, 5); // 修練者のTシャツ
                            createNewItem2(pc, 301066, 1, 0); // ギラン村お守り
                            createNewItem2(pc, 40308, 100000, 0); // アデナ
                            createNewItem2(pc, 99115, 3, 0); // クラウディア帰還ジュムソ
                            createNewItem2(pc, 210039, 3, 0); // ハブ
                            createNewItem2(pc, 41246, 1000, 0); // 結晶
                        }
                        break;
		/*
		 * case 30135:// LV10フェアリーのお祝いの贈り物 //
		 * createNewItem2（pc、40117、1、0）; //はナイトの村帰還スクロール if (pc.isElf())
		 * { createNewItem2（pc、40233、1、0）; //ボディトゥマインド
		 * createNewItem2（pc、40234、1、0）; //テレポートトゥマザー } else if
		 * (pc.isBlackwizard()) { createNewItem2（pc、210004、1、0）;
		 * //キューブ（イグニッション） createNewItem2（pc、210000、1、0）; //ミラーイメージ
		 * createNewItem2（pc、210001、1、0）; //コンフュージョン }
		 * pc.getInventory().removeItem(l1iteminstance, 1); break; case
		 * 30136:// LV15フェアリーのお祝いの贈り物 if (pc.isCrown()) {
		 * createNewItem2（pc、40226、1、0）; //トゥルーターゲット } else if
		 * (pc.isWizard()) { createNewItem2（pc、40188、1、0）; //ヘイスト
		 * createNewItem2（pc、40176、1、0）; //メディテーション } else if
		 * (pc.isDarkelf()) { createNewItem2（pc、40268、1、0）; //ブリングストーン }
		 * else if (pc.isDragonknight()) { createNewItem2(pc,
		 * 210020、1、0）; //ドラゴンスキン createNewItem2(pc,210021、1、0）;
		 * //バーニングスラッシュ createNewItem2(pc、210025、1、0）; //ブラッドラスト } else
		 * if (pc.is転写（））{ createNewItem2(pc, 210128, 1, 0); //
		 * 戦士の引張（スレイヤー） } pc.getInventory().removeItem(l1iteminstance,
		 * 1); break; case 30137:// LV20フェアリーのお祝いの贈り物 createNewItem2(pc,
		 * 30028, 1, 0); //象牙の塔の武器強化スクロール createNewItem2(pc, 30027, 4,
		 * 0); // 象牙の塔の防具強化スクロール createNewItem2(pc, 22338, 1, 0); //
		 * 修練者のリング if (pc.isWizard()) { createNewItem2(pc, 40170, 1, 0);
		 * // ファイアボール } pc.getInventory().removeItem(l1iteminstance, 1);
		 * break; case 30138:// LV25 フェアリーのお祝いの贈り物 createNewItem2(pc,
		 * 30028, 1, 0); // 象牙の塔の武器強化スクロール createNewItem2(pc, 30027, 4,
		 * 0); // 象牙の塔の防具強化スクロール createNewItem2(pc, 22338, 1, 0);
		 * //修練者のリング if (pc.isWizard()) { createNewItem2(pc, 40188, 1,
		 * 0); //ヘイスト } pc.getInventory().removeItem(l1iteminstance, 1);
		 * break; case 30139:// LV30 フェアリーのお祝いの贈り物 createNewItem2(pc,
		 * 30028, 1, 0); // 象牙の塔の武器強化スクロール createNewItem2(pc, 30027, 4,
		 * 0); // 象牙の塔の防具強化スクロール if (pc.isCrown()) { createNewItem2(pc,
		 * 40228, 1, 0); //コールクレン } else if (pc.isDarkelf()) {
		 * createNewItem2(pc, 40270, 1, 0); // ムービングエクセルレーション } else if
		 * (pc.isDragonknight()) { createNewItem2(pc, 210025, 1, 0); //
		 * ブラッドラスト createNewItem2(pc, 210026, 1, 0); // ポースレイヤー } else
		 * if (pc.isBlackwizard()) { createNewItem2(pc, 210014, 1, 0);
		 * // キューブ（ショック） } else if (pc.is転写（））{ createNewItem2(pc,
		 * 210121, 1, 0); // 戦士の引張（ハウル） }
		 * pc.getInventory().removeItem(l1iteminstance, 1); break; case
		 * 30140:// LV35フェアリーのお祝いの贈り物 createNewItem2(pc, 30028, 1, 0);
		 * // 象牙の塔の武器強化スクロール createNewItem2(pc, 30027, 4, 0); //
		 * 象牙の塔の防具強化スクロール createNewItem2(pc, 22339, 1, 0); // 修練者のネックレス
		 * pc.getInventory().removeItem(l1iteminstance, 1); break; case
		 * 30141:// LV40フェアリーのお祝いの贈り物 createNewItem2(pc, 410012, 5, 0);
		 * // 戦闘強化のスクロール if (pc.isElf()) { createNewItem2(pc, 40243, 1,
		 * 0); // レッサーエレメンタル }
		 * pc.getInventory().removeItem(l1iteminstance, 1); break; case
		 * 30142:// LV45フェアリーのお祝いの贈り物 createNewItem2(pc, 22073, 1, 0);
		 * // 修練者のイヤリング if (pc.isDarkelf()) { createNewItem2(pc, 40276,
		 * 1, 0); // アンキャニードッジ } else if (pc.is転写（））{ createNewItem2(pc,
		 * 210126, 1, 0); // 戦士の引張（クラッシュ） }
		 * pc.getInventory().removeItem(l1iteminstance, 1); break; case
		 * 30143:// LV50 フェアリーのお祝いの贈り物 createNewItem2(pc, 200000, 3, 0);
		 * // 回想のキャンドル pc.getInventory().removeItem(l1iteminstance, 1);
		 * break; case 30144: // LV52フェアリーのお祝いギフト if
		 * (pc.getInventory().getSize() > 120) { pc.sendPackets(new
		 * S_ChatPacket(pc, "所持しているアイテムが多すぎます。 "））; return; } if
		 * (pc.getInventory().getWeight100() > 82) { // この部分修正すると、エラー出る
		 * pc.sendPackets(new S_ChatPacket(pc,
		 * "持ち物が重すぎて使用することはできません。 "））; return; } if
		 * (pc.getInventory().checkItem(30144, 1)) {
		 * pc.getInventory().consumeItem(30144, 1);
		 *
		 * createNewItem2(pc, 60032, 1, 0); // 古い古書 createNewItem2(pc,
		 * 140100, 10, 0); // チュクスン if (pc.isCrown()) {
		 * createNewItem2(pc, 51, 1, 0); // 黄金バトン createNewItem2(pc,
		 * 20051, 1, 0); // 君主の威厳 } if (pc.isKnight()) {
		 * createNewItem2(pc, 56, 1, 0); // デスブレード createNewItem2(pc,
		 * 20318, 1, 0); // 容器のベルト } if (pc.isWizard()) {
		 * createNewItem2(pc, 20225, 1, 0); // マナクリスタル
		 * createNewItem2(pc, 20055, 1, 0); // マナマント } if (pc.isElf()) {
		 * createNewItem2(pc, 50, 1, 0); // 炎の剣 createNewItem2(pc, 184,
		 * 1, 0); // 炎の弓 } if (pc.isDarkelf()) { createNewItem2(pc, 13,
		 * 1, 0); // フィンガーオブデス createNewItem2(pc, 20195, 1, 0); // 影ブーツ
		 * } if (pc.isDragonknight()) { createNewItem2(pc, 500, 1, 0);
		 * // デストラクタのチェーンソード createNewItem2(pc, 22001, 1, 0); // 用ビヌルガより
		 * } if (pc.isBlackwizard()) { createNewItem2(pc, 503, 1, 0); //
		 * サファイアキーリンク createNewItem2(pc, 22006, 1, 0); //幻術師の魔法書 } if
		 * (pc.is転写（））{ createNewItem2(pc, 22365, 1, 0); // 前社団投球
		 * createNewItem2(pc, 203014, 1, 0); // 鍛冶屋の斧 } } break;
		 */
                    case 30127: // 52レベルのクエストアイテムボックス
                        if (pc.getInventory().getSize() > 120) {
                            pc.sendPackets(new S_ChatPacket(pc, "所持しているアイテムが多すぎます。"));
                            return;
                        }
                        if (pc.getInventory().getWeight100() > 82) { // 28 == 100%
                            pc.sendPackets(new S_ChatPacket(pc, "持ち物が重すぎて使用することはできません。"));
                            return;
                        }
                        if (pc.getInventory().checkItem(30127, 1)) {
                            pc.getInventory().consumeItem(30127, 1);
                            if (pc.isCrown()) {
                                createNewItem2(pc, 51, 1, 0);
                            }
                            if (pc.isKnight()) {
                                createNewItem2(pc, 56, 1, 0);
                            }
                            if (pc.isWizard()) {
                                createNewItem2(pc, 20225, 1, 0);
                            }
                            if (pc.isElf()) {
                                createNewItem2(pc, 184, 1, 0);
                            }
                            if (pc.isDarkelf()) {
                                createNewItem2(pc, 13, 1, 0);
                            }
                            if (pc.isDragonknight()) {
                                createNewItem2(pc, 500, 1, 0);
                            }
                            if (pc.isBlackwizard()) {
                                createNewItem2(pc, 503, 1, 0);
                            }
                            if (pc.isWarrior()) {
                                createNewItem2(pc, 22365, 1, 0);
                            }
                        }
                        break;

                    case 40007:
                    case 40412:
                    case 40006:
                    case 140006: {// 黒檀の棒白檀バー
                        if (pc.isInvisble()) {
                            return;
                        }
                        cancelAbsoluteBarrier(pc); // アブ小ガルトバリアの解除
                        L1Object target = L1World.getInstance().findObject(spellsc_objid);

                        if (target != null) {
                            doWandAction(pc, target, itemObjid);
                        } else if (itemId == 40006 || itemId == 140006) {
                            pc.sendPackets(new S_UseAttackSkill(pc, 0, 6598, spellsc_x, spellsc_y, 17));
                            Broadcaster.broadcastPacket(pc, new S_UseAttackSkill(pc, 0, 6598, spellsc_x, spellsc_y, 17));
                        } else {
                            pc.sendPackets(new S_UseAttackSkill(pc, 0, 10, spellsc_x, spellsc_y, 17));
                            Broadcaster.broadcastPacket(pc, new S_UseAttackSkill(pc, 0, 10, spellsc_x, spellsc_y, 17));
                        }
                        if (itemId == 40006 || itemId == 140006 || itemId == 40007) {
                            l1iteminstance.setChargeCount(l1iteminstance.getChargeCount() - 1);
                            if (l1iteminstance.getChargeCount() == 0) {
                                pc.getInventory().removeItem(l1iteminstance);
                            } else {
                                pc.getInventory().updateItem(l1iteminstance, L1PcInventory.COL_CHARGE_COUNT);
                            }
                        } else {
                            pc.getInventory().removeItem(l1iteminstance, 1);
                        }
                    }
                    break;

                    case 40008:
                    case 140008:
                    case 40410:// 紅葉バー
                        if (pc.getMapId() == 63 || pc.getMapId() == 552 || pc.getMapId() == 555 || pc.getMapId() == 557
                                || pc.getMapId() == 558 || pc.getMapId() == 779) { // HC4f・船の墓地水中では使用不可
                            pc.sendPackets(new S_ServerMessage(563));
                        } else {
                            pc.sendPackets(new S_AttackStatus(pc, 0, ActionCodes.ACTION_Wand));
                            pc.broadcastPacket(new S_AttackStatus(pc, 0, ActionCodes.ACTION_Wand));
                            int chargeCount = l1iteminstance.getChargeCount();
                            if (chargeCount <= 0 && itemId != 40410 || pc.getTempCharGfx() == 6034
                                    || pc.getTempCharGfx() == 6035) {
                                pc.sendPackets(new S_ServerMessage(79));
                                return;
                            }
                            L1Object target = L1World.getInstance().findObject(spellsc_objid);
                            if (spellsc_objid == pc.getId() || target != null) {
                                L1Character cha = spellsc_objid == pc.getId() ? pc : (L1Character) target;
                                polyAction(pc, cha, itemId, s);
                                cancelAbsoluteBarrier(pc);
                                if (itemId == 40008 || itemId == 140008) {

                                    l1iteminstance.setChargeCount(l1iteminstance.getChargeCount() - 1);
                                    if (l1iteminstance.getChargeCount() == 0) {
                                        pc.getInventory().removeItem(l1iteminstance);
                                    } else {
                                        pc.getInventory().updateItem(l1iteminstance, L1PcInventory.COL_CHARGE_COUNT);
                                    }
                                } else {
                                    pc.getInventory().removeItem(l1iteminstance, 1);
                                }
                            } else {
                                pc.sendPackets(new S_ServerMessage(79));
                            }
                        }
                        break;
                    case 40289:
                    case 40290:
                    case 40291:
                    case 40292:
                    case 40293:
                    case 40294:
                    case 40295:
                    case 40296:
                    case 40297:
                        // 傲慢の塔テレポートアミュレット11〜91
                        useToiTeleportAmulet(pc, itemId, l1iteminstance);
                        break;
                    case 40280:
                    case 40281:
                    case 40282:
                    case 40283:
                    case 40284:
                    case 40285:
                    case 40286:
                    case 40287:
                    case 40288:
                        // 封印された傲慢の塔テレポートアミュレット11〜91階
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        L1ItemInstance item3 = pc.getInventory().storeItem(itemId + 9, 1);
                        if (item3 != null) {
                            pc.sendPackets(new S_ServerMessage(403, item3.getLogName()));
                        }
                        break;
                    case 830032:
                    case 830033:
                    case 830034:
                    case 830035:
                    case 830036:
                    case 830037:
                    case 830038:
                    case 830039:
                    case 830040:
                    case 830041:
                        // 封印された傲慢の塔テレポートアミュレット1〜10階
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        L1ItemInstance item1 = pc.getInventory().storeItem(itemId - 20, 1);
                        if (item1 != null) {
                            pc.sendPackets(new S_ServerMessage(403, item1.getLogName()));
                        }
                        break;

                    case 40056:
                    case 40057:
                    case 40059:
                    case 40060:
                    case 40061:
                    case 40062:
                    case 40063:
                    case 40064:
                    case 40065:
                    case 40069:
                    case 40072:
                    case 40073:
                    case 41297:
                    case 41266:
                    case 41267:
                    case 41274:
                    case 41275:
                    case 41276:
                    case 41252:
                    case 49040:
                    case 49041:
                    case 49042:
                    case 49043:
                    case 49044:
                    case 49045:
                    case 49046:
                    case 49047:
                    case 140061:
                    case 140062:
                    case 140065:
                    case 140069:
                    case 140072:
                    case 410056:
                    case 210039:
                    case 30085:
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        // XXX 食品ごとの満腹度が差がない

                        if (itemId == 40057) { // フローティングアイ肉
                            pc.setSkillEffect(STATUS_FLOATING_EYE, 0);
                            if (pc.hasSkillEffect(CURSE_BLIND) || pc.hasSkillEffect(DARKNESS)
                                    || pc.hasSkillEffect(LINDBIOR_SPIRIT_EFFECT)) {
                                pc.sendPackets(new S_CurseBlind(2));
                            }
                        }
                        if (pc.get_food() < 225) { //
                            pc.set_food(pc.get_food() + 10);
                            if (itemId == 210039 || itemId == 30085) { // ハブ
                                pc.set_food(pc.get_food() + 90);
                            }
                            int foodvolume = (l1iteminstance.getItem().getFoodVolume() / 10);
                            pc.add_food(foodvolume <= 0 ? 5 : foodvolume);
                            pc.sendPackets(new S_PacketBox(S_PacketBox.FOOD, pc.get_food()));
                            pc.sendPackets(new S_ServerMessage(76, l1iteminstance.getItem().getNameId()));
                        }
                        break;

                    case 40070:// 進化の実
                        pc.sendPackets(new S_ServerMessage(76, l1iteminstance.getLogName()));
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        break;
                    case 408990:
                        pc.sendPackets(new S_SystemMessage("名前変更変更キャラクター名で入力してください。"));
                        break;
                    case 41146:// ドローモンドの招待状
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei001"));
                        break;
                    case 41209:// 包皮レアの依頼書
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei002"));
                        break;
                    case 41210:// 研磨剤
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei003"));
                        break;
                    case 41211:// ハブ
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei004"));
                        break;
                    case 41212:// 特製キャンディ
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei005"));
                        break;
                    case 41213:// ティミーのバスケット
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei006"));
                        break;
                    case 41214:// 運の証拠
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei012"));
                        break;
                    case 41215:// 地証拠
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei010"));
                        break;
                    case 41216:// 力の証拠
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei011"));
                        break;
                    case 41222:// マシュール
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei008"));
                        break;
                    case 41223:// 武器の破片
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei007"));
                        break;
                    case 41224:// バッジ
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei009"));
                        break;
                    case 41225:// ケスキンの発注書
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei013"));
                        break;
                    case 41226:// 掘りの約
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei014"));
                        break;
                    case 41227:// アレックスの紹介状
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei033"));
                        break;
                    case 41228:// 律法博士のお守り
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei034"));
                        break;
                    case 41229:// スケルトンの頭
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei025"));
                        break;
                    case 41230:// 過去へのメール
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei020"));
                        break;
                    case 41231:// 味ティーの手紙
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei021"));
                        break;
                    case 41233:// ケイこれの手紙
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei019"));
                        break;
                    case 41234: // 骨が入ってきた袋
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei023"));
                        break;
                    case 41235:// 材料表
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei024"));
                        break;
                    case 41236:// 本アーチャーの骨
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei026"));
                        break;
                    case 41237:// スケルトンスパイクの骨
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei027"));
                        break;
                    case 41239:// ブーツへのメール
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei018"));
                        break;
                    case 41240:// クライペダへのメール
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei022"));
                        break;
                    case 41060:// ノーナメの推薦書
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "nonames"));
                        break;
                    case 41061:// 調査団の証書：エルフ地域ドゥダーマラカメ
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "kames"));
                        break;
                    case 41062:// 調査団の証書：人間の地域ネールがバクモ
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "bakumos"));
                        break;
                    case 41063:// 調査団の証書：精霊地域二多摩ラブカー
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "bukas"));
                        break;
                    case 41064:// 調査団の証書：オーク地域ネールが後羽毛
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "huwoomos"));
                        break;
                    case 41065:// 調査団の証書：調査団長アートバーノア
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "noas"));
                        break;
                    case 41356:// パルムの資源リスト
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "rparum3"));
                        break;
                    case 40701:// 小さな宝の地図
                        if (pc.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 1) {
                            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "firsttmap"));
                        } else if (pc.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 2) {
                            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "secondtmapa"));
                        } else if (pc.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 3) {
                            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "secondtmapb"));
                        } else if (pc.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 4) {
                            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "secondtmapc"));
                        } else if (pc.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 5) {
                            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "thirdtmapd"));
                        } else if (pc.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 6) {
                            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "thirdtmape"));
                        } else if (pc.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 7) {
                            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "thirdtmapf"));
                        } else if (pc.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 8) {
                            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "thirdtmapg"));
                        } else if (pc.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 9) {
                            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "thirdtmaph"));
                        } else if (pc.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 10) {
                            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "thirdtmapi"));
                        }
                        break;
                    case 40663:// 息子の手紙
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "sonsletter"));
                        break;
                    case 40630:// ディエゴの古い日記
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "diegodiary"));
                        break;
                    case 41340:// 傭兵団長ティオン
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "tion"));
                        break;
                    case 41317:// ラルスンの推薦状
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "rarson"));
                        break;
                    case 41318:// クエンのメモ
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "kuen"));
                        break;
                    case 41329:// 剥製の製作依頼書
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "anirequest"));
                        break;
                    case 41346:// ロビンフッドのメモ1
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "robinscroll"));
                        break;
                    case 41347:// ロビンフッドのメモ2
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "robinscroll2"));
                        break;
                    case 41348:// ロビンフッドの紹介状
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "robinhood"));
                        break;
                    case 41007:
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "erisscroll"));
                        break;
                    case 41009:
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "erisscroll2"));
                        break;
                    case 41019:
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "lashistory1"));
                        break;
                    case 41020:
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "lashistory2"));
                        break;
                    case 41021:
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "lashistory3"));
                        break;
                    case 41022:
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "lashistory4"));
                        break;
                    case 41023:
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "lashistory5"));
                        break;
                    case 41024:
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "lashistory6"));
                        break;
                    case 41025:
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "lashistory7"));
                        break;
                    case 41026:
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "lashistory8"));
                        break;
                    case 210087:
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "first_p"));
                        break;
                    case 210093:// シランの最初の文字
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "silrein1lt"));
                        break;
                    case 410106:// ハーディンの日記11月10日
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "j_ep0s00"));
                        break;
                    case 410101:// ハーディンの日記：6月2日
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "j_ep0s01"));
                        break;
                    case 410103:// ハーディンの日記：8月9日
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "j_ep0s02"));
                        break;
                    case 410105:// ハーディンの日記：10月12日
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "j_ep0s03"));
                        break;
                    case 410098:// ハーディンの日記：2月24日
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "j_ep0s04"));
                        break;
                    case 410099:// ハーディンの日記：2月25日
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "j_ep0s05"));
                        break;
                    case 410100:// ハーディンの日記：5月5日
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "j_ep0s06"));
                        break;
                    case 410097:// ハーディンの日記：1月1日
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "j_ep0s07"));
                        break;
                    case 410102:// ハーディンの日記：6月9日
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "j_ep0s08"));
                        break;
                    case 410104:// ハーディンの日記：8月19日
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "j_ep0s09"));
                        break;
                    case 410107:// 暗いハーディンの日記帳
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "j_ep0s10"));
                        break;

                    case 40615:// 影の神殿2階の鍵
                        if ((pc.getX() >= 32701 && pc.getX() <= 32705) && (pc.getY() >= 32894 && pc.getY() <= 32898)
                                && pc.getMapId() == 522) { // 影の神殿
                            // 1F
                            new L1Teleport().teleport(pc, ((L1EtcItem) l1iteminstance.getItem()).get_locx(),
                                    ((L1EtcItem) l1iteminstance.getItem()).get_locy(),
                                    ((L1EtcItem) l1iteminstance.getItem()).get_mapid(), 5, true);
                        } else {
                            pc.sendPackets(new S_ServerMessage(79));
                        }
                        break;
                    case 40616:
                    case 40782:
                    case 40783:// 影の神殿3階の鍵
                        if ((pc.getX() >= 32698 && pc.getX() <= 32702) && (pc.getY() >= 32894 && pc.getY() <= 32898)
                                && pc.getMapId() == 523) {
                            // 影の神殿2階
                            new L1Teleport().teleport(pc, ((L1EtcItem) l1iteminstance.getItem()).get_locx(),
                                    ((L1EtcItem) l1iteminstance.getItem()).get_locy(),
                                    ((L1EtcItem) l1iteminstance.getItem()).get_mapid(), 5, true);
                        } else {
                            pc.sendPackets(new S_ServerMessage(79));
                        }
                        break;
                    case 40692:// 完成した宝の地図
                        if (pc.getInventory().checkItem(40621)) {
                            // \f1 何も起こらなかった。
                            pc.sendPackets(new S_ServerMessage(79));
                        } else if ((pc.getX() >= 32856 && pc.getX() <= 32858) && (pc.getY() >= 32857 && pc.getY() <= 32858)
                                && pc.getMapId() == 443) { // 海賊島の
                            // 地下
                            // 刑務所
                            // 3階
                            new L1Teleport().teleport(pc, ((L1EtcItem) l1iteminstance.getItem()).get_locx(),
                                    ((L1EtcItem) l1iteminstance.getItem()).get_locy(),
                                    ((L1EtcItem) l1iteminstance.getItem()).get_mapid(), 5, true);
                        } else {
                            pc.sendPackets(new S_ServerMessage(79));
                        }
                        break;
                    case 41208:// しぼんれる魂
                        if ((pc.getX() >= 32844 && pc.getX() <= 32845) && (pc.getY() >= 32693 && pc.getY() <= 32694)
                                && pc.getMapId() == 550) {
                            // 船の墓地：地上階
                            new L1Teleport().teleport(pc, ((L1EtcItem) l1iteminstance.getItem()).get_locx(),
                                    ((L1EtcItem) l1iteminstance.getItem()).get_locy(),
                                    ((L1EtcItem) l1iteminstance.getItem()).get_mapid(), 5, true);
                        } else {
                            pc.sendPackets(new S_ServerMessage(79));
                        }
                        break;
                    case 40700: // シルバーフルート
                        pc.sendPackets(new S_Sound(10));
                        pc.broadcastPacket(new S_Sound(10));
                        if ((pc.getX() >= 32619 && pc.getX() <= 32623) && (pc.getY() >= 33120 && pc.getY() <= 33124)
                                && pc.getMapId() == 440) {
                            // 海賊島前半魔方陣の座標
                            boolean found = false;
                            L1MonsterInstance mob = null;
                            for (L1Object obj : L1World.getInstance().getObject()) {
                                if (obj instanceof L1MonsterInstance) {
                                    mob = (L1MonsterInstance) obj;
                                    if (mob != null) {
                                        if (mob.getNpcTemplate().get_npcId() == 45875) {
                                            found = true;
                                            break;
                                        }
                                    }
                                }
                            }
                            if (found) {
                            } else {
                                L1SpawnUtil.spawn(pc, 45875, 0, 0);
                            }
                        }
                        break;
                    case 41121:// カヘルの契約書
                        if (pc.getQuest().get_step(L1Quest.QUEST_SHADOWS) == L1Quest.QUEST_END
                                || pc.getInventory().checkItem(41122, 1)) {
                            pc.sendPackets(new S_ServerMessage(79));
                        } else {
                            createNewItem(pc, 41122, 1);
                        }
                        break;
                    case 41130:// 血痕の契約書
                        if (pc.getQuest().get_step(L1Quest.QUEST_DESIRE) == L1Quest.QUEST_END
                                || pc.getInventory().checkItem(41131, 1)) {
                            pc.sendPackets(new S_ServerMessage(79));
                        } else {
                            createNewItem(pc, 41131, 1);
                        }
                        break;
                    case 42501:// ストームワーク
                        new L1Teleport().teleport(pc, spellsc_x, spellsc_y, pc.getMapId(), pc.getHeading(), false);
                        break;

                    case 41293:
                    case 41294:
                    case 41305:
                    case 41306:
                    case 600229:
                    case 9991:// 釣り竿
                        // startFishing(pc, itemId, fishX, fishY,
                        // l1iteminstance, itemObjid);
                        if (Config.ALT_FISHEVENT == true) {
                            Fishitem.clickItem(pc, itemId, fishX, fishY, l1iteminstance, itemObjid);
                        } else {
                            Fishitem.clickItem1(pc, itemId, fishX, fishY, l1iteminstance, itemObjid);
                        }
                        break;
                    case 7024: {
                        long curtime = System.currentTimeMillis() / 1000;
                        if (pc.getQuizTime() + 5 > curtime) {
                            long time = (pc.getQuizTime() + 5) - curtime;
                            pc.sendPackets(new S_ChatPacket(pc, time + "秒後に使ってください。"));
                            return;
                        }
                        int i = 1;
                        if (pc.watchCrest) {
                            i = 3;
                            pc.watchCrest = false;
                        } else
                            pc.watchCrest = true;
                        pc.sendPackets(new S_SystemMessage("すべての血盟のマークを表示したり、終了しました。"));
                        pc.setQuizTime(curtime);
                    }
                    break;

                    case 5564: { // かかしロボット
                        int type = 1;
                        int count = 1;
                        try {
                            Connection con = null;
                            PreparedStatement pstm = null;
                            ResultSet rs = null;
                            try {
                                con = L1DatabaseFactory.getInstance().getConnection();
                                if (type == 1) {
                                    pstm = con.prepareStatement(
                                            "SELECT * FROM characters WHERE account_name = '人工知能' and level <= 99 order by rand()");
                                }
                                rs = pstm.executeQuery();
                                while (rs.next()) {
                                    L1PcInstance player = L1World.getInstance().getPlayer(rs.getString("char_name"));
                                    if (player != null) {
                                        continue;
                                    }
                                    if (count > 0) {
                                        L1PcInstance robot = L1PcInstance.load(rs.getString("char_name"));
                                        L1Map map = pc.getMap();
                                        int x = 0;
                                        int y = 0;

                                        if (type == 1) {
                                            while (true) {
                                                x = robots[CommonUtil.random(5)];
                                                y = robots[CommonUtil.random(5)];
                                                robot.setX(pc.getX() + x);
                                                robot.setY(pc.getY() + y);
                                                robot.setMap(pc.getMapId());
                                                if (map.isPassable(robot.getX(), robot.getY())) {
                                                    break;
                                                }
                                            }
                                        }
                                        robot.setHeading(CommonUtil.random(0, 7));
                                        robot.setOnlineStatus(1);
                                        robot.setNetConnection(null);
                                        robot.beginGameTimeCarrier();
                                        robot.sendVisualEffectAtLogin();
                                        robot.setDead(false);
                                        robot.setActionStatus(0);
                                        robot.noPlayerCK = true;
                                        for (L1SummonInstance summon : L1World.getInstance().getAllSummons()) {
                                            if (summon.getMaster().getId() == robot.getId()) {
                                                summon.setMaster(robot);
                                                robot.addPet(summon);
                                                for (L1PcInstance visiblePc : L1World.getInstance()
                                                        .getVisiblePlayer(summon)) {
                                                    visiblePc.sendPackets(new S_SummonPack(summon, visiblePc));
                                                }
                                            }
                                        }
                                        WarTimeController.getInstance().checkCastleWar(robot);
                                        robot.getAC().setAc(-(robot.getLevel() + 10));
                                        L1World.getInstance().storeObject(robot);
                                        L1World.getInstance().addVisibleObject(robot);
                                        if (robot.getResistance().getMr() <= 145) {
                                            int mr = 145 - robot.getResistance().getMr();
                                            robot.getResistance().addMr(mr);
                                        }
                                        RobotItem(robot);
                                        for (L1ItemInstance item : robot.getInventory().getItems()) {
                                            robot.getInventory().removeItem(item);
                                        }
                                        if (robot.isKnight()) {
                                            boolean isWeapon = false;
                                            for (L1ItemInstance item : robot.getInventory().getItems()) {
                                                if (item.getItemId() == 300) { // ロボットの片手剣
                                                    isWeapon = true;
                                                    if (!item.isEquipped()) {
                                                        robot.getInventory().setEquipped(item, true);
                                                    }
                                                }
                                            }
                                            if (!isWeapon) {
                                                L1ItemInstance item = ItemTable.getInstance().createItem(300);
                                                item.setEnchantLevel(7);
                                                robot.getInventory().storeItem(item);
                                                robot.getInventory().setEquipped(item, true);
                                            }
                                        } else if (robot.isElf()) {
                                            boolean isBow = false;
                                            for (L1ItemInstance item : robot.getInventory().getItems()) {
                                                if (item.getItemId() == 305) { // ロボットの弓
                                                    isBow = true;
                                                    if (!item.isEquipped()) {
                                                        robot.getInventory().setEquipped(item, true);
                                                    }
                                                }
                                            }
                                            if (!isBow) {
                                                L1ItemInstance item = ItemTable.getInstance().createItem(305);
                                                item.setEnchantLevel(7);
                                                robot.getInventory().storeItem(item);
                                                robot.getInventory().setEquipped(item, true);
                                            }
                                        } else if (robot.isWizard()) {
                                            boolean isWeapon = false;
                                            for (L1ItemInstance item : robot.getInventory().getItems()) {
                                                if (item.getItemId() == 303) { // ロボットの杖
                                                    isWeapon = true;
                                                    if (!item.isEquipped()) {
                                                        robot.getInventory().setEquipped(item, true);
                                                    }
                                                }
                                            }
                                            if (!isWeapon) {
                                                L1ItemInstance item = ItemTable.getInstance().createItem(303);
                                                item.setEnchantLevel(7);
                                                robot.getInventory().storeItem(item);
                                                robot.getInventory().setEquipped(item, true);
                                            }
                                        } else if (robot.isDragonknight()) {
                                            boolean isWeapon = false;
                                            for (L1ItemInstance item : robot.getInventory().getItems()) {
                                                if (item.getItemId() == 304) { // ロボットの斧
                                                    isWeapon = true;
                                                    if (!item.isEquipped()) {
                                                        robot.getInventory().setEquipped(item, true);
                                                    }
                                                }
                                            }
                                            if (!isWeapon) {
                                                L1ItemInstance item = ItemTable.getInstance().createItem(304);
                                                item.setEnchantLevel(7);
                                                robot.getInventory().storeItem(item);
                                                robot.getInventory().setEquipped(item, true);
                                            }
                                        } else if (robot.isBlackwizard()) {
                                            boolean isWeapon = false;
                                            for (L1ItemInstance item : robot.getInventory().getItems()) {
                                                if (item.getItemId() == 306) { // ロボットのキーリンク
                                                    isWeapon = true;
                                                    if (!item.isEquipped()) {
                                                        robot.getInventory().setEquipped(item, true);
                                                    }
                                                }
                                            }
                                            if (!isWeapon) {
                                                L1ItemInstance item = ItemTable.getInstance().createItem(306);
                                                item.setEnchantLevel(7);
                                                robot.getInventory().storeItem(item);
                                                robot.getInventory().setEquipped(item, true);
                                            }
                                        } else if (robot.isDarkelf()) {
                                            boolean isWeapon = false;
                                            for (L1ItemInstance item : robot.getInventory().getItems()) {
                                                if (item.getItemId() == 302) { // ロボットの二刀流
                                                    isWeapon = true;
                                                    if (!item.isEquipped()) {
                                                        robot.getInventory().setEquipped(item, true);
                                                    }
                                                }
                                            }
                                            if (!isWeapon) {
                                                L1ItemInstance item = ItemTable.getInstance().createItem(302);
                                                item.setEnchantLevel(7);
                                                robot.getInventory().storeItem(item);
                                                robot.getInventory().setEquipped(item, true);
                                            }
                                        } else if (robot.isWarrior()) {
                                            boolean isWeapon = false;
                                            for (L1ItemInstance item : robot.getInventory().getItems()) {
                                                if (item.getItemId() == 304) { // ロボットの斧
                                                    isWeapon = true;
                                                    if (!item.isEquipped()) {
                                                        robot.getInventory().setEquipped(item, true);
                                                    }
                                                }
                                            }
                                            if (!isWeapon) {
                                                L1ItemInstance item = ItemTable.getInstance().createItem(304);
                                                item.setEnchantLevel(7);
                                                robot.getInventory().storeItem(item);
                                                robot.getInventory().setEquipped(item, true);
                                            }
                                        }
                                        if (type <= 2) {
                                            robot.getRobotAi().setType(type);
                                            RobotAIThread.append(robot, type);
                                            if (type == 1) {
                                                robot.getRobotAi().setAiStatus(robot.getRobotAi().AI_STATUS_WALK);
                                            }
                                        }
                                        count--;
                                    }
                                }
                            } catch (SQLException e) {
                                _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
                            } finally {
                                SQLUtil.close(rs);
                                SQLUtil.close(pstm);
                                SQLUtil.close(con);
                            }
                        } catch (Exception e) {
                            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
                        }

                    }
                    break;
                    case 5565: { // 虚像ロボット
                        int type = 3;
                        int count = 1;
                        try {
                            Connection con = null;
                            PreparedStatement pstm = null;
                            ResultSet rs = null;
                            try {
                                con = L1DatabaseFactory.getInstance().getConnection();
                                if (type == 3) {
                                    pstm = con.prepareStatement(
                                            "SELECT * FROM characters WHERE account_name = '人工知能' and level >= 10  order by rand()");
                                }
                                rs = pstm.executeQuery();
                                while (rs.next()) {
                                    L1PcInstance player = L1World.getInstance().getPlayer(rs.getString("char_name"));
                                    if (player != null) {
                                        continue;
                                    }
                                    if (count > 0) {
                                        L1PcInstance robot = L1PcInstance.load(rs.getString("char_name"));
                                        L1Map map = pc.getMap();
                                        int x = 0;
                                        int y = 0;

                                        if (type == 3) {
                                            while (true) {
                                                x = robots[CommonUtil.random(5)];
                                                y = robots[CommonUtil.random(5)];
                                                robot.setX(pc.getX() + x);
                                                robot.setY(pc.getY() + y);
                                                robot.setMap(pc.getMapId());
                                                if (map.isPassable(robot.getX(), robot.getY())) {
                                                    break;
                                                }
                                            }
                                        }
                                        robot.setHeading(CommonUtil.random(0, 7));
                                        robot.setOnlineStatus(1);
                                        robot.setNetConnection(null);
                                        robot.beginGameTimeCarrier();
                                        robot.sendVisualEffectAtLogin();
                                        robot.setDead(false);
                                        robot.setActionStatus(0);
                                        robot.noPlayerCK = true;
                                        for (L1SummonInstance summon : L1World.getInstance().getAllSummons()) {
                                            if (summon.getMaster().getId() == robot.getId()) {
                                                summon.setMaster(robot);
                                                robot.addPet(summon);
                                                for (L1PcInstance visiblePc : L1World.getInstance()
                                                        .getVisiblePlayer(summon)) {
                                                    visiblePc.sendPackets(new S_SummonPack(summon, visiblePc));
                                                }
                                            }
                                        }
                                        WarTimeController.getInstance().checkCastleWar(robot);
                                        robot.getAC().setAc(-(robot.getLevel() + 10));
                                        L1World.getInstance().storeObject(robot);
                                        L1World.getInstance().addVisibleObject(robot);
                                        if (robot.getResistance().getMr() <= 145) {
                                            int mr = 145 - robot.getResistance().getMr();
                                            robot.getResistance().addMr(mr);
                                        }
                                        RobotItem(robot);
                                        for (L1ItemInstance item : robot.getInventory().getItems()) {
                                            robot.getInventory().removeItem(item);
                                        }

                                        if (robot.getLevel() >= 51) {
                                            if (robot.isKnight()) {
                                                robot.getAbility().addAddedStr(robot.getLevel() - 50);
                                            } else if (robot.isCrown()) {
                                                robot.getAbility().addAddedStr(robot.getLevel() - 50);
                                            } else if (robot.isElf()) {
                                                robot.getAbility().addAddedDex(robot.getLevel() - 50);
                                            } else if (robot.isWizard()) {
                                                robot.getAbility().addAddedInt(robot.getLevel() - 50);
                                            } else if (robot.isDarkelf()) {
                                                robot.getAbility().addAddedStr(robot.getLevel() - 50);
                                            } else if (robot.isBlackwizard()) {
                                                robot.getAbility().addAddedInt(robot.getLevel() - 50);
                                            } else if (robot.isDragonknight()) {
                                                robot.getAbility().addAddedStr(robot.getLevel() - 50);
                                            } else if (robot.isWarrior()) {
                                                robot.getAbility().addAddedStr(robot.getLevel() - 50);
                                            }
                                        } else {
                                            if (robot.isKnight()) {
                                                boolean isWeapon = false;
                                                for (L1ItemInstance item : robot.getInventory().getItems()) {
                                                    if (item.getItemId() == 301) {
                                                        isWeapon = true;
                                                        if (!item.isEquipped()) {
                                                            robot.getInventory().setEquipped(item, true);
                                                        }
                                                    }
                                                }
                                                if (!isWeapon) {
                                                    L1ItemInstance item = ItemTable.getInstance().createItem(301);
                                                    item.setEnchantLevel(7);
                                                    robot.getInventory().storeItem(item);
                                                    robot.getInventory().setEquipped(item, true);
                                                }
                                            } else if (robot.isElf()) {
                                                boolean isBow = false;
                                                for (L1ItemInstance item : robot.getInventory().getItems()) {
                                                    if (item.getItemId() == 305) {
                                                        isBow = true;
                                                        if (!item.isEquipped()) {
                                                            robot.getInventory().setEquipped(item, true);
                                                        }
                                                    }
                                                }
                                                if (!isBow) {
                                                    L1ItemInstance item = ItemTable.getInstance().createItem(305);
                                                    item.setEnchantLevel(7);
                                                    robot.getInventory().storeItem(item);
                                                    robot.getInventory().setEquipped(item, true);
                                                }
                                            } else if (robot.isWizard()) {
                                                boolean isWeapon = false;
                                                for (L1ItemInstance item : robot.getInventory().getItems()) {
                                                    if (item.getItemId() == 303) {
                                                        isWeapon = true;
                                                        if (!item.isEquipped()) {
                                                            robot.getInventory().setEquipped(item, true);
                                                        }
                                                    }
                                                }
                                                if (!isWeapon) {
                                                    L1ItemInstance item = ItemTable.getInstance().createItem(303);
                                                    item.setEnchantLevel(7);
                                                    robot.getInventory().storeItem(item);
                                                    robot.getInventory().setEquipped(item, true);
                                                }
                                            } else if (robot.isDragonknight()) {
                                                boolean isWeapon = false;
                                                for (L1ItemInstance item : robot.getInventory().getItems()) {
                                                    if (item.getItemId() == 304) {
                                                        isWeapon = true;
                                                        if (!item.isEquipped()) {
                                                            robot.getInventory().setEquipped(item, true);
                                                        }
                                                    }
                                                }
                                                if (!isWeapon) {
                                                    L1ItemInstance item = ItemTable.getInstance().createItem(304);
                                                    item.setEnchantLevel(7);
                                                    robot.getInventory().storeItem(item);
                                                    robot.getInventory().setEquipped(item, true);
                                                }
                                            } else if (robot.isBlackwizard()) {
                                                boolean isWeapon = false;
                                                for (L1ItemInstance item : robot.getInventory().getItems()) {
                                                    if (item.getItemId() == 306) {
                                                        isWeapon = true;
                                                        if (!item.isEquipped()) {
                                                            robot.getInventory().setEquipped(item, true);
                                                        }
                                                    }
                                                }
                                                if (!isWeapon) {
                                                    L1ItemInstance item = ItemTable.getInstance().createItem(306);
                                                    item.setEnchantLevel(7);
                                                    robot.getInventory().storeItem(item);
                                                    robot.getInventory().setEquipped(item, true);
                                                }
                                            } else if (robot.isWarrior()) {
                                                boolean isWeapon = false;
                                                for (L1ItemInstance item : robot.getInventory().getItems()) {
                                                    if (item.getItemId() == 304) { // ロボットの斧
                                                        isWeapon = true;
                                                        if (!item.isEquipped()) {
                                                            robot.getInventory().setEquipped(item, true);
                                                        }
                                                    }
                                                }
                                                if (!isWeapon) {
                                                    L1ItemInstance item = ItemTable.getInstance().createItem(304);
                                                    item.setEnchantLevel(7);
                                                    robot.getInventory().storeItem(item);
                                                    robot.getInventory().setEquipped(item, true);
                                                }
                                            } else if (robot.isDarkelf()) {
                                                boolean isWeapon = false;
                                                for (L1ItemInstance item : robot.getInventory().getItems()) {
                                                    if (item.getItemId() == 302) {
                                                        isWeapon = true;
                                                        if (!item.isEquipped()) {
                                                            robot.getInventory().setEquipped(item, true);
                                                        }
                                                    }
                                                }
                                                if (!isWeapon) {
                                                    L1ItemInstance item = ItemTable.getInstance().createItem(302);
                                                    item.setEnchantLevel(7);
                                                    robot.getInventory().storeItem(item);
                                                    robot.getInventory().setEquipped(item, true);
                                                }
                                            }
                                        }
                                        // if (type <= 2) {
                                        // robot.getRobotAi().setType(type);
                                        // RobotAIThread.append(robot, type);
                                        // if (type == 1) {
                                        // robot.getRobotAi().setAiStatus(robot.getRobotAi().AI_STATUS_WALK);
                                        // }
                                        // }
                                        if (type == 3) {
                                            if (CommonUtil.random(100) < 75) {
                                                int rnd1 = CommonUtil.random(20, 60);
                                                robot.setTeleportTime(rnd1);
                                                int rnd2 = CommonUtil.random(5, 60);
                                                if (rnd1 == rnd2) {
                                                    rnd2++;
                                                }
                                                robot.setSkillTime(rnd2);
                                            }
                                        }
                                        count--;
                                    }
                                }
                            } catch (SQLException e) {
                                _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
                            } finally {
                                SQLUtil.close(rs);
                                SQLUtil.close(pstm);
                                SQLUtil.close(con);
                            }
                        } catch (Exception e) {
                            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
                        }

                    }
                    break;

                    case 200000: // 希望のロウソク
                        if (pc.getLevel() != pc.getHighLevel()) {
                            pc.sendPackets(new S_SystemMessage("レベルがダウンしたキャラクターです。レベルアップした後ご利用下さい。"));
                            return;
                        }
                        if (pc.getLevel() > 54) {
                            if (pc.getInventory().checkItem(200000)) {
                                pc.getInventory().consumeItem(200000, 1);
                                new L1Teleport().teleport(pc, 32723 + _random.nextInt(10), 32851 + _random.nextInt(10), (short) 5166, 5, true);
                                StatInitialize(pc);
                            } else {
                                pc.sendPackets(new S_ServerMessage(1290));
                            }
                        } else
                            pc.sendPackets(new S_SystemMessage("ステータス初期化は、レベル55以上のみ可能です。"));
                        break;

                    case 3000049: // 救済の証書
                        pc.sendPackets(new S_SystemMessage("アデン聖堂で経験値を無料で回復することができます。"));
                        break;

                    case 210118:
                        if (pc.getLevel() >= Config.NEW_CLAN_PROTECTION_LEVEL) {
                            pc.sendPackets(
                                    new S_SystemMessage(Config.NEW_CLAN_PROTECTION_LEVEL + "レベル以上は新規血盟に加入することができません。"));
                            return;
                        }
                        if (pc.getClanid() == 0) {
                            L1Clan clan = L1World.getInstance().getClan("新規保護血盟");
                            L1PcInstance clanMember[] = clan.getOnlineClanMember();
                            for (int cnt = 0; cnt < clanMember.length; cnt++) {
                                clanMember[cnt].sendPackets(new S_ServerMessage(94, pc.getName()));
                            }
                            pc.setClanid(Config.NEW_CLAN);
                            pc.setClanname("新規保護血盟");
                            pc.setTitle("\\f:新規保護血盟");
                            pc.setClanRank(L1Clan.TRAINING);
                            pc.setClanJoinDate(new Timestamp(System.currentTimeMillis()));
                            pc.save(); // DBに文字情報を記入する
                            clan.addClanMember(pc.getName(), pc.getClanRank(), pc.getLevel(), "", pc.getId(), pc.getType(),
                                    pc.getOnlineStatus(), pc);
                            pc.sendPackets(new S_SystemMessage("\\aA[メティス]:新規保護血盟に加入しました。"));
                            pc.sendPackets(new S_SystemMessage(
                                    "\\aA[メティス]:レベル(" + Config.NEW_CLAN_PROTECTION_LEVEL + "）になると、自動的に退会されます。"));
                            pc.sendPackets(new S_SystemMessage("\\aA[メティス]:新規保護血はPKダメージが50％にのみ適用されます。"));
                            pc.getInventory().removeItem(l1iteminstance, 1);
                            new L1Teleport().teleport(pc, pc.getX(), pc.getY(), pc.getMapId(), pc.getHeading(), false);
                        } else {
                            pc.sendPackets(new S_SystemMessage("あなたは既に血盟に加入しました。"));
                        }
                        break;
                    case 7643:// 祝福の整数
                        if (pc.getInventory().consumeItem(itemId, 1)) {
                            if (pc.getClan() != null)
                                pc.getClan().addBlessCount(10000000);
                            pc.sendPackets(new S_SystemMessage("通知：祝福の機運1000追加されました。"));
                        }
                        break;

                    case 400253: // 1億アデン通帳
                        if (pc.getInventory().checkItem(40308, 100000000)) {
                            pc.getInventory().consumeItem(40308, 100000000);
                            pc.getInventory().storeItem(400254, 1);
                            pc.sendPackets(new S_SystemMessage("\\fY1億アデナ小切手を獲得しました。"));
                        } else {
                            pc.sendPackets(new S_SystemMessage("100,000,000ウォンアデナが不足します。"));
                        }
                        break;
                    case 810010: { // 中央社員黄金箱
                        Random _random = new Random();
                        int rnd = _random.nextInt(1000);
                        if (rnd < 500) {
                            createNewItem(pc, 40308, _random.nextInt(150000) + 50000); // アデナ
                        } else if (rnd < 780) {
                            createNewItem(pc, 40308, _random.nextInt(450000) + 50000); // アデナ
                        } else if (rnd < 880) {
                            createNewItem(pc, 40308, _random.nextInt(900000) + 100000); // アデナ
                        } else if (rnd < 920) {
                            createNewItem(pc, 40308, _random.nextInt(2700000) + 300000); // アデナ
                        } else if (rnd < 950) {
                            createNewItem(pc, 40308, _random.nextInt(4500000) + 500000); // アデナ
                        } else if (rnd < 970) {
                            createNewItem(pc, 40308, _random.nextInt(9000000) + 1000000); // アデナ
                        } else if (rnd < 982) {
                            createNewItem(pc, 41352, 1); // ユプル
                        } else if (rnd < 984) {
                            createNewItem(pc, 30180, 1); // 知恵のベルト
                        } else if (rnd < 986) {
                            createNewItem(pc, 30179, 1); // アジャイルのベルト
                        } else if (rnd < 988) {
                            createNewItem(pc, 30178, 1); // 知識のベルト
                        } else if (rnd < 990) {
                            createNewItem(pc, 30177, 1); // 腕力のベルト
                        } else if (rnd < 100) {
                            createNewItem(pc, 810003, 1); // 職人の武器強化スクロール
                        }
                    }
                    pc.getInventory().removeItem(l1iteminstance, 1);
                    break;

                    case 810006:
                    case 810007: {
                        if (!(pc.getMapId() >= 1936 && pc.getMapId() <= 2035)) {
                            pc.sendPackets(new S_SystemMessage("中央社員のみ使用が可能です。"));
                            return;
                        }
                        if (delay_id != 0) { // 遅延設定あり
                            if (pc.hasItemDelay(delay_id) == true) {
                                return;
                            }
                        }
                        int chargeCount = l1iteminstance.getChargeCount();

                        if (chargeCount <= 0) {
                            pc.sendPackets(new S_ServerMessage(79));
                            return;
                        }

                        if (pc.isInvisble()) {
                            pc.sendPackets(new S_ServerMessage(1003));
                            return;
                        }

                        int gfx = 0;
                        int dmg = 0;
                        int range = 0;
                        if (itemId == 810006) {
                            gfx = 1819;
                            dmg = 150;
                            range = 3;
                        } else {
                            gfx = 3934;
                            dmg = 500;
                            range = 22;
                        }
                        L1MonsterInstance mon = null;
                        for (L1Object object : L1World.getInstance().getVisibleObjects(pc, range)) {
                            if (object == null) {
                                continue;
                            }
                            if (!(object instanceof L1Character)) {
                                continue;
                            }
                            if (object.getId() == pc.getId()) {
                                continue;
                            }

                            if (object instanceof L1MonsterInstance) {
                                mon = (L1MonsterInstance) object;
                                if (mon.getNpcId() != 7200003) {
                                    Broadcaster.broadcastPacket(mon,
                                            new S_DoActionGFX(mon.getId(), ActionCodes.ACTION_Damage));
                                    mon.receiveDamage(pc, (int) dmg);
                                }
                            }
                        }
                        pc.sendPackets(new S_UseAttackSkill(pc, 0, gfx, pc.getX(), pc.getY(), 18));
                        Broadcaster.broadcastPacket(pc, new S_UseAttackSkill(pc, 0, gfx, pc.getX(), pc.getY(), 18));
                        l1iteminstance.setChargeCount(l1iteminstance.getChargeCount() - 1);

                        pc.getInventory().updateItem(l1iteminstance, L1PcInventory.COL_CHARGE_COUNT);
                        if (chargeCount <= 1) {
                            pc.getInventory().removeItem(l1iteminstance, 1);
                        }
                        L1ItemDelay.onItemUse(pc, l1iteminstance);
                    }
                    break;

                    case 410140: { // ポールの快速リール
                        int item = l1iteminstance1.getItem().getItemId();
                        if (item == 41293) {
                            createNewItem(pc, 41294, 1);
                            pc.getInventory().removeItem(l1iteminstance1, 1);
                            pc.getInventory().removeItem(l1iteminstance, 1);
                        }
                        if (item == 41294) {
                            if (l1iteminstance1.getChargeCount() >= 500) {
                                pc.sendPackets(new S_ServerMessage(3457));
                                // さらに快速リールを使用することができません。
                                return;
                            } else if (l1iteminstance1.getChargeCount() > 4900) {
                                l1iteminstance1.setChargeCount(5000);
                                pc.getInventory().updateItem(l1iteminstance1, L1PcInventory.COL_CHARGE_COUNT);
                            } else {
                                l1iteminstance1.setChargeCount(l1iteminstance1.getChargeCount() + 100);
                                pc.getInventory().updateItem(l1iteminstance1, L1PcInventory.COL_CHARGE_COUNT);
                            }
                            pc.getInventory().removeItem(l1iteminstance, 1);
                        }
                    }
                    break;
                    case 410141: { // 銀色リール
                        int item = l1iteminstance1.getItem().getItemId();
                        if (item == 41293) {
                            createNewItem(pc, 41305, 1);
                            pc.getInventory().removeItem(l1iteminstance1, 1);
                            pc.getInventory().removeItem(l1iteminstance, 1);
                        }
                        if (item == 41305) {
                            if (l1iteminstance1.getChargeCount() >= 5000) {
                                pc.sendPackets(new S_ServerMessage(3457));
                                // さらに快速リールを使用することができません。
                                return;
                            } else if (l1iteminstance1.getChargeCount() > 4950) {
                                l1iteminstance1.setChargeCount(5000);
                                pc.getInventory().updateItem(l1iteminstance1, L1PcInventory.COL_CHARGE_COUNT);
                            } else {
                                l1iteminstance1.setChargeCount(l1iteminstance1.getChargeCount() + 50);
                                pc.getInventory().updateItem(l1iteminstance1, L1PcInventory.COL_CHARGE_COUNT);
                            }
                            pc.getInventory().removeItem(l1iteminstance, 1);
                        }
                    }
                    break;
                    case 410142: { // 金色リール
                        int item = l1iteminstance1.getItem().getItemId();
                        if (item == 41293) {
                            createNewItem(pc, 41306, 1);
                            pc.getInventory().removeItem(l1iteminstance1, 1);
                            pc.getInventory().removeItem(l1iteminstance, 1);
                        }
                        if (item == 41306) {
                            if (l1iteminstance1.getChargeCount() >= 5000) {
                                pc.sendPackets(new S_ServerMessage(3457));
                                // さらに快速リールを使用することができません。
                                return;
                            } else if (l1iteminstance1.getChargeCount() > 4950) {
                                l1iteminstance1.setChargeCount(5000);
                                pc.getInventory().updateItem(l1iteminstance1, L1PcInventory.COL_CHARGE_COUNT);
                            } else {
                                l1iteminstance1.setChargeCount(l1iteminstance1.getChargeCount() + 50);
                                pc.getInventory().updateItem(l1iteminstance1, L1PcInventory.COL_CHARGE_COUNT);
                            }
                            pc.getInventory().removeItem(l1iteminstance, 1);
                        }
                    }
                    break;

                    case 600228: { // 成長のリール
                        int item = l1iteminstance1.getItem().getItemId();
                        if (item == 41293) {
                            createNewItem(pc, 600229, 1);
                            pc.getInventory().removeItem(l1iteminstance1, 1);
                            pc.getInventory().removeItem(l1iteminstance, 1);
                        }
                        if (item == 600229) {
                            if (l1iteminstance1.getChargeCount() >= 5000) {
                                pc.sendPackets(new S_ServerMessage(3457)); // さらに快速リールを使用することができません。
                                return;
                            } else if (l1iteminstance1.getChargeCount() > 4900) {
                                l1iteminstance1.setChargeCount(5000);
                                pc.getInventory().updateItem(l1iteminstance1, L1PcInventory.COL_CHARGE_COUNT);
                            } else {
                                l1iteminstance1.setChargeCount(l1iteminstance1.getChargeCount() + 100);
                                pc.getInventory().updateItem(l1iteminstance1, L1PcInventory.COL_CHARGE_COUNT);
                            }
                            pc.getInventory().removeItem(l1iteminstance, 1);
                        }
                    }
                    break;

                    case 700081:
                        crownWideStun(pc);
                        break;
                    case 700080:// オペレータセット
                        if (pc.getInventory().checkItem(700080, 1)) {
                            pc.getInventory().consumeItem(700080, 1);
                            createNewItem(pc, 700078, 1);
                            createNewItem(pc, 700079, 1);
                            createNewItem(pc, 46162, 1);
                            createNewItem(pc, 46160, 1);
                            createNewItem(pc, 42501, 1);
                            createNewItem(pc, 46161, 1);
                            createNewItem(pc, 46162, 1);
                            createNewItem(pc, 410014, 1);
                            createNewItem(pc, 410015, 1);
                            createNewItem(pc, 900010, 1);
                            createNewItem(pc, 50020, 500);
                            createNewItem(pc, 50021, 50);
                            createNewItem(pc, 40126, 1000);
                            createNewItem(pc, 140100, 100);
                        }
                        break;
                    case 3000063:// クロノスのベルト
                        if (pc.getInventory().checkItem(3000063, 1)) {
                            pc.getInventory().consumeItem(3000063, 1);
                            createNewItem(pc, 900007, 1);
                        }
                        break;
                    case 700078:// モンスターまとめ
                        int objid = pc.getId();
                        pc.sendPackets(new S_SkillSound(objid, 4856)); // 3944
                        Broadcaster.broadcastPacket(pc, new S_SkillSound(objid, 4856));
                        for (L1PcInstance tg : L1World.getInstance().getVisiblePlayer(pc)) {
                            if (tg.getCurrentHp() == 0 && tg.isDead()) {
                                tg.sendPackets(new S_SystemMessage("GMが復活をくれました。"));
                                Broadcaster.broadcastPacket(tg, new S_SkillSound(tg.getId(), 3944));
                                tg.sendPackets(new S_SkillSound(tg.getId(), 3944));
                                // 祝福された復活スクロールのような効果
                                tg.setTempID(objid);
                                tg.sendPackets(new S_Message_YN(322, "")); // また、復活したいですか？
                                // （Y /
                                // N）
                            } else {
                                // tg.sendPackets(new
                                // S_SystemMessage("GMはHP、MPを回復してくれました。 "））;
                                Broadcaster.broadcastPacket(tg, new S_SkillSound(tg.getId(), 832));
                                tg.sendPackets(new S_SkillSound(tg.getId(), 832));
                                tg.setCurrentHp(tg.getMaxHp());
                                tg.setCurrentMp(tg.getMaxMp());
                            }
                        }
                        break;
                    case 700079:
                        for (L1Object obj : L1World.getInstance().getVisibleObjects(pc, 10)) {
                            if (obj instanceof L1MonsterInstance) { // モンスターなら
                                L1NpcInstance npc = (L1NpcInstance) obj;
                                npc.receiveDamage(pc, 50000); // ダメージ
                                if (npc.getCurrentHp() <= 0) {
                                } else {
                                }
                            } else if (obj instanceof L1PcInstance) { // pcなら
                                L1PcInstance Player = (L1PcInstance) obj;
                                Player.receiveDamage(Player, 0);
                                if (Player.getCurrentHp() <= 0) {
                                } else {
                                }
                            }
                        }
                        break;
                    case 7010: // 祝福書
                        if (l1iteminstance1 == null || l1iteminstance1.getItem().getType2() == 0) { // 武器と
                            pc.sendPackets(new S_SystemMessage("武器と防具のみ使用することができます。"));
                            return;
                        }
                        if (l1iteminstance1.getItem().getType() == 14) {
                            pc.sendPackets(new S_SystemMessage("現在のアイテムは不可能です。"));
                            return;
                        }
                        if (l1iteminstance1.getItem().getType2() == 2) {
                            if (l1iteminstance1.getItem().getType() >= 8 && l1iteminstance1.getItem().getType() <= 12) {
                                pc.sendPackets(new S_SystemMessage("アクセサリーは不可能です。"));
                                return;
                            }
                        }
                        if (l1iteminstance1.getBless() >= 128 || l1iteminstance1.getBless() == 0) {
                            pc.sendPackets(new S_ServerMessage(79));
                            return;
                        }
                        int ran = _random.nextInt(100) + 1;
                        if (ran < Config.BLESS_SCROLL) {//
                            l1iteminstance1.setBless(0);
                            l1iteminstance1.setSpecialEnchant(1);
                            pc.sendPackets(new S_ItemStatus(l1iteminstance1));
                            pc.getInventory().updateItem(l1iteminstance1, L1PcInventory.COL_BLESS);
                            pc.getInventory().saveItem(l1iteminstance1, L1PcInventory.COL_BLESS);
                            pc.getInventory().updateItem(l1iteminstance1, L1PcInventory.COL_SPECIAL_ENCHANT);
                            pc.getInventory().saveItem(l1iteminstance1, L1PcInventory.COL_SPECIAL_ENCHANT);
                            pc.getInventory().removeItem(l1iteminstance, 1);
                            pc.sendPackets(new S_SkillSound(pc.getId(), 9268));
                            pc.sendPackets(new S_SystemMessage(l1iteminstance1.getLogName() + "祝福の機運が染みます。"));
                            pc.save();
                        } else {
                            pc.sendPackets(new S_SystemMessage("祝福の機運が不浸透んでした。"));
                            pc.getInventory().removeItem(l1iteminstance, 1);
                        }
                        break;
                    case 7011: // 祝福書
                        if (l1iteminstance1 == null || l1iteminstance1.getItem().getType2() == 0) {
                            pc.sendPackets(new S_SystemMessage("武器と防具のみ使用することができます。"));
                            return;
                        }
                        if (l1iteminstance1.getItem().getType() == 14) {
                            pc.sendPackets(new S_SystemMessage("現在のアイテムは不可能です。"));
                            return;
                        }
                        if (l1iteminstance1.getItem().getType2() == 2) {
                            if (l1iteminstance1.getItem().getType() >= 8 && l1iteminstance1.getItem().getType() <= 12) {
                                pc.sendPackets(new S_SystemMessage("アクセサリーは不可能です。"));
                                return;
                            }
                        }
                        if (l1iteminstance1.getBless() >= 128 || l1iteminstance1.getBless() == 0) {
                            pc.sendPackets(new S_ServerMessage(79));
                            return;
                        }
                        l1iteminstance1.setBless(0);
                        l1iteminstance1.setSpecialEnchant(1);
                        pc.sendPackets(new S_ItemStatus(l1iteminstance1));
                        pc.getInventory().updateItem(l1iteminstance1, L1PcInventory.COL_BLESS);
                        pc.getInventory().saveItem(l1iteminstance1, L1PcInventory.COL_BLESS);
                        pc.getInventory().updateItem(l1iteminstance1, L1PcInventory.COL_SPECIAL_ENCHANT);
                        pc.getInventory().saveItem(l1iteminstance1, L1PcInventory.COL_SPECIAL_ENCHANT);
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        pc.sendPackets(new S_SkillSound(pc.getId(), 9268));
                        pc.sendPackets(new S_SystemMessage(l1iteminstance1.getLogName() + "祝福の機運が染みます。"));
                        pc.save();

                        break;
                    case 65648: {// 黒砂ののコイン
                        int[] allBuffSkill = { 4914 };
                        L1SkillUse l1skilluse = new L1SkillUse();
                        if (pc.hasSkillEffect(L1SkillId.God_buff))
                            pc.removeSkillEffect(L1SkillId.God_buff);
                        for (int i = 0; i < allBuffSkill.length; i++) {
                            l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0,
                                    L1SkillUse.TYPE_SPELLSC);
                        }
                        pc.getInventory().removeItem(l1iteminstance, 1);
                    }
                    break;
                    case 400254: // 小切手両替
                        long curtime = System.currentTimeMillis() / 1000;
                        if (pc.getQuizTime() + 3 > curtime) {
                            long sec = (pc.getQuizTime() + 3) - curtime;
                            pc.sendPackets(new S_ChatPacket(pc, sec + "秒後に可能です。"));
                            return;
                        }
                        int cheque = pc.getInventory().countItems(400254);
                        if (cheque >= 1) {
                            pc.getInventory().storeItem(40308, 100000000);
                            pc.setQuizTime(curtime);
                            pc.sendPackets(new S_ChatPacket(pc, "1億アデナで両替しました。"));
                            pc.getInventory().removeItem(l1iteminstance, 1);
                        } else {
                            pc.sendPackets(new S_ChatPacket(pc, "小切手が不足します。"));
                        }
                        break;
                    case 41245: // 溶解剤
                        useResolvent(pc, l1iteminstance1, l1iteminstance);
                        break;
                    case 700076:
                        if ((pc.getX() >= 33311 && pc.getX() <= 33351) && (pc.getY() >= 32432 && pc.getY() <= 32472)
                                && pc.getMapId() == 4) {
                            pc.getInventory().removeItem(l1iteminstance, 1);
                            L1SpawnUtil.spawn(pc, 45529, 0, 60 * 20000);
                            L1World.getInstance().broadcastPacketToAll(new S_ChatPacket(pc, "竜界三叉路に、誰かが「巨大ドレイク」を召喚しました。"));
                            L1World.getInstance().broadcastPacketToAll(
                                    new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "竜界三叉路に、誰かが「巨大ドレイク」を召喚しました。"));
                        } else {
                            pc.sendPackets(new S_SystemMessage("ドラゴンバレー三叉路で使用すると、「巨大ドレイク」が登場。"));
                        }
                        break;
                    case 700077:
                        if ((pc.getX() >= 33311 && pc.getX() <= 33351) && (pc.getY() >= 32432 && pc.getY() <= 32472)
                                && pc.getMapId() == 4) {
                            pc.getInventory().removeItem(l1iteminstance, 1);
                            L1SpawnUtil.spawn(pc, 7000093, 0, 60 * 20000);
                            L1World.getInstance().broadcastPacketToAll(new S_ChatPacket(pc, "今龍界三叉路に、誰かが「ゼロス」を召喚しました。"));
                            L1World.getInstance().broadcastPacketToAll(
                                    new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "今龍界三叉路に、誰かが「ゼロス」を召喚しました。"));
                        } else {
                            pc.sendPackets(new S_SystemMessage("ドラゴンバレー三叉路で使用すると、「ゼロス」が登場します。"));
                        }
                        break;

                    case 490028: // ラウフルポーション
                        if (pc.getLawful() < 0) {
                            pc.setLawful(30000);
                            pc.sendPackets(new S_ServerMessage(674));
                            pc.getInventory().removeItem(l1iteminstance, 1);
                            pc.save();
                            ; // DBに文字情報を記入する
                        } else {
                            pc.sendPackets(new S_SystemMessage("カオティック性向でのみ使用することができます。"));
                        }
                        break;
                    case 490029:// カオティックポーション
                        if (pc.getLawful() > 0) {
                            pc.setLawful(-30000);
                            pc.sendPackets(new S_ServerMessage(674));
                            pc.getInventory().removeItem(l1iteminstance, 1);
                            pc.save();
                            ; // DBに文字情報を記入する
                        } else {
                            pc.sendPackets(new S_SystemMessage("ラウフル性向でのみ使用することができます。"));
                        }
                        break;

                    case 41303: // 大きな銀色のベリーアナ
                        int rand = _random.nextInt(120) + 1;
                        pc.getInventory().storeItem(40308, 500000);
                        pc.sendPackets(new S_ChatPacket(pc, "アデナ（500,000）を獲得しました。"));
                        if (rand >= 1 && rand <= 12) {
                            pc.getInventory().storeItem(20315, 1);
                            pc.sendPackets(new S_ChatPacket(pc, "栄養満点腰ひもを獲得しました。"));
                        } else if (rand >= 13 && rand <= 24) {
                            pc.getInventory().storeItem(20262, 1);
                            pc.sendPackets(new S_ChatPacket(pc, "栄養満点のネックレスを獲得しました。"));
                        } else if (rand >= 25 && rand <= 36) {
                            pc.getInventory().storeItem(20291, 1);
                            pc.sendPackets(new S_ChatPacket(pc, "栄養満点のリングを獲得しました。"));
                        } else if (rand >= 37 && rand <= 48) {
                            pc.getInventory().storeItem(40087, 1);
                            pc.sendPackets(new S_ChatPacket(pc, "武器強化スクロールを獲得しました。"));
                        } else if (rand >= 49 && rand <= 59) {
                            pc.getInventory().storeItem(40074, 1);
                            pc.sendPackets(new S_ChatPacket(pc, "防具強化スクロールを獲得しました。"));
                        } else if (rand >= 60 && rand <= 65) {
                            pc.getInventory().storeItem(41248, 1);
                            pc.sendPackets(new S_ChatPacket(pc, "マジックドール（バグベア）を獲得しました。"));
                        } else if (rand >= 66 && rand <= 71) {
                            pc.getInventory().storeItem(210096, 1);
                            pc.sendPackets(new S_ChatPacket(pc, "マジックドール（エティ）を獲得しました。"));
                        } else if (rand >= 72 && rand <= 74) {
                            pc.getInventory().storeItem(210105, 1);
                            pc.sendPackets(new S_ChatPacket(pc, "マジックドール（コカトリス）を獲得しました。"));
                        } else if (rand >= 75 && rand <= 77) {
                            pc.getInventory().storeItem(20422, 1);
                            pc.sendPackets(new S_ChatPacket(pc, "輝く古代のネックレスを獲得しました。"));
                        } else if (rand >= 78 && rand <= 79) {
                            pc.getInventory().storeItem(22000, 1);
                            pc.sendPackets(new S_ChatPacket(pc, "古代ミョングンガよりを獲得しました。"));
                        } else if (rand >= 80 && rand <= 81) {
                            pc.getInventory().storeItem(22003, 1);
                            pc.sendPackets(new S_ChatPacket(pc, "古代闘士がを獲得しました。"));
                        } else if (rand >= 82 && rand <= 86) {
                            pc.getInventory().storeItem(30127, 1);
                            pc.sendPackets(new S_ChatPacket(pc, "52レベルクエストアイテムボックスを獲得しました。"));
                        } else {
                            pc.sendPackets(new S_ChatPacket(pc, "アイテムを獲得できなかった。"));
                        }
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        break;
                    case 41304: // 大きな金色ベリーアナ
                        int rand1 = _random.nextInt(170) + 1;
                        pc.getInventory().storeItem(40308, 5000000);
                        pc.sendPackets(new S_ChatPacket(pc, "アデナ（5,000,000）を獲得しました。"));
                        if (rand1 >= 1 && rand1 <= 25) {
                            pc.getInventory().storeItem(41249, 1);
                            pc.sendPackets(new S_ChatPacket(pc, "マジックドール（サキュバス）を獲得しました。"));
                        } else if (rand1 >= 26 && rand1 <= 51) {
                            pc.getInventory().storeItem(41250, 1);
                            pc.sendPackets(new S_ChatPacket(pc, "マジックドール（狼）を獲得しました。"));
                        } else if (rand1 >= 52 && rand1 <= 77) {
                            pc.getInventory().storeItem(210070, 1);
                            pc.sendPackets(new S_ChatPacket(pc, "マジックドール（ストーンゴーレム）を獲得しました。"));
                        } else if (rand1 >= 78 && rand1 <= 88) {
                            pc.getInventory().storeItem(40038, 1);
                            pc.sendPackets(new S_ChatPacket(pc, "エリクサー（CHA）を獲得しました。"));
                        } else if (rand1 >= 89 && rand1 <= 99) {
                            pc.getInventory().storeItem(140087, 1);
                            pc.sendPackets(new S_ChatPacket(pc, "祝福された武器強化スクロールを獲得しました。"));
                        } else if (rand1 >= 100 && rand1 <= 110) {
                            pc.getInventory().storeItem(140074, 1);
                            pc.sendPackets(new S_ChatPacket(pc, "祝福された防具強化スクロールを獲得しました。"));
                        } else if (rand1 >= 111 && rand1 <= 112) {
                            pc.getInventory().storeItem(202002, 1);
                            pc.sendPackets(new S_ChatPacket(pc, "赤いナイトの大剣を獲得しました。"));
                        } else if (rand1 >= 113 && rand1 <= 114) {
                            pc.getInventory().storeItem(504, 1);
                            pc.sendPackets(new S_ChatPacket(pc, "黒曜石キーリンクを獲得しました。"));
                        } else if (rand1 >= 115 && rand1 <= 116) {
                            pc.getInventory().storeItem(205, 1);
                            pc.sendPackets(new S_ChatPacket(pc, "月のロングボウを獲得しました。"));
                        } else if (rand1 >= 117 && rand1 <= 118) {
                            pc.getInventory().storeItem(20165, 1);
                            pc.sendPackets(new S_ChatPacket(pc, "デーモンの手袋を獲得しました。"));
                        } else if (rand1 >= 119 && rand1 <= 120) {
                            pc.getInventory().storeItem(20197, 1);
                            pc.sendPackets(new S_ChatPacket(pc, "デーモンのブーツを獲得しました。"));
                        } else if (rand1 >= 121 && rand1 <= 122) {
                            pc.getInventory().storeItem(20160, 1);
                            pc.sendPackets(new S_ChatPacket(pc, "ブラックエルダーローブを獲得しました。"));
                        } else if (rand1 >= 123 && rand1 <= 124) {
                            pc.getInventory().storeItem(20218, 1);
                            pc.sendPackets(new S_ChatPacket(pc, "ブラックエルダーのサンダルを獲得しました。"));
                        } else if (rand1 >= 125 && rand1 <= 126) {
                            pc.getInventory().storeItem(20298, 1);
                            pc.sendPackets(new S_ChatPacket(pc, "ゼニスのリングを獲得しました。"));
                        } else if (rand1 >= 127 && rand1 <= 131) {
                            pc.getInventory().storeItem(30127, 1);
                            pc.sendPackets(new S_ChatPacket(pc, "52レベルクエストアイテムボックスを獲得しました。"));
                        } else {
                            pc.sendPackets(new S_ChatPacket(pc, "アイテムを獲得できなかった。"));
                        }
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        break;

                    case 500035: // 自己血盟加入書
                        if (pc.getInventory().checkItem(500035, 1)) {
                            pc.getInventory().consumeItem(500035, 1);
                            if (pc.isCrown()) { // 君主なら
                                if (pc.get_sex() == 0) { // 王子なら
                                    pc.sendPackets(new S_ServerMessage(87));
                                    // あなたは王子です
                                } else {
                                    pc.sendPackets(new S_ServerMessage(88));
                                    // あなたは姫です
                                }
                                return;
                            }
                            if (pc.getClanid() != 0) { // 血盟がある場合
                                pc.sendPackets(new S_ServerMessage(89));
                                // すでに血盟があります
                                return;
                            }
                            Connection con = null;
                            con = L1DatabaseFactory.getInstance().getConnection();
                            Statement pstm2 = con.createStatement();
                            ResultSet rs2 = pstm2.executeQuery(
                                    "SELECT `account_name`, `char_name`, `ClanID`, `Clanname` FROM `characters` WHERE Type = 0");
                            while (rs2.next()) {
                                if (pc.getNetConnection().getAccountName()
                                        .equalsIgnoreCase(rs2.getString("account_name"))) {
                                    if (rs2.getInt("ClanID") != 0) { // 君主の血盟がある場合
                                        L1Clan clan = L1World.getInstance().getClan(rs2.getString("Clanname"));
                                        // 君主の血盟がある場合
                                        L1PcInstance clanMember[] = clan.getOnlineClanMember();
                                        for (int cnt = 0; cnt < clanMember.length; cnt++) {
                                            // 接続した血盟員にメッセージ振りかけ
                                            clanMember[cnt].sendPackets(new S_ServerMessage(94, pc.getName()));
                                            // \f1%0この血盟の一員として受け入れられました。
                                        }
                                        pc.setClanid(rs2.getInt("ClanID"));
                                        pc.setClanname(rs2.getString("Clanname"));
                                        pc.setClanRank(L1Clan.TRAINING);
                                        pc.save(); // DBに文字情報を記入する
                                        clan.addClanMember(pc.getName(), pc.getClanRank(), pc.getLevel(), "", pc.getId(),
                                                pc.getType(), pc.getOnlineStatus(), pc);
                                        pc.setClanMemberNotes("");
                                        pc.sendPackets(new S_ClanName(pc, clan.getEmblemId(), pc.getClanRank()));
                                        pc.sendPackets(new S_ReturnedStat(pc.getId(), clan.getClanId()));
                                        pc.sendPackets(new S_PacketBox(S_PacketBox.PLEDGE_EMBLEM_STATUS,
                                                pc.getClan().getEmblemStatus())); // TODO
                                        // pc.sendPackets(new S_ClanAttention());
                                        for (L1PcInstance player : clan.getOnlineClanMember()) {
                                            player.sendPackets(new S_ReturnedStat(pc.getId(), pc.getClan().getEmblemId()));
                                            player.broadcastPacket(
                                                    new S_ReturnedStat(player.getId(), pc.getClan().getEmblemId()));
                                        }
                                        pc.sendPackets(new S_ServerMessage(95, rs2.getString("Clanname")));
                                        new L1Teleport().teleport(pc, pc.getX(), pc.getY(), pc.getMapId(), pc.getHeading(),
                                                false);
                                        pc.getInventory().removeItem(l1iteminstance, 1);
                                        break;
                                    }
                                }
                            }
                            rs2.first(); // クエリを最初に戻し
                            rs2.close();// ここから下までリソースを削除する部分
                            pstm2.close();
                            con.close();
                            if (pc.getClanid() == 0) { // 血盟がある場合
                                pc.sendPackets(new S_SystemMessage("アカウント内君主が存在しない血盟が創設されていない。"));
                            }
                        }
                        break;
                    case 400246: // キルデト初期化
                        pc.setKills(0);
                        pc.setDeaths(0);
                        pc.save();
                        pc.sendPackets(new S_SystemMessage("通知：あなたのキルデスが初期化されました。"));
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        break;

                    case 87052:
                        if (pc.getInventory().checkItem(87052, 1)) {
                            pc.getInventory().consumeItem(87052, 1);
                            pc.getInventory().storeItem(87054, 1);
                            pc.sendPackets(new S_SkillSound(pc.getId(), 8473));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 8473));
                            pc.sendPackets(new S_SystemMessage("虎飼育場を獲得しました。"));
                        }
                        break;
                    case 87053:
                        if (pc.getInventory().checkItem(87053, 1)) {
                            pc.getInventory().consumeItem(87053, 1);
                            pc.getInventory().storeItem(87055, 1);
                            pc.sendPackets(new S_SkillSound(pc.getId(), 8473));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 8473));
                            pc.sendPackets(new S_SystemMessage("珍島犬のバスケットを獲得しました。"));
                        }
                        break;
                    case 87054: // 虎飼育
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        L1SpawnUtil.spawn(pc, 45313, 0, 120000); // 78160 ペットid
                        break;

                    case 87055: // 珍島犬バスケット
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        L1SpawnUtil.spawn(pc, 45711, 0, 120000); // 78161ペットid
                        break;

                    case 560025:
                    case 560027:
                    case 560028:
                    case 560029:
                        TelBook.clickItem(pc, itemId, BookTel, l1iteminstance);
                        break;

                    /** 人形関連 **/
                    case 41248:
                    case 41249:
                    case 41250:
                    case 210070:
                    case 210071:
                    case 210072:
                    case 210086:
                    case 210096:
                    case 210105:
                    case 210106:
                    case 210107:
                    case 210108:
                    case 210109:
                    case 500212:
                    case 500213:
                    case 500214:
                    case 500215:
                    case 447012:
                    case 447013:
                    case 447014:
                    case 30022:
                    case 30023:
                    case 30024:
                    case 30025:
                    case 447015:
                    case 447016:
                    case 447017:
                    case 510216:
                    case 510217:
                    case 510218: // 雪だるまの人形を追加
                    case 510219:
                    case 510220:
                    case 510221:
                    case 510222:// 新規人形4種
                    case 410171:// ジンテン
                    case 3000150: // バポ
                    case 3000151: // オルニョ
                    case 3000152: // カーツ
                    case 410172:
                    case 410173:// デスナイト、人魚、キングバグベア
                    case 740:
                    case 741:
                    case 742:
                    case 743:
                    case 744:
                    case 745:
                    case 746:// デスナイト人形た
                    case 750:
                    case 751:
                    case 752:
                    case 3000086:// マジックドール：アイリス
                    case 3000087:// マジックドール：ヴァンパイア
                    case 3000088:// マジックドール：バランカ
                        // 各種魔法人形たち
                        useMagicDoll(pc, itemId, itemObjid);
                        break;
                    case 210095:// つつくウィザード
                        useSupport(pc, itemId, itemObjid);
                        break;

                    case 410016:// ペットの名前を変更書
                        if (l1iteminstance1.getItem().getItemId() == 40314
                                || l1iteminstance1.getItem().getItemId() == 40316) {
                            L1Pet petTemplate = PetTable.getInstance().getTemplate(l1iteminstance1.getId());
                            if (petTemplate == null) {
                                throw new NullPointerException();
                            }
                            L1Npc l1npc = NpcTable.getInstance().getTemplate(petTemplate.get_npcid());
                            petTemplate.set_name(l1npc.get_name());
                            PetTable.getInstance().storePet(petTemplate); // DBに記入して
                            L1ItemInstance item = pc.getInventory().getItem(l1iteminstance1.getId());
                            pc.getInventory().updateItem(item);
                            pc.getInventory().removeItem(l1iteminstance, 1);
                            pc.sendPackets(new S_ServerMessage(1322, l1npc.get_name()));
                        } else {
                            pc.sendPackets(new S_ServerMessage(1164));
                        }
                        break;

                    case 41401:// 家具除去バー
                        useFurnitureRemovalWand(pc, spellsc_objid, l1iteminstance);
                        break;
                    case 410014:// 倉庫召喚バー
                        useNpcSpownWand(pc, 60001, l1iteminstance);
                        break;
                    case 46160:// npc削除バー
                        useFieldObjectRemovalWand(pc, spellsc_objid, l1iteminstance);
                        break;
                    case 46161: // ドロップ確認バー
                        serchdroplist2(pc, spellsc_objid, l1iteminstance);
                        break;
                    case 46162:// npc確認のバー
                        useFieldObjectRemovalWand1(pc, spellsc_objid, l1iteminstance);
                        break;
                    case 410015:// 店召喚バー
                        useNpcSpownWand(pc, 200006, l1iteminstance);
                        break;
                    case 41345:// 酸性の乳液
                        if (pc.getZoneType() == 1) {
                            pc.sendPackets(new S_ServerMessage(79)); // \f1
                            // 何も起こらなかった。
                            return;
                        }
                        L1DamagePoison.doInfection(pc, pc, 3000, 5, false);
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        break;
                    case 41315:// 聖水
                        if (pc.hasSkillEffect(STATUS_HOLY_WATER_OF_EVA)) {
                            pc.sendPackets(new S_ServerMessage(79)); // \f1何も起こらなかった。
                            return;
                        }
                        if (pc.hasSkillEffect(STATUS_HOLY_MITHRIL_POWDER)) {
                            pc.removeSkillEffect(STATUS_HOLY_MITHRIL_POWDER);
                        }
                        pc.setSkillEffect(STATUS_HOLY_WATER, 900 * 1000);
                        pc.sendPackets(new S_SkillSound(pc.getId(), 190));
                        pc.broadcastPacket(new S_SkillSound(pc.getId(), 190));
                        pc.sendPackets(new S_ServerMessage(1141));
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        break;
                    case 41316:// 神聖なミスリルパウダー
                        if (pc.hasSkillEffect(STATUS_HOLY_WATER_OF_EVA)) {
                            pc.sendPackets(new S_ServerMessage(79)); // \f1何も起こらなかった。
                            return;
                        }
                        if (pc.hasSkillEffect(STATUS_HOLY_WATER)) {
                            pc.removeSkillEffect(STATUS_HOLY_WATER);
                        }
                        pc.setSkillEffect(STATUS_HOLY_MITHRIL_POWDER, 900 * 1000);
                        pc.sendPackets(new S_SkillSound(pc.getId(), 190));
                        pc.broadcastPacket(new S_SkillSound(pc.getId(), 190));
                        pc.sendPackets(new S_ServerMessage(1142));
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        break;
                    case 41354:// 神聖なエヴァの水
                        if (pc.hasSkillEffect(STATUS_HOLY_WATER) || pc.hasSkillEffect(STATUS_HOLY_MITHRIL_POWDER)) {
                            pc.sendPackets(new S_ServerMessage(79)); // \f1
                            // 何も起こらなかった。
                            return;
                        }
                        pc.setSkillEffect(STATUS_HOLY_WATER_OF_EVA, 900 * 1000);
                        pc.sendPackets(new S_SkillSound(pc.getId(), 190));
                        pc.broadcastPacket(new S_SkillSound(pc.getId(), 190));
                        pc.sendPackets(new S_ServerMessage(1140));
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        break;
                    case 30055:// オルドン炎の棒
                        if (!(pc.getMapId() >= 2101 && pc.getMapId() <= 2151
                                || pc.getMapId() >= 2151 && pc.getMapId() <= 2201)) {
                            pc.sendPackets(new S_SystemMessage("オルドンでのみ使用可能です。"));
                            return;
                        }
                        if (pc.isInvisble()) {
                            return;
                        }
                        cancelAbsoluteBarrier(pc); // アブ小ガルトバリアの解除
                        L1Object target = L1World.getInstance().findObject(spellsc_objid);
                        if (target != null) {
                            for (L1Object object : L1World.getInstance().getVisiblePoint(target.getLocation(), 4)) {
                                if (object instanceof L1MonsterInstance) {
                                    L1NpcInstance npc = (L1NpcInstance) object;
                                    if (!npc.isDead() && npc.getId() != target.getId()) {
                                        npc.setActionStatus(ActionCodes.ACTION_Damage);
                                        Broadcaster.broadcastPacket(npc, new S_DoActionGFX(npc.getId(), 2));
                                    }
                                    npc.receiveDamage(pc, 250);
                                }
                            }
                            pc.sendPackets(new S_UseAttackSkill(pc, target.getId(), 762, target.getX(), target.getY(), 18));
                            Broadcaster.broadcastPacket(pc,
                                    new S_UseAttackSkill(pc, target.getId(), 762, target.getX(), target.getY(), 18));
                        }
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        break;

                    case 30071:// アデン王国機器支給する
                        pc.getInventory().removeItem(l1iteminstance, 1);
                        int[] Weapon = null;
                        int[] Armor = null;
                        int[] ArmorEnchant = null;
                        int[] Accessory = null;
                        int[] AccessoryEnchant = null;
                        int MagicDoll = 0;
                        int[] SpellBook = null;
                        if (pc.isCrown()) {
                            Weapon = new int[] { 1113, 169 }; // 隠された魔族の剣、ハンター弓
                            // マト、ヒムチ、ホガプ、ママン、パワーグローブ、ガンブ、新房
                            Armor = new int[] { 20011, 21028, 21060, 20056, 20187, 20194, 20234 };
                            ArmorEnchant = new int[] { 8, 8, 8, 8, 8, 8, 9 };
                            // プグィ、光枯れ木、純白魔、純白チェバン、イワン、オベリスク
                            Accessory = new int[] { 22230, 20422, 22228, 22228, 22226, 22226, 20288, 20317 };
                            AccessoryEnchant = new int[] { 5, 0, 5, 5, 5, 5, 0, 0 };
                            MagicDoll = 500214; // スパルトイ
                            SpellBook = new int[] { 40226, 40227, 40228, 40229, 40230, 40231 };
                        } else if (pc.isKnight()) {
                            Weapon = new int[] { 9, 62, 180 }; // オリハルコンダガー、無関係の両手剣、クロスボウ
                            // マト、ヒムチ、ホガプ、ママン、パワーグローブ、ガンブ、ヨバン、高投影
                            Armor = new int[] { 20011, 21028, 21060, 20056, 20187, 20194, 20236, 22003 };
                            ArmorEnchant = new int[] { 8, 8, 8, 8, 8, 8, 9, 0 };
                            // プグィ、光枯れ木、純白魔、純白チェバン、イワン、オベリスク
                            Accessory = new int[] { 22230, 20422, 22228, 22228, 22226, 22226, 20288, 20317 };
                            AccessoryEnchant = new int[] { 5, 0, 5, 5, 5, 5, 0, 0 };
                            MagicDoll = 500214; // スパルトイ
                            SpellBook = new int[] { 40164, 40165 };
                        } else if (pc.isElf()) {
                            Weapon = new int[] { 9, 508 }; // オリハルコンダガー、テーベオシリスの弓
                            // マト、ミンチ、ホガプ、ママン、パワーグローブ、ガンブ、名弓
                            Armor = new int[] { 20011, 21029, 21060, 20056, 20187, 20194, 22000 };
                            ArmorEnchant = new int[] { 8, 8, 8, 8, 8, 8, 0 };
                            // プグィ、光枯れ木、純白魔、純白チェバン、イワン、オベリスク
                            Accessory = new int[] { 22230, 20422, 22228, 22228, 22226, 22226, 20288, 20317 };
                            AccessoryEnchant = new int[] { 5, 0, 5, 5, 5, 5, 0, 0 };
                            MagicDoll = 210105; // コカトリス
                            SpellBook = new int[] { 40232, 40233, 40234, 40235, 40236, 40237, 40238, 40239, 40240, 40241,
                                    40242, 40244, 40170, 40171, 40172, 40173, 40174, 40175, 40176, 40177, 40178, 40179,
                                    40180, 40181, 40182, 40183, 40184, 40185, 40186, 40187, 40188, 40189, 40190, 40191,
                                    40192, 40193 };
                        } else if (pc.isWizard()) {
                            Weapon = new int[] { 509, 169 }; // テーベオシリスの杖、ハンター弓
                            // マト、クマー、マーティ、ゴロプ、ママン、マト、パワーグローブ、ガンブ、神魔、マナクリスタル、ウィザードが
                            Armor = new int[] { 20011, 22192, 21031, 20093, 20056, 20055, 20187, 20194, 20233, 20225,
                                    22255 };
                            ArmorEnchant = new int[] { 8, 8, 8, 0, 8, 7, 8, 8, 8, 5, 0 };
                            // プグィ、ブルモク、純白魔、純白の集中、イワン、オベリスク
                            Accessory = new int[] { 22231, 20257, 22228, 22228, 22225, 22225, 20288, 20317 };
                            AccessoryEnchant = new int[] { 5, 0, 5, 5, 5, 5, 0, 0 };
                            MagicDoll = 210071; // 長老
                            SpellBook = new int[] { 40170, 40171, 40172, 40173, 40174, 40175, 40176, 40177, 40178, 40179,
                                    40180, 40181, 40182, 40183, 40184, 40185, 40186, 40187, 40188, 40189, 40190, 40191,
                                    40192, 40193, 40197, 40224, 40213 };
                        } else if (pc.isDarkelf()) {
                            Weapon = new int[] { 507, 180 }; // テーベアヒル時のデュアルブレード、クロスボウ
                            // マト、ヒムチ、ホガプ、ママン、パワーグローブ、グブ、ヨバン、高投影
                            Armor = new int[] { 20011, 21028, 21060, 20056, 20187, 20195, 20236, 22003 };
                            ArmorEnchant = new int[] { 8, 8, 8, 8, 8, 8, 9, 0 };
                            // プグィ、光枯れ木、純白魔、純白チェバン、イワン、オベリスク
                            Accessory = new int[] { 22230, 20422, 22228, 22228, 22226, 22226, 20288, 20317 };
                            AccessoryEnchant = new int[] { 5, 0, 5, 5, 5, 5, 0, 0 };
                            MagicDoll = 500214; // スパルトイ
                            SpellBook = new int[] { 40265, 40266, 40267, 40269, 40271, 40272, 40273, 40274, 40275, 40278,
                                    40279 };
                        } else if (pc.isDragonknight()) {
                            Weapon = new int[] { 62, 501, 615 }; // 関係の両手剣、破滅者のチェーンソード。ククルカンのガントレット
                            // マト、ヒムチ、ホガプ、ママン、パワーグローブ、ガンブ、ヨバン、高投影
                            Armor = new int[] { 20011, 21028, 21060, 20056, 20187, 20194, 20236, 22003 };
                            ArmorEnchant = new int[] { 8, 8, 8, 8, 8, 8, 9, 0 };
                            // プグィ、光枯れ木、純白魔、純白チェバン、イワン、オベリスク
                            Accessory = new int[] { 22230, 20422, 22228, 22228, 22226, 22226, 20288, 20317 };
                            AccessoryEnchant = new int[] { 5, 0, 5, 5, 5, 5, 0, 0 };
                            MagicDoll = 500214; // スパルトイ
                            SpellBook = new int[] { 210021, 210022, 210023, 210024, 210026, 210027, 210028, 210029, 210030,
                                    210031, 210032, 210033, 210034 };
                        } else if (pc.isBlackwizard()) {
                            Weapon = new int[] { 509, 504 }; // テーベオシリスの杖、黒曜石キーリンクは、テーベオシリスの弓
                            // マト、ヒムチ、ホガプ、ママン、パワーグローブ、ガンブ、神魔、高投影
                            Armor = new int[] { 20011, 21028, 21060, 20056, 20187, 20194, 20233, 22003 };
                            ArmorEnchant = new int[] { 8, 8, 8, 8, 8, 8, 8, 0 };
                            // プグィ、光枯れ木、純白魔、純白チェバン、イワン、オベリスク
                            Accessory = new int[] { 22230, 20422, 22228, 22228, 22226, 22226, 20288, 20317 };
                            AccessoryEnchant = new int[] { 5, 0, 5, 5, 5, 5, 0, 0 };
                            MagicDoll = 500214; // スパルトイ
                            SpellBook = new int[] { 210000, 210001, 210002, 210003, 210005, 210006, 210007, 210008, 210009,
                                    210010, 210011, 210012, 210013, 210015, 210016, 210017, 210018, 210019 };
                        } else if (pc.isWarrior()) {
                            Weapon = new int[] { 9, 62, 180 }; // オリハルコンダガー、無関係の両手剣、クロスボウ
                            // マト、ヒムチ、ホガプ、ママン、パワーグローブ、ガンブ、ヨバン、高投影
                            Armor = new int[] { 20011, 21028, 21060, 20056, 20187, 20194, 20236, 22003 };
                            ArmorEnchant = new int[] { 8, 8, 8, 8, 8, 8, 9, 0 };
                            // プグィ、光枯れ木、純白魔、純白チェバン、イワン、オベリスク
                            Accessory = new int[] { 22230, 20422, 22228, 22228, 22226, 22226, 20288, 20317 };
                            AccessoryEnchant = new int[] { 5, 0, 5, 5, 5, 5, 0, 0 };
                            MagicDoll = 500214; // スパルトイ
                            SpellBook = new int[] { 40164, 40165 };
                        }
                        for (int i = 0; i < Weapon.length; i++) { // 武器
                            createNewItemTrade(pc, Weapon[i], 1, 9, 129, 3, true);
                        }
                        for (int i = 0; i < Armor.length; i++) { // 防具
                            createNewItemTrade(pc, Armor[i], 1, ArmorEnchant[i], 129, 0, true);
                        }
                        for (int i = 0; i < Accessory.length; i++) { // アクセサリー
                            createNewItemTrade(pc, Accessory[i], 1, AccessoryEnchant[i], 129, 0, true);
                        }
                        for (int i = 0; i < SpellBook.length; i++) { // スペルブック
                            createNewItemTrade(pc, SpellBook[i], 1, 0, 1, 0, false);
                        }
                        createNewItemTrade(pc, MagicDoll, 1, 0, 129, 0, false);
                        createNewItemTrade(pc, 30072, 200, 0, 129, 0, false); // ダンテスの遺物の袋
                        createNewItemTrade(pc, 40308, 2000000, 0, 1, 0, false); // アデナ
                        break;

                    case 700024:// ！DOCTYPE html>
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\ \\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\ \\\\\\\\\\\\\\\\
                        // \\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\ \\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\ \\\\\\\\\\\\\\\\
                        // \\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\ \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\ \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\ \\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\ \\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\ \\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\ \\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\ \\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\ \\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\ \\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\ \\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\ \\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\ \\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\ \\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\ \\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\ \\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\ \\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\ \\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\ \\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\ \\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\ \\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\ \\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\ \\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\ \\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\ \\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\ \\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\ \\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\ \\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\ \\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\ \\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\ \\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\ \\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\ \\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\ \\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\ \\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\ \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\ \\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\ \\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\ \\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\ \\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\ \\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\ \\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\ \\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\ \\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\ \\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\ \\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\ \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\ \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\ \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\ \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\ \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\ \\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\ \\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\ \\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\ \\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\ \\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\ \\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\ \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\ \\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\ \\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\ \\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\ \\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\ \\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\ \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\ \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\ \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\ \\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\ \\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\ \\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\ \\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\ \\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\ \\\\\\\\ \\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\ \\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\ \\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\ \\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\ \\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\ \\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\ \\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\ \\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\ \\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\ \\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\ \\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\ \\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\ \\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\ \\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\ \\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\ \\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\ \\\\
                        L1BookMark.Bookmarkitem(pc, l1iteminstance, itemId, false);
                        break;
                    /** 1014 永遠の命の光実装完了 **/
                    case 3000065: {// 永遠の命の光
                        int targetItem = l1iteminstance1.getItemId();
                        int[] item = new int[] { 3000065 };// 必要な材料
                        int[] A = new int[] { 202011 };// ガイアの激怒
                        int[] B = new int[] { 202012 };// ヒペリオンの絶望
                        int[] C = new int[] { 202013 };// クロノスの恐怖
                        int[] D = new int[] { 202014 };// タイタンの怒り
                        int[] E = new int[] { 202015 };// まだ未定
                        int[] temp = null;
                        switch (targetItem) {
                            case 3000072: // 魂を失った弓[材料]
                                temp = A;
                                break;
                            case 3000071: // 魂を失ったキーリンク【材料】
                                temp = B;
                                break;
                            case 3000070: // 魂を失ったチェーンソード[材料]
                                temp = C;
                                break;
                            case 3000073: // 魂を失った斧[材料]
                                temp = D;
                                break;
                            case 3000074: // 魂を失った両手剣未定[材料]
                                temp = E;
                                break;
                            default:
                                pc.sendPackets(new S_ServerMessage(79));
                                break;
                        }
                        if (temp != null) {
                            boolean chance = false;
                            for (int i = 0; i < item.length; i++) {
                                if (l1iteminstance.getItemId() == item[i]) {
                                    if (_random.nextInt(99) + 1 <= Config.LIGHT_OF_ETERNAL_LIFE) {
                                        chance = true;
                                        // 支払い処理。
                                        createNewItem2(pc, temp[i], 1, l1iteminstance1.getEnchantLevel());
                                        break;
                                    }
                                }
                            }
                            // 確率失敗ハットをするときのメッセージ処理。
                            if (chance == false) {
                                // pc.sendPackets(new S_ServerMessage(79));
                                pc.sendPackets(new S_SystemMessage("\\aA通知: 製作に \\aG[失敗]\\aA しました.."));
                            }
                            // 材料除去処理。
                            pc.getInventory().DeleteEnchant(l1iteminstance1.getItemId(), l1iteminstance1.getEnchantLevel());
                            pc.getInventory().removeItem(l1iteminstance, 1);
                        }
                    }
                    break;

                    case 5558:
                        normalBuff(pc, l1iteminstance);
                        break;
                    case 30107:
                    case 30108:
                    case 30109:
                    case 30110:
                    case 30111:
                    case 30112:
                    case 30113:
                    case 30114:
                    case 30115: {
                        // 純白のティー引張
                        int targetItem = l1iteminstance1.getItemId();
                        int[] item = new int[] { 30107, 30108, 30109, 30110, 30111, 30112, 30113, 30114, 30115 };
                        int[] t = new int[] { 22349, 22350, 22351, 22352, 22353, 22354, 22355, 22356, 22357 };
                        int[] elf_t = new int[] { 22340, 22341, 22342, 22343, 22344, 22345, 22346, 22347, 22348 };
                        if (targetItem == 20084) { // エルヴンTシャツ
                            for (int i = 0; i < item.length; i++) {
                                if (l1iteminstance.getItemId() == item[i]) {
                                    // createNewItem2(pc, elf_t[i], 1,
                                    // l1iteminstance1.getEnchantLevel());
                                    white(pc, elf_t[i], 1, l1iteminstance1.getEnchantLevel());
                                    pc.getInventory().DeleteEnchant(l1iteminstance1.getItemId(),
                                            l1iteminstance1.getEnchantLevel());
                                    pc.getInventory().removeItem(l1iteminstance, 1);
                                    break;
                                }
                            }
                        } else if (targetItem == 20085) { // Tシャツ
                            for (int i = 0; i < item.length; i++) {
                                if (l1iteminstance.getItemId() == item[i]) {
                                    // createNewItem2(pc, t[i], 1,
                                    // l1iteminstance1.getEnchantLevel());
                                    white(pc, t[i], 1, l1iteminstance1.getEnchantLevel());
                                    pc.getInventory().DeleteEnchant(l1iteminstance1.getItemId(),
                                            l1iteminstance1.getEnchantLevel());
                                    pc.getInventory().removeItem(l1iteminstance, 1);
                                    break;
                                }
                            }
                        } else {
                            pc.sendPackets(new S_ServerMessage(79));
                        }
                    }
                    break;
                    case 30116:// 天然石鹸：純白のティー
                        int targetItem = l1iteminstance1.getItemId();
                        if (targetItem >= 22340 && targetItem <= 22348) { // 純白のエルヴンティー
                            // createNewItem2(pc, 20084, 1,
                            // l1iteminstance1.getEnchantLevel());
                            white(pc, 20084, 1, l1iteminstance1.getEnchantLevel());
                            pc.getInventory().DeleteEnchant(l1iteminstance1.getItemId(), l1iteminstance1.getEnchantLevel());
                            pc.getInventory().removeItem(l1iteminstance, 1);
                        } else if (targetItem >= 22349 && targetItem <= 22357) { // 純白のティー
                            // createNewItem2(pc, 20085, 1,
                            // l1iteminstance1.getEnchantLevel());
                            white(pc, 20085, 1, l1iteminstance1.getEnchantLevel());
                            pc.getInventory().DeleteEnchant(l1iteminstance1.getItemId(), l1iteminstance1.getEnchantLevel());
                            pc.getInventory().removeItem(l1iteminstance, 1);
                        } else {
                            pc.sendPackets(new S_ServerMessage(79));
                        }
                        break;
                    case 30121:// 初心者ガイドブック
                        for (L1Object obj : L1World.getInstance().getObject()) {
                            if (obj instanceof L1NpcInstance) {
                                L1NpcInstance npc = (L1NpcInstance) obj;
                                if (npc.getNpcId() == 4200015) {
                                    pc.sendPackets(new S_Board(npc));
                                    break;
                                }
                            }
                        }
                        break;

                    ////////////////////////////////////////////////////////////////////////////////////////////
                    /************************** 武器強化エンチャント関連書 ***********************************************/
                    ///////////////////////////////////////////////////////////////////////////////////////////
                    /** 属性変換書 **/
                    case 560030:
                    case 560031:
                    case 560032:
                    case 560033:
                        /** 属性変換書 **/
                    case 40130:
                    case 140130:
                    case 40077:
                    case L1ItemId.SCROLL_OF_ENCHANT_WEAPON:
                    case L1ItemId.SCROLL_OF_ENCHANT_QUEST_WEAPON:
                    case L1ItemId.SCROLL_OF_ENCHANT_FANTASY_WEAPON: // 幻の武器強化スクロール
                    case L1ItemId.B_SCROLL_OF_ENCHANT_WEAPON:
                    case L1ItemId.C_SCROLL_OF_ENCHANT_WEAPON:
                    case L1ItemId.IVORYTOWER_WEAPON_SCROLL:
                    case 210085:
                    case 210064:
                    case 210065:
                    case 210066:
                    case 210067:
                    case 810003:
                    case 127000:
                    case 68076:
                    case 30146:
                    case 68078:
                    case 30068: {
                        if (l1iteminstance1 == null || l1iteminstance1.getItem().getType2() != 1) {
                            pc.sendPackets(new S_ServerMessage(79)); // \f1
                            // 何も起こらなかった。
                            return;
                        }

                        int safe_enchant = l1iteminstance1.getItem().get_safeenchant();
                        if (safe_enchant < 0) { // 強化不可
                            pc.sendPackets(new S_ServerMessage(79)); // \f1何も起こらなかった。
                            return;
                        }
                        int weaponId = l1iteminstance1.getItem().getItemId();
                        if (weaponId >= 246 && weaponId <= 249) { // 強化不可
                            if (itemId == L1ItemId.SCROLL_OF_ENCHANT_QUEST_WEAPON) {// 試練のスクロール
                            } else {
                                pc.sendPackets(new S_ServerMessage(79)); // \f1何も起こらなかった。
                                return;
                            }
                        }
                        if (itemId == L1ItemId.SCROLL_OF_ENCHANT_QUEST_WEAPON) {
                            // 試練のスクロール
                            if (weaponId >= 246 && weaponId <= 249) { // 強化不可
                            } else {
                                pc.sendPackets(new S_ServerMessage(79)); // \f1何も起こらなかった。
                                return;
                            }
                        }
                        /** アーノルド武器強化スクロール **/
                        if (weaponId >= 307 && weaponId <= 314) {
                            if (itemId == 30146) {
                            } else {
                                pc.sendPackets(new S_ServerMessage(79)); // \f1
                                // 何も起こらなかった。
                                return;
                            }
                        }
                        if (itemId == 30146) {
                            if (weaponId >= 307 && weaponId <= 314) {
                            } else {
                                pc.sendPackets(new S_ServerMessage(79)); // \f1何も起こらなかった。
                                return;
                            }
                        }

                        /** 幻の武器強化スクロール **/
                        if (weaponId >= 413000 && weaponId <= 413007) { // 加えて、強化不可
                            if (itemId == L1ItemId.SCROLL_OF_ENCHANT_FANTASY_WEAPON) {// 幻の武器強化スクロール
                            } else {
                                pc.sendPackets(new S_ServerMessage(79)); // \f1
                                // 何も起こらなかった。
                                return;
                            }
                        }
                        if (itemId == L1ItemId.SCROLL_OF_ENCHANT_FANTASY_WEAPON) {// 幻の武器強化スクロール
                            if (weaponId >= 413000 && weaponId <= 413007) { // 加えて、強化不可
                            } else {
                                pc.sendPackets(new S_ServerMessage(79)); // \f1
                                // 何も起こらなかった。
                                return;
                            }
                        }
                        // 勇者の武器強化スクロール
                        if (weaponId >= 1126 && weaponId <= 1133) { // 加えて、強化不可
                            if (itemId == 30068) {
                            } else {
                                pc.sendPackets(new S_ServerMessage(79)); // \f1何も起こらなかった。
                                return;
                            }
                        }
                        if (itemId == 30068) {
                            if (weaponId >= 1126 && weaponId <= 1133) { // 加えて、強化不可
                            } else {
                                pc.sendPackets(new S_ServerMessage(79)); // \f1何も起こらなかった。
                                return;
                            }
                        }
                        /** 蒼天武器強化スクロール **/
                        if (itemId == 210085) {
                            if ((weaponId >= 231 && weaponId <= 240) || (weaponId >= 510 && weaponId <= 539)) {
                            } else {
                                pc.sendPackets(new S_ServerMessage(79));
                                return;
                            }
                        }
                        if ((weaponId >= 231 && weaponId <= 240) || (weaponId >= 510 && weaponId <= 539)) {
                            if (itemId == 210085) {// 蒼天武器強化スクロール
                            } else {
                                pc.sendPackets(new S_ServerMessage(79)); // \f1何も起こらなかった。
                                return;
                            }
                        }
                        /** 蒼天武器強化スクロール **/

                        // 象牙の塔の武器強化スクロール
                        if (itemId == L1ItemId.IVORYTOWER_WEAPON_SCROLL) {
                            if (weaponId == 7 || weaponId == 35 || weaponId == 48 || weaponId == 73 || weaponId == 105
                                    || weaponId == 120 || weaponId == 147 || weaponId == 156 || weaponId == 174
                                    || weaponId == 175 || weaponId == 224 || weaponId == 203012) {
                                if (l1iteminstance1.getEnchantLevel() >= 6) {
                                    pc.sendPackets(new S_ServerMessage(79));
                                    return;
                                }
                            } else {
                                pc.sendPackets(new S_ServerMessage(79));
                                return;
                            }
                        }
                        if (weaponId == 7 || weaponId == 35 || weaponId == 48 || weaponId == 73 || weaponId == 105
                                || weaponId == 120 || weaponId == 147 || weaponId == 156 || weaponId == 174
                                || weaponId == 175 || weaponId == 224 || weaponId == 203012) {
                            if (itemId != L1ItemId.IVORYTOWER_WEAPON_SCROLL) {
                                pc.sendPackets(new S_ServerMessage(79)); // \f1何も起こらなかった。
                                return;
                            }
                        }
                        if (l1iteminstance1.getBless() >= 128 && (!(itemId >= 210064 && itemId <= 210067
                                || itemId >= 560030 && itemId <= 560033 || itemId == 810003))) { // 封印システム
                            pc.sendPackets(new S_ServerMessage(79)); // \f1何も起こらなかった。
                            return;
                        }

                        /** 属性エンチャント **/
                        if (itemId == 210064 && l1iteminstance1.getAttrEnchantLevel() != 0
                                && l1iteminstance1.getAttrEnchantLevel() != 11
                                && l1iteminstance1.getAttrEnchantLevel() != 12
                                && l1iteminstance1.getAttrEnchantLevel() != 13
                                && l1iteminstance1.getAttrEnchantLevel() != 14
                                && l1iteminstance1.getAttrEnchantLevel() != 15) {
                            pc.sendPackets(new S_ServerMessage(1294));
                            return;
                        }
                        if (itemId == 210065 && l1iteminstance1.getAttrEnchantLevel() != 0
                                && l1iteminstance1.getAttrEnchantLevel() != 16
                                && l1iteminstance1.getAttrEnchantLevel() != 17
                                && l1iteminstance1.getAttrEnchantLevel() != 18
                                && l1iteminstance1.getAttrEnchantLevel() != 19
                                && l1iteminstance1.getAttrEnchantLevel() != 20) {
                            pc.sendPackets(new S_ServerMessage(1294));
                            return;
                        }
                        if (itemId == 210066 && l1iteminstance1.getAttrEnchantLevel() != 0
                                && l1iteminstance1.getAttrEnchantLevel() != 6 && l1iteminstance1.getAttrEnchantLevel() != 7
                                && l1iteminstance1.getAttrEnchantLevel() != 8 && l1iteminstance1.getAttrEnchantLevel() != 9
                                && l1iteminstance1.getAttrEnchantLevel() != 10) {
                            pc.sendPackets(new S_ServerMessage(1294));
                            return;
                        }
                        if (itemId == 210067 && l1iteminstance1.getAttrEnchantLevel() != 0
                                && l1iteminstance1.getAttrEnchantLevel() != 1 && l1iteminstance1.getAttrEnchantLevel() != 2
                                && l1iteminstance1.getAttrEnchantLevel() != 3 && l1iteminstance1.getAttrEnchantLevel() != 4
                                && l1iteminstance1.getAttrEnchantLevel() != 5) {
                            pc.sendPackets(new S_ServerMessage(1294));
                            return;
                        }
                        /** 属性エンチャント **/
                        int enchant_level = l1iteminstance1.getEnchantLevel();

                        if (enchant_level >= Config.WEAPON_ENCHANT
                                && (!(itemId >= 210064 && itemId <= 210067 || itemId >= 560030 && itemId <= 560033))) { // エンチャント制限
                            pc.sendPackets(new S_SystemMessage("武器は+" + Config.WEAPON_ENCHANT + "以上強化することができません。"));
                            return;
                        }

                        if (safe_enchant == 0) {
                            if (enchant_level >= Config.WEAPON_PREMIUM_ENCHANT
                                    && (!(itemId >= 210064 && itemId <= 210067 || itemId >= 560030 && itemId <= 560033))) {
                                if (itemId == L1ItemId.C_SCROLL_OF_ENCHANT_WEAPON) { // c-dai
                                    pc.getInventory().removeItem(l1iteminstance, 1);
                                    SuccessEnchant(pc, l1iteminstance1, client, -1);
                                } else {
                                    pc.sendPackets(new S_SystemMessage(
                                            "高級アイテムは+" + Config.WEAPON_PREMIUM_ENCHANT + "以上強化することができません。"));
                                }
                                return;
                            }
                        }
                        if (itemId == L1ItemId.C_SCROLL_OF_ENCHANT_WEAPON) { // c-dai
                            pc.getInventory().removeItem(l1iteminstance, 1);
                            int rnd = _random.nextInt(100) + 1;
                            if (safe_enchant == 0 && rnd <= 30) {
                                FailureEnchant(pc, l1iteminstance1, client);
                                return;
                            }
                            if (enchant_level < -6) { // -7以上はできない。
                                FailureEnchant(pc, l1iteminstance1, client);
                            } else {
                                SuccessEnchant(pc, l1iteminstance1, client, -1);
                            }

                        } else if (itemId == 210064 || itemId == 210065 || itemId == 210066 || itemId == 210067) {
                            AttrEnchant(pc, l1iteminstance1, itemId);

                            /** 属性変換書 **/
                        } else if (itemId >= 560030 && itemId <= 560033) {
                            AttrChangeEnchant(pc, l1iteminstance1, itemId);

                            /** 属性変換書 **/

                        } else if (itemId == 68076) { // 古代の書：武器
                            if (l1iteminstance1 != null && l1iteminstance1.getItem().getType2() == 1) {
                                if (enchant_level >= 15) { // 使用最大エンチャント数値
                                    pc.sendPackets(new S_SystemMessage("もう強化することができません"));
                                    return;
                                }
                                Random random = new Random();
                                int k3 = random.nextInt(100);
			    /*
			     * if (k3 <= 15) {
			     * //-1になる確率は15％SuccessEnchant（pc、l1iteminstance1、
			     * client、-1）; pc.sendPackets（new S_SystemMessage（
			     * "アイテムのエンチャント数値が-1降りました。 "））;
			     * pc.getInventory（）removeItem（l1iteminstance、1）;}
			     */
                                if (k3 >= 0 && k3 <= 100) { // +1 される確率5％
                                    SuccessEnchant(pc, l1iteminstance1, client, +1);
                                    pc.sendPackets(new S_SystemMessage("\\aA[祝福]: \\aG武器のエンチャントが+1しました。"));
                                    pc.getInventory().removeItem(l1iteminstance, 1);
                                }
			    /*
			     * if (k3 >= 21 && k3 <= 100) {
			     * //確率は分かってpc.getInventory（）removeItem（
			     * l1iteminstance、1）; pc.sendPackets（new
			     * S_SystemMessage("何も起こらなかった。 "））;}
			     */
                            } else {
                                pc.sendPackets(new S_ServerMessage(79));
                            }

                        } else if (itemId == 68078) { // 古代の書：武器
                            if (l1iteminstance1 != null && l1iteminstance1.getItem().getType2() == 1) {
                                if (enchant_level >= 15) { // 使用最大エンチャント数値
                                    pc.sendPackets(new S_SystemMessage("もう強化することができません"));
                                    return;
                                }
                                Random random = new Random();
                                int k3 = random.nextInt(100);
			    /*
			     * if (k3 <= 15) { // -1
			     * される確率15％SuccessEnchant（pc、l1iteminstance1、client、
			     * -1）; pc.sendPackets（new S_SystemMessage（
			     * 「アイテムのエンチャント数値が-1降りました。 "））;
			     * pc.getInventory（）removeItem（l1iteminstance、1）; }
			     */
                                if (k3 <= Config.ANCIENT_WEAPON) { // +1になる確率5％
                                    SuccessEnchant(pc, l1iteminstance1, client, +1);
                                    pc.sendPackets(new S_SystemMessage("\\aA[祝福]: \\aG武器のエンチャントが+1しました。"));
                                    pc.getInventory().removeItem(l1iteminstance, 1);
                                } else {
                                    pc.getInventory().removeItem(l1iteminstance, 1);
                                    pc.sendPackets(new S_SystemMessage("\\aA[失敗]: \\aG武器のエンチャントに失敗しました。"));
                                }

                            } else {
                                pc.sendPackets(new S_ServerMessage(79));
                            }

                        } else if (itemId == 810003) {
                            if (!(l1iteminstance1.getItem().getMaterial() == 9
                                    || l1iteminstance1.getItem().getMaterial() == 18)) {
                                if (enchant_level == 9) {
                                    int rnd = _random.nextInt(100);
                                    if (rnd <= Config.CRAFTSMAN_WEAPON_SCROLL) {
                                        SuccessEnchant(pc, l1iteminstance1, client, 1);
                                        /** エンチャント全体の通知リニューアル **/
                                        if (enchant_level >= 9) { // 武器+10成功したときに通知
                                            L1World.getInstance().broadcastPacketToAll(new S_SystemMessage(
                                                    "とあるアデン勇士が[" + l1iteminstance1.getLogName() + "]エンチャントに成功しました。"));
                                            L1World.getInstance().broadcastPacketToAll(new S_PacketBox(
                                                    S_PacketBox.GREEN_MESSAGE,
                                                    "とあるアデン勇士が[" + l1iteminstance1.getLogName() + "]エンチャントに成功しました"));
                                        }
                                        /** エンチャント全体の通知リニューアル **/
                                    } else {
                                        pc.sendPackets(new S_ServerMessage(1310));
                                        // エンチャント：強烈な光たが何もありませんでした。
                                    }
                                    pc.getInventory().removeItem(l1iteminstance, 1);
                                } else {
                                    pc.sendPackets(new S_SystemMessage("\\aA警告: \\aG[+9]\\aA 武器のみ使用が可能です。"));
                                    // エンチャント+9武器のみ使用可能
                                }
                            } else {
                                pc.sendPackets(new S_ServerMessage(1294));
                                // エンチャント：この強化スクロールを使用不可
                            }
                        } else if (enchant_level < safe_enchant) {
                            pc.getInventory().removeItem(l1iteminstance, 1);
                            SuccessEnchant(pc, l1iteminstance1, client, RandomELevel(l1iteminstance1, itemId));
                        } else {
                            pc.getInventory().removeItem(l1iteminstance, 1);

                            int rnd = _random.nextInt(100) + 1;
                            int enchant_chance_wepon;
                            int chance = 0;

                            try {
                                chance = WeaponEnchantList.getInstance().getWeaponEnchant(l1iteminstance1.getItemId());
                            } catch (Exception e) {
                                System.out.println("WeaponEnchantList chance Error");
                            }
                            if (weaponId >= 307 && weaponId <= 314) {
                                enchant_chance_wepon = 90 / ((enchant_level - safe_enchant + 1) * 2)
                                        / (enchant_level / 9 != 0 ? 1 * 2 : 1) + Config.ARNOLD_WEAPON_CHANCE;
                            } else if (enchant_level >= 10) {
                                enchant_chance_wepon = 90 / ((enchant_level - safe_enchant + 1) * 2)
                                        / (enchant_level / 9 != 0 ? 1 * 2 : 1) + Config.ENCHANT_CHANCE_WEAPON + chance;
                            } else {
                                if (l1iteminstance1.getItem().get_safeenchant() == 0) {
                                    enchant_chance_wepon = 90 / ((enchant_level - safe_enchant + 1) * 2)
                                            / (enchant_level / 9 != 0 ? 1 * 2 : 1) + Config.ENCHANT_CHANCE_WEAPON + chance;
                                } else {
                                    enchant_chance_wepon = 90 / ((enchant_level - safe_enchant + 1) * 2)
                                            / (enchant_level / 9 != 0 ? 1 * 2 : 1) + Config.ENCHANT_CHANCE_WEAPON;
                                }
                            }

                            if (pc.isGm()) {
                                pc.sendPackets(new S_SystemMessage("\\fY確率 : [ " + enchant_chance_wepon + " ]"));
                                pc.sendPackets(new S_SystemMessage("\\fY追加 : [ " + chance + " ]"));
                                pc.sendPackets(new S_SystemMessage("\\fYチャンス : [ " + rnd + " ]"));
                            }

                            if (rnd < enchant_chance_wepon) {
                                int randomEnchantLevel = 0;
                                /** 執行武器+3以上の時、1ずつ浮遊するよう **/
                                if (enchant_level >= 2 && safe_enchant == 0
                                    // weaponId == 12 || //バッカル
                                    // weaponId == 14 || //混沌の爪
                                    // weaponId == 16 || //複数の刃
                                    // weaponId == 61 || //執行剣
                                    // weaponId == 86 || //赤くこの
                                    // weaponId == 119 || //デジ
                                    // weaponId == 123 || //ベレススタッフ
                                    // weaponId == 124 || //バポ杖
                                    // weaponId == 134 || //樹脂
                                    // weaponId == 151 || //デーモンのX
                                    // weaponId == 160 || //野獣王クロウ
                                    // weaponId == 123
                                    // weaponId == 123
                                        ) {
                                    randomEnchantLevel = 1;
                                    // ** 忘れられた島執行級武器無条件1ずつ浮遊するよう **//*
                                } else if (weaponId >= 202011 && weaponId <= 202015) {
                                    randomEnchantLevel = 1;
                                } else {
                                    randomEnchantLevel = RandomELevel(l1iteminstance1, itemId);
                                }
                                SuccessEnchant(pc, l1iteminstance1, client, randomEnchantLevel);
                            } else {
                                FailureEnchant(pc, l1iteminstance1, client);
                            }
                        }
                    }
                    break;
                    ////////////////////////////////////////////////////////////////////////////////////////////
                    /************************** 防具強化エンチャント関連書 ***********************************************/
                    ///////////////////////////////////////////////////////////////////////////////////////////
                    case 40078:
                    case L1ItemId.SCROLL_OF_ENCHANT_ARMOR:
                    case 40129:
                    case 140129:
                    case L1ItemId.SCROLL_OF_ENCHANT_FANTASY_ARMOR:
                    case L1ItemId.B_SCROLL_OF_ENCHANT_ARMOR:
                    case L1ItemId.C_SCROLL_OF_ENCHANT_ARMOR:
                    case L1ItemId.Inadril_T_ScrollA:
                    case L1ItemId.Inadril_T_ScrollB:
                    case L1ItemId.Inadril_T_ScrollC:
                    case L1ItemId.Pure_white_Scroll:
                    case L1ItemId.Roomtis_Scroll:
                    case L1ItemId.IVORYTOWER_ARMOR_SCROLL:
                    case 3000123:
                    case 3000124:
                    case 3000125:
                    case 210084:
                    case 3000100:// 文章強化石
                    case 210068:
                    case 68077:
                    case 68079:
                    case 30069:
                    case 30147:
                    case 68080:
                    case 68081:
                    case 3000130:
                    case 3000131:
                    case 3000132:
                    case 810012:
                    case 810013:
                    case 3000154: {
                        if (l1iteminstance1 == null || l1iteminstance1.getItem().getType2() != 2) {
                            pc.sendPackets(new S_ServerMessage(79)); // \f1
                            // 何も起こらなかった。
                            return;
                        }
                        int safe_enchant = ((L1Armor) l1iteminstance1.getItem()).get_safeenchant();
                        if (safe_enchant < 0) { // 強化不可
                            pc.sendPackets(new S_ServerMessage(79)); // \f1何も起こらなかった。
                            return;
                        }
                        int armorId = l1iteminstance1.getItem().getItemId();
                        int armortype = l1iteminstance1.getItem().getType();
                        /*** 記章強化スクロールでなければ **/
                        if (itemId != 3000154) {
                            if (armortype == 17) {
                                pc.sendPackets(new S_ServerMessage(79)); // \f1何も起こらなかった。
                                return;
                            }
                        }
                        /** アーノルド防具強化スクロール **/
                        if (armorId == 21095) {
                            if (itemId == 30147) {
                            } else {
                                pc.sendPackets(new S_ServerMessage(79));
                                return;
                            }
                        }
                        if (itemId == 30147) {
                            if (armorId == 21095) {
                            } else {
                                pc.sendPackets(new S_ServerMessage(79));
                                return;
                            }
                        }

                        /** インナドリルTシャツ防具強化スクロール **/
                        if (armorId >= 22215 && armorId <= 22223 || armorId >= 490000 && armorId <= 490008) {
                            if (itemId >= 410066 && itemId <= 410068) {
                            } else {
                                pc.sendPackets(new S_ServerMessage(79));
                                return;
                            }
                        }
                        if (itemId >= 410066 && itemId <= 410068) {
                            if (armorId >= 22215 && armorId <= 22223 || armorId >= 490000 && armorId <= 490008) {
                            } else {
                                pc.sendPackets(new S_ServerMessage(79));
                                return;
                            }
                        }

                        /** 幻の防具強化スクロール **/
                        if (armorId >= 423000 && armorId <= 423008) {
                            if (itemId == L1ItemId.SCROLL_OF_ENCHANT_FANTASY_ARMOR) {
                            } else {
                                pc.sendPackets(new S_ServerMessage(79));
                                return;
                            }
                        }

                        if (itemId == L1ItemId.SCROLL_OF_ENCHANT_FANTASY_ARMOR) {
                            if (armorId >= 423000 && armorId <= 423008) {
                            } else {
                                pc.sendPackets(new S_ServerMessage(79));
                                return;
                            }
                        }
                        if (itemId == 30069) {// 勇者の防具強化スクロール
                            if (armorId >= 22328 && armorId <= 22335) { // 加えて、強化不可
                            } else {
                                pc.sendPackets(new S_ServerMessage(79)); // \f1
                                // 何も起こらなかった。
                                return;
                            }
                        }
                        if (armorId >= 22328 && armorId <= 22335) {
                            if (itemId == 30069) {
                            } else {
                                pc.sendPackets(new S_ServerMessage(79));
                                return;
                            }
                        }
                        /** ユニコーンの防具強化スクロール **/
                        if (itemId >= 3000130 && itemId <= 3000132) {
                            if (armorId >= 900027 && armorId <= 900029) {
                                if (l1iteminstance1.isEquipped()) {
                                    pc.sendPackets(new S_SystemMessage("着用を解除した後、強化することができます。"));
                                    return;
                                }
                            } else {
                                pc.sendPackets(new S_ServerMessage(79));
                                return;
                            }
                        } else {
                            if (armorId >= 900027 && armorId <= 900029) {
                                pc.sendPackets(new S_ServerMessage(79));
                                return;
                            }
                        }

                        /** 文章強化石 **/
                        if (itemId == 3000100 || itemId == 68080 || itemId == 68081) {
                            if (armorId == 900020 || armorId == 900021 || armorId >= 222352 && armorId <= 222354
                                    || armorId >= 232355 && armorId <= 232361) {
                                if (l1iteminstance1.isEquipped()) {
                                    pc.sendPackets(new S_SystemMessage("着用を解除した後、強化することができます。"));
                                    return;
                                }
                            } else {
                                pc.sendPackets(new S_ServerMessage(79));
                                return;
                            }
                        }
                        if (armorId == 900020 || armorId == 900021 || armorId >= 222352 && armorId <= 222354
                                || armorId >= 232355 && armorId <= 232361) {
                            if (itemId == 3000100 || itemId == 68080 || itemId == 68081) {
                            } else {
                                pc.sendPackets(new S_ServerMessage(79));
                                return;
                            }
                        }

                        /** 蒼天の防具強化スクロール **/
                        if (itemId == 210084) {
                            if (armorId >= 22034 && armorId <= 22064) {
                            } else {
                                pc.sendPackets(new S_ServerMessage(79));
                                return;
                            }
                        }
                        if (armorId >= 22034 && armorId <= 22064) {
                            if (itemId == 210084) {
                            } else {
                                pc.sendPackets(new S_ServerMessage(79));
                                return;
                            }
                        }
                        /** 用のTシャツ強化スクロール **/
                        if (itemId >= 3000123 && itemId <= 3000125) {
                            if (armorId >= 900023 && armorId <= 900026) {
                            } else {
                                pc.sendPackets(new S_ServerMessage(79));
                                return;
                            }
                        }
                        if (armorId >= 900023 && armorId <= 900026) {
                            if (itemId >= 3000123 && itemId <= 3000125) {
                            } else {
                                pc.sendPackets(new S_ServerMessage(79));
                                return;
                            }
                        }
                        /** 装身具強化スクロール */
                        if (itemId == 210068 || itemId == L1ItemId.Pure_white_Scroll || itemId == L1ItemId.Roomtis_Scroll
                                || itemId == 810012 || itemId == 810013) {
                            if (armortype >= 8 && armortype <= 12) {
                                // if (itemId == 810012 || itemId == 810013) {
                                if (l1iteminstance1.isEquipped()) {
                                    pc.sendPackets(new S_SystemMessage("着用を解除した後、強化することができます。"));
                                    return;
                                }
                                // }
                            } else {
                                pc.sendPackets(new S_ServerMessage(79));
                                return;
                            }
                        }
                        if (armortype >= 8 && armortype <= 12) {
                            if (itemId == 210068 || itemId == L1ItemId.Pure_white_Scroll
                                    || itemId == L1ItemId.Roomtis_Scroll || itemId == 810012 || itemId == 810013) {
                            } else {
                                pc.sendPackets(new S_ServerMessage(79));
                                return;
                            }
                        }
                        /** ルームティス強化スクロール **/
                        if (itemId == L1ItemId.Roomtis_Scroll) {
                            if (armorId >= 22229 && armorId <= 22231 || armorId >= 222337 && armorId <= 222339
                                    || armorId == 222340 || armorId == 222341) {
                            } else {
                                pc.sendPackets(new S_ServerMessage(79));
                                return;
                            }
                        }
                        if (armorId >= 22229 && armorId <= 22231 || armorId >= 222337 && armorId <= 222339
                                || armorId == 222340 || armorId == 222341) {
                            if (itemId == L1ItemId.Roomtis_Scroll) {
                            } else {
                                pc.sendPackets(new S_ServerMessage(79));
                                return;
                            }
                        }
                        /** スナップファーリング強化スクロール **/
                        if (itemId == L1ItemId.Pure_white_Scroll) {
                            if (armorId >= 22224 && armorId <= 22228 || armorId == 222290 || armorId == 222291
                                    || armorId >= 222330 && armorId <= 222336) {
                            } else {
                                pc.sendPackets(new S_ServerMessage(79));
                                return;
                            }
                        }
                        if (armorId >= 22224 && armorId <= 22228 || armorId == 222290 || armorId == 222291
                                || armorId >= 222330 && armorId <= 222336) {
                            if (itemId == L1ItemId.Pure_white_Scroll) {
                            } else {
                                pc.sendPackets(new S_ServerMessage(79));
                                return;
                            }
                        }

                        // 象牙の塔の防具強化スクロール
                        if (itemId == L1ItemId.IVORYTOWER_ARMOR_SCROLL) {
                            if (armorId == 20028 || armorId == 20082 || armorId == 20126 || armorId == 20173
                                    || armorId == 20206 || armorId == 20232 || armorId == 20283) {
                                if (l1iteminstance1.getEnchantLevel() >= 4) {
                                    pc.sendPackets(new S_ServerMessage(79));
                                    return;
                                }
                            } else if (armorId >= 22300 && armorId <= 22312) {
                                if (l1iteminstance1.getEnchantLevel() >= 6) {
                                    pc.sendPackets(new S_ServerMessage(79));
                                    return;
                                }
                            } else {
                                pc.sendPackets(new S_ServerMessage(79));
                                return;
                            }
                        }

                        if (armorId == 20028 || armorId == 20082 || armorId == 20126 || armorId == 20173 || armorId == 20206
                                || armorId == 20232 || armorId == 20283 || armorId >= 22300 && armorId <= 22312) {
                            if (itemId != L1ItemId.IVORYTOWER_ARMOR_SCROLL) {
                                pc.sendPackets(new S_ServerMessage(79));
                                return;
                            }
                        }

                        if (l1iteminstance1.getBless() >= 128 && (!(itemId >= 810012 && itemId <= 810013))) { // 封印システム
                            pc.sendPackets(new S_ServerMessage(79)); // \f1何も起こらなかった。
                            return;
                        }

                        int enchant_level = l1iteminstance1.getEnchantLevel();

                        // ルームティスのイヤリング+8まで。
                        if (armorId >= 22229 && armorId <= 22231 || armorId >= 222337 && armorId <= 222339
                                || armorId == 222340 || armorId == 222341) {
                            if (enchant_level >= Config.ROOMTIS) {
                                pc.sendPackets(new S_SystemMessage("ルームティスイヤリングは+" + Config.ROOMTIS + "以上はエンチャントできません。"));
                                return;
                            }
                        }
                        // スナップファーリングエンチャン制​​限+8まで。
                        if (armorId >= 22224 && armorId <= 22228 || armorId == 222290 || armorId == 222291
                                || armorId >= 222330 && armorId <= 222336) {
                            if (enchant_level >= Config.SNAPPER) {
                                pc.sendPackets(new S_SystemMessage("スナップファーリングは+" + Config.SNAPPER + "以上はエンチャントできません。"));
                                return;
                            }
                        }

                        if (armortype >= 8 && armortype <= 12) {
                            if (enchant_level >= Config.ACCESSORIES) {
                                pc.sendPackets(new S_SystemMessage("アクセサリーは+" + Config.ACCESSORIES + "以上はエンチャントできません。"));
                                return;
                            }
                        }

                        if (enchant_level >= Config.ARMOR_ENCHANT) { // エンチャント制限
                            pc.sendPackets(new S_SystemMessage("防具は+" + Config.ARMOR_ENCHANT + "以上強化することができません。"));
                            return;
                        }
                        if (!(armortype >= 8 && armortype <= 12) && safe_enchant == 0 && armortype != 17) {
                            if (enchant_level >= Config.ARMOR_PREMIUM_ENCHANT) {
                                if (itemId == L1ItemId.C_SCROLL_OF_ENCHANT_ARMOR) { // c-dai
                                    pc.getInventory().removeItem(l1iteminstance, 1);
                                    SuccessEnchant(pc, l1iteminstance1, client, -1);
                                } else {
                                    pc.sendPackets(new S_SystemMessage(
                                            "高級アイテムは+" + Config.WEAPON_PREMIUM_ENCHANT + "以上強化することができません。"));
                                }
                                return;
                            }
                        }

                        if (itemId == L1ItemId.C_SCROLL_OF_ENCHANT_ARMOR || itemId == L1ItemId.Inadril_T_ScrollC
                                || itemId == 3000125 || itemId == 3000132) { // 呪いの鎧魔法の呪文書類
                            if (l1iteminstance1.isEquipped()) {
                                pc.sendPackets(new S_SystemMessage("着用を解除した後、強化することができます。"));
                                return;
                            }
                            pc.getInventory().removeItem(l1iteminstance, 1);
                            int rnd = _random.nextInt(100) + 1;
                            if (safe_enchant == 0 && rnd <= 30) {
                                FailureEnchant(pc, l1iteminstance1, client);
                                return;

                            } else if (enchant_level < -1) { // 基本的なシステムは、-2からジョジェルバー時の蒸発
                                FailureEnchant(pc, l1iteminstance1, client);
                            } else {
                                SuccessEnchant(pc, l1iteminstance1, client, -1);
                            }

                        } else if (itemId == 68077) { // 古代の書：防具
                            if (l1iteminstance1 != null && l1iteminstance1.getItem().getType2() == 2) {
                                if (enchant_level >= 11) { // 強化不可数値世話...
                                    pc.sendPackets(new S_SystemMessage("もう強化することができません"));
                                    return;
                                }
                                Random random = new Random();
                                int k3 = random.nextInt(100);
			    /*
			     * if (k3 <= 100) { //
			     * -1される確率15％SuccessEnchant（pc、l1iteminstance1、
			     * client、-1）; pc.sendPackets（new S_SystemMessage（
			     * "アイテムのエンチャント数値が-1降りました。 "））;
			     * pc.getInventory（）removeItem（l1iteminstance、1）;}
			     */
                                if (k3 >= 0 && k3 <= 100) { // +1になる確率10％
                                    SuccessEnchant(pc, l1iteminstance1, client, +1);
                                    pc.sendPackets(new S_SystemMessage("\\aA[祝福]: \\aG防具のエンチャントが+1しました。"));
                                    pc.getInventory().removeItem(l1iteminstance, 1);
                                }
			    /*
			     * if (k3> = 26 && k3 <=
			     * 100）{//無反応75％pc.getInventory（）removeItem（
			     * l1iteminstance、1）; pc.sendPackets（new
			     * S_SystemMessage(「何も起こらなかった。 "））; }
			     */
                            } else {
                                pc.sendPackets(new S_ServerMessage(79));
                            }

                        } else if (itemId == 68079) { // 古代の書：防具
                            if (l1iteminstance1 != null && l1iteminstance1.getItem().getType2() == 2) {
                                if (enchant_level >= 11) { // 強化不可数値世話...
                                    pc.sendPackets(new S_SystemMessage("もう強化することができません"));
                                    return;
                                }
                                Random random = new Random();
                                int k3 = random.nextInt(100);
			    /*
			     * if (k3 <= 100）{//
			     * -1される確率15％SuccessEnchant（pc、l1iteminstance1、
			     * client、-1）; pc.sendPackets（new S_SystemMessage（
			     * "アイテムのエンチャント数値が-1降りました。 "））;
			     * pc.getInventory（）removeItem（l1iteminstance、1）;}
			     */
                                if (k3 <= Config.ANCIENT_ARMOR) { // +1になる確率10％
                                    SuccessEnchant(pc, l1iteminstance1, client, +1);
                                    pc.sendPackets(new S_SystemMessage("\\aA[祝福]: \\aG防具のエンチャントが+1しました。"));
                                    pc.getInventory().removeItem(l1iteminstance, 1);
                                } else {
                                    pc.getInventory().removeItem(l1iteminstance, 1);
                                    pc.sendPackets(new S_SystemMessage("\\aA[失敗]: \\aG防具エンチャントに失敗しました。"));
                                }
			    /*
			     * if (k3 >= 26 && k3 <= 100) { // 無反応 75%
			     * pc.getInventory().removeItem(l1iteminstance, 1);
			     * pc.sendPackets(new
			     * S_SystemMessage("何も起こらなかった。 "））;}
			     */
                            } else {
                                pc.sendPackets(new S_ServerMessage(79));
                            }

                        } else if (itemId == 68080) { // 文章強化石（確率）
                            if (l1iteminstance1 != null && l1iteminstance1.getItem().getType2() == 2) {
                                if (armorId != 900020 && armorId != 900021 && (!(armorId >= 222352 && armorId <= 222354))) {
                                    pc.sendPackets(new S_SystemMessage("文章のみ可能です。"));
                                    return;
                                }
                                if (enchant_level >= 11) { // 強化不可数値世話...
                                    pc.sendPackets(new S_SystemMessage("もう強化することができません"));
                                    return;
                                }
                                Random random = new Random();
                                int k3 = random.nextInt(100);
                                if (k3 <= Config.CREST_ENCHANT_CHANCE) { // +1になる確率5％
                                    SuccessEnchant(pc, l1iteminstance1, client, +1);
                                    pc.sendPackets(new S_SystemMessage("\\aA[祝福]: \\aG防具のエンチャントが+1しました。"));
                                    pc.getInventory().removeItem(l1iteminstance, 1);
                                } else {
                                    pc.getInventory().removeItem(l1iteminstance, 1);
                                    pc.sendPackets(new S_SystemMessage("何も起こらなかった。"));
                                }
                            } else {
                                pc.sendPackets(new S_ServerMessage(79));
                            }
                        } else if (itemId == 68081) { // 文章強化石（100％）
                            if (l1iteminstance1 != null && l1iteminstance1.getItem().getType2() == 2) {
                                if (armorId != 900020 && armorId != 900021 && (!(armorId >= 222352 && armorId <= 222354))) {
                                    pc.sendPackets(new S_SystemMessage("文章のみ可能です。"));
                                    return;
                                }
                                if (enchant_level >= 11) { // 強化不可数値世話...
                                    pc.sendPackets(new S_SystemMessage("もう強化することができません"));
                                    return;
                                }
                                Random random = new Random();
                                int k3 = random.nextInt(100);
			    /*
			     * if (k3 <=100）{//
			     * -1される確率15％SuccessEnchant（pc、l1iteminstance1、
			     * client、-1）; pc.sendPackets（new S_SystemMessage（
			     * "アイテムのエンチャント数値が-1降りました。 "））;
			     * pc.getInventory（）removeItem（l1iteminstance、1）;}
			     */
                                if (k3 >= 0 && k3 <= 100) { // +1になる確率10％
                                    SuccessEnchant(pc, l1iteminstance1, client, +1);
                                    pc.sendPackets(new S_SystemMessage("\\aA[祝福]: \\aG防具のエンチャントが+1しました。"));
                                    pc.getInventory().removeItem(l1iteminstance, 1);
                                }
			    /*
			     * if (k3 >= 26 && k3 <= 100) { //
			     * 無反応の75％pc.getInventory（）removeItem（l1iteminstance
			     * 、1）; pc.sendPackets（new S_SystemMessage（
			     * "何も起こらなかった。"））; }
			     */
                            } else {
                                pc.sendPackets(new S_ServerMessage(79));
                            }

                        } else if (itemId == 3000154) { // 記章書
                            if (armortype == 17) {
                                pc.getInventory().removeItem(l1iteminstance, 1);
                                int rnd = _random.nextInt(100) + 1;
                                int badgechance = 30;
                                pc.getInventory().setEquipped(l1iteminstance1, false);
                                if (rnd <= badgechance)
                                    SuccessEnchant(pc, l1iteminstance1, client, 1);
                                else
                                    FailureEnchant(pc, l1iteminstance1, client);
                            }
                        } else if (enchant_level < safe_enchant) {
                            pc.getInventory().removeItem(l1iteminstance, 1);
                            SuccessEnchant(pc, l1iteminstance1, client, RandomELevel(l1iteminstance1, itemId));
                        } else {
                            pc.getInventory().removeItem(l1iteminstance, 1);
                            int rnd = _random.nextInt(100) + 1;
                            int enchant_chance_armor;
                            int enchant_level_tmp;
                            if (safe_enchant == 0) { // 骨、ブラックミスリルの補正
                                enchant_level_tmp = 2;
                            } else {
                                enchant_level_tmp = 1;
                            }
                            if (armortype >= 8 && armortype <= 12) {// 装身具エンチャント成功確率
                                pc.getInventory().setEquipped(l1iteminstance1, false);
                                if (enchant_level <= 0) {
                                    enchant_chance_armor = 9 * Config.ENCHANT_CHANCE_ACCESSORY; // コンフィグ5面0建て成功確率=
                                    // 45％
                                } else {
                                    enchant_chance_armor = (8 * Config.ENCHANT_CHANCE_ACCESSORY) / enchant_level; // コンフィグ5面1ウォン成功確率=
                                    // 40％2
                                    // =
                                    // 20％3
                                    // =
                                    // 13％
                                }
                                /** オペレータの場合 */
                                if (pc.isGm()) {
                                    pc.sendPackets(new S_SystemMessage("\\aDアクセサリー成功確率：[ " + enchant_chance_armor + " ]"));
                                }
                            } else {
                                int chance = 0;
                                try {
                                    chance = ArmorEnchantList.getInstance().getArmorEnchant(l1iteminstance1.getItemId());
                                } catch (Exception e) {
                                    System.out.println("WeaponEnchantList chance Error");
                                }

                                if (enchant_level >= 6) {
                                    if (l1iteminstance1.getMr() > 0) {
                                        enchant_chance_armor = 80 / ((enchant_level - safe_enchant + 1) * 2)
                                                / (enchant_level / 7 != 0 ? 1 * 2 : 1) / (enchant_level_tmp)
                                                + Config.ENCHANT_CHANCE_ARMOR + chance;
                                    } else {
                                        enchant_chance_armor = 90 / ((enchant_level - safe_enchant + 1) * 2)
                                                / (enchant_level / 7 != 0 ? 1 * 2 : 1) / (enchant_level_tmp)
                                                + Config.ENCHANT_CHANCE_ARMOR + chance;
                                    }
                                } else {
                                    if (l1iteminstance1.getItem().get_safeenchant() == 0) {
                                        if (armorId == 900020 || armorId == 900021) {
                                            enchant_chance_armor = Config.CREST_ENCHANT_CHANCE;

                                        } else if (l1iteminstance1.getMr() > 0) {
                                            enchant_chance_armor = 80 / ((enchant_level - safe_enchant + 1) * 2)
                                                    / (enchant_level / 7 != 0 ? 1 * 2 : 1) / (enchant_level_tmp)
                                                    + Config.ENCHANT_CHANCE_ARMOR + chance;
                                        } else {
                                            enchant_chance_armor = 90 / ((enchant_level - safe_enchant + 1) * 2)
                                                    / (enchant_level / 7 != 0 ? 1 * 2 : 1) / (enchant_level_tmp)
                                                    + Config.ENCHANT_CHANCE_ARMOR + chance;
                                        }

                                    } else {
                                        if (l1iteminstance1.getMr() > 0) {
                                            enchant_chance_armor = 80 / ((enchant_level - safe_enchant + 1) * 2)
                                                    / (enchant_level / 7 != 0 ? 1 * 2 : 1) / (enchant_level_tmp)
                                                    + Config.ENCHANT_CHANCE_ARMOR;
                                        } else {
                                            enchant_chance_armor = 90 / ((enchant_level - safe_enchant + 1) * 2)
                                                    / (enchant_level / 7 != 0 ? 1 * 2 : 1) / (enchant_level_tmp)
                                                    + Config.ENCHANT_CHANCE_ARMOR;
                                        }

                                    }
                                }
                                if (pc.isGm()) {
                                    pc.sendPackets(new S_SystemMessage("\\aD確率: [" + enchant_chance_armor + "] 追加: ["
                                            + chance + "] チャンス: [" + rnd + "]"));
                                    pc.sendPackets(new S_SystemMessage("\\aD追加 : [ " + chance + " ]"));
                                    pc.sendPackets(new S_SystemMessage("\\aDチャンス : [ " + rnd + " ]"));
                                }
                            }

                            if (rnd < enchant_chance_armor) {
                                int randomEnchantLevel = 0;
                                // int randomEnchantLevel =
                                // RandomELevel(l1iteminstance1, itemId);
                                //
                                if (enchant_level >= 3 && safe_enchant == 0) {
                                    randomEnchantLevel = 1;
                                    // **執行防具無条件1ずつ浮遊するよう** // *
                                } else {
                                    randomEnchantLevel = RandomELevel(l1iteminstance1, itemId);
                                }
                                SuccessEnchant(pc, l1iteminstance1, client, randomEnchantLevel);
                            } else if (enchant_level >= 9 && rnd < (enchant_chance_armor * 2)) {
                                pc.sendPackets(new S_ServerMessage(160, l1iteminstance1.getLogName(), "$245", "$248"));
                            } else if (itemId == 810012) {
                                pc.sendPackets(new S_ServerMessage(4056, l1iteminstance1.getLogName()));
                                if (enchant_level <= 0)
                                    return;
                                // エンチャント：％0％s消滅のくびきを克服しました。
                                SuccessEnchant(pc, l1iteminstance1, client, -1);
                            } else if (itemId == 810013) {
                                pc.sendPackets(new S_ServerMessage(4056, l1iteminstance1.getLogName()));
                                // エンチャント：％0％s消滅のくびきを克服しました。
                            } else {
                                FailureEnchant(pc, l1iteminstance1, client);
                            }
                        }
                    }
                    break;
                    default:
                        if (itemId >= 40136 && itemId <= 40161 || itemId == 410027) { // 炎
                            int soundid = 3198;
                            if (pc.getZoneType() != 1) {
                                pc.sendPackets(new S_SystemMessage("村でのみ使用可能です。"));
                                return;
                            }
                            if (itemId == 40154) {
                                soundid = 3198;
                            } else if (itemId == 40152) {
                                soundid = 2031;
                            } else if (itemId == 40141) {
                                soundid = 2028;
                            } else if (itemId == 40160) {
                                soundid = 2030;
                            } else if (itemId == 40145) {
                                soundid = 2029;
                            } else if (itemId == 40159) {
                                soundid = 2033;
                            } else if (itemId == 40151) {
                                soundid = 2032;
                            } else if (itemId == 40161) {
                                soundid = 2037;
                            } else if (itemId == 40142) {
                                soundid = 2036;
                            } else if (itemId == 40146) {
                                soundid = 2039;
                            } else if (itemId == 40148) {
                                soundid = 2043;
                            } else if (itemId == 40143) {
                                soundid = 2041;
                            } else if (itemId == 40156) {
                                soundid = 2042;
                            } else if (itemId == 40139) {
                                soundid = 2040;
                            } else if (itemId == 40137) {
                                soundid = 2047;
                            } else if (itemId == 40136) {
                                soundid = 2046;
                            } else if (itemId == 40138) {
                                soundid = 2048;
                            } else if (itemId == 40140) {
                                soundid = 2051;
                            } else if (itemId == 40144) {
                                soundid = 2053;
                            } else if (itemId == 40147) {
                                soundid = 2045;
                            } else if (itemId == 40149) {
                                soundid = 2034;
                            } else if (itemId == 40150) {
                                soundid = 2055;
                            } else if (itemId == 40153) {
                                soundid = 2038;
                            } else if (itemId == 40155) {
                                soundid = 2044;
                            } else if (itemId == 40157) {
                                soundid = 2035;
                            } else if (itemId == 40158) {
                                soundid = 2049;
                            } else {
                                soundid = 3198;
                            }

                            S_SkillSound s_skillsound = new S_SkillSound(pc.getId(), soundid);
                            pc.sendPackets(s_skillsound);
                            pc.broadcastPacket(s_skillsound);
                            pc.getInventory().removeItem(l1iteminstance, 1);
                            // スペルスクロール
                        } else if ((itemId >= 40859 && itemId <= 40898) && itemId != 40863) {
                            if (pc.isSkillDelay()) {
                                pc.sendPackets(new S_ServerMessage(281));
                                return;
                            }
                            // 40863はテレポートスクロールとして処理される
                            if (spellsc_objid == pc.getId() && l1iteminstance.getItem().getUseType() != 30) {
                                // spell_buff
                                pc.sendPackets(new S_ServerMessage(281));
                                // \f1魔法が無効になりました。
                                return;
                            }
                            pc.getInventory().removeItem(l1iteminstance, 1);
                            if (spellsc_objid == 0 && l1iteminstance.getItem().getUseType() != 0
                                    && l1iteminstance.getItem().getUseType() != 26
                                    && l1iteminstance.getItem().getUseType() != 27) {
                                return;
                                // ターゲットが存在しない場合にhandleCommands頌歌なるため、ここでreturn
                                // handleCommands側判断及び処理しなければならない部分かもしれない
                            }
                            cancelAbsoluteBarrier(pc);
                            // アブ小ガルトバリアの解除
                            int skillid = itemId - 40858;
                            L1SkillUse l1skilluse = new L1SkillUse();
                            l1skilluse.handleCommands(client.getActiveChar(), skillid, spellsc_objid, spellsc_x, spellsc_y,
                                    null, 0, L1SkillUse.TYPE_SPELLSC);
                        } else if (itemId >= 41357 && itemId <= 41382) {
                            // アルファベット炎
                            int soundid = itemId - 34946;
                            S_SkillSound s_skillsound = new S_SkillSound(pc.getId(), soundid);
                            pc.sendPackets(s_skillsound);
                            pc.broadcastPacket(s_skillsound);
                            pc.getInventory().removeItem(l1iteminstance, 1);
                        } else if ((itemId >= 41277 && itemId <= 41292) || (itemId >= 49049 && itemId <= 49064)
                                || (itemId >= 210048 && itemId <= 210063 || (itemId >= 30051 && itemId <= 30054))) { // 料理アイテム
                            L1Cooking.useCookingItem(pc, l1iteminstance);
                        } else if (itemId >= 41383 && itemId <= 41400) { // 家具
                            useFurnitureItem(pc, itemId, itemObjid);
                        } else if (itemId > 40169 && itemId < 40226 || itemId >= 45000 && itemId <= 45022
                                || itemId == 3000095) { // 魔法書
                            useSpellBook(pc, l1iteminstance, itemId);
                        } else if (itemId > 40225 && itemId < 40232 || itemId == 5560 || itemId == 3000090) {
                            if (pc.isCrown() || pc.isGm()) {
                                if (itemId == 40226 && pc.getLevel() >= 15) {
                                    SpellBook4(pc, l1iteminstance, client);
                                } else if (itemId == 40228 && pc.getLevel() >= 30) {
                                    SpellBook4(pc, l1iteminstance, client);
                                } else if (itemId == 40227 && pc.getLevel() >= 40) {
                                    SpellBook4(pc, l1iteminstance, client);
                                } else if ((itemId == 40231 || itemId == 40232) && pc.getLevel() >= 45) {
                                    SpellBook4(pc, l1iteminstance, client);
                                } else if (itemId == 40230 && pc.getLevel() >= 50) {
                                    SpellBook4(pc, l1iteminstance, client);
                                } else if (itemId == 40229 && pc.getLevel() >= 55) {
                                    SpellBook4(pc, l1iteminstance, client);
                                } else if (itemId == 5560 && pc.getLevel() >= 60) {
                                    SpellBook4(pc, l1iteminstance, client);
                                } else if (itemId == 3000090 && pc.getLevel() >= 80) {
                                    SpellBook4(pc, l1iteminstance, client);
                                } else {
                                    pc.sendPackets(new S_ServerMessage(312)); // LVが低く
                                }
                            } else {
                                pc.sendPackets(new S_ServerMessage(79));
                            }
                        } else if (itemId >= 40232 && itemId <= 40264 // 精霊の水晶
                                || itemId >= 41149 && itemId <= 41153 || itemId == 3000091) {
                            useElfSpellBook(pc, l1iteminstance, itemId);
                        } else if (itemId > 40264 && itemId < 40280 || itemId == 5559 || itemId == 3000089
                                || itemId == 3000097) {
                            if (pc.isDarkelf() || pc.isGm()) {
                                if (itemId >= 40265 && itemId <= 40269
                                        // 闇精霊の水晶
                                        && pc.getLevel() >= 15) {
                                    SpellBook1(pc, l1iteminstance, client);
                                } else if (itemId >= 40270 && itemId <= 40274
                                        // / 闇精霊の水晶
                                        && pc.getLevel() >= 30) {
                                    SpellBook1(pc, l1iteminstance, client);
                                } else if (itemId >= 40275 && itemId <= 40279 && pc.getLevel() >= 45) {
                                    SpellBook1(pc, l1iteminstance, client);
                                } else if (itemId == 5559 && pc.getLevel() >= 60) {// アーマーブレイク
                                    SpellBook1(pc, l1iteminstance, client);
                                } else if (itemId == 3000089 && pc.getLevel() >= 80) {
                                    SpellBook1(pc, l1iteminstance, client);
                                } else if (itemId == 3000097 && pc.getLevel() >= 85) {
                                    SpellBook1(pc, l1iteminstance, client);
                                } else {
                                    pc.sendPackets(new S_ServerMessage(312));
                                }
                            } else {
                                pc.sendPackets(new S_ServerMessage(79));
                                // (原文：闇精霊の水晶はダークエルフだけ習得することができます。 ）
                            }
                        } else if (itemId >= 40164 && itemId <= 40166 // 技術書
                                || itemId >= 41147 && itemId <= 41148 || itemId == 3000092) {
                            if (pc.isKnight() || pc.isGm()) {
                                if (itemId >= 40164 && itemId <= 40165 // スタン、縮小アーマー
                                        && pc.getLevel() >= 50) {
                                    SpellBook3(pc, l1iteminstance, client);
                                } else if (itemId >= 41147 && itemId <= 41148 // ソリッドキャリッジ、カウンターバリア
                                        && pc.getLevel() >= 50) {
                                    SpellBook3(pc, l1iteminstance, client);
                                } else if (itemId == 40166 && pc.getLevel() >= 60) { // バウンスアタック
                                    SpellBook3(pc, l1iteminstance, client);
                                } else if (itemId == 3000092 && pc.getLevel() >= 80) {
                                    SpellBook3(pc, l1iteminstance, client);
                                } else {
                                    pc.sendPackets(new S_ServerMessage(312));
                                }
                            } else {
                                pc.sendPackets(new S_ServerMessage(79));
                            }
                        } else if (itemId >= 210020 && itemId <= 210034 || itemId == 3000093) {
                            if (pc.isDragonknight() || pc.isGm()) {
                                if (itemId >= 210020 && itemId <= 210023 // 用ナイトの書板
                                        && pc.getLevel() >= 15) {
                                    SpellBook5(pc, l1iteminstance, client);
                                } else if (itemId >= 210024 && itemId <= 210031 // 用ナイトの書板
                                        && pc.getLevel() >= 30) {
                                    SpellBook5(pc, l1iteminstance, client);
                                } else if (itemId >= 210032 && itemId <= 210034 && pc.getLevel() >= 45) {
                                    SpellBook5(pc, l1iteminstance, client);
                                } else if (itemId == 3000093 && pc.getLevel() >= 80) {
                                    SpellBook5(pc, l1iteminstance, client);
                                } else {
                                    pc.sendPackets(new S_ServerMessage(312));
                                }
                            } else {
                                pc.sendPackets(new S_ServerMessage(79));
                            }
                        } else if (itemId >= 210000 && itemId <= 210019 || itemId == 3000096) {
                            if (pc.isBlackwizard() || pc.isGm()) {
                                if (itemId >= 210000 && itemId <= 210004 // 記憶の水晶
                                        && pc.getLevel() >= 10) {
                                    SpellBook6(pc, l1iteminstance, client);
                                } else if (itemId >= 210005 && itemId <= 210009 // 記憶の水晶
                                        && pc.getLevel() >= 20) {
                                    SpellBook6(pc, l1iteminstance, client);
                                } else if (itemId >= 210010 && itemId <= 210014 && pc.getLevel() >= 30) {
                                    SpellBook6(pc, l1iteminstance, client);
                                } else if (itemId >= 210015 && itemId <= 210019 && pc.getLevel() >= 40) {
                                    SpellBook6(pc, l1iteminstance, client);
                                } else if (itemId == 3000096 && pc.getLevel() >= 80) {
                                    SpellBook6(pc, l1iteminstance, client);
                                } else {
                                    pc.sendPackets(new S_ServerMessage(312));
                                }
                            } else {
                                pc.sendPackets(new S_ServerMessage(79));
                            }

                        } else if (itemId >= 210121 && itemId <= 210132 || itemId == 3000094) { // 戦士スキル
                            if (pc.isWarrior()) {
                                if (itemId >= 210121 && itemId <= 210125) { // アクティブスキル
                                    warriorSkill(pc, l1iteminstance, false);
                                } else if (itemId == 3000094 && pc.getLevel() >= 80) { // アクティブスキル
                                    warriorSkill(pc, l1iteminstance, false);
                                } else { // パッシブスキル。
                                    warriorSkill(pc, l1iteminstance, true);
                                }
                            }

                        } else {
                            int locX = ((L1EtcItem) l1iteminstance.getItem()).get_locx();
                            int locY = ((L1EtcItem) l1iteminstance.getItem()).get_locy();
                            short mapId = ((L1EtcItem) l1iteminstance.getItem()).get_mapid();
                            if (locX != 0 && locY != 0) {
                                if (pc.get_DuelLine() != 0) {
                                    pc.sendPackets(new S_ServerMessage(647));
                                    return;
                                }
                                if (pc.getMap().isEscapable() || pc.isGm()) {
                                    new L1Teleport().teleport(pc, locX, locY, mapId, pc.getHeading(), true);
                                    pc.getInventory().removeItem(l1iteminstance, 1);
                                } else {
                                    pc.sendPackets(new S_ServerMessage(647));
                                }
                                cancelAbsoluteBarrier(pc);
                            } else {
                                if (l1iteminstance.getCount() < 1) {
                                    pc.sendPackets(new S_ServerMessage(329, l1iteminstance.getLogName()));
                                } /*
			       * else { pc.sendPackets(new S_ServerMessage(74,
			       * l1iteminstance.getLogName())); }
			       */
                            }
                        }
                        break;
                }
            } else if (l1iteminstance.getItem().getType2() == 1) {
                int min = l1iteminstance.getItem().getMinLevel();
                int max = l1iteminstance.getItem().getMaxLevel();
                if (min != 0 && min > pc.getLevel()) {// このアイテムは％0レベル以上にならないと使用できません。
                    pc.sendPackets(new S_ServerMessage(318, String.valueOf(min)));
                } else if (max != 0 && max < pc.getLevel()) {// このアイテムは％dレベル以下のみ使用することができます。
                    // S_ServerMessageでは、引数が表示されない
                    if (max < 50) {
                        pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_LEVEL_OVER, max));
                    } else {
                        pc.sendPackets(new S_SystemMessage("このアイテムは" + max + "レベル以下のみ使用することができます。"));
                    }
                } else {
                    if (pc.isGm()) {
                        UseWeapon(pc, l1iteminstance);
                    } else if (pc.isCrown() && l1iteminstance.getItem().isUseRoyal()
                            || pc.isKnight() && l1iteminstance.getItem().isUseKnight()
                            || pc.isElf() && l1iteminstance.getItem().isUseElf()
                            || pc.isWizard() && l1iteminstance.getItem().isUseMage()
                            || pc.isDarkelf() && l1iteminstance.getItem().isUseDarkelf()
                            || pc.isDragonknight() && l1iteminstance.getItem().isUseDragonKnight()
                            || pc.isBlackwizard() && l1iteminstance.getItem().isUseBlackwizard()
                            || pc.isWarrior() && l1iteminstance.getItem().isUseWarrior()) {
                        UseWeapon(pc, l1iteminstance);
                    } else {
                        // \f1あなたのクラスでは、このアイテムは使用できません。
                        pc.sendPackets(new S_ServerMessage(264));

                    }
                }
            } else if (l1iteminstance.getItem().getType2() == 2) { // 種別：防具
                if (pc.isGm()) {
                    UseArmor(pc, l1iteminstance);
                } else if (pc.isCrown() && l1iteminstance.getItem().isUseRoyal()
                        || pc.isKnight() && l1iteminstance.getItem().isUseKnight()
                        || pc.isElf() && l1iteminstance.getItem().isUseElf()
                        || pc.isWizard() && l1iteminstance.getItem().isUseMage()
                        || pc.isDarkelf() && l1iteminstance.getItem().isUseDarkelf()
                        || pc.isDragonknight() && l1iteminstance.getItem().isUseDragonKnight()
                        || pc.isBlackwizard() && l1iteminstance.getItem().isUseBlackwizard()
                        || pc.isWarrior() && l1iteminstance.getItem().isUseWarrior()) {

                    int min = ((L1Armor) l1iteminstance.getItem()).getMinLevel();
                    int max = ((L1Armor) l1iteminstance.getItem()).getMaxLevel();
                    if (min != 0 && min > pc.getLevel()) {
                        // このアイテムは％0レベル以上にならないと使用できません。
                        pc.sendPackets(new S_ServerMessage(318, String.valueOf(min)));
                    } else if (max != 0 && max < pc.getLevel()) {
                        if (max < 50) {
                            pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_LEVEL_OVER, max));
                        } else {
                            pc.sendPackets(new S_SystemMessage("このアイテムは" + max + "レベル以下のみ使用することができます。"));
                        }
                    } else {
                        UseArmor(pc, l1iteminstance);
                    }
                } else {
                    // \f1あなたのクラスでは、このアイテムは使用できません。
                    pc.sendPackets(new S_ServerMessage(264));
                }
            }

            // 効果遅延がある場合は、現在の時刻をセット
            if (isDelayEffect) {
                if (itemId == 410008 || itemId == 700012 || itemId == 30043 || itemId == 30045 || itemId == 30026) {
                    int chargeCount = l1iteminstance.getChargeCount();
                    if (itemId == 30043) { // 討伐隊員のポケット
                        pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "クエスト開始：ナイトタウン討伐隊員に会っ開始"));
                    } else if (itemId == 30045) { // ドラゴンの骨を収集屋のポケット
                        pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "クエスト開始：シルバーナイトタウンのドラゴンの骨を収集屋に会っ開始"));
                    }
                    Timestamp ts = new Timestamp(System.currentTimeMillis());
                    l1iteminstance.setChargeCount(l1iteminstance.getChargeCount() - 1);
                    if (chargeCount <= 1) {
                        pc.getInventory().removeItem(l1iteminstance, 1);
                    } else {
                        l1iteminstance.setLastUsed(ts);
                        pc.getInventory().updateItem(l1iteminstance, L1PcInventory.COL_CHARGE_COUNT);
                        pc.getInventory().saveItem(l1iteminstance, L1PcInventory.COL_CHARGE_COUNT);
                    }
                } else {
                    Timestamp ts = new Timestamp(System.currentTimeMillis());
                    l1iteminstance.setLastUsed(ts);
                    pc.getInventory().updateItem(l1iteminstance, L1PcInventory.COL_DELAY_EFFECT);
                    pc.getInventory().saveItem(l1iteminstance, L1PcInventory.COL_DELAY_EFFECT);
                }
            }
            L1ItemDelay.onItemUse(pc, l1iteminstance); // アイテムの遅延開始
        }
    }

    @SuppressWarnings("deprecation")
    private void normalBuff(L1PcInstance pc, L1ItemInstance useItem) {
        Calendar currentDate = Calendar.getInstance();
        Timestamp lastUsed = useItem.getLastUsed();
        if (lastUsed == null
                || currentDate.getTimeInMillis() > lastUsed.getTime() + (1000 * Config.NORMAL_PROTECTION * 1)) {
            pc.sendPackets(new S_SkillSound(pc.getId(), 12536));
            pc.broadcastPacket(new S_SkillSound(pc.getId(), 12536));
            if (pc.hasSkillEffect(L1SkillId.RANK_BUFF_5)) {
                pc.setSkillEffect(L1SkillId.RANK_BUFF_5, 600 * 1000);
            } else {
                pc.setSkillEffect(L1SkillId.RANK_BUFF_5, 600 * 1000);
            }
            useItem.setLastUsed(new Timestamp(currentDate.getTimeInMillis()));
        } else {
            long i = (lastUsed.getTime() + (1000 * Config.NORMAL_PROTECTION * 1)) - currentDate.getTimeInMillis();
            Calendar cal = (Calendar) currentDate.clone();
            cal.setTimeInMillis(cal.getTimeInMillis() + i);
            pc.sendPackets(new S_SystemMessage(
                            i / 60000 + "分の間（" + cal.getTime().getHours() + ":" + cal.getTime().getMinutes() + "まで）は使用できません。"),
                    true);
        }
    }

    private boolean createNewItem(L1PcInstance pc, int item_id, int count) {
        L1ItemInstance item = ItemTable.getInstance().createItem(item_id);
        if (item != null) {
            item.setCount(count);
            if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
                pc.getInventory().storeItem(item);
            } else {
                pc.sendPackets(new S_ServerMessage(82));
                // 重量ゲージが不足したり、インベントリがいっぱいよりにできません。
                return false;
            }
            pc.sendPackets(new S_ServerMessage(403, item.getLogName())); // %0を手に入れました。
            return true;
        } else {
            return false;
        }
    }

    private boolean createNewItem2(L1PcInstance pc, int item_id, int count, int EnchantLevel) {
        L1ItemInstance item = ItemTable.getInstance().createItem(item_id);
        if (item != null) {
            item.setCount(count);
            item.setEnchantLevel(EnchantLevel);
            item.setIdentified(true);
            if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
                pc.getInventory().storeItem(item);
            } else {
                pc.sendPackets(new S_ServerMessage(82));
                // 重量ゲージが不足したり、インベントリがいっぱいよりにできません。
                return false;
            }
            pc.sendPackets(new S_ServerMessage(403, item.getLogName())); // %0を手に入れました。
            return true;
        } else {
            return false;
        }
    }

    private static boolean white(L1PcInstance pc, int item_id, int count, int EnchantLevel) {
        L1ItemInstance item = ItemTable.getInstance().createItem(item_id);
        if (item != null) {
            item.setCount(count);
            item.setEnchantLevel(EnchantLevel);
            item.setIdentified(true);
            if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
                pc.getInventory().storeItem(item);
            } else {
                pc.sendPackets(new S_ServerMessage(82));
                // 重量ゲージが不足したり、インベントリがいっぱいよりにできません。
                return false;
            }
            pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
            // %0を手に入れました。

            if (Config.PURE_WHITE_T) {
                Timestamp deleteTime = null;
                deleteTime = new Timestamp(System.currentTimeMillis() + (1000 * 60 * 4320));// 1日
                item.setEndTime(deleteTime);
                pc.getInventory().updateItem(item, L1PcInventory.COL_REMAINING_TIME);
                pc.getInventory().saveItem(item, L1PcInventory.COL_REMAINING_TIME);
            } else {
            }

            pc.saveInventory();
            return true;
        } else {
            return false;
        }
    }

    private boolean createNewItemTrade(L1PcInstance pc, int item_id, int count, int enchant, int bless, int attr,
                                       boolean identi) {
        L1ItemInstance item = ItemTable.getInstance().createItem(item_id);
        if (item != null) {
            item.setCount(count);
            item.setIdentified(identi);
            item.setEnchantLevel(enchant);
            item.setAttrEnchantLevel(attr);
            if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
                pc.getInventory().storeItem(item);
                item.setBless(bless);
                pc.getInventory().updateItem(item, L1PcInventory.COL_BLESS);
                pc.getInventory().saveItem(item, L1PcInventory.COL_BLESS);
            } else {
                pc.sendPackets(new S_ServerMessage(82)); // 重量ゲージが不足したり、インベントリがいっぱいよりにできません。
                return false;
            }
            pc.sendPackets(new S_ServerMessage(403, item.getLogName())); // %0を手に入れました。
            return true;
        } else {
            return false;
        }
    }

    private void AttrEnchant(L1PcInstance pc, L1ItemInstance item, int item_id) {
        int attr_level = item.getAttrEnchantLevel();
        int chance = _random.nextInt(80) + 1;
        if (item_id == 210067) { // 火武器強化スクロール
            if (attr_level == 0) {
                if (chance < 20) {
                    pc.sendPackets(new S_ServerMessage(1410, item.getLogName()));
                    item.setAttrEnchantLevel(1);
                } else {
                    pc.sendPackets(new S_ServerMessage(1411, item.getLogName()));
                }
            } else if (attr_level == 1) {
                if (chance < 10) {
                    pc.sendPackets(new S_ServerMessage(1410, item.getLogName()));
                    item.setAttrEnchantLevel(2);
                } else {
                    pc.sendPackets(new S_ServerMessage(1411, item.getLogName()));
                }
            } else if (attr_level == 2) {
                if (chance < 7) {
                    pc.sendPackets(new S_ServerMessage(1410, item.getLogName()));
                    item.setAttrEnchantLevel(3);
                } else {
                    pc.sendPackets(new S_ServerMessage(1411, item.getLogName()));
                }
            } else if (attr_level == 3) {
                if (item.getEnchantLevel() >= 9) {
                    if (chance < 5) {
                        pc.sendPackets(new S_ServerMessage(1410, item.getLogName()));
                        item.setAttrEnchantLevel(4);
                    } else {
                        pc.sendPackets(new S_ServerMessage(1411, item.getLogName()));
                    }
                } else {
                    pc.sendPackets(new S_ServerMessage(79));
                    return;
                }
            } else if (attr_level == 4) {
                if (item.getEnchantLevel() >= 10) {
                    if (chance < 3) {
                        pc.sendPackets(new S_ServerMessage(1410, item.getLogName()));
                        item.setAttrEnchantLevel(5);
                    } else {
                        pc.sendPackets(new S_ServerMessage(1411, item.getLogName()));
                    }
                } else {
                    pc.sendPackets(new S_ServerMessage(79));
                    return;
                }
            } else if (attr_level == 14) {
                pc.sendPackets(new S_ServerMessage(79));
                return;
            }
        } else if (item_id == 210066) { // 水の武器強化スクロール
            if (attr_level == 0) {
                if (chance < 20) {
                    pc.sendPackets(new S_ServerMessage(1410, item.getLogName()));
                    item.setAttrEnchantLevel(6);
                } else {
                    pc.sendPackets(new S_ServerMessage(1411, item.getLogName()));
                }
            } else if (attr_level == 6) {
                if (chance < 10) {
                    pc.sendPackets(new S_ServerMessage(1410, item.getLogName()));
                    item.setAttrEnchantLevel(7);
                } else {
                    pc.sendPackets(new S_ServerMessage(1411, item.getLogName()));
                }
            } else if (attr_level == 7) {
                if (chance < 7) {
                    pc.sendPackets(new S_ServerMessage(1410, item.getLogName()));
                    item.setAttrEnchantLevel(8);
                } else {
                    pc.sendPackets(new S_ServerMessage(1411, item.getLogName()));
                }
            } else if (attr_level == 8) {
                if (item.getEnchantLevel() >= 9) {
                    if (chance < 5) {
                        pc.sendPackets(new S_ServerMessage(1410, item.getLogName()));
                        item.setAttrEnchantLevel(9);
                    } else {
                        pc.sendPackets(new S_ServerMessage(1411, item.getLogName()));
                    }
                } else {
                    pc.sendPackets(new S_ServerMessage(79));
                    return;
                }
            } else if (attr_level == 9) {
                if (item.getEnchantLevel() >= 10) {
                    if (chance < 3) {
                        pc.sendPackets(new S_ServerMessage(1410, item.getLogName()));
                        item.setAttrEnchantLevel(10);
                    } else {
                        pc.sendPackets(new S_ServerMessage(1411, item.getLogName()));
                    }
                } else {
                    pc.sendPackets(new S_ServerMessage(79));
                    return;
                }
            } else if (attr_level == 10) {
                pc.sendPackets(new S_ServerMessage(79));
                return;
            }
        } else if (item_id == 210064) { // 風の武器強化スクロール
            if (attr_level == 0) {
                if (chance < 20) {
                    pc.sendPackets(new S_ServerMessage(1410, item.getLogName()));
                    item.setAttrEnchantLevel(11);
                } else {
                    pc.sendPackets(new S_ServerMessage(1411, item.getLogName()));
                }
            } else if (attr_level == 11) {
                if (chance < 10) {
                    pc.sendPackets(new S_ServerMessage(1410, item.getLogName()));
                    item.setAttrEnchantLevel(12);
                } else {
                    pc.sendPackets(new S_ServerMessage(1411, item.getLogName()));
                }
            } else if (attr_level == 12) {
                if (chance < 7) {
                    pc.sendPackets(new S_ServerMessage(1410, item.getLogName()));
                    item.setAttrEnchantLevel(13);
                } else {
                    pc.sendPackets(new S_ServerMessage(1411, item.getLogName()));
                }
            } else if (attr_level == 13) {
                if (item.getEnchantLevel() >= 9) {
                    if (chance < 5) {
                        pc.sendPackets(new S_ServerMessage(1410, item.getLogName()));
                        item.setAttrEnchantLevel(14);
                    } else {
                        pc.sendPackets(new S_ServerMessage(1411, item.getLogName()));
                    }
                } else {
                    pc.sendPackets(new S_ServerMessage(79));
                    return;
                }
            } else if (attr_level == 14) {
                if (item.getEnchantLevel() >= 10) {
                    if (chance < 3) {
                        pc.sendPackets(new S_ServerMessage(1410, item.getLogName()));
                        item.setAttrEnchantLevel(15);
                    } else {
                        pc.sendPackets(new S_ServerMessage(1411, item.getLogName()));
                    }
                } else {
                    pc.sendPackets(new S_ServerMessage(79));
                    return;
                }
            } else if (attr_level == 15) {
                pc.sendPackets(new S_ServerMessage(79));
                return;
            }
        } else if (item_id == 210065) { // 土地の武器強化スクロール
            if (attr_level == 0) {
                if (chance < 20) {
                    pc.sendPackets(new S_ServerMessage(1410, item.getLogName()));
                    item.setAttrEnchantLevel(16);
                } else {
                    pc.sendPackets(new S_ServerMessage(1411, item.getLogName()));
                }
            } else if (attr_level == 16) {
                if (chance < 10) {
                    pc.sendPackets(new S_ServerMessage(1410, item.getLogName()));
                    item.setAttrEnchantLevel(17);
                } else {
                    pc.sendPackets(new S_ServerMessage(1411, item.getLogName()));
                }
            } else if (attr_level == 17) {
                if (chance < 7) {
                    pc.sendPackets(new S_ServerMessage(1410, item.getLogName()));
                    item.setAttrEnchantLevel(18);
                } else {
                    pc.sendPackets(new S_ServerMessage(1411, item.getLogName()));
                }
            } else if (attr_level == 18) {
                if (item.getEnchantLevel() >= 9) {
                    if (chance < 5) {
                        pc.sendPackets(new S_ServerMessage(1410, item.getLogName()));
                        item.setAttrEnchantLevel(19);
                    } else {
                        pc.sendPackets(new S_ServerMessage(1411, item.getLogName()));
                    }
                } else {
                    pc.sendPackets(new S_ServerMessage(79));
                    return;
                }
            } else if (attr_level == 19) {
                if (item.getEnchantLevel() >= 10) {
                    if (chance < 3) {
                        pc.sendPackets(new S_ServerMessage(1410, item.getLogName()));
                        item.setAttrEnchantLevel(20);
                    } else {
                        pc.sendPackets(new S_ServerMessage(1411, item.getLogName()));
                    }
                } else {
                    pc.sendPackets(new S_ServerMessage(79));
                    return;
                }
            } else if (attr_level == 20) {
                pc.sendPackets(new S_ServerMessage(79));
                return;
            }
        }
        pc.getInventory().consumeItem(item_id, 1);
        pc.getInventory().updateItem(item, L1PcInventory.COL_ATTRENCHANTLVL);
        pc.getInventory().saveItem(item, L1PcInventory.COL_ATTRENCHANTLVL);
    }

    public void AttrChangeEnchant(L1PcInstance pc, L1ItemInstance item, int itemId) {
        int attr_level = item.getAttrEnchantLevel();
        int AttrScroll = 0;

        if (itemId == 560030) { // 火属性（ファイアー属性変換注文）
            AttrScroll = 0;
        } else if (itemId == 560031) { // 水属性（受領の属性変換注文）
            AttrScroll = 5;
        } else if (itemId == 560032) { // 風の属性（風鈴の属性変換注文）
            AttrScroll = 10;
        } else if (itemId == 560033) { // 地の属性（指令の属性変換注文）
            AttrScroll = 15;
        } else {
            pc.sendPackets(new S_ServerMessage(79));
            // 何も起こらなかった。
            return;
        }
        if (!pc.getInventory().checkItem(itemId, 1)) {
            return;
        }
        if (attr_level > 0) {
            if (AttrScroll + 1 <= attr_level && attr_level <= AttrScroll + 5) {
                pc.sendPackets(new S_ServerMessage(3319));
                // 同じ属性には、使用することができません。
                return;
            }
            if (attr_level % 5 == 0) {
                pc.sendPackets(new S_ServerMessage(3296, item.getLogName()));
                // エンチャント：％0にきらびやかな大自然の力が染みます。
                item.setAttrEnchantLevel(AttrScroll + 5);
            } else {
                pc.sendPackets(new S_ServerMessage(1410, item.getLogName()));
                // エンチャント：％0にきらめく大自然の力が染みます。
                item.setAttrEnchantLevel(attr_level % 5 + AttrScroll);
            }
            pc.getInventory().consumeItem(itemId, 1);
            pc.getInventory().updateItem(item, L1PcInventory.COL_ATTRENCHANTLVL);
            pc.getInventory().saveItem(item, L1PcInventory.COL_ATTRENCHANTLVL);
        } else {
            pc.sendPackets(new S_ServerMessage(79)); // 何も起こらなかった。
        }
    }

    private void SuccessEnchant(L1PcInstance pc, L1ItemInstance item, GameClient client, int i) {
        String s = "";
        String sa = "";
        String sb = "";
        String s1 = item.getName();
        String pm = "";
        if (item.getEnchantLevel() > 0) {
            pm = "+";
        }
        if (item.getItem().getType2() == 1) {
            if (!item.isIdentified() || item.getEnchantLevel() == 0) {
                switch (i) {
                    case -1:
                        s = s1;
                        sa = "$246";
                        sb = "$247";
                        break;

                    case 1: // '\001'
                        s = s1;
                        sa = "$245";
                        sb = "$247";
                        break;

                    case 2: // '\002'
                        s = s1;
                        sa = "$245";
                        sb = "$248";
                        break;

                    case 3: // '\003'
                        s = s1;
                        sa = "$245";
                        sb = "$248";
                        break;
                }
            } else {
                switch (i) {
                    case -1:
                        s = (new StringBuilder()).append(pm + item.getEnchantLevel()).append(" ").append(s1).toString();
                        // \f1%0この％2％1光ります。
                        sa = "$246";
                        sb = "$247";
                        break;

                    case 1: // '\001'
                        s = (new StringBuilder()).append(pm + item.getEnchantLevel()).append(" ").append(s1).toString();
                        // \f1%0この％2％1光ります。
                        sa = "$245";
                        sb = "$247";
                        break;

                    case 2: // '\002'
                        s = (new StringBuilder()).append(pm + item.getEnchantLevel()).append(" ").append(s1).toString();
                        // \f1%0％2％1光ります。
                        sa = "$245";
                        sb = "$248";
                        break;

                    case 3: // '\003'
                        s = (new StringBuilder()).append(pm + item.getEnchantLevel()).append(" ").append(s1).toString();
                        // \f1%0この％2％1光ります。
                        sa = "$245";
                        sb = "$248";
                        break;
                }
            }
        } else if (item.getItem().getType2() == 2) {
            if (!item.isIdentified() || item.getEnchantLevel() == 0) {
                switch (i) {
                    case -1:
                        s = s1;
                        sa = "$246";
                        sb = "$247";
                        break;

                    case 1: // '\001'
                        s = s1;
                        sa = "$252";
                        sb = "$247 ";
                        break;

                    case 2: // '\002'
                        s = s1;
                        sa = "$252";
                        sb = "$248 ";
                        break;

                    case 3: // '\003'
                        s = s1;
                        sa = "$252";
                        sb = "$248 ";
                        break;
                }
            } else {
                switch (i) {
                    case -1:
                        s = (new StringBuilder()).append(pm + item.getEnchantLevel()).append(" ").append(s1).toString();
                        // \f1％0％2％1光ります。
                        sa = "$246";
                        sb = "$247";
                        break;

                    case 1: // '\001'
                        s = (new StringBuilder()).append(pm + item.getEnchantLevel()).append(" ").append(s1).toString();
                        // \f1%0％2％1光ります。
                        sa = "$252";
                        sb = "$247 ";
                        break;

                    case 2: // '\002'
                        s = (new StringBuilder()).append(pm + item.getEnchantLevel()).append(" ").append(s1).toString();
                        // \f1％0％2％1光ります。
                        sa = "$252";
                        sb = "$248 ";
                        break;

                    case 3: // '\003'
                        s = (new StringBuilder()).append(pm + item.getEnchantLevel()).append(" ").append(s1).toString();
                        // \f1％0％2％1光ります。
                        sa = "$252";
                        sb = "$248 ";
                        break;
                }
            }
        }
        pc.sendPackets(new S_ServerMessage(161, s, sa, sb));
        int oldEnchantLvl = item.getEnchantLevel();
        int newEnchantLvl = item.getEnchantLevel() + i;
        int safe_enchant = item.getItem().get_safeenchant();

        /** 全ワールドメッセージまき **/
        if (item.getItem().getType2() == 1 && newEnchantLvl >= 10) {
            L1World.getInstance().broadcastPacketToAll(new S_ServerMessage(4446, item.getLogName()));
        }

        if (item.getItem().getType2() == 2) {
            if (item.getItem().getType() >= 8 && item.getItem().getType() <= 12) {
                if (newEnchantLvl >= 8) {
                    L1World.getInstance().broadcastPacketToAll(new S_ServerMessage(4445, item.getLogName()));
                }
            } else if (newEnchantLvl >= 9) {
                L1World.getInstance().broadcastPacketToAll(new S_ServerMessage(4445, item.getLogName()));
            }
        }

        item.setEnchantLevel(newEnchantLvl);
        client.getActiveChar().getInventory().updateItem(item, L1PcInventory.COL_ENCHANTLVL);
        pc.saveInventory();
        if (newEnchantLvl > safe_enchant) {
            client.getActiveChar().getInventory().saveItem(item, L1PcInventory.COL_ENCHANTLVL);
            pc.saveInventory();
            /** ログファイルの保存 **/
            LoggerInstance.getInstance().addEnchant(pc, item, true);
        }

        if (item.getItem().getType2() == 1 && Config.LOGGING_WEAPON_ENCHANT != 0) {
            if (safe_enchant == 0 || newEnchantLvl >= Config.LOGGING_WEAPON_ENCHANT) {
                LogEnchantTable logenchant = new LogEnchantTable();
                logenchant.storeLogEnchant(pc.getId(), item.getId(), oldEnchantLvl, newEnchantLvl);
            }
        }
        if (item.getItem().getType2() == 2 && Config.LOGGING_ARMOR_ENCHANT != 0) {
            if (safe_enchant == 0 || newEnchantLvl >= Config.LOGGING_ARMOR_ENCHANT) {
                LogEnchantTable logenchant = new LogEnchantTable();
                logenchant.storeLogEnchant(pc.getId(), item.getId(), oldEnchantLvl, newEnchantLvl);
            }
        }
        /** マネージャーのログ */
        if (item.getItem().getType2() == 1) {
            // manager.LogEnchantAppend("成功：武器"、pc.getName（）、Integer.toString（item.getEnchantLevel（））、item.getName（）、item.getId（））;
            LinAllManager.getInstance().EnchantAppend(item.getName(), oldEnchantLvl, newEnchantLvl, pc.getName(), 0);
            if (newEnchantLvl >= 8) {
                // manager.LogEnchantAppend（「[ゴインチェン]成功：武器
                // "、pc.getName（）、oldEnchantLvl +" - > "+
                // newEnchantLvl、item.getName（）、item.getId（））;
                LinAllManager.getInstance().EnchantAppend(item.getName(), oldEnchantLvl, newEnchantLvl, pc.getName(),
                        0);
            }
            if (newEnchantLvl >= Config.WEAPON_ENCHANT) {
                // manager.LogEnchantAppend（
                // "[最高エンチャン]成功：武器"、pc.getName（）、oldEnchantLvl + " - >" +
                // newEnchantLvl、item.getName（）、item.getId（））;
                LinAllManager.getInstance().EnchantAppend(item.getName(), oldEnchantLvl, newEnchantLvl, pc.getName(),
                        0);
            }
            if (newEnchantLvl >= Config.WEAPON_PREMIUM_ENCHANT) {
                // manager.LogEnchantAppend（
                // "[執行級最高エンチャン]成功：武器"、pc.getName（）、oldEnchantLvl + " - >" +
                // newEnchantLvl、item.getName（）、item.getId（））;
                LinAllManager.getInstance().EnchantAppend(item.getName(), oldEnchantLvl, newEnchantLvl, pc.getName(),
                        0);
            }
        }

        if (item.getItem().getType2() == 2) {
            if (newEnchantLvl >= 8) {
                // manager.LogEnchantAppend（「[ゴインチェン]成功：防具
                // "、pc.getName（）、oldEnchantLvl +" - > "+
                // newEnchantLvl、item.getName（）、item.getId（））;
                LinAllManager.getInstance().EnchantAppend(item.getName(), oldEnchantLvl, newEnchantLvl, pc.getName(),
                        0);
            }
            if (newEnchantLvl >= Config.ARMOR_ENCHANT) {
                // manager.LogEnchantAppend（
                // "[最高エンチャン]成功：防具"、pc.getName（）、oldEnchantLvl + " - >" +
                // newEnchantLvl、item.getName（）、item.getId（））;
                LinAllManager.getInstance().EnchantAppend(item.getName(), oldEnchantLvl, newEnchantLvl, pc.getName(),
                        0);
            }
        }

        if (item.getItem().getType2() == 2) {
            if (item.isEquipped()) {
                if (item.getItem().getType() >= 8 && item.getItem().getType() <= 12) {
                } else {
                    pc.getAC().addAc(-i);
                }
                int i2 = item.getItem().getItemId();
                if (i2 == 20011 || i2 == 20110 || i2 == 120011 || i2 == 22204 || i2 == 22223 || i2 == 22205
                        || i2 == 22206 || i2 == 22207 || i2 == 22213 || i2 == 22365 || i2 == 120110 || i2 == 93001
                        || i2 >= 222300 && i2 <= 222303 || i2 == 222328) {// タラスのブーツ
                    pc.getResistance().addMr(i);
                    pc.sendPackets(new S_SPMR(pc));
                }
                if (i2 == 20056 || i2 == 120056 || i2 == 220056 || i2 == 93002) { // マジッククローク
                    pc.getResistance().addMr(i * 2);
                    pc.sendPackets(new S_SPMR(pc));
                }
                if (i2 == 20079 || i2 == 20078 || i2 == 20074 || i2 == 120074) {
                    pc.getResistance().addMr(i * 3);
                    pc.sendPackets(new S_SPMR(pc));
                }
            }

            pc.sendPackets(new S_OwnCharStatus(pc));
        }
    }

    private void FailureEnchant(L1PcInstance pc, L1ItemInstance item, GameClient client) {
        String s = "";
        String sa = "";
        int itemType = item.getItem().getType2();
        int itemId = item.getItem().getItemId();
        String nameId = item.getName();
        String pm = "";

        if (itemType == 1) { // 武器
            if (!item.isIdentified() || item.getEnchantLevel() == 0) {
                s = nameId;
                // \f1%0この強烈な％1に輝いた後、蒸発していないことになります。
                sa = "$245";
            } else {
                if (item.getEnchantLevel() > 0) {
                    pm = "+";
                }
                s = (new StringBuilder()).append(pm + item.getEnchantLevel()).append(" ").append(nameId).toString();
                // \f1%0が強烈に％1に輝いた後、蒸発していないことになります。
                sa = "$245";
            }
        } else if (itemType == 2) { // 防具
            if (!item.isIdentified() || item.getEnchantLevel() == 0) {
                s = nameId;
                // \f1%0この強烈な％1に輝いた後、蒸発していないことになります。
                sa = " $252";
            } else {
                if (item.getEnchantLevel() > 0) {
                    pm = "+";
                }
                s = (new StringBuilder()).append(pm + item.getEnchantLevel()).append(" ").append(nameId).toString();
                // \f1%0この強烈な％1に輝いた後、蒸発していないことになります。
                sa = " $252";
            }
        }
        if ((itemId >= 1115 && itemId <= 1118) || (itemId >= 22250 && itemId <= 22252)) { // 神妙セット
            pc.sendPackets(new S_ServerMessage(1310));
            pc.getInventory().setEquipped(item, false);
            item.setEnchantLevel(0);
            pc.getInventory().updateItem(item, L1PcInventory.COL_ENCHANTLVL);
            pc.saveInventory();
            LinAllManager.getInstance().EnchantAppend(item.getName(), item.getEnchantLevel(), 0, pc.getName(), 1);
            if (itemType == 1) {
            } else if (itemType == 2) {
            }
        } else {
            pc.sendPackets(new S_ServerMessage(164, s, sa));
            pc.getInventory().removeItem(item, item.getCount());
            LinAllManager.getInstance().EnchantAppend(item.getName(), item.getEnchantLevel(), 0, pc.getName(), 1);
            /** ログファイルの保存 **/
            LoggerInstance.getInstance().addEnchant(pc, item, false);

            /** DBとメモリのアップデッドエ **/
            // RestoreItemTable.getInstance().AddRestoreItem(pc.getId(),
            // new
            // L1RestoreItemInstance(item.getItemId(),item.getEnchantLevel(),item.getAttrEnchantLevel(),item.getBless()));
        }
        pc.sendPackets(new S_OwnCharStatus(pc));
    }

    // 天上のポーション
    private void UseExpPotion(L1PcInstance pc, int item_id) {
        if (pc.hasSkillEffect(71) == true) { // ディケイポーションの状態
            pc.sendPackets(new S_ServerMessage(698, ""));
            // 魔力によって何も飲むことができません。
            return;
        }
        cancelAbsoluteBarrier(pc);

        int time = 0;
        if (item_id == 210094 || item_id == 30105) { // 経験値上昇ポーション
            time = 1800; // 30分
        }

        pc.setSkillEffect(EXP_POTION, time * 1000);
        pc.sendPackets(new S_PacketBox(S_PacketBox.EMERALD_ICON_NEW, 0x01, time));
        pc.sendPackets(new S_SkillSound(pc.getId(), 7976));
        pc.sendPackets(new S_ServerMessage(1313));
    }

    private void useGreenPotion(L1PcInstance pc, int itemId) {
        if (pc.hasSkillEffect(71) == true) { // ディケイポーションの状態
            pc.sendPackets(new S_ServerMessage(698)); // \f1馬力では何も飲むことができません。
            return;
        }

        // アブ小ガルトバリアの解除
        cancelAbsoluteBarrier(pc);

        int time = 0;
        if (itemId == L1ItemId.POTION_OF_HASTE_SELF) { // グリーン一部
            time = 300;
        } else if (itemId == L1ItemId.B_POTION_OF_HASTE_SELF) { // 祝福されたグリーン
            // いくつかの
            time = 350;
        } else if (itemId == 40018 || itemId == 41342 || itemId == 3000162) { // 強化グリーンいくつかは、祝福されたワイン、メデューサの血
            time = 1800;
            if (itemId == 3000162) {
                if (pc.cL == 0) {
                    pc.sendPackets(new S_MatizCloudia(1, 1));
                    pc.cL = 1;
                } else if (pc.cL == 2) {
                    pc.sendPackets(new S_MatizCloudia(1, 3));
                    pc.cL = 3;
                }
            }
        } else if (itemId == 140018 || itemId == 41338) { // 祝福された強化グリーンの一部
            time = 2250;
        } else if (itemId == 40039) { // ワイン
            time = 600;
        } else if (itemId == 40040) { // ウイスキー
            time = 900;
        } else if (itemId == 40030 || itemId == 30067) { // 象牙の塔のヘイスト一部
            time = 300;
        } else if (itemId == 41261 || itemId == 41262 || itemId == 41268 || itemId == 41269 || itemId == 41271
                || itemId == 41272 || itemId == 41273) {
            time = 30;
        }

        pc.sendPackets(new S_SkillSound(pc.getId(), 191));
        pc.broadcastPacket(new S_SkillSound(pc.getId(), 191));
        // XXX：ヘイストアイテム装備時、酔った状態が解除されるか不明
        if (pc.getHasteItemEquipped() > 0) {
            return;
        }
        // 酔った状態を解除
        pc.setDrink(false);

        // ヘイストライキ、グレーターヘイストライキとは重複しない
        if (pc.hasSkillEffect(HASTE)) {
            pc.killSkillEffectTimer(HASTE);
            pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
            pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
            pc.setMoveSpeed(0);
        } else if (pc.hasSkillEffect(GREATER_HASTE)) {
            pc.killSkillEffectTimer(GREATER_HASTE);
            pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
            pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
            pc.setMoveSpeed(0);
        } else if (pc.hasSkillEffect(STATUS_HASTE)) {
            pc.killSkillEffectTimer(STATUS_HASTE);
            pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
            pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
            pc.setMoveSpeed(0);
        }

        // スロー、グレータースロー、円タンル中はスロー状態を解除するだけで
        if (pc.hasSkillEffect(SLOW)) { // スロー
            pc.killSkillEffectTimer(SLOW);
            pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
            pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
        } else if (pc.hasSkillEffect(GREATER_SLOW)) { // グレートスロー
            pc.killSkillEffectTimer(GREATER_SLOW);
            pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
            pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
        } else if (pc.hasSkillEffect(ENTANGLE)) { // 円タンル
            pc.killSkillEffectTimer(ENTANGLE);
            pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
            pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
        } else {
            pc.sendPackets(new S_SkillHaste(pc.getId(), 1, time));
            pc.broadcastPacket(new S_SkillHaste(pc.getId(), 1, 0));
            pc.setMoveSpeed(1);
            pc.setSkillEffect(STATUS_HASTE, time * 1000);
        }
    }

    private void useBravePotion(L1PcInstance pc, int item_id) {
        if (pc.hasSkillEffect(71) == true) { // ディケイポーションの状態
            pc.sendPackets(new S_ServerMessage(698));// \f1馬力では何も飲むことができません。
            return;
        }
        // アブ小ガルトバリアの解除
        cancelAbsoluteBarrier(pc);

        int time = 0;
        if (item_id == L1ItemId.POTION_OF_EMOTION_BRAVERY || item_id == 30073) { // ブレイブ一部
            time = 300;
        } else if (item_id == L1ItemId.B_POTION_OF_EMOTION_BRAVERY) { // 祝福されたブレイヴの一部
            time = 350;
        } else if (item_id == 41415) { // 強化ブレイブ一部
            time = 1800;
        } else if (item_id == 40068 || item_id == 30076) { // エルブワトフル
            time = 480;
            if (pc.hasSkillEffect(STATUS_BRAVE)) { // 勇気とは重複しない。
                pc.killSkillEffectTimer(STATUS_BRAVE);
                pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
                pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
                pc.setBraveSpeed(0);
            }
            if (pc.hasSkillEffect(WIND_WALK)) { // ウィンドウォークとは重複しない
                pc.killSkillEffectTimer(WIND_WALK);
                pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
                pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
                pc.setBraveSpeed(0);
            }
            if (pc.hasSkillEffect(DANCING_BLADES)) { // ダンシングしては、重複していない
                pc.killSkillEffectTimer(DANCING_BLADES);
                pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
                Broadcaster.broadcastPacket(pc, new S_SkillBrave(pc.getId(), 0, 0));
                pc.setBraveSpeed(0);
            }
        } else if (item_id == 140068) { // 祝福されたエルブワトフル
            time = 700;
            if (pc.hasSkillEffect(STATUS_BRAVE)) { // 容器の効果とは重複しない。
                pc.killSkillEffectTimer(STATUS_BRAVE);
                pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
                pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
                pc.setBraveSpeed(0);
            }
            if (pc.hasSkillEffect(WIND_WALK)) { // ウィンドウォークとは重複しない
                pc.killSkillEffectTimer(WIND_WALK);
                pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
                pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
                pc.setBraveSpeed(0);
            }
            if (pc.hasSkillEffect(DANCING_BLADES)) { // ダンシングしては、重複していない
                pc.killSkillEffectTimer(DANCING_BLADES);
                pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
                Broadcaster.broadcastPacket(pc, new S_SkillBrave(pc.getId(), 0, 0));
                pc.setBraveSpeed(0);
            }
        } else if (item_id == 210110) { // 福祉エルヴンワッフル
            time = 1800;
            if (pc.hasSkillEffect(STATUS_BRAVE)) { // 容器の効果とは重複しない。
                pc.killSkillEffectTimer(STATUS_BRAVE);
                pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
                pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
                pc.setBraveSpeed(0);
            }
            if (pc.hasSkillEffect(WIND_WALK)) { // ウィンドウォークとは重複しない
                pc.killSkillEffectTimer(WIND_WALK);
                pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
                pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
                pc.setBraveSpeed(0);
            }
        } else if (item_id == 40031 || item_id == 30075) { // このビルブラッド
            time = 600;
        } else if (item_id == 210115) { // 福祉悪魔の血
            time = 1800;
        } else if (item_id == 40733) { // 名誉のコイン
            time = 600;
            if (pc.hasSkillEffect(STATUS_ELFBRAVE)) {
                pc.killSkillEffectTimer(STATUS_ELFBRAVE);
                pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
                pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
                pc.setBraveSpeed(0);
            }
            if (pc.hasSkillEffect(HOLY_WALK)) { // 号 - リワークとは重複しない
                pc.killSkillEffectTimer(HOLY_WALK);
                pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
                pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
                pc.setBraveSpeed(0);
            }
            if (pc.hasSkillEffect(MOVING_ACCELERATION)) { // ムービング悪セレーションとは重複しない
                pc.killSkillEffectTimer(MOVING_ACCELERATION);
                pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
                pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
                pc.setBraveSpeed(0);
            }
            if (pc.hasSkillEffect(WIND_WALK)) { // ウィンドウォークとは重複しない
                pc.killSkillEffectTimer(WIND_WALK);
                pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
                pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
                pc.setBraveSpeed(0);
            }
            if (pc.hasSkillEffect(STATUS_FRUIT)) { // ユグドラの実とは重複しない
                pc.killSkillEffectTimer(STATUS_FRUIT);
                pc.setBraveSpeed(0);
            }
        }

        if (item_id == 40068 || item_id == 140068 || item_id == 210110 || item_id == 30076) { // エルヴンワッフル
            pc.sendPackets(new S_SkillBrave(pc.getId(), 3, time));
            pc.broadcastPacket(new S_SkillBrave(pc.getId(), 3, 0));
            pc.setSkillEffect(STATUS_ELFBRAVE, time * 1000);
        } else {
            pc.sendPackets(new S_SkillBrave(pc.getId(), 1, time));
            pc.broadcastPacket(new S_SkillBrave(pc.getId(), 1, 0));
            pc.setSkillEffect(STATUS_BRAVE, time * 1000);
        }
        pc.sendPackets(new S_SkillSound(pc.getId(), 751));
        pc.broadcastPacket(new S_SkillSound(pc.getId(), 751));
        pc.setBraveSpeed(1);
    }

    private void useDragonPearl(L1PcInstance pc, int itemId) {// ドラゴンの真珠の時間そのままフィット
        if (pc.hasSkillEffect(DECAY_POTION) == true) { // ディケイポーションの状態
            pc.sendPackets(new S_ServerMessage(698));// \f1魔力によって何も飲むことができません。
            return;
        }
        cancelAbsoluteBarrier(pc);
        int time = (10 * 60 * 1000) + 1000; // 10分1秒
        if (pc.hasSkillEffect(STATUS_DRAGON_PEARL)) {
            pc.killSkillEffectTimer(STATUS_DRAGON_PEARL);
            pc.sendPackets(new S_Liquor(pc.getId(), 0));
            pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGON_PEARL, 8, 0)); // 既存のアイコンを削除するために入れたよう
            pc.setPearl(0);
        }
        pc.sendPackets(new S_ServerMessage(1065));
        pc.sendPackets(new S_SkillSound(pc.getId(), 7976));
        pc.broadcastPacket(new S_SkillSound(pc.getId(), 7976));
        pc.setSkillEffect(STATUS_DRAGON_PEARL, time);
        pc.sendPackets(new S_Liquor(pc.getId(), 8));
        pc.broadcastPacket(new S_Liquor(pc.getId(), 8));
        pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGON_PEARL, 8, 600)); // 10分1秒
        pc.setPearl(1);
    }

    private void useFruit(L1PcInstance pc, int item_id) {// ユグドラの実1/19修正
        if (pc.hasSkillEffect(DECAY_POTION) == true) { // ディケイポーションの状態
            pc.sendPackets(new S_ServerMessage(698));// \f1魔力によって何も飲むことができません。
            return;
        }
        // アブ小ガルトバリアの解除
        cancelAbsoluteBarrier(pc);

        int time = 0;
        if (item_id == 210036 || item_id == 30077) {
            time = 480;
        }
        if (pc.hasSkillEffect(STATUS_BRAVE)) {
            pc.killSkillEffectTimer(STATUS_BRAVE);
            pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
            pc.setBraveSpeed(0);
        }
        pc.sendPackets(new S_SkillBrave(pc.getId(), 4, time));
        pc.sendPackets(new S_SkillSound(pc.getId(), 7110));
        pc.broadcastPacket(new S_SkillSound(pc.getId(), 7110));
        pc.setSkillEffect(STATUS_FRUIT, time * 1000);
    }

    private int _EinhasadGift2[] = { 210130, 210131, 210132, 210125, // 戦士の引張
            1137, 203017, 1136, 202003, 121, 119, 123, 124, 30177, 30178, 30179, 222328 };

    private void einhasadGift(L1PcInstance pc, int itemId) {
        // TODO 自動生成されたメソッド・スタブ
        int itemid = 820018;
        int count = 1;
        L1ItemInstance gosu = pc.getInventory().storeItem(itemid, count);
        pc.sendPackets(new S_ServerMessage(403, gosu.getItem().getName() + " (" + count + ")"));

        gosu = pc.getInventory().storeItem(_EinhasadGift2[_random.nextInt(_EinhasadGift2.length)], count);
        pc.sendPackets(new S_ServerMessage(403, gosu.getItem().getName() + " (" + count + ")"));

    }

    private void useBluePotion(L1PcInstance pc, int itemId) {
        if (pc.hasSkillEffect(DECAY_POTION)) {// ディケイポーション
            pc.sendPackets(new S_ServerMessage(698));
            return;
        }
        cancelAbsoluteBarrier(pc);// アブソリュート解除
        int time = 0;
        switch (itemId) {
            case 30083:
            case 40015:
            case 40736:
                time = 600;
                break;
            case 140015:
                time = 700;
                break;
            case 41142:
                time = 300;
                break;
            case 210114:
                time = 1800;
                break;
            default:
                break;
        }
        pc.sendPackets(new S_SkillIconGFX(34, time, true));
        pc.sendPackets(new S_SkillSound(pc.getId(), 190));
        pc.broadcastPacket(new S_SkillSound(pc.getId(), 190));
        if (itemId == 41142) {
            pc.setSkillEffect(STATUS_BLUE_POTION2, time * 1000);
        } else {
            pc.setSkillEffect(STATUS_BLUE_POTION, time * 1000);
        }
        pc.sendPackets(new S_ServerMessage(1007));// MP回復速度が速くなります。
    }

    private void useWisdomPotion(L1PcInstance pc, int item_id) {
        if (pc.hasSkillEffect(71) == true) { // ディケイポーションの状態
            pc.sendPackets(new S_ServerMessage(698)); // \f1魔力によって何も飲むことができません。
            return;
        }
        // アブ小ガルトバリアの解除
        cancelAbsoluteBarrier(pc);

        int time = 0; // 時間は4の倍数すること
        switch (item_id) {
            case 40016:
            case 30089:
                time = 300;
                break;
            case 140016:
                time = 360;
                break;
            case 210113:
                time = 1000;
                break;
            default:
                return;
        }

        if (!pc.hasSkillEffect(STATUS_WISDOM_POTION)) {
            pc.getAbility().addSp(2);
            pc.addMpr(2);
        }
        pc.sendPackets(new S_SkillIconWisdomPotion((int) (time)));
        pc.sendPackets(new S_SkillSound(pc.getId(), 750));
        pc.broadcastPacket(new S_SkillSound(pc.getId(), 750));
        pc.setSkillEffect(STATUS_WISDOM_POTION, time * 1000);

    }

    private void useBlessOfEva(L1PcInstance pc, int item_id) {
        if (pc.hasSkillEffect(71) == true) { // ディケイポーションの状態
            pc.sendPackets(new S_ServerMessage(698)); // \f1魔力によって何も飲むことができません。
            return;
        }

        // アブ小ガルトバリアの解除
        cancelAbsoluteBarrier(pc);

        int time = 0;
        switch (item_id) {
            case 40032:// エヴァの祝福
                time = 1800;
                break;
            case 40041:
                time = 300;
                break;
            case 41344:
                time = 2100;
                break;
            default:
                return;
        }
        if (pc.hasSkillEffect(STATUS_UNDERWATER_BREATH)) {
            int timeSec = pc.getSkillEffectTimeSec(STATUS_UNDERWATER_BREATH);
            time += timeSec;
            if (time > 3600) {
                time = 3600;
            }
        }
        pc.sendPackets(new S_SkillIconBlessOfEva(pc.getId(), time));
        pc.sendPackets(new S_SkillSound(pc.getId(), 190));
        pc.broadcastPacket(new S_SkillSound(pc.getId(), 190));
        pc.setSkillEffect(STATUS_UNDERWATER_BREATH, time * 1000);
    }

    private void useBlindPotion(L1PcInstance pc) {
        if (pc.hasSkillEffect(DECAY_POTION)) {
            pc.sendPackets(new S_ServerMessage(698)); // \f1魔力によって何も飲むことができません。
            return;
        }

        // アブ小ガルトバリアの解除
        cancelAbsoluteBarrier(pc);

        int time = 480;
        if (pc.hasSkillEffect(CURSE_BLIND)) {
            pc.killSkillEffectTimer(CURSE_BLIND);
        } else if (pc.hasSkillEffect(DARKNESS)) {
            pc.killSkillEffectTimer(DARKNESS);
        } else if (pc.hasSkillEffect(LINDBIOR_SPIRIT_EFFECT)) {
            pc.killSkillEffectTimer(LINDBIOR_SPIRIT_EFFECT);
        }

        if (pc.hasSkillEffect(STATUS_FLOATING_EYE)) {
            pc.sendPackets(new S_CurseBlind(2));
        } else {
            pc.sendPackets(new S_CurseBlind(1));
        }

        pc.setSkillEffect(CURSE_BLIND, time * 1000);
    }

    private void useCashScroll(L1PcInstance pc, int item_id) {
        int time = 3600;
        int scroll = 0;

        if (pc.hasSkillEffect(STATUS_CASHSCROLL)) {
            pc.killSkillEffectTimer(STATUS_CASHSCROLL);
            pc.addMaxHp(-50);
            pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
            if (pc.isInParty()) {
                pc.getParty().updateMiniHP(pc);
            }
            pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
        }
        if (pc.hasSkillEffect(STATUS_CASHSCROLL2)) {
            pc.killSkillEffectTimer(STATUS_CASHSCROLL2);
            pc.addMaxMp(-40);
            pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
        }
        if (pc.hasSkillEffect(STATUS_CASHSCROLL3)) {
            pc.killSkillEffectTimer(STATUS_CASHSCROLL3);
            pc.addDmgup(-3);
            pc.addHitup(-3);
            // pc.addSp(-3);
            pc.getAbility().addSp(-3);
            pc.sendPackets(new S_SPMR(pc));
        }

        if (item_id == 410010) {
            scroll = 6993;
            pc.addMaxHp(50);
            pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
            if (pc.isInParty()) {
                pc.getParty().updateMiniHP(pc);
            }
            pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
        } else if (item_id == 410011) {
            scroll = 6994;
            pc.addMaxMp(40);
            pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
        } else if (item_id == 410012 || item_id == 30063) {
            scroll = 6995;
            pc.addDmgup(3);
            pc.addHitup(3);
            // pc.addSp(3);
            pc.getAbility().addSp(3);
            pc.sendPackets(new S_SPMR(pc));
        }

        if (item_id == 410012 || item_id == 30063) {
            time = 1800;
            pc.sendPackets(new S_SkillSound(pc.getId(), 751));
            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 751));
            pc.sendPackets(new S_NewSkillIcon(STATUS_CASHSCROLL3, true, time));
            pc.setSkillEffect(scroll, time * 1000);
        } else {
            pc.sendPackets(new S_SkillSound(pc.getId(), scroll));
            pc.broadcastPacket(new S_SkillSound(pc.getId(), scroll));
            pc.setSkillEffect(scroll, time * 1000);
        }
    }

    private boolean usePolyScroll(L1PcInstance pc, int item_id, String s) {
        int time = 0;
        switch (item_id) {
            case 40088:
            case 40096:
                time = 1800;
                break;
            case 140088:
                time = 2100;
                break;
            case 210112:
                time = 3600;
                break;
            case 40008:
                time = 7200;
                break;
            case 140008:
                time = 7200;
                break;
            default:
                return false;
        }

        if (s.equalsIgnoreCase("ranking class polymorph")) {
            if (pc.isCrown()) {
                if (pc.get_sex() == 0)
                    s = "rangking prince male";
                else
                    s = "rangking prince female";
            } else if (pc.isKnight()) {
                if (pc.get_sex() == 0)
                    s = "rangking knight male";
                else
                    s = "rangking knight female";
            } else if (pc.isElf()) {
                if (pc.get_sex() == 0)
                    s = "rangking elf male";
                else
                    s = "rangking elf female";
            } else if (pc.isWizard()) {
                if (pc.get_sex() == 0)
                    s = "rangking wizard male";
                else
                    s = "rangking wizard female";
            } else if (pc.isDarkelf()) {
                if (pc.get_sex() == 0)
                    s = "rangking darkelf male";
                else
                    s = "rangking darkelf female";
            } else if (pc.isDragonknight()) {
                if (pc.get_sex() == 0)
                    s = "rangking dragonknight male";
                else
                    s = "rangking dragonknight female";
            } else if (pc.isBlackwizard()) {
                if (pc.get_sex() == 0)
                    s = "rangking illusionist male";
                else
                    s = "rangking illusionist female";
            } else if (pc.isWarrior()) {
                if (pc.get_sex() == 0)
                    s = "rangking warrior male";
                else
                    s = "rangking warrior female";
            }
        }

        L1PolyMorph poly = PolyTable.getInstance().getTemplate(s);
        // System.out.println("変身 "+ s）;
        if (pc.isGm()) {
            pc.sendPackets(new S_SystemMessage("PolyName  > " + s));
            pc.sendPackets(new S_SystemMessage("PolyCode  > " + pc.getTempCharGfx()));
        }
        try {

        } catch (Exception e) {

        }
        if (poly != null || s.equals("")) {
            if (s.equals("")) {
                if (pc.getTempCharGfx() == 6034 || pc.getTempCharGfx() == 6035) {
                    return true;
                } else {
                    pc.removeSkillEffect(SHAPE_CHANGE);
                    return true;
                }
            } else if (poly.getMinLevel() <= pc.getLevel() || pc.isGm()) {
                L1PolyMorph.doPoly(pc, poly.getPolyId(), time, L1PolyMorph.MORPH_BY_ITEMMAGIC);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private void usePolyScale(L1PcInstance pc, int itemId) {
        int polyId = 0;
        if (itemId == 41154) { // 闇の鱗
            polyId = 3101;
        } else if (itemId == 41155) { // 劣化の鱗
            polyId = 3126;
        } else if (itemId == 41156) { // 背徳者の鱗
            polyId = 3888;
        } else if (itemId == 41157) { // 憎悪の鱗
            polyId = 3784;
        }
        L1PolyMorph.doPoly(pc, polyId, 600, L1PolyMorph.MORPH_BY_ITEMMAGIC);
    }

    private void usePolyScale2(L1PcInstance pc, int itemId) {
        int polyId = 0;
        if (itemId == 220001) { // サイ変身
            polyId = 11232;
        } else if (itemId == 220002) { // サイ変身
            polyId = 11234;
        } else if (itemId == 220003) { // サイ変身
            polyId = 11236;
        }
        L1PolyMorph.doPoly(pc, polyId, 600, L1PolyMorph.MORPH_BY_ITEMMAGIC);
    }

    private void usePolyPotion(L1PcInstance pc, int itemId) {
        int polyId = 0;

        if (itemId == 3000163) {
            if (pc.isElf()) {
                polyId = 12314;
            } else if (pc.isDarkelf()) {
                polyId = 12280;
            } else if (pc.isKnight() || pc.isCrown() || pc.isWarrior() || pc.isDragonknight()) {
                polyId = 12283;
            } else if (pc.isWizard() || pc.isBlackwizard()) {
                polyId = 12286;
            }
            if (pc.cL == 0) {
                pc.sendPackets(new S_MatizCloudia(1, 2));
                pc.cL = 2;
            } else if (pc.cL == 1) {
                pc.sendPackets(new S_MatizCloudia(1, 3));
                pc.cL = 3;
            }
        }
        if (itemId == 41143) {
            polyId = 6086;
        } else if (itemId == 41144) {
            polyId = 6087;
        } else if (itemId == 41145) {
            polyId = 6088;
        } else if (itemId == 30057) {
            polyId = 10429;
        } else if (itemId == 30058) {
            polyId = 10431;
        } else if (itemId == 30059) {
            polyId = 10430;
        } else if (itemId == 8000) {
            polyId = 12792;
        } else if (itemId == 8001) { // 7580ランスマスター変身
            polyId = 12237;
        } else if (itemId == 8002) { // 82進デスナイト - 完全高速
            polyId = 12015;
        } else if (itemId == 3000066) {// ナイト。のナイト。戦士。君主
            polyId = 12283;
        } else if (itemId == 3000067) {// ダークエルフ
            polyId = 12280;
        } else if (itemId == 3000068) {// ウィザード、イリュージョニスト
            polyId = 12286;
        } else if (itemId == 3000069) {// 妖精
            polyId = 12314;
        } else if (itemId == 8003) {// ランカー変身
            if (pc.isCrown()) {
                if (pc.get_sex() == 0)
                    polyId = 13715;
                else
                    polyId = 13717;
            } else if (pc.isKnight()) {
                if (pc.get_sex() == 0)
                    polyId = 13719;
                else
                    polyId = 13721;
            } else if (pc.isElf()) {
                if (pc.get_sex() == 0)
                    polyId = 13723;
                else
                    polyId = 13725;
            } else if (pc.isWizard()) {
                if (pc.get_sex() == 0)
                    polyId = 13727;
                else
                    polyId = 13729;
            } else if (pc.isDarkelf()) {
                if (pc.get_sex() == 0)
                    polyId = 13731;
                else
                    polyId = 13733;
            } else if (pc.isDragonknight()) {
                if (pc.get_sex() == 0)
                    polyId = 13735;
                else
                    polyId = 13737;
            } else if (pc.isBlackwizard()) {
                if (pc.get_sex() == 0)
                    polyId = 13739;
                else
                    polyId = 13741;
            } else if (pc.isWarrior()) {
                if (pc.get_sex() == 0)
                    polyId = 13743;
                else
                    polyId = 13745;
            }

        }
        L1PolyMorph.doPoly(pc, polyId, 1800, L1PolyMorph.MORPH_BY_ITEMMAGIC);
    }

    private void useLevelPolyScroll(L1PcInstance pc, int itemId) {
        int polyId = 0;
        if (itemId == 210097) { // 30
            if (pc.isCrown()) {
                if (pc.get_sex() == 0) {
                    polyId = 6822;
                } else {
                    polyId = 6823;
                }
            } else if (pc.isKnight()) {
                if (pc.get_sex() == 0) {
                    polyId = 6824;
                } else {
                    polyId = 6825;
                }
            } else if (pc.isElf()) {
                if (pc.get_sex() == 0) {
                    polyId = 6826;
                } else {
                    polyId = 6827;
                }
            } else if (pc.isWizard()) {
                if (pc.get_sex() == 0) {
                    polyId = 6828;
                } else {
                    polyId = 6829;
                }
            } else if (pc.isDarkelf()) {
                if (pc.get_sex() == 0) {
                    polyId = 6830;
                } else {
                    polyId = 6831;
                }
            } else if (pc.isDragonknight()) {
                if (pc.get_sex() == 0) {
                    polyId = 7139;
                } else {
                    polyId = 7140;
                }
            } else if (pc.isBlackwizard()) {
                if (pc.get_sex() == 0) {
                    polyId = 7141;
                } else {
                    polyId = 7142;
                }
            } else if (pc.isWarrior()) {
                if (pc.get_sex() == 0) {
                    polyId = 12490;
                } else {
                    polyId = 12494;
                }
            }
        } else if (itemId == 210098) { // 40
            if (pc.isCrown()) {
                if (pc.get_sex() == 0) {
                    polyId = 6832;
                } else {
                    polyId = 6833;
                }
            } else if (pc.isKnight()) {
                if (pc.get_sex() == 0) {
                    polyId = 6834;
                } else {
                    polyId = 6835;
                }
            } else if (pc.isElf()) {
                if (pc.get_sex() == 0) {
                    polyId = 6836;
                } else {
                    polyId = 6837;
                }
            } else if (pc.isWizard()) {
                if (pc.get_sex() == 0) {
                    polyId = 6838;
                } else {
                    polyId = 6839;
                }
            } else if (pc.isDarkelf()) {
                if (pc.get_sex() == 0) {
                    polyId = 6840;
                } else {
                    polyId = 6841;
                }
            } else if (pc.isDragonknight()) {
                if (pc.get_sex() == 0) {
                    polyId = 7143;
                } else {
                    polyId = 7144;
                }
            } else if (pc.isBlackwizard()) {
                if (pc.get_sex() == 0) {
                    polyId = 7145;
                } else {
                    polyId = 7146;
                }
            } else if (pc.isWarrior()) {
                if (pc.get_sex() == 0) {
                    polyId = 12490;
                } else {
                    polyId = 12494;
                }
            }
        } else if (itemId == 210099) { // 52
            if (pc.isCrown()) {
                if (pc.get_sex() == 0) {
                    polyId = 6842;
                } else {
                    polyId = 6843;
                }
            } else if (pc.isKnight()) {
                if (pc.get_sex() == 0) {
                    polyId = 6844;
                } else {
                    polyId = 6845;
                }
            } else if (pc.isElf()) {
                if (pc.get_sex() == 0) {
                    polyId = 6846;
                } else {
                    polyId = 6847;
                }
            } else if (pc.isWizard()) {
                if (pc.get_sex() == 0) {
                    polyId = 6848;
                } else {
                    polyId = 6849;
                }
            } else if (pc.isDarkelf()) {
                if (pc.get_sex() == 0) {
                    polyId = 6850;
                } else {
                    polyId = 6851;
                }
            } else if (pc.isDragonknight()) {
                if (pc.get_sex() == 0) {
                    polyId = 7147;
                } else {
                    polyId = 7148;
                }
            } else if (pc.isBlackwizard()) {
                if (pc.get_sex() == 0) {
                    polyId = 7149;
                } else {
                    polyId = 7150;
                }
            } else if (pc.isWarrior()) {
                if (pc.get_sex() == 0) {
                    polyId = 12490;
                } else {
                    polyId = 12494;
                }
            }
        } else if (itemId == 210100) { // 55
            if (pc.isCrown()) {
                if (pc.get_sex() == 0) {
                    polyId = 6852;
                } else {
                    polyId = 6853;
                }
            } else if (pc.isKnight()) {
                if (pc.get_sex() == 0) {
                    polyId = 6854;
                } else {
                    polyId = 6855;
                }
            } else if (pc.isElf()) {
                if (pc.get_sex() == 0) {
                    polyId = 6856;
                } else {
                    polyId = 6857;
                }
            } else if (pc.isWizard()) {
                if (pc.get_sex() == 0) {
                    polyId = 6858;
                } else {
                    polyId = 6859;
                }
            } else if (pc.isDarkelf()) {
                if (pc.get_sex() == 0) {
                    polyId = 6860;
                } else {
                    polyId = 6861;
                }
            } else if (pc.isDragonknight()) {
                if (pc.get_sex() == 0) {
                    polyId = 7151;
                } else {
                    polyId = 7152;
                }
            } else if (pc.isBlackwizard()) {
                if (pc.get_sex() == 0) {
                    polyId = 7153;
                } else {
                    polyId = 7154;
                }
            } else if (pc.isWarrior()) {
                if (pc.get_sex() == 0) {
                    polyId = 12490;
                } else {
                    polyId = 12494;
                }
            }
        } else if (itemId == 210101) { // 60
            if (pc.isCrown()) {
                if (pc.get_sex() == 0) {
                    polyId = 6862;
                } else {
                    polyId = 6863;
                }
            } else if (pc.isKnight()) {
                if (pc.get_sex() == 0) {
                    polyId = 6864;
                } else {
                    polyId = 6865;
                }
            } else if (pc.isElf()) {
                if (pc.get_sex() == 0) {
                    polyId = 6866;
                } else {
                    polyId = 6867;
                }
            } else if (pc.isWizard()) {
                if (pc.get_sex() == 0) {
                    polyId = 6868;
                } else {
                    polyId = 6869;
                }
            } else if (pc.isDarkelf()) {
                if (pc.get_sex() == 0) {
                    polyId = 6870;
                } else {
                    polyId = 6871;
                }
            } else if (pc.isDragonknight()) {
                if (pc.get_sex() == 0) {
                    polyId = 7155;
                } else {
                    polyId = 7156;
                }
            } else if (pc.isBlackwizard()) {
                if (pc.get_sex() == 0) {
                    polyId = 7157;
                } else {
                    polyId = 7158;
                }
            } else if (pc.isWarrior()) {
                if (pc.get_sex() == 0) {
                    polyId = 12490;
                } else {
                    polyId = 12494;
                }
            }
        } else if (itemId == 210102) { // 65
            if (pc.isCrown()) {
                if (pc.get_sex() == 0) {
                    polyId = 6872;
                } else {
                    polyId = 6873;
                }
            } else if (pc.isKnight()) {
                if (pc.get_sex() == 0) {
                    polyId = 6874;
                } else {
                    polyId = 6875;
                }
            } else if (pc.isElf()) {
                if (pc.get_sex() == 0) {
                    polyId = 6876;
                } else {
                    polyId = 6877;
                }
            } else if (pc.isWizard()) {
                if (pc.get_sex() == 0) {
                    polyId = 6878;
                } else {
                    polyId = 6879;
                }
            } else if (pc.isDarkelf()) {
                if (pc.get_sex() == 0) {
                    polyId = 6880;
                } else {
                    polyId = 6881;
                }
            } else if (pc.isDragonknight()) {
                if (pc.get_sex() == 0) {
                    polyId = 7159;
                } else {
                    polyId = 7160;
                }
            } else if (pc.isBlackwizard()) {
                if (pc.get_sex() == 0) {
                    polyId = 7161;
                } else {
                    polyId = 7162;
                }
            } else if (pc.isWarrior()) {
                if (pc.get_sex() == 0) {
                    polyId = 12490;
                } else {
                    polyId = 12494;
                }
            }
        } else if (itemId == 210103) { // 70
            if (pc.isCrown()) {
                if (pc.get_sex() == 0) {
                    polyId = 6882;
                } else {
                    polyId = 6883;
                }
            } else if (pc.isKnight()) {
                if (pc.get_sex() == 0) {
                    polyId = 6884;
                } else {
                    polyId = 6885;
                }
            } else if (pc.isElf()) {
                if (pc.get_sex() == 0) {
                    polyId = 6886;
                } else {
                    polyId = 6887;
                }
            } else if (pc.isWizard()) {
                if (pc.get_sex() == 0) {
                    polyId = 6888;
                } else {
                    polyId = 6889;
                }
            } else if (pc.isDarkelf()) {
                if (pc.get_sex() == 0) {
                    polyId = 6890;
                } else {
                    polyId = 6891;
                }
            } else if (pc.isDragonknight()) {
                if (pc.get_sex() == 0) {
                    polyId = 7163;
                } else {
                    polyId = 7164;
                }
            } else if (pc.isBlackwizard()) {
                if (pc.get_sex() == 0) {
                    polyId = 7165;
                } else {
                    polyId = 7166;
                }
            } else if (pc.isWarrior()) {
                if (pc.get_sex() == 0) {
                    polyId = 12490;
                } else {
                    polyId = 12494;
                }
            }
        } else if (itemId == 210116) { // 75
            if (pc.isCrown()) {
                if (pc.get_sex() == 0) {
                    polyId = 10987;
                } else {
                    polyId = 10988;
                }
            } else if (pc.isKnight()) {
                if (pc.get_sex() == 0) {
                    polyId = 10989;
                } else {
                    polyId = 10990;
                }
            } else if (pc.isElf()) {
                if (pc.get_sex() == 0) {
                    polyId = 10991;
                } else {
                    polyId = 10992;
                }
            } else if (pc.isWizard()) {
                if (pc.get_sex() == 0) {
                    polyId = 10993;
                } else {
                    polyId = 10994;
                }
            } else if (pc.isDarkelf()) {
                if (pc.get_sex() == 0) {
                    polyId = 10995;
                } else {
                    polyId = 10996;
                }
            } else if (pc.isDragonknight()) {
                if (pc.get_sex() == 0) {
                    polyId = 10997;
                } else {
                    polyId = 10998;
                }
            } else if (pc.isBlackwizard()) {
                if (pc.get_sex() == 0) {
                    polyId = 10999;
                } else {
                    polyId = 11000;
                }
            } else if (pc.isWarrior()) {
                if (pc.get_sex() == 0) {
                    polyId = 12490;
                } else {
                    polyId = 12494;
                }
            }
        } else if (itemId == 210117) { // 80
            if (pc.isCrown()) {
                if (pc.get_sex() == 0) {
                    polyId = 11001;
                } else {
                    polyId = 11002;
                }
            } else if (pc.isKnight()) {
                if (pc.get_sex() == 0) {
                    polyId = 11003;
                } else {
                    polyId = 11004;
                }
            } else if (pc.isElf()) {
                if (pc.get_sex() == 0) {
                    polyId = 11005;
                } else {
                    polyId = 11006;
                }
            } else if (pc.isWizard()) {
                if (pc.get_sex() == 0) {
                    polyId = 11007;
                } else {
                    polyId = 11008;
                }
            } else if (pc.isDarkelf()) {
                if (pc.get_sex() == 0) {
                    polyId = 11009;
                } else {
                    polyId = 11010;
                }
            } else if (pc.isDragonknight()) {
                if (pc.get_sex() == 0) {
                    polyId = 11011;
                } else {
                    polyId = 11012;
                }
            } else if (pc.isBlackwizard()) {
                if (pc.get_sex() == 0) {
                    polyId = 11013;
                } else {
                    polyId = 11014;
                }
            } else if (pc.isWarrior()) {
                if (pc.get_sex() == 0) {
                    polyId = 12490;
                } else {
                    polyId = 12494;
                }
            }
        }
        L1PolyMorph.doPoly(pc, polyId, 1800, L1PolyMorph.MORPH_BY_ITEMMAGIC);
    }

    public static void UseArmor(L1PcInstance activeChar, L1ItemInstance armor) {
        int type = armor.getItem().getType();
        L1PcInventory pcInventory = activeChar.getInventory();
        boolean equipeSpace; // 装備する箇所が空か
        if (type == 9) { // リングの場合
            equipeSpace = pcInventory.getTypeEquipped(2, 9) <= 4;
        } else if (type == 12) { // リングの場合
            equipeSpace = pcInventory.getTypeEquipped(2, 12) <= 2;
        } else {
            equipeSpace = pcInventory.getTypeEquipped(2, type) <= 0;
        }

        if (equipeSpace && !armor.isEquipped()) {
            // 使用した防具を装備していない場合は、その機器箇所が空の場合（装着を試みる）
            if (type == 18) { // 肩甲
                if (!activeChar.getQuest().isEnd(L1Quest.QUEST_SLOT83)) {
                    activeChar.sendPackets(new S_SystemMessage("象牙の塔の村スナップ広がりに83レベルのスロット開放後着用可能"));
                    return;
                }
            }
            if (type == 17) { // 記章
                if (!activeChar.getQuest().isEnd(L1Quest.QUEST_SLOT70)) { // 1~75の間
                    activeChar.sendPackets(new S_SystemMessage("象牙の塔の村スナップ広がりに70レベルのスロット開放後着用可能"));
                    return;
                }
            }
            if (type == 9) { // タイプが9であれば、
                if (!activeChar.getQuest().isEnd(L1Quest.QUEST_SLOT76) && pcInventory.getTypeEquipped(2, 9) >= 2) { // 1~75間
                    activeChar.sendPackets(new S_SystemMessage("象牙の塔の村スナップ広がりに76レベルのスロット開放後着用可能"));
                    return;
                } else if (!activeChar.getQuest().isEnd(L1Quest.QUEST_SLOT81)
                        && pcInventory.getTypeEquipped(2, 9) >= 3) { // 76〜80の間
                    activeChar.sendPackets(new S_SystemMessage("象牙の塔の村スナップ広がりに81レベルのスロット開放後着用可能"));
                    return;
                } else if (pcInventory.getTypeEquipped(2, 9) == 4) { // 4つ装着中であればもう着用不可
                    activeChar.sendPackets(new S_SystemMessage("これ以上着用することはできません。"));
                    return;
                }
            }
            if (pcInventory.getTypeAndItemIdEquipped(2, 9, armor.getItem().getItemId()) == 2) { // すでに2つの装着中
                activeChar.sendPackets(new S_SystemMessage("同じ名前のアイテムは、2個まで着用することができます。"));
                return;
            } else if (pcInventory.getTypeAndGradeEquipped(2, 9, armor.getItem().getGrade()) == 2) {
                if (type == 9) {
                    // アーマー、リング、着用しようとするアイテム固有のプロパティ番号3を満足するアイテムが2個着用しているとき
                    activeChar.sendPackets(new S_SystemMessage("イベント、あるいは有料アイテムは、2個まで着用することができますだ。"));
                    return;
                }
            }
            if (type == 12) { // タイプが12であれば、
                if (!activeChar.getQuest().isEnd(L1Quest.QUEST_SLOT59) && pcInventory.getTypeEquipped(2, 12) >= 1) {
                    activeChar.sendPackets(new S_SystemMessage("象牙の塔の村スナップ広がりに59レベルのスロット開放後着用可能"));
                    return;
                } else if (pcInventory.getTypeEquipped(2, 12) == 2) {
                    activeChar.sendPackets(new S_SystemMessage("これ以上着用することはできません。"));
                    return;
                }
            }
            if (pcInventory.getTypeAndItemIdEquipped(2, 12, armor.getItem().getItemId()) >= 1) {
                // すでに2つの装着中
                activeChar.sendPackets(new S_SystemMessage("同じ名前のアイテムは着用できません。"));
                return;
            } else if (type == 12) {
                if (pcInventory.getNameEquipped(2, 12, armor.getName()) >= 1) {
                    activeChar.sendPackets(new S_ServerMessage(3278));
                    // スロット拡張：同じ種類追加着用不可
                    return;
                }
            }

            int polyid = activeChar.getTempCharGfx();

            if (!L1PolyMorph.isEquipableArmor(polyid, type)) { // その変身は装備不可
                return;
            }
            if (type == 7 && pcInventory.getTypeEquipped(2, 13) >= 1
                    || type == 13 && pcInventory.getTypeEquipped(2, 7) >= 1) {
                activeChar.sendPackets(new S_ServerMessage(124));
                // \f1 すでに何かを装備しています。
                return;
            }

            if ((type == 7 || type == 13) && activeChar.getEquipSlot().getWeaponCount() == 2) {
                // \f1両手の武器を武装したままシールド（shield）を着用することはできません。
                activeChar.sendPackets(new S_ServerMessage(129));
                // ガーダーの場合両手の状態のときは無視します。
                return;
            }

            if (type == 7 && activeChar.getWeapon() != null) {
                // シールド（shield）の場合には、武器を装備していると両手武器チェック
                if (activeChar.getWeapon().getItem().isTwohandedWeapon() && armor.getItem().getUseType() != 13) {
                    // 両手武器
                    activeChar.sendPackets(new S_ServerMessage(129));
                    // \f1両手の武器を武装したままシールド（shield）を着用することはできません。
                    return;
                }
            }

            if (type == 14 && armor.getItemId() == 900022) { // ルーン
                if (activeChar.getMapId() >= 1700 && activeChar.getMapId() <= 1707) {

                } else {
                    activeChar.sendPackets(new S_SystemMessage("忘れられた島でのみ着用が可能です。"));
                    return;
                }
            }

            activeChar.cancelAbsoluteBarrier();
            pcInventory.setEquipped(armor, true);
        } else if (armor.isEquipped()) { // 使用した防具を装備していた場合（脱着をしようとする）
            pcInventory.setEquipped(armor, false);
        } else {
            activeChar.sendPackets(new S_ServerMessage(124)); // \f1すでに何かを装備しています。
        }
        activeChar.setCurrentHp(activeChar.getCurrentHp());
        activeChar.setCurrentMp(activeChar.getCurrentMp());
        activeChar.sendPackets(new S_OwnCharAttrDef(activeChar));
        activeChar.sendPackets(new S_OwnCharStatus(activeChar));
        activeChar.sendPackets(new S_SPMR(activeChar));
    }

    public static void UseWeapon(L1PcInstance activeChar, L1ItemInstance weapon) {
        boolean shieldWeapon = false;
        L1PcInventory pcInventory = activeChar.getInventory();
        L1ItemInstance current_weapon = activeChar.getEquipSlot().getWeapon();
        if (current_weapon == null || !activeChar.getEquipSlot().isWeapon(weapon)) {
            // 指定された武器が装備している武器や他の場合には、機器のだろうか確認
            int weapon_type = weapon.getItem().getType();
            int polyid = activeChar.getTempCharGfx();

            if (!L1PolyMorph.isEquipableWeapon(polyid, weapon_type)) {
                // その変身は装備不可
                return;
            }

            if (weapon.getItem().isTwohandedWeapon() && pcInventory.getTypeEquipped(2, 7) >= 1) {
                // 両手武器の場合、シールド（shield）機器の確認
                activeChar.sendPackets(new S_ServerMessage(128));
                // \f1シールド（shield）を装備しているときは、両手で持つ武器を使用することができません。
                return;
            }
        }

        activeChar.cancelAbsoluteBarrier();

        if (current_weapon != null) {
            // すでに何かを装備している場合は、前の機器をはずす
            if (current_weapon.getItem().getBless() == 2) {
                // 呪いとなっていた場合
                activeChar.sendPackets(new S_ServerMessage(150));
                // \f1 離せません。呪いをかけられているようです。
                return;
            }

            // 着用中の武器と要求された武器が同じ場合。
            if (activeChar.getEquipSlot().isWeapon(weapon)) {
                if (activeChar.getEquipSlot().getWeaponCount() >= 2) {
                    // 装着された2つの武器を解除します。
                    L1ItemInstance slot_1 = activeChar.getEquipSlot().getWeapon();
                    pcInventory.setEquipped(slot_1, false, false, false, false);
                    L1ItemInstance slot_2 = activeChar.getEquipSlot().getWeapon();
                    pcInventory.setEquipped(slot_2, false, false, false, false);
                    // 1つだけ再着用。
                    if (slot_2.getId() == weapon.getId())
                        pcInventory.setEquipped(slot_1, true, false, false, false);
                    else
                        pcInventory.setEquipped(slot_2, true, false, false, false);
                } else {
                    // 機器交換ではなく除外するだけで
                    pcInventory.setEquipped(weapon, false, false, false, false);
                }

                return;
                // 着用中の武器と要求された武器が異なる場合。
            } else {
                // 着用するアイテムと着用しているアイテムが片手斧の場合。
                if (SkillsTable.getInstance().spellCheck(activeChar.getId(), 235) && weapon.getItem().getType1() == 11
                        && current_weapon.getItem().getType1() == 11 && weapon.getItem().getType() == 6
                        && current_weapon.getItem().getType() == 6) {
                    if (pcInventory.getTypeEquipped(2, 7) >= 1) {
                        // \f1シールド（shield）を装備しているときは、両手で持つ武器を使用することができません。
                        activeChar.sendPackets(new S_ServerMessage(128));
                        return;
                    }
                    // ガーダー着用中であれば、
                    if (pcInventory.getTypeEquipped(2, 13) >= 1) {
                        // \f1シールド（shield）を装備しているときは、両手で持つ武器を使用することができません。
                        activeChar.sendPackets(new S_ServerMessage(128));
                        return;
                    }

                    if (activeChar.getEquipSlot().getWeaponCount() >= 2) {
                        // すでに着用中です。
                        activeChar.sendPackets(new S_ServerMessage(124));
                        return;
                    }

                    // // 着用スロットをシールドに置換ホウ知らせる。
                    shieldWeapon = true;
                } else {
                    // 現在着用中の武器を解除する。
                    for (L1ItemInstance item : activeChar.getEquipSlot().getWeapons())
                        pcInventory.setEquipped(item, false, false, true, false);
                }
                // by.lins
            }
        }

        if (weapon.getItemId() == 200002) { // 呪われたダイス近づい
            activeChar.sendPackets(new S_ServerMessage(149, weapon.getLogName())); // \f1％0が手に寄ってつきました。
        }
        pcInventory.setEquipped(weapon, true, false, false, shieldWeapon);
    }

    private int RandomELevel(L1ItemInstance item, int itemId) {
        // int enchant_level = item.getEnchantLevel();
        int a = 1;
        int b = 2;
        int c = 3;
        if (a > 1 || b > 2 || c > 3) {
            return 0;
        }
        if (item.getItem().getType2() == 2 && item.getItem().get_safeenchant() == 0) {
            if (item.getName().indexOf("ガーダー") > 0)
                return 1;
        }

        int j = _random.nextInt(100) + 1;
        if (itemId == L1ItemId.B_SCROLL_OF_ENCHANT_ARMOR || itemId == L1ItemId.B_SCROLL_OF_ENCHANT_WEAPON
                || itemId == L1ItemId.Inadril_T_ScrollB || itemId == 3000124 || itemId == 3000131) {
            if (item.getEnchantLevel() <= -1) {
                return a;
            } else if (item.getEnchantLevel() <= 2) {
                if (j < 32) {
                    // System.out.println("1");
                    return a;
                } else if (j >= 33 && j <= 76) {
                    // System.out.println("2");
                    return b;
                } else if (j >= 77 && j <= 100) {
                    // System.out.println("3");
                    return c;
                }
            } else if (item.getEnchantLevel() >= 3 && item.getEnchantLevel() <= 5) {
                if (j < 50) {
                    return b;
                } else {
                    return a;
                }
            }
            return a;
        } else if (itemId == 140129 || itemId == 140130) {
            if (item.getEnchantLevel() < 0) {
                if (j < 30) {
                    return b;
                } else {
                    return a;
                }
            } else if (item.getEnchantLevel() <= 2) {
                if (j < 32) {
                    return a;
                } else if (j >= 33 && j <= 60) {
                    return b;
                } else if (j >= 61 && j <= 100) {
                    return c;
                }
            } else if (item.getEnchantLevel() >= 3 && item.getEnchantLevel() <= 5) {
                if (j < 60) {
                    return b;
                } else {
                    return a;
                }
            }
            return a;
        }
        return a;
    }

    private void useSpellBook(L1PcInstance pc, L1ItemInstance item, int itemId) {
        int itemAttr = 1;
        int locAttr = 1; // 0:other 1:law 2:chaos
        boolean isLawful = true;
        int pcX = pc.getX();
        int pcY = pc.getY();
        int mapId = pc.getMapId();
        int level = pc.getLevel();
        if (itemId == 45000 || itemId == 45008 || itemId == 45018 || itemId == 45021 || itemId == 40171
                || itemId == 40179 || itemId == 40180 || itemId == 40182 || itemId == 40194 || itemId == 40197
                || itemId == 40202 || itemId == 40206 || itemId == 40213 || itemId == 40220 || itemId == 40222) {
            itemAttr = 1;
        }
        if (itemId == 45009 || itemId == 45010 || itemId == 45019 || itemId == 40172 || itemId == 40173
                || itemId == 40178 || itemId == 40185 || itemId == 40186 || itemId == 40192 || itemId == 40196
                || itemId == 40201 || itemId == 40204 || itemId == 40211 || itemId == 40221 || itemId == 40225) {
            itemAttr = 1;
        }
        /** 町で魔法を学んれるように **/
	/*
	 * if (pcX > 33116 && pcX < 33128 && pcY > 32930 && pcY < 32942 && mapId
	 * == 4 || pcX > 33135 && pcX < 33147 && pcY > 32235 && pcY < 32247 &&
	 * mapId == 4 || pcX > 1 && pcX < 40000 && pcY > 1 && pcY < 40000 &&
	 * mapId == 4 || pcX >= 32783 && pcX <= 32803 && pcY >= 32831 && pcY <=
	 * 32851 && mapId == 77) { locAttr = 1; isLawful = true; }
	 */

        /** 魔法を学ぶ本サーバー化 **/
        if (pcX > 33116 && pcX < 33128 && pcY > 32930 && pcY < 32942 && mapId == 4
                || pcX > 33135 && pcX < 33147 && pcY > 32235 && pcY < 32247 && mapId == 4
                || pcX >= 32783 && pcX <= 32803 && pcY >= 32831 && pcY <= 32851 && mapId == 77) {
            locAttr = 1;
            isLawful = true;
        }
        if (pcX > 32880 && pcX < 32892 && pcY > 32646 && pcY < 32658 && mapId == 4
                || pcX > 33135 && pcX < 33147 && pcY > 32235 && pcY < 32247 && mapId == 4
                || pcX > 32662 && pcX < 32674 && pcY > 32297 && pcY < 32309 && mapId == 4) {
            locAttr = 1;
            isLawful = true;
        }
        if (pc.isGm()) {
            SpellBook(pc, item, isLawful);
        } else if ((itemAttr == locAttr || itemAttr == 0) && locAttr != 0) {
            if (pc.isKnight()) {
                if (itemId >= 45000 && itemId <= 45007 && level >= 50) {
                    SpellBook(pc, item, isLawful);
                } else if (itemId >= 45000 && itemId <= 45007) {
                    pc.sendPackets(new S_ServerMessage(312));
                } else {
                    pc.sendPackets(new S_ServerMessage(79));
                }
            } else if (pc.isCrown() || pc.isDarkelf()) {
                if (itemId >= 45000 && itemId <= 45007 && level >= 10) {
                    SpellBook(pc, item, isLawful);
                } else if (itemId >= 45008 && itemId <= 45015 && level >= 20) {
                    SpellBook(pc, item, isLawful);
                } else if (itemId >= 45008 && itemId <= 45015 || itemId >= 45000 && itemId <= 45007) {
                    pc.sendPackets(new S_ServerMessage(312));
                } else {
                    pc.sendPackets(new S_ServerMessage(79));
                }
            } else if (pc.isElf()) {
                if (itemId >= 45000 && itemId <= 45007 && level >= 8) {
                    SpellBook(pc, item, isLawful);
                } else if (itemId >= 45008 && itemId <= 45015 && level >= 16) {
                    SpellBook(pc, item, isLawful);
                } else if (itemId >= 45016 && itemId <= 45022 && level >= 24) {
                    SpellBook(pc, item, isLawful);
                } else if (itemId >= 40170 && itemId <= 40177 && level >= 32) {
                    SpellBook(pc, item, isLawful);
                } else if (itemId >= 40178 && itemId <= 40185 && level >= 40) {
                    SpellBook(pc, item, isLawful);
                } else if (itemId >= 40186 && itemId <= 40193 && level >= 48) {
                    SpellBook(pc, item, isLawful);
                } else if (itemId >= 45000 && itemId <= 45022 || itemId >= 40170 && itemId <= 40193) {
                    pc.sendPackets(new S_ServerMessage(312));
                } else {
                    pc.sendPackets(new S_ServerMessage(79));
                }
            } else if (pc.isWizard()) {
                if (itemId >= 45000 && itemId <= 45007 && level >= 4 || itemId == 3000095) {
                    SpellBook(pc, item, isLawful);
                } else if (itemId >= 45008 && itemId <= 45015 && level >= 8) {
                    SpellBook(pc, item, isLawful);
                } else if (itemId >= 45016 && itemId <= 45022 && level >= 12) {
                    SpellBook(pc, item, isLawful);
                } else if (itemId >= 40170 && itemId <= 40177 && level >= 16) {
                    SpellBook(pc, item, isLawful);
                } else if (itemId >= 40178 && itemId <= 40185 && level >= 20) {
                    SpellBook(pc, item, isLawful);
                } else if (itemId >= 40186 && itemId <= 40193 && level >= 24) {
                    SpellBook(pc, item, isLawful);
                } else if (itemId >= 40194 && itemId <= 40201 && level >= 28) {
                    SpellBook(pc, item, isLawful);
                } else if (itemId >= 40202 && itemId <= 40209 && level >= 32) {
                    SpellBook(pc, item, isLawful);
                } else if (itemId >= 40210 && itemId <= 40217 && level >= 36) {
                    SpellBook(pc, item, isLawful);
                } else if (itemId >= 40218 && itemId <= 40225 && level >= 40) {
                    SpellBook(pc, item, isLawful);
                } else if (itemId == 3000095 && level >= 80) {
                    SpellBook(pc, item, isLawful);
                } else {
                    pc.sendPackets(new S_ServerMessage(312));
                }
            }
        } else if (itemAttr != locAttr && itemAttr != 0 && locAttr != 0) {
            pc.sendPackets(new S_ServerMessage(79));
            S_SkillSound effect = new S_SkillSound(pc.getId(), 10);
            pc.sendPackets(effect);
            pc.broadcastPacket(effect);
            pc.setCurrentHp(Math.max(pc.getCurrentHp() - 45, 0));
            if (pc.getCurrentHp() <= 0) {
                pc.death(null, true);
            }
            pc.getInventory().removeItem(item, 1);
        } else {
            pc.sendPackets(new S_ServerMessage(79));
        }
    }

    private void useElfSpellBook(L1PcInstance pc, L1ItemInstance item, int itemId) {
        int level = pc.getLevel();
        if ((pc.isElf() || pc.isGm()) && isLearnElfMagic(pc)) {
            if (itemId >= 40232 && itemId <= 40234 && level >= 10) {
                SpellBook2(pc, item);
            } else if (itemId >= 40235 && itemId <= 40236 && level >= 20) {
                SpellBook2(pc, item);
            }
            if (itemId >= 40237 && itemId <= 40240 && level >= 30) {
                SpellBook2(pc, item);
            } else if (itemId >= 40241 && itemId <= 40243 && level >= 40) {
                SpellBook2(pc, item);
            } else if (itemId >= 40244 && itemId <= 40246 && level >= 50) {
                SpellBook2(pc, item);
            } else if (itemId >= 40247 && itemId <= 40248 && level >= 30) {
                SpellBook2(pc, item);
            } else if (itemId >= 40249 && itemId <= 40250 && level >= 40) {
                SpellBook2(pc, item);
            } else if (itemId >= 40251 && itemId <= 40252 && level >= 50) {
                SpellBook2(pc, item);
            } else if (itemId == 40253 && level >= 30) {
                SpellBook2(pc, item);
            } else if (itemId == 40254 && level >= 40) {
                SpellBook2(pc, item);
            } else if (itemId == 40255 && level >= 50) {
                SpellBook2(pc, item);
            } else if (itemId == 40256 && level >= 30) {
                SpellBook2(pc, item);
            } else if (itemId == 40257 && level >= 40) {
                SpellBook2(pc, item);
            } else if (itemId >= 40258 && itemId <= 40259 && level >= 50) {
                SpellBook2(pc, item);
            } else if (itemId >= 40260 && itemId <= 40261 && level >= 30) {
                SpellBook2(pc, item);
            } else if (itemId == 40262 && level >= 40) {
                SpellBook2(pc, item);
            } else if (itemId >= 40263 && itemId <= 40264 && level >= 50) {
                SpellBook2(pc, item);
            } else if (itemId >= 41149 && itemId <= 41150 && level >= 50) {
                SpellBook2(pc, item);
            } else if (itemId == 41151 && level >= 40) {
                SpellBook2(pc, item);
            } else if (itemId >= 41152 && itemId <= 41153 && level >= 50) {
                SpellBook2(pc, item);
            } else if (itemId == 3000091 && level >= 80) {
                SpellBook2(pc, item);
            }
        } else {
            pc.sendPackets(new S_ServerMessage(79));
        }
    }

    private boolean isLearnElfMagic(L1PcInstance pc) {
        int pcX = pc.getX();
        int pcY = pc.getY();
        int pcMapId = pc.getMapId();
        if (pcX >= 1 && pcX <= 40000 && pcY >= 1 && pcY <= 40000 && pcMapId == 4// 妖精の変更も町で学んよう
                || pc.getLocation().isInScreen(new Point(33055, 32336)) && pcMapId == 4) {
            return true;
        }
        return false;
    }

    private void SpellBook(L1PcInstance pc, L1ItemInstance item, boolean isLawful) {
        String s = "";
        int i = 0;
        int level1 = 0;
        int level2 = 0;
        int l = 0;
        int i1 = 0;
        int j1 = 0;
        int k1 = 0;
        int l1 = 0;
        int i2 = 0;
        int j2 = 0;
        int k2 = 0;
        int l2 = 0;
        int i3 = 0;
        int j3 = 0;
        int k3 = 0;
        int l3 = 0;
        int i4 = 0;
        int j4 = 0;
        int k4 = 0;
        int l4 = 0;
        int i5 = 0;
        int j5 = 0;
        int k5 = 0;
        int l5 = 0;
        int i6 = 0;
        int dk3 = 0;
        int bw1 = 0;
        int bw2 = 0;
        int bw3 = 0;
        L1Skills l1skills = null;
        for (int skillId = 1; skillId < 81; skillId++) {
            l1skills = SkillsTable.getInstance().getTemplate(skillId);
            String s1 = "魔法書(" + l1skills.getName() + ")";
            if (item.getItem().getName().equalsIgnoreCase(s1)) {
                int skillLevel = l1skills.getSkillLevel();
                int i7 = l1skills.getId();
                s = l1skills.getName();
                i = l1skills.getSkillId();
                switch (skillLevel) {
                    case 1:
                        level1 = i7;
                        break;
                    case 2:
                        level2 = i7;
                        break;
                    case 3:
                        l = i7;
                        break;
                    case 4:
                        i1 = i7;
                        break;
                    case 5:
                        j1 = i7;
                        break;
                    case 6:
                        k1 = i7;
                        break;
                    case 7:
                        l1 = i7;
                        break;
                    case 8:
                        i2 = i7;
                        break;
                    case 9:
                        j2 = i7;
                        break;
                    case 10:
                        k2 = i7;
                        break;
                    case 11:
                        l2 = i7;
                        break;
                    case 12:
                        i3 = i7;
                        break;
                    case 13:
                        j3 = i7;
                        break;
                    case 14:
                        k3 = i7;
                        break;
                    case 15:
                        l3 = i7;
                        break;
                    case 16:
                        i4 = i7;
                        break;
                    case 17:
                        j4 = i7;
                        break;
                    case 18:
                        k4 = i7;
                        break;
                    case 19:
                        l4 = i7;
                        break;
                    case 20:
                        i5 = i7;
                        break;
                    case 21:
                        j5 = i7;
                        break;
                    case 22:
                        k5 = i7;
                        break;
                    case 23:
                        l5 = i7;
                        break;
                    case 24:
                        i6 = i7;
                        break;
                    case 25:
                        dk3 = i7;
                        break;
                    case 26:
                        bw1 = i7;
                        break;
                    case 27:
                        bw2 = i7;
                        break;
                    case 28:
                        bw3 = i7;
                        break;
                }
            }
        }
        if (SkillsTable.getInstance().spellCheck(pc.getId(), i)) {
            pc.sendPackets(new S_SystemMessage("すでに学んだ魔法です。"), true);
            return;
        }
        int objid = pc.getId();
        pc.sendPackets(new S_AddSkill(level1, level2, l, i1, j1, k1, l1, i2, j2, k2, l2, i3, j3, k3, l3, i4, j4, k4, l4,
                i5, j5, k5, l5, i6, dk3, bw1, bw2, bw3, 0, 0, pc.getElfAttr()));
        S_SkillSound s_skillSound = new S_SkillSound(objid, isLawful ? 224 : 231);
        pc.sendPackets(s_skillSound);
        pc.broadcastPacket(s_skillSound);
        SkillsTable.getInstance().spellMastery(objid, i, s, 0, 0);
        pc.getInventory().removeItem(item, 1);
    }

    private void SpellBook1(L1PcInstance pc, L1ItemInstance l1iteminstance, GameClient clientthread) {
        String s = "";
        int i = 0;
        int j = 0;
        int k = 0;
        int l = 0;
        int i1 = 0;
        int j1 = 0;
        int k1 = 0;
        int l1 = 0;
        int i2 = 0;
        int j2 = 0;
        int k2 = 0;
        int l2 = 0;
        int i3 = 0;
        int j3 = 0;
        int k3 = 0;
        int l3 = 0;
        int i4 = 0;
        int j4 = 0;
        int k4 = 0;
        int l4 = 0;
        int i5 = 0;
        int j5 = 0;
        int k5 = 0;
        int l5 = 0;
        int i6 = 0;
        int dk3 = 0;
        int bw1 = 0;
        int bw2 = 0;
        int bw3 = 0;
        int de3 = 0;
        int passive = 0;
        L1Skills l1skills = null;
        for (int j6 = 97; j6 <= 241; j6++) {
            l1skills = SkillsTable.getInstance().getTemplate(j6);
            String s1 = "闇精霊の水晶(" + l1skills.getName() + ")";
            if (l1iteminstance.getItem().getName().equalsIgnoreCase(s1)) {
                int l6 = l1skills.getSkillLevel();
                int i7 = l1skills.getId();
                s = l1skills.getName();
                i = l1skills.getSkillId();
                switch (l6) {
                    case 1:
                        j = i7;
                        break;
                    case 2:
                        k = i7;
                        break;
                    case 3:
                        l = i7;
                        break;
                    case 4:
                        i1 = i7;
                        break;
                    case 5:
                        j1 = i7;
                        break;
                    case 6:
                        k1 = i7;
                        break;
                    case 7:
                        l1 = i7;
                        break;
                    case 8:
                        i2 = i7;
                        break;
                    case 9:
                        j2 = i7;
                        break;
                    case 10:
                        k2 = i7;
                        break;
                    case 11:
                        l2 = i7;
                        break;
                    case 12:
                        i3 = i7;
                        break;
                    case 13:
                        j3 = i7;
                        break;
                    case 14:
                        k3 = i7;
                        break;
                    case 15:
                        l3 = i7;
                        break;
                    case 16:
                        i4 = i7;
                        break;
                    case 17:
                        j4 = i7;
                        break;
                    case 18:
                        k4 = i7;
                        break;
                    case 19:
                        l4 = i7;
                        break;
                    case 20:
                        i5 = i7;
                        break;
                    case 21:
                        j5 = i7;
                        break;
                    case 22:
                        k5 = i7;
                        break;
                    case 23:
                        l5 = i7;
                        break;
                    case 24:
                        i6 = i7;
                        break;
                    case 25:
                        dk3 = i7;
                        break;
                    case 26:
                        bw1 = i7;
                        break;
                    case 27:
                        bw2 = i7;
                        break;
                    case 28:
                        bw3 = i7;
                        break;
                    case 29:
                        de3 = i7;
                        break;
                    case 30:
                        passive = i7;
                        break;
                }
            }
        }
        if (SkillsTable.getInstance().spellCheck(pc.getId(), i)) {
            pc.sendPackets(new S_SystemMessage("すでに学んだ魔法です。"), true);
            return;
        }
        if (pc.isDarkelf() && passive != 0) {
            pc.sendPackets(new S_ACTION_UI(146, passive));
        }
        int k6 = pc.getId();
        pc.sendPackets(new S_AddSkill(j, k, l, i1, j1, k1, l1, i2, j2, k2, l2, i3, j3, k3, l3, i4, j4, k4, l4, i5, j5,
                k5, l5, i6, dk3, bw1, bw2, bw3, 0, de3, pc.getElfAttr()));
        S_SkillSound s_skillSound = new S_SkillSound(k6, 231);
        pc.sendPackets(s_skillSound);
        Broadcaster.broadcastPacket(pc, s_skillSound);
        SkillsTable.getInstance().spellMastery(k6, i, s, 0, 0);
        pc.getInventory().removeItem(l1iteminstance, 1);
    }

    private void SpellBook2(L1PcInstance pc, L1ItemInstance l1iteminstance) {
        String s = "";
        int i = 0;
        int j = 0;
        int k = 0;
        int l = 0;
        int i1 = 0;
        int j1 = 0;
        int k1 = 0;
        int l1 = 0;
        int i2 = 0;
        int j2 = 0;
        int k2 = 0;
        int l2 = 0;
        int i3 = 0;
        int j3 = 0;
        int k3 = 0;
        int l3 = 0;
        int i4 = 0;
        int j4 = 0;
        int k4 = 0;
        int l4 = 0;
        int i5 = 0;
        int j5 = 0;
        int k5 = 0;
        int l5 = 0;
        int i6 = 0;
        int dk3 = 0;
        int bw1 = 0;
        int bw2 = 0;
        int bw3 = 0;
        L1Skills l1skills = null;
        for (int j6 = 129; j6 <= 176; j6++) {
            l1skills = SkillsTable.getInstance().getTemplate(j6);
            String s1 = "精霊の水晶(" + l1skills.getName() + ")";
            if (l1iteminstance.getItem().getName().equalsIgnoreCase(s1)) {
                if (!pc.isGm() && l1skills.getAttr() != 0 && pc.getElfAttr() != l1skills.getAttr()) {
                    if (pc.getElfAttr() == 0 || pc.getElfAttr() == 1 || pc.getElfAttr() == 2 || pc.getElfAttr() == 4
                            || pc.getElfAttr() == 8) { // 属性値が
                        // 奇妙な
                        // 場合は、
                        // 全属性を
                        // 覚え
                        // できるようにしておく
                        pc.sendPackets(new S_ServerMessage(79));
                        return;
                    }
                }
                int l6 = l1skills.getSkillLevel();
                int i7 = l1skills.getId();
                s = l1skills.getName();
                i = l1skills.getSkillId();
                switch (l6) {
                    case 1: // '\001'
                        j = i7;
                        break;

                    case 2: // '\002'
                        k = i7;
                        break;

                    case 3: // '\003'
                        l = i7;
                        break;

                    case 4: // '\004'
                        i1 = i7;
                        break;

                    case 5: // '\005'
                        j1 = i7;
                        break;

                    case 6: // '\006'
                        k1 = i7;
                        break;

                    case 7: // '\007'
                        l1 = i7;
                        break;

                    case 8: // '\b'
                        i2 = i7;
                        break;

                    case 9: // '\t'
                        j2 = i7;
                        break;

                    case 10: // '\n'
                        k2 = i7;
                        break;

                    case 11: // '\013'
                        l2 = i7;
                        break;

                    case 12: // '\f'
                        i3 = i7;
                        break;

                    case 13: // '\r'
                        j3 = i7;
                        break;

                    case 14: // '\016'
                        k3 = i7;
                        break;

                    case 15: // '\017'
                        l3 = i7;
                        break;

                    case 16: // '\020'
                        i4 = i7;
                        break;

                    case 17: // '\021'
                        j4 = i7;
                        break;

                    case 18: // '\022'
                        k4 = i7;
                        break;

                    case 19: // '\023'
                        l4 = i7;
                        break;

                    case 20: // '\024'
                        i5 = i7;
                        break;

                    case 21: // '\025'
                        j5 = i7;
                        break;

                    case 22: // '\026'
                        k5 = i7;
                        break;

                    case 23: // '\027'
                        l5 = i7;
                        break;

                    case 24: // '\030'
                        i6 = i7;
                        break;

                    case 25: // のナイト3段階の魔法
                        dk3 = i7;
                        break;

                    case 26: // イリュージョニスト1段階魔法
                        bw1 = i7;
                        break;

                    case 27: // イリュージョニスト2段階魔法
                        bw2 = i7;
                        break;

                    case 28: // イリュージョニスト3段階の魔法
                        bw3 = i7;
                        break;

                }
            }
        }
        if (SkillsTable.getInstance().spellCheck(pc.getId(), i)) {
            pc.sendPackets(new S_SystemMessage("すでに学んだ魔法です。"), true);
            return;
        }
        int k6 = pc.getId();
        pc.sendPackets(new S_AddSkill(j, k, l, i1, j1, k1, l1, i2, j2, k2, l2, i3, j3, k3, l3, i4, j4, k4, l4, i5, j5,
                k5, l5, i6, dk3, bw1, bw2, bw3, 0, 0, pc.getElfAttr()));
        S_SkillSound s_skillSound = new S_SkillSound(k6, 224);
        pc.sendPackets(s_skillSound);
        pc.broadcastPacket(s_skillSound);
        SkillsTable.getInstance().spellMastery(k6, i, s, 0, 0);
        pc.getInventory().removeItem(l1iteminstance, 1);
    }

    private void SpellBook3(L1PcInstance pc, L1ItemInstance l1iteminstance, GameClient clientthread) {
        String s = "";
        int i = 0;
        int j = 0;
        int k = 0;
        int l = 0;
        int i1 = 0;
        int j1 = 0;
        int k1 = 0;
        int l1 = 0;
        int i2 = 0;
        int j2 = 0;
        int k2 = 0;
        int l2 = 0;
        int i3 = 0;
        int j3 = 0;
        int k3 = 0;
        int l3 = 0;
        int i4 = 0;
        int j4 = 0;
        int k4 = 0;
        int l4 = 0;
        int i5 = 0;
        int j5 = 0;
        int k5 = 0;
        int l5 = 0;
        int i6 = 0;
        int dk3 = 0;
        int bw1 = 0;
        int bw2 = 0;
        int bw3 = 0;
        L1Skills l1skills = null;
        for (int j6 = 87; j6 <= 92; j6++) {
            l1skills = SkillsTable.getInstance().getTemplate(j6);
            String s1 = (new StringBuilder()).append("技術書(").append(l1skills.getName()).append(")").toString();
            if (l1iteminstance.getItem().getName().equalsIgnoreCase(s1)) {
                int l6 = l1skills.getSkillLevel();
                int i7 = l1skills.getId();
                s = l1skills.getName();
                i = l1skills.getSkillId();
                switch (l6) {
                    case 1: // '\001'
                        j = i7;
                        break;

                    case 2: // '\002'
                        k = i7;
                        break;

                    case 3: // '\003'
                        l = i7;
                        break;

                    case 4: // '\004'
                        i1 = i7;
                        break;

                    case 5: // '\005'
                        j1 = i7;
                        break;

                    case 6: // '\006'
                        k1 = i7;
                        break;

                    case 7: // '\007'
                        l1 = i7;
                        break;

                    case 8: // '\b'
                        i2 = i7;
                        break;

                    case 9: // '\t'
                        j2 = i7;
                        break;

                    case 10: // '\n'
                        k2 = i7;
                        break;

                    case 11: // '\013'
                        l2 = i7;
                        break;

                    case 12: // '\f'
                        i3 = i7;
                        break;

                    case 13: // '\r'
                        j3 = i7;
                        break;

                    case 14: // '\016'
                        k3 = i7;
                        break;

                    case 15: // '\017'
                        l3 = i7;
                        break;

                    case 16: // '\020'
                        i4 = i7;
                        break;

                    case 17: // '\021'
                        j4 = i7;
                        break;

                    case 18: // '\022'
                        k4 = i7;
                        break;

                    case 19: // '\023'
                        l4 = i7;
                        break;

                    case 20: // '\024'
                        i5 = i7;
                        break;

                    case 21: // '\025'
                        j5 = i7;
                        break;

                    case 22: // '\026'
                        k5 = i7;
                        break;

                    case 23: // '\027'
                        l5 = i7;
                        break;

                    case 24: // '\030'
                        i6 = i7;
                        break;

                    case 25: // のナイト3段階の魔法
                        dk3 = i7;
                        break;

                    case 26: // イリュージョニスト1段階魔法
                        bw1 = i7;
                        break;

                    case 27: // イリュージョニスト2段階魔法
                        bw2 = i7;
                        break;

                    case 28: // イリュージョニスト3段階の魔法
                        bw3 = i7;
                        break;

                }
            }
        }
        if (SkillsTable.getInstance().spellCheck(pc.getId(), i)) {
            pc.sendPackets(new S_SystemMessage("すでに学んだ魔法です。"), true);
            return;
        }
        int k6 = pc.getId();
        pc.sendPackets(new S_AddSkill(j, k, l, i1, j1, k1, l1, i2, j2, k2, l2, i3, j3, k3, l3, i4, j4, k4, l4, i5, j5,
                k5, l5, i6, dk3, bw1, bw2, bw3, 0, 0, pc.getElfAttr()));
        S_SkillSound s_skillSound = new S_SkillSound(k6, 224);
        pc.sendPackets(s_skillSound);
        pc.broadcastPacket(s_skillSound);
        SkillsTable.getInstance().spellMastery(k6, i, s, 0, 0);
        pc.getInventory().removeItem(l1iteminstance, 1);
    }

    private void SpellBook4(L1PcInstance pc, L1ItemInstance l1iteminstance, GameClient clientthread) {
        String s = "";
        int i = 0;
        int j = 0;
        int k = 0;
        int l = 0;
        int i1 = 0;
        int j1 = 0;
        int k1 = 0;
        int l1 = 0;
        int i2 = 0;
        int j2 = 0;
        int k2 = 0;
        int l2 = 0;
        int i3 = 0;
        int j3 = 0;
        int k3 = 0;
        int l3 = 0;
        int i4 = 0;
        int j4 = 0;
        int k4 = 0;
        int l4 = 0;
        int i5 = 0;
        int j5 = 0;
        int k5 = 0;
        int l5 = 0;
        int i6 = 0;
        int dk3 = 0;
        int bw1 = 0;
        int bw2 = 0;
        int bw3 = 0;
        L1Skills l1skills = null;
        for (int j6 = 113; j6 < 123; j6++) {
            l1skills = SkillsTable.getInstance().getTemplate(j6);
            String s1 = "魔法書(" + l1skills.getName() + ")";
            if (l1iteminstance.getItem().getName().equalsIgnoreCase(s1)) {
                int l6 = l1skills.getSkillLevel();
                int i7 = l1skills.getId();
                s = l1skills.getName();
                i = l1skills.getSkillId();
                switch (l6) {
                    case 1: // '\001'
                        j = i7;
                        break;

                    case 2: // '\002'
                        k = i7;
                        break;

                    case 3: // '\003'
                        l = i7;
                        break;

                    case 4: // '\004'
                        i1 = i7;
                        break;

                    case 5: // '\005'
                        j1 = i7;
                        break;

                    case 6: // '\006'
                        k1 = i7;
                        break;

                    case 7: // '\007'
                        l1 = i7;
                        break;

                    case 8: // '\b'
                        i2 = i7;
                        break;

                    case 9: // '\t'
                        j2 = i7;
                        break;

                    case 10: // '\n'
                        k2 = i7;
                        break;

                    case 11: // '\013'
                        l2 = i7;
                        break;

                    case 12: // '\f'
                        i3 = i7;
                        break;

                    case 13: // '\r'
                        j3 = i7;
                        break;

                    case 14: // '\016'
                        k3 = i7;
                        break;

                    case 15: // '\017'
                        l3 = i7;
                        break;

                    case 16: // '\020'
                        i4 = i7;
                        break;

                    case 17: // '\021'
                        j4 = i7;
                        break;

                    case 18: // '\022'
                        k4 = i7;
                        break;

                    case 19: // '\023'
                        l4 = i7;
                        break;

                    case 20: // '\024'
                        i5 = i7;
                        break;

                    case 21: // '\025'
                        j5 = i7;
                        break;

                    case 22: // '\026'
                        k5 = i7;
                        break;

                    case 23: // '\027'
                        l5 = i7;
                        break;

                    case 24: // '\030'
                        i6 = i7;
                        break;

                    case 25: // のナイト3段階の魔法
                        dk3 = i7;
                        break;

                    case 26: // イリュージョニスト1段階魔法
                        bw1 = i7;
                        break;

                    case 27: // イリュージョニスト2段階魔法
                        bw2 = i7;
                        break;

                    case 28: // イリュージョニスト3段階の魔法
                        bw3 = i7;
                        break;

                }
            }
        }
        if (SkillsTable.getInstance().spellCheck(pc.getId(), i)) {
            pc.sendPackets(new S_SystemMessage("すでに学んだ魔法です。"), true);
            return;
        }
        int k6 = pc.getId();
        pc.sendPackets(new S_AddSkill(j, k, l, i1, j1, k1, l1, i2, j2, k2, l2, i3, j3, k3, l3, i4, j4, k4, l4, i5, j5,
                k5, l5, i6, dk3, bw1, bw2, bw3, 0, 0, pc.getElfAttr()));
        S_SkillSound s_skillSound = new S_SkillSound(k6, 224);
        pc.sendPackets(s_skillSound);
        pc.broadcastPacket(s_skillSound);
        SkillsTable.getInstance().spellMastery(k6, i, s, 0, 0);
        pc.getInventory().removeItem(l1iteminstance, 1);
    }

    private void SpellBook5(L1PcInstance pc, L1ItemInstance l1iteminstance, GameClient clientthread) {
        String s = "";
        int i = 0;
        int j = 0;
        int k = 0;
        int l = 0;
        int i1 = 0;
        int j1 = 0;
        int k1 = 0;
        int l1 = 0;
        int i2 = 0;
        int j2 = 0;
        int k2 = 0;
        int l2 = 0;
        int i3 = 0;
        int j3 = 0;
        int k3 = 0;
        int l3 = 0;
        int i4 = 0;
        int j4 = 0;
        int k4 = 0;
        int l4 = 0;
        int i5 = 0;
        int j5 = 0;
        int k5 = 0;
        int l5 = 0;
        int i6 = 0;
        int dk3 = 0;
        int bw1 = 0;
        int bw2 = 0;
        int bw3 = 0;
        L1Skills l1skills = null;
        for (int j6 = 181; j6 < 200; j6++) {
            l1skills = SkillsTable.getInstance().getTemplate(j6);
            String s1 = "ドラゴンナイトの書板(" + l1skills.getName() + ")";
            if (l1iteminstance.getItem().getName().equalsIgnoreCase(s1)) {
                int l6 = l1skills.getSkillLevel();
                int i7 = l1skills.getId();
                s = l1skills.getName();
                i = l1skills.getSkillId();
                switch (l6) {
                    case 1: // '\001'
                        j = i7;
                        break;

                    case 2: // '\002'
                        k = i7;
                        break;

                    case 3: // '\003'
                        l = i7;
                        break;

                    case 4: // '\004'
                        i1 = i7;
                        break;

                    case 5: // '\005'
                        j1 = i7;
                        break;

                    case 6: // '\006'
                        k1 = i7;
                        break;

                    case 7: // '\007'
                        l1 = i7;
                        break;

                    case 8: // '\b'
                        i2 = i7;
                        break;

                    case 9: // '\t'
                        j2 = i7;
                        break;

                    case 10: // '\n'
                        k2 = i7;
                        break;

                    case 11: // '\013'
                        l2 = i7;
                        break;

                    case 12: // '\f'
                        i3 = i7;
                        break;

                    case 13: // '\r'
                        j3 = i7;
                        break;

                    case 14: // '\016'
                        k3 = i7;
                        break;

                    case 15: // '\017'
                        l3 = i7;
                        break;

                    case 16: // '\020'
                        i4 = i7;
                        break;

                    case 17: // '\021'
                        j4 = i7;
                        break;

                    case 18: // '\022'
                        k4 = i7;
                        break;

                    case 19: // '\023'
                        l4 = i7;
                        break;

                    case 20: // '\024'
                        i5 = i7;
                        break;

                    case 21: // '\025'
                        j5 = i7;
                        break;

                    case 22: // '\026'
                        k5 = i7;
                        break;

                    case 23: // '\027'
                        l5 = i7;
                        break;

                    case 24: // '\030'
                        i6 = i7;
                        break;

                    case 25: // のナイト3段階の魔法
                        dk3 = i7;
                        break;

                    case 26: // イリュージョニスト1段階魔法
                        bw1 = i7;
                        break;

                    case 27: // イリュージョニスト2段階魔法
                        bw2 = i7;
                        break;

                    case 28: // イリュージョニスト3段階の魔法
                        bw3 = i7;
                        break;

                }
            }
        }
        if (SkillsTable.getInstance().spellCheck(pc.getId(), i)) {
            pc.sendPackets(new S_SystemMessage("すでに学んだ魔法です。"), true);
            return;
        }
        int k6 = pc.getId();
        pc.sendPackets(new S_AddSkill(j, k, l, i1, j1, k1, l1, i2, j2, k2, l2, i3, j3, k3, l3, i4, j4, k4, l4, i5, j5,
                k5, l5, i6, dk3, bw1, bw2, bw3, 0, 0, pc.getElfAttr()));
        S_SkillSound s_skillSound = new S_SkillSound(k6, 224);
        pc.sendPackets(s_skillSound);
        pc.broadcastPacket(s_skillSound);
        SkillsTable.getInstance().spellMastery(k6, i, s, 0, 0);
        pc.getInventory().removeItem(l1iteminstance, 1);
    }

    private void SpellBook6(L1PcInstance pc, L1ItemInstance l1iteminstance, GameClient clientthread) {
        String s = "";
        int i = 0;
        int j = 0;
        int k = 0;
        int l = 0;
        int i1 = 0;
        int j1 = 0;
        int k1 = 0;
        int l1 = 0;
        int i2 = 0;
        int j2 = 0;
        int k2 = 0;
        int l2 = 0;
        int i3 = 0;
        int j3 = 0;
        int k3 = 0;
        int l3 = 0;
        int i4 = 0;
        int j4 = 0;
        int k4 = 0;
        int l4 = 0;
        int i5 = 0;
        int j5 = 0;
        int k5 = 0;
        int l5 = 0;
        int i6 = 0;
        int dk3 = 0;
        int bw1 = 0;
        int bw2 = 0;
        int bw3 = 0;
        for (int j6 = 201; j6 < 224; j6++) {
            L1Skills l1skills = SkillsTable.getInstance().getTemplate(j6);
            String s1 = "記憶の水晶(" + l1skills.getName() + ")";
            if (l1iteminstance.getItem().getName().equalsIgnoreCase(s1)) {
                int l6 = l1skills.getSkillLevel();
                int i7 = l1skills.getId();
                s = l1skills.getName();
                i = l1skills.getSkillId();
                switch (l6) {
                    case 1: // '\001'
                        j = i7;
                        break;

                    case 2: // '\002'
                        k = i7;
                        break;

                    case 3: // '\003'
                        l = i7;
                        break;

                    case 4: // '\004'
                        i1 = i7;
                        break;

                    case 5: // '\005'
                        j1 = i7;
                        break;

                    case 6: // '\006'
                        k1 = i7;
                        break;

                    case 7: // '\007'
                        l1 = i7;
                        break;

                    case 8: // '\b'
                        i2 = i7;
                        break;

                    case 9: // '\t'
                        j2 = i7;
                        break;

                    case 10: // '\n'
                        k2 = i7;
                        break;

                    case 11: // '\013'
                        l2 = i7;
                        break;

                    case 12: // '\f'
                        i3 = i7;
                        break;

                    case 13: // '\r'
                        j3 = i7;
                        break;

                    case 14: // '\016'
                        k3 = i7;
                        break;

                    case 15: // '\017'
                        l3 = i7;
                        break;

                    case 16: // '\020'
                        i4 = i7;
                        break;

                    case 17: // '\021'
                        j4 = i7;
                        break;

                    case 18: // '\022'
                        k4 = i7;
                        break;

                    case 19: // '\023'
                        l4 = i7;
                        break;

                    case 20: // '\024'
                        i5 = i7;
                        break;

                    case 21: // '\025'
                        j5 = i7;
                        break;

                    case 22: // '\026'
                        k5 = i7;
                        break;

                    case 23: // '\027'
                        l5 = i7;
                        break;

                    case 24: // '\030'
                        i6 = i7;
                        break;

                    case 25: // のナイト3段階の魔法
                        dk3 = i7;
                        break;

                    case 26: // イリュージョニスト1段階魔法
                        bw1 = i7;
                        break;

                    case 27: // イリュージョニスト2段階魔法
                        bw2 = i7;
                        break;

                    case 28: // イリュージョニスト3段階の魔法
                        bw3 = i7;
                        break;

                }
            }
        }
        if (SkillsTable.getInstance().spellCheck(pc.getId(), i)) {
            pc.sendPackets(new S_SystemMessage("すでに学んだ魔法です。"), true);
            return;
        }
        int k6 = pc.getId();
        pc.sendPackets(new S_AddSkill(j, k, l, i1, j1, k1, l1, i2, j2, k2, l2, i3, j3, k3, l3, i4, j4, k4, l4, i5, j5,
                k5, l5, i6, dk3, bw1, bw2, bw3, 0, 0, pc.getElfAttr()));
        S_SkillSound s_skillSound = new S_SkillSound(k6, 224);
        pc.sendPackets(s_skillSound);
        pc.broadcastPacket(s_skillSound);
        SkillsTable.getInstance().spellMastery(k6, i, s, 0, 0);
        pc.getInventory().removeItem(l1iteminstance, 1);
    }

    private boolean doWandAction(L1PcInstance user, L1Object target, int itemObjectId) {
        // if (user.getId() == target.getId()) {
        // return false; //自分自身に当たった
        // }
        if (user.glanceCheck(target.getX(), target.getY()) == false) {
            return false; // 直線上に障害物がある
        }
        if (user.getAcceleratorChecker().isAccelerated(AcceleratorChecker.ACT_TYPE.ATTACK, 1000)) {
            return false;
        }

        // XXX 適切なダメージ計算、ポイント修正

        int dmgAdd = 8;
        L1ItemInstance l1iteminstance = user.getInventory().getItem(itemObjectId);
        int itemId = l1iteminstance.getItem().getItemId();
        int effect = 0;
        if (itemId == 40007)
            effect = 10;
        else if (itemId == 40006 || itemId == 140006)
            effect = 6598;
        if (target instanceof L1PcInstance) {
            // if (user.getLevel() > ((L1PcInstance) target).getLevel()) {
            // dmgAdd = 8 + (user.getLevel() - ((L1PcInstance)
            // target).getLevel()) / 2;
            dmgAdd = 60 - (((L1PcInstance) target).getMr() / 10);
            // if (dmgAdd > 15) {
            // dmgAdd = 15;
            // }
            // }

        } else {
            dmgAdd = 10 + user.getLevel() / 4;
        }
        int dmg = dmgAdd + _random.nextInt(5);
        if (itemId == 40007) {
            dmg = dmgAdd / 6 + _random.nextInt(5);
            effect = 10;
        } else if (itemId == 40006 || itemId == 140006) {
            dmg = dmgAdd / 2 + _random.nextInt(5);
            effect = 6598;
        }

        // if (target instanceof L1PcInstance) {
        // L1PcInstance pc = (L1PcInstance) target;
        // if(pc.getDamageReductionByArmor() != 0){
        // dmg -= pc.getDamageReductionByArmor();
        // }
        // }
        // dmg = Math.max(1, dmg);

        if (target instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) target;

            if (pc.hasSkillEffect(COUNTER_MAGIC)) {
                pc.removeSkillEffect(COUNTER_MAGIC);
                pc.sendPackets(new S_PacketBox(S_PacketBox.UNLIMITED_ICON1, 393, false));
                pc.sendPackets(new S_SkillSound(pc.getId(), 10702));
                pc.broadcastPacket(new S_SkillSound(pc.getId(), 10702));
            }
            if (pc.hasSkillEffect(ERASE_MAGIC)) {
                pc.removeSkillEffect(ERASE_MAGIC);
                pc.sendPackets(new S_PacketBox(S_PacketBox.UNLIMITED_ICON1, 55, false));
            }

            if (pc.getMap().isSafetyZone(pc.getLocation()) || user.checkNonPvP(user, pc)) {
                // 攻撃することはできないジョン
                return false;
            }
            if (user.getZoneType() == 1 && pc.getZoneType() == 0) { // セーフティゾーンでノーマルゾーンx
                return false;
            }
            if (user.getZoneType() == 1 && pc.getZoneType() == -1) { // セーフティゾーンでコンバットゾーンx
                return false;
            }
            if (pc.hasSkillEffect(50) == true || pc.hasSkillEffect(78) == true || pc.hasSkillEffect(157) == true) {
                // ターゲットがアイスランス、アブ小ガルト、バリアアスバインド状態
                return false;
            }

            if (dmg > 0) {
                user.sendPackets(new S_UseAttackSkill(user, pc.getId(), effect, pc.getX(), pc.getY(), 17));
                Broadcaster.broadcastPacket(user,
                        new S_UseAttackSkill(user, pc.getId(), effect, pc.getX(), pc.getY(), 17));
                pc.receiveDamage(user, dmg);
            } else {
                user.sendPackets(new S_UseAttackSkill(user, pc.getId(), effect, pc.getX(), pc.getY(), 17, 0));
                Broadcaster.broadcastPacket(user,
                        new S_UseAttackSkill(user, pc.getId(), effect, pc.getX(), pc.getY(), 17, 0));
            }
            L1PinkName.onAction(pc, user);

        } else if (target instanceof L1MonsterInstance) {
            L1MonsterInstance mob = (L1MonsterInstance) target;
            if (mob.getKarma() > 0) {
                return false;
            }

            user.sendPackets(new S_UseAttackSkill(user, mob.getId(), effect, mob.getX(), mob.getY(), 17));
            Broadcaster.broadcastPacket(user,
                    new S_UseAttackSkill(user, mob.getId(), effect, mob.getX(), mob.getY(), 17));
            dmg = Math.max(1, dmg);
            mob.receiveDamage(user, dmg);
        } else if (target instanceof L1NpcInstance) {
            L1NpcInstance npc = (L1NpcInstance) target;
            user.sendPackets(new S_UseAttackSkill(user, npc.getId(), effect, npc.getX(), npc.getY(), 17));
            Broadcaster.broadcastPacket(user,
                    new S_UseAttackSkill(user, npc.getId(), effect, npc.getX(), npc.getY(), 17));
        }

        return true;
    }

    private void polyAction(L1PcInstance attacker, L1Character cha, int itemId, String s) {
        boolean isSameClan = false;
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            if (pc.getClanid() != 0 && attacker.getClanid() == pc.getClanid()) {
                isSameClan = true;
            }
        }
        if (cha instanceof L1MonsterInstance) {
            return;
        }
        if (attacker.getId() != cha.getId() && !isSameClan) {
            int probability = 3 * (attacker.getLevel() - cha.getLevel()) - cha.getResistance().getEffectedMrBySkill();
            int rnd = _random.nextInt(100) + 1;
            if (rnd > probability) {
                return;
            }
        }

        int[] polyArray = { 29, 979, 1037, 1039, 3860, 3861, 3862, 3863, 3864, 3865, 3904, 3906, 95, 146, 2374, 2376,
                2377, 2378, 3866, 3867, 3868, 3869, 3870, 3871, 3872, 3873, 3874, 3875, 3876, 3882, 3883, 3884, 3885,
                11358, 11396, 11397, 12225, 12226, 11399, 11398, 12227 }; // 紅葉バー
        // リニューアルで
        // 追加

        int pid = _random.nextInt(polyArray.length);
        int polyId = polyArray[pid];

        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            if (pc.getInventory().checkEquipped(20281)) {
                if (usePolyScroll(pc, itemId, s)) {
                    //
                } else {
                    pc.sendPackets(new S_ServerMessage(181));
                }
            } else {
                L1Skills skillTemp = SkillsTable.getInstance().getTemplate(SHAPE_CHANGE);
                L1PolyMorph.doPoly(pc, polyId, skillTemp.getBuffDuration(), L1PolyMorph.MORPH_BY_ITEMMAGIC);
                if (attacker.getId() != pc.getId()) {
                    pc.sendPackets(new S_ServerMessage(241, attacker.getName())); // 誰かがあなたを変身させました。
                }
            }
        } else if (cha instanceof L1MonsterInstance) {
            L1MonsterInstance mob = (L1MonsterInstance) cha;
            if (mob.getLevel() < 50) {
                int npcId = mob.getNpcTemplate().get_npcId();
                if (npcId != 45338 && npcId != 45370 && npcId != 45456 && npcId != 45464 && npcId != 45473
                        && npcId != 45488 && npcId != 45497 && npcId != 45516 && npcId != 45529 && npcId != 45458) {
                    L1Skills skillTemp = SkillsTable.getInstance().getTemplate(SHAPE_CHANGE);
                    L1PolyMorph.doPoly(mob, polyId, skillTemp.getBuffDuration(), L1PolyMorph.MORPH_BY_ITEMMAGIC);
                }
            }
        }
    }

    private void cancelAbsoluteBarrier(L1PcInstance pc) { // アブ小ガルトバリアの解除
        if (pc.hasSkillEffect(ABSOLUTE_BARRIER)) {
            pc.killSkillEffectTimer(ABSOLUTE_BARRIER);
            pc.startMpRegenerationByDoll();
        }
    }

    private void useToiTeleportAmulet(L1PcInstance pc, int itemId, L1ItemInstance item) {
        boolean isTeleport = false;
	/*
	 * if (itemId == 40289 || itemId == 40293) { // 11,51Famulet if
	 * (pc.getX() >= 32816 && pc.getX() <= 32821 && pc.getY() >= 32778 &&
	 * pc.getY() <= 32783 && pc.getMapId() == 101) { isTeleport = true; } }
	 * else if (itemId == 40290 || itemId == 40294) { // 21,61Famulet if
	 * (pc.getX() >= 32815 && pc.getX() <= 32820 && pc.getY() >= 32815 &&
	 * pc.getY() <= 32820 && pc.getMapId() == 101) { isTeleport = true; } }
	 * else if (itemId == 40291 || itemId == 40295) { // 31,71Famulet if
	 * (pc.getX() >= 32779 && pc.getX() <= 32784 && pc.getY() >= 32778 &&
	 * pc.getY() <= 32783 && pc.getMapId() == 101) { isTeleport = true; } }
	 * else if (itemId == 40292 || itemId == 40296) { // 41,81Famulet if
	 * (pc.getX() >= 32779 && pc.getX() <= 32784 && pc.getY() >= 32815 &&
	 * pc.getY() <= 32820 && pc.getMapId() == 101) { isTeleport = true; } }
	 * else if (itemId == 40297) { // 91Famulet if (pc.getX() >= 32706 &&
	 * pc.getX() <= 32710 && pc.getY() >= 32909 && pc.getY() <= 32913 &&
	 * pc.getMapId() == 190) { isTeleport = true; } }
	 */
        /** 傲慢不適切帰還地域除く任意の場所を使用されるように **/
        if (itemId >= 40289 && itemId <= 40297) {
            if (pc.getMap().isEscapable()) {
                isTeleport = true;
            }
        }

        if (isTeleport) {
            new L1Teleport().teleport(pc, item.getItem().get_locx(), item.getItem().get_locy(),
                    item.getItem().get_mapid(), 5, true);
        } else {
            pc.sendPackets(new S_ServerMessage(79));
            // \f1 何も起こらなかった。
        }
    }

    private boolean withdrawPet(L1PcInstance pc, int itemObjectId) {
        if (!pc.getMap().isTakePets()) {
            pc.sendPackets(new S_ServerMessage(563)); // \f1ここで​​は使用できません。
            return false;
        }

        if (pc.getMapId() == 781 || pc.getMapId() == 782) {
            pc.sendPackets(new S_ServerMessage(563)); // \f1ここで​​は使用できません。
            return false;
        }

        if (pc.isInWarArea()) {
            pc.sendPackets(new S_ServerMessage(563)); // \f1ここで​​は使用できません。
            return false;
        }

        int petCost = 0;
        Object[] petList = pc.getPetList().values().toArray();
        for (Object pet : petList) {
            if (pet instanceof L1PetInstance) {
                if (((L1PetInstance) pet).getItemObjId() == itemObjectId) { // すでに取り出しているペット
                    return false;
                }
            }
            petCost += ((L1NpcInstance) pet).getPetcost();
        }
        int charisma = pc.getAbility().getTotalCha();
        if (pc.isCrown()) { // CROWN
            charisma += 6;
        } else if (pc.isElf()) { // ELF
            charisma += 12;
        } else if (pc.isWizard()) { // WIZ
            charisma += 6;
        } else if (pc.isDarkelf()) { // DE
            charisma += 6;
        } else if (pc.isDragonknight()) { // 竜騎士
            charisma += 6;
        } else if (pc.isBlackwizard()) { // イリュージョニスト
            charisma += 6;
        }

        charisma -= petCost;
        int petCount = charisma / 6;
        if (petCount <= 0) {
            pc.sendPackets(new S_ServerMessage(489)); // 退いていこうとするペットが多すぎます。
            return false;
        }

        L1Pet l1pet = PetTable.getInstance().getTemplate(itemObjectId);
        if (l1pet != null) {
            L1Npc npcTemp = NpcTable.getInstance().getTemplate(l1pet.get_npcid());
            L1PetInstance pet = new L1PetInstance(npcTemp, pc, l1pet);
            pet.setPetcost(6);
        }
        return true;
    }

    private void useResolvent(L1PcInstance pc, L1ItemInstance item, L1ItemInstance resolvent) {
        if (item == null || resolvent == null) {
            pc.sendPackets(new S_ServerMessage(79)); // \f1何も起こらなかった。
            return;
        }
        if (item.getItem().getType2() == 1 || item.getItem().getType2() == 2) { // 武器・防具
            if (item.getEnchantLevel() != 0) { // 強化済み
                pc.sendPackets(new S_ServerMessage(1161)); // 溶解することはできません。
                return;
            }
            if (item.isEquipped()) { // 機器の
                pc.sendPackets(new S_ServerMessage(1161)); // 溶解することはできません。
                return;
            }
            if (item.getBless() >= 128) { // 封印中
                pc.sendPackets(new S_ServerMessage(1161)); // 溶解することはできません。
                return;
            }
        }
        int crystalCount = ResolventTable.getInstance().getCrystalCount(item.getItem().getItemId());
        if (crystalCount == 0) {
            pc.sendPackets(new S_ServerMessage(1161)); // 溶解することはできません。
            return;
        }

        int rnd = _random.nextInt(100) + 1;
        if (rnd >= 1 && rnd <= 20) {
            crystalCount = 0;
            pc.sendPackets(new S_ServerMessage(158, item.getName())); // \f1%0が蒸発していなくなりました。
        } else if (rnd >= 21 && rnd <= 90) {
            crystalCount *= 1;
        } else if (rnd >= 91 && rnd <= 100) {
            crystalCount *= 1.5;
            pc.getInventory().storeItem(41246, (int) (crystalCount * 1.5));
        }
        if (crystalCount != 0) {
            L1ItemInstance crystal = ItemTable.getInstance().createItem(41246);
            crystal.setCount(crystalCount);
            if (pc.getInventory().checkAddItem(crystal, 1) == L1Inventory.OK) {
                pc.getInventory().storeItem(crystal);
                pc.sendPackets(new S_ServerMessage(403, crystal.getLogName())); // %0を手に入れました。
            } else { // 持つことができない場合は、地面に落とす処理のキャンセルはしない（不正防止）
                L1World.getInstance().getInventory(pc.getX(), pc.getY(), pc.getMapId()).storeItem(crystal);
            }
        }
        pc.getInventory().removeItem(item, 1);
        pc.getInventory().removeItem(resolvent, 1);
    }

    private void useMagicDoll(L1PcInstance pc, int itemId, int itemObjectId) {
        if (pc.isInvisble()) {
            return;
        }
        if (pc.isInWarArea()) {
            pc.sendPackets(new S_ServerMessage(563)); // \f1ここでは、使用することができません。
            return;
        }
        long curtime = System.currentTimeMillis() / 1000;
        if (pc.getQuizTime3() + 3 > curtime) {
            return;
        }
        boolean isAppear = true;
        if (pc.getDollList().size() > 0) {
            for (L1DollInstance dollObject : pc.getDollList()) {
                dollObject.deleteDoll();
                pc.sendPackets(new S_SkillIconGFX(56, 0));
                pc.sendPackets(new S_OwnCharStatus(pc));
                if (dollObject.getItemObjId() == itemObjectId) {
                    isAppear = false;
                }
            }
        }

        if (isAppear) {
            L1DollInstance doll = null;
            if (itemId == 210106 || itemId == 210107 || itemId == 210108 || itemId == 210109) {
                if (!pc.getInventory().checkItem(41246, 500)) {
                    pc.sendPackets(new S_ServerMessage(337, "$5240"));
                    return;
                }
            } else {
                if (!pc.getInventory().checkItem(41246, 50)) {
                    pc.sendPackets(new S_ServerMessage(337, "$5240"));
                    return;
                }
            }
            if (pc.getDollListSize() >= Config.MAX_DOLL_COUNT) {
                // \f1これ以上のmonsterを操縦することができません。
                pc.sendPackets(new S_ServerMessage(319));
                return;
            }

            int npcId = 0;
            int dollType = 0;
            switch (itemId) {
                case 740:
                    npcId = 507;
                    dollType = L1DollInstance.DOLLTYPE_MOKAK;
                    pc.sendPackets(new S_ChatPacket(pc, "マジックドール：最大HP + 50"));
                    break;
                case 741:
                    npcId = 508;
                    dollType = L1DollInstance.DOLLTYPE_LAVAGOLREM;
                    pc.sendPackets(new S_ChatPacket(pc, "近距離ダメージ+1、ダメージリダクション+1"));
                    break;
                case 742:
                    npcId = 509;
                    dollType = L1DollInstance.DOLLTYPE_DIAMONDGOLREM;
                    pc.sendPackets(new S_ChatPacket(pc, "ダメージリダクション+2"));
                    break;
                case 743:
                    npcId = 510;
                    dollType = L1DollInstance.DOLLTYPE_NIGHTBALD;
                    pc.sendPackets(new S_ChatPacket(pc, "近距離ダメージ+2、近距離命中+2、スタンレベル上昇+1"));
                    break;
                case 744:
                    npcId = 511;
                    dollType = L1DollInstance.DOLLTYPE_SIER;
                    pc.sendPackets(new S_ChatPacket(pc, "遠距離ダメージ+5、32秒ごとにHP回復+30"));
                    break;
                case 745:
                    npcId = 512;
                    dollType = L1DollInstance.DOLLTYPE_DEMON;
                    pc.sendPackets(new S_ChatPacket(pc, "スタン耐性+12、スタンレベル上昇+2"));
                    break;
                case 746:
                    npcId = 513;
                    dollType = L1DollInstance.DOLLTYPE_DEATHNIGHT;// デスナイト人形ジンツン
                    pc.sendPackets(new S_ChatPacket(pc, "ダメージリダクション+5、経験値20％追加獲得、魔法発動（ヘルファイア）"));
                    break;
                case 750:
                    npcId = 900233;
                    dollType = L1DollInstance.DOLLTYPE_SNOWMAN_NEW;
                    pc.sendPackets(new S_ChatPacket(pc, "近距離ダメージ+1、近距離命中+1"));
                    break;
                case 41248:
                    npcId = 80106;
                    dollType = L1DollInstance.DOLLTYPE_BUGBEAR;
                    pc.sendPackets(new S_ChatPacket(pc, "重量ゲージ+ 10％増加"));// バグベア
                    break;
                case 41249:
                    npcId = 80107;
                    dollType = L1DollInstance.DOLLTYPE_SUCCUBUS;
                    pc.sendPackets(new S_ChatPacket(pc, "64秒ごとにMP 15回復"));// サキュバス
                    break;
                case 41250:
                    npcId = 80108;
                    dollType = L1DollInstance.DOLLTYPE_WAREWOLF;
                    pc.sendPackets(new S_ChatPacket(pc, "近距離攻撃時、一定確率で追加ダメージ+15"));// 狼
                    break;
                case 210071:
                    npcId = 200018;
                    dollType = L1DollInstance.DOLLTYPE_ELDER;
                    pc.sendPackets(new S_ChatPacket(pc, "64秒ごとにMP 15回復"));// 長老
                    break;
                case 210072:
                    npcId = 200019;
                    dollType = L1DollInstance.DOLLTYPE_CRUSTACEA;
                    pc.sendPackets(new S_ChatPacket(pc, "近距離攻撃時、一定確率で追加ダメージ+15"));// シアン
                    break;
                case 210070:
                    npcId = 200017;
                    dollType = L1DollInstance.DOLLTYPE_STONEGOLEM;
                    pc.sendPackets(new S_ChatPacket(pc, "ダメージリダクション+1"));// ストーンゴーレム
                    break;
                case 210086:
                    npcId = 200068;
                    dollType = L1DollInstance.DOLLTYPE_SEADANCER;
                    pc.sendPackets(new S_ChatPacket(pc, "32秒ごとにHPを25ずつ回復"));// シーダンサー
                    break;
                case 210096:
                    npcId = 200074;
                    dollType = L1DollInstance.DOLLTYPE_SNOWMAN;
                    pc.sendPackets(new S_ChatPacket(pc, "AC-3、凍結耐性+7"));// エティ
                    break;
                case 210105:
                    npcId = 200012;
                    dollType = L1DollInstance.DOLLTYPE_COCA;
                    pc.sendPackets(new S_ChatPacket(pc, "遠距離ダメージ+1、遠距離命中+1"));// コカ
                    break;
                case 210106:
                    npcId = 200008;
                    dollType = L1DollInstance.DOLLTYPE_HATCHLING;
                    pc.sendPackets(new S_ChatPacket(pc, "MP回復+5、重ゲージ+ 10％"));
                    break;
                case 210107:
                    npcId = 200009;
                    dollType = L1DollInstance.DOLLTYPE_HATCHLING;
                    pc.sendPackets(new S_ChatPacket(pc, "MP回復+5、重ゲージ+ 10％"));
                    break;
                case 210108:
                    npcId = 200010;
                    dollType = L1DollInstance.DOLLTYPE_HATCHLING;
                    pc.sendPackets(new S_ChatPacket(pc, "MP回復+5、重ゲージ+ 10％"));
                    break;
                case 210109:
                    npcId = 200011;
                    dollType = L1DollInstance.DOLLTYPE_HATCHLING;
                    pc.sendPackets(new S_ChatPacket(pc, "MP回復+5、重ゲージ+ 10％"));
                    break;
                case 500212:
                    npcId = 900176;
                    dollType = L1DollInstance.DOLLTYPE_COBO;
                    pc.sendPackets(new S_ChatPacket(pc, "ツタ+ 3、命中+ 3、SP + 3、スタン耐性+10、リドク+ 10、HP + 200、MP + 50"));// ヘルパー
                    break;
                case 500213:
                    npcId = 900178;
                    dollType = L1DollInstance.DOLLTYPE_ETHYNE;
                    pc.sendPackets(new S_ChatPacket(pc, "ヘイスト、AC-2、ホールド耐性+10"));// エティ
                    break;
                case 500214:
                    npcId = 900179;
                    dollType = L1DollInstance.DOLLTYPE_SKELETON;
                    pc.sendPackets(new S_ChatPacket(pc, "近距離ダメージ+2、スタン耐性+10"));// スパ
                    break;
                case 500215:
                    npcId = 900180;
                    dollType = L1DollInstance.DOLLTYPE_SCARECROW;
                    pc.sendPackets(new S_ChatPacket(pc, "遠距離命中+2、近距離命中+2、HP + 50、MP + 30"));// かかし
                    break;
                case 447012:
                    npcId = 900220;
                    dollType = L1DollInstance.DOLLTYPE_PSY_CHAMPION;
                    pc.sendPackets(new S_ChatPacket(pc, "近距離ダメージ+2、HP + 30、64秒ごとにMP回復+15"));
                    break;
                case 447013:
                    npcId = 900221;
                    dollType = L1DollInstance.DOLLTYPE_PSY_BIRD;
                    pc.sendPackets(new S_ChatPacket(pc, "遠距離ダメージ+2、HP + 30、64秒ごとにMP回復+15"));
                    break;
                case 447014:
                    npcId = 900222;
                    dollType = L1DollInstance.DOLLTYPE_PSY_GANGNAM_STYLE;
                    pc.sendPackets(new S_ChatPacket(pc, "SP + 1、HP + 30、64秒ごとにMP回復+15"));
                    break;
                case 30022:
                    npcId = 5074;
                    dollType = L1DollInstance.DOLLTYPE_PIXIE_BLAG;
                    pc.sendPackets(new S_ChatPacket(pc, "ダメージ+2、SP + 1、一定確率で水属性攻撃魔法発動"));
                    break;
                case 30023:
                    npcId = 5075;
                    dollType = L1DollInstance.DOLLTYPE_PIXIE_LESDAG;
                    pc.sendPackets(new S_ChatPacket(pc, "ダメージ+2、SP + 1、一定確率で火属性攻撃魔法発動"));
                    break;
                case 30024:
                    npcId = 5076;
                    dollType = L1DollInstance.DOLLTYPE_PIXIE_ELREGEU;
                    pc.sendPackets(new S_ChatPacket(pc, "ダメージ+2、SP + 1、一定確率で地属性攻撃魔法発動"));
                    break;
                case 30025:
                    npcId = 5077;
                    dollType = L1DollInstance.DOLLTYPE_PIXIE_GREG;
                    pc.sendPackets(new S_ChatPacket(pc, "ダメージ+2、SP + 1、一定確率で風属性攻撃魔法発動"));
                    break;
                case 447015:
                    npcId = 900223;
                    dollType = L1DollInstance.DOLLTYPE_GREMLIN;
                    pc.sendPackets(new S_ChatPacket(pc, "ダメージ+2、SP + 1、HP + 30、64秒ごとにMP 10回復"));
                    break;
                case 447016:
                    npcId = 900224;
                    dollType = L1DollInstance.DOLLTYPE_LICH;
                    pc.sendPackets(new S_ChatPacket(pc, "SP+2, HP+80"));
                    break;
                case 447017:
                    npcId = 900225;
                    dollType = L1DollInstance.DOLLTYPE_DRAKE;
                    pc.sendPackets(new S_ChatPacket(pc, "遠距離ダメージ+2、64秒ごとにMP回復+6"));
                    break;
                case 510216:
                    npcId = 900226;
                    dollType = L1DollInstance.DOLLTYPE_SNOWMAN_A;
                    pc.sendPackets(new S_ChatPacket(pc, "経験値10％追加獲得、遠距離命中+5"));
                    break;
                case 510217:
                    npcId = 900227;
                    dollType = L1DollInstance.DOLLTYPE_SNOWMAN_B;
                    pc.sendPackets(new S_ChatPacket(pc, "経験値10％追加獲得、64秒ごとにMP 15回復"));
                    break;
                case 510218:
                    npcId = 900228;
                    dollType = L1DollInstance.DOLLTYPE_SNOWMAN_C;
                    pc.sendPackets(new S_ChatPacket(pc, "経験値10％追加獲得、HP回復+ 15"));
                    break;
                case 510219:
                    npcId = 900229;
                    dollType = L1DollInstance.DOLLTYPE_GIANT;
                    pc.sendPackets(new S_ChatPacket(pc, "経験値10％追加獲得、ダメージリダクション+1"));
                    break;
                case 510220:
                    npcId = 900230;
                    dollType = L1DollInstance.DOLLTYPE_CYCLOPS;
                    pc.sendPackets(new S_ChatPacket(pc, "スタン耐性+12、近距離ダメージ+2、近距離命中+2"));
                    break;
                case 510221:
                    npcId = 900231;
                    dollType = L1DollInstance.DOLLTYPE_BLACK_ELDER;
                    pc.sendPackets(new S_ChatPacket(pc, "64秒ごとにMP回復+15、一定確率でコールライトニング発動"));
                    break;
                case 510222:
                    npcId = 900232;
                    dollType = L1DollInstance.DOLLTYPE_SUCCUBUS_2;
                    pc.sendPackets(new S_ChatPacket(pc, "SP + 1、64秒ごとにMP回復+15"));
                    break;
                case 410171:// ジンテン
                    npcId = 513;
                    dollType = L1DollInstance.DOLLTYPE_DEATH_KNIGHT;
                    pc.sendPackets(new S_ChatPacket(pc, "ダメージリダクション+5、経験値20％追加獲得、魔法発動（ヘルファイア）"));
                    break;
                case 3000150:// バポ
                    npcId = 7310200;
                    dollType = L1DollInstance.DOLLTYPE_BAPHOMET;
                    pc.sendPackets(new S_ChatPacket(pc, "積極的なの+5、スタン耐性+10"));
                    break;
                case 3000151:// オルニョ
                    npcId = 7310201;
                    dollType = L1DollInstance.DOLLTYPE_ICE_QUEEN;
                    pc.sendPackets(new S_ChatPacket(pc, "遠距離ダメージ+5、遠距離命中+5、スタン耐性+10"));
                    break;
                case 3000152:// カーツ
                    npcId = 7310202;
                    dollType = L1DollInstance.DOLLTYPE_KURTS;
                    pc.sendPackets(new S_ChatPacket(pc, "AC-2、ポースレイヤーステップダメージ+10、ダメージリダクション+3、スタン耐性+10"));
                    break;
                case 410172:
                    npcId = 81212;
                    dollType = L1DollInstance.DOLLTYPE_MERMAID;
                    pc.sendPackets(new S_ChatPacket(pc, "経験値3％追加獲得"));
                    break;
                case 410173:
                    npcId = 81213;
                    dollType = L1DollInstance.DOLLTYPE_KBB;
                    pc.sendPackets(new S_ChatPacket(pc, "スタン耐性+8、64秒ごとにMP回復+10"));
                    break;
                case 3000086:
                    npcId = 7310082;
                    dollType = L1DollInstance.DOLL_Iris;// アイリス
                    pc.sendPackets(new S_ChatPacket(pc, "ポースレイヤーステップダメージ+10、ダメージリダクション+3魔法発動"));
                    break;
                case 3000087:
                    npcId = 7310083;
                    dollType = L1DollInstance.DOLL_vampire;// ヴァンパイア
                    pc.sendPackets(new S_ChatPacket(pc, "タイタン系技術発動HP区間5％増加、近距離ダメージ+2、近距離命中+2魔法発動"));
                    break;
                case 3000088:
                    npcId = 7310084;
                    dollType = L1DollInstance.DOLL_barranca;// バランカ
                    pc.sendPackets(new S_ChatPacket(pc, "アーマーブレイクレベル2、スタン耐性+12魔法発動"));
                    break;
                case 751:
                    npcId = 514;
                    dollType = L1DollInstance.DOLL_MUMMY_LORD;// マミーロード
                    pc.sendPackets(new S_ChatPacket(pc, "ダメージ減少+ 2、経験値ボーナス+ 10％、64秒ごとにMP 15回復"));
                    break;
                case 752:
                    npcId = 515;
                    dollType = L1DollInstance.DOLL_CURRUPTION;// 堕落
                    pc.sendPackets(new S_ChatPacket(pc, "スタン耐性+10、SP + 3、魔法命中+5"));
                    break;
            }
            L1Npc template = NpcTable.getInstance().getTemplate(npcId);
            doll = new L1DollInstance(template, pc, dollType, itemObjectId);
            pc.sendPackets(new S_SkillSound(doll.getId(), 5935));
            pc.broadcastPacket(new S_SkillSound(doll.getId(), 5935));
            pc.sendPackets(new S_SkillIconGFX(56, 1800));
            pc.sendPackets(new S_OwnCharStatus(pc));
            pc.getInventory().consumeItem(41246, 50);
            pc.setQuizTime3(curtime);
        }
    }

    private void useSupport(L1PcInstance pc, int itemId, int itemObjectId) { // つつくウィザード
        if (!pc.getMap().isTakePets()) {
            pc.sendPackets(new S_ServerMessage(563)); // \f1ここでは、使用することができません。
            return;
        }

        if (pc.isInWarArea()) {
            pc.sendPackets(new S_ServerMessage(563)); // \f1ここで​​は使用できません。
            return;
        }

        if (pc.getMapId() == 5153 || pc.getMapId() == 5140) {
            pc.sendPackets(new S_ServerMessage(563)); // \f1ここで​​は使用できません。
            return;
        }

        if (!pc.getInventory().checkItem(40308, 5000)) {
            pc.sendPackets(new S_ServerMessage(337, "$4")); // \f1%0が不足します。
            return;
        }
        if (pc.getLevel() >= Config.TSUTSUKUGEN) {
            pc.sendPackets(new S_ChatPacket(pc, " " + Config.TSUTSUKUGEN + "レベルまで使用出来ます。"));
            return;
        }
        boolean isAppear = true;
        L1SupportInstance support = null;
        Object[] supportList = pc.getSupportList().values().toArray();
        for (Object supportObject : supportList) {
            support = (L1SupportInstance) supportObject;
            if (support.getItemObjId() == itemObjectId) { // すでに取り出しているマジックドール
                isAppear = false;
                break;
            }
        }

        if (isAppear) {
            if (supportList.length >= Config.MAX_DOLL_COUNT) {// これ以上のmonsterを操縦することができません。
                pc.sendPackets(new S_ServerMessage(319));
                return;
            }
            int npcId = 0;
            int supportType = 0;
            if (itemId == 210095) {
                npcId = 200073;
                supportType = L1SupportInstance.SUPPORTTYPE_DWARF;
            }

            L1Npc template = NpcTable.getInstance().getTemplate(npcId);
            support = new L1SupportInstance(template, pc, supportType, itemObjectId);
            support.broadcastPacket(new S_SkillSound(support.getId(), 5935));
            pc.sendPackets(new S_OwnCharStatus(pc));
            pc.getInventory().consumeItem(40308, 1);
            pc.setSupporting(true);
        } else {
            support.broadcastPacket(new S_SkillSound(support.getId(), 5936));
            support.deleteSupport();
            pc.sendPackets(new S_OwnCharStatus(pc));
            pc.setSupporting(false);
        }
    }

    private void useFurnitureItem(L1PcInstance pc, int itemId, int itemObjectId) {
        if (!L1HouseLocation.isInHouse(pc.getX(), pc.getY(), pc.getMapId())) {
            pc.sendPackets(new S_ServerMessage(563)); // \f1ここで​​は使用できません。
            return;
        }

        boolean isAppear = true;
        L1FurnitureInstance furniture = null;
        for (L1Object l1object : L1World.getInstance().getObject()) {
            if (l1object instanceof L1FurnitureInstance) {
                furniture = (L1FurnitureInstance) l1object;
                if (furniture.getItemObjId() == itemObjectId) { // すでに取り出している家具
                    isAppear = false;
                    break;
                }
            }
        }

        if (isAppear) {
            if (pc.getHeading() != 0 && pc.getHeading() != 2) {
                return;
            }
            int npcId = 0;
            switch (itemId) {
                case 41383:
                    npcId = 80109;
                    break;
                case 41384:
                    npcId = 80110;
                    break;
                case 41385:
                    npcId = 80113;
                    break;
                case 41386:
                    npcId = 80114;
                    break;
                case 41387:
                    npcId = 80115;
                    break;
                case 41388:
                    npcId = 80124;
                    break;
                case 41389:
                    npcId = 80118;
                    break;
                case 41390:
                    npcId = 80118;
                    break;
                case 41391:
                    npcId = 80120;
                    break;
                case 41392:
                    npcId = 80121;
                    break;
                case 41393:
                    npcId = 80126;
                    break;
                case 41394:
                    npcId = 80125;
                    break;
                case 41395:
                    npcId = 80111;
                    break;
                case 41396:
                    npcId = 80112;
                    break;
                case 41397:
                    npcId = 80116;
                    break;
                case 41398:
                    npcId = 80117;
                    break;
                case 41399:
                    npcId = 80122;
                    break;
                case 41400:
                    npcId = 80123;
                    break;
            }

            try {
                L1Npc l1npc = NpcTable.getInstance().getTemplate(npcId);
                if (l1npc != null) {
                    try {
                        String s = l1npc.getImpl();
                        Constructor<?> constructor = Class.forName("l1j.server.server.model.Instance." + s + "Instance")
                                .getConstructors()[0];
                        Object aobj[] = { l1npc };
                        furniture = (L1FurnitureInstance) constructor.newInstance(aobj);
                        furniture.setId(IdFactory.getInstance().nextId());
                        furniture.setMap(pc.getMapId());
                        if (pc.getHeading() == 0) {
                            furniture.setX(pc.getX());
                            furniture.setY(pc.getY() - 1);
                        } else if (pc.getHeading() == 2) {
                            furniture.setX(pc.getX() + 1);
                            furniture.setY(pc.getY());
                        }
                        furniture.setHomeX(furniture.getX());
                        furniture.setHomeY(furniture.getY());
                        furniture.setHeading(0);
                        furniture.setItemObjId(itemObjectId);

                        L1World.getInstance().storeObject(furniture);
                        L1World.getInstance().addVisibleObject(furniture);
                        FurnitureSpawnTable.getInstance().insertFurniture(furniture);
                    } catch (Exception e) {
                        _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
                    }
                }
            } catch (Exception exception) {
            }
        } else {
            furniture.deleteMe();
            FurnitureSpawnTable.getInstance().deleteFurniture(furniture);
        }
    }

    private void serchdroplist2(L1PcInstance pc, int targetId, L1ItemInstance item) {
        S_AttackPacket s_attackStatus = new S_AttackPacket(pc, 0, ActionCodes.ACTION_Wand);
        long curtime = System.currentTimeMillis() / 1000;

        pc.sendPackets(s_attackStatus);
        Broadcaster.broadcastPacket(pc, s_attackStatus);
        L1Object target = L1World.getInstance().findObject(targetId);
        if (target != null && target instanceof L1MonsterInstance) {
            L1MonsterInstance npc = (L1MonsterInstance) target;

            int transformId = npc.getNpcTemplate().getTransformId();
            if (transformId == -1) {
                pc.sendPackets(new S_Serchdrop2(npc.getNpcId()));
                pc.setQuizTime(curtime);
            } else {
                pc.sendPackets(new S_Serchdrop2(transformId));
                pc.setQuizTime(curtime);
            }
        }
    }

    private void useFieldObjectRemovalWand(L1PcInstance pc, int targetId, L1ItemInstance item) {
        S_AttackPacket s_attackStatus = new S_AttackPacket(pc, 0, ActionCodes.ACTION_Wand);
        pc.sendPackets(s_attackStatus);
        Broadcaster.broadcastPacket(pc, s_attackStatus);
        L1Object target = L1World.getInstance().findObject(targetId);

        if (target != null && target instanceof L1MonsterInstance) {
            L1MonsterInstance mob = (L1MonsterInstance) target;
            new L1NpcDeleteTimer(mob, 2 * 1000).begin();
            mob.setRespawn(false);
            mob.deleteMe();
            pc.sendPackets(new S_SystemMessage("モンスター" + mob.getNpcId() + mob.getName() + "を2秒後削除します。"));
            pc.getInventory().updateItem(item, L1PcInventory.COL_CHARGE_COUNT);
        } else if (target != null && target instanceof L1NpcInstance) {
            L1NpcInstance npc = (L1NpcInstance) target;
            NpcSpawnTable.getInstance().removeSpawn(npc);
            npc.setRespawn(false);
            new L1NpcDeleteTimer(npc, 2 * 1000).begin();
            pc.sendPackets(new S_SystemMessage("エンピシ" + npc.getNpcId() + npc.getName() + "を2秒後削除します。"));
            pc.getInventory().updateItem(item, L1PcInventory.COL_CHARGE_COUNT);
        }
    }

    private void useFieldObjectRemovalWand1(L1PcInstance pc, int targetId, L1ItemInstance item) {
        S_AttackPacket s_attackStatus = new S_AttackPacket(pc, 0, ActionCodes.ACTION_Wand);
        pc.sendPackets(s_attackStatus);
        Broadcaster.broadcastPacket(pc, s_attackStatus);
        L1Object target = L1World.getInstance().findObject(targetId);
        if (target != null && target instanceof L1NpcInstance) {
            L1NpcInstance npc = (L1NpcInstance) target;
            pc.sendPackets(new S_ChatPacket(pc, "番号：（" + npc.getNpcId() + ") // " + "名前：（" + npc.getName() + ")"));
            pc.sendPackets(new S_ChatPacket(pc, "ロケーション：" + "(x:" + npc.getX() + "), (y:" + npc.getY() + "), (" + "Map:"
                    + npc.getMapId() + ") // GfxId : (" + npc.getGfxId() + ")"));
            pc.getInventory().updateItem(item, L1PcInventory.COL_CHARGE_COUNT);
        }
    }

    private void useFurnitureRemovalWand(L1PcInstance pc, int targetId, L1ItemInstance item) {
        S_AttackStatus s_attackStatus = new S_AttackStatus(pc, 0, ActionCodes.ACTION_Wand);
        pc.sendPackets(s_attackStatus);
        pc.broadcastPacket(s_attackStatus);
        int chargeCount = item.getChargeCount();
        if (chargeCount <= 0) {
            return;
        }

        L1Object target = L1World.getInstance().findObject(targetId);
        if (target != null && target instanceof L1FurnitureInstance) {
            L1FurnitureInstance furniture = (L1FurnitureInstance) target;
            furniture.deleteMe();
            FurnitureSpawnTable.getInstance().deleteFurniture(furniture);
            item.setChargeCount(item.getChargeCount() - 1);
            if (item.getChargeCount() == 0) {
                pc.getInventory().removeItem(item);
            } else {
                pc.getInventory().updateItem(item, L1PcInventory.COL_CHARGE_COUNT);
            }
        }
    }

    private void useNpcSpownWand(L1PcInstance pc, int targetId, L1ItemInstance item) {
        S_AttackStatus s_attackStatus = new S_AttackStatus(pc, 0, ActionCodes.ACTION_Wand);
        pc.sendPackets(s_attackStatus);
        pc.broadcastPacket(s_attackStatus);
        int chargeCount = item.getChargeCount();
        if (chargeCount <= 0) {
            return;
        }

        try {
            L1Npc l1npc = NpcTable.getInstance().getTemplate(targetId);
            String s = l1npc.getImpl();
            Constructor<?> constructor = Class.forName("l1j.server.server.model.Instance." + s + "Instance")
                    .getConstructors()[0];
            Object aobj[] = { l1npc };
            L1NpcInstance npc = (L1NpcInstance) constructor.newInstance(aobj);
            npc.setId(IdFactory.getInstance().nextId());
            npc.setMap(pc.getMapId());
            npc.setX(pc.getX());
            npc.setY(pc.getY());
            npc.setHomeX(pc.getX());
            npc.setHomeY(pc.getY());
            npc.setMap(pc.getMapId());
            npc.setHeading(2);
            L1World.getInstance().storeObject(npc);
            L1World.getInstance().addVisibleObject(npc);
            L1NpcDeleteTimer timer = new L1NpcDeleteTimer(npc, 60000);
            // 60秒後に削除処理
            timer.begin();
        } catch (Exception e) {
            e.printStackTrace();
        }
        item.setChargeCount(item.getChargeCount() - 1);
        if (item.getChargeCount() == 0) {
            pc.getInventory().removeItem(item);
        } else {
            pc.getInventory().updateItem(item, L1PcInventory.COL_CHARGE_COUNT);
        }
    }

    public static void crownWideStun(L1PcInstance pc) {
        if (pc.isCrown() || pc.isGm()) {
            if (pc.hasSkillEffect(L1SkillId.DELAY)) { // ディレイ
                pc.sendPackets(new S_SystemMessage("まだ広域スタンを使用することができません。"));
                return;
            }
            if (pc.isInvisble()) {
                pc.sendPackets(new S_SystemMessage("広域スタンは透明状態で使用が不可能です。"));
                return;
            }
            if (pc.getMapId() == 800) {
                pc.sendPackets(new S_SystemMessage("広域スターンは、市場での使用が不可能です。"));
                return;
            }
            if (pc.getZoneType() == 1) {
                pc.sendPackets(new S_SystemMessage("広域スターンは村で使用が不可能です。"));
                return;
            }
            if (pc.getCurrentMp() < 30) {
                pc.sendPackets(new S_ServerMessage(278)); // \f1MPが不足魔法を使用
                // することができません。
                return;
            }
            pc.setCurrentMp(pc.getCurrentMp() - 30);
            pc.sendPackets(new S_SystemMessage("広域スタンを使用します。"));
            pc.setSkillEffect(L1SkillId.DELAY, 10 * 1000);
            pc.sendPackets(new S_SkillIconGFX(74, 3));

            int actionId = ActionCodes.ACTION_SkillBuff;
            S_DoActionGFX gfx = new S_DoActionGFX(pc.getId(), actionId);
            pc.sendPackets(gfx);
            Broadcaster.broadcastPacket(pc, gfx);

            for (L1Object obj : L1World.getInstance().getVisibleObjects(pc, 10)) {
                Random random = new Random();
                int[] stunTimeArray = { 2000, 2500, 3000, 3500, 4000 };
                int rnd = random.nextInt(stunTimeArray.length);
                int probability = random.nextInt(100) + 1;

                if (probability < 50) {
                    int _shockStunDuration = stunTimeArray[rnd];
                    if (obj instanceof L1PcInstance) {
                        L1PcInstance target = (L1PcInstance) obj;
                        L1PinkName.onAction(target, pc);
                        if ((pc.getClanid() > 0 && (pc.getClanid() == target.getClanid())) || target.isGm()) {
                        } else {
                            L1Character cha = (L1Character) obj;

                            if (!cha.hasSkillEffect(SHOCK_STUN) && !cha.hasSkillEffect(EARTH_BIND)
                                    && !cha.hasSkillEffect(ICE_LANCE)) {
                                L1EffectSpawn.getInstance().spawnEffect(81162, _shockStunDuration, target.getX(),
                                        target.getY(), target.getMapId());
                                target.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, true));
                                target.setSkillEffect(SHOCK_STUN, _shockStunDuration);
                                target.sendPackets(new S_SkillSound(target.getId(), 4434)); // スターン
                                Broadcaster.broadcastPacket(target, new S_SkillSound(target.getId(), 4434));
                            }
                        }
                    } else if (obj instanceof L1MonsterInstance || obj instanceof L1SummonInstance
                            || obj instanceof L1PetInstance) {
                        L1NpcInstance targetnpc = (L1NpcInstance) obj;
                        L1EffectSpawn.getInstance().spawnEffect(81162, _shockStunDuration, targetnpc.getX(),
                                targetnpc.getY(), targetnpc.getMapId());
                        targetnpc.setParalyzed(true);
                        targetnpc.setSkillEffect(SHOCK_STUN, _shockStunDuration);
                        Broadcaster.broadcastPacket(targetnpc, new S_SkillSound(obj.getId(), 4434));
                    }
                }
            }
        } else {
            pc.sendPackets(new S_SystemMessage("この技術は、君主のみ使用が可能です。"));
        }
        System.currentTimeMillis();
        return;
    }

    private void RobotItem(L1PcInstance pc) {
        // DBから文字と倉庫のアイテムを読み込む
        CharacterTable.getInstance().restoreInventory(pc);
    }

    private void warriorSkill(L1PcInstance pc, L1ItemInstance item, boolean ispassibe) {
        L1Skills skill = SkillsTable.getInstance().getTemplateByItem(item.getItemId());
        if (skill != null) {
            if (ispassibe) {
                int id = skill.getId();
                int skillId = skill.getSkillId();
                // pc.sendPackets(new S_ACTION_UI(146, skillId));
                if (SkillsTable.getInstance().spellCheck(pc.getId(), skillId)) {
                    pc.sendPackets(new S_SystemMessage("すでに学んだ魔法です。"), true);
                    return;
                }
                pc.sendPackets(new S_ACTION_UI(146, id));
                S_SkillSound s_skillSound = new S_SkillSound(pc.getId(), 224);
                pc.sendPackets(s_skillSound);
                Broadcaster.broadcastPacket(pc, s_skillSound);
                SkillsTable.getInstance().spellMastery(pc.getId(), skillId, skill.getName(), 0, 0);
            } else {
                int skillLevel = skill.getSkillLevel();
                int id = skill.getId();
                int[] arr = new int[29];
                arr[skillLevel - 1] = id;
                int skillId = skill.getSkillId();
                if (SkillsTable.getInstance().spellCheck(pc.getId(), skillId)) {
                    pc.sendPackets(new S_SystemMessage("すでに学んだ魔法です。"), true);
                    return;
                }
                int objid = pc.getId();
                pc.sendPackets(new S_AddSkill(pc, arr));
                S_SkillSound s_skillSound = new S_SkillSound(objid, 224);
                pc.sendPackets(s_skillSound);
                Broadcaster.broadcastPacket(pc, s_skillSound);
                SkillsTable.getInstance().spellMastery(objid, skillId, skill.getName(), 0, 0);
            }
        }
        pc.getInventory().removeItem(item, 1);
    }

    private void internetCafeCoin(L1PcInstance pc, int itemId, L1ItemInstance useItem, int day) {
        long sysTime = System.currentTimeMillis();
        Timestamp deleteTime = null;
        deleteTime = new Timestamp(sysTime + (86400000 * (long) day) + 10000);// 7日
        try {
            if (pc.PCRoom_Buff) {
                pc.sendPackets(new S_SystemMessage("通知：すでにPC部屋バフ商品が適用中です。"));
                return;
            }

            pc.sendPackets(new S_PacketBox(S_PacketBox.PC_ROOM_BUFF, 1));
            pc.PCRoom_Buff = true;
            pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.PCBANG_SET, true));
            if (day == 7) {
                pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "【PC部屋利用時間】7日間PC部屋利点が適用されます。"));
            } else {
                pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "【PC部屋利用時間】30日間のPC部屋利点が適用されます。"));
            }
            pc.getAccount().setBuff_PCRoom(deleteTime);
            pc.getAccount().updateInternetCafe();
            pc.getInventory().removeItem(useItem, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void reinforceBuff(L1PcInstance pc, int itemId, L1ItemInstance useItem) {
        String n = "";
        long sysTime = System.currentTimeMillis();
        Timestamp deleteTime_Vigor = null;
        Timestamp deleteTime_Attack = null;
        Timestamp deleteTime_Defense = null;
        Timestamp deleteTime_Magic = null;
        Timestamp deleteTime_Stun = null;
        Timestamp deleteTime_Hold = null;
        Timestamp deleteTime_Power = null;
        Timestamp deleteTime_Dex = null;
        Timestamp deleteTime_Point = null;
        // pc.sendPackets(new S_OwnCharStatus2(pc));
        // pc.sendPackets(new S_CharVisualUpdate(pc)); // キャラ情報アップデッドエ
        deleteTime_Vigor = new Timestamp(sysTime + (86400000 * (long) Config.ENCHANT_BUFF_TIME_VITALITY) + 10000);// 7日
        deleteTime_Attack = new Timestamp(sysTime + (86400000 * (long) Config.ENCHANT_BUFF_TIME_ATTACK) + 10000);// 7日
        deleteTime_Defense = new Timestamp(sysTime + (86400000 * (long) Config.ENCHANT_BUFF_TIME_DEFENCE) + 10000);// 7日
        deleteTime_Magic = new Timestamp(sysTime + (86400000 * (long) Config.ENCHANT_BUFF_TIME_MAGIC) + 10000);// 7日
        deleteTime_Stun = new Timestamp(sysTime + (86400000 * (long) Config.ENCHANT_BUFF_TIME_STUN) + 10000);// 7日
        deleteTime_Hold = new Timestamp(sysTime + (86400000 * (long) Config.ENCHANT_BUFF_TIME_HOLD) + 10000);// 7日
        deleteTime_Power = new Timestamp(sysTime + (86400000 * (long) Config.ENCHANT_BUFF_TIME_STR) + 10000);// 7日
        deleteTime_Dex = new Timestamp(sysTime + (86400000 * (long) Config.ENCHANT_BUFF_TIME_DEX) + 10000);// 7日
        deleteTime_Point = new Timestamp(sysTime + (86400000 * (long) Config.ENCHANT_BUFF_TIME_INT) + 10000);// 7日

        try {
            if (itemId == 600212) {
                n = "活力";
                if (pc.hasSkillEffect(L1SkillId.ENCHANT_BUFF_VITAL)) {
                    pc.killSkillEffectTimer(L1SkillId.ENCHANT_BUFF_VITAL);
                    pc.addMaxHp(-50);
                    pc.addMaxMp(-50);
                    pc.addWeightReduction(-3);
                    pc.sendPackets(new S_HPUpdate(pc));
                    pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                }
                pc.removeSkillEffect(L1SkillId.ENCHANT_BUFF_VITAL);
                pc.setSkillEffect(L1SkillId.ENCHANT_BUFF_VITAL, (int) 86400000 * Config.ENCHANT_BUFF_TIME_VITALITY);
                pc.getNetConnection().getAccount().setBuff_HPMP(deleteTime_Vigor);
                pc.sendPackets(new S_ACTION_UI2(n, (long) 86400000 * Config.ENCHANT_BUFF_TIME_VITALITY), true);
                pc.addMaxHp(50);
                pc.addMaxMp(50);
                pc.addWeightReduction(3);
                pc.sendPackets(new S_HPUpdate(pc));
                pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));

            } else if (itemId == 600213) {
                n = "攻撃";
                if (pc.hasSkillEffect(L1SkillId.ENCHANT_BUFF_ATTACK)) {
                    pc.killSkillEffectTimer(L1SkillId.ENCHANT_BUFF_ATTACK);
                    pc.addDmgup(-1);
                    pc.addBowDmgup(-1);
                }
                pc.removeSkillEffect(L1SkillId.ENCHANT_BUFF_ATTACK);
                pc.setSkillEffect(L1SkillId.ENCHANT_BUFF_ATTACK, (int) 86400000 * Config.ENCHANT_BUFF_TIME_ATTACK);
                pc.getNetConnection().getAccount().setBuff_DMG(deleteTime_Attack);
                pc.sendPackets(new S_ACTION_UI2(n, (long) 86400000 * Config.ENCHANT_BUFF_TIME_ATTACK), true);
                pc.addDmgup(1);
                pc.addBowDmgup(1);
            } else if (itemId == 600214) {
                n = "防御";
                if (pc.hasSkillEffect(L1SkillId.ENCHANT_BUFF_DEFENSE)) {
                    pc.killSkillEffectTimer(L1SkillId.ENCHANT_BUFF_DEFENSE);
                    pc.addDamageReductionByArmor(-1);
                }
                pc.removeSkillEffect(L1SkillId.ENCHANT_BUFF_DEFENSE);
                pc.setSkillEffect(L1SkillId.ENCHANT_BUFF_DEFENSE, (int) 86400000 * Config.ENCHANT_BUFF_TIME_DEFENCE);
                pc.getNetConnection().getAccount().setBuff_REDUC(deleteTime_Defense);
                pc.sendPackets(new S_ACTION_UI2(n, (long) 86400000 * Config.ENCHANT_BUFF_TIME_DEFENCE), true);
                pc.addDamageReductionByArmor(1); // ダメージイパイ上げテスト
            } else if (itemId == 600215) {
                n = "魔法";
                if (pc.hasSkillEffect(L1SkillId.ENCHANT_BUFF_MAGIC)) {
                    pc.killSkillEffectTimer(L1SkillId.ENCHANT_BUFF_MAGIC);
                    pc.getAbility().addSp(-1); // オプション
                    pc.sendPackets(new S_SPMR(pc)); // 自分のmrを変更
                }
                pc.removeSkillEffect(L1SkillId.ENCHANT_BUFF_MAGIC);
                pc.setSkillEffect(L1SkillId.ENCHANT_BUFF_MAGIC, (int) 86400000 * Config.ENCHANT_BUFF_TIME_MAGIC);
                pc.getNetConnection().getAccount().setBuff_MAGIC(deleteTime_Magic);
                pc.sendPackets(new S_ACTION_UI2(n, (long) 86400000 * Config.ENCHANT_BUFF_TIME_MAGIC), true);
                pc.getAbility().addSp(1); // オプション
                pc.sendPackets(new S_SPMR(pc)); // 自分のmrを変更
            } else if (itemId == 600216) {
                n = "スターン";
                if (pc.hasSkillEffect(L1SkillId.ENCHANT_BUFF_STUN)) {
                    pc.killSkillEffectTimer(L1SkillId.ENCHANT_BUFF_STUN);
                    pc.getResistance().addStun(-2);
                }
                pc.removeSkillEffect(L1SkillId.ENCHANT_BUFF_STUN);
                pc.setSkillEffect(L1SkillId.ENCHANT_BUFF_STUN, (int) 86400000 * Config.ENCHANT_BUFF_TIME_STUN);
                pc.getNetConnection().getAccount().setBuff_STUN(deleteTime_Stun);
                pc.sendPackets(new S_ACTION_UI2(n, (long) 86400000 * Config.ENCHANT_BUFF_TIME_STUN), true);
                pc.getResistance().addStun(2); // オプション
            } else if (itemId == 600217) {
                n = "ホールド";
                if (pc.hasSkillEffect(L1SkillId.ENCHANT_BUFF_HOLD)) {
                    pc.killSkillEffectTimer(L1SkillId.ENCHANT_BUFF_HOLD);
                    pc.getResistance().addHold(-2);
                }
                pc.removeSkillEffect(L1SkillId.ENCHANT_BUFF_HOLD);
                pc.setSkillEffect(L1SkillId.ENCHANT_BUFF_HOLD, (int) 86400000 * Config.ENCHANT_BUFF_TIME_HOLD);
                pc.getNetConnection().getAccount().setBuff_HOLD(deleteTime_Hold);
                pc.sendPackets(new S_ACTION_UI2(n, (long) 86400000 * Config.ENCHANT_BUFF_TIME_HOLD), true);
                pc.getResistance().addHold(2); // オプション
            } else if (itemId == 600259) { // 力
                n = "力 ";
                if (pc.hasSkillEffect(L1SkillId.ENCHANT_BUFF_DEX) || pc.hasSkillEffect(L1SkillId.ENCHANT_BUFF_INT)) {
                    pc.sendPackets(new S_SystemMessage("ステップバフは、ネストして使用することができません"));
                    return;
                }
                if (pc.hasSkillEffect(L1SkillId.ENCHANT_BUFF_STR)) {
                    pc.killSkillEffectTimer(L1SkillId.ENCHANT_BUFF_STR);
                    pc.getAbility().addAddedStr((byte) -1);
                    pc.sendPackets(new S_OwnCharStatus2(pc), true);
                }
                pc.removeSkillEffect(L1SkillId.ENCHANT_BUFF_STR);
                pc.setSkillEffect(L1SkillId.ENCHANT_BUFF_STR, (int) 86400000 * Config.ENCHANT_BUFF_TIME_STR);
                pc.getNetConnection().getAccount().setBuff_STR(deleteTime_Power);
                pc.sendPackets(new S_ACTION_UI2(n, (long) 86400000 * Config.ENCHANT_BUFF_TIME_STR), true);
                pc.getAbility().addAddedStr((byte) 1);
                pc.sendPackets(new S_OwnCharStatus2(pc), true);
            } else if (itemId == 600260) {// デックス
                n = "デックス";
                if (pc.hasSkillEffect(L1SkillId.ENCHANT_BUFF_STR) || pc.hasSkillEffect(L1SkillId.ENCHANT_BUFF_INT)) {
                    pc.sendPackets(new S_SystemMessage("ステップバフは、ネストして使用することができません"));
                    return;
                }
                if (pc.hasSkillEffect(L1SkillId.ENCHANT_BUFF_DEX)) {
                    pc.killSkillEffectTimer(L1SkillId.ENCHANT_BUFF_DEX);
                    // pc.getAbility().addDex(-1);
                    pc.getAbility().addAddedDex((byte) -1);
                    pc.sendPackets(new S_OwnCharStatus2(pc), true);
                }
                pc.removeSkillEffect(L1SkillId.ENCHANT_BUFF_DEX);
                pc.setSkillEffect(L1SkillId.ENCHANT_BUFF_DEX, (int) 86400000 * Config.ENCHANT_BUFF_TIME_DEX);
                pc.getNetConnection().getAccount().setBuff_DEX(deleteTime_Dex);
                pc.sendPackets(new S_ACTION_UI2(n, (long) 86400000 * Config.ENCHANT_BUFF_TIME_DEX), true);
                // pc.getAbility().addDex(1);
                pc.getAbility().addAddedDex((byte) 1);
                pc.sendPackets(new S_OwnCharStatus2(pc), true);
            } else if (itemId == 600261) { // ポイント
                n = "ポイント";
                if (pc.hasSkillEffect(L1SkillId.ENCHANT_BUFF_DEX) || pc.hasSkillEffect(L1SkillId.ENCHANT_BUFF_STR)) {
                    pc.sendPackets(new S_SystemMessage("ステップバフは、ネストして使用することができません"));
                    return;
                }
                if (pc.hasSkillEffect(L1SkillId.ENCHANT_BUFF_INT)) {
                    pc.killSkillEffectTimer(L1SkillId.ENCHANT_BUFF_INT);
                    // pc.getAbility().addInt(-1);
                    pc.getAbility().addAddedInt((byte) -1);
                    pc.sendPackets(new S_OwnCharStatus2(pc), true);
                }
                pc.removeSkillEffect(L1SkillId.ENCHANT_BUFF_INT);
                pc.setSkillEffect(L1SkillId.ENCHANT_BUFF_INT, (int) 86400000 * Config.ENCHANT_BUFF_TIME_INT);
                pc.getNetConnection().getAccount().setBuff_INT(deleteTime_Point);
                pc.sendPackets(new S_ACTION_UI2(n, (long) 86400000 * Config.ENCHANT_BUFF_TIME_INT), true);
                // pc.getAbility().addInt(1);
                pc.getAbility().addAddedInt((byte) 1);
                pc.sendPackets(new S_OwnCharStatus2(pc), true);
            }
            pc.getNetConnection().getAccount().updateBUFF();
            pc.getInventory().removeItem(useItem, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initReinforceBuff(L1PcInstance pc, int itemId, L1ItemInstance useItem) {
        long sysTime = System.currentTimeMillis();
        Timestamp deleteTime = null;
        deleteTime = new Timestamp(sysTime + 1);// 7日
        try {

            if (pc.hasSkillEffect(L1SkillId.ENCHANT_BUFF_VITAL)) {
                pc.removeSkillEffect(L1SkillId.ENCHANT_BUFF_VITAL);
                pc.getNetConnection().getAccount().setBuff_HPMP(deleteTime);
                pc.sendPackets(new S_ACTION_UI2("活力", (long) 100 * 10), true);
            }

            if (pc.hasSkillEffect(L1SkillId.ENCHANT_BUFF_ATTACK)) {
                pc.removeSkillEffect(L1SkillId.ENCHANT_BUFF_ATTACK);
                pc.getNetConnection().getAccount().setBuff_DMG(deleteTime);
                pc.sendPackets(new S_ACTION_UI2("攻撃", (long) 100 * 10), true);
            }
            if (pc.hasSkillEffect(L1SkillId.ENCHANT_BUFF_DEFENSE)) {
                pc.removeSkillEffect(L1SkillId.ENCHANT_BUFF_DEFENSE);
                pc.getNetConnection().getAccount().setBuff_REDUC(deleteTime);
                pc.sendPackets(new S_ACTION_UI2("防御", (long) 100 * 10), true);
            }
            if (pc.hasSkillEffect(L1SkillId.ENCHANT_BUFF_MAGIC)) {
                pc.removeSkillEffect(L1SkillId.ENCHANT_BUFF_MAGIC);
                pc.getNetConnection().getAccount().setBuff_MAGIC(deleteTime);
                pc.sendPackets(new S_ACTION_UI2("魔法", (long) 100 * 10), true);
            }
            if (pc.hasSkillEffect(L1SkillId.ENCHANT_BUFF_STUN)) {
                pc.removeSkillEffect(L1SkillId.ENCHANT_BUFF_STUN);
                pc.getNetConnection().getAccount().setBuff_STUN(deleteTime);
                pc.sendPackets(new S_ACTION_UI2("スターン", (long) 100 * 10), true);
            }
            if (pc.hasSkillEffect(L1SkillId.ENCHANT_BUFF_HOLD)) {
                pc.removeSkillEffect(L1SkillId.ENCHANT_BUFF_HOLD);
                pc.getNetConnection().getAccount().setBuff_HOLD(deleteTime);
                pc.sendPackets(new S_ACTION_UI2("ホールド", (long) 100 * 10), true);
            }
            if (pc.hasSkillEffect(L1SkillId.ENCHANT_BUFF_STR)) {
                pc.removeSkillEffect(L1SkillId.ENCHANT_BUFF_STR);
                pc.getNetConnection().getAccount().setBuff_STR(deleteTime);
                pc.sendPackets(new S_ACTION_UI2("力", (long) 100 * 10), true);
            }
            if (pc.hasSkillEffect(L1SkillId.ENCHANT_BUFF_DEX)) {
                pc.removeSkillEffect(L1SkillId.ENCHANT_BUFF_DEX);
                pc.getNetConnection().getAccount().setBuff_DEX(deleteTime);
                pc.sendPackets(new S_ACTION_UI2("デックス", (long) 100 * 10), true);
            }
            if (pc.hasSkillEffect(L1SkillId.ENCHANT_BUFF_INT)) {
                pc.removeSkillEffect(L1SkillId.ENCHANT_BUFF_INT);
                pc.getNetConnection().getAccount().setBuff_INT(deleteTime);
                pc.sendPackets(new S_ACTION_UI2("ポイント", (long) 100 * 10), true);
            }
            pc.getNetConnection().getAccount().updateBUFF();
            pc.getInventory().removeItem(useItem, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isTwoLogin(L1PcInstance c) {// 重複チェックを変更
        boolean bool = false;
        for (L1PcInstance target : L1World.getInstance().getAllPlayers()) {
            if (target.noPlayerCK || target.noPlayerck2)
                continue;
            /** ロボットシステム **/
            if (target.getRobotAi() != null)
                continue;
            /** ロボットシステム **/
            if (c.getId() != target.getId() && (!target.isPrivateShop() && !target.isAutoClanjoin())) {
                if (c.getNetConnection().getAccountName()
                        .equalsIgnoreCase(target.getNetConnection().getAccountName())) {
                    bool = true;
                    break;
                }
            }
        }
        return bool;
    }

    public void tamadd(String _name, int objectId, int _day, String _encobjid) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("INSERT INTO Tam SET objid=?, Name=?, Day=? , encobjid=?");
            pstm.setInt(1, objectId);
            pstm.setString(2, _name);
            pstm.setInt(3, _day);
            pstm.setString(4, _encobjid);
            pstm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void tamupdate(int objectId, Timestamp date) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("UPDATE characters SET TamEndTime=? WHERE objid=?");
            pstm.setTimestamp(1, date);
            pstm.setInt(2, objectId);
            pstm.executeUpdate();
        } catch (SQLException e) {
            // _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public static final int[] hextable = { 0x80, 0x81, 0x82, 0x83, 0x84, 0x85, 0x86, 0x87, 0x88, 0x89, 0x8a, 0x8b, 0x8c,
            0x8d, 0x8e, 0x8f, 0x90, 0x91, 0x92, 0x93, 0x94, 0x95, 0x96, 0x97, 0x98, 0x99, 0x9a, 0x9b, 0x9c, 0x9d, 0x9e,
            0x9f, 0xa0, 0xa1, 0xa2, 0xa3, 0xa4, 0xa5, 0xa6, 0xa7, 0xa8, 0xa9, 0xaa, 0xab, 0xac, 0xad, 0xae, 0xaf, 0xb0,
            0xb1, 0xb2, 0xb3, 0xb4, 0xb5, 0xb6, 0xb7, 0xb8, 0xb9, 0xba, 0xbb, 0xbc, 0xbd, 0xbe, 0xbf, 0xc0, 0xc1, 0xc2,
            0xc3, 0xc4, 0xc5, 0xc6, 0xc7, 0xc8, 0xc9, 0xca, 0xcb, 0xcc, 0xcd, 0xce, 0xcf, 0xd0, 0xd1, 0xd2, 0xd3, 0xd4,
            0xd5, 0xd6, 0xd7, 0xd8, 0xd9, 0xda, 0xdb, 0xdc, 0xdd, 0xde, 0xdf, 0xe0, 0xe1, 0xe2, 0xe3, 0xe4, 0xe5, 0xe6,
            0xe7, 0xe8, 0xe9, 0xea, 0xeb, 0xec, 0xed, 0xee, 0xef, 0xf0, 0xf1, 0xf2, 0xf3, 0xf4, 0xf5, 0xf6, 0xf7, 0xf8,
            0xf9, 0xfa, 0xfb, 0xfc, 0xfd, 0xfe, 0xff };

    private String byteWrite(long value) {
        long temp = value / 128;
        StringBuffer sb = new StringBuffer();
        if (temp > 0) {
            sb.append((byte) hextable[(int) value % 128]);
            while (temp >= 128) {
                sb.append((byte) hextable[(int) temp % 128]);
                temp = temp / 128;
            }
            if (temp > 0)
                sb.append((int) temp);
        } else {
            if (value == 0) {
                sb.append(0);
            } else {
                sb.append((byte) hextable[(int) value]);
                sb.append(0);
            }
        }
        return sb.toString();
    }

    private void TAMFruit(L1PcInstance pc, int _objid, L1ItemInstance item, int day) {
        try {
            Timestamp tamtime = null;
            long time = 0;
            long sysTime = System.currentTimeMillis();
            String _Name = null;
            int tamcount = pc.tamcount();

            Connection con = null;
            PreparedStatement pstm = null;
            ResultSet rs = null;
            try {
                con = L1DatabaseFactory.getInstance().getConnection();
                pstm = con.prepareStatement("SELECT TamEndTime, char_name FROM characters WHERE objid=?");
                pstm.setInt(1, _objid);
                rs = pstm.executeQuery();
                while (rs.next()) {
                    _Name = rs.getString("char_name");
                    tamtime = rs.getTimestamp("TamEndTime");
                    if (tamtime != null) {
                        if (sysTime < tamtime.getTime()) {
                            time = tamtime.getTime() - sysTime;
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                SQLUtil.close(rs);
                SQLUtil.close(pstm);
                SQLUtil.close(con);
            }

            if (time != 0) {
                tamadd(_Name, _objid, day, byteWrite(_objid));
                pc.sendPackets(new S_TamWindow(pc.getAccountName()));
                pc.sendPackets(new S_SystemMessage("[" + _Name + "]で既に利用されている商品がありエヤクました。"), true);
                pc.getInventory().removeItem(item, 1);
                return;
            } else if (tamcount >= 3) {// ここでアカウントごとに3つの食べたのかチェックするように見えた
                pc.sendPackets(new S_SystemMessage("成長の輪は3つの文字のみ使用可能です。"), true);
                return;
            }
            Timestamp deleteTime = null;
            deleteTime = new Timestamp(sysTime + (86400000 * (long) day) + 10000);// 7日

            // deleteTime = new Timestamp(sysTime + 1000*60);//7日

            if (pc.getId() == _objid) {
                pc.setTamTime(deleteTime);
                try {
                    pc.save();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                tamupdate(_objid, deleteTime);
            }

            pc.sendPackets(new S_TamWindow(pc.getAccountName()));
            int aftertamcount = pc.tamcount();
            int aftertamtime = (int) pc.TamTime();

            if (pc.hasSkillEffect(L1SkillId.Tam_Fruit1)) {
                pc.killSkillEffectTimer(L1SkillId.Tam_Fruit1);
                pc.getAC().addAc(1);
            } else if (pc.hasSkillEffect(L1SkillId.Tam_Fruit2)) {
                pc.killSkillEffectTimer(L1SkillId.Tam_Fruit2);
                pc.getAC().addAc(2);
            } else if (pc.hasSkillEffect(L1SkillId.Tam_Fruit3)) {
                pc.killSkillEffectTimer(L1SkillId.Tam_Fruit3);
                pc.getAC().addAc(3);
            } else if (pc.hasSkillEffect(L1SkillId.Tam_Fruit4)) {
                pc.getAC().addAc(4);
            } else if (pc.hasSkillEffect(L1SkillId.Tam_Fruit5)) {
                pc.getAC().addAc(5);
            } else {
            }

            if (aftertamtime < 0) {
                aftertamtime = 0;
            }

            if (aftertamcount == 1) {
                pc.getAC().addAc(-1);
                pc.setSkillEffect(Tam_Fruit1, aftertamtime);
            } else if (aftertamcount == 2) {
                pc.getAC().addAc(-2);
                pc.setSkillEffect(Tam_Fruit2, aftertamtime);
            } else if (aftertamcount == 3) {
                pc.getAC().addAc(-3);
                pc.setSkillEffect(Tam_Fruit3, aftertamtime);
            } else if (aftertamcount == 4) { //
                pc.getAC().addAc(-4);
                pc.setSkillEffect(Tam_Fruit4, aftertamtime);
            } else if (aftertamcount == 5) {
                pc.getAC().addAc(-5);
                pc.setSkillEffect(Tam_Fruit5, aftertamtime);
            }

            pc.sendPackets(new S_OwnCharStatus(pc));
            pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.BUFF_WINDOW, pc.TamTime(), aftertamcount, true), true);
            pc.sendPackets(new S_ServerMessage(3916));
            pc.sendPackets(new S_SkillSound(pc.getId(), 2028), true);
            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2028), true);
            pc.getInventory().removeItem(item, 1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void supplyEXP(L1PcInstance pc) {
        int needExp = ExpTable.getNeedExpNextLevel(52);
        double exppenalty = ExpTable.getPenaltyRate(pc.getLevel());
        int exp = 0;
        if (pc.getLevel() <= 55) {
            exp = (int) (needExp * 0.01D);
        } else if (pc.getLevel() <= 60) {
            exp = (int) (needExp * 0.01D);
        } else if (pc.getLevel() <= 65) {
            exp = (int) (needExp * 0.001D);
        } else if (pc.getLevel() <= 70) {
            exp = (int) (needExp * 0.0100D);
        } else if (pc.getLevel() <= 74) {
            exp = (int) (needExp * 0.01000D);
        } else if (pc.getLevel() <= 99) {
            exp = (int) (needExp * 0.01000D);
        } else {
            exp = (int) (needExp * 0.01D * exppenalty);
        }
        pc.addExp(exp);
        Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 3944));
        pc.sendPackets(new S_SkillSound(pc.getId(), 3944));
    }

    private void StatInitialize(L1PcInstance pc) {
        L1SkillUse l1skilluse = new L1SkillUse();
        l1skilluse.handleCommands(pc, L1SkillId.CANCELLATION, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_LOGIN);

        if (pc.getWeapon() != null) {
            pc.getInventory().setEquipped(pc.getWeapon(), false, false, false, false);
        }

        pc.sendPackets(new S_CharVisualUpdate(pc));
        pc.sendPackets(new S_OwnCharStatus2(pc));

        for (L1ItemInstance armor : pc.getInventory().getItems()) {
            for (int type = 0; type <= 12; type++) {
                if (armor != null) {
                    pc.getInventory().setEquipped(armor, false, false, false, false);
                }
            }
        }
        pc.setReturnStat(pc.getExp());
        pc.sendPackets(new S_SPMR(pc));
        pc.sendPackets(new S_OwnCharAttrDef(pc));
        pc.sendPackets(new S_OwnCharStatus2(pc));
        pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.START));
        try {
            pc.save();
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
    }

    /**
     * TAM
     **/
    private HashMap<Integer, L1PcInstance> charlist = new HashMap<Integer, L1PcInstance>();

    public HashMap<Integer, L1PcInstance> getCharList() {
        return charlist;
    }

    public void addCharList(int id, L1PcInstance pc) {
        charlist.put(id, pc);
    }

    public void deleteCharList(int id) {
        charlist.remove(id);
    }

    @Override
    public String getType() {
        return C_ITEM_USE;
    }
}