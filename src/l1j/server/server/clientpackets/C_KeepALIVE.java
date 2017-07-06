/* This program is free software; you can redistribute it and/or modify
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
package l1j.server.server.clientpackets;

import l1j.server.server.GameClient;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_KeepALIVE extends ClientBasePacket {

	private static final String C_KEEP_ALIVE = "[C] C_KeepALIVE";

	public C_KeepALIVE(byte decrypt[], GameClient client) {
		super(decrypt);
		// XXX:GameTime를 송신(3바이트의 데이터를 보내 와 있으므로 그것을 무언가에 이용하지 않으면 안 될지도 모른다)
		// L1PcInstance pc = client.getActiveChar();
		// pc.sendPackets(new S_GameTime());
	}

	@Override
	public String getType() {
		return C_KEEP_ALIVE;
	}
}