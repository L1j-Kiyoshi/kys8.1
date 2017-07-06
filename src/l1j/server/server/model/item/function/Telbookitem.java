package l1j.server.server.model.item.function;

import static l1j.server.server.model.skill.L1SkillId.MASS_TELEPORT;

import java.util.Random;

import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1BookMark;

public class Telbookitem {

	public static void clickItem(L1PcInstance pc, int itemId, int bookmark_x,int bookmark_y,short bookmark_mapid, L1ItemInstance l1iteminstance) {
		if (pc.get_DuelLine() != 0) {
			pc.sendPackets(new S_SystemMessage("배틀존지역에서 사용할 수 없습니다."));
			return;
		}
		if(pc.isDead())
			return;
		if(pc.getCurrentHp()<1)
			return;
		
		if (bookmark_x != 0) {
			boolean isGetBookMark = false;
			for (L1BookMark book : pc.getBookMarkArray()) {
				if (book.getLocX() == bookmark_x && book.getLocY() == bookmark_y && book.getMapId() == bookmark_mapid) {
					isGetBookMark = true;
					break;
				}
			}
			if (isGetBookMark && (pc.getMap().isEscapable() || pc.isGm())) {
				if (itemId == 40086) {
					for (L1PcInstance member : L1World.getInstance().getVisiblePlayer(pc)) {
						if (pc.getLocation().getTileLineDistance(member.getLocation()) <= 3
								&& member.getClanid() == pc.getClanid() && pc.getClanid() != 0
								&& member.getId() != pc.getId() && !member.isPrivateShop()&& !member.isAutoClanjoin()) {
							new L1Teleport().teleport(member, bookmark_x, bookmark_y, bookmark_mapid, member.getHeading(), true);
						}
					}
				}
				new L1Teleport().teleport(pc, bookmark_x, bookmark_y, bookmark_mapid, pc.getHeading(),true);
				pc.getInventory().removeItem(l1iteminstance, 1);
			} else {
				pc.sendPackets(new S_ServerMessage(79));
			}
		} else {
			// 조건검색
			if(pc.getMapId()>=101 && pc.getMapId()<=110) {
				int find_item_ids[] = {
						830022, 	// 1층
						830023, 	// 2층 
						830024, 	// 3층 
						830025, 	// 4층 
						830026, 	// 5층 
						830027, 	// 6층 
						830028, 	// 7층 
						830029, 	// 8층 
						830030, 	// 9층
						830031   // 10층
						
				};
				L1ItemInstance findItem = pc.getInventory().findItemId(find_item_ids[pc.getMapId() - 101]);
				L1ItemInstance findItem1 = pc.getInventory().findItemId(560028);
				if(findItem != null || findItem1 != null ){
					toActive(pc, itemId, l1iteminstance, 0);
				}
				else{
					pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
					pc.sendPackets(new S_ServerMessage(276));
				}					
			} else {
				//
				if (pc.getMap().isTeleportable() || pc.isGm()) {
					toActive(pc, itemId, l1iteminstance, 0);

				} else {
					pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
					pc.sendPackets(new S_ServerMessage(276));
				}
			}
		}
		pc.cancelAbsoluteBarrier();
	}
	
	public static void toActive(L1PcInstance pc, int itemId, L1ItemInstance l1iteminstance, int skillId) {
		L1Location newLocation = pc.getLocation().randomLocation(200, true);
		int newX = newLocation.getX();
		int newY = newLocation.getY();
		short mapId = (short) newLocation.getMapId();
		
		if (itemId == 40086) {
			for (L1PcInstance member : L1World.getInstance().getVisiblePlayer(pc)) {
				if (pc.getLocation().getTileLineDistance(member.getLocation()) <= 3
						&& member.getClanid() == pc.getClanid() && pc.getClanid() != 0
						&& member.getId() != pc.getId() && !member.isPrivateShop()&& !member.isAutoClanjoin()) {
					new L1Teleport().teleport(member, newX, newY, mapId, member.getHeading(),
							true);
				}
			}
		}
		if (skillId == MASS_TELEPORT) {
			Random random = new Random();
			L1Map map = L1WorldMap.getInstance().getMap(mapId);
			for (L1PcInstance member : L1World.getInstance().getVisiblePlayer(pc, 3)) {
				if (pc.getClanid() != 0 && member.getClanid() == pc.getClanid()
						&& member.getId() != pc.getId() && !member.isPrivateShop()&& !member.isAutoClanjoin()) {
					int newX2 = newX + random.nextInt(3) + 1;
					int newY2 = newY + random.nextInt(3) + 1;
					if (map.isInMap(newX2, newY2) && map.isPassable(newX2, newY2)) {
						new L1Teleport().teleport(member, newX2, newY2, mapId, member.getHeading(),
								true);
					} else {
						new L1Teleport().teleport(member, newX, newY, mapId, member.getHeading(),
								true);
					}
				}
			}
		}
		
		new L1Teleport().teleport(pc, newX, newY, mapId, pc.getHeading(), true);
		
		if (l1iteminstance != null) {
			pc.getInventory().removeItem(l1iteminstance, 1);
		}
	}
}