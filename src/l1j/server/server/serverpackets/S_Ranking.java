/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.serverpackets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.utils.SQLUtil;

public class S_Ranking extends ServerBasePacket {

	private static final String S_Ranking = "[C] S_Ranking";

	private static Logger _log = Logger.getLogger(S_Ranking.class.getName());

	private byte[] _byte = null;

	private int j = 0;

	static String[] name;

	public S_Ranking(L1PcInstance pc, int number) {
		name = new String[10];
		buildPacket(pc, number);
	}

	

	private void buildPacket(L1PcInstance pc, int number) {
		String date = time();
		String type = null;
		String title = null;
		writeC(Opcodes.S_BOARD_READ);
		writeD(number);
		writeS("랭킹 관리자");
		switch (number) {
		case 1:
			//title = "전체 랭킹";
			title = "전사 랭킹";
			break;
		case 2:
			title = "군주 랭킹";
			break;
		case 3:
			title = "기사 랭킹";
			break;
		case 4:
			title = "요정 랭킹";
			break;
		case 5:
			title = "법사 랭킹";
			break;
		case 6:
			title = "다엘 랭킹";
			break;
		case 7:
			title = "용기사 랭킹";
			break;
		case 8:
			title = "환술사 랭킹";
			break;
		}
		writeS(title);
		writeS(date);
		switch (pc.getType()) {
		case 0:
			type = "군주";
			break;
		case 1:
			type = "기사";
			break;
		case 2:
			type = "요정";
			break;
		case 3:
			type = "마법사";
			break;
		case 4:
			type = "다크엘프";
			break;
		case 5:
			type = "용기사";
			break;
		case 6:
			type = "환술사";
			break;
		case 7:
			type = "전사";
			break;
		}
		int p = Rank(pc, number);
		if (number == 9) {
			writeS("\n\r\n\r\n\r" + pc.getName() + "님의 전체랭킹 : " + p + "위"
					+ "\n\r\n\r\n\r" + type + " 클래스 랭킹 : " + j + "위"
					+ "\n\r\n\r\n\r" + "        ");

		} else {
			writeS("\n\r" + "     1위 " + name[0] + "\n\r" +
					"     2위 " + name[1] + "\n\r" +
					"     3위 " + name[2] + "\n\r" +
					"     4위 " + name[3] + "\n\r" +
					"     5위 " + name[4] + "\n\r" +
					"     6위 " + name[5] + "\n\r" +
					"     7위 " + name[6] + "\n\r" +
					"     8위 " + name[7] + "\n\r" +
					"     9위 " + name[8] + "\n\r" +
					"    10위 " + name[9] + "\n\r" +
			"             ");
		}
	}
	private int Rank(L1PcInstance pc, int number) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		int objid = pc.getId();
		int i = 0;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			switch (number) {
			case 1:
				pstm = con
						//.prepareStatement("SELECT char_name FROM characters WHERE AccessLevel = 0 order by Exp desc limit 10"); //전체랭킹 주석
						.prepareStatement("SELECT char_name FROM characters WHERE Type = 7 And AccessLevel = 0 order by Exp desc limit 10");
				break;
			case 2:
				pstm = con
						.prepareStatement("SELECT char_name FROM characters WHERE Type = 0 And AccessLevel = 0 order by Exp desc limit 10");
				break;
			case 3:
				pstm = con
						.prepareStatement("SELECT char_name FROM characters WHERE Type = 1 And AccessLevel = 0 order by Exp desc limit 10");
				break;
			case 4:
				pstm = con
						.prepareStatement("SELECT char_name FROM characters WHERE Type = 2 And AccessLevel = 0 order by Exp desc limit 10");
				break;
			case 5:
				pstm = con
						.prepareStatement("SELECT char_name FROM characters WHERE Type = 3 And AccessLevel = 0 order by Exp desc limit 10");
				break;
			case 6:
				pstm = con
						.prepareStatement("SELECT char_name FROM characters WHERE Type = 4 And AccessLevel = 0 order by Exp desc limit 10");
				break;
			case 7:
				pstm = con
						.prepareStatement("SELECT char_name FROM characters WHERE Type = 5 And AccessLevel = 0 order by Exp desc limit 10");
				break;
			case 8:
				pstm = con
						.prepareStatement("SELECT char_name FROM characters WHERE Type = 6 And AccessLevel = 0 order by Exp desc limit 10");
				break;
			case 9:
				pstm = con
						.prepareStatement("SELECT objid FROM characters WHERE AccessLevel = 0 order by Exp desc");
				break;

			default:
				pstm = con
						.prepareStatement("SELECT char_name FROM characters WHERE AccessLevel = 0 order by Exp desc limit 10");
				break;
			}

			rs = pstm.executeQuery();
			if (number == 9) {
				while (rs.next()) {
					i++;
					if (objid == rs.getInt(1))
						break;
				}
				String sql = "SELECT objid FROM characters WHERE Type = ";
				sql = (new StringBuilder(String.valueOf(sql))).append(
						pc.getType()).toString();
				sql = (new StringBuilder(String.valueOf(sql))).append(
						" And AccessLevel = 0 order by Exp desc").toString();
				pstm = con.prepareStatement(sql);
				rs = pstm.executeQuery();
				j = 0;
				while (rs.next()) {
					j++;
					if (objid == rs.getInt(1))
						break;
				}
			} else {
				while (rs.next()) {
					name[i] = rs.getString(1);
					i++;
				}

				// 레코드가 없거나 5보다 작을때
				while (i < 10) {
					name[i] = " ";
					i++;
				}
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, "S_Ranking[]Error", e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}

		return i;
	}

	private static String time() {
		TimeZone tz = TimeZone.getTimeZone(Config.TIME_ZONE);
		Calendar cal = Calendar.getInstance(tz);
		int year = cal.get(Calendar.YEAR) - 2000;
		String year2;
		if (year < 10) {
			year2 = "0" + year;
		} else {
			year2 = Integer.toString(year);
		}
		int Month = cal.get(Calendar.MONTH) + 1;
		String Month2 = null;
		if (Month < 10) {
			Month2 = "0" + Month;
		} else {
			Month2 = Integer.toString(Month);
		}
		int date = cal.get(Calendar.DATE);
		String date2 = null;
		if (date < 10) {
			date2 = "0" + date;
		} else {
			date2 = Integer.toString(date);
		}
		return year2 + "/" + Month2 + "/" + date2;
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	public String getType() {
		return S_Ranking;
	}

}
