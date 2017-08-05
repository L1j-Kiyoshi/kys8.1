package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;

public class S_MapID extends ServerBasePacket {

    public S_MapID(int mapid, boolean isUnderwater) {
        writeC(Opcodes.S_EXTENDED_PROTOBUF);
        writeC(118);
        writeC(0);
        writeC(8);
        if (mapid > 6000 && mapid < 6499) {
            write4bit(1005);
        } // ヒット
        else if (mapid > 6501 && mapid < 6999) {
            write4bit(1011);
        } // パプ
        else if (mapid > 1017 && mapid < 1023) {
            write4bit(1017);
        } else if (mapid > 9000 && mapid < 9099) {
            write4bit(9000);
        } // TI
        else if (mapid > 2101 && mapid < 2151) {
            write4bit(2101);
        } // オルニョ
        else if (mapid > 2151 && mapid < 2201) {
            write4bit(2151);
        } // デーモン
        else if (mapid > 2699 && mapid < 2798) {
            write4bit(2699);
        } else if (mapid > 2600 && mapid < 2698) {
            write4bit(2600);
        } else {
            write4bit(mapid);
        }
        writeC(0x10);
        writeC(0x0);
        writeC(0x18);
        writeC(isUnderwater ? 1 : 0);
        writeC(0x20);
        writeC(0);
        writeC(0x28);
        writeC(0x00);
        writeC(0x30);
        writeC(0x00);
        writeH(0);
        // writeC(Opcodes.S_EXTENDED_PROTOBUF);
        // writeH(mapid);
        // writeC(isUnderwater ? 1 : 0);
        // writeC(isUnderwater ? 1 : 0);
    }

    @Override
    public byte[] getContent() {
        return getBytes();
    }
}
