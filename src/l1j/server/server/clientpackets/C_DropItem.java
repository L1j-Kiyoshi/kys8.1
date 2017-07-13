package l1j.server.server.clientpackets;

import java.util.Calendar;

import l1j.server.Config;
import l1j.server.server.GameClient;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.NoDropItem;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.monitor.Logger.ItemActionType;
import l1j.server.server.monitor.LoggerInstance;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import manager.LinAllManager;

public class C_DropItem extends ClientBasePacket {

	private static final String C_DROP_ITEM = "[C] C_DropItem";
	/** 日付、時刻の記録 **/
	Calendar rightNow = Calendar.getInstance();
	int day = rightNow.get(Calendar.DATE);
	int hour = rightNow.get(Calendar.HOUR);
	int min = rightNow.get(Calendar.MINUTE);
	int year = rightNow.get(Calendar.YEAR);
	int month =  rightNow.get(Calendar.MONTH)+1;
	String totime = "[" + year + ":" + month + ":" + day + ":" + hour +":"+min+"]";	

	public C_DropItem(byte[] decrypt, GameClient client) throws Exception {
		super(decrypt);
		int length = 1;
		length = readD();
		for(int i=0 ; i<length ; ++i) {
		int x = readH();
		int y = readH();
		int objectId = readD();
		int count = readD();

		L1PcInstance pc = client.getActiveChar();

		if (pc == null || pc.isGhost() || isTwoLogin(pc)) {
			return;
		}
		
		if (pc.getOnlineStatus() != 1) {
			pc.sendPackets(new S_Disconnect());
			return;
		}

		L1ItemInstance item = pc.getInventory().getItem(objectId);
		if (item != null) {
			long nowtime = System.currentTimeMillis();
			if (item.getItemdelay3() >= nowtime) {
				return;
			}
			if (!pc.isGm() && !item.getItem().isTradable() || item.getItemId() == L1ItemId.HIGH_CHARACTER_TRADE
					|| item.getItemId() == L1ItemId.LOW_CHARACTER_TRADE) {
				// \f1%0はしまったり、または他人に両日をすることができません。
				pc.sendPackets(new S_ServerMessage(210, item.getItem().getName()));
				return;
			}

			/** バグ防止 **/
			int itemType = item.getItem().getType2();
			int itemId = item.getItem().getItemId();

			if ((itemType == 1 && count != 1)||(itemType == 2 && count != 1)) {	
				pc.sendPackets(new S_Disconnect());
				return;
			}
			if (item.getCount() <= 0) {
				pc.sendPackets(new S_Disconnect());
				return;
			}
			if (!item.isStackable() && count != 1) {
				pc.sendPackets(new S_Disconnect());
				return;
			}
			if (item.getCount() < count || count <= 0 || count > 2000000000) {
				pc.sendPackets(new S_Disconnect());
				return;
			}
			if (count > item.getCount()) {
				count = item.getCount();
			}
			
			/** ドロップ防止外部化db nodropitemテーブルでリストに追加 **/
			if (!pc.isGm() && NoDropItem.getInstance().isNoDropItem(itemId))  {
				String itemName = ItemTable.getInstance().findItemIdByName(itemId);
			    pc.sendPackets(new S_SystemMessage("\\aA警告:あなたは、 \\aG["+ itemName +"]\\aA を捨てることはできません。"));
				return;
			}
			// エンチャンされたアイテム捨てるなくしよう！
			if (!pc.isGm() && pc.getLevel() < Config.ALT_DROPLEVELLIMIT) {
				pc.sendPackets(new S_SystemMessage("レベル " + Config.ALT_DROPLEVELLIMIT + "から捨てることができます。"));
				return;
			}
			if (item.getEnchantLevel() >= 1 && !pc.isGm()) {
				pc.sendPackets(new S_SystemMessage("エンチャンされたアイテムは捨てることができません。"));
				return;
			}
//			if (pc.getInventory().getWeight30() > 240) {
//				pc.sendPackets(new S_SystemMessage（ "持ち物が重すぎて使用することはできません。"））;
//				return;
//			}
//			if (pc.getMaxWeight() <= pc.getInventory().getWeight()) {
//				pc.sendPackets(new S_SystemMessage（「持ち物が重すぎて行動することができません。 "））;
//				return;
//			}
			if (item.getId() >= 0 && (pc.getMapId() == 350 || pc.getMapId() == 340 || pc.getMapId() == 370 || pc.getMapId() == 800)) {
				pc.sendPackets(new S_SystemMessage("市場では、アイテムを捨てるません。"));
				return;
			}
			
			if (item.getItemId() == 80500) {
				return;
			}
			/** バグ防止 **/

			if(!pc.isGm() && item.getBless() >= 128){
				pc.sendPackets(new S_ServerMessage(210, item.getItem().getName())); // \f1％0はたりまたは他人に両日をすることができません。
				return;
			}

			Object[] petlist = pc.getPetList().values().toArray();
			L1PetInstance pet = null;
			for (Object petObject : petlist) {
				if (petObject instanceof L1PetInstance) {
					pet = (L1PetInstance) petObject;
					if (item.getId() == pet.getItemObjId()) {
						// \f1%0はしまったり、または他人に両日をすることができません。
						pc.sendPackets(new S_ServerMessage(210, item.getItem()
								.getName()));
						return;
					}
				}
			}

			for (Object dollObject : pc.getDollList()) {
				if (dollObject instanceof L1DollInstance) {
					L1DollInstance doll = (L1DollInstance) dollObject;
					if (item.getId() == doll.getItemObjId()) {
						// \f1%0はしまったり、または他人に両日をすることができません。
						pc.sendPackets(new S_ServerMessage(210, item.getItem().getName()));
						return;
					}
				}
			}

			if (item.isEquipped()) {
				// \f1削除することができないアイテムや装備しているアイテムは捨てることができません。
				pc.sendPackets(new S_ServerMessage(125));
				return;
			}
			if (x > pc.getX() + 1 || x < pc.getX() - 1 || y > pc.getY() + 1 || y < pc.getY() - 1) {
				return;
			}
			int delay_time = 2000;
			if (item != null) {
				if (item.isStackable()) {
					if (item.getItemdelay3() <= nowtime) {
						item.setItemdelay3(nowtime + delay_time);
					}
				}
			}
			
			LinAllManager.getInstance().PicupAppend(item.getLogName(), pc.getName(), count, 1);
			pc.getInventory().tradeItem(item, count, L1World.getInstance().getInventory(x, y, pc.getMapId()));
			pc.getLight().turnOnOffLight();
			/** ファイルログの保存 **/
			LoggerInstance.getInstance().addItemAction(ItemActionType.Drop, pc, item, count);
		}
	}
	}

	@Override
	public String getType() {
		return C_DROP_ITEM;
	}

	private boolean isTwoLogin(L1PcInstance c) {// 重複チェックを変更 
		boolean bool = false;
		for (L1PcInstance target : L1World.getInstance().getAllPlayers()) {
			if (target.noPlayerCK || target.noPlayerck2)continue;
			/**ロボットシステム **/
			if(target.getRobotAi() != null) continue;
			/**ロボットシステム **/
			if (c.getId() != target.getId() && (!target.isPrivateShop() && !target.isAutoClanjoin())) {
				if (c.getNetConnection().getAccountName().equalsIgnoreCase(target.getNetConnection().getAccountName())) {
					bool = true;
					break;
				}
			}
		}
		return bool;
	}
}
