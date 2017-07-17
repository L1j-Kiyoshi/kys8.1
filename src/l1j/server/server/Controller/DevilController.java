package l1j.server.server.Controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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

	public boolean isGmOpen = false; // 追加

	private String NowTime = "";

	//時間間隔
	private static final int LOOP = Config.GLUDIN_DUNGEON_OPEN_CYCLE;;

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
				/** オープン * */
				if (!isOpen() && !isGmOpen)
					continue;
				if (L1World.getInstance().getAllPlayers().size() <= 0)
					continue;

				isGmOpen = false;

				/** オープンメッセージ * */
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
				//UserRankingController.isRenewal = true; //ランキング更新3時間に一回
				L1World.getInstance().broadcastPacketToAll(
						new S_SystemMessage("\\aG旧ラスタバドのダンジョン4階に移動ポータルが作成されました。"));
				L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,"\\f3旧ラスタバドのダンジョン4階に移動ポータルが作成されました。"));
				/** 悪魔王の領土を開始* */
				setDevilStart(true);

				/** 実行1時間開始* */
				
				
				Thread.sleep(3600000L); // 3600000L 1時間10分程度

				/** 1時間後に自動テレポート* */
				TelePort();
				close(); //追加
				Thread.sleep(5000L);
				TelePort2();

				/** 終了 * */
				End();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * オープン時刻を持って来る
	 * 
	 * @return (Strind）オープン時刻（MM-dd HH：mm）
	 */
	public String OpenTime() {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(sTime);
		return ss.format(c.getTime());
	}

	/**
	 * 領土が開いていることを確認
	 * 
	 * @return (boolean) 開いている場合true閉じている場合false
	 */
	private boolean isOpen() {
		NowTime = getTime();
		if ((Integer.parseInt(NowTime) % LOOP) == 0)
			return true;
		return false;
	}

	/**
	 * 実際、現在時刻を持って来る
	 * 
	 * @return (String) 現在時刻（HH：mm）
	 */
	private String getTime() {
		return s.format(Calendar.getInstance().getTime());
	}

	/** アデン村にティンギが* */
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
						new S_SystemMessage("\\aG旧ラスタバドのダンジョン4階に移動ポータルが消滅しました。"));
				L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,"\\f3旧ラスタバドのダンジョン4階に移動ポータルが消滅しました。"));
				break;
			default:
				break;
			}
		}
	}
	/**キャラクターが死亡した場合、終了させる**/
	 private void close() {
	  for(L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
	   if ((pc.getMap().getId() >= 530 && pc.getMap().getId() <= 536) && pc.isDead()) {
	    pc.stopHpRegenerationByDoll();
	    pc.stopMpRegenerationByDoll();
	    pc.sendPackets(new S_Disconnect());
	   }
	  }
	 }

	/** アデン村にティンギが* */
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
			door.setDead(false); // が、マナ？
			door.close();
		}
	}
	/** 終了 * */
	private void End() {
		L1World.getInstance().broadcastServerMessage("旧ラスタバドのダンジョン4階に移動ポータルが消滅しました。");
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
