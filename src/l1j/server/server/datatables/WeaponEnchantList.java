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

public class WeaponEnchantList {
    public class WeaponEnchant {
        int Chance = 0;
    }

    private static Logger _log = Logger.getLogger(WeaponEnchantList.class.getName());

    private static WeaponEnchantList _instance;

    private final Map<Integer, WeaponEnchant> _idlist = new HashMap<Integer, WeaponEnchant>();

    public static WeaponEnchantList getInstance() {
        if (_instance == null) {
            _instance = new WeaponEnchantList();
        }
        return _instance;
    }

	/*private WeaponEnchantList() {
        PerformanceTimer timer = new PerformanceTimer();
		System.out.print("■ 武器エンチャントデータ.......................... "）;
		weaponEnchantList();
		System.out.println("■ ロード正常終了」+ timer.get（）+ "ms"）;
	}*/

    public void weaponEnchantList() {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;

        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("select item_id, chance from weapon_enchant_list");
            rs = pstm.executeQuery();

            WeaponEnchant weaponenchant = null;
            while (rs.next()) {
                weaponenchant = new WeaponEnchant();

                weaponenchant.Chance = rs.getInt("chance");

                _idlist.put(rs.getInt("item_id"), weaponenchant);
            }

        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public static void reload() {
        WeaponEnchantList oldInstance = _instance;
        _instance = new WeaponEnchantList();
        if (oldInstance != null)
            oldInstance._idlist.clear();
    }

    public int getWeaponEnchant(int itemId) {
        WeaponEnchant weaponenchant = _idlist.get(itemId);

        if (weaponenchant == null) {
            return 0;
        }

        return weaponenchant.Chance;
    }
}
