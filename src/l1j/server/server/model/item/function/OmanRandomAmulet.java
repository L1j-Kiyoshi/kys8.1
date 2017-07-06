package l1j.server.server.model.item.function;

import java.util.Random;

import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;

public class OmanRandomAmulet {
	private static Random _random = new Random(System.nanoTime());

	public static void clickItem(L1PcInstance pc, int itemId, L1ItemInstance l1iteminstance) {
		int 찬스 = _random.nextInt(100) + 1;
		switch (itemId) {
		case 830042: //혼돈의 오만의 탑 1층 이동 부적
		case 830052: //변이된 오만의 탑 1층 이동 부적
			if (찬스 <= 35) {
				지급(pc, 830022, 1, 0); 
			} else {
				지급(pc, 830012, 1, 0); 
			}
			pc.getInventory().removeItem(l1iteminstance, 1);
			break;
		case 830043: //혼돈의 오만의 탑 2층 이동 부적
		case 830053: //변이된 오만의 탑 2층 이동 부적
			if (찬스 <= 35) {
				지급(pc, 830023, 1, 0); 
			} else {
				지급(pc, 830013, 1, 0); 
			}
			pc.getInventory().removeItem(l1iteminstance, 1);
			break;
		case 830044: //혼돈의 오만의 탑 3층 이동 부적
		case 830054: //변이된 오만의 탑 3층 이동 부적
			if (찬스 <= 35) {
				지급(pc, 830024, 1, 0); 
			} else {
				지급(pc, 830014, 1, 0); 
			}
			pc.getInventory().removeItem(l1iteminstance, 1);
			break;
		case 830045: //혼돈의 오만의 탑 4층 이동 부적
		case 830055: //변이된 오만의 탑 4층 이동 부적
			if (찬스 <= 35) {
				지급(pc, 830025, 1, 0); 
			} else {
				지급(pc, 830015, 1, 0); 
			}
			pc.getInventory().removeItem(l1iteminstance, 1);
			break;
		case 830046: //혼돈의 오만의 탑 5층 이동 부적
		case 830056: //변이된 오만의 탑 5층 이동 부적
			if (찬스 <= 35) {
				지급(pc, 830026, 1, 0); 
			} else {
				지급(pc, 830016, 1, 0); 
			}
			pc.getInventory().removeItem(l1iteminstance, 1);
			break;
		case 830047: //혼돈의 오만의 탑 6층 이동 부적
		case 830057: //변이된 오만의 탑 6층 이동 부적
			if (찬스 <= 35) {
				지급(pc, 830027, 1, 0); 
			} else {
				지급(pc, 830017, 1, 0); 
			}
			pc.getInventory().removeItem(l1iteminstance, 1);
			break;
		case 830048: //혼돈의 오만의 탑 7층 이동 부적
		case 830058: //변이된 오만의 탑 7층 이동 부적
			if (찬스 <= 35) {
				지급(pc, 830028, 1, 0);
			} else {
				지급(pc, 830018, 1, 0);
			}
			pc.getInventory().removeItem(l1iteminstance, 1);
			break;
		case 830049: //혼돈의 오만의 탑 8층 이동 부적
		case 830059: //변이된 오만의 탑 8층 이동 부적
			if (찬스 <= 35) {
				지급(pc, 830029, 1, 0);
			} else {
				지급(pc, 830019, 1, 0);
			}
			pc.getInventory().removeItem(l1iteminstance, 1);
			break;
		case 830050: //혼돈의 오만의 탑 9층 이동 부적
		case 830060: //변이된 오만의 탑 9층 이동 부적
			if (찬스 <= 35) {
				지급(pc, 830030, 1, 0);
			} else {
				지급(pc, 830020, 1, 0);
			}
			pc.getInventory().removeItem(l1iteminstance, 1);
			break;
		case 830051: //혼돈의 오만의 탑 10층 이동 부적
		case 830061: //변이된 오만의 탑 10층 이동 부적
			if (찬스 <= 35) {
				지급(pc, 830031, 1, 0);
			} else {
				지급(pc, 830021, 1, 0);
			}
			pc.getInventory().removeItem(l1iteminstance, 1);
			break;
			
		}
	}
	
	private static boolean 지급(L1PcInstance pc, int item_id, int count, int EnchantLevel) {
		L1ItemInstance item = ItemTable.getInstance().createItem(item_id);
		if (item != null) {
			item.setCount(count);
			item.setEnchantLevel(EnchantLevel);
			item.setIdentified(true);
			if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
				pc.getInventory().storeItem(item);
			} else {
				pc.sendPackets(new S_ServerMessage(82));
				// 무게 게이지가 부족하거나 인벤토리가 꽉차서 더 들 수 없습니다.
				return false;
			}
			pc.sendPackets(new S_ServerMessage(403, item.getLogName())); 
			return true;
		} else {
			return false;
		}
	}
}
