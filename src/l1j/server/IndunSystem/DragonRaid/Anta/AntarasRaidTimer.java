package l1j.server.IndunSystem.DragonRaid.Anta;

import l1j.server.server.GeneralThreadPool;

public class AntarasRaidTimer implements Runnable {
	
	private final AntarasRaid _ar; 
	private final int _type; 
	private final int _step; 
	private final int _timeMillis;
	
	
	public AntarasRaidTimer(AntarasRaid ar, int type, int step, int timeMillis) { 
		_ar = ar; 
		_type = type; 
		_step = step; 
		_timeMillis = timeMillis;
	}

	@Override
	public void run() {
		_ar.timeOverRun(_type, _step);
	}
		
	public void begin() {
		GeneralThreadPool.getInstance().schedule(this, _timeMillis);
	}
}
