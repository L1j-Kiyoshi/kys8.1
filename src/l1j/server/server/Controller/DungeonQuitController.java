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
	private final int status_Wait = 0;// 進行
	private final int status_Open = 1;
	private final int status_GetOn = 2;
	private final int status_End = 3;//

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
				case status_Wait:
					Thread.sleep(10000);
					/** オープンでなければ進行 **/
					if (isgameStart == false) {
						continue;
					}
					Status = status_Open;
					continue;
				case status_Open:
					L1World.getInstance().broadcastServerMessage("通知：しばらくして、すべてのインスタンスダンジョンの時間が初期化されます。");
					Thread.sleep(3000L);
					Status = status_GetOn;
					continue;
				case status_GetOn:
					Thread.sleep(5000L);
					init();
					Status = status_End;
					continue;
				case status_End:
					L1World.getInstance().broadcastServerMessage("通知：すべてのインスタンスダンジョンの時間が初期化されました。");
					isgameStart = false;
					Status = status_Wait;
					continue;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
				/** ダンジョン初期化 **/
	 public void init() {
		for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			if (pc == null || pc.getNetConnection() == null || pc.noPlayerCK || pc.noPlayerck2) {
				continue;
			}
			try {
				pc.setGirandungeonTime(0);//ギラン監獄
				pc.setOrendungeonTime(0);//象牙の塔ヤヒ
				pc.setDrageonTime(0);//龍界
				//pc.setRadungeonTime(0);//ラバー
				pc.setSomeTime(0);//モンソム
				pc.setSoulTime(0);//ゴム
				pc.seticedungeonTime(0);//オルドン
				pc.setislandTime(0);//巻い
				pc.setnewdodungeonTime(0);//象牙の塔バルログ
				initDungeon(pc);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void initDungeon(L1PcInstance pc) throws SQLException {
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
