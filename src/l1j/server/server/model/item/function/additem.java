package l1j.server.server.model.item.function;

import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;

public class additem {

	public static void clickItem(L1PcInstance pc, int itemId, L1ItemInstance l1iteminstance) {

		switch (itemId) {
		case 3000035:// 전투 지원상자
			if (pc.getInventory().checkItem(3000035, 1)) { // 체크 되는 아이템과 수량
				pc.getInventory().consumeItem(3000035, 1); // 삭제되는 아이템과 수량qrltrue); 
				if (pc.isKnight()) {
					봉인템(pc, 40014, 10, 0, 0 ,0, true); // 용기
					봉인템(pc, 1121, 1, 7 ,129 , 3, true); 	// 7나발
					봉인템(pc, 22200, 1, 5 , 129 ,0, true); 	// 5파푸완력
					봉인템(pc, 20049, 1, 5 , 129 ,0, true); 	// 5금날
					봉인템(pc, 22360, 1, 5 , 129 ,0, true); 	// 5지투
					봉인템(pc, 20085, 1, 5, 129,0, true); 	// 5티
					봉인템(pc, 222327, 1, 5, 129,0, true); 	// 5강철각반
					봉인템(pc, 20187, 1, 5 , 129 ,0, true); 	// 5암장
					봉인템(pc, 20194, 1, 5 , 129 ,0, true); 	// 5강철부츠
					봉인템(pc, 22254, 1, 3 , 129 ,0, true); 	// 3수호가더

//					봉인템(pc, 22003, 1, 1 , 129 ,0, true); // 고대투사의가더
//					봉인템(pc, 22192, 1, 7 , 129 ,0, true); // 7쿠쿨칸의방패
//					봉인템(pc, 20264, 1, 0 , 129 ,0, true); // 완력목걸이
//					봉인템(pc, 20317, 1, 0 , 129 ,0, true); // 오우거의벨트

				}
				if (pc.isElf()) {
					봉인템(pc, 40068, 10, 0, 0 ,0, true); // 와퍼
					
					봉인템(pc, 1136, 1, 7 ,129 , 3, true); 	// 7악장
					봉인템(pc, 22202, 1, 5 , 129 ,0, true); 	// 5파푸인내
					봉인템(pc, 20049, 1, 5 , 129 ,0, true); 	// 5금날
					봉인템(pc, 20017, 1, 7 , 129 ,0, true); 	// 7머미			
					봉인템(pc, 20085, 1, 5, 129,0, true); 	// 5티
					봉인템(pc, 222327, 1, 5, 129,0, true); 	// 5강철각반
					봉인템(pc, 20190, 1, 2 , 129 ,0, true); 	// 2혼손
					봉인템(pc, 222308, 1, 5 , 129 ,0, true); 	// 5민첩부츠
					봉인템(pc, 22000, 1, 3 , 129 ,0, true);   // 3명궁가더

//					봉인템(pc, 22003, 1, 1 , 129 ,0, true); // 고대투사의가더
//					봉인템(pc, 22192, 1, 7 , 129 ,0, true); // 7쿠쿨칸의방패
//					봉인템(pc, 20264, 1, 0 , 129 ,0, true); // 완력목걸이
//					봉인템(pc, 20317, 1, 0 , 129 ,0, true); // 오우거의벨트
					봉인템(pc, 40748, 1000, 0 , 129 ,0, true); // 오리하루콘의 화살
				}
				
				if (pc.isDarkelf()) {
					봉인템(pc, 203018, 1, 7 ,129 , 3, true); 	// 7포효의 이도류
					봉인템(pc, 22202, 1, 5 , 129 ,0, true); 	// 5파푸인내
					봉인템(pc, 20079, 1, 5 , 129 ,0, true); 	// 5뱀망
					봉인템(pc, 22360, 1, 5 , 129 ,0, true); 	// 5지투
					봉인템(pc, 20085, 1, 5, 129,0, true); 	// 5티
					봉인템(pc, 222327, 1, 5, 129,0, true); 	// 5강철각반
					봉인템(pc, 20187, 1, 5 , 129 ,0, true); 	// 5암장
					봉인템(pc, 222307, 1, 5 , 129 ,0, true); 	// 5완력부츠
					봉인템(pc, 22254, 1, 3 , 129 ,0, true); 	// 3수호가더


//					봉인템(pc, 22192, 1, 7 , 129 ,0, true); // 7쿠쿨칸의방패
//					봉인템(pc, 20264, 1, 0 , 129 ,0, true); // 완력목걸이
//					봉인템(pc, 20317, 1, 0 , 129 ,0, true); // 오우거의벨트

				}
				if (pc.isCrown()) {
					봉인템(pc, 54, 1, 7 ,129 , 3, true); 		// 7커검
					봉인템(pc, 22201, 1, 5 , 129 ,0, true); 	// 5파푸예지력
					봉인템(pc, 20049, 1, 5 , 129 ,0, true); 	// 5금날
					봉인템(pc, 22360, 1, 5 , 129 ,0, true); 	// 5지투
					봉인템(pc, 20085, 1, 5, 129,0, true); 	// 5티
					봉인템(pc, 222327, 1, 5, 129,0, true); 	// 5강철각반
					봉인템(pc, 20187, 1, 5 , 129 ,0, true); 	// 5암장
					봉인템(pc, 20194, 1, 5 , 129 ,0, true); 	// 5강철부츠
					봉인템(pc, 22254, 1, 3 , 129 ,0, true); 	// 3수호가더

//					봉인템(pc, 22003, 1, 1 , 129 ,0, true); // 고대투사의가더
//					봉인템(pc, 22192, 1, 7 , 129 ,0, true); // 7쿠쿨칸의방패
//					봉인템(pc, 20264, 1, 0 , 129 ,0, true); // 완력목걸이
//					봉인템(pc, 20317, 1, 0 , 129 ,0, true); // 오우거의벨트

				}
				
				if (pc.is전사()) {
					봉인템(pc, 40014, 10, 0, 0 ,0, true); // 용기
					봉인템(pc, 203006, 1, 7 ,129 , 3, true); 		// 7태풍도끼2개
					봉인템(pc, 203006, 1, 7 ,129 , 3, true);
					봉인템(pc, 22200, 1, 5 , 129 ,0, true); 	// 5파푸완력
					봉인템(pc, 20049, 1, 5 , 129 ,0, true); 	// 5금날
					봉인템(pc, 22360, 1, 5 , 129 ,0, true); 	// 5지투
					봉인템(pc, 20085, 1, 5, 129,0, true); 	// 5티
					봉인템(pc, 222327, 1, 5, 129,0, true); 	// 5강철각반
					봉인템(pc, 20187, 1, 5 , 129 ,0, true); 	// 5암장
					봉인템(pc, 222307, 1, 5 , 129 ,0, true); 	// 5완력부츠
					봉인템(pc, 22254, 1, 3 , 129 ,0, true); 	// 3수호가더

//					봉인템(pc, 22003, 1, 1 , 129 ,0, true); // 고대투사의가더
//					봉인템(pc, 22192, 1, 7 , 129 ,0, true); // 7쿠쿨칸의방패
//					봉인템(pc, 20264, 1, 0 , 129 ,0, true); // 완력목걸이
//					봉인템(pc, 20317, 1, 0 , 129 ,0, true); // 오우거의벨트

				}


				//
				if (pc.isDragonknight()) {
					봉인템(pc, 203017, 1, 7 ,129 , 3, true); 		// 7섬체
					봉인템(pc, 22201, 1, 5 , 129 ,0, true); 		// 5파푸예지력
					봉인템(pc, 20049, 1, 5 , 129 ,0, true); 		// 5금날
					봉인템(pc, 22360, 1, 5 , 129 ,0, true); 		// 5지투
					봉인템(pc, 20085, 1, 5, 129,0, true); 		// 5티
					봉인템(pc, 222327, 1, 5, 129,0, true); 		// 5강철각반
					봉인템(pc, 20187, 1, 5 , 129 ,0, true); 		// 5암장
					봉인템(pc, 222307, 1, 5 , 129 ,0, true); 		// 5완력부츠
					봉인템(pc, 22254, 1, 3 , 129 ,0, true); 		// 3수호가더
//					봉인템(pc, 22003, 1, 1 , 129 ,0, true); // 고대투사의가더
//					봉인템(pc, 22192, 1, 7 , 129 ,0, true); // 7쿠쿨칸의방패
//					봉인템(pc, 20264, 1, 0 , 129 ,0, true); // 완력목걸이
//					봉인템(pc, 20317, 1, 0 , 129 ,0, true); // 오우거의벨트

				}
				if (pc.isBlackwizard()) {
					봉인템(pc, 1120, 1, 7 ,129 , 3, true); 		// 7냉키
					봉인템(pc, 22202, 1, 5 , 129 ,0, true); 		// 5파푸인내
					봉인템(pc, 20049, 1, 5 , 129 ,0, true); 		// 5금날
					봉인템(pc, 22360, 1, 5 , 129 ,0, true); 		// 5지투
					봉인템(pc, 20085, 1, 5, 129,0, true); 		// 5티
					봉인템(pc, 222327, 1, 5, 129,0, true); 		// 5강철각반
					봉인템(pc, 20187, 1, 5 , 129 ,0, true); 		// 5암장
					봉인템(pc, 222309, 1, 5 , 129 ,0, true); 		// 5지식부츠
					봉인템(pc, 22254, 1, 3 , 129 ,0, true); 		// 3수호가더
//					봉인템(pc, 22003, 1, 1 , 129 ,0, true); // 고대투사의가더
//					봉인템(pc, 22192, 1, 7 , 129 ,0, true); // 7쿠쿨칸의방패
//					봉인템(pc, 20264, 1, 0 , 129 ,0, true); // 완력목걸이
//					봉인템(pc, 20317, 1, 0 , 129 ,0, true); // 오우거의벨트

				}
				
				if (pc.isWizard()) {
					봉인템(pc, 1120, 1, 7 ,129 , 3, true); 		// 7냉키
					봉인템(pc, 22203, 1, 5 , 129 ,0, true); 		// 5파푸마력
					봉인템(pc, 20050, 1, 5 , 129 ,0, true); 		// 5은날
					봉인템(pc, 202022, 1, 5 , 129 ,0, true); 		// 5대마법사모자
					봉인템(pc, 20085, 1, 5, 129,0, true); 		// 5티
					봉인템(pc, 222327, 1, 5, 129,0, true); 		// 5강철각반
					봉인템(pc, 20274, 1, 5 , 129 ,0, true); 		// 5빛마장
					봉인템(pc, 222309, 1, 5 , 129 ,0, true); 		// 5지식부츠
					봉인템(pc, 22255, 1, 3 , 129 ,0, true); 		// 3마법사가더
					

//					봉인템(pc, 22003, 1, 1 , 129 ,0, true); // 고대투사의가더
//					봉인템(pc, 22192, 1, 7 , 129 ,0, true); // 7쿠쿨칸의방패
//					봉인템(pc, 20264, 1, 0 , 129 ,0, true); // 완력목걸이
//					봉인템(pc, 20317, 1, 0 , 129 ,0, true); // 오우거의벨트

				}


				
			}
			break;
	}

}
	
	
	private static boolean 봉인템(L1PcInstance pc, int item_id, int count, int EnchantLevel, int Bless, int attr, boolean identi) {
		// 봉인템(pc, 5000045, 1, 5, 128);
		L1ItemInstance item = ItemTable.getInstance().createItem(item_id);
		if (item != null) {
			item.setCount(count);
			item.setIdentified(identi);
			item.setEnchantLevel(EnchantLevel);
			item.setAttrEnchantLevel(attr);
			item.setIdentified(true);
			if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
				pc.getInventory().storeItem(item);
				item.setBless(Bless);
				pc.getInventory().updateItem(item, L1PcInventory.COL_BLESS);
				pc.getInventory().saveItem(item, L1PcInventory.COL_BLESS);
			} else { // 가질 수 없는 경우는 지면에 떨어뜨리는 처리의 캔슬은 하지 않는다(부정 방지)
				pc.sendPackets(new S_ServerMessage(82));
				// 무게 게이지가 부족하거나 인벤토리가 꽉차서 더 들 수 없습니다.
				return false;
			}
			pc.sendPackets(new S_ServerMessage(403, item.getLogName())); //
			return true;
		} else {
			return false;
		}
	}
	

}