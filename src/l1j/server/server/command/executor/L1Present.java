package l1j.server.server.command.executor;

import java.util.StringTokenizer;
import java.util.logging.Logger;

import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Item;

public class L1Present implements L1CommandExecutor {
	@SuppressWarnings("unused")
	private static Logger _log = Logger.getLogger(L1Present.class.getName());

	private L1Present() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Present();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer st = new StringTokenizer(arg);
			String name = st.nextToken();
			String nameid = st.nextToken();
			L1PcInstance target = L1World.getInstance().getPlayer(name);

			if (target == null) {
				target = CharacterTable.getInstance().restoreCharacter(name);

				if (target == null) {
					pc.sendPackets(new S_SystemMessage("存在しない文字です。"));
					return;
				}

				CharacterTable.getInstance().restoreInventory(target);
			}

			int count = 1;
			if (st.hasMoreTokens()) {
				count = Integer.parseInt(st.nextToken());
			}
			int enchant = 0;
			if (st.hasMoreTokens()) {
				enchant = Integer.parseInt(st.nextToken());
			}
			int itemid = 0;
			int Attrenchant = 0;
			if (st.hasMoreTokens()) {
				Attrenchant = Integer.parseInt(st.nextToken());
			}
			int bless = 0;
			if (st.hasMoreTokens()) {
				bless = Integer.parseInt(st.nextToken());
			}
			try {
				itemid = Integer.parseInt(nameid);
			} catch (NumberFormatException e) {
				itemid = ItemTable.getInstance().findItemIdByNameWithoutSpace(nameid);
				if (itemid == 0) {
					pc.sendPackets(new S_SystemMessage("該当のアイテムが見つかりません。"));
					return;
				}
			}
			L1Item temp = ItemTable.getInstance().getTemplate(itemid);
			if (temp != null) {
				if (temp.isStackable()) {
					L1ItemInstance item = ItemTable.getInstance().createItem(itemid);
					item.setEnchantLevel(0);
					item.setCount(count);
					if (target.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
						target.getInventory().storeItem(item);
						target.sendPackets(new S_SkillSound(pc.getId(), 4856));
						target.sendPackets(new S_SystemMessage("\\aD[ギフト] " + item.getLogName() + " 獲得"));
                        pc.sendPackets(new S_SystemMessage("\\aD["+target.getName()+"] "+ item.getLogName() + " (ID:" + itemid + ") 보냄"));
					}
				} else {
					L1ItemInstance item = null;
					int createCount;
					for (createCount = 0; createCount < count; createCount++) {
						item = ItemTable.getInstance().createItem(itemid);
						item.setEnchantLevel(enchant);
						item.setAttrEnchantLevel(Attrenchant);
						if (bless == 129) {
							item.setBless(bless);
						}
						//if (target.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
							target.getInventory().storeItem(item);
							if (bless == 129) {
								item.setBless(bless);
								target.getInventory().updateItem(item, L1PcInventory.COL_BLESS);
								target.getInventory().saveItem(item, L1PcInventory.COL_BLESS);
							}
					//	} else {
					//		break;
					//	}
					}
					if (createCount > 0) {
						target.sendPackets(new S_SkillSound(pc.getId(), 4856));
						target.sendPackets(new S_SystemMessage("\\aD[ギフト] +" + enchant + " " + item.getLogName() + " (" + count + "本）獲得"));
        				pc.sendPackets(new S_SystemMessage("\\aD["+target.getName()+"] +" + enchant + " "+ temp.getNameId()+"(ID:" + itemid + ") "+ count +"개 보냄", true));
					}
				}
			} else {
				pc.sendPackets(new S_SystemMessage("指定IDのアイテムは存在しません。"));
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage("[ギフト] [キャラクター] [アイテムID] [本数] [エンチャント] [属性] [封印129]"));
			}
	}
}