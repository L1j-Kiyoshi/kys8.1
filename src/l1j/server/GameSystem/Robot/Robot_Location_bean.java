package l1j.server.GameSystem.Robot;

public class Robot_Location_bean {
	int _locx;
	int _locy;
	int _mapid;

	public Robot_Location_bean(int x, int y, int m) {
		_locx = x;
		_locy = y;
		_mapid = m;
	}

	public int getX() {
		return _locx;
	}

	public int getY() {
		return _locy;
	}

	public int getMapId() {
		return _mapid;
	}
}