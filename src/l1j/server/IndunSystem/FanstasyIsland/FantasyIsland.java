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
					//助けに来てくれてありがとうございます。
					Sleep(2000);
					Broadcaster.broadcastPacket(unicorn, new S_NpcChatPacket(unicorn, "$17692", 0));
					//異界の存在がすぐに戻ってです。
					Sleep(2000);
					Broadcaster.broadcastPacket(unicorn, new S_NpcChatPacket(unicorn, "$17693", 0));
					//その前に、私は封印を解くことができるよう時間を稼ぐください。
					Sleep(3000);
					Broadcaster.broadcastPacket(unicorn, new S_NpcChatPacket(unicorn, "$17947", 0));
					pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "$17947"));
					//魔法の棒を使って敵を倒してください。
					pc.getInventory().storeItem(810006, 1);
					pc.sendPackets(new S_SystemMessage("$17948"));
					Sleep(5000);
					pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "$17701"));
					pc.sendPackets(new S_PacketBox(S_PacketBox.ROUND, 1, 3));
					//敵が集まってきています。
					//11時方向スポンポールシステム、ベネボス
					L1SpawnUtil.spawnCount(32799, 32852, _map, 7200008, 0, 0, 5);
					L1SpawnUtil.spawnCount(32799, 32852, _map, 7200012, 0, 0, 5);
					stage = 2;
					break;
				case FIRST_STEP:
					Sleep(10000);
					//1時方向出現スコーピオン+メデューサ
					L1SpawnUtil.spawnCount(32810, 32863, _map, 7200009, 0, 0, 5);
					L1SpawnUtil.spawnCount(32810, 32863, _map, 7200013, 0, 0, 5);
					Sleep(10000);
					//5時方向コートルツ+フェルフェルスポン
					L1SpawnUtil.spawnCount(32801, 32873, _map, 7200010, 0, 0, 5);
					L1SpawnUtil.spawnCount(32801, 32873, _map, 7200014, 0, 0, 5);
					Sleep(10000);
					//7時方向メガ+ビア
					L1SpawnUtil.spawnCount(32789, 32861, _map, 7200011, 0, 0, 5);
					L1SpawnUtil.spawnCount(32789, 32861, _map, 7200015, 0, 0, 5);
					//土地の大精霊が現れました！
					pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "$17944"));
					stage = 3;
					break;
					/** 2番目の手順に進む **/
				case SECOND_STEP:
					Sleep(10000);
					pc.sendPackets(new S_PacketBox(S_PacketBox.ROUND, 2, 3));
					pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "$17703"));
					//敵がより集まってきます。準備してください
					pc.getInventory().storeItem(810006, 1);
					pc.sendPackets(new S_SystemMessage("$17948"));
					Sleep(5000);
					//11時フォルシス、ベネボス
					L1SpawnUtil.spawnCount(32799, 32852, _map, 7200008, 0, 0, 5);
					L1SpawnUtil.spawnCount(32799, 32852, _map, 7200012, 0, 0, 5);
					//1時方向メドゥーサ+スコーピオン+地医大精霊
					L1SpawnUtil.spawnCount(32810, 32863, _map, 7200009, 0, 0, 5);
					L1SpawnUtil.spawnCount(32810, 32863, _map, 7200013, 0, 0, 5);
					L1SpawnUtil.spawnCount(32810, 32863, _map, 7200018, 0, 0, 1);
					Sleep(10000);
					//5時コートルツ+フェルフェル
					L1SpawnUtil.spawnCount(32801, 32873, _map, 7200010, 0, 0, 5);
					L1SpawnUtil.spawnCount(32801, 32873, _map, 7200014, 0, 0, 5);
					Sleep(20000);
					//7時メガ+ビア
					L1SpawnUtil.spawnCount(32789, 32861, _map, 7200011, 0, 0, 5);
					L1SpawnUtil.spawnCount(32789, 32861, _map, 7200015, 0, 0, 5);
					Sleep(5000);
					//11時フォルシス、ベネボス
					L1SpawnUtil.spawnCount(32799, 32852, _map, 7200008, 0, 0, 5);
					L1SpawnUtil.spawnCount(32799, 32852, _map, 7200012, 0, 0, 5);
					Sleep(5000);
					pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "$17942"));
					//風の大精霊が現れました！
					stage = 4;
					break;
					/** 3段階 **/
				case THIRD_STEP:
					Sleep(3000);
					pc.sendPackets(new S_PacketBox(S_PacketBox.ROUND, 3, 3));
					pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "$17703"));
					//敵がより集まってきます。準備してください
					pc.getInventory().storeItem(810006, 1);
					pc.sendPackets(new S_SystemMessage("$17948"));
					Broadcaster.broadcastPacket(unicorn, new S_NpcChatPacket(unicorn, "$17706", 0));
	
					Sleep(5000);
					//4軍隊の同時出現+風医大精霊
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
					//4軍の同時出現
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
					//ユニコーンを奪っていこうと？そう聞かせ順なかっ!!
					Sleep(5000);
					//夢幻の支配者+ 4軍出現
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
						boss = L1SpawnUtil.spawnCount(32789, 32861, _map, 7200020, 0, 0, 1);	//禁断
					} else if (chance <= 30) {
						boss = L1SpawnUtil.spawnCount(32789, 32861, _map, 7199998, 0, 0, 1);	//ああヴィシー
					} else if (chance <= 45) {
						boss = L1SpawnUtil.spawnCount(32789, 32861, _map, 7199999, 0, 0, 1);	//アズモダン
					}

					stage = 5;
					break;
				case LAST_STEP:
					if (boss.isDead() || boss == null) {
						Sleep(5000);
						pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "$17707"));
						//夢幻の支配者が退治しました。
						Sleep(5000);
						Broadcaster.broadcastPacket(unicorn, new S_SkillSound(unicorn.getId(), 1911));
						Sleep(1000);
						Broadcaster.broadcastPacket(unicorn, new S_ChangeShape(unicorn.getId(), 12493));
						//Sleep(5000);
						//ありがとうございます！
						pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "$17708"));
						Broadcaster.broadcastPacket(unicorn, new S_NpcChatPacket(unicorn, "$17708", 0));

						Sleep(3000);
						//しばらくそれ戻れないでしょう。
						pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "$17709"));
						Broadcaster.broadcastPacket(unicorn, new S_NpcChatPacket(unicorn, "$17709", 0));

						Sleep(3000);
						//て夢幻の島に戻って見なければならね。
						pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "$17710"));
						Broadcaster.broadcastPacket(unicorn, new S_NpcChatPacket(unicorn, "$17710", 0));

						Sleep(3000);
						//プレゼントを差し上げたいですね。心にドたらいいですね。
						pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "$17712"));
						Broadcaster.broadcastPacket(unicorn, new S_NpcChatPacket(unicorn, "$17712", 0));

						Sleep(3000);
						Broadcaster.broadcastPacket(unicorn, new S_SkillSound(unicorn.getId(), 169));

//						int itemId = 0;
//						int rnd = new Random().nextInt(1000) + 1;
//						if (rnd < 300) { itemId = 40074; //防具強化スクロール
//						} else if (rnd < 600) { itemId = 40087; //武器強化スクロール
//						} else if (rnd < 610) { itemId = 600; //脳身体検査
//						} else if (rnd < 620) { itemId = 601; //破滅のグレートソード
//						} else if (rnd < 630) { itemId = 605; //狂風の斧
//						} else if (rnd < 640) { itemId = 604; //酷寒のウィンドウ
//						} else if (rnd < 650) { itemId = 603; //天使の杖
//						} else if (rnd < 660) { itemId = 191;// サルチョンの弓
//						} else if (rnd < 670) { itemId = 1125; //破壊の二刀流
//						} else if (rnd < 680) { itemId = 1124; //破壊のクロウ
//						} else if (rnd < 682) { itemId = 1136; //悪夢のロングボウ
//						} else if (rnd < 684) { itemId = 1137; //怒りのクロウ
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
						//システムメッセージ：5秒後にテレポートします。
						pc.sendPackets(new S_SystemMessage("しばらくして村に移動されます。"));
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
				if(npc.getName().equalsIgnoreCase("ユニコーン")){
					unicorn = npc;
				}
			}
		}
	}

	private void checkHp() {
		if ((unicorn.getMaxHp() * 1 / 5) > unicorn.getCurrentHp()) { //2000
			if (_status != 4) {
				Broadcaster.broadcastPacket(unicorn, new S_NpcChatPacket(unicorn, "$17949", 0));
				//これ以上は難しいと思われる。
				_status = 4;
			}
		} else if ((unicorn.getMaxHp() * 2 / 5) > unicorn.getCurrentHp()) { //4000
			if (_status != 3) {
				Broadcaster.broadcastPacket(unicorn, new S_NpcChatPacket(unicorn, "$17950", 0));
				//もう少し耐えている場合...
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
		int hour = Calendar.HOUR;
		int minute = Calendar.MINUTE;
		/** 0 午前、1午後 * */
		String ampm = "午後";
		if (cal.get(Calendar.AM_PM) == 0) {
			ampm = "午前";
		}
		GeneralThreadPool.getInstance().schedule(this, 2000);
		  System.out.println(""+ ampm + " " + cal.get(hour) + "時" + cal.get(minute) + "分" + "   ■■■■■■ 夢幻の島開始 " +  _map+" ■■■■■■");
	}
	private void endRaid(){
		Calendar cal = Calendar.getInstance();
		int hour = Calendar.HOUR;
		int minute = Calendar.MINUTE;
		/** 0 午前、1午後 * */
		String ampm = "午後";
		if (cal.get(Calendar.AM_PM) == 0) {
			ampm = "午前";
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
		  System.out.println(""+ ampm + " " + cal.get(hour) + "時" + cal.get(minute) + "分" + "   ■■■■■■ 夢幻の島終了 " +  _map+" ■■■■■■");
		}
	}
}
