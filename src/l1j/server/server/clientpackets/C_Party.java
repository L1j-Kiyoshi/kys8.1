package l1j.server.server.clientpackets;

import l1j.server.server.GameClient;
import l1j.server.server.model.L1Party;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Party;
import l1j.server.server.serverpackets.S_ServerMessage;

public class C_Party extends ClientBasePacket {

    private static final String C_PARTY = "[C] C_Party";

    public C_Party(byte abyte0[], GameClient clientthread) {
        super(abyte0);
        L1PcInstance pc = clientthread.getActiveChar();
        if (pc == null || pc.isGhost()) {
            return;
        }
        L1Party party = pc.getParty();
        if (pc.isInParty()) {
            pc.sendPackets(new S_Party("party", pc.getId(), party.getLeader().getName(), party.getMembersNameList()));
        } else {
            pc.sendPackets(new S_ServerMessage(425));
        }
    }

    @Override
    public String getType() {
        return C_PARTY;
    }

}
