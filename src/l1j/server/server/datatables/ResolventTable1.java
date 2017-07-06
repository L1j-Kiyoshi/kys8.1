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

public final class ResolventTable1 {
	private static Logger _log = Logger.getLogger(ResolventTable.class
			.getName());

	private static ResolventTable1 _instance;

	private final Map<Integer, Integer> _resolvent1 = new HashMap<Integer, Integer>();

	public static ResolventTable1 getInstance() {
		if (_instance == null) {
			_instance = new ResolventTable1();
		}
		return _instance;
	}

	private ResolventTable1() {
		loadMapsFromDatabase();
	}

	public static void reload() { // Gn.67
		ResolventTable1 oldInstance = _instance;
		_instance = new ResolventTable1();
		oldInstance._resolvent1.clear();
	}

	private void loadMapsFromDatabase() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM resolvent1");

			for (rs = pstm.executeQuery(); rs.next();) {
				int itemId = rs.getInt("item_id");
				int crystalCount = rs.getInt("crystal_count");

				_resolvent1.put(new Integer(itemId), crystalCount);
			}

			_log.config("resolvent " + _resolvent1.size());
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public int getCrystalCount(int itemId) {
		int crystalCount = 0;
		if (_resolvent1.containsKey(itemId)) {
			crystalCount = _resolvent1.get(itemId);
		}
		return crystalCount;
	}

}
