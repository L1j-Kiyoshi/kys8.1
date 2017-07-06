package l1j.server.IndunSystem.ValakasRoom;


import java.util.ArrayList;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_ServerMessage;

public class ValakasReady implements Runnable {
	private short _map;
	private int stage = 1;
	private static final int READY_START = 1;
	private static final int WAIT_RAID = 2;
	private static final int END = 3;

	private L1NpcInstance death;

	private L1PcInstance pc;

	private boolean Running = false;

	public ArrayList<L1NpcInstance> BasicNpcList;

	public ValakasReady(int id, L1PcInstance pc){
		_map = (short)id;
		this.pc = pc;
	}

	@Override
	public void run() {
		setting();
		Running = true;
		while(Running){
			try {
				switch(stage){
				case READY_START:
					Thread.sleep(2000);
					//모..두 파괴하라..모든 것을..파괴하라..
					Broadcaster.broadcastPacket(death, new S_NpcChatPacket(death, "$18861"));
					Thread.sleep(3000);
					//당신은 누구시죠?
					if (pc.getMapId() == _map) {
						pc.sendPackets(new S_NpcChatPacket(pc, "$18862"));
					}
					Thread.sleep(3000);
					//나? 나는..누구?? 기사..그래..나는 기사였다..그런데 나는 왜 여기에 있지?
					Broadcaster.broadcastPacket(death, new S_NpcChatPacket(death, "$18863"));
					Thread.sleep(3000);
					//기..사?
					if (pc.getMapId() == _map) {
						pc.sendPackets(new S_NpcChatPacket(pc, "$18864"));
					}
					Thread.sleep(3000);
					//그래..나는 기사다... 자신의 작은 재주 하나만 믿고 발라카스에게 도전한 어리석은 기사..
					Broadcaster.broadcastPacket(death, new S_NpcChatPacket(death, "$18865"));
					Thread.sleep(3000);
					//발라카스에게 패배하신겁니까?
					if (pc.getMapId() == _map) {
						pc.sendPackets(new S_NpcChatPacket(pc, "$18866"));
					}
					Thread.sleep(3000);
					//패배라..그래..결국엔 나의 패배겠지..나의 기억과 이성이 점차 사라지는 것을 보니..
					Broadcaster.broadcastPacket(death, new S_NpcChatPacket(death, "$18867"));
					Thread.sleep(3000);
					//내가 이성을 잃기 전에 어서 이 검을 가지고 이 곳을 피하게.
					Broadcaster.broadcastPacket(death, new S_NpcChatPacket(death, "$18868"));
					stage = WAIT_RAID;
					break;
				case WAIT_RAID:
					break;
				case END:
					if(pc.getMapId() == _map){ 
						pc.sendPackets(new S_ServerMessage(1478));  
						//시스템 메시지: 10초 후에 텔레포트 합니다.
					} 
					Thread.sleep(5000);
					if(pc.getMapId() == _map){ 
						pc.sendPackets(new S_ServerMessage(1480));  
						//시스템 메시지: 5초 후에 텔레포트 합니다.
					} 
					Thread.sleep(1000);
					if(pc.getMapId() == _map){ 
						pc.sendPackets(new S_ServerMessage(1481));  
						//시스템 메시지: 4초 후에 텔레포트 합니다.
					} 
					Thread.sleep(1000);
					if(pc.getMapId() == _map){ 
						pc.sendPackets(new S_ServerMessage(1482));  
						//시스템 메시지: 3초 후에 텔레포트 합니다.
					} 
					Thread.sleep(1000);
					if(pc.getMapId() == _map){ 
						pc.sendPackets(new S_ServerMessage(1483));  
						//시스템 메시지: 2초 후에 텔레포트 합니다.
					} 
					Thread.sleep(1000);
					if(pc.getMapId() == _map){ 
						pc.sendPackets(new S_ServerMessage(1484));  
						//시스템 메시지: 1초 후에 텔레포트 합니다.
					} 
					Thread.sleep(1000);
					if(pc.getMapId() == _map){ 
						new L1Teleport().teleport(pc, 33705, 32504, (short)4, 5, true);
					}
					Running = false;
					break;
				default:
					break;
				}
				Thread.sleep(1000);
				checkPc();
			}catch(Exception e){
			}finally{
				try{
					Thread.sleep(1500);
				}catch(Exception e){}
			}
		}
		endRaid();
	}

	public void Start(){
		GeneralThreadPool.getInstance().execute(this);
	}


	private void setting(){
		for(L1NpcInstance npc : BasicNpcList){
			if(npc != null){
				if(npc.getName().equalsIgnoreCase("진데스나이트")){
					death = npc;
				}
			}
		}
	}

	private void checkPc() {
		int check = 0;
		for (L1Object obj : L1World.getInstance().getVisibleObjects(_map).values()) {
			if (obj instanceof L1PcInstance) {
				check = 1;
			}
		}
		if (check == 0) {
			if (pc != null) {
				pc = null;
			}
			Running = false;
		}
	}

	private void endRaid(){
		for (L1Object ob : L1World.getInstance().getVisibleObjects(_map).values()) {
			if(ob == null) continue;
			if(ob instanceof L1ItemInstance){
				L1ItemInstance obj = (L1ItemInstance)ob;
				L1Inventory groundInventory = L1World.getInstance().getInventory(obj.getX(), obj.getY(), obj.getMapId());
				groundInventory.removeItem(obj);
			}else if(ob instanceof L1NpcInstance){
				L1NpcInstance npc = (L1NpcInstance)ob;
				npc.deleteMe();
			}
		}
		pc = null;
		death = null;
		if (BasicNpcList != null) BasicNpcList.clear();
		Running = false;
		ValakasReadyStart.getInstance().removeReady(_map);
	}
}