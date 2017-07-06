
package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;

public class S_Horun extends ServerBasePacket {
	public S_Horun(int o, L1PcInstance pc) {
		writeC(Opcodes.S_EXCHANGEABLE_SPELL_LIST);
		writeC(0x08);
		writeC(0);
		writeC(0);
		writeC(0);
		writeC(0);
		writeC(0);
		writeC(0x01);
		writeC(0);
		writeC(0);
		writeC(0);
		writeC(0x02);
		writeC(0);
		writeC(0);
		writeC(0);
		writeC(0x03);
		writeC(0);
		writeC(0);
		writeC(0);
		writeC(0x04);
		writeC(0);
		writeC(0);
		writeC(0);
		writeC(0x05);
		writeC(0);
		writeC(0);
		writeC(0);
		writeC(0x06);
		writeC(0);
		writeC(0);
		writeC(0);
		writeC(0x07);
		writeC(0);
		writeC(0);
		writeC(0);
		writeC(0);
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}
	@Override
	public String getType() {
		return _S__1B_HORUN;
	}

	private static final String _S__1B_HORUN = "[S] S_Horun";
}
