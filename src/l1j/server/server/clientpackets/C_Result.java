package l1j.server.server.clientpackets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.GameClient;
import l1j.server.server.IdFactory;
import l1j.server.server.Controller.BugRaceController;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.NoShopAndWare;
import l1j.server.server.datatables.NpcShopTable;
import l1j.server.server.datatables.ShopTable;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Warehouse.ClanWarehouse;
import l1j.server.server.model.Warehouse.ElfWarehouse;
import l1j.server.server.model.Warehouse.PackageWarehouse;
import l1j.server.server.model.Warehouse.PrivateWarehouse;
import l1j.server.server.model.Warehouse.SpecialWarehouse;
import l1j.server.server.model.Warehouse.WarehouseManager;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.shop.L1Shop;
import l1j.server.server.model.shop.L1ShopBuyOrder;
import l1j.server.server.model.shop.L1ShopBuyOrderList;
import l1j.server.server.model.shop.L1ShopSellOrderList;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.monitor.Logger.WarehouseType;
import l1j.server.server.monitor.LoggerInstance;
import l1j.server.server.serverpackets.S_ACTION_UI;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1PrivateShopBuyList;
import l1j.server.server.templates.L1PrivateShopSellList;
import l1j.server.server.utils.SQLUtil;
import manager.LinAllManager;

public class C_Result extends ClientBasePacket {
	/** 日付と時刻の記録 **/
	Calendar rightNow = Calendar.getInstance();
	int day = rightNow.get(Calendar.DATE);
	int hour = rightNow.get(Calendar.HOUR);
	int min = rightNow.get(Calendar.MINUTE);
	int sec = rightNow.get(Calendar.SECOND);
	int year = rightNow.get(Calendar.YEAR);
	int month =  rightNow.get(Calendar.MONTH)+1;
	String totime = "[" + year + ":" + month + ":" + day + "]";
	String totime1 = "[" + hour + ":" + min + ":" + sec + "]";
	String date = +year+"_"+month+"_"+day;
	
	public C_Result(byte abyte0[], GameClient clientthread) throws Exception {
		super(abyte0);
		int npcObjectId = readD();
		int resultType = readC();
		int size = readC();
		@SuppressWarnings("unused")
		int unknown = readC();

		if(size < 0) return;

		L1PcInstance pc = clientthread.getActiveChar();
		if ( pc == null)return;
		boolean isPrivateShop = false;

		// add
		boolean isPrivateNpcShop = false;
		// add

		int level = pc.getLevel();
		int npcId = 0;
		String npcImpl = "";
		boolean tradable = true;
		L1Object findObject = L1World.getInstance().findObject(npcObjectId);

		if (findObject != null) { //3セル
			int diffLocX = Math.abs(pc.getX() - findObject.getX());
			int diffLocY = Math.abs(pc.getY() - findObject.getY());
			if (diffLocX > 12 || diffLocY > 12) {
				return;
			}
			if (findObject instanceof L1NpcInstance) {
				L1NpcInstance targetNpc = (L1NpcInstance) findObject;
				npcId = targetNpc.getNpcTemplate().get_npcId();
				npcImpl = targetNpc.getNpcTemplate().getImpl();

				// npcshop add
				if(npcImpl.equals("L1NpcShop")) isPrivateNpcShop = true;

			} else if (findObject instanceof L1PcInstance) {
				isPrivateShop = true;
			}
		}
		if(pc.getOnlineStatus() == 0){
			clientthread.kick();
			return;
		}
		
		if (npcObjectId == 7626) {
			npcId = 7626;   //アデン店エンピシ番号 
			npcImpl = "L1Merchant";
		}
		if(pc.getIsChangeItem()){
			// アイテムの購入
						L1Shop shop = ShopTable.getInstance().get(pc.changeNpcId);
						L1ShopBuyOrderList oList = shop.newBuyOrderList();
						int itemNumber;
						long itemcount;
						int totalcount=0;
						for (int i = 0; i < size; i++) {
							itemNumber = readD();
							itemcount = readD();
							System.out.println("ITEMNUMBER = "+itemNumber+" count :"+itemcount);
							totalcount++;
							if(totalcount > 1){
								pc.sendPackets(new S_SystemMessage("アイテムは一つずつ交換することができます"));
								return;
							}
							if (itemcount <= 0) {
								return;
							}
							oList.add(itemNumber, 1, pc);
						}
						if(pc.getInventory().checkItem(3000156)){
							L1ItemInstance item = null;
							for (L1ShopBuyOrder order : oList.getList()) {
								int itemId = order.getItem().getItemId();
								item = ItemTable.getInstance().createItem(itemId);
							}
							item.setEnchantLevel(pc.getDefaultItem().getEnchantLevel());
							item.setBless(pc.getDefaultItem().getBless());
							item.setAttrEnchantLevel(pc.getDefaultItem().getAttrEnchantLevel());
							item.setIdentified(true);
							pc.getInventory().storeItem(item);
							pc.getInventory().consumeItem(3000156,1);
							pc.getInventory().removeItem(pc.getDefaultItem(),1);
						}
						pc.setIsChangeItem(false);
						pc.setDefaultItem(null);
						return;
						
		}
		pc.setIsChangeItem(false);
		pc.setDefaultItem(null);
		/********************************************************************************************************		
		 ****************************************** アイテムを購入する************************************************ *****
		 *********************************************************************************************************/
		if (resultType == 0 && size != 0 && npcImpl.equalsIgnoreCase("L1Merchant")) {
			

			//アイテムの購入
			L1Shop shop = ShopTable.getInstance().get(npcId);
			L1ShopBuyOrderList orderList = shop.newBuyOrderList();
			int itemNumber;
			long itemcount;
			if (npcId == 7200002 || npcId == 777017){
				pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.TAM, pc.getAccount().getTamPoint()));
			}
			if (npcId == 70035 || npcId == 70041 || npcId == 70042) {
				if (BugRaceController.getInstance().getBugState() != 0){
					return;
				}
			}
			if (pc.getClan() == null && pc.getLevel() >= Config.STORE_USAGE_LEVEL) {
				pc.sendPackets(new S_SystemMessage(Config.STORE_USAGE_LEVEL + "レベル以上は血盟がなければ店を利用することができません。"));
				if (pc.isGm()) {
				} else {
					return;
				}
			}
			
			if(pc.serverDown == true){
				if (npcId == 70035 || npcId == 70041 || npcId == 70042) {
					pc.sendPackets(new S_SystemMessage("サーバーダウン進行中には、購入ができません。"));
					return;
				}
			}
			
			if (shop.getSellingItems().size() < size) {
				System.out.println("店が販売しているアイテム数（" + shop.getSellingItems().size() + "）よりも多く購入すること。（" + size + "）の");
				pc.getNetConnection().kick();
				pc.getNetConnection().close();
				return;
			}
			for (int i = 0; i < size; i++) {
				itemNumber = readD();
				itemcount = readD();
				if (itemcount <= 0) {
					return;
				}
				if (npcId >= 6100000 && npcId <= 6100041) {
					if (itemcount > 1) {
						pc.sendPackets(new S_SystemMessage("1つずつ購入することができます。"));
						return;
					}
				}

				if (itemcount <= 0 || itemcount >= 10000) {
					return;
				}
				orderList.add(itemNumber, (int) itemcount, pc);
				if (orderList.BugOk() != 0) {
					for (L1PcInstance player : L1World.getInstance().getAllPlayers()) {
						if (player.isGm() || pc == player) {
							player.sendPackets(new S_SystemMessage(pc.getName() + "様店最大の購入数量を超える（" + itemcount + ")"));
						}
					}
				}
			}
			int bugok = orderList.BugOk();
			if (bugok == 0) {
				//「栄養餌」アイテムの場合、時間値更新接触。
				for(L1ShopBuyOrder sbo : orderList.getList()) {
					if(sbo.getItem().getItemId() == 41295)
						pc.setFishingShopBuyTime_1(System.currentTimeMillis());
				}
				shop.sellItems(pc, orderList);
				// アイテムを保存させる
				pc.saveInventory();
				// アイテムを保存させる
			}
		

			/********************************************************************************************************		
			 ****************************************** アイテム販売************************************************ *****
			 *********************************************************************************************************/
		} else if (resultType == 1 && size != 0 && npcImpl.equalsIgnoreCase("L1Merchant")) {
			// アイテム売却
			L1Shop shop = ShopTable.getInstance().get(npcId);
			L1ShopSellOrderList orderList = shop.newSellOrderList(pc);
			int itemNumber;
			long itemcount;

			for (int i = 0; i < size; i++) {
				itemNumber = readD();
				itemcount = readD();
				if (itemcount <= 0) {
					return;
				}
				if (npcId >= 6100000 && npcId <= 6100041 && !pc.getInventory().getItem(itemNumber).isPackage()) {
					pc.sendPackets(new S_SystemMessage("パケットの店で購入していないアイテムが含まれています。"));
					return;
				}
				orderList.add(itemNumber, (int) itemcount, pc);
			}
			int bugok = orderList.BugOk();
			if (bugok == 0) {
				shop.buyItems(orderList);
				// ベクソプコピー防止数量性のバグを防ぐ
				pc.saveInventory();
				// ベクソプコピー防止数量性のバグを防ぐ
			}

			/********************************************************************************************************		
			 ****************************************** 個人倉庫任せる*********************************************** ***
			 *********************************************************************************************************/			
		} else if (resultType == 2 && size != 0 && npcImpl.equalsIgnoreCase("L1Dwarf")) {

			if (isTwoLogin(pc))
				return;

			int objectId, count;
			L1Object object = null;
			L1ItemInstance item = null;
			for (int i = 0; i < size; i++) {
				tradable = true;
				objectId = readD();
				count = readD();
				object = pc.getInventory().getItem(objectId);
				item = (L1ItemInstance) object;
				if (item == null)
					return;
				//倉庫不可アイテムディビ連動NoShopAndWare
				int itemId = item.getItem().getItemId();
				if (!pc.isGm() && NoShopAndWare.getInstance().isNoShopAndWare(itemId))  {// 
					pc.sendPackets(new S_SystemMessage("このアイテムは倉庫の利用をすることはできません。"));
					return;
				}
				
				long nowtime = System.currentTimeMillis();
				if (item.getItemdelay3() >= nowtime) {
					break;
				}

				if (objectId != item.getId()) {
					pc.sendPackets(new S_Disconnect());
					return;
				}

				if (!item.isStackable() && count != 1) {	
					pc.sendPackets(new S_Disconnect());
					return;
				}

				if (count <= 0 || item.getCount() <= 0) {	
					pc.sendPackets(new S_Disconnect());
					return;
				}
				if (count > item.getCount()) {
					count = item.getCount();
				}
				if (pc.hasSkillEffect(L1SkillId.SetBuff)){
                    pc.sendPackets(new S_SystemMessage("ログイン後、30秒間の倉庫＆しまうができません。"));
                    return;
                }
				if(item.getCount() > 2000000000){
					return;
				}
				if(count > 2000000000){
					return;
				}
				/**   倉庫任せる部分のバグを防ぐ **/  

				if (!item.getItem().isTradable()) {
					tradable = false;
					// \f1%0はしまったり、または他人に譲渡することはできません。
					pc.sendPackets(new S_ServerMessage(210, item.getItem().getName())); 

				}
				Object[] petlist = pc.getPetList().values().toArray();
				for (Object petObject : petlist) {
					if (petObject instanceof L1PetInstance) {
						L1PetInstance pet = (L1PetInstance) petObject;
						if (item.getId() == pet.getItemObjId()) {
							tradable = false;
							// \f1%0はしまったり、または他人に譲渡することはできません。
							pc.sendPackets(new S_ServerMessage(210, item.getItem().getName()));
							break;
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

				PrivateWarehouse warehouse = WarehouseManager.getInstance().getPrivateWarehouse(pc.getAccountName());
				if(warehouse == null) return;

				if (warehouse.checkAddItemToWarehouse(item, count) == L1Inventory.SIZE_OVER) {
					pc.sendPackets(new S_ServerMessage(75)); // \f1相手がものをも持っており、取引することはできません。
					break;
				}

				if (item.getBless() >= 128) {
					pc.sendPackets(new S_ServerMessage(210, item.getItem().getName()));
					break;
				}

				if (tradable) {
					pc.getInventory().tradeItem(objectId, count, warehouse);					
					pc.getLight().turnOnOffLight();
					//manager.LogWareHouseAppend("一般：引き受け "、pc.getName（）、" "、item、count、objectId）;
					LinAllManager.getInstance().WarehouseAppend(item.getLogName(), count, pc.getName(), 0);
					
					/** ログファイルの保存 **/
					LoggerInstance.getInstance().addWarehouse(WarehouseType.Private, true, pc, item, count);
					// [倉庫任せる：一般]キューブ：短剣（1）		
					if(count>=500){
					}else{
					}
				}
			}

			/********************************************************************************************************		
			 ******************************************個人倉庫検索***************************************** *********
			 *********************************************************************************************************/			
		} else if (resultType == 3 && size != 0	&& npcImpl.equalsIgnoreCase("L1Dwarf")) {
			int objectId, count;
			L1ItemInstance item =null;
			for (int i = 0; i < size; i++) {
				objectId = readD();
				count = readD();

				if(isTwoLogin(pc)) return;

				PrivateWarehouse warehouse = WarehouseManager.getInstance().getPrivateWarehouse(pc.getAccountName());
				if(warehouse == null) return;
				item = warehouse.getItem(objectId);  

				/**  倉庫の検索部分のバグを防ぐ **/
				if (item == null) {
					return;
				}

				if (pc.hasSkillEffect(L1SkillId.SetBuff)){
					pc.sendPackets(new S_SystemMessage("ログイン後、30秒間の倉庫＆しまうができません。"));
					return;
				}	

				if (!pc.getInventory().checkItem(L1ItemId.ADENA, 30 + 1)){
					pc.sendPackets(new S_SystemMessage("アデナが不足して。"));
					return;}

				if (objectId != item.getId()) {
					pc.sendPackets(new S_Disconnect());
					return;
				}
				if (!item.isStackable() && count != 1) {
					pc.sendPackets(new S_Disconnect());
					return;
				}			
				if (count <= 0 || item.getCount() <= 0) {
					pc.sendPackets(new S_Disconnect());
					return;
				}
				if (count > item.getCount()) {
					count = item.getCount();
				}

				/**   倉庫の検索部分のバグを防ぐ **/  	  

				if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) // 容量重量を確認し、メッセージ送信
				{
					if (pc.getInventory().consumeItem(L1ItemId.ADENA, 30)) {
						warehouse.tradeItem(item, count, pc.getInventory());	
						//manager.LogWareHouseAppend(「一般：検索 "、pc.getName（）、" "、item、count、objectId）;
						LinAllManager.getInstance().WarehouseAppend(item.getLogName(), count, pc.getName(), 1);
						/** ログファイルの保存 **/
						LoggerInstance.getInstance().addWarehouse(WarehouseType.Private, false, pc, item, count);
						// [倉庫検索：一般]キューブ：短剣（1）		
						if(count>=500){
						}else{
						}
					} else {
						pc.sendPackets(new S_ServerMessage(189)); // \f1アデナが不足します。
						break;
					}
				} else {
					pc.sendPackets(new S_ServerMessage(270)); // \f1持っていることが重く取引することはできません。
					break;
				}
			}


			/********************************************************************************************************		
			 ***************************************血盟倉庫任せる****************************************** ***********
			 *********************************************************************************************************/
		} else if (resultType == 4 && size != 0 && npcImpl.equalsIgnoreCase("L1Dwarf")) {   
			int objectId, count;
			L1Object object = null;
			L1ItemInstance item = null;
			L1Clan clan = null;
			if (pc.getClanid() != 0) { // クランに所属
				for (int i = 0; i < size; i++) {
					tradable = true;
					objectId = readD();
					count = readD();

					if(isTwoLogin(pc)) return;

					clan = L1World.getInstance().getClan(pc.getClanname());
					object = pc.getInventory().getItem(objectId);
					item = (L1ItemInstance) object;	  
					if ( item == null ) {
						return;
					}					
					//倉庫不可アイテムディビ連動NoShopAndWare
					int itemId = item.getItem().getItemId();
					if (!pc.isGm() && NoShopAndWare.getInstance().isNoShopAndWare(itemId))  {// 
						pc.sendPackets(new S_SystemMessage("このアイテムは倉庫の利用をすることはできません。"));
						return;
					}
					long nowtime = System.currentTimeMillis();
					if (item.getItemdelay3() >= nowtime) {
						break;
					}

					if (objectId != item.getId()) {
						pc.sendPackets(new S_Disconnect());
						return;
					}
					if (!item.isStackable() && count != 1) {
						pc.sendPackets(new S_Disconnect());
						return;
					}							
					if (count <= 0 || item.getCount() <= 0) {
						pc.sendPackets(new S_Disconnect());
						return;
					}
					if(item.getCount() > 2000000000){
						return;
					}
					if(count > 2000000000){
						return;
					}
					if (count > item.getCount()) {
						count = item.getCount();
					}				
					/**   倉庫任せる部分のバグを防ぐ **/  	  

					if(item.getBless() >= 128){
						pc.sendPackets(new S_ServerMessage(210, item.getItem().getName())); // \f1%0はしまったり、または他人に両日をすることができません。
						return;
					}

					if (clan != null) {
						if (!item.getItem().isTradable()) {
							tradable = false;
							pc.sendPackets(new S_ServerMessage(210, item.getItem().getName())); // \f1％0はたりまたは他人に両日をすることができません。
						}
						Object[] petlist = pc.getPetList().values().toArray();
						for (Object petObject : petlist) {
							if (petObject instanceof L1PetInstance) {
								L1PetInstance pet = (L1PetInstance) petObject;
								if (item.getId() == pet.getItemObjId()) {
									tradable = false;
									// \f1％0はたりまたは他人に両日をすることができません。
									pc.sendPackets(new S_ServerMessage(210,
											item.getItem().getName()));
									break;
								}
							}
						}
						ClanWarehouse clanWarehouse = WarehouseManager.getInstance().getClanWarehouse(clan.getClanName());
						if (clanWarehouse.checkAddItemToWarehouse(item, count) == L1Inventory.SIZE_OVER) {
							pc.sendPackets(new S_ServerMessage(75)); // \f1相手がものをも持っており、取引することはできません。
							break;
						}
						if (tradable) {
							pc.getInventory().tradeItem(objectId, count, clanWarehouse);
							pc.getLight().turnOnOffLight();
							//UpdateLog(pc.getName(), pc.getClanname(), item.getName(), count, 0);
							history(pc, item, count, 1);
							//manager.LogWareHouseAppend（「血盟：預かり "、pc.getName（）、pc.getClanname（）、item、count、objectId）;
							LinAllManager.getInstance().EPWarehouseAppend(item.getLogName(), count, pc.getName(), 2);
							/** ログファイルの保存 **/
							LoggerInstance.getInstance().addWarehouse(WarehouseType.Clan, true, pc, item, count);
							if(count>=500){
							}else{
							}
						}
					}
				}
			} else {
				pc.sendPackets(new S_ServerMessage(208)); // \f1血盟倉庫を使用するには、血盟に加入しなければなりません。
			}


			/********************************************************************************************************		
			 ***************************************血盟倉庫検索******************************** *********************
			 *********************************************************************************************************/

		} else if (resultType == 5 && size != 0 && npcImpl.equalsIgnoreCase("L1Dwarf") && level >= 5) {
			// ** 倉庫利用5レップで修正** //
			if (pc.getInventory().findItemId(40308).getCount() < 31) {
				pc.sendPackets(new S_SystemMessage("アデナが不足します。"));
				return;
			}

			if(isTwoLogin(pc)) return;

			int objectId, count;

			L1ItemInstance item;

			L1Clan clan = L1World.getInstance().getClan(pc.getClanname());

			ClanWarehouse clanWarehouse = WarehouseManager.getInstance().getClanWarehouse(clan.getClanName());

			if (clan != null) {
				for (int i = 0; i < size; i++) {
					objectId = readD();
					count = readD();
					item = clanWarehouse.getItem(objectId);   

					//** クラン倉庫検索部分を守る **//		
					if ( item == null ) {						
						return; 
					}
					if (objectId != item.getId()) {
						pc.sendPackets(new S_Disconnect());
						return;
					}

					//					if (pc.hasSkillEffect(L1SkillId.SetBuff)){
					//						pc.sendPackets（new S_ChatPacket（pc、「ログイン後、30秒間の倉庫＆しまうができません。 "））;
					//						return;
					//					}	

					if (!item.isStackable() && count != 1) {
						pc.sendPackets(new S_Disconnect());
						return;
					}
					if (count <= 0 || item.getCount() <= 0 || item.getCount() >2000000000) {
						pc.sendPackets(new S_Disconnect());
						return;
					}
					if (count >= item.getCount()) {
						count = item.getCount();
					}				

					//** クラン倉庫検索部分を守る**//		 

					if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) { // 容量重量を確認し、メッセージ送信
						if (pc.getInventory().consumeItem(L1ItemId.ADENA, 30)) {
							clanWarehouse.tradeItem(item, count, pc.getInventory());
							//UpdateLog(pc.getName(), pc.getClanname(), item.getName(), count, 1);
							history(pc, item, count, 2);
							//manager.LogWareHouseAppend（「血盟：検索 "、pc.getName（）、pc.getClanname（）、item、count、objectId）;
							LinAllManager.getInstance().EPWarehouseAppend(item.getLogName(), count, pc.getName(), 3);
							/** ログファイルの保存 **/
							LoggerInstance.getInstance().addWarehouse(WarehouseType.Clan, false, pc, item, count);
							if (count >= 500) {
							} else {
							}
						} else {
							// \f1アデナが不足します。
							pc.sendPackets(new S_ServerMessage(189)); 
							break;
						}
					} else {
						// \f1 持っていることが重く取引することはできません。
						pc.sendPackets(new S_ServerMessage(270));
						break;
					}
				}
				clanWarehouse.setWarehouseUsingChar(0, 0);
			}	


			/**
			 *クラン倉庫に取り出す 
			 * Cancel、または、ESCキー
			 */
		}else if(resultType == 5 && size == 0 && npcImpl.equalsIgnoreCase("L1Dwarf")){
			L1Clan clan = L1World.getInstance().getClan(pc.getClanname());

			if (pc.hasSkillEffect(L1SkillId.SetBuff)){
				return;
			}

			ClanWarehouse clanWarehouse = WarehouseManager.getInstance().getClanWarehouse(clan.getClanName());
			if(clan != null){
				clanWarehouse.setWarehouseUsingChar(0, 0);
			}

			/*********************************************************************** *********************************		
			 ***************************************妖精倉庫任せる************************************** ***************
			 *********************************************************************************************************/
		} else if (resultType == 8 && size != 0
				&& npcImpl.equalsIgnoreCase("L1Dwarf") && level >= 5 && pc. isElf()) { //自分のエルフ倉庫に格納
			int objectId, count;
			L1Object object = null;
			L1ItemInstance item = null;
			for (int i = 0; i < size; i++) {
				tradable = true;
				objectId = readD();
				count = readD();

				if(isTwoLogin(pc)) return;

				object = pc.getInventory(). getItem(objectId);
				item = (L1ItemInstance) object;
				if ( item == null ) {
					return;
				}		
				//倉庫不可アイテムディビ連動NoShopAndWare
				int itemId = item.getItem().getItemId();
				if (!pc.isGm() && NoShopAndWare.getInstance().isNoShopAndWare(itemId))  {// 
					pc.sendPackets(new S_SystemMessage("このアイテムは倉庫の利用をすることはできません。"));
					return;
				}
				if (objectId != item.getId()) {
					pc.sendPackets(new S_Disconnect());
					return;
				}
				if (!item.isStackable() && count != 1) {
					pc.sendPackets(new S_Disconnect());
					return;
				}
				if (count <= 0 || item.getCount() <= 0) {
					pc.sendPackets(new S_Disconnect());
					return;
				}
				if (count > item.getCount()) {
					count = item.getCount();
				}
				if(item.getCount() > 2000000000){
					return;
				}
				if(count > 2000000000){
					return;
				}
				/**   倉庫任せる部分のバグを防ぐ **/  

				if (! item.getItem(). isTradable()) {
					tradable = false;
					pc.sendPackets(new S_ServerMessage(210, item.getItem()
							. getName())); // \f1%0はしまったり、または他人に両日をすることができません。
				}

//				if(item.getBless() >= 128){
//					pc.sendPackets(new S_ServerMessage(210,item.getItem（）。getName（）））; // \\ f1％0はたりまたは他人に両日をすることができません。
//					return;
//				}

				Object[] petlist = pc.getPetList(). values(). toArray();
				for (Object petObject : petlist) {
					if (petObject instanceof L1PetInstance) {
						L1PetInstance pet = (L1PetInstance) petObject;
						if (item.getId() == pet.getItemObjId()) {
							tradable = false;
							// \f1%0はしまったり、または他人に両日をすることができません。
							pc.sendPackets(new S_ServerMessage(210, item
									. getItem(). getName()));
							break;
						}
					}
				}
				ElfWarehouse elfwarehouse = WarehouseManager.getInstance().getElfWarehouse(pc.getAccountName());
				if (elfwarehouse.checkAddItemToWarehouse(item, count) == L1Inventory.SIZE_OVER) {
					pc.sendPackets(new S_ServerMessage(75)); // \\ f1相手がものをも持っており、取引することはできません。
					break;
				}
				if (tradable) {
					pc.getInventory().tradeItem(objectId, count, elfwarehouse);
					pc.getLight().turnOnOffLight();
					
					//manager.LogWareHouseAppend("妖精：引き受け "、pc.getName(), "", item, count, objectId);
					LinAllManager.getInstance().EPWarehouseAppend(item.getLogName(), count, item.getName(), 0);
					/** ログファイルの保存**/
					LoggerInstance.getInstance().addWarehouse(WarehouseType.Elf, true, pc, item, count);	
					if(count>=500){
					}else{
					}
				}
			}
			/********************************************************************************************************		
			 ***************************************妖精倉庫検索********************* ********************************
			 *********************************************************************************************************/
		} else if (resultType == 9 && size != 0	&& npcImpl.equalsIgnoreCase("L1Dwarf") 
				&& level >= 5 && pc.isElf()) { //自分の妖精倉庫から取り出して
			int objectId, count;
			L1ItemInstance item;
			for (int i = 0; i < size; i++) {
				objectId = readD();
				count = readD();

				if(isTwoLogin(pc)) return;

				ElfWarehouse elfwarehouse = WarehouseManager.getInstance().getElfWarehouse(pc.getAccountName());
				item = elfwarehouse.getItem(objectId);

				/**   倉庫の検索部分のバグを防ぐ **/				
				if ( item == null ) {
					return;
				}
				if (!pc.getInventory().checkItem(L1ItemId.ADENA, 30 + 1)){
					pc.sendPackets(new S_SystemMessage("アデナが不足して。"));
					return;
				}	
				if (objectId != item.getId()) {
					pc.sendPackets(new S_Disconnect());
					return;
				}
				if (!item.isStackable() && count != 1) {
					pc.sendPackets(new S_Disconnect());
					return;
				}			
				if (count <= 0 || item.getCount() <= 0) {
					pc.sendPackets(new S_Disconnect());
					return;
				}
				if (count > item.getCount()) {
					count = item.getCount();
				}
				/**   倉庫の検索部分のバグを防ぐ **/  

				if (pc.getInventory(). checkAddItem(item, count) == L1Inventory.OK) {
					if (pc.getInventory().consumeItem(40494, 2)) {
						elfwarehouse.tradeItem(item, count, pc.getInventory());
						
						//manager.LogWareHouseAppend（ "妖精：検索"、pc.getName（）、 ""、item、count、objectId）;
						LinAllManager.getInstance().EPWarehouseAppend(item.getLogName(), count, item.getName(), 1);
						/** ログファイルの保存**/
						LoggerInstance.getInstance().addWarehouse(WarehouseType.Elf, false, pc, item, count);
						if(count>=500){
						}else{
						}
					} else {
						pc.sendPackets(new S_ServerMessage(337,"$767"));
						break;
					}
				} else {
					pc.sendPackets(new S_ServerMessage(270)); // \f1持っていることが重く取引することはできません。
					break;
				}
			}
		
			/********************************************************************************************************		
			 *************************************** パッケージ倉庫検索  *************************************************
			 *********************************************************************************************************/
		} else if (resultType == 12 && size != 0 && npcImpl.equalsIgnoreCase("L1Dwarf") || pc.isPackegeWarehouse()) {
			int objectId, count;
			L1ItemInstance item = null;
			PackageWarehouse w = WarehouseManager.getInstance().getPackageWarehouse(pc.getAccountName());
			if (w == null)
				return;

			for (int i = 0; i < size; i++) {
				objectId = readD();
				count = readD();
				item = w.getItem(objectId);

				/**  倉庫の検索部分のバグを防ぐ **/				
				if ( item == null ) {
					return;
				}
				if (!pc.getInventory().checkItem(L1ItemId.ADENA, 30 + 1)){
					pc.sendPackets(new S_SystemMessage("アデナが不足して。"));
					return;
				}	
				if (objectId != item.getId()) {
					pc.sendPackets(new S_Disconnect());
					return;
				}
				if (!item.isStackable() && count != 1) {
					pc.sendPackets(new S_Disconnect());
					return;
				}			
				if (count <= 0 || item.getCount() <= 0) {
					pc.sendPackets(new S_Disconnect());
					return;
				}
				if (count > item.getCount()) {
					count = item.getCount();
				}
				/**   倉庫の検索部分のバグを防ぐ **/  
				
				 //** リピータノディルバグ防ぎましょう **//
				  long nowtime = System.currentTimeMillis();
				  if(item.getItemdelay3() >=  nowtime ){
					  break;
				  }  
				  //** リピータノディルバグ防ぎましょう **//
					if (item.getItemId() == 40308 && item.getCount() >= 10000000
							|| item.getCount() >= 1000 && item.getItemId() != 40308) {
					}
					 //新鮮な牛乳の量性のバグを防ぐ
				   if (!item.getItem().isToBeSavedAtOnce()) {
				   pc.getInventory().saveItem(item, L1PcInventory.COL_COUNT);
				   }	 
			      //新鮮な牛乳の量性のバグを防ぐ
				w.tradeItem(item, count, pc.getInventory());
				//manager.LogWareHouseAppend("パッケージ：検索 "、pc.getName（）、" "、item、count、objectId）;
				/** ログファイルの保存 **/
				LoggerInstance.getInstance().addWarehouse(WarehouseType.Private, false, pc, item, count);

			}
			/********************************************************************************************************		
			 ***************************************特殊倉庫任せる****************************************** *******
			 *********************************************************************************************************/
		} else if (resultType == 17 && size != 0 && npcImpl.equalsIgnoreCase("L1Dwarf") && pc.get_SpecialSize() > 0) {
			L1Object object = null;
			L1ItemInstance item = null;
			SpecialWarehouse warehouse = WarehouseManager.getInstance()
					.getSpecialWarehouse(pc.getName());
			
			if (warehouse == null)
				return;
			for (int i = 0, objectId, count; i < size; i++) {
				objectId = readD();
				count = readD();
				object = pc.getInventory().getItem(objectId);
				item = (L1ItemInstance) object;
				if (item == null){
					return;
				}
				if (pc.getLevel() < 5) {
					pc.sendPackets(new S_SystemMessage("倉庫は5レベル以上使用可能です。"));
					return;
				}
				/* バグ防止 */
				// **エンジン防御** //
				if (pc.getInventory().findItemId(40308).getCount() < 31) {
										S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"アデナが不足して"); 
						pc.sendPackets(s_chatpacket);
					return;
				}
				// ワールドマップ上マイアカウントなどの同じアカウントを持つキャラクターが接続中であれば、
				if (isTwoLogin(pc))
					return;
				if (objectId != item.getId()) {
					pc.sendPackets(new S_Disconnect());
					break;
				}
				if (!item.isStackable() && count != 1) {
					pc.sendPackets(new S_Disconnect());
					break;
				}
				/* バグ防止 */
				if (item == null || item.getCount() < count || count <= 0
						|| item.getCount() <= 0) {
					break;
				}
			    if (!pc.isGm() && item.getItem().getItemId() == 40308)  {
		        	if (count > 10000000) {
		        		 pc.sendPackets(new S_SystemMessage("アデナは1000万単位ずつ倉庫利用が可能です。"));
		        		 return;
		        	}
			    }
			    if(item.getItem().getUseType() == 44){
					pc.sendPackets(new S_SystemMessage("ルーン、遺物は、倉庫の利用ができません。"));
					return;
				}
				if(item.getItem().getMaxUseTime() > 0){
					pc.sendPackets(new S_SystemMessage("時間制アイテムは倉庫利用が不可能です。"));
					return;
				}

				if (item.getItem().getItemId() == 41159 ||item.getItem().getItemId() == 41246){ 
					S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"このアイテムは倉庫の利用をすることはできません。");
					pc.sendPackets(s_chatpacket);
					return;
				}
				if (item.getCount() > 10000000) {
					break;
				}
				if (count > 10000000) {
					break;
				}

				if (count > item.getCount())
					count = item.getCount();
				if (item.getItemId() == 40308 && item.getCount() >= 10000000
						|| item.getCount() >= 1000 && item.getItemId() != 40308) {
				}
				pc.getInventory().tradeItem(objectId, count, warehouse);
				pc.getLight().turnOnOffLight();

				//manager.LogWareHouseAppend("特殊：引き受け "、pc.getName（）、" "、item、count、objectId）;
				/** ログファイルの保存 **/
				LoggerInstance.getInstance().addWarehouse(WarehouseType.Private, false, pc, item, count);
			}
			/********************************************************************************************************		
			 ***************************************特殊倉庫検索********************************************* ****
			 *********************************************************************************************************/
		} else if (resultType == 18 && size != 0 && npcImpl.equalsIgnoreCase("L1Dwarf")) {
			L1ItemInstance item = null;
			SpecialWarehouse warehouse = WarehouseManager.getInstance().getSpecialWarehouse(pc.getName());
			if (warehouse == null)
				return;
			for (int i = 0, objectId, count; i < size; i++) {
				objectId = readD();
				count = readD();
				item = warehouse.getItem(objectId);
				if (pc.getLevel() < 5) {
					pc.sendPackets(new S_SystemMessage("倉庫は5レベル以上使用可能です。 "));
					return;
				}
			    if (!pc.isGm() && item.getItem().getItemId() == 40308)  {
		        	if (count > 10000000) {
		        		 pc.sendPackets(new S_SystemMessage("アデナは1000万単位ずつ倉庫利用が可能です。"));
		        		 return;
		        	}
			    }
				/* バグ防止 */
				if (item.getItem().getItemId() == 41159 ||item.getItem().getItemId() == 41246){ //魂の装飾
					S_ChatPacket s_chatpacket = new S_ChatPacket(pc, "このアイテムは倉庫の利用をすることはできません。");
					pc.sendPackets(s_chatpacket);
					return;
				}
				warehouse.tradeItem(item, count, pc.getInventory());
				//manager.LogWareHouseAppend（「特殊：検索 "、pc.getName（）、" "、item、count、objectId）;
				/** ログファイルの保存 **/
				LoggerInstance.getInstance().addWarehouse(WarehouseType.Private, false, pc, item, count);
			}
			/********************************************************************************************************		
			 *************************************** npc店アイテムを購入********************************** ***************
			 *********************************************************************************************************/
		} else if (resultType == 0 && size != 0 && isPrivateNpcShop) {
			L1Shop shop = NpcShopTable.getInstance().get(npcId);
			L1ShopBuyOrderList orderList = shop.newBuyOrderList();
			int itemNumber; long itemcount;

			for (int i = 0; i < size; i++) {
				itemNumber = readD();
				itemcount = readD();
				if(itemcount <= 0) {
					return;
				}
				if(size >= 2){ //同時に、他のものを買うことなく、2つの選択であれば、
					pc.sendPackets(new S_SystemMessage("一度別のアイテムを購入することができません。"));
					return;
				}
				if(pc.getMapId() == 800){
					if(itemcount > 15) {
						pc.sendPackets(new S_SystemMessage("最大購入数量：ザブテム類（15）/機器（1）"));
						return;
					}
				} 
				orderList.add(itemNumber, (int)itemcount , pc); 
			}
			int bugok = orderList.BugOk();
			if (bugok == 0){
				shop.sellItems(pc, orderList);
				//ベクソプコピー防止数量性のバグを防ぐ
				pc.saveInventory();
				//ベクソプコピー防止数量性のバグを防ぐ
			}
			/********************************************************************************************************		
			 *************************************** 個人商店アイテムを購入********************************************** ***
			 *********************************************************************************************************/
		} else if (resultType == 0 && size != 0 && isPrivateShop) {
			int order;
			int count;
			int price;
			ArrayList<L1PrivateShopSellList> sellList;
			L1PrivateShopSellList pssl;
			int itemObjectId;
			int sellPrice;
			int sellTotalCount;
			int sellCount;
			L1ItemInstance item;
			boolean[] isRemoveFromList = new boolean[8];

			L1PcInstance targetPc = null;
			if (findObject instanceof L1PcInstance) {
				targetPc = (L1PcInstance) findObject;
			}
			if (targetPc == null) {
				return;
			}


			sellList = targetPc.getSellList();

			synchronized (sellList) {
				// 在庫切れが発生し、閲覧中のアイテム数とリストの数が異なっている
				if (pc.getPartnersPrivateShopItemCount() != sellList.size()) {
					return;
				}

				for (int i = 0; i < size; i++) { // 購入予定の商品
					order = readD();
					count = readD();
					pssl = (L1PrivateShopSellList) sellList.get(order);
					itemObjectId = pssl.getItemObjectId();
					sellPrice = pssl.getSellPrice();
					sellTotalCount = pssl.getSellTotalCount(); // 腕予定の数
					sellCount = pssl.getSellCount(); // 板累計
					item = targetPc.getInventory().getItem(itemObjectId);
					if (item == null) {
						continue;
					}

					long nowtime = System.currentTimeMillis();
					if (item.getItemdelay3() >= nowtime) {
						break;
					}
					if (count > sellTotalCount - sellCount) {
						count = sellTotalCount - sellCount;
					}
					if (count <= 0) {
						continue;
					}
					if (item.isEquipped()) {
						pc.sendPackets(new S_ServerMessage(905, "")); //装備しているアイテムを購入するように。
						continue;
					}

					if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) { //容量重量を確認し、メッセージ送信
						for (int j = 0; j < count; j++) { // オーバーフローをチェック
							if (sellPrice * j > 2000000000 || sellPrice * j < 0) {
								pc.sendPackets(new S_ServerMessage(904, // 販売代理店毎の価格は％dアデナを超えることはできません。
										"2000000000"));
								return;
							}
						}
						price = count * sellPrice;	 

						/** 個人商店のバグを防ぐ **/  					

						if (itemObjectId != item.getId()) {
							pc.sendPackets(new S_Disconnect());
							targetPc.sendPackets(new S_Disconnect());
							return;
						}
						if (!item.isStackable() && count != 1) {
							pc.sendPackets(new S_Disconnect());
							targetPc.sendPackets(new S_Disconnect());
							return;
						}
						if (count <= 0 || item.getCount() <= 0 || item.getCount()<count) {
							pc.sendPackets(new S_Disconnect());
							targetPc.sendPackets(new S_Disconnect());
							return;
						}
						if (count >= item.getCount()) {count = item.getCount();	}

						if (item.isEquipped()) {
							pc.sendPackets(new S_SystemMessage("相手が着用しているアイテムです。"));
							return;
						}				
						if (price <= 0 ||price > 2000000000) return;
						/** 個人商店のバグを防ぐ **/  

						if (pc.getInventory().checkItem(L1ItemId.ADENA, price)) {
							L1ItemInstance adena = pc.getInventory()
									.findItemId(L1ItemId.ADENA);
							if (targetPc != null && adena != null) {
								if (targetPc.getInventory().tradeItem(item,
										count, pc.getInventory()) == null) {
									return;
								}
								pc.getInventory().tradeItem(adena, price,
										targetPc.getInventory());
								String message = item.getItem().getName()
										+ " (" + String.valueOf(count) + ")";								
								targetPc.sendPackets(new S_ServerMessage(877, // %1%o
										// %0販売しました。
										pc.getName(), message));
								pssl.setSellCount(count + sellCount);							
								sellList.set(order, pssl);
								writeLogbuyPrivateShop(pc, targetPc, item, count, price);
								if (pssl.getSellCount() == pssl.getSellTotalCount()) { // 腕予定の数を販売した
									isRemoveFromList[order] = true;
								}
								try {
									pc.saveInventory();
									targetPc.saveInventory();
								} catch (Exception e) {
									//									_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
								}
							}
						} else {
							pc.sendPackets(new S_ServerMessage(189)); // \f1アデナが不足します。
							break;
						}
					} else {
						pc.sendPackets(new S_ServerMessage(270)); // \f1 持っていることが重く取引することはできません。
						break;
					}
				}
				// 品切れになったアイテムをリストの末尾から削除
				for (int i = 7; i >= 0; i--) {
					if (isRemoveFromList[i]) {
						sellList.remove(i);
					}
				}
			}
			/********************************************************************************************************		
			 *************************************** Npc個人商店販売****************************************** ***********
			 *********************************************************************************************************/			

		} else if (resultType == 1 && size != 0 && isPrivateNpcShop) { // 個人商店にアイテム売却
			
			L1Shop shop = NpcShopTable.getInstance().get(npcId);
			L1ShopSellOrderList orderList = shop.newSellOrderList(pc);
			int itemNumber; long itemcount;

			for (int i = 0; i < size; i++) {
				itemNumber = readD();
				itemcount = readD();
				if(itemcount <= 0) {
					return;
				}
				orderList.add(itemNumber, (int)itemcount , pc); 
			}
			int bugok = orderList.BugOk();
			if (bugok == 0){
				shop.buyItems(orderList);
				//ベクソプコピー防止数量性のバグを防ぐ
				pc.saveInventory();
				//ベクソプコピー防止数量性のバグを防ぐ
			}
			
			/********************************************************************************************************		
			 *************************************** 個人商店販売*********************************************** ******
			 *********************************************************************************************************/			
		} else if (resultType == 1 && size != 0 && isPrivateShop) { // 個人商店にアイテム売却
			int count;
			int order;
			ArrayList<L1PrivateShopBuyList> buyList;
			L1PrivateShopBuyList psbl;
			int itemObjectId;
			L1ItemInstance item = null;
			int buyPrice;
			int buyTotalCount;
			int buyCount;

			boolean[] isRemoveFromList = new boolean[8];

			L1PcInstance targetPc = null;
			if (findObject instanceof L1PcInstance) {
				targetPc = (L1PcInstance) findObject;
			}
			if (targetPc == null) {
				return;
			}
			buyList = targetPc.getBuyList();

			synchronized (buyList) {
				for (int i = 0; i < size; i++) {
					itemObjectId = readD();
					count = readCH();
					order = readC();
					item = pc.getInventory().getItem(itemObjectId);
					if (item == null) {
						continue;
					}

//					if(item.getBless() >= 128){
//						pc.sendPackets(new S_ServerMessage(210, item.getItem().getName())); // \f1%0はしまったり、または他人に両日をすることができません。
//						return;
//					}

					psbl = (L1PrivateShopBuyList) buyList.get(order);
					buyPrice = psbl.getBuyPrice();
					buyTotalCount = psbl.getBuyTotalCount(); // 買う予定の数
					buyCount = psbl.getBuyCount(); // 山累計
					if (count > buyTotalCount - buyCount) {
						count = buyTotalCount - buyCount;
					}
					int buyItemObjectId = psbl.getItemObjectId();
					L1ItemInstance buyItem = targetPc.getInventory().getItem(buyItemObjectId);

					if (buyItem == null) {
						return;
					}
					if (item.isEquipped()) {
						pc.sendPackets(new S_ServerMessage(905)); // 装備しているアイテムは販売できません。
						continue;
					}

					if (targetPc.getInventory().checkAddItem(item, count) == L1Inventory.OK) { //容量重量を確認し、メッセージ送信
						for (int j = 0; j < count; j++) { //オーバーフローをチェック
							if (buyPrice * j > 2000000000 || buyPrice * j < 0) {
								targetPc.sendPackets(new S_ServerMessage(904, // 販売代理店毎の価格は％dアデナを超えることはできません。
										"2000000000"));
								return;
							}
						}	  
						/** バグ防止 **/
						if (itemObjectId != item.getId()) {
							pc.sendPackets(new S_Disconnect());
							targetPc.sendPackets(new S_Disconnect());
							return;
						}

						if (count >= item.getCount()) {
							count = item.getCount();
						}

						if (item.getItemId() != buyItem.getItemId()) return;
						if (!item.isStackable() && count !=1) return;					
						if (item.getCount() <= 0 || count <= 0) return;
						if (buyPrice * count <= 0 || buyPrice * count > 2000000000) return;
						//** 個人商店の部分ビシャス防御** //	

						if (targetPc.getInventory().checkItem(L1ItemId.ADENA, count * buyPrice)) {
							L1ItemInstance adena = targetPc.getInventory().findItemId(L1ItemId.ADENA);
							if (adena != null) {
								targetPc.getInventory().tradeItem(adena, count * buyPrice, pc.getInventory());
								pc.getInventory().tradeItem(item, count, targetPc.getInventory());
								psbl.setBuyCount(count + buyCount);
								buyList.set(order, psbl);
								if (psbl.getBuyCount() == psbl.getBuyTotalCount()) {
									// 買う予定の数を買った
									isRemoveFromList[order] = true;
								}
								try {
									pc.saveInventory();
									targetPc.saveInventory();
								} catch (Exception e) {
									//								_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
								}
							}
						} else {
							targetPc.sendPackets(new S_ServerMessage(189)); // \\ f1アデナが不足します。
							break;
						}
					} else {
						pc.sendPackets(new S_ServerMessage(271)); // \f1相手がものをも持っており、取引することはできません。
						break;
					}
				}
				// 売店たアイテムをリストの末尾から削除
				for (int i = 7; i >= 0; i--) {
					if (isRemoveFromList[i]) {
						buyList.remove(i);
					}
				}
			}
		}
	}

	@Override
	public String getType() {
		return "[C] C_Result";
	}

	private void writeLogbuyPrivateShop(L1PcInstance pc, L1PcInstance targetPc,
			L1ItemInstance item, int count, int price) {
		String itemadena = item.getName() + "(" + price + ")";
		//manager.LogShopAppend("個人商店で購入 "、pc.getName(), targetPc.getName(), item.getEnchantLevel(), itemadena, item.getBless(), count, item.getId());
		LinAllManager.getInstance().ShopAppend(item.getLogName(), count, price, targetPc.getName(), pc.getName());
		/** ログファイルの保存 **/
		LoggerInstance.getInstance().bayPersonalStore(true, pc, targetPc, item, item.getCount());
	}
	
	private void history(L1PcInstance pc, L1ItemInstance item, int count, int i) {
		StringBuilder itemname = new StringBuilder();
		Connection con = null;
		PreparedStatement pstm = null;
		int clanid = pc.getClanid();
		String char_name = pc.getName();
		int item_enchant = item.getEnchantLevel();
		int elapsed_time = (int) (System.currentTimeMillis() / 1000);
		String type = null;
		if (i == 1) {
			type = "任せた。";
		} else {
			type = "見つかりました。";
		}
		if (item.getItem().getType2() != 0) {
			if (item_enchant >= 0) {
				itemname.append("+"+item_enchant+" ");
			} else {
				itemname.append(item_enchant+" ");
			}
		}
		itemname.append(item.getName());
		try{
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("INSERT INTO clan_warehousehistory SET id =?, clan_id = ?, char_name = ?, item_name = ?, item_count = ?, elapsed_time = ?, item_getorput = ?");
			pstm.setInt(1, IdFactory.getInstance().nextId());
			pstm.setInt(2, clanid);
			pstm.setString(3, char_name);
			pstm.setString(4, itemname.toString());
			pstm.setInt(5, count);
			pstm.setInt(6, elapsed_time);
			pstm.setString(7, type);
			pstm.execute();
		} catch (SQLException e) {
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private boolean isTwoLogin(L1PcInstance c) {//重複チェックを変更 
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
	/**
	 * ワールド上にあるすべてのキャラクターのアカウントを比較して、同じアカウントがある場合trueない場合false
	 * @param c L1PcInstance
	 * @return場合はtrue
	 */

}