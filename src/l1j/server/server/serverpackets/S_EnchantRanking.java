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
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.utils.SQLUtil;

public class S_EnchantRanking extends ServerBasePacket {

	private static final String S_EnchantRanking = "[C] S_EnchantRanking";

	private static Logger _log = Logger.getLogger(S_EnchantRanking.class
			.getName());

 	private byte[] _byte = null;
 	private int j = 0;
  static String[] name;
  static String[] name1;
  static String[] castlename;
  static String[] clanname;
  static String[] leadername;
  static int[] enchantlvl;
  static int[] aden;
  static int[] armor;
  static int[] level;
  static int[] Ac;
  static int[] priaden;
  static int[] castleid;
  static int[] hascastle;
  static int[] taxrate;
  static int[] castleaden;
  static int[] MaxHp;
  static int[] MaxMp;
 	public S_EnchantRanking(L1PcInstance pc, int number) {
    name = new String[10];
    name1 = new String[10];
    enchantlvl = new int [10];
    aden = new int [10];
    armor = new int [10];
    level = new int [10];
    Ac = new int [10];
    priaden = new int [10];
    castlename = new String [10];
    clanname = new String [10];
    leadername = new String [10];
    castleid = new int [10];
    hascastle = new int [10];
    taxrate = new int [10];
    castleaden = new int [10];
    MaxHp = new int [10];
    MaxMp = new int [10];
  	buildPacket(pc, number);
 	}

	

 	private void buildPacket(L1PcInstance pc, int number) {
 		String date = time();
 		String type = null;
 		String title = null;
	  writeC(Opcodes.S_BOARD_READ);
	  writeD(number);
	  writeS("운영자");
	  switch(number) {
	  	case 1:
	  		title = "인첸 랭킹";
	  		break;
	  	case 2:
	  		title = "방어구 랭킹";
	  		break;
	  	case 3:
	  		title = "아덴 랭킹";
	  		break;
	  	case 4:
	  		title = "레벨 랭킹";
	  		break;
	  	case 5:
	  		title = "신비깃털 랭킹";
	  		break;
	  	case 6:
	  		title = "창고아덴랭킹";
	  		break;
	  	case 7:
	  		title = "HP 랭킹";
	  		break;
	  	case 8:
	  		title = "MP 랭킹";
	  		break;
	  }
	  writeS(title);
	  writeS(date);
	  switch(pc.getType()) {
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
	  }
		int p = Rank(pc, number);
	    if(number == 1) { //추가부분입니다
	        writeS("\n\r" + "  1위 "+ "+" + enchantlvl[0] + " " + name[0] + "\n\r" + "  소유자 : " + name1[0] +"\n\r" +
	  	          "  2위 " + "+" + enchantlvl[1] + " " + name[1] +"\n\r" + "  소유자 : " + name1[1] + "\n\r" +
	  	          "  3위 " + "+" + enchantlvl[2]  + " " + name[2]+ "\n\r" + "  소유자 : " + name1[2] + "\n\r" +
	  	          "  4위 " + "+" + enchantlvl[3] + " " + name[3] + "\n\r" + "  소유자 : " + name1[3] + "\n\r" +
	  	          "  5위 " + "+" + enchantlvl[4] + " " + name[4] + "\n\r" + "  소유자 : " + name1[4] + "\n\r" +
	  	          "  6위 " + "+" + enchantlvl[5] + " " + name[5] + "\n\r" + "  소유자 : " + name1[5] + "\n\r" +
	  	          "  7위 " + "+" + enchantlvl[6] + " " + name[6] + "\n\r" + "  소유자 : " + name1[6] +"\n\r" +
	  	          "  8위 " + "+" + enchantlvl[7] + " " + name[7] + "\n\r" + "  소유자 : " + name1[7] +"\n\r" +
	  	          "  9위 " + "+" + enchantlvl[8] + " " + name[8] + "\n\r" + "  소유자 : " + name1[8] +"\n\r"+
	  	          " 10위 " + "+" + enchantlvl[9] + " " + name[9] + "\n\r" + "  소유자 : " + name1[9] +"\n\r" +
	  	                      "      ");
	  }else if(number == 2) { //추가부분입니다
	        writeS("\n\r" + "  1위 "+ "+" + armor[0] + " " + name[0] + "\n\r" + "  소유자 : " + name1[0] +"\n\r" +
	  	          "  2위 " + "+" + armor[1] + " " + name[1] +"\n\r" + "  소유자 : " + name1[1] + "\n\r" +
	  	          "  3위 " + "+" + armor[2]  + " " + name[2]+ "\n\r" + "  소유자 : " + name1[2] + "\n\r" +
	  	          "  4위 " + "+" + armor[3] + " " + name[3] + "\n\r" + "  소유자 : " + name1[3] + "\n\r" +
	  	          "  5위 " + "+" + armor[4] + " " + name[4] + "\n\r" + "  소유자 : " + name1[4] + "\n\r" +
	  	          "  6위 " + "+" + armor[5] + " " + name[5] + "\n\r" + "  소유자 : " + name1[5] + "\n\r" +
	  	          "  7위 " + "+" + armor[6] + " " + name[6] + "\n\r" + "  소유자 : " + name1[6] +"\n\r" +
	  	          "  8위 " + "+" + armor[7] + " " + name[7] + "\n\r" + "  소유자 : " + name1[7] +"\n\r" +
	  	          "  9위 " + "+" + armor[8] + " " + name[8] + "\n\r" + "  소유자 : " + name1[8] +"\n\r"+
	  	          " 10위 " + "+" + armor[9] + " " + name[9] + "\n\r" + "  소유자 : " + name1[9] +"\n\r" +
	  	                      "      ");
	  }else if(number == 3) { //추가부분입니다
	        writeS("\n\r" + "  1위 "+ "$ " + aden[0] + " 아데나\n\r" + "  소유자 : " + name[0] + "\n\r" +
	  	          "  2위 " + "$ " + aden[1] + " 아데나\n\r" + "  소유자 : " + name[1] + "\n\r" +
	  	          "  3위 " + "$ " + aden[2] + " 아데나\n\r" + "  소유자 : " + name[2] + "\n\r" +
	  	          "  4위 " + "$ " + aden[3] + " 아데나\n\r" + "  소유자 : " + name[3] + "\n\r" +
	  	          "  5위 " + "$ " + aden[4] + " 아데나\n\r" + "  소유자 : " + name[4] + "\n\r" +
	  	          "  6위 " + "$ " + aden[5] + " 아데나\n\r" + "  소유자 : " + name[5] + "\n\r" +
	  	          "  7위 " + "$ " + aden[6] + " 아데나\n\r" + "  소유자 : " + name[6] + "\n\r" +
	  	          "  8위 " + "$ " + aden[7] + " 아데나\n\r" + "  소유자 : " + name[7] + "\n\r" +
	  	          "  9위 " + "$ " + aden[8] + " 아데나\n\r" + "  소유자 : " + name[8] + "\n\r" +
	  	          " 10위 " + "$ " + aden[9] + " 아데나\n\r" + "  소유자 : " + name[9] + "\n\r" +
	  	                      "      ");
	  }else if(number == 4) { //추가부분입니다
	        writeS("\n\r" + "  1위 " + name[0] + " \n\r" + "  현재레벨 : " + level[0] + "\n\r" +
	  	          "  2위 " + name[1] + " \n\r" + "  현재레벨 : " + level[1] + "\n\r" +
	  	          "  3위 " + name[2] + " \n\r" + "  현재레벨 : " + level[2] + "\n\r" +
	  	          "  4위 " + name[3] + " \n\r" + "  현재레벨 : " + level[3] + "\n\r" +
	  	          "  5위 " + name[4] + " \n\r" + "  현재레벨 : " + level[4] + "\n\r" +
	  	          "  6위 " + name[5] + " \n\r" + "  현재레벨 : " + level[5] + "\n\r" +
	  	          "  7위 " + name[6] + " \n\r" + "  현재레벨 : " + level[6] + "\n\r" +
	  	          "  8위 " + name[7] + " \n\r" + "  현재레벨 : " + level[7] + "\n\r" +
	  	          "  9위 " + name[8] + " \n\r" + "  현재레벨 : " + level[8] + "\n\r" +
	  	          " 10위 " + name[9] + " \n\r" + "  현재레벨 : " + level[9] + "\n\r" +
	                      "      ");
	  }else if(number == 5) { //추가부분입니다
	        writeS("\n\r" + "  1위 "+ priaden[0] + "개의 깃털\n\r" + "  소유자 : " + name[0] + "\n\r" +
	  	          "  2위 " + priaden[1] + "개의 깃털\n\r" + "  소유자 : " + name[1] + "\n\r" +
	  	          "  3위 " + priaden[2] + "개의 깃털\n\r" + "  소유자 : " + name[2] + "\n\r" +
	  	          "  4위 " + priaden[3] + "개의 깃털\n\r" + "  소유자 : " + name[3] + "\n\r" +
	  	          "  5위 " + priaden[4] + "개의 깃털\n\r" + "  소유자 : " + name[4] + "\n\r" +
	  	          "  6위 " + priaden[5] + "개의 깃털\n\r" + "  소유자 : " + name[5] + "\n\r" +
	  	          "  7위 " + priaden[6] + "개의 깃털\n\r" + "  소유자 : " + name[6] + "\n\r" +
	  	          "  8위 " + priaden[7] + "개의 깃털\n\r" + "  소유자 : " + name[7] + "\n\r" +
	  	          "  9위 " + priaden[8] + "개의 깃털\n\r" + "  소유자 : " + name[8] + "\n\r" +
	  	          " 10위 " + priaden[9] + "개의 깃털\n\r" + "  소유자 : " + name[9] + "\n\r" +
	  	                      "      ");
	  }else if(number == 6) { //추가부분입니다
	        writeS("\n\r" + "  1위 $ : "+ priaden[0] + " 아데나\n\r" + "  계정명 : " + name[0] + "\n\r" +
	  	          "  2위 $ : " + priaden[1] + " 아데나\n\r" + "  계정명 : " + name[1] + "\n\r" +
	  	          "  3위 $ : " + priaden[2] + " 아데나\n\r" + "  계정명 : " + name[2] + "\n\r" +
	  	          "  4위 $ : " + priaden[3] + " 아데나\n\r" + "  계정명 : " + name[3] + "\n\r" +
	  	          "  5위 $ : " + priaden[4] + " 아데나\n\r" + "  계정명 : " + name[4] + "\n\r" +
	  	          "  6위 $ : " + priaden[5] + " 아데나\n\r" + "  계정명 : " + name[5] + "\n\r" +
	  	          "  7위 $ : " + priaden[6] + " 아데나\n\r" + "  계정명 : " + name[6] + "\n\r" +
	  	          "  8위 $ : " + priaden[7] + " 아데나\n\r" + "  계정명 : " + name[7] + "\n\r" +
	  	          "  9위 $ : " + priaden[8] + " 아데나\n\r" + "  계정명 : " + name[8] + "\n\r" +
	  	          " 10위 $ : " + priaden[9] + " 아데나\n\r" + "  계정명 : " + name[9] + "\n\r" +
	  	                      "      ");
	  }else if(number == 7) { //추가부분입니다
		     writeS("\n\r" + "  1위. " + name[0]+" " + MaxHp[0] + "\n\r" +
		       "  2위. " + name[1]+" " + MaxHp[1] + "\n\r" +
		       "  3위. " + name[2]+" " + MaxHp[2] + "\n\r" +
		       "  4위. " + name[3]+" " + MaxHp[3] + "\n\r" +
		       "  5위. " + name[4]+" " + MaxHp[4] + "\n\r" +
		       "  6위. " + name[5]+" " + MaxHp[5] + "\n\r" +
		       "  7위. " + name[6]+" " + MaxHp[6] + "\n\r" +
		       "  8위. " + name[7]+" " + MaxHp[7] + "\n\r" +
		       "  9위. " + name[8]+" " + MaxHp[8] + "\n\r" +
		       " 10위. " + name[9]+" " + MaxHp[9] + "\n\r" +
		     "      ");
		    }else if(number == 8) { //추가부분입니다
		     writeS("\n\r" + "  1위. " + name[0]+" " + MaxMp[0] + "\n\r" +
		       "  2위. " + name[1]+" " + MaxMp[1] + "\n\r" +
		       "  3위. " + name[2]+" " + MaxMp[2] + "\n\r" +
		       "  4위. " + name[3]+" " + MaxMp[3] + "\n\r" +
		       "  5위. " + name[4]+" " + MaxMp[4] + "\n\r" +
		       "  6위. " + name[5]+" " + MaxMp[5] + "\n\r" +
		       "  7위. " + name[6]+" " + MaxMp[6] + "\n\r" +
		       "  8위. " + name[7]+" " + MaxMp[7] + "\n\r" +
		       "  9위. " + name[8]+" " + MaxMp[8] + "\n\r" +
		       " 10위. " + name[9]+" " + MaxMp[9] + "\n\r" +
		     "      ");
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
			switch(number) {

		    case 1: //추가부분입니다
		        pstm = con.prepareStatement("SELECT enchantlvl, weapon.name, characters.char_name  FROM character_items, weapon, characters WHERE character_items.item_id in(select item_id from weapon) And character_items.char_id in(select objid from characters where AccessLevel = 0) And character_items.item_id=weapon.item_id And character_items.char_id=characters.objid And count = 1 order by character_items.enchantlvl desc limit 10");
		        //		        pstm = con.prepareStatement("SELECT enchantlvl, weapon.name, characters.char_name FROM character_items, weapon, characters WHERE character_items.item_id in(select item_id from weapon) And character_items.char_id in(select objid from characters where AccessLevel = 200) And character_items.item_id=weapon.item_id And character_items.char_id=characters.objid And character_items.is_equipped = 1 order by character_items.enchantlvl desc limit 10");
		        break;		
		    case 2: //추가부분입니다
		        pstm = con.prepareStatement("SELECT enchantlvl, armor.name, characters.char_name  FROM character_items, armor, characters WHERE character_items.item_id in(select item_id from armor) And character_items.char_id in(select objid from characters where AccessLevel = 0) And character_items.item_id=armor.item_id And character_items.char_id=characters.objid And count = 1 order by character_items.enchantlvl desc limit 10");
		        break;	
		    case 3:
		    	pstm = con.prepareStatement("SELECT count, characters.char_name FROM character_items, characters WHERE item_id in(select item_id from etcitem) And char_id in(select objid from characters where AccessLevel = 0) And character_items.char_id=characters.objid And item_id = 40308 order by count desc limit 10");
		        break;
		//	        pstm = con.prepareStatement("SELECT enchantlvl, characters.char_name, armor.name  FROM character_items, characters, armor WHERE item_id in(select item_id from armor) And char_id in(select objid from characters where AccessLevel = 200) And char_id=characters.objid And item_id=armor.item_id And count = 1 order by enchantlvl desc limit 10");
		    case 4: //추가부분입니다
		    	if(pc.isGm()){
		    	pstm = con.prepareStatement("SELECT level, char_name FROM characters WHERE AccessLevel = 0 order by level desc limit 10");
		    	} else {
					pc.sendPackets(new S_SystemMessage("해당 게시판은 운영자만 사용가능합니다."));
				}
		    	break;		
		    case 5:
		    	pstm = con.prepareStatement("SELECT count, characters.char_name FROM character_items, characters WHERE item_id in(select item_id from etcitem) And char_id in(select objid from characters where AccessLevel = 0) And character_items.char_id=characters.objid And item_id = 41159 order by count desc limit 10");
		        break;
		    case 6:  
		    	if(pc.isGm()){
		    	pstm = con.prepareStatement("SELECT count, accounts.login FROM character_warehouse, accounts WHERE  login in(select login from accounts where access_level = 0) And character_warehouse.account_name =accounts.login And item_id = 40308 order by count desc limit 10");
		    	} else {
					pc.sendPackets(new S_SystemMessage("해당 게시판은 운영자만 사용가능합니다."));
				}
		    	break;
		    case 7: //추가부분입니다
		        pstm = con.prepareStatement("SELECT MaxHp, char_name FROM characters WHERE AccessLevel = 0 order by MaxHp desc limit 10");
		            break;  
		        case 8: //추가부분입니다
		         pstm = con.prepareStatement("SELECT MaxMp, char_name FROM characters WHERE AccessLevel = 0 order by MaxMp desc limit 10");
		            break;
				default:
					pstm = con.prepareStatement("SELECT char_name FROM characters WHERE AccessLevel = 0 order by Exp desc limit 10");
					break;
			}

			rs = pstm.executeQuery();
			if (number == 1) { // 추가부분입니다
				while (rs.next()) {
					enchantlvl[i] = rs.getInt(1);
					name[i] = rs.getString(2);
					name1[i] = rs.getString(3);
					i++;
				}
			} else if (number == 2) { // 추가부분입니다
				while (rs.next()) {
					armor[i] = rs.getInt(1);
					name[i] = rs.getString(2);
					name1[i] = rs.getString(3);
					i++;
				}
			} else if (number == 3) { // 추가부분입니다
				while (rs.next()) {
					aden[i] = rs.getInt(1);
					name[i] = rs.getString(2);
					i++;
				}
			} else if (number == 4) { // 추가부분입니다
				while (rs.next()) {
					level[i] = rs.getInt(1);
					name[i] = rs.getString(2);
					i++;
				}
			} else if (number == 5) { // 추가부분입니다
				while (rs.next()) {
					priaden[i] = rs.getInt(1);
					name[i] = rs.getString(2);
					i++;
				}
			} else if (number == 6) { // 추가부분입니다
				while (rs.next()) {
				     priaden[i] = rs.getInt(1);
				     name[i] = rs.getString(2);
				     i++;
					 
					 }

			}else if(number == 7) { //추가부분입니다
			    while(rs.next()){
			     MaxHp[i] = rs.getInt(1);
			     name[i] = rs.getString(2);
			     i++;
			    }
			   }else if(number == 8) { //추가부분입니다
			    while(rs.next()){
			     MaxMp[i] = rs.getInt(1);
			     name[i] = rs.getString(2);
			     i++;
			    }

			} else {
				while (rs.next()) {
					name[i] = rs.getString(1);
					i++;
				}

				// 레코드가 없거나 5보다 작을때
				while (i < 10) {
					name[i] = "없음.";
					i++;
				}
			}
		} catch (SQLException e) {
		//	_log.log(Level.SEVERE, "S_EnchantRanking[]Error", e);
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
		return S_EnchantRanking;
	}

}
