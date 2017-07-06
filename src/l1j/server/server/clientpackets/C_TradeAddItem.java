/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be trading_partnerful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */

package l1j.server.server.clientpackets;

import java.util.Calendar;

import l1j.server.server.GameClient;
import l1j.server.server.datatables.NoTradable;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Trade;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
/** 로그 남기기 **/
//Referenced classes of package l1j.server.server.clientpackets:
//ClientBasePacket

public class C_TradeAddItem extends ClientBasePacket {
	private static final String C_TRADE_ADD_ITEM = "[C] C_TradeAddItem";

	/** 날짜 , 시간 기록 **/
	Calendar rightNow = Calendar.getInstance();
	int day = rightNow.get(Calendar.DATE);
	int hour = rightNow.get(Calendar.HOUR);
	int min = rightNow.get(Calendar.MINUTE);
	int year = rightNow.get(Calendar.YEAR);
	int month =  rightNow.get(Calendar.MONTH)+1;
	String totime = "[" + year + ":" + month + ":" + day + ":" + hour +":"+min+"]";	
	
	public C_TradeAddItem(byte abyte0[], GameClient client)
	throws Exception {
		super(abyte0);

		int itemid = readD();
		int itemcount = readD();
		L1PcInstance pc = client.getActiveChar();
		if ( pc == null)return;
		L1Trade trade = new L1Trade();	
		L1ItemInstance item = pc.getInventory().getItem(itemid);
		if ( item == null) return; 
			
		System.out.println("ITEMID  "+itemid+"itemcount : "+itemcount);
		/** 버그 방지 **/
		if (itemid != item.getId()) {
			return;
		}
		if (!item.isStackable() && itemcount != 1) {
			return;
		}
		if (itemcount <= 0 || item.getCount() <= 0) {
			return;
		}
		if (itemcount > item.getCount()) {
			itemcount = item.getCount();
		}
		if (itemcount > 2000000000)  {  // 복사 버그 방지
			return;
		}
		/** 버그 방지 **/
		
		if (item.getItemId() == L1ItemId.HIGH_CHARACTER_TRADE || item.getItemId() == L1ItemId.LOW_CHARACTER_TRADE) {
			if (!pc.isQuizValidated()) {
				pc.sendPackets(new S_ChatPacket(pc, "퀴즈 인증을 하지 않으셨습니다."));
				pc.sendPackets(new S_ChatPacket(pc, "먼저 [.퀴즈인증]으로 퀴즈 인증 후 거래를 시도해주세요."));
				return;
			}
			if (pc.getLevel() >= 70 && item.getItemId() == L1ItemId.LOW_CHARACTER_TRADE) {
				pc.sendPackets(new S_ChatPacket(pc, "70레벨 이상은 상급 캐릭터교환주문서를 사용하셔야 합니다."));
				return;
			} else if (pc.getLevel() < 70 && item.getItemId() == L1ItemId.HIGH_CHARACTER_TRADE) {
				pc.sendPackets(new S_ChatPacket(pc, "70레벨 미만은 하급 캐릭터교환주문서를 사용하셔야 합니다."));
				return;
			}
		}
		
		//교환불가아이템 디비연동 NoTradable
		int itemId = item.getItem().getItemId();
		if (!pc.isGm() && NoTradable.getInstance().isNoTradable(itemId))  {// 
			pc.sendPackets(new S_SystemMessage("\\aG[!] : 해당 아이템은 교환 불가능합니다."));
			return;
		}

		if (!item.getItem().isTradable()) {
			pc.sendPackets(new S_ServerMessage(210, item.getItem().getName())); // \f1%0은 버리거나 또는 타인에게 양일을 할 수 없습니다.
			return;
		}
		if(item.getBless() >= 128){
			pc.sendPackets(new S_ServerMessage(210, item.getItem().getName())); // \f1%0은 버리거나 또는 타인에게 양일을 할 수 없습니다.
			return;
		}
		if(item.isEquipped()){
			pc.sendPackets(new S_ServerMessage(906)); // 
			return;
		}
		
		Object[] petlist = pc.getPetList().values().toArray();
		L1PetInstance pet = null;
		for (Object petObject : petlist) {
			if (petObject instanceof L1PetInstance) {
				pet = (L1PetInstance) petObject;
				if (item.getId() == pet.getItemObjId()) {
					// \f1%0은 버리거나 또는 타인에게 양일을 할 수 없습니다.
					pc.sendPackets(new S_ServerMessage(210, item.getItem().getName()));
					return;
				}
			}
		}
		
		for (Object dollObject : pc.getDollList()) {	
			if (dollObject instanceof L1DollInstance) {
				L1DollInstance doll = (L1DollInstance) dollObject;
				if (item.getId() == doll.getItemObjId()) {
					pc.sendPackets(new S_ServerMessage(210, item.getItem().getName()));
					return;
				}
			}
		}
		L1PcInstance tradingPartner = (L1PcInstance) L1World.getInstance().findObject(pc.getTradeID());
		if (tradingPartner == null) {
			if(pc.isGambleReady()){
				trade.TradeAddItem(pc, itemid, itemcount);
			}else if(pc.isNpcSell){
				trade.TradeAddItem(pc, itemid, itemcount);
			}
			return;
		}
		if (pc.getTradeOk() || tradingPartner.getTradeOk()) { 
			pc.sendPackets(new S_SystemMessage("올리기 불가능 : 한쪽이 완료를 누른 상태"));
			tradingPartner.sendPackets(new S_SystemMessage("올리기 불가능 : 한쪽이 완료를 누른 상태"));
			return;
		}
		if (tradingPartner.getInventory().checkAddItem(item, itemcount) != L1Inventory.OK) {
			tradingPartner.sendPackets(new S_ServerMessage(270));
			pc.sendPackets(new S_ServerMessage(271));
			return;
		}
		trade.TradeAddItem(pc, itemid, itemcount);
	}

	@Override
	public String getType() {
		return C_TRADE_ADD_ITEM;
	}
}
