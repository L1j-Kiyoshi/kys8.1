package l1j.server.server.command.executor;

import java.util.ArrayList;

import l1j.server.server.datatables.FurnitureSpawnTable;
import l1j.server.server.datatables.LetterTable;
import l1j.server.server.datatables.PetTable;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1FurnitureInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;

public class L1DeleteGroundItem implements L1CommandExecutor {
	
	private L1DeleteGroundItem() {	}

	public static L1CommandExecutor getInstance() {
		return new L1DeleteGroundItem();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		L1ItemInstance l1iteminstance = null;
		ArrayList<L1PcInstance> players = null;
		L1Inventory groundInventory= null;
		LetterTable lettertable = null;
		L1FurnitureInstance furniture = null;
		for (L1Object l1object : L1World.getInstance().getObject()) {
			if (l1object instanceof L1ItemInstance) {
				l1iteminstance = (L1ItemInstance) l1object;
				if (l1iteminstance.getX() == 0 && l1iteminstance.getY() == 0) { //地面上のアイテムではなく、誰かの所有物
					continue;
				}

				players = L1World.getInstance().getVisiblePlayer(l1iteminstance, 2);
				if (0 == players.size()) {
					groundInventory = L1World.getInstance().getInventory(l1iteminstance.getX(), l1iteminstance.getY(), l1iteminstance.getMapId());
					int itemId = l1iteminstance.getItem(). getItemId();
					if (itemId == 40314 || itemId == 40316) { // ペットのアミュレット
						PetTable.getInstance().deletePet(l1iteminstance.getId());
					} else if (itemId >= 49016 && itemId <= 49025) { // 文房具
						lettertable = new LetterTable();
						lettertable.deleteLetter(l1iteminstance.getId());
					} else if (itemId >= 41383 && itemId <= 41400) { // 家具
						if (l1object instanceof L1FurnitureInstance) {
							furniture = (L1FurnitureInstance) l1object;
							if (furniture.getItemObjId() == l1iteminstance.getId()) { // すでに取り出している家具
								FurnitureSpawnTable.getInstance().deleteFurniture(furniture);
							}
						}
					}
					groundInventory.deleteItem(l1iteminstance);
					L1World.getInstance().removeVisibleObject(l1iteminstance);
					L1World.getInstance().removeObject(l1iteminstance);
				}
			}
		}
		//L1World.getInstance().broadcastServerMessage("ワールドマップ上のアイテムがGMによって削除されました。 "）;
	}
}
