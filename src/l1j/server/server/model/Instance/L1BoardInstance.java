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

package l1j.server.server.model.Instance;

import l1j.server.server.Controller.BugRaceController;
import l1j.server.server.serverpackets.S_Board;
import l1j.server.server.serverpackets.S_BoardRead;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_EnchantRanking;
import l1j.server.server.serverpackets.S_Ranking;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Npc;

public class L1BoardInstance extends L1NpcInstance {
	/**
	 * 
	 */
	// private GameServerSetting _GameServerSetting =
	// GameServerSetting.getInstance();
	private static final long serialVersionUID = 1L;

	public L1BoardInstance(L1Npc template) {
		super(template);
	}

	@Override
	public void onAction(L1PcInstance player) {
		if (this.getNpcTemplate().get_npcId() == 999999) {// 버그베어 승률 게시판
			if (BugRaceController.getInstance().getBugState() == 0) { // 표판매중
				player.sendPackets(new S_Board(this));
			} else if (BugRaceController.getInstance().getBugState() == 1) { // 경기중
				player.sendPackets(new S_ChatPacket(player, "경기 중에는 보실 수 없습니다."));
			} else if (BugRaceController.getInstance().getBugState() == 2) { // 다음경기준비중
				player.sendPackets(new S_ChatPacket(player, "다음 경기를 준비 중 입니다."));
			}
		} else {
			player.sendPackets(new S_Board(this));
		}
	}

	public void onAction(L1PcInstance player, int number) {
		player.sendPackets(new S_Board(this, number));
	}

	public void onActionRead(L1PcInstance player, int number) {
		if (this.getNpcTemplate().get_npcId() == 500001) {// 랭킹 게시판
			player.sendPackets(new S_Ranking(player, number));
		} else if (this.getNpcTemplate().get_npcId() == 4200013) {// 인챈 게시판
			player.sendPackets(new S_EnchantRanking(player, number));

		} else {
			if (this.getNpcTemplate().get_npcId() == 500002) {//건의사항
				if (!player.isGm()) {
					player.sendPackets(new S_SystemMessage("운영자만 열람할 수 있습니다."));
					return;
				}
			} else if (this.getNpcTemplate().get_npcId() == 9200036) {
				if (!player.isGm()) {
					player.sendPackets(new S_SystemMessage("운영자만 열람할 수 있습니다."));
					return;
				}
			}
			player.sendPackets(new S_BoardRead(this, number));
		}
	}
}
