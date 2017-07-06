package l1j.server.server.serverpackets;

import java.util.StringTokenizer;

import l1j.server.server.Opcodes;

public class S_MatizBuff extends ServerBasePacket {
	private static final String _TYPE = "[S] S_MatizBuff";
	
	private byte[] _byte = null;
	
	public S_MatizBuff(int type,long time){
		writeC(Opcodes.S_EXTENDED_PROTOBUF);
		writeH(110);
		writeC(0x08);
		writeC(0x01);
		writeC(0x10);
		writeC(135+type);
		
		writeC(0x19);
		writeC(0x18);
		writeBit(time);
		writeC(0x20);
		writeC(0x08);
		writeC(0x28);
		writeC(195+type);
		writeC(0x3f);
		writeH(48);
		writeC(0x38);
		writeC(15+type);
		writeC(0x40);
		writeC(130+type);
		writeC(0x27);
		writeC(0x48);
		writeC(0);
		writeC(0x50);
		writeC(0);
		writeC(0x58);
		writeC(0x01);
		writeH(0x60);
		writeH(0x68);
		writeH(0x70);
		switch(type){
			case 1:
				writeH(0x62);
				break;
			case 2:
				writeC(0x23);
				writeC(0x15);
				break;
			case 3:
				writeC(0xcb);
				writeC(0x6d);
				break;
		}
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
		return _TYPE;
	}
}
