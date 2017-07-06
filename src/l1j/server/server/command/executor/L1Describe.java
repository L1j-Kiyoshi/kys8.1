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
package l1j.server.server.command.executor;

import java.util.StringTokenizer;

import l1j.server.server.Account;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
//import java.util.logging.Logger;

public class L1Describe implements L1CommandExecutor {
	//private static Logger _log = Logger.getLogger(L1Describe.class.getName());

	private L1Describe() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Describe();
	}


	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer st = new StringTokenizer(arg);
			String name = st.nextToken();
			L1PcInstance target = L1World.getInstance(). getPlayer(name);
			if (target == null) {
				pc.sendPackets(new S_ServerMessage(73, name)); // \f1%0은 게임을 하고 있지 않습니다.
				return;
			}		
			int lv = target.getLevel();
			int currentLvExp = ExpTable.getExpByLevel(lv);
			int nextLvExp = ExpTable.getExpByLevel(lv + 1);
			double neededExp = nextLvExp - currentLvExp ;
			double currentExp =  target.getExp() - currentLvExp;
			int per = (int)((currentExp / neededExp) * 100.0);
			
			String typeName = null;
			switch(target.getType()){
			case 0:
				typeName = "군주";
				break;

			case 1:
				typeName = "기사";
				break;

			case 2:
				typeName = "요정";
				break;

			case 3:
				typeName = "마법사";
				break;

			case 4:
				typeName = "다크엘프";
				break;

			case 5:
				typeName = "용기사";
				break;

			case 6:
				typeName = "환술사";
				break;
				
			case 7:
				typeName = "전사";
				break;

			default:
				typeName = "????";
			}
			pc.sendPackets(new S_SystemMessage("\\aD--------------------------------------------------"));
			pc.sendPackets(new S_SystemMessage("\\aD[ " + target.getName() + " ] 직업:" + typeName + ", 혈맹:" + target.getClanname()));
			if (!target.noPlayerCK) {
			pc.sendPackets(new S_SystemMessage("\\aD계정: " + target.getAccountName() + " / " + Account.load(target.getAccountName()).get_Password() + "   IP: " + target.getNetConnection().getIp()));
			//pc.sendPackets(new S_SystemMessage("IP : " + target.getNetConnection().getIp()));
			}	
			pc.sendPackets(new S_SystemMessage("\\aD--------------------------------------------------"));
			pc.sendPackets(new S_SystemMessage("\\aL* Lv: " + lv +" (" + per + "%)" + "   방어: " + target.getAC().getAc() + "   마방: " + target.getResistance().getMr())); 
			int hpr = target.getHpr() + target.getInventory(). hpRegenPerTick();
			int mpr = target.getMpr() + target.getInventory(). mpRegenPerTick();
			pc.sendPackets(new S_ChatPacket(pc,"* 피: " + target.getCurrentHp() + '/' + target.getMaxHp() + " (틱: " + hpr + ')' + "   엠: " + target.getCurrentMp() + '/' + target.getMaxMp() + " (틱: " + mpr + ')'));
			pc.sendPackets(new S_ChatPacket(pc,"* 힘: " + target.getAbility().getTotalStr() + "  " + "덱: " + target.getAbility().getTotalDex() + "   "
					    						+ "콘: " + target.getAbility().getTotalCon() + "   " + "인: " + target.getAbility().getTotalInt() + "   "
					    						+ "위: " + target.getAbility().getTotalWis() + "   " + "카: " + target.getAbility().getTotalCha()));
			pc.sendPackets(new S_ChatPacket(pc,"* 불: " + target.getResistance().getFire() + "  물: " + target.getResistance().getWater() + "  바람: " + target.getResistance().getWind() + "  땅: " + target.getResistance().getEarth()));		
			pc.sendPackets(new S_ChatPacket(pc,"* 홀드: " + target.getResistance().getHold() + "  동빙: " + target.getResistance().getFreeze() + "  수면: " + target.getResistance().getSleep() + "  스턴: " + target.getResistance().getStun() + "  공포: " + target.getResistance().getDESPERADO() + "  리덕: " + target.getDamageReductionByArmor())); 
			pc.sendPackets(new S_ChatPacket(pc,"* 추타: " + target.getDmgup() + "  공성: " + target.getHitup() + "  활타: " + target.getBowDmgup() + "  활명: " + target.getBowHitup() + "  주술: " + target.getAbility().getSp()));
			pc.sendPackets(new S_SystemMessage("\\aD--------------------------------------------------"));
		} catch (Exception e) {
			pc.sendPackets(new S_ChatPacket(pc,".정보 [캐릭명] 으로 입력."));
		}
	}
}
