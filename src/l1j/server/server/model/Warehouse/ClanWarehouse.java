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
package l1j.server.server.model.Warehouse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.templates.L1Item;
import l1j.server.server.utils.SQLUtil;

public class ClanWarehouse extends Warehouse {
    private static final long serialVersionUID = 1L;
    private static Logger _log = Logger.getLogger(ClanWarehouse.class.getName());
    private int _warehouse = 0;

    public ClanWarehouse(String clan) {
        super(clan);
    }

    @Override
    protected int getMax() {
        return Config.MAX_CLAN_WAREHOUSE_ITEM;
    }

    @Override
    public synchronized void loadItems() {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT * FROM clan_warehouse WHERE clan_name = ?");
            pstm.setString(1, getName());
            rs = pstm.executeQuery();
            L1ItemInstance item = null;
            L1Item itemTemplate = null;
            while (rs.next()) {
                item = new L1ItemInstance();
                int objectId = rs.getInt("id");
                item.setId(objectId);
                int itemId = rs.getInt("item_id");
                itemTemplate = ItemTable.getInstance().getTemplate(itemId);
                if (itemTemplate == null) {
                    throw new NullPointerException("item_id=" + itemId + " not found");
                }
                item.setItem(itemTemplate);
                item.setCount(rs.getInt("count"));
                item.setEquipped(false);
                item.setEnchantLevel(rs.getInt("enchantlvl"));
                item.setIdentified(rs.getInt("is_id") != 0 ? true : false);
                item.set_durability(rs.getInt("durability"));
                item.setChargeCount(rs.getInt("charge_count"));
                item.setRemainingTime(rs.getInt("remaining_time"));
                item.setLastUsed(rs.getTimestamp("last_used"));
                item.setBless(item.getItem().getBless());
                item.setAttrEnchantLevel(rs.getInt("attr_enchantlvl"));
                item.setSpecialEnchant(rs.getInt("special_enchant"));

                _items.add(item);
                L1World.getInstance().storeObject(item);
            }
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    @Override
    public synchronized void insertItem(L1ItemInstance item) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("INSERT INTO clan_warehouse SET id = ?, clan_name = ?, item_id = ?, item_name = ?, count = ?, is_equipped=0, enchantlvl = ?, is_id= ?, durability = ?, charge_count = ?, remaining_time = ?, last_used = ?, attr_enchantlvl = ?, special_enchant = ?, package = ?");
            pstm.setInt(1, item.getId());
            pstm.setString(2, getName());
            pstm.setInt(3, item.getItemId());
            pstm.setString(4, item.getName());
            pstm.setInt(5, item.getCount());
            pstm.setInt(6, item.getEnchantLevel());
            pstm.setInt(7, item.isIdentified() ? 1 : 0);
            pstm.setInt(8, item.get_durability());
            pstm.setInt(9, item.getChargeCount());
            pstm.setInt(10, item.getRemainingTime());
            pstm.setTimestamp(11, item.getLastUsed());
            pstm.setInt(12, item.getAttrEnchantLevel());
            pstm.setInt(13, item.getSpecialEnchant());
            pstm.setInt(14, item.isPackage() == false ? 0 : 1);
            pstm.execute();
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    @Override
    public synchronized void updateItem(L1ItemInstance item) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("UPDATE clan_warehouse SET count = ? WHERE id = ?");
            pstm.setInt(1, item.getCount());
            pstm.setInt(2, item.getId());
            pstm.execute();

        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    @Override
    public synchronized void deleteItem(L1ItemInstance item) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("DELETE FROM clan_warehouse WHERE id = ?");
            pstm.setInt(1, item.getId());
            pstm.execute();
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _items.remove(_items.indexOf(item));
    }

    public synchronized void deleteAllItems() {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("DELETE FROM clan_warehouse WHERE clan_name = ?");
            pstm.setString(1, getName());
            pstm.execute();
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public int getWarehouseUsingChar() {
        return _warehouse;
    }

    public boolean setWarehouseUsingChar(int objid, int prevOwner) {
        synchronized (this) {
            if (objid == _warehouse) {
                return true;
            }

            if (objid != 0 && _warehouse != 0) {
                if (prevOwner != _warehouse)
                    return false;
            }

            _warehouse = objid;

            return true;
        }
    }
}
