package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.GameSystem.Robot.L1RobotInstance;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Drop;
import l1j.server.server.utils.SQLUtil;
import manager.LinAllManagerInfoThread;

public class DropTable {

	private static Logger _log = Logger.getLogger(DropTable.class.getName());

	private static DropTable _instance;

	private final HashMap<Integer, ArrayList<L1Drop>> _droplists; // monsterごとのドロップリスト

	public static DropTable getInstance() {
		if (_instance == null) {
			_instance = new DropTable();
		}
		return _instance;
	}

	private DropTable() {
		_droplists = allDropList();
	}

	public static void reload() {
		DropTable oldInstance = _instance;
		_instance = new DropTable();
		oldInstance._droplists.clear();
	}

	private HashMap<Integer, ArrayList<L1Drop>> allDropList() {
		HashMap<Integer, ArrayList<L1Drop>> droplistMap = new HashMap<Integer, ArrayList<L1Drop>>();

		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("select * from droplist");
			rs = pstm.executeQuery();
			L1Drop drop = null;
			while (rs.next()) {
				int mobId = rs.getInt("mobId");
				int itemId = rs.getInt("itemId");
				int min = rs.getInt("min");
				int max = rs.getInt("max");
				int chance = rs.getInt("chance");
				int enchant = rs.getInt("Enchant");

				drop = new L1Drop(mobId, itemId, min, max, chance, enchant);

				ArrayList<L1Drop> dropList = droplistMap.get(drop.getMobid());
				if (dropList == null) {
					dropList = new ArrayList<L1Drop>();
					droplistMap.put(new Integer(drop.getMobid()), dropList);
				}
				dropList.add(drop);
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return droplistMap;
	}

	//インベントリツリーにドロップを設定
	public void setDrop(L1NpcInstance npc, L1Inventory inventory) {
		if(npc.getNpcId()==145684){
			System.out.println("ヴァラカスジェン");
		}
		if (Config.STANDBY_SERVER){ 
			return;
		}
		// ドロップリストの取得
		int mobId = npc.getNpcTemplate().get_npcId();
		ArrayList<L1Drop> dropList = _droplists.get(mobId);
		if (dropList == null) {
			return;
		}

		//レート取得
		double droprate = Config.RATE_DROP_ITEMS;
		if (droprate <= 0) {
			droprate = 0;
		}
		double adenarate = Config.RATE_DROP_ADENA;
		if (adenarate <= 0) {
			adenarate = 0;
		}
		if (droprate <= 0 && adenarate <= 0) {
			return;
		}

		int itemId;
		int itemCount;
		int addCount;
		int randomChance;
		L1ItemInstance item;
		/** 幻想イベント **/
		L1ItemInstance Fitem;		
		Random random = new Random(System.nanoTime());
		if(npc.getNpcId()==145684){
			System.out.println("DropListSize : "+dropList.size());
		}
		for (L1Drop drop : dropList) {
			// ドロップアイテムの取得
			itemId = drop.getItemid();
			if (adenarate == 0 && itemId == L1ItemId.ADENA) {
				continue; // アデナレート0にドロップがアデナの場合はスルー
			}

			// ドロップチャンス判定
			randomChance = random.nextInt(0xf4240) + 1;
			int npcMapid = npc.getMapId();
			if (npcMapid > 1017 && npcMapid < 1023 
					|| npcMapid > 6000 && npcMapid < 6999
					|| npcMapid > 2101 && npcMapid < 2201
					|| npcMapid > 9000 && npcMapid < 9099
					|| npcMapid == 1161){
				npcMapid = 1;
			}
			double rateOfMapId = MapsTable.getInstance().getDropRate(npc.getMap().getBaseMapId());
			double rateOfItem = DropItemTable.getInstance().getDropRate(itemId);
			if (droprate == 0 || drop.getChance() * droprate * rateOfMapId * rateOfItem < randomChance) {
				continue;
			}

			//ドロップ数を設定
			double amount = DropItemTable.getInstance().getDropAmount(itemId);
			int min = (int)(drop.getMin() * amount);
			int max = (int)(drop.getMax() * amount);

			itemCount = min;
			addCount = max - min + 1;

			if (addCount > 1) {
				itemCount += random.nextInt(addCount);
			}
			if (itemId == L1ItemId.ADENA) { //ドロップがアデナの場合はアデナレートをかける
				itemCount *= adenarate;
			}
			if (itemCount < 0) {
				itemCount = 0;
			}
			if (itemCount > 2000000000) {
				itemCount = 2000000000;
			}

			// アイテムの作成
			if (ItemTable.getInstance().getTemplate(itemId) != null) {
				item = ItemTable.getInstance().createItem(itemId);
				if (item == null) continue;
				item.setCount(itemCount);		
				if (drop.getEnchant() != 0) {
					System.out.println("[エラー] droplist：重なるアイテムエンチャンされる。（" + item.getItemId() + ")");
					item.setEnchantLevel(drop.getEnchant());
				}
				// アイテム格納
				inventory.storeItem(item);
			} else {
				_log.info("[ドロップリストの読み込み中]ないアイテムです：" + itemId);
			}
		}

		
		
		int[] lastabard = { 80453, 80454, 80455, 80456, 80457, 80458, 80459, 80460, 80461, 80462, 80463, 80452 };
		int[] tower = { 80450, 80451, 80466, 80467 };
		int[] glu = { 80464, 80465 };
		int[] oman = { 80468,80469,80470,80471,80472,80473,80474,80475,80476,80477 };
		int 드랍율 = random.nextInt(2000) + 1;
		int 라던 = random.nextInt(lastabard.length);
		int 상아탑 = random.nextInt(tower.length);
		int 본던 = random.nextInt(glu.length);
		int 오만 = random.nextInt(oman.length);
		switch (npc.getMapId()) {
		case 479:case 475:case 462:case 453:case 492:
			if (2 >= 드랍율) {
				inventory.storeItem(lastabard[라던], 1);
			}break;
		case 78:case 79:case 80:case 81:case 82:
			if (2 >= 드랍율) {// 象牙の塔
				inventory.storeItem(tower[상아탑], 1);
			}break;
		case 807:case 808:case 809:case 810:case 811:case 812:case 813:
			if (2 >= 드랍율) {// ボンドン
				inventory.storeItem(glu[본던], 1);
			}break;
		case 101:case 102:case 103:case 104:case 105:case 106:case 107:case 108:case 109:case 110:case 111:
			if (3 >= 드랍율) {// 傲慢
				inventory.storeItem(oman[오만], 1);
			}break;
		}


		/** 幻想イベント**/
		if (Config.ALT_FANTASYEVENT == true) {
			//Random random1 = new Random();
			int itemRandom = random.nextInt(100)+1;
			int countRandom = random.nextInt(100)+1;
			int item1Random = random.nextInt(100+1);
			int Fcount = 0;
			int Itemnum = 0;
			if(item1Random <= 50){
				Itemnum = 40127;
			} else {
				Itemnum = 40128;
			}
			if(countRandom <=90 ){
				Fcount = 1;
			} else if(countRandom >=91){
				Fcount = 2;
			}				
			if(itemRandom <=40 ){					
			} else if(itemRandom >=46 || itemRandom <= 70){
				Fitem = ItemTable.getInstance().createItem(Itemnum);
				Fitem.setCount(Fcount);
				inventory.storeItem(Fitem);				
			} else if(itemRandom >=96){
				Fitem = ItemTable.getInstance().createItem(Itemnum);
				Fitem.setCount(Fcount);
				inventory.storeItem(Fitem);					
			}				
		}
		/** 幻想イベント **/
	}

	// ドロップを分配
	public void dropShare(L1NpcInstance npc, ArrayList<?> acquisitorList, ArrayList<?> hateList, L1PcInstance pc) {
		if (Config.STANDBY_SERVER) {
			return;
		}

		L1Inventory inventory = npc.getInventory();
		int mobId = npc.getNpcTemplate().get_npcId();

		if (pc instanceof L1RobotInstance) {
			return;
		}
		/** ボスモンスターの自動分配**/
		if (mobId == 400016 || mobId == 400017 || mobId == 400017||mobId == 145684){
			Mapdrop(npc);
		}
		/**ボスモンスターの自動分配**/

		/** 大黒長老オートルーティング **/
		//if (mobId == 7000094){ 大黒長老（npc）;
		//}
		/** 大黒長老オートルーティング **/

		if (mobId == 5100 || mobId == 900013 || mobId == 900040){ // ドラゴンドロップ設定します。
			if (npc.getMapId() >= 1005 && npc.getMapId() <= 1022 
					|| npc.getMapId() > 6000 && npc.getMapId() < 6499
					|| npc.getMapId() > 6501 && npc.getMapId() < 6999){
				return;
			}
		}
		if (inventory.getItems() == null || inventory.getItems().size() <= 0){
			return;
		}
		if (acquisitorList.size() != hateList.size()) {
			return;
		}
		/** ロボットシステム **/
		if (pc.getRobotAi() != null) {
			return;
		}
		/** ロボットシステム **/
		// ヘイトの合計を取得
		int totalHate = 0;
		L1Character acquisitor;
		for (int i = hateList.size() - 1; i >= 0; i--) {
			acquisitor = (L1Character) acquisitorList.get(i);
			if ((Config.AUTO_LOOT == 0)
					// オートルーティング2の場合は、サーモンとペットは省略する
					&& (acquisitor instanceof L1SummonInstance || acquisitor instanceof L1PetInstance)) {
				acquisitorList.remove(i);
				hateList.remove(i);
			} else if (acquisitor != null && acquisitor.getMapId() == npc.getMapId()
					&& acquisitor.getLocation().getTileLineDistance(npc.getLocation()) <= Config.LOOTING_RANGE) {
				totalHate += (Integer) hateList.get(i);
			} else {
				// nullだったり死ぬもして遠かったら排除
				acquisitorList.remove(i);
				hateList.remove(i);
			}
		}

		// ドロップの分配
		L1Inventory targetInventory = null;
		L1PcInstance player;
		Random random = new Random();
		L1PcInstance[] partyMember;
		int randomInt;
		int chanceHate;
		for (int i = inventory.getSize(); i > 0; i--) {
			L1ItemInstance item = null;
			try {
				item = (L1ItemInstance) inventory.getItems().get(0);
				if (item == null) {
					continue;
				}
			} catch (Exception e) {
				System.out.println("ドロップリストエラー表示名：" + npc.getNpcId() + "[名前]：" + npc.getName());
			}

			int itemId = item.getItem().getItemId();
			if (item.getItem().getType2() == 0 && item.getItem().getType() == 2) { // light系アイテム
				item.setNowLighting(false);
			}
			if ((Config.AUTO_LOOT != 0 || AutoLoot.getInstance().isAutoLoot(itemId)) && totalHate > 0) {
				randomInt = random.nextInt(totalHate);
				chanceHate = 0;
				for (int j = hateList.size() - 1; j >= 0; j--) {
					chanceHate += (Integer) hateList.get(j);
					if (chanceHate > randomInt) {
						acquisitor = (L1Character) acquisitorList.get(j);

						if (acquisitor.getInventory().checkAddItem(item, item.getCount()) == L1Inventory.OK) {
							targetInventory = acquisitor.getInventory();
							if (acquisitor instanceof L1PcInstance) {
								player = (L1PcInstance) acquisitor;
								L1ItemInstance l1iteminstance = player.getInventory().findItemId(L1ItemId.ADENA);
								// 所持アデナをチェック
								if (l1iteminstance != null && l1iteminstance.getCount() > 2000000000) {
									targetInventory = L1World.getInstance().getInventory(acquisitor.getX(),acquisitor.getY(), acquisitor.getMapId());
									// 持つことができないので、足元に落とす
									player.sendPackets(new S_ServerMessage(166, "所持しているアデナ","20億を超えています。"));
								} else {
									if (player.isInParty()) { // パーティーの場合
										partyMember = player.getParty().getMembers();
										int Who = random.nextInt(partyMember.length);
										L1PcInstance pc1 = partyMember[Who];
										if (player.getLocation().getTileLineDistance(pc1.getLocation()) < 14) {
											if (item != null && item.getItem().getItemId() != L1ItemId.ADENA) {
												String 이름 = pc1.getName();
												String 아이템이름 = item.getName();
												targetInventory = pc1.getInventory();
												for (int p = 0; p < partyMember.length; p++) {
													if (player.RootMent) {
														partyMember[p].sendPackets(new S_SystemMessage("" + 아이템이름 + "獲得：" + 이름 + " (" + npc.getName() + ") "));
													}
												}
											}
										}
									} else if (player.RootMent) { // ソロの場合
										player.sendPackets(new S_ServerMessage(143, npc.getName(), item.getLogName()));
									}
								}
							}
						} else {
							targetInventory = L1World.getInstance().getInventory(acquisitor.getX(), acquisitor.getY(),acquisitor.getMapId()); 
							// 持つことができないので、足元に落とす
						}
						break;
					}
				}
			} else { // Nonオートルーティング
				/** 悪霊の種無条件オートルーティング **/
				if (itemId == 810008) {
					return; 
				}
				item.setDropMobId(mobId);

				int maxHatePc = -1;
				int maxHate = -1;

				for (int j = hateList.size() - 1; j >= 0; j--) {
					if (maxHate < (Integer) hateList.get(j)) {
						maxHatePc = j;
						maxHate = (Integer) hateList.get(j);
					}
				}

				if (maxHatePc != -1 && acquisitorList.get(maxHatePc) instanceof L1PcInstance) {
					item.startItemOwnerTimer((L1PcInstance) acquisitorList.get(maxHatePc));
				} else {
					item.startItemOwnerTimer(pc);
				}
				List<Integer> dirList = new ArrayList<Integer>();
				for (int j = 0; j < 8; j++) {
					dirList.add(j);
				}
				int x = 0;
				int y = 0;
				int dir = 0;
				do {
					if (dirList.size() == 0) {
						x = 0;
						y = 0;
						break;
					}
					randomInt = random.nextInt(dirList.size());
					dir = dirList.get(randomInt);
					dirList.remove(randomInt);
					switch (dir) {
					case 0:
						x = 0;
						y = -1;
						break;
					case 1:
						x = 1;
						y = -1;
						break;
					case 2:
						x = 1;
						y = 0;
						break;
					case 3:
						x = 1;
						y = 1;
						break;
					case 4:
						x = 0;
						y = 1;
						break;
					case 5:
						x = -1;
						y = 1;
						break;
					case 6:
						x = -1;
						y = 0;
						break;
					case 7:
						x = -1;
						y = -1;
						break;
					}
				} while (!npc.getMap().isPassable(npc.getX(), npc.getY(), dir));
				targetInventory = L1World.getInstance().getInventory(npc.getX() + x, npc.getY() + y, npc.getMapId());
			}
			if (pc.isInParty()) {
			if (item != null && item.getItem().getItemId() == L1ItemId.ADENA) {
				//
				List<L1PcInstance> temp = new ArrayList<L1PcInstance>();
				for (L1PcInstance partymember : pc.getParty().getMembers()) {
					if (pc.getLocation().getTileLineDistance(partymember.getLocation()) > 14) continue;
					temp.add(partymember);
				}
				//
				int 아데나 = item.getCount() / temp.size();
				for (L1PcInstance user : temp) {
					inventory.tradeItem(item, 아데나, user.getInventory());
					for (L1PcInstance partymember : pc.getParty().getMembers()) {
						if (pc.RootMent) {
							partymember.sendPackets(new S_SystemMessage("アデナ（" + 아데나 + "）獲得：" + user.getName() + " (" + npc.getName() + ") "));
						}
					}
				}
			}
			}
			
			if (targetInventory != null)
			inventory.tradeItem(item, item.getCount(), targetInventory);
		}
		npc.getLight().turnOnOffLight();
	}

	/** ボスモンスターの自動分配**/
	private void Mapdrop(L1NpcInstance npc) {
		L1Inventory inventory = npc.getInventory();
		L1ItemInstance item;
		L1Inventory targetInventory = null;
		L1PcInstance player;
		Random random = new Random();
		L1PcInstance acquisitor;
		ArrayList<L1PcInstance> acquisitorList = new ArrayList<L1PcInstance>();
		L1PcInstance[] pclist = L1World.getInstance().getAllPlayers3();
		for (L1PcInstance temppc : pclist) {
			if (temppc.getMapId() == npc.getMapId())
				acquisitorList.add(temppc);
		}
		for (int i = inventory.getSize(); i > 0; i--) {
			item = inventory.getItems().get(0);

			if (item.getItem().getType2() == 0 && item.getItem().getType() == 2) {
				item.setNowLighting(false);
			}
			acquisitor = acquisitorList.get(random.nextInt(acquisitorList.size()));
			if (acquisitor.getInventory().checkAddItem(item, item.getCount()) == L1Inventory.OK) {
				targetInventory = acquisitor.getInventory();
				player = acquisitor;
				L1ItemInstance l1iteminstance = player.getInventory().findItemId(L1ItemId.ADENA); //所持
				if (l1iteminstance != null && l1iteminstance.getCount() > 2000000000) {
					targetInventory = L1World.getInstance().getInventory(acquisitor.getX(), acquisitor.getY(),
							acquisitor.getMapId()); // 가질 수
					player.sendPackets(new S_ServerMessage(166, "所持しているアデナ", "20億を超えています。"));
				} else {
					for (L1PcInstance temppc : acquisitorList) {
						temppc.sendPackets(new S_ServerMessage(813, npc.getName(), item.getLogName(), player.getName()));
					}
				}
			} else {
				targetInventory = L1World.getInstance().getInventory(acquisitor.getX(), acquisitor.getY(),
						acquisitor.getMapId()); // 持つことができ
			}
			if (item.getItem().getItemId() == 40308)
				LinAllManagerInfoThread.AdenMake = Long.valueOf(LinAllManagerInfoThread.AdenMake.longValue() + item.getCount());
			inventory.tradeItem(item, item.getCount(), targetInventory);
		}
		npc.getLight().turnOnOffLight();
	}
	/*private void 大黒長老（L1NpcInstance npc）{
		L1Inventory inventory = npc.getInventory();
		L1ItemInstance item;
		L1Inventory targetInventory = null;
		L1PcInstance player;
		Random random = new Random();
		L1PcInstance acquisitor;
		ArrayList<L1PcInstance> acquisitorList = L1World.getInstance().getVisiblePlayer(npc, 2);
		if(acquisitorList.size() > 0){
			for (int i = inventory.getSize(); i > 0; i--) {
				item = inventory.getItems().get(0);

				if (item.getItem().getType2() == 0 && item.getItem().getType() == 2) {
					item.setNowLighting(false);
				}
				acquisitor = acquisitorList.get(random.nextInt(acquisitorList.size()));
				if (acquisitor.getInventory().checkAddItem(item, item.getCount()) == L1Inventory.OK) {
					targetInventory = acquisitor.getInventory();
					player = acquisitor;
					L1ItemInstance l1iteminstance= player.getInventory（）findItemId（L1ItemId.ADENA）; //所持
					if (l1iteminstance != null && l1iteminstance.getCount() > 2000000000) {
						targetInventory = L1World.getInstance().getInventory(acquisitor.getX(), acquisitor.getY(),
								acquisitor.getMapId()); //持つことができ
						player.sendPackets(new S_ServerMessage（166、 "所持しているアデナ"、 "20億を超えています。"））;
					} else {
						for (L1PcInstance temppc : acquisitorList) {
							temppc.sendPackets(new S_ServerMessage(813, npc.getName(), item.getLogName(), player.getName()));
						}
					}
				} else {
					targetInventory = L1World.getInstance().getInventory(acquisitor.getX(), acquisitor.getY(),acquisitor.getMapId());
				}
				inventory.tradeItem(item, item.getCount(), targetInventory);
			}
		}
		npc.getLight().turnOnOffLight();
	}*/

}