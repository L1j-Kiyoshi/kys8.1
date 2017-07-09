package l1j.server.server.model.item.function;

import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.RestoreItemTable;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1RestoreItemInstance;
import l1j.server.server.serverpackets.S_Message_YN;

public class RestoreItem {
	
		public static void ClickItem(L1PcInstance pc){
			L1RestoreItemInstance item = RestoreItemTable.getInstance().getRestoreItemInstance(pc.getId());
			System.out.println("PCID : "+pc.getId()+" itemid : "+item.getItemId());
			
			String itemName =  " "+ItemTable.getInstance().findItemIdByName(item.getItemId());
			String msg = "+"+item.getEnchantLevel()+itemName+"を回復希望ゲットか？";
			// pc.sendPackets(new S_Message_YN(2000, "+"+item.getEnchantLevel()+" "+itemName+"を修復しますか？"));
			 pc.isRestore = true;
			 pc.sendPackets(new S_Message_YN(622,msg));
		}
}
