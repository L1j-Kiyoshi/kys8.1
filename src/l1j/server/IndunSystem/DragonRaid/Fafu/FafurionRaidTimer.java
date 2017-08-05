package l1j.server.IndunSystem.DragonRaid.Fafu;

import l1j.server.server.GeneralThreadPool;

public class FafurionRaidTimer implements Runnable {

    private final FafurionRaid _ar;
    private final int _type;
    private final int _step;
    private final int _timeMillis;

    public FafurionRaidTimer(FafurionRaid ar, int type, int step, int timeMillis) {
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