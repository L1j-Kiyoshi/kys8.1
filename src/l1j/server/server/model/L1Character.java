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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import l1j.server.GameSystem.AStar.World;
import l1j.server.server.model.Instance.L1CastleGuardInstance;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1FollowerInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1SupportInstance;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.model.poison.L1Poison;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillTimer;
import l1j.server.server.serverpackets.S_Poison;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.ServerBasePacket;
import l1j.server.server.types.Point;
import l1j.server.server.utils.IntRange;

//Referenced classes of package l1j.server.server.model:
//L1Object, Die, L1PcInstance, L1MonsterInstance,
//L1World, ActionFailed

public class L1Character extends L1Object {
	private static final long serialVersionUID = 1L;
	// キャラクターの基本
	private String _name;
	private String _title;
	private int _level;
	private int _exp;
	private int _currentHp;
	private int _currentMp;
	private int _maxHp = 0;
	private int _trueMaxHp = 0;
	private int _maxMp = 0;
	private int _trueMaxMp = 0;
	private int _lawful;
	private int _karma;

	// 状態
	private int _heading; // ●方向0.左上1.上2偶像3.右4.右下5.し6左下7左
	private int _moveSpeed; // ●スピード0.通常1ヘイスト2スロー
	private int _braveSpeed; // ●ブレイブ状態0.通常1ブレイブ
	private int _tempCharGfx; // ●ベースのグラフィックスID
	private int _gfxid; // ●グラフィックスID
	private L1Poison _poison = null;
	private boolean _paralyzed;
	private boolean _sleeped;
	private L1Paralysis _paralysis;
	private boolean _isDead;

	protected Light light = null; // キャラクターの周りの光
	private MoveState moveState; // 移動速度、眺める方向
	protected Ability ability = null; // 能力値
	protected Resistance resistance = null; // 抵抗（魔、火、水、風、地、スタン、凍結、スリップ、石化）
	protected AC ac = null; // AC防御

	// 知らないだろ
	private boolean _isSkillDelay = false;
	private int _addAttrKind;
	private int _status;

	// ダメージ
	private int _dmgup = 0;
	private int _trueDmgup = 0;
	private int _bowDmgup = 0;
	private int _trueBowDmgup = 0;
	private int _hitup = 0;
	private int _trueHitup = 0;
	private int _bowHitup = 0;
	private int _trueBowHitup = 0;

	private int _sp = 0; // sp

	// その他
	private static Random _rnd = new Random(System.nanoTime());
	private final Map<Integer, L1NpcInstance> _petlist = new HashMap<Integer, L1NpcInstance>();
	private final Map<Integer, L1SupportInstance> _supportlist = new HashMap<Integer, L1SupportInstance>();
	private final Map<Integer, L1DollInstance> _dolllist = new HashMap<Integer, L1DollInstance>();
	private final Map<Integer, L1SkillTimer> _skillEffect = new HashMap<Integer, L1SkillTimer>();
	private final Map<Integer, L1ItemDelay.ItemDelayTimer> _itemdelay = new HashMap<Integer, L1ItemDelay.ItemDelayTimer>();
	private final Map<Integer, L1FollowerInstance> _followerlist = new HashMap<Integer, L1FollowerInstance>();

	// ■■■■■■■■■■ L1PcInstanceに移動するプロパティ ■■■■■■■■■■
	private final List<L1Object> _knownObjects = new CopyOnWriteArrayList<L1Object>();
	private final List<L1PcInstance> _knownPlayer = new CopyOnWriteArrayList<L1PcInstance>();

	public L1Character() {
		_level = 1;
		ability = new Ability(this);
		resistance = new Resistance(this);
		ac = new AC();
		moveState = new MoveState();
		light = new Light(this);
		
	}

	/**
	 * キャラクターを復活させる。
	 * 
	 * @param hp
	 *            復活後のHP
	 */
	public void resurrect(int hp) {
		if (!isDead())
			return;
		if (hp <= 0)
			hp = 1;

		setCurrentHp(hp);
		setDead(false);
		setStatus(0);
		L1PolyMorph.undoPoly(this);

		for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
			pc.sendPackets(new S_RemoveObject(this));
			pc.removeKnownObject(this);
			pc.updateObject();
		}
	}

	/**
	 * キャラクターの現在のHPを返す。
	 * 
	 * @return 現在のHP
	 */
	public int getCurrentHp() {
		return _currentHp;
	}

	/**
	 * キャラクターのHPを設定する。
	 * 
	 * @param i
	 *            キャラクターの新しいHP
	 */
	public void setCurrentHp(int i) {
		if (i >= getMaxHp()) {
			i = getMaxHp();
		}
		if (i < 0)
			i = 0;

		_currentHp = i;
	}

	/**
	 *キャラクターの現在のMPを返す。
	 * 
	 * @return 現在のMP
	 */
	public int getCurrentMp() {
		return _currentMp;
	}

	/**
	 * キャラクターのMPを設定する。
	 * 
	 * @param i
	 *            キャラクターの新しいMP
	 */
	public void setCurrentMp(int i) {
		if (i >= getMaxMp()) {
			i = getMaxMp();
		}
		if (i < 0)
			i = 0;

		_currentMp = i;
	}

	/**
	 * キャラクターの睡眠状態を返す。
	 * 
	 * @return 睡眠状態を示す値。睡眠状態であればtrue。
	 */
	public boolean isSleeped() {
		return _sleeped;
	}

	/**
	 * キャラクターの睡眠状態を設定する。
	 * 
	 * @param sleeped
	 *            睡眠状態を示す値。睡眠状態であればtrue。
	 */
	public void setSleeped(boolean sleeped) {
		_sleeped = sleeped;
	}

	/**
	 * キャラクターの麻痺状態を返す。
	 * 
	 * @return 麻痺状態を示す値。麻痺状態であればtrue。
	 */
	public boolean isParalyzed() {
		return _paralyzed;
	}

	/**
	 * キャラクターの麻痺状態を返す。
	 * 
	 * @return 麻痺状態を示す値。麻痺状態であればtrue。
	 */
	public void setParalyzed(boolean paralyzed) {
		_paralyzed = paralyzed;
	}

	public L1Paralysis getParalysis() {
		return _paralysis;
	}

	public void setParalaysis(L1Paralysis p) {
		_paralysis = p;
	}

	public void cureParalaysis() {
		if (_paralysis != null) {
			_paralysis.cure();
		}
	}
	/**
	 * キャラクターの可視範囲にあるプレーヤーでは、パケットを送信する。
	 * 
	 * @param packet
	 *            送信するパケットを示すServerBasePacketオブジェクト。
	 */
	public void broadcastPacket(ServerBasePacket packet) {
		for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(this)) {
			pc.sendPackets(packet);
		}
	}

	public void broadcastPacket(ServerBasePacket packet, L1Character target) {
		for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(this)) {
			pc.sendPackets(packet);
		}
	}

	public void broadcastPacket(ServerBasePacket packet, L1Character[] target) {
		for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(this)) {
			pc.sendPackets(packet);
		}
	}
	/**
	 * キャラクターの可視範囲にあるプレーヤーでは、パケットを送信する。ただし、ターゲットの画面には送信しない。
	 * 
	 * @param packet
	 *            送信するパケットを示すServerBasePacketオブジェクト。
	 */
	public void broadcastPacketExceptTargetSight(ServerBasePacket packet, L1Character target) {
		for (L1PcInstance pc : L1World.getInstance().getVisiblePlayerExceptTargetSight(this, target)) {
			if (pc.knownsObject(this)) {
				pc.sendPackets(packet);
			}
		}
	}

	/**
	 * キャラクターの50マス以内にいるプレイヤーに、パケットを送信する。
	 * 
	 * @param packet
	 *            送信するパケットを示すServerBasePacketオブジェクト。
	 */
	public void wideBroadcastPacket(ServerBasePacket packet) {
		for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(this, 50)) {
			pc.sendPackets(packet);
		}
	}

	/**
	 * キャラクターの正面の座標を返す。
	 * 
	 * @return 正面の座標
	 */
	public int[] getFrontLoc() {
		int[] loc = new int[2];
		int x = getX();
		int y = getY();
		int heading = getHeading();
		switch (heading) {
		case 0: {
			y--;
		}
			break;
		case 1: {
			x++;
			y--;
		}
			break;
		case 2: {
			x++;
		}
			break;
		case 3: {
			x++;
			y++;
		}
			break;
		case 4: {
			y++;
		}
			break;
		case 5: {
			x--;
			y++;
		}
			break;
		case 6: {
			x--;
		}
			break;
		case 7: {
			x--;
			y--;
		}
			break;
		default:
			break;
		}
		loc[0] = x;
		loc[1] = y;
		return loc;
	}

	
	/**
	 * 指定された座標に接する方向を返す。
	 * 
	 * @param tx
	 *            座標のX値
	 * @param ty
	 *            座標のY値
	 * @return 指定された座標に接する方向
	 */
	public int targetDirection(int tx, int ty) {
		float dis_x = Math.abs(getX() - tx); // X方向のターゲットまでの距離
		float dis_y = Math.abs(getY() - ty); //Y方向のターゲットまでの距離
		float dis = Math.max(dis_x, dis_y); // ターゲットまでの距離

		if (dis == 0)
			return getHeading();

		int avg_x = (int) Math.floor((dis_x / dis) + 0.59f); // 上下左右が少し優先的なラウンド
		int avg_y = (int) Math.floor((dis_y / dis) + 0.59f); // 上下左右が少し優先的なラウンド

		int dir_x = 0;
		int dir_y = 0;

		if (getX() < tx)
			dir_x = 1;
		if (getX() > tx)
			dir_x = -1;

		if (getY() < ty)
			dir_y = 1;
		if (getY() > ty)
			dir_y = -1;

		if (avg_x == 0)
			dir_x = 0;
		if (avg_y == 0)
			dir_y = 0;

		if (dir_x == 1 && dir_y == -1)
			return 1; // 上
		if (dir_x == 1 && dir_y == 0)
			return 2; // アイドル
		if (dir_x == 1 && dir_y == 1)
			return 3; // 右
		if (dir_x == 0 && dir_y == 1)
			return 4; // ウーハー
		if (dir_x == -1 && dir_y == 1)
			return 5; // し
		if (dir_x == -1 && dir_y == 0)
			return 6; // 左下
		if (dir_x == -1 && dir_y == -1)
			return 7; // 左
		if (dir_x == 0 && dir_y == -1)
			return 0; // 左上

		return getHeading();
	}

	/**
	 * 指定された座標までの直線上に、障害物が存在*しない*を返す。
	 * 
	 * @param tx
	 *            座標のX値
	 * @param ty
	 *            座標のY値
	 * @return 障害物がなければtrue、あるfalseを返す。
	 */
	public boolean glanceCheck(int tx, int ty) {
		L1Map map = getMap();
		int chx = getX();
		int chy = getY();
		for (int i = 0; i < 15; i++) {
			if ((chx == tx && chy == ty)
					|| (chx == tx && chy + 1 == ty)// 0 0 0 1
					|| (chx == tx && chy - 1 == ty)
					|| (chx + 1 == tx && chy == ty)// 0 -1 1 0
					|| (chx + 1 == tx && chy + 1 == ty)
					|| (chx + 1 == tx && chy - 1 == ty)// 1 1 1 -1
					|| (chx - 1 == tx && chy == ty)
					|| (chx - 1 == tx && chy + 1 == ty)// -1 0 -1 1
					|| (chx - 1 == tx && chy - 1 == ty)) { // -1 -1
				break;
			}

			if (!map.isArrowPassable(chx, chy, targetDirection(tx, ty)))
				return false;

			if (chx < tx && chy == ty) {
				chx++;
			} else if (chx > tx && chy == ty) {
				chx--;
			} else if (chx == tx && chy < ty) {
				chy++;
			} else if (chx == tx && chy > ty) {
				chy--;
			} else if (chx < tx && chy < ty) {
				chx++;
				chy++;
			} else if (chx < tx && chy > ty) {
				chx++;
				chy--;
			} else if (chx > tx && chy < ty) {
				chx--;
				chy++;
			} else if (chx > tx && chy > ty) {
				chx--;
				chy--;
			}
		}

		return true;
	}
	/**
	 * 該当する座標に方向を転換するために使用。
	 */
	public static int calcheading(int myx, int myy, int tx, int ty) {
		if (tx > myx && ty > myy) {
			return 3;
		} else if (tx < myx && ty < myy) {
			return 7;
		} else if (tx > myx && ty == myy) {
			return 2;
		} else if (tx < myx && ty == myy) {
			return 6;
		} else if (tx == myx && ty < myy) {
			return 0;
		} else if (tx == myx && ty > myy) {
			return 4;
		} else if (tx < myx && ty > myy) {
			return 5;
		} else {
			return 1;
		}
	}

	public static int calcheading(L1Character o, int x, int y) {
		return calcheading(o.getX(), o.getY(), x, y);
	}

	/**
	 * 希望ターゲットに長距離攻撃と近距離攻撃が可能かどうかチェック
	 */
	static public boolean isAreaAttack(L1Character o, int tx, int ty, int tm) {
		L1Map map = o.getMap();
		int chx = o.getX();
		int chy = o.getY();
		if (o.getMapId() == tm && o.getX() == tx && o.getY() == ty) {
			return true;
		}
		for (int i = 0; i < 20; i++) {

			if ((chx == tx && chy + 1 == ty)// 0 0 0 1
					|| (chx == tx && chy - 1 == ty)
					|| (chx + 1 == tx && chy == ty)// 0 -1 1 0
					|| (chx + 1 == tx && chy + 1 == ty)
					|| (chx + 1 == tx && chy - 1 == ty)// 1 1 1 -1
					|| (chx - 1 == tx && chy == ty)
					|| (chx - 1 == tx && chy + 1 == ty)// -1 0 -1 1
					|| (chx - 1 == tx && chy - 1 == ty)) { // -1 -1
				// System.out.println(chx+" "+chy);
				if (!World.isThroughAttack(chx, chy, map.getId(),
						calcheading(chx, chy, tx, ty))) {
					// System.out.println("ああファック");
					return false;
				}

				break;
			}
			if (!World.isThroughAttack(chx, chy, map.getId(),calcheading(chx, chy, tx, ty))) {
				// System.out.println(chx+" "+chy);
				// System.out.println(o.getName()+"マクヒョトオ！");
				return false;
			}
			if (chx == tx && chy == ty) {
				break;
			}
			if (chx < tx && chy == ty) {
				chx++;
			} else if (chx > tx && chy == ty) {
				chx--;
			} else if (chx == tx && chy < ty) {
				chy++;
			} else if (chx == tx && chy > ty) {
				chy--;
			} else if (chx < tx && chy < ty) {
				chx++;
				chy++;
			} else if (chx < tx && chy > ty) {
				chx++;
				chy--;
			} else if (chx > tx && chy < ty) {
				chx--;
				chy++;
			} else if (chx > tx && chy > ty) {
				chx--;
				chy--;
			}
		}

		return true;
	}
	/**
	 * 指定された座標に攻撃可能かを返す。
	 * 
	 * @param x
	 *            座標のX値。
	 * @param y
	 *            座標のY値。
	 * @param range
	 *            攻撃可能な範囲（タイル数）
	 * @return 攻撃可能な場合はtrue、不可能であればfalse
	 */
	public boolean isAttackPosition(int x, int y, int range) {
		if (range >= 7) {// リモート武器（7以上の場合の傾きを考慮すると、画面のほかに出てくる）
			if (getLocation().getTileDistance(new Point(x, y)) > range)
				return false;
		} else {
			if (getLocation().getTileLineDistance(new Point(x, y)) > range)
				return false;
		}

		return glanceCheck(x, y);
	}
	public static boolean isAttackPosition(L1Character cha, int x, int y,
			int mapid, int range) {
		if (range >= 7) {// リモート武器（7以上の場合の傾きを考慮すると、画面のほかに出てくる）
			if (cha.getLocation().getTileDistance(new Point(x, y)) > range)
				return false;
		} else {
			if (cha.getLocation().getTileLineDistance(new Point(x, y)) > range) {
				return false;
			}
		}
		if (cha instanceof L1CastleGuardInstance) {
			L1CastleGuardInstance guard = (L1CastleGuardInstance) cha;
			if (guard.getNpcId() == 7000002 || guard.getNpcId() == 4707001) {
				return true;
			}
		}
		return isAreaAttack(cha, x, y, mapid);
	}
	/**
	 * キャラクターのリストを返す。
	 * 
	 * @return キャラクターのリストを示す、L1Inventoryオブジェクト。
	 */
	public L1Inventory getInventory() {
		return null;
	}

	/**
	 * キャラクターでは、新たにスキル効果を追加する。
	 * 
	 * @param skillId
	 *            追加効果のスキルIDです。
	 * @param timeMillis
	 *            追加効果の持続時間。無限の場合は0。
	 */
	private void addSkillEffect(int skillId, int timeMillis) {
		L1SkillTimer timer = null;
		if (0 < timeMillis) {
			timer = new L1SkillTimer(this, skillId, timeMillis);
			timer.begin();
		}
		_skillEffect.put(skillId, timer);
	}

	/**
	 * キャラクターでは、スキルの効果を設定する。 <br>
	 *重複するスキルがない場合は、新たにスキル効果を追加する。 <br>
	 * 重複するスキルがある場合は、残りの効果時間とパラメータの効果時間の長い方を優先して設定する。
	 * 
	 * @param skillId
	 *            設定する効果のスキルIDです。
	 * @param timeMillis
	 *            設定する効果の持続時間。無限の場合は0。
	 */
	public void setSkillEffect(int skillId, int timeMillis) {
		if (hasSkillEffect(skillId)) {
			int remainingTimeMills = getSkillEffectTimeSec(skillId) * 1000;

			if (remainingTimeMills >= 0
					&& (remainingTimeMills < timeMillis || timeMillis == 0)) {
				killSkillEffectTimer(skillId);
				addSkillEffect(skillId, timeMillis);
			}
		} else {
			addSkillEffect(skillId, timeMillis);
		}
	}

	/**
	 * キャラクターから、スキル効果を削除する。
	 * 
	 * @param skillId
	 *            削除する効果のスキルID
	 */
	public void removeSkillEffect(int skillId) {
		L1SkillTimer timer = _skillEffect.remove(skillId);
		if (timer != null) {
			timer.end();
		}
	}

	/**
	 * キャラクターから、スキル効果のタイマーを削除する。スキル効果は削除されない。
	 * 
	 * @param skillId
	 *            削除するタイマーのスキルID
	 */
	public void killSkillEffectTimer(int skillId) {
		L1SkillTimer timer = _skillEffect.remove(skillId);
		if (timer != null) {
			timer.kill();
		}
	}

	/**
	 * キャラクターから、すべてのスキル効果のタイマーを削除する。スキル効果は削除されない。
	 */
	public void clearSkillEffectTimer() {
		for (L1SkillTimer timer : _skillEffect.values()) {
			if (timer != null) {
				timer.kill();
			}
		}
		_skillEffect.clear();
	}

	/**
	 * キャラクターには、そのスキル効果がかかっているかどうか知らせる
	 * 
	 * @param skillId
	 *           スキルID
	 * @return 魔法の効果がある場合はtrue、なければfalse。
	 */
	public boolean hasSkillEffect(int skillId) {
		return _skillEffect.containsKey(skillId);
	}

	/**
	 * キャラクターのスキル効果の持続時間を返す。
	 * 
	 * @param skillId
	 *            調査する効果のスキルID
	 * @return スキル効果の残り時間（秒）。スキルがかからないかの効果時間が無限の場合、-1。
	 */
	public int getSkillEffectTimeSec(int skillId) {
		L1SkillTimer timer = _skillEffect.get(skillId);
		if (timer == null) {
			return -1;
		}
		return timer.getRemainingTime();
	}

	/**
	 * キャラクターでは、skill delay追加
	 * 
	 * @param flag
	 */
	public void setSkillDelay(boolean flag) {
		_isSkillDelay = flag;
	}

	/**
	 * キャラクターの毒状態を返す。
	 * 
	 * @return スキルディレイ中なのか。
	 */
	public boolean isSkillDelay() {
		return _isSkillDelay;
	}

	/**
	 * キャラクターでは、Item delay追加
	 * 
	 * @param delayId
	 *            アイテムの遅延ID。通常のアイテムであれば0、インビジ不正チークロック、バルログブラッディクロークであれば1。
	 * @param timer
	 *            遅延時間を示す、L1ItemDelay.ItemDelayTimerオブジェクト。
	 */
	public void addItemDelay(int delayId, L1ItemDelay.ItemDelayTimer timer) {
		_itemdelay.put(delayId, timer);
	}

	/**
	 * キャラクターから、Item delay削除
	 * 
	 * @param delayId
	 *           アイテムの遅延ID。通常のアイテムであれば0、インビジ不正チークロック、バルログブラッディクロークであれば1。
	 */
	public void removeItemDelay(int delayId) {
		_itemdelay.remove(delayId);
	}

	/**
	 * キャラクターでは、Item delayがあるか
	 * 
	 * @param delayId
	 *            調査するアイテム遅延IDです。通常のアイテムであれば0、インビジ不正チークロック、バルログブラッディクロークであれば1。
	 * @return アイテムの遅延がある場合はtrue、なければfalse。
	 */
	public boolean hasItemDelay(int delayId) {
		return _itemdelay.containsKey(delayId);
	}

	/**
	 * キャラクターのitem delay時間を示す、L1ItemDelay.ItemDelayTimerを返す。
	 * 
	 * @param delayId
	 *            調査するアイテム遅延IDです。通常のアイテムであれば0、インビジ不正チークロック、バルログブラッディクロークであれば1。
	 * @return アイテム遅延時間を示す、L1ItemDelay.ItemDelayTimer。
	 */
	public L1ItemDelay.ItemDelayTimer getItemDelayTimer(int delayId) {
		return _itemdelay.get(delayId);
	}

	/**
	 * キャラクターでは、pet、summon monster、tame monster、created zombieを加える。
	 * 
	 * @param npc
	 *            追加するNpcを示す、L1NpcInstanceオブジェクト。
	 */
	public void addPet(L1NpcInstance npc) {
		_petlist.put(npc.getId(), npc);
	}

	/**
	 * キャラクターから、pet、summon monster、tame monster、created zombieを削除する。
	 * 
	 * @param npc
	 *            削除するNpcを示す、L1NpcInstanceオブジェクト。
	 */
	public void removePet(L1NpcInstance npc) {
		_petlist.remove(npc.getId());
	}

	/**
	 *キャラクターのペットのリストを返す。
	 * 
	 * @return キャラクターのペットのリストを示す、HashMapオブジェクト。このオブジェクトのKeyはオブジェクトID、Valueは
	 *         L1NpcInstance.
	 */
	public Map<Integer, L1NpcInstance> getPetList() {
		return _petlist;
	}

	/**
	 * キャラクターdollを加える。
	 * 
	 * @param doll
	 *            追加するdollを示す、L1DollInstanceオブジェクト。
	 */
	public void addDoll(L1DollInstance doll) {
		_dolllist.put(doll.getId(), doll);
	}

	/**
	 * キャラクターからdoolを削除する。
	 * 
	 * @param doll
	 *            削除するdollを示す、L1DollInstanceオブジェクト。
	 */
	public void removeDoll(L1DollInstance doll) {
		_dolllist.remove(doll.getId());
	}

	/**
	 * キャラクターのdollリストを返す。
	 * 
	 * @return キャラクターのマジックドールのリストを示す、HashMapオブジェクト。このオブジェクトのKeyはオブジェクトID、Valueは
	 *         L1DollInstance.
	 */
	public ArrayList<L1DollInstance> getDollList() {
		ArrayList<L1DollInstance> doll = new ArrayList<>();
		synchronized (_dolllist) {
			doll.addAll(_dolllist.values());
		}
		return doll;
	}
	
	public int getDollListSize() {
		synchronized (_dolllist) {
			return _dolllist.size();
		}
	}

	/**
	 * キャラクターにつつくボプサウル加える。
	 * 
	 * @param doll
	 *            追加するdollを示す、L1DollInstanceオブジェクト。
	 */
	public void addSupport(L1SupportInstance support) {
		_supportlist.put(support.getId(), support);
	}

	/**
	 * キャラクターからつつくボプサウル削除する。
	 * 
	 * @param doll
	 *            削除するdollを示す、L1DollInstanceオブジェクト。
	 */
	public void removeSupport(L1SupportInstance support) {
		_supportlist.remove(support.getId());
	}

	/**
	 * キャラクターのつつくウィザードリストを返す。
	 * 
	 * @return キャラクターのマジックドールのリストを示す、HashMapオブジェクト。このオブジェクトのKeyはオブジェクトID、Valueは
	 *         L1DollInstance.
	 */
	public Map<Integer, L1SupportInstance> getSupportList() {
		return _supportlist;
	}

	/**
	 * キャラクターのイベントNPC（キャラクターを付きまとう）を追加する。
	 * 
	 * @param follower
	 *            追加するfollowerを示す、L1FollowerInstanceオブジェクト。
	 */
	public void addFollower(L1FollowerInstance follower) {
		_followerlist.put(follower.getId(), follower);
	}

	/**
	 * 文字からイベントNPC（キャラクターを付きまとう）を削除する。
	 * 
	 * @param follower
	 *            削除するfollowerを示す、L1FollowerInstanceオブジェクト。
	 */
	public void removeFollower(L1FollowerInstance follower) {
		_followerlist.remove(follower.getId());
	}

	/**
	 * キャラクターのイベントNPC（キャラクターを付きまとう）リストを返す。
	 * 
	 * @returnキャラクターの種子リストを示す、HashMapオブジェクト。このオブジェクトのKeyはオブジェクトID、Valueは
	 *         L1FollowerInstance.
	 */
	public Map<Integer, L1FollowerInstance> getFollowerList() {
		return _followerlist;
	}

	/**
	 * キャラクターでは、毒を加える。
	 * 
	 * @param poison
	 *            毒を示す、L1Poisonオブジェクト。
	 */
	public void setPoison(L1Poison poison) {
		_poison = poison;
	}

	/**
	 * キャラクターの毒を治療する。
	 */
	public void curePoison() {
		if (_poison == null) {
			return;
		}
		_poison.cure();
	}

	/**
	 * キャラクターの毒状態を返す。
	 * 
	 * @return キャラクターの毒を示す、L1Poisonオブジェクト。
	 */
	public L1Poison getPoison() {
		return _poison;
	}

	/**
	 * キャラクターの毒の効果を付加する
	 * 
	 * @param effectId
	 * @see S_Poison#S_Poison(int, int)
	 */
	public void setPoisonEffect(int effectId) {
		broadcastPacket(new S_Poison(getId(), effectId));
	}

	/**
	 * キャラクターが存在する座標が、どのゾーンに属しているかを返す。
	 * 
	 * @return 座標のゾーンを示す値。セーフティゾーンであれば1、コンバットゾーンであれば-1、ノーマルジョンの場合は0。
	 */

	public int getZoneType() {
		if (getMap().isSafetyZone(getLocation())) {
			/** バトルゾーン **/
			if (getMapId() == 5153) {
				return -1;
			} else {
				return 1;
			}
		} else if (getMap().isCombatZone(getLocation())) {
			return -1;
		} else { // ノーマルジョン
			return 0;
		}
	}

	public int getExp() {
		return _exp;
	}

	public void setExp(int exp) {
		_exp = exp;
	}

	/**
	 * 指定されたオブジェクトを、文字が認識しているかを返す。
	 * 
	 * @param obj
	 *            調査するオブジェクト。
	 * @return オブジェクトを文字が認識している場合はtrue、していない場合はfalse。自分自身については、falseを
	 *         返す。
	 */
	public boolean knownsObject(L1Object obj) {
		return _knownObjects.contains(obj);
	}

	/**
	 * キャラクターが認識しているすべてのオブジェクトを返す。
	 * 
	 * @return キャラクターが認識しているオブジェクトを表すList <L1Object>
	 */
	public List<L1Object> getKnownObjects() {
		return _knownObjects;
	}

	/**
	 * キャラクターが認識しているすべてのプレイヤーを返す。
	 * 
	 * @return キャラクターが認識しているオブジェクトを表すList <L1PcInstance>
	 */
	public List<L1PcInstance> getKnownPlayers() {
		return _knownPlayer;
	}

	/**
	 * キャラクターでは、新たに認識しているオブジェクトを追加します。
	 * 
	 * @param obj
	 *            新たに認識しているオブジェクト。
	 */
	public void addKnownObject(L1Object obj) {
		if (!_knownObjects.contains(obj)) {
			_knownObjects.add(obj);
			if (obj instanceof L1PcInstance) {
				_knownPlayer.add((L1PcInstance) obj);
			}
		}
	}
	/**
	 * キャラクターから、認識しているオブジェクトを削除する。
	 * 
	 * @param obj
	 *            削除するオブジェクト。
	 */
	public void removeKnownObject(L1Object obj) {
		if (_knownObjects.contains(obj))
			_knownObjects.remove(obj);
		if (obj instanceof L1PcInstance) {
			if (_knownPlayer.contains((L1PcInstance) obj))
				_knownPlayer.remove((L1PcInstance) obj);
		}
	}


	/**
	 *キャラクターから、すべての認識しているオブジェクトを削除する。
	 */
	/*public void removeAllKnownObjects() {
		_knownObjects.clear();
		_knownPlayer.clear();
	}*/

	public void removeAllKnownObjects() {
		if (_knownObjects.size() > 0)
			_knownObjects.clear();
		if (_knownPlayer.size() > 0)
			_knownPlayer.clear();
	}
	
	public String getName() {
		return _name;
	}

	public void setName(String s) {
		_name = s;
	}

	public int getLevel() {
		return _level;
	}

	public synchronized void setLevel(long level) {
		_level = (int) level;
	}

	 public int getMaxHp(){
		return _maxHp;
	}

	public void addMaxHp(int i) {
		setMaxHp(_trueMaxHp + i);
	}

	public void setMaxHp(int hp) {
		_trueMaxHp = hp;
		//_maxHp = (short) IntRange.ensure(_trueMaxHp, 1, 32767);
		_maxHp = IntRange.ensure(_trueMaxHp, 1, 1000000);
		_currentHp = Math.min(_currentHp, _maxHp);
	}

	public int getMaxMp() {
		return _maxMp;
	}

	public void setMaxMp(int mp) {
		_trueMaxMp = mp;
		//_maxMp = (short) IntRange.ensure(_trueMaxMp, 0, 32767);
		 _maxMp = IntRange.ensure(_trueMaxMp, 0, 1000000);
		_currentMp = Math.min(_currentMp, _maxMp);
	}

	public void addMaxMp(int i) {
		setMaxMp(_trueMaxMp + i);
	}

	public void healHp(int pt) {
		setCurrentHp(getCurrentHp() + pt);
	}

	public int getAddAttrKind() {
		return _addAttrKind;
	}

	public void setAddAttrKind(int i) {
		_addAttrKind = i;
	}

	public int getDmgup() {
		return _dmgup;
	}

	public void addDmgup(int i) {
		_trueDmgup += i;
		if (_trueDmgup >= 127) {
			_dmgup = 127;
		} else if (_trueDmgup <= -128) {
			_dmgup = -128;
		} else {
			_dmgup = _trueDmgup;
		}
	}

	public int getBowDmgup() {
		return _bowDmgup;
	}

	public void addBowDmgup(int i) {
		_trueBowDmgup += i;
		if (_trueBowDmgup >= 127) {
			_bowDmgup = 127;
		} else if (_trueBowDmgup <= -128) {
			_bowDmgup = -128;
		} else {
			_bowDmgup = _trueBowDmgup;
		}
	}

	public int getHitup() {
		return _hitup;
	}

	public void addHitup(int i) {
		_trueHitup += i;
		if (_trueHitup >= 127) {
			_hitup = 127;
		} else if (_trueHitup <= -128) {
			_hitup = -128;
		} else {
			_hitup = _trueHitup;
		}
	}

	public int getBowHitup() {
		return _bowHitup;
	}

	public void addBowHitup(int i) {
		_trueBowHitup += i;
		if (_trueBowHitup >= 127) {
			_bowHitup = 127;
		} else if (_trueBowHitup <= -128) {
			_bowHitup = -128;
		} else {
			_bowHitup = _trueBowHitup;
		}
	}

	public void addSp(int i) {
		_sp += i;
	}

	public int getSp() {
		return getTrueSp() + _sp;
	}

	public int getTrueSp() {
		return getMagicLevel() + getMagicBonus();
	}

	public int getMagicLevel() {
		return getLevel() / 4;
	}

	public int getMagicBonus() {
		int i = getAbility().getTotalInt();
		if (i <= 5)
			return -2;
		else if (i <= 8)
			return -1;
		else if (i <= 11)
			return 0;
		else if (i <= 14)
			return 1;
		else if (i <= 17)
			return 2;
		else
			return i - 15;
	}

	public boolean isDead() {
		return _isDead;
	}

	public void setDead(boolean flag) {
		_isDead = flag;
	}

	public int getStatus() {
		return _status;
	}

	public void setStatus(int i) {
		_status = i;
	}

	public String getTitle() {
		return _title;
	}

	public void setTitle(String s) {
		_title = s;
	}

	public int getLawful() {
		return _lawful;
	}

	public void setLawful(int i) {
		_lawful = i;
	}

	public synchronized void addLawful(int i) {
		_lawful += i;
		if (_lawful > 32767) {
			_lawful = 32767;
		} else if (_lawful < -32768) {
			_lawful = -32768;
		}
	}

	public int getHeading() {
		return _heading;
	}

	public void setHeading(int i) {
		_heading = i;
	}

	public int getMoveSpeed() {
		return _moveSpeed;
	}

	public void setMoveSpeed(int i) {
		_moveSpeed = i;
	}

	public int getBraveSpeed() {
		return _braveSpeed;
	}

	public void setBraveSpeed(int i) {
		_braveSpeed = i;
	}
	
	public int getTempCharGfx() {
		return _tempCharGfx;
	}

	public void setTempCharGfx(int i) {
		_tempCharGfx = i;
	}

	public int getGfxId() {
		return _gfxid;
	}

	public void setGfxId(int i) {
		_gfxid = i;
	}

	public boolean isInvisble() {
		return (hasSkillEffect(L1SkillId.INVISIBILITY) || hasSkillEffect(L1SkillId.BLIND_HIDING));
	}

	/** キャラクターのアップを返す。*/
	public int getKarma() {
		return _karma;
	}

	/** キャラクターのアップを設定する。 */
	public void setKarma(int karma) {
		_karma = karma;
	}

	
	/* Kill & Death システム？ - */
	 private int _Kills;

	 public int getKills() {
	  return _Kills;
	 }
	 public void setKills(int Kills) {
	     _Kills = Kills;
	 } 
	 private int _Deaths;
	 
	 public int getDeaths() {
	     return _Deaths;
	 }
	 public void setDeaths(int Deaths) {
	     _Deaths = Deaths;
	 }
	/* Kill & Death システム？- */
	 
	
	// ** ドオノディレイタイマ修正 **// 
	private long _skilldelay2;

	public long getSkilldelay2() {
		return _skilldelay2;
	}

	public void setSkilldelay2(long skilldelay2) {
		_skilldelay2 = skilldelay2;
	}

	// **GMのバフを付けて保存 **//
	private int _buffnoch;

	public int getBuffnoch() {
		return _buffnoch;
	}

	public void setBuffnoch(int buffnoch) {
		_buffnoch = buffnoch;
	}

	public static Random getRnd() {
		return _rnd;
	}

	public Light getLight() {
		return light;
	}

	public Ability getAbility() {
		return ability;
	}

	public Resistance getResistance() {
		return resistance;
	}

	public AC getAC() {
		return ac;
	}
	
	public MoveState getMoveState() {
		return moveState;
	}
	
	private int _ac = 0;
	private int _trueAc = 0;
	
	public int getAc(){
		return _ac;
	}
	
	public void setAc(int i){
		_trueAc = i;
		_ac = IntRange.ensure(i, -128, 127);
	}
	
	public void addAc(int i){
		setAc(_trueAc + i);
	}
	
	private int _mr = 0; // ●魔法防御（0）
	private int _trueMr = 0; // ● 本当にの魔法防御

	public int getMr() {
		if (hasSkillEffect(153) == true) {
			return _mr / 4;
		} else {
			return _mr;
		}
	} // 使用時に

	public int getTrueMr() {
		return _trueMr;
	} // セットするとき

	public void addMr(int i) {
		_trueMr += i;
		if (_trueMr <= 0) {
			_mr = 0;
		} else {
			_mr = _trueMr;
		}
	}
	
	private int actionStatus;
	public int getActionStatus() { return actionStatus; }
	public void setActionStatus(int i) { actionStatus = i;	}
	
	private int lockSectionUp;
	 
	 public int getLockSectionUp() {
	     return lockSectionUp;
	 }
	 public void setLockSectionUp(int i) {
		 lockSectionUp = i;
	 }
    private int attackLevel;
	 
	 public int getAttackLevel() {
	     return attackLevel;
	 }
	 public void setAttackLevel(int i) {
		 attackLevel = i;
	 }
	
	
}
