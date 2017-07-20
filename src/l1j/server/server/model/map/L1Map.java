package l1j.server.server.model.map;

import l1j.server.server.types.Point;

public abstract class L1Map {
	private static L1NullMap _nullMap = new L1NullMap();

	protected L1Map() {
	}

	public abstract int getId();
	/**
	* 新しいマップIDを設定します。このIDは、マップオブジェクトを一意に識別するための値であることを見？必要があります。
	*
	* @ param mapId
	* 新たに設定されているマップのIDです。
	*/
	public abstract void setId(int mapId);

	/**
	* マップファイルIDを返します。
	* このIDをマップオブジェクト一意に識別するために使用しないでください。
	* このIDは、マップファイルを識別するためにどのようなゲームクライアントが必要とするIDであり、一意性を保証？ありません。
	* 一部のオブジェクトが同じマップファイルIDを持つことができます。
	* クライアントに送信するマップファイルIDが必要場合は、この方法を使用します。
	*
	* @ return マップファイルID
	*/
	public abstract int getBaseMapId();
	// TODO JavaDoc
	public abstract int getX();

	public abstract int getY();

	public abstract int getWidth();

	public abstract int getHeight();

	public abstract int getTile(int x, int y);

	public abstract int getOriginalTile(int x, int y);

	public abstract boolean isInMap(Point pt);

	public abstract boolean isInMap(int x, int y);

	public abstract boolean isPassable(Point pt);

	public abstract boolean isPassable(int x, int y);

	public abstract boolean isPassable(Point pt, int heading);

	public abstract boolean isUserPassable(int x, int y, int heading);

	public abstract boolean isPassable(int x, int y, int heading);

	public abstract void setPassable(Point pt, boolean isPassable);

	public abstract void setPassable(int x, int y, boolean isPassable);

	public abstract boolean isSafetyZone(Point pt);

	public abstract boolean isSafetyZone(int x, int y);

	public abstract boolean isCombatZone(Point pt);

	public abstract boolean isCombatZone(int x, int y);

	public abstract boolean isNormalZone(Point pt);

	public abstract boolean isNormalZone(int x, int y);

	public abstract boolean isArrowPassable(Point pt);

	public abstract boolean isArrowPassable(int x, int y);

	public abstract boolean isArrowPassable(Point pt, int heading);

	public abstract boolean isArrowPassable(int x, int y, int heading);

	public abstract boolean isUnderwater();

	public abstract boolean isMarkable();

	public abstract boolean isTeleportable();

	public abstract boolean isEscapable();

	public abstract boolean isUseResurrection();

	public abstract boolean isUsePainwand();

	public abstract boolean isEnabledDeathPenalty();

	public abstract boolean isTakePets();

	public abstract boolean isRecallPets();

	public abstract boolean isUsableItem();

	public abstract boolean isUsableSkill();

	public abstract boolean isFishingZone(int x, int y);

	public abstract boolean isExistDoor(int x, int y);
	
	public abstract L1V1Map copyMap(int a);//レイド

	public static L1Map newNull() {
		return _nullMap;
	}

	public abstract String toString(Point pt);

	public boolean isNull() {
		return false;
	}
	
	public static boolean isTeleportable(int x, int y, int mapId){//バグベアーレース座標
		if( mapId == 4 && x >= 33469 && x <= 33528 && y >= 32839 && y <= 32869 ){
			return false;
		}
		return true;
	}
}

class L1NullMap extends L1Map {
	public L1NullMap() {
	}

	@Override
	public int getId() {
		return 0;
	}

	@Override
	public int getX() {
		return 0;
	}

	@Override
	public int getY() {
		return 0;
	}

	@Override
	public int getWidth() {
		return 0;
	}

	@Override
	public int getHeight() {
		return 0;
	}

	@Override
	public int getTile(int x, int y) {
		return 0;
	}

	@Override
	public int getOriginalTile(int x, int y) {
		return 0;
	}

	@Override
	public boolean isInMap(int x, int y) {
		return false;
	}

	@Override
	public boolean isInMap(Point pt) {
		return false;
	}

	@Override
	public boolean isPassable(int x, int y) {
		return false;
	}

	@Override
	public boolean isUserPassable(int x, int y, int heading) {
		return false;
	}

	@Override
	public boolean isPassable(Point pt) {
		return false;
	}

	@Override
	public boolean isPassable(int x, int y, int heading) {
		return false;
	}

	@Override
	public boolean isPassable(Point pt, int heading) {
		return false;
	}

	@Override
	public void setPassable(int x, int y, boolean isPassable) {
	}

	@Override
	public void setPassable(Point pt, boolean isPassable) {
	}

	@Override
	public boolean isSafetyZone(int x, int y) {
		return false;
	}

	@Override
	public boolean isSafetyZone(Point pt) {
		return false;
	}

	@Override
	public boolean isCombatZone(int x, int y) {
		return false;
	}

	@Override
	public boolean isCombatZone(Point pt) {
		return false;
	}

	@Override
	public boolean isNormalZone(int x, int y) {
		return false;
	}

	@Override
	public boolean isNormalZone(Point pt) {
		return false;
	}

	@Override
	public boolean isArrowPassable(int x, int y) {
		return false;
	}

	@Override
	public boolean isArrowPassable(Point pt) {
		return false;
	}

	@Override
	public boolean isArrowPassable(int x, int y, int heading) {
		return false;
	}

	@Override
	public boolean isArrowPassable(Point pt, int heading) {
		return false;
	}

	@Override
	public boolean isUnderwater() {
		return false;
	}

	@Override
	public boolean isMarkable() {
		return false;
	}

	@Override
	public boolean isTeleportable() {
		return false;
	}

	@Override
	public boolean isEscapable() {
		return false;
	}

	@Override
	public boolean isUseResurrection() {
		return false;
	}

	@Override
	public boolean isUsePainwand() {
		return false;
	}

	@Override
	public boolean isEnabledDeathPenalty() {
		return false;
	}

	@Override
	public boolean isTakePets() {
		return false;
	}

	@Override
	public boolean isRecallPets() {
		return false;
	}

	@Override
	public boolean isUsableItem() {
		return false;
	}

	@Override
	public boolean isUsableSkill() {
		return false;
	}

	@Override
	public boolean isFishingZone(int x, int y) {
		return false;
	}

	@Override
	public boolean isExistDoor(int x, int y) {
		return false;
	}

	@Override
	public String toString(Point pt) {
		return "null";
	}

	@Override
	public boolean isNull() {
		return true;
	}
	
	@Override
	public L1V1Map copyMap(int id){//レイド
		return null;
	}
	@Override
	public void setId(int mapId) {

	}

	@Override
	public int getBaseMapId() {
		return 0;
	}
}