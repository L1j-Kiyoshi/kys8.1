package l1j.server.server.clientpackets;

import java.util.Random;

import l1j.server.GameSystem.Robot.L1RobotInstance;
import l1j.server.server.GameClient;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1ClanJoin;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.FaceToFace;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_JoinClan extends ClientBasePacket {
	

	private static final String C_JOIN_CLAN = "[C] C_JoinClan";

	public C_JoinClan(byte abyte0[], GameClient clientthread)
			throws Exception {
		super(abyte0);

		L1PcInstance pc = clientthread.getActiveChar();
		if (pc == null || pc.isGhost() ) {
			return;
		}

		L1PcInstance target = FaceToFace.faceToFace(pc);
		if (target != null) {
			JoinClan(pc, target);
		}
	}

	private void JoinClan(L1PcInstance player, L1PcInstance target) {
		if (!target.isCrown() && (target.getClanRank() != L1Clan.GUARDIAN)) {
			player.sendPackets(new S_SystemMessage(target.getName() + "は王子や王女守護記事がありません。"));
			return;
		}

		int clan_id = target.getClanid();
		String clan_name = target.getClanname();
		if (clan_id == 0) { // 相手クランがない
			player.sendPackets(new S_ServerMessage(90, target.getName())); // \f1%0は血盟を創設していない状態です。
			return;
		}

		L1Clan clan = L1World.getInstance().getClan(clan_name);
		if (clan == null) {
			return;
		}

		if (target.getClanRank() != L1Clan.MONARCH && target.getClanRank() != L1Clan.GUARDIAN) { 
			player.sendPackets(new S_SystemMessage(target.getName() + "は王子や王女守護記事がありません。"));
			return;
		}

		if (player.getClanid() != 0) { // すでにクランに加入済み
			if (player.isCrown()) { // 自分が君主
				String player_clan_name = player.getClanname();
				L1Clan player_clan = L1World.getInstance().getClan(
						player_clan_name);
				if (player_clan == null) {
					return;
				}

				if (player.getId() != player_clan.getLeaderId()) { // 自分が血盟主以外
					player.sendPackets(new S_ServerMessage(89)); // \f1あなたはすでに血盟に加入しています。
					return;
				}

				if (player_clan.getCastleId() != 0 || //自分が城主・アジト保有
						player_clan.getHouseId() != 0) {
					player.sendPackets(new S_ServerMessage(665)); // \f1性やアジトを所有した状態で血盟を解散することができません。
					return;
				}
			} else {
				player.sendPackets(new S_ServerMessage(89)); // \f1あなたはすでに血盟に加入しています。
				return;
			}
		}
		if(target.isAutoClanjoin()){
			L1ClanJoin.getInstance().ClanJoin(target, player);
		}else{
			target.setTempID(player.getId()); // 相手のオブジェクトIDを保存しておく
			if (target instanceof L1RobotInstance) {
				if (500 <= clan.getClanMemberList().size()) {// clanMembersName.length)
																// { // 空きがない
					player.sendPackets(new S_ServerMessage(188, target.getName())); // %0は
																					// あなたの
																					// 血盟員として
																					//受け入れる
																					// 数
																					// ありません。
					return;
				}
				Random _rnd = new Random(System.nanoTime());
				L1ClanJoin.getInstance().ClanJoin(target, player);
			}else{
			target.sendPackets(new S_Message_YN(97, player.getName())); // %0が血盟に加入したが、はあります。承諾しますか？ （Y / N）
			}
		}
	}


	@Override
	public String getType() {
		return C_JOIN_CLAN;
	}
}
