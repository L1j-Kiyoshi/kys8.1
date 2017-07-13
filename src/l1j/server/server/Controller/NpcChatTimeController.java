/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.Controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.IndunSystem.MiniGame.BattleZone;
import l1j.server.server.datatables.NpcChatTable;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.templates.L1NpcChat;

public class NpcChatTimeController implements Runnable {
	public static final int SLEEP_TIME = 60000;

	private static Logger _log = Logger.getLogger(NpcChatTimeController.class
			. getName());

	private static NpcChatTimeController _instance;

	public static NpcChatTimeController getInstance() {
		if (_instance == null) {
			_instance = new NpcChatTimeController();
		}
		return _instance;
	}

	@Override
	public void run() {
		try {
			checkNpcChatTime(); // チャット開始時間をチェック
			던전체크();
			
		} catch (Exception e1) {
			_log.warning(e1.getMessage());
		}
	}

	private void checkNpcChatTime() {
		for (L1NpcChat npcChat : NpcChatTable.getInstance(). getAllGameTime()) {
			if (isChatTime(npcChat.getGameTime())) {
				int npcId = npcChat.getNpcId();
				for (L1Object obj : L1World.getInstance(). getObject()) {
					if (! (obj instanceof L1NpcInstance)) {
						continue;
					}
					L1NpcInstance npc = (L1NpcInstance) obj;
					if (npc.getNpcTemplate(). get_npcId() == npcId) {
						npc.startChat(L1NpcInstance.CHAT_TIMING_GAME_TIME);
					}
				}
			}
		}
	}
	
	private void 던전체크() {
		Calendar cal = Calendar.getInstance();
		int 시간 = Calendar.HOUR;
		int 분 = Calendar.MINUTE;
		/** 0 午前、1午後 * */
		String 오전오후 = "午後";
		if (cal.get(Calendar.AM_PM) == 0) {
			오전오후 = "午前";
		}
		
		if (IsleController.getInstance().isgameStart == false) {
			if ((       cal.get(시간) == 1 && cal.get(분) == 59)
					|| (cal.get(시간) == 5 && cal.get(분) == 59)
					|| (cal.get(시간) == 9 && cal.get(분) == 59)
//					||午前午後.equals（ "午前"）&& cal.get（時間）== 10 && cal.get（分）== 0
					
				
					) {
				IsleController.getInstance().isgameStart = true;
				System.out.println("忘れられた島オープン：" + 오전오후 + " " + cal.get(시간) + "時" + cal.get(분) + "分");
			}
		}
		if(Config.배틀존작동유무){ 
			if (BattleZone.getInstance().getDuelStart() == false) {
				if ((   
						//午前午後.equals（ "午前"）&& cal.get（時間）== 5 && cal.get（分）== 0）
						// ||午前午後.equals（ "午前"）&& cal.get（時間）== 10 && cal.get（分）== 0
						// ||午前午後.equals（ "午後"）&& cal.get（時間）== 5 && cal.get（分）== 1
					 오전오후.equals("오후") && cal.get(시간) == 10 && cal.get(분) == 1)
//					||午前午後.equals（ "午前"）&& cal.get（時間）== 10 && cal.get（分）== 0
						) {
					BattleZone.getInstance().setGmStart(true);
				System.out.println("バトルゾーンオープン：" + 오전오후 + " " + cal.get(시간) + "時" + cal.get(분) + "分");
					}
				}
			}
	}
	



	private boolean isChatTime(int chatTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
		Calendar realTime = getRealTime();
		int nowTime = Integer.valueOf(sdf.format(realTime.getTime()));
		return (nowTime == chatTime);
	}

	private static Calendar getRealTime() {
		TimeZone _tz = TimeZone.getTimeZone(Config.TIME_ZONE);
		Calendar cal = Calendar.getInstance(_tz);
		return cal;
	}

}
