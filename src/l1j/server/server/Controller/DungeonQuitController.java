package l1j.server.server.Controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;

public class DungeonQuitController implements Runnable {

	public static final int SLEEP_TIME = 1000;
	
	private static DungeonQuitController _instance;

	/** ゲームオープンの有無 **/
	public boolean isgameStart = false;

	/** ゲームの状態 **/
	public int Status = 0;// 進行状況
	private final int 대기 = 0;// 進行
	private final int 오픈 = 1;
	private final int 진행 = 2;
	private final int 종료 = 3;//

	public static DungeonQuitController getInstance() {
		if (_instance == null) {
			_instance = new DungeonQuitController();
		}
		return _instance;
	}

	@Override
	public void run() {
		try {
			while (true) {
				switch (Status) {
				case 대기:
					Thread.sleep(10000);
					/** オープンでなければ進行 **/
					if (isgameStart == false) {
						continue;
					}
					Status = 오픈;
					continue;
				case 오픈:
					L1World.getInstance().broadcastServerMessage("通知：しばらくして、すべてのインスタンスダンジョンの時間が初期化されます。");
					Thread.sleep(3000L);
					Status = 진행;
					continue;
				case 진행:
					Thread.sleep(5000L);
					초기화();
					Status = 종료;
					continue;
				case 종료:
					L1World.getInstance().broadcastServerMessage("通知：すべてのインスタンスダンジョンの時間が初期化されました。");
					isgameStart = false;
					Status = 대기;
					continue;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
				/** ダンジョン初期化 **/
	 public void 초기화() {
		for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			if (pc == null || pc.getNetConnection() == null || pc.noPlayerCK || pc.noPlayerck2) {
				continue;
			}
			try {
				pc.setGirandungeonTime(0);//技監
				pc.setOrendungeonTime(0);//象牙の塔ヤヒ
				pc.setDrageonTime(0);//龍界
				//pc.setRadungeonTime(0);//ラバー
				pc.setSomeTime(0);//モンソム
				pc.setSoulTime(0);//ゴム
				pc.seticedungeonTime(0);//オルドン
				pc.setislandTime(0);//巻い
				pc.setnewdodungeonTime(0);//象牙の塔バルログ
				던전초기화(pc);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void 던전초기화(L1PcInstance pc) throws SQLException {
		Connection cc = null;
		PreparedStatement p = null;
		try {
			cc = L1DatabaseFactory.getInstance().getConnection();
			p = cc.prepareStatement("update characters set GiranTime=0,OrenTime=0,DrageonTime=0,SomeTime=0,SoulTime=0,iceTime=0,islanddungeonTime=0,newdoTime=0");
			p.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			p.close();
			cc.close();
		}
	}
}
