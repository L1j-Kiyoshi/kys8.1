package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;

public class S_ElfIcon extends ServerBasePacket {

	public S_ElfIcon(int a, int b, int c, int d) {
		writeC(Opcodes.S_EVENT);
		writeC(0x15);
		writeC(a);
		writeC(b);
		writeC(c);
		writeC(d);
		writeC(0);
		writeC(0);
		
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}
}
