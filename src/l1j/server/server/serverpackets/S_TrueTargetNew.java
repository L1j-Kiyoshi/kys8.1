package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;

public class S_TrueTargetNew extends ServerBasePacket {

	private static final String S_TRUETARGETNEW = "[S] S_TrueTargetNew";
	private byte[] _byte = null;

	public S_TrueTargetNew(int targetId, boolean active) {
		buildPacket(targetId, active);
	}
	private void buildPacket(int targetId, boolean active) {
		writeC(Opcodes.S_EVENT);
		writeC(0xc2);
		writeD(targetId);
		writeC(0x4f);
		writeC(0x33);
		writeC(0x00);
		writeC(0x00);
		writeD(active ? 1 : 0);
		writeH(0x00);
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
		return S_TRUETARGETNEW;
	}

}
