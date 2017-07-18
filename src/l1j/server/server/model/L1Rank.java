package l1j.server.server.model;

/**
 * ランキングシステム<br>
 * ランキングの演算は、DBへのアクセスせずにMapを利用した。
 * 
 * @author
 */
public class L1Rank {

	public L1Rank() { }

	private int id;
	public int getId() { return id; }
	public void setId(int a) { id = a; }

	private String name;
	public String getName() { return name; }
	public void setName(String s) { name = s; }

	private String accountName;
	public String getAccountName() { return accountName; }
	public void setAccountName(String s) { accountName = s; }

	private int type;
	public int getType() { return type; }
	public void setType(int a) { type = a; }

	private int exp;
	public int getExp() { return exp; }
	public void setExp(int a) { exp = a; }

	private int totalRank;
	public int getTotalRank() { return totalRank; }
	public void setTotalRank(int a) { totalRank = a; }

	private int classRank;
	public int getClassRank() { return classRank; }
	public void setClassRank(int a) { classRank = a; }

	private int oldTotalRank;
	public int getOldTotalRank() { return oldTotalRank; }
	public void setOldTotalRank(int a) { oldTotalRank = a; }

	private int oldClassRank;
	public int getOldClassRank() { return oldClassRank; }
	public void setOldClassRank(int a) { oldClassRank = a; }

	private boolean totalUpChance;
	public boolean getTotalUpChance() { return totalUpChance; }
	public void setTotalUpChance(boolean f) { totalUpChance = f; }

	private boolean totalOvertakingRisk;
	public boolean getTotalOvertakingRisk() { return totalOvertakingRisk; }
	public void setTotalOvertakingRisk(boolean f) { totalOvertakingRisk = f; }

	private boolean classUpChance;
	public boolean getClassUpChance() { return classUpChance; }
	public void setClassUpChance(boolean f) { classUpChance = f; }

	private boolean classOverTakingRisk;
	public boolean getClassOverTakingRisk() { return classOverTakingRisk; }
	public void setClassOverTakingRisk(boolean f) { classOverTakingRisk = f; }

	private int rankLevel = -1;

	public int getRankLevel() { // ランキングバフレベルは、唯一の全体ランキングのみ関係があります。クラスランキングはバフと関連なし（本サーバー火）
		if (rankLevel != -1) return rankLevel;
		if (totalRank <= 10) rankLevel = 4;
		else if (totalRank <= 30) rankLevel = 3;
		else if (totalRank <= 60) rankLevel = 2;
		else if (totalRank <= 100) rankLevel = 1;
		else rankLevel = 0;
		return rankLevel;
	}
}