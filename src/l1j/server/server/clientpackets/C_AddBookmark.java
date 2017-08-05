package l1j.server.server.clientpackets;

import l1j.server.server.GameClient;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1HouseLocation;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.templates.L1BookMark;

public class C_AddBookmark extends ClientBasePacket {

    private static final String C_ADD_BOOKMARK = "[C] C_AddBookmark";

    public C_AddBookmark(byte[] decrypt, GameClient client) {
        super(decrypt);
        String s = readS();
        L1PcInstance pc = client.getActiveChar();
        if (pc == null || pc.isGhost()) {
            return;
        }
        if (pc.getMap().isMarkable() || pc.isGm()) {
            if ((L1CastleLocation.checkInAllWarArea(pc.getX(), pc.getY(), pc.getMapId())
                    || L1HouseLocation.isInHouse(pc.getX(), pc.getY(), pc.getMapId()))
                    || ((pc.getX() >= 33514 && pc.getX() <= 33809) && (pc.getY() >= 32216 && pc.getY() <= 32457) && pc.getMapId() == 4)
                    || ((pc.getX() >= 34280 && pc.getX() <= 34287) && (pc.getY() >= 33103 && pc.getY() <= 33492) && pc.getMapId() == 4)
                    || ((pc.getX() >= 33464 && pc.getX() <= 33532) && (pc.getY() >= 32839 && pc.getY() <= 32878) && pc.getMapId() == 4)
/*竜骨*/ || ((pc.getX() >= 33261 && pc.getX() <= 33266) && (pc.getY() >= 32397 && pc.getY() <= 32405) && pc.getMapId() == 4)
/*竜骨*/ || ((pc.getX() >= 33331 && pc.getX() <= 33339) && (pc.getY() >= 32432 && pc.getY() <= 32440) && pc.getMapId() == 4)
/*竜骨*/ || ((pc.getX() >= 33388 && pc.getX() <= 33396) && (pc.getY() >= 32340 && pc.getY() <= 32348) && pc.getMapId() == 4)
/*韓国民*/ || ((pc.getX() >= 33449 && pc.getX() <= 33473) && (pc.getY() >= 32324 && pc.getY() <= 32347) && pc.getMapId() == 4)
/*ワニ島*/ || ((pc.getX() >= 33470 && pc.getX() <= 33530) && (pc.getY() >= 33177 && pc.getY() <= 33231) && pc.getMapId() == 4)
/*サンドワーム*/ || ((pc.getX() >= 32707 && pc.getX() <= 32826) && (pc.getY() >= 33117 && pc.getY() <= 33229) && pc.getMapId() == 4)
/*政務*/ || (pc.getMapId() == 430) && !pc.isGm()) {
                // \f1ここを記憶することができません。
                pc.sendPackets(new S_ServerMessage(214));
            } else if (!pc.getMap().isMarkable() && pc.isGm()) {
                L1BookMark.addBookmark(pc, s);
            } else {
                L1BookMark.addBookmark(pc, s);
            }
        } else { // \f1ここを記憶することができません。
            pc.sendPackets(new S_ServerMessage(214));
        }
    }

    @Override
    public String getType() {
        return C_ADD_BOOKMARK;
    }
}
