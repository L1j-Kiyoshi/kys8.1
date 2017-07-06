package l1j.server.server.command.executor;

import java.util.StringTokenizer;

import l1j.server.server.datatables.IpTable;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1BanIp implements L1CommandExecutor {
	
	private L1BanIp() {  }

	public static L1CommandExecutor getInstance() {
		return new L1BanIp();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			
			StringTokenizer stringtokenizer = new StringTokenizer(arg);// IP를 지정
			String s1 = stringtokenizer.nextToken();// add/del를 지정(하지 않아도 OK)
			String s2 = null;
			
			try {
				s2 = stringtokenizer.nextToken();
			} catch (Exception e) {		}

			IpTable iptable = IpTable.getInstance();
			boolean isBanned = iptable.isBannedIp(s1);

			for (L1PcInstance tg : L1World.getInstance(). getAllPlayers()) {
				if (tg.getNetConnection() != null && s1.equals(tg.getNetConnection(). getIp())) {
					String msg = new StringBuilder(). append("IP : ").append(s1).append(" 로 접속중의 플레이어 : ").append(tg.getName()).toString();
					pc.sendPackets(new S_SystemMessage(msg));
				}
			}

			if ("추가". equals(s2) && ! isBanned) {
				iptable.banIp(s1); // BAN 리스트에 IP를 더한다
				String msg = new StringBuilder(). append("IP : ").append(s1).append(" 를 BAN IP에 등록했습니다. ").toString();
				pc.sendPackets(new S_SystemMessage(msg));
			} else if ("삭제". equals(s2) && isBanned) {
				if (iptable.liftBanIp(s1)) { // BAN 리스트로부터 IP를 삭제한다
					String msg = new StringBuilder().append("IP : ").append(s1).append(" 를 BAN IP로부터 삭제했습니다. ").toString();
					pc.sendPackets(new S_SystemMessage(msg));
				}
			} else { // BAN의 확인
				if (isBanned) {
					String msg = new StringBuilder(). append("IP : "). append(s1).append(" 는 BAN IP에 등록되어 있습니다. "). toString();
					pc.sendPackets(new S_SystemMessage(msg));
				} else {
					String msg = new StringBuilder(). append("IP : "). append(s1).append(" 는 BAN IP에 등록되어 있지 않습니다. "). toString();
					pc.sendPackets(new S_SystemMessage(msg));
				}
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(cmdName + " 아이피 [추가, 삭제]라고 입력해 주세요. "));
		}
	}
}
