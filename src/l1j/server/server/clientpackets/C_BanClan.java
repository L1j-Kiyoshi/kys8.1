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
            if (pc.isCrown() && pc.getId() == clan.getLeaderId()) { // 君主、一方、
                // 血盟主
                for (i = 0; i < clan.getClanMemberList().size(); i++) {
                    if (pc.getName().toLowerCase().equals(s.toLowerCase())) { // 君主
                        //自分
                        return;
                    }
                }
                int castle_id = clan.getCastleId();
                if (castle_id != 0 && WarTimeController.getInstance().isNowWar(castle_id)) {
                    pc.sendPackets(new S_ServerMessage(439));
                    return;
                }
                L1PcInstance tempPc = L1World.getInstance().getPlayer(s);
                if (tempPc != null) { // オンライン中
                    if (tempPc.getClanid() == pc.getClanid()) { // 同じクラン
                        tempPc.ClearPlayerClanData(clan);
                        clan.removeClanMember(tempPc.getName());
                        pc.sendPackets(new S_PacketBox(pc, S_PacketBox.PLEDGE_REFRESH_MINUS));
                        tempPc.sendPackets(new S_ServerMessage(238, pc.getClanname()));
                        // あなたは％0血盟から追放されました。
                        pc.sendPackets(new S_ServerMessage(240, tempPc.getName())); // ％0が
                        // %0があなたの血盟から追放されました。
                    } else {
                        pc.sendPackets(new S_ServerMessage(109, s));
                        // %0という名前の人はありません。
                    }
                } else { //オフライン中
                    try {
                        L1PcInstance restorePc = CharacterTable.getInstance().restoreCharacter(s);
                        if (restorePc != null && restorePc.getClanid() == pc.getClanid()) {
                            //同じ血盟
                            restorePc.ClearPlayerClanData(clan);
                            clan.removeClanMember(restorePc.getName());
                            pc.sendPackets(new S_ServerMessage(240, restorePc.getName()));
                            // %0があなたの血盟から追放されました。
                        } else {
                            pc.sendPackets(new S_ServerMessage(109, s));
                            // %0という名前の人はありません。
                        }
                    } catch (Exception e) {
                        _log.log(Level.SEVERE, "C_BanClan[]Error", e);
                    }
                }
            } else {
                pc.sendPackets(new S_ServerMessage(518)); //このコマンドは、血盟の君主のみ利用
                //することができます。
            }
        }
    }

    @Override
    public String getType() {
        return C_BAN_CLAN;
    }
}
