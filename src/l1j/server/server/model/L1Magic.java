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

	/* ■■■■■■■■■■■■■■ 成功判定 ■■■■■■■■■■■■■ */
	// ●●●●確率系魔法の成功判定 ●●●●
	// 計算方法
	// 攻撃側のポイント：LV +（（MagicBonus * 3）*魔法固有係数）
	// 防御側のポイント：（（LV / 2）+（MR * 3））/ 2
	// 攻撃成功率：攻撃側のポイント - 防御側のポイント
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
					_targetPc.sendPackets(new S_SystemMessage("血盟員" + _pc.getName() + "様がキャンセレーション魔法を詠唱しました。"));
					return true;
				}
				if (_pc.isInParty()) {
					if (_pc.getParty().isMember(_targetPc)) {
						_targetPc.sendPackets(new S_SystemMessage("パーティーメンバー" + _pc.getName() + "様がキャンセレーション魔法を詠唱しました。"));
						return true;
					}
				}
				// 対象がインビジ状態イルテンキャンセ無効
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

		// 50レップ以上npcに下魔法ない距離。
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

		/*** 新規レベルの保護 ***/
		if (_calcType == PC_PC) {
			if (_targetPc.getLevel() < Config.AUTO_REMOVELEVEL || _pc.getLevel() < Config.AUTO_REMOVELEVEL) {
				if (skillId != EXTRA_HEAL && skillId != HEAL && skillId != GREATER_HEAL && skillId != HEAL_ALL && skillId != FULL_HEAL
						&& skillId != NATURES_BLESSING) { // バフ系
					_pc.sendPackets(new S_SystemMessage("\\aG[!] 新規保護で相手の魔法を保護されています"));
					_targetPc.sendPackets(new S_SystemMessage("\\aG[!] 新規保護で相手の魔法を保護されています"));
					return false;
				}
			}
		}
		/*** 新規レベルの保護 ***/

		/** 新規血盟攻撃途方もなく **/
		if (_calcType == PC_PC) {
			  boolean isAliveBoss = BossAlive.getInstance().isBossAlive(_targetPc.getMapId());
			if ((_pc.getClanid() == Config.NEW_CLAN || _targetPc.getClanid() == Config.NEW_CLAN )&& !isAliveBoss ) {
				if (skillId != EXTRA_HEAL && skillId != HEAL && skillId != GREATER_HEAL && skillId != HEAL_ALL && skillId != FULL_HEAL
						&& skillId != NATURES_BLESSING) { // バフ系
					_pc.sendPackets(new S_SystemMessage("\\aG[!] 新規血盟保護で相手の魔法を保護されています"));
					_targetPc.sendPackets(new S_SystemMessage("\\aG[!] 新規血盟保護で相手の魔法を保護されています"));
					return false;
				}
			}
		}
		/**新規血盟攻撃途方もなく**/

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

		//アスバインド中はWB、キャンセレーション以外無効
		if (_calcType == PC_PC || _calcType == NPC_PC) {
			if (_calcType == PC_PC) { // スターン中スターンに失敗
//				if (_targetPc.hasSkillEffect(SHOCK_STUN) || _targetPc.hasSkillEffect(BONE_BREAK)) {
//					if (skillId == SHOCK_STUN || skillId == BONE_BREAK) {
//						return false;
//					}
//				}
			}
			if (_targetPc.hasSkillEffect(EARTH_BIND)) {
				if (skillId != WEAPON_BREAK && skillId != CANCELLATION // 確率系
						&& skillId != EXTRA_HEAL && skillId != HEAL && skillId != GREATER_HEAL && skillId != HEAL_ALL && skillId != FULL_HEAL
						&& skillId != NATURES_BLESSING && skillId == MANA_DRAIN || skillId == CURSE_PARALYZE || skillId == THUNDER_GRAB
						|| skillId == ERASE_MAGIC || skillId == SHOCK_STUN || skillId == EARTH_BIND || skillId == BONE_BREAK) { //バフ系
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

		// 100％の確率を持つスキル
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

		if (probability + getMagicHitupByArmor() >= rnd) { // 魔法に失敗時にもミス開か
			isSuccess = true;
		} else {
			if (_calcType == NPC_PC || _calcType == PC_PC) {
				_targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 13418));
				Broadcaster.broadcastPacket(_targetPc, new S_SkillSound(_targetPc.getId(), 13418));
				isSuccess = false;
			} else if (_calcType == PC_NPC) {
				_pc.sendPackets(new S_SkillSound(_targetNpc.getId(), 13418));
				Broadcaster.broadcastPacket(_pc, new S_SkillSound(_targetNpc.getId(), 13418));// これは、他の人も見る...
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

		String msg2 = "確率:" + probability + "%";
		String msg3 = "";
		if (isSuccess == true) {
			msg3 = "成功";
		} else {
			msg3 = "失敗";
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
						|| skillId == PHANTASM || skillId == CONFUSION || skillId == DESPERADO || skillId == POWEGRRIP) {
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
			/** イレースマジックエレメンタルフォールダウン **/
			/** 同レベルの場合、40％レベルの下あたり2％の成功確率アップレベル高いとき2％成功確率の減少 **/
			if (attackLevel >= defenseLevel)
				probability = (attackLevel - defenseLevel) * 2 + Config.ERASE_MAGIC;
			else if (attackLevel < defenseLevel) {
				probability = (attackLevel - defenseLevel) * 3 + Config.ERASE_MAGIC;
			}
			if (probability > 70) {
				probability = 70;
			}
		}
			break;
		case EARTH_BIND:
		case STRIKER_GALE: {
			/** アースバインドストライカーゲイル **/
			/** 同レベルの場合、35％レベルの下あたり2％の確率の増加、レベル高いとき3％成功確率の減少 **/
			if (attackLevel >= defenseLevel)
				probability = (attackLevel - defenseLevel) * 2 + Config.EARTH_BIND;
			else if (attackLevel < defenseLevel) {
				probability = (attackLevel - defenseLevel) * 3 + Config.EARTH_BIND;
			}
			if (probability > 70) {
				probability = 70;
			}
		}
			break;
		case POLLUTE_WATER:
		case WIND_SHACKLE: {
			/** ウィンドセクルポールルートウォーター **/
			/** 同レベルの場合、30％レベルの下あたり2％の確率の増加、レベル高いとき3％成功確率の減少 **/
			if (attackLevel >= defenseLevel)
				probability = (attackLevel - defenseLevel) * 2 + Config.WIND_SHACKLE;
			else if (attackLevel < defenseLevel) {
				probability = (attackLevel - defenseLevel) * 3 + Config.WIND_SHACKLE;
			}
			if (probability > 70) {
				probability = 70;
			}
		}
			break;
		case DEATH_HEAL:
			probability = Config.DEATH_HEAL;
			break;
		case SHAPE_CHANGE: // シェイプ本サーバー魔140に60％ - 魔あたり-1％
		case CANCELLATION:// キャンセ本サーバー魔100に46％ - 魔あたり-1％
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
//			 _pc.sendPackets(new S_SystemMessage("[確率] -> " + defenseMr + " " + probability + "%"));
//			 System.out.println("[魔法確率] -> " + defenseMr + " " + probability + "%");
			L1ItemInstance findItem = _targetPc.getInventory().findItemId(11284);
			if(findItem != null){
				probability = 0;
			}
			break;
		case SLOW: // スロー本サーバー魔100に58％ - 魔あたり-1％
		case DISEASE: // ディジーズ本サーバー魔100に68％ - 魔あたり-1％
		case WEAKNESS: // ウィークネス本サーバー魔100に56％ - 魔あたり-1％
		case CURSE_PARALYZE:// パラライズ魔100に15％
		case WEAPON_BREAK:// ウェポンブレイク魔100に30％
		case DECAY_POTION: // ディケイポーション魔100に23％
		case ICE_LANCE: // アイスランス魔100に30％
		case CURSE_BLIND: // ヤメに15に設定する
		case CURSE_POISON: // ヤメに50に設定する
		case SILENCE: // ヤメに35に設定する
		case DARKNESS: // ヤメに30に設定する
		case FOG_OF_SLEEPING:// ヤメに25に設定する
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
//			 _pc.sendPackets(new S_SystemMessage("[確率] -> " + defenseMr + " " + probability + "%"));
//			 System.out.println("[魔法確率] -> " + defenseMr + " " + probability + "%");
			break;
		case THUNDER_GRAB:
			probability = 50;
			if (_calcType == PC_PC || _calcType == PC_NPC) {
				probability += 2 * _pc.getBaseMagicHitUp();
			}
			break;
		case COUNTER_BARRIER: // テスト
			probability = Config.COUNTER_BARRIER; // 19
			break;
		case GUARD_BREAK:
			probability = 45;
			break;
		/** 戦士スキルパワーグリップ、デスペラード確率本サーバー化 **/
		case DESPERADO:
			probability = (int) Config.DESPERADO + ((attackLevel - defenseLevel) * 5);
		    if(probability < 15){
			probability = 15;}
		    if(probability > 80){
			probability = 80;}
			break;
		case POWEGRRIP:
			probability = (int) Config.POWER_GRIP + ((attackLevel - defenseLevel) * 5);
			if (probability < 15) {
				probability = 15;
			}
			if (probability > 80) {
				probability = 80;
			}
			break;
		case TOMAHAWK: {
			/** 戦士スキルトマホーク：同レベルの場合63％のレベルの下あたり5％の確率の増加、レベル高いとき3％成功確率の減少 **/
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
		case FEAR: // ピア本サーバー22％〜42％ベースステータスによる
			probability = 35;
			if (_calcType == PC_PC || _calcType == PC_NPC) {
				probability += 2 * _pc.getMagicBonus();
			}
			break;
		case HORROR_OF_DEATH: // ポイント洗える本サーバー化
			probability = (int) ((attackInt - 11) * 6);
			break;
		case MORTAL_BODY:
			probability = 25;
			break;
		case CONFUSION:
		case PHANTASM: // コンフュージョン、ファンタズム本サーバー30％
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
					if (pc.getAttackLevel() != 0) {
						attackLevel += pc.getAttackLevel();
					}
					if (itemId == 1512) {
						attackLevel += 2;
					}
				}
			}
			probability = (int) Config.BONE_BREAK + (attackLevel - defenseLevel);
			if (probability < 30) {
				probability = 30;
			}
			if (probability > 50) {
				probability = 50;
			}
			break;
		case SHOCK_STUN:// ショックスタン
			/** デーモン人形、ナイトバルド人形スターンレベルの上昇に伴う、本ブレーキスタン確率増加 **/
			if (_calcType == PC_PC) {
				if (_calcType == PC_PC) {
					if (_pc instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) _pc;
						L1ItemInstance weapon = _pc.getWeapon();
						int itemId = weapon.getItem().getItemId();
						if (pc.getAttackLevel() != 0) {
							attackLevel += pc.getAttackLevel();
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
			/** デーモン人形、ナイトバルド人形スターンレベルの上昇に伴う、本ブレーキスタン確率増加 **/
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
			probability = (int) ((attackLevel) - (defenseMr / 5)); // ファイナル回
			break;
		case TURN_UNDEAD:
			if (attackInt > 25)
				attackInt = 25;
			if (attackLevel > 52)
				attackLevel = 60; // プリソプ化のために52に変更（基本は49である）
			probability = (int) ((attackInt * 3 + (attackLevel * 2.5) + _pc.getBaseMagicHitUp()) - (defenseMr + (defenseLevel / 2)) - 80);
			if (_calcType == PC_PC || _calcType == PC_NPC) {
				if (!_pc.isWizard()) {
					probability -= 30;
				}
			}
			break; //追加ターンアンデッド本サーバー化
		case ARMOR_BRAKE: // アーマーブレイク
			/** バランカスターンレベルの上昇に伴うアーマーブレイク確率増加 **/
			if (_calcType == PC_PC) {
				if (_pc instanceof L1PcInstance) {
					if (_calcType == PC_PC) {
						if (_pc instanceof L1PcInstance) {
							L1PcInstance pc = (L1PcInstance) _pc;
							if (pc.getAttackLevel() != 0) {
								attackLevel += pc.getAttackLevel();
							}
						}
					}
				}
			}
			/**バランカスターンレベルの上昇に伴うアーマーブレイク確率増加 **/
			probability = (int) (Config.ARMOR_BREAK + ((attackLevel - defenseLevel) * 3));
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
					// レッド記事の盾
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
			// 戦士スキル：タイタンマジック
			// HPが40％未満の場合、魔法攻撃を確率的に反射。
			if (SkillsTable.getInstance().spellCheck(_targetPc.getId(), 240)) {
				int percent = (int) Math.round(((double) _targetPc.getCurrentHp() / (double) _targetPc.getMaxHp()) * 100);
				int chance = _random.nextInt(100) + 1;
				if (!_targetPc.isstop() && (percent + _targetPc.getRisingUp())<= 40 && chance <= 30) {
					if (_targetPc.getInventory().checkItem(41246, 10)) {
						if (_calcType == PC_PC)
							_pc.receiveCounterBarrierDamage(_targetPc, calcTitanDamage());
						else if (_calcType == PC_NPC)
							_npc.receiveCounterBarrierDamage(_targetPc, calcTitanDamage());
						damage = 0;
						_targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 12559));
						_targetPc.getInventory().consumeItem(41246, 10);
					} else {
						_targetPc.sendPackets(new S_SystemMessage("タイタンマジック：触媒が不足します。"));
					}
				}
				return (int) damage;
			}

		} else {
			if (damage > _targetNpc.getCurrentHp()) {
				damage = _targetNpc.getCurrentHp();
			}
		}

		/** ロボットシステム **/
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
		/** ロボットシステム **/

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
		if (_targetPc.hasSkillEffect(MOB_BASILL)) { // バジルアーリー期待未知0
			dmg = 0;
		}
		if (_targetPc.hasSkillEffect(MOB_COCA)) { // コカアーリー期待未知0
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
		if (_targetNpc.hasSkillEffect(MOB_BASILL)) { // バジルアーリー期待未知0
			dmg = 0;
		}
		if (_targetNpc.hasSkillEffect(MOB_COCA)) { // コカアーリー期待未知0
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


		for (L1DollInstance doll : _targetPc.getDollList()) {// マジックドールによる追加の防御
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
		/** ウィザードの場合、ダメージ外部化さ */
		if (_calcType == PC_PC) {
			if (_pc.getType() == 3) {
				dmg *= Config.WIZARD_MAGIC_DAMAGE;
			}
		}
		/** ウィザードの場合、ダメージ外部化さ */
		if (_calcType == PC_NPC) {
			if (_pc.getType() == 3) {
				dmg *= Config.WIZARD_MONSTER_DAMAGE;
			}
		}
		/** ウィザードの場合、追加ダメージ適用 */
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
				if (_targetPc.getAbility().getTotalWis() >= _random.nextInt(70)) {// ソース100
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

		/*** 新規レベルの保護 ***/
		if (_calcType == PC_PC) {
			int castle_id = L1CastleLocation.getCastleIdByArea(_pc);
			if (castle_id == 0) {
				if (_targetPc.getLevel() < Config.AUTO_REMOVELEVEL || _pc.getLevel() < Config.AUTO_REMOVELEVEL) {
					dmg /= 2;
					_pc.sendPackets(new S_SystemMessage("新規レベルはダメージの50％だけがかかります。"));
					_targetPc.sendPackets(new S_SystemMessage("新規レベルはダメージを50％だけます。"));
				}
			}
		}
		/*** 新規レベルの保護 ***/

		/** 新規血盟攻撃途方もなく **/
		if (_calcType == PC_PC) {
			 boolean isAliveBoss = BossAlive.getInstance().isBossAlive(_targetPc.getMapId());
			int castle_id = L1CastleLocation.getCastleIdByArea(_pc);
			if (castle_id == 0 && !isAliveBoss) {
				if (_pc.getClanid() == Config.NEW_CLAN || _targetPc.getClanid() == Config.NEW_CLAN) {
					if (Config.NEW_CLAN_PROTECTION_PROCESS) {
						dmg = 0;
						_pc.sendPackets(new S_SystemMessage("新規保護血盟は相互に攻撃されていません"));
						_targetPc.sendPackets(new S_SystemMessage("新規保護血盟は相互に攻撃されていません"));
					} else {
						dmg /= 2;
						_pc.sendPackets(new S_SystemMessage("新規保護血盟はダメージを50％だけかかります。"));
						_targetPc.sendPackets(new S_SystemMessage("新規保護血盟はダメージを50％だけます。"));
					}
				}
			}
		}
		/**新規血盟攻撃途方もなく **/

		/** バトルゾーン **/
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

	/** プレイヤー・NPCからNPCへのダメージ算出 **/
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
			} // パプリオン血痕1.5モデム
		}

		return dmg;
	}

	private int calcMagicDiceDamage(int skillId) {
		L1Skills l1skills = SkillsTable.getInstance().getTemplate(skillId);
		int dice = l1skills.getDamageDice();
		int diceCount = l1skills.getDamageDiceCount();
		int value = l1skills.getDamageValue();
		int magicDamage = 0;
		double PowerMr = 0; // 魔

		Random random = new Random();

		dice += getSpellPower() / 2;

		for (int i = 0; i < diceCount; i++) {
			magicDamage += (_random.nextInt(dice) + 1);
		}

		magicDamage += value * (1 + getSpellPower() / 10);

		/** クリティカル発生部分 */
		double criticalCoefficient = 1.4;
		int rnd = random.nextInt(100) + 1;

		if (_calcType == PC_PC || _calcType == PC_NPC) {
			int propCritical = CalcStat.calcMagicCritical(_pc.ability.getTotalInt())+_pc.getMagicCritical();
			switch (skillId) {
			// 6レベル以下広域魔法を除く攻撃魔法
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
			// 魔眼一定確率で魔法クリティカル+1
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
		// ディス魔法はロウフルに応じてダメージボトムアップ処理。
		// : カオティック数値が高いほどダメージ下向き
		if (skillId == DISINTEGRATE) {
			int lawful = getLawful();
			if (lawful <= 0)
				lawful = 1;
			magicDamage += magicDamage * (lawful / 32767);
		}
		//
		if (getTargetMr() < 101) {
			PowerMr = getTargetMr() / (double) 200; // 魔100と、10当たり（基本ダメージ*魔法の定数）の5％ダメージ
			// 減る設定合計50％
		} else {
			PowerMr = 0.5 + (getTargetMr() - 100) / (double) 400; // 魔100超過分について10あたり
			// （基本ダメージ*魔法の定数）の1％減る設定100当たり10％
		} // 魔400と、魔法ダメージ0
		if (skillId == FINAL_BURN) {
			PowerMr = 0;
		}
		magicDamage -= magicDamage * PowerMr; //まず、魔によるダメージ減少から処理
		double attrDeffence = calcAttrResistance(l1skills.getAttr());

		// 属性防御100当たり45％減。
		// 10当たり4.5％超過分について
		// 10当たり0.9％減少し設定
		magicDamage -= magicDamage * attrDeffence; // 魔によるダメージ減少後の属性防御による
		// ダメージ減少処理
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			magicDamage += _pc.getBaseMagicDmg(); // ベースステータス魔法ダメージボーナスを追加
		}
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			int weaponAddDmg = 0;
			L1ItemInstance weapon = _pc.getWeapon();
			if (weapon != null) {
				weaponAddDmg = weapon.getItem().getMagicDmgModifier();
			}
			magicDamage += weaponAddDmg; // 武器による魔法ダメージ追加
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
	 * MRによる魔法ダメージ減少を処理する修正日：2013.02.22修飾子：メールキス
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
			// 属性100超過分について0.45の1/5程度減少されるように変更
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
		if (_targetPc.hasSkillEffect(MOB_BASILL)) { // バジルアーリー期待未知0
			damage = 0;
			drainMana = 0;
		}
		if (_targetPc.hasSkillEffect(MOB_COCA)) { // コカアーリー期待未知0
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
		if (_targetNpc.hasSkillEffect(MOB_BASILL)) { //バジルアーリー期待未知0
			damage = 0;
			drainMana = 0;
		}
		if (_targetNpc.hasSkillEffect(MOB_COCA)) { // コカアーリー期待未知0
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

	// ●●●● 戦士タイタンダメージを算出 ●●●●
	private int calcTitanDamage() {
		double damage = 0;
		L1ItemInstance weapon = null;
		weapon = _targetPc.getWeapon();
		if (weapon != null) {
			damage = Math.round((weapon.getItem().getDmgLarge() + weapon.getEnchantLevel() + weapon.getItem().getDmgModifier()) * 2);
		}
		return (int) damage;
	}
}