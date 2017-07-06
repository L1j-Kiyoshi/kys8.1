package l1j.server.GameSystem.Robot;

import static l1j.server.server.model.skill.L1SkillId.HASTE;
import static l1j.server.server.model.skill.L1SkillId.STATUS_UNDERWATER_BREATH;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Random;

import javolution.util.FastMap;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.IdFactory;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_ChangeShape;
import l1j.server.server.serverpackets.S_CharTitle;
import l1j.server.server.serverpackets.S_ClanName;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_ReturnedStat;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.utils.SQLUtil;

public class Robot {

	private static Random _random = new Random(System.currentTimeMillis());
	public static boolean 인형 = false;

	public static void poly(L1RobotInstance bot) {
		int rr = 0;

		if (bot.리스봇 || bot.사냥봇)
			rr = _random.nextInt(3);
		else
			rr = _random.nextInt(2);

		if (rr == 0 || (bot.리스봇_스폰위치 == 1 && _random.nextInt(10) < 8)) {
			polyNormal51(bot);
		} else {
			if (bot.getLevel() > 75) { // 75레벨 이상
				polyNormal75(bot);
			} else if (bot.getLevel() > 80) { // 80레벨 이상
				polyNormal80(bot);
			} else {
				polyNormal51(bot); // 그렇지않을경우
			}
		}
	}
private static void polyNormal51(L1RobotInstance bot) { // 로봇변신
	if (bot.사냥봇) {
		if (bot.isElf() && bot.getCurrentWeapon() == 20) {
			int f = _random.nextInt(4);
			if (f == 0)
				bot.setTempCharGfx(11378); // 다크엘프
			else if (f == 1)
				bot.setTempCharGfx(11386); // 실버 레인저
			else if (f == 2)
				bot.setTempCharGfx(11394); // 아크 스카우트
			else if (f == 3)
				bot.setTempCharGfx(15848); // 포노스 오크 궁수
		} else {
			int f1 = _random.nextInt(4);
			if (f1 == 0 && bot.isKnight())
				bot.setTempCharGfx(11392); // 아크 나이트
			
			else if (f1 == 1 && bot.getCurrentWeapon() != 20)
				bot.setTempCharGfx(11329); // 해골
			
			else if (f1 == 2 && bot.isDragonknight()) // 용기사
				bot.setTempCharGfx(11447); // 나이트 워치
			
			else if (f1 == 3 && bot.getCurrentWeapon() != 20)
				bot.setTempCharGfx(11392); // 아크 나이트
			else
				bot.setTempCharGfx(11392); //
		}
	} else {
		// int f = _random.nextInt(100);
		// 버경 봇들
		/*
		 * if(bot.리스봇 && (bot.리스봇_스폰위치 == 0 || bot.리스봇_스폰위치 == 3 ||
		 * (bot.리스봇_스폰위치 >= 16 && bot.리스봇_스폰위치 <= 19))) f =
		 * _random.nextInt(100);
		 */
		bot.setTempCharGfx(bot.getGfxId());

		/*
		 * if(f < 15){
		 * bot.getGfxId().setTempCharGfx(bot.getGfxId().getGfxId());
		 * if(bot.버경봇 && bot.isCrown()){ if(_random.nextInt(2) == 0){
		 * if(bot.get_sex() == 0) bot.getGfxId().setTempCharGfx(6094); else
		 * bot.getGfxId().setTempCharGfx(6080); } } }else if(f < 50){
		 * bot.getGfxId().setTempCharGfx(11375); }else if(f < 75){
		 * bot.getGfxId().setTempCharGfx(11328+_random.nextInt(3)); //else
		 * if(f == 3) // bot.getGfxId().setTempCharGfx(11341); }else{
		 * bot.getGfxId().setTempCharGfx(11370); } if(bot.isElf() && f != 0
		 * && bot.getCurrentWeapon() == 20)
		 * bot.getGfxId().setTempCharGfx(bot.getGfxId().getGfxId());
		 */
	}
}

private static void polyNormal75(L1RobotInstance bot) { // 로봇변신
	if (bot.사냥봇) {
		if (bot.isElf() && bot.getCurrentWeapon() == 20) {
			int f = _random.nextInt(4);
			if (f == 0)
				bot.setTempCharGfx(15848); // 포노스 오크
			else if (f == 1)
				bot.setTempCharGfx(15848); // 포노스 오크
			else if (f == 2)
				bot.setTempCharGfx(13346); // 진 다크엘프
			else if (f == 3)
				bot.setTempCharGfx(13346); // 진 다크엘프
		} else {
			int f1 = _random.nextInt(4);
			if (f1 == 0 && bot.isKnight())
				bot.setTempCharGfx(12702); // 75 진데스
			else if (f1 == 1 && bot.getCurrentWeapon() != 20)
				bot.setTempCharGfx(12702); // 75 진데스
			else if (f1 == 2 && bot.isDragonknight())
				bot.setTempCharGfx(15834); // 75 랜스 마스터
			else if (f1 == 3 && bot.getCurrentWeapon() != 20)
				bot.setTempCharGfx(12702); // 75 진데스
			else
				bot.setTempCharGfx(12702); // 75 진데스
		}
	} else {
		// int f = _random.nextInt(100);
		// 버경 봇들
		/*
		 * if(bot.리스봇 && (bot.리스봇_스폰위치 == 0 || bot.리스봇_스폰위치 == 3 ||
		 * (bot.리스봇_스폰위치 >= 16 && bot.리스봇_스폰위치 <= 19))) f =
		 * _random.nextInt(100);
		 */
		bot.setTempCharGfx(bot.getGfxId());

		/*
		 * if(f < 15){
		 * bot.getGfxId().setTempCharGfx(bot.getGfxId().getGfxId());
		 * if(bot.버경봇 && bot.isCrown()){ if(_random.nextInt(2) == 0){
		 * if(bot.get_sex() == 0) bot.getGfxId().setTempCharGfx(6094); else
		 * bot.getGfxId().setTempCharGfx(6080); } } }else if(f < 50){
		 * bot.getGfxId().setTempCharGfx(11375); }else if(f < 75){
		 * bot.getGfxId().setTempCharGfx(11328+_random.nextInt(3)); //else
		 * if(f == 3) // bot.getGfxId().setTempCharGfx(11341); }else{
		 * bot.getGfxId().setTempCharGfx(11370); } if(bot.isElf() && f != 0
		 * && bot.getCurrentWeapon() == 20)
		 * bot.getGfxId().setTempCharGfx(bot.getGfxId().getGfxId());
		 */
	}
}

private static void polyNormal80(L1RobotInstance bot) { // 로봇변신
	if (bot.사냥봇) {
		if (bot.isElf() && bot.getCurrentWeapon() == 20) {
			int f = _random.nextInt(4);
			if (f == 0)
				bot.setTempCharGfx(15814); // 80 하이엘프
			else if (f == 1)
				bot.setTempCharGfx(15814); // 80 하이엘프
			else if (f == 2)
				bot.setTempCharGfx(15814); // 80 하이엘프
			else if (f == 3)
				bot.setTempCharGfx(15814); // 80 하이엘프
		} else {
			int f1 = _random.nextInt(4);
			if (f1 == 0 && bot.isKnight())
				bot.setTempCharGfx(12681); // 80 진데스
			else if (f1 == 1 && bot.getCurrentWeapon() != 20)
				bot.setTempCharGfx(12681); // 80 진데스
			else if (f1 == 2 && bot.isDragonknight())
				bot.setTempCharGfx(15534); // 80 진 랜스마스터
			else if (f1 == 3 && bot.getCurrentWeapon() != 20)
				bot.setTempCharGfx(12681); // 80 진데스
			else
				bot.setTempCharGfx(12681); // 80 진데스
		}
	} else {
		// int f = _random.nextInt(100);
		// 버경 봇들
		/*
		 * if(bot.리스봇 && (bot.리스봇_스폰위치 == 0 || bot.리스봇_스폰위치 == 3 ||
		 * (bot.리스봇_스폰위치 >= 16 && bot.리스봇_스폰위치 <= 19))) f =
		 * _random.nextInt(100);
		 */
		bot.setTempCharGfx(bot.getGfxId());

		/*
		 * if(f < 15){
		 * bot.getGfxId().setTempCharGfx(bot.getGfxId().getGfxId());
		 * if(bot.버경봇 && bot.isCrown()){ if(_random.nextInt(2) == 0){
		 * if(bot.get_sex() == 0) bot.getGfxId().setTempCharGfx(6094); else
		 * bot.getGfxId().setTempCharGfx(6080); } } }else if(f < 50){
		 * bot.getGfxId().setTempCharGfx(11375); }else if(f < 75){
		 * bot.getGfxId().setTempCharGfx(11328+_random.nextInt(3)); //else
		 * if(f == 3) // bot.getGfxId().setTempCharGfx(11341); }else{
		 * bot.getGfxId().setTempCharGfx(11370); } if(bot.isElf() && f != 0
		 * && bot.getCurrentWeapon() == 20)
		 * bot.getGfxId().setTempCharGfx(bot.getGfxId().getGfxId());
		 */
	}
}

	public static boolean 속도버프(L1RobotInstance bot) {

		// 디케이 아닐때
		if (bot.getMap().isUnderwater()) {
			if (!bot.hasSkillEffect(
					STATUS_UNDERWATER_BREATH)) {
				Broadcaster.broadcastPacket(bot, new S_SkillSound(bot.getId(),
						190), true);
				bot.setSkillEffect(
						STATUS_UNDERWATER_BREATH, 1800 * 1000);
			}
		}
		if (bot.getMoveSpeed() == 0
				&& !bot.hasSkillEffect(HASTE)
				&& !bot.hasSkillEffect(
						L1SkillId.DECAY_POTION)) {
			bot.setMoveSpeed(1);
			bot.setSkillEffect(HASTE,
					(_random.nextInt(400) + 1700) * 1000);
			Broadcaster.broadcastPacket(bot,
					new S_SkillSound(bot.getId(), 191), true);
		}
		if (_random.nextInt(100) > 10)
			return false;
		if (bot.isKnight() || bot.isCrown()) {
			// 디케이아닐때
			if (!bot.hasSkillEffect(
					L1SkillId.STATUS_BRAVE)
					&& !bot.hasSkillEffect(
							L1SkillId.DECAY_POTION)) {
				bot.setSkillEffect(
						L1SkillId.STATUS_BRAVE,
						(_random.nextInt(600) + 400) * 1000);
				bot.setBraveSpeed(1);
				Broadcaster.broadcastPacket(bot, new S_SkillSound(bot.getId(),
						751), true);
				Broadcaster.broadcastPacket(bot, new S_SkillBrave(bot.getId(),
						1, 0), true);
				return true;
			}
		} else if (bot.isElf()) {
			if (bot.getCurrentWeapon() != 20) {
				if (!bot.hasSkillEffect(
						L1SkillId.DANCING_BLADES)
						&& !bot.hasSkillEffect(
								L1SkillId.SILENCE)) {
					bot.setSkillEffect(
							L1SkillId.DANCING_BLADES,
							(_random.nextInt(50) + 250) * 1000);
					bot.setBraveSpeed(1);
					Broadcaster.broadcastPacket(bot,
							new S_SkillSound(bot.getId(), 11775), true);
					Broadcaster.broadcastPacket(bot,
							new S_SkillBrave(bot.getId(), 1, 0), true);
					return true;
				}
			} else {
				if (!bot.hasSkillEffect(
						L1SkillId.STATUS_ELFBRAVE)
						&& !bot.hasSkillEffect(
								L1SkillId.DECAY_POTION)) {
					bot.setSkillEffect(
							L1SkillId.STATUS_ELFBRAVE,
							(_random.nextInt(600) + 400) * 1000);
					bot.setBraveSpeed(1);
					Broadcaster.broadcastPacket(bot,
							new S_SkillSound(bot.getId(), 751), true);
					Broadcaster.broadcastPacket(bot,
							new S_SkillBrave(bot.getId(), 3, 0), true);
					return true;
				}
			}
		} else if (bot.isDragonknight()) {
			if (!bot.hasSkillEffect(
					L1SkillId.BLOOD_LUST)
					&& !bot.hasSkillEffect(
							L1SkillId.SILENCE)) {
				bot.setSkillEffect(
						L1SkillId.BLOOD_LUST,
						(_random.nextInt(300) + 200) * 1000);
				bot.setBraveSpeed(1);
				Broadcaster.broadcastPacket(bot, new S_DoActionGFX(bot.getId(),
						19));
				Broadcaster.broadcastPacket(bot, new S_SkillBrave(bot.getId(),
						1, 0), true);
				Broadcaster.broadcastPacket(bot, new S_SkillSound(bot.getId(),
						6523), true);
				return true;
			}
		} else if (bot.isDarkelf()) {
			int percent = (int) Math.round((double) bot.getCurrentMp()
					/ (double) bot.getMaxMp() * 100);
			if (percent < 20)
				return false;
			if (!bot.hasSkillEffect(
					L1SkillId.MOVING_ACCELERATION)
					&& !bot.hasSkillEffect(
							L1SkillId.SILENCE)) {
				new L1SkillUse().handleCommands(bot,
						L1SkillId.MOVING_ACCELERATION, bot.getId(), bot.getX(),
						bot.getY(), null, 0, L1SkillUse.TYPE_NORMAL);
				/*
				 * bot.setSkillEffect(L1SkillId.
				 * MOVING_ACCELERATION, (_random.nextInt(600)+400) *1000);
				 * bot.getMoveState().setBraveSpeed(4);
				 * Broadcaster.broadcastPacket(bot, new
				 * S_DoActionGFX(bot.getId(), 19));
				 * Broadcaster.broadcastPacket(bot, new
				 * S_SkillBrave(bot.getId(), 4, 0), true);
				 * Broadcaster.broadcastPacket(bot, new
				 * S_SkillSound(bot.getId(), 2945), true);
				 */
				return true;
			}
		} else if (bot.isWizard()) {
			int percent = (int) Math.round((double) bot.getCurrentMp()
					/ (double) bot.getMaxMp() * 100);
			if (percent < 20)
				return false;
			if (!bot.hasSkillEffect(
					L1SkillId.HOLY_WALK)
					&& !bot.hasSkillEffect(
							L1SkillId.SILENCE)) {
				bot.setSkillEffect(
						L1SkillId.HOLY_WALK, (_random.nextInt(14) + 50) * 1000);
				bot.setBraveSpeed(4);
				Broadcaster.broadcastPacket(bot, new S_DoActionGFX(bot.getId(),
						19));
				Broadcaster.broadcastPacket(bot, new S_SkillBrave(bot.getId(),
						4, 0), true);
				Broadcaster.broadcastPacket(bot, new S_SkillSound(bot.getId(),
						3936), true);
				return true;
			}
		}
		return false;
	}

	public static boolean 클래스버프(L1RobotInstance bot) {

		if (bot.hasSkillEffect(L1SkillId.SILENCE))
			return false;
		if (!bot.isDragonknight()) {
			int percent = (int) Math.round((double) bot.getCurrentMp()/ (double) bot.getMaxMp() * 100);
			if (percent < 30)
				return false;
		}
		if (bot.isKnight()) {
			if (!bot.hasSkillEffect(L1SkillId.REDUCTION_ARMOR)) {
				new L1SkillUse().handleCommands(bot, L1SkillId.REDUCTION_ARMOR,bot.getId(), bot.getX(), bot.getY(), null, 192*1000,L1SkillUse.TYPE_NORMAL);
				return true;
			}
		} else if (bot.isCrown()) {
		} else if (bot.isElf()) {
			if (bot.getCurrentWeapon() == 20) {
				if (!bot.hasSkillEffect(L1SkillId.STORM_SHOT)) {
					new L1SkillUse().handleCommands(bot, L1SkillId.STORM_SHOT,bot.getId(), bot.getX(), bot.getY(), null, 960*1000,L1SkillUse.TYPE_NORMAL);
					return true;
				}
			} else {
				if (!bot.hasSkillEffect(L1SkillId.BURNING_WEAPON)) {
					new L1SkillUse().handleCommands(bot,	L1SkillId.BURNING_WEAPON, bot.getId(), bot.getX(),	bot.getY(), null, 960*1000, L1SkillUse.TYPE_NORMAL);
					return true;
				}
			}
		} else if (bot.isDragonknight()) {
			if (!bot.hasSkillEffect(L1SkillId.DRAGON_SKIN)) {
				new L1SkillUse().handleCommands(bot, L1SkillId.DRAGON_SKIN,bot.getId(), bot.getX(), bot.getY(), null, 1200*1000,L1SkillUse.TYPE_NORMAL);
				return true;
			}
		} else if (bot.isDarkelf()) {
			if (!bot.hasSkillEffect(L1SkillId.SHADOW_ARMOR)) {
				new L1SkillUse().handleCommands(bot, L1SkillId.SHADOW_ARMOR,bot.getId(), bot.getX(), bot.getY(), null, 960*1000,L1SkillUse.TYPE_NORMAL);
				return true;
			}
			if (!bot.hasSkillEffect(L1SkillId.BURNING_SPIRIT)) {
				new L1SkillUse().handleCommands(bot, L1SkillId.BURNING_SPIRIT,	bot.getId(), bot.getX(), bot.getY(), null, 320*1000,L1SkillUse.TYPE_NORMAL);
				return true;
			}
		} else if (bot.isWizard()) {
		}
		return false;
	}

	private static long joinTime = 0;

	public static void clan_join(L1RobotInstance bot) {

		if (bot.getClanid() != 0 || bot.isCrown())
			return;
		if (_random.nextInt(10) == 0)
			return;
		if (joinTime == 0) {
			joinTime = System.currentTimeMillis()
					+ (60000 * (20 + _random.nextInt(21)));
			return;
		} else {
			if (joinTime > System.currentTimeMillis())
				return;
			joinTime = System.currentTimeMillis()
					+ (60000 * (20 + _random.nextInt(21)));
		}
		// 로봇중 가입 되어있는 케릭이 650케릭 이상인지
		// 가입하려는혈 총혈 다 받아와서 유저 비교 제일적은혈
		String clanname = robot_clan_count();
		if (clanname == null)
			return;
		L1Clan clan = L1World.getInstance().getClan(clanname);
		if (clan == null)
			return;
		L1PcInstance pc = L1World.getInstance().getPlayer(clan.getLeaderName());
		if (pc == null)
			return;
		// 군주근처에 혈원이 있는지
		for (L1PcInstance pp : L1World.getInstance().getVisiblePlayer(pc)) {
			if (!(pp instanceof L1RobotInstance)
					&& pc.getClanid() == pp.getClanid())
				return;
		}
		// 내 근처에 같은혈 있는지
		for (L1PcInstance pp : L1World.getInstance().getVisiblePlayer(bot)) {
			if (!(pp instanceof L1RobotInstance)
					&& pc.getClanid() == pp.getClanid())
				return;
		}
		// 가입
		for (L1PcInstance clanMembers : clan.getOnlineClanMember()) {
			clanMembers.sendPackets(new S_ServerMessage(94, bot.getName())); // \f1%0이
																				// 혈맹의
																				// 일원으로서
																				// 받아들여졌습니다.
		}
		bot.setClanid(clan.getClanId());
		bot.setClanname(clan.getClanName());
		bot.setClanRank(L1Clan.수련);
		bot.setTitle("");
		bot.setClanJoinDate(new Timestamp(System.currentTimeMillis()));
		Broadcaster.broadcastPacket(bot, new S_CharTitle(bot.getId(), ""));
		if(bot.isCrown()){
		clan.addClanMember(bot.getName(),L1Clan.군주 , bot.getLevel(), "로봇" ,bot.getId(), bot.getType(),1, bot);
		}else{
			clan.addClanMember(bot.getName(),8 , bot.getLevel(),	bot.getClanMemberNotes(),bot.getId(), bot.getType(),1, bot);	
		}
		Broadcaster.broadcastPacket(bot, new S_ClanName(bot,clan.getEmblemId(),bot.getClanRank()));
		Broadcaster.broadcastPacket(bot, new S_ReturnedStat(bot.getId(),clan.getEmblemId()));
		GeneralThreadPool.getInstance().schedule(
				new title((L1RobotInstance) pc, bot),
				3000 + _random.nextInt(2000));

		try {
			Robot_ConnectAndRestart.getInstance().clanSetting(bot);
		} catch (Exception e) {
			e.getStackTrace();
		}
	
		bot.updateclan(bot.getClanname(), bot.getClanid(), bot.getTitle(), true);
	}

	private static String robot_clan_count() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String clan = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM robots_crown");
			rs = pstm.executeQuery();
			while (rs.next()) {
				String clanname = rs.getString("clanname");
				int clanid = rs.getInt("clanid");
				if (clanid == 0)
					continue;
				Connection con2 = null;
				PreparedStatement pstm2 = null;
				ResultSet rs2 = null;
				try {
					con2 = L1DatabaseFactory.getInstance().getConnection();
					pstm2 = con2
							.prepareStatement("SELECT * FROM robots WHERE clanid=?");
					pstm2.setInt(1, clanid);
					rs2 = pstm2.executeQuery();
					if (!rs2.next()) {
						clan = clanname;
						break;
					}
				} catch (SQLException e) {

				} finally {
					SQLUtil.close(rs2);
					SQLUtil.close(pstm2);
					SQLUtil.close(con2);
				}
			}
		} catch (SQLException e) {

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		if (clan != null)
			return clan;

		int count = 0;
		FastMap<String, Integer> list = new FastMap<String, Integer>();
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM robots");
			rs = pstm.executeQuery();
			while (rs.next()) {
				String clanname = rs.getString("clanname");
				int clanid = rs.getInt("clanid");
				if (clanid == 0)
					continue;
				count++;
				if (count > 650)
					break;
				try {
					int cc = list.get(clanname);
					list.put(clanname, cc + 1);
				} catch (Exception e) {
					list.put(clanname, 0);
				}
			}
		} catch (SQLException e) {

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		if (count > 650)
			return null;

		int ci = 1000;
		for (FastMap.Entry<String, Integer> e = list.head(), mapEnd = list
				.tail(); (e = e.getNext()) != mapEnd;) {
			int cu = e.getValue();
			if (ci >= cu) {
				ci = cu;
				clan = e.getKey();
			}
		}
		return clan;
	}

	static class title implements Runnable {

		private L1RobotInstance crown;
		private L1RobotInstance joinchar;

		public title(L1RobotInstance _crown, L1RobotInstance _joinchar) {
			crown = _crown;
			joinchar = _joinchar;
		}

		@Override
		public void run() {

			try {

				if (crown._userTitle == null
						|| crown._userTitle.equalsIgnoreCase(""))
					return;
				if (L1World.getInstance().getPlayer(crown.getName()) == null
						|| L1World.getInstance().getPlayer(joinchar.getName()) == null)
					return;

				joinchar.setTitle(crown._userTitle);
				S_CharTitle ct = new S_CharTitle(joinchar.getId(),
						joinchar.getTitle());
				joinchar.sendPackets(ct);
				Broadcaster.broadcastPacket(joinchar, ct, true);
				try {
					if (joinchar instanceof L1RobotInstance)
						joinchar.updateclan(joinchar.getClanname(),
								joinchar.getClanid(), crown._userTitle, true);
					else
						joinchar.save(); // DB에 캐릭터 정보를 써 우
				} catch (Exception e) {
				}

				L1Clan clan = L1World.getInstance()
						.getClan(crown.getClanname());
				if (clan != null) {
					for (L1PcInstance clanPc : clan.getOnlineClanMember()) {
						// \f1%0이%1에 「%2라고 하는 호칭을 주었습니다.
						S_ServerMessage sm = new S_ServerMessage(203,
								crown.getName(), joinchar.getName(),
								joinchar.getTitle());
						clanPc.sendPackets(sm, true);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static void Doll_Delete(L1RobotInstance bot) {

		Doll_Delete(bot, false);
	}

	public static void Doll_Delete(L1RobotInstance bot, boolean effect) {

		L1DollInstance doll = null;
		for (Object dollObject : bot.getDollList()) {
			doll = (L1DollInstance) dollObject;
		}
		if (doll != null) {
			if (effect)
				Broadcaster.broadcastPacket(doll, new S_SkillSound(
						doll.getId(), 5936), true);
			doll.deleteDoll();
		}
	}

	public static void Doll_Spawn(L1RobotInstance bot) {

		if (!인형)
			return;
		if (bot.인형스폰)
			return;
		if (bot.getDollList().size() > 0)
			return;
		if (!bot.사냥봇 && _random.nextInt(100) < 70)
			return;
		int time = 2000 + _random.nextInt(8000);
		GeneralThreadPool.getInstance().schedule(new DollSpawn(bot, time != 1),
				time);
		bot.인형스폰 = true;
	}

	static class DollSpawn implements Runnable {
		private L1RobotInstance bot;
		private boolean effect;

		public DollSpawn(L1RobotInstance _bot, boolean _effect) {
			bot = _bot;
			effect = _effect;
		}

		@Override
		public void run() {
			try {

				if (bot.isDead()
						|| bot._스레드종료
						|| L1World.getInstance().getPlayer(bot.getName()) == null)
					return;
				int npcId = 0;
				int dollType = 0;
				int dollTime = 0;

				if (bot.getCurrentWeapon() == 20) {
					// npcId = 41915;
					npcId = 900180;
					dollType = L1DollInstance.DOLLTYPE_SCARECROW;
				} else {
					// npcId = 4500161; //스파토이
					npcId = 81211; // 데나
					dollType = L1DollInstance.DOLLTYPE_SKELETON;
				}
				if (_random.nextInt(3) == 0) {
					// npcId = 45000161;
					npcId = 900178;
					dollType = L1DollInstance.DOLLTYPE_ETHYNE;
				}
				dollTime = 1800;

				if (dollType == 0)
					return;
				L1Npc template = NpcTable.getInstance().getTemplate(npcId);
				L1DollInstance doll = new L1DollInstance(template, bot,	dollType, IdFactory.getInstance().nextId());
				if (effect)
					Broadcaster.broadcastPacket(bot,
							new S_SkillSound(doll.getId(), 5935), true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	static class poly implements Runnable {

		L1RobotInstance bot;

		public poly(L1RobotInstance _bot) {
			bot = _bot;
		}

		@Override
		public void run() {
			try {

				if (bot.isDead()
						|| bot._스레드종료
						|| L1World.getInstance().getPlayer(bot.getName()) == null)
					return;
				poly(bot);
				Broadcaster.broadcastPacket(bot, new S_ChangeShape(bot.getId(),
						bot.getTempCharGfx()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void 로봇종료(L1RobotInstance bot) {
	}

}
