package l1j.server;

import static l1j.server.server.model.skill.L1SkillId.ADDITIONAL_FIRE;
import static l1j.server.server.model.skill.L1SkillId.AQUA_PROTECTER;
import static l1j.server.server.model.skill.L1SkillId.BERSERKERS;
import static l1j.server.server.model.skill.L1SkillId.BLESS_WEAPON;
import static l1j.server.server.model.skill.L1SkillId.BOUNCE_ATTACK;
import static l1j.server.server.model.skill.L1SkillId.BRAVE_AURA;
import static l1j.server.server.model.skill.L1SkillId.BURNING_SPIRIT;
import static l1j.server.server.model.skill.L1SkillId.BURNING_WEAPON;
import static l1j.server.server.model.skill.L1SkillId.CLEAR_MIND;
import static l1j.server.server.model.skill.L1SkillId.COMA_B;
import static l1j.server.server.model.skill.L1SkillId.DECREASE_WEIGHT;
import static l1j.server.server.model.skill.L1SkillId.DOUBLE_BRAKE;
import static l1j.server.server.model.skill.L1SkillId.DRAGON_SKIN;
import static l1j.server.server.model.skill.L1SkillId.DRESS_EVASION;
import static l1j.server.server.model.skill.L1SkillId.ELEMENTAL_FIRE;
import static l1j.server.server.model.skill.L1SkillId.ELEMENTAL_PROTECTION;
import static l1j.server.server.model.skill.L1SkillId.EXOTIC_VITALIZE;
import static l1j.server.server.model.skill.L1SkillId.FEATHER_BUFF_A;
import static l1j.server.server.model.skill.L1SkillId.GLOWING_AURA;
import static l1j.server.server.model.skill.L1SkillId.God_buff;
import static l1j.server.server.model.skill.L1SkillId.IMMUNE_TO_HARM;
import static l1j.server.server.model.skill.L1SkillId.INSIGHT;
import static l1j.server.server.model.skill.L1SkillId.IRON_SKIN;
import static l1j.server.server.model.skill.L1SkillId.LIFE_MAAN;
import static l1j.server.server.model.skill.L1SkillId.NATURES_TOUCH;
import static l1j.server.server.model.skill.L1SkillId.PHYSICAL_ENCHANT_DEX;
import static l1j.server.server.model.skill.L1SkillId.PHYSICAL_ENCHANT_STR;
import static l1j.server.server.model.skill.L1SkillId.REDUCTION_ARMOR;
import static l1j.server.server.model.skill.L1SkillId.RESIST_MAGIC;
import static l1j.server.server.model.skill.L1SkillId.SOLID_CARRIAGE;
import static l1j.server.server.model.skill.L1SkillId.SOUL_OF_FLAME;
import static l1j.server.server.model.skill.L1SkillId.UNCANNY_DODGE;
import static l1j.server.server.model.skill.L1SkillId.WATER_LIFE;

import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;

enum SpecialEvent { BugRace, AllBuf, InfinityFight, DoNotChatEveryone, DoChatEveryone};

// 게임 내, 전체 이벤트에 대한 처리를 담당
public class SpecialEventHandler {

	private static volatile SpecialEventHandler uniqueInstance = null;

//	private boolean CheckBugrace = false;

	private SpecialEventHandler() {}

	public static SpecialEventHandler getInstance() {
		if(uniqueInstance == null) {
			synchronized (SpecialEventHandler.class) {
				if(uniqueInstance == null) {
					uniqueInstance = new SpecialEventHandler();
				}
			}
		}

		return uniqueInstance;
	}

	public void doGiveEventStaff() {
		for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			if( pc.getNetConnection() != null ) {
				pc.getInventory().storeItem(30105, 3);
				L1ItemInstance item = ItemTable.getInstance().createItem(30105); //크리스마스 선물상자
				pc.sendPackets(new S_ChatPacket(pc,"■ 전체선물 ■: \\aA[" + item.getLogName() + "]이 도착했습니다."));
	
			}
		}
	}
	public void 통합버프() {
		int[] allBuffSkill = {PHYSICAL_ENCHANT_DEX, PHYSICAL_ENCHANT_STR, BLESS_WEAPON, IRON_SKIN, FEATHER_BUFF_A, LIFE_MAAN, God_buff, COMA_B };
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
			pc.sendPackets(new S_ChatPacket(pc,"\\aA알림: 게임마스터께서 '전체 버프'를 제공하였습니다."));
		}
	}
	public void 통합버프(L1PcInstance pc) {
		int[] allBuffSkill = {PHYSICAL_ENCHANT_DEX, PHYSICAL_ENCHANT_STR, BLESS_WEAPON, IRON_SKIN, FEATHER_BUFF_A, LIFE_MAAN, God_buff, COMA_B };
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
	public void 화면풀업(L1PcInstance gm) { //화면 안의 유져에게 풀버프
		int[] allBuffSkill = {PHYSICAL_ENCHANT_DEX, PHYSICAL_ENCHANT_STR, BLESS_WEAPON, IRON_SKIN, NATURES_TOUCH, ADDITIONAL_FIRE, INSIGHT, DRAGON_SKIN };
			L1SkillUse l1skilluse = null;
			for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(gm, 30)) {
			if(pc.isPrivateShop() || pc.isAutoClanjoin()){
			continue;
			}
			l1skilluse = new L1SkillUse();
			for (int i = 0; i < allBuffSkill.length ; i++) {
			l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
			}
			pc.sendPackets(new S_ChatPacket(pc,"\\aA알림: 게임마스터 주위에 '버프'가 제공하었습니다."));
			}
			}
	public void 화면축복(L1PcInstance gm) {
		int[] allBuffSkill = { FEATHER_BUFF_A };
		L1SkillUse l1skilluse = null;
		for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(gm, 30)) {
		if(pc.isPrivateShop() || pc.isAutoClanjoin()){
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
			pc.sendPackets(new S_ChatPacket(pc, "\\aA알림: 게임마스터 주위에 '메티스의 축복'을 제공하였습니다."));
		}
	}
	public void 화면생마(L1PcInstance gm) {
		int[] allBuffSkill = { 7678 };
		L1SkillUse l1skilluse = null;
		for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(gm, 30)) {
		if(pc.isPrivateShop() || pc.isAutoClanjoin()){
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
			pc.sendPackets(new S_ChatPacket(pc,"\\aA알림: 게임마스터 주위에 '생명의 마안'을 제공하였습니다."));
		}
	}
	public void 화면흑사(L1PcInstance gm) {
		int[] allBuffSkill = { God_buff };
		L1SkillUse l1skilluse = null;
		for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(gm, 30)) {
		if(pc.isPrivateShop() || pc.isAutoClanjoin()){
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
			pc.sendPackets(new S_ChatPacket(pc,"\\aA알림: 게임마스터 주위에 '흑사의 버프'을 제공하였습니다."));
		}
	}
	public void 화면코마(L1PcInstance gm) {
		int[] allBuffSkill = { COMA_B };
		L1SkillUse l1skilluse = null;
		for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(gm, 30)) {
		if(pc.isPrivateShop() || pc.isAutoClanjoin()){
		continue;
		}
			if (pc.hasSkillEffect(L1SkillId.COMA_A)) { // 코마 3조각
				pc.removeSkillEffect(L1SkillId.COMA_A);
			}
			if (pc.hasSkillEffect(L1SkillId.COMA_B)) { // 코마 5조각
				pc.removeSkillEffect(L1SkillId.COMA_B);
			}

			l1skilluse = new L1SkillUse();
			for (int i = 0; i < allBuffSkill.length; i++) {
				l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
			}
			pc.sendPackets(new S_SkillSound(pc.getId(), 4856));
			pc.sendPackets(new S_ChatPacket(pc,"\\aA알림: 게임마스터 주위에 '코마 버프'을 제공하였습니다."));
		}
	}
	public void 전체풀업() {
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
			pc.sendPackets(new S_ChatPacket(pc, "\\aA알림: 게임마스터께서 '버프'를 제공하였습니다."));
		}
	}
	public void 전체축복() {
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
			pc.sendPackets(new S_ChatPacket(pc, "\\aA알림: 게임마스터께서'메티스의 축복'을 제공하였습니다."));
		}
	}
	public void 전체생마() {
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
			pc.sendPackets(new S_ChatPacket(pc, "\\aA알림: 게임마스터께서 '생명의 마안'을 제공하였습니다."));
		}
	}
	public void 전체흑사() {
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
			pc.sendPackets(new S_ChatPacket(pc, "\\aA알림: 게임마스터께서 '흑사의 버프'를 제공하였습니다."));
		}
	}
	public void 전체코마() {
		int[] allBuffSkill = { COMA_B };
		L1SkillUse l1skilluse = null;
		for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			if (pc == null || pc.isPrivateShop() || pc.getMapId() == 5166 || pc.isAutoClanjoin()) {
				continue;
			}
			if (pc.noPlayerCK && !pc.noPlayerRobot) {
				continue;
			}
			if (pc.hasSkillEffect(L1SkillId.COMA_A)) { // 코마 3조각
				pc.removeSkillEffect(L1SkillId.COMA_A);
			}
			if (pc.hasSkillEffect(L1SkillId.COMA_B)) { // 코마 5조각
				pc.removeSkillEffect(L1SkillId.COMA_B);
			}

			l1skilluse = new L1SkillUse();
			for (int i = 0; i < allBuffSkill.length; i++) {
				l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
			}
			pc.sendPackets(new S_SkillSound(pc.getId(), 4856));
			pc.sendPackets(new S_ChatPacket(pc, "\\aA알림: 게임마스터께서 '코마 버프'를 제공하였습니다."));
		}
	}

	public void doNotChatEveryone() {
		L1World.getInstance().set_worldChatElabled(false);
		L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("\\aG경고: \\aA월드채팅 비활성화 시작"));
	}

	public void doChatEveryone() {
		L1World.getInstance().set_worldChatElabled(true);
		L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("\\aG경고: \\aA월드채팅 활성화 시작"));
	}

	
}
