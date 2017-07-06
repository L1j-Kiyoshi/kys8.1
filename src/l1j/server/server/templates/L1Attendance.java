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

public class L1Attendance {
	
	private final int _day;

	private final L1Item _item;

	private int _count;
	
	private final L1Item _itempc;

	private int _countpc;


	public L1Attendance(int day , int itemId, int Count, int itemIdpc, int Countpc) {
		_day = day;
		_item = ItemTable.getInstance().getTemplate(itemId);
		_count = Count;

		_itempc = ItemTable.getInstance().getTemplate(itemIdpc);
		_countpc = Countpc;
	}
	
	public int getDay() {
		return _day;
	}
	

	public L1Item getItem() {
		return _item;
	}


	public int getCount() {
		return _count;
	}

	
	public L1Item getItempc() {
		return _itempc;
	}


	public int getCountpc() {
		return _countpc;
	}

}
