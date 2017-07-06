package l1j.server.server.model;

import static l1j.server.server.model.skill.L1SkillId.SCALES_EARTH_DRAGON;
import static l1j.server.server.model.skill.L1SkillId.SCALES_FIRE_DRAGON;
import static l1j.server.server.model.skill.L1SkillId.SCALES_WATER_DRAGON;

import java.util.HashMap;
import java.util.Map;

import l1j.server.server.ActionCodes;
import l1j.server.server.datatables.PolyTable;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_ChangeShape;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_CloseList;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillIconGFX;

//Referenced classes of package l1j.server.server.model:
//L1PcInstance

public class L1PolyMorph {
	// weapon equip bit
	private static final int DAGGER_EQUIP = 1;

	private static final int SWORD_EQUIP = 2;

	private static final int TWOHANDSWORD_EQUIP = 4;

	private static final int AXE_EQUIP = 8;

	private static final int SPEAR_EQUIP = 16;

	private static final int STAFF_EQUIP = 32;

	private static final int EDORYU_EQUIP = 64;

	private static final int CLAW_EQUIP = 128;

	private static final int BOW_EQUIP = 256; 

	private static final int KIRINGKU_EQUIP = 512;
	
	private static final int CHAINSWORD_EQUIP = 1024;

	// armor equip bit
	private static final int HELM_EQUIP = 1;

	private static final int AMULET_EQUIP = 2;

	private static final int EARRING_EQUIP = 4;

	private static final int TSHIRT_EQUIP = 8;

	private static final int ARMOR_EQUIP = 16;

	private static final int CLOAK_EQUIP = 32;

	private static final int BELT_EQUIP = 64;

	private static final int SHIELD_EQUIP = 128;

	private static final int GARDER_EQUIP = 128;

	private static final int GLOVE_EQUIP = 256;

	private static final int RING_EQUIP = 512;

	private static final int BOOTS_EQUIP = 1024;

	// 변신의 원인을 나타내는 bit
	public static final int MORPH_BY_ITEMMAGIC = 1;

	public static final int MORPH_BY_GM = 2;

	public static final int MORPH_BY_NPC = 4; // 점성술사 케프리샤 이외의 NPC

	public static final int MORPH_BY_KEPLISHA = 8;

	public static final int MORPH_BY_LOGIN = 0;

	private static final Map<Integer, Integer> weaponFlgMap = new HashMap<Integer, Integer>();
	static {
		weaponFlgMap.put(1, SWORD_EQUIP);
		weaponFlgMap.put(2, DAGGER_EQUIP);
		weaponFlgMap.put(3, TWOHANDSWORD_EQUIP);
		weaponFlgMap.put(4, BOW_EQUIP);
		weaponFlgMap.put(5, SPEAR_EQUIP);
		weaponFlgMap.put(6, AXE_EQUIP);
		weaponFlgMap.put(7, STAFF_EQUIP);
		weaponFlgMap.put(8, BOW_EQUIP);
		weaponFlgMap.put(9, BOW_EQUIP);
		weaponFlgMap.put(10, BOW_EQUIP);
		weaponFlgMap.put(11, CLAW_EQUIP);
		weaponFlgMap.put(12, EDORYU_EQUIP);
		weaponFlgMap.put(13, BOW_EQUIP);
		weaponFlgMap.put(14, SPEAR_EQUIP);
		weaponFlgMap.put(15, AXE_EQUIP);
		weaponFlgMap.put(16, STAFF_EQUIP);
		weaponFlgMap.put(17, KIRINGKU_EQUIP);
		weaponFlgMap.put(18, CHAINSWORD_EQUIP);
	}
	private static final Map<Integer, Integer> armorFlgMap = new HashMap<Integer, Integer>();
	static {
		armorFlgMap.put(1, HELM_EQUIP);
		armorFlgMap.put(2, ARMOR_EQUIP);
		armorFlgMap.put(3, TSHIRT_EQUIP);
		armorFlgMap.put(4, CLOAK_EQUIP);
		armorFlgMap.put(5, GLOVE_EQUIP);
		armorFlgMap.put(6, BOOTS_EQUIP);
		armorFlgMap.put(7, SHIELD_EQUIP);
		armorFlgMap.put(7, GARDER_EQUIP);
		armorFlgMap.put(8, AMULET_EQUIP);
		armorFlgMap.put(9, RING_EQUIP);
		armorFlgMap.put(10, BELT_EQUIP);
		armorFlgMap.put(12, EARRING_EQUIP);
	}

	private int _id;
	private String _name;
	private int _polyId;
	private int _minLevel;
	private int _weaponEquipFlg;
	private int _armorEquipFlg;
	private boolean _canUseSkill;
	private int _causeFlg;

	public L1PolyMorph(int id, String name, int polyId, int minLevel,
			int weaponEquipFlg, int armorEquipFlg, boolean canUseSkill,
			int causeFlg) {
		_id = id;
		_name = name;
		_polyId = polyId;
		_minLevel = minLevel;
		_weaponEquipFlg = weaponEquipFlg;
		_armorEquipFlg = armorEquipFlg;
		_canUseSkill = canUseSkill;
		_causeFlg = causeFlg;
	}

	public int getId() {
		return _id;
	}

	public String getName() {
		return _name;
	}

	public int getPolyId() {
		return _polyId;
	}

	public int getMinLevel() {
		return _minLevel;
	}

	public int getWeaponEquipFlg() {
		return _weaponEquipFlg;
	}

	public int getArmorEquipFlg() {
		return _armorEquipFlg;
	}

	public boolean canUseSkill() {
		return _canUseSkill;
	}

	public int getCauseFlg() {
		return _causeFlg;
	}

	public static void handleCommands(L1PcInstance pc, String s) {
		if (pc == null || pc.isDead()) {
			return;
		}
		L1PolyMorph poly = PolyTable.getInstance().getTemplate(s);
		if (poly != null || s.equals("none")) {
			if (s.equals("none")) {
				pc.removeSkillEffect(L1SkillId.SHAPE_CHANGE);
				pc.sendPackets(new S_CloseList(pc.getId()));
			} else if (pc.getLevel() >= poly.getMinLevel() || pc.isGm()) {
				doPoly(pc, poly.getPolyId(), 7200, MORPH_BY_ITEMMAGIC);
				pc.sendPackets(new S_CloseList(pc.getId()));
			} else {
				pc.sendPackets(new S_ServerMessage(181));
			}
		}
	}

	public static void doPoly(L1Character cha, int polyId, int timeSecs,
			int cause) {
		if (cha == null || cha.isDead()) {
			return;
		}
		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			if (pc.getMapId() == 5302 || pc.getMapId() == 5490 || pc.getMapId() == 5153) { // 낚시터,배틀존
				pc.sendPackets(new S_ServerMessage(1170)); // 이곳에서 변신할수 없습니다.
				return;
			}

			if (pc.getTempCharGfx() == 6034
					|| pc.getTempCharGfx() == 6035) {
				pc.sendPackets(new S_ServerMessage(181));
				return;	
			}
			if (! isMatchCause(polyId, cause)) {
				pc.sendPackets(new S_ServerMessage(181)); // \f1 그러한 monster에게는 변신할 수 없습니다.
				return;
			}			
			if(cha.hasSkillEffect(SCALES_EARTH_DRAGON)
					|| cha.hasSkillEffect(SCALES_WATER_DRAGON)
					|| cha.hasSkillEffect(SCALES_FIRE_DRAGON)){
				cha.removeSkillEffect(SCALES_EARTH_DRAGON);
				cha.removeSkillEffect(SCALES_WATER_DRAGON);
				cha.removeSkillEffect(SCALES_FIRE_DRAGON);
			}
			pc.killSkillEffectTimer(L1SkillId.SHAPE_CHANGE);
			pc.setSkillEffect(L1SkillId.SHAPE_CHANGE, timeSecs * 1000);
			if (pc.getTempCharGfx() != polyId) {
				L1ItemInstance weapon = pc.getWeapon();
				//용기사 체인소드
//				if(pc.isDragonknight()){
//					if(polyId == 9206 || polyId == 6137 || polyId == 6142 || polyId == 6147 || polyId == 6152
//							|| polyId == 6157 || polyId == 9205 || polyId == 6267 || polyId == 6270 || polyId == 6273
//							|| polyId == 6276){
//						for (L1ItemInstance items : pc.getInventory().getItems()) {
//							if(items.getItem().getType() == 18){
//								if(items.getItem().getType1() == 24){
//									items.getItem().setType1(50);
//									if(weapon != null){
//										pc.getInventory().setEquipped(weapon, false);
//										pc.getInventory().setEquipped(weapon, true);
//									}
//								}
//							}
//						}
//					}else{
//						for (L1ItemInstance items : pc.getInventory().getItems()) {  
//							if(items.getItem().getType() == 18){
//								if(items.getItem().getType1() == 50){
//									items.getItem().setType1(24);
//									if(weapon != null){
//										pc.getInventory().setEquipped(weapon, false);
//										pc.getInventory().setEquipped(weapon, true);
//									}
//								}
//							}
//						}
//					}
//				}
				//용기사 체인소드
				boolean weaponTakeoff = (weapon != null && !isEquipableWeapon(polyId, weapon.getItem().getType()));
				pc.setTempCharGfx(polyId);
				pc.sendPackets(new S_ChangeShape(pc.getId(), polyId, weaponTakeoff));
				if (!pc.isGmInvis() && !pc.isInvisble() && !pc.isGhost()) {
					pc.broadcastPacket(new S_ChangeShape(pc.getId(), polyId));
				}
				pc.getInventory().takeoffEquip(polyId);
				weapon = pc.getWeapon();
				if (weapon != null) {
					S_CharVisualUpdate charVisual = new S_CharVisualUpdate(pc);
					pc.sendPackets(charVisual);
					pc.broadcastPacket(charVisual);
				}
			}
			L1ItemInstance weapon = pc.getWeapon();
			if (weapon != null) {
				int range = 1;
				int poly = pc.getTempCharGfx();
				if (weapon.getItem().getType() == 4) {
					range = 17;
				} else if ((weapon.getItem().getType() == 10) || (weapon.getItem().getType() == 13)) {
					range = 14;
				} else if (weapon.getItem().getType() == 5|| weapon.getItem().getType() == 14|| weapon.getItem().getType() == 18){
					if (poly == 11330||poly == 11344|| poly == 11351||poly == 11368||poly == 11376||poly == 11447|| 
    						poly == 12237||poly == 0 ||poly == 61|| poly == 138||poly == 734||poly == 2786|| poly == 6658||
    						poly == 6671||poly == 12490||poly == 1||poly == 48||poly == 37||poly == 1186||poly == 2796||poly == 6661||
    						poly == 6650||poly == 12494||poly == 13389||
    						poly == 11408||poly == 11409||poly == 11410||poly == 11411||poly == 11412||poly == 11413||
    						poly == 11414||poly == 11415||poly == 11416||poly == 11417||poly == 11418||poly == 11419||
    						poly == 11420||poly == 11421||poly == 12542||poly == 12541 || poly == 13735 || poly == 13737
    						|| poly == 14928 //82경비창
    						|| poly == 13389 //85경비창
    						) {
                		range = 2;
    					}
				}else{
					range = 1;
				}
				if (weapon.getItem().getType1() == 20) {
					if (weapon.getItem().getType() == 4)
						pc.sendPackets(new S_PacketBox(S_PacketBox.공격가능거리, range, 3, true));
					else
						pc.sendPackets(new S_PacketBox(S_PacketBox.공격가능거리, range, 3, true));
				} else {
					//
					int type = 7;
					boolean bow_or_tohand = false;
					if (weapon.getItem().getType() == 3){
						type = 1;
						bow_or_tohand = true;
					} else if (weapon.getItem().getType() == 11) {
						type = 2;
						bow_or_tohand = true;
					} else if (weapon.getItem().getType() == 12) {
						type = 4;
						bow_or_tohand = true;
					}else if (pc.isDragonknight()){
						type = 10;
						bow_or_tohand = true;
					}
					//
					pc.sendPackets(new S_PacketBox(S_PacketBox.공격가능거리, range, type, bow_or_tohand));
				}
			} else {
				pc.sendPackets(new S_PacketBox(S_PacketBox.공격가능거리, 1, 0, false));
			}
			pc.sendPackets(new S_SkillIconGFX(35, timeSecs));
		} else if (cha instanceof L1MonsterInstance) {
			L1MonsterInstance mob = (L1MonsterInstance) cha;
			mob.killSkillEffectTimer(L1SkillId.SHAPE_CHANGE);
			mob.setSkillEffect(L1SkillId.SHAPE_CHANGE, timeSecs * 1000);
			if (mob.getTempCharGfx() != polyId) {
				mob.setTempCharGfx(polyId);
				mob.broadcastPacket(new S_ChangeShape(mob.getId(), polyId));
			}
		}
	}
	
	/** 3.80 변신리스트 **/
	public static void doPolyPraivateShop(L1Character cha, int polyIndex){
		if ((cha == null) || cha.isDead()) {
			return;
		}
		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;

			int PolyList[] = { 11479, 11427, 10047, 9688, 11322, 10069, 10034,10032 };
			if (pc.getTempCharGfx() != PolyList[polyIndex - 1]) {
				pc.setTempCharGfx(PolyList[polyIndex - 1]);
				L1ItemInstance weapon = pc.getWeapon();
				boolean weaponTakeoff = (weapon != null && !isEquipableWeapon(PolyList[polyIndex - 1], weapon.getItem().getType()));
				if (weaponTakeoff) {
					pc.getInventory().setEquipped(weapon, false);
				}
				pc.sendPackets(new S_ChangeShape(pc.getId(), PolyList[polyIndex - 1], ActionCodes.ACTION_Shop));
				if (!pc.isGmInvis() && !pc.isInvisble()) {
					Broadcaster.broadcastPacket(pc,new S_ChangeShape(pc.getId(),PolyList[polyIndex - 1], ActionCodes.ACTION_Shop));
				}
			}
			pc.sendPackets(new S_CharVisualUpdate(pc, ActionCodes.ACTION_Shop));
			Broadcaster.broadcastPacket(pc,new S_CharVisualUpdate(pc, ActionCodes.ACTION_Shop));
		}
	}
	
	public static void undoPolyPrivateShop(L1Character cha){
		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			int classId = pc.getClassId();
			pc.setTempCharGfx(classId);
			if (!pc.isDead()) {
				pc.sendPackets(new S_ChangeShape(pc.getId(), classId, pc.getCurrentWeapon()));
				Broadcaster.broadcastPacket(pc, new S_ChangeShape(pc.getId(), classId, pc.getCurrentWeapon()));
				pc.sendPackets(new S_CharVisualUpdate(pc, pc.getCurrentWeapon()));
				Broadcaster.broadcastPacket(pc,new S_CharVisualUpdate(pc, pc.getCurrentWeapon()));
			}
		}
	}
	/** 3.80 변신리스트 **/
	
	public static void undoPolyAutoClanjoin(L1Character cha){
		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			int classId = pc.getClassId();
			pc.setTempCharGfx(classId);
			if (!pc.isDead()) {
				pc.sendPackets(new S_ChangeShape(pc.getId(), classId, pc.getCurrentWeapon()));
				Broadcaster.broadcastPacket(pc, new S_ChangeShape(pc.getId(), classId, pc.getCurrentWeapon()));
				pc.sendPackets(new S_CharVisualUpdate(pc, pc.getCurrentWeapon()));
				Broadcaster.broadcastPacket(pc,new S_CharVisualUpdate(pc, pc.getCurrentWeapon()));
			}
		}
	}
	
	
	public static void undoPoly(L1Character cha) {
		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
//			if(pc.getTempCharGfx() >= 13715 && pc.getTempCharGfx() <= 13745){
//				int gfxid = pc.getTempCharGfx();
//				int time = pc.getSkillEffectTimeSec(MORPH_BY_ITEMMAGIC);
//				doPoly(pc, gfxid, time, MORPH_BY_ITEMMAGIC);
//				return;
//			}
			if(pc.getMapId() == 5143){ // 펫레이싱
				doPoly(pc, 5065, 1000, MORPH_BY_NPC);
				return;
			}
			int classId = pc.getClassId();
			pc.setTempCharGfx(classId);
			pc.sendPackets(new S_ChangeShape(pc.getId(), classId));
			pc.broadcastPacket(new S_ChangeShape(pc.getId(), classId));
			L1ItemInstance weapon = pc.getWeapon();
			//용기사 포우
			if(pc.isDragonknight()){
				for (L1ItemInstance items : pc.getInventory().getItems()) {		
					if(items.getItem().getType() == 18){
						if(items.getItem().getType1() == 50){
							items.getItem().setType1(24);
							if(weapon != null){
								pc.getInventory().setEquipped(weapon, false);
								pc.getInventory().setEquipped(weapon, true);
							}
						}
					}
				}
			}
			if (weapon != null) {
				S_CharVisualUpdate charVisual = new S_CharVisualUpdate(pc);
				pc.sendPackets(charVisual);
				pc.broadcastPacket(charVisual);
				int range = 1;
				int poly = pc.getTempCharGfx();
				if (weapon.getItem().getType() == 4)
					range = 17;
				else if ((weapon.getItem().getType() == 10) || (weapon.getItem().getType() == 13))
					range = 14;
				else if (weapon.getItem().getType() == 5|| weapon.getItem().getType() == 14|| weapon.getItem().getType() == 18){
					if (poly == 11330||poly == 11344|| poly == 11351||poly == 11368||poly == 11376||poly == 11447|| 
							poly == 12237||poly == 0 ||poly == 61|| poly == 138||poly == 734||poly == 2786|| poly == 6658||
							poly == 6671||poly == 12490||poly == 1||poly == 48||poly == 37||poly == 1186||poly == 2796||poly == 6661||
							poly == 6650||poly == 12494||poly == 13389|| poly == 14928 ) {
						range = 2;
					}
				}else{
					range = 1;
				}
				if (weapon.getItem().getType1() == 20) {
					if (weapon.getItem().getType() == 4)
						pc.sendPackets(new S_PacketBox(S_PacketBox.공격가능거리, range, 3, true));
					else
						pc.sendPackets(new S_PacketBox(S_PacketBox.공격가능거리, range, 3, true));
				} else {
					//
					int type = 7;
					boolean bow_or_tohand = false;
					if (weapon.getItem().getType() == 3){
						type = 1;
						bow_or_tohand = true;
					}else if (pc.isDragonknight()){
						type = 10;
						bow_or_tohand = true;
					}
					//
					pc.sendPackets(new S_PacketBox(S_PacketBox.공격가능거리, range, type, bow_or_tohand));
				}
			} else {
				pc.sendPackets(new S_PacketBox(S_PacketBox.공격가능거리, 1, 0, false));
			}
		} else if (cha instanceof L1MonsterInstance) {
			L1MonsterInstance mob = (L1MonsterInstance) cha;
			mob.setTempCharGfx(0);
			mob.broadcastPacket(new S_ChangeShape(mob.getId(), mob.getGfxId()));
		}
	}

	public static void MagicBookPoly(L1PcInstance pc, String s, int time) {
		if (pc == null || pc.isDead()) {
			return;
		}
		L1PolyMorph poly = PolyTable.getInstance().getTemplate(s);
		if (poly != null ){
			doPoly(pc, poly.getPolyId(), time, MORPH_BY_ITEMMAGIC);
			pc.sendPackets(new S_CloseList(pc.getId()));
		}
		if (pc.getMagicItemId() != 0){
			pc.getInventory().consumeItem(pc.getMagicItemId(), 1);
			pc.setMagicItemId(0);
		}
	}
	
	public static boolean isEquipableWeapon(int polyId, int weaponType) {
		L1PolyMorph poly = PolyTable.getInstance().getTemplate(polyId);
		if (poly == null) {
			return true;
		}

		Integer flg = weaponFlgMap.get(weaponType);
		if (flg != null) {
			return 0 != (poly.getWeaponEquipFlg() & flg);
		}
		return true;
	}

	public static boolean isEquipableArmor(int polyId, int armorType) {
		L1PolyMorph poly = PolyTable.getInstance().getTemplate(polyId);
		if (poly == null) {
			return true;
		}

		Integer flg = armorFlgMap.get(armorType);
		if (flg != null) {
			return 0 != (poly.getArmorEquipFlg() & flg);
		}
		return true;
	}
	// 지정한 polyId가 무엇에 의해 변신해, 그것이 변신 당할까?
	public static boolean isMatchCause(int polyId, int cause) {
		L1PolyMorph poly = PolyTable.getInstance(). getTemplate(polyId);
		if (poly == null) {
			return true;
		}
		if (cause == MORPH_BY_LOGIN) {
			return true;
		}
		return 0 != (poly.getCauseFlg() & cause);
	}
}
