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
 * ランキングシステム<br>
 * ランキングの演算は、DBへのアクセスせずにMapを利用した。
 *
 * @author
 */
public class RankTable {

    private final ReentrantLock lock;
    private Map<Integer, int[]> old_ranks;
    private LinkedList<L1Rank> ll_All, ll_Pri, ll_Kni, ll_Elf, ll_Wiz, ll_DE, ll_Drk, ll_Ill, ll_War;
    private long lastUpdateTime;

    private RankTable() {
        lock = new ReentrantLock();
        old_ranks = new HashMap<Integer, int[]>();
        ll_All = new LinkedList<L1Rank>();
        ll_Pri = new LinkedList<L1Rank>();
        ll_Kni = new LinkedList<L1Rank>();
        ll_Elf = new LinkedList<L1Rank>();
        ll_Wiz = new LinkedList<L1Rank>();
        ll_DE = new LinkedList<L1Rank>();
        ll_Drk = new LinkedList<L1Rank>();
        ll_Ill = new LinkedList<L1Rank>();
        ll_War = new LinkedList<L1Rank>();
        load();
    }

    /**
     * サーバー起動時にデータのロード。
     */
    private void load() {
        // データベースのロード時に更新時間になってしまう場合に備えて、必ず同期します。
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
                    rank.setTotalUpChance(rs.getBoolean("total_step_up"));
                    rank.setTotalOvertakingRisk(rs.getBoolean("total_step_down"));
                    rank.setClassUpChance(rs.getBoolean("class_step_up"));
                    rank.setClassOverTakingRisk(rs.getBoolean("class_step_down"));
                    switch (type) {
                        case 0:
                            ll_Pri.add(rank);
                            break;
                        case 1:
                            ll_Kni.add(rank);
                            break;
                        case 2:
                            ll_Elf.add(rank);
                            break;
                        case 3:
                            ll_Wiz.add(rank);
                            break;
                        case 4:
                            ll_DE.add(rank);
                            break;
                        case 5:
                            ll_Drk.add(rank);
                            break;
                        case 6:
                            ll_Ill.add(rank);
                            break;
                        case 7:
                            ll_War.add(rank);
                            break;
                    }
                    ll_All.add(order++, rank);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * ランキングに関するすべてのデータを更新します。
     */
    public void updateRank() {//ランキング更新
//		System.out.println("CMD MSG：ランキングが更新されました。 "）;
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
     * 既存のランキングを格納します。
     */
    private void setOldRank() {
        old_ranks.clear();
        for (int i = 0; i < ll_All.size(); i++) {
            L1Rank rank = ll_All.get(i);
            if (rank == null) break;
            old_ranks.put(rank.getId(), new int[]{rank.getTotalRank(), rank.getClassRank()});
        }
    }

    /**
     * 現在のすべてのランキングを新たに読み込まれます。
     */
    private void reLoadCurrentRank() {
        allClear();
        int rank_All = 0, rank_Pri = 0, rank_Kni = 0, rank_Elf = 0, rank_Wiz = 0, rank_DE = 0, rank_Drk = 0, rank_Ill = 0, rank_War = 0;
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
                rank.setTotalRank(rank_All + 1);
                switch (rank.getType()) {
                    case 0:
                        this.ll_Pri.add(rank);
                        rank.setClassRank(++rank_Pri);
                        break;
                    case 1:
                        this.ll_Kni.add(rank);
                        rank.setClassRank(++rank_Kni);
                        break;
                    case 2:
                        this.ll_Elf.add(rank);
                        rank.setClassRank(++rank_Elf);
                        break;
                    case 3:
                        this.ll_Wiz.add(rank);
                        rank.setClassRank(++rank_Wiz);
                        break;
                    case 4:
                        this.ll_DE.add(rank);
                        rank.setClassRank(++rank_DE);
                        break;
                    case 5:
                        this.ll_Drk.add(rank);
                        rank.setClassRank(++rank_Drk);
                        break;
                    case 6:
                        this.ll_Ill.add(rank);
                        rank.setClassRank(++rank_Ill);
                        break;
                    case 7:
                        this.ll_War.add(rank);
                        rank.setClassRank(++rank_War);
                        break;
                }
                this.ll_All.add(rank_All, rank);
                rank_All++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 現在の全体ランキングと既存の全ランキングを比較分析。
     */
    private void operateTotalRank() {
        for (int i = 0; i < ll_All.size(); i++) {
            L1Rank rank = ll_All.get(i);
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
                L1Rank up = ll_All.get(up_index);
                int up_exp = up.getExp() - exp;
                boolean totalStepUpChance = exp * 0.05 > up_exp;
                rank.setTotalUpChance(totalStepUpChance);
            } else {
                rank.setTotalUpChance(false);
            }

            if (down_index < ll_All.size()) {
                L1Rank down = ll_All.get(down_index);
                int down_exp = exp - down.getExp();
                boolean totalStepDownChance = exp * 0.05 > down_exp;
                rank.setTotalOvertakingRisk(totalStepDownChance);
            } else {
                rank.setTotalOvertakingRisk(false);
            }
        }
    }

    /**
     * 現在のクラスのランキングと、既存のクラスのランキングを比較分析。
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
                    rank.setClassUpChance(totalStepUpChance);
                } else {
                    rank.setClassUpChance(false);
                }

                if (down_index < temp_ranks.size()) {
                    L1Rank down = temp_ranks.get(down_index);
                    int down_exp = exp - down.getExp();
                    boolean totalStepDownChance = exp * 0.05 > down_exp;
                    rank.setClassOverTakingRisk(totalStepDownChance);
                } else {
                    rank.setClassOverTakingRisk(false);
                }
            }
        }
    }

    /**
     * データベースに新たに記録した。
     */
    private void writeDatabase() {
        clearDB();
        final String ment = "INSERT INTO character_rank SET char_id=?,account_name=?,name=?,type=?,exp=?,total_rank=?,class_rank=?,old_total_rank=?,old_class_rank=?,total_step_up=?,total_step_down=?,class_step_up=?,class_step_down=?";
        try (Connection con = L1DatabaseFactory.getInstance().getConnection()) {
            for (int a = 0; a < ll_All.size(); a++) {
                L1Rank rank = ll_All.get(a);
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
                    pstm.setBoolean(++i, rank.getTotalUpChance());
                    pstm.setBoolean(++i, rank.getTotalOvertakingRisk());
                    pstm.setBoolean(++i, rank.getClassUpChance());
                    pstm.setBoolean(++i, rank.getClassOverTakingRisk());
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
     * 既存のランキング情報をデータベースから削除。
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
                    L1Rank r = ll_All.get(rank - 1);
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
        for (L1Rank rank : ll_All) {
            if (rank.getName().equals(name) && rank.getRankLevel() > 0) return rank;
        }
        return null;
    }

    public LinkedList<L1Rank> getMapByClass(int classType) {
        switch (classType) {
            case 0:
                return ll_Pri;
            case 1:
                return ll_Kni;
            case 2:
                return ll_Elf;
            case 3:
                return ll_Wiz;
            case 4:
                return ll_DE;
            case 5:
                return ll_Drk;
            case 6:
                return ll_Ill;
            case 7:
                return ll_War;
            default:
                return ll_All;
        }
    }

    private void allClear() {
        ll_All.clear();
        ll_Pri.clear();
        ll_Kni.clear();
        ll_Elf.clear();
        ll_Wiz.clear();
        ll_DE.clear();
        ll_Drk.clear();
        ll_Ill.clear();
        ll_War.clear();
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    private static final RankTable instance = new RankTable();

    public static RankTable getInstance() {
        return instance;
    }
}