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
package l1j.server.server.model.poison;

import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.RepeatTask;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_DoActionGFX;

public class L1DamagePoison extends L1Poison {

	private RepeatTask _timer;
	private final L1Character _attacker;
	private final L1Character _target;
	private final int _damageSpan;
	private final int _damage;
	private boolean _tomahawk;

	private L1DamagePoison(L1Character attacker, L1Character cha, int damageSpan, int damage, boolean tomahawk) {
		_attacker = attacker;
		_target = cha;
		_damageSpan = damageSpan;
		_damage = damage;
		_tomahawk = tomahawk;

		doInfection();
	}

	private class NormalPoisonTimer extends RepeatTask {

		NormalPoisonTimer() {
			super(_damageSpan);
		}

		@Override
		public void execute() {
			L1PcInstance player = null;
			L1MonsterInstance mob = null;
			do {
				if (!_target.hasSkillEffect(L1SkillId.STATUS_POISON) && !_target.hasSkillEffect(L1SkillId.STATUS_TOMAHAWK)) {
					cure();
					break;
				}

				if (_target.hasSkillEffect(L1SkillId.ICE_LANCE) || _target.hasSkillEffect(L1SkillId.MOB_COCA)
						|| _target.hasSkillEffect(L1SkillId.MOB_BASILL) || _target.hasSkillEffect(L1SkillId.EARTH_BIND)) {
					cure();
					break;
				}

				if (_target instanceof L1PcInstance) {
					player = (L1PcInstance) _target;
					player.receiveDamage(_attacker, _damage);
					if (_target.hasSkillEffect(L1SkillId.STATUS_TOMAHAWK)) {
						player.sendPackets(new S_DoActionGFX(player.getId(), ActionCodes.ACTION_Damage));
						player.broadcastPacket(new S_DoActionGFX(player.getId(), ActionCodes.ACTION_Damage));
					}
					if (player.isDead()) {
						cure();
						break;
					}
				} else if (_target instanceof L1MonsterInstance) {
					mob = (L1MonsterInstance) _target;
					mob.receiveDamage(_attacker, _damage);
					if (mob.hasSkillEffect(L1SkillId.STATUS_TOMAHAWK)) {
						mob.broadcastPacket(new S_DoActionGFX(mob.getId(), ActionCodes.ACTION_Damage));
					}
					if (mob.isDead()) {
						cancel();
						return;
					}
				}
			} while (false);
		}
	}

	boolean isDamageTarget(L1Character cha) {
		return (cha instanceof L1PcInstance) || (cha instanceof L1MonsterInstance);
	}

	private void doInfection() {
		if(_tomahawk) {
			_target.setSkillEffect(L1SkillId.STATUS_TOMAHAWK, 7000);
		} else {
			_target.setSkillEffect(L1SkillId.STATUS_POISON, 30000);
			_target.setPoisonEffect(1);
		}
		if (isDamageTarget(_target)) {
			_timer = new NormalPoisonTimer();
			GeneralThreadPool.getInstance().execute(_timer);
		}
	}

	public static boolean doInfection(L1Character attacker, L1Character cha, int damageSpan, int damage, boolean tomahawk) {
		if (!isValidTarget(cha)) {
			return false;
		}

		cha.setPoison(new L1DamagePoison(attacker, cha, damageSpan, damage, tomahawk));
		return true;
	}

	@Override
	public int getEffectId() {
		return 1;
	}

	@Override
	public void cure() {
		if (_timer != null) {
			_timer.cancel();
		}
		if(_tomahawk) {
			_target.killSkillEffectTimer(L1SkillId.STATUS_TOMAHAWK);
		} else {
			_target.setPoisonEffect(0);
			_target.killSkillEffectTimer(L1SkillId.STATUS_POISON);
		}
		_target.setPoison(null);
	}
}
