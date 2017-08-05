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

package l1j.server.server.clientpackets;

import java.util.Calendar;

import l1j.server.server.GameClient;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.ResolventTable1;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.monitor.Logger.ItemActionType;
import l1j.server.server.monitor.LoggerInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;

public class C_DeleteInventoryItem extends ClientBasePacket {
    /**
     * 日付、時刻の記録
     **/
    Calendar rightNow = Calendar.getInstance();
    int day = rightNow.get(Calendar.DATE);
    int hour = rightNow.get(Calendar.HOUR);
    int min = rightNow.get(Calendar.MINUTE);
    int year = rightNow.get(Calendar.YEAR);
    int month = rightNow.get(Calendar.MONTH) + 1;
    String totime = "[" + year + ":" + month + ":" + day + ":" + hour + ":" + min + "]";
    private static final String C_DELETE_INVENTORY_ITEM
            = "[C] C_DeleteInventoryItem";

    public C_DeleteInventoryItem(byte[] decrypt, GameClient client) {
        super(decrypt);

        L1PcInstance pc = client.getActiveChar();
        if (pc == null) return;

        int length = readD();
        for (int i = 0; i < length; ++i) {
            int itemObjectId = readD();
            int count = readD();


            L1ItemInstance item = pc.getInventory().getItem(itemObjectId);

            // 削除しようとしたアイテムがサーバー上に存在しない場合
            if (item == null) {
                return;
            }

            if (!pc.isGm() && item.getItem().isCantDelete()) {
                pc.sendPackets(new S_ServerMessage(125));
                return;
            }

//		if(!pc.isGm() &&item.getBless() >= 128){
//			pc.sendPackets(new S_ServerMessage(210, item.getItem().getName())); // \f1%0はしまったり、または他人に両日をすることができません。
//			return;
//		}
//		
            Object[] petlist = pc.getPetList().values().toArray();
            L1PetInstance pet = null;
            for (Object petObject : petlist) {
                if (petObject instanceof L1PetInstance) {
                    pet = (L1PetInstance) petObject;
                    if (item.getId() == pet.getItemObjId()) {
                        // \f1%0はしまったり、または他人に両日をすることができません。
                        pc.sendPackets(new S_ServerMessage(210, item.getItem()
                                .getName()));
                        return;
                    }
                }
            }
            for (Object dollObject : pc.getDollList()) {
                if (dollObject instanceof L1DollInstance) {
                    L1DollInstance doll = (L1DollInstance) dollObject;
                    if (item.getId() == doll.getItemObjId()) {
                        // \f1%0はしまったり、または他人に両日をすることができません。
                        pc.sendPackets(new S_ServerMessage(210, item.getItem().getName()));
                        return;
                    }
                }
            }

            if (item.isEquipped()) {
                // \f1削除できないアイテムや装備しているアイテムは捨てることができません。
                pc.sendPackets(new S_ServerMessage(125));
                return;
            }

            int crystalCount = ResolventTable1.getInstance().getCrystalCount(item.getItem().getItemId());
            L1ItemInstance crystal = ItemTable.getInstance().createItem(40308);

            if (crystalCount != 0) {
                if (count != 0) {
                    crystal.setCount(crystalCount * count);
                    pc.getInventory().storeItem(crystal);
                    pc.sendPackets(new S_SystemMessage("\\aB" + item.getName() + "(" + count + "）の削除：" + crystal.getName() + "(" + crystal.getCount() + "）ワンを獲得。"));
                } else {
                    crystal.setCount(crystalCount * item.getCount());
                    pc.getInventory().storeItem(crystal);
                    pc.sendPackets(new S_SystemMessage("\\aB" + item.getName() + "(" + item.getCount() + "）の削除：" + crystal.getName() + "(" + crystal.getCount() + "）ワンを獲得。"));
                }
            }

            /** ファイルログの保存 **/
            LoggerInstance.getInstance().addItemAction(ItemActionType.Delete, pc, item, count);

            if (count == 0)
                count = item.getCount();
            pc.getInventory().removeItem(item, count);
            pc.getLight().turnOnOffLight();
        }
    }

    @Override
    public String getType() {
        return C_DELETE_INVENTORY_ITEM;
    }
}
