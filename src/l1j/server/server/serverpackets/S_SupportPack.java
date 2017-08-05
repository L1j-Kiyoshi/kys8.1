/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful ,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not , write to the Free Software
 * Foundation , Inc., 59 Temple Place - Suite 330, Boston , MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */

package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1SupportInstance;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket , S_SupportPack

public class S_SupportPack extends ServerBasePacket {

    private static final String S_SUPPORTPACK = "[S] S_SupportPack";
    private byte[] _byte = null;

    private static final int STATUS_POISON = 1;

    private static final int STATUS_FASTMOVABLE = 64;


    public S_SupportPack(L1SupportInstance pet, L1PcInstance pc) {
        writeC(Opcodes.S_PUT_OBJECT);
        writeH(pet.getX());
        writeH(pet.getY());
        writeD(pet.getId());
        writeH(pet.getGfxId()); // SpriteID in List.spr
        writeC(pet.getStatus()); // Modes in List.spr
        writeC(pet.getHeading());
        writeC(pet.getLight().getChaLightSize()); // (Bright) - 0~15
        writeC(pet.getMoveSpeed()); // ⅩΥ【Ι - 0:normal,1:fast,2:slow
        writeD(0);
        writeH(0);
        writeS("");//名前なし。継続とまりジム
        writeS(pet.getTitle());
        int status = 0;
        if (pet.getPoison() != null) {
            if (pet.getPoison().getEffectId() == 1) {
                status |= STATUS_POISON;
            }
        }
        if (pet.isFastMovable()) {
            status |= STATUS_FASTMOVABLE;
        }
        writeC(status);
        writeD(0); // ??
        writeS(null); // ??
        writeS(null);
        writeC(0); // ??
        if (pet.getMaster() != null && pet.getMaster().getId() == pc.getId()) {
            writeC(100 * pet.getCurrentHp() / pet.getMaxHp());
        } else {
            writeC(0xFF);
        }
        writeC(0);
        writeC(pet.getLevel()); // PC = 0, Mon = Lv
        writeC(0);
        writeC(0xFF);
        writeC(0xFF);
        writeC(0);
        writeC(0);
        if (pet.getMaster() != null && pet.getMaster().getId() == pc.getId()) {
            writeC(100 * pet.getCurrentMp() / pet.getMaxMp());
        } else {
            writeC(0xFF);
        }
        writeH(0);
    }

    @Override
    public byte[] getContent() {
        if (_byte == null) {
            _byte = _bao.toByteArray();
        }

        return _byte;
    }

    public String getType() {
        return S_SUPPORTPACK;
    }

}
