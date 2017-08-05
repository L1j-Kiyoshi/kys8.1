package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;

public class S_Fishing2 extends ServerBasePacket {
    private final int FishUI = 63;

    private final int CAUI = 76;

    public S_Fishing2(int t) {
        writeC(Opcodes.S_EXTENDED_PROTOBUF);
        writeH(FishUI);
        writeH(264);
        writeC(16);
        writeC(t);
        writeH(0);
    }

    public S_Fishing2(int type, int test, int time, String CName, boolean swich) {
        writeC(Opcodes.S_EXTENDED_PROTOBUF);
        writeH(CAUI);
        writeH(264);
        writeC(16);
        if (swich) {
            writeH(test);
            writeH(time);
            writeS(CName);
        } else {
            writeC(0);
        }
        writeH(0);
    }

    public byte[] getContent() {
        return getBytes1();
    }
}
