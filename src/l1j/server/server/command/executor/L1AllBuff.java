package l1j.server.server.command.executor;

import static l1j.server.server.model.skill.L1SkillId.*;

import java.util.StringTokenizer;

import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1BuffUtil;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Skills;

public class L1AllBuff implements L1CommandExecutor {

    private L1AllBuff() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1AllBuff();
    }

    @Override
    public void execute(L1PcInstance pc, String cmdName, String arg) {
        int[] allBuffSkill = { DECREASE_WEIGHT, PHYSICAL_ENCHANT_DEX,
                PHYSICAL_ENCHANT_STR, BLESS_WEAPON, BERSERKERS,
                IMMUNE_TO_HARM, REDUCTION_ARMOR, BOUNCE_ATTACK,
                SOLID_CARRIAGE, BURNING_SPIRIT, DOUBLE_BRAKE, UNCANNY_DODGE, DRESS_EVASION,
                GLOWING_AURA, BRAVE_AURA, RESIST_MAGIC, CLEAR_MIND, ELEMENTAL_PROTECTION,
                AQUA_PROTECTER, BURNING_WEAPON, IRON_SKIN, EXOTIC_VITALIZE,
                WATER_LIFE, ELEMENTAL_FIRE, SOUL_OF_FLAME, ADDITIONAL_FIRE };
        try {
            StringTokenizer st = new StringTokenizer(arg);
            String name = st.nextToken();
            L1PcInstance target = L1World.getInstance().getPlayer(name);
            if (target == null) {
                pc.sendPackets(new S_ServerMessage(73, name)); // \f1%0ゲームをしていません。
                return;
            }

            L1BuffUtil.haste(target, 3600 * 1000);
            //L1BuffUtil.brave(target, 3600 * 1000);
            //L1PolyMorph.doPoly(target, 5641, 7200, L1PolyMorph.MORPH_BY_GM);
            L1Skills skill = null;
            for (int i = 0; i < allBuffSkill.length; i++) {
                skill = SkillsTable.getInstance().getTemplate(allBuffSkill[i]);
                new L1SkillUse().handleCommands(target, allBuffSkill[i], target.getId(), target.getX(), target.getY(), null, skill.getBuffDuration() * 1000, L1SkillUse.TYPE_GMBUFF);
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(cmdName+ "[キャラクター名]"));
        }
    }
}
