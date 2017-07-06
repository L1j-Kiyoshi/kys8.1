package l1j.server.server.command.executor;

import java.util.StringTokenizer;
import java.util.logging.Logger;

import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1BuffIcon implements L1CommandExecutor {
	@SuppressWarnings("unused")
	private static Logger _log = Logger.getLogger(L1BuffIcon.class.getName());

	private L1BuffIcon() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1BuffIcon();
	}

	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer st = new StringTokenizer(arg);
			int _sprid = Integer.parseInt(st.nextToken(), 10);
			int count = Integer.parseInt(st.nextToken(), 10);
			for (int i = 0; i < count; i++) {
				try {					
					Thread.sleep(1000);
					int num = _sprid + i;
					pc.sendPackets(new S_PacketBox(S_PacketBox.BUFFICON, _sprid + i, 10000, true));
					pc.sendPackets(new S_SystemMessage("무제한 아이콘 : "+num+" 번."));
				} catch (Exception exception) {
					break;
				}
			}
		} catch (Exception exception) {
			pc.sendPackets(new S_SystemMessage(cmdName
					+ " [id] [출현시키는 수]로 입력해 주세요. "));
		}
	}
}
