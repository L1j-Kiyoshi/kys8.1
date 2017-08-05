package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1ItemInstance;

public class S_IdentifyDesc extends ServerBasePacket {

    private byte[] _byte = null;

    /**
     * 確認スクロール使用時のメッセージを表示する
     */
    public S_IdentifyDesc(L1ItemInstance item) {
        buildPacket(item);
    }

    private void buildPacket(L1ItemInstance item) {
        writeC(Opcodes.S_IDENTIFY_CODE);
        writeH(item.getItem().getItemDescId());

        StringBuilder name = new StringBuilder();

        if (item.getBless() == 0) {
            name.append("$227 "); // 祝福された
        } else if (item.getBless() == 2) {
            name.append("$228 "); // 呪いになった
        }

        name.append(item.getItem().getNameId());

        if (item.getItem().getType2() == 1) { // weapon
            writeH(134); // \f1%0：小さなmonster打撃％1大monster打撃％2
            writeC(3);
            writeS(name.toString());
            writeS(item.getItem().getDmgSmall() + "+" + item.getEnchantLevel());
            writeS(item.getItem().getDmgLarge() + "+" + item.getEnchantLevel());

        } else if (item.getItem().getType2() == 2) { // armor
            if (item.getItem().getItemId() == 20383) { // 騎馬用ヘルム
                writeH(137); // \f1%0：使用可能回数％1 [重量％2]
                writeC(3);
                writeS(name.toString());
                writeS(String.valueOf(item.getChargeCount()));
            } else {
                writeH(135); // \f1%0：防御力％1防御ツール
                writeC(2);
                writeS(name.toString());
                writeS(Math.abs(item.getItem().get_ac()) + "+" + item.getEnchantLevel());
            }

        } else if (item.getItem().getType2() == 0) { // etcitem
            if (item.getItem().getType() == 1) { // wand
                writeH(137); // \f1%0：使用可能回数％1 [重量％2]
                writeC(3);
                writeS(name.toString());
                writeS(String.valueOf(item.getChargeCount()));
            } else if (item.getItem().getType() == 2) {
                writeH(138);
                writeC(2);
                name.append(": $231 "); // 残りの燃料
                name.append(String.valueOf(item.getRemainingTime()));
                writeS(name.toString());
            } else if (item.getItem().getType() == 7) { // food
                writeH(136);  // \f1%0：満腹度％1 [重量％2]
                writeC(3);
                writeS(name.toString());
                writeS(String.valueOf(item.getItem().getFoodVolume()));
            } else {
                writeH(138); // \f1%0：［重量%1］
                writeC(2);
                writeS(name.toString());
            }
            writeS(String.valueOf(item.getWeight()));
        }
    }

    @Override
    public byte[] getContent() {
        if (_byte == null) {
            _byte = getBytes();
        }
        return _byte;
    }
}
