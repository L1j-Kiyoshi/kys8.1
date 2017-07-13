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

import l1j.server.server.Account;
import l1j.server.server.datatables.IpTable;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_SystemMessage;


public class L1RangeKick implements L1CommandExecutor {
	@SuppressWarnings("unused")
	private static Logger _log = Logger.getLogger(L1RangeKick.class.getName());

	private L1RangeKick() {}

	public static L1CommandExecutor getInstance() {
		return new L1RangeKick();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			L1PcInstance target = L1World.getInstance().getPlayer(arg);
//			if (target == null) {
//				target = CharacterTable.getInstance().restoreCharacter(arg);
//			}

			if (target != null) {
				IpTable ip = IpTable.getInstance();

				Account.ban(target.getAccountName()); // アカウントをBANさせる。
				ip.rangeBanIp(target.getNetConnection().getHostname());
				pc.sendPackets(new S_SystemMessage(target.getName() + "[" + pc.getNetConnection() + "]を広域追放しました。"));						
				L1World.getInstance().removeObject(target);
				target.getNetConnection().kick();
				target.getNetConnection().close();
				target.logout();	
				target.sendPackets(new S_Disconnect());
//				if (target.getOnlineStatus() == 1) {
//					target.sendPackets(new S_Disconnect());
//				}
			} else {
				pc.sendPackets(new S_SystemMessage("そのような名前のキャラクターは、ワールド内には存在しません。"));
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(cmdName + "[キャラクター名]で入力してください。"));
		}
	}
}
