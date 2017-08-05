package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1ItemInstance;

public class S_ItemName extends ServerBasePacket {

    private static final String S_ITEM_NAME = "[S] S_ItemName";

    /**
     * アイテムの名前を変更する。装備や強化の状態が変わったときに送る。
     */
    public S_ItemName(L1ItemInstance item) {
        if (item == null) {
            return;
        }
        // jumpを見る限り、このOpcodeはアイテム名を更新させる目的だけに使用されている形状（機器フナOE後専用？）
        // 後何かデータを続けて送ってもすべて無視されてしまう
        writeC(Opcodes.S_CHANGE_ITEM_DESC);
        writeD(item.getId());
        writeS(item.getViewName());
    }

    @Override
    public byte[] getContent() {
        return getBytes();
    }

    @Override
    public String getType() {
        return S_ITEM_NAME;
    }
}
