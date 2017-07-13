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

package l1j.server.server.clientpackets;

import l1j.server.server.GameClient;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_WarTimeSet extends ClientBasePacket {

	private static final String C_WAR_TIME_SET = "[C] C_WarTimeSet";

	public C_WarTimeSet(byte abyte0[], GameClient clientthread)
			throws Exception {
		super(abyte0);
		/*
		 * int listNo = readC();
		 * 
		 * L1PcInstance pc = clientthread.getActiveChar();
		 * 
		 * L1Clan clan = L1World.getInstance().getClan(pc.getClanname()); if
		 * (clan != null) { int castle_id = clan.getCastleId(); if (castle_id !=
		 * 0) { // 城主クランL1Castle l1castle =
		 * CastleTable.getInstance().getCastleTable(castle_id);
		 * 
		 * Calendar warTime = l1castle.getWarTime(); int year =
		 * warTime.get(Calendar.YEAR); int month = warTime.get(Calendar.MONTH);
		 * int date = warTime.get(Calendar.DATE); String msg = null;
		 * 
		 * switch (listNo) { case 1: warTime.set(year, month, date, 18, 00); msg
		 * = ""+year + "年" +（month + 1）+ "月" + date + "日18時00分"; break; case 2：
		 * warTime.set(year, month, date, 18, 30); msg =
		 * ""+year + "年" +（month + 1）+ "月" + date + "日18時30分"; break; case 3：
		 * warTime.set(year, month, date, 19, 00); msg =
		 * ""+year + "年" +（month + 1）+ "月" + date + "日19時00分"; break; case 4：
		 * warTime.set(year, month, date, 22, 00); msg =
		 * ""+year + "年" +（month + 1）+ "月" + date + "日22時00分"; break; case 5：
		 * warTime.set(year, month, date, 22, 30); msg =
		 * ""+year + "年" +（month + 1）+ "月" + date + "日22時30分"; break; case 6：
		 * warTime.set(year, month, date, 23, 00); msg =
		 * ""+year + "年" +（month + 1）+ "月" + date + "日23時00分"; break; default：break;
		 * } CastleTable.getInstance().updateCastle(l1castle);
		 * pc.sendPackets（new S_ServerMessage（304、msg））; //次の攻城戦の時間が％0で
		 * 決定された。 }}
		 */
		clear();
	}

	@Override
	public String getType() {
		return C_WAR_TIME_SET;
	}

}
