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
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.Server;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.utils.SQLUtil;

// Referenced classes of package l1j.server.server:
// IdFactory

public class LetterSpamTable {
	private static Logger _log = Logger.getLogger(LetterSpamTable.class.getName());
	private volatile static LetterSpamTable uniqueInstance = null;
	
	public LetterSpamTable() {
	}

	public static LetterSpamTable  getInstance() {
		if(uniqueInstance == null) {
			synchronized (Server.class) {
				if(uniqueInstance == null) {
					uniqueInstance = new LetterSpamTable();
				}
			}
		}
		return uniqueInstance;
	}

	/**
	 * 편지를 보낼 수 있는지 체크한다.
	 * @param senderName
	 * @param receiverName
	 * @return
	 */
	public boolean spamLetterCheck(String senderName, String receiverName) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM letter_spam WHERE name = ? AND spamname = ?");
			pstm.setString(1, receiverName);
			pstm.setString(2, senderName);
			rs = pstm.executeQuery();
			if (rs.next()) return false;
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return true;
	}
	

	/**
	 * 편지 차단자를 저장한다.
	 * @param pcName 
	 * @param excludeName
	 */
	public void spamLetterAdd(L1PcInstance pc, String excludeName) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		int no = 0;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT Max(no)+1 as cnt FROM letter_spam ORDER BY no");
			rs = pstm.executeQuery();
			if (rs.next()) {
				no = rs.getInt("cnt");
			}
			if (no >= 50) {
				pc.sendPackets(new S_ServerMessage(472)); // 차단된 사용자가 너무 많습니다.
				return;
			}
			
			pstm = con.prepareStatement("INSERT INTO letter_spam SET no=?, name=?, spamname=?");
			pstm.setInt(1, no);
			pstm.setString(2, pc.getName());
			pstm.setString(3, excludeName);
			pstm.execute();
			//pc.sendPackets(new S_PacketBox(S_PacketBox.ADD_EXCLUDE, excludeName, 1));
			
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	
	/**
	 * 편지 차단자를 삭제한다.
	 * @param pcName
	 * @param excludeName
	 */
	public void spamLetterDel(L1PcInstance pc, String excludeName) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("DELETE FROM letter_spam WHERE name = ? AND spamname = ?");
			pstm.setString(1, pc.getName());
			pstm.setString(2, excludeName);
			pstm.execute();
			//pc.sendPackets(new S_PacketBox(S_PacketBox.REM_EXCLUDE, excludeName, 1));
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	
	/**
	 * 내 차단목록에 상대방이 있는지 체크한다.
	 * @param PcName
	 * @param spamname
	 * @return
	 */
	public boolean spamList(String PcName, String spamname) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM letter_spam WHERE name = ? AND spamname = ?");
			pstm.setString(1, PcName);
			pstm.setString(2, spamname);
			rs = pstm.executeQuery();
			if (rs.next()) return true;
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return false;
	}
	
	
	/**
	 * 월드 접속시에 불러와서 S패킷을 날려준다.
	 * @param pcName
	 * @param spamname
	 */
	public void loadSpamList(L1PcInstance pc) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String spamname = null;
		try{
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM letter_spam WHERE name = ?");
			pstm.setString(1, pc.getName());
			rs = pstm.executeQuery();
			while (rs.next()) {
				spamname = rs.getString("spamname");
				//pc.sendPackets(new S_PacketBox(S_PacketBox.ADD_EXCLUDE, spamname, 1));
			}
		}catch(SQLException e){
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}finally{
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
}
