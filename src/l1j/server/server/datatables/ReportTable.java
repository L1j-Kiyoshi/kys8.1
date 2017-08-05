package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;

public class ReportTable {

    private static Logger _log = Logger.getLogger(ReportTable.class.getName());

    private static ReportTable _instance;

    public static ArrayList<String> name = new ArrayList<String>();

    public ReportTable() {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT * FROM report");
            rs = pstm.executeQuery();
            while (rs.next()) {
                String objid = rs.getString(1); // この部分の修正
                name.add(rs.getString("objid"));
                name.add(rs.getString(2));
            }
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public static ReportTable getInstance() {
        if (_instance == null) {
            _instance = new ReportTable();
        }
        return _instance;
    }


    public void report(String name, String reporter_name) throws Exception {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("INSERT INTO report SET objid=?, reporter=?");
            pstm.setString(1, name);
            pstm.setString(2, reporter_name);
            pstm.execute();
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }
}







