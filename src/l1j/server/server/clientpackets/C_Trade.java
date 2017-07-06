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
import l1j.server.server.model.L1Trade;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.FaceToFace;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_Trade extends ClientBasePacket {

	private static final String C_TRADE = "[C] C_Trade";

	public C_Trade(byte abyte0[], GameClient clientthread) throws Exception {
		super(abyte0);

		L1PcInstance player = clientthread.getActiveChar();
		if (player == null || player.isGhost()) {
			return;
		}
		if (isTwoLogin(player))
			return;
		if (player.getOnlineStatus() == 0) {
			clientthread.kick();
			return;
		}
		if (player.getTradeID() > 0) {
			S_SystemMessage sm = new S_SystemMessage("당신은 현재 교환 중인 상태 입니다.");
			player.sendPackets(sm);
			sm = null;
			return;
		}
		if (player.isInvisble()) {
			player.sendPackets(new S_ServerMessage(334));
			return;
		}

		
		L1PcInstance target = FaceToFace.faceToFace(player);
		if (target != null) {
			if (player.getAccountName().equalsIgnoreCase(target.getAccountName())) {
				player.sendPackets(new S_Disconnect());
				target.sendPackets(new S_Disconnect());
				return;
			}
			if (player.isPrivateShop() || target.isPrivateShop() || target.isAutoClanjoin() || player == null || target == null) {
				return;
			}
			if (!target.isParalyzed()) {
				if (player.getTradeID() != 0) {
					L1Trade trade = new L1Trade();
					trade.TradeCancel(player);
				}

				if (target.getTradeID() != 0) {
					L1Trade trade = new L1Trade();
					trade.TradeCancel(target);
				}
				

				player.setTradeID(target.getId()); // 상대의 오브젝트 ID를 보존해 둔다
				target.setTradeID(player.getId());
				target.sendPackets(new S_Message_YN(252, player.getName()));
				// %0%s가 당신과 아이템의 거래를 바라고 있습니다. 거래합니까? (Y/N)
			}
		}
	}

	@Override
	public String getType() {
		return C_TRADE;
	}

	private boolean isTwoLogin(L1PcInstance c) {// 중복체크 변경
		boolean bool = false;

		for (L1PcInstance target : L1World.getInstance().getAllPlayers()) {
			if (target.noPlayerCK || target.noPlayerck2)
				continue;
			/** 로봇시스템 **/
			if (target.getRobotAi() != null)
				continue;
			/** 로봇시스템 **/
			if (c.getId() != target.getId() && (!target.isPrivateShop() && !target.isAutoClanjoin())) {
				if (c.getNetConnection().getAccountName().equalsIgnoreCase(target.getNetConnection().getAccountName())) {
					bool = true;
					break;
				}
			}
		}
		return bool;
	}
}
