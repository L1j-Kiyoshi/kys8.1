package l1j.server.server.model.item.function;

import static l1j.server.server.model.skill.L1SkillId.*;

import l1j.server.Config;
import l1j.server.server.ActionCodes;
import l1j.server.server.Controller.FishingTimeController;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.serverpackets.S_Fishing;
import l1j.server.server.serverpackets.S_Fishing2;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.types.Point;

public class Fishitem {

	public static void clickItem(L1PcInstance pc, int itemId, int fishX, int fishY, L1ItemInstance item, int itemObjectId) {
		int chargeCount = item.getChargeCount();

		if (pc.getMapId() != 4) {
			// ここで釣り竿を投げることができません。
			pc.sendPackets(new S_ServerMessage(1138));
			return;
		}
		if (pc.getLocation().getTileDistance(new Point(fishX, fishY)) > 13) {
			pc.sendPackets(new S_SystemMessage("\\fWもう少し近くで投げてください。"));
			return;
		}
		if (pc.hasSkillEffect(SHAPE_CHANGE)) { //
			pc.sendPackets(new S_SystemMessage("\\fW変身中の釣りは不可能です。"));
			pc.sendPackets(new S_SystemMessage("\\fWターバン解除後にビョンジュムに変身解除を直す必要があります。"));
			return;
		}

		if ((itemId == 41294 || itemId == 41305 || itemId == 41306 || itemId == 600229 || itemId == 9991) && chargeCount <= 0) {
			return;
		}
		if (pc.getInventory().getWeight100() > 82) { // 重量オーバー
			pc.sendPackets(new S_SystemMessage("重量が重すぎて釣りをすることができません。"));
			return;
		}
		if (pc.getInventory().getSize() >= 180) {
			pc.sendPackets(new S_ServerMessage(263));
			return;
		}
		int gab = 0;
		int heading = pc.getHeading(); // ● 方向：（0左上）（1。上）（
										// 2.偶像）（3。右）（4。右下）（5。ハ）（6。左下）（7。左）
		switch (heading) {
		case 0: // 上座
			gab = L1WorldMap.getInstance().getMap((short) 4).getOriginalTile(pc.getX(), pc.getY() - 1);
			break;
		case 1: // 上
			gab = L1WorldMap.getInstance().getMap((short) 4).getOriginalTile(pc.getX() + 1, pc.getY() - 1);
			break;
		case 2: // アイドル
			gab = L1WorldMap.getInstance().getMap((short) 4).getOriginalTile(pc.getX() + 1, pc.getY() - 1);
			break;
		case 3: // 右
			gab = L1WorldMap.getInstance().getMap((short) 4).getOriginalTile(pc.getX() + 1, pc.getY() + 1);
			break;
		case 4: // ウーハー
			gab = L1WorldMap.getInstance().getMap((short) 4).getOriginalTile(pc.getX(), pc.getY() + 1);
			break;
		case 5: // し
			gab = L1WorldMap.getInstance().getMap((short) 4).getOriginalTile(pc.getX() - 1, pc.getY() + 1);
			break;
		case 6: // 左下
			gab = L1WorldMap.getInstance().getMap((short) 4).getOriginalTile(pc.getX() - 1, pc.getY());
			break;
		case 7: // 左
			gab = L1WorldMap.getInstance().getMap((short) 4).getOriginalTile(pc.getX() - 1, pc.getY() - 1);
			break;
		}
		int fishGab = L1WorldMap.getInstance().getMap((short) 5490).getOriginalTile(fishX, fishY);
		int x = 33413;// キャッチた座標基準点
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
			// 成長の釣竿なら餌なしで可能。
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
				// 釣りをするためには、供給が必要です。
				pc.sendPackets(new S_ServerMessage(1137));
			}
		} else {
			// ここで釣り竿を投げることができません。
			pc.sendPackets(new S_ServerMessage(1138));
		}
	}

	public static void clickItem1(L1PcInstance pc, int itemId, int fishX, int fishY, L1ItemInstance item, int itemObjectId) {
		int chargeCount = item.getChargeCount();

		if (pc.getMapId() != 5490 || fishX <= 32704 || fishX >= 32831 || fishY <= 32768 || fishY >= 32895) {
			// ここで釣り竿を投げることができません。
			pc.sendPackets(new S_ServerMessage(1138));
			return;
		}

		if ((itemId == 41294 || itemId == 41305 || itemId == 41306 || itemId == 600229 || itemId == 9991) && chargeCount <= 0) {
			return;
		}
		if (pc.getInventory().getWeight100() > 82) { // 重量オーバー
			pc.sendPackets(new S_SystemMessage("重量が重すぎて釣りをすることができません。"));
			return;
		}
		if (pc.getInventory().getSize() >= 180) {
			pc.sendPackets(new S_ServerMessage(263));
			return;
		}
		int gab = 0;
		int heading = pc.getHeading(); // ● 方向：（0左上）（1。上）（
										// 2.偶像）（3。右）（4。右下）（5。ハ）（6。左下）（7。左）
		switch (heading) {
		case 0: //上座
			gab = L1WorldMap.getInstance().getMap((short) 5490).getOriginalTile(pc.getX(), pc.getY() - 5);
			break;
		case 1: // 上
			gab = L1WorldMap.getInstance().getMap((short) 5490).getOriginalTile(pc.getX() + 5, pc.getY() - 5);
			break;
		case 2: // アイドル
			gab = L1WorldMap.getInstance().getMap((short) 5490).getOriginalTile(pc.getX() + 5, pc.getY() - 5);
			break;
		case 3: // 右
			gab = L1WorldMap.getInstance().getMap((short) 5490).getOriginalTile(pc.getX() + 5, pc.getY() + 5);
			break;
		case 4: // ウーハー
			gab = L1WorldMap.getInstance().getMap((short) 5490).getOriginalTile(pc.getX(), pc.getY() + 5);
			break;
		case 5: // し
			gab = L1WorldMap.getInstance().getMap((short) 5490).getOriginalTile(pc.getX() - 5, pc.getY() + 5);
			break;
		case 6: // 左下
			gab = L1WorldMap.getInstance().getMap((short) 5490).getOriginalTile(pc.getX() - 5, pc.getY());
			break;
		case 7: // 左
			gab = L1WorldMap.getInstance().getMap((short) 5490).getOriginalTile(pc.getX() - 5, pc.getY() - 5);
			break;
		}
		int fishGab = L1WorldMap.getInstance().getMap((short) 5490).getOriginalTile(fishX, fishY);
		if (gab == 28 && fishGab == 28) {
			L1ItemInstance useItem = pc.getInventory().getItem(itemObjectId);
			if (useItem != null)
				pc._fishingRod = useItem;
			else {
				// 釣り竿がない状態で呼び出しても来ることイトヌンデそれに対する処理がダメイトオトね〜
				pc.sendPackets(new S_ServerMessage(1137));
				return;
			}
			// 成長の釣竿なら餌なしで可能。
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
				// 釣りをするためには、供給が必要です。
				pc.sendPackets(new S_ServerMessage(1137));
			}
		} else {
			// ここで釣り竿を投げることができません。
			pc.sendPackets(new S_ServerMessage(1138));
		}
	}
}
