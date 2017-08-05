package l1j.server.server.model.Instance;

import java.util.Arrays;
import java.util.Random;

import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.IdFactory;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Magic;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PinkName;
import l1j.server.server.model.L1World;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_BlueMessage;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_HPMeter;
import l1j.server.server.serverpackets.S_Lawful;
import l1j.server.server.serverpackets.S_SupportPack;
import l1j.server.server.templates.L1Npc;

public class L1SupportInstance extends L1NpcInstance {
    private static final long serialVersionUID = 1L;

    public static final int SUPPORTTYPE_DWARF = 0;
    public static final int SUPPORTYPE_SUCCUBUS = 1;
    public static final int SUPPORT_TIME = 1800000;

    private static Random _random = new Random(System.nanoTime());
    private int _supportType;
    private int _itemObjId;
    private boolean _isPinkName = false;
    public L1PcInstance _supportMaster;

    // ターゲットが存在しない場合の処理
    @Override
    public boolean noTarget() {
        if (_master.isDead()) {
            deleteSupport();
            return true;
        } else if (_master != null && _master.getMapId() == getMapId()) {
            if (getLocation().getTileLineDistance(_master.getLocation()) > 2) {
                int dir = moveDirection(_master.getX(), _master.getY());
                if (dir == -1) {
                    if (!isAiRunning()) {
                        startAI();
                    }
                    return true;
                } else {
                    setDirectionMove(dir);
                    setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
                }
            }
        } else {
            deleteSupport();
            return true;
        }
        return false;
    }

    // 時間計測用
    class SupportTimer implements Runnable {
        @Override
        public void run() {
            if (_destroyed) { // すでに破棄されていないかチェック
                return;
            }
            deleteSupport();
        }
    }

    public L1SupportInstance(L1Npc template, L1PcInstance master, int supportType,
                             int itemObjId) {
        super(template);
        setId(IdFactory.getInstance().nextId());

        _supportMaster = master;
        setSupportType(supportType);
        setItemObjId(itemObjId);
        GeneralThreadPool.getInstance().schedule(
                new SupportTimer(), SUPPORT_TIME);
        setMaster(master);
        setCurrentHp(template.get_hp());
        setCurrentMp(template.get_mp());
        setX(master.getX() + _random.nextInt(5) - 2);
        setY(master.getY() + _random.nextInt(5) - 2);
        setMap(master.getMapId());
        setHeading(5);
        setLightSize(template.getLightSize());

        L1World.getInstance().storeObject(this);
        L1World.getInstance().addVisibleObject(this);
        for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
            onPerceive(pc);
        }
        master.addSupport(this);

        if (!isAiRunning()) {
            startAI();
        }
        if (isSupport()) {
            master.startHelpBySupport();
        }
    }

    public void deleteSupport() {
        ((L1PcInstance) _master).setSupporting(false);
        if (isSupport()) {
            ((L1PcInstance) _master).stopHelpBySupport();
        }
        _master.getSupportList().remove(getId());
        getMap().setPassable(getLocation(), true);
        deleteMe();
    }

    @Override
    public void onPerceive(L1PcInstance perceivedFrom) {
        perceivedFrom.addKnownObject(this);
        perceivedFrom.sendPackets(new S_SupportPack(this, perceivedFrom));
    }

    public boolean isExsistMaster() {
        boolean isExsistMaster = true;
        if (this.getMaster() != null) {
            String masterName = this.getMaster().getName();
            if (L1World.getInstance().getPlayer(masterName) == null) {
                isExsistMaster = false;
            }
        }
        return isExsistMaster;
    }

    @Override
    public void onAction(L1PcInstance attacker) {
        if (attacker == null) {
            return;
        }

        if (getZoneType() == 1 || attacker.getZoneType() == 1) {
            L1Attack attack_mortion = new L1Attack(attacker, this);
            attack_mortion.action();
            return;
        }

        if (getCurrentHp() > 0 && !isDead()) {
            attacker.delInvis();

            boolean isCounterBarrier = false;
            boolean isMortalBody = false;
            L1Attack attack = new L1Attack(attacker, this);
            if (attack.calcHit()) {
                L1Magic magic = null;
                if (hasSkillEffect(L1SkillId.COUNTER_BARRIER)) {
                    magic = new L1Magic(this, attacker);
                    boolean isProbability = magic
                            .calcProbabilityMagic(L1SkillId.COUNTER_BARRIER);
                    boolean isShortDistance = attack.isShortDistance();
                    if (isProbability && isShortDistance) {
                        isCounterBarrier = true;
                    }
                } else if (hasSkillEffect(L1SkillId.MORTAL_BODY)) {
                    magic = new L1Magic(this, attacker);
                    boolean isProbability = magic
                            .calcProbabilityMagic(L1SkillId.MORTAL_BODY);
                    boolean isShortDistance = attack.isShortDistance();
                    if (isProbability && isShortDistance) {
                        isMortalBody = true;
                    }
                }
                if (!isCounterBarrier || !isMortalBody) {
                    attacker.setPetTarget(this);
                    attack.calcDamage();
                    attack.calcStaffOfMana();
                    /** ゾウのストーンゴーレム **/
                    attack.calcDrainOfMana();
                    /** ゾウのストーンゴーレム**/
                    attack.addPcPoisonAttack(attacker, this);
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
        }
    }

    public void receiveCounterBarrierDamage(L1Character attacker, int damage) {
        if (getCurrentHp() > 0 && !isDead()) {
            if (attacker != this && !knownsObject(attacker)
                    && attacker.getMapId() == this.getMapId()) {
                attacker.onPerceive(this);
            }

            if (damage > 0) {
                if (hasSkillEffect(L1SkillId.FOG_OF_SLEEPING)) {
                    removeSkillEffect(L1SkillId.FOG_OF_SLEEPING);
                } else if (hasSkillEffect(L1SkillId.PHANTASM)) {
                    removeSkillEffect(L1SkillId.PHANTASM);
                }
            }

            int newHp = getCurrentHp() - damage;
            if (newHp > getMaxHp()) {
                newHp = getMaxHp();
            }
            if (newHp <= 0) {
                death(attacker);
            }
            if (newHp > 0) {
                setCurrentHp(newHp);
            }
        } else if (!isDead()) {
            System.out.println("つつくウィザードのHP減少処理が正しく行われていない箇所があります。※あるいは最初からHP0");
            death(attacker);
        }
    }

    public void receiveDamage(L1Character attacker, int damage) {
        if (getCurrentHp() > 0 && !isDead()) {
            if (attacker != this && !knownsObject(attacker)
                    && attacker.getMapId() == this.getMapId()) {
                attacker.onPerceive(this);
            }

            if (damage > 0) {
                if (attacker instanceof L1PcInstance) {
                    L1PinkName.onAction(this, attacker);
                }
                if (attacker instanceof L1PcInstance
                        && ((L1PcInstance) attacker).isPinkName()) {
                    L1GuardInstance guard = null;
                    for (L1Object object : L1World.getInstance()
                            .getVisibleObjects(attacker)) {
                        if (object instanceof L1GuardInstance) {
                            guard = (L1GuardInstance) object;
                            guard.setTarget(((L1PcInstance) attacker));
                        }
                    }
                }
                if (hasSkillEffect(L1SkillId.FOG_OF_SLEEPING)) {
                    removeSkillEffect(L1SkillId.FOG_OF_SLEEPING);
                } else if (hasSkillEffect(L1SkillId.PHANTASM)) {
                    removeSkillEffect(L1SkillId.PHANTASM);
                }
            }

            int newHp = getCurrentHp() - damage;
            if (newHp > getMaxHp()) {
                newHp = getMaxHp();
            }
            if (newHp <= 0) {
                death(attacker);
            }
            if (newHp > 0) {
                setCurrentHp(newHp);
            }
        } else if (!isDead()) {
            System.out.println("警告：つつくウィザードのHP減少処理が正しく行われていない箇所があります。※あるいは最初からHP0");
            death(attacker);
        }
    }

    public synchronized void death(L1Character lastAttacker) {
        if (!isDead()) {
            setDead(true);
            setStatus(ActionCodes.ACTION_Die);
            setCurrentHp(0);

            getMap().setPassable(getLocation(), true);
            broadcastPacket(new S_DoActionGFX(getId(), ActionCodes.ACTION_Die));
            L1PcInstance player = null;
            if (lastAttacker instanceof L1PcInstance) {
                player = (L1PcInstance) lastAttacker;
            }
            if (player != null) {
                if (getLawful() >= 0 && isPinkName() == false) {
                    boolean isChangePkCount = false;
                    if (player.getLawful() < 30000) {
                        player.set_PKcount(player.get_PKcount() + 1);
                        isChangePkCount = true;
                        player.setLastPk();
                    }

                    int lawful;

                    if (player.getLevel() < 50) {
                        lawful = -1
                                * (int) ((Math.pow(player.getLevel(), 2) * 4));
                    } else {
                        lawful = -1
                                * (int) ((Math.pow(player.getLevel(), 3) * 0.08));
                    }
                    if ((player.getLawful() - 1000) < lawful) {
                        lawful = player.getLawful() - 1000;
                    }

                    if (lawful <= -32768) {
                        lawful = -32768;
                    }
                    player.setLawful(lawful);

                    S_Lawful s_lawful = new S_Lawful(player.getId(), player
                            .getLawful());
                    player.sendPackets(s_lawful);
                    player.broadcastPacket(s_lawful);

                    if (isChangePkCount && player.get_PKcount() >= 5
                            && player.get_PKcount() < 100) {
                        player.sendPackets(new S_BlueMessage(551, String
                                .valueOf(player.get_PKcount()), "100"));
                    } else if (isChangePkCount && player.get_PKcount() >= 100) {
                        player.beginHell(true);
                    }
                } else {
                    setPinkName(false);
                }
            }
            deleteSupport();
        }
    }

    public void setMasterTarget(L1Character target) {
        if (target != null) {
            setHate(target, 0);
            if (!isAiRunning()) {
                startAI();
            }
        }
    }

    @Override
    public void setCurrentHp(int i) {
        super.setCurrentHp(i);

        if (getMaxHp() > getCurrentHp()) {
            startHpRegeneration();
        }

        if (_supportMaster != null) {
            L1PcInstance Master = _supportMaster;
            Master.sendPackets(new S_HPMeter(this));
        }
    }

    @Override
    public void setCurrentMp(int i) {
        super.setCurrentMp(i);

        if (getMaxMp() > getCurrentMp()) {
            startMpRegeneration();
        }
    }

    @Override
    public void onItemUse() {
        if (!isActived()) {
            // 100％の確率でヘイスト一部使用
            useItem(USEITEM_HASTE, 100);
        }
        if (getCurrentHp() * 100 / getMaxHp() < 40) {
            useItem(USEITEM_HEAL, 100);
        }
    }

    @Override
    public void onGetItem(L1ItemInstance item) {
        if (getNpcTemplate().get_digestitem() > 0) {
            setDigestItem(item);
        }
        Arrays.sort(healPotions);
        Arrays.sort(haestPotions);
        if (Arrays.binarySearch(healPotions, item.getItem().getItemId()) >= 0) {
            if (getCurrentHp() != getMaxHp()) {
                useItem(USEITEM_HEAL, 100);
            }
        } else if (Arrays
                .binarySearch(haestPotions, item.getItem().getItemId()) >= 0) {
            useItem(USEITEM_HASTE, 100);
        }
    }

    public boolean isFastMovable() {
        return (hasSkillEffect(L1SkillId.HOLY_WALK)
                || hasSkillEffect(L1SkillId.MOVING_ACCELERATION)
                || hasSkillEffect(L1SkillId.WIND_WALK));
    }

    public int getSupportType() {
        return _supportType;
    }

    public void setSupportType(int i) {
        _supportType = i;
    }

    public int getItemObjId() {
        return _itemObjId;
    }

    public void setItemObjId(int i) {
        _itemObjId = i;
    }

    public boolean isSupport() {
        boolean isSupport = false;
        if (getSupportType() == SUPPORTTYPE_DWARF) {
            isSupport = true;
        }
        return isSupport;
    }

    public boolean isPinkName() {
        return _isPinkName;
    }

    public void setPinkName(boolean flag) {
        _isPinkName = flag;
    }


}
