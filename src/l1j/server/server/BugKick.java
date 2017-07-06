package l1j.server.server;

import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_Poison;

public class BugKick {
	//private static Logger _log = Logger.getLogger(BugKick.class.getName());
	
	private static BugKick _instance;

	private BugKick() {
	}

	public static BugKick getInstance() {
		if (_instance == null) {
			_instance = new BugKick();
		}
		return _instance;
	}

	public void KickPlayer(L1PcInstance pc){
		try {
			new L1Teleport().teleport(pc, 32737, 32796, (short) 99, 5, true);
		pc.sendPackets(new S_Poison(pc.getId(), 2)); // 동결 상태가 되었습니다.
		pc.broadcastPacket(new S_Poison(pc.getId(), 2)); // 동결 상태가 되었습니다.
		pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, true));
		pc.killSkillEffectTimer(87);
		pc.setSkillEffect(87, 24 * 60 * 60 * 1000);//여기까지 스턴

		pc.sendPackets(new S_ChatPacket(pc,"버그를 사용하지 않았으면 이곳에 올 이유가 없을텐데??"));
		
		L1World.getInstance().broadcastServerMessage("\\fY버그사용자 ["+pc.getName()+"] 신고바람!!");
		} catch (Exception e) {
			System.out.println(pc.getName()+" 화형장 등록 에러");
		}
	}
}
	