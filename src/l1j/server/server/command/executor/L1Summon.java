package l1j.server.server.command.executor;

import java.util.StringTokenizer;

import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Npc;

public class L1Summon implements L1CommandExecutor {

	private L1Summon() {
	}

	public static L1Summon getInstance() {
		return new L1Summon();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer tok = new StringTokenizer(arg);
			String nameid = tok.nextToken();
			int npcid = 0;
			try {
				npcid = Integer.parseInt(nameid);
			} catch (NumberFormatException e) {
				npcid = NpcTable.getInstance(). findNpcIdByNameWithoutSpace(
						nameid);
				if (npcid == 0) {
					pc.sendPackets(new S_SystemMessage("해당 NPC가 발견되지 않습니다. "));
					return;
				}
			}
			int count = 1;
			if (tok.hasMoreTokens()) {
				count = Integer.parseInt(tok.nextToken());
			}
			L1Npc npc = NpcTable.getInstance(). getTemplate(npcid);
			L1SummonInstance summonInst = null;
			for (int i = 0; i < count; i++) {
				summonInst = new L1SummonInstance(npc, pc);
				summonInst.setPetcost(0);
			}
			nameid = NpcTable.getInstance(). getTemplate(npcid). get_name();
			pc.sendPackets(new S_SystemMessage(nameid + "(ID:" + npcid + ") ("
					+ count + ")를 소환했습니다. "));
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(cmdName
					+ " [npcid or name] [서먼수] 라고 입력해 주세요. "));
		}
	}
}
