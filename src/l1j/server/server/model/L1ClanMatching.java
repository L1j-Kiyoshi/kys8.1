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

package l1j.server.server.model;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.utils.SQLUtil;

public class L1ClanMatching {

    private static Logger _log = Logger.getLogger(L1ClanMatching.class.getName());
    private static L1ClanMatching _instance;

    public static L1ClanMatching getInstance() {
        if (_instance == null) {
            _instance = new L1ClanMatching();
        }
        return _instance;
    }

    public void writeClanMatching(String clanname, String text, int htype) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("INSERT INTO clan_matching_list SET clanname = ?, text = ?, type = ?");
            pstm.setString(1, clanname);
            pstm.setString(2, text);
            pstm.setInt(3, htype);
            ClanMatchingList CML = new ClanMatchingList(clanname, text, htype);
            addMatching(CML);
            pstm.execute();
        } catch (Exception e) {
            _log.log(Level.SEVERE, "クランマッチングリスト書き込み、エラーが発生し。", e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void updateClanMatching(String clanname, String text, int htype) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("UPDATE clan_matching_list SET text = ?, type = ? WHERE clanname = ?");
            pstm.setString(1, text);
            pstm.setInt(2, htype);
            pstm.setString(3, clanname);
            ClanMatchingList CML = getClanMatchingList(clanname);
            CML._text = text;
            CML._type = htype;
            pstm.execute();
        } catch (Exception e) {
            _log.log(Level.SEVERE, "クランマッチングリストの修正、エラーが発生し。", e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void deleteClanMatching(L1PcInstance pc) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("DELETE FROM clan_matching_list WHERE clanname=?");
            pstm.setString(1, pc.getClanname());
            pstm.execute();

            removeMatching(pc.getClanname());
            pc.getCMAList().clear();
            for (L1PcInstance clanuser : pc.getClan().getOnlineClanMember()) {
                switch (clanuser.getClanRank()) {
                    case 3:
                    case 9:
                    case 10:
                        clanuser.getCMAList().clear();
                        break;
                }
            }
            for (L1PcInstance player : L1World.getInstance().getAllPlayers()) {
                if (player.getClanid() == 0 &&
                        player.getCMAList().contains(pc.getClanname())) {
                    player.removeCMAList(pc.getClanname());
                }
            }
        } catch (Exception e) {
            _log.log(Level.SEVERE, "writeClanMatchingApcList、エラーが発生し。", e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void loadClanMatching() {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String clanname = null;
        String text = null;
        int type = 0;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT * FROM clan_matching_list");
            rs = pstm.executeQuery();
            while (rs.next()) {
                clanname = rs.getString("clanname");
                text = rs.getString("text");
                type = rs.getInt("type");
                ClanMatchingList CML = new ClanMatchingList(clanname, text, type);
                addMatching(CML);
            }
        } catch (Exception e) {
            _log.log(Level.SEVERE, "クランマッチングリストの読み込み、エラーが発生し。", e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    // ユーザ専用
    public void writeClanMatchingApcList_User(L1PcInstance pc, L1Clan clan) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("INSERT INTO clan_matching_apclist SET pc_name=?, pc_objid=?, clan_name=?");
            pstm.setString(1, pc.getName());
            pstm.setInt(2, pc.getId());
            pstm.setString(3, clan.getClanName());
            pstm.execute();

            pc.addCMAList(clan.getClanName());
            for (L1PcInstance clanuser : clan.getOnlineClanMember()) {
                switch (clanuser.getClanRank()) {
                    case 3:
                    case 4:
                    case 6:
                        clanuser.addCMAList(pc.getName());
                        break;
                }
            }
        } catch (Exception e) {
            _log.log(Level.SEVERE, "writeClanMatchingApcList_User、エラーが発生し。", e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    // ユーザ専用
    public void loadClanMatchingApcList_User(L1PcInstance pc) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String clanname = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT * FROM clan_matching_apclist WHERE pc_name = ?");
            pstm.setString(1, pc.getName());
            rs = pstm.executeQuery();
            while (rs.next()) {
                clanname = rs.getString("clan_name");
                pc.addCMAList(clanname);
            }
        } catch (Exception e) {
            _log.log(Level.SEVERE, "loadClanMatchingApcList_User、エラーが発生し。", e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    // 君主、守護騎士、富君主のみ。
    public void loadClanMatchingApcList_Crown(L1PcInstance pc) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String username = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT * FROM clan_matching_apclist WHERE clan_name = ?");
            pstm.setString(1, pc.getClanname());
            rs = pstm.executeQuery();
            while (rs.next()) {
                username = rs.getString("pc_name");
                pc.addCMAList(username);
            }
        } catch (Exception e) {
            _log.log(Level.SEVERE, "loadClanMatchingApcList_Crown、エラーが発生し。", e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }


    // 承認
    public void deleteClanMatchingApcList(L1PcInstance pc) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("DELETE FROM clan_matching_apclist WHERE pc_name=?");
            pstm.setString(1, pc.getName());
            pstm.execute();

            pc.getCMAList().clear();
            for (L1PcInstance clanuser : pc.getClan().getOnlineClanMember()) {
                switch (clanuser.getClanRank()) {
                    case 3:
                    case 4:
                    case 6:
                        clanuser.removeCMAList(pc.getName());
                        break;
                }
            }
        } catch (Exception e) {
            _log.log(Level.SEVERE, "writeClanMatchingApcList、エラーが発生し。", e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    // 拒絶押されたとき
    public void deleteClanMatchingApcList(L1PcInstance pc, int objid, L1Clan clan) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String pcname = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            if (pc == null) {
                pstm = con.prepareStatement("SELECT * FROM clan_matching_apclist WHERE pc_objid=? AND clan_name=?");
                pstm.setInt(1, objid);
                pstm.setString(2, clan.getClanName());
                rs = pstm.executeQuery();
                rs.next();
                pcname = rs.getString("pc_name");
            } else {
                pcname = pc.getName();
                pc.removeCMAList(clan.getClanName());
            }
            pstm = con.prepareStatement("DELETE FROM clan_matching_apclist WHERE pc_objid=? AND clan_name=?");
            pstm.setInt(1, objid);
            pstm.setString(2, clan.getClanName());
            pstm.execute();


            for (L1PcInstance clanuser : clan.getOnlineClanMember()) {
                switch (clanuser.getClanRank()) {
                    case 3:
                    case 9:
                    case 10:
                        clanuser.removeCMAList(pcname);
                        break;
                }
            }
        } catch (Exception e) {
            _log.log(Level.SEVERE, "deleteClanMatchingApcList、エラーが発生し。", e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }


    // 削除押されたとき
    public void deleteClanMatchingApcList(L1PcInstance pc, L1Clan clan) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("DELETE FROM clan_matching_apclist WHERE pc_name=? AND clan_name=?");
            pstm.setString(1, pc.getName());
            pstm.setString(2, clan.getClanName());
            pstm.execute();

            pc.removeCMAList(clan.getClanName());
            for (L1PcInstance clanuser : clan.getOnlineClanMember()) {
                switch (clanuser.getClanRank()) {
                    case 3:
                    case 9:
                    case 10:
                        clanuser.removeCMAList(pc.getName());
                        break;
                }
            }
        } catch (Exception e) {
            _log.log(Level.SEVERE, "writeClanMatchingApcList、エラーが発生し。", e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }


    private ArrayList<ClanMatchingList> _list = new ArrayList<ClanMatchingList>();

    public void addMatching(ClanMatchingList list) {
        if (_list.contains(list)) {
            return;
        }
        _list.add(list);
    }

    public void removeMatching(String clanname) {
        if (!isClanMatchingList(clanname)) {
            return;
        }
        _list.remove(getClanMatchingList(clanname));
    }

    public ArrayList<ClanMatchingList> getMatchingList() {
        return _list;
    }

    static public class ClanMatchingList {
        public String _clanname = null;
        public String _text = null;
        public int _type = 0;

        public ClanMatchingList(String clanname, String text, int type) {
            this._clanname = clanname;
            this._text = text;
            this._type = type;
        }
    }

    public boolean isClanMatchingList(String clanname) {
        for (int i = 0; i < _list.size(); i++) {
            if (_list.get(i)._clanname.equalsIgnoreCase(clanname)) return true;
        }
        return false;
    }

    public ClanMatchingList getClanMatchingList(String clanname) {
        ClanMatchingList CML = null;
        for (int i = 0; i < _list.size(); i++) {
            if (_list.get(i)._clanname.equalsIgnoreCase(clanname)) {
                CML = _list.get(i);
                break;
            }
        }
        return CML;
    }
}