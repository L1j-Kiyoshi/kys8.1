package l1j.server.server.command.executor;

import java.util.StringTokenizer;
import java.util.logging.Logger;

import l1j.server.server.datatables.NpcSpawnTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1NpcDown1 implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1NpcDown1.class.getName());

	public static L1CommandExecutor getInstance() {
		return new L1NpcDown1();
	}

	public void execute(L1PcInstance pc, String cmdName, String st) {
		try {
			StringTokenizer token = new StringTokenizer(st);
			String type = token.nextToken();

			if (type.equals("set")) {
				NpcTable.reload();
				NpcSpawnTable.getInstance().reload1();
				
				pc.sendPackets(new S_SystemMessage("すべてのエンピシが配置されました。"));
			} else if (type.equals("delete")) {
				for (L1Object l1object : L1World.getInstance().getObject()) {
					if (l1object instanceof L1NpcInstance) {
						L1NpcInstance npc = (L1NpcInstance) l1object;
						if (npc != null) {
							npc.setRespawn(false);
							npc.deleteMe();
						}
					}
				}
				pc.sendPackets(new S_SystemMessage("すべてのエンピシが削除されました。"));
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage("。エンピシダウン[配置/削除]を入力してください。"));
		}
	}
}