package l1j.server.GameSystem.ClanMatching;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.utils.SQLUtil;

public class Sabu_CMBox {
	private static Sabu_CMBox _instance;
	private static ArrayList<Sabu_cm> cmlist;// mapに実装するのが正しいかもしれない。
	private static ArrayList<Sabu_um> umlist;// ユーザが申請リストを閲覧した時にすべての登録血盟を検索してい
												//ないために、追加

	public static Sabu_CMBox getInstance() {
		if (_instance == null) {
			_instance = new Sabu_CMBox();
		}
		return _instance;
	}

	private Sabu_CMBox() {
		cmlist = new ArrayList<Sabu_cm>();
		umlist = new ArrayList<Sabu_um>();
	}

	public void Load() {
		Init();
	}

	public int getsize() {
		return cmlist.size();
	}

	public ArrayList<Sabu_um> getjoinlist() {
		ArrayList<Sabu_um> l = new ArrayList<Sabu_um>();
		synchronized (umlist) {
			l.addAll(umlist);
		}
		return l;
	}

	public void remove(Sabu_cm cm) {
		try {
			synchronized (cmlist) {
				if (cmlist.contains(cm)) {
					cmlist.remove(cm);
					unregisterClan(cm.getClanId());
					cm = null;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void create(int clanid, String info, int num, int type) {
		try {
			Sabu_cm cm = new Sabu_cm();
			cm.setClanId(clanid);
			cm.setInfo(info);
			cm.setnum(num);
			cm.settype(type);
			add(cm);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void update(int clanid, String info, int type) {
		try {
			synchronized (cmlist) {
				ArrayList<Sabu_cm> l = new ArrayList<Sabu_cm>();
				l.addAll(cmlist);
				for (Sabu_cm cm : l) {
					if (cm.getClanId() == clanid) {
						cm.setInfo(info);
						cm.settype(type);
						editRegist(clanid, type, info);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void add(Sabu_cm cm) {
		try {
			synchronized (cmlist) {
				if (!cmlist.contains(cm)) {
					registClan(cm.getClanId(), cm.gettype(), cm.getInfo());
					cmlist.add(cm);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<Sabu_cm> getList() {
		ArrayList<Sabu_cm> l = new ArrayList<Sabu_cm>();
		synchronized (cmlist) {
			l.addAll(cmlist);
		}
		return l;
	}

	public Sabu_cm check(int clanid) {
		try {
			synchronized (cmlist) {
				for (Sabu_cm cm : cmlist) {
					if (cm == null)
						continue;
					if (cm.getClanId() == clanid)
						return cm;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void Init() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("SELECT * FROM clanmatching_clan order by num desc");
			rs = pstm.executeQuery();
			while (rs.next()) {
				Sabu_cm cm = new Sabu_cm();
				cm.setClanId(rs.getInt("clan_id"));
				cm.setInfo(rs.getString("info"));
				cm.setnum(rs.getInt("num"));
				cm.settype(rs.getInt("type"));
				cmlist.add(cm);
			}
			Sabu_cm[] scm = (Sabu_cm[]) cmlist.toArray(new Sabu_cm[cmlist
					.size()]);
			int clanid = 0;
			String charname = null;
			pstm = con.prepareStatement("SELECT * FROM clanmatching_user");
			rs = pstm.executeQuery();
			while (rs.next()) {
				clanid = rs.getInt("clan_id");
				charname = rs.getString("char_name");
				for (Sabu_cm cm : scm) {
					if (clanid == cm.getClanId()) {
						Sabu_um um = new Sabu_um();
						um.setClanId(clanid);
						um.setcharname(charname);
						um.setnum(cm.getnum());
						um.settype(cm.gettype());
						um.setInfo(cm.getInfo());
						umlist.add(um);
						cm.add(charname);
					}
				}
			}
			scm = null;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private void joinApply(String name, int clanid, String info, int num, int type) {
		try {
			Sabu_um um = new Sabu_um();
			um.setcharname(name);
			um.setClanId(clanid);
			um.setInfo(info);
			um.setnum(num);
			um.settype(type);
			synchronized (umlist) {
				umlist.add(um);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void joinsend(String charname, int num) {
		try {
			synchronized (cmlist) {
				ArrayList<Sabu_cm> l = new ArrayList<Sabu_cm>();
				l.addAll(cmlist);
				for (Sabu_cm cm : l) {
					if (cm.getnum() == num) {
						cm.add(charname);
						joinApply(charname, cm.getClanId(), cm.getInfo(), num,
								cm.gettype());
						applyUserRegist(charname, cm.getClanId(), num);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void cmCharRemove(String charname, int clanid) {
		synchronized (cmlist) {
			for (Sabu_cm cm : cmlist) {
				if (clanid == cm.getClanId()) {
					cm.remove(charname);
				}
			}
		}
	}

	public void joincancle(String charname, int clanId) {
		try {
			synchronized (umlist) {
				ArrayList<Sabu_um> l = new ArrayList<Sabu_um>();
				l.addAll(umlist);
				for (Sabu_um um : l) {
					if (um.getcharname().equalsIgnoreCase(charname)) {
						if (clanId == um.getClanId()) {
							cancellUserRegist(um.getcharname(), um.getClanId());
							cmCharRemove(um.getcharname(), um.getClanId());
							umlist.remove(um);
							um = null;
						}
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<Sabu_um> joinList(String charname) {
		try {
			synchronized (umlist) {
				ArrayList<Sabu_um> list = new ArrayList<Sabu_um>();
				for (Sabu_um um : umlist) {
					if (um.getcharname().equalsIgnoreCase(charname)) {
						list.add(um);
					}

				}
				return list;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public L1PcInstance getClanCrown(String charname, int num) {
		try {
			synchronized (umlist) {
				for (Sabu_um um : umlist) {
					if (um.getcharname().equalsIgnoreCase(charname)
							&& um.getnum() == num) {
						L1Clan c = L1World.getInstance()
								.getClan(um.getClanId());
						if (c == null)
							return null;
						return L1World.getInstance().getPlayer(
								c.getLeaderName());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public int getnextnum() {
		return matchingNumber() + 1;
	}

	private synchronized static int matchingNumber() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("SELECT * FROM clanmatching_clan order by num desc limit 1");
			rs = pstm.executeQuery();
			while (rs.next()) {
				return rs.getInt("num");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return 0;
	}

	private synchronized static void applyUserRegist(String charname, int clanid, int num) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("INSERT INTO clanmatching_user SET char_name=?, clan_id=?, num=?, info=?");
			pstm.setString(1, charname);
			pstm.setInt(2, clanid);
			pstm.setInt(3, num);
			pstm.setString(4, "");
			pstm.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private synchronized static void cancellUserRegist(String charname, int clanid) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("DELETE FROM clanmatching_user WHERE clan_id=? AND char_name=?");
			pstm.setInt(1, clanid);
			pstm.setString(2, charname);
			pstm.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private synchronized static void registClan(int clanid, int type, String info) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("INSERT INTO clanmatching_clan SET clan_id=?, info=?, type=?");
			pstm.setInt(1, clanid);
			pstm.setString(2, info);
			pstm.setInt(3, type);
			pstm.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private synchronized static void editRegist(int clanid, int type, String info) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("UPDATE clanmatching_clan SET info=?, type=? WHERE clan_id=?");
			pstm.setString(1, info);
			pstm.setInt(2, type);
			pstm.setInt(3, clanid);
			pstm.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private synchronized static void unregisterClan(int clanid) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("DELETE FROM clanmatching_clan WHERE clan_id=?");
			pstm.setInt(1, clanid);
			pstm.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
}
