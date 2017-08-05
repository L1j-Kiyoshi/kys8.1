package l1j.server.server.command.executor;

import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_AddSkill;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Skills;

public class L1AddSkill implements L1CommandExecutor {

    private L1AddSkill() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1AddSkill();
    }

    @Override
    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            int cnt = 0; // ループカウンタ
            String skill_name = ""; // スキル名
            int skill_id = 0; // スキルID

            int object_id = pc.getId(); // キャラクターのobjectidを取得
            pc.sendPackets(new S_SkillSound(object_id, '\343')); // 魔法習得の効果音を鳴らす
            pc.broadcastPacket(new S_SkillSound(object_id, '\343'));

            if (pc.isCrown()) {
                pc.sendPackets(new S_AddSkill(255, 255, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 255, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                L1Skills l1skills = null;

                for (cnt = 1; cnt <= 16; cnt++) { // LV1〜2魔法
                    l1skills = SkillsTable.getInstance().getTemplate(cnt); // スキル情報を取得
                    skill_name = l1skills.getName();
                    skill_id = l1skills.getSkillId();
                    SkillsTable.getInstance().spellMastery(object_id, skill_id, skill_name, 0, 0); // DBに登録
                }

                for (cnt = 113; cnt <= 122; cnt++) {// フリーマジック
                    l1skills = SkillsTable.getInstance().getTemplate(cnt); //スキル情報を取得
                    skill_name = l1skills.getName();
                    skill_id = l1skills.getSkillId();
                    SkillsTable.getInstance().spellMastery(object_id, skill_id, skill_name, 0, 0); // DBに登録
                }
            } else if (pc.isKnight()) {
                pc.sendPackets(new S_AddSkill(255, 0, 0, 0, 0, 0, 0, 0, 0, 0, 192, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                L1Skills l1skills = null;

                for (cnt = 1; cnt <= 8; cnt++) {// LV1魔法
                    l1skills = SkillsTable.getInstance().getTemplate(cnt); //スキル情報を取得
                    skill_name = l1skills.getName();
                    skill_id = l1skills.getSkillId();
                    SkillsTable.getInstance().spellMastery(object_id, skill_id, skill_name, 0, 0); // DBに登録
                }

                for (cnt = 87; cnt <= 92; cnt++) {//ナイトマジック
                    l1skills = SkillsTable.getInstance().getTemplate(cnt); // スキル情報を取得
                    skill_name = l1skills.getName();
                    skill_id = l1skills.getSkillId();
                    SkillsTable.getInstance().spellMastery(object_id, skill_id, skill_name, 0, 0); // DBに登録
                }
            } else if (pc.isElf()) {
                pc.sendPackets(new S_AddSkill(255, 255, 127, 255, 255, 255, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 127, 3, 255, 255, 255, 255, 0, 0, 0, 0, 0,
                        0, 0, 0, pc.getElfAttr()));
                L1Skills l1skills = null;

                for (cnt = 1; cnt <= 48; cnt++) {// LV1~6魔法
                    l1skills = SkillsTable.getInstance().getTemplate(cnt); // スキル情報を取得
                    skill_name = l1skills.getName();
                    skill_id = l1skills.getSkillId();
                    SkillsTable.getInstance().spellMastery(object_id, skill_id, skill_name, 0, 0); // DBに登録
                }

                for (cnt = 129; cnt <= 176; cnt++) {//エルフ魔法
                    l1skills = SkillsTable.getInstance().getTemplate(cnt); // スキル情報を取得
                    skill_name = l1skills.getName();
                    skill_id = l1skills.getSkillId();
                    SkillsTable.getInstance().spellMastery(object_id, skill_id, skill_name, 0, 0); // DBに登録
                }
            } else if (pc.isWizard()) {
                pc.sendPackets(new S_AddSkill(255, 255, 127, 255, 255, 255, 255, 255, 255, 255, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0));
                L1Skills l1skills = null;
                for (cnt = 1; cnt <= 80; cnt++) {// LV1~10魔法
                    l1skills = SkillsTable.getInstance().getTemplate(cnt); // スキル情報を取得
                    skill_name = l1skills.getName();
                    skill_id = l1skills.getSkillId();
                    SkillsTable.getInstance().spellMastery(object_id, skill_id, skill_name, 0, 0); // DBに登録
                }
            } else if (pc.isDarkelf()) {
                pc.sendPackets(new S_AddSkill(255, 255, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 255, 127, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                L1Skills l1skills = null;

                for (cnt = 1; cnt <= 16; cnt++) {// LV1〜2魔法
                    l1skills = SkillsTable.getInstance().getTemplate(cnt); // スキル情報を取得
                    skill_name = l1skills.getName();
                    skill_id = l1skills.getSkillId();
                    SkillsTable.getInstance().spellMastery(object_id, skill_id, skill_name, 0, 0); // DBに登録
                }

                for (cnt = 97; cnt <= 112; cnt++) { // DE魔法
                    l1skills = SkillsTable.getInstance().getTemplate(cnt); // スキル情報を取得
                    skill_name = l1skills.getName();
                    skill_id = l1skills.getSkillId();
                    SkillsTable.getInstance().spellMastery(object_id, skill_id, skill_name, 0, 0); // DBに登録
                }
            } else if (pc.isDragonknight()) {
                pc.sendPackets(new S_AddSkill(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 255, 255, 255, 0, 0, 0, 0, 0, 0));
                L1Skills l1skills = null;
                for (cnt = 177; cnt <= 200; cnt++) { // 竜騎士スキル
                    l1skills = SkillsTable.getInstance().getTemplate(cnt); // スキル情報を取得
                    skill_name = l1skills.getName();
                    skill_id = l1skills.getSkillId();
                    SkillsTable.getInstance().spellMastery(object_id, skill_id, skill_name, 0, 0); // DBに登録
                }
            } else if (pc.isBlackwizard()) {
                pc.sendPackets(new S_AddSkill(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 255, 255, 255, 0, 0, 0));
                L1Skills l1skills = null;
                for (cnt = 201; cnt <= 224; cnt++) { // イリュージョニストスキル
                    l1skills = SkillsTable.getInstance().getTemplate(cnt); // スキル情報を取得
                    skill_name = l1skills.getName();
                    skill_id = l1skills.getSkillId();
                    SkillsTable.getInstance().spellMastery(object_id, skill_id, skill_name, 0, 0); // DBに登録
                }
            } else if (pc.isWarrior()) {
                pc.sendPackets(new S_AddSkill(255, 0, 0, 0, 0, 0, 0, 0, 0, 0, 192, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)); // 戦士off
                L1Skills l1skills = null;

                for (cnt = 1; cnt <= 8; cnt++) {// LV1魔法
                    l1skills = SkillsTable.getInstance().getTemplate(cnt); // スキル情報を取得
                    skill_name = l1skills.getName();
                    skill_id = l1skills.getSkillId();
                    SkillsTable.getInstance().spellMastery(object_id, skill_id, skill_name, 0, 0); // DBに登録
                }

                for (cnt = 225; cnt <= 240; cnt++) {// 戦士、魔法
                    l1skills = SkillsTable.getInstance().getTemplate(cnt); // スキル情報を取得
                    skill_name = l1skills.getName();
                    skill_id = l1skills.getSkillId();
                    SkillsTable.getInstance().spellMastery(object_id, skill_id, skill_name, 0, 0); // DBに登録
                }
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(cmdName + "コマンドエラー"));
        }
    }
}
