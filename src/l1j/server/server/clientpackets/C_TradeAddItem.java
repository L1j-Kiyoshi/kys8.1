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
/** ログを残す **/
//Referenced classes of package l1j.server.server.clientpackets:
//ClientBasePacket

public class C_TradeAddItem extends ClientBasePacket {
	private static final String C_TRADE_ADD_ITEM = "[C] C_TradeAddItem";

	/** 日付、時刻の記録 **/
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
		/** バグ防止 **/
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
		if (itemcount > 2000000000)  {  // コピーのバグを防ぐ
			return;
		}
		/** バグ防止 **/
		
		if (item.getItemId() == L1ItemId.HIGH_CHARACTER_TRADE || item.getItemId() == L1ItemId.LOW_CHARACTER_TRADE) {
			if (!pc.isQuizValidated()) {
				pc.sendPackets(new S_ChatPacket(pc, "クイズの認証をしていません。"));
				pc.sendPackets(new S_ChatPacket(pc, "まず[。クイズ認証]で、クイズの認証後、取引をしようとしてください。"));
				return;
			}
			if (pc.getLevel() >= 70 && item.getItemId() == L1ItemId.LOW_CHARACTER_TRADE) {
				pc.sendPackets(new S_ChatPacket(pc, "70レベル以上は上級のキャラクターの交換スクロールを使用する必要があります。"));
				return;
			} else if (pc.getLevel() < 70 && item.getItemId() == L1ItemId.HIGH_CHARACTER_TRADE) {
				pc.sendPackets(new S_ChatPacket(pc, "70レベル未満は下級キャラクター交換スクロールを使用する必要があります。"));
				return;
			}
		}
		
		//交換不可アイテムディビ連動NoTradable
		int itemId = item.getItem().getItemId();
		if (!pc.isGm() && NoTradable.getInstance().isNoTradable(itemId))  {// 
			pc.sendPackets(new S_SystemMessage("\\aG[!] : このアイテムは交換できません。"));
			return;
		}

		if (!item.getItem().isTradable()) {
			pc.sendPackets(new S_ServerMessage(210, item.getItem().getName())); // \f1%0はしまったり、または他人に両日をすることができません。
			return;
		}
		if(item.getBless() >= 128){
			pc.sendPackets(new S_ServerMessage(210, item.getItem().getName())); // \f1％0はたりまたは他人に両日をすることができません。
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
					// \f1%0はしまったり、または他人に両日をすることができません。
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
			pc.sendPackets(new S_SystemMessage("上げる不可能：一方が完了を押したまま"));
			tradingPartner.sendPackets(new S_SystemMessage("上げる不可能：一方が完了を押したまま"));
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
