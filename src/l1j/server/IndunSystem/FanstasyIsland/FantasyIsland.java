package l1j.server.IndunSystem.FanstasyIsland;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Random;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ChangeShape;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.L1SpawnUtil;

public class FantasyIsland implements Runnable {

	private short _map;
	private int stage = 1;
	private static final int WAIT_RAID = 1;
	private static final int FIRST_STEP = 2;
	private static final int SECOND_STEP = 3;
	private static final int THIRD_STEP = 4;
	//private static final int FOURTH_STEP = 5;
	private static final int LAST_STEP = 5;
	private static final int END = 6;

	private int _status;
	private L1NpcInstance unicorn;
	private L1NpcInstance boss;
	private L1PcInstance pc;

	private boolean Running = true;

	public ArrayList<L1NpcInstance> BasicNpcList;
	public ArrayList<L1NpcInstance> NpcList;

	public FantasyIsland(int id, L1PcInstance pc){
		_map = (short)id;
		this.pc = pc;
	}

	@Override
	public void run() {
		setting();
		NpcList = FantasyIslandSpawn.getInstance().fillSpawnTable(_map, 1, true);
		while(Running){
			try {

				if(NpcList != null){
					for(L1NpcInstance npc : NpcList){
						if(npc == null || npc.isDead())
							NpcList.remove(npc);
					}
				}

				if (unicorn.isDead()) {
					if (pc != null) {
						new L1Teleport().teleport(pc, 33968, 32961, (short)  4, 2, true);
						pc.getInventory().consumeItem(810006);
						pc.getInventory().consumeItem(810007);
						pc = null;
					}
					endRaid();
				}

				checkHp();
				checkPc();

				switch(stage){
				case WAIT_RAID:
					if(NpcList.size() > 0)
						continue;
					Sleep(5000);
					Broadcaster.broadcastPacket(unicorn, new S_NpcChatPacket(unicorn, "$17691", 0));
					//도와주러 와 주셔서 감사합니다.
					Sleep(2000);
					Broadcaster.broadcastPacket(unicorn, new S_NpcChatPacket(unicorn, "$17692", 0));
					//이계의 존재가 곧 돌아올겁니다.
					Sleep(2000);
					Broadcaster.broadcastPacket(unicorn, new S_NpcChatPacket(unicorn, "$17693", 0));
					//그전에 제가 봉인을 풀 수 있도록 시간을 벌어주세요.
					Sleep(3000);
					Broadcaster.broadcastPacket(unicorn, new S_NpcChatPacket(unicorn, "$17947", 0));
					pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "$17947"));
					//마법 막대를 이용해 적을 처치해주세요.
					pc.getInventory().storeItem(810006, 1);
					pc.sendPackets(new S_SystemMessage("$17948"));
					Sleep(5000);
					pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "$17701"));
					pc.sendPackets(new S_PacketBox(S_PacketBox.ROUND, 1, 3));
					//적들이 몰려오고 있습니다.
					//11시 방향 스폰 포르시스, 베네보스
					L1SpawnUtil.spawnCount(32799, 32852, _map, 7200008, 0, 0, 5);
					L1SpawnUtil.spawnCount(32799, 32852, _map, 7200012, 0, 0, 5);
					stage = 2;
					break;
				case FIRST_STEP:
					Sleep(10000);
					//1시방향 스폰 스콜피온+ 메두사
					L1SpawnUtil.spawnCount(32810, 32863, _map, 7200009, 0, 0, 5);
					L1SpawnUtil.spawnCount(32810, 32863, _map, 7200013, 0, 0, 5);
					Sleep(10000);
					//5시방향 코트룻+페르페르 스폰
					L1SpawnUtil.spawnCount(32801, 32873, _map, 7200010, 0, 0, 5);
					L1SpawnUtil.spawnCount(32801, 32873, _map, 7200014, 0, 0, 5);
					Sleep(10000);
					//7시방향 메가+비아
					L1SpawnUtil.spawnCount(32789, 32861, _map, 7200011, 0, 0, 5);
					L1SpawnUtil.spawnCount(32789, 32861, _map, 7200015, 0, 0, 5);
					//땅의 대정령이 나타났습니다!!!
					pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "$17944"));
					stage = 3;
					break;
					/** 2번째 단계진행 **/
				case SECOND_STEP:
					Sleep(10000);
					pc.sendPackets(new S_PacketBox(S_PacketBox.ROUND, 2, 3));
					pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "$17703"));
					//적들이 더 몰려옵니다. 준비해 주세요
					pc.getInventory().storeItem(810006, 1);
					pc.sendPackets(new S_SystemMessage("$17948"));
					Sleep(5000);
					//11시 포르시스, 베네보스
					L1SpawnUtil.spawnCount(32799, 32852, _map, 7200008, 0, 0, 5);
					L1SpawnUtil.spawnCount(32799, 32852, _map, 7200012, 0, 0, 5);
					//1시방향 메두사+스콜피온 + 땅의대정령
					L1SpawnUtil.spawnCount(32810, 32863, _map, 7200009, 0, 0, 5);
					L1SpawnUtil.spawnCount(32810, 32863, _map, 7200013, 0, 0, 5);
					L1SpawnUtil.spawnCount(32810, 32863, _map, 7200018, 0, 0, 1);
					Sleep(10000);
					//5시 코트룻+페르페르
					L1SpawnUtil.spawnCount(32801, 32873, _map, 7200010, 0, 0, 5);
					L1SpawnUtil.spawnCount(32801, 32873, _map, 7200014, 0, 0, 5);
					Sleep(20000);
					//7시 메가+비아
					L1SpawnUtil.spawnCount(32789, 32861, _map, 7200011, 0, 0, 5);
					L1SpawnUtil.spawnCount(32789, 32861, _map, 7200015, 0, 0, 5);
					Sleep(5000);
					//11시 포르시스, 베네보스
					L1SpawnUtil.spawnCount(32799, 32852, _map, 7200008, 0, 0, 5);
					L1SpawnUtil.spawnCount(32799, 32852, _map, 7200012, 0, 0, 5);
					Sleep(5000);
					pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "$17942"));
					//바람의 대정령이 나타났습니다!!!
					stage = 4;
					break;
					/** 3단계 **/
				case THIRD_STEP:
					Sleep(3000);
					pc.sendPackets(new S_PacketBox(S_PacketBox.ROUND, 3, 3));
					pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "$17703"));
					//적들이 더 몰려옵니다. 준비해 주세요
					pc.getInventory().storeItem(810006, 1);
					pc.sendPackets(new S_SystemMessage("$17948"));
					Broadcaster.broadcastPacket(unicorn, new S_NpcChatPacket(unicorn, "$17706", 0));
	
					Sleep(5000);
					//4군대 동시 스폰 + 바람의대정령
					L1SpawnUtil.spawnCount(32799, 32852, _map, 7200008, 0, 0, 3);
					L1SpawnUtil.spawnCount(32799, 32852, _map, 7200012, 0, 0, 3);
					L1SpawnUtil.spawnCount(32810, 32863, _map, 7200009, 0, 0, 3);
					L1SpawnUtil.spawnCount(32810, 32863, _map, 7200013, 0, 0, 3);
					L1SpawnUtil.spawnCount(32801, 32873, _map, 7200010, 0, 0, 3);
					L1SpawnUtil.spawnCount(32801, 32873, _map, 7200014, 0, 0, 3);
					L1SpawnUtil.spawnCount(32789, 32861, _map, 7200011, 0, 0, 3);
					L1SpawnUtil.spawnCount(32789, 32861, _map, 7200015, 0, 0, 3);
					L1SpawnUtil.spawnCount(32801, 32873, _map, 7200016, 0, 0, 1);
					Sleep(5000);
					//4군대 동시 스폰
					L1SpawnUtil.spawnCount(32799, 32852, _map, 7200008, 0, 0, 3);
					L1SpawnUtil.spawnCount(32799, 32852, _map, 7200012, 0, 0, 3);
					L1SpawnUtil.spawnCount(32810, 32863, _map, 7200009, 0, 0, 3);
					L1SpawnUtil.spawnCount(32810, 32863, _map, 7200013, 0, 0, 3);
					L1SpawnUtil.spawnCount(32801, 32873, _map, 7200010, 0, 0, 3);
					L1SpawnUtil.spawnCount(32801, 32873, _map, 7200014, 0, 0, 3);
					L1SpawnUtil.spawnCount(32789, 32861, _map, 7200011, 0, 0, 3);
					L1SpawnUtil.spawnCount(32789, 32861, _map, 7200015, 0, 0, 3);
					Sleep(15000);
					pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "$17995 : $17713"));
					//유니콘을 빼앗아가려고? 그렇게 놔둘 순 없지!!
					Sleep(5000);
					//몽환의 지배자+ 4군대 스폰
					L1SpawnUtil.spawnCount(32799, 32852, _map, 7200008, 0, 0, 4);
					L1SpawnUtil.spawnCount(32799, 32852, _map, 7200012, 0, 0, 4);
					L1SpawnUtil.spawnCount(32810, 32863, _map, 7200009, 0, 0, 4);
					L1SpawnUtil.spawnCount(32810, 32863, _map, 7200013, 0, 0, 4);
					L1SpawnUtil.spawnCount(32801, 32873, _map, 7200010, 0, 0, 4);
					L1SpawnUtil.spawnCount(32801, 32873, _map, 7200014, 0, 0, 4);
					L1SpawnUtil.spawnCount(32789, 32861, _map, 7200011, 0, 0, 4);
					L1SpawnUtil.spawnCount(32789, 32861, _map, 7200015, 0, 0, 4);
					
					Random random = new Random(System.nanoTime());
					int chance = random.nextInt(45) + 1;
					if (chance <= 15) {
						boss = L1SpawnUtil.spawnCount(32789, 32861, _map, 7200020, 0, 0, 1);	//구미호
					} else if (chance <= 30) {
						boss = L1SpawnUtil.spawnCount(32789, 32861, _map, 7199998, 0, 0, 1);	//아비쉬
					} else if (chance <= 45) {
						boss = L1SpawnUtil.spawnCount(32789, 32861, _map, 7199999, 0, 0, 1);	//아즈모단
					}

					stage = 5;
					break;
				case LAST_STEP:
					if (boss.isDead() || boss == null) {
						Sleep(5000);
						pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "$17707"));
						//몽환의 지배자가 퇴치 되었습니다.
						Sleep(5000);
						Broadcaster.broadcastPacket(unicorn, new S_SkillSound(unicorn.getId(), 1911));
						Sleep(1000);
						Broadcaster.broadcastPacket(unicorn, new S_ChangeShape(unicorn.getId(), 12493));
						//Sleep(5000);
						//감사합니다!
						pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "$17708"));
						Broadcaster.broadcastPacket(unicorn, new S_NpcChatPacket(unicorn, "$17708", 0));

						Sleep(3000);
						//당분간 그것은 돌아올 수 없을 것입니다.
						pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "$17709"));
						Broadcaster.broadcastPacket(unicorn, new S_NpcChatPacket(unicorn, "$17709", 0));

						Sleep(3000);
						//어서 몽환의 섬으로 돌아가 봐야겠군요.
						pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "$17710"));
						Broadcaster.broadcastPacket(unicorn, new S_NpcChatPacket(unicorn, "$17710", 0));

						Sleep(3000);
						//선물을 드리고 싶군요. 마음에 드셨으면 좋겠네요.
						pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "$17712"));
						Broadcaster.broadcastPacket(unicorn, new S_NpcChatPacket(unicorn, "$17712", 0));

						Sleep(3000);
						Broadcaster.broadcastPacket(unicorn, new S_SkillSound(unicorn.getId(), 169));

//						int itemId = 0;
//						int rnd = new Random().nextInt(1000) + 1;
//						if (rnd < 300) { itemId = 40074; //갑옷 마법 주문서
//						} else if (rnd < 600) { itemId = 40087; //무기 마법 주문서
//						} else if (rnd < 610) { itemId = 600; //뇌신검
//						} else if (rnd < 620) { itemId = 601; //파멸의 대검
//						} else if (rnd < 630) { itemId = 605; //광풍의 도끼
//						} else if (rnd < 640) { itemId = 604; //혹한의 창
//						} else if (rnd < 650) { itemId = 603; //천사의 지팡이
//						} else if (rnd < 660) { itemId = 191;// 살천의 활
//						} else if (rnd < 670) { itemId = 1125; //파괴의 이도류
//						} else if (rnd < 680) { itemId = 1124; //파괴의 크로우
//						} else if (rnd < 682) { itemId = 1136; //악몽의 장궁
//						} else if (rnd < 684) { itemId = 1137; //진노의 크로우
//						}
						
						L1ItemInstance item = ItemTable.getInstance().createItem(31089);
						L1World.getInstance().getInventory(unicorn.getX(), unicorn.getY(), unicorn.getMapId()).storeItem(item);
//						L1ItemInstance item1 = ItemTable.getInstance().createItem(itemId);
//						if (item1 != null) {
//							L1World.getInstance().getInventory(unicorn.getX(), unicorn.getY(), unicorn.getMapId()).storeItem(item1);
//						} 

						unicorn.deleteMe();
						stage = 6;
					}
					break;
				case END:
					Thread.sleep(2000);
					if(pc.getMapId() == _map){ 
						//pc.sendPackets(new S_ServerMessage(1480));  
						//시스템 메시지: 5초 후에 텔레포트 합니다.
						pc.sendPackets(new S_SystemMessage("잠시 후 마을로 이동됩니다."));
					}
					Thread.sleep(10000);

					new L1Teleport().teleport(pc, 33459, 32791, (short)  4, 2, true);
					pc.getInventory().consumeItem(810006);
					pc.getInventory().consumeItem(810007);
					pc = null;
					break;
				default:
					break;
				}
			}catch(Exception e){
			}finally{
				try{
					Thread.sleep(1500);
				}catch(Exception e){}
			}
		}
		endRaid();
	}

	private void Sleep(int time){
		try{
			Thread.sleep(time);
		}catch(Exception e){}
	}

	private void setting(){
		for(L1NpcInstance npc : BasicNpcList){
			if(npc != null){
				if(npc.getName().equalsIgnoreCase("유니콘")){
					unicorn = npc;
				}
			}
		}
	}

	private void checkHp() {
		if ((unicorn.getMaxHp() * 1 / 5) > unicorn.getCurrentHp()) { //2000
			if (_status != 4) {
				Broadcaster.broadcastPacket(unicorn, new S_NpcChatPacket(unicorn, "$17949", 0));
				//더 이상은 힘들 것 같습니다.
				_status = 4;
			}
		} else if ((unicorn.getMaxHp() * 2 / 5) > unicorn.getCurrentHp()) { //4000
			if (_status != 3) {
				Broadcaster.broadcastPacket(unicorn, new S_NpcChatPacket(unicorn, "$17950", 0));
				//조금만 더 버틸 수 있다면...
				_status = 3;
			}
		} else if ((unicorn.getMaxHp() * 3 / 5) > unicorn.getCurrentHp()) { //6000
			if (_status != 2) {
				Broadcaster.broadcastPacket(unicorn, new S_NpcChatPacket(unicorn, "$17952", 0));
				_status = 2;
			}
		} else if ((unicorn.getMaxHp() * 4 / 5) > unicorn.getCurrentHp()) { //8000
			if (_status != 1) {
				Broadcaster.broadcastPacket(unicorn, new S_NpcChatPacket(unicorn, "$17952", 0));
				_status = 1;
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
			endRaid();
		}
	}
	public void Start(){
		Calendar cal = Calendar.getInstance();
		int 시간 = Calendar.HOUR;
		int 분 = Calendar.MINUTE;
		/** 0 오전 , 1 오후 * */
		String 오전오후 = "오후";
		if (cal.get(Calendar.AM_PM) == 0) {
			오전오후 = "오전";
		}
		GeneralThreadPool.getInstance().schedule(this, 2000);
		  System.out.println(""+ 오전오후 + " " + cal.get(시간) + "시" + cal.get(분) + "분" + "   ■■■■■■ 몽환의  섬 시작 " +  _map+" ■■■■■■");
	}
	private void endRaid(){
		Calendar cal = Calendar.getInstance();
		int 시간 = Calendar.HOUR;
		int 분 = Calendar.MINUTE;
		/** 0 오전 , 1 오후 * */
		String 오전오후 = "오후";
		if (cal.get(Calendar.AM_PM) == 0) {
			오전오후 = "오전";
		}
		if (Running) {
			Collection<L1Object> cklist = L1World.getInstance().getVisibleObjects(_map).values();
			for(L1Object ob : cklist){
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
			Running = false;
			FantasyIslandSystem.getInstance().remove(_map);
		  System.out.println(""+ 오전오후 + " " + cal.get(시간) + "시" + cal.get(분) + "분" + "   ■■■■■■ 몽환의  섬 종료 " +  _map+" ■■■■■■");
		}
	}
}
