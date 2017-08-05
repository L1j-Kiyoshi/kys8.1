package l1j.server.server.utils;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

import l1j.server.server.model.Instance.L1ItemInstance;

public class CommonUtil {
    /**
     * 2011.08.05 金額表示
     *
     * @param number
     * @return
     */
    public static String numberFormat(int number) {
        try {
            NumberFormat nf = NumberFormat.getInstance();

            return nf.format(number);
        } catch (Exception e) {
            return Integer.toString(number);
        }
    }

    /**
     * 2011.08.05 ランダム関数
     *
     * @param number
     * @return
     */
    public static int random(int number) {
        Random rnd = new Random();

        return rnd.nextInt(number);
    }

    /**
     * 2011.08.05 ランダム関数
     *
     * @param lbound
     * @param ubound
     * @return
     */
    public static int random(int lbound, int ubound) {
        return (int) ((Math.random() * (ubound - lbound + 1)) + lbound);
    }

    /**
     * 2011.08.30 データフォーマット
     *
     * @param type
     * @return
     */
    public static String dateFormat(String type) {
        SimpleDateFormat sdf = new SimpleDateFormat(type, Locale.KOREA);
        return sdf.format(Calendar.getInstance().getTime());
    }

    /**
     * 2011.08.30 データフォーマット
     *
     * @param type
     * @return
     */
    public static String dateFormat(String type, Timestamp date) {
        SimpleDateFormat sdf = new SimpleDateFormat(type, Locale.KOREA);
        return sdf.format(date.getTime());
    }

    /**
     * 2011.08.31 アイテムの終了時間
     *
     * @param item
     * @param minute
     */
    public static void SetTodayDeleteTime(L1ItemInstance item, int minute) {
        Timestamp deleteTime = null;
        deleteTime = new Timestamp(System.currentTimeMillis() + (60000 * minute));
        item.setEndTime(deleteTime);
    }

    /**
     * 2011.08.31 アイテムの終了時刻を指定する（今日の残り時間を計算） - ロトシステム
     *
     * @param item
     */
    public static void SetTodayDeleteTime(L1ItemInstance item) {
        int hour = Integer.parseInt(dateFormat("HH"));
        int minute = Integer.parseInt(dateFormat("mm"));
        int time = 0;

        if (hour <= 22 && minute < 30) {
            time = (23 - hour) * 60 + (60 - minute) - 60;
        } else {
            time = (23 - hour) * 60 + (60 - minute) - 60 + (24 * 60);
        }

        Timestamp deleteTime = null;

        deleteTime = new Timestamp(System.currentTimeMillis() + (60000 * time));
        item.setEndTime(deleteTime);
    }

    /**
     * 2011.08.31 指定時間までの残り時間
     *
     * @param item
     */
    public static int getRestTime(int hh) {
        int hour = Integer.parseInt(dateFormat("HH"));
        int minute = Integer.parseInt(dateFormat("mm"));

        int time = 0;

        time = (hh - hour) * 60 - minute;

        return time;

    }
}
