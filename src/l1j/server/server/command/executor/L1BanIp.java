package l1j.server.server.command.executor;

import java.util.StringTokenizer;

import l1j.server.server.datatables.IpTable;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1BanIp implements L1CommandExecutor {

    private L1BanIp() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1BanIp();
    }

    @Override
    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {

            StringTokenizer stringtokenizer = new StringTokenizer(arg);// IPを指定
            String s1 = stringtokenizer.nextToken();// add/delを指定（していなくてもOK）
            String s2 = null;

            try {
                s2 = stringtokenizer.nextToken();
            } catch (Exception e) {
            }

            IpTable iptable = IpTable.getInstance();
            boolean isBanned = iptable.isBannedIp(s1);

            for (L1PcInstance tg : L1World.getInstance().getAllPlayers()) {
                if (tg.getNetConnection() != null && s1.equals(tg.getNetConnection().getIp())) {
                    String msg = new StringBuilder().append("IP : ").append(s1).append("で接続中のプレイヤー：").append(tg.getName()).toString();
                    pc.sendPackets(new S_SystemMessage(msg));
                }
            }

            if ("add".equals(s2) && !isBanned) {
                iptable.banIp(s1); // BANリストにIPアドレスを加える
                String msg = new StringBuilder().append("IP : ").append(s1).append("をBAN IPに登録しました。").toString();
                pc.sendPackets(new S_SystemMessage(msg));
            } else if ("delete".equals(s2) && isBanned) {
                if (iptable.liftBanIp(s1)) { // BANリストからIPアドレスを削除する
                    String msg = new StringBuilder().append("IP : ").append(s1).append("をBAN IPから削除しました。").toString();
                    pc.sendPackets(new S_SystemMessage(msg));
                }
            } else { // BANの確認
                if (isBanned) {
                    String msg = new StringBuilder().append("IP : ").append(s1).append("はBAN IPに登録されています。").toString();
                    pc.sendPackets(new S_SystemMessage(msg));
                } else {
                    String msg = new StringBuilder().append("IP : ").append(s1).append("はBAN IPに登録されていません。").toString();
                    pc.sendPackets(new S_SystemMessage(msg));
                }
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(cmdName + "[ip][add|delete]と入力してください。"));
        }
    }
}
