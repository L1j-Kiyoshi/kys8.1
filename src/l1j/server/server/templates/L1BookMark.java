package l1j.server.server.templates;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_BookMarkLoad;
import l1j.server.server.serverpackets.S_Bookmarks;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.SQLUtil;

public class L1BookMark {

    private static Logger _log = Logger.getLogger(L1BookMark.class.getName());

    private int _charId;
    private int _id;
    private String _name;
    private int _locX;
    private int _locY;
    private short _mapId;
    private int _randomX;
    private int _randomY;
    private int _speed_id;

    public int getSpeed_id() {
       return _speed_id;
    }
    public void setSpeed_id(int i) {
        _speed_id = i;
    }
    public int getId() {
        return _id;
    }
    public void setId(int i) {
        _id = i;
    }
    public int getCharId() {
        return _charId;
    }
    private int _NumId;

    public int getNumId() {
        return _NumId;
    }

    public void setNumId(int i) {
        _NumId = i;
    }

    private int _Temp_id;

    public int getTemp_id() {
        return _Temp_id;
    }

    public void setTemp_id(int i) {
        _Temp_id = i;
    }

    public void setCharId(int i) {
        _charId = i;
    }

    public String getName() {
        return _name;
    }

    public void setName(String s) {
        _name = s;
    }

    public int getLocX() {
        return _locX;
    }

    public void setLocX(int i) {
        _locX = i;
    }

    public int getLocY() {
        return _locY;
    }

    public void setLocY(int i) {
        _locY = i;
    }

    public short getMapId() {
        return _mapId;
    }

    public void setMapId(short i) {
        _mapId = i;
    }

    public int getRandomX() {
        return _randomX;
    }

    public void setRandomX(int i) {
        _randomX = i;
    }

    public int getRandomY() {
        return _randomY;
    }

    public void setRandomY(int i) {
        _randomY = i;
    }

    public L1BookMark() {
    }

    @SuppressWarnings("resource")
	public static void bookmarkDB(L1PcInstance pc) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        L1BookMark bookmark = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT * FROM character_teleport WHERE char_id='" + pc.getId() + "' ORDER BY num_id ASC");
            rs = pstm.executeQuery();
           int i = 0;
            while (rs.next()) {
                bookmark = new L1BookMark();
                bookmark.setCharId(rs.getInt("char_id"));
                bookmark.setId(rs.getInt("id"));
                bookmark.setNumId(i);
                bookmark.setTemp_id(i);
                bookmark.setSpeed_id(rs.getInt("speed_id"));
                bookmark.setName(rs.getString("name"));
                bookmark.setLocX(rs.getInt("locx"));
                bookmark.setLocY(rs.getInt("locy"));
                bookmark.setMapId(rs.getShort("mapid"));
                bookmark.setRandomX(rs.getShort("randomX"));
                bookmark.setRandomY(rs.getShort("randomY"));
                pc._bookmarks.add(bookmark);
                i++;
            }
            pstm = con.prepareStatement("SELECT * FROM character_teleport WHERE char_id='" + pc.getId() + "' AND speed_id > -1 ORDER BY speed_id ASC");
            rs = pstm.executeQuery();
            while (rs.next()) {
                pc._speedbookmarks.add(pc._bookmarks.get(rs.getInt("num_id")));
            }

        } catch (SQLException e) {
           // _log.log(Level.WARNING, "bookmarks 例外が発生。", e);
        } finally {
            SQLUtil.close(rs, pstm, con);
       }
    }

	public static void insertBookmark(L1BookMark bookmark) {
		Connection con = null;
		PreparedStatement pstm = null;

		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("INSERT INTO character_teleport SET id = ?, char_id = ?, name = ?, locx = ?, locy = ?, mapid = ?, randomX = ?, randomY = ?");
			pstm.setInt(1, bookmark.getId());
			pstm.setInt(2, bookmark.getCharId());
			pstm.setString(3, bookmark.getName());
			pstm.setInt(4, bookmark.getLocX());
			pstm.setInt(5, bookmark.getLocY());
			pstm.setInt(6, bookmark.getMapId());
			pstm.setInt(7, 0);
			pstm.setInt(8, 0);
			pstm.executeUpdate();
		} catch (SQLException e) {
			//_log.log(Level.SEVERE, "ブックマークの追加でエラーが発生しました。", e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

    
    public static void deleteBookmark(L1PcInstance pc, String s) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            L1BookMark book = pc.getBookMark(s);
            if (book != null) {
                con = L1DatabaseFactory.getInstance().getConnection();
                pstm = con.prepareStatement("DELETE FROM character_teleport WHERE id=? AND char_id=?");
                pstm.setInt(1, book.getId());
                pstm.setInt(2, pc.getId());
                pstm.execute();
                int del_id = book.getNumId();
                for (L1BookMark books : pc.getBookMarkArray()) {
                    if (book == books) {
                        pc._bookmarks.remove(book);
                    }
                    if (books.getNumId() > del_id) {
                        books.setNumId(books.getNumId() - 1);
                        books.setTemp_id(books.getTemp_id() - 1);
                    }
                }
                if (pc._speedbookmarks.contains(book)) {
                    del_id = book.getSpeed_id();
                    for (L1BookMark books : pc.getSpeedBookMarkArray()) {
                        if (book == books) {
                            pc._speedbookmarks.remove(book);
                        }
                        if (books.getSpeed_id() > del_id) {
                            books.setSpeed_id(books.getSpeed_id() - 1);
                        }
                    }
                }

                WriteBookmark(pc);
                pc._bookmarks.clear();
                pc._speedbookmarks.clear();
                bookmarkDB(pc);
           }
        } catch (SQLException e) {
            //_log.log(Level.SEVERE, "ブックマークの削除でエラーが発生しました。", e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    @SuppressWarnings("resource")
	public static synchronized void addBookmark(L1PcInstance pc, String s) {
        if (!pc.getMap().isMarkable() && !pc.isGm()) {
            pc.sendPackets(new S_ServerMessage(214));
            return;
        }
        if (pc._bookmarks.size() >= pc.getMark_count()) {
            if (pc.getMark_count() == 100) {
                pc.sendPackets(new S_ServerMessage(2930));
                return;
            } else {
                pc.sendPackets(new S_ServerMessage(676));
                return;
            }
        }
        if (pc.getBookMark(s) == null) {
            Connection con = null;
            PreparedStatement pstm = null;
            ResultSet rs = null;
            try {
                con = L1DatabaseFactory.getInstance().getConnection();
                pstm = con.prepareStatement("SELECT max(id)+1 as newid FROM character_teleport");
                rs = pstm.executeQuery();
                rs.next();
                int id = rs.getInt("newid");
                L1BookMark bookmark = new L1BookMark();
                bookmark.setId(id);
                pstm = con.prepareStatement("SELECT max(num_id)+1 as newid FROM character_teleport WHERE char_id=?");
                pstm.setInt(1, pc.getId());
                rs = pstm.executeQuery();
                rs.next();
                int Numid = rs.getInt("newid");                
                bookmark.setNumId(Numid);
                bookmark.setTemp_id(Numid);
                bookmark.setSpeed_id(-1);
                bookmark.setCharId(pc.getId());
                bookmark.setName(s);
                bookmark.setLocX(pc.getX());
                bookmark.setLocY(pc.getY());
                bookmark.setMapId(pc.getMapId());
                pstm = con.prepareStatement("INSERT INTO character_teleport SET id=?,num_id=?,speed_id=?, char_id=?, name=?, locx=?, locy=?, mapid=?");
                pstm.setInt(1, bookmark.getId());
                pstm.setInt(2, bookmark.getNumId());
                pstm.setInt(3, bookmark.getSpeed_id());
                pstm.setInt(4, bookmark.getCharId());
                pstm.setString(5, bookmark.getName());
                pstm.setInt(6, bookmark.getLocX());
                pstm.setInt(7, bookmark.getLocY());
                pstm.setInt(8, bookmark.getMapId());
                pstm.execute();
                pc._bookmarks.add(bookmark);
                pc.sendPackets(new S_Bookmarks(s, bookmark.getMapId(), bookmark.getLocX(), bookmark.getLocY(), bookmark.getNumId()));
            } catch (SQLException e) {
                //_log.log(Level.SEVERE, "ブックマークの追加でエラーが発生しました。", e);
            } finally {
                SQLUtil.close(pstm);
                SQLUtil.close(con);
            }

        } else {
            pc.sendPackets(new S_ServerMessage(1655));// 同じ記憶人で保存することができません。
       }
    }

    public static void WriteBookmark(L1PcInstance pc) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            if (pc.getBookMarkSize() > 0) {
                con = L1DatabaseFactory.getInstance().getConnection();
                int size = pc._bookmarks.size();
                if (size > 0) {
                    for (int i = 0; i < size; i++) {
                        pstm = con.prepareStatement("UPDATE character_teleport SET num_id=?, speed_id=?  WHERE id=?");
                        pstm.setInt(1, pc._bookmarks.get(i).getTemp_id());
                        if (pc._speedbookmarks.contains(pc._bookmarks.get(i))) {
                           pstm.setInt(2, pc._bookmarks.get(i).getSpeed_id());
                       } else {
                            pstm.setInt(2, -1);
                        }
                        pstm.setInt(3, pc._bookmarks.get(i).getId());
                        pstm.execute();
                    }
                }
            }
        } catch (SQLException e) {
           // _log.log(Level.WARNING, "WriteBookmark 例外が発生。", e);
        } finally {
            SQLUtil.close(rs, pstm, con);
       }
    }

    @SuppressWarnings("resource")
	public synchronized static void Bookmarkitem(L1PcInstance pc, L1ItemInstance useItem , int obj_id, boolean isChange) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        L1BookMark bookmark = null;
        try {
            int size = pc._bookmarks.size();
            int count = 0;
            int count2 = 0;
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT max(id)+1 as newid FROM character_teleport");
            rs = pstm.executeQuery();
            rs.next();
            int Numid = rs.getInt("newid");
            pstm = con.prepareStatement("SELECT * FROM beginner_addteleport WHERE item_obj_id='" + obj_id + "' ORDER BY num_id ASC");
            rs = pstm.executeQuery();
            if (pc.getMark_count() == size) {
                pc.sendPackets(new S_SystemMessage("記憶ウィンドウに桁が一つもありません。[拡張が必要]"));
                return;
            }
            while (rs.next()) {
                if (size + 1 > pc.getMark_count()) {
                    count++;
                    continue;
                }
                count2++;
                bookmark = new L1BookMark();
                bookmark.setId(Numid++);
                bookmark.setNumId(size++);
                bookmark.setSpeed_id(-1);
                bookmark.setCharId(pc.getId());
                bookmark.setName(rs.getString("name"));
                bookmark.setLocX(rs.getInt("locx"));
                bookmark.setLocY(rs.getInt("locy"));
                bookmark.setMapId(rs.getShort("mapid"));
                bookmark.setRandomX(rs.getInt("randomX"));
                bookmark.setRandomY(rs.getInt("randomY"));
                int i = 1;
                if (!isChange) {
                    pstm = con.prepareStatement("INSERT INTO character_teleport SET id=?,num_id=?,speed_id=?, char_id=?, name=?, locx=?, locy=?, mapid=?,randomX=?,randomY=?");
                    pstm.setInt(i++, bookmark.getId());
                    pstm.setInt(i++, bookmark.getNumId());
                    pstm.setInt(i++, bookmark.getSpeed_id());
                    pstm.setInt(i++, bookmark.getCharId());
                    pstm.setString(i++, bookmark.getName());
                    pstm.setInt(i++, bookmark.getLocX());
                    pstm.setInt(i++, bookmark.getLocY());
                    pstm.setInt(i++, bookmark.getMapId());
                    pstm.setInt(i++, bookmark.getRandomX());
                    pstm.setInt(i++, bookmark.getRandomY());
                    pstm.execute();
                } else {
                    pstm = con .prepareStatement("UPDATE character_teleport SET num_id=?,speed_id=?, char_id=?, name=?, locx=?, locy=?, mapid=?,randomX=?,randomY=?,item_obj_id=? WHERE  id=?");
                    pstm.setInt(i++, bookmark.getNumId());
                    pstm.setInt(i++, bookmark.getSpeed_id());
                    pstm.setInt(i++, bookmark.getCharId());
                    pstm.setString(i++, bookmark.getName());
                    pstm.setInt(i++, bookmark.getLocX());
                    pstm.setInt(i++, bookmark.getLocY());
                    pstm.setInt(i++, bookmark.getMapId());
                    pstm.setInt(i++, bookmark.getRandomX());
                    pstm.setInt(i++, bookmark.getRandomY());
                    pstm.setInt(i++, 0);
                    pstm.setInt(i++, rs.getInt("id"));
                    pstm.execute();
                }
                pc.addBookMark(bookmark);
                pc.sendPackets(new S_Bookmarks(bookmark.getName(), bookmark.getMapId(), bookmark.getLocX(), bookmark.getLocY(), bookmark.getNumId()));
            }
            pc.sendPackets(new S_SystemMessage("[ " + count2  + " ]つの記憶が登録"));
            if (count != 0) {
                pc.sendPackets(new S_SystemMessage("[ " + count + " ]つの記憶は保存されません。[拡張が必要]"));
            }
            pc.sendPackets(new S_BookMarkLoad(pc));
            pc.getInventory().removeItem(useItem, 1);
        } catch (SQLException e) {
           // _log.log(Level.WARNING, "WriteBookmark 例外が発生。", e);
        } finally {
            bookmark = null;
          SQLUtil.close(rs, pstm, con);
        }
    }

   public static void deleteBookmarkItem(int obj_id) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("DELETE FROM character_teleport WHERE item_obj_id=?");
            pstm.setInt(1, obj_id);
            pstm.execute();
        } catch (SQLException e) {
           // _log.log(Level.SEVERE, "ブックマークの削除でエラーが発生しました。", e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public synchronized static void MakeBookmarkItem(L1PcInstance pc, L1ItemInstance item) {
        int size = pc._speedbookmarks.size();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT max(id)+1 as newid FROM character_teleport");
            rs = pstm.executeQuery();
            rs.next();
            int Numid = rs.getInt("newid");
            for (int i = 0; i < size; i++) {
                pstm = con.prepareStatement("INSERT INTO character_teleport SET id=?,num_id=?,speed_id=?, char_id=?, name=?, locx=?, locy=?, mapid=?,randomX=?,randomY=?,item_obj_id=?");
                pstm.setInt(1, Numid++);
                pstm.setInt(2, i);
                pstm.setInt(3, -1);
                pstm.setInt(4, 0);
                pstm.setString(5, pc._speedbookmarks.get(i).getName());
                pstm.setInt(6, pc._speedbookmarks.get(i).getLocX());
                pstm.setInt(7, pc._speedbookmarks.get(i).getLocY());
                pstm.setInt(8, pc._speedbookmarks.get(i).getMapId());
                pstm.setInt(9, pc._speedbookmarks.get(i).getRandomX());
                pstm.setInt(10, pc._speedbookmarks.get(i).getRandomY());
                pstm.setInt(11, item.getId());
                pstm.execute();
            }
        } catch (SQLException e) {
           // _log.log(Level.WARNING, "WriteBookmark 例外が発生。", e);
        } finally {
            SQLUtil.close(rs, pstm, con);
        }
    }
}