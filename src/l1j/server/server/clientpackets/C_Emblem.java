package l1j.server.server.clientpackets;

import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.GameClient;
import l1j.server.server.IdFactory;
import l1j.server.server.datatables.ClanTable;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ReturnedStat;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_Emblem extends ClientBasePacket {

    private static final String C_EMBLEM = "[C] C_Emblem";
    private static Logger _log = Logger.getLogger(C_Emblem.class.getName());

    public C_Emblem(byte abyte0[], GameClient clientthread)
            throws Exception {
        super(abyte0);

        L1PcInstance player = clientthread.getActiveChar();
        if (player == null) {
            return;
        } else if (player.getClanRank() != 4 && player.getClanRank() != 10) {
            return;
        }
        if (player.getClanid() != 0) {
            int newEmblemdId = IdFactory.getInstance().nextId();
            String emblem_file = String.valueOf(newEmblemdId);

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream("emblem/" + emblem_file);
                for (short cnt = 0; cnt < 384; cnt++) {
                    fos.write(readC());
                }
            } catch (Exception e) {
                _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
                throw e;
            } finally {
                if (null != fos) {
                    fos.close();
                }
                fos = null;
            }
            //player.sendPackets(new S_Emblem(player.getClanid()));
            L1Clan clan = ClanTable.getInstance().getTemplate(player.getClanid());
            clan.setEmblemId(newEmblemdId);
            ClanTable.getInstance().updateClan(clan);
            for (L1PcInstance pc : clan.getOnlineClanMember()) {
                pc.sendPackets(new S_ReturnedStat(pc.getId(), newEmblemdId));
                pc.broadcastPacket(new S_ReturnedStat(pc.getId(), newEmblemdId));
            }
//			L1World.getInstance().broadcastPacketToAll(new S_Emblem(player.getClanid()));
        }
    }

    @Override
    public String getType() {
        return C_EMBLEM;
    }
}
