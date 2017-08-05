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

import l1j.server.server.Opcodes;
import l1j.server.server.model.gametime.RealTimeClock;

public class S_MatizAlarm extends ServerBasePacket {
    private static final String S_MatizAlarm = "[S] S_MatizAlarm";

    private byte[] _byte = null;

    public S_MatizAlarm(int type, int time, int duration, boolean mod) {

        //type 1、エルジャベ2、サンドワーム3、赤騎士団の進撃
        String name;
        switch (type) {
            case 1:
                name = "エルジャベ";
                break;
            case 2:
                name = "サンドワーム";
                break;
            case 3:
                name = "赤き騎士団の進撃";
                break;
            default:
                name = "アラームはありません";
                break;
        }
        int length = name.getBytes().length;

        int realTime, realTime2;
        if (mod) {
            realTime = RealTimeClock.getInstance().getRealTime().getSeconds() + time;  //いくつかの残り時間表記の時間

            if (realTime < 0)
                realTime = -realTime;

            realTime2 = RealTimeClock.getInstance().getRealTime().getSeconds() + duration; //消える時間

            if (realTime2 < 0)
                realTime2 = -realTime2;

        } else {

            realTime = RealTimeClock.getInstance().getRealTime().getSeconds();

            if (realTime < 0)
                realTime = -realTime;

            realTime2 = RealTimeClock.getInstance().getRealTime().getSeconds();

            if (realTime2 < 0)
                realTime2 = -realTime2;
        }
        int total = 0x1d + name.getBytes().length;


        writeC(Opcodes.S_EXTENDED_PROTOBUF);
        writeC(0x8d);
        writeC(0x00);
        writeC(0x8);
        writeC(0x1);
        writeC(0x10);
        writeC(0x1);

        writeC(0x1a); // 0x1a
        writeBit(total);
        writeC(0x8);
        writeC(type);

        writeC(0x1a);
        writeC(0x00);
        writeC(0x22);
        writeC(length);
        writeByte(name.getBytes());


        writeC(0x28);


        writeBit(realTime);

        writeC(0x30);

        writeBit(realTime2);

        writeC(0x3a);
        writeC(0x09);
        writeC(0x0a);
        writeC(0x04);
        writeC(0x34);
        writeC(0x36);
        writeC(0x35);
        writeC(0x34);
        writeC(0x10);
        writeC(0x90);
        writeC(0x4e);
        writeH(0x00);
    }


    @Override
    public byte[] getContent() {
        if (_byte == null) {
            _byte = getBytes();
        }
        return _byte;
    }

    @Override
    public String getType() {
        return S_MatizAlarm;
    }
}