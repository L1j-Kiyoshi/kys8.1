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
import l1j.server.server.model.skill.L1SkillId;

public class S_SPMR extends ServerBasePacket {

    private static final String S_SPMR = "[S] S_S_SPMR";

    private byte[] _byte = null;

    public S_SPMR(L1PcInstance pc) {
        buildPacket(pc);
    }

    private void buildPacket(L1PcInstance pc) {
        writeC(Opcodes.S_MAGIC_STATUS);
        // ウィズダム一部のSPはS_SkillBrave送信時に更新されるため控除しておく
        if (pc.hasSkillEffect(L1SkillId.STATUS_WISDOM_POTION)) {
            writeH(pc.getAbility().getSp() - pc.getAbility().getTrueSp() - 2); // 機器増加SP
        } else {
            writeH(pc.getAbility().getSp() - pc.getAbility().getTrueSp()); // 機器増加SP
        }
        writeH(pc.getResistance().getMr() - pc.getResistance().getBaseMr()); // 装備や魔法で増加したMR
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
        return S_SPMR;
    }
}
