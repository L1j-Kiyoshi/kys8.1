package l1j.server.server.clientpackets;

import java.util.logging.Logger;

import l1j.server.server.GameClient;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ChangeHeading;

public class C_ChangeHeading extends ClientBasePacket {
    private static final String C_CHANGE_HEADING = "[C] C_ChangeHeading";
    private static Logger _log = Logger.getLogger(C_ChangeHeading.class.getName());

    public C_ChangeHeading(byte[] decrypt, GameClient client) {
        super(decrypt);
        int heading = readC();
        if ((heading < 0) || (heading > 7))
            return;

        L1PcInstance pc = client.getActiveChar();
        if (pc == null)
            return;

        pc.setHeading(heading);

        _log.finest("Change Heading : " + pc.getHeading());

        if (!pc.isGmInvis() && !pc.isGhost() && !pc.isInvisble()) {
            pc.broadcastPacket(new S_ChangeHeading(pc));
        }
    }

    @Override
    public String getType() {
        return C_CHANGE_HEADING;
    }
}