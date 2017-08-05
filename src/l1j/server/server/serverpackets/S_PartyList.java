package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;

public class S_PartyList extends ServerBasePacket {
    private static final String _TYPE = "[S] S_PartyList";
    private byte[] _byte = null;

    public S_PartyList(L1PcInstance cha) {
        double nowhp = 0.0d;
        double maxhp = 0.0d;
        writeC(Opcodes.S_EVENT);
        writeC(0x68);
        if (cha.getParty() == null) {
            writeC(0x00);
        } else {
            L1PcInstance leader = cha.getParty().getLeader();
            L1PcInstance temp[] = cha.getParty().getMembers();

            writeC(temp.length - 1);
            writeD(leader.getId());
            writeS(leader.getName());
            nowhp = leader.getCurrentHp();
            maxhp = leader.getMaxHp();
            writeC((int) ((nowhp / maxhp) * 100));
            writeD(leader.getMapId());
            writeH(leader.getX());
            writeH(leader.getY());
            for (int i = 0; i < temp.length; i++) {
                if (cha.getId() == temp[i].getId() || leader.getId() == temp[i].getId()) continue;
                nowhp = temp[i].getCurrentHp();
                maxhp = temp[i].getMaxHp();
                writeD(temp[i].getId());
                writeS(temp[i].getName());
                writeC((int) ((nowhp / maxhp) * 100));
                writeD(temp[i].getMapId());
                writeH(temp[i].getX());
                writeH(temp[i].getY());
                writeC(0x00);
            }
        }
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
