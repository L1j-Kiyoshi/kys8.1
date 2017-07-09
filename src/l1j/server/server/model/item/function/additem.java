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
		case 3000035:// 戦闘支援箱
			if (pc.getInventory().checkItem(3000035, 1)) { // チェックされているアイテムと数量
				pc.getInventory().consumeItem(3000035, 1); // 削除されたアイテムと数量qrltrue）; 
				if (pc.isKnight()) {
					sealedSystem(pc, 40014, 10, 0, 0 ,0, true); // 容器
					sealedSystem(pc, 1121, 1, 7 ,129 , 3, true); 	// 7ナバル
					sealedSystem(pc, 22200, 1, 5 , 129 ,0, true); 	// 5パプ腕力
					sealedSystem(pc, 20049, 1, 5 , 129 ,0, true); 	// 5グムナル
					sealedSystem(pc, 22360, 1, 5 , 129 ,0, true); 	// 5ジツ
					sealedSystem(pc, 20085, 1, 5, 129,0, true); 	// 5ティー
					sealedSystem(pc, 222327, 1, 5, 129,0, true); 	// 5鋼ゲートル
					sealedSystem(pc, 20187, 1, 5 , 129 ,0, true); 	// 5岩場
					sealedSystem(pc, 20194, 1, 5 , 129 ,0, true); 	// 5スチールブーツ
					sealedSystem(pc, 22254, 1, 3 , 129 ,0, true); 	// 3守護が

//					封印システム(pc, 22003, 1, 1 , 129 ,0, true); // 古代闘士のがより
//					封印システム(pc, 22192, 1, 7 , 129 ,0, true); // 7ククルカンの盾
//					封印システム(pc, 20264, 1, 0 , 129 ,0, true); // 腕力のネックレス
//					封印システム(pc, 20317, 1, 0 , 129 ,0, true); // オーガのベルト

				}
				if (pc.isElf()) {
					sealedSystem(pc, 40068, 10, 0, 0 ,0, true); // ワッフル
					
					sealedSystem(pc, 1136, 1, 7 ,129 , 3, true); 	// 7楽章
					sealedSystem(pc, 22202, 1, 5 , 129 ,0, true); 	// 5パプ忍耐
					sealedSystem(pc, 20049, 1, 5 , 129 ,0, true); 	// 5グムナル
					sealedSystem(pc, 20017, 1, 7 , 129 ,0, true); 	// 7マミー			
					sealedSystem(pc, 20085, 1, 5, 129,0, true); 	// 5ティー
					sealedSystem(pc, 222327, 1, 5, 129,0, true); 	// 5鋼ゲートル
					sealedSystem(pc, 20190, 1, 2 , 129 ,0, true); 	// 2本村
					sealedSystem(pc, 222308, 1, 5 , 129 ,0, true); 	// 5速ブーツ
					sealedSystem(pc, 22000, 1, 3 , 129 ,0, true);   // 3ミョングンガより

//					봉인템(pc, 22003, 1, 1 , 129 ,0, true); // 古代闘士のがより
//					봉인템(pc, 22192, 1, 7 , 129 ,0, true); // 7ククルカンの盾
//					봉인템(pc, 20264, 1, 0 , 129 ,0, true); // 腕力のネックレス
//					봉인템(pc, 20317, 1, 0 , 129 ,0, true); // オーガのベルト
					sealedSystem(pc, 40748, 1000, 0 , 129 ,0, true); // オリハルコンの矢
				}
				
				if (pc.isDarkelf()) {
					sealedSystem(pc, 203018, 1, 7 ,129 , 3, true); 	// 7轟音の二刀流
					sealedSystem(pc, 22202, 1, 5 , 129 ,0, true); 	// 5パプ忍耐
					sealedSystem(pc, 20079, 1, 5 , 129 ,0, true); 	// 5ベムマン
					sealedSystem(pc, 22360, 1, 5 , 129 ,0, true); 	// 5ジツ
					sealedSystem(pc, 20085, 1, 5, 129,0, true); 	// 5ティー
					sealedSystem(pc, 222327, 1, 5, 129,0, true); 	// 5鋼ゲートル
					sealedSystem(pc, 20187, 1, 5 , 129 ,0, true); 	// 5岩場
					sealedSystem(pc, 222307, 1, 5 , 129 ,0, true); 	// 5腕力ブーツ
					sealedSystem(pc, 22254, 1, 3 , 129 ,0, true); 	// 3守護が


//					봉인템(pc, 22192, 1, 7 , 129 ,0, true); // 7ククルカンの盾
//					봉인템(pc, 20264, 1, 0 , 129 ,0, true); // 腕力のネックレス
//					봉인템(pc, 20317, 1, 0 , 129 ,0, true); // オーガのベルト

				}
				if (pc.isCrown()) {
					sealedSystem(pc, 54, 1, 7 ,129 , 3, true); 		// 7コゴム
					sealedSystem(pc, 22201, 1, 5 , 129 ,0, true); 	// 5パプ先見の明
					sealedSystem(pc, 20049, 1, 5 , 129 ,0, true); 	// 5グムナル
					sealedSystem(pc, 22360, 1, 5 , 129 ,0, true); 	// 5ジツ
					sealedSystem(pc, 20085, 1, 5, 129,0, true); 	// 5ティー
					sealedSystem(pc, 222327, 1, 5, 129,0, true); 	// 5鋼ゲートル
					sealedSystem(pc, 20187, 1, 5 , 129 ,0, true); 	// 5岩場
					sealedSystem(pc, 20194, 1, 5 , 129 ,0, true); 	// 5スチールブーツ
					sealedSystem(pc, 22254, 1, 3 , 129 ,0, true); 	// 3守護が

				}
				
				if (pc.isWarrior()) {
					sealedSystem(pc, 40014, 10, 0, 0 ,0, true); // 容器
					sealedSystem(pc, 203006, 1, 7 ,129 , 3, true); 		// 7台風斧2つ
					sealedSystem(pc, 203006, 1, 7 ,129 , 3, true);
					sealedSystem(pc, 22200, 1, 5 , 129 ,0, true); 	// 5パプ腕力
					sealedSystem(pc, 20049, 1, 5 , 129 ,0, true); 	// 5グムナル
					sealedSystem(pc, 22360, 1, 5 , 129 ,0, true); 	// 5ジツ
					sealedSystem(pc, 20085, 1, 5, 129,0, true); 	// 5ティー
					sealedSystem(pc, 222327, 1, 5, 129,0, true); 	// 5鋼ゲートル
					sealedSystem(pc, 20187, 1, 5 , 129 ,0, true); 	// 5岩場
					sealedSystem(pc, 222307, 1, 5 , 129 ,0, true); 	// 5腕力ブーツ
					sealedSystem(pc, 22254, 1, 3 , 129 ,0, true); 	// 3守護が
				}


				//
				if (pc.isDragonknight()) {
					sealedSystem(pc, 203017, 1, 7 ,129 , 3, true); 		// 7ソムチェ
					sealedSystem(pc, 22201, 1, 5 , 129 ,0, true); 		// 5パプ先見の明
					sealedSystem(pc, 20049, 1, 5 , 129 ,0, true); 		// 5グムナル
					sealedSystem(pc, 22360, 1, 5 , 129 ,0, true); 		// 5ジツ
					sealedSystem(pc, 20085, 1, 5, 129,0, true); 		// 5ティー
					sealedSystem(pc, 222327, 1, 5, 129,0, true); 		// 5鋼ゲートル
					sealedSystem(pc, 20187, 1, 5 , 129 ,0, true); 		// 5岩場
					sealedSystem(pc, 222307, 1, 5 , 129 ,0, true); 		// 5腕力ブーツ
					sealedSystem(pc, 22254, 1, 3 , 129 ,0, true); 		// 3守護が
				}
				if (pc.isBlackwizard()) {
					sealedSystem(pc, 1120, 1, 7 ,129 , 3, true); 		// 7ネンキ
					sealedSystem(pc, 22202, 1, 5 , 129 ,0, true); 		// 5パプ忍耐
					sealedSystem(pc, 20049, 1, 5 , 129 ,0, true); 		// 5グムナル
					sealedSystem(pc, 22360, 1, 5 , 129 ,0, true); 		// 5ジツ
					sealedSystem(pc, 20085, 1, 5, 129,0, true); 		// 5ティー
					sealedSystem(pc, 222327, 1, 5, 129,0, true); 		// 5鋼ゲートル
					sealedSystem(pc, 20187, 1, 5 , 129 ,0, true); 		// 5岩場
					sealedSystem(pc, 222309, 1, 5 , 129 ,0, true); 		//5知識ブーツ
					sealedSystem(pc, 22254, 1, 3 , 129 ,0, true); 		//3守護が
				}
				
				if (pc.isWizard()) {
					sealedSystem(pc, 1120, 1, 7 ,129 , 3, true); 		// 7ネンキ
					sealedSystem(pc, 22203, 1, 5 , 129 ,0, true); 		// 5パプ馬力
					sealedSystem(pc, 20050, 1, 5 , 129 ,0, true); 		// 5ウンナル
					sealedSystem(pc, 202022, 1, 5 , 129 ,0, true); 		// 5大魔法使いの帽子
					sealedSystem(pc, 20085, 1, 5, 129,0, true); 		// 5ティー
					sealedSystem(pc, 222327, 1, 5, 129,0, true); 		// 5鋼ゲートル
					sealedSystem(pc, 20274, 1, 5 , 129 ,0, true); 		// 5光馬場
					sealedSystem(pc, 222309, 1, 5 , 129 ,0, true); 		// 5知識ブーツ
					sealedSystem(pc, 22255, 1, 3 , 129 ,0, true); 		// 3ウィザードが
				}


				
			}
			break;
	}

}
	
	
	private static boolean sealedSystem(L1PcInstance pc, int item_id, int count, int EnchantLevel, int Bless, int attr, boolean identi) {
		// 封印システム(pc, 5000045, 1, 5, 128);
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
			} else { // 持つことができない場合は、地面に落とす処理のキャンセルはしない（不正防止）
				pc.sendPackets(new S_ServerMessage(82));
				// 重量ゲージが不足したり、インベントリがいっぱいよりにできません。
				return false;
			}
			pc.sendPackets(new S_ServerMessage(403, item.getLogName())); //
			return true;
		} else {
			return false;
		}
	}
	

}