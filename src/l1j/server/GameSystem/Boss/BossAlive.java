package l1j.server.GameSystem.Boss;

import java.util.HashMap;

public class BossAlive {
	public static BossAlive ins;

	public static BossAlive getInstance(){
		if(ins==null)
			ins = new BossAlive();
		return ins;
	}
	//MapID 1:生存 2:死
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
	public boolean isErusabe = false;
	public long ezTime = -1;

	public void setErusabeTime(long s){
		ezTime = s;
	}
	public boolean isSandWarm = false;
	public long sdTime = -1;
	public void setSandWarmTime(long s){
		sdTime = s;
	}
}
