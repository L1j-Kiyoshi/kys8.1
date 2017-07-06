/*package l1j.server.server.serverpackets;
import l1j.server.server.Opcodes;

public class S_ChainSword extends ServerBasePacket {
	private byte[] _byte;
	public S_ChainSword(int value) {
		writeC(Opcodes.S_EVENT);
		writeC(0x4b);
		writeC(value);
	}
	
	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}
}
*/