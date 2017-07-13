package l1j.server.server.command.executor;

import java.util.StringTokenizer;

import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillIconGFX;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1ChatNG2 implements L1CommandExecutor {

	private L1ChatNG2() {	}

	public static L1CommandExecutor getInstance() {
		return new L1ChatNG2();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer st = new StringTokenizer(arg);
			String name = st.nextToken();
			int time = Integer.parseInt(st.nextToken());
			String reason = st.nextToken();

			L1PcInstance tg = L1World.getInstance(). getPlayer(name);

			if (tg != null) {
				tg.setSkillEffect(L1SkillId.STATUS_CHAT_PROHIBITED, time * 60 * 1000);
				tg.sendPackets(new S_SkillIconGFX(36, time * 60));
				tg.sendPackets(new S_ServerMessage(286, String.valueOf(time))); // \f3ゲームに適していない行動であるため、今後％0分間チャットを禁止します。
				L1World.getInstance().broadcastPacketToAll(new S_SystemMessage(name + " キャラクター "+ String.valueOf(time) + "分間チャット禁止（理由：" + reason + ")"));
			} else {
				pc.sendPackets(new S_SystemMessage("そのキャラクター未接続。"));
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(cmdName + "[キャラクター名] [分] [金鉱の理由]を入力。"));
		}
	}
}
