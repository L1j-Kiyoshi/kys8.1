package l1j.server.server.command.executor;

import l1j.server.server.Account;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1AccountBanKick implements L1CommandExecutor {

	private L1AccountBanKick() {	}

	public static L1CommandExecutor getInstance() {
		return new L1AccountBanKick();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			
			L1PcInstance target = L1World.getInstance().getPlayer(arg);
			if (target == null) {
				target = CharacterTable.getInstance().restoreCharacter(arg);
			}

			if (target != null) { //アカウントをBANする
				Account.ban(target.getAccountName());
				pc.sendPackets(new S_SystemMessage(target.getName() + "アカウント差し押さえました。"));
				target.sendPackets(new S_Disconnect());
				
				if (target.getOnlineStatus() == 1) {
					target.sendPackets(new S_Disconnect());
				}
			} else {
				pc.sendPackets(new S_SystemMessage("そのような名前のキャラクターは、ワールド内には存在しません。"));
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(cmdName + "[キャラクター名]で入力してください。"));
		}
	}
}
