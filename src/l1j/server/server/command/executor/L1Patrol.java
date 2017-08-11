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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.GameSystem.Robot.L1RobotInstance;
import l1j.server.server.Account;
import l1j.server.server.Opcodes;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.ServerBasePacket;

public class L1Patrol implements L1CommandExecutor {
    @SuppressWarnings("unused")
    private static Logger _log = Logger.getLogger(L1Patrol.class.getName());

    private L1Patrol() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1Patrol();
    }

    @Override
    public void execute(L1PcInstance pc, String cmdName, String arg) {
        pc.sendPackets(new L1PatrolCommandPacket());
    }

    private class L1PatrolCommandPacket extends ServerBasePacket {
        public L1PatrolCommandPacket() {
            ArrayList<L1PcInstance> players = new ArrayList<L1PcInstance>();
            Iterator<L1PcInstance> itr = L1World.getInstance().getAllPlayers().iterator();
            while (itr.hasNext()) {
                L1PcInstance pc = itr.next();
                if (pc instanceof L1RobotInstance) {
                    continue;
                }
                players.add(pc);
            }

            writeC(Opcodes.S_EVENT);
            writeC(0x2d);
            writeC(players.size());

            Account acc = null;
            Calendar cal = null;
            for (L1PcInstance pc : players) {
                if (pc instanceof L1RobotInstance) {
                    continue;
                }

                acc = Account.load(pc.getAccountName());
                // 時間情報、まずログイン時間を入れてみる655
                if (acc == null) {
                    writeD(0);
                } else {
                    cal = Calendar.getInstance(TimeZone.getTimeZone(Config.TIME_ZONE));
                    long lastactive = acc.getLastActive().getTime();
                    cal.setTimeInMillis(lastactive);
                    cal.set(Calendar.YEAR, 1970);
                    int time = (int) (cal.getTimeInMillis() / 1000);
                    writeD(time); // JST 1970 1/1 09:00 この基準
                }

                // キャラクター情報
                writeS(pc.getName()); // 半角12文字まで
                writeS(pc.getClanname()); // []内に表示される文字列。半角12文字まで
            }
        }

        @Override
        public byte[] getContent() throws IOException {
            return super.getBytes();
        }
    }
}
