package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;
import l1j.server.server.model.L1Buddy;
import l1j.server.server.model.L1World;

public class S_Buddy extends ServerBasePacket {
    private static final String _S_Buddy = "[S] _S_Buddy";

    private byte[] _byte = null;

    public S_Buddy(L1Buddy buddy) {
        writeC(Opcodes.S_EXTENDED_PROTOBUF);
        writeD(0x01080151);
        for (String bu : buddy.getBuddy().values()) {
            writeC(0x12);
            writeC(bu.getBytes().length + 6);
            writeC(0x0a);
            writeC(bu.getBytes().length);
            writeByte(bu.getBytes());
            writeC(0x10);
            writeC(L1World.getInstance().getPlayer(bu) != null ? 1 : 0);
            writeH(0x001a);
        }
        writeH(0);
    }

    @Override
    public byte[] getContent() {
        if (_byte == null) {
            _byte = _bao.toByteArray();
        }
        return _byte;
    }

    @Override
    public String getType() {
        return _S_Buddy;
    }
}
