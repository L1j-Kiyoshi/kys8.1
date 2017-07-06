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
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;

public class NoShopAndWare {

    private static Logger _log = Logger.getLogger(NoShopAndWare.class.getName());

    private static NoShopAndWare _instance;

	private static ArrayList<Integer> _idlist = new ArrayList<Integer>();

    public static NoShopAndWare getInstance()
    {
        if (_instance == null) {
            _instance = new NoShopAndWare();
        }
        return _instance;
    }

    private NoShopAndWare()
    {
        _idlist = allIdList();
    }

	private ArrayList<Integer> allIdList()
	{
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		
		ArrayList<Integer> idlist = new ArrayList<Integer>();
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("select * from NoShopAndWare");
			rs = pstm.executeQuery();
			while (rs.next()) {
				idlist.add(rs.getInt("item_id"));
			}

		} catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }

		return idlist;
	}
	
	public void storeId(int itemid)
	{
		int index = _idlist.indexOf(itemid);
		if (index != -1)
			return;

        Connection con = null;
        PreparedStatement pstm = null;

        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("INSERT INTO NoShopAndWare SET item_id=?");
            pstm.setInt(1, itemid);
            pstm.execute();
			_idlist.add(itemid);
        } catch (Exception e) {
            NpcTable._log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
	}

	public void deleteId(int itemid)
	{
		Connection con = null;
		PreparedStatement pstm = null;
		int index = _idlist.indexOf(itemid);
		if (index == -1)
			return;
	
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("DELETE FROM NoShopAndWare WHERE item_id=?");
			pstm.setInt(1, itemid);
			pstm.execute();
			_idlist.remove(index);
		} catch (Exception e) {
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public void reload() {
		_idlist.clear();
		_idlist = allIdList();
	}

	public ArrayList<Integer> getIdList()
	{
		return _idlist;
	}

	public boolean isNoShopAndWare(int itemId)
	{
		for (int id : _idlist) {
			if (itemId == id) {
				return true;
			}
		}
		return false;
	}
}
