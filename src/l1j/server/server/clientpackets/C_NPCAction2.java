package l1j.server.server.clientpackets;

import static l1j.server.server.model.skill.L1SkillId.STATUS_UNDERWATER_BREATH;

import java.util.List;
import java.util.Random;

import l1j.server.Config;
import l1j.server.IndunSystem.FanstasyIsland.FantasyIslandSystem;
import l1j.server.IndunSystem.Orim.OrimController;
import l1j.server.IndunSystem.Training.BossTrainingSystem;
import l1j.server.IndunSystem.ValakasRoom.ValakasReadyStart;
import l1j.server.IndunSystem.ValakasRoom.ValakasRoomSystem;
import l1j.server.server.ActionCodes;
import l1j.server.server.Controller.DevilController;
import l1j.server.server.Controller.WarTimeController;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.KeyTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1EffectSpawn;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Party;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1Quest;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1War;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1CataInstance;
import l1j.server.server.model.Instance.L1EffectInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_EffectLocation;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ReturnedStat;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillIconBlessOfEva;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.L1SpawnUtil;

public class C_NPCAction2 {

	private static C_NPCAction2 _instance;

	private static Random _random = new Random(System.nanoTime());

	public static C_NPCAction2 getInstance() {
		if (_instance == null) {
			_instance = new C_NPCAction2();
		}
		return _instance;
	}


	int[] materials = null;
	int[] counts = null;

	public String NpcAction(L1PcInstance pc, L1Object obj, String s, String htmlid) {
		int npcid = ((L1NpcInstance) obj).getNpcTemplate().get_npcId();
		try {
			
		if (npcid == 200201) {// 조우의 돌골렘
			if (s.equalsIgnoreCase("A")) {
				if (pc.getInventory().checkEnchantItem(5, 7, 1) && pc.getInventory().checkEnchantItem(6, 7, 1)
					&& pc.getInventory().checkItem(41246, 30000)) {
					pc.getInventory().consumeEnchantItem(5, 7, 1);
					pc.getInventory().consumeEnchantItem(6, 7, 1);
					pc.getInventory().consumeItem(41246, 3000);

					pc.getInventory().storeItem(602, 1);
					htmlid = "joegolem9";
				} else {
					htmlid = "joegolem15";
				}
			}
			// 광풍의 도끼
			if (s.equalsIgnoreCase("B")) {
				if (pc.getInventory().checkEnchantItem(145, 7, 1) && pc.getInventory().checkEnchantItem(148, 7, 1)
						&& pc.getInventory().checkItem(41246, 30000)) {
					pc.getInventory().consumeEnchantItem(145, 7, 1);
					pc.getInventory().consumeEnchantItem(148, 7, 1);
					pc.getInventory().consumeItem(41246, 30000);

					pc.getInventory().storeItem(605, 1);
					htmlid = "joegolem10";
				} else {
					htmlid = "joegolem15";
				}
			}
			// 파멸의 대검
			if (s.equalsIgnoreCase("C")) {
				if (pc.getInventory().checkEnchantItem(52, 7, 1) && pc.getInventory().checkEnchantItem(64, 7, 1)
						&& pc.getInventory().checkItem(41246, 30000)) {
					pc.getInventory().consumeEnchantItem(52, 7, 1);
					pc.getInventory().consumeEnchantItem(64, 7, 1);
					pc.getInventory().consumeItem(41246, 30000);

					pc.getInventory().storeItem(601, 1);
					htmlid = "joegolem11";
				} else {
					htmlid = "joegolem15";
				}
			}
			// 아크메이지의 지팡이
			if (s.equalsIgnoreCase("D")) {
				if (pc.getInventory().checkEnchantItem(125, 7, 1) && pc.getInventory().checkEnchantItem(129, 7, 1)
						&& pc.getInventory().checkItem(41246, 30000)) {
					pc.getInventory().consumeEnchantItem(125, 7, 1);
					pc.getInventory().consumeEnchantItem(129, 7, 1);
					pc.getInventory().consumeItem(41246, 30000);

					pc.getInventory().storeItem(603, 1);
					htmlid = "joegolem12";
				} else {
					htmlid = "joegolem15";
				}
			}
			// 혹한의 창
			if (s.equalsIgnoreCase("E")) {
				if (pc.getInventory().checkEnchantItem(99, 7, 1) && pc.getInventory().checkEnchantItem(104, 7, 1)
						&& pc.getInventory().checkItem(41246, 30000)) {
					pc.getInventory().consumeEnchantItem(99, 7, 1);
					pc.getInventory().consumeEnchantItem(104, 7, 1);
					pc.getInventory().consumeItem(41246, 30000);

					pc.getInventory().storeItem(604, 1);
					htmlid = "joegolem13";
				} else {
					htmlid = "joegolem15";
				}
			}
			// 뇌신검
			if (s.equalsIgnoreCase("F")) {
				if (pc.getInventory().checkEnchantItem(32, 7, 1) && pc.getInventory().checkEnchantItem(42, 7, 1)
						&& pc.getInventory().checkItem(41246, 30000)) {
					pc.getInventory().consumeEnchantItem(32, 7, 1);
					pc.getInventory().consumeEnchantItem(42, 7, 1);
					pc.getInventory().consumeItem(41246, 30000);

					pc.getInventory().storeItem(600, 1);
					htmlid = "joegolem14";
				} else {
					htmlid = "joegolem15";
				}
			}
			
			
		} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 5162) {
			if (s.equals("A")) { // 76부터
				if (pc.getQuest().isEnd(L1Quest.QUEST_SLOT76)) {
					pc.sendPackets(new S_ServerMessage(3255));
					// 해당 슬롯은 이미 확장되었습니다.
				} else {
					if (pc.getInventory().checkItem(40308, 10000000) && pc.getLevel() >= 76) {
						pc.getInventory().consumeItem(40308, 10000000);
						pc.getQuest().set_end(L1Quest.QUEST_SLOT76);
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "slot9"));
						pc.sendPackets(new S_ReturnedStat(S_ReturnedStat.RING_RUNE_SLOT,S_ReturnedStat.SUBTYPE_RING, 1));
					} else {
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "slot6"));
					}
				}
			} else if (s.equals("B")) { // 81부터
				if (pc.getQuest().isEnd(L1Quest.QUEST_SLOT81)) {
					pc.sendPackets(new S_ServerMessage(3255));// 해당 슬롯은 이미 확장되었습니다.
				} else {
					if (pc.getInventory().checkItem(40308, 30000000) && pc.getLevel() >= 81) {
						pc.getInventory().consumeItem(40308, 30000000);
						pc.getQuest().set_end(L1Quest.QUEST_SLOT81);
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "slot9"));
						pc.sendPackets(new S_ReturnedStat(S_ReturnedStat.RING_RUNE_SLOT,S_ReturnedStat.SUBTYPE_RING, 2));
					} else {
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "slot6"));
					}
				}
			} else if (s.equals("D") || s.equals("C")) { // 본섭은 c 임
				if (pc.getQuest().isEnd(L1Quest.QUEST_SLOT59)) {
					pc.sendPackets(new S_ServerMessage(3255));
					// 해당 슬롯은 이미 확장되었습니다.
				} else {
					if (pc.getInventory().checkItem(40308, 2000000) && pc.getLevel() >= 59) {
						pc.getInventory().consumeItem(40308, 2000000);
						pc.sendPackets(new S_ReturnedStat(67, 1, 16));
						pc.getQuest().set_end(L1Quest.QUEST_SLOT59);
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "slot9"));
					} else {
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "slot11"));
					}
				}
			}else if(s.equals("F")){ //70 휘장 
				if (pc.getQuest().isEnd(L1Quest.QUEST_SLOT70)) {
					pc.sendPackets(new S_ServerMessage(3255));
					// 해당 슬롯은 이미 확장되었습니다.
				}else{
					if(pc.getInventory().checkItem(40308,2000000) && pc.getLevel()>=70){
						pc.getInventory().consumeItem(40308,2000000);
						pc.getQuest().set_end(L1Quest.QUEST_SLOT70);
						pc.sendPackets(new S_ReturnedStat(S_ReturnedStat.RING_RUNE_SLOT,S_ReturnedStat.SUBTYPE_RING,128));
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(),"slot9"));
					}else {
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "slot6"));
					}
				}
			}else if(s.equals("E")){ //83 견갑 
				if (pc.getQuest().isEnd(L1Quest.QUEST_SLOT83)) {
					pc.sendPackets(new S_ServerMessage(3255));
					// 해당 슬롯은 이미 확장되었습니다.
				}else{
					if(pc.getInventory().checkItem(40308,30000000) && pc.getLevel()>=83){
						pc.getInventory().consumeItem(40308,30000000);
						pc.getQuest().set_end(L1Quest.QUEST_SLOT83);
						pc.sendPackets(new S_ReturnedStat(S_ReturnedStat.RING_RUNE_SLOT,S_ReturnedStat.SUBTYPE_RING,64));
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(),"slot9"));
					}else{
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "slot6"));
					}
				}
			}

			// 안톤
		}else if (npcid == 7000079) {/** 카이저 */
			if (s.equalsIgnoreCase("1")){ //대여
				int countActiveMaps = BossTrainingSystem.getInstance().countRaidPotal();
				if (pc.getInventory().checkItem(80500)) {
					htmlid = "bosskey6";
					//이미 훈련소 열쇠를 가지고 계신 것 같군요.
					//많은 분들께서 이용하실 수 있도록 훈련소는 한 사람 당 하나 씩만 대여해 드리고 있습니다.
				} else if (countActiveMaps >= 99) {
					htmlid = "bosskey3";
					//죄송합니다.
					//지금은 모든 훈련소에서 훈련이 진행 중 입니다.
				} else {
					htmlid = "bosskey4";
				}
			} else if (s.matches("[2-4]")){
				if (!pc.getInventory().checkItem(80500)) { //액션 조작 방지
					L1ItemInstance item = null;
					int count = 0;
					if (s.equalsIgnoreCase("2")){ //4개
						count = 4;
					} else if (s.equalsIgnoreCase("3")){ //8개
						count = 8;
					} else if (s.equalsIgnoreCase("4")){ //16개
						count = 16;
					}
					if (pc.getInventory().consumeItem(40308, count * 300)) {
						int id = BossTrainingSystem.getInstance().blankMapId();
						BossTrainingSystem.getInstance().startRaid(pc, id);
						for (int i = 0; i < count; i++) {
							item = pc.getInventory().storeItem(80500, 1);
							item.setKeyId(id);
							if (KeyTable.checkey(item)) {
								KeyTable.DeleteKey(item);
								KeyTable.StoreKey(item);
							} else {
								KeyTable.StoreKey(item);
							}
						}
						htmlid = "bosskey7";
						// 같이 훈련을 받으실 분들에게 열쇠를 나누어 주신 다음 저에게 보여주시면 훈련소로 안내해 드리겠습니다.
						//훈련소의 대여시간은 최대 4시간이며, 훈련 중이라 해도 대여 시간이 종료되면 다음 사람을 위해 훈련소 사용이 중지됩니다.
						//훈련용 몬스터를 소환하실 때에는 항상 훈련소의 남은 사용 시간을 확인하시기 바랍니다.
					} else {
						htmlid = "bosskey5";
						//죄송하지만, 사용료를 지불하지 않으시면 훈련소를 빌려드릴 수 없습니다.
						//아덴 왕국의 지원금만으로 이 많은 훈련소를 관리하는 것이 쉬운 일은 아니라서요.
					}
				} else {
					htmlid = "bosskey6";
					//이미 훈련소 열쇠를 가지고 계신 것 같군요.
					//많은 분들께서 이용하실 수 있도록 훈련소는 한 사람 당 하나 씩만 대여해 드리고 있습니다.
				}
			} else if (s.equalsIgnoreCase("6")){ //입장
				int countActiveMaps = BossTrainingSystem.getInstance().countRaidPotal();
				if (countActiveMaps < 100) {
					L1ItemInstance item = pc.getInventory().findItemId(80500);
					if (item != null) {
						int id = item.getKeyId();
						new L1Teleport().teleport(pc, 32901, 32814, (short) id, 0, true);
					} else {
						htmlid = "bosskey2";
						//훈련소 열쇠를 가지고 있지 않으신 것 같네요.
						//먼저 훈련소를 대여하신 후에 사용하실 수 있습니다.
					}
				} else {
					htmlid = "bosskey3";
					// 죄송합니다.
					//지금은 모든 훈련소에서 훈련이 진행 중 입니다.
				}
			}
		} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7200022) {//생일도우미
	         L1NpcInstance npc = (L1NpcInstance) obj;
	         if (pc.isInvisble()) {
	             pc.sendPackets(new S_NpcChatPacket(npc,"투명 상태에서 받을 수 없습니다.", 0));
	             return htmlid;
	         }
	         if (s.equalsIgnoreCase("a")) {
//	        	 if (pc.getInventory().checkItem(3000046, 1) || pc.getInventory().checkItem(3000048, 1)) {
	        		 pc.sendPackets(new S_NpcChatPacket(npc,"아직 생일이 아니잖아요!!", 0));
	        	/* } else {
	        		 pc.getInventory().storeItem(3000046, 1);
	        		 htmlid = "birthday4";
	        	 }*/
	         }
	         if (s.equalsIgnoreCase("b")) {
	        	 if (pc.getInventory().consumeItem(3000048, 1)) {
	        		 new L1SkillUse().handleCommands(pc,L1SkillId.COMA_B, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_SPELLSC);
	        		 htmlid = "birthday4";
	        	 } else { // 재료가 부족한 경우
	        		 pc.sendPackets(new S_NpcChatPacket(npc,"꼬마 요정의 마음이 필요합니다.", 0));
	        	 }
	         }
		/** 세실리아 */
		} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7000080) {
				L1NpcInstance npc = (L1NpcInstance) obj;
				if (s.equalsIgnoreCase("A")) {// 상아탑몬스터
					if (pc.getInventory().checkItem(80466)) {
						pc.getInventory().consumeItem(80466, 1);
						L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId() , 900076, 0, 3600 * 1000 , 0);
						pc.sendPackets(new S_NpcChatPacket(npc, "'하딘의분신'이 중앙에 소환되었습니다.", 0));
					} else {
						htmlid = "bosskey10";
					}
				}
				if (s.equalsIgnoreCase("B")) {// 상아탑몬스터
					if (pc.getInventory().checkItem(80467)) {
						pc.getInventory().consumeItem(80467, 1);
						L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 900070, 0, 3600 * 1000 , 0);
						pc.sendPackets(new S_NpcChatPacket(npc, "'흑마법사'가 중앙에 소환되었습니다.", 0));
					} else {
						htmlid = "bosskey10";
					}
				}
				if (s.equalsIgnoreCase("C")) {// 상아탑몬스터
					if (pc.getInventory().checkItem(80450)) {
						pc.getInventory().consumeItem(80450, 1);
						L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 45649, 0, 3600 * 1000 , 0);
						pc.sendPackets(new S_NpcChatPacket(npc, "'데몬'이 중앙에 소환되었습니다.", 0));
					} else {
						htmlid = "bosskey10";
					}
				}
				if (s.equalsIgnoreCase("D")) {// 상아탑몬스터
					if (pc.getInventory().checkItem(80451)) {
						pc.getInventory().consumeItem(80451, 1);
						L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 45685, 0, 3600 * 1000 , 0);
						pc.sendPackets(new S_NpcChatPacket(npc, "상아탑 최종보스 '타락'이 중앙에 소환되었습니다.", 0));
					} else {
						htmlid = "bosskey10";
					}
				}
				/** 라스타바드 몬스터 **/
				if (s.equalsIgnoreCase("E")) {
					if (pc.getInventory().checkItem(80452)) {
						pc.getInventory().consumeItem(80452, 1);
						L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 45955, 0, 3600 * 1000 , 0);
						pc.sendPackets(new S_NpcChatPacket(npc, "'케이나'가 중앙에 소환되었습니다.", 0));
					} else {
						htmlid = "bosskey10";
					}
				}
				if (s.equalsIgnoreCase("F")) {
					if (pc.getInventory().checkItem(80453)) {
						pc.getInventory().consumeItem(80453, 1);
						L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 45959, 0, 3600 * 1000 , 0);
						pc.sendPackets(new S_NpcChatPacket(npc, "'이데아'가 중앙에 소환되었습니다.", 0));
					} else {
						htmlid = "bosskey10";
					}
				}
				if (s.equalsIgnoreCase("G")) {
					if (pc.getInventory().checkItem(80454)) {
						pc.getInventory().consumeItem(80454, 1);
						L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 45956, 0, 3600 * 1000 , 0);
							pc.sendPackets(new S_NpcChatPacket(npc, "'비아타스'가 중앙에 소환되었습니다.", 0));
					} else {
						htmlid = "bosskey10";
					}
				}
				if (s.equalsIgnoreCase("H")) {
					if (pc.getInventory().checkItem(80455)) {
						pc.getInventory().consumeItem(80455, 1);
						L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 45957, 0, 3600 * 1000 , 0);
						pc.sendPackets(new S_NpcChatPacket(npc, "'바로메스'가 중앙에 소환되었습니다.", 0));
					} else {
						htmlid = "bosskey10";
					}
				}
				if (s.equalsIgnoreCase("I")) {
					if (pc.getInventory().checkItem(80456)) {
						pc.getInventory().consumeItem(80456, 1);
						L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 45960, 0, 3600 * 1000 , 0);
						pc.sendPackets(new S_NpcChatPacket(npc, "'티아메스'가 중앙에 소환되었습니다.", 0));
					} else {
						htmlid = "bosskey10";
					}
				}
				if (s.equalsIgnoreCase("J")) {
					if (pc.getInventory().checkItem(80457)) {
						pc.getInventory().consumeItem(80457, 1);
						L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 45958, 0, 3600 * 1000 , 0);
						pc.sendPackets(new S_NpcChatPacket(npc, "'엔디아스'가 중앙에 소환되었습니다.", 0));
					} else {
						htmlid = "bosskey10";
					}
				}
				if (s.equalsIgnoreCase("K")) {
					if (pc.getInventory().checkItem(80458)) {
						pc.getInventory().consumeItem(80458, 1);
						L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 45961, 0, 3600 * 1000 , 0);
						pc.sendPackets(new S_NpcChatPacket(npc, "'라미아스'가 중앙에 소환되었습니다.", 0));
					} else {
						htmlid = "bosskey10";
					}
				}
				if (s.equalsIgnoreCase("L")) {
					if (pc.getInventory().checkItem(80459)) {
						pc.getInventory().consumeItem(80459, 1);
						L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 45962, 0, 3600 * 1000 , 0);
						pc.sendPackets(new S_NpcChatPacket(npc, "'바로드'가 중앙에 소환되었습니다.", 0));
					} else {
						htmlid = "bosskey10";
					}
				}
				if (s.equalsIgnoreCase("M")) {
					if (pc.getInventory().checkItem(80460)) {
						pc.getInventory().consumeItem(80460, 1);
						L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 45676, 0, 3600 * 1000 , 0);
						pc.sendPackets(new S_NpcChatPacket(npc, "'헬바인'이 중앙에 소환되었습니다.", 0));
					} else {
						htmlid = "bosskey10";
					}
				}
				if (s.equalsIgnoreCase("N")) {
					if (pc.getInventory().checkItem(80461)) {
						pc.getInventory().consumeItem(80461, 1);
						L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 45677, 0, 3600 * 1000 , 0);
						pc.sendPackets(new S_NpcChatPacket(npc, "'라이아'가 중앙에 소환되었습니다.", 0));
					} else {
						htmlid = "bosskey10";
					}
				}
				if (s.equalsIgnoreCase("O")) {
					if (pc.getInventory().checkItem(80462)) {
						pc.getInventory().consumeItem(80462, 1);
						L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 45844, 0, 3600 * 1000 , 0);
						pc.sendPackets(new S_NpcChatPacket(npc, "'바란카'가 중앙에 소환되었습니다.", 0));
					} else {
						htmlid = "bosskey10";
					}
				}
				if (s.equalsIgnoreCase("P")) {
					if (pc.getInventory().checkItem(80463)) {
						pc.getInventory().consumeItem(80463, 1);
						L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 45648, 0, 3600 * 1000 , 0);
						pc.sendPackets(new S_NpcChatPacket(npc, "라스타바드 최종보스 '슬레이브'가 중앙에 소환되었습니다.", 0));
					} else {
						htmlid = "bosskey10";
					}
				}
				/** 글루디오 체크몬스터 **/
				if (s.equalsIgnoreCase("Q")) {
					if (pc.getInventory().checkItem(80464)) {
						pc.getInventory().consumeItem(80464, 1);
						L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 45456, 0, 3600 * 1000 , 0);
						pc.sendPackets(new S_NpcChatPacket(npc, "'네크로맨서'가 중앙에 소환되었습니다.", 0));
					} else {
						htmlid = "bosskey10";
					}
				}
				if (s.equalsIgnoreCase("S")) {
					if (pc.getInventory().checkItem(80465)) {
						pc.getInventory().consumeItem(80465, 1);
						L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 45601, 0, 3600 * 1000 , 0);
						pc.sendPackets(new S_NpcChatPacket(npc, "'데스나이트'가 중앙에 소환되었습니다.", 0));
					} else {
						htmlid = "bosskey10";
					}
				}
				/** 오만의 탑 **/
				if (s.equalsIgnoreCase("T")) {
					if (pc.getInventory().checkItem(80468)) {
						pc.getInventory().consumeItem(80468, 1);
						L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 7310015, 0, 3600 * 1000 , 0);
						pc.sendPackets(new S_NpcChatPacket(npc, "'왜곡의 제니스 퀸'가 중앙에 소환되었습니다.", 0));
					} else {
						htmlid = "bosskey10";
					}
				}
				if (s.equalsIgnoreCase("U")) {
					if (pc.getInventory().checkItem(80469)) {
						pc.getInventory().consumeItem(80469, 1);
						L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 7310021, 0, 3600 * 1000 , 0);
						pc.sendPackets(new S_NpcChatPacket(npc, "'불신의 시어'가 중앙에 소환되었습니다.", 0));
					} else {
						htmlid = "bosskey10";
					}
				}
				if (s.equalsIgnoreCase("V")) {
					if (pc.getInventory().checkItem(80470)) {
						pc.getInventory().consumeItem(80470, 1);
						L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 7310028, 0, 3600 * 1000 , 0);
						pc.sendPackets(new S_NpcChatPacket(npc, "'공포의 뱀파이어'가 중앙에 소환되었습니다.", 0));
					} else {
						htmlid = "bosskey10";
					}
				}
				if (s.equalsIgnoreCase("W")) {
					if (pc.getInventory().checkItem(80471)) {
						pc.getInventory().consumeItem(80471, 1);
						L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 7310034, 0, 3600 * 1000 , 0);
						pc.sendPackets(new S_NpcChatPacket(npc, "'죽음의 좀비 로드'가 중앙에 소환되었습니다.", 0));
					} else {
						htmlid = "bosskey10";
					}
				}
				if (s.equalsIgnoreCase("X")) {
					if (pc.getInventory().checkItem(80472)) {
						pc.getInventory().consumeItem(80472, 1);
						L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 7310041, 0, 3600 * 1000 , 0);
						pc.sendPackets(new S_NpcChatPacket(npc, "'지옥의 쿠거'가 중앙에 소환되었습니다.", 0));
					} else {
						htmlid = "bosskey10";
					}
				}
				if (s.equalsIgnoreCase("Y")) {
					if (pc.getInventory().checkItem(80473)) {
						pc.getInventory().consumeItem(80473, 1);
						L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 7310046, 0, 3600 * 1000 , 0);
						pc.sendPackets(new S_NpcChatPacket(npc, "'불사의 머미 로드'가 중앙에 소환되었습니다.", 0));
					} else {
						htmlid = "bosskey10";
					}
				}
				if (s.equalsIgnoreCase("Z")) {
					if (pc.getInventory().checkItem(80474)) {
						pc.getInventory().consumeItem(80474, 1);
						L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 7310051, 0, 3600 * 1000 , 0);
						pc.sendPackets(new S_NpcChatPacket(npc, "'잔혹한 아이리스'가 중앙에 소환되었습니다.", 0));
					} else {
						htmlid = "bosskey10";
					}
				}
				if (s.equalsIgnoreCase("a")) {
					if (pc.getInventory().checkItem(80475)) {
						pc.getInventory().consumeItem(80475, 1);
						L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 7310056, 0, 3600 * 1000 , 0);
						pc.sendPackets(new S_NpcChatPacket(npc, "'어둠의 나이트 발드'가 중앙에 소환되었습니다.", 0));
					} else {
						htmlid = "bosskey10";
					}
				}
				if (s.equalsIgnoreCase("b")) {
					if (pc.getInventory().checkItem(80476)) {
						pc.getInventory().consumeItem(80476, 1);
						L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 7310061, 0, 3600 * 1000 , 0);
						pc.sendPackets(new S_NpcChatPacket(npc, "'불멸의 리치'가 중앙에 소환되었습니다.", 0));
					} else {
						htmlid = "bosskey10";
					}
				}
				if (s.equalsIgnoreCase("c")) {
						if (pc.getInventory().checkItem(80477)) {
							pc.getInventory().consumeItem(80477, 1);
						L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 7310077, 0, 3600 * 1000 , 0);
						pc.sendPackets(new S_NpcChatPacket(npc, "'사신 그림 리퍼'가 중앙에 소환되었습니다.", 0));
					} else {
						htmlid = "bosskey10";
					}
				}
				if (s.equalsIgnoreCase("d")) {
					if (pc.getInventory().checkItem(80478)) {
						pc.getInventory().consumeItem(80478, 1);
						L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 45600, 0, 3600 * 1000 , 0);
						pc.sendPackets(new S_NpcChatPacket(npc, "'흑기사 대장 커츠'가 중앙에 소환되었습니다.", 0));
					} else {
						htmlid = "bosskey10";
					}
				}
				if (s.equalsIgnoreCase("e")) { //바포메트
					if (pc.getInventory().checkItem(80479)) {
						pc.getInventory().consumeItem(80479, 1);
						L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 45573, 0, 3600 * 1000 , 0);
						pc.sendPackets(new S_NpcChatPacket(npc, "'바포메트'가 중앙에 소환되었습니다.", 0));
					} else {
						htmlid = "bosskey10";
					}
				}
				/** START **/
				
				//낡은책더미
		} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7210050) {
			if (s.equalsIgnoreCase("a")) {
				if (pc.getInventory().checkItem(60032)) {
					pc.sendPackets(new S_ChatPacket(pc, "이미 낡은 고서를 가지고있네요"));
					htmlid = "";
				} else {
					pc.getInventory().storeItem(60032, 1);
					htmlid = "oldbook2";
				}
			}
				
				//슈콘,슈고
			} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7210047
					|| ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7210048) {
				if (s.equalsIgnoreCase("a")){
					if (pc.getSoulTime() < 29) {
//						pc.setSkillEffect(L1SkillId.ABSOLUTE_BARRIER, 3000);
//						new L1Teleport().teleport(pc, 32773, 32860, (short) 400, 5, true);
						htmlid = "";
						pc.sendPackets(new S_ChatPacket(pc, "알림: 2015. 10. 14. 업데이트 이후 폐쇄 되었습니다."));
					} else {
						htmlid = "GiantTomb_1";
					}
				}			
				
			} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 6000014) {//맘몬NPC 멘트설정
				if (s.equalsIgnoreCase("a")){
					} else {
						htmlid = "GiantTomb_1";
						pc.sendPackets(new S_ChatPacket(pc, "알림: 2015. 10. 14. 업데이트 이후 폐쇄 되었습니다."));
					}
				
				//용의전령
			} else if (((L1NpcInstance)obj).getNpcTemplate().get_npcId() == 1000002) { 
				if (s.equalsIgnoreCase("1")){
					if (pc.getDrageonTime() >= 120){
						pc.sendPackets(new S_ChatPacket(pc, "시스템: 용의 던전 시간이 만료 되었습니다."));
						return htmlid;
					}
					if (pc.getLevel() >= Config.용던입장레벨 & pc.getLevel() <= Config.용던제한레벨) {//해당레벨 75~85
						pc.setSkillEffect(L1SkillId.ABSOLUTE_BARRIER, 5000);
						new L1Teleport().teleport(pc, 32770, 32759, (short) 30, 5, true);
						htmlid = "";
					} else {
						htmlid = "dvdgate2";
						pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,"\\fQ지룡의 던전: \\f3[Lv."+ Config.용던입장레벨 +"~ "+Config.용던제한레벨+"]\\fQ 까지 입장 허용 레벨입니다."));
						}
					}
				
				//수룡의던전
			} else if (((L1NpcInstance)obj).getNpcTemplate().get_npcId() == 7210000) { 
				if (s.equalsIgnoreCase("1")){
					if (pc.getDrageonTime() >= 120){
						pc.sendPackets(new S_ChatPacket(pc, "용의 던전 시간이 만료 되었습니다."));
						return htmlid;
					}
					if (pc.getLevel() >= Config.수던입장레벨 & pc.getLevel() <= Config.수던제한레벨) {
						pc.setSkillEffect(L1SkillId.ABSOLUTE_BARRIER, 5000);
						new L1Teleport().teleport(pc, 32774, 32805, (short) 814, 5, true);
						htmlid = "";
					} else {
						htmlid = "newbieddw2";
						pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,"\\fQ수룡의 던전: \\f3[Lv."+ Config.수던입장레벨 +"~ "+ Config.수던제한레벨 +"]\\fQ 까지 입장 허용 레벨입니다."));
					}
				}

				
			} else if (((L1NpcInstance)obj).getNpcTemplate().get_npcId() == 7310089) { //마법사 아도니스
				if (s.equalsIgnoreCase("1")){
					if (pc.getnewdodungeonTime() >= 60){
						pc.sendPackets(new S_SystemMessage("\\aA경고: \\aG[발록진영]\\aA 던전 시간이 만료되었습니다."));
						return htmlid;
					}
					if (pc.getLevel() >= 1 & pc.getLevel() <= 99) {
						pc.setSkillEffect(L1SkillId.ABSOLUTE_BARRIER, 5000);
						new L1Teleport().teleport(pc, 32901, 32765, (short) 280, 5, true);
						htmlid = "";
					} else {
						htmlid = "newbieddw2";
					}
				}
				
			} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7310088) { // 피터리뉴얼
				if (s.equalsIgnoreCase("1")) {
					pc.setSkillEffect(L1SkillId.ABSOLUTE_BARRIER, 5000);
					new L1Teleport().teleport(pc, 32770, 32826, (short) 75, 5, true);
					htmlid = "";
				}
				if (s.equalsIgnoreCase("2")) {
					pc.setSkillEffect(L1SkillId.ABSOLUTE_BARRIER, 5000);
					new L1Teleport().teleport(pc, 32772, 32823, (short) 76, 5, true);
					htmlid = "";
				}
				if (s.equalsIgnoreCase("3")) {
					pc.setSkillEffect(L1SkillId.ABSOLUTE_BARRIER, 5000);
					new L1Teleport().teleport(pc, 32762, 32839, (short) 77, 5, true);
					htmlid = "";
				}
				if (s.equalsIgnoreCase("4")) {
					if (pc.getnewdodungeonTime() >= 60) {
						pc.sendPackets(new S_SystemMessage("던전 시간이 만료되었습니다."));
						return htmlid;
					}
					if (pc.getLevel() >= 1 & pc.getLevel() <= 99) {
						pc.setSkillEffect(L1SkillId.ABSOLUTE_BARRIER, 5000);
						new L1Teleport().teleport(pc, 32901, 32765, (short) 280, 5, true);
						htmlid = "";
					} else 
						htmlid = "newbieddw2";
					//pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,"\\fQ[알림]: \\f3[Lv.62~이상]\\fQ 적절 사냥터 입니다."));
					//pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,"\\aA[알림]: 현재 이동하신 '발록진영'사냥터는 \\aG[아데나]\\aA 획득량이 높습니다."));					}
				}
				if (s.equalsIgnoreCase("7")) {
						if (pc.getnewdodungeonTime() >= 60) {
							pc.sendPackets(new S_SystemMessage("던전 시간이 만료되었습니다."));
							return htmlid;
						}
						if (pc.getLevel() >= 1 & pc.getLevel() <= 99) {
							pc.setSkillEffect(L1SkillId.ABSOLUTE_BARRIER, 5000);
							new L1Teleport().teleport(pc, 32738, 32795, (short) 283, 5, true);
							htmlid = "";
						} else {
							htmlid = "newbieddw2";
						}
				}
//				}
				
			} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 81026666) {//마법사 리드
				if (pc.getLevel() < Config.FG_ISVAL) {
				if (s.equals("control")) {//액션코드
				if (pc.getInventory().checkItem(40308, 10000)) {
					pc.getInventory().consumeItem(40308, 10000);
					if (pc.getGirandungeonTime() >= 120) {
						pc.sendPackets(new S_SystemMessage("던전 시간이 만료되었습니다."));
						return htmlid;
					} else {
						new L1Teleport().teleport(pc, 32835 + _random.nextInt(5), 32796 + _random.nextInt(2), (short) 15403, pc.getHeading(), true);
					}
				} else {
					pc.sendPackets(new S_SystemMessage("아데나(10,000) 부족합니다."));
				}
			}
				}else {
					pc.sendPackets(new S_SystemMessage("입장 불가: 레벨이 맞지 않음 (" + Config.FG_ISVAL + " 레벨 이상"));
				}
				
				//햄
			}else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7000020) {//기란 감옥 멀린 리뉴얼
				if (s.equals("D_giran")) {//액션코드
				if (pc.getInventory().checkItem(40308, 20000)) {
					pc.getInventory().consumeItem(40308, 20000);
					if (pc.getGirandungeonTime() >= 120) {
						pc.sendPackets(new S_SystemMessage("던전 시간이 만료되었습니다."));
						return htmlid;
					} else {
						new L1Teleport().teleport(pc, 32806, 32732, (short) 53, pc.getHeading(), true);
					}
				} else {
					pc.sendPackets(new S_SystemMessage("시스템: 아데나(20,000) 부족합니다."));
				}
			}
				//햄
			} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7210007) {
				if (s.equalsIgnoreCase("a")) {
					if (pc.getLevel() >= 60) {
						L1Quest quest = pc.getQuest();
						int questStep = quest.get_step(L1Quest.QUEST_HAMO);
						if (!pc.getInventory().checkItem(820000) && questStep != L1Quest.QUEST_END) {
							pc.getQuest().set_end(L1Quest.QUEST_HAMO); 
							pc.getInventory().storeItem(820000, 1);//햄의주머니
							htmlid = "";
						} else {
							htmlid = "hamo1";
						}
					} else {
						htmlid = "hamo3";
						pc.sendPackets(new S_SystemMessage("60이상의 캐릭터만 받을 수 있습니다."));
					}
				}
				//엘드나스
			} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7210008) {
				if (s.equalsIgnoreCase("a")) {
					if (pc.getLevel() >= 60) {
						if (pc.getInventory().consumeItem(820001, 1)) {//냉한의기운
							ValakasReadyStart.getInstance().startReady(pc);
						} else {
							htmlid = "eldnas1";
						}
					} else {
						htmlid = "eldnas3";
					}
				}
				//진데스나이트
			} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7210009) {
				if (s.equalsIgnoreCase("enter")) {
					if (pc.getLevel() >= 60) {
						if (!pc.getInventory().checkItem(203003, 1)) {//데스나이트의 불검:진
							pc.getInventory().storeItem(203003, 1);// 데스나이트의 불검:진
							ValakasRoomSystem.getInstance().startRaid(pc);
						}
					} else {
						htmlid = "fd_death2";
					}
				}
				
				/** 깃털마을 피아르 **/
			} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7310093) {				
				if (s.equalsIgnoreCase("a")) {
					if (!pc.PC방_버프) {
						pc.sendPackets(new S_SystemMessage("PC방 이용권을 사용중에만 사용 가능한 행동입니다."));
						htmlid = "pc_tell2";
						return htmlid;
					}
/*					if (pc.getInventory().checkItem(41159, 30)) {
						pc.getInventory().consumeItem(41159, 30);*/
						
						if (pc.getMap().isEscapable() || pc.isGm()) {
							int rx = _random.nextInt(7);
							int ux = 32768 + rx;
							int uy = 32834 + rx; // 상아탑
								new L1Teleport().teleport(pc, ux, uy, (short) 622, pc.getHeading(), true);
							}
							pc.setSkillEffect(L1SkillId.ABSOLUTE_BARRIER, 3000);	
					}
//				}
				
							/** 깃털마을 정령의오브 **/
			} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7210041) {
				if (s.equalsIgnoreCase("a")) { //정무 입구
					if (pc.getSoulTime() > 29){
						pc.sendPackets(new S_ChatPacket(pc, "고대 무덤의 시간이 만료 되었습니다."));
						return htmlid;
					}
					if (pc.PC방_버프) {
						new L1Teleport().teleport(pc, 32902, 32811, (short) 430, 5, true);
					} else {
						pc.sendPackets(new S_SystemMessage("PC방 이용권을 사용중에만 사용 가능한 행동입니다."));
					}
				}
				if (s.equalsIgnoreCase("b")) { //정무 중앙
					if (pc.getSoulTime() > 29){
						pc.sendPackets(new S_ChatPacket(pc, "고대 무덤의 시간이 만료 되었습니다."));
						return htmlid;
					}
					if (pc.PC방_버프) {
						new L1Teleport().teleport(pc, 32869, 32876, (short) 430, 5, true);
					} else {
						pc.sendPackets(new S_SystemMessage("PC방 이용권을 사용중에만 사용 가능한 행동입니다."));
					}
				}
				if (s.equalsIgnoreCase("c")) { //상아탑:야히진영 4층
					if (pc.getOrendungeonTime() > 59){
						pc.sendPackets(new S_SystemMessage("던전 시간이 만료되었습니다."));
						return htmlid;
					}
					if (pc.getInventory().checkItem(40308, 14000)) {
						pc.getInventory().consumeItem(40308, 14000);
						new L1Teleport().teleport(pc, 32899, 32766, (short) 285, 0, true);
					} else {
						pc.sendPackets(new S_SystemMessage("14,000 아데나가 필요합니다."));
					}
				}
				if (s.equalsIgnoreCase("d")) { //상아탑 5층
					if (pc.getOrendungeonTime() > 59){
						pc.sendPackets(new S_SystemMessage("던전 시간이 만료되었습니다."));
						return htmlid;
					}
					if (pc.getInventory().checkItem(40308, 14000)) {
						pc.getInventory().consumeItem(40308, 14000);
						new L1Teleport().teleport(pc, 32773, 32833, (short) 286, 0, true);
					} else {
						pc.sendPackets(new S_SystemMessage("던전 시간이 만료되었습니다."));
					}
				}
				if (s.equalsIgnoreCase("e")) { //상아탑 6층
					if (pc.getOrendungeonTime() > 59){
						pc.sendPackets(new S_SystemMessage("던전 시간이 만료되었습니다."));
						return htmlid;
					}
					if (pc.getInventory().checkItem(40308, 14000)) {
						pc.getInventory().consumeItem(40308, 14000);
						new L1Teleport().teleport(pc, 32773, 32833, (short) 287, 0, true);
					} else {
						pc.sendPackets(new S_SystemMessage("14,000 아데나가 필요합니다."));
					}
				}
				if (s.equalsIgnoreCase("f")) { //상아탑 7층
					if (pc.getOrendungeonTime() > 59){
						pc.sendPackets(new S_SystemMessage("던전 시간이 만료되었습니다."));
						return htmlid;
					}
					if (pc.getInventory().checkItem(40308, 14000)) {
						pc.getInventory().consumeItem(40308, 14000);
						new L1Teleport().teleport(pc, 32811, 32870, (short) 288, 0, true);		
					} else {
						pc.sendPackets(new S_SystemMessage("14,000 아데나가 필요합니다."));
					}
				}
				if (s.equalsIgnoreCase("g")) { //얼던PC
						if (!pc.PC방_버프) {
							pc.sendPackets(new S_SystemMessage("PC방 이용권을 사용중에만 사용 가능한 행동입니다."));
							return htmlid;
						}
					if (pc.geticedungeonTime() > 29){
						pc.sendPackets(new S_SystemMessage("얼음 수정 동굴(PC) 던전 시간이 만료되었습니다."));
						return htmlid;
					}
					if (pc.getInventory().checkItem(40308, 14000)) {
						pc.getInventory().consumeItem(40308, 14000);
						new L1Teleport().teleport(pc, 32816, 32847, (short) 5555, 0, true);		
					} else {
						pc.sendPackets(new S_SystemMessage("14,000 아데나가 필요합니다."));
						}
					}
				if (s.equalsIgnoreCase("h")) { //오만1층
					if (!pc.PC방_버프) {
						pc.sendPackets(new S_SystemMessage("PC방 이용권을 사용중에만 사용 가능한 행동입니다."));
						return htmlid;
					}
					if (pc.getInventory().checkItem(40308, 14000)) {
						pc.getInventory().consumeItem(40308, 14000);
						new L1Teleport().teleport(pc, 32735, 32798, (short) 101, 0, true);		
					} else {
						pc.sendPackets(new S_SystemMessage("14,000 아데나가 필요합니다."));
						}
					}
				if (s.equalsIgnoreCase("i")) { //오만2층
					if (!pc.PC방_버프) {
						pc.sendPackets(new S_SystemMessage("PC방 이용권을 사용중에만 사용 가능한 행동입니다."));
						return htmlid;
					}
					if (pc.getInventory().checkItem(40308, 14000)) {
						pc.getInventory().consumeItem(40308, 14000);
						new L1Teleport().teleport(pc, 32726, 32803, (short) 102, 0, true);		
					} else {
						pc.sendPackets(new S_SystemMessage("14,000 아데나가 필요합니다."));
						}
					}
				if (s.equalsIgnoreCase("j")) { //오만3층
					if (!pc.PC방_버프) {
						pc.sendPackets(new S_SystemMessage("PC방 이용권을 사용중에만 사용 가능한 행동입니다."));
						return htmlid;
					}
					if (pc.getInventory().checkItem(40308, 14000)) {
						pc.getInventory().consumeItem(40308, 14000);
						new L1Teleport().teleport(pc, 32726, 32803, (short) 103, 0, true);		
					} else {
						pc.sendPackets(new S_SystemMessage("14,000 아데나가 필요합니다."));
						}
					}
				if (s.equalsIgnoreCase("k")) { //오만4층
					if (!pc.PC방_버프) {
						pc.sendPackets(new S_SystemMessage("PC방 이용권을 사용중에만 사용 가능한 행동입니다."));
						return htmlid;
					}
					if (pc.getInventory().checkItem(40308, 14000)) {
						pc.getInventory().consumeItem(40308, 14000);
						new L1Teleport().teleport(pc, 32613, 32863, (short) 104, 0, true);		
					} else {
						pc.sendPackets(new S_SystemMessage("14,000 아데나가 필요합니다."));
						}
					}
				if (s.equalsIgnoreCase("l")) { //오만5층
					if (!pc.PC방_버프) {
						pc.sendPackets(new S_SystemMessage("PC방 이용권을 사용중에만 사용 가능한 행동입니다."));
						return htmlid;
					}
					if (pc.getInventory().checkItem(40308, 14000)) {
						pc.getInventory().consumeItem(40308, 14000);
						new L1Teleport().teleport(pc, 32597, 32867, (short) 105, 0, true);		
					} else {
						pc.sendPackets(new S_SystemMessage("14,000 아데나가 필요합니다."));
						}
					}
				if (s.equalsIgnoreCase("m")) { //오만6층
					if (!pc.PC방_버프) {
						pc.sendPackets(new S_SystemMessage("PC방 이용권을 사용중에만 사용 가능한 행동입니다."));
						return htmlid;
					}
					if (pc.getInventory().checkItem(40308, 14000)) {
						pc.getInventory().consumeItem(40308, 14000);
						new L1Teleport().teleport(pc, 32607, 32865, (short) 106, 0, true);		
					} else {
						pc.sendPackets(new S_SystemMessage("14,000 아데나가 필요합니다."));
						}
					}
				if (s.equalsIgnoreCase("n")) { //오만7층
					if (!pc.PC방_버프) {
						pc.sendPackets(new S_SystemMessage("PC방 이용권을 사용중에만 사용 가능한 행동입니다."));
						return htmlid;
					}
					if (pc.getInventory().checkItem(40308, 14000)) {
						pc.getInventory().consumeItem(40308, 14000);
						new L1Teleport().teleport(pc, 32618, 32866, (short) 107, 0, true);		
					} else {
						pc.sendPackets(new S_SystemMessage("14,000 아데나가 필요합니다."));
						}
					}
				if (s.equalsIgnoreCase("o")) { //오만8층
					if (!pc.PC방_버프) {
						pc.sendPackets(new S_SystemMessage("PC방 이용권을 사용중에만 사용 가능한 행동입니다."));
						return htmlid;
					}
					if (pc.getInventory().checkItem(40308, 14000)) {
						pc.getInventory().consumeItem(40308, 14000);
						new L1Teleport().teleport(pc, 32598, 32867, (short) 108, 0, true);		
					} else {
						pc.sendPackets(new S_SystemMessage("14,000 아데나가 필요합니다."));
						}
					}
				if (s.equalsIgnoreCase("p")) { //오만9층
					if (!pc.PC방_버프) {
						pc.sendPackets(new S_SystemMessage("PC방 이용권을 사용중에만 사용 가능한 행동입니다."));
						return htmlid;
					}
					if (pc.getInventory().checkItem(40308, 14000)) {
						pc.getInventory().consumeItem(40308, 14000);
						new L1Teleport().teleport(pc, 32609, 32866, (short) 109, 0, true);		
					} else {
						pc.sendPackets(new S_SystemMessage("14,000 아데나가 필요합니다."));
						}
					}
				if (s.equalsIgnoreCase("q")) { //오만10층
					if (!pc.PC방_버프) {
						pc.sendPackets(new S_SystemMessage("PC방 이용권을 사용중에만 사용 가능한 행동입니다."));
						return htmlid;
					}
					if (pc.getInventory().checkItem(40308, 14000)) {
						pc.getInventory().consumeItem(40308, 14000);
						new L1Teleport().teleport(pc, 32726, 32803, (short) 110, 0, true);		
					} else {
						pc.sendPackets(new S_SystemMessage("14,000 아데나가 필요합니다."));
						}
					}
				                     /** 마법의 문 **/
			} else if (((L1NpcInstance)obj).getNpcTemplate().get_npcId() == 7310085) { 
				if (s.equalsIgnoreCase("1")){
					if (pc.getislandTime() >= 119){
						pc.sendPackets(new S_ChatPacket(pc, "말하는 섬 시간이 만료 되었습니다."));
						return htmlid;
					}
					if (s.equalsIgnoreCase("1"))
					if (pc.getLevel() > Config.말섬입장레벨 & pc.getLevel() < Config.말섬제한레벨) {//해당레벨
						Random random = new Random(System.nanoTime());
						int ran = random.nextInt(3);
					    new L1Teleport().teleport(pc, 32668 + ran, 32804 + ran, (short) 1, 5, true);
						htmlid = "";
					} else {
						htmlid = "talkinggate2";
						}
					}		
								
			}else if (((L1NpcInstance)obj).getNpcTemplate().get_npcId() == 900135) {//유리에
				L1ItemInstance item = null;
				  L1NpcInstance npc = (L1NpcInstance)obj;
				if (s.equalsIgnoreCase("b")) {	//오림님의 이야기를 듣고 싶어요
					if (!OrimController.getInstance().getInDunOpen()) {
						if ((pc.isInParty()) && (pc.getParty().isLeader(pc))) {
							boolean isInMap = true; // 우선 맵에 있는걸로 선언 후
							for (L1PcInstance player : pc.getParty().getMembers()) {
								if (player.getMapId() != 0) {
									isInMap = false;
									break;
								} else if (!player.getInventory().checkItem(410096, 1)) {
									pc.sendPackets(new S_SystemMessage("파티원의 누군가가 시공의 구슬이 없습니다."));
									player.sendPackets(new S_SystemMessage("파티원의 누군가가 시공의 구슬이 없습니다."));
									return htmlid;
								}
							}
							if (pc.getParty().getNumOfMembers() > 2 && isInMap) {
								pc.getParty().getLeader().getName();
								OrimController Indun = OrimController.getInstance();
								Indun.start();
								L1Party party = pc.getParty();
								L1PcInstance[] players = party.getMembers();
								L1World.getInstance().broadcastPacketToAll(
								new S_PacketBox(84, pc.getParty().getLeader().getName() + " 님이 동료들과 함께 해상던전으로 떠났습니다."));
								L1World.getInstance().broadcastServerMessage("\\aD" + pc.getParty().getLeader().getName() + " 님이 동료들과 함께 해상던전으로 떠났습니다.");
								for (L1PcInstance pc1 : players) {
									Indun.addPlayMember(pc1);
									pc1.getInventory().consumeItem(410096, 1);
									new L1Teleport().teleport(pc1, 32796, 32801, (short)9101, pc1.getHeading(), true);
								}
							} else {
								//htmlid = "id0_1";
								//htmlid = "id0_3";
								pc.sendPackets(new S_NpcChatPacket(npc, "3명~5명의 파티원으로 구성되어야합니다.", 0));
							}
						} else htmlid = "id0_2";
					} else {
						pc.sendPackets(new S_NpcChatPacket(npc, "이미 해상던전으로 선발대가 출발했다네. 잠시후 다시오게.", 0));
						htmlid = "";
					}
				}
				if (s.equalsIgnoreCase("c")) {	//항아리 지급
					if (!pc.getInventory().checkItem(410095, 1)){
						item = pc.getInventory().storeItem(410095, 1);
						pc.sendPackets(new S_ServerMessage(143, "$7918" ,item.getName()));
					} else {
						htmlid = "j_html03"; 
					}
				} else if (s.equalsIgnoreCase("a")) {	//비밀 연구실 텔
					if (pc.getInventory().checkItem(410096, 1) && pc.getInventory().checkItem(L1ItemId.ADENA, 10000)){
						pc.getInventory().consumeItem(410096, 1);
						pc.getInventory().consumeItem(L1ItemId.ADENA, 10000);
						new L1Teleport().teleport(pc, 32744, 32860, (short) 9100, 5, true);
						htmlid = ""; 
					} else {
						htmlid = "j_html02";
					}
				} else if (s.equalsIgnoreCase("d")) { // 일기장 복원
					if (pc.getInventory().checkItem(410097, 1) 
							&& pc.getInventory().checkItem(410098, 1)
							&& pc.getInventory().checkItem(410099, 1) 
							&& pc.getInventory().checkItem(410100, 1)
							&& pc.getInventory().checkItem(410101, 1) 
							&& pc.getInventory().checkItem(410102, 1)
							&& pc.getInventory().checkItem(410103, 1) 
							&& pc.getInventory().checkItem(410104, 1)
							&& pc.getInventory().checkItem(410105, 1) 
							&& pc.getInventory().checkItem(410106, 1)) {
								pc.getInventory().consumeItem(410097, 1);
								pc.getInventory().consumeItem(410098, 1);
								pc.getInventory().consumeItem(410099, 1);
								pc.getInventory().consumeItem(410100, 1);
								pc.getInventory().consumeItem(410101, 1);
								pc.getInventory().consumeItem(410102, 1);
								pc.getInventory().consumeItem(410103, 1);
								pc.getInventory().consumeItem(410104, 1);
								pc.getInventory().consumeItem(410105, 1);
								pc.getInventory().consumeItem(410106, 1);
						htmlid = "j_html04";
						pc.getInventory().storeItem(410107, 1); // 어두운 하딘의 일기장
					} else {
						htmlid = "j_html06";
	                    pc.sendPackets(new S_SystemMessage("10권의 일기가 모두 필요합니다."));
					}
				} else if (s.equalsIgnoreCase("e")) {
		                if (pc.getInventory().checkItem(410144)
		                        && pc.getInventory().checkItem(410145)
		                        && pc.getInventory().checkItem(410146)
		                        && pc.getInventory().checkItem(410147)
		                        && pc.getInventory().checkItem(410148)
		                        && pc.getInventory().checkItem(410149)
		                        && pc.getInventory().checkItem(410150)
		                        && pc.getInventory().checkItem(410151)
		                        && pc.getInventory().checkItem(410152)
		                        && pc.getInventory().checkItem(410153)
		                        && pc.getInventory().checkItem(410154)
		                        && pc.getInventory().checkItem(410155)
		                        && pc.getInventory().checkItem(410156)
		                        && pc.getInventory().checkItem(410157)
		                        && pc.getInventory().checkItem(410158)
		                        && pc.getInventory().checkItem(410159)
		                        && pc.getInventory().checkItem(410160)
		                        && pc.getInventory().checkItem(410161)) {
		                			pc.getInventory().consumeItem(410144, 1);
		                            pc.getInventory().consumeItem(410145, 1);
		                            pc.getInventory().consumeItem(410146, 1);
		                            pc.getInventory().consumeItem(410147, 1);
		                            pc.getInventory().consumeItem(410148, 1);
		                            pc.getInventory().consumeItem(410149, 1);
		                            pc.getInventory().consumeItem(410150, 1);
		                            pc.getInventory().consumeItem(410151, 1);
		                            pc.getInventory().consumeItem(410152, 1);
		                            pc.getInventory().consumeItem(410153, 1);
		                            pc.getInventory().consumeItem(410154, 1);
		                            pc.getInventory().consumeItem(410155, 1);
		                            pc.getInventory().consumeItem(410156, 1);
		                            pc.getInventory().consumeItem(410157, 1);
		                            pc.getInventory().consumeItem(410158, 1);
		                            pc.getInventory().consumeItem(410159, 1);
		                            pc.getInventory().consumeItem(410160, 1);
		                            pc.getInventory().consumeItem(410161, 1);
		    	            htmlid = "j_html04";
		                            pc.getInventory().storeItem(410143, 1);// 오림의 일기장 획득
		                } else {
		                    htmlid = "j_html06"; // 일기장에 대한 정보가 부족.
		                    pc.sendPackets(new S_SystemMessage("18권의 일기가 모두 필요합니다."));
		                }
		            }
			} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71090) {
				if (s.equalsIgnoreCase("a")) {
					htmlid = "";
					final int[] item_ids = { 246, 247, 248, 249, 40660 };
					final int[] item_amounts = { 1, 1, 1, 1, 5 };
					L1ItemInstance item = null;
					for (int i = 0; i < item_ids.length; i++) {
						item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
						pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj).getNpcTemplate().get_name(), item.getItem().getName()));
						pc.getQuest().set_step(L1Quest.QUEST_CRYSTAL, 1);
					}
				} else if (s.equalsIgnoreCase("b")) {
					if (pc.getInventory().checkEquipped(246) || pc.getInventory().checkEquipped(247) || pc.getInventory().checkEquipped(248) || pc.getInventory().checkEquipped(249)) {
						htmlid = "jcrystal5";
					} else if (pc.getInventory().checkItem(40660)) {
						htmlid = "jcrystal4";
					} else {
						pc.getInventory().consumeItem(246, 1);
						pc.getInventory().consumeItem(247, 1);
						pc.getInventory().consumeItem(248, 1);
						pc.getInventory().consumeItem(249, 1);
						pc.getInventory().consumeItem(40620, 1);
						pc.getQuest().set_step(L1Quest.QUEST_CRYSTAL, 2);
						new L1Teleport().teleport(pc, 32801, 32895, (short) 483, 4, true);
					}
				} else if (s.equalsIgnoreCase("c")) {
					if (pc.getInventory().checkEquipped(246) || pc.getInventory().checkEquipped(247) || pc.getInventory().checkEquipped(248) || pc.getInventory().checkEquipped(249)) {
						htmlid = "jcrystal5";
					} else {
						pc.getInventory().checkItem(40660);
						L1ItemInstance l1iteminstance = pc.getInventory().findItemId(40660);
						int sc = l1iteminstance.getCount();
						if (sc > 0) {
							pc.getInventory().consumeItem(40660, sc);
						} else {
						}
						pc.getInventory().consumeItem(246, 1);
						pc.getInventory().consumeItem(247, 1);
						pc.getInventory().consumeItem(248, 1);
						pc.getInventory().consumeItem(249, 1);
						pc.getInventory().consumeItem(40620, 1);
						pc.getQuest().set_step(L1Quest.QUEST_CRYSTAL, 0);
						new L1Teleport().teleport(pc, 32736, 32800, (short) 483, 4, true);
					}
				}
			} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71091) {
				if (s.equalsIgnoreCase("a")) {
					htmlid = "";
					pc.getInventory().consumeItem(40654, 1);
					pc.getQuest().set_step(L1Quest.QUEST_CRYSTAL, L1Quest.QUEST_END);
					new L1Teleport().teleport(pc, 32744, 32927, (short) 483, 4, true);
				}
			} else if (((L1NpcInstance)obj).getNpcTemplate().get_npcId() == 7000055) {
				if (s.equals("A") || s.equals("B") || s.equals("C") || s.equals("D") || s.equals("E")) {
					if (pc.getInventory().checkItem(60034, 1)) {
						pc.getInventory().consumeItem(60034, 1);
						if (s.equals("A")) {
							pc.getInventory().storeItem(60036, 1); // 힘의 엘릭서 룬 주머니
							pc.sendPackets(new S_SystemMessage("힘의 엘릭서 룬을 획득했습니다."));
						} else if (s.equals("B")) {
							pc.getInventory().storeItem(60037, 1); // 민첩의 엘릭서 룬
							pc.sendPackets(new S_SystemMessage("민첩의 엘릭서 룬을 획득했습니다."));
						} else if (s.equals("C")) {
							pc.getInventory().storeItem(60038, 1); // 체력의 엘릭서 룬
							pc.sendPackets(new S_SystemMessage("체력의 엘릭서 룬을 획득했습니다."));
						} else if (s.equals("D")) {
							pc.getInventory().storeItem(60039, 1); // 지식의 엘릭서 룬
							pc.sendPackets(new S_SystemMessage("지식의 엘릭서 룬을 획득했습니다."));
						} else if (s.equals("E")) {
							pc.getInventory().storeItem(60040, 1); // 지혜의 엘릭서 룬
							pc.sendPackets(new S_SystemMessage("지혜의 엘릭서 룬을 획득했습니다."));
						}
					} else {
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "riddle2"));
					}
				}
				
				//네르바 70룬 퀘스트
			} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7210042) {
				if (s.matches("[a-e]")) {
					if (pc.getQuest().isEnd(L1Quest.QUEST_SAI_RUNE70)) {
						htmlid = "nerva1";
					}
					if (pc.getLevel() >= 70) {
						if (pc.getInventory().checkItem(60033, 1)
								&& pc.getInventory().checkItem(60034, 1)
								&& pc.getInventory().checkItem(40087, 70)){
							pc.getInventory().consumeItem(60033, 1);
							pc.getInventory().consumeItem(60034, 1);
							pc.getInventory().consumeItem(40087, 70);
							if (s.equals("a")) {
								pc.getInventory().storeItem(60041, 1); // 힘의 엘릭서 룬
							} else if (s.equals("b")) {
								pc.getInventory().storeItem(60042, 1); // 민첩의 엘릭서 룬
							} else if (s.equals("c")) {
								pc.getInventory().storeItem(60043, 1); // 체력의 엘릭서 룬
							} else if (s.equals("d")) {
								pc.getInventory().storeItem(60044, 1); // 지식의 엘릭서 룬
							} else if (s.equals("e")) {
								pc.getInventory().storeItem(60045, 1); // 지혜의 엘릭서 룬
							}
							htmlid = "nerva3"; //있으면
							pc.getQuest().set_end(L1Quest.QUEST_SAI_RUNE70);
						} else {
							htmlid = "nerva4"; //없으면
						}
					} else {
						htmlid = "nerva4";
					}
				}	

				// 세이룬
			} else if (((L1NpcInstance)obj).getNpcTemplate().get_npcId() == 7000054) {
				if (s.equals("A") || s.equals("B") || s.equals("C") || s.equals("D") || s.equals("E")) {
					if (pc.getLevel() >= 55) {
						if (pc.getInventory().checkItem(60031, 1) && pc.getInventory().checkItem(60032, 1)) {
							pc.getInventory().consumeItem(60031, 1);
							pc.getInventory().consumeItem(60032, 1);
							if (s.equals("A")) {
								pc.getInventory().storeItem(60036, 1); // 힘의 엘릭서 룬주머니
							} else if (s.equals("B")) {
								pc.getInventory().storeItem(60037, 1); // 민첩의 엘릭서 룬
							} else if (s.equals("C")) {
								pc.getInventory().storeItem(60038, 1); // 체력의 엘릭서 룬
							} else if (s.equals("D")) {
								pc.getInventory().storeItem(60039, 1); // 지식의 엘릭서 룬
							} else if (s.equals("E")) {
								pc.getInventory().storeItem(60040, 1); // 지혜의 엘릭서 룬
							}
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "seirune6"));

						} else {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "seirune5"));
							pc.sendPackets(new S_SystemMessage("낡은 고서와 마법사의 돌이 필요하네. 마법사의 돌은 우측으로가서 포이에게 구매하게나."));
						}
					} else {
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "seirune5"));
					}
				}
			} else if (((L1NpcInstance)obj).getNpcTemplate().get_npcId() == 1000001) {//땅굴개미
				int locx = 0, locy = 0, map = 0;
				if (s.equalsIgnoreCase("b")){//1번동굴
					locx = 32783; locy = 32751; map = 43;
				} else if (s.equalsIgnoreCase("c")){//2번동굴
					locx = 32798; locy = 32754; map = 44;
				} else if (s.equalsIgnoreCase("d")){//3번동굴
					locx = 32776; locy = 32731; map = 45;
				} else if (s.equalsIgnoreCase("e")){//4번동굴
					locx = 32787; locy = 32795; map = 46;
				} else if (s.equalsIgnoreCase("f")){//5번동굴
					locx = 32796; locy = 32745; map = 47;
				} else if (s.equalsIgnoreCase("g")){//6번동굴
					locx = 32768; locy = 32805; map = 50;
				}
				if (pc.getInventory().checkItem(40308, 500)){
					pc.getInventory().consumeItem(40308, 500);
					pc.setSkillEffect(L1SkillId.ABSOLUTE_BARRIER, 5000);
					new L1Teleport().teleport(pc, locx, locy, (short) map, pc.getHeading(),true);
				} else {
					htmlid = "cave2";
				}
			

				
									/** 클라우디아 라라 **/
			} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 202057) {//라라
				if (s.equalsIgnoreCase("a")) {
					if (pc.getInventory().checkItem(40308, 0)) {
						pc.getInventory().consumeItem(40308, 0);
//						new L1Teleport().teleport(pc, 32646, 32865, (short) 7783, 5, true);
					} else {
					}
					if (pc.getLevel() >= 60) {//해당레벨이상일경우						
						pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,"\\fH라라: 신규 레벨이 아닙니다. 사용불가능합니다."));
						htmlid = "tel_lala2";
						return htmlid;
					}
						if (pc.getLevel() >= 1 & pc.getLevel() <= 60) {
							pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,"\\fH라라: 클라우디아에서 [60]까지 성장하세요! "));
							htmlid = "tel_lala1";
//							레벨52기준보상경험치(pc, 1);
							new L1Teleport().teleport(pc, 32646, 32865, (short) 7783, 5, true);
					}
				}
				
				/** 클라우디아 훈련 군터 **/
			} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 202065) {//군터
				if (s.equalsIgnoreCase("a")) {
					if (pc.getInventory().checkItem(447011, 1)) {
//						pc.getInventory().consumeItem(447011, 0);
					} else {
//					}
					if (pc.getLevel() <= 4) {//해당레벨이하일경우						
						pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,"\\fH군터(스승): 자네는..아직 레벨[5]도 못만들었나?!"));
						pc.sendPackets(new S_SystemMessage("\\aA군터: 레벨[\\aG5\\aA]를 만들고 오게나.."));
						htmlid = "archgunter2";
						return htmlid;
					}
						if (pc.getLevel() >= 5) {//해당레벨이상일경우
							pc.getInventory().checkItem(447011, 1);// 체크한다
	                        pc.getInventory().storeItem(447011, 1);// 아크프리패스상자
							htmlid = "archgunter1";
							아크프리패스(pc, 1);
							pc.sendPackets(new S_SystemMessage("\\aA군터: 이제 [\\aG훈련교관 테온\\aA]을 만나거라.."));
							new L1Teleport().teleport(pc, 32646, 32865, (short) 7783, 5, true);
					}
				}
				}
				
				/** 클라우디아 훈련교관 테온 **/
			} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 202066) {//테온
				if (s.equalsIgnoreCase("a")) {
					if (pc.getInventory().checkItem(99115, 5)) {
						pc.sendPackets(new S_ChatPacket(pc, "테온(훈련교관): '클라우디아 이동 주문서'을 소지하고 계십니다."));
						htmlid = "";
					} else {
						pc.getInventory().storeItem(99115, 5);
						pc.sendPackets(new S_ChatPacket(pc, "테온(훈련교관): '클라우디아 이동 주문서'을 드렸습니다."));
						pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,"\\fH테온(훈련교관): 이제 아덴월드 지배자를 처치하시오.."));
					}
				}
				
	            // 군터
	        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 60169) {
	            if (s.equalsIgnoreCase("a")) {
	                new L1SkillUse().handleCommands(pc, L1SkillId.BUFF_GUNTER, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_SPELLSC);	              
	            }	
				
	            // 크레이
	        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7200026) {
	            if (s.equalsIgnoreCase("a")) {
	                new L1SkillUse().handleCommands(pc, L1SkillId.BUFF_CRAY, pc
	                        .getId(), pc.getX(), pc.getY(), null, 0,
	                        L1SkillUse.TYPE_SPELLSC);
	                htmlid = "grayknight2";
	            }
	    		// 저주받은 무녀 사엘 (입구 npc)
			} else if (((L1NpcInstance)obj).getNpcTemplate().get_npcId() == 4039009) {
				if (s.equals("a")) {
					new L1SkillUse().handleCommands(pc, L1SkillId.BUFF_SAEL, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_SPELLSC);
					if (!pc.hasSkillEffect(STATUS_UNDERWATER_BREATH)) {						
						pc.setSkillEffect(STATUS_UNDERWATER_BREATH, 1800 * 1000);
						pc.sendPackets(new S_SkillIconBlessOfEva(pc.getId(), 1800));
					}
				}
				// 쿠루 몽섬 집시촌이동
			} else if (npcid == 7000097) {
				if (s.equalsIgnoreCase("teleport tamshop")) {
					new L1Teleport().teleport(pc, 33964, 32953, (short) 4,pc.getHeading(), true);
				}
			} else if (((L1NpcInstance)obj).getNpcTemplate().get_npcId() == 50015) {//말섬 텔레포트
				if (s.equalsIgnoreCase("teleport island-silver")){//
					if(pc.getInventory().checkItem(40308, 1500)){
						pc.getInventory().consumeItem(40308, 1500);
						new L1Teleport().teleport(pc, 33080, 33392, (short) 4, 5,true);
						htmlid = "";
					} else {
						pc.sendPackets(new S_SystemMessage("아데나가 부족합니다."));
					}	
				}
			} else if (((L1NpcInstance)obj).getNpcTemplate().get_npcId() == 81210) {//수상한 텔리포터
				int locx = 0, locy = 0, mapid = 0;
				if (s.equalsIgnoreCase("b")){//
					locx = 33442; locy = 32797; mapid = 4;
				} else if (s.equalsIgnoreCase("C")){//
					locx = 34056; locy = 32279; mapid = 4;
				} else if (s.equalsIgnoreCase("D")){//발라 둥지
					locx = 33705; locy = 32504; mapid = 4;
				} else if (s.equalsIgnoreCase("E")){//
					locx = 33614; locy = 33253; mapid = 4;
				} else if (s.equalsIgnoreCase("F")){//
					locx = 33050; locy = 32780; mapid = 4;
				} else if (s.equalsIgnoreCase("G")){//
					locx = 32631; locy = 32770; mapid = 4;
				} else if (s.equalsIgnoreCase("H")){//
					locx = 33080; locy = 33392; mapid = 4;
				} else if (s.equalsIgnoreCase("I")){//
					locx = 32617; locy = 33201; mapid = 4;
				} else if (s.equalsIgnoreCase("J")){//오크 숲
					locx = 32741; locy = 32450; mapid = 4;
				} else if (s.equalsIgnoreCase("K")){//
					locx = 32581; locy = 32940; mapid = 0;
				} else if (s.equalsIgnoreCase("L")){//
					locx = 33958; locy = 33364; mapid = 4;
				} else if (s.equalsIgnoreCase("N")){//
					locx = 32800; locy = 32927; mapid = 800;
				} else if (s.equalsIgnoreCase("V")){//데포류즈앞
					locx = 32595; locy = 33163; mapid = 4;
				}
				if (pc.getInventory().checkItem(40308, 100)){
					pc.getInventory().consumeItem(40308, 100);
					new L1Teleport().teleport(pc, locx, locy, (short) mapid, 5,true);
					htmlid = "";	
				} else {
					htmlid = "pctel2";
				}
		
			} else if (((L1NpcInstance)obj).getNpcTemplate().get_npcId() == 80082) {
				// 「길고 무거운 낚싯대」
				if (s.equalsIgnoreCase("a")) {
					if (pc.getInventory().checkItem(L1ItemId.ADENA, 1000)) {
						pc.getInventory().consumeItem(L1ItemId.ADENA, 1000);
							L1PolyMorph.undoPoly(pc);
							new L1Teleport().teleport(pc, 32804, 32812, (short) 5490, 6, true);
					} else {
						pc.sendPackets(new S_SystemMessage("아데나(1000)가  부족합니다."));
						htmlid = "";
					}
				}
				
				
				//리키 수련의텔레포트
				} else if (npcid == 70798) {
				if (s.equalsIgnoreCase("a")) {// 숨겨진계곡
					if (pc.getLevel() >= 1 & pc.getLevel() <= 45) {
						new L1Teleport().teleport(pc, 32684, 32851, (short) 2005, pc.getHeading(), true);
					} else {
//						pc.sendPackets(new S_ChatPacket(pc, "레벨 45 이하만 가능합니다."));
						pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,"\\fQ리키: \\f3[Lv.45]\\fQ이하만 입장 허용 레벨입니다."));
					}
				} else if (s.equalsIgnoreCase("b")) {// 기란마을
					new L1Teleport().teleport(pc, 33436, 32799, (short) 4, pc.getHeading(), true);
				
				} else if (s.equalsIgnoreCase("c")) {// 라우풀신전
					if (pc.getLevel() >= 10 & pc.getLevel() <= 29) {
						new L1Teleport().teleport(pc, 33184, 33449, (short) 4, pc.getHeading(), true);
					} else {
						pc.sendPackets(new S_ChatPacket(pc,"은기사 필드 이동 가능레벨 10 ~ 29"));
					}
				} else if (s.equalsIgnoreCase("d")) {// 카오틱신전
					if (pc.getLevel() >= 10 & pc.getLevel() <= 29) {
					new L1Teleport().teleport(pc, 33066, 33218, (short) 4, pc.getHeading(), true);
					} else {
						pc.sendPackets(new S_ChatPacket(pc,"은기사 필드 이동 가능레벨 10 ~ 29"));
					}
				} else if (s.equalsIgnoreCase("f")) {// 수련던전
					if (pc.getLevel() >= 10 & pc.getLevel() < 20) {
						new L1Teleport().teleport(pc, 32801, 32806, (short) 25, pc.getHeading(), true);
					} else if (pc.getLevel() >= 20 & pc.getLevel() < 30) {
						new L1Teleport().teleport(pc, 32806, 32746, (short) 26, pc.getHeading(), true);
				    } else if (pc.getLevel() >= 30 & pc.getLevel() < 40) {
						new L1Teleport().teleport(pc, 32808, 32766, (short) 27, pc.getHeading(), true);
					} else if (pc.getLevel() >= 40 & pc.getLevel() < 44) {
						new L1Teleport().teleport(pc, 32796, 32799, (short) 28, pc.getHeading(), true);
					} else {
						pc.sendPackets(new S_ChatPacket(pc,"수련 던전 이동 가능레벨 10 ~ 44"));
					}
				} else if (s.equalsIgnoreCase("e")) {// 폭풍던전 불신 Lv 45~51
					if (pc.getLevel() >= 45 & pc.getLevel() <= 51) {
						new L1Teleport().teleport(pc, 32807, 32789, (short) 2010, pc.getHeading(), true);
					}  else {
						pc.sendPackets(new S_ChatPacket(pc,"폭풍 수련 던전 이동 가능레벨 45 ~ 51"));
					}
				}

				// 바무트 제작리뉴얼
			} else if (npcid == 70690) {
				if (s.equalsIgnoreCase("a")) {
				if (pc.getInventory().checkItem(410061, 50)
					&& pc.getInventory().checkItem(40053, 10)
					&& pc.getInventory().checkItem(40393, 5)) {
						pc.getInventory().consumeItem(410061, 50);
						pc.getInventory().consumeItem(40053, 10);
						pc.getInventory().consumeItem(40393, 5);
						pc.getInventory().storeItem(222307, 1);// 완력의 부츠
						htmlid = "";
				} else {
					pc.sendPackets(new S_SystemMessage("제작 아이템이 부족합니다."));
					pc.sendPackets(new S_SystemMessage("마물의 기운(50), 최고급 루비(10), 화룡 비늘(5)"));
				}
			 } else if (s.equalsIgnoreCase("b")) {
				if (pc.getInventory().checkItem(410061, 50) 
					&& pc.getInventory().checkItem(40052, 10)
					&& pc.getInventory().checkItem(40396, 5)) {
						pc.getInventory().consumeItem(410061, 50);
						pc.getInventory().consumeItem(40052, 10);
						pc.getInventory().consumeItem(40396, 5);
						pc.getInventory().storeItem(22359, 1);// 지혜의 부츠
						htmlid = "";
					} else {
						pc.sendPackets(new S_SystemMessage("제작 아이템이 부족합니다."));
						pc.sendPackets(new S_SystemMessage("마물의 기운(50), 최고급 다이아몬드(10), 지룡 비늘(5)"));
					}

			 } else if (s.equalsIgnoreCase("c")) {
				if (pc.getInventory().checkItem(410061, 50)
					&& pc.getInventory().checkItem(40055, 10)
					&& pc.getInventory().checkItem(40394, 5)) {
						pc.getInventory().consumeItem(410061, 50);
						pc.getInventory().consumeItem(40055, 10);
						pc.getInventory().consumeItem(40394, 5);
						pc.getInventory().storeItem(222308, 1);// 민첩의 부츠
						htmlid = "";
					} else {
						pc.sendPackets(new S_SystemMessage("제작 아이템이 부족합니다."));
						pc.sendPackets(new S_SystemMessage("마물의 기운(50), 최고급 에메랄드(10), 풍룡 비늘(5)"));
					}
			 } else if (s.equalsIgnoreCase("d")) {
				if (pc.getInventory().checkItem(410061, 50)
					&& pc.getInventory().checkItem(40054, 10)
					&& pc.getInventory().checkItem(40395, 5)) {
						pc.getInventory().consumeItem(410061, 50);
						pc.getInventory().consumeItem(40054, 10);
						pc.getInventory().consumeItem(40395, 5);
						pc.getInventory().storeItem(222309, 1);// 지식의부츠
						htmlid = "";
					} else {
						pc.sendPackets(new S_SystemMessage("제작 아이템이 부족합니다."));
						pc.sendPackets(new S_SystemMessage("마물의 기운(50), 최고급 사파이어(10), 수룡 비늘(5)"));
					}
			 } else if (s.equalsIgnoreCase("e")) {
				if (pc.getInventory().checkItem(410061, 50) 
					 && pc.getInventory().checkItem(560030)) {
						pc.getInventory().consumeItem(410061, 50);
						pc.getInventory().consumeItem(560030, 1);
						pc.getInventory().storeItem(222307, 1);// 완력의 부츠
						htmlid = "";
					} else {
						pc.sendPackets(new S_SystemMessage("제작 아이템이 부족합니다."));
						pc.sendPackets(new S_SystemMessage("마물의 기운(50), 화령 속성 변환 주문서(1)"));
					}	
			 } else if (s.equalsIgnoreCase("f")) {
				if (pc.getInventory().checkItem(410061, 50) 
					 && pc.getInventory().checkItem(560033)) {
						pc.getInventory().consumeItem(410061, 50);
						pc.getInventory().consumeItem(560033, 1);
						pc.getInventory().storeItem(22359, 1);// 지혜의 부츠
						htmlid = "";
					} else {
						pc.sendPackets(new S_SystemMessage("제작 아이템이 부족합니다."));
						pc.sendPackets(new S_SystemMessage("마물의 기운(50), 지령 속성 변환 주문서(1)"));
					}	
			 } else if (s.equalsIgnoreCase("g")) {
				if (pc.getInventory().checkItem(410061, 50) 
					 && pc.getInventory().checkItem(560032)) {
						pc.getInventory().consumeItem(410061, 50);
						pc.getInventory().consumeItem(560032, 1);
						pc.getInventory().storeItem(222308, 1);// 민첩의 부츠
						htmlid = "";
					} else {
						pc.sendPackets(new S_SystemMessage("제작 아이템이 부족합니다."));
						pc.sendPackets(new S_SystemMessage("마물의 기운(50), 풍령 속성 변환 주문서(1)"));
					}
			 } else if (s.equalsIgnoreCase("h")) {
				if (pc.getInventory().checkItem(410061, 50) 
					 && pc.getInventory().checkItem(560031)) {
						pc.getInventory().consumeItem(410061, 50);
						pc.getInventory().consumeItem(560031, 1);
						pc.getInventory().storeItem(222309, 1);// 지식의 부츠
						htmlid = "";
					} else {
						pc.sendPackets(new S_SystemMessage("제작 아이템이 부족합니다."));
						pc.sendPackets(new S_SystemMessage("마물의 기운(50), 수령 속성 변환 주문서(1)"));
					}
			 }

			} else if (npcid == 50045) {// 헤이트(유니콘사원관리자)
				if (s.equalsIgnoreCase("a")) {
					if (pc.getInventory().consumeItem(810000)) {
						new L1Teleport().teleport(pc, 32800, 32798, (short) 1935, 2, true);
					} else {
						htmlid = "edlen4";
					}
				} else if (s.equalsIgnoreCase("b")) {
					new L1Teleport().teleport(pc, 33440, 32808, (short) 4, 2, true);
				} else {
					htmlid = "";
				}


			} else if (npcid == 7200000) { // 몽섬 에킨스
				L1ItemInstance item = null;
				L1NpcInstance npc = (L1NpcInstance) obj;
				String npcName = npc.getNameId();
				if (s.equalsIgnoreCase("a")) {
					pc.sendPackets(new S_SystemMessage("성장의 구슬 및 성장의 구슬 조각은 더 이상 얻을 수 없습니다."));
					htmlid = "";
				} else if (s.equalsIgnoreCase("b")) {
					pc.sendPackets(new S_SystemMessage("성장의 구슬 및 성장의 구슬 조각은 더 이상 얻을 수 없습니다."));
					htmlid = "";
				} else if (s.equalsIgnoreCase("c")) {
					if (pc.getInventory().checkItem(31088, 1)) {
						pc.getInventory().consumeItem(31088, 1);
						에킨스경험치2(pc);
						item = pc.getInventory().storeItem(810016, 5);
						pc.sendPackets(new S_ServerMessage(143, npcName, item.getLogName()));
					} else {
						pc.sendPackets(new S_SystemMessage("유니콘의 성장 징표가 부족합니다."));
					}
				} else if (s.equalsIgnoreCase("d")) {
					if (pc.getInventory().checkItem(31088, 1) && pc.getInventory().checkItem(1000004, 1)) {
						pc.getInventory().consumeItem(31088, 1);
						pc.getInventory().consumeItem(1000004, 1);
						에킨스경험치6(pc);
						item = pc.getInventory().storeItem(810016, 8);
						pc.sendPackets(new S_ServerMessage(143, npcName, item.getLogName()));
					} else {
						pc.sendPackets(new S_SystemMessage("유니콘의 성장 징표, 드래곤의 다이아몬드 가 부족합니다."));
					}
				}

			} else if (npcid == 7200001) { //중앙사원 문지기
				if (s.equalsIgnoreCase("enter")) {
					FantasyIslandSystem.getInstance().startRaid(pc);
				}
				
				/** 하버트 **/
			} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70641) {
				if (s.equalsIgnoreCase("a")) {
					if (pc.getInventory().checkItem(40395, 1)       //수룡비늘
					 && pc.getInventory().checkItem(410061, 10)     //마물의기운
					 && pc.getInventory().checkItem(820004, 300)) { //마력의실타래

						pc.getInventory().consumeItem(40395, 1);
						pc.getInventory().consumeItem(410061, 10);
						pc.getInventory().consumeItem(820004, 300);
						pc.getInventory().storeItem(20273, 1);// 마력의 장갑
						htmlid = "";
					} else {
						pc.sendPackets(new S_SystemMessage("제작 아이템이 부족합니다."));
						pc.sendPackets(new S_SystemMessage("수룡 비늘(1)"));
						pc.sendPackets(new S_SystemMessage("마물의 기운(10)"));
						pc.sendPackets(new S_SystemMessage("마력의 실타래(300)"));
					}
				}else if (s.equalsIgnoreCase("b")) {
					if (pc.getInventory().checkEnchantItem(20273,7,1)  //+7 마력의장갑				
					 && pc.getInventory().checkItem(40395, 1)      //수룡비늘
					 && pc.getInventory().checkItem(410061, 10)    //마물의기운
					 && pc.getInventory().checkItem(820004, 300)   //마력의실타래
					 && pc.getInventory().checkItem(820005, 1)) {  //마력의핵

						pc.getInventory().consumeEnchantItem(20273,7,1);
						pc.getInventory().consumeItem(40395, 1);
						pc.getInventory().consumeItem(410061, 10);
						pc.getInventory().consumeItem(820004, 300);
						pc.getInventory().consumeItem(820005, 1);
						pc.getInventory().storeItem(20274, 1);// 빛나는 마력의 장갑
						htmlid = "";
					} else {
						pc.sendPackets(new S_SystemMessage("제작 아이템이 부족합니다."));
						pc.sendPackets(new S_SystemMessage("수룡 비늘(1)"));
						pc.sendPackets(new S_SystemMessage("마물의 기운(10)"));
						pc.sendPackets(new S_SystemMessage("마력의 실타래(300)"));
						pc.sendPackets(new S_SystemMessage("마력의 핵(1)"));
						pc.sendPackets(new S_SystemMessage("+7 마력의 장갑(1)"));
					}
				}
			
	
				//악마왕영토
			} else if (npcid == 5100017) {
				if (pc.getLevel() < Config.악마왕입장레벨) {
					pc.sendPackets(new S_SystemMessage("레벨 " + Config.악마왕입장레벨 + " 이상만 입장할 수 있습니다."));
					return htmlid;
				}
				if(s.equalsIgnoreCase("b")) { 
					htmlid = "";  
					if(DevilController.getInstance().getDevilStart() == true) {
						Random random = new Random(); 
						int i13 = 32723 + random.nextInt(5); 
						int k19 = 32800 + random.nextInt(5); 
						new L1Teleport().teleport(pc, i13, k19, (short)5167, 6, true); 
						pc.setSkillEffect(L1SkillId.ABSOLUTE_BARRIER, 3000);
						pc.sendPackets(new S_ChatPacket(pc,"열린시각으로부터 60분동안 입장이 가능합니다."));
					}else{         	
						pc.sendPackets(new S_ChatPacket(pc,"악마왕의 영토가 아직 열리지않았습니다."));
					} 
				} 
				//조우의 불골렘 리뉴얼
			} else if (npcid == 5066) {
				int enchant = 0;
				int itemId = 0;
				int oldArmor = 0;
				L1NpcInstance npc = (L1NpcInstance) obj;
				String npcName = npc.getNpcTemplate().get_name();
				if (s.equalsIgnoreCase("1")) {     // [+7]마력의 단검	
					if ((pc.getInventory().checkEnchantItem(5,8,1)
							|| pc.getInventory().checkEnchantItem(6,8,1)
							|| pc.getInventory().checkEnchantItem(32,8,1)
							|| pc.getInventory().checkEnchantItem(37,8,1)
							|| pc.getInventory().checkEnchantItem(41,8,1) 
							|| pc.getInventory().checkEnchantItem(42,8,1)
							|| pc.getInventory().checkEnchantItem(52,8,1)
							|| pc.getInventory().checkEnchantItem(64,8,1)
							|| pc.getInventory().checkEnchantItem(99,8,1)
							|| pc.getInventory().checkEnchantItem(104,8,1)
							|| pc.getInventory().checkEnchantItem(125,8,1)
							|| pc.getInventory().checkEnchantItem(129,8,1)
							|| pc.getInventory().checkEnchantItem(131,8,1)
							|| pc.getInventory().checkEnchantItem(145,8,1)
							|| pc.getInventory().checkEnchantItem(148,8,1)
							|| pc.getInventory().checkEnchantItem(180,8,1)
							|| pc.getInventory().checkEnchantItem(181,8,1))
							&& pc.getInventory().checkItem(40308,5000000))	{
						if (pc.getInventory().consumeEnchantItem(5,8,1)
								||pc.getInventory().consumeEnchantItem(6,8,1)
								||pc.getInventory().consumeEnchantItem(32,8,1)
								||pc.getInventory().consumeEnchantItem(37,8,1)
								||pc.getInventory().consumeEnchantItem(41,8,1)
								||pc.getInventory().consumeEnchantItem(42,8,1)
								||pc.getInventory().consumeEnchantItem(52,8,1)
								||pc.getInventory().consumeEnchantItem(64,8,1)
								||pc.getInventory().consumeEnchantItem(99,8,1)
								||pc.getInventory().consumeEnchantItem(104,8,1)
								||pc.getInventory().consumeEnchantItem(125,8,1)
								||pc.getInventory().consumeEnchantItem(129,8,1)
								||pc.getInventory().consumeEnchantItem(131,8,1)
								||pc.getInventory().consumeEnchantItem(145,8,1)
								||pc.getInventory().consumeEnchantItem(148,8,1)
								||pc.getInventory().consumeEnchantItem(180,8,1)
								||pc.getInventory().consumeEnchantItem(181,8,1)) {
							;
						}
						pc.getInventory().consumeItem(40308, 5000000);
						인첸트지급(pc, 602, 1, 7); 
						htmlid = "";
					} else {
						pc.sendPackets(new S_SystemMessage("제작 아이템이 부족합니다."));
					}
				}else if(s.equalsIgnoreCase("2")) {// [+8]마력의 단검
					if ((pc.getInventory().checkEnchantItem(5,9,1)
							|| pc.getInventory().checkEnchantItem(6,9,1)
							|| pc.getInventory().checkEnchantItem(32,9,1)
							|| pc.getInventory().checkEnchantItem(37,9,1)
							|| pc.getInventory().checkEnchantItem(41,9,1) 
							|| pc.getInventory().checkEnchantItem(42,9,1)
							|| pc.getInventory().checkEnchantItem(52,9,1)
							|| pc.getInventory().checkEnchantItem(64,9,1)
							|| pc.getInventory().checkEnchantItem(99,9,1)
							|| pc.getInventory().checkEnchantItem(104,9,1)
							|| pc.getInventory().checkEnchantItem(125,9,1)
							|| pc.getInventory().checkEnchantItem(129,9,1)
							|| pc.getInventory().checkEnchantItem(131,9,1)
							|| pc.getInventory().checkEnchantItem(145,9,1)
							|| pc.getInventory().checkEnchantItem(148,9,1)
							|| pc.getInventory().checkEnchantItem(180,9,1)
							|| pc.getInventory().checkEnchantItem(181,9,1))
							&& pc.getInventory().checkItem(40308,10000000))	{
						if (pc.getInventory().consumeEnchantItem(5,9,1)
								||pc.getInventory().consumeEnchantItem(6,9,1)
								||pc.getInventory().consumeEnchantItem(32,9,1)
								||pc.getInventory().consumeEnchantItem(37,9,1)
								||pc.getInventory().consumeEnchantItem(41,9,1)
								||pc.getInventory().consumeEnchantItem(42,9,1)
								||pc.getInventory().consumeEnchantItem(52,9,1)
								||pc.getInventory().consumeEnchantItem(64,9,1)
								||pc.getInventory().consumeEnchantItem(99,9,1)
								||pc.getInventory().consumeEnchantItem(104,9,1)
								||pc.getInventory().consumeEnchantItem(125,9,1)
								||pc.getInventory().consumeEnchantItem(129,9,1)
								||pc.getInventory().consumeEnchantItem(131,9,1)
								||pc.getInventory().consumeEnchantItem(145,9,1)
								||pc.getInventory().consumeEnchantItem(148,9,1)
								||pc.getInventory().consumeEnchantItem(180,9,1)
								||pc.getInventory().consumeEnchantItem(181,9,1)) {
							;
						}
						pc.getInventory().consumeItem(40308, 10000000);
						인첸트지급(pc, 602, 1, 8); 
						htmlid = "";
					} else {
						pc.sendPackets(new S_SystemMessage("제작 아이템이 부족합니다."));
					}
				}else if(s.equalsIgnoreCase("3")) {// [+7]환영의 체인소드
					if ((pc.getInventory().checkEnchantItem(500,8,1)
							|| pc.getInventory().checkEnchantItem(501,8,1))
							&& pc.getInventory().checkItem(40308,5000000))	{
						if (pc.getInventory().consumeEnchantItem(500,8,1)
								||pc.getInventory().consumeEnchantItem(501,8,1)) {
							;
						}
						pc.getInventory().consumeItem(40308, 5000000);
						인첸트지급(pc, 202001, 1, 7); 
						htmlid = "";
					} else {
						pc.sendPackets(new S_SystemMessage("제작 아이템이 부족합니다."));
					}
				}else if(s.equalsIgnoreCase("4")) {// [+8]환영의 체인소드
					if ((pc.getInventory().checkEnchantItem(500,9,1)
							|| pc.getInventory().checkEnchantItem(501,9,1))
							&& pc.getInventory().checkItem(40308,10000000))	{
						if (pc.getInventory().consumeEnchantItem(500,9,1)
								||pc.getInventory().consumeEnchantItem(501,9,1)) {
							;
						}
						pc.getInventory().consumeItem(40308, 10000000);
						인첸트지급(pc, 202001, 1, 8); 
						htmlid = "";
					} else {
						pc.sendPackets(new S_SystemMessage("제작 아이템이 부족합니다."));
					}
				}else if(s.equalsIgnoreCase("5")) {// [+7]공명의 키링크 
					if ((pc.getInventory().checkEnchantItem(503,8,1)
							|| pc.getInventory().checkEnchantItem(504,8,1))
							&& pc.getInventory().checkItem(40308,5000000))	{
						if (pc.getInventory().consumeEnchantItem(503,8,1)
								||pc.getInventory().consumeEnchantItem(504,8,1)) {
							;
						}
						pc.getInventory().consumeItem(40308, 5000000);
						인첸트지급(pc, 1135, 1, 7); 
						htmlid = "";
					} else {
						pc.sendPackets(new S_SystemMessage("제작 아이템이 부족합니다."));
					}
				}else if(s.equalsIgnoreCase("6")) {// [+8]공명의 키링크 
					if ((pc.getInventory().checkEnchantItem(503,9,1)
							|| pc.getInventory().checkEnchantItem(504,9,1))
							&& pc.getInventory().checkItem(40308,10000000))	{
						if (pc.getInventory().consumeEnchantItem(503,9,1)
								||pc.getInventory().consumeEnchantItem(504,9,1)) {
							;
						}
						pc.getInventory().consumeItem(40308, 10000000);
						인첸트지급(pc, 1135, 1, 8); 
						htmlid = "";
					} else {
						pc.sendPackets(new S_SystemMessage("제작 아이템이 부족합니다."));
					}
				}else if(s.equalsIgnoreCase("7")) {// [+7]파괴의 크로우
					if ((pc.getInventory().checkEnchantItem(81,8,1)
							|| pc.getInventory().checkEnchantItem(177,8,1)
							|| pc.getInventory().checkEnchantItem(194,8,1)
							|| pc.getInventory().checkEnchantItem(13,8,1))
							&& pc.getInventory().checkItem(40308,5000000))	{
						if (pc.getInventory().consumeEnchantItem(81,8,1)
								||pc.getInventory().consumeEnchantItem(177,8,1)
								||pc.getInventory().consumeEnchantItem(194,8,1)
								||pc.getInventory().consumeEnchantItem(13,8,1)) {
							;
						}
						pc.getInventory().consumeItem(40308, 5000000);
						인첸트지급(pc, 1124, 1, 7); 
						htmlid = "";
					} else {
						pc.sendPackets(new S_SystemMessage("제작 아이템이 부족합니다."));
					}
				}else if(s.equalsIgnoreCase("8")) {// [+8]파괴의 크로우
					if ((pc.getInventory().checkEnchantItem(81,9,1)
							|| pc.getInventory().checkEnchantItem(177,9,1)
							|| pc.getInventory().checkEnchantItem(194,9,1)
							|| pc.getInventory().checkEnchantItem(13,9,1))
							&& pc.getInventory().checkItem(40308,10000000))	{
						if (pc.getInventory().consumeEnchantItem(81,9,1)
								||pc.getInventory().consumeEnchantItem(177,9,1)
								||pc.getInventory().consumeEnchantItem(194,9,1)
								||pc.getInventory().consumeEnchantItem(13,9,1)) {
							;
						}
						pc.getInventory().consumeItem(40308, 10000000);
						인첸트지급(pc, 1124, 1, 8); 
						htmlid = "";
					} else {
						pc.sendPackets(new S_SystemMessage("제작 아이템이 부족합니다."));
					}
				}else if(s.equalsIgnoreCase("9")) {// [+7]파괴의 이도류 
					if ((pc.getInventory().checkEnchantItem(81,8,1)
							|| pc.getInventory().checkEnchantItem(177,8,1)
							|| pc.getInventory().checkEnchantItem(194,8,1)
							|| pc.getInventory().checkEnchantItem(13,8,1))
							&& pc.getInventory().checkItem(40308,5000000))	{
						if (pc.getInventory().consumeEnchantItem(81,8,1)
								||pc.getInventory().consumeEnchantItem(177,8,1)
								||pc.getInventory().consumeEnchantItem(194,8,1)
								||pc.getInventory().consumeEnchantItem(13,8,1)) {
							;
						}
						pc.getInventory().consumeItem(40308, 5000000);
						인첸트지급(pc, 1125, 1, 7); 
						htmlid = "";
					} else {
						pc.sendPackets(new S_SystemMessage("제작 아이템이 부족합니다."));
					}
				}else if(s.equalsIgnoreCase("10")) {// [+8]파괴의 이도류 
					if ((pc.getInventory().checkEnchantItem(81,9,1)
							|| pc.getInventory().checkEnchantItem(177,9,1)
							|| pc.getInventory().checkEnchantItem(194,9,1)
							|| pc.getInventory().checkEnchantItem(13,9,1))
							&& pc.getInventory().checkItem(40308,10000000))	{
						if (pc.getInventory().consumeEnchantItem(81,9,1)
								||pc.getInventory().consumeEnchantItem(177,9,1)
								||pc.getInventory().consumeEnchantItem(194,9,1)
								||pc.getInventory().consumeEnchantItem(13,9,1)) {
							;
						}
						pc.getInventory().consumeItem(40308, 10000000);
						인첸트지급(pc, 1125, 1, 8); 
						htmlid = "";
					} else {
						pc.sendPackets(new S_SystemMessage("제작 아이템이 부족합니다."));
					}
				}else if(s.equalsIgnoreCase("11")) {// [+0]제로스의 지팡이
					if (pc.getInventory().checkEnchantItem(119, 5, 1) && pc.getInventory().checkEnchantItem(121, 9, 1)
							&& pc.getInventory().checkItem(700077) && pc.getInventory().checkItem(41246)) {
						pc.getInventory().consumeEnchantItem(119, 5, 1);
						pc.getInventory().consumeEnchantItem(121, 9, 1);
						pc.getInventory().consumeItem(700077, 1);
						pc.getInventory().consumeItem(41246, 100000);
						pc.getInventory().storeItem(202003, 1);
						htmlid = "";
					} else {
						pc.sendPackets(new S_SystemMessage("제작 아이템이 부족합니다."));
					}
					
				}else if(s.equalsIgnoreCase("12")) {// [+8]제로스의 지팡이 
					if (pc.getInventory().checkEnchantItem(119, 5, 1) && pc.getInventory().checkEnchantItem(121, 10, 1)
							&& pc.getInventory().checkItem(700077) && pc.getInventory().checkItem(41246)) {
						pc.getInventory().consumeEnchantItem(119, 5, 1);
						pc.getInventory().consumeEnchantItem(121, 10, 1);
						pc.getInventory().consumeItem(700077, 1);
						pc.getInventory().consumeItem(41246, 100000);
						인첸트지급(pc, 202003, 1, 8); 
						htmlid = "";
					} else {
						pc.sendPackets(new S_SystemMessage("제작 아이템이 부족합니다."));
					}
				}else if(s.equalsIgnoreCase("13")) {// [+9]제로스의 지팡이 
					if (pc.getInventory().checkEnchantItem(119, 5, 1) && pc.getInventory().checkEnchantItem(121, 11, 1)
							&& pc.getInventory().checkItem(700077) && pc.getInventory().checkItem(41246)) {
						pc.getInventory().consumeEnchantItem(119, 5, 1);
						pc.getInventory().consumeEnchantItem(121, 11, 1);
						pc.getInventory().consumeItem(700077, 1);
						pc.getInventory().consumeItem(41246, 100000);
						인첸트지급(pc, 202003, 1, 9); 
						htmlid = "";
					} else {
						pc.sendPackets(new S_SystemMessage("제작 아이템이 부족합니다."));
					}
					
				}else if (s.equals("A") || s.equals("B") || s.equals("C") || s.equals("D") // 판금
						|| s.equals("E") || s.equals("F") || s.equals("G") || s.equals("H") // 비늘
						|| s.equals("I") || s.equals("J") || s.equals("K") || s.equals("L") // 가죽
						|| s.equals("M") || s.equals("N") || s.equals("O") || s.equals("P")) { // 로브
					if (s.equals("A") || s.equals("B") || s.equals("C") || s.equals("D")) {
						if (s.equals("A")) { enchant = 7;
						} else if (s.equals("B")) { enchant = 8;
						} else if (s.equals("C")) { enchant = 9;
						} else if (s.equals("D")) { enchant = 10;
						}
						oldArmor = 20095;
						itemId = 222300;
					} else if (s.equals("E") || s.equals("F") || s.equals("G") || s.equals("H")) {
						if (s.equals("E")) { enchant = 7;
						} else if (s.equals("F")) { enchant = 8;
						} else if (s.equals("G")) { enchant = 9;
						} else if (s.equals("H")) { enchant = 10;
						}
						oldArmor = 20094;
						itemId = 222301;
					} else if (s.equals("I") || s.equals("J") || s.equals("K") || s.equals("L")) {
						if (s.equals("I")) { enchant = 7;
						} else if (s.equals("J")) { enchant = 8;
						} else if (s.equals("K")) { enchant = 9;
						} else if (s.equals("L")) { enchant = 10;
						}
						oldArmor = 20092;
						itemId = 222302;
					} else if (s.equals("M") || s.equals("N") || s.equals("O") || s.equals("P")) {
						if (s.equals("M")) { enchant = 7;
						} else if (s.equals("N")) { enchant = 8;
						} else if (s.equals("O")) { enchant = 9;
						} else if (s.equals("P")) { enchant = 10;
						}
						oldArmor = 20093;
						itemId = 222303;
					}
					if (pc.getInventory().checkEnchantItem(20110, enchant, 1) && pc.getInventory().checkItem(41246, 100000)
							&& pc.getInventory().checkItem(oldArmor, 1)) {
						pc.getInventory().consumeEnchantItem(20110, enchant, 1);
						pc.getInventory().consumeItem(41246, 100000); // 용해제
						pc.getInventory().consumeItem(oldArmor, 1); // 고대의
						createNewItem(pc, npcName, itemId, 1, enchant - 7);
						htmlid = "";
					} else {
						pc.sendPackets(new S_SystemMessage("제작 아이템이 부족합니다."));
					}
			}else if(s.equals("a")) {// []질풍의도끼
				if ((pc.getInventory().checkEnchantItem(605,8,1))
				 && pc.getInventory().checkItem(41246,100000))	{
					if (pc.getInventory().consumeEnchantItem(605, 8, 1)) {;
					}
					pc.getInventory().consumeItem(41246, 100000);
					인첸트지급(pc, 203015, 1, 0); 
					htmlid = "";
				} else {
					pc.sendPackets(new S_SystemMessage("+8 광풍의도끼, 결정체(100,000)개 필요합니다."));
				}
			}else if(s.equals("b")) {// [+8]질풍의도끼
				if ((pc.getInventory().checkEnchantItem(605,9,1))
				 && pc.getInventory().checkItem(41246,100000))	{
					if (pc.getInventory().consumeEnchantItem(605, 9, 1)) {;
					}
					pc.getInventory().consumeItem(41246, 100000);
					인첸트지급(pc, 203015, 1, 8); 
					htmlid = "";
				} else {
					pc.sendPackets(new S_SystemMessage("+9 광풍의도끼, 결정체(100,000)개 필요합니다."));
				}
			}else if(s.equals("c")) {// [+9]질풍의도끼
				if ((pc.getInventory().checkEnchantItem(605,10,1))
				 && pc.getInventory().checkItem(41246,100000))	{
					if (pc.getInventory().consumeEnchantItem(605, 10, 1)) {;
					}
					pc.getInventory().consumeItem(41246, 100000);
					인첸트지급(pc, 203015, 1, 9); 
					htmlid = "";
				} else {
					pc.sendPackets(new S_SystemMessage("+10 광풍의도끼, 결정체(100,000)개 필요합니다."));
				}
			}else if(s.equals("d")) {// []마물의도끼
				if ((pc.getInventory().checkEnchantItem(151,0,1))
				 && pc.getInventory().checkItem(41246,200000))	{
					if (pc.getInventory().consumeEnchantItem(151, 0, 1)) {;
					}
					pc.getInventory().consumeItem(41246, 200000);
					인첸트지급(pc, 203016, 1, 0); 
					htmlid = "";
				} else {
					pc.sendPackets(new S_SystemMessage("+0 데몬 액스, 결정체(100,000)개 필요합니다."));
				}
			}else if(s.equals("e")) {// [+1]마물의도끼
				if ((pc.getInventory().checkEnchantItem(151,3,1))
				 && pc.getInventory().checkItem(41246,200000))	{
					if (pc.getInventory().consumeEnchantItem(151, 3, 1)) {;
					}
					pc.getInventory().consumeItem(41246, 200000);
					인첸트지급(pc, 203016, 1, 1); 
					htmlid = "";
				} else {
					pc.sendPackets(new S_SystemMessage("+3 데몬 액스, 결정체(100,000)개 필요합니다."));
				}
			}else if(s.equals("f")) {// [+3]마물의도끼
				if ((pc.getInventory().checkEnchantItem(151,5,1))
				 && pc.getInventory().checkItem(41246,200000))	{
					if (pc.getInventory().consumeEnchantItem(151, 5, 1)) {;
					}
					pc.getInventory().consumeItem(41246, 200000);
					인첸트지급(pc, 203016, 1, 3); 
					htmlid = "";
					} else {
						pc.sendPackets(new S_SystemMessage("+5 데몬 액스, 결정체(100,000)개 필요합니다."));
					}
				}
			
				//제작테이블(금속) 	
				} else if (npcid == 7210043) {
					Random random = new Random();
					if (s.equals("1")) {
						if (pc.getInventory().checkItem(40747,1000) && pc.getInventory().checkItem(41246,1000)) {
							pc.getInventory().consumeItem(40747,1000);
							pc.getInventory().consumeItem(41246,1000);
							pc.getInventory().storeItem(820014, 1000);
							pc.sendPackets(new S_SystemMessage("지령의 블랙미스릴 화살(1000) 획득."));
							htmlid = "";//지령의 블랙 미스릴 화살
						} else {
							pc.sendPackets(new S_SystemMessage("블랙 미스릴 화살(1000), 결정체(1000) 필요합니다."));
						}
					}else if (s.equals("2")) {
						if (pc.getInventory().checkItem(40747,1000) && pc.getInventory().checkItem(41246,1000)) {
							pc.getInventory().consumeItem(40747,1000);
							pc.getInventory().consumeItem(41246,1000);
							pc.getInventory().storeItem(820015, 1000);
							pc.sendPackets(new S_SystemMessage("수령의 블랙미스릴 화살(1000) 획득."));
							htmlid = "";//수령의 블랙 미스릴 화살
						} else {
							pc.sendPackets(new S_SystemMessage("블랙 미스릴 화살(1000), 결정체(1000) 필요합니다."));
						}
					}else if (s.equals("3")) {
						if (pc.getInventory().checkItem(40747,1000) && pc.getInventory().checkItem(41246,1000)) {
							pc.getInventory().consumeItem(40747,1000);
							pc.getInventory().consumeItem(41246,1000);
							pc.getInventory().storeItem(820016, 1000);
							pc.sendPackets(new S_SystemMessage("풍령의 블랙미스릴 화살(1000) 획득."));
							htmlid = "";//풍령의 블랙 미스릴 화살
						} else {
							pc.sendPackets(new S_SystemMessage("블랙 미스릴 화살(1000), 결정체(1000) 필요합니다."));
						}
					}else if (s.equals("4")) {
						if (pc.getInventory().checkItem(40747,1000) && pc.getInventory().checkItem(41246,1000)) {
							pc.getInventory().consumeItem(40747,1000);
							pc.getInventory().consumeItem(41246,1000);
							pc.getInventory().storeItem(820017, 1000);
							pc.sendPackets(new S_SystemMessage("화령의 블랙미스릴 화살(1000) 획득."));
							htmlid = "";//화령의 블랙 미스릴 화살
						} else {
							pc.sendPackets(new S_SystemMessage("블랙 미스릴 화살(1000), 결정체(1000) 필요합니다."));
						}
					}else if (s.equalsIgnoreCase("5")) {
						if (pc.getInventory().checkEnchantItem(20011, 7, 2)) {
							if (random.nextInt(10) > 7) { // 30%의 확률로 성공
								pc.getInventory().consumeEnchantItem(20011, 7, 1);
								pc.getInventory().consumeEnchantItem(20011, 7, 1);
								인첸트지급(pc, 222325, 1, 6);
								pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
								Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
								pc.sendPackets(new S_SystemMessage("대성공! +6 축복 받은 신성한 마법 방어 투구를 얻었습니다."));
							} else { // 나머지 확률은 걍 신성투구
								pc.getInventory().consumeEnchantItem(20011, 7, 1);
								pc.getInventory().consumeEnchantItem(20011, 7, 1);
								인첸트지급(pc, 222324, 1, 6);
							}
							htmlid = "";
						} else {
							pc.sendPackets(new S_SystemMessage("+7 마법 방어 투구(2) 필요합니다."));
						}
					}else if (s.equalsIgnoreCase("6")) {
						if (pc.getInventory().checkEnchantItem(20011, 8, 2)) {
							if (random.nextInt(10) > 7) { // 30%의 확률로 성공
								pc.getInventory().consumeEnchantItem(20011, 8, 1);
								pc.getInventory().consumeEnchantItem(20011, 8, 1);
								인첸트지급(pc, 222325, 1, 7);
								pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
								Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
								pc.sendPackets(new S_SystemMessage("대성공! +7 축복 받은 신성한 마법 방어 투구를 얻었습니다."));
							} else { // 나머지 확률은 걍 신성투구
								pc.getInventory().consumeEnchantItem(20011, 8, 1);
								pc.getInventory().consumeEnchantItem(20011, 8, 1);
								인첸트지급(pc, 222324, 1, 7);
							}
							htmlid = "";
						} else {
							pc.sendPackets(new S_SystemMessage("+8 마법 방어 투구(2) 필요합니다."));
						}
					}else if (s.equalsIgnoreCase("7")) {
						if (pc.getInventory().checkEnchantItem(20011, 9, 2)) {
							if (random.nextInt(10) > 7) { // 30%의 확률로 성공
								pc.getInventory().consumeEnchantItem(20011, 9, 1);
								pc.getInventory().consumeEnchantItem(20011, 9, 1);
								인첸트지급(pc, 222325, 1, 8);
								pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
								Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
								pc.sendPackets(new S_SystemMessage("대성공! +8 축복 받은 신성한 마법 방어 투구를 얻었습니다."));
							} else { // 나머지 확률은 걍 신성투구
								pc.getInventory().consumeEnchantItem(20011, 9, 1);
								pc.getInventory().consumeEnchantItem(20011, 9, 1);
								인첸트지급(pc, 222324, 1, 8);
							}
							htmlid = "";
						} else {
							pc.sendPackets(new S_SystemMessage("+9 마법 방어 투구(2) 필요합니다."));
						}
					}else if (s.equalsIgnoreCase("8")) {
						if (pc.getInventory().checkEnchantItem(20011, 10, 2)) {
							if (random.nextInt(10) > 7) { // 30%의 확률로 성공
								pc.getInventory().consumeEnchantItem(20011, 10, 1);
								pc.getInventory().consumeEnchantItem(20011, 10, 1);
								인첸트지급(pc, 222325, 1, 9);
								pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
								Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
								pc.sendPackets(new S_SystemMessage("대성공! +9 축복 받은 신성한 마법 방어 투구를 얻었습니다."));
							} else { // 나머지 확률은 걍 신성투구
								pc.getInventory().consumeEnchantItem(20011, 10, 1);
								pc.getInventory().consumeEnchantItem(20011, 10, 1);
								인첸트지급(pc, 222324, 1, 9);
							}
							htmlid = "";
						} else {
							pc.sendPackets(new S_SystemMessage("+10 마법 방어 투구(2) 필요합니다."));
						}
					}
				//기란마을 헥터
			} else if (npcid == 70642) {
				if (s.equalsIgnoreCase("a")) {// 오우거의도끼
					if (pc.getInventory().checkEnchantItem(203005, 9, 1) && pc.getInventory().checkItem(820010)
							&& pc.getInventory().checkItem(40513, 5) && pc.getInventory().checkItem(40308, 1000000)) {
						pc.getInventory().consumeEnchantItem(203005, 9, 1);
						pc.getInventory().consumeItem(820010, 1);
						pc.getInventory().consumeItem(40513, 5);
						pc.getInventory().consumeItem(40308, 1000000);
						인첸트지급(pc, 203006, 1, 0);
						htmlid = "";
					} else {
//						+9 산적의 도끼 1개 + 봉인된 오우거의 도끼 1개 + 오우거의 눈물 5개 + 100만 아데나
						pc.sendPackets(new S_SystemMessage("제작 아이템이 부족합니다."));
					}
				} else if (s.equalsIgnoreCase("b")) {// 산적의도끼
					if (pc.getInventory().checkEnchantItem(203015, 9, 1) && pc.getInventory().checkItem(820011)) {
						pc.getInventory().consumeEnchantItem(203015, 9, 1);
						pc.getInventory().consumeItem(820011, 1);
						인첸트지급(pc, 203005, 1, 0);
						htmlid = "";
					} else {
//						+9 질풍의 도끼 1개 + 봉인된 산적의 도끼
						pc.sendPackets(new S_SystemMessage("제작 아이템이 부족합니다."));
					}
				}
				
				//라이라 리뉴얼
			} else if (npcid == 70811) {
				if (s.equals("a")) {
					if (pc.getInventory().checkItem(7022,1000)) {
						pc.getInventory().consumeItem(7022,1000);
						pc.getInventory().storeItem(40308, 200000);
						htmlid = "";
					} else {
						htmlid = "orcfnoname10";
					}
				} else if (s.equals("b")) {
					if (pc.getInventory().checkItem(7022,3000)) {
						pc.getInventory().consumeItem(7022,3000);
						pc.getInventory().storeItem(40308, 750000);
						htmlid = "";
					} else {
						htmlid = "orcfnoname10";
					}
				} else if (s.equals("c")) {
					if (pc.getInventory().checkItem(7022,10000)) {
						pc.getInventory().consumeItem(7022,10000);
						pc.getInventory().storeItem(40308, 3000000);
						htmlid = "";
					} else {
						htmlid = "orcfnoname10";
					}
				} else if (s.equals("d")) {
					if (pc.getInventory().checkItem(7022,20000)) {
						pc.getInventory().consumeItem(7022,20000);
						pc.getInventory().storeItem(40308, 10000000);
						htmlid = "";
					} else {
						htmlid = "orcfnoname10";
					}
				}
				// 아놀드
			} else if (npcid == 7){
				if (s.equals("a")) {// 일반보상
					if(pc.getLevel() >= 52){
						if(pc.getInventory().checkItem(30151, 1)){
							pc.getInventory().consumeItem(30151, 1);
							pc.getInventory().storeItem(30149, 1);
							레벨52기준보상경험치(pc, 1);
							htmlid = "anold3";
						} else {
							pc.sendPackets(new S_SystemMessage("훈련 완료 증표(1)개 필요합니다."));
							htmlid = "anold4";
						}
					} else {
						htmlid = "anold2";
					}
				} else if (s.equals("b")) {// 특별한보상
					if(pc.getLevel() >= 52){
						if(pc.getInventory().checkItem(30151, 1) && pc.getInventory().checkItem(1000004, 1)){
							pc.getInventory().consumeItem(30151, 1);
							pc.getInventory().consumeItem(1000004, 1);
							pc.getInventory().storeItem(30149, 1);
							레벨52기준보상경험치(pc, 2);
							htmlid = "anold3";
						} else {
							pc.sendPackets(new S_SystemMessage("훈련 완료 증표(1),드래곤의 다이아몬드(1)개 필요합니다."));
							htmlid = "anold4";
						}
					} else {
						htmlid = "anold2";
					}
				} else if (s.equals("c")) {// 빛나는 특별한보상
					if(pc.getLevel() >= 52){
						if(pc.getInventory().checkItem(30151, 1) && pc.getInventory().checkItem(1000007, 1)){
							pc.getInventory().consumeItem(30151, 1);
							pc.getInventory().consumeItem(1000007, 1);
							pc.getInventory().storeItem(30149, 1);
							레벨52기준보상경험치(pc, 3);
							htmlid = "anold3";
						} else {
							pc.sendPackets(new S_SystemMessage("훈련 완료 증표(1), 드래곤의 고급 다이아몬드(1)개 필요합니다."));
							htmlid = "anold4";
						}
					} else {
						htmlid = "anold2";
					}
				}
				//나루터
			} else if (npcid == 9){
				if (s.equals("a")) {// 일반보상
					if(pc.getLevel() >= 30){
						if(pc.getInventory().checkItem(9992, 5) && pc.getInventory().checkItem(9993, 1)){
							pc.getInventory().consumeItem(9992, 5);
							pc.getInventory().consumeItem(9993, 1);
							pc.getInventory().storeItem(9994, 1);
							레벨52기준보상경험치(pc, 1);
							htmlid = "naruto3";
						} else {
							pc.sendPackets(new S_SystemMessage("제작 아이템이 부족합니다."));
							htmlid = "naruto4";
						}
					} else {
						htmlid = "naruto2";
					}
				} else if (s.equals("b")) {// 특별한보상
					if(pc.getLevel() >= 30){
						if(pc.getInventory().checkItem(9992, 5) && pc.getInventory().checkItem(9993, 1) && pc.getInventory().checkItem(1000004, 1)){
							pc.getInventory().consumeItem(9992, 5);
							pc.getInventory().consumeItem(9993, 1);
							pc.getInventory().consumeItem(1000004, 1);
							pc.getInventory().storeItem(9994, 1);
							레벨52기준보상경험치(pc, 1);
							htmlid = "naruto3";
						} else {
							pc.sendPackets(new S_SystemMessage("제작 아이템이 부족합니다."));
							htmlid = "naruto4";
						}
					} else {
						htmlid = "naruto2";
					}
				} else if (s.equals("c")) {// 빛나는 특별한보상
					if(pc.getLevel() >= 30){
						if(pc.getInventory().checkItem(9992, 5) && pc.getInventory().checkItem(9993, 1) && pc.getInventory().checkItem(1000007, 1)){
							pc.getInventory().consumeItem(9992, 5);
							pc.getInventory().consumeItem(9993, 1);
							pc.getInventory().consumeItem(1000007, 1);
							pc.getInventory().storeItem(9994, 1);
							레벨52기준보상경험치(pc, 1);
							htmlid = "naruto3";
						} else {
							pc.sendPackets(new S_SystemMessage("제작 아이템이 부족합니다."));
							htmlid = "naruto4";
						}
					} else {
						htmlid = "naruto2";
					}
				}
				// 알드란	
			} else if (npcid == 80077) {
				if (s.equals("a")) {
					if (pc.getInventory().checkItem(41207, 1)) {
						new L1Teleport().teleport(pc, 32674, 32871, (short) 550, 0, true);
						htmlid = "";
					} else {
						htmlid = "aldran9";
					}
				} else if (s.equals("b")) {
					if (pc.getInventory().checkItem(41207, 1)) {
						new L1Teleport().teleport(pc, 32778, 33009, (short) 550, 0, true);
						htmlid = "";
					} else {
						htmlid = "aldran9";
					}
				} else if (s.equals("c")) {
					if (pc.getInventory().checkItem(41207, 1)) {
						new L1Teleport().teleport(pc, 32471, 32766, (short) 550, 0, true);
						htmlid = "";
					} else {
						htmlid = "aldran9";
					}
				} else if (s.equals("d")) {
					if (pc.getInventory().checkItem(41207, 1)) {
						new L1Teleport().teleport(pc, 32511, 32998, (short) 550, 0, true);
						htmlid = "";
					} else {
						htmlid = "aldran9";
					}
				} else if (s.equals("e")) {
					if (pc.getInventory().checkItem(41207, 1)) {
						new L1Teleport().teleport(pc, 32998, 33028, (short) 558, 0, true);
						htmlid = "";
					} else {
						htmlid = "aldran9";
					}
				}
				// 수도르		
			} else if (npcid == 7310101) {
				if (s.equals("Tel_B_AREA")) { //남쪽
					if (pc.getInventory().checkItem(40308, 5000)) {
						new L1Teleport().teleport(pc, 32661, 33003, (short) 1708, 0,true); // 남쪽지역
						pc.getInventory().consumeItem(40308, 5000);
						htmlid = "";
					} else {
						htmlid = "soodor_fl";
					}
				} else if (s.equals("Tel_A_AREA")) { //서쪽 
					if (pc.getInventory().checkItem(40308, 5000)) {
						new L1Teleport().teleport(pc, 32628, 32688, (short) 1708, 4,true); // 서쪽지역
						pc.getInventory().consumeItem(40308, 5000);
						htmlid = "";
					} else {
						htmlid = "soodor_fl";
					}
				} else if (s.equals("Tel_C_AREA")) { //동쪽
					if (pc.getInventory().checkItem(40308, 5000)) {
						new L1Teleport().teleport(pc, 32905, 32955, (short) 1708, 4,true); // 동쪽지역
						pc.getInventory().consumeItem(40308, 5000);
						htmlid = "";
					} else {
						htmlid = "soodor_fl";
					}
				}

				/** 투석기 **/
				
			} else if (npcid == 7000082 || npcid == 7000083 || npcid == 7000084 || npcid == 7000085
					|| npcid == 7000086 || npcid == 7000087){
				if (s.equalsIgnoreCase("0-5") //외성문 방향으로 발사!
						||  s.equalsIgnoreCase("0-6") //내성문 방향으로 발사!
						||  s.equalsIgnoreCase("0-7") //수호탑 방향으로 발사!
						||  s.equalsIgnoreCase("1-16") //외성문 방향으로 침묵포탄 발사!
						||  s.equalsIgnoreCase("1-17") //내성문 앞쪽으로 침묵포탄 발사!
						||  s.equalsIgnoreCase("1-18") //내성문 좌측으로 침묵포탄 발사!
						||  s.equalsIgnoreCase("1-19") //내성문 우측으로 침묵포탄 발사!
						||  s.equalsIgnoreCase("1-20") //수호탑 방향으로 침묵포탄 발사!
						// 수성
						||  s.equalsIgnoreCase("0-9") //외성문 방향으로 발사!
				) {
					int locx = 0;
					int locy = 0;
					int gfxid = 0;
					int castleid = 0;
					int npcId = ((L1NpcInstance) obj).getNpcTemplate().get_npcId();
					if (s.equalsIgnoreCase("0-5")) { //외성문 방향으로 발사!
						switch(npcId) {
						case 7000086: //5시 방향 공성 오크요새 공성측
							locx = 32795;
							locy = 32315;
							gfxid = 12197; //우측
							castleid = 2;
							break;
						case 7000082: //5시 방향 공성 기란성 공성측
							locx = 33632;
							locy = 32731;
							gfxid = 12197; //우측
							castleid = 4;
							break;
						case 7000084: //7시 방향 공성 켄트성 공성측
							locx = 33114;
							locy = 32771;
							gfxid = 12193; //좌측
							castleid = 1;
							break;
						}
					} else if (s.equalsIgnoreCase("0-6")) { //내성문 방향으로 발사!
						switch(npcId) {
						case 7000086: //11시 방향 공성 오크요새 공성측
							locx = 32798;
							locy = 32268;
							gfxid = 12197; //우측
							castleid = 2;
							break;
						case 7000082: //11시 방향 공성 기란성 공성측
							locx = 33632;
							locy = 32664;
							gfxid = 12197; //우측
							castleid = 4;
							break;
						case 7000084: //2시 방향 공성 켄트성 공성측
							locx = 33171;
							locy = 32763;
							gfxid = 12197; //좌측
							castleid = 1;
							break;
						}
					} else if (s.equalsIgnoreCase("0-7")) { //수호탑 방향으로 발사!
						switch(npcId) {
						case 7000086: //11시 방향 공성 오크요새 공성측
							locx = 32798;
							locy = 32285;
							gfxid = 12197; //우측
							castleid = 2;
							break;
						case 7000082: //11시 방향 공성 기란성 공성측
							locx = 33631;
							locy = 32678;
							gfxid = 12197; //우측
							castleid = 4;
							break;
						case 7000084: //2시 방향 공성 켄트성 공성측
							locx = 33168;
							locy = 32779;
							gfxid = 12197; //좌측
							castleid = 1;
							break;
						}
					} else if (s.equalsIgnoreCase("0-9")) { //외성문 방향으로 발사!
						int pcCastleId = 0;
						if (pc.getClanid() != 0) {
							L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
							if (clan != null) {
								pcCastleId = clan.getCastleId();
							}
						}
						switch(npcId) {
						case 7000087: //11시 방향 공성 오크요새 수성측
							if (isExistDefenseClan(L1CastleLocation.OT_CASTLE_ID)) {
								if (pcCastleId != L1CastleLocation.OT_CASTLE_ID) {
									pc.sendPackets(new S_ServerMessage(3682)); 
									//투석기 사용: 실패(성을 수호하는 성혈 군주만 사용 가능)
									return htmlid;
								}
							}
							locx = 32794;
							locy = 32320;
							gfxid = 12193; //우측
							castleid = 2;
							break;
						case 7000083: //11시 방향 공성 기란성 수성측
							if (isExistDefenseClan(L1CastleLocation.GIRAN_CASTLE_ID)) {
								if (pcCastleId != L1CastleLocation.GIRAN_CASTLE_ID) {
									pc.sendPackets(new S_ServerMessage(3682)); 
									//투석기 사용: 실패(성을 수호하는 성혈 군주만 사용 가능)
									return htmlid;
								}
							} 
							locx = 33631;
							locy = 32738;
							gfxid = 12193; //우측
							castleid = 4;
							break;
						case 7000085: //2시 방향 공성 켄트성 수성측
							if (isExistDefenseClan(L1CastleLocation.KENT_CASTLE_ID)) {
								if (pcCastleId != L1CastleLocation.KENT_CASTLE_ID) {
									pc.sendPackets(new S_ServerMessage(3682)); 
									//투석기 사용: 실패(성을 수호하는 성혈 군주만 사용 가능)
									return htmlid;
								}
							}
							locx = 33107;
							locy = 32770;
							gfxid = 12197; //우측
							castleid = 1;
							break;
						}
						
					/*	<a action="1-16">외성문 방향으로 침묵포탄 발사!</a><br>
						 <a action="1-17">내성문 앞쪽으로 침묵포탄 발사!</a><br>
						 <a action="1-18">내성문 좌측으로 침묵포탄 발사!</a><br>
						 <a action="1-19">내성문 우측으로 침묵포탄 발사!</a><br>
						 <a action="1-20">수호탑 방향으로 침묵포탄 발사!</a><br><br>
					} else if (s.equalsIgnoreCase("0-9")) { //외성문 방향으로 침묵포탄 발사!*/
						
					} else {
						pc.sendPackets(new S_SystemMessage("침묵포탄은 사용 불가능 합니다.")); 
						return htmlid;
					}

					boolean isNowWar = false;
					isNowWar = WarTimeController.getInstance().isNowWar(castleid);
					if (!isNowWar) {
						pc.sendPackets(new S_ServerMessage(3683)); 
						//투석기 사용: 실패(공성 시간에만 사용 가능)
						return htmlid;
					}
//
					boolean inWar = false;
					List<L1War> warList = L1World.getInstance().getWarList();
					for (L1War war : warList) {
						if (war.CheckClanInWar(pc.getClanname())) {
							inWar = true;
							break;
						}
					}
					if (!(pc.isCrown()&& inWar && isNowWar)) {
						pc.sendPackets(new S_ServerMessage(3681)); 
						//투석기 사용: 실패(전쟁을 선포한 군주만 사용 가능)
						return htmlid; 
					}
					if (pc.getlastShellUseTime() + 10000L > System.currentTimeMillis()) {
						pc.sendPackets(new S_ServerMessage(3680)); 
						//투석기 사용: 실패(재장전 시간 필요)
						return htmlid;
					}
					
					if (obj != null) {
						if (obj instanceof L1CataInstance) {
							L1CataInstance npc = (L1CataInstance) obj;
							if (pc.getInventory().consumeItem(30124, 1)) {
								Broadcaster.broadcastPacket(npc, new S_DoActionGFX(npc.getId(), ActionCodes.ACTION_Attack));
								S_EffectLocation packet = new S_EffectLocation(locx, locy, gfxid);
								pc.sendPackets(packet);
								Broadcaster.wideBroadcastPacket(pc, packet, 100);
								getShellDmg(locx, locy);
								//침묵포탄(locx, locy); // 침묵포탄 테스트
								pc.updatelastShellUseTime();
							} else {
								pc.sendPackets(new S_ServerMessage(337, "$16785"));
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return htmlid;
	}
	
	private boolean 인첸트지급(L1PcInstance pc, int item_id, int count, int EnchantLevel) {
		L1ItemInstance item = ItemTable.getInstance().createItem(item_id);
		if (item != null) {
			item.setCount(count);
			item.setEnchantLevel(EnchantLevel);
			item.setIdentified(true);
			if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
				pc.getInventory().storeItem(item);
			} else { 
				pc.sendPackets(new S_ServerMessage(82)); 
				// 무게 게이지가 부족하거나 인벤토리가 꽉차서 더 들 수 없습니다. 
				return false; 
			}
			pc.sendPackets(new S_ServerMessage(403, item.getLogName())); // %0를 손에 넣었습니다.
			return true;
		} else {
			return false;
		}
	}
	
	private void 아크프리패스(L1PcInstance pc, int type) {
		int needExp = ExpTable.getNeedExpNextLevel(52);
		double exppenalty = ExpTable.getPenaltyRate(pc.getLevel());
		int exp = 0;
		if (type == 1) {
			exp = (int) (needExp * 0.03D * exppenalty); //올리면 경험치가 더 많이 상승 [vl:32]
		} else {
			pc.sendPackets(new S_SystemMessage("잘못된 요구입니다.")); 
		}
		pc.addExp(exp);
		Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 3944));
		pc.sendPackets(new S_SkillSound(pc.getId(), 3944));
	}
	
	private void 레벨52기준보상경험치(L1PcInstance pc, int type) {
		int needExp = ExpTable.getNeedExpNextLevel(52);
		double exppenalty = ExpTable.getPenaltyRate(pc.getLevel());
		int exp = 0;
		if (type == 1) {
			exp = (int) (needExp * 0.02D * exppenalty);
		} else if (type == 2) {
			exp = (int) (needExp * 0.05D * exppenalty);
		} else if (type == 3) {
			exp = (int) (needExp * 0.20D * exppenalty);
		} else {
			pc.sendPackets(new S_SystemMessage("잘못된 요구입니다.")); 
		}
		pc.addExp(exp);
		Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 3944));
		pc.sendPackets(new S_SkillSound(pc.getId(), 3944));
	}
	
	private void 에킨스경험치2(L1PcInstance pc) {
		int needExp = ExpTable.getNeedExpNextLevel(52);
		double exppenalty = ExpTable.getPenaltyRate(pc.getLevel());
		int exp = 0;
		if (pc.getLevel() <= 60) {
			exp = (int) (needExp * 0.04D);
		} else if (pc.getLevel() <= 65) {
			exp = (int) (needExp * 0.03D);
		} else if (pc.getLevel() <= 70) {
			exp = (int) (needExp * 0.02D);
		} else if (pc.getLevel() <= 75) {
			exp = (int) (needExp * 0.01D);
		} else {
			exp = (int) (needExp * 16D * exppenalty);
		}
		pc.addExp(exp);
		Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 3944));
		pc.sendPackets(new S_SkillSound(pc.getId(), 3944));
	}

	private void 에킨스경험치6(L1PcInstance pc) {
		int needExp = ExpTable.getNeedExpNextLevel(52);
		double exppenalty = ExpTable.getPenaltyRate(pc.getLevel());
		int exp = 0;
		if (pc.getLevel() <= 60) {
			exp = (int) (needExp * 0.12D);
		} else if (pc.getLevel() <= 65) {
			exp = (int) (needExp * 0.09D);
		} else if (pc.getLevel() <= 70) {
			exp = (int) (needExp * 0.06D);
		} else if (pc.getLevel() <= 75) {
			exp = (int) (needExp * 0.03D);
		} else {
			exp = (int) (needExp * 48D * exppenalty);
		}
		pc.addExp(exp);
		Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 3944));
		pc.sendPackets(new S_SkillSound(pc.getId(), 3944));
	}
	
	private boolean createNewItem(L1PcInstance pc, String npcName, int item_id, int count, int enchant) {
		L1ItemInstance item = ItemTable.getInstance().createItem(item_id);
		if (item != null) {
			item.setCount(count);
			item.setEnchantLevel(enchant);
			if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
				pc.getInventory().storeItem(item);
			} else {
				L1World.getInstance().getInventory(pc.getX(), pc.getY(), pc.getMapId()).storeItem(item);
			}
			pc.sendPackets(new S_ServerMessage(143, npcName, item.getLogName()));
			return true;
		} else {
			return false;
		}
	}

	private boolean isExistDefenseClan(int castleId) {
		boolean isExistDefenseClan = false;
		for (L1Clan clan : L1World.getInstance().getAllClans()) {
			if (castleId == clan.getCastleId()) {
				isExistDefenseClan = true;
				break;
			}
		}
		return isExistDefenseClan;
	}
	
	private void getShellDmg(int locx, int locy) {
		L1PcInstance targetPc = null;
		L1NpcInstance targetNpc = null;
		L1EffectInstance effect = L1EffectSpawn.getInstance().spawnEffect(81154, 1 * 1000, locx, locy, (short) 4);
		for (L1Object object : L1World.getInstance().getVisibleObjects(effect, 3)) {
			if (object == null) {
				continue;
			}
			if (!(object instanceof L1Character)) {
				continue;
			}
			if (object.getId() == effect.getId()) {
				continue;
			}

			if (object instanceof L1PcInstance) {
				targetPc = (L1PcInstance) object;
				targetPc.sendPackets(new S_DoActionGFX(targetPc.getId(), ActionCodes.ACTION_Damage));
				Broadcaster.broadcastPacket(targetPc, new S_DoActionGFX(targetPc.getId(), ActionCodes.ACTION_Damage));
				targetPc.receiveDamage(targetPc, 100, 3);
			} else if (object instanceof L1SummonInstance
					|| object instanceof L1PetInstance) {
				targetNpc = (L1NpcInstance) object;
				Broadcaster.broadcastPacket(targetNpc, new S_DoActionGFX(targetNpc.getId(), ActionCodes.ACTION_Damage));
				targetNpc.receiveDamage(targetNpc, (int)100);
			}
		}
	}
	
	private void 침묵포탄(int locx, int locy) {
		L1PcInstance targetPc = null;
		L1NpcInstance targetNpc = null;
		L1EffectInstance effect = L1EffectSpawn.getInstance().spawnEffect(81154, 1 * 1000, locx, locy, (short) 4);
		for (L1Object object : L1World.getInstance().getVisibleObjects(effect, 3)) {
			if (object == null) {
				continue;
			}
			if (!(object instanceof L1Character)) {
				continue;
			}
			if (object.getId() == effect.getId()) {
				continue;
			}

			if (object instanceof L1PcInstance) {
				targetPc = (L1PcInstance) object;
				targetPc.sendPackets(new S_DoActionGFX(targetPc.getId(), ActionCodes.ACTION_Damage));
				Broadcaster.broadcastPacket(targetPc, new S_DoActionGFX(targetPc.getId(), ActionCodes.ACTION_Damage));
				targetPc.setSkillEffect(L1SkillId.SILENCE, 15);
				targetPc.sendPackets(new S_PacketBox(S_PacketBox.POSION_ICON, targetPc, 6, 15));
				Broadcaster.broadcastPacket(targetPc, new S_PacketBox(S_PacketBox.POSION_ICON, targetPc, 6, 15));
				Broadcaster.broadcastPacket(targetPc, new S_SkillSound(targetPc.getId(), 10708));
			} else if (object instanceof L1SummonInstance
					|| object instanceof L1PetInstance) {
				targetNpc = (L1NpcInstance) object;
				Broadcaster.broadcastPacket(targetNpc, new S_DoActionGFX(targetNpc.getId(), ActionCodes.ACTION_Damage));
				//targetNpc.receiveDamage(targetNpc, (int)100);
				targetNpc.setSkillEffect(L1SkillId.SILENCE, 15);
			}
		}
	}

}
