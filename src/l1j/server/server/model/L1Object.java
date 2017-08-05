package l1j.server.server.model;

import java.io.Serializable;

import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1SupportInstance;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.model.map.L1WorldMap;

/**
 * ワールド上に存在するすべてのオブジェクトのベースクラス
 */
public class L1Object implements Serializable {
    private static final long serialVersionUID = 1L;
    private L1Location _loc = new L1Location();
    private int _id = 0;

    /**
     * ロボットを追加
     **/

    private int _dir = 0;
    private int _dis = 0;

    private int _visibleX = 0;
    private int _visibleY = 0;
    private int _visibleTempX = 0;
    private int _visibleTempY = 0;
    private int _visibleMapId = 0;

    public int getVisibleX() {
        return _visibleX;
    }

    public void setVisibleX(int x) {
        _visibleX = x;
    }

    public int getVisibleY() {
        return _visibleY;
    }

    public void setVisibleY(int y) {
        _visibleY = y;
    }

    public int getVisibleTempX() {
        return _visibleTempX;
    }

    public void setVisibleTempX(int x) {
        _visibleTempX = x;
    }

    public int getVisibleTempY() {
        return _visibleTempY;
    }

    public void setVisibleTempY(int y) {
        _visibleTempY = y;
    }

    public int getVisibleMapId() {
        return _visibleMapId;
    }

    public void setVisibleMapId(int mapId) {
        _visibleMapId = mapId;
    }

    /**
     * オブジェクトが存在するMAPのMAP IDを返す
     *
     * @return MAP ID
     */
    public short getMapId() {
        return (short) _loc.getMap().getId();
    }

    /**
     * オブジェクトが存在するMAPのMAP IDを設定する
     *
     * @param mapId MAP ID
     */
    public void setMap(short mapId) {
        _loc.setMap(L1WorldMap.getInstance().getMap(mapId));
    }

    /**
     * オブジェクトが存在するMAPを保持するL1Mapオブジェクトを返す
     */
    public L1Map getMap() {
        return _loc.getMap();
    }

    /**
     * オブジェクトが存在するMAPを設定する
     *
     * @param map オブジェクトが存在するMAPを保持するL1Mapオブジェクト
     */
    public void setMap(L1Map map) {
        if (map == null) {
            throw new NullPointerException();
        }
        _loc.setMap(map);
    }

    /**
     * オブジェクトを識別するためのIDを返す
     *
     * @return オブジェクトID
     */
    public int getId() {
        return _id;
    }

    /**
     * オブジェクトを識別するためのIDを設定する
     *
     * @param id オブジェクトID
     */
    public void setId(int id) {
        _id = id;
    }

    /**
     * オブジェクトが存在する座標のX値を返す
     *
     * @return 座標のX値
     */
    public int getX() {
        return _loc.getX();
    }

    /**
     * オブジェクトが存在する座標のX値を設定する
     *
     * @param x 座標のX値
     */
    public void setX(int x) {
        _loc.setX(x);
    }

    /**
     * オブジェクトが存在する座標のY値を返す
     *
     * @return 座標のY値
     */
    public int getY() {
        return _loc.getY();
    }

    /**
     * オブジェクトが存在する座標のY値を設定する
     *
     * @param y 座標のY値
     */
    public void setY(int y) {
        _loc.setY(y);
    }

    /**
     * オブジェクトが存在する位置を保持する、L1Locationオブジェクトへの参照を返す。
     *
     * @return 座標を保持する、L1Locationオブジェクトへの参照
     */
    public L1Location getLocation() {
        return _loc;
    }

    public void setLocation(L1Location loc) {
        _loc.setX(loc.getX());
        _loc.setY(loc.getY());
        _loc.setMap(loc.getMapId());
    }

    public void setLocation(int x, int y, int mapid) {
        _loc.setX(x);
        _loc.setY(y);
        _loc.setMap(mapid);
    }

    /**
     * 指定されたオブジェクトまでの直線距離を返す。
     */
    public double getLineDistance(L1Object obj) {
        return this.getLocation().getLineDistance(obj.getLocation());
    }

    /**
     * 指定されたオブジェクトまでの直線タイル数を返す。
     */
    public int getTileLineDistance(L1Object obj) {
        return this.getLocation().getTileLineDistance(obj.getLocation());
    }

    /**
     * 指定されたオブジェクトまでのタイルの数を返す。
     */
    public int getTileDistance(L1Object obj) {
        return this.getLocation().getTileDistance(obj.getLocation());
    }

    /**
     * オブジェクトがプレーヤーの画面内に入った（認識された）ときに呼び出される。
     *
     * @param perceivedFrom このオブジェクトを認識したPC
     */
    public void onPerceive(L1PcInstance perceivedFrom) {
    }

    /**
     * オブジェクトがプレーヤーの画面内に入った（認識された）ときに呼び出される。
     *
     * @param perceivedFrom このオブジェクトを認識したPC
     */
    public void onPerceive(L1SupportInstance perceivedFrom) {
    }

    /**
     * オブジェクトとアクションが発生したときに呼び出さ
     *
     * @param actionFrom アクションを起こしたPC
     */
    public void onAction(L1PcInstance actionFrom) {
    }

    /**
     * オブジェクトと対話するときに呼び出さ
     *
     * @param talkFrom 話しかけたPC
     */
    public void onTalkAction(L1PcInstance talkFrom) {
    }
}
