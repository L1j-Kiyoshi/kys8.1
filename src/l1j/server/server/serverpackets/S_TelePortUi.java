package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;

public class S_TelePortUi extends ServerBasePacket {
    private static final String S_TelePortUi = "[S] S_TelePortUi";

    private byte[] _byte = null;

    public S_TelePortUi(int objid, String[] action, int[] price, int map) {
        writeC(Opcodes.S_EXTENDED_PROTOBUF);
        writeH(0x0243);
        writeC(0x08);
        writeBit(objid);
        for (int i = 0; i < map; i++) {
            int length = bitlengh(price[i]) + 5;
            int totallen = length + action[i].getBytes().length + 4;
            writeC(0x12);
            writeBit(totallen);
            writeC(0x0a);
            writeS2(action[i]);
            writeC(0x12);
            writeBit(length);
            writeH(0x0708);
            writeC(0x10);
            writeBit(price[i]);
            writeH(0x0118);
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
        return S_TelePortUi;
    }
}
