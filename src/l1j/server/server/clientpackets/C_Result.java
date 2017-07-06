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
	/** 날짜 및 시간 기록 **/
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

		if (findObject != null) { // 3셀
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
			npcId = 7626;   //아덴상점 엔피씨번호 
			npcImpl = "L1Merchant";
		}
		if(pc.getIsChangeItem()){
			// 아이템 구입
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
								pc.sendPackets(new S_SystemMessage("아이템은 한개씩 교환할 수 있습니다"));
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
		 ****************************************** 아이템 구매 *****************************************************
		 *********************************************************************************************************/
		if (resultType == 0 && size != 0 && npcImpl.equalsIgnoreCase("L1Merchant")) {
			

			// 아이템 구입
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
			if (pc.getClan() == null && pc.getLevel() >= Config.상점이용레벨) {
				pc.sendPackets(new S_SystemMessage(Config.상점이용레벨 + "레벨 이상은 혈맹이 없으면 상점을 이용할 수 없습니다."));
				if (pc.isGm()) {
				} else {
					return;
				}
			}
			
			if(pc.서버다운중 == true){
				if (npcId == 70035 || npcId == 70041 || npcId == 70042) {
					pc.sendPackets(new S_SystemMessage("서버다운 진행중에는 구매가 불가능합니다."));
					return;
				}
			}
			
			if (shop.getSellingItems().size() < size) {
				System.out.println("상점이 판매하는 아이템 수(" + shop.getSellingItems().size() + ")보다 더 많이 사려고 함.(" + size + ")개");
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
						pc.sendPackets(new S_SystemMessage("1개씩 구입할 수 있습니다."));
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
							player.sendPackets(new S_SystemMessage(pc.getName() + "님 상점 최대구매 수량초과 (" + itemcount + ")"));
						}
					}
				}
			}
			int bugok = orderList.BugOk();
			if (bugok == 0) {
				// '영양 미끼' 아이템일경우 시간값 갱신해주기.
				for(L1ShopBuyOrder sbo : orderList.getList()) {
					if(sbo.getItem().getItemId() == 41295)
						pc.setFishingShopBuyTime_1(System.currentTimeMillis());
				}
				shop.sellItems(pc, orderList);
				// 아이템저장시킴
				pc.saveInventory();
				// 아이템저장시킴
			}
		

			/********************************************************************************************************		
			 ****************************************** 아이템 판매 *****************************************************
			 *********************************************************************************************************/
		} else if (resultType == 1 && size != 0 && npcImpl.equalsIgnoreCase("L1Merchant")) {
			// 아이템 매각
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
					pc.sendPackets(new S_SystemMessage("패킷상점에서 구매하지 않은 아이템이 포함되어 있습니다."));
					return;
				}
				orderList.add(itemNumber, (int) itemcount, pc);
			}
			int bugok = orderList.BugOk();
			if (bugok == 0) {
				shop.buyItems(orderList);
				// 백섭복사 방지 수량성버그방지
				pc.saveInventory();
				// 백섭복사 방지 수량성버그방지
			}

			/********************************************************************************************************		
			 ****************************************** 개인 창고 맡기기 **************************************************
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
				//창고불가아이템 디비연동 NoShopAndWare
				int itemId = item.getItem().getItemId();
				if (!pc.isGm() && NoShopAndWare.getInstance().isNoShopAndWare(itemId))  {// 
					pc.sendPackets(new S_SystemMessage("해당 아이템은 창고 이용을 할 수 없습니다."));
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
                    pc.sendPackets(new S_SystemMessage("로그인 후 30초간 창고 & 버리기가 불가능 합니다."));
                    return;
                }
				if(item.getCount() > 2000000000){
					return;
				}
				if(count > 2000000000){
					return;
				}
				/**   창고 맡기기 부분 버그 방지 **/  

				if (!item.getItem().isTradable()) {
					tradable = false;
					// \f1%0은 버리거나 또는 타인에게 양도 할 수 없습니다.
					pc.sendPackets(new S_ServerMessage(210, item.getItem().getName())); 

				}
				Object[] petlist = pc.getPetList().values().toArray();
				for (Object petObject : petlist) {
					if (petObject instanceof L1PetInstance) {
						L1PetInstance pet = (L1PetInstance) petObject;
						if (item.getId() == pet.getItemObjId()) {
							tradable = false;
							// \f1%0은 버리거나 또는 타인에게 양도 할 수 없습니다.
							pc.sendPackets(new S_ServerMessage(210, item.getItem().getName()));
							break;
						}
					}
				}

				for (Object dollObject : pc.getDollList()) {
					if (dollObject instanceof L1DollInstance) {
						L1DollInstance doll = (L1DollInstance) dollObject;
						if (item.getId() == doll.getItemObjId()) {
							// \f1%0은 버리거나 또는 타인에게 양일을 할 수 없습니다.
							pc.sendPackets(new S_ServerMessage(210, item.getItem().getName()));
							return;
						}
					}
				}

				PrivateWarehouse warehouse = WarehouseManager.getInstance().getPrivateWarehouse(pc.getAccountName());
				if(warehouse == null) return;

				if (warehouse.checkAddItemToWarehouse(item, count) == L1Inventory.SIZE_OVER) {
					pc.sendPackets(new S_ServerMessage(75)); // \f1상대가 물건을 너무 가지고 있어 거래할 수 없습니다.
					break;
				}

				if (item.getBless() >= 128) {
					pc.sendPackets(new S_ServerMessage(210, item.getItem().getName()));
					break;
				}

				if (tradable) {
					pc.getInventory().tradeItem(objectId, count, warehouse);					
					pc.getLight().turnOnOffLight();
					//manager.LogWareHouseAppend("일반:맡", pc.getName(), "", item, count, objectId);
					LinAllManager.getInstance().WarehouseAppend(item.getLogName(), count, pc.getName(), 0);
					
					/** 로그파일저장 **/
					LoggerInstance.getInstance().addWarehouse(WarehouseType.Private, true, pc, item, count);
					// [창고맡기기:일반] 큐브 : 단검(1)		
					if(count>=500){
					}else{
					}
				}
			}

			/********************************************************************************************************		
			 ****************************************** 개인 창고 찾기 **************************************************
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

				/**   창고 찾기 부분 버그 방지 **/
				if (item == null) {
					return;
				}

				if (pc.hasSkillEffect(L1SkillId.SetBuff)){
					pc.sendPackets(new S_SystemMessage("로그인 후 30초간 창고 & 버리기가 불가능 합니다."));
					return;
				}	

				if (!pc.getInventory().checkItem(L1ItemId.ADENA, 30 + 1)){
					pc.sendPackets(new S_SystemMessage("아데나가 부족합니다.."));
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

				/**   창고 찾기 부분 버그 방지 **/  	  

				if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) // 용량 중량 확인 및 메세지 송신
				{
					if (pc.getInventory().consumeItem(L1ItemId.ADENA, 30)) {
						warehouse.tradeItem(item, count, pc.getInventory());	
						//manager.LogWareHouseAppend("일반:찾", pc.getName(), "", item, count, objectId);
						LinAllManager.getInstance().WarehouseAppend(item.getLogName(), count, pc.getName(), 1);
						/** 로그파일저장 **/
						LoggerInstance.getInstance().addWarehouse(WarehouseType.Private, false, pc, item, count);
						// [창고찾기:일반] 큐브 : 단검(1)		
						if(count>=500){
						}else{
						}
					} else {
						pc.sendPackets(new S_ServerMessage(189)); // \f1아데나가 부족합니다.
						break;
					}
				} else {
					pc.sendPackets(new S_ServerMessage(270)); // \f1 가지고 있는 것이 무거워서 거래할 수 없습니다.
					break;
				}
			}


			/********************************************************************************************************		
			 *************************************** 혈맹 창고 맡기기 *****************************************************
			 *********************************************************************************************************/
		} else if (resultType == 4 && size != 0 && npcImpl.equalsIgnoreCase("L1Dwarf")) {   
			int objectId, count;
			L1Object object = null;
			L1ItemInstance item = null;
			L1Clan clan = null;
			if (pc.getClanid() != 0) { // 크란 소속
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
					//창고불가아이템 디비연동 NoShopAndWare
					int itemId = item.getItem().getItemId();
					if (!pc.isGm() && NoShopAndWare.getInstance().isNoShopAndWare(itemId))  {// 
						pc.sendPackets(new S_SystemMessage("해당 아이템은 창고 이용을 할 수 없습니다."));
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
					/**   창고 맡기기 부분 버그 방지 **/  	  

					if(item.getBless() >= 128){
						pc.sendPackets(new S_ServerMessage(210, item.getItem().getName())); // \f1%0은 버리거나 또는 타인에게 양일을 할 수 없습니다.
						return;
					}

					if (clan != null) {
						if (!item.getItem().isTradable()) {
							tradable = false;
							pc.sendPackets(new S_ServerMessage(210, item.getItem().getName())); // \f1%0은 버리거나 또는 타인에게 양일을 할 수 없습니다.
						}
						Object[] petlist = pc.getPetList().values().toArray();
						for (Object petObject : petlist) {
							if (petObject instanceof L1PetInstance) {
								L1PetInstance pet = (L1PetInstance) petObject;
								if (item.getId() == pet.getItemObjId()) {
									tradable = false;
									// \f1%0은 버리거나 또는 타인에게 양일을 할 수 없습니다.
									pc.sendPackets(new S_ServerMessage(210,
											item.getItem().getName()));
									break;
								}
							}
						}
						ClanWarehouse clanWarehouse = WarehouseManager.getInstance().getClanWarehouse(clan.getClanName());
						if (clanWarehouse.checkAddItemToWarehouse(item, count) == L1Inventory.SIZE_OVER) {
							pc.sendPackets(new S_ServerMessage(75)); // \f1상대가 물건을 너무 가지고 있어 거래할 수 없습니다.
							break;
						}
						if (tradable) {
							pc.getInventory().tradeItem(objectId, count, clanWarehouse);
							pc.getLight().turnOnOffLight();
							//UpdateLog(pc.getName(), pc.getClanname(), item.getName(), count, 0);
							history(pc, item, count, 1);
							//manager.LogWareHouseAppend("혈맹:맡", pc.getName(), pc.getClanname(), item, count, objectId);
							LinAllManager.getInstance().EPWarehouseAppend(item.getLogName(), count, pc.getName(), 2);
							/** 로그파일저장 **/
							LoggerInstance.getInstance().addWarehouse(WarehouseType.Clan, true, pc, item, count);
							if(count>=500){
							}else{
							}
						}
					}
				}
			} else {
				pc.sendPackets(new S_ServerMessage(208)); // \f1혈맹 창고를 사용하려면  혈맹에 가입하지 않으면 안됩니다.
			}


			/********************************************************************************************************		
			 *************************************** 혈맹 창고 찾기 *****************************************************
			 *********************************************************************************************************/

		} else if (resultType == 5 && size != 0 && npcImpl.equalsIgnoreCase("L1Dwarf") && level >= 5) {
			// ** 창고이용 5렙으로 수정**//
			if (pc.getInventory().findItemId(40308).getCount() < 31) {
				pc.sendPackets(new S_SystemMessage("아데나가 부족합니다."));
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

					//** 클랜 창고 찾기 부분 방어 **//		
					if ( item == null ) {						
						return; 
					}
					if (objectId != item.getId()) {
						pc.sendPackets(new S_Disconnect());
						return;
					}

					//					if (pc.hasSkillEffect(L1SkillId.SetBuff)){
					//						pc.sendPackets(new S_ChatPacket(pc,"로그인 후 30초간 창고 & 버리기가 불가능 합니다."));
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

					//** 클랜 창고 찾기 부분 방어 **//		 

					if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) { // 용량 중량 확인 및 메세지 송신
						if (pc.getInventory().consumeItem(L1ItemId.ADENA, 30)) {
							clanWarehouse.tradeItem(item, count, pc.getInventory());
							//UpdateLog(pc.getName(), pc.getClanname(), item.getName(), count, 1);
							history(pc, item, count, 2);
							//manager.LogWareHouseAppend("혈맹:찾", pc.getName(), pc.getClanname(), item, count, objectId);
							LinAllManager.getInstance().EPWarehouseAppend(item.getLogName(), count, pc.getName(), 3);
							/** 로그파일저장 **/
							LoggerInstance.getInstance().addWarehouse(WarehouseType.Clan, false, pc, item, count);
							if (count >= 500) {
							} else {
							}
						} else {
							// \f1아데나가 부족합니다.
							pc.sendPackets(new S_ServerMessage(189)); 
							break;
						}
					} else {
						// \f1 가지고 있는 것이 무거워서 거래할 수 없습니다.
						pc.sendPackets(new S_ServerMessage(270));
						break;
					}
				}
				clanWarehouse.setWarehouseUsingChar(0, 0);
			}	


			/**
			 * 크란 창고로 꺼낸다 
			 * Cancel, 또는, ESC 키
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

			/********************************************************************************************************		
			 *************************************** 요정 창고 맡기기 *****************************************************
			 *********************************************************************************************************/
		} else if (resultType == 8 && size != 0
				&& npcImpl.equalsIgnoreCase("L1Dwarf") && level >= 5 && pc. isElf()) { // 자신의 에르프 창고에 격납
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
				//창고불가아이템 디비연동 NoShopAndWare
				int itemId = item.getItem().getItemId();
				if (!pc.isGm() && NoShopAndWare.getInstance().isNoShopAndWare(itemId))  {// 
					pc.sendPackets(new S_SystemMessage("해당 아이템은 창고 이용을 할 수 없습니다."));
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
				/**   창고 맡기기 부분 버그 방지 **/  

				if (! item.getItem(). isTradable()) {
					tradable = false;
					pc.sendPackets(new S_ServerMessage(210, item.getItem()
							. getName())); // \f1%0은 버리거나 또는 타인에게 양일을 할 수 없습니다.
				}

//				if(item.getBless() >= 128){
//					pc.sendPackets(new S_ServerMessage(210, item.getItem().getName())); // \f1%0은 버리거나 또는 타인에게 양일을 할 수 없습니다.
//					return;
//				}

				Object[] petlist = pc.getPetList(). values(). toArray();
				for (Object petObject : petlist) {
					if (petObject instanceof L1PetInstance) {
						L1PetInstance pet = (L1PetInstance) petObject;
						if (item.getId() == pet.getItemObjId()) {
							tradable = false;
							// \f1%0은 버리거나 또는 타인에게 양일을 할 수 없습니다.
							pc.sendPackets(new S_ServerMessage(210, item
									. getItem(). getName()));
							break;
						}
					}
				}
				ElfWarehouse elfwarehouse = WarehouseManager.getInstance().getElfWarehouse(pc.getAccountName());
				if (elfwarehouse.checkAddItemToWarehouse(item, count) == L1Inventory.SIZE_OVER) {
					pc.sendPackets(new S_ServerMessage(75)); // \f1상대가 물건을 너무 가지고 있어 거래할 수 없습니다.
					break;
				}
				if (tradable) {
					pc.getInventory().tradeItem(objectId, count, elfwarehouse);
					pc.getLight().turnOnOffLight();
					
					//manager.LogWareHouseAppend("요정:맡", pc.getName(), "", item, count, objectId);
					LinAllManager.getInstance().EPWarehouseAppend(item.getLogName(), count, item.getName(), 0);
					/** 로그파일저장**/
					LoggerInstance.getInstance().addWarehouse(WarehouseType.Elf, true, pc, item, count);	
					if(count>=500){
					}else{
					}
				}
			}
			/********************************************************************************************************		
			 *************************************** 요정 창고 찾기 *****************************************************
			 *********************************************************************************************************/
		} else if (resultType == 9 && size != 0	&& npcImpl.equalsIgnoreCase("L1Dwarf") 
				&& level >= 5 && pc.isElf()) { // 자신의 요정 창고로부터 꺼내
			int objectId, count;
			L1ItemInstance item;
			for (int i = 0; i < size; i++) {
				objectId = readD();
				count = readD();

				if(isTwoLogin(pc)) return;

				ElfWarehouse elfwarehouse = WarehouseManager.getInstance().getElfWarehouse(pc.getAccountName());
				item = elfwarehouse.getItem(objectId);

				/**   창고 찾기 부분 버그 방지 **/				
				if ( item == null ) {
					return;
				}
				if (!pc.getInventory().checkItem(L1ItemId.ADENA, 30 + 1)){
					pc.sendPackets(new S_SystemMessage("아데나가 부족합니다.."));
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
				/**   창고 찾기 부분 버그 방지 **/  

				if (pc.getInventory(). checkAddItem(item, count) == L1Inventory.OK) {
					if (pc.getInventory().consumeItem(40494, 2)) {
						elfwarehouse.tradeItem(item, count, pc.getInventory());
						
						//manager.LogWareHouseAppend("요정:찾", pc.getName(), "", item, count, objectId);
						LinAllManager.getInstance().EPWarehouseAppend(item.getLogName(), count, item.getName(), 1);
						/** 로그파일저장**/
						LoggerInstance.getInstance().addWarehouse(WarehouseType.Elf, false, pc, item, count);
						if(count>=500){
						}else{
						}
					} else {
						pc.sendPackets(new S_ServerMessage(337,"$767"));
						break;
					}
				} else {
					pc.sendPackets(new S_ServerMessage(270)); // \f1 가지고 있는 것이 무거워서 거래할 수 없습니다.
					break;
				}
			}
		
			/********************************************************************************************************		
			 *************************************** 패키지 창고 찾기  *************************************************
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

				/**   창고 찾기 부분 버그 방지 **/				
				if ( item == null ) {
					return;
				}
				if (!pc.getInventory().checkItem(L1ItemId.ADENA, 30 + 1)){
					pc.sendPackets(new S_SystemMessage("아데나가 부족합니다.."));
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
				/**   창고 찾기 부분 버그 방지 **/  
				
				 //** 중계기 노딜버그 막아 보자 **//
				  long nowtime = System.currentTimeMillis();
				  if(item.getItemdelay3() >=  nowtime ){
					  break;
				  }  
				  //** 중계기 노딜버그 막아 보자 **//
					if (item.getItemId() == 40308 && item.getCount() >= 10000000
							|| item.getCount() >= 1000 && item.getItemId() != 40308) {
					}
					 //신선한우유 수량성버그방지
				   if (!item.getItem().isToBeSavedAtOnce()) {
				   pc.getInventory().saveItem(item, L1PcInventory.COL_COUNT);
				   }	 
			      //신선한우유 수량성버그방지
				w.tradeItem(item, count, pc.getInventory());
				//manager.LogWareHouseAppend("패키지:찾", pc.getName(), "", item, count, objectId);
				/** 로그파일저장 **/
				LoggerInstance.getInstance().addWarehouse(WarehouseType.Private, false, pc, item, count);

			}
			/********************************************************************************************************		
			 *************************************** 특수 창고 맡기기 *************************************************
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
					pc.sendPackets(new S_SystemMessage("창고는 5레벨 이상 사용 가능 합니다."));
					return;
				}
				/* 버그방지 */
				// ** 엔진방어 **// By 도우너
				if (pc.getInventory().findItemId(40308).getCount() < 31) {
										S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"아데나가 부족합니다"); 
						pc.sendPackets(s_chatpacket);
					return;
				}
				// 월드맵상 내 계정과 같은 동일 한 계정을 가진 캐릭이 접속중이라면
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
				/* 버그방지 */
				if (item == null || item.getCount() < count || count <= 0
						|| item.getCount() <= 0) {
					break;
				}
			    if (!pc.isGm() && item.getItem().getItemId() == 40308)  {
		        	if (count > 10000000) {
		        		 pc.sendPackets(new S_SystemMessage("아데나는 1000만 단위씩 창고이용이 가능합니다."));
		        		 return;
		        	}
			    }
			    if(item.getItem().getUseType() == 44){
					pc.sendPackets(new S_SystemMessage("룬,유물은 창고이용이 불가능 합니다."));
					return;
				}
				if(item.getItem().getMaxUseTime() > 0){
					pc.sendPackets(new S_SystemMessage("시간제 아이템은 창고이용이 불가능 합니다."));
					return;
				}

				if (item.getItem().getItemId() == 41159 ||item.getItem().getItemId() == 41246){ 
					S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"해당 아이템은 창고 이용을 할 수 없습니다.");
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

				//manager.LogWareHouseAppend("특수:맡", pc.getName(), "", item, count, objectId);
				/** 로그파일저장 **/
				LoggerInstance.getInstance().addWarehouse(WarehouseType.Private, false, pc, item, count);
			}
			/********************************************************************************************************		
			 *************************************** 특수 창고 찾기 *************************************************
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
					pc.sendPackets(new S_SystemMessage("창고는 5레벨 이상 사용 가능 합니다."));
					return;
				}
			    if (!pc.isGm() && item.getItem().getItemId() == 40308)  {
		        	if (count > 10000000) {
		        		 pc.sendPackets(new S_SystemMessage("아데나는 1000만 단위씩 창고이용이 가능합니다."));
		        		 return;
		        	}
			    }
				/* 버그방지 */
				if (item.getItem().getItemId() == 41159 ||item.getItem().getItemId() == 41246){ //영혼 훈장
					S_ChatPacket s_chatpacket = new S_ChatPacket(pc, "해당 아이템은 창고 이용을 할 수 없습니다.");
					pc.sendPackets(s_chatpacket);
					return;
				}
				warehouse.tradeItem(item, count, pc.getInventory());
				//manager.LogWareHouseAppend("특수:찾", pc.getName(), "", item, count, objectId);
				/** 로그파일저장 **/
				LoggerInstance.getInstance().addWarehouse(WarehouseType.Private, false, pc, item, count);
			}
			/********************************************************************************************************		
			 *************************************** npc 상점 아이템 구매 *************************************************
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
				if(size >= 2){ //동시에 다른물건을 살수없게 2개가 선택된다면,
					pc.sendPackets(new S_SystemMessage("한번에 서로 다른아이템을 구입할 수 없습니다."));
					return;
				}
				if(pc.getMapId() == 800){
					if(itemcount > 15) {
						pc.sendPackets(new S_SystemMessage("최대 구매수량 : 잡템류(15) / 장비(1)"));
						return;
					}
				} 
				orderList.add(itemNumber, (int)itemcount , pc); 
			}
			int bugok = orderList.BugOk();
			if (bugok == 0){
				shop.sellItems(pc, orderList);
				//백섭복사 방지 수량성버그방지
				pc.saveInventory();
				//백섭복사 방지 수량성버그방지
			}
			/********************************************************************************************************		
			 *************************************** 개인 상점 아이템 구매 *************************************************
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
				// 품절이 발생해, 열람중의 아이템수와 리스트수가 다르다
				if (pc.getPartnersPrivateShopItemCount() != sellList.size()) {
					return;
				}

				for (int i = 0; i < size; i++) { // 구입 예정의 상품
					order = readD();
					count = readD();
					pssl = (L1PrivateShopSellList) sellList.get(order);
					itemObjectId = pssl.getItemObjectId();
					sellPrice = pssl.getSellPrice();
					sellTotalCount = pssl.getSellTotalCount(); // 팔 예정의 개수
					sellCount = pssl.getSellCount(); // 판 누계
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
						pc.sendPackets(new S_ServerMessage(905, "")); // 장비 하고 있는 아이템 구매못하게.
						continue;
					}

					if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) { // 용량 중량 확인 및 메세지 송신
						for (int j = 0; j < count; j++) { // 오버플로우를 체크
							if (sellPrice * j > 2000000000 || sellPrice * j < 0) {
								pc.sendPackets(new S_ServerMessage(904, // 총판 매가격은%d아데나를 초과할 수 없습니다.
										"2000000000"));
								return;
							}
						}
						price = count * sellPrice;	 

						/** 개인상점 버그방지 **/  					

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
							pc.sendPackets(new S_SystemMessage("상대방이 착용중인 아이템입니다."));
							return;
						}				
						if (price <= 0 ||price > 2000000000) return;
						/** 개인상점 버그방지 **/  

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
										// %0에 판매했습니다.
										pc.getName(), message));
								pssl.setSellCount(count + sellCount);							
								sellList.set(order, pssl);
								writeLogbuyPrivateShop(pc, targetPc, item, count, price);
								if (pssl.getSellCount() == pssl.getSellTotalCount()) { // 팔 예정의 개수를 팔았다
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
							pc.sendPackets(new S_ServerMessage(189)); // \f1아데나가 부족합니다.
							break;
						}
					} else {
						pc.sendPackets(new S_ServerMessage(270)); // \f1 가지고 있는 것이 무거워서 거래할 수 없습니다.
						break;
					}
				}
				// 품절된 아이템을 리스트의 말미로부터 삭제
				for (int i = 7; i >= 0; i--) {
					if (isRemoveFromList[i]) {
						sellList.remove(i);
					}
				}
			}
			/********************************************************************************************************		
			 *************************************** Npc 개인 상점 판매 *****************************************************
			 *********************************************************************************************************/			

		} else if (resultType == 1 && size != 0 && isPrivateNpcShop) { // 개인 상점에 아이템 매각
			
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
				//백섭복사 방지 수량성버그방지
				pc.saveInventory();
				//백섭복사 방지 수량성버그방지
			}
			
			/********************************************************************************************************		
			 *************************************** 개인 상점 판매 *****************************************************
			 *********************************************************************************************************/			
		} else if (resultType == 1 && size != 0 && isPrivateShop) { // 개인 상점에 아이템 매각
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
//						pc.sendPackets(new S_ServerMessage(210, item.getItem().getName())); // \f1%0은 버리거나 또는 타인에게 양일을 할 수 없습니다.
//						return;
//					}

					psbl = (L1PrivateShopBuyList) buyList.get(order);
					buyPrice = psbl.getBuyPrice();
					buyTotalCount = psbl.getBuyTotalCount(); // 살 예정의 개수
					buyCount = psbl.getBuyCount(); // 산 누계
					if (count > buyTotalCount - buyCount) {
						count = buyTotalCount - buyCount;
					}
					int buyItemObjectId = psbl.getItemObjectId();
					L1ItemInstance buyItem = targetPc.getInventory().getItem(buyItemObjectId);

					if (buyItem == null) {
						return;
					}
					if (item.isEquipped()) {
						pc.sendPackets(new S_ServerMessage(905)); // 장비 하고 있는 아이템은 판매할 수 없습니다.
						continue;
					}

					if (targetPc.getInventory().checkAddItem(item, count) == L1Inventory.OK) { // 용량 중량 확인 및 메세지 송신
						for (int j = 0; j < count; j++) { // 오버플로우를 체크
							if (buyPrice * j > 2000000000 || buyPrice * j < 0) {
								targetPc.sendPackets(new S_ServerMessage(904, // 총판 매가격은%d아데나를 초과할 수 없습니다.
										"2000000000"));
								return;
							}
						}	  
						/** 버그 방지 **/
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
						//** 개인상점 부분 비셔스 방어 **//	

						if (targetPc.getInventory().checkItem(L1ItemId.ADENA, count * buyPrice)) {
							L1ItemInstance adena = targetPc.getInventory().findItemId(L1ItemId.ADENA);
							if (adena != null) {
								targetPc.getInventory().tradeItem(adena, count * buyPrice, pc.getInventory());
								pc.getInventory().tradeItem(item, count, targetPc.getInventory());
								psbl.setBuyCount(count + buyCount);
								buyList.set(order, psbl);
								if (psbl.getBuyCount() == psbl.getBuyTotalCount()) {
									// 살 예정의 개수를 샀다
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
							targetPc.sendPackets(new S_ServerMessage(189)); // \f1아데나가 부족합니다.
							break;
						}
					} else {
						pc.sendPackets(new S_ServerMessage(271)); // \f1상대가 물건을 너무 가지고 있어 거래할 수 없습니다.
						break;
					}
				}
				// 매점한 아이템을 리스트의 말미로부터 삭제
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
		//manager.LogShopAppend("개인상점구매", pc.getName(), targetPc.getName(), item.getEnchantLevel(), itemadena, item.getBless(), count, item.getId());
		LinAllManager.getInstance().ShopAppend(item.getLogName(), count, price, targetPc.getName(), pc.getName());
		/** 로그파일저장 **/
		LoggerInstance.getInstance().개인상점구매(true, pc, targetPc, item, item.getCount());
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
			type = "맡겼습니다.";
		} else {
			type = "찾았습니다.";
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

	private boolean isTwoLogin(L1PcInstance c) {// 중복체크 변경 
		boolean bool = false;

		for (L1PcInstance target : L1World.getInstance().getAllPlayers()) {
			if (target.noPlayerCK || target.noPlayerck2)continue;
			/**로봇시스템 **/
			if(target.getRobotAi() != null) continue;
			/**로봇시스템 **/
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
	 * 월드상에 있는 모든 캐릭의 계정을 비교해 같은 계정이 있다면 true 없다면 false
	 * @param c L1PcInstance
	 * @return 있다면 true
	 */

}