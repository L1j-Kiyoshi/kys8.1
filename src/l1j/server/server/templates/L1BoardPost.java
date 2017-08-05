/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package l1j.server.server.templates;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;

public class L1BoardPost {
    private static Logger _log = Logger.getLogger(L1BoardPost.class.getName());

    private final int _id;
    private final String _name;
    private final String _date;
    private final String _title;
    private final String _content;

    public int getId() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    public String getDate() {
        return _date;
    }

    public String getTitle() {
        return _title;
    }

    public String getContent() {
        return _content;
    }

    private String today(String timeZoneID) {
        TimeZone tz = TimeZone.getTimeZone(timeZoneID);
        Calendar cal = Calendar.getInstance(tz);
        int year = cal.get(Calendar.YEAR) - 2000;
        int month = cal.get(Calendar.MONTH) + 1;
        int date = cal.get(Calendar.DATE);
        return String.format("%02d/%02d/%02d", year, month, date);
    }

    private L1BoardPost(int id, String name, String title, String content) {
        _id = id;
        _name = name;
        _date = today(Config.TIME_ZONE);
        _title = title;
        _content = content;
    }

    private L1BoardPost(ResultSet rs) throws SQLException {
        _id = rs.getInt("id");
        _name = rs.getString("name");
        _date = rs.getString("date");
        _title = rs.getString("title");
        _content = rs.getString("content");
    }

    public synchronized static L1BoardPost create(String name, String title, String content) {
        Connection con = null;
        PreparedStatement pstm1 = null;
        ResultSet rs = null;
        PreparedStatement pstm2 = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm1 = con.prepareStatement("SELECT max(id) + 1 as newid FROM board_free");
            rs = pstm1.executeQuery();
            rs.next();
            int id = rs.getInt("newid");
            L1BoardPost topic = new L1BoardPost(id, name, title, content);
            pstm2 = con.prepareStatement("INSERT INTO board_free SET id=?, name=?, date=?, title=?, content=?");
            pstm2.setInt(1, topic.getId());
            pstm2.setString(2, topic.getName());
            pstm2.setString(3, topic.getDate());
            pstm2.setString(4, topic.getTitle());
            pstm2.setString(5, topic.getContent());
            pstm2.execute();
            return topic;
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm1);
            SQLUtil.close(pstm2);
            SQLUtil.close(con);
        }
        return null;
    }

    public synchronized static L1BoardPost createGM(String name, String title, String content) {
        Connection con = null;
        PreparedStatement pstm1 = null;
        ResultSet rs = null;
        PreparedStatement pstm2 = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm1 = con.prepareStatement("SELECT max(id) + 1 as newid FROM board_notice");
            rs = pstm1.executeQuery();
            rs.next();
            int id = rs.getInt("newid");
            L1BoardPost topic = new L1BoardPost(id, name, title, content);
            pstm2 = con.prepareStatement("INSERT INTO board_notice SET id=?, name=?, date=?, title=?, content=?");
            pstm2.setInt(1, topic.getId());
            pstm2.setString(2, topic.getName());
            pstm2.setString(3, topic.getDate());
            pstm2.setString(4, topic.getTitle());
            pstm2.setString(5, topic.getContent());
            pstm2.execute();
            return topic;
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm1);
            SQLUtil.close(pstm2);
            SQLUtil.close(con);
        }
        return null;
    }

    public synchronized static L1BoardPost createGM1(String name, String title, String content) {
        Connection con = null;
        PreparedStatement pstm1 = null;
        ResultSet rs = null;
        PreparedStatement pstm2 = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm1 = con.prepareStatement("SELECT max(id) + 1 as newid FROM board_notice1");
            rs = pstm1.executeQuery();
            rs.next();
            int id = rs.getInt("newid");
            L1BoardPost topic = new L1BoardPost(id, name, title, content);
            pstm2 = con.prepareStatement("INSERT INTO board_notice1 SET id=?, name=?, date=?, title=?, content=?");
            pstm2.setInt(1, topic.getId());
            pstm2.setString(2, topic.getName());
            pstm2.setString(3, topic.getDate());
            pstm2.setString(4, topic.getTitle());
            pstm2.setString(5, topic.getContent());
            pstm2.execute();
            return topic;
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm1);
            SQLUtil.close(pstm2);
            SQLUtil.close(con);
        }
        return null;
    }

    public synchronized static L1BoardPost createGM2(String name, String title, String content) {
        Connection con = null;
        PreparedStatement pstm1 = null;
        ResultSet rs = null;
        PreparedStatement pstm2 = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm1 = con.prepareStatement("SELECT max(id) + 1 as newid FROM board_notice2");
            rs = pstm1.executeQuery();
            rs.next();
            int id = rs.getInt("newid");
            L1BoardPost topic = new L1BoardPost(id, name, title, content);
            pstm2 = con.prepareStatement("INSERT INTO board_notice2 SET id=?, name=?, date=?, title=?, content=?");
            pstm2.setInt(1, topic.getId());
            pstm2.setString(2, topic.getName());
            pstm2.setString(3, topic.getDate());
            pstm2.setString(4, topic.getTitle());
            pstm2.setString(5, topic.getContent());
            pstm2.execute();
            return topic;
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm1);
            SQLUtil.close(pstm2);
            SQLUtil.close(con);
        }
        return null;
    }

    public synchronized static L1BoardPost createGM3(String name, String title, String content) {
        Connection con = null;
        PreparedStatement pstm1 = null;
        ResultSet rs = null;
        PreparedStatement pstm2 = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm1 = con.prepareStatement("SELECT max(id) + 1 as newid FROM board_notice3");
            rs = pstm1.executeQuery();
            rs.next();
            int id = rs.getInt("newid");
            L1BoardPost topic = new L1BoardPost(id, name, title, content);
            pstm2 = con.prepareStatement("INSERT INTO board_notice3 SET id=?, name=?, date=?, title=?, content=?");
            pstm2.setInt(1, topic.getId());
            pstm2.setString(2, topic.getName());
            pstm2.setString(3, topic.getDate());
            pstm2.setString(4, topic.getTitle());
            pstm2.setString(5, topic.getContent());
            pstm2.execute();
            return topic;
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm1);
            SQLUtil.close(pstm2);
            SQLUtil.close(con);
        }
        return null;
    }

    public synchronized static L1BoardPost createKey(String name, String title, String content) {
        Connection con = null;
        PreparedStatement pstm1 = null;
        ResultSet rs = null;
        PreparedStatement pstm2 = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm1 = con.prepareStatement("SELECT max(id) + 1 as newid FROM board_free_key");
            rs = pstm1.executeQuery();
            rs.next();
            int id = rs.getInt("newid");
            L1BoardPost topic = new L1BoardPost(id, name, title, content);
            pstm2 = con.prepareStatement("INSERT INTO board_free_key SET id=?, name=?, date=?, title=?, content=?");
            pstm2.setInt(1, topic.getId());
            pstm2.setString(2, topic.getName());
            pstm2.setString(3, topic.getDate());
            pstm2.setString(4, topic.getTitle());
            pstm2.setString(5, topic.getContent());
            pstm2.execute();
            return topic;
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm1);
            SQLUtil.close(pstm2);
            SQLUtil.close(con);
        }
        return null;
    }


    public synchronized static L1BoardPost createPhone(String name, String title, String content) {
        Connection con = null;
        PreparedStatement pstm1 = null;
        ResultSet rs = null;
        PreparedStatement pstm2 = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm1 = con.prepareStatement("SELECT max(id) + 1 as newid FROM board_posts_fix");
            rs = pstm1.executeQuery();
            rs.next();
            int id = rs.getInt("newid");
            L1BoardPost topic = new L1BoardPost(id, name, title, content);
            pstm2 = con.prepareStatement("INSERT INTO board_posts_fix SET id=?, name=?, date=?, title=?, content=?");
            pstm2.setInt(1, topic.getId());
            pstm2.setString(2, topic.getName());
            pstm2.setString(3, topic.getDate());
            pstm2.setString(4, topic.getTitle());
            pstm2.setString(5, topic.getContent());
            pstm2.execute();
            return topic;
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm1);
            SQLUtil.close(pstm2);
            SQLUtil.close(con);
        }
        return null;
    }

    public void board_Free() {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("DELETE FROM board_free WHERE id=?");
            pstm.setInt(1, getId());
            pstm.execute();
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void serverInfo() {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("DELETE FROM board_notice WHERE id=?");
            pstm.setInt(1, getId());
            pstm.execute();
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void operator1() {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("DELETE FROM board_notice1 WHERE id=?");
            pstm.setInt(1, getId());
            pstm.execute();
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void operator2() {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("DELETE FROM board_notice2 WHERE id=?");
            pstm.setInt(1, getId());
            pstm.execute();
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void operator3() {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("DELETE FROM board_notice2 WHERE id=?");
            pstm.setInt(1, getId());
            pstm.execute();
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void proposal() {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("DELETE FROM board_posts_fix WHERE id=?");
            pstm.setInt(1, getId());
            pstm.execute();
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }


    public static L1BoardPost findById(int id) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT * FROM board_free WHERE id=?");
            pstm.setInt(1, id);
            rs = pstm.executeQuery();
            if (rs.next()) {
                return new L1BoardPost(rs);
            }
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs, pstm, con);
        }
        return null;
    }

    public static L1BoardPost findByIdGM(int id) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT * FROM board_notice WHERE id=?");
            pstm.setInt(1, id);
            rs = pstm.executeQuery();
            if (rs.next()) {
                return new L1BoardPost(rs);
            }
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs, pstm, con);
        }
        return null;
    }

    public static L1BoardPost findByIdGM1(int id) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT * FROM board_notice1 WHERE id=?");
            pstm.setInt(1, id);
            rs = pstm.executeQuery();
            if (rs.next()) {
                return new L1BoardPost(rs);
            }
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs, pstm, con);
        }
        return null;
    }

    public static L1BoardPost findByIdGM2(int id) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT * FROM board_notice2 WHERE id=?");
            pstm.setInt(1, id);
            rs = pstm.executeQuery();
            if (rs.next()) {
                return new L1BoardPost(rs);
            }
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs, pstm, con);
        }
        return null;
    }

    public static L1BoardPost findByIdGM3(int id) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT * FROM board_notice3 WHERE id=?");
            pstm.setInt(1, id);
            rs = pstm.executeQuery();
            if (rs.next()) {
                return new L1BoardPost(rs);
            }
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs, pstm, con);
        }
        return null;
    }


    public static L1BoardPost findByIdPhone(int id) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT * FROM board_posts_fix WHERE id=?");
            pstm.setInt(1, id);
            rs = pstm.executeQuery();
            if (rs.next()) {
                return new L1BoardPost(rs);
            }
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs, pstm, con);
        }
        return null;
    }

    public static L1BoardPost findByIdKey(int id) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT * FROM board_free_key WHERE id=?");
            pstm.setInt(1, id);
            rs = pstm.executeQuery();
            if (rs.next()) {
                return new L1BoardPost(rs);
            }
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs, pstm, con);
        }
        return null;
    }

    private static PreparedStatement makeIndexStatement(Connection con, int id, int limit) throws SQLException {
        PreparedStatement result = null;
        int offset = 1;
        if (id == 0) {
            result = con.prepareStatement("SELECT * FROM board_free ORDER BY id DESC LIMIT ?");
        } else {
            result = con.prepareStatement("SELECT * FROM board_free WHERE id < ? ORDER BY id DESC LIMIT ?");
            result.setInt(1, id);
            offset++;
        }
        result.setInt(offset, limit);
        return result;
    }

    private static PreparedStatement makeIndexStatementGM(Connection con, int id, int limit) throws SQLException {
        PreparedStatement result = null;
        int offset = 1;
        if (id == 0) {
            result = con.prepareStatement("SELECT * FROM board_notice ORDER BY id DESC LIMIT ?");
        } else {
            result = con.prepareStatement("SELECT * FROM board_notice WHERE id < ? ORDER BY id DESC LIMIT ?");
            result.setInt(1, id);
            offset++;
        }
        result.setInt(offset, limit);
        return result;
    }

    private static PreparedStatement makeIndexStatementGM1(Connection con, int id, int limit) throws SQLException {
        PreparedStatement result = null;
        int offset = 1;
        if (id == 0) {
            result = con.prepareStatement("SELECT * FROM board_notice1 ORDER BY id DESC LIMIT ?");
        } else {
            result = con.prepareStatement("SELECT * FROM board_notice1 WHERE id < ? ORDER BY id DESC LIMIT ?");
            result.setInt(1, id);
            offset++;
        }
        result.setInt(offset, limit);
        return result;
    }

    private static PreparedStatement makeIndexStatementGM2(Connection con, int id, int limit) throws SQLException {
        PreparedStatement result = null;
        int offset = 1;
        if (id == 0) {
            result = con.prepareStatement("SELECT * FROM board_notice2 ORDER BY id DESC LIMIT ?");
        } else {
            result = con.prepareStatement("SELECT * FROM board_notice2 WHERE id < ? ORDER BY id DESC LIMIT ?");
            result.setInt(1, id);
            offset++;
        }
        result.setInt(offset, limit);
        return result;
    }

    private static PreparedStatement makeIndexStatementGM3(Connection con, int id, int limit) throws SQLException {
        PreparedStatement result = null;
        int offset = 1;
        if (id == 0) {
            result = con.prepareStatement("SELECT * FROM board_notice3 ORDER BY id DESC LIMIT ?");
        } else {
            result = con.prepareStatement("SELECT * FROM board_notice3 WHERE id < ? ORDER BY id DESC LIMIT ?");
            result.setInt(1, id);
            offset++;
        }
        result.setInt(offset, limit);
        return result;
    }

    private static PreparedStatement makeIndexStatementPhone(Connection con, int id, int limit) throws SQLException {
        PreparedStatement result = null;
        int offset = 1;
        if (id == 0) {
            result = con.prepareStatement("SELECT * FROM board_posts_fix ORDER BY id DESC LIMIT ?");
        } else {
            result = con.prepareStatement("SELECT * FROM board_posts_fix WHERE id < ? ORDER BY id DESC LIMIT ?");
            result.setInt(1, id);
            offset++;
        }
        result.setInt(offset, limit);
        return result;
    }

    private static PreparedStatement makeIndexStatementKey(Connection con, int id, int limit) throws SQLException {
        PreparedStatement result = null;
        int offset = 1;
        if (id == 0) {
            result = con.prepareStatement("SELECT * FROM board_free_key ORDER BY id DESC LIMIT ?");
        } else {
            result = con.prepareStatement("SELECT * FROM board_free_key WHERE id < ? ORDER BY id DESC LIMIT ?");
            result.setInt(1, id);
            offset++;
        }
        result.setInt(offset, limit);
        return result;
    }


    public static List<L1BoardPost> index(int limit) {
        return index(0, limit);
    }

    public static List<L1BoardPost> index(int id, int limit) {
        List<L1BoardPost> result = new ArrayList<L1BoardPost>();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = makeIndexStatement(con, id, limit);
            rs = pstm.executeQuery();
            while (rs.next()) {
                result.add(new L1BoardPost(rs));
            }
            return result;
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs, pstm, con);
        }
        return null;
    }

    public static List<L1BoardPost> indexGM(int id, int limit) {
        List<L1BoardPost> result = new ArrayList<L1BoardPost>();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = makeIndexStatementGM(con, id, limit);
            rs = pstm.executeQuery();
            while (rs.next()) {
                result.add(new L1BoardPost(rs));
            }
            return result;
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs, pstm, con);
        }
        return null;
    }

    public static List<L1BoardPost> indexGM1(int id, int limit) {
        List<L1BoardPost> result = new ArrayList<L1BoardPost>();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = makeIndexStatementGM1(con, id, limit);
            rs = pstm.executeQuery();
            while (rs.next()) {
                result.add(new L1BoardPost(rs));
            }
            return result;
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs, pstm, con);
        }
        return null;
    }

    public static List<L1BoardPost> indexGM2(int id, int limit) {
        List<L1BoardPost> result = new ArrayList<L1BoardPost>();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = makeIndexStatementGM2(con, id, limit);
            rs = pstm.executeQuery();
            while (rs.next()) {
                result.add(new L1BoardPost(rs));
            }
            return result;
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs, pstm, con);
        }
        return null;
    }

    public static List<L1BoardPost> indexGM3(int id, int limit) {
        List<L1BoardPost> result = new ArrayList<L1BoardPost>();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = makeIndexStatementGM3(con, id, limit);
            rs = pstm.executeQuery();
            while (rs.next()) {
                result.add(new L1BoardPost(rs));
            }
            return result;
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs, pstm, con);
        }
        return null;
    }


    public static List<L1BoardPost> indexPhone(int id, int limit) {
        List<L1BoardPost> result = new ArrayList<L1BoardPost>();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = makeIndexStatementPhone(con, id, limit);
            rs = pstm.executeQuery();
            while (rs.next()) {
                result.add(new L1BoardPost(rs));
            }
            return result;
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs, pstm, con);
        }
        return null;
    }

    public static List<L1BoardPost> indexKey(int id, int limit) {
        List<L1BoardPost> result = new ArrayList<L1BoardPost>();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = makeIndexStatementKey(con, id, limit);
            rs = pstm.executeQuery();
            while (rs.next()) {
                result.add(new L1BoardPost(rs));
            }
            return result;
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs, pstm, con);
        }
        return null;
    }
}
