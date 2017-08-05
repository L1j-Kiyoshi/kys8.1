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

import java.util.Calendar;

public class L1CubeAuctionBoard {
    public L1CubeAuctionBoard() {
    }

    private int _Id;

    public int getId() {
        return _Id;
    }

    public void setId(int i) {
        _Id = i;
    }

    private int _objectid;

    public int getObjectId() {
        return _objectid;
    }

    public void setObjectId(int i) {
        _objectid = i;
    }

    private int _itemid;

    public int getItemId() {
        return _itemid;
    }

    public void setItemId(int i) {
        _itemid = i;
    }

    private String _itemName;

    public String getItemName() {
        return _itemName;
    }

    public void setItemName(String s) {
        _itemName = s;
    }

    private int _itemType;

    public int getItemType() {
        return _itemType;
    }

    public void setItemType(int s) {
        _itemType = s;
    }

    private int _itemCount;

    public int getItemCount() {
        return _itemCount;
    }

    public void setItemCount(int i) {
        _itemCount = i;
    }

    private int _itemEnchant;

    public int getItemEnchant() {
        return _itemEnchant;
    }

    public void setItemEnchant(int i) {
        _itemEnchant = i;
    }

    private int _itemAttrEnchant;

    public int getItemAttrEnchant() {
        return _itemAttrEnchant;
    }

    public void setItemAttrEnchant(int i) {
        _itemAttrEnchant = i;
    }

    private int _itemIdentity;

    public int getItemIdentity() {
        return _itemIdentity;
    }

    public void setItemIdentity(int i) {
        _itemIdentity = i;
    }

    private int _itemBless;

    public int getItemBless() {
        return _itemBless;
    }

    public void setItemBless(int i) {
        _itemBless = i;
    }

    private int _itemprice;

    public int getItemPrice() {
        return _itemprice;
    }

    public void setItemPrice(int i) {
        _itemprice = i;
    }

    private String _oldOwner;

    public String getOldOwner() {
        return _oldOwner;
    }

    public void setOldOwner(String s) {
        _oldOwner = s;
    }

    private int _oldOwnerId;

    public int getOldOwnerId() {
        return _oldOwnerId;
    }

    public void setOldOwnerId(int i) {
        _oldOwnerId = i;
    }

    private String _newOwner;

    public String getNewOwner() {
        return _newOwner;
    }

    public void setNewOwner(String s) {
        _newOwner = s;
    }

    private int _newOwnerId;

    public int getNewOwnerId() {
        return _newOwnerId;
    }

    public void setNewOwnerId(int i) {
        _newOwnerId = i;
    }

    private Calendar _deadline;

    public Calendar getDeadline() {
        return _deadline;
    }

    public void setDeadline(Calendar i) {
        _deadline = i;
    }

}