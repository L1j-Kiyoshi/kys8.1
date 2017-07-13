/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.model.shop.L1Shop;
import l1j.server.server.templates.L1ShopItem;
import l1j.server.server.utils.SQLUtil;

public class ShopTable {

	private static final long serialVersionUID = 1L;

	private static Logger _log = Logger.getLogger(ShopTable.class.getName());

	private static ShopTable _instance;

	private final Map<Integer, L1Shop> _allShops = new HashMap<Integer, L1Shop>();

	public static ShopTable getInstance() {
		if (_instance == null) {
			_instance = new ShopTable();
		}
		return _instance;
	}

	private ShopTable() {
		loadShops();
	}
	
	public static void reload(){
		ShopTable oldInstance = _instance;
		_instance = new ShopTable();
		oldInstance._allShops.clear();
	}

	private ArrayList<Integer> enumNpcIds() {
		ArrayList<Integer> ids = new ArrayList<Integer>();

		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT DISTINCT npc_id FROM shop");
			rs = pstm.executeQuery();
			while (rs.next()) {
				ids.add(rs.getInt("npc_id"));
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs, pstm, con);
		}
		return ids;
	}

	private L1Shop loadShop(int npcId, ResultSet rs) throws SQLException {
		List<L1ShopItem> sellingList = new ArrayList<L1ShopItem>();
		List<L1ShopItem> purchasingList = new ArrayList<L1ShopItem>();
		L1ShopItem item = null;
		while (rs.next()) {
			int itemId = rs.getInt("item_id");
			int sellingPrice = rs.getInt("selling_price");
			int purchasingPrice = rs.getInt("purchasing_price");
			int packCount = rs.getInt("pack_count");
			int enchant = rs.getInt("enchant");
			boolean timeLimit = Boolean.valueOf(rs.getString("time_limit"));
			packCount = packCount == 0 ? 1 : packCount;
			if (0 <= sellingPrice) {
				item = new L1ShopItem(itemId, sellingPrice, packCount, enchant, timeLimit);
				sellingList.add(item);
			}
			if (0 <= purchasingPrice) {
				item = new L1ShopItem(itemId, purchasingPrice, packCount, enchant, timeLimit);
				purchasingList.add(item);
			}
		}
		return new L1Shop(npcId, sellingList, purchasingList);
	}
	public void Reload(int npcid) { //特定NPCリロード
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM shop WHERE npc_id=? ORDER BY order_id");
			L1Shop shop = null;
			for (int npcId : enumNpcIds()) {
				if (npcId != npcid)
					continue;
				pstm.setInt(1, npcId);
				rs = pstm.executeQuery();
				shop = loadShop(npcId, rs);
				_allShops.put(npcId, shop);
				rs.close();
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs, pstm, con);
		}
	}
	private void loadShops() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM shop WHERE npc_id=? ORDER BY order_id");
			L1Shop shop = null;
			// 70035 70041 70042 
			for (int npcId : enumNpcIds()) {
				if (npcId == 70035 || npcId == 70041 || npcId == 70042){
					continue;
				}
				pstm.setInt(1, npcId);
				rs = pstm.executeQuery();
				shop = loadShop(npcId, rs);
				_allShops.put(npcId, shop);
				rs.close();
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs, pstm, con);
		}
	}

	public L1Shop get(int npcId) {
		return _allShops.get(npcId);
	}
	/*ボギョン関連*/
	public void addShop(int npcId, L1Shop shop){		
		_allShops.put(npcId, shop);
	}

	/*ボギョン関連*/
	public void delShop(int npcId) {
	_allShops.remove(npcId);	
	}
}
