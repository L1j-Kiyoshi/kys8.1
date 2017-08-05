package l1j.server.server.clientpackets;

import l1j.server.server.GameClient;
import l1j.server.server.utils.Teleportation;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_Teleport extends ClientBasePacket {

    private static final String C_TELEPORT = "[C] C_Teleport";

    public C_Teleport(byte abyte0[], GameClient clientthread) throws Exception {

        super(abyte0);
        //	Teleportation.Teleportation(clientthread.getActiveChar());
        new Teleportation().doTeleportation(clientthread.getActiveChar());

    }

    @Override
    public String getType() {
        return C_TELEPORT;
    }
}