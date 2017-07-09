package l1j.server.server.model.shop;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import l1j.server.Config;
import l1j.server.server.model.L1TaxCalculator;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.templates.L1ShopItem;

public class L1ShopBuyOrderList {
	private final L1Shop _shop;
	private final List<L1ShopBuyOrder> _list = new ArrayList<L1ShopBuyOrder>();
	private final L1TaxCalculator _taxCalc;

	private int _totalWeight = 0;
	private int _totalPrice = 0;
	private int _totalPriceTaxIncluded = 0;
	private int bugok = 0;

	/** 日付、時刻の記録 **/
	Calendar rightNow = Calendar.getInstance();
	int day = rightNow.get(Calendar.DATE);
	int hour = rightNow.get(Calendar.HOUR);
	int min = rightNow.get(Calendar.MINUTE);
	int year = rightNow.get(Calendar.YEAR);
	int month = rightNow.get(Calendar.MONTH) + 1;
	String totime = "[" + year + ":" + month + ":" + day + ":" + hour + ":" + min + "]";

	L1ShopBuyOrderList(L1Shop shop) {
		_shop = shop;
		_taxCalc = new L1TaxCalculator(shop.getNpcId());
	}

	public void add(int orderNumber, int count, L1PcInstance pc) {

		if (_shop.getSellingItems().size() < orderNumber) {
			return;
		}

		// arraylist size over flow exception.
		if (_shop.getSellingItems().size() <= 0 || _shop.getSellingItems().size() < orderNumber)
			return;

		L1ShopItem shopItem = _shop.getSellingItems().get(orderNumber);

		int price = (int) (shopItem.getPrice() * Config.RATE_SHOP_SELLING_PRICE);
		// オーバーフローチェック
		for (int j = 0; j < count; j++) {
			if (price * j < 0) {
				return;
			}
		}
		_totalPrice += price * count;
		_totalPriceTaxIncluded += _taxCalc.layTax(price) * count;
		_totalWeight += shopItem.getItem().getWeight() * count * shopItem.getPackCount();
		long totalprice = _totalPrice;

		// ** 店購入ビシャス守る **//

		if (count <= 0 || count > 9999) {
			pc.sendPackets(new S_Disconnect());
			bugok = 1;
			return;
		}
		if (totalprice < 0 || price < 0) { // ##### 43億のバグを防ぐ追加
			pc.sendPackets(new S_Disconnect());
			bugok = 1;
			return;
		}
		// ** 店購入ビシャス守る **//

		if (shopItem.getItem().isStackable()) {
			_list.add(new L1ShopBuyOrder(shopItem, count * shopItem.getPackCount(), orderNumber));
			return;
		}

		for (int i = 0; i < (count * shopItem.getPackCount()); i++) {
			_list.add(new L1ShopBuyOrder(shopItem, 1, orderNumber));
		}
	}

	public List<L1ShopBuyOrder> getList() {
		return _list;
	}

	// ** 店購入ビシャス守る **//
	public int BugOk() {
		return bugok;
	}

	// ** 店購入ビシャス守る **//

	public int getTotalWeight() {
		return _totalWeight;
	}

	public int getTotalPrice() {
		return _totalPrice;
	}

	public int getTotalPriceTaxIncluded() {
		return _totalPriceTaxIncluded;
	}

	L1TaxCalculator getTaxCalculator() {
		return _taxCalc;
	}

}
