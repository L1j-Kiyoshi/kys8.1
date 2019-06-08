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
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;

public class S_AttackPacket extends ServerBasePacket {
    private static final String _S__1F_ATTACKPACKET = "[S] S_AttackPacket";

    private byte[] _byte = null;

	public S_AttackPacket(L1PcInstance pc, int objid, int type) {
		buildpacket(pc, objid, type, 0);
	}

	public S_AttackPacket(L1PcInstance pc, int objid, int type, int attacktype) {
		buildpacket(pc, objid, type, attacktype);
	}

	public S_AttackPacket(L1PcInstance pc, int objid, int type, int attacktype, int gfx) {
		buildpacket(pc, objid, type, attacktype, gfx);
	}

	private void buildpacket(L1PcInstance pc, int objid, int type, int attacktype) {
		writeC(Opcodes.S_ATTACK);
		writeC(type);
		writeD(pc.getId());
		writeD(objid);
		writeC(0x01); // damage
		writeC(0x00);
		writeC(pc.getMoveState().getHeading());
		writeH(0x0000); // target x
		writeH(0x0000); // target y
		writeC(attacktype); //
		writeH(0x00);
	}

	private void buildpacket(L1PcInstance pc, int objid, int type, int attacktype, int gfx) {
		writeC(Opcodes.S_ATTACK);
		writeC(type);
		writeD(pc.getId());
		writeD(objid);
		writeC(0x01); // damage
		writeC(0x00);
		writeC(pc.getMoveState().getHeading());
		writeH(0x0000); // target x
		writeH(0x0000); // target y
		writeC(attacktype); //
		writeH(gfx);
		writeH(0x00);
		writeH(0x00);
	}

	public S_AttackPacket(L1NpcInstance pc, int objid, int type) {
		writeC(Opcodes.S_ATTACK);
		writeC(type);
		writeD(pc.getId());
		writeD(objid);
		writeC(0x01); // damage
		writeC(0x00);
		writeC(pc.getMoveState().getHeading());
		writeH(0x0000); // target x
		writeH(0x0000); // target y
		writeC(0); //
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
        return _S__1F_ATTACKPACKET;
    }
}
