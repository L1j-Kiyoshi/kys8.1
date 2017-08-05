package l1j.server.server.model.Instance;

import static l1j.server.server.model.item.L1ItemId.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.datatables.NpcChatTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1GroundInventory;
import l1j.server.server.model.L1HateList;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Magic;
import l1j.server.server.model.L1MobGroupInfo;
import l1j.server.server.model.L1MobSkillUse;
import l1j.server.server.model.L1NpcChatTimer;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Spawn;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_ChangeShape;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_Door;
import l1j.server.server.serverpackets.S_MoveCharPacket;
import l1j.server.server.serverpackets.S_NPCPack;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1NpcChat;
import l1j.server.server.types.Point;

public class L1NpcInstance extends L1Character {
    private static final long serialVersionUID = 1L;

    public static final int MOVE_SPEED = 0;
    public static final int ATTACK_SPEED = 1;
    public static final int MAGIC_SPEED = 2;

    public static final int HIDDEN_STATUS_NONE = 0;
    public static final int HIDDEN_STATUS_SINK = 1;
    public static final int HIDDEN_STATUS_FLY = 2;

    public static final int CHAT_TIMING_APPEARANCE = 0;
    public static final int CHAT_TIMING_DEAD = 1;
    public static final int CHAT_TIMING_HIDE = 2;
    public static final int CHAT_TIMING_GAME_TIME = 3;

    public long NpcDeleteTime = 0;

    private static final long DELETE_TIME = 40000L; // 削除時間40秒

    private L1Npc _npcTemplate;
    private L1Spawn _spawn;

    private int _spawnNumber;
    private int _petcost;

    protected L1Inventory _inventory = new L1Inventory();
    private L1MobSkillUse mobSkill;
    private static Random _random = new Random(System.nanoTime());

    private boolean firstFound = true;

    private static int courceRange = 30;
    private int _drainedMana = 0;

    private boolean _rest = false;
    private boolean _isResurrect;

    private int _randomMoveDistance = 0;
    private int _randomMoveDirection = 0;

    private boolean _aiRunning = false;
    private boolean _actived = false;
    private boolean _firstAttack = false;
    private int _sleep_time;
    protected L1HateList _hateList = new L1HateList();
    protected L1HateList _dropHateList = new L1HateList();
    protected List<L1ItemInstance> _targetItemList = new ArrayList<L1ItemInstance>();
    protected L1Character _target = null;
    protected L1ItemInstance _targetItem = null;
    protected L1Character _master = null;
    private boolean _deathProcessing = false;
    private L1MobGroupInfo _mobGroupInfo = null;
    private int _mobGroupId = 0;
    @SuppressWarnings("unused")
    private int CubeTime; //キューブの時間
    private L1PcInstance CubePc; //キューブユーザー
    private int Cube = 20;
    private int num;	/*バグベアーレース関連*/

    private DeleteTimer _deleteTask;
    private ScheduledFuture<?> _future = null;

    private Map<Integer, Integer> _digestItems;
    public boolean _digestItemRunning = false;

    private boolean _CanNotMove = false;

    public boolean isCanNotMove() {
        return _CanNotMove;
    }

    public void setCanNotMove(boolean flag) {
        _CanNotMove = flag;
    }

    private static Logger _log = Logger.getLogger(L1NpcInstance.class.getName());

    public L1NpcInstance(L1Npc template) {
        setStatus(0);
        setMoveSpeed(0);
        setDead(false);
        setRespawn(false);

        if (template != null) {
            setting_template(template);
        }
    }

    private String Spawn_Location;

    public String getSpawnLocation() {//ハーディンシステム
        return Spawn_Location;
    }

    public void setSpawnLocation(String st) {
        Spawn_Location = st;
    }

    private double calcRandomVal(int seed, int ranval, double rate) {
        return rate * (ranval - seed);
    }

    protected void setting_template(L1Npc template) {
        _npcTemplate = template;
        double rate = 0;
        double diff = 0;

        setName(template.get_name());
        setNameId(template.get_nameid());

        int randomlevel = 0;
        int level = template.get_level();
        if (template.get_randomlevel() != 0) {
            randomlevel = _random.nextInt(template.get_randomlevel() - level + 1);
            diff = template.get_randomlevel() - level;
            rate = randomlevel / diff;
            randomlevel += template.get_level();
            level = randomlevel;
        }
        setLevel(level);

        int hp = template.get_hp();
        if (template.get_randomhp() != 0) {
            hp = (int) (hp + calcRandomVal(hp, template.get_randomhp(), rate));
        }
        setMaxHp(hp);
        setCurrentHp(hp);

        int mp = template.get_mp();
        if (template.get_randommp() != 0) {
            mp = (int) (mp + calcRandomVal(mp, template.get_randommp(), rate));
        }
        setMaxMp(mp);
        setCurrentMp(mp);

        int ac = template.get_ac();
        if (template.get_randomac() != 0) {
            ac = (int) (ac + calcRandomVal(ac, template.get_randomac(), rate));
        }
        this.ac.setAc(ac);

        if (template.get_randomlevel() == 0) {
            ability.setStr(template.get_str());
            ability.setCon(template.get_con());
            ability.setDex(template.get_dex());
            ability.setInt(template.get_int());
            ability.setWis(template.get_wis());
            resistance.setBaseMr(template.get_mr());
        } else {
            ability.setStr((byte) Math.min(template.get_str() + diff, 127));
            ability.setCon((byte) Math.min(template.get_con() + diff, 127));
            ability.setDex((byte) Math.min(template.get_dex() + diff, 127));
            ability.setInt((byte) Math.min(template.get_int() + diff, 127));
            ability.setWis((byte) Math.min(template.get_wis() + diff, 127));
            resistance.setBaseMr((byte) Math.min(template.get_mr() + diff, 127));

            addHitup((int) diff * 2);
            addDmgup((int) diff * 2);
        }

        setPassispeed(template.get_passispeed());
        setAtkspeed(template.get_atkspeed());
        setAgro(template.is_agro());
        setAgrocoi(template.is_agrocoi());
        setAgrososc(template.is_agrososc());
        setTempCharGfx(template.get_gfxid());
        setGfxId(template.get_gfxid());

        if (template.get_randomexp() == 0) {
            setExp(template.get_exp());
        } else {
            setExp(template.get_randomexp() + randomlevel);
        }

        int lawful = template.get_lawful();
        if (template.get_randomlawful() != 0) {
            lawful = (int) (lawful + calcRandomVal(lawful, template.get_randomlawful(), rate));
        }
        setLawful(lawful);
        setTempLawful(lawful);

        setPickupItem(template.is_picupitem());
        if (template.is_bravespeed()) {
            setBraveSpeed(1);
        } else {
            setBraveSpeed(0);
        }
        if (template.get_digestitem() > 0) {
            _digestItems = new HashMap<Integer, Integer>();
        }
        setKarma(template.getKarma());
        setLightSize(template.getLightSize());

        mobSkill = new L1MobSkillUse(this);
    }

    class NpcAI implements Runnable {
        public void start() {
            setAiRunning(true);
            GeneralThreadPool.getInstance().schedule(NpcAI.this, 0);
        }

        private void stop() {
            mobSkill.resetAllSkillUseCount();
            GeneralThreadPool.getInstance().schedule(new DeathSyncTimer(), 0);
        }

        private void schedule(int delay) {
            GeneralThreadPool.getInstance().schedule(NpcAI.this, delay);
        }

        @Override
        public void run() {
            try {
                if (notContinued()) {
                    stop();
                    return;
                }
                if (0 < _paralysisTime) {
                    schedule(_paralysisTime);
                    _paralysisTime = 0;
                    setParalyzed(false);
                    return;
                } else if (isParalyzed() || isSleeped()) {
                    schedule(200);
                    return;
                }

                if (!AIProcess()) {
                    schedule(getSleepTime());
                    return;
                }
                stop();
            } catch (Exception e) {
                System.out.println("NPC ID : " + getNpcTemplate().get_npcId());
                _log.log(Level.WARNING, "NpcAIに例外が発生しました。", e);
            }
        }

        private boolean notContinued() {
            return _destroyed || isDead() || getCurrentHp() <= 0
                    || getHiddenStatus() != HIDDEN_STATUS_NONE;
        }
    }

    protected void startAI() {
        new NpcAI().start();
    }

    class DeathSyncTimer implements Runnable {
        private void schedule(int delay) {
            GeneralThreadPool.getInstance().schedule(DeathSyncTimer.this, delay);
        }

        @Override
        public void run() {
            if (isDeathProcessing()) {
                schedule(getSleepTime());
                return;
            }
            allTargetClear();
            setAiRunning(false);
        }
    }

    public void monsterTeleport() {
        int lvl = this.getLevel();
        if (lvl > 50) {// 50レベル以上のモンスターにのみ適用
            if (this instanceof L1MonsterInstance) {
                if (getLocation().getTileLineDistance(new Point(getHomeX(), getHomeY())) > 30) {
                    teleport(getHomeX(), getHomeY(), getHeading());
                }
            }
        }
    }


    private boolean AIProcess() {
        if (checkCondition()) {
            return false;
        }

        if (this instanceof L1MerchantInstance) {
            if (this.getNpcId() == 5095) { //砂嵐
                for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                    L1Location newLocation = pc.getLocation().randomLocation(30, true);
                    int newX = newLocation.getX();
                    int newY = newLocation.getY();
                    short mapId = (short) newLocation.getMapId();
                    if (pc.getLocation().getTileLineDistance(new Point(this.getLocation())) < 2 && !pc.isDead()) {
                        if (pc != null) {
                            new L1Teleport().teleport(pc, newX, newY, mapId, pc.getHeading(), true);
                        }
                    }
                }
            }
        }
        /** ハイネフィールドトラップ**/
        if (this instanceof L1MerchantInstance) {
            if (this.getNpcId() == 7210040) {
                for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                    if (pc.getLocation().getTileLineDistance(new Point(this.getLocation())) < 2 && !pc.isDead()) {
                        if (pc != null) {
                            Random random1 = new Random();
                            int chance = random1.nextInt(100) + 1;
                            if (chance < 40) {
                                new L1Teleport().teleport(pc, 33445, 33130, (short) 4, pc.getHeading(), true);
                            } else if (chance < 60) {
                                new L1Teleport().teleport(pc, 33428, 33244, (short) 4, pc.getHeading(), true);
                            } else if (chance < 70) {
                                new L1Teleport().teleport(pc, 33474, 33165, (short) 4, pc.getHeading(), true);
                            } else if (chance < 80) {
                                new L1Teleport().teleport(pc, 33495, 33197, (short) 4, pc.getHeading(), true);
                            }
                        }
                    }
                }
            }
        }

        setSleepTime(300);

        checkTarget();
        monsterTeleport();
        if (_target == null && _master == null) {
            searchTarget();
        }

        onItemUse();

        if (_target == null) {
            checkTargetItem();
            if (isPickupItem() && _targetItem == null) {
                searchTargetItem();
            }

            if (_targetItem == null) {
                if (noTarget()) {
                    return true;
                }
            } else {
                // onTargetItem();
                L1Inventory groundInventory = L1World.getInstance()
                        .getInventory(_targetItem.getX(), _targetItem.getY(),
                                _targetItem.getMapId());
                if (groundInventory.checkItem(_targetItem.getItemId())) {
                    onTargetItem();
                } else {
                    _targetItemList.remove(_targetItem);
                    _targetItem = null;
                    setSleepTime(1000);
                    return false;
                }
            }
        } else {
            if (getHiddenStatus() == HIDDEN_STATUS_NONE) {
                onTarget();
            } else {
                return true;
            }
        }

        return false;
    }

    public void onItemUse() {
    }

    public void searchTarget() {
    }

    public boolean checkCondition() {
        return false;
    }

    public void checkTarget() {
        if (_target == null || _target.getMapId() != getMapId() || _target.isDead()
                || _target.getCurrentHp() <= 0
                || (_target.isInvisble() && !getNpcTemplate().is_agrocoi() && !_hateList.containsKey(_target))
                || (_target instanceof L1SummonInstance && ((L1SummonInstance) _target).isDestroyed())
                || (_target instanceof L1PetInstance && ((L1PetInstance) _target).isDestroyed())) {

            if (_target != null) {
                tagertClear();
            }

            if (!_hateList.isEmpty()) {
                _target = _hateList.getMaxHateCharacter();
                checkTarget();
            }
        }
    }

    public void checkTargetItem() {
        if (_targetItem == null || _targetItem.getMapId() != getMapId()
                || getLocation().getTileDistance(_targetItem.getLocation()) > 15) {
            if (!_targetItemList.isEmpty()) {
                _targetItem = _targetItemList.get(0);
                _targetItemList.remove(0);
                checkTargetItem();
            } else {
                _targetItem = null;
            }
        }
    }

    public void onTarget() {
        setActived(true);
        _targetItemList.clear();
        _targetItem = null;
        L1Character target = _target;
        if (_target == null)
            return;
        if (hasSkillEffect(L1SkillId.DARKNESS) && getLocation().getTileLineDistance(target.getLocation()) > 1) {
            tagertClear();
            return;
        }
        if (target == null)
            return;
        if (getAtkspeed() == 0 && getPassispeed() > 0) {
            int escapeDistance = 15;

            if (getLocation().getTileLineDistance(target.getLocation()) > escapeDistance) {
                tagertClear();
            } else {
                int dir = targetReverseDirection(target.getX(), target.getY());
                dir = checkObject(getX(), getY(), getMapId(), dir);
                setDirectionMove(dir);
                setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
            }
        } else {
            boolean isSkillUse = false;
            isSkillUse = mobSkill.skillUse(target);
            if (isSkillUse == true) {
                setSleepTime(calcSleepTime(mobSkill.getSleepTime(), MAGIC_SPEED));
                return;
            }

            if (isAttackPosition(target.getX(), target.getY(), getNpcTemplate().get_ranged())) {
                setHeading(targetDirection(target.getX(), target.getY()));
                attackTarget(target);
            } else {
                if (getPassispeed() > 0) {
                    int distance = getLocation().getTileDistance(target.getLocation());
                    if (firstFound == true && getNpcTemplate().is_teleport()
                            && distance > 3 && distance < 15) {
                        if (nearTeleport(target.getX(), target.getY()) == true) {
                            firstFound = false;
                            return;
                        }
                    }

                    if (getNpcTemplate().is_teleport()
                            && 20 > _random.nextInt(100)
                            && getCurrentMp() >= 10 && distance > 6
                            && distance < 15) {
                        if (nearTeleport(target.getX(), target.getY()) == true) {
                            return;
                        }
                    }
                    int dir = moveDirection(target.getX(), target.getY());
                    if (dir == -1) {
                        tagertClear();
                    } else if (isCanNotMove()) {
                        return;
                    } else {
                        setDirectionMove(dir);
                        setSleepTime(calcSleepTime(getPassispeed(),
                                MOVE_SPEED));
                    }
                    /*else {
                        if (onAStar(target, true)) { 	// 対象まで移動することができますので、A *アルゴリズムを働かせる
							onAStar(target, false);
						} else { 						// 対象まで移動することができないため、ランダムウォークを働かせる
							randomWalk();
						}
					}
					/*
					for (Object object : L1World.getInstance().getObject()) {
						L1DoorInstance door = (L1DoorInstance) object;
						if (door.getPassable() == L1DoorInstance.NOT_PASS) {
							getMap().setPassable(door.getLocation(), false);
							setSleepTime(calcSleepTime(getPassispeed()));
						}
					}
					 */

                } else {
                    tagertClear();
                }
            }
        }
    }

    public void die(L1Character lastAttacker) {
        setDeathProcessing(true);
        setCurrentHp(0);
        setDead(true);
        setActionStatus(ActionCodes.ACTION_Die);
        getMap().setPassable(getLocation(), true);
        Broadcaster.broadcastPacket(this, new S_DoActionGFX(getId(),
                ActionCodes.ACTION_Die), true);
        startChat(CHAT_TIMING_DEAD);
        setDeathProcessing(false);
        setExp(0);
        setKarma(0);
        setLawful(0);
        allTargetClear();
        startDeleteTimer();
    }

    public void setHate(L1Character cha, int hate) {
        if (cha != null && cha.getId() != getId()) {
            if (!isFirstAttack() && hate != 0) {
                // hate += 20;
                hate += getMaxHp() / 10;
                setFirstAttack(true);
            }

            _hateList.add(cha, hate);
            _dropHateList.add(cha, hate);
            _target = _hateList.getMaxHateCharacter();
            checkTarget();
        }
    }

    public void setLink(L1Character cha) {
    }

    public void serchLink(L1PcInstance targetPlayer, int family) {
        List<L1Object> targetKnownObjects = targetPlayer.getKnownObjects();
        L1NpcInstance npc = null;
        L1MobGroupInfo mobGroupInfo = null;
        for (Object knownObject : targetKnownObjects) {
            if (knownObject == null)
                continue;
            if (knownObject instanceof L1NpcInstance) {
                npc = (L1NpcInstance) knownObject;
                if (npc.getNpcTemplate().get_agrofamily() > 0) {
                    if (npc.getNpcTemplate().get_agrofamily() == 1) {
                        if (npc.getNpcTemplate().get_family() == family) {
                            npc.setLink(targetPlayer);
                        }
                    } else {
                        npc.setLink(targetPlayer);
                    }
                }
                mobGroupInfo = getMobGroupInfo();
                if (mobGroupInfo != null) {
                    if (getMobGroupId() != 0 && getMobGroupId() == npc.getMobGroupId()) {
                        npc.setLink(targetPlayer);
                    }
                }
            }
        }
    }

    public void attackTarget(L1Character target) {
        if (target == null)
            return;
        if (target instanceof L1PcInstance) {
            L1PcInstance player = (L1PcInstance) target;
            if (player.isTeleport()) {
                return;
            }
        } else if (target instanceof L1PetInstance) {
            L1PetInstance pet = (L1PetInstance) target;
            L1Character cha = pet.getMaster();
            if (cha instanceof L1PcInstance) {
                L1PcInstance player = (L1PcInstance) cha;
                if (player.isTeleport()) {
                    return;
                }
            }
        } else if (target instanceof L1SummonInstance) {
            L1SummonInstance summon = (L1SummonInstance) target;
            L1Character cha = summon.getMaster();
            if (cha instanceof L1PcInstance) {
                L1PcInstance player = (L1PcInstance) cha;
                if (player.isTeleport()) {
                    return;
                }
            }
        } else if (target instanceof L1SupportInstance) {
            L1SupportInstance support = (L1SupportInstance) target;
            L1Character cha = support.getMaster();
            if (cha instanceof L1PcInstance) {
                L1PcInstance player = (L1PcInstance) cha;
                if (player.isTeleport()) {
                    return;
                }
            }
        }
        if (this instanceof L1PetInstance) {
            L1PetInstance pet = (L1PetInstance) this;
            L1Character cha = pet.getMaster();
            if (cha instanceof L1PcInstance) {
                L1PcInstance player = (L1PcInstance) cha;
                if (player.isTeleport()) {
                    return;
                }
            }
        } else if (this instanceof L1SummonInstance) {
            L1SummonInstance summon = (L1SummonInstance) this;
            L1Character cha = summon.getMaster();
            if (cha instanceof L1PcInstance) {
                L1PcInstance player = (L1PcInstance) cha;
                if (player.isTeleport()) {
                    return;
                }
            }
        } else if (this instanceof L1SupportInstance) {
            L1SupportInstance support = (L1SupportInstance) this;
            L1Character cha = support.getMaster();
            if (cha instanceof L1PcInstance) {
                L1PcInstance player = (L1PcInstance) cha;
                if (player.isTeleport()) {
                    return;
                }
            }
        }

        if (target instanceof L1NpcInstance) {
            L1NpcInstance npc = (L1NpcInstance) target;
            if (npc.getHiddenStatus() != HIDDEN_STATUS_NONE) {
                allTargetClear();
                return;
            }
        }

        boolean isCounterBarrier = false;
        boolean isMortalBody = false;
        L1Attack attack = new L1Attack(this, target);
        if (attack.calcHit()) {
            if (target.hasSkillEffect(L1SkillId.COUNTER_BARRIER)) {
                L1Magic magic = new L1Magic(target, this);
                boolean isProbability = magic
                        .calcProbabilityMagic(L1SkillId.COUNTER_BARRIER);
                boolean isShortDistance = attack.isShortDistance();
                if (isProbability && isShortDistance) {
                    isCounterBarrier = true;
                }
            } else if (target.hasSkillEffect(L1SkillId.MORTAL_BODY)) {
                L1Magic magic = new L1Magic(target, this);
                boolean isProbability = magic
                        .calcProbabilityMagic(L1SkillId.MORTAL_BODY);
                boolean isShortDistance = attack.isShortDistance();
                if (isProbability && isShortDistance) {
                    isMortalBody = true;
                }
            }
            if (!isCounterBarrier && !isMortalBody) {
                attack.calcDamage();

                if (target instanceof L1PcInstance) {
                    applySpecialEnchant((L1PcInstance) target);
                }
            }
        }
        if (isCounterBarrier) {
            attack.actionCounterBarrier();
            attack.commitCounterBarrier();
        } else if (isMortalBody) {
            attack.calcDamage();
            attack.actionMortalBody();
            attack.commitMortalBody();
            attack.commit();
        } else {
            attack.action();
            attack.commit();
        }
        setSleepTime(calcSleepTime(getAtkspeed(), ATTACK_SPEED));
    }

    private void applySpecialEnchant(L1PcInstance pc) {

        if (pc.getWeapon() == null || !pc.getWeapon().isSpecialEnchantable()) {
            return;
        }

        for (int i = 1; i <= 3; ++i) {
            int specialEnchant = pc.getWeapon().getSpecialEnchant(i);

            if (specialEnchant == 0) {
                break;
            }

            if (_random.nextInt(100) >= 1) {
                continue;
            }

            boolean success = true;

            switch (specialEnchant) {
                // ここ各性能別の処理
                case L1ItemInstance.CHAOS_SPIRIT:
                    success = false;
                    break;
                case L1ItemInstance.CORRUPT_SPIRIT:
                    new L1SkillUse().handleCommands(pc, L1SkillId.COUNTER_MAGIC, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                    break;
                case L1ItemInstance.ANTARAS_SPIRIT:
                case L1ItemInstance.BALLACAS_SPIRIT:
                case L1ItemInstance.LINDBIOR_SPIRIT:
                    success = false;
                    break;
                case L1ItemInstance.PAPURION_SPIRIT:
                    if (hasSkillEffect(L1SkillId.STATUS_BRAVE)
                            || hasSkillEffect(L1SkillId.STATUS_HASTE)
                            || hasSkillEffect(L1SkillId.HOLY_WALK)
                            || hasSkillEffect(L1SkillId.MOVING_ACCELERATION)
                            || hasSkillEffect(L1SkillId.WIND_WALK)) {
                        killSkillEffectTimer(L1SkillId.STATUS_BRAVE);
                        killSkillEffectTimer(L1SkillId.STATUS_HASTE);
                        killSkillEffectTimer(L1SkillId.HOLY_WALK);
                        killSkillEffectTimer(L1SkillId.MOVING_ACCELERATION);
                        killSkillEffectTimer(L1SkillId.WIND_WALK);
                        //sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
                        broadcastPacket(new S_SkillBrave(getId(), 0, 0));
                        setBraveSpeed(0);
                        //sendPackets(new S_SkillHaste(getId(), 0, 0));
                        broadcastPacket(new S_SkillHaste(getId(), 0, 0));
                        setMoveSpeed(0);
                    }
                    break;
                case L1ItemInstance.DEATHKNIGHT_SPIRIT:
                case L1ItemInstance.BAPPOMAT_SPIRIT:
                    success = false;
                    break;
                case L1ItemInstance.BALLOG_SPIRIT:
                    break;
                case L1ItemInstance.ARES_SPIRIT:
                    success = false;
                    break;
            }

            if (success) {
                break; //同時に2つ以上は発動しない。
            }
        }
    }

    public void searchTargetItem() {
        ArrayList<L1GroundInventory> gInventorys = new ArrayList<L1GroundInventory>();

        for (L1Object obj : L1World.getInstance().getVisibleObjects(this)) {
            if (obj == null)
                continue;
            if (obj != null && obj instanceof L1GroundInventory) {
                gInventorys.add((L1GroundInventory) obj);
            }
        }
        if (gInventorys.size() == 0) {
            return;
        }

        int pickupIndex = (int) (Math.random() * gInventorys.size());
        L1GroundInventory inventory = gInventorys.get(pickupIndex);
        for (L1ItemInstance item : inventory.getItems()) {
            if (item == null)
                continue;
            if (getInventory().checkAddItem(item, item.getCount())
                    == L1Inventory.OK) {
                _targetItem = item;
                _targetItemList.add(_targetItem);
            }
        }
    }

    @Override
    public void onAction(L1PcInstance pc) {
        L1Attack attack = new L1Attack(pc, this);
        attack.action();
    }

    public void searchItemFromAir() {
        ArrayList<L1GroundInventory> gInventorys =
                new ArrayList<L1GroundInventory>();

        for (L1Object obj : L1World.getInstance().getVisibleObjects(this, 4)) {
            if (obj == null)
                continue;
            if (obj != null && obj instanceof L1GroundInventory && ((L1GroundInventory) obj).getSize() > 0) {
                gInventorys.add((L1GroundInventory) obj);
            }
        }
        if (gInventorys.size() == 0) {
            return;
        }

        int pickupIndex = (int) (Math.random() * gInventorys.size());
        L1GroundInventory inventory = gInventorys.get(pickupIndex);
        for (L1ItemInstance item : inventory.getItems()) {
            if (item == null)
                continue;
            if (item.getItem().getType() == 6 // potion
                    || item.getItem().getType() == 7) { // food
                if (getHiddenStatus() == HIDDEN_STATUS_FLY) {
                    setHiddenStatus(HIDDEN_STATUS_NONE);
                    broadcastPacket(new S_DoActionGFX(getId(),
                            ActionCodes.ACTION_Movedown));
                    setStatus(0);
                    broadcastPacket(new S_NPCPack(this));
                    onNpcAI();
                    startChat(CHAT_TIMING_HIDE);
                    _targetItem = item;
                    _targetItemList.add(_targetItem);
                }
            }
        }
    }

    public static void shuffle(L1Object[] arr) {
        for (int i = arr.length - 1; i > 0; i--) {
            int t = (int) (Math.random() * i);

            L1Object tmp = arr[i];
            arr[i] = arr[t];
            arr[t] = tmp;
        }
    }

    public void onTargetItem() {
        if (getLocation().getTileLineDistance(_targetItem.getLocation()) == 0) {
            pickupTargetItem(_targetItem);
        } else {
            int dir = moveDirection(_targetItem.getX(), _targetItem.getY());
            if (dir == -1) {
                _targetItemList.remove(_targetItem);
                _targetItem = null;
            } else {
                setDirectionMove(dir);
                setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
            }
        }
    }

    public void pickupTargetItem(L1ItemInstance targetItem) {
        L1Inventory groundInventory = L1World.getInstance().getInventory(
                targetItem.getX(), targetItem.getY(), targetItem.getMapId());
        L1ItemInstance item = groundInventory.tradeItem(targetItem, targetItem
                .getCount(), getInventory());
        light.turnOnOffLight();
        onGetItem(item);
        _targetItemList.remove(_targetItem);
        _targetItem = null;
        setSleepTime(1000);
    }

    public boolean noTarget() {
        if (_master != null && _master.getMapId() == getMapId()
                && getLocation().getTileLineDistance(_master
                .getLocation()) > 2) {
            int dir = moveDirection(_master.getX(), _master.getY());
            if (dir != -1) {
                setDirectionMove(dir);
                setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
            } else {
                return true;
            }
        } else {
            if (L1World.getInstance().getRecognizePlayer(this).size() == 0) {
                return true;
            }

            if (_master == null && getPassispeed() > 0 && !isRest()) {
                //randomWalk();
                L1MobGroupInfo mobGroupInfo = getMobGroupInfo();
                if (mobGroupInfo == null
                        || mobGroupInfo != null && mobGroupInfo
                        .isLeader(this)) {
                    if (_randomMoveDistance == 0) {
                        _randomMoveDistance = _random.nextInt(5) + 1;
                        _randomMoveDirection = _random.nextInt(20);
                        if (getHomeX() != 0 && getHomeY() != 0
                                && _randomMoveDirection < 8
                                && _random.nextInt(3) == 0) {
                            _randomMoveDirection = moveDirection(getHomeX(),
                                    getHomeY());
                        }
                    } else {
                        _randomMoveDistance--;
                    }
                    int dir = checkObject(getX(), getY(), getMapId(),
                            _randomMoveDirection);
                    if (dir != -1) {
                        setDirectionMove(dir);
                        setSleepTime(calcSleepTime(getPassispeed(),
                                MOVE_SPEED));
                    }
                } else {
                    L1NpcInstance leader = mobGroupInfo.getLeader();
                    if (getLocation().getTileLineDistance(leader
                            .getLocation()) > 2) {
                        int dir = moveDirection(leader.getX(), leader.getY());
                        if (dir == -1) {
                            return true;
                        } else {
                            setDirectionMove(dir);
                            setSleepTime(calcSleepTime(getPassispeed(),
                                    MOVE_SPEED));
                        }
                    }
                }
            }
        }
        return false;
    }

    public void onFinalAction(L1PcInstance pc, String s) {
    }


    public void tagertClear() {
        if (_target != null) {
            _hateList.remove(_target);
        }
        _target = null;
    }

    public void targetRemove(L1Character target) {
        _hateList.remove(target);
        if (_target != null && _target.equals(target)) {
            _target = null;
        }
    }

    public void allTargetClear() {
        _hateList.clear();
        _dropHateList.clear();
        _target = null;
        _targetItemList.clear();
        _targetItem = null;
    }

    public void setMaster(L1Character cha) {
        _master = cha;
    }

    public L1Character getMaster() {
        return _master;
    }

    public void onNpcAI() {
    }

    public void refineItem() {
        int[] materials = null;
        int[] counts = null;
        int[] createitem = null;
        int[] createcount = null;

        if (_npcTemplate.get_npcId() == 45032) {
            if (getExp() != 0 && !_inventory.checkItem(20)) {
                materials = new int[]{40508, 40521, 40045};
                counts = new int[]{150, 3, 3};
                createitem = new int[]{20};
                createcount = new int[]{1};
                if (_inventory.checkItem(materials, counts)) {
                    for (int i = 0; i < materials.length; i++) {
                        _inventory.consumeItem(materials[i], counts[i]);
                    }
                    for (int j = 0; j < createitem.length; j++) {
                        _inventory.storeItem(createitem[j], createcount[j]);
                    }
                }
            }
            if (getExp() != 0 && !_inventory.checkItem(19)) {
                materials = new int[]{40494, 40521};
                counts = new int[]{150, 3};
                createitem = new int[]{19};
                createcount = new int[]{1};
                if (_inventory.checkItem(materials, counts)) {
                    for (int i = 0; i < materials.length; i++) {
                        _inventory.consumeItem(materials[i], counts[i]);
                    }
                    for (int j = 0; j < createitem.length; j++) {
                        _inventory.storeItem(createitem[j], createcount[j]);
                    }
                }
            }
            if (getExp() != 0 && !_inventory.checkItem(3)) {
                materials = new int[]{40494, 40521};
                counts = new int[]{50, 1};
                createitem = new int[]{3};
                createcount = new int[]{1};
                if (_inventory.checkItem(materials, counts)) {
                    for (int i = 0; i < materials.length; i++) {
                        _inventory.consumeItem(materials[i], counts[i]);
                    }
                    for (int j = 0; j < createitem.length; j++) {
                        _inventory.storeItem(createitem[j], createcount[j]);
                    }
                }
            }
            if (getExp() != 0 && !_inventory.checkItem(100)) {
                materials = new int[]{88, 40508, 40045};
                counts = new int[]{4, 80, 3};
                createitem = new int[]{100};
                createcount = new int[]{1};
                if (_inventory.checkItem(materials, counts)) {
                    for (int i = 0; i < materials.length; i++) {
                        _inventory.consumeItem(materials[i], counts[i]);
                    }
                    for (int j = 0; j < createitem.length; j++) {
                        _inventory.storeItem(createitem[j], createcount[j]);
                    }
                }
            }
            if (getExp() != 0 && !_inventory.checkItem(89)) {
                materials = new int[]{88, 40494};
                counts = new int[]{2, 80};
                createitem = new int[]{89};
                createcount = new int[]{1};
                if (_inventory.checkItem(materials, counts)) {
                    for (int i = 0; i < materials.length; i++) {
                        _inventory.consumeItem(materials[i], counts[i]);
                    }
                    L1ItemInstance item = null;
                    for (int j = 0; j < createitem.length; j++) {
                        item = _inventory.storeItem(
                                createitem[j], createcount[j]);
                        if (getNpcTemplate().get_digestitem() > 0) {
                            setDigestItem(item);
                        }
                    }
                }
            }
        } else if (_npcTemplate.get_npcId() == 81069) {
            if (getExp() != 0 && !_inventory.checkItem(40542)) {
                materials = new int[]{40032};
                counts = new int[]{1};
                createitem = new int[]{40542};
                createcount = new int[]{1};
                if (_inventory.checkItem(materials, counts)) {
                    for (int i = 0; i < materials.length; i++) {
                        _inventory.consumeItem(materials[i], counts[i]);
                    }
                    for (int j = 0; j < createitem.length; j++) {
                        _inventory.storeItem(createitem[j], createcount[j]);
                    }
                }
            }
        } else if (_npcTemplate.get_npcId() == 45166
                || _npcTemplate.get_npcId() == 45167) {
            if (getExp() != 0 && !_inventory.checkItem(40726)) {
                materials = new int[]{40725};
                counts = new int[]{1};
                createitem = new int[]{40726};
                createcount = new int[]{1};
                if (_inventory.checkItem(materials, counts)) {
                    for (int i = 0; i < materials.length; i++) {
                        _inventory.consumeItem(materials[i], counts[i]);
                    }
                    for (int j = 0; j < createitem.length; j++) {
                        _inventory.storeItem(createitem[j], createcount[j]);
                    }
                }
            }
        }
    }

    public L1HateList getHateList() {
        return _hateList;
    }

    private int _paralysisTime = 0;

    public void setParalysisTime(int ptime) {
        _paralysisTime = ptime;
    }

    public int getParalysisTime() {
        return _paralysisTime;
    }

    public final void startHpRegeneration() {
        int hprInterval = getNpcTemplate().get_hprinterval();
        int hpr = getNpcTemplate().get_hpr();
        if (!_hprRunning && hprInterval > 0 && hpr > 0) {
            _hprTimer = new HprTimer(hpr, hprInterval);
            GeneralThreadPool.getInstance().schedule(_hprTimer,
                    hprInterval);
            _hprRunning = true;
        }
    }

    public final void stopHpRegeneration() {
        if (_hprRunning) {
            _hprTimer.cancel();
            _hprRunning = false;
        }
    }

    public final void startMpRegeneration() {
        int mprInterval = getNpcTemplate().get_mprinterval();
        int mpr = getNpcTemplate().get_mpr();
        if (!_mprRunning && mprInterval > 0 && mpr > 0) {
            _mprTimer = new MprTimer(mpr, mprInterval);
            GeneralThreadPool.getInstance().schedule(_mprTimer,
                    mprInterval);
            _mprRunning = true;
        }
    }

    public final void stopMpRegeneration() {
        if (_mprRunning) {
            _mprTimer.cancel();
            _mprRunning = false;
        }
    }

    private boolean _hprRunning = false;

    private HprTimer _hprTimer;

    class HprTimer implements Runnable {
        private boolean _active;
        private long _interval;
        private int _point;

        @Override
        public void run() {
            try {
                if (!_active) {
                    return;
                }

                if ((!_destroyed && !isDead()) && (getCurrentHp() > 0 && getCurrentHp() < getMaxHp())) {
                    setCurrentHp(getCurrentHp() + _point);

                    GeneralThreadPool.getInstance().schedule(this, _interval);
                } else {
                    _hprRunning = false;
                }
            } catch (Exception e) {
                _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            }
        }

        public HprTimer(int point, long interval) {
            if (point < 1) {
                point = 1;
            }
            _point = point;
            _active = true;
            _interval = interval;
        }

        public void cancel() {
            _active = false;
        }

    }

    private boolean _mprRunning = false;

    private MprTimer _mprTimer;

    class MprTimer implements Runnable {
        private boolean _active;
        private long _interval;
        private int _point;

        @Override
        public void run() {
            try {
                if (!_active) {
                    return;
                }

                if ((!_destroyed && !isDead())
                        && (getCurrentHp() > 0 && getCurrentMp() < getMaxMp())) {
                    setCurrentMp(getCurrentMp() + _point);

                    GeneralThreadPool.getInstance().schedule(this, _interval);
                } else {
                    _mprRunning = false;
                }
            } catch (Exception e) {
                _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            }
        }

        public MprTimer(int point, long interval) {
            if (point < 1) {
                point = 1;
            }
            _point = point;
            _active = true;
            _interval = interval;
        }

        public void cancel() {
            _active = false;
        }
    }

    class DigestItemTimer implements Runnable {
        @Override
        public void run() {
            if (!_digestItemRunning) {
                _digestItemRunning = true;
            }

            Object[] keys = null;
            L1ItemInstance digestItem = null;

            if (!_destroyed && _digestItems.size() > 0) {
                keys = _digestItems.keySet().toArray();
                Integer key = null;
                Integer digestCounter = null;
                for (int i = 0; i < keys.length; i++) {
                    key = (Integer) keys[i];
                    digestCounter = _digestItems.get(key);
                    digestCounter -= 1;
                    if (digestCounter <= 0) {
                        _digestItems.remove(key);
                        digestItem = getInventory().getItem(key);
                        if (digestItem != null) {
                            getInventory().removeItem(digestItem,
                                    digestItem.getCount());
                        }
                    } else {
                        _digestItems.put(key, digestCounter);
                    }
                }
                GeneralThreadPool.getInstance().schedule(this, 1000);
            } else {
                _digestItemRunning = false;
            }
        }
    }

    private int _passispeed;
    private int _atkspeed;
    private boolean _pickupItem;

    public int getPassispeed() {
        return _passispeed;
    }

    public void setPassispeed(int i) {
        _passispeed = i;
    }

    public int getAtkspeed() {
        return _atkspeed;
    }

    public void setAtkspeed(int i) {
        _atkspeed = i;
    }

    public boolean isPickupItem() {
        return _pickupItem;
    }

    public void setPickupItem(boolean flag) {
        _pickupItem = flag;
    }

    @Override
    public L1Inventory getInventory() {
        return _inventory;
    }

    public void setInventory(L1Inventory inventory) {
        _inventory = inventory;
    }

    public L1Npc getNpcTemplate() {
        return _npcTemplate;
    }

    public int getNpcId() {
        return _npcTemplate.get_npcId();
    }

    public void setPetcost(int i) {
        _petcost = i;
    }

    public int getPetcost() {
        return _petcost;
    }

    public void setSpawn(L1Spawn spawn) {
        _spawn = spawn;
    }

    public L1Spawn getSpawn() {
        return _spawn;
    }

    public void setSpawnNumber(int number) {
        _spawnNumber = number;
    }

    public int getSpawnNumber() {
        return _spawnNumber;
    }

    public void onDecay(boolean isReuseId) {
        int id = 0;
        if (isReuseId) {
            id = getId();
        } else {
            id = 0;
        }
        _spawn.executeSpawnTask(_spawnNumber, id);
    }

    public int PASS = 1;

    @Override
    public void onPerceive(L1PcInstance perceivedFrom) {
        if (this == null || perceivedFrom == null)
            return;// ハーディンシステム
        perceivedFrom.addKnownObject(this);
        perceivedFrom.sendPackets(new S_NPCPack(this));
        if (getNpcTemplate().get_npcId() == 900168) // ボス部屋裏話ドア
            perceivedFrom.sendPackets(new S_Door(getX(), getY(), 0, PASS));// ハーディン
        // システム
        onNpcAI();
    }

    public void deleteMe() {
        _destroyed = true;

        getMap().setPassable(getLocation(), true);

        if (getInventory() != null) {
            getInventory().clearItems();
        }
        allTargetClear();
        _master = null;
        L1World.getInstance().removeVisibleObject(this);
        L1World.getInstance().removeObject(this);
        List<L1PcInstance> players = L1World.getInstance().getRecognizePlayer(this);
        if (players.size() > 0) {
            S_RemoveObject s_deleteNewObject = new S_RemoveObject(this);
            for (L1PcInstance pc : players) {
                if (pc != null) {
                    pc.removeKnownObject(this);
                    pc.sendPackets(s_deleteNewObject);
                }
            }
        }
        removeAllKnownObjects();

        L1MobGroupInfo mobGroupInfo = getMobGroupInfo();
        if (mobGroupInfo == null) {
            if (isReSpawn()) {
                onDecay(true);
            }
        } else {
            if (mobGroupInfo.removeMember(this) == 0) {
                setMobGroupInfo(null);
                if (isReSpawn()) {
                    onDecay(false);
                }
            }
        }
    }

    public void NpcDie() {
        try {
            setDeathProcessing(true);
            setCurrentHp(0);
            setDead(true);
            getMap().setPassable(getLocation(), true);
            setDeathProcessing(false);
            setExp(0);
            setKarma(0);
            setLawful(0);
            allTargetClear();
            deleteMe2();
        } catch (Exception e) {
        }
    }

    public void deleteMe2() {
        _destroyed = true;
        if (getInventory() != null) {
            getInventory().clearItems();
        }
        _master = null;
        L1World.getInstance().removeVisibleObject(this);
        L1World.getInstance().removeObject(this);
        List<L1PcInstance> players = null;
        players = L1World.getInstance().getRecognizePlayer(this);
        if (players != null && players.size() > 0) {
            S_RemoveObject s_deleteNewObject = new S_RemoveObject(this);
            for (L1PcInstance pc : players) {
                if (pc != null) {
                    pc.removeKnownObject(this);
                    pc.sendPackets(s_deleteNewObject);
                }
            }
        }
        removeAllKnownObjects();
    }

    public void ReceiveManaDamage(L1Character attacker, int damageMp) {
    }

    public void receiveCounterBarrierDamage(L1Character attacker, int damage) {
        receiveDamage(attacker, damage);
    }

    public void receiveDamage(L1Character attacker, int damage) {
    }

    public void setDigestItem(L1ItemInstance item) {
        if (item == null)
            return;
        _digestItems.put(new Integer(item.getId()), new Integer(getNpcTemplate().get_digestitem()));
        if (!_digestItemRunning) {
            DigestItemTimer digestItemTimer = new DigestItemTimer();
            GeneralThreadPool.getInstance().execute(digestItemTimer);
        }
    }

    public void onGetItem(L1ItemInstance item) {
        refineItem();
        getInventory().shuffle();
        if (getNpcTemplate().get_digestitem() > 0) {
            setDigestItem(item);
        }
    }

    public void approachPlayer(L1PcInstance pc) {
        if (pc == null)
            return;
        if (pc.hasSkillEffect(60) || pc.hasSkillEffect(97)) {
            return;
        }
        if (getHiddenStatus() == HIDDEN_STATUS_SINK) {
            if (getCurrentHp() == getMaxHp()) {
                if (pc.getLocation().getTileLineDistance(this.getLocation()) <= 2) {
                    appearOnGround(pc);
                }
            }
        } else if (getHiddenStatus() == HIDDEN_STATUS_FLY) {
            if (getCurrentHp() == getMaxHp()) {
                if (pc.getLocation().getTileLineDistance(this.getLocation()) <= 1) {
                    appearOnGround(pc);
                }
            } else {
                //if (getNpcTemplate().get_npcId() != 45681) {
                searchItemFromAir();
                //}
            }
        }

    }

    public void appearOnGround(L1PcInstance pc) {
        if (pc == null)
            return;
        if (getHiddenStatus() == HIDDEN_STATUS_SINK) {
            setHiddenStatus(HIDDEN_STATUS_NONE);
            broadcastPacket(new S_DoActionGFX(getId(), ActionCodes.ACTION_Appear));
            setStatus(0);
            broadcastPacket(new S_NPCPack(this));
            if (!pc.hasSkillEffect(60) && !pc.hasSkillEffect(97) && !pc.isGm()) {
                _hateList.add(pc, 0);
                _target = pc;
            }
            onNpcAI();
        } else if (getHiddenStatus() == HIDDEN_STATUS_FLY) {
            setHiddenStatus(HIDDEN_STATUS_NONE);
            broadcastPacket(new S_DoActionGFX(getId(), ActionCodes.ACTION_Movedown));
            setStatus(0);
            broadcastPacket(new S_NPCPack(this));
            if (!pc.hasSkillEffect(60) && !pc.hasSkillEffect(97) && !pc.isGm()) {
                _hateList.add(pc, 0);
                _target = pc;
            }
            onNpcAI();
            startChat(CHAT_TIMING_HIDE);
        }
    }

    public void setDirectionMove(int dir) {
        if (dir >= 0) {
            int nx = 0;
            int ny = 0;

            switch (dir) {
                case 1:
                    nx = 1;
                    ny = -1;
                    setHeading(1);
                    break;
                case 2:
                    nx = 1;
                    ny = 0;
                    setHeading(2);
                    break;
                case 3:
                    nx = 1;
                    ny = 1;
                    setHeading(3);
                    break;
                case 4:
                    nx = 0;
                    ny = 1;
                    setHeading(4);
                    break;
                case 5:
                    nx = -1;
                    ny = 1;
                    setHeading(5);
                    break;
                case 6:
                    nx = -1;
                    ny = 0;
                    setHeading(6);
                    break;
                case 7:
                    nx = -1;
                    ny = -1;
                    setHeading(7);
                    break;
                case 0:
                    nx = 0;
                    ny = -1;
                    setHeading(0);
                    break;
                default:
                    break;
            }

            getMap().setPassable(getLocation(), true);

            int nnx = getX() + nx;
            int nny = getY() + ny;
            setX(nnx);
            setY(nny);

            getMap().setPassable(getLocation(), false);

            broadcastPacket(new S_MoveCharPacket(this));

            if (getMovementDistance() > 0) {
                if (this instanceof L1GuardInstance
                        || this instanceof L1CastleGuardInstance
                        || this instanceof L1MerchantInstance
                        || this instanceof L1MonsterInstance) {
                    if (getLocation().getLineDistance(
                            new Point(getHomeX(), getHomeY())) > getMovementDistance()) {
                        teleport(getHomeX(), getHomeY(), getHeading());
                    }
                }
            }
            if (getNpcTemplate().get_npcId() >= 45912
                    && getNpcTemplate().get_npcId() <= 45916) {
                if (getX() >= 32591 && getX() <= 32644
                        && getY() >= 32643 && getY() <= 32688
                        && getMapId() == 4) {
                    teleport(getHomeX(), getHomeY(), getHeading());
                }
            }
        }
    }

    public int moveDirection(int x, int y) {
        return moveDirection(x, y, getLocation().getLineDistance(new Point(x, y)));
    }

    public int moveDirection(int x, int y, double d) {
        int dir = 0;
        if (hasSkillEffect(40) == true && d >= 2D) {
            return -1;
        } else if (d > 30D) {
            return -1;
        } else if (d > courceRange) {
            dir = targetDirection(x, y);
            dir = checkObject(getX(), getY(), getMapId(), dir);
        } else {
            dir = _serchCource(x, y);
            if (dir == -1) {
                dir = targetDirection(x, y);
                if (!isExsistCharacterBetweenTarget(dir)) {
                    dir = checkObject(getX(), getY(), getMapId(), dir);
                }
            }
        }
        return dir;
    }

    private boolean isExsistCharacterBetweenTarget(int dir) {
        if (!(this instanceof L1MonsterInstance)) {
            return false;
        }
        if (_target == null) {
            return false;
        }

        int locX = getX();
        int locY = getY();
        int targetX = locX;
        int targetY = locY;

        switch (dir) {
            case 1:
                targetX = locX + 1;
                targetY = locY - 1;
                break;
            case 2:
                targetX = locX + 1;
                break;
            case 3:
                targetX = locX + 1;
                targetY = locY + 1;
                break;
            case 4:
                targetY = locY + 1;
                break;
            case 5:
                targetX = locX - 1;
                targetY = locY + 1;
                break;
            case 6:
                targetX = locX - 1;
                break;
            case 7:
                targetX = locX - 1;
                targetY = locY - 1;
                break;
            case 0:
                targetY = locY - 1;
                break;
            default:
                break;
        }
        L1Character cha = null;
        L1PcInstance pc = null;

        for (L1Object object : L1World.getInstance().getVisibleObjects(this, 1)) {
            if (object == null) continue;
            if (object instanceof L1PcInstance || object instanceof L1SummonInstance || object instanceof L1PetInstance) {
                cha = (L1Character) object;
                if (cha.getX() == targetX && cha.getY() == targetY && cha.getMapId() == getMapId()) {
                    if (object instanceof L1PcInstance) {
                        pc = (L1PcInstance) object;
                        if (pc.isGhost()) {
                            continue;
                        }
                    }
                    _hateList.add(cha, 0);
                    _target = cha;
                    return true;
                }
            }
        }
        return false;
    }

    public int targetReverseDirection(int tx, int ty) {
        int dir = targetDirection(tx, ty);
        dir += 4;
        if (dir > 7) {
            dir -= 8;
        }
        return dir;
    }

    public static int checkObject(int x, int y, short m, int d) {
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

    private int _serchCource(int x, int y) {
        int i;
        int locCenter = courceRange + 1;
        int diff_x = x - locCenter;
        int diff_y = y - locCenter;
        int[] locBace = {getX() - diff_x, getY() - diff_y, 0, 0};
        int[] locNext = new int[4];
        int[] locCopy;
        int[] dirFront = new int[5];
        boolean serchMap[][] = new boolean[locCenter * 2 + 1][locCenter * 2 + 1];
        LinkedList<int[]> queueSerch = new LinkedList<int[]>();

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
            if (locNext[0] - locCenter == 0 && locNext[1] - locCenter == 0) {
                return firstCource[i];
            }
            if (serchMap[locNext[0]][locNext[1]]) {
                int tmpX = locNext[0] + diff_x;
                int tmpY = locNext[1] + diff_y;
                boolean found = false;
                switch (i) {
                    case 0:
                        found = getMap().isPassable(tmpX, tmpY + 1, i);
                        break;
                    case 1:
                        found = getMap().isPassable(tmpX - 1, tmpY + 1, i);
                        break;
                    case 2:
                        found = getMap().isPassable(tmpX - 1, tmpY, i);
                        break;
                    case 3:
                        found = getMap().isPassable(tmpX - 1, tmpY - 1, i);
                        break;
                    case 4:
                        found = getMap().isPassable(tmpX, tmpY - 1, i);
                        break;
                    case 5:
                        found = getMap().isPassable(tmpX + 1, tmpY - 1, i);
                        break;
                    case 6:
                        found = getMap().isPassable(tmpX + 1, tmpY, i);
                        break;
                    case 7:
                        found = getMap().isPassable(tmpX + 1, tmpY + 1, i);
                        break;
                    default:
                        break;
                }
                if (found) {
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
            locBace = queueSerch.removeFirst();
            _getFront(dirFront, locBace[2]);
            for (i = 4; i >= 0; i--) {
                System.arraycopy(locBace, 0, locNext, 0, 4);
                _moveLocation(locNext, dirFront[i]);
                if (locNext[0] - locCenter == 0 && locNext[1] - locCenter == 0) {
                    return locNext[3];
                }
                if (serchMap[locNext[0]][locNext[1]]) {
                    int tmpX = locNext[0] + diff_x;
                    int tmpY = locNext[1] + diff_y;
                    boolean found = false;
                    if (i == 0) {
                        found = getMap().isPassable(tmpX, tmpY + 1, i);
                    } else if (i == 1) {
                        found = getMap().isPassable(tmpX - 1, tmpY + 1, i);
                    } else if (i == 2) {
                        found = getMap().isPassable(tmpX - 1, tmpY, i);
                    } else if (i == 3) {
                        found = getMap().isPassable(tmpX - 1, tmpY - 1, i);
                    } else if (i == 4) {
                        found = getMap().isPassable(tmpX, tmpY - 1, i);
                    }
                    if (found) {
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
        switch (d) {
            case 1:
                ary[0] = ary[0] + 1;
                ary[1] = ary[1] - 1;
                break;
            case 2:
                ary[0] = ary[0] + 1;
                break;
            case 3:
                ary[0] = ary[0] + 1;
                ary[1] = ary[1] + 1;
                break;
            case 4:
                ary[1] = ary[1] + 1;
                break;
            case 5:
                ary[0] = ary[0] - 1;
                ary[1] = ary[1] + 1;
                break;
            case 6:
                ary[0] = ary[0] - 1;
                break;
            case 7:
                ary[0] = ary[0] - 1;
                ary[1] = ary[1] - 1;
                break;
            case 0:
                ary[1] = ary[1] - 1;
                break;
            default:
                break;
        }
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


    private void useHealPotion(int healHp, int effectId) {
        broadcastPacket(new S_SkillSound(getId(), effectId));
        if (this.hasSkillEffect(L1SkillId.POLLUTE_WATER)) {
            healHp /= 2;
        }
        if (this instanceof L1PetInstance) {
            ((L1PetInstance) this).setCurrentHp(getCurrentHp() + healHp);
        } else if (this instanceof L1SummonInstance) {
            ((L1SummonInstance) this).setCurrentHp(getCurrentHp() + healHp);
        } else if (this instanceof L1SupportInstance) {
            ((L1SupportInstance) this).setCurrentHp(getCurrentHp() + healHp);
        } else {
            setCurrentHp(getCurrentHp() + healHp);
        }
    }

    public void useHastePotion(int time) {
        broadcastPacket(new S_SkillHaste(getId(), 1, time));
        broadcastPacket(new S_SkillSound(getId(), 191));
        setMoveSpeed(1);
        setSkillEffect(L1SkillId.STATUS_HASTE, time * 1000);
    }

    public static final int USEITEM_HEAL = 0;
    public static final int USEITEM_HASTE = 1;
    public static int[] healPotions = {POTION_OF_GREATER_HEALING, POTION_OF_EXTRA_HEALING, POTION_OF_HEALING};
    public static int[] haestPotions = {B_POTION_OF_GREATER_HASTE_SELF, POTION_OF_GREATER_HASTE_SELF,
            B_POTION_OF_HASTE_SELF, POTION_OF_HASTE_SELF};

    public void useItem(int type, int chance) {
        if (hasSkillEffect(71)) {
            return;
        }

        Random random = new Random();
        if (random.nextInt(100) > chance) {
            return;
        }

        if (type == USEITEM_HEAL) {
            if (getInventory().consumeItem(POTION_OF_GREATER_HEALING, 1)) {
                useHealPotion(75, 197);
            } else if (getInventory().consumeItem(POTION_OF_EXTRA_HEALING, 1)) {
                useHealPotion(45, 194);
            } else if (getInventory().consumeItem(POTION_OF_HEALING, 1)) {
                useHealPotion(15, 189);
            }
        } else if (type == USEITEM_HASTE) {
            if (hasSkillEffect(1001)) {
                return;
            }

            if (getInventory().consumeItem(B_POTION_OF_GREATER_HASTE_SELF, 1)) {
                useHastePotion(2100);
            } else if (getInventory().consumeItem(POTION_OF_GREATER_HASTE_SELF,
                    1)) {
                useHastePotion(1800);
            } else if (getInventory().consumeItem(B_POTION_OF_HASTE_SELF, 1)) {
                useHastePotion(350);
            } else if (getInventory().consumeItem(POTION_OF_HASTE_SELF, 1)) {
                useHastePotion(300);
            }
        }
    }

    public boolean nearTeleport(int nx, int ny) {
        int rdir = _random.nextInt(8);
        int dir;
        for (int i = 0; i < 8; i++) {
            dir = rdir + i;
            if (dir > 7) {
                dir -= 8;
            }
            switch (dir) {
                case 1:
                    nx++;
                    ny--;
                    break;
                case 2:
                    nx++;
                    break;
                case 3:
                    nx++;
                    ny++;
                    break;
                case 4:
                    ny++;
                    break;
                case 5:
                    nx--;
                    ny++;
                    break;
                case 6:
                    nx--;
                    break;
                case 7:
                    nx--;
                    ny--;
                    break;
                case 0:
                    ny--;
                    break;
                default:
                    break;
            }
            if (getMap().isPassable(nx, ny)) {
                dir += 4;
                if (dir > 7) {
                    dir -= 8;
                }
                teleport(nx, ny, dir);
                setCurrentMp(getCurrentMp() - 10);
                return true;
            }
        }
        return false;
    }

    public void teleport(int nx, int ny, int dir, int mapid) {
        getMap().setPassable(getLocation(), true);

        setX(nx);
        setY(ny);
        setHeading(dir);

        getMap().setPassable(getLocation(), false);
    }

    public void teleport(int nx, int ny, int dir) {
        for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
            if (pc == null) continue;
            pc.sendPackets(new S_SkillSound(getId(), 169));
            pc.sendPackets(new S_RemoveObject(this));
            pc.removeKnownObject(this);
        }
        getMap().setPassable(getLocation(), true);

        setX(nx);
        setY(ny);
        setHeading(dir);

        getMap().setPassable(getLocation(), false);
    }

    // ----------From L1Character-------------
    private String _nameId;
    private boolean _Agro;
    private boolean _Agrocoi;
    private boolean _Agrososc;
    private int _homeX;
    private int _homeY;
    private boolean _reSpawn;
    private int _lightSize;
    private boolean _weaponBreaked;
    private int _hiddenStatus;
    private int _movementDistance = 0;
    private int _tempLawful = 0;

    public String getNameId() {
        return _nameId;
    }

    public void setNameId(String s) {
        _nameId = s;
    }

    public boolean isAgro() {
        return _Agro;
    }

    public void setAgro(boolean flag) {
        _Agro = flag;
    }

    public boolean isAgrocoi() {
        return _Agrocoi;
    }

    public void setAgrocoi(boolean flag) {
        _Agrocoi = flag;
    }

    public boolean isAgrososc() {
        return _Agrososc;
    }

    public void setAgrososc(boolean flag) {
        _Agrososc = flag;
    }

    public int getHomeX() {
        return _homeX;
    }

    public void setHomeX(int i) {
        _homeX = i;
    }

    public int getHomeY() {
        return _homeY;
    }

    public void setHomeY(int i) {
        _homeY = i;
    }

    public boolean isReSpawn() {
        return _reSpawn;
    }

    public void setRespawn(boolean flag) {
        _reSpawn = flag;
    }

    public int getLightSize() {
        return _lightSize;
    }

    public void setLightSize(int i) {
        _lightSize = i;
    }

    public boolean isWeaponBreaked() {
        return _weaponBreaked;
    }

    public void setWeaponBreaked(boolean flag) {
        _weaponBreaked = flag;
    }

    public int getHiddenStatus() {
        return _hiddenStatus;
    }

    public void setHiddenStatus(int i) {
        _hiddenStatus = i;
    }

    public int getMovementDistance() {
        return _movementDistance;
    }

    public void setMovementDistance(int i) {
        _movementDistance = i;
    }

    public int getTempLawful() {
        return _tempLawful;
    }

    public void setTempLawful(int i) {
        _tempLawful = i;
    }

    protected int calcSleepTime(int sleepTime, int type) {
        switch (getMoveSpeed()) {
            case 0:
                break;
            case 1:
                sleepTime -= (sleepTime * 0.25);
                break;
            case 2:
                sleepTime *= 2;
                break;
        }
        if (getBraveSpeed() == 1) {
            sleepTime -= (sleepTime * 0.25);
        }
        if (hasSkillEffect(L1SkillId.WIND_SHACKLE)) {
            if (type == ATTACK_SPEED || type == MAGIC_SPEED) {
                sleepTime += (sleepTime * 0.25);
            }
        }
        return sleepTime;
    }

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

    protected void setFirstAttack(boolean firstAttack) {
        _firstAttack = firstAttack;
    }

    protected boolean isFirstAttack() {
        return _firstAttack;
    }

    protected void setSleepTime(int sleep_time) {
        _sleep_time = sleep_time;
    }

    protected int getSleepTime() {
        return _sleep_time;
    }

    protected void setDeathProcessing(boolean deathProcessing) {
        _deathProcessing = deathProcessing;
    }

    protected boolean isDeathProcessing() {
        return _deathProcessing;
    }

    public int drainMana(int drain) {
        if (_drainedMana >= Config.MANA_DRAIN_LIMIT_PER_NPC) {
            return 0;
        }
        int result = Math.min(drain, getCurrentMp());
        if (_drainedMana + result > Config.MANA_DRAIN_LIMIT_PER_NPC) {
            result = Config.MANA_DRAIN_LIMIT_PER_NPC - _drainedMana;
        }
        _drainedMana += result;
        return result;
    }

    public boolean _destroyed = false;

    public boolean isDestroyed() {
        return _destroyed;
    }

    protected void transform(int transformId) {
        stopHpRegeneration();
        stopMpRegeneration();
        int transformGfxId = getNpcTemplate().getTransformGfxId();
        if (transformGfxId != 0) {
            broadcastPacket(new S_SkillSound(getId(), transformGfxId));
        }
        L1Npc npcTemplate = NpcTable.getInstance().getTemplate(transformId);
        setting_template(npcTemplate);

        broadcastPacket(new S_ChangeShape(getId(), getTempCharGfx()));
        ArrayList<L1PcInstance> list = null;
        list = L1World.getInstance().getRecognizePlayer(this);
        for (L1PcInstance pc : list) {
            if (pc != null)
                onPerceive(pc);
        }
    }


    public void setRest(boolean _rest) {
        this._rest = _rest;
    }

    public boolean isRest() {
        return _rest;
    }

    public boolean isResurrect() {
        return _isResurrect;
    }

    public void setResurrect(boolean flag) {
        _isResurrect = flag;
    }

    @Override
    public synchronized void resurrect(int hp) {

        if (_destroyed) {
            return;
        }
        if (_deleteTask != null) {
            if (!_future.cancel(false)) {
                return;
            }
            _deleteTask = null;
            _future = null;
        }
        super.resurrect(hp);
        startHpRegeneration();
        startMpRegeneration();
        L1SkillUse skill = new L1SkillUse();
        skill.handleCommands(null, L1SkillId.CANCELLATION, getId(), getX(), getY(), null, 0, L1SkillUse.TYPE_LOGIN, this);
    }

    protected synchronized void startDeleteTimer() {
        if (_deleteTask != null) {
            return;
        }
        _deleteTask = new DeleteTimer(getId());
        _future = GeneralThreadPool.getInstance().schedule(_deleteTask, DELETE_TIME);
    }

    protected static class DeleteTimer implements Runnable {
        private int _id;

        protected DeleteTimer(int oId) {
            try {
                _id = oId;
                if (!(L1World.getInstance().findObject(_id) instanceof L1NpcInstance)) {
                    throw new IllegalArgumentException("allowed only L1NpcInstance");
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("DeleteTimerエラー：" + L1World.getInstance().findObject(_id));
            }
        }

        @Override
        public void run() {
            L1NpcInstance npc = (L1NpcInstance) L1World.getInstance().findObject(_id);
            if (npc == null || !npc.isDead() || npc._destroyed) {
                return;
            }
            try {
                npc.deleteMe();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isInMobGroup() {
        return getMobGroupInfo() != null;
    }

    public L1MobGroupInfo getMobGroupInfo() {
        return _mobGroupInfo;
    }

    public void setMobGroupInfo(L1MobGroupInfo m) {
        _mobGroupInfo = m;
    }

    public int getMobGroupId() {
        return _mobGroupId;
    }

    public void setMobGroupId(int i) {
        _mobGroupId = i;
    }

    public void startChat(int chatTiming) {
        if (chatTiming == CHAT_TIMING_APPEARANCE && this.isDead()) {
            return;
        }
        if (chatTiming == CHAT_TIMING_DEAD && !this.isDead()) {
            return;
        }
        if (chatTiming == CHAT_TIMING_HIDE && this.isDead()) {
            return;
        }
        if (chatTiming == CHAT_TIMING_GAME_TIME && this.isDead()) {
            return;
        }

        int npcId = this.getNpcTemplate().get_npcId();
        L1NpcChat npcChat = null;
        switch (chatTiming) {
            case CHAT_TIMING_APPEARANCE:
                npcChat = NpcChatTable.getInstance().getTemplateAppearance(npcId);
                break;
            case CHAT_TIMING_DEAD:
                npcChat = NpcChatTable.getInstance().getTemplateDead(npcId);
                break;
            case CHAT_TIMING_HIDE:
                npcChat = NpcChatTable.getInstance().getTemplateHide(npcId);
                break;
            case CHAT_TIMING_GAME_TIME:
                npcChat = NpcChatTable.getInstance().getTemplateGameTime(npcId);
                break;
            default:
                break;
        }
        if (npcChat == null) {
            return;
        }

        L1NpcChatTimer npcChatTimer;
        if (!npcChat.isRepeat()) {
            npcChatTimer = new L1NpcChatTimer(this, npcChat);
        } else {
            npcChatTimer = new L1NpcChatTimer(this, npcChat, npcChat.getRepeatInterval());

        }
        npcChatTimer.startChat(npcChat.getStartDelayTime());
    }

    /**
     * キューブだ
     */
    public void setCubeTime(int CubeTime) {
        this.CubeTime = CubeTime;
    }

    public void setCubePc(L1PcInstance CubePc) {
        this.CubePc = CubePc;
    }

    public L1PcInstance CubePc() {
        return CubePc;
    }

    public boolean Cube() {
        return Cube-- <= 0;
    }

    public void set_num(int num) {
        this.num = num;
    }

    public int get_num() {
        return num;
    }

    public void randomWalk() {
        tagertClear();
        int dir = checkObject(getX(), getY(), getMapId(), _random.nextInt(20));
        if (dir != -1) {
            setDirectionMove(dir);
            setSleepTime(calcSleepTime(getPassispeed()));
        }
    }

    public int calcSleepTime(int i) {
        int sleepTime = i;
        switch (getMoveState().getMoveSpeed()) {
            case 0:
                break;
            case 1:
                sleepTime -= (sleepTime * 0.25);
                break;
            case 2:
                sleepTime *= 2;
                break;
        }
        if (getMoveState().getBraveSpeed() == 1) {
            sleepTime -= (sleepTime * 0.25);
        }
        return sleepTime;
    }

    //public boolean onAStar(L1Character target, boolean check) {
    //	return onAStar(target.getX(), target.getY(), target.getMapId(), check);
    //}

	/*public boolean onAStar(int tx, int ty, int mapId, boolean check) {
		// check: true - パスを確認しますがするが、false  - 検出されたパスを移動させる
		pfAStar = new L1Astar();
		iPath = new int[300][2];
		// 最短経路を検索する
		nodePath = pfAStar.FindPath(this, tx, ty, mapId);
		// 現在のパスカウンタを初期化する
		iCurrentPath = 0;
		// パスのリストを作る
		while ( nodePath != null ) {
			iPath[iCurrentPath][0] = nodePath.x;
			iPath[iCurrentPath][1] = nodePath.y;
			iCurrentPath++;
			nodePath = nodePath.prev;
		}
		// パスの最大カウンターに現在のパスカウンタを代入する
		iMaxPath = iCurrentPath;
		// 確認の目的ではなく、ターゲット・パスが対象と0または1、2タイル距離である場合にのみ、移動させる
		// 対象の直接位置は移動不可（検索不可）の位置であるため、0のタイルは、することができない
		int tile = Math.max(Math.abs(iPath[0][0] - tx), Math.abs(iPath[0][1] - ty));
		if (tile < 3) {
			if (check) { // 確認用
				return true;
			} else { // 確認が容易でない場合
				iMonsterX = iPath[iCurrentPath-2][0];
				iMonsterY = iPath[iCurrentPath-2][1];
				// 移動座標に合わせてモンスターの方向を設定する
				if (getX() 			< 	iMonsterX && getY() 	> 	iMonsterY) {
					setHeading(1);
				} else if (getX() 	< 	iMonsterX && getY() 	== 	iMonsterY) {
					setHeading(2);
				} else if (getX() 	< 	iMonsterX && getY() 	< 	iMonsterY) {
					setHeading(3);
				} else if (getX() 	== 	iMonsterX && getY() 	< 	iMonsterY) {
					setHeading(4);
				} else if (getX() 	> 	iMonsterX && getY() 	< 	iMonsterY) {
					setHeading(5);
				} else if (getX() 	> 	iMonsterX && getY() 	== 	iMonsterY) {
					setHeading(6);
				} else if (getX() 	> 	iMonsterX && getY() 	> 	iMonsterY) {
					setHeading(7);
				} else if (getX() 	== 	iMonsterX && getY() 	> 	iMonsterY) {
					setHeading(0);
				}
				// 移動させる
				getMap().setPassable(getLocation(), true);
				setX(iMonsterX);
				setY(iMonsterY);
				getMap().setPassable(getLocation(), false);
				broadcastPacket(new S_MoveCharPacket(this));
				setSleepTime(calcSleepTime(getPassispeed()));
				// ノードを初期化する
				pfAStar.ResetPath();
				// テスト用のメッセージを出力する
				//System.out.println（ "パス" +（iMaxPath  -  1）+ "|" +
				//		"距離： "+ Math.max（Math.abs（getX（） -  tx）、Math.abs（getY（） -  ty）））;
			}
		}
		// 目標パスが2タイル以上の差がある場合は、ターゲットを初期化する
		// 長さが存在しないか、詰まっているからである
		else {
			tagertClear();
			return false;
		}
		return true;
	}

	private void randomWalk() {
		tagertClear();
		int dir = checkObject(getX(), getY(), getMapId(), getRnd()
				.nextInt(20));
		if (dir != -1) {
			setDirectionMove(dir);
			setSleepTime(calcSleepTime(getPassispeed()));
		}
	}
	public int calcSleepTime(int i) {
		int sleepTime = i;
		switch (getMoveSpeed()) {
		case 0: break;
		case 1: sleepTime -= (sleepTime * 0.25); break;
		case 2: sleepTime *= 2; break;
		}
		if (getBraveSpeed() == 1) {
			sleepTime -= (sleepTime * 0.25);
		}
		return sleepTime;
	}*/


}
