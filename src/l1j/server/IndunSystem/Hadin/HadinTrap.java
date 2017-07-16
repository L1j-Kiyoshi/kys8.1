package l1j.server.IndunSystem.Hadin;

import java.util.Collection;
import java.util.HashMap;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.utils.L1SpawnUtil;

public class HadinTrap implements Runnable {

	private short _map;
	public boolean Running = true;

	private boolean LEVEL_1_TRAP_1 = false;
	private boolean LEVEL_1_TRAP_2 = false;
	private boolean LEVEL_1_TRAP_3 = false;
	private boolean LEVEL_1_TRAP_4 = false;
	private boolean LEVEL_2_TRAP_1 = false;
	private boolean LEVEL_2_TRAP_2 = false;
	private boolean LEVEL_2_TRAP_3 = false;
	private boolean LEVEL_2_TRAP_4 = false;
	private boolean LEVEL_3_TRAP_1 = false;
	private boolean LEVEL_3_TRAP_2 = false;
	private boolean LEVEL_3_TRAP_3 = false;
	private boolean LEVEL_3_TRAP_4 = false;

	private boolean TRAP_1 = false;
	
	private boolean LAST_TRAP_1 = false;
	private boolean LAST_TRAP_2 = false;
	//private boolean LAST_TRAP_3 = false;
	private boolean LAST_TRAP_4 = false;
	private boolean LAST_TRAP_5 = false;
	//private boolean LAST_TRAP_6 = false;
	//private boolean LAST_TRAP_7 = false;

	private boolean LAST_TRAP = false;

	public boolean MonCK = false;
	public boolean LEVEL_1_TRAP_CK = false;//初めての部屋足場4つ
	public boolean LEVEL_2_TRAP_CK = false;//ヅルバン足場1個
	public boolean LEVEL_3_TRAP_CK = false;//貸し足場4つ
	public boolean LEVEL_4_TRAP_CK = false;//最後の部屋足場

	public boolean BossRoomCK = false;
	public boolean LAST_TRAP_CK = false;

	private HashMap<String, L1DoorInstance> doorList;

	public HadinTrap(short id){
		_map = (short)id;
		doorList = new HashMap<String, L1DoorInstance>();
		GeneralThreadPool.getInstance().schedule(this, 5000);
	}

	@Override
	public void run() {
		//System.out.println("HadinTrap Thread Create Compleate");
		while(Running){
			try {
				TRAP_RESET();
				Collection<L1Object> cklist;
				cklist = L1World.getInstance().getVisibleObjects(_map).values();
				for(L1Object ob : cklist){
					if(ob == null || ob instanceof L1MonsterInstance)
						continue;
					if(ob instanceof L1PcInstance){
						L1PcInstance pc = (L1PcInstance)ob;
						TRAP_ON(pc);
					} else if(ob instanceof L1DoorInstance) {
						L1DoorInstance door = (L1DoorInstance)ob;
						if(door.getNpcTemplate().get_npcId() == 900151 || door.getNpcTemplate().get_npcId() == 900152){
							if(!doorList.containsKey(door.getSpawnLocation())){
								doorList.put(door.getSpawnLocation(), door);
							}
						}
					}
				}
				if(!MonCK)
					TRAP_ON_CK();
			}catch(Exception e){
				//System.out.println("HadinTrap Event Thread Error instanceID : "+_map +" -> "+e);
			}finally{
				try{
					Thread.sleep(2000);
				}catch(Exception e){}
			}
		}
	}


	private void TRAP_RESET(){
		LEVEL_1_TRAP_1 = false;
		LEVEL_1_TRAP_2 = false;
		LEVEL_1_TRAP_3 = false;
		LEVEL_1_TRAP_4 = false;
		LEVEL_2_TRAP_1 = false;
		LEVEL_2_TRAP_2 = false;
		LEVEL_2_TRAP_3 = false;
		LEVEL_2_TRAP_4 = false;
		LEVEL_3_TRAP_1 = false;
		LEVEL_3_TRAP_2 = false;
		LEVEL_3_TRAP_3 = false;
		LEVEL_3_TRAP_4 = false;
		TRAP_1 = false;
		LAST_TRAP_1 = false;
		LAST_TRAP_2 = false;
		//LAST_TRAP_3 = false;
		LAST_TRAP_4 = false;
		LAST_TRAP_5 = false;
		//LAST_TRAP_6 = false;
		//LAST_TRAP_7 = false;
		LAST_TRAP = false;
	}

	private void TRAP_ON_CK(){
		L1DoorInstance door = null;
		if(LEVEL_1_TRAP_1 && LEVEL_1_TRAP_2 && LEVEL_1_TRAP_3 && LEVEL_1_TRAP_4){//初めての部屋の4つの足場
			LEVEL_1_TRAP_CK = true;
			door = doorList.get("スケルトンドア2");
			door.open();
			BonginSendPacekt(7611);
		} else if (TRAP_1){//第二の部屋1つ足場
			LEVEL_2_TRAP_CK = true;
			door = doorList.get("スケルトンドア4");
			door = doorList.get("スケルトンドア5");
			door = doorList.get("スケルトンドア6");
			door = doorList.get("スケルトンドア8");
			door.open();
		} else if(LEVEL_2_TRAP_1 && LEVEL_2_TRAP_2 && LEVEL_2_TRAP_3 && LEVEL_2_TRAP_4){//貸し足場4つのドア2個のオープン
			LEVEL_3_TRAP_CK = true;
			door = doorList.get("スケルトンドア7");
			door = doorList.get("スケルトンドア9");
			BonginSendPacekt(7628);
			door.open();			
		} else if(LEVEL_3_TRAP_1 && LEVEL_3_TRAP_2 && LEVEL_3_TRAP_3 && LEVEL_3_TRAP_4){
			Collection<L1Object> cklist;
			cklist = L1World.getInstance().getVisibleObjects(_map).values();
			for(L1Object ob : cklist){
				if(ob == null)
					continue;
				if(ob instanceof L1PcInstance){
					L1PcInstance pc = (L1PcInstance) ob;
					new L1Teleport().teleport(pc, 32785, 32821, _map, 5, true);
				}
			}
			L1SpawnUtil.spawn2(32743, 32928, _map, 900160, 0, 0, 0);
			LEVEL_4_TRAP_CK = true;
		} else if (LAST_TRAP_1 && LAST_TRAP_2 && LAST_TRAP_4 && LAST_TRAP_5){
			Collection<L1Object> cklist;
			cklist = L1World.getInstance().getVisibleObjects(_map).values();
			for(L1Object ob : cklist){
				if(ob == null)
					continue;
				if(ob instanceof L1PcInstance){
					L1PcInstance pc = (L1PcInstance) ob;
					pc.sendPackets(new S_ServerMessage(8719));
				}
			}
			LAST_TRAP_CK = true;
		} else if (LAST_TRAP){
			Running = false;
			Collection<L1Object> cklist;
			cklist = L1World.getInstance().getVisibleObjects(_map).values();
			for(L1Object ob : cklist){
				if(ob == null)
					continue;
				if(ob instanceof L1PcInstance){
					L1PcInstance pc = (L1PcInstance) ob;
					pc.sendPackets(new S_ServerMessage(403, "$9355 (1)"));
					pc.getInventory().storeItem(410108, 1);//秘密包み
				}
			}
			BonginSendPacekt(8719);
			return;
		}

		if(door != null){
			door.open();
		}
	}

	private void TRAP_ON(L1PcInstance pc){

		if(pc.getX() == 32666 && pc.getY() == 32817)       LEVEL_1_TRAP_1 = true;
		else if(pc.getX() == 32668 && pc.getY() == 32817)  LEVEL_1_TRAP_2 = true;
		else if(pc.getX() == 32666 && pc.getY() == 32819)  LEVEL_1_TRAP_3 = true;
		else if(pc.getX() == 32668 && pc.getY() == 32819)  LEVEL_1_TRAP_4 = true;//初めての部屋の4つの足場

		else if(pc.getX() == 32684 && pc.getY() == 32816)  TRAP_1 = true;//第二の部屋1つ足場

		else if(pc.getX() == 32703 && pc.getY() == 32800)  LEVEL_2_TRAP_1 = true;
		else if(pc.getX() == 32703 && pc.getY() == 32791)  LEVEL_2_TRAP_2 = true;
		else if(pc.getX() == 32710 && pc.getY() == 32803)  LEVEL_2_TRAP_3 = true;
		else if(pc.getX() == 32712 && pc.getY() == 32793)  LEVEL_2_TRAP_4 = true;//第三の部屋の4つの足場

		else if(pc.getX() == 32807 && pc.getY() == 32837)  LEVEL_3_TRAP_1 = true;
		else if(pc.getX() == 32809 && pc.getY() == 32837)  LEVEL_3_TRAP_2 = true;
		else if(pc.getX() == 32807 && pc.getY() == 32839)  LEVEL_3_TRAP_3 = true;
		else if(pc.getX() == 32809 && pc.getY() == 32839)  LEVEL_3_TRAP_4 = true;

		else if(pc.getX() == 32798 && pc.getY() == 32866)  LAST_TRAP_1 = true;
		else if(pc.getX() == 32801 && pc.getY() == 32864)  LAST_TRAP_2 = true;
	//	else if(pc.getX() == 32805 && pc.getY() == 32864)  LAST_TRAP_3 = true;
		else if(pc.getX() == 32798 && pc.getY() == 32872)  LAST_TRAP_4 = true;
		else if(pc.getX() == 32800 && pc.getY() == 32874)  LAST_TRAP_5 = true;
	//	else if(pc.getX() == 32804 && pc.getY() == 32874)  LAST_TRAP_6 = true;
	//	else if(pc.getX() == 32805 && pc.getY() == 32871)  LAST_TRAP_7 = true;
		else if(pc.getX() == 32802 && pc.getY() == 32868)  LAST_TRAP = true;
	}

	private void BonginSendPacekt(int count){
		Collection<L1Object> cklist;
		cklist = L1World.getInstance().getVisibleObjects(_map).values();
		for(L1Object ob : cklist){
			if(ob == null)
				continue;
			if(ob instanceof L1PcInstance){
				L1PcInstance pc = (L1PcInstance) ob;
				pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "$"+count));
			}
		}
	}

}