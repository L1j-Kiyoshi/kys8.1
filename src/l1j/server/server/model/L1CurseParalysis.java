/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.model;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.SingleTask;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_ServerMessage;

public class L1CurseParalysis extends L1Paralysis {
    private final L1Character _target;
    private final int _delay;
    private final int _time;

    private SingleTask _timer;

    private class ParalysisDelayTimer extends SingleTask {
        @Override
        public void execute() {

            if (isActive()) {
                _target.killSkillEffectTimer(L1SkillId.STATUS_CURSE_PARALYZING);

                if (_target instanceof L1PcInstance) {
                    L1PcInstance player = (L1PcInstance) _target;

                    if (player.isDead() == false) {
                        player.sendPackets(new S_Paralysis(1, true));
                        player.sendPackets(new S_PacketBox(
                                S_PacketBox.POSION_ICON, player, 2, _time / 1000), true);
                    }
                    _timer = new ParalysisTimer();
                    _target.setSkillEffect(L1SkillId.STATUS_CURSE_PARALYZED, 0);
                    _target.setParalyzed(true);

                    GeneralThreadPool.getInstance().schedule(_timer, _time);
                }
            }
        }
    }

    private class ParalysisTimer extends SingleTask {
        @Override
        public void execute() {

            _target.killSkillEffectTimer(L1SkillId.STATUS_CURSE_PARALYZED);

            if (_target instanceof L1PcInstance) {
                L1PcInstance player = (L1PcInstance) _target;
                if (!player.isDead()) {
                    player.sendPackets(new S_PacketBox(
                            S_PacketBox.POSION_ICON, player, 2, 0), true);
                    player.sendPackets(new S_Paralysis(1, false));

                }
            }
            _timer = null;
            _target.setParalyzed(false);
            cure();
        }
    }

    private L1CurseParalysis(L1Character cha, int delay, int time) {
        _target = cha;
        _delay = delay;
        _time = time;

        curse();
    }

    private void curse() {
        if (_target instanceof L1PcInstance) {
            L1PcInstance player = (L1PcInstance) _target;
            player.sendPackets(new S_ServerMessage(212));
        }

        _target.setPoisonEffect(2);

        _timer = new ParalysisDelayTimer();
        _target.setSkillEffect(L1SkillId.STATUS_CURSE_PARALYZING, 0);

        GeneralThreadPool.getInstance().schedule(_timer, _delay);
    }

    public static boolean curse(L1Character cha, int delay, int time) {
        if (!(cha instanceof L1PcInstance || cha instanceof L1MonsterInstance)) {
            return false;
        }
        if (cha.hasSkillEffect(L1SkillId.STATUS_CURSE_PARALYZING)
                || cha.hasSkillEffect(L1SkillId.STATUS_CURSE_PARALYZED)) {
            return false;
        }

        cha.setParalaysis(new L1CurseParalysis(cha, delay, time));
        return true;
    }

    @Override
    public int getEffectId() {
        return 2;
    }

    @Override
    public void cure() {
        if (_timer != null) {
            _timer.cancel();

            if (_timer instanceof ParalysisTimer) {
                if (!_timer.isExecuted()) {
                    _timer.execute();
                }
            } else {
                _target.killSkillEffectTimer(L1SkillId.STATUS_POISON_PARALYZING);
            }

            _timer = null;
        }
        if (_target instanceof L1PcInstance) {
            L1PcInstance player = (L1PcInstance) _target;
            player.sendPackets(new S_PacketBox(
                    S_PacketBox.POSION_ICON, player, 0, 0), true);
        }
        _target.setPoisonEffect(0);
        _target.setParalaysis(null);
    }
}
