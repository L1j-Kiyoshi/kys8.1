package l1j.server.server.command.executor;

import java.util.StringTokenizer;

import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1ServerMessage implements L1CommandExecutor{
	
	private L1ServerMessage(){ }
	
	public static L1CommandExecutor getInstance(){
		return new L1ServerMessage();
	}
	
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer st = new StringTokenizer(arg);
			int ment = Integer.parseInt(st.nextToken(), 10);
			int count = Integer.parseInt(st.nextToken(), 10);

			for (int i = 0; i <= count; i++ ) {
				pc.sendPackets(new S_ServerMessage(ment + i));
				pc.sendPackets(new S_SystemMessage("(" + (ment + i) +")번의 멘트는 위와 같습니다"));
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".메세지 [번호] [갯수]을 입력 해주세요."));
		}
	}

}
