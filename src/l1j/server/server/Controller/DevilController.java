package l1j.server.server.Controller;

import java.util.Calendar;
import java.util.Locale;
import java.text.SimpleDateFormat;

import l1j.server.Config;
import l1j.server.server.datatables.DoorSpawnTable;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.L1SpawnUtil;

public class DevilController extends Thread {

	private static DevilController _instance;

	private boolean _DevilStart;

	public boolean getDevilStart() {
		return _DevilStart;
	}

	public void setDevilStart(boolean Devil) {
		_DevilStart = Devil;
	}

	private static long sTime = 0;

	public boolean isGmOpen = false; // 추가

	private String NowTime = "";

	// 시간 간격
	private static final int LOOP = Config.구라던오픈주기;;

	private static final SimpleDateFormat s = new SimpleDateFormat("HH",
			Locale.KOREA);

	private static final SimpleDateFormat ss = new SimpleDateFormat(
			"MM-dd HH:mm", Locale.KOREA);

	public static DevilController getInstance() {
		if (_instance == null) {
			_instance = new DevilController();
		}
		return _instance;
	}

	@Override
	public void run() {
		try {
			while (true) {
				Thread.sleep(1000);
				/** 오픈 * */
				if (!isOpen() && !isGmOpen)
					continue;
				if (L1World.getInstance().getAllPlayers().size() <= 0)
					continue;

				isGmOpen = false;

				/** 오픈 메세지 * */
				L1SpawnUtil.spawn2(33430, 32802, (short) 4, 200001, 0, 3600 * 1000, 0);
				L1SpawnUtil.spawn2(33430, 32802, (short) 4, 199997, 0, 3600 * 1000, 0);
		//		L1SpawnUtil.spawn2(33430, 32802, (short) 4, 777854, 0, 3600 * 1000, 0);	
				
				L1SpawnUtil.spawn2(33430, 32824, (short) 4, 200001, 0, 3600 * 1000, 0);
				L1SpawnUtil.spawn2(33430, 32824, (short) 4, 199997, 0, 3600 * 1000, 0);
			//	L1SpawnUtil.spawn2(33430, 32824, (short) 4, 777854, 0, 3600 * 1000, 0);	
				
				L1SpawnUtil.spawn2(32693, 32798, (short) 450, 200001, 0, 3600 * 1000, 0);
				L1SpawnUtil.spawn2(32693, 32798, (short) 450, 199997, 0, 3600 * 1000, 0);
			//	L1SpawnUtil.spawn2(32693, 32798, (short) 450, 777854, 0, 3600 * 1000, 0);	
				
				L1SpawnUtil.spawn2(32863, 32839, (short) 530, 45955, 0, 3600 * 1000, 0);	
			/*	reset(530, 4058);
				reset(531, 4059);
				reset(531, 4060);
				reset(531, 4061);
				reset(532, 4062);
				reset(533, 4063);
				reset(533, 4064);
				reset(533, 4065);
				reset(534, 4066);*/
				//UserRankingController.isRenewal = true; //랭킹갱신 3시간에 한번
				L1World.getInstance().broadcastPacketToAll(
						new S_SystemMessage("\\aG구 라스타바드던전 4층 이동포탈이 생성되었습니다."));
				L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,"\\f3구 라스타바드던전 4층 이동포탈이 생성되었습니다."));
				/** 악마왕영토 시작* */
				setDevilStart(true);

				/** 실행 1시간 시작* */
				
				
				Thread.sleep(3600000L); // 3600000L 1시간 10분정도

				/** 1시간 후 자동 텔레포트* */
				TelePort();
				close(); //추가
				Thread.sleep(5000L);
				TelePort2();

				/** 종료 * */
				End();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 오픈 시각을 가져온다
	 * 
	 * @return (Strind) 오픈 시각(MM-dd HH:mm)
	 */
	public String OpenTime() {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(sTime);
		return ss.format(c.getTime());
	}

	/**
	 * 영토가 열려있는지 확인
	 * 
	 * @return (boolean) 열려있다면 true 닫혀있다면 false
	 */
	private boolean isOpen() {
		NowTime = getTime();
		if ((Integer.parseInt(NowTime) % LOOP) == 0)
			return true;
		return false;
	}

	/**
	 * 실제 현재시각을 가져온다
	 * 
	 * @return (String) 현재 시각(HH:mm)
	 */
	private String getTime() {
		return s.format(Calendar.getInstance().getTime());
	}

	/** 아덴마을로 팅기게* */
	private void TelePort() {
		for (L1PcInstance c : L1World.getInstance().getAllPlayers()) {
			switch (c.getMap().getId()) {
			case 530:
			case 531:
			case 532:
			case 533:
			case 534:
			case 535:
			case 536:
				c.stopHpRegenerationByDoll();
				c.stopMpRegenerationByDoll();
				new L1Teleport().teleport(c, 33437, 32799, (short) 4, 4, true);
				L1World.getInstance().broadcastPacketToAll(
						new S_SystemMessage("\\aG구 라스타바드던전 4층 이동포탈이 소멸되었습니다."));
				L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,"\\f3구 라스타바드던전 4층 이동포탈이 소멸되었습니다."));
				break;
			default:
				break;
			}
		}
	}
	/**캐릭터가 죽었다면 종료시키기**/
	 private void close() {
	  for(L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
	   if ((pc.getMap().getId() >= 530 && pc.getMap().getId() <= 536) && pc.isDead()) {
	    pc.stopHpRegenerationByDoll();
	    pc.stopMpRegenerationByDoll();
	    pc.sendPackets(new S_Disconnect());
	   }
	  }
	 }

	/** 아덴마을로 팅기게* */
	private void TelePort2() {
		for (L1PcInstance c : L1World.getInstance().getAllPlayers()) {
			switch (c.getMap().getId()) {
			case 530:
			case 531:
			case 532:
			case 533:
			case 534:
			case 535:
			case 536:
				c.stopHpRegenerationByDoll();
				c.stopMpRegenerationByDoll();
				new L1Teleport().teleport(c, 33437, 32799, (short) 4, 4, true);
				break;
			default:
				break;
			}
		}
	}
	
	private void reset(int mapId, int relatedDoor) {
		if(relatedDoor == 0) return;
		L1DoorInstance door = DoorSpawnTable.getInstance().getDoor(relatedDoor);
		if(door != null) {
			door.setDead(false); // 있으나 마나?
			door.close();
		}
	}
	/** 종료 * */
	private void End() {
		L1World.getInstance().broadcastServerMessage("구 라스타바드던전 4층 이동포탈이 소멸되었습니다.");
		setDevilStart(false);
		reset(530, 4058);
		reset(531, 4059);
		reset(531, 4060);
		reset(531, 4061);
		reset(532, 4062);
		reset(533, 4063);
		reset(533, 4064);
		reset(533, 4065);
		reset(534, 4066);
	}
}
