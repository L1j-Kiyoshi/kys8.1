package l1j.server.server.serverpackets;

import java.io.IOException;

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Warehouse.SpecialWarehouse;
import l1j.server.server.model.Warehouse.WarehouseManager;


public class S_RetrieveSpecialList extends ServerBasePacket {
	public boolean NonValue = false;
	public S_RetrieveSpecialList(int objid, L1PcInstance pc) {
		if (pc.getInventory().getSize() < 180) {
			SpecialWarehouse w = WarehouseManager.getInstance().getSpecialWarehouse(pc.getName());
			if (w == null){
				return;
			}
			int size = w.getSize();
			if (size > 0) {
				writeC(Opcodes.S_RETRIEVE_LIST);
				writeD(objid);
				writeH(size);
				writeC(17);
				L1ItemInstance item = null;
				for (Object itemObject : w.getItems()) {
					item = (L1ItemInstance) itemObject;
					writeD(item.getId());
					writeC(item.getItem().getType2());
					writeH(item.get_gfxid());
					writeC(item.getBless());
					writeD(item.getCount());
					writeC(item.isIdentified() ? 1 : 0);
					if (!item.isIdentified()) {
						writeC(0);
					} else {
						byte[] status = item.getStatusBytes();
						writeC(status.length);
						for (byte b : status) {
							writeC(b);
						}
					}
					writeS(item.getViewName());
				}
				writeD(pc.get_SpecialSize());//추가
				writeD(60);
			} else {
				this.NonValue = true;
			}
		} else {
			pc.sendPackets(new S_ServerMessage(263)); // \f1한사람의 캐릭터가 가지고 걸을 수 있는 아이템은 최대 180개까지입니다.
		}
	}

	@Override
	public byte[] getContent() throws IOException {
		return getBytes();
	}
}
