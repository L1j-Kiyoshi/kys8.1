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

import java.util.ArrayList;

import l1j.server.server.Opcodes;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.templates.L1PrivateShopBuyList;
import l1j.server.server.templates.L1PrivateShopSellList;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket

public class S_PrivateShop extends ServerBasePacket {

    public S_PrivateShop(L1PcInstance pc, int objectId, int type) {
        L1PcInstance shopPc = (L1PcInstance) L1World.getInstance().findObject(objectId);

        if (shopPc == null) {
            return;
        }

        writeC(Opcodes.S_PERSONAL_SHOP_LIST);
        writeC(type);
        writeD(objectId);

        if (type == 0) {
            ArrayList<?> list = shopPc.getSellList();
            int size = list.size();
            pc.setPartnersPrivateShopItemCount(size);
            writeH(size);
            L1PrivateShopSellList pssl = null;
            L1ItemInstance item = null;
            for (int i = 0; i < size; i++) {
                pssl = (L1PrivateShopSellList) list.get(i);
                int itemObjectId = pssl.getItemObjectId();
                int count = pssl.getSellTotalCount() - pssl.getSellCount();
                int price = pssl.getSellPrice();
                item = shopPc.getInventory().getItem(itemObjectId);
                if (item != null) {
                    writeC(i);
                    writeD(count);
                    writeD(price);
                    writeH(item.getItem().getGfxId());
                    writeC(0);
                    if (!item.isIdentified())
                        writeC(0);
                    else {
                        writeC(1);
                    }
                    writeC(item.getItem().getBless());
                    writeS(item.getNumberedViewName(count));
//					writeC(0);
                    byte[] status = item.getStatusBytes();
                    writeC(status.length);
                    for (byte b : status)
                        writeC(b);
                }
            }
            writeH(0);
        } else if (type == 1) {
            ArrayList<?> list = shopPc.getBuyList();
            int size = list.size();
            writeH(size);
            L1PrivateShopBuyList psbl = null;
            L1ItemInstance item = null;
            for (int i = 0; i < size; i++) {
                psbl = (L1PrivateShopBuyList) list.get(i);
                int itemObjectId = psbl.getItemObjectId();
                int count = psbl.getBuyTotalCount();
                int price = psbl.getBuyPrice();
                item = shopPc.getInventory().getItem(itemObjectId);
                for (L1ItemInstance pcItem : pc.getInventory().getItems())
                    try {
                        if ((item.getItemId() == pcItem.getItemId()) && (item.getEnchantLevel() == pcItem.getEnchantLevel())) {
                            writeC(i);
                            writeD(count);
                            writeD(price);
                            writeD(pcItem.getId());
                            writeC(0);
                        }
                    } catch (Exception e) {
                    }
            }
            writeH(0);
        }
    }


    @Override
    public byte[] getContent() {
        return getBytes();
    }
}
