
package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;

public class S_HorunOK extends ServerBasePacket {
    public S_HorunOK(int type, L1PcInstance pc) {
        writeC(Opcodes.S_EXCHANGEABLE_SPELL_LIST);
        writeC(type);
        writeC(0);
        writeC(0);
        writeC(0);
        writeC(0);
        writeC(0);
        writeC(0);
    }

    @Override
    public byte[] getContent() {
        return getBytes();
    }

    @Override
    public String getType() {
        return _S__1B_HORUNOK;
    }

    private static final String _S__1B_HORUNOK = "[S] S_HorunOK";
}
