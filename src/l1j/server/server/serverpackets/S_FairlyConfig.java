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

package l1j.server.server.serverpackets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.utils.SQLUtil;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket

public class S_FairlyConfig extends ServerBasePacket {

	private static Logger _log = Logger.getLogger(S_FairlyConfig.class
			.getName());
	private static final String S_CHARACTER_CONFIG = "[S] S_CharacterConfig";
	private byte[] _byte = null;

	public S_FairlyConfig(L1PcInstance pc) {
		buildPacket(pc);
	}

	private void buildPacket(L1PcInstance pc) {
		byte data[] = null;
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		int ok = 0;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("SELECT * FROM character_Fairly_Config WHERE object_id=?");
			pstm.setInt(1, pc.getId());
			rs = pstm.executeQuery();
			while (rs.next()) {
				data = rs.getBytes(2);
				ok = 1;
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		writeC(Opcodes.S_EVENT);
		writeC(0xBC);
		if (ok != 0) {
			writeByte(data);
			// System.out.println("페어리 정보 있어요.");
			pc.페어리정보 = data;
		} else {
			for (int i = 0; i < 512; i++) {
				writeC(0);
			}
		}
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return S_CHARACTER_CONFIG;
	}
}
