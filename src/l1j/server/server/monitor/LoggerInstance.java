package l1j.server.server.monitor;

public class LoggerInstance extends FileLogger implements Runnable {
	private static LoggerInstance _instance = null;

	private LoggerInstance() {
	};

	public static LoggerInstance getInstance() {
		if (_instance == null) {
			_instance = new LoggerInstance();
			new Thread(_instance).start();
		}
		return _instance;
	}

	public void run() {
		while (true) {
			try {
				flush();
				Thread.sleep(1000 * 60);
			} catch (Exception e) {
			}
		}
	}
}
