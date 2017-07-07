package l1j.server.server.clientpackets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.GameSystem.AttendanceController;
import l1j.server.server.ActionCodes;
import l1j.server.server.GameClient;
import l1j.server.server.Controller.WarTimeController;
import l1j.server.server.datatables.ClanTable;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.NpcActionTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.RankTable;
import l1j.server.server.datatables.WeekQuestTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1AccountAttendance;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1ClanJoin;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Party;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1Rank;
import l1j.server.server.model.L1Spawn;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1War;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.npc.L1NpcHtml;
import l1j.server.server.model.npc.action.L1NpcAction;
import l1j.server.server.serverpackets.S_ACTION_UI;
import l1j.server.server.serverpackets.S_ACTION_UI2;
import l1j.server.server.serverpackets.S_ChangeShape;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_DoActionShop;
import l1j.server.server.serverpackets.S_MatizCloudia;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_NewCreateItem;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_TamWindow;
import l1j.server.server.serverpackets.S_WeekQuest;
import l1j.server.server.serverpackets.S_Ranking2;
import l1j.server.server.templates.L1PrivateShopBuyList;
import l1j.server.server.templates.L1PrivateShopSellList;
import l1j.server.server.utils.SQLUtil;

public class C_ActionUi extends ClientBasePacket {

	Random _Random = new Random(System.nanoTime());
	
	private static final String C_ACTION_UI = "[C] C_ActionUi";
	private static final int CRAFT_ITEM = 0x36;
	private static final int CRAFT_ITEMLIST = 0x38; // 0x38;
	private static final int CRAFT_OK = 0x3a; // 첫번째 타입
	private static final int ACCOUNT_TAM = 0x01cc;//탐창
	private static final int ACCOUNT_TAM_CANCEL = 0x01e0;//탐 취소
	private static final int ACCOUNT_TAM_UPDATE = 0x013d;//탐
	private static final int 액션 = 0x013F;
	private static final int 수상한하늘정원 = 0x84;
	/*private static final int 혈맹가입 = 0x0142;
	private static final int 혈맹가입신청받기설정 = 0x0146;
	private static final int 혈맹모집세팅 = 0x014C;*/
	private static final int 가입대기 = 0x44;
	private static final int 공성관련 = 0x45;
	private static final int 표식설정 = 0x0152;
	public static final int LANK_UI = 135;
	public static final int 출석 = 0x222;
	private static final int 주퀘보상 = 811;
	private static final int 주퀘텔 = 815;
	private static final int 개인상점 = 817;
	private static final int 알람 = 143;
	private static final int 클라우디아 = 524;
	public C_ActionUi(byte abyte0[], GameClient client) {
		super(abyte0);
		int type = readH();
		L1PcInstance pc = client.getActiveChar();
		if (pc == null || pc.isGhost())
			return;
		int objectId = 0, itemtype = 0, itemcount = 0;
		L1Object obj;
		String s = null;
		L1NpcInstance npc;
		L1NpcAction action;
		//System.out.println("액션 > " + type);
		switch (type) {
		case 클라우디아:
			//0000: f1 0c 02 03 00 08 80 02 4a 0e b9 36
			//0000: f1 0c 02 03 00 08 b4 02 82 ae 02 78
			int step=readC();
			if(step==3 && pc.cL==0){
				pc.sendPackets(new S_MatizCloudia(2));
				pc.sendPackets(new S_MatizCloudia(3));
				pc.sendPackets(new S_MatizCloudia(1,0));
				pc.getInventory().storeItem(3000162,	1);
				pc.getInventory().storeItem(3000163, 1);
				pc.setExp(ExpTable.getExpByLevel(8));
			}else{
				pc.sendPackets(new S_MatizCloudia(4));
				pc.setExp(ExpTable.getExpByLevel(10));
			}
			break;
		case 알람:
			readH();
			readC();
			int num =readC(); //에르자베 1, 샌드 웜 2, 기사단 3 등등 S_MatizAlarm에서 추가하신 case대로 나옵니다.
			readC();
			switch(num){
				case 1 ://에르자베
					new L1Teleport().teleport(pc, 32899, 33244, (short)4,pc.getMoveState().getHeading(), true);
					break;
				case 2: //샌드웜
					new L1Teleport().teleport(pc, 32899, 33244, (short)4,pc.getMoveState().getHeading(), true);
					break;
				case 3:
					break;
			
			}
			break;
		case 개인상점:
	        L1ItemInstance sellitem = null;
	        L1ItemInstance buyitem = null;
	        ArrayList<L1PrivateShopSellList> sellList = pc.getSellList();
	        ArrayList<L1PrivateShopBuyList> buyList = pc.getBuyList();
	        boolean tradable = true;
	        int sellTotalCount = 0;
	        int buyTotalCount = 0;
	        readC();
	        readC();
	        readC();
	        int start = readC();
	        if (start == 0) {
	          boolean next = true;
	          while (next) {
	            int subtype = readC();
	            if (subtype == 0x22) {
	              next = false;
	              continue;
	            }
	            if (subtype == 0x12) {
	              sellTotalCount++;
	              int totallen = readC();
	              int len = readC();
	              int sellObjectId = readK(len - 3);
	              readC();
	              int pricelen = totallen - (len + 1);
	              int sellPrice = readK(pricelen);
	              readC();
	              int sellCount = readC();

	              L1ItemInstance checkItem = pc.getInventory().getItem(sellObjectId);
	              if (checkItem == null) {
	                continue;
	              }
	              if (sellObjectId != checkItem.getId()) {
	                tradable = false;
	                pc.sendPackets(new S_SystemMessage("비정상 아이템 입니다. 다시 시도해주세요."), true);
	              }

	              if ((!checkItem.isStackable()) && (sellCount != 1)) {
	                tradable = false;
	                pc.sendPackets(new S_SystemMessage("비정상 아이템 입니다. 다시 시도해주세요."), true);
	              }

	              if (sellCount > checkItem.getCount()) {
	                sellCount = checkItem.getCount();
	              }
	              if ((checkItem.getCount() < sellCount) || (checkItem.getCount() <= 0) || (sellCount <= 0)) {
	                tradable = false;
	                pc.sendPackets(new S_SystemMessage("비정상 아이템 입니다. 다시 시도해주세요."), true);
	              }

	              if (checkItem.getBless() >= 128) {
	                tradable = false;
	                pc.sendPackets(new S_ServerMessage(210, checkItem.getItem().getName()));
	              }

	              if (checkItem.getEndTime() != null) { pc.sendPackets(new S_SystemMessage("시간제 아이템은 상점등록이 불가능합니다."), true);
	                return; }
					L1DollInstance 인형 = null;
					for (Object 인형오브젝트 : pc.getDollList()) {
						if (인형오브젝트 instanceof L1DollInstance) {
							인형 = (L1DollInstance) 인형오브젝트;
							if (checkItem.getId() == 인형.getItemObjId()) {

								tradable = false;
								pc.sendPackets(new S_SystemMessage("소환중인 인형은 상점에 올릴 수 없습니다."), true);
							}
						}
					}

	              if (!checkItem.getItem().isTradable()) {
	                tradable = false;
	                pc.sendPackets(new S_ServerMessage(166, checkItem.getItem().getName(), "거래 불가능합니다. "), true);
	              }
					for (Object petObject : pc.getPetList().values().toArray()) {
						if (petObject instanceof L1PetInstance) {
							L1PetInstance pet = (L1PetInstance) petObject;
							if (checkItem.getId() == pet.getItemObjId()) { 
								tradable = false;
								pc.sendPackets(new S_ServerMessage(166,checkItem.getItem().getName(),"거래 불가능합니다. "), true);
								break;
							}
						}
					}
	              L1PrivateShopSellList pssl = new L1PrivateShopSellList();
	              pssl.setItemObjectId(sellObjectId);
	              pssl.setItemId(checkItem.getItemId());
	              pssl.setSellPrice(sellPrice);
	              pssl.setSellTotalCount(sellCount);
	              pssl.setUserName(pc.getName());
	              sellList.add(pssl);
	              
	            } else if (subtype == 0x1a) {
	              buyTotalCount++;
	              int totallen = readC();
	              int len = readC();
	              int buyObjectId = readK(len - 3);
	              readC();
	              int pricelen = totallen - (len + 1);
	              int buyPrice = readK(pricelen);
	              readC();
	              int buyCount = readC();
	              
	              L1ItemInstance checkItem = pc.getInventory().getItem(buyObjectId);
	              if (checkItem == null) {
	                continue;
	              }
	              if (buyObjectId != checkItem.getId()) {
	                tradable = false;
	                pc.sendPackets(new S_SystemMessage("비정상 아이템 입니다. 다시 시도해주세요."), true);
	              }

	              if ((!checkItem.isStackable()) && (buyCount != 1)) {
	                tradable = false;
	                pc.sendPackets(new S_SystemMessage("비정상 아이템 입니다. 다시 시도해주세요."), true);
	              }

	              if (buyCount > checkItem.getCount()) {
	                buyCount = checkItem.getCount();
	              }
	              if ((checkItem.getCount() < buyCount) || (checkItem.getCount() <= 0) || (buyCount <= 0)) {
	                tradable = false;
	                pc.sendPackets(new S_SystemMessage("비정상 아이템 입니다. 다시 시도해주세요."), true);
	              }

	              if (checkItem.getBless() >= 128) {
	                tradable = false;
	                pc.sendPackets(new S_ServerMessage(210, checkItem.getItem().getName()));
	              }

	              if (checkItem.getEndTime() != null) { pc.sendPackets(new S_SystemMessage("시간제 아이템은 상점등록이 불가능합니다."), true);
	                return; }
					L1DollInstance 인형 = null;
					for (Object 인형오브젝트 : pc.getDollList()) {
						if (인형오브젝트 instanceof L1DollInstance) {
							인형 = (L1DollInstance) 인형오브젝트;
							if (checkItem.getId() == 인형.getItemObjId()) {

								tradable = false;
								pc.sendPackets(new S_SystemMessage("소환중인 인형은 상점에 올릴 수 없습니다."), true);
							}
						}
					}

	              if (!checkItem.getItem().isTradable()) {
	                tradable = false;
	                pc.sendPackets(new S_ServerMessage(166, checkItem.getItem().getName(), "거래 불가능합니다. "), true);
	              }
					for (Object petObject : pc.getPetList().values().toArray()) {
						if (petObject instanceof L1PetInstance) {
							L1PetInstance pet = (L1PetInstance) petObject;
							if (checkItem.getId() == pet.getItemObjId()) { 
								tradable = false;
								pc.sendPackets(new S_ServerMessage(166,checkItem.getItem().getName(),"거래 불가능합니다. "), true);
								break;
							}
						}
					}
	              
	              L1PrivateShopBuyList psbl = new L1PrivateShopBuyList();
	              psbl.setItemObjectId(buyObjectId);
	              psbl.setItemId(checkItem.getItemId());
	              psbl.setBuyPrice(buyPrice);
	              psbl.setBuyTotalCount(buyCount);
	              psbl.setUserName(pc.getName());
	              buyList.add(psbl);
	            }
	          }
	          int len = readC();

	          byte[] chat = readByte(len);
	          readC();
	          int polylen = readC();

	          if ((sellTotalCount == 0) && (buyTotalCount == 0)) { pc.sendPackets(new S_ServerMessage(908), true);
	            pc.setPrivateShop(false);
	            pc.sendPackets(new S_DoActionGFX(pc.getId(), 3), true);
	            Broadcaster.broadcastPacket(pc, new S_DoActionGFX(pc.getId(), 3), true);
	            return; } 
	          	if (!tradable) { 
	            sellList.clear();
	            buyList.clear();
	            pc.setPrivateShop(false);
	            pc.sendPackets(new S_DoActionGFX(pc.getId(), 3), true);
	            Broadcaster.broadcastPacket(pc, new S_DoActionGFX(pc.getId(), 3), true);
	            return; } pc.getNetConnection().getAccount().updateShopOpenCount();
	          pc.sendPackets(new S_PacketBox(198, pc.getNetConnection().getAccount().Shop_open_count), true);

	          pc.setShopChat(chat);
	          pc.setPrivateShop(true);

	          pc.sendPackets(new S_DoActionShop(pc.getId(), 70, chat), true);
	          Broadcaster.broadcastPacket(pc, new S_DoActionShop(pc.getId(), 70, chat), true);
	          try
	          {
	        	  for (L1PrivateShopSellList pss : pc.getSellList()) {
						int sellp = pss.getSellPrice();
						int sellc = pss.getSellTotalCount();
						sellitem = pc.getInventory().getItem(
								pss.getItemObjectId());
						if (sellitem == null)
							continue;
						pc.SaveShop(pc, sellitem, sellp, sellc, 1);
						}
	          }
	          catch (Exception e) {
	            e.printStackTrace();
	          }
	          try
	          {
					for (L1PrivateShopBuyList psb : pc.getBuyList()) {
						int buyp = psb.getBuyPrice();
						int buyc = psb.getBuyTotalCount();
						buyitem = pc.getInventory().getItem(
								psb.getItemObjectId());
						if (buyitem == null)
							continue;
						pc.SaveShop(pc, buyitem, buyp, buyc, 0);
					}
	          }
	          catch (Exception e) {
	            e.printStackTrace();
	          }
	          try
	          {
	            String polyName = readS2(polylen);
	            int polyId = 0;
	            if (polyName.equalsIgnoreCase("tradezone1"))
	              polyId = 11480;
	            else if (polyName.equalsIgnoreCase("tradezone2"))
	              polyId = 11486;
	            else if (polyName.equalsIgnoreCase("tradezone3"))
	              polyId = 10047;
	            else if (polyName.equalsIgnoreCase("tradezone4"))
	              polyId = 11481;
	            else if (polyName.equalsIgnoreCase("tradezone5"))
	              polyId = 11486;
	            else if (polyName.equalsIgnoreCase("tradezone6"))
	              polyId = 10069;
	            else if (polyName.equalsIgnoreCase("tradezone7"))
	              polyId = 10034;
	            else if (polyName.equalsIgnoreCase("tradezone8")) {
	              polyId = 10047;
	            }
	            pc.상점변신 = polyId;
	            if (polyId != 0) {
	              pc.killSkillEffectTimer(67);
	              L1PolyMorph.undoPoly(pc);
	              L1ItemInstance weapon = pc.getWeapon();
	              if (weapon != null)
	                pc.getInventory().setEquipped(weapon, false, false, false,false);
	              pc.setTempCharGfx(polyId);
	              pc.sendPackets(new S_ChangeShape(pc.getId(), polyId, pc.getCurrentWeapon()));
	              if ((!pc.isGmInvis()) && (!pc.isInvisble())) {
	                Broadcaster.broadcastPacket(pc, new S_ChangeShape(pc.getId(), polyId));
	              }
	              S_CharVisualUpdate charVisual = new S_CharVisualUpdate(pc, 0x46);
	              pc.sendPackets(charVisual);
	              Broadcaster.broadcastPacket(pc, charVisual);
	            }
	          } catch (Exception e) {
	            pc.상점아이템삭제(pc.getId());
	            sellList.clear();
	            buyList.clear();
	            pc.setPrivateShop(false);
	            pc.sendPackets(new S_DoActionGFX(pc.getId(), 3), true);
	            Broadcaster.broadcastPacket(pc, new S_DoActionGFX(pc.getId(), 3), true);

	            clear();

	            return;
	          }
	        } else if (start == 1) {
	        	sellList.clear();
				buyList.clear();
				pc.setPrivateShop(false);
				pc.상점변신 = 0;
				pc.sendPackets(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Idle));
				pc.broadcastPacket(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Idle));
				L1PolyMorph.undoPolyPrivateShop(pc);
	        }

		
		break;
		case 주퀘텔:
				readH(); // 2byte
				readC(); // 1byte
				int line_index = readC(); // 1byte
				readC(); // 1byte
				int index = readC();
				int queue_index = line_index * 3 + index;
				int pcLevel = pc.getLevel();

				int npc_id;
				switch(pc.getWeekType()){
					case 1:
						npc_id=WeekQuestTable.getInstance().NpcidList.get(queue_index);
						break;
					case 2:
						npc_id=WeekQuestTable.getInstance().NpcidList2.get(queue_index);
						break;
					case 3:
						npc_id=WeekQuestTable.getInstance().NpcidList3.get(queue_index);
						break;
						default:
							npc_id = 0;
							break;
				}
				 

				if (!pc.getInventory().consumeItem(140100, 1)) {
					S_SystemMessage sm = new S_SystemMessage(
							"\\aG 축복의 순간이동 주문서가 부족 합니다.");
					pc.sendPackets(sm, true);
					return;
				}
				
				L1Spawn spawndata = WeekQuestTable.getInstance().SpawnData.get(npc_id);
				if (spawndata != null) {

					new L1Teleport().teleport(pc, spawndata.getLocX(), spawndata.getLocY(), spawndata.getMapId(), pc.getHeading(), true);
					S_SystemMessage sm = new S_SystemMessage(
							"\\aG잠시후 주간 퀘스트 몬스터 지역으로 이동합니다.");

					pc.sendPackets(sm, true);
				} else {
					System.out.println(" 도감 퀘스트 몬스터 : " + npc_id
							+ " 의 텔레포트 위치를 찾지 못했습니다.");

					pc.sendPackets(new S_WeekQuest(566));
				}

			
			break;
		case 주퀘보상:
			readH();
			readC();
			int line = readC();
			//System.out.println("LINE : "+line);
			switch(line){
				case 0:
					pc.setReward(0, true);
					break;
				case 1:
					pc.setReward(1, true);
					break;
				case 2:
					pc.setReward(2, true);
					break;
					
				
			
			}
			pc.getInventory().storeItem(500001, 1); // 군터의 인장
			pc.sendPackets(new S_WeekQuest(pc));
			break;
		case 출석: 
				readC();
				readH();
				int daycheck = readC();
				readC();
				int pcbang = readC();
				if (pc != null) {
					L1AccountAttendance acc = AttendanceController.findacc(pc.getAccountName());
					if (acc != null) {

						AttendanceController.clear(acc, pc, daycheck, pcbang);
					}
				}

			break;
		case 가입대기:{
			pc.sendPackets(new S_ACTION_UI2(S_ACTION_UI2.CLAN_JOIN_WAIT, true));
		}
			break;
		case LANK_UI:
			int subtype = readH();
			readC();
			int classType = readC();
			if (subtype == 4) {
				LinkedList<L1Rank> list = RankTable.getInstance().getMapByClass(classType);
				int size = list.size();
				if (size > 100) {
					List<L1Rank> list1 = list.subList(0, 100);
					if (size > 200) size = 200;
					List<L1Rank> list2 = list.subList(100, size);
					pc.sendPackets(new S_Ranking2(list1, classType, 2, 1));
					pc.sendPackets(new S_Ranking2(list2, classType, 2, 2));
				} else {
					pc.sendPackets(new S_Ranking2(list, classType, 1, 1));
				}
			} else {
				pc.sendPackets(new S_Ranking2(S_Ranking2.SHOW_RANK_UI));
			}
			break;
		/*case 혈맹가입신청받기설정:
			if (pc.getClanid() == 0 || (!pc.isCrown() && pc.getClanRank() != L1Clan.수호))
				return;
			readC();
			readH();
			int setting = readC();
			readC();
			int setting2 = readC();
			if (setting2 == 2) {
				pc.sendPackets(new S_SystemMessage("현재 암호 가입 유형으로 설정할 수 없습니다."));
				setting2 = 1;
			}
			
			pc.getClan().setJoinSetting(setting);
			pc.getClan().setJoinType(setting2);
			pc.sendPackets(new S_ACTION_UI2(S_ACTION_UI2.CLAN_JOIN_SETTING, setting, setting2));
			ClanTable.getInstance().updateClan(pc.getClan());
			pc.sendPackets(new S_ServerMessage(3980));
			break;*/
		/*case 혈맹가입:
			try {
				readC();
				readH();
				int length = readC();
				byte[] BYTE2 = readByte();
				
				if (pc.isCrown()) {
					pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.CLAN_JOIN_MESSAGE, 13));
					return;
				}
				
				if (pc.getClanid() != 0) {
					pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.CLAN_JOIN_MESSAGE, 9));
					return;
				}
				
				String clanname = new String(BYTE2, 0, length, "MS949");
				
				L1Clan clan = L1World.getInstance().getClan(clanname);
				if (clan == null) {
					pc.sendPackets(new S_SystemMessage("존재하지않는 혈맹입니다."));
					pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.CLAN_JOIN_MESSAGE, 4));
					return;
				}
				
				if (clan.getJoinSetting() == 0) {
					pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.CLAN_JOIN_MESSAGE, 8));
					return;
				}
				
				
				for(L1PcInstance cra : clan.getOnlineClanMember()){
					if(cra.getClanRank() >= 9){
						if (clan.getJoinSetting() == 0) {
							pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.CLAN_JOIN_MESSAGE, 8));
							return;
							
						} else if (clan.getJoinType() == 0) {
							pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.CLAN_JOIN_MESSAGE, 0));
							return;
						} else {
							cra.setTempID(pc.getId()); // 상대의 오브젝트 ID를 보존해 둔다
							S_Message_YN myn = new S_Message_YN(97, pc.getName());
							cra.sendPackets(myn, true);
							pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.CLAN_JOIN_MESSAGE, 1));
						}
					} else {
						pc.sendPackets(new S_SystemMessage("혈맹의 군주나 수호계급혈맹원이 접속되어있어야 사용가능합니다."));
						pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.CLAN_JOIN_MESSAGE, 11));
						return;
					}
				}
			} catch (Exception e) {
			} finally {
				clear();
			}
			break;*/
		/*case 혈맹모집세팅:
			if (pc.getClanid() == 0)
				return;
			pc.sendPackets(new S_ACTION_UI2(S_ACTION_UI2.CLAN_JOIN_SETTING, pc.getClan().getJoinSetting(), pc.getClan().getJoinType()));
			break;*/
		case 수상한하늘정원:
			if (!pc.PC방_버프) {
				pc.sendPackets(new S_SystemMessage("PC방 이용권을 사용중에만 사용 가능한 행동입니다."));
				return;
			}
			if (pc.getMapId() == 99 || pc.getMapId() == 6202) {
				pc.sendPackets(new S_SystemMessage("주위의 마력에의해 순간이동을 사용할 수 없습니다."));
				return;
			}

			if (!pc.getMap().isTeleportable()) {
				pc.sendPackets(new S_SystemMessage("주위의 마력에의해 순간이동을 사용할 수 없습니다."));
				return;
			}

			int ran = _Random.nextInt(4);

			if (ran == 0) {
				new L1Teleport().teleport(pc, 32779, 32825, (short) 622, pc.getHeading(), true);
			} else if (ran == 1) {
				new L1Teleport().teleport(pc, 32761, 32819, (short) 622, pc.getHeading(), true);
			} else if (ran == 2) {
				new L1Teleport().teleport(pc, 32756, 32837, (short) 622, pc.getHeading(), true);
			} else {
				new L1Teleport().teleport(pc, 32770, 32839, (short) 622, pc.getHeading(), true);
			}
			break;
		case CRAFT_ITEM: // 제작 시스템
			pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.CRAFT_ITEM));
			break;
		case CRAFT_ITEMLIST:
			readH(); // size;
			readC(); // dummy
			objectId = read4(read_size());
			obj = L1World.getInstance().findObject(objectId);
			if (obj instanceof L1NpcInstance) {
				npc = (L1NpcInstance) obj;
				pc.sendPackets(new S_ACTION_UI(npc));
			}
			break;
		case CRAFT_OK:
		case 92:
			readH(); // size;
			readC(); // dummy
			objectId = read4(read_size());
			readC(); // dummy
			itemtype = read4(read_size());
			readC(); // dummy
			itemcount = read4(read_size());
			if (itemcount < 1 || itemcount > 99) {
				return;
			}
			s = Integer.toString(itemtype);
			obj = L1World.getInstance().findObject(objectId);
			if (obj instanceof L1NpcInstance) {
				npc = (L1NpcInstance) obj;
				if (itemtype == 116) {
					if ((pc.getInventory().checkEnchantItem(5, 8, 1) // [+7]마력의
																		// 단검
							|| pc.getInventory().checkEnchantItem(6, 8, 1)
							|| pc.getInventory().checkEnchantItem(32, 8, 1)
							|| pc.getInventory().checkEnchantItem(37, 8, 1)
							|| pc.getInventory().checkEnchantItem(41, 8, 1)
							|| pc.getInventory().checkEnchantItem(42, 8, 1)
							|| pc.getInventory().checkEnchantItem(52, 8, 1)
							|| pc.getInventory().checkEnchantItem(64, 8, 1)
							|| pc.getInventory().checkEnchantItem(99, 8, 1)
							|| pc.getInventory().checkEnchantItem(104, 8, 1)
							|| pc.getInventory().checkEnchantItem(125, 8, 1)
							|| pc.getInventory().checkEnchantItem(129, 8, 1)
							|| pc.getInventory().checkEnchantItem(131, 8, 1)
							|| pc.getInventory().checkEnchantItem(145, 8, 1) || pc.getInventory().checkEnchantItem(148, 8, 1) || pc.getInventory().checkEnchantItem(180, 8, 1) || pc
							.getInventory().checkEnchantItem(181, 8, 1)) && pc.getInventory().checkItem(40308, 5000000)) {
						if (pc.getInventory().consumeEnchantItem(5, 8, 1) || pc.getInventory().consumeEnchantItem(6, 8, 1) || pc.getInventory().consumeEnchantItem(32, 8, 1)
								|| pc.getInventory().consumeEnchantItem(37, 8, 1) || pc.getInventory().consumeEnchantItem(41, 8, 1)
								|| pc.getInventory().consumeEnchantItem(42, 8, 1) || pc.getInventory().consumeEnchantItem(52, 8, 1)
								|| pc.getInventory().consumeEnchantItem(64, 8, 1) || pc.getInventory().consumeEnchantItem(99, 8, 1)
								|| pc.getInventory().consumeEnchantItem(104, 8, 1) || pc.getInventory().consumeEnchantItem(125, 8, 1)
								|| pc.getInventory().consumeEnchantItem(129, 8, 1) || pc.getInventory().consumeEnchantItem(131, 8, 1)
								|| pc.getInventory().consumeEnchantItem(145, 8, 1) || pc.getInventory().consumeEnchantItem(148, 8, 1)
								|| pc.getInventory().consumeEnchantItem(180, 8, 1) || pc.getInventory().consumeEnchantItem(181, 8, 1)) {
							;
						}
						pc.getInventory().consumeItem(40308, 5000000);
						인첸트지급(pc, 602, 1, 7);
					}
				} else if (itemtype == 123) {
					if ((pc.getInventory().checkEnchantItem(5, 9, 1) // [+8]마력의
																		// 단검
							|| pc.getInventory().checkEnchantItem(6, 9, 1)
							|| pc.getInventory().checkEnchantItem(32, 9, 1)
							|| pc.getInventory().checkEnchantItem(37, 9, 1)
							|| pc.getInventory().checkEnchantItem(41, 9, 1)
							|| pc.getInventory().checkEnchantItem(42, 9, 1)
							|| pc.getInventory().checkEnchantItem(52, 9, 1)
							|| pc.getInventory().checkEnchantItem(64, 9, 1)
							|| pc.getInventory().checkEnchantItem(99, 9, 1)
							|| pc.getInventory().checkEnchantItem(104, 9, 1)
							|| pc.getInventory().checkEnchantItem(125, 9, 1)
							|| pc.getInventory().checkEnchantItem(129, 9, 1)
							|| pc.getInventory().checkEnchantItem(131, 9, 1)
							|| pc.getInventory().checkEnchantItem(145, 9, 1) || pc.getInventory().checkEnchantItem(148, 9, 1) || pc.getInventory().checkEnchantItem(180, 9, 1) || pc
							.getInventory().checkEnchantItem(181, 9, 1)) && pc.getInventory().checkItem(40308, 10000000)) {
						if (pc.getInventory().consumeEnchantItem(5, 9, 1) || pc.getInventory().consumeEnchantItem(6, 9, 1) || pc.getInventory().consumeEnchantItem(32, 9, 1)
								|| pc.getInventory().consumeEnchantItem(37, 9, 1) || pc.getInventory().consumeEnchantItem(41, 9, 1)
								|| pc.getInventory().consumeEnchantItem(42, 9, 1) || pc.getInventory().consumeEnchantItem(52, 9, 1)
								|| pc.getInventory().consumeEnchantItem(64, 9, 1) || pc.getInventory().consumeEnchantItem(99, 9, 1)
								|| pc.getInventory().consumeEnchantItem(104, 9, 1) || pc.getInventory().consumeEnchantItem(125, 9, 1)
								|| pc.getInventory().consumeEnchantItem(129, 9, 1) || pc.getInventory().consumeEnchantItem(131, 9, 1)
								|| pc.getInventory().consumeEnchantItem(145, 9, 1) || pc.getInventory().consumeEnchantItem(148, 9, 1)
								|| pc.getInventory().consumeEnchantItem(180, 9, 1) || pc.getInventory().consumeEnchantItem(181, 9, 1)) {
							;
						}
						pc.getInventory().consumeItem(40308, 10000000);
						인첸트지급(pc, 602, 1, 8);
					}
					
					/** 엘릭서 제작아이템 확률 **/
				} else if (itemtype == 1043) {//엘릭서 [STR]
					Random random = new Random();
					if ((pc.getInventory().checkItem(820018, 35) && pc.getInventory().checkItem(410061, 2))) {//재료아이템
						if (pc.getInventory().consumeItem(820018, 35) && pc.getInventory().consumeItem(410061, 2)) {//아이템 회수
							;
						}
						if (random.nextInt(100) < 10) { // 20%의 확률로 성공
							인첸트지급(pc, 40033, 1, 0);
							pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
							Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 성공하여 \\aG'엘릭서[STR]'\\aA을 획득"));
						} else { // 나머지 확률 실패
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 실패 하였습니다."));
						}
					} else {
						pc.sendPackets(new S_SystemMessage("재료가 부족합니다"));
					}
				} else if (itemtype == 1044) {//엘릭서 [STR]
					Random random = new Random();
					if ((pc.getInventory().checkItem(820018, 35) && pc.getInventory().checkItem(410061, 2))) {//재료아이템
						if (pc.getInventory().consumeItem(820018, 35) && pc.getInventory().consumeItem(410061, 2)) {//아이템 회수
							;
						}
						if (random.nextInt(100) < 30) { // 20%의 확률로 성공
							인첸트지급(pc, 40034, 1, 0);
							pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
							Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 성공하여 \\aG'엘릭서[STR]'\\aA을 획득"));
						} else { // 나머지 확률 실패
							//pc.getInventory().consumeEnchantItem(40308, 0, 1);
							//pc.getInventory().consumeEnchantItem(40308, 0, 1);
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 실패 하였습니다."));
						}
					} else {
						pc.sendPackets(new S_SystemMessage("재료가 부족합니다"));
					}
				} else if (itemtype == 1045) {//엘릭서 [DEX]
					Random random = new Random();
					if ((pc.getInventory().checkItem(820018, 35) && pc.getInventory().checkItem(410061, 2))) {//재료아이템
						if (pc.getInventory().consumeItem(820018, 35) && pc.getInventory().consumeItem(410061, 2)) {//아이템 회수
							;
						}
						if (random.nextInt(100) < 30) { // 20%의 확률로 성공
							인첸트지급(pc, 40035, 1, 0);
							pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
							Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 성공하여 \\aG'엘릭서[DEX]'\\aA을 획득"));
						} else { // 나머지 확률 실패
							//pc.getInventory().consumeEnchantItem(40308, 0, 1);
							//pc.getInventory().consumeEnchantItem(40308, 0, 1);
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 실패 하였습니다."));
						}
					} else {
						pc.sendPackets(new S_SystemMessage("재료가 부족합니다"));
					}
				} else if (itemtype == 1046) {//엘릭서 [INT]
					Random random = new Random();
					if ((pc.getInventory().checkItem(820018, 35) && pc.getInventory().checkItem(410061, 2))) {//재료아이템
						if (pc.getInventory().consumeItem(820018, 35) && pc.getInventory().consumeItem(410061, 2)) {//아이템 회수
							;
						}
						if (random.nextInt(100) < 30) { // 20%의 확률로 성공
							인첸트지급(pc, 40036, 1, 0);
							pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
							Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 성공하여 \\aG'엘릭서[INT]'\\aA을 획득"));
						} else { // 나머지 확률 실패
							//pc.getInventory().consumeEnchantItem(40308, 0, 1);
							//pc.getInventory().consumeEnchantItem(40308, 0, 1);
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 실패 하였습니다."));
						}
					} else {
						pc.sendPackets(new S_SystemMessage("재료가 부족합니다"));
					}
				} else if (itemtype == 1047) {//엘릭서 [WIS]
					Random random = new Random();
					if ((pc.getInventory().checkItem(820018, 35) && pc.getInventory().checkItem(410061, 2))) {//재료아이템
						if (pc.getInventory().consumeItem(820018, 35) && pc.getInventory().consumeItem(410061, 2)) {//아이템 회수
							;
						}
						if (random.nextInt(100) < 30) { // 20%의 확률로 성공
							인첸트지급(pc, 40037, 1, 0);
							pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
							Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 성공하여 \\aG'엘릭서[WIS]'\\aA을 획득"));
						} else { // 나머지 확률 실패
							//pc.getInventory().consumeEnchantItem(40308, 0, 1);
							//pc.getInventory().consumeEnchantItem(40308, 0, 1);
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 실패 하였습니다."));
						}
					} else {
						pc.sendPackets(new S_SystemMessage("재료가 부족합니다"));
					}
				} else if (itemtype == 1048) {//엘릭서 [CHA]
					Random random = new Random();
					if ((pc.getInventory().checkItem(820018, 35) && pc.getInventory().checkItem(410061, 2))) {//재료아이템
						if (pc.getInventory().consumeItem(820018, 35) && pc.getInventory().consumeItem(410061, 2)) {//아이템 회수
							;
						}
						if (random.nextInt(100) < 30) { // 20%의 확률로 성공
							인첸트지급(pc, 40038, 1, 0);
							pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
							Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 성공하여 \\aG'엘릭서[CHA]'\\aA을 획득"));
						} else { // 나머지 확률 실패
							//pc.getInventory().consumeEnchantItem(40308, 0, 1);
							//pc.getInventory().consumeEnchantItem(40308, 0, 1);
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 실패 하였습니다."));
						}
					} else {
						pc.sendPackets(new S_SystemMessage("재료가 부족합니다"));
					}
					
					
				} else if (itemtype == 2747) {//오림 주문서
					Random random = new Random();
					if (pc.getInventory().checkItem(810012, 3)) {//재료아이템
						if (pc.getInventory().consumeItem(810012, 3)) {//아이템 회수
							;
						}
						if (random.nextInt(100) < 5) { // 20%의 확률로 성공
							인첸트지급(pc, 810013, 1, 0);
							pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
							Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
							pc.sendPackets(new S_SystemMessage("제작에 성공 하였습니다."));
						} else { // 나머지 확률 실패
							//pc.getInventory().consumeEnchantItem(40308, 0, 1);
							//pc.getInventory().consumeEnchantItem(40308, 0, 1);
							pc.sendPackets(new S_SystemMessage("제작에 실패 하였습니다."));
						}
					} else {
						pc.sendPackets(new S_SystemMessage("재료가 부족합니다."));
					}
					
					
					/** 아놀드 제작아이템 **/
				} else if (itemtype == 1647) {//양손검
					if ((pc.getInventory().checkEnchantItem(307, 10, 1))) {
						pc.getInventory().consumeEnchantItem(307, 10, 1);
						인첸트지급(pc, 30148, 1, 0);
					}
				} else if (itemtype == 1648) {//지팡이
					if ((pc.getInventory().checkEnchantItem(308, 10, 1))) {
						pc.getInventory().consumeEnchantItem(308, 10, 1);
						인첸트지급(pc, 30148, 1, 0);
					}
				} else if (itemtype == 1649) {//검
					if ((pc.getInventory().checkEnchantItem(309, 10, 1))) {
						pc.getInventory().consumeEnchantItem(309, 10, 1);
						인첸트지급(pc, 30148, 1, 0);
					}
				} else if (itemtype == 1650) {//활
					if ((pc.getInventory().checkEnchantItem(310, 10, 1))) {
						pc.getInventory().consumeEnchantItem(310, 10, 1);
						인첸트지급(pc, 30148, 1, 0);
					}
				} else if (itemtype == 1651) {//이도류
					if ((pc.getInventory().checkEnchantItem(311, 10, 1))) {
						pc.getInventory().consumeEnchantItem(311, 10, 1);
						인첸트지급(pc, 30148, 1, 0);
					}
				} else if (itemtype == 1652) {//체인소드
					if ((pc.getInventory().checkEnchantItem(312, 10, 1))) {
						pc.getInventory().consumeEnchantItem(312, 10, 1);
						인첸트지급(pc, 30148, 1, 0);
					}
				} else if (itemtype == 1653) {//키링크
					if ((pc.getInventory().checkEnchantItem(313, 10, 1))) {
						pc.getInventory().consumeEnchantItem(313, 10, 1);
						인첸트지급(pc, 30148, 1, 0);
					}
				} else if (itemtype == 1654) {//도끼
					if ((pc.getInventory().checkEnchantItem(314, 10, 1))) {
						pc.getInventory().consumeEnchantItem(314, 10, 1);
						인첸트지급(pc, 30148, 1, 0);
					}
				} else if (itemtype == 1655) {//장갑
					if ((pc.getInventory().checkEnchantItem(21095, 10, 1))) {
						pc.getInventory().consumeEnchantItem(21095, 10, 1);
						인첸트지급(pc, 30148, 1, 0);
					}
					
					
					/** 타라스 제작아이템 **/
				} else if (itemtype == 214) {//대마법사모자
					Random random = new Random();
					if ((pc.getInventory().checkItem(20040, 1) || pc.getInventory().checkItem(20025, 1) || pc.getInventory().checkItem(20018, 1)
							|| pc.getInventory().checkItem(20029, 1) || pc.getInventory().checkItem(410061, 200) || pc.getInventory().checkItem(41246, 100000))) {// 재료아이템
						
						if (pc.getInventory().consumeItem(20040, 1) && pc.getInventory().consumeItem(20025, 1) && pc.getInventory().consumeItem(20018, 1)
								&& pc.getInventory().consumeItem(20029, 1) && pc.getInventory().consumeItem(410061, 200) && pc.getInventory().consumeItem(41246, 100000)) {// 아이템 회수
							;
						}
						if (random.nextInt(100) < 100) { // 100%의 확률로 성공
							인첸트지급(pc, 202022, 1, 0);
							pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
							Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 성공하여 \\aG'대마법사모자'\\aA를 획득"));
						} else { // 나머지 확률 실패
							//pc.getInventory().consumeEnchantItem(40308, 0, 1);
							//pc.getInventory().consumeEnchantItem(40308, 0, 1);
						}
					} else {
						pc.sendPackets(new S_SystemMessage("재료가 부족합니다"));
					}
				} else if (itemtype == 2057) {//그레이스 아바타
					Random random = new Random();
					if ((pc.getInventory().checkItem(3000114, 1))) {// 재료아이템
						if (pc.getInventory().consumeItem(3000114, 1)) {// 아이템 회수
							;
						}
						if (random.nextInt(100) < 100) { // 100%의 확률로 성공
							인첸트지급(pc, 3000090, 1, 0);
							pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
							Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 성공하여 \\aG'그레이스 아바타'\\aA를 획득"));
						} else { // 나머지 확률 실패
							//pc.getInventory().consumeEnchantItem(40308, 0, 1);
							//pc.getInventory().consumeEnchantItem(40308, 0, 1);
						}
					} else {
						pc.sendPackets(new S_SystemMessage("재료가 부족합니다"));
					}
				} else if (itemtype == 2058) {//앱솔루트 블레이드
					Random random = new Random();
					if ((pc.getInventory().checkItem(3000110, 1))) {// 재료아이템
						if (pc.getInventory().consumeItem(3000110, 1)) {// 아이템 회수
							;
						}
						if (random.nextInt(100) < 100) { // 100%의 확률로 성공
							인첸트지급(pc, 3000092, 1, 0);
							pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
							Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 성공하여 \\aG'앱솔루트 블레이드'\\aA를 획득"));
						} else { // 나머지 확률 실패
							//pc.getInventory().consumeEnchantItem(40308, 0, 1);
							//pc.getInventory().consumeEnchantItem(40308, 0, 1);
						}
					} else {
						pc.sendPackets(new S_SystemMessage("재료가 부족합니다"));
					}
				} else if (itemtype == 2059) {//소울 배리어
					Random random = new Random();
					if ((pc.getInventory().checkItem(3000116, 1))) {// 재료아이템
						if (pc.getInventory().consumeItem(3000116, 1)) {// 아이템 회수
							;
						}
						if (random.nextInt(100) < 100) { // 100%의 확률로 성공
							인첸트지급(pc, 3000091, 1, 0);
							pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
							Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 성공하여 \\aG'소울 배리어'\\aA를 획득"));
						} else { // 나머지 확률 실패
							//pc.getInventory().consumeEnchantItem(40308, 0, 1);
							//pc.getInventory().consumeEnchantItem(40308, 0, 1);
						}
					} else {
						pc.sendPackets(new S_SystemMessage("재료가 부족합니다"));
					}
				} else if (itemtype == 2060) {//(데스힐)
					Random random = new Random();
					if ((pc.getInventory().checkItem(3000111, 1))) {// 재료아이템
						if (pc.getInventory().consumeItem(3000111, 1)) {// 아이템 회수
							;
						}
						if (random.nextInt(100) < 100) { // 100%의 확률로 성공
							인첸트지급(pc, 3000095, 1, 0);
							pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
							Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 성공하여 \\aG'데스힐'\\aA을 획득"));
						} else { // 나머지 확률 실패
							//pc.getInventory().consumeEnchantItem(40308, 0, 1);
							//pc.getInventory().consumeEnchantItem(40308, 0, 1);
						}
					} else {
						pc.sendPackets(new S_SystemMessage("재료가 부족합니다"));
					}
				} else if (itemtype == 2061) {//어쌔신
					Random random = new Random();
					if ((pc.getInventory().checkItem(3000117, 1))) {// 재료아이템
						if (pc.getInventory().consumeItem(3000117, 1)) {// 아이템 회수
							;
						}
						if (random.nextInt(100) < 100) { // 100%의 확률로 성공
							인첸트지급(pc, 3000089, 1, 0);
							pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
							Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 성공하여 \\aG'흑정령의수정(어쌔신)'\\aA을 획득"));
						} else { // 나머지 확률 실패
							//pc.getInventory().consumeEnchantItem(40308, 0, 1);
							//pc.getInventory().consumeEnchantItem(40308, 0, 1);
						}
					} else {
						pc.sendPackets(new S_SystemMessage("재료가 부족합니다"));
					}
				} else if (itemtype == 2062) {//블레이징 
					Random random = new Random();
					if ((pc.getInventory().checkItem(3000117, 1))) {// 재료아이템
						if (pc.getInventory().consumeItem(3000117, 1)) {// 아이템 회수
							;
						}
						if (random.nextInt(100) < 100) { // 100%의 확률로 성공
							인첸트지급(pc, 3000097, 1, 0);
							pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
							Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 성공하여 \\aG'블레이징 스피릿츠'\\aA를 획득"));
						} else { // 나머지 확률 실패
							//pc.getInventory().consumeEnchantItem(40308, 0, 1);
							//pc.getInventory().consumeEnchantItem(40308, 0, 1);
						}
					} else {
						pc.sendPackets(new S_SystemMessage("재료가 부족합니다"));
					}
				} else if (itemtype == 2064) {//디스트로이
					Random random = new Random();
					if ((pc.getInventory().checkItem(3000113, 1))) {// 재료아이템
						if (pc.getInventory().consumeItem(3000113, 1)) {// 아이템 회수
							;
						}
						if (random.nextInt(100) < 100) { // 100%의 확률로 성공
							인첸트지급(pc, 3000093, 1, 0);
							pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
							Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 성공하여 \\aG'용기사서판(디스트로이)'\\aA를 획득"));
						} else { // 나머지 확률 실패
							//pc.getInventory().consumeEnchantItem(40308, 0, 1);
							//pc.getInventory().consumeEnchantItem(40308, 0, 1);
						}
					} else {
						pc.sendPackets(new S_SystemMessage("재료가 부족합니다"));
					}
				} else if (itemtype == 2063) {//임팩트
					Random random = new Random();
					if ((pc.getInventory().checkItem(3000112, 1))) {// 재료아이템
						if (pc.getInventory().consumeItem(3000112, 1)) {// 아이템 회수
							;
						}
						if (random.nextInt(100) < 100) { // 100%의 확률로 성공
							인첸트지급(pc, 3000096, 1, 0);
							pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
							Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 성공하여 \\aG'기억의 수정(임팩트)'\\aA를 획득"));
						} else { // 나머지 확률 실패
							//pc.getInventory().consumeEnchantItem(40308, 0, 1);
							//pc.getInventory().consumeEnchantItem(40308, 0, 1);
						}
					} else {
						pc.sendPackets(new S_SystemMessage("재료가 부족합니다"));
					}
				} else if (itemtype == 2065) {//타이탄 라이징
					Random random = new Random();
					if ((pc.getInventory().checkItem(3000115, 1))) {// 재료아이템
						if (pc.getInventory().consumeItem(3000115, 1)) {// 아이템 회수
							;
						}
						if (random.nextInt(100) < 100) { // 100%의 확률로 성공
							인첸트지급(pc, 3000094, 1, 0);
							pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
							Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 성공하여 \\aG'전사의 인장(타이탄:라이징)'\\aA을 획득"));
						} else { // 나머지 확률 실패
							//pc.getInventory().consumeEnchantItem(40308, 0, 1);
							//pc.getInventory().consumeEnchantItem(40308, 0, 1);
						}
					} else {
						pc.sendPackets(new S_SystemMessage("재료가 부족합니다"));
					}
					
					
					/** 레옹 제작아이템 확률 **/
				} else if (itemtype == 1763) {//고대암석각반
					Random random = new Random();
					if ((pc.getInventory().checkItem(3000065, 1) || pc.getInventory().checkItem(3000064, 1))) {// 재료아이템
						if (pc.getInventory().consumeItem(3000065, 1) && pc.getInventory().consumeItem(3000064, 1)) {// 아이템 회수
							;
						}
						if (random.nextInt(100) < 20) { // 10%의 확률로 성공
							인첸트지급(pc, 900011, 1, 0);
							pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
							Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 성공하여 \\aG'고대암석각반'\\aA을 획득"));
						} else { // 나머지 확률 실패
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 실패 하였습니다."));
						}
					} else {
						pc.sendPackets(new S_SystemMessage("재료가 부족합니다"));
					}
				} else if (itemtype == 1764) {//고대암석부츠
					Random random = new Random();
					if ((pc.getInventory().checkItem(3000065, 1) || pc.getInventory().checkItem(3000064, 1))) {// 재료아이템
						if (pc.getInventory().consumeItem(3000065, 1) && pc.getInventory().consumeItem(3000064, 1)) {// 아이템 회수
							;
						}
						if (random.nextInt(100) < 20) { // 10%의 확률로 성공
							인첸트지급(pc, 900012, 1, 0);
							pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
							Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 성공하여 \\aG'고대암석부츠'\\aA를 획득!"));
						} else { // 나머지 확률 실패
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 실패 하였습니다."));
						}
					} else {
						pc.sendPackets(new S_SystemMessage("재료가 부족합니다"));
					}
				} else if (itemtype == 1765) {//고대암석망토
					Random random = new Random();
					if ((pc.getInventory().checkItem(3000065, 1) || pc.getInventory().checkItem(3000064, 1))) {// 재료아이템
						if (pc.getInventory().consumeItem(3000065, 1) && pc.getInventory().consumeItem(3000064, 1)) {// 아이템 회수
							;
						}
						if (random.nextInt(100) < 20) { // 10%의 확률로 성공
							인첸트지급(pc, 900013, 1, 0);
							pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
							Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 성공하여 \\aG'고대암석망토'\\aA를 획득!"));
						} else { // 나머지 확률 실패
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 실패 하였습니다."));
						}
					} else {
						pc.sendPackets(new S_SystemMessage("재료가 부족합니다"));
					}
				} else if (itemtype == 1766) {//고대암석장갑
					Random random = new Random();
					if ((pc.getInventory().checkItem(3000065, 1) || pc.getInventory().checkItem(3000064, 1))) {// 재료아이템
						if (pc.getInventory().consumeItem(3000065, 1) && pc.getInventory().consumeItem(3000064, 1)) {// 아이템 회수
							;
						}
						if (random.nextInt(100) < 20) { // 10%의 확률로 성공
							인첸트지급(pc, 900014, 1, 0);
							pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
							Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 성공하여 \\aG'고대암석장갑'\\aA을 획득!"));
						} else { // 나머지 확률 실패
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 실패 하였습니다."));
						}
					} else {
						pc.sendPackets(new S_SystemMessage("재료가 부족합니다"));
					}
				} else if (itemtype == 1767) {//고대마물의각반
					Random random = new Random();
						if ((pc.getInventory().checkItem(3000065, 1) || pc.getInventory().checkItem(3000075, 1))) {// 재료아이템
							if (pc.getInventory().consumeItem(3000065, 1) && pc.getInventory().consumeItem(3000075, 1)) {// 아이템 회수
								;
							}
						if (random.nextInt(100) < 10) { // 10%의 확률로 성공
							인첸트지급(pc, 900015, 1, 0);
							pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
							Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
							pc.sendPackets(new S_SystemMessage("\\aA알림: 당신은 \\aG'고대마물의각반'\\aA을 획득!"));
						} else { // 나머지 확률 실패
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 실패 하였습니다."));
						}
					} else {
						pc.sendPackets(new S_SystemMessage("재료가 부족합니다"));
					}
				} else if (itemtype == 1768) {//고대마물의부츠
					Random random = new Random();
					if ((pc.getInventory().checkItem(3000065, 1) || pc.getInventory().checkItem(3000075, 1))) {// 재료아이템
						if (pc.getInventory().consumeItem(3000065, 1) && pc.getInventory().consumeItem(3000075, 1)) {// 아이템 회수
							;
						}
						if (random.nextInt(100) < 10) { // 10%의 확률로 성공
							인첸트지급(pc, 900018, 1, 0);
							pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
							Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
							pc.sendPackets(new S_SystemMessage("\\aA알림: 당신은 \\aG'고대마물의부츠'\\aA를 획득!"));
						} else { // 나머지 확률 실패
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 실패 하였습니다."));
						}
					} else {
						pc.sendPackets(new S_SystemMessage("재료가 부족합니다"));
					}
				} else if (itemtype == 1769) {//고대마물의망토
					Random random = new Random();
					if ((pc.getInventory().checkItem(3000065, 1) || pc.getInventory().checkItem(3000075, 1))) {// 재료아이템
						if (pc.getInventory().consumeItem(3000065, 1) && pc.getInventory().consumeItem(3000075, 1)) {// 아이템 회수
							;
						}
						if (random.nextInt(100) < 10) { // 10%의 확률로 성공
							인첸트지급(pc, 900017, 1, 0);
							pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
							Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
							pc.sendPackets(new S_SystemMessage("\\aA알림: 당신은 \\aG'고대마물의망토'\\aA를 획득!"));
						} else { // 나머지 확률 실패
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 실패 하였습니다."));
						}
					} else {
						pc.sendPackets(new S_SystemMessage("재료가 부족합니다"));
					}
				} else if (itemtype == 1770) {//고대마물의장갑
					Random random = new Random();
					if ((pc.getInventory().checkItem(3000065, 1) || pc.getInventory().checkItem(3000075, 1))) {// 재료아이템
						if (pc.getInventory().consumeItem(3000065, 1) && pc.getInventory().consumeItem(3000075, 1)) {// 아이템 회수
							;
						}
						if (random.nextInt(100) < 10) { // 10%의 확률로 성공
							인첸트지급(pc, 900016, 1, 0);
							pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
							Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
							pc.sendPackets(new S_SystemMessage("\\aA알림: 당신은 \\aG'고대마물의장갑'\\aA을 획득!"));
						} else { // 나머지 확률 실패
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 실패 하였습니다."));
						}
					} else {
						pc.sendPackets(new S_SystemMessage("재료가 부족합니다"));
					}
					
					/** 럭키 제작아이템 확률 **/
				} else if (itemtype == 1771) {//고대 기술서
					Random random = new Random();
					if ((pc.getInventory().checkItem(3000065, 1))) {// 재료아이템
						if (pc.getInventory().consumeItem(3000065, 1)) {// 아이템 회수
							;
						}
						if (random.nextInt(100) < 20) { // 20%의 확률로 성공
							인첸트지급(pc, 3000110, 1, 0);
							pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
							Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 성공하여 \\aG'고대 기술서'\\aA를 획득!"));
						} else { // 나머지 확률 실패
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 실패 하였습니다."));
						}
					} else {
						pc.sendPackets(new S_SystemMessage("재료가 부족합니다"));
					}
				} else if (itemtype == 1772) {//고대 서판
					Random random = new Random();
					if ((pc.getInventory().checkItem(3000065, 1))) {// 재료아이템
						if (pc.getInventory().consumeItem(3000065, 1)) {// 아이템 회수
							;
						}
						if (random.nextInt(100) < 20) { // 20%의 확률로 성공
							인첸트지급(pc, 3000113, 1, 0);
							pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
							Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 성공하여 \\aG'고대 서판'\\aA을 획득!"));
						} else { // 나머지 확률 실패
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 실패 하였습니다."));
						}
					} else {
						pc.sendPackets(new S_SystemMessage("재료가 부족합니다"));
					}
				} else if (itemtype == 1773) {//고대 기억의수정
					Random random = new Random();
					if ((pc.getInventory().checkItem(3000065, 1))) {// 재료아이템
						if (pc.getInventory().consumeItem(3000065, 1)) {// 아이템 회수
							;
						}
						if (random.nextInt(100) < 20) { // 20%의 확률로 성공
							인첸트지급(pc, 3000112, 1, 0);
							pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
							Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 성공하여 \\aG'고대기억의수정'\\aA을 획득!"));
						} else { // 나머지 확률 실패
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 실패 하였습니다."));
						}
					} else {
						pc.sendPackets(new S_SystemMessage("재료가 부족합니다"));
					}
				} else if (itemtype == 1774) {//고대 정령의수정
					Random random = new Random();
					if ((pc.getInventory().checkItem(3000065, 1))) {// 재료아이템
						if (pc.getInventory().consumeItem(3000065, 1)) {// 아이템 회수
							;
						}
						if (random.nextInt(100) < 20) { // 20%의 확률로 성공
							인첸트지급(pc, 3000116, 1, 0);
							pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
							Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 성공하여 \\aG'고대정령의수정'\\aA을 획득!"));
						} else { // 나머지 확률 실패
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 실패 하였습니다."));
						}
					} else {
						pc.sendPackets(new S_SystemMessage("재료가 부족합니다"));
					}
				} else if (itemtype == 1775) {//고대 흑정령의수정
					Random random = new Random();
					if ((pc.getInventory().checkItem(3000065, 1))) {// 재료아이템
						if (pc.getInventory().consumeItem(3000065, 1)) {// 아이템 회수
							;
						}
						if (random.nextInt(100) < 20) { // 20%의 확률로 성공
							인첸트지급(pc, 3000117, 1, 0);
							pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
							Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 성공하여 \\aG'고대흑정령의수정'\\aA을 획득!"));
						} else { // 나머지 확률 실패
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 실패 하였습니다."));
						}
					} else {
						pc.sendPackets(new S_SystemMessage("재료가 부족합니다"));
					}
				} else if (itemtype == 1776) {//고대 전사의인장
					Random random = new Random();
					if ((pc.getInventory().checkItem(3000065, 1))) {// 재료아이템
						if (pc.getInventory().consumeItem(3000065, 1)) {// 아이템 회수
							;
						}
						if (random.nextInt(100) < 20) { // 20%의 확률로 성공
							인첸트지급(pc, 3000115, 1, 0);
							pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
							Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 성공하여 \\aG'고대전사의인장'\\aA을 획득!"));
						} else { // 나머지 확률 실패
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 실패 하였습니다."));
						}
					} else {
						pc.sendPackets(new S_SystemMessage("재료가 부족합니다"));
					}
				} else if (itemtype == 1777) {//고대 마법서
					Random random = new Random();
					if ((pc.getInventory().checkItem(3000065, 1))) {// 재료아이템
						if (pc.getInventory().consumeItem(3000065, 1)) {// 아이템 회수
							;
						}
						if (random.nextInt(100) < 20) { // 20%의 확률로 성공
							인첸트지급(pc, 3000111, 1, 0);
							pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
							Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 성공하여 \\aG'고대마법서'\\aA를 획득!"));
						} else { // 나머지 확률 실패
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 실패 하였습니다."));
						}
					} else {
						pc.sendPackets(new S_SystemMessage("재료가 부족합니다"));
					}
				} else if (itemtype == 1778) {//고대 오라
					Random random = new Random();
					if ((pc.getInventory().checkItem(3000065, 1))) {// 재료아이템
						if (pc.getInventory().consumeItem(3000065, 1)) {// 아이템 회수
							;
						}
						if (random.nextInt(100) < 20) { // 20%의 확률로 성공
							인첸트지급(pc, 3000114, 1, 0);
							pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
							Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 성공하여 \\aG'고대오라'\\aA를 획득!"));
						} else { // 나머지 확률 실패
							pc.sendPackets(new S_SystemMessage("\\aA알림: 제작에 실패 하였습니다."));
						}
					} else {
						pc.sendPackets(new S_SystemMessage("재료가 부족합니다"));
					}
					
				} else if (itemtype == 159) {
					if ((pc.getInventory().checkEnchantItem(500, 8, 1) // [+7]환영의 체인소드
							|| pc.getInventory().checkEnchantItem(501, 8, 1))
							&& pc.getInventory().checkItem(40308, 5000000)) {
						if (pc.getInventory().consumeEnchantItem(500, 8, 1) || pc.getInventory().consumeEnchantItem(501, 8, 1)) {
							;
						}
						pc.getInventory().consumeItem(40308, 5000000);
						인첸트지급(pc, 202001, 1, 7);
					}
				} else if (itemtype == 160) {
					if ((pc.getInventory().checkEnchantItem(500, 9, 1) // [+8]환영의 체인소드
							|| pc.getInventory().checkEnchantItem(501, 9, 1))
							&& pc.getInventory().checkItem(40308, 10000000)) {
						if (pc.getInventory().consumeEnchantItem(500, 9, 1) || pc.getInventory().consumeEnchantItem(501, 9, 1)) {
							;
						}
						pc.getInventory().consumeItem(40308, 10000000);
						인첸트지급(pc, 202001, 1, 8);
					}
				} else if (itemtype == 161) {
					if ((pc.getInventory().checkEnchantItem(503, 8, 1) // [+7]공명의
																		// 키링크
							|| pc.getInventory().checkEnchantItem(504, 8, 1))
							&& pc.getInventory().checkItem(40308, 5000000)) {
						if (pc.getInventory().consumeEnchantItem(503, 8, 1) || pc.getInventory().consumeEnchantItem(504, 8, 1)) {
							;
						}
						pc.getInventory().consumeItem(40308, 5000000);
						인첸트지급(pc, 1135, 1, 7);
					}
				} else if (itemtype == 162) {
					if ((pc.getInventory().checkEnchantItem(503, 9, 1) // [+8]공명의
																		// 키링크
							|| pc.getInventory().checkEnchantItem(504, 9, 1))
							&& pc.getInventory().checkItem(40308, 10000000)) {
						if (pc.getInventory().consumeEnchantItem(503, 9, 1) || pc.getInventory().consumeEnchantItem(504, 9, 1)) {
							;
						}
						pc.getInventory().consumeItem(40308, 10000000);
						인첸트지급(pc, 1135, 1, 8);
					}
				} else if (itemtype == 287) {
					if ((pc.getInventory().checkEnchantItem(81, 8, 1) // [+7]파괴의 크로우
							|| pc.getInventory().checkEnchantItem(162, 8, 1) || pc.getInventory().checkEnchantItem(177, 8, 1) || pc.getInventory().checkEnchantItem(194, 8, 1) || pc
							.getInventory().checkEnchantItem(13, 8, 1))

					&& pc.getInventory().checkItem(40308, 5000000)) {
						if (pc.getInventory().consumeEnchantItem(81, 8, 1) || pc.getInventory().consumeEnchantItem(162, 8, 1) || pc.getInventory().consumeEnchantItem(177, 8, 1)
								|| pc.getInventory().consumeEnchantItem(194, 8, 1) || pc.getInventory().consumeEnchantItem(13, 8, 1)) {
							;
						}
						pc.getInventory().consumeItem(40308, 5000000);
						인첸트지급(pc, 1124, 1, 7);
					}
				} else if (itemtype == 288) {
					if ((pc.getInventory().checkEnchantItem(81, 9, 1) // [+8]파괴의 크로우
							|| pc.getInventory().consumeEnchantItem(162, 8, 1) || pc.getInventory().checkEnchantItem(177, 9, 1) || pc.getInventory().checkEnchantItem(194, 9, 1) || pc
							.getInventory().checkEnchantItem(13, 9, 1)) && pc.getInventory().checkItem(40308, 10000000)) {
						if (pc.getInventory().consumeEnchantItem(81, 9, 1) || pc.getInventory().consumeEnchantItem(162, 9, 1) || pc.getInventory().consumeEnchantItem(177, 9, 1)
								|| pc.getInventory().consumeEnchantItem(194, 9, 1) || pc.getInventory().consumeEnchantItem(13, 9, 1)) {
							;
						}
						pc.getInventory().consumeItem(40308, 10000000);
						인첸트지급(pc, 1124, 1, 8);
					}
				} else if (itemtype == 289) {
					if ((pc.getInventory().checkEnchantItem(81, 8, 1) // [+7]파괴의 이도류
							|| pc.getInventory().checkEnchantItem(162, 8, 1) || pc.getInventory().checkEnchantItem(177, 8, 1) || pc.getInventory().checkEnchantItem(194, 8, 1) || pc
							.getInventory().checkEnchantItem(13, 8, 1)) && pc.getInventory().checkItem(40308, 5000000)) {
						if (pc.getInventory().consumeEnchantItem(81, 8, 1) || pc.getInventory().consumeEnchantItem(162, 8, 1) || pc.getInventory().consumeEnchantItem(177, 8, 1)
								|| pc.getInventory().consumeEnchantItem(194, 8, 1) || pc.getInventory().consumeEnchantItem(13, 8, 1)) {
							;
						}
						pc.getInventory().consumeItem(40308, 5000000);
						인첸트지급(pc, 1125, 1, 7);
					}
				} else if (itemtype == 290) {
					if ((pc.getInventory().checkEnchantItem(81, 9, 1) // [+8]파괴의 이도류
							|| pc.getInventory().checkEnchantItem(162, 9, 1) || pc.getInventory().checkEnchantItem(177, 9, 1) || pc.getInventory().checkEnchantItem(194, 9, 1) || pc
							.getInventory().checkEnchantItem(13, 9, 1)) && pc.getInventory().checkItem(40308, 10000000)) {
						if (pc.getInventory().consumeEnchantItem(81, 9, 1) || pc.getInventory().consumeEnchantItem(162, 9, 1) || pc.getInventory().consumeEnchantItem(177, 9, 1)
								|| pc.getInventory().consumeEnchantItem(194, 9, 1) || pc.getInventory().consumeEnchantItem(13, 9, 1)) {
							;
						}
						pc.getInventory().consumeItem(40308, 10000000);
						인첸트지급(pc, 1125, 1, 8);
					}
				} else if (itemtype == 577) { // 힘의룬주머니
					if (pc.getInventory().checkItem(60033, 1) && pc.getInventory().checkItem(60034, 1) && pc.getInventory().checkItem(40087, 70)) {
						pc.getInventory().consumeItem(60033, 1);
						pc.getInventory().consumeItem(60034, 1);
						pc.getInventory().consumeItem(40087, 70);
					}
					인첸트지급(pc, 60041, 1, 0);
				} else if (itemtype == 578) { // 민첩의룬주머니
					if (pc.getInventory().checkItem(60033, 1) && pc.getInventory().checkItem(60034, 1) && pc.getInventory().checkItem(40087, 70)) {
						pc.getInventory().consumeItem(60033, 1);
						pc.getInventory().consumeItem(60034, 1);
						pc.getInventory().consumeItem(40087, 70);
					}
					인첸트지급(pc, 60042, 1, 0);
				} else if (itemtype == 579) { // 체력의룬주머니
					if (pc.getInventory().checkItem(60033, 1) && pc.getInventory().checkItem(60034, 1) && pc.getInventory().checkItem(40087, 70)) {
						pc.getInventory().consumeItem(60033, 1);
						pc.getInventory().consumeItem(60034, 1);
						pc.getInventory().consumeItem(40087, 70);
					}
					인첸트지급(pc, 60043, 1, 0);
				} else if (itemtype == 580) { // 지식의의룬주머니
					if (pc.getInventory().checkItem(60033, 1) && pc.getInventory().checkItem(60034, 1) && pc.getInventory().checkItem(40087, 70)) {
						pc.getInventory().consumeItem(60033, 1);
						pc.getInventory().consumeItem(60034, 1);
						pc.getInventory().consumeItem(40087, 70);
					}
					인첸트지급(pc, 60044, 1, 0);
				} else if (itemtype == 581) { // 지혜의의룬주머니
					if (pc.getInventory().checkItem(60033, 1) && pc.getInventory().checkItem(60034, 1) && pc.getInventory().checkItem(40087, 70)) {
						pc.getInventory().consumeItem(60033, 1);
						pc.getInventory().consumeItem(60034, 1);
						pc.getInventory().consumeItem(40087, 70);
					}
					인첸트지급(pc, 60045, 1, 0);
					
					
				}else if (itemtype == 3385){  //완력유물
					if (pc.getInventory().checkItem(30072, 4) && pc.getInventory().checkItem(40308, 7000)){	
						pc.getInventory().consumeItem(30072, 4);
						pc.getInventory().consumeItem(40308, 7000);
					}
					인첸트지급(pc, 8023, 1, 0);
				}else if (itemtype == 3386){  //민첩유물
					if (pc.getInventory().checkItem(30072, 4) && pc.getInventory().checkItem(40308, 7000)){	
						pc.getInventory().consumeItem(30072, 4);
						pc.getInventory().consumeItem(40308, 7000);
					} 
					인첸트지급(pc, 8024, 1, 0);
				}else if (itemtype == 3387){  //지식유물
					if (pc.getInventory().checkItem(30072, 4) && pc.getInventory().checkItem(40308, 7000)){	
						pc.getInventory().consumeItem(30072, 4);
						pc.getInventory().consumeItem(40308, 7000);
					}
					인첸트지급(pc, 8025, 1, 0);
					
					
				}else if (itemtype == 3387){  //드래곤고급상자
					if (pc.getInventory().checkEnchantItem(1000006, 0, 4)){
						인첸트지급(pc, 1000008, 1, 0);
						pc.getInventory().consumeEnchantItem(1000006, 0, 4);
					} 	
					
				}else if (itemtype == 107){                                               // 탄생마안
					int chance = _Random.nextInt(100) + 1;
					if (pc.getInventory().checkItem(410032, 1)
				     || pc.getInventory().checkItem(410034, 1)
					 && pc.getInventory().checkItem(40308, 200000)){
						if (chance <= 30){	
							인첸트지급(pc, 410036, 1, 0);	
						} else {
							pc.sendPackets(new S_SystemMessage("마안 조합에 실패하였습니다."));
						}
						pc.getInventory().consumeItem(410032, 1);
						pc.getInventory().consumeItem(410034, 1);
						pc.getInventory().consumeItem(40308, 200000);
						
					}
					
				}else if (itemtype == 108){                                               // 형상마안
					int chance = _Random.nextInt(100) + 1;
					if (pc.getInventory().checkItem(410033, 1)
				     || pc.getInventory().checkItem(410036, 1)
					 && pc.getInventory().checkItem(40308, 200000)){
						
						if (chance <= 30){	
							인첸트지급(pc, 410037, 1, 0);	
						} else {
							pc.sendPackets(new S_SystemMessage("마안 조합에 실패하였습니다."));
						}
						pc.getInventory().consumeItem(410033, 1);
						pc.getInventory().consumeItem(410036, 1);
						pc.getInventory().consumeItem(40308, 200000);
					}
					
				}else if (itemtype == 109){                                               // 생명의마안
					int chance = _Random.nextInt(100) + 1;
					if (pc.getInventory().checkItem(410037, 1)
				     || pc.getInventory().checkItem(410035, 1)
					 && pc.getInventory().checkItem(40308, 200000)){

						if (chance <= 30){	
							인첸트지급(pc, 410038, 1, 0);	
						} else {
							pc.sendPackets(new S_SystemMessage("마안 조합에 실패하였습니다."));
						}
						pc.getInventory().consumeItem(410037, 1);
						pc.getInventory().consumeItem(410035, 1);
						pc.getInventory().consumeItem(40308, 200000);						
						
					}
					
					/** 진명황집행검**/
				} else if (itemtype == 46) {
					if (pc.getInventory().checkItem(49, 1) // 무관장검
							&& pc.getInventory().checkItem(40965, 1) // 미법서
							&& pc.getInventory().checkItem(40445, 10) // 미스릴판금
							&& pc.getInventory().checkItem(40677, 50) // 어둠의주괴
							&& pc.getInventory().checkItem(40525, 10) // 그랑카인눈물
							&& pc.getInventory().checkItem(40969, 300) // 다영결
							&& pc.getInventory().checkItem(40967, 100) // 성지의 유물
							&& pc.getInventory().checkItem(40964, 50)) { // 흑마법가루
						pc.getInventory().consumeItem(40965, 1);
						pc.getInventory().consumeItem(40445, 10);
						pc.getInventory().consumeItem(40677, 50);
						pc.getInventory().consumeItem(40525, 10);
						pc.getInventory().consumeItem(40969, 300);
						pc.getInventory().consumeItem(40967, 100);
						pc.getInventory().consumeItem(40964, 50);
					}
					인첸트지급(pc, 61, 1, 0);
				} else if (itemtype == 198) {
					if (pc.getInventory().checkItem(111, 1) && pc.getInventory().checkItem(40318, 100) && pc.getInventory().checkItem(40090, 2)
							&& pc.getInventory().checkItem(40091, 2) && pc.getInventory().checkItem(40092, 2) && pc.getInventory().checkItem(40093, 2)
							&& pc.getInventory().checkItem(40094, 2)) {
						pc.getInventory().consumeItem(111, 1);
						pc.getInventory().consumeItem(40318, 100);
						pc.getInventory().consumeItem(40090, 2);
						pc.getInventory().consumeItem(40091, 2);
						pc.getInventory().consumeItem(40092, 2);
						pc.getInventory().consumeItem(40093, 2);
						pc.getInventory().consumeItem(40094, 2);
					}
					인첸트지급(pc, 121, 1, 0);
				} else if (itemtype == 49) {
					if (pc.getInventory().checkItem(111, 1) && pc.getInventory().checkItem(40318, 100) && pc.getInventory().checkItem(40090, 2)
							&& pc.getInventory().checkItem(40091, 2) && pc.getInventory().checkItem(40092, 2) && pc.getInventory().checkItem(40093, 2)
							&& pc.getInventory().checkItem(40094, 2)) {
						pc.getInventory().consumeItem(111, 1);
						pc.getInventory().consumeItem(40318, 100);
						pc.getInventory().consumeItem(40090, 2);
						pc.getInventory().consumeItem(40091, 2);
						pc.getInventory().consumeItem(40092, 2);
						pc.getInventory().consumeItem(40093, 2);
						pc.getInventory().consumeItem(40094, 2);
					}
					인첸트지급(pc, 134, 1, 0);


					// 고대의무기상자
				}else if (itemtype == 2652){                                               
					int chance = _Random.nextInt(100) + 1;
					if (pc.getInventory().checkItem(40087, 5) && pc.getInventory().checkItem(40308, 100000)){
						if (chance <= 20){	
							인첸트지급(pc, 31093, 1, 0);	
						} else {
							pc.sendPackets(new S_SystemMessage("고대의 무기 상자 제작에 실패하였습니다."));
						}
						pc.getInventory().consumeItem(40087, 5);
						pc.getInventory().consumeItem(40308, 100000);
					}
					// 고대의방어구상자
				}else if (itemtype == 2653){                                               
					int chance = _Random.nextInt(100) + 1;
					if (pc.getInventory().checkItem(40074, 5) && pc.getInventory().checkItem(40308, 100000)){
						if (chance <= 20){	
							인첸트지급(pc, 31094, 1, 0);	
						} else {
							pc.sendPackets(new S_SystemMessage("고대의 무기 상자 제작에 실패하였습니다."));
						}
						pc.getInventory().consumeItem(40074, 5);
						pc.getInventory().consumeItem(40308, 100000);
					}
					
				/** 환생의보석**/
				}else if (itemtype == 2739){
					if (pc.getInventory().checkItem(40052, 1) && pc.getInventory().checkItem(40055, 1)
							&&pc.getInventory().checkItem(40053, 1) && pc.getInventory().checkItem(40054, 1)	
							&&pc.getInventory().checkItem(410061, 1)	|| pc.getInventory().checkItem(500020, 1)
							){
						인첸트지급(pc, 31096, 1, 0);	
						pc.getInventory().consumeItem(40052, 1);
						pc.getInventory().consumeItem(40055, 1);
						pc.getInventory().consumeItem(40053, 1);
						pc.getInventory().consumeItem(40054, 1);
						pc.getInventory().consumeItem(410061, 1);
						pc.getInventory().consumeItem(500020, 1);
					}
					
					/** 신성한요정족판금갑옷**/
				}else if (itemtype == 2864){
					if (pc.getInventory().checkItem(40396, 5) &&pc.getInventory().checkItem(31096, 50)	
					 || pc.getInventory().checkItem(820018, 5)
							){
						인첸트지급(pc, 222351, 1, 0);	
						pc.getInventory().consumeItem(40396, 5);
						pc.getInventory().consumeItem(31096, 50);
						pc.getInventory().consumeItem(820018, 5);
					}
					/** 신성한 요정족 방패**/
				}else if (itemtype == 2865){
					if (pc.getInventory().checkItem(40396, 2) &&pc.getInventory().checkItem(31096, 25)	
					 || pc.getInventory().checkItem(820018, 2)
							){
						인첸트지급(pc, 222355, 1, 0);	
						pc.getInventory().consumeItem(40396, 2);
						pc.getInventory().consumeItem(31096, 25);
						pc.getInventory().consumeItem(820018, 2);
					}
					/** 6수호성파워글로브	**/
				}else if (itemtype == 2784){
					if (pc.getInventory().checkEnchantItem(20187, 7, 1) && pc.getInventory().checkItem(31096, 75)	
					 || pc.getInventory().checkEnchantItem(22212, 6, 1)
							){
						인첸트지급(pc, 222345, 1, 6);
						pc.getInventory().consumeEnchantItem(20187, 7, 1);
						pc.getInventory().consumeEnchantItem(22212, 6, 1);
						pc.getInventory().consumeItem(31096, 75);
					}
					/** 7수호성파워글로브	**/
				}else if (itemtype == 2785){
					if (pc.getInventory().checkEnchantItem(20187, 8, 1) && pc.getInventory().checkItem(31096, 100)	
					 || pc.getInventory().checkEnchantItem(22212, 7, 1)
							){
						인첸트지급(pc, 222345, 1, 7);
						pc.getInventory().consumeEnchantItem(20187, 8, 1);
						pc.getInventory().consumeEnchantItem(22212, 7, 1);
						pc.getInventory().consumeItem(31096, 100);
					}
					/** 8수호성파워글로브	**/
				}else if (itemtype == 2786){
					if (pc.getInventory().checkEnchantItem(20187, 9, 1) && pc.getInventory().checkItem(31096, 125)	
					 || pc.getInventory().checkEnchantItem(22212, 8, 1)
							){
						인첸트지급(pc, 222345, 1, 8);
						pc.getInventory().consumeEnchantItem(20187, 9, 1);
						pc.getInventory().consumeEnchantItem(22212, 8, 1);
						pc.getInventory().consumeItem(31096, 125);
					}
					/** 9수호성파워글로브	**/
				}else if (itemtype == 2787){
					if (pc.getInventory().checkEnchantItem(20187, 10, 1) && pc.getInventory().checkItem(31096, 150)	
					 || pc.getInventory().checkEnchantItem(22212, 9, 1)
							){
						인첸트지급(pc, 222345, 1, 9);
						pc.getInventory().consumeEnchantItem(20187, 10, 1);
						pc.getInventory().consumeEnchantItem(22212, 9, 1);
						pc.getInventory().consumeItem(31096, 150);
					}
					/** 빛나는신체의벨트**/
				}else if (itemtype == 232){
					if (pc.getInventory().checkItem(40308, 100000) && pc.getInventory().checkItem(40458, 50)	
					 && pc.getInventory().checkItem(40049, 30) && pc.getInventory().checkItem(20306, 1)			
					 || pc.getInventory().checkItem(20312, 2)
							){
						인첸트지급(pc, 20309, 1, 0);	
						pc.getInventory().consumeItem(40308, 100000);
						pc.getInventory().consumeItem(40458, 50);
						pc.getInventory().consumeItem(40049, 30);
						pc.getInventory().consumeItem(20306, 1);
						pc.getInventory().consumeItem(20312, 1);
					}
					/** 빛나는정신의벨트**/
				}else if (itemtype == 233){
					if (pc.getInventory().checkItem(40308, 100000) && pc.getInventory().checkItem(40458, 50)	
					 && pc.getInventory().checkItem(40050, 30) && pc.getInventory().checkItem(20308, 1)			
					 || pc.getInventory().checkItem(20319, 2)
							){
						인첸트지급(pc, 20309, 1, 0);	
						pc.getInventory().consumeItem(40308, 100000);
						pc.getInventory().consumeItem(40458, 50);
						pc.getInventory().consumeItem(40050, 30);
						pc.getInventory().consumeItem(20308, 1);
						pc.getInventory().consumeItem(20319, 1);
					}
					/** 빛나는영혼의벨트**/
				}else if (itemtype == 234){
					if (pc.getInventory().checkItem(40308, 100000) && pc.getInventory().checkItem(40458, 50)	
					 && pc.getInventory().checkItem(40050, 20) && pc.getInventory().checkItem(40049, 20) && pc.getInventory().checkItem(20307, 1)			
					 || pc.getInventory().checkItem(20316, 1)
							){
						인첸트지급(pc, 20309, 1, 0);	
						pc.getInventory().consumeItem(40308, 100000);
						pc.getInventory().consumeItem(40458, 50);
						pc.getInventory().consumeItem(40050, 20);
						pc.getInventory().consumeItem(40049, 20);
						pc.getInventory().consumeItem(20307, 1);
						pc.getInventory().consumeItem(20316, 1);
					}
					/** 9진싸울	**/
				}else if (itemtype == 2876){
					if (pc.getInventory().checkEnchantItem(57, 11, 1) && pc.getInventory().checkItem(67, 1)	
					 || pc.getInventory().checkItem(68, 1)	
							){
						인첸트지급(pc, 203025, 1, 9);
						pc.getInventory().consumeEnchantItem(57, 11, 1);
						pc.getInventory().consumeItem(67, 1);
						pc.getInventory().consumeItem(68, 1);
					}
					/** 7진싸울	**/
				}else if (itemtype == 2877){
					if (pc.getInventory().checkEnchantItem(57, 10, 1) && pc.getInventory().checkItem(67, 1)	
					 || pc.getInventory().checkItem(68, 1)	
							){
						인첸트지급(pc, 203025, 1, 7);
						pc.getInventory().consumeEnchantItem(57, 10, 1);
						pc.getInventory().consumeItem(67, 1);
						pc.getInventory().consumeItem(68, 1);
					}
				
					//신성한 완력의모걸이	
				}else if (itemtype == 2775){
					if (pc.getInventory().checkItem(40393, 15) && pc.getInventory().checkItem(31096, 10)	
					 || pc.getInventory().checkItem(31095, 30)	
							){
						인첸트지급(pc, 222346, 1, 0);
						pc.getInventory().consumeItem(40393, 15);
						pc.getInventory().consumeItem(31096, 10);
						pc.getInventory().consumeItem(31095, 30);
					}
					//신성한 민첩의목걸	
				}else if (itemtype == 2776){
					if (pc.getInventory().checkItem(40394, 15) && pc.getInventory().checkItem(31096, 10)	
					 || pc.getInventory().checkItem(31095, 30)	
							){
						인첸트지급(pc, 222347, 1, 0);
						pc.getInventory().consumeItem(40394, 15);
						pc.getInventory().consumeItem(31096, 10);
						pc.getInventory().consumeItem(31095, 30);
					}
					//신성한 지식의목걸	
				}else if (itemtype == 2777){
					if (pc.getInventory().checkItem(40395, 15) && pc.getInventory().checkItem(31096, 10)	
					 || pc.getInventory().checkItem(31095, 30)	
							){
						인첸트지급(pc, 222348, 1, 0);
						pc.getInventory().consumeItem(40395, 15);
						pc.getInventory().consumeItem(31096, 10);
						pc.getInventory().consumeItem(31095, 30);
					}
					//신성한 영생의목걸	
				}else if (itemtype == 2778){
					if (pc.getInventory().checkItem(40396, 25) && pc.getInventory().checkItem(31096, 50)	
					 || pc.getInventory().checkItem(31095, 50)	
							){
						인첸트지급(pc, 222349, 1, 0);
						pc.getInventory().consumeItem(40396, 25);
						pc.getInventory().consumeItem(31096, 50);
						pc.getInventory().consumeItem(31095, 50);
					}
					
					/** 10)신성한목걸이조각	**/
				}else if (itemtype == 2779){
					if (pc.getInventory().checkEnchantItem(22194, 5, 1) || pc.getInventory().checkEnchantItem(900008, 5, 1)
					 || pc.getInventory().checkEnchantItem(22195, 5, 1) || pc.getInventory().checkEnchantItem(20264, 5, 1)
					 || pc.getInventory().checkEnchantItem(20422, 5, 1) || pc.getInventory().checkEnchantItem(20256, 5, 1)
					 || pc.getInventory().checkEnchantItem(20257, 5, 1) || pc.getInventory().checkEnchantItem(20266, 5, 1)
					 || pc.getInventory().checkEnchantItem(20411, 5, 1) || pc.getInventory().checkEnchantItem(22361, 5, 1)
					 || pc.getInventory().checkEnchantItem(20412, 5, 1) || pc.getInventory().checkEnchantItem(22362, 5, 1)
							){
						인첸트지급(pc, 31095, 10, 0);
						pc.getInventory().consumeEnchantItem(22194, 5, 1);
						pc.getInventory().consumeEnchantItem(22195, 5, 1);
						pc.getInventory().consumeEnchantItem(20422, 5, 1);
						pc.getInventory().consumeEnchantItem(20257, 5, 1);
						pc.getInventory().consumeEnchantItem(20411, 5, 1);
						pc.getInventory().consumeEnchantItem(20412, 5, 1);
						pc.getInventory().consumeEnchantItem(900008, 5, 1);
						pc.getInventory().consumeEnchantItem(20264, 5, 1);
						pc.getInventory().consumeEnchantItem(20256, 5, 1);
						pc.getInventory().consumeEnchantItem(20266, 5, 1);
						pc.getInventory().consumeEnchantItem(22361, 5, 1);
						pc.getInventory().consumeEnchantItem(22362, 5, 1);
					}
					/** 20)신성한목걸이조각		**/
				}else if (itemtype == 2780){
					if (pc.getInventory().checkEnchantItem(22194, 6, 1) || pc.getInventory().checkEnchantItem(900008, 6, 1)
					 || pc.getInventory().checkEnchantItem(22195, 6, 1) || pc.getInventory().checkEnchantItem(20264, 6, 1)
					 || pc.getInventory().checkEnchantItem(20422, 6, 1) || pc.getInventory().checkEnchantItem(20256, 6, 1)
					 || pc.getInventory().checkEnchantItem(20257, 6, 1) || pc.getInventory().checkEnchantItem(20266, 6, 1)
					 || pc.getInventory().checkEnchantItem(20411, 6, 1) || pc.getInventory().checkEnchantItem(22361, 6, 1)
					 || pc.getInventory().checkEnchantItem(20412, 6, 1) || pc.getInventory().checkEnchantItem(22362, 6, 1)
							){
						인첸트지급(pc, 31095, 20, 0);
						pc.getInventory().consumeEnchantItem(22194, 6, 1);
						pc.getInventory().consumeEnchantItem(22195, 6, 1);
						pc.getInventory().consumeEnchantItem(20422, 6, 1);
						pc.getInventory().consumeEnchantItem(20257, 6, 1);
						pc.getInventory().consumeEnchantItem(20411, 6, 1);
						pc.getInventory().consumeEnchantItem(20412, 6, 1);
						pc.getInventory().consumeEnchantItem(900008, 6, 1);
						pc.getInventory().consumeEnchantItem(20264, 6, 1);
						pc.getInventory().consumeEnchantItem(20256, 6, 1);
						pc.getInventory().consumeEnchantItem(20266, 6, 1);
						pc.getInventory().consumeEnchantItem(22361, 6, 1);
						pc.getInventory().consumeEnchantItem(22362, 6, 1);
					}
					/** 40)신성한목걸이조각			**/
				}else if (itemtype == 2781){
					if (pc.getInventory().checkEnchantItem(22194, 7, 1) || pc.getInventory().checkEnchantItem(900008, 7, 1)
					 || pc.getInventory().checkEnchantItem(22195, 7, 1) || pc.getInventory().checkEnchantItem(20264, 7, 1)
					 || pc.getInventory().checkEnchantItem(20422, 7, 1) || pc.getInventory().checkEnchantItem(20256, 7, 1)
					 || pc.getInventory().checkEnchantItem(20257, 7, 1) || pc.getInventory().checkEnchantItem(20266, 7, 1)
					 || pc.getInventory().checkEnchantItem(20411, 7, 1) || pc.getInventory().checkEnchantItem(22361, 7, 1)
					 || pc.getInventory().checkEnchantItem(20412, 7, 1) || pc.getInventory().checkEnchantItem(22362, 7, 1)
							){
						인첸트지급(pc, 31095, 40, 0);
						pc.getInventory().consumeEnchantItem(22194, 7, 1);
						pc.getInventory().consumeEnchantItem(22195, 7, 1);
						pc.getInventory().consumeEnchantItem(20422, 7, 1);
						pc.getInventory().consumeEnchantItem(20257, 7, 1);
						pc.getInventory().consumeEnchantItem(20411, 7, 1);
						pc.getInventory().consumeEnchantItem(20412, 7, 1);
						pc.getInventory().consumeEnchantItem(900008, 7, 1);
						pc.getInventory().consumeEnchantItem(20264, 7, 1);
						pc.getInventory().consumeEnchantItem(20256, 7, 1);
						pc.getInventory().consumeEnchantItem(20266, 7, 1);
						pc.getInventory().consumeEnchantItem(22361, 7, 1);
						pc.getInventory().consumeEnchantItem(22362, 7, 1);
					}
					/** 80)신성한목걸이조각				**/
				}else if (itemtype == 2782){
					if (pc.getInventory().checkEnchantItem(22194, 8, 1) || pc.getInventory().checkEnchantItem(900008, 8, 1)
					 || pc.getInventory().checkEnchantItem(22195, 8, 1) || pc.getInventory().checkEnchantItem(20264, 8, 1)
					 || pc.getInventory().checkEnchantItem(20422, 8, 1) || pc.getInventory().checkEnchantItem(20256, 8, 1)
					 || pc.getInventory().checkEnchantItem(20257, 8, 1) || pc.getInventory().checkEnchantItem(20266, 8, 1)
					 || pc.getInventory().checkEnchantItem(20411, 8, 1) || pc.getInventory().checkEnchantItem(22361, 8, 1)
					 || pc.getInventory().checkEnchantItem(20412, 8, 1) || pc.getInventory().checkEnchantItem(22362, 8, 1)
							){
						인첸트지급(pc, 31095, 80, 0);
						pc.getInventory().consumeEnchantItem(22194, 8, 1);
						pc.getInventory().consumeEnchantItem(22195, 8, 1);
						pc.getInventory().consumeEnchantItem(20422, 8, 1);
						pc.getInventory().consumeEnchantItem(20257, 8, 1);
						pc.getInventory().consumeEnchantItem(20411, 8, 1);
						pc.getInventory().consumeEnchantItem(20412, 8, 1);
						pc.getInventory().consumeEnchantItem(900008, 8, 1);
						pc.getInventory().consumeEnchantItem(20264, 8, 1);
						pc.getInventory().consumeEnchantItem(20256, 8, 1);
						pc.getInventory().consumeEnchantItem(20266, 8, 1);
						pc.getInventory().consumeEnchantItem(22361, 8, 1);
						pc.getInventory().consumeEnchantItem(22362, 8, 1);
					}
					
					//관용의귀걸이		
				}else if (itemtype == 305){
					if (pc.getInventory().checkItem(40651, 60) && pc.getInventory().checkItem(40643, 60)	
					 && pc.getInventory().checkItem(40645, 60) && pc.getInventory().checkItem(40618, 60)
					 && pc.getInventory().checkItem(40676, 60) 
					 || pc.getInventory().checkItem(40961, 9) || pc.getInventory().checkItem(40960, 9)
					 || pc.getInventory().checkItem(40962, 9) || pc.getInventory().checkItem(40959, 9)
					 || pc.getInventory().checkItem(40638, 90) || pc.getInventory().checkItem(40635, 90)
					 || pc.getInventory().checkItem(40667, 90) || pc.getInventory().checkItem(40642, 90)
					 
					 || pc.getInventory().checkItem(21012, 1)
					 
							){
						인첸트지급(pc, 21013, 1, 0);
						pc.getInventory().consumeItem(40651, 60);
						pc.getInventory().consumeItem(40643, 60);
						pc.getInventory().consumeItem(40645, 60);
						pc.getInventory().consumeItem(40618, 60);
						pc.getInventory().consumeItem(40676, 60);
						pc.getInventory().consumeItem(40961, 9);
						pc.getInventory().consumeItem(40960, 9);
						pc.getInventory().consumeItem(40962, 9);
						pc.getInventory().consumeItem(40959, 9);
						pc.getInventory().consumeItem(40638, 90);
						pc.getInventory().consumeItem(40635, 90);
						pc.getInventory().consumeItem(40667, 90);
						pc.getInventory().consumeItem(40642, 90);
						pc.getInventory().consumeItem(21012, 1);
					}
					//불사의귀걸이		
				}else if (itemtype == 309){
					if (pc.getInventory().checkItem(40651, 60) && pc.getInventory().checkItem(40643, 60)	
					 && pc.getInventory().checkItem(40645, 60) && pc.getInventory().checkItem(40618, 60)
					 && pc.getInventory().checkItem(40676, 60) 
					 || pc.getInventory().checkItem(40961, 9) || pc.getInventory().checkItem(40960, 9)
					 || pc.getInventory().checkItem(40962, 9) || pc.getInventory().checkItem(40959, 9)
					 || pc.getInventory().checkItem(40638, 90) || pc.getInventory().checkItem(40635, 90)
					 || pc.getInventory().checkItem(40667, 90) || pc.getInventory().checkItem(40642, 90)
					 
					 || pc.getInventory().checkItem(21010, 1)
					 
							){
						인첸트지급(pc, 21011, 1, 0);
						pc.getInventory().consumeItem(40651, 60);
						pc.getInventory().consumeItem(40643, 60);
						pc.getInventory().consumeItem(40645, 60);
						pc.getInventory().consumeItem(40618, 60);
						pc.getInventory().consumeItem(40676, 60);
						pc.getInventory().consumeItem(40961, 9);
						pc.getInventory().consumeItem(40960, 9);
						pc.getInventory().consumeItem(40962, 9);
						pc.getInventory().consumeItem(40959, 9);
						pc.getInventory().consumeItem(40638, 90);
						pc.getInventory().consumeItem(40635, 90);
						pc.getInventory().consumeItem(40667, 90);
						pc.getInventory().consumeItem(40642, 90);
						pc.getInventory().consumeItem(21010, 1);
					}
					//지배의귀걸이		
				}else if (itemtype == 313){
					if (pc.getInventory().checkItem(40651, 60) && pc.getInventory().checkItem(40643, 60)	
					 && pc.getInventory().checkItem(40645, 60) && pc.getInventory().checkItem(40618, 60)
					 && pc.getInventory().checkItem(40676, 60) 
					 || pc.getInventory().checkItem(40961, 9) || pc.getInventory().checkItem(40960, 9)
					 || pc.getInventory().checkItem(40962, 9) || pc.getInventory().checkItem(40959, 9)
					 || pc.getInventory().checkItem(40638, 90) || pc.getInventory().checkItem(40635, 90)
					 || pc.getInventory().checkItem(40667, 90) || pc.getInventory().checkItem(40642, 90)
					 
					 || pc.getInventory().checkItem(21016, 1)
					 
							){
						인첸트지급(pc, 21017, 1, 0);
						pc.getInventory().consumeItem(40651, 60);
						pc.getInventory().consumeItem(40643, 60);
						pc.getInventory().consumeItem(40645, 60);
						pc.getInventory().consumeItem(40618, 60);
						pc.getInventory().consumeItem(40676, 60);
						pc.getInventory().consumeItem(40961, 9);
						pc.getInventory().consumeItem(40960, 9);
						pc.getInventory().consumeItem(40962, 9);
						pc.getInventory().consumeItem(40959, 9);
						pc.getInventory().consumeItem(40638, 90);
						pc.getInventory().consumeItem(40635, 90);
						pc.getInventory().consumeItem(40667, 90);
						pc.getInventory().consumeItem(40642, 90);
						pc.getInventory().consumeItem(21016, 1);
					}
					
					
					
					
					
				}else if(itemtype==2619){ //드슬
					if(pc.getInventory().checkAttrEnchantItem(1121, 10, 3, 1) && pc.getInventory().checkItem(40346,3) && pc.getInventory().checkItem(40354,3) 
							&& pc.getInventory().checkItem(40362,3) &&pc.getInventory().checkItem(40370,3)&&pc.getInventory().checkItem(40308,10000000)){
						pc.getInventory().consumeAttrItem(1121, 10, 3, 1);
						pc.getInventory().consumeItem(40346,3);
						pc.getInventory().consumeItem(40354,3);
						pc.getInventory().consumeItem(40362,3);
						pc.getInventory().consumeItem(40370,3);
						pc.getInventory().consumeItem(40308,10000000);
						인첸트지급(pc, 66, 1, 0);
					}
				}else if(itemtype == 2626){//드슬 +5
					if(pc.getInventory().checkAttrEnchantItem(66, 4, 3, 1) && pc.getInventory().checkItem(40346,1) && pc.getInventory().checkItem(40354,1)
							&& pc.getInventory().checkItem(40362,1) && pc.getInventory().checkItem(40370,1)){
							pc.getInventory().consumeAttrItem(66, 4, 3, 1);
							pc.getInventory().consumeItem(40346,1);
							pc.getInventory().consumeItem(40354,1);
							pc.getInventory().consumeItem(40362,1);
							pc.getInventory().consumeItem(40370,1);
							인첸트지급(pc,66,1,5);
					}
				}else if(itemtype == 2625){//드슬 +4
					if(pc.getInventory().checkAttrEnchantItem(66, 3, 3, 1) && pc.getInventory().checkItem(40346,1) && pc.getInventory().checkItem(40354,1)
							&& pc.getInventory().checkItem(40362,1) && pc.getInventory().checkItem(40370,1)){
							pc.getInventory().consumeAttrItem(66, 3, 3, 1);
							pc.getInventory().consumeItem(40346,1);
							pc.getInventory().consumeItem(40354,1);
							pc.getInventory().consumeItem(40362,1);
							pc.getInventory().consumeItem(40370,1);
							인첸트지급(pc,66,1,4);
					}
				}else if(itemtype == 2624){//드슬 +3
					if(pc.getInventory().checkAttrEnchantItem(66, 2, 3, 1) && pc.getInventory().checkItem(40346,1) && pc.getInventory().checkItem(40354,1)
							&& pc.getInventory().checkItem(40362,1) && pc.getInventory().checkItem(40370,1)){
							pc.getInventory().consumeAttrItem(66, 2, 3, 1);
							pc.getInventory().consumeItem(40346,1);
							pc.getInventory().consumeItem(40354,1);
							pc.getInventory().consumeItem(40362,1);
							pc.getInventory().consumeItem(40370,1);
							인첸트지급(pc,66,1,3);
					}
				}else if(itemtype == 2623){//드슬 +2
					if(pc.getInventory().checkAttrEnchantItem(66, 1, 3, 1) && pc.getInventory().checkItem(40346,1) && pc.getInventory().checkItem(40354,1)
							&& pc.getInventory().checkItem(40362,1) && pc.getInventory().checkItem(40370,1)){
							pc.getInventory().consumeAttrItem(66, 1, 3, 1);
							pc.getInventory().consumeItem(40346,1);
							pc.getInventory().consumeItem(40354,1);
							pc.getInventory().consumeItem(40362,1);
							pc.getInventory().consumeItem(40370,1);
							인첸트지급(pc,66,1,2);
					}
				}else if(itemtype == 2622){//드슬 +1
					if(pc.getInventory().checkAttrEnchantItem(66, 0, 3, 1) && pc.getInventory().checkItem(40346,1) && pc.getInventory().checkItem(40354,1)
							&& pc.getInventory().checkItem(40362,1) && pc.getInventory().checkItem(40370,1)){
							pc.getInventory().consumeAttrItem(66, 0, 3, 1);
							pc.getInventory().consumeItem(40346,1);
							pc.getInventory().consumeItem(40354,1);
							pc.getInventory().consumeItem(40362,1);
							pc.getInventory().consumeItem(40370,1);
							인첸트지급(pc,66,1,1);
					}
					
				}else if (itemtype == 3541){  //3완력성장
					if (pc.getInventory().checkEnchantItem(900020, 3, 1) && pc.getInventory().checkEnchantItem(222352, 3, 1)	
							){
						인첸트지급(pc, 232355, 1, 3);
						pc.getInventory().consumeEnchantItem(900020, 3, 1);
						pc.getInventory().consumeEnchantItem(222352, 3, 1);
					}
				}else if (itemtype == 3542){  //4완력성장
					if (pc.getInventory().checkEnchantItem(900020, 4, 1) && pc.getInventory().checkEnchantItem(222352, 4, 1)	
							){
						인첸트지급(pc, 232355, 1, 4);
						pc.getInventory().consumeEnchantItem(900020, 4, 1);
						pc.getInventory().consumeEnchantItem(222352, 4, 1);
					}
				}else if (itemtype == 3543){  //5완력성장
					if (pc.getInventory().checkEnchantItem(900020, 5, 1) && pc.getInventory().checkEnchantItem(222352, 5, 1)	
							){
						인첸트지급(pc, 232355, 1, 5);
						pc.getInventory().consumeEnchantItem(900020, 5, 1);
						pc.getInventory().consumeEnchantItem(222352, 5, 1);
					}
				}else if (itemtype == 3544){  //6완력성장
					if (pc.getInventory().checkEnchantItem(900020, 6, 1) && pc.getInventory().checkEnchantItem(222352, 6, 1)	
							){
						인첸트지급(pc, 232355, 1, 6);
						pc.getInventory().consumeEnchantItem(900020, 6, 1);
						pc.getInventory().consumeEnchantItem(222352, 6, 1);
					} 
				}else if (itemtype == 3545){  //7완력성장
					if (pc.getInventory().checkEnchantItem(900020, 7, 1) && pc.getInventory().checkEnchantItem(222352, 7, 1)	
							){
						인첸트지급(pc, 232355, 1, 7);
						pc.getInventory().consumeEnchantItem(900020, 7, 1);
						pc.getInventory().consumeEnchantItem(222352, 7, 1);
					} 
				}else if (itemtype == 3546){  //8완력성장
					if (pc.getInventory().checkEnchantItem(900020, 8, 1) && pc.getInventory().checkEnchantItem(222352, 8, 1)	
							){
						인첸트지급(pc, 232355, 1, 8);
						pc.getInventory().consumeEnchantItem(900020, 8, 1);
						pc.getInventory().consumeEnchantItem(222352, 8, 1);
					} 
				}else if (itemtype == 3547){  //9완력성장
					if (pc.getInventory().checkEnchantItem(900020, 9, 1) && pc.getInventory().checkEnchantItem(222352, 9, 1)	
							){
						인첸트지급(pc, 232355, 1, 9);
						pc.getInventory().consumeEnchantItem(900020, 9, 1);
						pc.getInventory().consumeEnchantItem(222352, 9, 1);
					} 
				}else if (itemtype == 3548){  //10완력성장
					if (pc.getInventory().checkEnchantItem(900020, 10, 1) && pc.getInventory().checkEnchantItem(222352, 10, 1)	
							){
						인첸트지급(pc, 232355, 1, 10);
						pc.getInventory().consumeEnchantItem(900020, 10, 1);
						pc.getInventory().consumeEnchantItem(222352, 10, 1);
					} 
					
					
				}else if (itemtype == 3549){  //3민첩성장
					if (pc.getInventory().checkEnchantItem(900020, 3, 1) && pc.getInventory().checkEnchantItem(222353, 3, 1)	
							){
						인첸트지급(pc, 232356, 1, 3);
						pc.getInventory().consumeEnchantItem(900020, 3, 1);
						pc.getInventory().consumeEnchantItem(222353, 3, 1);
					} 
				}else if (itemtype == 3550){  //4민첩성장
					if (pc.getInventory().checkEnchantItem(900020, 4, 1) && pc.getInventory().checkEnchantItem(222353, 4, 1)	
							){
						인첸트지급(pc, 232356, 1, 4);
						pc.getInventory().consumeEnchantItem(900020, 4, 1);
						pc.getInventory().consumeEnchantItem(222353, 4, 1);
					} 
				}else if (itemtype == 3551){  //5민첩성장
					if (pc.getInventory().checkEnchantItem(900020, 5, 1) && pc.getInventory().checkEnchantItem(222353, 5, 1)	
							){
						인첸트지급(pc, 232356, 1, 5);
						pc.getInventory().consumeEnchantItem(900020, 5, 1);
						pc.getInventory().consumeEnchantItem(222353, 5, 1);
					} 
				}else if (itemtype == 3552){  //6민첩성장
					if (pc.getInventory().checkEnchantItem(900020, 6, 1) && pc.getInventory().checkEnchantItem(222353, 6, 1)	
							){
						인첸트지급(pc, 232356, 1, 6);
						pc.getInventory().consumeEnchantItem(900020, 6, 1);
						pc.getInventory().consumeEnchantItem(222353, 6, 1);
					} 
				}else if (itemtype == 3553){  //7민첩성장
					if (pc.getInventory().checkEnchantItem(900020, 7, 1) && pc.getInventory().checkEnchantItem(222353, 7, 1)	
							){
						인첸트지급(pc, 232356, 1, 7);
						pc.getInventory().consumeEnchantItem(900020, 7, 1);
						pc.getInventory().consumeEnchantItem(222353, 7, 1);
					} 
				}else if (itemtype == 3554){  //8민첩성장
					if (pc.getInventory().checkEnchantItem(900020, 8, 1) && pc.getInventory().checkEnchantItem(222353, 8, 1)	
							){
						인첸트지급(pc, 232356, 1, 8);
						pc.getInventory().consumeEnchantItem(900020, 8, 1);
						pc.getInventory().consumeEnchantItem(222353, 8, 1);
					} 
				}else if (itemtype == 3555){  //9민첩성장
					if (pc.getInventory().checkEnchantItem(900020, 9, 1) && pc.getInventory().checkEnchantItem(222353, 9, 1)	
							){
						인첸트지급(pc, 232356, 1, 9);
						pc.getInventory().consumeEnchantItem(900020, 9, 1);
						pc.getInventory().consumeEnchantItem(222353, 9, 1);
					} 
				}else if (itemtype == 3556){  //10민첩성장
					if (pc.getInventory().checkEnchantItem(900020, 10, 1) && pc.getInventory().checkEnchantItem(222353, 10, 1)	
							){
						인첸트지급(pc, 232356, 1, 10);
						pc.getInventory().consumeEnchantItem(900020, 10, 1);
						pc.getInventory().consumeEnchantItem(222353, 10, 1);
					} 
					
					
				}else if (itemtype == 3557){  //3지식성장
					if (pc.getInventory().checkEnchantItem(900020, 3, 1) && pc.getInventory().checkEnchantItem(222354, 3, 1)	
							){
						인첸트지급(pc, 232356, 1, 3);
						pc.getInventory().consumeEnchantItem(900020, 3, 1);
						pc.getInventory().consumeEnchantItem(222354, 3, 1);
					} 
				}else if (itemtype == 3558){  //4지식성장
					if (pc.getInventory().checkEnchantItem(900020, 4, 1) && pc.getInventory().checkEnchantItem(222354, 4, 1)	
							){
						인첸트지급(pc, 232356, 1, 4);
						pc.getInventory().consumeEnchantItem(900020, 4, 1);
						pc.getInventory().consumeEnchantItem(222354, 4, 1);
					} 
				}else if (itemtype == 3559){  //5지식성장
					if (pc.getInventory().checkEnchantItem(900020, 5, 1) && pc.getInventory().checkEnchantItem(222354, 5, 1)	
							){
						인첸트지급(pc, 232356, 1, 5);
						pc.getInventory().consumeEnchantItem(900020, 5, 1);
						pc.getInventory().consumeEnchantItem(222354, 5, 1);
					} 
				}else if (itemtype == 3560){  //6지식성장
					if (pc.getInventory().checkEnchantItem(900020, 6, 1) && pc.getInventory().checkEnchantItem(222354, 6, 1)	
							){
						인첸트지급(pc, 232356, 1, 6);
						pc.getInventory().consumeEnchantItem(900020, 6, 1);
						pc.getInventory().consumeEnchantItem(222354, 6, 1);
					} 
				}else if (itemtype == 3561){  //7지식성장
					if (pc.getInventory().checkEnchantItem(900020, 7, 1) && pc.getInventory().checkEnchantItem(222354, 7, 1)	
							){
						인첸트지급(pc, 232356, 1, 7);
						pc.getInventory().consumeEnchantItem(900020, 7, 1);
						pc.getInventory().consumeEnchantItem(222354, 7, 1);
					} 
				}else if (itemtype == 3562){  //8지식성장
					if (pc.getInventory().checkEnchantItem(900020, 8, 1) && pc.getInventory().checkEnchantItem(222354, 8, 1)	
							){
						인첸트지급(pc, 232356, 1, 8);
						pc.getInventory().consumeEnchantItem(900020, 8, 1);
						pc.getInventory().consumeEnchantItem(222354, 8, 1);
					} 
				}else if (itemtype == 3563){  //9지식성장
					if (pc.getInventory().checkEnchantItem(900020, 9, 1) && pc.getInventory().checkEnchantItem(222354, 9, 1)	
							){
						인첸트지급(pc, 232356, 1, 9);
						pc.getInventory().consumeEnchantItem(900020, 9, 1);
						pc.getInventory().consumeEnchantItem(222354, 9, 1);
					} 
				}else if (itemtype == 3564){  //10지식성장
					if (pc.getInventory().checkEnchantItem(900020, 10, 1) && pc.getInventory().checkEnchantItem(222354, 10, 1)	
							){
						인첸트지급(pc, 232356, 1, 10);
						pc.getInventory().consumeEnchantItem(900020, 10, 1);
						pc.getInventory().consumeEnchantItem(222354, 10, 1);
					} 
					
					
					
				}else if (itemtype == 3566){  //3완력회복
					if (pc.getInventory().checkEnchantItem(900021, 3, 1) && pc.getInventory().checkEnchantItem(222352, 3, 1)	
							){
						인첸트지급(pc, 232356, 1, 3);
						pc.getInventory().consumeEnchantItem(900020, 3, 1);
						pc.getInventory().consumeEnchantItem(222354, 3, 1);
					} 
				}else if (itemtype == 3567){  //4완력회복
					if (pc.getInventory().checkEnchantItem(900021, 4, 1) && pc.getInventory().checkEnchantItem(222352, 4, 1)	
							){
						인첸트지급(pc, 232356, 1, 4);
						pc.getInventory().consumeEnchantItem(900020, 4, 1);
						pc.getInventory().consumeEnchantItem(222354, 4, 1);
					} 
				}else if (itemtype == 3568){  //5완력회복
					if (pc.getInventory().checkEnchantItem(900021, 5, 1) && pc.getInventory().checkEnchantItem(222352, 5, 1)	
							){
						인첸트지급(pc, 232356, 1, 5);
						pc.getInventory().consumeEnchantItem(900020, 5, 1);
						pc.getInventory().consumeEnchantItem(222354, 5, 1);
					} 
				}else if (itemtype == 3569){  //6완력회복
					if (pc.getInventory().checkEnchantItem(900021, 6, 1) && pc.getInventory().checkEnchantItem(222352, 6, 1)	
							){
						인첸트지급(pc, 232356, 1, 6);
						pc.getInventory().consumeEnchantItem(900020, 6, 1);
						pc.getInventory().consumeEnchantItem(222354, 6, 1);
					} 
				}else if (itemtype == 3570){  //7완력회복
					if (pc.getInventory().checkEnchantItem(900021, 7, 1) && pc.getInventory().checkEnchantItem(222352, 7, 1)	
							){
						인첸트지급(pc, 232356, 1, 7);
						pc.getInventory().consumeEnchantItem(900020, 7, 1);
						pc.getInventory().consumeEnchantItem(222354, 7, 1);
					} 
				}else if (itemtype == 3571){  //8완력회복
					if (pc.getInventory().checkEnchantItem(900021, 8, 1) && pc.getInventory().checkEnchantItem(222352, 8, 1)	
							){
						인첸트지급(pc, 232356, 1, 8);
						pc.getInventory().consumeEnchantItem(900020, 8, 1);
						pc.getInventory().consumeEnchantItem(222354, 8, 1);
					} 
				}else if (itemtype == 3572){  //9완력회복
					if (pc.getInventory().checkEnchantItem(900021, 9, 1) && pc.getInventory().checkEnchantItem(222352, 9, 1)	
							){
						인첸트지급(pc, 232356, 1, 9);
						pc.getInventory().consumeEnchantItem(900020, 9, 1);
						pc.getInventory().consumeEnchantItem(222354, 9, 1);
					} 
				}else if (itemtype == 3573){  //10완력회복
					if (pc.getInventory().checkEnchantItem(900021, 10, 1) && pc.getInventory().checkEnchantItem(222352, 10, 1)	
							){
						인첸트지급(pc, 232356, 1, 10);
						pc.getInventory().consumeEnchantItem(900020, 10, 1);
						pc.getInventory().consumeEnchantItem(222354, 10, 1);
					} 
					
					
					
				}else if (itemtype == 3574){  //3민첩회복
					if (pc.getInventory().checkEnchantItem(900021, 3, 1) && pc.getInventory().checkEnchantItem(222353, 3, 1)	
							){
						인첸트지급(pc, 232356, 1, 3);
						pc.getInventory().consumeEnchantItem(900020, 3, 1);
						pc.getInventory().consumeEnchantItem(222354, 3, 1);
					}
				}else if (itemtype == 3575){  //4민첩회복
					if (pc.getInventory().checkEnchantItem(900021, 4, 1) && pc.getInventory().checkEnchantItem(222353, 4, 1)	
							){
						인첸트지급(pc, 232356, 1, 4);
						pc.getInventory().consumeEnchantItem(900020, 4, 1);
						pc.getInventory().consumeEnchantItem(222354, 4, 1);
					} 
				}else if (itemtype == 3576){  //5민첩회복
					if (pc.getInventory().checkEnchantItem(900021, 5, 1) && pc.getInventory().checkEnchantItem(222353, 5, 1)	
							){
						인첸트지급(pc, 232356, 1, 5);
						pc.getInventory().consumeEnchantItem(900020, 5, 1);
						pc.getInventory().consumeEnchantItem(222354, 5, 1);
					} 
				}else if (itemtype == 3577){  //6민첩회복
					if (pc.getInventory().checkEnchantItem(900021, 6, 1) && pc.getInventory().checkEnchantItem(222353, 6, 1)	
							){
						인첸트지급(pc, 232356, 1, 6);
						pc.getInventory().consumeEnchantItem(900020, 6, 1);
						pc.getInventory().consumeEnchantItem(222354, 6, 1);
					} 
				}else if (itemtype == 3578){  //7민첩회복
					if (pc.getInventory().checkEnchantItem(900021, 7, 1) && pc.getInventory().checkEnchantItem(222353, 7, 1)	
							){
						인첸트지급(pc, 232356, 1, 7);
						pc.getInventory().consumeEnchantItem(900020, 7, 1);
						pc.getInventory().consumeEnchantItem(222354, 7, 1);
					} 
				}else if (itemtype == 3579){  //8민첩회복
					if (pc.getInventory().checkEnchantItem(900021, 8, 1) && pc.getInventory().checkEnchantItem(222353, 8, 1)	
							){
						인첸트지급(pc, 232356, 1, 8);
						pc.getInventory().consumeEnchantItem(900020, 8, 1);
						pc.getInventory().consumeEnchantItem(222354, 8, 1);
					} 
				}else if (itemtype == 3580){  //9민첩회복
					if (pc.getInventory().checkEnchantItem(900021, 9, 1) && pc.getInventory().checkEnchantItem(222353, 9, 1)	
							){
						인첸트지급(pc, 232356, 1, 9);
						pc.getInventory().consumeEnchantItem(900020, 9, 1);
						pc.getInventory().consumeEnchantItem(222354, 9, 1);
					} 
				}else if (itemtype == 3581){  //10민첩회복
					if (pc.getInventory().checkEnchantItem(900021, 10, 1) && pc.getInventory().checkEnchantItem(222353, 10, 1)	
							){
						인첸트지급(pc, 232356, 1, 10);
						pc.getInventory().consumeEnchantItem(900020, 10, 1);
						pc.getInventory().consumeEnchantItem(222354, 10, 1);
					} 
					
				}else if (itemtype == 3582){  //3지식회복
					if (pc.getInventory().checkEnchantItem(900021, 3, 1) && pc.getInventory().checkEnchantItem(222354, 3, 1)	
							){
						인첸트지급(pc, 232356, 1, 3);
						pc.getInventory().consumeEnchantItem(900020, 3, 1);
						pc.getInventory().consumeEnchantItem(222354, 3, 1);
					} 
				}else if (itemtype == 3583){  //4지식회복
					if (pc.getInventory().checkEnchantItem(900021, 4, 1) && pc.getInventory().checkEnchantItem(222354, 4, 1)	
							){
						인첸트지급(pc, 232356, 1, 4);
						pc.getInventory().consumeEnchantItem(900020, 4, 1);
						pc.getInventory().consumeEnchantItem(222354, 4, 1);
					} 
				}else if (itemtype == 3584){  //5지식회복
					if (pc.getInventory().checkEnchantItem(900021, 5, 1) && pc.getInventory().checkEnchantItem(222354, 5, 1)	
							){
						인첸트지급(pc, 232356, 1, 5);
						pc.getInventory().consumeEnchantItem(900020, 5, 1);
						pc.getInventory().consumeEnchantItem(222354, 5, 1);
					} 
				}else if (itemtype == 3585){  //6지식회복
					if (pc.getInventory().checkEnchantItem(900021, 6, 1) && pc.getInventory().checkEnchantItem(222354, 6, 1)	
							){
						인첸트지급(pc, 232356, 1, 6);
						pc.getInventory().consumeEnchantItem(900020, 6, 1);
						pc.getInventory().consumeEnchantItem(222354, 6, 1);
					} 
				}else if (itemtype == 3586){  //7지식회복
					if (pc.getInventory().checkEnchantItem(900021, 7, 1) && pc.getInventory().checkEnchantItem(222354, 3, 1)	
							){
						인첸트지급(pc, 232356, 1, 7);
						pc.getInventory().consumeEnchantItem(900020, 7, 1);
						pc.getInventory().consumeEnchantItem(222354, 7, 1);
					} 
				}else if (itemtype == 3587){  //8지식회복
					if (pc.getInventory().checkEnchantItem(900021, 8, 1) && pc.getInventory().checkEnchantItem(222354, 8, 1)	
							){
						인첸트지급(pc, 232356, 1, 8);
						pc.getInventory().consumeEnchantItem(900020, 8, 1);
						pc.getInventory().consumeEnchantItem(222354, 8, 1);
					} 
				}else if (itemtype == 3588){  //9지식회복
					if (pc.getInventory().checkEnchantItem(900021, 9, 1) && pc.getInventory().checkEnchantItem(222354, 9, 1)	
							){
						인첸트지급(pc, 232356, 1, 9);
						pc.getInventory().consumeEnchantItem(900020, 9, 1);
						pc.getInventory().consumeEnchantItem(222354, 9, 1);
					} 
				}else if (itemtype == 3589){  //10지식회복
					if (pc.getInventory().checkEnchantItem(900021, 10, 1) && pc.getInventory().checkEnchantItem(222354, 10, 1)	
							){
						인첸트지급(pc, 232356, 1, 10);
						pc.getInventory().consumeEnchantItem(900020, 10, 1);
						pc.getInventory().consumeEnchantItem(222354, 10, 1);
					} 
					
					
				}else if (itemtype == 3590){  //3회복성장
					if (pc.getInventory().checkEnchantItem(900021, 3, 1) && pc.getInventory().checkEnchantItem(900020, 3, 1)	
							){
						인첸트지급(pc, 232356, 1, 3);
						pc.getInventory().consumeEnchantItem(900020, 3, 1);
						pc.getInventory().consumeEnchantItem(900020, 3, 1);
					} 
				}else if (itemtype == 3591){  //4회복성장
					if (pc.getInventory().checkEnchantItem(900021, 4, 1) && pc.getInventory().checkEnchantItem(900020, 4, 1)	
							){
						인첸트지급(pc, 232356, 1, 4);
						pc.getInventory().consumeEnchantItem(900020, 4, 1);
						pc.getInventory().consumeEnchantItem(900020, 4, 1);
					} 
				}else if (itemtype == 3592){  //5회복성장
					if (pc.getInventory().checkEnchantItem(900021, 5, 1) && pc.getInventory().checkEnchantItem(900020, 5, 1)	
							){
						인첸트지급(pc, 232356, 1, 5);
						pc.getInventory().consumeEnchantItem(900020, 5, 1);
						pc.getInventory().consumeEnchantItem(900020, 5, 1);
					} 
				}else if (itemtype == 3593){  //6회복성장
					if (pc.getInventory().checkEnchantItem(900021, 6, 1) && pc.getInventory().checkEnchantItem(900020, 6, 1)	
							){
						인첸트지급(pc, 232356, 1, 6);
						pc.getInventory().consumeEnchantItem(900020, 6, 1);
						pc.getInventory().consumeEnchantItem(900020, 6, 1);
					} 
				}else if (itemtype == 3594){  //7회복성장
					if (pc.getInventory().checkEnchantItem(900021, 7, 1) && pc.getInventory().checkEnchantItem(900020, 7, 1)	
							){
						인첸트지급(pc, 232356, 1, 7);
						pc.getInventory().consumeEnchantItem(900020, 7, 1);
						pc.getInventory().consumeEnchantItem(900020, 7, 1);
					} 
				}else if (itemtype == 3595){  //8회복성장
					if (pc.getInventory().checkEnchantItem(900021, 8, 1) && pc.getInventory().checkEnchantItem(900020, 8, 1)	
							){
						인첸트지급(pc, 232356, 1, 8);
						pc.getInventory().consumeEnchantItem(900020, 8, 1);
						pc.getInventory().consumeEnchantItem(900020, 8, 1);
					} 
				}else if (itemtype == 3596){  //9회복성장
					if (pc.getInventory().checkEnchantItem(900021, 9, 1) && pc.getInventory().checkEnchantItem(900020, 9, 1)	
							){
						인첸트지급(pc, 232356, 1, 9);
						pc.getInventory().consumeEnchantItem(900020, 9, 1);
						pc.getInventory().consumeEnchantItem(900020, 9, 1);
					} 
				}else if (itemtype == 3597){  //10회복성장
					if (pc.getInventory().checkEnchantItem(900021, 10, 1) && pc.getInventory().checkEnchantItem(900020, 10, 1)	
							){
						인첸트지급(pc, 232356, 1, 10);
						pc.getInventory().consumeEnchantItem(900020, 10, 1);
						pc.getInventory().consumeEnchantItem(900020, 10, 1);
					} 
					
					
				}else if (itemtype == 2871){  //+3성장문장
					if (pc.getInventory().checkEnchantItem(3000099, 0, 6)){
						인첸트지급(pc, 900020, 1, 3);
						pc.getInventory().consumeEnchantItem(3000099, 0, 6);
					} 
				}else if (itemtype == 2872){  //+3회복문장
					if (pc.getInventory().checkEnchantItem(3000098, 0, 6)){
						인첸트지급(pc, 900021, 1, 3);
						pc.getInventory().consumeEnchantItem(3000098, 0, 6);
					} 
				}else if (itemtype == 2873){  //+3완력문장
					if (pc.getInventory().checkEnchantItem(61523, 0, 6)){
						인첸트지급(pc, 222352, 1, 3);
						pc.getInventory().consumeEnchantItem(61523, 0, 6);
					} 
				}else if (itemtype == 2874){  //+3민첩문장
					if (pc.getInventory().checkEnchantItem(61524, 0, 6)){
						인첸트지급(pc, 222353, 1, 3);
						pc.getInventory().consumeEnchantItem(61524, 0, 6);
					} 
				}else if (itemtype == 2875){  //+3지식문장
					if (pc.getInventory().checkEnchantItem(61525, 0, 6)){
						인첸트지급(pc, 222352, 1, 3);
						pc.getInventory().consumeEnchantItem(61525, 0, 6);
					} 
	
				}
				
			} else {
				pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.CRAFT_OK));
				return;
			}
			action = NpcActionTable.getInstance().get(s, pc, npc);
			

			if (action != null) {
				L1NpcHtml result = action.executeWithAmount(s, pc, npc, itemcount);
				if (result != null) {
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), result));
				} else {
					pc.sendPackets(new S_SystemMessage("해당 제작 아이템은 서버에 준비되지 않았습니다."));
				}
				pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.CRAFT_OK));
				pc.sendPackets(new S_SkillSound(pc.getId(), 7976));
				pc.broadcastPacket(new S_SkillSound(pc.getId(), 7976));
			}
			break;
		case 0x0146:	// 혈맹 가입신청 받기 설정
			if (pc.getClanid()==0 || (!pc.isCrown() && pc.getClanRank() != L1Clan.수호))
				return;
			readC();
			readH();
			int setting = readC();
			readC();
			int setting2 = readC();
			if (setting2 == 2) {
				pc.sendPackets(new S_SystemMessage("현재 암호 가입 유형으로 설정할 수 없습니다."), true);
				setting2 = 1;
			}

			pc.getClan().setJoinSetting(setting);
			pc.getClan().setJoinType(setting2);
			pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.CLAN_JOIN_SETTING, setting, setting2), true);
			ClanTable.getInstance().updateClan(pc.getClan());
			pc.sendPackets(new S_ServerMessage(3980), true);
			break;
		case 0x014C:	// 혈맹 모집 셋팅
			if (pc.getClanid() == 0)
				return;
			pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.CLAN_JOIN_SETTING, pc.getClan().getJoinSetting(), pc.getClan().getJoinType()), true);
			break;
		case 322: // 혈맹가입
		{
			readC();
			readH();
			int length = readC();
			byte[] BYTE = readByte();
			
			// 존재하지 않는 혈맹입니다.
			if(pc.isCrown()) {
				pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.CLAN_JOIN_MESSAGE, 4), true);
				break;
			}

			// 이미 혈맹에 가입한 상태 입니다.
			if (pc.getClanid() != 0) {
				pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.CLAN_JOIN_MESSAGE, 9), true);
				break;
			}

			//  군주를 만나 가입해 주세요.
			try {
				String clanname = new String(BYTE, 0, length, "MS949");
				L1Clan clan = L1World.getInstance().getClan(clanname);
				//
				if (clan == null) {
					pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.CLAN_JOIN_MESSAGE, 13), true);
					break;
				}
				//
				L1PcInstance crown = clan.getonline간부();
				if (crown == null) {
					pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.CLAN_JOIN_MESSAGE, 11), true);
					break;
				}
				//
				if (clan.getJoinSetting() == 0) {
					pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.CLAN_JOIN_MESSAGE, 8), true);

				} else if (clan.getJoinType() == 0) {
					L1ClanJoin.getInstance().ClanJoin(crown, pc);
					pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.CLAN_JOIN_MESSAGE, 0), true);
				} else {
					pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.CLAN_JOIN_MESSAGE, 13), true);
				}
			} catch (Exception e) { }
			break;
		}
		case 표식설정: // 파티 표식설정
			int size = readH();
			byte[] flag = new byte[size];
			for (int i = 0; i < size; ++i)
				flag[i] = (byte) readC();
			//
			L1Party party = pc.getParty();
			if (party == null)
				return;
			//
			for (L1PcInstance member : party.getMembers())
				member.sendPackets(new S_ACTION_UI(flag));
			break;
		case ACCOUNT_TAM_UPDATE://탐창띄우기
			//pc.sendPackets(new S_TamWindow(pc.getAccountName()));
			break;
		case ACCOUNT_TAM://탐창띄우기
			pc.sendPackets(new S_TamWindow(pc.getAccountName()));
			break;
		case ACCOUNT_TAM_CANCEL://탐 
			readC();
			readH();
			byte[] BYTE = readByte();
			byte[] temp = new byte[BYTE.length - 1];
			for (int i = 0; i < temp.length; i++) {
				temp[i] = BYTE[i];
			}
			StringBuffer sb = new StringBuffer();
			for (byte zzz : temp) {
				sb.append(String.valueOf(zzz));
			}
			int day = Nexttam(sb.toString());
			int charobjid = TamCharid(sb.toString());
			if (charobjid != pc.getId()) {
				pc.sendPackets(new S_SystemMessage("알림: 해당 케릭터만 취소를 할 수 있습니다."));
				return;
			}
			int itemid = 0;
			if (day != 0) {
				if (day == 3) {//기간 3일
					itemid = 600226;
				} else if (day == 30){//기간30일
					itemid = 600227;
				}
				L1ItemInstance item = pc.getInventory().storeItem(itemid, 1);
				if (item != null) {
					pc.sendPackets(new S_ServerMessage(403, item.getName() + " (1)"));
					tamcancle(sb.toString());
					pc.sendPackets(new S_TamWindow(pc.getAccountName()));
				}
			}
			break;
		case 액션: // 소셜액션
			readD();
			readC();
			int action1 = readC();
			if (action1 >= 1 && action1 <= 11) {
				pc.sendPackets(new S_ACTION_UI2(S_ACTION_UI2.EMOTICON, action1, pc.getId()), true);
				Broadcaster.broadcastPacket(pc,new S_ACTION_UI2(S_ACTION_UI2.EMOTICON, action1,pc.getId()), true);
			}
			break;
		case 공성관련:
			try {
				readH();
				readC();
				int castleType = readC();
				// 1켄트 2기란 4오크요새
				for (L1Clan cc : L1World.getInstance().getAllClans()) {
					if (castleType == cc.getCastleId()) {
						s = cc.getClanName();
						break;
					}
				}

				if (s.equalsIgnoreCase("")) {
					return;
				}

				L1PcInstance player = pc;
				String clanName = player.getClanname();
				int clanId = player.getClanid();

				if (!player.isCrown()) { // 군주 이외
					S_ServerMessage sm = new S_ServerMessage(478);
					player.sendPackets(sm, true);
					return;
				}
				if (clanId == 0) { // 크란미소속
					S_ServerMessage sm = new S_ServerMessage(272);
					player.sendPackets(sm, true);
					return;
				}
				L1Clan clan = L1World.getInstance().getClan(clanName);
				if (clan == null) { // 자크란이 발견되지 않는다
					S_SystemMessage sm = new S_SystemMessage("대상 혈맹이 발견되지 않았습니다.");
					player.sendPackets(sm, true);
					return;
				}
				if (player.getId() != clan.getLeaderId()) { // 혈맹주
					S_ServerMessage sm = new S_ServerMessage(478);
					player.sendPackets(sm, true);
					return;
				}
				if (clanName.toLowerCase().equals(s.toLowerCase())) { // 자크란을 지정
					S_SystemMessage sm = new S_SystemMessage("자신의 혈에 공성 선포는 불가능합니다.");
					player.sendPackets(sm, true);
					return;
				}
				L1Clan enemyClan = null;
				String enemyClanName = null;
				for (L1Clan checkClan : L1World.getInstance().getAllClans()) { // 크란명을 체크
					if (checkClan.getClanName().toLowerCase().equals(s.toLowerCase())) {
						enemyClan = checkClan;
						enemyClanName = checkClan.getClanName();
						break;
					}
				}
				if (enemyClan == null) { // 상대 크란이 발견되지 않았다
					S_SystemMessage sm = new S_SystemMessage("대상 혈맹이 발견되지 않았습니다.");
					player.sendPackets(sm, true);
					return;
				}
//				if (clan.getAlliance() == enemyClan.getAlliance()) {
//					S_ServerMessage sm = new S_ServerMessage(1205);
//					player.sendPackets(sm, true);
//					return;
//				}
				List<L1War> warList = L1World.getInstance().getWarList(); // 전쟁 리스트를 취득
				if (clan.getCastleId() != 0) { // 자크란이 성주
					S_ServerMessage sm = new S_ServerMessage(474);
					player.sendPackets(sm, true);
					return;
				}
				if (enemyClan.getCastleId() != 0 && player.getLevel() < Config.선포레벨) {
					player.sendPackets(new S_SystemMessage("레벨 " + Config.선포레벨 + "부터 선포할 수 있습니다."));
					return;
				}


				if (clan.getOnlineClanMember().length <= Config.혈맹접속인원) {   
					player.sendPackets(new S_SystemMessage("접속한 혈맹원이 "+Config.혈맹접속인원+"명 이상이면 선포가 가능합니다."));
					return;
				}

				/*if (clan.getHouseId() > 0) {
					S_SystemMessage sm = new S_SystemMessage("아지트가 있는 상태에서는 선전 포고를 할 수 없습니다.");
					player.sendPackets(sm, true);
					return;
				}*/
				if (enemyClan.getCastleId() != 0) { // 상대 크란이 성주
					int castle_id = enemyClan.getCastleId();
					if (WarTimeController.getInstance().isNowWar(castle_id)) { // 전쟁 시간내
						L1PcInstance clanMember[] = clan.getOnlineClanMember();
						for (int k = 0; k < clanMember.length; k++) {
							if (L1CastleLocation.checkInWarArea(castle_id, clanMember[k])) {
								int[] loc = new int[3];
								Random _rnd = new Random(System.nanoTime());
								loc = L1CastleLocation
										.getGetBackLoc(castle_id);
								int locx = loc[0] + (_rnd.nextInt(4) - 2);
								int locy = loc[1] + (_rnd.nextInt(4) - 2);
								short mapid = (short) loc[2];
								new L1Teleport().teleport(clanMember[k], locx,
										locy, mapid, clanMember[k]
												.getHeading(), true);
							}
						}
						boolean enemyInWar = false;
						for (L1War war : warList) {
							if (war.CheckClanInWar(enemyClanName)) { // 상대 크란이 이미 전쟁중
								war.DeclareWar(clanName, enemyClanName);
								war.AddAttackClan(clanName);
								enemyInWar = true;
								break;
							}
						}
						if (!enemyInWar) { // 상대 크란이 전쟁중 이외로, 선전포고
							L1War war = new L1War();
							war.handleCommands(1, clanName, enemyClanName); // 공성전 개시
						}
					} else { // 전쟁 시간외
						S_ServerMessage sm = new S_ServerMessage(476);
						player.sendPackets(sm, true); // 아직 공성전의 시간이 아닙니다.
					}
				} else { // 상대 크란이 성주는 아니다
					return;
				}
			} catch (Exception e) {
			} finally {
				clear();
			}
			break;
		default:
			// System.out.println(type);
			break;
		}
	}

	private boolean 인첸트지급(L1PcInstance pc, int item_id, int count, int EnchantLevel) {
		L1ItemInstance item = ItemTable.getInstance().createItem(item_id);
		if (item != null) {
			item.setCount(count);
			item.setEnchantLevel(EnchantLevel);
			item.setIdentified(true);
			if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
				pc.getInventory().storeItem(item);
			} else {
				pc.sendPackets(new S_ServerMessage(82));// 무게 게이지가 부족하거나 인벤토리가 꽉차서 더 들 수 없습니다.
				return false;
			}
			pc.sendPackets(new S_SystemMessage("아이템 제작에 성공했습니다."));
			pc.sendPackets(new S_ServerMessage(143, item.getLogName())); // %0를 손에 넣었습니다.
			pc.sendPackets(new S_SkillSound(pc.getId(), 7976));
			pc.broadcastPacket(new S_SkillSound(pc.getId(), 7976));
			return true;
		} else {
			return false;
		}
	}

	public int Nexttam(String encobj) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		int day = 0;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT day FROM `tam` WHERE encobjid = ? order by id asc limit 1"); // 케릭터 테이블에서 군주만 골라와서
			pstm.setString(1, encobj);
			rs = pstm.executeQuery();
			while (rs.next()) {
				day = rs.getInt("Day");
			}
		} catch (SQLException e) {
			// _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return day;
	}
	
	public int TamCharid(String encobj) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		int objid = 0;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT objid FROM `tam` WHERE encobjid = ? order by id asc limit 1"); // 케릭터 테이블에서 군주만 골라와서
			pstm.setString(1, encobj);
			rs = pstm.executeQuery();
			while (rs.next()) {
				objid = rs.getInt("objid");
			}
		} catch (SQLException e) {
			// _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return objid;
	}
	
	public void tamcancle(String objectId) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("delete from tam where encobjid = ? order by id asc limit 1");
			pstm.setString(1, objectId);
			pstm.executeUpdate();
		} catch (SQLException e) {
			// _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	
	@Override
	public String getType() {
		return C_ACTION_UI;
	}

}