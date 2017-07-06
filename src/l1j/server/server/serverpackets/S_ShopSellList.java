package l1j.server.server.serverpackets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import l1j.server.Config;
import l1j.server.server.Opcodes;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.ShopTable;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1TaxCalculator;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.shop.L1Shop;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1ShopItem;

public class S_ShopSellList extends ServerBasePacket {
	public S_ShopSellList(int objId, L1PcInstance pc) {
		writeC(Opcodes.S_BUY_LIST);
		writeD(objId);

		L1Object npcObj = L1World.getInstance().findObject(objId);
		if (!(npcObj instanceof L1NpcInstance)) {
			writeH(0);
			return;
		}
		int npcId = ((L1NpcInstance) npcObj).getNpcTemplate().get_npcId();

		L1TaxCalculator calc = new L1TaxCalculator(npcId);

		L1Shop shop = ShopTable.getInstance().get(npcId);
		List<L1ShopItem> shopItems = null;
		List<L1ShopItem> passList = new ArrayList<L1ShopItem>();
		try {
			shopItems = shop.getSellingItems();
		} catch (Exception e) {
			System.out.println("S_ShopSellList 오류 엔피시번호 : " + npcId);
		}
		if (shopItems != null) {
			// 시간 제한이 있는 아이템일경우 열람을 원하는 사용자에 인벤에서 동일한 아이템 찾아보기.
			Date date = new Date(0);
			for(L1ShopItem si : shopItems) {
				if(!si.isTimeLimit())
					continue;
				//
				date.setTime( pc.getFishingShopBuyTime_1() );
				int item_day = date.getDate();
				date.setTime( System.currentTimeMillis() );
				int current_day = date.getDate();
				if(item_day == current_day)
					passList.add(si);
			}
			//
			writeH(shopItems.size() - passList.size());
		} else {
			writeH(0);
			return;
		}

		L1ItemInstance dummy = new L1ItemInstance();
		L1ShopItem shopItem = null;
		L1Item item = null;
		L1Item template = null;
		for (int i = 0; i < shopItems.size(); i++) {
			shopItem = (L1ShopItem) shopItems.get(i);
			if(passList.contains(shopItem))
				continue;
			
			item = shopItem.getItem();
			int price = calc.layTax((int) (shopItem.getPrice() * Config.RATE_SHOP_SELLING_PRICE));

			int price1 = shopItem.getPrice();
			writeD(i);
			try {
				writeH(shopItem.getItem().getGfxId());
			} catch (Exception e) {
				System.out.println("엔피시 상점 오류 엔피시 번호 :" + npcId);
			}
			if ((npcId == 70035) || (npcId == 70041) || (npcId == 70042))
				writeD(price1);
			else {
				writeD(price);
			}
			if (shopItem.getPackCount() > 1){
				writeS(item.getNameId() + " (" + shopItem.getPackCount() + ")");
			}else if (shopItem.getEnchant() > 0){
				writeS("+" + shopItem.getEnchant() + " " + item.getName());
			}else if (shopItem.getItem().getMaxUseTime() > 0){
				writeS(item.getName() + " [" + item.getMaxUseTime() + "]");
			
			}else {
				if(item.getItemId() >= 140074 && item.getItemId() <= 140100)
					writeS("축복 "+item.getName());
				else if(item.getItemId() >= 240074 && item.getItemId() <= 240087)
					writeS("저주 "+item.getName());
				else
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
		writeH(7);//표기 타입부분
	}

	public byte[] getContent() throws IOException {
		return this._bao.toByteArray();
	}
}