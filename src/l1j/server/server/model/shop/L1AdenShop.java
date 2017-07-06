package l1j.server.server.model.shop;

import java.util.ArrayList;

import l1j.server.server.datatables.AdenShopTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Warehouse.PackageWarehouse;
import l1j.server.server.model.Warehouse.WarehouseManager;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1AdenShopItem;
import l1j.server.server.templates.L1Item;
import l1j.server.server.utils.IntRange;

public class L1AdenShop {

	private ArrayList<Item> buylist = new ArrayList<Item>();
	private boolean bugok = false;

	public L1AdenShop() {
	}

	private boolean NcoinSell(L1PcInstance pc, L1ShopBuyOrderList orderList) {
		int price = orderList.getTotalPrice();
		if (!IntRange.includes(price, 0, 50000000)) {
			pc.sendPackets(new S_SystemMessage("이상점은 한번에 50,000,000만 이상 사용할수 없습니다."));
			return false;
		}
		if (!pc.getInventory().checkItem(40308, price)) {
			pc.sendPackets(new S_SystemMessage("아데나가 부족합니다."));
			return false;
		}

		if (price <= 0 || price > 2000000000) {
			pc.sendPackets(new S_Disconnect());
			return false;
		}
		return true;
	}

	private void NcoinSellItems(L1PcInstance pc, L1ShopBuyOrderList orderList) {
		if (!pc.getInventory().consumeItem(40308, orderList.getTotalPrice())) {
			throw new IllegalStateException("구입에 필요한 아데나를 소비 할 수 없습니다.");
		}

		for (L1ShopBuyOrder order : orderList.getList()) {
			int itemId = order.getItem().getItemId();
			int enchant = order.getItem().getEnchant();
			int count = order.getCount();

			PackageWarehouse.itemshop(pc.getAccountName(), itemId,enchant,count);
			pc.saveInventory();
//			System.out.println("계정명 : "+ pc.getAccountName() + " 아이템번호 : "+ itemId + " 인첸트 : " + enchant + " 카운터 " + count);
		}
		pc.sendPackets(new S_SystemMessage("\\fY 아덴상점에서 구입하신 아이템은 TAB키를 눌러서 "));
		pc.sendPackets(new S_SystemMessage("\\fY 부가 아이템 창고에서 찾을수있습니다."));
	}
	
	private int _totalPrice = 0;

	public void add(int id, int count) {
		try {
			if (_totalPrice < 0)
				return;
			L1AdenShopItem item = AdenShopTable.getInstance().get(id);
			if (item == null)
				return;
			int price = item.getPrice();
			if (price == 0)
				return;
			_totalPrice += price * count;
			if (_totalPrice < 0 || _totalPrice >= 1000000000) {
				bugok = true;
				return;
			}
			if (count <= 0 || count > 50) {
				bugok = true;
				return;
			}
			Item listitem = new Item();
			listitem.itemid = id;
			listitem.count = count * (item.getPackCount() > 0 ? item.getPackCount() : 1);
			buylist.add(listitem);
		} catch (Exception e) {

		}
	}

	public boolean BugOk() {
		// TODO 자동 생성된 메소드 스텁
		return bugok;
	}

	public boolean commit(L1PcInstance pc) {
		if (pc.getNcoin() < _totalPrice) {
			pc.sendPackets(new S_SystemMessage("N코인이 부족합니다."), true);
			return false;
		}

		try {
			pc.addNcoin(-_totalPrice);
			pc.getNetConnection().getAccount().updateNcoin();
		} catch (Exception e) {}
		
		//PackageWarehouse pwh = WarehouseManager.getInstance().getSupplementaryService(pc.getAccountName());
		PackageWarehouse pwh = WarehouseManager.getInstance().getPackageWarehouse(pc.getAccountName());
		if (pwh == null)
			return false;
		for (Item listitem : buylist) {
			if (listitem.itemid == 0 || listitem.count == 0)
				continue;
			L1Item tempItem = ItemTable.getInstance().getTemplate(listitem.itemid);
			if (tempItem.isStackable()) {
				L1ItemInstance item = ItemTable.getInstance().createItem(listitem.itemid);
				item.setIdentified(true);
				item.setEnchantLevel(0);
				item.setCount(listitem.count);

				pwh.storeTradeItem(item);
			} else {
				L1ItemInstance item = null;
				int createCount;
				for (createCount = 0; createCount < listitem.count; createCount++) {
					item = ItemTable.getInstance().createItem(listitem.itemid);
					item.setIdentified(true);
					item.setEnchantLevel(0);
					pwh.storeTradeItem(item);
				}
			}
		}
		return true;
	}

	class Item {
		public int itemid = 0;
		public int count = 0;
	}

}
