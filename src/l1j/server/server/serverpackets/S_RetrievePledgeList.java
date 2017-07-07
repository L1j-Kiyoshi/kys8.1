/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
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
package l1j.server.server.serverpackets;

import java.io.IOException;

import l1j.server.server.Opcodes;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Warehouse.ClanWarehouse;
import l1j.server.server.model.Warehouse.WarehouseManager;

public class S_RetrievePledgeList extends ServerBasePacket {
	public boolean NonValue = false;
	public boolean InUse = false;
	public S_RetrievePledgeList(int objid, L1PcInstance pc) {
		L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
		if (clan == null) {
			return;
		}

		ClanWarehouse clanWarehouse = WarehouseManager.getInstance().getClanWarehouse(clan.getClanName());

		if (!clanWarehouse.setWarehouseUsingChar(pc.getId(), 0)) {
			int id = clanWarehouse.getWarehouseUsingChar();

			L1Object prevUser = L1World.getInstance().findObject(id);

			if (prevUser instanceof L1PcInstance) {
				L1PcInstance usingPc = (L1PcInstance) prevUser;

				
				if (usingPc.getClan() == clan) {
					// \f1 血盟員が倉庫を使用中です。しばらく経ってから利用してください。
					pc.sendPackets(new S_ChatPacket(pc,"" + usingPc.getName() + "が現在の血盟倉庫を使用中です。"));
					InUse = true;
					return;
				}
			}
			if (!clanWarehouse.setWarehouseUsingChar(pc.getId(), id)) {
				// その間に誰挟まっ入った場合
				// \f1 血盟員が倉庫を使用中です。しばらく経ってから利用してください。
				pc.sendPackets(new S_ChatPacket(pc,"" + clanWarehouse.getName() + "が現在の血盟倉庫を使用中です。"));
				InUse = true;
				return;
			}
		}

		if (pc.getInventory().getSize() < 180) {
			int size = clanWarehouse.getSize();
			if (size > 0) {
				writeC(Opcodes.S_RETRIEVE_LIST);
				writeD(objid);
				writeH(size);
				writeC(5); // 血盟倉庫
				L1ItemInstance item = null;
				for (Object itemObject : clanWarehouse.getItems()) {
					item = (L1ItemInstance) itemObject;
					writeD(item.getId());
					writeC(item.getItem().getType2());
					writeH(item.get_gfxid());
					writeC(item.getItem().getBless());
					writeD(item.getCount());
					writeC(item.isIdentified() ? 1 : 0);
					writeS(item.getViewName());
					// by.lins
					writeC(0);
					// by.lins
				}
				writeD(30);
				writeD(0x00000000);
				writeH(0x00);
			} else {
				this.NonValue = true;
			}
		} else {
			clanWarehouse.setWarehouseUsingChar(0, 0);
			pc.sendPackets(new S_ServerMessage(263)); // \f1一人のキャラクターが持って歩くことができ
														// アイテムは、最大180個までです。
		}
	}

	@Override
	public byte[] getContent() throws IOException {
		return getBytes();
	}
}
