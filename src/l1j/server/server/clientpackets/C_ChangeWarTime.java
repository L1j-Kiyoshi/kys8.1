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

package l1j.server.server.clientpackets;

import l1j.server.server.GameClient;
import l1j.server.server.datatables.CastleTable;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.templates.L1Castle;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_ChangeWarTime extends ClientBasePacket {

    private static final String C_CHANGE_WAR_TIME = "[C] C_ChangeWarTime";

    public C_ChangeWarTime(byte abyte0[], GameClient clientthread)
            throws Exception {
        super(abyte0);

        L1PcInstance player = clientthread.getActiveChar();
        if (player == null) return;

        L1Clan clan = L1World.getInstance().getClan(player.getClanname());
        if (clan != null) {
            int castle_id = clan.getCastleId();
            if (castle_id != 0) { // 城主クラン
                L1Castle l1castle = CastleTable.getInstance().getCastleTable(castle_id);
                player.sendPackets(new S_ServerMessage(305)); // 追加
                //Calendar cal = l1castle.getWarTime(); // コメントアウト
                //player.sendPackets(new S_WarTime(cal)); // コメントアウト
            }
        }
    }

    @Override
    public String getType() {
        return C_CHANGE_WAR_TIME;
    }

}
