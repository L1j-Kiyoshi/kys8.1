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
		if (this.getNpcTemplate().get_npcId() == 999999) {// バグベア勝率掲示板
			if (BugRaceController.getInstance().getBugState() == 0) { // 表販売中
				player.sendPackets(new S_Board(this));
			} else if (BugRaceController.getInstance().getBugState() == 1) { // 試合中
				player.sendPackets(new S_ChatPacket(player, "試合中に見ることができません。"));
			} else if (BugRaceController.getInstance().getBugState() == 2) { // 次の試合準備中
				player.sendPackets(new S_ChatPacket(player, "次の試合の準備をしています。"));
			}
		} else {
			player.sendPackets(new S_Board(this));
		}
	}

	public void onAction(L1PcInstance player, int number) {
		player.sendPackets(new S_Board(this, number));
	}

	public void onActionRead(L1PcInstance player, int number) {
		if (this.getNpcTemplate().get_npcId() == 500001) {// ランキング掲示板
			player.sendPackets(new S_Ranking(player, number));
		} else if (this.getNpcTemplate().get_npcId() == 4200013) {//エンチャント掲示板
			player.sendPackets(new S_EnchantRanking(player, number));

		} else {
			if (this.getNpcTemplate().get_npcId() == 500002) {//提案
				if (!player.isGm()) {
					player.sendPackets(new S_SystemMessage("オペレータのみ閲覧することができます。"));
					return;
				}
			} else if (this.getNpcTemplate().get_npcId() == 9200036) {
				if (!player.isGm()) {
					player.sendPackets(new S_SystemMessage("オペレータのみ閲覧することができます。"));
					return;
				}
			}
			player.sendPackets(new S_BoardRead(this, number));
		}
	}
}
