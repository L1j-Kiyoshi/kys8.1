
package l1j.server.server.clientpackets;

import l1j.server.server.Account;
import l1j.server.server.GameClient;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_RetrieveList;
import l1j.server.server.serverpackets.S_ServerMessage;

public class C_WhPw extends ClientBasePacket{
	
	private static final String C_WhPw = "[C] C_WhPw";
	
	/**
	 * (0e) (00) (0e 64 03) 00 (0e 64 03) 00 00 00
	 *  オプションタイプ現在、以下の
	 */
	public C_WhPw(byte[] data, GameClient client){
		super(data);
		L1PcInstance pc = client.getActiveChar();
		if ( pc == null)return;
		int gamepassword = client.getAccount().getGamePassword();
		int type = readC();
		if(type == 0){	/** 設定 */			
			int oldpass = readCH();		
			//System.out.println(oldpass);
			readC();	// dummy
			int newpass = readCH();
			//System.out.println(newpass);
			if(gamepassword == 0 || gamepassword == oldpass){
				Account.setGamePassword(client, newpass);
			}else{
				pc.sendPackets(new S_ServerMessage(835));
			}			
		}else if(type == 1){	/** 倉庫の検索*/
			int chkpass = readCH();							
			readC();	// dummy
			int objId = readD();			
			if(gamepassword == 0 || gamepassword == chkpass){
			if(pc.getLevel() >= 5) pc.sendPackets(new S_RetrieveList(objId, pc));
			}else{
				pc.sendPackets(new S_ServerMessage(835));
			}		
		}
	}


	public String getType() {
		return C_WhPw;
	}
}