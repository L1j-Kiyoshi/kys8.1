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
import l1j.server.server.model.Instance.L1PcInstance;

public class S_NewCharPacket extends ServerBasePacket {
    private static final String _S__25_NEWCHARPACK = "[S] New Char Packet";
    private byte[] _byte = null;

    public S_NewCharPacket(L1PcInstance pc) {
        buildPacket(pc);
    }

    private void buildPacket(L1PcInstance pc) {
        writeC(Opcodes.S_NEW_CHAR_INFO);
        writeS(pc.getName());
        writeS("");
        writeC(pc.getType());
        writeC(pc.get_sex());
        writeH(pc.getLawful());
        writeH(pc.getMaxHp());
        writeH(pc.getMaxMp());
        writeC(pc.getAC().getAc());
        writeC(pc.getLevel());
        writeC(pc.getAbility().getStr());
        writeC(pc.getAbility().getDex());
        writeC(pc.getAbility().getCon());
        writeC(pc.getAbility().getWis());
        writeC(pc.getAbility().getCha());
        writeC(pc.getAbility().getInt());
        writeC(0);
        writeD(pc.getBirthDay());
        int code = pc.getLevel() ^ pc.getAbility().getStr() ^ pc.getAbility().getDex() ^ pc.getAbility().getCon()
                ^ pc.getAbility().getWis() ^ pc.getAbility().getCha() ^ pc.getAbility().getInt();
        writeC(code & 0xFF);
        //writeD(0000);


    }

    @Override
    public byte[] getContent() {
        if (_byte == null) {
            _byte = _bao.toByteArray();
        }
        return _byte;
    }

    @Override
    public String getType() {
        return _S__25_NEWCHARPACK;
    }

}
