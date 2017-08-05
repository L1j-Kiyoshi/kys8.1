/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.command.executor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1CheckPassword implements L1CommandExecutor {
    @SuppressWarnings("unused")
    private static Logger _log = Logger.getLogger(L1CheckPassword.class.getName());

    private L1CheckPassword() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1CheckPassword();
    }

    @Override
    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            StringTokenizer stringtokenizer = new StringTokenizer(arg);
            String target = stringtokenizer.nextToken();

            Connection con = null;
            PreparedStatement pstm = null;
            PreparedStatement pstm2 = null;
            ResultSet rs = null;
            ResultSet rs2 = null;
            String login = null;
            String pass = null;
            String lastactive = null;
            String quiz = null;
            String ip = null;
            String host = null;


            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT account_name FROM characters WHERE char_name=?");
            pstm.setString(1, target);
            rs = pstm.executeQuery();

            if (rs.next()) {
                login = rs.getString(1);
            }
            pstm2 = con.prepareStatement("SELECT password, quiz, lastactive, ip, host FROM accounts WHERE login= '" + login + "'");
            rs2 = pstm2.executeQuery();

            if (rs2.next()) {
                pass = rs2.getString(1);
                quiz = rs2.getString(2);
                lastactive = rs2.getString(3);
                ip = rs2.getString(4);
                host = rs2.getString(5);
            }
            pc.sendPackets(new S_SystemMessage("キャラクター名：" + target + "\nアカウント: " + login + "\n非番: " + pass + "\nクイズ: " + quiz + "\n最近アクセス: " + lastactive + "\n接続アイピー: " + ip + "\n生成アイピー: " + host));

            con.close();
            pstm.close();
            pstm2.close();
            rs.close();
            rs2.close();
        } catch (Exception exception) {
            pc.sendPackets(new S_SystemMessage(cmdName + "[キャラクター名]を入力してください。"));
        }
    }
}
