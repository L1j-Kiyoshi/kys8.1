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

package l1j.server.server.serverpackets;

import java.util.Calendar;

import l1j.server.Config;
import l1j.server.server.Opcodes;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket

public class S_WarTime extends ServerBasePacket {
    private static final String S_WAR_TIME = "[S] S_WarTime";

    public S_WarTime(Calendar cal) {
        // 1997/01/01 17:00を起点としている
        Calendar base_cal = Calendar.getInstance();
        base_cal.set(1997, 0, 1, 17, 0);
        long base_millis = base_cal.getTimeInMillis();
        long millis = cal.getTimeInMillis();
        long diff = millis - base_millis;
        diff -= 1200 * 60 * 1000; // 誤差修正
        diff = diff / 60000; // 分以下切捨て
        // timeは1加算すると3:02（182分）行われる
        int time = (int) (diff / 182);

        // writeDの直前のwriteCで時間の調節をすることができる
        // 0.7倍になった時間だけ減少しますが
        // 1つの調整すると、次の時間が広がって？
        writeC(Opcodes.S_SELECTABLE_TIME_LIST);
        writeH(6); // リストの数（6以上は無効）
        writeS(Config.TIME_ZONE); // 時間の後（）中に表示される文字列
        writeH(1);// 順番
        writeC(136);
        writeH(time);// 6:00
        writeH(2);// 順番
        writeC(178);
        writeH(time);// 6:30
        writeH(3);// 順番
        writeC(220);
        writeH(time);// 7:00
        writeH(4);// 順番
        writeC(218);
        writeH(time + 1);// 10:00
        writeH(5);// 順番
        writeC(4);
        writeH(time + 2);// 10:30
        writeH(6);// 順番
        writeC(46);// 11:00
        writeD(time + 2);
        writeC(0);

		/*writeC(Opcodes.S_SELECTABLE_TIME_LIST);
        writeH(6); // リストの数（6以上は無効）
		writeS(Config.TIME_ZONE); // 時間の後（）中に表示される文字列
		writeC(0); // ?
		writeC(0); // ?
		writeC(0);
		writeD(time);
		writeC(0);
		writeD(time - 1);
		writeC(0);
		writeD(time - 2);
		writeC(0);
		writeD(time - 3);
		writeC(0);
		writeD(time - 4);
		writeC(0);
		writeD(time - 5);
		writeC(0);*/
    }

    @Override
    public byte[] getContent() {
        return getBytes();
    }

    @Override
    public String getType() {
        return S_WAR_TIME;
    }
}
