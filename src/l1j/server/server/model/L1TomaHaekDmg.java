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

import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_SkillSound;

public class L1TomaHaekDmg {

	private Thread _timer;
	private final L1Character _attacker;
	private final L1Character _target;
	private final int _damage;

	public L1TomaHaekDmg(L1Character attacker, L1Character cha, int damage) {
		_attacker = attacker;
		_target = cha;
		_damage = damage;

		doInfection();
	}

	private class NormalPoisonTimer extends Thread {
		@Override
		public void run() {
			L1PcInstance player = null;
			L1MonsterInstance mob = null;
			while (true) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					break;
				}

				if (!_target.hasSkillEffect(L1SkillId.TOMAHAWK)) {
					break;
				}
				if (_target instanceof L1PcInstance) {
					player = (L1PcInstance) _target;
					player.receiveDamage(_attacker, _damage);

					player.sendPackets(new S_SkillSound(player.getId(), 12617));
					Broadcaster.broadcastPacket(player, new S_SkillSound(player.getId(), 12617));
					player.sendPackets(new S_DoActionGFX(player.getId(), ActionCodes.ACTION_Damage));
					Broadcaster.broadcastPacket(player, new S_DoActionGFX(player.getId(), ActionCodes.ACTION_Damage));

					if (player.isDead()) {
						break;
					}
				} else if (_target instanceof L1MonsterInstance) {
					mob = (L1MonsterInstance) _target;
					mob.receiveDamage(_attacker, _damage);

					Broadcaster.broadcastPacket(mob, new S_SkillSound(mob.getId(), 12617));
					Broadcaster.broadcastPacket(mob, new S_DoActionGFX(mob.getId(), ActionCodes.ACTION_Damage));
					if (mob.isDead()) {
						return;
					}
				}
			}
			cure();
		}
	}

	boolean isDamageTarget(L1Character cha) {
		return (cha instanceof L1PcInstance) || (cha instanceof L1MonsterInstance);
	}

	private void doInfection() {
		_target.setSkillEffect(L1SkillId.TOMAHAWK, 6000);
		if (isDamageTarget(_target)) {
			_timer = new NormalPoisonTimer();
			GeneralThreadPool.getInstance().execute(_timer);
		}
	}

	public void cure() {
		if (_timer != null) {
			_timer.interrupt();
		}

		_target.killSkillEffectTimer(L1SkillId.TOMAHAWK);

	}
}
