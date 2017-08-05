/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.command.executor;


import java.util.StringTokenizer;

import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Warehouse.PackageWarehouse;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Item;

public class L1PackagePresent implements L1CommandExecutor {

    private L1PackagePresent() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1PackagePresent();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            StringTokenizer st = new StringTokenizer(arg);
            if (pc.isGm()) { // オペレータのリングを着用したときのオペレータコマンドを使用可能
                String account = st.nextToken();
                int itemid = Integer.parseInt(st.nextToken(), 10);
                int enchant = Integer.parseInt(st.nextToken(), 10);
                int count = Integer.parseInt(st.nextToken(), 10);
                L1Item temp = ItemTable.getInstance().getTemplate(itemid);
                if (temp == null) {
                    pc.sendPackets(new S_SystemMessage("存在しないアイテムのIDです。"));
                    return;
                }
                PackageWarehouse.present(account, itemid, enchant, count);
                pc.sendPackets(new S_SystemMessage(temp.getNameId() + "を" + count + "個プレゼントしました。", true));
            } else {
                pc.sendPackets(new S_SystemMessage("あなたは、オペレータになる条件ではありません。"));
                return;
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage("パッケージ[アカウント名] [アイテムID] [エンチャント数] [アイテム数]を入力してください。（アカウント名を*にすると、全体の支払い）"));
        }
    }
}