package l1j.server.server.command.executor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.GameClient;
import l1j.server.server.datatables.IpTable;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.SQLUtil;

public class L1GlobalBan implements L1CommandExecutor {
    private static Logger _log = Logger.getLogger(L1QueryCharacter.class.getName());

    public static L1CommandExecutor getInstance() {
        return new L1GlobalBan();
    }

    @Override
    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            L1PcInstance target = L1World.getInstance().getPlayer(arg);

            if (target != null) {
                GameClient client = target.getNetConnection();

                if (client == null) {
                    pc.sendPackets(new S_SystemMessage("接続中でないキャラクターに対して照会することができません。"));
                    return;
                }

                String targetIp = client.getIp();
                String cClass = getCClass(targetIp);

                Collection<L1PcInstance> pcs = L1World.getInstance().getAllPlayers();

                for (L1PcInstance otherPc : pcs) {

                    if (otherPc.getNetConnection() != null) {
                        String otherPcIp = otherPc.getNetConnection().getIp();

                        if (cClass.equals(getCClass(otherPcIp))) {
                            otherPc.sendPackets(new S_Disconnect());
                            pc.sendPackets(new S_SystemMessage("接続中のキャラクター[" + otherPc.getName() + "]を追放しました。"));
                        }
                    }
                }

                banGlobalIp(targetIp);
                banGlobalAccounts(targetIp);

            } else {
                pc.sendPackets(new S_SystemMessage("そのような名前のキャラクターは、ワールド内には存在しません。"));
            }

        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(cmdName + "[キャラクター名]で入力してください。"));
        }
    }

    private void banGlobalIp(String ip) {
        String cClass = getCClass(ip);

        for (int i = 1; i <= 255; ++i) {
            String newIp = cClass + "." + i;

            IpTable iptable = IpTable.getInstance();

            if (!iptable.isBannedIp(newIp)) {
                iptable.banIp(newIp);
            }
        }
    }

    private void banGlobalAccounts(String ip) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {

            conn = L1DatabaseFactory.getInstance().getConnection();
            pstm = conn.prepareStatement("UPDATE accounts SET banned = 1 WHERE ip like CONCAT(?, '.%')");
            pstm.setString(1, getCClass(ip));
            pstm.execute();
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(conn);
        }

    }

    static private String getCClass(String ip) {
        return ip.substring(0, ip.lastIndexOf('.'));
    }
}
