package l1j.server.server.serverpackets;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

import l1j.server.GameSystem.Robot.L1RobotInstance;
import l1j.server.GameSystem.Robot.Robot_Hunt;
import l1j.server.server.Opcodes;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.ClanTable;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.Instance.L1PcInstance;


public class S_Pledge extends ServerBasePacket {
    private static final String _S_Pledge = "[S] _S_Pledge";

    private byte[] _byte = null;


    public S_Pledge(int ClanId) {
        L1Clan clan = ClanTable.getInstance().getTemplate(ClanId);
        writeC(Opcodes.S_EVENT);
        writeC(S_PacketBox.HTML_PLEDGE_ANNOUNCE);
        writeS(clan.getClanName());
        writeS(clan.getLeaderName());
        writeD(clan.getEmblemId());
        writeC(clan.getHouseId() != 0 ? 1 : 0);
        writeC(clan.getCastleId() != 0 ? 1 : 0);    
        writeC(0);
        writeD((int) (clan.getClanBirthDay().getTime() / 1000)); 
        try {
            byte[] text = new byte[478];
            Arrays.fill(text, (byte) 0);
            int i = 0;
            for (byte b : clan.getAnnouncement().getBytes("MS949")) {
                text[i++] = b;
            }
            writeByte(text);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        writeH(0);
    }

    public S_Pledge(int page, int current_page, ArrayList<String> list) {
        writeC(Opcodes.S_EVENT);
        writeC(S_PacketBox.HTML_PLEDGE_MEMBERS);      
        writeC(page);
        writeC(current_page);
        writeC(list.size());
        for (String name : list) {
            if (name == null) continue;
            try {
                L1PcInstance clanMember = CharacterTable.getInstance().restoreCharacter(name);
                if(clanMember!=null){
                	
                	writeS(clanMember.getName());              
                	writeC(clanMember.getClanRank());                
                	writeC(clanMember.getLevel());
                
                byte[] text = new byte[62];
                Arrays.fill(text, (byte) 0);

                if (clanMember.getClanMemberNotes().length() != 0) {
                    int i = 0;
                    for (byte b : clanMember.getClanMemberNotes().getBytes("MS949")) {
                        text[i++] = b;
                    }
                }
                writeByte(text);
                writeD(clanMember.getClanMemberId());
                writeC(clanMember.getType());
                //writeD((int) (System.currentTimeMillis() / 1000L)); //가입일자를 만들자
            	if (clanMember == null
    					|| clanMember.getClanJoinDate() == null) {
    				writeD(0x00);// 가입일
    			} else {
    				writeD((int) (clanMember.getClanJoinDate().getTime() / 1000));
    			}
                }else{
                	 L1RobotInstance robot = Robot_Hunt.getInstance().getRobotInstance(name);
                   	writeS(robot.getName());              
                	writeC(8);                
                	writeC(robot.getLevel());
                
                byte[] text = new byte[62];
                Arrays.fill(text, (byte) 0);

                if (robot.getClanMemberNotes().length() != 0) {
                    int i = 0;
                    for (byte b : robot.getClanMemberNotes().getBytes("MS949")) {
                        text[i++] = b;
                    }
                }
                writeByte(text);
                writeD(robot.getClanMemberId());
                writeC(robot.getType());
                //writeD((int) (System.currentTimeMillis() / 1000L)); //가입일자를 만들자
            	if (robot == null
    					|| robot.getClanJoinDate() == null) {
    				writeD(0x00);// 가입일
    			} else {
    				writeD((int) (robot.getClanJoinDate().getTime() / 1000));
    			}
                	
                }
            } catch (Exception e) {
            }
        }
        writeH(0);
    }
    
    

    /**
     * 메모
     * @param name 혈맹원 이름
     * @param notes 메모 내용
     */
    public S_Pledge(String name, String notes) {
        writeC(Opcodes.S_EVENT);
        writeC(S_PacketBox.HTML_PLEDGE_WRITE_NOTES);
        writeS(name);

        byte[] text = new byte[62];
        Arrays.fill(text, (byte) 0);

        if (notes.length() != 0) {
            int i = 0;
            try {
                for (byte b : notes.getBytes("MS949")) {
                    text[i++] = b;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        writeByte(text);
        writeH(0);
    }

	public S_Pledge(L1Clan clan, int bless) {		
		writeC(Opcodes.S_EXTENDED_PROTOBUF);
		writeH(0x8a);
		writeC(0x08);// 현제 축복기운
		write7B((int) clan.getBlessCount() / 10000);
		writeC(0x10);// 최대 축복기운
		write7B(40000);
		writeC(0x18);// 1회 버프값
		write7B(30000);
		writeC(0x20);// 1회 교환값[재버프사용시]
		write7B(1000);
		for (int i = 0; i < 4; i++) {
			int time = clan.getBuffTime()[i];
			if (time == 0)
				time = 172800;
			writeC(0x2a);// 총길이
			write7B(27 + bitlengh(time));
			writeC(0x0a);
			writeC(bitlengh(time) + 6);		
			writeC(0x08);// 버프아디
			write7B(2724 + i);
			writeC(0x10);// 초
			write7B(time);
			writeC(0x18);// 1:사용가능 2:사용중 3:대기
			writeC(clan.getBuffTime()[i] == 0 ? 1 : bless == i + 1 ? 2 : 3);
			writeC(0x12);// 이름
			writeS2("$" + Integer.toString(22503 + i));
			writeC(0x1a);// 설명
			writeS2("$" + Integer.toString(22508 + i));
			writeC(0x20);// 인벤이미지
			write7B(7233 + (i * 2));
		}
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
        return _S_Pledge;
    }
}
