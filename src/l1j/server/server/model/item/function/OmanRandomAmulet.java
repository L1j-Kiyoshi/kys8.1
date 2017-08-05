package l1j.server.server.model.item.function;

import java.util.Random;

import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;

public class OmanRandomAmulet {
    private static Random _random = new Random(System.nanoTime());

    public static void clickItem(L1PcInstance pc, int itemId, L1ItemInstance l1iteminstance) {
        int chance = _random.nextInt(100) + 1;
        switch (itemId) {
            case 830042: //混沌の傲慢の塔1階テレポートアミュレット
            case 830052: //変異された傲慢の塔1階テレポートアミュレット
                if (chance <= 35) {
                    supply(pc, 830022, 1, 0);
                } else {
                    supply(pc, 830012, 1, 0);
                }
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
            case 830043: //混沌の傲慢の塔2階テレポートアミュレット
            case 830053: //変異された傲慢の塔2階テレポートアミュレット
                if (chance <= 35) {
                    supply(pc, 830023, 1, 0);
                } else {
                    supply(pc, 830013, 1, 0);
                }
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
            case 830044: //混沌の傲慢の塔3階テレポートアミュレット
            case 830054: //変異された傲慢の塔3階テレポートアミュレット
                if (chance <= 35) {
                    supply(pc, 830024, 1, 0);
                } else {
                    supply(pc, 830014, 1, 0);
                }
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
            case 830045: //混沌の傲慢の塔4階テレポートアミュレット
            case 830055: //変異された傲慢の塔4階テレポートアミュレット
                if (chance <= 35) {
                    supply(pc, 830025, 1, 0);
                } else {
                    supply(pc, 830015, 1, 0);
                }
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
            case 830046: //混沌の傲慢の塔5階テレポートアミュレット
            case 830056: //変異された傲慢の塔5階テレポートアミュレット
                if (chance <= 35) {
                    supply(pc, 830026, 1, 0);
                } else {
                    supply(pc, 830016, 1, 0);
                }
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
            case 830047: //混沌の傲慢の塔6階テレポートアミュレット
            case 830057: //変異された傲慢の塔6階テレポートアミュレット
                if (chance <= 35) {
                    supply(pc, 830027, 1, 0);
                } else {
                    supply(pc, 830017, 1, 0);
                }
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
            case 830048: //混沌の傲慢の塔7階テレポートアミュレット
            case 830058: //変異された傲慢の塔7階テレポートアミュレット
                if (chance <= 35) {
                    supply(pc, 830028, 1, 0);
                } else {
                    supply(pc, 830018, 1, 0);
                }
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
            case 830049: //混沌の傲慢の塔8階テレポートアミュレット
            case 830059: //変異された傲慢の塔8階テレポートアミュレット
                if (chance <= 35) {
                    supply(pc, 830029, 1, 0);
                } else {
                    supply(pc, 830019, 1, 0);
                }
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
            case 830050: //混沌の傲慢の塔9階テレポートアミュレット
            case 830060: //変異された傲慢の塔9階テレポートアミュレット
                if (chance <= 35) {
                    supply(pc, 830030, 1, 0);
                } else {
                    supply(pc, 830020, 1, 0);
                }
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
            case 830051: //混沌の傲慢の塔10階テレポートアミュレット
            case 830061: //変異された傲慢の塔10階テレポートアミュレット
                if (chance <= 35) {
                    supply(pc, 830031, 1, 0);
                } else {
                    supply(pc, 830021, 1, 0);
                }
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;

        }
    }

    private static boolean supply(L1PcInstance pc, int item_id, int count, int EnchantLevel) {
        L1ItemInstance item = ItemTable.getInstance().createItem(item_id);
        if (item != null) {
            item.setCount(count);
            item.setEnchantLevel(EnchantLevel);
            item.setIdentified(true);
            if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
                pc.getInventory().storeItem(item);
            } else {
                pc.sendPackets(new S_ServerMessage(82));
                // 重量ゲージが不足したり、インベントリがいっぱいよりにできません。
                return false;
            }
            pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
            return true;
        } else {
            return false;
        }
    }
}
