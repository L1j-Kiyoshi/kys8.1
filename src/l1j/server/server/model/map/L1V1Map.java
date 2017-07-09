package l1j.server.server.model.map;

import l1j.server.server.ActionCodes;
import l1j.server.server.datatables.DoorSpawnTable;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.types.Point;

public class L1V1Map extends L1Map {
	
	private int _mapId;
    private int _baseMapId;
	private int _worldTopLeftX;
	private int _worldTopLeftY;
	private int _worldBottomRightX;
	private int _worldBottomRightY;
	
	private byte _map[][];

	public byte _doorMap[][];
	
	private boolean _isUnderwater;
	private boolean _isMarkable;
	private boolean _isTeleportable;
	private boolean _isEscapable;
	private boolean _isUseResurrection;
	private boolean _isUsePainwand;
	private boolean _isEnabledDeathPenalty;
	private boolean _isTakePets;
	private boolean _isRecallPets;
	private boolean _isUsableItem;
	private boolean _isUsableSkill;

	private static final byte BITFLAG_IS_IMPASSABLE = (byte) 128; // 1000 0000

	protected L1V1Map() {  }

	public L1V1Map(int mapId, byte map[][], int worldTopLeftX,
			int worldTopLeftY, boolean underwater, boolean markable,
			boolean teleportable, boolean escapable, boolean useResurrection,
			boolean usePainwand, boolean enabledDeathPenalty, boolean takePets,
			boolean recallPets, boolean usableItem, boolean usableSkill) {
		_mapId = mapId;
        _baseMapId = mapId;
        _doorMap = new byte[map.length][map[0].length];
		for (int i = 0; i < map.length; ++i) {
			for (int j = 0; j < map[0].length; ++j) {
				_doorMap[i][j] = 0;
			}
		}
		_map = map;
		_worldTopLeftX = worldTopLeftX;
		_worldTopLeftY = worldTopLeftY;

		_worldBottomRightX = worldTopLeftX + map.length - 1;
		_worldBottomRightY = worldTopLeftY + map[0].length - 1;

		_isUnderwater = underwater;
		_isMarkable = markable;
		_isTeleportable = teleportable;
		_isEscapable = escapable;
		_isUseResurrection = useResurrection;
		_isUsePainwand = usePainwand;
		_isEnabledDeathPenalty = enabledDeathPenalty;
		_isTakePets = takePets;
		_isRecallPets = recallPets;
		_isUsableItem = usableItem;
		_isUsableSkill = usableSkill;
	}

	public L1V1Map(L1V1Map map) {
		_mapId = map._mapId;
        _baseMapId = map._mapId;

		_map = new byte[map._map.length][];
		for (int i = 0; i < map._map.length; i++) {
			_map[i] = map._map[i].clone();
		}

		_doorMap = new byte[_map.length][_map[0].length];
		for (int i = 0; i < _map.length; ++i) {
			for (int j = 0; j < _map[0].length; ++j) {
				_doorMap[i][j] = 0;
			}
		}
		
		_worldTopLeftX = map._worldTopLeftX;
		_worldTopLeftY = map._worldTopLeftY;
		_worldBottomRightX = map._worldBottomRightX;
		_worldBottomRightY = map._worldBottomRightY;

	}
	
	public L1V1Map clone(int id){//レイド
		L1V1Map map = new L1V1Map(this);
		map._mapId = id;
		map._isUnderwater = _isUnderwater;
		map._isMarkable = _isMarkable;
		map._isTeleportable = _isTeleportable;
		map._isEscapable = _isEscapable;
		map._isUseResurrection = _isUseResurrection;
		map._isUsePainwand = _isUsePainwand;
		map._isEnabledDeathPenalty = _isEnabledDeathPenalty;
		map._isTakePets = _isTakePets;
		map._isRecallPets = _isRecallPets;
		map._isUsableItem = _isUsableItem;
		map._isUsableSkill = _isUsableSkill;
		return map;
	}

	public void reset(L1V1Map map) {
		if (map == null || _map.length != map._map.length)
			return;
		for (int i = 0; i < map._map.length; i++) {
			_map[i] = map._map[i].clone();
		}
		for (int i = 0; i < _map.length; ++i) {
			for (int j = 0; j < _map[0].length; ++j) {
				_doorMap[i][j] = 0;
			}
		}
	}
	public int accessTile(int x, int y) {
		if (!isInMap(x, y)) { 
			return 0;
		}

		return _map[x - _worldTopLeftX][y - _worldTopLeftY];
	}

	private int accessOriginalTile(int x, int y) {
		return accessTile(x, y) & (~BITFLAG_IS_IMPASSABLE);
	}

	private void setTile(int x, int y, int tile) {
		if (!isInMap(x, y)) { 
			return;
		}
		_map[x - _worldTopLeftX][y - _worldTopLeftY] = (byte) tile;
	}

	public byte[][] getRawTiles() {
		return _map;
	}
	
	@Override
	public L1V1Map copyMap(int newMapId){//一時的に追加
		return clone(newMapId);
	}

	@Override
	public int getId() {
		return _mapId;
	}

	@Override
	public int getX() {
		return _worldTopLeftX;
	}

	@Override
	public int getY() {
		return _worldTopLeftY;
	}

	@Override
	public int getWidth() {
		return _worldBottomRightX - _worldTopLeftX + 1;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return _worldBottomRightY - _worldTopLeftY + 1;
	}

	@Override
	public int getTile(int x, int y) {
		short tile = _map[x - _worldTopLeftX][y - _worldTopLeftY];
		if (0 != (tile & BITFLAG_IS_IMPASSABLE)) {
			return 300;
		}
		return accessOriginalTile(x, y);
	}

	@Override
	public int getOriginalTile(int x, int y) {
		return accessOriginalTile(x, y);
	}

	@Override
	public boolean isInMap(Point pt) {
		return isInMap(pt.getX(), pt.getY());
	}

	@Override
	public boolean isInMap(int x, int y) {
		if (_mapId == 4
				&& (x < 32520 || y < 32070 || (y < 32190 && x < 33950))) {
			return false;
		}
		return (_worldTopLeftX <= x && x <= _worldBottomRightX
				&& _worldTopLeftY <= y && y <= _worldBottomRightY);
	}

	@Override
	public boolean isPassable(Point pt) {
		return isPassable(pt.getX(), pt.getY());
	}

	@Override
	public boolean isPassable(int x, int y) {
		return isPassable(x, y - 1, 4) || isPassable(x + 1, y, 6)
		|| isPassable(x, y + 1, 0) || isPassable(x - 1, y, 2);
	}

	@Override
	public boolean isPassable(Point pt, int heading) {
		return isPassable(pt.getX(), pt.getY(), heading);
	}

	@Override
	public boolean isUserPassable(int x, int y, int heading)
	{
		int tile1 = accessTile(x, y);
		int tile2;

		switch(heading){
		case 0: tile2 = accessTile(x, y - 1); break;
		case 1: tile2 = accessTile(x + 1, y - 1); break;
		case 2: tile2 = accessTile(x + 1, y); break;
		case 3: tile2 = accessTile(x + 1, y + 1); break;
		case 4: tile2 = accessTile(x, y + 1); break;
		case 5: tile2 = accessTile(x - 1, y + 1); break;
		case 6: tile2 = accessTile(x - 1, y); break;
		case 7: tile2 = accessTile(x - 1, y - 1); 	break;
		default: return false;
		}

		switch(heading){
		case 0:{ return (tile1 & 0x02) == 0x02; }
		case 1:{
			int tile3 = accessTile(x, y - 1);
			int tile4 = accessTile(x + 1, y);
			return (tile1 & 0x02) == 0x02 && (tile3 & 0x01) == 0x01 || (tile1 & 0x01) == 0x01 && (tile4 & 0x02) == 0x02; }
		case 2:{ return (tile1 & 0x01) == 0x01; }
		case 3:{
			int tile3 = accessTile(x, y + 1);
			return (tile3 & 0x03) == 0x03 || (tile1 & 0x01) == 0x01 && (tile2 & 0x02) == 0x02;
			}
		case 4:{ return (tile2 & 0x02) == 0x02; }
		case 5:{
			int tile3 = accessTile(x, y + 1);
			int tile4 = accessTile(x - 1, y);
			return (tile2 & 0x01) == 0x01 && ( tile3 & 0x02 ) == 0x02 || (tile2 & 0x02) == 0x02 && (tile4 & 0x01) == 0x01; }
		case 6:{ return (tile2 & 0x01) == 0x01; }
		case 7:{
			int tile3 = accessTile(x - 1, y);
			return (tile3 & 0x03) == 0x03 || (tile1 & 0x02) == 0x02 && (tile2 & 0x01) == 0x01;
			}
		default:break;
		}
		
		return false;		
	}
	
	@Override
	public boolean isPassable(int x, int y, int heading) {
		int tile1 = accessTile(x, y);
		int tile2;

		switch(heading){
		case 0: tile2 = accessTile(x, y - 1); break;
		case 1: tile2 = accessTile(x + 1, y - 1); break;
		case 2: tile2 = accessTile(x + 1, y); break;
		case 3: tile2 = accessTile(x + 1, y + 1); break;
		case 4: tile2 = accessTile(x, y + 1); break;
		case 5: tile2 = accessTile(x - 1, y + 1); break;
		case 6: tile2 = accessTile(x - 1, y); break;
		case 7: tile2 = accessTile(x - 1, y - 1); 	break;
		default: return false;
		}

		if ((tile2 & BITFLAG_IS_IMPASSABLE) == BITFLAG_IS_IMPASSABLE) {
			return false;
		}

		switch(heading){
		case 0:{ return (tile1 & 0x02) == 0x02; }
		case 1:{
			int tile3 = accessTile(x, y - 1);
			int tile4 = accessTile(x + 1, y);
			return (tile1 & 0x02) == 0x02 && (tile3 & 0x01) == 0x01 || (tile1 & 0x01) == 0x01 && (tile4 & 0x02) == 0x02; }
		case 2:{ return (tile1 & 0x01) == 0x01; }
		case 3:{
			int tile3 = accessTile(x, y + 1);
			return (tile3 & 0x03) == 0x03 || (tile1 & 0x01) == 0x01 && (tile2 & 0x02) == 0x02;
			}
		case 4:{ return (tile2 & 0x02) == 0x02; }
		case 5:{
			int tile3 = accessTile(x, y + 1);
			int tile4 = accessTile(x - 1, y);
			return (tile2 & 0x01) == 0x01 && ( tile3 & 0x02 ) == 0x02 || (tile2 & 0x02) == 0x02 && (tile4 & 0x01) == 0x01; }
		case 6:{ return (tile2 & 0x01) == 0x01; }
		case 7:{
			int tile3 = accessTile(x - 1, y);
			return (tile3 & 0x03) == 0x03 || (tile1 & 0x02) == 0x02 && (tile2 & 0x01) == 0x01;
			}
		default:break;
		}
		
		return false;
	}

	@Override
	public void setPassable(Point pt, boolean isPassable) {
		setPassable(pt.getX(), pt.getY(), isPassable);
	}

	@Override
	public void setPassable(int x, int y, boolean isPassable) {
		if (isPassable) {
			setTile(x, y, (short) (accessTile(x, y) & (~BITFLAG_IS_IMPASSABLE)));
		} else {
			setTile(x, y, (short) (accessTile(x, y) | BITFLAG_IS_IMPASSABLE));
		}
	}

	@Override
	public boolean isSafetyZone(Point pt) {
		return isSafetyZone(pt.getX(), pt.getY());
	}

	@Override
	public boolean isSafetyZone(int x, int y) {
		int tile = accessOriginalTile(x, y);

		return (tile & 0x30) == 0x10;
	}

	@Override
	public boolean isCombatZone(Point pt) {
		return isCombatZone(pt.getX(), pt.getY());
	}

	@Override
	public boolean isCombatZone(int x, int y) {
		int tile = accessOriginalTile(x, y);

		return (tile & 0x30) == 0x20;
	}

	@Override
	public boolean isNormalZone(Point pt) {
		return isNormalZone(pt.getX(), pt.getY());
	}

	@Override
	public boolean isNormalZone(int x, int y) {
		int tile = accessOriginalTile(x, y);
		return (tile & 0x30) == 0x00;
	}

	@Override
	public boolean isArrowPassable(Point pt) {
		return isArrowPassable(pt.getX(), pt.getY());
	}

	@Override
	public boolean isArrowPassable(int x, int y) {
		return (accessOriginalTile(x, y) & 0x0e) != 0;
	}

	@Override
	public boolean isArrowPassable(Point pt, int heading) {
		return isArrowPassable(pt.getX(), pt.getY(), heading);
	}

	@Override
	public boolean isArrowPassable(int x, int y, int heading) {
		int tile1 = accessTile(x, y);
		int tile2;
		int newX;
		int newY;

		switch(heading){
		case 0: tile2 = accessTile(x, y - 1); newX = x; newY = y - 1; break;
		case 1: tile2 = accessTile(x + 1, y - 1); newX = x + 1; newY = y - 1; break;
		case 2: tile2 = accessTile(x + 1, y); newX = x + 1; newY = y; break;
		case 3: tile2 = accessTile(x + 1, y + 1); newX = x + 1; newY = y + 1; break;
		case 4: tile2 = accessTile(x, y + 1); newX = x; newY = y + 1; break;
		case 5: tile2 = accessTile(x - 1, y + 1); newX = x - 1; newY = y + 1; break;
		case 6: tile2 = accessTile(x - 1, y); newX = x - 1; newY = y; break;
		case 7: tile2 = accessTile(x - 1, y - 1); newX = x - 1; newY = y - 1; break;
		default: return false;
		}

		if (isExistDoor(newX, newY)) {
			return false;
		}

		switch(heading){
		case 0:{ return (tile1 & 0x08) == 0x08; }
		case 1:{ int tile3 = accessTile(x, y - 1); int tile4 = accessTile(x + 1, y); return (tile3 & 0x04) == 0x04 || (tile4 & 0x08) == 0x08; }
		case 2:{ return (tile1 & 0x04) == 0x04; }
		case 3:{ int tile3 = accessTile(x, y + 1); return (tile3 & 0x04) == 0x04; }
		case 4:{ return (tile2 & 0x08) == 0x08; }
		case 5:{ return (tile2 & 0x04) == 0x04 || (tile2 & 0x08) == 0x08; }
		case 6:{ return (tile2 & 0x04) == 0x04; }
		case 7:{ int tile3 = accessTile(x - 1, y); return (tile3 & 0x08) == 0x08; }
		default: break;
		}

		return false;
	}

	@Override
	public boolean isUnderwater() {
		return _isUnderwater;
	}

	@Override
	public boolean isMarkable() {
		return _isMarkable;
	}

	@Override
	public boolean isTeleportable() {
		return _isTeleportable;
	}

	@Override
	public boolean isEscapable() {
		return _isEscapable;
	}

	@Override
	public boolean isUseResurrection() {
		return _isUseResurrection;
	}

	@Override
	public boolean isUsePainwand() {
		return _isUsePainwand;
	}

	@Override
	public boolean isEnabledDeathPenalty() {
		return _isEnabledDeathPenalty;
	}

	@Override
	public boolean isTakePets() {
		return _isTakePets;
	}

	@Override
	public boolean isRecallPets() {
		return _isRecallPets;
	}

	@Override
	public boolean isUsableItem() {
		return _isUsableItem;
	}

	@Override
	public boolean isUsableSkill() {
		return _isUsableSkill;
	}

	@Override
	public boolean isFishingZone(int x, int y) {
		return accessOriginalTile(x, y) == 16;
	}

	@Override
	public boolean isExistDoor(int x, int y) {
		for (L1DoorInstance door : DoorSpawnTable.getInstance().getDoorList()) {
			if (_mapId != door.getMapId()) {
				continue;
			}
			if (door.getOpenStatus() == ActionCodes.ACTION_Open) {
				continue;
			}
			if (door.isDead()) {
				continue;
			}
			int leftEdgeLocation = door.getLeftEdgeLocation();
			int rightEdgeLocation = door.getRightEdgeLocation();
			int size = rightEdgeLocation - leftEdgeLocation;
			if (size == 0) {
				if (x == door.getX() && y == door.getY()) {
					return true;
				}
			} else {
				if (door.getDirection() == 0) { 
					for (int doorX = leftEdgeLocation;
					doorX <= rightEdgeLocation; doorX++) {
						if (x == doorX && y == door.getY()) {
							return true;
						}
					}
				} else {
					for (int doorY = leftEdgeLocation;
					doorY <= rightEdgeLocation; doorY++) {
						if (x == door.getX() && y == doorY) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public String toString(Point pt) {
		return "" + getOriginalTile(pt.getX(), pt.getY());
	} 
	@Override
	public void setId(int mapId) {
		_mapId = mapId;
	}

	@Override
	public int getBaseMapId() {
		return _baseMapId;
	}
}
