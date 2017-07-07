package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;

public class S_TamBuff extends ServerBasePacket {

    private static final String S_TamBuff = "[S] S_TamBuff";
    private byte[] _byte = null;

    public S_TamBuff(int id, int time, int type) {
		writeC(Opcodes.S_EXTENDED_PROTOBUF);
		writeH(0x6e);
		writeC(0x08);
		writeC(2);
		writeC(0x10);
		write7B(id);
		writeC(0x18);
		write7B(time);
		writeC(0x20);
		writeC(8);
		writeC(0x28);
		write7B(type == 1 ? 6100 : type == 2 ? 6546 : 6547); 
		writeC(0x30);
		writeC(0);
		writeC(0x38);
		writeC(1);
		writeC(0x40);
		write7B(type + 4180);// string-2(与える)
		writeC(0x48);
		writeH(0x20d5);
		writeC(0x50);
		writeC(0);
		writeC(0x58);
		writeC(1);
		writeH(0);
	}

    @Override
    public byte[] getContent() {
        if (_byte == null) {
            _byte = getBytes();
        }
        return _byte;
    }

    @Override
    public String getType() {
        return S_TamBuff;
    }
}
