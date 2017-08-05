package l1j.server.server.serverpackets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.StringTokenizer;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.GameClient;
import l1j.server.server.Opcodes;
import l1j.server.server.utils.SQLUtil;

public class S_Notice extends ServerBasePacket {

    private byte[] _data = null;

    private StringBuffer sb = new StringBuffer();

    public S_Notice(String account, GameClient ct) {
        String date = getDate(account);
        ResultSet r = null;
        Connection c = null;
        PreparedStatement p = null;
        try {
            c = L1DatabaseFactory.getInstance().getConnection();
            p = c.prepareStatement("select * from notice where id > '" + date
                    + "'");
            r = p.executeQuery();
            String sTemp = "";
            String sDate = "";
            if (r.next()) {
                sDate = r.getString("id");
                sTemp = r.getString("message");
                StringTokenizer s = new StringTokenizer(sTemp, "^");
                while (s.hasMoreElements()) {
                    sb.append(s.nextToken()).append("\n");
                }
                writeC(Opcodes.S_NEWS); // opcode
                writeS(sb.toString()); // Data
                sb.setLength(0);
                UpDate(account, sDate);
            }
        } catch (Exception e) {
            System.out.println("notice Error : " + e.getMessage());
        } finally {
            try {
                SQLUtil.close(r);
                SQLUtil.close(p);
                SQLUtil.close(c);
            } catch (Exception e) {
            }
        }
    }

    public S_Notice(String s) {
        writeC(Opcodes.S_NEWS);
        writeS(s);
    }

    public S_Notice() { //これを追加
    }


    /**
     * アカウント名に読むべきお知らせ本数リターン
     *
     * @param account アカウント名
     * @return お知らせ本数
     */
    public static int NoticeCount(String account) {
        int Count = 0;
        ResultSet r = null;
        Connection c = null;
        PreparedStatement p = null;
        try {
            c = L1DatabaseFactory.getInstance().getConnection();
            p = c
                    .prepareStatement("select count(id) as cnt from notice where id > (select notice from accounts where login='"
                            + account + "')");
            r = p.executeQuery();
            if (r.next())
                Count = r.getInt("cnt");
        } catch (Exception e) {
            Count = 0;
        } finally {
            SQLUtil.close(r);
            SQLUtil.close(p);
            SQLUtil.close(c);
        }
        return Count;
    }

    /**
     * アカウント表でアカウントを相手に最終通知日を持って来る。
     *
     * @param client
     * @return 最終的な通知を読んだ日（yyyy-MM-dd）
     */
    private String getDate(String account) {
        String sTemp = "";
        ResultSet r = null;
        Connection c = null;
        PreparedStatement p = null;
        try {
            c = L1DatabaseFactory.getInstance().getConnection();
            p = c.prepareStatement("select * from accounts where login='"
                    + account + "'");
            r = p.executeQuery();
            if (r.next())
                sTemp = r.getString("notice");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                SQLUtil.close(r);
                SQLUtil.close(p);
                SQLUtil.close(c);
            } catch (Exception e) {
            }
        }
        return sTemp != "" ? sTemp : "";
    }

    /**
     * アカウント名を対象に、最終的な公知の日付を現在の日付に更新
     *
     * @param account
     */
    public void UpDate(String account, String date) {

        Connection c = null;
        PreparedStatement p = null;
        try {
            c = L1DatabaseFactory.getInstance().getConnection();
            p = c.prepareStatement("update accounts set notice=? where login='"
                    + account + "'");
            p.setString(1, date);
            p.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                SQLUtil.close(p);
                SQLUtil.close(c);
            } catch (Exception e) {
            }
        }
    }

    @Override
    public byte[] getContent() {
        if (_data == null) {
            _data = _bao.toByteArray();
        }
        return _data;
    }
}