package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;
import l1j.server.server.Controller.BugRaceController;

public class S_RaceBoard extends ServerBasePacket {

	private static final String S_RaceBoard = "[C] S_RaceBoard";

	private byte[] _byte = null;

	public S_RaceBoard(int number) {
		buildPacket(number);
	}

	private void buildPacket(int number) {
	writeC(Opcodes.S_HYPERTEXT);
	writeD(number);
	writeS("maeno4");
	writeC(0);                        
	writeH(15);

	for( int i = 0; i < 5; ++i ) {
		writeS(BugRaceController.getInstance()._littleBugBear[i].getName()); //バグベア名前
		writeS(BugRaceController.getInstance()._bugCondition[i]); //状態
		writeS(Double.toString(BugRaceController.getInstance()._winRate[i]) + "%"); //勝率
	}
}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	public String getType() {
		return S_RaceBoard;
	}
}

