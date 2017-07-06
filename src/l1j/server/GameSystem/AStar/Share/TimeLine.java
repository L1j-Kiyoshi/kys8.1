package l1j.server.GameSystem.AStar.Share;

public final class TimeLine {

	static private long time;

	static public void startNano() {
		time = System.nanoTime();
	}

	static public long endNano() {
		return System.nanoTime() - time;
	}

	static public void start(String msg) {
		time = System.currentTimeMillis();
		if (msg != null)
			System.out.print(msg);
	}

	static public void end() {
		System.out.printf("%dms\r\n", System.currentTimeMillis() - time);
	}

}
