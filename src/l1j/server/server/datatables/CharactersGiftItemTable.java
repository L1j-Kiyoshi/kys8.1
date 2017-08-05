package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;

public class CharactersGiftItemTable {

    private static CharactersGiftItemTable _instance;
    private HashMap<Integer, ArrayList<Item>> _itemList;

    public static CharactersGiftItemTable getInstance() {
        if (_instance == null) {
            _instance = new CharactersGiftItemTable();
        }
        return _instance;
    }

    public static void reload() {
        CharactersGiftItemTable oldInstance = _instance;
        _instance = new CharactersGiftItemTable();
        oldInstance._itemList.clear();
    }

    private CharactersGiftItemTable() {

        _itemList = new HashMap<Integer, ArrayList<Item>>();

        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;

        // アイテム
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("select level from levelup_quests_item group by level order by level");
            rs = pstm.executeQuery();
            while (rs.next()) {
                readItem(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    private void readItem(int level) {

        ArrayList<Item> list = new ArrayList<CharactersGiftItemTable.Item>();

        // アイテム
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("select type, item_name, item_id, count, enchant, attrlevel, bless from levelup_quests_item where level=?");
            pstm.setInt(1, level);
            rs = pstm.executeQuery();
            while (rs.next()) {
                Item item = new Item();
                item.type = rs.getInt(1);
                item.itemName = rs.getString(2);
                item.itemId = rs.getInt(3);
                item.count = rs.getInt(4);
                item.enchant = rs.getInt(5);
                item.attrLevel = rs.getInt(6);
                item.bless = rs.getInt(7);

                list.add(item);
            }
            _itemList.put(level, list);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public Item[] getItems(int level) {
        ArrayList<Item> result = _itemList.get(level);
        if (result == null) return null;
        return result.toArray(new Item[result.size()]);

    }


    public static class Item {

        private int type;
        private String itemName;
        private int itemId;
        private int count;
        private int enchant;
        private int attrLevel;
        private int bless;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public int getItemId() {
            return itemId;
        }

        public void setItemId(int itemId) {
            this.itemId = itemId;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getEnchant() {
            return enchant;
        }

        public void setEnchant(int enchant) {
            this.enchant = enchant;
        }

        public int getAttrLevel() {
            return attrLevel;
        }

        public void setAttrLevel(int attrLevel) {
            this.attrLevel = attrLevel;
        }

        public int getBless() {
            return bless;
        }

        public void setBless(int bless) {
            this.bless = bless;
        }
    }
}