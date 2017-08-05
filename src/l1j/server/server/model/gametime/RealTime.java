package l1j.server.server.model.gametime;

import java.util.Calendar;
import java.util.TimeZone;

public class RealTime extends BaseTime {
    @Override
    protected Calendar makeCalendar(int time) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+9")); // 韓国
        // 時間
        cal.setTimeInMillis(0);
        cal.add(Calendar.SECOND, _time);
        return cal;
    }

    @Override
    protected long getBaseTimeInMil() {
        return 0;
    }

    @Override
    protected int makeTime(long timeMillis) {
        long t1 = timeMillis - getBaseTimeInMil();
        if (t1 < 0) {
            throw new IllegalArgumentException();
        }
        int t2 = (int) (t1 / 1000L);
        return t2;
    }
}
