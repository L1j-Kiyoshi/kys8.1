package l1j.server.server.command.executor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Skills;

public class L1Buff implements L1CommandExecutor {

	private L1Buff() {  }

	public static L1CommandExecutor getInstance() {
		return new L1Buff();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer tok = new StringTokenizer(arg);
			Collection<L1PcInstance> players = null;
			String s = tok.nextToken();
			if (s.equals("나")) {
				players = new ArrayList<L1PcInstance>();
				players.add(pc);
				s = tok.nextToken();
			} else if (s.equals("전체")) {
				players = L1World.getInstance(). getAllPlayers();
				s = tok.nextToken();
			} else {
				players = L1World.getInstance(). getVisiblePlayer(pc);
			}

			int skillId = Integer.parseInt(s);
			int time = 0;
			if (tok.hasMoreTokens()) {
				time = Integer.parseInt(tok.nextToken());
			}

			L1Skills skill = SkillsTable.getInstance(). getTemplate(skillId);

			if (skill.getTarget(). equals("buff")) {
				for (L1PcInstance tg : players) {
					new L1SkillUse(). handleCommands(pc, skillId, tg.getId(), tg.getX(), tg.getY(), null, time, L1SkillUse.TYPE_SPELLSC);
				}
			} else if (skill.getTarget(). equals("none")) {
				for (L1PcInstance tg : players) {
					new L1SkillUse(). handleCommands(tg, skillId, tg.getId(), tg.getX(), tg.getY(), null, time, L1SkillUse.TYPE_GMBUFF);
				}
			} else {
				pc.sendPackets(new S_SystemMessage("버프계의 스킬이 아닙니다. "));
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(cmdName + " [전체, 나] [스킬아이디] [시간] 라고 입력해 주세요. "));
		}
	}
}
