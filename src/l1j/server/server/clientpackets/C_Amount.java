package l1j.server.server.clientpackets;

import java.util.Calendar;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.TimeZone;

import l1j.server.Config;
import l1j.server.server.GameClient;
import l1j.server.server.datatables.AuctionBoardTable;
import l1j.server.server.datatables.HouseTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.NpcActionTable;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.npc.L1NpcHtml;
import l1j.server.server.model.npc.action.L1NpcAction;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.storage.CharactersItemStorage;
import l1j.server.server.templates.L1AuctionBoard;
import l1j.server.server.templates.L1House;

public class C_Amount extends ClientBasePacket {

	private static final String C_AMOUNT = "[C] C_Amount";

	public C_Amount(byte[] decrypt, GameClient client) throws Exception {
		super(decrypt);
		if (client == null) return;
		int objectId = readD();
		int amount = readD();
		
		//アジト競売掲示板のバグを修正
		long _amount = amount;
		if(_amount <= 0){ //追加部分
			return;
		}
		
		@SuppressWarnings("unused")
		int c = readC();
		String s = readS();

		L1PcInstance pc = client.getActiveChar();
		if ( pc == null)return;
		L1NpcInstance npc = (L1NpcInstance) L1World.getInstance().findObject(objectId);	

		if (npc == null) {
			return;
		}

		String s1 = "";
		String s2 = "";
		try {
			StringTokenizer stringtokenizer = new StringTokenizer(s);
			s1 = stringtokenizer.nextToken();
			s2 = stringtokenizer.nextToken();
		} catch (NoSuchElementException e) {
			s1 = "";
			s2 = "";
		}
		if (s1.equalsIgnoreCase("agapply")) { //オークションに入札した場合
			String pcName = pc.getName();
			AuctionBoardTable boardTable = new AuctionBoardTable();
			for (L1AuctionBoard board : boardTable.getAuctionBoardTableList()) {
				if (pcName.equalsIgnoreCase(board.getBidder())) {
					pc.sendPackets(new S_ServerMessage(523)); //すでに他の家のオークションに参加しています。
					return;
				}
			}
			int houseId = Integer.valueOf(s2);
			L1AuctionBoard board = boardTable.getAuctionBoardTable(houseId);
			if (board != null) {
				int nowPrice = board.getPrice();
			    long _nowPrice = nowPrice;
			    if (_nowPrice <= 0 ){ //追加部分
			    	return;
			    }
			    //オークション掲示板関連のバグを追加 
			    if (_amount < _nowPrice){//追加部分
			       return; 
			    }
			    
			    if (pc.getInventory().findItemId(L1ItemId.ADENA).getCount() < _amount){//追加部分
					return; 
				}

				int nowBidderId = board.getBidderId();
				if (pc.getInventory().consumeItem(L1ItemId.ADENA, amount)) {
					// オークション掲示板を更新
					board.setPrice(amount);
					board.setBidder(pcName);
					board.setBidderId(pc.getId());
					boardTable.updateAuctionBoard(board);
					if (nowBidderId != 0) {
						//入札者にアデナを返金
						L1PcInstance bidPc = (L1PcInstance) L1World.getInstance().findObject(nowBidderId);
						if (bidPc != null) { //オンライン中
							bidPc.getInventory().storeItem(L1ItemId.ADENA,nowPrice);
							// あなたが提示された金額よりも少し高価な金額を提示した方が現われたので、残念ながら入札に失敗しました。 ％n
							// あなたがオークションに任せ％0アデナをお返しします。 ％nありがとうございます。 ％n％n
							bidPc.sendPackets(new S_ServerMessage(525, String.valueOf(nowPrice)));
						} else { // オフライン中
							L1ItemInstance item = ItemTable.getInstance().createItem(L1ItemId.ADENA);
							item.setCount(nowPrice);
							CharactersItemStorage storage = CharactersItemStorage.create();
							storage.storeItem(nowBidderId, item);
						}
					}
				} else {
					pc.sendPackets(new S_ServerMessage(189)); // \f1アデナが不足します。
				}
			}
		} else if (s1.equalsIgnoreCase("agsell")) { // 行売った場合
			int houseId = Integer.valueOf(s2);
			AuctionBoardTable boardTable = new AuctionBoardTable();
			L1AuctionBoard board = new L1AuctionBoard();

			L1Clan ownerClan = null;
			for (L1Clan clan : L1World.getInstance().getAllClans()) {
				if (clan.getLeaderId() == pc.getId()) {
					ownerClan = clan;
					break;
				}
			}
			
			if( ownerClan == null )
			{
				return;
			}

			L1House house = HouseTable.getInstance().getHouseTable(houseId);
			if( house == null || ownerClan.getHouseId() != house.getHouseId() )
			{
				return;
			}
			
			if( pc.getInventory().findItemId(L1ItemId.ADENA).getCount() < amount)
			{
				pc.sendPackets(new S_SystemMessage("所持金額以上を設定することができません。"));
				return;
			}

			if (board != null) {
				// オークション掲示板に新規書き込み
				board.setHouseId(houseId);
				board.setHouseName(house.getHouseName());
				board.setHouseArea(house.getHouseArea());
				TimeZone tz = TimeZone.getTimeZone(Config.TIME_ZONE);
				Calendar cal = Calendar.getInstance(tz);
				cal.add(Calendar.DATE, 1); //5日後
				cal.set(Calendar.MINUTE, 0); //分、秒は切り捨て
				cal.set(Calendar.SECOND, 0);
				board.setDeadline(cal);
				board.setPrice(amount);
				board.setLocation(house.getLocation());
				board.setOldOwner(pc.getName());
				board.setOldOwnerId(pc.getId());
				board.setBidder("");
				board.setBidderId(0);
				boardTable.insertAuctionBoard(board);

				house.setOnSale(true);  // オークション中の設定
				house.setPurchaseBasement(true); // 地下アジト未購入に設定
				HouseTable.getInstance().updateHouse(house); // DBに書き込まれ
			}
		} else {
		    L1NpcAction action = NpcActionTable.getInstance().get(s, pc, npc);

		    if (action != null) {
				L1NpcHtml result = action.executeWithAmount(s, pc, npc, amount);
				if (result != null) {
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), result));
				}
				return;
			}
		}
	}

	@Override
	public String getType() {
		return C_AMOUNT;
	}
}
