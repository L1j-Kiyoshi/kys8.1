package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;
import l1j.server.server.model.L1Character;

public class S_HPMeter extends ServerBasePacket {
	private static final String _typeString = "[S] S_HPMeter";

	private byte[] _byte = null;

	public S_HPMeter(int objId, int hpRatio, int mpRatio) {
		buildPacket(objId, hpRatio, mpRatio);
	}

	public S_HPMeter(L1Character cha) {
		int objId = cha.getId();
		int hpRatio = 100;
		int mpRatio = 100;
		if (0 < cha.getMaxHp())
			hpRatio = 100 * cha.getCurrentHp() / cha.getMaxHp();
		if (0 < cha.getMaxMp())
			mpRatio = 100 * cha.getCurrentMp() / cha.getMaxMp();

		buildPacket(objId, hpRatio, mpRatio);
	}

	private void buildPacket(int objId, int hpRatio, int mpRatio) {
		// 43 04 5d 91 05 00 00 93 1b
		writeC(Opcodes.S_HIT_RATIO);
		writeD(objId);
		writeC(hpRatio);
		writeC(mpRatio);
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
		return _typeString;
	}
}
