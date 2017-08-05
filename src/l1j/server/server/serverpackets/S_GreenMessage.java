/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package l1j.server.server.serverpackets;

import java.util.logging.Logger;

import l1j.server.server.Opcodes;

public class S_GreenMessage extends ServerBasePacket {

    private static Logger _log = Logger.getLogger(S_GreenMessage.class.getName());

    private static final String S_GREENMESSAGE = "[S] S_GreenMessage";

    public S_GreenMessage(String s) {
        writeC(Opcodes.S_PUT_OBJECT);
        writeC(0x54);
        writeC(0x02);
        writeS(s);
    }

    @Override
    public byte[] getContent() {
        return getBytes();
    }

    @Override
    public String getType() {
        return S_GREENMESSAGE;
    }
}