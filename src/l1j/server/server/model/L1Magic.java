package l1j.server.server.model;

import static l1j.server.server.model.skill.L1SkillId.*;

import java.util.Random;

import l1j.server.Config;
import l1j.server.GameSystem.Boss.BossAlive;
import l1j.server.GameSystem.Robot.L1RobotInstance;
import l1j.server.server.ActionCodes;
import l1j.server.server.Controller.WarTimeController;
import l1j.server.server.datatables.CharacterReduc;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Skills;
import l1j.server.server.utils.CalcStat;

public class L1Magic {

	private int _calcType;

	private final int PC_PC = 1;

	private final int PC_NPC = 2;

	private final int NPC_PC = 3;

	private final int NPC_NPC = 4;

	private L1PcInstance _pc = null;

	private L1PcInstance _targetPc = null;

	private L1NpcInstance _npc = null;

	private L1NpcInstance _targetNpc = null;

	private int _leverage = 10;

	private static Random _random = new Random(System.nanoTime());

	public boolean _CriticalDamage = false;

	public boolean isCriticalDamage() {
		return _CriticalDamage;
	}

	public void setLeverage(int i) {
		_leverage = i;
	}

	private int getLeverage() {
		return _leverage;
	}

	public L1Magic(L1Character attacker, L1Character target) {
		if (attacker instanceof L1PcInstance) {
			if (target instanceof L1PcInstance) {
				_calcType = PC_PC;
				_pc = (L1PcInstance) attacker;
				_targetPc = (L1PcInstance) target;
			} else {
				_calcType = PC_NPC;
				_pc = (L1PcInstance) attacker;
				_targetNpc = (L1NpcInstance) target;
			}
		} else {
			if (target instanceof L1PcInstance) {
				_calcType = NPC_PC;
				_npc = (L1NpcInstance) attacker;
				_targetPc = (L1PcInstance) target;
			} else {
				_calcType = NPC_NPC;
				_npc = (L1NpcInstance) attacker;
				_targetNpc = (L1NpcInstance) target;
			}
		}
	}

	private int getSpellPower() {
		int spellPower = 0;
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			spellPower = _pc.getAbility().getSp();
		} else if (_calcType == NPC_PC || _calcType == NPC_NPC) {
			spellPower = _npc.getAbility().getSp();
		}
		return spellPower;
	}

	private int getMagicLevel() {
		int magicLevel = 0;
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			magicLevel = _pc.getAbility().getMagicLevel();
		} else if (_calcType == NPC_PC || _calcType == NPC_NPC) {
			magicLevel = _npc.getAbility().getMagicLevel();
		}
		return magicLevel;
	}

	private int getMagicBonus() {
		int magicBonus = 0;
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			magicBonus = _pc.getAbility().getMagicBonus();
		} else if (_calcType == NPC_PC || _calcType == NPC_NPC) {
			magicBonus = _npc.getAbility().getMagicBonus();
		}
		return magicBonus;
	}

	private int getLawful() {
		int lawful = 0;
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			lawful = _pc.getLawful();
		} else if (_calcType == NPC_PC || _calcType == NPC_NPC) {
			lawful = _npc.getLawful();
		}
		return lawful;
	}

	private int getTargetMr() {
		int mr = 0;
		if (_calcType == PC_PC || _calcType == NPC_PC) {
			mr = _targetPc.getResistance().getEffectedMrBySkill();
		} else {
			mr = _targetNpc.getResistance().getEffectedMrBySkill();
		}
		return mr;
	}

	private int getMagicHitupByArmor() {
		int HitupByArmor = 0;
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			HitupByArmor = _pc.getMagicHitupByArmor();
		} else if (_calcType == NPC_PC || _calcType == NPC_NPC) {
			HitupByArmor = 0;
		}
		return HitupByArmor;
	}

	/* ■■■■■■■■■■■■■■ 성공 판정 ■■■■■■■■■■■■■ */
	// ●●●● 확률계 마법의 성공 판정 ●●●●
	// 계산방법
	// 공격측 포인트：LV + ((MagicBonus * 3) * 마법 고유 계수)
	// 방어측 포인트：((LV / 2) + (MR * 3)) / 2
	// 공격 성공율：공격측 포인트 - 방어측 포인트
	public boolean calcProbabilityMagic(int skillId) {
		int probability = 0;
		boolean isSuccess = false;

		if (_pc != null && _pc.isGm()) {
			return true;
		}

		if (_calcType == PC_NPC && _targetNpc != null) {
			int npcId = _targetNpc.getNpcTemplate().get_npcId();
			if (npcId >= 45912 && npcId <= 45915 && !_pc.hasSkillEffect(STATUS_HOLY_WATER)) {
				return false;
			}
			if (npcId == 45916 && !_pc.hasSkillEffect(STATUS_HOLY_MITHRIL_POWDER)) {
				return false;
			}
			if (npcId == 45941 && !_pc.hasSkillEffect(STATUS_HOLY_WATER_OF_EVA)) {
				return false;
			}
			if (npcId == 45752 && !_pc.hasSkillEffect(STATUS_CURSE_BARLOG)) {
				return false;
			}
			if (npcId == 45753 && !_pc.hasSkillEffect(STATUS_CURSE_BARLOG)) {
				return false;
			}
			if (npcId == 45675 && !_pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
				return false;
			}
			if (npcId == 81082 && !_pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
				return false;
			}
			if (npcId == 45625 && !_pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
				return false;
			}
			if (npcId == 45674 && !_pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
				return false;
			}
			if (npcId == 45685 && !_pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
				return false;
			}
			if (npcId >= 46068 && npcId <= 46091 && _pc.getTempCharGfx() == 6035) {
				return false;
			}
			if (npcId >= 46092 && npcId <= 46106 && _pc.getTempCharGfx() == 6034) {
				return false;
			}
			if (_targetNpc.getNpcTemplate().get_gfxid() == 7684 && !_pc.hasSkillEffect(PAP_FIVEPEARLBUFF)) {
				return false;
			}
			if (_targetNpc.getNpcTemplate().get_gfxid() == 7805 && !_pc.hasSkillEffect(PAP_MAGICALPEARLBUFF)) {
				return false;
			}
			if (_targetNpc.getNpcTemplate().get_gfxid() == 7720) {
				return false;
			}
		}

		if (!checkZone(skillId)) {
			return false;
		}
		if (skillId == CANCELLATION) {
			/*L1ItemInstance findItem = _pc.getInventory().findItemId(11284);
			if(findItem != null){
				return true;
			}*/
			if (_calcType == PC_PC && _pc != null && _targetPc != null) {

				if (_pc.getId() == _targetPc.getId()) {
					return true;
				}
				if (_pc.getClanid() > 0 && (_pc.getClanid() == _targetPc.getClanid()) && (_pc.get_DuelLine() == _targetPc.get_DuelLine())) {
					_targetPc.sendPackets(new S_SystemMessage("혈맹원 " + _pc.getName() + " 님이 캔슬레이션 마법을 시전했습니다."));
					return true;
				}
				if (_pc.isInParty()) {
					if (_pc.getParty().isMember(_targetPc)) {
						_targetPc.sendPackets(new S_SystemMessage("파티원 " + _pc.getName() + " 님이 캔슬레이션 마법을 시전했습니다."));
						return true;
					}
				}
				// 대상이 인비지 상태일땐 켄슬 무효
				if (_targetPc.isInvisble()) {
					return false;
				}

				if (_pc.getZoneType() == 1 || _targetPc.getZoneType() == 1) {
					return false;
				}
			}
			if (_calcType == PC_NPC || _calcType == NPC_PC || _calcType == NPC_NPC) {
				return true;
			}
		}

		// 50렙 이상 npc 에게 아래 마법 안걸림.
		if (_calcType == PC_NPC && _targetNpc.getLevel() >= 50 && _targetNpc.getNpcTemplate().isCantResurrect()) {
			if (skillId == WEAPON_BREAK || skillId == SLOW || skillId == CURSE_PARALYZE || skillId == MANA_DRAIN
					|| skillId == WEAKNESS || skillId == DISEASE || skillId == DECAY_POTION || skillId == GREATER_SLOW
					|| skillId == ENTANGLE || skillId == ERASE_MAGIC || skillId == AREA_OF_SILENCE
					|| skillId == WIND_SHACKLE || skillId == STRIKER_GALE || skillId == SHOCK_STUN
					|| skillId == FOG_OF_SLEEPING || skillId == ICE_LANCE || skillId == BONE_BREAK
					|| skillId == POLLUTE_WATER || skillId == ELEMENTAL_FALL_DOWN || skillId == RETURN_TO_NATURE
					|| skillId == THUNDER_GRAB || skillId == ARMOR_BRAKE || skillId == SILENCE
					|| skillId == DARKNESS /*|| skillId == DESPERADO || skillId == POWERRIP*/ ) {
				return false;
			}
		} 

		/*** 신규레벨보호 ***/
		if (_calcType == PC_PC) {
			if (_targetPc.getLevel() < Config.AUTO_REMOVELEVEL || _pc.getLevel() < Config.AUTO_REMOVELEVEL) {
				if (skillId != EXTRA_HEAL && skillId != HEAL && skillId != GREATER_HEAL && skillId != HEAL_ALL && skillId != FULL_HEAL
						&& skillId != NATURES_BLESSING) { // 버프계
					_pc.sendPackets(new S_SystemMessage("\\aG[!] 신규보호로 상대방의 마법을 보호받고 있습니다"));
					_targetPc.sendPackets(new S_SystemMessage("\\aG[!] 신규보호로 상대방의 마법을 보호받고 있습니다"));
					return false;
				}
			}
		}
		/*** 신규레벨보호 ***/

		/** 신규혈맹 공격안되게 **/
		if (_calcType == PC_PC) {
			  boolean isAliveBoss = BossAlive.getInstance().isBossAlive(_targetPc.getMapId());
			if ((_pc.getClanid() == Config.신규혈맹클랜 || _targetPc.getClanid() == Config.신규혈맹클랜 )&& !isAliveBoss ) {
				if (skillId != EXTRA_HEAL && skillId != HEAL && skillId != GREATER_HEAL && skillId != HEAL_ALL && skillId != FULL_HEAL
						&& skillId != NATURES_BLESSING) { // 버프계
					_pc.sendPackets(new S_SystemMessage("\\aG[!] 신규혈맹보호로 상대방의 마법을 보호받고 있습니다"));
					_targetPc.sendPackets(new S_SystemMessage("\\aG[!] 신규혈맹보호로 상대방의 마법을 보호받고 있습니다"));
					return false;
				}
			}
		}
		/** 신규혈맹 공격안되게 **/

		if (_calcType == PC_NPC && (_targetNpc.getNpcId() == 5042)) {
			if (skillId == TAMING_MONSTER)
				return false;
		}
		// if (_calcType == PC_NPC && (_targetNpc.getNpcId() == 45684 || _targetNpc.getNpcId() == 45683 || _targetNpc.getNpcId() == 45682
		// || _targetNpc.getNpcId() == 45681 || _targetNpc.getNpcId() == 81163
		// || _targetNpc.getNpcId() == 81047 || _targetNpc.getNpcId() == 45653)) {
		// if (skillId == DARKNESS)
		// return false;
		// }

		// 아스바인드중은 WB, 왈가닥 세레이션 이외 무효
		if (_calcType == PC_PC || _calcType == NPC_PC) {
			if (_calcType == PC_PC) { // 스턴중에 스턴실패
//				if (_targetPc.hasSkillEffect(SHOCK_STUN) || _targetPc.hasSkillEffect(BONE_BREAK)) {
//					if (skillId == SHOCK_STUN || skillId == BONE_BREAK) {
//						return false;
//					}
//				}
			}
			if (_targetPc.hasSkillEffect(EARTH_BIND)) {
				if (skillId != WEAPON_BREAK && skillId != CANCELLATION // 확률계
						&& skillId != EXTRA_HEAL && skillId != HEAL && skillId != GREATER_HEAL && skillId != HEAL_ALL && skillId != FULL_HEAL
						&& skillId != NATURES_BLESSING && skillId == MANA_DRAIN || skillId == CURSE_PARALYZE || skillId == THUNDER_GRAB
						|| skillId == ERASE_MAGIC || skillId == SHOCK_STUN || skillId == EARTH_BIND || skillId == BONE_BREAK) { // 버프계
					return false;
				}
			}
		} else {
			if (_targetNpc.hasSkillEffect(EARTH_BIND)) {
				if (skillId != WEAPON_BREAK && skillId != CANCELLATION) {
					return false;
				}
			}
		}

		if (_calcType == PC_NPC || _calcType == NPC_NPC) {
			if ((skillId == SILENCE || skillId == AREA_OF_SILENCE)
					&& (_targetNpc.getNpcId() == 45684 || _targetNpc.getNpcId() == 45683 || _targetNpc.getNpcId() == 45681
							|| _targetNpc.getNpcId() == 45682 || _targetNpc.getNpcId() == 900011 || _targetNpc.getNpcId() == 900012
							|| _targetNpc.getNpcId() == 900013 || _targetNpc.getNpcId() == 900038 || _targetNpc.getNpcId() == 900039
							|| _targetNpc.getNpcId() == 900040 || _targetNpc.getNpcId() == 5096 || _targetNpc.getNpcId() == 5097
							|| _targetNpc.getNpcId() == 5098 || _targetNpc.getNpcId() == 5099 || _targetNpc.getNpcId() == 5100)) {
				return false;
			}
		}

		// 100% 확률을 가지는 스킬
		if (skillId == MIND_BREAK || skillId == IllUSION_AVATAR) {
			return true;
		}
		probability = calcProbability(skillId);
		int rnd = 0;

		switch (skillId) {
		case DECAY_POTION:
		case SILENCE:
		case CURSE_PARALYZE:
		case CANCELLATION:
		case SLOW:
			// case DARKNESS:
		case WEAKNESS:
		case CURSE_POISON:
		case CURSE_BLIND:
		case WEAPON_BREAK:
		case MANA_DRAIN:
			if (_calcType == PC_PC) {
				if(_targetPc != null){
					if (_targetPc instanceof L1RobotInstance) {
						if (_targetPc.hasSkillEffect(
								L1SkillId.ERASE_MAGIC)) {
							probability = 100;
							rnd = 100;
						} else {
							probability = 20;
							rnd = _random.nextInt(_targetPc.getResistance()
									.getEffectedMrBySkill() + 1) + 1;
						}
					}else{
					rnd = _random.nextInt(_targetPc.getResistance().getEffectedMrBySkill()) + 1;
					}
				}else{
					rnd = 1;
				}
			} else if (_calcType == PC_NPC) {
				if (_targetNpc.getResistance().getEffectedMrBySkill() < 1)
					rnd = 1;
				else
					rnd = _random.nextInt(_targetNpc.getResistance().getEffectedMrBySkill()) + 1;
			} else {
				rnd = _random.nextInt(100) + 1;
			}
			break;
		default:
			rnd = _random.nextInt(100) + 1;
			if (probability > 90)
				probability = 90;
			break;
		}

		if (probability + getMagicHitupByArmor() >= rnd) { // 마법실패시에도 미쓰 뜨게
			isSuccess = true;
		} else {
			if (_calcType == NPC_PC || _calcType == PC_PC) {
				_targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 13418));
				Broadcaster.broadcastPacket(_targetPc, new S_SkillSound(_targetPc.getId(), 13418));
				isSuccess = false;
			} else if (_calcType == PC_NPC) {
				_pc.sendPackets(new S_SkillSound(_targetNpc.getId(), 13418));
				Broadcaster.broadcastPacket(_pc, new S_SkillSound(_targetNpc.getId(), 13418));// 이거는 다른 사람도 보게...
				isSuccess = false;
			}
		}
		if (!isSuccess & skillId == TURN_UNDEAD) {
			if (_calcType == PC_NPC) {
				int ran = _random.nextInt(100) + 1;
				if (ran <= 50) {
					Broadcaster.broadcastPacket(_targetNpc, new S_SkillSound(_targetNpc.getId(), 8987));
					Broadcaster.broadcastPacket(_targetNpc, new S_SkillHaste(_targetNpc.getId(), 1, 0));
					_targetNpc.setMoveSpeed(1);
				}
			}
		}

		if (!Config.ALT_ATKMSG) {
			return isSuccess;
		}
		if (_targetPc == null && _targetNpc == null)
			return isSuccess;

		String msg2 = "확률:" + probability + "%";
		String msg3 = "";
		if (isSuccess == true) {
			msg3 = "성공";
		} else {
			msg3 = "실패";
		}

		if (_pc != null && _pc.isGm()) {
			_pc.sendPackets(new S_SystemMessage("\\aL> ["+ (_targetPc == null ? _targetNpc.getName() : _targetPc.getName()) + "][= \\aJ" + msg2 + " \\aL=][" + msg3 + "]"));
			}
		if (_targetPc != null && _targetPc.isGm()) {
			_targetPc.sendPackets(new S_SystemMessage("\\aL < ["+ (_pc == null ? _npc.getName() : _pc.getName()) + "][= \\aG"+ msg2 + " \\aL=][" + msg3 + "]"));
			}

		return isSuccess;
	}

	private boolean checkZone(int skillId) {
		if (_pc != null && _targetPc != null) {
			if (_pc.getZoneType() == 1 || _targetPc.getZoneType() == 1) {
				if (skillId == CURSE_POISON || skillId == CURSE_BLIND || skillId == WEAPON_BREAK || skillId == SLOW || skillId == CURSE_PARALYZE
						|| skillId == MANA_DRAIN || skillId == DARKNESS || skillId == WEAKNESS || skillId == DISEASE || skillId == SILENCE
						|| skillId == FOG_OF_SLEEPING || skillId == DECAY_POTION || skillId == GREATER_SLOW || skillId == SHOCK_STUN
						|| skillId == ENTANGLE || skillId == ERASE_MAGIC || skillId == EARTH_BIND || skillId == AREA_OF_SILENCE
						|| skillId == WIND_SHACKLE || skillId == POLLUTE_WATER || skillId == STRIKER_GALE || skillId == GUARD_BREAK || skillId == FEAR
						|| skillId == HORROR_OF_DEATH || skillId == ICE_LANCE || skillId == ELEMENTAL_FALL_DOWN || skillId == RETURN_TO_NATURE
						|| skillId == PHANTASM || skillId == CONFUSION || skillId == DESPERADO || skillId == POWERRIP) {
					return false;
				}
			}
		}
		return true;
	}

	private int calcProbability(int skillId) {
		L1Skills l1skills = SkillsTable.getInstance().getTemplate(skillId);
		int attackLevel = 0;
		int defenseLevel = 0;
		int probability = 0;
		int attackInt = 0;
		int defenseMr = 0;

		if (_calcType == PC_PC || _calcType == PC_NPC) {
			attackLevel = _pc.getLevel();
			attackInt = _pc.getAbility().getTotalInt();
		} else {
			attackLevel = _npc.getLevel();
			attackInt = _npc.getAbility().getTotalInt();
		}

		if (_calcType == PC_PC || _calcType == NPC_PC) {
			defenseLevel = _targetPc.getLevel();
			defenseMr = _targetPc.getResistance().getEffectedMrBySkill();
		} else {
			defenseLevel = _targetNpc.getLevel();
			defenseMr = _targetNpc.getResistance().getEffectedMrBySkill();
			if (skillId == RETURN_TO_NATURE) {
				if (_targetNpc instanceof L1SummonInstance) {
					L1SummonInstance summon = (L1SummonInstance) _targetNpc;
					defenseLevel = summon.getMaster().getLevel();
				}
			}
		}
		switch (skillId) {

		case ERASE_MAGIC:
		case ELEMENTAL_FALL_DOWN: {
			/** 이레이즈매직 엘리멘탈폴다운 **/
			/** 동레벨일경우 40% 레벨 아래당 2% 성공확률 상향 레벨 높을때 2% 성공확률 감소 **/
			if (attackLevel >= defenseLevel)
				probability = (attackLevel - defenseLevel) * 2 + Config.이레이즈매직;
			else if (attackLevel < defenseLevel) {
				probability = (attackLevel - defenseLevel) * 3 + Config.이레이즈매직;
			}
			if (probability > 70) {
				probability = 70;
			}
		}
			break;
		case EARTH_BIND:
		case STRIKER_GALE: {
			/** 어스바인드 스트라이커게일 **/
			/** 동레벨일경우 35% 레벨아래당 2% 성공확률 증가, 레벨높을때 3% 성공확률 감소 **/
			if (attackLevel >= defenseLevel)
				probability = (attackLevel - defenseLevel) * 2 + Config.어스바인드;
			else if (attackLevel < defenseLevel) {
				probability = (attackLevel - defenseLevel) * 3 + Config.어스바인드;
			}
			if (probability > 70) {
				probability = 70;
			}
		}
			break;
		case POLLUTE_WATER:
		case WIND_SHACKLE: {
			/** 윈드세클 폴루토워터 **/
			/** 동레벨일경우 30% 레벨아래당 2% 성공확률 증가, 레벨높을때 3% 성공확률 감소 **/
			if (attackLevel >= defenseLevel)
				probability = (attackLevel - defenseLevel) * 2 + Config.윈드세클;
			else if (attackLevel < defenseLevel) {
				probability = (attackLevel - defenseLevel) * 3 + Config.윈드세클;
			}
			if (probability > 70) {
				probability = 70;
			}
		}
			break;
		case DEATH_HEAL:
			probability = Config.데스힐;
			break;
		case SHAPE_CHANGE: // 셰이프 본섭 마방140에게 60% - 마방당 -1%
		case CANCELLATION:// 켄슬 본섭 마방100에게 46% - 마방당 -1%
			if (attackInt > 25)
				attackInt = 25;
			probability = (int) (attackInt * 4 + l1skills.getProbabilityValue() - defenseMr);
			if (_pc != null && _pc.isElf() && (_calcType == PC_PC || _calcType == PC_NPC)) {
				probability -= 30;
			}
			if (probability < 1) {
				probability = 1;
			}
			if (probability > 80) {
				probability = 80;
			}
//			 _pc.sendPackets(new S_SystemMessage("[확률] -> " + defenseMr + " " + probability + "%"));
//			 System.out.println("[마법확률] -> " + defenseMr + " " + probability + "%");
			L1ItemInstance findItem = _targetPc.getInventory().findItemId(11284);
			if(findItem != null){
				probability = 0;
			}
			break;
		case SLOW: // 슬로우 본섭 마방100에게 58% - 마방당 -1%
		case DISEASE: // 디지즈 본섭 마방100에게 68% - 마방당 -1%
		case WEAKNESS: // 위크니스 본섭 마방100에게 56% - 마방당 -1%
		case CURSE_PARALYZE:// 패럴라이즈 마방100에게 15%
		case WEAPON_BREAK:// 웨폰브레이크 마방100에게 30%
		case DECAY_POTION: // 디케이포션 마방100에게 23%
		case ICE_LANCE: // 아이스랜스 마방100에게 30%
		case CURSE_BLIND: // 야매로 15로 설정함
		case CURSE_POISON: // 야매로 50로 설정함
		case SILENCE: // 야매로 35로 설정함
		case DARKNESS: // 야매로 30로 설정함
		case FOG_OF_SLEEPING:// 야매로 25로 설정함
			if (attackInt > 25)
				attackInt = 25;
			probability = (int) (attackInt * 4 + l1skills.getProbabilityValue() - defenseMr);
			if (_pc != null && _pc.isElf() && (_calcType == PC_PC || _calcType == PC_NPC)) {
				probability -= 30;
			}
			if (probability < 1) {
				probability = 1;
			}
			if (probability > 80) {
				probability = 80;
			}
//			 _pc.sendPackets(new S_SystemMessage("[확률] -> " + defenseMr + " " + probability + "%"));
//			 System.out.println("[마법확률] -> " + defenseMr + " " + probability + "%");
			break;
		case THUNDER_GRAB:
			probability = 50;
			if (_calcType == PC_PC || _calcType == PC_NPC) {
				probability += 2 * _pc.getBaseMagicHitUp();
			}
			break;
		case COUNTER_BARRIER: // 테스트
			probability = Config.카운터배리어; // 19
			break;
		case GUARD_BREAK:
			probability = 45;
			break;
		/** 전사스킬 파워그립, 데스페라도 확율 본섭화 **/
		case DESPERADO:
			probability = (int) Config.데스페라도 + ((attackLevel - defenseLevel) * 5);
		    if(probability < 15){
			probability = 15;}
		    if(probability > 80){
			probability = 80;}
			break;
		case POWERRIP:
			probability = (int) Config.파워그립 + ((attackLevel - defenseLevel) * 5);
			if (probability < 15) {
				probability = 15;
			}
			if (probability > 80) {
				probability = 80;
			}
			break;
		case TOMAHAWK: {
			/** 전사스킬 토마호크 : 동레벨일경우 63% 레벨아래당 5% 성공확률 증가, 레벨높을때 3% 성공확률 감소 **/
			if (attackLevel >= defenseLevel)
				probability = (attackLevel - defenseLevel) * 5 + 63;
			else if (attackLevel < defenseLevel) {
				probability = (attackLevel - defenseLevel) * 3 + 63;
			}
			if (probability > 90) {
				probability = 90;
			}
		}
			break;
		case FEAR: // 피어 본섭 22% ~ 42% 베이스 스텟에 의한
			probability = 35;
			if (_calcType == PC_PC || _calcType == PC_NPC) {
				probability += 2 * _pc.getMagicBonus();
			}
			break;
		case HORROR_OF_DEATH: // 인트빨 본섭화
			probability = (int) ((attackInt - 11) * 6);
			break;
		case MORTAL_BODY:
			probability = 25;
			break;
		case CONFUSION:
		case PHANTASM: // 컨퓨젼, 판타즘 본섭 30%
			probability = 38;
			break;
		case RETURN_TO_NATURE:
		case ENTANGLE:
		case AREA_OF_SILENCE:
			probability = (int) (((l1skills.getProbabilityDice()) / 10D) * (attackLevel - defenseLevel)) + l1skills.getProbabilityValue();
			break;
		case BONE_BREAK:
			if (_calcType == PC_PC) {
				if (_pc instanceof L1PcInstance) {
					L1PcInstance pc = (L1PcInstance) _pc;
					L1ItemInstance weapon = _pc.getWeapon();
					int itemId = weapon.getItem().getItemId();
					if (pc.get어택레벨() != 0) {
						attackLevel += pc.get어택레벨();
					}
					if (itemId == 1512) {
						attackLevel += 2;
					}
				}
			}
			probability = (int) Config.본브레이크 + (attackLevel - defenseLevel);
			if (probability < 30) {
				probability = 30;
			}
			if (probability > 50) {
				probability = 50;
			}
			break;
		case SHOCK_STUN:// 쇼크스턴
			/** 데몬인형, 나이트발드인형 스턴레벨 상승에 따른 본브레이크 스턴확률 증가 **/
			if (_calcType == PC_PC) {
				if (_calcType == PC_PC) {
					if (_pc instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) _pc;
						L1ItemInstance weapon = _pc.getWeapon();
						int itemId = weapon.getItem().getItemId();
						if (pc.get어택레벨() != 0) {
							attackLevel += pc.get어택레벨();
						}
						if (itemId == 61) {
							attackLevel += 2;
						}
						if (itemId == 1121) {
							attackLevel += 1;
						}
					}
				}
			}
			/** 데몬인형, 나이트발드인형 스턴레벨 상승에 따른 본브레이크 스턴확률 증가 **/
			probability = (int) Config.SHOCK_STUN + ((attackLevel - defenseLevel) * 2);
			if (_calcType == PC_PC || _calcType == PC_NPC) {
				probability += _pc.getBaseMagicHitUp();
				probability += _pc.getImpactUp();
			}
			if (probability < 10) {
				probability = 10;
			}
			if (probability > 80) {
				probability = 80;
			}
			// System.out.println(" : " + probability);
			break;
		case MANA_DRAIN:
			if (attackInt > 25)
				attackInt = 25;
			probability = (int) ((attackInt - (defenseMr / 5.95)) * l1skills.getProbabilityValue());
			if (_pc != null && _pc.isElf() && (_calcType == PC_PC || _calcType == PC_NPC)) {
				probability -= 33;
			}
			if (probability < 0)
				probability = 0;
			break;
		case FINAL_BURN:
			probability = (int) ((attackLevel) - (defenseMr / 5)); // 파이널 번
			break;
		case TURN_UNDEAD:
			if (attackInt > 25)
				attackInt = 25;
			if (attackLevel > 52)
				attackLevel = 60; // 프리섭화를 위해 52로 변경(기본은 49임)
			probability = (int) ((attackInt * 3 + (attackLevel * 2.5) + _pc.getBaseMagicHitUp()) - (defenseMr + (defenseLevel / 2)) - 80);
			if (_calcType == PC_PC || _calcType == PC_NPC) {
				if (!_pc.isWizard()) {
					probability -= 30;
				}
			}
			break; // 추가 턴언데드 본섭화
		case ARMOR_BRAKE: // 아머 브레이크
			/** 바란카 스턴레벨 상승에 따른 아머브레이크 확률 증가 **/
			if (_calcType == PC_PC) {
				if (_pc instanceof L1PcInstance) {
					if (_calcType == PC_PC) {
						if (_pc instanceof L1PcInstance) {
							L1PcInstance pc = (L1PcInstance) _pc;
							if (pc.get어택레벨() != 0) {
								attackLevel += pc.get어택레벨();
							}
						}
					}
				}
			}
			/** 바란카 스턴레벨 상승에 따른 아머브레이크 확률 증가 **/
			probability = (int) (Config.아머브레이크 + ((attackLevel - defenseLevel) * 3));
			probability += _pc.getImpactUp();
			if (probability < 10) {
				probability = 10;
			}
			if (probability > 80) {
				probability = 80;
			}
			break;
		default: {
			int dice1 = l1skills.getProbabilityDice();
			int diceCount1 = 0;
			if (_calcType == PC_PC || _calcType == PC_NPC) {
				if (_pc.isWizard()) {
					diceCount1 = getMagicBonus() + getMagicLevel() + 1;
				} else if (_pc.isElf()) {
					diceCount1 = getMagicBonus() + getMagicLevel() - 1;
				} else if (_pc.isDragonknight()) {
					diceCount1 = getMagicBonus() + getMagicLevel();
				} else {
					diceCount1 = getMagicBonus() + getMagicLevel() - 1;
				}
			} else {
				diceCount1 = getMagicBonus() + getMagicLevel();
			}
			if (diceCount1 < 1) {
				diceCount1 = 1;
			}
			if (dice1 > 0) {
				for (int i = 0; i < diceCount1; i++) {
					probability += (_random.nextInt(dice1) + 1);
				}
			}

			probability = probability * getLeverage() / 10;
			probability -= getTargetMr();

			if (skillId == TAMING_MONSTER) {
				double probabilityRevision = 1;
				if ((_targetNpc.getMaxHp() * 1 / 4) > _targetNpc.getCurrentHp()) {
					probabilityRevision = 1.3;
				} else if ((_targetNpc.getMaxHp() * 2 / 4) > _targetNpc.getCurrentHp()) {
					probabilityRevision = 1.2;
				} else if ((_targetNpc.getMaxHp() * 3 / 4) > _targetNpc.getCurrentHp()) {
					probabilityRevision = 1.1;
				}
				probability *= probabilityRevision;
			}
		}
			break;
		}

		switch (skillId) {
		case DESPERADO:
			if (_calcType == PC_PC || _calcType == NPC_PC) {
				probability -= _targetPc.getResistance().getDESPERADO();
			}
			break;
		case EARTH_BIND:
			if (_calcType == PC_PC || _calcType == NPC_PC) {
				probability -= _targetPc.getResistance().getHold();
			}
			break;
		case SHOCK_STUN:
		case BONE_BREAK:
		case 30081:
			if (_calcType == PC_PC || _calcType == NPC_PC) {
				probability -= _targetPc.getResistance().getStun() / 2;
			}
			break;
		case CURSE_PARALYZE:
			if (_calcType == PC_PC || _calcType == NPC_PC) {
				probability -= _targetPc.getResistance().getPetrifaction();
			}
			break;
		case FOG_OF_SLEEPING:
			if (_calcType == PC_PC || _calcType == NPC_PC) {
				probability -= _targetPc.getResistance().getSleep();
			}
			break;
		case ICE_LANCE:
			if (_calcType == PC_PC || _calcType == NPC_PC) {
				probability -= _targetPc.getResistance().getFreeze();
			}
			break;
		case CURSE_BLIND:
		case DARKNESS:
		case DARK_BLIND:
			if (_calcType == PC_PC || _calcType == NPC_PC) {
				probability -= _targetPc.getResistance().getHold();
			}
			break;
		default:
			break;
		}
		return probability;
	}

	public int calcMagicDamage(int skillId) {
		int damage = 0;
		if (_calcType == PC_PC || _calcType == NPC_PC) {
			damage = calcPcMagicDamage(skillId);
		} else if (_calcType == PC_NPC || _calcType == NPC_NPC) {
			damage = calcNpcMagicDamage(skillId);
		}

		if (skillId != CONFUSION && skillId != MIND_BREAK && skillId != MAGMA_BREATH) {
			damage = calcMrDefense(damage);
		}

		if (_calcType == PC_PC || _calcType == NPC_PC) {

			if (skillId == ENERGY_BOLT || skillId == CALL_LIGHTNING || skillId == DISINTEGRATE) {
				for (L1ItemInstance armor : _targetPc.getEquipSlot().getArmors()) {
					// 붉은 기사의 방패
					if (armor.getItemId() == 20230) {
						int probability = 1;

						if (armor.getEnchantLevel() >= 10) {
							probability = 5;
						} else if (armor.getEnchantLevel() < 6) {
							probability = 1;
						} else {
							probability = armor.getEnchantLevel() - 5;
						}

						if (_random.nextInt(100) < probability) {
							damage *= 0.8D;
						}

						break;
					}
				}
			}
			if (damage > _targetPc.getCurrentHp()) {
				if (_targetPc.isElf() && _targetPc.hasSkillEffect(L1SkillId.SOUL_BARRIER)) {
					if (damage > _targetPc.getCurrentHp() + _targetPc.getCurrentMp()) {
						damage = _targetPc.getCurrentHp() + _targetPc.getCurrentMp();
					}
				} else {
					damage = _targetPc.getCurrentHp();
				}
			}
			// 전사스킬 : 타이탄 매직
			// HP가 40% 미만일때 마법공격을 확률적으로 반사.
			if (SkillsTable.getInstance().spellCheck(_targetPc.getId(), 240)) {
				int percent = (int) Math.round(((double) _targetPc.getCurrentHp() / (double) _targetPc.getMaxHp()) * 100);
				int chance = _random.nextInt(100) + 1;
				if (!_targetPc.isstop() && (percent + _targetPc.getRisingUp())<= 40 && chance <= 30) {
					if (_targetPc.getInventory().checkItem(41246, 10)) {
						if (_calcType == PC_PC)
							_pc.receiveCounterBarrierDamage(_targetPc, 타이탄대미지());
						else if (_calcType == PC_NPC)
							_npc.receiveCounterBarrierDamage(_targetPc, 타이탄대미지());
						damage = 0;
						_targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 12559));
						_targetPc.getInventory().consumeItem(41246, 10);
					} else {
						_targetPc.sendPackets(new S_SystemMessage("타이탄 매직: 촉매제가 부족합니다."));
					}
				}
				return (int) damage;
			}

		} else {
			if (damage > _targetNpc.getCurrentHp()) {
				damage = _targetNpc.getCurrentHp();
			}
		}

		/** 로봇시스템 **/
		if (_calcType == PC_PC) {
			if (_targetPc.getRobotAi() != null && (_targetPc.noPlayerCK || _targetPc.noPlayerck2 || _targetPc.isGm())) {
				if (_targetPc != null && _targetPc.getClanid() != 0 && !_targetPc.getMap().isSafetyZone(_targetPc.getLocation())) {
					_targetPc.getRobotAi().getAttackList().add(_pc, 0);
				} else if (!_targetPc.getMap().isSafetyZone(_targetPc.getLocation())) {
					if (_targetPc.getMap().isTeleportable()) {
						L1Location newLocation = _targetPc.getLocation().randomLocation(200, true);
						int newX = newLocation.getX();
						int newY = newLocation.getY();
						short mapId = (short) newLocation.getMapId();
						new L1Teleport().teleport(_targetPc, newX, newY, mapId, _targetPc.getHeading(), true);
					}
				}
			}
		}
		/** 로봇시스템 **/

		return damage;
	}

	public int calcPcFireWallDamage() {
		int dmg = 0;

		L1Skills l1skills = SkillsTable.getInstance().getTemplate(FIRE_WALL);
		dmg = calcAttrDefence(l1skills.getDamageValue(), L1Skills.ATTR_FIRE);

		if (_targetPc.hasSkillEffect(ABSOLUTE_BARRIER)) {
			dmg = 0;
		}
		if (_targetPc.hasSkillEffect(ICE_LANCE)) {
			dmg = 0;
		}
		if (_targetPc.hasSkillEffect(EARTH_BIND)) {
			dmg = 0;
		}
		if (_targetPc.hasSkillEffect(MOB_BASILL)) { // 바실얼리기대미지0
			dmg = 0;
		}
		if (_targetPc.hasSkillEffect(MOB_COCA)) { // 코카얼리기대미지0
			dmg = 0;
		}
		if (dmg < 0) {
			dmg = 0;
		}

		return dmg;
	}

	public int calcNpcFireWallDamage() {
		int dmg = 0;

		L1Skills l1skills = SkillsTable.getInstance().getTemplate(FIRE_WALL);
		dmg = calcAttrDefence(l1skills.getDamageValue(), L1Skills.ATTR_FIRE);

		if (_targetNpc.hasSkillEffect(ICE_LANCE)) {
			dmg = 0;
		}
		if (_targetNpc.hasSkillEffect(EARTH_BIND)) {
			dmg = 0;
		}
		if (_targetNpc.hasSkillEffect(MOB_BASILL)) { // 바실얼리기대미지0
			dmg = 0;
		}
		if (_targetNpc.hasSkillEffect(MOB_COCA)) { // 코카얼리기대미지0
			dmg = 0;
		}
		if (dmg < 0) {
			dmg = 0;
		}

		return dmg;
	}

	private int calcPcMagicDamage(int skillId) {
		int dmg = 0;

		if (skillId == FINAL_BURN) {
			if (_calcType == PC_PC || _calcType == PC_NPC) {
				dmg = _pc.getCurrentMp() / 2;
			} else {
				dmg = _npc.getCurrentMp() / 2;
			}
		} else {
			dmg = calcMagicDiceDamage(skillId);
			dmg = (dmg * getLeverage()) / 10;
		}

		dmg -= _targetPc.getDamageReductionByArmor();


		for (L1DollInstance doll : _targetPc.getDollList()) {// 마법인형에 의한 추가 방어
			dmg -= doll.getDamageReductionByDoll();
			// dmg -= dmg * doll.getDamageReductionRatioByDoll();
		}

		if (_targetPc.hasSkillEffect(COOKING_1_0_S) || _targetPc.hasSkillEffect(COOKING_1_1_S) || _targetPc.hasSkillEffect(COOKING_1_2_S)
				|| _targetPc.hasSkillEffect(COOKING_1_3_S) || _targetPc.hasSkillEffect(COOKING_1_4_S) || _targetPc.hasSkillEffect(COOKING_1_5_S)
				|| _targetPc.hasSkillEffect(COOKING_1_6_S) || _targetPc.hasSkillEffect(COOKING_1_8_S) || _targetPc.hasSkillEffect(COOKING_1_9_S)
				|| _targetPc.hasSkillEffect(COOKING_1_10_S) || _targetPc.hasSkillEffect(COOKING_1_11_S) || _targetPc.hasSkillEffect(COOKING_1_12_S)
				|| _targetPc.hasSkillEffect(COOKING_1_13_S) || _targetPc.hasSkillEffect(COOKING_1_14_S) || _targetPc.hasSkillEffect(COOKING_1_16_S)
				|| _targetPc.hasSkillEffect(COOKING_1_17_S) || _targetPc.hasSkillEffect(COOKING_1_18_S) || _targetPc.hasSkillEffect(COOKING_1_19_S)
				|| _targetPc.hasSkillEffect(COOKING_1_20_S) || _targetPc.hasSkillEffect(COOKING_1_21_S) || _targetPc.hasSkillEffect(COOKING_1_22_S)) {
			dmg -= 4;
		}
		if (_targetPc.hasSkillEffect(COOKING_1_7_S) || _targetPc.hasSkillEffect(COOKING_1_15_S) || _targetPc.hasSkillEffect(COOKING_1_20_S)) {
			dmg -= 4;
		}

		if (_targetPc.hasSkillEffect(REDUCTION_ARMOR)) {
			int targetPcLvl = _targetPc.getLevel();
			if (targetPcLvl < 50) {
				targetPcLvl = 50;
			}
			dmg -= (targetPcLvl - 50) / 5 + 1;
		}

		if (_targetPc.hasSkillEffect(EARTH_GUARDIAN)) {
			dmg -= 2;
		}

		if (_calcType == NPC_PC) {
			boolean isNowWar = false;
			int castleId = L1CastleLocation.getCastleIdByArea(_targetPc);
			if (castleId > 0) {
				isNowWar = WarTimeController.getInstance().isNowWar(castleId);
			}
			if (!isNowWar) {
				if (_npc instanceof L1PetInstance) {
					dmg /= 8;
				}
				if (_npc instanceof L1SummonInstance) {
					L1SummonInstance summon = (L1SummonInstance) _npc;
					if (summon.isExsistMaster()) {
						dmg /= 8;
					}
				}
			}
		}	
		/** 마법사 일경우 대미지외부화 적용 */
		if (_calcType == PC_PC) {
			if (_pc.getType() == 3) {
				dmg *= Config.마법사마법대미지;
			}
		}
		/** 마법사 일경우 대미지외부화 적용 */
		if (_calcType == PC_NPC) {
			if (_pc.getType() == 3) {
				dmg *= Config.마법사몬스터대미지;
			}
		}
		/** 마법사 일경우 추가대미지 적용 */
		if (_targetPc.hasSkillEffect(IllUSION_AVATAR)) {
			dmg += dmg / 3;
		}
		if (_targetPc.hasSkillEffect(PATIENCE)) {
			dmg -= 2;
		}
		if (_targetPc.hasSkillEffect(DRAGON_SKIN)) {
			dmg -= 5;
		}
		if (_targetPc.hasSkillEffect(IMMUNE_TO_HARM)) {
			dmg /= 1.3;//1.4
		}
		if (_targetPc.hasSkillEffect(COUNTER_MIRROR)) {
			if (_calcType == PC_PC) {
				if (_targetPc.getAbility().getTotalWis() >= _random.nextInt(70)) {// 원본 100
					_pc.sendPackets(new S_DoActionGFX(_pc.getId(), ActionCodes.ACTION_Damage));
					_pc.broadcastPacket(new S_DoActionGFX(_pc.getId(), ActionCodes.ACTION_Damage));
					_targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 4395));
					_targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 4395));
					_pc.receiveDamage(_targetPc, dmg);
					dmg = 0;
					_targetPc.killSkillEffectTimer(COUNTER_MIRROR);
				}
			} else if (_calcType == NPC_PC) {
				int npcId = _npc.getNpcTemplate().get_npcId();
				if (npcId == 45681 || npcId == 45682 || npcId == 45683 || npcId == 45684) {
				} else if (!_npc.getNpcTemplate().get_IsErase()) {
				} else {
					if (_targetPc.getAbility().getTotalWis() >= _random.nextInt(100)) {
						_npc.broadcastPacket(new S_DoActionGFX(_npc.getId(), ActionCodes.ACTION_Damage));
						_targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 4395));
						_targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 4395));
						_npc.receiveDamage(_targetPc, dmg);
						dmg = 0;
						_targetPc.killSkillEffectTimer(COUNTER_MIRROR);
					}
				}
			}
		}
		if (_targetPc.hasSkillEffect(FEATHER_BUFF_A)) {
			dmg -= 3;
		}
		if (_targetPc.hasSkillEffect(FEATHER_BUFF_B)) {
			dmg -= 2;
		}

		try {
			if (_targetPc.isCrown()) {
				dmg -= CharacterReduc.getInstance().getCharacterReduc(0);
			} else if (_targetPc.isKnight()) {
				dmg -= CharacterReduc.getInstance().getCharacterReduc(1);
			} else if (_targetPc.isElf()) {
				dmg -= CharacterReduc.getInstance().getCharacterReduc(2);
			} else if (_targetPc.isWizard()) {
				dmg -= CharacterReduc.getInstance().getCharacterReduc(3);
			} else if (_targetPc.isDarkelf()) {
				dmg -= CharacterReduc.getInstance().getCharacterReduc(4);
			} else if (_targetPc.isBlackwizard()) {
				dmg -= CharacterReduc.getInstance().getCharacterReduc(5);
			} else if (_targetPc.isDragonknight()) {
				dmg -= CharacterReduc.getInstance().getCharacterReduc(6);
			}
		} catch (Exception e) {
			System.out.println("Character Add Reduction Error");
		}

		/*** 신규레벨보호 ***/
		if (_calcType == PC_PC) {
			int castle_id = L1CastleLocation.getCastleIdByArea(_pc);
			if (castle_id == 0) {
				if (_targetPc.getLevel() < Config.AUTO_REMOVELEVEL || _pc.getLevel() < Config.AUTO_REMOVELEVEL) {
					dmg /= 2;
					_pc.sendPackets(new S_SystemMessage("신규 레벨은 대미지의 50%만 가해집니다."));
					_targetPc.sendPackets(new S_SystemMessage("신규 레벨은 대미지를 50%만 받습니다."));
				}
			}
		}
		/*** 신규레벨보호 ***/

		/** 신규혈맹 공격안되게 **/
		if (_calcType == PC_PC) {
			 boolean isAliveBoss = BossAlive.getInstance().isBossAlive(_targetPc.getMapId());
			int castle_id = L1CastleLocation.getCastleIdByArea(_pc);
			if (castle_id == 0 && !isAliveBoss) {
				if (_pc.getClanid() == Config.신규혈맹클랜 || _targetPc.getClanid() == Config.신규혈맹클랜) {
					if (Config.신규혈맹보호처리) {
						dmg = 0;
						_pc.sendPackets(new S_SystemMessage("신규보호 혈맹은 상호간에 공격이 되지 않습니다"));
						_targetPc.sendPackets(new S_SystemMessage("신규보호 혈맹은 상호간에 공격이 되지 않습니다"));
					} else {
						dmg /= 2;
						_pc.sendPackets(new S_SystemMessage("신규보호혈맹은 대미지를 50%만 가해집니다."));
						_targetPc.sendPackets(new S_SystemMessage("신규보호혈맹은 대미지를 50%만 받습니다."));
					}
				}
			}
		}
		/** 신규혈맹 공격안되게 **/

		/** 배틀존 **/
		if (_calcType == PC_PC) {
			if (_pc.getMapId() == 5153) {
				if (_pc.get_DuelLine() == _targetPc.get_DuelLine() || _pc.get_DuelLine() == 0) {
					dmg = 0;
				}
			}
		}

		if (dmg < 0) {
			dmg = 0;
		}

		return dmg;
	}

	/** 플레이어·NPC 로부터 NPC 에의 대미지 산출 **/
	private int calcNpcMagicDamage(int skillId) {
		int dmg = 0;
		if (skillId == FINAL_BURN) {
			if (_calcType == PC_PC || _calcType == PC_NPC) {
				dmg = _pc.getCurrentMp();
			} else {
				dmg = _npc.getCurrentMp();
			}
		} else {
			dmg = calcMagicDiceDamage(skillId);
			dmg = (dmg * getLeverage()) / 10;
		}

		if (_calcType == PC_NPC) {
			boolean isNowWar = false;
			int castleId = L1CastleLocation.getCastleIdByArea(_targetNpc);
			if (castleId > 0) {
				isNowWar = WarTimeController.getInstance().isNowWar(castleId);
			}
			if (!isNowWar) {
				if (_targetNpc instanceof L1PetInstance) {
					dmg /= 8;
				}
				if (_targetNpc instanceof L1SummonInstance) {
					L1SummonInstance summon = (L1SummonInstance) _targetNpc;
					if (summon.isExsistMaster()) {
						dmg /= 8;
					}
				}
			}
		}

		if (_calcType == PC_NPC && _targetNpc != null) {
			int npcId = _targetNpc.getNpcTemplate().get_npcId();
			if (npcId >= 45912 && npcId <= 45915 && !_pc.hasSkillEffect(STATUS_HOLY_WATER)) {
				dmg = 0;
			}
			if (npcId == 45916 && !_pc.hasSkillEffect(STATUS_HOLY_MITHRIL_POWDER)) {
				dmg = 0;
			}
			if (npcId == 45941 && !_pc.hasSkillEffect(STATUS_HOLY_WATER_OF_EVA)) {
				dmg = 0;
			}
			if (npcId == 45752 && !_pc.hasSkillEffect(STATUS_CURSE_BARLOG)) {
				dmg = 0;
			}
			if (npcId == 45753 && !_pc.hasSkillEffect(STATUS_CURSE_BARLOG)) {
				dmg = 0;
			}
			if (npcId == 45675 && !_pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
				dmg = 0;
			}
			if (npcId == 81082 && !_pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
				dmg = 0;
			}
			if (npcId == 45625 && !_pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
				dmg = 0;
			}
			if (npcId == 45674 && !_pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
				dmg = 0;
			}
			if (npcId == 45685 && !_pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
				dmg = 0;
			}
			if (npcId >= 46068 && npcId <= 46091 && _pc.getTempCharGfx() == 6035) {
				dmg = 0;
			}
			if (npcId >= 46092 && npcId <= 46106 && _pc.getTempCharGfx() == 6034) {
				dmg = 0;
			}
			if (_targetNpc.getNpcTemplate().get_gfxid() == 7684 && !_pc.hasSkillEffect(PAP_FIVEPEARLBUFF)) {
				dmg = 0;
			}
			if (_targetNpc.getNpcTemplate().get_gfxid() == 7805 && !_pc.hasSkillEffect(PAP_MAGICALPEARLBUFF)) {
				dmg = 0;
			}
			if ((_targetNpc.getNpcTemplate().get_gfxid() == 7864 || _targetNpc.getNpcTemplate().get_gfxid() == 7869
					|| _targetNpc.getNpcTemplate().get_gfxid() == 7870)) {
				dmg *= 1.5;
			} // 파푸리온 혈흔1.5뎀
		}

		return dmg;
	}

	private int calcMagicDiceDamage(int skillId) {
		L1Skills l1skills = SkillsTable.getInstance().getTemplate(skillId);
		int dice = l1skills.getDamageDice();
		int diceCount = l1skills.getDamageDiceCount();
		int value = l1skills.getDamageValue();
		int magicDamage = 0;
		double PowerMr = 0; // 마방

		Random random = new Random();

		dice += getSpellPower() / 2;

		for (int i = 0; i < diceCount; i++) {
			magicDamage += (_random.nextInt(dice) + 1);
		}

		magicDamage += value * (1 + getSpellPower() / 10);

		/** 치명타 발생 부분 */
		double criticalCoefficient = 1.4;
		int rnd = random.nextInt(100) + 1;

		if (_calcType == PC_PC || _calcType == PC_NPC) {
			int propCritical = CalcStat.calcMagicCritical(_pc.ability.getTotalInt())+_pc.getMagicCritical();
			switch (skillId) {
			// 6레벨 이하 광역마법 제외한 공격마법
			case ENERGY_BOLT:
			case ICE_DAGGER:
			case WIND_CUTTER:
			case CHILL_TOUCH:
			case SMASH:
			case FIRE_ARROW:
			case STALAC:
			case VAMPIRIC_TOUCH:
			case CONE_OF_COLD:
			case CALL_LIGHTNING:
			case DISINTEGRATE:
				propCritical = +10;
				break;
			}
			// 마안 일정확률로 마법치명타+1
			if (_pc.hasSkillEffect(LIND_MAAN) || _pc.hasSkillEffect(SHAPE_MAAN) || _pc.hasSkillEffect(LIFE_MAAN)) {
				propCritical += 1;
			}
			if (criticalOccur(propCritical)) {
				magicDamage *= 1.5;
			}
		} else if (_calcType == NPC_PC || _calcType == NPC_NPC) {
			if (rnd <= 15) {
				magicDamage *= criticalCoefficient;
			}
		}
		// 디스마법은 라우풀에 따라 데미지 상향처리.
		// : 카오틱수치가 높을수록 데미지 하향
		if (skillId == DISINTEGRATE) {
			int lawful = getLawful();
			if (lawful <= 0)
				lawful = 1;
			magicDamage += magicDamage * (lawful / 32767);
		}
		//
		if (getTargetMr() < 101) {
			PowerMr = getTargetMr() / (double) 200; // 마방100되면 10당 (기본대미지*마법상수)의 5% 대미지
			// 줄어들게 설정 총50%
		} else {
			PowerMr = 0.5 + (getTargetMr() - 100) / (double) 400; // 마방100초과분에 대해 10당
			// (기본대미지*마법상수)의 1% 줄어들게 설정 100당 10%
		} // 마방 400되면 마법대미지 0
		if (skillId == FINAL_BURN) {
			PowerMr = 0;
		}
		magicDamage -= magicDamage * PowerMr; // 먼저 마방에 의한 대미지 감소부터 처리
		double attrDeffence = calcAttrResistance(l1skills.getAttr());

		// 속성방어 100당 45% 줄어듬.
		// 10당4.5%초과분에대해서
		// 10당 0.9% 줄어들게 설정
		magicDamage -= magicDamage * attrDeffence; // 마방에 의한 대미지 감소후 속성방어에 의한
		// 대미지 감소 처리
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			magicDamage += _pc.getBaseMagicDmg(); // 베이스 스탯 마법 대미지 보너스 추가
		}
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			int weaponAddDmg = 0;
			L1ItemInstance weapon = _pc.getWeapon();
			if (weapon != null) {
				weaponAddDmg = weapon.getItem().getMagicDmgModifier();
			}
			magicDamage += weaponAddDmg; // 무기에 의한 마법 대미지 추가
		}
		return magicDamage;
	}

	public int calcHealing(int skillId) {
		L1Skills l1skills = SkillsTable.getInstance().getTemplate(skillId);
		int dice = l1skills.getDamageDice();
		int value = l1skills.getDamageValue();
		int magicDamage = 0;

		int magicBonus = getMagicBonus();
		if (magicBonus > 10) {
			magicBonus = 10;
		}

		int diceCount = value + magicBonus;
		for (int i = 0; i < diceCount; i++) {
			magicDamage += (_random.nextInt(dice) + 1);
		}

		double alignmentRevision = 1.0;
		if (getLawful() > 0) {
			alignmentRevision += (getLawful() / 32768.0);
		}

		magicDamage *= alignmentRevision;

		magicDamage = (magicDamage * getLeverage()) / 10;

		return magicDamage;
	}

	/**
	 * MR에 의한 마법 대미지 감소를 처리 한다 수정일자 : 2013.02.22 수정자 : 메르키스
	 * 
	 * @param dmg
	 * @return dmg
	 */

	public int calcMrDefense(int dmg) {
		int PInt = 0;
		int mrs = 0;
		int attackPcLvSp = 0;
		int targetPcLvMr = 0;
		int ran1 = 0;
		int mrset = 0;

		if (_calcType == PC_PC || _calcType == PC_NPC) {
			PInt = _pc.getSp() * 2;
		} else if (_calcType == NPC_PC) {
			PInt = _npc.getSp() * 2;
		}
		if (_calcType == PC_PC || _calcType == NPC_PC) {
			mrs = (int) (_targetPc.getMr() * 1.7D - 20);
		} else {
			mrs = (int) (_targetNpc.getMr() * 1.7D - 20);
		}
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			attackPcLvSp = _pc.getLevel();
		} else if (_calcType == NPC_PC) {
			attackPcLvSp = _npc.getLevel();
		}
		if (_calcType == PC_PC || _calcType == NPC_PC) {
			targetPcLvMr = _targetPc.getLevel();
		} else {
			targetPcLvMr = _targetNpc.getLevel();
		}

		Random random = new Random();
		ran1 = random.nextInt(15) + 1;
		mrset = mrs - ran1;

		int PPPP = (int) (attackPcLvSp / 8D + 1);
		int TTTT = (int) (targetPcLvMr / 10D + 1);
		Random random2 = new Random();
		Random random3 = new Random();
		int fail = PInt + PPPP - TTTT;

		if ((mrset - fail) >= 151) {
			dmg *= 0.01D;
		} else if ((mrset - fail) >= 146 && (mrset - fail) <= 150) {
			dmg *= 0.03D;
		} else if ((mrset - fail) >= 141 && (mrset - fail) <= 145) {
			dmg *= 0.07D;
		} else if ((mrset - fail) >= 136 && (mrset - fail) <= 140) {
			dmg *= 0.10D;
		} else if ((mrset - fail) >= 131 && (mrset - fail) <= 135) {
			dmg *= 0.13D;
		} else if ((mrset - fail) >= 126 && (mrset - fail) <= 130) {
			dmg *= 0.17D;
		} else if ((mrset - fail) >= 121 && (mrset - fail) <= 125) {
			dmg *= 0.20D;
		} else if ((mrset - fail) >= 116 && (mrset - fail) <= 120) {
			dmg *= 0.23D;
		} else if ((mrset - fail) >= 111 && (mrset - fail) <= 115) {
			dmg *= 0.27D;
		} else if ((mrset - fail) >= 106 && (mrset - fail) <= 110) {
			dmg *= 0.30D;
		} else if ((mrset - fail) >= 101 && (mrset - fail) <= 105) {
			dmg *= 0.33D;
		} else if ((mrset - fail) >= 96 && (mrset - fail) <= 100) {
			dmg *= 0.37D;
		} else if ((mrset - fail) >= 91 && (mrset - fail) <= 95) {
			dmg *= 0.40D;
		} else if ((mrset - fail) >= 86 && (mrset - fail) <= 90) {
			dmg *= 0.43D;
		} else if ((mrset - fail) >= 81 && (mrset - fail) <= 85) {
			dmg *= 0.47D;
		} else if ((mrset - fail) >= 76 && (mrset - fail) <= 80) {
			dmg *= 0.50D;
		} else if ((mrset - fail) >= 71 && (mrset - fail) <= 75) {
			dmg *= 0.53D;
		} else if ((mrset - fail) >= 66 && (mrset - fail) <= 70) {
			dmg *= 0.57D;
		} else if ((mrset - fail) >= 60 && (mrset - fail) <= 65) {
			dmg *= 0.60D;
		} else if ((mrset - fail) >= 51 && (mrset - fail) <= 56) {
			dmg *= 0.63D;
		} else if ((mrset - fail) >= 46 && (mrset - fail) <= 50) {
			dmg *= 0.67D;
		} else if ((mrset - fail) >= 41 && (mrset - fail) <= 45) {
			dmg *= 0.70D;
		} else if ((mrset - fail) >= 36 && (mrset - fail) <= 40) {
			dmg *= 0.73D;
		} else if ((mrset - fail) >= 31 && (mrset - fail) <= 35) {
			dmg *= 0.77D;
		} else if ((mrset - fail) >= 26 && (mrset - fail) <= 30) {
			dmg *= 0.80D;
		} else if ((mrset - fail) >= 21 && (mrset - fail) <= 25) {
			dmg *= 0.85D;
		} else if ((mrset - fail) >= 16 && (mrset - fail) <= 20) {
			dmg *= 0.90D;
		} else if ((mrset - fail) >= 11 && (mrset - fail) <= 15) {
			dmg *= 0.95D;
		} else if ((mrset - fail) >= 6 && (mrset - fail) <= 10) {
			dmg *= 1.00D;
		} else {
			dmg *= 1.05D;
		}
		return dmg;
	}

	private boolean criticalOccur(int prop) {
		int num = _random.nextInt(100) + 1;

		if (prop == 0) {
			return false;
		}
		if (num <= prop) {
			_CriticalDamage = true;
		}
		return _CriticalDamage;
	}

	private double calcAttrResistance(int attr) {
		int resist = 0;
		int resistFloor = 0;
		if (_calcType == PC_PC || _calcType == NPC_PC) {
			switch (attr) {
			case L1Skills.ATTR_EARTH:
				resist = _targetPc.getResistance().getEarth();
				break;
			case L1Skills.ATTR_FIRE:
				resist = _targetPc.getResistance().getFire();
				break;
			case L1Skills.ATTR_WATER:
				resist = _targetPc.getResistance().getWater();
				break;
			case L1Skills.ATTR_WIND:
				resist = _targetPc.getResistance().getWind();
				break;
			}
		} else if (_calcType == PC_NPC || _calcType == NPC_NPC) {
		}
		if (resist < 0) {
			resistFloor = (int) (-0.45 * Math.abs(resist));
		} else if (resist < 101) {
			resistFloor = (int) (0.45 * Math.abs(resist));
		} else {
			resistFloor = (int) (45 + 0.09 * Math.abs(resist));
			// 속성100초과분에 대해0.45의 1/5정도 감소되게 변경
		}
		double attrDeffence = resistFloor / 100;
		return attrDeffence;
	}

	private int calcAttrDefence(int dmg, int attr) {
		if (dmg < 1) {
			return dmg;
		}

		int resist = 0;

		if (_calcType == PC_PC || _calcType == NPC_PC) {
			switch (attr) {
			case L1Skills.ATTR_EARTH:
				resist = _targetPc.getResistance().getEarth();
				break;
			case L1Skills.ATTR_FIRE:
				resist = _targetPc.getResistance().getFire();
				break;
			case L1Skills.ATTR_WATER:
				resist = _targetPc.getResistance().getWater();
				break;
			case L1Skills.ATTR_WIND:
				resist = _targetPc.getResistance().getWind();
				break;
			}
		} else if (_calcType == PC_NPC || _calcType == NPC_NPC) {
		}

		dmg -= resist / 2;

		if (dmg < 1) {
			dmg = 1;
		}

		return dmg;
	}

	public void commit(int damage, int drainMana) {
		if (_calcType == PC_PC || _calcType == NPC_PC) {
			commitPc(damage, drainMana);
		} else if (_calcType == PC_NPC || _calcType == NPC_NPC) {
			commitNpc(damage, drainMana);
		}

		if (!Config.ALT_ATKMSG) {
			return;
		}
		if (_targetPc == null && _targetNpc == null)
			return;
		if (Config.ALT_ATKMSG) {
			if ((_calcType == PC_PC || _calcType == PC_NPC) && !_pc.isGm()) {
				return;
			}
			if ((_calcType == PC_PC || _calcType == NPC_PC) && !_targetPc.isGm()) {
				return;
			}
		}
		String msg0 = "";
		String msg2 = "";
		String msg3 = "";
		String msg4 = "";

		if (_calcType == PC_PC || _calcType == PC_NPC) {
			msg0 = _pc.getName();
		} else if (_calcType == NPC_PC) {
			msg0 = _npc.getName();
		}

		if (_calcType == NPC_PC || _calcType == PC_PC) {
			msg4 = _targetPc.getName();
			msg2 = "HP:" + _targetPc.getCurrentHp();
		} else if (_calcType == PC_NPC) {
			msg4 = _targetNpc.getName();
			msg2 = "HP:" + _targetNpc.getCurrentHp();
		}

		msg3 = "DMG:" + damage;

		if (_calcType == PC_PC || _calcType == PC_NPC) {
			_pc.sendPackets(new S_SystemMessage("\\fR[" + msg0 + "->" + msg4 + "] " + msg3 + " / " + msg2));
		}
		if (_calcType == NPC_PC || _calcType == PC_PC) {
			_targetPc.sendPackets(new S_SystemMessage("\\fY[" + msg0 + "->" + msg4 + "] " + msg3 + " / " + msg2));
		}
	}

	private void commitPc(int damage, int drainMana) {
		if (_targetPc.hasSkillEffect(ABSOLUTE_BARRIER)) {
			damage = 0;
			drainMana = 0;
		}
		if (_targetPc.hasSkillEffect(ICE_LANCE)) {
			damage = 0;
			drainMana = 0;
		}
		if (_targetPc.hasSkillEffect(EARTH_BIND)) {
			damage = 0;
			drainMana = 0;
		}
		if (_targetPc.hasSkillEffect(MOB_BASILL)) { // 바실얼리기대미지0
			damage = 0;
			drainMana = 0;
		}
		if (_targetPc.hasSkillEffect(MOB_COCA)) { // 코카얼리기대미지0
			damage = 0;
			drainMana = 0;
		}

		if (_calcType == PC_PC) {
			if (drainMana > 0 && _targetPc.getCurrentMp() > 0) {
				if (drainMana > _targetPc.getCurrentMp()) {
					drainMana = _targetPc.getCurrentMp();
				}
				int newMp = _pc.getCurrentMp() + drainMana;
				_pc.setCurrentMp(newMp);
			}
			_targetPc.receiveManaDamage(_pc, drainMana);
			_targetPc.receiveDamage(_pc, damage);
		} else if (_calcType == NPC_PC) {
			_targetPc.receiveDamage(_npc, damage);
		}
	}

	private void commitNpc(int damage, int drainMana) {
		if (_targetNpc.hasSkillEffect(ICE_LANCE)) {
			damage = 0;
			drainMana = 0;
		}
		if (_targetNpc.hasSkillEffect(EARTH_BIND)) {
			damage = 0;
			drainMana = 0;
		}
		if (_targetNpc.hasSkillEffect(MOB_BASILL)) { // 바실얼리기대미지0
			damage = 0;
			drainMana = 0;
		}
		if (_targetNpc.hasSkillEffect(MOB_COCA)) { // 코카얼리기대미지0
			damage = 0;
			drainMana = 0;
		}
		if (_targetNpc.getNpcTemplate().get_gfxid() == 7684 && _pc.hasSkillEffect(PAP_FIVEPEARLBUFF)) {
			damage = 1;
			drainMana = 0;
		}
		if (_targetNpc.getNpcTemplate().get_gfxid() == 7805 && _pc.hasSkillEffect(PAP_MAGICALPEARLBUFF)) {
			damage = 1;
			drainMana = 0;
		}
		if (_targetNpc.getNpcTemplate().get_gfxid() == 7720) {
			damage = 1;
			drainMana = 0;
		}

		if (_calcType == PC_NPC) {
			if (drainMana > 0) {
				int drainValue = _targetNpc.drainMana(drainMana);
				int newMp = _pc.getCurrentMp() + drainValue;
				_pc.setCurrentMp(newMp);
			}
			_targetNpc.ReceiveManaDamage(_pc, drainMana);
			_targetNpc.receiveDamage(_pc, damage);
		} else if (_calcType == NPC_NPC) {
			_targetNpc.receiveDamage(_npc, damage);
		}
	}

	// ●●●● 전사 타이탄 대미지를 산출 ●●●●
	private int 타이탄대미지() {
		double damage = 0;
		L1ItemInstance weapon = null;
		weapon = _targetPc.getWeapon();
		if (weapon != null) {
			damage = Math.round((weapon.getItem().getDmgLarge() + weapon.getEnchantLevel() + weapon.getItem().getDmgModifier()) * 2);
		}
		return (int) damage;
	}
}