/*package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;

public class S_Einhasad extends ServerBasePacket {

	private static final String S_Einhasad = "[S] S_Einhasad";

	private byte[] _byte = null;

	public S_Einhasad(int i){
		writeC(Opcodes.S_PACKETBOX);
		writeC(0x52);
		writeC(i);
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	public String getType() {
		return S_Einhasad;
	}
}
*/