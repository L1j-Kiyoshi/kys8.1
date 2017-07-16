package l1j.server.GameSystem.Boss;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;


public class NewBossSpawnTable {
	private static NewBossSpawnTable _instance;

	public static NewBossSpawnTable getInstance() {
		if (_instance == null) {
			_instance = new NewBossSpawnTable();
		}
		return _instance;
	}

	private static Logger _log = Logger.getLogger(NewBossSpawnTable.class.getName());

	private ArrayList<BossTemp> bosslist = new ArrayList<BossTemp>();

	private Random rnd = new Random(System.nanoTime());

	public ArrayList<BossTemp> getlist() {
		return bosslist;
	}
	
	public static void reload() {
		NewBossSpawnTable oldInstance = _instance;
		_instance = new NewBossSpawnTable();
		oldInstance.bosslist.clear();
	}
	
	

	private NewBossSpawnTable() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		
		FileOutputStream fos;
		BufferedOutputStream bos;
		
		try {
			
			
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM spawnlist_boss_new");
			rs = pstm.executeQuery();

			while (rs.next()) {
				BossTemp temp = new BossTemp();
				temp.npcid = rs.getInt("npcid");
				String text = rs.getString("info");
				temp.rndLoc = rs.getInt("rndXY");
				temp.Groupid = rs.getInt("groupid");
				temp.isYn = rs.getInt("is_yn") == 0 ? false : true;
				temp.isMent = rs.getInt("is_ment") == 0 ? false : true;
				temp.Ment = rs.getString("ment");

				StringTokenizer s = new StringTokenizer(text, "\r\n");
				int number = 0;
				while (s.hasMoreElements()) {
					String temp2 = "";
					StringTokenizer values = new StringTokenizer(s.nextToken(), "出現デイタイムランダム削除座標範囲グループYNメッセージコメント");
					while (values.hasMoreElements()) {// 空白を削除
						temp2 += values.nextToken();
					}
					// System.out.println("temp2 = " + temp2);
					if (number == 0) { // 日付月火水木~~
						StringTokenizer Day = new StringTokenizer(temp2, ",");
						ArrayList<Integer> list = new ArrayList<Integer>();
						while (Day.hasMoreElements()) {
							String day = Day.nextToken();
							if (day.equalsIgnoreCase("日")) {
								list.add(new Integer(0));
							} else if (day.equalsIgnoreCase("月")) {
								list.add(new Integer(1));
							} else if (day.equalsIgnoreCase("火")) {
								list.add(new Integer(2));
							} else if (day.equalsIgnoreCase("水")) {
								list.add(new Integer(3));
							} else if (day.equalsIgnoreCase("木")) {
								list.add(new Integer(4));
							} else if (day.equalsIgnoreCase("金")) {
								list.add(new Integer(5));
							} else if (day.equalsIgnoreCase("土")) {
								list.add(new Integer(6));
							}
						}
						temp.Day = new int[list.size()];
						for (int i = 0; i < list.size(); i++) {
							temp.Day[i] = list.get(i);
						}
					} else if (number == 1) { // 出現時間
						StringTokenizer mdata = new StringTokenizer(temp2, ",");
						ArrayList<Integer> Hourlist = new ArrayList<Integer>();
						ArrayList<Integer> Minutelist = new ArrayList<Integer>();
						while (mdata.hasMoreElements()) {
							String Times = mdata.nextToken();
							StringTokenizer Hours = new StringTokenizer(Times, "時");
							String Hour = Hours.nextToken();
							StringTokenizer Minutes = new StringTokenizer(Hours.nextToken(), "分");
							String Minute = Minutes.nextToken();
							Hourlist.add(Integer.parseInt(Hour.trim()));
							Minutelist.add(Integer.parseInt(Minute.trim()));
						}

						temp.SpawnHour = new int[Hourlist.size()];
						temp.SpawnMinute = new int[Hourlist.size()];
						for (int i = 0; i < Hourlist.size(); i++) {
							int Hour = Hourlist.get(i);
							int Minute = Minutelist.get(i);
							temp.SpawnHour[i] = Hour;
							temp.SpawnMinute[i] = Minute;
							 //System.out.println("Hour = " + Hour);
							 //System.out.println("Minute = " + Minute);
						}
						
					} else if (number == 2) { // ランダムタイム分
						StringTokenizer mdata = new StringTokenizer(temp2, "分");
						temp.rndTime = Integer.parseInt(mdata.nextToken().trim());
						// System.out.println("ランダム時間 = " + temp.rndTime);
					} else if (number == 3) { // 削除時間秒
						StringTokenizer mdata = new StringTokenizer(temp2, "超");
						temp.DeleteTime = Integer.parseInt(mdata.nextToken().trim());
						// System.out.println("削除時間秒= " + temp.DeleteTime);
					} else if (number == 4) { // 出現座標
						StringTokenizer mdata = new StringTokenizer(temp2, ",");
						temp.SpawnLoc = new int[3];
						temp.SpawnLoc[0] = Integer.parseInt(mdata.nextToken().trim());
						temp.SpawnLoc[1] = Integer.parseInt(mdata.nextToken().trim());
						temp.SpawnLoc[2] = Integer.parseInt(mdata.nextToken().trim());
						// System.out.println("出現座標x = " + temp.SpawnLoc[0]);
						// System.out.println("出現座標y = " + temp.SpawnLoc[1]);
						// System.out.println("出現座標m = " + temp.SpawnLoc[2]);
					}

					number++;
				}
				bosslist.add(temp);
			}

			
			
			/*for (BossTemp temp : bosslist) {
				temp.NearTime = new int[temp.SpawnHour.length];
				for (int i = 0; i < temp.SpawnHour.length; i++) {
					if (temp.rndTime != 0) {
						int Hour = temp.SpawnHour[i];
						int rndtime = rnd.nextInt(temp.rndTime) + 1;
						int Minute = temp.SpawnMinute[i] + rndtime;
						if (Minute >= 120) {
							Hour += 2;
							Minute -= 120;
						} else if (Minute >= 60) {
							Hour++;
							Minute -= 60;
						}
						StringBuffer NewText = new StringBuffer();
						NewText.append(Hour);
						NewText.append(Minute < 10 ? "0" + Minute : Minute);
						temp.NearTime[i] = Integer.parseInt(NewText.toString().trim());
						// System.out.println("temp.NearTime[i] = " + temp.NearTime[i]);
					} else {
						StringBuffer NewText = new StringBuffer();
						NewText.append(temp.SpawnHour[i]);
						NewText.append(temp.SpawnMinute[i] < 10 ? "0" + temp.SpawnMinute[i] : temp.SpawnMinute[i]);
						temp.NearTime[i] = Integer.parseInt(NewText.toString().trim());
						// System.out.println("temp.NearTime[i] = " + temp.NearTime[i]);
						
					}
				}
			}*/
		
		} catch (SQLException e) {
			e.printStackTrace();
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/*
	 * 1~6 0 
	 * 出現デー：月、火、水、木、金、土、日
	 * 出現時間：2時00分、5時30分
	 * ランダムタイム：0分
	 *削除タイム：3600秒
	 * 出現座標：32726、32832、603
	 * ランダム範囲：0
	 * グループ出現：0
	 * YNメッセージ：1
	 * 出現コメント：1
	 */
	public static class BossTemp {
		public int npcid;
		public boolean isSpawn;
		public int[] Day;
		public int[] SpawnHour;
		public int[] SpawnMinute;
		public int[] NearTime;
		public int rndTime;
		public int DeleteTime;
		public int[] SpawnLoc;
		public int rndLoc;
		public int Groupid;
		public boolean isYn;
		public boolean isMent;
		public String Ment;
		public int timeM;
	}
}