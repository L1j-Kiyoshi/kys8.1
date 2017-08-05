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

public class ArmorEnchantList {
    public class ArmorEnchant {
        int Chance = 0;
    }

    private static Logger _log = Logger.getLogger(ArmorEnchantList.class.getName());

    private static ArmorEnchantList _instance;

    private final Map<Integer, ArmorEnchant> _idlist = new HashMap<Integer, ArmorEnchant>();

    public static ArmorEnchantList getInstance() {
        if (_instance == null) {
            _instance = new ArmorEnchantList();
        }
        return _instance;
    }

/*	private ArmorEnchantList() {
        PerformanceTimer timer = new PerformanceTimer();
		System.out.print("■防御エンチャントデータ.......................... "）;		
		armorEnchantList();
		System.out.println("■ロード正常終了」+ timer.get（）+ "ms"）;			
	}*/

    public void armorEnchantList() {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;

        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("select item_id, chance from armor_enchant_list");
            rs = pstm.executeQuery();

            ArmorEnchant armorenchant = null;
            while (rs.next()) {
                armorenchant = new ArmorEnchant();

                armorenchant.Chance = rs.getInt("chance");

                _idlist.put(rs.getInt("item_id"), armorenchant);
            }

        } catch (SQLException e) {
            _log.log(Level.SEVERE, "ArmorEnchantList[]Error", e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public static void reload() {
        ArmorEnchantList oldInstance = _instance;
        _instance = new ArmorEnchantList();
        if (oldInstance != null)
            oldInstance._idlist.clear();
    }

    public int getArmorEnchant(int itemId) {
        ArmorEnchant armorenchant = _idlist.get(itemId);

        if (armorenchant == null) {
            return 0;
        }

        return armorenchant.Chance;
    }
}
