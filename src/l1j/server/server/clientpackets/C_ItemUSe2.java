package l1j.server.server.clientpackets;

import static l1j.server.server.model.skill.L1SkillId.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.ActionCodes;
import l1j.server.server.GameClient;
import l1j.server.server.datatables.ClanTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.Getback;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1EffectSpawn;
import l1j.server.server.model.L1HouseLocation;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PinkName;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1TownLocation;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1EffectInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_ChangeShape;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_DelSkill;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_Karma;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_OwnCharStatus2;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_ReturnedStat;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.SQLUtil;

public class C_ItemUSe2 extends ClientBasePacket {

    private static final String C_ITEM_USE2 = "[C] C_ItemUSe2";

    private static Logger _log = Logger.getLogger(C_ItemUSe2.class.getName());

    private static Random _random = new Random(System.nanoTime());

    public C_ItemUSe2(byte abyte0[], GameClient client) throws Exception {
        super(abyte0);
        int itemObjid = readD();
        int l = 0;

        L1PcInstance pc = client.getActiveChar();
        if (pc == null)
            return;

        L1ItemInstance l1iteminstance = pc.getInventory().getItem(itemObjid);

        if (l1iteminstance == null || l1iteminstance.getItem() == null) {
            return;
        }

        int itemId;
        int spellsc_objid = 0;

        try {
            itemId = l1iteminstance.getItem().getItemId();
        } catch (Exception e) {
            return;
        }


        switch (itemId) {

            /** エンコインアイテム化 **/
            case 1000024:    //10
                pc.getNetConnection().getAccount().Ncoin_point += 10;
                pc.getNetConnection().getAccount().updateNcoin();
                pc.sendPackets(new S_SystemMessage("Nコイン" + 10 + "ワン充電された。"), true);
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
            case 1000025:    //100
                pc.getNetConnection().getAccount().Ncoin_point += 100;
                pc.getNetConnection().getAccount().updateNcoin();
                pc.sendPackets(new S_SystemMessage("Nコイン" + 100 + "ワン充電された。"), true);
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
            case 1000026:    //1000
                pc.getNetConnection().getAccount().Ncoin_point += 1000;
                pc.getNetConnection().getAccount().updateNcoin();
                pc.sendPackets(new S_SystemMessage("Nコイン" + 1000 + "ワン充電された。"), true);
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
            case 1000027:    //5000
                pc.getNetConnection().getAccount().Ncoin_point += 5000;
                pc.getNetConnection().getAccount().updateNcoin();
                pc.sendPackets(new S_SystemMessage("Nコイン" + 5000 + "ワン充電された。"), true);
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
            case 1000028:    //10000
                pc.getNetConnection().getAccount().Ncoin_point += 10000;
                pc.getNetConnection().getAccount().updateNcoin();
                pc.sendPackets(new S_SystemMessage("Nコイン" + 10000 + "ワン充電された。"), true);
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
            case 1000029:    //30000
                pc.getNetConnection().getAccount().Ncoin_point += 30000;
                pc.getNetConnection().getAccount().updateNcoin();
                pc.sendPackets(new S_SystemMessage("Nコイン" + 30000 + "ワン充電された。"), true);
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;


            case 410057: {// 占いメッセージa
                int[] allBuffSkill = {L1SkillId.FEATHER_BUFF_A};
                L1SkillUse l1skilluse = new L1SkillUse();
                if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_B))
                    pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_B);
                if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_C))
                    pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_C);
                if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_D))
                    pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_D);
                for (int i = 0; i < allBuffSkill.length; i++) {
                    l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_SPELLSC);
                    pc.broadcastPacket(new S_SkillSound(pc.getId(), 7947));
                }
                pc.getInventory().removeItem(l1iteminstance, 1);
            }
            break;

            case 410058: {// 占いメッセージb
                int[] allBuffSkill = {L1SkillId.FEATHER_BUFF_B};
                L1SkillUse l1skilluse = new L1SkillUse();
                if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_A))
                    pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_A);
                if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_C))
                    pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_C);
                if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_D))
                    pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_D);
                for (int i = 0; i < allBuffSkill.length; i++) {
                    l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_SPELLSC);
                    pc.broadcastPacket(new S_SkillSound(pc.getId(), 7948));
                }
                pc.getInventory().removeItem(l1iteminstance, 1);
            }
            break;
            case 410059: {// 占いメッセージc
                int[] allBuffSkill = {L1SkillId.FEATHER_BUFF_C};
                L1SkillUse l1skilluse = new L1SkillUse();
                if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_B))
                    pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_B);
                if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_A))
                    pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_A);
                if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_D))
                    pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_D);
                for (int i = 0; i < allBuffSkill.length; i++) {
                    l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_SPELLSC);
                    pc.broadcastPacket(new S_SkillSound(pc.getId(), 7949));
                }
                pc.getInventory().removeItem(l1iteminstance, 1);
            }
            break;
            case 410060: {// 占いメッセージd
                int[] allBuffSkill = {L1SkillId.FEATHER_BUFF_D};
                L1SkillUse l1skilluse = new L1SkillUse();
                if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_B))
                    pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_B);
                if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_C))
                    pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_C);
                if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_A))
                    pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_A);
                for (int i = 0; i < allBuffSkill.length; i++) {
                    l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_SPELLSC);
                    pc.broadcastPacket(new S_SkillSound(pc.getId(), 7950));
                }
                pc.getInventory().removeItem(l1iteminstance, 1);
            }
            break;


            case 40304: { // マーブルの遺産
                int count = _random.nextInt(6) + 5;
                pc.getInventory().storeItem(40318, count); //マドル
                pc.sendPackets(new S_SystemMessage("魔力の石（" + count + "）獲得"), true);
                pc.getInventory().removeItem(l1iteminstance, 1);
            }
            break;
            case 40305: { // パアグリオの遺産
                int count = _random.nextInt(6) + 5;
                pc.getInventory().storeItem(40320, count); // 黒魔石
                pc.sendPackets(new S_SystemMessage("黒魔石（" + count + "）獲得"), true);
                pc.getInventory().removeItem(l1iteminstance, 1);
            }
            break;
            case 40306: { //エヴァの遺産
                int count = _random.nextInt(6) + 5;
                pc.getInventory().storeItem(40319, count); // 精霊の玉
                pc.sendPackets(new S_SystemMessage("精霊の玉（" + count + "）獲得"), true);
                pc.getInventory().removeItem(l1iteminstance, 1);
            }
            break;
            case 40307: { // サイハの遺産
                int count = _random.nextInt(20) + 1;
                pc.getInventory().storeItem(40318, count); // マドル
                pc.sendPackets(new S_SystemMessage("魔力の石（" + count + "）獲得"), true);
                count = _random.nextInt(30) + 1;
                pc.getInventory().storeItem(40319, count); // 精霊の玉
                pc.sendPackets(new S_SystemMessage("精霊の玉（" + count + "）獲得"), true);
                count = _random.nextInt(20) + 1;
                pc.getInventory().storeItem(40320, count); // 黒魔石
                pc.sendPackets(new S_SystemMessage("黒魔石（" + count + "）獲得"), true);
                count = _random.nextInt(5) + 1;
                pc.getInventory().storeItem(40031, count); // 悪魔の血
                pc.sendPackets(new S_SystemMessage("悪魔の血（" + count + "）獲得"), true);
                pc.getInventory().removeItem(l1iteminstance, 1);
            }
            break;
            case 410009: { // 性転換ポーション
                int[] MALE_LIST = new int[]{0, 61, 138, 734, 2786, 6658, 6671, 12490};
                int[] FEMALE_LIST = new int[]{1, 48, 37, 1186, 2796, 6661, 6650, 12494};
                if (pc.get_sex() == 0) {
                    pc.set_sex(1);
                    pc.setClassId(FEMALE_LIST[pc.getType()]);
                } else {
                    pc.set_sex(0);
                    pc.setClassId(MALE_LIST[pc.getType()]);
                }
                pc.setTempCharGfx(pc.getClassId());
                pc.sendPackets(new S_ChangeShape(pc.getId(), pc.getClassId()));
                pc.broadcastPacket(new S_ChangeShape(pc.getId(), pc.getClassId()));
                pc.getInventory().removeItem(l1iteminstance, 1);
            }
            break;
            case 700021:// 封印解除スクロール申請書
                if (!pc.isQuizValidated()) {
                    pc.sendPackets(new S_ChatPacket(pc, "クイズの認証をしていません。"));
                    pc.sendPackets(new S_ChatPacket(pc, "まず[。クイズ認証]でクイズ認証後、再試行してください。"));
                    return;
                }
                if (pc.getInventory().checkItem(50021)) { //
                    pc.sendPackets(new S_ChatPacket(pc, "すでに封印解除スクロールを持っています。"));
                    return;
                }
                if (pc.getInventory().checkItem(700021, 1)) {
                    pc.getInventory().consumeItem(700021, 1);
                    pc.getInventory().storeItem(50021, 15);
                }
                break;
            case 30104: {// コマの祝福コイン
                int[] allBuffSkill = {50007};
                L1SkillUse l1skilluse = new L1SkillUse();
                for (int i = 0; i < allBuffSkill.length; i++) {
                    l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_SPELLSC);
                }
                pc.getInventory().removeItem(l1iteminstance, 1);
            }
            break;
            case 30124:// 貝
                pc.sendPackets(new S_SystemMessage("攻城戦の際カタパルトを使用するときに必要な消耗性アイテム"));

                break;
            case 800200://パッケージ1次
                if (pc.getInventory().getSize() > 120) {
                    pc.sendPackets(new S_ChatPacket(pc, "所持しているアイテムが多すぎます。"));
                    return;
                }
                if (pc.getInventory().getWeight100() > 82) { // この部分を変更すると、エラーが発生する。
                    pc.sendPackets(new S_ChatPacket(pc, "持ち物が重すぎて使用することはできません。"));
                    return;
                }
                if (pc.getInventory().checkItem(800200, 1)) { // チェックされているアイテムと数量
                    pc.getInventory().removeItem(l1iteminstance, 1);
                    createNewItem2(pc, 800001, 1, 0); // 武器コイン
                    createNewItem2(pc, 800002, 1, 0); // 投球コイン
                    createNewItem2(pc, 800003, 1, 0); // Tシャツコイン
                    createNewItem2(pc, 800004, 1, 0); // マントコイン
                    createNewItem2(pc, 800005, 1, 0); // 鎧コイン
                    createNewItem2(pc, 800007, 1, 0); // 手袋コイン
                    createNewItem2(pc, 800008, 1, 0); // ブーツコイン
                    createNewItem2(pc, 800009, 1, 0); // ネックレスコイン
                    createNewItem2(pc, 800010, 1, 0); // イヤリングコイン
                    createNewItem2(pc, 800011, 1, 0); // リングコイン
                    createNewItem2(pc, 800011, 1, 0); // リングコイン
                    createNewItem2(pc, 800012, 1, 0); // ベルトコイン
                    createNewItem2(pc, 800013, 1, 0); // 人形コイン
                    createNewItem2(pc, 800014, 1, 0); // ゲートルコイン
                    createNewItem2(pc, 40308, 50000000, 0); // アデナ
                    createNewItem2(pc, 3000119, 1, 0); // パッケージの移動お守り
                    if (pc.isWarrior()) {
                        createNewItem2(pc, 203006, 1, 9); // 台風の斧
                    }
                    if (!pc.isWarrior()) {
                        createNewItem2(pc, 800006, 1, 0); // 盾コイン
                    }
                }
                break;
            case 1000010:// ランダム武器箱
                int randomchance1 = _random.nextInt(17);
                int randomchance2 = _random.nextInt(14);
                int[] item1 = {1121, 47, 203006, 203018, 76, 1137, 203017, 1119, 1123, 202001,
                        1120, 1136, 202003, 58, 54, 203025, 203023};
                int[] item2 = {212, 614, 604, 600, 57, 9, 11, 602, 74, 157, 205, 127, 1134, 603};
                if (_random.nextInt(100) + 1 <= 10) {
                    createNewItem2(pc, item1[randomchance1], 1, 5); // ランダム武器2万評価
                } else {
                    createNewItem2(pc, item2[randomchance2], 1, 5); // ランダム武器1万評価
                }
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
            case 430507:// 4~ステップ6スペルブック
                if (pc.getInventory().getSize() > 120) {
                    pc.sendPackets(new S_ChatPacket(pc, "所持しているアイテムが多すぎます。"));
                    return;
                }
                if (pc.getInventory().getWeight100() > 82) { // この部分を変更すると、エラーが発生する。
                    pc.sendPackets(new S_ChatPacket(pc, "持ち物が重すぎて使用することはできません。"));
                    return;
                }
                for (int i = 40170; i < 40194; i++) {
                    createNewItem2(pc, i, 1, 0);
                }
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
            case 1000050:// 4〜ステップ10スペルブック（ディス、デス・ヒル、メテオ、アブソセーフ除く）
                if (pc.getInventory().getSize() > 120) {
                    pc.sendPackets(new S_ChatPacket(pc, "所持しているアイテムが多すぎます。"));
                    return;
                }
                if (pc.getInventory().getWeight100() > 82) { // この部分を変更すると、エラーが発生する。
                    pc.sendPackets(new S_ChatPacket(pc, "持ち物が重すぎて使用することはできません。"));
                    return;
                }
                for (int i = 40170; i < 40226; i++) {
                    if (i == 40212 || i == 40219 || i == 40222 || i == 40223) {
                        i++;
                    }
                    createNewItem2(pc, i, 1, 0);
                }
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
            case 500219:// バルログ献呈書（友好度）
                if (pc.getKarma() <= 10000000) {
                    pc.addKarma((int) (+15000 * Config.RATE_KARMA));
                    pc.sendPackets(new S_Karma(pc));
                    pc.sendPackets(new S_SystemMessage(pc.getName() + "さんの友好度が向上しました。"));
                    // pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE、「Ctrl + Aキー押し4番目のウィンドウで、現在の友好度の状態を確認することができます。 "））;
                    pc.getInventory().removeItem(l1iteminstance, 1);
                } else
                    pc.sendPackets(new S_ServerMessage(79));
                break;
            case 500218:// ヤヒ献呈書（友好度）
                if (pc.getKarma() >= -10000000) {
                    pc.addKarma((int) (-15000 * Config.RATE_KARMA));
                    pc.sendPackets(new S_Karma(pc));
                    pc.sendPackets(new S_SystemMessage(pc.getName() + "さんの友好度が向上しました。"));
                    // pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE、「Ctrl + Aキー押し4番目のウィンドウで、現在の友好度の状態を確認することができます。 "））;

                    pc.getInventory().removeItem(l1iteminstance, 1);
                } else
                    pc.sendPackets(new S_ServerMessage(79));
                break;
            case 1000011:// ギラン監獄充電コイン
                if (pc.getGirandungeonTime() > 115) {
                    pc.getInventory().removeItem(l1iteminstance, 1);
                    pc.setGirandungeonTime(0);
                    pc.save();
                    pc.sendPackets(new S_ChatPacket(pc, "ギラン監獄、支配者の結界ダンジョン滞留時間が2時間の充電された。"));
                } else {
                    pc.sendPackets(new S_ChatPacket(pc, "ギラン監獄、支配者の結界滞留時間がまだ残っています。"));
                }
                break;
            case 410062://象牙の塔のダンジョン充電コイン
                if (pc.getOrendungeonTime() > 55) {
                    pc.getInventory().removeItem(l1iteminstance, 1);
                    pc.setOrendungeonTime(0);
                    pc.save();
                    pc.sendPackets(new S_ChatPacket(pc, "象牙の塔のダンジョン滞留時間が1時間の充電された。"));
                } else {
                    pc.sendPackets(new S_ChatPacket(pc, "象牙の塔のダンジョン滞留時間がまだ残っています。"));
                }
                break;
            case 1000012:// モンソム充電コイン
                if (pc.getSomeTime() > 25) {
                    pc.getInventory().removeItem(l1iteminstance, 1);
                    pc.setSomeTime(0);
                    pc.save();
                    pc.sendPackets(new S_ChatPacket(pc, "夢幻の島滞在時間が充電された。"));
                } else {
                    pc.sendPackets(new S_ChatPacket(pc, "夢幻の島滞在時間がまだ残っています"));
                }
                break;
            case 1000013://政務充電
                if (pc.getSoulTime() > 25) {
                    pc.getInventory().removeItem(l1iteminstance, 1);
                    pc.setSoulTime(0);
                    pc.save();
                    pc.sendPackets(new S_ChatPacket(pc, "墓滞留時間が30分の充電された。"));
                } else {
                    pc.sendPackets(new S_ChatPacket(pc, "墓滞留時間がまだ残っています"));
                }
                break;

            case 410135:// ラスタバドケイブ充電コイン
                if (pc.getRadungeonTime() > 115) {
                    pc.getInventory().removeItem(l1iteminstance, 1);
                    pc.setRadungeonTime(0);
                    pc.save();
                    pc.sendPackets(new S_ChatPacket(pc, "ラスタバド滞留時間が2時間の充電された。"));
                } else {
                    pc.sendPackets(new S_ChatPacket(pc, "ラスタバド滞留時間がまだ残っています"));
                }
                break;
            case 500216:// 用のダンジョン充電コイン
                if (pc.getDrageonTime() > 115) {
                    pc.getInventory().removeItem(l1iteminstance, 1);
                    pc.setDrageonTime(0);
                    pc.save();
                    pc.sendPackets(new S_ChatPacket(pc, "用のダンジョン滞留時間2時間充電しました。"));
                } else {
                    pc.sendPackets(new S_ChatPacket(pc, "用のダンジョン滞留時間がまだ残っています。"));
                }
                break;
            case 40867: {// キュアポイズンブランクスクロール
                int[] allBuffSkill = {9};
                L1SkillUse l1skilluse = null;
                l1skilluse = new L1SkillUse();
                pc.setBuffnoch(1);
                for (int i = 0; i < allBuffSkill.length; i++) {
                    l1skilluse
                            .handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_SPELLSC);
                }
                pc.setBuffnoch(0);
                pc.getInventory().removeItem(l1iteminstance, 1);
            }
            break;
            case 40877: {// エクストラ・ヒル
                int[] allBuffSkill = {19};
                L1SkillUse l1skilluse = null;
                l1skilluse = new L1SkillUse();
                pc.setBuffnoch(1);
                for (int i = 0; i < allBuffSkill.length; i++) {
                    l1skilluse
                            .handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_SPELLSC);
                }
                pc.setBuffnoch(0);
                pc.getInventory().removeItem(l1iteminstance, 1);
            }
            break;
            case 40893: {// グレートヒル
                int[] allBuffSkill = {35};
                L1SkillUse l1skilluse = null;
                l1skilluse = new L1SkillUse();
                pc.setBuffnoch(1);
                for (int i = 0; i < allBuffSkill.length; i++) {
                    l1skilluse
                            .handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_SPELLSC);
                }
                pc.setBuffnoch(0);
                pc.getInventory().removeItem(l1iteminstance, 1);
            }
            break;

            /** 傲慢の塔混沌お守り、変異されたお守り **/
            case 30106: {// 隠された渓谷の村帰還お守り
                if (pc.get_DuelLine() != 0) {
                    pc.sendPackets(new S_SystemMessage("バトルゾーン地域で使用することができません。"));
                    return;
                }
                if (pc.isDead())
                    return;
                if (pc.getCurrentHp() < 1)
                    return;

                int[] loc = L1TownLocation.getGetBackLoc(L1TownLocation.TOWNID_HIDDEN_VALLEY);
                new L1Teleport().teleport(pc, loc[0], loc[1], (short) loc[2], pc.getHeading(), true);
            }
            break;
            case 3000108:// バルログ陣営4階
                if (pc.getnewdodungeonTime() >= 59) {
                    pc.sendPackets(new S_SystemMessage("\\aA警告: \\aG[バルログ陣営]\\aA ダンジョン時間が経過しました。"));
                    return;
                }
                if (pc.getMap().isEscapable() || pc.isGm()) {
                    int rx = _random.nextInt(2);
                    int ux = 32901 + rx;
                    int uy = 32765 + rx; // 象牙の塔
                    if (itemId == 3000108) {
                        new L1Teleport().teleport(pc, ux, uy, (short) 280, pc.getHeading(), true);
                    }
                    pc.setSkillEffect(L1SkillId.ABSOLUTE_BARRIER, 3000);
                    pc.getInventory().removeItem(l1iteminstance, 1);
                } else {
                    pc.sendPackets(new S_ServerMessage(647));
                }
                cancelAbsoluteBarrier(pc); //アブ小ガルトバリアの解除
                break;
            case 3000120://メインランドのダンジョン1階
                if (pc.getGirandungeonTime() >= 180) {
                    pc.sendPackets(new S_SystemMessage("\\aA警告: \\aG[グルーディオ]\\aA ダンジョン時間が経過しました。"));
                    return;
                }
                if (pc.getMap().isEscapable() || pc.isGm()) {
                    int rx = _random.nextInt(2);
                    int ux = 32811 + rx;
                    int uy = 32726 + rx;
                    if (itemId == 3000120) {
                        new L1Teleport().teleport(pc, ux, uy, (short) 807, pc.getHeading(), true);
                    }
                    pc.setSkillEffect(L1SkillId.ABSOLUTE_BARRIER, 3000);
                    pc.getInventory().removeItem(l1iteminstance, 1);
                } else {
                    pc.sendPackets(new S_ServerMessage(647));
                }
                cancelAbsoluteBarrier(pc); // アブ小ガルトバリアの解除
                break;
            case 42069:// 巻い1階
                if (pc.getislandTime() >= 59) {
                    pc.sendPackets(new S_SystemMessage("\\aA警告: \\aG[話せる島]\\aA ダンジョン時間が経過しました。"));
                    return;
                }
                if (pc.getMap().isEscapable() || pc.isGm()) {
                    int rx = _random.nextInt(2);
                    int ux = 32668 + rx;
                    int uy = 32804 + rx;
                    if (itemId == 42069) {
                        new L1Teleport().teleport(pc, ux, uy, (short) 1, pc.getHeading(), true);
                    }
                    pc.setSkillEffect(L1SkillId.ABSOLUTE_BARRIER, 3000);
                    pc.getInventory().removeItem(l1iteminstance, 1);
                } else {
                    pc.sendPackets(new S_ServerMessage(647));
                }
                cancelAbsoluteBarrier(pc); // アブ小ガルトバリアの解除
                break;
            case 301067: {// 桜の村
                if (pc.get_DuelLine() != 0) {
                    pc.sendPackets(new S_SystemMessage("バトルゾーン地域で使用することができません。"));
                    return;
                }
                if (pc.isDead())
                    return;
                if (pc.getCurrentHp() < 1)
                    return;

                if (pc.getMap().isEscapable() || pc.isGm()) {
                    new L1Teleport().teleport(pc, 32756, 32867, (short) 610, pc.getHeading(), true);
                }
            }
            break;
            case 600232: {// 忘れられた島脱出書
                if (pc.get_DuelLine() != 0) {
                    pc.sendPackets(new S_SystemMessage("バトルゾーン地域で使用することができません。"));
                    return;
                }
                if (pc.isDead())
                    return;
                if (pc.getCurrentHp() < 1)
                    return;

                if (!(pc.getMapId() >= 1700 && pc.getMapId() <= 1711)) {
                    pc.sendPackets(new S_SystemMessage("忘れられた島でのみ使用可能です。"));
                    return;
                }
                new L1Teleport().teleport(pc, 33452, 32788, (short) 4, pc.getHeading(), true);
            }
            break;


            case 202099: {// クラウディア村
                if (pc.get_DuelLine() != 0) {
                    pc.sendPackets(new S_SystemMessage("バトルゾーン地域で使用することができません。"));
                    return;
                }
                if (pc.isDead())
                    return;
                if (pc.getCurrentHp() < 1)
                    return;

                if (pc.getMap().isEscapable() || pc.isGm()) {
                    int[] loc = L1TownLocation.getGetBackLoc(L1TownLocation.TOWNID_claudia);
                    new L1Teleport().teleport(pc, loc[0], loc[1], (short) loc[2], pc.getHeading(), true);
                }
            }
            break;
            case 40081: //ギラン村書
            case 301066:
                if (pc.get_DuelLine() != 0) {
                    pc.sendPackets(new S_SystemMessage("バトルゾーン地域で使用することができません。"));
                    return;
                }
                if (pc.isDead())
                    return;
                if (pc.getCurrentHp() < 1)
                    return;

                if (pc.getMap().isEscapable() || pc.isGm()) {
                    int[] loc = L1TownLocation.getGetBackLoc(L1TownLocation.TOWNID_GIRAN);
                    new L1Teleport().teleport(pc, loc[0], loc[1], (short) loc[2], pc.getHeading(), true);
                    if (itemId == 40081) {
                        pc.getInventory().removeItem(l1iteminstance, 1);
                    }

                } else {
                    pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
                    pc.sendPackets(new S_ServerMessage(647));
                }
                cancelAbsoluteBarrier(pc);
                break;
            case 40117: // はナイトの村
                if (pc.get_DuelLine() != 0) {
                    pc.sendPackets(new S_SystemMessage("バトルゾーン地域で使用することができません。"));
                    return;
                }
                if (pc.isDead())
                    return;
                if (pc.getCurrentHp() < 1)
                    return;

                if (pc.getMap().isEscapable() || pc.isGm()) {
                    int[] loc = L1TownLocation.getGetBackLoc(L1TownLocation.TOWNID_SILVER_KNIGHT_TOWN);
                    new L1Teleport().teleport(pc, loc[0], loc[1], (short) loc[2], pc.getHeading(), true);
                    pc.getInventory().removeItem(l1iteminstance, 1);
                    cancelAbsoluteBarrier(pc);
                } else {
                    pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
                    pc.sendPackets(new S_ServerMessage(647));
                }
                break;

            case 41260:// 装着
                for (L1Object object : L1World.getInstance().getVisibleObjects(pc, 3)) {
                    if (object instanceof L1EffectInstance) {
                        if (((L1NpcInstance) object).getNpcTemplate().get_npcId() == 81170) {
                            // すでに周囲に焚き火があります。
                            pc.sendPackets(new S_ServerMessage(1162));
                            return;
                        }
                    }
                }
                int[] loc1 = new int[2];
                loc1 = pc.getFrontLoc();
                L1EffectSpawn.getInstance().spawnEffect(81170, 300000, loc1[0], loc1[1], pc.getMapId());
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
            case 240100://呪われたテレポートスクロール（オリジナルアイテム）
                if (pc.get_DuelLine() != 0) {
                    pc.sendPackets(new S_SystemMessage("バトルゾーン地域で使用することができません。"));
                    return;
                }
                if (pc.isDead())
                    return;
                if (pc.getCurrentHp() < 1)
                    return;

                new L1Teleport().teleport(pc, pc.getX(), pc.getY(), pc.getMapId(), pc.getHeading(), true);
                pc.getInventory().removeItem(l1iteminstance, 1);
                pc.cancelAbsoluteBarrier(); // アブ小ガルトバリアの解除
                break;
            case 748: // 雑貨店に移動お守り
                new L1Teleport().teleport(pc, 33453, 32820, (short) 4, pc.getHeading(), true);
                break;
            case 749: // 装置の店に移動お守り
                new L1Teleport().teleport(pc, 33435, 32754, (short) 4, pc.getHeading(), true);
                break;

            case 500076: {//近距離バフポーション
                int[] allBuffSkill = {14, 26, 42, 48, 168, 160, 206, 211, 216, 148, 158};
                //ディクリーズ、デッキ、力、ブレスウェポン、オベン、アース、アクア、コンセント、ペイシェンスは、Insight、ファイアーウェポン、アバター、ネイチャーズ
                pc.setBuffnoch(1);
                L1SkillUse l1skilluse = null;
                l1skilluse = new L1SkillUse();
                for (int i = 0; i < allBuffSkill.length; i++) {
                    l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                    pc.sendPackets(new S_SkillSound(pc.getId(), 830));
                    pc.curePoison();
                }
                pc.setBuffnoch(0);
                pc.getInventory().removeItem(l1iteminstance, 1);
            }
            break;
            case 500077: {//遠距離バフポーション
                int[] allBuffSkill = {14, 26, 42, 43, 48, 151, 160, 206, 211, 216, 149};
                //ディクリーズ、デッキ、力、ブレスウェポン、オベン、アース、アクア、コンセント、ペイシェンスは、Insight、ストームショット、アバター、ネイチャーズ
                pc.setBuffnoch(1);
                L1SkillUse l1skilluse = null;
                l1skilluse = new L1SkillUse();
                for (int i = 0; i < allBuffSkill.length; i++) {
                    l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                    pc.sendPackets(new S_SkillSound(pc.getId(), 830));
                    pc.curePoison();
                }
                pc.setBuffnoch(0);
                pc.getInventory().removeItem(l1iteminstance, 1);
            }
            break;


            /** パッケージレポートスクロール **/
            case 800100:
                pc.setCashStep(1);
                new L1Teleport().teleport(pc, 32672, 32793, (short) 514, 5, true);
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
            case 800101:
                pc.setCashStep(2);
                new L1Teleport().teleport(pc, 32672, 32793, (short) 515, 5, true);
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
            case 800102:
                pc.setCashStep(3);
                new L1Teleport().teleport(pc, 32672, 32793, (short) 516, 5, true);
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
            case 3000119: //パッケージ店レポートスクロール
                if (pc.getZoneType() != 1 || pc.getMapId() != 4) {
                    pc.sendPackets(new S_SystemMessage("村でのみ使用することができます。"));
                    return;
                } else {
                    pc.setCashStep(1);
                    new L1Teleport().teleport(pc, 32672, 32785, (short) 514, 5, true);
                }
                break;
            case 40124:
            case 30086:// 血盟帰還スクロール
                if (pc.get_DuelLine() != 0) {
                    pc.sendPackets(new S_SystemMessage("バトルゾーン地域で使用することができません。"));
                    return;
                }
                if (pc.isDead())
                    return;
                if (pc.getCurrentHp() < 1)
                    return;

                if (pc.getMap().isEscapable() || pc.isGm()) {
                    int castle_id = 0;
                    int house_id = 0;
                    if (pc.getClanid() != 0) { // クランに所属
                        L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
                        if (clan != null) {
                            castle_id = clan.getCastleId();
                            house_id = clan.getHouseId();
                        }
                    }
                    if (castle_id != 0) { //城主クラン員
                        if (pc.getMap().isEscapable() || pc.isGm()) {
                            int[] loc = new int[3];
                            loc = L1CastleLocation.getCastleLoc(castle_id);
                            int locx = loc[0];
                            int locy = loc[1];
                            short mapid = (short) (loc[2]);
                            new L1Teleport().teleport(pc, locx, locy, mapid, pc.getHeading(), true);
                            pc.getInventory().removeItem(l1iteminstance, 1);
                        } else {
                            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
                            pc.sendPackets(new S_ServerMessage(647));
                        }
                    } else if (house_id != 0) { // アジトを所有クラン員
                        if (pc.getMap().isEscapable() || pc.isGm()) {
                            int[] loc = new int[3];
                            loc = L1HouseLocation.getHouseLoc(house_id);
                            int locx = loc[0];
                            int locy = loc[1];
                            short mapid = (short) (loc[2]);
                            new L1Teleport().teleport(pc, locx, locy, mapid, 5, true);
                            pc.getInventory().removeItem(l1iteminstance, 1);
                        } else {
                            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
                            pc.sendPackets(new S_ServerMessage(647));
                        }
                    } else {
                        if (pc.getHomeTownId() > 0) {
                            int[] loc = L1TownLocation.getGetBackLoc(pc.getHomeTownId());
                            int locx = loc[0];
                            int locy = loc[1];
                            short mapid = (short) (loc[2]);
                            new L1Teleport().teleport(pc, locx, locy, mapid, 5, true);
                            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
                            pc.getInventory().removeItem(l1iteminstance, 1);
                        } else {
                            int[] loc = Getback.GetBack_Location(pc, true);
                            new L1Teleport().teleport(pc, loc[0], loc[1], (short) loc[2], 5, true);
                            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
                            pc.getInventory().removeItem(l1iteminstance, 1);
                        }
                    }
                } else {
                    pc.sendPackets(new S_ServerMessage(647));
                }
                cancelAbsoluteBarrier(pc); // アブ小ガルトバリアの解除
                break;

            case 40312:// 旅館鍵
                if (pc.isstop()) {
                    return;
                }
                if (pc.isParalyzed() || pc.isSleeped() || pc.isDead()) {
                    return;
                }
                if (pc.getMap().isEscapable() || pc.isGm()) {
                    Thread.sleep(500);
                    new L1Teleport().teleport(pc, 32742, 32803, (short) 18432, 5, false);
                }
                break;
            case 5988: //アビスポイントポーション
                pc.addAbysspoint(100);
                pc.sendPackets(new S_SystemMessage("アビスポイント100点獲得"));
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
            case 5989: //血盟経験値ポーション
                if (pc.getClanid() != 0) {
                    pc.getClan().addClanExp(100);
                    pc.getInventory().removeItem(l1iteminstance, 1);
                    ClanTable.getInstance().updateClan(pc.getClan()); // 殺した人血盟の更新
                } else {
                    pc.sendPackets(new S_ServerMessage(4055));
                }
                break;
            case 9990:
                abyssStunSkill(pc);
                break;
            case 9989:
                abyssDamageSkill(pc);
                break;
            case 9995:
                L1SkillUse l1skilluse = new L1SkillUse();
                l1skilluse.handleCommands(pc, L1SkillId.NARUTO_THANKS_CANDY, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
            case 60256:
                if (pc.getInventory().checkItem(60255)) {
                    pc.sendPackets(new S_ServerMessage(939));
                    pc.sendPackets(new S_SystemMessage("ドラゴンのアメジストを保有。"));
                    return;
                }
                pc.getInventory().storeItem(60255, 1);
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
            case 89136:
                if (pc.get_SpecialSize() == 40) {
                    pc.sendPackets(new S_ServerMessage(1622));
                    return;
                }
                if (pc.get_SpecialSize() == 0) {
                    pc.set_SpecialSize(20);
                    pc.getInventory().consumeItem(89136, 1);
                    pc.sendPackets(new S_ServerMessage(1624, "20"));
                } else if (pc.get_SpecialSize() == 20) {
                    pc.set_SpecialSize(40);
                    pc.getInventory().consumeItem(89136, 1);
                    pc.sendPackets(new S_ServerMessage(1624, "40"));
                }
                break;
            case 51093:
            case 51094:
            case 51095:
            case 51096:
            case 51097:
            case 51098:
            case 51099:
            case 51100:
                //クラス変更ポーション
                if (pc.getClanid() != 0) {
                    pc.sendPackets(new S_ChatPacket(pc, "血盟をまず脱退してください。"));
                    return;
                } else if (itemId == 51093 && pc.getType() == 0) { // あなた君主？
                    pc.sendPackets(new S_ChatPacket(pc, "あなたは既に君主クラスです。"));
                    return;
                } else if (itemId == 51094 && pc.getType() == 1) { // あなたのナイト？
                    pc.sendPackets(new S_ChatPacket(pc, "あなたは既にナイトクラスです。"));
                    return;
                } else if (itemId == 51095 && pc.getType() == 2) { // あなたの妖精？
                    pc.sendPackets(new S_ChatPacket(pc, "あなたは既にエルフクラスです。"));
                    return;
                } else if (itemId == 51096 && pc.getType() == 3) { // あなたのウィザード？
                    pc.sendPackets(new S_ChatPacket(pc, "あなたは既にウィザードクラスです。"));
                    return;
                } else if (itemId == 51097 && pc.getType() == 4) { // あなたダークエルフ？
                    pc.sendPackets(new S_ChatPacket(pc, "あなたは既にダークエルフクラスです。"));
                    return;
                } else if (itemId == 51098 && pc.getType() == 5) { // あなたのナイト？
                    pc.sendPackets(new S_ChatPacket(pc, "あなたは既に用のナイトのクラスです。"));
                    return;
                } else if (itemId == 51099 && pc.getType() == 6) { // あなたイリュージョニスト？
                    pc.sendPackets(new S_ChatPacket(pc, "あなたは既にイリュージョニストクラスです。"));
                    return;
                } else if (itemId == 51100 && pc.getType() == 7) { // あなたイリュージョニスト？
                    pc.sendPackets(new S_ChatPacket(pc, "あなたは既に戦士クラスです。"));
                    return;
                }
                int[] Mclass = new int[]{0, 61, 138, 734, 2786, 6658, 6671, 12490};
                int[] Wclass = new int[]{1, 48, 37, 1186, 2796, 6661, 6650, 12494};
                if (itemId == 51093 && pc.getType() != 0 && pc.get_sex() == 0) {
                    pc.setType(0);
                    pc.setClassId(Mclass[pc.getType()]);
                } else if (itemId == 51093 && pc.getType() != 0 && pc.get_sex() == 1) {//君主
                    pc.setType(0);
                    pc.setClassId(Wclass[pc.getType()]);
                } else if (itemId == 51094 && pc.getType() != 1 && pc.get_sex() == 0) { // 変更：ナイト
                    pc.setType(1);
                    pc.setClassId(Mclass[pc.getType()]);
                } else if (itemId == 51094 && pc.getType() != 1 && pc.get_sex() == 1) {
                    pc.setType(1);
                    pc.setClassId(Wclass[pc.getType()]);
                } else if (itemId == 51095 && pc.getType() != 2 && pc.get_sex() == 0) { // 変更：妖精
                    pc.setType(2);
                    pc.setClassId(Mclass[pc.getType()]);
                } else if (itemId == 51095 && pc.getType() != 2 && pc.get_sex() == 1) {
                    pc.setType(2);
                    pc.setClassId(Wclass[pc.getType()]);
                } else if (itemId == 51096 && pc.getType() != 3 && pc.get_sex() == 0) { // 変更：ウィザード
                    pc.setType(3);
                    pc.setClassId(Mclass[pc.getType()]);
                } else if (itemId == 51096 && pc.getType() != 3 && pc.get_sex() == 1) {
                    pc.setType(3);
                    pc.setClassId(Wclass[pc.getType()]);
                } else if (itemId == 51097 && pc.getType() != 4 && pc.get_sex() == 0) { // 変更：ダークエルフ
                    pc.setType(4);
                    pc.setClassId(Mclass[pc.getType()]);
                } else if (itemId == 51097 && pc.getType() != 4 && pc.get_sex() == 1) {
                    pc.setType(4);
                    pc.setClassId(Wclass[pc.getType()]);
                } else if (itemId == 51098 && pc.getType() != 5 && pc.get_sex() == 0) { // 変更：竜騎士
                    pc.setType(5);
                    pc.setClassId(Mclass[pc.getType()]);
                } else if (itemId == 51098 && pc.getType() != 5 && pc.get_sex() == 1) {
                    pc.setType(5);
                    pc.setClassId(Wclass[pc.getType()]);
                } else if (itemId == 51099 && pc.getType() != 6 && pc.get_sex() == 0) { // 変更：イリュージョニスト
                    pc.setType(6);
                    pc.setClassId(Mclass[pc.getType()]);
                } else if (itemId == 51099 && pc.getType() != 6 && pc.get_sex() == 1) {
                    pc.setType(6);
                    pc.setClassId(Wclass[pc.getType()]);
                } else if (itemId == 51100 && pc.getType() != 7 && pc.get_sex() == 0) { // 変更：戦士
                    pc.setType(7);
                    pc.setClassId(Mclass[pc.getType()]);
                } else if (itemId == 51100 && pc.getType() != 7 && pc.get_sex() == 1) {
                    pc.setType(7);
                    pc.setClassId(Wclass[pc.getType()]);
                }
                if (pc.getWeapon() != null)
                    pc.getInventory().setEquipped(pc.getWeapon(), false, false, false, false);
                pc.getInventory().takeoffEquip(945);
                pc.sendPackets(new S_CharVisualUpdate(pc));
                for (L1ItemInstance armor : pc.getInventory().getItems()) {
                    for (int type = 0; type <= 12; type++) {
                        if (armor != null) {
                            pc.getInventory().setEquipped(armor, false, false, false, false);
                        }
                    }
                }
                pc.sendPackets(new S_DelSkill(255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
                        255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255));
                deleteSpell(pc);
                pc.setTempCharGfx(pc.getClassId());
                pc.sendPackets(new S_ChangeShape(pc.getId(), pc.getClassId()));
                Broadcaster.broadcastPacket(pc, new S_ChangeShape(pc.getId(), pc.getClassId()));
                pc.getInventory().removeItem(l1iteminstance, 1);
                try {
                    pc.save();
                } catch (Exception e) {
                }
                pc.sendPackets(new S_SystemMessage("クラス変更に自動的に接続を終了します。"));
                new L1Teleport().teleport(pc, 32723 + _random.nextInt(10), 32851 + _random.nextInt(10), (short) 5166, 5, true);
                StatInitialize(pc);
                Thread.sleep(500);
                pc.sendPackets(new S_Disconnect());
                break;
        }
    }

    private void deleteSpell(L1PcInstance pc) {
        int player = pc.getId();
        Connection con = null;
        PreparedStatement pstm = null;
        try {

            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("DELETE FROM character_skills WHERE char_obj_id=?");
            pstm.setInt(1, player);
            pstm.execute();
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }


    public static void abyssDamageSkill(L1PcInstance pc) {
        pc.sendPackets(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Attack));
        Broadcaster.broadcastPacket(pc, new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Damage));
        if (pc.getPeerage() < 14) {
            pc.sendPackets(new S_SystemMessage("5星将校以上使用可能なアイテムです。"));
            return;
        }
        if (pc.hasSkillEffect(L1SkillId.ABYSS_LIGHTNING_TIME)) {
            int time = pc.getSkillEffectTimeSec(L1SkillId.ABYSS_LIGHTNING_TIME);
            pc.sendPackets(new S_SystemMessage("スキルディレイでの使用が停止されます。[残り時間：" + time + "秒]"));
            return;
        }
        if (pc.getZoneType() == 1) {
            pc.sendPackets(new S_SystemMessage("村では使用できません。"));
            return;
        }
        L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
        int dmg = 0;
        for (L1PcInstance tg : L1World.getInstance().getVisiblePlayer(pc)) {
            if (clan != null) { //血盟がある場合
                if (tg.getClanid() > 0) {
                    if (tg.getClanid() == pc.getClanid()) { //同じ血盟なら
                        break;
                    }
                }
            }
            if (tg.isInParty()) {// 同じパーティーメンバーであればダメージx
                if (tg.getParty().getLeader() == pc.getParty().getLeader()) {
                    break;
                }
            }
            if (tg.isDead()) {
                break;
            }
            if (tg.getId() == pc.getId()) {
                break;
            }
            tg.sendPackets(new S_SystemMessage("深淵の嵐にダメージを負った。"));
            pc.sendPackets(new S_SkillSound(tg.getId(), 10407));// /これ自己のに見える
            Broadcaster.broadcastPacket(pc, new S_SkillSound(tg.getId(), 10407));// これは、他の人も見る...
            pc.sendPackets(new S_DoActionGFX(tg.getId(), ActionCodes.ACTION_Damage));
            Broadcaster.broadcastPacket(pc, new S_DoActionGFX(tg.getId(), ActionCodes.ACTION_Damage));
            if (pc.getPeerage() == 18) {
                dmg = Config.SUPREMECOMMANDER_DAMAGE + _random.nextInt(100);
            } else if (pc.getPeerage() == 17) {
                dmg = Config.COMMANDER_DAMAGE + _random.nextInt(100);
            } else if (pc.getPeerage() == 16) {
                dmg = Config.IMPERATOR_DAMAGE + _random.nextInt(100);
            } else if (pc.getPeerage() == 15) {
                dmg = Config.GENERAL_DAMAGE + _random.nextInt(100);
            } else if (pc.getPeerage() == 14) {
                dmg = Config.STAR_FIVE_DAMAGE + _random.nextInt(100);
            }
            tg.receiveDamage(tg, dmg);
            pc.setSkillEffect(L1SkillId.ABYSS_LIGHTNING_TIME, 150000);
        }
        for (L1Object obj : L1World.getInstance().getVisibleObjects(pc)) {
            if (obj instanceof L1MonsterInstance) {
                L1NpcInstance npc = (L1NpcInstance) obj;
                if (npc.isDead()) {
                    break;
                }
                pc.sendPackets(new S_SkillSound(obj.getId(), 10407));// /これ自己のに見える
                Broadcaster.broadcastPacket(pc, new S_SkillSound(obj.getId(), 10407));// これは、他の人も見る...
                pc.sendPackets(new S_DoActionGFX(obj.getId(), ActionCodes.ACTION_Damage));
                Broadcaster.broadcastPacket(pc, new S_DoActionGFX(obj.getId(), ActionCodes.ACTION_Damage));
                if (pc.getPeerage() == 18) {
                    dmg = Config.SUPREMECOMMANDER_DAMAGE + _random.nextInt(100);
                } else if (pc.getPeerage() == 17) {
                    dmg = Config.COMMANDER_DAMAGE + _random.nextInt(100);
                } else if (pc.getPeerage() == 16) {
                    dmg = Config.IMPERATOR_DAMAGE + _random.nextInt(100);
                } else if (pc.getPeerage() == 15) {
                    dmg = Config.GENERAL_DAMAGE + _random.nextInt(100);
                } else if (pc.getPeerage() == 14) {
                    dmg = Config.STAR_FIVE_DAMAGE + _random.nextInt(100);
                }
                npc.receiveDamage(npc, dmg);
                pc.setSkillEffect(L1SkillId.ABYSS_LIGHTNING_TIME, 150000);
            }
        }
    }

    public static void abyssStunSkill(L1PcInstance pc) {
        if (pc.getAbysspoint() < Config.STAR_FIVE || pc.isGm()) {
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

            pc.sendPackets(new S_SystemMessage("広域スタンを使用します。"));
            pc.setSkillEffect(L1SkillId.DELAY, 300000);

            int actionId = ActionCodes.ACTION_SkillBuff;
            S_DoActionGFX gfx = new S_DoActionGFX(pc.getId(), actionId);
            pc.sendPackets(gfx);
            Broadcaster.broadcastPacket(pc, gfx);

            for (L1Object obj : L1World.getInstance().getVisibleObjects(pc, 10)) {
                Random random = new Random();
                int[] stunTimeArray = {2000, 2500, 3000, 3500, 4000};
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
                            if (!cha.hasSkillEffect(SHOCK_STUN) && !cha.hasSkillEffect(EARTH_BIND) && !cha.hasSkillEffect(ICE_LANCE)) {
                                L1EffectSpawn.getInstance().spawnEffect(81162, _shockStunDuration, target.getX(), target.getY(), target.getMapId());
                                target.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, true));
                                target.setSkillEffect(SHOCK_STUN, _shockStunDuration);
                                target.sendPackets(new S_SkillSound(target.getId(), 4434)); //スターン
                                Broadcaster.broadcastPacket(target, new S_SkillSound(target.getId(), 4434));
                            }
                        }
                    } else if (obj instanceof L1MonsterInstance || obj instanceof L1SummonInstance
                            || obj instanceof L1PetInstance) {
                        L1NpcInstance targetnpc = (L1NpcInstance) obj;
                        L1EffectSpawn.getInstance().spawnEffect(81162, _shockStunDuration, targetnpc.getX(), targetnpc.getY(),
                                targetnpc.getMapId());
                        targetnpc.setParalyzed(true);
                        targetnpc.setSkillEffect(SHOCK_STUN, _shockStunDuration);
                        Broadcaster.broadcastPacket(targetnpc, new S_SkillSound(obj.getId(), 4434));
                    }
                }
            }
        } else {
            pc.sendPackets(new S_SystemMessage("この技術は、5将校以上のみ使用が可能です。"));
        }
        System.currentTimeMillis();
        return;
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

    private boolean createNewItem2(L1PcInstance pc, int item_id, int count, int EnchantLevel) {
        L1ItemInstance item = ItemTable.getInstance().createItem(item_id);
        if (item != null) {
            item.setCount(count);
            item.setEnchantLevel(EnchantLevel);
            item.setIdentified(true);
            if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
                pc.getInventory().storeItem(item);
            } else { //持つことができない場合は、地面に落とす処理のキャンセルはしない（不正防止）
                L1World.getInstance().getInventory(pc.getX(), pc.getY(), pc.getMapId()).storeItem(item);
            }
            pc.sendPackets(new S_ServerMessage(403, item.getLogName())); // %0を
            // 手に
            //入れました。
            return true;
        } else {
            return false;
        }
    }

    private void cancelAbsoluteBarrier(L1PcInstance pc) { // アブ小ガルトバリアの解除
        if (pc.hasSkillEffect(ABSOLUTE_BARRIER)) {
            pc.killSkillEffectTimer(ABSOLUTE_BARRIER);
            //pc.startMpRegeneration();
            pc.startMpRegenerationByDoll();
        }
    }

    @Override
    public String getType() {
        return C_ITEM_USE2;
    }
}