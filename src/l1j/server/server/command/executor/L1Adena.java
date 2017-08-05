package l1j.server.server.command.executor;

import java.util.StringTokenizer;

import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1Adena implements L1CommandExecutor {

    private L1Adena() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1Adena();
    }

    @Override
    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {

            StringTokenizer stringtokenizer = new StringTokenizer(arg);

            int count = Integer.parseInt(stringtokenizer.nextToken());
            L1ItemInstance adena = pc.getInventory().storeItem(L1ItemId.ADENA, count);

            if (adena != null) {
                pc.sendPackets(new S_SystemMessage((new StringBuilder()).append(count).append("アデナを生成しました。").toString()));
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage((new StringBuilder()).append("。アデナ[金額]に入力してください。").toString()));
        }
    }
}
