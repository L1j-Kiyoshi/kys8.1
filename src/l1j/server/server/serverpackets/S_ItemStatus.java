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
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket

public class S_ItemStatus extends ServerBasePacket {

    private static final String S_ITEM_STATUS = "[S] S_ItemStatus";

    /**
     * アイテムの名前、状態、特性、重量などの表示を変更する
     */
    public S_ItemStatus(L1ItemInstance item) {
        if (item.getItem().getSetId() != 0) {
            if (item.getItem().getMainId() == item.getItem().getItemId()) {
                if (item.isEquipped()) {
                    build(item, null, true, true);
                } else {
                    build(item, null, true, false);
                }
            } else {
                build(item, null, false, false);
            }
        } else {
            build(item, null, false, false);
        }
    }

    public S_ItemStatus(L1ItemInstance item, L1PcInstance pc) {
        if (item.getItem().getSetId() != 0) {
            if (item.getItem().getMainId() == item.getItem().getItemId()) {
                if (item.isEquipped()) {
                    build(item, pc, true, true);
                } else {
                    build(item, pc, true, false);
                }
            } else {
                build(item, pc, false, false);
            }
        } else {
            build(item, pc, false, false);
        }
    }

    public S_ItemStatus(L1ItemInstance item, L1PcInstance pc, boolean dd, boolean check) {
        build(item, pc, dd, check);
    }

    public void build(L1ItemInstance item, L1PcInstance pc, boolean dd, boolean check) {
        writeC(Opcodes.S_CHANGE_ITEM_DESC_EX);
        writeD(item.getId());
        writeS(item.getViewName());
        writeD(item.getCount());
        if (!item.isIdentified()) {
            // 米感情の場合ステータスを送信する必要はない
            writeC(0);
        } else {
            byte[] status = null;
            if (dd) {
                status = item.getStatusBytes(pc, check);
            } else {
                status = item.getStatusBytes(pc);
            }
            writeC(status.length);
            for (byte b : status) {
                writeC(b);
            }
        }
    }

    @Override
    public byte[] getContent() {
        return _bao.toByteArray();
    }

    @Override
    public String getType() {
        return S_ITEM_STATUS;
    }
}
