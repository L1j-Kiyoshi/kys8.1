/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.command.executor;

import java.util.logging.Logger;

import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_Invis;
import l1j.server.server.serverpackets.S_OtherCharPacks;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1Invisible implements L1CommandExecutor {
	@SuppressWarnings("unused")
	private static Logger _log = Logger.getLogger(L1Invisible.class.getName());

	private L1Invisible() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Invisible();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
		
			if (!pc.hasSkillEffect(L1SkillId.INVISIBILITY)) {
				pc.setGmInvis(true);
				pc.sendPackets(new S_Invis(pc.getId(), 1));
				pc.broadcastPacket(new S_Invis(pc.getId(), 1));
				pc.broadcastPacket(new S_RemoveObject(pc));
				pc.setSkillEffect(L1SkillId.INVISIBILITY, 0);
				pc.sendPackets(new S_SystemMessage("透明状態になりました。"));
			} else {
				pc.setGmInvis(false);
				pc.killSkillEffectTimer(L1SkillId.INVISIBILITY);
				pc.sendPackets(new S_Invis(pc.getId(), 0));
				for (L1PcInstance pc2 : L1World.getInstance().getVisiblePlayer(pc)) {
				pc.broadcastPacket(new S_OtherCharPacks(pc,pc2));
				}
				pc.sendPackets(new S_SystemMessage("透明状態を解除しました。"));
			}
			
			
			
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(cmdName + "コマンドエラー"));
		}
	}
}
