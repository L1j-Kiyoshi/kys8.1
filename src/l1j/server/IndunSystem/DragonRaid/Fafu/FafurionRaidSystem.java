package l1j.server.IndunSystem.DragonRaid.Fafu;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Random;

import l1j.server.IndunSystem.DragonRaid.Anta.AntarasRaidSpawn;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.utils.L1SpawnUtil;

public class FafurionRaidSystem {

	private static FafurionRaidSystem _instance;

	private final Map<Integer, FafurionRaid> _list = new ConcurrentHashMap<Integer, FafurionRaid>();

	private final ArrayList<Integer> _map = new ArrayList<Integer>();

	public static FafurionRaidSystem getInstance() {
		if (_instance == null) { _instance = new FafurionRaidSystem(); }
		return _instance;
	}

	public FafurionRaidSystem(){  _map.add(1011);  }
	static class FafurionMsgTimer implements Runnable {

		private int _mapid = 0; 
		private int _type = 0; 
		private int _stage = 0;

		public FafurionMsgTimer(int mapid, int type, int stage){ 
			_mapid = mapid; 
			_type = type; 
			_stage = stage;			
		}

		@Override
		public void run() {
			try {
				/**Lair = 파푸방 , Romm1 = 1번방, Room2 = 2번방, Room3= 3번방 Room4 = 4번방*/
				ArrayList<L1PcInstance> Lair = FafurionRaidSystem.getInstance().getAR(_mapid).getRoomList(5);
				switch(_type){
				/**
				1차 레어 입장 
				1657 : 파푸리온 : 감히 나의 영역에 들어오다니... 용기가 가상하구나..
				1658 : 무녀사엘 : 이 비열한 파푸리온! 이제 나를 속인 댓가를 치루게 될 것이다!
				1659 : 파푸리온 : 봉인을 풀 때 네가 큰 도움이 되었지만..나에게 두 번의 자비는 없다..				
				1660 : 무녀 사엘 : 그때는 네 녀석이 내 뼈 속까지 저주를 내렸지만.. 지금은 다르다!
				1661 : 파푸리온 : 가소롭구나! 저둘이 너와 함께 이승을 떠돌게 될 나의 제물들인 거이냐!
				1662 : 무녀사엘 : 용사들이여! 저 사악한 파푸리온을 물리치고 에바 왕국에 내려진 피의 저주를 부디 풀어 주소서!
				 */
				case 1:
					for(int i =0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){ pc.sendPackets(new S_ServerMessage(1657)); }
					}Thread.sleep(3000);
					for(int i =0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){ pc.sendPackets(new S_ServerMessage(1658)); }
					}Thread.sleep(3000);
					for(int i =0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){ pc.sendPackets(new S_ServerMessage(1659)); }
					}Thread.sleep(3000);
					for(int i =0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){ pc.sendPackets(new S_ServerMessage(1660)); }
					}Thread.sleep(3000);
					for(int i =0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){ pc.sendPackets(new S_ServerMessage(1661)); }
					}Thread.sleep(3000);
					for(int i =0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){ pc.sendPackets(new S_ServerMessage(1662)); }
					}Thread.sleep(3000);
					for(int i =0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){ pc.sendPackets(new S_ChatPacket(pc,"시스템 메세지 : 파푸리온을 공략 하실수 있습니다.")); }
					}Thread.sleep(3000);
					AntarasRaidSpawn.getInstance().fillSpawnTable(_mapid, _stage);
					break;
					/** 1차 죽은후 메시지 방출 
					1663 : 파푸리온 : 놀잇감으로는 충분하구나! 흐흐흐...
					1664 : 파푸리온 : 뼈 속까지 파고드는 두려움이 무엇인지 이몸이 알게 해주마!
					 */
				case 2:
					for(int i =0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){ pc.sendPackets(new S_ServerMessage(1663)); }
					}Thread.sleep(20000);
					for(int i =0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){ pc.sendPackets(new S_ServerMessage(1664)); }
					}
					break;
					/** 2차 죽은후 메시지 방출  
					1665 : 무녀사엘 : 이제 파푸리온의 힘이 많이 떨어진것 같습니다! 용사들이어 조금 더 힘을 내주소서!
					1666 : 파푸리온 : 네 놈들이 희망이라는 부르는 것이, 단지 헛된 몽상이었음을 알게 해주마! 
					1667 : 파푸리온 : 사엘과 함께 한것을 후회하게 될 것이다! 어리석은 존재들이여...
					 */
				case 3:
					for(int i =0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){ pc.sendPackets(new S_ServerMessage(1665)); }
					}Thread.sleep(3000);
					for(int i =0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){ pc.sendPackets(new S_ServerMessage(1666)); }
					}Thread.sleep(20000);
					for(int i =0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){ pc.sendPackets(new S_ServerMessage(1667)); } }
					break;
					/** 3차  죽은후 메세지 방출 
				1668 : 파푸리온 : 사엘.. 네 녀석이..어떻게...나의 어머니..실렌이시여 나의 숨을..거두소서...
				1669 : 무녀사엘 : 감사합니다.. 당신들은 역시 아덴 최고의 용사들입니다. 드디어.. 에바 왕국의 오랜 저주가 풀릴 수 있을 것 같습니다.
				1682 : 카임사무의 외침 : 파푸리온의 검은 숨결을 멈추게 한 용사들이 탄생 하였습니다.!!		
				1641 : 난쟁이의 외침 : 파푸리온의 검은 숨결을 멈추게 한 용사들이 탄생 하였습니다.!!		
					 */
				case 4:
					for (int i = 0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						pc.removeSkillEffect(L1SkillId.FAFU_BUFF);
						pc.killSkillEffectTimer(L1SkillId.FAFU_BUFF);
						if (pc.isPrivateShop()){
							continue;
						}
						if (pc.isAutoClanjoin()){
							continue;
						}
						if (pc.getMapId() == _mapid){
							pc.setSkillEffect(L1SkillId.FAFU_BUFF, 10800 * 1000);
							pc.addHpr(3);
							pc.addMpr(1);
							pc.getResistance().addWind(50);
							pc.sendPackets(new S_OwnCharStatus(pc));
							pc.sendPackets(new S_SkillSound(pc.getId(), 7783));
							pc.broadcastPacket(new S_SkillSound(pc.getId(), 7783));
							pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONBLOOD, 85, 10800/60)); 
							pc.sendPackets(new S_ServerMessage(1644));						
						}
					}
					Thread.sleep(5000);
					for(int i =0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){ pc.sendPackets(new S_ServerMessage(1668)); }
					}Thread.sleep(3000);
					for(int i =0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){ pc.sendPackets(new S_ServerMessage(1669)); } 
					}Thread.sleep(3000);
					for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
						pc.sendPackets(new S_ServerMessage(1682));
						pc.sendPackets(new S_ServerMessage(1641));
					} Thread.sleep(5000);
					for (L1Object obj : L1World.getInstance().getVisibleObjects(_mapid).values()){
						if (obj instanceof L1MonsterInstance){
							L1MonsterInstance mob = (L1MonsterInstance) obj;
							if (mob.getNpcId() == 900040){
								Mapdrop(mob);
							}
						}
					}
					//				for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
					//						if(pc.isPrivateShop()){
					//							continue;
					//						}
					//						pc.setSkillEffect(L1SkillId.FAFU_BUFF, 18000 * 1000);
					//						pc.addHpr(3);
					//						pc.addMpr(1);
					//						pc.getResistance().addWind(50);
					//						pc.sendPackets(new S_OwnCharStatus(pc));
					//						pc.sendPackets(new S_SkillSound(pc.getId(), 7783));
					//						pc.broadcastPacket(new S_SkillSound(pc.getId(), 7783));
					//						pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONBLOOD, 85, 18000/60)); 
					//						pc.sendPackets(new S_ServerMessage(1644));
					//					} Thread.sleep(3000);
					for(int i =0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){ pc.sendPackets(new S_ChatPacket(pc,"시스템 메시지 : 잠시 후 마을로 텔레포트 됩니다."));
						System.out.println("■■■■■■■■■■ 파푸리온 레이드 종료 ■■■■■■■■■■");
						}
					} Thread.sleep(15000);
					for(int i =0; i < Lair.size(); i++){ // [성공 기란 텔 ]
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){
							Random random = new Random();
							int Dragontel = random.nextInt(3)+ 1;
							if(Dragontel == 1) new L1Teleport().teleport(pc, 33440, 32817, (short)4, 5, true); // 기란 
							else if(Dragontel == 2) new L1Teleport().teleport(pc, 33436, 32800, (short)4, 5, true); // 기란 
							else new L1Teleport().teleport(pc, 33443, 32798, (short)4, 5, true); // 기란						
						}
					} break;
				default: 
					break; 
				}
			} catch (Exception exception) {}
		}
		
		// 자동 분배
		private void Mapdrop(L1NpcInstance npc){
			L1Inventory inventory = npc.getInventory();
			L1ItemInstance item;
			L1Inventory targetInventory = null;
			L1PcInstance player;
			Random random = new Random();
			L1PcInstance acquisitor;
			ArrayList<L1PcInstance> acquisitorList = new ArrayList<L1PcInstance>();
			L1PcInstance[] pclist = L1World.getInstance().getAllPlayers3();
			for(L1PcInstance temppc : pclist){
				if(temppc.getMapId() == npc.getMapId())
					acquisitorList.add(temppc);
			}
			for (int i = inventory.getSize(); i > 0; i--) {
				item = inventory.getItems().get(0);

				if (item.getItem().getType2() == 0 && item.getItem().getType() == 2) {
					item.setNowLighting(false);
				}
				acquisitor = acquisitorList.get(random.nextInt(acquisitorList.size()));
				if (acquisitor.getInventory().checkAddItem(item,item.getCount()) == L1Inventory.OK) {
						targetInventory = acquisitor.getInventory();
						player = acquisitor;
						L1ItemInstance l1iteminstance = player.getInventory().findItemId(L1ItemId.ADENA); // 소지
						if (l1iteminstance != null&& l1iteminstance.getCount() > 2000000000) {
								targetInventory = L1World.getInstance().getInventory(acquisitor.getX(),acquisitor.getY(),acquisitor.getMapId()); // 가질 수
								player.sendPackets(new S_ServerMessage(166,"소지하고 있는 아데나","2,000,000,000을 초과하고 있습니다."));
						}else{
							for(L1PcInstance temppc : acquisitorList){
									temppc.sendPackets(new S_ServerMessage(813, npc.getName(), item.getLogName(), player.getName()));
							}
						}
				} else {
						targetInventory = L1World.getInstance().getInventory(acquisitor.getX(),acquisitor.getY(),acquisitor.getMapId()); // 가질 수
				}
				inventory.tradeItem(item, item.getCount(), targetInventory);
			}
			npc.getLight().turnOnOffLight();
		}
	}

	public void startRaid(L1PcInstance pc){
		int id = blankMapId();
		if (id != 1011) L1WorldMap.getInstance().cloneMap(1011, id);
		FafurionRaid ar = new FafurionRaid(id);
		AntarasRaidSpawn.getInstance().fillSpawnTable(id, 2); // 생성된 포탈맵에 [스폰 파푸리온 / 2번타입스폰]
		System.out.println("■■■■■■■■■■ 파푸리온 레이드 시작 ■■■■■■■■■■ MAP - " + _map);
		L1SpawnUtil.spawn2(pc.getX(), pc.getY(), pc.getMapId(), 900036, 0, 3600 * 1000, id); // 파푸리온 스폰 7200
		L1SpawnUtil.spawn2(32941, 32670, (short)id, 900037, 0, 3600 * 1000, id);//[파푸리온 대기방] => 파푸레어
		L1SpawnUtil.spawn2(32941, 32671, (short)id, 900037, 0, 3600 * 1000, id);//[파푸리온 대기방] => 파푸레어
		_list.put(id, ar);
		FafurionRaidTimer RaidEndTime = new FafurionRaidTimer(ar, 5, 0, 1800 * 1000);// 2시간 7200
		RaidEndTime.begin();
	}

	public FafurionRaid getAR(int id){ return _list.get(id); }

	public int blankMapId(){
		int mapid = 0;
		if(_list.size() == 0) return 1011;
		mapid = 6501 + _list.size();
		return mapid;
	}
	/** 포탈 갯수 */
	public int countRaidPotal1(){ return _list.size(); }

}
