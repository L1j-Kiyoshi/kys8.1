package l1j.server.server.serverpackets;

import java.io.UnsupportedEncodingException;

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;

public class S_NewChat extends ServerBasePacket {

	private static final String S_NewChat = "[S] S_NewChat";
	private byte[] _byte = null;
	
	public S_NewChat(L1PcInstance pc, int type, int chat_type, String chat_text, String target_name) {
		writeC(Opcodes.S_EXTENDED_PROTOBUF);
		
		switch(type) {
		case 3:  
			writeC(0x03);
			break;
		case 4: 
			writeC(0x04);
			break;
		}
		
		writeC(0x02);
		writeC(0x08);
		writeC(0x00); 
		writeC(0x10);
		writeC(chat_type);  
		
		writeC(0x1a);
		byte[] text_byte = chat_text.getBytes();
		writeC(text_byte.length);  
		writeByte(text_byte); 
		switch(type) {
		case 3:
			writeC(0x22);
			
			if (chat_type == 0) {  
				writeC(0x00);
				writeC(0x28);
				writeC(0x00);
				writeC(0x30);
				writeC(0x18);
			} else if (chat_type == 1) { 
				byte[] name_byte = target_name.getBytes();
				writeC(name_byte.length);
				writeByte(name_byte);
				writeC(0x28);
				writeC(0x00);
				writeC(0x30);
				writeC(0x00);
				writeH(0);
			}
			break;
		case 4:
			writeC(0x2a);
			try {
				if (pc.isGm() && chat_type == 3) {
					byte[] name = "******".getBytes("MS949");
					writeC(name.length);
					writeByte(name);
				} else if (pc.getAge() != 0 && chat_type == 4) {
					String names = pc.getName() + "(" + pc.getAge() + ")";
					byte[] name = names.getBytes("MS949");
					writeC(name.length);
					writeByte(name);
				} else {
					byte[] name = pc.getName().getBytes("MS949");
					writeC(name.length);
					writeByte(name);
				}
			} catch (UnsupportedEncodingException e) {
			}
			if (chat_type == 0) {  
				writeC(0x38);				
				writeK(pc.getId()); 
				writeC(0x40);
				writeK(pc.getX());  
				writeC(0x48);
				writeK(pc.getY()); 
			}
				int step = pc.getRankLevel();
				if (step != 0) {
					writeC(0x50);
					writeC(step);
				}
				writeH(0);
		}
	}

	@Override
	public byte[] getContent() {
		if (null == _byte) {
			_byte = _bao.toByteArray();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return S_NewChat;
	}
}