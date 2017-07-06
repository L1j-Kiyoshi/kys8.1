package l1j.server.server.command.executor;

import java.util.StringTokenizer;
import java.util.logging.Logger;

import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1요리아이콘 implements L1CommandExecutor {
	@SuppressWarnings("unused")
	private static Logger _log = Logger.getLogger(L1요리아이콘.class.getName());

	private L1요리아이콘() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1요리아이콘();
	}

	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer st = new StringTokenizer(arg);
			int _sprid = Integer.parseInt(st.nextToken(), 10);
			int count = Integer.parseInt(st.nextToken(), 10);
			for (int i = 0; i < count; i++) {
				try {					
					Thread.sleep(2000);
					int num = _sprid + i;
					//new S_PacketBox(53, cookingType, time));
					pc.sendPackets(new S_PacketBox(S_PacketBox.ICON_COOKING, _sprid+i, 10));//무제한패킷
					pc.sendPackets(new S_SystemMessage("요리 아이콘 : "+num+" 번."));
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