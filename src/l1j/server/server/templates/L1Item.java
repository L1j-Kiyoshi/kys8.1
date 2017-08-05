package l1j.server.server.templates;

import java.io.Serializable;

public abstract class L1Item implements Serializable {

    private static final long serialVersionUID = 1L;

    public L1Item() {
    }

    // ■■■■■■ L1EtcItem, L1Weapon, L1Armor に共通する項目 ■■■■■■

    private int _type2; //  0=L1EtcItem, 1=L1Weapon, 2=L1Armor

    /**
     * @return 0 if L1EtcItem, 1 if L1Weapon, 2 if L1Armor
     */
    public int getType2() {
        return _type2;
    }

    public void setType2(int type) {
        _type2 = type;
    }

    private int _itemId;

    public int getItemId() {
        return _itemId;
    }

    public void setItemId(int itemId) {
        _itemId = itemId;
    }

    private String _name;

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    private String _Magic_name;

    public String getMagicName() {
        return _Magic_name;
    }

    public void setMagicName(String name) {
        _Magic_name = name;
    }


    private String _nameId;

    public String getNameId() {
        return _nameId;
    }

    public void setNameId(String nameid) {
        _nameId = nameid;
    }

    private int _type;

    /**
     * アイテムの種類を返す。<br>
     *
     * @return <p>
     * [etcitem]<br>
     * 0:arrow, 1:wand, 2:light, 3:gem, 4:totem, 5:firecracker, 6:potion,
     * 7:food, 8:scroll, 9:questitem, 10:spellbook, 11:petitem, 12:other,
     * 13:material, 14:event, 15:sting
     * </p>
     * <p>
     * [weapon]<br>
     * 1:sword, 2:dagger, 3:tohandsword, 4:bow, 5:spear, 6:blunt, 7:staff,
     * 8:throwingknife, 9:arrow, 10:gauntlet, 11:claw, 12:edoryu, 13:singlebow,
     * 14:singlespear, 15:tohandblunt, 16:tohandstaff
     * </p>
     * <p>
     * [armor]<br>
     * 1:helm, 2:armor, 3:T, 4:cloak, 5:glove, 6:boots, 7:shield, 8:amulet,
     * 9:ring, 10:belt, 11:ring2, 12:earring , 13:garder 14,rune1, 15ゲートル16文章17記章18肩甲
     */
    public int getType() {
        return _type;
    }

    public void setType(int type) {
        _type = type;
    }

    private int _type1;

    /**
     * アイテムの種類を返す。<br>
     *
     * @return <p>
     * [weapon]<br>
     * sword:4, dagger:46, tohandsword:50, bow:20, blunt:11, spear:24, staff:40,
     * throwingknife:2922, arrow:66, gauntlet:62, claw:58, edoryu:54,
     * singlebow:20, singlespear:24, tohandblunt:11, tohandstaff:40
     * </p>
     */
    public int getType1() {
        return _type1;
    }

    public void setType1(int type1) {
        _type1 = type1;
    }

    private int _material;

    /**
     * アイテムの素材を返す
     *
     * @return 0：none 1：液体2：web 3：植物性4：動物5：地6：フォー7：血8：木9：ゴール10：竜のリン11：鉄
     * 12：鋼鉄13：同14：15：金の16：プラチナ17：ミスリル18：ブラックミスリル19：ガラス20：宝石
     * 21：鉱物22：オリハルコン
     */
    public int getMaterial() {
        return _material;
    }

    public void setMaterial(int material) {
        _material = material;
    }

    private int _weight;

    public int getWeight() {
        return _weight;
    }

    public void setWeight(int weight) {
        _weight = weight;
    }

    private int _gfxId;

    public int getGfxId() {
        return _gfxId;
    }

    public void setGfxId(int gfxId) {
        _gfxId = gfxId;
    }

    private int _Mainid;

    public int getMainId() {
        return _Mainid;
    }

    public void setMainId(int m) {
        _Mainid = m;
    }

    private int _Mainid2;

    public int getMainId2() {
        return _Mainid2;
    }

    public void setMainId2(int m) {
        _Mainid2 = m;
    }

    private int _Mainid3;

    public int getMainId3() {
        return _Mainid3;
    }

    public void setMainId3(int m) {
        _Mainid3 = m;
    }

    private int _SetId;

    public int getSetId() {
        return _SetId;
    }

    public void setSetId(int m) {
        _SetId = m;
    }


    private int _groundGfxId;

    public int getGroundGfxId() {
        return _groundGfxId;
    }

    public void setGroundGfxId(int groundGfxId) {
        _groundGfxId = groundGfxId;
    }

    private int _minLevel;

    private int _itemDescId;

    /**
     * 感情時に表示されるItemDesc.tblのメッセージIDを返す。
     */
    public int getItemDescId() {
        return _itemDescId;
    }

    public void setItemDescId(int descId) {
        _itemDescId = descId;
    }

    public int getMinLevel() {
        return _minLevel;
    }

    public void setMinLevel(int level) {
        _minLevel = level;
    }

    private int _maxLevel;

    public int getMaxLevel() {
        return _maxLevel;
    }

    public void setMaxLevel(int maxlvl) {
        _maxLevel = maxlvl;
    }

    private int _bless;

    public int getBless() {
        return _bless;
    }

    public void setBless(int i) {
        _bless = i;
    }

    private boolean _tradable;

    public boolean isTradable() {
        return _tradable;
    }

    public void setTradable(boolean flag) {
        _tradable = flag;
    }

    private boolean _cantDelete;

    public boolean isCantDelete() {
        return _cantDelete;
    }

    public void setCantDelete(boolean flag) {
        _cantDelete = flag;
    }

    private boolean _save_at_once;

    private int _AccessoryProcess;

    public int getAccessoryProcess() {
        return _AccessoryProcess;
    }

    public void setAccessoryProcess(int i) {
        _AccessoryProcess = i;
    }

    /**
     * アイテムの数が変化したとき、すぐにDBに書き込むべきかを返す。
     */
    public boolean isToBeSavedAtOnce() {
        return _save_at_once;
    }

    public void setToBeSavedAtOnce(boolean flag) {
        _save_at_once = flag;
    }

    // ■■■■■■ L1EtcItem, L1Weapon に共通する項目 ■■■■■■


    private int _dmgSmall = 0;

    public int getDmgSmall() {
        return _dmgSmall;
    }

    public void setDmgSmall(int dmgSmall) {
        _dmgSmall = dmgSmall;
    }

    private int _dmgLarge = 0;

    public int getDmgLarge() {
        return _dmgLarge;
    }

    public void setDmgLarge(int dmgLarge) {
        _dmgLarge = dmgLarge;
    }

    // ■■■■■■ L1EtcItem, L1Armor に共通する項目 ■■■■■■

    // ■■■■■■ L1Weapon, L1Armor に共通する項目 ■■■■■■

    private int _safeEnchant = 0;

    public int get_safeenchant() {
        return _safeEnchant;
    }

    public void set_safeenchant(int safeenchant) {
        _safeEnchant = safeenchant;
    }

    private int _enchant = 0;

    public int get_enchant() {
        return _enchant;
    }

    public void set_enchant(int enchant) {
        _enchant = enchant;
    }


    private boolean _useRoyal = false;

    public boolean isUseRoyal() {
        return _useRoyal;
    }

    public void setUseRoyal(boolean flag) {
        _useRoyal = flag;
    }

    private boolean _useKnight = false;

    public boolean isUseKnight() {
        return _useKnight;
    }

    public void setUseKnight(boolean flag) {
        _useKnight = flag;
    }

    private boolean _useElf = false;

    public boolean isUseElf() {
        return _useElf;
    }

    public void setUseElf(boolean flag) {
        _useElf = flag;
    }

    private boolean _useMage = false;

    public boolean isUseMage() {
        return _useMage;
    }

    public void setUseMage(boolean flag) {
        _useMage = flag;
    }

    private boolean _useDarkelf = false;

    public boolean isUseDarkelf() {
        return _useDarkelf;
    }

    public void setUseDarkelf(boolean flag) {
        _useDarkelf = flag;
    }

    private boolean _useDragonKnight = false;

    public boolean isUseDragonKnight() {
        return _useDragonKnight;
    }

    public void setUseDragonKnight(boolean flag) {
        _useDragonKnight = flag;
    }

    private boolean _useBlackwizard = false;

    public boolean isUseBlackwizard() {
        return _useBlackwizard;
    }

    private boolean _useWarrior = false;

    public boolean isUseWarrior() {
        return _useWarrior;
    }

    public void setUseWarrior(boolean flag) {
        _useWarrior = flag;
    }

    private boolean _useHighPet = false;

    public boolean isUseHighPet() {
        return _useHighPet;
    }

    public void setUseHighPet(boolean flag) {
        _useHighPet = flag;
    }

    public void setUseBlackwizard(boolean flag) {
        _useBlackwizard = flag;
    }

    private byte _addstr = 0;

    public byte get_addstr() {
        return _addstr;
    }

    public void set_addstr(byte addstr) {
        _addstr = addstr;
    }

    private byte _adddex = 0;

    public byte get_adddex() {
        return _adddex;
    }

    public void set_adddex(byte adddex) {
        _adddex = adddex;
    }

    private byte _addcon = 0;

    public byte get_addcon() {
        return _addcon;
    }

    public void set_addcon(byte addcon) {
        _addcon = addcon;
    }

    private byte _addexp1 = 0;

    public byte get_addexp1() {
        return _addexp1;
    }

    public void set_addexp1(byte addexp1) {
        _addexp1 = addexp1;
    }

    private byte _addint = 0;

    public byte get_addint() {
        return _addint;
    }

    public void set_addint(byte addint) {
        _addint = addint;
    }

    private byte _addwis = 0;

    public byte get_addwis() {
        return _addwis;
    }

    public void set_addwis(byte addwis) {
        _addwis = addwis;
    }

    private byte _addcha = 0;

    public byte get_addcha() {
        return _addcha;
    }

    public void set_addcha(byte addcha) {
        _addcha = addcha;
    }

    private int _addhp = 0;

    public int get_addhp() {
        return _addhp;
    }

    public void set_addhp(int addhp) {
        _addhp = addhp;
    }

    private int _addmp = 0;

    public int get_addmp() {
        return _addmp;
    }

    public void set_addmp(int addmp) {
        _addmp = addmp;
    }

    private int _addhpr = 0;

    public int get_addhpr() {
        return _addhpr;
    }

    public void set_addhpr(int addhpr) {
        _addhpr = addhpr;
    }

    private int _addmpr = 0;

    public int get_addmpr() {
        return _addmpr;
    }

    public void set_addmpr(int addmpr) {
        _addmpr = addmpr;
    }

    private int _addsp = 0;

    public int get_addsp() {
        return _addsp;
    }

    public void set_addsp(int addsp) {
        _addsp = addsp;
    }

    private int _mdef = 0;

    public int get_mdef() {
        return _mdef;
    }

    public void set_mdef(int i) {
        this._mdef = i;
    }

    private boolean _isHasteItem = false;

    public boolean isHasteItem() {
        return _isHasteItem;
    }

    public void setHasteItem(boolean flag) {
        _isHasteItem = flag;
    }

    private int _maxUseTime = 0;

    public int getMaxUseTime() {
        return _maxUseTime;
    }

    public void setMaxUseTime(int i) {
        _maxUseTime = i;
    }

    private int _useType;

    /**
     * 使用した時のリアクションを決定するタイプを返す。
     */
    public int getUseType() {
        return _useType;
    }

    public void setUseType(int useType) {
        _useType = useType;
    }

    private int _foodVolume;

    /**
     * 肉などのアイテムに設定されている満腹度を返す。
     */
    public int getFoodVolume() {
        return _foodVolume;
    }

    public void setFoodVolume(int volume) {
        _foodVolume = volume;
    }

    /**
     * ランプなどのアイテムに設定されている明るさを返す。
     */
    public int getLightRange() {
        if (_itemId == 40001) {
            return 11;
        } else if (_itemId == 40002) {
            return 14;
        } else if (_itemId == 40004) {
            return 14;
        } else if (_itemId == 40005) {
            return 8;
        } else {
            return 0;
        }
    }

    public int getLightFuel() {
        if (_itemId == 40001) {
            return 600;
        } else if (_itemId == 40002) {
            return 0;
        } else if (_itemId == 40003) {
            return 600;
        } else if (_itemId == 40004) {
            return 0;
        } else if (_itemId == 40005) {
            return 600;
        } else {
            return 0;
        }
    }

    /***魔法の触媒の種類を返す。 */
    public int getMagicCatalystType() {
        int type = 0;
        switch (getItemId()) {
            case 40318: // 魔力の石
            case 30079:
                type = 166; // 材料によるアイコンパッケージ
                break;
            case 40319: // 精霊の玉
            case 30078: // 象牙の塔の精霊の玉
                type = 569;
                break;
            case 40321: // 黒曜石
            case 30080: // 象牙の塔の黒曜石
                type = 837;
                break;
            case 30077: // 象牙の塔のユグドラの実
            case 210036: // ユグドラの実
                type = 3674;
                break;
            case 30081: // 象牙の塔の刻印のボーンピース
            case 210035: // 刻印のボーンピース
                type = 3605;
                break;
            case 30082: // 象牙の塔の属性石
            case 210038: // 属性石
                type = 3606;
                break;
            case 30076: // 象牙の塔のエルヴンワッフル
            case 40068: //エルヴンワッフル
            case 140068:
                type = 86;
                break;
            case 41295: //栄養餌
                type = 14467;
                break;
            case 41293: //高弾力釣り竿
                type = 14462;
                break;
            case 41294: // リール装着高弾力釣り竿
                type = 14463;
                break;
            case 41305:// リール装着銀色釣り竿
                type = 14464;
                break;
            case 41296: //ベリーアナ
                type = 14468;
                break;
            case 41297: //ブルーベリーアナ
                type = 14469;
                break;
            case 41298: //インコベリーアナ
                type = 14470;
                break;
            case 41246: //結晶
                type = 2257;
                break;
            case 41159: //羽
                type = 2671;
                break;
        }
        return type;
    }


    // ■■■■■■ L1EtcItem でオーバーライド（override）する項目 ■■■■■■
    public boolean isStackable() {
        return false;
    }

    public int get_locx() {
        return 0;
    }

    public int get_locy() {
        return 0;
    }

    public short get_mapid() {
        return 0;
    }

    public int get_delayid() {
        return 0;
    }

    public int get_delaytime() {
        return 0;
    }

    public int getMaxChargeCount() {
        return 0;
    }

    public int get_delayEffect() {
        return 0;
    }

    // ■■■■■■ L1Weapon でオーバーライド（override）する項目 ■■■■■■

    public int getHitModifier() {
        return 0;
    }

    public int getDmgModifier() {
        return 0;
    }

    public int getDoubleDmgChance() {
        return 0;
    }

    public int getIgnoreReductionByWeapon() {
        return 0;
    }

    public int getMagicDmgModifier() {
        return 0;
    }

    public int get_canbedmg() {
        return 0;
    }

    public int get_penetration() {
        return 0;
    }

    public boolean isTwohandedWeapon() {
        return false;
    }

    // ■■■■■■ L1Armor でオーバーライド（override）する項目 ■■■■■■
    public int get_ac() {
        return 0;
    }

    public int getIgnoreReductionByArmor() {
        return 0;
    }

    public int getDamageReduction() {
        return 0;
    }

    public int getWeightReduction() {
        return 0;
    }

    public int getDmgRate() {
        return 0;
    }

    public int getHitRate() {
        return 0;
    }

    public int getBowHitRate() {
        return 0;
    }

    public int getBowDmgRate() {
        return 0;
    }

    public int get_defense_water() {
        return 0;
    }

    public int get_defense_fire() {
        return 0;
    }

    public int get_defense_earth() {
        return 0;
    }

    public int get_defense_wind() {
        return 0;
    }

    public int get_regist_stun() {
        return 0;
    }

    public int get_regist_stone() {
        return 0;
    }

    public int get_regist_sleep() {
        return 0;
    }

    public int get_regist_freeze() {
        return 0;
    }

    public int get_regist_sustain() {
        return 0;
    }

    public int get_regist_blind() {
        return 0;
    }

    public int get_regist_DESPERADO() {
        return 0;
    }

    public int get_regist_calcPcDefense() {
        return 0;
    }

    public int get_regist_PVPweaponTotalDamage() {
        return 0;
    }

    private int _grade; // ● アクセサリーのステップ

    public int getGrade() {
        return _grade;
    }

    public void setGrade(int grade) {
        _grade = grade;
    }

    private int _price; // ● 価格

    public int get_price() {
        return _price;
    }

    public void set_price(int price) {
        _price = price;
    }

}
