package l1j.server.server.Controller;

import java.util.ArrayList;

import l1j.server.server.datatables.ClanTable;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_OwnCharStatus2;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_Pledge;
import l1j.server.server.serverpackets.S_SPMR;

public class BraveavatarController implements Runnable {

    private static BraveavatarController _instance;
    private final ArrayList<L1PcInstance> _pbalist = new ArrayList<L1PcInstance>();

    public static BraveavatarController getInstance() {
        if (_instance == null) {
            _instance = new BraveavatarController();
        }
        return _instance;
    }

    public void run() {
        try {
            while (true) {
                Thread.sleep(2000);
                BraveAvata();
                Clanbuff();
            }
        } catch (Exception e) {
        }
    }

    private void Clanbuff() {
        try {
            for (L1Clan clan : L1World.getInstance().getAllClans()) {
                int bless = clan.getBless();
                int[] time = clan.getBuffTime();
                if (bless != 0) {
                    clan.setBuffTime(bless - 1, time[bless - 1] - 1);
                    if (clan.getBuffTime()[bless - 1] == 0) {
                        for (L1PcInstance member : clan.getOnlineClanMember()) {
                            member.sendPackets(new S_Pledge(clan, bless));
                            member.removeSkillEffect(bless + 504);
                        }
                        clan.setBless(0);
                        ClanTable.getInstance().updateBless(clan.getClanId(), 0);
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    public void addMember(L1PcInstance pc) {
        try {
            if (pc == null || _pbalist.contains(pc)) {
                return;
            }
        } catch (Exception e) {
        }
        _pbalist.add(pc);
    }

    public void removeMember(L1PcInstance pc) {
        try {
            if (pc == null || !_pbalist.contains(pc)) {
                return;
            }
        } catch (Exception e) {
        }
        _pbalist.remove(pc);
    }

    public boolean getMember(L1PcInstance pc) {
        return _pbalist.contains(pc);
    }

    private void BraveAvata() {
        try {
            if (_pbalist.size() > 0) {
                L1PcInstance pc = null;
                for (int i = 0; i < _pbalist.size(); i++) {
                    pc = _pbalist.get(i);
                    if (pc != null) {
                        int count = partycount(pc);
                        if (!pc.getPbavatar()) {
                            if (count >= 2) {
                                Bravestart(pc, count);
                            }
                        } else {
                            Bravecheck(pc, count);
                            if (count == 1) {
                                brave_end(pc);
                            }
                        }
                    }

                }
            }
        } catch (Exception e) {
        }
    }

    private void Bravestart(L1PcInstance pc, int count) {
        try {
            for (L1PcInstance player : L1World.getInstance().getVisiblePlayer(pc, 16)) { // 8カンヌで15軒にリニューアル
                if (pc.getParty().isMember(player)) {
                    if (count >= 2 && count <= 3) {
                        BraveState(player, 1);
                        player.setPbacount(count);
                        if (!pc.getPbavatar()) {
                            BraveState(pc, 1);
                            pc.setPbacount(count);
                        }
                    } else if (count >= 4 && count <= 6) {
                        BraveState(player, 2);
                        player.setPbacount(count);
                        if (!pc.getPbavatar()) {
                            BraveState(pc, 2);
                            pc.setPbacount(count);
                        }
                    } else if (count >= 7) {
                        BraveState(player, 3);
                        player.setPbacount(count);
                        if (!pc.getPbavatar()) {
                            BraveState(pc, 3);
                            pc.setPbacount(count);
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private void Bravecheck(L1PcInstance pc, int count) {
        try {
            for (L1PcInstance player : pc.getParty().getMembers()) {
                player.setPbavataron(false);
                for (L1PcInstance pc2 : L1World.getInstance().getVisiblePlayer(player, 8)) {
                    if (pc2.isInParty() == player.isInParty()) {
                        if (player.getParty().isLeader(pc2)) {
                            player.setPbavataron(true);
                        }
                    }
                }

                if (pc != player) {
                    if (player.getPbavataron()) {
                        if (!player.getPbavatar()) {
                            if (count >= 2 && count <= 3) {
                                BraveState(player, 1);
                                player.setPbacount(count);
                            } else if (count >= 4 && count <= 6) {
                                BraveState(player, 2);
                                player.setPbacount(count);
                            } else if (count >= 7) {
                                BraveState(player, 3);
                                player.setPbacount(count);
                            }
                        } else {
                            if (count != player.getPbacount()) {
                                if (count >= 2 && count <= 3) {
                                    brave_end(player);
                                    BraveState(player, 1);
                                    player.setPbacount(count);
                                } else if (count >= 4 && count <= 6) {
                                    brave_end(player);
                                    BraveState(player, 2);
                                    player.setPbacount(count);
                                } else if (count >= 7) {
                                    brave_end(player);
                                    BraveState(player, 3);
                                    player.setPbacount(count);
                                }
                            }
                        }
                    } else {
                        if (player.getPbavatar()) {
                            brave_end(player);
                        }
                    }
                } else {
                    if (count != player.getPbacount()) {
                        if (count >= 2 && count <= 3) {
                            brave_end(player);
                            BraveState(player, 1);
                            player.setPbacount(count);
                        } else if (count >= 4 && count <= 6) {
                            brave_end(player);
                            BraveState(player, 2);
                            player.setPbacount(count);
                        } else if (count >= 7) {
                            brave_end(player);
                            BraveState(player, 3);
                            player.setPbacount(count);
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private int partycount(L1PcInstance pc) {
        int count = 1;
        try {
            for (L1PcInstance player : L1World.getInstance().getVisiblePlayer(pc, 8)) {
                if (player == null)
                    continue;
                if (pc.getParty() == null)
                    continue;
                if (pc.getParty().isMember(player)) {
                    count += 1;
                }
            }
        } catch (Exception e) {
        }
        return count;

    }

    private void BraveState(L1PcInstance pc, int Count) {
        try {
            switch (Count) {
                case 1:
                    if (!pc.hasSkillEffect(L1SkillId.BRAVE_AVATAR_1ST)) {
                        pc.setSkillEffect(L1SkillId.BRAVE_AVATAR_1ST, 0);
                        pc.sendPackets(new S_PacketBox(S_PacketBox.NONE_TIME_ICON, 1, 479));
                        pc.getResistance().addMr(10);
                        pc.getResistance().addStun(2);
                        pc.getResistance().addHold(2);
                        pc.getAbility().addAddedInt((byte) 1);
                        pc.getAbility().addAddedDex((byte) 1);
                        pc.getAbility().addAddedStr((byte) 1);
                        pc.resetBaseMr();
                        pc.setPbavatar(true);
                        pc.sendPackets(new S_SPMR(pc));
                        pc.sendPackets(new S_OwnCharStatus2(pc));
                    }
                    break;
                case 2:
                    if (!pc.hasSkillEffect(L1SkillId.BRAVE_AVATAR_2ND)) {
                        pc.setSkillEffect(L1SkillId.BRAVE_AVATAR_2ND, 0);
                        pc.sendPackets(new S_PacketBox(S_PacketBox.NONE_TIME_ICON, 1, 479));
                        pc.getResistance().addMr(10);
                        pc.getResistance().addStun(2);
                        pc.getResistance().addHold(2);
                        pc.getAbility().addAddedInt((byte) 1);
                        pc.getAbility().addAddedDex((byte) 1);
                        pc.getAbility().addAddedStr((byte) 1);
                        pc.resetBaseMr();
                        pc.setPbavatar(true);
                        pc.sendPackets(new S_SPMR(pc));
                        pc.sendPackets(new S_OwnCharStatus2(pc));
                    }
                    break;
                case 3:
                    if (!pc.hasSkillEffect(L1SkillId.BRAVE_AVATAR_3RD)) {
                        pc.setSkillEffect(L1SkillId.BRAVE_AVATAR_3RD, 0);
                        pc.sendPackets(new S_PacketBox(S_PacketBox.NONE_TIME_ICON, 1, 479));
                        pc.getResistance().addMr(10);
                        pc.getResistance().addStun(2);
                        pc.getResistance().addHold(2);
                        pc.getAbility().addAddedInt((byte) 1);
                        pc.getAbility().addAddedDex((byte) 1);
                        pc.getAbility().addAddedStr((byte) 1);
                        pc.resetBaseMr();
                        pc.setPbavatar(true);
                        pc.sendPackets(new S_SPMR(pc));
                        pc.sendPackets(new S_OwnCharStatus2(pc));
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
        }
    }

    public void brave_end(L1PcInstance pc) {
        try {
            if (pc.hasSkillEffect(L1SkillId.BRAVE_AVATAR_1ST)) {
                pc.killSkillEffectTimer(L1SkillId.BRAVE_AVATAR_1ST);
                pc.getResistance().addMr(-10);
                pc.getResistance().addStun(-2);
                pc.getResistance().addHold(-2);
                pc.getAbility().addAddedInt((byte) -1);
                pc.getAbility().addAddedDex((byte) -1);
                pc.getAbility().addAddedStr((byte) -1);
                pc.setPbavatar(false);
                pc.setPbacount(0);
                pc.sendPackets(new S_SPMR(pc));
                pc.sendPackets(new S_OwnCharStatus2(pc));
                pc.sendPackets(new S_PacketBox(S_PacketBox.NONE_TIME_ICON, 0, 479));
            } else if (pc.hasSkillEffect(L1SkillId.BRAVE_AVATAR_2ND)) {
                pc.killSkillEffectTimer(L1SkillId.BRAVE_AVATAR_2ND);
                pc.getResistance().addMr(-10);
                pc.getResistance().addStun(-2);
                pc.getResistance().addHold(-2);
                pc.getAbility().addAddedInt((byte) -1);
                pc.getAbility().addAddedDex((byte) -1);
                pc.getAbility().addAddedStr((byte) -1);
                pc.setPbavatar(false);
                pc.setPbacount(0);
                pc.sendPackets(new S_SPMR(pc));
                pc.sendPackets(new S_OwnCharStatus2(pc));
                pc.sendPackets(new S_PacketBox(S_PacketBox.NONE_TIME_ICON, 0, 479));
            } else if (pc.hasSkillEffect(L1SkillId.BRAVE_AVATAR_3RD)) {
                pc.killSkillEffectTimer(L1SkillId.BRAVE_AVATAR_3RD);
                pc.getResistance().addMr(-10);
                pc.getResistance().addStun(-2);
                pc.getResistance().addHold(-2);
                pc.getAbility().addAddedInt((byte) -1);
                pc.getAbility().addAddedDex((byte) -1);
                pc.getAbility().addAddedStr((byte) -1);
                pc.setPbavatar(false);
                pc.setPbacount(0);
                pc.sendPackets(new S_SPMR(pc));
                pc.sendPackets(new S_OwnCharStatus2(pc));
                pc.sendPackets(new S_PacketBox(S_PacketBox.NONE_TIME_ICON, 0, 479));
            }
        } catch (Exception e) {
        }
    }

    public void allbrave_end(L1PcInstance pc) {
        try {
            for (L1PcInstance player : pc.getParty().getMembers()) {
                if (player.getPbavatar()) {
                    brave_end(player);
                }
            }
        } catch (Exception e) {
        }
    }

}
