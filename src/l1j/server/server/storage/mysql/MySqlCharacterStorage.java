package l1j.server.server.storage.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.RobotSystem.L1RobotAI;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.storage.CharacterStorage;
import l1j.server.server.utils.SQLUtil;

public class MySqlCharacterStorage implements CharacterStorage {
	
	private static Logger _log = Logger.getLogger(MySqlCharacterStorage.class.getName());

	@Override
	public L1PcInstance loadCharacter(String charName) {
		L1PcInstance pc = null;
		Connection con = null;
		PreparedStatement pstm = null;
		//PreparedStatement pstm2 = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM characters WHERE char_name=?");
			pstm.setString(1, charName);

			rs = pstm.executeQuery();
			
			if (!rs.next()) {// SELECT가 결과를 돌려주지 않았다.
				return null;
			}
			
			pc = new L1PcInstance();
			pc.setAccountName(rs.getString("account_name"));
			pc.setId(rs.getInt("objid"));
			pc.setName(rs.getString("char_name"));
			pc.setHighLevel(rs.getInt("HighLevel"));
			pc.setExp(rs.getInt("Exp"));
			pc.addBaseMaxHp(rs.getShort("MaxHp"));
			short currentHp = rs.getShort("CurHp");
			if (currentHp < 1) {
				currentHp = 1;
			}
			pc.setCurrentHp(currentHp);
			pc.setDead(false);
			pc.setStatus(0);
			pc.addBaseMaxMp(rs.getShort("MaxMp"));
			pc.setCurrentMp(rs.getShort("CurMp"));
			
			pc.getAbility().setBaseStr(rs.getByte("BaseStr"));
			pc.getAbility().setStr(rs.getByte("Str"));
			pc.getAbility().setBaseCon(rs.getByte("BaseCon"));
			pc.getAbility().setCon(rs.getByte("Con"));
			pc.getAbility().setBaseDex(rs.getByte("BaseDex"));
			pc.getAbility().setDex(rs.getByte("Dex"));
			pc.getAbility().setBaseCha(rs.getByte("BaseCha"));
			pc.getAbility().setCha(rs.getByte("Cha"));
			pc.getAbility().setBaseInt(rs.getByte("BaseIntel"));
			pc.getAbility().setInt(rs.getByte("Intel"));
			pc.getAbility().setBaseWis(rs.getByte("BaseWis"));
			pc.getAbility().setWis(rs.getByte("Wis"));
			
			int status = rs.getInt("Status");
			pc.setCurrentWeapon(status);
			int classId = rs.getInt("Class");
			pc.setClassId(classId);
			pc.setTempCharGfx(classId);
			pc.setGfxId(classId);
			pc.set_sex(rs.getInt("Sex"));
			pc.setType(rs.getInt("Type"));
			int head = rs.getInt("Heading");
			if (head > 7) {
				head = 0;
			}
			pc.setHeading(head);
			pc.setX(rs.getInt("locX"));
			pc.setY(rs.getInt("locY"));
			pc.setMap(rs.getShort("MapID"));
			pc.set_food(rs.getInt("Food"));
			pc.setLawful(rs.getInt("Lawful"));
			pc.setTitle(rs.getString("Title"));
			pc.setClanid(rs.getInt("ClanID"));
			pc.setClanname(rs.getString("Clanname"));
			pc.setClanRank(rs.getInt("ClanRank"));
			pc.setClanMemberNotes(rs.getString("notes"));
			pc.setBonusStats(rs.getInt("BonusStatus"));
			pc.setElixirStats(rs.getInt("ElixirStatus"));
			pc.setElfAttr(rs.getInt("ElfAttr"));
			pc.set_PKcount(rs.getInt("PKcount"));
			pc.setExpRes(rs.getInt("ExpRes"));
			pc.setPartnerId(rs.getInt("PartnerID"));
			pc.setAccessLevel(rs.getShort("AccessLevel"));
			if (pc.getAccessLevel() == Config.GMCODE) {
				pc.setGm(true);
				pc.setMonitor(false);
			} else if (pc.getAccessLevel() == 100) {
				pc.setGm(false);
				pc.setMonitor(true);
			} else {
				pc.setGm(false);
				pc.setMonitor(false);
			}
			pc.setOnlineStatus(rs.getInt("OnlineStatus"));
			pc.setHomeTownId(rs.getInt("HomeTownID"));
			pc.setContribution(rs.getInt("Contribution"));
			pc.setHellTime(rs.getInt("HellTime"));
			pc.setBanned(rs.getBoolean("Banned"));
			pc.setKarma(rs.getInt("Karma"));
			pc.setLastPk(rs.getTimestamp("LastPk"));
			pc.setDeleteTime(rs.getTimestamp("DeleteTime"));
			pc.setReturnStat(rs.getInt("ReturnStat"));
			pc.setSealingPW(rs.getString("sealingPW"));
			pc.setSealScrollTime(rs.getInt("sealScrollTime"));
			pc.setSealScrollCount(rs.getInt("sealScrollCount"));
			pc.setLastLoginTime(rs.getTimestamp("lastLoginTime"));
			pc.setEinhasad(rs.getInt("einhasad"));
			pc.setAinState(rs.getInt("AinState")); //생존 쿨타임 상태
			pc.setSurvivalGauge(rs.getInt("SurvivalGauge")); //생존게이지			
			pc.setKills(rs.getInt("PC_Kill"));
			pc.setDeaths(rs.getInt("PC_Death"));
			pc.setGirandungeonTime(rs.getInt("GiranTime"));
			pc.setOrendungeonTime(rs.getInt("OrenTime"));
			pc.setDrageonTime(rs.getInt("DrageonTime"));
			pc.setRadungeonTime(rs.getInt("RadungeonTime"));
			pc.setSomeTime(rs.getInt("SomeTime"));
			pc.setSoulTime(rs.getInt("SoulTime"));
			pc.setnewdodungeonTime(rs.getInt("newdoTime"));//발록
			pc.seticedungeonTime(rs.getInt("iceTime"));//얼던
			pc.setislandTime(rs.getInt("islanddungeonTime"));//말던
			pc.setMark_count(rs.getInt("Mark_Count"));
			pc.setAge(rs.getInt("Age")); //나이설정
			/** 캐릭별 추가데미지, 추가리덕션, 확률  **/
			pc.setAddDamage(rs.getInt("AddDamage"));
			pc.setAddDamageRate(rs.getInt("AddDamageRate"));
			pc.setAddReduction(rs.getInt("AddReduction"));
			pc.setAddReductionRate(rs.getInt("AddReductionRate"));
			/** 캐릭별 추가데미지, 추가리덕션, 확률  **/
			pc.setPeerage(rs.getInt("IsPeerage"));
			pc.setAbysspoint(rs.getInt("Abysspoint"));
			pc.setTamTime(rs.getTimestamp("TamEndTime"));
			pc.set_SpecialSize(rs.getInt("SpecialSize"));//특수창고
			pc.setHuntPrice(rs.getInt("HuntPrice"));
			pc.setHuntCount(rs.getInt("HuntCount"));
			pc.setReasonToHunt(rs.getString("HuntText"));
			/** 로봇시스템 **/
			if (rs.getString("account_name").equals("인공지능")) {
				pc.setRobotAi(new L1RobotAI(pc));
			}
			pc.setClanJoinDate(rs.getTimestamp("Clan_Join_Date"));
			/** 로봇시스템 **/
			pc.refresh();
			pc.setMoveSpeed(0);
			pc.setBraveSpeed(0);
			pc.setGmInvis(false);
			//
			try {
				pc.setFishingShopBuyTime_1( rs.getTimestamp("FishingShopBuyTime_1").getTime() );
			} catch (Exception e) {
				pc.setFishingShopBuyTime_1(0);
			}

			_log.finest("restored char data: ");
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			pc = null;
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return pc;
	}

	@Override
	public void createCharacter(L1PcInstance pc) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			int i = 0;
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("INSERT INTO characters SET account_name=?,objid=?,char_name=?" +
					",level=?,HighLevel=?,Exp=?,MaxHp=?,CurHp=?,MaxMp=?,CurMp=?,Ac=?,Str=?" +
					",BaseStr=?,Con=?,BaseCon=?,Dex=?,BaseDex=?,Cha=?,BaseCha=?,Intel=?,BaseIntel=?" +
					",Wis=?,BaseWis=?,Status=?,Class=?,Sex=?,Type=?,Heading=?,LocX=?,LocY=?,MapID=?,Food=?" +
					",Lawful=?,Title=?,ClanID=?,Clanname=?,ClanRank=?,notes=?,BonusStatus=?,ElixirStatus=?,ElfAttr=?" +
					",PKcount=?,ExpRes=?,PartnerID=?,AccessLevel=?,OnlineStatus=?,HomeTownID=?,Contribution=?" +
					",Pay=?,HellTime=?,Banned=?,Karma=?,LastPk=?,DeleteTime=?,ReturnStat=?,lastLoginTime=now(),AinState=?,SurvivalGauge=?,BirthDay=?" +
					",GiranTime=?,OrenTime=?,DrageonTime=?,RadungeonTime=?,SomeTime=?,SoulTime=?,newdoTime=?,iceTime=?,islanddungeonTime=?,Mark_Count=?,Age=?,AddDamage=?,AddDamageRate=?,AddReduction=?,AddReductionRate=?,IsPeerage=?"
							+ ",Abysspoint=?,TamEndTime=?,SpecialSize=?,FishingShopBuyTime_1=?,HuntPrice=?, HuntCount=?, HuntText=?,Clan_Join_Date=?");
			pstm.setString(++i, pc.getAccountName());
			pstm.setInt(++i, pc.getId());
			pstm.setString(++i, pc.getName());
			pstm.setInt(++i, pc.getLevel());
			pstm.setInt(++i, pc.getHighLevel());
			pstm.setInt(++i, pc.getExp());
			pstm.setInt(++i, pc.getBaseMaxHp());
			int hp = pc.getCurrentHp();
			if (hp < 1) {
				hp = 1;
			}
			pstm.setInt(++i, hp);
			pstm.setInt(++i, pc.getBaseMaxMp());
			pstm.setInt(++i, pc.getCurrentMp());
			pstm.setInt(++i, pc.getAC().getAc());
			pstm.setInt(++i, pc.getAbility().getStr());
			pstm.setInt(++i, pc.getAbility().getBaseStr());
			pstm.setInt(++i, pc.getAbility().getCon());
			pstm.setInt(++i, pc.getAbility().getBaseCon());
			pstm.setInt(++i, pc.getAbility().getDex());
			pstm.setInt(++i, pc.getAbility().getBaseDex());
			pstm.setInt(++i, pc.getAbility().getCha());
			pstm.setInt(++i, pc.getAbility().getBaseCha());
			pstm.setInt(++i, pc.getAbility().getInt());
			pstm.setInt(++i, pc.getAbility().getBaseInt());
			pstm.setInt(++i, pc.getAbility().getWis());
			pstm.setInt(++i, pc.getAbility().getBaseWis());
			pstm.setInt(++i, pc.getCurrentWeapon());
			pstm.setInt(++i, pc.getClassId());
			pstm.setInt(++i, pc.get_sex());
			pstm.setInt(++i, pc.getType());
			pstm.setInt(++i, pc.getHeading());
			pstm.setInt(++i, pc.getX());
			pstm.setInt(++i, pc.getY());
			pstm.setInt(++i, pc.getMapId());
			pstm.setInt(++i, pc.get_food());
			pstm.setInt(++i, pc.getLawful());
			pstm.setString(++i, pc.getTitle());
			pstm.setInt(++i, pc.getClanid());
			pstm.setString(++i, pc.getClanname());
			pstm.setInt(++i, pc.getClanRank());
			pstm.setString(++i, pc.getClanMemberNotes());
			pstm.setInt(++i, pc.getBonusStats());
			pstm.setInt(++i, pc.getElixirStats());
			pstm.setInt(++i, pc.getElfAttr());
			pstm.setInt(++i, pc.get_PKcount());
			pstm.setInt(++i, pc.getExpRes());
			pstm.setInt(++i, pc.getPartnerId());
			pstm.setShort(++i, pc.getAccessLevel());
			pstm.setInt(++i, pc.getOnlineStatus());
			pstm.setInt(++i, pc.getHomeTownId());
			pstm.setInt(++i, pc.getContribution());
			pstm.setInt(++i, 0);
			pstm.setInt(++i, pc.getHellTime());
			pstm.setBoolean(++i, pc.isBanned());
			pstm.setInt(++i, pc.getKarma());
			pstm.setTimestamp(++i, pc.getLastPk());
			pstm.setTimestamp(++i, pc.getDeleteTime());
			pstm.setInt(++i, pc.getReturnStat());
			pstm.setInt(++i, pc.getAinState()); //생존 쿨타임
			pstm.setInt(++i, pc.getSurvivalGauge()); //생존게이지			
			pstm.setInt(++i, pc.getBirthDay());
			pstm.setInt(++i, pc.getGirandungeonTime());
			pstm.setInt(++i, pc.getOrendungeonTime());
			pstm.setInt(++i, pc.getDrageonTime());
			pstm.setInt(++i, pc.getRadungeonTime());
			pstm.setInt(++i, pc.getSomeTime());
			pstm.setInt(++i, pc.getSoulTime());
			pstm.setInt(++i, pc.getnewdodungeonTime());//발록진영
			pstm.setInt(++i, pc.geticedungeonTime());//얼던PC
			pstm.setInt(++i, pc.getislandTime());//말던
			pstm.setInt(++i, pc.getMark_count());
			pstm.setInt(++i, pc.getAge()); //나이설정
			/** 캐릭별 추가데미지, 추가리덕션, 확률 **/
			pstm.setInt(++i, pc.getAddDamage());
			pstm.setInt(++i, pc.getAddDamageRate());
			pstm.setInt(++i, pc.getAddReduction());
			pstm.setInt(++i, pc.getAddReductionRate());
			/** 캐릭별 추가데미지, 추가리덕션, 확률 **/
			pstm.setInt(++i, pc.getPeerage());
			pstm.setInt(++i, pc.getAbysspoint());
			pstm.setTimestamp(++i, pc.getTamTime());
			pstm.setInt(++i, pc.get_SpecialSize());
			pstm.setString(++i, pc.getFishingShopBuyTime_1()==0 ? "0000-00-00 00:00:00" : new Timestamp(pc.getFishingShopBuyTime_1()).toString());
			pstm.setInt(++i, pc.getHuntPrice());		
			pstm.setInt(++i, pc.getHuntCount());
			pstm.setString(++i, pc.getReasonToHunt());
			pstm.setTimestamp(++i, pc.getClanJoinDate());
			pstm.execute();
			_log.finest("stored char data: " + pc.getName());
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	@Override
	public void deleteCharacter(String accountName, String charName)
			throws Exception {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("SELECT objid FROM characters WHERE account_name=? AND char_name=?");
			pstm.setString(1, accountName);
			pstm.setString(2, charName);
			rs = pstm.executeQuery();
			if (!rs.next()) {
				/*
				 * SELECT가 값을 돌려주지 않았다
				 * 존재하지 않는지, 혹은 다른 어카운트가 소유하고 있는 캐릭터명이 지정되었다고 하는 것이 된다.
				 */
				_log.warning("invalid delete char request: account="
						+ accountName + " char=" + charName);
				throw new RuntimeException("could not delete character");
			}
			
			int objId = rs.getInt("objid");

			pstm = con.prepareStatement("DELETE FROM character_buddys WHERE char_id = ?");
			pstm.setInt(1, objId);
			pstm.execute();
			pstm = con.prepareStatement("DELETE FROM character_buff WHERE char_obj_id = ?");
			pstm.setInt(1, objId);
			pstm.execute();
			pstm = con.prepareStatement("DELETE FROM character_config WHERE object_id = ?");
			pstm.setInt(1, objId);
			pstm.execute();
			pstm = con.prepareStatement("DELETE FROM character_items WHERE char_id = ?");
			pstm.setInt(1, objId);
			pstm.execute();
			pstm = con.prepareStatement("DELETE FROM character_quests WHERE char_id = ?");
			pstm.setInt(1, objId);
			pstm.execute();
			pstm = con.prepareStatement("DELETE FROM character_skills WHERE char_obj_id = ?");
			pstm.setInt(1, objId);
			pstm.execute();
			pstm = con.prepareStatement("DELETE FROM character_teleport WHERE char_id = ?");
			pstm.setInt(1, objId);
			pstm.execute();
			pstm = con.prepareStatement("DELETE FROM characters WHERE objid=?");
			pstm.setInt(1, objId);
			pstm.execute();

		} catch (SQLException e) {
			throw e;
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	@Override
	public void updateAccountName(L1PcInstance pc) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			int i = 0;
			con = L1DatabaseFactory.getInstance().getConnection();
			
			pstm = con.prepareStatement("UPDATE characters SET account_name=? WHERE objid=?");
			pstm.setString(++i, pc.getAccountName());
			pstm.setInt(++i, pc.getId());
			pstm.execute();
			_log.finest("update account data:" + pc.getName());
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	@Override
	public void storeCharacter(L1PcInstance pc) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			int i = 0;
			con = L1DatabaseFactory.getInstance().getConnection();
			
			pstm = con.prepareStatement("UPDATE characters SET level=?,HighLevel=?,Exp=?,MaxHp=?" +
					",CurHp=?,MaxMp=?,CurMp=?,Ac=?,Str=?,BaseStr=?,Con=?,BaseCon=?,Dex=?,BaseDex=?" +
					",Cha=?,BaseCha=?,Intel=?,BaseIntel=?,Wis=?,BaseWis=?,Status=?,Class=?,Sex=?,Type=?" +
					",Heading=?,LocX=?,LocY=?,MapID=?,Food=?,Lawful=?,Title=?,ClanID=?,Clanname=?,ClanRank=?" +
					",notes=?,BonusStatus=?,ElixirStatus=?,ElfAttr=?,PKcount=?,ExpRes=?,PartnerID=?,AccessLevel=?" +
					",OnlineStatus=?,HomeTownID=?,Contribution=?,HellTime=?,Banned=?,Karma=?,LastPk=?" +
					",DeleteTime=?,ReturnStat=?,sealScrollTime=?,sealScrollCount=?,lastLogoutTime=now()" +
					",einhasad=?,AinState=?,SurvivalGauge=?, PC_Kill=?, PC_Death=?" +
					",GiranTime=?,OrenTime=?,DrageonTime=?,RadungeonTime=?,SomeTime=?,SoulTime=?,newdoTime=?,iceTime=?,islanddungeonTime=?,Mark_Count=?,Age=?,AddDamage=?,AddDamageRate=?,AddReduction=?,AddReductionRate=?,IsPeerage=?,Abysspoint=?,TamEndTime=?,SpecialSize=?,FishingShopBuyTime_1=?,HuntPrice=?,HuntCount=?,HuntText=? WHERE objid=?");
			pstm.setInt(++i, pc.getLevel());
			pstm.setInt(++i, pc.getHighLevel());
			pstm.setInt(++i, pc.getExp());
			pstm.setInt(++i, pc.getBaseMaxHp());
			int hp = pc.getCurrentHp();
			if (hp < 1) {
				hp = 1;
			}
			pstm.setInt(++i, hp);
			pstm.setInt(++i, pc.getBaseMaxMp());
			pstm.setInt(++i, pc.getCurrentMp());
			pstm.setInt(++i, pc.getAC().getAc());
			pstm.setInt(++i, pc.getAbility().getStr());
			pstm.setInt(++i, pc.getAbility().getBaseStr());
			pstm.setInt(++i, pc.getAbility().getCon());
			pstm.setInt(++i, pc.getAbility().getBaseCon());
			pstm.setInt(++i, pc.getAbility().getDex());
			pstm.setInt(++i, pc.getAbility().getBaseDex());
			pstm.setInt(++i, pc.getAbility().getCha());
			pstm.setInt(++i, pc.getAbility().getBaseCha());
			pstm.setInt(++i, pc.getAbility().getInt());
			pstm.setInt(++i, pc.getAbility().getBaseInt());
			pstm.setInt(++i, pc.getAbility().getWis());
			pstm.setInt(++i, pc.getAbility().getBaseWis());
			pstm.setInt(++i, pc.getCurrentWeapon());
			pstm.setInt(++i, pc.getClassId());
			pstm.setInt(++i, pc.get_sex());
			pstm.setInt(++i, pc.getType());
			pstm.setInt(++i, pc.getHeading());
			pstm.setInt(++i, pc.getX());
			pstm.setInt(++i, pc.getY());
			pstm.setInt(++i, pc.getMapId());
			pstm.setInt(++i, pc.get_food());
			pstm.setInt(++i, pc.getLawful());
			pstm.setString(++i, pc.getTitle());
			pstm.setInt(++i, pc.getClanid());
			pstm.setString(++i, pc.getClanname());
			pstm.setInt(++i, pc.getClanRank());
			pstm.setString(++i, pc.getClanMemberNotes());
			pstm.setInt(++i, pc.getBonusStats());
			pstm.setInt(++i, pc.getElixirStats());
			pstm.setInt(++i, pc.getElfAttr());
			pstm.setInt(++i, pc.get_PKcount());
			pstm.setInt(++i, pc.getExpRes());
			pstm.setInt(++i, pc.getPartnerId());
			pstm.setShort(++i, pc.getAccessLevel());
			pstm.setInt(++i, pc.getOnlineStatus());
			pstm.setInt(++i, pc.getHomeTownId());
			pstm.setInt(++i, pc.getContribution());
			pstm.setInt(++i, pc.getHellTime());
			pstm.setBoolean(++i, pc.isBanned());
			pstm.setInt(++i, pc.getKarma());
			pstm.setTimestamp(++i, pc.getLastPk());
			pstm.setTimestamp(++i, pc.getDeleteTime());
			pstm.setInt(++i, pc.getReturnStat());
			pstm.setInt(++i, pc.getSealScrollTime());
			pstm.setInt(++i, pc.getSealScrollCount());
			pstm.setInt(++i, pc.getEinhasad());
			pstm.setInt(++i, pc.getAinState()); //생존 쿨타임
			pstm.setInt(++i, pc.getSurvivalGauge()); //생존게이지
			pstm.setInt(++i, pc.getKills());
			pstm.setInt(++i, pc.getDeaths());
			pstm.setInt(++i, pc.getGirandungeonTime());
			pstm.setInt(++i, pc.getOrendungeonTime());
			pstm.setInt(++i, pc.getDrageonTime());
			pstm.setInt(++i, pc.getRadungeonTime());
			pstm.setInt(++i, pc.getSomeTime());
			pstm.setInt(++i, pc.getSoulTime());
			pstm.setInt(++i, pc.getnewdodungeonTime());//발록진영
			pstm.setInt(++i, pc.geticedungeonTime());//얼던PC
			pstm.setInt(++i, pc.getislandTime());//말섬
			pstm.setInt(++i, pc.getMark_count());
			pstm.setInt(++i, pc.getAge()); //나이설정
			/** 캐릭별 추가데미지, 추가리덕션, 확률 **/
			pstm.setInt(++i, pc.getAddDamage());
			pstm.setInt(++i, pc.getAddDamageRate());
			pstm.setInt(++i, pc.getAddReduction());
			pstm.setInt(++i, pc.getAddReductionRate());
			/** 캐릭별 추가데미지, 추가리덕션, 확률 **/
			pstm.setInt(++i, pc.getPeerage());
			pstm.setInt(++i, pc.getAbysspoint());
			pstm.setTimestamp(++i, pc.getTamTime());
			pstm.setInt(++i, pc.get_SpecialSize());
			pstm.setString(++i, pc.getFishingShopBuyTime_1()==0 ? "0000-00-00 00:00:00" : new Timestamp(pc.getFishingShopBuyTime_1()).toString());
			pstm.setInt(++i, pc.getHuntPrice());	
			pstm.setInt(++i, pc.getHuntCount());
			pstm.setString(++i, pc.getReasonToHunt());
			pstm.setInt(++i, pc.getId());
			pstm.execute();
			_log.finest("stored char data:" + pc.getName());
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	
	public void storeMemo(L1PcInstance pc, String memo) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("UPDATE characters SET Memo=? WHERE objid=?");
			pstm.setString(1, memo);
			pstm.setInt(2, pc.getId());
			pstm.execute();
			_log.finest("stored char data:" + pc.getName());
		} catch (SQLException e) {
			System.out.println("ERROR:"+e);
			_log.log(Level.SEVERE, "MySqlCharacterStorage[]Error2", e);
		} catch (Exception e) {
			System.out.println("ERROR:"+e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public String getMemo(String name) {
		String memo = null;
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT Memo FROM characters WHERE char_name=?");
			pstm.setString(1, name);
			rs = pstm.executeQuery();

			if (rs.next())
				memo = rs.getString(1);
		} catch (SQLException e) {
			System.out.println("ERROR:"+e);
			_log.log(Level.SEVERE, "MySqlCharacterStorage[]Error2", e);
		} catch (Exception e) {
			System.out.println("ERROR:"+e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return memo;
	}
}
