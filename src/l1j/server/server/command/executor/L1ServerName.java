package l1j.server.server.command.executor;

import java.util.StringTokenizer;

import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1ServerName implements L1CommandExecutor{

	private L1ServerName() {  }

	public static L1CommandExecutor getInstance(){
		return new L1ServerName();
	}

	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer st = new StringTokenizer(arg);
			int invid = Integer.parseInt(st.nextToken(), 10);
			int count = Integer.parseInt(st.nextToken(), 10);
			L1ItemInstance item = null;

			for (int i = 0; i < count; i++) {
				item = ItemTable.getInstance().createItem(40308);
				item.getItem().setName(String.valueOf("$" + (invid + i)));
				pc.sendPackets(new S_SystemMessage((invid + i)+" : " + "$" + (invid + i)));
			}
		} catch (Exception exception) {
			pc.sendPackets(new S_SystemMessage(".네임  [id] [출현시키는 수]로 입력해 주세요. "));
		}
	}
}