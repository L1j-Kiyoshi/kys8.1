package l1j.server.server.serverpackets;

import java.io.IOException;
import java.util.List;

import l1j.server.server.Opcodes;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.ShopTable;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.shop.L1Shop;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1ShopItem;

public class S_PremiumShopSellList extends ServerBasePacket {

	/**
	 * 店の物件一覧を表示する。キャラクターがBUYボタンを押したときに送る。
	 */
	public S_PremiumShopSellList(boolean bb,int types){
		writeC(Opcodes.S_BUY_LIST);
		writeD(0);
		int npcId;
		switch(types){ //0 執行級 
			case 0:
				npcId = 90000;
				break;
				
			default:
					npcId = 0;
					break;
		}
		L1Shop shop = ShopTable.getInstance().get(npcId);
		List shopItems = null;
		try {
			shopItems = shop.getSellingItems();
		} catch (Exception e) {
		}
		if (shopItems != null) {
			writeH(shopItems.size());
		} else {
			writeH(0);
			return;
		}

		// L1ItemInstanceのgetStatusBytesを利用するため
		L1ItemInstance dummy = new L1ItemInstance();
		L1ShopItem shopItem = null;
		L1Item item = null;
		L1Item template = null;
		for (int i = 0; i < shopItems.size(); i++) {
			shopItem = (L1ShopItem) shopItems.get(i);
			item = shopItem.getItem();
			int price = shopItem.getPrice();
			writeD(i);
			try {
				writeH(shopItem.getItem().getGfxId());
			} catch (Exception e) {
			}
			writeD(price);
			if (shopItem.getPackCount() > 1) {
				writeS(item.getName() + " (" + shopItem.getPackCount() + ")");
			} else if (shopItem.getEnchant() > 0) {
				writeS("+" + shopItem.getEnchant() + " " + item.getName());
			} else if (shopItem.getItem().getMaxUseTime() > 0) {
				writeS(item.getName() + " [" + item.getMaxUseTime() + "]");
			} else {
				writeS(item.getName());
			}
			int type = shopItem.getItem().getUseType();
			if (type < 0){
				type = 0;
			}
			writeD(type);
			template = ItemTable.getInstance().getTemplate(item.getItemId());
			if (template == null) {
				writeC(0);
			} else {
				dummy.setItem(template);
				byte[] status = dummy.getStatusBytes();
				writeC(status.length);
				for (byte b : status) {
					writeC(b);
				}
			}
		}
			writeC(73);
			writeC(58);
	}
	public S_PremiumShopSellList(int objId) {
		writeC(Opcodes.S_BUY_LIST);
		writeD(objId);
		

		L1Object npcObj = L1World.getInstance().findObject(objId);
		if (!(npcObj instanceof L1NpcInstance)) {
			writeH(0);
			return;
		}
		int npcId = ((L1NpcInstance) npcObj).getNpcTemplate().get_npcId();

		L1Shop shop = ShopTable.getInstance().get(npcId);
		List shopItems = null;
		try {
			shopItems = shop.getSellingItems();
		} catch (Exception e) {
			System.out.println("S_PremiumShopSellListエラーエンピシ番号：" + npcId);
		}
		if (shopItems != null) {
			writeH(shopItems.size());
		} else {
			writeH(0);
			return;
		}

		//L1ItemInstanceのgetStatusBytesを利用するため
		L1ItemInstance dummy = new L1ItemInstance();
		L1ShopItem shopItem = null;
		L1Item item = null;
		L1Item template = null;
		for (int i = 0; i < shopItems.size(); i++) {
			shopItem = (L1ShopItem) shopItems.get(i);
			item = shopItem.getItem();
			int price = shopItem.getPrice();
			writeD(i);
			try {
				writeH(shopItem.getItem().getGfxId());
			} catch (Exception e) {
				System.out.println("エンピシ店エラーエンピシ番号：" + npcId);
			}
			writeD(price);
			if (shopItem.getPackCount() > 1) {
				writeS(item.getName() + " (" + shopItem.getPackCount() + ")");
			} else if (shopItem.getEnchant() > 0) {
				writeS("+" + shopItem.getEnchant() + " " + item.getName());
			} else if (shopItem.getItem().getMaxUseTime() > 0) {
				writeS(item.getName() + " [" + item.getMaxUseTime() + "]");
			} else {
				writeS(item.getName());
			}
			int type = shopItem.getItem().getUseType();
			if (type < 0){
				type = 0;
			}
			writeD(type);
			template = ItemTable.getInstance().getTemplate(item.getItemId());
			if (template == null) {
				writeC(0);
			} else {
				dummy.setItem(template);
				byte[] status = dummy.getStatusBytes();
				writeC(status.length);
				for (byte b : status) {
					writeC(b);
				}
			}
		}
		//探傷ある 
		if ((npcId ==7200002)){
			writeC(253);
			writeC(255);
	    //ベリー 
		}else if ((npcId ==7000077)){ 
			writeC(73);
			writeC(58);
		// 不明 0/0	
		}else if ((npcId == 900107)) { // 満月の定期のに？。
			//writeC(0x2c08); //desc番号だが..ならないね。（笑）
			writeC(255);
			writeC(255);
			writeC(0);
			writeC(0);
		// 金色の羽
		} else if ((npcId == 6000002)){
			writeH(0x3ccf);
		} else if ((npcId >= 7210061 &&  npcId <= 7210070)){
			writeH(0x3DE4);
		//15844
		} else {//その他羽で表示
			writeC(111);
			writeC(10);
		}
	}

	@Override
	public byte[] getContent() throws IOException {
		return _bao.toByteArray();
	}
}
