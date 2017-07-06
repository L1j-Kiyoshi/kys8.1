package l1j.server.server.model.item.function;

import static l1j.server.server.model.skill.L1SkillId.SHAPE_CHANGE;
import l1j.server.Config;
import l1j.server.server.ActionCodes;
import l1j.server.server.Controller.FishingTimeController;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.serverpackets.S_Fishing;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_Fishing2;
import l1j.server.server.types.Point;

public class Fishitem {

	public static void clickItem(L1PcInstance pc, int itemId, int fishX, int fishY, L1ItemInstance item, int itemObjectId) {
		int chargeCount = item.getChargeCount();

		if (pc.getMapId() != 4) {
			// 여기에 낚싯대를 던질 수 없습니다.
			pc.sendPackets(new S_ServerMessage(1138));
			return;
		}
		if (pc.getLocation().getTileDistance(new Point(fishX, fishY)) > 13) {
			pc.sendPackets(new S_SystemMessage("\\fW조금 더 가까이서 던져주세요."));
			return;
		}
		if (pc.hasSkillEffect(SHAPE_CHANGE)) { //
			pc.sendPackets(new S_SystemMessage("\\fW변신중엔 낚시가 불가능합니다."));
			pc.sendPackets(new S_SystemMessage("\\fW터번해제후엔 변줌으로 변신해제를 다시해야합니다."));
			return;
		}

		if ((itemId == 41294 || itemId == 41305 || itemId == 41306 || itemId == 600229 || itemId == 9991) && chargeCount <= 0) {
			return;
		}
		if (pc.getInventory().getWeight100() > 82) { // 중량 오버
			pc.sendPackets(new S_SystemMessage("무게가 너무 무거워서 낚시를 할 수 없습니다."));
			return;
		}
		if (pc.getInventory().getSize() >= 180) {
			pc.sendPackets(new S_ServerMessage(263));
			return;
		}
		int gab = 0;
		int heading = pc.getHeading(); // ● 방향: (0.좌상)(1.상)(
										// 2.우상)(3.오른쪽)(4.우하)(5.하)(6.좌하)(7.좌)
		switch (heading) {
		case 0: // 상좌
			gab = L1WorldMap.getInstance().getMap((short) 4).getOriginalTile(pc.getX(), pc.getY() - 1);
			break;
		case 1: // 상
			gab = L1WorldMap.getInstance().getMap((short) 4).getOriginalTile(pc.getX() + 1, pc.getY() - 1);
			break;
		case 2: // 우상
			gab = L1WorldMap.getInstance().getMap((short) 4).getOriginalTile(pc.getX() + 1, pc.getY() - 1);
			break;
		case 3: // 오른쪽
			gab = L1WorldMap.getInstance().getMap((short) 4).getOriginalTile(pc.getX() + 1, pc.getY() + 1);
			break;
		case 4: // 우하
			gab = L1WorldMap.getInstance().getMap((short) 4).getOriginalTile(pc.getX(), pc.getY() + 1);
			break;
		case 5: // 하
			gab = L1WorldMap.getInstance().getMap((short) 4).getOriginalTile(pc.getX() - 1, pc.getY() + 1);
			break;
		case 6: // 좌하
			gab = L1WorldMap.getInstance().getMap((short) 4).getOriginalTile(pc.getX() - 1, pc.getY());
			break;
		case 7: // 좌
			gab = L1WorldMap.getInstance().getMap((short) 4).getOriginalTile(pc.getX() - 1, pc.getY() - 1);
			break;
		}
		int fishGab = L1WorldMap.getInstance().getMap((short) 5490).getOriginalTile(fishX, fishY);
		int x = 33413;// 잡아둔좌표 기준점
		int y = 32809;
		// if (gab == 28 && fishGab == 28) {
		if ((fishX >= x - 5 && fishX <= x + 5) && (fishY >= y - 8 && fishY <= y + 8)) {
			L1ItemInstance useItem = pc.getInventory().getItem(itemObjectId);
			if (useItem != null)
				pc._fishingRod = useItem;
			else {
				pc.sendPackets(new S_ServerMessage(1137));
				return;
			}
			// 성장의 낚싯대라면 미끼없이 가능.
			if (pc._fishingRod.getItemId() == 600229 || pc.getInventory().consumeItem(41295, 1)) {
				pc._fishingX = fishX;
				pc._fishingY = fishY;
				pc.sendPackets(new S_Fishing(pc.getId(), ActionCodes.ACTION_Fishing, fishX, fishY));
				pc.broadcastPacket(new S_Fishing(pc.getId(), ActionCodes.ACTION_Fishing, fishX, fishY));
				pc.setFishing(true);
				if (pc._fishingRod.getItemId() == 41293) {
					pc.setFishingTime(System.currentTimeMillis() + 240000);
					pc.sendPackets(new S_Fishing2(240));
				} else {
					item.setChargeCount(item.getChargeCount() - 1);
					pc.getInventory().updateItem(item, L1PcInventory.COL_CHARGE_COUNT);
					pc.setFishingTime(System.currentTimeMillis() + Config.FISH_TIME * 1000);
					pc.sendPackets(new S_Fishing2(Config.FISH_TIME));
				}
				FishingTimeController.getInstance().addMember(pc);
			} else {
				// 낚시를 하기 위해서는 먹이가 필요합니다.
				pc.sendPackets(new S_ServerMessage(1137));
			}
		} else {
			// 여기에 낚싯대를 던질 수 없습니다.
			pc.sendPackets(new S_ServerMessage(1138));
		}
	}

	public static void clickItem1(L1PcInstance pc, int itemId, int fishX, int fishY, L1ItemInstance item, int itemObjectId) {
		int chargeCount = item.getChargeCount();

		if (pc.getMapId() != 5490 || fishX <= 32704 || fishX >= 32831 || fishY <= 32768 || fishY >= 32895) {
			// 여기에 낚싯대를 던질 수 없습니다.
			pc.sendPackets(new S_ServerMessage(1138));
			return;
		}

		if ((itemId == 41294 || itemId == 41305 || itemId == 41306 || itemId == 600229 || itemId == 9991) && chargeCount <= 0) {
			return;
		}
		if (pc.getInventory().getWeight100() > 82) { // 중량 오버
			pc.sendPackets(new S_SystemMessage("무게가 너무 무거워서 낚시를 할 수 없습니다."));
			return;
		}
		if (pc.getInventory().getSize() >= 180) {
			pc.sendPackets(new S_ServerMessage(263));
			return;
		}
		int gab = 0;
		int heading = pc.getHeading(); // ● 방향: (0.좌상)(1.상)(
										// 2.우상)(3.오른쪽)(4.우하)(5.하)(6.좌하)(7.좌)
		switch (heading) {
		case 0: // 상좌
			gab = L1WorldMap.getInstance().getMap((short) 5490).getOriginalTile(pc.getX(), pc.getY() - 5);
			break;
		case 1: // 상
			gab = L1WorldMap.getInstance().getMap((short) 5490).getOriginalTile(pc.getX() + 5, pc.getY() - 5);
			break;
		case 2: // 우상
			gab = L1WorldMap.getInstance().getMap((short) 5490).getOriginalTile(pc.getX() + 5, pc.getY() - 5);
			break;
		case 3: // 오른쪽
			gab = L1WorldMap.getInstance().getMap((short) 5490).getOriginalTile(pc.getX() + 5, pc.getY() + 5);
			break;
		case 4: // 우하
			gab = L1WorldMap.getInstance().getMap((short) 5490).getOriginalTile(pc.getX(), pc.getY() + 5);
			break;
		case 5: // 하
			gab = L1WorldMap.getInstance().getMap((short) 5490).getOriginalTile(pc.getX() - 5, pc.getY() + 5);
			break;
		case 6: // 좌하
			gab = L1WorldMap.getInstance().getMap((short) 5490).getOriginalTile(pc.getX() - 5, pc.getY());
			break;
		case 7: // 좌
			gab = L1WorldMap.getInstance().getMap((short) 5490).getOriginalTile(pc.getX() - 5, pc.getY() - 5);
			break;
		}
		int fishGab = L1WorldMap.getInstance().getMap((short) 5490).getOriginalTile(fishX, fishY);
		if (gab == 28 && fishGab == 28) {
			L1ItemInstance useItem = pc.getInventory().getItem(itemObjectId);
			if (useItem != null)
				pc._fishingRod = useItem;
			else {
				// 낚싯대가 없는상태에서 호출도 올수잇는데 그에 대한 처리가 안되잇엇네요~
				pc.sendPackets(new S_ServerMessage(1137));
				return;
			}
			// 성장의 낚싯대라면 미끼없이 가능.
			if (pc._fishingRod.getItemId() == 600229 || pc.getInventory().consumeItem(41295, 1)) {
				pc._fishingX = fishX;
				pc._fishingY = fishY;
				pc.sendPackets(new S_Fishing(pc.getId(), ActionCodes.ACTION_Fishing, fishX, fishY));
				pc.broadcastPacket(new S_Fishing(pc.getId(), ActionCodes.ACTION_Fishing, fishX, fishY));
				pc.setFishing(true);
				if (pc._fishingRod.getItemId() == 41293) {
					pc.setFishingTime(System.currentTimeMillis() + 240000);
					pc.sendPackets(new S_Fishing2(240));
				} else {
					item.setChargeCount(item.getChargeCount() - 1);
					pc.getInventory().updateItem(item, L1PcInventory.COL_CHARGE_COUNT);
					pc.setFishingTime(System.currentTimeMillis() + Config.FISH_TIME * 1000);
					pc.sendPackets(new S_Fishing2(Config.FISH_TIME));
				}
				FishingTimeController.getInstance().addMember(pc);
			} else {
				// 낚시를 하기 위해서는 먹이가 필요합니다.
				pc.sendPackets(new S_ServerMessage(1137));
			}
		} else {
			// 여기에 낚싯대를 던질 수 없습니다.
			pc.sendPackets(new S_ServerMessage(1138));
		}
	}
}
