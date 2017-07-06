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
package l1j.server.server.datatables;

import static l1j.server.server.ActionCodes.ACTION_AltAttack;
import static l1j.server.server.ActionCodes.ACTION_Attack;
import static l1j.server.server.ActionCodes.ACTION_AxeAttack;
import static l1j.server.server.ActionCodes.ACTION_AxeWalk;
import static l1j.server.server.ActionCodes.ACTION_BowAttack;
import static l1j.server.server.ActionCodes.*;
import static l1j.server.server.ActionCodes.ACTION_ClawAttack;
import static l1j.server.server.ActionCodes.ACTION_ClawWalk;
import static l1j.server.server.ActionCodes.ACTION_DaggerAttack;
import static l1j.server.server.ActionCodes.ACTION_DaggerWalk;
import static l1j.server.server.ActionCodes.ACTION_EdoryuAttack;
import static l1j.server.server.ActionCodes.ACTION_EdoryuWalk;
import static l1j.server.server.ActionCodes.ACTION_SkillAttack;
import static l1j.server.server.ActionCodes.ACTION_SkillBuff;
import static l1j.server.server.ActionCodes.ACTION_SpearAttack;
import static l1j.server.server.ActionCodes.ACTION_SpearWalk;
import static l1j.server.server.ActionCodes.ACTION_SpellDirectionExtra;
import static l1j.server.server.ActionCodes.ACTION_StaffAttack;
import static l1j.server.server.ActionCodes.ACTION_StaffWalk;
import static l1j.server.server.ActionCodes.ACTION_SwordAttack;
import static l1j.server.server.ActionCodes.ACTION_SwordWalk;
import static l1j.server.server.ActionCodes.ACTION_ThrowingKnifeAttack;
import static l1j.server.server.ActionCodes.ACTION_ThrowingKnifeWalk;
import static l1j.server.server.ActionCodes.ACTION_TwoHandSwordAttack;
import static l1j.server.server.ActionCodes.ACTION_TwoHandSwordWalk;
import static l1j.server.server.ActionCodes.ACTION_Walk;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;

public class SprTable {

	private static Logger _log = Logger.getLogger(SprTable.class.getName());

	private static class Frame {
		private int framecount = 1200;

		private int framerate = 1200;
	}
	
	private static class Spr {
		private final HashMap<Integer, Integer> moveSpeed = new HashMap<Integer, Integer>();

		private final HashMap<Integer, Frame> attackSpeed = new HashMap<Integer, Frame>();
		
		private final HashMap<Integer, Integer> dmgMotionSpeed = new HashMap<Integer, Integer>();

		private int nodirSpellSpeed = 1200;

		private int dirSpellSpeed = 1200;
	}

	private static final HashMap<Integer, Spr> _dataMap = new HashMap<Integer, Spr>();

	private static final SprTable _instance = new SprTable();

	private SprTable() {
		loadSprAction();
	}

	public static SprTable getInstance() {
		return _instance;
	}

	/**
	 * spr_action 테이블을 로드한다.
	 */
	public void loadSprAction() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		Spr spr = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM spr_action");
			rs = pstm.executeQuery();
			while (rs.next()) {
				int key = rs.getInt("spr_id");
				if (!_dataMap.containsKey(key)) {
					spr = new Spr();
					_dataMap.put(key, spr);
				} else {
					spr = _dataMap.get(key);
				}

				int actid = rs.getInt("act_id");
				int frameCount = rs.getInt("framecount");
				int frameRate = rs.getInt("framerate");
				int speed = calcActionSpeed(frameCount, frameRate);

				switch (actid) {
				case ACTION_Walk:
				case ACTION_SwordWalk:
				case ACTION_AxeWalk:
				case ACTION_BowWalk:
				case ACTION_SpearWalk:
				case ACTION_StaffWalk:
				case ACTION_DaggerWalk:
				case ACTION_TwoHandSwordWalk:
				case ACTION_EdoryuWalk:
				case ACTION_ClawWalk:
				case ACTION_ThrowingKnifeWalk:
				case ACTION_ChainSwordWalk:
				case ACTION_DoubleAxeWalk:
					spr.moveSpeed.put(actid, speed);
					break;
				case ACTION_SkillAttack:
					spr.dirSpellSpeed = speed;
					break;
				case ACTION_SkillBuff:
					spr.nodirSpellSpeed = speed;
					break;
				case ACTION_Attack:
				case ACTION_SwordAttack:
				case ACTION_AxeAttack:
				case ACTION_BowAttack:
				case ACTION_SpearAttack:
				case ACTION_AltAttack:
				case ACTION_SpellDirectionExtra:
				case ACTION_StaffAttack:
				case ACTION_DaggerAttack:
				case ACTION_TwoHandSwordAttack:
				case ACTION_EdoryuAttack:
				case ACTION_ClawAttack:
				case ACTION_ThrowingKnifeAttack:
				case ACTION_ChainSwordAttack:
				case ACTION_DoubleAxeAttack:
					Frame f = new Frame();
					f.framecount = frameCount;
					f.framerate = frameRate;
					spr.attackSpeed.put(actid, f);
					break;
				case 2:
					spr.dmgMotionSpeed.put(actid, speed);
					break;
				default:
					break;
					
				}
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.config("SPR 데이터 " + _dataMap.size() + "건 로드");
	}
	
	/**
	 * 프레임수와 frame rate로부터 액션의 합계 시간(ms)을 계산해 돌려준다.
	 */
	private int calcActionSpeed(int frameCount, int frameRate) {
		return (int) (frameCount * 40 * (24D / frameRate));
	}

	/**
	 * 지정된 spr의 공격 속도를 돌려준다. 만약 spr로 지정된 weapon_type의 데이터가 설정되어 있지 않은 경우는, 1. attack의 데이터를 돌려준다.
	 * 
	 * @param sprid -
	 *            조사하는 spr의 ID
	 * @param actid -
	 *            무기의 종류를 나타내는 값. L1Item.getType1()의 변환값 +1과 일치한다
	 * @return 지정된 spr의 공격 속도(ms)
	 */

	
	public int getAttackSpeed(int sprid, int actid, int lv, int classid) {
	
		if(((11328 <= sprid && 11407 >= sprid) && sprid != 11333 && sprid != 11364 && sprid != 11379) 
				|| (11408 <= sprid && 11421 >= sprid) || sprid == 12681 || sprid == 12702
				 || sprid == 11447 || sprid == 11446 || sprid == 12237 || sprid == 12240|| sprid == 12232
				|| sprid == 13152 || sprid == 13153 || sprid == 13388 || sprid ==13389 ){//자가 공속 변신들
			
			int fc = 22;
            if(actid == 12){
            	fc = 25;
            }else if(actid == 19){
            	fc = 28;
            }else if(actid == 21){
            	fc = 26;
            }else if(actid == 25){
            	fc = 23;
            }else if(actid == 47){
            	fc = 21;
            }else if(actid == 51){
            	fc = 24;
            }else if(actid == 63){
            	fc = 26;
            }else if(actid == 89){
            	fc = 21;
            }
			
            if(lv >= 10){
				fc--;
			}
			if(lv >= 20){
				fc--;
			}
			if(lv >= 30){
				fc--;
			}
			if(lv >= 40){
				fc--;
			}
			if(lv >= 45){
				fc--;
			}
			if(lv >= 50){
				fc--;
			}
			if(lv >= 52){
				fc--;
			}
			if(lv >= 55){
				fc--;
			}
			if(lv >= 75){
				fc--;
			}
			if(lv >= 80){
				fc--;
			}
			//System.out.println("스탭3 : "+calcActionSpeed(fc, f.framerate));
			return calcActionSpeed(fc, 24);
		}else{
			//System.out.println("스탭4 : ");
			if (_dataMap.containsKey(sprid)) {
				if (_dataMap.get(sprid).attackSpeed.containsKey(actid)) {
					Frame f = _dataMap.get(sprid).attackSpeed.get(actid);
					return calcActionSpeed(f.framecount, f.framerate);
				} else if (_dataMap.get(sprid).attackSpeed.containsKey(ACTION_Attack)) {
					Frame f = _dataMap.get(sprid).attackSpeed.get(ACTION_Attack);
					return calcActionSpeed(f.framecount, f.framerate);
				} else {
					return 640;
				}
			}
		}
		return 640;
	}

	public int getMoveSpeed(int sprid, int actid) {
		if (_dataMap.containsKey(sprid)) {
			if (_dataMap.get(sprid).moveSpeed.containsKey(actid)) {
				return _dataMap.get(sprid).moveSpeed.get(actid);
			}else if (_dataMap.get(sprid).moveSpeed.containsKey(ACTION_Walk)) {
				return _dataMap.get(sprid).moveSpeed.get(ACTION_Walk);
			} else {
				return 640;
			}
		}
		return 640;
	}

	public int getDirSpellSpeed(int sprid) {
		if (_dataMap.containsKey(sprid)) {
			return _dataMap.get(sprid).dirSpellSpeed;
		}
		return 0;
	}

	public int getNodirSpellSpeed(int sprid) {
		if (_dataMap.containsKey(sprid)) {
			return _dataMap.get(sprid).nodirSpellSpeed;
		}
		return 0;
	}
	public int getDmgMotionSpeed(int sprid) {
		try {
			if (_dataMap.containsKey(sprid)) {
				if (_dataMap.get(sprid).dmgMotionSpeed.containsKey(2))
					return _dataMap.get(sprid).dmgMotionSpeed.get(2);
			}
		} catch (Exception e) {
			return 0;
		}
		return 0;

	}
}


