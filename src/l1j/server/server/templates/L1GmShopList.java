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

import l1j.server.server.model.Instance.L1ItemInstance;

public class L1GmShopList {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    private L1ItemInstance _item;

    private int _Count;

    private int _Price;

    public L1GmShopList(L1ItemInstance i, int c, int p) {
        _item = i;
        _Count = c;
        _Price = p;
    }

    public L1ItemInstance getItem() {
        return _item;
    }

    public void setCount(int i) {
        _Count = i;
    }

    public int getCount() {
        return _Count;
    }

    public int getPrice() {
        return _Price;
    }


}
