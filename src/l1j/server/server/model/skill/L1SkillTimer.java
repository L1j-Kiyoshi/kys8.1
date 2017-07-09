package l1j.server.server.model.skill;

import static l1j.server.server.model.skill.L1SkillId.*;

import java.util.ArrayList;
import java.util.List;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.Controller.CrockController;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.serverpackets.S_CurseBlind;
import l1j.server.server.serverpackets.S_Dexup;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_Liquor;
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_NewCreateItem;
import l1j.server.server.serverpackets.S_NewSkillIcon;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_OwnCharStatus2;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_Poison;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillIconAura;
import l1j.server.server.serverpackets.S_SkillIconBlessOfEva;
import l1j.server.server.serverpackets.S_SkillIconShield;
import l1j.server.server.serverpackets.S_SkillIconWindShackle;
import l1j.server.server.serverpackets.S_SkillIconWisdomPotion;
import l1j.server.server.serverpackets.S_Strup;
import l1j.server.server.serverpackets.S_TrueTargetNew;
import l1j.server.server.templates.L1Skills;

public class L1SkillTimer implements Runnable {
	public L1SkillTimer(L1Character cha, int skillId, int timeMillis) {
		_cha = cha;
		_skillId = skillId;
		_timeMillis = timeMillis;

		_remainingTime = _timeMillis / 1000;
		_stop = false;
	}

	@Override
	public void run() {
		if (_stop) {
			return;
		}
		_remainingTime--;
		if (_remainingTime <= 0) {
			_cha.removeSkillEffect(_skillId);
			return;
		}

		GeneralThreadPool.getInstance().schedule(this, 1000);
	}

	public void begin() {
		GeneralThreadPool.getInstance().schedule(this, 1000);
	}

	public void end() {
		_stop = true;
		L1SkillStop.stopSkill(_cha, _skillId);
	}

	public void kill() {
		_stop = true;
	}

	public int getRemainingTime() {
		return _remainingTime;
	}

	private final L1Character _cha;
	private final int _timeMillis;
	private final int _skillId;
	private int _remainingTime;
	private boolean _stop;
}

class L1SkillStop {
	public static void stopSkill(L1Character cha, int skillId) {
		switch (skillId) {
		case TOMAHAWK:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(S_PacketBox.UNLIMITED_ICON1, 570, false));
			}
			break;
		case LIFE_STREAM:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(S_PacketBox.UNLIMITED_ICON1, 59, false));
			}
			break;
		case BLAZING_SPIRITS:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.removeSkillEffect(BLAZING_SPIRITS);
				pc.sendPackets(new S_NewSkillIcon(L1SkillId.BLAZING_SPIRITS, false, -1));
			}
			break;
		case ASSASSIN:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.removeSkillEffect(ASSASSIN);
				pc.sendPackets(new S_NewSkillIcon(L1SkillId.ASSASSIN, false, -1));
			}
			break;
		case DESTROY:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.removeSkillEffect(DESTROY);
				pc.sendPackets(new S_NewSkillIcon(L1SkillId.DESTROY, false, -1));
			}
			break;
		case SOUL_BARRIER:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.removeSkillEffect(SOUL_BARRIER);
				pc.sendPackets(new S_NewSkillIcon(L1SkillId.SOUL_BARRIER, false, -1));
			}
			break;
		case IMPACT:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.removeSkillEffect(IMPACT);
				pc.sendPackets(new S_NewSkillIcon(L1SkillId.IMPACT, false, -1));
				pc.setImpactUp(0);
			}
			break;
		case TITANL_RISING:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.removeSkillEffect(TITANL_RISING);
				pc.sendPackets(new S_NewSkillIcon(L1SkillId.TITANL_RISING, false, -1));
				pc.setRisingUp(0);
			}
			break;
		case ABSOLUTE_BLADE:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.removeSkillEffect(ABSOLUTE_BLADE);
				pc.sendPackets(new S_NewSkillIcon(L1SkillId.ABSOLUTE_BLADE, false, -1));
			}
			break;
		case DEATH_HEAL:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.removeSkillEffect(DEATH_HEAL);
				pc.sendPackets(new S_NewSkillIcon(L1SkillId.DEATH_HEAL, false, -1));
			}
			break;
		case GRACE_AVATAR:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getResistance().addHold(-10 + (pc.getGraceLv() * -1)); // 홀드 내성
				pc.getResistance().addStun(-10 + (pc.getGraceLv() * -1)); // 스턴 내성
				pc.getResistance().addDESPERADO(-10 + (pc.getGraceLv() * -1)); // 공포 내성
				pc.removeSkillEffect(GRACE_AVATAR);
				pc.sendPackets(new S_NewSkillIcon(L1SkillId.GRACE_AVATAR, false, -1));
			}
			break;
		/** 혈맹버프 **/
		case CLAN_BUFF1: {// 일반 공격 태세
			L1PcInstance pc = (L1PcInstance) cha;
			pc.addDmgupByArmor(-2);
			pc.addBowDmgupByArmor(-2);			
			pc.sendPackets(new S_ServerMessage(4619, "$22503"));		
		}
			break;
		case CLAN_BUFF2: {// 일반 방어 태세
			L1PcInstance pc = (L1PcInstance) cha;
			pc.getAC().addAc(3);
			pc.sendPackets(new S_OwnCharAttrDef(pc));		
			pc.sendPackets(new S_ServerMessage(4619, "$22504"));		
		}
			break;
		case CLAN_BUFF3: {// 전투 공격 태세
			L1PcInstance pc = (L1PcInstance) cha;
		//	pc.addPvPDmgup(-1);			
			pc.sendPackets(new S_ServerMessage(4619, "$22505"));		
		}
			break;
		case CLAN_BUFF4: {// 전투 방어 태세
			L1PcInstance pc = (L1PcInstance) cha;
		//	pc.addDmgReducPvp(-1);		
			pc.sendPackets(new S_ServerMessage(4619, "$22506"));		
		}
			break;
		case L1SkillId.레벨업보너스:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(0, true, true));
			}
			break;
		case L1SkillId.DRAGON_PUPLE:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(0, 1, true, true));
			}
			break;
		case L1SkillId.DRAGON_TOPAZ:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(0, 2, true, true));
			}
			break;
		case 나루토감사캔디:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				if(pc.getLevel() >= 1 && pc.getLevel() <= 60){
					pc.getAbility().addAddedDex((byte) -7);
					pc.sendPackets(new S_Dexup(pc, 1, 0));
					pc.getAbility().addAddedStr((byte) -7);
					pc.sendPackets(new S_Strup(pc, 1, 0));
				} else {
					pc.getAbility().addAddedDex((byte) -6);
					pc.sendPackets(new S_Dexup(pc, 1, 0));
					pc.getAbility().addAddedStr((byte) -6);
					pc.sendPackets(new S_Strup(pc, 1, 0));
				}
			}
			break;
		case DRESS_EVASION:// 12
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.Add_Er(-18);
				pc.sendPackets(new S_PacketBox(S_PacketBox.ER_UpDate, pc.get_PlusEr()));
				
			}
			break;
		case AQUA_PROTECTER:// 5
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.Add_Er(-5);
				pc.sendPackets(new S_PacketBox(S_PacketBox.ER_UpDate, pc.get_PlusEr()));
			}
			break;
		case SOLID_CARRIAGE:// 15
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.Add_Er(-15);
				pc.sendPackets(new S_PacketBox(S_PacketBox.ER_UpDate, pc.get_PlusEr()));
			}
			break;
		case STRIKER_GALE:// -99
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				// pc.Add_Er(99);
				pc.sendPackets(new S_PacketBox(S_PacketBox.ER_UpDate, pc.get_PlusEr()));
				pc.sendPackets(new S_OwnCharStatus(pc));
			}
			break;
		case 800018:// 티칼
			if (!cha.isDead()) {
				CrockController.getInstance().dieCount(0);
				CrockController.getInstance().BossSpawn(800018, 32753, 32870, (short) 784, 1900000);
			}
			break;
		case 800019:// 티칼
			if (!cha.isDead()) {
				CrockController.getInstance().dieCount(0);
				CrockController.getInstance().BossSpawn(800019, 32750, 32859, (short) 784, 1900000);
			}
			break;
		case LIGHT:
			if (cha instanceof L1PcInstance) {
				if (!cha.isInvisble()) {
					L1PcInstance pc = (L1PcInstance) cha;
					pc.getLight().turnOnOffLight();
				}
			}
			break;
		case TRUE_TARGET:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.set트루타켓(0);
			}
			Broadcaster.broadcastPacket(cha, new S_TrueTargetNew(cha.getId(), false));
			synchronized (L1SkillUse._truetarget_list) {
				List<Integer> remove_list = new ArrayList<Integer>();
				for (Integer id : L1SkillUse._truetarget_list.keySet()) {
					L1Object o = L1SkillUse._truetarget_list.get(id);
					if (o.getId() != cha.getId())
						continue;
					remove_list.add(id);
				}
				for (Integer id : remove_list)
					L1SkillUse._truetarget_list.remove(id);
			}
			break;
		case GLOWING_AURA:
			cha.addHitup(-5);
			cha.addBowHitup(-5);
			cha.getResistance().addMr(-20);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_SkillIconAura(113, 0));
			}
			break;
		case God_buff: {
			L1PcInstance pc = (L1PcInstance) cha;
			pc.getAC().addAc(2);
			pc.addHitup(-3);
			pc.addMaxHp(-20);
			pc.addMaxMp(-13);
			pc.getResistance().addHold(-10); // 홀드내성
		}
			break;
		case DELAY:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				if (!pc.hasSkillEffect(L1SkillId.DELAY)) {
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_END));
				}
			}
			break;
		case BUFF_SAEL: {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				if (pc.hasSkillEffect(L1SkillId.BUFF_SAEL)) {
					pc.removeSkillEffect(L1SkillId.BUFF_SAEL);
				}
				pc.getAC().addAc(8);
				pc.addBowHitup(-6);
				pc.addBowDmgup(-3);
				pc.addMaxHp(-80);
				pc.addMaxMp(10);
				pc.addHpr(-8);
				pc.addMpr(-1);
				pc.getResistance().addWater(-30);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				pc.sendPackets(new S_OwnCharAttrDef(pc));
				pc.sendPackets(new S_SPMR(pc));
			}
		}
			break;
		case SHINING_AURA:
			cha.getAC().addAc(8);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(114, 0));
			}
			break;
		case BRAVE_AURA:
			cha.addDmgup(-5);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(116, 0));
			}
			break;
		case SHIELD:
			cha.getAC().addAc(2);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconShield(1, 0));
			}
			break;
		case BLIND_HIDING:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.delBlindHiding();
			}
			break;
		case SHADOW_ARMOR:
			cha.getResistance().addMr(-5);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SPMR(pc));
				// pc.sendPackets(new S_SkillIconShield(3, 0));
			}
			break;
		case DRESS_DEXTERITY:
			cha.getAbility().addAddedDex((byte) -3);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Dexup(pc, 3, 0));
			}
			break;
		case DRESS_MIGHTY:
			cha.getAbility().addAddedStr((byte) -3);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Strup(pc, 3, 0));
			}
			break;
		case EARTH_GUARDIAN:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconShield(7, 0));
			}
			break;
		case RESIST_MAGIC:
			cha.getResistance().addMr(-10);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SPMR(pc));
			}
			break;
		case CLEAR_MIND:
			cha.getAbility().addAddedWis((byte) -3);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.resetBaseMr();
			}
			break;
		case RESIST_ELEMENTAL:
			cha.getResistance().addAllNaturalResistance(-10);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
			break;
		case ELEMENTAL_PROTECTION:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				int attr = pc.getElfAttr();
				if (attr == 1) {
					cha.getResistance().addEarth(-50);
				} else if (attr == 2) {
					cha.getResistance().addFire(-50);
				} else if (attr == 4) {
					cha.getResistance().addWater(-50);
				} else if (attr == 8) {
					cha.getResistance().addWind(-50);
				}
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
			break;
		case ELEMENTAL_FALL_DOWN:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				int attr = pc.getAddAttrKind();
				int i = 50;
				switch (attr) {
				case 1:
					pc.getResistance().addEarth(i);
					break;
				case 2:
					pc.getResistance().addFire(i);
					break;
				case 4:
					pc.getResistance().addWater(i);
					break;
				case 8:
					pc.getResistance().addWind(i);
					break;
				default:
					break;
				}
				pc.setAddAttrKind(0);
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			} else if (cha instanceof L1NpcInstance) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				int attr = npc.getAddAttrKind();
				int i = 50;
				switch (attr) {
				case 1:
					npc.getResistance().addEarth(i);
					break;
				case 2:
					npc.getResistance().addFire(i);
					break;
				case 4:
					npc.getResistance().addWater(i);
					break;
				case 8:
					npc.getResistance().addWind(i);
					break;
				default:
					break;
				}
				npc.setAddAttrKind(0);
			}
			break;
		case IRON_SKIN:
			cha.getAC().addAc(10);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconShield(10, 0));
			}
			break;
		case EARTH_SKIN:
			cha.getAC().addAc(6);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconShield(6, 0));
			}
			break;
		case PHYSICAL_ENCHANT_STR:
			cha.getAbility().addAddedStr((byte) -5);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Strup(pc, 1, 0));
			}
			break;
		case PHYSICAL_ENCHANT_DEX:
			cha.getAbility().addAddedDex((byte) -5);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Dexup(pc, 1, 0));
			}
			break;
		case FIRE_WEAPON:
			cha.addDmgup(-4);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(147, 0));
			}
			break;
		case DANCING_BLADES:
			cha.setBraveSpeed(0);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				Broadcaster.broadcastPacket(pc, new S_SkillBrave(pc.getId(), 0, 0));
			}
			break;
		// 크레이 혈흔
		case BUFF_CRAY:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addHitup(-5);
				pc.addDmgup(-1);
				pc.addBowHitup(-5);
				pc.addBowDmgup(-1);
				pc.addMaxHp(-100);
				pc.addMaxMp(-50);
				pc.addHpr(-3);
				pc.addMpr(-3);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				pc.sendPackets(new S_SPMR(pc));
			}
			break;
		case BUFF_GUNTER:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getAbility().addAddedDex((byte) -5);
				pc.addBowHitup(-7);
				pc.addBowDmgup(-5);
				pc.addMaxHp(-100);
				pc.addMaxMp(-40);
				pc.addHpr(-10);
				pc.addMpr(-3);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				pc.sendPackets(new S_OwnCharAttrDef(pc));
				pc.sendPackets(new S_SPMR(pc));
			}
			break;
		// UI DG표시
		case UNCANNY_DODGE: // 언케니닷지
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addDg(8);
			}
			break;
		// UI DG표시
		case BURNING_WEAPON:
			cha.addDmgup(-6);
			cha.addHitup(-3);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(162, 0));
			}
			break;
		case MIRROR_IMAGE: {
			L1PcInstance pc = (L1PcInstance) cha;
			pc.addDg(8);
		}
			break;
		case WIND_SHOT:
			cha.addBowHitup(-6);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(148, 0));
			}
			break;
		case STORM_EYE:
			cha.addBowHitup(-2);
			cha.addBowDmgup(-3);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(155, 0));
			}
			break;
		case STORM_SHOT:
			cha.addBowDmgup(-5);
			cha.addBowHitup(3);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(165, 0));
			}
			break;
		case BERSERKERS:
			cha.getAC().addAc(-10);
			cha.addDmgup(-5);
			cha.addHitup(-5);
			break;
		case SCALES_EARTH_DRAGON:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getAC().addAc(3);
				pc.getResistance().addHold(-10);
				pc.sendPackets(new S_OwnCharAttrDef(pc));
				// pc.stopMpDecreaseByScales();
				// L1PolyMorph.undoPoly(pc);
				// pc.addMaxHp(-35);
				// pc.getAC().addAc(8);
				// pc.sendPackets(new S_OwnCharAttrDef(pc));
				// pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(),
				// pc.getMaxHp()));
				// if (pc.isInParty()) {
				// pc.getParty().updateMiniHP(pc);
				// }
				// pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(),
				// pc.getMaxMp()));
			}
			break;
		case SCALES_WATER_DRAGON:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getResistance().addFreeze(-10);
				// pc.stopMpDecreaseByScales();
				// L1PolyMorph.undoPoly(pc);
				// pc.getResistance().addMr(-15);
				// pc.getResistance().addAllNaturalResistance(-15);
				// pc.sendPackets(new S_SPMR(pc));
				// pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
			break;
		case SCALES_FIRE_DRAGON:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getResistance().addStun(-10);
				pc.addHitup(-5);
				// pc.stopMpDecreaseByScales();
				// L1PolyMorph.undoPoly(pc);
				// pc.getAbility().addAddedStr((byte) -3);
				// pc.getAbility().addAddedDex((byte) -3);
				// pc.getAbility().addAddedCon((byte) -3);
				// pc.getAbility().addAddedInt((byte) -3);
				// pc.getAbility().addAddedWis((byte) -3);
			}
			break;
		case IllUSION_OGRE:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addDmgup(-4);
				pc.addHitup(-4);
			}
			break;
		case IllUSION_LICH:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
//				pc.addSp(-2);
				pc.getAbility().addSp(-2);
				pc.sendPackets(new S_SPMR(pc));
			}
			break;
		case IllUSION_DIAMONDGOLEM:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getAC().addAc(8);
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
			break;
		case IllUSION_AVATAR:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addDmgup(-10);
			}
			break;
		case INSIGHT:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getAbility().addAddedStr((byte) -1);
				pc.getAbility().addAddedDex((byte) -1);
				pc.getAbility().addAddedCon((byte) -1);
				pc.getAbility().addAddedInt((byte) -1);
				pc.getAbility().addAddedWis((byte) -1);
				pc.resetBaseMr();
			}
			break;
		case Tam_Fruit1:// tam
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getAC().addAc(1);
				pc.sendPackets(new S_OwnCharStatus(pc));
				int tamcount = pc.tamcount();
				if (tamcount > 0) {
					long tamtime = pc.TamTime();
					pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.BUFF_WINDOW,
							tamtime, tamcount, true), true);
					if (tamcount == 1) {
						pc.setSkillEffect(Tam_Fruit1,
								(int) tamtime);
						pc.getAC().addAc(-1);
					} else if (tamcount == 2) {
						pc.setSkillEffect(Tam_Fruit2,
								(int) tamtime);
						pc.getAC().addAc(-2);
					} else if (tamcount == 3) {
						pc.setSkillEffect(Tam_Fruit3,
								(int) tamtime);
						pc.getAC().addAc(-3); 
					} else if (tamcount == 4) {
						pc.setSkillEffect(Tam_Fruit4,
								(int) tamtime);
						pc.getAC().addAc(-4); 
					} else if (tamcount == 5) {
						pc.setSkillEffect(Tam_Fruit5,
								(int) tamtime);
						pc.getAC().addAc(-5); 
					}
					pc.sendPackets(new S_OwnCharStatus(pc));
				}
				/*
				 * if(pc.Tam_wait_count()!=0){ Timestamp deleteTime = null;
				 * deleteTime = new Timestamp(System.currentTimeMillis() +
				 * (86400000 * (long)pc.Nexttam(pc.getId()))+10000);//7일
				 * pc.setTamTime(deleteTime); pc.tamdel(pc.getId()); try {
				 * pc.save(); } catch (Exception e) { e.printStackTrace(); } }
				 */
			}
			break;
		case Tam_Fruit2:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getAC().addAc(2);
				pc.sendPackets(new S_OwnCharStatus(pc));
				int tamcount = pc.tamcount();
				if (tamcount > 0) {
					long tamtime = pc.TamTime();
					pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.BUFF_WINDOW,
							tamtime, tamcount, true), true);
					if (tamcount == 1) {
						pc.setSkillEffect(Tam_Fruit1,
								(int) tamtime);
						pc.getAC().addAc(-1);
					} else if (tamcount == 2) {
						pc.setSkillEffect(Tam_Fruit2,
								(int) tamtime);
						pc.getAC().addAc(-2);
					} else if (tamcount == 3) {
						pc.setSkillEffect(Tam_Fruit3,
								(int) tamtime);
						pc.getAC().addAc(-3);
					}else if (tamcount == 4) {
						pc.setSkillEffect(Tam_Fruit4,
								(int) tamtime);
						pc.getAC().addAc(-4); 
					} else if (tamcount == 5) {
						pc.setSkillEffect(Tam_Fruit5,
								(int) tamtime);
						pc.getAC().addAc(-5); 
					}
					pc.sendPackets(new S_OwnCharStatus(pc));
				}
				/*
				 * if(pc.Tam_wait_count()!=0){ Timestamp deleteTime = null;
				 * deleteTime = new Timestamp(System.currentTimeMillis() +
				 * (86400000 * (long)pc.Nexttam(pc.getId()))+10000);//7일
				 * pc.setTamTime(deleteTime); pc.tamdel(pc.getId()); try {
				 * pc.save(); } catch (Exception e) { e.printStackTrace(); } }
				 */
			}
			break;
		case Tam_Fruit3:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getAC().addAc(3);
				pc.sendPackets(new S_OwnCharStatus(pc));
				int tamcount = pc.tamcount();
				if (tamcount > 0) {
					long tamtime = pc.TamTime();
					pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.BUFF_WINDOW,
							tamtime, tamcount, true), true);

					if (tamcount == 1) {
						pc.setSkillEffect(Tam_Fruit1,
								(int) tamtime);
						pc.getAC().addAc(-1);
					} else if (tamcount == 2) {
						pc.setSkillEffect(Tam_Fruit2,
								(int) tamtime);
						pc.getAC().addAc(-2);
					} else if (tamcount == 3) {
						pc.setSkillEffect(Tam_Fruit3,
								(int) tamtime);
						pc.getAC().addAc(-3);
					}else if (tamcount == 4) {
						pc.setSkillEffect(Tam_Fruit4,
								(int) tamtime);
						pc.getAC().addAc(-4); 
					} else if (tamcount == 5) {
						pc.setSkillEffect(Tam_Fruit5,
								(int) tamtime);
						pc.getAC().addAc(-5); 
					}
					pc.sendPackets(new S_OwnCharStatus(pc));
				}
			
			}
			break;
		case Tam_Fruit4:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getAC().addAc(4);
				pc.sendPackets(new S_OwnCharStatus(pc));
				int tamcount = pc.tamcount();
				if (tamcount > 0) {
					long tamtime = pc.TamTime();
					pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.BUFF_WINDOW,
							tamtime, tamcount, true), true);

					if (tamcount == 1) {
						pc.setSkillEffect(Tam_Fruit1,
								(int) tamtime);
						pc.getAC().addAc(-1);
					} else if (tamcount == 2) {
						pc.setSkillEffect(Tam_Fruit2,
								(int) tamtime);
						pc.getAC().addAc(-2);
					} else if (tamcount == 3) {
						pc.setSkillEffect(Tam_Fruit3,
								(int) tamtime);
						pc.getAC().addAc(-3);
					}else if (tamcount == 4) {
						pc.setSkillEffect(Tam_Fruit4,
								(int) tamtime);
						pc.getAC().addAc(-4); 
					} else if (tamcount == 5) {
						pc.setSkillEffect(Tam_Fruit5,
								(int) tamtime);
						pc.getAC().addAc(-5); 
					}
					pc.sendPackets(new S_OwnCharStatus(pc));
				}
			
			}
			break;
		case Tam_Fruit5:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getAC().addAc(5);
				pc.sendPackets(new S_OwnCharStatus(pc));
				int tamcount = pc.tamcount();
				if (tamcount > 0) {
					long tamtime = pc.TamTime();
					pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.BUFF_WINDOW,
							tamtime, tamcount, true), true);

					if (tamcount == 1) {
						pc.setSkillEffect(Tam_Fruit1,
								(int) tamtime);
						pc.getAC().addAc(-1);
					} else if (tamcount == 2) {
						pc.setSkillEffect(Tam_Fruit2,
								(int) tamtime);
						pc.getAC().addAc(-2);
					} else if (tamcount == 3) {
						pc.setSkillEffect(Tam_Fruit3,
								(int) tamtime);
						pc.getAC().addAc(-3);
					}else if (tamcount == 4) {
						pc.setSkillEffect(Tam_Fruit4,
								(int) tamtime);
						pc.getAC().addAc(-4); 
					} else if (tamcount == 5) {
						pc.setSkillEffect(Tam_Fruit5,
								(int) tamtime);
						pc.getAC().addAc(-5); 
					}
					pc.sendPackets(new S_OwnCharStatus(pc));
				}
			
			}
			break;
		case SHAPE_CHANGE:
			L1PolyMorph.undoPoly(cha);
			break;
		case 천하장사버프: {
			L1PcInstance pc = (L1PcInstance) cha;
			pc.addDamageReductionByArmor(-5);
			pc.sendPackets(new S_PacketBox(S_PacketBox.ICON_COOKING, pc, 187, 0));
			pc.setDessertId(0);
			break;
		}
		case GIGANTIC:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-pc.getGiganticHp());
				pc.setGiganticHp(0);
				if (pc.isInParty())
					pc.getParty().updateMiniHP(pc);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			}
			break;
		case POWERRIP:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_RIP, false));
			} else if (cha instanceof L1MonsterInstance || cha instanceof L1SummonInstance || cha instanceof L1PetInstance) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				npc.set발묶임상태(false);
			}
			break;
		case DESPERADO:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_PERADO, false));
			} else if (cha instanceof L1MonsterInstance || cha instanceof L1SummonInstance || cha instanceof L1PetInstance) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				npc.set발묶임상태(false);
			}
			break;
		case ADVANCE_SPIRIT:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-pc.getAdvenHp());
				pc.addMaxMp(-pc.getAdvenMp());
				pc.setAdvenHp(0);
				pc.setAdvenMp(0);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) {
					pc.getParty().updateMiniHP(pc);
				}
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
			}
			break;
		case HASTE:
		case GREATER_HASTE:
			cha.setMoveSpeed(0);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
			}
			break;
		case HOLY_WALK:
		case MOVING_ACCELERATION:
		case WIND_WALK:
			cha.setBraveSpeed(0);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
			}
			break;
		case BLOOD_LUST:
			cha.setBraveSpeed(0);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillBrave(pc.getId(), 1, 0));
			}
			break;
		case CURSE_BLIND:
		case DARKNESS:
		case LINDBIOR_SPIRIT_EFFECT:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_CurseBlind(0));
			}
			break;
		case CURSE_PARALYZE:
		case DESERT_SKILL1:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Poison(pc.getId(), 0));
				pc.broadcastPacket(new S_Poison(pc.getId(), 0));
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_PARALYSIS, false));
			}
			break;
		case WEAKNESS:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addDmgup(5);
				pc.addHitup(1);
			}
			break;
		case DISEASE:
		case MOB_DISEASE_1:
		case MOB_DISEASE_30:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addDmgup(6);
				pc.getAC().addAc(-12);
			}
			break;
		case GUARD_BREAK:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getAC().addAc(-10);
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
			break;
		case HORROR_OF_DEATH:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getAbility().addAddedStr((byte) 3);
				pc.getAbility().addAddedInt((byte) 3);
			}
			break;
		case PANIC:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getAbility().addAddedStr((byte) 1);
				pc.getAbility().addAddedDex((byte) 1);
				pc.getAbility().addAddedCon((byte) 1);
				pc.getAbility().addAddedInt((byte) 1);
				pc.getAbility().addAddedWis((byte) 1);
				pc.getAbility().addAddedCha((byte) 1);
				pc.resetBaseMr();
			}
			break;
		case ICE_LANCE:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Poison(pc.getId(), 0));
				pc.broadcastPacket(new S_Poison(pc.getId(), 0));
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, false));
			} else if (cha instanceof L1MonsterInstance || cha instanceof L1SummonInstance || cha instanceof L1PetInstance) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				npc.broadcastPacket(new S_Poison(npc.getId(), 0));
				npc.setParalyzed(false);
			}
			break;
		case EARTH_BIND:
		case MOB_BASILL:
		case MOB_COCA:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Poison(pc.getId(), 0));
				pc.broadcastPacket(new S_Poison(pc.getId(), 0));
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, false));
			} else if (cha instanceof L1MonsterInstance || cha instanceof L1SummonInstance || cha instanceof L1PetInstance) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				npc.broadcastPacket(new S_Poison(npc.getId(), 0));
				npc.setParalyzed(false);
			}
			break;
		case SHOCK_STUN:
		case MOB_SHOCKSTUN_30:
		case MOB_RANGESTUN_18:
		case MOB_RANGESTUN_19:
		case MOB_RANGESTUN_20:
		case Mob_RANGESTUN_30: // 쇼크 스탠
		case ANTA_MESSAGE_6:
		case ANTA_MESSAGE_7:
		case ANTA_MESSAGE_8:
		case ANTA_SHOCKSTUN:
		case OMAN_STUN:// 오만 스턴
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, false));
			} else if (cha instanceof L1MonsterInstance || cha instanceof L1SummonInstance || cha instanceof L1PetInstance) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				npc.setParalyzed(false);
			}
			break;
		case BONE_BREAK:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, false));
			} else if (cha instanceof L1MonsterInstance || cha instanceof L1SummonInstance || cha instanceof L1PetInstance) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				npc.setParalyzed(false);
			}
			break;
		case PHANTASM:
		case FOG_OF_SLEEPING:
			cha.setSleeped(false);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_SLEEP, false));
				pc.sendPackets(new S_OwnCharStatus(pc));
			}
			break;
		case ABSOLUTE_BARRIER:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				// pc.startMpRegeneration();
				pc.startMpRegenerationByDoll();
			}
			break;
		case WIND_SHACKLE:
		case MOB_WINDSHACKLE_1:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconWindShackle(pc.getId(), 0));
			}
			break;
		case SLOW:
		case ENTANGLE:
		case GREATER_SLOW:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
			}
			cha.setMoveSpeed(0);
			break;
		case STATUS_FREEZE:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, false));
			} else if (cha instanceof L1MonsterInstance || cha instanceof L1SummonInstance || cha instanceof L1PetInstance) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				npc.setParalyzed(false);
			}
			break;
		case STATUS_IGNITION:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getResistance().addFire(-30);
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
			break;
		case STATUS_QUAKE:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getResistance().addEarth(-30);
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
			break;
		case STATUS_SHOCK:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getResistance().addWind(-30);
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
			break;
		case STATUS_BRAVE:
		case STATUS_ELFBRAVE:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
			}
			cha.setBraveSpeed(0);
			break;
		case STATUS_HASTE:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
			}
			cha.setMoveSpeed(0);
			break;
		case STATUS_BLUE_POTION:
		case STATUS_BLUE_POTION2:
		case STATUS_FRUIT:
			break;
		case STATUS_UNDERWATER_BREATH:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconBlessOfEva(pc.getId(), 0));
			}
			break;
		case STATUS_WISDOM_POTION:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
//				cha.addSp(-2);
				cha.getAbility().addSp(-2);
				pc.addMpr(-2);
				pc.sendPackets(new S_SkillIconWisdomPotion(0));
			}
			break;
		case STATUS_CHAT_PROHIBITED:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_ServerMessage(288));
			}
			break;
		case STATUS_CASHSCROLL:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-50);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) {
					pc.getParty().updateMiniHP(pc);
				}
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
			}
			break;
		case STATUS_CASHSCROLL2:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxMp(-40);
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
			}
			break;
		case STATUS_CASHSCROLL3:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addDmgup(-3);
				pc.addHitup(-3);
//				pc.addSp(-3);
				pc.getAbility().addSp(-3);
				pc.sendPackets(new S_SPMR(pc));
			}
			break;
		case STATUS_POISON:
			cha.curePoison();
			break;

		case COOKING_1_0_N:
		case COOKING_1_0_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getResistance().addAllNaturalResistance(-10);
				pc.sendPackets(new S_OwnCharAttrDef(pc));
				pc.sendPackets(new S_PacketBox(53, 0, 0));
				pc.setCookingId(0);
			}
			break;
		case COOKING_1_1_N:
		case COOKING_1_1_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-30);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) {
					pc.getParty().updateMiniHP(pc);
				}
				pc.sendPackets(new S_PacketBox(53, 1, 0));
				pc.setCookingId(0);
			}
			break;
		case COOKING_1_2_N:
		case COOKING_1_2_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 2, 0));
				pc.setCookingId(0);
			}
			break;
		case COOKING_1_3_N:
		case COOKING_1_3_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getAC().addAc(1);
				pc.sendPackets(new S_PacketBox(53, 3, 0));
				pc.setCookingId(0);
			}
			break;
		case COOKING_1_4_N:
		case COOKING_1_4_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxMp(-20);
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				pc.sendPackets(new S_PacketBox(53, 4, 0));
				pc.setCookingId(0);
			}
			break;
		case COOKING_1_5_N:
		case COOKING_1_5_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 5, 0));
				pc.setCookingId(0);
			}
			break;
		case COOKING_1_6_N:
		case COOKING_1_6_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getResistance().addMr(-5);
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_PacketBox(53, 6, 0));
				pc.setCookingId(0);
			}
			break;
		case COOKING_1_7_N:
		case COOKING_1_7_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 7, 0));
				pc.setDessertId(0);
			}
			break;
		case COOKING_1_8_N:
		case COOKING_1_8_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 16, 0));
				pc.setCookingId(0);
			}
			break;
		case COOKING_1_9_N:
		case COOKING_1_9_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxMp(-30);
				pc.addMaxHp(-30);
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				pc.sendPackets(new S_PacketBox(53, 17, 0));
				pc.setCookingId(0);
			}
			break;
		case COOKING_1_10_N:
		case COOKING_1_10_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getAC().addAc(2);
				pc.sendPackets(new S_OwnCharStatus2(pc));
				pc.sendPackets(new S_PacketBox(53, 18, 0));
				pc.setCookingId(0);
			}
			break;
		case COOKING_1_11_N:
		case COOKING_1_11_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 19, 0));
				pc.setCookingId(0);
			}
			break;
		case COOKING_1_12_N:
		case COOKING_1_12_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 20, 0));
				pc.setCookingId(0);
			}
			break;
		case COOKING_1_13_N:
		case COOKING_1_13_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getResistance().addMr(-10);
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_PacketBox(53, 21, 0));
				pc.setCookingId(0);
			}
			break;
		case COOKING_1_14_N:
		case COOKING_1_14_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
//				pc.addSp(-1);
				pc.getAbility().addSp(-1);
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_PacketBox(53, 22, 0));
				pc.setCookingId(0);
			}
			break;
		case COOKING_1_15_N:
		case COOKING_1_15_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 7, 0));
				pc.setDessertId(0);
			}
			break;
		case COOKING_1_16_N:
		case COOKING_1_16_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addBowHitRate(-2);
				pc.addBowDmgup(-1);
				pc.sendPackets(new S_PacketBox(53, 45, 0));
				pc.setCookingId(0);
			}
			break;
		case COOKING_1_17_N:
		case COOKING_1_17_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-50);
				pc.addMaxMp(-50);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) {
					pc.getParty().updateMiniHP(pc);
				}
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				pc.sendPackets(new S_PacketBox(53, 46, 0));
				pc.setCookingId(0);
			}
			break;
		case COOKING_1_18_N:
		case COOKING_1_18_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addHitup(-2);
				pc.addDmgup(-1);
				pc.sendPackets(new S_PacketBox(53, 47, 0));
				pc.setCookingId(0);
			}
			break;
		case COOKING_1_19_N:
		case COOKING_1_19_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getAC().addAc(3);
				pc.sendPackets(new S_OwnCharStatus2(pc));
				pc.sendPackets(new S_PacketBox(53, 48, 0));
				pc.setCookingId(0);
			}
			break;
		case COOKING_1_20_N:
		case COOKING_1_20_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getResistance().addAllNaturalResistance(-10);
				pc.getResistance().addMr(-15);
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_OwnCharAttrDef(pc));
				pc.sendPackets(new S_PacketBox(53, 49, 0));
				pc.setCookingId(0);
			}
			break;
		case COOKING_1_21_N:
		case COOKING_1_21_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
//				pc.addSp(-2);
				pc.getAbility().addSp(-2);
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_PacketBox(53, 50, 0));
				pc.setCookingId(0);
			}
			break;
		case COOKING_1_22_N:
		case COOKING_1_22_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-30);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) {
					pc.getParty().updateMiniHP(pc);
				}
				pc.sendPackets(new S_PacketBox(53, 51, 0));
				pc.setCookingId(0);
			}
			break;
		case COOKING_1_23_N:
		case COOKING_1_23_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 7, 0));
				pc.setDessertId(0);
			}
			break;
		case COMA_A:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				// pc.sendPackets(new S_Coma(40, 0));
				pc.getAbility().addAddedCon(-1);
				pc.getAbility().addAddedDex(-5);
				pc.getAbility().addAddedStr(-5);
				pc.addHitRate(-3);
				pc.getAC().addAc(3);
			}
			break;
		case COMA_B:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				// pc.sendPackets(new S_Coma(41, 0));
//				pc.addSp(-1);
				pc.getAbility().addSp(-1);
				pc.getAbility().addAddedCon(-3);
				pc.getAbility().addAddedDex(-5);
				pc.getAbility().addAddedStr(-5);
				pc.addHitRate(-5);
				pc.getAC().addAc(8);
				pc.sendPackets(new S_SPMR(pc));
			}
			break;
		case FEATHER_BUFF_A:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addHpr(-3);
				pc.addMpr(-3);
				pc.addDmgup(-2);
				pc.addHitup(-2);
				pc.addMaxHp(-50);
				pc.addMaxMp(-30);
//				pc.addSp(-2);
				pc.getAbility().addSp(-2);
				pc.sendPackets(new S_SPMR(pc));
				if (pc.isInParty()) {
					pc.getParty().updateMiniHP(pc);
				}
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
			}
			break;
		case FEATHER_BUFF_B:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addHitup(-2);
//				pc.addSp(-1);
				pc.getAbility().addSp(-1);
				pc.addMaxHp(-50);
				pc.addMaxMp(-30);
				pc.sendPackets(new S_SPMR(pc));
				if (pc.isInParty()) {
					pc.getParty().updateMiniHP(pc);
				}
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
			}
			break;
		case FEATHER_BUFF_C:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-50);
				pc.addMaxMp(-30);
				pc.getAC().addAc(2);
				pc.sendPackets(new S_OwnCharAttrDef(pc));
				if (pc.isInParty()) {
					pc.getParty().updateMiniHP(pc);
				}
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
			}
			break;
		case FEATHER_BUFF_D:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getAC().addAc(1);
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
			break;
		case ANTA_MAAN:// 지룡의 마안
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getAC().addAc(2);
				pc.getResistance().addHold(-15);
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
			break;
		case FAFU_MAAN:// 수룡의 마안
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getResistance().addFreeze(-15);
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
			break;
		case LIND_MAAN:// 풍룡의 마안
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
//				pc.addSp(-1);
				pc.getAbility().addSp(-1);
				pc.getResistance().addSleep(-15);
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
			break;
		case VALA_MAAN:// 화룡의 마안
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addDmgup(-2);
				pc.getResistance().addStun(-15);
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
			break;
		case BIRTH_MAAN:// 탄생의 마안
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getAC().addAc(2);
				pc.getResistance().addHold(-15);
				pc.getResistance().addFreeze(-15);
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
			break;
		case SHAPE_MAAN:// 형상의 마안
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getAC().addAc(3);
//				pc.addSp(-1);
				pc.getAbility().addSp(-1);
				pc.getResistance().addHold(-15);
				pc.getResistance().addFreeze(-15);
				pc.getResistance().addSleep(-15);
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
			break;
		case LIFE_MAAN:// 생명의 마안
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addDmgup(-2);
				pc.getAC().addAc(5);
//				pc.addSp(-1);
				pc.getAbility().addSp(-1);
				pc.getResistance().addHold(-15);
				pc.getResistance().addFreeze(-15);
				pc.getResistance().addSleep(-15);
				pc.getResistance().addStun(-15);
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
			break;
		case ANTA_BUFF:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getAC().addAc(2);
				pc.getResistance().addWater(-50);
				pc.sendPackets(new S_OwnCharStatus(pc));
				pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONBLOOD, 82, 0));
			}
			break;
		case FAFU_BUFF:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addHpr(-3);
				pc.addMpr(-1);
				pc.getResistance().addWind(50);
				pc.sendPackets(new S_OwnCharStatus(pc));
				pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONBLOOD, 85, 0));
			}
			break;
		case VALA_BUFF:
		case RIND_BUFF:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addHitup(-3);
				pc.addBowHitup(-3);
				pc.getResistance().addFire(-50);
				pc.sendPackets(new S_OwnCharStatus(pc));
				pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONBLOOD, 88, 0));
			}
			break;
		case STATUS_DRAGON_PEARL: // 드래곤의 진주
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Liquor(pc.getId(), 0));
				pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGON_PEARL, 0, 0));
				pc.sendPackets(new S_ServerMessage(185));
				pc.setPearl(0);
			}
			break;
		case BOUNCE_ATTACK: {
			L1PcInstance pc = (L1PcInstance) cha;
			pc.addHitup(-6);
		}
			break;
		case COOK_STR: {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addDmgup(-2);
				pc.addHitup(-1);
				pc.addHpr(-2);
				pc.addMpr(-2);
				pc.getResistance().addMr(-10);
				pc.getResistance().addAllNaturalResistance(-10);
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_PacketBox(53, 157, 0));
				pc.setCookingId(0);
			}
		}
			break;
		case COOK_DEX: {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addBowDmgup(-2);
				pc.addBowHitup(-1);
				pc.addHpr(-2);
				pc.addMpr(-2);
				pc.getResistance().addMr(-10);
				pc.getResistance().addAllNaturalResistance(-10);
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_PacketBox(53, 158, 0));
				pc.setCookingId(0);
			}
		}
			break;
		case COOK_INT: {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
//				pc.addSp(-2);
				pc.getAbility().addSp(-2);
				pc.addHpr(-2);
				pc.addMpr(-3);
				pc.getResistance().addMr(-10);
				pc.getResistance().addAllNaturalResistance(-10);
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_PacketBox(53, 159, 0));
				pc.setCookingId(0);
			}
		}
			break;
		case COOK_GROW: {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 160, 0));
				pc.setDessertId(0);
			}
		}
			break;
		case RINDVIOR_WIND_SHACKLE:
		case RINDVIOR_WIND_SHACKLE_1:
		case DRAKE_WIND_SHACKLE:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconWindShackle(pc.getId(), 0));
			}
			break;
		case 강화버프_활력:
			if(cha instanceof L1PcInstance){
				L1PcInstance pc = (L1PcInstance) cha;
			pc.addMaxHp(-50);
			pc.addMaxMp(-50);
			pc.addWeightReduction(-3);
			pc.sendPackets(new S_HPUpdate(pc));
			pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
			}
			break;
			
		case 강화버프_공격:
			if(cha instanceof L1PcInstance){
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addDmgup(-1);
				pc.addBowDmgup(-1);
			}
			break;
			
		case 강화버프_방어:
			if(cha instanceof L1PcInstance){
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addDamageReductionByArmor(-1);
			}
			break;
			
		case 강화버프_마법:
			if(cha instanceof L1PcInstance){
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getAbility().addSp(-1);
				pc.sendPackets(new S_SPMR(pc));
			}
			break;
			
		case 강화버프_스턴:
			if(cha instanceof L1PcInstance){
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getResistance().addStun(-2);
			}
			break;
			
		case 강화버프_홀드:
			if(cha instanceof L1PcInstance){
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getResistance().addHold(-2);
			}
			break;
			
		case 강화버프_힘:
			if(cha instanceof L1PcInstance){
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getAbility().addAddedStr(-1);
				pc.sendPackets(new S_OwnCharStatus2(pc), true);
				
			}
			break;
		case 강화버프_덱스:
			if(cha instanceof L1PcInstance){
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getAbility().addAddedDex((byte)-1);
				pc.sendPackets(new S_OwnCharStatus2(pc), true);
			}
			break;
		case 강화버프_인트:
			if(cha instanceof L1PcInstance){
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getAbility().addAddedInt((byte)-1);
				pc.sendPackets(new S_OwnCharStatus2(pc), true);
			}
			break;
		case 메티스스프:
			if(cha instanceof L1PcInstance){
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getResistance().addStun(-1);
				pc.getResistance().addMr(-10);
				pc.getResistance().addHold(-1);
				pc.sendPackets(new S_SPMR(pc));
			}
			break;
		case 메티스요리:
			if(cha instanceof L1PcInstance){
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addDmgCritical(-3);
				pc.addBowDmgCritical(-3);
				pc.addHitup(-3);
				pc.addBowHitup(-3);
				pc.addDmgup(-3);
				pc.addBowDmgCritical(-3);
				pc.getAbility().addSp(-3);
				pc.sendPackets(new S_SPMR(pc));
			}
			break;
		case Matiz_Buff2: //방어
			if(cha instanceof L1PcInstance){
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getResistance().addMr(-10);
				pc.addDamageReductionByArmor(-2);
				pc.addMaxHp(-100);
				pc.addHpr(-2);
				pc.sendPackets(new S_HPUpdate(pc));
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				pc.sendPackets(new S_SPMR(pc));
			}
			break;
		case Matiz_Buff3 ://공격
			if(cha instanceof L1PcInstance){
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addDmgup(-3);
				pc.addBowDmgup(-3);
				pc.getAbility().addSp(-3);
				pc.addMaxMp(-50);
				pc.addMpr(-2);
				pc.sendPackets(new S_HPUpdate(pc));
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				pc.sendPackets(new S_SPMR(pc));
			}
			break;
		default:
			break;
		}

		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			sendStopMessage(pc, skillId);
			pc.sendPackets(new S_OwnCharStatus(pc));
		}
	}

	private static void sendStopMessage(L1PcInstance charaPc, int skillid) {
		L1Skills l1skills = SkillsTable.getInstance().getTemplate(skillid);
		if (l1skills == null || charaPc == null) {
			return;
		}

		int msgID = l1skills.getSysmsgIdStop();
		if (msgID > 0) {
			charaPc.sendPackets(new S_ServerMessage(msgID));
		}
	}
}