package l1j.server.server.command.executor;

import java.util.StringTokenizer;
import java.util.logging.Logger;

import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1CookingIcon implements L1CommandExecutor {
    @SuppressWarnings("unused")
    private static Logger _log = Logger.getLogger(L1CookingIcon.class.getName());

    private L1CookingIcon() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1CookingIcon();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            StringTokenizer st = new StringTokenizer(arg);
            int _sprid = Integer.parseInt(st.nextToken(), 10);
            int count = Integer.parseInt(st.nextToken(), 10);
            for (int i = 0; i < count; i++) {
                try {
                    Thread.sleep(2000);
                    int num = _sprid + i;
                    //new S_PacketBox(53, cookingType, time));
                    pc.sendPackets(new S_PacketBox(S_PacketBox.ICON_COOKING, _sprid + i, 10));//無制限のパケット
                    pc.sendPackets(new S_SystemMessage("料理のアイコン：" + num + "回。"));
                } catch (Exception exception) {
                    break;
                }
            }
        } catch (Exception exception) {
            pc.sendPackets(new S_SystemMessage(cmdName
                    + "[id] [出現させることができ】で入力してください。"));
        }
    }
}