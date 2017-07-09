package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;

public class S_EventNotice extends ServerBasePacket
{
  @SuppressWarnings("unused")
private static final String S_EventNotice = "[S] S_EventNotice";
  
  private byte[] _byte = null;
 

public S_EventNotice(){
	  
	  String event = "               イベント終了";
//	  String date = "3日前";
	  
	  int length = event.getBytes().length + 19;
//	  int length2 = date.getBytes().length + 19;
	  writeC(Opcodes.S_EXTENDED_PROTOBUF);
      writeC(141);
      writeC(0);
      writeH(264);
      writeC(16);
      writeC(1); 
      writeC(26);
      writeC(length);
      writeC(16);
      writeD(110139902); 
      writeC(34);
      writeS2(event);
      writeC(40);
      writeC(129);
      writeD(95470807);
      writeC(48);  
      writeC(129); 
      writeD(95470807);
      writeH(0);
   
  }

  public byte[] getContent()
  {
    if (this._byte == null) {
      this._byte = this._bao.toByteArray();
    }
    return this._byte;
  }

  public String getType()
  {
    return "[S] S_EventNotice";
  }
}



//ソース
/*package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;

public class S_EventNotice extends ServerBasePacket {
	private static final String S_EventNotice = "[S] S_EventNotice";

	private byte[] _byte = null;
	public static final int EVENT_NOTICE = 141;

	public S_EventNotice(int code) {
		writeC(Opcodes.S_EXTENDED_PROTOBUF);
		writeC(code);
		switch (code) {
		case EVENT_NOTICE:
			String event = "[実装中です]";
			writeC(0x00);
			writeH(0x0108);
			writeC(0x10);
			writeC(0x01);
			writeC(0x1a);
			int length = event.getBytes().length + 19;
			writeC(length);
			writeC(0x10);
			writeD(0x069099fe); // ? イベント番号と推定
			writeC(0x22);
			writeS2(event);
			writeC(0x28);
			writeC(0x81);
			writeD(0x05b0c4d7);
			writeC(0x30);
			writeC(0x81);
			writeD(0x05b0c4d7);
			writeH(0);
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
		return S_EventNotice;
	}
}
*/