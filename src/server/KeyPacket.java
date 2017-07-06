package server;

import l1j.server.server.serverpackets.ServerBasePacket;

public class KeyPacket extends ServerBasePacket {
    private byte[] _byte = null;

    public KeyPacket() {
	// byte[] _byte1 = {
	// (byte) 0x4F,
	// (byte) 0x7E, (byte) 0x64, (byte) 0x95, (byte) 0x77,
	// (byte) 0x93, (byte) 0x24, (byte) 0x1F, (byte) 0x39};
	byte[] _byte1 = { (byte) 0xe7, (byte) 0x5d, (byte) 0xa4, (byte) 0x84, (byte) 0x1f, (byte) 0x85, (byte) 0x4a,
		(byte) 0x58, (byte) 0x5e }; // 170705

	for (int i = 0; i < _byte1.length; i++) {
	    writeC(_byte1[i]);
	}
    }

    @Override
    public byte[] getContent() {
	if (_byte == null) {
	    _byte = getBytes();
	}
	return _byte;
    }
}