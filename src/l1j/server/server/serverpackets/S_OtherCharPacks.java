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

//Referenced classes of package l1j.server.server.serverpackets:
//ServerBasePacket, S_OtherCharPacks

public class S_OtherCharPacks extends ServerBasePacket {

    private static final String S_OTHER_CHAR_PACKS = "[S] S_OtherCharPacks";
    private static final int STATUS_POISON = 1;
    private static final int STATUS_INVISIBLE = 2;
    private static final int STATUS_PC = 4;
    private static final int STATUS_FREEZE = 8;
    private static final int STATUS_BRAVE = 16;
    private static final int STATUS_ELFBRAVE = 32;
    private static final int STATUS_FASTMOVABLE = 64;
    private static final int STATUS_GHOST = 128;
    //private static final int BLOOD_LUST = 16;//100
    private static final int DANCING_BLADES = 16;//ダンシング

    private byte[] _byte = null;


    public S_OtherCharPacks(L1PcInstance pc, L1PcInstance user) {
        build(pc, user, false);
    }

    public S_OtherCharPacks(L1PcInstance pc, L1PcInstance user, boolean invis) {
        build(pc, user, invis);
    }

    public void build(L1PcInstance pc, L1PcInstance user, boolean invis) {
        int status = STATUS_PC;

        if (pc.getPoison() != null) {
            if (pc.getPoison().getEffectId() == 1) {
                status |= STATUS_POISON;
            }
        }
        if (pc.isGhost()) {
            status |= STATUS_GHOST;
        }
        if (pc.isInvisble()) {
            status |= STATUS_INVISIBLE;
        }
        if (pc.isBrave()) {
            status |= STATUS_BRAVE;
        }
        if (pc.isElfBrave()) {
            status |= STATUS_BRAVE;
            status |= STATUS_ELFBRAVE;
        }
        if (pc.isBlood_lust()) {
            status |= STATUS_BRAVE;
        }
        if (pc.isDancingBlades()) {
            status |= DANCING_BLADES;
        }
        if (pc.isFastMovable() || pc.isFruit()) {//ユグドラ追加変更1/19
            status |= STATUS_FASTMOVABLE;
        }
        if (pc.isParalyzed()) {
            status |= STATUS_FREEZE;
        }

        // int addbyte = 0;
        // int addbyte1 = 1;

        writeC(Opcodes.S_PUT_OBJECT);
        if (pc.isGm() && pc.isGmInvis()) {// インビジ追加
            writeH(0);
            writeH(0);
            writeD(0);
        } else if (pc.getMapId() == 631 || pc.getMapId() >= 514 && pc.getMapId() <= 516) {
            writeH(0);
            writeH(0);
            writeD(0);
        } else {
            writeH(pc.getX());
            writeH(pc.getY());
            writeD(pc.getId());
        }
        if (pc.isDead()) {
            writeH(pc.getTempCharGfxAtDead());
        } else if (pc.isPrivateShop()) {
            if (pc.shopPoly != 0)
                writeH(pc.shopPoly);
        } else {
            writeH(pc.getTempCharGfx());
        }
        if (pc.isDead()) {
            writeC(pc.getStatus());
        } else if (pc.isPrivateShop()) {
            writeC(0x46);
        } else {
            int polyId = pc.getTempCharGfx();
            int weaponId = pc.getCurrentWeapon();

            if (polyId == 3784 || polyId == 6137 || polyId == 6142 || polyId == 6147 || polyId == 6152 || polyId == 6157
                    || polyId == 9205 || polyId == 9206) {
                if (weaponId == 24 && pc.getWeapon() != null && pc.getWeapon().getItem().getType() == 18)
                    weaponId = 83;
            } else if (polyId == 13152 || polyId == 13153 || polyId == 12702 || polyId == 12681 || polyId == 8812
                    || polyId == 8817 || polyId == 6267 || polyId == 6270 || polyId == 6273 || polyId == 6276) {
                if (weaponId == 24 && pc.getWeapon() != null && pc.getWeapon().getItem().getType() == 18)
                    weaponId = 50;
            }
            writeC(weaponId);
        }
        writeC(pc.getHeading());
        // writeC(0); // makes char invis (0x01), cannot move. spells display
        writeC(pc.getLight().getChaLightSize());
        writeC(pc.getMoveSpeed());
        writeD(1); // exp
        // writeC(0x00);
        writeH(pc.getLawful());
        if (pc.getHuntCount() >= 1) { //
            writeS(pc.getName() + "\\fe(手配中)");
        } else {
            writeS(pc.getName());
        }
        writeS(pc.getTitle());
        writeC(status);
        writeD(pc.getClanid() > 0 ? pc.getClan().getEmblemId() > 0 ? pc.getClan().getEmblemId() : 0 : 0);
        writeS(pc.getClanname()); // クラン名
        writeS(null); // ペットホチン?
        writeC(0); // ?
        /*
         * if(pc.is_isInParty()) // パーティー中 { writeC(100 * pc.get_currentHp() /
		 * pc.get_maxHp()); } else { writeC(0xFF); }
		 */

//		if (pc.isInParty()) {	// パーティー中
//			writeC(100 * pc.getCurrentHp() / pc.getMaxHp());
//		} else {
        writeC(0xFF);
//		}
        writeC(pc.isDragonPearl() ? 0x08 : 0x00);
        writeC(0); // PC = 0, Mon = Lv
        if (pc.isPrivateShop()) {
            writeByte(pc.getShopChat());
        } else {
            writeC(0); // ?
        }
        writeC(0xFF);
        writeC(0xFF);

        writeC(0);
        writeC(pc.getAttackLevelCount());//pc.getAttackLevelCount()
//		 if (pc.isInParty()) {	// パーティー中
//			int mpRatio = 100;
//			if (0 < pc.getMaxMp()) {
//				mpRatio = 100 * pc.getCurrentMp() / pc.getMaxMp();
//			}
//			writeC(mpRatio);
//		} else {
        writeC(0xFF);
//		}
        writeH(0);
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
        return S_OTHER_CHAR_PACKS;
    }

}