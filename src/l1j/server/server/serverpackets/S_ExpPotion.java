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

public class S_ExpPotion extends ServerBasePacket {

	public S_ExpPotion(int time){
		writeC(Opcodes.S_EVENT);
		writeC(0x14);
		for(int i = 0; i < 45; i++) writeC(0x00);
		writeC((int)(time + 8)/16);
		for(int i = 0; i < 16; i++) writeC(0x00);
		writeC(0x14);
		writeD(0x00000000);
		writeC(0x00);
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}
}
