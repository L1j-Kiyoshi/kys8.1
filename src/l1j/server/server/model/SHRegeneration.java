package l1j.server.server.model;

import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.RepeatTask;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;

public class SHRegeneration extends RepeatTask {
    private static Logger _log = Logger.getLogger(SHRegeneration.class
            .getName());

    private final L1PcInstance _pc;

    public SHRegeneration(L1PcInstance pc, long interval) {
        super(interval);
        _pc = pc;
    }

    @Override
    public void execute() {
        try {
            if (_pc.isDead()) {
                return;
            }
            regenItem();
        } catch (Throwable e) {
            _log.log(Level.WARNING, e.getLocalizedMessage(), e);
        }
    }

    public void regenItem() {
        _pc.getInventory().storeItem(410002, 1);
        _pc.sendPackets(new S_ServerMessage(403, "$6379"));
    }
}
