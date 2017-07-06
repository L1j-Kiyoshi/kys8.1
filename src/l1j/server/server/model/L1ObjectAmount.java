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
package l1j.server.server.model;

public class L1ObjectAmount<T> {
	private final T _obj;
	private final int _amount;
	private final int _en;

	public L1ObjectAmount(T obj, int amount, int en) {
		_obj = obj;
		_amount = amount;
		_en = en;
	}

	public T getObject() {	return _obj;	}

	public int getAmount() {	return _amount;	}

	public int getEnchant() {	return _en;	}
}
