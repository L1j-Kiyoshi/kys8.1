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
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_BanParty extends ClientBasePacket {

    private static final String C_BAN_PARTY = "[C] C_BanParty";

    public C_BanParty(byte decrypt[], GameClient client) throws Exception {
        super(decrypt);
        String s = readS();

        L1PcInstance player = client.getActiveChar();
        if (player == null) return;
        if (!player.getParty().isLeader(player)) {
            // パーティーリーダーがない場合
            player.sendPackets(new S_ServerMessage(427)); //パーティーのリーダーだけ追放することができます。
            return;
        }

        for (L1PcInstance member : player.getParty().getMembers()) {
            if (member.getName().toLowerCase().equals(s.toLowerCase())) {
                player.getParty().kickMember(member);
                return;
            }
        }
        // 発見されなかった
        player.sendPackets(new S_ServerMessage(426, s)); // %0はパーティーメンバーがありません。
    }

    @Override
    public String getType() {
        return C_BAN_PARTY;
    }

}
