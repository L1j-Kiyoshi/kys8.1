package l1j.server.server.templates;

import l1j.server.server.datatables.ItemTable;

public class L1ShopItem {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	private final int _itemId;

	private final L1Item _item;

	private final int _price;

	private final int _packCount;
	
	private final int _enchant;
	
	private int _count;
	
	private boolean timeLimit;

	public L1ShopItem(int itemId, int price, int packCount) {
		this(itemId, price, packCount, 0, false);
	}
	
	public L1ShopItem(int itemId, int price, int packCount, int enchant, boolean timeLimit) {
		_itemId = itemId;
		_item = ItemTable.getInstance().getTemplate(itemId);
		_price = price;
		_packCount = packCount;
		_enchant = enchant;
		_count = 1;
		this.timeLimit = timeLimit;
		timeLimit = false;
	}

	public int get_count() {
		return _count;
	}

	public void set_count(int _count) {
		this._count = _count;
	}

	public int getItemId() {
		return _itemId;
	}

	public L1Item getItem() {
		return _item;
	}

	public int getPrice() {
		return _price;
	}

	public int getPackCount() {
		return _packCount;
	}
	public int getEnchant() {
		return _enchant;
	}

	public int getCount() {
		return _count;
	}

	public void setCount(int i) {
		_count = i;
	}

	public boolean isTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(boolean timeLimit) {
		this.timeLimit = timeLimit;
	}
}

