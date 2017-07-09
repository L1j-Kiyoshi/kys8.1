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
		writeS("ランキング管理者");
		switch (number) {
		case 1:
			//title = "全体ランキング";
			title = "戦士ランキング";
			break;
		case 2:
			title = "君主ランキング";
			break;
		case 3:
			title = "記事ランキング";
			break;
		case 4:
			title = "妖精ランキング";
			break;
		case 5:
			title = "玄ランキング";
			break;
		case 6:
			title = "すべてへランキング";
			break;
		case 7:
			title = "用記事ランキング";
			break;
		case 8:
			title = "イリュージョニストランキング";
			break;
		}
		writeS(title);
		writeS(date);
		switch (pc.getType()) {
		case 0:
			type = "君主";
			break;
		case 1:
			type = "記事";
			break;
		case 2:
			type = "妖精";
			break;
		case 3:
			type = "ウィザード";
			break;
		case 4:
			type = "ダークエルフ";
			break;
		case 5:
			type = "竜騎士";
			break;
		case 6:
			type = "イリュージョニスト";
			break;
		case 7:
			type = "戦士";
			break;
		}
		int p = Rank(pc, number);
		if (number == 9) {
			writeS("\n\r\n\r\n\r" + pc.getName() + "さんの全ランキング：" + p + "位"
					+ "\n\r\n\r\n\r" + type + "クラスランキング：" + j + "位"
					+ "\n\r\n\r\n\r" + "        ");

		} else {
			writeS("\n\r" + "     1位 " + name[0] + "\n\r" +
					"     2位 " + name[1] + "\n\r" +
					"     3位 " + name[2] + "\n\r" +
					"     4位 " + name[3] + "\n\r" +
					"     5位 " + name[4] + "\n\r" +
					"     6位 " + name[5] + "\n\r" +
					"     7位 " + name[6] + "\n\r" +
					"     8位 " + name[7] + "\n\r" +
					"     9位 " + name[8] + "\n\r" +
					"    10位 " + name[9] + "\n\r" +
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
						//.prepareStatement("SELECT char_name FROM characters WHERE AccessLevel = 0 order by Exp desc limit 10"); //全体ランキングコメント
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

				// レコードがない場合、または5よりも小さいとき
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
