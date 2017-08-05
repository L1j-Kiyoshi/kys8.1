package l1j.server.server.serverpackets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.utils.SQLUtil;

public class S_AuctionBoard extends ServerBasePacket {

    private static Logger _log = Logger.getLogger(S_AuctionBoard.class.getName());
    private static final String S_AUCTIONBOARD = "[S] S_AuctionBoard";
    private byte[] _byte = null;

    public S_AuctionBoard(L1NpcInstance board) {
        buildPacket(board);
    }

    private void buildPacket(L1NpcInstance board) {
        ArrayList<Integer> houseList = new ArrayList<Integer>();
        int houseId = 0;
        int count = 0;
        int[] id = null;
        String[] name = null;
        int[] area = null;
        int[] month = null;
        int[] day = null;
        int[] price = null;
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;

        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT * FROM board_auction");
            rs = pstm.executeQuery();
            while (rs.next()) {
                houseId = rs.getInt(1);
                if (board.getX() == 33421 && board.getY() == 32823) { // オークション掲示板（ギラン）
                    if (houseId >= 262145 && houseId <= 262189) {
                        houseList.add(houseId);
                        count++;
                    }
                } else if (board.getX() == 33585 && board.getY() == 33235) { // オークション掲示板（Heine）
                    if (houseId >= 327681 && houseId <= 327691) {
                        houseList.add(houseId);
                        count++;
                    }
                } else if (board.getX() == 33959 && board.getY() == 33253) { // オークション掲示板（エデン）
                    if (houseId >= 458753 && houseId <= 458819) {
                        houseList.add(houseId);
                        count++;
                    }
                } else if (board.getX() == 32611 && board.getY() == 32775) { // オークション掲示板（グルーディオ）
                    if (houseId >= 524289 && houseId <= 524294) {
                        houseList.add(houseId);
                        count++;
                    }
                }
            }
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }

        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            id = new int[count];
            name = new String[count];
            area = new int[count];
            month = new int[count];
            day = new int[count];
            price = new int[count];
            Calendar cal = null;
            for (int i = 0; i < count; ++i) {
                pstm = con.prepareStatement("SELECT * FROM board_auction WHERE house_id=?");
                houseId = houseList.get(i);
                pstm.setInt(1, houseId);
                rs = pstm.executeQuery();
                while (rs.next()) {
                    id[i] = rs.getInt(1);
                    name[i] = rs.getString(2);
                    area[i] = rs.getInt(3);
                    cal = timestampToCalendar((Timestamp) rs.getObject(4));
                    month[i] = cal.get(Calendar.MONTH) + 1;
                    day[i] = cal.get(Calendar.DATE);
                    price[i] = rs.getInt(5);
                }
                SQLUtil.close(rs);
                SQLUtil.close(pstm);
            }
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }

        writeC(Opcodes.S_AGIT_LIST);
        writeD(board.getId());
        writeH(count); // レコード数
        for (int i = 0; i < count; ++i) {
            writeD(id[i]); // アジトの番号
            writeS(name[i]); // アジトの名前
            writeH(area[i]); // アジトの広さ
            writeC(month[i]); // 締め切り月
            writeC(day[i]); // 締め切り
            writeD(price[i]); // 現在の入札価格
        }
    }

    private Calendar timestampToCalendar(Timestamp ts) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ts.getTime());
        return cal;
    }

    @Override
    public byte[] getContent() {
        if (_byte == null) {
            _byte = getBytes();
        }
        return _byte;
    }

    @Override
    public String getType() {
        return S_AUCTIONBOARD;
    }
}
