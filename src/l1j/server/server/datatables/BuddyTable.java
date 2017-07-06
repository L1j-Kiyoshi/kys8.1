package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.model.L1Buddy;
import l1j.server.server.utils.SQLUtil;

public class BuddyTable {

    private final static Logger _log = Logger.getLogger(BuddyTable.class.getName());

    private static BuddyTable _instance;

    private final Map<Integer, L1Buddy> _buddys = new HashMap<Integer, L1Buddy>();

    public static BuddyTable getInstance() {
        if (_instance == null) {
            _instance = new BuddyTable();
        }
        return _instance;
    }

    private BuddyTable() {

        Connection con = null;
        PreparedStatement charIdPS = null;
        ResultSet charIdRS = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            charIdPS = con.prepareStatement("SELECT distinct(char_id) as char_id FROM character_buddys");

            charIdRS = charIdPS.executeQuery();
            PreparedStatement buddysPS = null;
            ResultSet buddysRS = null;
            while (charIdRS.next()) {
                try {
                    buddysPS = con.prepareStatement("SELECT buddy_id, buddy_name FROM character_buddys WHERE char_id = ?");
                    int charId = charIdRS.getInt("char_id");
                    buddysPS.setInt(1, charId);
                    L1Buddy buddy = new L1Buddy(charId);

                    buddysRS = buddysPS.executeQuery();
                    while (buddysRS.next()) {
                        buddy.add(buddysRS.getInt("buddy_id"), buddysRS.getString("buddy_name"));
                    }

                    _buddys.put(buddy.getCharId(), buddy);
                } catch (Exception e) {
                    _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
                } finally {
                    SQLUtil.close(buddysRS);
                    SQLUtil.close(buddysPS);
                }
            }
            _log.config("loaded " + _buddys.size() + " character's buddylists");
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(charIdRS);
            SQLUtil.close(charIdPS);
            SQLUtil.close(con);
        }
    }

    public L1Buddy getBuddyTable(int charId) {
        L1Buddy buddy = _buddys.get(charId);
        if (buddy == null) {
            buddy = new L1Buddy(charId);
            _buddys.put(charId, buddy);
        }
        return buddy;
    }

    public void addBuddy(int charId, int objId, String name) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {

            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("INSERT INTO character_buddys SET char_id=?, buddy_id=?, buddy_name=?");
            pstm.setInt(1, charId);
            pstm.setInt(2, objId);
            pstm.setString(3, name);
            pstm.execute();
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void removeBuddy(int charId, String buddyName) {
        Connection con = null;
        PreparedStatement pstm = null;
        L1Buddy buddy = getBuddyTable(charId);
        if (!buddy.containsName(buddyName)) {
            return;
        }

        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("DELETE FROM character_buddys WHERE char_id=? AND buddy_name=?");
            pstm.setInt(1, charId);
            pstm.setString(2, buddyName);
            pstm.execute();

            buddy.remove(buddyName);
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }
}
