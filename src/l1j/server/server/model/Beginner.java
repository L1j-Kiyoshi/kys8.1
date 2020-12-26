package l1j.server.server.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.IdFactory;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.templates.L1Item;
import l1j.server.server.utils.SQLUtil;

public class Beginner {

    private static Logger _log = Logger.getLogger(Beginner.class.getName());

    private static Beginner _instance;

    public static Beginner getInstance() {
        if (_instance == null) {
            _instance = new Beginner();
        }
        return _instance;
    }

    private Beginner() {
    }

    public static void reload() {
        Beginner oldInstance = _instance;
        _instance = new Beginner();
        if (oldInstance != null) ;
    }

    public int GiveItemToActivePc(L1PcInstance pc) {
        Connection con = null;
        PreparedStatement pstm1 = null;
        ResultSet rs = null;

        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm1 = con.prepareStatement("SELECT * FROM beginner WHERE activate IN(?,?)");

            pstm1.setString(1, "A");
            if (pc.isCrown()) {
                pstm1.setString(2, "P");
            } else if (pc.isKnight()) {
                pstm1.setString(2, "K");
            } else if (pc.isElf()) {
                pstm1.setString(2, "E");
            } else if (pc.isWizard()) {
                pstm1.setString(2, "W");
            } else if (pc.isDarkelf()) {
                pstm1.setString(2, "D");
            } else if (pc.isDragonknight()) {
                pstm1.setString(2, "T");
            } else if (pc.isBlackwizard()) {
                pstm1.setString(2, "B");
            } else if (pc.isWarrior()) {
                pstm1.setString(2, "J");
            } else {
                pstm1.setString(2, "A");
            }
            rs = pstm1.executeQuery();
            while (rs.next()) {
                int itemid = rs.getInt("item_id");
                int count = rs.getInt("count");
                int enchant = rs.getInt("enchantlvl");

                L1Item temp = ItemTable.getInstance().getTemplate(itemid);
                if (temp != null) {
                    if (!temp.isStackable()) {
                        L1ItemInstance item = null;
                        int createCount;
                        for (createCount = 0; createCount < count; createCount++) {
                            item = ItemTable.getInstance().createItem(itemid);
                            item.setEnchantLevel(enchant);
                            if (pc.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
                                pc.getInventory().storeItem(item);
                            } else {
                                break;
                            }
                        }
                        if (createCount > 0) {
                            pc.sendPackets(new S_ServerMessage(403, // %0を手に入れました。
                                    item.getLogName() + "(ID:" + itemid + ")"));
                        }
                    }
                }
            }
        } catch (SQLException e1) {
            _log.log(Level.SEVERE, e1.getLocalizedMessage(), e1);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm1);
            SQLUtil.close(con);
        }
        return 0;

    }


    public void writeBookmark(L1PcInstance pc) {
        Connection c = null;
        PreparedStatement p = null;
        PreparedStatement p1 = null;
        ResultSet r = null;

        try {
            c = L1DatabaseFactory.getInstance().getConnection();
            p = c.prepareStatement("SELECT * FROM beginner_teleport");
            p1 = c.prepareStatement("INSERT INTO character_teleport SET id = ?, char_id = ?, name = ?, locx = ?, locy = ?, mapid = ?, speed =?, num=?");

            r = p.executeQuery();
            while (r.next()) {
                p1.setInt(1, IdFactory.getInstance().nextId());
                p1.setInt(2, pc.getId());
                p1.setString(3, r.getString("name"));
                p1.setInt(4, r.getInt("locx"));
                p1.setInt(5, r.getInt("locy"));
                p1.setShort(6, r.getShort("mapid"));
                p1.setInt(7, -1);
                p1.setInt(8, 0);
//				p1.setInt(7, r.getInt("num"));
                p1.execute();
            }
        } catch (Exception e) {
            _log.log(Level.SEVERE, "ブックマークの追加でエラーが発生しました。", e);
        } finally {
            SQLUtil.close(r);
            SQLUtil.close(p1);
            SQLUtil.close(p);
            SQLUtil.close(c);
        }
    }

    public int GiveItem(L1PcInstance pc) {
        Connection con = null;
        PreparedStatement pstm1 = null;
        PreparedStatement pstm2 = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm1 = con.prepareStatement("SELECT * FROM beginner WHERE activate RLIKE(?)");
            String pcClass = "";
            if (pc.isCrown()) {
                pcClass = "P";
            } else if (pc.isKnight()) {
                pcClass = "K";
            } else if (pc.isElf()) {
                pcClass = "E";
            } else if (pc.isWizard()) {
                pcClass = "W";
            } else if (pc.isDarkelf()) {
                pcClass = "D";
            } else if (pc.isDragonknight()) {
                pcClass = "T";
            } else if (pc.isBlackwizard()) {
                pcClass = "B";
            } else if (pc.isWarrior()) {
                pcClass = "J";
            } else {
                pcClass = "A";
            }
            String sql = "A|"+pcClass;
            pstm1.setString(1,sql);
            rs = pstm1.executeQuery();
            while (rs.next()) {
                try {
                    pstm2 = con.prepareStatement("INSERT INTO character_items SET id=?, item_id=?, char_id=?, item_name=?, count=?, is_equipped=?, enchantlvl=?, is_id=?, durability=?, charge_count=?, remaining_time=?, last_used=?, bless=?, attr_enchantlvl=?, special_enchant = 0");
                    pstm2.setInt(1, IdFactory.getInstance().nextId());
                    pstm2.setInt(2, rs.getInt("item_id"));
                    pstm2.setInt(3, pc.getId());
                    pstm2.setString(4, rs.getString("item_name"));
                    pstm2.setInt(5, rs.getInt("count"));
                    pstm2.setInt(6, 0);
                    pstm2.setInt(7, rs.getInt("enchantlvl"));
                    pstm2.setInt(8, 1);
                    pstm2.setInt(9, 0);
                    pstm2.setInt(10, rs.getInt("charge_count"));
                    pstm2.setInt(11, 0);
                    pstm2.setTimestamp(12, null);
                    pstm2.setInt(13, 1);
                    pstm2.setInt(14, 0);
                    pstm2.execute();
                } catch (SQLException e2) {
                    _log.log(Level.SEVERE, e2.getLocalizedMessage(), e2);
                } finally {
                    SQLUtil.close(pstm2);
                }
            }
        } catch (SQLException e1) {
            _log.log(Level.SEVERE, e1.getLocalizedMessage(), e1);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm2);
            SQLUtil.close(pstm1);
            SQLUtil.close(con);
        }
        return 0;
    }
}