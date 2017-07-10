package l1j.server.server.model.gametime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import l1j.server.server.utils.IntRange;

public abstract class BaseTime {
	protected final int _time;

	protected final Calendar _calendar;

	public BaseTime() {
		this(System.currentTimeMillis());
	}

	public BaseTime(long timeMillis) {
		_time = makeTime(timeMillis);
		_calendar = makeCalendar(_time);
	}

	protected Calendar makeCalendar(int time) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		cal.setTimeInMillis(0);
		cal.add(Calendar.SECOND, _time);
		return cal;
	}

	protected abstract int makeTime(long timeMillis);

	protected abstract long getBaseTimeInMil();

	public int get(int field) {
		return _calendar.get(field);
	}

	public int getSeconds() {
		return _time;
	}

	public Calendar getCalendar() {
		return (Calendar) _calendar.clone();
	}

	public boolean isNight() {
		int hour = _calendar.get(Calendar.HOUR_OF_DAY);
		return !IntRange.includes(hour, 6, 17); // 6:00-17:59、日の場合はtrue
	}

	public String toString() {
		SimpleDateFormat f = new SimpleDateFormat(
				"yyyy.MM.dd G 'at' HH:mm:ss z");
		f.setTimeZone(_calendar.getTimeZone());
		return f.format(_calendar.getTime()) + "(" + getSeconds() + ")";
	}
}