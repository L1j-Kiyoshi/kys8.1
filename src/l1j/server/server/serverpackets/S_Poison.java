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

public class S_Poison extends ServerBasePacket {

	/**
	 * 캐릭터의 외관을 독상태에 변경할 때에 송신하는 패킷을 구축한다
	 * 
	 * @param objId
	 *            외관을 바꾸는 캐릭터의 ID
	 * @param type
	 *            외관의 타입 0 = 통상색, 1 = 녹색, 2 = 회색
	 */
	public S_Poison(int objId, int type) {
		writeC(Opcodes.S_POISON);
		writeD(objId);

		if (type == 0) { // 통상
			writeC(0);
			writeC(0);
		} else if (type == 1) { // 녹색
			writeC(1);
			writeC(0);
		} else if (type == 2) { // 회색
			writeC(0);
			writeC(1);
		} else {
			throw new IllegalArgumentException("부정한 인수입니다. type = " + type);
		}
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}
	@Override
	public String getType() {
		return S_POISON;
	}

	private static final String S_POISON = "[S] S_Poison";
}
