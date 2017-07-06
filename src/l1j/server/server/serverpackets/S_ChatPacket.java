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

	// 매니저용 귓말
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
		case 0: // 통상채팅
			writeC(type);
			writeD(pc.getId());
			//배틀존 
			if (!pc.isGm() && pc.getMapId() == 5153) {
				int DuelLine = pc.get_DuelLine();
				if (DuelLine == 1) {
					writeS("1번 : " + chat);
				} else if (DuelLine == 2) {
					writeS("2번 : " + chat);
				} else {
					writeS("관전자 : " + chat);
				}
			}
			if(pc.is9급병()){ writeS("[9급병]"+pc.getName()+": " + chat); }
			else if(pc.is8급병()){ writeS("[8급병]"+pc.getName()+": " + chat); }
			else if(pc.is7급병()){ writeS("[7급병]"+pc.getName()+": " + chat); }
			else if(pc.is6급병()){ writeS("[6급병]"+pc.getName()+": " + chat); }
			else if(pc.is5급병()){ writeS("[5급병]"+pc.getName()+": " + chat); }
			else if(pc.is4급병()){ writeS("[4급병]"+pc.getName()+": " + chat); }
			else if(pc.is3급병()){ writeS("[3급병]"+pc.getName()+": " + chat); }
			else if(pc.is2급병()){ writeS("[2급병]"+pc.getName()+": " + chat); }
			else if(pc.is1급병()){ writeS("[1급병]"+pc.getName()+": " + chat); }
			else if(pc.is1성장교()){ writeS("[1성장교]"+pc.getName()+": " + chat); }
			else if(pc.is2성장교()){ writeS("[2성장교]"+pc.getName()+": " + chat); }
			else if(pc.is3성장교()){ writeS("[3성장교]"+pc.getName()+": " + chat); }
			else if(pc.is4성장교()){ writeS("[4성장교]"+pc.getName()+": " + chat); }
			else if(pc.is5성장교()){ writeS("[5성장교]"+pc.getName()+": " + chat); }
			else if(pc.is장군()){ writeS("[장군]"+pc.getName()+": " + chat); }
			else if(pc.is대장군()){ writeS("[대장군]"+pc.getName()+": " + chat); }
			else if(pc.is사령관()){ writeS("[사령관]"+pc.getName()+": " + chat); }
			else if(pc.is총사령관()){ writeS("[총사령관]"+pc.getName()+": " + chat); }
			else {
				writeS(pc.getName() + ": " + chat); 
			}
			break;
		case 2: // 절규
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
        	if(pc.is9급병() && !pc.isGm()){ writeS("[9급병]["+pc.getName()+"] " + chat); }
			else if(pc.is8급병() && !pc.isGm()){ writeS("[8급병]["+pc.getName()+"] " + chat); }
			else if(pc.is7급병() && !pc.isGm()){ writeS("[7급병]["+pc.getName()+"] " + chat); }
			else if(pc.is6급병() && !pc.isGm()){ writeS("[6급병]["+pc.getName()+"] " + chat); }
			else if(pc.is5급병() && !pc.isGm()){ writeS("[5급병]["+pc.getName()+"] " + chat); }
			else if(pc.is4급병() && !pc.isGm()){ writeS("[4급병]["+pc.getName()+"] " + chat); }
			else if(pc.is3급병() && !pc.isGm()){ writeS("[3급병]["+pc.getName()+"] " + chat); }
			else if(pc.is2급병() && !pc.isGm()){ writeS("[2급병]["+pc.getName()+"] " + chat); }
			else if(pc.is1급병() && !pc.isGm()){ writeS("[1급병]["+pc.getName()+"] " + chat); }
			else if(pc.is1성장교() && !pc.isGm()){ writeS("\\fR[1성장교]["+pc.getName()+"] " + chat); }
			else if(pc.is2성장교() && !pc.isGm()){ writeS("\\fR[2성장교]["+pc.getName()+"] " + chat); }
			else if(pc.is3성장교() && !pc.isGm()){ writeS("\\fR[3성장교]["+pc.getName()+"] " + chat); }
			else if(pc.is4성장교() && !pc.isGm()){ writeS("\\fR[4성장교]["+pc.getName()+"] " + chat); }
			else if(pc.is5성장교() && !pc.isGm()){ writeS("\\fR[5성장교]["+pc.getName()+"] " + chat); }
			else if(pc.is장군() && !pc.isGm()){ writeS("\\fR[장군]["+pc.getName()+"] " + chat); }
			else if(pc.is대장군() && !pc.isGm()){ writeS("\\fR[대장군]["+pc.getName()+"] " + chat); }
			else if(pc.is사령관() && !pc.isGm()){ writeS("\\fR[사령관]["+pc.getName()+"] " + chat); }
			else if(pc.is총사령관() && !pc.isGm()){ writeS("\\fR[총사령관]["+pc.getName()+"] " + chat); }
			else {
			writeS("[" + pc.getName() + "] " + chat);
			}
            break;
		case 4: // 혈맹채팅
			writeC(type);
			if (pc.getAge() == 0) {
				writeS("{" + pc.getName() + "} " + chat);
			} else {
				writeS("{" + pc.getName() + "(" + pc.getAge() + ")" + "} " + chat);
			}
			break;
		case 9: // 위스파
			writeC(type);
			writeS("-> (" + pc.getName() + ") " + chat);
			break;
		case 11: // 파티채팅
			writeC(type);
			writeS("(" + pc.getName() + ") " + chat);
			break;
		case 12: // 연합 채팅
			writeC(type);
			writeS("[" + pc.getName() + "] " + chat);
			break;
		case 13:
			writeC(4);
			writeS("{{" + pc.getName() + "}} " + chat);
			break;
		case 14: // 채팅파티
			writeC(type);
			writeD(pc.getId());
			writeS("\\fU(" + pc.getName() + ") " + chat); // #
			break;
		case 15:
			writeC(type);
			writeS("[" + pc.getName() + "] " + chat);
			break;
		case 16: // 위스파
			writeS(pc.getName());
			writeS(chat);
			break;
		case 17: // 군주채팅 +
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