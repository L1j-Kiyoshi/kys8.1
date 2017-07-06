package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.GameClient;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Rank;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Ranking2;
import l1j.server.server.utils.SQLUtil;

/**
 * 랭킹 시스템<br>
 * 랭킹에 대한 연산은 DB에 접근하지 않고 Map을 활용.
 * 
 * @author
 */
public class RankTable {

	private final ReentrantLock lock;
	private Map<Integer, int[]> old_ranks;
	private LinkedList<L1Rank> 전체, 군주, 기사, 요정, 법사, 다엘, 용기, 환술, 전사;
	private long lastUpdateTime;

	private RankTable() {
		lock = new ReentrantLock();
		old_ranks = new HashMap<Integer, int[]>();
		전체 = new LinkedList<L1Rank>();
		군주 = new LinkedList<L1Rank>();
		기사 = new LinkedList<L1Rank>();
		요정 = new LinkedList<L1Rank>();
		법사 = new LinkedList<L1Rank>();
		다엘 = new LinkedList<L1Rank>();
		용기 = new LinkedList<L1Rank>();
		환술 = new LinkedList<L1Rank>();
		전사 = new LinkedList<L1Rank>();
		load();
	}

	/**
	 * 서버 구동시 데이터 로드.
	 */
	private void load() {
		// 데이터베이스 로드시에 갱신 시간이 되어버릴 경우에 대비하여 반드시 동기화.
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			resetUpdateTime();
			int order = 0;
			try (Connection con = L1DatabaseFactory.getInstance().getConnection();
					PreparedStatement pstm = con.prepareStatement("SELECT * FROM character_rank ORDER BY exp DESC");
					ResultSet rs = pstm.executeQuery()) {
				while (rs.next()) {
					L1Rank rank = new L1Rank();
					rank.setId(rs.getInt("char_id"));
					rank.setAccountName(rs.getString("account_name"));
					rank.setName(rs.getString("name"));
					int type = rs.getInt("type");
					rank.setType(type);
					rank.setExp(rs.getInt("exp"));
					int total = rs.getInt("total_rank");
					rank.setTotalRank(total);
					int classes = rs.getInt("class_rank");
					rank.setClassRank(classes);
					rank.setOldTotalRank(rs.getInt("old_total_rank"));
					rank.setOldClassRank(rs.getInt("old_class_rank"));
					rank.전체상승기회(rs.getBoolean("total_step_up"));
					rank.전체추월위험(rs.getBoolean("total_step_down"));
					rank.클래스상승기회(rs.getBoolean("class_step_up"));
					rank.클래스추월위험(rs.getBoolean("class_step_down"));
					switch (type) {
					case 0: 군주.add(rank); break;
					case 1: 기사.add(rank); break;
					case 2: 요정.add(rank); break;
					case 3: 법사.add(rank); break;
					case 4: 다엘.add(rank); break;
					case 5: 용기.add(rank); break;
					case 6: 환술.add(rank); break;
					case 7: 전사.add(rank); break;
					}
					전체.add(order++, rank);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 랭킹에 관한 모든 데이터를 업데이트.
	 */
	public void updateRank() {//랭킹갱신
//		System.out.println("CMD MSG: 랭킹이 갱신되었습니다.");
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			resetUpdateTime();
			setOldRank();
			reLoadCurrentRank(); 
			operateTotalRank();
			operateClassRank();
			writeDatabase(); 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	private void resetUpdateTime() {
		lastUpdateTime = System.currentTimeMillis() / 1000;
	}

	/**
	 * 기존의 랭킹을 저장.
	 */
	private void setOldRank() {
		old_ranks.clear();
		for (int i = 0; i < 전체.size(); i++) {
			L1Rank rank = 전체.get(i);
			if (rank == null) break;
			old_ranks.put(rank.getId(), new int[] { rank.getTotalRank(), rank.getClassRank() });
		}
	}

	/**
	 * 현재의 모든 랭킹을 새롭게 로드.
	 */
	private void reLoadCurrentRank() {
		allClear();
		int 전체 = 0, 군주 = 0, 기사 = 0, 요정 = 0, 법사 = 0, 다엘 = 0, 용기 = 0, 환술 = 0, 전사 = 0;
		try (Connection con = L1DatabaseFactory.getInstance().getConnection();
				PreparedStatement pstm = con.prepareStatement("SELECT * FROM characters WHERE AccessLevel <> 9999 ORDER BY exp DESC LIMIT 1600");
				ResultSet rs = pstm.executeQuery()) {
			while (rs.next()) {
				L1Rank rank = new L1Rank();
				rank.setId(rs.getInt("objid"));
				rank.setAccountName(rs.getString("account_name"));
				rank.setName(rs.getString("char_name"));
				rank.setType(rs.getInt("Type"));
				int exp = rs.getInt("Exp");
				rank.setExp(exp);
				rank.setTotalRank(전체 + 1);
				switch (rank.getType()) {
				case 0: this.군주.add(rank); rank.setClassRank(++군주); break;
				case 1: this.기사.add(rank); rank.setClassRank(++기사); break;
				case 2: this.요정.add(rank); rank.setClassRank(++요정); break;
				case 3: this.법사.add(rank); rank.setClassRank(++법사); break;
				case 4: this.다엘.add(rank); rank.setClassRank(++다엘); break;
				case 5: this.용기.add(rank); rank.setClassRank(++용기); break;
				case 6: this.환술.add(rank); rank.setClassRank(++환술); break;
				case 7: this.전사.add(rank); rank.setClassRank(++전사); break;
				}
				this.전체.add(전체, rank);
				전체++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 현재의 전체 랭킹과 기존의 전체 랭킹을 비교 분석.
	 */
	private void operateTotalRank() {
		for (int i = 0; i < 전체.size(); i++) {
			L1Rank rank = 전체.get(i);
			if (rank == null) break;
			L1PcInstance pc = L1World.getInstance().getPlayer(rank.getName());
			if (pc != null && rank.getRankLevel() == 4) {
				L1ItemInstance item = ItemTable.getInstance().createItem(5558);
				if (pc.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
					if (!pc.getInventory().checkItem(5558)) 
						pc.getInventory().storeItem(item);
				}
			}

			int id = rank.getId();
			int[] oldRank = old_ranks.get(id);
			if (oldRank != null) {
				int old_total = oldRank[0];
				int old_clsses = oldRank[1];
				rank.setOldTotalRank(old_total);
				rank.setOldClassRank(old_clsses);
			} else {
				rank.setOldTotalRank(101);
				rank.setOldClassRank(101);
			}

			int exp = rank.getExp();
			int up_index = i - 1;
			int down_index = i + 1;

			if (up_index >= 0) {
				L1Rank up = 전체.get(up_index);
				int up_exp = up.getExp() - exp;
				boolean totalStepUpChance = exp * 0.05 > up_exp;
				rank.전체상승기회(totalStepUpChance);
			} else {
				rank.전체상승기회(false);
			}

			if (down_index < 전체.size()) {
				L1Rank down = 전체.get(down_index);
				int down_exp = exp - down.getExp();
				boolean totalStepDownChance = exp * 0.05 > down_exp;
				rank.전체추월위험(totalStepDownChance);
			} else {
				rank.전체추월위험(false);
			}
		}
	}

	/**
	 * 현재의 클래스 랭킹과 기존의 클래스 랭킹을 비교 분석.
	 */
	private void operateClassRank() {
		for (int i = 0; i < 8; i++) {
			List<L1Rank> temp_ranks = getMapByClass(i);
			for (int k = 0; k < temp_ranks.size(); k++) {
				L1Rank rank = temp_ranks.get(k);
				int exp = rank.getExp();
				int up_index = k - 1;
				int down_index = k + 1;

				if (up_index >= 0) {
					L1Rank up = temp_ranks.get(up_index);
					int up_exp = up.getExp() - exp;
					boolean totalStepUpChance = exp * 0.05 > up_exp;
					rank.클래스상승기회(totalStepUpChance);
				} else {
					rank.클래스상승기회(false);
				}

				if (down_index < temp_ranks.size()) {
					L1Rank down = temp_ranks.get(down_index);
					int down_exp = exp - down.getExp();
					boolean totalStepDownChance = exp * 0.05 > down_exp;
					rank.클래스추월위험(totalStepDownChance);
				} else {
					rank.클래스추월위험(false);
				}
			}
		}
	}

	/** 
	 * 데이터베이스에 새롭게 기록.
	 */
	private void writeDatabase() { 
		clearDB();
		final String ment = "INSERT INTO character_rank SET char_id=?,account_name=?,name=?,type=?,exp=?,total_rank=?,class_rank=?,old_total_rank=?,old_class_rank=?,total_step_up=?,total_step_down=?,class_step_up=?,class_step_down=?";
		try (Connection con = L1DatabaseFactory.getInstance().getConnection()) {
			for (int a = 0; a < 전체.size(); a++) {
				L1Rank rank = 전체.get(a);
				if (rank == null) break;
				int i = 0;
				try (PreparedStatement pstm = con.prepareStatement(ment)) {
					pstm.setInt(++i, rank.getId());
					pstm.setString(++i, rank.getAccountName());
					pstm.setString(++i, rank.getName());
					pstm.setInt(++i, rank.getType());
					pstm.setInt(++i, rank.getExp());
					pstm.setInt(++i, rank.getTotalRank());
					pstm.setInt(++i, rank.getClassRank());
					pstm.setInt(++i, rank.getOldTotalRank());
					pstm.setInt(++i, rank.getOldClassRank());
					pstm.setBoolean(++i, rank.전체상승기회());
					pstm.setBoolean(++i, rank.전체추월위험());
					pstm.setBoolean(++i, rank.클래스상승기회());
					pstm.setBoolean(++i, rank.클래스추월위험());
					pstm.execute();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 기존 랭킹정보를 데이터베이스에서 모두 삭제. 
	 */
	private void clearDB() {  
		try (Connection con = L1DatabaseFactory.getInstance().getConnection();
				PreparedStatement pstm = con.prepareStatement("TRUNCATE `character_rank`")) {
			pstm.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void sendRankStatusPacks(GameClient client, String name) {
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			conn = L1DatabaseFactory.getInstance().getConnection();
			pstm = conn.prepareStatement("SELECT * FROM character_rank WHERE name=?");
			pstm.setString(1, name);
			rs = pstm.executeQuery();
			if (rs.next()) {
				int rank = rs.getInt("total_rank");
				if (rank <= 100) {
					L1Rank r = 전체.get(rank - 1);
					int exp = 0;
					Connection conn2 = null;
					PreparedStatement pstm2 = null;
					ResultSet rs2 = null;
					try {
						conn2 = L1DatabaseFactory.getInstance().getConnection();
						pstm2 = conn2.prepareStatement("SELECT * FROM characters WHERE char_name=?");
						pstm2.setString(1, r.getName());
						rs2 = pstm2.executeQuery();
						if (rs2.next()) {
							exp = rs2.getInt("Exp");
						} else return;
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						SQLUtil.close(rs2);
						SQLUtil.close(pstm2);
						SQLUtil.close(conn2);
					}
					client.sendPacket(new S_Ranking2(r, exp));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(conn);
		}
	}

	public L1Rank getRankByName(String name) {
		for (L1Rank rank : 전체) {
			if (rank.getName().equals(name) && rank.getRankLevel() > 0) return rank; 
		}
		return null;
	}

	public LinkedList<L1Rank> getMapByClass(int classType) {
		switch (classType) {
		case 0: return 군주;
		case 1: return 기사;
		case 2: return 요정;
		case 3: return 법사;
		case 4: return 다엘;
		case 5: return 용기;
		case 6: return 환술;
		case 7: return 전사;
		default : return 전체;
		}
	}

	private void allClear() {
		전체.clear();
		군주.clear();
		기사.clear();
		요정.clear();
		법사.clear();
		다엘.clear();
		용기.clear();
		환술.clear();
		전사.clear();
	}

	public long getLastUpdateTime() { return lastUpdateTime; }

	private static final RankTable instance = new RankTable();
	public static RankTable getInstance() { return instance; }
}