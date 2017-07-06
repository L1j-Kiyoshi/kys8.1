package l1j.server.server.clientpackets;

import l1j.server.server.GameClient;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Warehouse.PackageWarehouse;
import l1j.server.server.model.Warehouse.WarehouseManager;
import l1j.server.server.model.shop.L1AdenShop;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_RetrievePackageList;
import l1j.server.server.serverpackets.S_SurvivalCry;
import l1j.server.server.serverpackets.S_ArdenStore;

public class C_AdenShop extends ClientBasePacket {

	private static final String C_아덴상점 = "[C] C_아덴상점";

	public C_AdenShop(byte[] decrypt, GameClient client) {
		super(decrypt);
		try {
			int type = readH();
			L1PcInstance pc = client.getActiveChar();
			
			switch (type) {
			case 1: { // 상점 열기
			
				if (pc == null)
					return;
				pc.sendPackets(new S_ArdenStore(pc));
			}
				break;
			case 4: { // OTP 입력
				for (int i = 0; i < 1000; i++) {
					int ff = readH();
					if (ff == 0)
						break;
				}
				for (int i = 0; i < 16 * 8 + 1; i++) {
					readC();
				}
				int size = readH();
				if (size == 0)
					return;
				L1AdenShop as = new L1AdenShop();
				for (int i = 0; i < size; i++) {
					int id = readD();
					int count = readH();
					if (count <= 0 || count >= 10000) {
						return;
					}
					as.add(id, count);
				}
				if (!as.BugOk()) {
					if (as.commit(pc))
						client.sendPacket(new S_SurvivalCry(S_SurvivalCry.OTP_CHECK_MSG, pc));
				}
			}
				break;
			case 6: { // 부가서비스 창고
				PackageWarehouse w = WarehouseManager.getInstance().getPackageWarehouse(pc.getAccountName());
				if (w.getSize() != 0){
					pc.setPackegeWarehouse(true);
					pc.sendPackets(new S_RetrievePackageList(pc.getId(), pc));
				} else {   
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "noitemret")); 
				}
			}
			break;
			case 0x32: {// 동의 및 구매
				client.sendPacket(new S_SurvivalCry(S_SurvivalCry.OTP_SHOW, pc));
			}
				break;
			default:
				break;
			}
		} catch (Exception e) {
		} finally {
		}
	}

	@Override
	public String getType() {
		return C_아덴상점;
	}
}