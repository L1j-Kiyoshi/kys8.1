package l1j.server.GameSystem.Boss;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.GameSystem.Boss.NewBossSpawnTable.BossTemp;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.IdFactory;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1MobGroupSpawn;
import l1j.server.server.model.L1NpcDeleteTimer;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.gametime.RealTime;
import l1j.server.server.model.gametime.RealTimeClock;
import l1j.server.server.serverpackets.S_MatizAlarm;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.utils.CommonUtil;
import manager.LinAllManager;

public class BossTimeController implements Runnable {
	private static Logger _log = Logger.getLogger(BossTimeController.class.getName());

	private static BossTimeController _instance;

	private Random rnd = new Random(System.nanoTime());
	private int _time = 0;
	private int _timeM = 0;
	private Date day = new Date(System.currentTimeMillis());

	public static BossTimeController getInstance() {
		if (_instance == null)
			_instance = new BossTimeController();
		return _instance;
	}

	ArrayList<BossTemp> bosslist = null;

	private boolean isNow = false;

	public BossTimeController() {
		GeneralThreadPool.getInstance().execute(this);
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(1000);
				BossChack();
			} catch (Exception e) {
				_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			}
		}
	}

	private void BossChack() {

		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		int minute = Calendar.getInstance().get(Calendar.MINUTE);
		
		bosslist = NewBossSpawnTable.getInstance().getlist();
		for (BossTemp temp : bosslist) {
			boolean isDay = false;
			for (int i : temp.Day) {
				if (i == day.getDay()) {
					isDay = true;
					break;
				}
			}
			if (!isDay) {
				continue;
			}
			
			if(temp.isSpawn){
				boolean ck = false;
				for (int Minute_temp : temp.SpawnMinute) {
					if(minute == Minute_temp){
						ck = true;
					}
				}
				if(ck)continue;
				else temp.isSpawn = false;
			}
			
			
			for (int i = 0; i < temp.SpawnHour.length; i++) {
				if(hour == temp.SpawnHour[i]-1 && minute==temp.SpawnMinute[i]){
					switch(temp.npcid){
						case 5136:
							if(!BossAlive.getInstance().is에르자베){
								BossAlive.getInstance().is에르자베 = true;
								BossAlive.getInstance().set에르자베타임(RealTimeClock.getInstance().getRealTime().getSeconds()+3600);
								int time = (int)(BossAlive.getInstance().ezTime - RealTimeClock.getInstance().getRealTime().getSeconds());
								L1World.getInstance().broadcastPacketToAll(new S_MatizAlarm(1,time,3600,true));

							}
						break;
						case 5135:
							if(!BossAlive.getInstance().is샌드웜){
								BossAlive.getInstance().is샌드웜 = true;
								BossAlive.getInstance().set샌드웜타임(RealTimeClock.getInstance().getRealTime().getSeconds()+3600);
								int time = (int)(BossAlive.getInstance().sdTime - RealTimeClock.getInstance().getRealTime().getSeconds());
								L1World.getInstance().broadcastPacketToAll(new S_MatizAlarm(2,time,3600,true));
							}
							break;
					}
				}
				if(hour == temp.SpawnHour[i] && minute == temp.SpawnMinute[i]){
					temp.isSpawn = true;
					GeneralThreadPool.getInstance().execute(new BossThread(temp));
				}	
			}
		}
	}
	
	
	/**보스 스폰 처리 */
	class BossThread implements Runnable {
		BossTemp temp;
		public BossThread(BossTemp _temp){
			temp = _temp;
		}
		
		public void run(){
			try{
				
				int rndtime = rnd.nextInt(temp.rndTime) + 1;
				if(rndtime > 0)Thread.sleep(rndtime * 60 * 1000);
				StoreBoss(temp.npcid, temp.SpawnLoc, temp.rndLoc, temp.Groupid, temp.isYn, temp.isMent, temp.Ment,temp.DeleteTime);
			}catch(Exception e){}
		}
	}
	
	

	public void StoreBoss(int npcid, int[] Loc, int rndXY, int groupid, boolean isYN, boolean isMent, String ment, int deleteTime) {
		try {
			L1Npc template = NpcTable.getInstance().getTemplate(npcid);
			if (template == null) {
				_log.warning("Boss mob data for id:" + npcid + " missing in npc table");
				System.out.println("보스스폰 컨트롤러 보스 npcid " + npcid + "가 존재하지 않습니다.");
				return;
			}
			L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(npcid);
			
			npc.setId(IdFactory.getInstance().nextId());

			L1Location loc = new L1Location(Loc[0], Loc[1], Loc[2]);
			if (rndXY != 0) {
				loc.randomLocation(rndXY, false);
			}

			npc.setLocation(loc);
			npc.getLocation().forward(5);
			npc.setHomeX(npc.getX());
			npc.setHomeY(npc.getY());
			if (groupid > 0)
				L1MobGroupSpawn.getInstance().doSpawn(npc, groupid, true, false);

			L1World.getInstance().storeObject(npc);
			L1World.getInstance().addVisibleObject(npc);
			
			BossAlive.getInstance().BossSpawn(loc.getMapId());
			
			npc.getLight().turnOnOffLight();
			npc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE);

			if (isMent) {
				L1World.getInstance().broadcastPacketToAll(new S_SystemMessage(ment));
				L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, ment));
			}

			if (isYN) {
//				switch (npcid) {
//				case 7310046:
//					for (L1PcInstance player : L1World.getInstance().getAllPlayers()) {
//						player.set머미로드(true);
//						player.sendPackets(new S_Message_YN(622, "머미로드를 징벌하러 가시겠습니까?"));
//					}
//					break;
//				case 2:
//					break;
//				
//				}

			}

			if (0 < deleteTime) {
				L1NpcDeleteTimer timer = new L1NpcDeleteTimer(npc, deleteTime * 1000);
				timer.begin();
			} else {
				L1NpcDeleteTimer timer = new L1NpcDeleteTimer(npc, 3600 * 1000);
				timer.begin();
			}
			LinAllManager.getInstance().BossAppend(npc.getName());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}