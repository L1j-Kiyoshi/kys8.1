package l1j.server.server.clientpackets;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import l1j.server.server.ActionCodes;
import l1j.server.server.GameClient;
import l1j.server.server.Controller.FishingTimeController;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_ChangeShape;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_DoActionShop;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1PrivateShopBuyList;
import l1j.server.server.templates.L1PrivateShopSellList;

public class C_Shop extends ClientBasePacket {

	private static final String C_SHOP = "[C] C_Shop";

	public C_Shop(byte abyte0[], GameClient clientthread) {
		super(abyte0);

		L1PcInstance pc = clientthread.getActiveChar();
		if (pc == null || pc.isGhost() || pc.isDead()) {
			return;
		}
		if (pc.isInvisble()) {
			pc.sendPackets(new S_ServerMessage(755));
			return;
		}
		if (pc.getMapId() != 800) {
			pc.sendPackets(new S_SystemMessage("개인상점은 시장에서만  열수 있습니다."));
			return;
		}
		
		if (pc.getMapId() != 800) {
			if (pc.isFishing()) {
				try {
					pc.setFishing(false);
					pc.setFishingTime(0);
					pc.setFishingReady(false);
					pc.sendPackets(new S_CharVisualUpdate(pc));
					Broadcaster.broadcastPacket(pc, new S_CharVisualUpdate(pc));
					FishingTimeController.getInstance().removeMember(pc);
					pc.sendPackets(new S_ServerMessage(2120)); 
					return;
				} catch (Exception e) {
				}
			} else {
				pc.sendPackets(new S_ServerMessage(3405)); 
				return;
			}
		}

		if (pc.getInventory().checkEquipped(22232) || pc.getInventory().checkEquipped(22234) || 
			pc.getInventory().checkEquipped(22233) || pc.getInventory().checkEquipped(22235) ||	
			pc.getInventory().checkEquipped(22236) || pc.getInventory().checkEquipped(22237) || 
			pc.getInventory().checkEquipped(22238) || pc.getInventory().checkEquipped(22239) || 
			pc.getInventory().checkEquipped(22240) || pc.getInventory().checkEquipped(22241) ||
			pc.getInventory().checkEquipped(22242) || pc.getInventory().checkEquipped(22243) || 
			pc.getInventory().checkEquipped(22244) || pc.getInventory().checkEquipped(22245) ||
			pc.getInventory().checkEquipped(22246) || pc.getInventory().checkEquipped(22247) || 
			pc.getInventory().checkEquipped(22248) || pc.getInventory().checkEquipped(22249)) { //룬 방어구				
			pc.sendPackets(new S_ChatPacket(pc,"룬을 착용하셨다면 해제하시기 바랍니다."));
			return;
		}
		
		if (pc.getInventory().checkEquipped(10000)) {
			pc.sendPackets(new S_ChatPacket(pc,"직장인 경험치 아이템을 해제하세요."));
			return;
		}
		
		if (pc.getTempCharGfx() != pc.getClassId()
				&& pc.getSkillEffectTimeSec(L1SkillId.SHAPE_CHANGE) <= 0) {
			pc.sendPackets(new S_SystemMessage("변신 아이템을 해제하세요."));
			return;
		}

		ArrayList<L1PrivateShopSellList> sellList = pc.getSellList();
		ArrayList<L1PrivateShopBuyList> buyList = pc.getBuyList();
		L1ItemInstance checkItem;
		boolean tradable = true;

		int type = readC();
		if (type == 0) { // 개시
			int sellTotalCount = readH();
			int sellObjectId;
			int sellPrice;
			int sellCount;
			Object[] petlist = null;
			for (int i = 0; i < sellTotalCount; i++) {
				sellObjectId = readD();
				sellPrice = readD();
				sellCount = readD();
				
				/** 개인상점 오류 수정 */
				if(sellTotalCount == 8){ 
					pc.sendPackets(new S_ChatPacket(pc,"물품등록은 7개까지만 가능합니다.")); 
					return;
				}
				
				// 거래 가능한 아이템이나 체크
				checkItem = pc.getInventory().getItem(sellObjectId);
				if (sellObjectId != checkItem.getId()) {
					pc.sendPackets(new S_Disconnect());
					return;
				}
				if (!checkItem.isStackable() && sellCount != 1) {
					pc.sendPackets(new S_Disconnect());
					return;
				}
				if (sellCount > checkItem.getCount()) {
					sellCount = checkItem.getCount();
				}
				if (checkItem.getCount() < sellCount || checkItem.getCount() <= 0 || sellCount <= 0) {
				     sellList.clear();  
				     buyList.clear();				     
					 return;
				}
				if(checkItem.getBless() >= 128){
					pc.sendPackets(new S_ServerMessage(210, checkItem.getItem().getName())); // \f1%0은 버리거나 또는 타인에게 양일을 할 수 없습니다.
					return;
				}
				if (!checkItem.getItem().isTradable()) {
					tradable = false;
					pc.sendPackets(new S_ServerMessage(166, checkItem.getItem().getName(), "거래 불가능합니다. "));
				}
				

				petlist = pc.getPetList().values().toArray();
				for (Object petObject : petlist) {
					if (petObject instanceof L1PetInstance) {
						L1PetInstance pet = (L1PetInstance) petObject;
						if (checkItem.getId() == pet.getItemObjId()) {
							tradable = false;
							pc.sendPackets(new S_ServerMessage(166, checkItem.getItem().getName(), "거래 불가능합니다. "));
							break;
						}
					}
				}

				for (Object dollObject : pc.getDollList()) {	
					if (dollObject instanceof L1DollInstance) {
						L1DollInstance doll = (L1DollInstance) dollObject;
						if (checkItem.getId() == doll.getItemObjId()) {
							tradable = false;
							pc.sendPackets(new S_ServerMessage(166, checkItem.getItem().getName(), "거래 불가능합니다. "));
							break;
						}
					}
				}
				L1PrivateShopSellList pssl = new L1PrivateShopSellList();
				pssl.setItemObjectId(sellObjectId);
				pssl.setSellPrice(sellPrice);
				pssl.setSellTotalCount(sellCount);
				pssl.setSellCount(0);
				sellList.add(pssl);
			}
			int buyTotalCount = readH();
			int buyObjectId;
			int buyPrice;
			int buyCount;
			for (int i = 0; i < buyTotalCount; i++) {
				buyObjectId = readD();
				buyPrice = readD();
				buyCount = readD();
				
				/** 개인상점 오류 수정 */
				if(sellTotalCount == 8){ 
					pc.sendPackets(new S_ChatPacket(pc,"물품등록은 7개까지만 가능합니다.")); 
					return;
				}
				// 거래 가능한 아이템이나 체크
				checkItem = pc.getInventory().getItem(buyObjectId);
				/*버그방지*/
				if (buyObjectId != checkItem.getId()) {
					pc.sendPackets(new S_Disconnect());
					return;
				}
				if (!checkItem.isStackable() && buyCount != 1) {
					pc.sendPackets(new S_Disconnect());
					return;
				}
				if (buyCount <= 0 || checkItem.getCount() <= 0) {
					pc.sendPackets(new S_Disconnect());
					return;
				}
				if (buyCount > checkItem.getCount()) {
					buyCount = checkItem.getCount();
				}
				/*버그방지*/
				// 거래 가능한 아이템이나 체크
				checkItem = pc.getInventory().getItem(buyObjectId);
				if (!checkItem.getItem().isTradable()) {
					tradable = false;
					pc.sendPackets(new S_ServerMessage(166, checkItem.getItem().getName(), "거래 불가능합니다. "));
				}
				petlist = pc.getPetList().values().toArray();
				for (Object petObject : petlist) {
					if (petObject instanceof L1PetInstance) {
						L1PetInstance pet = (L1PetInstance) petObject;
						if (checkItem.getId() == pet.getItemObjId()) {
							tradable = false;
							pc.sendPackets(new S_ServerMessage(166, checkItem.getItem().getName(), "거래 불가능합니다. "));
							break;
						}
					}
				}
				L1PrivateShopBuyList psbl = new L1PrivateShopBuyList();
				psbl.setItemObjectId(buyObjectId);
				psbl.setBuyPrice(buyPrice);
				psbl.setBuyTotalCount(buyCount);
				psbl.setBuyCount(0);
				buyList.add(psbl);
			}
			if (!tradable) { // 거래 불가능한 아이템이 포함되어 있는 경우, 개인 상점 종료
				sellList.clear();
				buyList.clear();
				pc.setPrivateShop(false);
				pc.sendPackets(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Idle));
				pc.broadcastPacket(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Idle));
				return;
			}
			byte[] chat = readByte();
			String test;
			int poly;
			test = null;
			try {
				test = new String(chat, 0, chat.length, "MS949");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			pc.getNetConnection().getAccount().updateShopOpenCount();
			pc.sendPackets(new S_PacketBox(S_PacketBox.SHOP_OPEN_COUNT, pc
					.getNetConnection().getAccount().Shop_open_count), true);
			
			pc.setShopChat(chat);
			pc.setPrivateShop(true);
			pc.sendPackets(new S_DoActionShop(pc.getId(), ActionCodes.ACTION_Shop, chat));
			pc.broadcastPacket(new S_DoActionShop(pc.getId(), ActionCodes.ACTION_Shop, chat));
			pc.sendPackets(new S_ChatPacket(pc, "명령어 [.무인상점] 누른후 다른 캐릭터로 접속 가능합니다"));

			poly = 0;
			if (test.matches(".*tradezone1.*"))
				poly = 11479;
			else if (test.matches(".*tradezone2.*"))
				poly = 11483;
			else if (test.matches(".*tradezone3.*"))
				poly = 11480;
			else if (test.matches(".*tradezone4.*"))
				poly = 11485;
			else if (test.matches(".*tradezone5.*"))
				poly = 11482;
			else if (test.matches(".*tradezone6.*"))
				poly = 11486;
			else if (test.matches(".*tradezone7.*"))
				poly = 11481;
			else if (test.matches(".*tradezone8.*")) {
				poly = 11484;
			}
			test = null;
			pc.상점변신 = poly;
			pc.sendPackets(new S_ChangeShape(pc.getId(), poly, 70));
			Broadcaster.broadcastPacket(pc, new S_ChangeShape(pc.getId(), poly, 70));
			pc.sendPackets(new S_CharVisualUpdate(pc));
			Broadcaster.broadcastPacket(pc, new S_CharVisualUpdate(pc));
			pc.curePoison();
			
		} else if (type == 1) { // 종료
			sellList.clear();
			buyList.clear();
			pc.setPrivateShop(false);
			pc.상점변신 = 0;
			pc.sendPackets(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Idle));
			pc.broadcastPacket(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Idle));
			L1PolyMorph.undoPolyPrivateShop(pc);
		}
		
	}

	private static HashMap<String, Integer> 상점개설_계정횟수 = new HashMap<String, Integer>();

	public static boolean get상점개설계정횟수(String account) {
		synchronized (상점개설_계정횟수) {
			int time = 0;
			try {
				time = 상점개설_계정횟수.get(account);
			} catch (Exception e) {
			}
			if (time >= 50)
				return false;
			상점개설_계정횟수.put(account, time++);
			return true;
		}
	}

	public static void reset상점개설계정횟수() {
		synchronized (상점개설_계정횟수) {
			상점개설_계정횟수.clear();
		}
	}
	
	@Override
	public String getType() {
		return C_SHOP;
	}

}
