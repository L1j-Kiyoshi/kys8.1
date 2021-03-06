package l1j.server.server.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import l1j.server.server.datatables.CastleTable;
import l1j.server.server.model.gametime.L1GameTime;
import l1j.server.server.model.gametime.L1GameTimeAdapter;
import l1j.server.server.model.gametime.L1GameTimeClock;
import l1j.server.server.templates.L1Castle;

// Referenced classes of package l1j.server.server.model:
// L1CastleLocation

public class L1CastleLocation {

    // castle_id
    public static final int KENT_CASTLE_ID = 1;

    public static final int OT_CASTLE_ID = 2;

    public static final int WW_CASTLE_ID = 3;

    public static final int GIRAN_CASTLE_ID = 4;

    public static final int HEINE_CASTLE_ID = 5;

    public static final int DOWA_CASTLE_ID = 6;

    public static final int ADEN_CASTLE_ID = 7;

    public static final int DIAD_CASTLE_ID = 8;

    // →↑が、X軸、→↓このY軸
    // ケント城
    private static final int KENT_TOWER_X = 33168; // 33170//33139

    private static final int KENT_TOWER_Y = 32779; // 32774//32768

    private static final short KENT_TOWER_MAP = 4;

    private static final int KENT_X1 = 33089;

    private static final int KENT_X2 = 33219;

    private static final int KENT_Y1 = 32717;

    private static final int KENT_Y2 = 32827;

    private static final short KENT_MAP = 4;

    private static final short KENT_INNER_CASTLE_MAP = 15;

    // オーク性
    private static final int OT_TOWER_X = 32798;

    private static final int OT_TOWER_Y = 32285;

    private static final short OT_TOWER_MAP = 4;

    private static final int OT_X1 = 32750;

    private static final int OT_X2 = 32850;

    private static final int OT_Y1 = 32250;

    private static final int OT_Y2 = 32350;

    private static final short OT_MAP = 4;

    // ウィンダウッド
    private static final int WW_TOWER_X = 32623;

    private static final int WW_TOWER_Y = 33379;

    private static final short WW_TOWER_MAP = 4;

    private static final int WW_X1 = 32571;

    private static final int WW_X2 = 32721;

    private static final int WW_Y1 = 33350;

    private static final int WW_Y2 = 33460;

    private static final short WW_MAP = 4;

    private static final short WW_INNER_CASTLE_MAP = 29;

    // ギラン
    private static final int GIRAN_TOWER_X = 33631;

    private static final int GIRAN_TOWER_Y = 32678;

    private static final short GIRAN_TOWER_MAP = 4;

    private static final int GIRAN_X1 = 33559;

    private static final int GIRAN_X2 = 33686;

    private static final int GIRAN_Y1 = 32615;

    private static final int GIRAN_Y2 = 32755;

    private static final short GIRAN_MAP = 4;

    private static final short GIRAN_INNER_CASTLE_MAP = 52;

    // ハイネ
    private static final int HEINE_TOWER_X = 33524;

    private static final int HEINE_TOWER_Y = 33396;

    private static final short HEINE_TOWER_MAP = 4;

    private static final int HEINE_X1 = 33458;

    private static final int HEINE_X2 = 33583;

    private static final int HEINE_Y1 = 33315;

    private static final int HEINE_Y2 = 33490;

    private static final short HEINE_MAP = 4;

    private static final short HEINE_INNER_CASTLE_MAP = 64;

    // ドワーフ
    private static final int DOWA_TOWER_X = 32828;

    private static final int DOWA_TOWER_Y = 32818;

    private static final short DOWA_TOWER_MAP = 66;

    private static final int DOWA_X1 = 32755;

    private static final int DOWA_X2 = 32870;

    private static final int DOWA_Y1 = 32790;

    private static final int DOWA_Y2 = 32920;

    private static final short DOWA_MAP = 66;

    // アデン
    private static final int ADEN_TOWER_X = 34090;

    private static final int ADEN_TOWER_Y = 33260;

    private static final short ADEN_TOWER_MAP = 4;

    private static final int ADEN_X1 = 34007;

    private static final int ADEN_X2 = 34162;

    private static final int ADEN_Y1 = 33172;

    private static final int ADEN_Y2 = 33332;

    private static final short ADEN_MAP = 4;

    private static final short ADEN_INNER_CASTLE_MAP = 300;

    private static final int ADEN_SUB_TOWER1_X = 34057;

    private static final int ADEN_SUB_TOWER1_Y = 33291;

    private static final int ADEN_SUB_TOWER2_X = 34123;

    private static final int ADEN_SUB_TOWER2_Y = 33291;

    private static final int ADEN_SUB_TOWER3_X = 34057;

    private static final int ADEN_SUB_TOWER3_Y = 33230;

    private static final int ADEN_SUB_TOWER4_X = 34123;

    private static final int ADEN_SUB_TOWER4_Y = 33230;

    // ディアド要塞
    private static final int DIAD_TOWER_X = 33033;

    private static final int DIAD_TOWER_Y = 32895;

    private static final short DIAD_TOWER_MAP = 320;

    private static final int DIAD_X1 = 32888;

    private static final int DIAD_X2 = 33070;

    private static final int DIAD_Y1 = 32839;

    private static final int DIAD_Y2 = 32953;

    private static final short DIAD_MAP = 320;

    private static final short DIAD_INNER_CASTLE_MAP = 330;

    private static final Map<Integer, L1Location> _towers = new HashMap<Integer, L1Location>();

    static {
        _towers.put(KENT_CASTLE_ID, new L1Location(KENT_TOWER_X, KENT_TOWER_Y, KENT_TOWER_MAP));
        _towers.put(OT_CASTLE_ID, new L1Location(OT_TOWER_X, OT_TOWER_Y, OT_TOWER_MAP));
        _towers.put(WW_CASTLE_ID, new L1Location(WW_TOWER_X, WW_TOWER_Y, WW_TOWER_MAP));
        _towers.put(GIRAN_CASTLE_ID, new L1Location(GIRAN_TOWER_X, GIRAN_TOWER_Y, GIRAN_TOWER_MAP));
        _towers.put(HEINE_CASTLE_ID, new L1Location(HEINE_TOWER_X, HEINE_TOWER_Y, HEINE_TOWER_MAP));
        _towers.put(DOWA_CASTLE_ID, new L1Location(DOWA_TOWER_X, DOWA_TOWER_Y, DOWA_TOWER_MAP));
        _towers.put(ADEN_CASTLE_ID, new L1Location(ADEN_TOWER_X, ADEN_TOWER_Y, ADEN_TOWER_MAP));
        _towers.put(DIAD_CASTLE_ID, new L1Location(DIAD_TOWER_X, DIAD_TOWER_Y, DIAD_TOWER_MAP));
    }

    private static final Map<Integer, L1MapArea> _areas = new HashMap<Integer, L1MapArea>();

    static {
        _areas.put(KENT_CASTLE_ID, new L1MapArea(KENT_X1, KENT_Y1, KENT_X2, KENT_Y2, KENT_MAP));
        _areas.put(OT_CASTLE_ID, new L1MapArea(OT_X1, OT_Y1, OT_X2, OT_Y2, OT_MAP));
        _areas.put(WW_CASTLE_ID, new L1MapArea(WW_X1, WW_Y1, WW_X2, WW_Y2, WW_MAP));
        _areas.put(GIRAN_CASTLE_ID, new L1MapArea(GIRAN_X1, GIRAN_Y1, GIRAN_X2, GIRAN_Y2, GIRAN_MAP));
        _areas.put(HEINE_CASTLE_ID, new L1MapArea(HEINE_X1, HEINE_Y1, HEINE_X2, HEINE_Y2, HEINE_MAP));
        _areas.put(DOWA_CASTLE_ID, new L1MapArea(DOWA_X1, DOWA_Y1, DOWA_X2, DOWA_Y2, DOWA_MAP));
        _areas.put(ADEN_CASTLE_ID, new L1MapArea(ADEN_X1, ADEN_Y1, ADEN_X2, ADEN_Y2, ADEN_MAP));
        _areas.put(DIAD_CASTLE_ID, new L1MapArea(DIAD_X1, DIAD_Y1, DIAD_X2, DIAD_Y2, DIAD_MAP));
    }

    private static final Map<Integer, Integer> _innerTowerMaps = new HashMap<Integer, Integer>();

    static {
        _innerTowerMaps.put(KENT_CASTLE_ID, (int) KENT_INNER_CASTLE_MAP);
        _innerTowerMaps.put(WW_CASTLE_ID, (int) WW_INNER_CASTLE_MAP);
        _innerTowerMaps.put(GIRAN_CASTLE_ID, (int) GIRAN_INNER_CASTLE_MAP);
        _innerTowerMaps.put(HEINE_CASTLE_ID, (int) HEINE_INNER_CASTLE_MAP);
        _innerTowerMaps.put(ADEN_CASTLE_ID, (int) ADEN_INNER_CASTLE_MAP);
        _innerTowerMaps.put(DIAD_CASTLE_ID, (int) DIAD_INNER_CASTLE_MAP);
    }

    private static final Map<Integer, L1Location> _subTowers = new HashMap<Integer, L1Location>();

    static {
        _subTowers.put(1, new L1Location(ADEN_SUB_TOWER1_X, ADEN_SUB_TOWER1_Y, ADEN_TOWER_MAP));
        _subTowers.put(2, new L1Location(ADEN_SUB_TOWER2_X, ADEN_SUB_TOWER2_Y, ADEN_TOWER_MAP));
        _subTowers.put(3, new L1Location(ADEN_SUB_TOWER3_X, ADEN_SUB_TOWER3_Y, ADEN_TOWER_MAP));
        _subTowers.put(4, new L1Location(ADEN_SUB_TOWER4_X, ADEN_SUB_TOWER4_Y, ADEN_TOWER_MAP));
    }

    private L1CastleLocation() {
    }

    public static int getCastleId(L1Location loc) {
        for (Map.Entry<Integer, L1Location> entry : _towers.entrySet()) {
            if (entry.getValue().equals(loc)) {
                return entry.getKey();
            }
        }
        return 0;
    }

    /**
     * ガーディアンタワー、クラウンの座標からcastle_idを返す
     */
    public static int getCastleId(int locx, int locy, short mapid) {
        return getCastleId(new L1Location(locx, locy, mapid));
    }

    public static int getCastleIdByArea(L1Location loc) {
        for (Map.Entry<Integer, L1MapArea> entry : _areas.entrySet()) {
            if (entry.getValue().contains(loc)) {
                return entry.getKey();
            }
        }
        for (Map.Entry<Integer, Integer> entry : _innerTowerMaps.entrySet()) {
            if (entry.getValue() == loc.getMapId()) {
                return entry.getKey();
            }
        }
        return 0;
    }

    /**
     * 指定された城の戦争エリア（機内）にあるか返す
     */
    public static int getCastleIdByArea(L1Character cha) {
        return getCastleIdByArea(cha.getLocation());
    }

    public static boolean checkInWarArea(int castleId, L1Location loc) {
        return castleId == getCastleIdByArea(loc);
    }

    /**
     * いくつかの戦争エリア（機内）かどうかチェック
     */
    public static boolean checkInWarArea(int castleId, L1Character cha) {
        return checkInWarArea(castleId, cha.getLocation());
    }

    public static boolean checkInAllWarArea(L1Location loc) {
        return 0 != getCastleIdByArea(loc);
    }

    /**
     * いくつかの戦争エリア（機内）かどうかチェック
     */
    public static boolean checkInAllWarArea(int locx, int locy, short mapid) {
        return checkInAllWarArea(new L1Location(locx, locy, mapid));
    }

    /**
     * castleIdからガーディアンタワーの座標を返す
     */
    public static int[] getTowerLoc(int castleId) {
        int[] result = new int[3];
        L1Location loc = _towers.get(castleId);
        if (loc != null) {
            result[0] = loc.getX();
            result[1] = loc.getY();
            result[2] = loc.getMapId();
        }
        return result;
    }

    /**
     * castleIdから戦争エリア（機内）の座標を返す
     */
    public static int[] getWarArea(int castleId) {
        int[] loc = new int[5];
        switch (castleId) {
            case KENT_CASTLE_ID:
                loc[0] = KENT_X1;
                loc[1] = KENT_X2;
                loc[2] = KENT_Y1;
                loc[3] = KENT_Y2;
                loc[4] = KENT_MAP;
                break;
            case OT_CASTLE_ID:
                loc[0] = OT_X1;
                loc[1] = OT_X2;
                loc[2] = OT_Y1;
                loc[3] = OT_Y2;
                loc[4] = OT_MAP;
                break;
            case WW_CASTLE_ID:
                loc[0] = WW_X1;
                loc[1] = WW_X2;
                loc[2] = WW_Y1;
                loc[3] = WW_Y2;
                loc[4] = WW_MAP;
                break;
            case GIRAN_CASTLE_ID:
                loc[0] = GIRAN_X1;
                loc[1] = GIRAN_X2;
                loc[2] = GIRAN_Y1;
                loc[3] = GIRAN_Y2;
                loc[4] = GIRAN_MAP;
                break;
            case HEINE_CASTLE_ID:
                loc[0] = HEINE_X1;
                loc[1] = HEINE_X2;
                loc[2] = HEINE_Y1;
                loc[3] = HEINE_Y2;
                loc[4] = HEINE_MAP;
                break;
            case DOWA_CASTLE_ID:
                loc[0] = DOWA_X1;
                loc[1] = DOWA_X2;
                loc[2] = DOWA_Y1;
                loc[3] = DOWA_Y2;
                loc[4] = DOWA_MAP;
                break;
            case ADEN_CASTLE_ID:
                loc[0] = ADEN_X1;
                loc[1] = ADEN_X2;
                loc[2] = ADEN_Y1;
                loc[3] = ADEN_Y2;
                loc[4] = ADEN_MAP;
                break;
            case DIAD_CASTLE_ID:
                loc[0] = DIAD_X1;
                loc[1] = DIAD_X2;
                loc[2] = DIAD_Y1;
                loc[3] = DIAD_Y2;
                loc[4] = DIAD_MAP;
                break;
            default:
                break;
        }
        return loc;
    }

    public static int[] getCastleLoc(int castle_id) { // / castle_idから育て値座標を返す
        int[] loc = new int[3];
        switch (castle_id) {
            case KENT_CASTLE_ID:
                loc[0] = 32731;
                loc[1] = 32810;
                loc[2] = 15;
                break;
            case OT_CASTLE_ID:
                loc[0] = 32800;
                loc[1] = 32277;
                loc[2] = 4;
                break;
            case WW_CASTLE_ID:
                loc[0] = 32730;
                loc[1] = 32814;
                loc[2] = 29;
                break;
            case GIRAN_CASTLE_ID:
                loc[0] = 32724;
                loc[1] = 32827;
                loc[2] = 52;
                break;
            case HEINE_CASTLE_ID:
                loc[0] = 32568;
                loc[1] = 32855;
                loc[2] = 64;
                break;
            case DOWA_CASTLE_ID:
                loc[0] = 32853;
                loc[1] = 32810;
                loc[2] = 66;
                break;
            case ADEN_CASTLE_ID:
                loc[0] = 32892;
                loc[1] = 32572;
                loc[2] = 300;
                break;
            case DIAD_CASTLE_ID:
                loc[0] = 32733;
                loc[1] = 32985;
                loc[2] = 330;
                break;
            default:
                break;
        }
        return loc;
    }

    /*
     * castle_idから帰還先の座標をランダムに返す
     */
    public static int[] getGetBackLoc(int castle_id) {
        int[] loc;
        switch (castle_id) {
            case KENT_CASTLE_ID:
                loc = L1TownLocation.getGetBackLoc(L1TownLocation.TOWNID_KENT);
                break;
            case OT_CASTLE_ID:
                loc = L1TownLocation.getGetBackLoc(L1TownLocation.TOWNID_ORCISH_FOREST);
                break;
            case WW_CASTLE_ID:
                loc = L1TownLocation.getGetBackLoc(L1TownLocation.TOWNID_WINDAWOOD);
                break;
            case GIRAN_CASTLE_ID:
                loc = L1TownLocation.getGetBackLoc(L1TownLocation.TOWNID_GIRAN);
                break;
            case HEINE_CASTLE_ID:
                loc = L1TownLocation.getGetBackLoc(L1TownLocation.TOWNID_HEINE);
                break;
            case DOWA_CASTLE_ID:
                loc = L1TownLocation.getGetBackLoc(L1TownLocation.TOWNID_WERLDAN);
                break;
            case ADEN_CASTLE_ID:
                loc = L1TownLocation.getGetBackLoc(L1TownLocation.TOWNID_ADEN);
                break;
            case DIAD_CASTLE_ID:
                // ディアド要塞の帰還先は未調査
                Random random = new Random(System.nanoTime());
                int rnd = random.nextInt(3);
                loc = new int[3];
                if (rnd == 0) {
                    loc[0] = 32792;
                    loc[1] = 32807;
                    loc[2] = 310;
                } else if (rnd == 1) {
                    loc[0] = 32816;
                    loc[1] = 32820;
                    loc[2] = 310;
                } else if (rnd == 2) {
                    loc[0] = 32823;
                    loc[1] = 32797;
                    loc[2] = 310;
                }
                break;
            default:
                loc = L1TownLocation.getGetBackLoc(L1TownLocation.TOWNID_SILVER_KNIGHT_TOWN);
                break;
        }
        return loc;
    }

    /**
     * npcidからcastle_idを返す
     *
     * @param npcid
     * @return
     */
    public static int getCastleIdByNpcid(int npcid) {
        //エデン性：エデン王国全域
        //ケント城：ケント、グルディーン
        //ウィンダウトドゥソン：ウッドベック、オアシス、シルバーナイトタウン
        //ギラン城：ギラン、話すことができている島
        // Heine性：Heine
        //ドゥウォ症候性：完熟、象牙の塔、象牙の塔の村
        //オーク間：焼畑村
        //ディアド要塞：戦争歳の一部

        int castle_id = 0;

        int town_id = L1TownLocation.getTownIdByNpcid(npcid);

        switch (town_id) {
            case L1TownLocation.TOWNID_KENT:
            case L1TownLocation.TOWNID_GLUDIO:
                castle_id = KENT_CASTLE_ID; // ケント城
                break;

            case L1TownLocation.TOWNID_ORCISH_FOREST:
                castle_id = OT_CASTLE_ID; // オークの森
                break;

            case L1TownLocation.TOWNID_SILVER_KNIGHT_TOWN:
            case L1TownLocation.TOWNID_WINDAWOOD:
                castle_id = WW_CASTLE_ID; //ウィンダウトドゥソン
                break;

            case L1TownLocation.TOWNID_TALKING_ISLAND:
            case L1TownLocation.TOWNID_GIRAN:
                castle_id = GIRAN_CASTLE_ID; // ギラン城
                break;

            case L1TownLocation.TOWNID_HEINE:
                castle_id = HEINE_CASTLE_ID; // Heine性
                break;

            case L1TownLocation.TOWNID_WERLDAN:
            case L1TownLocation.TOWNID_OREN:
                castle_id = DOWA_CASTLE_ID; // ドゥウォ症候性
                break;

            case L1TownLocation.TOWNID_ADEN:
                castle_id = ADEN_CASTLE_ID; // エデン性
                break;

            case L1TownLocation.TOWNID_OUM_DUNGEON:
                castle_id = DIAD_CASTLE_ID; // ディアド要塞
                break;

            default:
                break;
        }
        return castle_id;
    }

    // このメソッドは、エデン時間に1日ごとに更新されている税率を返します。 （リアルタイムの税率ではない）
    public static int getCastleTaxRateByNpcId(int npcId) {
        int castleId = getCastleIdByNpcid(npcId);
        if (castleId != 0) {
            return _castleTaxRate.get(castleId);
        }
        return 0;
    }

    // 各省税率を保管しておくHashMap（ショプヨン）
    private static HashMap<Integer, Integer> _castleTaxRate = new HashMap<Integer, Integer>();

    private static L1CastleTaxRateListener _listener;

    // GameServer#initialize, L1CastleTaxRateListener#onDayChangedだけ呼び出される予定。
    public static void setCastleTaxRate() {
        for (L1Castle castle : CastleTable.getInstance().getCastleTableList()) {
            _castleTaxRate.put(castle.getId(), castle.getTaxRate());
        }
        if (_listener == null) {
            _listener = new L1CastleTaxRateListener();
            L1GameTimeClock.getInstance().addListener(_listener);
        }
    }

    private static class L1CastleTaxRateListener extends L1GameTimeAdapter {
        @Override
        public void onDayChanged(L1GameTime time) {
            L1CastleLocation.setCastleTaxRate();
        }
    }

    public static int[] getSubTowerLoc(int no) {
        int[] result = new int[3];
        L1Location loc = _subTowers.get(no);
        if (loc != null) {
            result[0] = loc.getX();
            result[1] = loc.getY();
            result[2] = loc.getMapId();
        }
        return result;
    }
}
