/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * 
 * http://www.gnu.org/copyleft/gpl.html
 */

package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1ItemInstance;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket

public class S_AddItem extends ServerBasePacket {

    private static final String S_ADD_ITEM = "[S] S_AddItem";

    /**
     * リストにアイテムを1つ追加する。
     */
    public S_AddItem(L1ItemInstance item) {
        writeC(Opcodes.S_ADD_INVENTORY);
        writeD(item.getId());
        writeH(item.getItem().getItemDescId());

        if (item.getItemId() == 600226 || item.getItemId() == 600227) {
            writeH(0x0044);
        } else {
            int type = item.getItem().getUseType();
            if (type < 0) {
                type = 0;
            }
            writeC(type);
            int count = item.getChargeCount();
            if (count < 0) {
                count = 0;
            }
            writeC(count);
        }

        writeH(item.get_gfxid());
        writeC(item.getBless());
        writeD(item.getCount());
        int bit = 0;
        if (!item.getItem().isTradable()) bit += 2;//交換不可能
        if (item.getItem().isCantDelete()) bit += 4;//削除不可能
        if (item.getItem().get_safeenchant() < 0) bit += 8;//エンチャント不可能
        //  if(item.getItem().getWareHouse()>0&&!item.getItem().isTradable()) bit += 16; // 倉庫保管可能
        if (item.getBless() >= 128) bit = 46;
        if (item.isIdentified()) bit += 1;//確認
        writeC(bit);
        writeS(item.getViewName());


        if (!item.isIdentified()) {
            // 米感情の場合ステータスを送信する必要はない
            writeC(0);
        } else {
            byte[] status = item.getStatusBytes();
            writeC(status.length);
            for (byte b : status) {
                writeC(b);
            }
        }
        writeC(0x18);
        writeC(0);
        writeH(0);
        writeH(0);
        if (item.getItem().getType2() == 0) {
            writeC(0);
        } else {
            writeC(item.getEnchantLevel());
        }
        writeD(item.getId());
        writeD(0);
        writeD(0);
        writeD(item.getBless() >= 128 ? 3 : item.getItem().isTradable() ? 7 : 2);
        writeC(0);
        switch (item.getAttrEnchantLevel()) {
            case 0:
                writeC(0);
                break;
            case 1:
                writeC(0x11);
                break;
            case 2:
                writeC(0x21);
                break;
            case 3:
                writeC(0x31);
                break;
            case 4:
                writeC(0x41);
                break;
            case 5:
                writeC(0x51);
                break;
            case 6:
                writeC(0x12);
                break;
            case 7:
                writeC(0x22);
                break;
            case 8:
                writeC(0x32);
                break;
            case 9:
                writeC(0x42);
                break;
            case 10:
                writeC(0x52);
                break;
            case 11:
                writeC(0x13);
                break;
            case 12:
                writeC(0x23);
                break;
            case 13:
                writeC(0x33);
                break;
            case 14:
                writeC(0x43);
                break;
            case 15:
                writeC(0x53);
                break;
            case 16:
                writeC(0x14);
                break;
            case 17:
                writeC(0x24);
                break;
            case 18:
                writeC(0x34);
                break;
            case 19:
                writeC(0x44);
                break;
            case 20:
                writeC(0x54);
                break;
        }

        writeH(0);
    }

    @Override
    public byte[] getContent() {
        return _bao.toByteArray();
    }

    @Override
    public String getType() {
        return S_ADD_ITEM;
    }
}
