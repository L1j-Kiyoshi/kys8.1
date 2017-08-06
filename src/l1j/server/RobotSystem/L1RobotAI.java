package l1j.server.RobotSystem;

import static l1j.server.server.model.skill.L1SkillId.*;

import java.util.Random;

import l1j.server.Config;
import l1j.server.server.ActionCodes;
import l1j.server.server.command.executor.L1Robot4;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.datatables.SprTable;
import l1j.server.server.model.AcceleratorChecker.ACT_TYPE;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.Getback;
import l1j.server.server.model.L1Astar;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1HateList;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Node;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1GuardianInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1ScarecrowInstance;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.model.trap.L1WorldTraps;
import l1j.server.server.serverpackets.S_AttackPacket;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_MoveCharPacket;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillIconGFX;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.templates.L1Skills;
import l1j.server.server.utils.CommonUtil;


public class L1RobotAI {

    private final byte HEADING_TABLE_X[] = { 0, 1, 1, 1, 0, -1, -1, -1 };
    private final byte HEADING_TABLE_Y[] = { -1, -1, 0, 1, 1, 1, 0, -1 };

    private int searchCount = 0;
    private int pickupCount = 0;
    private int teleportDelayCount = 2;
    private int moveCount = 0;
    private int moveDelayCount = 0;
    private int polyCount = 0;
    private int polyDelayCount = 0;
    private int skillDelayCount = 0;
    private int dieDelayCount = 0;
    private int postionCount = 0;
    private int curePostionCount = 0;
    private int cancellationCount = 0;


    private int x = 0;
    private int y = 0;
    private L1Object targetObj = null;

    public int getCancellationCount() {
        return cancellationCount;
    }

    public void setCancellationCount(int cancellationCount) {
        this.cancellationCount = cancellationCount;
    }

    private L1PcInstance robot; // ロボット対象
    private L1Character target;
    private L1Object dropItem;
    private L1Astar aStar;
    private L1Node tail;
    private int iCurrentPath;
    private int[][] iPath;

    private int gfxid;
    private int weapon;
    private int interval;
    private double HASTE_RATE = 0.75;
    private double WAFFLE_RATE = 0.875;
    private double THIRDSPEED_RATE = 0.87;
    private String polyList = "6274, 6277, 6273, 6276";
    private L1Inventory groundInventory;
    private L1Object object;


    private long ai_start_time; // 人工知能の開始時刻の値
    @SuppressWarnings("unused")
    private long ai_time; // 人工知能処理に使用されるフレーム参考値

    // テレポートした狩り場の位置一時的に保存用。
    private RobotLocation location;
    private L1Skills l1skills;
    // 人工知能の状態変数
    public final int AI_STATUS_SETTING = 0;  //序盤セッティング処理
    public final int AI_STATUS_WALK = 1;     // ランダムウォーク状態
    public final int AI_STATUS_ATTACK = 2;   //攻撃状態
    public final int AI_STATUS_DEAD = 3;     // 死んだ状態
    public final int AI_STATUS_CORPSE = 4;   // 死体の状態
    public final int AI_STATUS_SPAWN = 5;    // スポン状態
    public final int AI_STATUS_ESCAPE = 6;   // 逃げ状態
    public final int AI_STATUS_PICKUP = 7;   // アイテム拾い状態
    public final int AI_STATUS_SHOP = 8;     // 店に移動処理

    // 村でバフスタッフ確認用
    private int buff_step;

    public L1RobotAI(L1PcInstance pc) {
        robot = pc;
        attackList = new L1HateList();
        aStar = new L1Astar();
        iPath = new int[300][2];
        postionCount = CommonUtil.random(50, 200);
    }

    private boolean active = false;    // 有効

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int type = 1;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int statusType = 0;

    private int ai_Status = 1; //人工知能処理すべき状態

    public int getAiStatus() {
        return ai_Status;
    }

    public void setAiStatus(int aiStatus) {
        this.ai_Status = aiStatus;
    }

    private L1HateList attackList;

    public L1HateList getAttackList() {
        return attackList;
    }

    public void setAttackList(L1HateList attackList) {
        this.attackList = attackList;
    }


    private int getFrame(int gfx, int gfxmode) {
        int AttackSpeed = 0;

        switch (robot.getRobotAi().getType()) {
            case 1:
                AttackSpeed = 600;
                break;
            case 2:
                AttackSpeed = 0;
                break;

        }

        if (robot.getGfxId() == robot.getTempCharGfx()) {
            AttackSpeed = 600;
        } else {
            if (!robot.isElf()) {
                AttackSpeed = 0;
            } else {
                AttackSpeed = 200;
            }
        }

        switch (gfxmode) {
            case 0: // 移動
                return 1000;
            case 1: //攻撃
                return 800 + AttackSpeed;
        }
        return 1000;
    }


    public void toAI(long time) {
        try {
            if (!isAi(time)) {
                return;
            }
            if (robot.getName().equals(Config.ROBOT_NAME)) {
                for (L1Character cha : attackList.toTargetArrayList()) {
                    System.out.println(cha.getName() + " " + cha.isDead());
                }
                if (target != null)
                    System.out.println("AI:" + ai_Status + " " + target.getName());
                else
                    System.out.println("AI:" + ai_Status);
            }
            if (robot.isDead()) {
                toAiDead();
                return;
            }

            if (type != 1 && ai_Status != AI_STATUS_SETTING && robot.getMap().isSafetyZone(robot.getLocation())) {
                attackList.clear();
                target = null;
                dropItem = null;
                groundInventory = null;
                object = null;
                setAiStatus(AI_STATUS_SETTING);
            }

            switch (ai_Status) {
                case AI_STATUS_WALK:
                    toSearchTarget();
                    if (attackList.toTargetArrayList().size() > 0) {
                        setAiStatus(AI_STATUS_ATTACK);
                    }
                    break;
                case AI_STATUS_PICKUP:
                    pickup(robot);
                    break;
                case AI_STATUS_ATTACK:
                case AI_STATUS_ESCAPE:
                    if (attackList.toTargetArrayList().size() == 0) {
                        setAiStatus(AI_STATUS_WALK);
                    }
                    break;
                default:
                    break;
            }

            if (getAiStatus() != AI_STATUS_DEAD && robot.isDead()) {
                setAiStatus(AI_STATUS_DEAD);
            }

            ai_start_time = time;


            //ポーション服用処理。

            toHealingPostion(false);
            //ポーション服用処理。
            // 速度ポーション服用処理。
            if (cancellationCount <= 0) {
                toSpeedPostion();

                // 変身処理
                if (type == 1) {
                    int ran = CommonUtil.random(100);

                    if (ran <= 5 && polyDelayCount <= 0) {
                        if (polyCount <= 0) {
                            toPolyMorph();
                            polyCount = CommonUtil.random(3500, 4500);
                        }
                        if (polyDelayCount <= 0) {
                            polyDelayCount = CommonUtil.random(3000, 4000);
                        } else {
                            polyDelayCount--;
                        }
                    } else {
                        polyDelayCount = CommonUtil.random(3000, 4000);
                    }

                    if (polyDelayCount > 0) {
                        polyDelayCount--;
                    }
                    if (polyCount > 0) {
                        polyCount--;
                    }
                } else {
                    if (robot.getGfxId() == robot.getTempCharGfx()) {
                        toPolyMorph();
                    }
                }

                cancellationCount = 0;
            } else {
                cancellationCount--;
                polyCount = 0;
                return;
            }

            switch (getAiStatus()) {
                case AI_STATUS_SETTING:
                    toAiSetting();
                    break;
                case AI_STATUS_WALK:
                    toRandomWalk();
                    break;
                case AI_STATUS_ATTACK:
                    toAiAttack();
                    break;
                case AI_STATUS_DEAD:
                    toAiDead();
                    break;
                case AI_STATUS_CORPSE:
                    //toAiCorpse(time);
                    break;
                case AI_STATUS_SPAWN:
                    //toAiSpawn(time);
                    break;
                case AI_STATUS_ESCAPE:
                    //toAiEscape(time);
                    break;
                case AI_STATUS_PICKUP:
                    pickup(robot);
                    break;
                case AI_STATUS_SHOP:
                    toShopMove(time);
                    break;
                default://ここ
                    ai_time = 1000;
                    break;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    public void scarecrowProcess(long time) {
        try {

            if (!isAi(time)) {
                return;
            }

            if (robot.getName().equals(Config.ROBOT_NAME)) {
                for (L1Character cha : attackList.toTargetArrayList()) {
                    System.out.println(cha.getName() + " " + cha.isDead());
                }
                if (target != null)
                    System.out.println(ai_Status + " " + target.getName());
                else
                    System.out.println(ai_Status);
            }
            if (robot.isDead()) {
                toAiDead();
                return;
            }

            if (type != 1 && ai_Status != AI_STATUS_SETTING && robot.getMap().isSafetyZone(robot.getLocation())) {
                attackList.clear();
                target = null;
                dropItem = null;
                groundInventory = null;
                object = null;
                setAiStatus(AI_STATUS_SETTING);
            }

            switch (ai_Status) {
                case AI_STATUS_WALK:
                    //toSearchTarget();
                    search_target();
                    if (attackList.toTargetArrayList().size() > 0) {
                        setAiStatus(AI_STATUS_ATTACK);
                    }
                    break;
                case AI_STATUS_PICKUP:
                    pickup(robot);
                    break;
                case AI_STATUS_ATTACK:
                    //	case AI_STATUS_ESCAPE:
                    if (attackList.toTargetArrayList().size() == 0) {
                        setAiStatus(AI_STATUS_WALK);
                    }
                    break;
                default:
                    break;
            }

            if (getAiStatus() != AI_STATUS_DEAD && robot.isDead()) {
                setAiStatus(AI_STATUS_DEAD);
            }
            ai_start_time = time;
            // ポーション服用処理。

            toHealingPostion(false);

            // 速度ポーション服用処理。
            if (cancellationCount <= 0) {
                toSpeedPostion1();//かかしロボットポーション処理の設定
                // 変身処理
                if (type == 1) {
                    int ran = CommonUtil.random(100);

                    if (ran <= 5 && polyDelayCount <= 0) {
                        if (polyCount <= 0) {
                            toPolyMorph();
                            polyCount = CommonUtil.random(3500, 4500);
                        }
                        if (polyDelayCount <= 0) {
                            polyDelayCount = CommonUtil.random(3000, 4000);
                        } else {
                            polyDelayCount--;
                        }
                    } else {
                        polyDelayCount = CommonUtil.random(3000, 4000);
                    }

                    if (polyDelayCount > 0) {
                        polyDelayCount--;
                    }
                    if (polyCount > 0) {
                        polyCount--;
                    }
                } else {
                    if (robot.getGfxId() == robot.getTempCharGfx()) {
                        toPolyMorph();
                    }
                }

                cancellationCount = 0;
            } else {
                cancellationCount--;
                polyCount = 0;
                return;
            }

            switch (getAiStatus()) {
                case AI_STATUS_SETTING:
                    toAiSetting();
                    break;
                case AI_STATUS_WALK:
                    //toRandomWalk();
                    si_move();
                    break;
                case AI_STATUS_ATTACK:
//				toAiAttack();
                    to_ai_attack();
                    break;
                case AI_STATUS_DEAD:
                    toAiDead();
                    break;
                case AI_STATUS_PICKUP:
                    pickup(robot);
                    break;
                case AI_STATUS_SHOP:
                    toShopMove(time);
                    break;
                default://ここ
                    ai_time = 1000;
                    break;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }

    }

    private void toShopMove(long time) {
        try {
            if (targetObj == null) {
                for (L1Object obj : L1World.getInstance().getVisibleObjects(robot, 20)) {
                    if (obj instanceof L1NpcInstance) {
                        L1NpcInstance npc = (L1NpcInstance) obj;
                        if (npc.getNpcId() == 900088) {
                            x = npc.getX() - L1Robot4.random(1, 5);
                            y = npc.getY() - L1Robot4.random(1, 5);
                            targetObj = obj;
                            break;
                        }
                    }
                }
            }

            if (targetObj == null) {
                setAiStatus(AI_STATUS_WALK);
                return;
            }

            if (x != 0 && y != 0) {
                if (!isDistance(robot.getX(), robot.getY(), robot.getMapId(), x, y, targetObj.getMapId(), 1)) {
                    ai_time = 500;
                    toMoving(targetObj, x, y, 0, true);
                    return;
                } else {
                    targetObj = null;
                    x = 0;
                    y = 0;
                }
            }
            // 状態の変更。
            setAiStatus(AI_STATUS_WALK);
        } catch (Exception e) {

        }
    }

    public void checkTarget() {
        try {
            if (target == null || target.getMapId() != robot.getMapId() || target.isDead() || target.getCurrentHp() <= 0
                    || (target.isInvisble() && !attackList.containsKey(target))) {
                if (target != null) {
                    tagertClear();
                }
                if (!attackList.isEmpty()) {
                    target = attackList.getMaxHateCharacter();
                    checkTarget();
                }
            }
        } catch (Exception e) {
        }
    }

    public void targetRemove(L1Character target) {
        attackList.remove(target);
        if (target != null && target.equals(target)) {
            target = null;
        }
    }

    public void tagertClear() {
        if (target == null) {
            return;
        }
        attackList.remove(target);
        target = null;
    }

    public void setHate(L1Character cha, int hate) {
        if (cha != null && cha.getId() != robot.getId()) {
            if (!attackList.containsKey(cha)) {
                attackList.add(cha, hate);
            }
            if (target == null) {
                target = attackList.getMaxHateCharacter();
            }

            checkTarget();
        }
    }

    /**
     * 人工知能有効にする時間がドゥェトことを確認してくれる関数です。
     *
     * @param time
     * @return
     */
    private boolean isAi(long time) {
        long temp = time - ai_start_time;
        if (robot.hasSkillEffect(L1SkillId.SHOCK_STUN) ||
                robot.hasSkillEffect(L1SkillId.ICE_LANCE) ||
                robot.hasSkillEffect(L1SkillId.BONE_BREAK) ||
                robot.hasSkillEffect(L1SkillId.EARTH_BIND) ||
                robot.hasSkillEffect(L1SkillId.MOB_RANGESTUN_19) ||
                robot.hasSkillEffect(L1SkillId.MOB_SHOCKSTUN_30) ||
                robot.hasSkillEffect(L1SkillId.OMAN_STUN) ||
                robot.hasSkillEffect(L1SkillId.ANTA_MESSAGE_6) ||
                robot.hasSkillEffect(L1SkillId.ANTA_MESSAGE_7) ||
                robot.hasSkillEffect(L1SkillId.MOB_COCA) ||
                robot.hasSkillEffect(L1SkillId.MOB_CURSEPARALYZ_18) ||
                robot.hasSkillEffect(L1SkillId.ANTA_SHOCKSTUN)) {
            return false;
        }

        gfxid = robot.getTempCharGfx();
        weapon = robot.getCurrentWeapon();
        //  - 追加のソース
        interval = SprTable.getInstance().getAttackSpeed(robot.getTempCharGfx(), robot.getCurrentWeapon() + 1, robot.getLevel(), robot.getClassId());

        // ウィンドセクルかかった状態であれば、
        if (robot.equals(ACT_TYPE.ATTACK) && this.robot.hasSkillEffect(167)) {
            interval *= 2;
        }

        if (robot.isHaste()) {
            interval *= HASTE_RATE;
        }
        if (robot.getBraveSpeed() == 4) {
            interval *= HASTE_RATE;
        }
        if (robot.isBrave()) {
            interval *= HASTE_RATE;
        }
        if (robot.isElfBrave()) {
            interval *= WAFFLE_RATE;
        }
        if (robot.isDragonPearl()) {
            interval *= THIRDSPEED_RATE;
        }

        if (polyList.indexOf(String.valueOf(gfxid)) > -1) {
            if (statusType == 1) {
                interval += 50;
            } else if (statusType == 2) {
                interval += 120;
            }
        } else {
            if (type != 1) {
                if (statusType == 1) {
                    interval -= 10;
                } else if (statusType == 2) {
                    interval -= 0;
                }
            }
        }

        if (robot.isBlackwizard()) {
            if (type != 1) {
                if (statusType == 1) {
                    interval -= 10;
                } else if (statusType == 2) {
                    interval += 50;
                }
            }
        }

        if (temp < interval) {
            return false;
        }

        if (temp >= interval) {
            if (robot.getName().equals(Config.ROBOT_NAME)) {
                //System.out.println(time + " " + ai_start_time);
                System.out.println("AI :" + temp + " " + interval);
            }
            ai_start_time = time;
            return true;
        }
        return false;
    }

    /**
     * 村での基本的なセッティングを処理するときに使用。
     *
     * @param time
     */
    private void toAiSetting() {
        statusType = 0;

        ai_time = getFrame(robot.getGfxId(), 0);

        // 村に帰還。
        if (!robot.getMap().isSafetyZone(robot.getLocation())) {
            // ディレイ。
            int ran = L1Robot4.random(5, 15);
            ai_time = 1000 * ran;

            robot.getMap().setPassable(robot.getLocation(), true);

            int[] loc = Getback.GetBack_Location(robot, true);
            teleport(robot, loc[0], loc[1], (short) loc[2]);
            return;
        }

        // hp回復処置。
        if (robot.getMaxHp() != robot.getCurrentHp()) {
            toHealingPostion(true);
            return;
        }

        // ロボットに変身処理。
        if (robot.getGfxId() == robot.getTempCharGfx()) {
            Poly(robot);
            return;
        }

        if (skillDelayCount > 0) {
            skillDelayCount--;
            return;
        }

        location = RobotAIThread.getLocation();
        if (location != null) {
            int count = 0;
            while (true) {
                for (L1PcInstance player : L1World.getInstance().getAllPlayers()) {
                    if (player.getRobotAi() != null && (short) location.map == player.getMapId()) {
                        count++;
                    }
                }
                if (count < 3) {
                    break;
                }
                location = RobotAIThread.getLocation();
                count = 0;
            }

            teleport(robot, location.x, location.y, (short) location.map);
        }
        setAiStatus(AI_STATUS_WALK);
        buff_step = 0;

    }

    private void toBuff() {
        try {
            if (skillDelayCount > 0) {
                skillDelayCount--;
                return;
            }

            //バフ詠唱。
            if (robot.isKnight()) {
                switch (buff_step++) {
                    case 0:
                        if (robot.hasSkillEffect(L1SkillId.REDUCTION_ARMOR)) {
                            return;
                        }
                        Broadcaster.broadcastPacket(robot, new S_DoActionGFX(robot.getId(), 19));
                        new L1SkillUse().handleCommands(robot, L1SkillId.REDUCTION_ARMOR, robot.getId(), robot.getX(), robot.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                        l1skills = SkillsTable.getInstance().getTemplate(L1SkillId.REDUCTION_ARMOR);
                        skillDelayCount = l1skills.getReuseDelay() / 1000 + 2;
                        return;
                    default:
                        buff_step = 0;
                        break;
                }
            } else if (robot.isElf()) {
                switch (buff_step++) {
                    case 0:
                        if (robot.hasSkillEffect(L1SkillId.STORM_SHOT)) {
                            return;
                        }
                        Broadcaster.broadcastPacket(robot, new S_DoActionGFX(robot.getId(), 19));
                        new L1SkillUse().handleCommands(robot, L1SkillId.STORM_SHOT, robot.getId(), robot.getX(), robot.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                        l1skills = SkillsTable.getInstance().getTemplate(L1SkillId.STORM_SHOT);
                        skillDelayCount = l1skills.getReuseDelay() / 1000 + 2;
                        return;
                    case 1:
                        if (robot.getLevel() < 64) {
                            return;
                        }
                        Broadcaster.broadcastPacket(robot, new S_DoActionGFX(robot.getId(), 19));
                        new L1SkillUse().handleCommands(robot, L1SkillId.BLOODY_SOUL, robot.getId(), robot.getX(), robot.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                        l1skills = SkillsTable.getInstance().getTemplate(L1SkillId.BLOODY_SOUL);
                        skillDelayCount = l1skills.getReuseDelay() / 1000 + 2;
                        return;
                    default:
                        buff_step = 0;
                        break;
                }
            } else if (robot.isWizard()) {
                switch (buff_step++) {
                    case 0:
                        if (robot.hasSkillEffect(L1SkillId.ADVANCE_SPIRIT) || robot.getLevel() < 65) {
                            return;
                        }
                        Broadcaster.broadcastPacket(robot, new S_DoActionGFX(robot.getId(), 19));
                        new L1SkillUse().handleCommands(robot, L1SkillId.ADVANCE_SPIRIT, robot.getId(), robot.getX(), robot.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                        l1skills = SkillsTable.getInstance().getTemplate(L1SkillId.ADVANCE_SPIRIT);
                        skillDelayCount = l1skills.getReuseDelay() / 1000 + 2;
                        return;
                    case 1:
                        if (robot.hasSkillEffect(L1SkillId.PHYSICAL_ENCHANT_DEX)) {
                            return;
                        }
                        Broadcaster.broadcastPacket(robot, new S_DoActionGFX(robot.getId(), 19));
                        new L1SkillUse().handleCommands(robot, L1SkillId.PHYSICAL_ENCHANT_DEX, robot.getId(), robot.getX(), robot.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                        l1skills = SkillsTable.getInstance().getTemplate(L1SkillId.PHYSICAL_ENCHANT_DEX);
                        skillDelayCount = l1skills.getReuseDelay() / 1000 + 2;
                        return;
                    case 2:
                        if (robot.hasSkillEffect(L1SkillId.PHYSICAL_ENCHANT_STR)) {
                            return;
                        }
                        Broadcaster.broadcastPacket(robot, new S_DoActionGFX(robot.getId(), 19));
                        new L1SkillUse().handleCommands(robot, L1SkillId.PHYSICAL_ENCHANT_STR, robot.getId(), robot.getX(), robot.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                        l1skills = SkillsTable.getInstance().getTemplate(L1SkillId.PHYSICAL_ENCHANT_STR);
                        skillDelayCount = l1skills.getReuseDelay() / 1000 + 2;
                        return;
                    case 3:
                        if (robot.hasSkillEffect(L1SkillId.BERSERKERS)) {
                            return;
                        }
                        Broadcaster.broadcastPacket(robot, new S_DoActionGFX(robot.getId(), 19));
                        new L1SkillUse().handleCommands(robot, L1SkillId.BERSERKERS, robot.getId(), robot.getX(), robot.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                        l1skills = SkillsTable.getInstance().getTemplate(L1SkillId.BERSERKERS);
                        skillDelayCount = l1skills.getReuseDelay() / 1000 + 2;
                        return;
                    case 4:
                        if (robot.hasSkillEffect(L1SkillId.HOLY_WALK)) {
                            return;
                        }
                        Broadcaster.broadcastPacket(robot, new S_DoActionGFX(robot.getId(), 19));
                        new L1SkillUse().handleCommands(robot, L1SkillId.HOLY_WALK, robot.getId(), robot.getX(), robot.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                        l1skills = SkillsTable.getInstance().getTemplate(L1SkillId.HOLY_WALK);
                        skillDelayCount = l1skills.getReuseDelay() / 1000 + 2;
                        return;
                    default:
                        buff_step = 0;
                        break;
                }
            } else if (robot.isDarkelf()) {
                switch (buff_step++) {
                    case 0:
                        if (robot.hasSkillEffect(L1SkillId.ENCHANT_VENOM)) {
                            return;
                        }
                        Broadcaster.broadcastPacket(robot, new S_DoActionGFX(robot.getId(), 19));
                        new L1SkillUse().handleCommands(robot, L1SkillId.ENCHANT_VENOM, robot.getId(), robot.getX(), robot.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                        l1skills = SkillsTable.getInstance().getTemplate(L1SkillId.ENCHANT_VENOM);
                        skillDelayCount = l1skills.getReuseDelay() / 1000 + 2;
                        return;
                    case 1:
                        if (robot.hasSkillEffect(L1SkillId.SHADOW_ARMOR)) {
                            return;
                        }
                        Broadcaster.broadcastPacket(robot, new S_DoActionGFX(robot.getId(), 19));
                        new L1SkillUse().handleCommands(robot, L1SkillId.SHADOW_ARMOR, robot.getId(), robot.getX(), robot.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                        l1skills = SkillsTable.getInstance().getTemplate(L1SkillId.SHADOW_ARMOR);
                        skillDelayCount = l1skills.getReuseDelay() / 1000 + 2;
                        return;
                    case 2:
                        if (robot.hasSkillEffect(L1SkillId.DOUBLE_BRAKE)) {
                            return;
                        }
                        Broadcaster.broadcastPacket(robot, new S_DoActionGFX(robot.getId(), 19));
                        new L1SkillUse().handleCommands(robot, L1SkillId.DOUBLE_BRAKE, robot.getId(), robot.getX(), robot.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                        l1skills = SkillsTable.getInstance().getTemplate(L1SkillId.DOUBLE_BRAKE);
                        skillDelayCount = l1skills.getReuseDelay() / 1000 + 2;
                        return;
                    case 3:
                        if (robot.hasSkillEffect(L1SkillId.UNCANNY_DODGE)) {
                            return;
                        }
                        Broadcaster.broadcastPacket(robot, new S_DoActionGFX(robot.getId(), 19));
                        new L1SkillUse().handleCommands(robot, L1SkillId.UNCANNY_DODGE, robot.getId(), robot.getX(), robot.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                        l1skills = SkillsTable.getInstance().getTemplate(L1SkillId.UNCANNY_DODGE);
                        skillDelayCount = l1skills.getReuseDelay() / 1000 + 2;
                        return;
                    case 4:
                        if (robot.hasSkillEffect(L1SkillId.DRESS_DEXTERITY)) {
                            return;
                        }
                        Broadcaster.broadcastPacket(robot, new S_DoActionGFX(robot.getId(), 19));
                        new L1SkillUse().handleCommands(robot, L1SkillId.DRESS_DEXTERITY, robot.getId(), robot.getX(), robot.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                        l1skills = SkillsTable.getInstance().getTemplate(L1SkillId.DRESS_DEXTERITY);
                        skillDelayCount = l1skills.getReuseDelay() / 1000 + 2;
                        return;
                    default:
                        buff_step = 0;
                        break;
                }
            } else if (robot.isDragonknight()) {
                //
            } else if (robot.isBlackwizard()) {
                //
            } else if (robot.isCrown()) {

            }
            buff_step = 0;
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    /**
     * オブジェクトが攻撃可能な状態であることを確認してくれる関数です。
     *
     * @param o
     * @param walk
     * @return
     */
    public boolean isAttack(L1Character cha, boolean walk) {
        try {
            if (cha == null)
                return false;

            if (cha.hasSkillEffect(L1SkillId.EARTH_BIND) ||
                    cha.hasSkillEffect(L1SkillId.ICE_LANCE)) {
                return false;
            }
            if (cha.getMap().isSafetyZone(cha.getLocation()))
                return false;
            if (cha.isDead())
                return false;
            if (cha.isInvisble())
                return false;
            if (!isDistance(robot.getX(), robot.getY(), robot.getMapId(), cha.getX(), cha.getY(), cha.getMapId(), 12))
                return false;
            if (!robot.glanceCheck(cha.getX(), cha.getY()))
                return false;

            return true;
        } catch (Exception e) {
            //e.printStackTrace();
            return false;
        }
    }

    private void to_ai_attack() {
        try {
            if (isDistance(robot.getX(), robot.getY(), robot.getMapId(), target.getX(), target.getY(), target.getMapId(), robot.isElf() ? 8 : 1)) {
                ai_time = getFrame(robot.getGfxId(), 1);
                if (robot.glanceCheck(target.getX(), target.getY())) {
                    //	toAttack(target, 0, 0, robot.isElf(), 1, 0);
                    target.onAction(robot);
                } else {
                    toMoving(target, target.getX(), target.getY(), 0, true);
                }
            } else {
                ai_time = getFrame(robot.getGfxId(), 0);
                toMoving(target, target.getX(), target.getY(), 0, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void toAiAttack() {//狩りロボット設定
        try {
            if (target != null && target.isDead()) {
                if (robot.getName().equals(Config.ROBOT_NAME)) {
                    System.out.println("ロボットダイ");
                }
                attackList.remove(target);
                target = null;

                if (isPickup(robot)) {
                    setAiStatus(AI_STATUS_PICKUP);
                    attackList.clear();
                }
                return;
            }

            if (dropItem != null) {
                pickupCount++;

                if (pickupCount >= 5) {
                    dropItem = null;
                    groundInventory = null;
                    object = null;
                    setAiStatus(AI_STATUS_WALK);
                    pickupCount = 0;
                }
                if (robot.getName().equals(Config.ROBOT_NAME)) {
                    System.out.println("ロボットドロップ" + ((L1ItemInstance) dropItem).getName());
                }
                return;
            }
            //ウォーキング
            if (type != 1 && target != null && !(target instanceof L1PcInstance) && !(target instanceof L1GuardianInstance) && ((L1MonsterInstance) target).getHiddenStatus() >= 1) {
                attackList.remove(target);
                target = null;
                return;
            }

            target = findDangerousObject();

            if (type != 1 && !isAttack(target, true)) {
                attackList.remove(target);
                target = null;
            }

            // オブジェクトが見つからなかった場合はランダムウォークに変更。
            if (target == null && dropItem == null) {
                ai_time = 0;
                setAiStatus(AI_STATUS_WALK);
                return;
            }
            // オブジェクトの距離を確認し
            if (type > 1 && isDistance(robot.getX(), robot.getY(), robot.getMapId(), target.getX(), target.getY(), target.getMapId(), robot.isElf() ? 8 : 1)) {
                // ユーザーは区分処理します。
                if (target instanceof L1PcInstance) { // PCラング戦っ場合。
                    //ウィザード
                    if (robot.isWizard() && robot.getCurrentMp() >= 50 && isDistance(robot.getX(), robot.getY(), robot.getMapId(), target.getX(), target.getY(), target.getMapId(), robot.isWizard() ? 5 : 1)) {
                        if (target.hasSkillEffect(L1SkillId.SILENCE) ||
                                target.hasSkillEffect(L1SkillId.DECAY_POTION) ||
                                target.hasSkillEffect(L1SkillId.SHOCK_STUN) ||
                                target.hasSkillEffect(L1SkillId.THUNDER_GRAB) ||
                                target.hasSkillEffect(L1SkillId.MIND_BREAK) ||
                                target.hasSkillEffect(L1SkillId.PANIC) ||
                                target.hasSkillEffect(L1SkillId.IllUSION_AVATAR) ||
                                target.hasSkillEffect(L1SkillId.STRIKER_GALE) ||
                                target.hasSkillEffect(L1SkillId.POLLUTE_WATER) ||
                                target.hasSkillEffect(L1SkillId.EARTH_BIND)) {
                            return;

                        } else {
                            toWizardMagic(target);
                            return;
                        }
                        //ナイト
                    } else if (robot.isKnight() && robot.getCurrentMp() >= 50 && isDistance(robot.getX(), robot.getY(), robot.getMapId(), target.getX(), target.getY(), target.getMapId(), robot.isKnight() ? 2 : 1)) {
                        if (target.hasSkillEffect(L1SkillId.SILENCE) ||
                                target.hasSkillEffect(L1SkillId.DECAY_POTION) ||
                                target.hasSkillEffect(L1SkillId.SHOCK_STUN) ||
                                target.hasSkillEffect(L1SkillId.THUNDER_GRAB) ||
                                target.hasSkillEffect(L1SkillId.MIND_BREAK) ||
                                target.hasSkillEffect(L1SkillId.PANIC) ||
                                target.hasSkillEffect(L1SkillId.IllUSION_AVATAR) ||
                                target.hasSkillEffect(L1SkillId.STRIKER_GALE) ||
                                target.hasSkillEffect(L1SkillId.POLLUTE_WATER) ||
                                target.hasSkillEffect(L1SkillId.EARTH_BIND)) {
                            return;

                        } else {
                            toKnightMagic(target);
                            return;
                        }
                        //竜騎士
                    } else if (robot.isDragonknight() && robot.getCurrentMp() >= 50 && isDistance(robot.getX(), robot.getY(), robot.getMapId(), target.getX(), target.getY(), target.getMapId(), robot.isDragonknight() ? 2 : 1)) {
                        if (target.hasSkillEffect(L1SkillId.SILENCE) ||
                                target.hasSkillEffect(L1SkillId.DECAY_POTION) ||
                                target.hasSkillEffect(L1SkillId.SHOCK_STUN) ||
                                target.hasSkillEffect(L1SkillId.THUNDER_GRAB) ||
                                target.hasSkillEffect(L1SkillId.MIND_BREAK) ||
                                target.hasSkillEffect(L1SkillId.PANIC) ||
                                target.hasSkillEffect(L1SkillId.IllUSION_AVATAR) ||
                                target.hasSkillEffect(L1SkillId.STRIKER_GALE) ||
                                target.hasSkillEffect(L1SkillId.POLLUTE_WATER) ||
                                target.hasSkillEffect(L1SkillId.EARTH_BIND)) {
                            return;

                        } else {
                            toDragonknightMagic(target);
                            return;
                        }
                        //イリュージョニスト
                    } else if (robot.isBlackwizard() && robot.getCurrentMp() >= 50
                            && isDistance(robot.getX(), robot.getY(), robot.getMapId(),
                            target.getX(), target.getY(), target.getMapId(), robot.isBlackwizard() ? 4 : 1)) {
                        if (target.hasSkillEffect(L1SkillId.SILENCE) ||
                                target.hasSkillEffect(L1SkillId.DECAY_POTION) ||
                                target.hasSkillEffect(L1SkillId.SHOCK_STUN) ||
                                target.hasSkillEffect(L1SkillId.THUNDER_GRAB) ||
                                target.hasSkillEffect(L1SkillId.MIND_BREAK) ||
                                target.hasSkillEffect(L1SkillId.PANIC) ||
                                target.hasSkillEffect(L1SkillId.IllUSION_AVATAR) ||
                                target.hasSkillEffect(L1SkillId.STRIKER_GALE) ||
                                target.hasSkillEffect(L1SkillId.POLLUTE_WATER) ||
                                target.hasSkillEffect(L1SkillId.EARTH_BIND)) {
                            return;

                        } else {
                            toIllusionistMagic(target);
                            return;
                        }
                        //妖精
                    } else if (robot.isElf() && robot.getCurrentMp() >= 50 && isDistance(robot.getX(), robot.getY(), robot.getMapId(), target.getX(), target.getY(), target.getMapId(), robot.isElf() ? 7 : 1)) {
                        if (target.hasSkillEffect(L1SkillId.SILENCE) ||
                                target.hasSkillEffect(L1SkillId.DECAY_POTION) ||
                                target.hasSkillEffect(L1SkillId.SHOCK_STUN) ||
                                target.hasSkillEffect(L1SkillId.THUNDER_GRAB) ||
                                target.hasSkillEffect(L1SkillId.MIND_BREAK) ||
                                target.hasSkillEffect(L1SkillId.PANIC) ||
                                target.hasSkillEffect(L1SkillId.IllUSION_AVATAR) ||
                                target.hasSkillEffect(L1SkillId.STRIKER_GALE) ||
                                target.hasSkillEffect(L1SkillId.POLLUTE_WATER) ||
                                target.hasSkillEffect(L1SkillId.EARTH_BIND)) {
                            return;
                        } else {
                            toElfMagic(target);
                            return;
                        }
                    }
                }
            }

            if (isDistance(robot.getX(), robot.getY(), robot.getMapId(), target.getX(), target.getY(), target.getMapId(), robot.isElf() ? 8 : 1)) {
                ai_time = getFrame(robot.getGfxId(), 1);
                if (robot.glanceCheck(target.getX(), target.getY())) {
                    toAttack(target, 0, 0, robot.isElf(), 1, 0);
                } else {
                    toMoving(target, target.getX(), target.getY(), 0, true);
                    moveDelayCount++;

                    if (moveDelayCount >= 40) {
                        L1Location newLocation = robot.getLocation().randomLocation(200, true);
                        int newX = newLocation.getX();
                        int newY = newLocation.getY();
                        short mapId = (short) newLocation.getMapId();
                        teleport(robot, newX, newY, mapId);
                        moveDelayCount = 0;
                    }
                }
            } else {
                ai_time = getFrame(robot.getGfxId(), 0);
                toMoving(target, target.getX(), target.getY(), 0, true);
            }
        } catch (Exception e) {
            attackList.remove(target);
            target = null;
            //e.printStackTrace();
        }
    }

    /**
     * 各クレスが魔法を詠唱している。
     *
     * @param o
     */
    private void toWizardMagic(L1Object o) {
        if (target instanceof L1MonsterInstance) {
            return;
        }

        if (robot.isWizard()) {
            Random random = new Random();
            int a = random.nextInt(5);
            switch (a) {
                case 1:
                    new L1SkillUse().handleCommands(robot, L1SkillId.SUNBURST, o.getId(), o.getX(), o.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                    robot.setCurrentMp(1);
/*				L1Skills skill = SkillsTable.getInstance().getTemplate(L1SkillId.SUNBURST);
                robot.setCurrentMp(robot.getCurrentMp()-skill.getMpConseume());*/
                    break;
                case 2:
                    new L1SkillUse().handleCommands(robot, L1SkillId.SILENCE, o.getId(), o.getX(), o.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                    robot.setCurrentMp(1);
                    break;
                case 3:
                    new L1SkillUse().handleCommands(robot, L1SkillId.ERUPTION, o.getId(), o.getX(), o.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                    robot.setCurrentMp(1);
                    break;
                case 4:
                    new L1SkillUse().handleCommands(robot, L1SkillId.DECAY_POTION, o.getId(), o.getX(), o.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                    robot.setCurrentMp(1);
                    break;
                case 5:
                    new L1SkillUse().handleCommands(robot, L1SkillId.WEAPON_BREAK, o.getId(), o.getX(), o.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                    robot.setCurrentMp(1);
                    break;

                default:
            }
        }
    }

    private void toKnightMagic(L1Object o) {
        if (robot.isKnight()) {
            new L1SkillUse().handleCommands(robot, L1SkillId.SHOCK_STUN, o.getId(), o.getX(), o.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
            robot.setCurrentMp(1);
        }
    }

    private void toDragonknightMagic(L1Object o) {
        if (target instanceof L1MonsterInstance) {
            return;
        }

        Random random = new Random();
        int a = random.nextInt(3);
        switch (a) {
            case 1:
                new L1SkillUse().handleCommands(robot, L1SkillId.FOU_SLAYER, o.getId(), o.getX(), o.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                robot.setCurrentMp(1);
                break;
            case 2:
                new L1SkillUse().handleCommands(robot, L1SkillId.THUNDER_GRAB, o.getId(), o.getX(), o.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                robot.setCurrentMp(1);
                break;
            case 3:
                new L1SkillUse().handleCommands(robot, L1SkillId.FOU_SLAYER, o.getId(), o.getX(), o.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                robot.setCurrentMp(1);
                break;
            default:
        }
    }

    private void toIllusionistMagic(L1Object o) {
        if (target instanceof L1MonsterInstance) {
            return;
        }

        if (robot.isBlackwizard()) {
            Random random = new Random();
            int a = random.nextInt(4);
            switch (a) {
                case 1:
                    new L1SkillUse().handleCommands(robot, L1SkillId.MIND_BREAK, o.getId(), o.getX(), o.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                    robot.setCurrentMp(1);
                    break;
                case 2:
                    new L1SkillUse().handleCommands(robot, L1SkillId.MIND_BREAK, o.getId(), o.getX(), o.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                    robot.setCurrentMp(1);
                    break;
                case 3:
                    new L1SkillUse().handleCommands(robot, L1SkillId.PANIC, o.getId(), o.getX(), o.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                    robot.setCurrentMp(1);
                    break;
                case 4:
                    new L1SkillUse().handleCommands(robot, L1SkillId.IllUSION_AVATAR, o.getId(), o.getX(), o.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                    robot.setCurrentMp(1);
                    break;

                default:
            }
        }
    }

    private void toElfMagic(L1Object o) {
        if (robot.isElf()) {
            Random random = new Random();
            int a = random.nextInt(6);
            switch (a) {
                case 1:
                    Broadcaster.broadcastPacket(robot, new S_SkillSound(robot.getId(), 4394));
                    for (int i = 0; i < 3; ++i) {
                        toAttack(o, 0, 0, robot.isElf(), 1, 0);
                    }
                    robot.setCurrentMp(1000);
                    break;
                case 2:
                    Broadcaster.broadcastPacket(robot, new S_SkillSound(robot.getId(), 4394));
                    for (int i = 0; i < 3; ++i) {
                        toAttack(o, 0, 0, robot.isElf(), 1, 0);
                    }
                    robot.setCurrentMp(1000);
                    break;
                case 3:
                    new L1SkillUse().handleCommands(robot, L1SkillId.STRIKER_GALE, o.getId(), o.getX(), o.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                    robot.setCurrentMp(1000);
                    break;
                case 4:
                    new L1SkillUse().handleCommands(robot, L1SkillId.POLLUTE_WATER, o.getId(), o.getX(), o.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                    robot.setCurrentMp(1000);
                    break;
                case 5:
                    Broadcaster.broadcastPacket(robot, new S_SkillSound(robot.getId(), 2178));
                    robot.setCurrentMp(1);
                    break;
                case 6:
                    new L1SkillUse().handleCommands(robot, L1SkillId.EARTH_BIND, o.getId(), o.getX(), o.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                    robot.setCurrentMp(1000);
                    break;
                default:
            }
        }
    }

    /**
     * 攻撃処理関数です。
     *
     * @param o
     * @param x
     * @param y
     * @param bow
     * @param gfxMode
     * @param alpha_dmg
     */
    private void toAttack(L1Object o, int x, int y, boolean bow, int gfxMode, int alpha_dmg) {
        try {
            statusType = 2;
            moveDelayCount = 0;
            searchCount = 0;
            if (target == null) {
                if (robot.getName().equals(Config.ROBOT_NAME)) {
                    System.out.println("ロボットクリア");
                }
                attackList.clear();
                setAiStatus(AI_STATUS_WALK);
                return;
            }


            if (robot.hasSkillEffect(L1SkillId.MEDITATION)) {
                robot.killSkillEffectTimer(L1SkillId.MEDITATION);
            }

            robot.delInvis();

            if (target != null) {
                target.onAction(robot);
                if (robot.getLevel() >= 50 && robot.getWeapon().getItem().getType() == 17) {
                    if (robot.getWeapon().getItemId() == 504) {
                        robot.sendPackets(new S_SkillSound(robot.getId(), 6983));
                        Broadcaster.broadcastPacket(robot, new S_SkillSound(robot.getId(), 6983));
                    } else {
                        robot.sendPackets(new S_SkillSound(robot.getId(), 7049));
                        Broadcaster.broadcastPacket(robot, new S_SkillSound(robot.getId(), 7049));
                    }
                }
            } else {
                attackList.remove(target);
                target = null;
            }
        } catch (Exception e) {
            target = null;
            attackList.clear();
            //e.printStackTrace();
        }
    }

    /**
     * 攻撃のリストが登録されていない場合、周辺に攻撃のリストを取得する。
     */
    private void toSearchTarget() {
        int distance = 20;
        int temp = 0;

        if (type != 1) {
            searchCount++;
        }
        if (robot.getName().equals(Config.ROBOT_NAME)) {
            System.out.println("+++++++++++++++++++++++++++++++++++++++");
            System.out.println("リサーチ" + attackList.toTargetArrayList().size());
        }


        if (type != 1 && searchCount >= 15) {
            if (robot.getName().equals(Config.ROBOT_NAME)) {
                System.out.println("+++++++++++++++++++++++++++++++++++++++");
                System.out.println("searchCount : " + searchCount);
            }
            attackList.clear();
            target = null;
            searchCount = 0;
            L1Location newLocation = robot.getLocation().randomLocation(200, true);
            int newX = newLocation.getX();
            int newY = newLocation.getY();
            short mapId = (short) newLocation.getMapId();
            teleport(robot, newX, newY, mapId);
            moveCount = 0;
            teleportDelayCount = 2;
            return;
        }
        switch (type) {
            case 1:
                for (L1Object obj : L1World.getInstance().getVisibleObjects(robot)) {
                    if (obj instanceof L1ScarecrowInstance) {
                        L1ScarecrowInstance sca = (L1ScarecrowInstance) obj;
                        temp = getDistance(robot.getX(), robot.getY(), sca.getX(), sca.getY());

                        attackList.add((L1Character) obj, 0);

                        if (temp <= distance) {
                            target = sca;
                            distance = temp;
                        }
                    }
                }
                break;
            case 2:
                checkTarget();
                if (target == null && dropItem == null) {
                    for (L1Object obj : L1World.getInstance().getVisibleObjects(robot, 15)) {
                        if (obj == null) {
                            continue;
                        }

                        if (obj instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) obj;
                            if (pc.getRobotAi() != null && (robot.getClanid() != pc.getClanid())) {
                                attackList.add(pc, 0);
                            } else if (pc.getRobotAi() == null && !pc.isGm()) {
                                attackList.clear();
                                target = null;
                                searchCount = 0;
                                L1Location newLocation = robot.getLocation().randomLocation(200, true);
                                int newX = newLocation.getX();
                                int newY = newLocation.getY();
                                short mapId = (short) newLocation.getMapId();
                                teleport(robot, newX, newY, mapId);
                                moveCount = 0;
                                teleportDelayCount = 2;
                                return;
                            }

                        }

                        if (obj instanceof L1MonsterInstance) {
                            L1MonsterInstance mon = (L1MonsterInstance) obj;

                            if (mon.isDead()) {
                                continue;
                            }

                            if (mon.getHiddenStatus() >= 1) {
                                continue;
                            }

                            if (attackList.containsKey((L1Character) mon)) {
                                continue;
                            }

                            if (!robot.glanceCheck(mon.getX(), mon.getY())) {
                                continue;
                            }

                            attackList.add((L1Character) mon, 0);

                        }


                    }

                    if (target != null && attackList.toTargetArrayList().size() == 0) {
                        if (!attackList.containsKey(target)) {
                            if (robot.getName().equals(Config.ROBOT_NAME)) {
                                System.out.println("ロボットテレポート。");
                            }
                            attackList.clear();
                            target = null;
                            searchCount = 0;
                            L1Location newLocation = robot.getLocation().randomLocation(200, true);
                            int newX = newLocation.getX();
                            int newY = newLocation.getY();
                            short mapId = (short) newLocation.getMapId();
                            teleport(robot, newX, newY, mapId);
                            moveCount = 0;
                            teleportDelayCount = 2;
                            return;
                        }
                    }
                }
                break;
            default:
                break;
        }

    }

    /**
     * 攻撃リストに登録されたオブジェクトの中に危険なオブジェクトを優先検索し返す。 ：返されたオブジェクトをターゲットに攻撃する。
     *
     * @return
     */
    private L1Character findDangerousObject() {
        L1Character o = null;
        try {
            // ユーザー優先検索します。
            for (int i = attackList.toTargetArrayList().size() - 1; i >= 0; i--) {
                L1Character oo = attackList.toTargetArrayList().get(i);
                if (oo instanceof L1PcInstance) {
                    if (((L1PcInstance) oo).isInvisble()) {
                        attackList.remove(oo);
                        target = null;
                        continue;
                    }
                    if (robot.getMapId() != oo.getMapId()) {
                        attackList.remove(oo);
                        target = null;
                        continue;
                    }
                    if (!robot.glanceCheck(oo.getX(), oo.getY())) {
                        attackList.remove(oo);
                        target = null;
                        continue;
                    }
                    if (o == null) {
                        o = oo;
                    } else if (!o.isDead() && getDistance(robot.getX(), robot.getY(), oo.getX(), oo.getY()) < getDistance(robot.getX(), robot.getY(), o.getX(), o.getY())) {
                        o = oo;
                    }
                }
            }

            if (o != null) {
                return o;
            }

            /** モンスター検索 **/
            for (int i = attackList.toTargetArrayList().size() - 1; i >= 0; i--) {
                L1Character oo = attackList.toTargetArrayList().get(i);
                if (oo.isDead()) {
                    attackList.remove(oo);
                    target = null;
                    continue;
                }
                if (!robot.glanceCheck(oo.getX(), oo.getY())) {
                    attackList.remove(oo);
                    target = null;
                    continue;
                }
                if (o == null) {
                    o = oo;
                } else if (!o.isDead() && getDistance(robot.getX(), robot.getY(), oo.getX(), oo.getY()) < getDistance(robot.getX(), robot.getY(), o.getX(), o.getY())) {
                    o = oo;
                }
            }
            return o;
        } catch (Exception e) {
            attackList.clear();
            target = null;
            //e.printStackTrace();
            return o;
        }
    }

    /**
     * ポーション服用処理関数です。
     *
     * @param direct
     */
    private void toHealingPostion(boolean direct) {
        if (robot.isDead()) {
            return;
        }

        if (robot.hasSkillEffect(71) == true) { // ディケイポーションの状態
        } else {
            curePostionCount++;
            if (robot.getPoison() != null && curePostionCount >= 3) {
                Broadcaster.broadcastPacket(robot, new S_SkillSound(robot.getId(), 192));
                robot.curePoison();
                curePostionCount = 0;
            }
        }

        int p = (int) (((double) robot.getCurrentHp() / (double) robot.getMaxHp()) * 100);
        if (direct || p <= 70) {

            if (robot.hasSkillEffect(71))
                return;

            Broadcaster.broadcastPacket(robot, new S_SkillSound(robot.getId(), 197));

            int healHp = 30;
            // ポルトウォーター中は回復量1/2倍
            if (robot.hasSkillEffect(POLLUTE_WATER))
                healHp /= 2;

            robot.setCurrentHp(robot.getCurrentHp() + healHp);
            postionCount--;
        }


        if (postionCount <= 0) {
            setAiStatus(AI_STATUS_SETTING);
            postionCount = CommonUtil.random(400, 600);
            attackList.clear();
            target = null;
        }
        if (robot.getLawful() < 30000 && p < 40) {
            setAiStatus(AI_STATUS_SETTING);
            postionCount = CommonUtil.random(400, 600);
            attackList.clear();
            target = null;
            return;
        }

        // 50％未満と、村に移動するために状態を変更する。
        if (p < 15) { // 50％以上でベルする//基本15
            setAiStatus(AI_STATUS_SETTING);
            postionCount = CommonUtil.random(400, 600);
            attackList.clear();
            target = null;
        }
    }

    private void toPolyMorph() {
        if (robot.getLevel() < 52) {
            return;
        }

        // ロボットに変身処理。
        if (robot.getGfxId() == robot.getTempCharGfx()) {
            Poly(robot);
            return;
        }
    }

    private void toSpeedPostion1() {//ロボット専用容器
        if (robot.isDead()) {
            return;
        }
        if (robot.getMoveSpeed() == 0) {
            // チォルギトロた場合服用する。
            Broadcaster.broadcastPacket(robot, new S_SkillSound(robot.getId(), 191));
            Broadcaster.broadcastPacket(robot, new S_SkillHaste(robot.getId(), 1, 0));
            robot.setMoveSpeed(1);
            // robot.setSkillEffect(STATUS_HASTE, 300 * 1000);
            return;
        }
    }

    private void toSpeedPostion() {
        if (robot.isDead()) {
            return;
        }
        if (robot.getMoveSpeed() == 0) {
            // チォルギトロた場合服用する。
            Broadcaster.broadcastPacket(robot, new S_SkillSound(robot.getId(), 191));
            Broadcaster.broadcastPacket(robot, new S_SkillHaste(robot.getId(), 1, 0));
            robot.setMoveSpeed(1);
            //  robot.setSkillEffect(STATUS_HASTE, 300 * 1000);
            return;
        }
        if (!robot.isWizard() && !robot.isDarkelf() && robot.getBraveSpeed() == 0) {
            // 容器トロた場合服用する。
            Broadcaster.broadcastPacket(robot, new S_SkillBrave(robot.getId(), 1, 0));
            robot.setBraveSpeed(1);
            robot.setSkillEffect(STATUS_BRAVE, 300 * 1000);
            Broadcaster.broadcastPacket(robot, new S_SkillSound(robot.getId(), 751));
            return;
        }
        if (robot.isWizard() && !robot.hasSkillEffect(L1SkillId.STATUS_BLUE_POTION)) {
            // パレンイトロた場合服用する。
            robot.sendPackets(new S_SkillIconGFX(34, 600));
            robot.sendPackets(new S_SkillSound(robot.getId(), 190));
            robot.setSkillEffect(STATUS_BLUE_POTION, 600 * 1000);
            return;
        }
    }

    private void toAiDead() {
        ai_time = getFrame(robot.getGfxId(), 0);

        if (dieDelayCount <= 5) {
            dieDelayCount++;
            return;
        }

        L1PolyMorph.undoPoly(robot);

        int[] loc = Getback.GetBack_Location(robot, true);
        robot.removeAllKnownObjects();
        Broadcaster.broadcastPacket(robot, new S_RemoveObject(robot));
        robot.setCurrentHp(robot.getLevel());
        robot.set_food(39); // 死んだときにラゲッジ？ 10％
        robot.setDead(false);
        robot.setActionStatus(0);
        L1World.getInstance().moveVisibleObject(robot, loc[2]);
        robot.setX(loc[0]);
        robot.setY(loc[1]);
        robot.setMap((short) loc[2]);
//		for (L1PcInstance pc2 : L1World.getInstance().getVisiblePlayer(robot)) {
//			//pc2.sendPackets(new S_OtherCharPacks(pc, pc2));
//			Broadcaster.broadcastPacket(robot, new S_OtherCharPacks(robot,pc2));
//		}
        attackList.clear();
        target = null;
        dieDelayCount = 0;

        setAiStatus(AI_STATUS_SETTING);
    }

    private void search_target() {
        try {
            if (target == null) {
                for (L1Object obj : L1World.getInstance().getVisibleObjects(robot)) {
                    if (obj instanceof L1ScarecrowInstance) {
                        L1ScarecrowInstance sca = (L1ScarecrowInstance) obj;
                        attackList.add((L1Character) obj, 0);
                        target = sca;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void si_move() {
        try {
            ai_time = getFrame(robot.getGfxId(), 0);

            int dir = checkObject(robot.getX(), robot.getY(), robot.getMapId(), CommonUtil.random(20));
            if (dir != -1) {
                ai_time = getFrame(robot.getGfxId(), 0);
                toRandomMoving(dir);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void toRandomWalk() {
        if (type != 1) {
            toSearchTarget();
            toBuff();
        }

        if (type != 1 && attackList.toTargetArrayList().size() == 0) {
            if (teleportDelayCount > 0) {
                teleportDelayCount--;
                return;
            }

            L1Location newLocation = robot.getLocation().randomLocation(200, true);
            int newX = newLocation.getX();
            int newY = newLocation.getY();
            short mapId = (short) newLocation.getMapId();
            teleport(robot, newX, newY, mapId);
            moveCount = 0;
            teleportDelayCount = 2;
        }

        ai_time = getFrame(robot.getGfxId(), 0);

        if (type != 1 && !robot.getMap().isSafetyZone(robot.getLocation())) {
            int dir = checkObject(robot.getX(), robot.getY(), robot.getMapId(), CommonUtil.random(20));
            if (dir != -1) {
                ai_time = getFrame(robot.getGfxId(), 0);
                toRandomMoving(dir);
            }
        }

        moveCount++;
        //System.out.println("ロボット名： "+ robot.getName（）+"状態： "+ getAiStatus（）+"タイプ： "+ type）;
        //System.out.println("モンスターリスト： "+ attackList.toHateArrayList（）。size（）+"ポーション数： "+ postionCount +"動き "+ moveCount）;
        if (moveCount >= 5) {
            if (robot.getMap().isTeleportable()) {
                L1Location newLocation = robot.getLocation().randomLocation(200, true);
                int newX = newLocation.getX();
                int newY = newLocation.getY();
                short mapId = (short) newLocation.getMapId();
                teleport(robot, newX, newY, mapId);
                moveCount = 0;
            } else {
                moveCount = 0;
            }
        }
    }

    private void toRandomMoving(int dir) {
        if (dir >= 0) {
            int nx = 0;
            int ny = 0;

            int heading = 0;
            nx = HEADING_TABLE_X[dir];
            ny = HEADING_TABLE_Y[dir];
            heading = dir;

            robot.setHeading(heading);
            robot.getMap().setPassable(robot.getLocation(), true);

            int nnx = robot.getX() + nx;
            int nny = robot.getY() + ny;

            robot.setX(nnx);
            robot.setY(nny);
            L1WorldTraps.getInstance().onPlayerMoved(robot);
            robot.getMap().setPassable(robot.getLocation(), false);

            Broadcaster.broadcastPacket(robot, new S_MoveCharPacket(robot));
        }
    }


    private void toMoving(L1Object o, int x, int y, int h, boolean astar) {
        if (target != null && target.isDead()) {
            attackList.remove(target);
            target = null;
            return;
        }

        statusType = 1;
        if (astar) {
            aStar.ResetPath();
            tail = aStar.FindPath(robot, x, y, robot.getMapId(), target);
            if (tail != null) {
                iCurrentPath = -1;
                while (tail != null) {
                    if (tail.x == robot.getX() && tail.y == robot.getY()) {
                        //現在の位置であれば、終了
                        break;
                    }
                    iPath[++iCurrentPath][0] = tail.x;
                    iPath[iCurrentPath][1] = tail.y;
                    tail = tail.prev;
                }
                toMoving(iPath[iCurrentPath][0], iPath[iCurrentPath][1], calcheading(robot.getX(), robot.getY(), iPath[iCurrentPath][0], iPath[iCurrentPath][1]));
            } else {
            }
        } else {
            toMoving(x, y, h);
        }
    }


    public int targetReverseDirection(int tx, int ty) {
        int dir = robot.targetDirection(tx, ty);
        dir += 4;
        if (dir > 7) {
            dir -= 8;
        }
        return dir;
    }

    public void setDirectionMove(int dir) {
        if (dir >= 0) {
            int nx = 0;
            int ny = 0;

            int heading = 0;
            nx = HEADING_TABLE_X[dir];
            ny = HEADING_TABLE_Y[dir];
            heading = dir;

            robot.setHeading(heading);
            robot.getMap().setPassable(robot.getLocation(), true);

            int nnx = robot.getX() + nx;
            int nny = robot.getY() + ny;
            robot.setX(nnx);
            robot.setY(nny);
            L1WorldTraps.getInstance().onPlayerMoved(robot);
            robot.getMap().setPassable(robot.getLocation(), false);

            Broadcaster.broadcastPacket(robot, new S_MoveCharPacket(robot));
        }
    }

    private void toMoving(final int x, final int y, final int h) {
        try {
            robot.getMap().setPassable(robot.getLocation(), true);
            robot.getLocation().set(x, y);
            robot.setHeading(h);
            L1WorldTraps.getInstance().onPlayerMoved(robot);
            robot.getMap().setPassable(robot.getLocation(), false);
            Broadcaster.broadcastPacket(robot, new S_MoveCharPacket(robot));
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private boolean isDistance(int x, int y, int m, int tx, int ty, int tm, int loc) {
        int distance = getDistance(x, y, tx, ty);
        if (loc < distance)
            return false;
        if (m != tm)
            return false;
        return true;
    }

    private int getDistance(int x, int y, int tx, int ty) {
        long dx = tx - x;
        long dy = ty - y;
        return (int) Math.sqrt(dx * dx + dy * dy);
    }


    private int calcheading(int myx, int myy, int tx, int ty) {
        if (tx > myx && ty > myy) {
            return 3;
        } else if (tx < myx && ty < myy) {
            return 7;
        } else if (tx > myx && ty == myy) {
            return 2;
        } else if (tx < myx && ty == myy) {
            return 6;
        } else if (tx == myx && ty < myy) {
            return 0;
        } else if (tx == myx && ty > myy) {
            return 4;
        } else if (tx < myx && ty > myy) {
            return 5;
        } else {
            return 1;
        }
    }

    public int checkObject(int x, int y, short m, int d) {
        L1Map map = L1WorldMap.getInstance().getMap(m);
        switch (d) {
            case 1:
                if (map.isPassable(x, y, 1)) {
                    return 1;
                } else if (map.isPassable(x, y, 0)) {
                    return 0;
                } else if (map.isPassable(x, y, 2)) {
                    return 2;
                }
                break;
            case 2:
                if (map.isPassable(x, y, 2)) {
                    return 2;
                } else if (map.isPassable(x, y, 1)) {
                    return 1;
                } else if (map.isPassable(x, y, 3)) {
                    return 3;
                }
                break;
            case 3:
                if (map.isPassable(x, y, 3)) {
                    return 3;
                } else if (map.isPassable(x, y, 2)) {
                    return 2;
                } else if (map.isPassable(x, y, 4)) {
                    return 4;
                }
                break;
            case 4:
                if (map.isPassable(x, y, 4)) {
                    return 4;
                } else if (map.isPassable(x, y, 3)) {
                    return 3;
                } else if (map.isPassable(x, y, 5)) {
                    return 5;
                }
                break;
            case 5:
                if (map.isPassable(x, y, 5)) {
                    return 5;
                } else if (map.isPassable(x, y, 4)) {
                    return 4;
                } else if (map.isPassable(x, y, 6)) {
                    return 6;
                }
                break;
            case 6:
                if (map.isPassable(x, y, 6)) {
                    return 6;
                } else if (map.isPassable(x, y, 5)) {
                    return 5;
                } else if (map.isPassable(x, y, 7)) {
                    return 7;
                }
                break;
            case 7:
                if (map.isPassable(x, y, 7)) {
                    return 7;
                } else if (map.isPassable(x, y, 6)) {
                    return 6;
                } else if (map.isPassable(x, y, 0)) {
                    return 0;
                }
                break;
            case 0:
                if (map.isPassable(x, y, 0)) {
                    return 0;
                } else if (map.isPassable(x, y, 7)) {
                    return 7;
                } else if (map.isPassable(x, y, 1)) {
                    return 1;
                }
                break;
            default:
                break;
        }
        return -1;
    }

    private void Poly(L1PcInstance pc) {
        int polyid = 0;
        int time = 1800;
        if (pc.getWeapon() != null) {
            // タイプ別分類
            switch (pc.getWeapon().getItem().getType()) {
                // 弓
                case 4:
                case 13:
                    if (pc.getLevel() < 55) {
                        int[] polyList = { 11382 };
                        polyid = polyList[CommonUtil.random(polyList.length)];
                    } else if (pc.getLevel() >= 55 && pc.getLevel() < 60) {
                        int[] polyList = { 11382, 11382 };
                        polyid = polyList[CommonUtil.random(polyList.length)];
                    } else if (pc.getLevel() >= 60 && pc.getLevel() < 65) {
                        int[] polyList = { 2284, 3892, 3895 };
                        polyid = polyList[CommonUtil.random(polyList.length)];
                    } else if (pc.getLevel() >= 65 && pc.getLevel() < 70) {
                        int[] polyList = { 2284, 3892, 3895, 6275 };
                        polyid = polyList[CommonUtil.random(polyList.length)];
                    } else if (pc.getLevel() >= 70 && pc.getLevel() < 75) {
                        int[] polyList = { 2284, 3892, 3895, 6275, 6278 };
                        polyid = polyList[CommonUtil.random(polyList.length)];
                    } else if (pc.getLevel() >= 75 && pc.getLevel() < 80) {
                        int[] polyList = { 2284, 3892, 3895, 6275, 6278, 8900 };
                        polyid = polyList[CommonUtil.random(polyList.length)];
                    } else if (pc.getLevel() >= 80) {
                        int[] polyList = { 2284, 3892, 3895, 6275, 6278, 8900, 8913 };
                        polyid = polyList[CommonUtil.random(polyList.length)];
                    }
                    L1PolyMorph.doPoly(pc, polyid, time, 1);
                    break;
                // クロウデュアルブレード
                case 11:
                case 12:
                    if (pc.getLevel() < 55) {
                        int[] polyList = { 6142 };
                        polyid = polyList[CommonUtil.random(polyList.length)];
                    } else if (pc.getLevel() >= 55 && pc.getLevel() < 60) {
                        int[] polyList = { 6142, 5727 };
                        polyid = polyList[CommonUtil.random(polyList.length)];
                    } else if (pc.getLevel() >= 60 && pc.getLevel() < 65) {
                        int[] polyList = { 6142, 5727, 5730 };
                        polyid = polyList[CommonUtil.random(polyList.length)];
                    } else if (pc.getLevel() >= 65 && pc.getLevel() < 70) {
                        int[] polyList = { 6142, 5727, 5730, 6281 };
                        polyid = polyList[CommonUtil.random(polyList.length)];
                    } else if (pc.getLevel() >= 70 && pc.getLevel() < 75) {
                        int[] polyList = { 6142, 5727, 5730, 6281, 6282 };
                        polyid = polyList[CommonUtil.random(polyList.length)];
                    } else if (pc.getLevel() >= 75 && pc.getLevel() < 80) {
                        int[] polyList = { 6142, 5727, 5730, 6281, 6282, 8851 };
                        polyid = polyList[CommonUtil.random(polyList.length)];
                    } else if (pc.getLevel() >= 80) {
                        int[] polyList = { 6142, 5727, 5730, 6281, 6282, 8851, 8978 };
                        polyid = polyList[CommonUtil.random(polyList.length)];
                    }
                    L1PolyMorph.doPoly(pc, polyid, time, 1);
                    break;
                // 杖
                case 7:
                case 16:
                    if (pc.getLevel() < 55) {
                        int[] polyList = { 6142 };
                        polyid = polyList[CommonUtil.random(polyList.length)];
                    } else if (pc.getLevel() >= 55 && pc.getLevel() < 60) {
                        int[] polyList = { 6142, 3890 };
                        polyid = polyList[CommonUtil.random(polyList.length)];
                    } else if (pc.getLevel() >= 60 && pc.getLevel() < 65) {
                        int[] polyList = { 6142, 3890, 3893 };
                        polyid = polyList[CommonUtil.random(polyList.length)];
                    } else if (pc.getLevel() >= 65 && pc.getLevel() < 70) {
                        int[] polyList = { 6142, 3890, 3893, 6274 };
                        polyid = polyList[CommonUtil.random(polyList.length)];
                    } else if (pc.getLevel() >= 70 && pc.getLevel() < 75) {
                        int[] polyList = { 6142, 3890, 3893, 6274, 6277 };
                        polyid = polyList[CommonUtil.random(polyList.length)];
                    } else if (pc.getLevel() >= 75 && pc.getLevel() < 80) {
                        int[] polyList = { 6142, 3890, 3893, 6274, 6277, 8817 };
                        polyid = polyList[CommonUtil.random(polyList.length)];
                    } else if (pc.getLevel() >= 80) {
                        int[] polyList = { 6142, 3890, 3893, 6274, 6277, 8817, 8812 };
                        polyid = polyList[CommonUtil.random(polyList.length)];
                    }
                    L1PolyMorph.doPoly(pc, polyid, time, 1);
                    break;
                // その他。
                default:
                    if (pc.getLevel() < 55) {
                        int[] polyList = { 6142 };
                        polyid = polyList[CommonUtil.random(polyList.length)];
                    } else if (pc.getLevel() >= 55 && pc.getLevel() < 60) {
                        int[] polyList = { 6142, 3890 };
                        polyid = polyList[CommonUtil.random(polyList.length)];
                    } else if (pc.getLevel() >= 60 && pc.getLevel() < 65) {
                        int[] polyList = { 6142, 3890, 3893 };
                        polyid = polyList[CommonUtil.random(polyList.length)];
                    } else if (pc.getLevel() >= 65 && pc.getLevel() < 70) {
                        int[] polyList = { 6142, 3890, 3893, 6273 };
                        polyid = polyList[CommonUtil.random(polyList.length)];
                    } else if (pc.getLevel() >= 70 && pc.getLevel() < 75) {
                        int[] polyList = { 6142, 3890, 3893, 6273, 6276 };
                        polyid = polyList[CommonUtil.random(polyList.length)];
                    } else if (pc.getLevel() >= 75 && pc.getLevel() < 80) {
                        int[] polyList = { 6142, 3890, 3893, 6273, 6276, 8817 };
                        polyid = polyList[CommonUtil.random(polyList.length)];
                    } else if (pc.getLevel() >= 80) {
                        int[] polyList = { 6142, 3890, 3893, 6273, 6276, 8817, 8812 };
                        polyid = polyList[CommonUtil.random(polyList.length)];
                    }
                    L1PolyMorph.doPoly(pc, polyid, time, 1);
                    break;
            }
        } else {
            if (pc.getLevel() < 55) {
                int[] polyList = { 6142 };
                polyid = polyList[CommonUtil.random(polyList.length)];
            } else if (pc.getLevel() >= 55 && pc.getLevel() < 60) {
                int[] polyList = { 6142, 3890 };
                polyid = polyList[CommonUtil.random(polyList.length)];
            } else if (pc.getLevel() >= 60 && pc.getLevel() < 65) {
                int[] polyList = { 6142, 3890, 3893 };
                polyid = polyList[CommonUtil.random(polyList.length)];
            } else if (pc.getLevel() >= 65 && pc.getLevel() < 70) {
                int[] polyList = { 6142, 3890, 3893, 6273 };
                polyid = polyList[CommonUtil.random(polyList.length)];
            } else if (pc.getLevel() >= 70 && pc.getLevel() < 75) {
                int[] polyList = { 6142, 3890, 3893, 6273, 6276 };
                polyid = polyList[CommonUtil.random(polyList.length)];
            } else if (pc.getLevel() >= 75 && pc.getLevel() < 80) {
                int[] polyList = { 6142, 3890, 3893, 6273, 6276, 8817 };
                polyid = polyList[CommonUtil.random(polyList.length)];
            } else if (pc.getLevel() >= 80) {
                int[] polyList = { 6142, 3890, 3893, 6273, 6276, 8817, 8812 };
                polyid = polyList[CommonUtil.random(polyList.length)];
            }
            L1PolyMorph.doPoly(pc, polyid, time, 1);
        }
    }

    private void teleport(L1PcInstance pc, int x, int y, short mapid) {
        try {
            pc.getMap().setPassable(pc.getLocation(), true);
            new L1Teleport().teleport(pc, x, y, mapid, CommonUtil.random(0, 7), false);
            //teleportAfterDelayCount = 3;
            attackList.clear();
            target = null;
            dropItem = null;
            groundInventory = null;
            object = null;
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private boolean isPickup(L1PcInstance pc) {
        try {
            if (L1World.getInstance().getVisibleObjects(pc, 1).size() >= 7) {
                dropItem = null;
                groundInventory = null;
                object = null;
                return false;
            }

            for (L1Object obj : L1World.getInstance().getObject()) {
                if (obj == null) {
                    continue;
                }
                if (obj.getMapId() != pc.getMapId()) {
                    continue;
                }

                if (pc.getLocation().getTileLineDistance(obj.getLocation()) <= 4) {

                    if (obj instanceof L1ItemInstance) {
                        groundInventory = L1World.getInstance().getInventory(obj.getX(), obj.getY(), obj.getMapId());
                        object = groundInventory.getItem(obj.getId());
                        if (object != null) {
                            dropItem = object;
                            groundInventory = null;
                            object = null;
                            return true;
                        }
                    }
                }
            }
            return false;
        } catch (Exception e) {
            //e.printStackTrace();
            return false;
        }
    }

    private void pickup(L1PcInstance pc) {
        try {
            boolean dropCheck = true;
            try {
                for (L1ItemInstance item : robot.getInventory().getItems()) {
                    if (item.isEquipped()) {
                        continue;
                    }
                    if (item.getItem().getItemId() == 40308) {
                        continue;
                    }
                    if (item.getItem().getItemId() == 41159) {
                        continue;
                    }
                    robot.getInventory().removeItem(item);
                }

                pickupCount++;

                if (pickupCount >= 5) {
                    dropItem = null;
                    groundInventory = null;
                    object = null;
                    setAiStatus(AI_STATUS_WALK);
                    pickupCount = 0;
                }

                if (robot.getName().equals(Config.ROBOT_NAME)) {
                    System.out.println("ロボット切り替え");
                }
                if (dropItem != null && !isDistance(robot.getX(), robot.getY(), robot.getMapId(), dropItem.getX(), dropItem.getY(), dropItem.getMapId(), (robot.isElf() ? 8 : 1))) {

                    toMoving(dropItem, dropItem.getX(), dropItem.getY(), 0, true);

                    groundInventory = L1World.getInstance().getInventory(dropItem.getX(), dropItem.getY(), dropItem.getMapId());
                    object = groundInventory.getItem(dropItem.getId());
                    if (object == null) {
                        dropItem = null;
                        groundInventory = null;
                        object = null;
                        setAiStatus(AI_STATUS_WALK);
                    }
                    return;
                }
            } catch (Exception e) {
                dropItem = null;
                groundInventory = null;
                object = null;
                return;
            }

            for (L1Object obj : L1World.getInstance().getObject()) {
                if (obj.getMapId() != pc.getMapId()) {
                    continue;
                }

                if (pc.getLocation().getTileLineDistance(obj.getLocation()) <= 2) {
                    if (obj instanceof L1ItemInstance) {
                        L1Inventory groundInventory = L1World.getInstance().getInventory(obj.getX(), obj.getY(), obj.getMapId());
                        L1Object object = groundInventory.getItem(obj.getId());
                        if (object != null) {
                            L1ItemInstance item = (L1ItemInstance) object;
                            groundInventory.tradeItem(item.getId(), item.getCount(), pc.getInventory());
                            pc.getLight().turnOnOffLight();
                            Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, obj.getId(), ActionCodes.ACTION_Pickup));
                            dropItem = null;
                            groundInventory = null;
                            object = null;
                            dropCheck = false;

                            if (isPickup(robot)) {
                                setAiStatus(AI_STATUS_PICKUP);
                            } else {
                                setAiStatus(AI_STATUS_WALK);
                            }
                            break;
                        }
                    }
                }
            }

            if (dropCheck) {
                dropItem = null;
                groundInventory = null;
                object = null;
            }
        } catch (Exception e) {
            //e.printStackTrace();
            attackList.remove(target);
            target = null;
            dropItem = null;
            groundInventory = null;
            object = null;
            setAiStatus(AI_STATUS_WALK);
        }
    }
}
