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

	/** 게임오픈유무 **/
	public boolean isgameStart = false;

	/** 게임상태 **/
	public int Status = 0;// 진행 상태
	private final int 대기 = 0;// 진행
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
					/** 오픈이 아니면 진행 **/
					if (isgameStart == false) {
						continue;
					}
					Status = 오픈;
					continue;
				case 오픈:
					L1World.getInstance().broadcastServerMessage("알림: 잠시 후 모든 인스턴스 던전 시간이 초기화 됩니다.");
					Thread.sleep(3000L);
					Status = 진행;
					continue;
				case 진행:
					Thread.sleep(5000L);
					초기화();
					Status = 종료;
					continue;
				case 종료:
					L1World.getInstance().broadcastServerMessage("알림: 모든 인스턴스 던전 시간이 초기화 되었습니다.");
					isgameStart = false;
					Status = 대기;
					continue;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
				/** 던전 초기화 **/
	 public void 초기화() {
		for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			if (pc == null || pc.getNetConnection() == null || pc.noPlayerCK || pc.noPlayerck2) {
				continue;
			}
			try {
				pc.setGirandungeonTime(0);//기감
				pc.setOrendungeonTime(0);//상아탑 야히
				pc.setDrageonTime(0);//용계
				//pc.setRadungeonTime(0);//라바
				pc.setSomeTime(0);//몽섬
				pc.setSoulTime(0);//고무
				pc.seticedungeonTime(0);//얼던
				pc.setislandTime(0);//말던
				pc.setnewdodungeonTime(0);//상아탑 발록
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
