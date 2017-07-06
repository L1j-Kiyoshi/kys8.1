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

public class CharacterBalance {
	public class Balance {
		int Damage = 0;
	}

	private static Logger _log = Logger.getLogger(CharacterBalance.class.getName());

	private static CharacterBalance _instance;

	private final Map<Integer, Balance> _idlist = new HashMap<Integer, Balance>();

	public static CharacterBalance getInstance() {
		if (_instance == null) {
			_instance = new CharacterBalance();
		}
		return _instance;
	}

	private CharacterBalance() {
//		System.out.print("■ 클래스추타 데이터 .......................... ");		
		characterBalance();
//		System.out.println("■ 로딩 정상 완료");				
	}

	public void characterBalance() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;

		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("select id, addDmg from character_balance");
			rs = pstm.executeQuery();

			Balance characterdamage = null;
			while (rs.next()) {
				characterdamage = new Balance();

				characterdamage.Damage = rs.getInt("addDmg");

				_idlist.put(rs.getInt("id"), characterdamage);
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
		CharacterBalance oldInstance = _instance;
		_instance = new CharacterBalance();
		if (oldInstance != null)
			oldInstance._idlist.clear();
	}

	public double getCharacterBalance(int Id) {
		Balance characterdamage = _idlist.get(Id);

		if (characterdamage == null) {
			return 0;
		}

		return characterdamage.Damage;
	}
}