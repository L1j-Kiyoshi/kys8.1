package l1j.server.server.Controller;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.datatables.KeyTable;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillIconGFX;
import l1j.server.server.serverpackets.S_SystemMessage;

public class PcInventoryDeleteController implements Runnable {
	private static Logger _log = Logger.getLogger(PcInventoryDeleteController.class.getName());

	private static PcInventoryDeleteController _instance;

	public static final int SLEEP_TIME = 60000;

	public static PcInventoryDeleteController getInstance() {
		if (_instance == null)
			_instance = new PcInventoryDeleteController();
		return _instance;
	}

	private Collection<L1PcInstance> _list = null;

	public void run() {
		long currentTimeMillis = System.currentTimeMillis();
		try {
			_list = L1World.getInstance().getAllPlayers();
			for (L1PcInstance pc : _list) {
				if (pc == null)
					continue;
				L1Inventory pcInventory = pc.getInventory();
				for (L1ItemInstance item : pcInventory.getItems()) {
					if (item == null)
						continue;

					if (item.getEndTime() == null)
						continue;

					if (currentTimeMillis > item.getEndTime().getTime()) {
						
						int itemId = item.getItemId();

						if (itemId == L1ItemId.MERIN_CONTRACT){
							pc.sendPackets(new S_ServerMessage(1823));
							pc.getInventory().storeItem(L1ItemId.MERIN_PIPE, 1);
						}else if (itemId == L1ItemId.KILLTON_CONTRACT){
							pc.sendPackets(new S_ServerMessage(1823));
							pc.getInventory().storeItem(L1ItemId.KILLTON_PIPE, 1);
						}else if (itemId == 3000048){
							pc.sendPackets(new S_ServerMessage(1823));
							pc.getInventory().consumeItem(3000048, 1);
						}else if (itemId == 80500){
							KeyTable.DeleteKeyId(item.getKeyId());
						}else if (itemId >= 30022 && itemId <= 30025) {
							// 해당 아이템 인형이 사용중이라면 리스트 삭제.
							for (Object dollObject : pc.getDollList()) {
								if (dollObject instanceof L1DollInstance) {
									L1DollInstance doll = (L1DollInstance) dollObject;
									if (item.getId() == doll.getItemObjId()) {
										doll.deleteDoll();
										pc.sendPackets(new S_SkillIconGFX(56, 0));
										pc.sendPackets(new S_OwnCharStatus(pc));
									}
								}
							}
						}
						pc.sendPackets(new S_SystemMessage(item.getName() + "의 사용시간이 만료 되어 소멸되었습니다."));
						pcInventory.removeItem(item);
					}
				}
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			_list = null;
		}
	}
}