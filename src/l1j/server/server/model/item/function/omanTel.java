package l1j.server.server.model.item.function;

import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class omanTel {

	public static void clickItem(L1PcInstance pc, int itemId, L1ItemInstance l1iteminstance) {
		L1Location newLocation = pc.getLocation().randomLocation(200, true);
		int newX = newLocation.getX();
		int newY = newLocation.getY();
		short mapId = (short) newLocation.getMapId();
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
		case 830001: //오만의 탑 1층 이동 주문서
			if (pc.getMapId() == 101) {
				new L1Teleport().teleport(pc, newX, newY, mapId, pc.getHeading(), true);
			} else {
				new L1Teleport().teleport(pc, 32735, 32798, (short) 101, pc.getHeading(), true);
			}
			pc.getInventory().removeItem(l1iteminstance, 1);
			break;
		case 830002: //오만의 탑 2층 이동 주문서
			if (pc.getMapId() == 102) {
				new L1Teleport().teleport(pc, newX, newY, mapId, pc.getHeading(), true);
			} else {
				new L1Teleport().teleport(pc, 32726, 32803, (short) 102, pc.getHeading(), true);
			}
			pc.getInventory().removeItem(l1iteminstance, 1);
			break;
		case 830003: //오만의 탑 3층 이동 주문서
			if (pc.getMapId() == 103) {
				new L1Teleport().teleport(pc, newX, newY, mapId, pc.getHeading(), true);
			} else {
				new L1Teleport().teleport(pc, 32726, 32803, (short) 103, pc.getHeading(), true);
			}
			pc.getInventory().removeItem(l1iteminstance, 1);
			break;
		case 830004: //오만의 탑 4층 이동 주문서
			if (pc.getMapId() == 104) {
				new L1Teleport().teleport(pc, newX, newY, mapId, pc.getHeading(), true);

			} else {
				new L1Teleport().teleport(pc, 32613, 32863, (short) 104, pc.getHeading(), true);
			}
			pc.getInventory().removeItem(l1iteminstance, 1);
			break;
		case 830005: //오만의 탑 5층 이동 주문서
			if (pc.getMapId() == 105) {
				new L1Teleport().teleport(pc, newX, newY, mapId, pc.getHeading(), true);
				
			} else {
				new L1Teleport().teleport(pc, 32597, 32867, (short) 105, pc.getHeading(), true);
			}
			pc.getInventory().removeItem(l1iteminstance, 1);
			break;
		case 830006: //오만의 탑 6층 이동 주문서
			if (pc.getMapId() == 106) {
				new L1Teleport().teleport(pc, newX, newY, mapId, pc.getHeading(), true);
				
			} else {
				new L1Teleport().teleport(pc, 32607, 32865, (short) 106, pc.getHeading(), true);
			}
			pc.getInventory().removeItem(l1iteminstance, 1);
			break;
		case 830007: //오만의 탑 7층 이동 주문서
			if (pc.getMapId() == 107) {
				new L1Teleport().teleport(pc, newX, newY, mapId, pc.getHeading(), true);
			
			} else {
				new L1Teleport().teleport(pc, 32618, 32866, (short) 107, pc.getHeading(), true);
			}
			pc.getInventory().removeItem(l1iteminstance, 1);
			break;
		case 830008: //오만의 탑 8층 이동 주문서
			if (pc.getMapId() == 108) {
				new L1Teleport().teleport(pc, newX, newY, mapId, pc.getHeading(), true);
				
			} else {
				new L1Teleport().teleport(pc, 32598, 32867, (short) 108, pc.getHeading(), true);
			}
			pc.getInventory().removeItem(l1iteminstance, 1);
			break;
		case 830009: //오만의 탑 9층 이동 주문서
			if (pc.getMapId() == 109) {
				new L1Teleport().teleport(pc, newX, newY, mapId, pc.getHeading(), true);
				
			} else {
				new L1Teleport().teleport(pc, 32609, 32866, (short) 109, pc.getHeading(), true);
			}
			pc.getInventory().removeItem(l1iteminstance, 1);
			break;
		case 830010: //오만의 탑 10층 이동 주문서
			if (pc.getMapId() == 110) {
				new L1Teleport().teleport(pc, newX, newY, mapId, pc.getHeading(), true);
				
			} else {
				new L1Teleport().teleport(pc, 32726, 32803, (short) 110, pc.getHeading(), true);
			}
			pc.getInventory().removeItem(l1iteminstance, 1);
			break;
		case 830011: //오만의 탑 정상 이동 주문서
			if (pc.getMapId() == 200) {
				new L1Teleport().teleport(pc, newX, newY, mapId, pc.getHeading(), true);
				
			} else {
				new L1Teleport().teleport(pc, 32657, 32797, (short) 111, pc.getHeading(), true);
			}
			pc.getInventory().removeItem(l1iteminstance, 1);
			break;
		}
	}
}
