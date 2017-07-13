package l1j.server.server.command.executor;

import java.util.StringTokenizer;

import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_SkillIconGFX;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1ChatNG implements L1CommandExecutor {

	private L1ChatNG() {	}

	public static L1CommandExecutor getInstance() {
		return new L1ChatNG();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer st = new StringTokenizer(arg);
			String name = st.nextToken();
			int time = Integer.parseInt(st.nextToken());
//			String reason = st.nextToken();

			L1PcInstance tg = L1World.getInstance(). getPlayer(name);

			if (tg != null) {
				tg.setSkillEffect(L1SkillId.STATUS_CHAT_PROHIBITED, time * 60 * 1000);
				tg.sendPackets(new S_SkillIconGFX(36, time * 60));
				pc.sendPackets(new S_SystemMessage(name + "チャット禁止："+ String.valueOf(time) + "分適用完了。"));
//				tg.sendPackets(new S_ServerMessage(286, String.valueOf(time))); // \f3ゲームに適していない行動であるため、今後％0分間チャットを禁止します。
			} else {
				pc.sendPackets(new S_SystemMessage("そのキャラクター未接続。"));
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(cmdName + "[キャラクター名] [分]入力。"));
		}
	}
}
