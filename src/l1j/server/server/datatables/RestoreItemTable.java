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
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.model.Instance.L1RestoreItemInstance;
import l1j.server.server.utils.SQLUtil;

// Referenced classes of package l1j.server.server:
// IdFactory

public class RestoreItemTable {

	private static Logger _log = Logger.getLogger(RestoreItemTable.class.getName());

	public HashMap<Integer,L1RestoreItemInstance> restoreItemList = new HashMap<Integer,L1RestoreItemInstance>();
	public ArrayList<Integer> objlist = new ArrayList<Integer>();
	private static RestoreItemTable _instance;

	public static RestoreItemTable getInstance() {
		if (_instance == null) {
			_instance = new RestoreItemTable();
		}
		return _instance;
	}
	
/*		public void AddRestoreItem(int objid,L1RestoreItemInstance item ){
			if(restoreItemList.containsKey(objid)){
				restoreItemList.replace(objid, item);
			}else{
				restoreItemList.put(objid, item);
				objlist.add(objid);
			}
		} */
		
	
	
	public void RemoveRestoreItem(int objid){
		restoreItemList.remove(objid);
		objlist.remove((Object)objid);
	}
	
	public void LoadRestoreItemTable(){
		
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
	
			try {
				con = L1DatabaseFactory.getInstance().getConnection();
				pstm = con
						.prepareStatement("SELECT * FROM character_restoreItem");
				rs = pstm.executeQuery();
				L1RestoreItemInstance item = null;
				while (rs.next()) {
					item = new L1RestoreItemInstance(rs.getInt(2),rs.getInt(3),rs.getInt(4),rs.getInt(5));
					restoreItemList.put(rs.getInt(1), item);
				}
			} catch (SQLException e) {
				_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			} finally {
				SQLUtil.close(rs);
				SQLUtil.close(pstm);
				SQLUtil.close(con);
			}
	}
	public L1RestoreItemInstance getRestoreItemInstance(int objid){
		return restoreItemList.get(objid);
	}
	public void DeleteReStoreItem(int id){ //スクロールを使えば作成の
		Connection con = null;
		PreparedStatement pstm = null;
		try{
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("DELETE FROM character_restoreItem where objid=?");
			pstm.setInt(1, id);
			pstm.execute();
			
			RemoveRestoreItem(id);
		} catch(SQLException e){
			
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
}

	public void SaveReStoreItem(){
		Connection con = null;
		PreparedStatement pstm = null;
		PreparedStatement pstm2 = null;
		int id=0;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();

			pstm = con.prepareStatement("DELETE FROM character_restoreItem");
				pstm.execute();
			for(int i = 0; i <objlist.size();i++){
			id = objlist.get(i);
			pstm2 = con	.prepareStatement("INSERT INTO character_restoreItem SET objid=?, itemid=?, enchantLevel=?,attrenchantLevel=?,bless=?");
			pstm2.setInt(1, id);
			pstm2.setInt(2,restoreItemList.get(id).getItemId());
			pstm2.setInt(3,restoreItemList.get(id).getEnchantLevel());
			pstm2.setInt(4,restoreItemList.get(id).getAttrEnchantLevel());
			pstm2.setInt(5,restoreItemList.get(id).getBless());
			
			pstm2.execute();
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm2);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

}
