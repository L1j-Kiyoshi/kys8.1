package l1j.server.server.model;

public class MoveState {
	private int _heading; // ●方向0.左上1.上2偶像3.右4.右下5.し6左下7。

	// 左

	private int _moveSpeed; // ●スピード0.通常1ヘイスト2スロー

	private int _braveSpeed; // ● ブレイブ状態0.通常1ブレイブ

	public int getHeading() {
		return _heading;
	}

	public void setHeading(int i) {
		_heading = i;
	}

	public int getMoveSpeed() {
		return _moveSpeed;
	}

	public void setMoveSpeed(int i) {
		_moveSpeed = i;
	}

	public int getBraveSpeed() {
		return _braveSpeed;
	}

	public void setBraveSpeed(int i) {
		_braveSpeed = i;
	}
}
