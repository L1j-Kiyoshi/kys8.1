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
import l1j.server.server.model.Instance.L1PcInstance;

public class S_SystemMessage extends ServerBasePacket {
	private static final String S_SYSTEM_MESSAGE = "[S] S_SystemMessage";

	private byte[] _byte = null;

	/**
	 * 클라이언트에 데이터의 존재하지 않는 오리지날의 메세지를 표시한다.
	 * 메세지에 nameid($xxx)가 포함되어 있는 경우는 overload 된 이제(벌써) 한편을 사용한다.
	 * 
	 * @param msg - 표시하는 캐릭터 라인
	 */
	public S_SystemMessage(String msg) {
		writeC(Opcodes.S_MESSAGE);
		writeC(0x09);
		writeS(msg);
	}
	public S_SystemMessage(L1PcInstance pc, String msg) {
		writeC(Opcodes.S_SAY);
		writeC(15);
		writeD(pc.getId());
		writeS(msg);
	}
	/**
	 * 클라이언트에 데이터의 존재하지 않는 오리지날의 메세지를 표시한다.
	 * 
	 * @param msg - 표시하는 캐릭터 라인
	 * @param nameid - 캐릭터 라인에 nameid($xxx)가 포함되어 있는 경우 true로 한다.
	 */
	public S_SystemMessage(String msg, boolean nameid) {
		writeC(Opcodes.S_SAY_CODE);
		writeC(2);
		writeD(0);
		writeS(msg);
		// NPC 채팅 패킷이면 nameid가 해석되기 (위해)때문에 이것을 이용한다
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
		return S_SYSTEM_MESSAGE;
	}
}
