package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;

public class S_Weight extends ServerBasePacket {

    public S_Weight(L1PcInstance pc) {
        writeC(Opcodes.S_EXTENDED_PROTOBUF);
        writeC(0xe5);
        writeC(0x01);
        writeC(0x08);// パーセント
        writeC(pc.getInventory().getWeight100());
        writeC(0x10);// 所持重量
        write7B(pc.getInventory().getWeight());
        writeC(0x18);// 最大重量
        write7B(pc.getMaxWeight());
        writeH(0);
    }

    @Override
    public byte[] getContent() {
        return _bao.toByteArray();
    }

    @Override
    public String getType() {
        return S_Weight;
    }

    private static final String S_Weight = "[S] S_Weight";
}
