package l1j.server.server.clientpackets;

import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.GameClient;
import l1j.server.server.Controller.WarTimeController;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;

public class C_BanClan extends ClientBasePacket {

	private static final String C_BAN_CLAN = "[C] C_BanClan";
	private static Logger _log = Logger.getLogger(C_BanClan.class.getName());

	public C_BanClan(byte abyte0[], GameClient clientthread) throws Exception {
		super(abyte0);

		String s = readS();
		if ((s == null) || (s.equals("")))
			return;

		L1PcInstance pc = clientthread.getActiveChar();
		if (pc == null) {
			return;
		}
		L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
		if (clan != null) {
			int i;
			if (pc.isCrown() && pc.getId() == clan.getLeaderId()) { // 군주, 한편,
				// 혈맹주
				for (i = 0; i < clan.getClanMemberList().size(); i++) {
					if (pc.getName().toLowerCase().equals(s.toLowerCase())) { // 군주
						// 자신
						return;
					}
				}
				int castle_id = clan.getCastleId();
				if (castle_id != 0 && WarTimeController.getInstance().isNowWar(castle_id)) {
					pc.sendPackets(new S_ServerMessage(439));
					return;
				}
				L1PcInstance tempPc = L1World.getInstance().getPlayer(s);
				if (tempPc != null) { // 온라인중
					if (tempPc.getClanid() == pc.getClanid()) { // 같은 크란
						tempPc.ClearPlayerClanData(clan);
						clan.removeClanMember(tempPc.getName());
						pc.sendPackets(new S_PacketBox(pc, S_PacketBox.PLEDGE_REFRESH_MINUS));
						tempPc.sendPackets(new S_ServerMessage(238, pc.getClanname()));
						// 당신은%0혈맹으로부터추방되었습니다.
						pc.sendPackets(new S_ServerMessage(240, tempPc.getName())); // %0가
						// %0가당신의 혈맹으로부터추방되었습니다.
					} else {
						pc.sendPackets(new S_ServerMessage(109, s));
						// %0라는이름의사람은없습니다.
					}
				} else { // 오프 라인중
					try {
						L1PcInstance restorePc = CharacterTable.getInstance().restoreCharacter(s);
						if (restorePc != null && restorePc.getClanid() == pc.getClanid()) {
							// 같은 혈맹
							restorePc.ClearPlayerClanData(clan);
							clan.removeClanMember(restorePc.getName());
							pc.sendPackets(new S_ServerMessage(240, restorePc.getName()));
							// %0가당신의 혈맹으로부터추방되었습니다.
						} else {
							pc.sendPackets(new S_ServerMessage(109, s));
							// %0라는이름의사람은없습니다.
						}
					} catch (Exception e) {
						_log.log(Level.SEVERE, "C_BanClan[]Error", e);
					}
				}
			} else {
				pc.sendPackets(new S_ServerMessage(518)); // 이 명령은 혈맹의 군주만 이용할
				// 수 있습니다.
			}
		}
	}

	@Override
	public String getType() {
		return C_BAN_CLAN;
	}
}
