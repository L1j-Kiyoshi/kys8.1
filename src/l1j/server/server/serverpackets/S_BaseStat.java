package l1j.server.server.serverpackets;

import l1j.server.server.model.Instance.L1PcInstance;

public class S_BaseStat extends ServerBasePacket {
    private static final String _TYPE = "[S] S_BaseStat";

    private byte[] _byte = null;
    private static final int[][] baseStat = {{13, 10, 10, 11, 10, 13}, {16, 12, 14, 9, 8, 12},
            {11, 12, 12, 12, 12, 9}, {8, 7, 12, 12, 12, 8}, {12, 15, 8, 10, 11, 9}, {13, 11, 14, 12, 11, 8},
            {11, 10, 12, 12, 12, 8}};

    public S_BaseStat(L1PcInstance cha) {
//		writeC(Opcodes.S_BASESTAT);
        writeC(0x04);

        int value = cha.getAbility().getBaseCha() - baseStat[cha.getType()][5];
        value *= 16;
        value += cha.getAbility().getBaseCon() - baseStat[cha.getType()][2];
        value *= 16;
        value += cha.getAbility().getBaseDex() - baseStat[cha.getType()][1];
        value *= 16;
        value += cha.getAbility().getBaseWis() - baseStat[cha.getType()][3];
        value *= 16;
        value += cha.getAbility().getBaseInt() - baseStat[cha.getType()][4];
        value *= 16;
        value += cha.getAbility().getBaseStr() - baseStat[cha.getType()][0];
        writeD(value);
    }

    @Override
    public byte[] getContent() {
        if (_byte == null) {
            _byte = _bao.toByteArray();
        }
        return _byte;
    }

    @Override
    public String getType() {
        return _TYPE;
    }
}
