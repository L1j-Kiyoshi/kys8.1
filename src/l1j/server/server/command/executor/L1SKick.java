package l1j.server.server.command.executor;

import java.util.logging.Logger;

import l1j.server.server.GameServer;
import l1j.server.server.datatables.IpTable;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1SKick implements L1CommandExecutor {
	@SuppressWarnings("unused")
	private static Logger _log = Logger.getLogger(L1SKick.class.getName());

	private L1SKick() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1SKick();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			L1PcInstance target = L1World.getInstance().getPlayer(arg);

			IpTable iptable = IpTable.getInstance();
			if (target != null) {
				if( target.getNetConnection() != null ){
					iptable.banIp(target.getNetConnection().getIp());
				}
				pc.sendPackets(new S_SystemMessage((new StringBuilder()).append(target.getName()).append(" 를 강력 추방 했습니다. ").toString()));
				target.setX(33080);
				target.setY(33392);
				target.setMap((short) 4);
				GameServer.disconnectChar(target);
				target.sendPackets(new S_Disconnect());
			} else {
				pc.sendPackets(new S_SystemMessage(
						"그러한 이름의 캐릭터는 월드내에는 존재하지 않습니다. "));
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(cmdName + " [캐릭터명]으로 입력해 주세요. "));
		}
	}
}