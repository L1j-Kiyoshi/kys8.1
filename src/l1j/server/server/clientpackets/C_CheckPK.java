package l1j.server.server.clientpackets;

import l1j.server.server.GameClient;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;

public class C_CheckPK extends ClientBasePacket {

    private static final String C_CHECK_PK = "[C] C_CheckPK";

    public C_CheckPK(byte abyte0[], GameClient clientthread) throws Exception {
        super(abyte0);

        L1PcInstance player = clientthread.getActiveChar();
        if (player == null)
            return;
        player.sendPackets(new S_ServerMessage(562, String.valueOf(player.get_PKcount()))); //現在の
        // PK回数は％0です。
    }

    @Override
    public String getType() {
        return C_CHECK_PK;
    }

}
