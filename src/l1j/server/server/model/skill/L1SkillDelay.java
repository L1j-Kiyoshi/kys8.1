package l1j.server.server.model.skill;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.L1Character;

public class L1SkillDelay {

    private L1SkillDelay() {
    }

    static class SkillDelayTimer implements Runnable {
        private L1Character _cha;

        public SkillDelayTimer(L1Character cha, int time) {
            _cha = cha;
        }

        @Override
        public void run() {
            stopDelayTimer();
        }

        public void stopDelayTimer() {
            _cha.setSkillDelay(false);
        }
    }

    public static void onSkillUse(L1Character cha, int time) {
        cha.setSkillDelay(true);
        GeneralThreadPool.getInstance().schedule(new SkillDelayTimer(cha, time), time);
    }

}
