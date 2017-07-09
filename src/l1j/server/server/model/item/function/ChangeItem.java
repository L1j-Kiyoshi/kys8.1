package l1j.server.server.model.item.function;

import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_PremiumShopSellList;
import l1j.server.server.serverpackets.S_SystemMessage;

public class ChangeItem {
	public static void ClickToItem(L1PcInstance pc,L1ItemInstance item){
				int itemid = item.getItemId();
				int type=-1;
				/**
				 * type = 0 執行級武器
				 * 
				 */
				switch(itemid){
					case 61:
					case 12:
					case 134:
					case 86:
					case 202011:
					case 202012:
					case 202013:
					case 202014:
						type = 0;
						break;
				}
				if(type==-1){
					pc.sendPackets(new S_SystemMessage("交換することができないアイテムです"));
				}else{
					pc.sendPackets(new S_PremiumShopSellList(true,type)); //執行剣テスト				
					pc.setIsChangeItem(true);
					pc.setDefaultItem(item); //0が執行級武器
				}
		
	}
}
