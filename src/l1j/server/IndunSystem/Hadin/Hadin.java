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
					if(getParty().getNumOfMembers() < 5 || !pc.isInParty() || pc.getMapId() != _map){//7人
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
					if(getParty().getNumOfMembers() < 5){// 7人
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
				/** ハーディンロビー **/
				case SECRET_HADIN:
						Sleep(3000);
						//ああ、来たかしばらく待っても、準備することがあるのね。
						Npc_Hadin.broadcastPacket(new S_NpcChatPacket(Npc_Hadin, "$7598", 0));
						Sleep(3000);
						//最終点検を開始するには？ 
						Npc_Hadin.broadcastPacket(new S_NpcChatPacket(Npc_Hadin, "$8693", 0));
						BonginSendPacekt(8693);
						Sleep(3000);
						//ダンジョンに入ると、あなたの最小限の資格を試さなるね
						Npc_Hadin.broadcastPacket(new S_NpcChatPacket(Npc_Hadin, "$8695", 0));
						Sleep(3000);
						//難しくない相手許容だろうから、簡単に通過するものと信じているのね
						Npc_Hadin.broadcastPacket(new S_NpcChatPacket(Npc_Hadin, "$8696", 0));
						Sleep(3000);
						//そして、あなたがトラップを無効にするか、インストールするための足場が用意されているのね
						Npc_Hadin.broadcastPacket(new S_NpcChatPacket(Npc_Hadin, "$8697", 0));
						Sleep(3000);
						//足場は、可能な限り分かりやすく表現しましたが。
						Npc_Hadin.broadcastPacket(new S_NpcChatPacket(Npc_Hadin, "$8698", 0));
						Sleep(3000);
						//私美的感覚に取り組むには仕様ね
						Npc_Hadin.broadcastPacket(new S_NpcChatPacket(Npc_Hadin, "$8699", 0));
						Sleep(3000);
						//他にも、何か期待すべきでは見えたりしますが、時間に遅れないように急いで与える
						Npc_Hadin.broadcastPacket(new S_NpcChatPacket(Npc_Hadin, "$8700", 0));
						Sleep(3000);
						//注意して！安全は責任を負うことができないね
						Npc_Hadin.broadcastPacket(new S_NpcChatPacket(Npc_Hadin, "$8701", 0));
						Sleep(3000);
						//その後、開始するには？
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
					/** 初めての部屋 - >ドアオープン - > 2番目の部屋のモンスター2回出現**/
				case TALK_ISLAND_DUNGEON_1:
					if(NpcList != null && NpcList.size() > 0)
						continue;
					if (SubStep == 0){
						Sleep(2000);
						Sleep(2000);
						Teleportation.teleport(Npc_Hadin, 32716, 32846, _map, 6);
						Teleportation.teleport(Hadin_Effect, 32716, 32846, _map, 6);
						DoorOpen("スケルトンドア1");
					}
					if(SubStep <= 0){
						NpcList = HadinSpawn.getInstance().fillSpawnTable(_map, 1, true);
						SubStep++;
					} else {
						stage = 3;
						SubStep = 0;
					}
					break;
					/**モンスターだ取った時 - >足場チェック4つ**/
				case TALK_ISLAND_DUNGEON_2:
					if(HT.LEVEL_1_TRAP_CK){
						DoorOpen("スケルトンドア2");
						NpcList = HadinSpawn.getInstance().fillSpawnTable(_map, 2, true);
						Effect();
						stage = 4;
					}
					break;
					/** 3番目の部屋のモンスター出現 **/
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
					/** トラップ1個4個ドアオープン **/
				case TALK_ISLAND_DUNGEON_4:
					if (HT.LEVEL_2_TRAP_CK){
						DoorOpen("スケルトンドア4");
						DoorOpen("スケルトンドア5");
						DoorOpen("スケルトンドア6");
						DoorOpen("スケルトンドア8");
						Effect();
						stage = 6;
					}
					break;
					/** モンスター出現  **/
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
					/** 足場の5つのチェック - >ドアの2つのオープン**/
				case TALK_ISLAND_DUNGEON_6:
					if (HT.LEVEL_3_TRAP_CK){
						DoorOpen("スケルトンドア7");
						DoorOpen("スケルトンドア9");
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
						DoorOpen("鉄の窓7");
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
						DoorOpen("スケルトンドア10");
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
						DoorOpen("スケルトンドア11");
						DoorOpen("鉄の窓8");
						DoorOpen("鉄の窓9");
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
						DoorOpen("スケルトンドア12");
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
						DoorOpen("スケルトンドア13");
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
						DoorOpen("スケルトンドア14");
						Effect();
						stage = 15;
					}
					break;
				case TALK_ISLAND_DUNGEON_14://15
					Sleep(30000);
					//みんな緊張して！巨大な闇が迫っ生じ！
					Npc_Hadin.broadcastPacket(new S_NpcChatPacket(Npc_Hadin, "$7654", 0));
					BonginSendPacekt(7654);
					Sleep(3000);
					//悪のものがまた集まってきてね！準備に！
					Npc_Hadin.broadcastPacket(new S_NpcChatPacket(Npc_Hadin, "$7653", 0));
					BonginSendPacekt(7653);
					Sleep(3000);
					//予想より早く近づいてね！注意して！
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
				case TALK_ISLAND_DUNGEON_26://ここけれど出現
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
					door = BossRoomDoor.get("ボス部屋裏話ドア8");
					door.PASS = 0;
					door.broadcastPacket(new S_Door(door.getX(), door.getY(), 0, door.PASS));
					//ケレニスが倒れなんて。
					door.broadcastPacket(new S_NpcChatPacket(Npc_Hadin, "$7833", 0));
					Sleep(3000);
					door = BossRoomDoor.get("ボス部屋裏話ドア7");
					door.PASS = 0;
					//これは何現象で！
					Npc_Hadin.broadcastPacket(new S_NpcChatPacket(Npc_Hadin, "$7835", 0));
					door.broadcastPacket(new S_Door(door.getX(), door.getY(), 0, door.PASS));
					Sleep(3000);
					Npc_Hadin.broadcastPacket(new S_NpcChatPacket(Npc_Hadin, "$7836", 0));					
					Sleep(3000);
					door = BossRoomDoor.get("ボス部屋裏話ドア6");
					door.PASS = 0;
					door.broadcastPacket(new S_Door(door.getX(), door.getY(), 0, door.PASS));
					//みんな避難しろ！ここを封印したい！
					Npc_Hadin.broadcastPacket(new S_NpcChatPacket(Npc_Hadin, "$7837", 0));
					Sleep(3000);
					door = BossRoomDoor.get("ボス部屋裏話ドア5");
					door.PASS = 0;
					door.broadcastPacket(new S_Door(door.getX(), door.getY(), 0, door.PASS));
					//南口をすぐに停止するので、是非逃がす！
					Npc_Hadin.broadcastPacket(new S_NpcChatPacket(Npc_Hadin, "$7838", 0));
					Sleep(3000);
					door = BossRoomDoor.get("ボス部屋裏話ドア16");
					door.PASS = 0;
					door.broadcastPacket(new S_Door(door.getX(), door.getY(), 0, door.PASS));
					//東に抜けていくと、出口が見える！
					Npc_Hadin.broadcastPacket(new S_NpcChatPacket(Npc_Hadin, "$7839", 0));
					Sleep(3000);
					door = BossRoomDoor.get("ボス部屋裏話ドア15");
					door.PASS = 0;
					door.broadcastPacket(new S_Door(door.getX(), door.getY(), 0, door.PASS));
					//生きている場合は、次の狙える！ての脱出しろ！
					Npc_Hadin.broadcastPacket(new S_NpcChatPacket(Npc_Hadin, "$7840", 0));
					Sleep(3000);
					door = BossRoomDoor.get("ボス部屋裏話ドア14");
					door.PASS = 0;
					door.broadcastPacket(new S_Door(door.getX(), door.getY(), 0, door.PASS));
					Sleep(3000);
					door = BossRoomDoor.get("ボス部屋裏話ドア13");
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
					DoorOpen("鉄の窓14");
					DoorOpen("鉄の窓13");
					DoorOpen("鉄の窓12");
					DoorOpen("鉄の窓11");
					DoorOpen("鉄の窓10");
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
					/** 報酬部屋イベントの後 **/
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
				if(npc.getName().equalsIgnoreCase("ハーディン")){
					Npc_Hadin = npc;
				} else if(npc.getName().equalsIgnoreCase("ハーディン床エフェクト")){
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
		if(HT != null){//トラップスレッドのように終了
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

	private void Effect() { // 画面のブレエフェクト。
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
