package l1j.server.IndunSystem.MiniGame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


import l1j.server.Config;
import l1j.server.server.IdFactory;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.L1Party;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_SystemMessage;



public class MiniSiege extends Thread {
		private static MiniSiege ins;
		public int count=0;
		private HashMap<Integer,ArrayList<L1PcInstance>> teamcount = new HashMap<Integer,ArrayList<L1PcInstance>>();
	//	private final int maxcount = 20;
		private int stage=0; //0 : 준비단계, 1: 가로막고있는 Object 제거 , 2: 미니공성전 진행 , 3: 공성전완료 보상품 지급
		public boolean running = false;

		private boolean destroyed[] = { false, false, false, false, false, false,false,false,false };

		L1Party party = new L1Party();
		L1Party party2 = new L1Party();
		L1Party party3 = new L1Party();
		
		private String msg[] = {"참가자를 모으는 중입니다 잠시 기다려주세요!", "수호탑,중간탑,사령탑 순으로 격파해주세요!", "각각 타워를 철거하면 추가 보상이 있습니다!"};
		public boolean isDestory(int i){
			return destroyed[i];
		}
		public void Exit(){
			//ins = null;
			for(int i = 0 ; i <9;i++)
			destroyed[i]=false;
			running = false;
			
			teamcount.clear();
			stage = 0;
			count = 0;
			for(int i = 0 ; i <L1World.getInstance().getMapObject(10502).size();i++){
				if(L1World.getInstance().getMapObject(10502).get(i) instanceof L1NpcInstance){
				L1NpcInstance npc = (L1NpcInstance)L1World.getInstance().getMapObject(10502).get(i);
				npc.deleteMe();				
				}
			}
		}
		public static MiniSiege getInstance(){
			if(ins == null)
				ins = new MiniSiege();
			return ins;
		}


		public void setDestroy(int i){
			//0,1,2,3,4,5,6,7,8 {순서대로 A~C 1차, 2차, 사령}
			switch(i){
				case 0:
					sendMsg("A팀 수호탑이 파괴되었습니다");
					destroyed[0] = true;
					break;
				case 1:
					sendMsg("B팀 수호탑이 파괴되었습니다");
					destroyed[1] = true;
					break;
				case 2:
					sendMsg("C팀 수호탑이 파괴되었습니다");
					destroyed[2] = true;
					break;
				case 3:
					sendMsg("A팀 중간탑이 파괴되었습니다");
					destroyed[3] = true;
					break;
				case 4:
					sendMsg("B팀 중간탑이 파괴되었습니다");
					destroyed[4] = true;
					break;
				case 5:
					sendMsg("C팀 중간탑이 파괴되었습니다");
					destroyed[5] = true;
					break;
				case 6:
					sendMsg("A팀 사령탑이 파괴되었습니다");
					destroyed[6] = true;
//					System.out.println("멤버함보자 : "+teamcount.get(0).size()+" 멤바브 : "+teamcount.get(0).get(0).getName());
					for(int j = 0 ; j < teamcount.get(0).size();j++){
						new L1Teleport().teleport(teamcount.get(0).get(j), 33438, 32810, (short) 4, teamcount.get(0).get(j).getMoveState().getHeading(), true);
						teamcount.get(0).get(j).setTeam(-1);
						teamcount.get(0).get(j).isSiege = false;
					}
					//teamcount.remove(0);
					break;
				case 7:
					sendMsg("B팀 사령탑이 파괴되었습니다");
					destroyed[7] = true;
					for(int j = 0 ; j < teamcount.get(1).size();j++){
						new L1Teleport().teleport(teamcount.get(1).get(j), 33438, 32810, (short) 4, teamcount.get(1).get(j).getMoveState().getHeading(), true);
						teamcount.get(1).get(j).setTeam(-1);
						teamcount.get(1).get(j).isSiege = false;
					}
					//teamcount.remove(1);
					break;
				case 8:
					sendMsg("C팀 사령탑이 파괴되었습니다");
					destroyed[8] = true;
					for(int j = 0 ; j < teamcount.get(2).size();j++){
						new L1Teleport().teleport(teamcount.get(2).get(j), 33438, 32810, (short) 4, teamcount.get(2).get(j).getMoveState().getHeading(), true);
						 teamcount.get(2).get(j).setTeam(-1);
						 teamcount.get(2).get(j).isSiege = false;
					}
					//teamcount.remove(2);
					break;					
			}
		}
		
		public void GiveReward(int type,int team2){
			System.out.println("GIVEREWARD : "+type+" TEAM2 : "+team2);
			int itemid=0;
			int count2=0;
			for(int i = 0 ; i < teamcount.get(team2).size();i++){
				switch(type){
				case 1:
					itemid = Config.Tower;
					count2 = Config.TowerC;
					teamcount.get(team2).get(i).getInventory().storeItem(itemid,count2);
					teamcount.get(team2).get(i).sendPackets(new S_SystemMessage("보상 지급 완료"));
					break;
				case 2:
					 itemid= Config.MTower;
					 count2= Config.MTowerC;
					teamcount.get(team2).get(i).getInventory().storeItem(itemid,count2);
					teamcount.get(team2).get(i).sendPackets(new S_SystemMessage("보상 지급 완료"));
					break;
				}
			}
		}
		public void ini(){
			if(!running){
			for(int i = 0 ; i < 3; i++){
				teamcount.put(i, new ArrayList<L1PcInstance>());
			}
			//A진형 수호탑
			spawn(4201,32771,32871,10502);
			spawn(4205,32772,32839,10502);
			spawn(4209,32773,32798,10502);
			//B 진형 수호탑
			spawn(4202,32747,32895,10502);
			spawn(4206,32715,32894,10502);
			spawn(4210,32678,32894,10502);
			//C진형 수호탑
			spawn(4203,32771,32919,10502);
			spawn(4207,32772,32951,10502);
			spawn(4211,32771,32987,10502);
			}
			L1World.getInstance().broadcastPacketToAll2(new S_Message_YN(622,"미니공성전이 개최되었습니다. 참여하시겠습니까?"));
			running = true;
		}
		private void sendMsg(String msg){
			for(int i = 0 ; i < teamcount.size();i++){
				for(int j = 0 ; j < teamcount.get(i).size();j++){
				teamcount.get(i).get(j).sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,S_PacketBox.RED_MESSAGE, msg, true));
				}
			}
		}
		public synchronized void EnterTeam(L1PcInstance pc){
				if(pc.getTeam()==-1){
					System.out.println("들어와서 저장됨"+count%3);
					pc.setTeam(count%3);
					teamcount.get(count%3).add(pc);
					count++;
				}else{
					System.out.println("이미 이벤트 참가중");
				}
				
			System.out.println("COUNT:"+count+teamcount.get(count%3)+" pc : " +pc.getTeam());
			
			switch(pc.getTeam()){
				case 0:
					new L1Teleport().teleport(pc, 32771, 32815, (short) 10502, pc.getHeading(), true);
					party.addMember(pc);
					break;
				case 1:
					new L1Teleport().teleport(pc, 32691, 32895, (short) 10502, pc.getHeading(), true);
					//party2.addMember(pc);
					party.addMember(pc);
					break;
				case 2:
					new L1Teleport().teleport(pc, 32771, 32975, (short) 10502, pc.getHeading(), true);
					party3.addMember(pc);
					break;
			}
			
		}
	/*	public boolean checkStart(){
			boolean check=true;
				for(int i = 0 ; i < 4;i++){
					if(teamcount.get(i).size() != maxcount)
						check = false;
				}
			
			return check;
		}*/

		public void setStage(int i){
			stage = i;
		}
		public int getStage(){ return stage; }

		public int checkWinTeam(){
			int i = -1;
			for(int j = 6 ; j < 9 ; j++){
				if(!destroyed[j])
					i = j;
			}
			i = i%3;
			return i;
		}
		public int getNexuscount(){
			int c = 0;
			for(int j = 6 ; j < 9 ; j++){
				if(destroyed[j])
					c++;
			}
			if(c>=2)
				return 1;
			else
				return 0;
		}
		@Override
		public void run(){
			Random rnd = new Random();
			while(running){
				try{
				switch(stage){
					case 0:// 준비단계
						int m = rnd.nextInt(3);
						for(int i = 0 ; i < 3 ; i++){
							for(int j = 0 ; j<teamcount.get(i).size();j++){
								teamcount.get(i).get(j).sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,msg[m] ));
							}
						}
						Thread.sleep(8000);
						break;
					case 1:
						for(int i = 0 ; i < 3 ; i++){
							for(int j = 0 ; j<teamcount.get(i).size();j++){
								teamcount.get(i).get(j).sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,S_PacketBox.RED_MESSAGE, "미니공성전을 시작합니다!", true));
							}
						}
						stage = 2;
						break;
					case 2: //진행중
						for(int i = 0 ; i < 3; i++){
							if(getNexuscount()==1){
								stage = 3;
							}
						}
						break;
					case 3://진행완료 보상지급
						int winteam = checkWinTeam();
						//이긴팀 전원보상 및 마을로 귀환;
						int itemid = Config.LTower;
						int count2 = Config.LTowerC;
						for(int i = 0 ; i < teamcount.get(winteam).size();i++){
							teamcount.get(winteam).get(i).getInventory().storeItem(itemid,count2);
							teamcount.get(winteam).get(i).sendPackets(new S_SystemMessage("보상 지급 완료"));
							teamcount.get(winteam).get(i).sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,S_PacketBox.RED_MESSAGE, "최종승리를 축하합니다~~!!!", true));
							teamcount.get(winteam).get(i).sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,"30초뒤 마을로 귀환됩니다!"));
							
						}
						Thread.sleep(30000);
						for(int j = 0 ; j < teamcount.get(winteam).size();j++){
							new L1Teleport().teleport(teamcount.get(winteam).get(j), 33438, 32810, (short) 4, teamcount.get(winteam).get(j).getMoveState().getHeading(), true);
							 teamcount.get(winteam).get(j).setTeam(-1);
							 teamcount.get(winteam).get(j).isSiege = false;
						}
						running = false;
						
						break;
					default:
						break;
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			Exit();
		}
		public static void spawn(int npcId, int locX,int locY,int mapid) {
			try {
				L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(npcId);
				npc.setId(IdFactory.getInstance().nextId());
				npc.setMap((short)mapid);

	
					int tryCount = 0;
					do {
						tryCount++;
						npc.setX(locX);
						npc.setY(locY);
						if (npc.getMap(). isInMap(npc.getLocation()) && npc.getMap(). isPassable(npc.getLocation())) {
							break;
						}
						Thread.sleep(1);
					} while (tryCount < 50);

					if (tryCount >= 50) {
						npc.getLocation().set(locX,locY,mapid);
						npc.getLocation().forward(1);
					}
				
				npc.setHomeX(npc.getX());
				npc.setHomeY(npc.getY());
				npc.setHeading(1);
				
				L1World.getInstance().storeObject(npc);
				L1World.getInstance().addVisibleObject(npc);
				npc.getLight().turnOnOffLight();
				npc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE); // 채팅 개시
			} catch (Exception e) {
			
			}
		}
}
