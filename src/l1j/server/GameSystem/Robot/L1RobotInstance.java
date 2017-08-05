package l1j.server.GameSystem.Robot;

import static l1j.server.server.model.skill.L1SkillId.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import l1j.server.L1DatabaseFactory;
import l1j.server.GameSystem.AStar.AStar;
import l1j.server.GameSystem.AStar.Node;
import l1j.server.GameSystem.AStar.World;
import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.Opcodes;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.datatables.SprTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.Getback;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1EffectSpawn;
import l1j.server.server.model.L1GroundInventory;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillDelay;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_AttackPacket;
import l1j.server.server.serverpackets.S_ChangeHeading;
import l1j.server.server.serverpackets.S_ChangeShape;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_MoveCharPacket;
import l1j.server.server.serverpackets.S_OtherCharPacks;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.templates.L1Skills;
import l1j.server.server.utils.SQLUtil;

public class L1RobotInstance extends L1PcInstance {
    private static final long serialVersionUID = 1L;
    private static final int MOVE_SPEED = 0;
    private static final int ATTACK_SPEED = 1;
    private static final int MAGIC_SPEED = 2;
    public static final int DMG_MOTION_SPEED = 3;

    private static final int MOVE = 0;
    private static final int ATTACK = 1;
    private static Random _random = new Random(System.nanoTime());

    public Robot_Location_bean loc = null;
    public L1Character _target;
    public L1PcInstance _target2;
    public L1ItemInstance _targetItem;

    public byte LisBot_SpawnLocation = -1;
    public boolean LisBot = false;
    public boolean clan = false;
    public boolean huntingBot = false;
    public boolean spawnDoll = false;
    private byte LisBot_Move = 0;
    public boolean tel_Hunting = false;
    private int actionStatus = 0;
    private boolean FirstSkill = false;
    public boolean Hunt_End = false;
    public boolean IgnoreHitReturn = true;

    public String huntingBot_Location;
    public int huntingBot_Type = 0;

    private short potCount = 1000;
    private short cyanPotion = 10;

    private AStar aStar; // ルート変数
    private int[][] iPath; // ルート変数
    private Node tail; // ルート変数
    private int iCurrentPath; // ルート変数
    // private L1RobotInstance _instance = null;

	/*
     * private boolean _Rsaid = false;
	 *
	 * private boolean Rsaid() { return _Rsaid; }
	 *
	 * protected void setRsaid(boolean flag) { _Rsaid = flag; }
	 */

    private boolean _Townsaid = false;

    private boolean Townsaid() {
        return _Townsaid;
    }

    protected void setTownsaid(boolean flag) {
        _Townsaid = flag;
    }

	/*
     * private boolean _Dissaid = false;
	 *
	 * private boolean Dissaid() { return _Dissaid; }
	 *
	 * protected void setDissaid(boolean flag) { _Dissaid = flag; }
	 */

    private boolean _GLsaid = false;

    private boolean Glsaid() {
        return _GLsaid;
    }

    protected void setGlsaid(boolean flag) {
        _GLsaid = flag;
    }

    private int _shockStunDuration;
    private static final int[] stunTimeArray = {2000, 3000, 4000, 5000};

    private String _himent;
    private static final String[] himentArray = {"Hi", "どうぞ！", "攻撃しないで", "ハイ？", "じょうじ...", "どっかいって", "戦う？"};

    private String _townment;

    private String _glment;

    private static final int[] LisBotBuffSkill4 = {L1SkillId.PHYSICAL_ENCHANT_STR, L1SkillId.PHYSICAL_ENCHANT_DEX,
            L1SkillId.BLESS_WEAPON, L1SkillId.REMOVE_CURSE};

    public L1RobotInstance() {

        iPath = new int[300][2];
        aStar = new AStar();
        // _instance = this;
    }

    public void startAI() {
        new BrainThread().start();
    }

    private boolean _aiRunning = false;
    private boolean _actived = false;
    private int _sleep_time;
    public String _userTitle;

    protected void setAiRunning(boolean aiRunning) {
        _aiRunning = aiRunning;
    }

    protected boolean isAiRunning() {
        return _aiRunning;
    }

    protected void setActived(boolean actived) {
        _actived = actived;
    }

    protected boolean isActived() {
        return _actived;
    }

    protected void setSleepTime(int sleep_time) {
        _sleep_time = sleep_time;
    }

    protected int getSleepTime() {
        return _sleep_time;
    }

    public boolean _EndThread = false;

    class BrainThread implements Runnable {

        public void start() {
            setAiRunning(true);
            GeneralThreadPool.getInstance().execute(BrainThread.this);
            if (huntingBot)
                GeneralThreadPool.getInstance().execute(new PotionThread());
        }

        public void run() {
            try {
                if (_EndThread) {
                    setAiRunning(false);
                    return;
                }
                if (isParalyzed() || isSleeped()) {
                    GeneralThreadPool.getInstance().schedule(this, 200);
                    return;
                }

                if (delay != 0) {
                    GeneralThreadPool.getInstance().schedule(this, delay);
                    delay = 0;
                    return;
                }
                if (actionStatus == MOVE && delay_Bot != 0) {
                    GeneralThreadPool.getInstance().schedule(this, delay_Bot);
                    delay_Bot = 0;
                    return;
                }
                if (AI()) {
                    setAiRunning(false);
                    return;
                }
                if (getSleepTime() == 0)
                    setSleepTime(300);
            } catch (Exception e) {
                e.printStackTrace();
            }
            GeneralThreadPool.getInstance().schedule(this, getSleepTime());

        }
    }

    class PotionThread implements Runnable {
        public void start() {
            setAiRunning(true);
            GeneralThreadPool.getInstance().execute(PotionThread.this);
        }

        public void run() {
            try {
                if (_EndThread) {
                    setAiRunning(false);
                    return;
                }

                if (isDead()) {
                    GeneralThreadPool.getInstance().schedule(this, 500);
                    return;
                }

                if (isParalyzed() || isSleeped()) {
                    GeneralThreadPool.getInstance().schedule(this, 200);
                    return;
                }

                if (isTeleport()) {
                    GeneralThreadPool.getInstance().schedule(this, 400);
                    return;
                }

                int percent = (int) Math.round((double) getCurrentHp() / (double) getMaxHp() * 100);
                if (percent < 10 && huntingBot_Type == HUNT && !huntingBot_Location.startsWith("忘れられた島")) {
                    setCurrentHp(getCurrentHp() + 500);
                    returnBot();
                    GeneralThreadPool.getInstance().schedule(this, 2000);
                    return;
                } else if (percent < 30 && huntingBot_Type == HUNT && !huntingBot_Location.startsWith("忘れられた島")) {
                    setCurrentHp(getCurrentHp() + 500);
                    randomTel();
                    GeneralThreadPool.getInstance().schedule(this, 2000);
                    return;
                }
                int delay = Debuff();
                if (delay > 0) {
                    GeneralThreadPool.getInstance().schedule(this, delay);
                    return;
                }
                if (Poison()) {
                    GeneralThreadPool.getInstance().schedule(this, 300);
                    return;
                }
                delay = Potion();
                if (delay > 0) {
                    GeneralThreadPool.getInstance().schedule(this, delay);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            GeneralThreadPool.getInstance().schedule(this, 100);
        }
    }

    private boolean AI() {
        if (isTeleport()) {
            return false;
        }

        if (LisBot) {
            if (LisBot_SpawnLocation == 1 || LisBot_SpawnLocation == 0 || LisBot_SpawnLocation == 3)
                return false;
            if (LisBot_Move > 0)
                moveLisBot();
            else if (_random.nextInt(1000) <= 1)
                LisBot_Move = 1;
        } else if (huntingBot) {
            huntingBot();
        }
        return false;
    }

    private int Debuff() {
        // TODO 自動生成されたメソッド・スタブ
        // カース待機
        if (hasSkillEffect(L1SkillId.STATUS_CURSE_PARALYZING)) {
            returnBot();
            delay = 8000;
            return 8000;
        }
        if (hasSkillEffect(L1SkillId.DECAY_POTION)) {
            returnBot();
            int time = getSkillEffectTimeSec(L1SkillId.DECAY_POTION) * 1000;
            return (int) (delay = time);
        }
        if (hasSkillEffect(L1SkillId.SILENCE)) {
            returnBot();
            int time = getSkillEffectTimeSec(L1SkillId.SILENCE) * 1000;
            return (int) (delay = time);
        }
        return 0;
    }

    public boolean Poison() {
        // TODO 自動生成されたメソッド・スタブ
        if (hasSkillEffect(71) == true) { // ディケイポーションの状態
            return false;
        }
        if (getPoison() != null) {
            cancelAbsoluteBarrier(); // アブソルートバリアの解除
            Broadcaster.broadcastPacket(this, new S_SkillSound(getId(), 192), true);
            curePoison();
            cyanPotion--;
            if (cyanPotion <= 0)
                resetPotion();
            return true;
        }
        return false;
    }

    public static final int SETTING = 0;
    public static final int TEL_NPC_MOVE = 1;
    public static final int HUNT_MOVE = 2;
    public static final int HUNT = 3;
    public static final int DEATH = 4;
    public static final int EXIT = 10;

    public long Hunt_Exit_Time = 0;

    private boolean etc_Village_Move = false;
    private int cnt2 = 0;
    private Queue<Robot_Location_bean> location_queue = new ConcurrentLinkedQueue<Robot_Location_bean>();
    private Queue<L1ItemInstance> item_queue = new ConcurrentLinkedQueue<L1ItemInstance>();

    private void huntingBot() {
        try {
            if (isDead() && huntingBot_Type != DEATH) {
                delayBot(2000 + _random.nextInt(3000));
                huntingBot_Type = DEATH;
                return;
            }
            if (!isDead() && !isTeleport()) {

                if (!hasSkillEffect(L1SkillId.SHAPE_CHANGE)) {
                    setSkillEffect(L1SkillId.SHAPE_CHANGE, 1800 * 1000);
                    int time = getSkillEffectTimeSec(L1SkillId.SHAPE_CHANGE);
                    if (time == -1) {
                        endBot();
                        return;
                    }
                    Robot.poly(this);
                    Broadcaster.broadcastPacket(this, new S_ChangeShape(getId(), getTempCharGfx()));
                    Broadcaster.broadcastPacket(this, new S_CharVisualUpdate(this, getCurrentWeapon()));
                    return;
                }

                if (Robot.speedBuff(this)) {
                    setSleepTime(calcSleepTime(MAGIC_SPEED));
                    return;
                }

                if (!isSkillDelay()) {

                    if (isElf()) {

                        if (actionStatus == MOVE) {
                            int percent = (int) Math.round((double) getCurrentMp() / (double) getMaxMp() * 100);
                            if (percent < 55) {
                                // new L1SkillUse()
                                // .handleCommands(this,L1SkillId.BLOODY_SOUL,
                                // getId(), getX(), getY(), null,
                                // 0,L1SkillUse.TYPE_NORMAL);
                                S_DoActionGFX gfx = new S_DoActionGFX(getId(), 19);
                                broadcastPacket(gfx);
                                broadcastPacket(new S_SkillSound(getId(), 2178));
                                if (getCurrentMp() + 19 > getMaxMp())
                                    setCurrentMp(getMaxMp());
                                else
                                    setCurrentMp(getCurrentMp() + 19);
                                setSleepTime(calcSleepTime(MAGIC_SPEED));
                                return;
                            }
                        }
                    }

                }

                // Robot.Doll_Spawn(this);

            }

            // 他の地域であることをチェックその位置にテレポート
            if (loc == null) {
                location_queue.clear();
                ArrayList<Robot_Location_bean> list = Robot_Location.Location(this);
                if (list != null) {
                    for (Robot_Location_bean ro : list) {

                        if (huntingBot_Type == SETTING)
                            add_SETTING_Loc(ro);
                        location_queue.offer(ro);
                    }
                    loc = location_queue.poll();
                }
            }

            switch (huntingBot_Type) {
                case SETTING:// 店舗、倉庫、バフ
                case TEL_NPC_MOVE:// テレポーター移動
                    /** 村でのチャット **/
                    int townrandom = _random.nextInt(1000) + 1;
                    if (townrandom > 980) {
                        try {
                            Delay(350);
                            _townment = Robot_Hunt.getInstance().getMessage2();
                            Broadcaster.broadcastPacket(this, new S_ChatPacket(this, _townment, Opcodes.S_SAY, 0));
                            setTownsaid(true);
                            _townment = null;
                        } catch (Exception e) {
                            return;
                        }
                    }
                    /** 村でのチャット **/
                    if (loc == null) {
                        huntingBot_Type++;
                        return;
                    }
                    if (isDistance(getX(), getY(), getMapId(), loc.getX(), loc.getY(), loc.getMapId(),
                            1 + _random.nextInt(10))) {
                        loc = location_queue.poll();
                        delayBot(5000 + _random.nextInt(15000));
                        if (loc != null && etc_Village_Move) {
                            setHeading(5);
                            telBot(loc.getX(), loc.getY(), loc.getMapId(), 3000 + _random.nextInt(3000));
                            loc = location_queue.poll();
                            etc_Village_Move = false;
                        }
                        if (loc == null) {
                            if (huntingBot_Type == SETTING)
                                zendleBuff();
                            huntingBot_Type++;
                        }
                        return;
                    }
                    break;
                case HUNT_MOVE: // 狩り場に移動
                    delayBot(500 + _random.nextInt(1000));
                    telBot(loc.getX(), loc.getY(), loc.getMapId());
                    location_queue.offer(loc);
                    loc = location_queue.poll();
                    huntingBot_Type++;
                    return;
                case HUNT: // 狩猟
                    if (checkTarget() || checkTarget2()) {
                        return;
                    }

                    if (tel_Hunting) {
                        delayBot(1000 + _random.nextInt(500));
                        randomTel(500 + _random.nextInt(1000));
                        setTownsaid(false);
                        setGlsaid(false);
                        return;

                    }

                    if (loc == null) {
                        delayBot(3000 + _random.nextInt(6000));
                        returnBot(1000 + _random.nextInt(2000));
                        return;
                    }

                    int range = _random.nextInt(5) + 1;

                    if (isDistance(getX(), getY(), getMapId(), loc.getX(), loc.getY(), getMapId(), range)) {
                        location_queue.offer(loc);
                        loc = location_queue.poll();
                        cnt2++;
                        if (cnt2 >= 3) {
                            passTargetList.clear();
                            passTargetList2.clear();
                            cnt2 = 0;
                            return;
                        }
                    }
                    break;
                case DEATH: // 死
                    int[] loc = Getback.GetBack_Restart(this);
                    Broadcaster.broadcastPacket(this, new S_RemoveObject(this), true);
                    setCurrentHp(getLevel());
                    set_food(225); // 死んだときに100％
                    setDead(false);
                    L1World.getInstance().moveVisibleObject(this, loc[0], loc[1], loc[2]);
                    setX(loc[0]);
                    setY(loc[1]);
                    setMap((short) loc[2]);
                    for (L1PcInstance pc2 : L1World.getInstance().getVisiblePlayer(this)) {
                        pc2.sendPackets(new S_OtherCharPacks(this, pc2));
                    }
                    _target = null; // リーク防止
                    _targetItem = null; // リーク防止
                    _target2 = null; // リーク防止
                    delayBot(3000 + _random.nextInt(6000));
                    returnBot(1000 + _random.nextInt(2000));
                    setTownsaid(false);
                    setGlsaid(false);
                    return;
                case EXIT: // 終了
                    return;
                default:
                    break;
            }

            if (!isDead() && loc != null) {
                moveBot();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void add_SETTING_Loc(Robot_Location_bean ro) {
        // TODO 自動生成されたメソッド・スタブ
        if (ro.getX() == 33457 && ro.getY() == 32819 && ro.getMapId() == 4) {// ギラン
            // ポーション店
            if (getX() >= 34047 && getX() <= 34064 && getY() >= 32273 && getY() <= 32297 && getMapId() == 4) {// オーレン
                location_queue.offer(new Robot_Location_bean(34064, 32278, 4));// テレポーターの位置
                location_queue.offer(new Robot_Location_bean(33438, 32796, 4));// テレポートする位置
            } else if (getX() >= 33065 && getX() <= 33093 && getY() >= 33385 && getY() <= 33411 && getMapId() == 4) {// ナイト
                location_queue.offer(new Robot_Location_bean(33080, 33384, 4));
                location_queue.offer(new Robot_Location_bean(33438, 32796, 4));
            }
            if (location_queue.size() > 0)
                etc_Village_Move = true;
        } else if (ro.getX() == 33432 && ro.getY() == 32815 && ro.getMapId() == 4) {// ギラン2ポーション店
            if (getX() >= 34047 && getX() <= 34064 && getY() >= 32273 && getY() <= 32297 && getMapId() == 4) {// オーレン
                location_queue.offer(new Robot_Location_bean(34064, 32278, 4));// テレポーターの位置
                location_queue.offer(new Robot_Location_bean(33438, 32796, 4));// テレポートする位置
            } else if (getX() >= 33065 && getX() <= 33093 && getY() >= 33385 && getY() <= 33411 && getMapId() == 4) {// ナイト
                location_queue.offer(new Robot_Location_bean(33080, 33384, 4));
                location_queue.offer(new Robot_Location_bean(33438, 32796, 4));
            }
            if (location_queue.size() > 0)
                etc_Village_Move = true;
        } else if (ro.getX() == 33428 && ro.getY() == 32806 && ro.getMapId() == 4) {// ギラン3,5アデン上段
            if (getX() >= 34047 && getX() <= 34064 && getY() >= 32273 && getY() <= 32297 && getMapId() == 4) {// オーレン
                location_queue.offer(new Robot_Location_bean(34064, 32278, 4));// テレポーター位置
                location_queue.offer(new Robot_Location_bean(33438, 32796, 4));// テレポートする位置
            } else if (getX() >= 33065 && getX() <= 33093 && getY() >= 33385 && getY() <= 33411 && getMapId() == 4) {// ナイト
                location_queue.offer(new Robot_Location_bean(33080, 33384, 4));
                location_queue.offer(new Robot_Location_bean(33438, 32796, 4));
            }
            if (location_queue.size() > 0)
                etc_Village_Move = true;
        } else if (ro.getX() == 33437 && ro.getY() == 32803 && ro.getMapId() == 4) {// ギラン4
            // ジェンドール
            if (getX() >= 34047 && getX() <= 34064 && getY() >= 32273 && getY() <= 32297 && getMapId() == 4) {// オーレン
                location_queue.offer(new Robot_Location_bean(34064, 32278, 4));// テレポーター位置
                location_queue.offer(new Robot_Location_bean(33438, 32796, 4));// テレポートする位置
            } else if (getX() >= 33065 && getX() <= 33093 && getY() >= 33385 && getY() <= 33411 && getMapId() == 4) {// ナイト
                location_queue.offer(new Robot_Location_bean(33080, 33384, 4));
                location_queue.offer(new Robot_Location_bean(33438, 32796, 4));
            }
            if (location_queue.size() > 0)
                etc_Village_Move = true;
        } else if (ro.getX() == 34065 && ro.getY() == 32287 && ro.getMapId() == 4) {// オーレンポーション店
            if (getX() >= 33065 && getX() <= 33093 && getY() >= 33385 && getY() <= 33411 && getMapId() == 4) {// ナイト
                location_queue.offer(new Robot_Location_bean(33080, 33384, 4));
                location_queue.offer(new Robot_Location_bean(34062, 32278, 4));
            } else if (getX() >= 33410 && getX() <= 33461 && getY() >= 32788 && getY() <= 32838 && getMapId() == 4) {// ギラン
                location_queue.offer(new Robot_Location_bean(33437, 32794, 4));
                location_queue.offer(new Robot_Location_bean(34062, 32278, 4));
            }
            if (location_queue.size() > 0)
                etc_Village_Move = true;
        } else if (ro.getX() == 32596 && ro.getY() == 32741 && ro.getMapId() == 4) {// グルーディン
            // ポーション店
            if (getX() >= 33065 && getX() <= 33093 && getY() >= 33385 && getY() <= 33411 && getMapId() == 4) {// ナイト
                location_queue.offer(new Robot_Location_bean(33080, 33384, 4));
                location_queue.offer(new Robot_Location_bean(32608, 32734, 4));
            } else if (getX() >= 33410 && getX() <= 33461 && getY() >= 32788 && getY() <= 32838 && getMapId() == 4) {// ギラン
                location_queue.offer(new Robot_Location_bean(33437, 32794, 4));
                location_queue.offer(new Robot_Location_bean(32608, 32734, 4));
            } else if (getX() >= 34047 && getX() <= 34064 && getY() >= 32273 && getY() <= 32297 && getMapId() == 4) {// オーレン
                location_queue.offer(new Robot_Location_bean(34064, 32278, 4));// テレポーター位置
                location_queue.offer(new Robot_Location_bean(32608, 32734, 4));
            }
            if (location_queue.size() > 0)
                etc_Village_Move = true;
        } else if (ro.getX() == 33738 && ro.getY() == 32494 && ro.getMapId() == 4) {// ウェルダン
            // ポーション店
            if (getX() >= 33065 && getX() <= 33093 && getY() >= 33385 && getY() <= 33411 && getMapId() == 4) {// ナイト
                location_queue.offer(new Robot_Location_bean(33080, 33384, 4));
                location_queue.offer(new Robot_Location_bean(33709, 32500, 4));
            } else if (getX() >= 33410 && getX() <= 33461 && getY() >= 32788 && getY() <= 32838 && getMapId() == 4) {// ギラン
                location_queue.offer(new Robot_Location_bean(33437, 32794, 4));
                location_queue.offer(new Robot_Location_bean(33709, 32500, 4));
            } else if (getX() >= 34047 && getX() <= 34064 && getY() >= 32273 && getY() <= 32297 && getMapId() == 4) {// オーレン
                location_queue.offer(new Robot_Location_bean(34064, 32278, 4));// テレポーター位置
                location_queue.offer(new Robot_Location_bean(33709, 32500, 4));
            }
            if (location_queue.size() > 0)
                etc_Village_Move = true;
        }
    }

    public void endBot() {
        endBot(1000 + _random.nextInt(20000));
    }

    public void endBot(int time) {
        Hunt_End = true;
        GeneralThreadPool.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                // TODO自動生成されたメソッド・スタブ
                _EndThread = true;
                for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(L1RobotInstance.this)) {
                    pc.sendPackets(new S_RemoveObject(L1RobotInstance.this), true);
                    pc.removeKnownObject(L1RobotInstance.this);
                }
                L1World world = L1World.getInstance();
                world.removeVisibleObject(L1RobotInstance.this);
                world.removeObject(L1RobotInstance.this);
                removeAllKnownObjects();
                stopHalloweenRegeneration();
                stopPapuBlessing();
                stopAHRegeneration();
                stopHpRegenerationByDoll();
                stopMpRegenerationByDoll();
                stopSHRegeneration();
                stopMpDecreaseByScales();
                stopEtcMonitor();
                huntingBot_Location = null;
                huntingBot = false;
                huntingBot_Type = 0;
                Hunt_End = false;
                IgnoreHitReturn = true; // 打撃帰還元false
                loc = null;
                updateconnect(false);
                Robot.Doll_Delete(L1RobotInstance.this, true);
                Robot_Hunt.getInstance().put(L1RobotInstance.this);
            }

        }, time);
    }

    public void randomTel() {
        delayBot(1000);
        randomTel(1);
        passTargetList.clear();
        passTargetList2.clear();

    }

    private void randomTel(int time) {
        L1Location newLocation = getLocation().randomLocation(200, true);
        int newX = newLocation.getX();
        int newY = newLocation.getY();
        short mapId = (short) newLocation.getMapId();
        telBot(newX, newY, mapId, time);
    }

    public void returnBot() {
        returnBot(1);
    }

    public void returnBot(int time) {
        int[] loc = new int[3];
        _random.setSeed(System.currentTimeMillis());
        switch (_random.nextInt(10)) {
            case 0:
                loc[0] = 33433;
                loc[1] = 32800;
                loc[2] = 4;
                break;
            case 1:
                loc[0] = 33418;
                loc[1] = 32815;
                loc[2] = 4;
                break;
            case 2:
                loc[0] = 33425;
                loc[1] = 32827;
                loc[2] = 4;
                break;
            case 3:
                loc[0] = 33442;
                loc[1] = 32797;
                loc[2] = 4;
                break;
            case 6:
            case 5:
            case 4:
                loc[0] = 34056;
                loc[1] = 32279;
                loc[2] = 4;
                break;
            case 7:
            case 8:
            case 9:
                loc[0] = 33080;
                loc[1] = 33392;
                loc[2] = 4;
                break;
            default:
                loc[0] = 33442;
                loc[1] = 32797;
                loc[2] = 4;
                break;
        }
        telBot(loc[0], loc[1], loc[2], time);
        if (huntingBot) {
            item_queue.clear();
            passTargetList.clear();
            passTargetList2.clear();
            huntingBot_Type = SETTING;
            this.loc = null;
        }
    }

    private int Potion() {
        if (hasSkillEffect(10513))
            return 1000;
        if (hasSkillEffect(71) == true) { // ディケイポーションの状態
            return 0;
        }
        int percent = (int) Math.round((double) getCurrentHp() / (double) getMaxHp() * 100);
        int gfxid = 0;
        int healHp = 0;
        int delay = 0;

        if (percent < 95) {
            gfxid = 197; // アカ189
            healHp = 45 + _random.nextInt(35);
            delay = 800;
            potCount--;
        }

        if (healHp == 0)
            return 0;
        // アブソリュートバリアの解除
        cancelAbsoluteBarrier();
        Broadcaster.broadcastPacket(this, new S_SkillSound(getId(), gfxid), true);
        if (hasSkillEffect(POLLUTE_WATER) || hasSkillEffect(10517)) { // ポルートウォーター中は回復量1/2倍
            healHp /= 2;
        }

        setCurrentHp(getCurrentHp() + healHp);
        if (potCount <= 0) {
            resetPotion();
        }
        return delay;
    }

    private void resetPotion() {
		/*
		 * ディレイ（2000 + _random.nextInt（14000））;帰還（）;
		 */

        potCount = (short) (800 + _random.nextInt(1000));
        cyanPotion = (short) (1000);
    }

    private boolean checkTarget() {
        if (_target == null && _targetItem == null) {
            searchTarget();
        }
        if (_target != null && _target instanceof L1MonsterInstance) {
            if (((L1MonsterInstance) _target).getHiddenStatus() != L1NpcInstance.HIDDEN_STATUS_NONE || _target.isDead()
                    || ((L1MonsterInstance) _target)._destroyed || ((L1MonsterInstance) _target).getTarget() != this
                    || _target.isInvisble()) {
                _target = null;
                searchTarget();
                setSleepTime(100);
            }
        }
        if (_targetItem != null) {
            L1Inventory groundInventory = L1World.getInstance().getInventory(_targetItem.getX(), _targetItem.getY(),
                    _targetItem.getMapId());
            if (!groundInventory.checkItem(_targetItem.getItemId())) {
                _targetItem = null;
                searchTarget(); // 人工知能
                setSleepTime(100);
            } else {
                onTargetItem();
                return true;
            }
        } else if (_target != null && _target2 == null) { // 殴る奴いないとき
            return onTarget();
        }
        return false;
    }

    private boolean checkTarget2() {
        // TODO自動生成されたメソッド・スタブ
        if (_target2 == null) {
            searchTarget();
        }

        if (_target2 != null && _target2 instanceof L1PcInstance) {
            if (_target2.isDead() || _target2.isInvisble()) {
                _target2 = null;
                searchTarget();
                setSleepTime(100);
            } else {
                return onTarget2();

            }
        }
        return false;
    }

    private void searchTarget() {

        int MaxRange = 2;

        ArrayList<L1Object> list = L1World.getInstance().getVisibleObjects(this);
        ArrayList<L1PcInstance> list2 = L1World.getInstance().getVisiblePlayer(this);

        if (list2.contains(_target2)) {
            return;
        }
        // 殴ってない奴を殴らせAI

        if (list.size() > 1)
            Collections.shuffle(list);

        int mapid = getMapId();
        for (L1Object obj : list) {
            if (obj instanceof L1GroundInventory) {
                L1GroundInventory inv = (L1GroundInventory) obj;
                for (L1ItemInstance item : inv.getItems()) {
                    if (item != null && !isDistance(getX(), getY(), mapid, item.getX(), item.getY(), mapid, 20)) {
                        continue;
                    }
                    if (item != null && isDistance(getX(), getY(), mapid, item.getX(), item.getY(), mapid, 10)
                            && !isDistance(getX(), getY(), mapid, item.getX(), item.getY(), mapid, -1)) {
                        if (_serchCource(item.getX(), item.getY()) == -1) {
                            continue;
                        }
                        if (item_queue.contains(item)) {
                            continue;
                        }
                        item_queue.offer(item);
                        list = null; // リーク防止 2015.11.26
                        obj = null; // リーク防止 2015.11.26
                        item = null; // リーク防止
                    }
                }
            }
        }
        if (item_queue.size() > 0) {
            _targetItem = item_queue.poll();
            return;
        }

        for (int i = 0; i <= MaxRange; i++) {

            list = L1World.getInstance().getVisibleObjects(this, i == 0 ? 1 : 4 * i);
            list2 = L1World.getInstance().getVisiblePlayer(this, i == 0 ? 1 : 4 * i);

            if (list2.size() > 1)
                Collections.shuffle(list2);
            if (list.size() > 1)
                Collections.shuffle(list);
            if (list.size() > 1 && list2.size() > 1)
                Collections.shuffle(list2);

            for (L1PcInstance obj2 : list2) {
                if (obj2 instanceof L1PcInstance) {
                    L1PcInstance saram = (L1PcInstance) obj2;

                    if (passTargetList2.contains(obj2)) {
                        continue;
                    }
                    if (saram.getCurrentHp() <= 0 || saram.isDead()) {
                        continue;
                    }
                    if (saram.isInvisble()) {
                        continue;
                    }
                    if (saram.getMap().isSafetyZone(saram.getX(), saram.getY())) {
                        continue;
                    }
                    if (getClanid() == saram.getClanid()) {
                        continue;
                    }

                    if (obj2 != null && !isDistance(getX(), getY(), mapid, obj2.getX(), obj2.getY(), mapid, 20)) {
                        continue;
                    }
                    if (obj2 != null && isDistance(getX(), getY(), mapid, obj2.getX(), obj2.getY(), mapid, 10)
                            && !isDistance(getX(), getY(), mapid, obj2.getX(), obj2.getY(), mapid, -1)) {
                        if (_serchCource(obj2.getX(), obj2.getY()) == -1) {
                            passTargetList2.add(obj2);
                            continue;
                        }
                    }

                    _target2 = saram;
                    FirstSkill = false;
                    // setRsaid(false); //ロボットチャット
                    // setDissaid(false); //距離チャット
                    list2 = null;// リーク防止 2015.11.26
                    obj2 = null; // リーク防止 2015.11.26
                    saram = null;
                    return;
                }
            }
            for (L1Object obj : list) {
                if (obj instanceof L1MonsterInstance) {
                    L1MonsterInstance mon = (L1MonsterInstance) obj;

                    if (passTargetList.contains(obj)) {
                        continue;
                    }

                    if (mon.getCurrentHp() <= 0 || mon.isDead()) {
                        continue;
                    }
                    if (mon.getHiddenStatus() != L1NpcInstance.HIDDEN_STATUS_NONE || mon._destroyed
                            || mon.isInvisble()) {
                        continue;
                    }
                    if (mon.getNpcId() == 100623 || mon.getNpcId() == 100624 || mon.getNpcId() == 45941
                            || (mon.getNpcId() >= 46048 && mon.getNpcId() <= 46052)) {
                        continue;
                    }
                    if (mon.getNpcId() >= 70981 && mon.getNpcId() <= 70984) {
                        continue;
                    }
                    if (mon.getTarget() != null && mon.getTarget() != this) {
                        continue;
                    }
                    if (obj != null && !isDistance(getX(), getY(), mapid, obj.getX(), obj.getY(), mapid, 20)) {
                        continue;
                    }
                    if (obj != null && _serchCource(obj.getX(), obj.getY()) == -1) {
                        passTargetList.add(obj);
                        continue;
                    }

                    _target = mon;
                    FirstSkill = false;
                    list = null; // リーク防止2015.11.26
                    obj = null; // リーク防止2015.11.26
                    mon = null;
                    return;
                }
            }
        }
    }

    public void onTargetItem() {

        if (_targetItem == null) {
            return;
        }
        if (getLocation().getTileLineDistance(_targetItem.getLocation()) <= 1) {
            pickupTargetItem();
            setSleepTime(800 + _random.nextInt(400));

        } else {
			/*
			 * int dir = moveDirection(_targetItem.getX(), _targetItem.getY(),
			 * _targetItem.getMapId());
			 */
            int dir = moveDirectionMatiz(_targetItem.getX(), _targetItem.getY(), _targetItem.getMapId());
            if (dir == -1) {
                _targetItem = null;
            } else {
                boolean tail = World.isThroughObject(getX(), getY(), getMapId(), dir);
                int tmpx = aStar.getXY(dir, true) + getX();
                int tmpy = aStar.getXY(dir, false) + getY();
                boolean obj = World.isMapdynamic(tmpx, tmpy, getMapId());
                boolean door = World.moveDoor(getX(), getY(), getMapId(), dir);
                if (tail && !obj && !door) {
                    setDirectionMove(dir);
                }
                setSleepTime(calcSleepTime(MOVE_SPEED));
            }
        }
    }

    private void pickupTargetItem() {
        int chdir = calcheading(this, _targetItem.getX(), _targetItem.getY());
        if (getHeading() != chdir) {
            setHeading(chdir);
            Broadcaster.broadcastPacket(this, new S_ChangeHeading(this), true);
        }
        Broadcaster.broadcastPacket(this, new S_AttackPacket(this, _targetItem.getId(), ActionCodes.ACTION_Pickup),
                true);
        L1Inventory groundInventory = L1World.getInstance().getInventory(_targetItem.getX(), _targetItem.getY(),
                _targetItem.getMapId());
        groundInventory.tradeItem(_targetItem, _targetItem.getCount(), getInventory());
        _targetItem = null;
    }

    private ArrayList<L1Object> passTargetList = new ArrayList<L1Object>();
    private ArrayList<L1PcInstance> passTargetList2 = new ArrayList<L1PcInstance>();

    public boolean onTarget() {
        setActived(true);
        _targetItem = null;
        L1Character target = _target;

        int percent = (int) Math.round((double) getCurrentHp() / (double) getMaxHp() * 100);
        if (_target2 != null && percent < 85) {
            _target = null;
            return checkTarget2(); // 人工知能

        }

        if (target == null) {
            return false;
        }

        /** ウェチャン **/
        int glrandom = _random.nextInt(1000) + 1;
        if (!Glsaid() && glrandom > 980) {
            etcWindow();
        }
        /** ウェチャン **/

        int escapeDistance = 15;
        if (hasSkillEffect(L1SkillId.DARKNESS) || hasSkillEffect(L1SkillId.CURSE_BLIND))
            escapeDistance = 1;
        int calcx = (int) getLocation().getX() - target.getLocation().getX();
        int calcy = (int) getLocation().getY() - target.getLocation().getY();

        if (Math.abs(calcx) > escapeDistance || Math.abs(calcy) > escapeDistance) {
            _target = null;
            return false;
        }
        boolean tail = World.isThroughAttack(getX(), getY(), getMapId(),
                calcheading(this, target.getX(), target.getY()));

        if (getX() == _target.getX() && getY() == _target.getY() && getMapId() == _target.getMapId())
            tail = true;

        boolean door = World.moveDoor(getX(), getY(), getMapId(), calcheading(this, target.getX(), target.getY()));

        int range = 1;
        if (isElf() && getCurrentWeapon() == 20)
            range = 11;
        // 初段がフォースレイヤー、またはトリプルアロー、または魔法？
        if (!FirstSkill && !isSkillDelay() && getCurrentMp() > 30) {
            int skillId = 0;
            int skill_range = 11;
            if (isElf() && getCurrentWeapon() == 20) {
                skillId = L1SkillId.TRIPLE_ARROW;
            } else if (isDragonknight()) {
                skillId = L1SkillId.FOU_SLAYER;
                skill_range = 1;
            }
            if (skillId > 0) {
                if (isAttackPosition(this, target.getX(), target.getY(), target.getMapId(), skill_range) == true
                        && isAttackPosition(target, getX(), getY(), getMapId(), skill_range) == true) {
                    FirstSkill = true;
                    new L1SkillUse().handleCommands(this, skillId, _target.getId(), _target.getX(), _target.getY(),
                            null, 0, L1SkillUse.TYPE_NORMAL);
                    setSleepTime(calcSleepTime(MAGIC_SPEED));
                    actionStatus = ATTACK;
                    return true;
                }
            }
        }

        if (isAttackPosition(this, target.getX(), target.getY(), target.getMapId(), range) == true
                && isAttackPosition(target, getX(), getY(), getMapId(), range) == true

                ) {// 基本攻撃範囲
            if (door || !tail) {
                cnt++;
                if (cnt > 5) {
                    _target = null;
                    cnt = 0;
                }
                return false;
            }
            setHeading(targetDirection(target.getX(), target.getY()));
            attackTarget(target);
            actionStatus = ATTACK;
            return true;

        } else {
			/*
			 * int dir = moveDirection(target.getX(), target.getY(),
			 * target.getMapId());
			 */
            int dir = moveDirectionMatiz(target.getX(), target.getY(), target.getMapId());
            if (dir == -1) {
                passTargetList.add(_target);
                _target = null;
                return false;
            } else {
                boolean tail2 = World.isThroughObject(getX(), getY(), getMapId(), dir);
                if (door || !tail2) {
                    cnt++;
                    if (cnt > 5) {
                        _target = null;
                        cnt = 0;
                    }
                    return false;
                }
                setDirectionMove(dir);
                setSleepTime(calcSleepTime(MOVE_SPEED));
            }
        }
        return true;
    }

    public <L1Pcinstance> boolean onTarget2() {
        setActived(true);
        _targetItem = null;
        _target = null;
        L1PcInstance target2 = _target2;

        if (target2 == null) {
            try {
                Delay(1000);
                return false;
            } catch (Exception e) {
            }

        }

        /** 遭遇時のチャット **/
        int hirandom = _random.nextInt(1000) + 1;
        if (hirandom > 995 && !Glsaid() && !target2.isRobot()) {
            _himent = himentArray[_random.nextInt(himentArray.length)];
            try {
                Delay(1500);
                Broadcaster.broadcastPacket(this, new S_ChatPacket(this, _himent, Opcodes.S_SAY, 0));
                setGlsaid(true);
                _himent = null;
            } catch (Exception e) {
            }
        }
        /** 遭遇時のチャット **/

        int escapeDistance = 15;
        if (hasSkillEffect(L1SkillId.DARKNESS) || hasSkillEffect(L1SkillId.CURSE_BLIND))
            escapeDistance = 1;
        int calcx = (int) getLocation().getX() - target2.getLocation().getX();
        int calcy = (int) getLocation().getY() - target2.getLocation().getY();

        if (Math.abs(calcx) > escapeDistance || Math.abs(calcy) > escapeDistance) {
            _target2 = null;
            return false;
        }

        /** 逃げる時のチャット **/
		/*
		 * int disrandom= _random.nextInt(100)+1; if(disrandom > 50 && !isElf()
		 * && Rsaid() && !target2.isRobot() && Math.abs(calcx) > 6 && !Dissaid()
		 * || disrandom > 50 && !isElf() && Rsaid() && !target2.isRobot() &&
		 * Math.abs(calcy) > 6 && !Dissaid() ){ _disment =
		 * dismentArray[_random.nextInt(dismentArray.length)]; String NAME =
		 * target2.getName(); try{ Delay(1500); Broadcaster.broadcastPacket(this
		 * ,new S_ChatPacket(this, NAME+_disment, Opcodes.S_OPCODE_NORMALCHAT,
		 * 0)); cnt++; if (cnt > 2) { setDissaid(true); _disment = null; }
		 * }catch(Exception e){} }
		 */
        /** 逃げる時のチャット **/

        boolean tail = World.isThroughAttack(getX(), getY(), getMapId(),
                calcheading(this, target2.getX(), target2.getY()));

        if (getX() == _target2.getX() && getY() == _target2.getY() && getMapId() == _target2.getMapId())
            tail = true;

        boolean door = World.moveDoor(getX(), getY(), getMapId(), calcheading(this, target2.getX(), target2.getY()));

        int range = 1;
        if (isElf() && getCurrentWeapon() == 20)
            range = 11;

        // 初段ポーまたはトリプルまたは魔法？
        if (!FirstSkill && !isSkillDelay() && getCurrentMp() > 30) {
            int skillId = 0;
            int skill_range = 11;
            if (isElf() && getCurrentWeapon() == 20) {
                skillId = L1SkillId.TRIPLE_ARROW;
            } else if (isWizard()) { // ウィザードBOT DIG
                skillId = L1SkillId.DISINTEGRATE;
            } else if (isDragonknight()) {
                skillId = L1SkillId.FOU_SLAYER;
                skill_range = 1;
            } else if (isKnight()) {
                skillId = L1SkillId.SHOCK_STUN;
                skill_range = 1;
            }

            if (skillId > 0) {
                if (isAttackPosition(this, target2.getX(), target2.getY(), target2.getMapId(), skill_range) == true
                        && isAttackPosition(target2, getX(), getY(), getMapId(), skill_range) == true) {

                    FirstSkill = true;

                    if (isKnight() && skillId == L1SkillId.SHOCK_STUN
                            && !target2.hasSkillEffect(L1SkillId.SHOCK_STUN)) {

                        int STrnd = _random.nextInt(10) + 1;
                        if (STrnd >= 7) {
                            _shockStunDuration = stunTimeArray[_random.nextInt(stunTimeArray.length)];
                            S_SkillSound ss1 = new S_SkillSound(target2.getId(), 4434);
                            target2.sendPackets(ss1);
                            Broadcaster.broadcastPacket(target2, ss1);
                            ss1 = null;
                            target2.setSkillEffect(L1SkillId.SHOCK_STUN, _shockStunDuration);
                            L1EffectSpawn.getInstance().spawnEffect(81162, _shockStunDuration, target2.getX(),
                                    target2.getY(), target2.getMapId());
                            S_Paralysis par = new S_Paralysis(S_Paralysis.TYPE_STUN, true);
                            target2.sendPackets(par);
                            par = null;

                            L1SkillDelay.onSkillUse(this, 8000);

                        } else {
                            S_SkillSound ss1 = new S_SkillSound(target2.getId(), 4434);
                            target2.sendPackets(ss1);
                            Broadcaster.broadcastPacket(target2, ss1);
                            ss1 = null;
                            L1SkillDelay.onSkillUse(this, 8000);
                        }
                    } else if (!isKnight()) {

                        new L1SkillUse().handleCommands(this, skillId, _target2.getId(), _target2.getX(),
                                _target2.getY(), null, 0, L1SkillUse.TYPE_NORMAL);
                    }
                    int drandom = _random.nextInt(10) + 1;
                    if (drandom > 6 && isDarkelf()) { // ダークエルフ ダブル
                        Broadcaster.broadcastPacket(_target2, new S_SkillSound(_target2.getId(), 3398));
                        _target2.sendPackets(new S_SkillSound(_target2.getId(), 3398));
                        _target2.receiveDamage(this, 100);
                    }
                    setSleepTime(calcSleepTime(MAGIC_SPEED));
                    actionStatus = ATTACK;
                    return true;
                }
            }
        }
        if (isAttackPosition(this, target2.getX(), target2.getY(), target2.getMapId(), range) == true
                && isAttackPosition(target2, getX(), getY(), getMapId(), range) == true) {// 基本攻撃範囲
            if (door || !tail) {
                cnt++;
                if (cnt > 5) {
                    _target2 = null;
                    cnt = 0;
                }
                return false;
            }

            setHeading(targetDirection(target2.getX(), target2.getY()));
            attackTarget(target2);
            actionStatus = ATTACK;
            return true;

        } else {
            // int dir = moveDirection(target2.getX(), target2.getY(),
            // target2.getMapId());
            int dir = moveDirectionMatiz(target2.getX(), target2.getY(), target2.getMapId());
            if (dir == -1) {
                passTargetList2.add(_target2);
                _target2 = null;
                return false;
            } else {
                boolean tail2 = World.isThroughObject(getX(), getY(), getMapId(), dir);
                if (door || !tail2) {
                    cnt++;
                    if (cnt > 5) {
                        _target2 = null;
                        cnt = 0;
                    }
                    return false;
                }
                setDirectionMove(dir);
                setSleepTime(calcSleepTime(MOVE_SPEED));
            }
        }

        return true;
    }

    public void attackTarget(L1Character target) {
        Random random = new Random();
        if (target instanceof L1PcInstance) {
            L1PcInstance player = (L1PcInstance) target;
            if (player.isTeleport())
                return;
        }

        boolean isCounterBarrier = false;
        boolean isMortalBody = false;
        boolean isLindArmor = false;
        L1Attack attack = new L1Attack(this, target);
        if (attack.calcHit()) {
            if (target.hasSkillEffect(L1SkillId.COUNTER_BARRIER)) {
                int chan = random.nextInt(100) + 1;
                boolean isProbability = false;
                if (20 > chan) {
                    isProbability = true;
                }
                boolean isShortDistance = attack.isShortDistance();
                if (isProbability && isShortDistance) {
                    isCounterBarrier = true;
                }
            } else if (target.hasSkillEffect(L1SkillId.MORTAL_BODY)) {
                int chan = random.nextInt(100) + 1;
                boolean isProbability = false;
                if (15 > chan) {
                    isProbability = true;
                }
                // boolean isShortDistance = attack.isShortDistance();
                if (isProbability /* && isShortDistance */) {
                    isMortalBody = true;
                }
            }
            if (!isCounterBarrier && !isMortalBody && !isLindArmor) {
                attack.calcDamage();
            }
        }
        if (isCounterBarrier) {
            attack.actionCounterBarrier();
            attack.commitCounterBarrier();
        } else if (isMortalBody) {
            attack.actionMortalBody();
            attack.commitMortalBody();
        } else {
            attack.action();
            attack.commit();
        }
        attack = null;
        setSleepTime(calcSleepTime(ATTACK_SPEED));

    }

    // 狩猟ボットの移動
    private void moveBot() {
        moveBot(loc.getX(), loc.getY());
    }

    private L1Location BackLoc_1th = null;
    private L1Location BackLoc_2th = null;
    private int cnt3 = 0;
    private boolean BackRR = false;

    private void moveBot(int x, int y) {
        // int dir = moveDirection(x, y, getMapId());
        int dir = moveDirectionMatiz(x, y, getMapId());
        if (dir == -1) {
            dir = new Random().nextInt(8);
            cnt++;
            if (cnt > 20) {
                delayBot(3000 + _random.nextInt(2000));
                returnBot(1000 + _random.nextInt(2000));
                cnt = 0;

                return;
            }
            setSleepTime(1000 + _random.nextInt(1000));
        } else {
            boolean tail2 = World.isThroughObject(getX(), getY(), getMapId(), dir);
            boolean door = World.moveDoor(getX(), getY(), getMapId(), calcheading(this, x, y));
            if (door || !tail2) {
                cnt++;
                if (cnt > 20) {
                    delayBot(3000 + _random.nextInt(2000));
                    returnBot(1000 + _random.nextInt(2000));
                    cnt = 0;
                    return;
                }
            }

            setDirectionMove(dir);
            setSleepTime(calcSleepTime(MOVE_SPEED));

            if ((BackLoc_1th != null && getLocation().getTileDistance(BackLoc_1th) == 0)
                    || (BackLoc_2th != null && getLocation().getTileDistance(BackLoc_2th) == 0))
                cnt3++;
            else
                cnt3 = 0;

            if (!BackRR)
                BackLoc_1th = new L1Location(getLocation());
            else
                BackLoc_2th = new L1Location(getLocation());

            BackRR = !BackRR;

            if (cnt3 > 20) {
                delayBot(3000 + _random.nextInt(2000));
                returnBot(1000 + _random.nextInt(2000));
                cnt3 = 0;

                return;
            }
        }
    }

    public void telBot(int x, int y, int mapid) {
        telBot(x, y, mapid, 1, true);
    }

    public void telBot(int x, int y, int mapid, int time) {
        telBot(x, y, mapid, time, true);
    }

    public void telBot(final int x, final int y, final int mapid, int time, final boolean effect) {
        if (huntingBot)
            item_queue.clear();
        GeneralThreadPool.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                // TODO 自動生成されたメソッド・スタブ
                try {
                    if (L1RobotInstance.this.isDead() || L1RobotInstance.this.isTeleport()
                            || L1RobotInstance.this.isParalyzed() || L1RobotInstance.this.isSleeped()
                            || L1RobotInstance.this.hasSkillEffect(L1SkillId.DESPERADO))
                        return;

                    setTeleport(true);
                    S_SkillSound ss = new S_SkillSound(getId(), 169);
                    S_RemoveObject ro = new S_RemoveObject(L1RobotInstance.this);
                    for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(L1RobotInstance.this)) {
                        if (effect)
                            pc.sendPackets(ss);
                        pc.sendPackets(ro);
                    }
                    Thread.sleep(280);
                    for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(L1RobotInstance.this)) {
                        pc.removeKnownObject(L1RobotInstance.this);
                        pc.sendPackets(ro); // テレポート虚像ないまま？

                    }
                    L1World.getInstance().moveVisibleObject(L1RobotInstance.this, x, y, mapid);
                    setX(x);
                    setY(y);
                    setMap((short) mapid);
                    setTeleport(false);
                } catch (Exception e) {
                }
            }
        }, time);
    }

    /*
     * private static final byte HEADING_TABLE_X[] = { 0, 1, 1, 1, 0, -1, -1, -1
     * }; private static final byte HEADING_TABLE_Y[] = { -1, -1, 0, 1, 1, 1, 0,
     * -1 };
     */
    private int moveDirectionMatiz(int x, int y, int m) {
        int dir = 0;
        // dir : 0 : -1
        // 現在の位置よりも大きく、目的地の位置よりも小さく移動可能な場所
        int mX = x - getX();
        int mY = y - getY();

        int dCase = 9;
        if (mX > 0 && mY > 0) {
            dCase = 1;
        } else if (mX == 0 && mY > 0) {
            dCase = 2;
        } else if (mX < 0 && mY > 0) {
            dCase = 3;
        } else if (mX < 0 && mY == 0) {
            dCase = 4;
        } else if (mX < 0 && mY < 0) {
            dCase = 5;
        } else if (mX == 0 && mY < 0) {
            dCase = 6;
        } else if (mX > 0 && mY < 0) {
            dCase = 7;
        } else if (mX > 0 && mY == 0) {
            dCase = 8;
        } else if (mX == 0 && mY == 0) {
            dCase = 9;
        }
        int direction[] = new int[3];
        int count = 0;
        while (true) {
            switch (dCase) {
                case 1:
                    direction[0] = 2;
                    direction[1] = 4;
                    direction[2] = 3;
                    break;
                case 2:
                    direction[0] = 4;
                    direction[1] = 4;
                    direction[2] = 4;
                    break;
                case 3:
                    direction[0] = 6;
                    direction[1] = 4;
                    direction[2] = 5;
                    break;
                case 4:
                    direction[0] = 6;
                    direction[1] = 6;
                    direction[2] = 6;
                    break;
                case 5:
                    direction[0] = 6;
                    direction[1] = 0;
                    direction[2] = 7;
                    break;
                case 6:
                    direction[0] = 0;
                    direction[1] = 0;
                    direction[2] = 0;
                    break;
                case 7:
                    direction[0] = 2;
                    direction[1] = 0;
                    direction[2] = 1;
                    break;
                case 8:
                    direction[0] = 2;
                    direction[1] = 2;
                    direction[2] = 2;
                    break;
                case 9:
                    direction[0] = -1;
                    direction[1] = -1;
                    direction[2] = -1;
                    break;
            }
            int rnd = new Random().nextInt(3);
            if (getMap().isPassable(getX() + HEADING_TABLE_X[direction[rnd]],
                    getY() + HEADING_TABLE_Y[direction[rnd]])) {
                dir = direction[rnd];
                break;
            } else {
                count++;
                if (count > 6) {
                    dir = -1;
                    break;
                }
            }

        }
        return dir;
    }

    private int moveDirection(int x, int y, int m) {
        int dir = 0;
        try {
            aStar.cleanTail();
            tail = aStar.searchTail(this, x, y, m, true);
        } catch (Exception e) {
            return -1;
        }
        if (tail != null) {
            iCurrentPath = -1;
            while (!_EndThread && tail != null) {
                if (tail.x == getX() && tail.y == getY()) {
                    // 現在の位置であれば、終了
                    break;
                }
                if (iCurrentPath >= 299 || isDead()) {
                    return -1;
                }
                iPath[++iCurrentPath][0] = tail.x;
                iPath[iCurrentPath][1] = tail.y;
                tail = tail.prev;

            }
            if (iCurrentPath != -1) {
                return aStar.calcheading(getX(), getY(), iPath[iCurrentPath][0], iPath[iCurrentPath][1]);
            } else {
                return -1;
            }
        } else {

            try {
                aStar.cleanTail();
                int calcx = (int) getLocation().getX() - loc.getX();
                int calcy = (int) getLocation().getY() - loc.getY();
                if ((Math.abs(calcx) <= 15 && Math.abs(calcy) <= 15) && loc != null) {
                    tail = aStar.searchNearTile(this, x, y, m, false);
                } else {
                    tail = aStar.searchNearTile(this, x, y, m, true);
                }
            } catch (Exception e) {
                return -1;
            }
            if (tail != null && !(tail.x == getX() && tail.y == getY())) {
                iCurrentPath = -1;
                while (!_EndThread && tail != null) {
                    if (tail.x == getX() && tail.y == getY()) {
                        // 現在の位置であれば、終了
                        break;
                    }
                    if (iCurrentPath >= 299 || isDead()) {
                        return -1;
                    }
                    iPath[++iCurrentPath][0] = tail.x;
                    iPath[iCurrentPath][1] = tail.y;
                    tail = tail.prev;
                }
                if (iCurrentPath != -1) {
                    return aStar.calcheading(getX(), getY(), iPath[iCurrentPath][0], iPath[iCurrentPath][1]);
                } else {
                    dir = -1;
                }
            } else {
                dir = -1;
                if (!huntingBot) {
                    int chdir = calcheading(this, x, y);
                    if (getHeading() != chdir) {
                        this.setHeading(calcheading(this, x, y));
                        Broadcaster.broadcastPacket(this, new S_ChangeHeading(this), true);
                    }
                }
            }

            return dir;
        }
    }

    private static final byte HEADING_TABLE_X[] = {0, 1, 1, 1, 0, -1, -1, -1};
    private static final byte HEADING_TABLE_Y[] = {-1, -1, 0, 1, 1, 1, 0, -1};

    private void setDirectionMove(int dir) {
        if (dir >= 0) {
            int nx = 0;
            int ny = 0;
            if (hasSkillEffect(L1SkillId.THUNDER_GRAB) || hasSkillEffect(L1SkillId.DESPERADO)
                    || hasSkillEffect(L1SkillId.POWEGRRIP)) {
                return;
            }
            int heading = 0;
            nx = HEADING_TABLE_X[dir];
            ny = HEADING_TABLE_Y[dir];
            heading = dir;
            int nnx = getX() + nx;
            int nny = getY() + ny;

            if (World.isMapdynamic(nnx, nny, getMapId())) {
                return;
            }
            setHeading(heading);
            L1World.getInstance().Move(this, nnx, nny);
            getMap().setPassable(getLocation(), true);

            setX(nnx);
            setY(nny);
            getMap().setPassable(nnx, nny, false);
            S_MoveCharPacket mp = new S_MoveCharPacket(this);
            Broadcaster.broadcastPacket(this, mp, true);
            actionStatus = MOVE;
        }
    }

    private static final double HASTE_RATE = 0.745;
    private static final double WAFFLE_RATE = 0.874;
    private static final double THIRDSPEED_RATE = 0.874;

    public int calcSleepTime(int type) {
        int interval = 640;
        try {
            int gfxid = this.getTempCharGfx();
            int weapon = this.getCurrentWeapon();
            if (gfxid == 3784 || gfxid == 6137 || gfxid == 6142 || gfxid == 6147 || gfxid == 6152 || gfxid == 6157
                    || gfxid == 9205 || gfxid == 9206) {

                if (weapon == 24)
                    weapon = 83;
            }
            switch (type) {
                case ATTACK_SPEED:
                    // interval = SprTable.getInstance().getAttackSpeed(gfxid,
                    // weapon +1);
                    // if(interval < 406)
                    interval = 426; // 680-304 396
                    if (weapon == 50)
                        interval = 446;
                    if (weapon == 20)
                        interval = 436;
                    break;
                case MOVE_SPEED:
                    // interval = SprTable.getInstance().getMoveSpeed(gfxid,
                    // weapon);
                    interval = 515; // 515
                    break;
                case MAGIC_SPEED:
                    interval = SprTable.getInstance().getNodirSpellSpeed(gfxid);
                    if (interval <= 0) {
                        interval = 120;
                    }
                    break;
                case DMG_MOTION_SPEED:
                    interval = SprTable.getInstance().getDmgMotionSpeed(gfxid);
                    if (interval <= 0) {
                        interval = 120;
                    }
                    break;
                default:
                    interval = SprTable.getInstance().getMoveSpeed(gfxid, weapon);
                    break;
            }

            if (gfxid == 13719 || gfxid == 13725 || gfxid == 13735) {// ロボット攻撃速度修正
                interval += 90; // ランカー変身速度遅い

            }

            if (this.isHaste() || getMoveSpeed() == 1) {
                interval *= HASTE_RATE;
            }
            if (type == MOVE_SPEED && this.isFastMovable()) {
                interval *= HASTE_RATE;
            }
            if (type == MOVE_SPEED && this.isBlackwizard() && this.isUgdraFruit()) {
                interval *= HASTE_RATE;
            }
            if (this.isBlood_lust()) { // ブラッドラスト
                interval *= HASTE_RATE;
            }
            if (this.isBrave()) {
                interval *= HASTE_RATE;
            }
            if (this.hasSkillEffect(L1SkillId.DANCING_BLADES)) {
                interval *= HASTE_RATE;
            }
            if (this.isElfBrave()) {
                interval *= WAFFLE_RATE;
            }
            if (this.isDragonPearl()) {
                interval *= THIRDSPEED_RATE;
            }
            if (hasSkillEffect(L1SkillId.WIND_SHACKLE)) {
                if (type == ATTACK_SPEED) {
                    interval *= 2;
                }
            }
            if (hasSkillEffect(L1SkillId.SLOW)) {
                interval *= 2;
            }

            if (type == ATTACK_SPEED) {
                int[] list = {100, 110, 120};
                interval += list[_random.nextInt(3)];
                list = null;
            } else {
                int[] list = {30, 40, 50};
                interval += list[_random.nextInt(3)];
                list = null;
            }

        } catch (Exception e) {
            // e.printStackTrace();
            if (interval == 0)
                return 640;
        }
        return interval;
    }

    public long delay = 0;
    public int delay_Bot = 0;

    public void delayBot(int i) {
        delay = i;
    }

    private int cnt = 0;

    public void moveLisBot() {
        if (LisBot_Move == 1) {
            if (loc == null) {
                if (LisBot_SpawnLocation == 2 || LisBot_SpawnLocation == 4 || LisBot_SpawnLocation == 5
                        || LisBot_SpawnLocation == 9 || LisBot_SpawnLocation == 8) // ギラン
                    loc = new Robot_Location_bean(33437, 32804, 4);
                else if (LisBot_SpawnLocation == 6 || LisBot_SpawnLocation == 7) // ハイネ
                    loc = new Robot_Location_bean(33613, 33248, 4);
                else if (LisBot_SpawnLocation == 10 || LisBot_SpawnLocation == 11) // グルーディン
                    loc = new Robot_Location_bean(32609, 32738, 4);
                else if (LisBot_SpawnLocation == 12) // TI
                    loc = new Robot_Location_bean(32587, 32929, 0);
                else if (LisBot_SpawnLocation == 13) // ナイト
                    loc = new Robot_Location_bean(33089, 33393, 4);
                else if (LisBot_SpawnLocation == 14) // オーレン
                    loc = new Robot_Location_bean(34065, 32280, 4);
                else if (LisBot_SpawnLocation == 15) // アデン
                    loc = new Robot_Location_bean(33938, 33358, 4);
            }
        } else if (LisBot_Move == 2) {
            if (loc == null) {
                if (LisBot_SpawnLocation == 2 || LisBot_SpawnLocation == 4 || LisBot_SpawnLocation == 5
                        || LisBot_SpawnLocation == 9 || LisBot_SpawnLocation == 8) // ギラン
                    loc = new Robot_Location_bean(33437, 32795, 4);
                else if (LisBot_SpawnLocation == 6 || LisBot_SpawnLocation == 7) // ハイネ
                    loc = new Robot_Location_bean(33613, 33257, 4);
                else if (LisBot_SpawnLocation == 10 || LisBot_SpawnLocation == 11) // グルーディン
                    loc = new Robot_Location_bean(32611, 32732, 4);
                else if (LisBot_SpawnLocation == 12) // TI
                    loc = new Robot_Location_bean(32583, 32922, 0);
                else if (LisBot_SpawnLocation == 13) // ナイト
                    loc = new Robot_Location_bean(33089, 33396, 4);
                else if (LisBot_SpawnLocation == 14) // オーレン
                    loc = new Robot_Location_bean(34063, 32278, 4);
                else if (LisBot_SpawnLocation == 15) // アデン
                    loc = new Robot_Location_bean(33934, 33351, 4);
            }
        }
        if (loc == null)
            return;

        if (isDistance(getX(), getY(), getMapId(), loc.getX(), loc.getY(), getMapId(), 1 + _random.nextInt(3))) {
            loc = null;
            if (LisBot_Move == 1) {
                LisBot_Move = 2;
                zendleBuff();
            } else {
                LisBot_Move = 0;
                telBot(32750, 32809, 39, 1000 + _random.nextInt(3000));
                _EndThread = true;
                stopHalloweenRegeneration();
                stopPapuBlessing();
                stopAHRegeneration();
                stopHpRegenerationByDoll();
                stopMpRegenerationByDoll();
                stopSHRegeneration();
                stopMpDecreaseByScales();
                stopEtcMonitor();
            }
            setSleepTime(4000 + _random.nextInt(2000));
            return;
        }
        if (loc == null)
            return;
        if (!isParalyzed()) {
            // int dir = moveDirection(loc.getX(), loc.getY(), loc.getMapId());
            int dir = moveDirectionMatiz(loc.getX(), loc.getY(), loc.getMapId());
            if (dir == -1) {
                cnt++;
            } else {
                boolean tail2 = World.isThroughObject(getX(), getY(), getMapId(), dir);
                boolean door = World.moveDoor(getX(), getY(), getMapId(), calcheading(this, loc.getX(), loc.getY()));
                if (door || !tail2) {
                    cnt++;
                }
                setDirectionMove(dir);
                setSleepTime(calcSleepTime(MOVE_SPEED));
            }
        }
    }

    private void etcWindow() {
        try {
            _glment = Robot_Hunt.getInstance().getMessage();
            Delay(30);
            for (L1PcInstance listner : L1World.getInstance().getAllPlayers()) {
                S_ChatPacket cp = new S_ChatPacket(this, _glment, Opcodes.S_MESSAGE, 3);
                listner.sendPackets(cp, true);
                setGlsaid(true);
                listner = null; // リーク防止
                cp = null;
            }
        } catch (Exception e) {
            return;
        }
    }

    private void zendleBuff() {
        GeneralThreadPool.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                // TODO 自動生成されたメソッド・スタブ
                try {
                    int[] skillt = LisBotBuffSkill4;
                    if (_random.nextInt(2) == 0) {
                        for (Integer i : skillt) {
                            L1Skills skill = SkillsTable.getInstance().getTemplate(i);
                            if (i == L1SkillId.HASTE)
                                new L1SkillUse().handleCommands(L1RobotInstance.this, i, L1RobotInstance.this.getId(),
                                        L1RobotInstance.this.getX(), L1RobotInstance.this.getY(), null, 0,
                                        L1SkillUse.TYPE_GMBUFF);
                            else
                                Broadcaster.broadcastPacket(L1RobotInstance.this,
                                        new S_SkillSound(L1RobotInstance.this.getId(), skill.getCastGfx()), true);
                        }
                        Thread.sleep(1000 + _random.nextInt(1000));
                        // 黒砂のコイン
                        // Broadcaster.broadcastPacket(L1RobotInstance.this, new
                        // S_SkillSound(L1RobotInstance.this.getId(), 4914),
                        // true);
                    } else {
                        // 黒砂のコイン
                        // Broadcaster.broadcastPacket(L1RobotInstance.this, new
                        // S_SkillSound(L1RobotInstance.this.getId(), 4914),
                        // true);
                        Thread.sleep(1000 + _random.nextInt(1000));
                        for (Integer i : skillt) {
                            L1Skills skill = SkillsTable.getInstance().getTemplate(i);
                            if (i == L1SkillId.HASTE)
                                new L1SkillUse().handleCommands(L1RobotInstance.this, i, L1RobotInstance.this.getId(),
                                        L1RobotInstance.this.getX(), L1RobotInstance.this.getY(), null, 0,
                                        L1SkillUse.TYPE_GMBUFF);
                            else
                                Broadcaster.broadcastPacket(L1RobotInstance.this,
                                        new S_SkillSound(L1RobotInstance.this.getId(), skill.getCastGfx()), true);
                        }
                    }
                } catch (Exception e) {
                }
            }

        }, 1000 + _random.nextInt(1000));
    }

    /**
     * 距離の値を抽出する。
     *
     * @param o
     * @param oo
     * @return
     */
    public int getDistance(int x, int y, int tx, int ty) {
        long dx = tx - x;
        long dy = ty - y;
        return (int) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * 距離内にある場合は真
     */
    public boolean isDistance(int x, int y, int m, int tx, int ty, int tm, int loc) {
        int distance = getDistance(x, y, tx, ty);
        if (loc < distance)
            return false;
        if (m != tm)
            return false;
        return true;
    }

    public void updateban(boolean swich) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("UPDATE robots SET ban = ? WHERE name = ?");
            if (swich) {
                pstm.setInt(1, 1);
            } else {
                pstm.setInt(1, 0);
            }
            pstm.setString(2, getName());
            pstm.executeUpdate();
        } catch (SQLException e) {
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public int _step = 0;

    public void updateconnect(boolean swich) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("UPDATE robots SET connect = ?,step = ?,map = ? WHERE name = ?");
            if (swich) {
                pstm.setInt(1, 1);
                pstm.setInt(2, _step);
                pstm.setInt(3, 4);
            } else {
                pstm.setInt(1, 0);
                pstm.setInt(2, 0);
                pstm.setInt(3, 0);
            }
            pstm.setString(4, getName());
            pstm.executeUpdate();
        } catch (SQLException e) {
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void updateclan(String clanName, int clanid, String title, boolean swich) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("UPDATE robots SET clanname = ?,clanid = ?,title = ? WHERE name = ?");
            if (swich) {
                pstm.setString(1, clanName);
                pstm.setInt(2, clanid);
                pstm.setString(3, title);
            } else {
                pstm.setString(1, "");
                pstm.setInt(2, 0);
                pstm.setString(3, "");
            }
            pstm.setString(4, getName());
            pstm.executeUpdate();
        } catch (SQLException e) {
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public int calcheading(L1Object o, int x, int y) {
        return calcheading(o.getX(), o.getY(), x, y);
    }

    public synchronized int _serchCource(int x, int y) {
        int courceRange = 10;

        int i;
        int locCenter = courceRange + 1;
        int mapId = getMapId();
        int diff_x = x - locCenter;
        int diff_y = y - locCenter;
        int[] locBace = {getX() - diff_x, getY() - diff_y, 0, 0};
        int[] locNext = new int[4];
        int[] locCopy;
        int[] dirFront = new int[5];
        boolean serchMap[][] = new boolean[locCenter * 2 + 1][locCenter * 2 + 1];
        LinkedList queueSerch = new LinkedList();

        for (int j = courceRange * 2 + 1; j > 0; j--) {
            for (i = courceRange - Math.abs(locCenter - j); i >= 0; i--) {
                serchMap[j][locCenter + i] = true;
                serchMap[j][locCenter - i] = true;
            }
        }
        int[] firstCource = {2, 4, 6, 0, 1, 3, 5, 7};
        for (i = 0; i < 8; i++) {
            System.arraycopy(locBace, 0, locNext, 0, 4);
            _moveLocation(locNext, firstCource[i]);
            if (locNext[0] - locCenter == 0 && locNext[1] - locCenter == 0)
                return firstCource[i];
            if (serchMap[locNext[0]][locNext[1]]) {
                if (World.isMapdynamic(locNext[0] + diff_x, locNext[1] + diff_y, mapId) == false) {
                    locCopy = new int[4];
                    System.arraycopy(locNext, 0, locCopy, 0, 4);
                    locCopy[2] = firstCource[i];
                    locCopy[3] = firstCource[i];
                    queueSerch.add(locCopy);
                }
                serchMap[locNext[0]][locNext[1]] = false;
            }
        }
        locBace = null;
        while (queueSerch.size() > 0) {
            locBace = (int[]) queueSerch.removeFirst();
            _getFront(dirFront, locBace[2]);
            for (i = 4; i >= 0; i--) {
                System.arraycopy(locBace, 0, locNext, 0, 4);
                _moveLocation(locNext, dirFront[i]);
                if (locNext[0] - locCenter == 0 && locNext[1] - locCenter == 0)
                    return locNext[3];
                if (serchMap[locNext[0]][locNext[1]]) {
                    if (World.isMapdynamic(locNext[0] + diff_x, locNext[1] + diff_y, mapId) == false) {
                        locCopy = new int[4];
                        System.arraycopy(locNext, 0, locCopy, 0, 4);
                        locCopy[2] = dirFront[i];
                        queueSerch.add(locCopy);
                    }
                    serchMap[locNext[0]][locNext[1]] = false;
                }
            }
            locBace = null;
        }
        return -1;
    }

    private void _moveLocation(int[] ary, int d) {
        ary[0] = ary[0] + HEADING_TABLE_X[d];
        ary[1] = ary[1] + HEADING_TABLE_Y[d];
        ary[2] = d;
    }

    private void _getFront(int[] ary, int d) {
        switch (d) {
            case 1:
                ary[4] = 2;
                ary[3] = 0;
                ary[2] = 1;
                ary[1] = 3;
                ary[0] = 7;
                break;
            case 2:
                ary[4] = 2;
                ary[3] = 4;
                ary[2] = 0;
                ary[1] = 1;
                ary[0] = 3;
                break;
            case 3:
                ary[4] = 2;
                ary[3] = 4;
                ary[2] = 1;
                ary[1] = 3;
                ary[0] = 5;
                break;
            case 4:
                ary[4] = 2;
                ary[3] = 4;
                ary[2] = 6;
                ary[1] = 3;
                ary[0] = 5;
                break;
            case 5:
                ary[4] = 4;
                ary[3] = 6;
                ary[2] = 3;
                ary[1] = 5;
                ary[0] = 7;
                break;
            case 6:
                ary[4] = 4;
                ary[3] = 6;
                ary[2] = 0;
                ary[1] = 5;
                ary[0] = 7;
                break;
            case 7:
                ary[4] = 6;
                ary[3] = 0;
                ary[2] = 1;
                ary[1] = 5;
                ary[0] = 7;
                break;
            case 0:
                ary[4] = 2;
                ary[3] = 6;
                ary[2] = 0;
                ary[1] = 1;
                ary[0] = 7;
                break;
            default:
                break;
        }
    }
}