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
package l1j.server.server.templates;

import l1j.server.server.datatables.ItemTable;

public class L1AdenShopItem {
    private final int _itemId;

    private final L1Item _item;

    private int _price;

    private final int _packCount;

    private int _count;

    private String _html;

    private int _status;

    private int _type;

    public L1AdenShopItem(int itemId, int price, int packCount, String html,
                          int status, int type) {
        _itemId = itemId;
        _item = ItemTable.getInstance().getTemplate(itemId);
        _price = price;
        _packCount = packCount;
        _html = html;
        _count = 1;
        _status = status;
        _type = type;
    }

    public int getItemId() {
        return _itemId;
    }

    public L1Item getItem() {
        return _item;
    }

    public void setPrice(int i) {
        _price = i;
    }

    public int getPrice() {
        return _price;
    }

    public int getPackCount() {
        return _packCount;
    }

    public int getCount() {
        return _count;
    }

    public void setCount(int i) {
        _count = i;
    }

    public String getHtml() {
        return _html;
    }

    public int getStatus() {
        return _status;
    }

    public int getType() {
        return _type;
    }
}
