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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;

public class AccessoryEnchantList {
	public class AccessoryEnchant {
		int Chance = 0;
	}

	private static Logger _log = Logger.getLogger(AccessoryEnchantList.class.getName());

	private static AccessoryEnchantList _instance;

	private final Map<Integer, AccessoryEnchant> _idlist = new HashMap<Integer, AccessoryEnchant>();

	public static AccessoryEnchantList getInstance() {
		if (_instance == null) {
			_instance = new AccessoryEnchantList();
		}
		return _instance;
	}

/*	private AccessoryEnchantList() {
		PerformanceTimer timer = new PerformanceTimer();
		System.out.print("【악세인첸트】  ");		
		weaponEnchantList();
		//조우서버 CMD창 변형
		System.out.println("불러오기 성공 " + timer.get() + "ms");				
	}*/

	public void weaponEnchantList() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;

		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("select item_id, chance from accessory_enchant_list");
			rs = pstm.executeQuery();

			AccessoryEnchant accessoryenchant = null;
			while (rs.next()) {
				accessoryenchant = new AccessoryEnchant();

				accessoryenchant.Chance = rs.getInt("chance");

				_idlist.put(rs.getInt("item_id"), accessoryenchant);
			}

		} catch (SQLException e) {
			_log.log(Level.SEVERE, "AccessoryEnchantList[]Error", e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public static void reload() {
		AccessoryEnchantList oldInstance = _instance;
		_instance = new AccessoryEnchantList();
		if (oldInstance != null)
			oldInstance._idlist.clear();
	}

	public int getAccessoryEnchant(int itemId) {
		AccessoryEnchant accessoryenchant = _idlist.get(itemId);

		if (accessoryenchant == null) {
			return 0;
		}

		return accessoryenchant.Chance;
	}
}
