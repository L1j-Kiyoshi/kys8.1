package l1j.server.server.model;

import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.RepeatTask;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SkillSound; //## [A142] MP回復時エフェクト見えるように

public class HpRegenerationByDoll extends RepeatTask {
	private static Logger _log = Logger.getLogger(HpRegenerationByDoll.class.getName());

	private final L1PcInstance _pc;
	private final int _regenAmount;

	public HpRegenerationByDoll(L1PcInstance pc, int regenAmount, long interval) {
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
			regenHp();
		} catch (Throwable e) {
			_log.log(Level.WARNING, e.getLocalizedMessage(), e);
		}
	}

	public void regenHp() {
		int newHp = _pc.getCurrentHp() + _regenAmount;
		if (newHp < 0) {
			newHp = 0;
		}
		_pc.setCurrentHp(newHp);
		_pc.sendPackets(new S_SkillSound(_pc.getId(), 1608));
		_pc.broadcastPacket(new S_SkillSound(_pc.getId(), 1608));

	}
}
