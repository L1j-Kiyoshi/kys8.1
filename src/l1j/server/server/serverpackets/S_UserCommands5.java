/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.serverpackets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.utils.SQLUtil;

public class S_UserCommands5 extends ServerBasePacket {

    private static final String S_UserCommands5 = "[C] S_UserCommands5";

    private static Logger _log = Logger.getLogger(S_UserCommands5.class.getName());

    private byte[] _byte = null;

    private int j = 0;

    static String[] name;

    public S_UserCommands5(L1PcInstance pc, int number) {
        name = new String[10];
        buildPacket(pc, number);
    }

    private void buildPacket(L1PcInstance pc, int number) {
        String date = time();
        String type = null;
        String title = null;
        writeC(Opcodes.S_BOARD_READ);
        writeD(number);
        writeS("ランキング管理者");
        switch (number) {
            case 1:
                title = "デスランキング";
                break;
        }
        writeS(title);
        writeS(date);
        switch (pc.getType()) {
            case 0:
                type = "君主";
                break;
            case 1:
                type = "ナイト";
                break;
            case 2:
                type = "エルフ";
                break;
            case 3:
                type = "ウィザード";
                break;
            case 4:
                type = "ダークエルフ";
                break;
            case 5:
                type = "ドラゴンナイト";
                break;
            case 6:
                type = "イリュージョニスト";
                break;
            case 7:
                type = "ウォリアー";
                break;
        }
        int p = Rank(pc, number);
        if (number == 9) {
            writeS("\n\r\n\r\n\r" + pc.getName() + "さんの全ランキング：" + p + "位"
                    + "\n\r\n\r\n\r" + type + "クラスランキング：" + j + "位"
                    + "\n\r\n\r\n\r" + "        ");
        } else {
            writeS("\n\r" + "   1位 " + name[0] + "\n\r" +
                    "   2位 " + name[1] + "\n\r" +
                    "   3位 " + name[2] + "\n\r" +
                    "   4位 " + name[3] + "\n\r" +
                    "   5位 " + name[4] + "\n\r" +
                    "   6位 " + name[5] + "\n\r" +
                    "   7位 " + name[6] + "\n\r" +
                    "   8位 " + name[7] + "\n\r" +
                    "   9位 " + name[8] + "\n\r" +
                    "  10位 " + name[9] + "\n\r" +
                    "             ");
        }
    }

    private int Rank(L1PcInstance pc, int number) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        int objid = pc.getId();
        int i = 0;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            switch (number) {
                case 1:
                    pstm = con
                            .prepareStatement("SELECT char_name FROM characters WHERE AccessLevel = 0 order by PC_Death desc limit 10");
                    break;
                default:
                    pstm = con
                            .prepareStatement("SELECT char_name FROM characters WHERE AccessLevel = 0 order by PC_Death desc limit 10");
                    break;
            }

            rs = pstm.executeQuery();
            if (number == 9) {
                while (rs.next()) {
                    i++;
                    if (objid == rs.getInt(1))
                        break;
                }
                String sql = "SELECT objid FROM characters WHERE Type = ";
                sql = (new StringBuilder(String.valueOf(sql))).append(
                        pc.getType()).toString();
                sql = (new StringBuilder(String.valueOf(sql))).append(
                        " And AccessLevel = 0 order by PC_Death desc").toString();
                pstm = con.prepareStatement(sql);
                rs = pstm.executeQuery();
                j = 0;
                while (rs.next()) {
                    j++;
                    if (objid == rs.getInt(1))
                        break;
                }
            } else if (number == 1) { // 追加部分です
                while (rs.next()) {
                    name[i] = rs.getString(1);
                    //Kills[i] = rs.getString(2);
                    i++;
                }
            } else {
                while (rs.next()) {
                    name[i] = rs.getString(1);
                    i++;
                }

                // レコードがない場合、または5よりも小さいとき
                while (i < 10) {
                    name[i] = "無し。";
                    i++;
                }
            }
        } catch (SQLException e) {
            _log.log(Level.SEVERE, "S_UserCommands5[]Error", e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }

        return i;
    }

    private static String time() {
        TimeZone tz = TimeZone.getTimeZone(Config.TIME_ZONE);
        Calendar cal = Calendar.getInstance(tz);
        int year = cal.get(Calendar.YEAR) - 2000;
        String year2;
        if (year < 10) {
            year2 = "0" + year;
        } else {
            year2 = Integer.toString(year);
        }
        int Month = cal.get(Calendar.MONTH) + 1;
        String Month2 = null;
        if (Month < 10) {
            Month2 = "0" + Month;
        } else {
            Month2 = Integer.toString(Month);
        }
        int date = cal.get(Calendar.DATE);
        String date2 = null;
        if (date < 10) {
            date2 = "0" + date;
        } else {
            date2 = Integer.toString(date);
        }
        return year2 + "/" + Month2 + "/" + date2;
    }

    @Override
    public byte[] getContent() {
        if (_byte == null) {
            _byte = getBytes();
        }
        return _byte;
    }

    public String getType() {
        return S_UserCommands5;
    }

}
