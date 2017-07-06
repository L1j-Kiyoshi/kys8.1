package l1j.server.IndunSystem.DragonRaid.Anta;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Random;

import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
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

public class AntarasRaidSystem {	

	private static AntarasRaidSystem _instance;

	private final Map<Integer, AntarasRaid> _list = new ConcurrentHashMap<Integer, AntarasRaid>();

	private final ArrayList<Integer> _map = new ArrayList<Integer>();

	public static AntarasRaidSystem getInstance() {
		if (_instance == null) { _instance = new AntarasRaidSystem(); }
		return _instance;
	}

	public AntarasRaidSystem(){  _map.add(1005);  }

	static class AntarasMsgTimer implements Runnable {

		private int _mapid = 0; 
		private int _type = 0; 
		private int _stage = 0;

		public AntarasMsgTimer(int mapid, int type, int stage){ 
			_mapid = mapid; 
			_type = type; 
			_stage = stage;			
		}

		@Override
		public void run() {
			try {
				/**Lair = 안타방 , Romm1 = 1번방, Room2 = 2번방, Room3= 3번방 Room4 = 4번방*/
				ArrayList<L1PcInstance> Lair = AntarasRaidSystem.getInstance().getAR(_mapid).getRoomList(5);
				switch(_type){
				/**1차 안타라스 나올때
				 * 1570	안타라스 : 나의 잠을 깨우는자! 누구인가? 
				 * 1571	크레이 : 안타라스! 너를 쫓아 이곳 칠흑의 어둠까지 왔다! 
				 * 1572	안타라스 : 가소롭군. 다시 한번 죽여주마, 크레이! 
				 * 1573	안타라스 : 어리석은 자여! 나의 분노를 자극하는 구나. 
				 * 1574	크레이 : 용사들이여 그대들의 칼에 아덴의 운명이 걸려있다. 
				 *              안타라스의 검은 숨결을 멈추게 할 자는 그대들 뿐이다! 
				 *
				 *              */
				case 1:
					for(int i =0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){ pc.sendPackets(new S_ServerMessage(1570)); }
					} Thread.sleep(3000);
					for(int i =0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){ pc.sendPackets(new S_ServerMessage(1571)); }
					}Thread.sleep(3000);
					for(int i =0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){ pc.sendPackets(new S_ServerMessage(1572)); }
					}Thread.sleep(3000);
					for(int i =0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){ pc.sendPackets(new S_ServerMessage(1573)); }
					}Thread.sleep(3000);
					for(int i =0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){ pc.sendPackets(new S_ServerMessage(1574)); }
					}Thread.sleep(3000);
					for(int i =0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){ pc.sendPackets(new S_ChatPacket(pc,"시스템 메세지 : 안타라스를 공략할 수 있습니다.")); }
					}Thread.sleep(3000);
					AntarasRaidSpawn.getInstance().fillSpawnTable(_mapid, _stage);
					break;
					/** 1차 죽은후 메세지
					 ** 1575	안타라스 : 이런 조무래기들로 나를 이길 수 있을 것 같은가! 크하하하.. 
					 ** 1576	안타라스 : 이제 맛있는 식사를 해볼까? 너희 피냄새가 나를 미치게 하는구나. */
				case 2:
					for(int i =0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){ pc.sendPackets(new S_ServerMessage(1575)); }
					}Thread.sleep(20000);
					for(int i =0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){ pc.sendPackets(new S_ServerMessage(1576)); }
					}
					break;
					/**2차 안타라스 죽은후 메세지
					 * 1577	크레이 : 우오오오옷! 피맺힌 원혼들의 외침이 들리지 않는가! 죽어랏! 
					 * 1578	안타라스 : 감히 나를 상대하려 하다니..그러고도 너희가 살길 바라느냐? 
					 * 1579	안타라스 : 나의 분노가 하늘에 닿았다. 이제 곧 나의 아버지가 나설 것이다. */
				case 3:
					for(int i =0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){ pc.sendPackets(new S_ServerMessage(1577)); }
					}Thread.sleep(3000);
					for(int i =0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){ pc.sendPackets(new S_ServerMessage(1578)); }
					}Thread.sleep(20000);
					for(int i =0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){ pc.sendPackets(new S_ServerMessage(1579)); } }
					break; 
					/**3차 안타라스 죽은후 메세지
					 * 1580 안타라스 : 황혼의 저주가 그대들에게 있을 지어다! 실렌이여, 나의 어머니여,나의 숨을.. 거두소서... 
					 * 1581 크레이 : 오오.. 최강의 용사임을 증명한 최고의 기사여! 
					 * 엄청난 시련을 이겨내고 당신의 손에 안타라스의 피를 묻혔는가! 드디어 이 원한을 풀겠구나. 
					 * 으하하하하!! 고맙다. 땅 위에 가장 강한 용사들이여! */
				case 4:
					for (int i = 0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						pc.removeSkillEffect(L1SkillId.ANTA_BUFF);
						pc.killSkillEffectTimer(L1SkillId.ANTA_BUFF);
						if (pc.isPrivateShop()){
							continue;
						}
						if (pc.isAutoClanjoin()){
							continue;
						}
						if (pc.getMapId() == _mapid){
							pc.setSkillEffect(L1SkillId.ANTA_BUFF, 10800 * 1000);
							pc.getAC().addAc(-2);
							pc.getResistance().addWind(50);
							pc.sendPackets(new S_OwnCharStatus(pc));
							pc.sendPackets(new S_SkillSound(pc.getId(), 7783));
							pc.broadcastPacket(new S_SkillSound(pc.getId(), 7783));
							pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONBLOOD, 82, 10800/60));
							pc.sendPackets(new S_ServerMessage(1628)); 						
						}
					}
					Thread.sleep(5000);
					for(int i =0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){ pc.sendPackets(new S_ServerMessage(1580)); }
					}Thread.sleep(3000);
					for(int i =0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){ pc.sendPackets(new S_ServerMessage(1581)); } 
					}Thread.sleep(5000);
					for (L1Object obj : L1World.getInstance().getVisibleObjects(_mapid).values()){
						if (obj instanceof L1MonsterInstance){
							L1MonsterInstance mob = (L1MonsterInstance) obj;
							if (mob.getNpcId() == 900013){
								Mapdrop(mob);
							}
						}
					}
					//					for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
					//						if(pc.isPrivateShop()){
					//							continue;
					//						}
					//						pc.removeSkillEffect(L1SkillId.ANTA_BUFF); //이미 스킬이 있는 경우 삭제후
					//						if (pc.getMapId() == _mapid){
					//							pc.setSkillEffect(L1SkillId.ANTA_BUFF, 18000 * 1000);// 재 스킬 생성을 실행해준다.
					//							pc.getAC().addAc(-2);
					//							pc.getResistance().addWater(50);
					//							pc.sendPackets(new S_OwnCharStatus(pc));
					//							pc.sendPackets(new S_SkillSound(pc.getId(), 7783));
					//							pc.broadcastPacket(new S_SkillSound(pc.getId(), 7783));
					//							pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONBLOOD, 82, 18000/60)); 
					//							pc.sendPackets(new S_ServerMessage(1628)); 
					//						}
					//					} Thread.sleep(3000);
					for(int i =0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){ pc.sendPackets(new S_ChatPacket(pc,"시스템 메시지 : 잠시 후 마을로 텔레포트 됩니다."));
//						System.out.println("■■■■■■■■■■ 안타라스 레이드 종료 ■■■■■■■■■■");
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
					}
					break;
					/**1582	난쟁이의 외침 : 웰던 마을에 숨겨진 용들의 땅으로 가는 문이 열렸습니다.
					 * 1583	난쟁이의 외침 : 숨겨진 용들의 땅으로 가는 문이 이미 웰던 마을에 열려 있습니다.
					 * 1593	난쟁이의 외침 : 안타라스의 검은 숨결을 멈추게 한 용사들이 탄생 하였습니다.!!
					 * 1608안타라스 : 네 녀석의 무모함도 여기까지다..! 이 곳에서 종말을 맞이하라!
					 * 1609	크레이 : 더 이상 소중한 용사들을 잃을 수는 없소. 마지막 남은 힘으로 이제 그대들을 소환하겠소.*/
				default: 
					break;  //클래스파일이..다업는거같은뎅?
				}
			} catch (Exception exception) {	}
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
		if (id != 1005) L1WorldMap.getInstance().cloneMap(1005, id);
		AntarasRaid ar = new AntarasRaid(id);
		AntarasRaidSpawn.getInstance().fillSpawnTable(id, 0);
//		System.out.println("■■■■■■■■■■ 안타라스 레이드 시작 ■■■■■■■■■■ MAP - " + _map);
		L1SpawnUtil.spawn2(pc.getX(), pc.getY(), pc.getMapId(), 900007, 0, 3600 * 1000, id); // 안타포탈 스폰
		L1SpawnUtil.spawn2(32703, 32669, (short)id, 900008, 0, 3600 * 1000, id);//[안타라스대기방] => 안타레어
		L1SpawnUtil.spawn2(32703, 32670, (short)id, 900008, 0, 3600 * 1000, id);//[안타라스대기방] => 안타레어
		L1SpawnUtil.spawn2(32623, 32725, (short)id, 810851, 0, 3600 * 1000, id);//[안타라스대기방] => 안타레어
		_list.put(id, ar);
		AntarasRaidTimer RaidEndTime = new AntarasRaidTimer(ar, 5, 0, 3500 * 1000);// 2시간 7200
		RaidEndTime.begin();
	}

	public AntarasRaid getAR(int id){ return _list.get(id); }

	/** 빈 맵 아이디를 가져온다 @return */
	public int blankMapId(){
		int mapid = 0;
		if(_list.size() == 0) return 1005;
		mapid = 6000 + _list.size();
		return mapid;
	}
	/** 포탈 갯수 */
	public int countRaidPotal(){ return _list.size(); }

}
