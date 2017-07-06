package l1j.server.server.clientpackets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.GameClient;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.RankTable;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1World;
import l1j.server.server.serverpackets.S_CharAmount;
import l1j.server.server.serverpackets.S_CharPacks;
import l1j.server.server.serverpackets.S_NewCreateItem;
import l1j.server.server.serverpackets.S_Unknown2;
import l1j.server.server.utils.SQLUtil;

public class C_CommonClick {
	private static final String C_COMMON_CLICK = "[C] C_CommonClick";
	private static Logger _log = Logger.getLogger(C_CommonClick.class.getName());
	//private static final int LIMIT_MIN = 1;
	//private static final int LIMIT_MAX = 32767;

	public C_CommonClick(GameClient client) {
		if (client == null || client.getAccount() == null)
			return;

		deleteCharacter(client);
		
		client.getAccount().탐포인트업데이트(client.getAccount());
		client.sendPacket(new S_NewCreateItem(S_NewCreateItem.TAM_POINT, client));
			
		int amountOfChars = client.getAccount().countCharacters();
		int slot = client.getAccount().getCharSlot();
		client.sendPacket(new S_CharAmount(amountOfChars, slot));
		if (amountOfChars > 0) {
			sendCharPacks(client);
		}
		client.sendPacket(new S_Unknown2(0)); // 로그인시 언노처리
	}

	public static void sendCharPacks(GameClient client) {
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {

			conn = L1DatabaseFactory.getInstance().getConnection();
			pstm = conn.prepareStatement("SELECT * FROM characters WHERE account_name=? ORDER BY objid");
			pstm.setString(1, client.getAccountName());
			rs = pstm.executeQuery();
			S_CharPacks cpk = null;
			while (rs.next()) {
				String name = rs.getString("char_name");
				String clanname = rs.getString("Clanname");
				int type = rs.getInt("Type");
				byte sex = rs.getByte("Sex");
				int lawful = rs.getInt("Lawful");

				int currenthp = rs.getInt("CurHp");
				if (currenthp < 1) {
					currenthp = 1;
				} else if (currenthp > 32767) {
					currenthp = 32767;
				}

				int currentmp = rs.getInt("CurMp");
				if (currentmp < 1) {
					currentmp = 1;
				} else if (currentmp > 32767) {
					currentmp = 32767;
				}

				int lvl;
				if (Config.CHARACTER_CONFIG_IN_SERVER_SIDE) {
					lvl = rs.getInt("level");
					if (lvl < 1) {
						lvl = 1;
					} else if (lvl > 127) {
						lvl = 127;
					}
				} else {
					lvl = 1;
				}

				int ac;// = rs.getByte("Ac");
				if (rs.getInt("Ac") < -128) {
					ac = (byte) -128;
				} else {
					ac = rs.getByte("Ac");
				}
				int str = rs.getByte("Str");
				int dex = rs.getByte("Dex");
				int con = rs.getByte("Con");
				int wis = rs.getByte("Wis");
				int cha = rs.getByte("Cha");
				int intel = rs.getByte("Intel");
				int accessLevel = rs.getShort("AccessLevel");
				int birth = rs.getInt("BirthDay");
//				System.out.println("이름은?" + name);
				RankTable.getInstance().sendRankStatusPacks(client, name);//랭킹
			    cpk = new S_CharPacks(name, clanname, type, sex, lawful, currenthp, currentmp, ac, lvl, str, dex, con,
				wis, cha, intel, accessLevel, birth);

				client.sendPacket(cpk);
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(conn);
		}
	}
	
	private void deleteCharacter(GameClient client) {
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {

			conn = L1DatabaseFactory.getInstance().getConnection();
			pstm = conn.prepareStatement("SELECT * FROM characters WHERE account_name=? ORDER BY objid");
			pstm.setString(1, client.getAccountName());
			rs = pstm.executeQuery();
			Timestamp deleteTime = null;
			Calendar cal = null;
			L1Clan clan = null;
			while (rs.next()) {
				String name = rs.getString("char_name");
				String clanname = rs.getString("Clanname");

				deleteTime = rs.getTimestamp("DeleteTime");
				if (deleteTime != null) {
					cal = Calendar.getInstance();
					long checkDeleteTime = ((cal.getTimeInMillis() - deleteTime.getTime()) / 1000) / 3600;
					if (checkDeleteTime >= 0) {
						clan = L1World.getInstance().getClan(clanname);
						if (clan != null) {
							clan.removeClanMember(name);
						}
						CharacterTable.getInstance().deleteCharacter(client.getAccountName(), name);
					}
				}
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(conn);
		}
	}
	
	public String getType() {
		return C_COMMON_CLICK;
	}
}