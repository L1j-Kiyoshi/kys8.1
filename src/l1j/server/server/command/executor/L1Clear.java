package l1j.server.server.command.executor;

import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SkillSound;

public class L1Clear implements L1CommandExecutor {

    private L1Clear() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1Clear();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        for (L1Object obj : L1World.getInstance().getVisibleObjects(pc, 20)) { // 20の範囲内のオブジェクトを探して
            if (obj instanceof L1MonsterInstance) { // モンスターなら
                L1NpcInstance npc = (L1NpcInstance) obj;
                npc.receiveDamage(pc, 50000); //ダメージ
                if (npc.getCurrentHp() <= 0) {
                    pc.sendPackets(new S_SkillSound(obj.getId(), 2059));
                    pc.broadcastPacket(new S_SkillSound(obj.getId(), 2059));
                } else {
                    pc.sendPackets(new S_SkillSound(obj.getId(), 2059));
                    pc.broadcastPacket(new S_SkillSound(obj.getId(), 2059));
                }
            } else if (obj instanceof L1PcInstance) { // pcなら
                L1PcInstance player = (L1PcInstance) obj;
                player.receiveDamage(player, 0); // ダメージ
                if (player.getCurrentHp() <= 0) {
                }
            }
        }
    }
}