package l1j.server.server.model;

import static l1j.server.server.model.skill.L1SkillId.*;

import java.util.ArrayList;
import java.util.List;

import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_Ability;
import l1j.server.server.serverpackets.S_AddSkill;
import l1j.server.server.serverpackets.S_DelSkill;
import l1j.server.server.serverpackets.S_Invis;
import l1j.server.server.serverpackets.S_ItemStatus;
import l1j.server.server.serverpackets.S_OtherCharPacks;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillIconAura;
import l1j.server.server.serverpackets.S_SkillIconBlessOfEva;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.templates.L1Item;

public class L1EquipmentSlot {

    private L1PcInstance _owner;

    private ArrayList<L1ArmorSet> _currentArmorSet;

    private ArrayList<L1ItemInstance> _weapons;

    private ArrayList<L1ItemInstance> _armors;

    private int weapons_idx = 0;
    public int worldjoin_weapon_idx = 0;

    public L1EquipmentSlot(L1PcInstance owner) {
        _owner = owner;
        _weapons = new ArrayList<L1ItemInstance>();
        _armors = new ArrayList<L1ItemInstance>();
        _currentArmorSet = new ArrayList<L1ArmorSet>();
    }

    private void setWeapon(L1ItemInstance weapon) {
        int itemId = weapon.getItem().getItemId();
        int enchant = weapon.getEnchantLevel();
        if (itemId == 1134 || itemId == 101134) { // 瞑想の杖
            if (enchant > 0) {
                _owner.addMpr(enchant);
            }
        }


        weapon.startEquipmentTimer(_owner);
        _weapons.add(weapon);
        for (int i = 0; i < 2; ++i) {
            try {
                if (i == 0)
                    _owner.setWeapon(_weapons.get(i));
                else
                    _owner.setSecondWeapon(_weapons.get(i));
            } catch (Exception e) {
                if (i == 0)
                    _owner.setWeapon(null);
                else
                    _owner.setSecondWeapon(null);
            }
        }
        // System.out.println("左("+_owner.getWeapon()+") 右("+_owner.getSecondWeapon()+")");
        if (itemId == 134 && enchant >= 1) {
            _owner.getAbility().addSp(enchant);
            _owner.sendPackets(new S_SPMR(_owner));
        }
        if (_weapons.size() == 2) {
            _owner.setCurrentWeapon(88);
            _owner.sendPackets(new S_SkillSound(_owner.getId(), 12534));
        } else {
            _owner.setCurrentWeapon(weapon.getItem().getType1());
        }
        weaponRange(_owner);
        if (itemId == 10000) { // 会社員
            L1PolyMorph.doPoly(_owner, 11498, 0, L1PolyMorph.MORPH_BY_ITEMMAGIC);
        } else if (itemId == 203003) { // デスナイトのフレイムブレード：ジン
            L1PolyMorph.doPoly(_owner, 12232, 0, L1PolyMorph.MORPH_BY_ITEMMAGIC);
        }

        if (weapon.hasSkillEffectTimer(L1SkillId.BLESS_WEAPON)) {
            int time = weapon.getSkillEffectTimeSec(L1SkillId.BLESS_WEAPON);
            _owner.sendPackets(new S_PacketBox(S_PacketBox.BUFFICON, time, weapon.getEnchantMagic(), getWeapons().size() - 1));
        } else if (weapon.hasSkillEffectTimer(L1SkillId.ENCHANT_WEAPON)) {
            int time = weapon.getSkillEffectTimeSec(L1SkillId.ENCHANT_WEAPON);
            _owner.sendPackets(new S_PacketBox(S_PacketBox.BUFFICON, time, weapon.getEnchantMagic(), getWeapons().size() - 1));
        } else if (weapon.hasSkillEffectTimer(L1SkillId.SHADOW_FANG)) {
            int time = weapon.getSkillEffectTimeSec(L1SkillId.SHADOW_FANG);
            _owner.sendPackets(new S_PacketBox(S_PacketBox.BUFFICON, time, weapon.getEnchantMagic(), getWeapons().size() - 1));
        } else if (weapon.hasSkillEffectTimer(L1SkillId.HOLY_WEAPON)) {
            int time = weapon.getSkillEffectTimeSec(L1SkillId.HOLY_WEAPON);
            _owner.sendPackets(new S_PacketBox(S_PacketBox.BUFFICON, time, weapon.getEnchantMagic(), getWeapons().size() - 1));
        }
    }

    /**
     * 着用中の武器のゲル最後の武器を返し。
     *
     * @return
     */
    public L1ItemInstance getWeapon() {
        return _weapons.size() > 0 ? _weapons.get(_weapons.size() - 1) : null;
    }

    /**
     * 着用中の武器を交互に戻す。
     *
     * @return
     */
    public L1ItemInstance getWeaponSwap() {
        if (_weapons.size() > 0) {
            if (_weapons.size() > 1)
                return _weapons.get(weapons_idx++ % 2);
            else
                return _weapons.get(0);
        }
        return null;
    }

    public boolean isWeapon(L1ItemInstance weapon) {
        return _weapons.contains(weapon);
    }

    public int getWeaponCount() {
        return _weapons.size();
    }

    public List<L1ItemInstance> getWeapons() {
        return new ArrayList<L1ItemInstance>(_weapons);
    }

    private void setArmor(L1ItemInstance armor) {
        L1Item item = armor.getItem();
        int itemlvl = armor.getEnchantLevel();
        int itemtype = armor.getItem().getType();
        int itemId = armor.getItem().getItemId();
        int itemgrade = armor.getItem().getGrade();
        if (armor.getItem().getDmgRate() != 0) {
            _owner.addDmgup(armor.getItem().getDmgRate());
        }
        if (itemtype >= 8 && itemtype <= 12) {
            _owner.getAC().addAc(item.get_ac() - armor.getAcByMagic() + armor.get_durability());
            _owner.sendPackets(new S_OwnCharAttrDef(_owner));
        } else if (itemtype == 17) { //記章時
            _owner.getAC().addAc(item.get_ac() - armor.getAcByMagic() + armor.get_durability());
            _owner.sendPackets(new S_OwnCharAttrDef(_owner));
        } else {
            _owner.getAC().addAc(item.get_ac() - armor.getEnchantLevel() - armor.getAcByMagic() + armor.get_durability());
            _owner.sendPackets(new S_OwnCharAttrDef(_owner));
        }
        /** 大魔法使いの帽子であるチェンダンmp増加 **/
        if (itemId == 202022) {
            if (itemlvl >= 1) {
                _owner.setMaxMp(_owner.getMaxMp() + (itemlvl * 10));
            }
        }
        /** リッチローブのエンチャントSP増加 **/
        if (itemId == 20107) {
            if (itemlvl >= 3) {
                _owner.getAbility().addSp(itemlvl - 2);
            }
        }
        /** マンボコートのエンチャント+7でのCHA増加 **/
        if (itemId == 20112 || itemId == 120112) {
            if (itemlvl >= 7) {
                _owner.getAbility().addCha(1);
            }
        }
        /** ヴァラカスフレイムシリーズ **/
        if (itemId == 22208 || itemId == 22209 || itemId == 22210 || itemId == 22211) {
            if (itemlvl <= 6) {
                _owner.addDmgCritical(3);
            } else if (itemlvl == 7) {
                _owner.addDmgCritical(4);
            } else if (itemlvl == 8) {
                _owner.addDmgCritical(5);
            } else if (itemlvl >= 9) {
                _owner.addDmgCritical(6);
            }
        }
//        /** ドラゴンアーマーシリーズの竜語耐性 **/
//        if (itemId == 22196 || itemId == 22197 || itemId == 22198 || itemId == 22199
//            || itemId == 22200 || itemId == 22201 || itemId == 22202 || itemId == 22203
//            || itemId == 22204 || itemId == 22205 || itemId == 22206 || itemId == 22207
//            || itemId == 22208 || itemId == 22209 || itemId == 22210 || itemId == 22211) {
//            if (itemlvl <= 4) {
//            } else if (itemlvl == 5) {
//            } else if (itemlvl == 6) {
//            } else if (itemlvl == 7) {
//            } else if (itemlvl == 8) {
//            } else if (itemlvl >= 9) {
//            }
//        }

        _owner.addDamageReductionByArmor(item.getDamageReduction());
        _owner.addWeightReduction(item.getWeightReduction());
        _owner.addBowHitRate(item.getBowHitRate());
        _owner.getResistance().addEarth(item.get_defense_earth());
        _owner.getResistance().addWind(item.get_defense_wind());
        _owner.getResistance().addWater(item.get_defense_water());
        _owner.getResistance().addFire(item.get_defense_fire());
        _owner.getResistance().addStun(item.get_regist_stun());
        _owner.getResistance().addPetrifaction(item.get_regist_stone());
        _owner.getResistance().addSleep(item.get_regist_sleep());
        _owner.getResistance().addFreeze(item.get_regist_freeze());
        _owner.getResistance().addHold(item.get_regist_sustain());
        _owner.getResistance().addDESPERADO(item.get_regist_DESPERADO());
        _owner.getResistance().addcalcPcDefense(item.get_regist_calcPcDefense());
        _owner.getResistance().addPVPweaponTotalDamage(item.get_regist_PVPweaponTotalDamage());
        _armors.add(armor);

        if (armor.hasSkillEffectTimer(L1SkillId.BLESSED_ARMOR)) {
            int time = armor.getSkillEffectTimeSec(L1SkillId.BLESSED_ARMOR);
            _owner.sendPackets(new S_PacketBox(S_PacketBox.BUFFICON, time, armor.getEnchantMagic(), 0));
        }

        for (L1ArmorSet armorSet : L1ArmorSet.getAllSet()) {
            if (armorSet.isPartOfSet(itemId) && armorSet.isValid(_owner)) {
                if (armor.getItem().getType2() == 2 && armor.getItem().getType() == 9) {
                    if (!armorSet.isEquippedRingOfArmorSet(_owner)) {
                        armorSet.giveEffect(_owner);
                        _currentArmorSet.add(armorSet);
                        if (item.getMainId() != 0) {
                            L1ItemInstance main = _owner.getInventory().findItemId(item.getMainId());
                            if (main != null) {
                                if (main.isEquipped())
                                    _owner.sendPackets(new S_ItemStatus(main, _owner, true, true));

                            }
                        }
                        if (item.getMainId2() != 0) {
                            L1ItemInstance main = _owner.getInventory().findItemId(item.getMainId2());
                            if (main != null) {
                                if (main.isEquipped())
                                    _owner.sendPackets(new S_ItemStatus(main, _owner, true, true));
                            }
                        }
                        if (item.getMainId3() != 0) {
                            L1ItemInstance main = _owner.getInventory().findItemId(item.getMainId3());
                            if (main != null) {
                                if (main.isEquipped())
                                    _owner.sendPackets(new S_ItemStatus(main, _owner, true, true));
                            }
                        }

                    }
                } else {
                    armorSet.giveEffect(_owner);
                    _currentArmorSet.add(armorSet);
                    if (item.getMainId() != 0) {
                        L1ItemInstance main = _owner.getInventory().findItemId(item.getMainId());
                        if (main != null) {
                            if (main.isEquipped())
                                _owner.sendPackets(new S_ItemStatus(main, _owner, true, true));
                        }
                    }
                    if (item.getMainId2() != 0) {
                        L1ItemInstance main = _owner.getInventory().findItemId(item.getMainId2());
                        if (main != null) {
                            if (main.isEquipped())
                                _owner.sendPackets(new S_ItemStatus(main, _owner, true, true));
                        }
                    }
                    if (item.getMainId3() != 0) {
                        L1ItemInstance main = _owner.getInventory().findItemId(item.getMainId3());
                        if (main != null) {
                            if (main.isEquipped())
                                _owner.sendPackets(new S_ItemStatus(main, _owner, true, true));
                        }
                    }
                }
            }
        }
        if (itemId == 423014) {
            _owner.startAHRegeneration();
        }
        if (itemId == 423015) {
            _owner.startSHRegeneration();
        }
        if (itemId == 20380) {
            _owner.startHalloweenRegeneration();
        }
        if (itemId == 20077 || itemId == 20062 || itemId == 120077) {
            if (!_owner.hasSkillEffect(L1SkillId.INVISIBILITY)) {
                for (L1DollInstance doll : _owner.getDollList()) {
                    doll.deleteDoll();
                }
                _owner.killSkillEffectTimer(L1SkillId.BLIND_HIDING);
                _owner.setSkillEffect(L1SkillId.INVISIBILITY, 0);
                _owner.sendPackets(new S_Invis(_owner.getId(), 1));
                if (_owner.isInParty()) {
                    for (L1PcInstance tar : L1World.getInstance().getVisiblePlayer(_owner, -1)) {
                        if (_owner.getParty().isMember(tar)) {
                            tar.sendPackets(new S_OtherCharPacks(_owner, tar, true));
                        }
                    }
                }
                for (L1PcInstance pc2 : L1World.getInstance().getVisiblePlayer(_owner)) {
                    if (pc2.hasSkillEffect(L1SkillId.STATUS_FLOATING_EYE) && pc2.hasSkillEffect(L1SkillId.CURSE_BLIND)) {
                        pc2.sendPackets(new S_OtherCharPacks(_owner, pc2, true));
                    }
                }
            }
        }
        if (itemId == 20288) {
            _owner.sendPackets(new S_Ability(1, true));
        }
        if (itemId == 20281) {
            _owner.sendPackets(new S_Ability(2, true));
        }
        if (itemId == 20036) {
            _owner.sendPackets(new S_Ability(3, true));
        }
        /*if (itemId == 20284) {
            _owner.sendPackets(new S_Ability(5, true));
		}*/
        if (itemId == 20207) {
            _owner.sendPackets(new S_SkillIconBlessOfEva(_owner.getId(), -1));
        }

        if (itemId == 20383) {
            if (armor.getChargeCount() != 0) {
                armor.setChargeCount(armor.getChargeCount() - 1);
                _owner.getInventory().updateItem(armor, L1PcInventory.COL_CHARGE_COUNT);
            }
        }

        /*** 50レベルエリクサールーン ***/
        // アジャイルのエリクサールーン
        if (itemId == 222295) {
            switch (_owner.getType()) {
                // 君主
                case 0:
                    _owner.addDamageReductionByArmor(3);
                    break;
                // ナイト//戦士
                case 7:
                case 1:
                    _owner.addMaxHp(50);
                    break;
                // 妖精
                case 2:
                    _owner.addMaxMp(50);
                    break;
                // ウィザード
                case 3:
                    _owner.addMpr(3);
                    break;
                // すべてへ
                case 4:
                    _owner.getAC().addAc(-3);
                    break;
                // 竜騎士
                case 5:
                    _owner.addHitup(3);
                    break;
                // イリュージョニスト
                case 6:
                    _owner.addWeightReduction(5);
                    break;
            }
        }
        // 体力のエリクサールーン
        if (itemId == 222296) {
            switch (_owner.getType()) {
                // 君主
                case 0:
                    _owner.addDamageReductionByArmor(3);
                    break;
                // ナイト//戦士
                case 7:
                case 1:
                    _owner.addMaxHp(50);
                    break;
                // 妖精
                case 2:
                    _owner.addMaxMp(50);
                    break;
                // ウィザード
                case 3:
                    _owner.addMpr(3);
                    break;
                // すべてへ
                case 4:
                    _owner.getAC().addAc(-3);
                    break;
                // 竜騎士
                case 5:
                    _owner.addHitup(3);
                    break;
                // イリュージョニスト
                case 6:
                    _owner.addWeightReduction(5);
                    break;
            }
        }
        // 知識のエリクサールーン
        if (itemId == 222297) {
            switch (_owner.getType()) {
                // 君主
                case 0:
                    _owner.addDamageReductionByArmor(3);
                    break;
                // ナイト//戦士
                case 7:
                case 1:
                    _owner.addMaxHp(50);
                    break;
                // 妖精
                case 2:
                    _owner.addMaxMp(50);
                    break;
                // ウィザード
                case 3:
                    _owner.addMpr(3);
                    break;
                // すべてへ
                case 4:
                    _owner.getAC().addAc(-3);
                    break;
                // 竜騎士
                case 5:
                    _owner.addHitup(3);
                    break;
                // イリュージョニスト
                case 6:
                    _owner.addWeightReduction(5);
                    break;
            }
        }
        // 知恵のエリクサールーン
        if (itemId == 222298) {
            switch (_owner.getType()) {
                // 君主
                case 0:
                    _owner.addDamageReductionByArmor(3);
                    break;
                // ナイト//戦士
                case 7:
                case 1:
                    _owner.addMaxHp(50);
                    break;
                // 妖精
                case 2:
                    _owner.addMaxMp(50);
                    break;
                // ウィザード
                case 3:
                    _owner.addMpr(3);
                    break;
                // すべてへ
                case 4:
                    _owner.getAC().addAc(-3);
                    break;
                // 竜騎士
                case 5:
                    _owner.addHitup(3);
                    break;
                // イリュージョニスト
                case 6:
                    _owner.addWeightReduction(5);
                    break;
            }
        }
        // 力のエリクサールーン
        if (itemId == 222299) {
            switch (_owner.getType()) {
                // 君主
                case 0:
                    _owner.addDamageReductionByArmor(3);
                    break;
                // ナイト//戦士
                case 7:
                case 1:
                    _owner.addMaxHp(50);
                    break;
                // 妖精
                case 2:
                    _owner.addMaxMp(50);
                    break;
                // ウィザード
                case 3:
                    _owner.addMpr(3);
                    break;
                // すべてへ
                case 4:
                    _owner.getAC().addAc(-3);
                    break;
                // 竜騎士
                case 5:
                    _owner.addHitup(3);
                    break;
                // イリュージョニスト
                case 6:
                    _owner.addWeightReduction(5);
                    break;
            }
        }

        /*** 70レベルエリクサールーン ***/
        // アジャイルのエリクサールーン
        if (itemId == 222312) {
            switch (_owner.getType()) {
                // 君主
                case 0:
                    _owner.addDamageReductionByArmor(3);
                    _owner.addDmgup(2);
                    break;
                // ナイト
                case 1:
                    _owner.addMaxHp(50);
                    _owner.addDmgup(1);
                    break;
                // 妖精
                case 2:
                    _owner.addMaxMp(50);
                    _owner.addBowDmgup(1);
                    break;
                // ウィザード
                case 3:
                    _owner.addMpr(3);
                    _owner.getAbility().addSp(1);
                    break;
                // すべてへ
                case 4:
                    _owner.getAC().addAc(-3);
                    _owner.addMaxMp(30);
                    break;
                // 竜騎士
                case 5:
                    _owner.addHitup(3);
                    _owner.addDamageReductionByArmor(1);
                    break;
                // イリュージョニスト
                case 6:
                    _owner.addWeightReduction(5);
                    _owner.addMaxHp(50);
                    break;
                // 戦士
                case 7:
                    _owner.getResistance().addMr(5);
                    _owner.addMaxHp(50);
                    break;
            }
        }
        // 体力のエリクサールーン
        if (itemId == 222313) {
            switch (_owner.getType()) {
                // 君主
                case 0:
                    _owner.addDamageReductionByArmor(3);
                    _owner.addDmgup(2);
                    break;
                // ナイト
                case 1:
                    _owner.addMaxHp(50);
                    _owner.addDmgup(1);
                    break;
                // 妖精
                case 2:
                    _owner.addMaxMp(50);
                    _owner.addBowDmgup(1);
                    break;
                // ウィザード
                case 3:
                    _owner.addMpr(3);
                    _owner.getAbility().addSp(1);
                    break;
                // すべてへ
                case 4:
                    _owner.getAC().addAc(-3);
                    _owner.addMaxMp(30);
                    break;
                // 竜騎士
                case 5:
                    _owner.addHitup(3);
                    _owner.addDamageReductionByArmor(1);
                    break;
                // イリュージョニスト
                case 6:
                    _owner.addWeightReduction(5);
                    _owner.addMaxHp(50);
                    break;
                // 戦士
                case 7:
                    _owner.getResistance().addMr(5);
                    _owner.addMaxHp(50);
                    break;
            }
        }
        // 知識のエリクサールーン
        if (itemId == 222314) {
            switch (_owner.getType()) {
                // 君主
                case 0:
                    _owner.addDamageReductionByArmor(3);
                    _owner.addDmgup(2);
                    break;
                // ナイト
                case 1:
                    _owner.addMaxHp(50);
                    _owner.addDmgup(1);
                    break;
                // 妖精
                case 2:
                    _owner.addMaxMp(50);
                    _owner.addBowDmgup(1);
                    break;
                // ウィザード
                case 3:
                    _owner.addMpr(3);
                    _owner.getAbility().addSp(1);
                    break;
                // すべてへ
                case 4:
                    _owner.getAC().addAc(-3);
                    _owner.addMaxMp(30);
                    break;
                // 竜騎士
                case 5:
                    _owner.addHitup(3);
                    _owner.addDamageReductionByArmor(1);
                    break;
                // イリュージョニスト
                case 6:
                    _owner.addWeightReduction(5);
                    _owner.addMaxHp(50);
                    break;
                // 戦士
                case 7:
                    _owner.getResistance().addMr(5);
                    _owner.addMaxHp(50);
                    break;
            }
        }
        // 知恵のエリクサールーン
        if (itemId == 222315) {
            switch (_owner.getType()) {
                // 君主
                case 0:
                    _owner.addDamageReductionByArmor(3);
                    _owner.addDmgup(2);
                    break;
                // ナイト
                case 1:
                    _owner.addMaxHp(50);
                    _owner.addDmgup(1);
                    break;
                // 妖精
                case 2:
                    _owner.addMaxMp(50);
                    _owner.addBowDmgup(1);
                    break;
                // ウィザード
                case 3:
                    _owner.addMpr(3);
                    _owner.getAbility().addSp(1);
                    break;
                // すべてへ
                case 4:
                    _owner.getAC().addAc(-3);
                    _owner.addMaxMp(30);
                    break;
                // 竜騎士
                case 5:
                    _owner.addHitup(3);
                    _owner.addDamageReductionByArmor(1);
                    break;
                // イリュージョニスト
                case 6:
                    _owner.addWeightReduction(5);
                    _owner.addMaxHp(50);
                    break;
                // 戦士
                case 7:
                    _owner.getResistance().addMr(5);
                    _owner.addMaxHp(50);
                    break;
            }
        }
        // 力のエリクサールーン
        if (itemId == 222316) {
            switch (_owner.getType()) {
                // 君主
                case 0:
                    _owner.addDamageReductionByArmor(3);
                    _owner.addDmgup(2);
                    break;
                // ナイト
                case 1:
                    _owner.addMaxHp(50);
                    _owner.addDmgup(1);
                    break;
                // 妖精
                case 2:
                    _owner.addMaxMp(50);
                    _owner.addBowDmgup(1);
                    break;
                // ウィザード
                case 3:
                    _owner.addMpr(3);
                    _owner.getAbility().addSp(1);
                    break;
                // すべてへ
                case 4:
                    _owner.getAC().addAc(-3);
                    _owner.addMaxMp(30);
                    break;
                // 竜騎士
                case 5:
                    _owner.addHitup(3);
                    _owner.addDamageReductionByArmor(1);
                    break;
                // イリュージョニスト
                case 6:
                    _owner.addWeightReduction(5);
                    _owner.addMaxHp(50);
                    break;
                // 戦士
                case 7:
                    _owner.getResistance().addMr(5);
                    _owner.addMaxHp(50);
                    break;
            }
        }

        /** ウィザードのがより **/
        if (itemId == 22255) {
            switch (itemlvl) {
                case 5:
                case 6:
                    _owner.getAbility().addSp(1);
                    break;
                case 7:
                case 8:
                    _owner.getAbility().addSp(2);
                    break;
                case 9:
                    _owner.getAbility().addSp(3);
                    break;
                default:
                    break;
            }
        }

        /** 体力のガーダー **/
        if (itemId == 22256) {
            switch (itemlvl) {
                case 5:
                case 6:
                    _owner.addMaxHp(25);
                    break;
                case 7:
                case 8:
                    _owner.addMaxHp(50);
                    break;
                case 9:
                    _owner.addMaxHp(75);
                    break;
                default:
                    break;
            }
        }

        /** ナイトバルド両手剣スタンレベル+1 **/
        if (itemId == 1121 || itemId == 11121) {
            switch (itemlvl) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                    _owner.getResistance().addStun(1);
                    break;
                default:
                    break;
            }
        }
        /** タイタンの怒り技術発動HP区間5％増加した。**/
        if (itemId == 202014) {
            _owner.setLockSectionUp(5);
        }


        /** 真冥王の執行剣スタンレベル+2 **/
        if (itemId == 61 || itemId == 7000061 || itemId == 202012) {
            switch (itemlvl) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                    _owner.getResistance().addStun(2);
                    break;
                default:
                    break;
            }
        }

        /** 古代闘士のガーダー **/
        if (itemId == 22003) {
            switch (itemlvl) {
                case 5:
                case 6:
                    _owner.addDmgup(1);
                    break;
                case 7:
                case 8:
                    _owner.addDmgup(2);
                    break;
                case 9:
                    _owner.addDmgup(3);
                    break;
                default:
                    break;
            }
        }

        /** 古代名弓のガーダー **/
        if (itemId == 22000) {
            switch (itemlvl) {
                case 5:
                case 6:
                    _owner.addBowHitup(1);
                    break;
                case 7:
                case 8:
                    _owner.addBowHitup(2);
                    break;
                case 9:
                    _owner.addBowHitup(3);
                    break;
                default:
                    break;
            }
        }
        /** マミーロードクラウン **/
        if (itemId == 20017) {
            switch (itemlvl) {
                case 7:
                    _owner.addBowHitup(1);
                    break;
                case 8:
                    _owner.addBowHitup(2);
                    break;
                case 9:
                    _owner.addBowHitup(3);
                    break;
                default:
                    break;
            }
        }
        /**守護のガーダー **/
        if (itemId == 22254) {
            switch (itemlvl) {
                case 5:
                case 6:
                    _owner.addDamageReductionByArmor(1);
                    break;
                case 7:
                case 8:
                    _owner.addDamageReductionByArmor(2);
                    break;
                case 9:
                    _owner.addDamageReductionByArmor(3);
                    break;
                default:
                    break;
            }
        }
        /** アンタラス グランド シリーズ **/
        if (itemId == 22196 || itemId == 22197 || itemId == 22198 || itemId == 22199) {
            switch (itemlvl) {
                case 7:
                    _owner.addDamageReductionByArmor(1);
                    break;
                case 8:
                    _owner.addDamageReductionByArmor(2);
                    break;
                case 9:
                    _owner.addDamageReductionByArmor(3);
                    break;
                default:
                    break;
            }
        }
        /**輝く魔力の手袋 **/
        if (itemId == 20274) {
            switch (itemlvl) {
                case 5:
                    _owner.addWeightReduction(1);
                    break;
                case 6:
                    _owner.addWeightReduction(2);
                    break;
                case 7:
                    _owner.addWeightReduction(3);
                    break;
                case 8:
                    _owner.addWeightReduction(4);
                    break;
                case 9:
                    _owner.addWeightReduction(5);
                    break;
                default:
                    break;
            }
        }
        /** 激怒の手袋**/
        if (itemId == 222317) {
            switch (itemlvl) {
                case 5:
                    _owner.addHitup(1);
                    _owner.addBowHitup(1);
                    break;
                case 6:
                    _owner.addHitup(2);
                    _owner.addBowHitup(2);
                    break;
                case 7:
                    _owner.addHitup(4);
                    _owner.addBowHitup(4);
                    break;
                case 8:
                    _owner.addHitup(5);
                    _owner.addBowHitup(5);
                    break;
                case 9:
                    _owner.addHitup(6);
                    _owner.addBowHitup(6);
                    break;
                default:
                    break;
            }
        }


        // 古代の岩シリーズ
        if (itemId >= 900011 && itemId <= 900014) {
            _owner.addDamageReductionByArmor(2);
            if (itemlvl >= 8) {
                _owner.addDamageReductionByArmor(1);
                _owner.getResistance().addcalcPcDefense(1);
            }
        }
        // 古代魔物シリーズ
        if (itemId >= 900015 && itemId <= 900018) {
            _owner.addregist_PVPweaponTotalDamage(2);
            if (itemlvl >= 8) {
                _owner.addDmgup(1);
                _owner.addBowDmgup(1);
            }
        }
        if (itemId == 900019) { // シルフのTシャツ
            switch (itemlvl) {
                case 0:
                    _owner.getResistance().addMr(7);
                    _owner.getResistance().addStun(7);
                    break;
                case 1:
                    _owner.getResistance().addStun(8);
                    _owner.getResistance().addMr(8);
                    break;
                case 2:
                    _owner.getResistance().addStun(9);
                    _owner.getResistance().addMr(9);
                    break;
                case 3:
                    _owner.getResistance().addStun(10);
                    _owner.getResistance().addMr(10);
                    break;
                case 4:
                    _owner.getResistance().addStun(11);
                    _owner.getResistance().addMr(11);
                    break;
                case 5:
                    _owner.getResistance().addStun(12);
                    _owner.getResistance().addMr(12);
                    break;
                case 6:
                    _owner.getResistance().addStun(13);
                    _owner.getResistance().addMr(13);
                    break;
                case 7:
                    _owner.getResistance().addStun(14);
                    _owner.getResistance().addMr(14);
                    break;
                case 8:
                    _owner.getResistance().addStun(15);
                    _owner.getResistance().addMr(15);
                    break;
                case 9:
                    _owner.getResistance().addStun(16);
                    _owner.getResistance().addMr(16);
                    break;
                case 10:
                    _owner.getResistance().addStun(17);
                    _owner.getResistance().addMr(17);
                    break;
                case 11:
                    _owner.getResistance().addStun(18);
                    _owner.getResistance().addMr(18);
                    break;
                case 12:
                    _owner.getResistance().addStun(19);
                    _owner.getResistance().addMr(19);
                    break;
                case 13:
                    _owner.getResistance().addStun(20);
                    _owner.getResistance().addMr(20);
                    break;
                case 14:
                    _owner.getResistance().addStun(21);
                    _owner.getResistance().addMr(21);
                    break;
                case 15:
                    _owner.getResistance().addStun(22);
                    _owner.getResistance().addMr(22);
                    break;
                default:
                    break;
            }
        }


        if (itemId == 22200 || itemId == 22201 || itemId == 22202 || itemId == 22203) {// パプリオンアーマー
            _owner.startPapuBlessing();
        }

        /** アクセサリーエンチャントリニューアル **/
        if (itemtype >= 8 && itemtype <= 12) {
            if ((itemgrade >= 0 && itemgrade <= 2) && (itemtype == 8 || itemtype == 12)) { //ネックレス、イヤリング
                switch (itemlvl) {
                    case 1:
                        _owner.addMaxHp(5);
                        break;
                    case 2:
                        _owner.addMaxHp(10);
                        break;
                    case 3:
                        _owner.addMaxHp(20);
                        break;
                    case 4:
                        _owner.addMaxHp(30);
                        break;
                    case 5:
                        _owner.addMaxHp(40);
                        _owner.getAC().addAc(-1);
                        break;
                    case 6:
                        _owner.addMaxHp(40);
                        _owner.getAC().addAc(-2);
                        break;
                    case 7:
                        _owner.addMaxHp(50);
                        _owner.getAC().addAc(-3);
                        _owner.getResistance().addStun(2);
                        break;
                    case 8:
                        _owner.addMaxHp(50);
                        _owner.getAC().addAc(-4);
                        _owner.getResistance().addStun(3);
                        break;
                    case 9:
                        _owner.addMaxHp(60);
                        _owner.getAC().addAc(-5);
                        _owner.getResistance().addStun(4);
                        break;
                }
            } else if ((itemgrade >= 0 && itemgrade <= 2) && (itemtype == 9 || itemtype == 11)) { // リング
                switch (itemlvl) {
                    case 1:
                        _owner.addMaxHp(5);
                        break;
                    case 2:
                        _owner.addMaxHp(10);
                        break;
                    case 3:
                        _owner.addMaxHp(20);
                        break;
                    case 4:
                        _owner.addMaxHp(30);
                        break;
                    case 5:
                        _owner.addDmgup(1);
                        _owner.addBowDmgup(1);
                        _owner.addMaxHp(40);
                        break;
                    case 6:
                        _owner.addDmgup(2);
                        _owner.addBowDmgup(2);
                        _owner.addMaxHp(40);
                        _owner.getResistance().addMr(1);
                        break;
                    case 7:
                        _owner.addDmgup(3);
                        _owner.addBowDmgup(3);
                        _owner.addMaxHp(50);
                        _owner.getAbility().addSp(1);
                        _owner.getResistance().addMr(3);
                        _owner.getResistance().addPVPweaponTotalDamage(1);
                        break;
                    case 8:
                        _owner.addDmgup(4);
                        _owner.addBowDmgup(4);
                        _owner.addMaxHp(50);
                        _owner.getAbility().addSp(2);
                        _owner.getResistance().addMr(5);
                        _owner.getResistance().addPVPweaponTotalDamage(2);
                        break;
                    case 9:
                        _owner.addDmgup(5);
                        _owner.addBowDmgup(5);
                        _owner.addMaxHp(60);
                        _owner.getAbility().addSp(3);
                        _owner.getResistance().addMr(7);
                        _owner.getResistance().addPVPweaponTotalDamage(3);
                        break;
                }
            } else if ((itemgrade >= 0 && itemgrade <= 2) && (itemtype == 10)) { // ベルト
                switch (itemlvl) {
                    case 1:
                        _owner.addMaxMp(5);
                        break;
                    case 2:
                        _owner.addMaxMp(10);
                        break;
                    case 3:
                        _owner.addMaxMp(20);
                        break;
                    case 4:
                        _owner.addMaxMp(30);
                        break;
                    case 5:
                        _owner.addDamageReductionByArmor(1);
                        _owner.addMaxMp(40);
                        break;
                    case 6:
                        _owner.addDamageReductionByArmor(2);
                        _owner.addMaxMp(40);
                        _owner.addMaxHp(20);
                        break;
                    case 7:
                        _owner.addDamageReductionByArmor(3);
                        _owner.addMaxMp(50);
                        _owner.addMaxHp(30);
                        _owner.getResistance().addPVPweaponTotalDamage(2);
                        break;
                    case 8:
                        _owner.addDamageReductionByArmor(4);
                        _owner.addMaxMp(50);
                        _owner.addMaxHp(40);
                        _owner.getResistance().addPVPweaponTotalDamage(3);
                        break;
                    case 9:
                        _owner.addDamageReductionByArmor(5);
                        _owner.addMaxMp(60);
                        _owner.addMaxHp(50);
                        _owner.getResistance().addPVPweaponTotalDamage(4);
                        break;
                }
                // スナップファーリング着用着用部分//スタン耐性の場合現全体と一般ユーザーの格差を減らすために579ですが - > 456に下方ヘトウム
            } else if (itemgrade == 3 && itemId >= 22224 && itemId <= 22228) { // 回復、集中、体力、マナ、魔法
                switch (itemlvl) {
                    case 1:
                        _owner.addMaxHp(15);
                        break;
                    case 2:
                        _owner.addMaxHp(20);
                        _owner.getAC().addAc(-1);
                        break;
                    case 3:
                        _owner.addMaxHp(25);
                        _owner.getAC().addAc(-2);
                        break;
                    case 4:
                        _owner.addMaxHp(30);
                        _owner.getAC().addAc(-3);
                        break;
                    case 5:
                        _owner.addMaxHp(35);
                        _owner.getAC().addAc(-3);
                        _owner.addDmgup(1);
                        _owner.addBowDmgup(1);
                        break;
                    case 6:
                        _owner.addMaxHp(40);
                        _owner.getAC().addAc(-3);
                        _owner.addDmgup(2);
                        _owner.addBowDmgup(2);
                        _owner.getResistance().addStun(4);
                        break;
                    case 7:
                        _owner.addMaxHp(45);
                        _owner.getAC().addAc(-3);
                        _owner.addDmgup(3);
                        _owner.addBowDmgup(3);
                        _owner.getResistance().addStun(5);
                        break;
                    case 8:
                        _owner.addMaxHp(50);
                        _owner.getAC().addAc(-3);
                        _owner.addDmgup(4);
                        _owner.addBowDmgup(4);
                        _owner.getResistance().addStun(6);
                        break;
                    default:
                        break;
                }
            } else if (itemgrade == 3 && itemId == 222290) { // 知恵のリング
                switch (itemlvl) {
                    case 1:
                        _owner.addMaxHp(5);
                        break;
                    case 2:
                        _owner.addMaxHp(10);
                        _owner.getAC().addAc(-1);
                        break;
                    case 3:
                        _owner.addMaxHp(15);
                        _owner.getAC().addAc(-2);
                        break;
                    case 4:
                        _owner.addMaxHp(20);
                        _owner.getAC().addAc(-3);
                        break;
                    case 5:
                        _owner.addMaxHp(25);
                        _owner.getAC().addAc(-3);
                        _owner.getAbility().addSp(1);
                        break;
                    case 6:
                        _owner.addMaxHp(30);
                        _owner.getAC().addAc(-3);
                        _owner.getAbility().addSp(2);
                        _owner.getResistance().addStun(4);
                        break;
                    case 7:
                        _owner.addMaxHp(35);
                        _owner.getAC().addAc(-3);
                        _owner.getAbility().addSp(2);
                        _owner.getResistance().addStun(5);
                        break;
                    case 8:
                        _owner.addMaxHp(40);
                        _owner.getAC().addAc(-3);
                        _owner.getAbility().addSp(3);
                        _owner.getResistance().addStun(6);
                        break;
                    default:
                        break;
                }
            } else if (itemgrade == 3 && itemId == 222291) { // 勇者のリング
                switch (itemlvl) {
                    case 1:
                        _owner.getAC().addAc(-1);
                        break;
                    case 2:
                        _owner.getAC().addAc(-2);
                        break;
                    case 3:
                        _owner.addMaxHp(5);
                        _owner.getAC().addAc(-3);
                        break;
                    case 4:
                        _owner.addMaxHp(10);
                        _owner.getAC().addAc(-4);
                        break;
                    case 5:
                        _owner.addMaxHp(15);
                        _owner.getAC().addAc(-4);
                        _owner.addHitup(1);
                        _owner.addBowHitup(1);
                        _owner.addDmgup(1);
                        _owner.addBowDmgup(1);
                        break;
                    case 6:
                        _owner.addMaxHp(20);
                        _owner.getAC().addAc(-4);
                        _owner.addHitup(2);
                        _owner.addBowHitup(2);
                        _owner.addDmgup(2);
                        _owner.addBowDmgup(2);
                        _owner.getResistance().addStun(4);
                        break;
                    case 7:
                        _owner.addMaxHp(25);
                        _owner.getAC().addAc(-4);
                        _owner.addHitup(3);
                        _owner.addBowHitup(3);
                        _owner.addDmgup(3);
                        _owner.addBowDmgup(3);
                        _owner.getResistance().addStun(5);
                        break;
                    case 8:
                        _owner.addMaxHp(30);
                        _owner.getAC().addAc(-4);
                        _owner.addHitup(4);
                        _owner.addBowHitup(4);
                        _owner.addDmgup(4);
                        _owner.addBowDmgup(4);
                        _owner.getResistance().addStun(6);
                        break;
                    default:
                        break;
                }
                /** 祝福されたスナップ広がりの体力リング**/
            } else if (itemgrade == 3 && itemId == 222332) {
                switch (itemlvl) {
                    case 1:
                        _owner.addMaxHp(15);
                        break;
                    case 2:
                        _owner.addMaxHp(20);
                        _owner.getAC().addAc(-1);
                        break;
                    case 3:
                        _owner.addMaxHp(30);
                        _owner.getAC().addAc(-2);
                        break;
                    case 4:
                        _owner.addMaxHp(35);
                        _owner.getAC().addAc(-3);
                        _owner.addDmgup(1);
                        _owner.addBowDmgup(1);
                        break;
                    case 5:
                        _owner.addMaxHp(40);
                        _owner.getAC().addAc(-3);
                        _owner.addDmgup(2);
                        _owner.addBowDmgup(2);
                        break;
                    case 6:
                        _owner.addMaxHp(45);
                        _owner.getAC().addAc(-3);
                        _owner.addDmgup(3);
                        _owner.addBowDmgup(3);
                        _owner.getResistance().addStun(4);
                        break;
                    case 7:
                        _owner.addMaxHp(55);
                        _owner.getAC().addAc(-4);
                        _owner.addDmgup(4);
                        _owner.addBowDmgup(4);
                        _owner.getResistance().addStun(5);
                        break;
                    case 8:
                        _owner.addMaxHp(65);
                        _owner.getAC().addAc(-4);
                        _owner.addDmgup(5);
                        _owner.addBowDmgup(5);
                        _owner.getResistance().addStun(6);
                        break;
                    default:
                        break;
                }
                /** 祝福されたスナップファーの回復、集中、マナリング **/
            } else if (itemgrade == 3 && itemId == 222330 || itemId == 222331 || itemId == 222333) {
                switch (itemlvl) {
                    case 1:
                        _owner.addMaxHp(15);
                        break;
                    case 2:
                        _owner.addMaxHp(20);
                        _owner.getAC().addAc(-1);
                        break;
                    case 3:
                        _owner.addMaxHp(30);
                        _owner.getAC().addAc(-2);
                        break;
                    case 4:
                        _owner.addMaxHp(35);
                        _owner.getAC().addAc(-3);
                        _owner.addDmgup(1);
                        _owner.addBowDmgup(1);
                        break;
                    case 5:
                        _owner.addMaxHp(40);
                        _owner.getAC().addAc(-3);
                        _owner.addDmgup(2);
                        _owner.addBowDmgup(2);
                        break;
                    case 6:
                        _owner.addMaxHp(45);
                        _owner.getAC().addAc(-3);
                        _owner.addDmgup(3);
                        _owner.addBowDmgup(3);
                        _owner.getResistance().addStun(4);
                        break;
                    case 7:
                        _owner.addMaxHp(50);
                        _owner.getAC().addAc(-3);
                        _owner.addDmgup(4);
                        _owner.addBowDmgup(4);
                        _owner.getResistance().addStun(5);
                        break;
                    case 8:
                        _owner.addMaxHp(50);
                        _owner.getAC().addAc(-3);
                        _owner.addDmgup(5);
                        _owner.addBowDmgup(5);
                        _owner.getResistance().addStun(6);
                        break;
                    default:
                        break;
                }
                /**祝福されたスナップ広がりの魔法抵抗リング **/
            } else if (itemgrade == 3 && itemId == 222334) {
                switch (itemlvl) {
                    case 1:
                        _owner.addMaxHp(15);
                        break;
                    case 2:
                        _owner.addMaxHp(20);
                        _owner.getAC().addAc(-1);
                        break;
                    case 3:
                        _owner.addMaxHp(30);
                        _owner.getAC().addAc(-2);
                        break;
                    case 4:
                        _owner.addMaxHp(35);
                        _owner.getAC().addAc(-3);
                        _owner.addDmgup(1);
                        _owner.addBowDmgup(1);
                        break;
                    case 5:
                        _owner.addMaxHp(40);
                        _owner.getAC().addAc(-3);
                        _owner.addDmgup(2);
                        _owner.addBowDmgup(2);
                        break;
                    case 6:
                        _owner.addMaxHp(45);
                        _owner.getAC().addAc(-3);
                        _owner.addDmgup(3);
                        _owner.addBowDmgup(3);
                        _owner.getResistance().addMr(1);
                        _owner.getResistance().addStun(4);
                        break;
                    case 7:
                        _owner.addMaxHp(50);
                        _owner.getAC().addAc(-4);
                        _owner.addDmgup(4);
                        _owner.addBowDmgup(4);
                        _owner.getResistance().addMr(2);
                        _owner.getResistance().addStun(5);
                        break;
                    case 8:
                        _owner.addMaxHp(50);
                        _owner.getAC().addAc(-4);
                        _owner.addDmgup(5);
                        _owner.addBowDmgup(5);
                        _owner.getResistance().addMr(3);
                        _owner.getResistance().addStun(6);
                        break;
                    default:
                        break;
                }
                /** 祝福されたスナップ広がりの知恵リング **/
            } else if (itemgrade == 3 && itemId == 222335) {
                switch (itemlvl) {
                    case 1:
                        _owner.addMaxHp(5);
                        break;
                    case 2:
                        _owner.addMaxHp(10);
                        _owner.getAC().addAc(-1);
                        break;
                    case 3:
                        _owner.addMaxHp(20);
                        _owner.getAC().addAc(-2);
                        break;
                    case 4:
                        _owner.addMaxHp(25);
                        _owner.getAC().addAc(-3);
                        _owner.getAbility().addSp(1);
                        break;
                    case 5:
                        _owner.addMaxHp(30);
                        _owner.getAC().addAc(-3);
                        _owner.getAbility().addSp(2);
                        break;
                    case 6:
                        _owner.addMaxHp(35);
                        _owner.getAC().addAc(-3);
                        _owner.getAbility().addSp(2);
                        _owner.getResistance().addStun(4);
                        break;
                    case 7:
                        _owner.addMaxHp(40);
                        _owner.getAC().addAc(-4);
                        _owner.getAbility().addSp(3);
                        _owner.getResistance().addStun(5);
                        break;
                    case 8:
                        _owner.addMaxHp(50);
                        _owner.getAC().addAc(-4);
                        _owner.getAbility().addSp(4);
                        _owner.getResistance().addStun(6);
                        break;
                    default:
                        break;
                }
                /** 祝福されたスナップ広がりの勇士リング **/
            } else if (itemgrade == 3 && itemId == 222336) {
                switch (itemlvl) {
                    case 1:
                        _owner.getAC().addAc(-1);
                        break;
                    case 2:
                        _owner.getAC().addAc(-2);
                        break;
                    case 3:
                        _owner.addMaxHp(10);
                        _owner.getAC().addAc(-3);
                        break;
                    case 4:
                        _owner.addMaxHp(15);
                        _owner.getAC().addAc(-4);
                        _owner.addHitup(1);
                        _owner.addBowHitup(1);
                        _owner.addDmgup(1);
                        _owner.addBowDmgup(1);
                        break;
                    case 5:
                        _owner.addMaxHp(20);
                        _owner.getAC().addAc(-4);
                        _owner.addHitup(2);
                        _owner.addBowHitup(2);
                        _owner.addDmgup(2);
                        _owner.addBowDmgup(2);
                        break;
                    case 6:
                        _owner.addMaxHp(25);
                        _owner.getAC().addAc(-4);
                        _owner.addHitup(3);
                        _owner.addBowHitup(3);
                        _owner.addDmgup(3);
                        _owner.addBowDmgup(3);
                        _owner.getResistance().addStun(4);
                        break;
                    case 7:
                        _owner.addMaxHp(30);
                        _owner.getAC().addAc(-5);
                        _owner.addHitup(4);
                        _owner.addBowHitup(4);
                        _owner.addDmgup(4);
                        _owner.addBowDmgup(4);
                        _owner.getResistance().addStun(5);
                        break;
                    case 8:
                        _owner.addMaxHp(30);
                        _owner.getAC().addAc(-5);
                        _owner.addHitup(5);
                        _owner.addBowHitup(5);
                        _owner.addDmgup(5);
                        _owner.addBowDmgup(5);
                        _owner.getResistance().addStun(6);
                        break;
                    default:
                        break;
                }

            } else if (itemgrade == 4 && itemId == 222340) { // ルームティス ブラック イヤリング
                switch (itemlvl) {
                case 1:
                    _owner.getAC().addAc(-1);
                    break;
                case 2:
                    _owner.getAC().addAc(-2);
                    break;
                case 3:
                    _owner.getAC().addAc(-3);
                    _owner.addDmgup(1);
                    _owner.addBowDmgup(1);
                    break;
                case 4:
                    _owner.getAC().addAc(-4);
                    _owner.addDmgup(1);
                    _owner.addBowDmgup(1);
                    break;
                case 5:
                    _owner.getAC().addAc(-5);
                    _owner.addDmgup(2);
                    _owner.addBowDmgup(2);
                    break;
                case 6:
                    _owner.getAC().addAc(-6);
                    _owner.addDmgup(3);
                    _owner.addBowDmgup(3);
                    break;
                case 7:
                    _owner.getAC().addAc(-7);
                    _owner.addDmgup(4);
                    _owner.addBowDmgup(4);
                    break;
                case 8:
                    _owner.getAC().addAc(-8);
                    _owner.addDmgup(5);
                    _owner.addBowDmgup(5);
                    break;
                default:
                    break;
                }

            } else if (itemgrade == 4 && itemId == 222341) { // 祝福されたルームティス ブラック イヤリング
                switch (itemlvl) {
                case 3:
                    _owner.getAC().addAc(-4);
                    _owner.addDmgup(1);
                    _owner.addBowDmgup(1);
                    break;
                case 4:
                    _owner.getAC().addAc(-5);
                    _owner.addDmgup(2);
                    _owner.addBowDmgup(2);
                    break;
                case 5:
                    _owner.getAC().addAc(-6);
                    _owner.addDmgup(3);
                    _owner.addBowDmgup(3);
                    break;
                case 6:
                    _owner.getAC().addAc(-7);
                    _owner.addDmgup(4);
                    _owner.addBowDmgup(4);
                    break;
                case 7:
                    _owner.getAC().addAc(-8);
                    _owner.addDmgup(5);
                    _owner.addBowDmgup(5);
                    break;
                case 8:
                    _owner.getAC().addAc(-9);
                    _owner.addDmgup(6);
                    _owner.addBowDmgup(6);
                    break;
                default:
                    break;
                }

                /**成長/回復の文章 **/
            } else if (itemgrade == 3 && itemId == 900020 || itemId == 900021) {
                switch (itemlvl) {
                    case 0:
                        _owner.getAC().addAc(0);
                        break;
                    case 1:
                        _owner.getAC().addAc(1);
                        break;
                    case 2:
                        _owner.getAC().addAc(2);
                        break;
                    case 3:
                        _owner.getAC().addAc(3);
                        break;
                    case 4:
                        _owner.getAC().addAc(4);
                        break;
                    case 5:
                        _owner.getAC().addAc(5);
                        break;
                    case 6:
                        _owner.getAC().addAc(6);
                        break;
                    case 7:
                        _owner.getAC().addAc(7);
                        break;
                    case 8:
                        _owner.getAC().addAc(8);
                        break;
                    case 9:
                        _owner.getAC().addAc(9);
                        break;
                    case 10:
                        _owner.getAC().addAc(10);
                        break;
                    default:
                        break;
                }

            } else if (itemgrade == 4 && itemId == 22229) { // ルームティス レッド イアリング
                switch (itemlvl) {
                    case 1:
                        _owner.addMaxHp(20);
                        break;
                    case 2:
                        _owner.addMaxHp(30);
                        break;
                    case 3:
                        _owner.addMaxHp(40);
                        _owner.addDamageReductionByArmor(1);
                        break;
                    case 4:
                        _owner.addMaxHp(50);
                        _owner.addDamageReductionByArmor(1);
                        break;
                    case 5:
                        _owner.addMaxHp(60);
                        _owner.addDamageReductionByArmor(2);
                        break;
                    case 6:
                        _owner.getAC().addAc(-7);
                        _owner.addMaxHp(70);
                        _owner.addDamageReductionByArmor(3);
                        break;
                    case 7:
                        _owner.getAC().addAc(-8);
                        _owner.addHitup(1);
                        _owner.addBowHitup(1);
                        _owner.addMaxHp(80);
                        _owner.addDamageReductionByArmor(4);
                        break;
                    case 8:
                        _owner.getAC().addAc(-9);
                        _owner.addHitup(3);
                        _owner.addBowHitup(3);
                        _owner.addMaxHp(90);
                        _owner.addDamageReductionByArmor(5);
                        break;
                    default:
                        break;
                }
            } else if (itemgrade == 4 && itemId == 222337) { // 祝福されたルームティス レッド イアリング
                switch (itemlvl) {
                    case 3:
                        _owner.addMaxHp(50);
                        _owner.addDamageReductionByArmor(1);
                        break;
                    case 4:
                        _owner.addMaxHp(60);
                        _owner.addDamageReductionByArmor(2);
                        break;
                    case 5:
                        _owner.getAC().addAc(-7);
                        _owner.addMaxHp(70);
                        _owner.addDamageReductionByArmor(3);
                        break;
                    case 6:
                        _owner.getAC().addAc(-8);
                        _owner.addHitup(1);
                        _owner.addBowHitup(1);
                        _owner.addMaxHp(80);
                        _owner.addDamageReductionByArmor(4);
                        break;
                    case 7:
                        _owner.getAC().addAc(-9);
                        _owner.addHitup(3);
                        _owner.addBowHitup(3);
                        _owner.addMaxHp(90);
                        _owner.addDamageReductionByArmor(5);
                        break;
                    case 8:
                        _owner.getAC().addAc(-10);
                        _owner.addHitup(5);
                        _owner.addBowHitup(5);
                        _owner.addMaxHp(150);
                        _owner.addDamageReductionByArmor(6);
                        break;
                    default:
                        break;
                }
            } else if (itemgrade == 4 && itemId == 22230) { // ルームティス ブルー イヤリング
                switch (itemlvl) {
                    case 5:
                        _owner.getAC().addAc(-1);
                        break;
                    case 6:
                        _owner.getAC().addAc(-2);
                        break;
                    case 7:
                        _owner.getAC().addAc(-2);
                        break;
                    case 8:
                        _owner.getAC().addAc(-3);
                        break;
                    default:
                        break;
                }
            } else if (itemgrade == 4 && itemId == 222338) { // 祝福されたルームティス ブルー イヤリング
                switch (itemlvl) {
                    case 4:
                        _owner.getAC().addAc(-1);
                        break;
                    case 5:
                        _owner.getAC().addAc(-2);
                        break;
                    case 6:
                        _owner.getAC().addAc(-2);
                        break;
                    case 7:
                        _owner.getAC().addAc(-3);
                        break;
                    case 8:
                        _owner.getAC().addAc(-4);
                        break;
                    default:
                        break;
                }
            } else if (itemgrade == 4 && itemId == 22231) { // ルームティス パープル イヤリング
                switch (itemlvl) {
                    case 1:
                        _owner.addMaxMp(10);
                        _owner.getResistance().addMr(3);
                        break;
                    case 2:
                        _owner.addMaxMp(15);
                        _owner.getResistance().addMr(4);
                        break;
                    case 3:
                        _owner.addMaxMp(30);
                        _owner.getResistance().addMr(5);
                        _owner.getAbility().addSp(1);
                        break;
                    case 4:
                        _owner.addMaxMp(35);
                        _owner.getResistance().addMr(6);
                        _owner.getAbility().addSp(1);
                        break;
                    case 5:
                        _owner.addMaxMp(50);
                        _owner.getResistance().addMr(7);
                        _owner.getAbility().addSp(2);
                        break;
                    case 6:
                        _owner.getAC().addAc(-1);
                        _owner.addMaxMp(55);
                        _owner.getResistance().addMr(8);
                        _owner.getAbility().addSp(2);
                        break;
                    case 7:
                        _owner.getAC().addAc(-2);
                        _owner.addMaxMp(70);
                        _owner.getResistance().addMr(10);
                        _owner.getAbility().addSp(3);
                        break;
                    case 8:
                        _owner.getAC().addAc(-3);
                        _owner.addMaxMp(95);
                        _owner.getResistance().addMr(13);
                        _owner.getAbility().addSp(3);
                        break;
                    default:
                        break;
                }
            } else if (itemgrade == 4 && itemId == 222339) { // 祝福されたルームティス パープル イヤリング
                switch (itemlvl) {
                    case 3:
                        _owner.addMaxMp(35);
                        _owner.getResistance().addMr(6);
                        _owner.getAbility().addSp(1);
                        break;
                    case 4:
                        _owner.addMaxMp(50);
                        _owner.getResistance().addMr(7);
                        _owner.getAbility().addSp(2);
                        break;
                    case 5:
                        _owner.getAC().addAc(-1);
                        _owner.addMaxMp(55);
                        _owner.getResistance().addMr(8);
                        _owner.getAbility().addSp(2);
                        break;
                    case 6:
                        _owner.getAC().addAc(-2);
                        _owner.addMaxMp(70);
                        _owner.getResistance().addMr(10);
                        _owner.getAbility().addSp(3);
                        break;
                    case 7:
                        _owner.getAC().addAc(-3);
                        _owner.addMaxMp(95);
                        _owner.getResistance().addMr(13);
                        _owner.getAbility().addSp(3);
                        break;
                    case 8:
                        _owner.getAC().addAc(-4);
                        _owner.addMaxMp(125);
                        _owner.getResistance().addMr(18);
                        _owner.getAbility().addSp(4);
                        break;
                    default:
                        break;
                }
            }
        }
        /**用のTシャツ **/
        if (itemId == 900023) {
            switch (itemlvl) {
                case 5:
                    _owner.getResistance().addMr(4);
                    break;
                case 6:
                    _owner.getResistance().addMr(5);
                    break;
                case 7:
                    _owner.getResistance().addMr(6);
                    break;
                case 8:
                    _owner.getResistance().addMr(8);
                    break;
                case 9:
                    _owner.getResistance().addMr(11);
                    break;
                case 10:
                    _owner.getResistance().addMr(14);
                    break;
                case 11:
                    _owner.getResistance().addMr(14);
                    break;
                default:
                    break;
            }
        } else if (itemId == 900024 || itemId == 900025 || itemId == 900026) {
            switch (itemlvl) {
                case 5:
                    _owner.getResistance().addStun(8);
                    break;
                case 6:
                    _owner.getResistance().addStun(9);
                    break;
                case 7:
                    _owner.getResistance().addStun(10);
                    break;
                case 8:
                    _owner.getResistance().addStun(12);
                    break;
                case 9:
                    _owner.getResistance().addStun(15);
                    break;
                case 10:
                    _owner.getResistance().addStun(18);
                    break;
                case 11:
                    _owner.getResistance().addStun(18);
                    break;
                default:
                    break;
            }
        }
        if (itemId == 900023 && itemlvl >= 9)  //地竜のTシャツ
            _owner.addDamageReductionByArmor(1);

        if (itemId == 900024 && itemlvl >= 9)  //火竜のTシャツ
            _owner.addDmgup(1);

        if (itemId == 900025 && itemlvl >= 9)  //風竜Tシャツ
            _owner.addBowDmgup(1);

        if (itemId == 900026)  //水竜のTシャツ
            switch (itemlvl) {
                case 9:
                    _owner.getAbility().addSp(2);
                    break;
                default:
                    _owner.getAbility().addSp(1);
                    break;
            }


        if ((itemId >= 900023 && itemId <= 900026) && itemlvl >= 10) {
            _owner.addMaxHp(100);
            _owner.getResistance().addcalcPcDefense(1);
            _owner.getResistance().addPVPweaponTotalDamage(1);
        }
        //ユニゲートル
        if (itemId >= 900027 && itemId <= 900029 && itemlvl >= 9) {
            switch (itemId) {
                case 900027:
                    _owner.addBowDmgup(1);
                    break;
                case 900028:
                    _owner.addDmgup(1);
                    break;
                case 900029:
                    _owner.getAbility().addSp(1);
                    break;
            }
        }
        /** 黒騎士のバイザーMR表示 **/
        /** 神聖なエルムの祝福MR表示 **/
        if (itemId == 222342 || itemId == 222344) {
            switch (itemlvl) {
                case 5:
                    _owner.getResistance().addMr(4);
                    break;
                case 6:
                    _owner.getResistance().addMr(8);
                    break;
                case 7:
                    _owner.getResistance().addMr(12);
                    break;
                case 8:
                    _owner.getResistance().addMr(16);
                    break;
                case 9:
                    _owner.getResistance().addMr(20);
                    break;
                case 10:
                    _owner.getResistance().addMr(24);
                    break;
                case 11:
                    _owner.getResistance().addMr(28);
                    break;
                default:
                    break;
            }
        }
        /** シールドウィングのブレイサー遠距離命中 **/
        if (itemId == 222343) {
            switch (itemlvl) {
                case 5:
                    _owner.addBowHitRate(1);
                    break;
                case 6:
                    _owner.addBowHitRate(2);
                    break;
                case 7:
                    _owner.addBowHitRate(3);
                    break;
                case 8:
                    _owner.addBowHitRate(4);
                    break;
                case 9:
                    _owner.addBowHitRate(5);
                    break;
                default:
                    break;
            }
        }
        /** シールドウィングのパワーグローブ近距離命中 **/
        if (itemId == 222345) {
            switch (itemlvl) {
                case 5:
                    _owner.addHitup(1);
                    break;
                case 6:
                    _owner.addHitup(2);
                    break;
                case 7:
                    _owner.addHitup(3);
                    break;
                case 8:
                    _owner.addHitup(4);
                    break;
                case 9:
                    _owner.addHitup(5);
                    break;
                default:
                    break;
            }
        }
        /** 神聖な永遠のネックレススタン耐性**/
        if (itemId == 222349) {
            _owner.getResistance().addStun(7);
        }
        /** （知恵/機敏/知識/腕力）のブーツ *
         * :	+7から最大HP + 20 / + 40 / + 60増加
         * :	+9にダメージ減少+ 1を追加
         * */
        if (itemId == 22359 || itemId == 222308 || itemId == 222309 || itemId == 222307) {
            switch (itemlvl) {
                case 7:
                    _owner.addMaxHp(20);
                    break;
                case 8:
                    _owner.addMaxHp(40);
                    break;
                case 9:
                    _owner.addMaxHp(60);
                    _owner.addDamageReductionByArmor(1);
                    break;
                default:
                    break;
            }
        }
        /** 腕力の文章 **/
        if (itemId == 222352) {
            switch (itemlvl) {
                case 5:
                    _owner.addHitup(1);
                    break;
                case 6:
                    _owner.addHitup(1);
                    _owner.addDmgup(1);
                    break;
                case 7:
                    _owner.addHitup(2);
                    _owner.addDmgup(2);
                    break;
                case 8:
                    _owner.addHitup(3);
                    _owner.addDmgup(3);
                    break;
                case 9:
                    _owner.addHitup(4);
                    _owner.addDmgup(4);
                    break;
                case 10:
                    _owner.addHitup(5);
                    _owner.addDmgup(5);
                    break;
                default:
                    break;
            }
        }
        /** 機敏の文章**/
        if (itemId == 222353) {
            switch (itemlvl) {
                case 5:
                    _owner.addBowHitRate(1);
                    break;
                case 6:
                    _owner.addBowHitRate(1);
                    _owner.addBowDmgup(1);
                    break;
                case 7:
                    _owner.addBowHitRate(2);
                    _owner.addBowDmgup(2);
                    break;
                case 8:
                    _owner.addBowHitRate(3);
                    _owner.addBowDmgup(3);
                    break;
                case 9:
                    _owner.addBowHitRate(4);
                    _owner.addBowDmgup(4);
                    break;
                case 10:
                    _owner.addBowHitRate(5);
                    _owner.addBowDmgup(5);
                    break;
                default:
                    break;
            }
        }
        /** 知識の文章 **/
        if (itemId == 222354) {
            switch (itemlvl) {
                case 6:
                    _owner.getAbility().addSp(1);
                    break;
                case 7:
                    _owner.getAbility().addSp(2);
                    break;
                case 8:
                    _owner.getAbility().addSp(3);
                    break;
                case 9:
                    _owner.getAbility().addSp(4);
                    break;
                case 10:
                    _owner.getAbility().addSp(5);
                    break;
                default:
                    break;
            }
        }
        if (itemId == 900032 && itemgrade == 5) { //闘士のインシグニア
            switch (itemlvl) {
                case 0:
                    _owner.addMaxHp(5);
                    break;
                case 1:
                    _owner.addMaxHp(10);
                    break;
                case 2:
                    _owner.addMaxHp(15);
                    break;
                case 3:
                    _owner.addMaxHp(20);
                    break;
                case 4:
                    _owner.addMaxHp(25);
                    _owner.getAC().addAc(-1);
                    break;
                case 5:
                    _owner.addMaxHp(30);
                    _owner.getAC().addAc(-2);
                    _owner.addDmgup(1);
                    break;
                case 6:
                    _owner.addMaxHp(35);
                    _owner.getAC().addAc(-3);
                    _owner.addDmgup(2);
                    _owner.addDmgCritical(1);
                    break;
                case 7:
                    _owner.addMaxHp(40);
                    _owner.getAC().addAc(-3);
                    _owner.addDmgup(3);
                    _owner.addDmgCritical(3);
                    break;
                case 8:
                    _owner.addMaxHp(50);
                    _owner.getAC().addAc(-3);
                    _owner.addDmgup(4);
                    _owner.addDmgCritical(5);
                    break;
            }
        }
        if (itemId == 900033 && itemgrade == 5) { //射手のインシグニア
            switch (itemlvl) {
                case 0:
                    _owner.addMaxHp(5);
                    break;
                case 1:
                    _owner.addMaxHp(10);
                    break;
                case 2:
                    _owner.addMaxHp(15);
                    break;
                case 3:
                    _owner.addMaxHp(20);
                    break;
                case 4:
                    _owner.addMaxHp(25);
                    _owner.getAC().addAc(-1);
                    break;
                case 5:
                    _owner.addMaxHp(30);
                    _owner.getAC().addAc(-2);
                    _owner.addBowDmgup(1);
                    break;
                case 6:
                    _owner.addMaxHp(35);
                    _owner.getAC().addAc(-3);
                    _owner.addBowDmgup(2);
                    _owner.addBowDmgCritical(1);
                    break;
                case 7:
                    _owner.addMaxHp(40);
                    _owner.getAC().addAc(-3);
                    _owner.addBowDmgup(3);
                    _owner.addBowDmgCritical(3);
                    break;
                case 8:
                    _owner.addMaxHp(50);
                    _owner.getAC().addAc(-3);
                    _owner.addBowDmgup(4);
                    _owner.addBowDmgCritical(5);
                    break;
            }
        }
        if (itemId == 900034 && itemgrade == 5) { //賢者のインシグニア
            switch (itemlvl) {
                case 0:
                    _owner.addMaxHp(5);
                    break;
                case 1:
                    _owner.addMaxHp(10);
                    break;
                case 2:
                    _owner.addMaxHp(15);
                    break;
                case 3:
                    _owner.addMaxHp(20);
                    break;
                case 4:
                    _owner.addMaxHp(25);
                    _owner.getAC().addAc(-1);
                    break;
                case 5:
                    _owner.addMaxHp(30);
                    _owner.getAC().addAc(-2);
                    _owner.addHitup(1);
                    break;
                case 6:
                    _owner.addMaxHp(35);
                    _owner.getAC().addAc(-3);
                    _owner.addHitup(2);
                    _owner.addMagicCritical(1);
                    break;
                case 7:
                    _owner.addMaxHp(40);
                    _owner.getAC().addAc(-3);
                    _owner.addHitup(3);
                    _owner.addMagicCritical(2);
                    break;
                case 8:
                    _owner.addMaxHp(50);
                    _owner.getAC().addAc(-3);
                    _owner.addHitup(4);
                    _owner.addMagicCritical(4);
                    break;
            }
        }
        if (itemId == 900035 && itemgrade == 5) { //守護のインシグニア
            switch (itemlvl) {
                case 0:
                    _owner.addMaxHp(5);
                    break;
                case 1:
                    _owner.addMaxHp(10);
                    break;
                case 2:
                    _owner.addMaxHp(15);
                    break;
                case 3:
                    _owner.addMaxHp(20);
                    _owner.getAC().addAc(-1);
                    break;
                case 4:
                    _owner.addMaxHp(25);
                    _owner.getAC().addAc(-2);
                    break;
                case 5:
                    _owner.addMaxHp(30);
                    _owner.getAC().addAc(-3);
                    _owner.addDamageReductionByArmor(1);
                    break;
                case 6:
                    _owner.addMaxHp(35);
                    _owner.getAC().addAc(-5);
                    _owner.addDamageReductionByArmor(2);
                    _owner.getResistance().addMr(3);

                    break;
                case 7:
                    _owner.addMaxHp(40);
                    _owner.getAC().addAc(-6);
                    _owner.addDamageReductionByArmor(3);
                    _owner.getResistance().addMr(5);
                    break;
                case 8:
                    _owner.addMaxHp(50);
                    _owner.getAC().addAc(-7);
                    _owner.addDamageReductionByArmor(4);
                    _owner.getResistance().addMr(7);
                    break;
            }
        }


        armor.startEquipmentTimer(_owner);
    }

    public ArrayList<L1ItemInstance> getArmors() {
        return _armors;
    }

    private void removeWeapon(L1ItemInstance weapon) {
        _owner.setWeapon(null);
        weaponRange(_owner);
        _owner.setCurrentWeapon(0);
        int itemId = weapon.getItem().getItemId();
        int enchant = weapon.getEnchantLevel();
        if (itemId == 1134 || itemId == 101134) { // 瞑想の杖
            if (enchant > 0) {
                _owner.addMpr(-enchant);
            }
        }

        int count = 0;
        for (L1ItemInstance item : getWeapons()) {
            if (item.hasSkillEffectTimer(L1SkillId.BLESS_WEAPON)) {
                count++;
            } else if (item.hasSkillEffectTimer(L1SkillId.ENCHANT_WEAPON)) {
                count++;
            }
            if (count > 0) {
                _owner.sendPackets(new S_PacketBox(S_PacketBox.BUFFICON, 0, item.getEnchantMagic(), 0));
                _owner.sendPackets(new S_PacketBox(S_PacketBox.BUFFICON, 0, item.getEnchantMagic(), 1));
            } else {
                _owner.sendPackets(new S_PacketBox(S_PacketBox.BUFFICON, 0, item.getEnchantMagic(), 0));
            }
        }

        weapon.stopEquipmentTimer(_owner);
        _weapons.remove(weapon);

        if (_owner.hasSkillEffect(L1SkillId.COUNTER_BARRIER)) {
            _owner.removeSkillEffect(L1SkillId.COUNTER_BARRIER);
        }
        if (_weapons.size() == 1)
            _owner.setCurrentWeapon(getWeapon().getItem().getType1());
        else
            _owner.setCurrentWeapon(0);
        if (itemId == 134 && enchant >= 1) {
            _owner.getAbility().addSp(-enchant);
            _owner.sendPackets(new S_SPMR(_owner));
        }
        if (itemId == 10000) { // 会社員
            L1PolyMorph.undoPoly(_owner);
        } else if (itemId == 203003) { // デスナイトのフレイムブレード：ジン
            L1PolyMorph.undoPoly(_owner);
        }
    }

    private void removeArmor(L1ItemInstance armor) {
        L1Item item = armor.getItem();
        int itemId = armor.getItem().getItemId();
        int itemlvl = armor.getEnchantLevel();
        int itemtype = armor.getItem().getType();
        int itemgrade = armor.getItem().getGrade();
        if (armor.getItem().getDmgRate() != 0) {
            _owner.addDmgup(-armor.getItem().getDmgRate());
        }
        if (itemtype >= 8 && itemtype <= 12) {
            _owner.getAC().addAc(-(item.get_ac() - armor.getAcByMagic() + armor.get_durability()));
            _owner.sendPackets(new S_OwnCharAttrDef(_owner));
        } else if (itemtype == 17) { //記章時
            _owner.getAC().addAc(-(item.get_ac() - armor.getAcByMagic() + armor.get_durability()));
            _owner.sendPackets(new S_OwnCharAttrDef(_owner));
        } else {
            _owner.getAC().addAc(-(item.get_ac() - armor.getEnchantLevel() - armor.getAcByMagic() + armor.get_durability()));
            _owner.sendPackets(new S_OwnCharAttrDef(_owner));
        }
        /** 大魔法使いの帽子であるチェンダンmp増加 **/
        if (itemId == 202022) {
            if (itemlvl >= 1) {
                _owner.setMaxMp(_owner.getMaxMp() - (itemlvl * 10));
            }
        }
        /** リッチローブのエンチャントSP増加 **/
        if (itemId == 20107) {
            if (itemlvl >= 3) {
                _owner.getAbility().addSp(-(itemlvl - 2));
            }
        }
        /** マンボコートのエンチャント+7でのCHA増加 **/
        if (itemId == 20112 || itemId == 120112) {
            if (itemlvl >= 7) {
                _owner.getAbility().addCha(-1);
            }
        }
        /** ヴァラカスフレイムシリーズ **/
        if (itemId == 22208 || itemId == 22209 || itemId == 22210 || itemId == 22211) {
            if (itemlvl <= 6) {
                _owner.addDmgCritical(-3);
            } else if (itemlvl == 7) {
                _owner.addDmgCritical(-4);
            } else if (itemlvl == 8) {
                _owner.addDmgCritical(-5);
            } else if (itemlvl >= 9) {
                _owner.addDmgCritical(-6);
            }
        }
//        /** ドラゴンアーマーシリーズの竜語耐性 **/
//        if (itemId == 22196 || itemId == 22197 || itemId == 22198 || itemId == 22199
//            || itemId == 22200 || itemId == 22201 || itemId == 22202 || itemId == 22203
//            || itemId == 22204 || itemId == 22205 || itemId == 22206 || itemId == 22207
//            || itemId == 22208 || itemId == 22209 || itemId == 22210 || itemId == 22211) {
//            if (itemlvl <= 4) {
//            } else if (itemlvl == 5) {
//            } else if (itemlvl == 6) {
//            } else if (itemlvl == 7) {
//            } else if (itemlvl == 8) {
//            } else if (itemlvl >= 9) {
//            }
//        }

        _owner.addDamageReductionByArmor(-item.getDamageReduction());
        _owner.addWeightReduction(-item.getWeightReduction());
        _owner.addBowHitRate(-item.getBowHitRate());
        _owner.getResistance().addEarth(-item.get_defense_earth());
        _owner.getResistance().addWind(-item.get_defense_wind());
        _owner.getResistance().addWater(-item.get_defense_water());
        _owner.getResistance().addFire(-item.get_defense_fire());
        _owner.getResistance().addStun(-item.get_regist_stun());
        _owner.getResistance().addPetrifaction(-item.get_regist_stone());
        _owner.getResistance().addSleep(-item.get_regist_sleep());
        _owner.getResistance().addFreeze(-item.get_regist_freeze());
        _owner.getResistance().addHold(-item.get_regist_sustain());
        _owner.getResistance().addDESPERADO(-item.get_regist_DESPERADO());
        _owner.getResistance().addcalcPcDefense(-item.get_regist_calcPcDefense());
        _owner.getResistance().addPVPweaponTotalDamage(-item.get_regist_PVPweaponTotalDamage());

        if (armor.hasSkillEffectTimer(L1SkillId.BLESSED_ARMOR)) {
            _owner.sendPackets(new S_PacketBox(S_PacketBox.BUFFICON, 0, armor.getEnchantMagic(), 0));
        }

        for (L1ArmorSet armorSet : L1ArmorSet.getAllSet()) {
            if (armorSet.isPartOfSet(itemId) && _currentArmorSet.contains(armorSet) && !armorSet.isValid(_owner)) {
                armorSet.cancelEffect(_owner);
                _currentArmorSet.remove(armorSet);
                if (item.getMainId() != 0) {
                    L1ItemInstance main = _owner.getInventory().findItemId(item.getMainId());
                    if (main != null) {
                        _owner.sendPackets(new S_ItemStatus(main, _owner, true, false));
                    }
                }
                if (item.getMainId2() != 0) {
                    L1ItemInstance main = _owner.getInventory().findItemId(item.getMainId2());
                    if (main != null) {
                        _owner.sendPackets(new S_ItemStatus(main, _owner, true, false));
                    }
                }
                if (item.getMainId3() != 0) {
                    L1ItemInstance main = _owner.getInventory().findItemId(item.getMainId3());
                    if (main != null) {
                        _owner.sendPackets(new S_ItemStatus(main, _owner, true, false));
                    }
                }

            }
        }

        // セットアイテムメソッドに変更。
        //removeSetItems(itemId);

        if (itemId == 423014) {
            _owner.stopAHRegeneration();
        }
        if (itemId == 423015) {
            _owner.stopSHRegeneration();
        }
        if (itemId == 20380) {
            _owner.stopHalloweenRegeneration();
        }
        if (itemId == 20077 || itemId == 20062 || itemId == 120077) {
            _owner.delInvis();
        }
        if (itemId == 20288) {
            _owner.sendPackets(new S_Ability(1, false));
        }
        if (itemId == 20281) {
            _owner.sendPackets(new S_Ability(2, false));
        }
        if (itemId == 20036) {
            _owner.sendPackets(new S_Ability(3, false));
        }
        /*if (itemId == 20284) {
			_owner.sendPackets(new S_Ability(5, false));
		}*/
        if (itemId == 20207) {
            _owner.sendPackets(new S_SkillIconBlessOfEva(_owner.getId(), 0));
        }
        if (itemId == 22200 || itemId == 22201 || itemId == 22202 || itemId == 22203) {// パプリオンアーマー
            _owner.stopPapuBlessing();
        }

        /*** 50レベルエリクサールーン ***/
        // アジャイルのエリクサールーン
        if (itemId == 222295) {
            switch (_owner.getType()) {
                // 君主
                case 0:
                    _owner.addDamageReductionByArmor(-3);
                    break;
                // ナイト//戦士
                case 7:
                case 1:
                    _owner.addMaxHp(-50);
                    break;
                // 妖精
                case 2:
                    _owner.addMaxMp(-50);
                    break;
                // ウィザード
                case 3:
                    _owner.addMpr(-3);
                    break;
                // すべてへ
                case 4:
                    _owner.getAC().addAc(3);
                    break;
                // 竜騎士
                case 5:
                    _owner.addHitup(-3);
                    break;
                // イリュージョニスト
                case 6:
                    _owner.addWeightReduction(-5);
                    break;
            }
        }
        // 体力のエリクサールーン
        if (itemId == 222296) {
            switch (_owner.getType()) {
                // 君主
                case 0:
                    _owner.addDamageReductionByArmor(-3);
                    break;
                // ナイト//戦士
                case 7:
                case 1:
                    _owner.addMaxHp(-50);
                    break;
                // 妖精
                case 2:
                    _owner.addMaxMp(-50);
                    break;
                // ウィザード
                case 3:
                    _owner.addMpr(-3);
                    break;
                // すべてへ
                case 4:
                    _owner.getAC().addAc(3);
                    break;
                // 竜騎士
                case 5:
                    _owner.addHitup(-3);
                    break;
                // イリュージョニスト
                case 6:
                    _owner.addWeightReduction(-5);
                    break;
            }
        }
        // 知識のエリクサールーン
        if (itemId == 222297) {
            switch (_owner.getType()) {
                // 君主
                case 0:
                    _owner.addDamageReductionByArmor(-3);
                    break;
                // ナイト//戦士
                case 7:
                case 1:
                    _owner.addMaxHp(-50);
                    break;
                // 妖精
                case 2:
                    _owner.addMaxMp(-50);
                    break;
                // ウィザード
                case 3:
                    _owner.addMpr(-3);
                    break;
                // すべてへ
                case 4:
                    _owner.getAC().addAc(3);
                    break;
                // 竜騎士
                case 5:
                    _owner.addHitup(-3);
                    break;
                // イリュージョニスト
                case 6:
                    _owner.addWeightReduction(-5);
                    break;
            }
        }
        // 知恵のエリクサールーン
        if (itemId == 222298) {
            switch (_owner.getType()) {
                // 君主
                case 0:
                    _owner.addDamageReductionByArmor(-3);
                    break;
                //ナイト//戦士
                case 7:
                case 1:
                    _owner.addMaxHp(-50);
                    break;
                // 妖精
                case 2:
                    _owner.addMaxMp(-50);
                    break;
                // ウィザード
                case 3:
                    _owner.addMpr(-3);
                    break;
                // すべてへ
                case 4:
                    _owner.getAC().addAc(3);
                    break;
                // 竜騎士
                case 5:
                    _owner.addHitup(-3);
                    break;
                // イリュージョニスト
                case 6:
                    _owner.addWeightReduction(-5);
                    break;
            }
        }
        //力のエリクサールーン
        if (itemId == 222299) {
            switch (_owner.getType()) {
                // 君主
                case 0:
                    _owner.addDamageReductionByArmor(-3);
                    break;
                // ナイト//戦士
                case 7:
                case 1:
                    _owner.addMaxHp(-50);
                    break;
                // 妖精
                case 2:
                    _owner.addMaxMp(-50);
                    break;
                // ウィザード
                case 3:
                    _owner.addMpr(-3);
                    break;
                // すべてへ
                case 4:
                    _owner.getAC().addAc(3);
                    break;
                // 竜騎士
                case 5:
                    _owner.addHitup(-3);
                    break;
                // イリュージョニスト
                case 6:
                    _owner.addWeightReduction(-5);
                    break;
            }
        }
        /*** 70レベルエリクサールーン ***/
        // アジャイルのエリクサールーン
        if (itemId == 222312) {
            switch (_owner.getType()) {
                // 君主
                case 0:
                    _owner.addDamageReductionByArmor(-3);
                    _owner.addDmgup(-2);
                    break;
                // ナイト
                case 1:
                    _owner.addMaxHp(-50);
                    _owner.addDmgup(-1);
                    break;
                // 妖精
                case 2:
                    _owner.addMaxMp(-50);
                    _owner.addBowDmgup(-1);
                    break;
                // ウィザード
                case 3:
                    _owner.addMpr(-3);
                    _owner.getAbility().addSp(-1);
                    break;
                // すべてへ
                case 4:
                    _owner.getAC().addAc(3);
                    _owner.addMaxMp(-30);
                    break;
                // 竜騎士
                case 5:
                    _owner.addHitup(-3);
                    _owner.addDamageReductionByArmor(-1);
                    break;
                // イリュージョニスト
                case 6:
                    _owner.addWeightReduction(-5);
                    _owner.addMaxHp(-50);
                    break;
                // 戦士
                case 7:
                    _owner.getResistance().addMr(-5);
                    _owner.addMaxHp(-50);
                    break;
            }
        }
        // 体力のエリクサールーン
        if (itemId == 222313) {
            switch (_owner.getType()) {
                // 君主
                case 0:
                    _owner.addDamageReductionByArmor(-3);
                    _owner.addDmgup(-2);
                    break;
                // ナイト
                case 1:
                    _owner.addMaxHp(-50);
                    _owner.addDmgup(-1);
                    break;
                // 妖精
                case 2:
                    _owner.addMaxMp(-50);
                    _owner.addBowDmgup(-1);
                    break;
                // ウィザード
                case 3:
                    _owner.addMpr(-3);
                    _owner.getAbility().addSp(-1);
                    break;
                // すべてへ
                case 4:
                    _owner.getAC().addAc(3);
                    _owner.addMaxMp(-30);
                    break;
                // 竜騎士
                case 5:
                    _owner.addHitup(-3);
                    _owner.addDamageReductionByArmor(-1);
                    break;
                // イリュージョニスト
                case 6:
                    _owner.addWeightReduction(-5);
                    _owner.addMaxHp(-50);
                    break;
                // 戦士
                case 7:
                    _owner.getResistance().addMr(-5);
                    _owner.addMaxHp(-50);
                    break;
            }
        }
        // 知識のエリクサールーン
        if (itemId == 222314) {
            switch (_owner.getType()) {
                // 君主
                case 0:
                    _owner.addDamageReductionByArmor(-3);
                    _owner.addDmgup(-2);
                    break;
                // ナイト
                case 1:
                    _owner.addMaxHp(-50);
                    _owner.addDmgup(-1);
                    break;
                // 妖精
                case 2:
                    _owner.addMaxMp(-50);
                    _owner.addBowDmgup(-1);
                    break;
                // ウィザード
                case 3:
                    _owner.addMpr(-3);
                    _owner.getAbility().addSp(-1);
                    break;
                // すべてへ
                case 4:
                    _owner.getAC().addAc(3);
                    _owner.addMaxMp(-30);
                    break;
                // 竜騎士
                case 5:
                    _owner.addHitup(-3);
                    _owner.addDamageReductionByArmor(-1);
                    break;
                // イリュージョニスト
                case 6:
                    _owner.addWeightReduction(-5);
                    _owner.addMaxHp(-50);
                    break;
                // 戦士
                case 7:
                    _owner.getResistance().addMr(-5);
                    _owner.addMaxHp(-50);
                    break;
            }
        }
        // 知恵のエリクサールーン
        if (itemId == 222315) {
            switch (_owner.getType()) {
                // 君主
                case 0:
                    _owner.addDamageReductionByArmor(-3);
                    _owner.addDmgup(-2);
                    break;
                // ナイト
                case 1:
                    _owner.addMaxHp(-50);
                    _owner.addDmgup(-1);
                    break;
                // 妖精
                case 2:
                    _owner.addMaxMp(-50);
                    _owner.addBowDmgup(-1);
                    break;
                // ウィザード
                case 3:
                    _owner.addMpr(-3);
                    _owner.getAbility().addSp(-1);
                    break;
                // すべてへ
                case 4:
                    _owner.getAC().addAc(3);
                    _owner.addMaxMp(-30);
                    break;
                // 竜騎士
                case 5:
                    _owner.addHitup(-3);
                    _owner.addDamageReductionByArmor(-1);
                    break;
                // イリュージョニスト
                case 6:
                    _owner.addWeightReduction(-5);
                    _owner.addMaxHp(-50);
                    break;
                // 戦士
                case 7:
                    _owner.getResistance().addMr(-5);
                    _owner.addMaxHp(-50);
                    break;
            }
        }
        // 力のエリクサールーン
        if (itemId == 222316) {
            switch (_owner.getType()) {
                // 君主
                case 0:
                    _owner.addDamageReductionByArmor(-3);
                    _owner.addDmgup(-2);
                    break;
                // ナイト
                case 1:
                    _owner.addMaxHp(-50);
                    _owner.addDmgup(-1);
                    break;
                // 妖精
                case 2:
                    _owner.addMaxMp(-50);
                    _owner.addBowDmgup(-1);
                    break;
                // ウィザード
                case 3:
                    _owner.addMpr(-3);
                    _owner.getAbility().addSp(-1);
                    break;
                // すべてへ
                case 4:
                    _owner.getAC().addAc(3);
                    _owner.addMaxMp(-30);
                    break;
                // 竜騎士
                case 5:
                    _owner.addHitup(-3);
                    _owner.addDamageReductionByArmor(-1);
                    break;
                // イリュージョニスト
                case 6:
                    _owner.addWeightReduction(-5);
                    _owner.addMaxHp(-50);
                    break;
                // 戦士
                case 7:
                    _owner.getResistance().addMr(-5);
                    _owner.addMaxHp(-50);
                    break;
            }
        }

        /** ウィザードのがより **/
        if (itemId == 22255) {
            switch (itemlvl) {
                case 5:
                case 6:
                    _owner.getAbility().addSp(-1);
                    break;
                case 7:
                case 8:
                    _owner.getAbility().addSp(-2);
                    break;
                case 9:
                    _owner.getAbility().addSp(-3);
                    break;
                default:
                    break;
            }
        }

        /** 体力のガーダー **/
        if (itemId == 22256) {
            switch (itemlvl) {
                case 5:
                case 6:
                    _owner.addMaxHp(-25);
                    break;
                case 7:
                case 8:
                    _owner.addMaxHp(-50);
                    break;
                case 9:
                    _owner.addMaxHp(-75);
                    break;
                default:
                    break;
            }
        }

        /** ナイトバルド両手剣スタンレベル+1**/
        if (itemId == 1121 || itemId == 11121) {
            switch (itemlvl) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                    _owner.getResistance().addStun(-1);
                    break;
                default:
                    break;
            }
        }
        /** タイタンの怒り技術発動HP区間5％増加した。**/
        if (itemId == 202014) {
            _owner.setLockSectionUp(-5);
        }

        /** 真冥王の執行剣スタンレベル+2**/
        if (itemId == 61 || itemId == 7000061 || itemId == 202012) {
            switch (itemlvl) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                    _owner.getResistance().addStun(-2);
                    break;
                default:
                    break;
            }
        }

        /** 古代闘士のガーダー **/
        if (itemId == 22003) {
            switch (itemlvl) {
                case 5:
                case 6:
                    _owner.addDmgup(-1);
                    break;
                case 7:
                case 8:
                    _owner.addDmgup(-2);
                    break;
                case 9:
                    _owner.addDmgup(-3);
                    break;
                default:
                    break;
            }
        }

        /** 古代名弓のガーダー **/
        if (itemId == 22000) {
            switch (itemlvl) {
                case 5:
                case 6:
                    _owner.addBowHitup(-1);
                    break;
                case 7:
                case 8:
                    _owner.addBowHitup(-1);
                    break;
                case 9:
                    _owner.addBowHitup(-1);
                    break;
                default:
                    break;
            }
        }
        /** マミーロードクラウン **/
        if (itemId == 20017) {
            switch (itemlvl) {
                case 7:
                    _owner.addBowHitup(-1);
                    break;
                case 8:
                    _owner.addBowHitup(-2);
                    break;
                case 9:
                    _owner.addBowHitup(-3);
                    break;
                default:
                    break;
            }
        }
        /** 守護のガーダー **/
        if (itemId == 22254) {
            switch (itemlvl) {
                case 5:
                case 6:
                    _owner.addDamageReductionByArmor(-1);
                    break;
                case 7:
                case 8:
                    _owner.addDamageReductionByArmor(-2);
                    break;
                case 9:
                    _owner.addDamageReductionByArmor(-3);
                    break;
                default:
                    break;
            }
        }
        /** アンタラス グランド シリーズ **/
        if (itemId == 22196 || itemId == 22197 || itemId == 22198 || itemId == 22199) {
            switch (itemlvl) {
                case 7:
                    _owner.addDamageReductionByArmor(-1);
                    break;
                case 8:
                    _owner.addDamageReductionByArmor(-2);
                    break;
                case 9:
                    _owner.addDamageReductionByArmor(-3);
                    break;
                default:
                    break;
            }
        }
        /** 輝く魔力の手袋 **/
        if (itemId == 20274) {
            switch (itemlvl) {
                case 5:
                    _owner.addWeightReduction(-1);
                    break;
                case 6:
                    _owner.addWeightReduction(-2);
                    break;
                case 7:
                    _owner.addWeightReduction(-3);
                    break;
                case 8:
                    _owner.addWeightReduction(-4);
                    break;
                case 9:
                    _owner.addWeightReduction(-5);
                    break;
                default:
                    break;
            }
        }
        /** 激怒の手袋 **/
        if (itemId == 222317) {
            switch (itemlvl) {
                case 5:
                    _owner.addHitup(-1);
                    _owner.addBowHitup(-1);
                    break;
                case 6:
                    _owner.addHitup(-2);
                    _owner.addBowHitup(-2);
                    break;
                case 7:
                    _owner.addHitup(-4);
                    _owner.addBowHitup(-4);
                    break;
                case 8:
                    _owner.addHitup(-5);
                    _owner.addBowHitup(-5);
                    break;
                case 9:
                    _owner.addHitup(-6);
                    _owner.addBowHitup(-6);
                    break;
                default:
                    break;
            }
        }

        // 古代の岩シリーズ
        if (itemId >= 900011 && itemId <= 900014) {
            _owner.addDamageReductionByArmor(-2);
            if (itemlvl >= 8) {
                _owner.addDamageReductionByArmor(-1);
                _owner.getResistance().addcalcPcDefense(-1);
            }
        }
        //古代魔物シリーズ
        if (itemId >= 900015 && itemId <= 900018) {
            _owner.addregist_PVPweaponTotalDamage(-2);
            if (itemlvl >= 8) {
                _owner.addDmgup(-1);
                _owner.addBowDmgup(-1);
            }
        }
        if (itemId == 900019) { // シルフのTシャツ
            switch (itemlvl) {
                case 0:
                    _owner.getResistance().addMr(-7);
                    _owner.getResistance().addStun(-7);
                    break;
                case 1:
                    _owner.getResistance().addStun(-8);
                    _owner.getResistance().addMr(-8);
                    break;
                case 2:
                    _owner.getResistance().addStun(-9);
                    _owner.getResistance().addMr(-9);
                    break;
                case 3:
                    _owner.getResistance().addStun(-10);
                    _owner.getResistance().addMr(-10);
                    break;
                case 4:
                    _owner.getResistance().addStun(-11);
                    _owner.getResistance().addMr(-11);
                    break;
                case 5:
                    _owner.getResistance().addStun(-12);
                    _owner.getResistance().addMr(-12);
                    break;
                case 6:
                    _owner.getResistance().addStun(-13);
                    _owner.getResistance().addMr(-13);
                    break;
                case 7:
                    _owner.getResistance().addStun(-14);
                    _owner.getResistance().addMr(-14);
                    break;
                case 8:
                    _owner.getResistance().addStun(-15);
                    _owner.getResistance().addMr(-15);
                    break;
                case 9:
                    _owner.getResistance().addStun(-16);
                    _owner.getResistance().addMr(-16);
                    break;
                case 10:
                    _owner.getResistance().addStun(-17);
                    _owner.getResistance().addMr(-17);
                    break;
                case 11:
                    _owner.getResistance().addStun(-18);
                    _owner.getResistance().addMr(-18);
                    break;
                case 12:
                    _owner.getResistance().addStun(-19);
                    _owner.getResistance().addMr(-19);
                    break;
                case 13:
                    _owner.getResistance().addStun(-20);
                    _owner.getResistance().addMr(-20);
                    break;
                case 14:
                    _owner.getResistance().addStun(-21);
                    _owner.getResistance().addMr(-21);
                    break;
                case 15:
                    _owner.getResistance().addStun(-22);
                    _owner.getResistance().addMr(-22);
                    break;
                default:
                    break;
            }
        }

        if (itemtype >= 8 && itemtype <= 12) {
            if ((itemgrade >= 0 && itemgrade <= 2) && (itemtype == 8 || itemtype == 12)) { // ネックレス、イヤリング
                switch (itemlvl) {
                    case 1:
                        _owner.addMaxHp(-5);
                        break;
                    case 2:
                        _owner.addMaxHp(-10);
                        break;
                    case 3:
                        _owner.addMaxHp(-20);
                        break;
                    case 4:
                        _owner.addMaxHp(-30);
                        break;
                    case 5:
                        _owner.addMaxHp(-40);
                        _owner.getAC().addAc(1);
                        break;
                    case 6:
                        _owner.addMaxHp(-40);
                        _owner.getAC().addAc(2);
                        break;
                    case 7:
                        _owner.addMaxHp(-50);
                        _owner.getAC().addAc(3);
                        _owner.getResistance().addStun(-2);
                        break;
                    case 8:
                        _owner.addMaxHp(-50);
                        _owner.getAC().addAc(4);
                        _owner.getResistance().addStun(-3);
                        break;
                    case 9:
                        _owner.addMaxHp(-60);
                        _owner.getAC().addAc(5);
                        _owner.getResistance().addStun(-4);
                        break;
                }
            } else if ((itemgrade >= 0 && itemgrade <= 2) && (itemtype == 9 || itemtype == 11)) { // リング
                switch (itemlvl) {
                    case 1:
                        _owner.addMaxHp(-5);
                        break;
                    case 2:
                        _owner.addMaxHp(-10);
                        break;
                    case 3:
                        _owner.addMaxHp(-20);
                        break;
                    case 4:
                        _owner.addMaxHp(-30);
                        break;
                    case 5:
                        _owner.addDmgup(-1);
                        _owner.addBowDmgup(-1);
                        _owner.addMaxHp(-40);
                        break;
                    case 6:
                        _owner.addDmgup(-2);
                        _owner.addBowDmgup(-2);
                        _owner.addMaxHp(-40);
                        _owner.getResistance().addMr(-1);
                        break;
                    case 7:
                        _owner.addDmgup(-3);
                        _owner.addBowDmgup(-3);
                        _owner.addMaxHp(-50);
                        _owner.getAbility().addSp(-1);
                        _owner.getResistance().addMr(-3);
                        _owner.getResistance().addPVPweaponTotalDamage(-1);
                        break;
                    case 8:
                        _owner.addDmgup(-4);
                        _owner.addBowDmgup(-4);
                        _owner.addMaxHp(-50);
                        _owner.getAbility().addSp(-2);
                        _owner.getResistance().addMr(-5);
                        _owner.getResistance().addPVPweaponTotalDamage(-2);
                        break;
                    case 9:
                        _owner.addDmgup(-5);
                        _owner.addBowDmgup(-5);
                        _owner.addMaxHp(-60);
                        _owner.getAbility().addSp(-3);
                        _owner.getResistance().addMr(-7);
                        _owner.getResistance().addPVPweaponTotalDamage(-3);
                        break;
                }
            } else if ((itemgrade >= 0 && itemgrade <= 2) && (itemtype == 10)) {
                switch (itemlvl) {
                    case 1:
                        _owner.addMaxMp(-5);
                        break;
                    case 2:
                        _owner.addMaxMp(-10);
                        break;
                    case 3:
                        _owner.addMaxMp(-20);
                        break;
                    case 4:
                        _owner.addMaxMp(-30);
                        break;
                    case 5:
                        _owner.addDamageReductionByArmor(-1);
                        _owner.addMaxMp(-40);
                        break;
                    case 6:
                        _owner.addDamageReductionByArmor(-2);
                        _owner.addMaxMp(-40);
                        _owner.addMaxHp(-20);
                        break;
                    case 7:
                        _owner.addDamageReductionByArmor(-3);
                        _owner.addMaxMp(-50);
                        _owner.addMaxHp(-30);
                        _owner.getResistance().addPVPweaponTotalDamage(-2);
                        break;
                    case 8:
                        _owner.addDamageReductionByArmor(-4);
                        _owner.addMaxMp(-50);
                        _owner.addMaxHp(-40);
                        _owner.getResistance().addPVPweaponTotalDamage(-3);
                        break;
                    case 9:
                        _owner.addDamageReductionByArmor(-5);
                        _owner.addMaxMp(-60);
                        _owner.addMaxHp(-50);
                        _owner.getResistance().addPVPweaponTotalDamage(-4);
                        break;
                }
                // スナップファーリング着用解除部分//スタン耐性の場合現全体と一般ユーザーの格差を減らすために579ですが - > 456に下方ヘトウム
            } else if (itemgrade == 3 && itemId >= 22224 && itemId <= 22228) {
                switch (itemlvl) {
                    case 1:
                        _owner.addMaxHp(-15);
                        break;
                    case 2:
                        _owner.addMaxHp(-20);
                        _owner.getAC().addAc(1);
                        break;
                    case 3:
                        _owner.addMaxHp(-25);
                        _owner.getAC().addAc(2);
                        break;
                    case 4:
                        _owner.addMaxHp(-30);
                        _owner.getAC().addAc(3);
                        break;
                    case 5:
                        _owner.addMaxHp(-35);
                        _owner.getAC().addAc(3);
                        _owner.addDmgup(-1);
                        _owner.addBowDmgup(-1);
                        break;
                    case 6:
                        _owner.addMaxHp(-40);
                        _owner.getAC().addAc(3);
                        _owner.addDmgup(-2);
                        _owner.addBowDmgup(-2);
                        _owner.getResistance().addStun(-4);
                        break;
                    case 7:
                        _owner.addMaxHp(-45);
                        _owner.getAC().addAc(3);
                        _owner.addDmgup(-3);
                        _owner.addBowDmgup(-3);
                        _owner.getResistance().addStun(-5);
                        break;
                    case 8:
                        _owner.addMaxHp(-50);
                        _owner.getAC().addAc(3);
                        _owner.addDmgup(-4);
                        _owner.addBowDmgup(-4);
                        _owner.getResistance().addStun(-6);
                        break;
                    default:
                        break;
                }
            } else if (itemgrade == 3 && itemId == 222290) { // 知恵のリング
                switch (itemlvl) {
                    case 1:
                        _owner.addMaxHp(-5);
                        break;
                    case 2:
                        _owner.addMaxHp(-10);
                        _owner.getAC().addAc(1);
                        break;
                    case 3:
                        _owner.addMaxHp(-15);
                        _owner.getAC().addAc(2);
                        break;
                    case 4:
                        _owner.addMaxHp(-20);
                        _owner.getAC().addAc(3);
                        break;
                    case 5:
                        _owner.addMaxHp(-25);
                        _owner.getAC().addAc(3);
                        _owner.getAbility().addSp(-1);
                        break;
                    case 6:
                        _owner.addMaxHp(-30);
                        _owner.getAC().addAc(3);
                        _owner.getAbility().addSp(-2);
                        _owner.getResistance().addStun(-4);
                        break;
                    case 7:
                        _owner.addMaxHp(-35);
                        _owner.getAC().addAc(3);
                        _owner.getAbility().addSp(-2);
                        _owner.getResistance().addStun(-5);
                        break;
                    case 8:
                        _owner.addMaxHp(-40);
                        _owner.getAC().addAc(3);
                        _owner.getAbility().addSp(-3);
                        _owner.getResistance().addStun(-6);
                        break;
                    default:
                        break;
                }
            } else if (itemgrade == 3 && itemId == 222291) {// 勇者のリング
                switch (itemlvl) {
                    case 1:
                        _owner.getAC().addAc(1);
                        break;
                    case 2:
                        _owner.getAC().addAc(2);
                        break;
                    case 3:
                        _owner.addMaxHp(-5);
                        _owner.getAC().addAc(3);
                        break;
                    case 4:
                        _owner.addMaxHp(-10);
                        _owner.getAC().addAc(4);
                        break;
                    case 5:
                        _owner.addMaxHp(-15);
                        _owner.getAC().addAc(4);
                        _owner.addHitup(-1);
                        _owner.addBowHitup(-1);
                        _owner.addDmgup(-1);
                        _owner.addBowDmgup(-1);
                        break;
                    case 6:
                        _owner.addMaxHp(-20);
                        _owner.getAC().addAc(4);
                        _owner.addHitup(-2);
                        _owner.addBowHitup(-2);
                        _owner.addDmgup(-2);
                        _owner.addBowDmgup(-2);
                        _owner.getResistance().addStun(-4);
                        break;
                    case 7:
                        _owner.addMaxHp(-25);
                        _owner.getAC().addAc(4);
                        _owner.addHitup(-3);
                        _owner.addBowHitup(-3);
                        _owner.addDmgup(-3);
                        _owner.addBowDmgup(-3);
                        _owner.getResistance().addStun(-5);
                        break;
                    case 8:
                        _owner.addMaxHp(-30);
                        _owner.getAC().addAc(4);
                        _owner.addHitup(-4);
                        _owner.addBowHitup(-4);
                        _owner.addDmgup(-4);
                        _owner.addBowDmgup(-4);
                        _owner.getResistance().addStun(-6);
                        break;
                    default:
                        break;
                }
                /** 祝福されたスナップ広がりの体力リング **/
            } else if (itemgrade == 3 && itemId == 222332) {
                switch (itemlvl) {
                    case 1:
                        _owner.addMaxHp(-15);
                        break;
                    case 2:
                        _owner.addMaxHp(-20);
                        _owner.getAC().addAc(1);
                        break;
                    case 3:
                        _owner.addMaxHp(-30);
                        _owner.getAC().addAc(2);
                        break;
                    case 4:
                        _owner.addMaxHp(-35);
                        _owner.getAC().addAc(3);
                        _owner.addDmgup(-1);
                        _owner.addBowDmgup(-1);
                        break;
                    case 5:
                        _owner.addMaxHp(-40);
                        _owner.getAC().addAc(3);
                        _owner.addDmgup(-2);
                        _owner.addBowDmgup(-2);
                        break;
                    case 6:
                        _owner.addMaxHp(-45);
                        _owner.getAC().addAc(3);
                        _owner.addDmgup(-3);
                        _owner.addBowDmgup(-3);
                        _owner.getResistance().addStun(-4);
                        break;
                    case 7:
                        _owner.addMaxHp(-55);
                        _owner.getAC().addAc(4);
                        _owner.addDmgup(-4);
                        _owner.addBowDmgup(-4);
                        _owner.getResistance().addStun(-5);
                        break;
                    case 8:
                        _owner.addMaxHp(-65);
                        _owner.getAC().addAc(4);
                        _owner.addDmgup(-5);
                        _owner.addBowDmgup(-5);
                        _owner.getResistance().addStun(-6);
                        break;
                    default:
                        break;
                }
                /** 祝福されたスナップファーの回復、集中、マナリング **/
            } else if (itemgrade == 3 && itemId == 222330 || itemId == 222331 || itemId == 222333) {
                switch (itemlvl) {
                    case 1:
                        _owner.addMaxHp(-15);
                        break;
                    case 2:
                        _owner.addMaxHp(-20);
                        _owner.getAC().addAc(1);
                        break;
                    case 3:
                        _owner.addMaxHp(-30);
                        _owner.getAC().addAc(2);
                        break;
                    case 4:
                        _owner.addMaxHp(-35);
                        _owner.getAC().addAc(3);
                        _owner.addDmgup(-1);
                        _owner.addBowDmgup(-1);
                        break;
                    case 5:
                        _owner.addMaxHp(-40);
                        _owner.getAC().addAc(3);
                        _owner.addDmgup(-2);
                        _owner.addBowDmgup(-2);
                        break;
                    case 6:
                        _owner.addMaxHp(-45);
                        _owner.getAC().addAc(3);
                        _owner.addDmgup(-3);
                        _owner.addBowDmgup(-3);
                        _owner.getResistance().addStun(-4);
                        break;
                    case 7:
                        _owner.addMaxHp(-50);
                        _owner.getAC().addAc(3);
                        _owner.addDmgup(-4);
                        _owner.addBowDmgup(-4);
                        _owner.getResistance().addStun(-5);
                        break;
                    case 8:
                        _owner.addMaxHp(-50);
                        _owner.getAC().addAc(3);
                        _owner.addDmgup(-5);
                        _owner.addBowDmgup(-5);
                        _owner.getResistance().addStun(-6);
                        break;
                    default:
                        break;
                }
                /** 祝福されたスナップ広がりの魔法抵抗リング **/
            } else if (itemgrade == 3 && itemId == 222334) {
                switch (itemlvl) {
                    case 1:
                        _owner.addMaxHp(-15);
                        break;
                    case 2:
                        _owner.addMaxHp(-20);
                        _owner.getAC().addAc(1);
                        break;
                    case 3:
                        _owner.addMaxHp(-30);
                        _owner.getAC().addAc(2);
                        break;
                    case 4:
                        _owner.addMaxHp(-35);
                        _owner.getAC().addAc(3);
                        _owner.addDmgup(-1);
                        _owner.addBowDmgup(-1);
                        break;
                    case 5:
                        _owner.addMaxHp(-40);
                        _owner.getAC().addAc(3);
                        _owner.addDmgup(-2);
                        _owner.addBowDmgup(-2);
                        break;
                    case 6:
                        _owner.addMaxHp(-45);
                        _owner.getAC().addAc(3);
                        _owner.addDmgup(-3);
                        _owner.addBowDmgup(-3);
                        _owner.getResistance().addMr(-1);
                        _owner.getResistance().addStun(-4);
                        break;
                    case 7:
                        _owner.addMaxHp(-50);
                        _owner.getAC().addAc(4);
                        _owner.addDmgup(-4);
                        _owner.addBowDmgup(-4);
                        _owner.getResistance().addMr(-2);
                        _owner.getResistance().addStun(-5);
                        break;
                    case 8:
                        _owner.addMaxHp(-50);
                        _owner.getAC().addAc(4);
                        _owner.addDmgup(-5);
                        _owner.addBowDmgup(-5);
                        _owner.getResistance().addMr(-3);
                        _owner.getResistance().addStun(-6);
                        break;
                    default:
                        break;
                }
                /** 祝福されたスナップ広がりの知恵リング **/
            } else if (itemgrade == 3 && itemId == 222335) {
                switch (itemlvl) {
                    case 1:
                        _owner.addMaxHp(-5);
                        break;
                    case 2:
                        _owner.addMaxHp(-10);
                        _owner.getAC().addAc(1);
                        break;
                    case 3:
                        _owner.addMaxHp(-20);
                        _owner.getAC().addAc(2);
                        break;
                    case 4:
                        _owner.addMaxHp(-25);
                        _owner.getAC().addAc(3);
                        _owner.getAbility().addSp(-1);
                        break;
                    case 5:
                        _owner.addMaxHp(-30);
                        _owner.getAC().addAc(3);
                        _owner.getAbility().addSp(-2);
                        break;
                    case 6:
                        _owner.addMaxHp(-35);
                        _owner.getAC().addAc(3);
                        _owner.getAbility().addSp(-2);
                        _owner.getResistance().addStun(-4);
                        break;
                    case 7:
                        _owner.addMaxHp(-40);
                        _owner.getAC().addAc(4);
                        _owner.getAbility().addSp(-3);
                        _owner.getResistance().addStun(-5);
                        break;
                    case 8:
                        _owner.addMaxHp(-50);
                        _owner.getAC().addAc(4);
                        _owner.getAbility().addSp(-4);
                        _owner.getResistance().addStun(-6);
                        break;
                    default:
                        break;
                }
                /**祝福されたスナップ広がりの勇士リング **/
            } else if (itemgrade == 3 && itemId == 222336) {
                switch (itemlvl) {
                    case 1:
                        _owner.getAC().addAc(1);
                        break;
                    case 2:
                        _owner.getAC().addAc(2);
                        break;
                    case 3:
                        _owner.addMaxHp(-10);
                        _owner.getAC().addAc(3);
                        break;
                    case 4:
                        _owner.addMaxHp(-15);
                        _owner.getAC().addAc(4);
                        _owner.addHitup(-1);
                        _owner.addBowHitup(-1);
                        _owner.addDmgup(-1);
                        _owner.addBowDmgup(-1);
                        break;
                    case 5:
                        _owner.addMaxHp(-20);
                        _owner.getAC().addAc(4);
                        _owner.addHitup(-2);
                        _owner.addBowHitup(-2);
                        _owner.addDmgup(-2);
                        _owner.addBowDmgup(-2);
                        break;
                    case 6:
                        _owner.addMaxHp(-25);
                        _owner.getAC().addAc(4);
                        _owner.addHitup(-3);
                        _owner.addBowHitup(-3);
                        _owner.addDmgup(-3);
                        _owner.addBowDmgup(-3);
                        _owner.getResistance().addStun(-4);
                        break;
                    case 7:
                        _owner.addMaxHp(-30);
                        _owner.getAC().addAc(5);
                        _owner.addHitup(-4);
                        _owner.addBowHitup(-4);
                        _owner.addDmgup(-4);
                        _owner.addBowDmgup(-4);
                        _owner.getResistance().addStun(-5);
                        break;
                    case 8:
                        _owner.addMaxHp(-30);
                        _owner.getAC().addAc(5);
                        _owner.addHitup(-5);
                        _owner.addBowHitup(-5);
                        _owner.addDmgup(-5);
                        _owner.addBowDmgup(-5);
                        _owner.getResistance().addStun(-6);
                        break;
                    default:
                        break;
                }
            } else if (itemgrade == 4 && itemId == 222340) { // ルームティス ブラック イヤリング
                switch (itemlvl) {
                case 1:
                    _owner.getAC().addAc(1);
                    break;
                case 2:
                    _owner.getAC().addAc(2);
                    break;
                case 3:
                    _owner.getAC().addAc(3);
                    _owner.addDmgup(-1);
                    _owner.addBowDmgup(-1);
                    break;
                case 4:
                    _owner.getAC().addAc(4);
                    _owner.addDmgup(-1);
                    _owner.addBowDmgup(-1);
                    break;
                case 5:
                    _owner.getAC().addAc(5);
                    _owner.addDmgup(-2);
                    _owner.addBowDmgup(-2);
                    break;
                case 6:
                    _owner.getAC().addAc(6);
                    _owner.addDmgup(-3);
                    _owner.addBowDmgup(-3);
                    break;
                case 7:
                    _owner.getAC().addAc(7);
                    _owner.addDmgup(-4);
                    _owner.addBowDmgup(-4);
                    break;
                case 8:
                    _owner.getAC().addAc(8);
                    _owner.addDmgup(-5);
                    _owner.addBowDmgup(-5);
                    break;
                default:
                    break;
                }

            } else if (itemgrade == 4 && itemId == 222341) { // 祝福されたルームティス ブラック イヤリング
                switch (itemlvl) {
                case 3:
                    _owner.getAC().addAc(4);
                    _owner.addDmgup(-1);
                    _owner.addBowDmgup(-1);
                    break;
                case 4:
                    _owner.getAC().addAc(5);
                    _owner.addDmgup(-2);
                    _owner.addBowDmgup(-2);
                    break;
                case 5:
                    _owner.getAC().addAc(6);
                    _owner.addDmgup(-3);
                    _owner.addBowDmgup(-3);
                    break;
                case 6:
                    _owner.getAC().addAc(7);
                    _owner.addDmgup(-4);
                    _owner.addBowDmgup(-4);
                    break;
                case 7:
                    _owner.getAC().addAc(8);
                    _owner.addDmgup(-5);
                    _owner.addBowDmgup(-5);
                    break;
                case 8:
                    _owner.getAC().addAc(9);
                    _owner.addDmgup(-6);
                    _owner.addBowDmgup(-6);
                    break;
                default:
                    break;
                }

                /** 成長/回復の文章 **/
            } else if (itemgrade == 3 && itemId == 900020 || itemId == 900021) {
                switch (itemlvl) {
                    case 0:
                        _owner.getAC().addAc(-0);
                        break;
                    case 1:
                        _owner.getAC().addAc(-1);
                        break;
                    case 2:
                        _owner.getAC().addAc(-2);
                        break;
                    case 3:
                        _owner.getAC().addAc(-3);
                        break;
                    case 4:
                        _owner.getAC().addAc(-4);
                        break;
                    case 5:
                        _owner.getAC().addAc(-5);
                        break;
                    case 6:
                        _owner.getAC().addAc(-6);
                        break;
                    case 7:
                        _owner.getAC().addAc(-7);
                        break;
                    case 8:
                        _owner.getAC().addAc(-8);
                        break;
                    case 9:
                        _owner.getAC().addAc(-9);
                        break;
                    case 10:
                        _owner.getAC().addAc(-10);
                        break;
                    default:
                        break;
                }

            } else if (itemgrade == 4 && itemId == 22229) { // ルームティス レッド イアリング
                switch (itemlvl) {
                    case 1:
                        _owner.addMaxHp(-20);
                        break;
                    case 2:
                        _owner.addMaxHp(-30);
                        break;
                    case 3:
                        _owner.addMaxHp(-40);
                        _owner.addDamageReductionByArmor(-1);
                        break;
                    case 4:
                        _owner.addMaxHp(-50);
                        _owner.addDamageReductionByArmor(-1);
                        break;
                    case 5:
                        _owner.addMaxHp(-60);
                        _owner.addDamageReductionByArmor(-2);
                        break;
                    case 6:
                        _owner.getAC().addAc(7);
                        _owner.addMaxHp(-70);
                        _owner.addDamageReductionByArmor(-3);
                        break;
                    case 7:
                        _owner.getAC().addAc(8);
                        _owner.addHitup(-1);
                        _owner.addBowHitup(-1);
                        _owner.addMaxHp(-80);
                        _owner.addDamageReductionByArmor(-4);
                        break;
                    case 8:
                        _owner.getAC().addAc(9);
                        _owner.addHitup(-3);
                        _owner.addBowHitup(-3);
                        _owner.addMaxHp(-90);
                        _owner.addDamageReductionByArmor(-5);
                        break;
                    default:
                        break;
                }
            } else if (itemgrade == 4 && itemId == 222337) { // 祝福されたルームティス レッド イアリング
                switch (itemlvl) {
                    case 3:
                        _owner.addMaxHp(-50);
                        _owner.addDamageReductionByArmor(-1);
                        break;
                    case 4:
                        _owner.addMaxHp(-60);
                        _owner.addDamageReductionByArmor(-2);
                        break;
                    case 5:
                        _owner.getAC().addAc(7);
                        _owner.addMaxHp(-70);
                        _owner.addDamageReductionByArmor(-3);
                        break;
                    case 6:
                        _owner.getAC().addAc(8);
                        _owner.addHitup(-1);
                        _owner.addBowHitup(-1);
                        _owner.addMaxHp(-80);
                        _owner.addDamageReductionByArmor(-4);
                        break;
                    case 7:
                        _owner.getAC().addAc(9);
                        _owner.addHitup(-3);
                        _owner.addBowHitup(-3);
                        _owner.addMaxHp(-90);
                        _owner.addDamageReductionByArmor(-5);
                        break;
                    case 8:
                        _owner.getAC().addAc(10);
                        _owner.addHitup(-5);
                        _owner.addBowHitup(-5);
                        _owner.addMaxHp(-150);
                        _owner.addDamageReductionByArmor(-6);
                        break;
                    default:
                        break;
                }
            } else if (itemgrade == 4 && itemId == 22230) { // ルームティス ブルー イヤリング
                switch (itemlvl) {
                    case 5:
                        _owner.getAC().addAc(1);
                        break;
                    case 6:
                        _owner.getAC().addAc(2);
                        break;
                    case 7:
                        _owner.getAC().addAc(2);
                        break;
                    case 8:
                        _owner.getAC().addAc(3);
                        break;
                    default:
                        break;
                }
            } else if (itemgrade == 4 && itemId == 222338) { // 祝福されたルームティス ブルー イヤリング
                switch (itemlvl) {
                    case 4:
                        _owner.getAC().addAc(1);
                        break;
                    case 5:
                        _owner.getAC().addAc(2);
                        break;
                    case 6:
                        _owner.getAC().addAc(2);
                        break;
                    case 7:
                        _owner.getAC().addAc(3);
                        break;
                    case 8:
                        _owner.getAC().addAc(4);
                        break;
                    default:
                        break;
                }
            } else if (itemgrade == 4 && itemId == 22231) { // ルームティス パープル イヤリング
                switch (itemlvl) {
                    case 1:
                        _owner.addMaxMp(-10);
                        _owner.getResistance().addMr(-3);
                        break;
                    case 2:
                        _owner.addMaxMp(-15);
                        _owner.getResistance().addMr(-4);
                        break;
                    case 3:
                        _owner.addMaxMp(-30);
                        _owner.getResistance().addMr(-5);
                        _owner.getAbility().addSp(-1);
                        break;
                    case 4:
                        _owner.addMaxMp(-35);
                        _owner.getResistance().addMr(-6);
                        _owner.getAbility().addSp(-1);
                        break;
                    case 5:
                        _owner.addMaxMp(-50);
                        _owner.getResistance().addMr(-7);
                        _owner.getAbility().addSp(-2);
                        break;
                    case 6:
                        _owner.getAC().addAc(1);
                        _owner.addMaxMp(-55);
                        _owner.getResistance().addMr(-8);
                        _owner.getAbility().addSp(-2);
                        break;
                    case 7:
                        _owner.getAC().addAc(2);
                        _owner.addMaxMp(-70);
                        _owner.getResistance().addMr(-10);
                        _owner.getAbility().addSp(-3);
                        break;
                    case 8:
                        _owner.getAC().addAc(3);
                        _owner.addMaxMp(-95);
                        _owner.getResistance().addMr(-13);
                        _owner.getAbility().addSp(-3);
                        break;
                    default:
                        break;
                }
            } else if (itemgrade == 4 && itemId == 222339) { // 祝福されたルームティス パープル イヤリング
                switch (itemlvl) {
                    case 3:
                        _owner.addMaxMp(-35);
                        _owner.getResistance().addMr(-6);
                        _owner.getAbility().addSp(-1);
                        break;
                    case 4:
                        _owner.addMaxMp(-50);
                        _owner.getResistance().addMr(-7);
                        _owner.getAbility().addSp(-2);
                        break;
                    case 5:
                        _owner.getAC().addAc(1);
                        _owner.addMaxMp(-55);
                        _owner.getResistance().addMr(-8);
                        _owner.getAbility().addSp(-2);
                        break;
                    case 6:
                        _owner.getAC().addAc(2);
                        _owner.addMaxMp(-70);
                        _owner.getResistance().addMr(-10);
                        _owner.getAbility().addSp(-3);
                        break;
                    case 7:
                        _owner.getAC().addAc(3);
                        _owner.addMaxMp(-95);
                        _owner.getResistance().addMr(-13);
                        _owner.getAbility().addSp(-3);
                        break;
                    case 8:
                        _owner.getAC().addAc(4);
                        _owner.addMaxMp(-125);
                        _owner.getResistance().addMr(-18);
                        _owner.getAbility().addSp(-4);
                        break;
                    default:
                        break;
                }
            }
        }
        /** 用のTシャツ **/
        if (itemId == 900023) {
            switch (itemlvl) {
                case 5:
                    _owner.getResistance().addMr(-4);
                    break;
                case 6:
                    _owner.getResistance().addMr(-5);
                    break;
                case 7:
                    _owner.getResistance().addMr(-6);
                    break;
                case 8:
                    _owner.getResistance().addMr(-8);
                    break;
                case 9:
                    _owner.getResistance().addMr(-11);
                    break;
                case 10:
                    _owner.getResistance().addMr(-14);
                    break;
                case 11:
                    _owner.getResistance().addMr(-14);
                    break;
                default:
                    break;
            }
        } else if (itemId == 900024 || itemId == 900025 || itemId == 900026) {
            switch (itemlvl) {
                case 5:
                    _owner.getResistance().addStun(-8);
                    break;
                case 6:
                    _owner.getResistance().addStun(-9);
                    break;
                case 7:
                    _owner.getResistance().addStun(-10);
                    break;
                case 8:
                    _owner.getResistance().addStun(-12);
                    break;
                case 9:
                    _owner.getResistance().addStun(-15);
                    break;
                case 10:
                    _owner.getResistance().addStun(-18);
                    break;
                case 11:
                    _owner.getResistance().addStun(-18);
                    break;
                default:
                    break;
            }
        }
        if (itemId == 900023 && itemlvl >= 9)  //地竜のTシャツ
            _owner.addDamageReductionByArmor(-1);

        if (itemId == 900024 && itemlvl >= 9)  //火竜のTシャツ
            _owner.addDmgup(-1);

        if (itemId == 900025 && itemlvl >= 9)  //風竜Tシャツ
            _owner.addBowDmgup(-1);

        if (itemId == 900026)  //水竜のTシャツ
            switch (itemlvl) {
                case 9:
                    _owner.getAbility().addSp(-2);
                    break;
                default:
                    _owner.getAbility().addSp(-1);
                    break;
            }
        if ((itemId >= 900023 && itemId <= 900026) && itemlvl >= 10) {
            _owner.addMaxHp(-100);
            _owner.getResistance().addcalcPcDefense(-1);
            _owner.getResistance().addPVPweaponTotalDamage(-1);
        }

        //ユニゲートル
        if (itemId >= 900027 && itemId <= 900029 && itemlvl >= 9) {
            switch (itemId) {
                case 900027:
                    _owner.addBowDmgup(-1);
                    break;
                case 900028:
                    _owner.addDmgup(-1);
                    break;
                case 900029:
                    _owner.getAbility().addSp(-1);
                    break;
            }
        }
        /** 黒騎士のバイザーMR表示 **/
        /** 神聖なエルムの祝福MR表示 **/
        if (itemId == 222342 || itemId == 222344) {
            switch (itemlvl) {
                case 5:
                    _owner.getResistance().addMr(-4);
                    break;
                case 6:
                    _owner.getResistance().addMr(-8);
                    break;
                case 7:
                    _owner.getResistance().addMr(-12);
                    break;
                case 8:
                    _owner.getResistance().addMr(-16);
                    break;
                case 9:
                    _owner.getResistance().addMr(-20);
                    break;
                case 10:
                    _owner.getResistance().addMr(-24);
                    break;
                case 11:
                    _owner.getResistance().addMr(-28);
                    break;
                default:
                    break;
            }
        }
        /**シールドウィングのブレイサー遠距離命中 **/
        if (itemId == 222343) {
            switch (itemlvl) {
                case 5:
                    _owner.addBowHitRate(-1);
                    break;
                case 6:
                    _owner.addBowHitRate(-2);
                    break;
                case 7:
                    _owner.addBowHitRate(-3);
                    break;
                case 8:
                    _owner.addBowHitRate(-4);
                    break;
                case 9:
                    _owner.addBowHitRate(-5);
                    break;
                default:
                    break;
            }
        }
        /** シールドウィングのパワーグローブ近距離命中 **/
        if (itemId == 222345) {
            switch (itemlvl) {
                case 5:
                    _owner.addHitup(-1);
                    break;
                case 6:
                    _owner.addHitup(-2);
                    break;
                case 7:
                    _owner.addHitup(-3);
                    break;
                case 8:
                    _owner.addHitup(-4);
                    break;
                case 9:
                    _owner.addHitup(-5);
                    break;
                default:
                    break;
            }
        }
        /** 神聖な永遠のネックレススタン耐性 **/
        if (itemId == 222349) {
            _owner.getResistance().addStun(-7);
        }
        /** （知恵/機敏/知識/腕力）のブーツ *
         * :	+7から最大HP + 20 / + 40 / + 60増加
         * :	+9にダメージ減少+ 1を追加
         * */
        if (itemId == 22359 || itemId == 222308 || itemId == 222309 || itemId == 222307) {
            switch (itemlvl) {
                case 7:
                    _owner.addMaxHp(-20);
                    break;
                case 8:
                    _owner.addMaxHp(-40);
                    break;
                case 9:
                    _owner.addMaxHp(-60);
                    _owner.addDamageReductionByArmor(-1);
                    break;
                default:
                    break;
            }
        }
        /** 腕力の文章 **/
        if (itemId == 222352) {
            switch (itemlvl) {
                case 5:
                    _owner.addHitup(-1);
                    break;
                case 6:
                    _owner.addHitup(-1);
                    _owner.addDmgup(-1);
                    break;
                case 7:
                    _owner.addHitup(-2);
                    _owner.addDmgup(-2);
                    break;
                case 8:
                    _owner.addHitup(-3);
                    _owner.addDmgup(-3);
                    break;
                case 9:
                    _owner.addHitup(-4);
                    _owner.addDmgup(-4);
                    break;
                case 10:
                    _owner.addHitup(-5);
                    _owner.addDmgup(-5);
                    break;
                default:
                    break;
            }
        }
        /** 機敏の文章 **/
        if (itemId == 222353) {
            switch (itemlvl) {
                case 5:
                    _owner.addBowHitRate(-1);
                    break;
                case 6:
                    _owner.addBowHitRate(-1);
                    _owner.addBowDmgup(-1);
                    break;
                case 7:
                    _owner.addBowHitRate(-2);
                    _owner.addBowDmgup(-2);
                    break;
                case 8:
                    _owner.addBowHitRate(-3);
                    _owner.addBowDmgup(-3);
                    break;
                case 9:
                    _owner.addBowHitRate(-4);
                    _owner.addBowDmgup(-4);
                    break;
                case 10:
                    _owner.addBowHitRate(-5);
                    _owner.addBowDmgup(-5);
                    break;
                default:
                    break;
            }
        }
        /**知識の文章**/
        if (itemId == 222354) {
            switch (itemlvl) {
                case 6:
                    _owner.getAbility().addSp(-1);
                    break;
                case 7:
                    _owner.getAbility().addSp(-2);
                    break;
                case 8:
                    _owner.getAbility().addSp(-3);
                    break;
                case 9:
                    _owner.getAbility().addSp(-4);
                    break;
                case 10:
                    _owner.getAbility().addSp(-5);
                    break;
                default:
                    break;
            }
        }
        if (itemId == 900032 && itemgrade == 5) { //闘士のインシグニア
            switch (itemlvl) {
                case 0:
                    _owner.addMaxHp(-5);
                    break;
                case 1:
                    _owner.addMaxHp(-10);
                    break;
                case 2:
                    _owner.addMaxHp(-15);
                    break;
                case 3:
                    _owner.addMaxHp(-20);
                    break;
                case 4:
                    _owner.addMaxHp(-25);
                    _owner.getAC().addAc(1);
                    break;
                case 5:
                    _owner.addMaxHp(-30);
                    _owner.getAC().addAc(2);
                    _owner.addDmgup(-1);
                    break;
                case 6:
                    _owner.addMaxHp(-35);
                    _owner.getAC().addAc(3);
                    _owner.addDmgup(-2);
                    _owner.addDmgCritical(-1);
                    break;
                case 7:
                    _owner.addMaxHp(-40);
                    _owner.getAC().addAc(3);
                    _owner.addDmgup(-3);
                    _owner.addDmgCritical(-3);
                    break;
                case 8:
                    _owner.addMaxHp(-50);
                    _owner.getAC().addAc(3);
                    _owner.addDmgup(-4);
                    _owner.addDmgCritical(-5);
                    break;
            }
        }

        if (itemId == 900033 && itemgrade == 5) { //射手のインシグニア
            switch (itemlvl) {
                case 0:
                    _owner.addMaxHp(-5);
                    break;
                case 1:
                    _owner.addMaxHp(-10);
                    break;
                case 2:
                    _owner.addMaxHp(-15);
                    break;
                case 3:
                    _owner.addMaxHp(-20);
                    break;
                case 4:
                    _owner.addMaxHp(-25);
                    _owner.getAC().addAc(1);
                    break;
                case 5:
                    _owner.addMaxHp(-30);
                    _owner.getAC().addAc(2);
                    _owner.addBowDmgup(-1);
                    break;
                case 6:
                    _owner.addMaxHp(-35);
                    _owner.getAC().addAc(3);
                    _owner.addBowDmgup(-2);
                    _owner.addBowDmgCritical(-1);
                    break;
                case 7:
                    _owner.addMaxHp(-40);
                    _owner.getAC().addAc(3);
                    _owner.addBowDmgup(-3);
                    _owner.addBowDmgCritical(-3);
                    break;
                case 8:
                    _owner.addMaxHp(-50);
                    _owner.getAC().addAc(3);
                    _owner.addBowDmgup(-4);
                    _owner.addBowDmgCritical(-5);
                    break;
            }
        }
        if (itemId == 900034 && itemgrade == 5) { //賢者のインシグニア
            switch (itemlvl) {
                case 0:
                    _owner.addMaxHp(-5);
                    break;
                case 1:
                    _owner.addMaxHp(-10);
                    break;
                case 2:
                    _owner.addMaxHp(-15);
                    break;
                case 3:
                    _owner.addMaxHp(-20);
                    break;
                case 4:
                    _owner.addMaxHp(-25);
                    _owner.getAC().addAc(1);
                    break;
                case 5:
                    _owner.addMaxHp(-30);
                    _owner.getAC().addAc(2);
                    _owner.addHitup(-1);
                    break;
                case 6:
                    _owner.addMaxHp(-35);
                    _owner.getAC().addAc(3);
                    _owner.addHitup(-2);
                    _owner.addMagicCritical(-1);
                    break;
                case 7:
                    _owner.addMaxHp(-40);
                    _owner.getAC().addAc(3);
                    _owner.addHitup(-3);
                    _owner.addMagicCritical(-2);
                    break;
                case 8:
                    _owner.addMaxHp(-50);
                    _owner.getAC().addAc(3);
                    _owner.addHitup(-4);
                    _owner.addMagicCritical(-4);
                    break;
            }
        }
        if (itemId == 900035 && itemgrade == 5) { //守護のインシグニア
            switch (itemlvl) {
                case 0:
                    _owner.addMaxHp(-5);
                    break;
                case 1:
                    _owner.addMaxHp(-10);
                    break;
                case 2:
                    _owner.addMaxHp(-15);
                    break;
                case 3:
                    _owner.addMaxHp(-20);
                    _owner.getAC().addAc(1);
                    break;
                case 4:
                    _owner.addMaxHp(-25);
                    _owner.getAC().addAc(2);
                    break;
                case 5:
                    _owner.addMaxHp(-30);
                    _owner.getAC().addAc(3);
                    _owner.addDamageReductionByArmor(-1);
                    break;
                case 6:
                    _owner.addMaxHp(-35);
                    _owner.getAC().addAc(5);
                    _owner.addDamageReductionByArmor(-2);
                    _owner.getResistance().addMr(-3);

                    break;
                case 7:
                    _owner.addMaxHp(-40);
                    _owner.getAC().addAc(6);
                    _owner.addDamageReductionByArmor(-3);
                    _owner.getResistance().addMr(-5);
                    break;
                case 8:
                    _owner.addMaxHp(-50);
                    _owner.getAC().addAc(7);
                    _owner.addDamageReductionByArmor(-4);
                    _owner.getResistance().addMr(-7);
                    break;
            }
        }
        armor.stopEquipmentTimer(_owner);
        _armors.remove(armor);
    }

    public void set(L1ItemInstance equipment) {
        L1Item item = equipment.getItem();

        if (item.getType2() == 0) {
            return;
        }

        _owner.addMaxHp(item.get_addhp());
        _owner.addMaxMp(item.get_addmp());
        _owner.getAbility().addAddedStr(item.get_addstr());
        _owner.getAbility().addAddedCon(item.get_addcon());
        _owner.getAbility().addAddedDex(item.get_adddex());
        _owner.getAbility().addAddedInt(item.get_addint());
        _owner.getAbility().addAddedWis(item.get_addwis());
        if (item.get_addwis() != 0) {
            _owner.resetBaseMr();
        }
        _owner.getAbility().addAddedCha(item.get_addcha());

        int addMr = 0;
        addMr += equipment.getMr();
        if (item.getItemId() == 20236 && _owner.isElf()) {
            addMr += 5;
        }
        if (addMr != 0) {
            _owner.getResistance().addMr(addMr);
            _owner.sendPackets(new S_SPMR(_owner));
        }
        if (item.get_addsp() != 0) {
            _owner.getAbility().addSp(item.get_addsp());
            _owner.sendPackets(new S_SPMR(_owner));
        }
        if (item.isHasteItem()) {
            _owner.addHasteItemEquipped(1);
            _owner.removeHasteSkillEffect();
            if (_owner.getMoveSpeed() != 1) {
                _owner.setMoveSpeed(1);
                _owner.sendPackets(new S_SkillHaste(_owner.getId(), 1, -1));
                _owner.broadcastPacket(new S_SkillHaste(_owner.getId(), 1, 0));
            }
        }
        if (item.getItemId() == 20383) {
            if (_owner.hasSkillEffect(STATUS_BRAVE)) {
                _owner.killSkillEffectTimer(STATUS_BRAVE);
                _owner.sendPackets(new S_SkillBrave(_owner.getId(), 0, 0));
                _owner.broadcastPacket(new S_SkillBrave(_owner.getId(), 0, 0));
                _owner.setBraveSpeed(0);
            }
        }
        _owner.getEquipSlot().setMagicHelm(equipment);

        if (item.getType2() == 1) {
            setWeapon(equipment);
        } else if (item.getType2() == 2) {
            setArmor(equipment);
            _owner.sendPackets(new S_SPMR(_owner));
        }
    }

    public void remove(L1ItemInstance equipment) {
        L1Item item = equipment.getItem();
        if (item.getType2() == 0) {
            return;
        }

        _owner.addMaxHp(-item.get_addhp());
        _owner.addMaxMp(-item.get_addmp());
        _owner.getAbility().addAddedStr((byte) -item.get_addstr());
        _owner.getAbility().addAddedCon((byte) -item.get_addcon());
        _owner.getAbility().addAddedDex((byte) -item.get_adddex());
        _owner.getAbility().addAddedInt((byte) -item.get_addint());
        _owner.getAbility().addAddedWis((byte) -item.get_addwis());
        if (item.get_addwis() != 0) {
            _owner.resetBaseMr();
        }
        _owner.getAbility().addAddedCha((byte) -item.get_addcha());

        int addMr = 0;
        addMr -= equipment.getMr();
        if (item.getItemId() == 20236 && _owner.isElf()) {
            addMr -= 5;
        }
        if (addMr != 0) {
            _owner.getResistance().addMr(addMr);
            _owner.sendPackets(new S_SPMR(_owner));
        }
        if (item.get_addsp() != 0) {
            _owner.getAbility().addSp(-item.get_addsp());
            _owner.sendPackets(new S_SPMR(_owner));
        }
        if (item.isHasteItem()) {
            _owner.addHasteItemEquipped(-1);
            if (_owner.getHasteItemEquipped() == 0) {
                _owner.setMoveSpeed(0);
                _owner.sendPackets(new S_SkillHaste(_owner.getId(), 0, 0));
                _owner.broadcastPacket(new S_SkillHaste(_owner.getId(), 0, 0));
            }
        }
        _owner.getEquipSlot().removeMagicHelm(_owner.getId(), equipment);

        if (item.getType2() == 1) {
            removeWeapon(equipment);
            if (_owner.hasSkillEffect(DANCING_BLADES)) {
                _owner.sendPackets(new S_SkillIconAura(154, 0));
                _owner.removeSkillEffect(DANCING_BLADES);

            }
        } else if (item.getType2() == 2) {
            removeArmor(equipment);
        }
    }

    public void setMagicHelm(L1ItemInstance item) {
        if (item.getItemId() == 20013) {
            _owner.sendPackets(new S_AddSkill(0, 0, 0, 2, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        }
        if (item.getItemId() == 20014) {
            _owner.sendPackets(new S_AddSkill(1, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        }
        if (item.getItemId() == 20015) {
            _owner.sendPackets(new S_AddSkill(0, 24, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        }
        if (item.getItemId() == 20008) {
            _owner.sendPackets(new S_AddSkill(0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        }
        if (item.getItemId() == 20023) {
            _owner.sendPackets(new S_AddSkill(0, 0, 0, 0, 0, 0, 32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        }
    }

    public void removeMagicHelm(int objectId, L1ItemInstance item) {
        if (item.getItemId() == 20013) {
            if (!SkillsTable.getInstance().spellCheck(objectId, 26)) {
                _owner.sendPackets(new S_DelSkill(0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
            }
            if (!SkillsTable.getInstance().spellCheck(objectId, 43)) {
                _owner.sendPackets(new S_DelSkill(0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
            }
        }
        if (item.getItemId() == 20014) {
            if (!SkillsTable.getInstance().spellCheck(objectId, 1)) {
                _owner.sendPackets(new S_DelSkill(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
            }
            if (!SkillsTable.getInstance().spellCheck(objectId, 19)) {
                _owner.sendPackets(new S_DelSkill(0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
            }
        }
        if (item.getItemId() == 20015) {
            if (!SkillsTable.getInstance().spellCheck(objectId, 12)) {
                _owner.sendPackets(new S_DelSkill(0, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
            }
            if (!SkillsTable.getInstance().spellCheck(objectId, 13)) {
                _owner.sendPackets(new S_DelSkill(0, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
            }
            if (!SkillsTable.getInstance().spellCheck(objectId, 42)) {
                _owner.sendPackets(new S_DelSkill(0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
            }
        }
        if (item.getItemId() == 20008) {
            if (!SkillsTable.getInstance().spellCheck(objectId, 43)) {
                _owner.sendPackets(new S_DelSkill(0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
            }
        }
        if (item.getItemId() == 20023) {
            if (!SkillsTable.getInstance().spellCheck(objectId, 54)) {
                _owner.sendPackets(new S_DelSkill(0, 0, 0, 0, 0, 0, 32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
            }
        }
    }

    private static void weaponRange(L1PcInstance pc) {
		// TODO Auto-generated method stub
		 int range = 1;
		 int type = 1;
		 boolean ck = false;
		 L1ItemInstance weapon = pc.getWeapon();
		 if(weapon != null){
			 if(weapon.getItem().getType() == 4) { //양손활
				 range = 17;
			 }else if (weapon.getItem().getType() == 13) { //한손활
				 range = 14;
			 }else if ((weapon.getItem().getType() == 24) || (weapon.getItem().getType() == 14) || (weapon.getItem().getType() == 18)) { //창과 체인소드
				 range = 1;
				 int polyId = pc.getTempCharGfx();
				 //변신에 따라 거리 2로 조절
				 if ((polyId == 15539) || (polyId == 15537) || (polyId == 15534) || (polyId == 15834)
						 || (polyId == 15599) || (polyId == 11447) || (polyId == 11351) || (polyId == 11368) || (polyId == 11344) || (polyId == 11330)//変身リスト スピアー
						 || (polyId == 15531) || (polyId == 15833) || (polyId == 15832) //変身リスト その他
						 || (polyId == 0) || (polyId == 1) || (polyId == 61) || (polyId == 48) || (polyId == 138) || (polyId == 37) || (polyId == 734)
						 || (polyId == 1186) || (polyId == 2786) || (polyId == 2796) || (polyId == 6658) || (polyId == 6661) || (polyId == 6671) || (polyId == 6650)
						 || (polyId == 12490) || (polyId == 12494) //各クラス男女未変身
					 	 || (polyId == 13715) || (polyId == 13717) || (polyId == 15115) || (polyId == 13721) || (polyId == 13723) || (polyId == 13725) || (polyId == 13727)
					 	 || (polyId == 13729) || (polyId == 13731) || (polyId == 13733) || (polyId == 13735) || (polyId == 13737) || (polyId == 13739) || (polyId == 13741)
					 	 || (polyId == 13743) || (polyId == 13745)) { // 各クラス男女ランカー変身
					 range = 2;
				 }else if (!pc.hasSkillEffect(L1SkillId.SHAPE_CHANGE)) {
					 //노변신시에도 거리를 2로 해줘야한다.
					 range = 2;
				 }
				 System.out.println("equipspot : " + weapon.getItem().getType1() + " AR: "+ range);
			 }
			 if(pc.isKnight()){
				 if(weapon.getItem().getType() == 3) { //양손검
					 ck = true;
				 }
			 }else if(pc.isElf()){
				 if(pc.isDancingBlades()){
					 ck = true;
				 }
				 if((weapon.getItem().getType() == 4 ||  weapon.getItem().getType() == 13 ) && weapon.getItem().getType1() == 20){
					 type = 3;
					 ck = true;
				 }
			 }else if(pc.isDragonknight()){
				 ck = true;
				 if((weapon.getItem().getType() == 14) || (weapon.getItem().getType() == 18)){
					 type = 10;
				 }
			 }
			 if (weapon.getItem().getType1() != 20 && weapon.getItem().getType1() != 62) {
				 pc.sendPackets(new S_PacketBox(S_PacketBox.ATTACKABLE_DISTANCE, range, type, ck));
				 System.out.println("1");
			 }else{
				 pc.sendPackets(new S_PacketBox(S_PacketBox.ATTACKABLE_DISTANCE, range, 3, ck));
				 System.out.println("2");
			 }
		 }else{
			 pc.sendPackets(new S_PacketBox(S_PacketBox.ATTACKABLE_DISTANCE, 1, 0, ck));
			 System.out.println("3");
		 }
		 pc.setRange(range);
		// System.out.println("WT : " + weapon.getItem().getType1() + " AR: "+ pc.getRange());
	}
    /**
     * セットアイテムを解除
     *
     * @param itemId
     */
    public void removeSetItems(int itemId) {
        for (L1ArmorSet armorSet : L1ArmorSet.getAllSet()) {
            if (armorSet.isPartOfSet(itemId) && _currentArmorSet.contains(armorSet) && !armorSet.isValid(_owner)) {
                armorSet.cancelEffect(_owner);
                _currentArmorSet.remove(armorSet);
            }
        }
    }
}