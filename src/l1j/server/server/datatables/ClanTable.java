package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.IdFactory;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Warehouse.ClanWarehouse;
import l1j.server.server.model.Warehouse.WarehouseManager;
import l1j.server.server.utils.SQLUtil;

public class ClanTable {
    private static Logger _log = Logger.getLogger(ClanTable.class.getName());

    private static ClanTable _instance;

    private final HashMap<Integer, L1Clan> _clans = new HashMap<Integer, L1Clan>();

    private final HashMap<Integer, L1Clan> _clancastle = new HashMap<Integer, L1Clan>();

    public static ClanTable getInstance() {
        if (_instance == null) {
            _instance = new ClanTable();
        }
        return _instance;
    }

    private ClanTable() {
        {
            Connection con = null;
            PreparedStatement pstm = null;
            ResultSet rs = null;

            try {
                con = L1DatabaseFactory.getInstance().getConnection();
                pstm = con.prepareStatement("SELECT * FROM clan_data ORDER BY clan_id");

                rs = pstm.executeQuery();
                L1Clan clan = null;
                while (rs.next()) {
                    clan = new L1Clan();
                    int clan_id = rs.getInt(1);
                    int castle_id = rs.getInt(5);
                    clan.setClanId(clan_id);
                    clan.setClanName(rs.getString(2));
                    clan.setLeaderId(rs.getInt(3));
                    clan.setLeaderName(rs.getString(4));
                    clan.setCastleId(castle_id);
                    clan.setHouseId(rs.getInt(6));
                    clan.setAlliance(rs.getInt(7));
                    clan.setClanBirthDay(rs.getTimestamp(8));
                    /** 血盟自動登録 */
                    clan.setBot(rs.getString(9).equalsIgnoreCase("true"));
                    clan.setBotStyle(rs.getInt(10));
                    clan.setBotLevel(rs.getInt(11));
                    /** 血盟自動登録 */
                    clan.setOnlineMaxUser(rs.getInt(12));
                    clan.setAnnouncement(rs.getString(13));
                    clan.setEmblemId(rs.getInt(14));
                    clan.setEmblemStatus(rs.getInt(15));
                    clan.setClanExp(rs.getInt(16));
                    clan.setBless(rs.getInt("bless"));
                    clan.setBlessCount(rs.getInt("bless_count"));
                    clan.setBuffTime(rs.getInt("attack"), rs.getInt("defence"), rs.getInt("pvpattack"), rs.getInt("pvpdefence"));
                    clan.setUnderDungeon(rs.getInt("under_dungeon"));
                    clan.setRankTime(rs.getInt("ranktime"));
                    clan.setRankDate(rs.getTimestamp("rankdate"));
                    L1World.getInstance().storeClan(clan);
                    _clans.put(clan_id, clan);
                    if (castle_id > 0) {
                        _clancastle.put(castle_id, clan);
                    }
                }

            } catch (SQLException e) {
                _log.log(Level.SEVERE, "ClanTable[]Error", e);
            } finally {
                SQLUtil.close(rs);
                SQLUtil.close(pstm);
                SQLUtil.close(con);
            }
        }

        for (L1Clan clan : L1World.getInstance().getAllClans()) {
            Connection con = null;
            PreparedStatement pstm = null;
            ResultSet rs = null;

            try {
                con = L1DatabaseFactory.getInstance().getConnection();
                pstm = con.prepareStatement("SELECT char_name, ClanRank, level, notes, objid, Type FROM characters WHERE ClanID = ?");
                pstm.setInt(1, clan.getClanId());
                rs = pstm.executeQuery();

                while (rs.next()) {
                    String name = rs.getString("char_name");
                    int rank = rs.getInt("ClanRank");
                    int level = rs.getInt("level");
                    String notes = rs.getString("notes");
                    int memberId = rs.getInt("objid");
                    int type = rs.getInt("Type");
                    clan.addClanMember(name, rank, level, notes, memberId, type, 0, null);
                }
            } catch (SQLException e) {
                _log.log(Level.SEVERE, "ClanTable[]Error1", e);
            } finally {
                SQLUtil.close(rs);
                SQLUtil.close(pstm);

            }

            try {
                pstm = con
                        .prepareStatement("SELECT name, class FROM robots WHERE clanid = ?");
                pstm.setInt(1, clan.getClanId());
                rs = pstm.executeQuery();

                while (rs.next()) {
                    String name;
                    String Memo;
                    int rank;
                    int level;
                    int type;
                    name = rs.getString("name");
                    rank = L1Clan.NORMAL;
                    level = 65;
                    int memberId = 0;
                    int clas = rs.getInt("class");
                    if (clas == 0 || clas == 1)
                        type = 0;
                    else if (clas == 61 || clas == 48)
                        type = 1;
                    else if (clas == 138 || clas == 37)
                        type = 2;
                    else if (clas == 734 || clas == 1186)
                        type = 3;
                    else if (clas == 2786 || clas == 2796)
                        type = 4;
                    else if (clas == 6658 || clas == 6661)
                        type = 5;
                    else
                        type = 6;
                    Memo = "";

                    clan.addClanMember(name, rank, level, Memo, memberId, type, 0, null);
                }
            } catch (SQLException e) {
                _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            } finally {
                SQLUtil.close(rs);
                SQLUtil.close(pstm);
            }
            try {
                pstm = con
                        .prepareStatement("SELECT name, clan_memo FROM robots_crown WHERE clanid = ?");
                pstm.setInt(1, clan.getClanId());
                rs = pstm.executeQuery();

                while (rs.next()) {
                    String name;
                    String Memo;
                    int rank;
                    int level;
                    int type;
                    int memberId = 0;
                    name = rs.getString("name");
                    rank = L1Clan.MONARCH;
                    level = 65;
                    type = 0;
                    Memo = rs.getString("clan_memo");

                    clan.addClanMember(name, rank, level, Memo, memberId, type, 0, null);
                }
            } catch (SQLException e) {
                _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            } finally {
                SQLUtil.close(rs);
                SQLUtil.close(pstm);
                SQLUtil.close(con);
            }
        }

        ClanWarehouse clanWarehouse;
        for (L1Clan clan : L1World.getInstance().getAllClans()) {
            clanWarehouse = WarehouseManager.getInstance().getClanWarehouse(clan.getClanName());
            clanWarehouse.loadItems();
        }
    }

    public L1Clan createClan(L1PcInstance player, String clan_name) {
        for (L1Clan oldClans : L1World.getInstance().getAllClans()) {
            if (oldClans.getClanName().equalsIgnoreCase(clan_name)) {
                return null;
            }
        }
        Timestamp time = new Timestamp(System.currentTimeMillis());
        L1Clan clan = new L1Clan();
        clan.setClanId(IdFactory.getInstance().nextId());
        clan.setClanName(clan_name);
        clan.setLeaderId(player.getId());
        clan.setLeaderName(player.getName());
        clan.setCastleId(0);
        clan.setHouseId(0);
        clan.setAlliance(0);
        clan.setClanBirthDay(time);
        clan.setAnnouncement("");
        clan.setEmblemId(0);
        clan.setEmblemStatus(0);
        clan.setBless(0);
        clan.setBlessCount(0);
        clan.setBuffTime(0, 0, 0, 0);

        Connection con = null;
        PreparedStatement pstm = null;

        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("INSERT INTO clan_data SET clan_id=?, clan_name=?, leader_id=?, leader_name=?, hascastle=?, hashouse=?, alliance=?, clan_birthday=?, max_online_user=?, announcement=?, emblem_id=?, emblem_status=?,clan_exp=?,bless=?,bless_count=?,attack=?,defence=?,pvpattack=?,pvpdefence=?");
            pstm.setInt(1, clan.getClanId());
            pstm.setString(2, clan.getClanName());
            pstm.setInt(3, clan.getLeaderId());
            pstm.setString(4, clan.getLeaderName());
            pstm.setInt(5, clan.getCastleId());
            pstm.setInt(6, clan.getHouseId());
            pstm.setInt(7, clan.getAlliance());
            pstm.setTimestamp(8, clan.getClanBirthDay());
            pstm.setInt(9, clan.getOnlineMaxUser());
            pstm.setString(10, "");
            pstm.setInt(11, 0);
            pstm.setInt(12, 0);
            pstm.setInt(13, 0);

            pstm.setInt(14, 0);
            pstm.setInt(15, 0);
            pstm.setInt(16, 0);
            pstm.setInt(17, 0);
            pstm.setInt(18, 0);
            pstm.setInt(19, 0);
            pstm.execute();
        } catch (SQLException e) {
            _log.log(Level.SEVERE, "ClanTable[]Error2", e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }

        L1World.getInstance().storeClan(clan);
        _clans.put(clan.getClanId(), clan);

        player.setClanid(clan.getClanId());
        player.setClanname(clan.getClanName());

//		if(player.getQuest().isEnd(L1Quest.QUEST_LEVEL45)){
//			player.setClanRank(L1Clan.CLAN_RANK_LEAGUE_PRINCE);
//			player.sendPackets(new S_PacketBox(S_PacketBox.MSG_RANK_CHANGED, L1Clan.CLAN_RANK_LEAGUE_PRINCE, player.getName()));
//		} else {
        player.setClanRank(L1Clan.MONARCH);
        //	player.sendPackets(new S_PacketBox(S_PacketBox.MSG_RANK_CHANGED, L1Clan.君主、player.getName（）））;
        //}
        clan.addClanMember(player.getName(), player.getClanRank(), player.getLevel(), "", player.getId(), player.getType(), player.getOnlineStatus(), player);
        try {
            player.save();
        } catch (Exception e) {
            _log.log(Level.SEVERE, "ClanTable[]Error3", e);
        }
        return clan;
    }

    public L1Clan createClan(L1PcInstance player, String clan_name, int clanid) {
        for (L1Clan oldClans : L1World.getInstance().getAllClans()) {
            if (oldClans.getClanName().equalsIgnoreCase(clan_name)) {
                return null;
            }
        }
        Timestamp time = new Timestamp(System.currentTimeMillis());
        L1Clan clan = new L1Clan();
        if (clanid == 0)
            clan.setClanId(IdFactory.getInstance().nextId());
        else
            clan.setClanId(clanid);
        clan.setClanName(clan_name);
        clan.setLeaderId(player.getId());
        clan.setLeaderName(player.getName());
        clan.setCastleId(0);
        clan.setHouseId(0);
        clan.setAlliance(0);
        clan.setClanBirthDay(time);
        clan.setAnnouncement("");
        clan.setEmblemId(0);
        clan.setEmblemStatus(0);
        clan.setBless(0);
        clan.setBlessCount(0);
        clan.setBuffTime(0, 0, 0, 0);

        Connection con = null;
        PreparedStatement pstm = null;

        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("INSERT INTO clan_data SET clan_id=?, clan_name=?, leader_id=?, leader_name=?, hascastle=?, hashouse=?, alliance=?, clan_birthday=?, max_online_user=?, announcement=?, emblem_id=?, emblem_status=?,clan_exp=?,bless=?,bless_count=?,attack=?,defence=?,pvpattack=?,pvpdefence=?");
            pstm.setInt(1, clan.getClanId());
            pstm.setString(2, clan.getClanName());
            pstm.setInt(3, clan.getLeaderId());
            pstm.setString(4, clan.getLeaderName());
            pstm.setInt(5, clan.getCastleId());
            pstm.setInt(6, clan.getHouseId());
            pstm.setInt(7, clan.getAlliance());
            pstm.setTimestamp(8, clan.getClanBirthDay());
            pstm.setInt(9, clan.getOnlineMaxUser());
            pstm.setString(10, "");
            pstm.setInt(11, 0);
            pstm.setInt(12, 0);
            pstm.setInt(13, 0);

            pstm.setInt(14, 0);
            pstm.setInt(15, 0);
            pstm.setInt(16, 0);
            pstm.setInt(17, 0);
            pstm.setInt(18, 0);
            pstm.setInt(19, 0);
            pstm.execute();
        } catch (SQLException e) {
            _log.log(Level.SEVERE, "ClanTable[]Error2", e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }

        L1World.getInstance().storeClan(clan);
        _clans.put(clan.getClanId(), clan);

        player.setClanid(clan.getClanId());
        player.setClanname(clan.getClanName());

//		if(player.getQuest().isEnd(L1Quest.QUEST_LEVEL45)){
//			player.setClanRank(L1Clan.CLAN_RANK_LEAGUE_PRINCE);
//			player.sendPackets(new S_PacketBox(S_PacketBox.MSG_RANK_CHANGED, L1Clan.CLAN_RANK_LEAGUE_PRINCE, player.getName()));
//		} else {
        player.setClanRank(L1Clan.MONARCH);
        //	player.sendPackets(new S_PacketBox(S_PacketBox.MSG_RANK_CHANGED, L1Clan.君主、player.getName（）））;
        //}
        clan.addClanMember(player.getName(), player.getClanRank(), player.getLevel(), "", player.getId(), player.getType(), player.getOnlineStatus(), player);
        try {
            player.save();
        } catch (Exception e) {
            _log.log(Level.SEVERE, "ClanTable[]Error3", e);
        }
        return clan;
    }

    public void updateClan(L1Clan clan) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("UPDATE clan_data SET clan_id=?, leader_id=?, leader_name=?, hascastle=?, hashouse=?, alliance=?, clan_birthday=?, bot_style=?, bot_level=?, max_online_user=?, announcement=?, emblem_id=?, emblem_status=?,clan_exp=? WHERE clan_name=?");
            pstm.setInt(1, clan.getClanId());
            pstm.setInt(2, clan.getLeaderId());
            pstm.setString(3, clan.getLeaderName());
            pstm.setInt(4, clan.getCastleId());
            pstm.setInt(5, clan.getHouseId());
            pstm.setInt(6, clan.getAlliance());
            pstm.setTimestamp(7, clan.getClanBirthDay());
            /** 血盟自動登録 */
            pstm.setInt(8, clan.getBotStyle());
            pstm.setInt(9, clan.getBotLevel());
            /** 血盟自動登録 */
            pstm.setInt(10, clan.getOnlineMaxUser());
            pstm.setString(11, clan.getAnnouncement());
            pstm.setInt(12, clan.getEmblemId());
            pstm.setInt(13, clan.getEmblemStatus());
            pstm.setInt(14, clan.getClanExp());

            pstm.setString(15, clan.getClanName());
            pstm.execute();
        } catch (SQLException e) {
            _log.log(Level.SEVERE, "ClanTable[]Error4", e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    /**
     * * 血盟自動登録*
     *
     * @param player
     * @param clan_name
     * @param style
     * @return
     */
    public void createClanBot(L1PcInstance player, String clan_name, int style) {
        for (L1Clan oldClans : L1World.getInstance().getAllClans()) {
            if (oldClans.getClanName().equalsIgnoreCase(clan_name))
                return;
        }

        L1Clan clan = new L1Clan();
        clan.setClanId(IdFactory.getInstance().nextId());
        clan.setClanName(clan_name);
        clan.setLeaderId(player.getId());
        clan.setLeaderName(player.getName());
        clan.setCastleId(0);
        clan.setHouseId(0);
        clan.setBot(true);
        clan.setBotStyle(style);

        player.setClanid(clan.getClanId());
        player.setClanname(clan.getClanName());

        Connection con = null;
        PreparedStatement pstm = null;

        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con
                    .prepareStatement("INSERT INTO clan_data SET clan_id=?, clan_name=?, leader_id=?, leader_name=?, hascastle=?, hashouse=?, bot=?, bot_style=?");
            pstm.setInt(1, clan.getClanId());
            pstm.setString(2, clan.getClanName());
            pstm.setInt(3, clan.getLeaderId());
            pstm.setString(4, clan.getLeaderName());
            pstm.setInt(5, clan.getCastleId());
            pstm.setInt(6, clan.getHouseId());
            pstm.setString(7, "true");
            pstm.setInt(8, style);
            pstm.execute();
        } catch (SQLException e) {
            _log.log(Level.SEVERE, "ClanTable[]Error5", e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        /** 血盟自動登録 */
        L1World.getInstance().storeClan(clan);
        _clans.put(clan.getClanId(), clan);
        /** 血盟自動登録 */
    }

    public void deleteClan(String clan_name) {
        L1Clan clan = L1World.getInstance().getClan(clan_name);
        if (clan == null) {
            return;
        }
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("DELETE FROM clan_data WHERE clan_name=?");
            pstm.setString(1, clan_name);
            pstm.execute();
        } catch (SQLException e) {
            _log.log(Level.SEVERE, "ClanTable[]Error6", e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }

        ClanWarehouse clanWarehouse = WarehouseManager.getInstance().getClanWarehouse(clan.getClanName());
        clanWarehouse.clearItems();
        clanWarehouse.deleteAllItems();

        L1World.getInstance().removeClan(clan);
        _clans.remove(clan.getClanId());
    }

    public L1Clan getTemplate(int clan_id) {
        return _clans.get(clan_id);
    }

    /**
     * 血盟自動登録
     */
    public static void reload() {
        ClanTable oldInstance = _instance;
        _instance = new ClanTable();
        if (oldInstance != null) {
            oldInstance._clans.clear();
            oldInstance._clancastle.clear();
        }
    }

    public L1Clan find(String clan_name) {
        for (L1Clan clan : _clans.values()) {
            if (clan.getClanName().equalsIgnoreCase(clan_name))
                return clan;
        }
        return null;
    }

    /**
     * 血盟自動登録
     */
    public HashMap<Integer, L1Clan> getClanCastles() {
        return _clancastle;
    }

    public void updateUnderDungeon(int clanid, int type) {
        try (Connection con = L1DatabaseFactory.getInstance().getConnection(); PreparedStatement pstm = con.prepareStatement("UPDATE clan_data SET under_dungeon=? WHERE clan_id=?")) {
            pstm.setInt(1, type);
            pstm.setInt(2, clanid);
            pstm.execute();
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
    }

    public void updateRankDate(int clanid, Timestamp time) {
        try (Connection con = L1DatabaseFactory.getInstance().getConnection(); PreparedStatement pstm = con.prepareStatement("UPDATE clan_data SET rankdate=? WHERE clan_id=?")) {
            pstm.setTimestamp(1, time);
            pstm.setInt(2, clanid);
            pstm.execute();
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
    }

    public void updateRankTime(int clanid, int time) {
        try (Connection con = L1DatabaseFactory.getInstance().getConnection(); PreparedStatement pstm = con.prepareStatement("UPDATE clan_data SET ranktime=? WHERE clan_id=?")) {
            pstm.setInt(1, time);
            pstm.setInt(2, clanid);
            pstm.execute();
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
    }

    private static String currentTime() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+9"));
        int year = cal.get(Calendar.YEAR);
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
        return year + "/" + Month2 + "/" + date2;
    }

    /**
     * 血盟バフ
     **/
    public void updateBlessCount(int clanid, int count) {
        try (Connection con = L1DatabaseFactory.getInstance().getConnection();
             PreparedStatement pstm = con.prepareStatement("UPDATE clan_data SET bless_count=? WHERE clan_id=?")) {
            pstm.setInt(1, count);
            pstm.setInt(2, clanid);
            pstm.execute();
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
    }

    public void updateBless(int clanid, int bless) {
        try (Connection con = L1DatabaseFactory.getInstance().getConnection(); PreparedStatement pstm = con.prepareStatement("UPDATE clan_data SET bless=? WHERE clan_id=?")) {
            pstm.setInt(1, bless);
            pstm.setInt(2, clanid);
            pstm.execute();
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
    }

    public void updateBuffTime(int a, int b, int c, int d, int clanid) {
        try (Connection con = L1DatabaseFactory.getInstance().getConnection();
             PreparedStatement pstm = con.prepareStatement("UPDATE clan_data SET attack=?,defence=?,pvpattack=?,pvpdefence=? WHERE clan_id=?")) {
            pstm.setInt(1, a);
            pstm.setInt(2, b);
            pstm.setInt(3, c);
            pstm.setInt(4, d);
            pstm.setInt(5, clanid);
            pstm.execute();
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
    }

}
