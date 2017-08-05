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
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.Account;
import l1j.server.server.datatables.IpTable;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.SQLUtil;

public class L1PowerKick implements L1CommandExecutor {
    @SuppressWarnings("unused")
    private static Logger _log = Logger.getLogger(L1PowerKick.class.getName());

    private L1PowerKick() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1PowerKick();
    }

    @Override
    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            L1PcInstance target = L1World.getInstance().getPlayer(arg);
            IpTable iptable = IpTable.getInstance();
            if (target != null) {
                Account.ban(target.getAccountName()); // アカウントをBANさせる。
                iptable.banIp(target.getNetConnection().getIp()); // BANリストにIPアドレスを追加します。
                pc.sendPackets(new S_SystemMessage((new StringBuilder()).append(target.getName()).append("を永久追放しました。")
                        .toString()));
                target.sendPackets(new S_Disconnect());
            } else {
                String name = loadCharacter(arg);
                if (name != null) {
                    Account.ban(name);
                    String nc = Account.checkIP(name);
                    if (nc != null)
                        iptable.banIp(nc);
                    pc.sendPackets(new S_SystemMessage(name + "アカウント差し押さえました。"));
                }
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(cmdName + "[キャラクター名]で入力してください。"));
        }
    }

    private String loadCharacter(String charName) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String name = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT * FROM characters WHERE char_name=?");
            pstm.setString(1, charName);

            rs = pstm.executeQuery();

            if (rs.next()) {
                name = rs.getString("account_name");
            }

        } catch (Exception e) {
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return name;
    }
}
