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
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.GameSystem.AttendanceController;
import l1j.server.server.model.L1AccountAttendance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.utils.SQLUtil;

public class AccountAttendanceTable {

	private static Logger _log = Logger
			.getLogger(AccountAttendanceTable.class.getName());

	private static AccountAttendanceTable _instance;

	public static AccountAttendanceTable getInstance() {
		if (_instance == null) {
			_instance = new AccountAttendanceTable();
		}
		return _instance;
	}

	private AccountAttendanceTable() {
		load();
	}
	
	public void load_account(L1PcInstance pc)
	{
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		L1AccountAttendance acc = new L1AccountAttendance(pc.getAccountName());
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM AttendanceAccount WHERE account_name=? ");
			pstm.setString(1, pc.getAccountName());
			rs = pstm.executeQuery();

			while (rs.next()) {
				acc.setDay(rs.getInt("day"));
				acc.setTime(rs.getInt("time"));
				StringTokenizer stt = new StringTokenizer(rs.getString("clear"), ",");
				acc.toArray().clear();
				while (stt.hasMoreTokens()) {
					String check = stt.nextToken();
					acc.toArray().add(Integer.valueOf(check));
				}

				acc.setDaypc(rs.getInt("day_pc"));
				acc.setTimepc(rs.getInt("time_pc"));
				stt = new StringTokenizer(rs.getString("clear_pc"), ",");
				acc.toArraypc().clear();
				while (stt.hasMoreTokens()) {
					String check = stt.nextToken();
					acc.toArraypc().add(Integer.valueOf(check));
				}
				
				acc.setToday(rs.getInt("laste_check_day"));
				acc.setYear(rs.getInt("laste_check_year"));
				if(acc.getDay()==42){
					if(acc.checktype()==2){
						//初期化
						acc.clearday();
						//acc.setDay(1);
					}
				}
				
				if(acc.getDaypc()==42){
					if(acc.checktypepc()==2){
						//初期化
						acc.cleardaypc();
						//acc.setDaypc(1);
					}
				}
			}
			
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} catch (Exception e) {

		} finally {
			SQLUtil.close(rs, pstm, con);
		}
		AttendanceController.addaccountlist(acc);
	}

	private void load() {
			return;
	}
	
	public void save_account(L1PcInstance pc)
	{
		Connection con = null;
		PreparedStatement st = null;
		Calendar cal = Calendar.getInstance();
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			st = con.prepareStatement("DELETE FROM AttendanceAccount WHERE account_name=?");
			st.setString(1, pc.getAccountName());
			st.executeUpdate();
			
			L1AccountAttendance acc = AttendanceController.findacc(pc.getAccountName());
			st = con.prepareStatement("INSERT INTO AttendanceAccount SET account_name=?, day=?, time=?, clear=?, day_pc=?, time_pc=?, clear_pc=?, laste_check_day=?, laste_check_year=?");
			st.setString(1, acc.getAccounts());
			st.setInt(2, acc.getDay());
			st.setInt(3, acc.getTime());
			StringBuffer result = new StringBuffer();
			for(int i :acc.toArray()){
				result.append(String.valueOf(i));
				result.append(",");
			}
			st.setString(4, result.toString());
			
			st.setInt(5, acc.getDaypc());
			st.setInt(6, acc.getTimepc());
			result = new StringBuffer();
			for(int i :acc.toArraypc()){
				result.append(String.valueOf(i));
				result.append(",");
			}
			st.setString(7, result.toString());
			
			st.setInt(8, acc.getToday());
			st.setInt(9, acc.getYear());
			st.executeUpdate();
			st.close();			

		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} catch (Exception e) {

		} finally {
			SQLUtil.close(st);
			SQLUtil.close(con);
		}
	}


}
