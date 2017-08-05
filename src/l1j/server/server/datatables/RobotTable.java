package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.utils.PerformanceTimer;
import l1j.server.server.utils.SQLUtil;

public class RobotTable {
    public class RobotTeleport {
        public int id;
        public int x;
        public int y;
        public int mapid;
        public int heading;
    }

    private static Logger _log = Logger.getLogger(RobotTable.class.getName());

    private static RobotTable _instance;

    private static HashMap<Integer, RobotTeleport> _TeleportList = new HashMap<Integer, RobotTeleport>();

    public static RobotTable getInstance() {
        if (_instance == null) {
            _instance = new RobotTable();
        }
        return _instance;
    }

    private RobotTable() {
        PerformanceTimer timer = new PerformanceTimer();
        System.out.print("[RobotTable] loading RobotTable...");
        selectRobotTeleportList();
        System.out.println("OK! " + timer.get() + " ms");
    }

    public void selectRobotTeleportList() {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;

        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con
                    .prepareStatement("select * from robot_teleport_list where isuse = ?");
            pstm.setInt(1, 1);
            rs = pstm.executeQuery();

            RobotTeleport robotTeleport = null;

            while (rs.next()) {
                robotTeleport = new RobotTeleport();
                robotTeleport.id = rs.getInt(1);
                robotTeleport.x = rs.getInt(2);
                robotTeleport.y = rs.getInt(3);
                robotTeleport.mapid = rs.getInt(4);
                robotTeleport.heading = rs.getInt(5);

                _TeleportList.put(robotTeleport.id, robotTeleport);
            }
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void createRobotTeleportList(L1PcInstance pc) {
        Connection con = null;
        PreparedStatement pstm = null;

        try {
            int i = 0;
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con
                    .prepareStatement("insert into robot_teleport_list set x = ?, y = ?, mapid = ?, heading = ?, note = ?, isuse = ?");
            pstm.setInt(++i, pc.getX());
            pstm.setInt(++i, pc.getY());
            pstm.setInt(++i, (int) pc.getMapId());
            pstm.setInt(++i, pc.getHeading());
            pstm.setString(++i, "");
            pstm.setInt(++i, 1);
            pstm.executeUpdate();
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public static HashMap<Integer, RobotTeleport> getRobotTeleportList() {
        return _TeleportList;
    }
}
