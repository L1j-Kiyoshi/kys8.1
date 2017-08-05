package l1j.server.server.model.item.function;

import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class omanTel {

    public static void clickItem(L1PcInstance pc, int itemId, L1ItemInstance l1iteminstance) {
        L1Location newLocation = pc.getLocation().randomLocation(200, true);
        int newX = newLocation.getX();
        int newY = newLocation.getY();
        short mapId = (short) newLocation.getMapId();
        if (pc.get_DuelLine() != 0) {
            pc.sendPackets(new S_SystemMessage("バトルゾーン地域で使用することができません。"));
            return;
        }
        if (pc.isParalyzed() || pc.isSleeped() || pc.isDead()) {
            return;
        }
        if (!pc.getMap().isEscapable()) {
            return;
        }
        if (pc.isDead())
            return;
        if (pc.getCurrentHp() < 1)
            return;

        switch (itemId) {
            case 830001: //傲慢の塔1階テレポートスクロール
                if (pc.getMapId() == 101) {
                    new L1Teleport().teleport(pc, newX, newY, mapId, pc.getHeading(), true);
                } else {
                    new L1Teleport().teleport(pc, 32735, 32798, (short) 101, pc.getHeading(), true);
                }
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
            case 830002: //傲慢の塔2階テレポートスクロール
                if (pc.getMapId() == 102) {
                    new L1Teleport().teleport(pc, newX, newY, mapId, pc.getHeading(), true);
                } else {
                    new L1Teleport().teleport(pc, 32726, 32803, (short) 102, pc.getHeading(), true);
                }
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
            case 830003: //傲慢の塔3階テレポートスクロール
                if (pc.getMapId() == 103) {
                    new L1Teleport().teleport(pc, newX, newY, mapId, pc.getHeading(), true);
                } else {
                    new L1Teleport().teleport(pc, 32726, 32803, (short) 103, pc.getHeading(), true);
                }
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
            case 830004: //傲慢の塔4階テレポートスクロール
                if (pc.getMapId() == 104) {
                    new L1Teleport().teleport(pc, newX, newY, mapId, pc.getHeading(), true);

                } else {
                    new L1Teleport().teleport(pc, 32613, 32863, (short) 104, pc.getHeading(), true);
                }
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
            case 830005: //傲慢の塔5階テレポートスクロール
                if (pc.getMapId() == 105) {
                    new L1Teleport().teleport(pc, newX, newY, mapId, pc.getHeading(), true);

                } else {
                    new L1Teleport().teleport(pc, 32597, 32867, (short) 105, pc.getHeading(), true);
                }
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
            case 830006: //傲慢の塔6階テレポートスクロール
                if (pc.getMapId() == 106) {
                    new L1Teleport().teleport(pc, newX, newY, mapId, pc.getHeading(), true);

                } else {
                    new L1Teleport().teleport(pc, 32607, 32865, (short) 106, pc.getHeading(), true);
                }
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
            case 830007: //傲慢の塔7階テレポートスクロール
                if (pc.getMapId() == 107) {
                    new L1Teleport().teleport(pc, newX, newY, mapId, pc.getHeading(), true);

                } else {
                    new L1Teleport().teleport(pc, 32618, 32866, (short) 107, pc.getHeading(), true);
                }
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
            case 830008: //傲慢の塔8階テレポートスクロール
                if (pc.getMapId() == 108) {
                    new L1Teleport().teleport(pc, newX, newY, mapId, pc.getHeading(), true);

                } else {
                    new L1Teleport().teleport(pc, 32598, 32867, (short) 108, pc.getHeading(), true);
                }
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
            case 830009: //傲慢の塔9階テレポートスクロール
                if (pc.getMapId() == 109) {
                    new L1Teleport().teleport(pc, newX, newY, mapId, pc.getHeading(), true);

                } else {
                    new L1Teleport().teleport(pc, 32609, 32866, (short) 109, pc.getHeading(), true);
                }
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
            case 830010: //傲慢の塔10階テレポートスクロール
                if (pc.getMapId() == 110) {
                    new L1Teleport().teleport(pc, newX, newY, mapId, pc.getHeading(), true);

                } else {
                    new L1Teleport().teleport(pc, 32726, 32803, (short) 110, pc.getHeading(), true);
                }
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
            case 830011: //傲慢の塔頂上テレポートスクロール
                if (pc.getMapId() == 200) {
                    new L1Teleport().teleport(pc, newX, newY, mapId, pc.getHeading(), true);

                } else {
                    new L1Teleport().teleport(pc, 32657, 32797, (short) 111, pc.getHeading(), true);
                }
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
        }
    }
}
