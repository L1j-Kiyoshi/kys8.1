package l1j.server.server.model;

import java.util.EnumMap;

import l1j.server.server.datatables.SprTable;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;

/**
 * アクセラレータの使用をチェックするクラス。
 */
public class AcceleratorChecker {
	class AccelInfo {
		public AccelInfo(long actTime, boolean first) {
			_actTime = actTime;
			_first = first;
		}

		public long _actTime;
		public boolean _first;
	}

	class SpeedInfo {
		public SpeedInfo(int gfxId) {
			_gfxId = gfxId;
		}

		public int _gfxId;
		public long _actTime;
		public long _totalActTime;
		public long _actCount;
	}

	private final L1PcInstance _pc;

	private int _move_gfxid = -1;
	private int _move_weapon = -1;
	private int _move_interval = -1;
	private int _attack_gfxid = -1;
	private int _attack_weapon = -1;
	private int _attack_interval = -1;
	
	private static final double Level_Rate_0 = 1.392;
	private static final double Level_Rate_15 = 1.321;
	private static final double Level_Rate_30 = 1.25;
	private static final double Level_Rate_45 = 1.178;
	private static final double Level_Rate_50 = 1.107;
	private static final double Level_Rate_52 = 1.035;
	private static final double Level_Rate_55 = 0.964;
	private static final double Level_Rate_75 = 0.892;
	private static final double Level_Rate_80 = 0.821;

	private static final double Level_Rate_82 = 0.812;
	private static final double Level_Rate_85 = 0.794;

	private static final double Move_Level_Rate_0 = 1.023;
	private static final double Move_Level_Rate_15 = 0.992;
	private static final double Move_Level_Rate_45 = 0.960;
	private static final double Move_Level_Rate_50 = 0.929;
	private static final double Move_Level_Rate_52 = 0.898;
	private static final double Move_Level_Rate_55 = 0.867;
	private static final double Move_Level_Rate_60 = 0.835;
	private static final double Move_Level_Rate_65 = 0.804;
	private static final double Move_Level_Rate_70 = 0.773;
	private static final double Move_Level_Rate_75 = 0.773;
	private static final double Move_Level_Rate_80 = 0.773;
	
	private static final double HASTE_RATE = 0.745;

	private static final double WAFFLE_RATE = 0.874;

	private static final double PEARL_RATE = 0.874;
	

	private final EnumMap<ACT_TYPE, AccelInfo> _actTimers = new EnumMap<ACT_TYPE, AccelInfo>(ACT_TYPE.class);

	private final EnumMap<ACT_TYPE, SpeedInfo> _speedRecorder = new EnumMap<ACT_TYPE, SpeedInfo>(ACT_TYPE.class);

	public static enum ACT_TYPE {
		MOVE, ATTACK, SPELL_DIR, SPELL_NODIR
	}

	// チェックの結果
	public static final int R_OK = 0;

	public static final int R_DETECTED = 1;

	public static final int R_DISCONNECTED = 2;

	public AcceleratorChecker(L1PcInstance pc) {
		_pc = pc;
		long now = System.currentTimeMillis();
		for (ACT_TYPE each : ACT_TYPE.values()) {
			_actTimers.put(each, new AccelInfo(now, false));
		}
	}

	/**
	 * アクションの間隔が否定しないかチェックして、適当処理を実施する。
	 * 
	 * @param type
	 *             - チェックするアクションのタイプ
	 * @return 問題がなかった場合は0、負の場合は、1、不正動作が一定回数に達したためプレイヤーを切断した場合は、
	 *         2を返す。
	 */

	public boolean isAccelerated(ACT_TYPE type) {
		long now = System.currentTimeMillis();
		AccelInfo accelInfo = _actTimers.get(type);
		
		long interval = getRightInterval(type);
		
		if( interval == 0 )
		{
			return false;
			
//			SpeedInfo speedInfo = _speedRecorder.get(type);
//			
//			if( speedInfo == null || speedInfo._gfxId != _pc.getTempCharGfx())
//			{
//				speedInfo = new SpeedInfo(_pc.getTempCharGfx());
//			}
//			
//			if( speedInfo._actTime != 0 )
//			{
//				long duration = now - speedInfo._actTime;
//				
//				++speedInfo._actCount;
//				speedInfo._totalActTime += duration;
//			}
//
//			speedInfo._actTime = now;
//			
//			_speedRecorder.put(type, speedInfo);
//			
//			return false;
		}

		if( now - accelInfo._actTime > interval * 3 )
		{
			_actTimers.put(type, new AccelInfo(now, true));
			return false;
		}
		else if( now - accelInfo._actTime > interval )
		{
			_actTimers.put(type, new AccelInfo(accelInfo._actTime + interval, false));
			return false;
		}
		else if( accelInfo._first && now - accelInfo._actTime > interval / 2 )
		{
			_actTimers.put(type, new AccelInfo(now, false));
			return false;
		}
		else if( type == ACT_TYPE.MOVE)
		{
			_actTimers.put(type, new AccelInfo(now, false));
		}
		
		return true;
	}

	public boolean isAccelerated(ACT_TYPE type, int interval) {
		long now = System.currentTimeMillis();
		AccelInfo accelInfo = _actTimers.get(type);

		if (interval == 0) {
			return false;
		}

		if (now - accelInfo._actTime > interval) {
			return false;
		}

		return true;
	}

	public long getLeftTime(ACT_TYPE type) {
		long now = System.currentTimeMillis();
		AccelInfo accelInfo = _actTimers.get(type);

		return Math.max(0, accelInfo._actTime + getRightInterval(type) - now);
	}

	/**
	 * PCの状態から指定された種類のアクションの適切なインターバル（ms）を計算し、返す。
	 * 
	 * @param type
	 *             - アクションの種類
	 * @param _pc
	 *             - 調査しているPC
	 * @return 正しいインターバル（ms）
	 */
	public int getRightInterval(ACT_TYPE type) {
		int interval;
	    int gfxid = _pc.getTempCharGfx();
	    int weapon = _pc.getCurrentWeapon();

		switch (type) {
		case ATTACK:
			interval = SprTable.getInstance().getAttackSpeed(_pc.getTempCharGfx(), _pc.getCurrentWeapon() + 1, _pc.getLevel(), _pc.getClassId());
			
//			if (_attack_gfxid != gfxid || _attack_weapon != weapon) {
//				_attack_gfxid = gfxid;
//				_attack_weapon = weapon;
//				_attack_interval = SprTable.getInstance().getAttackSpeed(_pc.getTempCharGfx(), _pc.getCurrentWeapon() + 1, _pc.getLevel(), _pc.getClassId());
//			}
//
//			interval = _attack_interval;
//
//			if (gfxid == 13140) {
//				interval *= Level_Rate_80;
//			}
//
//			if ((gfxid >= 11328 && gfxid <= 11448) || gfxid == 12237 || gfxid == 12702 || gfxid == 12681 || gfxid == 12541
//					|| gfxid == 12542 || gfxid == 13152 || gfxid == 13153) {
//				if (_pc.getLevel() >= 85) {
//					interval *= Level_Rate_85;
//				} else if (_pc.getLevel() >= 82) {
//					interval *= Level_Rate_82;
//				} else if (_pc.getLevel() >= 80) {
//					interval *= Level_Rate_80;
//				} else if (_pc.getLevel() >= 75) {
//					interval *= Level_Rate_75;
//				} else if (_pc.getLevel() >= 55) {
//					interval *= Level_Rate_55;
//				} else if (_pc.getLevel() >= 52) {
//					interval *= Level_Rate_52;
//				} else if (_pc.getLevel() >= 50) {
//					interval *= Level_Rate_50;
//				} else if (_pc.getLevel() >= 45) {
//					interval *= Level_Rate_45;
//				} else if (_pc.getLevel() >= 30) {
//					interval *= Level_Rate_30;
//				} else if (_pc.getLevel() >= 15) {
//					interval *= Level_Rate_15;
//				} else {
//					interval *= Level_Rate_0;
//				}
//			}

			break;
			
		case MOVE:
			if ((_move_gfxid != gfxid) || (_move_weapon != weapon)) {
				_move_gfxid = gfxid;
				_move_weapon = weapon;
				_move_interval = SprTable.getInstance().getMoveSpeed(_pc.getTempCharGfx(), _pc.getCurrentWeapon());
			}
			interval = _move_interval;
			if (gfxid == 13140) {
				interval *= Move_Level_Rate_80;
			}
			if (gfxid == 11333 || // "lv1 dwarf" ; 小人
					gfxid == 11343 || // "lv15 ungoliant" ; ドレッドスパイダー
					gfxid == 11355 || // "lv30 cockatrice" ; コカトリス
					gfxid == 11364 || // "lv45 baphomet" ; バフォメット
					gfxid == 11379// "lv52 beleth" ; ベレス
			) {
				if (_pc.getLevel() >= 80) {
					interval *= Move_Level_Rate_80;
				} else if (_pc.getLevel() >= 75) {
					interval *= Move_Level_Rate_75;
				} else if (_pc.getLevel() >= 70) {
					interval *= Move_Level_Rate_70;
				} else if (_pc.getLevel() >= 65) {
					interval *= Move_Level_Rate_65;
				} else if (_pc.getLevel() >= 60) {
					interval *= Move_Level_Rate_60;
				} else if (_pc.getLevel() >= 55) {
					interval *= Move_Level_Rate_55;
				} else if (_pc.getLevel() >= 52) {
					interval *= Move_Level_Rate_52;
				} else if (_pc.getLevel() >= 50) {
					interval *= Move_Level_Rate_50;
				} else if (_pc.getLevel() >= 45) {
					interval *= Move_Level_Rate_45;
				} else if (_pc.getLevel() >= 15) {
					interval *= Move_Level_Rate_15;
				} else {
					interval *= Move_Level_Rate_0;
				}
			}
			break;
		case SPELL_DIR:
			interval = SprTable.getInstance().getDirSpellSpeed(_pc.getTempCharGfx());
			break;
		case SPELL_NODIR:
			interval = SprTable.getInstance().getNodirSpellSpeed(_pc.getTempCharGfx());
			break;
		default:
			return 0;
		}

		// ウィンドセクルかかった状態であれば、
		if (type.equals(ACT_TYPE.ATTACK) && this._pc.hasSkillEffect(167)) { 
			interval *= 2;
		}
		if (_pc.isHaste()) {
			interval *= HASTE_RATE;
		}
		if (type.equals(ACT_TYPE.MOVE) && _pc.isFastMovable()) {
			interval *= HASTE_RATE;
		}
		if (type.equals(ACT_TYPE.MOVE) && _pc.isBlackwizard() && _pc.isUgdraFruit()) {
			interval *= HASTE_RATE;
		}
		if (_pc.isBlood_lust()) { // ブラッドラスト
			interval *= HASTE_RATE;
		}

		if (_pc.isBrave()) {
			interval *= HASTE_RATE;
		}
		if (_pc.hasSkillEffect(L1SkillId.DANCING_BLADES)) {
			interval *= HASTE_RATE;
		}
		if (type.equals(ACT_TYPE.MOVE) && _pc.isElfBrave()) {
			interval *= HASTE_RATE;
		}

		if (type.equals(ACT_TYPE.ATTACK) && _pc.isElfBrave()) {
			interval *= WAFFLE_RATE;
		}
		if (_pc.isDragonPearl()) {
			interval *= PEARL_RATE;
		}
		if (_pc.getMapId() == 5143) {
			interval *= (HASTE_RATE / 2);
		}
		if (type.equals(ACT_TYPE.MOVE) && (gfxid == 6697 || gfxid == 6698)) {
			interval *= HASTE_RATE;
		}
		interval *= 0.95;
		
		return interval;
	}

}
