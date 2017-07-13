package l1j.server.server.clientpackets;

import java.util.Random;

import l1j.server.server.ActionCodes;
import l1j.server.server.GameClient;
import l1j.server.server.datatables.HouseTable;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1House;

public class C_Door extends ClientBasePacket {

	private static final String C_DOOR = "[C] C_Door";

	private static Random _random = new Random(System.nanoTime());

	public C_Door(byte abyte0[], GameClient client) throws Exception {
		super(abyte0);
		int locX = readH();// 扉クリック
		int locY = readH();// 扉クリック
		int objectId = readD();

		L1PcInstance pc = client.getActiveChar();
		if (pc == null)
			return;

		// L1DoorInstance door =
		// (L1DoorInstance)L1World.getInstance().findObject(objectId);
		L1DoorInstance door = null;
		L1Object obj = L1World.getInstance().findObject(objectId);
		if (obj instanceof L1DoorInstance)
			door = (L1DoorInstance) obj;
		if (door == null) {
			return;
		}

		if (L1World.getInstance().findObject(objectId) == null
				|| !(L1World.getInstance().findObject(objectId) instanceof L1DoorInstance)) {
			return;
		}

		if (locX > pc.getX() + 1 || locX < pc.getX() - 1 || locY > pc.getY() + 1 || locY < pc.getY() - 1) {
			return;
		}

		if (door.getNpcId() >= 5147 && door.getNpcId() <= 5151 || door.getNpcId() >= 14911 && door.getNpcId() <= 14912) {
			return;
		}

		if (door != null && !isExistKeeper(pc, door.getKeeperId())) {
			if (door.getDoorId() == 113) {
				if (pc.getInventory().checkItem(40163)) {
					pc.getInventory().consumeItem(40163, 1);
				} else {
					return;
				}
			}
			// if(door.getDoorId() == 125){
			// if(pc.getInventory().checkItem(40313)){
			// pc.getInventory().consumeItem(40313, 1);
			// }else{
			// return;
			// }
			// }

			//言うソムドン2階のボスドア
			if (door.getDoorId() >= 4100 && door.getDoorId() <= 4111) {
				if (pc.getInventory().checkItem(40313, 1)) {
					pc.getInventory().consumeItem(40313, 1);
					pc.바포방 = true;
				} else {
					pc.바포방 = false;
					return;
				}
			}
			if (door.getDoorId() >= 8001 && door.getDoorId() <= 8010) {
				if (pc.getInventory().checkItem(L1ItemId.GIRANCAVE_BOXKEY, 1)) {
					giranCaveBox(pc, door);
					return;
				} else {
					return;
				}
			}
			if (door.getDoorId() >= 900151 && door.getDoorId() <= 900154) {// ハーディン
																			// ドア
				return;
			}

			/** 火竜の聖域 */
			if (door.getNpcId() >= 7210013 && door.getNpcId() <= 7210015) {
				return;
			}
			if (door.getOpenStatus() == ActionCodes.ACTION_Open) {
				if (!(door.getDoorId() >= 7210016 && door.getDoorId() <= 7210019)) {
					door.close();
				}
			} else if (door.getOpenStatus() == ActionCodes.ACTION_Close) {
				door.open();
				if (door.getDoorId() >= 7210016 && door.getDoorId() <= 7210019) {
					phoenixEgg(pc, door);
				}
			}
		}
	}

	// ///////////////////////////////ギラン監獄ボックス///////////////////////////////////
	private void giranCaveBox(L1PcInstance pc, L1DoorInstance door) {
		int ran = _random.nextInt(100) + 1;
		// L1ItemInstance item = null;
		if (door.getOpenStatus() == ActionCodes.ACTION_Close) {
			pc.getInventory().consumeItem(L1ItemId.GIRANCAVE_BOXKEY, 1);
			door.open();
			if (ran >= 0 && ran <= 15) {
				pc.getInventory().storeItem(40308, 10000);
				pc.sendPackets(new S_SystemMessage("アデナ（10000）を獲得しました。"));
			} else if (ran >= 16 && ran <= 29) {
				pc.getInventory().storeItem(40308, 20000);
				pc.sendPackets(new S_SystemMessage("アデナ（20000）を獲得しました。"));
			} else if (ran >= 30 && ran <= 49) {
				pc.getInventory().storeItem(40308, 30000);
				pc.sendPackets(new S_SystemMessage("アデナ（30000）を獲得しました。"));
			} else if (ran >= 50 && ran <= 59) {
				pc.getInventory().storeItem(40308, 50000);
				pc.sendPackets(new S_SystemMessage("アデナ（50000）を獲得しました。"));
			} else if (ran >= 60 && ran <= 64) {
				pc.getInventory().storeItem(40308, 100000);
				pc.sendPackets(new S_SystemMessage("アデナ（100000）を獲得しました。"));
			} else if (ran >= 65 && ran <= 69) {
				pc.getInventory().storeItem(40308, 200000);
				pc.sendPackets(new S_SystemMessage("アデナ（200000）を獲得しました。"));
			} else if (ran >= 70 && ran <= 72) {
				pc.getInventory().storeItem(40308, 300000);
				pc.sendPackets(new S_SystemMessage("アデナ（300000）を獲得しました。"));
			} else if (ran >= 73 && ran <= 75) {
				pc.getInventory().storeItem(40308, 400000);
				pc.sendPackets(new S_SystemMessage("アデナ（400000）を獲得しました。"));
			} else if (ran >= 76 && ran <= 78) {
				pc.getInventory().storeItem(40308, 500000);
				pc.sendPackets(new S_SystemMessage("アデナ（500000）を獲得しました。"));
			} else if (ran >= 79 && ran <= 80) {
				pc.getInventory().storeItem(40308, 1000000);
				pc.sendPackets(new S_SystemMessage("アデナ（1000000）を獲得しました。"));
			} else if (ran >= 81 && ran <= 90) {
				pc.getInventory().storeItem(40074, 3);
				pc.sendPackets(new S_SystemMessage("防具強化スクロール（3）を獲得しました。"));
			} else if (ran >= 91 && ran <= 100) {
				pc.getInventory().storeItem(40087, 3);
				pc.sendPackets(new S_SystemMessage("武器強化スクロール（3）を獲得しました。"));
			}
		}

	}

	// ///////////////////////////////ギラン監獄ボックス///////////////////////////////////

	private void phoenixEgg(L1PcInstance pc, L1DoorInstance door) {
		int ran = _random.nextInt(100) + 1;
		L1ItemInstance item = null;
		if (ran >= 0 && ran <= 10) {
			item = pc.getInventory().storeItem(40010, 5);
			pc.sendPackets(new S_ServerMessage(403, item.getName() + " (5)"));
		} else if (ran >= 11 && ran <= 20) {
			item = pc.getInventory().storeItem(40010, 10);
			pc.sendPackets(new S_ServerMessage(403, item.getName() + " (10)"));
		} else if (ran >= 21 && ran <= 30) {
			item = pc.getInventory().storeItem(40010, 15);
			pc.sendPackets(new S_ServerMessage(403, item.getName() + " (15)"));
		} else if (ran >= 31 && ran <= 40) {
			item = pc.getInventory().storeItem(40010, 30);
			pc.sendPackets(new S_ServerMessage(403, item.getName() + " (30)"));
		} else if (ran >= 41 && ran <= 50) {
			item = pc.getInventory().storeItem(40010, 50);
			pc.sendPackets(new S_ServerMessage(403, item.getName() + " (50)"));
		} else if (ran >= 51 && ran <= 60) {
			item = pc.getInventory().storeItem(40012, 5);
			pc.sendPackets(new S_ServerMessage(403, item.getName() + " (5)"));
		} else if (ran >= 61 && ran <= 70) {
			item = pc.getInventory().storeItem(40012, 10);
			pc.sendPackets(new S_ServerMessage(403, item.getName() + " (10)"));
		} else if (ran >= 71 && ran <= 80) {
			item = pc.getInventory().storeItem(40012, 15);
			pc.sendPackets(new S_ServerMessage(403, item.getName() + " (15)"));
		} else if (ran >= 81 && ran <= 90) {
			item = pc.getInventory().storeItem(40012, 30);
			pc.sendPackets(new S_ServerMessage(403, item.getName() + " (30)"));
		} else if (ran >= 91 && ran <= 100) {
			item = pc.getInventory().storeItem(40012, 50);
			pc.sendPackets(new S_ServerMessage(403, item.getName() + " (50)"));
		}
	}

	private boolean isExistKeeper(L1PcInstance pc, int keeperId) {
		if (keeperId == 0) {
			return false;
		}

		L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
		if (clan != null) {
			int houseId = clan.getHouseId();
			if (houseId != 0) {
				L1House house = HouseTable.getInstance().getHouseTable(houseId);
				if (keeperId == house.getKeeperId()) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public String getType() {
		return C_DOOR;
	}
}
