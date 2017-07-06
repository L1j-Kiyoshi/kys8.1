package l1j.server.server.model;

import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.RepeatTask;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SkillSound;

public class MpRegenerationByDoll extends RepeatTask {
	private static Logger _log = Logger.getLogger(MpRegenerationByDoll.class
			.getName());

	private final L1PcInstance _pc;
	private final int _regenAmount; 

	public MpRegenerationByDoll(L1PcInstance pc, int regenAmount, long interval) {
		super(interval);
		_pc = pc;
		_regenAmount = regenAmount;
	}

	@Override
	public void execute() {
		try {
			if (_pc.isDead()) {
				return;
			}
			regenMp();
		} catch (Throwable e) {
			_log.log(Level.WARNING, e.getLocalizedMessage(), e);
		}
	}

	public void regenMp() {
		int newMp = _pc.getCurrentMp() + _regenAmount;

		_pc.setCurrentMp(newMp);
		_pc.sendPackets(new S_SkillSound(_pc.getId(), 6321)); 
		_pc.broadcastPacket(new S_SkillSound(_pc.getId(), 6321));

	}

}
