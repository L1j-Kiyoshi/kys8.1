package l1j.server.server.monitor;

import java.io.IOException;

import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;

public interface Logger {
    public enum ChatType {
        Normal, Global, Clan, Alliance, Guardian, Party, Group, Shouting
        /** Trade 商売チャットログ残さないように変更 */
    }

    ;

    public enum ItemActionType {
        Pickup, Drop, Delete, del
        /** AutoLoot オートルーティングログ記録残さないように変更 */
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

    /**
     * 取引の成功時のログ記録を残す
     */
    public void addEnchant(L1PcInstance pc, L1ItemInstance item, boolean success);

    public void addAll(String msg);

    public void addItemAction(ItemActionType type, L1PcInstance pc, L1ItemInstance item, int count);

    /**
     * 78レベルからレベルアップの場合levellog記録
     */
    public void addLevel(L1PcInstance pc, int level);

    public void flush() throws IOException;
}
