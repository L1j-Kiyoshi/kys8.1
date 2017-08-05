package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;

public class S_Unknown1 extends ServerBasePacket {
    public S_Unknown1(L1PcInstance pc) {
        writeC(Opcodes.S_ENTER_WORLD_CHECK);
        writeC(0x03);
        if (pc.getClanid() > 0) {
            writeD(pc.getId());
        } else {
            writeC(0x53);
            writeC(0x01);
            writeC(0x00);
            writeC(0x8b);
        }
        writeC(0x9c);
        writeC(0x1f);
    }

    @Override
    public byte[] getContent() {
        return getBytes();
    }
}
