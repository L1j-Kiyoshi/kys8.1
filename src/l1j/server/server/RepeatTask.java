package l1j.server.server;

public abstract class RepeatTask implements Runnable {
	
	public RepeatTask( long interval) {
		_interval = interval;
		_active = true;
	}
	
	public long getInterval() {
		return _interval;
	}
	
    public abstract void execute();
	
	@Override
	public final void run() {
		if (!_active) {
			return;
		}		
		execute();
		
		if (_active){
			GeneralThreadPool.getInstance().schedule(this, _interval);
		}
	}
	
	public void cancel() {
		_active = false;
	}
	
	private boolean _active;
	private long _interval;
}
