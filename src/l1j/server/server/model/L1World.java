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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.TextMapReader;
import l1j.server.GameSystem.Robot.L1RobotInstance;
import l1j.server.server.model.Instance.L1CastleGuardInstance;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1GuardInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1NpcShopInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.Instance.L1SupportInstance;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.ServerBasePacket;
import l1j.server.server.types.Point;

public class L1World {
    private static Logger _log = Logger.getLogger(L1World.class.getName());
    private final ConcurrentHashMap<String, L1PcInstance> _allPlayers;
    private final ConcurrentHashMap<Integer, L1PetInstance> _allPets;
    private final ConcurrentHashMap<Integer, L1SummonInstance> _allSummons;
    private final ConcurrentHashMap<Integer, L1SupportInstance> _allSupports;
    private final ConcurrentHashMap<Integer, L1Object> _allObjects;
    private final ConcurrentHashMap<Integer, L1Object>[] _visibleObjects;
    private final CopyOnWriteArrayList<L1War> _allWars;
    private final ConcurrentHashMap<Integer, L1ItemInstance> _allitem;
    private final ConcurrentHashMap<String, L1Clan> _allClans;
    private final ConcurrentHashMap<Integer, L1Clan> _allClansById;
    private final ConcurrentHashMap<Integer, L1NpcInstance> _allNpc;
    private final ConcurrentHashMap<Integer, L1NpcShopInstance> _allShopNpc;
    private final ConcurrentHashMap<Integer, L1GuardInstance> _allGuard;
    private final ConcurrentHashMap<Integer, L1CastleGuardInstance> _allCastleGuard;
    private final ConcurrentHashMap<Integer, L1RobotInstance> _allRobot;


    private int _weather = 4;

    private boolean _worldChatEnabled = true;

    private boolean _processingContributionTotal = false;

    private static final int MAX_MAP_ID = 32768;
    private final visibleLocObjects[] _visibleLocMap;
    private static L1World _instance;

    @SuppressWarnings("unchecked")
    private L1World() {
        _allPlayers = new ConcurrentHashMap<String, L1PcInstance>(); // すべてのプレイヤー
        _allPets = new ConcurrentHashMap<Integer, L1PetInstance>(); // すべてのペット
        _allSummons = new ConcurrentHashMap<Integer, L1SummonInstance>(); // すべてのサーモンモンスター
        _allSupports = new ConcurrentHashMap<Integer, L1SupportInstance>(); //すべてのサポート
        _allObjects = new ConcurrentHashMap<Integer, L1Object>();
        _visibleObjects = new ConcurrentHashMap[MAX_MAP_ID + 1];
        _allWars = new CopyOnWriteArrayList<L1War>(); // すべての戦争
        _allClansById = new ConcurrentHashMap<Integer, L1Clan>();
        _allClans = new ConcurrentHashMap<String, L1Clan>();
        _allNpc = new ConcurrentHashMap<Integer, L1NpcInstance>(); // すべてのnpc
        _allShopNpc = new ConcurrentHashMap<Integer, L1NpcShopInstance>(); // すべての無人NPC商店
        _allitem = new ConcurrentHashMap<Integer, L1ItemInstance>();
        _allGuard = new ConcurrentHashMap<Integer, L1GuardInstance>(); // すべての警備兵
        _allCastleGuard = new ConcurrentHashMap<Integer, L1CastleGuardInstance>();
        _allRobot = new ConcurrentHashMap<Integer, L1RobotInstance>(); // すべて
        for (int i = 0; i <= MAX_MAP_ID; i++) {
            _visibleObjects[i] = new ConcurrentHashMap<Integer, L1Object>();
        }
        _visibleLocMap = new visibleLocObjects[MAX_MAP_ID + 1];

        try {
            for (int[] mapInfo : TextMapReader.MAP_INFO) {
                short map = (short) mapInfo[TextMapReader.MAPINFO_MAP_NO];
                L1Map m = L1WorldMap.getInstance().getMap(map);
                _visibleLocMap[map] = new visibleLocObjects(m.getX(),
                        m.getWidth(), m.getY(), m.getHeight());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void mapClone(int oldMapId, int newMapId) {
        boolean check = false;
        try {
            visibleLocObjects vl = _visibleLocMap[newMapId];
            if (vl.CHECK())
                check = true;
        } catch (Exception e) {
            check = true;
        }
        // if(_visibleLocMap[newMapId] == null){
        if (check) {
            L1Map m = L1WorldMap.getInstance().getMap((short) oldMapId);
            _visibleLocMap[newMapId] = new visibleLocObjects(m.getX(),
                    m.getWidth(), m.getY(), m.getHeight());
        }
    }

    private static final int visibleLocSize = 10;

    class visibleLocObjects {
        private int _startX;
        private int _startY;
        private int _width;
        private int _height;
        private final ConcurrentHashMap<Integer, L1Object>[][] _visibleLocXY;

        public visibleLocObjects(int startX, int width, int startY, int height) {
            _startX = startX;
            _startY = startY;
            _width = width;
            _height = height;

            // 座標ごとに生成すればよいが、メモリが4基、または食べる。
            // だからvisibleLocSize単位で分割することによって保存
            int w = width / visibleLocSize;
            int h = height / visibleLocSize;

            _visibleLocXY = new ConcurrentHashMap[w + 1][h + 1];
            for (int x = 0; x <= w; x++) {
                for (int y = 0; y <= h; y++) {
                    _visibleLocXY[x][y] = new ConcurrentHashMap<Integer, L1Object>();
                }
            }
        }

        public boolean CHECK() {
            if (_startX == 0 && _startY == 0 && _width == 0 && _height == 0)
                return true;
            return false;
        }

        public void store(L1Object object) {
            try {
                int x = (object.getX() - _startX) / visibleLocSize;
                int y = (object.getY() - _startY) / visibleLocSize;
                object.setVisibleX(x);
                object.setVisibleY(y);
                object.setVisibleMapId(object.getMapId());
                object.setVisibleTempX(object.getX());
                object.setVisibleTempY(object.getY());
                _visibleLocXY[x][y].put(object.getId(), object);
            } catch (Exception e) {
            }
        }

        public void store(L1Object object, int newX, int newY) {
            try {
                int x = (newX - _startX) / visibleLocSize;
                int y = (newY - _startY) / visibleLocSize;
                object.setVisibleX(x);
                object.setVisibleY(y);
                object.setVisibleTempX(newX);
                object.setVisibleTempY(newY);
                _visibleLocXY[x][y].put(object.getId(), object);
            } catch (Exception e) {
            }
        }

        public void remove(L1Object object) {
            try {
                _visibleLocXY[object.getVisibleX()][object.getVisibleY()]
                        .remove(object.getId());
            } catch (Exception e) {
            }
        }

        public void move(L1Object object, int newX, int newY) {
            try {
                int newx = (newX - _startX) / visibleLocSize;
                int newy = (newY - _startY) / visibleLocSize;
                _visibleLocXY[object.getVisibleX()][object.getVisibleY()]
                        .remove(object.getId());
                object.setVisibleX(newx);
                object.setVisibleY(newy);
                object.setVisibleTempX(newX);
                object.setVisibleTempY(newY);
                _visibleLocXY[newx][newy].put(object.getId(), object);
            } catch (Exception e) {
            }
        }

        public ArrayList<L1Object> VisiblePoint(L1Location loc, int radius) {
            ArrayList<L1Object> result = new ArrayList<L1Object>();
            try {
                int cx = (loc.getX() - _startX) / visibleLocSize;
                int cy = (loc.getY() - _startY) / visibleLocSize;
                int ccx = _width / visibleLocSize;
                int ccy = _height / visibleLocSize;
                for (int x = cx - 4; x < cx + 4; x++) {
                    if (x > ccx || x < 0)
                        continue;
                    for (int y = cy - 4; y < cy + 4; y++) {
                        if (y > ccy || y < 0)
                            continue;
                        for (L1Object obj : _visibleLocXY[x][y].values()) {
                            if (obj == null)
                                continue;
                            if (loc.getTileLineDistance(obj.getLocation()) <= radius)
                                result.add(obj);
                        }
                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
            return result;
        }

        public ArrayList<L1Object> Visible(L1Object object, int radius) {
            ArrayList<L1Object> result = new ArrayList<L1Object>();
            try {
                Point pt = object.getLocation();
                int cx = (object.getX() - _startX) / visibleLocSize;
                int cy = (object.getY() - _startY) / visibleLocSize;
                int ccx = _width / visibleLocSize;
                int ccy = _height / visibleLocSize;
                for (int x = cx - 4; x < cx + 4; x++) {
                    if (x > ccx || x < 0)
                        continue;
                    for (int y = cy - 4; y < cy + 4; y++) {
                        if (y > ccy || y < 0)
                            continue;
                        for (L1Object obj : _visibleLocXY[x][y].values()) {
                            if (obj == null || obj.equals(object))
                                continue;
                            if (radius == -1) {
                                if (pt.isInScreen(obj.getLocation()))
                                    result.add(obj);
                            } else if (radius == 0) {
                                if (pt.isSamePoint(obj.getLocation()))
                                    result.add(obj);
                            } else {
                                if (pt.getTileLineDistance(obj.getLocation()) <= radius)
                                    result.add(obj);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
            return result;
        }
    }

    public void Move(L1Object object, int newX, int newY) {
        try {
            if (object == null)
                return;
            int mapid = object.getVisibleMapId();
            if (mapid <= MAX_MAP_ID)
                _visibleLocMap[mapid].move(object, newX, newY);
        } catch (Exception e) {
        }
    }

    public static L1World getInstance() {
        if (_instance == null) {
            _instance = new L1World();
        }
        return _instance;
    }

    /**
     * すべての状態をクリアする。<br>
     * デバッグ、テストなどの特殊な目的以外で呼び出してはいけない。
     */
    public void clear() {
        _instance = new L1World();
    }

    public void storeObject(L1Object object) {
        if (object == null) {
            throw new NullPointerException();
        }

        _allObjects.put(object.getId(), object);
        if (object instanceof L1RobotInstance) {
            _allRobot.put(object.getId(), (L1RobotInstance) object);
            _allPlayers.put(((L1PcInstance) object).getName(), (L1PcInstance) object);
        } else if (object instanceof L1PcInstance) {
            _allPlayers.put(((L1PcInstance) object).getName().toUpperCase(), (L1PcInstance) object);
        }
        if (object instanceof L1PetInstance) {
            _allPets.put(object.getId(), (L1PetInstance) object);
        }
        if (object instanceof L1SummonInstance) {
            _allSummons.put(object.getId(), (L1SummonInstance) object);
        }
        if (object instanceof L1SupportInstance) {
            _allSupports.put(object.getId(), (L1SupportInstance) object);
        }
        if (object instanceof L1NpcInstance) {
            _allNpc.put(((L1NpcInstance) object).getNpcTemplate().get_npcId(), (L1NpcInstance) object);
        }
        if (object instanceof L1NpcShopInstance) {
            _allShopNpc.put(object.getId(), (L1NpcShopInstance) object);
        }
    }

    public void removeObject(L1Object object) {
        if (object == null) {
            throw new NullPointerException();
        }

        _allObjects.remove(object.getId());
        if (object instanceof L1RobotInstance) {
            _allRobot.put(object.getId(), (L1RobotInstance) object);
            _allPlayers.put(((L1PcInstance) object).getName(), (L1PcInstance) object);
        } else if (object instanceof L1PcInstance) {
            _allPlayers.remove(((L1PcInstance) object).getName().toUpperCase());
        }
        if (object instanceof L1PetInstance) {
            _allPets.remove(object.getId());
        }
        if (object instanceof L1SummonInstance) {
            _allSummons.remove(object.getId());
        }
        if (object instanceof L1SupportInstance) {
            _allSupports.remove(object.getId());
        }
        if (object instanceof L1NpcInstance) {
            _allNpc.remove(((L1NpcInstance) object).getNpcTemplate().get_npcId());
        }
        if (object instanceof L1NpcShopInstance) {
            _allShopNpc.remove(object.getId());
        }
    }

    public L1Object findObject(int oID) {
        return _allObjects.get(oID);
    }

    public L1Object findObject(String name) {
        if (_allObjects.contains(name)) {
            return _allObjects.get(name);
        }
        for (L1PcInstance each : getAllPlayers()) {
            if (each.getName().equalsIgnoreCase(name)) {
                return each;
            }
        }
        return null;
    }

    // _allObjectsのビュー
    private Collection<L1Object> _allValues;

    public Collection<L1Object> getObject() {
        Collection<L1Object> vs = _allValues;
        return (vs != null) ? vs : (_allValues = Collections
                .unmodifiableCollection(_allObjects.values()));
    }

    public L1GroundInventory getInventory(int x, int y, short map) {
        int inventoryKey = ((x - 30000) * 10000 + (y - 30000)) * -1; // xyのマイナス値をインベントリツリーキーとして使用

        Object object = _visibleObjects[map].get(inventoryKey);
        if (object == null) {
            return new L1GroundInventory(inventoryKey, x, y, map);
        } else {
            return (L1GroundInventory) object;
        }
    }

    public L1GroundInventory getInventory(L1Location loc) {
        return getInventory(loc.getX(), loc.getY(), (short) loc.getMap().getId());
    }

    public ArrayList<L1Object> getMapObject(int mapid) {
        ArrayList<L1Object> obj = new ArrayList<L1Object>();

        if (mapid > MAX_MAP_ID) return null;
        for (L1Object element : _visibleObjects[mapid].values()) {
            obj.add(element);
        }
        return obj;
    }

    public void addVisibleObject(L1Object object) {
        if (object == null) return;
        if (object.getMapId() <= MAX_MAP_ID) {
            _visibleObjects[object.getMapId()].put(object.getId(), object);
        }
    }

    public void removeVisibleObject(L1Object object) {
        if (object == null) return;
        if (object.getMapId() <= MAX_MAP_ID) {
            _visibleObjects[object.getMapId()].remove(object.getId());
        }
    }

    public void moveVisibleObject(L1Object object, int newx, int newy,
                                  int newMap) {
        if (object == null) {
            return;
        }

        int oldMapId = object.getVisibleMapId();
        L1Map oldmap = L1WorldMap.getInstance().getMap((short) oldMapId);

        if (oldMapId != newMap) {
            L1Map newmap = L1WorldMap.getInstance().getMap((short) newMap);
            if (!(object instanceof L1Inventory)
                    && !(object instanceof L1DollInstance)
                    && !(object instanceof L1DoorInstance)) {
                oldmap.setPassable(object.getVisibleTempX(),
                        object.getVisibleTempY(), true);
                newmap.setPassable(newx, newy, false);
            }
            if (object instanceof L1DoorInstance) {
                L1DoorInstance door = (L1DoorInstance) object;
                if (door.getNpcTemplate().get_npcId() == 5000091) {
                    oldmap.setPassable(object.getVisibleTempX(),
                            object.getVisibleTempY(), true);
                    newmap.setPassable(newx, newy, false);
                }
            }
            if (oldMapId <= MAX_MAP_ID) {
                _visibleObjects[oldMapId].remove(object.getId());
                try {
                    _visibleLocMap[oldMapId].remove(object);
                } catch (Exception e) {
                }
            }
            if (newMap <= MAX_MAP_ID) {
                object.setVisibleMapId(newMap);
                _visibleObjects[newMap].put(object.getId(), object);
                try {
                    _visibleLocMap[newMap].store(object, newx, newy);
                } catch (Exception e) {
                }
            }
        } else {
            if (!(object instanceof L1Inventory)
                    && !(object instanceof L1DollInstance)
                    && !(object instanceof L1DoorInstance)) {
                oldmap.setPassable(object.getVisibleTempX(),
                        object.getVisibleTempY(), true);
                oldmap.setPassable(newx, newy, false);
            }
            if (object instanceof L1DoorInstance) {
                L1DoorInstance door = (L1DoorInstance) object;
                if (door.getNpcTemplate().get_npcId() == 5000091) {
                    oldmap.setPassable(object.getVisibleTempX(),
                            object.getVisibleTempY(), true);
                    oldmap.setPassable(newx, newy, false);
                }
            }
            try {
                _visibleLocMap[oldMapId].move(object, newx, newy);
            } catch (Exception e) {
            }
        }
    }

    public void moveVisibleObject(L1Object object, int newMap) // set_Mapで新しいMapにする前に呼ばれること
    {
        if (object == null) return;
        if (object.getMapId() != newMap) {
            if (object.getMapId() <= MAX_MAP_ID) {
                _visibleObjects[object.getMapId()].remove(object.getId());
            }
            if (newMap <= MAX_MAP_ID) {
                _visibleObjects[newMap].put(object.getId(), object);
            }
        }
    }

    private ConcurrentHashMap<Integer, Integer> createLineMap(Point src,
                                                              Point target) {
        ConcurrentHashMap<Integer, Integer> lineMap = new ConcurrentHashMap<Integer, Integer>();

		/*
         * http://www2.starcat.ne.jp/~fussy/algo/algo1-1.htmより
		 */
        int E;
        int x;
        int y;
        int key;
        int i;
        int x0 = src.getX();
        int y0 = src.getY();
        int x1 = target.getX();
        int y1 = target.getY();
        int sx = (x1 > x0) ? 1 : -1;
        int dx = (x1 > x0) ? x1 - x0 : x0 - x1;
        int sy = (y1 > y0) ? 1 : -1;
        int dy = (y1 > y0) ? y1 - y0 : y0 - y1;

        x = x0;
        y = y0;
        /* 傾きが1以下の場合 */
        if (dx >= dy) {
            E = -dx;
            for (i = 0; i <= dx; i++) {
                key = (x << 16) + y;
                lineMap.put(key, key);
                x += sx;
                E += 2 * dy;
                if (E >= 0) {
                    y += sy;
                    E -= 2 * dx;
                }
            }
            /* 傾きが1よりも大きい場合 */
        } else {
            E = -dy;
            for (i = 0; i <= dy; i++) {
                key = (x << 16) + y;
                lineMap.put(key, key);
                y += sy;
                E += 2 * dx;
                if (E >= 0) {
                    x += sx;
                    E -= 2 * dy;
                }
            }
        }

        return lineMap;
    }

    public ArrayList<L1Object> getVisibleLineObjects(L1Object src, L1Object target) {
        ConcurrentHashMap<Integer, Integer> lineMap = createLineMap(src.getLocation(), target.getLocation());

        int map = target.getMapId();
        ArrayList<L1Object> result = new ArrayList<L1Object>();

        if (map <= MAX_MAP_ID) {
            for (L1Object element : _visibleObjects[map].values()) {
                if (element == null || element.equals(src)) {
                    continue;
                }

                int key = (element.getX() << 16) + element.getY();
                if (lineMap.containsKey(key)) {
                    result.add(element);
                }
            }
        }

        return result;
    }

    public ArrayList<L1Object> getVisibleBoxObjects(L1Object object, int heading, int width, int height) {
        int x = object.getX();
        int y = object.getY();
        int map = object.getMapId();
        L1Location location = object.getLocation();
        ArrayList<L1Object> result = new ArrayList<L1Object>();
        int headingRotate[] = { 6, 7, 0, 1, 2, 3, 4, 5 };
        double cosSita = Math.cos(headingRotate[heading] * Math.PI / 4);
        double sinSita = Math.sin(headingRotate[heading] * Math.PI / 4);

        if (map <= MAX_MAP_ID) {
            for (L1Object element : _visibleObjects[map].values()) {
                if (element == null || element.equals(object)) {
                    continue;
                }
                if (map != element.getMapId()) {
                    continue;
                }
                if (location.isSamePoint(element.getLocation())) {
                    result.add(element);
                    continue;
                }
                int distance = location.getTileLineDistance(element
                        .getLocation());
                // 直線距離が高さ、幅のどちらよりも大きい場合には、計算もなく範囲外
                if (distance > height && distance > width) {
                    continue;
                }

                // objectの位置を原点とするための座標補正
                int x1 = element.getX() - x;
                int y1 = element.getY() - y;

                // Z軸回転させて角度を0度とする。
                int rotX = (int) Math.round(x1 * cosSita + y1 * sinSita);
                int rotY = (int) Math.round(-x1 * sinSita + y1 * cosSita);

                int xmin = 0;
                int xmax = height;
                int ymin = -width;
                int ymax = width;

                // 深さが事情とかみ合わないので、直線距離で判定するように変更します。
                // if (rotX > xmin && rotX <= xmax && rotY >= ymin && rotY <=
                // ymax) {
                if (rotX > xmin && distance <= xmax && rotY >= ymin
                        && rotY <= ymax) {
                    result.add(element);
                }
            }
        }

        return result;
    }

    public ArrayList<L1Object> getVisibleObjects(L1Object object) {
        return getVisibleObjects(object, -1);
    }

    public ArrayList<L1Object> getVisibleObjects(L1Object object, int radius) {
        L1Map map = object.getMap();
        Point pt = object.getLocation();
        ArrayList<L1Object> result = new ArrayList<L1Object>();
        if (map.getId() <= MAX_MAP_ID) {
            for (L1Object element : _visibleObjects[map.getId()].values()) {
                if (element == null || element.equals(object)) {
                    continue;
                }
                if (map != element.getMap()) {
                    continue;
                }

                if (radius == -1) {
                    if (pt.isInScreen(element.getLocation())) {
                        result.add(element);
                    }
                } else if (radius == 0) {
                    if (pt.isSamePoint(element.getLocation())) {
                        result.add(element);
                    }
                } else {
                    if (pt.getTileLineDistance(element.getLocation()) <= radius) {
                        result.add(element);
                    }
                }
            }
        }

        return result;
    }

    public ArrayList<L1Object> getVisiblePoint(L1Location loc, int radius) {
        ArrayList<L1Object> result = new ArrayList<L1Object>();
        int mapId = loc.getMapId(); // ループ内で歌えば（者）重いので、

        if (mapId <= MAX_MAP_ID) {
            for (L1Object element : _visibleObjects[mapId].values()) {
                if (element == null || mapId != element.getMapId()) {
                    continue;
                }

                if (loc.getTileLineDistance(element.getLocation()) <= radius) {
                    result.add(element);
                }
            }
        }

        return result;
    }

    public ArrayList<L1PcInstance> getVisiblePlayer(L1Object object) {
        return getVisiblePlayer(object, -1);
    }
//
//	public ArrayList<L1PcInstance> getVisiblePlayer(L1Object object, int radius) {
//		int map = object.getMapId();
//		Point pt = object.getLocation();
//		ArrayList<L1PcInstance> result = new ArrayList<L1PcInstance>();
//
//		for (L1PcInstance element : _allPlayers.values()) {
//			if (element.equals(object)) {
//				continue;
//			}
//
//			if (map != element.getMapId()) {
//				continue;
//			}
//
//			if (radius == -1) {
//				if (pt.isInScreen(element.getLocation())) {
//					result.add(element);
//				}
//			} else if (radius == 0) {
//				if (pt.isSamePoint(element.getLocation())) {
//					result.add(element);
//				}
//			} else {
//				if (pt.getTileLineDistance(element.getLocation()) <= radius) {
//					result.add(element);
//				}
//			}
//		}
//		return result;
//	}
//
//	public ArrayList<L1PcInstance> getVisiblePlayerExceptTargetSight(
//			L1Object object, L1Object target) {
//		int map = object.getMapId();
//		Point objectPt = object.getLocation();
//		Point targetPt = target.getLocation();
//		ArrayList<L1PcInstance> result = new ArrayList<L1PcInstance>();
//
//		for (L1PcInstance element : _allPlayers.values()) {
//			if (element.equals(object)) {
//				continue;
//			}
//
//			if (map != element.getMapId()) {
//				continue;
//			}
//
//			if (Config.PC_RECOGNIZE_RANGE == -1) {
//				if (objectPt.isInScreen(element.getLocation())) {
//					if (!targetPt.isInScreen(element.getLocation())) {
//						result.add(element);
//					}
//				}
//			} else {
//				if (objectPt.getTileLineDistance(element.getLocation()) <= Config.PC_RECOGNIZE_RANGE) {
//					if (targetPt.getTileLineDistance(element.getLocation()) > Config.PC_RECOGNIZE_RANGE) {
//						result.add(element);
//					}
//				}
//			}
//		}
//		return result;
//	}

    //修正
    public ArrayList<L1PcInstance> getVisiblePlayer(L1Object object, int radius) {
        int map = object.getMapId();
        Point pt = object.getLocation();
        ArrayList<L1PcInstance> result = new ArrayList<L1PcInstance>();
        Collection<L1PcInstance> pc = null;
        pc = _allPlayers.values();
        for (L1PcInstance element : pc) {
            if ((element == null) || (element.equals(object)) || (map != element.getMapId()))
                continue;
            if (radius == -1) {
                if (pt.isInScreen(element.getLocation()))
                    result.add(element);
            } else if (radius == 0) {
                if (pt.isSamePoint(element.getLocation())) {
                    result.add(element);
                }
            } else if (pt.getTileLineDistance(element.getLocation()) <= radius) {
                result.add(element);
            }
        }

        return result;
    }


    public ArrayList<L1PcInstance> getVisiblePlayerExceptTargetSight(L1Object object, L1Object target) {
        int map = object.getMapId();
        Point objectPt = object.getLocation();
        Point targetPt = target.getLocation();
        ArrayList<L1PcInstance> result = new ArrayList<L1PcInstance>();

        for (L1Object targetObject : _visibleObjects[map].values()) {
            if (!(targetObject instanceof L1PcInstance)) {
                continue;
            }

            L1PcInstance element = (L1PcInstance) (targetObject);

            if (element == null || element.equals(object)) {
                continue;
            }

            if (Config.PC_RECOGNIZE_RANGE == -1) {
                if ((objectPt.isInScreen(element.getLocation())) && (!targetPt.isInScreen(element.getLocation()))) {
                    result.add(element);
                }
            } else if ((objectPt.getTileLineDistance(element.getLocation()) <= Config.PC_RECOGNIZE_RANGE) &&
                    (targetPt.getTileLineDistance(element.getLocation()) > Config.PC_RECOGNIZE_RANGE)) {
                result.add(element);
            }
        }
        return result;
    }


    /**
     * objectを認識することができる範囲にあるプレーヤーを取得する
     *
     * @param object
     * @return
     */
    public ArrayList<L1PcInstance> getRecognizePlayer(L1Object object) {
        return getVisiblePlayer(object, Config.PC_RECOGNIZE_RANGE);
    }

    public L1PcInstance[] getAllPlayers3() {
        return _allPlayers.values().toArray(new L1PcInstance[_allPlayers.size()]);
    }

    private Collection<L1PcInstance> _allPlayerValues;

    public Collection<L1PcInstance> getAllPlayers() {
        Collection<L1PcInstance> vs = _allPlayerValues;
        return (vs != null) ? vs : (_allPlayerValues = Collections.unmodifiableCollection(_allPlayers.values()));
    }

    public Collection<L1RobotInstance> getAllRobot() {
        return Collections.unmodifiableCollection(_allRobot.values());
    }

    public Collection<L1NpcShopInstance> getAllNpcShop() {
        return Collections.unmodifiableCollection(_allShopNpc.values());
    }

    public Collection<L1GuardInstance> getAllGuard() {
        return Collections.unmodifiableCollection(_allGuard.values());
    }

    public Collection<L1CastleGuardInstance> getAllCastleGuard() {
        return Collections.unmodifiableCollection(_allCastleGuard.values());
    }

    /**
     * ワールド内にある指定された名前のプレイヤーを取得する。
     *
     * @param name -
     *             プレイヤー名（小文字・大文字は無視される）
     * @return 指定された名前のL1PcInstance。そのプレイヤーが存在しない場合はnullを返す。
     */
    public L1PcInstance getPlayer(String name) {
        if (null == name) return null;
        return _allPlayers.get(name.toUpperCase());
    }

    /**
     * ワールド内にある指定された名前の無人NPC商店を取得する。
     *
     * @param name -
     *             無人NPC店名（小文字・大文字は無視される）
     * @return 指定された名前のL1ShopNpcInstance。このマネキンが存在しない場合はnullを返す。
     */
    public L1NpcShopInstance getShopNpc(String name) {
        Collection<L1NpcShopInstance> npc = null;
        npc = getAllShopNpc();
        for (L1NpcShopInstance each : npc) {
            if (each == null) continue;
            if (each.getName().equalsIgnoreCase(name)) {
                return each;
            }
        }
        return null;
    }

    // _allShopNpcのビュー
    private Collection<L1NpcShopInstance> _allShopNpcValues;

    public Collection<L1NpcShopInstance> getAllShopNpc() {
        Collection<L1NpcShopInstance> vs = _allShopNpcValues;
        return (vs != null) ? vs : (_allShopNpcValues = Collections.unmodifiableCollection(_allShopNpc.values()));
    }


    // _allPetsのビュー
    private Collection<L1PetInstance> _allPetValues;

    public Collection<L1PetInstance> getAllPets() {
        Collection<L1PetInstance> vs = _allPetValues;
        return (vs != null) ? vs : (_allPetValues = Collections.unmodifiableCollection(_allPets.values()));
    }

    // _allSummonsのビュー
    private Collection<L1SummonInstance> _allSummonValues;

    public Collection<L1SummonInstance> getAllSummons() {
        Collection<L1SummonInstance> vs = _allSummonValues;
        return (vs != null) ? vs : (_allSummonValues = Collections
                .unmodifiableCollection(_allSummons.values()));
    }

    public final Map<Integer, L1Object> getVisibleObjects(int mapId) {
        return _visibleObjects[mapId];
    }

    public void addWar(L1War war) {
        if (!_allWars.contains(war)) {
            _allWars.add(war);
        }
    }

    public void removeWar(L1War war) {
        if (_allWars.contains(war)) {
            _allWars.remove(war);
        }
    }

    // 追加
    public L1War[] get_wars() {
        return _allWars.toArray(new L1War[_allWars.size()]);
    }

    // _allWarsのビュー
    private List<L1War> _allWarList;

    public List<L1War> getWarList() {
        List<L1War> vs = _allWarList;
        return (vs != null) ? vs : (_allWarList = Collections
                .unmodifiableList(_allWars));
    }

    public void storeClan(L1Clan clan) {
        L1Clan temp = getClan(clan.getClanId());
        if (temp == null) {
            _allClansById.put(clan.getClanId(), clan);
            _allClans.put(clan.getClanName(), clan);
        }
    }

    public void removeClan(L1Clan clan) {
        L1Clan temp = getClan(clan.getClanId());
        if (temp != null) {
            _allClansById.remove(clan.getClanId());
            _allClans.remove(clan.getClanName());
        }
    }

    public L1Clan getClan(String clan_name) {
        return _allClans.get(clan_name);
    }

    public L1Clan getClan(int clanId) {
        return _allClansById.get(clanId);
    }

    // _allClansのビュー
    private Collection<L1Clan> _allClanValues;

    public Collection<L1Clan> getAllClans() {
        Collection<L1Clan> vs = _allClanValues;
        return (vs != null) ? vs : (_allClanValues = Collections
                .unmodifiableCollection(_allClans.values()));
    }

    public void setWeather(int weather) {
        _weather = weather;
    }

    public int getWeather() {
        return _weather;
    }

    public void set_worldChatElabled(boolean flag) {
        _worldChatEnabled = flag;
    }

    public boolean isWorldChatElabled() {
        return _worldChatEnabled;
    }

    public void setProcessingContributionTotal(boolean flag) {
        _processingContributionTotal = flag;
    }

    public boolean isProcessingContributionTotal() {
        return _processingContributionTotal;
    }

    public L1Object[] getObject2() {
        return _allObjects.values().toArray(new L1Object[_allObjects.size()]);
    }

    /**
     * ワールド上に存在するすべてのプレイヤーにパケットを送信する。
     *
     * @param packet 送信するパケットを示すServerBasePacketオブジェクト。
     */
    public void broadcastPacketToAll(ServerBasePacket packet) {
        _log.finest("players to notify : " + getAllPlayers().size());
        for (L1PcInstance pc : getAllPlayers()) {
            if (pc != null)
                pc.sendPackets(packet);
        }
    }

    public void broadcastPacketToAll2(ServerBasePacket packet) {
        for (L1PcInstance pc : getAllPlayers()) {
            if (pc != null) {
                pc.sendPackets(packet);
                pc.isSiege = true;
            }
        }
    }

    public void broadcastPacketToAll(ServerBasePacket packet, boolean clear) {
        Collection<L1PcInstance> pclist = null;
        pclist = getAllPlayers();
        _log.finest("players to notify : " + pclist.size());
        for (L1PcInstance pc : pclist) {
            if (pc != null)
                pc.sendPackets(packet);
        }
        if (clear) {
            packet.clear();
            packet = null;
        }
    }

    /**
     * ワールド上に存在するすべてのプレイヤーにサーバーメッセージを送信する。
     *
     * @param message 送信するメッセージ
     */
    public void broadcastServerMessage(String message) {
        broadcastPacketToAll(new S_SystemMessage(message));
    }

    public L1NpcInstance findNpc(int id) {
        return _allNpc.get(id);
    }

    private Collection<L1ItemInstance> _allItemValues;

    public Collection<L1ItemInstance> getAllItem() {
        Collection<L1ItemInstance> vs = _allItemValues;
        return (vs != null) ? vs : (_allItemValues = Collections
                .unmodifiableCollection(_allitem.values()));
    }

}