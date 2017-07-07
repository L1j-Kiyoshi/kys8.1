package l1j.server.GameSystem.Boss;

import java.util.HashMap;

public class BossAlive {
	public static BossAlive ins;

	public static BossAlive getInstance(){
		if(ins==null)
			ins = new BossAlive();
		return ins;
	}
	//MapID , 1生存2死
	HashMap<Integer,Integer> isAlive = new HashMap<Integer,Integer>();
	
	public boolean isBossAlive(int mapid){
		boolean alive = false;
			if(isAlive.containsKey(mapid)){
				alive = true;
			}
		
		return alive;		
	}
	
	public void BossSpawn(int mapid){
		isAlive.put(mapid, 1);
	}
	public void BossDeath(int mapid){
		isAlive.remove(mapid);
	}
	//後でこれHashMapにする
	public boolean is에르자베 = false;
	public long ezTime = -1;

	public void set에르자베타임(long s){
		ezTime = s;
	}
	public boolean is샌드웜 = false;
	public long sdTime = -1;
	public void set샌드웜타임(long s){
		sdTime = s;
	}
}
