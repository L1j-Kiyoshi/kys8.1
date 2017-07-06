package l1j.server.server.monitor;

import java.io.IOException;

import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;

public interface Logger {
	public enum ChatType {
		Normal, Global, Clan, Alliance, Guardian, Party, Group, Shouting
		/** Trade 장사채팅 로그 기록 남기지 않게 변경 */
	}

	;

	public enum ItemActionType {
		Pickup, Drop, Delete ,del
		/** AutoLoot 오토루팅 로그 기록 남기지 않게 변경 */
	}

	;

	public enum WarehouseType {
		Private, Clan, Package, Elf
	}

	;

	public void addChat(ChatType type, L1PcInstance pc, String msg);

	public void addWhisper(L1PcInstance pcfrom, L1PcInstance pcto, String msg);

	public void addCommand(String msg);

	public void addConnection(String msg);

	public void addWarehouse(WarehouseType type, boolean put, L1PcInstance pc, L1ItemInstance item, int count);

	public void addTrade(boolean success, L1PcInstance pcfrom, L1PcInstance pcto, L1ItemInstance item, int count);

	/** 거래 성공시 로그 기록 남기기 */
	public void addEnchant(L1PcInstance pc, L1ItemInstance item, boolean success);

	public void addAll(String msg);

	public void addItemAction(ItemActionType type, L1PcInstance pc, L1ItemInstance item, int count);

	/** 78레벨 부터 레벨업할 경우 levellog 기록 */
	public void addLevel(L1PcInstance pc, int level);

	public void flush() throws IOException;
}
