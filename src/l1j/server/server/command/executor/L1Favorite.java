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

import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.GMCommands;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1Favorite implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1Favorite.class.getName());
	private static final Map<Integer, String> _faviCom = new HashMap<Integer, String>();

	private L1Favorite() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Favorite();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			if (!_faviCom.containsKey(pc.getId())) {
				_faviCom.put(pc.getId(), "");
			}
			String faviCom = _faviCom.get(pc.getId());
			if (arg.startsWith("셋팅")) {
				// 커멘드의 등록
				StringTokenizer st = new StringTokenizer(arg);
				st.nextToken();
				if (!st.hasMoreTokens()) {
					pc.sendPackets(new S_SystemMessage("커멘드가 없습니다. "));
					return;
				}
				StringBuilder cmd = new StringBuilder();
				String temp = st.nextToken(); // 커멘드 타입
				if (temp.equalsIgnoreCase(cmdName)) {
					pc.sendPackets(new S_SystemMessage(cmdName + " 자신은 등록할 수 없습니다. "));
					return;
				}
				cmd.append(temp + " ");
				while (st.hasMoreTokens()) {
					cmd.append(st.nextToken() + " ");
				}
				faviCom = cmd.toString().trim();
				_faviCom.put(pc.getId(), faviCom);
				pc.sendPackets(new S_SystemMessage(faviCom + " 를 등록했습니다. "));
			} else if (arg.startsWith("보기")) {
				pc.sendPackets(new S_SystemMessage("현재의 등록 커멘드: " + faviCom));
			} else if (faviCom.isEmpty()) {
				pc.sendPackets(new S_SystemMessage("등록하고 있는 커멘드가 없습니다. "));
			} else {
				StringBuilder cmd = new StringBuilder();
				StringTokenizer st = new StringTokenizer(arg);
				StringTokenizer st2 = new StringTokenizer(faviCom);
				while (st2.hasMoreTokens()) {
					String temp = st2.nextToken();
					if (temp.startsWith("%")) {
						cmd.append(st.nextToken() + " ");
					} else {
						cmd.append(temp + " ");
					}
				}
				while (st.hasMoreTokens()) {
					cmd.append(st.nextToken() + " ");
				}
				pc.sendPackets(new S_SystemMessage(cmd + " 를 실행합니다. "));
				GMCommands.getInstance().handleCommands(pc, cmd.toString());
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(cmdName + " 셋팅 [커멘드명] " + "| " + cmdName + " 보기 | " + cmdName
					+ " [인수] 라고 입력해 주세요. "));
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}
}
