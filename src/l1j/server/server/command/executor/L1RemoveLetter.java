/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.command.executor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_LetterList;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.SQLUtil;

public class L1RemoveLetter implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1RemoveLetter.class.getName());
	private L1RemoveLetter() {}
	public static L1CommandExecutor getInstance() {	
		return new L1RemoveLetter();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {				
			checkLetter(pc.getName());	
			pc.sendPackets(new S_LetterList(pc, 0, 200));
			pc.sendPackets(new S_SystemMessage("편지를 삭제 하였습니다."));
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".편지삭제을 입력해주세요."));
		}
	}

	public void checkLetter(String name) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();			
			pstm = con.prepareStatement("DELETE FROM letter WHERE receiver = ?");
			pstm.setString(1, name);
			pstm.execute();	
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
}
