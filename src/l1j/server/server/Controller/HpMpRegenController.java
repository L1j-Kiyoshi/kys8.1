package l1j.server.server.Controller;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.GameSystem.Robot.L1RobotInstance;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1HouseLocation;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1EffectInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_WeekQuest;
import l1j.server.server.utils.CalcStat;

public class HpMpRegenController implements Runnable {
	private static Logger _log = Logger.getLogger(HpMpRegenController.class.getName());

	private static Random _random = new Random(System.nanoTime());

	private final int _regenTime;

	public HpMpRegenController(int regenTime) {
		_regenTime = regenTime;
	}

	public void start() {
		GeneralThreadPool.getInstance().execute(this);
	}

	private Collection<L1PcInstance> _list = null;

	public void run() {
		while (true) {
			try {
				_list = L1World.getInstance().getAllPlayers();
				for (L1PcInstance pc : _list) {
					if (pc == null || (pc.getNetConnection() == null  && !(pc instanceof L1RobotInstance))|| pc.noPlayerCK || pc.noPlayerck2) {
						continue;
					} else {
						if (pc.isDead() || pc.isPrivateShop() || pc.isAutoClanjoin()) {
							continue;
						} else {
							// HP 부문 먼저 쳐리
							pc.updateLevel();
							pc.addHpregenPoint(pc.getHpcurPoint());
							pc.setHpcurPoint(4);
							if (pc.getHpregenMax() <= pc.getHpregenPoint()) {
								pc.setHpregenPoint(0);
								regenHp(pc);
							}

							pc.addMpregenPoint(pc.getMpcurPoint());
							pc.setMpcurPoint(4);
							if (64 <= pc.getMpregenPoint()) {
								pc.setMpregenPoint(0);
								regenMp(pc);
							}
							
							if (pc.hasSkillEffect(L1SkillId.STRIKER_GALE)) {
								pc.sendPackets(new S_PacketBox(S_PacketBox.ER_UpDate, pc.get_PlusEr()), true);
							}
							DanteasBuff(pc);
							clanbuff(pc);
						}
						주퀘검사();
					}
				}
			} catch (Exception e) {
				System.out.println("피엠틱컨트롤러 오류  : " + e);
			} finally {
				try {
					_list = null;
					Thread.sleep(_regenTime);
				} catch (Exception e) {
				}
			}
		}

	}

	private void 주퀘검사(){
		try{
			Collection<L1PcInstance> _list;
			_list = L1World.getInstance().getAllPlayers();
			for (L1PcInstance pc : _list) {
			//다시 주간퀘스트로 할라면 W로 변경
			SimpleDateFormat day = new SimpleDateFormat("D"); 
			String nowday = day.format(new Date());
			if(pc.getQuestWeek() != Integer.parseInt(nowday)){
					//재설정
					if(pc.getLevel() <= 65){
						pc.setWeekType(1);
					}else if(pc.getLevel()>65 && pc.getLevel() <= 85 ){
						pc.setWeekType(2);
					}else{
						pc.setWeekType(3);
					}
					for(int i = 0 ; i < 3; i++)
						pc.setLineClear(i, false); //라인클리어 false
					
					for(int i = 0 ; i < 3; i++)
						pc.setReward(i, false);//보상받기 여부 false
					
					for(int i = 0; i <9; i++)
						pc.setWcount(i, 0); //잡은 마리수 초기화
					
			//		pc.sendPackets(new S_SystemMessage("\\aG 몬스터 일일 퀘스트가 초기화 되었습니다."), true);
					pc.setQuestWeek(Integer.parseInt(nowday));
					//
					pc.sendPackets(new S_WeekQuest(pc));
				}

			}
		}catch(Exception e){}
	}
	private int baseMpr = 1;

	private int itemMpr = 0;

	private int baseStatMpr = 0;

	private int wis = 0;

	private int mpr = 0;

	private int newMp = 0;

	public void regenMp(L1PcInstance _pc) {
		try {
			if (_pc.isDead()) {
				return;
			}
			if (_pc.getCurrentMp() == _pc.getMaxMp()) {
				return;
			}
			baseMpr = 1;
			itemMpr = _pc.getInventory().mpRegenPerTick();
			// 베이스 WIS 회복 보너스
			baseStatMpr =  CalcStat.calcMpr(_pc.getAbility().getBaseWis());

			itemMpr += _pc.getMpr();

			/** 배고픔, 중량 * */
			if (_pc.get_food() < 40 || isOverWeight(_pc)) {
				baseMpr = 0;
				baseStatMpr = 0;
				if (itemMpr > 0) {
					itemMpr = 0;
				}
			} else {
				wis = _pc.getAbility().getTotalWis();
				if (wis == 15 || wis == 16) {
					baseMpr = 2;
				} else if (wis >= 17) {
					baseMpr = 3;
				}

				if (_pc.hasSkillEffect(L1SkillId.STATUS_BLUE_POTION) == true) {
					if (wis < 11) {
						wis = 11;
					}
					baseMpr += wis - 10;
				}
				if (_pc.hasSkillEffect(L1SkillId.STATUS_BLUE_POTION2) == true) {
					if (wis < 11) {
						wis = 11;
					}
					baseMpr += wis - 8;
				}
				if (_pc.hasSkillEffect(L1SkillId.MEDITATION) == true) {
					baseMpr += 5;
				}
				if (_pc.hasSkillEffect(L1SkillId.CONCENTRATION) == true) {
					baseMpr += 4;
				}
				if (L1HouseLocation.isInHouse(_pc.getX(), _pc.getY(), _pc.getMapId())) {
					baseMpr += 3;
				}
				if (isInn(_pc)) {
					baseMpr += 3;
				}
				if (L1HouseLocation.isRegenLoc(_pc, _pc.getX(), _pc.getY(), _pc.getMapId())) {
					baseMpr += 3;
				}
			}

			mpr = baseMpr + itemMpr + baseStatMpr;
			newMp = _pc.getCurrentMp() + mpr;

			_pc.setCurrentMp(newMp);
		} catch (Exception e) {
		} finally {
			baseMpr = 1;
			itemMpr = 0;
			baseStatMpr = 0;
			wis = 0;
			mpr = 0;
			newMp = 0;
		}
	}

	private int maxBonus = 1;

	private int basebonus = 0;

	private int equipHpr = 0;

	private int bonus = 0;

	private boolean inLifeStream = false;

	private int newHp = 0;

	public void regenHp(L1PcInstance _pc) {
		try {
			if (_pc.isDead()) {
				return;
			}
			if (_pc.getCurrentHp() == _pc.getMaxHp() && !isUnderwater(_pc)) {
				return;
			}
			maxBonus = 1;
			basebonus = 0;
			equipHpr = 0;
			bonus = 0;
			inLifeStream = false;
			// 공복과 중량의 체크
			if (_pc.get_food() < 40 || isOverWeight(_pc) || _pc.hasSkillEffect(L1SkillId.BERSERKERS)) {
				bonus = 0;
				basebonus = 0;
				// 장비에 의한 HPR 증가는 만복도, 중량에 의해 없어지지만, 감소인 경우는 만복도, 중량에 관계없이 효과가
				// 남는다
				if (equipHpr > 0) {
					equipHpr = 0;
				}
			} else {
				// CON 보너스
				if (11 < _pc.getLevel() && 14 <= _pc.getAbility().getTotalCon()) {
					maxBonus = _pc.getAbility().getTotalCon() - 12;
					if (25 < _pc.getAbility().getTotalCon()) {
						maxBonus = 14;
					}
				}
				// 베이스 CON 보너스
				basebonus = CalcStat.calcHpr(_pc.getAbility().getBaseCon());

				equipHpr = _pc.getInventory().hpRegenPerTick();
				equipHpr += _pc.getHpr();
				bonus = _random.nextInt(maxBonus) + 1;

				if (_pc.hasSkillEffect(L1SkillId.NATURES_TOUCH)) {
					bonus += 15;
				}
				if (L1HouseLocation.isInHouse(_pc.getX(), _pc.getY(), _pc.getMapId())) {
					bonus += 5;
				}
				if (isInn(_pc)) {
					bonus += 5;
				}
				if (L1HouseLocation.isRegenLoc(_pc, _pc.getX(), _pc.getY(), _pc.getMapId())) {
					bonus += 5;
				}

				if (isPlayerInLifeStream(_pc)) {
					inLifeStream = true;
					// 고대의 공간, 마족의 신전에서는 HPR+3은 없어져?
					bonus += 3;
				}
			}
			newHp = 0;
			newHp = _pc.getCurrentHp();
			newHp += bonus + equipHpr + basebonus;
			
			if (newHp < 1) {
				newHp = 1; // HPR 감소 장비에 의해 사망은 하지 않는다
			}
			// 수중에서의 감소 처리
			// 라이프 시냇물로 감소를 없앨 수 있을까 불명
			if (isUnderwater(_pc)) {
				newHp -= 20;
				if (newHp < 1) {
					if (_pc.isGm()) {
						newHp = 1;
					} else {
						_pc.death(null, true); // HP가 0이 되었을 경우는 사망한다.
					}
				}
				// Lv50 퀘스트의 고대의 공간 1 F2F에서의 감소 처리
			} else if (isLv50Quest(_pc) && !inLifeStream) {
				newHp -= 10;
				if (newHp < 1) {
					if (_pc.isGm()) {
						newHp = 1;
					} else {
						_pc.death(null, true); // HP가 0이 되었을 경우는 사망한다.
					}
				}
				// 마족의 신전에서의 감소 처리
			} else if (_pc.getMapId() == 410 && !inLifeStream) {
				newHp -= 10;
				if (newHp < 1) {
					if (_pc.isGm()) {
						newHp = 1;
					} else {
						_pc.death(null, true); // HP가 0이 되었을 경우는 사망한다.
					}
				}
			}

			if (!_pc.isDead()) {
				_pc.setCurrentHp(Math.min(newHp, _pc.getMaxHp()));
			}
		} catch (Exception e) {
		} finally {
			maxBonus = 1;
			basebonus = 0;
			equipHpr = 0;
			bonus = 0;
			inLifeStream = false;
			newHp = 0;
		}
	}

	private boolean isUnderwater(L1PcInstance pc) {
		// 워터 부츠 장비시인가, 에바의 축복 상태이면, 수중은 아니면 간주한다.
		if (pc.hasSkillEffect(L1SkillId.STATUS_UNDERWATER_BREATH)) {
			return false;
		}
		if (pc.getInventory().checkEquipped(20207)) {
			return false;
		}
		if (pc.getInventory().checkEquipped(21048)
				&& pc.getInventory().checkEquipped(21049)
				&& pc.getInventory().checkEquipped(21050)) {
			return false;
		}

		return pc.getMap().isUnderwater();
	}

	private boolean isOverWeight(L1PcInstance pc) {
		if (pc.hasSkillEffect(L1SkillId.EXOTIC_VITALIZE)
				|| pc.hasSkillEffect(L1SkillId.ADDITIONAL_FIRE)
				|| pc.hasSkillEffect(L1SkillId.SCALES_WATER_DRAGON)) {
			return false;
		}
		if (pc.getInventory().checkEquipped(20049)) {
			return false;
		}
		if (isInn(pc)) {
			return false;
		}
		return (120 <= pc.getInventory().getWeight100()) ? true : false;
	}

	private int mapId = 0;

	private boolean isLv50Quest(L1PcInstance pc) {
		mapId = pc.getMapId();
		return (mapId == 2000 || mapId == 2001) ? true : false;
	}
	
	private void DanteasBuff(L1PcInstance pc) {
		if (pc.isDanteasBuff == false) {
			if (pc.getMapId() == 479) {
				pc.addDmgup(2);
				pc.addBowDmgup(2);
//				pc.addSp(1);
				pc.getAbility().addSp(1);
				pc.addMpr(2);
				pc.sendPackets(new S_SPMR(pc));
				pc.isDanteasBuff = true;
				pc.sendPackets(new S_PacketBox(S_PacketBox.UNLIMITED_ICON, 5219, true));
				pc.sendPackets(new S_SystemMessage("단테스 버프 : 근거리/원거리 대미지+2, SP+1, MP 회복+2 "));
			}
		} else {
			boolean DanteasOk = false;
			if (pc.getMapId() == 479) {
				DanteasOk = true;
			}
			if (DanteasOk == false) {
				pc.addDmgup(-2);
				pc.addBowDmgup(-2);
//				pc.addSp(-1);
				pc.getAbility().addSp(-1);
				pc.addMpr(-2);
				pc.sendPackets(new S_SPMR(pc));
				pc.isDanteasBuff = false;
				pc.sendPackets(new S_PacketBox(S_PacketBox.UNLIMITED_ICON, 5219, false));
				pc.sendPackets(new S_SystemMessage("단테스의 버프 : 버프가 사라짐"));
			}
		}
	}

	private void clanbuff(L1PcInstance pc) {
		String clanName = pc.getClanname();
		L1Clan clan = L1World.getInstance().getClan(clanName);
		if (pc.getClanid() != 0 && clan.getOnlineClanMember().length >= Config.CLAN_COUNT && !pc.isClanBuff()) {
			pc.setSkillEffect(L1SkillId.CLANBUFF_YES, 0);
			//pc.sendPackets(new S_PacketBox(S_PacketBox.CLAN_BUFF_ICON, 1));
			pc.sendPackets(new S_PacketBox(S_PacketBox.UNLIMITED_ICON1, 450, true));
			pc.setClanBuff(true);
		} else if (pc.getClanid() != 0 && clan.getOnlineClanMember().length < Config.CLAN_COUNT && pc.isClanBuff()) {
			pc.killSkillEffectTimer(L1SkillId.CLANBUFF_YES);
			pc.sendPackets(new S_PacketBox(S_PacketBox.UNLIMITED_ICON1, 450, false));
			pc.setClanBuff(false);
		}
	}

	/**
	 * 지정한 PC가 라이프 시냇물의 범위내에 있는지 체크한다
	 * 
	 * @param pc
	 *            PC
	 * @return true PC가 라이프 시냇물의 범위내에 있는 경우
	 */
	private static L1EffectInstance effect = null;

	private static boolean isPlayerInLifeStream(L1PcInstance pc) {
		for (L1Object object : pc.getKnownObjects()) {
			if (object instanceof L1EffectInstance == false) {
				continue;
			}
			effect = (L1EffectInstance) object;
			if (effect.getNpcId() == 81169
					&& effect.getLocation().getTileLineDistance(pc.getLocation()) < 4) {
				return true;
			}
		}
		effect = null;
		return false;
	}

	private boolean isInn(L1PcInstance pc) {
		mapId = pc.getMapId();
		return (mapId == 16384 || mapId == 16896 || mapId == 17408
				|| mapId == 17492 || mapId == 17820 || mapId == 17920
				|| mapId == 18432 || mapId == 18944 || mapId == 19456
				|| mapId == 19968 || mapId == 20480 || mapId == 20992
				|| mapId == 21504 || mapId == 22016 || mapId == 22528
				|| mapId == 23040 || mapId == 23552 || mapId == 24064
				|| mapId == 24576 || mapId == 25088) ? true : false;
	}

}
