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

import static l1j.server.server.ActionCodes.*;

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
     * spr_actionテーブルをロードする。
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
        _log.config("SPRデータ" + _dataMap.size() + "件ロード");
    }

    /**
     * フレーム数とframe rateからアクションの合計時間（ms）を計算して返す。
     */
    private int calcActionSpeed(int frameCount, int frameRate) {
        return (int) (frameCount * 40 * (24D / frameRate));
    }

    /**
     * 指定されたsprの攻撃速度を返す。
     * もしsprに指定されたweapon_typeのデータが設定されていない場合は、1 attackのデータを返す。
     *
     * @param sprid -
     *              調査するsprのID
     * @param actid -
     *              武器の種類を示す値。 L1Item.getType1（）の変換値+1と一致する
     * @return 指定されたsprの攻撃速度（ms）
     */


    public int getAttackSpeed(int sprid, int actid, int lv, int classid) {

        if (((11328 <= sprid && 11407 >= sprid) && sprid != 11333 && sprid != 11364 && sprid != 11379)
                || (11408 <= sprid && 11421 >= sprid) || sprid == 12681 || sprid == 12702
                || sprid == 11447 || sprid == 11446 || sprid == 12237 || sprid == 12240 || sprid == 12232
                || sprid == 13152 || sprid == 13153 || sprid == 13388 || sprid == 13389) {//自己攻撃速度変身たち

            int fc = 22;
            if (actid == 12) {
                fc = 25;
            } else if (actid == 19) {
                fc = 28;
            } else if (actid == 21) {
                fc = 26;
            } else if (actid == 25) {
                fc = 23;
            } else if (actid == 47) {
                fc = 21;
            } else if (actid == 51) {
                fc = 24;
            } else if (actid == 63) {
                fc = 26;
            } else if (actid == 89) {
                fc = 21;
            }

            if (lv >= 10) {
                fc--;
            }
            if (lv >= 20) {
                fc--;
            }
            if (lv >= 30) {
                fc--;
            }
            if (lv >= 40) {
                fc--;
            }
            if (lv >= 45) {
                fc--;
            }
            if (lv >= 50) {
                fc--;
            }
            if (lv >= 52) {
                fc--;
            }
            if (lv >= 55) {
                fc--;
            }
            if (lv >= 75) {
                fc--;
            }
            if (lv >= 80) {
                fc--;
            }
            //System.out.println("ステップ3：「+ calcActionSpeed（fc、f.framerate））;
            return calcActionSpeed(fc, 24);
        } else {
            //System.out.println("スタッフ4： "）;
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
            } else if (_dataMap.get(sprid).moveSpeed.containsKey(ACTION_Walk)) {
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


