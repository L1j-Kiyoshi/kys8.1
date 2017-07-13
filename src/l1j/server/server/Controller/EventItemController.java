/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful ,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not , write to the Free Software
 * Foundation , Inc., 59 Temple Place - Suite 330, Boston , MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.Controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import l1j.server.Config;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class EventItemController implements Runnable {
	private static EventItemController _instance;
	
	public static final int SleepTime = 1 * 60 * 1000; //1分ごとにチェック

	public static EventItemController getInstance() {
		if (_instance == null) {
			_instance = new EventItemController();
		}
		return _instance;
	}

	@Override
	public void run() {
		try {
	    	checkEventItem(); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Calendar getRealTime() {
		TimeZone _tz = TimeZone.getTimeZone(Config.TIME_ZONE);
		Calendar cal = Calendar.getInstance(_tz);
		return cal;
	}

	private void checkEventItem() {
		SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
		Calendar realTime = getRealTime();
		int nowTime = Integer.valueOf(sdf.format(realTime.getTime()));
		int EventTime = Config.EVENT_TIME;
		int EventNumber = Config.EVENT_NUMBER;
		int EventItem = Config.EVENT_ITEM;
		if (EventTime == 0) return;
		
		if (nowTime % EventTime == 0) {
			for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
				if (!pc.isDead() && !pc.isPrivateShop() && !pc.isAutoClanjoin() && !pc.noPlayerCK && pc != null) {
					L1ItemInstance item = pc.getInventory().storeItem(EventItem, EventNumber);
					if (item == null)continue;
					if (item != null)
		 			pc.sendPackets(new S_SystemMessage(item.getName() + " (" + EventNumber + ") 獲得"));
				}
			}
		} else {
			return;
		}
	}

}
