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

import java.util.StringTokenizer;

import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1UserCalc implements L1CommandExecutor {
	private L1UserCalc() {
	}

	private static int calcUser = 0;

	public static L1CommandExecutor getInstance() {
		return new L1UserCalc();
	}

	public static int getClacUser() {
		return calcUser;
	}

	public void execute(L1PcInstance pc, String cmdName, String arg) {
		String msg = null;

		try {
			StringTokenizer tok = new StringTokenizer(arg);
			String type = tok.nextToken();
			int count = Integer.parseInt(tok.nextToken());

			if (type.equalsIgnoreCase("+")) {
				calcUser += count;
				msg = new StringBuilder().append("뻥튀기 : " + count + "명 추가 / 현재 뻥튀기 : " + calcUser + "명").toString();
			} else if (type.equalsIgnoreCase("-")) {
				int temp = calcUser - count;
				if (temp < 0) {
					pc.sendPackets(new S_SystemMessage("뻥튀기가 -가 될수는 없습니다. 현재 뻥튀기 : " + calcUser));
					return;
				} else {
					calcUser = temp;
					msg = new StringBuilder().append("뻥튀기 : " + count + "명 감소 / 현재 뻥튀기 : " + calcUser + "명").toString();
				}
			}
		} catch (Exception e) {
			// _log.log(Level.SEVERE, "", e);
			msg = new StringBuilder().append(cmdName).append(" [+,-] [COUNT] 입력").toString();
		} finally {
			if (msg != null) {
				pc.sendPackets(new S_SystemMessage(msg));
			}
		}
	}
}
