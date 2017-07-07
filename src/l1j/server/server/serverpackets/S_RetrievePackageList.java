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
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Warehouse.PackageWarehouse;
import l1j.server.server.model.Warehouse.WarehouseManager;

public class S_RetrievePackageList extends ServerBasePacket {

	private static final String _S_RetrievePackageList = "[S] S_RetrievePackageList";

	public boolean NonValue = false;

	public S_RetrievePackageList(int objid, L1PcInstance pc) {
		if (pc.getInventory().getSize() < 180) {
			PackageWarehouse w = WarehouseManager.getInstance().getPackageWarehouse(pc.getAccountName());
			if (w == null)
				return;
			int size = w.getSize();
			if (size > 0) {
				writeC(Opcodes.S_RETRIEVE_LIST);
				writeD(objid);
				writeH(size);
				writeC(3);
				//writeC(12); // 6 : 無反応7：ティング8：妖精倉庫任せる9：妖精検索15：パッケージ店
				L1ItemInstance item = null;
				for (Object itemObject : w.getItems()) {
					item = (L1ItemInstance) itemObject;
					writeD(item.getId());
					writeC(item.getItem().getType2());
					writeH(item.get_gfxid());
					writeC(item.getBless());
					writeD(item.getCount());
					writeC(item.isIdentified() ? 1 : 0);
					writeS(item.getViewName());
					// by.lins
					writeC(0);
					// by.lins					
				}
				writeH(0x001e);
				writeD(0x00);
				writeH(0x00);
				writeH(0x08);
			} else
				NonValue = true;
		} else {
			pc.sendPackets(new S_ServerMessage(263)); // \f1一人のキャラクターが持って歩くことができるアイテムは最大180個までです。
		}
	}

	@Override
	public byte[] getContent() throws IOException {
		return getBytes();
	}

	@Override
	public String getType() {
		return _S_RetrievePackageList;
	}
}
