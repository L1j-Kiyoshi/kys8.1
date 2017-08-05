/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */

package l1j.server.server.clientpackets;

import static l1j.server.server.model.skill.L1SkillId.*;

import l1j.server.IndunSystem.Orim.OrimController;
import l1j.server.server.GameClient;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_EffectLocation;
import l1j.server.server.serverpackets.S_SkillSound;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_ExtraCommand extends ClientBasePacket {
    private static final String C_EXTRA_COMMAND = "[C] C_ExtraCommand";

    public C_ExtraCommand(byte abyte0[], GameClient client)
            throws Exception {
        super(abyte0);
        int actionId = readC();

        L1PcInstance pc = client.getActiveChar();
        if (pc == null || pc.isGhost()) {
            return;
        }
        if (pc.isInvisble()) { // インビジビリティ、ブラインドハイディーン、そのうち
            return;
        }
        if (pc.isTeleport()) { // テレポート処理中
            return;
        }
        if (pc.hasSkillEffect(SHAPE_CHANGE)) { // 万一のために、変身中は他のプレイヤーに送信しない
            int gfxId = pc.getTempCharGfx();
            if (gfxId != 6080 && gfxId != 6094) {
                return;
            }
        } else {
            S_DoActionGFX gfx = new S_DoActionGFX(pc.getId(), actionId);
            Broadcaster.broadcastPacket(pc, gfx);
        }

        if ((pc.getMapId() == 9101) && (pc.isInParty()))
            if (((pc.getParty().isLeader(pc) & pc.getX() == 32799)) && (pc.getY() == 32808) && (pc.getHeading() == 4)) {
                if (!pc.hasSkillEffect(67)) {
                    pc.sendPackets(new S_EffectLocation(pc.getX(), pc.getY(), 3206));
                }
                if (actionId == 68) {
                    OrimController.getInstance().explain = true;
                } else {
                    int localL1PcInstance3;
                    if ((actionId == 66) && (OrimController.getInstance().attackTrap().booleanValue()) && (pc.getHeading() == 4)) {
                        L1PcInstance[] arrayOfL1PcInstance1;
                        localL1PcInstance3 = (arrayOfL1PcInstance1 = OrimController.getInstance().getPlayMemberArray()).length;
                        for (int localL1PcInstance1 = 0; localL1PcInstance1 < localL1PcInstance3; localL1PcInstance1++) {
                            L1PcInstance pc1 = arrayOfL1PcInstance1[localL1PcInstance1];
                            pc1.sendPackets(new S_SkillSound(pc1.getId(), 2029));
                            pc1.broadcastPacket(new S_SkillSound(pc1.getId(), 2029));
                        }
                        L1PcInstance[] arrayOfL1PcInstance3;
                        int i;
                        if (OrimController.getInstance().getAtCount() % 2 == 0) {
                            S_DoActionGFX gfxShell1 = new S_DoActionGFX(OrimController.getInstance().getShell1().getId(), 10242);
                            Broadcaster.broadcastPacket(OrimController.getInstance().getShell1(), gfxShell1);
                            i = (arrayOfL1PcInstance3 = OrimController.getInstance().getPlayMemberArray()).length;
                            for (localL1PcInstance3 = 0; localL1PcInstance3 < i; localL1PcInstance3++) {
                                L1PcInstance pc1 = arrayOfL1PcInstance3[localL1PcInstance3];
                                pc1.sendPackets(new S_EffectLocation(32789, 32817, 8233));
                            }
                        } else {
                            S_DoActionGFX gfxShell2 = new S_DoActionGFX(OrimController.getInstance().getShell2().getId(), 10242);
                            Broadcaster.broadcastPacket(OrimController.getInstance().getShell2(), gfxShell2);
                            i = (arrayOfL1PcInstance3 = OrimController.getInstance().getPlayMemberArray()).length;
                            for (localL1PcInstance3 = 0; localL1PcInstance3 < i; localL1PcInstance3++) {
                                L1PcInstance pc1 = arrayOfL1PcInstance3[localL1PcInstance3];
                                pc1.sendPackets(new S_EffectLocation(32795, 32817, 8233));
                            }
                        }
                        OrimController.getInstance().addScore(50);
                        OrimController.getInstance().addAtCount();
                    } else if ((actionId == 69) && (OrimController.getInstance().dependTrap().booleanValue()) && (pc.getHeading() == 4)) {
                        L1PcInstance[] arrayOfL1PcInstance2;
                        localL1PcInstance3 = (arrayOfL1PcInstance2 = OrimController.getInstance().getPlayMemberArray()).length;
                        for (int localL1PcInstance2 = 0; localL1PcInstance2 < localL1PcInstance3; localL1PcInstance2++) {
                            L1PcInstance pc1 = arrayOfL1PcInstance2[localL1PcInstance2];
                            pc1.sendPackets(new S_SkillSound(pc1.getId(), 10165));
                            pc1.sendPackets(new S_SkillSound(pc1.getId(), 2030));
                            pc1.broadcastPacket(new S_SkillSound(pc1.getId(), 2030));
                        }
                        OrimController.getInstance().addDeCount();
                    }
                }
            }
    }

    @Override
    public String getType() {
        return C_EXTRA_COMMAND;
    }
}
