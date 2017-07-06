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
				tg.sendPackets(new S_ServerMessage(286, String.valueOf(time))); // \f3게임에 적합하지 않는 행동이기 (위해)때문에, 향후%0분간 채팅을 금지합니다.
				L1World.getInstance().broadcastPacketToAll(new S_SystemMessage(name + " 캐릭터 "+ String.valueOf(time) + "분간 채팅금지 (사유: " + reason + ")"));
			} else {
				pc.sendPackets(new S_SystemMessage("해당 캐릭터 미접속."));
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(cmdName + " [캐릭명] [분] [채금사유] 입력."));
		}
	}
}
