package l1j.server.server.clientpackets;

import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.GameClient;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1War;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.FaceToFace;

public class C_Rank extends ClientBasePacket {

	private static final String C_RANK = "[C] C_Rank";

	private static Logger _log = Logger.getLogger(C_Rank.class.getName());

	private L1ItemInstance weapon;

	public C_Rank(byte abyte0[], GameClient clientthread) throws Exception {
		super(abyte0);

		int type = readC();
		int rank = readC();

		L1PcInstance pc = clientthread.getActiveChar();
		if (pc == null) {  
			return;  
		}
		L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
		String clanname = pc.getClanname();

		switch (type) {
		case 1:// 계급
			String name = readS();
			L1PcInstance targetPc = L1World.getInstance().getPlayer(name);
			if ((!pc.isCrown()) && (pc.getClanRank() != L1Clan.수호) && (pc.getClanRank() != L1Clan.부군주)) {
				pc.sendPackets(new S_SystemMessage("계급 부여 실패 : 계급 부여 권한이 없음."));		
				return;
			}
			if (targetPc != null) {
				if (pc.getClanid() == targetPc.getClanid()) {
					try {
						if ((pc.getClanRank() != L1Clan.군주) && (pc.getClanRank() != L1Clan.수호) && (pc.getClanRank() != L1Clan.부군주)) {
							pc.sendPackets(new S_SystemMessage("계급 부여 실패 : 계급 부여 권한이 없음."));		
							return;
						}
						if ((targetPc.isCrown()) && (targetPc.getId() == targetPc.getClan().getLeaderId())) {
							pc.sendPackets(new S_SystemMessage("계급 부여 실패 : 대상이 혈맹의 군주"));			
							return;
						}
						if ((pc.getClanRank() == L1Clan.부군주) && (rank == 3)) {
							pc.sendPackets(new S_SystemMessage("계급 부여 실패 : 부여 계급이 자신보다 높거나 같은 계급"));		
							return;
						}
						if ((pc.getClanRank() == L1Clan.수호) && (rank == 9)) {
							pc.sendPackets(new S_SystemMessage("계급 부여 실패 : 부여 계급이 자신보다 높거나 같은 계급"));		

							return;
						}
						if ((pc.getClanRank() == L1Clan.수호)
								&& ((targetPc.getClanRank() == L1Clan.군주) || (targetPc.getClanRank() == L1Clan.수호) || (targetPc.getClanRank() == L1Clan.부군주))) {
							pc.sendPackets(new S_SystemMessage("계급 부여 실패 : 대상이 현재 자신보다 높거나 같은 계급"));			
							return;
						}
						targetPc.setClanRank(rank);
						targetPc.save(); // DB에 캐릭터 정보를 기입한다
						//						targetPc.sendPackets(new S_PacketBox(S_PacketBox.MSG_RANK_CHANGED, rank, name));//PC에게 한번더 보낸다? 왜?중복일텐데
						pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_RANK_CHANGED, rank, name));//군주에게 계급 알림
						clan.UpdataClanMember(targetPc.getName() ,targetPc.getClanRank());
						String rankString = "일반";
						if (rank == 7)       rankString = "수련";
						else if (rank == 3)  rankString = "부군주";
						else if (rank == 8)  rankString = "일반";
						else if (rank == 9)  rankString = "수호기사";
						else if (rank == 13) rankString = "정예";
						targetPc.sendPackets(new S_SystemMessage("계급: " + rankString + "(으)로 계급 임명함"));
					} catch (Exception e) {
						_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
					}
				} else {
					pc.sendPackets(new S_SystemMessage("같은 혈맹원이 아닙니다."));	
					return;
				}
			} else {
				L1PcInstance restorePc = CharacterTable.getInstance().restoreCharacter(name);
				if ((restorePc != null) && (restorePc.getClanid() == pc.getClanid())) {
					try {
						if ((restorePc.isCrown()) && (restorePc.getId() == restorePc.getClan().getLeaderId())) {
							pc.sendPackets(new S_SystemMessage("계급 부여 실패 : 대상이 혈맹의 군주."));	
							return;
						}
						if ((pc.getClanRank() != L1Clan.군주) && (pc.getClanRank() != L1Clan.수호) && (pc.getClanRank() != L1Clan.부군주)) {
							pc.sendPackets(new S_SystemMessage("계급 부여 실패 : 계급 부여 권한이 없음."));	
							return;
						}
						if ((pc.getClanRank() == L1Clan.부군주) && (rank == 3)) {
							pc.sendPackets(new S_SystemMessage("계급 부여 실패 : 부여 계급이 자신보다 높거나 같은 계급"));		
							return;
						}
						if ((pc.getClanRank() == L1Clan.수호) && (rank == 9)) {
							pc.sendPackets(new S_SystemMessage("계급 부여 실패 : 부여 계급이 자신보다 높거나 같은 계급"));		
							return;
						}
						if ((pc.getClanRank() == L1Clan.수호)
								&& ((restorePc.getClanRank() == L1Clan.군주) || (restorePc.getClanRank() == L1Clan.수호) || (restorePc.getClanRank() == L1Clan.부군주))) {
							pc.sendPackets(new S_SystemMessage("계급 부여 실패 : 대상이 현재 자신보다 높거나 같은 계급"));			
							return;
						}
						restorePc.setClanRank(rank);
						restorePc.save(); // DB에 캐릭터 정보를 기입한다
						restorePc.sendPackets(new S_PacketBox(S_PacketBox.MSG_RANK_CHANGED, rank, name));
						pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_RANK_CHANGED, rank, name));
						clan.UpdataClanMember(restorePc.getName() ,restorePc.getClanRank());
						String rankString = "일반";
						if (rank == 7)       rankString = "수련";
						else if (rank == 3)  rankString = "부군주";
						else if (rank == 8)  rankString = "일반";
						else if (rank == 9)  rankString = "수호기사";
						else if (rank == 13) rankString = "정예";
						for (L1PcInstance mem : clan.getOnlineClanMember()) {
							mem.sendPackets(new S_SystemMessage(restorePc.getName() + " 님의 계급이 " + rankString + "(으)로 변경되었습니다."));	
						}
					} catch (Exception e) {
						_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
					}
				} else {
					pc.sendPackets(new S_SystemMessage("그러한 케릭터는 없습니다."));	
					return;
				}
				restorePc = null;
			}
			break;
		case 2://목록
			try {
				if (clan.getAlliance() != 0) {
					S_PacketBox pb2 = new S_PacketBox(pc,S_PacketBox.ALLIANCE_LIST);
					pc.sendPackets(pb2, true);
				}else {
					return;
				}
			}catch(Exception e){}//오류X
			break;
		case 3://가입
			L1PcInstance allianceLeader = FaceToFace.faceToFace(pc);
			if ( allianceLeader == null) return;
			if (pc.getLevel() < 25 || !pc.isCrown()) {
				pc.sendPackets(new S_ServerMessage(1206));// 25레벨이상 혈맹 군주만 동맹신청을 할 수 있습니다. 또한 연합 군주는 동맹을 맺을 수 없습니다.
				return;
			}
			/*if (pc.getClan().getAlliance() != 0) {
				pc.sendPackets(new S_ServerMessage(1202));// 이미 동맹에 가입된 상태입니다.
				return;
			}*/
			if (clan.getAlliance() > 4) {
				S_SystemMessage sm = new S_SystemMessage(
						"동맹은 4개 혈맹 까지만 가능합니다.");
				pc.sendPackets(sm, true);
				return;
			}
			for (L1War war : L1World.getInstance().getWarList()) {
				if (war.CheckClanInWar(clanname)) {
					pc.sendPackets(new S_ServerMessage(1234)); // 전쟁중에는 동맹에 가입할 수 없습니다.
					return;
				}
			} // 동맹 수 제한(4개혈맹) 추가해야함 // 1201 // 동맹에 가입할 수 없습니다.
			if (allianceLeader != null) {
				if (allianceLeader.getLevel() > 24 && allianceLeader.isCrown()) {
					allianceLeader.setTempID(pc.getId());
					allianceLeader.sendPackets(new S_Message_YN(223, pc.getName()));
				} else {
					pc.sendPackets(new S_ServerMessage(1201));// 동맹에 가입할 수 없습니다.
				}
			}
			break;
		case 4://탈퇴
			for (L1War war : L1World.getInstance().getWarList()) {
				if (war.CheckClanInWar(clanname)) {
					pc.sendPackets(new S_ServerMessage(1203)); // 전쟁중에는 동맹을 탈퇴할 수 없습니다.
					return;
				}
			}
			if (clan.getAlliance() != 0) {
				pc.sendPackets(new S_Message_YN(1210, "")); //정말로 동맹을 탈퇴하시겠습니까? (Y/N)
			} else {
				pc.sendPackets(new S_ServerMessage(1233)); // 동맹이 없습니다.
			}
			break;

		case 5: // 생존의 외침 (CTRL + E)
			if (pc.getWeapon() == null) {
				pc.sendPackets(new S_ServerMessage(1973));
				//무기를 착용해야 사용할수 있습니다.
				return;
			}
			if (pc.get_food() >= 225) { 
				int addHp = 0;
				int gfxId1 = 8683;
				int gfxId2 = 829;
				long curTime = System.currentTimeMillis() / 1000;
				int fullTime = (int) ((curTime - pc.getCryOfSurvivalTime()) / 60);
				if (fullTime < 30) {
					long time = (pc.getCryOfSurvivalTime() + (1*60*30)) - curTime;
					//pc.sendPackets(new S_ServerMessage(1974));
					//생존의 외침: 대기중
					pc.sendPackets(new S_SystemMessage("생존의 외침: " + (time/60) + "분 " + (time%60) + "초 후 사용가능."));
					return;
				}
				int enchant = pc.getWeapon().getEnchantLevel();
				if (enchant>= 0 && enchant <= 6) { 
					gfxId1 = 8684;
					gfxId2 = 8907;
					addHp = 400;
				} else if (enchant == 7 || enchant == 8){
					gfxId1 = 8685;
					gfxId2 = 8909;
					addHp = enchant * 100;
				} else if (enchant == 9 || enchant == 10) {
					gfxId1 = 8773;
					gfxId2 = 8910;
					addHp = enchant * 100;
				} else if (enchant  >= 11) {
					gfxId1 = 8686;
					gfxId2 = 8908;
					addHp = enchant * 100;
				}
				S_SkillSound sound = new S_SkillSound(pc.getId(), gfxId1);
				pc.sendPackets(sound);
				Broadcaster.broadcastPacket(pc, sound);
				sound = new S_SkillSound(pc.getId(), gfxId2);
				pc.sendPackets(sound);
				Broadcaster.broadcastPacket(pc, sound);
				pc.setCryOfSurvivalTime();
				pc.set_food(0);
				pc.sendPackets(new S_PacketBox(S_PacketBox.FOOD, 0));
				pc.setCurrentHp(pc.getCurrentHp() + addHp);
			} else {
				pc.sendPackets(new S_ServerMessage(3461)); 
				//포만감이 부족하여 사용할 수 없습니다.
			}
			break;
		case 6: // 무기 허세 떨기 Alt + 0(숫자)
			if (pc.getWeapon() == null) {
				pc.sendPackets(new S_ServerMessage(1973));
				return;
			}
			int gfx3 = 0;
			weapon = pc.getWeapon();
			int EnchantLevel2 = weapon.getEnchantLevel();
			if (EnchantLevel2 < 0) {
				pc.sendPackets(new S_ServerMessage(79));
				return;
			} else if (EnchantLevel2 >= 0 && EnchantLevel2 <= 6) {
				gfx3 = 8684;
			} else if (EnchantLevel2 >= 7 && EnchantLevel2 <= 8) {
				gfx3 = 8685;
			} else if (EnchantLevel2 >= 9 && EnchantLevel2 <= 10) {
				gfx3 = 8773;
			} else if (EnchantLevel2 >= 11) {
				gfx3 = 8686;
			}
			pc.sendPackets(new S_SkillSound(pc.getId(), gfx3));
			pc.broadcastPacket(new S_SkillSound(pc.getId(), gfx3));
			break;
		case 8:		
			/** 입장시간 표기 **/
			int getTimer1 = 120 - pc.getGirandungeonTime();// 기란 감옥
			int getTimer2 = 60 - pc.getnewdodungeonTime();// 상아탑:발록 진영
			int getTimer3 = 60 - pc.getOrendungeonTime();// 상아탑:야히 진영
			int getTimer4 = 30 - pc.getSoulTime();// 고대정령무덤
			int getTimer5 = 30 - pc.geticedungeonTime();// 얼음 던전 PC
			int getTimer6 = 30 - pc.getSomeTime(); // 몽환의 섬
			//int getTimer7 = 120 - pc.getRadungeonTime(); // 라스타바드 던전
			int getTimer8 = 120 - pc.getDrageonTime();// 용의 계곡 던전
			int getTimer9 = 120 - pc.getislandTime();// 말하는섬던전
							/** 입장시간 표기 **/
			pc.sendPackets(new S_ServerMessage(2535, "$12125",  getTimer1 +"")); // 기란 감옥
			pc.sendPackets(new S_ServerMessage(2535, "$6081", getTimer2 +"")); // 상아탑:발록 진영
			pc.sendPackets(new S_ServerMessage(2535, "$13527",  getTimer3 +"")); // 상아탑:야히 진영 PC
			pc.sendPackets(new S_ServerMessage(2535, "고대정령의무덤(PC)", getTimer4 +"")); // 고대정령무덤
			pc.sendPackets(new S_ServerMessage(2535, "얼음수정동굴(PC)",  getTimer5 +"")); // 얼음 던전 PC
			pc.sendPackets(new S_ServerMessage(2535, "몽환의섬", getTimer6 +"")); // 몽환의 섬
			//pc.sendPackets(new S_ServerMessage(2535, "$12126",  getTimer7 +"")); // 라스타바드 던전
			pc.sendPackets(new S_ServerMessage(2535, "$14250",  getTimer8 +"")); // 용의 계곡 던전
			pc.sendPackets(new S_ServerMessage(2535, "말하는섬 던전",  getTimer9 +"")); // 말하는섬던전
			break;
		case 9:				/** 리스창표기 **/
			int setTimer1 = 180 - pc.getGirandungeonTime();//기감
			int setTimer2 = 60 - pc.getnewdodungeonTime();//발록진영
			int setTimer3 = 60 - pc.getOrendungeonTime();//야히진영
			int setTimer4 = 30 - pc.getSoulTime();//고대정령무덤
			pc.sendPackets(new S_PacketBox(S_PacketBox.DungeonTime, setTimer1, setTimer2,setTimer3,setTimer4));
			break;
		default:
			break;
		}		
	}


	@Override
	public String getType() {
		return C_RANK;
	}
}
