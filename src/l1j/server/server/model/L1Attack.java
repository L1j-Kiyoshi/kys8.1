package l1j.server.server.model;

import static l1j.server.server.model.skill.L1SkillId.*;

import java.util.Random;

import l1j.server.Config;
import l1j.server.GameSystem.Boss.BossAlive;
import l1j.server.GameSystem.Robot.L1RobotInstance;
import l1j.server.server.ActionCodes;
import l1j.server.server.Controller.WarTimeController;
import l1j.server.server.datatables.CharacterBalance;
import l1j.server.server.datatables.CharacterHitRate;
import l1j.server.server.datatables.CharacterReduc;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.datatables.WeaponAddDamage;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.gametime.L1GameTimeClock;
import l1j.server.server.model.poison.L1DamagePoison;
import l1j.server.server.model.poison.L1ParalysisPoison;
import l1j.server.server.model.poison.L1SilencePoison;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_AttackCritical;
import l1j.server.server.serverpackets.S_AttackMissPacket;
import l1j.server.server.serverpackets.S_AttackPacket;
import l1j.server.server.serverpackets.S_AttackPacketForNpc;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_NewSkillIcon;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_UseArrowSkill;
import l1j.server.server.serverpackets.S_UseAttackSkill;
import l1j.server.server.types.Point;
import l1j.server.server.utils.CalcStat;
import l1j.server.server.utils.CommonUtil;

public class L1Attack {

    private L1PcInstance _pc = null;

    private L1Character _target = null;

    private L1PcInstance _targetPc = null;

    private L1NpcInstance _npc = null; 

    private L1NpcInstance _targetNpc = null;

    private final int _targetId;

    private int _targetX;

    private int _targetY;

    private int _statusDamage = 0;

    private static final Random _random = new Random(System.nanoTime());

    private int _hitRate = 0;

    private int _calcType;

    private static final int PC_PC = 1;

    private static final int PC_NPC = 2;

    private static final int NPC_PC = 3;

    private static final int NPC_NPC = 4;

    public boolean _isHit = false;

    public boolean _isCritical = false;

    private int _damage = 0;

    private int _drainMana = 0;

    /** ゾウのストーンゴーレム **/

    private int _drainHp = 0;

    /** ゾウのストーンゴーレム **/

    private int _attckGrfxId = 0;

    private int _attckActId = 0;

    // 攻撃者がプレイヤーの場合の武器情報
    private L1ItemInstance weapon = null;

    private L1ItemInstance armor = null;
    // 戦士両手
    private L1ItemInstance Sweapon = null;// セカンドウェポン
    private int _SweaponId = 0;
    private int _SweaponType = 0;
    private int _SweaponType1 = 0;
    private int _SweaponAddHit = 0;
    private int _SweaponAddDmg = 0;
    private int _SweaponSmall = 0;
    private int _SweaponLarge = 0;
    private int _SweaponRange = 1;
    private int _SweaponBless = 1;
    private int _SweaponEnchant = 0;
    private int _SweaponMaterial = 0;
    private int _SweaponAttrEnchantLevel = 0;

    private int _weaponId = 0;

    private int _weaponType = 0;

    private int _weaponType2 = 0;

    // private int _weaponType1 = 0;

    private int _weaponAddHit = 0;

    private int _weaponAddDmg = 0;

    private int _weaponSmall = 0;

    private int _weaponLarge = 0;

    private int _weaponBless = 1;

    private int _weaponEnchant = 0;

    private int _weaponMaterial = 0;

    private int _weaponDoubleDmgChance = 0;
    
    private int _ignorereductionbyweapon = 0;
    
    private int _ignorereductionbyarmor = 0;

    private int _weaponAttrLevel = 0; //属性レベル

    private int _attackType = 0;

    private L1ItemInstance _arrow = null;

    private L1ItemInstance _sting = null;

    private int _leverage = 10; // 1/10倍表現する。

    public void setLeverage(int i) {
        _leverage = i;
    }

    private int getLeverage() {
        return _leverage;
    }

    private static final int[] strHit = new int[128];

    static {
        for (int str = 0; str <= 7; str++) {
            strHit[str] = -2;
        }
        strHit[8] = -1;
        strHit[9] = -1;
        strHit[10] = 0;
        strHit[11] = 0;
        strHit[12] = 1;
        strHit[13] = 1;
        strHit[14] = 2;
        strHit[15] = 2;
        strHit[16] = 3;
        strHit[17] = 3;
        strHit[18] = 4;
        strHit[19] = 4;
        strHit[20] = 4;
        strHit[21] = 5;
        strHit[22] = 5;
        strHit[23] = 5;
        strHit[24] = 6;
        strHit[25] = 6;
        strHit[26] = 6;
        strHit[27] = 7;
        strHit[28] = 7;
        strHit[29] = 7;
        strHit[30] = 8;
        strHit[31] = 8;
        strHit[32] = 8;
        strHit[33] = 9;
        strHit[34] = 9;
        strHit[35] = 9;
        strHit[36] = 10;
        strHit[37] = 10;
        strHit[38] = 10;
        strHit[39] = 11;
        strHit[40] = 11;
        strHit[41] = 11;
        strHit[42] = 12;
        strHit[43] = 12;
        strHit[44] = 12;
        strHit[45] = 13;
        strHit[46] = 13;
        strHit[47] = 13;
        strHit[48] = 14;
        strHit[49] = 14;
        strHit[50] = 14;
        strHit[51] = 15;
        strHit[52] = 15;
        strHit[53] = 15;
        strHit[54] = 16;
        strHit[55] = 16;
        strHit[56] = 16;
        strHit[57] = 17;
        strHit[57] = 17;
        strHit[58] = 17;
        int Hit = 18;
        for (int str = 59; str <= 127; str++) { // 59~127は3ごとに+1
            if (str % 3 == 1) {
                Hit++;
            }
            strHit[str] = Hit;
        }
    }

    private static final int[] dexHit = new int[128];

    static {
        // DEXダメージ補正
        for (int dex = 0; dex <= 6; dex++) {
            // 0~11は0
            dexHit[dex] = -2;
        }
        dexHit[7] = -1;
        dexHit[8] = -1;
        dexHit[9] = 0;
        dexHit[10] = 0;
        dexHit[11] = 1;
        dexHit[12] = 1;
        dexHit[13] = 2;
        dexHit[14] = 2;
        dexHit[15] = 3;
        dexHit[16] = 3;
        dexHit[17] = 4;
        dexHit[18] = 4;
        dexHit[19] = 5;
        dexHit[20] = 6;
        dexHit[21] = 7;
        dexHit[22] = 8;
        dexHit[23] = 9;
        dexHit[24] = 10;
        dexHit[25] = 11;
        dexHit[26] = 12;
        dexHit[27] = 13;
        dexHit[28] = 14;
        dexHit[29] = 15;
        dexHit[30] = 16;
        dexHit[31] = 17;
        dexHit[32] = 18;
        dexHit[33] = 19;
        dexHit[34] = 19;
        dexHit[35] = 19;
        dexHit[36] = 20;
        dexHit[37] = 20;
        dexHit[38] = 20;
        dexHit[39] = 21;
        dexHit[40] = 21;
        dexHit[41] = 21;
        dexHit[42] = 22;
        dexHit[43] = 22;
        dexHit[44] = 22;
        dexHit[45] = 23;
        dexHit[46] = 23;
        dexHit[47] = 23;
        dexHit[48] = 24;
        dexHit[49] = 24;
        dexHit[50] = 24;
        dexHit[51] = 25;
        dexHit[52] = 25;
        dexHit[53] = 25;
        dexHit[54] = 26;
        dexHit[55] = 26;
        dexHit[56] = 26;
        dexHit[57] = 27;
        dexHit[58] = 27;
        dexHit[59] = 27;
        dexHit[60] = 28;
        dexHit[61] = 28;
        dexHit[62] = 28;
        dexHit[63] = 29;
        dexHit[64] = 29;
        dexHit[65] = 29;
        dexHit[66] = 30;
        dexHit[67] = 30;
        dexHit[68] = 30;

        int hit = 31;
        for (int dex = 69; dex <= 127; dex++) { // 48~127は3ごとに+1
            if (dex % 3 == 1) {
                hit++;
            }
            dexHit[dex] = hit;
        }
    }

    private static final int[] strDmg = new int[128];

    static {
		 strDmg[ 7 ] = 2 ;
		 strDmg[ 8 ] = 2 ;
		 strDmg[ 9 ] = 2 ;
		 strDmg[ 10 ] = 3 ;
		 strDmg[ 11 ] = 3 ;
		 strDmg[ 12 ] = 4 ;
		 strDmg[ 13 ] = 4 ;
		 strDmg[ 14 ] = 5 ;
		 strDmg[ 15 ] = 5 ;
		 strDmg[ 16 ] = 6 ;
		 strDmg[ 17 ] = 6 ;
		 strDmg[ 18 ] = 7 ;
		 strDmg[ 19 ] = 7 ;
		 strDmg[ 20 ] = 8 ;
		 strDmg[ 21 ] = 8 ;
		 strDmg[ 22 ] = 9 ;
		 strDmg[ 23 ] = 9 ;
		 strDmg[ 24 ] = 10 ;
		 strDmg[ 25 ] = 10 ;
		 strDmg[ 26 ] = 11 ;
		 strDmg[ 27 ] = 11 ;
		 strDmg[ 28 ] = 12 ;
		 strDmg[ 29 ] = 12 ;
		 strDmg[ 30 ] = 13 ;
		 strDmg[ 31 ] = 13 ;
		 strDmg[ 32 ] = 14 ;
		 strDmg[ 33 ] = 14 ;
		 strDmg[ 34 ] = 15 ;
		 strDmg[ 35 ] = 15 ;
		 strDmg[ 36 ] = 16 ;
		 strDmg[ 37 ] = 16 ;
		 strDmg[ 38 ] = 17 ;
		 strDmg[ 39 ] = 17 ;
		 strDmg[ 40 ] = 18 ;
		 strDmg[ 41 ] = 18 ;
		 strDmg[ 42 ] = 19 ;
		 strDmg[ 43 ] = 19 ;
		 strDmg[ 44 ] = 20 ;
		 strDmg[ 45 ] = 20 ;
		 strDmg[ 46 ] = 21 ;
		 strDmg[ 47 ] = 21 ;
		 strDmg[ 48 ] = 22 ;
		 strDmg[ 49 ] = 22 ;
		 strDmg[ 50 ] = 23 ;
		 strDmg[ 51 ] = 23 ;
		 strDmg[ 52 ] = 24 ;
		 strDmg[ 53 ] = 24 ;
		 strDmg[ 54 ] = 25 ;
		 strDmg[ 55 ] = 25 ;
		 strDmg[ 56 ] = 26 ;
		 strDmg[ 57 ] = 26 ;
		 strDmg[ 58 ] = 27 ;
		 strDmg[ 59 ] = 27 ;
		 strDmg[ 60 ] = 28 ;
		 strDmg[ 61 ] = 28 ;
		 strDmg[ 62 ] = 29 ;
		 strDmg[ 63 ] = 29 ;
		 strDmg[ 64 ] = 30 ;
		 strDmg[ 65 ] = 30 ;
		 strDmg[ 66 ] = 31 ;
		 strDmg[ 67 ] = 31 ;
		 strDmg[ 68 ] = 32 ;
		 strDmg[ 69 ] = 32 ;
		 strDmg[ 70 ] = 33 ;
		 strDmg[ 71 ] = 33 ;
		 strDmg[ 72 ] = 34 ;

	}

    private static final int[] dexDmg = new int[128];

    static {
		// DEXダメージ補正
		dexDmg[ 7 ] = 2 ;
		dexDmg[ 8 ] = 2 ;
		dexDmg[ 9 ] = 3 ;
		dexDmg[ 10 ] = 3 ;
		dexDmg[ 11 ] = 3 ;
		dexDmg[ 12 ] = 4 ;
		dexDmg[ 13 ] = 4 ;
		dexDmg[ 14 ] = 4 ;
		dexDmg[ 15 ] = 5 ;
		dexDmg[ 16 ] = 5 ;
		dexDmg[ 17 ] = 5 ;
		dexDmg[ 18 ] = 6 ;
		dexDmg[ 19 ] = 6 ;
		dexDmg[ 20 ] = 6 ;
		dexDmg[ 21 ] = 7 ;
		dexDmg[ 22 ] = 7 ;
		dexDmg[ 23 ] = 7 ;
		dexDmg[ 24 ] = 8 ;
		dexDmg[ 25 ] = 8 ;
		dexDmg[ 26 ] = 8 ;
		dexDmg[ 27 ] = 9 ;
		dexDmg[ 28 ] = 9 ;
		dexDmg[ 29 ] = 9 ;
		dexDmg[ 30 ] = 10 ;
		dexDmg[ 31 ] = 10 ;
		dexDmg[ 32 ] = 10 ;
		dexDmg[ 33 ] = 11 ;
		dexDmg[ 34 ] = 11 ;
		dexDmg[ 35 ] = 11 ;
		dexDmg[ 36 ] = 12 ;
		dexDmg[ 37 ] = 12 ;
		dexDmg[ 38 ] = 12 ;
		dexDmg[ 39 ] = 13 ;
		dexDmg[ 40 ] = 13 ;
		dexDmg[ 41 ] = 13 ;
		dexDmg[ 42 ] = 14 ;
		dexDmg[ 43 ] = 14 ;
		dexDmg[ 44 ] = 14 ;
		dexDmg[ 45 ] = 15 ;
		dexDmg[ 46 ] = 15 ;
		dexDmg[ 47 ] = 15 ;
		dexDmg[ 48 ] = 16 ;
		dexDmg[ 49 ] = 16 ;
		dexDmg[ 50 ] = 16 ;
		dexDmg[ 51 ] = 17 ;
		dexDmg[ 52 ] = 17 ;
		dexDmg[ 53 ] = 17 ;
		dexDmg[ 54 ] = 18 ;
		dexDmg[ 55 ] = 18 ;
		dexDmg[ 56 ] = 18 ;
		dexDmg[ 57 ] = 19 ;
		dexDmg[ 58 ] = 19 ;
		dexDmg[ 59 ] = 19 ;
		dexDmg[ 60 ] = 20 ;
		dexDmg[ 61 ] = 20 ;
		dexDmg[ 62 ] = 20 ;
		dexDmg[ 63 ] = 21 ;
		dexDmg[ 64 ] = 21 ;
		dexDmg[ 65 ] = 21 ;
		dexDmg[ 66 ] = 22 ;
		dexDmg[ 67 ] = 22 ;
		dexDmg[ 68 ] = 22 ;
		dexDmg[ 69 ] = 23 ;
		dexDmg[ 70 ] = 23 ;
		dexDmg[ 71 ] = 23 ;
		dexDmg[ 72 ] = 24 ;    								
	}

    private static final int[] intDmg = new int[128];

    static {
        // intダメージ補正
        for (int int1 = 0; int1 <= 14; int1++) {
            intDmg[int1] = 0;
        }
        intDmg[15] = 3;
        intDmg[16] = 4;
        intDmg[17] = 5;
        intDmg[18] = 6;
        intDmg[19] = 6;
        intDmg[20] = 7;
        intDmg[21] = 7;
        intDmg[22] = 8;
        intDmg[23] = 8;
        intDmg[24] = 9;
        intDmg[25] = 9;
        intDmg[26] = 9;
        intDmg[27] = 10;
        intDmg[28] = 10;
        intDmg[29] = 11;
        intDmg[30] = 11;
        intDmg[31] = 12;
        intDmg[32] = 12;
        intDmg[33] = 13;
        intDmg[34] = 13;
        int dmg = 25;
        for (int int1 = 35; int1 <= 127; int1++) { // 35~127は1に+1
            dmg += 1;
            intDmg[int1] = dmg;
        }
    }

    public void setActId(int actId) {
        _attckActId = actId;
    }

    public void setGfxId(int gfxId) {
        _attckGrfxId = gfxId;
    }

    public int getActId() {
        return _attckActId;
    }

    public int getGfxId() {
        return _attckGrfxId;
    }

    public L1Attack(L1Character attacker, L1Character target) {
        if (attacker instanceof L1PcInstance) {
            _pc = (L1PcInstance) attacker;
            if (target instanceof L1PcInstance) {
                _targetPc = (L1PcInstance) target;
                _calcType = PC_PC;
            } else if (target instanceof L1NpcInstance) {
                _targetNpc = (L1NpcInstance) target;
                _calcType = PC_NPC;
            }
            // 武器情報の取得
            weapon = _pc.getWeaponSwap();
            Sweapon = _pc.getSecondWeapon();
            if (Sweapon != null) {
                _SweaponId = Sweapon.getItem().getItemId();
                _SweaponType = Sweapon.getItem().getType1();
                _SweaponAddHit = Sweapon.getItem().getHitModifier() + Sweapon.getHitByMagic();
                _SweaponAddDmg = Sweapon.getItem().getDmgModifier() + Sweapon.getDmgByMagic();
                _SweaponType1 = Sweapon.getItem().getType();
                _SweaponSmall = Sweapon.getItem().getDmgSmall();
                _SweaponLarge = Sweapon.getItem().getDmgLarge();
                _SweaponBless = Sweapon.getItem().getBless();
                _SweaponEnchant = Sweapon.getEnchantLevel() - Sweapon.get_durability(); //損傷分マイナス
                _SweaponMaterial = Sweapon.getItem().getMaterial();
                _SweaponAttrEnchantLevel = Sweapon.getAttrEnchantLevel();
            }
            if (weapon != null) {
                _weaponId = weapon.getItem().getItemId();
                _weaponType = weapon.getItem().getType1();
                _weaponType2 = weapon.getItem().getType();// 変更
                _weaponAddHit = weapon.getItem().getHitModifier() + weapon.getHitByMagic();
                _weaponAddDmg = weapon.getItem().getDmgModifier() + weapon.getDmgByMagic();
                _weaponSmall = weapon.getItem().getDmgSmall();
                _weaponLarge = weapon.getItem().getDmgLarge();
                _weaponBless = weapon.getItem().getBless();
                if (_weaponType == 0) {
                    _weaponEnchant = 0;
                }
                if (_weaponType != 20 && _weaponType != 62) {
                    _weaponEnchant = weapon.getEnchantLevel() - weapon.get_durability(); //損傷分マイナス
                } else {
                    _weaponEnchant = weapon.getEnchantLevel();
                }
                _weaponMaterial = weapon.getItem().getMaterial();
                if (_weaponType == 20) { // アローの取得
                    _arrow = _pc.getInventory().getArrow();
                    if (_arrow != null) {
                        _weaponBless = _arrow.getItem().getBless();
                        _weaponMaterial = _arrow.getItem().getMaterial();
                    }
                }
                if (_weaponType == 62) { // スティングの取得
                    _sting = _pc.getInventory().getSting();
                    if (_sting != null) {
                        _weaponBless = _sting.getItem().getBless();
                        _weaponMaterial = _sting.getItem().getMaterial();
                    }
                }
                _weaponDoubleDmgChance = weapon.getItem().getDoubleDmgChance();
                _weaponAttrLevel = weapon.getAttrEnchantLevel();
            }
            // ステータスによる追加ダメージ補正
            if (_weaponType == 20) { // 弓の場合はDEX値を参照
                _statusDamage = dexDmg[_pc.getAbility().getTotalDex()];
            } else if (_weaponType2 == 17) {// キーリンクはポイントの影響
                _statusDamage = intDmg[_pc.getAbility().getTotalInt()];
            } else {
                _statusDamage = strDmg[_pc.getAbility().getTotalStr()];
            }
        } else if (attacker instanceof L1NpcInstance) {
            _npc = (L1NpcInstance) attacker;
            if (target instanceof L1PcInstance) {
                _targetPc = (L1PcInstance) target;
                _calcType = NPC_PC;
            } else if (target instanceof L1NpcInstance) {
                _targetNpc = (L1NpcInstance) target;
                _calcType = NPC_NPC;
            }
        }
        _target = target;
        _targetId = target.getId();
        _targetX = target.getX();
        _targetY = target.getY();
    }

    /* ■■■■■■■■■■■■■■■■ 衝突判定 ■■■■■■■■■■■■■■■■ */

    public boolean calcHit() {
        if (_calcType == PC_PC || _calcType == PC_NPC) {
            if (_pc == null || _target == null)
                return _isHit;
            // キーリンクの場合、相手アブソの場合は無視
            if (_weaponType2 == 17) {
                if (_target.hasSkillEffect(L1SkillId.ABSOLUTE_BARRIER)) {
                    _isHit = false;
                } else {
                    _isHit = true;
                }
                return _isHit;
            }
    		if (_pc instanceof L1RobotInstance && _pc.isElf()) {
				if (!_pc.getLocation().isInScreen(_target.getLocation())) {
					_isHit = false;
					return _isHit;
				}
			}
            if (!(_pc instanceof L1RobotInstance) && _weaponType == 20 && _weaponId != 190 && _weaponId != 10000 && _weaponId != 202011 && _arrow == null) {
                _isHit = false; // 矢がない場合は、ミス
            } else if (_weaponType == 62 && _sting == null) {
                _isHit = false; // スティングがない場合は、ミス
            } else if (!_pc.glanceCheck(_targetX, _targetY)) {
                _isHit = false; // 攻撃者がプレイヤーの場合は、障害物判定
            } else if (_weaponId == 247 || _weaponId == 248 || _weaponId == 249) {
                _isHit = false; // 試練の剣B〜Cの攻撃無効
            } else if (_pc.getMapId() == 631 || _pc.getMapId() == 514) {
            	_isHit = false;
            } else if (_calcType == PC_PC) {
                _isHit = calcPcPcHit();
                if (_isHit == false) {
                    _pc.sendPackets(new S_SkillSound(_target.getId(), 13418));// エフェクト
                    _targetPc.sendPackets(new S_SkillSound(_target.getId(), 13418));// エフェクト
                }
            } else if (_calcType == PC_NPC) {
                /** バーポバン開け防止 **/
                if (_pc.baphomettRoom != true && _pc.getX() == 32758 && _pc.getY() == 32878 && _pc.getMapId() == 2) {
                    return _isHit = false;
                } else if (_pc.baphomettRoom != true && _pc.getX() == 32794 && _pc.getY() == 32790 && _pc.getMapId() == 2) {
                    return _isHit = false;
                } else {
                    _isHit = calcPcNpcHit();
                }
                /** バーポバン開け防止 **/
                if (_isHit == false) {
                    _pc.sendPackets(new S_SkillSound(_targetNpc.getId(), 13418));// ミスエフェクト
                }
            }
        } else if (_calcType == NPC_PC) {
            _isHit = calcNpcPcHit();
            if (_isHit == false) {
                _targetPc.sendPackets(new S_SkillSound(_target.getId(), 13418));// エフェクト
            }
        } else if (_calcType == NPC_NPC) {
            _isHit = calcNpcNpcHit();
        } else if (_targetNpc.getNpcTemplate().get_gfxid() == 7684 && !_pc.hasSkillEffect(PAP_FIVEPEARLBUFF)) {
            _isHit = false;
            return _isHit;
        } else if (_targetNpc.getNpcTemplate().get_gfxid() == 7805 && !_pc.hasSkillEffect(PAP_MAGICALPEARLBUFF)) {
            _isHit = false;
            return _isHit;
        }
        return _isHit;
    }

    // ●●●● プレイヤーからプレイヤーへの衝突判定 ●●●●
    /*
     * PCへの命中率=（PCのLv +クラス補正+ STR補正+ DEX補正+武器補正+ DAIの枚数/ 2 +魔法の補正）×0.68-10これで算出された数値は、自分が最大命中（95％）を与えることができる相手側PCのACそこから相手側PCのACが
     * 1良くたび者命中率から1引いていく少なくとも命中率5％、最大命中率95％
     */
    private boolean calcPcPcHit() {

        if (_pc.hasSkillEffect(L1SkillId.ABSOLUTE_BLADE)) {
            if (_target.hasSkillEffect(ABSOLUTE_BARRIER)) {
                int chance = _pc.getLevel() - 79;
                if (chance >= 10)
                    chance = 10;
                if (chance >= _random.nextInt(100) + 1) {
                    _targetPc.removeSkillEffect(ABSOLUTE_BARRIER);
                    _targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 14539));
                    _targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 14539));
                }
            }
        }
        if (_targetPc.hasSkillEffect(ABSOLUTE_BARRIER) || _targetPc.hasSkillEffect(ICE_LANCE))
            return false;

        /** バトルゾーン **/
        if (_calcType == PC_PC) {
            if (_pc.getMapId() == 5153) {
                if (_pc.get_DuelLine() == _targetPc.get_DuelLine()) {
                    return false;
                }
            }
        }

        _hitRate = _pc.getLevel();

        /** ステータス+武器による攻城 **/
        _hitRate += PchitAdd();

        /**転写の場合、50からレプダン衝突+2上昇 **/
        if (_pc.isWarrior()) {
            _hitRate += Math.max(0, _pc.getLevel() - 50) * 2;
        }
        int attackerDice = _random.nextInt(20) + 1 + _hitRate - 10;

        /** ターゲットPCの回避スキル凧山 **/
        attackerDice += toPcSkillHit();

        int defenderValue = (int) (_targetPc.getAC().getAc() * 1.5) * -1;
        int levelDmg = (int) ((_targetPc.getLevel() - _pc.getLevel()) * 2.0);
        if (levelDmg <= 0)
            levelDmg = 0;

        defenderValue += levelDmg;

        /** DefenderDice演算 **/
        int defenderDice = toPcDD(defenderValue);

        // キャラクター攻城データの追加
        try {
            if (_pc.isCrown()) {
                _hitRate += CharacterHitRate.getInstance().getCharacterHitRate(0);
            } else if (_pc.isKnight()) {
                _hitRate += CharacterHitRate.getInstance().getCharacterHitRate(1);
            } else if (_pc.isElf()) {
                _hitRate += CharacterHitRate.getInstance().getCharacterHitRate(2);
            } else if (_pc.isWizard()) {
                _hitRate += CharacterHitRate.getInstance().getCharacterHitRate(3);
            } else if (_pc.isDarkelf()) {
                _hitRate += CharacterHitRate.getInstance().getCharacterHitRate(4);
            } else if (_pc.isBlackwizard()) {
                _hitRate += CharacterHitRate.getInstance().getCharacterHitRate(5);
            } else if (_pc.isDragonknight()) {
                _hitRate += CharacterHitRate.getInstance().getCharacterHitRate(6);
            } else if (_pc.isWarrior()) {
            	 _hitRate += CharacterHitRate.getInstance().getCharacterHitRate(7);
                
            }
        } catch (Exception e) {
            System.out.println("Character Add Damege Error");
        }

        /** ヒット最終演算 **/
        if (hitRateCal(attackerDice, defenderDice, _hitRate - 9, _hitRate + 10))
            return false;

        if (_pc.getLocation().getLineDistance(_targetPc.getLocation()) >= 3 && _weaponType != 20 && _weaponType != 62) { //ターゲットとの距離が3以上で弓やスティングが
                                                                                                                         // それとも攻撃ミス笑;
            _hitRate = 0;
        }
        int rnd = _random.nextInt(100) + 1;
        if (_weaponType == 20 && _hitRate > rnd) { // 弓の場合、ヒットした場合でもERからの回避を再度実施する。
            return calcErEvasion();
        }

        int _jX = _pc.getX() - _targetPc.getX();
        int _jY = _pc.getY() - _targetPc.getY();

        if (_weaponType == 24) { // ウィンドウ
            if ((_jX > 3 || _jX < -3) && (_jY > 3 || _jY < -3)) {
                _hitRate = 0;
            }
        } else if (_weaponType == 20 || _weaponType == 62) {// 弓
            if ((_jX > 15 || _jX < -15) && (_jY > 15 || _jY < -15)) {
                _hitRate = 0;
            }
        } else {
            if ((_jX > 2 || _jX < -2) && (_jY > 2 || _jY < -2)) {
                _hitRate = 0;
            }
        }

        // System.out.println("Final 値 : _hitRate:"+_hitRate+" rnd:"+rnd+" 結果:"+(_hitRate >= rnd));
        // return _hitRate >= rnd;
        // ミスイパック付け
        if (_hitRate >= rnd) {
            return true;
        } else {
            _pc.sendPackets(new S_SkillSound(_targetPc.getId(), 13418));// エフェクト
            _targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 13418));// エフェクト
            return false;
        }
    }

    // ●●●●プレイヤーからNPCへの命中判定 ●●●●
    private boolean calcPcNpcHit() {
        /** SPRチェック **/
        if (_pc.AttackSpeedCheck2 >= 1) {
            if (_pc.AttackSpeedCheck2 == 1) {
                _pc.AttackSpeed2 = System.currentTimeMillis();
                _pc.sendPackets(new S_SystemMessage("\\fY[チェック開始]"));
            }
            _pc.AttackSpeedCheck2++;
            if (_pc.AttackSpeedCheck2 >= 12) {
                _pc.AttackSpeedCheck2 = 0;
                double k = (System.currentTimeMillis() - _pc.AttackSpeed2) / 10D;
                String s = String.format("%.0f", k);
                _pc.AttackSpeed2 = 0;
                _pc.sendPackets(new S_ChatPacket(_pc, "-----------------------------------------"));
                _pc.sendPackets(new S_ChatPacket(_pc, "この変身は" + s + "この攻撃速度に適切な値です。"));
                _pc.sendPackets(new S_ChatPacket(_pc, "-----------------------------------------"));
            }
        }
        /** SPRチェック **/


        try {
        	if(_pc.isCrown()){
        		_hitRate += CharacterHitRate.getInstance().getCharacterHitRate(0);
        	} else if(_pc.isKnight()){
        		_hitRate += CharacterHitRate.getInstance().getCharacterHitRate(1);
        	} else if(_pc.isElf()){
        		_hitRate += CharacterHitRate.getInstance().getCharacterHitRate(2);
        	} else if(_pc.isWizard()){
        		_hitRate += CharacterHitRate.getInstance().getCharacterHitRate(3);
        	} else if(_pc.isDarkelf()){
        		_hitRate += CharacterHitRate.getInstance().getCharacterHitRate(4);
        	} else if(_pc.isBlackwizard()){
        		_hitRate += CharacterHitRate.getInstance().getCharacterHitRate(5);
        	} else if(_pc.isDragonknight()){
        		_hitRate += CharacterHitRate.getInstance().getCharacterHitRate(6);
        	} else if(_pc.isWarrior()){
        		_hitRate += CharacterHitRate.getInstance().getCharacterHitRate(7);
        	}
        } catch (Exception e){
        	System.out.println("Character Add Damege Error");
        }
  
        // NPCへの命中率=（PCのLv +クラス補正+ STR補正+ DEX補正+武器補正+ DAIの枚数/ 2 +魔法の補正）×5 {NPCのAC×（-5）}
        _hitRate = _pc.getLevel();

        /** ステータス+武器による攻城 **/
        _hitRate += PchitAdd();

        if (_targetNpc.getAc() < 0) {
            int acrate = _targetNpc.getAc() * -1;
            double aaaa = (_hitRate / 100) * (acrate / 2.5D);
            _hitRate -= (int) aaaa;
        }

        if (_pc.getLevel() < _targetNpc.getLevel()) {
            _hitRate -= _targetNpc.getLevel() - _pc.getLevel();
        }
        if (_hitRate > 95) {
            _hitRate = 95;
        } else if (_hitRate < 5) {
            _hitRate = 5;
        }

        int _jX = _pc.getX() - _targetNpc.getX();
        int _jY = _pc.getY() - _targetNpc.getY();

        if (_weaponType == 24) { // ウィンドウのとき
            if ((_jX > 3 || _jX < -3) && (_jY > 3 || _jY < -3)) {
                _hitRate = 0;
            }
        } else if (_weaponType == 20 || _weaponType == 62) {// 弓のとき
            if ((_jX > 15 || _jX < -15) && (_jY > 15 || _jY < -15)) {
                _hitRate = 0;
            }
        } else {
            if ((_jX > 2 || _jX < -2) && (_jY > 2 || _jY < -2)) {
                _hitRate = 0;
            }
        }
        
        int npcId = _targetNpc.getNpcTemplate().get_npcId(); // シェムリディエラー出
        if (npcId >= 45912 && npcId <= 45915 && !_pc.hasSkillEffect(STATUS_HOLY_WATER)) {
            _hitRate = 0;
        }
        if (npcId == 45916 && !_pc.hasSkillEffect(STATUS_HOLY_MITHRIL_POWDER)) {
            _hitRate = 0;
        }
        if (npcId == 45941 && !_pc.hasSkillEffect(STATUS_HOLY_WATER_OF_EVA)) {
            _hitRate = 0;
        }
        if (npcId == 45752 && !_pc.hasSkillEffect(STATUS_CURSE_BARLOG)) {
            _hitRate = 0;
        }
        if (npcId == 45753 && !_pc.hasSkillEffect(STATUS_CURSE_BARLOG)) {
            _hitRate = 0;
        }
        if (npcId == 45675 && !_pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
            _hitRate = 0;
        }
        if (npcId == 81082 && !_pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
            _hitRate = 0;
        }
        if (npcId == 45625 && !_pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
            _hitRate = 0;
        }
        if (npcId == 45674 && !_pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
            _hitRate = 0;
        }
        if (npcId == 45685 && !_pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
            _hitRate = 0;
        }
        if (npcId >= 46068 && npcId <= 46091 && _pc.getTempCharGfx() == 6035) {
            _hitRate = 0;
        }
        if (npcId >= 46092 && npcId <= 46106 && _pc.getTempCharGfx() == 6034) {
            _hitRate = 0;
        }
        if (_targetNpc.getNpcTemplate().get_gfxid() == 7684 && !_pc.hasSkillEffect(PAP_FIVEPEARLBUFF)) { // 五色真珠
            _hitRate = 0;
        }
        if (_targetNpc.getNpcTemplate().get_gfxid() == 7805 && !_pc.hasSkillEffect(PAP_MAGICALPEARLBUFF)) { // 神秘的な真珠
            _hitRate = 0;
        }

        return _hitRate >= _random.nextInt(100) + 1;
    }

    // ●●●● NPCからプレイヤーへの衝突判定 ●●●●
    private boolean calcNpcPcHit() {

        if (_targetPc.hasSkillEffect(ABSOLUTE_BARRIER)) {
            return false;
        }
        _hitRate += _npc.getLevel() * 1.2;

        if (_npc instanceof L1PetInstance) { // ペットはLV1に追加命中+2
            _hitRate += _npc.getLevel() * 2;
            _hitRate += ((L1PetInstance) _npc).getHitByWeapon();
        }

        _hitRate += _npc.getHitup();

        int attackerDice = _random.nextInt(20) + 1 + _hitRate - 1;

        /** ターゲットPCの回避スキル演算 **/
        attackerDice += toPcSkillHit();

        int defenderValue = (_targetPc.getAC().getAc()) * -1;

        /** DefenderDice演算 **/
        int defenderDice = toPcDD(defenderValue);

        /** ヒット最終演算 **/
        if (hitRateCal(attackerDice, defenderDice, _hitRate, _hitRate + 19))
            return false;

        int rnd = _random.nextInt(100) + 1;

        // NPCの攻撃レンジが10以上の場合で、2以上離れている場合、弓攻撃とみなす
        if (_npc.getNpcTemplate().get_ranged() >= 10 && _hitRate > rnd
                && _npc.getLocation().getTileLineDistance(new Point(_targetX, _targetY)) >= 2) {
            return calcErEvasion();
        }

        return _hitRate >= rnd;
    }

    // ●●●● NPCからNPCへの命中判定 ●●●●
    private boolean calcNpcNpcHit() {
        int target_ac = 10 - _targetNpc.getAC().getAc();
        int attacker_lvl = _npc.getNpcTemplate().get_level();

        if (target_ac != 0) {
            _hitRate = (100 / target_ac * attacker_lvl); // 被攻撃AC =攻撃Lv //のとき命中率100％
        } else {
            _hitRate = 100 / 1 * attacker_lvl;
        }

        if (_npc instanceof L1PetInstance) { // ペットはLV1に追加命中+2
            _hitRate += _npc.getLevel() * 2;
            _hitRate += ((L1PetInstance) _npc).getHitByWeapon();
        }

        if (_hitRate < attacker_lvl) {
            _hitRate = attacker_lvl; // 最低命中率= Lv％
        }
        if (_hitRate > 95) {
            _hitRate = 95; // 最高命中率は95％
        }
        if (_hitRate < 5) {
            _hitRate = 5; // 攻撃者Lvが5未満の時命中率5％
        }

        int rnd = _random.nextInt(100) + 1;
        return _hitRate >= rnd;
    }

    // ●●●● ERによる回避判定 ●●●●
    private boolean calcErEvasion() {
        int er = _targetPc.get_PlusEr();

        int rnd = _random.nextInt(130) + 1;
        return er < rnd;
    }

    /* ■■■■■■■■■■■■■■■ダメージ算出 ■■■■■■■■■■■■■■■ */

    public int calcDamage() {
        try {
            switch (_calcType) {
            case PC_PC:
                _damage = calcPcPcDamage();
                // タイタンロック：HPが40％未満の場合、近接攻撃を確率的に反射。
                if (_weaponType != 20 && _weaponType != 62 && _weaponType2 != 17 && _weaponType2 != 19) { // 弓がない場合はロック発動。
                    if (SkillsTable.getInstance().spellCheck(_targetPc.getId(), 238)) {
                        int percent = (int) Math.round(((double) _targetPc.getCurrentHp() / (double) _targetPc.getMaxHp()) * 100);
                        int chance = _random.nextInt(100) + 1;
                        int lockSection = 0;
                        if (_target.getLockSectionUp() != 0) {
                            lockSection += _target.getLockSectionUp();
                        }
                        // System.out.println("ロック区間いくら上がるか」+ロック区間);
                        if (!_targetPc.isstop() && percent <= (40 + lockSection + _targetPc.getRisingUp()) && chance <= 23) {
                            // System.out.println("ロック発動するか?");
                            if (_targetPc.getInventory().checkItem(41246, 10)) {
                                _pc.receiveCounterBarrierDamage(_targetPc, calcTitanDamage());
                                _damage = 0;
                                _targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 12555));
                              //  _targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 12555));
                                _targetPc.getInventory().consumeItem(41246, 10);
                            } else {
                                _targetPc.sendPackets(new S_SystemMessage("タイタンロック：触媒が不足します。"));
                            }
                        }
                    }
                } else { // そうでない場合のウェーブレット発動
                    if (_weaponType2 != 17 && _weaponType2 != 19) {
                        if (SkillsTable.getInstance().spellCheck(_targetPc.getId(), 239)) {
                            int percent = (int) Math.round(((double) _targetPc.getCurrentHp() / (double) _targetPc.getMaxHp()) * 100);
                            int chance = _random.nextInt(100) + 1;
                            int lockSection = 0;
                            if (_target.getLockSectionUp() != 0) {
                                lockSection += _target.getLockSectionUp();
                            }
                            if (!_targetPc.isstop() && percent <= (40 + lockSection + _targetPc.getRisingUp()) && chance <= 23) {
                                if (_targetPc.getInventory().checkItem(41246, 10)) {
                                    _pc.receiveCounterBarrierDamage(_targetPc, calcTitanDamage());
                                    _damage = 0;
                                    _targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 12557));
                                  //  _targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 12557));
                                    _targetPc.getInventory().consumeItem(41246, 10);
                                } else {
                                    _targetPc.sendPackets(new S_SystemMessage("タイタンウェーブレット：触媒が不足します。"));
                                }
                            }
                        }
                    } else { // そうでない場合マジック発動
                        if (SkillsTable.getInstance().spellCheck(_targetPc.getId(), 240)) {
                            int percent = (int) Math.round(((double) _targetPc.getCurrentHp() / (double) _targetPc.getMaxHp()) * 100);
                            int chance = _random.nextInt(100) + 1;
                            int lockSection = 0;
                            if (_target.getLockSectionUp() != 0) {
                                lockSection += _target.getLockSectionUp();
                            }
                            if (!_targetPc.isstop() && percent <= (40 + lockSection + _targetPc.getRisingUp()) && chance <= 23) {
                                if (this._targetPc.getInventory().checkItem(41246, 10)) {
                                    if (this._calcType == 1)
                                        this._pc.receiveCounterBarrierDamage(this._targetPc, calcTitanDamage());
                                    else if (this._calcType == 2)
                                        this._npc.receiveCounterBarrierDamage(this._targetPc, calcTitanDamage());
                                    _damage = 0;
                                    this._targetPc.sendPackets(new S_SkillSound(this._targetPc.getId(), 12559));
                                   // this._targetPc.broadcastPacket(new S_SkillSound(this._targetPc.getId(), 12559));
                                    this._targetPc.getInventory().consumeItem(41246, 10);
                                } else {
                                    this._targetPc.sendPackets(new S_SystemMessage("タイタンマジック：触媒が不足します。"));
                                }
                            }
                        }
                    }
                }
                break;
            case PC_NPC:
                _damage = calcPcNpcDamage();
                break;
            case NPC_PC:
                _damage = calcNpcPcDamage();
                // タイタンロック：HPが40％未満の場合、近接攻撃を確率的に反射。
                int bowactid = _npc.getNpcTemplate().getBowActId();
                if (bowactid != 66) {
                    if (SkillsTable.getInstance().spellCheck(_targetPc.getId(), 238)) {
                        int percent = (int) Math.round(((double) _targetPc.getCurrentHp() / (double) _targetPc.getMaxHp()) * 100);
                        int chance = _random.nextInt(100) + 1;
                        int lockSection = 0;
                        if (_target.getLockSectionUp() != 0) {
                            lockSection += _target.getLockSectionUp();
                        }
                        if (!_targetPc.isstop() && percent <= (40 + lockSection + _targetPc.getRisingUp()) && chance <= 23) {
                            if (_targetPc.getInventory().checkItem(41246, 10)) {
                                _npc.receiveCounterBarrierDamage(_targetPc, calcTitanDamage());
                                _damage = 0;
                                _targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 12555));
                               // _targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 12555));
                                _targetPc.getInventory().consumeItem(41246, 10);
                            } else {
                                _targetPc.sendPackets(new S_SystemMessage("タイタンロック：触媒が不足します。"));
                            }
                        }
                    }
                } else {
                    // タイタンウェーブレット：HPが40％未満の場合、遠距離攻撃を確率的に反射。
                    if (SkillsTable.getInstance().spellCheck(_targetPc.getId(), 239)) {
                        int percent = (int) Math.round(((double) _targetPc.getCurrentHp() / (double) _targetPc.getMaxHp()) * 100);
                        int chance = _random.nextInt(100) + 1;
                        int lockSection = 0;
                        if (_target.getLockSectionUp() != 0) {
                            lockSection += _target.getLockSectionUp();
                        }
                        if (!_targetPc.isstop() && percent <= (40 + lockSection + _targetPc.getRisingUp()) && chance <= 23) {
                            if (_targetPc.getInventory().checkItem(41246, 10)) {
                                _npc.receiveCounterBarrierDamage(_targetPc, calcTitanDamage());
                                _damage = 0;
                                _targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 12557));
                               // _targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 12557));
                                _targetPc.getInventory().consumeItem(41246, 10);
                            } else {
                                _targetPc.sendPackets(new S_SystemMessage("タイタンウェーブレット：触媒が不足します。"));
                            }
                        }
                    }
                }
                break;
            case NPC_NPC:
                _damage = calcNpcNpcDamage();
                break;
            default:
                break;
            }
        } catch (Exception e) {
        }
        return _damage;
    }

    // ●●●● プレイヤーからプレイヤーへのダメージ算出 ●●●●
    public int calcPcPcDamage() {
		if (_pc instanceof L1RobotInstance) {
			if (!_targetPc.isRobot()) {
				if (_pc.getCurrentWeapon() == 20) { // 弓
					return _random.nextInt(70) + 40;
				} else {
					return _random.nextInt(80) + 30;
				}
			} else {
				return 50;
			}

		}
        int weaponMaxDamage = _weaponSmall; // + _weaponAddDmg;

        int weaponDamage = 0;

        if ((_pc.getZoneType() == 1 && _targetPc.getZoneType() == 0) || (_pc.getZoneType() == 1 && _targetPc.getZoneType() == -1)) {
            _isHit = false;
            // セーフティゾーンでノーマル/コンバットゾーン攻撃不可
        }

        /** 轟音の二刀流エンチャンあたり発動確率増加 **/
        if (_weaponId == 203018) {
            _weaponDoubleDmgChance += _pc.getWeapon().getEnchantLevel();
        }
        if (_weaponType == 58) { // クロウ
        	int crowchance = _random.nextInt(100) + 1;
        	if(crowchance <= _weaponDoubleDmgChance){
        		weaponDamage = weaponMaxDamage + _weaponAddDmg;
                // _attackType = 2;
                _pc.sendPackets(new S_SkillSound(_pc.getId(), 3671));
                _pc.broadcastPacket(new S_SkillSound(_pc.getId(), 3671));
        	}else{
        		weaponDamage = _random.nextInt(weaponMaxDamage) + _weaponAddDmg + 1;
        	}
        }else if (_weaponType == 0) { // 素手
            weaponDamage = 0;
        } else {
            weaponDamage = _random.nextInt(weaponMaxDamage) + _weaponAddDmg + 1;
        }

        if (_pc.hasSkillEffect(SOUL_OF_FLAME)) {
            if (_weaponType != 20 && _weaponType != 62) {
                // weaponDamage = weaponMaxDamage + 5;
                weaponDamage = weaponMaxDamage + _weaponAddDmg;
            }
        }
		/** 祝福書武器ツタ関連 **/
		/*if (_weaponType != 0) {
			if (weapon.getBless() == 0 || weapon.getBless() == 128) {
				weaponDamage += 3;
			}
		}*/
        if(_weaponType != 0 ){
            if (_weaponType != 20 && _weaponType != 62) { // 近距離
                int Dmgcritical = CalcStat.calcDmgCritical(_pc.getAbility().getTotalStr())+_pc.getDmgCritical();
                int chance = _random.nextInt(100) + 1;
                //記章クリティカル
                if (_pc.getInventory().checkEquipped(900032)){
                	L1ItemInstance item = _pc.getInventory().findEquippedItemId(900032);
                	if(item.getEnchantLevel() == 6){
                		Dmgcritical += 1;
                	}else if(item.getEnchantLevel() == 7){
                		Dmgcritical += 3;
                	}else if(item.getEnchantLevel() >= 8){
                		Dmgcritical += 5;
                	}
                }/*if (_pc.getInventory().checkEquipped(22208)){ // 塗って腕力
                	L1ItemInstance item = _pc.getInventory().findEquippedItemId(22208);
                	if(item.getEnchantLevel() == 7){
                		Dmgcritical += 1;
                	}else if(item.getEnchantLevel() == 8){
                		Dmgcritical += 2;
                	}else if(item.getEnchantLevel() >= 9){
                		Dmgcritical += 3;
                	}
                }if (_pc.getInventory().checkEquipped(22209)){ //塗って忍耐
                	L1ItemInstance item = _pc.getInventory().findEquippedItemId(22209);
                	if(item.getEnchantLevel() == 7){
                		Dmgcritical += 1;
                	}else if(item.getEnchantLevel() == 8){
                		Dmgcritical += 2;
                	}else if(item.getEnchantLevel() >= 9){
                		Dmgcritical += 3;
                	}
                }*/
                /** ヴァラカスの一撃 **/
         		if (_pc.getInventory().checkEquipped(22208) || _pc.getInventory().checkEquipped(22209)
         				|| _pc.getInventory().checkEquipped(22210) || _pc.getInventory().checkEquipped(22211)) {
         			int chancess = _random.nextInt(100);
         			if (chancess <= 8) {
         				weaponDamage *= 1.3;
         				S_UseAttackSkill packet = new S_UseAttackSkill(_target, _target.getId(), 15841, _targetX, _targetY,
         						ActionCodes.ACTION_Attack, false);
         				_pc.sendPackets(packet);
         				Broadcaster.broadcastPacket(_pc, packet);
         			}
         		}
                if (chance <= Dmgcritical) {
                    weaponDamage = weaponMaxDamage + _weaponAddDmg;
                    _isCritical = true;
                }
            } else {
                int Bowcritical = CalcStat.calcBowCritical(_pc.getAbility().getTotalDex())+_pc.getBowDmgCritical();
                int chance = _random.nextInt(100) + 1;
              //記章クリティカル
                if (_pc.getInventory().checkEquipped(900033)){
                	L1ItemInstance item = _pc.getInventory().findEquippedItemId(900033);
                	if(item.getEnchantLevel() == 6){
                		Bowcritical += 1;
                	}else if(item.getEnchantLevel() == 7){
                		Bowcritical += 3;
                	}else if(item.getEnchantLevel() >= 8){
                		Bowcritical += 5;
                	}
                }/*if (_pc.getInventory().checkEquipped(22210)){
                	L1ItemInstance item = _pc.getInventory().findEquippedItemId(22210);
                	if(item.getEnchantLevel() == 7){
                		Bowcritical += 1;
                	}else if(item.getEnchantLevel() == 8){
                		Bowcritical += 2;
                	}else if(item.getEnchantLevel() >= 9){
                		Bowcritical += 3;
                	}
                }*/
                /** ヴァラカスの一撃 **/
        		if (_pc.getInventory().checkEquipped(22208) || _pc.getInventory().checkEquipped(22209)
        				|| _pc.getInventory().checkEquipped(22210) || _pc.getInventory().checkEquipped(22211)) {
        			int chancess = _random.nextInt(100);
        			if (chancess <= 8) {
        				weaponDamage *= 1.3;
        				S_UseAttackSkill packet = new S_UseAttackSkill(_target, _target.getId(), 15841, _targetX, _targetY,
        						ActionCodes.ACTION_Attack, false);
        				_pc.sendPackets(packet);
        				Broadcaster.broadcastPacket(_pc, packet);
        			}
        		}
                if (chance <= Bowcritical) {
                    weaponDamage = weaponMaxDamage + _weaponAddDmg;
                    _isCritical = true;
                }
    	        }
            }
        int weaponTotalDamage = weaponDamage + _weaponEnchant;
        boolean secondw = false;
        if (_pc.isWarrior() && _pc.hasSkillEffect(L1SkillId.SLAYER) && _pc.getSecondWeapon() != null) {
            int ran = _random.nextInt(100);
            if (ran < 50) {
                secondw = true;
                weaponMaxDamage = _SweaponSmall + _SweaponAddDmg;
            }
        }

        if (_weaponType == 54 && _pc.hasSkillEffect(L1SkillId.ASSASSIN)) {
            if (!_pc.getInventory().checkEquipped(20077) && !_pc.getInventory().checkEquipped(120077) && !_pc.getInventory().checkEquipped(20062)) {

                if (_random.nextInt(100) + 1 <= 60) {
                    weaponTotalDamage *= 2.5;
                    _pc.sendPackets(new S_SkillSound(_pc.getId(), 14547));
                    _pc.broadcastPacket(new S_SkillSound(_pc.getId(), 14547));

                    if (SkillsTable.getInstance().spellCheck(_pc.getId(), 241)) {
                        int time = 3 + (_pc.getLevel() - 85);
                        if (time > 8)
                            time = 8;
                        _pc.setSkillEffect(L1SkillId.BLAZING_SPIRITS, time * 1000);
                        _pc.sendPackets(new S_NewSkillIcon(L1SkillId.BLAZING_SPIRITS, true, time));
                    }
                    
                }
            } else {
                _pc.sendPackets(new S_SystemMessage("現在、透明状態では、このスキル発動無効。"));
            }
            _pc.removeSkillEffect(L1SkillId.ASSASSIN);
        }

        if (_weaponType == 54 && _pc.isDarkelf()) {
        	if (_pc.hasSkillEffect(L1SkillId.BLAZING_SPIRITS)) {
        		weaponTotalDamage *= 2.5;
        		_targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 14547));
        		_targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 14547));
        		_pc.sendPackets(new S_AttackCritical(_pc, _targetId, 54));
        		Broadcaster.broadcastPacket(_pc, new S_AttackCritical(_pc, _targetId, 54));
        	} else if ((_random.nextInt(100) + 1) <= (_weaponDoubleDmgChance - weapon.get_durability())) {
        		weaponTotalDamage *= 2.5;
        		_pc.sendPackets(new S_SkillSound(_pc.getId(), 3398));
        		_pc.broadcastPacket(new S_SkillSound(_pc.getId(), 3398));
        	}
        }


        if (_pc.hasSkillEffect(DOUBLE_BRAKE) && (_weaponType == 54 || _weaponType == 58)) { //ダブルブレーキ確率50レップから5レプダン1％ずつ上昇
            int RealSteelLevel = _pc.getLevel();
            if (RealSteelLevel < 45) {
                RealSteelLevel = 45;
            }
            int RealSteelLevelChance = (RealSteelLevel - 45) / 5 + 33;
            if ((_random.nextInt(100) + 1) <= RealSteelLevelChance) {
                weaponTotalDamage *= 2;
                if (_pc.hasSkillEffect(BURNING_SPIRIT)) {
                    _pc.sendPackets(new S_SkillSound(_targetPc.getId(), 6532));
                    Broadcaster.broadcastPacket(_pc, new S_SkillSound(_targetPc.getId(), 6532));
                }
            }
        }
       

        double dmg = weaponTotalDamage + _statusDamage;

        if (_weaponType2 == 17) {
            dmg = L1WeaponSkill.KiringkuDamage(_pc, _target);
        }
        if (_weaponType != 20 && _weaponType != 62) {
            dmg = weaponTotalDamage + _statusDamage + _pc.getDmgup() + _pc.getDmgRate() + _pc.get_regist_PVPweaponTotalDamage();
        } else {
            dmg = weaponTotalDamage + _statusDamage + _pc.getBowDmgup() + _pc.getBowDmgRate() + _pc.get_regist_PVPweaponTotalDamage();
        }
        if (_pc.hasSkillEffect(BURNING_SPIRIT)) {
            if ((_random.nextInt(100) + 1) <= 33) {
                dmg *= 3 / 2;
            }
        }
        
        if (_weaponType == 20) { // 弓
            if (_arrow != null) {
                int add_dmg = _arrow.getItem().getDmgSmall();
                if (add_dmg == 0) {
                    add_dmg = 1;
                }
                dmg = dmg + _random.nextInt(add_dmg) + 1;
            } else if (_weaponId == 190) { // サイハの弓
                dmg = dmg + _random.nextInt(15);

            } else if (_pc.getTempCharGfx() == 7959) { // 天上の弓
                dmg = dmg + _random.nextInt(13);
            }
        } else if (_pc.getTempCharGfx() == 202011) { // ガイアの激怒
            dmg = dmg + _random.nextInt(15);

        } else if (_weaponType == 62) { // がんトートレッド
            int add_dmg = _sting.getItem().getDmgSmall();
            if (add_dmg == 0) {
                add_dmg = 1;
            }
            dmg = dmg + _random.nextInt(add_dmg) + 1;
        }
        dmg = calcBuffDamage(dmg);
    

		//double dmgRate = 1;
        
        /** 本サーバー10剣以上ツタ+1表記効果 **/
        if (_weaponType != 0 && _weaponType != 20) {
			if(_weaponId != 66){
            switch (weapon.getEnchantLevel()) {
            case 10:
                dmg += 1;
                break;
            case 11:
                dmg += 2;
                break;
            case 12:
                dmg += 3;
                break;
            case 13:
                dmg += 4;
                break;
            case 14:
                dmg += 5;
                break;
            case 15:
                dmg += 6;
                break;
            default:
                break;
            }
			}
        }
        
        /** エンチャントによるツタ関連**/
        if (_weaponType != 0 && _weaponType != 20) {
            switch (weapon.getEnchantLevel()) {
            case 7:
                dmg += 1;
                break;
            case 8:
                dmg += 2;
                break;
            case 9:
                dmg += 3;
                break;
            case 10:
                dmg += 4;
                break;
            case 11:
                dmg += 5;
                break;
            case 12:
                dmg += 6;
                break;
            case 13:
                dmg += 7;
                break;
            case 14:
                dmg += 8;
                break;
            case 15:
                dmg += 9;
                break;
            default:
                break;
            }
        }
        /** ドゥスルエンチャン星ツタ2ずつ **/
        if(_weaponId == 66){
        	dmg += weapon.getEnchantLevel();
        }
        
        /** 赤のナイトの大剣リニューアル **/
        if (_pc.getInventory().checkEquipped(202002) || _pc.getInventory().checkEquipped(203002) || _pc.getInventory().checkEquipped(1136)
                || _pc.getInventory().checkEquipped(1137)) {
            if (_pc.getLawful() < -32760) {
                dmg += 8;
            }
            if (_pc.getLawful() >= -32760 && _pc.getLawful() < -25000) {
                dmg += 6;
            }
            if (_pc.getLawful() >= -25000 && _pc.getLawful() < -15000) {
                dmg += 4;
            }
            if (_pc.getLawful() >= -15000 && _pc.getLawful() < 0) {
                dmg += 2;
            }
            if (_pc.getLawful() >= 0) {
                dmg += 1;
            }
        }
        if (_weaponType2 == 17) {
            dmg = L1WeaponSkill.KiringkuDamage(_pc, _target);
        }
       // dmg *= dmgRate;
        
        switch (_weaponId) {
        case 307:
        case 308:
        case 309:
        case 310:
        case 311:
        case 313:
        case 314:
            dmg = L1WeaponSkill.BlazeShock(_pc, _targetPc, _weaponEnchant);
            break;
        case 1010:
        case 1011:
        case 1012:
        case 1013:
        case 1014:
            L1WeaponSkill.getDiseaseWeapon(_pc, _targetPc, _weaponId);
            break;
        case 2: // 悪運の短剣
        case 200002: // 軸悪運の短剣
            dmg = L1WeaponSkill.DiceDagger(_pc, _targetPc, weapon);
            break;
        case 12: // 風の刃短剣
        case 203020: // 生命の短剣
        case 601: // 破滅のグレートソード
            ruinGreatSword(dmg);
            break;
        case 204: // 真紅のクロスボウ
        case 100204: // 軸真紅のクロスボウ
        case 86: // 赤い影のデュアルブレード
            L1WeaponSkill.RedShadowDualBlade(_pc, _targetPc);
            break;
        case 1115: // 神妙ソード
        case 1117: // 神妙クロ
            dmg += getEbHP(_pc, _target, 8981, _weaponEnchant);
            break;
        case 1116: // 神妙杖
        case 1118: // 神妙ロングボウ
        case 202011: // がよの激怒
            dmg += getEbMP1(_pc, _target, 8981, _weaponEnchant);
            break;
        case 1109: // 魔族クロウ
        case 1113: // 魔族の剣
        case 1114: // 魔族両手剣
        case 203011: // 魔族斧
            dmg += getEbHP(_pc, _target, 8150, _weaponEnchant);
            break;
        case 1110: // 魔族の杖
        case 1112: // 魔族のキーリンク
        case 1111: // 魔族の弓
            dmg += getEbMP(_pc, _target, 8152, _weaponEnchant);
            break;
        case 1108: // 魔族チェーン
            dmg += getEbHP(_pc, _target, 8150, _weaponEnchant);
            L1WeaponSkill.ChainSword(_pc);
            break;
        case 1119: // 極限のチェーンソード
            dmg += extremeChainSword(_pc, _target, 3685, _weaponEnchant);
            L1WeaponSkill.ChainSword(_pc);
            break;
        case 1123: // ブラッドサッカー
        case 202013:
            L1WeaponSkill.ChainSword(_pc);
            bloodSucker(dmg, _weaponEnchant);
            break;
        case 500:// デストラクタのチェーンソード
        case 501:// 破滅者のチェーンソード
        case 1104:// エルモアチェーンソード
        case 1132:// ベビーテルランチェーンソード
            L1WeaponSkill.ChainSword(_pc);
            break;
        case 203017:
            L1WeaponSkill.ChainSword_Destroyer(_pc);
            if (weapon.getEnchantLevel() >= 10)
            dmg += L1WeaponSkill.Destroyer(_pc, _target, 4077, _weaponEnchant);
            break;
        case 203006://台風の斧
        	if (weapon.getEnchantLevel() >= 10)
        	 dmg += L1WeaponSkill.StormAx(_pc, _target, 7977, _weaponEnchant);
        	break;
            
        case 1136://悪夢のロングボウ
       	 if (weapon.getEnchantLevel() >= 10)
                dmg += L1WeaponSkill.Nightmare(_pc, _target, 14339, _weaponEnchant);
       	break;
        case 203025://ジンサ
        case 203026:
       	 if (weapon.getEnchantLevel() >= 10)
                dmg += L1WeaponSkill.Jinsa(_pc, _target, 8032, _weaponEnchant);
       	break;
        case 312:
            dmg = L1WeaponSkill.ChainSword_BlazeShock(_pc, _targetNpc, _weaponEnchant);
            break;
        case 202001: // 歓迎のチェーンソード
            dmg += L1WeaponSkill.ChainSword_Welcome(_pc, _target, _weaponEnchant);
            L1WeaponSkill.ChainSword(_pc);
            break;
        case 1124: // 破壊の二刀流
        case 1125: // 破壊のクロウ
        case 11125:// 祝福破壊の二刀流
            dmg += L1WeaponSkill.DestructionDualBlade_Crow(_pc, _target, 9359, _weaponEnchant);
            break;
        case 600: // 脳身体検査
            dmg += L1WeaponSkill.LightningEdge(_pc, _target, 3940, _weaponEnchant);
            break;
        case 604: // 酷寒のウィンドウ
            dmg += L1WeaponSkill.ExColdWind(_pc, _target, 3704, _weaponEnchant);
            break;
        case 605: //狂風の斧
        case 203015: // 疾風の斧
            dmg += L1WeaponSkill.InsanityWindAx(_pc, _target, 5524, _weaponEnchant);
            break;
        case 191: // サルチョンの弓
            dmg += L1WeaponSkill.AngelSlayer(_pc, _target, 9361, _weaponEnchant);
            break;
        case 1135: // 共鳴のキーリンク
            dmg += L1WeaponSkill.Kiringku_Resonance(_pc, _target, 5201, _weaponEnchant);
            break;
        case 202012: // ヒペリオンの絶望
            dmg += L1WeaponSkill.HypelionsDespair(_pc, _target, 12248, _weaponEnchant);
            break;
        case 1120: // 冷え性のキーリンク
            dmg += L1WeaponSkill.Kiringku_Cold(_pc, _target, 6553, _weaponEnchant);
            break;
        case 283: // ヴァラカスのキーリンク
            dmg += L1WeaponSkill.Redskill(_pc, _target, 10405, _weaponEnchant);
            break;
        case 294: // 君主の剣
            dmg += L1WeaponSkill.LordSword(_pc, _target, 4842, _weaponEnchant);
            break;
        case 58: // デスナイトのフレイムブレード
            dmg += L1WeaponSkill.DeathKnightFlameBlade(_pc, _target, _weaponEnchant, 7300);
            break;
        case 54: // カーツの剣
            dmg += L1WeaponSkill.KurtzsSword(_pc, _target, _weaponEnchant, 10405);
            break;
        case 124: // バフォメットスタッフ
            dmg += L1WeaponSkill.BaphometStaff(_pc, _target, _weaponEnchant, 129);
            break;
        case 202003: // ゼロスの杖
            dmg += L1WeaponSkill.ZerosWand(_pc, _target, _weaponEnchant, 11760);
            break;
        case 134: // 修正結晶棒
            dmg += L1WeaponSkill.HolyHedronStaff(_pc, _target, _weaponEnchant, 10405);
            dmg+=getEbMP(_pc, _target, 8152, _weaponEnchant);
            break;
        default:
            dmg += L1WeaponSkill.getWeaponSkillDamage(_pc, _target, _weaponId);
            break;
        }

        if (_weaponType == 0) { // 素手
            dmg = (_random.nextInt(5) + 4) / 4;
        }

        try {
            dmg += WeaponAddDamage.getInstance().getWeaponAddDamage(_weaponId);
        } catch (Exception e) {
            System.out.println("武器追加ダメージエラー");
        }
        
        //スキル、料理などのダメージリダクション
        int damagereduction = 0;
        if (_targetPc.hasSkillEffect(COOK_STR) || _targetPc.hasSkillEffect(COOK_DEX) || _targetPc.hasSkillEffect(COOK_INT)) { // リニューアル料理
        	damagereduction += 2;
        }
        if(_targetPc.hasSkillEffect(COOK_GROW)){
        	damagereduction += 2;
        }
        // 戦士スキル：アーマーガード - キャラクターのAC / 10のダメージ減少効果を得る。
        if (SkillsTable.getInstance().spellCheck(_targetPc.getId(), 237)) {
        	if(_targetPc.getAC().getAc() < -10){
        		damagereduction += _targetPc.getAC().getAc() / -10;
        	}
        }
        

        if (_targetPc.hasSkillEffect(REDUCTION_ARMOR)) {
            int targetPcLvl = _targetPc.getLevel();
            if (targetPcLvl < 50) {
                targetPcLvl = 50;
            }
            damagereduction += (targetPcLvl - 50) / 5 + 1;
        }

        if (_targetPc.hasSkillEffect(EARTH_GUARDIAN)) {
        	damagereduction += 2;
        }
        
        dmg -= roomtisDecreaseDamage();

        if (_targetPc.hasSkillEffect(DRAGON_SKIN)) {
            if (_targetPc.getLevel() >= 80) {
            	damagereduction += 5 + ((_targetPc.getLevel() - 78) / 2);
            } else {
            	damagereduction += 5;
            }
        }
        if (_targetPc.hasSkillEffect(PATIENCE)) {
        	damagereduction += 2;
        }        
        if (_targetPc.hasSkillEffect(FEATHER_BUFF_A)) {
        	damagereduction += 3;
        }
        if (_targetPc.hasSkillEffect(FEATHER_BUFF_B)) {
        	damagereduction += 2;
        }
        if (_targetPc.hasSkillEffect(RANK_BUFF_2) || _targetPc.hasSkillEffect(RANK_BUFF_3)
            || _targetPc.hasSkillEffect(RANK_BUFF_4)) {
        
        	damagereduction += 2;
        }
        if (_targetPc.hasSkillEffect(RANK_BUFF_5)){ //通常のが号
        	damagereduction += 8;
        }
        if (_targetPc.hasSkillEffect(CLAN_BUFF4)) {
        	damagereduction += 1;
        }
     /*   for (L1DollInstance doll : _targetPc.getDollList()) {// マジックドールによるダメージ減少。ドルゴールレム人形
			dmg -= doll.getDamageReductionByDoll();
		}
        dmg -= _targetPc.getDamageReductionByArmor(); // 防具によるダメージ減少
*/        
       /* _ignorereductionbyarmor = armor.getItem().getIgnoreReductionByArmor(); //リダクション無視
        _ignorereductionbyweapon = weapon.getItem().getIgnoreReductionByWeapon(); //リダクション無視
        int ignorereduction = _ignorereductionbyarmor + _ignorereductionbyweapon;
        int damagereductiontotal = _targetPc.getDamageReductionByArmor() + damagereduction;
        
        if(damagereduction >= ignorereduction){
        	damagereduction -= ignorereduction;
        }
        dmg += _ignorereductionbyarmor;
        dmg += _ignorereductionbyweapon;
        if(_pc.getInventory().checkEquipped(1)){
        	if(damagereduction > 0 && damagereduction <= 12){
        		damagereduction = 0;
        	}else{
        		damagereduction -= 12;
        	}
        }*/
        dmg -= damagereduction;
        
        //スキル、料理などのダメージリダクション
        
        
        if (_pc.hasSkillEffect(L1SkillId.RANK_BUFF_3) || _pc.hasSkillEffect(L1SkillId.RANK_BUFF_4)) {
        	dmg += 2;
        }
       
       
        // クランバフpvp 
        if (_pc.hasSkillEffect(L1SkillId.RANK_BUFF_3)){
            dmg += 2;
        }
        if (_pc.hasSkillEffect(L1SkillId.RANK_BUFF_4)){
        	dmg += 2;
        }
        
        
        if (_pc.hasSkillEffect(L1SkillId.DESTROY)) {
            if (_pc.getWeapon().getItem().getType() == 18)
                ArmorDestory();
        } 
        if (_targetPc.hasSkillEffect(ABSOLUTE_BARRIER)) {
            dmg = 0;
        }
        if (_targetPc.hasSkillEffect(ICE_LANCE)) {
            dmg = 0;
        }
        if (_targetPc.hasSkillEffect(EARTH_BIND)) {
            dmg = 0;
        }

        if (_targetPc.hasSkillEffect(PHANTASM)) {
            _targetPc.removeSkillEffect(PHANTASM);
        }
        if (_targetPc.hasSkillEffect(IllUSION_AVATAR)) {
        	dmg += (dmg / 5);
        }
        if (_targetPc.hasSkillEffect(IMMUNE_TO_HARM)) {
        	dmg -= (dmg * 0.3);//4
        }
        dmg += roomtisAddDamage(); // 黒い光ピアス追加ダメージ処理


		for (L1DollInstance doll : _pc.getDollList()) {// マジックドールドールによる追加ダメージ
			if (doll == null)
				continue;
			if (_weaponType != 20 && _weaponType != 62) {
				dmg += doll.getDamageByDoll();
			}
			dmg += doll.attackPixieDamage(_pc, _targetPc);
			doll.getPixieGreg(_pc, _targetPc);
		}

        // 戦士スキルPC  -  PC
        // クラッシュ：攻撃者のレベルに50％程度をダメージに反映する。
        if (SkillsTable.getInstance().spellCheck(_pc.getId(), 236)) {
            int chance = _random.nextInt(100) + 1;
            if (13 >= chance) { // クラッシュ：レベル分割2のダメージ
                //
                int alpha_dmg = _pc.getLevel() / 2;
                if (SkillsTable.getInstance().spellCheck(_pc.getId(), 234)) {//ピュリ：クラッシュから出てきたダメージに2倍
                    chance = _random.nextInt(100) + 1;
                    if (2 >= chance) { // ピュリ確率
                        dmg += dmg + _pc.getLevel();
                        // 成功時イペク2つ出て行くだろ
                        _pc.sendPackets(new S_SkillSound(_targetPc.getId(), 12489));
                        _targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 12489));
                    }
                }
                dmg += alpha_dmg;
                // クラッシュはクラッシュエフェクトのまま処理します。
                _pc.sendPackets(new S_SkillSound(_targetPc.getId(), 12487));
                _targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 12487));
            }
        }

        if (_pc.hasSkillEffect(L1SkillId.LORDS_BUFF)) {
            if (_pc.getClanRank() >= L1Clan.GUARDIAN)
                dmg += 5;
		}
        int dolldamagereduction = 0;
        for (L1DollInstance doll : _targetPc.getDollList()) {// マジックドールによるダメージ減少。ドルゴールレム人形
        	dolldamagereduction = doll.getDamageReductionByDoll();
		}
        dmg -= dolldamagereduction;
        int itemamagereduction= _targetPc.getDamageReductionByArmor(); // 防具によるダメージ減少
        
        dmg -= itemamagereduction;
        int totaldamagereduction = 0;
        totaldamagereduction = dolldamagereduction + itemamagereduction + damagereduction;	
        if(_pc.getInventory().checkEquipped(202011)){
        	if(_pc.getInventory().checkEquipped(22209)
        		|| _pc.getInventory().checkEquipped(22210)){
        		if(totaldamagereduction > 0 && totaldamagereduction <= 15){
            		dmg += totaldamagereduction;
            	}else{
            		dmg += 15;
            	}	
        	}else{
        		if(totaldamagereduction > 0 && totaldamagereduction <= 12){
        		dmg += totaldamagereduction;
        	}else{
        		dmg += 12;
        	}
        	}
        	
        }
        if((_pc.getInventory().checkEquipped(22208) || _pc.getInventory().checkEquipped(22209)
        		|| _pc.getInventory().checkEquipped(22210) || _pc.getInventory().checkEquipped(22211))
        		&& !_pc.getInventory().checkEquipped(202011)){
        	if(_pc.getInventory().checkEquipped(22208)){
        		L1ItemInstance item = _pc.getInventory().findEquippedItemId(22208);
        		if(item.getEnchantLevel() == 7){
        			dmg += 1;
        		}else if(item.getEnchantLevel() == 8){
        			dmg += 2;
        		}else if(item.getEnchantLevel() == 9){
        			dmg += 3;
        		}
        	}if(_pc.getInventory().checkEquipped(22209)){
        		L1ItemInstance item = _pc.getInventory().findEquippedItemId(22209);
        		if(item.getEnchantLevel() == 7){
        			dmg += 1;
        		}else if(item.getEnchantLevel() == 8){
        			dmg += 2;
        		}else if(item.getEnchantLevel() == 9){
        			dmg += 3;
        		}
        	}if(_pc.getInventory().checkEquipped(22210)){
        		L1ItemInstance item = _pc.getInventory().findEquippedItemId(22210);
        		if(item.getEnchantLevel() == 7){
        			dmg += 1;
        		}else if(item.getEnchantLevel() == 8){
        			dmg += 2;
        		}else if(item.getEnchantLevel() == 9){
        			dmg += 3;
        		}
        	}if(_pc.getInventory().checkEquipped(22211)){
        		L1ItemInstance item = _pc.getInventory().findEquippedItemId(22211);
        		if(item.getEnchantLevel() == 7){
        			dmg += 1;
        		}else if(item.getEnchantLevel() == 8){
        			dmg += 2;
        		}else if(item.getEnchantLevel() == 9){
        			dmg += 3;
        		}
        	}
        	if(totaldamagereduction > 0 && totaldamagereduction <= 3){
        		dmg += totaldamagereduction;
        	}else{
        		dmg += 3;
        	}
        }
        
        /** 対象の属性エンチャントによるダメージ演算 **/
        dmg += fishAttrEnchantEffect();

        /** 対象Buffによるダメージ演算 **/
        //dmg += toPcBuffDmg(dmg);

        /** キャラクター別追加ダメージ、追加リダクション、確率 **/
        if (_calcType == PC_PC) {
            if (_pc.getAddDamageRate() >= CommonUtil.random(100)) {
                dmg += _pc.getAddDamage();
            }
            if (_targetPc.getAddReductionRate() >= CommonUtil.random(100)) {
                dmg -= _targetPc.getAddReduction();
            }
        }
        /** キャラクター別追加ダメージ、追加リダクション、確率 **/

        /** 70レベルから追加打撃+ 1 **/
       // dmg += Math.max(0, _pc.getLevel() - 70) * 1;

        /** アーマーブレイク */
        if (_targetPc.hasSkillEffect(ARMOR_BRAKE)) { // アーマーブレイク
            if (_weaponType != 20 && _weaponType != 62) {
                dmg *= 1.25;
            }
        }
        /** アーマーブレイク */

        /** トゥルーターゲット **/
        if (_targetPc.getTrueTarget() > 0) {
            dmg *= 1 + (_targetPc.getTrueTarget() / 100);
        }
        /** トゥルーターゲット **/

        /*** 新規レベルの保護 ***/
        /*if (_calcType == PC_PC) {
            int castle_id = L1CastleLocation.getCastleIdByArea(_pc);
            if (castle_id == 0) {
                if (_targetPc.getLevel() < Config.AUTO_REMOVELEVEL || _pc.getLevel() < Config.AUTO_REMOVELEVEL) {
                    dmg /= 2;
                    _pc.sendPackets(new S_SystemMessage("新規レベルはダメージの50％だけがかかります。"));
                    _targetPc.sendPackets(new S_SystemMessage("新規レベルはダメージを50％だけます。"));
                }
            }
        }*/
        /*** 新規レベルの保護 ***/

        /** 新規血盟攻撃途方もなく **/
       if (_calcType == PC_PC) {
            int castle_id = L1CastleLocation.getCastleIdByArea(_pc);
            boolean isAliveBoss = BossAlive.getInstance().isBossAlive(_targetPc.getMapId());
            if (castle_id == 0 && !isAliveBoss ) {
                if (_pc.getClanid() == Config.NEW_CLAN || _targetPc.getClanid() == Config.NEW_CLAN) {
                    if (Config.NEW_CLAN_PROTECTION_PROCESS) {
                        _isHit = false;
                        _pc.sendPackets(new S_SystemMessage("新規保護血盟は相互に攻撃されていません。"));
                        _targetPc.sendPackets(new S_SystemMessage("新規保護血盟は相互に攻撃されていません。"));
                    } else {
                        dmg /= 2;
                        _pc.sendPackets(new S_SystemMessage("新規保護血盟はダメージの50％だけがかかります。"));
                        _targetPc.sendPackets(new S_SystemMessage("新規保護血盟はダメージを50％だけます。"));
                    }
                }
            }
        }
        /** 新規血盟攻撃途方もなく **/

        if (_pc.hasSkillEffect(BURNING_SLASH)) {
            if (_weaponType != 20) {
                dmg += 30;
                _pc.sendPackets(new S_SkillSound(_targetPc.getId(), 6591));
                _pc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 6591));
                _pc.removeSkillEffect(BURNING_SLASH);
            }
        }
    /**/
        //反逆者の盾ダメージ減少処理
        if (_targetPc.getInventory().checkEquipped(22263)) {//反逆者の盾
            int chance = _random.nextInt(100);
            L1ItemInstance item = _targetPc.getInventory().findEquippedItemId(22263);
            int enchant = item.getEnchantLevel();
            if (chance <= 1 + (enchant * 2)) {
                dmg -= 50;
                _targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 6320));
                _targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 6320));
            }
        }
        if (_targetPc.getInventory().checkEquipped(222355)) {// 神聖なエルヴンシールド
            int chance = _random.nextInt(100) + 1;
            L1ItemInstance item = _targetPc.getInventory().findEquippedItemId(222355);
            if (chance <= item.getEnchantLevel()) {
                dmg -= 20;
                _targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 14543));
                _targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 14543));
            }
        }
        // リンドビオルの加護
        int chance6 = _random.nextInt(100) + 1;
        if (dmg > 25) {
            if (_target != _targetNpc) {
                if (_targetPc.getInventory().checkEquipped(22204)) {// リンド腕力
                    if (chance6 <= 5) {
                        short getMp = (short) (_targetPc.getCurrentMp() + 10);
                        _targetPc.setCurrentMp(getMp);
                        _targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 2188));
                        _targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 2188));
                    }
                } else if (_targetPc.getInventory().checkEquipped(22205)) {// リンド先見の明
                    if (chance6 <= 5) {
                        short getMp = (short) (_targetPc.getCurrentMp() + 15);
                        _targetPc.setCurrentMp(getMp);
                        _targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 2188));
                        _targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 2188));
                    }
                } else if (_targetPc.getInventory().checkEquipped(22206)// リンド耐久
                        || _targetPc.getInventory().checkEquipped(22207)) {// リンド馬力
                    if (chance6 <= 5) {
                        short getMp = (short) (_targetPc.getCurrentMp() + 20);
                        _targetPc.setCurrentMp(getMp);
                        _targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 2188));
                        _targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 2188));
                    }
                }
            }
        }

        // パプリオンの加護
        int chance5 = _random.nextInt(100) + 1;
            if (_target != null) {
                int dmg2 = 0;
                int plus = 0;
                if (_targetPc.getInventory().checkEquipped(22200) || // パプ腕力
                        _targetPc.getInventory().checkEquipped(22201) || // パプ先見の明
                        _targetPc.getInventory().checkEquipped(22202) || // パプ耐久
                        _targetPc.getInventory().checkEquipped(22203)) { // パプ馬力
                    if (chance5 <= 6) { // 元5である
                        L1ItemInstance item = _targetPc.getInventory().findEquippedItemId(22200);
                        L1ItemInstance item1 = _targetPc.getInventory().findEquippedItemId(22201);
                        L1ItemInstance item2 = _targetPc.getInventory().findEquippedItemId(22202);
                        L1ItemInstance item3 = _targetPc.getInventory().findEquippedItemId(22203);
                        if (item.getEnchantLevel() >= 7 && item.getEnchantLevel() <= 9) {
                            plus = item.getEnchantLevel() - 6;
                        } else if (item1.getEnchantLevel() >= 7 && item1.getEnchantLevel() <= 9) {
                            plus = item.getEnchantLevel() - 6;
                        } else if (item2.getEnchantLevel() >= 7 && item2.getEnchantLevel() <= 9) {
                            plus = item.getEnchantLevel() - 6;
                        } else if (item3.getEnchantLevel() >= 7 && item3.getEnchantLevel() <= 9) {
                            plus = item.getEnchantLevel() - 6;
                        } else if (item.getEnchantLevel() > 9 || item1.getEnchantLevel() > 9 || item2.getEnchantLevel() > 9
                                || item3.getEnchantLevel() > 9) {
                            plus = 3;
                        }
                        if (_targetPc.hasSkillEffect(L1SkillId.POLLUTE_WATER)) {
                            dmg2 += (40 + _random.nextInt(15) + (plus * 10)) / 2; // フルートウォーター場合の半分//元のランダム数値30である
                        }
                        if (_targetPc.hasSkillEffect(L1SkillId.WATER_LIFE)) {
                            dmg2 += (40 + _random.nextInt(15) + (plus * 10)) * 2; // ウォーターライフ場合倍増//元のランダム数値30である
                        }
                        dmg2 += 40 + _random.nextInt(15) + (plus * 10); // 回復率=基本50回復+ランダム（1〜30）//元のランダム数値30である
                        _targetPc.setCurrentHp(_targetPc.getCurrentHp() + dmg2);
                        _targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 2187));
                        _targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 2187));
                    }
                }
            }
          
		// 神聖なエルヴンプレートメイル
		int chance66 = _random.nextInt(100) + 1;
		if (_target != null) {
			int dmg2 = 0;
			int plus = 0;
			if (_targetPc.getInventory().checkEquipped(222351)) {
				if (chance66 <= 5) { // 元5である
					if (_targetPc.hasSkillEffect(L1SkillId.POLLUTE_WATER)) {
						dmg2 += (25 + _random.nextInt(15) + (plus * 10)) / 2; //
					}
					if (_targetPc.hasSkillEffect(L1SkillId.WATER_LIFE)) {
						dmg2 += (25 + _random.nextInt(15) + (plus * 10)) * 2; //
					}
					dmg2 += 25 + _random.nextInt(15) + (plus * 10); //
					_targetPc.setCurrentHp(_targetPc.getCurrentHp() + dmg2);
					_targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 15355));
					_targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 15355));
				}
			}
		}
        try {
            if (_targetPc.isCrown()) {
                dmg -= CharacterReduc.getInstance().getCharacterReduc(0);
            } else if (_targetPc.isKnight()) {
                dmg -= CharacterReduc.getInstance().getCharacterReduc(1);
            } else if (_targetPc.isElf()) {
                dmg -= CharacterReduc.getInstance().getCharacterReduc(2);
            } else if (_targetPc.isWizard()) {
                dmg -= CharacterReduc.getInstance().getCharacterReduc(3);
            } else if (_targetPc.isDarkelf()) {
                dmg -= CharacterReduc.getInstance().getCharacterReduc(4);
            } else if (_targetPc.isBlackwizard()) {
                dmg -= CharacterReduc.getInstance().getCharacterReduc(5);
            } else if (_targetPc.isDragonknight()) {
                dmg -= CharacterReduc.getInstance().getCharacterReduc(6);
            } else if (_targetPc.isWarrior()) {
                dmg -= CharacterReduc.getInstance().getCharacterReduc(7);
            }
        } catch (Exception e) {
            System.out.println("Character Add Reduction Error");
        }

        try {
            if (_pc.isCrown()) {
                dmg += CharacterBalance.getInstance().getCharacterBalance(0);
            } else if (_pc.isKnight()) {
                dmg += CharacterBalance.getInstance().getCharacterBalance(1);
            } else if (_pc.isElf()) {
                dmg += CharacterBalance.getInstance().getCharacterBalance(2);
            } else if (_pc.isWizard()) {
                dmg += CharacterBalance.getInstance().getCharacterBalance(3);
            } else if (_pc.isDarkelf()) {
                dmg += CharacterBalance.getInstance().getCharacterBalance(4);
            } else if (_pc.isBlackwizard()) {
                dmg += CharacterBalance.getInstance().getCharacterBalance(5);
            } else if (_pc.isDragonknight()) {
                dmg += CharacterBalance.getInstance().getCharacterBalance(6);
            } else if (_pc.isWarrior()) {
            	dmg += CharacterBalance.getInstance().getCharacterBalance(7);
            }
        } catch (Exception e) {
            System.out.println("Character Add Damege Error");
        }

        /** バーニングスピリッツ、エレメンタルファイヤー、ブレイブメンタル1.5倍スキルエフェクトとツタの部分 **/
        int chance41 = _random.nextInt(100) + 1;
        if (_weaponType != 20 && _weaponType != 62 && _weaponType2 != 17) {
            if (_pc.hasSkillEffect(BURNING_SPIRIT) || _pc.hasSkillEffect(ELEMENTAL_FIRE) || _pc.hasSkillEffect(BRAVE_AURA)) {
                if (chance41 <= 15) {
                    if (_pc.isDarkelf()) {
                        dmg *= 1.3;
                        _targetNpc.broadcastPacket(new S_SkillSound(_targetNpc.getId(), 7727));
                    } else {
                        dmg *= 1.5;
                        _targetNpc.broadcastPacket(new S_SkillSound(_targetNpc.getId(), 7727));
                    }
                }
            }
        }

        // キャラクター間ダメージ外部化処理
        /*if (_calcType == PC_PC) {
            if (_pc.isCrown()) {
                dmg += Config.PRINCE_ADD_DAMAGEPC;
            } else if (_pc.isKnight()) {
                dmg += Config.KNIGHT_ADD_DAMAGEPC;
            } else if (_pc.isElf()) {
                dmg += Config.ELF_ADD_DAMAGEPC;
            } else if (_pc.isDarkelf()) {
                dmg += Config.DARKELF_ADD_DAMAGEPC;
            } else if (_pc.isWizard()) {
                dmg += Config.WIZARD_ADD_DAMAGEPC;
            } else if (_pc.isDragonknight()) {
                dmg += Config.DRAGONKNIGHT_ADD_DAMAGEPC;
            } else if (_pc.isBlackwizard()) {
                dmg += Config.BLACKWIZARD_ADD_DAMAGEPC;
            } else if (_pc.is戦士()) {
                dmg += Config.戦士_ADD_DAMAGEPC;
            }
        }
*/
        /** ACによるダメージ減少修正 **/
        if (dmg <= 0) {
            _isHit = false;
        }
/**アビスポイントによるリドク	 */
		
		if (_targetPc.getPeerage() == 1){ dmg -=0.5;
		} else if (_targetPc.getPeerage() == 2){ dmg -= 1;
		} else if (_targetPc.getPeerage() == 3){ dmg -= 1.5;
		} else if (_targetPc.getPeerage() == 4){ dmg -= 2;
		} else if (_targetPc.getPeerage() == 5){ dmg -= 2.5;
		} else if (_targetPc.getPeerage() == 6){ dmg -= 3;
		} else if (_targetPc.getPeerage() == 7){ dmg -= 3.5;
		} else if (_targetPc.getPeerage() == 8){ dmg -= 4;
		} else if (_targetPc.getPeerage() == 9){ dmg -= 4.5;
		} else if (_targetPc.getPeerage() == 10){ dmg -= 5;
		} else if (_targetPc.getPeerage() == 11){ dmg -= 5.5;
		} else if (_targetPc.getPeerage() == 12){ dmg -= 6;
		} else if (_targetPc.getPeerage() == 13){ dmg -= 6.5;
		} else if (_targetPc.getPeerage() == 14){ dmg -= 7;
		} else if (_targetPc.getPeerage() == 15){ dmg -= 7.5;
		} else if (_targetPc.getPeerage() == 16){ dmg -= 8;
		} else if (_targetPc.getPeerage() == 17){ dmg -= 8.5;
		} else if (_targetPc.getPeerage() == 18){ dmg -= 9; 
		}
        return (int) dmg;
    }

    // ●●●● プレイヤーからNPCへのダメージ算出 ●●●●
    private int calcPcNpcDamage() {
        if (_targetNpc == null || _pc == null) {
            _isHit = false;
            _drainHp = 0;
            return 0;
        }
    	if (_pc instanceof L1RobotInstance) {
			if (((L1RobotInstance) _pc).huntingBot_Location.equalsIgnoreCase("地底")
					|| ((L1RobotInstance) _pc).huntingBot_Location.startsWith("忘れられた島")
					|| ((L1RobotInstance) _pc).huntingBot_Location.equalsIgnoreCase("船舶睡眠")
					|| ((L1RobotInstance) _pc).huntingBot_Location.equalsIgnoreCase("象牙の塔4階")
					|| ((L1RobotInstance) _pc).huntingBot_Location.equalsIgnoreCase("象牙の塔5階")) {
				if (_pc.getCurrentWeapon() == 46 // 短剣
						|| _pc.getCurrentWeapon() == 20
						|| _pc.getCurrentWeapon() == 24) {// 弓
					return _random.nextInt(50) + 100;
				} else {
					return _random.nextInt(50) + 50;
				}
			} else {
				if (_pc.getCurrentWeapon() == 46 // 短剣
						|| _pc.getCurrentWeapon() == 20
						|| _pc.getCurrentWeapon() == 24) {// 弓
					return _random.nextInt(30) + 70;
				} else {
					return _random.nextInt(40) + 40;
				}
			}
		}
        int weaponMaxDamage = 0;
        boolean secondw = false;

        if (_targetNpc.getNpcTemplate().get_size().equalsIgnoreCase("small") && _weaponSmall > 0) {
            weaponMaxDamage = _weaponSmall;
        } else if (_targetNpc.getNpcTemplate().get_size().equalsIgnoreCase("large") && _weaponLarge > 0) {
            weaponMaxDamage = _weaponLarge;
        }

        // weaponMaxDamage += _weaponAddDmg;

        int weaponDamage = 0;

        if (_weaponType == 58) { // クロウ
        	int crowchance = _random.nextInt(100) + 1;
        	if(crowchance <= _weaponDoubleDmgChance){
        		weaponDamage = weaponMaxDamage + _weaponAddDmg;
                // _attackType = 2;
                _pc.sendPackets(new S_SkillSound(_pc.getId(), 3671));
                _pc.broadcastPacket(new S_SkillSound(_pc.getId(), 3671));
        	}else{
        		weaponDamage = _random.nextInt(weaponMaxDamage) + _weaponAddDmg + 1;
        	}
        }

        else if (_weaponType == 0) { // 素手、弓、癌トートレッド_weaponType == 0 ||
            weaponDamage = 0;
        } else {
            // weaponDamage = _random.nextInt(weaponMaxDamage) + 1;
            weaponDamage = _random.nextInt(weaponMaxDamage) + _weaponAddDmg;
        }

        if (_pc.hasSkillEffect(SOUL_OF_FLAME)) {
            if (_weaponType != 20 && _weaponType != 62) {
                // weaponDamage = weaponMaxDamage;
                weaponDamage = weaponMaxDamage + _weaponAddDmg;
            }
        }
        if(_weaponType != 0 ){
            if (_weaponType != 20 && _weaponType != 62) { // 近距離
                int Dmgcritical = CalcStat.calcDmgCritical(_pc.getAbility().getTotalStr())+_pc.getDmgCritical();
                int chance = _random.nextInt(100) + 1;
                //記章クリティカル
                if (_pc.getInventory().checkEquipped(900032)){
                	L1ItemInstance item = _pc.getInventory().findEquippedItemId(900032);
                	if(item.getEnchantLevel() == 6){
                		Dmgcritical += 1;
                	}else if(item.getEnchantLevel() == 7){
                		Dmgcritical += 3;
                	}else if(item.getEnchantLevel() >= 8){
                		Dmgcritical += 5;
                	}
                }if (_pc.getInventory().checkEquipped(22208)){ // 塗って腕力
                	L1ItemInstance item = _pc.getInventory().findEquippedItemId(22208);
                	if(item.getEnchantLevel() == 7){
                		Dmgcritical += 1;
                	}else if(item.getEnchantLevel() == 8){
                		Dmgcritical += 2;
                	}else if(item.getEnchantLevel() >= 9){
                		Dmgcritical += 3;
                	}
                }if (_pc.getInventory().checkEquipped(22209)){ //塗って忍耐
                	L1ItemInstance item = _pc.getInventory().findEquippedItemId(22209);
                	if(item.getEnchantLevel() == 7){
                		Dmgcritical += 1;
                	}else if(item.getEnchantLevel() == 8){
                		Dmgcritical += 2;
                	}else if(item.getEnchantLevel() >= 9){
                		Dmgcritical += 3;
                	}
                }
                if (chance <= Dmgcritical) {
                    weaponDamage = weaponMaxDamage + _weaponAddDmg;
                    _isCritical = true;
                }
            } else {
                int Bowcritical = CalcStat.calcBowCritical(_pc.getAbility().getTotalDex())+_pc.getBowDmgCritical();
                int chance = _random.nextInt(100) + 1;
              //記章クリティカル
                if (_pc.getInventory().checkEquipped(900033)){
                	L1ItemInstance item = _pc.getInventory().findEquippedItemId(900033);
                	if(item.getEnchantLevel() == 6){
                		Bowcritical += 1;
                	}else if(item.getEnchantLevel() == 7){
                		Bowcritical += 3;
                	}else if(item.getEnchantLevel() >= 8){
                		Bowcritical += 5;
                	}
                }if (_pc.getInventory().checkEquipped(22210)){
                	L1ItemInstance item = _pc.getInventory().findEquippedItemId(22210);
                	if(item.getEnchantLevel() == 7){
                		Bowcritical += 1;
                	}else if(item.getEnchantLevel() == 8){
                		Bowcritical += 2;
                	}else if(item.getEnchantLevel() >= 9){
                		Bowcritical += 3;
                	}
                }
                if (chance <= Bowcritical) {
                    weaponDamage = weaponMaxDamage + _weaponAddDmg;
                    _isCritical = true;
                }
    	        }
            }
        int weaponTotalDamage = weaponDamage + _weaponEnchant;

        if (_pc.isWarrior() && _pc.hasSkillEffect(L1SkillId.SLAYER) && _pc.getSecondWeapon() != null) {
            int ran = _random.nextInt(100);
            if (ran < 50) {
                secondw = true;
                if (_targetNpc.getNpcTemplate().get_size().equalsIgnoreCase("small") && _SweaponSmall > 0) {
                    weaponMaxDamage = _SweaponSmall + _SweaponAddDmg;
                } else if (_targetNpc.getNpcTemplate().get_size().equalsIgnoreCase("large") && _SweaponLarge > 0) {
                    weaponMaxDamage = _SweaponLarge + _SweaponAddDmg;
                } else {
                    weaponMaxDamage = _SweaponSmall + _SweaponAddDmg;
                }
            }
        }

        weaponTotalDamage += calcMaterialBlessDmg(); // 祝福ダメージボーナス
        /** 轟音の二刀流エンチャンあたり発動確率増加 **/
        if (_weaponId == 203018) {
            _weaponDoubleDmgChance += _pc.getWeapon().getEnchantLevel();
        }

        if (_weaponType == 54 && (_random.nextInt(100) + 1) <= (_weaponDoubleDmgChance - weapon.get_durability()) && _pc.isDarkelf()) { // 二刀流
            weaponTotalDamage *= 2.5;
            // _attackType = 4;
            _pc.sendPackets(new S_SkillSound(_pc.getId(), 3398));
            _pc.broadcastPacket(new S_SkillSound(_pc.getId(), 3398));
        }
        if (_pc.hasSkillEffect(DOUBLE_BRAKE) && (_weaponType == 54 || _weaponType == 58)) {
            int RealSteelLevel = _pc.getLevel();
            if (RealSteelLevel < 45) {
                RealSteelLevel = 45;
            }
            int RealSteelLevelChance = (RealSteelLevel - 45) / 5 + 33;
            if ((_random.nextInt(100) + 1) <= RealSteelLevelChance) {
                weaponTotalDamage *= 2;
                if (_pc.hasSkillEffect(BURNING_SPIRIT)) {
                    _pc.sendPackets(new S_SkillSound(_targetNpc.getId(), 6532));
                    Broadcaster.broadcastPacket(_pc, new S_SkillSound(_targetNpc.getId(), 6532));
                }
            }
        }
      

        double dmg = weaponTotalDamage + _statusDamage;

        if (_weaponType2 == 17) {
            dmg = L1WeaponSkill.KiringkuDamage(_pc, _target);
        }
        if (_weaponType != 20 && _weaponType != 62) {
            dmg = weaponTotalDamage + _statusDamage + _pc.getDmgup() + _pc.getDmgRate();
        } else {
            dmg = weaponTotalDamage + _statusDamage + _pc.getBowDmgup() + _pc.getBowDmgRate();
        }
        if (_pc.hasSkillEffect(BURNING_SPIRIT) && _pc.isDarkelf()) { // バーニングスピリッツ
            if ((_random.nextInt(100) + 1) <= 33) {
                dmg *= 3 / 2;
            }
        }
        dmg += monsterAttrEnchantEffect(); // 属性ダメージ

        if (_weaponType == 20) { // 弓
            if (_arrow != null) {
                int add_dmg = 0;
                if (_targetNpc.getNpcTemplate().get_size().equalsIgnoreCase("large")) {
                    add_dmg = _arrow.getItem().getDmgLarge();
                } else {
                    add_dmg = _arrow.getItem().getDmgSmall();
                }
                if (add_dmg == 0) {
                    add_dmg = 1;
                }
                if (_targetNpc.getNpcTemplate().is_hard() && weapon.getItem().get_penetration() != 1) {
					add_dmg /= 2;
				}
            } else if (_weaponId == 190 || _weaponId == 10000 || _weaponId == 202011) { // サイハの弓
                dmg = dmg + _random.nextInt(15) + 4;
            }

        } else if (_weaponType == 62) { // がんトートレッド
            int add_dmg = 0;
            if (_targetNpc.getNpcTemplate().get_size().equalsIgnoreCase("large")) {
                add_dmg = _sting.getItem().getDmgLarge();
            } else {
                add_dmg = _sting.getItem().getDmgSmall();
            }
            if (add_dmg == 0) {
                add_dmg = 1;
            }
            dmg = dmg + _random.nextInt(add_dmg) + 1 + attrArrow(_arrow, _targetNpc);
        }
        
        /** 本サーバー10剣以上ツタ+1表記効果 **/
        if (_weaponType != 0 && _weaponType != 20) {
            switch (weapon.getEnchantLevel()) {
            case 10:
                dmg += 1;
                break;
            case 11:
                dmg += 2;
                break;
            case 12:
                dmg += 3;
                break;
            case 13:
                dmg += 4;
                break;
            case 14:
                dmg += 5;
                break;
            case 15:
                dmg += 6;
                break;
            default:
                break;
            }
        }
        
        /** ドゥスルエンチャン星ツタ2ずつ **/
        if(_weaponId == 66){
        	dmg += weapon.getEnchantLevel();
        }
        /** エンチャントによるツタ関連 **/
        /*if (_weaponType != 0 && _weaponType != 20) {
            switch (weapon.getEnchantLevel()) {
            case 7:
                dmg += 1;
                break;
            case 8:
                dmg += 2;
                break;
            case 9:
                dmg += 3;
                break;
            case 10:
                dmg += 4;
                break;
            case 11:
                dmg += 5;
                break;
            case 12:
                dmg += 6;
                break;
            case 13:
                dmg += 7;
                break;
            case 14:
                dmg += 8;
                break;
            case 15:
                dmg += 9;
                break;
            default:
                break;
            }
        }*/
        switch (_weaponId) {
        case 307:
        case 308:
        case 309:
        case 310:
        case 311:
        case 313:
        case 314:
            dmg = L1WeaponSkill.BlazeShock(_pc, _targetNpc, _weaponEnchant);
            break;
        case 1010:
        case 1011:
        case 1012:
        case 1013:
        case 1014:
            L1WeaponSkill.getDiseaseWeapon(_pc, _targetNpc, _weaponId);
            break;
        case 12: // 風の刃短剣
        case 203020: // 生命の短剣
        case 601: // 破滅のグレートソード
            ruinGreatSword(dmg);
            break;
        case 204: // 真紅のクロスボウ
        case 100204: // 軸真紅のクロスボウ
        case 86: // 赤い影のデュアルブレード
            L1WeaponSkill.RedShadowDualBlade(_pc, _targetNpc);
            break;
        case 1115: // 神妙ソード
        case 1117: // 神妙クロ
            dmg += getEbHP(_pc, _target, 8981, _weaponEnchant);
            break;
        case 1116: // 神妙杖
        case 1118: // 神妙ロングボウ
        case 202011: // がよの激怒
            dmg += getEbMP1(_pc, _target, 8981, _weaponEnchant);
            break;
        case 1109: // 魔族クロウ
        case 1113: // 魔族の剣
        case 1114: // 魔族両手剣
        case 203011: // 魔族両手剣
            dmg += getEbHP(_pc, _target, 8150, _weaponEnchant);
            break;
        case 1110: // 魔族の杖
        case 1112: // 魔族のキーリンク
        case 1111: // 魔族の弓
            dmg += getEbMP(_pc, _target, 8152, _weaponEnchant);
            break;
        case 1108: // 魔族チェーン
            dmg += getEbHP(_pc, _target, 8150, _weaponEnchant);
            L1WeaponSkill.ChainSword(_pc);
            break;
        case 1119: // 極限のチェーンソード
            dmg += extremeChainSword(_pc, _target, 3685, _weaponEnchant);
            L1WeaponSkill.ChainSword(_pc);
            break;
        case 1123: // ブラッドサッカー
        case 202013:
            L1WeaponSkill.ChainSword(_pc);
            bloodSucker(dmg, _weaponEnchant);
            break;
        case 500:// デストラクタのチェーンソード
        case 501:// 破滅者のチェーンソード
        case 1104:// エルモアチェーンソード
        case 1132:// ベビーテルランチェーンソード
            L1WeaponSkill.ChainSword(_pc);
            break;
        case 312:
            dmg = L1WeaponSkill.ChainSword_BlazeShock(_pc, _targetNpc, _weaponEnchant);
            break;
        case 203017:
            L1WeaponSkill.ChainSword_Destroyer(_pc);
            if (weapon.getEnchantLevel() >= 10)
            dmg += L1WeaponSkill.Destroyer(_pc, _target, 4077, _weaponEnchant);
            break;
        case 203006://台風の斧
        	if (weapon.getEnchantLevel() >= 10)
        	 dmg += L1WeaponSkill.StormAx(_pc, _target, 7977, _weaponEnchant);
        	break;
        case 1136://悪夢のロングボウ
        	 if (weapon.getEnchantLevel() >= 10)
                 dmg += L1WeaponSkill.Nightmare(_pc, _target, 14339, _weaponEnchant);
        	break;
        case 203025://ジンサ
        case 203026:
       	 if (weapon.getEnchantLevel() >= 10)
                dmg += L1WeaponSkill.Jinsa(_pc, _target, 8032, _weaponEnchant);
       	break;
        case 202001: // 歓迎のチェーンソード
            dmg += L1WeaponSkill.ChainSword_Welcome(_pc, _target, _weaponEnchant);
            L1WeaponSkill.ChainSword(_pc);
            break;
        case 1124: // 破壊の二刀流
        case 1125: // 破壊のクロウ
        case 11125:// 祝福破壊の二刀流
            dmg += L1WeaponSkill.DestructionDualBlade_Crow(_pc, _target, 9359, _weaponEnchant);
            break;
        case 600: // 脳身体検査
            dmg += L1WeaponSkill.LightningEdge(_pc, _target, 3940, _weaponEnchant);
            break;
        case 604: // 酷寒のウィンドウ
            dmg += L1WeaponSkill.ExColdWind(_pc, _target, 3704, _weaponEnchant);
            break;
        case 605: // 狂風の斧
        case 203015: // 疾風の斧
            dmg += L1WeaponSkill.InsanityWindAx(_pc, _target, 5524, _weaponEnchant);
            break;
        case 191: // サルチョンの弓
            dmg += L1WeaponSkill.AngelSlayer(_pc, _target, 9361, _weaponEnchant);
            break;
        case 1135: // 共鳴のキーリンク
            dmg += L1WeaponSkill.Kiringku_Resonance(_pc, _target, 5201, _weaponEnchant);
            break;
        case 1120: // 冷え性のキーリンク
            dmg += L1WeaponSkill.Kiringku_Cold(_pc, _target, 6553, _weaponEnchant);
            break;
        case 202012: // ヒペリオンの絶望
            dmg += L1WeaponSkill.HypelionsDespair(_pc, _target, 12248, _weaponEnchant);
            break;
        case 283: // ヴァラカスのキーリンク
            dmg += L1WeaponSkill.Redskill(_pc, _target, 10405, _weaponEnchant);
            break;
        case 294: // 君主の剣
            dmg += L1WeaponSkill.LordSword(_pc, _target, 4842, _weaponEnchant);
            break;
        case 58: // デスナイトのフレイムブレード
            dmg += L1WeaponSkill.DeathKnightFlameBlade(_pc, _target, _weaponEnchant, 7300);
            break;
        case 54: // カーツの剣
            dmg += L1WeaponSkill.KurtzsSword(_pc, _target, _weaponEnchant, 10405);
            break;
        case 124: // バフォメットスタッフ
            dmg += L1WeaponSkill.BaphometStaff(_pc, _target, _weaponEnchant, 129);
            break;
        case 202003: // ゼロスの杖
            dmg += L1WeaponSkill.ZerosWand(_pc, _target, _weaponEnchant, 11760);
            break;
        case 134: // 修正結晶棒
            dmg += L1WeaponSkill.HolyHedronStaff(_pc, _target, _weaponEnchant, 10405);
            dmg+=getEbMP(_pc, _target, 8152, _weaponEnchant);
            break;
        case 603: // 天使の杖
            L1WeaponSkill.AngelStaff(_pc, _target, _weaponEnchant);
            break;
        default:
            dmg += L1WeaponSkill.getWeaponSkillDamage(_pc, _target, _weaponId);
            break;
        }

        if (_weaponType == 0) { // 素手
            dmg = (_random.nextInt(5) + 4) / 4;
        }

        try {
            dmg += WeaponAddDamage.getInstance().getWeaponAddDamage(_weaponId);
        } catch (Exception e) {
            System.out.println("Weapon Add Damege Error");
        }

        dmg += roomtisAddDamage();  // 黒い光ピアス追加ダメージ処理

        if (_pc.hasSkillEffect(BURNING_SLASH)) {
            if (_weaponType != 20 && _weaponType != 62) {
                dmg += 20;
                _pc.sendPackets(new S_SkillSound(_targetNpc.getId(), 6591));
                _pc.broadcastPacket(new S_SkillSound(_targetNpc.getId(), 6591));
                _pc.removeSkillEffect(BURNING_SLASH);
            }
        }
        for (L1DollInstance doll : _pc.getDollList()) {// マジックドールドールによる追加ダメージ
        	if (doll == null)
        		continue;
        	if (_weaponType != 20 && _weaponType != 62) {
        		dmg += doll.getDamageByDoll();
        	}
        	dmg += doll.attackPixieDamage(_pc, _targetNpc);
        	doll.getPixieGreg(_pc, _targetNpc);
        }

        // 戦士スキルPC  -  NPC
        // クラッシュ：攻撃者のレベルに50％程度をダメージに反映する。
        if (SkillsTable.getInstance().spellCheck(_pc.getId(), 236)) {
            int chance = _random.nextInt(100) + 1;
            if (13 >= chance) { // クラッシュの確率
                //
                int alpha_dmg = _pc.getLevel() / 2;
                // ピュリ：クラッシュから出てきたデムジに2倍。
                if (SkillsTable.getInstance().spellCheck(_pc.getId(), 234)) {
                    chance = _random.nextInt(100) + 1;
                    if (1 >= chance) { // ピュリ確率
                        dmg += dmg + _pc.getLevel();
                        // 成功時イペク2つ出て行くだろ
                        _targetNpc.broadcastPacket(new S_SkillSound(_targetNpc.getId(), 12489));
                    }
                }
                dmg += alpha_dmg;
                // クラッシュはクラッシュエフェクトのまま処理します。
                _targetNpc.broadcastPacket(new S_SkillSound(_targetNpc.getId(), 12487));
            }
        }

        /** バーニングスピリッツ、エレメンタルファイヤー、ブレイブメンタル1.5倍スキルエフェクトとツタの部分 **/
        int chance41 = _random.nextInt(100) + 1;
        if (_weaponType != 20 && _weaponType != 62 && _weaponType2 != 17) {
            if (_pc.hasSkillEffect(BURNING_SPIRIT) || _pc.hasSkillEffect(ELEMENTAL_FIRE) || _pc.hasSkillEffect(BRAVE_AURA)) {
                if (chance41 <= 15) {
                    if (_pc.isDarkelf()) {
                        dmg *= 1.5;
                        _targetNpc.broadcastPacket(new S_SkillSound(_targetNpc.getId(), 7727));
                    } else {
                        dmg *= 1.5;
                        _targetNpc.broadcastPacket(new S_SkillSound(_targetNpc.getId(), 7727));
                    }
                }
            }
        }

        try {
            if (_pc.isCrown()) {
                dmg += CharacterBalance.getInstance().getCharacterBalance(0);
            } else if (_pc.isKnight()) {
                dmg += CharacterBalance.getInstance().getCharacterBalance(1);
            } else if (_pc.isElf()) {
                dmg += CharacterBalance.getInstance().getCharacterBalance(2);
            } else if (_pc.isWizard()) {
                dmg += CharacterBalance.getInstance().getCharacterBalance(3);
            } else if (_pc.isDarkelf()) {
                dmg += CharacterBalance.getInstance().getCharacterBalance(4);
            } else if (_pc.isBlackwizard()) {
                dmg += CharacterBalance.getInstance().getCharacterBalance(5);
            } else if (_pc.isDragonknight()) {
                dmg += CharacterBalance.getInstance().getCharacterBalance(6);
            } else if (_pc.isWarrior()) {
            	dmg += CharacterBalance.getInstance().getCharacterBalance(7);
            }
        } catch (Exception e) {
            System.out.println("Character Add Damege Error");
        }

        dmg -= calcNpcDamageReduction();

        boolean isNowWar = false;
        int castleId = L1CastleLocation.getCastleIdByArea(_targetNpc);
        if (castleId > 0) {
            isNowWar = WarTimeController.getInstance().isNowWar(castleId);
        }
        if (!isNowWar) {
            if (_targetNpc instanceof L1PetInstance) {
                dmg /= 8;
            }
            if (_targetNpc instanceof L1SummonInstance) {
                L1SummonInstance summon = (L1SummonInstance) _targetNpc;
                if (summon.isExsistMaster()) {
                    dmg /= 8;
                }
            }
        }

        if (_targetNpc.hasSkillEffect(ICE_LANCE)) {
            dmg = 0;
        }
        if (_targetNpc.hasSkillEffect(EARTH_BIND)) {
            dmg = 0;
        }
        if (_targetNpc.hasSkillEffect(PHANTASM)) {
            _targetNpc.removeSkillEffect(PHANTASM);
        }
        if (dmg <= 0) {
            _isHit = false;
        }
        return (int) dmg;
    }

    // ●●●●NPCからプレイヤーへのダメージ算出 ●●●●
    private int calcNpcPcDamage() {
        if (_npc == null || _targetPc == null)
            return 0;

        
        int lvl = _npc.getLevel();
		double dmg = 0D;
		if (_targetPc instanceof L1RobotInstance) {
			dmg = 20;

		} // ロボットデムガム
		if (lvl < 10) // モプレプが10未満
			dmg = _random.nextInt(lvl) + _npc.getAbility().getTotalStr() + 2;
		else if (lvl >= 10 && lvl < 20) // モプレプが10〜49
			dmg = _random.nextInt(lvl) + _npc.getAbility().getTotalStr() + 2;
		else if (lvl >= 20 && lvl < 30) // モプレプが50〜69
			dmg = _random.nextInt(lvl) + _npc.getAbility().getTotalStr() + 2;
		else if (lvl >= 30 && lvl < 40) // モプレプが50〜69
			dmg = _random.nextInt(lvl) + _npc.getAbility().getTotalStr() + 2;
		else if (lvl >= 40 && lvl < 50) // モプレプが50〜69
			dmg = _random.nextInt(lvl) + _npc.getAbility().getTotalStr() + 3;
		else if (lvl >= 50 && lvl < 60) // モプレプが70〜79
			dmg = _random.nextInt(lvl) + _npc.getAbility().getTotalStr() + 5;
		else if (lvl >= 60 && lvl < 70) // モプレプが80〜86
			dmg = _random.nextInt(lvl) + _npc.getAbility().getTotalStr() + 10;
		else if (lvl >= 70 && lvl < 80) //モプレプが50〜69
			dmg = _random.nextInt(lvl) + _npc.getAbility().getTotalStr() + 15;
		else if (lvl >= 80 && lvl < 87) // モプレプが50〜69
			dmg = _random.nextInt(lvl) + _npc.getAbility().getTotalStr() + 20;
		else if (lvl >= 87) // モプレプが87以上
			dmg = _random.nextInt(lvl) + _npc.getAbility().getTotalStr() + 100;
	

        if (_npc instanceof L1PetInstance) {
            dmg += (lvl / 16); // ペットはLV16に追加打撃
            dmg += ((L1PetInstance) _npc).getDamageByWeapon();
        }
        dmg += _npc.getDmgup();

        if (isUndeadDamage()) {
            // dmg *= 1.1;
            dmg *= 1.2;
        }
        if (_npc.getMapId() == 1700 /*|| _npc.getMapId()== ???*/) {// 忘れられた島なら
        	dmg *= 1.4; //ダメージ倍増
        }
       /* *//** 特定のマップのモンスターセゲ **//*
        if (_npc.getMapId() == 30) {
        	dmg = (dmg * getLeverage()) / 0; //数字を上げるほど。するとれる
        	}*/
        /** 全モンスターセゲ **/
        // dmg = dmg * getLeverage() / 13;//<モンスターの物理ダメージ上げれば弱まる。
        dmg = dmg * getLeverage() / Config.npcdmg; // npc物理ダメージ外部化
        dmg -= calcPcDefense();

        if (_npc.isWeaponBreaked()) { // NPCがウェポンブレイク中。
            dmg *= 0.5;
        }

        for (L1DollInstance doll : _targetPc.getDollList()) {// マジックドールによるダメージ減少。ストーンゴーレム
        	dmg -= doll.getDamageReductionByDoll();
        }

        dmg -= _targetPc.getDamageReductionByArmor(); // 防具によるダメージ減少

        /** 対象Buffによるダメージ演算 **/
        //dmg += toPcBuffDmg(dmg);
      //スキル、料理などのダメージリダクション
        int damagereduction = 0;
        if (_targetPc.hasSkillEffect(COOK_STR) || _targetPc.hasSkillEffect(COOK_DEX) || _targetPc.hasSkillEffect(COOK_INT)) { // リニューアル料理
        	damagereduction += 2;
        }
        if(_targetPc.hasSkillEffect(COOK_GROW)){
        	damagereduction += 2;
        }
        // 戦士スキル：アーマーガード - キャラクターのAC / 10のダメージ減少効果を得る。
        if (SkillsTable.getInstance().spellCheck(_targetPc.getId(), 237)) {
        	if(_targetPc.getAC().getAc() < -10){
        		damagereduction += _targetPc.getAC().getAc() / -10;
        	}
        }

        if (_targetPc.hasSkillEffect(REDUCTION_ARMOR)) {
            int targetPcLvl = _targetPc.getLevel();
            if (targetPcLvl < 50) {
                targetPcLvl = 50;
            }
            damagereduction += (targetPcLvl - 50) / 5 + 1;
        }

        if (_targetPc.hasSkillEffect(EARTH_GUARDIAN)) {
        	damagereduction += 2;
        }
        
        dmg -= roomtisDecreaseDamage();

        if (_targetPc.hasSkillEffect(DRAGON_SKIN)) {
            if (_targetPc.getLevel() >= 80) {
            	damagereduction += 5 + ((_targetPc.getLevel() - 78) / 2);
            } else {
            	damagereduction += 5;
            }
        }
        if (_targetPc.hasSkillEffect(PATIENCE)) {
        	damagereduction += 2;
        }        
        if (_targetPc.hasSkillEffect(FEATHER_BUFF_A)) {
        	damagereduction += 3;
        }
        if (_targetPc.hasSkillEffect(FEATHER_BUFF_B)) {
        	damagereduction += 2;
        }
        if (_targetPc.hasSkillEffect(CLAN_BUFF4)) {
        	damagereduction += 1;
        }     
        dmg -= damagereduction;
        
        if (_targetPc.hasSkillEffect(IMMUNE_TO_HARM)) {
        	dmg -= (dmg * 0.3);//4
        }
        //スキル、料理などのダメージリダクション
        

        // 反逆者の盾確率的ダメージ減少処理
        if (_targetPc.getInventory().checkEquipped(22263)) {// 反逆者の盾
            int chance = _random.nextInt(100);
            L1ItemInstance item = _targetPc.getInventory().findEquippedItemId(22263);
            int enchant = item.getEnchantLevel();
            if (chance <= 1 + (enchant * 2)) {
                dmg -= 50; // 元50である
                _targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 6320));
                _targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 6320));
            }
        }
        if (_targetPc.getInventory().checkEquipped(222355)) {// 神聖なエルヴンシールド
            int chance = _random.nextInt(100) + 1;
            L1ItemInstance item = _targetPc.getInventory().findEquippedItemId(222355);
            if (chance <= item.getEnchantLevel()) {
                dmg -= 20;
                _targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 14543));
                _targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 14543));
            }
        }
        // リンドビオルの加護
        int chance6 = _random.nextInt(100) + 1;
        if (dmg > 25) {
            if (_target != _targetNpc) {
                if (_targetPc.getInventory().checkEquipped(22204)) {// リンド腕力
                    if (chance6 <= 5) {
                        short getMp = (short) (_targetPc.getCurrentMp() + 10);
                        _targetPc.setCurrentMp(getMp);
                        _targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 2188));
                        _targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 2188));
                    }
                } else if (_targetPc.getInventory().checkEquipped(22205)) {// リンド先見の明
                    if (chance6 <= 5) {
                        short getMp = (short) (_targetPc.getCurrentMp() + 15);
                        _targetPc.setCurrentMp(getMp);
                        _targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 2188));
                        _targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 2188));
                    }
                } else if (_targetPc.getInventory().checkEquipped(22206)// リンド耐久
                        || _targetPc.getInventory().checkEquipped(22207)) {// リンド馬力
                    if (chance6 <= 5) {
                        short getMp = (short) (_targetPc.getCurrentMp() + 20);
                        _targetPc.setCurrentMp(getMp);
                        _targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 2188));
                        _targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 2188));
                    }
                }
            }
        }
        // パプリオンの加護
        int chance5 = _random.nextInt(100) + 1;
        if (dmg > 25) {
            if (_target != null) {
                int dmg2 = 0;
                int plus = 0;
                if (_targetPc.getInventory().checkEquipped(22200) || // パプ腕力
                        _targetPc.getInventory().checkEquipped(22201) || // パプ先見の明
                        _targetPc.getInventory().checkEquipped(22202) || // パプ耐久
                        _targetPc.getInventory().checkEquipped(22203)) { // パプ馬力
                    if (chance5 <= 5) { // 元5である
                        L1ItemInstance item = _targetPc.getInventory().findEquippedItemId(22200);
                        L1ItemInstance item1 = _targetPc.getInventory().findEquippedItemId(22201);
                        L1ItemInstance item2 = _targetPc.getInventory().findEquippedItemId(22202);
                        L1ItemInstance item3 = _targetPc.getInventory().findEquippedItemId(22203);
                        if (item.getEnchantLevel() >= 7 && item.getEnchantLevel() <= 9) {
                            plus = item.getEnchantLevel() - 6;
                        } else if (item1.getEnchantLevel() >= 7 && item1.getEnchantLevel() <= 9) {
                            plus = item.getEnchantLevel() - 6;
                        } else if (item2.getEnchantLevel() >= 7 && item2.getEnchantLevel() <= 9) {
                            plus = item.getEnchantLevel() - 6;
                        } else if (item3.getEnchantLevel() >= 7 && item3.getEnchantLevel() <= 9) {
                            plus = item.getEnchantLevel() - 6;
                        } else if (item.getEnchantLevel() > 9 || item1.getEnchantLevel() > 9 || item2.getEnchantLevel() > 9
                                || item3.getEnchantLevel() > 9) {
                            plus = 3;
                        }
                        if (_targetPc.hasSkillEffect(L1SkillId.POLLUTE_WATER)) {
                            dmg2 += (40 + _random.nextInt(15) + (plus * 10)) / 2; // フルートウォーター場合の半分//元のランダム数値30である
                        }
                        if (_targetPc.hasSkillEffect(L1SkillId.WATER_LIFE)) {
                            dmg2 += (40 + _random.nextInt(15) + (plus * 10)) * 2; // ウォーターライフ場合倍増//元のランダム数値30である
                        }
                        dmg2 += 40 + _random.nextInt(15) + (plus * 10); // 回復率=基本50回復+ランダム（1〜30）//元のランダム数値30である
                        _targetPc.setCurrentHp(_targetPc.getCurrentHp() + dmg2);
                        _targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 2187));
                        _targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 2187));
                    }
                }
            }
        }
		// 神聖なエルヴンプレートメイル
		int chance66 = _random.nextInt(100) + 1;
		if (_target != null) {
			int dmg2 = 0;
			int plus = 0;
			if (_targetPc.getInventory().checkEquipped(222351)) {
				if (chance66 <= 6) { // 元5である
					if (_targetPc.hasSkillEffect(L1SkillId.POLLUTE_WATER)) {
						dmg2 += (25 + _random.nextInt(15) + (plus * 10)) / 2; //
					}
					if (_targetPc.hasSkillEffect(L1SkillId.WATER_LIFE)) {
						dmg2 += (25 + _random.nextInt(15) + (plus * 10)) * 2; //
					}
					dmg2 += 25 + _random.nextInt(15) + (plus * 10); //
					_targetPc.setCurrentHp(_targetPc.getCurrentHp() + dmg2);
					_targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 15355));
					_targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 15355));
				}
			}
		}

        try {
            if (_targetPc.isCrown()) {
                dmg -= CharacterReduc.getInstance().getCharacterReduc(0);
            } else if (_targetPc.isKnight()) {
                dmg -= CharacterReduc.getInstance().getCharacterReduc(1);
            } else if (_targetPc.isElf()) {
                dmg -= CharacterReduc.getInstance().getCharacterReduc(2);
            } else if (_targetPc.isWizard()) {
                dmg -= CharacterReduc.getInstance().getCharacterReduc(3);
            } else if (_targetPc.isDarkelf()) {
                dmg -= CharacterReduc.getInstance().getCharacterReduc(4);
            } else if (_targetPc.isBlackwizard()) {
                dmg -= CharacterReduc.getInstance().getCharacterReduc(5);
            } else if (_targetPc.isDragonknight()) {
                dmg -= CharacterReduc.getInstance().getCharacterReduc(6);
            } else if (_targetPc.isWarrior()) {
            	dmg -= CharacterReduc.getInstance().getCharacterReduc(7);
            }
        } catch (Exception e) {
            System.out.println("Character Add Reduction Error");
        }

        // ペット、サーモンからプレイヤーに攻撃
        boolean isNowWar = false;
        int castleId = L1CastleLocation.getCastleIdByArea(_targetPc);
        if (castleId > 0) {
            isNowWar = WarTimeController.getInstance().isNowWar(castleId);
        }
        if (!isNowWar) {
            if (_npc instanceof L1PetInstance) {
                dmg /= 2;
            }
            if (_npc instanceof L1SummonInstance) {
                L1SummonInstance summon = (L1SummonInstance) _npc;
                if (summon.isExsistMaster()) {
                    dmg /= 2;
                }
            }
        }

        addNpcPoisonAttack(_npc, _targetPc);

        if (_npc instanceof L1PetInstance || _npc instanceof L1SummonInstance) {
            if (_targetPc.getZoneType() == 1) {
                _isHit = false;
            }
        }

        if (dmg <= 0) {
            _isHit = false;
        }

        return (int) dmg;
    }

    // ●●●● NPCからNPCへのダメージ算出 ●●●●
    private int calcNpcNpcDamage() {
        if (_targetNpc == null || _npc == null)
            return 0;

        int lvl = _npc.getLevel();
        double dmg = 0;

        if (_npc instanceof L1PetInstance) {
            dmg = _random.nextInt(_npc.getNpcTemplate().get_level()) + _npc.getAbility().getTotalStr() / 2 + 1;
            dmg += (lvl / 16); // ペットはLV16に追加打撃
            dmg += ((L1PetInstance) _npc).getDamageByWeapon();
        } else if (_npc instanceof L1SummonInstance) {
            dmg = _random.nextInt(lvl) + _npc.getAbility().getTotalStr() + 5;
        } else {
            dmg = _random.nextInt(lvl) + _npc.getAbility().getTotalStr() / 2 + 1;
        }

        if (isUndeadDamage()) {
            dmg *= 1.1;
        }

        dmg = dmg * getLeverage() / 10;

        dmg -= calcNpcDamageReduction();

        if (_npc.isWeaponBreaked()) { // NPCがウェポンブレイク中。
            dmg /= 2;
        }

        addNpcPoisonAttack(_npc, _targetNpc);

        if (_targetNpc.hasSkillEffect(ICE_LANCE)) {
            dmg = 0;
        }
        if (_targetNpc.hasSkillEffect(EARTH_BIND)) {
            dmg = 0;
        }

        if (dmg <= 0) {
            _isHit = false;
        }

        return (int) dmg;
    }

    // ●●●● プレイヤーのダメージ強化魔法 ●●●●
    private double calcBuffDamage(double dmg) {
        if (_pc.hasSkillEffect(BURNING_SPIRIT) || (_pc.hasSkillEffect(ELEMENTAL_FIRE) && _weaponType != 20 && _weaponType != 62)) {
            if ((_random.nextInt(100) + 1) <= 33) {
                double tempDmg = dmg;
                if (_pc.hasSkillEffect(FIRE_WEAPON)) {
                    tempDmg -= 4;
                }
                if (_pc.hasSkillEffect(BURNING_WEAPON)) {
                    tempDmg -= 6;
                }
                if (_pc.hasSkillEffect(BERSERKERS)) {
                    tempDmg -= 5;
                }
                double diffDmg = dmg - tempDmg;
                dmg = tempDmg * 1.5 + diffDmg;
            }
        }

        return dmg;
    }

    /** 武器属性エンチャントによる効果付与（PC-PC） **/
    private double fishAttrEnchantEffect() {
        int Attr = _weaponAttrLevel;
        double AttrDmg = 0;
        switch (_weaponAttrLevel) {
        case 1:
        case 2:
        case 3:
        case 4:
        case 5:
            // AttrDmg += (Attr - 1) * 2+1;
            AttrDmg += (Attr - 1) + 1;
            AttrDmg -= AttrDmg * _targetPc.getResistance().getFire() / 100;
            break;
        case 6:
        case 7:
        case 8:
        case 9:
        case 10:
            // AttrDmg += (Attr - 6) * 2+1;
            AttrDmg += (Attr - 6) + 1;
            AttrDmg -= AttrDmg * _targetPc.getResistance().getWater() / 100;
            break;
        case 11:
        case 12:
        case 13:
        case 14:
        case 15:
            // AttrDmg += (Attr - 11) * 2+1;
            AttrDmg += (Attr - 11) + 1;
            AttrDmg -= AttrDmg * _targetPc.getResistance().getWind() / 100;
            break;
        case 16:
        case 17:
        case 18:
        case 19:
        case 20:
            // AttrDmg += (Attr - 16) * 2+1;
            AttrDmg += (Attr - 16) + 1;
            AttrDmg -= AttrDmg * _targetPc.getResistance().getEarth() / 100;
            break;
        default:
            AttrDmg = 0;
            break;
        }
        return AttrDmg;
    }

    /**武器属性エンチャントによる効果付与（PC-NPC) **/
    private int monsterAttrEnchantEffect() {
        int AttrDmg = 0;
        int Attr = _weaponAttrLevel;
        int NpcWeakAttr = _targetNpc.getNpcTemplate().get_weakAttr();
        switch (NpcWeakAttr) {
        case 1: // 土地脆弱モンスター
            if (Attr >= 15 && Attr <= 20) {
                // AttrDmg += 1 + (Attr - 15) * 2;
                AttrDmg += 1 + (Attr - 15);
            }
            break;
        case 2: // 水脆弱モンスター
            if (Attr >= 6 && Attr <= 10) {
                // AttrDmg += 1 + (Attr - 6) * 2;
                AttrDmg += 1 + (Attr - 6);
            }
            break;
        case 4: // 火脆弱モンスター
            if (Attr >= 1 && Attr <= 5) {
                // AttrDmg += (Attr - 1) * 2 + 1;
                AttrDmg += 1 + (Attr - 1);
            }
            break;
        case 8: //風脆弱モンスター
            if (Attr >= 11 && Attr <= 15) {
                // AttrDmg += 1 + (Attr - 11) * 2;
                AttrDmg += 1 + (Attr - 11);
            }
            break;
        default:
            AttrDmg = 0;
            break;
        }
        return AttrDmg;
    }

    // ●●●● プレイヤーのACによるダメージ軽減 ●●●●
    private int calcPcDefense() {
        int ac = Math.max(0, 10 - _targetPc.getAC().getAc());
        int acDefMax = _targetPc.getClassFeature().getAcDefenseMax(ac);
        return _random.nextInt(acDefMax + 1);
    }

    // ●●●● NPCのダメージ縮小による軽減 ●●●●
    private int calcNpcDamageReduction() {
        return _targetNpc.getNpcTemplate().get_damagereduction();
    }

    // ●●●● 武器の材質と祝福による追加ダメージ算出 ●●●●
    private int calcMaterialBlessDmg() {
        int damage = 0;
        int undead = _targetNpc.getNpcTemplate().get_undead();
        if ((_weaponMaterial == 14 || _weaponMaterial == 17 || _weaponMaterial == 22) && (undead == 1 || undead == 3)) { // は・ミスリル・オリハルコン、かつ、アンデッド系・アン
                                                                                                                         // デッド系ボス
            damage += _random.nextInt(20) + 1;
        }
        if (_weaponBless == 0 && (undead == 1 || undead == 2 || undead == 3)) { // 祝福武器、かつ、アンデッド系・悪魔系・アンデッド系ボス
            damage += _random.nextInt(4) + 1;
        }
        if (weapon != null && _weaponType != 20 && _weaponType != 62 && weapon.getHolyDmgByMagic() != 0 && (undead == 1 || undead == 3)) {
            damage += weapon.getHolyDmgByMagic();
        }
        return damage;
    }

    // ●●●● NPCの内部デッドの夜の攻撃力の変化 ●●●●
    private boolean isUndeadDamage() {
        boolean flag = false;
        int undead = _npc.getNpcTemplate().get_undead();
        boolean isNight = L1GameTimeClock.getInstance().getGameTime().isNight();
        if (isNight && (undead == 1 || undead == 3)) {
            flag = true;
        }
        return flag;
    }

    // ●●●● NPCの毒攻撃を付加 ●●●●
    private void addNpcPoisonAttack(L1Character attacker, L1Character target) {
        if (_npc.getNpcTemplate().get_poisonatk() != 0) { // 毒攻撃おり
            if (15 >= _random.nextInt(100) + 1) { // 15％の確率で毒攻撃
                if (_npc.getNpcTemplate().get_poisonatk() == 1) { // 通常毒
                    // 3秒周期でダメー​​ジ5
                    L1DamagePoison.doInfection(attacker, target, 3000, 5, false);
                } else if (_npc.getNpcTemplate().get_poisonatk() == 2) { // 沈黙毒
                    L1SilencePoison.doInfection(target);
                } else if (_npc.getNpcTemplate().get_poisonatk() == 4) { // 麻痺毒
                    // 20秒後に16秒間麻痺
                    L1ParalysisPoison.doInfection(target, 20000, 16000);
                }
            }
        } else if (_npc.getNpcTemplate().get_paralysisatk() != 0) { // / 麻痺攻撃おり
        }
    }

    // ■■■■ マナススタッフと鋼のマナスせい後MP吸収量算出 ■■■■
    public void calcStaffOfMana() {
        // マナ、鋼鉄マナ、悪魔の杖
        if (_weaponId == 126 || _weaponId == 127 || _weaponId == 413103) {
            int som_lvl = _weaponEnchant + 3; // 最大MP吸収量を設定
            if (som_lvl < 0) {
                som_lvl = 0;
            }
            // MP吸収量をランダム取得
            _drainMana = _random.nextInt(som_lvl) + 1;
            // 最大MP吸収量を9に制限
            if (_drainMana > Config.MANA_DRAIN_LIMIT_PER_SOM_ATTACK) {
                _drainMana = Config.MANA_DRAIN_LIMIT_PER_SOM_ATTACK;
            }
        }
    }

    public void manaBaselard() { //マナ吸収のための追加
        int MR = getTargetMr(); // 魔に基づいて成功率を適用
        if (MR >= 100)
            return;
        if (MR < _random.nextInt(100))
            _drainMana = 1;
    }

    private int getTargetMr() {
        int mr = 1;
        if (_calcType == PC_PC || _calcType == NPC_PC) {
            mr = _targetPc.getResistance().getEffectedMrBySkill();
        } else {
            mr = _targetNpc.getResistance().getEffectedMrBySkill();
        }
        return mr;
    }

    public void getAbsorHP(L1PcInstance pc, L1Character target) {
        int pcInt = pc.getAbility().getTotalInt();

        _drainHp = (_random.nextInt(5) + pcInt + _weaponEnchant) / 3;

        if (_drainHp > 0 && target.getCurrentHp() > 0) {
            if (_drainHp > target.getCurrentHp()) {
                _drainHp = target.getCurrentHp();
            }
            short newHp = (short) (target.getCurrentHp() - _drainHp);
            target.setCurrentHp(newHp);
            newHp = (short) (_pc.getCurrentHp() + _drainHp);
            pc.setCurrentHp(newHp);
        }
    }

    /** ゾウのストーンゴーレム - 馬力の短剣 **/
    public void calcDrainOfMana() {
        if (_weaponId == 602) {
            manaBaselard();
        }
    }

    /** ゾウのストーンゴーレム - 破滅のグレートソード * */
    public void ruinGreatSword(double dmg) { // 21回破滅のグレートソードパワーブック参照
        int r = _random.nextInt(100);
        if (r <= 80) {
            if (dmg <= 30) {
                _drainHp = 1;
            } else if (dmg > 30 && dmg <= 38) {
                _drainHp = 2;
            } else if (dmg > 38 && dmg <= 46) {
                _drainHp = 3;
            } else if (dmg > 46 && dmg <= 54) {
                _drainHp = 4;
            } else if (dmg > 54 && dmg <= 62) {
                _drainHp = 5;
            } else if (dmg > 62 && dmg <= 70) {
                _drainHp = 6;
            } else if (dmg > 70 && dmg <= 78) {
                _drainHp = 7;
            } else if (dmg > 78) {
                _drainHp = 8;
            }
        }
    }

    public void bloodSucker(double dmg, int enchant) { // 21回パワーブック参照
        int r = _random.nextInt(100);
        int e = enchant - 6;
        if (r <= 75) {
            if (dmg <= 30) {
                _drainHp = 1 + e;
            } else if (dmg > 30 && dmg <= 38) {
                _drainHp = 2 + e;
            } else if (dmg > 38 && dmg <= 46) {
                _drainHp = 3 + e;
            } else if (dmg > 46 && dmg <= 54) {
                _drainHp = 4 + e;
            } else if (dmg > 54 && dmg <= 63) {
                _drainHp = 5 + e;
            } else if (dmg > 63 && dmg <= 73) {
                _drainHp = 6 + e;
            } else if (dmg > 73 && dmg <= 83) {
                _drainHp = 7 + e;
            } else if (dmg > 83 && dmg <= 92) {
                _drainHp = 8 + e;
            } else if (dmg > 92) {
                _drainHp = 9 + e;
            }
            if (e <= 0) {
                e = 0;
            }
        }
    }

    public int extremeChainSword(L1PcInstance pc, L1Character target, int effect, int enchant) {
        int dmg = 0;
        int en = enchant;
        int intel = pc.getAbility().getTotalInt();
        int chance = _random.nextInt(100) + 1;
        if (chance <= en + 2) {
            _drainHp = _random.nextInt(intel / 2) + (intel * 2);
            pc.sendPackets(new S_SkillSound(target.getId(), effect));
            Broadcaster.broadcastPacket(pc, new S_SkillSound(target.getId(), effect));
        }
        return dmg;
        // return L1WeaponSkill.calcDamageReduction(target, dmg, L1Skills.ATTR_WATER);
    }

    // ガイア激怒
    public int getEbMP1(L1PcInstance pc, L1Character target, int effect, int enchant) {
        int dmg = 0;
        int en = enchant;
        int intel = pc.getAbility().getTotalInt();
        int chance = _random.nextInt(90) + 1;
        if (chance <= en + 8) {
            _drainMana = _random.nextInt(intel / 6) + (intel / 3);
            pc.sendPackets(new S_SkillSound(target.getId(), effect));
            Broadcaster.broadcastPacket(pc, new S_SkillSound(target.getId(), effect));
        }
        return dmg;
    }

    /** ●●●●悪リバース ●●●● **/
    // 剣チェーンソードクロウ斧
    public int getEbHP(L1PcInstance pc, L1Character target, int effect, int enchant) {
        int dmg = 0;
        int en = enchant;
        int intel = pc.getAbility().getTotalInt();
        int chance = _random.nextInt(100) + 1;
        if (chance <= en + 5) {
            _drainHp = _random.nextInt(intel / 2) + (intel);
            pc.sendPackets(new S_SkillSound(target.getId(), effect));
            Broadcaster.broadcastPacket(pc, new S_SkillSound(target.getId(), effect));
        }
        return dmg;
    }

    public void miss(L1PcInstance pc, L1Character target) {

        pc.sendPackets(new S_SkillSound(target.getId(), 13418));
        Broadcaster.broadcastPacket(pc, new S_SkillSound(target.getId(), 13418));
    }

    /** ●●●● 悪のトリック ●●●● **/
    // 杖弓キーリンク
    public int getEbMP(L1PcInstance pc, L1Character target, int effect, int enchant) {
        int dmg = 0;
        int en = enchant;
        int intel = pc.getAbility().getTotalInt();
        int chance = _random.nextInt(100) + 1;
        if (chance <= (en) + 5) {
            _drainMana = _random.nextInt(intel / 6) + (intel / 3);
            pc.sendPackets(new S_SkillSound(target.getId(), effect));
            Broadcaster.broadcastPacket(pc, new S_SkillSound(target.getId(), effect));
        }
        return dmg;
    }

    // ■■■■ PCの毒攻撃を付加 ■■■■
    public void addPcPoisonAttack(L1Character attacker, L1Character target) {
        int chance = _random.nextInt(100) + 1;
        if ((_weaponId == 13 || _weaponId == 44 // FOD、古代のダークエルフソード
                || (_weaponId != 0 && _pc.hasSkillEffect(ENCHANT_VENOM))) // エンチャント
                // ベノムの
                && chance <= 10) {
            L1DamagePoison.doInfection(attacker, target, 3000, 30, false);
        }
    }

    /* ■■■■■■■■■■■■■■ 攻撃モーション送信 ■■■■■■■■■■■■■■ */

    public void action() {
        try {
            if (_calcType == PC_PC || _calcType == PC_NPC) {
                if (_isCritical) {
                    criticalPc();
                    if (!_pc.isGm()) {
                        _isCritical = false;
                    }
                } else {
                    actionPc();
                }
            } else if (_calcType == NPC_PC || _calcType == NPC_NPC) {
                actionNpc();
            }
        } catch (Exception e) {
        }
    }

    // ●●●● プレイヤーの攻撃モーション送信 ●●●●
    private void actionPc() {
        _pc.setHeading(_pc.targetDirection(_targetX, _targetY)); // 方向セット
        if(_target instanceof L1NpcInstance){
        	if(((L1NpcInstance)_target).getNpcId() >=400067 && ((L1NpcInstance)_target).getNpcId() <=400080){
        		_isHit = false;
        	}
        }
        if (_weaponType == 20) {
            if (_pc instanceof L1RobotInstance || _arrow != null) {
                if (!_pc.noPlayerCK)
                    _pc.getInventory().removeItem(_arrow, 1);
                 if (_pc.getTempCharGfx() == 7967) {
                    _pc.sendPackets(new S_UseArrowSkill(_pc, _targetId, 7972, _targetX, _targetY, _isHit));
                    Broadcaster.broadcastPacket(_pc, new S_UseArrowSkill(_pc, _targetId, 7972, _targetX, _targetY, _isHit));
                } else if (_pc.getTempCharGfx() == 11402 || _pc.getTempCharGfx() == 8900) { // 75レプ変身
                    _pc.sendPackets(new S_UseArrowSkill(_pc, _targetId, 8904, _targetX, _targetY, _isHit));
                    Broadcaster.broadcastPacket(_pc, new S_UseArrowSkill(_pc, _targetId, 8904, _targetX, _targetY, _isHit));
                    Broadcaster.broadcastPacketExceptTargetSight(_target, new S_DoActionGFX(_targetId, ActionCodes.ACTION_Damage), _pc);
                } else if (_pc.getTempCharGfx() == 11406 || _pc.getTempCharGfx() == 8913) {// 80レプ変身
                    _pc.sendPackets(new S_UseArrowSkill(_pc, _targetId, 8916, _targetX, _targetY, _isHit));
                    Broadcaster.broadcastPacket(_pc, new S_UseArrowSkill(_pc, _targetId, 8916, _targetX, _targetY, _isHit));
                    Broadcaster.broadcastPacketExceptTargetSight(_target, new S_DoActionGFX(_targetId, ActionCodes.ACTION_Damage), _pc);
                } else if (_pc.getTempCharGfx() == 13631) {// 82レプ変身
                    _pc.sendPackets(new S_UseArrowSkill(_pc, _targetId, 13656, _targetX, _targetY, _isHit));
                    Broadcaster.broadcastPacket(_pc, new S_UseArrowSkill(_pc, _targetId, 13656, _targetX, _targetY, _isHit));
                    Broadcaster.broadcastPacketExceptTargetSight(_target, new S_DoActionGFX(_targetId, ActionCodes.ACTION_Damage), _pc);
                } else if (_pc.getTempCharGfx() == 13635) {// 85レップ変身
                    _pc.sendPackets(new S_UseArrowSkill(_pc, _targetId, 13658, _targetX, _targetY, _isHit));
                    Broadcaster.broadcastPacket(_pc, new S_UseArrowSkill(_pc, _targetId, 13658, _targetX, _targetY, _isHit));
                    Broadcaster.broadcastPacketExceptTargetSight(_target, new S_DoActionGFX(_targetId, ActionCodes.ACTION_Damage), _pc);
                } else {
                    _pc.sendPackets(new S_UseArrowSkill(_pc, _targetId, 66, _targetX, _targetY, _isHit));
                    Broadcaster.broadcastPacket(_pc, new S_UseArrowSkill(_pc, _targetId, 66, _targetX, _targetY, _isHit));
                }
                if (_isHit) {
                    Broadcaster.broadcastPacketExceptTargetSight(_target, new S_DoActionGFX(_targetId, ActionCodes.ACTION_Damage), _pc);
                }
            } else if (_weaponId == 190) {
                _pc.sendPackets(new S_UseArrowSkill(_pc, _targetId, 2349, _targetX, _targetY, _isHit));
                Broadcaster.broadcastPacket(_pc, new S_UseArrowSkill(_pc, _targetId, 2349, _targetX, _targetY, _isHit));
                if (_isHit) {
                    Broadcaster.broadcastPacketExceptTargetSight(_target, new S_DoActionGFX(_targetId, ActionCodes.ACTION_Damage), _pc);
                }
            } else if (_weaponId == 202011) {// ガイアの激怒
                _pc.sendPackets(new S_UseArrowSkill(_pc, _targetId, 13658, _targetX, _targetY, _isHit));
                Broadcaster.broadcastPacket(_pc, new S_UseArrowSkill(_pc, _targetId, 13658, _targetX, _targetY, _isHit));
                if (_isHit) {
                    Broadcaster.broadcastPacketExceptTargetSight(_target, new S_DoActionGFX(_targetId, ActionCodes.ACTION_Damage), _pc);
                }
            } else if (_weaponId == 10000) {
                _pc.sendPackets(new S_UseArrowSkill(_pc, _targetId, 8771, _targetX, _targetY, _isHit));
                Broadcaster.broadcastPacket(_pc, new S_UseArrowSkill(_pc, _targetId, 8771, _targetX, _targetY, _isHit));
                if (_isHit) {
                    Broadcaster.broadcastPacketExceptTargetSight(_target, new S_DoActionGFX(_targetId, ActionCodes.ACTION_Damage), _pc);
                }
            }
        } else if (_weaponType == 62 && _sting != null) {
            _pc.getInventory().removeItem(_sting, 1);
             if (_pc.getTempCharGfx() == 7967) {
                _pc.sendPackets(new S_UseArrowSkill(_pc, _targetId, 7972, _targetX, _targetY, _isHit));
                Broadcaster.broadcastPacket(_pc, new S_UseArrowSkill(_pc, _targetId, 7972, _targetX, _targetY, _isHit));
            } else if (_pc.getTempCharGfx() == 11402 || _pc.getTempCharGfx() == 8900) {// 75レプ変身
                _pc.sendPackets(new S_UseArrowSkill(_pc, _targetId, 8904, _targetX, _targetY, _isHit));
                Broadcaster.broadcastPacket(_pc, new S_UseArrowSkill(_pc, _targetId, 8904, _targetX, _targetY, _isHit));
                Broadcaster.broadcastPacketExceptTargetSight(_target, new S_DoActionGFX(_targetId, ActionCodes.ACTION_Damage), _pc);
            } else if (_pc.getTempCharGfx() == 11406 || _pc.getTempCharGfx() == 8913) {// 80レプ変身
                _pc.sendPackets(new S_UseArrowSkill(_pc, _targetId, 8916, _targetX, _targetY, _isHit));
                Broadcaster.broadcastPacket(_pc, new S_UseArrowSkill(_pc, _targetId, 8916, _targetX, _targetY, _isHit));
                Broadcaster.broadcastPacketExceptTargetSight(_target, new S_DoActionGFX(_targetId, ActionCodes.ACTION_Damage), _pc);
            } else if (_pc.getTempCharGfx() == 13631) {// 82レプ変身
                _pc.sendPackets(new S_UseArrowSkill(_pc, _targetId, 13656, _targetX, _targetY, _isHit));
                Broadcaster.broadcastPacket(_pc, new S_UseArrowSkill(_pc, _targetId, 13656, _targetX, _targetY, _isHit));
                Broadcaster.broadcastPacketExceptTargetSight(_target, new S_DoActionGFX(_targetId, ActionCodes.ACTION_Damage), _pc);
            } else if (_pc.getTempCharGfx() == 13635) {// 85レプ変身
                _pc.sendPackets(new S_UseArrowSkill(_pc, _targetId, 13658, _targetX, _targetY, _isHit));
                Broadcaster.broadcastPacket(_pc, new S_UseArrowSkill(_pc, _targetId, 13658, _targetX, _targetY, _isHit));
                Broadcaster.broadcastPacketExceptTargetSight(_target, new S_DoActionGFX(_targetId, ActionCodes.ACTION_Damage), _pc);
            } else {
                _pc.sendPackets(new S_UseArrowSkill(_pc, _targetId, 2989, _targetX, _targetY, _isHit));
                Broadcaster.broadcastPacket(_pc, new S_UseArrowSkill(_pc, _targetId, 2989, _targetX, _targetY, _isHit));
            }
            if (_isHit) {
                Broadcaster.broadcastPacketExceptTargetSight(_target, new S_DoActionGFX(_targetId, ActionCodes.ACTION_Damage), _pc);
            }
        } else {
            if (_isHit) {
                _pc.sendPackets(new S_AttackPacket(_pc, _targetId, ActionCodes.ACTION_Attack, _attackType));
               Broadcaster.broadcastPacket(_pc, new S_AttackPacket(_pc, _targetId, ActionCodes.ACTION_Attack, _attackType));
                Broadcaster.broadcastPacketExceptTargetSight(_target, new S_DoActionGFX(_targetId, ActionCodes.ACTION_Damage), _pc);
            } else {
                if (_targetId > 0) {
                    _pc.sendPackets(new S_AttackMissPacket(_pc, _targetId));
                    Broadcaster.broadcastPacket(_pc, new S_AttackMissPacket(_pc, _targetId));
                } else {
                    _pc.sendPackets(new S_AttackPacket(_pc, 0, ActionCodes.ACTION_Attack));
                    Broadcaster.broadcastPacket(_pc, new S_AttackPacket(_pc, 0, ActionCodes.ACTION_Attack));
                }
            }
        }
    }

    private void criticalPc() {
        _pc.setHeading(_pc.targetDirection(_targetX, _targetY)); // 方向セット
        if (_weaponType == 20) {
            _pc.sendPackets(new S_AttackCritical(_pc, _targetId, _targetX, _targetY, _weaponType, _isHit));
            Broadcaster.broadcastPacket(_pc, new S_AttackCritical(_pc, _targetId, _targetX, _targetY, _weaponType, _isHit));//新たに作成しましてふ
        } else if (_weaponType == 62 && _sting != null) {

            _pc.sendPackets(new S_AttackCritical(_pc, _targetId, _targetX, _targetY, _weaponType, _isHit));
            Broadcaster.broadcastPacket(_pc, new S_AttackCritical(_pc, _targetId, _targetX, _targetY, _weaponType, _isHit));
        } else {
            if (_pc.isWarrior()) {
                _pc.sendPackets(new S_AttackCritical(_pc, _targetId, 99));
                Broadcaster.broadcastPacket(_pc, new S_AttackCritical(_pc, _targetId, 99));
            } else {
                if (_weaponType2 == 18) {
                    _weaponType = 90;
                } else if (_weaponType2 == 17) {
                    _weaponType = 91;
                } else if (_weaponType2 == 8 || _weaponType == 10) {
                    _weaponType = 92;
                }
            }
            _pc.sendPackets(new S_AttackCritical(_pc, _targetId, _weaponType));
            Broadcaster.broadcastPacket(_pc, new S_AttackCritical(_pc, _targetId, _weaponType));
        }
    }

    // ●●●● NPCの攻撃モーション送信 ●●●●
    private void actionNpc() {
        int _npcObjectId = _npc.getId();
        int bowActId = 0;
        int actId = 0;

        _npc.setHeading(_npc.targetDirection(_targetX, _targetY)); // 方向セット

        // ターゲットとの距離が2以上の場合、遠距離攻撃
        boolean isLongRange = (_npc.getLocation().getTileLineDistance(new Point(_targetX, _targetY)) > 1);
        bowActId = _npc.getNpcTemplate().getBowActId();

        if (getActId() > 0) {
            actId = getActId();
        } else {
            actId = ActionCodes.ACTION_Attack;
        }

        if (isLongRange && bowActId > 0) {
            Broadcaster.broadcastPacket(_npc, new S_UseArrowSkill(_npc, _targetId, bowActId, _targetX, _targetY, _isHit));
        } else {
            if (_isHit) {
                if (getGfxId() > 0) {
                    Broadcaster.broadcastPacket(_npc, new S_UseAttackSkill(_target, _npcObjectId, getGfxId(), _targetX, _targetY, actId));
                    Broadcaster.broadcastPacketExceptTargetSight(_target, new S_DoActionGFX(_targetId, ActionCodes.ACTION_Damage), _npc);
                } else {
                    Broadcaster.broadcastPacket(_npc, new S_AttackPacketForNpc(_target, _npcObjectId, actId));
                    Broadcaster.broadcastPacketExceptTargetSight(_target, new S_DoActionGFX(_targetId, ActionCodes.ACTION_Damage), _npc);
                }
            } else {
                if (getGfxId() > 0) {
                    Broadcaster.broadcastPacket(_npc, new S_UseAttackSkill(_target, _npcObjectId, getGfxId(), _targetX, _targetY, actId, 0));
                } else {
                    Broadcaster.broadcastPacket(_npc, new S_AttackMissPacket(_npc, _targetId, actId));
                }
            }
        }
    }

    // 飛び道具（矢、スティング）がミスだった木の軌道を計算
    public void calcOrbit(int cx, int cy, int head) // 基点X基点Y今向いている方向
    {
        float dis_x = Math.abs(cx - _targetX); //X方向のターゲットまでの距離
        float dis_y = Math.abs(cy - _targetY); // Y方向のターゲットまでの距離
        float dis = Math.max(dis_x, dis_y); // ターゲットまでの距離
        float avg_x = 0;
        float avg_y = 0;
        if (dis == 0) { // 目標と同じ位置であれば、向いている方向に真っ直ぐ
            switch (head) {
            case 1:
                avg_x = 1;
                avg_y = -1;
                break;
            case 2:
                avg_x = 1;
                avg_y = 0;
                break;
            case 3:
                avg_x = 1;
                avg_y = 1;
                break;
            case 4:
                avg_x = 0;
                avg_y = 1;
                break;
            case 5:
                avg_x = -1;
                avg_y = 1;
                break;
            case 6:
                avg_x = -1;
                avg_y = 0;
                break;
            case 7:
                avg_x = -1;
                avg_y = -1;
                break;
            case 0:
                avg_x = 0;
                avg_y = -1;
                break;
            default:
                break;
            }
        } else {
            avg_x = dis_x / dis;
            avg_y = dis_y / dis;
        }

        int add_x = (int) Math.floor((avg_x * 15) + 0.59f); // 上下左右が少し優先的なラウンド
        int add_y = (int) Math.floor((avg_y * 15) + 0.59f); // 上下左右が少し優先的なラウンド

        if (cx > _targetX) {
            add_x *= -1;
        }
        if (cy > _targetY) {
            add_y *= -1;
        }

        _targetX = _targetX + add_x;
        _targetY = _targetY + add_y;
    }

    /* ■■■■■■■■■■■■■■■ 計算結果に反映 ■■■■■■■■■■■■■■■ */

    public void commit() {
        if (_isHit) {
            try {
                if (_calcType == PC_PC || _calcType == NPC_PC) {
                    commitPc();
                } else if (_calcType == PC_NPC || _calcType == NPC_NPC) {
                    commitNpc();
                }
            } catch (Exception e) {
            }
        }

        // ダメージ値と命中率確認用のメッセージ
        if (!Config.ALT_ATKMSG) {
            return;
        }
        if (_target == null)
			return;
        if (Config.ALT_ATKMSG) {
            if ((_calcType == PC_PC || _calcType == PC_NPC) && !_pc.isGm()) {
                return;
            }
            if ((_calcType == PC_PC || _calcType == NPC_PC) && !_targetPc.isGm()) {
                return;
            }
        }
        String msg0 = "";
        String msg1 = "";
        String msg2 = "";
        String msg3 = "";
        if (_calcType == PC_PC || _calcType == PC_NPC) { // アタックカーがPCの場合
            msg0 = _pc.getName();
        } else if (_calcType == NPC_PC) { // アタックカーがNPCの場合
            msg0 = _npc.getName();
        }

        if (_calcType == NPC_PC || _calcType == PC_PC) { // ターゲットがPCの場合
            msg3 = _targetPc.getName();
            msg1 = "HP:" + _targetPc.getCurrentHp() + " / HR:" + _hitRate;
        } else if (_calcType == PC_NPC) { // ターゲットがNPCの場合
            msg3 = _targetNpc.getName();
            msg1 = "HP:" + _targetNpc.getCurrentHp() + " / HR:" + _hitRate;
        }
        msg2 = "DMG:" + _damage;

        if (_calcType == PC_PC || _calcType == PC_NPC) { // アタックカーがPCの場合
            _pc.sendPackets(new S_SystemMessage("\\fR[" + msg0 + "->" + msg3 + "] " + msg2 + " / " + msg1));
        }
        if (_calcType == NPC_PC || _calcType == PC_PC) { // ターゲットがPCの場合
            _targetPc.sendPackets(new S_SystemMessage("\\fY[" + msg0 + "->" + msg3 + "] " + msg2 + " / " + msg1));
        }
    }

    // ●●●● プレイヤーの計算結果を反映 ●●●●
    private void commitPc() {
        if (_calcType == PC_PC) {
            if (_targetPc.hasSkillEffect(ICE_LANCE)) {
                _damage = 0;
                _drainMana = 0;
                _drainHp = 0;
            }
            if (_targetPc.hasSkillEffect(EARTH_BIND)) {
                _damage = 0;
                _drainMana = 0;
                _drainHp = 0;
            }
            if (_targetPc.hasSkillEffect(MOB_BASILL)) { // バジルアーリー期待未知0
                _damage = 0;
                _drainMana = 0;
                _drainHp = 0;
            }
            if (_targetPc.hasSkillEffect(MOB_COCA)) { // コカアーリー期待未知0
                _damage = 0;
                _drainMana = 0;
                _drainHp = 0;
            }
            if (_drainMana > 0 && _targetPc.getCurrentMp() > 0) {
                if (_drainMana > _targetPc.getCurrentMp()) {
                    _drainMana = _targetPc.getCurrentMp();
                }
                short newMp = (short) (_targetPc.getCurrentMp() - _drainMana);
                _targetPc.setCurrentMp(newMp);
                newMp = (short) (_pc.getCurrentMp() + _drainMana);
                _pc.setCurrentMp(newMp);
            }

            /** ゾウのストーンゴーレム **/

            if (_drainHp > 0 && _targetPc.getCurrentHp() > 0) {
                if (_drainHp > _targetPc.getCurrentHp()) {
                    _drainHp = _targetPc.getCurrentHp();
                }
                short newHp = (short) (_targetPc.getCurrentHp() - _drainHp);
                _targetPc.setCurrentHp(newHp);
                newHp = (short) (_pc.getCurrentHp() + _drainHp);
                _pc.setCurrentHp(newHp);
            }
            /** ゾウのストーンゴーレム **/

            // damagePcWeaponDurability(); // 武器を損傷させる。

            _targetPc.receiveDamage(_pc, _damage);
        } else if (_calcType == NPC_PC) {
            if (_targetPc.hasSkillEffect(ICE_LANCE)) {
                _damage = 0;
            }
            if (_targetPc.hasSkillEffect(EARTH_BIND)) {
                _damage = 0;
            }
            if (_targetPc.hasSkillEffect(MOB_BASILL)) { // バジルアーリー期待未知0
                _damage = 0;
            }
            if (_targetPc.hasSkillEffect(MOB_COCA)) { // コカアーリー期待未知0
                _damage = 0;
            }
            _targetPc.receiveDamage(_npc, _damage);
        }
    }

    // ●●●● NPCに計算結果を反映●●●●
    private void commitNpc() {
        if (_calcType == PC_NPC) {
            if (_targetNpc.hasSkillEffect(ICE_LANCE)) {
                _damage = 0;
                _drainMana = 0;
                _drainHp = 0;
            }
            if (_targetNpc.hasSkillEffect(EARTH_BIND)) {
                _damage = 0;
                _drainMana = 0;
                _drainHp = 0;
            }
            if (_targetNpc.hasSkillEffect(MOB_BASILL)) { // バジルアーリー期待未知0
                _damage = 0;
                _drainMana = 0;
                _drainHp = 0;
            }
            if (_targetNpc.hasSkillEffect(MOB_COCA)) { // コカアーリー期待未知0
                _damage = 0;
                _drainMana = 0;
                _drainHp = 0;
            }
            if (_drainMana > 0) {
                int drainValue = _targetNpc.drainMana(_drainMana);
                int newMp = _pc.getCurrentMp() + drainValue;
                _pc.setCurrentMp(newMp);

                if (drainValue > 0) {
                    int newMp2 = _targetNpc.getCurrentMp() - drainValue;
                    _targetNpc.setCurrentMp(newMp2);
                }
            }

            /** ゾウのストーンゴーレム **/

            if (_drainHp > 0) {
                int newHp = _pc.getCurrentHp() + _drainHp;
                _pc.setCurrentHp(newHp);
            }
            /** ゾウのストーンゴーレム **/

            damageNpcWeaponDurability(); // 武器を損傷させる。

            _targetNpc.receiveDamage(_pc, _damage);
        } else if (_calcType == NPC_NPC) {
            if (_targetNpc.hasSkillEffect(ICE_LANCE)) {
                _damage = 0;
            }
            if (_targetNpc.hasSkillEffect(EARTH_BIND)) {
                _damage = 0;
            }
            if (_targetNpc.hasSkillEffect(MOB_BASILL)) { // バジルアーリー期待未知0
                _damage = 0;
            }
            if (_targetNpc.hasSkillEffect(MOB_COCA)) { // コカアーリー期待未知0
                _damage = 0;
            }
            _targetNpc.receiveDamage(_npc, _damage);
        }
    }

    /* ■■■■■■■■■■■■■■■ カウンターバリアー ■■■■■■■■■■■■■■■ */

    // ■■■■ カウンターバリアー時の攻撃モーション送信 ■■■■
    public void actionCounterBarrier() {
        if (_calcType == PC_PC) {
            if (_pc == null)
                return;
            _pc.setHeading(_pc.targetDirection(_targetX, _targetY)); // 方向セット
            _pc.sendPackets(new S_AttackMissPacket(_pc, _targetId));
            _pc.broadcastPacket(new S_AttackMissPacket(_pc, _targetId), _target);
            _pc.sendPackets(new S_DoActionGFX(_pc.getId(), ActionCodes.ACTION_Damage));
            _pc.broadcastPacket(new S_DoActionGFX(_pc.getId(), ActionCodes.ACTION_Damage));
            _pc.sendPackets(new S_SkillSound(_targetId, 10710));
            _pc.broadcastPacket(new S_SkillSound(_targetId, 10710));
        } else if (_calcType == NPC_PC) {
            if (_npc == null || _target == null)
                return;
            int actId = 0;
            _npc.setHeading(_npc.targetDirection(_targetX, _targetY)); // 方向セット
            if (getActId() > 0) {
                actId = getActId();
            } else {
                actId = ActionCodes.ACTION_Attack;
            }
            if (getGfxId() > 0) {
                _npc.broadcastPacket(new S_UseAttackSkill(_target, _npc.getId(), getGfxId(), _targetX, _targetY, actId, 0), _target);
            } else {
                _npc.broadcastPacket(new S_AttackMissPacket(_npc, _targetId, actId), _target);
            }
            _npc.broadcastPacket(new S_DoActionGFX(_npc.getId(), ActionCodes.ACTION_Damage));
            _npc.broadcastPacket(new S_SkillSound(_targetId, 10710));
        }
    }

    // ■■■■ モータルボディ発動時の攻撃モーション送信 ■■■■

    public void actionMortalBody() {
        if (_calcType == PC_PC) {
            if (_pc == null || _target == null)
                return;
            _pc.setHeading(_pc.targetDirection(_targetX, _targetY)); // 方向セット
            S_UseAttackSkill packet = new S_UseAttackSkill(_pc, _target.getId(), 6519, _targetX, _targetY, ActionCodes.ACTION_Attack, false);
            _pc.sendPackets(packet);
            _pc.broadcastPacket(packet, _target);
            _pc.sendPackets(new S_DoActionGFX(_pc.getId(), ActionCodes.ACTION_Damage));
            _pc.broadcastPacket(new S_DoActionGFX(_pc.getId(), ActionCodes.ACTION_Damage));
        } else if (_calcType == NPC_PC) {
            if (_npc == null || _target == null)
                return;
            _npc.setHeading(_npc.targetDirection(_targetX, _targetY)); // 方向セット
            _npc.broadcastPacket(new S_SkillSound(_target.getId(), 6519));
            _npc.broadcastPacket(new S_DoActionGFX(_npc.getId(), ActionCodes.ACTION_Damage));
        }
    }

    // ■■■■ 相手の攻撃に対してカウンターバリアーが有効かを判別 ■■■■
    public boolean isShortDistance() {
        boolean isShortDistance = true;
        if (_calcType == PC_PC) {
            if (_weaponType == 20 || _weaponType == 62 || _weaponType2 == 17 || _weaponType2 == 19 || _pc.hasSkillEffect(L1SkillId.ARMOR_BRAKE)) {
                isShortDistance = false;
            }
        } else if (_calcType == NPC_PC) {
            if (_npc == null)
                return false;
            boolean isLongRange = (_npc.getLocation().getTileLineDistance(new Point(_targetX, _targetY)) > 1);
            int bowActId = _npc.getNpcTemplate().getBowActId();
            // 距離が2以上、攻撃者の弓のアクションIDがある場合は、ワン攻撃
            if (isLongRange && bowActId > 0) {
                isShortDistance = false;
            }
        }
        return isShortDistance;
    }

    // ■■■■ カウンターバリアーのダメージを反映 ■■■■
    public void commitCounterBarrier() {
        int damage = calcCounterBarrierDamage();
        if (damage == 0) {
            return;
        }
        if (_calcType == PC_PC) {
            _pc.receiveCounterBarrierDamage(_targetPc, damage);
        } else if (_calcType == NPC_PC) {
            _npc.receiveCounterBarrierDamage(_targetPc, damage);
        }
    }

    // ■■■■モータルボディのダメージを反映 ■■■■
    public void commitMortalBody() {
        // int damage = 40;
        // if (damage == 0) {
        // return;
        // }
        int ac = Math.max(0, 10 - _targetPc.getAC().getAc());
        int damage = ac / 2;

        if (damage == 0) {
            return;
        }
        if (damage <= 40) {
            damage = 40;
        }
        if (_calcType == PC_PC) {
            _pc.receiveDamage(_targetPc, damage);
        } else if (_calcType == NPC_PC) {
            _npc.receiveDamage(_targetPc, damage);
        }
    }

    // ●●●● カウンターバリアーのダメージを算出 ●●●●
    private int calcCounterBarrierDamage() {
        double damage = 0;
        L1ItemInstance weapon = null;
        weapon = _targetPc.getWeapon();
        if (weapon != null) {
            if (weapon.getItem().getType() == 3) {
                damage = Math.round((weapon.getItem().getDmgLarge() + weapon.getEnchantLevel() + weapon.getItem().getDmgModifier()) * 2);
                
            }
        }
        return (int) damage;
    }

    // ●●●● 戦士タイタンダメージを算出 ●●●●
    private int calcTitanDamage() {
        double damage = 0;
        L1ItemInstance weapon = null;
        weapon = _targetPc.getWeapon();
        if (weapon != null) {
            damage = Math.round((weapon.getItem().getDmgLarge() + weapon.getEnchantLevel() + weapon.getItem().getDmgModifier()) * 1.5);
        }
        return (int) damage;
    }

    /*
     * 武器を損傷させる。大NPCの場合には、損傷確率は10％とする。祝福武器は3％とする。
     */
    private void damageNpcWeaponDurability() {
        int chance = 10; //一般武器
        int bchance = 3; //祝福された武器の損傷確率

        /** ロボットシステム **/
        if (_pc.getRobotAi() != null) {
            return;
        }
        /** ロボットシステム **/

        /*
         * 損傷していないNPC、素手、損傷していない武器の使用、SOF中の場合は何もしない。
         */
        if (_calcType != PC_NPC || _targetNpc.getNpcTemplate().is_hard() == false || _weaponType == 0 || weapon.getItem().get_canbedmg() == 0
                || _pc.hasSkillEffect(SOUL_OF_FLAME)) {
            return;
        }
        // 通常の武器・呪われた武器
        if ((_weaponBless == 1 || _weaponBless == 2) && ((_random.nextInt(100) + 1) < chance)) {
            // \f1あなたの%0が破損しました。
            _pc.sendPackets(new S_ServerMessage(268, weapon.getLogName()));
            _pc.getInventory().receiveDamage(weapon);
        }
        // 祝福された武器
        if (_weaponBless == 0 && ((_random.nextInt(100) + 1) < bchance)) {
            // \f1あなたの%0が破損しました。
            _pc.sendPackets(new S_ServerMessage(268, weapon.getLogName()));
            _pc.getInventory().receiveDamage(weapon);
        }
    }

    /** 属性矢 **/
    private int attrArrow(L1ItemInstance arrow, L1NpcInstance npc) {
        int itemId = arrow.getItem().getItemId();
        int damage = 0;
        int NpcWeakAttr = _targetNpc.getNpcTemplate().get_weakAttr();
        if (itemId == 820014) {// 受領のブラックミスリルアロー
            if (NpcWeakAttr == 2) {
                damage = 3;
            }
        } else if (itemId == 820015) {// 風鈴のブラックミスリルアロー
            if (NpcWeakAttr == 8) {
                damage = 3;
            }
        } else if (itemId == 820016) {// 指令のブラックミスリルアロー
            if (NpcWeakAttr == 1) {
                damage = 3;
            }
        } else if (itemId == 820017) {// ファイアーブラックミスリルアロー
            if (NpcWeakAttr == 4) {
                damage = 3;
            }
        }
        return damage;
    }

    /** ステータス+武器による攻城 **/
    private int PchitAdd() {
        int value = 0;
        if(_pc instanceof L1RobotInstance){
        	return 10;
        }
        if (_pc.getAbility().getTotalStr() > 59) {
            value += (strHit[58]);
        } else {
            value += (strHit[_pc.getAbility().getTotalStr() - 1]);
        }

        if (_pc.getAbility().getTotalDex() > 60) {
            value += (dexHit[59]);
        } else {
            value += (dexHit[_pc.getAbility().getTotalDex() - 1]);
        }

        if (_weaponType != 20 && _weaponType != 62) {
            value += _weaponAddHit + _pc.getHitup() + _pc.getHitRate() + (_weaponEnchant / 2);
        } else {
            value += _weaponAddHit + _pc.getBowHitup() + _pc.getBowHitRate() + (_weaponEnchant / 2);
        }
		if (Sweapon != null){ // 戦士両手攻城追加
			value += _SweaponAddHit + (_SweaponEnchant /2);
		}
        return value;
    }

    /** ターゲットPC回避スキルの演算 **/
    private int toPcSkillHit() {
        int value = 0;
        if (_targetPc.hasSkillEffect(UNCANNY_DODGE)) {
            value -= 5;// 2
        }
        if (_targetPc.hasSkillEffect(FEAR)) {
            value += 4;
        }
        if (_targetPc.hasSkillEffect(MIRROR_IMAGE)) {
            value -= 4;
        }
        return value;
    }

    /** Hit最終演算 **/
    private boolean hitRateCal(int AD, int DD, int fumble, int critical) {
        if (AD <= fumble) {
            _hitRate = 0;
            return true;
        } else if (AD >= critical) {
            _hitRate = 100;
        } else {
            if (AD > DD) {
                _hitRate = 100;
            } else if (AD <= DD) {
                _hitRate = 0;
                return true;
            }
        }
        return false;
    }

    /** ターゲットPC DD演算 **/
    private int toPcDD(int dv) {
        if (_targetPc.getAC().getAc() >= 0) {
            return 10 - _targetPc.getAC().getAc();
        } else {
            return 10 + _random.nextInt(dv) + 1;
        }
    }
    
	private double roomtisAddDamage() {
		int dmg = 0;
		if(_calcType == PC_PC || _calcType == PC_NPC) {
			L1ItemInstance blackRumti = _pc.getInventory().checkEquippedItem(222340);
			if(blackRumti == null)
				blackRumti = _pc.getInventory().checkEquippedItem(222341);
			if(blackRumti != null) {
				int chance = 0;
				if(blackRumti.getBless() == 0 && blackRumti.getEnchantLevel() >= 4) {
					chance = 2 + blackRumti.getEnchantLevel() - 4;
				}
				else if(blackRumti.getEnchantLevel() >= 5) {
					chance = 2 + blackRumti.getEnchantLevel() - 5;
				}
				if(chance != 0) {
					if(_random.nextInt(100) < Config.ROOMTIECE_CHANCE) {
						dmg += 20;
						_pc.sendPackets(new S_SkillSound(_pc.getId(), 13931));
						_pc.broadcastPacket(new S_SkillSound(_pc.getId(), 13931));
					}
				}
			}
		}
		return dmg;
	}
	
	
	private int roomtisDecreaseDamage() {
		int damage = 0;
		if (_calcType == NPC_PC || _calcType == PC_PC) {
			L1ItemInstance item = _targetPc.getInventory().checkEquippedItem(22229);
			if (item != null && item.getEnchantLevel() >= 5) {
				if (_random.nextInt(100) < 2 + item.getEnchantLevel() - 5) {
					damage = 20;
					_targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 12118), true);
				}
			}

			L1ItemInstance item2 = _targetPc.getInventory().checkEquippedItem(222337);
			if (item2 != null && item2.getEnchantLevel() >= 5) {
				if (_random.nextInt(100) < Config.ROOMTIECE_CHANCE) {
					damage = 20;
					_targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 12118), true);
				}
			}
		}
		return damage;
	}


    public void ArmorDestory() {
        for (L1ItemInstance armorItem : _targetPc.getInventory().getItems()) {
            if (armorItem.getItem().getType2() == 2 && armorItem.getItem().getType() == 2) {
                int armorId = armorItem.getItemId();
                L1ItemInstance item = _targetPc.getInventory().findEquippedItemId(armorId);
                if (item != null) {
                    int chance = _random.nextInt(100) + 1;
                    if (item.get_durability() == (armorItem.getItem().get_ac() * -1)) {
                        break;
                    } else {
                        if (chance <= 15) {
                            item.set_durability(item.get_durability() + 1);
                            _targetPc.getInventory().updateItem(item, L1PcInventory.COL_DURABILITY);
                            _targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 14549));
                            _targetPc.getAC().addAc(1);
                            _targetPc.sendPackets(new S_OwnCharAttrDef(_targetPc));
                            _targetPc.sendPackets(new S_ServerMessage(268, armorItem.getLogName()));
                            Broadcaster.broadcastPacket(_targetPc, new S_SkillSound(_targetPc.getId(), 14549));
                        }
                    }
                }
            }
        }
    }
}
