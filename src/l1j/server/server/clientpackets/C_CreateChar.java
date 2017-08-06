package l1j.server.server.clientpackets;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.BadNamesList;
import l1j.server.server.GameClient;
import l1j.server.server.IdFactory;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.MonsterBookTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.datatables.WeekQuestTable;
import l1j.server.server.model.Beginner;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_AddSkill;
import l1j.server.server.serverpackets.S_CharCreateStatus;
import l1j.server.server.serverpackets.S_NewCharPacket;
import l1j.server.server.templates.L1Skills;
import manager.LinAllManagerInfoThread;

public class C_CreateChar extends ClientBasePacket {
    private static Logger _log = Logger.getLogger(C_CreateChar.class.getName());
    private static final String C_CREATE_CHAR = "[C] C_CreateChar";

    public C_CreateChar(byte[] abyte0, GameClient client) throws Exception {
        super(abyte0);
        L1PcInstance pc = new L1PcInstance();

        String name = readS();

        Calendar cal = Calendar.getInstance();
        int hour = Calendar.HOUR;
        int minute = Calendar.MINUTE;
        /** 0時、1午後* */
        String ampm = "午後";
        if (cal.get(Calendar.AM_PM) == 0) {
            ampm = "午前";
        }

        if (name.length() <= 0) { // 一字名途方もなく修正
            // 英語は、ハングルショ糖1の長さは、ハングルはハングルのショ糖2の長さ
            S_CharCreateStatus s_charcreatestatus = new S_CharCreateStatus(S_CharCreateStatus.REASON_INVALID_NAME);
            client.sendPacket(s_charcreatestatus);
            return;
        }

        if (BadNamesList.getInstance().isBadName(name)) {
            S_CharCreateStatus s_charcreatestatus = new S_CharCreateStatus(S_CharCreateStatus.REASON_INVALID_NAME);
            // _log.info（「生成禁止されたキャラクターの名前、作成に失敗 "）;
            client.sendPacket(s_charcreatestatus);
            return;
        }


        if (isInvalidName(name)) {
            S_CharCreateStatus s_charcreatestatus = new S_CharCreateStatus(S_CharCreateStatus.REASON_INVALID_NAME);
            client.sendPacket(s_charcreatestatus);
            return;
        }
        /**ロボットの名前の重複 **/
        if (CharacterTable.RobotNameExist(name) || L1World.getInstance().getPlayer(name) != null) {
            _log.fine("charname: " + pc.getName() + " already exists. creation failed.");
            S_CharCreateStatus s_charcreatestatus1 = new S_CharCreateStatus(S_CharCreateStatus.REASON_ALREADY_EXSISTS);
            client.sendPacket(s_charcreatestatus1);
            return;
        }
        if (CharacterTable.RobotCrownNameExist(name) || L1World.getInstance().getPlayer(name) != null) {
            _log.fine("charname: " + pc.getName() + " already exists. creation failed.");
            S_CharCreateStatus s_charcreatestatus1 = new S_CharCreateStatus(S_CharCreateStatus.REASON_ALREADY_EXSISTS);
            client.sendPacket(s_charcreatestatus1);
            return;
        }
        if (CharacterTable.doesCharNameExist(name) || L1World.getInstance().getPlayer(name) != null) {
            _log.fine("charname: " + pc.getName() + " already exists. creation failed.");
            S_CharCreateStatus s_charcreatestatus1 = new S_CharCreateStatus(S_CharCreateStatus.REASON_ALREADY_EXSISTS);
            client.sendPacket(s_charcreatestatus1);
            return;
        }

        if (client.getAccount().countCharacters() >= 8) {
            // _log.fine("account: " + client.getAccountName() + " 8を超えるキャラクター作成要求。 "）;
            S_CharCreateStatus s_charcreatestatus1 = new S_CharCreateStatus(S_CharCreateStatus.REASON_WRONG_AMOUNT);
            client.sendPacket(s_charcreatestatus1);
            return;
        }

        pc.setName(name);
        pc.setType(readC());
        pc.set_sex(readC());
        pc.getAbility().setBaseStr((byte) readC());
        pc.getAbility().setBaseDex((byte) readC());
        pc.getAbility().setBaseCon((byte) readC());
        pc.getAbility().setBaseWis((byte) readC());
        pc.getAbility().setBaseCha((byte) readC());
        pc.getAbility().setBaseInt((byte) readC());

        int statusAmount = pc.getAbility().getAmount();

        if (pc.getAbility().getBaseStr() > 20 || pc.getAbility().getBaseDex() > 20 || pc.getAbility().getBaseCon() > 20
                || pc.getAbility().getBaseWis() > 20 || pc.getAbility().getBaseCha() > 20 || pc.getAbility().getBaseInt() > 20
                || statusAmount != 75) {
            _log.finest("Character have wrong value");
            S_CharCreateStatus s_charcreatestatus3 = new S_CharCreateStatus(S_CharCreateStatus.REASON_WRONG_AMOUNT);
            client.sendPacket(s_charcreatestatus3);
            return;
        }

        _log.fine("charname: " + pc.getName() + " classId: " + pc.getClassId());
        S_CharCreateStatus s_charcreatestatus2 = new S_CharCreateStatus(S_CharCreateStatus.REASON_OK);
        client.sendPacket(s_charcreatestatus2);
        initNewChar(client, pc);
        System.out.println("" + ampm + " " + cal.get(hour) + "時" + cal.get(minute) + "分" + "■新規キャラクター：[" + pc.getName() + "]さんの作成完了■");
        LinAllManagerInfoThread.CharCount += 1;
    }

    // by.lins
    public static final int[] MALE_LIST = new int[] { 0, 61, 138, 734, 2786, 6658, 6671, 12490 };
    public static final int[] FEMALE_LIST = new int[] { 1, 48, 37, 1186, 2796, 6661, 6650, 12494 };
    // by.lins

    public static final int[][] START_LOC_X = new int[][] { { 33434, 33435, 33440, 33424, 33415 } };// リニューアルスムギェ統合X
    public static final int[][] START_LOC_Y = new int[][] { { 32815, 32823, 32797, 32813, 32824 } };//リニューアルスムギェ統合Y

    public static final short[] MAPID_LIST = new short[] { 4, 4, 4, 4, 4, 4, 4, 4 };//リニューアルスムギェ

    private static void initNewChar(GameClient client, L1PcInstance pc) throws IOException, Exception {
        short init_hp = 0, init_mp = 0;
        Random random = new Random();
        final int NewHi = 0; // スムギェ

        int startPosType = NewHi; // defalut
        int startPos = random.nextInt(5);

        pc.setId(IdFactory.getInstance().nextId());

        if (pc.get_sex() == 0)
            pc.setClassId(MALE_LIST[pc.getType()]);
        else
            pc.setClassId(FEMALE_LIST[pc.getType()]);

        if (pc.isCrown()) { // CROWN
            init_hp = 14;
            switch (pc.getAbility().getBaseWis()) {
                case 11:
                    init_mp = 2;
                    break;
                case 12:
                case 13:
                case 14:
                case 15:
                    init_mp = 3;
                    break;
                case 16:
                case 17:
                case 18:
                    init_mp = 4;
                    break;
                default:
                    init_mp = 2;
                    break;
            }
            startPosType = NewHi;
        } else if (pc.isKnight()) { // KNIGHT
            init_hp = 16;
            switch (pc.getAbility().getBaseWis()) {
                case 9:
                case 10:
                case 11:
                    init_mp = 1;
                    break;
                case 12:
                case 13:
                    init_mp = 2;
                    break;
                default:
                    init_mp = 1;
                    break;
            }
            startPosType = NewHi;
        } else if (pc.isElf()) { // ELF
            init_hp = 15;
            switch (pc.getAbility().getBaseWis()) {
                case 12:
                case 13:
                case 14:
                case 15:
                    init_mp = 4;
                    break;
                case 16:
                case 17:
                case 18:
                    init_mp = 6;
                    break;
                default:
                    init_mp = 4;
                    break;
            }
            startPosType = NewHi;
        } else if (pc.isWizard()) { // WIZ
            init_hp = 12;
            switch (pc.getAbility().getBaseWis()) {
                case 12:
                case 13:
                case 14:
                case 15:
                    init_mp = 6;
                    break;
                case 16:
                case 17:
                case 18:
                    init_mp = 8;
                    break;
                default:
                    init_mp = 6;
                    break;
            }
            startPosType = NewHi;
        } else if (pc.isDarkelf()) { // DE
            init_hp = 12;
            switch (pc.getAbility().getBaseWis()) {
                case 10:
                case 11:
                    init_mp = 3;
                    break;
                case 12:
                case 13:
                case 14:
                case 15:
                    init_mp = 4;
                    break;
                case 16:
                case 17:
                case 18:
                    init_mp = 6;
                    break;
                default:
                    init_mp = 3;
                    break;
            }
            startPosType = NewHi;
        } else if (pc.isDragonknight()) { // 竜騎士
            init_hp = 16;
            init_mp = 2;
            startPosType = NewHi;
        } else if (pc.isBlackwizard()) { // イリュージョニスト
            init_hp = 14;
            switch (pc.getAbility().getBaseWis()) {
                case 12:
                case 13:
                case 14:
                case 15:
                    init_mp = 5;
                    break;
                case 16:
                case 17:
                case 18:
                    init_mp = 6;
                    break;
                default:
                    init_mp = 5;
                    break;
            }
            startPosType = NewHi;
            // by.lins
        } else if (pc.isWarrior()) {
            init_hp = 16;
            switch (pc.getAbility().getBaseWis()) {
                case 9:
                case 10:
                case 11:
                    init_mp = 1;
                    break;
                case 12:
                case 13:
                    init_mp = 2;
                    break;
                default:
                    init_mp = 1;
                    break;
            }
            startPosType = NewHi;
        }
        // by.lins
        /*pc.setX(START_LOC_X[startPosType][startPos]);
        pc.setY(START_LOC_Y[startPosType][startPos]);
		pc.setMap(MAPID_LIST[pc.getType()]);*/
        //クラウディアに位置移動
        pc.setX(32772);
        pc.setY(32819);
        pc.setMap((short) 7783);

        pc.setHeading(0);
        pc.setLawful(0);

        pc.addBaseMaxHp(init_hp);
        pc.setCurrentHp(init_hp);
        pc.addBaseMaxMp(init_mp);
        pc.setCurrentMp(init_mp);
        pc.resetBaseAc();
        pc.setTitle("");
        pc.setClanid(0);
        pc.setClanRank(0);
        pc.set_food(39); // 17%
        pc.setAccessLevel((short) 0);
        pc.setGm(false);
        pc.setMonitor(false);
        pc.setGmInvis(false);
        pc.setExp(0);// 経験値0
        pc.setHighLevel(1);
        pc.setStatus(0);
        pc.setAccessLevel((short) 0);
        pc.setClanname("");
        pc.setClanMemberNotes("");
        pc.setBonusStats(0);
        pc.setElixirStats(0);
        pc.resetBaseMr();
        pc.setElfAttr(0);
        pc.set_PKcount(0);
        pc.setExpRes(0);
        pc.setPartnerId(0);
        pc.setOnlineStatus(0);
        pc.setHomeTownId(0);
        pc.setContribution(0);
        pc.setBanned(false);
        pc.setKarma(0);
        pc.setReturnStat(0);
        pc.setGirandungeonTime(0);//ギラン監獄
        pc.setOrendungeonTime(0);//ヤヒ
        pc.setDrageonTime(0);//DVC
        //pc.setRadungeonTime(0);//ラバー
        pc.setSomeTime(0);//モンソム
        pc.setSoulTime(0);//ゴム
        pc.setnewdodungeonTime(0);//バルログ陣営
        pc.seticedungeonTime(0);//オルドン
        pc.setislandTime(0);//巻い
        pc.setMark_count(60);
        pc.setEinhasad(2000000);

        /** 修正新規血盟タイトルの自動 **/
        /*
		 * pc.addBaseMaxHp(init_hp); pc.setCurrentHp(init_hp); pc.addBaseMaxMp(init_mp); pc.setCurrentMp(init_mp); pc.resetBaseAc();
		 * pc.setTitle("\\f:新規保護血盟 "）; pc.setClanid（1）; pc.setClanRank（L1Clan修煉）; pc.set_food（39）; // 17％pc.setAccessLevel（（short）0）;
		 * pc.setGm(false); pc.setMonitor（false）; pc.setGmInvis（false）; pc.setExp（0）; //経験値0 pc.setHighLevel（1）; pc.setStatus（0）;
		 * pc.setAccessLevel((short) 0）; pc.setClanname（「新規保護血盟 "）; pc.setClanMemberNotes（ ""）; pc.setBonusStats（0）; pc.setElixirStats（0）;
		 * pc.resetBaseMr(); pc.setElfAttr(0); pc.set_PKcount(0); pc.setExpRes(0); pc.setPartnerId(0); pc.setOnlineStatus(0); pc.setHomeTownId(0);
		 * pc.setContribution(0); pc.setBanned(false); pc.setKarma(0); pc.setReturnStat(0); pc.setGirandungeonTime(0); pc.setOrendungeonTime(0);
		 * pc.setDrageonTime(0); pc.setRadungeonTime(0); pc.setSomeTime(0); pc.setSoulTime(0); pc.setMark_count(60); pc.setEinhasad(2000000);
		 */

        Calendar local_c = Calendar.getInstance();
        SimpleDateFormat local_sdf = new SimpleDateFormat("yyyyMMdd");
        local_c.setTimeInMillis(System.currentTimeMillis());
        pc.setBirthDay(Integer.parseInt(local_sdf.format(local_c.getTime())));
        if (pc.isWizard()) { // WIZ
            pc.sendPackets(new S_AddSkill(3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
            int object_id = pc.getId();
            L1Skills l1skills = SkillsTable.getInstance().getTemplate(4); // EB
            String skill_name = l1skills.getName();
            int skill_id = l1skills.getSkillId();
            SkillsTable.getInstance().spellMastery(object_id, skill_id, skill_name, 0, 0); // DBに登録
        }
        if (pc.isElf()) { // 妖精テルレポーターツーマザーキャラクター作成時に追加
            pc.sendPackets(new S_AddSkill(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, pc.getElfAttr()));
            int object_id = pc.getId();
            L1Skills l1skills = SkillsTable.getInstance().getTemplate(131); // テレパスパルトゥーマザー
            String skill_name = l1skills.getName();
            int skill_id = l1skills.getSkillId();
            SkillsTable.getInstance().spellMastery(object_id, skill_id, skill_name, 0, 0);
        }
        Beginner.getInstance().GiveItem(pc);
        Beginner.getInstance().writeBookmark(pc);
        pc.setAccountName(client.getAccountName());
        CharacterTable.getInstance().storeNewCharacter(pc);
        S_NewCharPacket s_newcharpacket = new S_NewCharPacket(pc);
        client.sendPacket(s_newcharpacket);
        MonsterBookTable.getInstace().createMonsterBookList(pc.getId());
        for (int i = 0; i < 9; i++)
            pc.setWcount(0);
        pc.setQuestWeek(0);
        for (int i = 0; i < 3; i++)
            pc.setReward(i, false);
        WeekQuestTable.getInstance().CreateQuestData(pc.getName());
        pc.save();
        pc.refresh();
    }

    private static boolean isAlphaNumeric(String s) {
        if (s == null) {
            return false;
        }
        boolean flag = true;
        char ac[] = s.toCharArray();
        int i = 0;
        do {
            if (i >= ac.length) {
                break;
            }
            if (!Character.isLetterOrDigit(ac[i])) {
                flag = false;
                break;
            }
            i++;
        } while (true);
        return flag;
    }

    private static boolean isInvalidName(String name) {
        int numOfNameBytes = 0;
        try {
            numOfNameBytes = name.getBytes("MS932").length;
        } catch (UnsupportedEncodingException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            return false;
        }

        if (isAlphaNumeric(name)) {
            return false;
        }

        // XXX -本庁の仕様と同等未確認
        // 全角文字が5文字を超えるか、全体に12バイトを超えると無効な名前である
        if (5 < (numOfNameBytes - name.length()) || 12 < numOfNameBytes) {
            return false;
        }

        if (BadNamesList.getInstance().isBadName(name)) {
            return false;
        }
        return true;
    }

    @Override
    public String getType() {
        return C_CREATE_CHAR;
    }
}
