package l1j.server.server.model.skill;

import static l1j.server.server.model.skill.L1SkillId.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.GameSystem.Robot.L1RobotInstance;
import l1j.server.server.ActionCodes;
import l1j.server.server.SkillCheck;
import l1j.server.server.Controller.WarTimeController;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.PolyTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Cube;
import l1j.server.server.model.L1CurseParalysis;
import l1j.server.server.model.L1EffectSpawn;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Magic;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PinkName;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1TomaHaekDmg;
import l1j.server.server.model.L1War;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1AuctionBoardInstance;
import l1j.server.server.model.Instance.L1BoardInstance;
import l1j.server.server.model.Instance.L1CrownInstance;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1DwarfInstance;
import l1j.server.server.model.Instance.L1EffectInstance;
import l1j.server.server.model.Instance.L1FieldObjectInstance;
import l1j.server.server.model.Instance.L1FurnitureInstance;
import l1j.server.server.model.Instance.L1HousekeeperInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MerchantInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.Instance.L1SupportInstance;
import l1j.server.server.model.Instance.L1TeleporterInstance;
import l1j.server.server.model.Instance.L1TowerInstance;
import l1j.server.server.model.item.function.Telbookitem;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.model.poison.L1DamagePoison;
import l1j.server.server.model.poison.L1ParalysisPoison;
import l1j.server.server.model.trap.L1WorldTraps;
import l1j.server.server.serverpackets.S_ACTION_UI2;
import l1j.server.server.serverpackets.S_ChangeHeading;
import l1j.server.server.serverpackets.S_ChangeName;
import l1j.server.server.serverpackets.S_ChangeShape;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_CloseList;
import l1j.server.server.serverpackets.S_CurseBlind;
import l1j.server.server.serverpackets.S_Dexup;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_DoActionShop;
import l1j.server.server.serverpackets.S_EffectLocation;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_Invis;
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_NewSkillIcon;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_Poison;
import l1j.server.server.serverpackets.S_RangeSkill;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillIconAura;
import l1j.server.server.serverpackets.S_SkillIconGFX;
import l1j.server.server.serverpackets.S_SkillIconShield;
import l1j.server.server.serverpackets.S_SkillIconWindShackle;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_Sound;
import l1j.server.server.serverpackets.S_Strup;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_TrueTargetNew;
import l1j.server.server.serverpackets.S_UseAttackSkill;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1Skills;
import l1j.server.server.utils.CalcStat;
import l1j.server.server.utils.L1SpawnUtil;

public class L1SkillUse {
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_LOGIN = 1;
    public static final int TYPE_SPELLSC = 2;
    public static final int TYPE_NPCBUFF = 3;
    public static final int TYPE_GMBUFF = 4;

    private ArrayList<L1ItemInstance> _weapons;

    private L1Skills _skill;
    private int _skillId;
    private int _getBuffDuration;
    private int _shockStunDuration;
    private int _getBuffIconDuration;
    private int _targetID;
    private int _mpConsume = 0;
    private int _hpConsume = 0;
    private int _targetX = 0;
    private int _targetY = 0;
    private int _PowerRipDuration;
    private int _earthBindDuration;
    @SuppressWarnings("unused")
    private String _message = null;
    private int _skillTime = 0;
    private int _type = 0;
    private boolean _isPK = false;
    // private int _bookmarkId = 0;
    private int _itemobjid = 0;
    private boolean _checkedUseSkill = false;
    private int _leverage = 10;
    private boolean _isFreeze = false;
    private boolean _isCounterMagic = true;

    private L1Character _user = null;
    private L1Character _target = null;

    private L1PcInstance _player = null;
    private L1NpcInstance _npc = null;
    private L1NpcInstance _targetNpc = null;

    private int _calcType;
    private static final int PC_PC = 1;
    private static final int PC_NPC = 2;
    private static final int NPC_PC = 3;
    private static final int NPC_NPC = 4;
    private Random random = new Random(System.nanoTime());
    private ArrayList<TargetStatus> _targetList;

    private short _bookmark_mapid = 0;
    private int _bookmark_x = 0;
    private int _bookmark_y = 0;

    private boolean _isGlanceCheckFail = false;
    private boolean _isCriticalDamage = false;
    // 使用者が詠唱したトゥルーターゲットを一時的に入れるスペース。
    public static Map<Integer, L1Object> _truetarget_list = new HashMap<Integer, L1Object>();

    private static Logger _log = Logger.getLogger(L1SkillUse.class.getName());

    private static final int[] CAST_WITH_INVIS = { 1, 2, 3, 5, 8, 9, 12, 13, 14, 19, 21, 26, 31, 32, 35, 37, 42, 43, 44,
            48, 49, 52, 54, 55, 57, 60, 61, 63, 67, 68, 69, 72, 73, 75, 78, 79, REDUCTION_ARMOR, BOUNCE_ATTACK,
            SOLID_CARRIAGE, COUNTER_BARRIER, 97, 98, 99, 100, 101, 102, 104, 105, 106, 107, 109, 110, 111, 113, 114,
            115, 116, 117, 118, 129, 130, 131, 133, 134, 137, 138, 146, 147, 148, 149, 150, 151, 155, 156, 158, 159,
            163, 164, 165, 166, 168, 169, 170, 171, 181, SOUL_OF_FLAME, ADDITIONAL_FIRE, ANTA_BUFF, FAFU_BUFF,
            RIND_BUFF, VALA_BUFF };

    /**
     * カウンターマジックで防御できないスキル
     **/
    // カウンターマジック
    private static final int[] EXCEPT_COUNTER_MAGIC = { 1, 2, 3, 5, 8, 9, 12, 13, 14, 19, 21, 26, 31, 32, 35, 37, 42,
            43, 44, 48, 49, 52, 54, 55, 57, 60, 61, 63, 67, 68, 69, 72, 73, 75, 78, 79, SHOCK_STUN, BONE_BREAK,
            REDUCTION_ARMOR, BOUNCE_ATTACK, SOLID_CARRIAGE, COUNTER_BARRIER, 97, 98, 99, 100, 101, 102, 104, 105, 106,
            107, 109, 110, 111, 113, 114, 115, 116, 117, 118, 129, 130, 131, 132, 134, 137, 138, 146, 147, 148, 149,
            150, 151, 155, 156, 158, 159, 161, 163, 164, 165, 166, 168, 169, 170, 171, 181, SOUL_OF_FLAME,
            ADDITIONAL_FIRE, FOU_SLAYER, SCALES_EARTH_DRAGON, SCALES_FIRE_DRAGON, DRAGON_SKIN, SCALES_WATER_DRAGON,
            MIRROR_IMAGE, IllUSION_OGRE, PATIENCE, IllUSION_DIAMONDGOLEM, IllUSION_LICH, IllUSION_AVATAR, INSIGHT,
            SHAPE_CHANGE, 10026, 10027, 10028, 10029, 30060, 30000, 30078, 30079, 30011, 30081, 30082, 30083, 30080,
            30084, 30010, 30002, 30086, OMAN_CANCELLATION, ANTA_MESSAGE_2, ANTA_MESSAGE_3, ANTA_MESSAGE_4,
            ANTA_MESSAGE_5, ANTA_MESSAGE_6, ANTA_MESSAGE_7, ANTA_MESSAGE_8, ANTA_MESSAGE_10, 22034, OMAN_STUN,
            PAP_PREDICATE1, PAP_PREDICATE3, PAP_PREDICATE5, PAP_PREDICATE6, PAP_PREDICATE7, PAP_PREDICATE8,
            PAP_PREDICATE9, PAP_PREDICATE11, PAP_PREDICATE12, DESPERADO, POWEGRRIP };

    public L1SkillUse() {
    }

    private static class TargetStatus {
        private L1Character _target = null;
        // private boolean _isAction = false;
        // private boolean _isSendStatus = false;
        private boolean _isCalc = true;

        public TargetStatus(L1Character _cha) {
            _target = _cha;
        }

        public TargetStatus(L1Character _cha, boolean _flg) {
            _isCalc = _flg;
        }

        public L1Character getTarget() {
            return _target;
        }

        public boolean isCalc() {
            return _isCalc;
        }
    }

    public void setLeverage(int i) {
        _leverage = i;
    }

    public int getLeverage() {
        return _leverage;
    }

    private boolean isCheckedUseSkill() {
        return _checkedUseSkill;
    }

    private void setCheckedUseSkill(boolean flg) {
        _checkedUseSkill = flg;
    }

    public boolean checkUseSkill(L1PcInstance player, int skillid, int target_id, int x, int y, String message,
            int time, int type, L1Character attacker) {
        // ** 下のバグチェックステートメントを実行してエラーアンナゲ
        if (player instanceof L1PcInstance) {
            L1Object l1object = L1World.getInstance().findObject(target_id);
            if (l1object instanceof L1ItemInstance) {
                L1ItemInstance item = (L1ItemInstance) l1object;
                if (item.getX() != 0 && item.getY() != 0) { // 地面上のアイテムではなく、
                    // 誰かの所有物
                    return false;
                }
            }
            // ** 下のバグチェックステートメントを実行してエラーアンナゲ

            // ** ノディル防止追加
            long nowtime = System.currentTimeMillis();
            if (skillid == 17 && player.getSkilldelay2() >= nowtime
                    || skillid == 25 && player.getSkilldelay2() >= nowtime) {
                return false;
            } else if (player.getSkilldelay2() >= nowtime) {
                return false;
            }
            // ** ノディル防止追加

            // ** 2次スキルのバグ防止ソースの追加
            int[] CheckSkillID = { 45, 46, 47, 48, 49, 50, 51, 52, 53, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66,
                    67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91,
                    92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 113,
                    114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132, 133,
                    134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 145, 146, 147, 148, 149, 150, 151, 152, 153, 154,
                    155, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165, 166, 167, 169, 170, 171, 172, 173, 174, 175,
                    176, 181, 182, 183, 184, 185, 186, 187, 188, 189, 190, 191, 192, 193, 194, 195, 201, 202, 203, 204,
                    205, 206, 207, 208, 209, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220 };
            // 3、12、13、21、26、42、168、43、54、1、8チツシンツヒムツヘイストは落ち

            // スキルチェックで落ちることのスキルは、上記番号でペサム！

            int check = 0;
            for (int chskill : CheckSkillID) {
                if (chskill == skillid) {
                    check = chskill;
                    break;
                }
            }
            if (player.getBuffnoch() == 0) {
                if (check != 0) {
                    if (!SkillCheck.getInstance().CheckSkill(player, check)) {
                        return false;
                    }
                }
            }
            // **2次スキルのバグ防止ソースの追加

        } // ** 上記のバグチェックステートメントを実行してエラーアンナゲ

        // 存在バグ関連を追加
        if (player instanceof L1PcInstance) {
            L1PcInstance jonje = L1World.getInstance().getPlayer(player.getName());
            if (jonje == null && player.getAccessLevel() != 200) {
                player.sendPackets(new S_SystemMessage("存在バグ強制終了！再接続してください"));
                player.sendPackets(new S_Disconnect());
                return false;
            }

        }
        setCheckedUseSkill(true);
        _targetList = new ArrayList<TargetStatus>();

        _skill = SkillsTable.getInstance().getTemplate(skillid);
        _skillId = skillid;
        _targetX = x;
        _targetY = y;
        _message = message;
        _skillTime = time;
        _type = type;
        boolean checkedResult = true;
        if (attacker == null) {
            // pc
            _player = player;
            _user = _player;
        } else {
            // npc
            _npc = (L1NpcInstance) attacker;
            _user = _npc;
        }

        if (_skill.getTarget().equals("none")) {
            _targetID = _user.getId();
            _targetX = _user.getX();
            _targetY = _user.getY();
        } else {
            _targetID = target_id;
        }

        if (type == TYPE_NORMAL) {
            checkedResult = isNormalSkillUsable();
        } else if (type == TYPE_SPELLSC) {
            checkedResult = isSpellScrollUsable();
        } else if (type == TYPE_NPCBUFF) {
            checkedResult = true;
        }
        if (!checkedResult) {
            return false;
        }

        if (_skillId == FIRE_WALL || _skillId == LIFE_STREAM || _skillId == CUBE_IGNITION || _skillId == CUBE_QUAKE
                || _skillId == CUBE_SHOCK || _skillId == CUBE_BALANCE) {
            return true;
        }

        L1Object l1object = L1World.getInstance().findObject(_targetID);

        if (l1object instanceof L1ItemInstance) {
            _log.fine("skill target item name: " + ((L1ItemInstance) l1object).getViewName());
            return false;
        }
        if (_user instanceof L1PcInstance) {
            if (l1object instanceof L1PcInstance) {
                _calcType = PC_PC;
            } else {
                _calcType = PC_NPC;
                _targetNpc = (L1NpcInstance) l1object;
            }
        } else if (_user instanceof L1NpcInstance) {
            if (l1object instanceof L1PcInstance) {
                _calcType = NPC_PC;
            } else if (_skill.getTarget().equals("none")) {
                _calcType = NPC_PC;
            } else {
                _calcType = NPC_NPC;
                _targetNpc = (L1NpcInstance) l1object;
            }
        }

        if (_skillId == TELEPORT || _skillId == MASS_TELEPORT || _skillId == TRUE_TARGET) {
            _bookmark_mapid = (short) target_id;
            _bookmark_x = x;
            _bookmark_y = y;
        }
        if (_skillId == SUMMON_MONSTER) {
            _bookmark_x = x;
            _bookmark_y = y;
        }

        if (_skillId == BRING_STONE || _skillId == BLESSED_ARMOR || _skillId == ENCHANT_WEAPON
                || _skillId == SHADOW_FANG) {
            _itemobjid = target_id;
        }
        _target = (L1Character) l1object;

        if (!(_target instanceof L1MonsterInstance) && _skill.getTarget().equals("attack")
                && _user.getId() != target_id) {
            _isPK = true;
        }
        if (!(l1object instanceof L1Character)) {
            checkedResult = false;
        }
        makeTargetList();
        if (_targetList.size() == 0 && (_user instanceof L1NpcInstance)) {
            checkedResult = false;
        }
        return checkedResult;
    }

    /**
     * 通常のスキル使用時にユーザーの状態からスキルが使用可能か判断する
     *
     * @return false スキルが使用不可能な状態である場合、
     */
    private boolean isNormalSkillUsable() {
        if (_user instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) _user;

            if (pc.isParalyzed()) {
                return false;
            }
            if ((pc.isInvisble() || pc.isInvisDelay()) && !isInvisUsableSkill() && _skillId != 233) {
                return false;
            }
            if (pc.getRankLevel() < 4 && pc.getInventory().getWeight100() > 82) { // 重量オーバーであればスキルを使用することができない
                pc.sendPackets(new S_ServerMessage(316));
                return false;
            }

            int polyId = pc.getTempCharGfx();
            L1PolyMorph poly = PolyTable.getInstance().getTemplate(polyId);
            if (poly != null && !poly.canUseSkill()) {
                pc.sendPackets(new S_ServerMessage(285));
                return false;
            }

            int castle_id = L1CastleLocation.getCastleIdByArea(pc);
            if (castle_id != 0) {
                if (_skillId == 50 || _skillId == 69 || _skillId == 157 || _skillId == 66 || _skillId == 78
                        || _skillId == 116) {
                    pc.sendPackets(new S_SystemMessage("ボール成長で使用することはできません。"));
                    return false;
                }
            }

            if (pc.getMap().isSafetyZone(pc.getLocation())) {
                if (_skillId == 69 || _skillId == 220 || _skillId == 215 || _skillId == 205 || _skillId == 11
                        || _skillId == 208 || _skillId == 219 || _skillId == 67) { // 67回SHAPE_CHANGE
                    pc.sendPackets(new S_SystemMessage("村では使用できません。"));
                    return false;
                }
            }

            if (!isAttrAgrees()) {
                return false;
            }

            if (_skillId == ELEMENTAL_PROTECTION && pc.getElfAttr() == 0) {
                pc.sendPackets(new S_ServerMessage(280));
                return false;
            }

            if (pc.isSkillDelay()) {
                return false;
            }

            if (_skillId == TRUE_TARGET) {
            } else if ((pc.hasSkillEffect(SILENCE) || pc.hasSkillEffect(AREA_OF_SILENCE)
                    || pc.hasSkillEffect(STATUS_POISON_SILENCE))
                    && (_skillId < SHOCK_STUN || _skillId > COUNTER_BARRIER)) { // サイレンス状態でもトゥルーターゲット詠唱可能
                pc.sendPackets(new S_ServerMessage(285));
                return false;
            }

            if (_skillId == COUNTER_BARRIER || _skillId == SHOCK_STUN) {
                if (pc.getWeapon().getItem().getType() != 3) {
                    pc.sendPackets(new S_ServerMessage(1008));
                    return false;
                }
            }
            if (_skillId == ASSASSIN) {
                if (!pc.hasSkillEffect(L1SkillId.BLIND_HIDING)) {
                    pc.sendPackets(new S_SystemMessage("ブラインド使用人ディング状態でのみ使用することができます。"));
                    return false;
                }
            }

            if (_skillId == DANCING_BLADES) {
                if (pc.getWeapon() == null) {
                    pc.sendPackets(new S_SystemMessage("\\fU剣装備時に使用可能です。"));
                    return false;
                }
                if (pc.getWeapon().getItem().getType() != 1 && pc.getWeapon().getItem().getType() != 2) {
                    pc.sendPackets(new S_SystemMessage("\\fU剣装備時に使用可能です。"));
                    return false;
                }
            }

            /*
             * if (_skillId == ARMOR_BRAKE && _skillId == COUNTER_BARRIER) { //
             * アーマー状態カベ不可 pc.sendPackets(new
             * S_SystemMessage("アーマーブレイクが詰まっている場合詠唱することはできません。")); return false;
             * }
             */

            if (pc.hasSkillEffect(CONFUSION)) {
                pc.sendPackets(new S_ServerMessage(285));
                return false;
            }

            if (isItemConsume() == false && !_player.isGm()) {
                _player.sendPackets(new S_ServerMessage(299));
                return false;
            }
        } else if (_user instanceof L1NpcInstance) {

            if (_skillId == TRUE_TARGET) {
            } else if (_user.hasSkillEffect(CONFUSION)) {
                return false;
            } else if (_user.hasSkillEffect(SILENCE)) {
                _user.removeSkillEffect(SILENCE);
                return false;
            }
        }

        if (!isHPMPConsume()) {
            return false;
        }
        return true;
    }

    private boolean isSpellScrollUsable() {
        L1PcInstance pc = (L1PcInstance) _user;

        if (pc.isParalyzed()) {
            return false;
        }

        if ((pc.isInvisble() || pc.isInvisDelay()) && !isInvisUsableSkill()) {
            return false;
        }

        return true;
    }

    private boolean isInvisUsableSkill() {
        for (int skillId : CAST_WITH_INVIS) {
            if (skillId == _skillId) {
                return true;
            }
        }
        return false;
    }

    public void handleCommands(L1PcInstance player, int skillId, int targetId, int x, int y, String message,
            int timeSecs, int type) {
        L1Character attacker = null;
        handleCommands(player, skillId, targetId, x, y, message, timeSecs, type, attacker);
    }

    // private boolean useok = true;

    public void handleCommands(L1PcInstance player, int skillId, int targetId, int x, int y, String message,
            int timeSecs, int type, L1Character attacker) {

        try {
            if (!isCheckedUseSkill()) {
                boolean isUseSkill = checkUseSkill(player, skillId, targetId, x, y, message, timeSecs, type, attacker);

                if (!isUseSkill) {
                    failSkill();
                    return;
                }
            }
            switch (type) {
            case TYPE_NORMAL:
                if (!_isGlanceCheckFail || _skill.getArea() > 0 || _skill.getTarget().equals("none")) {
                    if (skillId == DANCING_BLADES || skillId == SOLID_CARRIAGE) {
                        // 特定のスキルのタイムアウト
                        sendGrfx(true);
                        runSkill();
                        useConsume();
                        sendFailMessageHandle();
                        setDelay();
                    } else {
                        runSkill();
                        useConsume();
                        sendGrfx(true);
                        sendFailMessageHandle();
                        setDelay();
                    }
                }
                break;
            case TYPE_LOGIN:
                runSkill();
                break;
            case TYPE_SPELLSC:
                runSkill();
                sendGrfx(true);
                setDelay();
                break;
            case TYPE_GMBUFF:
                runSkill();
                sendGrfx(false);
                break;
            case TYPE_NPCBUFF:
                runSkill();
                sendGrfx(true);
                break;
            default:
                break;
            }
            setCheckedUseSkill(false);
        } catch (Exception e) {
            // この部分がNPC見えなかった原因
            // System.out.println("skillId : " + skillId + " / attacker : " +
            // (attacker==null ? "" : attacker.getName()));
            // _log.log(Level.SEVERE, "", e);
        }
    }

    private void failSkill() {
        setCheckedUseSkill(false);
        if (_skillId == TELEPORT || _skillId == MASS_TELEPORT || _skillId == TELEPORT_TO_MATHER) {
            _player.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
        }
    }

    private boolean isTarget(L1Character cha) throws Exception {
        boolean _flg = false;

        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            if (pc.isGhost() || pc.isGmInvis()) {
                return false;
            }
        }
        if (_calcType == NPC_PC
                && (cha instanceof L1PcInstance || cha instanceof L1PetInstance || cha instanceof L1SummonInstance)) {
            _flg = true;
        }

        if (cha instanceof L1DoorInstance) {
            if (cha.getMaxHp() == 0 || cha.getMaxHp() == 1) {
                return false;
            }
        }

        if ((_skill.getTarget().equals("attack") || _skill.getType() == L1Skills.TYPE_ATTACK) && _calcType == NPC_PC
                && cha instanceof L1PcInstance && _user instanceof L1SummonInstance) {
            L1SummonInstance summon = (L1SummonInstance) _user;
            if (cha.getId() == summon.getMaster().getId()) {
                return false;
            }
            if (cha.getZoneType() == 1) {
                return false;
            }
        }

        if ((_skill.getTarget().equals("attack") || _skill.getType() == L1Skills.TYPE_ATTACK) && _calcType == NPC_PC
                && cha instanceof L1PcInstance && _user instanceof L1PetInstance) {
            L1PetInstance pet = (L1PetInstance) _user;
            if (cha.getId() == pet.getMaster().getId()) {
                return false;
            }
            if (cha.getZoneType() == 1) {
                return false;
            }
        }

        if (cha instanceof L1DollInstance && _skillId != HASTE) {
            return false;
        }

        if (_calcType == PC_NPC && _target instanceof L1NpcInstance && !(_target instanceof L1PetInstance)
                && !(_target instanceof L1SummonInstance) && !(_target instanceof L1SupportInstance)
                && (cha instanceof L1PetInstance || cha instanceof L1SummonInstance || cha instanceof L1SupportInstance
                        || cha instanceof L1PcInstance)) {
            return false;
        }

        if ((_skill.getTarget().equals("attack") || _skill.getType() == L1Skills.TYPE_ATTACK) && _calcType == NPC_PC
                && !(cha instanceof L1PetInstance) && !(cha instanceof L1SummonInstance)
                && !(cha instanceof L1SupportInstance) && !(cha instanceof L1PcInstance)) {
            return false;
        }

        if ((_skill.getTarget().equals("attack") || _skill.getType() == L1Skills.TYPE_ATTACK) && _calcType == NPC_NPC
                && _user instanceof L1MonsterInstance && cha instanceof L1MonsterInstance) {
            return false;
        }

        if (_skill.getTarget().equals("none") && _skill.getType() == L1Skills.TYPE_ATTACK
                && (cha instanceof L1AuctionBoardInstance || cha instanceof L1BoardInstance
                        || cha instanceof L1CrownInstance || cha instanceof L1DwarfInstance
                        || cha instanceof L1EffectInstance || cha instanceof L1FieldObjectInstance
                        || cha instanceof L1FurnitureInstance || cha instanceof L1HousekeeperInstance
                        || cha instanceof L1MerchantInstance || cha instanceof L1TeleporterInstance)) {
            return false;
        }

        if (_skill.getType() == L1Skills.TYPE_ATTACK && cha.getId() == _user.getId()) {
            return false;
        }

        if (cha.getId() == _user.getId() && _skillId == HEAL_ALL) {
            return false;
        }

        if (((_skill.getTargetTo() & L1Skills.TARGET_TO_PC) == L1Skills.TARGET_TO_PC
                || (_skill.getTargetTo() & L1Skills.TARGET_TO_CLAN) == L1Skills.TARGET_TO_CLAN
                || (_skill.getTargetTo() & L1Skills.TARGET_TO_PARTY) == L1Skills.TARGET_TO_PARTY)
                && cha.getId() == _user.getId() && _skillId != HEAL_ALL) {
            return true;
        }

        if (_user instanceof L1PcInstance
                && (_skill.getTarget().equals("attack") || _skill.getType() == L1Skills.TYPE_ATTACK)
                && _isPK == false) {
            if (cha instanceof L1SummonInstance) {
                L1SummonInstance summon = (L1SummonInstance) cha;
                if (_player.getId() == summon.getMaster().getId()) {
                    return false;
                }
            } else if (cha instanceof L1PetInstance) {
                L1PetInstance pet = (L1PetInstance) cha;
                if (_player.getId() == pet.getMaster().getId()) {
                    return false;
                }
            } else if (cha instanceof L1SupportInstance) {
                L1SupportInstance supprot = (L1SupportInstance) cha;
                if (_player.getId() == supprot.getMaster().getId()) {
                    return false;
                }
            }
        }

        if ((_skill.getTarget().equals("attack") || _skill.getType() == L1Skills.TYPE_ATTACK)
                && !(cha instanceof L1MonsterInstance) && _isPK == false && _target instanceof L1PcInstance) {
            L1PcInstance enemy = (L1PcInstance) cha;
            if (_skillId == COUNTER_DETECTION && enemy.getZoneType() != 1
                    && (cha.hasSkillEffect(INVISIBILITY) || cha.hasSkillEffect(BLIND_HIDING))) {
                return true;
            }
            if (_player.getClanid() != 0 && enemy.getClanid() != 0) {
                for (L1War war : L1World.getInstance().getWarList()) {
                    if (war.CheckClanInWar(_player.getClanname())) {
                        if (war.CheckClanInSameWar(_player.getClanname(), enemy.getClanname())) {
                            if (L1CastleLocation.checkInAllWarArea(enemy.getX(), enemy.getY(), enemy.getMapId())) {
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        }

        if (_user.glanceCheck(cha.getX(), cha.getY()) == false && _skill.getIsThrough() == false) {
            if (!(_skill.getType() == L1Skills.TYPE_CHANGE || _skill.getType() == L1Skills.TYPE_RESTORE)) {
                _isGlanceCheckFail = true;
                return false;
            }
        }

        /** アイスランスしている場合デバフないかかる **/
        if ((cha.hasSkillEffect(ICE_LANCE)) && (_skillId == ICE_LANCE || _skillId == SHOCK_STUN
                || _skillId == DECAY_POTION || _skillId == WEAPON_BREAK || _skillId == SLOW
                || _skillId == CURSE_PARALYZE || _skillId == MANA_DRAIN || _skillId == DARKNESS
                || _skillId == FOG_OF_SLEEPING || _skillId == ARMOR_BRAKE || _skillId == EARTH_BIND
                || _skillId == WIND_SHACKLE || _skillId == POLLUTE_WATER || _skillId == STRIKER_GALE
                || _skillId == GUARD_BREAK || _skillId == FEAR || _skillId == HORROR_OF_DEATH || _skillId == PANIC
                || _skillId == IllUSION_AVATAR || _skillId == DESPERADO || _skillId == POWEGRRIP)) {
            return false;
        }

        if (cha.hasSkillEffect(EARTH_BIND) && _skillId != CANCELLATION) {
            return false;
        }
        // if (cha.hasSkillEffect(POWERRIP) && _skillId != POWERRIP) {
        //
        // return false;
        // }
        //
        // if (cha.hasSkillEffect(SHOCK_STUN) && _skillId != SHOCK_STUN) {
        // return false;
        // }
        //
        // if (cha.hasSkillEffect(DESPERADO) && _skillId == DESPERADO) {
        // return false; //デスペラード中デスペラードリターン
        // }

        if (cha.hasSkillEffect(MOB_BASILL) && _skillId == MOB_BASILL) {
            return false; // バジルのかたさの中バジルのかたさ
        }
        if (cha.hasSkillEffect(MOB_COCA) && _skillId == MOB_COCA) {
            return false; // コカかた中コカかた
        }

        if (!(cha instanceof L1MonsterInstance) && (_skillId == TAMING_MONSTER || _skillId == CREATE_ZOMBIE)) {
            return false;
        }
        if (cha.isDead() && (_skillId != CREATE_ZOMBIE && _skillId != RESURRECTION && _skillId != GREATER_RESURRECTION
                && _skillId != CALL_OF_NATURE)) {
            return false;
        }

        if (cha.isDead() == false && (_skillId == CREATE_ZOMBIE || _skillId == RESURRECTION
                || _skillId == GREATER_RESURRECTION || _skillId == CALL_OF_NATURE)) {
            return false;
        }

        if ((cha instanceof L1TowerInstance || cha instanceof L1DoorInstance) && (_skillId == CREATE_ZOMBIE
                || _skillId == RESURRECTION || _skillId == GREATER_RESURRECTION || _skillId == CALL_OF_NATURE)) {
            return false;
        }

        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            if (pc.hasSkillEffect(ABSOLUTE_BARRIER)) {// アブソル中
                if (_skillId == CURSE_BLIND || _skillId == WEAPON_BREAK || _skillId == DARKNESS || _skillId == WEAKNESS
                        || _skillId == DISEASE || _skillId == FOG_OF_SLEEPING || _skillId == GREATER_SLOW
                        || _skillId == SLOW || _skillId == CANCELLATION || _skillId == SILENCE || _skillId == ENTANGLE
                        || _skillId == DECAY_POTION || _skillId == MASS_TELEPORT || _skillId == DETECTION
                        || _skillId == IZE_BREAK || _skillId == HORROR_OF_DEATH || _skillId == COUNTER_DETECTION
                        || _skillId == GUARD_BREAK || _skillId == ERASE_MAGIC || _skillId == FEAR
                        || _skillId == PHYSICAL_ENCHANT_DEX || _skillId == PHYSICAL_ENCHANT_STR
                        || _skillId == BLESS_WEAPON || _skillId == IMMUNE_TO_HARM || _skillId == REMOVE_CURSE
                        || _skillId == CONFUSION || _skillId == Sand_worms || _skillId == Sand_worms1
                        || _skillId == Sand_worms2 || _skillId == Sand_worms3 || _skillId == MOB_SLOW_1
                        || _skillId == MOB_SLOW_18 || _skillId == MOB_WEAKNESS_1 || _skillId == MOB_DISEASE_1
                        || _skillId == MOB_BASILL || _skillId == MOB_SHOCKSTUN_30 || _skillId == MOB_RANGESTUN_19
                        || _skillId == MOB_RANGESTUN_18 || _skillId == MOB_DISEASE_30 || _skillId == MOB_WINDSHACKLE_1
                        || _skillId == MOB_COCA || _skillId == MOB_CURSEPARALYZ_19 || _skillId == MOB_CURSEPARALYZ_18
                        || _skillId == Mob_RANGESTUN_30 || _skillId == ANTA_MESSAGE_1 || _skillId == ANTA_MESSAGE_2
                        || _skillId == ANTA_MESSAGE_3 || _skillId == ANTA_MESSAGE_4 || _skillId == ANTA_MESSAGE_5
                        || _skillId == ANTA_MESSAGE_6 || _skillId == ANTA_MESSAGE_7 || _skillId == ANTA_MESSAGE_8
                        || _skillId == ANTA_MESSAGE_9 || _skillId == ANTA_MESSAGE_10 || _skillId == OMAN_STUN
                        || _skillId == OMAN_CANCELLATION) {
                    return true;
                } else {
                    return false;
                }
            }
        }

        if (cha instanceof L1NpcInstance) {
            int hiddenStatus = ((L1NpcInstance) cha).getHiddenStatus();
            if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_SINK) {
                if (_skillId == DETECTION || _skillId == IZE_BREAK || _skillId == EYE_OF_DRAGON
                        || _skillId == COUNTER_DETECTION) {
                    return true;
                } else {
                    return false;
                }
            } else if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_FLY) {
                return false;
            }
        }

        if ((_skill.getTargetTo() & L1Skills.TARGET_TO_PC) == L1Skills.TARGET_TO_PC && cha instanceof L1PcInstance) {
            _flg = true;
        } else if ((_skill.getTargetTo() & L1Skills.TARGET_TO_NPC) == L1Skills.TARGET_TO_NPC
                && (cha instanceof L1MonsterInstance || cha instanceof L1NpcInstance || cha instanceof L1SummonInstance
                        || cha instanceof L1PetInstance)) {
            _flg = true;
        } else if ((_skill.getTargetTo() & L1Skills.TARGET_TO_PET) == L1Skills.TARGET_TO_PET
                && _user instanceof L1PcInstance) {
            if (cha instanceof L1SummonInstance) {
                L1SummonInstance summon = (L1SummonInstance) cha;
                if (summon.getMaster() != null) {
                    if (_player.getId() == summon.getMaster().getId()) {
                        if (_skillId != L1SkillId.RETURN_TO_NATURE) {
                            _flg = true;
                        }
                    } else {
                        if (_skillId == L1SkillId.RETURN_TO_NATURE) {
                            _flg = true;
                        }
                    }
                }
            }

            if (cha instanceof L1PetInstance) {
                L1PetInstance pet = (L1PetInstance) cha;
                if (pet.getMaster() != null) {
                    if (_player.getId() == pet.getMaster().getId()) {
                        if (_skillId != L1SkillId.RETURN_TO_NATURE) {
                            _flg = true;
                        }
                    } else {
                        if (_skillId == L1SkillId.RETURN_TO_NATURE) {
                            _flg = true;
                        }
                    }
                }
            }
        }

        if (_calcType == PC_PC && cha instanceof L1PcInstance) {
            if ((_skill.getTargetTo() & L1Skills.TARGET_TO_CLAN) == L1Skills.TARGET_TO_CLAN
                    && ((_player.getClanid() != 0 && _player.getClanid() == ((L1PcInstance) cha).getClanid())
                            || _player.isGm())) {
                return true;
            }
            if ((_skill.getTargetTo() & L1Skills.TARGET_TO_PARTY) == L1Skills.TARGET_TO_PARTY
                    && (_player.getParty().isMember((L1PcInstance) cha) || _player.isGm())) {
                return true;
            }
        }

        return _flg;
    }

    private void EffectSpawn() { // エフェクト出現タイプ別分けよう
        int Effect = 0;
        if (_skillId == DESERT_SKILL4) {
            Effect = 5137;
        }
        int xx = 0;
        int yy = 0;
        int xx1 = 0;
        int yy1 = 0;
        int xx2 = 0;
        int yy2 = 0;
        int xx3 = 0;
        int yy3 = 0;
        int xx4 = 0;
        int yy4 = 0;
        int randomxy = random.nextInt(4);
        int r = random.nextInt(2) + 1;
        int a1 = 3 + randomxy;
        int a2 = -3 - randomxy;
        int b1 = 2 + randomxy;
        int b2 = -2 - randomxy;
        int heading = _npc.getHeading(); // モンスター方向
        switch (heading) {
        case 1:
            xx = a1 - r;
            yy = a2 + r;
            yy1 = a2;
            xx2 = a1;
            xx3 = a2;
            yy3 = b2;
            xx4 = b1;
            yy4 = a1;
            break;
        case 2:
            xx = a1 + 1;
            xx1 = b1;
            yy1 = a2;
            xx2 = b1;
            yy2 = a1;
            xx3 = b1 - 3;
            yy3 = a2 - 2;
            xx4 = b1 - 2;
            yy4 = a1 + 3;
            break;
        case 3:
            xx = a1 - r;
            yy = a1 - r;
            xx1 = a1;
            yy2 = a1;
            xx3 = a1;
            yy3 = a2;
            xx4 = a2;
            yy4 = b1;
            break;
        case 4:
            yy = a1 + 1;
            xx1 = a1;
            yy1 = b1;
            xx2 = a2;
            yy2 = b1;
            xx3 = a1 + 3;
            yy3 = b1 - 3;
            xx4 = a2 - 3;
            yy4 = b1 - 3;
            break;
        case 5:
            xx = a2 + r;
            yy = a1 - r;
            yy1 = a1;
            xx2 = a2;
            xx3 = a1;
            yy3 = b1;
            xx4 = b2;
            yy4 = a2;
            break;
        case 6:
            xx = a2 - 1;
            xx1 = b2;
            yy1 = a1;
            xx2 = b2;
            yy2 = a2;
            xx3 = b2 + 3;
            yy3 = a1 + 2;
            xx4 = b2 + 2;
            yy4 = a2 - 3;
            break;
        case 7:
            xx = a2 + r;
            yy = a2 + r;
            xx1 = a2;
            yy2 = a2;
            xx3 = a2;
            yy3 = a1;
            xx4 = a1;
            yy4 = b2;
            break;
        case 0:
            yy = a2 - 1;
            xx1 = a2;
            yy1 = b2;
            xx2 = a1;
            yy2 = b2;
            xx3 = a2 - 3;
            yy3 = b2 + 3;
            xx4 = a1 + 3;
            yy4 = b2 + 3;
            break;
        default:
            break;
        }
        int x = _npc.getX() + xx;
        int y = _npc.getY() + yy;
        // 菱形4 * 4ピクセルの形状（モンスターの基準で正面に出現）
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x, y, _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x, y + 1, _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x, y - 1, _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x, y - 2, _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x - 1, y, _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x - 1, y + 1,
                _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x - 1, y - 1,
                _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x - 1, y - 2,
                _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x + 1, y + 1,
                _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x + 1, y - 1,
                _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x + 1, y, _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x + 1, y - 2,
                _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x + 2, y - 2,
                _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x + 2, y - 1,
                _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x + 2, y, _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x + 2, y + 1,
                _user.getMapId());
        int x1 = _npc.getX() + xx1;
        int y1 = _npc.getY() + yy1;
        // 菱形4 * 4ピクセルの形状（モンスターの基準で左側に出現）
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x1, y1, _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x1, y1 + 1, _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x1, y1 - 1, _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x1, y1 - 2, _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x1 - 1, y1, _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x1 - 1, y1 + 1,
                _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x1 - 1, y1 - 1,
                _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x1 - 1, y1 - 2,
                _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x1 + 1, y1 + 1,
                _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x1 + 1, y1 - 1,
                _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x1 + 1, y1, _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x1 + 1, y1 - 2,
                _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x1 + 2, y1 - 2,
                _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x1 + 2, y1 - 1,
                _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x1 + 2, y1, _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x1 + 2, y1 + 1,
                _user.getMapId());
        int x2 = _npc.getX() + xx2;
        int y2 = _npc.getY() + yy2;
        // 菱形4 * 4ピクセルの形状（モンスターの基準から右に出現）
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x2, y2, _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x2, y2 + 1, _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x2, y2 - 1, _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x2, y2 - 2, _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x2 - 1, y2, _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x2 - 1, y2 + 1,
                _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x2 - 1, y2 - 1,
                _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x2 - 1, y2 - 2,
                _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x2 + 1, y2 + 1,
                _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x2 + 1, y2 - 1,
                _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x2 + 1, y2, _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x2 + 1, y2 - 2,
                _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x2 + 2, y2 - 2,
                _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x2 + 2, y2 - 1,
                _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x2 + 2, y2, _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x2 + 2, y2 + 1,
                _user.getMapId());
        int x3 = _npc.getX() + xx3;
        int y3 = _npc.getY() + yy3;
        // 菱形4 * 4ピクセルの形状（モンスターの基準で左側2に出現）
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x3, y3, _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x3, y3 + 1, _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x3, y3 - 1, _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x3, y3 - 2, _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x3 - 1, y3, _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x3 - 1, y3 + 1,
                _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x3 - 1, y3 - 1,
                _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x3 - 1, y3 - 2,
                _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x3 + 1, y3 + 1,
                _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x3 + 1, y3 - 1,
                _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x3 + 1, y3, _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x3 + 1, y3 - 2,
                _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x3 + 2, y3 - 2,
                _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x3 + 2, y3 - 1,
                _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x3 + 2, y3, _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x3 + 2, y3 + 1,
                _user.getMapId());
        int x4 = _npc.getX() + xx4;
        int y4 = _npc.getY() + yy4;
        // 菱形4 * 4ピクセルの形状（モンスターの基準から右2に出現）
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x4, y4, _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x4, y4 + 1, _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x4, y4 - 1, _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x4, y4 - 2, _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x4 - 1, y4, _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x4 - 1, y4 + 1,
                _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x4 - 1, y4 - 1,
                _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x4 - 1, y4 - 2,
                _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x4 + 1, y4 + 1,
                _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x4 + 1, y4 - 1,
                _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x4 + 1, y4, _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x4 + 1, y4 - 2,
                _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x4 + 2, y4 - 2,
                _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x4 + 2, y4 - 1,
                _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x4 + 2, y4, _user.getMapId());
        L1EffectSpawn.getInstance().spawnEffect(Effect, _skill.getBuffDuration() * 1000, x4 + 2, y4 + 1,
                _user.getMapId());
        return;
    }

    private void makeTargetList() {
        try {
            if (_type == TYPE_LOGIN) {
                _targetList.add(new TargetStatus(_user));
                return;
            }
            if (_skill.getTargetTo() == L1Skills.TARGET_TO_ME
                    && (_skill.getType() & L1Skills.TYPE_ATTACK) != L1Skills.TYPE_ATTACK) {
                _targetList.add(new TargetStatus(_user));
                return;
            }

            if (_skill.getRanged() != -1) {// 射程距離-1画面内のオブジェクトのみ
                if (_user.getLocation().getTileLineDistance(_target.getLocation()) > _skill.getRanged()) {
                    return;
                }
            } else {
                if (!_user.getLocation().isInScreen(_target.getLocation())) {
                    return;
                }
            }

            if (isTarget(_target) == false && !(_skill.getTarget().equals("none"))) {
                return;
            }

            if (_skillId == LIGHTNING) {
                for (L1Object tgobj : L1World.getInstance().getVisibleLineObjects(_user, _target)) {
                    if (tgobj == null) {
                        continue;
                    }
                    if (!(tgobj instanceof L1Character)) {
                        continue;
                    }
                    L1Character cha = (L1Character) tgobj;
                    if (isTarget(cha) == false) {
                        continue;
                    }
                    _targetList.add(new TargetStatus(cha));
                }
                return;
            }

            if (_skillId == IMMUNE_TO_HARM) {
                if (_user.glanceCheck(_target.getX(), _target.getY()) == false) {
                    return;
                }
            }

            if (_skill.getArea() == 0) {
                if (_user.glanceCheck(_target.getX(), _target.getY()) == false) {
                    if ((_skill.getType() & L1Skills.TYPE_ATTACK) == L1Skills.TYPE_ATTACK) {
                        _targetList.add(new TargetStatus(_target, false));
                        return;
                    }
                }
                _targetList.add(new TargetStatus(_target));
            } else {
                if (!_skill.getTarget().equals("none")) {
                    _targetList.add(new TargetStatus(_target));
                }

                if (_skillId != 49
                        && !(_skill.getTarget().equals("attack") || _skill.getType() == L1Skills.TYPE_ATTACK)) {
                    _targetList.add(new TargetStatus(_user));
                }

                List<L1Object> objects;
                if (_skill.getArea() == -1) {
                    objects = L1World.getInstance().getVisibleObjects(_user);
                } else {
                    objects = L1World.getInstance().getVisibleObjects(_target, _skill.getArea());
                }
                for (L1Object tgobj : objects) {
                    if (tgobj == null) {
                        continue;
                    }
                    if (!(tgobj instanceof L1Character)) {
                        continue;
                    }
                    L1Character cha = (L1Character) tgobj;
                    if (!isTarget(cha)) {
                        continue;
                    }
                    /*
                     * if (tgobj instanceof L1PcInstance) { L1PcInstance
                     * targetpc = (L1PcInstance) tgobj; if (_player.getClanid()
                     * == targetpc.getClanid()) { continue; } }
                     */

                    if (_skillId == METEOR_STRIKE) {
                        if (cha instanceof L1PcInstance && _user instanceof L1PcInstance) {
                            boolean isNowWar = false;
                            int castleId = L1CastleLocation.getCastleIdByArea((L1PcInstance) _user);
                            if (castleId != 0) {
                                isNowWar = WarTimeController.getInstance().isNowWar(castleId);
                            }
                            if (isNowWar == false) {
                                continue;
                            }
                        }
                    }
                    _targetList.add(new TargetStatus(cha));
                }
                return;
            }

        } catch (Exception e) {
            _log.finest("exception in L1Skilluse makeTargetList" + e);
        }
    }

    private void sendHappenMessage(L1PcInstance pc) {
        int msgID = _skill.getSysmsgIdHappen();
        if (msgID > 0) {
            pc.sendPackets(new S_ServerMessage(msgID));
        }
    }

    private void sendFailMessageHandle() {
        if (_skill.getType() != L1Skills.TYPE_ATTACK && !_skill.getTarget().equals("none") && _targetList.size() == 0) {
            sendFailMessage();
        }
    }

    private void sendFailMessage() {
        int msgID = _skill.getSysmsgIdFail();
        if (msgID > 0 && (_user instanceof L1PcInstance)) {
            _player.sendPackets(new S_ServerMessage(msgID));
        }
    }

    private boolean isAttrAgrees() {
        int magicattr = _skill.getAttr();
        if (_user instanceof L1NpcInstance || _user instanceof L1RobotInstance) {
            return true;
        }

        if ((_skill.getSkillLevel() >= 17 && _skill.getSkillLevel() <= 22 && magicattr != 0)
                && (magicattr != _player.getElfAttr() && !_player.isGm())) {
            return false;
        }
        return true;
    }

    private boolean isHPMPConsume() {
        _mpConsume = _skill.getMpConsume();
        _hpConsume = _skill.getHpConsume();
        int currentMp = 0;
        int currentHp = 0;

        if (_user instanceof L1NpcInstance) {
            currentMp = _npc.getCurrentMp();
            currentHp = _npc.getCurrentHp();
        } else {
            currentMp = _player.getCurrentMp();
            currentHp = _player.getCurrentHp();

            // intステータスボーナス(消費MP減少)
            int mpDecrease = CalcStat.calcDecreaseMp(_player.getAbility().getTotalInt());
            _mpConsume -= (int) (_mpConsume * (mpDecrease / 100d));

            if (_skillId == PHYSICAL_ENCHANT_DEX && _player.getInventory().checkEquipped(20013)) {
                _mpConsume /= 2;
            }
            if (_skillId == HASTE && _player.getInventory().checkEquipped(20013)) {
                _mpConsume /= 2;
            }
            if (_skillId == HEAL && _player.getInventory().checkEquipped(20014)) {
                _mpConsume /= 2;
            }
            if (_skillId == EXTRA_HEAL && _player.getInventory().checkEquipped(20014)) {
                _mpConsume /= 2;
            }
            if (_skillId == ENCHANT_WEAPON && _player.getInventory().checkEquipped(20015)) {
                _mpConsume /= 2;
            }
            if (_skillId == DETECTION && _player.getInventory().checkEquipped(20015)) {
                _mpConsume /= 2;
            }
            if (_skillId == PHYSICAL_ENCHANT_STR && _player.getInventory().checkEquipped(20015)) {
                _mpConsume /= 2;
            }
            if (_skillId == HASTE && _player.getInventory().checkEquipped(20008)) {
                _mpConsume /= 2;
            }
            if (_skillId == GREATER_HASTE && _player.getInventory().checkEquipped(20023)) {
                _mpConsume /= 2;
            }

            if (0 < _skill.getMpConsume()) {
                _mpConsume = Math.max(_mpConsume, 1);
            }
        }

        if (currentHp < _hpConsume + 1) {
            if (_user instanceof L1PcInstance) {
                _player.sendPackets(new S_ServerMessage(279));
            }
            return false;
        } else if (currentMp < _mpConsume) {
            if (_user instanceof L1PcInstance) {
                _player.sendPackets(new S_ServerMessage(278));
            }
            return false;
        }

        return true;
    }

    private boolean isItemConsume() {

        int itemConsume = _skill.getItemConsumeId();
        int itemConsumeCount = _skill.getItemConsumeCount();

        if (itemConsume == 0) {
            return true;
        }

        if (itemConsume == 40318) { // 魔力の石
            if (_player.getInventory().checkItem(30079, itemConsumeCount) && _player.getLevel() < 56) {
                return true;
            }
        } else if (itemConsume == 40321) { // 黒曜石
            if (_player.getInventory().checkItem(30080, itemConsumeCount) && _player.getLevel() < 56) {
                return true;
            }
        } else if (itemConsume == 210035) { // 刻印のボーンピース
            if (_player.getInventory().checkItem(30081, itemConsumeCount) && _player.getLevel() < 56) {
                return true;
            }
        } else if (itemConsume == 210038) { // 属性石
            if (_player.getInventory().checkItem(30082, itemConsumeCount) && _player.getLevel() < 56) {
                return true;
            }
        } else if (itemConsume == 40319) { // 精霊の玉
            if (_player.getInventory().checkItem(30078, itemConsumeCount) && _player.getLevel() < 56) {
                return true;
            }
        }
        if (!_player.getInventory().checkItem(itemConsume, itemConsumeCount)) {
            return false;
        }

        return true;
    }

    private void useConsume() {
        if (_user instanceof L1NpcInstance) {
            int current_hp = _npc.getCurrentHp() - _hpConsume;
            _npc.setCurrentHp(current_hp);

            int current_mp = _npc.getCurrentMp() - _mpConsume;
            _npc.setCurrentMp(current_mp);
            return;
        }

        if (isHPMPConsume()) {
            if (_skillId == FINAL_BURN) {
                _player.setCurrentHp(100);
                _player.setCurrentMp(1);
            } else {
                int current_hp = _player.getCurrentHp() - _hpConsume;
                _player.setCurrentHp(current_hp);

                int current_mp = _player.getCurrentMp() - _mpConsume;
                _player.setCurrentMp(current_mp);
            }
        }

        int lawful = _player.getLawful() + _skill.getLawful();
        if (lawful > 32767) {
            lawful = 32767;
        }
        if (lawful < -32767) {
            lawful = -32767;
        }
        _player.setLawful(lawful);

        int itemConsume = _skill.getItemConsumeId();
        int itemConsumeCount = _skill.getItemConsumeCount();

        if (itemConsume == 0) {
            return;
        }

        if (itemConsume == 40318) { // 魔力の石
            if (_player.getInventory().checkItem(30079, itemConsumeCount) && _player.getLevel() < 56) {
                itemConsume = 30079;
            }
        } else if (itemConsume == 40321) { // 黒曜石
            if (_player.getInventory().checkItem(30080, itemConsumeCount) && _player.getLevel() < 56) {
                itemConsume = 30080;
            }
        } else if (itemConsume == 210035) { // 刻印のボーンピース
            if (_player.getInventory().checkItem(30081, itemConsumeCount) && _player.getLevel() < 56) {
                itemConsume = 30081;
            }
        } else if (itemConsume == 210038) { // 属性石
            if (_player.getInventory().checkItem(30082, itemConsumeCount) && _player.getLevel() < 56) {
                itemConsume = 30082;
            }
        } else if (itemConsume == 40319) { // 精霊の玉
            if (_player.getInventory().checkItem(30078, itemConsumeCount) && _player.getLevel() < 56) {
                itemConsume = 30078;
            }
        }
        _player.getInventory().consumeItem(itemConsume, itemConsumeCount);
    }

    private void addMagicList(L1Character cha, boolean repetition) {
        if (_skillTime == 0) {
            _getBuffDuration = _skill.getBuffDuration() * 1000;
            if (_skill.getBuffDuration() == 0) {
                if (_skillId == INVISIBILITY) {
                    cha.setSkillEffect(INVISIBILITY, 0);
                }
                return;
            }
        } else {
            _getBuffDuration = _skillTime * 1000;
        }

        if (_skillId == SHOCK_STUN || _skillId == ANTA_MESSAGE_6 || _skillId == ANTA_MESSAGE_7
                || _skillId == ANTA_MESSAGE_8 || _skillId == OMAN_STUN || _skillId == POWEGRRIP
                || _skillId == EARTH_BIND || _skillId == DESPERADO) {
            _getBuffDuration = _shockStunDuration;
        }

        if (_skillId == CURSE_POISON || _skillId == TOMAHAWK) {
            return;
        }

        if (_skillId == CURSE_PARALYZE || _skillId == CURSE_PARALYZE2) {
            return;
        }
        // if (_skillId == SHAPE_CHANGE) {
        // return;
        // }
        if (_skillId == BLESSED_ARMOR || _skillId == HOLY_WEAPON || _skillId == ENCHANT_WEAPON
                || _skillId == BLESS_WEAPON || _skillId == SHADOW_FANG) {
            return;
        }
        if ((_skillId == ICE_LANCE) && !_isFreeze) {
            return;
        }
        cha.setSkillEffect(_skillId, _getBuffDuration);

        if (cha instanceof L1PcInstance && repetition) {
            L1PcInstance pc = (L1PcInstance) cha;
            sendIcon(pc);
        }
    }

    private void sendIcon(L1PcInstance pc) {
        if (_skillTime == 0) {
            _getBuffIconDuration = _skill.getBuffDuration();
        } else {
            _getBuffIconDuration = _skillTime;
        }
        switch (_skillId) {
        case VALA_BUFF:
            pc.sendPackets(new S_SkillIconGFX(88, _getBuffIconDuration));
            break;
        case SHIELD:
            pc.sendPackets(new S_SkillIconShield(1, _getBuffIconDuration));
            break;
        // case SHADOW_ARMOR:
        // pc.sendPackets(new S_SkillIconShield(3, _getBuffIconDuration));
        // break;
        case DRESS_DEXTERITY:
            pc.sendPackets(new S_Dexup(pc, 2, _getBuffIconDuration));
            break;
        case DRESS_MIGHTY:
            pc.sendPackets(new S_Strup(pc, 2, _getBuffIconDuration));
            break;
        case GLOWING_AURA:
            pc.sendPackets(new S_SkillIconAura(113, _getBuffIconDuration));
            break;
        case SHINING_AURA:
            pc.sendPackets(new S_SkillIconAura(114, _getBuffIconDuration));
            break;
        case BRAVE_AURA:
            pc.sendPackets(new S_SkillIconAura(116, _getBuffIconDuration));
            break;
        case FIRE_WEAPON:
            pc.sendPackets(new S_SkillIconAura(147, _getBuffIconDuration));
            break;
        case WIND_SHOT:
            pc.sendPackets(new S_SkillIconAura(148, _getBuffIconDuration));
            break;
        case DANCING_BLADES:
            pc.sendPackets(new S_SkillIconAura(154, _getBuffIconDuration));
            break;
        case STORM_EYE:
            pc.sendPackets(new S_SkillIconAura(155, _getBuffIconDuration));
            break;
        case EARTH_GUARDIAN:
            pc.sendPackets(new S_SkillIconShield(7, _getBuffIconDuration));
            break;
        case BURNING_WEAPON:
            pc.sendPackets(new S_SkillIconAura(162, _getBuffIconDuration));
            break;
        case STORM_SHOT:
            pc.sendPackets(new S_SkillIconAura(165, _getBuffIconDuration));
            break;
        case IRON_SKIN:
            pc.sendPackets(new S_SkillIconShield(10, _getBuffIconDuration));
            break;
        case EARTH_SKIN:
            pc.sendPackets(new S_SkillIconShield(6, _getBuffIconDuration));
            break;
        case PHYSICAL_ENCHANT_STR:
            pc.sendPackets(new S_Strup(pc, 5, _getBuffIconDuration));
            break;
        case PHYSICAL_ENCHANT_DEX:
            pc.sendPackets(new S_Dexup(pc, 5, _getBuffIconDuration));
            break;
        case NARUTO_THANKS_CANDY:
            if (pc.getLevel() >= 1 && pc.getLevel() <= 60) {
                pc.sendPackets(new S_Dexup(pc, 7, _getBuffIconDuration));
                pc.sendPackets(new S_Strup(pc, 7, _getBuffIconDuration));
            } else {
                pc.sendPackets(new S_Dexup(pc, 6, _getBuffIconDuration));
                pc.sendPackets(new S_Strup(pc, 6, _getBuffIconDuration));
            }
            break;
        case IMMUNE_TO_HARM:
            pc.sendPackets(new S_SkillIconGFX(40, _getBuffIconDuration));
            break;
        case HASTE:
        case GREATER_HASTE:
            pc.sendPackets(new S_SkillHaste(pc.getId(), 1, _getBuffIconDuration));
            pc.broadcastPacket(new S_SkillHaste(pc.getId(), 1, 0));
            break;
        case HOLY_WALK:
        case MOVING_ACCELERATION:
        case WIND_WALK:
            pc.sendPackets(new S_SkillBrave(pc.getId(), 4, _getBuffIconDuration));
            pc.broadcastPacket(new S_SkillBrave(pc.getId(), 4, 0));
            break;
        case BLOOD_LUST:
            pc.sendPackets(new S_SkillBrave(pc.getId(), 1, _getBuffIconDuration));
            pc.broadcastPacket(new S_SkillBrave(pc.getId(), 1, 0));
            break;
        case SLOW:
        case MOB_SLOW_1:
        case MOB_SLOW_18:
        case GREATER_SLOW:
        case ENTANGLE:
            pc.sendPackets(new S_SkillHaste(pc.getId(), 2, _getBuffIconDuration));
            pc.broadcastPacket(new S_SkillHaste(pc.getId(), 2, 0));
            break;
        default:
            break;
        }
        pc.sendPackets(new S_OwnCharStatus(pc));
    }

    private void sendGrfx(boolean isSkillAction) {
        int actionId = _skill.getActionId();
        int actionId2 = _skill.getActionId2();
        int actionId3 = _skill.getActionId3();
        int castgfx = _skill.getCastGfx();
        int castgfx2 = _skill.getCastGfx2();
        int castgfx3 = _skill.getCastGfx3();

        if (castgfx == 0) {
            return;
        }
        if (_isCriticalDamage) {
            switch (_skillId) {
            case CALL_LIGHTNING: // コールライトニング
                castgfx = 11737;
                break;
            case SUNBURST: // サンバースト
                castgfx = 11760;
                break;
            case CONE_OF_COLD: // コーンオブコールド
                castgfx = 11742;
                break;
            case DISINTEGRATE: // ディスインテグレート
                castgfx = 11748;
                break;
            case ERUPTION:
                castgfx = 11754;
                break;
            }
        } else {
            if (_skillId == UNCANNY_DODGE) {
                L1PcInstance pc = (L1PcInstance) _target;
                if (pc.getAC().getAc() <= -100) {
                    castgfx = 11766;
                }
            } else {
                if (castgfx != _skill.getCastGfx()) {
                    return; // グラフィック番号が異なっている。
                }
            }
        }
        if (castgfx2 != _skill.getCastGfx2()) {
            return;
        }
        if (castgfx3 != _skill.getCastGfx3()) {
            return;
        }

        if (_user instanceof L1PcInstance) {
            if (_skillId == FIRE_WALL || _skillId == LIFE_STREAM || _skillId == CUBE_IGNITION || _skillId == CUBE_QUAKE
                    || _skillId == CUBE_SHOCK || _skillId == CUBE_BALANCE) {
                L1PcInstance pc = (L1PcInstance) _user;
                if (_skillId == FIRE_WALL) {
                    pc.setHeading(pc.targetDirection(_targetX, _targetY));
                    pc.sendPackets(new S_ChangeHeading(pc));
                    pc.broadcastPacket(new S_ChangeHeading(pc));
                }
                S_DoActionGFX gfx = new S_DoActionGFX(pc.getId(), actionId);
                pc.sendPackets(gfx);
                pc.broadcastPacket(gfx);
                return;
            }

            int targetid = _target.getId();

            if (_skillId == SHOCK_STUN || _skillId == MOB_SHOCKSTUN_30 || _skillId == MOB_RANGESTUN_20
                    || _skillId == MOB_RANGESTUN_19 || _skillId == MOB_RANGESTUN_18 || _skillId == Mob_RANGESTUN_30
                    || _skillId == ANTA_MESSAGE_6 || _skillId == ANTA_MESSAGE_7 || _skillId == ANTA_MESSAGE_8
                    || _skillId == OMAN_STUN) {
                if (_targetList.size() == 0) {// 失敗スターンモーション
                    if (_target instanceof L1PcInstance) { // Gn.89
                        L1PcInstance pc = (L1PcInstance) _target;
                        pc.sendPackets(new S_SkillSound(pc.getId(), 4434));
                        pc.broadcastPacket(new S_SkillSound(pc.getId(), 4434));
                        pc.sendPackets(new S_ServerMessage(280));
                    } else if (_target instanceof L1NpcInstance) {
                        _target.broadcastPacket(new S_SkillSound(_target.getId(), 4434));
                    }
                    return;
                } else {
                    if (_target instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) _target;
                        pc.sendPackets(new S_SkillSound(pc.getId(), 4434));
                        pc.broadcastPacket(new S_SkillSound(pc.getId(), 4434));
                    } else if (_target instanceof L1NpcInstance) {
                        _target.broadcastPacket(new S_SkillSound(_target.getId(), 4434));
                    }
                    return;
                }
            }

            if (_skillId == SMASH) {
                if (_targetList.size() == 0) {
                    return;
                } else {
                    if (_target instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) _target;
                        pc.sendPackets(new S_SkillSound(pc.getId(), 6526));
                        pc.broadcastPacket(new S_SkillSound(pc.getId(), 6526));
                    } else if (_target instanceof L1NpcInstance) {
                        _target.broadcastPacket(new S_SkillSound(_target.getId(), 6526));
                    }
                    return;
                }
            }

            if (_skillId == LIGHT) {
                L1PcInstance pc = (L1PcInstance) _target;
                pc.sendPackets(new S_Sound(145));
            }
            if (_skillId == SOUL_OF_FLAME) {
                L1PcInstance pc = (L1PcInstance) _target;
                pc.sendPackets(new S_SkillSound(pc.getId(), 11778, 19));
                pc.sendPackets(new S_PacketBox(S_PacketBox.BUFFICON, 11778, 128));
                Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 11778));
            }

            if (_skillId == SOLID_CARRIAGE) {// ソリッドキャリッジ
                L1PcInstance pc = (L1PcInstance) _target;
                pc.sendPackets(new S_SkillSound(pc.getId(), 5831, 19));
                pc.sendPackets(new S_PacketBox(S_PacketBox.BUFFICON, 5831, 192));
                Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 5831));
            }

            if (_skillId == UNCANNY_DODGE) {
                L1PcInstance pc = (L1PcInstance) _target;
                if (pc.getAC().getAc() <= -100) {
                    pc.sendPackets(new S_SkillSound(pc.getId(), 11766, 19));
                    pc.broadcastPacket(new S_SkillSound(pc.getId(), 11766, 19));
                } else {
                    pc.sendPackets(new S_SkillSound(pc.getId(), 11765, 19));
                    pc.broadcastPacket(new S_SkillSound(pc.getId(), 11765, 19));
                }
            }

            if (_targetList.size() == 0 && !(_skill.getTarget().equals("none"))) {
                int tempchargfx = _player.getTempCharGfx();
                if (tempchargfx == 5727 || tempchargfx == 5730) {
                    actionId = ActionCodes.ACTION_SkillBuff;
                } else if (tempchargfx == 5733 || tempchargfx == 5736) {
                    actionId = ActionCodes.ACTION_Attack;
                }
                if (isSkillAction) {
                    S_DoActionGFX gfx = new S_DoActionGFX(_player.getId(), actionId);
                    _player.sendPackets(gfx);
                    _player.broadcastPacket(gfx);
                }
                return;
            }

            if (_skill.getTarget().equals("attack") && _skillId != 18) {
                if (isPcSummonPet(_target)) {
                    if (_player.getZoneType() == 1 || _target.getZoneType() == 1
                            || _player.checkNonPvP(_player, _target)) {
                        _player.sendPackets(new S_UseAttackSkill(_player, 0, castgfx, _targetX, _targetY, actionId));
                        _player.broadcastPacket(
                                new S_UseAttackSkill(_player, 0, castgfx, _targetX, _targetY, actionId));
                        return;
                    }
                }

                if (_skill.getArea() == 0) {
                    _player.sendPackets(new S_UseAttackSkill(_player, targetid, castgfx, _targetX, _targetY, actionId));
                    _player.broadcastPacket(
                            new S_UseAttackSkill(_player, targetid, castgfx, _targetX, _targetY, actionId), _target);
                    _target.broadcastPacketExceptTargetSight(new S_DoActionGFX(targetid, ActionCodes.ACTION_Damage),
                            _player);



                } else {//対象指定範囲
                    L1Character[] cha = new L1Character[_targetList.size()];
                    int i = 0;
                    int cnt =0;
                    for (TargetStatus ts : _targetList) {
                        cha[i] = ts.getTarget();
                        if(cha[i].hasSkillEffect(IGNORE_AOE)) {
                        	cha[i].setIgnoreAoe(1);
                        }
                        if(cha[i].getIgnoreAoe() ==0) {

                        	cha[i].broadcastPacket(new S_DoActionGFX(cha[i].getId(), ActionCodes.ACTION_Damage),_player); //218 = FIREBALL
                        	if(castgfx ==171) {
                        		cnt +=1;
                        		 S_UseAttackSkill packet = new S_UseAttackSkill(_player, _target.getId(), 171, _target.getX(), _target.getY(),
                                         ActionCodes.ACTION_Attack, false);
                                 _player.sendPackets(packet);
                                 _player.broadcastPacket(packet, _target);

                                 if (cha[i]!=_target) {
                                	 _player.sendPackets(new S_SkillSound(cha[i].getId(), 218));
                                	 _target.broadcastPacket(new S_SkillSound(cha[i].getId(), 218));

                                 }

                        	}
                        }
                        if(cha[i].getIgnoreAoe() ==1) {
                        	if(castgfx ==171) {
                        		cnt+=1;
                        		S_UseAttackSkill packet = new S_UseAttackSkill(_player, _target.getId(), 171, _target.getX(), _target.getY(),
                                        ActionCodes.ACTION_Attack, false);
                                _player.sendPackets(packet);
                                _player.broadcastPacket(packet, _target);
                        	}
                        }
                        i++;
                    }
                    _player.sendPackets(new S_DoActionGFX(_player.getId(), actionId));
                    _player.broadcastPacket(new S_DoActionGFX(_player.getId(), actionId));
                    if(cnt ==0) {
                    	 _player.sendPackets(new S_SkillSound(_target.getId(), castgfx));
                    	 _player.broadcastPacket(new S_SkillSound(_target.getId(), castgfx));
                    }


                    //_player.sendPackets(new S_RangeSkill(_player, cha, castgfx, actionId, S_RangeSkill.TYPE_DIR));
                    //_player.broadcastPacket(new S_RangeSkill(_player, cha, castgfx, actionId, S_RangeSkill.TYPE_DIR),
                		//   cha);
                }



            } else if (_skill.getTarget().equals("none") && _skill.getType() == L1Skills.TYPE_ATTACK) {//術者中心範囲
                L1Character[] cha = new L1Character[_targetList.size()];

                int i = 0;
                int cnt = 0;
                for (TargetStatus ts : _targetList) {

                    cha[i] = ts.getTarget();
                    if(cha[i].hasSkillEffect(IGNORE_AOE)) {
                    	cha[i].setIgnoreAoe(1);

                    }
                    if(cha[i].getIgnoreAoe() == 0) {
                    	cha[i].broadcastPacket(new S_DoActionGFX(cha[i].getId(), ActionCodes.ACTION_Damage),_player);
                    }
                    if(cha[i].getIgnoreAoe() == 1) {
                    	cnt +=1;
                    	//cha[i].broadcastPacketExceptTargetSight(
                    		//	new S_DoActionGFX(cha[i].getId(), ActionCodes.ACTION_Damage),_player);
                    }


                 //   String msg = String.valueOf(cha[i].getIgnoreAoe());
                 //  L1World.getInstance().broadcastServerMessage(msg);		//テスト用

                    i++;


                }

                	_player.sendPackets(new S_DoActionGFX(_player.getId(), actionId));
                    _player.broadcastPacket(new S_DoActionGFX(_player.getId(), actionId));
                    _player.sendPackets(new S_SkillSound(_player.getId(), castgfx));
                    _player.broadcastPacket(new S_SkillSound(_player.getId(), castgfx));
                    //_player.sendPackets(new S_RangeSkill(_player,cha, castgfx, actionId, S_RangeSkill.TYPE_NODIR));
                   // _player.broadcastPacket(new S_RangeSkill(_player, cha, castgfx, actionId, S_RangeSkill.TYPE_NODIR),
                            //cha);



//                _player.sendPackets(new S_DoActionGFX(_player.getId(), actionId));
//                _player.broadcastPacket(new S_DoActionGFX(_player.getId(), actionId));
//                _player.sendPackets(new S_SkillSound(_player.getId(), castgfx));
//                _player.broadcastPacket(new S_SkillSound(_player.getId(), castgfx));

//                _player.sendPackets(new S_RangeSkill(_player,cha, castgfx, actionId, S_RangeSkill.TYPE_NODIR));
//                _player.broadcastPacket(new S_RangeSkill(_player, cha, castgfx, actionId, S_RangeSkill.TYPE_NODIR),
//                        cha);






            } else {
                if (_skillId != 5 && _skillId != 69 && _skillId != 131) {
                    if (isSkillAction) {
                        S_DoActionGFX gfx = new S_DoActionGFX(_player.getId(), _skill.getActionId());
                        _player.sendPackets(gfx);
                        _player.broadcastPacket(gfx);
                    }
                    if (_skillId == COUNTER_MAGIC || _skillId == COUNTER_BARRIER || _skillId == COUNTER_MIRROR) {
                        _player.sendPackets(new S_SkillSound(targetid, castgfx));
                        _player.broadcastPacket(new S_SkillSound(targetid, castgfx));

                    } else if (_skillId == TRUE_TARGET) {
                        return;
                    } else if (_skillId == ARMOR_BRAKE || _skillId == ASSASSIN) {
                        _player.sendPackets(new S_SkillSound(targetid, castgfx));
                    } else {
                        _player.sendPackets(new S_SkillSound(targetid, castgfx));
                        _player.broadcastPacket(new S_SkillSound(targetid, castgfx));
                    }
                }
                for (TargetStatus ts : _targetList) {
                    L1Character cha = ts.getTarget();
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.sendPackets(new S_OwnCharStatus(pc));
                    }
                }
            }
        } else if (_user instanceof L1NpcInstance) {
            int targetid = _target.getId();
            if (_skillId == BLACKELDER) {
                Broadcaster.broadcastPacket(_user, new S_SkillSound(_user.getId(), 4848));
                Broadcaster.broadcastPacket(_user, new S_SkillSound(_user.getId(), 2552));
            }

            if (_user instanceof L1MerchantInstance) {
                _user.broadcastPacket(new S_SkillSound(targetid, castgfx));
                return;
            }

            if (_targetList.size() == 0 && !(_skill.getTarget().equals("none"))) {
                S_DoActionGFX gfx = new S_DoActionGFX(_user.getId(), _skill.getActionId());
                _user.broadcastPacket(gfx);
                return;
            }

            if (_skill.getTarget().equals("attack") && _skillId != 18) {
                if (_skill.getArea() == 0) {
                    _user.broadcastPacket(new S_UseAttackSkill(_user, targetid, castgfx, _targetX, _targetY, actionId),
                            _target);
                    if (actionId2 > 0 && castgfx2 > 0) {
                        _user.broadcastPacket(
                                new S_UseAttackSkill(_user, targetid, castgfx2, _targetX, _targetY, actionId2),
                                _target);
                        _target.broadcastPacketExceptTargetSight(new S_DoActionGFX(targetid, ActionCodes.ACTION_Damage),
                                _user);
                    }
                    if (actionId3 > 0 && castgfx3 > 0) {
                        _user.broadcastPacket(
                                new S_UseAttackSkill(_user, targetid, castgfx3, _targetX, _targetY, actionId3),
                                _target);
                        _target.broadcastPacketExceptTargetSight(new S_DoActionGFX(targetid, ActionCodes.ACTION_Damage),
                                _user);
                    }
                    _target.broadcastPacketExceptTargetSight(new S_DoActionGFX(targetid, ActionCodes.ACTION_Damage),
                            _user);
                } else {

                    L1Character[] cha = new L1Character[_targetList.size()];
                    int i = 0;
                    for (TargetStatus ts : _targetList) {
                        cha[i] = ts.getTarget();
                        cha[i].broadcastPacketExceptTargetSight(
                                new S_DoActionGFX(cha[i].getId(), ActionCodes.ACTION_Damage), _user);
                        i++;
                    }
                    _user.broadcastPacket(new S_RangeSkill(_user, cha, castgfx, actionId, S_RangeSkill.TYPE_DIR), cha);
                    if (actionId2 > 0 && castgfx2 > 0) {
                        _user.broadcastPacket(new S_RangeSkill(_user, cha, castgfx2, actionId2, S_RangeSkill.TYPE_DIR));
                        _target.broadcastPacketExceptTargetSight(new S_DoActionGFX(targetid, ActionCodes.ACTION_Damage),
                                _user);
                    }
                    if (actionId3 > 0 && castgfx3 > 0) {
                        _user.broadcastPacket(new S_RangeSkill(_user, cha, castgfx3, actionId3, S_RangeSkill.TYPE_DIR));
                        _target.broadcastPacketExceptTargetSight(new S_DoActionGFX(targetid, ActionCodes.ACTION_Damage),
                                _user);
                    }
                }
            } else if (_skill.getTarget().equals("none") && _skill.getType() == L1Skills.TYPE_ATTACK) {
                L1Character[] cha = new L1Character[_targetList.size()];
                int i = 0;
                for (TargetStatus ts : _targetList) {
                    cha[i] = ts.getTarget();
                    i++;
                }
                _user.broadcastPacket(new S_RangeSkill(_user, cha, castgfx, actionId, S_RangeSkill.TYPE_NODIR), cha);
            } else {
                if (_skillId != 5 && _skillId != 69 && _skillId != 131) {
                    S_DoActionGFX gfx = new S_DoActionGFX(_user.getId(), _skill.getActionId());
                    _user.broadcastPacket(gfx);
                    _user.broadcastPacket(new S_SkillSound(targetid, castgfx));
                    if (actionId2 > 0 && castgfx2 > 0) {
                        S_DoActionGFX gfx2 = new S_DoActionGFX(_user.getId(), _skill.getActionId2());
                        _user.broadcastPacket(gfx2);
                        _user.broadcastPacket(new S_SkillSound(targetid, castgfx2));
                    }
                    if (actionId3 > 0 && castgfx3 > 0) {
                        S_DoActionGFX gfx3 = new S_DoActionGFX(_user.getId(), _skill.getActionId3());
                        _user.broadcastPacket(gfx3);
                        _user.broadcastPacket(new S_SkillSound(targetid, castgfx3));
                    }
                }
            }
        }
    }

    private void deleteRepeatedSkills(L1Character cha) {
        final int[][] repeatedSkills = {
                // { HOLY_WEAPON, ENCHANT_WEAPON, BLESS_WEAPON, SHADOW_FANG },
                { FIRE_WEAPON, WIND_SHOT, STORM_EYE, BURNING_WEAPON, STORM_SHOT }, { SHIELD, EARTH_SKIN, IRON_SKIN },
                { HOLY_WALK, BLOOD_LUST, MOVING_ACCELERATION, WIND_WALK, STATUS_BRAVE, STATUS_ELFBRAVE },
                { HASTE, GREATER_HASTE, STATUS_HASTE }, { PHYSICAL_ENCHANT_DEX, NARUTO_THANKS_CANDY, DRESS_DEXTERITY },
                { PHYSICAL_ENCHANT_STR, DRESS_MIGHTY },
                // { GLOWING_AURA, SHINING_AURA },
                { FAFU_MAAN, ANTA_MAAN, LIND_MAAN, VALA_MAAN, LIFE_MAAN, BIRTH_MAAN, SHAPE_MAAN },
                { SCALES_EARTH_DRAGON, SCALES_WATER_DRAGON, SCALES_FIRE_DRAGON },
                { PAP_FIVEPEARLBUFF, PAP_MAGICALPEARLBUFF } };
        for (int[] skills : repeatedSkills) {
            for (int id : skills) {
                if (id == _skillId) {
                    stopSkillList(cha, skills);
                }
            }
        }
    }

    private void stopSkillList(L1Character cha, int[] repeat_skill) {
        for (int skillId : repeat_skill) {
            if (skillId != _skillId) {
                cha.removeSkillEffect(skillId);
            }
        }
    }

    private void setDelay() {
        int reuse = _skill.getReuseDelay();
        if (_player != null) {
            if (_player.isHaste()) {
                reuse -= 250;
            }
            if (_player.isBrave() || _player.isElfBrave()) {
                reuse -= 300;
            }
            reuse -= 300;
            if (reuse < 500) {
                reuse = 500;
            }
            if (_skill.getReuseDelay() > 0) {
                L1SkillDelay.onSkillUse(_user, reuse);
            }
        }
    }

    /**
     * アンタラスパプリオンMessage
     */
    private void MonsterMessage(int type) {
        String MonMessage = " ";
        if (type == 1) { // アンタラス
            switch (_skillId) {
            case ANTA_MESSAGE_1:
                MonMessage = "$7861";
                break;
            case ANTA_MESSAGE_2:
                MonMessage = "$7911";
                break;
            case ANTA_MESSAGE_3:
                MonMessage = "$7905";
                break;
            case ANTA_MESSAGE_4:
                MonMessage = "$7907";
                break;
            case ANTA_MESSAGE_5:
                MonMessage = "$7863";
                break;
            case ANTA_MESSAGE_6:
                MonMessage = "$7903";
                break;
            case ANTA_MESSAGE_7:
                MonMessage = "$7909";
                break;
            case ANTA_MESSAGE_8:
                MonMessage = "$7915";
                break;
            case ANTA_MESSAGE_9:
                MonMessage = "$7862";
                break;
            case ANTA_MESSAGE_10:
                MonMessage = "$7913";
                break;
            default:
                break;
            }
        } else if (type == 2) { // パプリオン
            switch (_skillId) {
            case PAP_PREDICATE1:
                MonMessage = "$8467";
                break;
            case PAP_PREDICATE3:
                MonMessage = "$8458";
                break;
            case PAP_PREDICATE5:
                MonMessage = "$8456";
                break;
            case PAP_PREDICATE6:
                MonMessage = "$8457";
                break;
            case PAP_PREDICATE7:
                MonMessage = "$8454";
                break;
            case PAP_PREDICATE8:
                MonMessage = "$8455";
                break;
            case PAP_PREDICATE9:
                MonMessage = "$8460";
                break;
            case PAP_PREDICATE11:
                MonMessage = "$8463";
                break;
            case PAP_PREDICATE12:
                MonMessage = "$8465";
                break;
            default:
                break;
            }
        }
        _user.broadcastPacket(new S_NpcChatPacket(_npc, MonMessage, 0));
        return;
    }

    private void runSkill() {
        if (_player != null && _player.isInvisble()) {
            if (_skill.getType() == L1Skills.TYPE_ATTACK || _skill.getType() == L1Skills.TYPE_CURSE
                    || _skill.getType() == L1Skills.TYPE_PROBABILITY) {
                _player.delInvis();
            }
        }

        if (_skillId == CUBE_IGNITION) {
            L1EffectInstance effect = L1EffectSpawn.getInstance().spawnEffect(200056, _skill.getBuffDuration() * 1000,
                    _user.getX(), _user.getY(), _user.getMapId());
            _player.setSkillEffect(CUBE_IGNITION, _skill.getBuffDuration() * 1000);
            effect.setCubeTime(4);
            effect.setCubePc(_player);
            L1Cube.getInstance().add(0, effect);
            L1PinkName.onAction(_player);
            return;
        }

        if (_skillId == CUBE_QUAKE) {
            L1EffectInstance effect = L1EffectSpawn.getInstance().spawnEffect(200057, _skill.getBuffDuration() * 1000,
                    _user.getX(), _user.getY(), _user.getMapId());
            _player.setSkillEffect(CUBE_QUAKE, _skill.getBuffDuration() * 1000);
            effect.setCubeTime(4);
            effect.setCubePc(_player);
            L1Cube.getInstance().add(1, effect);

            L1PinkName.onAction(_player);
            return;
        }

        if (_skillId == CUBE_SHOCK) {
            L1EffectInstance effect = L1EffectSpawn.getInstance().spawnEffect(200058, _skill.getBuffDuration() * 1000,
                    _user.getX(), _user.getY(), _user.getMapId());
            _player.setSkillEffect(CUBE_SHOCK, _skill.getBuffDuration() * 1000);
            effect.setCubeTime(4);
            effect.setCubePc(_player);
            L1Cube.getInstance().add(2, effect);

            L1PinkName.onAction(_player);
            return;
        }

        if (_skillId == CUBE_BALANCE) {
            L1EffectInstance effect = L1EffectSpawn.getInstance().spawnEffect(200059, _skill.getBuffDuration() * 1000,
                    _user.getX(), _user.getY(), _user.getMapId());
            _player.setSkillEffect(CUBE_BALANCE, _skill.getBuffDuration() * 1000);
            effect.setCubeTime(5);
            effect.setCubePc(_player);
            L1Cube.getInstance().add(3, effect);

            L1PinkName.onAction(_player);
            return;
        }

        if (_skillId == DISINTEGRATE && _target instanceof L1PcInstance) {
            if (_target.hasSkillEffect(ANTI_DISINTEGRATE)) {
                return;
            }

            _target.setSkillEffect(ANTI_DISINTEGRATE, 2000);
        }

        if (_skillId == METEOR_STRIKE && _target instanceof L1PcInstance) {
            if (_target.hasSkillEffect(ANTI_METEOR)) {
                return;
            }

            _target.setSkillEffect(ANTI_METEOR, 2000);
        }

        if (_skillId == FINAL_BURN && _target instanceof L1PcInstance) {
            if (_target.hasSkillEffect(ANTI_FINAL_BURN)) {
                return;
            }

            _target.setSkillEffect(ANTI_FINAL_BURN, 2000);
        }

        if (_skillId == LIFE_STREAM) {
            L1EffectSpawn.getInstance().spawnEffect(81169, _skill.getBuffDuration() * 1000, _targetX, _targetY,
                    _user.getMapId());

            return;
        }

        if (_skillId == FIRE_WALL) {
            L1EffectSpawn.getInstance().doSpawnFireWall(_user, _targetX, _targetY);
            return;
        }

        for (int skillId : EXCEPT_COUNTER_MAGIC) {
            if (_skillId == skillId) {
                _isCounterMagic = false;
                break;
            }
        }

        if (_skillId == SHOCK_STUN || _skillId == BONE_BREAK || _skillId == SMASH && _user instanceof L1PcInstance) {
            _target.onAction(_player);
        }

        if (!isTargetCalc(_target)) {
            return;
        }

        // 毒雲
        if (_skillId == DESERT_SKILL4) {
            EffectSpawn();
        }

        /** MonsterMessage Type 1: アンタラス、Type 2：パプリオン **/
        if (_skillId >= ANTA_MESSAGE_1 && _skillId <= ANTA_MESSAGE_10) {
            MonsterMessage(1);
        }
        if (_skillId >= PAP_PREDICATE1 && _skillId <= PAP_PREDICATE12) {
            MonsterMessage(2);
        }
        // if (_skillId >= RINDVIOR_WIND_SHACKLE && _skillId <=
        // RINDVIOR_SUMMON_MONSTER_CLOUD){ MonsterMessage(3); }
        try {
            TargetStatus ts = null;
            L1Character cha = null;
            int dmg = 0;
            int drainMana = 0;
            int heal = 0;
            boolean isSuccess = false;
            int undeadType = 0;

            for (Iterator<TargetStatus> iter = _targetList.iterator(); iter.hasNext();) {
                ts = null;
                cha = null;
                dmg = 0;
                heal = 0;
                isSuccess = false;
                undeadType = 0;

                ts = iter.next();
                cha = ts.getTarget();
                if (!ts.isCalc() || !isTargetCalc(cha)) {
                    continue;
                }

                L1Magic _magic = new L1Magic(_user, cha);
                _magic.setLeverage(getLeverage());

                if (cha instanceof L1MonsterInstance) {
                    undeadType = ((L1MonsterInstance) cha).getNpcTemplate().get_undead();
                }

                if ((_skill.getType() == L1Skills.TYPE_CURSE || _skill.getType() == L1Skills.TYPE_PROBABILITY)
                        && isTargetFailure(cha)) {
                    iter.remove();
                    continue;
                }

                if (cha instanceof L1PcInstance) {
                    if (_skillTime == 0) {
                        _getBuffIconDuration = _skill.getBuffDuration();
                    } else {
                        _getBuffIconDuration = _skillTime;
                    }
                }

                deleteRepeatedSkills(cha);

                if (_user instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) _user;
                    removeNewIcon(pc, _skillId);
                }
                if (_target instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) _target;
                    removeNewIcon(pc, _skillId);
                }
                if (_skill.getType() == L1Skills.TYPE_ATTACK && _user.getId() != cha.getId()) {
                    if (isUseCounterMagic(cha)) {
                        iter.remove();
                        continue;
                    }
                    dmg = _magic.calcMagicDamage(_skillId);

                    if (_magic.isCriticalDamage()) {
                        _isCriticalDamage = true;
                    } else {
                        _isCriticalDamage = false;
                    }

                    // 攻撃スキルの時！イレースかどうか板ミョルフ削除
                    if (_skillId != SHOCK_STUN && _skillId != TRIPLE_ARROW && _skillId != FOU_SLAYER) {
                        if (cha instanceof L1PcInstance) {
                            if (cha.hasSkillEffect(ERASE_MAGIC)) {
                                cha.killSkillEffectTimer(ERASE_MAGIC);
                                L1PcInstance pc = (L1PcInstance) cha;
                                pc.sendPackets(new S_PacketBox(S_PacketBox.ICON_AURA));
                            }
                        } else if (cha instanceof L1MonsterInstance) {
                            if (cha.hasSkillEffect(ERASE_MAGIC)) {
                                cha.killSkillEffectTimer(ERASE_MAGIC);
                            }
                        }
                    }
                } else if (_skill.getType() == L1Skills.TYPE_CURSE || _skill.getType() == L1Skills.TYPE_PROBABILITY) {
                    isSuccess = _magic.calcProbabilityMagic(_skillId);
                    // 七魔法ではなく、現在の七中であれば！
                    if (cha instanceof L1PcInstance && _user instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) cha;
                        L1PinkName.onAction(pc, _user);
                    }

                    if (_skillId != ERASE_MAGIC && _skillId != EARTH_BIND) {
                        if (cha instanceof L1PcInstance) {
                            if (cha.hasSkillEffect(ERASE_MAGIC)) {
                                cha.killSkillEffectTimer(ERASE_MAGIC);
                                L1PcInstance pc = (L1PcInstance) cha;
                                pc.sendPackets(new S_PacketBox(S_PacketBox.ICON_AURA));
                            }
                        } else if (cha instanceof L1MonsterInstance) {
                            if (cha.hasSkillEffect(ERASE_MAGIC)) {
                                cha.killSkillEffectTimer(ERASE_MAGIC);
                            }
                        }
                    }
                    if (_skillId != FOG_OF_SLEEPING) {
                        cha.removeSkillEffect(FOG_OF_SLEEPING);
                    }
                    if (_skillId != PHANTASM) {
                        cha.removeSkillEffect(PHANTASM);
                    }
                    if (isSuccess) {
                        if (isUseCounterMagic(cha)) {
                            iter.remove();
                            continue;
                        }
                    } else {
                        if (_skillId == FOG_OF_SLEEPING && cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_ServerMessage(297));
                        }
                        iter.remove();
                        continue;
                    }
                } else if (_skill.getType() == L1Skills.TYPE_HEAL) {
                    dmg = -1 * _magic.calcHealing(_skillId);
                    if (cha.hasSkillEffect(WATER_LIFE)) {
                        dmg *= 2;
                    }
                    if (cha.hasSkillEffect(POLLUTE_WATER)) {
                        dmg /= 2;
                    }
                    if (cha.hasSkillEffect(PAP_REDUCE_HELL)) {
                        dmg /= 2;
                    }
                    if (cha.hasSkillEffect(BLACKELDER_DEATH_HELL) || cha.hasSkillEffect(DEATH_HEAL)) {
                        dmg = -dmg;
                        if (cha.hasSkillEffect(WATER_LIFE)) {
                            dmg *= 2;
                        }
                        if (cha.hasSkillEffect(POLLUTE_WATER)) {
                            dmg /= 2;
                        }
                    }
                }

                if (cha.hasSkillEffect(_skillId) && _skillId != 228 && _skillId != SHOCK_STUN
                        && _skillId != THUNDER_GRAB && _skillId != OMAN_STUN && _skillId != ANTA_MESSAGE_6
                        && _skillId != ANTA_MESSAGE_7 && _skillId != ANTA_MESSAGE_8) {
                    addMagicList(cha, true);
                    if (_skillId != SHAPE_CHANGE) {
                        continue;
                    }
                }

                // ●●●● PC、NPC両方の効果があるスキル ●●●●
                // GFX Check (Made by HuntBoy)
                switch (_skillId) {
                case ASSASSIN: {
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) cha;
                        if (pc.hasSkillEffect(L1SkillId.ASSASSIN)) {
                            pc.removeSkillEffect(L1SkillId.ASSASSIN);
                        }
                        pc.setSkillEffect(L1SkillId.ASSASSIN, 15 * 1000);
                        pc.sendPackets(new S_NewSkillIcon(L1SkillId.ASSASSIN, true, 15));
                    }
                }
                    break;
                case DESTROY: {
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) cha;
                        if (pc.hasSkillEffect(L1SkillId.DESTROY)) {
                            pc.removeSkillEffect(L1SkillId.DESTROY);
                        }
                        pc.setSkillEffect(L1SkillId.DESTROY, 30 * 1000);
                        pc.sendPackets(new S_NewSkillIcon(L1SkillId.DESTROY, true, 30));
                    }
                }
                    break;
                case SOUL_BARRIER: {
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) cha;
                        if (pc.hasSkillEffect(L1SkillId.SOUL_BARRIER)) {
                            pc.removeSkillEffect(L1SkillId.SOUL_BARRIER);
                        }
                        pc.setSkillEffect(L1SkillId.SOUL_BARRIER, 600 * 1000);
                        pc.sendPackets(new S_NewSkillIcon(L1SkillId.SOUL_BARRIER, true, 600));
                    }
                }
                    break;
                case IMPACT: {
                    if (_user instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) _user;
                        if (_target instanceof L1PcInstance) {
                            L1PcInstance target = (L1PcInstance) _target;
                            if (target.hasSkillEffect(L1SkillId.IMPACT)) {
                                target.removeSkillEffect(L1SkillId.IMPACT);
                            }
                            target.setSkillEffect(L1SkillId.IMPACT, 15 * 1000);
                            target.sendPackets(new S_NewSkillIcon(L1SkillId.IMPACT, true, 15));
                            target.sendPackets(new S_SkillSound(target.getId(), 14513));
                            Broadcaster.broadcastPacket(target, new S_SkillSound(target.getId(), 14513));
                            int upskill = pc.getLevel() - 80;
                            if (upskill >= 5)
                                upskill = 5;
                            target.setImpactUp(5 + upskill);
                        }
                    }
                }
                    break;
                case TITANL_RISING: {
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) cha;
                        if (pc.hasSkillEffect(L1SkillId.TITANL_RISING)) {
                            pc.removeSkillEffect(L1SkillId.TITANL_RISING);
                        }
                        pc.setSkillEffect(L1SkillId.TITANL_RISING, 2400 * 1000);
                        pc.sendPackets(new S_NewSkillIcon(L1SkillId.TITANL_RISING, true, 2400));
                        int upHP = pc.getLevel() - 80;
                        if (upHP >= 5)
                            upHP = 5;
                        pc.setRisingUp(5 + upHP);
                    }
                }
                    break;
                case ABSOLUTE_BLADE: {
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) cha;
                        if (pc.hasSkillEffect(L1SkillId.ABSOLUTE_BLADE)) {
                            pc.removeSkillEffect(L1SkillId.ABSOLUTE_BLADE);
                        }
                        pc.setSkillEffect(L1SkillId.ABSOLUTE_BLADE, 8 * 1000);
                        pc.sendPackets(new S_NewSkillIcon(L1SkillId.ABSOLUTE_BLADE, true, 8));
                    }
                }
                    break;
                case DEATH_HEAL: {
                    if (_target instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) _target;
                        if (pc.hasSkillEffect(DEATH_HEAL)) {
                            pc.sendPackets(new S_NewSkillIcon(DEATH_HEAL, false, -1));
                            pc.removeSkillEffect(DEATH_HEAL);
                        }
                        int chance = random.nextInt(10) + 1;
                        pc.sendPackets(new S_NewSkillIcon(DEATH_HEAL, true, chance));
                        pc.setSkillEffect(DEATH_HEAL, chance * 1000);
                        pc.sendPackets(new S_SkillSound(pc.getId(), 14501));
                        Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 14501));
                    }
                }
                    break;
                case GRACE_AVATAR: {
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) cha;
                        if (pc.hasSkillEffect(L1SkillId.GRACE_AVATAR)) {
                            pc.sendPackets(new S_NewSkillIcon(L1SkillId.GRACE_AVATAR, false, -1));
                            pc.removeSkillEffect(L1SkillId.GRACE_AVATAR);
                        }
                        pc.setGraceLv(pc.getLevel());
                        pc.getResistance().addHold(10 + pc.getGraceLv()); // ホールド耐性（自分）
                        pc.getResistance().addStun(10 + pc.getGraceLv()); // スタン耐性（自分）
                        pc.getResistance().addDESPERADO(10 + pc.getGraceLv()); // 恐怖耐性（自分）
                        pc.setSkillEffect(L1SkillId.GRACE_AVATAR, 15 * 1000);
                        pc.sendPackets(new S_NewSkillIcon(L1SkillId.GRACE_AVATAR, true, 15));
                        pc.sendPackets(new S_SkillSound(pc.getId(), 14495));
                        Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 14495));
                        for (L1PcInstance player : L1World.getInstance().getVisiblePlayer(pc, 18)) {// 18セル
                            if (pc.getParty() != null) {
                                if (pc.getParty().isMember(player) && player != null) {
                                    if (player.hasSkillEffect(L1SkillId.GRACE_AVATAR)) {
                                        player.sendPackets(new S_NewSkillIcon(L1SkillId.GRACE_AVATAR, false, -1));
                                        player.removeSkillEffect(L1SkillId.GRACE_AVATAR);
                                    }
                                    player.setGraceLv(pc.getLevel());
                                    player.getResistance().addHold(10 + player.getGraceLv()); // ホールド耐性（パーティーメンバー）
                                    player.getResistance().addStun(10 + player.getGraceLv()); // スタン耐性（パーティーメンバー）
                                    player.getResistance().addDESPERADO(10 + player.getGraceLv()); // 恐怖耐性（パーティーメンバー）
                                    player.sendPackets(new S_NewSkillIcon(GRACE_AVATAR, true, 15));
                                    player.setSkillEffect(L1SkillId.GRACE_AVATAR, 15 * 1000);
                                    player.sendPackets(new S_ServerMessage(4734));// パーティーメンバーグレースアバター効果メッセージ
                                }
                            }
                        }
                    }
                }
                    break;
                case IMMUNE_TO_HARM:
                case IllUSION_OGRE:
                case IllUSION_LICH:
                case IllUSION_DIAMONDGOLEM:
                case CONCENTRATION:
                case PATIENCE: {
                    if (_user instanceof L1PcInstance) {
                        L1PinkName.onHelp(cha, _user);
                    }
                }
                    break;

                case HASTE: {
                    if (cha.getMoveSpeed() != 2) {
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            if (pc.getHasteItemEquipped() > 0) {
                                continue;
                            }
                            pc.setDrink(false);
                            pc.sendPackets(new S_SkillHaste(pc.getId(), 1, _getBuffIconDuration));
                        }
                        cha.broadcastPacket(new S_SkillHaste(cha.getId(), 1, 0));
                        cha.setMoveSpeed(1);
                    } else {
                        int skillNum = 0;
                        if (cha.hasSkillEffect(SLOW)) {
                            skillNum = SLOW;
                        } else if (cha.hasSkillEffect(GREATER_SLOW)) {
                            skillNum = GREATER_SLOW;
                        } else if (cha.hasSkillEffect(ENTANGLE)) {
                            skillNum = ENTANGLE;
                        } else if (cha.hasSkillEffect(MOB_SLOW_1)) {
                            skillNum = MOB_SLOW_1;
                        } else if (cha.hasSkillEffect(MOB_SLOW_18)) {
                            skillNum = MOB_SLOW_18;
                        }
                        if (skillNum != 0) {
                            cha.removeSkillEffect(skillNum);
                            cha.removeSkillEffect(HASTE);
                            cha.setMoveSpeed(0);
                            continue;
                        }
                    }
                }
                    break;
                case CURE_POISON: {
                    cha.curePoison();
                }
                    break;
                case DRESS_EVASION:// 12
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.Add_Er(18);
                        pc.sendPackets(new S_PacketBox(S_PacketBox.ER_UpDate, pc.get_PlusEr()));
                    }
                    break;
                case AQUA_PROTECTER:// 5
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.Add_Er(5);
                        pc.sendPackets(new S_PacketBox(S_PacketBox.ER_UpDate, pc.get_PlusEr()));
                    }
                    break;
                case SOLID_CARRIAGE:// 15
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.Add_Er(15);
                        pc.sendPackets(new S_PacketBox(S_PacketBox.ER_UpDate, pc.get_PlusEr()));
                    }
                    break;
                /*
                 * case STRIKER_GALE:// ソース if (cha instanceof L1PcInstance) {
                 * L1PcInstance pc = (L1PcInstance) cha; // pc.Add_Er(-99);
                 * pc.sendPackets(new S_PacketBox(S_PacketBox.ER_UpDate,
                 * pc.get_PlusEr())); pc.sendPackets(new S_OwnCharStatus(pc)); }
                 * break;
                 */
                case STRIKER_GALE:// ゲイルリアルタイム
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) cha;
                        int er = 0;
                        er += pc.get_Er();
                        er += pc.getAdd_Er();
                        if (er <= 0) {
                            er = 0;
                        } else {
                            er = (int) er / 3;
                        }
                        pc.sendPackets(new S_PacketBox(S_PacketBox.ER_UpDate, er), true);
                    }
                    break; // 修正
                case REMOVE_CURSE: {
                    cha.curePoison();
                    if (cha.hasSkillEffect(STATUS_CURSE_PARALYZING) || cha.hasSkillEffect(STATUS_CURSE_PARALYZED)
                            || cha.hasSkillEffect(ANTA_MESSAGE_1) || cha.hasSkillEffect(ANTA_MESSAGE_6)
                            || cha.hasSkillEffect(ANTA_MESSAGE_7) || cha.hasSkillEffect(ANTA_MESSAGE_8)
                            || cha.hasSkillEffect(OMAN_STUN)) {
                        cha.cureParalaysis();
                    }
                    if (cha.hasSkillEffect(CURSE_BLIND) || cha.hasSkillEffect(DARKNESS)) {
                        if (cha.hasSkillEffect(CURSE_BLIND)) {
                            cha.removeSkillEffect(CURSE_BLIND);
                        } else if (cha.hasSkillEffect(DARKNESS)) {
                            cha.removeSkillEffect(DARKNESS);
                        }
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_CurseBlind(0));
                        }
                    }
                }
                    break;
                case RESURRECTION:
                case GREATER_RESURRECTION: {
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) cha;
                        if (_player.getId() != pc.getId()) {
                            if (L1World.getInstance().getVisiblePlayer(pc, 0).size() > 0) {
                                for (L1PcInstance visiblePc : L1World.getInstance().getVisiblePlayer(pc, 0)) {
                                    if (!visiblePc.isDead()) {
                                        _player.sendPackets(new S_ServerMessage(592));
                                        return;
                                    }
                                }
                            }
                            /** ボール成長は復活できないように **/
                            int castle_id = L1CastleLocation.getCastleIdByArea(pc);
                            if (castle_id != 0) {
                                pc.sendPackets(new S_SystemMessage("利用できない地域です。"));
                                return;
                            }
                            /** ボール成長は復活できないように **/
                            if (pc.getCurrentHp() == 0 && pc.isDead()) {
                                if (pc.getMap().isUseResurrection()) {
                                    if (_skillId == RESURRECTION) {
                                        pc.setGres(false);
                                    } else if (_skillId == GREATER_RESURRECTION) {
                                        pc.setGres(true);
                                    }
                                    pc.setTempID(_player.getId());
                                    pc.sendPackets(new S_Message_YN(322, ""));
                                }
                            }
                        }
                    }
                    if (cha instanceof L1NpcInstance) {
                        if (!(cha instanceof L1TowerInstance)) {
                            L1NpcInstance npc = (L1NpcInstance) cha;
                            if (npc.getNpcTemplate().isCantResurrect() && !(npc instanceof L1PetInstance)) {
                                return;
                            }
                            if (npc instanceof L1PetInstance
                                    && L1World.getInstance().getVisiblePlayer(npc, 0).size() > 0) {
                                for (L1PcInstance visiblePc : L1World.getInstance().getVisiblePlayer(npc, 0)) {
                                    if (!visiblePc.isDead()) {
                                        _player.sendPackets(new S_ServerMessage(592));
                                        return;
                                    }
                                }
                            }
                            if (npc.getCurrentHp() == 0 && npc.isDead()) {
                                npc.resurrect(npc.getMaxHp() / 4);
                                npc.setResurrect(true);
                            }
                        }
                    }
                }
                    break;
                case CALL_OF_NATURE: {
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) cha;
                        if (_player.getId() != pc.getId()) {
                            if (L1World.getInstance().getVisiblePlayer(pc, 0).size() > 0) {
                                for (L1PcInstance visiblePc : L1World.getInstance().getVisiblePlayer(pc, 0)) {
                                    if (!visiblePc.isDead()) {
                                        _player.sendPackets(new S_ServerMessage(592));
                                        return;
                                    }
                                }
                            }
                            if (pc.getCurrentHp() == 0 && pc.isDead()) {
                                pc.setTempID(_player.getId());
                                pc.sendPackets(new S_Message_YN(322, ""));
                            }
                        }
                    }
                    if (cha instanceof L1NpcInstance) {
                        if (!(cha instanceof L1TowerInstance)) {
                            L1NpcInstance npc = (L1NpcInstance) cha;
                            if (npc instanceof L1PetInstance
                                    && L1World.getInstance().getVisiblePlayer(npc, 0).size() > 0) {
                                for (L1PcInstance visiblePc : L1World.getInstance().getVisiblePlayer(npc, 0)) {
                                    if (!visiblePc.isDead()) {
                                        _player.sendPackets(new S_ServerMessage(592));
                                        return;
                                    }
                                }
                            }
                            if (npc.getCurrentHp() == 0 && npc.isDead()) {
                                npc.resurrect(cha.getMaxHp());
                                npc.resurrect(cha.getMaxMp() / 100);
                                npc.setResurrect(true);
                            }
                        }
                    }
                }
                    break;
                // UI DG表示
                case UNCANNY_DODGE: // アンキャニードッジ
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.addDg(-8);
                    }
                    break;
                // UI DG表示
                case DETECTION:
                case IZE_BREAK:
                case EYE_OF_DRAGON: {
                    if (cha instanceof L1NpcInstance) {
                        L1NpcInstance npc = (L1NpcInstance) cha;
                        int hiddenStatus = npc.getHiddenStatus();
                        if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_SINK) {
                            npc.appearOnGround(_player);
                        }
                    }
                }
                    break;

                case COUNTER_DETECTION: {
                    if (cha instanceof L1PcInstance) {
                        dmg = _magic.calcMagicDamage(_skillId);
                    } else if (cha instanceof L1NpcInstance) {
                        L1NpcInstance npc = (L1NpcInstance) cha;
                        int hiddenStatus = npc.getHiddenStatus();
                        if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_SINK) {
                            npc.appearOnGround(_player);
                        } else {
                            dmg = 0;
                        }
                    } else {
                        dmg = 0;
                    }
                }
                    break;
                case MIND_BREAK: {
                    if (_target.getCurrentMp() >= 5) {
                        _target.setCurrentMp(_target.getCurrentMp() - 5);
                        dmg = 15;
                    } else {
                        return;
                    }
                }
                    break;
                case TRUE_TARGET: {
                    if (_user instanceof L1PcInstance) {
                        L1PcInstance pri = (L1PcInstance) _user;
                        pri.sendPackets(new S_TrueTargetNew(_targetID, true));
                        if (_target instanceof L1PcInstance) {
                            int step = pri.getLevel() / 15;
                            L1PcInstance target = (L1PcInstance) _target;
                            if (step > 0) {
                                target.setTrueTarget(step);
                            }
                        }
                        for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(_target)) {
                            if (pri.getClanid() == pc.getClanid()) {
                                pc.sendPackets(new S_TrueTargetNew(_targetID, true));
                            }
                        }
                        // 以前に詠唱したトゥルーターゲット探し強制終了させる。
                        synchronized (_truetarget_list) {
                            L1Object temp = _truetarget_list.remove(_user.getId());
                            if (temp != null && temp instanceof L1Character) {
                                L1Character temp2 = (L1Character) temp;
                                temp2.removeSkillEffect(L1SkillId.TRUE_TARGET);
                            }
                        }
                        // トゥルーターゲット活性化。
                        _target.setSkillEffect(L1SkillId.TRUE_TARGET, 16 * 1000);
                        synchronized (_truetarget_list) {
                            _truetarget_list.put(_user.getId(), _target);
                        }
                    }
                }
                    break;
                case ELEMENTAL_FALL_DOWN: {
                    if (_user instanceof L1PcInstance) {
                        int playerAttr = _player.getElfAttr();
                        int i = -50;
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            switch (playerAttr) {
                            case 0:
                                _player.sendPackets(new S_ServerMessage(79));
                                break;
                            case 1:
                                pc.getResistance().addEarth(i);
                                pc.setAddAttrKind(1);
                                break;
                            case 2:
                                pc.getResistance().addFire(i);
                                pc.setAddAttrKind(2);
                                break;
                            case 4:
                                pc.getResistance().addWater(i);
                                pc.setAddAttrKind(4);
                                break;
                            case 8:
                                pc.getResistance().addWind(i);
                                pc.setAddAttrKind(8);
                                break;
                            default:
                                break;
                            }
                        } else if (cha instanceof L1MonsterInstance) {
                            L1MonsterInstance mob = (L1MonsterInstance) cha;
                            switch (playerAttr) {
                            case 0:
                                _player.sendPackets(new S_ServerMessage(79));
                                break;
                            case 1:
                                mob.getResistance().addEarth(i);
                                mob.setAddAttrKind(1);
                                break;
                            case 2:
                                mob.getResistance().addFire(i);
                                mob.setAddAttrKind(2);
                                break;
                            case 4:
                                mob.getResistance().addWater(i);
                                mob.setAddAttrKind(4);
                                break;
                            case 8:
                                mob.getResistance().addWind(i);
                                mob.setAddAttrKind(8);
                                break;
                            default:
                                break;
                            }
                        }
                    }
                }
                    break;
                case HEAL:
                case EXTRA_HEAL:
                case GREATER_HEAL:
                case FULL_HEAL:
                case HEAL_ALL:
                case NATURES_TOUCH:
                case NATURES_BLESSING: {
                    if (cha instanceof L1PcInstance) {
                        cha.killSkillEffectTimer(WATER_LIFE);
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.sendPackets(new S_PacketBox(S_PacketBox.DEL_ICON));
                    }
                }
                    break;
                case CHILL_TOUCH:
                case VAMPIRIC_TOUCH: {
                    heal = dmg / 2;
                }
                    break;
                case TRIPLE_ARROW: {// トリプル
                    int playerGFX = _player.getTempCharGfx();
                    int weaponType = _player.getWeapon().getItem().getType1();
                    if (weaponType != 20 || playerGFX == 3784)
                        return;
                    for (int i = 3; i > 0; i--) {
                        if (_target instanceof L1PcInstance) {
                            L1PcInstance s = (L1PcInstance) _target;
                            s.TRIPLE = true;
                        }
                        _target.onAction(_player);
                    }
                    _player.sendPackets(new S_SkillSound(_player.getId(), 11764));
                    Broadcaster.broadcastPacket(_player, new S_SkillSound(_player.getId(), 11764));
                }
                    break;
                case 50011: { // モンスタートリプル
                    if (_user instanceof L1NpcInstance) {
                        for (int i = 3; i > 0; i--) {
                            if (_target instanceof L1PcInstance) {
                                L1PcInstance target = (L1PcInstance) _target;
                                target.onAction(_npc);
                                Broadcaster.broadcastPacket(_user, new S_SkillSound(_user.getId(), 7020));
                                Broadcaster.broadcastPacket(_user, new S_SkillSound(_user.getId(), 11764));
                            }
                        }
                    }

                }
                    break;
                case FOU_SLAYER: { // ポースレイヤー
                    if (_player.getWeapon() == null) {
                        return;
                    }
                    int weapontype = _player.getWeapon().getItem().getType1();
                    if (weapontype != 4 && weapontype != 11 && weapontype != 24 && weapontype != 50) {
                        return;
                    }
                    for (int i = 3; i > 0; i--) {
                        if (_target instanceof L1PcInstance) {
                            L1PcInstance s = (L1PcInstance) _target;
                            s.FouSlayer = true;
                        }
                        _target.onAction(_player);
                    }
                    _player.sendPackets(new S_SkillSound(_player.getId(), 7020));
                    _player.sendPackets(new S_SkillSound(_targetID, 6509));
                    Broadcaster.broadcastPacket(_player, new S_SkillSound(_player.getId(), 7020));
                    Broadcaster.broadcastPacket(_player, new S_SkillSound(_targetID, 6509));
                    if (_player.hasSkillEffect(CHAINSWORD1)) {
                        dmg += 15;
                        _player.killSkillEffectTimer(CHAINSWORD1);
                        _player.sendPackets(new S_PacketBox(S_PacketBox.SPOT, 0)); // 追加
                    }
                    if (_player.hasSkillEffect(CHAINSWORD2)) {
                        dmg += 30;
                        _player.killSkillEffectTimer(CHAINSWORD2);
                        _player.sendPackets(new S_PacketBox(S_PacketBox.SPOT, 0)); // 追加
                    }
                    if (_player.hasSkillEffect(CHAINSWORD3)) {
                        _player.killSkillEffectTimer(CHAINSWORD3);
                        _player.sendPackets(new S_PacketBox(S_PacketBox.SPOT, 0)); // 追加
                        dmg += 45;
                    }
                    for (L1DollInstance doll : _player.getDollList()) {
                        dmg += doll.fou_DamageUp();
                    }
                }
                    break;
                case Sand_worms: { // サンドワームイラプション
                    L1PcInstance pc = (L1PcInstance) _player;
                    S_UseAttackSkill packet = new S_UseAttackSkill(_user, _player.getId(), 10145, _player.getX(),
                            _player.getY(), ActionCodes.ACTION_Attack, false);
                    Broadcaster.broadcastPacket(_user, packet);
                    Broadcaster.broadcastPacket(pc, packet);
                    pc.sendPackets(new S_SkillSound(pc.getId(), 10145));
                }
                    break;
                case Sand_worms1: { // サンドワーム範囲攻撃1
                    L1PcInstance pc = (L1PcInstance) _player;
                    S_UseAttackSkill packet = new S_UseAttackSkill(_user, _player.getId(), 10195, _player.getX(),
                            _player.getY(), ActionCodes.ACTION_Attack, false);
                    Broadcaster.broadcastPacket(_user, packet);
                    Broadcaster.broadcastPacket(pc, packet);
                    pc.sendPackets(new S_SkillSound(pc.getId(), 10195));
                }
                    break;
                case Sand_worms2: { // サンドワーム範囲攻撃1
                    L1PcInstance pc = (L1PcInstance) _player;
                    S_UseAttackSkill packet = new S_UseAttackSkill(_user, _player.getId(), 10194, _player.getX(),
                            _player.getY(), ActionCodes.ACTION_Attack, false);
                    Broadcaster.broadcastPacket(_user, packet);
                    Broadcaster.broadcastPacket(pc, packet);
                    pc.sendPackets(new S_SkillSound(pc.getId(), 10194));
                }
                    break;
                case Sand_worms3: { // サンドワーム範囲攻撃1
                    L1PcInstance pc = (L1PcInstance) _player;
                    S_UseAttackSkill packet = new S_UseAttackSkill(_user, _player.getId(), 10191, _player.getX(),
                            _player.getY(), ActionCodes.ACTION_Attack, false);
                    Broadcaster.broadcastPacket(_user, packet);
                    Broadcaster.broadcastPacket(pc, packet);
                    pc.sendPackets(new S_SkillSound(pc.getId(), 10191));
                }
                    break;

                /** 血盟バフ **/
                case CLAN_BUFF1: {// 通常攻撃態勢
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addDmgupByArmor(2);
                    pc.addBowDmgupByArmor(2);
                    pc.sendPackets(new S_ACTION_UI2(2724, pc.getClan().getBuffTime()[pc.getClan().getBless() - 1], 7,
                            7233, 4650));
                    pc.sendPackets(new S_ServerMessage(4618, "$22503"));
                    pc.sendPackets(new S_SkillSound(pc.getId(), 14482));
                }
                    break;
                case CLAN_BUFF2: {// 一般防御態勢
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.getAC().addAc(-3);
                    pc.sendPackets(new S_OwnCharAttrDef(pc));
                    pc.sendPackets(new S_ACTION_UI2(2725, pc.getClan().getBuffTime()[pc.getClan().getBless() - 1], 7,
                            7235, 4651));
                    pc.sendPackets(new S_ServerMessage(4618, "$22504"));
                    pc.sendPackets(new S_SkillSound(pc.getId(), 14482));
                }
                    break;
                case CLAN_BUFF3: {// 戦闘攻撃態勢
                    L1PcInstance pc = (L1PcInstance) cha;// 別にないとはこのようにしても良い。
                    // pc.addPvPDmgup(1); //pvp追加ダメージ
                    pc.sendPackets(new S_ACTION_UI2(2726, pc.getClan().getBuffTime()[pc.getClan().getBless() - 1], 7,
                            7237, 4652));
                    pc.sendPackets(new S_ServerMessage(4618, "$22505"));
                    pc.sendPackets(new S_SkillSound(pc.getId(), 14482));
                }
                    break;
                case CLAN_BUFF4: {// 戦闘防御態勢
                    L1PcInstance pc = (L1PcInstance) cha;
                    // pc.addDmgReducPvp(1); //pvpリドク
                    pc.sendPackets(new S_ACTION_UI2(2727, pc.getClan().getBuffTime()[pc.getClan().getBless() - 1], 7,
                            7239, 4653));
                    pc.sendPackets(new S_ServerMessage(4618, "$22506"));
                    pc.sendPackets(new S_SkillSound(pc.getId(), 14482));
                }
                    break;
                case 10026:
                case 10027:
                case 10028:
                case 10029: {
                    if (_user instanceof L1NpcInstance) {
                        _user.broadcastPacket(new S_NpcChatPacket(_npc, "$3717", 0));
                    } else {
                        _player.broadcastPacket(new S_ChatPacket(_player, "$3717", 0, 0));
                    }
                    dmg = cha.getCurrentHp();
                }
                    break;
                case 10057: {
                    new L1Teleport().teleportToTargetFront(cha, _user, 1);
                }
                    break;
                case SLOW:
                case GREATER_SLOW:
                case ENTANGLE:
                case MOB_SLOW_1:
                case MOB_SLOW_18: {
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) cha;
                        if (pc.getHasteItemEquipped() > 0) {
                            continue;
                        }

                    }
                    if (cha.getMoveSpeed() == 0) {
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_SkillHaste(pc.getId(), 2, _getBuffIconDuration));
                        }
                        cha.broadcastPacket(new S_SkillHaste(cha.getId(), 2, _getBuffIconDuration));
                        cha.setMoveSpeed(2);
                    } else if (cha.getMoveSpeed() == 1) {
                        int skillNum = 0;
                        if (cha.hasSkillEffect(HASTE)) {
                            skillNum = HASTE;
                        } else if (cha.hasSkillEffect(GREATER_HASTE)) {
                            skillNum = GREATER_HASTE;
                        } else if (cha.hasSkillEffect(STATUS_HASTE)) {
                            skillNum = STATUS_HASTE;
                        }
                        if (skillNum != 0) {
                            cha.removeSkillEffect(skillNum);
                            cha.killSkillEffectTimer(skillNum);
                            cha.removeSkillEffect(_skillId);
                            ((L1PcInstance) cha).sendPackets(new S_SkillHaste(cha.getId(), 1, 0));
                            cha.setMoveSpeed(0);
                            continue;
                        }
                    }
                }
                    break;
                case CURSE_BLIND:
                case DARKNESS: {
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) cha;
                        if (pc.hasSkillEffect(STATUS_FLOATING_EYE)) {
                            pc.sendPackets(new S_CurseBlind(2));
                        } else {
                            pc.sendPackets(new S_CurseBlind(1));
                        }
                    }
                }
                    break;
                case CURSE_POISON:
                    L1DamagePoison.doInfection(_user, cha, 3000, 5, false);
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.sendPackets(new S_PacketBox(S_PacketBox.POSION_ICON, pc, 1, 30));
                    }
                    break;
                case TOMAHAWK: { // トマホーク持続時間、出血状態になってダメージを受ける。レベル* 2/6
                    if (cha.hasSkillEffect(TOMAHAWK)) {
                        _player.sendPackets(new S_ServerMessage(280));
                        return;
                    }
                    dmg = ((_user.getLevel() + 2) / 7);
                    boolean f = _magic.calcProbabilityMagic(_skillId);
                    if (f) {
                        new L1TomaHaekDmg(_user, cha, ((_user.getLevel() + 2) / 7));
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            // pc.sendPackets(new S_ServerMessage(3992));
                            pc.sendPackets(new S_PacketBox(S_PacketBox.UNLIMITED_ICON1, 570, true));
                        }

                    } else {
                        _player.sendPackets(new S_ServerMessage(280));
                    }
                }
                    break;
                case CURSE_PARALYZE:
                case CURSE_PARALYZE2:
                case MOB_CURSEPARALYZ_18:
                case MOB_CURSEPARALYZ_19: {
                    if (!cha.hasSkillEffect(EARTH_BIND) && !cha.hasSkillEffect(ICE_LANCE)) {
                        if (cha instanceof L1PcInstance) {
                            L1CurseParalysis.curse(cha, 7000, 3000);
                        } else if (cha instanceof L1MonsterInstance) {
                            if (cha.getMaxHp() < 4300) {
                                L1CurseParalysis.curse(cha, 0, 3000);
                            }
                        }
                    }
                }
                    break;
                case WEAKNESS:
                case MOB_WEAKNESS_1: {
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.addDmgup(-5);
                        pc.addHitup(-1);
                    }
                }
                    break;
                case DISEASE:
                case MOB_DISEASE_1:
                case MOB_DISEASE_30: {
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.addDmgup(-6);
                        pc.getAC().addAc(12);
                    }
                }
                    break;
                case GUARD_BREAK: {
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.getAC().addAc(10);
                    }
                }
                    break;
                case HORROR_OF_DEATH: {
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.getAbility().addAddedStr((byte) -3);
                        pc.getAbility().addAddedInt((byte) -3);
                    }
                }
                    break;
                case PANIC: {
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.getAbility().addAddedStr((byte) -1);
                        pc.getAbility().addAddedDex((byte) -1);
                        pc.getAbility().addAddedCon((byte) -1);
                        pc.getAbility().addAddedInt((byte) -1);
                        pc.getAbility().addAddedWis((byte) -1);
                        pc.getAbility().addAddedCha((byte) -1);
                        pc.resetBaseMr();
                    }
                }
                    break;
                case ICE_LANCE: {
                    _isFreeze = _magic.calcProbabilityMagic(_skillId);
                    if (_isFreeze) {
                        int time = _skill.getBuffDuration() * 1000;
                        L1EffectSpawn.getInstance().spawnEffect(81168, time, cha.getX(), cha.getY(), cha.getMapId());
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_Poison(pc.getId(), 2));
                            pc.broadcastPacket(new S_Poison(pc.getId(), 2));
                            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, true));
                        } else if (cha instanceof L1MonsterInstance || cha instanceof L1SummonInstance
                                || cha instanceof L1PetInstance) {
                            L1NpcInstance npc = (L1NpcInstance) cha;
                            npc.broadcastPacket(new S_Poison(npc.getId(), 2));
                            npc.setParalyzed(true);
                        }
                    }
                }
                    break;
                /** 語パンツの中の時間本サーバー化 **/
                case EARTH_BIND: {// 語パンツの中の時間

                    int[] ebTimeArray = { 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000, 11000, 12000 };
                    _shockStunDuration = ebTimeArray[random.nextInt(ebTimeArray.length)];

                    if (cha instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) cha;
                        // pc.setSkillEffect(L1SkillId.EARTH_BIND,
                        // _earthBindDuration);
                        pc.sendPackets(new S_Poison(pc.getId(), 2));
                        pc.broadcastPacket(new S_Poison(pc.getId(), 2));
                        pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, true));
                    } else if (cha instanceof L1MonsterInstance || cha instanceof L1SummonInstance
                            || cha instanceof L1PetInstance) {
                        L1NpcInstance npc = (L1NpcInstance) cha;
                        npc.broadcastPacket(new S_Poison(npc.getId(), 2));
                        npc.setParalyzed(true);
                        npc.setParalysisTime(_shockStunDuration);
                    }
                }
                    break;
                case MOB_BASILL:
                case MOB_COCA: {
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) cha;
                        if (cha.hasSkillEffect(L1SkillId.STATUS_CURSE_PARALYZING)
                                || cha.hasSkillEffect(L1SkillId.STATUS_CURSE_PARALYZED)) {
                            pc.cureParalaysis();
                        }
                        pc.sendPackets(new S_Poison(pc.getId(), 2));
                        pc.broadcastPacket(new S_Poison(pc.getId(), 2));
                        pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, true));
                    } else if (cha instanceof L1MonsterInstance || cha instanceof L1SummonInstance
                            || cha instanceof L1PetInstance) {
                        L1NpcInstance npc = (L1NpcInstance) cha;
                        npc.broadcastPacket(new S_Poison(npc.getId(), 2));
                        npc.setParalyzed(true);
                    }
                }
                    break;

                case SHOCK_STUN: {// スタン確率
                    int targetLevel = 0;
                    int diffLevel = 0;

                    if (cha instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) cha;
                        targetLevel = pc.getLevel();
                    } else if (cha instanceof L1MonsterInstance || cha instanceof L1SummonInstance
                            || cha instanceof L1PetInstance) {
                        L1NpcInstance npc = (L1NpcInstance) cha;
                        targetLevel = npc.getLevel();
                    }

                    diffLevel = _user.getLevel() - targetLevel;

                    if (diffLevel < -5) {
                        int[] stunTimeArray = { 600, 1000, 1400, 1800, 2200, 2600, 3000, 3400 };
                        _shockStunDuration = stunTimeArray[random.nextInt(stunTimeArray.length)];
                    } else if (diffLevel >= -5 && diffLevel <= -3) {
                        int[] stunTimeArray = { 800, 1200, 1600, 2000, 2400, 2800, 3200, 3600 };
                        _shockStunDuration = stunTimeArray[random.nextInt(stunTimeArray.length)];
                    } else if (diffLevel >= -2 && diffLevel <= 2) {
                        int[] stunTimeArray = { 1000, 1400, 1800, 2200, 2600, 3000, 3400, 3800 };
                        _shockStunDuration = stunTimeArray[random.nextInt(stunTimeArray.length)];
                    } else if (diffLevel >= 3 && diffLevel <= 5) {
                        int[] stunTimeArray = { 1200, 1600, 2000, 2400, 2800, 3200, 3600, 4000 };
                        _shockStunDuration = stunTimeArray[random.nextInt(stunTimeArray.length)];
                    } else if (diffLevel >= 5 && diffLevel <= 10) {
                        int[] stunTimeArray = { 1400, 1800, 2200, 2600, 3000, 3400, 3800, 4200 };
                        _shockStunDuration = stunTimeArray[random.nextInt(stunTimeArray.length)];
                    } else if (diffLevel > 10) {
                        int[] stunTimeArray = { 1600, 2000, 2400, 2800, 3200, 3600, 4000, 4400 };
                        _shockStunDuration = stunTimeArray[random.nextInt(stunTimeArray.length)];
                    }
                    L1EffectSpawn.getInstance().spawnEffect(81162, _shockStunDuration, cha.getX(), cha.getY(),
                            cha.getMapId());
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, true));
                    } else if (cha instanceof L1MonsterInstance || cha instanceof L1SummonInstance
                            || cha instanceof L1PetInstance) {
                        L1NpcInstance npc = (L1NpcInstance) cha;
                        npc.setParalyzed(true);
                        npc.setParalysisTime(_shockStunDuration);
                    }
                }
                    break;
                case MOB_RANGESTUN_18:
                case MOB_RANGESTUN_19:
                case MOB_SHOCKSTUN_30: {

                    int levelDiff = _user.getLevel() - cha.getLevel();
                    int duration = 2250 + levelDiff * 80;

                    duration += random.nextInt(1600) - 800;

                    if (duration < 1000) {
                        duration = 1000;
                    } else if (duration > 5000) {
                        duration = 5000;
                    }

                    _shockStunDuration = duration;

                    L1EffectSpawn.getInstance().spawnEffect(81162, _shockStunDuration, cha.getX(), cha.getY(),
                            cha.getMapId());
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, true));
                    } else if (cha instanceof L1MonsterInstance || cha instanceof L1SummonInstance
                            || cha instanceof L1PetInstance) {
                        L1NpcInstance npc = (L1NpcInstance) cha;
                        npc.setParalyzed(true);
                    }
                }
                    break;

                case THUNDER_GRAB: {
                    _isFreeze = _magic.calcProbabilityMagic(_skillId);
                    if (_isFreeze) {
                        // int time = _skill.getBuffDuration() * 1000;
                        int[] grabTime = { 1000, 2000, 3000, 4000 };
                        int rnd = random.nextInt(grabTime.length);
                        int time = grabTime[rnd]; // 時間ランダムに
                        L1EffectSpawn.getInstance().spawnEffect(81182, time, cha.getX(), cha.getY(), cha.getMapId());
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.setSkillEffect(L1SkillId.STATUS_FREEZE, time);
                            pc.sendPackets(new S_SkillSound(pc.getId(), 4184));
                            pc.broadcastPacket(new S_SkillSound(pc.getId(), 4184));
                            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, true));
                        } else if (cha instanceof L1MonsterInstance || cha instanceof L1SummonInstance
                                || cha instanceof L1PetInstance) {
                            L1NpcInstance npc = (L1NpcInstance) cha;
                            npc.setSkillEffect(L1SkillId.STATUS_FREEZE, time);
                            npc.broadcastPacket(new S_SkillSound(npc.getId(), 4184));
                            npc.setParalyzed(true);
                        }
                    }
                }
                    break;
                case BONE_BREAK: {
                    int bonetime = 2000;
                    L1EffectSpawn.getInstance().spawnEffect(200020, bonetime, cha.getX(), cha.getY(), cha.getMapId());
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, true));
                    } else if (cha instanceof L1MonsterInstance || cha instanceof L1SummonInstance
                            || cha instanceof L1PetInstance) {
                        L1NpcInstance npc = (L1NpcInstance) cha;
                        npc.setParalyzed(true);
                    }
                }
                    break;

                case PHANTASM: {
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_SLEEP, true));
                    }
                    cha.setSleeped(true);
                }
                    break;

                case WIND_SHACKLE:
                case MOB_WINDSHACKLE_1: {
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) cha;
                        // pc.sendPackets(new S_SkillIconWindShackle(pc.getId(),
                        // _getBuffIconDuration));
                        pc.setSkillEffect(pc.getId(), _getBuffIconDuration);
                    }
                }
                    break;

                case CANCELLATION: {
                    try { // for test
                        if (cha instanceof L1NpcInstance) {
                            L1NpcInstance npc = (L1NpcInstance) cha;
                            int npcId = npc.getNpcTemplate().get_npcId();
                            if (npcId == 71092) {
                                if (npc.getGfxId() == npc.getTempCharGfx()) {
                                    npc.setTempCharGfx(1314);
                                    npc.broadcastPacket(new S_ChangeShape(npc.getId(), 1314));
                                    return;
                                } else {
                                    return;
                                }
                            }
                            if (npcId == 45640) {
                                if (npc.getGfxId() == npc.getTempCharGfx()) {
                                    npc.setCurrentHp(npc.getMaxHp());
                                    npc.setTempCharGfx(2332);
                                    npc.broadcastPacket(new S_ChangeShape(npc.getId(), 2332));
                                    npc.setName("$2103");
                                    npc.setNameId("$2103");
                                    npc.broadcastPacket(new S_ChangeName(npc.getId(), "$2103"));
                                } else if (npc.getTempCharGfx() == 2332) {
                                    npc.setCurrentHp(npc.getMaxHp());
                                    npc.setTempCharGfx(2755);
                                    npc.broadcastPacket(new S_ChangeShape(npc.getId(), 2755));
                                    npc.setName("$2488");
                                    npc.setNameId("$2488");
                                    npc.broadcastPacket(new S_ChangeName(npc.getId(), "$2488"));
                                }
                            }
                            if (npcId == 81209) {
                                if (npc.getGfxId() == npc.getTempCharGfx()) {
                                    npc.setTempCharGfx(4310);
                                    npc.broadcastPacket(new S_ChangeShape(npc.getId(), 4310));
                                    return;
                                } else {
                                    return;
                                }
                            }
                        }
                        if (!(cha instanceof L1PcInstance)) {
                            L1NpcInstance npc = (L1NpcInstance) cha;
                            npc.setMoveSpeed(0);
                            npc.setBraveSpeed(0);
                            npc.broadcastPacket(new S_SkillHaste(cha.getId(), 0, 0));
                            npc.broadcastPacket(new S_SkillBrave(cha.getId(), 0, 0));
                            npc.setWeaponBreaked(false);
                            npc.setParalyzed(false);
                        }

                        if (cha instanceof L1PcInstance) {
                            detection((L1PcInstance) cha, false);
                        }

                        for (int skillNum = SKILLS_BEGIN; skillNum <= SKILLS_END; skillNum++) {
                            if (isNotCancelable(skillNum) && !cha.isDead()) {
                                continue;
                            }
                            if (skillNum == SHAPE_CHANGE) {
                                if (cha instanceof L1PcInstance) {
                                    L1PcInstance pc = (L1PcInstance) cha;
                                    if (pc.getRankLevel() > 2 && pc.getTempCharGfx() >= 13715
                                            && pc.getTempCharGfx() <= 13745)
                                        continue;
                                }
                            }
                            cha.removeSkillEffect(skillNum);
                        }

                        for (int skillNum = STATUS_BEGIN; skillNum <= STATUS_END; skillNum++) {
                            if (skillNum == STATUS_CHAT_PROHIBITED || skillNum == STATUS_CURSE_BARLOG
                                    || skillNum == STATUS_CURSE_YAHEE) {
                                continue;
                            }
                            cha.removeSkillEffect(skillNum);
                        }

                        cha.curePoison();
                        cha.cureParalaysis();

                        for (int skillNum = COOKING_BEGIN; skillNum <= COOKING_END; skillNum++) {
                            if (isNotCancelable(skillNum) && !cha.isDead()) {
                                continue;
                            }
                            cha.removeSkillEffect(skillNum);
                        }

                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            if (pc.getHasteItemEquipped() > 0) {
                                continue;
                                // pc.setMoveSpeed(0);
                                // pc.sendPackets(new S_SkillHaste(pc.getId(),
                                // 0, 0));
                                // pc.broadcastPacket(new
                                // S_SkillHaste(pc.getId(), 0, 0));

                            }
                        }
                        cha.removeSkillEffect(STATUS_FREEZE);
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_CharVisualUpdate(pc));
                            pc.broadcastPacket(new S_CharVisualUpdate(pc));
                            if (pc.isPrivateShop()) {
                                pc.sendPackets(
                                        new S_DoActionShop(pc.getId(), ActionCodes.ACTION_Shop, pc.getShopChat()));
                                pc.broadcastPacket(
                                        new S_DoActionShop(pc.getId(), ActionCodes.ACTION_Shop, pc.getShopChat()));
                            }
                            if (_user instanceof L1PcInstance) {
                                L1PinkName.onAction(pc, _user);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                    break;
                case TURN_UNDEAD: {
                    if (undeadType == 1 || undeadType == 3) {
                        dmg = cha.getCurrentHp();
                    }
                }
                    break;
                case MANA_DRAIN: {
                    int chance = random.nextInt(5) + 5;
                    drainMana = chance + (_user.getAbility().getTotalInt() / 2);
                    if (cha.getCurrentMp() < drainMana) {
                        drainMana = cha.getCurrentMp();
                    }
                    if (_user instanceof L1PcInstance) {
                        _player.sendPackets(new S_SkillSound(_player.getId(), 2171), true);
                        Broadcaster.broadcastPacket(_player, new S_SkillSound(_player.getId(), 2171), true);
                    } else {
                        Broadcaster.broadcastPacket(_user, new S_SkillSound(_user.getId(), 2171), true);
                    }
                }
                    break;
                case WEAPON_BREAK: {
                    if (_calcType == PC_PC || _calcType == NPC_PC) {
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            L1ItemInstance weapon = pc.getWeapon();
                            if (weapon != null) {
                                int weaponDamage = random.nextInt(_user.getAbility().getTotalInt() / 3) + 1;
                                pc.sendPackets(new S_ServerMessage(268, weapon.getLogName()));
                                pc.getInventory().receiveDamage(weapon, weaponDamage);
                            }
                        }
                    } else {
                        ((L1NpcInstance) cha).setWeaponBreaked(true);
                    }
                }
                    break;
                case FOG_OF_SLEEPING: {
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_SLEEP, true));
                    }
                    cha.setSleeped(true);
                }
                    break;
                case STATUS_FREEZE: {
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, true));
                    }
                }
                    break;
                case OMAN_STUN: {
                    int[] stunTimeArray = { 2500, 3000, 3500 };
                    int rnd = random.nextInt(stunTimeArray.length);
                    _shockStunDuration = stunTimeArray[rnd];
                    L1EffectSpawn.getInstance().spawnEffect(81162, _shockStunDuration, cha.getX(), cha.getY(),
                            cha.getMapId());
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, true));
                    } else if (cha instanceof L1MonsterInstance || cha instanceof L1SummonInstance
                            || cha instanceof L1PetInstance) {
                        L1NpcInstance npc = (L1NpcInstance) cha;
                        npc.setParalyzed(true);
                        npc.setParalysisTime(_shockStunDuration);
                    }
                }
                    break;
                case OMAN_CANCELLATION: {
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) cha;
                        for (int skillNum = SKILLS_BEGIN; skillNum <= SKILLS_END; skillNum++) {
                            if (isNotCancelable(skillNum) && !pc.isDead()) {
                                continue;
                            }
                            if (skillNum == SHAPE_CHANGE) {
                                if (pc.getRankLevel() > 2 && pc.getTempCharGfx() >= 13715
                                        && pc.getTempCharGfx() <= 13745)
                                    continue;
                            }
                            pc.removeSkillEffect(skillNum);
                        }
                        for (int skillNum = STATUS_BEGIN; skillNum <= STATUS_CANCLEEND; skillNum++) {
                            if (skillNum == STATUS_CHAT_PROHIBITED) {
                                continue;
                            }
                            pc.removeSkillEffect(skillNum);
                        }
                        for (int skillNum = COOKING_BEGIN; skillNum <= COOKING_END; skillNum++) {
                            if (isNotCancelable(skillNum) && !pc.isDead()) {
                                continue;
                            }
                            pc.removeSkillEffect(skillNum);
                        }
                        pc.curePoison();
                        pc.cureParalaysis();
                        if (!(pc.getRankLevel() > 2 && pc.getTempCharGfx() >= 13715 && pc.getTempCharGfx() <= 13745)) {
                            L1PolyMorph.undoPoly(pc);
                            pc.sendPackets(new S_CharVisualUpdate(pc));
                            pc.broadcastPacket(new S_CharVisualUpdate(pc));
                        }
                        if (pc.getHasteItemEquipped() > 0) {
                            pc.setMoveSpeed(0);
                            pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
                            pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
                        }
                        if (pc != null && pc.isInvisble()) {
                            if (pc.hasSkillEffect(L1SkillId.INVISIBILITY)) {
                                pc.killSkillEffectTimer(L1SkillId.INVISIBILITY);
                                pc.sendPackets(new S_Invis(pc.getId(), 0));
                                pc.broadcastPacket(new S_Invis(pc.getId(), 0));
                                pc.sendPackets(new S_Sound(147));
                            }
                            if (pc.hasSkillEffect(L1SkillId.BLIND_HIDING)) {
                                pc.killSkillEffectTimer(L1SkillId.BLIND_HIDING);
                                pc.sendPackets(new S_Invis(pc.getId(), 0));
                                pc.broadcastPacket(new S_Invis(pc.getId(), 0));
                            }
                        }
                        pc.removeSkillEffect(STATUS_FREEZE);
                        pc.sendPackets(new S_SkillSound(pc.getId(), 870));
                        pc.broadcastPacket(new S_SkillSound(pc.getId(), 870));
                    }
                }
                    break;
                case ANTA_MESSAGE_1: // ヒット[用言1 /キャンセル - >オブモク！ケインドネシア]
                case ANTA_MESSAGE_2: // ヒット[用言2 /ブレス+毒/オブモク！ケンロー]
                case ANTA_MESSAGE_3: // ヒット[用言3 /左手+右パンチ+叫ん/オブモク！ティー育て]
                case ANTA_MESSAGE_4: // ヒット[用言4 /パンチ+ブレス/オブモク！ケンティ育て]
                case ANTA_MESSAGE_5: // ヒット[用言5 /叫び声+ブレス/オブモク！ルーミス]
                case ANTA_MESSAGE_6: // ヒット[用言6 /スタン+ジャンプ/オブモク！ミューズ三]
                case ANTA_MESSAGE_7: // ヒット[用言7 /スタン+発作/オブモク！ナッツ三]
                case ANTA_MESSAGE_8: // ヒット[用言8 /スタン+足+点/オブモク！ティープサム]
                case ANTA_MESSAGE_9: // ヒット[用言9 /ウェポンブレス/オブモク！リラのプ]
                case ANTA_MESSAGE_10: // ヒット[用言10 /ウェポン+麻痺/オブモク！セイララフ]
                case ANTA_CANCELLATION:
                case ANTA_WEAPON_BREAK:
                case ANTA_SHOCKSTUN: {
                    int npcId = _npc.getNpcTemplate().get_npcId();
                    if (npcId == 900011 || npcId == 900012 || npcId == 900013) {
                        if (_skillId == ANTA_MESSAGE_1 || _skillId == ANTA_CANCELLATION) { // キャンセル
                            if (cha instanceof L1PcInstance) {
                                L1PcInstance pc = (L1PcInstance) cha;
                                for (int skillNum = SKILLS_BEGIN; skillNum <= SKILLS_END; skillNum++) {
                                    if (isNotCancelable(skillNum) && !pc.isDead()) {
                                        continue;
                                    }
                                    if (skillNum == SHAPE_CHANGE) {
                                        if (pc.getRankLevel() > 2 && pc.getTempCharGfx() >= 13715
                                                && pc.getTempCharGfx() <= 13745)
                                            continue;
                                    }
                                    pc.removeSkillEffect(skillNum);
                                }
                                for (int skillNum = STATUS_BEGIN; skillNum <= STATUS_CANCLEEND; skillNum++) {
                                    if (skillNum == STATUS_CHAT_PROHIBITED) {
                                        continue;
                                    }
                                    pc.removeSkillEffect(skillNum);
                                }
                                for (int skillNum = COOKING_BEGIN; skillNum <= COOKING_END; skillNum++) {
                                    if (isNotCancelable(skillNum) && !pc.isDead()) {
                                        continue;
                                    }
                                    pc.removeSkillEffect(skillNum);
                                }
                                pc.curePoison();
                                pc.cureParalaysis();
                                if (!(pc.getRankLevel() > 2 && pc.getTempCharGfx() >= 13715
                                        && pc.getTempCharGfx() <= 13745)) {
                                    L1PolyMorph.undoPoly(pc);
                                    pc.sendPackets(new S_CharVisualUpdate(pc));
                                    pc.broadcastPacket(new S_CharVisualUpdate(pc));
                                }
                                if (pc.getHasteItemEquipped() > 0) {
                                    pc.setMoveSpeed(0);
                                    pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
                                    pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
                                }
                                if (pc != null && pc.isInvisble()) {
                                    if (pc.hasSkillEffect(L1SkillId.INVISIBILITY)) {
                                        pc.killSkillEffectTimer(L1SkillId.INVISIBILITY);
                                        pc.sendPackets(new S_Invis(pc.getId(), 0));
                                        pc.broadcastPacket(new S_Invis(pc.getId(), 0));
                                        pc.sendPackets(new S_Sound(147));
                                    }
                                    if (pc.hasSkillEffect(L1SkillId.BLIND_HIDING)) {
                                        pc.killSkillEffectTimer(L1SkillId.BLIND_HIDING);
                                        pc.sendPackets(new S_Invis(pc.getId(), 0));
                                        pc.broadcastPacket(new S_Invis(pc.getId(), 0));
                                    }
                                }
                                pc.removeSkillEffect(STATUS_FREEZE);
                                pc.sendPackets(new S_SkillSound(pc.getId(), 870));
                                pc.broadcastPacket(new S_SkillSound(pc.getId(), 870));
                            }
                        }

                        if (_skillId == ANTA_MESSAGE_1 || _skillId == ANTA_MESSAGE_10) {// 麻痺毒
                            Random random = new Random();
                            int time = random.nextInt(5) + 1;
                            if (cha instanceof L1PcInstance) {
                                L1PcInstance pc = (L1PcInstance) cha;
                                if (time > 10)
                                    L1ParalysisPoison.doInfection(pc, 5, time * 1000);
                            }
                        }

                        if (_skillId == ANTA_MESSAGE_2 || _skillId == ANTA_MESSAGE_5 || _skillId == ANTA_MESSAGE_9) { // 対米ひどく
                            Random random = new Random();
                            int PoisonDmg = random.nextInt(50) + 1;
                            int PoisonTime = random.nextInt(15) + 1;
                            if (cha instanceof L1PcInstance) {
                                L1PcInstance pc = (L1PcInstance) cha;
                                if (PoisonTime > 2)
                                    L1DamagePoison.doInfection(pc, _target, PoisonTime * 1000, PoisonDmg,
                                            _skillId == TOMAHAWK);
                            }
                        }
                        if (_skillId == ANTA_MESSAGE_6 || _skillId == ANTA_MESSAGE_7 || _skillId == ANTA_MESSAGE_8
                                || _skillId == ANTA_SHOCKSTUN) {// スターン
                            int[] stunTimeArray = { 4500, 5000, 5500 };
                            int rnd = random.nextInt(stunTimeArray.length);
                            _shockStunDuration = stunTimeArray[rnd];
                            L1EffectSpawn.getInstance().spawnEffect(81162, _shockStunDuration, cha.getX(), cha.getY(),
                                    cha.getMapId());
                            if (cha instanceof L1PcInstance) {
                                L1PcInstance pc = (L1PcInstance) cha;
                                pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, true));
                            } else if (cha instanceof L1MonsterInstance || cha instanceof L1SummonInstance
                                    || cha instanceof L1PetInstance) {
                                L1NpcInstance npc = (L1NpcInstance) cha;
                                npc.setParalyzed(true);
                                npc.setParalysisTime(_shockStunDuration);
                            }
                        }
                        if (_skillId == ANTA_MESSAGE_9 || _skillId == ANTA_MESSAGE_10
                                || _skillId == ANTA_WEAPON_BREAK) { // ウェポン
                            if (cha instanceof L1PcInstance) {
                                L1PcInstance pc = (L1PcInstance) cha;
                                L1ItemInstance weapon = pc.getWeapon();
                                if (weapon != null) {
                                    int weaponDamage = random.nextInt(3) + 1;
                                    pc.sendPackets(new S_ServerMessage(268, weapon.getLogName()));
                                    pc.getInventory().receiveDamage(weapon, weaponDamage);
                                    pc.sendPackets(new S_SkillSound(pc.getId(), 172));
                                    pc.broadcastPacket(new S_SkillSound(pc.getId(), 172));
                                }
                            }
                        }
                    }
                }
                    break;
                case PAP_PREDICATE1: // パプ[用言1：リオタ！ピロイや！ 【五色パール3 /神秘的な五色真珠1
                    // / トールや召喚5]
                case PAP_PREDICATE3: // パプ[用言3：リオタ！ラナキュウリので！ 【デスポーション - >右 - >
                    // アイスイイラプション]
                case PAP_PREDICATE5: // パプ[用言5：リオタ！ネーナウーアヌス！ 【リデュースヒル+頭の攻撃+アイス
                    // ブレス]
                case PAP_PREDICATE6: // パプ[用言6：リオタ！アンテナウェインラク！ [デスヒル+尾の攻撃+アイス
                    // ブレス]
                case PAP_PREDICATE7: // パプ[用言7：リオタ！ラナポンポン！ [キャンセレーション+上がった中2回]
                                     // [範囲
                    // X]
                case PAP_PREDICATE8: // パプ[用言8：リオタ！レポポンポン！ [ウェポンブレイク+左手2回] [範囲
                    // X]
                case PAP_PREDICATE9: // パプ[用言9：リオタ！アンテナ論連邦！ [尾2連打+アイス
                    // ブレス] [範囲X]
                case PAP_PREDICATE11: // パプ[用言11：リオタ！オニーズウェインラク！
                                      // [マスキャンセレーション+デスヒル
                    // + アイスメテオ+アイスイラプション] [範囲O]
                case PAP_PREDICATE12: { // パプ[用言12：リオタ！オニーズアックスオンウェインラク！
                                        // [マスキャンセレーション
                    // + デス・ヒル+アイスメテオ+発作] [範囲0]
                    int npcId = _npc.getNpcTemplate().get_npcId();
                    if (npcId == 900038 || npcId == 900039 || npcId == 900040) {
                        if (_skillId == PAP_PREDICATE1) { // リコール召喚（サエル - パール -
                                                          // トールや）
                            int i;
                            for (i = 0; i < 2; i++) { // タイマーテイク部分のfor文で
                                // 回すとスレッドエラー動作が
                                // 発生する。
                                L1SpawnUtil.spawn2(_user.getX(), _user.getY(), (short) _user.getMap().getId(), 900049,
                                        8, 60 * 1000, 0);
                                L1SpawnUtil.spawn2(_user.getX(), _user.getY(), (short) _user.getMap().getId(), 900050,
                                        8, 60 * 1000, 0);
                                L1SpawnUtil.spawn2(_user.getX(), _user.getY(), (short) _user.getMap().getId(), 900051,
                                        8, 60 * 1000, 0);
                                L1SpawnUtil.spawn2(_user.getX(), _user.getY(), (short) _user.getMap().getId(), 900052,
                                        8, 120 * 1000, 0);
                            }
                        }

                        if (_skillId == PAP_PREDICATE7 || _skillId == PAP_PREDICATE11 || _skillId == PAP_PREDICATE12) { // キャンセル
                            if (cha instanceof L1PcInstance) {
                                L1PcInstance pc = (L1PcInstance) cha;
                                for (int skillNum = SKILLS_BEGIN; skillNum <= SKILLS_END; skillNum++) {
                                    if (isNotCancelable(skillNum) && !pc.isDead()) {
                                        continue;
                                    }
                                    if (skillNum == SHAPE_CHANGE) {
                                        if (pc.getRankLevel() > 2 && pc.getTempCharGfx() >= 13715
                                                && pc.getTempCharGfx() <= 13745)
                                            continue;
                                    }
                                    pc.removeSkillEffect(skillNum);
                                }
                                for (int skillNum = STATUS_BEGIN; skillNum <= STATUS_CANCLEEND; skillNum++) {
                                    if (skillNum == STATUS_CHAT_PROHIBITED) {
                                        continue;
                                    }
                                    pc.removeSkillEffect(skillNum);
                                }
                                for (int skillNum = COOKING_BEGIN; skillNum <= COOKING_END; skillNum++) {
                                    if (isNotCancelable(skillNum) && !pc.isDead()) {
                                        continue;
                                    }
                                    pc.removeSkillEffect(skillNum);
                                }
                                pc.curePoison();
                                pc.cureParalaysis();
                                if (!(pc.getRankLevel() > 2 && pc.getTempCharGfx() >= 13715
                                        && pc.getTempCharGfx() <= 13745)) {
                                    L1PolyMorph.undoPoly(pc);
                                    pc.sendPackets(new S_CharVisualUpdate(pc));
                                    pc.broadcastPacket(new S_CharVisualUpdate(pc));
                                }
                                if (pc.getHasteItemEquipped() > 0) {
                                    pc.setMoveSpeed(0);
                                    pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
                                    pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
                                }
                                if (pc != null && pc.isInvisble()) {
                                    if (pc.hasSkillEffect(L1SkillId.INVISIBILITY)) {
                                        pc.killSkillEffectTimer(L1SkillId.INVISIBILITY);
                                        pc.sendPackets(new S_Invis(pc.getId(), 0));
                                        pc.broadcastPacket(new S_Invis(pc.getId(), 0));
                                        pc.sendPackets(new S_Sound(147));
                                    }
                                    if (pc.hasSkillEffect(L1SkillId.BLIND_HIDING)) {
                                        pc.killSkillEffectTimer(L1SkillId.BLIND_HIDING);
                                        pc.sendPackets(new S_Invis(pc.getId(), 0));
                                        pc.broadcastPacket(new S_Invis(pc.getId(), 0));
                                    }
                                }
                                pc.removeSkillEffect(STATUS_FREEZE);
                                pc.sendPackets(new S_SkillSound(pc.getId(), 870));
                                pc.broadcastPacket(new S_SkillSound(pc.getId(), 870));
                            }
                        }

                        if (_skillId == PAP_PREDICATE8) { // ウェポン
                            if (cha instanceof L1PcInstance) {
                                L1PcInstance pc = (L1PcInstance) cha;
                                L1ItemInstance weapon = pc.getWeapon();
                                Random random = new Random();
                                int rnd = random.nextInt(100) + 1;
                                if (weapon != null && rnd > 33) {
                                    int weaponDamage = random.nextInt(2) + 1;
                                    pc.sendPackets(new S_ServerMessage(268, weapon.getLogName()));
                                    pc.getInventory().receiveDamage(weapon, weaponDamage);
                                    pc.sendPackets(new S_SkillSound(pc.getId(), 172));
                                    pc.broadcastPacket(new S_SkillSound(pc.getId(), 172));
                                }
                            }
                        }
                        if (_skillId == PAP_PREDICATE3) {
                            if (cha instanceof L1PcInstance) {
                                L1PcInstance pc = (L1PcInstance) cha;
                                pc.sendPackets(new S_SkillSound(pc.getId(), 7781));
                                pc.broadcastPacket(new S_SkillSound(pc.getId(), 7781));
                                pc.setSkillEffect(L1SkillId.PAP_DEATH_PORTION, 12 * 1000);
                            }
                        }
                        if (_skillId == PAP_PREDICATE5) {
                            if (cha instanceof L1PcInstance) {
                                L1PcInstance pc = (L1PcInstance) cha;
                                pc.sendPackets(new S_SkillSound(pc.getId(), 7782));
                                pc.broadcastPacket(new S_SkillSound(pc.getId(), 7782));
                                pc.setSkillEffect(L1SkillId.PAP_REDUCE_HELL, 12 * 1000);
                            }
                        }

                        if (_skillId == PAP_PREDICATE6) {
                            if (cha instanceof L1PcInstance) {
                                L1PcInstance pc = (L1PcInstance) cha;
                                pc.sendPackets(new S_SkillSound(pc.getId(), 7780));
                                pc.setSkillEffect(L1SkillId.PAP_DEATH_HELL, 12 * 1000);
                                pc.broadcastPacket(new S_SkillSound(pc.getId(), 7780));
                            }
                        }

                        if (_skillId == PAP_PREDICATE11 || _skillId == PAP_PREDICATE12) {// デス・ヒル
                            if (cha instanceof L1PcInstance) {
                                L1PcInstance pc = (L1PcInstance) cha;
                                pc.sendPackets(new S_SkillSound(pc.getId(), 7780));
                                pc.setSkillEffect(L1SkillId.PAP_DEATH_HELL, 12 * 1000);
                                pc.broadcastPacket(new S_SkillSound(pc.getId(), 7780));
                            }
                        }
                    }
                }
                    break;
                default:
                    break;
                }

                if (_calcType == PC_PC || _calcType == NPC_PC) {
                    switch (_skillId) {
                    case TELEPORT:
                    case MASS_TELEPORT: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        Random random = new Random();
                        if (_bookmark_x != 0) {
                            if (pc.getMap().isEscapable() || pc.isGm()) {
                                L1Map map = L1WorldMap.getInstance().getMap(_bookmark_mapid);
                                if (_skillId == MASS_TELEPORT) {
                                    for (L1PcInstance member : L1World.getInstance().getVisiblePlayer(pc, 3)) {
                                        if (pc.getClanid() != 0 && member.getClanid() == pc.getClanid()
                                                && member.getId() != pc.getId() && !member.isPrivateShop()
                                                && !member.isAutoClanjoin()) {
                                            int newX2 = _bookmark_x + random.nextInt(3) + 1;
                                            int newY2 = _bookmark_y + random.nextInt(3) + 1;
                                            if (map.isInMap(newX2, newY2) && map.isPassable(newX2, newY2)) {
                                                new L1Teleport().teleport(member, newX2, newY2, _bookmark_mapid,
                                                        member.getHeading(), true);

                                            } else {
                                                new L1Teleport().teleport(member, _bookmark_x, _bookmark_y,
                                                        _bookmark_mapid, member.getHeading(), true);

                                            }
                                        }
                                    }
                                }
                                if (pc.getInventory().checkItem(20288)) {
                                    new L1Teleport().teleport(pc, _bookmark_x, _bookmark_y, _bookmark_mapid,
                                            pc.getHeading(), true);
                                } else {
                                    int newX2 = _bookmark_x + random.nextInt(15);
                                    int newY2 = _bookmark_y + random.nextInt(15);
                                    if (map.isInMap(newX2, newY2) && map.isPassable(newX2, newY2)) {
                                        new L1Teleport().teleport(pc, newX2, newY2, _bookmark_mapid, pc.getHeading(),
                                                true);
                                    } else {
                                        new L1Teleport().teleport(pc, _bookmark_x, _bookmark_y, _bookmark_mapid,
                                                pc.getHeading(), true);
                                    }
                                }
                            } else {
                                pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
                                pc.sendPackets(new S_ServerMessage(79));
                            }
                        } else {
                            if (pc.getMapId() >= 101 && pc.getMapId() <= 110) {
                                int find_item_ids[] = { 830022, // 1階
                                        830023, // 2階
                                        830024, // 3階
                                        830025, // 4階
                                        830026, // 5階
                                        830027, // 6階
                                        830028, // 7階
                                        830029, // 8階
                                        830030, // 9階
                                        830031 // 10階
                                };
                                L1ItemInstance findItem = pc.getInventory()
                                        .findItemId(find_item_ids[pc.getMapId() - 101]);
                                if (findItem != null)
                                    Telbookitem.toActive(pc, 0, null, _skillId);
                                else
                                    pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
                                pc.sendPackets(new S_ServerMessage(276));

                            } else {
                                if (pc.getMap().isTeleportable() || pc.isGm()) {
                                    Telbookitem.toActive(pc, 0, null, _skillId);
                                } else {
                                    pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
                                    pc.sendPackets(new S_ServerMessage(276));
                                }
                            }
                        }
                    }
                        break;
                    case TELEPORT_TO_MATHER: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        if (pc.getMap().isEscapable() || pc.isGm()) {
                            new L1Teleport().teleport(pc, 33051, 32337, (short) 4, 5, true);
                        } else {
                            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
                            pc.sendPackets(new S_ServerMessage(647));
                        }
                    }
                        break;
                    case CALL_CLAN: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        L1PcInstance clanPc = (L1PcInstance) L1World.getInstance().findObject(_targetID);
                        if (clanPc != null) {
                            clanPc.setTempID(pc.getId());
                            clanPc.sendPackets(new S_Message_YN(729, ""));
                        }
                    }
                        break;
                    case RUN_CLAN: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        L1PcInstance clanPc = (L1PcInstance) L1World.getInstance().findObject(_targetID);
                        if (clanPc != null) {
                            if (pc.getMap().isEscapable() || pc.isGm()) {
                                // バトルゾーン
                                if (pc.get_DuelLine() != 0) {
                                    return;
                                }
                                boolean castle_area = L1CastleLocation.checkInAllWarArea(clanPc.getX(), clanPc.getY(),
                                        clanPc.getMapId());
                                if ((clanPc.getMapId() == 0 || clanPc.getMapId() == 4 || clanPc.getMapId() == 5153
                                        || clanPc.getMapId() == 5001 || clanPc.getMapId() == 304)
                                        && castle_area == false) {
                                    new L1Teleport().teleport(pc, clanPc.getX(), clanPc.getY(), clanPc.getMapId(), 5,
                                            true);
                                } else {
                                    pc.sendPackets(new S_ServerMessage(547));
                                }
                            } else {
                                pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
                                pc.sendPackets(new S_ServerMessage(647));
                            }
                        }
                    }
                        break;
                    case BRING_STONE: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        Random random = new Random();
                        L1ItemInstance item = pc.getInventory().getItem(_itemobjid);
                        if (item != null) {
                            int dark = (int) (10 + (pc.getLevel() * 0.8) + (pc.getAbility().getTotalWis() - 6) * 1.2);
                            int brave = (int) (dark / 2.1);
                            int wise = (int) (brave / 2.0);
                            int kayser = (int) (wise / 1.9);
                            int chance = random.nextInt(100) + 1;
                            if (Config.CHECK_AUTO_ENCHANT) {
                                if (pc.checkAuto()) {
                                    chance = 10000;
                                }
                            }
                            if (item.getItem().getItemId() == 40320) {
                                pc.getInventory().removeItem(item, 1);
                                if (dark >= chance) {
                                    pc.getInventory().storeItem(40321, 1);
                                    pc.sendPackets(new S_ServerMessage(403, "$2475"));
                                } else {
                                    pc.sendPackets(new S_ServerMessage(280));
                                }
                            } else if (item.getItem().getItemId() == 40321) {
                                pc.getInventory().removeItem(item, 1);
                                if (brave >= chance) {
                                    pc.getInventory().storeItem(40322, 1);
                                    pc.sendPackets(new S_ServerMessage(403, "$2476"));
                                } else {
                                    pc.sendPackets(new S_ServerMessage(280));
                                }
                            } else if (item.getItem().getItemId() == 40322) {
                                pc.getInventory().removeItem(item, 1);
                                if (wise >= chance) {
                                    pc.getInventory().storeItem(40323, 1);
                                    pc.sendPackets(new S_ServerMessage(403, "$2477"));
                                } else {
                                    pc.sendPackets(new S_ServerMessage(280));
                                }
                            } else if (item.getItem().getItemId() == 40323) {
                                pc.getInventory().removeItem(item, 1);
                                if (kayser >= chance) {
                                    pc.getInventory().storeItem(40324, 1);
                                    pc.sendPackets(new S_ServerMessage(403, "$2478"));
                                } else {
                                    pc.sendPackets(new S_ServerMessage(280));
                                }
                            }
                        }
                    }
                        break;
                    case SUMMON_MONSTER: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        int level = pc.getLevel();
                        int chari = pc.getAbility().getTotalCha();
                        if ((pc.getMap().isRecallPets() && !pc.isInWarArea() && pc.getMapId() != 781
                                && pc.getMapId() != 782) || pc.isGm()) {
                            int summonid = 0;
                            if (pc.getInventory().checkEquipped(20284) && chari >= 25) {
                                if (level < 40) {
                                    summonid = 81083;
                                } else if (level >= 40 && level < 52) {
                                    summonid = 81085;
                                } else if (level >= 52 && level < 64) {
                                    summonid = 81086;
                                } else if (level >= 64 && level < 76) {
                                    summonid = 81087;
                                } else if (level >= 76) {
                                    summonid = 81088;
                                    if ((level >= 80 && level < 82) && chari >= 35) {
                                        summonid = 81089;
                                    } else if ((level >= 82 && level < 84) && chari >= 35) {
                                        summonid = 81090;
                                    } else if ((level >= 84 && level < 86) && chari >= 35) {
                                        summonid = 81091;
                                    } else if ((level >= 86 && level < 88) && chari >= 35) {
                                        summonid = 81092;
                                    } else if (level >= 88 && chari >= 35) {
                                        summonid = 81093;
                                    }
                                }
                            } else {
                                summonid = 81083;
                            }
                            int petcost = 0;
                            int summoncost = pc.getAbility().getTotalCha() + 6 - petcost;
                            Object[] petlist = pc.getPetList().values().toArray();
                            for (Object pet : petlist) {
                                petcost += pc.getAbility().getTotalCha();
                            }
                            int charisma = pc.getAbility().getTotalCha() + 6 - petcost;
                            int summoncount = charisma / summoncost;
                            L1Npc npcTemp = NpcTable.getInstance().getTemplate(summonid);
                            for (int i = 0; i < summoncount; i++) {
                                L1SummonInstance summon = new L1SummonInstance(npcTemp, pc);
                                summon.setPetcost(summoncost);
                            }
                        } else {
                            pc.sendPackets(new S_ServerMessage(79));
                        }
                    }
                        break;
                    case LESSER_ELEMENTAL:
                    case GREATER_ELEMENTAL: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        int attr = pc.getElfAttr();
                        if (attr != 0) {
                            if ((pc.getMap().isRecallPets() && !pc.isInWarArea()) || pc.isGm()) {
                                int petcost = 0;
                                Object[] petlist = pc.getPetList().values().toArray();
                                for (Object pet : petlist) {
                                    petcost += ((L1NpcInstance) pet).getPetcost();
                                }

                                if (petcost == 0) {
                                    int summonid = 0;
                                    int summons[];
                                    if (_skillId == LESSER_ELEMENTAL) {
                                        summons = new int[] { 45306, 45303, 45304, 45305 };
                                    } else {
                                        summons = new int[] { 81053, 81050, 81051, 81052 };
                                    }
                                    int npcattr = 1;
                                    for (int i = 0; i < summons.length; i++) {
                                        if (npcattr == attr) {
                                            summonid = summons[i];
                                            i = summons.length;
                                        }
                                        npcattr *= 2;
                                    }
                                    if (summonid == 0) {
                                        Random random = new Random();
                                        int k3 = random.nextInt(4);
                                        summonid = summons[k3];
                                    }

                                    L1Npc npcTemp = NpcTable.getInstance().getTemplate(summonid);
                                    L1SummonInstance summon = new L1SummonInstance(npcTemp, pc);
                                    summon.setPetcost(pc.getAbility().getTotalCha() + 7);
                                }
                            } else {
                                pc.sendPackets(new S_ServerMessage(79));
                            }
                        }
                    }
                        break;
                    case ABSOLUTE_BARRIER: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        // pc.stopMpRegeneration();
                        pc.stopMpRegenerationByDoll();
                    }
                        break;
                    case LIGHT:
                        break;

                    case GLOWING_AURA: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.addHitup(5);
                        pc.addBowHitup(5);
                        pc.getResistance().addMr(20);
                        pc.sendPackets(new S_SPMR(pc));
                        pc.sendPackets(new S_SkillIconAura(113, _getBuffIconDuration));
                    }
                        break;
                    case SHINING_AURA: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.getAC().addAc(-8);
                        pc.sendPackets(new S_SkillIconAura(114, _getBuffIconDuration));
                    }
                        break;
                    case BRAVE_AURA: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.addDmgup(5);
                        pc.sendPackets(new S_SkillIconAura(116, _getBuffIconDuration));
                    }
                        break;
                    case SHIELD: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.getAC().addAc(-2);
                        pc.sendPackets(new S_SkillIconShield(2, _getBuffIconDuration));
                    }
                        break;
                    case SHADOW_ARMOR: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.getResistance().addMr(5);
                        pc.sendPackets(new S_SPMR(pc));
                        // pc.sendPackets(new S_SkillIconShield(3,
                        // _getBuffIconDuration));
                    }
                        break;
                    case DRESS_DEXTERITY: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.getAbility().addAddedDex((byte) 3);
                        pc.sendPackets(new S_Dexup(pc, 3, _getBuffIconDuration));
                    }
                        break;
                    case DRESS_MIGHTY: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.getAbility().addAddedStr((byte) 3);
                        pc.sendPackets(new S_Strup(pc, 3, _getBuffIconDuration));
                    }
                        break;
                    case SHADOW_FANG: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        L1ItemInstance item = pc.getInventory().getItem(_itemobjid);
                        if (item != null && item.getItem().getType2() == 1) {
                            item.setSkillWeaponEnchant(pc, _skillId, _skill.getBuffDuration() * 1000);
                            if (item.isEquipped())
                                pc.sendPackets(new S_PacketBox(S_PacketBox.BUFFICON, _skill.getBuffDuration(),
                                        _skill.getCastGfx(), 0));
                        } else {
                            pc.sendPackets(new S_ServerMessage(79));
                        }
                    }
                        break;
                    case ENCHANT_WEAPON: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        L1ItemInstance item = pc.getInventory().getItem(_itemobjid);
                        if (item != null && item.getItem().getType2() == 1) {
                            pc.sendPackets(new S_ServerMessage(161, item.getLogName(), "$245", "$247"));
                            item.setSkillWeaponEnchant(pc, _skillId, _skill.getBuffDuration() * 1000);
                            int count = 0;
                            for (L1ItemInstance item2 : pc.getEquipSlot().getWeapons()) {
                                if (item2.getEnchantMagic() == _skill.getCastGfx())
                                    count++;
                                pc.sendPackets(new S_PacketBox(S_PacketBox.BUFFICON, _skill.getBuffDuration(),
                                        _skill.getCastGfx(), count));
                            }
                        } else {
                            pc.sendPackets(new S_ServerMessage(79));
                        }
                    }
                        break;
                    /** 双バフ関連 **/
                    case HOLY_WEAPON:
                    case BLESS_WEAPON: {
                        if (!(cha instanceof L1PcInstance)) {
                            return;
                        }
                        L1PcInstance pc = (L1PcInstance) cha;
                        if (pc.getWeapon() == null) {
                            pc.sendPackets(new S_ServerMessage(79));
                            return;
                        }

                        if (pc.isWarrior() && pc.getEquipSlot().getWeaponCount() == 2) {
                            int count = 0;

                            for (L1ItemInstance item2 : pc.getInventory().getItems()) {
                                if (item2 != null && item2.getItem().getType2() == 1 && item2.getItem().getType() == 6
                                        && item2.isEquipped()) {
                                    pc.sendPackets(new S_ServerMessage(161, String.valueOf(item2.getLogName()).trim(),
                                            "$245", "$247"));
                                    item2.setSkillWeaponEnchant(pc, _skillId, _skill.getBuffDuration() * 1000);
                                    count++;
                                    pc.sendPackets(new S_PacketBox(S_PacketBox.BUFFICON, _skill.getBuffDuration(),
                                            _skill.getCastGfx(), count - 1));
                                }
                            }
                        } else {
                            for (L1ItemInstance item : pc.getInventory().getItems()) {
                                if (pc.getWeapon().equals(item)) {
                                    pc.sendPackets(new S_ServerMessage(161, String.valueOf(item.getLogName()).trim(),
                                            "$245", "$247"));
                                    item.setSkillWeaponEnchant(pc, _skillId, _skill.getBuffDuration() * 1000);
                                    if (item.isEquipped())
                                        pc.sendPackets(new S_PacketBox(S_PacketBox.BUFFICON, _skill.getBuffDuration(),
                                                _skill.getCastGfx(), 0));
                                }
                            }
                        }
                    }
                        break;
                    case BLESSED_ARMOR: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        L1ItemInstance item = pc.getInventory().getItem(_itemobjid);
                        if (item != null && item.getItem().getType2() == 2 && item.getItem().getType() == 2) {
                            pc.sendPackets(new S_ServerMessage(161, item.getLogName(), "$245", "$247"));
                            item.setSkillArmorEnchant(pc, _skillId, _skill.getBuffDuration() * 1000);
                            if (item.isEquipped())
                                pc.sendPackets(new S_PacketBox(S_PacketBox.BUFFICON, _skill.getBuffDuration(),
                                        _skill.getCastGfx(), 0));
                        } else {
                            pc.sendPackets(new S_ServerMessage(79));
                        }
                    }
                        break;
                    case EARTH_GUARDIAN: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.sendPackets(new S_SkillIconShield(7, _getBuffIconDuration));
                    }
                        break;
                    case RESIST_MAGIC: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.getResistance().addMr(10);
                        pc.sendPackets(new S_SPMR(pc));
                    }
                        break;
                    case CLEAR_MIND: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.getAbility().addAddedWis((byte) 3);
                        pc.resetBaseMr();
                    }
                        break;
                    case RESIST_ELEMENTAL: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.getResistance().addAllNaturalResistance(10);
                        pc.sendPackets(new S_OwnCharAttrDef(pc));
                    }
                        break;
                    case BODY_TO_MIND: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.setCurrentMp(pc.getCurrentMp() + 2);
                    }
                        break;
                    case BLOODY_SOUL: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.setCurrentMp(pc.getCurrentMp() + 19);
                    }
                        break;
                    case ELEMENTAL_PROTECTION: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        int attr = pc.getElfAttr();
                        if (attr == 1) {
                            pc.getResistance().addEarth(50);
                        } else if (attr == 2) {
                            pc.getResistance().addFire(50);
                        } else if (attr == 4) {
                            pc.getResistance().addWater(50);
                        } else if (attr == 8) {
                            pc.getResistance().addWind(50);
                        }
                    }
                        break;
                    case INVISIBILITY:
                    case BLIND_HIDING: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        for (L1DollInstance doll : pc.getDollList()) {
                            doll.deleteDoll();
                        }
                        pc.sendPackets(new S_Invis(pc.getId(), 1));
                        pc.broadcastPacket(new S_Invis(pc.getId(), 1));
                    }
                        break;
                    case BUFF_SAEL: {
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            if (pc.hasSkillEffect(L1SkillId.BUFF_SAEL)) {
                                pc.removeSkillEffect(L1SkillId.BUFF_SAEL);
                            }
                            pc.getAC().addAc(-8);
                            pc.addBowHitup(6);
                            pc.addBowDmgup(3);
                            pc.addMaxHp(80);
                            pc.addMaxMp(10);
                            pc.addHpr(8);
                            pc.addMpr(1);
                            pc.getResistance().addWater(30);
                            pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                            pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                            pc.sendPackets(new S_OwnCharAttrDef(pc));
                            pc.sendPackets(new S_SPMR(pc));
                        }
                    }
                        break;
                    case BUFF_GUNTER: {
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            if (pc.hasSkillEffect(L1SkillId.BUFF_GUNTER)) {
                                pc.removeSkillEffect(L1SkillId.BUFF_GUNTER);
                            }
                            pc.getAbility().addAddedDex((byte) 5);
                            pc.addBowHitup(7);
                            pc.addBowDmgup(5);
                            pc.addMaxHp(100);
                            pc.addMaxMp(40);
                            pc.addHpr(10);
                            pc.addMpr(3);
                            pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                            pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                            pc.sendPackets(new S_OwnCharAttrDef(pc));
                            pc.sendPackets(new S_SPMR(pc));
                        }
                    }
                        break;
                    case God_buff: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.getAC().addAc(-2);
                        pc.addHitup(3);
                        pc.addMaxHp(20);
                        pc.addMaxMp(13);
                        pc.getResistance().addHold(10); // ホールド耐性
                        pc.sendPackets(new S_SkillSound(pc.getId(), 4914));
                    }
                        break;
                    case IRON_SKIN: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.getAC().addAc(-10);
                        pc.sendPackets(new S_SkillIconShield(10, _getBuffIconDuration));
                    }
                        break;
                    case EARTH_SKIN: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.getAC().addAc(-6);
                        pc.sendPackets(new S_SkillIconShield(6, _getBuffIconDuration));
                    }
                        break;
                    case PHYSICAL_ENCHANT_STR: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.getAbility().addAddedStr((byte) 5);
                        pc.sendPackets(new S_Strup(pc, 5, _getBuffIconDuration));
                    }
                        break;
                    case PHYSICAL_ENCHANT_DEX: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.getAbility().addAddedDex((byte) 5);
                        pc.sendPackets(new S_Dexup(pc, 5, _getBuffIconDuration));
                    }
                        break;
                    case NARUTO_THANKS_CANDY: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        if (pc.getLevel() >= 1 && pc.getLevel() <= 60) {
                            pc.getAbility().addAddedDex((byte) 7);
                            pc.sendPackets(new S_Dexup(pc, 7, _getBuffIconDuration));
                            pc.getAbility().addAddedStr((byte) 7);
                            pc.sendPackets(new S_Strup(pc, 7, _getBuffIconDuration));
                        } else {
                            pc.getAbility().addAddedDex((byte) 6);
                            pc.sendPackets(new S_Dexup(pc, 6, _getBuffIconDuration));
                            pc.getAbility().addAddedStr((byte) 6);
                            pc.sendPackets(new S_Strup(pc, 6, _getBuffIconDuration));
                        }
                    }
                        break;
                    case FIRE_WEAPON: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.addDmgup(4);
                        pc.sendPackets(new S_SkillIconAura(147, _getBuffIconDuration));
                    }
                        break;
                    case REDUCE_WEIGHT:// イリュージョニスト、ウィザードのスキルのように使用してください
                    case DECREASE_WEIGHT: {// ウィザードの魔法
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.addWeightReduction(800);
                    }
                        break;
                    /*
                     * case DECREASE_WEIGHT:{//ウィザードの魔法 L1PcInstance pc =
                     * (L1PcInstance) cha; pc.addWeightReduction(800); } break;
                     */
                    case DANCING_BLADES: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.sendPackets(new S_SkillBrave(pc.getId(), 1, _getBuffIconDuration));
                        Broadcaster.broadcastPacket(pc, new S_SkillBrave(pc.getId(), 1, 0));
                        pc.sendPackets(new S_SkillIconAura(154, _getBuffIconDuration));
                    }
                        break;
                    case BURNING_WEAPON: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.addDmgup(6);
                        pc.addHitup(3);
                        pc.sendPackets(new S_SkillIconAura(162, _getBuffIconDuration));
                    }
                        break;
                    case MIRROR_IMAGE: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.addDg(-8);
                    }
                        break;
                    case WIND_SHOT: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.addBowHitup(6);
                        pc.sendPackets(new S_SkillIconAura(148, _getBuffIconDuration));
                    }
                        break;
                    case STORM_EYE: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.addBowHitup(2);
                        pc.addBowDmgup(3);
                        pc.sendPackets(new S_SkillIconAura(155, _getBuffIconDuration));
                    }
                        break;
                    case STORM_SHOT: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.addBowDmgup(5);
                        pc.addBowHitup(-3);
                        pc.sendPackets(new S_SkillIconAura(165, _getBuffIconDuration));
                    }
                        break;
                    case BERSERKERS: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.getAC().addAc(10);
                        pc.addDmgup(5);
                        pc.addHitup(5);
                    }
                        break;
                    case SCALES_EARTH_DRAGON: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.getAC().addAc(-3);
                        pc.getResistance().addHold(10);
                        pc.sendPackets(new S_OwnCharAttrDef(pc));
                    }
                        break;
                    case SCALES_WATER_DRAGON: { // バイタルライズ効果
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.getResistance().addFreeze(10);
                    }
                        break;
                    case SCALES_FIRE_DRAGON: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.getResistance().addStun(10);
                        pc.addHitup(5);
                    }
                        break;
                    case IllUSION_OGRE: { // イリュージョンオーガスタ
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.addDmgup(4);
                        pc.addHitup(4);
                    }
                        break;
                    case IllUSION_LICH: { // イリュージョンリッチ
                        L1PcInstance pc = (L1PcInstance) cha;
                        // pc.addSp(2);
                        pc.getAbility().addSp(2);
                        pc.sendPackets(new S_SPMR(pc));
                    }
                        break;
                    case IllUSION_DIAMONDGOLEM: { // イリュージョンダイヤゴーレム
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.getAC().addAc(-8);
                        pc.sendPackets(new S_OwnCharAttrDef(pc));
                    }
                        break;
                    case IllUSION_AVATAR: { // イリュージョンアバター
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.addDmgup(10);
                    }
                        break;
                    case INSIGHT: { // インサイト
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.getAbility().addAddedStr((byte) 1);
                        pc.getAbility().addAddedDex((byte) 1);
                        pc.getAbility().addAddedCon((byte) 1);
                        pc.getAbility().addAddedInt((byte) 1);
                        pc.getAbility().addAddedWis((byte) 1);
                        pc.resetBaseMr();
                    }
                        break;
                    case TENKASOUSHI_BUFF: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        if (pc.hasSkillEffect(TENKASOUSHI_BUFF))
                            pc.removeSkillEffect(TENKASOUSHI_BUFF);
                        pc.addDamageReductionByArmor(5);
                        // pc.sendPackets(new S_SkillIconNEW(2730, 7200, 5,
                        // 7244, 1426));
                        pc.sendPackets(new S_PacketBox(S_PacketBox.ICON_COOKING, pc, 187, 1800));
                        pc.setDessertId(TENKASOUSHI_BUFF);
                        pc.sendPackets(new S_ServerMessage(1426));
                    }
                        break;
                    case SHAPE_CHANGE: {
                        boolean isSameClan = false;
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            if (pc.getClanid() != 0 && _player.getClanid() == pc.getClanid()) {
                                isSameClan = true;
                            }
                        }
                        if (cha instanceof L1MonsterInstance) {
                            return;
                        }
                        if (_player.getId() != cha.getId() && !isSameClan) {
                            int probability = 80;
                            int rnd = random.nextInt(100) + 1;
                            if (rnd > probability) {
                                return;
                            }

                        }
                        int[] polyArray = { 29, 945, 947, 979, 1037, 1039, 3860, 3861, 3862, 3863, 3864, 3865, 3904,
                                3906, 95, 146, 2374, 2376, 2377, 2378, 3866, 3867, 3868, 3869, 3870, 3871, 3872, 3873,
                                3874, 3875, 3876, 3882, 3883, 3884, 3885, 11358, 11396, 11397, 12225, 12226, 11399,
                                11398, 12227 };
                        int pid = random.nextInt(polyArray.length);
                        int polyId = polyArray[pid];
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            if (pc.getInventory().checkEquipped(20281)) {
                                pc.sendPackets(new S_Message_YN(180));
                            } else {
                                L1Skills skillTemp = SkillsTable.getInstance().getTemplate(SHAPE_CHANGE);
                                L1PolyMorph.doPoly(pc, polyId, skillTemp.getBuffDuration(),
                                        L1PolyMorph.MORPH_BY_ITEMMAGIC);
                                if (_player.getId() != pc.getId()) {
                                    pc.sendPackets(new S_ServerMessage(241, _player.getName()));
                                }
                            }
                        }
                    }
                        break;
                    case ADVANCE_SPIRIT: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.setAdvenHp(pc.getBaseMaxHp() / 5);
                        pc.setAdvenMp(pc.getBaseMaxMp() / 5);
                        pc.addMaxHp(pc.getAdvenHp());
                        pc.addMaxMp(pc.getAdvenMp());
                        if (pc.isInParty()) {
                            pc.getParty().updateMiniHP(pc);
                        }
                        pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                        pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                    }
                        break;

                    // 戦士スキル：ギガンティック
                    case GIGANTIC: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        if (pc.hasSkillEffect(L1SkillId.GIGANTIC)) {
                            pc.removeSkillEffect(L1SkillId.GIGANTIC);
                        }
                        double percent = pc.getLevel() / 2;
                        int addHp = (int) Math.round(pc.getBaseMaxHp() * (percent * 0.01));
                        pc.setGiganticHp(addHp);
                        pc.addMaxHp(pc.getGiganticHp());
                        if (pc.isInParty()) {
                            pc.getParty().updateMiniHP(pc);
                        }
                        pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                        pc.setSkillEffect(L1SkillId.GIGANTIC, 300 * 1000);
                    }
                        break;
                    /** パワーグリップ詠唱時間本サーバー化 **/
                    case POWEGRRIP: {

                        int[] PowerRipTimeArray = { 1000, 1500, 2000, 2500, 3000, 3500, 4000, 4500, 5000, 5500, 6000 };
                        int rnd = random.nextInt(PowerRipTimeArray.length);
                        _shockStunDuration = PowerRipTimeArray[rnd];
                        L1EffectSpawn.getInstance().spawnEffect(9415, _shockStunDuration, _target.getX(),
                                _target.getY(), _target.getMapId());
                        if (_target instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) _target;
                            // _target.setSkillEffect(L1SkillId.POWERRIP,
                            // _shockStunDuration);
                            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_RIP, true));

                        } else if (_target instanceof L1MonsterInstance || _target instanceof L1SummonInstance
                                || _target instanceof L1PetInstance) {
                            L1NpcInstance npc = (L1NpcInstance) _target;
                            // npc.setSkillEffect(L1SkillId.POWERRIP,
                            // _shockStunDuration);
                            npc.setCanNotMove(true);
                        }
                    }
                        break;
                    case DESPERADO: {
                        int[] stunTimeArray = { 1500, 2200, 2500, 3500, 4000, };
                        int rnd = random.nextInt(stunTimeArray.length);
                        _shockStunDuration = stunTimeArray[rnd];
                        L1EffectSpawn.getInstance().spawnEffect(9416, _shockStunDuration, _target.getX(),
                                _target.getY(), _target.getMapId());
                        if (_target instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) _target;
                            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_PERADO, true));

                            _target.setSkillEffect(L1SkillId.DESPERADO, _shockStunDuration);
                        } else if (_target instanceof L1MonsterInstance || _target instanceof L1SummonInstance
                                || _target instanceof L1PetInstance) {
                            L1NpcInstance npc = (L1NpcInstance) _target;
                            // npc.setSkillEffect(L1SkillId.DESPERADO,
                            // _shockStunDuration);
                            npc.setCanNotMove(true);
                        }
                    }
                        break;

                    case GREATER_HASTE: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        if (pc.getHasteItemEquipped() > 0) {
                            continue;
                        }
                        if (pc.getMoveSpeed() != 2) {
                            pc.setDrink(false);
                            pc.setMoveSpeed(1);
                            pc.sendPackets(new S_SkillHaste(pc.getId(), 1, _getBuffIconDuration));
                            pc.broadcastPacket(new S_SkillHaste(pc.getId(), 1, 0));
                        } else {
                            int skillNum = 0;
                            if (pc.hasSkillEffect(SLOW)) {
                                skillNum = SLOW;
                            } else if (pc.hasSkillEffect(GREATER_SLOW)) {
                                skillNum = GREATER_SLOW;
                            } else if (pc.hasSkillEffect(ENTANGLE)) {
                                skillNum = ENTANGLE;
                            } else if (pc.hasSkillEffect(MOB_SLOW_1)) {
                                skillNum = MOB_SLOW_1;
                            } else if (pc.hasSkillEffect(MOB_SLOW_18)) {
                                skillNum = MOB_SLOW_18;
                            }
                            if (skillNum != 0) {
                                pc.removeSkillEffect(skillNum);
                                pc.removeSkillEffect(GREATER_HASTE);
                                pc.setMoveSpeed(0);
                                continue;
                            }
                        }
                    }
                        break;
                    case HOLY_WALK:
                    case MOVING_ACCELERATION:
                    case WIND_WALK: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.setBraveSpeed(4);
                        pc.sendPackets(new S_SkillBrave(pc.getId(), 4, _getBuffIconDuration));
                        pc.broadcastPacket(new S_SkillBrave(pc.getId(), 4, 0));
                    }
                        break;
                    case BLOOD_LUST: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.setBraveSpeed(6);
                        pc.sendPackets(new S_SkillBrave(pc.getId(), 1, _getBuffIconDuration));
                        pc.broadcastPacket(new S_SkillBrave(pc.getId(), 1, 0));
                    }
                        break;
                    // クレイ血痕
                    case BUFF_CRAY: {
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.addHitup(5);
                            pc.addDmgup(1);
                            pc.addBowHitup(5);
                            pc.addBowDmgup(1);
                            pc.addExp(30);
                            pc.addMaxHp(100);
                            pc.addMaxMp(50);
                            pc.addHpr(3);
                            pc.addMpr(3);
                            pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                            pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                            pc.sendPackets(new S_SPMR(pc));
                        }
                    }
                        break;
                    case COMA_A:
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.getAbility().addAddedCon(1);
                            pc.getAbility().addAddedDex(5);
                            pc.getAbility().addAddedStr(5);
                            pc.addHitRate(3);
                            pc.getAC().addAc(-3);
                        }
                        break;
                    case COMA_B:
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            // pc.addSp(1);
                            pc.getAbility().addSp(1);
                            pc.getAbility().addAddedCon(3);
                            pc.getAbility().addAddedDex(5);
                            pc.getAbility().addAddedStr(5);
                            pc.addHitRate(5);
                            pc.getAC().addAc(-8);
                            pc.sendPackets(new S_SPMR(pc));
                        }
                        break;
                    case FEATHER_BUFF_A: { // 占いバフ（非常に良い）
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.addHpr(3);
                        pc.addMpr(3);
                        pc.addDmgup(2);
                        pc.addHitup(2);
                        pc.addMaxHp(50);
                        pc.addMaxMp(30);
                        // pc.addSp(2);
                        pc.getAbility().addSp(2);
                        pc.sendPackets(new S_SPMR(pc));
                        if (pc.isInParty()) {
                            pc.getParty().updateMiniHP(pc);
                        }
                        pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                        pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                    }
                        break;
                    case FEATHER_BUFF_B: { // 占いバフ（良い）
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.addHitup(2);
                        // pc.addSp(1);
                        pc.getAbility().addSp(1);
                        pc.addMaxHp(50);
                        pc.addMaxMp(30);
                        pc.sendPackets(new S_SPMR(pc));
                        if (pc.isInParty()) {
                            pc.getParty().updateMiniHP(pc);
                        }
                        pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                        pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                    }
                        break;
                    case FEATHER_BUFF_C: { // 占いバフ（通常）
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.addMaxHp(50);
                        pc.addMaxMp(30);
                        pc.getAC().addAc(-2);
                        pc.sendPackets(new S_OwnCharAttrDef(pc));
                        if (pc.isInParty()) {
                            pc.getParty().updateMiniHP(pc);
                        }
                        pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                        pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                    }
                        break;
                    case FEATHER_BUFF_D: { // 占いバフ（悪い）
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.getAC().addAc(-1);
                        pc.sendPackets(new S_OwnCharAttrDef(pc));
                    }
                        break;
                    case ANTA_MAAN: {// 地竜の魔眼
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.getAC().addAc(-2);
                        pc.getResistance().addHold(15);
                        pc.sendPackets(new S_OwnCharAttrDef(pc));
                    }
                        break;
                    case FAFU_MAAN: {// 水竜の魔眼
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.getResistance().addFreeze(15);
                        pc.sendPackets(new S_OwnCharAttrDef(pc));
                    }
                        break;
                    case LIND_MAAN: {// 風竜の魔眼
                        L1PcInstance pc = (L1PcInstance) cha;
                        // pc.addSp(1);
                        pc.getAbility().addSp(1);
                        pc.getResistance().addSleep(15);
                        pc.sendPackets(new S_SPMR(pc));
                        pc.sendPackets(new S_OwnCharAttrDef(pc));
                    }
                        break;
                    case VALA_MAAN: {// 火竜の魔眼
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.addDmgup(2);
                        pc.getResistance().addStun(15);
                        pc.sendPackets(new S_OwnCharAttrDef(pc));
                    }
                        break;
                    case BIRTH_MAAN: {// 誕生の魔眼
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.getAC().addAc(-2);
                        pc.getResistance().addHold(15);
                        pc.getResistance().addFreeze(15);
                        pc.sendPackets(new S_OwnCharAttrDef(pc));
                    }
                        break;
                    case SHAPE_MAAN: {// 形状の魔眼
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.getAC().addAc(-3);
                        // pc.addSp(1);
                        pc.getAbility().addSp(1);
                        pc.getResistance().addHold(15);
                        pc.getResistance().addFreeze(15);
                        pc.getResistance().addSleep(15);
                        pc.sendPackets(new S_SPMR(pc));
                        pc.sendPackets(new S_OwnCharAttrDef(pc));
                    }
                        break;
                    case LIFE_MAAN: {// 生命の魔眼
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.addDmgup(2);
                        pc.getAC().addAc(-5);
                        // pc.addSp(1);
                        pc.getAbility().addSp(1);
                        pc.getResistance().addHold(15);
                        pc.getResistance().addFreeze(15);
                        pc.getResistance().addSleep(15);
                        pc.getResistance().addStun(15);
                        pc.sendPackets(new S_SPMR(pc));
                        pc.sendPackets(new S_OwnCharAttrDef(pc));
                    }
                        break;
                    case ANTA_BUFF: {
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            if (pc.hasSkillEffect(L1SkillId.ANTA_BUFF))
                                pc.removeSkillEffect(L1SkillId.ANTA_BUFF);
                            pc.getAC().addAc(-2);
                            pc.getResistance().addWater(50);
                            pc.sendPackets(new S_OwnCharStatus(pc));
                            pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONBLOOD, 82, _getBuffIconDuration / 60));
                        }
                    }
                        break;
                    case FAFU_BUFF: {
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            if (pc.hasSkillEffect(L1SkillId.FAFU_BUFF))
                                pc.removeSkillEffect(L1SkillId.FAFU_BUFF);
                            pc.addHpr(3);
                            pc.addMpr(1);
                            pc.getResistance().addWind(50);
                            pc.sendPackets(new S_OwnCharStatus(pc));
                            pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONBLOOD, 85, _getBuffIconDuration / 60));
                        }
                    }
                        break;
                    case VALA_BUFF:
                    case RIND_BUFF: {
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            if (pc.hasSkillEffect(L1SkillId.RIND_BUFF))
                                pc.removeSkillEffect(L1SkillId.RIND_BUFF);
                            else if (pc.hasSkillEffect(L1SkillId.VALA_BUFF))
                                pc.removeSkillEffect(L1SkillId.VALA_BUFF);
                            pc.addHitup(3);
                            pc.addBowHitup(3);
                            pc.getResistance().addFire(50);
                            pc.sendPackets(new S_OwnCharStatus(pc));
                            pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONBLOOD, 88, _getBuffIconDuration / 60));
                        }
                    }
                        break;
                    case BOUNCE_ATTACK: {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.addHitup(6);
                    }
                        break;
                    // リンドビオル
                    case RINDVIOR_SUMMON_MONSTER_CLOUD: {
                        L1SpawnUtil.spawn(_npc, 5110, 10); // グルムデ精霊
                    }
                        break;
                    case RINDVIOR_PREDICATE: {
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            if (_npc.getLocation().getTileLineDistance(pc.getLocation()) > 4) {
                                L1Location newLoc = null;
                                for (int count = 0; count < 10; count++) {
                                    newLoc = _npc.getLocation().randomLocation(3, 4, false);
                                    if (_npc.glanceCheck(newLoc.getX(), newLoc.getY()) == true) {
                                        new L1Teleport().teleport(pc, newLoc.getX(), newLoc.getY(), _npc.getMapId(), 5,
                                                true);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                        break;
                    case RINDVIOR_SUMMON_MONSTER: {
                        Random _random = new Random();
                        int[] MobId = new int[] { 5106, 5107, 5108, 5109 }; // 鉱物
                        // ゴーレム
                        int rnd = _random.nextInt(100);
                        for (int i = 0; i < _random.nextInt(2) + 1; i++) {
                            L1SpawnUtil.spawn(_npc, MobId[rnd % MobId.length], _random.nextInt(3) + 8);
                        }
                    }
                        break;
                    case RINDVIOR_SILENCE: {
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            if (pc.isDead()) {
                                continue;
                            }
                            pc.setSkillEffect(L1SkillId.SILENCE, 12 * 1000);
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2177));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2177));
                        }
                    }
                        break;
                    case RINDVIOR_BOW: {
                        if (_target instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) _target;
                            if (pc.isDead()) {
                                continue;
                            }
                            int SprNum = 0;
                            int pcX = pc.getX();
                            int pcY = pc.getY();
                            int npcId = _npc.getNpcTemplate().get_npcId();
                            switch (npcId) {
                            case 5097:
                                pcY -= 6;
                                SprNum = 7987;
                                break;
                            case 5098:
                                pcX += 4;
                                pcY -= 4;
                                SprNum = 8050;
                                break;
                            case 5099:
                                pcX += 5;
                                SprNum = 8051;
                                break;
                            default:
                                break;
                            }
                            S_EffectLocation packet = new S_EffectLocation(pcX, pcY, SprNum);
                            pc.sendPackets(packet);
                            Broadcaster.broadcastPacket(pc, packet);
                        }
                    }
                        break;
                    case RINDVIOR_WIND_SHACKLE:
                    case RINDVIOR_WIND_SHACKLE_1: {
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            if (pc.isDead()) {
                                continue;
                            }
                            pc.setSkillEffect(L1SkillId.WIND_SHACKLE, 12 * 1000);
                            pc.sendPackets(new S_SkillIconWindShackle(pc.getId(), _getBuffIconDuration));
                            pc.sendPackets(new S_SkillSound(pc.getId(), 1799));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 1799));
                        }
                    }
                        break;
                    case RINDVIOR_PREDICATE_CANCELLATION: {
                        Random random = new Random();
                        int Chance = random.nextInt(100) + 1;
                        if (Chance > 33) {
                            if (cha instanceof L1PcInstance) {
                                L1PcInstance pc = (L1PcInstance) cha;
                                if (_npc.getLocation().getTileLineDistance(pc.getLocation()) > 4) {
                                    L1Location newLoc = null;
                                    for (int count = 0; count < 10; count++) {
                                        newLoc = _npc.getLocation().randomLocation(3, 4, false);
                                        if (_npc.glanceCheck(newLoc.getX(), newLoc.getY()) == true) {
                                            new L1Teleport().teleport(pc, newLoc.getX(), newLoc.getY(), _npc.getMapId(),
                                                    5, true);
                                            break;
                                        }
                                    }
                                }
                                for (int skillNum = SKILLS_BEGIN; skillNum <= SKILLS_END; skillNum++) {
                                    if (isNotCancelable(skillNum) && !pc.isDead()) {
                                        continue;
                                    }
                                    if (skillNum == SHAPE_CHANGE) {
                                        if (pc.getRankLevel() > 2 && pc.getTempCharGfx() >= 13715
                                                && pc.getTempCharGfx() <= 13745)
                                            continue;
                                    }
                                    pc.removeSkillEffect(skillNum);
                                }
                                for (int skillNum = STATUS_BEGIN; skillNum <= STATUS_CANCLEEND; skillNum++) {
                                    if (skillNum == STATUS_CHAT_PROHIBITED) {
                                        continue;
                                    }
                                    pc.removeSkillEffect(skillNum);
                                }
                                for (int skillNum = COOKING_BEGIN; skillNum <= COOKING_END; skillNum++) {
                                    if (isNotCancelable(skillNum) && !pc.isDead()) {
                                        continue;
                                    }
                                    pc.removeSkillEffect(skillNum);
                                }
                                pc.curePoison();
                                pc.cureParalaysis();
                                if (!(pc.getRankLevel() > 2 && pc.getTempCharGfx() >= 13715
                                        && pc.getTempCharGfx() <= 13745)) {
                                    L1PolyMorph.undoPoly(pc);
                                    pc.sendPackets(new S_CharVisualUpdate(pc));
                                    Broadcaster.broadcastPacket(pc, new S_CharVisualUpdate(pc));
                                }
                                if (pc.getHasteItemEquipped() > 0) {
                                    pc.setMoveSpeed(0);
                                    pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
                                    Broadcaster.broadcastPacket(pc, new S_SkillHaste(pc.getId(), 0, 0));
                                }
                                if (pc != null && pc.isInvisble()) {
                                    if (pc.hasSkillEffect(L1SkillId.INVISIBILITY)) {
                                        pc.killSkillEffectTimer(L1SkillId.INVISIBILITY);
                                        pc.sendPackets(new S_Invis(pc.getId(), 0));
                                        Broadcaster.broadcastPacket(pc, new S_Invis(pc.getId(), 0));
                                        pc.sendPackets(new S_Sound(147));
                                    }
                                    if (pc.hasSkillEffect(L1SkillId.BLIND_HIDING)) {
                                        pc.killSkillEffectTimer(L1SkillId.BLIND_HIDING);
                                        pc.sendPackets(new S_Invis(pc.getId(), 0));
                                        Broadcaster.broadcastPacket(pc, new S_Invis(pc.getId(), 0));
                                    }
                                }
                                pc.removeSkillEffect(STATUS_FREEZE);
                                pc.sendPackets(new S_SkillSound(pc.getId(), 870));
                                Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 870));
                            }
                        }
                    }
                        break;
                    case RINDVIOR_CANCELLATION: {
                        Random random = new Random();
                        int Chance = random.nextInt(100) + 1;
                        if (Chance > 33) {
                            if (cha instanceof L1PcInstance) {
                                L1PcInstance pc = (L1PcInstance) cha;
                                for (int skillNum = SKILLS_BEGIN; skillNum <= SKILLS_END; skillNum++) {
                                    if (isNotCancelable(skillNum) && !pc.isDead()) {
                                        continue;
                                    }
                                    if (skillNum == SHAPE_CHANGE) {
                                        if (pc.getRankLevel() > 2 && pc.getTempCharGfx() >= 13715
                                                && pc.getTempCharGfx() <= 13745)
                                            continue;
                                    }
                                    pc.removeSkillEffect(skillNum);
                                }
                                for (int skillNum = STATUS_BEGIN; skillNum <= STATUS_CANCLEEND; skillNum++) {
                                    if (skillNum == STATUS_CHAT_PROHIBITED) {
                                        continue;
                                    }
                                    pc.removeSkillEffect(skillNum);
                                }
                                for (int skillNum = COOKING_BEGIN; skillNum <= COOKING_END; skillNum++) {
                                    if (isNotCancelable(skillNum) && !pc.isDead()) {
                                        continue;
                                    }
                                    pc.removeSkillEffect(skillNum);
                                }
                                pc.curePoison();
                                pc.cureParalaysis();
                                if (!(pc.getRankLevel() > 2 && pc.getTempCharGfx() >= 13715
                                        && pc.getTempCharGfx() <= 13745)) {
                                    L1PolyMorph.undoPoly(pc);
                                    pc.sendPackets(new S_CharVisualUpdate(pc));
                                    Broadcaster.broadcastPacket(pc, new S_CharVisualUpdate(pc));
                                }
                                if (pc.getHasteItemEquipped() > 0) {
                                    pc.setMoveSpeed(0);
                                    pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
                                    Broadcaster.broadcastPacket(pc, new S_SkillHaste(pc.getId(), 0, 0));
                                }
                                if (pc != null && pc.isInvisble()) {
                                    if (pc.hasSkillEffect(L1SkillId.INVISIBILITY)) {
                                        pc.killSkillEffectTimer(L1SkillId.INVISIBILITY);
                                        pc.sendPackets(new S_Invis(pc.getId(), 0));
                                        Broadcaster.broadcastPacket(pc, new S_Invis(pc.getId(), 0));
                                        pc.sendPackets(new S_Sound(147));
                                    }
                                    if (pc.hasSkillEffect(L1SkillId.BLIND_HIDING)) {
                                        pc.killSkillEffectTimer(L1SkillId.BLIND_HIDING);
                                        pc.sendPackets(new S_Invis(pc.getId(), 0));
                                        Broadcaster.broadcastPacket(pc, new S_Invis(pc.getId(), 0));
                                    }
                                }
                                pc.removeSkillEffect(STATUS_FREEZE);
                                pc.sendPackets(new S_SkillSound(pc.getId(), 870));
                                Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 870));
                            }
                        }
                    }
                        break;
                    case RINDVIOR_WEAPON:
                    case RINDVIOR_WEAPON_2: {
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            L1ItemInstance weapon = pc.getWeapon();
                            Random random = new Random();
                            int rnd = random.nextInt(100) + 1;
                            if (weapon != null && rnd > 33) {
                                int weaponDamage = random.nextInt(3) + 1;
                                if (pc.isDead()) {
                                    continue;
                                }
                                pc.sendPackets(new S_ServerMessage(268, weapon.getLogName()));
                                pc.getInventory().receiveDamage(weapon, weaponDamage);
                                pc.sendPackets(new S_SkillSound(pc.getId(), 172));
                                Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 172));
                            }
                        }
                    }
                        break;
                    // ブラックエルダーデスヒル/キャンセレーション
                    case BLACKELDER_DEATH_HELL: {
                        Random random = new Random();
                        int Chance = random.nextInt(100) + 1;
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_SkillSound(pc.getId(), 7780));
                            pc.setSkillEffect(L1SkillId.PAP_DEATH_HELL, 12 * 1000);
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 7780));
                        }
                        if (Chance > 33) {
                            if (cha instanceof L1PcInstance) {
                                L1PcInstance pc = (L1PcInstance) cha;
                                for (int skillNum = SKILLS_BEGIN; skillNum <= SKILLS_END; skillNum++) {
                                    if (isNotCancelable(skillNum) && !pc.isDead()) {
                                        continue;
                                    }
                                    if (skillNum == SHAPE_CHANGE) {
                                        if (pc.getRankLevel() > 2 && pc.getTempCharGfx() >= 13715
                                                && pc.getTempCharGfx() <= 13745)
                                            continue;
                                    }
                                    pc.removeSkillEffect(skillNum);
                                }
                                for (int skillNum = STATUS_BEGIN; skillNum <= STATUS_CANCLEEND; skillNum++) {
                                    pc.removeSkillEffect(skillNum);
                                }
                                for (int skillNum = COOKING_BEGIN; skillNum <= COOKING_END; skillNum++) {
                                    if (isNotCancelable(skillNum) && !pc.isDead()) {
                                        continue;
                                    }
                                    pc.removeSkillEffect(skillNum);
                                }
                                pc.curePoison();
                                pc.cureParalaysis();
                                if (!(pc.getRankLevel() > 2 && pc.getTempCharGfx() >= 13715
                                        && pc.getTempCharGfx() <= 13745)) {
                                    L1PolyMorph.undoPoly(pc);
                                    pc.sendPackets(new S_CharVisualUpdate(pc));
                                    Broadcaster.broadcastPacket(pc, new S_CharVisualUpdate(pc));
                                }
                                if (pc.getHasteItemEquipped() > 0) {
                                    pc.setMoveSpeed(0);
                                    pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
                                    Broadcaster.broadcastPacket(pc, new S_SkillHaste(pc.getId(), 0, 0));
                                }
                                if (pc != null && pc.isInvisble()) {
                                    if (pc.hasSkillEffect(L1SkillId.INVISIBILITY)) {
                                        pc.killSkillEffectTimer(L1SkillId.INVISIBILITY);
                                        pc.sendPackets(new S_Invis(pc.getId(), 0));
                                        Broadcaster.broadcastPacket(pc, new S_Invis(pc.getId(), 0));
                                        pc.sendPackets(new S_Sound(147));
                                    }
                                    if (pc.hasSkillEffect(L1SkillId.BLIND_HIDING)) {
                                        pc.killSkillEffectTimer(L1SkillId.BLIND_HIDING);
                                        pc.sendPackets(new S_Invis(pc.getId(), 0));
                                        Broadcaster.broadcastPacket(pc, new S_Invis(pc.getId(), 0));
                                    }
                                }
                                pc.removeSkillEffect(STATUS_FREEZE);
                                pc.sendPackets(new S_SkillSound(pc.getId(), 870));
                                Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 870));
                            }
                        }
                    }
                        break;
                    // ドレイクマステレポート
                    case DRAKE_MASSTELEPORT: {
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            L1Location newLocation = pc.getLocation().randomLocation(5, true);
                            int newX = newLocation.getX();
                            int newY = newLocation.getY();
                            if (pc.isDead())
                                continue;
                            new L1Teleport().teleport(pc, newX, newY, pc.getMapId(), pc.getHeading(), true);
                        }
                    }
                        break;
                    // ドレイクウィンドセクル
                    case DRAKE_WIND_SHACKLE: {
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            if (pc.isDead()) {
                                continue;
                            }
                            pc.setSkillEffect(L1SkillId.WIND_SHACKLE, 12 * 1000);
                            pc.sendPackets(new S_SkillIconWindShackle(pc.getId(), _getBuffIconDuration));
                            pc.sendPackets(new S_SkillSound(pc.getId(), 1799));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 1799));
                        }
                    }
                        break;
                    // ブラックエルダーデスポーション
                    case BLACKELDER_DEATH_POTION: {
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_SkillSound(pc.getId(), 7781));
                            pc.setSkillEffect(L1SkillId.PAP_DEATH_PORTION, 12 * 1000);
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 7781));
                        }
                    }
                        break;
                    // イフリートサモンモンスター
                    case EFRETE_SUMMON_MONSTER: {
                        Random _random = new Random();
                        for (int i = 0; i < 2; i++) {
                            L1SpawnUtil.spawn(_npc, 5121, _random.nextInt(3) + 8);
                        }
                    }
                        break;
                    // フェニックスサモンモンスター
                    case PHOENIX_SUMMON_MONSTER: {
                        Random _random = new Random();
                        for (int i = 0; i < 2; i++) {
                            L1SpawnUtil.spawn(_npc, 900177, _random.nextInt(3) + 8);
                        }
                    }
                        break;
                    // フェニックスキャンセレーション
                    case PHOENIX_CANCELLATION: {
                        Random random = new Random();
                        int Chance = random.nextInt(100) + 1;
                        if (Chance > 33) {
                            if (cha instanceof L1PcInstance) {
                                L1PcInstance pc = (L1PcInstance) cha;
                                for (int skillNum = SKILLS_BEGIN; skillNum <= SKILLS_END; skillNum++) {
                                    if (isNotCancelable(skillNum) && !pc.isDead()) {
                                        continue;
                                    }
                                    if (skillNum == SHAPE_CHANGE) {
                                        if (pc.getRankLevel() > 2 && pc.getTempCharGfx() >= 13715
                                                && pc.getTempCharGfx() <= 13745)
                                            continue;
                                    }
                                    pc.removeSkillEffect(skillNum);
                                }
                                for (int skillNum = STATUS_BEGIN; skillNum <= STATUS_CANCLEEND; skillNum++) {
                                    if (skillNum == STATUS_CHAT_PROHIBITED) {
                                        continue;
                                    }
                                    pc.removeSkillEffect(skillNum);
                                }
                                for (int skillNum = COOKING_BEGIN; skillNum <= COOKING_END; skillNum++) {
                                    if (isNotCancelable(skillNum) && !pc.isDead()) {
                                        continue;
                                    }
                                    pc.removeSkillEffect(skillNum);
                                }
                                pc.curePoison();
                                pc.cureParalaysis();
                                if (!(pc.getRankLevel() > 2 && pc.getTempCharGfx() >= 13715
                                        && pc.getTempCharGfx() <= 13745)) {
                                    L1PolyMorph.undoPoly(pc);
                                    pc.sendPackets(new S_CharVisualUpdate(pc));
                                    Broadcaster.broadcastPacket(pc, new S_CharVisualUpdate(pc));
                                }
                                if (pc.getHasteItemEquipped() > 0) {
                                    pc.setMoveSpeed(0);
                                    pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
                                    Broadcaster.broadcastPacket(pc, new S_SkillHaste(pc.getId(), 0, 0));
                                }
                                if (pc != null && pc.isInvisble()) {
                                    if (pc.hasSkillEffect(L1SkillId.INVISIBILITY)) {
                                        pc.killSkillEffectTimer(L1SkillId.INVISIBILITY);
                                        pc.sendPackets(new S_Invis(pc.getId(), 0));
                                        Broadcaster.broadcastPacket(pc, new S_Invis(pc.getId(), 0));
                                        pc.sendPackets(new S_Sound(147));
                                    }
                                    if (pc.hasSkillEffect(L1SkillId.BLIND_HIDING)) {
                                        pc.killSkillEffectTimer(L1SkillId.BLIND_HIDING);
                                        pc.sendPackets(new S_Invis(pc.getId(), 0));
                                        Broadcaster.broadcastPacket(pc, new S_Invis(pc.getId(), 0));
                                    }
                                }
                                pc.removeSkillEffect(STATUS_FREEZE);
                                pc.sendPackets(new S_SkillSound(pc.getId(), 870));
                                Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 870));
                            }
                        }
                    }
                        break;
                    case AREA_OF_SILENCE: {
                        L1PcInstance pc = (L1PcInstance) _target;
                        pc.sendPackets(new S_PacketBox(S_PacketBox.POSION_ICON, pc, 6, 15));
                        Broadcaster.broadcastPacket(_target, new S_PacketBox(S_PacketBox.POSION_ICON, pc, 6, 15));
                        Broadcaster.broadcastPacket(_target, new S_SkillSound(pc.getId(), 10708));
                    }
                        break;
                    case DESERT_SKILL1: { // 広域カーズパラライズ
                        if (!cha.hasSkillEffect(EARTH_BIND) && !cha.hasSkillEffect(ICE_LANCE)
                                && !cha.hasSkillEffect(DESERT_SKILL1) && !cha.hasSkillEffect(DESERT_SKILL2)) {
                            if (cha instanceof L1PcInstance) {
                                L1PcInstance pc = (L1PcInstance) cha;
                                if (pc.isDead())
                                    continue;
                                L1CurseParalysis.curse(pc, 0, 4000);
                            }
                        }
                    }
                        break;
                    case DESERT_SKILL2: { // 広域アースバインド
                        if (!cha.hasSkillEffect(EARTH_BIND) && !cha.hasSkillEffect(ICE_LANCE)
                                && !cha.hasSkillEffect(DESERT_SKILL1) && !cha.hasSkillEffect(DESERT_SKILL2)) {
                            if (cha instanceof L1PcInstance) {
                                L1PcInstance pc = (L1PcInstance) cha;
                                if (pc.isDead())
                                    continue;

                                pc.setSkillEffect(EARTH_BIND, 12 * 1000); // ディケイポーション
                                pc.sendPackets(new S_Poison(pc.getId(), 2));
                                pc.broadcastPacket(new S_Poison(pc.getId(), 2));
                                pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, true));

                                pc.sendPackets(new S_SkillSound(pc.getId(), 2251));
                                Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2251));
                            }
                        }
                    }
                        break;
                    case DESERT_SKILL3: { // 広域マナドレイン
                        int ranMp = random.nextInt(20);
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            if (pc.getCurrentMp() <= ranMp || pc.isDead())
                                continue;
                            pc.setCurrentMp(pc.getCurrentMp() - ranMp);
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2172));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2172));
                        }
                    }
                        break;
                    case DESERT_SKILL4: { // 広域ポイズン
                        Random random = new Random();
                        int PoisonTime = random.nextInt(5) + 1;
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            if (PoisonTime > 2)
                                L1DamagePoison.doInfection(_user, pc, PoisonTime * 1000, 500, _skillId == TOMAHAWK);
                        }
                    }
                        break;
                    case DESERT_SKILL5: { // カース/ディケイ/ダークネス/ディジーズ/ウィークネス
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            if (pc.isDead() || pc.hasSkillEffect(CURSE_PARALYZE)) {
                                continue;
                            }
                            L1CurseParalysis.curse(cha, 0, 4000); // カーズパラライズ
                            pc.setSkillEffect(CURSE_PARALYZE, 4 * 1000); // カーズパラライズ
                            pc.sendPackets(new S_SkillSound(pc.getId(), 10704));
                            Broadcaster.broadcastPacket(cha, new S_SkillSound(pc.getId(), 10704));
                        }
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            if (pc.isDead() || pc.hasSkillEffect(DECAY_POTION)) {
                                continue;
                            }
                            pc.setSkillEffect(DECAY_POTION, 16 * 1000); // ディケイポーション
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2232));
                            Broadcaster.broadcastPacket(cha, new S_SkillSound(pc.getId(), 2232));
                        }
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            if (pc.isDead() || pc.hasSkillEffect(DARKNESS)) {
                                continue;
                            }
                            if (pc.hasSkillEffect(STATUS_FLOATING_EYE)) {
                                pc.sendPackets(new S_CurseBlind(2));
                            } else {
                                pc.sendPackets(new S_CurseBlind(1));
                            }
                            pc.setSkillEffect(DARKNESS, 32 * 1000); // ダークネス
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2175));
                            Broadcaster.broadcastPacket(cha, new S_SkillSound(pc.getId(), 2175));
                        }
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            if (pc.isDead() || pc.hasSkillEffect(DISEASE)) {
                                continue;
                            }
                            pc.addDmgup(-6);
                            pc.getAC().addAc(12);
                            pc.setSkillEffect(DISEASE, 64 * 1000); // ディジーズ
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2230));
                            Broadcaster.broadcastPacket(cha, new S_SkillSound(pc.getId(), 2230));
                        }
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            if (pc.isDead() || pc.hasSkillEffect(WEAKNESS)) {
                                continue;
                            }
                            pc.addDmgup(-5);
                            pc.addHitup(-1);
                            pc.setSkillEffect(WEAKNESS, 64 * 1000); // ウィークネス
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2228));
                            Broadcaster.broadcastPacket(cha, new S_SkillSound(pc.getId(), 2228));
                        }
                    }
                        break;
                    case DESERT_SKILL6: { // 広域ダークネス
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            if (pc.isDead() || pc.hasSkillEffect(DARKNESS)) {
                                continue;
                            }
                            if (pc.hasSkillEffect(STATUS_FLOATING_EYE)) {
                                pc.sendPackets(new S_CurseBlind(2));
                            } else {
                                pc.sendPackets(new S_CurseBlind(1));
                            }
                            pc.setSkillEffect(DARKNESS, 32 * 1000); // ダークネス
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2175));
                            Broadcaster.broadcastPacket(cha, new S_SkillSound(pc.getId(), 2175));
                        }
                    }
                        break;
                    case DESERT_SKILL7: { // 広域フォグ
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            if (pc.isDead() || pc.hasSkillEffect(FOG_OF_SLEEPING)) {
                                continue;
                            }
                            pc.setSkillEffect(FOG_OF_SLEEPING, 32 * 1000); // フォグオブスリーピング
                            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_SLEEP, true));
                            pc.sendPackets(new S_SkillSound(pc.getId(), 760));
                            Broadcaster.broadcastPacket(cha, new S_SkillSound(pc.getId(), 760));
                        }
                        cha.setSleeped(true);
                    }
                        break;
                    case DESERT_SKILL8: { // エルジャベトルネードダメージ
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_SkillSound(pc.getId(), 10082));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 10082));
                        }
                    }
                        break;
                    case DESERT_SKILL9: { // エルジャベサモンモンスター
                        for (int i = 0; i < 4; i++) {
                            L1SpawnUtil.spawn(_npc, 5138, 6, 120 * 1000); // グラカス
                            L1SpawnUtil.spawn(_npc, 5139, 6, 120 * 1000); // ベイカス
                            L1SpawnUtil.spawn(_npc, 5140, 6, 120 * 1000); // 号ルーカス
                            L1SpawnUtil.spawn(_npc, 5141, 6, 120 * 1000); // アルカス
                            L1SpawnUtil.spawn(_npc, 5142, 6, 120 * 1000); // 女王守護蟻
                            L1SpawnUtil.spawn(_npc, 5143, 6, 120 * 1000); // 女王守護蟻
                            L1SpawnUtil.spawn(_npc, 5144, 6, 120 * 1000); // 女王守護蟻
                            L1SpawnUtil.spawn(_npc, 5145, 6, 120 * 1000); // 女王守護蟻
                        }
                    }
                        break;
                    case DESERT_SKILL10: { // エルジャベ砂嵐
                        for (int i = 0; i < random.nextInt(3) + 1; i++) {
                            L1SpawnUtil.spawn(_npc, 5095, 6, 3 * 1000); // 砂嵐
                        }
                    }
                        break;
                    default:
                        break;
                    }
                }

                if (_calcType == PC_NPC || _calcType == NPC_NPC) {
                    if (_skillId == TAMING_MONSTER && ((L1MonsterInstance) cha).getNpcTemplate().isTamable()) {
                        int petcost = 0;
                        Object[] petlist = _user.getPetList().values().toArray();
                        for (Object pet : petlist) {
                            petcost += ((L1NpcInstance) pet).getPetcost();
                        }
                        int charisma = _user.getAbility().getTotalCha();
                        if (_player.isElf()) {
                            charisma += 12;
                        } else if (_player.isWizard()) {
                            charisma += 6;
                        }
                        charisma -= petcost;
                        if (charisma >= 6) {
                            L1SummonInstance summon = new L1SummonInstance(_targetNpc, _user, false);
                            _target = summon;
                        } else {
                            _player.sendPackets(new S_ServerMessage(319));
                        }
                    } else if (_skillId == CREATE_ZOMBIE) {
                        int petcost = 0;
                        Object[] petlist = _user.getPetList().values().toArray();
                        for (Object pet : petlist) {
                            petcost += ((L1NpcInstance) pet).getPetcost();
                        }
                        int charisma = _user.getAbility().getTotalCha();
                        if (_player.isElf()) {
                            charisma += 12;
                        } else if (_player.isWizard()) {
                            charisma += 6;
                        }
                        charisma -= petcost;
                        if (charisma >= 6) {
                            L1SummonInstance summon = new L1SummonInstance(_targetNpc, _user, true);
                            _target = summon;
                        } else {
                            _player.sendPackets(new S_ServerMessage(319));
                        }
                    } else if (_skillId == WEAK_ELEMENTAL) {
                        if (cha instanceof L1MonsterInstance) {
                            L1Npc npcTemp = ((L1MonsterInstance) cha).getNpcTemplate();
                            int weakAttr = npcTemp.get_weakAttr();
                            if ((weakAttr & 1) == 1) {
                                cha.broadcastPacket(new S_SkillSound(cha.getId(), 2169));
                            }
                            if ((weakAttr & 2) == 2) {
                                cha.broadcastPacket(new S_SkillSound(cha.getId(), 2167));
                            }
                            if ((weakAttr & 4) == 4) {
                                cha.broadcastPacket(new S_SkillSound(cha.getId(), 2166));
                            }
                            if ((weakAttr & 8) == 8) {
                                cha.broadcastPacket(new S_SkillSound(cha.getId(), 2168));
                            }
                        }
                    } else if (_skillId == RETURN_TO_NATURE) {
                        if (Config.RETURN_TO_NATURE && cha instanceof L1SummonInstance) {
                            L1SummonInstance summon = (L1SummonInstance) cha;
                            summon.broadcastPacket(new S_SkillSound(summon.getId(), 2245));
                            summon.returnToNature();
                        } else {
                            if (_user instanceof L1PcInstance) {
                                _player.sendPackets(new S_ServerMessage(79));
                            }
                        }
                    }
                }

                if (_skill.getType() == L1Skills.TYPE_HEAL && _calcType == PC_NPC && undeadType == 1) {
                    dmg *= -1;
                }

                if (_skill.getType() == L1Skills.TYPE_HEAL && _calcType == PC_NPC && undeadType == 3) {
                    dmg = 0;
                }

                if ((cha instanceof L1TowerInstance || cha instanceof L1DoorInstance) && dmg < 0) {
                    dmg = 0;
                }

                if (dmg != 0 || drainMana != 0) {
                    /** ディスネストさ不可 **/
                    if (_skillId == DISINTEGRATE && dmg > 0) {
                        if (dmg > 1000)
                            dmg = 1200;
                        if (cha.hasSkillEffect(NO_DIS))
                            dmg = 0;
                        else
                            cha.setSkillEffect(NO_DIS, 3000);
                    }
                    /** ディスネストさ不可 **/
                    // ポーは無視する
                    if (_skillId != FOU_SLAYER)
                        _magic.commit(dmg, drainMana);
                }

                if (heal > 0) {
                    if ((heal + _user.getCurrentHp()) > _user.getMaxHp()) {
                        _user.setCurrentHp(_user.getMaxHp());
                    } else {
                        _user.setCurrentHp(heal + _user.getCurrentHp());
                    }
                }

                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.getLight().turnOnOffLight();
                    pc.sendPackets(new S_OwnCharAttrDef(pc));
                    pc.sendPackets(new S_OwnCharStatus(pc));
                    sendHappenMessage(pc);
                }

                addMagicList(cha, false);
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.getLight().turnOnOffLight();
                }
            }

            if (_skillId == DETECTION || _skillId == IZE_BREAK || _skillId == EYE_OF_DRAGON
                    || _skillId == COUNTER_DETECTION) {// キャンセル
                detection(_player, true);
            }

        } catch (Exception e) {
            // スキルエラー部分にキャラクター名、モプミョン、ターゲット名順に出力

            System.out.println("エラー：" + (_player != null ? _player.getAccountName() : "") + " | "
                    + (_npc != null ? _npc.getName() : "") + " | " + (_target != null ? _target.getName() : ""));
            // system message output
            e.printStackTrace();

            // _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
    }

    private void summonMonster(L1PcInstance pc, int level, int order) {
        int[] summonid_list = null;
        int summonid = 0;
        int summoncost = 8;

        level *= 4;
        switch (level) {
        case 28:
            summonid_list = new int[] { 81083, 81084 };
            summonid = summonid_list[order];
            break;
        case 40:
            summonid = 81085;
            break;
        case 52:
            summonid = 81086;
            break;
        case 64:
            summonid = 81087;
            break;
        case 76:
            summonid = 81088;
            break;
        case 80:
            summonid = 81089;
            break;
        case 82:
            summonid = 81090;
            break;
        case 84:
            summonid = 81091;
            break;
        case 86:
            summonid = 81092;
            break;
        case 88:
            summonid = 81093;
            break;
        }
        int petcost = 0;
        Object[] petlist = pc.getPetList().values().toArray();
        for (Object pet : petlist) {
            petcost += ((L1NpcInstance) pet).getPetcost();
        }
        if ((summonid == 810848 || summonid == 810850 || summonid == 810849) && petcost != 0) {
            pc.sendPackets(new S_CloseList(pc.getId()));
            return;
        }
        int charisma = pc.getAbility().getTotalCha() + 6 - petcost;
        int summoncount = 0;
        if (level <= 52) {
            summoncount = charisma / summoncost;
        } else if (level == 56) {
            summoncount = charisma / (summoncost + 2);
        } else if (level == 60) {
            summoncount = charisma / (summoncost + 4);
        } else if (level == 64) {
            summoncount = charisma / (summoncost + 6);
        } else {
            summoncount = charisma / summoncost;
        }

        if (level <= 52 && summoncount > 5) {
            summoncount = 5;
        } else if (level == 56 && summoncount > 4) {
            summoncount = 4;
        } else if (level == 60 && summoncount > 3) {
            summoncount = 3;
        } else if (level == 64 && summoncount > 2) {
            summoncount = 2;
        }

        L1Npc npcTemp = NpcTable.getInstance().getTemplate(summonid);
        L1SummonInstance summon = null;
        for (int cnt = 0; cnt < summoncount; cnt++) {
            summon = new L1SummonInstance(npcTemp, pc);
            if (summonid == 810848 || summonid == 810850 || summonid == 810849) {
                summon.setPetcost(pc.getAbility().getTotalCha() + 7);
            } else {
                if (level <= 52)
                    summon.setPetcost(summoncost);
                else if (level == 56)
                    summon.setPetcost(summoncost + 2);
                else if (level == 60)
                    summon.setPetcost(summoncost + 4);
                else if (level == 64)
                    summon.setPetcost(summoncost + 6);
                else
                    summoncount = charisma / summoncost;
            }
        }
    }

    /**
     * キャンセルに解除することができないスキルなのかを返す。
     */
    private boolean isNotCancelable(int skillNum) {
        return skillNum == ABSOLUTE_BARRIER || skillNum == ADVANCE_SPIRIT || skillNum == SHOCK_STUN
                || skillNum == REDUCTION_ARMOR || skillNum == SOLID_CARRIAGE || skillNum == COUNTER_BARRIER
                || skillNum == COMA_A || skillNum == COMA_B || skillNum == ANTA_MAAN || skillNum == FAFU_MAAN
                || skillNum == LIND_MAAN || skillNum == VALA_MAAN || skillNum == BIRTH_MAAN || skillNum == SHAPE_MAAN
                || skillNum == LIFE_MAAN || skillNum == ANTA_BUFF || skillNum == FAFU_BUFF || skillNum == ANTA_MESSAGE_6
                || skillNum == ANTA_MESSAGE_7 || skillNum == ANTA_MESSAGE_8 || skillNum == PREDICATEDELAY
                || skillNum == FEATHER_BUFF_A || skillNum == FEATHER_BUFF_B || skillNum == FEATHER_BUFF_C
                || skillNum == FEATHER_BUFF_D || skillNum == PAP_DEATH_PORTION || skillNum == PAP_DEATH_HELL
                || skillNum == PAP_REDUCE_HELL || skillNum == STATUS_DRAGON_PEARL || skillNum == UNCANNY_DODGE
                || skillNum == DRESS_EVASION || skillNum == SHADOW_ARMOR || skillNum == OMAN_STUN
                || skillNum == SCALES_EARTH_DRAGON || skillNum == SCALES_WATER_DRAGON || skillNum == SCALES_FIRE_DRAGON
                || skillNum == COOK_STR || skillNum == COOK_DEX || skillNum == COOK_INT || skillNum == ARMOR_BRAKE
                || skillNum == SHADOW_FANG || skillNum == CLANBUFF_YES || skillNum == God_buff || skillNum == COOK_GROW
                || skillNum == DESPERADO || skillNum == MIRROR_IMAGE || skillNum == RANK_BUFF_1
                || skillNum == RANK_BUFF_2 || skillNum == RANK_BUFF_3 || skillNum == RANK_BUFF_4
                || skillNum == RANK_BUFF_5;
    }

    private void detection(L1PcInstance pc, boolean detectAll) {
        if (pc == null) {
            return;
        }

        if (!pc.isGmInvis() && pc.isInvisble() && !pc.isGhost()) {
            unequipInvisItem(pc);

            pc.delInvis();
            pc.beginInvisTimer();
        }

        if (detectAll) {
            for (L1PcInstance tgt : L1World.getInstance().getVisiblePlayer(pc)) {
                if (!tgt.isGmInvis() && tgt.isInvisble() && !pc.isGhost()) {
                    unequipInvisItem(tgt);
                    tgt.delInvis();
                }
            }

            L1WorldTraps.getInstance().onDetection(pc);
        }
    }

    private void unequipInvisItem(L1PcInstance pc) {
        if (pc == null) {
            return;
        }

        L1ItemInstance invisItem = pc.getInventory().getEquippedItem(20077);
        if (invisItem != null) {
            pc.getInventory().setEquipped(invisItem, false);
        }

        invisItem = pc.getInventory().getEquippedItem(20062);
        if (invisItem != null) {
            pc.getInventory().setEquipped(invisItem, false);
        }

        invisItem = pc.getInventory().getEquippedItem(120077);
        if (invisItem != null) {
            pc.getInventory().setEquipped(invisItem, false);
        }
    }

    /*
     * private void detection(L1PcInstance pc) { if (pc == null ) { return; }
     *
     * if (!pc.isGmInvis() && pc.isInvisble() && !pc.isGhost()) { pc.delInvis();
     * pc.beginInvisTimer(); }
     *
     * for (L1PcInstance tgt : L1World.getInstance().getVisiblePlayer(pc)) { if
     * (!tgt.isGmInvis() && tgt.isInvisble() && !pc.isGhost()) { tgt.delInvis();
     * } } L1WorldTraps.getInstance().onDetection(pc); }
     */

    private boolean isTargetCalc(L1Character cha) {
        if (_skill.getTarget().equals("attack") && _skillId != 18) {
            if (isPcSummonPet(cha)) {
                if (_player.getZoneType() == 1 || cha.getZoneType() == 1 || _player.checkNonPvP(_player, cha)) {
                    return false;
                }
            }
        }

        if (_skillId == FOG_OF_SLEEPING && _user.getId() == cha.getId()) {
            return false;
        }

        if (_skillId == GREATER_SLOW) {
            if (_user.getId() == cha.getId()) {
                return false;
            }
            if (cha instanceof L1SummonInstance) {
                L1SummonInstance summon = (L1SummonInstance) cha;
                if (_user.getId() == summon.getMaster().getId()) {
                    return false;
                }
            } else if (cha instanceof L1PetInstance) {
                L1PetInstance pet = (L1PetInstance) cha;
                if (_user.getId() == pet.getMaster().getId()) {
                    return false;
                }
            }
        }

        if (_skillId == MASS_TELEPORT) {
            if (_user.getId() != cha.getId()) {
                return false;
            }
        }

        return true;
    }

    private boolean isPcSummonPet(L1Character cha) {
        if (_calcType == PC_PC) {
            return true;
        }

        if (_calcType == PC_NPC) {
            if (cha instanceof L1SummonInstance) {
                L1SummonInstance summon = (L1SummonInstance) cha;
                if (summon.isExsistMaster()) {
                    return true;
                }
            }
            if (cha instanceof L1PetInstance) {
                return true;
            }
            if (cha instanceof L1SupportInstance) {
                return true;
            }
        }
        return false;
    }

    private boolean isUseCounterMagic(L1Character cha) {
        if (_isCounterMagic && cha.hasSkillEffect(COUNTER_MAGIC)) {
            cha.removeSkillEffect(COUNTER_MAGIC);
            // int castgfx =
            // SkillsTable.getInstance().getTemplate(COUNTER_MAGIC).getCastGfx();
            cha.broadcastPacket(new S_SkillSound(cha.getId(), 10702));
            if (cha instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) cha;
                pc.sendPackets(new S_SkillSound(pc.getId(), 10702));
            }
            return true;
        }
        return false;
    }

    private boolean isTargetFailure(L1Character cha) {
        boolean isTU = false;
        boolean isErase = false;
        boolean isManaDrain = false;
        int undeadType = 0;

        if (cha instanceof L1TowerInstance || cha instanceof L1DoorInstance) {
            return true;
        }

        if (cha instanceof L1PcInstance) {
            if (_calcType == PC_PC && _player.checkNonPvP(_player, cha)) {
                L1PcInstance pc = (L1PcInstance) cha;
                if (_player.getId() == pc.getId() || (pc.getClanid() != 0 && _player.getClanid() == pc.getClanid())) {
                    return false;
                }
                return true;
            }
            return false;
        }

        if (cha instanceof L1MonsterInstance) {
            isTU = ((L1MonsterInstance) cha).getNpcTemplate().get_IsTU();
        }

        if (cha instanceof L1MonsterInstance) {
            isErase = ((L1MonsterInstance) cha).getNpcTemplate().get_IsErase();
        }

        if (cha instanceof L1MonsterInstance) {
            undeadType = ((L1MonsterInstance) cha).getNpcTemplate().get_undead();
        }

        if (cha instanceof L1MonsterInstance) {
            isManaDrain = true;
        }
        if ((_skillId == TURN_UNDEAD && (undeadType == 0 || undeadType == 2))
                || (_skillId == TURN_UNDEAD && isTU == false)
                || ((_skillId == ERASE_MAGIC || _skillId == SLOW || _skillId == MOB_SLOW_1 || _skillId == MOB_SLOW_18
                        || _skillId == MANA_DRAIN || _skillId == GREATER_SLOW || _skillId == ENTANGLE
                        || _skillId == WIND_SHACKLE) && isErase == false)
                || (_skillId == MANA_DRAIN && isManaDrain == false)) {
            return true;
        }
        return false;
    }

    public void removeNewIcon(L1PcInstance pc, int skillid) {
        switch (skillid) {
        case ABSOLUTE_BLADE:
        case DEATH_HEAL:
        case ASSASSIN:
        case SOUL_BARRIER:
        case DESTROY:
        case IMPACT:
        case TITANL_RISING:
        case BLAZING_SPIRITS:
        case GRACE_AVATAR:
            pc.sendPackets(new S_NewSkillIcon(skillid, false, -1));
            pc.removeSkillEffect(skillid);
            break;
        default:
            break;
        }
    }
}
