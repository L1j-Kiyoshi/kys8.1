package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;

public class S_ChatPacket extends ServerBasePacket {

	private static final String _S__1F_NORMALCHATPACK = "[S] S_ChatPacket";
	private byte[] _byte = null;

	public S_ChatPacket(String targetname, String chat, int opcode) {
		writeC(opcode);
		writeC(9);
		writeS("-> (" + targetname + ") " + chat);
	}
	
	public S_ChatPacket(String targetname, int type, String chat) {
		writeC(Opcodes.S_MESSAGE);
		writeC(type);
		writeS("[" + targetname + "] " + chat);
	}

	// マネージャーのささやき
	public S_ChatPacket(String from, String chat) {
		writeC(Opcodes.S_TELL);
		writeS(from);
		writeS(chat);
	}

	public S_ChatPacket(L1PcInstance pc, String chat) {
		writeC(Opcodes.S_MESSAGE);
		writeC(3);//11
		writeS(chat);
	}

	public S_ChatPacket(String chat) {
		writeC(Opcodes.S_MESSAGE);
		writeC(0x0F);
		writeD(000000000);
		writeS(chat);
	}

	public S_ChatPacket(L1PcInstance pc, String chat, int a, int b, int c) {
		writeC(Opcodes.S_MESSAGE);
		writeC(4);
		writeS(chat);
	}

	public S_ChatPacket(L1PcInstance pc, String chat, int test) {
		writeC(Opcodes.S_SAY);
		writeC(15);
		writeD(pc.getId());
		writeS(chat);
	}

	public S_ChatPacket(String chat, int opcode) {
		writeC(opcode);
		writeC(3);
		writeS(chat);
	}

	public S_ChatPacket(L1PcInstance pc, String chat, int opcode, int type) {
		writeC(opcode);
				
		switch (type) {
		case 0: // 通常のチャット
			writeC(type);
			writeD(pc.getId());
			//バトルゾーン 
			if (!pc.isGm() && pc.getMapId() == 5153) {
				int DuelLine = pc.get_DuelLine();
				if (DuelLine == 1) {
					writeS("1回 : " + chat);
				} else if (DuelLine == 2) {
					writeS("2回 : " + chat);
				} else {
					writeS("観戦者 : " + chat);
				}
			}
			if(pc.is9급병()){ writeS("[9等兵]"+pc.getName()+": " + chat); }
			else if(pc.is8급병()){ writeS("[8等兵]"+pc.getName()+": " + chat); }
			else if(pc.is7급병()){ writeS("[7等兵]"+pc.getName()+": " + chat); }
			else if(pc.is6급병()){ writeS("[6等兵]"+pc.getName()+": " + chat); }
			else if(pc.is5급병()){ writeS("[5等兵]"+pc.getName()+": " + chat); }
			else if(pc.is4급병()){ writeS("[4等兵]"+pc.getName()+": " + chat); }
			else if(pc.is3급병()){ writeS("[3等兵]"+pc.getName()+": " + chat); }
			else if(pc.is2급병()){ writeS("[2等兵]"+pc.getName()+": " + chat); }
			else if(pc.is1급병()){ writeS("[1等兵]"+pc.getName()+": " + chat); }
			else if(pc.is1성장교()){ writeS("[1将校]"+pc.getName()+": " + chat); }
			else if(pc.is2성장교()){ writeS("[2将校]"+pc.getName()+": " + chat); }
			else if(pc.is3성장교()){ writeS("[3将校]"+pc.getName()+": " + chat); }
			else if(pc.is4성장교()){ writeS("[4将校]"+pc.getName()+": " + chat); }
			else if(pc.is5성장교()){ writeS("[5将校]"+pc.getName()+": " + chat); }
			else if(pc.is장군()){ writeS("[将軍]"+pc.getName()+": " + chat); }
			else if(pc.is대장군()){ writeS("[大将軍]"+pc.getName()+": " + chat); }
			else if(pc.is사령관()){ writeS("[司令官]"+pc.getName()+": " + chat); }
			else if(pc.is총사령관()){ writeS("[総司令官]"+pc.getName()+": " + chat); }
			else {
				writeS(pc.getName() + ": " + chat); 
			}
			break;
		case 2: // 絶叫
			writeC(type);
			if (pc.isInvisble()) {
				writeD(0);
			} else {
				writeD(pc.getId());
			}
			writeS("<" + pc.getName() + "> " + chat);
			writeH(pc.getX());
			writeH(pc.getY());
			break;
        case 3:
            writeC(type);
        	if (pc.getName().equalsIgnoreCase("메티스")&& !pc.getName().equalsIgnoreCase("미소피아")&& !pc.getName().equalsIgnoreCase("카시오페아")) {
				writeS("[******] " + chat);
            }
        	if(pc.is9급병() && !pc.isGm()){ writeS("[9等兵]["+pc.getName()+"] " + chat); }
			else if(pc.is8급병() && !pc.isGm()){ writeS("[8等兵]["+pc.getName()+"] " + chat); }
			else if(pc.is7급병() && !pc.isGm()){ writeS("[7等兵]["+pc.getName()+"] " + chat); }
			else if(pc.is6급병() && !pc.isGm()){ writeS("[6等兵]["+pc.getName()+"] " + chat); }
			else if(pc.is5급병() && !pc.isGm()){ writeS("[5等兵]["+pc.getName()+"] " + chat); }
			else if(pc.is4급병() && !pc.isGm()){ writeS("[4等兵]["+pc.getName()+"] " + chat); }
			else if(pc.is3급병() && !pc.isGm()){ writeS("[3等兵]["+pc.getName()+"] " + chat); }
			else if(pc.is2급병() && !pc.isGm()){ writeS("[2等兵]["+pc.getName()+"] " + chat); }
			else if(pc.is1급병() && !pc.isGm()){ writeS("[1等兵]["+pc.getName()+"] " + chat); }
			else if(pc.is1성장교() && !pc.isGm()){ writeS("\\fR[1将校]["+pc.getName()+"] " + chat); }
			else if(pc.is2성장교() && !pc.isGm()){ writeS("\\fR[2将校]["+pc.getName()+"] " + chat); }
			else if(pc.is3성장교() && !pc.isGm()){ writeS("\\fR[3将校]["+pc.getName()+"] " + chat); }
			else if(pc.is4성장교() && !pc.isGm()){ writeS("\\fR[4将校]["+pc.getName()+"] " + chat); }
			else if(pc.is5성장교() && !pc.isGm()){ writeS("\\fR[5将校]["+pc.getName()+"] " + chat); }
			else if(pc.is장군() && !pc.isGm()){ writeS("\\fR[将軍]["+pc.getName()+"] " + chat); }
			else if(pc.is대장군() && !pc.isGm()){ writeS("\\fR[大将軍]["+pc.getName()+"] " + chat); }
			else if(pc.is사령관() && !pc.isGm()){ writeS("\\fR[司令官]["+pc.getName()+"] " + chat); }
			else if(pc.is총사령관() && !pc.isGm()){ writeS("\\fR[総司令官]["+pc.getName()+"] " + chat); }
			else {
			writeS("[" + pc.getName() + "] " + chat);
			}
            break;
		case 4: // 血盟チャット
			writeC(type);
			if (pc.getAge() == 0) {
				writeS("{" + pc.getName() + "} " + chat);
			} else {
				writeS("{" + pc.getName() + "(" + pc.getAge() + ")" + "} " + chat);
			}
			break;
		case 9: // ウィスパー
			writeC(type);
			writeS("-> (" + pc.getName() + ") " + chat);
			break;
		case 11: // パーティーチャット
			writeC(type);
			writeS("(" + pc.getName() + ") " + chat);
			break;
		case 12: // 連合チャット
			writeC(type);
			writeS("[" + pc.getName() + "] " + chat);
			break;
		case 13:
			writeC(4);
			writeS("{{" + pc.getName() + "}} " + chat);
			break;
		case 14: // チャットパーティー
			writeC(type);
			writeD(pc.getId());
			writeS("\\fU(" + pc.getName() + ") " + chat); // #
			break;
		case 15:
			writeC(type);
			writeS("[" + pc.getName() + "] " + chat);
			break;
		case 16: // ウィスパー
			writeS(pc.getName());
			writeS(chat);
			break;
		case 17: // 君主チャット +
			writeC(type);
			writeS("{" + pc.getName() + "} " + chat);
			break;
		default:
			break;
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
		return _S__1F_NORMALCHATPACK;
	}

}