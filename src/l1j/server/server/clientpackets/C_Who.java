package l1j.server.server.clientpackets;

import java.util.Collection;

import l1j.server.server.GameClient;
import l1j.server.server.command.executor.L1UserCalc;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1NpcShopInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_WhoAmount;
import l1j.server.server.serverpackets.S_WhoCharinfo;

public class C_Who extends ClientBasePacket {

	private static final String C_WHO = "[C] C_Who";

	public C_Who(byte[] decrypt, GameClient client) {
		super(decrypt);
		String s = readS();
		L1PcInstance find = L1World.getInstance().getPlayer(s);
		L1NpcShopInstance find1 = L1World.getInstance().getShopNpc(s);
		L1PcInstance pc = client.getActiveChar();
		
		//실시간 플레이어수를 체크하기위함..
		int playercount = 0; 
		Collection<L1PcInstance> players = L1World.getInstance().getAllPlayers();
		for (L1PcInstance each : players) {
			if(each.noPlayerCK || each.noPlayerck2)
				playercount++;
		}
		//실시간 플레이어수를 체크하기위함..실패..ㅋ
		
		if (pc == null) return;

		if (find != null) {
			S_WhoCharinfo s_whocharinfo = new S_WhoCharinfo(find);
			pc.sendPackets(s_whocharinfo);
		} else if (find1 != null) {
			S_WhoCharinfo s_whocharinfo = new S_WhoCharinfo(find1);
			pc.sendPackets(s_whocharinfo);
		} else {
			int AddUser = (int) (L1World.getInstance().getAllPlayers().size() * 1.5);
			int CalcUser = L1UserCalc.getClacUser();
			AddUser += CalcUser;
			String amount = String.valueOf(AddUser);
			S_WhoAmount s_whoamount = new S_WhoAmount(amount);
			pc.sendPackets(s_whoamount);
		}
	}

	@Override
	public String getType() {
		return C_WHO;
	}
}
