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

public class S_Paralysis extends ServerBasePacket {

	public S_Paralysis(int type, boolean flag) {
		writeC(Opcodes.S_PARALYSE);
		if (type == TYPE_PARALYSIS) // 체가 완전하게 마비되었습니다.
		{
			if (flag == true) {
				writeC(2);
			} else {
				writeC(3);
			}
		} else if (type == TYPE_PARALYSIS2) // 체가 완전하게 마비되었습니다.
		{
			if (flag == true) {
				writeC(4);
			} else {
				writeC(5);
			}
		} else if (type == TYPE_TELEPORT_UNLOCK){ // 텔레포트 대기 상태의 해제
			writeC(7);
		} else if (type == TYPE_SLEEP) // 강력한 수마가 덮쳐 와, 자 버렸습니다.
		{
			if (flag == true) {
				writeC(10);
			} else {
				writeC(11);
			}
		} else if (type == TYPE_FREEZE)  // 체가 얼었습니다.
		{
			if (flag == true) {
				writeC(12);
			} else {
				writeC(13);
			}
		} else if (type == TYPE_STUN) // 스탠 상태입니다.
		{
			if (flag == true) {
				writeC(22);
			} else {
				writeC(23);
			}
		} else if (type == TYPE_BIND) // 다리가 속박된 것처럼 움직일 수 없습니다.
		{
			if (flag == true) {
				writeC(24);
			} else {
				writeC(25);
			}
		} else if (type == TYPE_RIP) // 파워그립
		{
			if (flag == true) {
				writeC(26);
			} else {
				writeC(27);
			}
		} else if (type == TYPE_PERADO) // 데스페라도
		{
			if (flag == true) {
				writeC(30);
			} else {
				writeC(31);
			}
		}
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}
	@Override
	public String getType() {
		return _S__2F_PARALYSIS;
	}
	
	public static final int TYPE_PARALYSIS = 1;

	public static final int TYPE_PARALYSIS2 = 2;

	public static final int TYPE_SLEEP = 3;

	public static final int TYPE_FREEZE = 4;

	public static final int TYPE_STUN = 5;

	public static final int TYPE_BIND = 6;

	public static final int TYPE_TELEPORT_UNLOCK = 7;
	
	public static final int TYPE_RIP = 8;
	
	public static final int TYPE_PERADO = 9;

	private static final String _S__2F_PARALYSIS = "[S] S_Paralysis";
}
