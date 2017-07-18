package l1j.server.RobotSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.command.executor.L1Robot4;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.utils.SQLUtil;

public class RobotAIThread implements Runnable {

	static private final int Scarecrow = 1; // かかし
	static private final int Hunt = 2; // 狩猟

	// 人工知能スレッドを処理してもいることを確認の。
	// static private boolean running;
	// スレッドが動作中休憩時間の値。
	static private long sleep;
	// 管理されるロボットのオブジェクト
	static private ArrayList<L1PcInstance> huntList;
	// 管理されるロボットのオブジェクト
	static private ArrayList<L1PcInstance> scarecrowList;
	// テレポートする座標のリスト
	static private List<RobotLocation> list_location;
	// テレポートする座標のリスト
	static private List<RobotMent> list_ment;
	// 生成されるケリー人のリスト
	static private List<RobotName> list_name;
	static public int list_name_idx;
	static private List<RobotFishing> list_fish;

	/**
	 * 初期化処理関数です。
	 */
	static public void init() {
		// 変数の初期化。
		sleep = 10;
		// running = true;
		list_name_idx = 0;
		huntList = new ArrayList<L1PcInstance>();
		scarecrowList = new ArrayList<L1PcInstance>();
		list_location = new ArrayList<RobotLocation>();
		list_ment = new ArrayList<RobotMent>();
		list_name = new ArrayList<RobotName>();
		list_fish = new ArrayList<RobotFishing>();
		// ロボット人工知能のスレッドが有効。
		new Thread(new RobotAIThread()).start();
		new Thread(new RobotScarecrow()).start();
		// ディビから情報を抽出する。
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();

			pstm = con.prepareStatement("SELECT * FROM robot_location where count = '1'");
			rs = pstm.executeQuery();
			while (rs.next()) {
				RobotLocation rl = new RobotLocation();
				rl.uid = rs.getInt("uid");
				// rl.istown = rs.getBoolean("istown");
				rl.x = rs.getInt("x");
				rl.y = rs.getInt("y");
				rl.map = rs.getInt("map");
				rl.etc = rs.getString("etc");

				list_location.add(rl);
			}
			SQLUtil.close(rs);
			SQLUtil.close(pstm);

			pstm = con.prepareStatement("SELECT * FROM robot_message");
			rs = pstm.executeQuery();
			while (rs.next()) {
				RobotMent rm = new RobotMent();
				rm.uid = rs.getInt("uid");
				rm.type = rs.getString("type");
				rm.ment = rs.getString("ment");

				list_ment.add(rm);
			}
			SQLUtil.close(rs);
			SQLUtil.close(pstm);

			pstm = con.prepareStatement("SELECT * FROM robot_name");
			rs = pstm.executeQuery();
			while (rs.next()) {
				RobotName rn = new RobotName();
				rn.uid = rs.getInt("uid");
				rn.name = rs.getString("name");
				list_name.add(rn);
			}
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			// pstm = con.prepareStatement("SELECT * FROM robot_fishing");
			// rs = pstm.executeQuery();
			// while (rs.next()) {
			// RobotFishing rn = new RobotFishing();
			// rn.x = rs.getInt("x");
			// rn.y = rs.getInt("y");
			// rn.map = rs.getInt("mapid");
			// rn.heading = rs.getInt("heading");
			// rn.fishX = rs.getInt("fishingX");
			// rn.fishY = rs.getInt("fishingY");
			// list_fish.add(rn);
			// }

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public static boolean doesCharNameExist(String name) {
		boolean result = true;
		java.sql.Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT name FROM robot_name WHERE name=?");
			pstm.setString(1, name);
			rs = pstm.executeQuery();
			result = rs.next();
		} catch (SQLException e) {

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return result;
	}

	/**
	 * 終了処理関数です。
	 */
	static public void close() {
		// running = false;
	}

	/**
	 * 管理リストに追加要求処理関数です。
	 * 
	 * @param robot
	 */
	static public void append(L1PcInstance robot, int type) {
		switch (type) {
		case Scarecrow:
			synchronized (scarecrowList) {
				if (!huntList.contains(robot)) {
					scarecrowList.add(robot);
				} else {
					break;
				}
			}
			break;
		case Hunt:
			synchronized (huntList) {
				if (!scarecrowList.contains(robot)) {
					huntList.add(robot);
				} else {
					break;
				}
			}
			break;
		default:
			break;
		}

	}

	/**
	 * 管理リストから削除要求の処理関数です。
	 * 
	 * @param robot
	 */
	static public void remove(L1PcInstance robot, int type) {
		switch (type) {
		case Scarecrow:
			synchronized (scarecrowList) {
				scarecrowList.remove(robot);
			}
			break;
		case Hunt:
			synchronized (huntList) {
				huntList.remove(robot);
			}
			break;
		default:
			break;
		}
	}

	/**
	 * オート狩りキャラクターリスト
	 */
	private static Collection<L1PcInstance> huntValues;

	static public Collection<L1PcInstance> getHunt() {
		Collection<L1PcInstance> vs = huntValues;
		return (vs != null) ? vs : (huntValues = Collections.unmodifiableCollection(huntList));
	}

	@Override
	public void run() {
		try {
			long time = System.currentTimeMillis();
			while (true) {
				try {
					time = System.currentTimeMillis();
					// ロボット人工知能活性化。
					for (L1PcInstance robot : huntList) {
						if (robot == null) {
							continue;
						}
						robot.getRobotAi().toAI(time);
					}
				} catch (Exception e) {
					// System.out.println（ "ロボットスレッドが異常終了していAI再起動中の重複エラー
					// 発生！ "）;
					// e.printStackTrace();
				} finally {
					try {
						Thread.sleep(sleep);
					} catch (Exception e) {
					}
				}
			}
		} catch (Exception e) {
		} finally {
			try {
				Thread.sleep(sleep);
			} catch (Exception e) {
			}
		}
	}

	/** かかし人工知能 **/
	static class RobotScarecrow implements Runnable {
		@Override
		public void run() {
			try {
				long time = System.currentTimeMillis();
				while (true) {
					try {
						time = System.currentTimeMillis();
						// ロボット人工知能活性化。
						for (L1PcInstance robot : scarecrowList) {
							if (robot == null) {
								continue;
							}
							robot.getRobotAi().scarecrowProcess(time);
						}
					} catch (Exception e) {
						// System.out.println("ロボットスレッドが異常終了していAI再起動中の重複エラー
						// 発生！ "）;
						// e.printStackTrace();
					} finally {
						try {
							Thread.sleep(sleep);
						} catch (Exception e) {
						}
					}
				}
			} catch (Exception e) {
			} finally {
				try {
					Thread.sleep(sleep);
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * 狩場テレポート座標戻り。
	 * 
	 * @param type
	 * @return
	 */
	static public RobotLocation getLocation() {
		if (list_location.size() == 0)
			return null;

		return list_location.get(L1Robot4.random(0, list_location.size() - 1));
	}

	static public List<RobotMent> getRobotMent() {
		return list_ment;
	}

	static public List<RobotName> getRobotName() {
		return list_name;
	}

	static public List<RobotFishing> getRobotFish() {
		return list_fish;
	}

	static public String getName() {
		try {
			// 名前のリスト巡回。
			for (; list_name_idx < list_name.size();) {
				String name = list_name.get(list_name_idx++).name;
				Connection con = null;
				PreparedStatement pstm = null;
				ResultSet rs = null;
				// System.out.println(name);
				try {
					con = L1DatabaseFactory.getInstance().getConnection();
					pstm = con.prepareStatement("SELECT * FROM characters WHERE char_name=?");
					pstm.setString(1, name);
					rs = pstm.executeQuery();
					if (!rs.next())
						return name;
				} catch (SQLException e) {
				} finally {
					SQLUtil.close(rs);
					SQLUtil.close(pstm);
					SQLUtil.close(con);
				}
			}
		} catch (Exception e) {
		}
		// ディーに類似した名前が存在確認した。
		// 最後に、このインデックスに達した場合は無視。
		return null;
	}

	static public void reload() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;

		list_location.clear();
		list_ment.clear();
		list_name.clear();

		try {
			con = L1DatabaseFactory.getInstance().getConnection();

			pstm = con.prepareStatement("SELECT * FROM robot_location where count = '1'");
			rs = pstm.executeQuery();
			while (rs.next()) {
				RobotLocation rl = new RobotLocation();
				rl.uid = rs.getInt("uid");
				// rl.istown = rs.getBoolean("istown");
				rl.x = rs.getInt("x");
				rl.y = rs.getInt("y");
				rl.map = rs.getInt("map");
				rl.etc = rs.getString("etc");

				list_location.add(rl);
			}
			SQLUtil.close(rs);
			SQLUtil.close(pstm);

			pstm = con.prepareStatement("SELECT * FROM robot_message");
			rs = pstm.executeQuery();
			while (rs.next()) {
				RobotMent rm = new RobotMent();
				rm.uid = rs.getInt("uid");
				rm.type = rs.getString("type");
				rm.ment = rs.getString("ment");

				list_ment.add(rm);
			}
			SQLUtil.close(rs);
			SQLUtil.close(pstm);

			pstm = con.prepareStatement("SELECT * FROM robot_name");
			rs = pstm.executeQuery();
			while (rs.next()) {
				RobotName rn = new RobotName();
				rn.uid = rs.getInt("uid");
				rn.name = rs.getString("name");
				list_name.add(rn);
			}
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			pstm = con.prepareStatement("SELECT * FROM robot_fishing");
			rs = pstm.executeQuery();
			while (rs.next()) {
				RobotFishing rn = new RobotFishing();
				rn.x = rs.getInt("x");
				rn.y = rs.getInt("y");
				rn.map = rs.getInt("mapid");
				rn.heading = rs.getInt("heading");
				rn.fishX = rs.getInt("fishingX");
				rn.fishY = rs.getInt("fishingY");
				list_fish.add(rn);
			}
		} catch (SQLException e) {

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
}
