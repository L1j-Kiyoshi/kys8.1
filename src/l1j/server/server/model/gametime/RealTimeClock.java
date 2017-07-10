package l1j.server.server.model.gametime;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.GeneralThreadPool;

public class RealTimeClock {
	private static Logger _log = Logger.getLogger(RealTimeClock.class.getName());

	private static RealTimeClock _instance;

	private volatile RealTime _currentTime = new RealTime();

	private RealTime _previousTime = null;

	private List<TimeListener> _listeners = new CopyOnWriteArrayList<TimeListener>();

	private class TimeUpdater implements Runnable {

		public void run() {
			while (true) {
				try {
					_previousTime = null;
					_previousTime = _currentTime;
					_currentTime = new RealTime();
					notifyChanged();

					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						_log.log(Level.SEVERE, "RealTimeClock[]Error", e);
					}
				} catch (Exception e) {
				}
			}
		}
	}

	private boolean isFieldChanged(int field) {
		return _previousTime.get(field) != _currentTime.get(field);
	}

	private void notifyChanged() {
		if (isFieldChanged(Calendar.MONTH)) {
			for (TimeListener listener : _listeners) {
				listener.onMonthChanged(_currentTime);
			}
		}
		if (isFieldChanged(Calendar.DAY_OF_MONTH)) {
			for (TimeListener listener : _listeners) {
				listener.onDayChanged(_currentTime);
			}
		}
		if (isFieldChanged(Calendar.HOUR_OF_DAY)) {
			for (TimeListener listener : _listeners) {
				listener.onHourChanged(_currentTime);
			}
		}
		if (isFieldChanged(Calendar.MINUTE)) {
			for (TimeListener listener : _listeners) {
				listener.onMinuteChanged(_currentTime);
			}
		}
	}

	private RealTimeClock() {
		GeneralThreadPool.getInstance().execute(new TimeUpdater());
	}

	public static void init() {
		_instance = new RealTimeClock();
	}

	public static RealTimeClock getInstance() {
		return _instance;
	}

	public RealTime getRealTime() {
		return _currentTime;
	}

	public void addListener(TimeListener listener) {
		_listeners.add(listener);
	}

	public void removeListener(TimeListener listener) {
		_listeners.remove(listener);
	}

	public Calendar getRealTimeCalendar() {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+9")); //韓国
		// 時間
		return cal;
	}
}
