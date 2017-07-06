package l1j.server.server.model;

import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Party;

public class L1PartyRefresh extends TimerTask {
	private static Logger _log = Logger.getLogger(L1PartyRefresh.class.getName());

	private final L1PcInstance _pc;

	public L1PartyRefresh(L1PcInstance pc) {
		_pc = pc;
	}

	public void run() {
		try {
			if (_pc.isDead() || _pc.getParty() == null) {
				return;
			}
			rp();
		} catch (Throwable e) {
			_log.log(Level.WARNING, e.getLocalizedMessage(), e);
		}
	}

	public void rp() {
		_pc.sendPackets(new S_Party(0x6e, _pc));
	}

}
