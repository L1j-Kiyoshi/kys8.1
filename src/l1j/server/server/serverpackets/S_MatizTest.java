package l1j.server.server.serverpackets;

import java.util.StringTokenizer;

import l1j.server.server.Opcodes;

public class S_MatizTest extends ServerBasePacket {
	private static final String _TYPE = "[S] S_MatizTest";
	
	private byte[] _byte = null;
	
	/*아이콘만 띠워줍니다 스킬이랑은 추가하셔야함!! 힁! 패킷만 살짝!
	 
	 // 이런식으로 되어있으니 아이콘이 안되는거네요
	 public S_PacketBox(int skillId, long time, boolean on) { //, boolean on
	  int[] p = new int [3];
	  switch (skillId) {
	   
	  case L1SkillId.헌터버프: //139번 스킬넘버로 지정됨
	   //6621 9901 10136
	   p[0] = 6621;  p[1] = 9901 ; p[2] = 10136;
	   break;
	  }
	  
	  writeC(Opcodes.S_OPCODE_NewUI);
	  writeH(S_NewCreateItem.버프창);
	  writeC(0x08);
	  writeC(on ? 2 : 3);//true,false
	  writeC(0x10);
	  writeH(p[0]);
	  
	  writeC(0x18);
	  writeBit(time);
	  writeC(0x20);
	  writeC(0x08);
	  writeC(0x28);
	  writeH(p[1]);//아이콘
	  
	  writeC(0x30);
	  writeC(0x00);
	  writeC(0x38);
	  writeC(0x10);
	  writeC(0x40);
	  writeH(p[2]); // 스트링
	  writeC(0x48);
	  writeC(0x00);
	  writeC(0x50);
	  writeC(0x00);
	  writeC(0x58);
	  writeC(0x01);
	  writeC(0x00);
	  writeC(0x00);
	  
	 }*/
	public S_MatizTest(int type,String value){
		String s ="";
		switch(type){
			case 1:
				s="6f 07 02 0a 13 08 80 02 10 9e 96 95 c2 05 22 06 08 01 10 "+value+"  18 05 28 01 00 00";
				break;
			case 2:
				s="6f 0b 02 08 00 10 82 02 00 00";
				break;
			case 3:
				s="6f 0d 02 08 00 10 81 02 00 00";				
				//  6f 0d 02 08 00 10 81 02 00 00
				//6f 0d 02 08 00 10 81 02 00 00
				break;
			case 4:
				s="6f 0b 02 08 00 10 a8 02 00 00";
				break;
			case 5:
				s="6f 07 02 0a 17 08 82 02 10 00 22 06 08 01 10 00 18 05 22 06 08 02 10 00 18 06 28 01 00 00";
							break;
			case 6:
				s="6f 0d 02 08 00 10 82 02 00 00";
					break;
			case 7:
				s="6f 07 02 0a 1b 08 81 02 10 9e db c3 c3 05 22 06 08 01 10 01 18 01 22 06 08 02 10 01 18 01 28 01 00 00";
					break;
			case 8:
				s="6f 6e 00 08 01 10 8a 19 18 88 0e 20 08 28 c6 3f 30 00 38 12 40 85 27 48 00 50 00 58 01 60 00 68 00 70 00 cb 6b";
						break;	
			case 9:
				s="6f 6e 00 08 01 10 8a 19 18 88 0e 20 08 28 c6 3f 30 00 38 12 40 85 27 48 00 50 00 58 01 60 00 68 00 70 00 cb 6b";
				break;
			case 10:
				s="00 db ef  ff 05 2a 05 01 00 01 00 01 01 00 00 00 03 2b 30	20 24 33 32 38 31 00 12 01 12 15 0b c8 00 00 00 02 01 6b 01 01 04 06 01 07 e3 18 00 00 00 00 00 03 1b 38 33 95 00 00 00 00 00 00 00 00	02 00 00 00 00 00 76 30";
				//s="00 db ef  ff 05 2a 05 01 00 01 00 01 01 00 00 00 03 2b 30	20 24 33 32 38 31 00 12 (01 12 15 0b c8 00 00 00 02 01 6b 01 01 04 06 01 07 e3 )18 00 00 00 00 00 00 1b 38 33 95 00 00 00 00 00 00 00 00	02 00 00 00 00 00 76 30";
				   //03 2b 30 20 24 33 32 38 31 이쪽이 string Name임 30이 00이고 31이 +1
				//괄호친 부분이 STATUS인듯함
				break;
			case 11:
				s="00 42 d7 d7 15 00 00 01 00 65 07 01 01 00 00 00 12 24 36 31 31 35 2b 38 20 b0 a2 c0 ce b5 c8 20 b9 ab b0 fc c0 c7 20 be e7 bc d5 b0 cb 00 1d 01 13 17 0b 96 00 00 00 02 08 04 07 22 27 b9 ab b1 e2 20 b8 ed c1 df 20 2b 31 00 06 05 18 00 00 00 00 00 08 42 d7 d7 15 00 00 00 00 00 00 00 00 03 00 00 00 00 11 00 00";
				break;
		}
		StringTokenizer st = new StringTokenizer(s);
		while (st.hasMoreTokens()) {
			writeC(Integer.parseInt(st.nextToken(), 16));
		}
	}
	public S_MatizTest(int type,String value,String value2)
	{
		String s;//="df 07 02 0a 1b 08 82 02 10 9b cd 9f c2 05 22 06 08 01 10 "+value+" 18 05 22 06 08 02 10 "+value2+" 18 06 28 01 00 00";
		s="00 42 d7 d7 15 00 00 01 00 65 07 01 01 00 00 00 12 24 36 31 31 "+value+" 2b "+value2+" 20 b0 a2 c0 ce b5 c8 20 b9 ab b0 fc c0 c7 20 be e7 bc d5 b0 cb 00 1d 01 13 17 0b 96 00 00 00 02 08 04 07 22 27 b9 ab b1 e2 20 b8 ed c1 df 20 2b 31 00 06 05 18 00 00 00 00 00 08 42 d7 d7 15 00 00 00 00 00 00 00 00 03 00 00 00 00 11 00 00";
		
				StringTokenizer st = new StringTokenizer(s);
			while (st.hasMoreTokens()) {
				writeC(Integer.parseInt(st.nextToken(), 16));
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
