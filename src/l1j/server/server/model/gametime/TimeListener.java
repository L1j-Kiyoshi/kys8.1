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

/**
 * <p>
 * アデン時間の変化を受け取るためのListenerインタフェース。
 * </p>
 * <p>
 * アデン時間の変化を監視しなければならクラスは、 * このインタフェースに含まれているすべてのメソッドを定義して、
 * このインタフェースを実装するか、関連するメソッドだけをオーバーライド（override）してabstractクラスL1GameTimeAdapterを拡張する。
 * そのようなクラスから作成されたリスナーオブジェクトは、L1GameTimeClockのaddListenerメソッドを使用してL1GameTimeClockに登録される。
 * アデン時間変化の通知は、年月時分がそれぞれ変わったときに行われる。
 * これらのメソッドは、L1GameTimeClockのthread上で動作する。
 * これらのメソッドの処理に時間がかかった場合には、他のリスナーへの通知が遅れる可能性がある。
 * 完了までの時間を必要とする処理や、threadをブロックするメソッドの呼び出しが含まれている処理を実施する場合は、
 * 内部で新たにthreadを作成し処理を行うべきである。
 */
public interface TimeListener {
    /**
     * アデン時間に月が変わったときに呼び出される。
     *
     * @param time 最新のアデン時間
     */
    public void onMonthChanged(BaseTime time);

    /**
     * アデン時間に日が変わったときに呼び出される。
     *
     * @param time 最新のアデン時間
     */
    public void onDayChanged(BaseTime time);

    /**
     * アデン時間に時間が変わったときに呼び出される。
     *
     * @param time 最新のアデン時間
     */
    public void onHourChanged(BaseTime time);

    /**
     * アデン時間に分変わったときに呼び出される。
     *
     * @param time 最新のアデン時間
     */
    public void onMinuteChanged(BaseTime time);
}
