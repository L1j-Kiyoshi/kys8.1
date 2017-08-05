package l1j.server.server.command.executor;

import static l1j.server.server.model.skill.L1SkillId.*;

import java.util.logging.Logger;

import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1RessBuff implements L1CommandExecutor {
    @SuppressWarnings("unused")
    private static Logger _log = Logger.getLogger(L1RessBuff.class.getName());

    private L1RessBuff() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1RessBuff();
    }

    @Override
    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            //int[] allBuffSkill = { PHYSICAL_ENCHANT_DEX, PHYSICAL_ENCHANT_STR, BLESS_WEAPON, IRON_SKIN };
            int[] allBuffSkill = {PHYSICAL_ENCHANT_DEX, PHYSICAL_ENCHANT_STR, BLESS_WEAPON, IRON_SKIN, FEATHER_BUFF_A, LIFE_MAAN, God_buff, COMA_B};
            for (L1PcInstance tg : L1World.getInstance().getVisiblePlayer(pc)) {
                tg.setBuffnoch(1);
                L1SkillUse l1skilluse = new L1SkillUse();
                for (int i = 0; i < allBuffSkill.length; i++) {
                    l1skilluse.handleCommands(tg, allBuffSkill[i], tg.getId(), tg.getX(), tg.getY(), null, 0,
                            L1SkillUse.TYPE_GMBUFF);
                }
                tg.setBuffnoch(0);
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(cmdName + "コマンドエラー"));
        }
    }
}
