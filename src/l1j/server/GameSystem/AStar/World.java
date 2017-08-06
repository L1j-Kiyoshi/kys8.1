package l1j.server.GameSystem.AStar;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import l1j.server.GameSystem.AStar.Share.TimeLine;
import l1j.server.server.model.map.L1V1Map;
import l1j.server.server.model.map.L1WorldMap;

public final class World {

    static private Map<Integer, l1j.server.GameSystem.AStar.bean.Map> list;

    static public void init() {
        TimeLine.start("ワールドマップの読み込み....No.");

        list = new HashMap<Integer, l1j.server.GameSystem.AStar.bean.Map>();

        try {
            File f = new File("Sabu/maps/Cache");
            // フォルダが存在する場合
            if (f.isDirectory()) {
                // キャッシュファイルからマップの読み込み
                read(false);
                // フォルダが存在しない場合
            } else {
                System.out.println("キャッシュフォルダが存在しません。");
                // フォルダの作成
                f.mkdir();
                // txtファイルからマップの読み込み
                read(true);
                // キャッシュファイルの作成
                writeCache();
            }
        } catch (Exception e) {
            System.out.printf("%s : init()\r\n", World.class.toString());
            System.out.println(e);
        }

        TimeLine.end();
    }

    static private void writeCache() throws Exception {
        try {

            System.out.println("キャッシュファイルを作成しています。");
            BufferedOutputStream bw = null;
            for (l1j.server.GameSystem.AStar.bean.Map m : list.values()) {
                bw = new BufferedOutputStream(new FileOutputStream(
                        String.format("Sabu/maps/Cache/%d.data", +m.mapid)));
                bw.write(m.data);
                bw.close();
            }
            System.out.println(" (完了)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static private void read(boolean type) throws Exception {
        // textから読むて通知用
        try {

            if (type)
                System.out.println("Textファイルからワールドマップ情報を抽出しています。");

            String maps;
            StringTokenizer st1;
            BufferedReader lnrr = new BufferedReader(new FileReader(
                    "Sabu/maps/maps.csv"));
            byte[] temp = new byte[22149121];
            while ((maps = lnrr.readLine()) != null) {
                if (maps.startsWith("#")) {
                    continue;
                }
                st1 = new StringTokenizer(maps, ",");
                int readID = Integer.parseInt(st1.nextToken());
                int x1 = Integer.parseInt(st1.nextToken());
                int x2 = Integer.parseInt(st1.nextToken());
                int y1 = Integer.parseInt(st1.nextToken());
                int y2 = Integer.parseInt(st1.nextToken());
                int size = Integer.parseInt(st1.nextToken());
                if (type)
                    readText(temp, readID, size, x1, x2, y1, y2);
                else
                    readCache(readID, x1, x2, y1, y2, size);
            }
            temp = null;
            st1 = null;
            lnrr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static private void readText(byte[] temp, int readID, int size, int x1,
                                 int x2, int y1, int y2) throws Exception {

        int TotalSize = -1;
        String line;
        StringTokenizer st = null;

        BufferedReader lnr = new BufferedReader(new FileReader(String.format(
                "Sabu/maps/Text/%d.txt", readID)));
        while ((line = lnr.readLine()) != null) {

            st = new StringTokenizer(line, ",");

            for (int i = 0; i < size; ++i) {
                int t = 0;
                try {
                    t = Integer.parseInt(st.nextToken());
                } catch (Exception e) {
                    System.out.println(readID);
                }
                if (Byte.MAX_VALUE < t) {
                    temp[++TotalSize] = Byte.MAX_VALUE;
                } else {
                    temp[++TotalSize] = (byte) t;
                }
            }
        }
        byte[] MAP = new byte[TotalSize - 1];
        try {
            System.arraycopy(temp, 0, MAP, 0, MAP.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        l1j.server.GameSystem.AStar.bean.Map m = new l1j.server.GameSystem.AStar.bean.Map();
        m.mapid = readID;
        m.locX1 = x1;
        m.locX2 = x2;
        m.locY1 = y1;
        m.locY2 = y2;
        m.size = size;
        m.data = MAP;
        m.data_size = m.data.length;
        m.dataDynamic = new byte[m.data_size];
        m.isdoor = new boolean[m.data_size][8];
        list.put(m.mapid, m);
        try {
            lnr.close();
        } catch (Exception e) {
        }
    }

    static private void readCache(int readID, int x1, int x2, int y1, int y2,
                                  int size) throws Exception {
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
                String.format("Sabu/maps/Cache/%d.data", readID)));
        byte[] data = new byte[bis.available()];
        bis.read(data, 0, data.length);
        l1j.server.GameSystem.AStar.bean.Map m = new l1j.server.GameSystem.AStar.bean.Map();
        m.mapid = readID;
        m.locX1 = x1;
        m.locX2 = x2;
        m.locY1 = y1;
        m.locY2 = y2;
        m.size = size;
        m.data = data;
        m.data_size = data.length;
        // System.out.println("x1>"+x1+" x2>"+x2+" y1>"+y1+" y2>"+y2+" > "+m.data_size);
        m.dataDynamic = new byte[m.data_size];
        m.isdoor = new boolean[m.data_size][8];
        list.put(m.mapid, m);
        bis.close();
        bis = null;
    }

    /*
     * static public l1j.server.GameSystem.Astar.bean.Map get_map(int map){
     * return list.get(map); }
     */
    static public boolean get_map(int map) {
        return L1WorldMap.getInstance().getMapCK((short) map);
    }

    static public void cloneMap(int targetId, int newId) {
        /*
         * l1j.server.GameSystem.Astar.bean.Map m = new
		 * l1j.server.GameSystem.Astar.bean.Map(); //m = list.get(targetId);
		 * //m.mapid = newId;
		 *
		 * l1j.server.GameSystem.Astar.bean.Map ori_map = list.get(targetId);
		 * m.data = new byte[ori_map.data_size]; for (int i = 0; i <
		 * ori_map.data_size; i++) { m.data[i] = ori_map.data[i]; } m.mapid =
		 * newId; m.locX1 = ori_map.locX1; m.locX2 = ori_map.locX2; m.locY1 =
		 * ori_map.locY1; m.locY2 = ori_map.locY2; m.size = ori_map.size;
		 * m.data_size = ori_map.data_size; m.dataDynamic = new
		 * byte[m.data_size]; m.isdoor = new boolean[m.data_size][8];
		 *
		 * list.put(newId , m);
		 */
    }

    static public void resetMap(int targetId, int resetId) {

		/*
         * l1j.server.GameSystem.Astar.bean.Map ori_map = list.get(targetId);
		 * l1j.server.GameSystem.Astar.bean.Map re_map = list.get(resetId);
		 * if(ori_map == null || re_map == null) return; if(ori_map.data_size !=
		 * re_map.data_size) return;
		 *
		 * for (int i = 0; i < re_map.data_size; i++) { re_map.data[i] =
		 * ori_map.data[i]; } for (int i = 0; i < re_map.data_size; i++) {
		 * re_map.dataDynamic[i] = ori_map.dataDynamic[i]; } for (int i = 0; i <
		 * re_map.data_size; i++) { for(int a = 0; a < 8; a++){
		 * re_map.isdoor[i][a] = ori_map.isdoor[i][a]; } }
		 */
    }

	/*
	 * static public int get_map(int x, int y, int map){
	 * l1j.server.GameSystem.Astar.bean.Map m = get_map(map); if(m!=null){ if(x
	 * < m.locX1) return 0; if(y < m.locY1) return 0; int pos =
	 * ((m.locX2-m.locX1)*(y-m.locY1)) + (x-m.locX1) + (y-m.locY1); return
	 * pos>=m.data_size || pos<0 ? 0 : m.data[ pos ]; } return 0; }
	 */

	/*
	 * static public void set_map(int x, int y, int map, int tail){
	 * l1j.server.GameSystem.Astar.bean.Map m = get_map(map); if(m!=null){ int
	 * pos = ((m.locX2-m.locX1)*(y-m.locY1)) + (x-m.locX1) + (y-m.locY1);
	 * if(pos<m.data_size && pos>=0) m.data[ pos ] = (byte)tail; } }
	 */

	/*
	 * static public void update_mapDynamic(int x, int y, int map, boolean
	 * plus){ l1j.server.GameSystem.Astar.bean.Map m = get_map(map);
	 * if(m!=null){ int pos = ((m.locX2-m.locX1)*(y-m.locY1)) + (x-m.locX1) +
	 * (y-m.locY1); if(pos<m.data_size && pos>=0){ synchronized (m.dataDynamic)
	 * { if(plus){ m.dataDynamic[ pos ] += 1; }else{ m.dataDynamic[ pos ] -= 1;
	 * if(m.dataDynamic[ pos ] < 0) m.dataDynamic[ pos ] = 0; } } } } }
	 */

    private static final byte BITFLAG_IS_DOOR_IMPASSABLE_X = (byte) 0x80;
    private static final byte BITFLAG_IS_DOOR_IMPASSABLE_Y = (byte) 0x40;

    static public void moveDoor(int x, int y, int map, boolean h, boolean flag) {
        L1V1Map m = (L1V1Map) L1WorldMap.getInstance().getMap((short) map);
        if (m != null) {
            if (!m.isInMap(x, y)) {
                return;
            }
            if (flag) {
                synchronized (m._doorMap) {
                    m._doorMap[x - m.getX()][y - m.getY()] = 0;
                }
            } else {
                byte setBit = BITFLAG_IS_DOOR_IMPASSABLE_Y;
                if (h) {
                    setBit = BITFLAG_IS_DOOR_IMPASSABLE_X;
                }
                synchronized (m._doorMap) {
                    m._doorMap[x - m.getX()][y - m.getY()] = setBit;
                }
            }
        }
		/*
		 * l1j.server.GameSystem.Astar.bean.Map m = get_map(map); if(m!=null){
		 * int pos = ((m.locX2-m.locX1)*(y-m.locY1)) + (x-m.locX1) +
		 * (y-m.locY1); if(pos<m.data_size && pos>=0){ synchronized (m.isdoor) {
		 * m.isdoor[pos][h]=flag; } } }
		 */
    }

    private static final byte HEADING_TABLE_X[] = { 0, 1, 1, 1, 0, -1, -1, -1 };
    private static final byte HEADING_TABLE_Y[] = { -1, -1, 0, 1, 1, 1, 0, -1 };

    static public boolean moveDoor(int x, int y, int map, int h) {
        if (h < 0 || h > 7) {
            return false;
        }

        L1V1Map m = (L1V1Map) L1WorldMap.getInstance().getMap((short) map);
        if (m != null) {
            int newX = x + HEADING_TABLE_X[h];
            int newY = y + HEADING_TABLE_Y[h];
            if (x > newX) {
                int doorTile1 = accessDoorTile(newX, y, m);
                int doorTile2 = accessDoorTile(newX, newY, m);

                if (((doorTile1 & BITFLAG_IS_DOOR_IMPASSABLE_X) != 0)
                        || ((doorTile2 & BITFLAG_IS_DOOR_IMPASSABLE_X) != 0))
                    return true;
            } else if (x < newX) {
                int doorTileOld = accessDoorTile(x, y, m);
                int doorTileNew = accessDoorTile(newX - 1, newY, m);

                if (((doorTileOld & BITFLAG_IS_DOOR_IMPASSABLE_X) != 0)
                        || ((doorTileNew & BITFLAG_IS_DOOR_IMPASSABLE_X) != 0))
                    return true;
            }

            if (y < newY) {
                int doorTile1 = accessDoorTile(x, newY, m);
                int doorTile2 = accessDoorTile(newX, newY, m);

                if (((doorTile1 & BITFLAG_IS_DOOR_IMPASSABLE_Y) != 0)
                        || ((doorTile2 & BITFLAG_IS_DOOR_IMPASSABLE_Y) != 0))
                    return true;
            } else if (y > newY) {
                int doorTileOld = accessDoorTile(x, y, m);
                int doorTileNew = accessDoorTile(newX, newY + 1, m);

                if (((doorTileOld & BITFLAG_IS_DOOR_IMPASSABLE_Y) != 0)
                        || ((doorTileNew & BITFLAG_IS_DOOR_IMPASSABLE_Y) != 0))
                    return true;
            }
        }
		/*
		 * l1j.server.GameSystem.Astar.bean.Map m = get_map(map); if(m!=null){
		 * int pos = ((m.locX2-m.locX1)*(y-m.locY1)) + (x-m.locX1) +
		 * (y-m.locY1); if(pos<m.data_size && pos>=0) synchronized (m.isdoor) {
		 * return m.isdoor[pos][h]==true; } }
		 */
        // }
        return false;
    }

    private static int accessDoorTile(int x, int y, L1V1Map m) {
        if (!m.isInMap(x, y)) {
            return 0;
        }
        synchronized (m._doorMap) {
            return m._doorMap[x - m.getX()][y - m.getY()];
        }
    }

    static public boolean isMapdynamic(int x, int y, int map) {
        L1V1Map m = (L1V1Map) L1WorldMap.getInstance().getMap((short) map);
        return !m.isPassable(x, y);
		/*
		 * l1j.server.GameSystem.Astar.bean.Map m = get_map(map); if(m!=null){
		 * int pos = ((m.locX2-m.locX1)*(y-m.locY1)) + (x-m.locX1) +
		 * (y-m.locY1); if(pos<m.data_size && pos>=0) synchronized
		 * (m.dataDynamic) { return m.dataDynamic[ pos ] != 0; } } return false;
		 */
    }

	/*
	 * static public int getMapdynamic(int x, int y, int map){
	 * l1j.server.GameSystem.Astar.bean.Map m = get_map(map); if(m!=null){ int
	 * pos = ((m.locX2-m.locX1)*(y-m.locY1)) + (x-m.locX1) + (y-m.locY1);
	 * if(pos<m.data_size && pos>=0) synchronized (m.dataDynamic) { return
	 * m.dataDynamic[ pos ]; } } return 0; }
	 */

	/*
	 * static public int getZone(int x, int y, int map){ return get_map(x, y,
	 * map) & 48; }
	 */

    static public boolean isThroughObject(int x, int y, int map, int dir) {
        L1V1Map m = (L1V1Map) L1WorldMap.getInstance().getMap((short) map);
        if (map >= 10010 && map <= 10100) {
            switch (dir) {
                case 0:
                    if (m.accessTile(x, y - 1) != 0) {
                        return true;
                    } else {
                        return false;
                    }
                case 1:
                    if (m.accessTile(x + 1, y - 1) != 0) {
                        return true;
                    } else {
                        return false;
                    }
                case 2:
                    if (m.accessTile(x + 1, y) != 0) {
                        return true;
                    } else {
                        return false;
                    }
                case 3:
                    if (m.accessTile(x + 1, y + 1) != 0) {
                        return true;
                    } else {
                        return false;
                    }
                case 4:
                    if (m.accessTile(x, y + 1) != 0) {
                        return true;
                    } else {
                        return false;
                    }
                case 5:
                    if (m.accessTile(x - 1, y + 1) != 0) {
                        return true;
                    } else {
                        return false;
                    }
                case 6:
                    if (m.accessTile(x - 1, y) != 0) {
                        return true;
                    } else {
                        return false;
                    }
                case 7:
                    if (m.accessTile(x - 1, y - 1) != 0) {
                        return true;
                    } else {
                        return false;
                    }
            }
        }
        // return m.isPassable(x, y, dir);
        switch (dir) {
            case 0:
                return (m.accessTile(x, y) & 2) > 0;
            case 1:
                return ((m.accessTile(x, y) & 2) > 0 && (m.accessTile(x, y - 1) & 1) > 0)
                        || ((m.accessTile(x, y) & 1) > 0 && (m.accessTile(x + 1, y) & 2) > 0);
            case 2:
                return (m.accessTile(x, y) & 1) > 0;
            case 3:
                return ((m.accessTile(x, y + 1) & 2) > 0 && (m.accessTile(x, y + 1) & 1) > 0)
                        || ((m.accessTile(x, y) & 1) > 0 && (m.accessTile(x + 1,
                        y + 1) & 2) > 0);
            case 4:
                return (m.accessTile(x, y + 1) & 2) > 0;
            case 5:
                return ((m.accessTile(x, y + 1) & 2) > 0 && (m.accessTile(x - 1,
                        y + 1) & 1) > 0)
                        || ((m.accessTile(x - 1, y) & 1) > 0 && (m.accessTile(
                        x - 1, y + 1) & 2) > 0);
            case 6:
                return (m.accessTile(x - 1, y) & 1) > 0;
            case 7:
                return ((m.accessTile(x, y) & 2) > 0 && (m.accessTile(x - 1, y - 1) & 1) > 0)
                        || ((m.accessTile(x - 1, y) & 1) > 0 && (m.accessTile(
                        x - 1, y) & 2) > 0);
            default:
                break;
        }
        return false;

		/*
		 * switch(dir){ case 0: return (get_map(x, y, map)&2)>0; case 1: return
		 * ((get_map(x, y, map)&2)>0 && (get_map(x, y-1, map)&1)>0) ||
		 * ((get_map(x, y, map)&1)>0 && (get_map(x+1, y, map)&2)>0) ; case 2:
		 * return (get_map(x, y, map)&1)>0; case 3: return ((get_map(x, y+1,
		 * map)&2)>0 && (get_map(x, y+1, map)&1)>0) || ((get_map(x, y, map)&1)>0
		 * && (get_map(x+1, y+1, map)&2)>0); case 4: return (get_map(x, y+1,
		 * map)&2)>0; case 5: return ((get_map(x, y+1, map)&2)>0 &&
		 * (get_map(x-1, y+1, map)&1)>0) || ((get_map(x-1, y, map)&1)>0 &&
		 * (get_map(x-1, y+1, map)&2)>0); case 6: return (get_map(x-1, y,
		 * map)&1) > 0; case 7: return ((get_map(x, y, map)&2)>0 &&
		 * (get_map(x-1, y-1, map)&1)>0) || ((get_map(x-1, y, map)&1)>0 &&
		 * (get_map(x-1, y, map)&2)>0); } return false;
		 */
    }

    static public boolean isThroughAttack(int x, int y, int map, int dir) {
        L1V1Map m = (L1V1Map) L1WorldMap.getInstance().getMap((short) map);
        return m.isArrowPassable(x, y, dir);
		/*
		 * int gab =
		 * L1WorldMap.getInstance().getMap((short)map).getOriginalTile(x, y);
		 * int gab2; switch(dir){ case 0: gab2 =
		 * L1WorldMap.getInstance().getMap((short)map).getOriginalTile(x, y-1);
		 * break; case 1: gab2 =
		 * L1WorldMap.getInstance().getMap((short)map).getOriginalTile(x+1,
		 * y-1);break; case 2: gab2 =
		 * L1WorldMap.getInstance().getMap((short)map).getOriginalTile(x+1,
		 * y);break; case 3: gab2 =
		 * L1WorldMap.getInstance().getMap((short)map).getOriginalTile(x+1,
		 * y+1);break; case 4: gab2 =
		 * L1WorldMap.getInstance().getMap((short)map).getOriginalTile(x,
		 * y+1);break; case 5: gab2 =
		 * L1WorldMap.getInstance().getMap((short)map).getOriginalTile(x-1,
		 * y+1);break; case 6: gab2 =
		 * L1WorldMap.getInstance().getMap((short)map).getOriginalTile(x-1,
		 * y);break; case 7: gab2 =
		 * L1WorldMap.getInstance().getMap((short)map).getOriginalTile(x-1,
		 * y-1);break; default: return false; }
		 *
		 * if(World.moveDoor(x, y, map, dir)){ return false; }
		 *
		 *
		 * if (gab == 12 || gab2== 12){ return true; }else if (gab == 47 ||
		 * gab2== 47){ return true; }
		 *
		 * switch(dir){ case 0: return (get_map(x, y, map)&8)>0; case 1: return
		 * ((get_map(x, y, map)&8)>0 && (get_map(x, y-1, map)&4)>0) ||
		 * ((get_map(x, y, map)&4)>0 && (get_map(x+1, y, map)&8)>0); case 2:
		 * return (get_map(x, y, map)&4)>0; case 3: return ((get_map(x, y+1,
		 * map)&8)>0 && (get_map(x, y+1, map)&4)>0) || ((get_map(x, y, map)&4)>0
		 * && (get_map(x+1, y+1, map)&8)>0); case 4: return (get_map(x, y+1,
		 * map)&8)>0; case 5: return ((get_map(x, y+1, map)&8)>0 &&
		 * (get_map(x-1, y+1, map)&4)>0) || ((get_map(x-1, y, map)&4)>0 &&
		 * (get_map(x-1, y+1, map)&8)>0); case 6: return (get_map(x-1, y,
		 * map)&4) > 0; case 7: return ((get_map(x, y, map)&8)>0 &&
		 * (get_map(x-1, y-1, map)&4)>0) || ((get_map(x-1, y, map)&4)>0 &&
		 * (get_map(x-1, y, map)&8)>0); } return false;
		 */
    }

}
