package l1j.server.server;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import l1j.server.server.datatables.NpcShopSpawnTable;
import l1j.server.server.datatables.NpcShopTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1NpcShopInstance;
import l1j.server.server.serverpackets.S_DoActionShop;
import l1j.server.server.templates.L1NpcShop;

// npc shop add
public class NpcShopSystem implements Runnable {

	private static NpcShopSystem _instance;

	private boolean _power = false;

	// private int Count = 0;
	// private final int Time = 2;

	public static NpcShopSystem getInstance() {
		if (_instance == null) {
			_instance = new NpcShopSystem();
		}
		return _instance;
	}

	private boolean isReload = false;

	@Override
	public void run() {

		// 1時間ごとにshopリロード
		String currentTime = "";
		while (true) {
			try {
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(System.currentTimeMillis());
				SimpleDateFormat sdf = new SimpleDateFormat("HHmm", Locale.KOREA);
				currentTime = sdf.format(c.getTime());
				if (!isReload) {
					if (isOwnHour(currentTime)) {
						// リロード
						if (isPower()) {
							NpcShopTable.reloding();
							isReload = true;
						}
					} else {
						isReload = false;
					}
				} else {
					isReload = false;
				}
				Thread.sleep(1000L); //
			} catch (Exception e) {
			}
		}
	}

	private boolean isOwnHour(String time) {
		// System.out.println("時間 : " + time);
		if (time == null || time.equals(""))
			return false;
		return time.equals("0000") || time.equals("0100") || time.equals("0200") || time.equals("0300")
				|| time.equals("0400") || time.equals("0500") || time.equals("0600") || time.equals("0700")
				|| time.equals("0800") || time.equals("0900") || time.equals("1000") || time.equals("1100")
				|| time.equals("1200") || time.equals("1300") || time.equals("1400") || time.equals("1500")
				|| time.equals("1600") || time.equals("1700") || time.equals("1800") || time.equals("1900")
				|| time.equals("2000") || time.equals("2100") || time.equals("2200") || time.equals("2300");
	}

	static class NpcShopTimer implements Runnable {

		public NpcShopTimer() {
		}

		public void run() {
			try {
				ArrayList<L1NpcShop> list = NpcShopSpawnTable.getInstance().getList();
				for (int i = 0; i < list.size(); i++) {
					L1NpcShop shop = list.get(i);
					L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(shop.getNpcId());
					npc.setId(IdFactory.getInstance().nextId());
					npc.setMap(shop.getMapId());
					npc.getLocation().set(shop.getX(), shop.getY(), shop.getMapId());
					npc.getLocation().forward(5);
					npc.setHomeX(npc.getX());
					npc.setHomeY(npc.getY());
					npc.setHeading(shop.getHeading());
					npc.setName(shop.getName());
					npc.setTitle(shop.getTitle());
					L1NpcShopInstance obj = (L1NpcShopInstance) npc;
					obj.setShopName(shop.getShopName());
					L1World.getInstance().storeObject(npc);
					L1World.getInstance().addVisibleObject(npc);
					npc.getLight().turnOnOffLight();
					Thread.sleep(30);
					obj.setState(1);
					Broadcaster.broadcastPacket(npc, new S_DoActionShop(npc.getId(), ActionCodes.ACTION_Shop, shop
							.getShopName().getBytes()));

					Thread.sleep(10);
				}
				// list.clear();

			} catch (Exception exception) {
				return;
			}
		}
	}

	public void npcShopStart() {
		NpcShopTable.reloding();
		NpcShopTimer ns = new NpcShopTimer();
		GeneralThreadPool.getInstance().execute(ns);
		_power = true;
	}

	public void npcShopStop() {
		_power = false;
		ArrayList<L1NpcShop> list = NpcShopSpawnTable.getInstance().getList();
		if (list != null) {
			L1NpcShop[] shop = list.toArray(new L1NpcShop[list.size()]);
			for (int i = 0; i < shop.length; i++) {
				if (shop[i] == null)
					continue;
				L1NpcInstance npc = L1World.getInstance().findNpc(shop[i].getNpcId());
				if (npc == null)
					continue;
				npc.deleteMe();
			}
		}
	}

	public boolean isPower() {
		return _power;
	}
}
