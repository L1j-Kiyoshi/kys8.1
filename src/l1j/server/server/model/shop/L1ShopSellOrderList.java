package l1j.server.server.model.shop;

import java.util.ArrayList;
import java.util.List;

import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;

class L1ShopSellOrder {
    private final L1AssessedItem _item;
    private final int _count;

    public L1ShopSellOrder(L1AssessedItem item, int count) {
        _item = item;
        _count = count;
    }

    public L1AssessedItem getItem() {
        return _item;
    }

    public int getCount() {
        return _count;
    }

}

public class L1ShopSellOrderList {
    private final L1Shop _shop;
    private final L1PcInstance _pc;
    private final List<L1ShopSellOrder> _list = new ArrayList<L1ShopSellOrder>();
    private int bugok = 0; // ** 店販売ビシャス防御 **//

    L1ShopSellOrderList(L1Shop shop, L1PcInstance pc) {
        _shop = shop;
        _pc = pc;
    }

    public void add(int itemObjectId, int count, L1PcInstance pc) {
        /** 日付と時刻を記録してみましょう **/
        // Calendar rightNow = Calendar.getInstance();
        // int day = rightNow.get(Calendar.DATE);
        // int year = rightNow.get(Calendar.YEAR);
        // int month = rightNow.get(Calendar.MONTH)+1;
        // String totime = "[" + year + ":" + month + ":" + day + "]";

        L1ItemInstance item;
        item = pc.getInventory().getItem(itemObjectId);

        // item instance null pointer exception
        if (item == null || item.getItem() == null)
            return;
        // item instance null pointer exception

        if (item.getCount() < count) {
            bugok = 1;
            return;
        }

        if (!item.isStackable() && count != 1) {
            bugok = 1;
            return;
        }

        if (item.getCount() <= 0 || count <= 0) {
            bugok = 1;
            return;
        }
        if (count > 500 && item.getItemId() != 41246) {
        }
        // ** 店販売ビシャス防御**//

        if (item.getBless() >= 128) {
            return;
        }

        L1AssessedItem assessedItem = _shop.assessItem(_pc.getInventory().getItem(itemObjectId));

        if (assessedItem == null) {
            /*
             * 買取リストにないアイテムが指定された。不正パッケージの可能性。
			 */
            throw new IllegalArgumentException();
        }

        _list.add(new L1ShopSellOrder(assessedItem, count));
    }

    // ** 店販売ビシャス防御 **//
    public int BugOk() {
        return bugok;
    }

    // ** 店販売ビシャス防御**//

    L1PcInstance getPc() {
        return _pc;
    }

    List<L1ShopSellOrder> getList() {
        return _list;
    }
}
