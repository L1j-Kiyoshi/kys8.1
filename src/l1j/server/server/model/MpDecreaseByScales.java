package l1j.server.server.model;

import static l1j.server.server.model.skill.L1SkillId.SCALES_EARTH_DRAGON;
import static l1j.server.server.model.skill.L1SkillId.SCALES_FIRE_DRAGON;
import static l1j.server.server.model.skill.L1SkillId.SCALES_WATER_DRAGON;

import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.RepeatTask;
import l1j.server.server.model.Instance.L1PcInstance;

public class MpDecreaseByScales extends RepeatTask {
    private static Logger _log = Logger.getLogger(MpDecreaseByScales.class
            .getName());

    private final L1PcInstance _pc;

    public MpDecreaseByScales(L1PcInstance pc, long interval) {
        super(interval);
        _pc = pc;
    }

    @Override
    public void execute() {
        try {
            if (_pc.isDead() || _pc.getCurrentMp() < 6) {
                killSkill();
                return;
            }
            regenMp();
        } catch (Throwable e) {
            _log.log(Level.WARNING, e.getLocalizedMessage(), e);
        }
    }

    public void regenMp() {
        int newMp = _pc.getCurrentMp() - 6;

        _pc.setCurrentMp(newMp);
    }

    public void killSkill() {
        if (_pc.hasSkillEffect(SCALES_EARTH_DRAGON)) {
            _pc.removeSkillEffect(SCALES_EARTH_DRAGON);
        } else if (_pc.hasSkillEffect(SCALES_WATER_DRAGON)) {
            _pc.removeSkillEffect(SCALES_WATER_DRAGON);
        } else if (_pc.hasSkillEffect(SCALES_FIRE_DRAGON)) {
            _pc.removeSkillEffect(SCALES_FIRE_DRAGON);
        }
    }

}
