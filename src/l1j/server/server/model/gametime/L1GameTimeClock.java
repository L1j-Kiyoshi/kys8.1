package l1j.server.server.model.gametime;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import l1j.server.server.GeneralThreadPool;

public class L1GameTimeClock {
    private static int TIMER_INTERVAL = 1000;
    private static L1GameTimeClock _instance;
    private volatile L1GameTime _currentTime = new L1GameTime();
    private L1GameTime _previousTime = new L1GameTime();

    private List<L1GameTimeListener> _listeners = new CopyOnWriteArrayList<L1GameTimeListener>();


    private class TimeUpdater implements Runnable {
        @Override
        public void run() {
            _previousTime = _currentTime;
            _currentTime = new L1GameTime();
            notifyChanged();
        }
    }

    private boolean isFieldChanged(int field) {
        return _previousTime.get(field) != _currentTime.get(field);
    }

    private void notifyChanged() {
        if (isFieldChanged(Calendar.MONTH)) {
            for (L1GameTimeListener listener : _listeners) {
                listener.onMonthChanged(_currentTime);
            }
        }
        if (isFieldChanged(Calendar.DAY_OF_MONTH)) {
            for (L1GameTimeListener listener : _listeners) {
                listener.onDayChanged(_currentTime);
            }
        }
        if (isFieldChanged(Calendar.HOUR_OF_DAY)) {
            for (L1GameTimeListener listener : _listeners) {
                listener.onHourChanged(_currentTime);
            }
        }
        if (isFieldChanged(Calendar.MINUTE)) {
            for (L1GameTimeListener listener : _listeners) {
                listener.onMinuteChanged(_currentTime);
            }
        }
    }

    private L1GameTimeClock() {
        GeneralThreadPool.getInstance().scheduleAtFixedRate(new TimeUpdater(), 0, TIMER_INTERVAL);
    }

    public static void init() {
        _instance = new L1GameTimeClock();
    }

    public static L1GameTimeClock getInstance() {
        return _instance;
    }

    public L1GameTime getGameTime() {
        return _currentTime;
    }

    public void addListener(L1GameTimeListener listener) {
        _listeners.add(listener);
    }

    public void removeListener(L1GameTimeListener listener) {
        _listeners.remove(listener);
    }
}
