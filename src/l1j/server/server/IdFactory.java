package l1j.server.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;

public class IdFactory {
    private static Logger _log = Logger.getLogger(IdFactory.class.getName());

    private int _curId;

    private Object _monitor = new Object();

    private static final int FIRST_ID = 0x10000000;

    private static IdFactory _instance = new IdFactory();

    private IdFactory() {
        loadState();
    }

    public static IdFactory getInstance() {
        return _instance;
    }

    public int nextId() {
        synchronized (_monitor) {
            return _curId++;
        }
    }

    private void loadState() {
        // DBからMAXIDを要求する
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;

        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("select max(id)+1 as nextid from (select id from character_items union all select id from character_teleport union all select id from character_warehouse union all select id from character_elf_warehouse union all select objid as id from characters union all select clan_id as id from clan_data union all select id from clan_warehouse union all select objid as id from pets) t");
            rs = pstm.executeQuery();

            int id = 0;
            if (rs.next()) {
                id = rs.getInt("nextid");
            }
            if (id < FIRST_ID) {
                id = FIRST_ID;
            }
            _curId = id;
//			System.out.println("■ オブジェクトID NUMBER.......................... " + _curId);
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }
}
