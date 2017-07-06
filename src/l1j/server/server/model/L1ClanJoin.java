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

import java.io.File;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.ClanTable;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ACTION_UI;
import l1j.server.server.serverpackets.S_CharTitle;
import l1j.server.server.serverpackets.S_ClanName;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ReturnedStat;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.utils.SQLUtil;

public class L1ClanJoin {

	private static Logger _log = Logger.getLogger(L1ClanJoin.class.getName());
	private static L1ClanJoin _instance;
	public static L1ClanJoin getInstance() {
		if (_instance == null) { _instance = new L1ClanJoin(); }
		return _instance;
	}
	private L1ClanJoin() { }
	public boolean ClanJoin(L1PcInstance pc, L1PcInstance joinPc){
		int clan_id = pc.getClanid();
		String clanName = pc.getClanname();
		L1Clan clan = L1World.getInstance().getClan(clanName);
		if (clan != null) {
			int maxMember = 0;
			///////////혈맹리뉴얼//////////////
			int charisma = 0;
			if(pc.getId() != clan.getLeaderId())
				charisma = pc.getAbility().getTotalCha();
			else
				charisma = getOfflineClanLeaderCha(clan.getLeaderId());
			///////////혈맹리뉴얼//////////////
			boolean lv45quest = false;
			if (pc.getQuest().isEnd(L1Quest.QUEST_LEVEL45)) {
				lv45quest = true;
			}
			if (pc.getLevel() >= 50) { // Lv50 이상
				if (lv45quest == true) { // Lv45 퀘스트 클리어가 끝난 상태
					maxMember = charisma * 9;
				} else {
					maxMember = charisma * 3;
				}
			} else { // Lv50 미만
				if (lv45quest == true) { // Lv45 퀘스트 클리어가 끝난 상태
					maxMember = charisma * 6;
				} else {
					maxMember = charisma * 2;
				}
			}
			if (Config.MAX_CLAN_MEMBER > 0) { // Clan 인원수의 상한의 설정
				// 있어
				maxMember = Config.MAX_CLAN_MEMBER;
			}

			if (joinPc.getClanid() == 0) { // 크란미가입
				if (maxMember <= clan.getClanMemberList().size()) {
					joinPc.sendPackets(new S_ServerMessage(188, pc.getName())); 
					return false;
				}
				for (L1PcInstance clanMembers : clan.getOnlineClanMember()) {
					clanMembers.sendPackets(new S_ServerMessage(94,joinPc.getName()));
				}

				joinPc.setClanid(clan_id);
				joinPc.setClanname(clanName);
				joinPc.setClanRank(L1Clan.수련);
				joinPc.setClanMemberNotes("");					
				joinPc.setTitle("");
				joinPc.sendPackets(new S_CharTitle(joinPc.getId(), ""));
				Broadcaster.broadcastPacket(joinPc, new S_CharTitle(joinPc.getId(), ""));
				joinPc.setClanJoinDate(new Timestamp(System.currentTimeMillis()));
				try{
					joinPc.save(); // DB에 캐릭터 정보를 기입한다
				} catch(Exception e) {}
				clan.addClanMember(joinPc.getName(), joinPc.getClanRank(), joinPc.getLevel(), "", joinPc.getId(), joinPc.getType(), joinPc.getOnlineStatus(), joinPc);
				joinPc.sendPackets(new S_PacketBox(S_PacketBox.MSG_RANK_CHANGED, L1Clan.수련, joinPc.getName()));
				joinPc.sendPackets(new S_ServerMessage(95, clanName)); // \f1%0z				
				joinPc.sendPackets(new S_ClanName(joinPc, clan.getEmblemId(), joinPc.getClanRank()));	
				joinPc.sendPackets(new S_ReturnedStat(joinPc.getId(), clan.getClanId()));
				joinPc.sendPackets(new S_PacketBox(S_PacketBox.PLEDGE_EMBLEM_STATUS, pc.getClan().getEmblemStatus())); // TODO
				//joinPc.sendPackets(new S_ClanAttention());
				joinPc.sendPackets(new S_ACTION_UI(clan.getClanName(), joinPc.getClanRank()));
				for(L1PcInstance player : clan.getOnlineClanMember()){
					player.sendPackets(new S_ReturnedStat(joinPc.getId(), joinPc.getClan().getEmblemId()));
					player.broadcastPacket(new S_ReturnedStat(player.getId(), joinPc.getClan().getEmblemId()));
				}
				//L1Teleport.teleport(joinPc, joinPc.getX(), joinPc.getY(), joinPc.getMapId(),joinPc.getHeading(), false);
				// 혈맹에
				// 가입했습니다.
			} else { // 크란 가입이 끝난 상태(크란 연합)
				if (Config.CLAN_ALLIANCE) {
					changeClan(pc, joinPc, maxMember);
				} else {
					joinPc.sendPackets(new S_ServerMessage(89)); 
					// \f1당신은벌써혈맹에가입하고있습니다.
				}
			}
		} else {
			return false;
		}
		return true;
	}


	private void changeClan(L1PcInstance pc, L1PcInstance joinPc, int maxMember) {
		int clanId = pc.getClanid();
		String clanName = pc.getClanname();
		L1Clan clan = L1World.getInstance().getClan(clanName);
		int clanNum = clan.getClanMemberList().size();

		int oldClanId = joinPc.getClanid();
		String oldClanName = joinPc.getClanname();
		L1Clan oldClan = L1World.getInstance().getClan(oldClanName);
		int oldClanNum = oldClan.getClanMemberList().size();
		if (clan != null && oldClan != null && joinPc.isCrown()
				&& joinPc.getId() == oldClan.getLeaderId()) {
			if (maxMember < clanNum + oldClanNum) { // 빈 곳이 없다
				joinPc.sendPackets(new S_ServerMessage(188, pc.getName())); 
				// %0는당신을혈맹원으로서받아들일수가없습니다.
				return;
			}
			L1PcInstance clanMember[] = clan.getOnlineClanMember();
			for (int cnt = 0; cnt < clanMember.length; cnt++) {
				clanMember[cnt].sendPackets(new S_ServerMessage(94, joinPc.getName())); 
				// \f1%0이혈맹의일원으로서받아들여졌습니다.
			}


			for (int i = 0; i < oldClan.getClanMemberList().size(); i++) {
				L1PcInstance oldClanMember = L1World.getInstance().getPlayer(
						oldClan.getClanMemberList().get(i).name);
				if (oldClanMember != null) { // 온라인중의 구크란 멤버
					oldClanMember.setClanid(clanId);
					oldClanMember.setClanname(clanName);
					if (oldClanMember.getId() == joinPc.getId()) {
						oldClanMember.setClanRank(L1Clan.수호);
					} else
						oldClanMember.setClanRank(L1Clan.수련);
					try {
						// DB에 캐릭터 정보를 기입한다
						oldClanMember.save();
					} catch (Exception e) {
						_log.log(Level.SEVERE, "C_Attr[changeClan]Error", e);
					}
					clan.addClanMember(oldClanMember.getName(), oldClanMember.getClanRank(), oldClanMember.getLevel(),
							oldClanMember.getClanMemberNotes(), oldClanMember.getId(), oldClanMember.getType(), oldClanMember.getOnlineStatus(), oldClanMember);


					oldClanMember.sendPackets(new S_ServerMessage(95, clanName));
					oldClanMember.sendPackets(new S_ClanName(oldClanMember, clan.getEmblemId(), oldClanMember.getClanRank()));	
					oldClanMember.sendPackets(new S_ReturnedStat(oldClanMember.getId(), clan.getClanId()));
					oldClanMember.sendPackets(new S_PacketBox(S_PacketBox.PLEDGE_EMBLEM_STATUS, pc.getClan().getEmblemStatus()));
					//oldClanMember.sendPackets(new S_ClanAttention());
					for(L1PcInstance player : clan.getOnlineClanMember()){
						player.sendPackets(new S_ReturnedStat(oldClanMember.getId(), oldClanMember.getClan().getEmblemId()));
						player.broadcastPacket(new S_ReturnedStat(player.getId(), oldClanMember.getClan().getEmblemId()));
					}
					
							 // \f1%0
					
					// 혈맹에
					// 가입했습니다.
				} else { // 오프 라인중의 구크란 멤버
					try {
						L1PcInstance offClanMember = CharacterTable.getInstance().restoreCharacter(
								oldClan.getClanMemberList().get(i).name);
						offClanMember.setClanid(clanId);
						offClanMember.setClanname(clanName);
						offClanMember.setClanRank(L1Clan.수련);
						offClanMember.save();
						clan.addClanMember(offClanMember.getName(), offClanMember.getClanRank(), offClanMember.getLevel(), 
								offClanMember.getClanMemberNotes(), offClanMember.getId(), offClanMember.getType(), offClanMember.getOnlineStatus(), offClanMember);
					} catch (Exception e) {
						_log.log(Level.SEVERE, "C_Attr[changeClan]Error", e);
					}
				}
			}
			// 이전혈맹 삭제
			String emblem_file = String.valueOf(oldClanId);
			File file = new File("emblem/" + emblem_file);
			file.delete();
			ClanTable.getInstance().deleteClan(oldClanName);
		}
	}
	// 오프라인중의 군주 카리스마
	///////////혈맹리뉴얼//////////////
	public int getOfflineClanLeaderCha(int member) {
		java.sql.Connection con = null;
		java.sql.PreparedStatement pstm = null;
		java.sql.ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT Cha FROM characters WHERE objid=?");
			pstm.setInt(1, member);
			rs = pstm.executeQuery();
			if(!rs.next()) return 0;

			return rs.getInt("Cha");

		} catch (SQLException e) {
			_log.warning("could not check existing charname:" + e.getMessage());
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return 0;
	}

}