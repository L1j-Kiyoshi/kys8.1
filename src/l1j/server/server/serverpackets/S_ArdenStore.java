package l1j.server.server.serverpackets;

import java.io.IOException;
import java.util.List;

import l1j.server.server.Opcodes;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.ShopTable;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.shop.L1Shop;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1ShopItem;

public class S_ArdenStore extends ServerBasePacket {
	
	public S_ArdenStore(int objId, L1PcInstance pc) {
		writeC(Opcodes.S_EXTENDED);
		writeD(objId);
		writeH(0);
		writeD(6000);
		writeH(0);
	} 
	
	
	
	public S_ArdenStore(L1PcInstance pc) {
		writeC(Opcodes.S_BUY_LIST);
		writeD(7626);
		int npcId = 7626;
		 //아덴상점 엔피씨번호 
	//	int npcId = 7626; //아덴상점 하고자하는 엔피씨번호 월드상에 없어도됨 


		L1Shop shop = ShopTable.getInstance().get(npcId);
		List<L1ShopItem> shopItems = shop.getSellingItems();
		writeH(shopItems.size());

		L1ItemInstance dummy = new L1ItemInstance();
		L1ShopItem shopItem = null;
		L1Item item = null;
		L1Item template = null;
		for (int i = 0; i < shopItems.size(); i++) {
			shopItem = (L1ShopItem) shopItems.get(i);
			item = shopItem.getItem();
			int price = shopItem.getPrice();
			writeD(i);
			writeH(shopItem.getItem().getGfxId());
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
			if (type < 0) {
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
		writeH(7);
	}

	public byte[] getContent() throws IOException {
		return this._bao.toByteArray();
	}
}