package l1j.server.server.command.executor;

import java.util.Collection;

import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_WhoAmount;

public class L1Who implements L1CommandExecutor {

	private L1Who() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Who();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			int CalcUser = L1UserCalc.getClacUser();
			Collection<L1PcInstance> players = L1World.getInstance().getAllPlayers();
			int robotcount = 0; // 무인
			int playercount = 0; 
			int AutoShopUser = 0;
			for (L1PcInstance each : players) {
				if(each.noPlayerCK || each.noPlayerck2)
					robotcount++;
				else if (each.isPrivateShop() && each.getNetConnection() == null) {
					AutoShopUser++;
				} else {
					playercount++;
				}
			}
			String amount = String.valueOf(playercount);
			S_WhoAmount s_whoamount = new S_WhoAmount(amount);
			pc.sendPackets(s_whoamount);
			pc.sendPackets(new S_ChatPacket(pc,"접 속 중 : " + playercount));
			pc.sendPackets(new S_ChatPacket(pc,"로 보 트 : " + robotcount));
			pc.sendPackets(new S_ChatPacket(pc,"무인상점 : " + AutoShopUser));
			pc.sendPackets(new S_ChatPacket(pc,"뻥 튀 기 : " + CalcUser));

			
			// 온라인의 플레이어 리스트를 표시
			if (arg.equalsIgnoreCase("전체")) {
				StringBuffer gmList = new StringBuffer();
				StringBuffer playList = new StringBuffer();
				StringBuffer shopList = new StringBuffer();
				StringBuffer robotList = new StringBuffer();

				int countGM = 0, countPlayer = 0, countShop = 0, countRobot = 0;

				for (L1PcInstance each : players) {
					if (each.isGm()) {
						gmList.append(each.getName() + ", ");
						countGM++;
						continue;
					}
					if (each.noPlayerCK || each.noPlayerck2) {
						robotList.append(each.getName() + ", ");
						countRobot++;
						continue;
					}
					if (!each.isPrivateShop()) {
						playList.append(each.getName() + ", ");
						countPlayer++;
						continue;
					}
					if (each.isPrivateShop()) {
						shopList.append(each.getName() + ", ");
						countShop++;
					}
				}
				if (gmList.length() > 0) {
					pc.sendPackets(new S_ChatPacket(pc,"-- 운영자 (" + countGM + "명)"));
					pc.sendPackets(new S_ChatPacket(pc,gmList.toString()));
				}

				if (playList.length() > 0) {
					pc.sendPackets(new S_ChatPacket(pc,"-- 플레이어 (" + countPlayer + "명)"));
					pc.sendPackets(new S_ChatPacket(pc,playList.toString()));
				}
				if (robotList.length() > 0) {
					pc.sendPackets(new S_ChatPacket(pc,"-- 로봇유저 (" + countRobot + "명)"));
					pc.sendPackets(new S_ChatPacket(pc,robotList.toString()));
				}
				if (shopList.length() > 0) {
					pc.sendPackets(new S_ChatPacket(pc,"-- 개인상점 (" + countShop + "명)"));
					pc.sendPackets(new S_ChatPacket(pc,shopList.toString()));
				}
			}
			players = null;
		} catch (Exception e) {
			pc.sendPackets(new S_ChatPacket(pc,".누구 [전체] 라고 입력해 주세요. "));
		}
	}
}
