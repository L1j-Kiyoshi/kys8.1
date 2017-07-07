/**
 * タイマー関連マップオブジェクト
 * 2008. 12. 04
*/

package l1j.server.server.templates;

public class L1TimeMap{

	private int id;
	private int time;
	private int DoorId;

	/** 
	 * 既定のコンストラクタ
	 * @param	(int)	id		マップ名
	 * @param	(int)	time	設定された時間（s）
	*/
	public L1TimeMap(int id, int time){
		this.id = id;
		this.time = time;
	}
	/** 
	 *既定のコンストラクタ
	 * @param	(int)	id		マップ名
	 * @param	(int)	time	設定された時間（s）
	 * @param	(int)	DoorId	設定されたドアのID
	*/
	public L1TimeMap(int id, int time, int DoorId){
		this.id = id;
		this.time = time;
		this.DoorId = DoorId;
	}
	/** 
	 * マップ名リターン
	 * @return	(int)	マップ名
	*/
	public int getId(){
		return id;
	}
	/** 
	 * 設定時間リターン
	 * @return	(int)	設定時間
	*/
	public int getTime(){
		return time;
	}
	/** 
	 * 設定されたドアのIDリターン
	 * @return	(int)	ドア名
	*/
	public int getDoor(){
		return DoorId;
	}
	/** 
	 * 残り時間を計算
	 * @return	(boolean)	時間が終了したらtrue、残っている場合false
	*/
	public boolean count(){
		return time-- <= 0;
	}
}