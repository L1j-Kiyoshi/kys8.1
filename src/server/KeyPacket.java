package server;

import l1j.server.server.serverpackets.ServerBasePacket;

public class KeyPacket extends ServerBasePacket {
    private byte[] _byte = null;

    public KeyPacket() {
	// byte[] _byte1 = {
	// (byte) 0x4F,
	// (byte) 0x7E, (byte) 0x64, (byte) 0x95, (byte) 0x77,
	// (byte) 0x93, (byte) 0x24, (byte) 0x1F, (byte) 0x39};

	// byte[] _byte1 = { (byte) 0xe7, (byte) 0x5d, (byte) 0xa4, (byte) 0x84,
	// (byte) 0x1f, (byte) 0x85, (byte) 0x4a,
	// (byte) 0x58, (byte) 0x5e }; // 170705 8.xc?

	// byte[] _byte1 = { (byte) 0xcc, (byte) 0x73, (byte) 0x3a, (byte) 0x0d,
	// (byte) 0x01, (byte) 0x8e, (byte) 0xe0,
	// (byte) 0x12, (byte) 0x06 }; // 170705 7.6c

	byte[] _byte1 = { (byte) 0x08, (byte) 0xd9, (byte) 0xf5, (byte) 0xbc, (byte) 0x54, }; // 8.1c?

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