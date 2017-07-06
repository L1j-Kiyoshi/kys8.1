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

public class CharacterReduc {
	public class Reduc {
		int reduc = 0;
	}

	private static Logger _log = Logger.getLogger(CharacterReduc.class.getName());

	private static CharacterReduc _instance;

	private final Map<Integer, Reduc> _idlist = new HashMap<Integer, Reduc>();

	public static CharacterReduc getInstance() {
		if (_instance == null) {
			_instance = new CharacterReduc();
		}
		return _instance;
	}

/*	private CharacterReduc() {
		System.out.print("■ 클래스리덕 데이터 .......................... ");		
		characterReduc();
		System.out.println("■ 로딩 정상 완료");				
	}*/

	public void characterReduc() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;

		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("select id, addReduction from character_balance");
			rs = pstm.executeQuery();

			Reduc characterreduc = null;
			while (rs.next()) {
				characterreduc = new Reduc();

				characterreduc.reduc = rs.getInt("addReduction");

				_idlist.put(rs.getInt("id"), characterreduc);
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
		CharacterReduc oldInstance = _instance;
		_instance = new CharacterReduc();
		if (oldInstance != null)
			oldInstance._idlist.clear();
	}

	public double getCharacterReduc(int Id) {
		Reduc characterreduc = _idlist.get(Id);

		if (characterreduc == null) {
			return 0;
		}

		return characterreduc.reduc;
	}
}