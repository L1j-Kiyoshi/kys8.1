package l1j.server.server.clientpackets;

import l1j.server.server.GameClient;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Emblem;

public class C_Clan extends ClientBasePacket {

    private static final String C_Clan = "[C] C_Clan";

    public C_Clan(byte abyte0[], GameClient clientthread) {
        super(abyte0);

        int emblemId = readD();

        L1PcInstance pc = clientthread.getActiveChar();
        if (pc == null) {
            return;
        }
        pc.sendPackets(new S_Emblem(emblemId));
    }

    @Override
    public String getType() {
        return C_Clan;
    }
}