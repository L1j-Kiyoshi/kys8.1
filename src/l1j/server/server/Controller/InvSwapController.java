package l1j.server.server.Controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_InventorySwap;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.utils.SQLUtil;

public final class InvSwapController {
	private static InvSwapController _instance;
	private static Map<Integer, Map<Integer, List<Integer>>> list;
	private static Map<Integer, Integer> code_list; // 현재 설정된 셋트위치.

	public static InvSwapController getInstance() {
		if (_instance == null) {
			_instance = new InvSwapController();
		}
		return _instance;
	}

	private InvSwapController() {
		list = new HashMap<Integer, Map<Integer, List<Integer>>>();
		code_list = new HashMap<Integer, Integer>();
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			st = con.prepareStatement("SELECT * FROM characters_inventory_set");
			rs = st.executeQuery();
			while (rs.next()) {
				int key = rs.getInt("objectId");
				//
				Map<Integer, List<Integer>> db = list.get(key);
				if (db == null) {
					db = new HashMap<Integer, List<Integer>>();
					db.put(0, new ArrayList<Integer>());
					db.put(1, new ArrayList<Integer>());
					list.put(key, db);
				}
				//
				String set1 = rs.getString("set1");
				String set2 = rs.getString("set2");
				if (set1 != null && set1.length() > 0) {
					List<Integer> dbs = db.get(0);
					for (String value : set1.split(","))
						dbs.add(Integer.valueOf(value));
				}
				if (set2 != null && set2.length() > 0) {
					List<Integer> dbs = db.get(1);
					for (String value : set2.split(","))
						dbs.add(Integer.valueOf(value));
				}
				//
				code_list.put(key, rs.getInt("setCode"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SQLUtil.close(st);
			SQLUtil.close(rs);
			SQLUtil.close(con);
		}
	}

	public void initDB() {
		Connection con = null;
		PreparedStatement st = null;
		synchronized (list) {
			for (int key : list.keySet()) {
				try {
					//
					con = L1DatabaseFactory.getInstance().getConnection();
					st = con.prepareStatement("DELETE FROM characters_inventory_set WHERE objectId=?");
					st.setInt(1, key);
					st.executeUpdate();
					st.close();
					//
					Map<Integer, List<Integer>> db = list.get(key);
					StringBuffer set1 = new StringBuffer();
					StringBuffer set2 = new StringBuffer();
					for (int value : db.get(0))
						set1.append(value).append(",");
					for (int value : db.get(1))
						set2.append(value).append(",");
					//
					int code = code_list.get(key);
					//
					st = con.prepareStatement("INSERT INTO characters_inventory_set SET objectId=?, setCode=?, set1=?, set2=?");
					st.setInt(1, key);
					st.setInt(2, code);
					st.setString(3, set1.toString());
					st.setString(4, set2.toString());
					st.executeUpdate();
				} catch (Exception e) {
					System.out.println("SWAP ERROR :"+e);
				} finally {
					SQLUtil.close(st);
					SQLUtil.close(con);
				}
			}
		}
	}

	public void toWorldJoin(L1PcInstance pc) {
		Map<Integer, List<Integer>> set = null;
		synchronized (list) {
			set = list.get(pc.getId());
		}
		// System.out.println(set);
		if (set == null) {
			set = new HashMap<Integer, List<Integer>>();
			set.put(0, new ArrayList<Integer>());
			set.put(1, new ArrayList<Integer>());
			synchronized (list) {
				list.put(pc.getId(), set);
			}
			synchronized (code_list) {
				code_list.put(pc.getId(), 0);
			}
		}
		pc.sendPackets(new S_InventorySwap(code_list.get(pc.getId()), set));
	}

	public void toChangeSet(L1PcInstance pc, int code) {
		if (code < 0 && code > 1)
			return;
		synchronized (code_list) {
			code_list.put(pc.getId(), code);
		}
		
		Map<Integer, List<Integer>> set = null;
		synchronized (list) {
			set = list.get(pc.getId());
		}
		if (set == null)
			return;
		//
		List<Integer> set_list = set.get(code);
		for (L1ItemInstance item : pc.getInventory().getItems()) {
			if (set_list.contains(item.getId()))
				continue;
			if (item.getItem().getType2() != 1&& item.getItem().getType2() != 2)
				continue;

			pc.getInventory().setEquipped(item, false, false, false, false);
		}
		for (int value : set_list) {
			L1ItemInstance item = pc.getInventory().findItemObjId(value);
			if (item == null)
				continue;
			if (item.getItem().getType2() != 1&& item.getItem().getType2() != 2)
				continue;
			if (item.isEquipped())
				continue;

			pc.getInventory().setEquipped(item, true, false, false, false);
		}
		//
		pc.sendPackets(new S_OwnCharStatus(pc));
		pc.sendPackets(new S_SPMR(pc));
		pc.sendPackets(new S_InventorySwap(code));
	}

	/**
	 * 저장 클릭시 호출됨.
	 * 
	 * @param pc
	 * @param code
	 */
	public void toSaveSet(L1PcInstance pc, int code) {
		//
		if (code < 0 && code > 1)
			return;
		//
		Map<Integer, List<Integer>> set = null;
		synchronized (list) {
			set = list.get(pc.getId());
		}
		if (set == null)
			return;
		//
		synchronized (set) {
			List<Integer> db = set.get(code);
			db.clear();
			for (L1ItemInstance item : pc.getInventory().getItems()) {
				if (item == null)
					continue;
				if (item.getItem().getType2() != 1
						&& item.getItem().getType2() != 2)
					continue;
				if (!item.isEquipped())
					continue;

				db.add(item.getId());
			}
			if (db.size() > 21) {
				System.out.println("착용갯수 토탈 오버 " + pc.getName());
			}
		}
	}
}
