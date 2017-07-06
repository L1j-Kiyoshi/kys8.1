package l1j.server.server.command.executor;


import java.util.StringTokenizer;

import l1j.server.server.datatables.NpcSpawnTable;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.Controller.NpcDeleteController;


public class L1RemoveNpc implements L1CommandExecutor {

	private L1RemoveNpc() {}

	public static L1CommandExecutor getInstance() {
		return new L1RemoveNpc();
	}
	
	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		// TODO Auto-generated method stub
		try {
			StringTokenizer tok = new StringTokenizer(arg);
			
			int npcid = Integer.parseInt(tok.nextToken());
			int time;

			try {
				time = Integer.parseInt(tok.nextToken());
			} catch (Exception e) {
				time = 0;
			}
			
			for (L1Object obj : L1World.getInstance().getVisibleObjects(pc)) {
				if (obj instanceof L1NpcInstance) {
					L1NpcInstance npc = (L1NpcInstance) obj;

					if (npc.getNpcId() == npcid) {
						NpcSpawnTable.getInstance().removeSpawn(npc);
						npc.setRespawn(false);
						npc.NpcDeleteTime = System.currentTimeMillis() + (time * 60 * 1000);
						NpcDeleteController.getInstance().addNpcDelete(npc);
						pc.sendPackets(new S_SystemMessage(npc.getName() + "을(를) " + time + "분 뒤에 삭제 합니다."));
					}
				}
			}
			
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".삭제 [시간(분)] (시야에 있는 npc의 id를 입력하면 입력시간 뒤 삭제(DB에도 적용) 됩니다)"));
		}
	}
}
