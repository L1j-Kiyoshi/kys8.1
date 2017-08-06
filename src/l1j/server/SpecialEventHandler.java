package l1j.server;

import static l1j.server.server.model.skill.L1SkillId.*;

import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;

enum SpecialEvent {BugRace, AllBuf, InfinityFight, DoNotChatEveryone, DoChatEveryone};

// ゲーム内、全体のイベントの処理を担当
public class SpecialEventHandler {

    private static volatile SpecialEventHandler uniqueInstance = null;

//	private boolean CheckBugrace = false;

    private SpecialEventHandler() {
    }

    public static SpecialEventHandler getInstance() {
        if (uniqueInstance == null) {
            synchronized (SpecialEventHandler.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new SpecialEventHandler();
                }
            }
        }

        return uniqueInstance;
    }

    public void doGiveEventStaff() {
        for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
            if (pc.getNetConnection() != null) {
                pc.getInventory().storeItem(30105, 3);
                L1ItemInstance item = ItemTable.getInstance().createItem(30105); //クリスマスのギフトボックス
                pc.sendPackets(new S_ChatPacket(pc, "■ 全体プレゼント ■: \\aA[" + item.getLogName() + "]が到着しました。"));

            }
        }
    }

    public void buff_ALL() {
        int[] allBuffSkill = { PHYSICAL_ENCHANT_DEX, PHYSICAL_ENCHANT_STR, BLESS_WEAPON, IRON_SKIN, FEATHER_BUFF_A, LIFE_MAAN, God_buff, COMA_B };
        L1SkillUse l1skilluse = null;
        for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
            if (pc == null || pc.isPrivateShop() || pc.getMapId() == 5166 || pc.isAutoClanjoin()) {
                continue;
            }
            if (pc.noPlayerCK && !pc.noPlayerRobot) {
                continue;
            }
            l1skilluse = new L1SkillUse();
            for (int i = 0; i < allBuffSkill.length; i++) {
                l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
            }
            pc.sendPackets(new S_SkillSound(pc.getId(), 4856));
            pc.sendPackets(new S_ChatPacket(pc, "\\aA通知：ゲームマスターが「全体バフ」を提供していました。"));
        }
    }

    public void buff_ALL(L1PcInstance pc) {
        int[] allBuffSkill = { PHYSICAL_ENCHANT_DEX, PHYSICAL_ENCHANT_STR, BLESS_WEAPON, IRON_SKIN, FEATHER_BUFF_A, LIFE_MAAN, God_buff, COMA_B };
        L1SkillUse l1skilluse = null;
        if (pc == null || pc.isPrivateShop() || pc.getMapId() == 5166 || pc.isAutoClanjoin()) {
            return;
        }
        if (pc.noPlayerCK && !pc.noPlayerRobot) {
            return;
        }
        l1skilluse = new L1SkillUse();
        for (int i = 0; i < allBuffSkill.length; i++) {
            l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
        }
        pc.sendPackets(new S_SkillSound(pc.getId(), 4856));

    }

    public void buff_ScreenFull(L1PcInstance gm) { //画面の中のユーザーズにフルバフ
        int[] allBuffSkill = { PHYSICAL_ENCHANT_DEX, PHYSICAL_ENCHANT_STR, BLESS_WEAPON, IRON_SKIN, NATURES_TOUCH, ADDITIONAL_FIRE, INSIGHT, DRAGON_SKIN };
        L1SkillUse l1skilluse = null;
        for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(gm, 30)) {
            if (pc.isPrivateShop() || pc.isAutoClanjoin()) {
                continue;
            }
            l1skilluse = new L1SkillUse();
            for (int i = 0; i < allBuffSkill.length; i++) {
                l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
            }
            pc.sendPackets(new S_ChatPacket(pc, "\\aA通知：ゲームマスターの周りに「バフ」が提供ハオトた。"));
        }
    }

    public void buff_ScreenMetis(L1PcInstance gm) {
        int[] allBuffSkill = { FEATHER_BUFF_A };
        L1SkillUse l1skilluse = null;
        for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(gm, 30)) {
            if (pc.isPrivateShop() || pc.isAutoClanjoin()) {
                continue;
            }
            if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_A)) {
                pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_A);
            }
            if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_B)) {
                pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_B);
            }
            if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_C)) {
                pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_C);
            }
            if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_D)) {
                pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_D);
            }
            l1skilluse = new L1SkillUse();
            for (int i = 0; i < allBuffSkill.length; i++) {
                l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
            }
            pc.sendPackets(new S_SkillSound(pc.getId(), 4856));
            pc.sendPackets(new S_ChatPacket(pc, "\\aA通知：ゲームマスターの周りに「メティスの祝福」を提供していました。"));
        }
    }

    public void buff_EvilEye(L1PcInstance gm) {
        int[] allBuffSkill = { 7678 };
        L1SkillUse l1skilluse = null;
        for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(gm, 30)) {
            if (pc.isPrivateShop() || pc.isAutoClanjoin()) {
                continue;
            }
            if (pc.hasSkillEffect(7671)) {
                pc.removeSkillEffect(7671);
            }
            if (pc.hasSkillEffect(7672)) {
                pc.removeSkillEffect(7672);
            }
            if (pc.hasSkillEffect(7673)) {
                pc.removeSkillEffect(7673);
            }
            if (pc.hasSkillEffect(7674)) {
                pc.removeSkillEffect(7674);
            }
            if (pc.hasSkillEffect(7675)) {
                pc.removeSkillEffect(7675);
            }
            if (pc.hasSkillEffect(7676)) {
                pc.removeSkillEffect(7676);
            }
            if (pc.hasSkillEffect(7678)) {
                pc.removeSkillEffect(7677);
            }
            l1skilluse = new L1SkillUse();
            for (int i = 0; i < allBuffSkill.length; i++) {
                l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
            }
            pc.sendPackets(new S_SkillSound(pc.getId(), 4856));
            pc.sendPackets(new S_ChatPacket(pc, "\\aA通知：ゲームマスターの周りに「生命の魔眼」を提供していました。"));
        }
    }

    public void buff_God(L1PcInstance gm) {
        int[] allBuffSkill = { God_buff };
        L1SkillUse l1skilluse = null;
        for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(gm, 30)) {
            if (pc.isPrivateShop() || pc.isAutoClanjoin()) {
                continue;
            }
            if (pc.hasSkillEffect(L1SkillId.God_buff)) {
                pc.removeSkillEffect(L1SkillId.God_buff);
            }
            l1skilluse = new L1SkillUse();
            for (int i = 0; i < allBuffSkill.length; i++) {
                l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
            }
            pc.sendPackets(new S_SkillSound(pc.getId(), 4856));
            pc.sendPackets(new S_ChatPacket(pc, "\\aA通知：ゲームマスターの周りに「黒砂ののバフ」を提供していました。"));
        }
    }

    public void buff_ScreenCOMA(L1PcInstance gm) {
        int[] allBuffSkill = { COMA_B };
        L1SkillUse l1skilluse = null;
        for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(gm, 30)) {
            if (pc.isPrivateShop() || pc.isAutoClanjoin()) {
                continue;
            }
            if (pc.hasSkillEffect(L1SkillId.COMA_A)) { // コマ3個
                pc.removeSkillEffect(L1SkillId.COMA_A);
            }
            if (pc.hasSkillEffect(L1SkillId.COMA_B)) { // コマ5個
                pc.removeSkillEffect(L1SkillId.COMA_B);
            }

            l1skilluse = new L1SkillUse();
            for (int i = 0; i < allBuffSkill.length; i++) {
                l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
            }
            pc.sendPackets(new S_SkillSound(pc.getId(), 4856));
            pc.sendPackets(new S_ChatPacket(pc, "\\aA通知：ゲームマスターの周りに「コマバフ」を提供していました。"));
        }
    }

    public void buff_ALLFull() {
        int[] allBuffSkill = { DECREASE_WEIGHT, PHYSICAL_ENCHANT_DEX,
                PHYSICAL_ENCHANT_STR, BLESS_WEAPON, BERSERKERS,
                IMMUNE_TO_HARM, REDUCTION_ARMOR, BOUNCE_ATTACK,
                SOLID_CARRIAGE, BURNING_SPIRIT, DOUBLE_BRAKE, UNCANNY_DODGE, DRESS_EVASION,
                GLOWING_AURA, BRAVE_AURA, RESIST_MAGIC, CLEAR_MIND, ELEMENTAL_PROTECTION,
                AQUA_PROTECTER, BURNING_WEAPON, IRON_SKIN, EXOTIC_VITALIZE,
                WATER_LIFE, ELEMENTAL_FIRE, SOUL_OF_FLAME, ADDITIONAL_FIRE };
        L1SkillUse l1skilluse = null;
        for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
            if (pc == null || pc.isPrivateShop() || pc.getMapId() == 5166 || pc.isAutoClanjoin()) {
                continue;
            }
            if (pc.noPlayerCK && !pc.noPlayerRobot) {
                continue;
            }
            pc.setBuffnoch(1);
            l1skilluse = new L1SkillUse();
            for (int i = 0; i < allBuffSkill.length; i++) {
                l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
            }
            pc.setBuffnoch(0);
            pc.sendPackets(new S_SkillSound(pc.getId(), 4856));
            pc.sendPackets(new S_ChatPacket(pc, "\\aA通知：ゲームマスターが「バフ」を提供していました。"));
        }
    }

    public void buff_ALLMetis() {
        int[] allBuffSkill = { FEATHER_BUFF_A };
        L1SkillUse l1skilluse = null;
        for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
            if (pc == null || pc.isPrivateShop() || pc.getMapId() == 5166 || pc.isAutoClanjoin()) {
                continue;
            }
            if (pc.noPlayerCK && !pc.noPlayerRobot) {
                continue;
            }
            if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_A)) {
                pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_A);
            }
            if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_B)) {
                pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_B);
            }
            if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_C)) {
                pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_C);
            }
            if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_D)) {
                pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_D);
            }
            l1skilluse = new L1SkillUse();
            for (int i = 0; i < allBuffSkill.length; i++) {
                l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
            }
            pc.sendPackets(new S_SkillSound(pc.getId(), 4856));
            pc.sendPackets(new S_ChatPacket(pc, "\\aA通知：ゲームマスターが「メティスの祝福」を提供していました。"));
        }
    }

    public void buff_ALLEvilEye() {
        int[] allBuffSkill = { 7678 };
        L1SkillUse l1skilluse = null;
        for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
            if (pc == null || pc.isPrivateShop() || pc.getMapId() == 5166 || pc.isAutoClanjoin()) {
                continue;
            }
            if (pc.noPlayerCK && !pc.noPlayerRobot) {
                continue;
            }
            if (pc.hasSkillEffect(7671)) {
                pc.removeSkillEffect(7671);
            }
            if (pc.hasSkillEffect(7672)) {
                pc.removeSkillEffect(7672);
            }
            if (pc.hasSkillEffect(7673)) {
                pc.removeSkillEffect(7673);
            }
            if (pc.hasSkillEffect(7674)) {
                pc.removeSkillEffect(7674);
            }
            if (pc.hasSkillEffect(7675)) {
                pc.removeSkillEffect(7675);
            }
            if (pc.hasSkillEffect(7676)) {
                pc.removeSkillEffect(7676);
            }
            if (pc.hasSkillEffect(7678)) {
                pc.removeSkillEffect(7677);
            }
            l1skilluse = new L1SkillUse();
            for (int i = 0; i < allBuffSkill.length; i++) {
                l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
            }
            pc.sendPackets(new S_SkillSound(pc.getId(), 4856));
            pc.sendPackets(new S_ChatPacket(pc, "\\aA通知：ゲームマスターが「生命の魔眼」を提供していました。"));
        }
    }

    public void buff_ALLGod() {
        int[] allBuffSkill = { God_buff };
        L1SkillUse l1skilluse = null;
        for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
            if (pc == null || pc.isPrivateShop() || pc.getMapId() == 5166 || pc.isAutoClanjoin()) {
                continue;
            }
            if (pc.noPlayerCK && !pc.noPlayerRobot) {
                continue;
            }
            if (pc.hasSkillEffect(L1SkillId.God_buff)) {
                pc.removeSkillEffect(L1SkillId.God_buff);
            }
            l1skilluse = new L1SkillUse();
            for (int i = 0; i < allBuffSkill.length; i++) {
                l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
            }
            pc.sendPackets(new S_SkillSound(pc.getId(), 4856));
            pc.sendPackets(new S_ChatPacket(pc, "\\aA通知：ゲームマスターが「黒砂ののバフ」を提供していました。"));
        }
    }

    public void buff_ALLCOMA() {
        int[] allBuffSkill = { COMA_B };
        L1SkillUse l1skilluse = null;
        for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
            if (pc == null || pc.isPrivateShop() || pc.getMapId() == 5166 || pc.isAutoClanjoin()) {
                continue;
            }
            if (pc.noPlayerCK && !pc.noPlayerRobot) {
                continue;
            }
            if (pc.hasSkillEffect(L1SkillId.COMA_A)) { // コマ3個
                pc.removeSkillEffect(L1SkillId.COMA_A);
            }
            if (pc.hasSkillEffect(L1SkillId.COMA_B)) { // コマ5個
                pc.removeSkillEffect(L1SkillId.COMA_B);
            }

            l1skilluse = new L1SkillUse();
            for (int i = 0; i < allBuffSkill.length; i++) {
                l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
            }
            pc.sendPackets(new S_SkillSound(pc.getId(), 4856));
            pc.sendPackets(new S_ChatPacket(pc, "\\aA通知：ゲームマスターが「コマバフ」を提供していました。"));
        }
    }

    public void doNotChatEveryone() {
        L1World.getInstance().set_worldChatElabled(false);
        L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("\\aG警告： \\aAワールドチャットを無効に開始"));
    }

    public void doChatEveryone() {
        L1World.getInstance().set_worldChatElabled(true);
        L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("\\aG警告： \\aAワールドチャットを有効に開始"));
    }


}
