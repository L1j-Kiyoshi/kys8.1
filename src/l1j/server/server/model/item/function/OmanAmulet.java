package l1j.server.server.model.item.function;

import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class OmanAmulet {

	public static void clickItem(L1PcInstance pc, int itemId, L1ItemInstance l1iteminstance) {
		if (pc.get_DuelLine() != 0) {
			pc.sendPackets(new S_SystemMessage("배틀존지역에서 사용할 수 없습니다."));
			return;
		}
		if (pc.isParalyzed() || pc.isSleeped() || pc.isDead()) {
			return;
		}
		if (!pc.getMap().isEscapable()){
			return;
		}
		if(pc.isDead())
			return;
		if(pc.getCurrentHp()<1)
			return;
		
		switch (itemId) {
		case 830012: //오만의 탑 1층 이동 부적
		case 830022: //오만의 탑 1층 지배 부적
			new L1Teleport().teleport(pc, 32735, 32798, (short) 101, pc.getHeading(), true);
			break;
		case 830013: //오만의 탑 2층 이동 부적
		case 830023: //오만의 탑 2층 지배 부적
			new L1Teleport().teleport(pc, 32730, 32802, (short) 102, pc.getHeading(), true);
			break;
		case 830014: //오만의 탑 3층 이동 부적
		case 830024: //오만의 탑 3층 지배 부적
			new L1Teleport().teleport(pc, 32726, 32803, (short) 103, pc.getHeading(), true);
			break;
		case 830015: //오만의 탑 4층 이동 부적
		case 830025: //오만의 탑 4층 지배 부적
			new L1Teleport().teleport(pc, 32621, 32858, (short) 104, pc.getHeading(), true);
			break;
		case 830016: //오만의 탑 5층 이동 부적
		case 830026: //오만의 탑 5층 지배 부적
			new L1Teleport().teleport(pc, 32599, 32866, (short) 105, pc.getHeading(), true);
			break;
		case 830017: //오만의 탑 6층 이동 부적
		case 830027: //오만의 탑 6층 지배 부적a
			new L1Teleport().teleport(pc, 32611, 32862, (short) 106, pc.getHeading(), true);
			break;
		case 830018: //오만의 탑 7층 이동 부적
		case 830028: //오만의 탑 7층 지배 부적
			new L1Teleport().teleport(pc, 32618, 32866, (short) 107, pc.getHeading(), true);
			break;
		case 830019: //오만의 탑 8층 이동 부적
		case 830029: //오만의 탑 8층 지배 부적
			new L1Teleport().teleport(pc, 32600, 32866, (short) 108, pc.getHeading(), true);
			break;
		case 830020: //오만의 탑 9층 이동 부적
		case 830030: //오만의 탑 9층 지배 부적
			new L1Teleport().teleport(pc, 32612, 32866, (short) 109, pc.getHeading(), true);
			break;
		case 830021: //오만의 탑 10층 이동 부적
		case 830031: //오만의 탑 10층 지배 부적
			new L1Teleport().teleport(pc, 32729, 32802, (short) 110, pc.getHeading(), true);
			break;
		}
	}
}
