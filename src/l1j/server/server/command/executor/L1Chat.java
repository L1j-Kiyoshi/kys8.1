package l1j.server.server.command.executor;

import java.util.StringTokenizer;

import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1Chat implements L1CommandExecutor {

	private L1Chat() { }

	public static L1CommandExecutor getInstance() {
		return new L1Chat();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer st = new StringTokenizer(arg);
			if (st.hasMoreTokens()) {
				String flag = st.nextToken();
				String msg;
				if (flag.compareToIgnoreCase("on") == 0) {
					L1World.getInstance(). set_worldChatElabled(true);
					msg = "ワールドチャットを有効にしました。";
				} else if (flag.compareToIgnoreCase("off") == 0) {
					L1World.getInstance(). set_worldChatElabled(false);
					msg = "ワールドチャットを停止しました。";
				} else {
					throw new Exception();
				}
				pc.sendPackets(new S_SystemMessage(msg));
			} else {
				String msg;
				if (L1World.getInstance(). isWorldChatElabled()) {
					msg = "現在ワールドチャットは有効です。チャットオフに停止することができます。";
				} else {
					msg = "現在ワールドチャットは停止しています。チャットオンで有効にすることができます。";
				}
				pc.sendPackets(new S_SystemMessage(msg));
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(cmdName + "[入、切]"));
		}
	}
}
