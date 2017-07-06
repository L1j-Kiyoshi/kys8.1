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
package l1j.server.server.model.Instance;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;

import l1j.server.Config;
import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.datatables.DropTable;
import l1j.server.server.datatables.NPCTalkDataTable;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1NpcTalkData;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_ChangeHeading;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_Karma;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.utils.CalcExp;

public class L1GuardianInstance extends L1NpcInstance {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger _log = Logger.getLogger(L1GuardianInstance.class
			.getName());

	private Random _random = new Random(System.nanoTime());
	private L1GuardianInstance _npc = this;

	/**
	 * @param template
	 */
	public L1GuardianInstance(L1Npc template) {
		super(template);
		_restCallCount = new AtomicInteger(0);
	}

	@Override
	public void searchTarget() {
		L1PcInstance targetPlayer = null;

		for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(this)) {
			if (pc == null || pc.getCurrentHp() <= 0 || pc.isDead() || pc.isGm() || pc.isGhost()) {
				continue;
			}
			if (!pc.isInvisble() || getNpcTemplate().is_agrocoi()) { // 인비지체크
				if (!pc.isElf()) { // 요정이아니면
					targetPlayer = pc;
					wideBroadcastPacket(new S_NpcChatPacket(this, "$804", 2)); // 그대여. 목숨이 아까우면 빨리 이곳을 떠날지어다. 이곳은 그대같은 자가 더럽히지 못할 신성한 곳이다.
					break;
				}
			}
		}
		if (targetPlayer != null) {
			_hateList.add(targetPlayer, 0);
			_target = targetPlayer;
		}
	}

	// 링크의 설정
	@Override
	public void setLink(L1Character cha) {
		if (cha != null && _hateList.isEmpty()) { // 타겟이 없는 경우만 추가
			_hateList.add(cha, 0);
			checkTarget();
		}
	}

	@Override
	public void onNpcAI() {
		if (isAiRunning()) {
			return;
		}
		setActived(false);
		startAI();
	}

	@Override
	public void onAction(L1PcInstance player) {
		if (this == null || player == null)
			return;
		if (player.getType() == 2 && player.getCurrentWeapon() == 0
				&& player.isElf()) {
			L1Attack attack = new L1Attack(player, this);

			if (attack.calcHit()) {
				if (getNpcTemplate().get_npcId() == 70848) { // 엔트
					int chance = _random.nextInt(100) + 1;
					if (chance <= 10) {
						player.getInventory().storeItem(40506, 1);
						player.sendPackets(new S_ServerMessage(143, "$755",
								"$794")); // \f1%0이%1를 주었습니다.
					} else if (chance <= 60 && chance > 10) {
						player.getInventory().storeItem(40507, 1);
						player.sendPackets(new S_ServerMessage(143, "$755",
								"$763")); // \f1%0이%1를 주었습니다.
					} else if (chance <= 70 && chance > 60) {
						player.getInventory().storeItem(40505, 1);
						player.sendPackets(new S_ServerMessage(143, "$755",
								"$770")); // \f1%0이%1를 주었습니다.
					}
				}
				if (getNpcTemplate().get_npcId() == 70850) { // 빵
					int chance = _random.nextInt(100) + 1;
					if (chance <= 30) {
						player.getInventory().storeItem(40519, 5);
						player.sendPackets(new S_ServerMessage(143, "$753",
								"$760" + " (" + 5 + ")")); // \f1%0이%1를 주었습니다.
					}
				}
				if (getNpcTemplate().get_npcId() == 70846) { 
					int chance = _random.nextInt(100) + 1;
					if (chance <= 30) {
						player.getInventory().storeItem(40503, 1);
						player.sendPackets(new S_ServerMessage(143, "$752",
								"$769")); // \f1%0이%1를 주었습니다.
					}
				}
				attack.calcDamage();
				attack.calcStaffOfMana();
				attack.addPcPoisonAttack(player, this);
			}
			attack.action();
			attack.commit();
		} else if (getCurrentHp() > 0 && !isDead()) {
			L1Attack attack = new L1Attack(player, this);
			if (attack.calcHit()) {
				attack.calcDamage();
				attack.calcStaffOfMana();
				attack.addPcPoisonAttack(player, this);
			}
			attack.action();
			attack.commit();
		}
	}

	@SuppressWarnings("unused")
	@Override
	public void onTalkAction(L1PcInstance player) {
		if (player == null || this == null)
			return;
		int objid = getId();
		L1NpcTalkData talking = NPCTalkDataTable.getInstance().getTemplate(
				getNpcTemplate().get_npcId());
		L1Object object = L1World.getInstance().findObject(getId());
		if (object == null)
			return;
		L1NpcInstance target = (L1NpcInstance) object;
		String htmlid = null;
		String[] htmldata = null;

		if (talking != null) {
			int pcx = player.getX(); // PC의 X좌표
			int pcy = player.getY(); // PC의 Y좌표
			int npcx = target.getX(); // NPC의 X좌표
			int npcy = target.getY(); // NPC의 Y좌표

			if (pcx == npcx && pcy < npcy) {
				setHeading(0);
			} else if (pcx > npcx && pcy < npcy) {
				setHeading(1);
			} else if (pcx > npcx && pcy == npcy) {
				setHeading(2);
			} else if (pcx > npcx && pcy > npcy) {
				setHeading(3);
			} else if (pcx == npcx && pcy > npcy) {
				setHeading(4);
			} else if (pcx < npcx && pcy > npcy) {
				setHeading(5);
			} else if (pcx < npcx && pcy == npcy) {
				setHeading(6);
			} else if (pcx < npcx && pcy < npcy) {
				setHeading(7);
			}
			broadcastPacket(new S_ChangeHeading(this));

			if (htmlid != null) { 
				if (htmldata != null) { 
					player.sendPackets(new S_NPCTalkReturn(objid, htmlid,
							htmldata));
				} else {
					player.sendPackets(new S_NPCTalkReturn(objid, htmlid));
				}
			} else {
				if (player.getLawful() < -1000) {
					player.sendPackets(new S_NPCTalkReturn(talking, objid, 2));
				} else {
					player.sendPackets(new S_NPCTalkReturn(talking, objid, 1));
				}
			}

			// 얘 좀 구린듯. interlockedIncrement하는데 compare&Swap이라니. 어차피 intel 기준 lock xadd 한번만 콜해주면 되는데.-_- 
			if( _restCallCount.getAndIncrement() == 0 )
			{
				setRest(true);
			}

			GeneralThreadPool.getInstance().schedule(new RestMonitor(), REST_MILLISEC);
		}
	}

	@Override
	public void receiveDamage(L1Character attacker, int damage) { 
		if (this == null || attacker == null)
			return;
		if (attacker instanceof L1PcInstance && damage > 0) {
			L1PcInstance pc = (L1PcInstance) attacker;
			if (pc.getType() == 2 && 
					pc.getCurrentWeapon() == 0) {
			} else {
				if (getCurrentHp() > 0 && !isDead()) {
					if (damage >= 0) {
						setHate(attacker, damage);
					}
					if (damage > 0) {
						if(hasSkillEffect(L1SkillId.FOG_OF_SLEEPING)){
							removeSkillEffect(L1SkillId.FOG_OF_SLEEPING);
						}else if (hasSkillEffect(L1SkillId.PHANTASM)){
							removeSkillEffect(L1SkillId.PHANTASM);
						}
					}
					onNpcAI();
					serchLink(pc, getNpcTemplate().get_family());
					if (damage > 0) {
						pc.setPetTarget(this);
					}

					int newHp = getCurrentHp() - damage;
					if (newHp <= 0 && !isDead()) {
						setCurrentHp(0);
						setDead(true);
						setStatus(ActionCodes.ACTION_Die);
						_lastattacker = attacker;
						Death death = new Death();
						GeneralThreadPool.getInstance().execute(death);
					}
					if (newHp > 0) {
						setCurrentHp(newHp);
					}
				} else if (!isDead()) {
					setDead(true);
					setStatus(ActionCodes.ACTION_Die);
					_lastattacker = attacker;
					Death death = new Death();
					GeneralThreadPool.getInstance().execute(death);
				}
			}
		}
	}

	@Override
	public void setCurrentHp(int i) {
		super.setCurrentHp(i);
		
		if (getMaxHp() > getCurrentHp()) {
			startHpRegeneration();
		}
	}

	@Override
	public void setCurrentMp(int i) {
		super.setCurrentMp(i);

		if (getMaxMp() > getCurrentMp()) {
			startMpRegeneration();
		}
	}

	private L1Character _lastattacker;

	class Death implements Runnable {
		L1Character lastAttacker = _lastattacker;

		public void run() {
			setDeathProcessing(true);
			setCurrentHp(0);
			setDead(true);
			setStatus(ActionCodes.ACTION_Die);
			int targetobjid = getId();
			getMap().setPassable(getLocation(), true);
			broadcastPacket(new S_DoActionGFX(targetobjid,
					ActionCodes.ACTION_Die));

			L1PcInstance player = null;
			if (lastAttacker instanceof L1PcInstance) {
				player = (L1PcInstance) lastAttacker;
			} else if (lastAttacker instanceof L1PetInstance) {
				player = (L1PcInstance) ((L1PetInstance) lastAttacker)
						.getMaster();
			} else if (lastAttacker instanceof L1SummonInstance) {
				player = (L1PcInstance) ((L1SummonInstance) lastAttacker)
						.getMaster();
			}
			if (player != null) {
				ArrayList<L1Character> targetList = _hateList
						.toTargetArrayList();
				ArrayList<Integer> hateList = _hateList.toHateArrayList();
				int exp = getExp();
				CalcExp.calcExp(player, targetobjid, targetList, hateList, exp);

				ArrayList<L1Character> dropTargetList = _dropHateList
						.toTargetArrayList();
				ArrayList<Integer> dropHateList = _dropHateList
						.toHateArrayList();
				try {
					DropTable.getInstance().dropShare(_npc,
							dropTargetList, dropHateList, player);
				} catch (Exception e) {
					_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
				}
				// 업은 급소를 찌른 플레이어로 설정.애완동물 or사몬으로 넘어뜨렸을 경우도 들어간다.
				player.addKarma((int) (getKarma() * Config.RATE_KARMA));
				player.sendPackets( new S_Karma(player) );
			}
			setDeathProcessing(false);

			setKarma(0);
			setLawful(0);
			setExp(0);
			allTargetClear();

			startDeleteTimer();
		}
	}

	@Override
	public void onFinalAction(L1PcInstance player, String action) {
	}

	public void doFinalAction(L1PcInstance player) {
	}

	private static final long REST_MILLISEC = 10000;

	private AtomicInteger _restCallCount;

	public class RestMonitor implements Runnable {
		@Override
		public void run() {
			if( _restCallCount.decrementAndGet() == 0 )
			{
				setRest(false);
			}
		}
	}
}
