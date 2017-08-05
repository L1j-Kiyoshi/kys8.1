/**
 * 無人エンピシ店起動コマンド
 */
package l1j.server.server.command.executor;

import java.util.logging.Logger;

import l1j.server.server.NpcShopSystem;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1NpcShopSwitch implements L1CommandExecutor {
    @SuppressWarnings("unused")
    private static Logger _log = Logger.getLogger(L1NpcShopSwitch.class.getName());

    private L1NpcShopSwitch() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1NpcShopSwitch();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            boolean power = NpcShopSystem.getInstance().isPower();
            if (arg.equalsIgnoreCase("on")) {
                if (power) {
                    pc.sendPackets(new S_SystemMessage("既に実行中です。"));
                    return;
                } else {
                    NpcShopSystem.getInstance().npcShopStart();
                }
            } else if (arg.equalsIgnoreCase("off")) {
                if (!power) {
                    pc.sendPackets(new S_SystemMessage("実行されていない。"));
                    return;
                } else {
                    NpcShopSystem.getInstance().npcShopStop();
                }
            } else {
                pc.sendPackets(new S_SystemMessage("。英字店のオン/オフ"));
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage("。英字店メソッドエラー"));
        }
    }
}

	