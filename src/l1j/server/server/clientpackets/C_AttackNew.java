package l1j.server.server.clientpackets;

import l1j.server.server.GameClient;
import l1j.server.server.Controller.AttackController;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;

public class C_AttackNew extends ClientBasePacket {

	public C_AttackNew(byte[] decrypt, GameClient client) {
		super(decrypt);
		int targetId = readD();
		L1PcInstance pc = client.getActiveChar();
		if(pc == null)
			return;

		L1Object target = L1World.getInstance().findObject(targetId);
	
		if (target != null && target instanceof L1Character && !((L1Character) target).isDead()) {
			AttackController.start(pc, (L1Character) target);
		} else {
			AttackController.stop(pc);
		}
	}
}

