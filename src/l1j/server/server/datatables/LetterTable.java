package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;

public class LetterTable {
    private static Logger _log = Logger.getLogger(LetterTable.class.getName());
    private volatile static LetterTable uniqueInstance = null;

    public LetterTable() {
    }

    public static LetterTable getInstance() {
        if (uniqueInstance == null) {
            synchronized (LetterTable.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new LetterTable();
                }
            }
        }
        return uniqueInstance;
    }

    // テンプレートID一覧
    // 16：キャラクターが存在しない
    // 32：荷物が多すぎる
    // 48：血盟が存在しない
    // 64：※内容が表示されない（白文字）
    // 80：※内容が表示されない（黒字）
    // 96：※内容が表示されない（黒字）
    // 112：おめでとうございます。 ％nあなたが参加したオークションは、最終的な価格％0アデナの価格で落札された。
    // 128：あなたが提示された金額よりも少し高価な金額を提示した方が現われたので、残念ながら入札に失敗しました。
    // 144：あなたが参加したオークションは成功しましたが、現在の家を所有することができる状態にありません。
    // 160：あなたが所有していた家が最終価格％1アデナで落札された。
    // 176：あなたが適用されたオークションは、オークション期間内に提示した金額以上でのお支払いを表明することが表示されていなかったため、最終的には削除されました。
    // 192：あなたが適用されたオークションは、オークション期間内に提示した金額以上でのお支払いを表明することが表示されていなかったため、最終的には削除されました。
    // 208：あなたの血盟が所有している家は、本領注意領地に帰属しているため、今後利用したい場合は、この方の税金を払わなければなりません。
    // 224：あなたは、あなたの家に課せられた税金％0アデナをまだ納入していません。
    // 240：あなたは、最終的にあなたの家に課せられた税金％0を納入していなかったので、警告ようにあなたの家の所有権を剥奪します。

    public int getLetterCount(String name, int type) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        int cnt = 0;

        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT count(*) as cnt FROM letter WHERE receiver=? AND template_id = ? order by date");
            pstm.setString(1, name);
            pstm.setInt(2, type);
            rs = pstm.executeQuery();
            if (rs.next()) {
                cnt = rs.getInt(1);
            }

        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return cnt;
    }

    public int writeLetter(int code, Timestamp dTime, String sender, String receiver, int templateId, String subject, String content) {
        Connection con = null;
        PreparedStatement pstm1 = null;
        ResultSet rs = null;
        PreparedStatement pstm2 = null;
        int itemObjectId = 0;

        try {
            con = L1DatabaseFactory.getInstance().getConnection();

            pstm1 = con.prepareStatement(" SELECT Max(item_object_id)+1 as cnt FROM letter ORDER BY item_object_id ");
            rs = pstm1.executeQuery();
            if (rs.next()) {
                itemObjectId = rs.getInt("cnt");
            }

            pstm2 = con.prepareStatement("INSERT INTO letter SET item_object_id=?, code=?, sender=?, receiver=?, date=?, template_id=?, subject=?, content=?, isCheck=? ");
            pstm2.setInt(1, itemObjectId);
            pstm2.setInt(2, code);
            pstm2.setString(3, sender);
            pstm2.setString(4, receiver);
            pstm2.setTimestamp(5, dTime);
            pstm2.setInt(6, templateId);
            pstm2.setString(7, subject);
            pstm2.setString(8, content);
            pstm2.setInt(9, 0);
            pstm2.execute();
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm1);
            SQLUtil.close(pstm2);
            SQLUtil.close(con);
        }
        return itemObjectId;
    }

    public void writeLetter(int itemObjectId, int code, String sender, String receiver, String date, int templateId, byte[] subject, byte[] content) {
        Connection con = null;
        PreparedStatement pstm1 = null;
        ResultSet rs = null;
        PreparedStatement pstm2 = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm1 = con.prepareStatement("SELECT * FROM letter ORDER BY item_object_id");
            rs = pstm1.executeQuery();
            pstm2 = con.prepareStatement("INSERT INTO letter SET item_object_id=?, code=?, sender=?, receiver=?, date=?, template_id=?, subject=?, content=?");
            pstm2.setInt(1, itemObjectId);
            pstm2.setInt(2, code);
            pstm2.setString(3, sender);
            pstm2.setString(4, receiver);
            pstm2.setString(5, date);
            pstm2.setInt(6, templateId);
            pstm2.setBytes(7, subject);
            pstm2.setBytes(8, content);
            pstm2.execute();
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm1);
            SQLUtil.close(pstm2);
            SQLUtil.close(con);
        }
    }

    public void writeLetter(int itemObjectId, int code, String sender, String receiver, String date, int templateId, String subject, String content) {
        Connection con = null;
        PreparedStatement pstm1 = null;
        ResultSet rs = null;
        PreparedStatement pstm2 = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm1 = con.prepareStatement("SELECT * FROM letter ORDER BY item_object_id");
            rs = pstm1.executeQuery();
            pstm2 = con.prepareStatement("INSERT INTO letter SET item_object_id=?, code=?, sender=?, receiver=?, date=?, template_id=?, subject=?, content=?");
            pstm2.setInt(1, itemObjectId);
            pstm2.setInt(2, code);
            pstm2.setString(3, sender);
            pstm2.setString(4, receiver);
            pstm2.setString(5, date);
            pstm2.setInt(6, templateId);
            pstm2.setString(7, subject);
            pstm2.setString(8, content);
            pstm2.execute();
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm1);
            SQLUtil.close(pstm2);
            SQLUtil.close(con);
        }
    }

    public void deleteLetter(int itemObjectId) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("DELETE FROM letter WHERE item_object_id=?");
            pstm.setInt(1, itemObjectId);
            pstm.execute();
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void SaveLetter(int id, int letterType) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("UPDATE letter SET template_id = ? WHERE item_object_id=?");
            pstm.setInt(1, letterType);
            pstm.setInt(2, id);
            pstm.execute();
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void writeLetter(int code, String dTime, String sender, String receiver, int templateId, String subject, String content) {
        Connection con = null;
        PreparedStatement pstm1 = null;
        ResultSet rs = null;
        PreparedStatement pstm2 = null;
        int itemObjectId = 0;

        try {
            con = L1DatabaseFactory.getInstance().getConnection();

            pstm1 = con.prepareStatement(" SELECT Max(item_object_id)+1 as cnt FROM letter ORDER BY item_object_id ");
            rs = pstm1.executeQuery();
            if (rs.next()) {
                itemObjectId = rs.getInt("cnt");
            }

            pstm2 = con.prepareStatement("INSERT INTO letter SET item_object_id=?, code=?, sender=?, receiver=?, date=?, template_id=?, subject=?, content=?, isCheck=? ");
            pstm2.setInt(1, itemObjectId);
            pstm2.setInt(2, code);
            pstm2.setString(3, sender);
            pstm2.setString(4, receiver);
            pstm2.setString(5, dTime);
            pstm2.setInt(6, templateId);
            pstm2.setString(7, subject);
            pstm2.setString(8, content);
            pstm2.setInt(9, 0);
            pstm2.execute();
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm1);
            SQLUtil.close(pstm2);
            SQLUtil.close(con);
        }
    }

    public void CheckLetter(int id) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("UPDATE letter SET isCheck = 1 WHERE item_object_id=?");
            pstm.setInt(1, id);
            pstm.execute();
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }
}
