package l1j.server.server.command.executor;

import java.util.List;

import l1j.server.server.command.L1Commands;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Command;

public class L1CommandHelp implements L1CommandExecutor {

    private L1CommandHelp() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1CommandHelp();
    }

    private String join(List<L1Command> list, String with) {
        StringBuilder result = new StringBuilder();
        for (L1Command cmd : list) {
            if (result.length() > 0) {
                result.append(with);
            }
            result.append(cmd.getName());
        }
        return result.toString();
    }

    @Override
    public void execute(L1PcInstance pc, String cmdName, String arg) {
        List<L1Command> list = L1Commands.availableCommandList(pc.getAccessLevel());
        pc.sendPackets(new S_SystemMessage(join(list, ", ")));
    }
}
