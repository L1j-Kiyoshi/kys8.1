package l1j.server.server.utils;

import java.util.Random;

/**
 * <p>
 * 最低値lowと最大値highに囲まれ、数値の範囲を指定するクラスです。
 * </p>
 * <p>
 * <b>このクラスは、同期されない。</ b>複数のthreadが同時に、このクラスのインスタンスにアクセスし、
 * 1つ以上のthreadが範囲を変更する場合は、外部的な同期が必要である。
 * </p>
 */
public class IntRange {
    private static final Random _rnd = new Random(System.nanoTime());
    private int _low;
    private int _high;

    public IntRange(int low, int high) {
        _low = low;
        _high = high;
    }

    public IntRange(IntRange range) {
        this(range._low, range._high);
    }

    public static int random(int number) {
        Random rnd = new Random();
        return rnd.nextInt(number);
    }

    /**
     * 数値iが、範囲内にあるかを返す。
     *
     * @param i 数値
     * @return 範囲内であれば true
     */
    public boolean includes(int i) {
        return (_low <= i) && (i <= _high);
    }

    public static boolean includes(int i, int low, int high) {
        return (low <= i) && (i <= high);
    }

    /**
     * 値iを、この範囲内に丸める。
     *
     * @param i 数値
     * @return 丸められた値
     */
    public int ensure(int i) {
        int r = i;
        r = (_low <= r) ? r : _low;
        r = (r <= _high) ? r : _high;
        return r;
    }

    public static int ensure(int n, int low, int high) {
        int r = n;
        r = (low <= r) ? r : low;
        r = (r <= high) ? r : high;
        return r;
    }

    /**
     * この範囲内からランダムな値を生成する。
     *
     * @return 範囲内のランダムな値
     */
    public int randomValue() {
        return _rnd.nextInt(getWidth() + 1) + _low;
    }

    public int getLow() {
        return _low;
    }

    public int getHigh() {
        return _high;
    }

    public int getWidth() {
        return _high - _low;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IntRange)) {
            return false;
        }
        IntRange range = (IntRange) obj;
        return (this._low == range._low) && (this._high == range._high);
    }

    @Override
    public String toString() {
        return "low=" + _low + ", high=" + _high;
    }
}
