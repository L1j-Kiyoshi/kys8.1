/**
 * タイマー関連マップのコントローラ
 * 2008. 12. 04
 */

package l1j.server.server.Controller;

import java.util.ArrayList;
import java.util.logging.Logger;

import l1j.server.server.datatables.DoorSpawnTable;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.templates.L1TimeMap;

public class TimeMapController implements Runnable {
    public static final int SLEEP_TIME = 1000;

    private static Logger _log = Logger.getLogger(TimeMapController.class
            .getName());

    private ArrayList<L1TimeMap> mapList;                                                // マップストア
    private static TimeMapController instance;                                        // シングルシングルトンオブジェクト

    /**
     * シングルトンの実装 - 単一のオブジェクト返さ
     *
     * @return (TimeMapController)    単一のオブジェクト
     */
    public static TimeMapController getInstance() {
        if (instance == null) instance = new TimeMapController();
        return instance;
    }

    /**
     * 既定のコンストラクタ（シングルトン実装でprivate）
     */
    private TimeMapController() {
        mapList = new ArrayList<L1TimeMap>();
    }

    /**
     * Thread abstract Method
     */
    @Override
    public void run() {
        try {
            for (L1TimeMap timeMap : array()) {
                if (timeMap.count()) {
                    for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                        if (timeMap.getId() != pc.getMapId()) {
                            continue;
                        }

                        switch (pc.getMapId()) {
                            case 72:
                            case 73:
                            case 74:
                                new L1Teleport().teleport(pc, 34056, 32279, (short) 4, 5, true);
                                break;
                            case 460:
                            case 461:
                            case 462:
                            case 463:
                            case 464:
                            case 465:
                            case 466:
                                new L1Teleport().teleport(pc, 32664, 32855, (short) 457, 5, true);
                                break;
                            case 470:
                            case 471:
                            case 472:
                            case 473:
                            case 474:
                                new L1Teleport().teleport(pc, 32663, 32853, (short) 467, 5, true);
                                break;
                            case 475:
                            case 476:
                            case 477:
                            case 478:
                                new L1Teleport().teleport(pc, 32660, 32876, (short) 468, 5, true);
                                break;
                            default:
                                break;
                        }
                    }
                    DoorSpawnTable.getInstance().getDoor(timeMap.getDoor()).close();
                    remove(timeMap);
                }
            }
        } catch (Exception e) {
            _log.warning(e.getMessage());
        }
    }

    /**
     * タイムイベントがあるマップの登録
     * 重複登録がされていないように、既に登録されたマップ名と比較ない場合登録
     * サイズが0であれば、つまり、初期であれば、比較対象がないので、無条件に登録
     *
     * @param (TimeMap) 登録するマップオブジェクト
     */
    public void add(L1TimeMap map) {
        if (mapList.size() > 0) {
            boolean found = false;
            for (L1TimeMap m : array()) {
                if (m.getId() == map.getId()) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                mapList.add(map);
            }
        } else mapList.add(map);
    }

    /**
     * タイムイベントがあるマップの削除
     * 重複削除またはIndexOutOfBoundsExceptionがないように、既に登録されたマップ名と比較の場合、削除
     *
     * @param （TimeMap）削除マップオブジェクト
     */
    private void remove(L1TimeMap map) {
        for (L1TimeMap m : array()) {
            if (m.getId() == map.getId()) {
                mapList.remove(map);
                break;
            }
        }
        map = null;
    }

    /**
     * 登録されたイベントマップ配列戻り
     *
     * @return (TimeMap[])    マップオブジェクトの配列
     */
    private L1TimeMap[] array() {
        return mapList.toArray(new L1TimeMap[mapList.size()]);
    }
}