package l1j.server.server.command.executor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1Recall implements L1CommandExecutor {
    @SuppressWarnings("unused")
    private static Logger _log = Logger.getLogger(L1Recall.class.getName());

    private L1Recall() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1Recall();
    }

    @Override
    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            Collection<L1PcInstance> targets = null;
            if (arg.equalsIgnoreCase("summon")) {
                targets = L1World.getInstance().getAllPlayers();
            } else {
                targets = new ArrayList<L1PcInstance>();
                L1PcInstance tg = L1World.getInstance().getPlayer(arg);
                if (tg == null) {
                    pc.sendPackets(new S_SystemMessage("そのようなキャラクターはありません。"));
                    return;
                }
                targets.add(tg);
            }

            for (L1PcInstance target : targets) {
                if (target.isPrivateShop()) {
                    pc.sendPackets(new S_SystemMessage(target.getName() + "様は店モードです。"));
                    return;
                }
                if (target.isAutoClanjoin()) {
                    pc.sendPackets(new S_SystemMessage(target.getName() + "様は、自動登録中です。"));
                    return;
                }

                L1Location loc = L1Teleport.summonTeleport(pc, 1);
                target.dx = loc.getX();
                target.dy = loc.getY();
                target.dm = (short) loc.getMapId();
                target.dh = target.getHeading();
                target.setTelType(7);
                new L1Teleport().teleport(target, target.dx, target.dy, target.dm, target.dh, true);
                pc.sendPackets(new S_SystemMessage("通知：\\\\ aG" + target.getName() + "を召喚しました。"));
                target.sendPackets(new S_SystemMessage("\\aGゲームマスターが \\aAあなた\\aGを召喚しました。"));
            }


        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(cmdName + "[。召喚：キャラクター名]で入力してください。]"));
        }
    }
}
