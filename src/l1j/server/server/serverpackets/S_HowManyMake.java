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

import java.io.IOException;

import l1j.server.server.Opcodes;

public class S_HowManyMake extends ServerBasePacket {
	public S_HowManyMake(int objId, int max, String htmlId) {
		writeC(Opcodes.S_HYPERTEXT_INPUT);
		writeD(objId);
		writeD(0); // ?
		writeD(0); // 스핀 컨트롤의 초기 가격
		writeD(0); // 가격의 하한
		writeD(max); // 가격의 상한
		writeH(0); // ?
		writeS("request");
		writeS(htmlId);
	}

	@Override
	public byte[] getContent() throws IOException {
		return getBytes();
	}
}
