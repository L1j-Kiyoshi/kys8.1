package l1j.server.server.clientpackets;

import l1j.server.server.GameClient;
import l1j.server.server.Controller.WarTimeController;
import l1j.server.server.datatables.CastleTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Castle;


public class C_Drawal extends ClientBasePacket {

	private static final String C_DRAWAL = "[C] C_Drawal";

	public C_Drawal(byte abyte0[], GameClient clientthread) throws Exception {
		super(abyte0);
		try {
			@SuppressWarnings("unused")
			int i = readD();
			int j = readD();

			L1PcInstance pc = clientthread.getActiveChar();
			L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
			if (clan != null) {
				int castle_id = clan.getCastleId();
				if (castle_id != 0) {
					if (WarTimeController.getInstance().isNowWar(clan.getCastleId())) {
						S_SystemMessage sm = new S_SystemMessage("警告：攻城中の税金を見つけることができません。");
						pc.sendPackets(sm, true);
						return;
					}
					if (pc.getClanRank() != L1Clan.군주 || !pc.isCrown() || pc.getId() != pc.getClan().getLeaderId())
						return;

					L1Castle l1castle = CastleTable.getInstance().getCastleTable(castle_id);

					int money = l1castle.getPublicMoney();
					long _money = money;

					if (_money <= 0 || money < j) {//バグ防止
						return;
					}
					money -= j;
					L1ItemInstance item = ItemTable.getInstance().createItem(L1ItemId.ADENA);
					
					if (item != null) {
						l1castle.setPublicMoney(money);
						CastleTable.getInstance().updateCastle(l1castle);
						if (pc.getInventory().checkAddItem(item, j) == L1Inventory.OK) {
							pc.getInventory().storeItem(L1ItemId.ADENA, j);
						} else {
							L1World.getInstance().getInventory(pc.getX(), pc.getY(),pc.getMapId()).storeItem(L1ItemId.ADENA, j);
						}
//						pc.sendPackets(new S_SystemMessage("公金 "+ j +"アデナを引き出しました。 "））;
						S_ServerMessage sm = new S_ServerMessage(143, "$457","$4" + " (" + j + ")");
						pc.sendPackets(sm, true);
					}
				}
			}
		} catch (Exception e) {

		} finally {
			clear();
		}
	}

	@Override
	public String getType() {
		return C_DRAWAL;
	}
}
