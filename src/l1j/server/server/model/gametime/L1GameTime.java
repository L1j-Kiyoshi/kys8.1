/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.model.gametime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import l1j.server.server.utils.IntRange;

public class L1GameTime {
    // 2003年7月3日12:00（UTC）が1月1日00:00
    private static final long BASE_TIME_IN_MILLIS_REAL = 1057233600000L;
    private final int _time;
    private final Calendar _calendar;

    private Calendar makeCalendar(int time) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTimeInMillis(0);
        cal.add(Calendar.SECOND, _time);
        return cal;
    }

    public L1GameTime(long timeMillis) {
        long t1 = timeMillis - BASE_TIME_IN_MILLIS_REAL;
        if (t1 < 0) {
            throw new IllegalArgumentException();
        }
        t1 %= 86400000L; //Bug fix
        int t2 = (int) ((t1 * 6) / 1000L);
        int t3 = t2 % 3; // 時間が3の倍数になるように調整
        _time = t2 - t3;

        _calendar = makeCalendar(_time);
    }

    public L1GameTime() {
        this(System.currentTimeMillis());
    }

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
        return !IntRange.includes(hour, 6, 17); // 6:00-17:59（昼）でなければtrue
    }

    @Override
    public String toString() {
        SimpleDateFormat f = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
        f.setTimeZone(_calendar.getTimeZone());
        return f.format(_calendar.getTime()) + "(" + getSeconds() + ")";
    }
}
