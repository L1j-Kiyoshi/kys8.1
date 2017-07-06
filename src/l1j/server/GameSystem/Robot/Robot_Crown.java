package l1j.server.GameSystem.Robot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Random;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.datatables.ClanTable;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.SQLUtil;

public class Robot_Crown {

	private static Robot_Crown _instance;

	public static Robot_Crown getInstance() {
		if (_instance == null) {
			_instance = new Robot_Crown();
		}
		return _instance;
	}

	public Robot_Crown() {
	}

	public void loadbot() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM robots_crown");
			rs = pstm.executeQuery();
			Random _random = new Random(System.nanoTime());
			int i = 0;
			while (rs.next()) {
				i++;
				L1RobotInstance newPc = new L1RobotInstance();
				newPc.setId(rs.getInt("id"));
				newPc.setAccountName("");
				newPc.setName(rs.getString("name"));
				int level = _random.nextInt(9) + 62;
				newPc.setHighLevel(level);
				newPc.setLevel(level);
				newPc.setExp(ExpTable.getExpByLevel(level)
						+ _random.nextInt(ExpTable.getNeedExpNextLevel(level)));
				// newPc.setHighLevel(65);newPc.setLevel(65);
				// newPc.setExp(ExpTable.getExpByLevel(65));
				newPc.getAC().setAc(-75);
				newPc.addHitup(40);
				newPc.addBowHitup(40);
				newPc.addDamageReductionByArmor(15);
				newPc.setLawful(rs.getInt("lawful"));
				newPc.addBaseMaxHp((short) 1500);
				newPc.setCurrentHp(1500);
				newPc.setDead(false);
				newPc.addBaseMaxMp((short) 100);
				newPc.setCurrentMp(100);
				newPc.getResistance().addMr(150);
				newPc.setTitle(rs.getString("title"));
				newPc.getAbility().setBaseStr(18);
				newPc.getAbility().setStr(35);
				newPc.getAbility().setBaseCon(18);
				newPc.getAbility().setCon(18);
				newPc.getAbility().setBaseDex(18);
				newPc.getAbility().setDex(35);
				newPc.getAbility().setBaseCha(18);
				newPc.getAbility().setCha(18);
				newPc.getAbility().setBaseInt(18);
				newPc.getAbility().setInt(18);
				newPc.getAbility().setBaseWis(18);
				newPc.getAbility().setWis(35);

				newPc.set_sex(rs.getInt("sex"));
				newPc.setClassId(newPc.get_sex());
				newPc.setGfxId(newPc.get_sex());
				newPc.setTempCharGfx(newPc.get_sex());
				// newPc.setCurrentWeapon(4);

				newPc.setType(0);
				newPc.getMoveState().setMoveSpeed(0);
				newPc.getMoveState().setBraveSpeed(0);
				newPc.getMoveState().setHeading(0);

				newPc.set_food(39);
				newPc.setClanid(rs.getInt("clanid"));
				newPc.setClanname(rs.getString("clanname"));
				newPc.setClanRank(L1Clan.군주);
				newPc.setElfAttr(0);
				newPc.set_PKcount(0);
				newPc.setExpRes(0);
				newPc.setPartnerId(0);
				newPc.setAccessLevel((short) 0);
				newPc.setGm(false);
				newPc.setMonitor(false);
				newPc.setHomeTownId(0);
				newPc.setContribution(0);
				newPc.setHellTime(0);
				newPc.setBanned(false);
				newPc.setKarma(0);
				newPc.setReturnStat(0);
				newPc.setGmInvis(false);
				newPc.noPlayerCK = true;
				newPc.setActionStatus(0);
				newPc.getMoveState().setHeading(rs.getInt("heading"));
				newPc.setX(rs.getInt("x"));
				newPc.setY(rs.getInt("y"));
				newPc.setMap((short) rs.getInt("map"));
				newPc.setClanMemberNotes(rs.getString("clan_memo"));
				newPc.setKills(0);
				newPc.setDeaths(0);
				newPc.setNetConnection(null);
				newPc._userTitle = rs.getString("user_title");
				newPc.가입군주 = true;
				GeneralThreadPool.getInstance().schedule(new on(newPc),
						_random.nextInt(60000));
				// GeneralThreadPool.getInstance().schedule(new on(newPc),
				// _random.nextInt(600*5));
			}
		} catch (SQLException e) {
		} catch (SecurityException e) {
		} catch (IllegalArgumentException e) {
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	class on implements Runnable {
		private L1RobotInstance newPc;

		public on(L1RobotInstance _newPc) {
			newPc = _newPc;
		}

		@Override
		public void run() {
			// TODO 자동 생성된 메소드 스텁
			try {
				L1Clan clan = L1World.getInstance()
						.getClan(newPc.getClanname());
				if (clan != null) {
					if (newPc.getClanid() == clan.getClanId() && // 크란을 해산해, 재차,
																	// 동명의 크란이
																	// 창설되었을 때의
																	// 대책
							newPc.getClanname().toLowerCase()
									.equals(clan.getClanName().toLowerCase())) {
						clan.updateClanMemberOnline(newPc);
						S_ServerMessage sm = new S_ServerMessage(843,
								newPc.getName());
						for (L1PcInstance clanMember : clan
								.getOnlineClanMember()) {
							if (clanMember.getId() != newPc.getId()) {
								clanMember.sendPackets(sm);
							}
						}
					}
				} else {
					ClanTable.getInstance().createClan(newPc,
							newPc.getClanname(), newPc.getClanid());
					clan = L1World.getInstance().getClan(newPc.getClanname());
				}
				//String[] str = clan.getCreateDate().split("/");
				/*Calendar ca = (Calendar) Calendar.getInstance().clone();
				ca.set(Integer.parseInt(str[0]), Integer.parseInt(str[1]) - 1,
						Integer.parseInt(str[2]));*/
				//newPc.setClanJoinDate(new Timestamp(ca.getTimeInMillis()));
				newPc.setClanJoinDate(new Timestamp(System.currentTimeMillis()));
				clan.updateClanMemberOnline(newPc);

				L1World.getInstance().storeObject(newPc);
				L1World.getInstance().addVisibleObject(newPc);
			} catch (Exception e) {
			}
		}

	}
}
