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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Item;
import l1j.server.server.utils.SQLUtil;

public class L1LevelPresent implements L1CommandExecutor {
    private static Logger _log = Logger.getLogger(L1LevelPresent.class
            .getName());

    private L1LevelPresent() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1LevelPresent();
    }

    @Override
    public void execute(L1PcInstance pc, String cmdName, String arg) {

        try {
            StringTokenizer st = new StringTokenizer(arg);
            int minlvl = Integer.parseInt(st.nextToken(), 10);
            int maxlvl = Integer.parseInt(st.nextToken(), 10);
            int itemid = Integer.parseInt(st.nextToken(), 10);
            int enchant = Integer.parseInt(st.nextToken(), 10);
            int count = Integer.parseInt(st.nextToken(), 10);

            L1Item temp = ItemTable.getInstance().getTemplate(itemid);
            if (temp == null) {
                pc.sendPackets(new S_SystemMessage("存在しないアイテムのIDです。"));
                return;
            }

            present(minlvl, maxlvl, itemid, enchant, count);
            pc.sendPackets(new S_SystemMessage(temp.getName() + "を " + count
                    + "個プレゼントしました。 （Lv" + minlvl + "~" + maxlvl + ")"));
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage("。レプギフト[最低レベル]、[最高レベル] [アイテムID] [エンチャント] [本数]に入力してください。"));
        }
    }

    public static void present(String account, int itemid, int enchant, int count) throws Exception {
        L1Item temp = ItemTable.getInstance().getTemplate(itemid);
        if (temp == null) {
            return;
        }

        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();

            if (account.compareToIgnoreCase("*") == 0) {
                pstm = con.prepareStatement("SELECT * FROM accounts");
            } else {
                pstm = con.prepareStatement("SELECT * FROM accounts WHERE login=?");
                pstm.setString(1, account);
            }
            rs = pstm.executeQuery();

            ArrayList<String> accountList = new ArrayList<String>();
            while (rs.next()) {
                accountList.add(rs.getString("login"));
            }

            present(accountList, itemid, enchant, count);

        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            throw e;
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }

    }

    public static void present(int minlvl, int maxlvl, int itemid, int enchant, int count) throws Exception {
        L1Item temp = ItemTable.getInstance().getTemplate(itemid);
        if (temp == null) {
            return;
        }

        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();

            pstm = con.prepareStatement("SELECT distinct(account_name) as account_name FROM characters WHERE level between ? and ?");
            pstm.setInt(1, minlvl);
            pstm.setInt(2, maxlvl);
            rs = pstm.executeQuery();

            ArrayList<String> accountList = new ArrayList<String>();
            while (rs.next()) {
                accountList.add(rs.getString("account_name"));
            }

            present(accountList, itemid, enchant, count);

        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            throw e;
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }

    }

    private static void present(ArrayList<String> accountList, int itemid, int enchant, int count) throws Exception {
        L1Item temp = ItemTable.getInstance().getTemplate(itemid);
        if (temp == null) {
            throw new Exception("存在しないアイテムID");
        }
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            con.setAutoCommit(false);
            L1ItemInstance item = null;
            for (String account : accountList) {
                if (temp.isStackable()) {
                    item = ItemTable.getInstance().createItem(itemid);
                    item.setEnchantLevel(enchant);
                    item.setCount(count);

                    pstm = con.prepareStatement("INSERT INTO character_warehouse SET id = ?, account_name = ?, item_id = ?, item_name = ?, count = ?, is_equipped=0, enchantlvl = ?, is_id = ?, durability = ?, charge_count = ?, remaining_time = ?");
                    pstm.setInt(1, item.getId());
                    pstm.setString(2, account);
                    pstm.setInt(3, item.getItemId());
                    pstm.setString(4, item.getName());
                    pstm.setInt(5, item.getCount());
                    pstm.setInt(6, item.getEnchantLevel());
                    pstm.setInt(7, item.isIdentified() ? 1 : 0);
                    pstm.setInt(8, item.get_durability());
                    pstm.setInt(9, item.getChargeCount());
                    pstm.setInt(10, item.getRemainingTime());
                    pstm.execute();
                } else {
                    item = null;
                    int createCount;
                    for (createCount = 0; createCount < count; createCount++) {
                        item = ItemTable.getInstance().createItem(itemid);
                        item.setEnchantLevel(enchant);
                        pstm = con.prepareStatement("INSERT INTO character_warehouse SET id = ?, account_name = ?, item_id = ?, item_name = ?, count = ?, is_equipped=0, enchantlvl = ?, is_id = ?, durability = ?, charge_count = ?, remaining_time = ?");
                        pstm.setInt(1, item.getId());
                        pstm.setString(2, account);
                        pstm.setInt(3, item.getItemId());
                        pstm.setString(4, item.getName());
                        pstm.setInt(5, item.getCount());
                        pstm.setInt(6, item.getEnchantLevel());
                        pstm.setInt(7, item.isIdentified() ? 1 : 0);
                        pstm.setInt(8, item.get_durability());
                        pstm.setInt(9, item.getChargeCount());
                        pstm.setInt(10, item.getRemainingTime());
                        pstm.execute();
                    }
                }
            }

            con.commit();
            con.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ignore) {
                // ignore
            }
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            throw new Exception(".present処理中にエラーが発生しました。");
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }
}
