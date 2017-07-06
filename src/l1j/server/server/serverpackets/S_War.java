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

import l1j.server.server.Opcodes;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket

public class S_War extends ServerBasePacket {

	private static final String S_WAR = "[S] S_War";
	private byte[] _byte = null;

	public S_War(int type, String clan_name1, String clan_name2) {
		buildPacket(type, clan_name1, clan_name2);
	}

	private void buildPacket(int type, String clan_name1, String clan_name2) {
		// 1 : _혈맹이_혈맹에 선전포고했습니다.
		// 2 : _혈맹이_혈맹에 항복했습니다.
		// 3 : _혈맹과_혈맹과의 전쟁이 종결했습니다.
		// 4 : _혈맹이_혈맹과의 전쟁으로 승리했습니다.
		// 6 : _혈맹과_혈맹이 동맹을 맺었습니다.
		// 7 : _혈맹과_혈맹과의 동맹 관계가 해제되었습니다.
		// 8 : 당신의 혈맹이 현재_혈맹과 교전중입니다.

		writeC(Opcodes.S_WAR);
		writeC(type);
		writeS(clan_name1);
		writeS(clan_name2);
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
		return S_WAR;
	}
}
