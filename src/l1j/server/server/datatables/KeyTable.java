/**
 *                            License
 * THE WORK (AS DEFINED BELOW) IS PROVIDED UNDER THE TERMS OF THIS  
 * CREATIVE COMMONS PUBLIC LICENSE ("CCPL" OR "LICENSE"). 
 * THE WORK IS PROTECTED BY COPYRIGHT AND/OR OTHER APPLICABLE LAW.  
 * ANY USE OF THE WORK OTHER THAN AS AUTHORIZED UNDER THIS LICENSE OR  
 * COPYRIGHT LAW IS PROHIBITED.
 * 
 * BY EXERCISING ANY RIGHTS TO THE WORK PROVIDED HERE, YOU ACCEPT AND  
 * AGREE TO BE BOUND BY THE TERMS OF THIS LICENSE. TO THE EXTENT THIS LICENSE  
 * MAY BE CONSIDERED TO BE A CONTRACT, THE LICENSOR GRANTS YOU THE RIGHTS CONTAINED 
 * HERE IN CONSIDERATION OF YOUR ACCEPTANCE OF SUCH TERMS AND CONDITIONS.
 * 
 */
package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.utils.SQLUtil;

public class KeyTable {

	private static Logger _log = Logger.getLogger(KeyTable.class.getName());

	private KeyTable() {
	}

	public static void StoreKey(L1ItemInstance item) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("INSERT INTO item_key_boss SET item_obj_id=?, key_id=?");

			pstm.setInt(1, item.getId());
			pstm.setInt(2, item.getKeyId());
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public static void DeleteKey(L1ItemInstance item) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("DELETE FROM item_key_boss WHERE item_obj_id=?");
			pstm.setInt(1, item.getId());
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);

		}
	}
	
	public static void DeleteKeyId(int keyId) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("DELETE FROM item_key_boss WHERE key_id=?");
			pstm.setInt(1, keyId);
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);

		}
	}
	
	public static void initBossKey() {
		Connection con = null;
		PreparedStatement pstm = null;
		PreparedStatement pstm1 = null;
		PreparedStatement pstm2= null;
		PreparedStatement pstm3 = null;
		PreparedStatement pstm4 = null;
		PreparedStatement pstm5 = null;
		
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("truncate `item_key_boss`;");
			pstm1 = con.prepareStatement("DELETE FROM character_items WHERE item_id = 80500");
			pstm2 = con.prepareStatement("DELETE FROM character_elf_warehouse WHERE item_id = 80500");
			pstm3 = con.prepareStatement("DELETE FROM clan_warehouse WHERE item_id = 80500");
			pstm4 = con.prepareStatement("DELETE FROM character_warehouse WHERE item_id = 80500");
			pstm.execute();
			pstm1.execute();
			pstm2.execute();
			pstm3.execute();
			pstm4.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(pstm1);
			SQLUtil.close(pstm2);
			SQLUtil.close(pstm3);
			SQLUtil.close(pstm4);
			SQLUtil.close(con);

		}
	}

	public static boolean checkey(L1ItemInstance item) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM item_key_boss WHERE item_obj_id=?");

			pstm.setInt(1, item.getId());
			rs = pstm.executeQuery();
			while (rs.next()) {
				int itemObj = rs.getInt("item_obj_id");
				if (item.getId() == itemObj) {
					item.setKeyId(rs.getInt("key_id"));
					return true;
				}
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return false;
	}

}
