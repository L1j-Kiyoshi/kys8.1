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
				pc.sendPackets(new S_SystemMessage((new StringBuilder()).append(target.getName()).append("を強力追放しました。").toString()));
				target.setX(33080);
				target.setY(33392);
				target.setMap((short) 4);
				GameServer.disconnectChar(target);
				target.sendPackets(new S_Disconnect());
			} else {
				pc.sendPackets(new S_SystemMessage(
						"そのような名前のキャラクターは、ワールド内には存在しません。"));
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(cmdName + "[キャラクター名]で入力してください。"));
		}
	}
}