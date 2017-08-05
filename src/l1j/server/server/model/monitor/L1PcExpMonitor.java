package l1j.server.server.model.monitor;

import l1j.server.Config;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Lawful;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_SPMR;

public class L1PcExpMonitor extends L1PcMonitor {

    private int _old_lawful;

    private int _old_exp;

    public L1PcExpMonitor(int oId) {
        super(oId);
    }

    @Override
    public void execTask(L1PcInstance pc) {

        if (_old_lawful != pc.getLawful()) {
            _old_lawful = pc.getLawful();
            S_Lawful s_lawful = new S_Lawful(pc.getId(), _old_lawful);
            pc.sendPackets(s_lawful);
            pc.broadcastPacket(s_lawful);
            //LawfulBonus(pc);//バポシステム
        }

        if (_old_exp != pc.getExp()) {
            _old_exp = pc.getExp();
            pc.onChangeExp();
        }
    }

    private void LawfulBonus(L1PcInstance pc) {
        int ACvalue = 0;
        int MRvalue = 0;
        int SPvalue = 0;
        int ATvalue = 0;
        int bapo = 0;
        if (pc.getLawful() >= 30000 && pc.getLawful() <= 32768) {
            ACvalue = -6;
            MRvalue = 9;
            pc.setOBapoLevel(pc.getNBapoLevel());
            bapo = 2;
            pc.setNBapoLevel(bapo);
        } else if (pc.getLawful() >= 20000 && pc.getLawful() <= 29999) {
            ACvalue = -4;
            MRvalue = 6;
            pc.setOBapoLevel(pc.getNBapoLevel());
            bapo = 1;
            pc.setNBapoLevel(bapo);
        } else if (pc.getLawful() >= 10000 && pc.getLawful() <= 19999) {
            ACvalue = -2;
            MRvalue = 3;
            pc.setOBapoLevel(pc.getNBapoLevel());
            bapo = 0;
            pc.setNBapoLevel(bapo);
        } else if (pc.getLawful() >= -9999 && pc.getLawful() <= 9999) {
            SPvalue = 0;
            ATvalue = 0;
            ACvalue = 0;
            MRvalue = 0;
            pc.setOBapoLevel(pc.getNBapoLevel());
            bapo = 7;
            pc.setNBapoLevel(bapo);
        } else if (pc.getLawful() <= -10000 && pc.getLawful() >= -19999) {
            SPvalue = 1;
            ATvalue = 1;
            pc.setOBapoLevel(pc.getNBapoLevel());
            bapo = 3;
            pc.setNBapoLevel(bapo);
        } else if (pc.getLawful() <= -20000 && pc.getLawful() >= -29999) {
            SPvalue = 2;
            ATvalue = 3;
            pc.setOBapoLevel(pc.getNBapoLevel());
            bapo = 4;
            pc.setNBapoLevel(bapo);
        } else if (pc.getLawful() <= -30000 && pc.getLawful() >= -32768) {
            SPvalue = 3;
            ATvalue = 5;
            pc.setOBapoLevel(pc.getNBapoLevel());
            bapo = 5;
            pc.setNBapoLevel(bapo);
        }

        if (pc.getOBapoLevel() != pc.getNBapoLevel()) {
            pc.sendPackets(new S_PacketBox(S_PacketBox.BAPO, pc.getOBapoLevel(), false));
            pc.sendPackets(new S_PacketBox(S_PacketBox.BAPO, pc.getNBapoLevel(), true));
            pc.setOBapoLevel(pc.getNBapoLevel());
            if (pc.getLevel() < Config.NEW_PLAYER) {
                pc.sendPackets(new S_PacketBox(S_PacketBox.BAPO, 6, true));
            } else {
                pc.sendPackets(new S_PacketBox(S_PacketBox.BAPO, 6, false));
            }
        }

        if (ACvalue != 0 && MRvalue != 0) {
            if (ACvalue != pc.LawfulAC) {
                if (pc.LawfulAC != 0) {
                    pc.getAC().addAc(pc.LawfulAC * -1);
                }
                pc.LawfulAC = ACvalue;
                pc.getAC().addAc(ACvalue);
                pc.sendPackets(new S_OwnCharStatus(pc));
            }
            if (MRvalue != pc.LawfulMR) {
                if (pc.LawfulMR != 0) {
                    pc.getResistance().addMr(pc.LawfulMR * -1);
                }
                pc.LawfulMR = MRvalue;
                pc.getResistance().addMr(MRvalue);
                pc.sendPackets(new S_SPMR(pc));
            }
            if (SPvalue != pc.LawfulSP) {
                if (pc.LawfulSP != 0) {
                    pc.addSp(pc.LawfulSP * -1);
                }
                pc.LawfulSP = SPvalue;
                pc.addSp(SPvalue);
                pc.sendPackets(new S_SPMR(pc));
            }
        /*	if (pc.LawfulSP != 0) {
                pc.addSp(pc.LawfulSP * 1);
				pc.LawfulSP = 0;
				pc.sendPackets(new S_SPMR(pc));
			}*/
        } else {
            if (pc.LawfulAC != 0) {
                pc.getAC().addAc(pc.LawfulAC * -1);
                pc.LawfulAC = 0;
                pc.sendPackets(new S_OwnCharStatus(pc));
            }
            if (pc.LawfulMR != 0) {
                pc.getResistance().addMr(pc.LawfulMR * -1);
                pc.LawfulMR = 0;
                pc.sendPackets(new S_SPMR(pc));
            }

            if (ATvalue != 0) {
                if (pc.LawfulAT != 0) {
                    pc.setBapodmg(pc.LawfulAT * -1);
                }
                pc.LawfulAT = ATvalue;
                pc.setBapodmg(ATvalue);
                pc.sendPackets(new S_OwnCharStatus(pc));
            } else if (pc.LawfulAT != 0) {
                pc.setBapodmg(pc.LawfulAT * -1);
                pc.LawfulAT = 0;
                pc.sendPackets(new S_OwnCharStatus(pc));
            }

            if (SPvalue != 0) {
                if (pc.LawfulSP != 0) {
                    pc.addSp(pc.LawfulSP * -1);
                }
                pc.LawfulSP = SPvalue;
                pc.addSp(SPvalue);
                pc.sendPackets(new S_SPMR(pc));
            } else if (pc.LawfulSP != 0) {
                pc.addSp(pc.LawfulSP * -1);
                pc.LawfulSP = 0;
                pc.sendPackets(new S_SPMR(pc));
            }
        }
    }

}
