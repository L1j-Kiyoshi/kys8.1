/**
 * 무인 엔피씨 상점 시작 명령어
 * by - Eva Team.
 */
package l1j.server.server.command.executor;

import java.util.logging.Logger;

import l1j.server.server.NpcShopSystem;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1NpcShopSwitch implements L1CommandExecutor {
	@SuppressWarnings("unused")
	private static Logger _log = Logger.getLogger(L1NpcShopSwitch.class.getName());

	private L1NpcShopSwitch() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1NpcShopSwitch();
	}

	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			boolean power = NpcShopSystem.getInstance().isPower();
			if (arg.equalsIgnoreCase("켬")) {
				if (power) {
					pc.sendPackets(new S_SystemMessage("이미실행중입니다."));
					return;
				} else {
					NpcShopSystem.getInstance().npcShopStart();
				}
			} else if (arg.equalsIgnoreCase("끔")) {
				if (!power) {
					pc.sendPackets(new S_SystemMessage("실행되지 않았습니다."));
					return;
				} else {
					NpcShopSystem.getInstance().npcShopStop();
				}
			} else {
				pc.sendPackets(new S_SystemMessage(".영자상점 켬/끔"));
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".영자상점 메소드오류"));
		}
	}
}
	
	