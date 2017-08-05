package l1j.server.server.model;

import static l1j.server.server.model.skill.L1SkillId.*;

import java.util.Random;

import l1j.server.Config;
import l1j.server.server.ActionCodes;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.datatables.WeaponSkillTable;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_EffectLocation;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_UseAttackSkill;
import l1j.server.server.templates.L1Skills;

public class L1WeaponSkill {

    private static Random _random = new Random(System.nanoTime());

    private int _weaponId;

    private int _probability;

    private int _fixDamage;

    private int _randomDamage;

    private int _area;

    private int _skillId;

    private int _skillTime;

    private int _effectId;

    private int _effectTarget;

    private boolean _isArrowType;

    private int _attr;

    public L1WeaponSkill(int weaponId, int probability, int fixDamage,
                         int randomDamage, int area, int skillId, int skillTime,
                         int effectId, int effectTarget, boolean isArrowType, int attr) {
        _weaponId = weaponId;
        _probability = probability;
        _fixDamage = fixDamage;
        _randomDamage = randomDamage;
        _area = area;
        _skillId = skillId;
        _skillTime = skillTime;
        _effectId = effectId;
        _effectTarget = effectTarget;
        _isArrowType = isArrowType;
        _attr = attr;
    }

    public int getWeaponId() {
        return _weaponId;
    }

    public int getProbability() {
        return _probability;
    }

    public int getFixDamage() {
        return _fixDamage;
    }

    public int getRandomDamage() {
        return _randomDamage;
    }

    public int getArea() {
        return _area;
    }

    public int getSkillId() {
        return _skillId;
    }

    public int getSkillTime() {
        return _skillTime;
    }

    public int getEffectId() {
        return _effectId;
    }

    public int getEffectTarget() {
        return _effectTarget;
    }

    public boolean isArrowType() {
        return _isArrowType;
    }

    public int getAttr() {
        return _attr;
    }

    public static double getWeaponSkillDamage(L1PcInstance pc, L1Character cha, int weaponId) {
        L1WeaponSkill weaponSkill = WeaponSkillTable.getInstance().getTemplate(weaponId);
        if (pc == null || cha == null || weaponSkill == null) {
            return 0;
        }

        int chance = _random.nextInt(100) + 1;
        if (weaponSkill.getProbability() < chance) {
            return 0;
        }

        int skillId = weaponSkill.getSkillId();

        if (skillId == L1SkillId.SILENCE && cha instanceof L1NpcInstance) {
            L1NpcInstance npc = (L1NpcInstance) cha;

            if (npc.getNpcId() == 45684 || npc.getNpcId() == 45683 || npc.getNpcId() == 45681
                    || npc.getNpcId() == 45682 || npc.getNpcId() == 900011 || npc.getNpcId() == 900012
                    || npc.getNpcId() == 900013 || npc.getNpcId() == 900038 || npc.getNpcId() == 900039 || npc.getNpcId() == 900040
                    || npc.getNpcId() == 5096 || npc.getNpcId() == 5097 || npc.getNpcId() == 5098 || npc.getNpcId() == 5099 || npc.getNpcId() == 5100) {
                return 0;
            }
        }

        if (skillId != 0) {
            L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);
            if (skill != null && skill.getTarget().equals("buff")) {
                if (!isFreeze(cha)) {
                    if (skillId == 56) {
                        if (!cha.hasSkillEffect(skillId)) {
                            cha.addDmgup(-6);
                            cha.getAC().addAc(12);
                            if (cha instanceof L1PcInstance) {
                                L1PcInstance target = (L1PcInstance) cha;
                                target.sendPackets(new S_OwnCharAttrDef(target));
                            }
                        }
                    }
                    cha.setSkillEffect(skillId, weaponSkill.getSkillTime() * 1000);
                }
            }
        }

        int effectId = weaponSkill.getEffectId();
        if (effectId != 0) {
            int chaId = 0;
            if (weaponSkill.getEffectTarget() == 0) {
                chaId = cha.getId();
            } else {
                chaId = pc.getId();
            }
            boolean isArrowType = weaponSkill.isArrowType();
            if (!isArrowType) {
                pc.sendPackets(new S_SkillSound(chaId, effectId));
                pc.broadcastPacket(new S_SkillSound(chaId, effectId));
            } else {
                S_UseAttackSkill packet = new S_UseAttackSkill(pc, cha.getId(),
                        effectId, cha.getX(), cha.getY(), ActionCodes
                        .ACTION_Attack, false);
                pc.sendPackets(packet);
                pc.broadcastPacket(packet, cha);
            }
        }

        double damage = 0;
        int randomDamage = weaponSkill.getRandomDamage();
        if (randomDamage != 0) {
            damage = _random.nextInt(randomDamage);
        }
        damage += weaponSkill.getFixDamage();

        if (effectId == 6985) {
            damage += pc.getAbility().getTotalInt() * 3;
        } else {
            damage += pc.getAbility().getTotalInt() * 2;
        }

        int area = weaponSkill.getArea();
        if (area > 0 || area == -1) {
            L1PcInstance targetPc = null;
            L1NpcInstance targetNpc = null;
            for (L1Object object : L1World.getInstance()
                    .getVisibleObjects(cha, area)) {
                if (object == null) {
                    continue;
                }
                if (!(object instanceof L1Character)) {
                    continue;
                }
                if (object.getId() == pc.getId()) {
                    continue;
                }
                if (object.getId() == cha.getId()) {
                    continue;
                }
                if (object instanceof L1PcInstance) {
                    targetPc = (L1PcInstance) object;
                    if (targetPc.getZoneType() == 1) {
                        continue;
                    }
                }

                if (cha instanceof L1MonsterInstance) {
                    if (!(object instanceof L1MonsterInstance)) {
                        continue;
                    }
                }
                if (cha instanceof L1PcInstance
                        || cha instanceof L1SummonInstance
                        || cha instanceof L1PetInstance) {
                    if (!(object instanceof L1PcInstance
                            || object instanceof L1SummonInstance
                            || object instanceof L1PetInstance
                            || object instanceof L1MonsterInstance)) {
                        continue;
                    }
                }
                damage = calcDamageReduction((L1Character) object, damage,
                        weaponSkill.getAttr());
                if (damage <= 0) {
                    continue;
                }
                if (object instanceof L1PcInstance) {
                    targetPc = (L1PcInstance) object;
                    targetPc.sendPackets(new S_DoActionGFX(targetPc.getId(),
                            ActionCodes.ACTION_Damage));
                    targetPc.broadcastPacket(new S_DoActionGFX(targetPc.getId(),
                            ActionCodes.ACTION_Damage));
                    targetPc.receiveDamage(pc, (int) damage);
                } else if (object instanceof L1SummonInstance
                        || object instanceof L1PetInstance
                        || object instanceof L1MonsterInstance) {
                    targetNpc = (L1NpcInstance) object;
                    targetNpc.broadcastPacket(new S_DoActionGFX(targetNpc
                            .getId(), ActionCodes.ACTION_Damage));
                    targetNpc.receiveDamage(pc, (int) damage);
                }
            }
        }

        return calcDamageReduction(cha, damage, weaponSkill.getAttr());
    }

    public static int KiringkuDamage(L1PcInstance pc, L1Character cha) {
        int dmg = 0;
        int dice = 9;
        int diceCount = 5;
        int value = Config.KIRINGKU;
        int KiringkuDamage = 0;
        int charaIntelligence = 0;

        if (cha instanceof L1PcInstance) {
            L1PcInstance targetPc = (L1PcInstance) cha;
            if (targetPc.hasSkillEffect(COUNTER_MAGIC)) {
                targetPc.removeSkillEffect(COUNTER_MAGIC);
                targetPc.sendPackets(new S_PacketBox(S_PacketBox.UNLIMITED_ICON1, 393, false));
                targetPc.sendPackets(new S_SkillSound(targetPc.getId(), 10702));
                targetPc.broadcastPacket(new S_SkillSound(targetPc.getId(), 10702));
            }
        }

        for (int i = 0; i < diceCount; i++) {
            KiringkuDamage += (_random.nextInt(dice) + 1);
        }
        KiringkuDamage += value;

        int spByItem = pc.getAbility().getSp() - pc.getAbility().getTrueSp();
        charaIntelligence = pc.getAbility().getTotalInt() + spByItem - 12;
        if (charaIntelligence < 1) {
            charaIntelligence = 1;
        }
        double KiringkuCoefficientA = (1.0 + charaIntelligence * 3.0 / 32.0);

        KiringkuDamage *= KiringkuCoefficientA;

        double Mrfloor = 0;
        if (cha.getResistance().getEffectedMrBySkill() <= 100) {
            Mrfloor = Math.floor((cha.getResistance().getEffectedMrBySkill() - pc.getBaseMagicHitUp()) / 2);
        } else if (cha.getResistance().getEffectedMrBySkill() >= 100) {
            Mrfloor = Math.floor((cha.getResistance().getEffectedMrBySkill() - pc.getBaseMagicHitUp()) / 10);
        }

        double KiringkuCoefficientB = 0;
        if (cha.getResistance().getEffectedMrBySkill() <= 100) {
            KiringkuCoefficientB = 1 - 0.01 * Mrfloor;
        } else if (cha.getResistance().getEffectedMrBySkill() > 100) {
            KiringkuCoefficientB = 0.6 - 0.01 * Mrfloor;
        }

        double Kiringkufloor = Math.floor(KiringkuDamage);

        dmg += Kiringkufloor + (pc.getWeapon().getEnchantLevel() * 1.5);

        dmg *= KiringkuCoefficientB;

        if (pc.getWeapon().getItem().getItemId() == 503) {
            pc.sendPackets(new S_SkillSound(pc.getId(), 6983));
            pc.broadcastPacket(new S_SkillSound(pc.getId(), 6983));
        } else {
            pc.sendPackets(new S_SkillSound(pc.getId(), 7049));
            pc.broadcastPacket(new S_SkillSound(pc.getId(), 7049));
        }
        return dmg;
    }

    public static double DiceDagger(L1PcInstance pc,
                                    L1PcInstance targetPc, L1ItemInstance weapon) {
        double dmg = 0;
        int chance = _random.nextInt(100) + 1;
        if (3 >= chance) {
            dmg = targetPc.getCurrentHp() / 2;
            if (targetPc.getCurrentHp() - dmg < 0) {
                dmg = 0;
            }
            String msg = weapon.getLogName();
            pc.sendPackets(new S_ServerMessage(158, msg));
            pc.getInventory().removeItem(weapon, 1);
        }
        L1PinkName.onAction(targetPc, pc);
        return dmg;
    }

    public static void RedShadowDualBlade(L1PcInstance pc, L1Character cha) {
        int fettersTime = 8000;
        if (isFreeze(cha)) {
            return;
        }
        if ((_random.nextInt(100) + 1) <= 2) {
            L1EffectSpawn.getInstance().spawnEffect(81182, fettersTime, cha.getX(), cha.getY(), cha.getMapId());
            if (cha instanceof L1PcInstance) {
                L1PcInstance targetPc = (L1PcInstance) cha;
                targetPc.setSkillEffect(STATUS_FREEZE, fettersTime);
                targetPc.sendPackets(new S_SkillSound(targetPc.getId(), 4184));
                targetPc.broadcastPacket(new S_SkillSound(targetPc.getId(), 4184));
                targetPc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, true));
            } else if (cha instanceof L1MonsterInstance || cha instanceof L1SummonInstance || cha instanceof L1PetInstance) {
                L1NpcInstance npc = (L1NpcInstance) cha;
                npc.setSkillEffect(STATUS_FREEZE, fettersTime);
                npc.broadcastPacket(new S_SkillSound(npc.getId(), 4184));
                npc.setCanNotMove(true);
            }
        }
    }

    public static void ChainSword(L1PcInstance pc) { //チェーンソードダメージ。
        if (_random.nextInt(100) < 15) {
            if (pc.hasSkillEffect(L1SkillId.CHAINSWORD1)) {
                pc.killSkillEffectTimer(L1SkillId.CHAINSWORD1);
                pc.setSkillEffect(L1SkillId.CHAINSWORD2, 15 * 1000);
                pc.sendPackets(new S_PacketBox(S_PacketBox.SPOT, 2));
            } else if (pc.hasSkillEffect(L1SkillId.CHAINSWORD2)) {
                pc.killSkillEffectTimer(L1SkillId.CHAINSWORD2);
                pc.setSkillEffect(L1SkillId.CHAINSWORD3, 15 * 1000);
                pc.sendPackets(new S_PacketBox(S_PacketBox.SPOT, 3));
            } else if (pc.hasSkillEffect(L1SkillId.CHAINSWORD3)) {
            } else {
                pc.setSkillEffect(L1SkillId.CHAINSWORD1, 15 * 1000);
                pc.sendPackets(new S_PacketBox(S_PacketBox.SPOT, 1));
            }
        }
    }

    public static void ChainSword_Destroyer(L1PcInstance pc) { //チェーンソードダメージ。
        if (_random.nextInt(100) < 18) {
            if (pc.hasSkillEffect(L1SkillId.CHAINSWORD1)) {
                pc.killSkillEffectTimer(L1SkillId.CHAINSWORD1);
                pc.setSkillEffect(L1SkillId.CHAINSWORD2, 15 * 1000);
                pc.sendPackets(new S_PacketBox(S_PacketBox.SPOT, 2));
            } else if (pc.hasSkillEffect(L1SkillId.CHAINSWORD2)) {
                pc.killSkillEffectTimer(L1SkillId.CHAINSWORD2);
                pc.setSkillEffect(L1SkillId.CHAINSWORD3, 15 * 1000);
                pc.sendPackets(new S_PacketBox(S_PacketBox.SPOT, 3));
            } else if (pc.hasSkillEffect(L1SkillId.CHAINSWORD3)) {
            } else {
                pc.setSkillEffect(L1SkillId.CHAINSWORD1, 15 * 1000);
                pc.sendPackets(new S_PacketBox(S_PacketBox.SPOT, 1));
            }
        }
    }

    public static void getDiseaseWeapon(L1PcInstance pc, L1Character cha,
                                        int weaponid) {
        int chance = _random.nextInt(100) + 1;
        int skilltime = weaponid == 412003 ? 64 : 20;
        if (7 >= chance) {
            if (!cha.hasSkillEffect(56)) {
                cha.addDmgup(-6);
                cha.getAC().addAc(12);
                if (cha instanceof L1PcInstance) {
                    L1PcInstance target = (L1PcInstance) cha;
                    target.sendPackets(new S_OwnCharAttrDef(target));
                }
            }
            cha.setSkillEffect(56, skilltime * 1000);
            pc.sendPackets(new S_SkillSound(cha.getId(), 2230));
            Broadcaster
                    .broadcastPacket(pc, new S_SkillSound(cha.getId(), 2230));
            if (cha.hasSkillEffect(ERASE_MAGIC))
                cha.removeSkillEffect(ERASE_MAGIC);
        }
    }

    public static double BlazeShock(L1PcInstance pc, L1Character cha, int enchant) {
        double dmg = 0;
        int chance = _random.nextInt(100) + 1;
        int val = enchant * 1;
        if (val <= 0) {
            val = 1;
        } else
            val += 1;

        if (val >= chance) {
            int randmg = _random.nextInt(50) + 20;
            dmg = randmg;

            if (dmg < 20)
                dmg = 20;

            pc.sendPackets(new S_SkillSound(cha.getId(), 3939));
            Broadcaster.broadcastPacket(pc, new S_SkillSound(cha.getId(), 3939));
        }
        return calcDamageReduction(cha, dmg, L1Skills.ATTR_FIRE);
    }

    public static double ChainSword_BlazeShock(L1PcInstance pc, L1Character cha, int enchant) {
        double dmg = 0;
        int chance = _random.nextInt(100) + 1;
        int val = enchant * 1;
        if (val <= 0) {
            val = 1;
        } else
            val += 1;

        if (val >= chance) {
            int randmg = _random.nextInt(50) + 20;
            dmg = randmg;

            if (dmg < 20)
                dmg = 20;

            pc.sendPackets(new S_SkillSound(cha.getId(), 3939));
            Broadcaster.broadcastPacket(pc, new S_SkillSound(cha.getId(), 3939));
        }
        if (_random.nextInt(100) < 15) {
            if (pc.hasSkillEffect(L1SkillId.CHAINSWORD1)) {
                pc.killSkillEffectTimer(L1SkillId.CHAINSWORD1);
                pc.setSkillEffect(L1SkillId.CHAINSWORD2, 15 * 1000);
                pc.sendPackets(new S_PacketBox(S_PacketBox.SPOT, 2));
            } else if (pc.hasSkillEffect(L1SkillId.CHAINSWORD2)) {
                pc.killSkillEffectTimer(L1SkillId.CHAINSWORD2);
                pc.setSkillEffect(L1SkillId.CHAINSWORD3, 15 * 1000);
                pc.sendPackets(new S_PacketBox(S_PacketBox.SPOT, 3));
            } else if (pc.hasSkillEffect(L1SkillId.CHAINSWORD3)) {
            } else {
                pc.setSkillEffect(L1SkillId.CHAINSWORD1, 15 * 1000);
                pc.sendPackets(new S_PacketBox(S_PacketBox.SPOT, 1));
            }
        }
        return calcDamageReduction(cha, dmg, L1Skills.ATTR_FIRE);
    }


    public static double ChainSword_Welcome(L1PcInstance pc, L1Character cha, int enchant) {
        double dmg = 0;
        int locx = cha.getX();
        int locy = cha.getY();
        int chance = _random.nextInt(100) + 1;
        if (5 + enchant >= chance) {
            dmg = 20;
            if (dmg <= 0) {
                dmg = 0;
            }
            S_EffectLocation packet = new S_EffectLocation(locx, locy, 7398);
            pc.sendPackets(packet);
            pc.broadcastPacket(packet);
        }
        return calcDamageReduction(cha, dmg, L1Skills.ATTR_WATER);
    }

    public static double LordSword(L1PcInstance pc, L1Character cha, int effect, int enchant) {
        double dmg = 0;
        int locx = cha.getX();
        int locy = cha.getY();
        int intel = pc.getAbility().getTotalInt();
        int chance = _random.nextInt(100) + 1;
        if (2 + enchant >= chance) {
            dmg = _random.nextInt(intel / 2) + (intel);
            if (dmg <= 0) {
                dmg = 0;
            }
            S_EffectLocation packet = new S_EffectLocation(locx, locy, effect);
            pc.sendPackets(packet);
            pc.broadcastPacket(packet);
        }
        return calcDamageReduction(cha, dmg, L1Skills.ATTR_WIND);
    }

    public static double Destroyer(L1PcInstance pc, L1Character cha, int effect, int enchant) {
        double dmg = 0;
        int locx = cha.getX();
        int locy = cha.getY();
        int intel = pc.getAbility().getTotalInt();
        int chance = _random.nextInt(100) + 1;
        if (10 >= chance) {
            dmg = _random.nextInt(intel / 2) + (intel);
            if (dmg <= 0) {
                dmg = 0;
            }
            S_EffectLocation packet = new S_EffectLocation(locx, locy, effect);
            pc.sendPackets(packet);
            pc.broadcastPacket(packet);
        }
        return calcDamageReduction(cha, dmg, L1Skills.ATTR_WIND);
    }

    public static double Nightmare(L1PcInstance pc, L1Character cha, int effect, int enchant) {
        double dmg = 0;
        int locx = cha.getX();
        int locy = cha.getY();
        int intel = pc.getAbility().getTotalInt();
        int chance = _random.nextInt(100) + 1;
        if (10 >= chance) {
            dmg = _random.nextInt(intel / 2) + (intel);
            if (dmg <= 0) {
                dmg = 0;
            }
            S_EffectLocation packet = new S_EffectLocation(locx, locy, effect);
            pc.sendPackets(packet);
            pc.broadcastPacket(packet);
        }
        return calcDamageReduction(cha, dmg, L1Skills.ATTR_WIND);
    }

    public static double Jinsa(L1PcInstance pc, L1Character cha, int effect, int enchant) {
        double dmg = 0;
        int locx = cha.getX();
        int locy = cha.getY();
        int intel = pc.getAbility().getTotalInt();
        int chance = _random.nextInt(100) + 1;
        if (10 >= chance) {
            dmg = _random.nextInt(intel / 2) + (intel);
            if (dmg <= 0) {
                dmg = 0;
            }
            S_EffectLocation packet = new S_EffectLocation(locx, locy, effect);
            pc.sendPackets(packet);
            pc.broadcastPacket(packet);
        }
        return calcDamageReduction(cha, dmg, L1Skills.ATTR_WIND);
    }

    public static double StormAx(L1PcInstance pc, L1Character cha, int effect, int enchant) {
        double dmg = 0;
        int locx = cha.getX();
        int locy = cha.getY();
        int intel = pc.getAbility().getTotalInt();
        int chance = _random.nextInt(100) + 1;
        if (10 >= chance) {
            dmg = _random.nextInt(intel / 2) + (intel);
            if (dmg <= 0) {
                dmg = 0;
            }
            S_EffectLocation packet = new S_EffectLocation(locx, locy, effect);
            pc.sendPackets(packet);
            pc.broadcastPacket(packet);
        }
        return calcDamageReduction(cha, dmg, L1Skills.ATTR_WIND);
    }

    public static double DestructionDualBlade_Crow(L1PcInstance pc, L1Character cha, int effect, int enchant) {
        double dmg = 0;
        int locx = cha.getX();
        int locy = cha.getY();
        int intel = pc.getAbility().getTotalInt();
        int chance = _random.nextInt(100) + 1;
        if (1 + enchant >= chance) {
            dmg = _random.nextInt(intel / 2) + (intel * 2);
            if (dmg <= 0) {
                dmg = 0;
            }
            S_EffectLocation packet = new S_EffectLocation(locx, locy, effect);
            pc.sendPackets(packet);
            pc.broadcastPacket(packet);
        }
        return calcDamageReduction(cha, dmg, L1Skills.ATTR_EARTH);
    }

    public static double InsanityWindAx(L1PcInstance pc, L1Character cha, int effect, int enchant) {
        double dmg = 0;
        int locx = cha.getX();
        int locy = cha.getY();
        int intel = pc.getAbility().getTotalInt();
        int chance = _random.nextInt(100) + 1;
        if (2 + enchant >= chance) {
            dmg = _random.nextInt(intel / 2) + (intel * 2);
            if (dmg <= 0) {
                dmg = 0;
            }
            S_EffectLocation packet = new S_EffectLocation(locx, locy, effect);
            pc.sendPackets(packet);
            pc.broadcastPacket(packet);
        }
        return calcDamageReduction(cha, dmg, L1Skills.ATTR_WIND);
    }

    public static double ExColdWind(L1PcInstance pc, L1Character cha, int effect, int enchant) {
        double dmg = 0;
        int locx = cha.getX();
        int locy = cha.getY();
        int intel = pc.getAbility().getTotalInt();
        int chance = _random.nextInt(100) + 1;
        if (2 + enchant >= chance) {
            dmg = _random.nextInt(intel / 2) + (intel);
            if (dmg <= 0) {
                dmg = 0;
            }
            S_EffectLocation packet = new S_EffectLocation(locx, locy, effect);
            pc.sendPackets(packet);
            pc.broadcastPacket(packet);
        }
        return calcDamageReduction(cha, dmg, L1Skills.ATTR_WIND);
    }

    public static double AngelSlayer(L1PcInstance pc, L1Character cha, int effect, int enchant) {
        double dmg = 0;
        int locx = cha.getX();
        int locy = cha.getY();
        int intel = pc.getAbility().getTotalInt();
        int chance = _random.nextInt(100) + 1;
        if (1 + enchant >= chance) {
            dmg = _random.nextInt(intel) + (intel);
            if (dmg <= 0) {
                dmg = 0;
            }
            S_EffectLocation packet = new S_EffectLocation(locx, locy, effect);
            pc.sendPackets(packet);
            pc.broadcastPacket(packet);
        }
        return calcDamageReduction(cha, dmg, L1Skills.ATTR_WIND);
    }

    public static double LightningEdge(L1PcInstance pc, L1Character cha, int effect, int enchant) {
        double dmg = 0;
        int locx = cha.getX();
        int locy = cha.getY();
        int intel = pc.getAbility().getTotalInt();
        int chance = _random.nextInt(100) + 1;
        if (1 + enchant >= chance) {
            dmg = _random.nextInt(intel) + (intel * 2);
            if (dmg <= 0) {
                dmg = 0;
            }
            S_EffectLocation packet = new S_EffectLocation(locx, locy, effect);
            pc.sendPackets(packet);
            pc.broadcastPacket(packet);
        }
        return calcDamageReduction(cha, dmg, L1Skills.ATTR_WIND);
    }

    public static double Kiringku_Resonance(L1PcInstance pc, L1Character cha, int effect, int enchant) {
        double dmg = 0;
        int locx = cha.getX();
        int locy = cha.getY();
        int intel = pc.getAbility().getTotalInt();
        int chance = _random.nextInt(100) + 1;
        if (2 + enchant >= chance) {
            dmg = _random.nextInt(intel) + 20 + (intel * 2);
            if (dmg <= 0) {
                dmg = 0;
            }
            S_EffectLocation packet = new S_EffectLocation(locx, locy, effect);
            pc.sendPackets(packet);
            pc.broadcastPacket(packet);
        }
        return calcDamageReduction(cha, dmg, L1Skills.ATTR_WIND);
    }

    public static double Kiringku_Cold(L1PcInstance pc, L1Character cha, int effect, int enchant) {
        double dmg = 0;
        int locx = cha.getX();
        int locy = cha.getY();
        int intel = pc.getAbility().getTotalInt();
        int chance = _random.nextInt(100) + 1;
        if (3 + enchant >= chance) {
            dmg = _random.nextInt(intel / 2) + (intel * 3);
            if (cha.getCurrentMp() >= 5) {
                cha.setCurrentMp(cha.getCurrentMp() - 5);
                if (dmg <= 0) {
                    dmg = 0;
                }
            }
            S_EffectLocation packet = new S_EffectLocation(locx, locy, effect);
            pc.sendPackets(packet);
            pc.broadcastPacket(packet);
        }
        return calcDamageReduction(cha, dmg, L1Skills.ATTR_WATER);
    }

    public static double HypelionsDespair(L1PcInstance pc, L1Character cha, int effect, int enchant) {
        double dmg = 0;
        int locx = cha.getX();
        int locy = cha.getY();
        int intel = pc.getAbility().getTotalInt();
        int chance = _random.nextInt(100) + 1;
        if (3 + enchant >= chance) {
            dmg = _random.nextInt(intel / 2) + (intel * 3);
            if (dmg <= 0) {
                dmg = 0;
            }
            S_EffectLocation packet = new S_EffectLocation(locx, locy, effect);
            pc.sendPackets(packet);
            pc.broadcastPacket(packet);
        }
        return calcDamageReduction(cha, dmg, L1Skills.ATTR_WATER);
    }

    public static double Redskill(L1PcInstance pc, L1Character cha, int effect, int enchant) {
        double dmg = 0;
        int locx = cha.getX();
        int locy = cha.getY();
        int intel = pc.getAbility().getTotalInt();
        int chance = _random.nextInt(100) + 1;
        if (3 + enchant >= chance) {
            dmg = _random.nextInt(intel / 2) + (intel * 3);
            if (cha.getCurrentMp() >= 5) {
                cha.setCurrentMp(cha.getCurrentMp() - 5);
                if (dmg <= 0) {
                    dmg = 0;
                }
            }
            S_EffectLocation packet = new S_EffectLocation(locx, locy, effect);
            pc.sendPackets(packet);
            pc.broadcastPacket(packet);
        }
        return calcDamageReduction(cha, dmg, L1Skills.ATTR_WATER);
    }

    public static double KurtzsSword(L1PcInstance pc, L1Character cha, int enchant, int effect) {
        double dmg = 0;
        int locx = cha.getX();
        int locy = cha.getY();
        int intel = pc.getAbility().getTotalInt();
        int chance = _random.nextInt(100) + 1;
        if (2 + enchant >= chance) {
            dmg = _random.nextInt(intel * 2) + (intel * 4);
            if (dmg <= 0) {
                dmg = 0;
            }
            S_EffectLocation packet = new S_EffectLocation(locx, locy, effect);
            pc.sendPackets(packet);
            pc.broadcastPacket(packet);
        }
        return calcDamageReduction(cha, dmg, L1Skills.ATTR_WIND);
    }

    public static double DeathKnightFlameBlade(L1PcInstance pc, L1Character cha, int enchant, int effect) {
        double dmg = 0;
        int locx = cha.getX();
        int locy = cha.getY();
        int intel = pc.getAbility().getTotalInt();
        int chance = _random.nextInt(100) + 1;
        if (2 + enchant >= chance) {
            dmg = _random.nextInt(intel * 2) + (intel * 4);
            if (dmg <= 0) {
                dmg = 0;
            }
            S_EffectLocation packet = new S_EffectLocation(locx, locy, effect);
            pc.sendPackets(packet);
            pc.broadcastPacket(packet);
        }
        return calcDamageReduction(cha, dmg, L1Skills.ATTR_FIRE);
    }

    public static double BaphometStaff(L1PcInstance pc, L1Character cha, int enchant, int effect) {
        double dmg = 0;
        int locx = cha.getX();
        int locy = cha.getY();
        int sp = pc.getSp();
        int intel = pc.getAbility().getTotalInt();
        int chance = _random.nextInt(100) + 1;
        if (18 + enchant >= chance) {
            dmg = _random.nextInt(enchant * 2 + 2) + ((sp + intel) * 2); // <--エンチャン側+1ないつけるバポ杖0の場合、エフェクトない発光
            if (dmg <= 0) {
                dmg = 0;
            }
            S_EffectLocation packet = new S_EffectLocation(locx, locy, effect);
            pc.sendPackets(packet);
            pc.broadcastPacket(packet);
        }
        return calcDamageReduction(cha, dmg, L1Skills.ATTR_EARTH);
    }

    public static double ZerosWand(L1PcInstance pc, L1Character cha, int enchant, int effect) {
        double dmg = 0;
        int locx = cha.getX();
        int locy = cha.getY();
        int sp = pc.getSp();
        int intel = pc.getAbility().getTotalInt();
        int chance = _random.nextInt(100) + 1;
        if (6 + enchant >= chance && 3 <= chance) {
            dmg = _random.nextInt(sp + intel) + ((sp + intel) * 2.5);
            if (2 >= chance) {
                dmg += dmg * 1.5;
            }
            if (dmg <= 0) {
                dmg = 0;
            }
            S_EffectLocation packet = new S_EffectLocation(locx, locy, effect);
            pc.sendPackets(packet);
            pc.broadcastPacket(packet);
        }
        return calcDamageReduction(cha, dmg, L1Skills.ATTR_FIRE);
    }

    public static double HolyHedronStaff(L1PcInstance pc, L1Character cha, int enchant, int effect) {
        double dmg = 0;
        int locx = cha.getX();
        int locy = cha.getY();
        int sp = pc.getSp();
        int intel = pc.getAbility().getTotalInt();
        int chance = _random.nextInt(100) + 1;
        if (18 + enchant >= chance) {
            dmg = _random.nextInt(sp + intel) + ((sp + intel) * 3.3);
            if (dmg <= 0) {
                dmg = 0;
            }
            S_EffectLocation packet = new S_EffectLocation(locx, locy, effect);
            pc.sendPackets(packet);
            pc.broadcastPacket(packet);
        }
        return calcDamageReduction(cha, dmg, L1Skills.ATTR_WIND);
    }

    public static void AngelStaff(L1PcInstance pc, L1Character cha, int enchant) {
        int chance = _random.nextInt(100) + 1;
        int undeadType = ((L1MonsterInstance) cha).getNpcTemplate().get_undead();
        if (undeadType == 1 || undeadType == 3) {
            if (enchant >= chance) {
                new L1SkillUse().handleCommands(pc, TURN_UNDEAD, cha.getId(), cha.getX(), cha.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
            }
        }
    }

    public static double calcDamageReduction(L1Character cha, double dmg, int attr) {
        if (isFreeze(cha)) {
            return 0;
        }

        int ran1 = 0; //ランダム数値を適用
        int mrset = 0; //エムアルでランダム数値をペンガプ
        int mrs = cha.getResistance().getEffectedMrBySkill();
        ran1 = _random.nextInt(5) + 1;
        mrset = mrs - ran1;
        double calMr = 0.00D;
        calMr = (220 - mrset) / 250.00D;
        dmg *= calMr;

        if (dmg < 0) {
            dmg = 0;
        }

        int resist = 0;
        if (attr == L1Skills.ATTR_EARTH) {
            resist = cha.getResistance().getEarth();
        } else if (attr == L1Skills.ATTR_FIRE) {
            resist = cha.getResistance().getFire();
        } else if (attr == L1Skills.ATTR_WATER) {
            resist = cha.getResistance().getWater();
        } else if (attr == L1Skills.ATTR_WIND) {
            resist = cha.getResistance().getWind();
        }
        int resistFloor = (int) (0.32 * Math.abs(resist));
        if (resist >= 0) {
            resistFloor *= 1;
        } else {
            resistFloor *= -1;
        }
        double attrDeffence = resistFloor / 32.0;
        dmg = (1.0 - attrDeffence) * dmg;

        return dmg;
    }

    private static boolean isFreeze(L1Character cha) {

        if (cha.hasSkillEffect(STATUS_FREEZE)) {
            return true;
        }
        if (cha.hasSkillEffect(ABSOLUTE_BARRIER)) {
            return true;
        }
        if (cha.hasSkillEffect(ICE_LANCE)) {
            return true;
        }
        if (cha.hasSkillEffect(EARTH_BIND)) {
            return true;
        }

        if (cha.hasSkillEffect(COUNTER_MAGIC)) {
            cha.removeSkillEffect(COUNTER_MAGIC);
            int castgfx = SkillsTable.getInstance().getTemplate(COUNTER_MAGIC).getCastGfx();
            cha.broadcastPacket(new S_SkillSound(cha.getId(), castgfx));
            if (cha instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) cha;
                pc.sendPackets(new S_SkillSound(pc.getId(), castgfx));
            }
            return true;
        }
        return false;
    }
}
