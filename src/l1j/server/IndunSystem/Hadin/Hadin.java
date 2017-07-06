package l1j.server.IndunSystem.Hadin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.datatables.DoorSpawnTable;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Party;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Door;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.utils.Teleportation;

public class Hadin implements Runnable {

	private static Logger _log = Logger.getLogger(Hadin.class.getName());
	
	private short _map;
	private int stage = 1;
	private L1Party _pARTy;
	private static final int SECRET_HADIN = 1;
	private static final int TALK_ISLAND_DUNGEON_1 = 2;
	private static final int TALK_ISLAND_DUNGEON_2 = 3;
	private static final int TALK_ISLAND_DUNGEON_3 = 4;
	private static final int TALK_ISLAND_DUNGEON_4 = 5;
	private static final int TALK_ISLAND_DUNGEON_5 = 6;
	private static final int TALK_ISLAND_DUNGEON_6 = 7;
	private static final int TALK_ISLAND_DUNGEON_7 = 8;
	private static final int TALK_ISLAND_DUNGEON_8 = 9;
	private static final int TALK_ISLAND_DUNGEON_9 = 10;
	private static final int TALK_ISLAND_DUNGEON_10 = 11;
	private static final int TALK_ISLAND_DUNGEON_11 = 12;
	private static final int TALK_ISLAND_DUNGEON_12 = 13;
	private static final int TALK_ISLAND_DUNGEON_13 = 14;
	private static final int TALK_ISLAND_DUNGEON_14 = 15;
	private static final int TALK_ISLAND_DUNGEON_15 = 16;
	private static final int TALK_ISLAND_DUNGEON_16 = 17;
	private static final int TALK_ISLAND_DUNGEON_17 = 18;
	private static final int TALK_ISLAND_DUNGEON_18 = 19;
	private static final int TALK_ISLAND_DUNGEON_19 = 20;
	private static final int TALK_ISLAND_DUNGEON_20 = 21;
	private static final int TALK_ISLAND_DUNGEON_21 = 22;
	private static final int TALK_ISLAND_DUNGEON_22 = 23;
	private static final int TALK_ISLAND_DUNGEON_23 = 24;
	private static final int TALK_ISLAND_DUNGEON_24 = 25;
	private static final int TALK_ISLAND_DUNGEON_25 = 26;
	private static final int TALK_ISLAND_DUNGEON_26 = 27;
	private static final int TALK_ISLAND_DUNGEON_27 = 28;
	private static final int TALK_ISLAND_DUNGEON_28 = 29;
	private static final int TALK_ISLAND_DUNGEON_29 = 30;
	private static final int TALK_ISLAND_DUNGEON_END = 31;
	private int SubStep = 0;
	private L1NpcInstance Npc_Hadin;
	private L1NpcInstance Hadin_Effect;
	private List<L1PcInstance> list2;
	private HadinTrap HT = null;

	private boolean Running = true;
	private boolean listck = false;

	public ArrayList<L1NpcInstance> BasicNpcList;
	public ArrayList<L1NpcInstance> NpcList;

	public HashMap<String, L1NpcInstance> BossRoomDoor;

	public Hadin(int id){
		_map = (short)id;
		list2 = new ArrayList<L1PcInstance>();
	}

	public boolean StartCK = false;

	private boolean UserCountCK = true;

	@Override
	public void run() {
		//System.out.println("Hadin Thread Create Compleate");
		Hadin_Setting();
		BossRoomDoor = HadinSpawn.getInstance().fillSpawnTable2(_map, 22);
		list2 = getParty().getList();
		int firtCkCount = 0;
		while(Running){
			try {
				if(UserCountCK){
					int i = 0;
					for(L1PcInstance pc : list2){
						if(pc.getMapId() != _map){
							firtCkCount++;
							break;
						}else{
							i++;
						}
					}					
					if(i >= getParty().getNumOfMembers()){
						UserCountCK = false;
						firtCkCount = 0;
					} else {
						if(firtCkCount >= 10){
							RETURN_TEL();
							UserCountCK = false;
						} else {
							try{
								Thread.sleep(1500);
							} catch(Exception e){}
							continue;
						}
					}
				}
				for(L1PcInstance pc : list2){
					if(pc == null) continue;
					if(getParty().getNumOfMembers() < 5 || !pc.isInParty() || pc.getMapId() != _map){//7명
						new L1Teleport().teleport(pc, 32574, 32942, (short) 0, 5, true);
						if(list2.contains(pc)){
							list2.remove(pc);
						}
					}
					if(pc.getMapId() != _map){
						if (pc.isInParty()){ 
							getParty().leaveMember(pc);
						}
					}
					if(getParty().getNumOfMembers() < 5){//7명
						listck = true;
						Running = false;
					}
				}
				if(listck)
					break;

				if(NpcList != null){
					for(L1NpcInstance npc : NpcList){
						if(npc == null || npc.isDead())
							NpcList.remove(npc);
					}
				}
				switch(stage){
				/** 하딘 로비 **/
				case SECRET_HADIN:
						Sleep(3000);
						//오, 왔는가? 잠시 기다리게나, 준비할 것이 있네.
						Npc_Hadin.broadcastPacket(new S_NpcChatPacket(Npc_Hadin, "$7598", 0));
						Sleep(3000);
						//최종 점검을 시작해볼까? 
						Npc_Hadin.broadcastPacket(new S_NpcChatPacket(Npc_Hadin, "$8693", 0));
						BonginSendPacekt(8693);
						Sleep(3000);
						//던전에 들어서면, 자네들의 최소한의 자격을 시험받게 될것이네
						Npc_Hadin.broadcastPacket(new S_NpcChatPacket(Npc_Hadin, "$8695", 0));
						Sleep(3000);
						//어렵지 않은 상대들일테니, 쉽게 통과할것이라고 믿고있네
						Npc_Hadin.broadcastPacket(new S_NpcChatPacket(Npc_Hadin, "$8696", 0));
						Sleep(3000);
						//그리고, 자네들이 트랩을 해제하거나, 설치하기 위한 발판이 준비되어 있네
						Npc_Hadin.broadcastPacket(new S_NpcChatPacket(Npc_Hadin, "$8697", 0));
						Sleep(3000);
						//발판은 최대한 알기쉽게 표현해 놓았지만..
						Npc_Hadin.broadcastPacket(new S_NpcChatPacket(Npc_Hadin, "$8698", 0));
						Sleep(3000);
						//내 미적 감각에 태클은 사양하네
						Npc_Hadin.broadcastPacket(new S_NpcChatPacket(Npc_Hadin, "$8699", 0));
						Sleep(3000);
						//그 밖에도, 무언가 기대할만한 것이 보이기도 하겠지만, 시간에 늦지않게 서둘러주게
						Npc_Hadin.broadcastPacket(new S_NpcChatPacket(Npc_Hadin, "$8700", 0));
						Sleep(3000);
						//조심하게! 안전은 책임질 수 없네
						Npc_Hadin.broadcastPacket(new S_NpcChatPacket(Npc_Hadin, "$8701", 0));
						Sleep(3000);
						//그럼 시작해볼까?
						Npc_Hadin.broadcastPacket(new S_NpcChatPacket(Npc_Hadin, "$8702", 0));                           
					Sleep(3000);
					for(L1PcInstance pc : getParty().getMembers()){
						if(pc == null)
							continue;
						new L1Teleport().teleport(pc, 32665, 32793, _map, 3, true);
					}
					HT = new HadinTrap(_map);
					Sleep(5000);
					stage = 2;
					break;
					/** 첫방 -> 문 오픈 -> 2번째방 몬스터 2번 스폰**/
				case TALK_ISLAND_DUNGEON_1:
					if(NpcList != null && NpcList.size() > 0)
						continue;
					if (SubStep == 0){
						Sleep(2000);
						Sleep(2000);
						Teleportation.teleport(Npc_Hadin, 32716, 32846, _map, 6);
						Teleportation.teleport(Hadin_Effect, 32716, 32846, _map, 6);
						DoorOpen("해골문 1");
					}
					if(SubStep <= 0){
						NpcList = HadinSpawn.getInstance().fillSpawnTable(_map, 1, true);
						SubStep++;
					} else {
						stage = 3;
						SubStep = 0;
					}
					break;
					/** 몬스터 다 잡았을시 -> 발판 체크 4개**/
				case TALK_ISLAND_DUNGEON_2:
					if(HT.LEVEL_1_TRAP_CK){
						DoorOpen("해골문 2");
						NpcList = HadinSpawn.getInstance().fillSpawnTable(_map, 2, true);
						Effect();
						stage = 4;
					}
					break;
					/** 3번째방 몬스터 스폰 **/
				case TALK_ISLAND_DUNGEON_3:
					if(NpcList != null && NpcList.size() > 0)
						continue;
					if(SubStep <= 0){
						NpcList = HadinSpawn.getInstance().fillSpawnTable(_map, 2, true);
						SubStep++;
					} else {
						stage = 5;
						SubStep = 0;
					}
					break;
					/** 트랩 1개 4개 문 오픈 **/
				case TALK_ISLAND_DUNGEON_4:
					if (HT.LEVEL_2_TRAP_CK){
						DoorOpen("해골문 4");
						DoorOpen("해골문 5");
						DoorOpen("해골문 6");
						DoorOpen("해골문 8");
						Effect();
						stage = 6;
					}
					break;
					/** 몬스터 스폰  **/
				case TALK_ISLAND_DUNGEON_5:
					if(NpcList != null && NpcList.size() > 0)
						continue;
					if(SubStep <= 0){
						NpcList = HadinSpawn.getInstance().fillSpawnTable(_map, 3, true);
						SubStep++;
					} else {
						stage = 7;
						SubStep = 0;
					}
					break;
					/** 발판 5개 체크 -> 문 2개 오픈**/
				case TALK_ISLAND_DUNGEON_6:
					if (HT.LEVEL_3_TRAP_CK){
						DoorOpen("해골문 7");
						DoorOpen("해골문 9");
						Effect();
						stage = 8;
					}
					break;
				case TALK_ISLAND_DUNGEON_7:
					if(NpcList != null && NpcList.size() > 0)
						continue;
					if(SubStep <= 0){
						NpcList = HadinSpawn.getInstance().fillSpawnTable(_map, 4, true);
						SubStep++;
					} else {
						DoorOpen("철창문 7");
						Effect();
						stage = 9;
						SubStep = 0;
						Sleep(3000);
					}
					break;
				case TALK_ISLAND_DUNGEON_8:
					if(NpcList != null && NpcList.size() > 0)
						continue;
					if(SubStep <= 0){
						NpcList = HadinSpawn.getInstance().fillSpawnTable(_map, 5, true);
						SubStep++;
					} else {
						DoorOpen("해골문 10");
						Effect();
						SubStep = 0;
						stage = 10;
						Sleep(3000);
					}
					break;
				case TALK_ISLAND_DUNGEON_9:
					if(NpcList != null && NpcList.size() > 0)
						continue;
					if(SubStep <= 0){
						NpcList = HadinSpawn.getInstance().fillSpawnTable(_map, 6, true);
						SubStep++;
					} else {
						DoorOpen("해골문 11");
						DoorOpen("철창문 8");
						DoorOpen("철창문 9");
						Effect();						
						SubStep = 0;
						stage = 11;
						Sleep(3000);
					}						
					break;
				case TALK_ISLAND_DUNGEON_10:
					if (HT.LEVEL_4_TRAP_CK){
						NpcList = HadinSpawn.getInstance().fillSpawnTable(_map, 7, true);
						Effect();
						stage = 12;
					}
					break;
				case TALK_ISLAND_DUNGEON_11:
					if(NpcList != null && NpcList.size() > 0)
						continue;
					if(SubStep <= 0){
						NpcList = HadinSpawn.getInstance().fillSpawnTable(_map, 7, true);
						SubStep++;
					} else {
						DoorOpen("해골문 12");
						Effect();
						SubStep = 0;
						stage = 13;
						Sleep(3000);
					}
					break;
				case TALK_ISLAND_DUNGEON_12:
					if(NpcList != null && NpcList.size() > 0)
						continue;
					if(SubStep <= 0){
						NpcList = HadinSpawn.getInstance().fillSpawnTable(_map, 8, true);
						SubStep++;
					} else {
						DoorOpen("해골문 13");
						Effect();
						SubStep = 0;
						stage = 14;
						Sleep(3000);
					}
					break;
				case TALK_ISLAND_DUNGEON_13:
					if(NpcList != null && NpcList.size() > 0)
						continue;
					if(SubStep <= 0){
						NpcList = HadinSpawn.getInstance().fillSpawnTable(_map, 9, true);
						SubStep++;
					} else {
						SubStep = 0;
						DoorOpen("해골문 14");
						Effect();
						stage = 15;
					}
					break;
				case TALK_ISLAND_DUNGEON_14://15
					Sleep(30000);
					//모두들 긴장하게! 거대한 어둠이 다가오네!
					Npc_Hadin.broadcastPacket(new S_NpcChatPacket(Npc_Hadin, "$7654", 0));
					BonginSendPacekt(7654);
					Sleep(3000);
					//악한것들이 또 몰려오고 있네! 준비하게!
					Npc_Hadin.broadcastPacket(new S_NpcChatPacket(Npc_Hadin, "$7653", 0));
					BonginSendPacekt(7653);
					Sleep(3000);
					//예상보다 빠르게 다가오고 있네! 조심하게!
					Npc_Hadin.broadcastPacket(new S_NpcChatPacket(Npc_Hadin, "$7652", 0));
					BonginSendPacekt(7652);
					Sleep(3000);
					stage = 16;					
					break;
				case TALK_ISLAND_DUNGEON_15:
					if(NpcList != null && NpcList.size() > 0)
						continue;
					if(SubStep <= 0){
						NpcList = HadinSpawn.getInstance().fillSpawnTable(_map, 10, true);
						SubStep++;
					} else {
						SubStep = 0;
						stage = 17;
						Effect();
						BonginSendPacekt(8708);
						Sleep(3000);
					}
					break;
				case TALK_ISLAND_DUNGEON_16:
					if(NpcList != null && NpcList.size() > 0)
						continue;
					if(SubStep <= 0){
						NpcList = HadinSpawn.getInstance().fillSpawnTable(_map, 11, true);
						SubStep++;
					} else {
						SubStep = 0;
						stage = 18;						
						BonginSendPacekt(8709);
						Effect();
						Sleep(3000);
					}
					break;
				case TALK_ISLAND_DUNGEON_17:
					if(NpcList != null && NpcList.size() > 0)
						continue;
					if(SubStep <= 0){
						NpcList = HadinSpawn.getInstance().fillSpawnTable(_map, 12, true);
						SubStep++;
					} else {
						SubStep = 0;
						stage = 19;
						BonginSendPacekt(8710);
						Effect();
						Sleep(3000);
					}
					break;
				case TALK_ISLAND_DUNGEON_18:
					if(NpcList != null && NpcList.size() > 0)
						continue;
					if(SubStep <= 0){
						NpcList = HadinSpawn.getInstance().fillSpawnTable(_map, 13, true);
						SubStep++;
					} else {
						SubStep = 0;
						stage = 20;
						BonginSendPacekt(8711);
						Effect();
						Sleep(3000);
					}
					break;
				case TALK_ISLAND_DUNGEON_19:
					if(NpcList != null && NpcList.size() > 0)
						continue;
					if(SubStep <= 0){
						NpcList = HadinSpawn.getInstance().fillSpawnTable(_map, 14, true);
						SubStep++;
					} else {
						SubStep = 0;
						stage = 21;
						BonginSendPacekt(8712);
						Effect();
						Sleep(3000);
					}
					break;
				case TALK_ISLAND_DUNGEON_20:
					if(NpcList != null && NpcList.size() > 0)
						continue;
					if(SubStep <= 0){
						NpcList = HadinSpawn.getInstance().fillSpawnTable(_map, 15, true);
						SubStep++;
					} else {
						SubStep = 0;
						stage = 22;
						BonginSendPacekt(8713);
						Effect();
						Sleep(3000);
					}
					break;
				case TALK_ISLAND_DUNGEON_21:
					if(NpcList != null && NpcList.size() > 0)
						continue;
					if(SubStep <= 0){
						NpcList = HadinSpawn.getInstance().fillSpawnTable(_map, 16, true);
						SubStep++;
					} else {
						SubStep = 0;
						stage = 23;
						BonginSendPacekt(8714);
						Effect();
						Sleep(3000);
					}
					break;
				case TALK_ISLAND_DUNGEON_22:
					if(NpcList != null && NpcList.size() > 0)
						continue;
					if(SubStep <= 0){
						NpcList = HadinSpawn.getInstance().fillSpawnTable(_map, 17, true);
						SubStep++;
					} else {
						SubStep = 0;
						stage = 24;
						BonginSendPacekt(8715);
						Effect();
						Sleep(3000);
					}
					break;
				case TALK_ISLAND_DUNGEON_23:
					if(NpcList != null && NpcList.size() > 0)
						continue;
					if(SubStep <= 0){
						NpcList = HadinSpawn.getInstance().fillSpawnTable(_map, 18, true);
						SubStep++;
					} else {
						SubStep = 0;
						stage = 25;
						BonginSendPacekt(8716);
						Effect();
						Sleep(3000);
					}
					break;
				case TALK_ISLAND_DUNGEON_24:
					if(NpcList != null && NpcList.size() > 0)
						continue;
					if(SubStep <= 0){
						NpcList = HadinSpawn.getInstance().fillSpawnTable(_map, 19, true);
						SubStep++;
					} else {
						SubStep = 0;
						stage = 26;
						BonginSendPacekt(8717);
						Effect();
						Sleep(3000);
					}
					break;
				case TALK_ISLAND_DUNGEON_25:
					if(NpcList != null && NpcList.size() > 0)
						continue;
					if(SubStep <= 0){
						NpcList = HadinSpawn.getInstance().fillSpawnTable(_map, 20, true);
						SubStep++;
					} else {
						SubStep = 0;
						stage = 27;
						Effect();
						BonginSendPacekt(7820);
						Sleep(3000);
						BonginSendPacekt(7821);
						Sleep(3000);
						BonginSendPacekt(7822);
						Sleep(3000);
						BonginSendPacekt(7823);
						Sleep(3000);
						BonginSendPacekt(7824);
						Sleep(3000);
						BonginSendPacekt(7825);
						Sleep(3000);
						BonginSendPacekt(7826);					
						Sleep(10000);
					}
					break;
				case TALK_ISLAND_DUNGEON_26://여기서 케레 스폰
					if(NpcList != null && NpcList.size() > 0)
						continue;
					if(SubStep <= 0){
						NpcList = HadinSpawn.getInstance().fillSpawnTable(_map, 21, true);
						SubStep++;
					} else {
						BonginSendPacekt(7827);
						Sleep(3000);
						BonginSendPacekt(7828);
						Sleep(3000);
						BonginSendPacekt(7829);
						Sleep(3000);
						SubStep = 0;
						stage = 28;
						Effect();
						Sleep(5000);
					}
					break;
				case TALK_ISLAND_DUNGEON_27:
					L1NpcInstance door = null;
					door = BossRoomDoor.get("보스방 후문 문 8");
					door.PASS = 0;
					door.broadcastPacket(new S_Door(door.getX(), door.getY(), 0, door.PASS));
					//케레니스가 쓰러지다니..
					door.broadcastPacket(new S_NpcChatPacket(Npc_Hadin, "$7833", 0));
					Sleep(3000);
					door = BossRoomDoor.get("보스방 후문 문 7");
					door.PASS = 0;
					//이건 무슨 현상이지!
					Npc_Hadin.broadcastPacket(new S_NpcChatPacket(Npc_Hadin, "$7835", 0));
					door.broadcastPacket(new S_Door(door.getX(), door.getY(), 0, door.PASS));
					Sleep(3000);
					Npc_Hadin.broadcastPacket(new S_NpcChatPacket(Npc_Hadin, "$7836", 0));					
					Sleep(3000);
					door = BossRoomDoor.get("보스방 후문 문 6");
					door.PASS = 0;
					door.broadcastPacket(new S_Door(door.getX(), door.getY(), 0, door.PASS));
					//모두들 대피해라! 이곳을 봉인하겠다!
					Npc_Hadin.broadcastPacket(new S_NpcChatPacket(Npc_Hadin, "$7837", 0));
					Sleep(3000);
					door = BossRoomDoor.get("보스방 후문 문 5");
					door.PASS = 0;
					door.broadcastPacket(new S_Door(door.getX(), door.getY(), 0, door.PASS));
					//남쪽 출구를 곧 막을테니, 어서 빠져나가게!
					Npc_Hadin.broadcastPacket(new S_NpcChatPacket(Npc_Hadin, "$7838", 0));
					Sleep(3000);
					door = BossRoomDoor.get("보스방 후문 문 16");
					door.PASS = 0;
					door.broadcastPacket(new S_Door(door.getX(), door.getY(), 0, door.PASS));
					//동쪽으로 빠져나가면, 탈출구가 보일 것이다!
					Npc_Hadin.broadcastPacket(new S_NpcChatPacket(Npc_Hadin, "$7839", 0));
					Sleep(3000);
					door = BossRoomDoor.get("보스방 후문 문 15");
					door.PASS = 0;
					door.broadcastPacket(new S_Door(door.getX(), door.getY(), 0, door.PASS));
					//살아있다면 다음을 노릴 수 있다! 어서들 탈출해라!
					Npc_Hadin.broadcastPacket(new S_NpcChatPacket(Npc_Hadin, "$7840", 0));
					Sleep(3000);
					door = BossRoomDoor.get("보스방 후문 문 14");
					door.PASS = 0;
					door.broadcastPacket(new S_Door(door.getX(), door.getY(), 0, door.PASS));
					Sleep(3000);
					door = BossRoomDoor.get("보스방 후문 문 13");
					door.PASS = 0;
					door.broadcastPacket(new S_Door(door.getX(), door.getY(), 0, door.PASS));
					Npc_Hadin.broadcastPacket(new S_SkillSound(Npc_Hadin.getId(), 169));
					Teleportation.teleport(Npc_Hadin, 32747, 32930, _map, 5);
					Teleportation.teleport(Hadin_Effect, 32747, 32930, _map, 5);
					Sleep(10000);
					BonginSendPacekt(8718);
					stage = 29;
					break;
				case TALK_ISLAND_DUNGEON_28:
					Sleep(3000);
					DoorOpen("철창문 14");
					DoorOpen("철창문 13");
					DoorOpen("철창문 12");
					DoorOpen("철창문 11");
					DoorOpen("철창문 10");
					Effect();
					stage = 30;
					break;
				case TALK_ISLAND_DUNGEON_29:
					if (HT.LAST_TRAP_CK){
						NpcList = HadinSpawn.getInstance().fillSpawnTable(_map, 99, true);
						stage = 31;
					}
					Sleep(15000);
					break;
					/** 보상방 이벤트 이후 **/
				case TALK_ISLAND_DUNGEON_END:
					if(!HT.Running){
						RETURN_TEL();
						Running = false;
					}
					break;
				default:
					break;
				}
			}catch(Exception e){
				//System.out.println("Hadin Event Thread Error instanceID : "+_map +" -> "+e);
			}finally{
				try{
					Thread.sleep(1500);
				} catch (Exception e) {
					_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
				}
			}
		}
		//System.out.println("Hadin Thread Delete");
		Hadin_Delete();
	}

	public void Start(){
		GeneralThreadPool.getInstance().execute(this);
	}

	private void Sleep(int time){
		try{
			Thread.sleep(time);
		}catch(Exception e){}
	}

	private void Hadin_Setting(){
		for(L1NpcInstance npc : BasicNpcList){
			if(npc != null){
				if(npc.getName().equalsIgnoreCase("하딘")){
					Npc_Hadin = npc;
				} else if(npc.getName().equalsIgnoreCase("하딘 바닥 이펙트")){
					Hadin_Effect = npc;
				}
			}
		}
	}

	private L1Party getParty(){
		return _pARTy;
	}

	public void setParty(L1Party p){
		_pARTy = p;
	}

	private void Hadin_Delete(){
		Collection<L1Object> cklist = L1World.getInstance().getVisibleObjects(_map).values();
		for(L1Object ob : cklist){
			if(ob == null)
				continue;
			if(ob instanceof L1ItemInstance){
				L1ItemInstance obj = (L1ItemInstance)ob;
				L1Inventory groundInventory = L1World.getInstance().getInventory(obj.getX(), obj.getY(), obj.getMapId());
				groundInventory.removeItem(obj);
			}else if(ob instanceof L1NpcInstance){
				L1NpcInstance npc = (L1NpcInstance)ob;
				npc.deleteMe();
			}
		}
		HadinSystem.getInstance().removeHadin(_map);
		if(HT != null){//트랩 쓰레드 같이 종료
			HT.Running = false;
			HT = null;
		}
	}

	public void OpenDoor(int i){
		L1DoorInstance door = DoorSpawnTable.getInstance().getDoor(i);
		if (door != null){
			if(door.getOpenStatus() == ActionCodes.ACTION_Close){
				door.open();
			}
		}
	}

	private void DoorOpen(String name){
		for(L1NpcInstance npc : BasicNpcList){
			if(npc != null){
				if(npc instanceof L1DoorInstance){
					L1DoorInstance door = (L1DoorInstance)npc;
					if(door.getSpawnLocation().equalsIgnoreCase(name)){
						door.open();
						break;
					}
				}
			}
		}
	}

	private void Effect() { // 화면 떨림 이펙트.
		for(L1PcInstance c : getParty().getMembers()) {
			c.sendPackets(new S_SkillSound(c.getId(), 1249));
		}
	}

	private void BonginSendPacekt(int count){
		for(L1PcInstance pc : getParty().getMembers()){
			pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "$"+count));
		}
	}

	private void RETURN_TEL(){
		for(L1PcInstance pc : getParty().getMembers()){
			new L1Teleport().teleport(pc, 32572, 32944, (short) 0, 3, true);
		}
	}
}
