package l1j.server.server.utils;

import java.util.ArrayList;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.server.GameServerSetting;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.datatables.PetTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_PetPack;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.templates.L1Pet;

public class CalcExp {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	private static Logger _log = Logger.getLogger(CalcExp.class.getName());

	public static final int MAX_EXP = ExpTable.getExpByLevel(100) - 1;

	private CalcExp() {
	}

	public static void calcExp(L1PcInstance l1pcinstance, int targetid,
			ArrayList<?> acquisitorList, ArrayList<?> hateList, int exp) {

		int i = 0;
		double party_level = 0;
		double dist = 0;
		int member_exp = 0;
		int member_lawful = 0;
		L1Object l1object = L1World.getInstance().findObject(targetid);
		L1NpcInstance npc = (L1NpcInstance) l1object;

		// 헤이트의 합계를 취득
		L1Character acquisitor;
		int hate = 0;
		int acquire_exp = 0;
		int acquire_lawful = 0;
		int party_exp = 0;
		int party_lawful = 0;
		int totalHateExp = 0;
		int totalHateLawful = 0;
		int partyHateExp = 0;
		int partyHateLawful = 0;
		int ownHateExp = 0;

		if (acquisitorList.size() != hateList.size()) {
			return;
		}
		for (i = hateList.size() - 1; i >= 0; i--) {
			acquisitor = (L1Character) acquisitorList.get(i);
			hate = (Integer) hateList.get(i);
			if (acquisitor != null && !acquisitor.isDead()) {
				totalHateExp += hate;
				if (acquisitor instanceof L1PcInstance) {
					totalHateLawful += hate;
				}
			} else { // null였거나 죽어 있으면(자) 배제
				acquisitorList.remove(i);
				hateList.remove(i);
			}
		}
		if (totalHateExp == 0) { // 취득자가 없는 경우
			return;
		}

		if (l1object != null && !(npc instanceof L1PetInstance)
				&& !(npc instanceof L1SummonInstance)) {
			// int exp = npc.get_exp();
			if (!L1World.getInstance().isProcessingContributionTotal()
					&& l1pcinstance.getHomeTownId() > 0) {
				int contribution = npc.getLevel() / 10;
				l1pcinstance.addContribution(contribution);
			}
			int lawful = npc.getLawful();

			if (l1pcinstance.isInParty()) { // 파티중
				// 파티의 헤이트의 합계를 산출
				// 파티 멤버 이외에는 그대로 배분
				partyHateExp = 0;
				partyHateLawful = 0;
				for (i = hateList.size() - 1; i >= 0; i--) {
					acquisitor = (L1Character) acquisitorList.get(i);
					hate = (Integer) hateList.get(i);
					if (acquisitor instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) acquisitor;
						if (pc == l1pcinstance) {
							partyHateExp += hate;
							partyHateLawful += hate;
						} else if (l1pcinstance.getParty().isMember(pc)) {
							partyHateExp += hate;
							partyHateLawful += hate;
						} else {
							if (totalHateExp > 0) {
								acquire_exp = (exp * hate / totalHateExp);
							}
							if (totalHateLawful > 0) {
								acquire_lawful = (lawful * hate / totalHateLawful);
							}
							AddExp(pc, acquire_exp, acquire_lawful);
						}
					} else if (acquisitor instanceof L1PetInstance) {
						L1PetInstance pet = (L1PetInstance) acquisitor;
						L1PcInstance master = (L1PcInstance) pet.getMaster();
						if (master == l1pcinstance) {
							partyHateExp += hate;
						} else if (l1pcinstance.getParty().isMember(master)) {
							partyHateExp += hate;
						} else {
							if (totalHateExp > 0) {
								acquire_exp = (exp * hate / totalHateExp);
							}
							AddExpPet(pet, acquire_exp);
						}
					} else if (acquisitor instanceof L1SummonInstance) {
						L1SummonInstance summon = (L1SummonInstance) acquisitor;
						L1PcInstance master = (L1PcInstance) summon.getMaster();
						if (master == l1pcinstance) {
							partyHateExp += hate;
						} else if (l1pcinstance.getParty().isMember(master)) {
							partyHateExp += hate;
						} else {
						}
					}
				}
				if (totalHateExp > 0) {
					party_exp = (exp * partyHateExp / totalHateExp);
				}
				if (totalHateLawful > 0) {
					party_lawful = (lawful * partyHateLawful / totalHateLawful);
				}

				// EXP, 로우훌 배분

				// 프리보나스
				double pri_bonus = 0;
				L1PcInstance leader = l1pcinstance.getParty().getLeader();
				if (leader.isCrown()
						&& (l1pcinstance.knownsObject(leader)
								|| l1pcinstance.equals(leader))) {
					pri_bonus = 0.059;
				}

				// PT경험치의 계산
				L1PcInstance[] ptMembers = l1pcinstance.getParty().getMembers();
				double pt_bonus = 0;
				for (L1PcInstance each : l1pcinstance.getParty().getMembers()) {
					if (l1pcinstance.knownsObject(each) || l1pcinstance.equals(each)) {
						party_level += each.getLevel() * each.getLevel();
					}
					if (l1pcinstance.knownsObject(each)) {
						pt_bonus += 0.04;
					}
				}

				party_exp = (int) (party_exp * (1 + pt_bonus + pri_bonus));

				// 자캐릭터와 그 애완동물·사몬의 헤이트의 합계를 산출
				if (party_level > 0) {
					dist = ((l1pcinstance.getLevel() * l1pcinstance.getLevel()) / party_level);
				}
				member_exp = (int) (party_exp * dist);
				member_lawful = (int) (party_lawful * dist);

				ownHateExp = 0;
				for (i = hateList.size() - 1; i >= 0; i--) {
					acquisitor = (L1Character) acquisitorList.get(i);
					hate = (Integer) hateList.get(i);
					if (acquisitor instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) acquisitor;
						if (pc == l1pcinstance) {
							ownHateExp += hate;
						}
					} else if (acquisitor instanceof L1PetInstance) {
						L1PetInstance pet = (L1PetInstance) acquisitor;
						L1PcInstance master = (L1PcInstance) pet.getMaster();
						if (master == l1pcinstance) {
							ownHateExp += hate;
						}
					} else if (acquisitor instanceof L1SummonInstance) {
						L1SummonInstance summon = (L1SummonInstance) acquisitor;
						L1PcInstance master = (L1PcInstance) summon.getMaster();
						if (master == l1pcinstance) {
							ownHateExp += hate;
						}
					}
				}
				// 자캐릭터와 그 애완동물·사몬에 분배
				if (ownHateExp != 0) { // 공격에 참가하고 있었다
					for (i = hateList.size() - 1; i >= 0; i--) {
						acquisitor = (L1Character) acquisitorList.get(i);
						hate = (Integer) hateList.get(i);
						if (acquisitor instanceof L1PcInstance) {
							L1PcInstance pc = (L1PcInstance) acquisitor;
							if (pc == l1pcinstance) {
								if (ownHateExp > 0) {
									acquire_exp = (member_exp * hate / ownHateExp);
								}
								
								if(pc.getMapId() == 111 || pc.getMapId() == 1700){ //정상
									int newExp = (exp * partyHateExp / totalHateExp);
									AddExp(pc, newExp, member_lawful);
								}else{
									AddExp(pc, acquire_exp, member_lawful);
								}
							}
						} else if (acquisitor instanceof L1PetInstance) {
							L1PetInstance pet = (L1PetInstance) acquisitor;
							L1PcInstance master = (L1PcInstance) pet
									.getMaster();
							if (master == l1pcinstance) {
								if (ownHateExp > 0) {
									acquire_exp = (member_exp * hate / ownHateExp);
								}
								AddExpPet(pet, acquire_exp);
							}
						} else if (acquisitor instanceof L1SummonInstance) {
						}
					}
				} else { // 공격에 참가하고 있지 않았다
					// 자캐릭터에만 분배
					if(l1pcinstance.getMapId() == 111 || l1pcinstance.getMapId() == 1700){ //정상
						int newExp = (exp * partyHateExp / totalHateExp);
						AddExp(l1pcinstance, newExp, member_lawful);
					}else{
						AddExp(l1pcinstance, member_exp, member_lawful);
					}
				}

				// 파티 멤버와 그 애완동물·사몬의 헤이트의 합계를 산출
				for (int cnt = 0; cnt < ptMembers.length; cnt++) {
					if (l1pcinstance.knownsObject(ptMembers[cnt])) {
						if (party_level > 0) {
							dist = ((ptMembers[cnt].getLevel() * ptMembers[cnt]
									.getLevel()) / party_level);
						}
						member_exp = (int) (party_exp * dist);
						member_lawful = (int) (party_lawful * dist);

						ownHateExp = 0;
						for (i = hateList.size() - 1; i >= 0; i--) {
							acquisitor = (L1Character) acquisitorList.get(i);
							hate = (Integer) hateList.get(i);
							if (acquisitor instanceof L1PcInstance) {
								L1PcInstance pc = (L1PcInstance) acquisitor;
								if (pc == ptMembers[cnt]) {
									ownHateExp += hate;
								}
							} else if (acquisitor instanceof L1PetInstance) {
								L1PetInstance pet = (L1PetInstance) acquisitor;
								L1PcInstance master = (L1PcInstance) pet
										.getMaster();
								if (master == ptMembers[cnt]) {
									ownHateExp += hate;
								}
							} else if (acquisitor instanceof L1SummonInstance) {
								L1SummonInstance summon = (L1SummonInstance) acquisitor;
								L1PcInstance master = (L1PcInstance) summon
										.getMaster();
								if (master == ptMembers[cnt]) {
									ownHateExp += hate;
								}
							}
						}
						// 파티 멤버와 그 애완동물·사몬에 분배
						if (ownHateExp != 0) { // 공격에 참가하고 있었다
							for (i = hateList.size() - 1; i >= 0; i--) {
								acquisitor = (L1Character) acquisitorList
										.get(i);
								hate = (Integer) hateList.get(i);
								if (acquisitor instanceof L1PcInstance) {
									L1PcInstance pc = (L1PcInstance) acquisitor;
									if (pc == ptMembers[cnt]) {
										if (ownHateExp > 0) {
											acquire_exp = (member_exp * hate / ownHateExp);
										}
										if(pc.getMapId() == 111 || pc.getMapId() == 1700){ //정상
											int newExp = (exp * partyHateExp / totalHateExp);
											AddExp(pc, newExp, member_lawful);
										}else{
											AddExp(pc, acquire_exp, member_lawful);
										}
									}
								} else if (acquisitor instanceof L1PetInstance) {
									L1PetInstance pet = (L1PetInstance) acquisitor;
									L1PcInstance master = (L1PcInstance) pet
											.getMaster();
									if (master == ptMembers[cnt]) {
										if (ownHateExp > 0) {
											acquire_exp = (member_exp * hate / ownHateExp);
										}
										AddExpPet(pet, acquire_exp);
									}
								} else if (acquisitor instanceof L1SummonInstance) {
								}
							}
						} else { // 공격에 참가하고 있지 않았다
							// 파티 멤버에만 분배
							if(ptMembers[cnt].getMapId() == 111 || ptMembers[cnt].getMapId() == 1700){ //정상
								int newExp = (exp * partyHateExp / totalHateExp);
								AddExp(ptMembers[cnt], newExp, member_lawful);
							}else{
								AddExp(ptMembers[cnt], member_exp, member_lawful);
							}
						}
					}
				}
			} else { // 파티를 짜지 않았다
				// EXP, 로우훌의 분배
				for (i = hateList.size() - 1; i >= 0; i--) {
					acquisitor = (L1Character) acquisitorList.get(i);
					hate = (Integer) hateList.get(i);
					acquire_exp = (exp * hate / totalHateExp);
					if (acquisitor instanceof L1PcInstance) {
						if (totalHateLawful > 0) {
							acquire_lawful = (lawful * hate / totalHateLawful);
						}
					}

					if (acquisitor instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) acquisitor;
						AddExp(pc, acquire_exp, acquire_lawful);
					} else if (acquisitor instanceof L1PetInstance) {
						L1PetInstance pet = (L1PetInstance) acquisitor;
						AddExpPet(pet, acquire_exp);
					} else if (acquisitor instanceof L1SummonInstance) {
					}
				}
			}
		}
	}

	private static void AddExp(L1PcInstance pc, int exp, int lawful) {
		if(pc.isGhost()){
			return;
		}
		int add_lawful = (int) (lawful * Config.RATE_LAWFUL) * -1;
		pc.addLawful(add_lawful);

		/** 로봇시스템 **/
		if(pc.getRobotAi() != null ){			
			return;
		}
		
		if (pc.getLevel()>=Config.LIMITLEVEL && pc.getExp()>=ExpTable.getExpByLevel(Config.LIMITLEVEL+1)){//경험치
			pc.sendPackets(new S_ChatPacket(pc,"레벨제한으로 더이상 경험치 획득이 불가능합니다"));
				return;
			}
		
		double exppenalty = ExpTable.getPenaltyRate(pc.getLevel());
		double foodBonus = 0;
		double expposion = 0;
		double dollBonus = 0;
		double levelupBonus = 0;
		double abyssBonus = 0;
		double clanBonus = 0;
		double comboBonus = 0; //콤보시스템
		double addexp1 = 0; //성장의문장
		
		if (pc.getPeerage() == 1){ abyssBonus += 0.05;
		} else if (pc.getPeerage() == 2){ abyssBonus += 0.1;
		} else if (pc.getPeerage() == 3){ abyssBonus += 0.15;
		} else if (pc.getPeerage() == 4){ abyssBonus += 0.2;
		} else if (pc.getPeerage() == 5){ abyssBonus += 0.25;
		} else if (pc.getPeerage() == 6){ abyssBonus += 0.3;
		} else if (pc.getPeerage() == 7){ abyssBonus += 0.35;
		} else if (pc.getPeerage() == 8){ abyssBonus += 0.4;
		} else if (pc.getPeerage() == 9){ abyssBonus += 0.45;
		} else if (pc.getPeerage() == 10){ abyssBonus += 0.5;
		} else if (pc.getPeerage() == 11){ abyssBonus += 0.55;
		} else if (pc.getPeerage() == 12){ abyssBonus += 0.6;
		} else if (pc.getPeerage() == 13){ abyssBonus += 0.65;
		} else if (pc.getPeerage() == 14){ abyssBonus += 0.7;
		} else if (pc.getPeerage() == 15){ abyssBonus += 0.75;
		} else if (pc.getPeerage() == 16){ abyssBonus += 0.8;
		} else if (pc.getPeerage() == 17){ abyssBonus += 0.85;
		} else if (pc.getPeerage() == 18){ abyssBonus += 0.9; }
		
		if(pc.getClanname() != null && pc.getClanid() > 0){
			if(pc.getClan().getClanExp() >= Config.CLAN_EXP_ONE && pc.getClan().getClanExp() < Config.CLAN_EXP_TWO){ // 혈맹1레벨
				clanBonus += 0.1;
			}
			if(pc.getClan().getClanExp() >= Config.CLAN_EXP_TWO && pc.getClan().getClanExp() < Config.CLAN_EXP_THREE){ // 혈맹2레벨
				clanBonus += 0.15;
			}
			if(pc.getClan().getClanExp() >= Config.CLAN_EXP_THREE && pc.getClan().getClanExp() < Config.CLAN_EXP_FOUR){ // 혈맹3레벨
				clanBonus += 0.2;
			}
			if(pc.getClan().getClanExp() >= Config.CLAN_EXP_FOUR && pc.getClan().getClanExp() < Config.CLAN_EXP_FIVE){ // 혈맹4레벨
				clanBonus += 0.25;
			}
			if(pc.getClan().getClanExp() >= Config.CLAN_EXP_FIVE && pc.getClan().getClanExp() < Config.CLAN_EXP_SIX){ // 혈맹5레벨
				clanBonus += 0.3;
			}
			if(pc.getClan().getClanExp() >= Config.CLAN_EXP_SIX && pc.getClan().getClanExp() < Config.CLAN_EXP_SEVEN){ // 혈맹6레벨
				clanBonus += 0.4;
			}
			if(pc.getClan().getClanExp() >= Config.CLAN_EXP_SEVEN){ // 혈맹7레벨
				clanBonus += 0.5;
			}
		}

		
		for (L1DollInstance doll : pc.getDollList()) {
			int dollType = doll.getDollType();
			if (dollType == L1DollInstance.DOLLTYPE_SNOWMAN_A || dollType == L1DollInstance.DOLLTYPE_SNOWMAN_B || dollType == L1DollInstance.DOLLTYPE_SNOWMAN_C
					|| dollType == L1DollInstance.DOLLTYPE_자이언트 || dollType == L1DollInstance.DOLL_머미로드 
					) {
				dollBonus += 0.1;
			}else if (dollType == L1DollInstance.DOLLTYPE_데스나이트 || dollType == L1DollInstance.DOLLTYPE_DEATHNIGHT){
				dollBonus += 0.2;
			}else if (dollType == L1DollInstance.DOLLTYPE_인어){
				dollBonus += 0.03;
			}
		}
		
		
		if (pc.hasSkillEffect(L1SkillId.COOKING_1_7_N) || pc.hasSkillEffect(L1SkillId.COOKING_1_7_S)) {
			foodBonus += 0.01;
		} else if (pc.hasSkillEffect(L1SkillId.COOKING_1_15_N) || pc.hasSkillEffect(L1SkillId.COOKING_1_15_S)) {
			foodBonus += 0.05;
		} else if (pc.hasSkillEffect(L1SkillId.COOKING_1_23_N) || pc.hasSkillEffect(L1SkillId.COOKING_1_23_S)) {
			foodBonus += 0.09;
		} else if (pc.hasSkillEffect(L1SkillId.COOK_GROW)) {
			foodBonus += 0.04;
		} else if (pc.hasSkillEffect(L1SkillId.천하장사버프)) {
			foodBonus += 0.2;
		} else if(pc.hasSkillEffect(L1SkillId.메티스스프)){
			foodBonus +=0.1;
		}else if(pc.hasSkillEffect(L1SkillId.Matiz_Buff1)){
			foodBonus +=0.1;
		}

		if (pc.hasSkillEffect(L1SkillId.EXP_POTION)) {
			if (pc.PC방_버프) {
				expposion += 0.3;
			} else {
				expposion += 0.2;
			}
		} else if (pc.hasSkillEffect(L1SkillId.COMA_B)) {
			if (pc.PC방_버프) {
				expposion += 0.3;
			} else {
				expposion += 0.20;
			}
		}
		
		L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
		
		double beginnerBonus = 0;

		if(Config.ALT_BEGINNER_BONUS && pc.getLevel() <= 65){
			beginnerBonus += 0.5;
		}
		
		//직장인
		if (pc.getInventory().checkEquipped(10000)) {
			beginnerBonus += 1.5;
		}
		
		// 성장의문장
		int 성장의문장 = pc.getInventory().getEnchantCount(900020);
		if (pc.getInventory().checkEquipped(900020)) {
			beginnerBonus += (0.1 * 성장의문장);
		}
		
		if (pc.hasSkillEffect(L1SkillId.레벨업보너스))
			levelupBonus += 2.23;

		double einhasadBonus = 0;

		if (pc.getEinhasad() > 10000){
			pc.calEinhasad(-exp);
			if (pc.getClan() != null)
			    clan.addBlessCount(exp);
			einhasadBonus += 0.77;
			//einhasadBonus = 1.00;
			if (pc.PC방_버프) {
				einhasadBonus += 0.20;
				pc.calEinhasad(-exp);
				if (pc.getClan() != null)
				    clan.addBlessCount(exp);
				if (pc.hasSkillEffect(80006)) {
			        if (pc.getComboCount() <= 10) { comboBonus = 0.1D * pc.getComboCount();
			        } else if ((pc.getComboCount() > 10) && (pc.getComboCount() <= 15)) {
			          comboBonus = 0.1D * pc.getComboCount();
			          comboBonus += 0.2D * (pc.getComboCount() - 10);
			        } else if (pc.getComboCount() > 15) {comboBonus = 3.0D; }
			        if (comboBonus > 0.0D) { 
			        	pc.calEinhasad(-(int)(exp * comboBonus));
			        	if (pc.getClan() != null)
						    clan.addBlessCount((int)(exp * comboBonus));
			        }
			      }
			      pc.sendPackets(new S_PacketBox(82, pc));
			}
			pc.sendPackets(new S_PacketBox(S_PacketBox.EINHASAD, pc.getEinhasad()));
		}

		double emeraldBonus = 0;

		if (pc.hasSkillEffect(L1SkillId.EMERALD_YES) && pc.getEinhasad() > 10000) {
			emeraldBonus += 0.77;
			pc.calEinhasad(-exp);
			if (pc.getClan() != null)
			    clan.addBlessCount(exp);	
			if (pc.PC방_버프) {
				einhasadBonus += 0.20;
				pc.calEinhasad(-exp);
				if (pc.getClan() != null)
				    clan.addBlessCount(exp);	
				if (pc.hasSkillEffect(80006)) {
			        if (pc.getComboCount() <= 10) {  comboBonus = 0.1D * pc.getComboCount();
			        } else if ((pc.getComboCount() > 10) && (pc.getComboCount() <= 15)) {
			          comboBonus = 0.1D * pc.getComboCount();
			          comboBonus += 0.2D * (pc.getComboCount() - 10);
			        } else if (pc.getComboCount() > 15) {comboBonus = 3.0D;   }
			        if (comboBonus > 0.0D) {
			          pc.calEinhasad(-(int)(exp * comboBonus));
			          if (pc.getClan() != null)
						    clan.addBlessCount((int)(exp * comboBonus));
			        }
			      }
			      pc.sendPackets(new S_PacketBox(82, pc));
			}
			pc.sendPackets(new S_PacketBox(S_PacketBox.EINHASAD, pc.getEinhasad()));
		} else if (pc.hasSkillEffect(L1SkillId.DRAGON_PUPLE) && pc.getEinhasad() > 10000) {
			if (pc.getLevel() >= 49 && pc.getLevel() <= 54)
				einhasadBonus += 0.53;
			else if (pc.getLevel() >= 55 && pc.getLevel() <= 59)
				einhasadBonus += 0.43;
			else if (pc.getLevel() >= 60 && pc.getLevel() <= 64)
				einhasadBonus += 0.33;
			else if (pc.getLevel() >= 65)
				einhasadBonus += 0.23;
			pc.calEinhasad(-exp); 
			if (pc.getClan() != null)
			    clan.addBlessCount(exp);	
			pc.sendPackets(new S_PacketBox(S_PacketBox.EINHASAD, pc));
			if (pc.getEinhasad() <= 10000) {
				pc.removeSkillEffect(L1SkillId.DRAGON_PUPLE);
			}
		} else if (pc.hasSkillEffect(L1SkillId.DRAGON_TOPAZ) && pc.getEinhasad() > 10000) {
			einhasadBonus += 0.8;
			pc.calEinhasad(-exp);
			if (pc.getClan() != null)
			    clan.addBlessCount(exp);	
			if (pc.hasSkillEffect(80006)) {
		        if (pc.getComboCount() <= 10) { comboBonus = 0.1D * pc.getComboCount();
		        } else if ((pc.getComboCount() > 10) && (pc.getComboCount() <= 15)) {
		          comboBonus = 0.1D * pc.getComboCount();
		          comboBonus += 0.2D * (pc.getComboCount() - 10);
		        } else if (pc.getComboCount() > 15) {  comboBonus = 3.0D; }
		        if (comboBonus > 0.0D) {
		            pc.calEinhasad(-(int)(exp * comboBonus));
		            if (pc.getClan() != null)
					    clan.addBlessCount((int)(exp * comboBonus));
		        }
		      }
			pc.sendPackets(new S_PacketBox(S_PacketBox.EINHASAD, pc));
			if (pc.getEinhasad() <= 10000) {
				pc.removeSkillEffect(L1SkillId.DRAGON_TOPAZ);
			}
		}
		
		double clanOnlineBonus = 0;
		
		/** 혈맹버프 추가경험치 20% **/
		if (pc.getClanid() != 0) {
			if (pc.getClan().getOnlineClanMember().length >= Config.CLAN_COUNT) {
				clanOnlineBonus += 0.10;
				//System.out.println("혈맹 : " + clanOnlineBonus);
			}
		}
		
		/** 성혈추가경험치지급 **/
		double BloodBonus = 0;	
		if (clan != null && clan.getCastleId() != 0) {
			BloodBonus += Config.성혈경험치;
			//System.out.println("성혈 : " + BloodBonus);
		}
				
		/** 수련던전 해당레벨 부터 경험치안받도록 사용금지 리뉴얼됨 **/
		if ((pc.getLevel() >= 1 && pc.getLevel() <= 2) && (pc.getMapId() >= 25 && pc.getMapId() <= 28)) {
			return;
		}
		
		int add_exp = (int) (exp * exppenalty * Config.RATE_XP *( 1 + abyssBonus + levelupBonus + beginnerBonus 
				+ foodBonus + clanBonus + expposion + comboBonus + einhasadBonus + emeraldBonus + clanOnlineBonus + BloodBonus 
				+ dollBonus + addexp1));
	
		// 폭렙방지
		if (pc.getLevel() >= 1) {
			if ((add_exp + pc.getExp()) > ExpTable.getExpByLevel((pc.getLevel() + 1))) {
				add_exp = (ExpTable.getExpByLevel((pc.getLevel() + 1)) - pc.getExp());
			}
		}
		
		// 레벨제한
		if (pc.getLevel() >= GameServerSetting.getInstance().get_maxLevel()) {
			// 다음레벨에 필요한 경험치
			int maxexp = ExpTable.getExpByLevel(GameServerSetting.getInstance().get_maxLevel() + 1);
			if (pc.getExp() + add_exp >= maxexp) {
				return;
			}
		}
	
		pc.addExp(add_exp);
		//System.out.println("기본 : " + add_exp);
		pc.addMonsterKill(1);
	}

	private static void AddExpPet(L1PetInstance pet, int exp) {
		L1PcInstance pc = (L1PcInstance) pet.getMaster();

		//int petNpcId = pet.getNpcTemplate().get_npcId();
		int petItemObjId = pet.getItemObjId();

		int levelBefore = pet.getLevel();
		int totalExp = (int) (exp * 50 + pet.getExp());
		if (totalExp >= ExpTable.getExpByLevel(51)) {
			totalExp = ExpTable.getExpByLevel(51) - 1;
		}
		pet.setExp(totalExp);

		pet.setLevel(ExpTable.getLevelByExp(totalExp));

		int expPercentage = ExpTable.getExpPercentage(pet.getLevel(), totalExp);

		int gap = pet.getLevel() - levelBefore;
		for (int i = 1; i <= gap; i++) {
			IntRange hpUpRange = pet.getPetType().getHpUpRange();
			IntRange mpUpRange = pet.getPetType().getMpUpRange();
			pet.addMaxHp(hpUpRange.randomValue());
			pet.addMaxMp(mpUpRange.randomValue());
		}

		pet.setExpPercent(expPercentage);
		pc.sendPackets(new S_PetPack(pet, pc));

		if (gap != 0) { // 레벨업하면(자) DB에 기입한다
			pc.sendPackets(new S_SkillSound(pet.getId(), 6353));// /이건 자기한데 보이게
			Broadcaster.broadcastPacket(pc, new S_SkillSound(pet.getId(), 6353));// 이거는 다른 사람도 보게...
			L1Pet petTemplate = PetTable.getInstance().getTemplate(petItemObjId);
			if (petTemplate == null) { // PetTable에 없다
				_log.warning("L1Pet == null");
				return;
			}
			petTemplate.set_exp(pet.getExp());
			petTemplate.set_level(pet.getLevel());
			petTemplate.set_hp(pet.getMaxHp());
			petTemplate.set_mp(pet.getMaxMp());
			PetTable.getInstance().storePet(petTemplate); // DB에 기입해
			pc.sendPackets(new S_ServerMessage(320, pet.getName())); // \f1%0의 레벨이 올랐습니다.
		}
	}
}