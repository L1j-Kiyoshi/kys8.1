package server.netty.coder.manager;

import l1j.server.Config;
import l1j.server.server.GameClient;
import l1j.server.server.GeneralThreadPool;

public class DecoderManager {
	private static DecoderManager _instance;
	
	public static DecoderManager getInstance(){
		if (_instance == null) {
			_instance = new DecoderManager();
		}
		return _instance;
	}
	private LineageDecoderThread[] dts = new LineageDecoderThread[Config.MAX_ONLINE_USERS / 100]; 
	
	private DecoderManager(){
		for(int i=0; i<dts.length; i++){
			LineageDecoderThread dt = new LineageDecoderThread();
			GeneralThreadPool.getInstance().execute(dt);
			dts[i] = dt;
		}
	}
	private int tempcount = 0;
	private int indexcount = 0;
	public int getRowIndex(){
		return indexcount;
		//++indexcount;
		//return indexcount %=  dts.length;
		
	}
	
	public int getindex_size(){
		return dts.length;
	}
	
	/*public int getRowcheck(){
		int temp = 0;
		for(int i=0; i<dts.length; i++){
			int temp1 = dts[i].ClientCount();
			if(temp1 > temp){
				temp = temp1;
			}
		}
		return temp;
	}*/
	
	public void putClient(GameClient lc ){
		dts[lc.getthreadIndex()].putClient(lc);
	}
	
	public void removeClient(GameClient lc, int ix){
		dts[ix].removeClient(lc);
	}
}
