package l1j.server.server.clientpackets;

import java.io.File;

import l1j.server.server.GameClient;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.ClanTable;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1ClanMatching;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1War;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.ServerMessage;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_LeaveClan extends ClientBasePacket {

	private static final String C_LEAVE_CLAN = "[C] C_LeaveClan";

	public C_LeaveClan(byte abyte0[], GameClient clientthread)
			throws Exception {
		super(abyte0);

		L1PcInstance player = clientthread.getActiveChar();
		if (player == null) {
			return;
		}
		int clan_id = player.getClanid();

		if (clan_id == 0)
			return;

		L1Clan clan = L1World.getInstance().getClan(player.getClanname());
		if (clan == null)
			return;
		// その血の君主か？
		if (player.isCrown() && player.getId() == clan.getLeaderId()) {
			L1ClanMatching Clan = L1ClanMatching.getInstance();
			Clan.deleteClanMatching(player);
			leaveClanBoss(clan, player);
		} else { // 君主ではなく、血盟員の脱退
			leaveClanMember(clan, player);
		}
	}

	private void leaveClanBoss(L1Clan clan, L1PcInstance player) throws Exception {
		String player_name = player.getName();
		String clan_name = player.getClanname();

		if (clan.getCastleId() > 0 || clan.getHouseId() > 0) {
			player.sendPackets(new S_ServerMessage(	ServerMessage.HAVING_NEST_OF_CLAN));
			return;
		}

		for (L1War war : L1World.getInstance().getWarList()) {
			if (war.CheckClanInWar(clan_name)) {
				player.sendPackets(new S_ServerMessage(	ServerMessage.CANNOT_BREAK_CLAN));
				return;
			}
		}

		if (clan.getAlliance() > 0) {
			player.sendPackets(new S_ServerMessage(ServerMessage.CANNOT_BREAK_CLAN_HAVING_FRIENDS));
			return;
		}

		L1PcInstance pc = null;
		for (int i = 0; i < clan.getClanMemberList().size(); i++) { 
			// 血盟員の血盟情報を初期化
			pc = L1World.getInstance().getPlayer(clan.getClanMemberList().get(i).name);
			
			if (pc == null) {
				// 血盟員がオフラインの場合
				pc = CharacterTable.getInstance().restoreCharacter(clan.getClanMemberList().get(i).name);
			} else {
				// %1血盟の君主％0が血盟を解散しました。
				pc.sendPackets(new S_ServerMessage(269, player_name, clan_name));
			}
			pc.ClearPlayerClanData(clan);
		}

		String emblem_file = String.valueOf(player.getClanid());
		File file = new File("emblem/" + emblem_file);
		file.delete();
		ClanTable.getInstance().deleteClan(clan_name);
	}

	private void leaveClanMember(L1Clan clan, L1PcInstance player) throws Exception {
		String player_name = player.getName();
		String clan_name = player.getClanname();
		L1PcInstance clanMember[] = clan.getOnlineClanMember();

		for (int i = 0; i < clanMember.length; i++) {
			clanMember[i].sendPackets(new S_ServerMessage(178, player_name, clan_name)); //\\ f1％0％1血盟を脱退しました。
			// \f1%0この％1血盟を脱退しました。
		}
		if (player.isClanBuff()) {
			player.killSkillEffectTimer(L1SkillId.CLANBUFF_YES);
			player.sendPackets(new S_PacketBox(S_PacketBox.UNLIMITED_ICON1, 450, false));
			player.setClanBuff(false);
		}
		player.ClearPlayerClanData(clan);
		clan.removeClanMember(player_name);
		new L1Teleport().teleport(player, player.getX(), player.getY(), player.getMapId(), player.getHeading(), false);
	}

	@Override
	public String getType() {
		return C_LEAVE_CLAN;
	}
}