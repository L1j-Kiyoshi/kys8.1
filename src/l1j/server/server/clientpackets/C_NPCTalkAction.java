package l1j.server.server.clientpackets;

import java.io.FileNotFoundException;
import java.util.logging.Logger;

import l1j.server.server.GameClient;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;

public class C_NPCTalkAction extends ClientBasePacket {

    private static final String C_NPC_TALK_ACTION = "[C] C_NPCTalkAction";
    private static Logger _log = Logger.getLogger(C_NPCTalkAction.class
            .getName());

    public C_NPCTalkAction(byte decrypt[], GameClient client)
            throws FileNotFoundException, Exception {
        super(decrypt);
        int objectId = readD();
        String action = readS();
        L1PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) return;

        L1Object obj = L1World.getInstance().findObject(objectId);
        if (obj == null) {
            _log.warning("object not found, oid " + objectId);
            return;
        }

        try {
            L1NpcInstance npc = (L1NpcInstance) obj;
            npc.onFinalAction(activeChar, action);
        } catch (ClassCastException e) {
        }
    }

    @Override
    public String getType() {
        return C_NPC_TALK_ACTION;
    }

}
