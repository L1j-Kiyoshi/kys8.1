package l1j.server.server.model.Instance;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import l1j.server.server.GeneralThreadPool; //CrockController
import l1j.server.server.clientpackets.ClientBasePacket;
import l1j.server.server.datatables.ArmorSetTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.PetTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1EquipmentTimer;
import l1j.server.server.model.L1ItemOwnerTimer;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.templates.L1Armor;
import l1j.server.server.templates.L1ArmorSets;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1Pet;
import l1j.server.server.templates.L1Skills;
import l1j.server.server.utils.BinaryOutputStream;

public class L1ItemInstance extends L1Object {

    public static final int CHAOS_SPIRIT = 1;
    public static final int CORRUPT_SPIRIT = 2;
    public static final int BALLACAS_SPIRIT = 3;
    public static final int ANTARAS_SPIRIT = 4;
    public static final int LINDBIOR_SPIRIT = 5;
    public static final int PAPURION_SPIRIT = 6;
    public static final int DEATHKNIGHT_SPIRIT = 7;
    public static final int BAPPOMAT_SPIRIT = 8;
    public static final int BALLOG_SPIRIT = 9;
    public static final int ARES_SPIRIT = 10;

    private static final long serialVersionUID = 1L;
    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.KOREA);

    public boolean _isSecond = false;

    private int _count;

    private int _itemId;

    private L1Item _item;

    private boolean _isEquipped = false;

    private int _enchantLevel;

    private int _attrenchantLevel;

    private boolean _isIdentified = false;

    private int _durability;

    private int _chargeCount;

    private int _specialEnchant;

    private int _remainingTime;

    private Timestamp _lastUsed = null;

    private Timestamp _endTime = null;

    /**
     * パッケージ店
     **/
    private boolean _isPackage = false;

    private int bless;

    private int _lastWeight;

    private final LastStatus _lastStatus = new LastStatus();

    private Map<Integer, EnchantTimer> _skillEffect = new HashMap<Integer, EnchantTimer>();

    public L1PcInstance _cha;

    public L1ItemInstance() {
        _count = 1;
        _enchantLevel = 0;
        _specialEnchant = 0;
    }

    public L1ItemInstance(L1Item item, int count) {
        this();
        setItem(item);
        setCount(count);
    }

    public L1ItemInstance(L1Item item) {
        this(item, 1);
    }

    public void clickItem(L1Character cha, ClientBasePacket packet) {
    }

    public boolean isSpecialEnchantable() {
        return (_specialEnchant & 0xFF) == 1;
    }

    public void setSpecialEnchantable() {
        _specialEnchant = 1;
    }

    public int getSpecialEnchant() {
        return _specialEnchant;
    }

    public int getSpecialEnchant(int index) {
        return ((_specialEnchant >> (8 * index)) & 0xFF);
    }

    public void setSpecialEnchant(int enchant) {
        _specialEnchant = enchant;
    }

    public void setSpecialEnchant(int index, int enchant) {
        _specialEnchant |= enchant << (8 * index);
    }

    public boolean isIdentified() {
        return _isIdentified;
    }

    public void setIdentified(boolean identified) {
        _isIdentified = identified;
    }

    public String getName() {
        return _item.getName();
    }

    public int getCount() {
        return _count;
    }

    public void setCount(int count) {
        _count = count;
    }

    public boolean isEquipped() {
        return _isEquipped;
    }

    public void setEquipped(boolean equipped) {
        _isEquipped = equipped;
    }

    public L1Item getItem() {
        return _item;
    }

    public void setItem(L1Item item) {
        _item = item;
        _itemId = item.getItemId();
    }

    public int getItemId() {
        return _itemId;
    }

    public void setItemId(int itemId) {
        _itemId = itemId;
    }

    public boolean isStackable() {
        return _item.isStackable();
    }

    @Override
    public void onAction(L1PcInstance player) {
    }

    public int getEnchantLevel() {
        return _enchantLevel;
    }

    public void setEnchantLevel(int enchantLevel) {
        _enchantLevel = enchantLevel;
    }

    public int getAttrEnchantLevel() {
        return _attrenchantLevel;
    }

    public int getHitModifierByAttrEnchant() {
        if (getAttrEnchantLevel() == 0) {
            return 0;
        } else if (getAttrEnchantLevel() % 3 == 0) {
            return 3;
        }

        return getAttrEnchantLevel() % 3;
    }

    public void setAttrEnchantLevel(int attrenchantLevel) {
        _attrenchantLevel = attrenchantLevel;
    }

    public int get_gfxid() {
        return _item.getGfxId();
    }

    public int get_durability() {
        return _durability;
    }

    public int getChargeCount() {
        return _chargeCount;
    }

    public void setChargeCount(int i) {
        _chargeCount = i;
    }

    public int getRemainingTime() {
        return _remainingTime;
    }

    public void setRemainingTime(int i) {
        _remainingTime = i;
    }

    public void setLastUsed(Timestamp t) {
        _lastUsed = t;
    }

    public Timestamp getLastUsed() {
        return _lastUsed;
    }

    public int getBless() {
        return bless;
    }

    public void setBless(int i) {
        bless = i;
    }

    public int getLastWeight() {
        return _lastWeight;
    }

    public void setLastWeight(int weight) {
        _lastWeight = weight;
    }

    public Timestamp getEndTime() {
        return _endTime;
    }

    public void setEndTime(Timestamp t) {
        _endTime = t;
    }

    /**
     * パッケージ店
     **/
    public boolean isPackage() {
        return _isPackage;
    }

    public void setPackage(boolean _isPackage) {
        this._isPackage = _isPackage;
    }

    private long _itemdelay3;

    public long getItemdelay3() {
        return _itemdelay3;
    }

    public void setItemdelay3(long itemdelay3) {
        _itemdelay3 = itemdelay3;
    }

    /**
     * ルームティス青い光のイヤリングポーション効率表示
     **/
    private String RoomtisHealingPotion() {
        int lvl = getEnchantLevel();
        String in = "";
        switch (lvl) {
            case 0:
                in = "ポーション回復量2％+ 2";
                break;
            case 1:
                in = "ポーション回復量6％+6";
                break;
            case 2:
                in = "ポーション回復量8％+8";
                break;
            case 3:
                in = "ポーション回復量10％+ 10";
                break;
            case 4:
                in = "ポーション回復量12％+ 12";
                break;
            case 5:
                in = "ポーション回復量14％+14";
                break;
            case 6:
                in = "ポーション回復量16％+16";
                break;
            case 7:
                in = "ポーション回復量18％+18";
                break;
            case 8:
                in = "ポーション回復量20％+20";
                break;
            default:
                break;
        }
        return in;
    }

    /**
     * ルームティス青い光のイヤリングポーション効率表示
     **/
    private String RoomtisHealingPotion11() {
        int lvl = getEnchantLevel();
        String in = "";
        switch (lvl) {
            case 0:
                in = "恐怖回復減少効果を相殺+ 2％";
                break;
            case 1:
                in = "恐怖回復減少効果を相殺+ 6％";
                break;
            case 2:
                in = "恐怖回復減少効果を相殺+ 8％";
                break;
            case 3:
                in = "恐怖回復減少効果を相殺+ 10％";
                break;
            case 4:
                in = "恐怖回復減少効果を相殺+ 12％";
                break;
            case 5:
                in = "恐怖回復減少効果を相殺+ 14％";
                break;
            case 6:
                in = "恐怖回復減少効果を相殺+ 16％";
                break;
            case 7:
                in = "恐怖回復減少効果を相殺+ 18％";
                break;
            case 8:
                in = "恐怖回復減少効果を相殺+ 20％";
                break;
            default:
                break;
        }
        return in;
    }

    /**
     * 祝福されたルームティス青い光のイヤリングポーション効率表示
     **/
    private String RoomtisHealingPotion1() {
        int lvl = getEnchantLevel();
        String in = "";
        switch (lvl) {
            case 0:
                in = "ポーション回復量2％+ 2";
                break;
            case 1:
                in = "ポーション回復量6％+6";
                break;
            case 2:
                in = "ポーション回復量8％+8";
                break;
            case 3:
                in = "ポーション回復量12％+ 12";
                break;
            case 4:
                in = "ポーション回復量14％+14";
                break;
            case 5:
                in = "ポーション回復量16％+16";
                break;
            case 6:
                in = "ポーション回復量18％+18";
                break;
            case 7:
                in = "ポーション回復量20％+20";
                break;
            case 8:
                in = "ポーション回復量22％+22";
                break;
            default:
                break;
        }
        return in;
    }

    /**
     * ルームティス青い光のイヤリングポーション効率表示
     **/
    private String RoomtisHealingPotion12() {
        int lvl = getEnchantLevel();
        String in = "";
        switch (lvl) {
            case 0:
                in = "恐怖回復減少効果を相殺+ 2％";
                break;
            case 1:
                in = "恐怖回復減少効果を相殺+ 6％";
                break;
            case 2:
                in = "恐怖回復減少効果を相殺+ 8％";
                break;
            case 3:
                in = "恐怖回復減少効果を相殺+ 12％";
                break;
            case 4:
                in = "恐怖回復減少効果を相殺+ 14％";
                break;
            case 5:
                in = "恐怖回復減少効果を相殺+ 16％";
                break;
            case 6:
                in = "恐怖回復減少効果を相殺+ 18％";
                break;
            case 7:
                in = "恐怖回復減少効果を相殺+ 20％";
                break;
            case 8:
                in = "恐怖回復減少効果を相殺+ 22％";
                break;
            default:
                break;
        }
        return in;
    }

    public int getMr() {
        int mr = _item.get_mdef();
        int itemid = getItemId();
        if (itemid == 20011 || itemid == 20110 || itemid == 120011 || itemid == 22223 || itemid == 20117 // バポカッパ
                || getItemId() == 22213 || itemid == 120110 || itemid == 93001 || itemid == 490008 || itemid == 22365
                || itemid == 222328 || getItemId() >= 222300 && getItemId() <= 222303 || itemid == 222328) {
            mr += getEnchantLevel();
        } else if (itemid == 20056 || itemid == 120056 || itemid == 220056 || itemid == 93002 || itemid == 222324
                || itemid == 222325) {
            mr += getEnchantLevel() * 2;
        } else if (itemid == 20079 || itemid == 20078 || itemid == 20074 || itemid == 120074 || itemid == 20049
                || itemid == 20050) {
            mr += getEnchantLevel() * 3;
        }
        if (mr < 0)
            mr = 0;
        return mr;
    }

    public int addhp() {
        int hp = _item.get_addhp();

        return hp;
    }

    public int addmp() {
        int mp = _item.get_addmp();
        return mp;
    }

    public int addsp() {
        int sp = _item.get_addsp();
        return sp;
    }

    public void set_durability(int i) {
        if (i < 0) {
            i = 0;
        }

        if (i > 127) {
            i = 127;
        }
        _durability = i;
    }

    public int getWeight() {
        if (getItem().getWeight() == 0) {
            return 0;
        } else {
            return Math.max(getCount() * getItem().getWeight() / 1000, 1);
        }
    }

    public class LastStatus {
        public int count;
        public int itemId;
        public boolean isEquipped = false;
        public int enchantLevel;
        public boolean isIdentified = true;
        public int durability;
        public int chargeCount;
        public int remainingTime;
        public Timestamp lastUsed = null;
        public int bless;
        public int attrenchantLevel;
        public int specialEnchant;
        public Timestamp endTime = null;

        public void updateAll() {
            count = getCount();
            itemId = getItemId();
            isEquipped = isEquipped();
            isIdentified = isIdentified();
            enchantLevel = getEnchantLevel();
            durability = get_durability();
            chargeCount = getChargeCount();
            remainingTime = getRemainingTime();
            lastUsed = getLastUsed();
            bless = getBless();
            attrenchantLevel = getAttrEnchantLevel();
            specialEnchant = getSpecialEnchant();
            endTime = getEndTime();
        }

        public void updateSpecialEnchant() {
            specialEnchant = getSpecialEnchant();
        }

        public void updateCount() {
            count = getCount();
        }

        public void updateItemId() {
            itemId = getItemId();
        }

        public void updateEquipped() {
            isEquipped = isEquipped();
        }

        public void updateIdentified() {
            isIdentified = isIdentified();
        }

        public void updateEnchantLevel() {
            enchantLevel = getEnchantLevel();
        }

        public void updateDuraility() {
            durability = get_durability();
        }

        public void updateChargeCount() {
            chargeCount = getChargeCount();
        }

        public void updateRemainingTime() {
            remainingTime = getRemainingTime();
        }

        public void updateLastUsed() {
            lastUsed = getLastUsed();
        }

        public void updateBless() {
            bless = getBless();
        }

        public void updateAttrEnchantLevel() {
            attrenchantLevel = getAttrEnchantLevel();
        }

        public void updateEndTime() {
            endTime = getEndTime();
        }
    }

    public LastStatus getLastStatus() {
        return _lastStatus;
    }

    public int getRecordingColumns() {
        int column = 0;

        if (getCount() != _lastStatus.count) {
            column += L1PcInventory.COL_COUNT;
        }
        if (getItemId() != _lastStatus.itemId) {
            column += L1PcInventory.COL_ITEMID;
        }
        if (isEquipped() != _lastStatus.isEquipped) {
            column += L1PcInventory.COL_EQUIPPED;
        }
        if (getEnchantLevel() != _lastStatus.enchantLevel) {
            column += L1PcInventory.COL_ENCHANTLVL;
        }
        if (get_durability() != _lastStatus.durability) {
            column += L1PcInventory.COL_DURABILITY;
        }
        if (getChargeCount() != _lastStatus.chargeCount) {
            column += L1PcInventory.COL_CHARGE_COUNT;
        }
        if (getLastUsed() != _lastStatus.lastUsed) {
            column += L1PcInventory.COL_DELAY_EFFECT;
        }
        if (isIdentified() != _lastStatus.isIdentified) {
            column += L1PcInventory.COL_IS_ID;
        }
        if (getRemainingTime() != _lastStatus.remainingTime) {
            column += L1PcInventory.COL_REMAINING_TIME;
        }
        if (getBless() != _lastStatus.bless) {
            column += L1PcInventory.COL_BLESS;
        }
        if (getAttrEnchantLevel() != _lastStatus.attrenchantLevel) {
            column += L1PcInventory.COL_ATTRENCHANTLVL;
        }

        if (getSpecialEnchant() != _lastStatus.specialEnchant) {
            column += L1PcInventory.COL_SPECIAL_ENCHANT;
        }
        return column;
    }

    public String getNumberedViewName(int count) {
        StringBuilder name = new StringBuilder();
        if (isSpecialEnchantable()) {
            name.append("\\f3");
        }
        name.append(getNumberedName(count));
        int itemType2 = getItem().getType2();

        int itemId = getItem().getItemId();

        if (itemId == 40314 || itemId == 40316) {
            L1Pet pet = PetTable.getInstance().getTemplate(getId());
            if (pet != null) {
                L1Npc npc = NpcTable.getInstance().getTemplate(pet.get_npcid());
                // name.append("[Lv." + pet.get_level() + " "
                // + npc.get_nameid() + "]");
                name.append("[Lv." + pet.get_level() + " " + pet.get_name() + "]HP" + pet.get_hp() + " "
                        + npc.get_nameid());
            }
        }
        /*
         * if(getItem().getType2() ==1 &&getAttrEnchantLevel()>0){
		 * switch(getAttrEnchantLevel()){ case 1: name.append("$6115"); break;
		 * case 2: name.append("$6116"); break; case 3: name.append("$6117");
		 * break; } }
		 */
        if (getItem().getType2() == 0 && getItem().getType() == 2) { // light
            if (isNowLighting()) {
                name.append(" ($10)");
            }
            if (itemId == 40001 || itemId == 40002) {
                if (getRemainingTime() <= 0) {
                    name.append(" ($11)");
                }
            }
        }

        if (getEndTime() != null) {
            if (isIdentified()) {
                name.append(" [" + sdf.format(getEndTime().getTime()) + "]");
            } else {
                name.append("");
            }
        }

        if (isEquipped()) {
            if (itemType2 == 1) {
                name.append(" ($9)");
            } else if (itemType2 == 2) {
                name.append(" ($117)");
            } else if (itemType2 == 0 && getItem().getType() == 11) { // petitem
                name.append(" ($117)");
            }
        }
        return name.toString();
    }

    public String getViewName() {
        return getNumberedViewName(_count);
    }

    public String getLogName() {
        return getNumberedName(_count);
    }

    /**
     * 属性エンチャント
     **/
    public String getNumberedName(int count) {
        StringBuilder name = new StringBuilder();

        if (isIdentified()) {
            if (getItem().getType2() == 1 || getItem().getType2() == 2) {
                switch (getAttrEnchantLevel()) {
                    case 1:
                        name.append("$6115");
                        break; // ファイアー1段
                    case 2:
                        name.append("$6116");
                        break; // ファイアー2段
                    case 3:
                        name.append("$6117");
                        break; // ファイアー3段（火属性）
                    case 4:
                        name.append("$14361");
                        break; // ファイアー4段
                    case 5:
                        name.append("$14365");
                        break; // ファイアー5段

                    case 6:
                        name.append("$6118");
                        break; // 受領1段
                    case 7:
                        name.append("$6119");
                        break; // 受領2段
                    case 8:
                        name.append("$6120");
                        break; // 受領3段（水属性）
                    case 9:
                        name.append("$14362");
                        break; // 受領4段
                    case 10:
                        name.append("$14366");
                        break; // 受領5段

                    case 11:
                        name.append("$6121");
                        break; // 風鈴1段
                    case 12:
                        name.append("$6122");
                        break; // 風鈴2段
                    case 13:
                        name.append("$6123");
                        break; // 風鈴3段（風の属性）
                    case 14:
                        name.append("$14363");
                        break; // 風鈴4段
                    case 15:
                        name.append("$14367");
                        break; // 風鈴5段

                    case 16:
                        name.append("$6124");
                        break; // 指令1段
                    case 17:
                        name.append("$6125");
                        break; // 指令2段
                    case 18:
                        name.append("$6126");
                        break; // 指令3段（地の属性）
                    case 19:
                        name.append("$14364");
                        break; // 指令4段
                    case 20:
                        name.append("$14368");
                        break; // 指令5段
                    default:
                        break;
                }
                if (getEnchantLevel() >= 0) {
                    name.append("+" + getEnchantLevel() + " ");
                } else if (getEnchantLevel() < 0) {
                    name.append(String.valueOf(getEnchantLevel()) + " ");
                }
            }
        }
        // マネージャー
        // name.append(_item.getNameId());
        name.append(_item.getName());

        // if (isSpecialEnchantable()) {
        // for (int i = 1; i <= 3; ++i) {
        // if (getSpecialEnchant(i) == 0) {
        // break;
        // }
        //
        // switch (getSpecialEnchant(i)) {
        // case CHAOS_SPIRIT:
        // name.append("[混沌] ");
        // break;
        // case CORRUPT_SPIRIT:
        // name.append("[堕落] ");
        // break;
        // case BALLACAS_SPIRIT:
        // name.append("[ヴァラカス] ");
        // break;
        // case ANTARAS_SPIRIT:
        // name.append("[アンタラス] ");
        // break;
        // case LINDBIOR_SPIRIT:
        // name.append("[リンドビオル] ");
        // break;
        // case PAPURION_SPIRIT:
        // name.append("[パプリオン] ");
        // break;
        // case DEATHKNIGHT_SPIRIT:
        // name.append("[デスナイト] ");
        // break;
        // case BAPPOMAT_SPIRIT:
        // name.append("[バフォメット] ");
        // break;
        // case BALLOG_SPIRIT:
        // name.append("[バルログ] ");
        // break;
        // case ARES_SPIRIT:
        // name.append("[アレス] ");
        // break;
        // }
        // }
        // }

        if (isIdentified()) {
            if (getItem().getMaxChargeCount() > 0) {
                name.append(" (" + getChargeCount() + ")");
            }
            if (getItem().getItemId() == 20383) {
                name.append(" (" + getChargeCount() + ")");
            }
            if (getItem().getMaxUseTime() > 0 && getItem().getType2() != 0) {
                name.append(" [" + getRemainingTime() + "]");
            }
        }

        if (count > 1) {
            name.append(" (" + count + ")");
        }

        return name.toString();
    }

    /**
     * アイテムの状態からサーバーのパケットに利用する形式のバイト列を生成し、返す。 1：打撃、2：エンチャントレベル、3：損傷度、4：両手剣、5：攻撃成功、6：追加打撃7：王子/王女、8：Str、9：Dex、10：Con、11：Wiz、
     * 12：Int、13：Cha、14：Hp、Mp 15：Mr、16：マナの吸収、17：呪術力、18：ヘイスト効果、19：Ac、20：幸運、21：栄養、22：明るさ、23：材質、24：弓命中、25：タイプ[writeH]、26：
     * レベル[writeH]、27：火属性28：水属性、29：風属性、30：地属性、31：最大Hp、32：最大Mp、33：耐性、34：生命吸収、35：弓打撃、36：branch用dummy、37：体力回復率、38：マナ回復率、39： `、
     *
     * @param armor
     */
    public byte[] getStatusBytes() {
        int itemType2 = getItem().getType2();
        int itemId = getItemId();
        BinaryOutputStream os = new BinaryOutputStream();
        /*if(getAttrEnchantLevel()==3){
		//	20 28 24 39 29
			os.writeC(0x20);
			os.writeC(0x28);
			os.writeC(0x24);
			os.writeC(0x39);
			os.writeC(0x29);
		}*/
        if (itemId == 66 && itemType2 == 1) {
            /** クラス着用部分 **/
            int bit = 0;
            bit |= getItem().isUseRoyal() ? 1 : 0;
            bit |= getItem().isUseKnight() ? 2 : 0;
            bit |= getItem().isUseElf() ? 4 : 0;
            bit |= getItem().isUseMage() ? 8 : 0;
            bit |= getItem().isUseDarkelf() ? 16 : 0;
            bit |= getItem().isUseDragonKnight() ? 32 : 0;
            bit |= getItem().isUseBlackwizard() ? 64 : 0;
            bit |= getItem().isUseWarrior() ? 128 : 0;
            os.writeC(7);
            os.writeC(bit);

            os.writeC(39);
            os.writeS("ダメージ" + getItem().getDmgSmall() + "+" + (getEnchantLevel() * 2) + "/" + getItem().getDmgLarge() + "+" + (getEnchantLevel() * 2));
            os.writeC(39);
            os.writeS("両手武器");
            os.writeC(39);
            os.writeS("STR +" + getItem().get_addstr());
            os.writeC(39);
            os.writeS("追加ダメージ+" + getItem().getDmgModifier());
            os.writeC(39);
            os.writeS("近距離命中+" + getItem().getHitModifier());
            os.writeC(39);
            os.writeS("魔法発動：ドラゴンの一撃");
            os.writeC(23);
            os.writeC(getItem().getMaterial());
            os.writeD(getWeight());
            return os.getBytes();
        }
        if (itemType2 == 0) { // etcitem
            if (dollSP() != 0) {
                os.writeC(39);
                os.writeS("SP +" + dollSP());
            }
            if (ShortDamage() != 0) {
                os.writeC(39);
                os.writeS("近距離ダメージ+" + ShortDamage());
            }
            if (ShortHit() != 0) {
                os.writeC(39);
                os.writeS("近距離命中+" + ShortHit());
            }
            if (LongDamage() != 0) {
                os.writeC(39);
                os.writeS("遠距離ダメージ+" + LongDamage());
            }
            if (LongHit() != 0) {
                os.writeC(39);
                os.writeS("遠距離命中+" + LongHit());
            }
            if (damageAdd() != 0) {
                os.writeC(39);
                os.writeS("確率追加ダメージ+" + damageAdd());
            }
            if (damageReduction() != 0) {
                os.writeC(39);
                os.writeS("ダメージリダクション+" + damageReduction());
            }
            if (stunLevel() != 0) {
                os.writeC(39);
                os.writeS("スタンレベル+" + stunLevel());
            }
            if (MaxHP() != 0) {
                os.writeC(39);
                os.writeS("最大HP +" + MaxHP());
            }
            if (MaxMP() != 0) {
                os.writeC(39);
                os.writeS("最大MP +" + MaxMP());
            }
            if (EXPBonus() != 0) {
                os.writeC(39);
                os.writeS("経験値ボーナス+" + EXPBonus() + "%");
            }
            if (HPRecovery() != 0) {
                os.writeC(39);
                os.writeS("32秒ごとにHP +" + HPRecovery() + "回復");
            }
            if (MPRecovery() != 0) {
                os.writeC(39);
                os.writeS("64秒ごとにMP +" + MPRecovery() + "回復");
            }
            if (magicActivation() != null) {
                os.writeC(39);
                os.writeS("発動：" + magicActivation());
            }
            if (itemGet() != null) {
                os.writeC(39);
                os.writeS("一定時間ごとに " + itemGet() + "獲得");
            }
            if (weightGauge() != 0) {
                os.writeC(39);
                os.writeS("重量ゲージ +" + weightGauge() + "%");
            }
            if (dollArmor() != 0) {
                os.writeC(39);
                os.writeS("AC-" + dollArmor());
            }
            if (dollMpr() != 0) {
                os.writeC(39);
                os.writeS("MP回復率+" + dollMpr());
            }
            if (resistStun() != 0) {
                os.writeC(39);
                os.writeS("スタン耐性+" + resistStun());
            }
            if (dollResist_Hold() != 0) {
                os.writeC(39);
                os.writeS("ホールド耐性+" + dollResist_Hold());
            }
            if (dollResist_Freeze() != 0) {
                os.writeC(39);
                os.writeS("凍結耐性+" + dollResist_Freeze());
            }
            switch (getItem().getType()) {
                case 2: // light
                    os.writeC(22);
                    os.writeH(getItem().getLightRange());
                    break;
                case 7: // food
                    os.writeC(21);
                    os.writeH(getItem().getFoodVolume());
                    break;
                case 0: // arrow
                case 15: // sting
                    os.writeC(1);
                    os.writeC(getItem().getDmgSmall());
                    os.writeC(getItem().getDmgLarge());
                    break;
                default:
                    os.writeC(23);
                    break;
            }
            os.writeC(getItem().getMaterial());
            os.writeD(getWeight());

            switch (getItem().getItemId()) {
                case 41288: //幻想アリの足のチーズ焼き
                    os.writeC(39);
                    os.writeS("AC-1ダメージ減少+5");
                    break;
                case 41280: // アリの足のチーズ焼き
                    os.writeC(39);
                    os.writeS("AC-1ダメージ減少+5");
                    break;
                case 41286: //幻想クマ肉焼き
                    os.writeC(39);
                    os.writeS("HP + 30、ダメージ減少+5");
                    break;
                case 41278: // クマ肉焼き
                    os.writeC(39);
                    os.writeS("HP + 30、ダメージ減少+5");
                    break;
                case 41289: // 幻想のフルーツサラダ
                    os.writeC(39);
                    os.writeS("MP+20");
                    break;
                case 41281: // フルーツサラダ
                    os.writeC(39);
                    os.writeS("MP+20");
                    break;
                case 41290: // 幻想のフルーツ甘酢あんかけ
                    os.writeC(39);
                    os.writeS("HP回復+3");
                    break;
                case 41282: // フルーツ甘酸っぱい
                    os.writeC(39);
                    os.writeS("HP回復+3");
                    break;
                case 41285: //幻想モンスターアイステーキ
                    os.writeC(39);
                    os.writeS("すべての属性抵抗力+10");
                    break;
                case 41277: // モンスターの目ステーキ
                    os.writeC(39);
                    os.writeS("すべての属性抵抗力+10");
                    break;
                case 41291: // 幻想イノシシ串焼き
                    os.writeC(39);
                    os.writeS("MR+5");
                    break;
                case 41283: // イノシシ肉の串焼き
                    os.writeC(39);
                    os.writeS("MR+5");
                    break;
                case 41292: // 幻想キノコのスープ
                    os.writeC(39);
                    os.writeS("経験値獲得量+ 1％");
                    break;
                case 41284: // キノコのスープ
                    os.writeC(39);
                    os.writeS("経験値獲得量+ 1％");
                    break;
                case 41287: // 幻想さん餅
                    os.writeC(39);
                    os.writeS("MP回復+3");
                    break;
                case 41279: // ナッツ餅
                    os.writeC(39);
                    os.writeS("MP回復+3");
                    break;
                case 49063: // 幻想クモ足串焼き
                    os.writeC(39);
                    os.writeS("SP+1");
                    break;
                case 49055: // クモの脚の串焼き
                    os.writeC(39);
                    os.writeS("SP+1");
                    break;
                case 49061: // 幻想スコーピオン焼き
                    os.writeC(39);
                    os.writeS("HP回復+2、MP回復+2");
                    break;
                case 49053: //スコーピオン焼き
                    os.writeC(39);
                    os.writeS("HP回復+2、MP回復+2");
                    break;
                case 49058: // 幻想ワニのステーキ
                    os.writeC(39);
                    os.writeS("最大HP + 30、最大MP + 30");
                    break;
                case 49050: // ワニのステーキ
                    os.writeC(39);
                    os.writeS("最大HP + 30、最大MP + 30");
                    break;
                case 49062: //幻想イレッカドムシチュー
                    os.writeC(39);
                    os.writeS("MR+10");
                    break;
                case 49054: // イレッカドムシチュー
                    os.writeC(39);
                    os.writeS("MR+10");
                    break;
                case 49057: // 幻想キャビアカナッペ
                    os.writeC(39);
                    os.writeS("近距離ダメージ+1、近距離命中+1");
                    break;
                case 49049: // キャビアカナッペ
                    os.writeC(39);
                    os.writeS("近距離ダメージ+1、近距離命中+1");
                    break;
                case 49064: // 幻想クラブ肉スープ
                    os.writeC(39);
                    os.writeS("経験値獲得量+ 2％");
                    break;
                case 49056: // クラブ肉スープ
                    os.writeC(39);
                    os.writeS("経験値獲得量+ 2％");
                    break;
                case 49060: // 幻想キウィパロット焼き
                    os.writeC(39);
                    os.writeS("遠距離ダメージ+1、遠距離命中+1");
                    break;
                case 49052: // キウィパロット焼き
                    os.writeC(39);
                    os.writeS("遠距離ダメージ+1、遠距離命中+1");
                    break;
                case 49059: // 幻想タートルドラゴンお菓子
                    os.writeC(39);
                    os.writeS("AC-2");
                    break;
                case 49051: // タートルドラゴンお菓子
                    os.writeC(39);
                    os.writeS("AC-2");
                    break;
                case 210057: // 幻想グリフォン焼き
                    os.writeC(39);
                    os.writeS("最大HP + 50、最大MP + 50");
                    break;
                case 210049: // グリフォン焼き
                    os.writeC(39);
                    os.writeS("最大HP + 50、最大MP + 50");
                    break;
                case 210059: //幻想タートル焼き
                    os.writeC(39);
                    os.writeS("AC-3");
                    break;
                case 210051: // タートル焼き
                    os.writeC(39);
                    os.writeS("AC-3");
                    break;
                case 210061: // 幻想ドレイク焼き
                    os.writeC(39);
                    os.writeS("SP + 2、MP回復+2");
                    break;
                case 210053: // ドレイク焼き
                    os.writeC(39);
                    os.writeS("SP + 2、MP回復+2");
                    break;
                case 210060: // 幻想レッサードラゴンの翼串
                    os.writeC(39);
                    os.writeS("MR + 15、すべての属性抵抗力+10");
                    break;
                case 210052: // レッサードラゴン羽串
                    os.writeC(39);
                    os.writeS("MR + 15、すべての属性抵抗力+10");
                    break;
                case 210063: // 幻想バシリスク卵スープ
                    os.writeC(39);
                    os.writeS("経験値獲得量+ 3％");
                    break;
                case 210055: // バシリスク卵スープ
                    os.writeC(39);
                    os.writeS("経験値獲得量+ 3％");
                    break;
                case 210062: // 幻想深海魚のシチュー
                    os.writeC(39);
                    os.writeS("最大HP + 30、HP回復+2");
                    break;
                case 210054: // 深海魚のシチュー
                    os.writeC(39);
                    os.writeS("最大HP + 30、HP回復+2");
                    break;
                case 210058: // 幻想コカトリスステーキ
                    os.writeC(39);
                    os.writeS("近距離命中+2、近距離ダメージ+1");
                    break;
                case 210050: //コカトリスステーキ
                    os.writeC(39);
                    os.writeS("近距離命中+2、近距離ダメージ+1");
                    break;
                case 210056: // 幻想クラスタシアンニッパー焼き
                    os.writeC(39);
                    os.writeS("遠距離命中+2、遠距離ダメージ+1");
                    break;
                case 210048: // クラスタシアンニッパー焼き
                    os.writeC(39);
                    os.writeS("遠距離命中+2、遠距離ダメージ+1");
                    break;

                case 3000159://スープ
                    os.writeC(39);
                    os.writeS("経験値獲得量+ 10％");
                    os.writeC(39);
                    os.writeS("恐怖、魔法、スタン抵抗+1");
                    break;
                case 3000160://メティスの料理
                    os.writeC(39);
                    os.writeS("根、遠距離ダメージ+3");
                    os.writeC(39);
                    os.writeS("根、遠距離命中+3");
                    os.writeC(39);
                    os.writeS("SP +3");
                    os.writeC(39);
                    os.writeS("根、遠距離クリティカル+3");
                    break;
                case 3000161://メティスのスクロール
                    os.writeC(39);
                    os.writeS("\\f2メティスのプルアップ");
                    break;
                /** 属性矢 **/
                case 820014:
                    os.writeC(39);
                    os.writeS("水属性ダメージ+3");
                    break;
                case 820015:
                    os.writeC(39);
                    os.writeS("風属性ダメージ+3");
                    break;
                case 820016:
                    os.writeC(39);
                    os.writeS("地属性ダメージ+3");
                    break;
                case 820017:
                    os.writeC(39);
                    os.writeS("火属性ダメージ+3");
                    break;
                /** 属性矢 **/
                default:
                    break;
            }

        } else if (itemType2 == 1 || itemType2 == 2) { // weapon | armor
            int op_addAc = 0;
            /** アイテム安全なチャン表示を追加 **/
			/*int SafeEnchant = getItem().get_safeenchant();
			os.writeC(39);
			if (SafeEnchant < 0) {
				SafeEnchant = 0;
			}
			os.writeS("\\fY[安全なチャン : +" + SafeEnchant + "]");*/

            if (itemType2 == 1) { // weapon武器打撃値
                os.writeC(1);
                os.writeC(getItem().getDmgSmall());
                os.writeC(getItem().getDmgLarge());
                os.writeC(getItem().getMaterial());
                os.writeD(getWeight());
            } else if (itemType2 == 2) { // armor
                // AC
                os.writeC(19);
                int ac = ((L1Armor) getItem()).get_ac();
                int Grade = ((L1Armor) getItem()).getGrade();
                if (ac < 0) {
                    ac = ac - ac - ac;
                }
                os.writeC(ac - get_durability());
                os.writeC(getItem().getMaterial());
                os.writeC(-1);
                os.writeD(getWeight());
            }
            if (getEnchantLevel() != 0 && !(itemType2 == 2 && getItem().getGrade() >= 0)) {
                os.writeC(2);
                os.writeC(getEnchantLevel());

                /** ルームティス黒い光ピアスAC表現処理部 **/
            } else if (itemType2 == 2 && itemId == 222340 || itemId == 222341) {
                os.writeC(2);
                switch (getEnchantLevel()) {
                    case 1:
                        os.writeC(1 + op_addAc);
                        break;
                    case 2:
                        os.writeC(2 + op_addAc);
                        break;
                    case 3:
                        if (itemId == 222341) {
                            os.writeC(4 + op_addAc);
                        } else {
                            os.writeC(3 + op_addAc);
                        }
                        break;
                    case 4:
                        if (itemId == 222341) {
                            os.writeC(6 + op_addAc);
                        } else {
                            os.writeC(5 + op_addAc);
                        }
                        break;
                    case 5:
                        if (itemId == 222341) {
                            os.writeC(7 + op_addAc);
                        } else {
                            os.writeC(6 + op_addAc);
                        }
                        break;
                    case 6:
                        if (itemId == 222341) {
                            os.writeC(8 + op_addAc);
                        } else {
                            os.writeC(7 + op_addAc);
                        }
                        break;
                    case 7:
                        if (itemId == 222341) {
                            os.writeC(9 + op_addAc);
                        } else {
                            os.writeC(8 + op_addAc);
                        }
                        break;
                    case 8:
                        if (itemId == 222341) {
                            os.writeC(10 + op_addAc);
                        } else {
                            os.writeC(9 + op_addAc);
                        }
                        break;
                    default:
                        os.writeC(0 + op_addAc);
                }

                /** スナップファーリングACセクションの処理 **/
            } else if (itemType2 == 2 && itemId >= 22224 && itemId <= 22228 || itemId == 222290) {
                os.writeC(2);
                switch (getEnchantLevel()) {
                    case 2:
                        os.writeC(1 + op_addAc);
                        break;
                    case 3:
                        os.writeC(2 + op_addAc);
                        break;
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                        os.writeC(3 + op_addAc);
                        break;
                    default:
                        os.writeC(0 + op_addAc);
                }
                /** スナップファーの勇士リングACセクションの処理 **/
            } else if (itemType2 == 2 && itemId == 222291) {
                os.writeC(2);
                switch (getEnchantLevel()) {
                    case 1:
                        os.writeC(1 + op_addAc);
                        break;
                    case 2:
                        os.writeC(2 + op_addAc);
                        break;
                    case 3:
                        os.writeC(3 + op_addAc);
                        break;
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                        os.writeC(4 + op_addAc);
                        break;
                    default:
                        os.writeC(0 + op_addAc);
                }
                /** 祝福されたスナップパー体力、魔法抵抗リングACセクションの処理 **/
            } else if (itemType2 == 2 && itemId == 222332 || itemId == 222334 || itemId == 222335) {
                os.writeC(2);
                switch (getEnchantLevel()) {
                    case 2:
                        os.writeC(1 + op_addAc);
                        break;
                    case 3:
                        os.writeC(2 + op_addAc);
                        break;
                    case 4:
                    case 5:
                    case 6:
                        os.writeC(3 + op_addAc);
                        break;
                    case 7:
                    case 8:
                        os.writeC(4 + op_addAc);
                        break;
                    default:
                        os.writeC(0 + op_addAc);
                }
                /** 祝福されたスナップファーの回復、集中、マナリングACセクションの処理 **/
            } else if (itemType2 == 2 && itemId == 222330 || itemId == 222331 || itemId == 222333) {
                os.writeC(2);
                switch (getEnchantLevel()) {
                    case 2:
                        os.writeC(1 + op_addAc);
                        break;
                    case 3:
                        os.writeC(2 + op_addAc);
                        break;
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                        os.writeC(3 + op_addAc);
                        break;
                    default:
                        os.writeC(0 + op_addAc);
                }
                /** 祝福されたスナッパー勇士のリングACセクションの処理 **/
            } else if (itemType2 == 2 && itemId == 222336) {
                os.writeC(2);
                switch (getEnchantLevel()) {
                    case 1:
                        os.writeC(1 + op_addAc);
                        break;
                    case 2:
                        os.writeC(2 + op_addAc);
                        break;
                    case 3:
                        os.writeC(3 + op_addAc);
                        break;
                    case 4:
                    case 5:
                    case 6:
                        os.writeC(4 + op_addAc);
                        break;
                    case 7:
                    case 8:
                        os.writeC(5 + op_addAc);
                        break;
                    default:
                        os.writeC(0 + op_addAc);
                }
                /** ルームティスの青い光のイヤリングACセクションの処理**/
            } else if (itemType2 == 2 && getItem().getGrade() == 4 && itemId == 22230) {
                os.writeC(2);
                switch (getEnchantLevel()) {
                    case 5:
                        os.writeC(1 + op_addAc);
                        break;
                    case 6:
                    case 7:
                        os.writeC(2 + op_addAc);
                        break;
                    case 8:
                        os.writeC(3 + op_addAc);
                        break;
                    default:
                        os.writeC(0 + op_addAc);
                }
                /** 祝福されたルームティスの青い光のイヤリングACセクションの処理 **/
            } else if (itemType2 == 2 && getItem().getGrade() == 4 && itemId == 222338) {
                os.writeC(2);
                switch (getEnchantLevel()) {
                    case 4:
                        os.writeC(1 + op_addAc);
                        break;
                    case 5:
                    case 6:
                        os.writeC(2 + op_addAc);
                        break;
                    case 7:
                        os.writeC(3 + op_addAc);
                        break;
                    case 8:
                        os.writeC(4 + op_addAc);
                        break;
                    default:
                        os.writeC(0 + op_addAc);
                }

            } else if (itemType2 == 2 && (itemId == 900032 || itemId == 900033 || itemId == 900034)) { //記章
                os.writeC(2);
                switch (getEnchantLevel()) {
                    case 4:
                        os.writeC(1);
                        break;
                    case 5:
                        os.writeC(2);
                        break;
                    case 6:
                    case 7:
                    case 8:
                        os.writeC(3);
                        break;
                    default:
                        os.writeC(0);
                        break;
                }
            } else if (itemType2 == 2 && itemId == 900035) { //記章
                os.writeC(2);
                switch (getEnchantLevel()) {
                    case 3:
                        os.writeC(1);
                        break;
                    case 4:
                        os.writeC(2);
                        break;
                    case 5:
                        os.writeC(3);
                        break;
                    case 6:
                        os.writeC(5);
                        break;
                    case 7:
                        os.writeC(6);
                        break;
                    case 8:
                        os.writeC(7);
                        break;
                    default:
                        os.writeC(0);
                        break;
                }
            }


            if (getItem().isTwohandedWeapon()) { // 両手武器
                os.writeC(4);
            }

            if (get_durability() != 0) { // 損傷も
                os.writeC(3);
                os.writeC(get_durability());
                // os.writeC(39);
                // os.writeS("\\aG損傷も「+ get_durability（））;
            }

            /** クラス着用部分 **/
            int bit = 0;
            bit |= getItem().isUseRoyal() ? 1 : 0;
            bit |= getItem().isUseKnight() ? 2 : 0;
            bit |= getItem().isUseElf() ? 4 : 0;
            bit |= getItem().isUseMage() ? 8 : 0;
            bit |= getItem().isUseDarkelf() ? 16 : 0;
            bit |= getItem().isUseDragonKnight() ? 32 : 0;
            bit |= getItem().isUseBlackwizard() ? 64 : 0;
            bit |= getItem().isUseWarrior() ? 128 : 0;
            os.writeC(7);
            os.writeC(bit);

            if (getSpecialEnchant() != 0) {
                if (itemType2 == 1 && getSpecialEnchant() == 1) {
                    os.writeC(39);
                    os.writeS("\\f2特化：追加ダメージ+5");
                }
                if (itemType2 == 2 && getSpecialEnchant() == 1 && (!(getItem().getType() >= 8 && getItem().getType() <= 12))) {
                    os.writeC(39);
                    os.writeS("\\f2特化：ダメージリダクション+2");
                }
            }

            /** 55レベルエリクサールーンオプションを表示 **/
            if (itemId == 222295) { // アジャイルのエリクサー
                switch (_cha.getType()) {
                    case 0:
                        os.writeC(39);
                        os.writeS("ダメージリダクション+3");
                        break;
                    case 1:
                    case 7:
                        os.writeC(39);
                        os.writeS("HP +50");
                        break;
                    case 2:
                        os.writeC(39);
                        os.writeS("MP +50");
                        break;
                    case 3:
                        os.writeC(39);
                        os.writeS("MP回復+3");
                        break;
                    case 4:
                        os.writeC(39);
                        os.writeS("AC-3");
                        break;
                    case 5:
                        os.writeC(39);
                        os.writeS("近距離命中+3");
                        break;
                    case 6:
                        os.writeC(39);
                        os.writeS("重量ゲージ+ 5％");
                        break;
                }
            }
            if (itemId == 222296) { // 体力のエリクサー
                switch (_cha.getType()) {
                    case 0:
                        os.writeC(39);
                        os.writeS("ダメージリダクション+3");
                        break;
                    case 1:
                    case 7:
                        os.writeC(39);
                        os.writeS("HP +50");
                        break;
                    case 2:
                        os.writeC(39);
                        os.writeS("MP +50");
                        break;
                    case 3:
                        os.writeC(39);
                        os.writeS("MP回復+3");
                        break;
                    case 4:
                        os.writeC(39);
                        os.writeS("AC-3");
                        break;
                    case 5:
                        os.writeC(39);
                        os.writeS("近距離命中+3");
                        break;
                    case 6:
                        os.writeC(39);
                        os.writeS("重量ゲージ+ 5％");
                        break;
                }
            }
            if (itemId == 222297) { // 知識のエリクサー
                switch (_cha.getType()) {
                    case 0:
                        os.writeC(39);
                        os.writeS("ダメージリダクション+3");
                        break;
                    case 1:
                    case 7:
                        os.writeC(39);
                        os.writeS("HP +50");
                        break;
                    case 2:
                        os.writeC(39);
                        os.writeS("MP +50");
                        break;
                    case 3:
                        os.writeC(39);
                        os.writeS("MP回復+3");
                        break;
                    case 4:
                        os.writeC(39);
                        os.writeS("AC-3");
                        break;
                    case 5:
                        os.writeC(39);
                        os.writeS("近距離命中+3");
                        break;
                    case 6:
                        os.writeC(39);
                        os.writeS("重量ゲージ+ 5％");
                        break;
                }
            }
            if (itemId == 222298) { // 知恵のエリクサー
                switch (_cha.getType()) {
                    case 0:
                        os.writeC(39);
                        os.writeS("ダメージリダクション+3");
                        break;
                    case 1:
                    case 7:
                        os.writeC(39);
                        os.writeS("HP +50");
                        break;
                    case 2:
                        os.writeC(39);
                        os.writeS("MP +50");
                        break;
                    case 3:
                        os.writeC(39);
                        os.writeS("MP回復+3");
                        break;
                    case 4:
                        os.writeC(39);
                        os.writeS("AC-3");
                        break;
                    case 5:
                        os.writeC(39);
                        os.writeS("近距離命中+3");
                        break;
                    case 6:
                        os.writeC(39);
                        os.writeS("重量ゲージ+ 5％");
                        break;
                }
            }
            if (itemId == 222299) { //力のエリクサー
                switch (_cha.getType()) {
                    case 0:
                        os.writeC(39);
                        os.writeS("ダメージリダクション+3");
                        break;
                    case 1:
                    case 7:
                        os.writeC(39);
                        os.writeS("HP +50");
                        break;
                    case 2:
                        os.writeC(39);
                        os.writeS("MP +50");
                        break;
                    case 3:
                        os.writeC(39);
                        os.writeS("MP回復+3");
                        break;
                    case 4:
                        os.writeC(39);
                        os.writeS("AC-3");
                        break;
                    case 5:
                        os.writeC(39);
                        os.writeS("近距離命中+3");
                        break;
                    case 6:
                        os.writeC(39);
                        os.writeS("重量ゲージ+ 5％");
                        break;
                }
            }

            /** 70レベルエリクサールーンオプションを表示 **/
            if (itemId == 222312) { // アジャイルのエリクサー
                switch (_cha.getType()) {
                    case 0:
                        os.writeC(39);
                        os.writeS("ダメージリダクション+3");
                        os.writeC(39);
                        os.writeS("近距離命中+2");
                        break;
                    case 1:
                        os.writeC(39);
                        os.writeS("最大HP + 50");
                        os.writeC(39);
                        os.writeS("近距離ダメージ+1");
                        break;
                    case 2:
                        os.writeC(39);
                        os.writeS("最大MP + 50");
                        os.writeC(39);
                        os.writeS("遠距離ダメージ+1");
                        break;
                    case 3:
                        os.writeC(39);
                        os.writeS("MP回復+3");
                        os.writeC(39);
                        os.writeS("SP +1");
                        break;
                    case 4:
                        os.writeC(39);
                        os.writeS("AC-3");
                        os.writeC(39);
                        os.writeS("MP +30");
                        break;
                    case 5:
                        os.writeC(39);
                        os.writeS("近距離命中+3");
                        os.writeC(39);
                        os.writeS("ダメージリダクション+1");
                        break;
                    case 6:
                        os.writeC(39);
                        os.writeS("重量ゲージ+ 5％");
                        os.writeC(39);
                        os.writeS("HP +50");
                        break;
                    case 7:
                        os.writeC(39);
                        os.writeS("MR +5");
                        os.writeC(39);
                        os.writeS("HP +50");
                        break;
                }
            }
            if (itemId == 222313) { // 体力のエリクサー
                switch (_cha.getType()) {
                    case 0:
                        os.writeC(39);
                        os.writeS("ダメージリダクション+3");
                        os.writeC(39);
                        os.writeS("近距離命中+2");
                        break;
                    case 1:
                        os.writeC(39);
                        os.writeS("最大HP + 50");
                        os.writeC(39);
                        os.writeS("近距離ダメージ+1");
                        break;
                    case 2:
                        os.writeC(39);
                        os.writeS("最大MP + 50");
                        os.writeC(39);
                        os.writeS("遠距離ダメージ+1");
                        break;
                    case 3:
                        os.writeC(39);
                        os.writeS("MP回復+3");
                        os.writeC(39);
                        os.writeS("SP +1");
                        break;
                    case 4:
                        os.writeC(39);
                        os.writeS("AC-3");
                        os.writeC(39);
                        os.writeS("MP +30");
                        break;
                    case 5:
                        os.writeC(39);
                        os.writeS("近距離命中+3");
                        os.writeC(39);
                        os.writeS("ダメージリダクション+1");
                        break;
                    case 6:
                        os.writeC(39);
                        os.writeS("重量ゲージ+ 5％");
                        os.writeC(39);
                        os.writeS("HP +50");
                        break;
                    case 7:
                        os.writeC(39);
                        os.writeS("MR +5");
                        os.writeC(39);
                        os.writeS("HP +50");
                        break;
                }
            }
            if (itemId == 222314) { // 知識のエリクサー
                switch (_cha.getType()) {
                    case 0:
                        os.writeC(39);
                        os.writeS("ダメージリダクション+3");
                        os.writeC(39);
                        os.writeS("近距離命中+2");
                        break;
                    case 1:
                        os.writeC(39);
                        os.writeS("最大HP + 50");
                        os.writeC(39);
                        os.writeS("近距離ダメージ+1");
                        break;
                    case 2:
                        os.writeC(39);
                        os.writeS("最大MP + 50");
                        os.writeC(39);
                        os.writeS("遠距離ダメージ+1");
                        break;
                    case 3:
                        os.writeC(39);
                        os.writeS("MP回復+3");
                        os.writeC(39);
                        os.writeS("SP +1");
                        break;
                    case 4:
                        os.writeC(39);
                        os.writeS("AC-3");
                        os.writeC(39);
                        os.writeS("MP +30");
                        break;
                    case 5:
                        os.writeC(39);
                        os.writeS("近距離命中+3");
                        os.writeC(39);
                        os.writeS("ダメージリダクション+1");
                        break;
                    case 6:
                        os.writeC(39);
                        os.writeS("重量ゲージ+ 5％");
                        os.writeC(39);
                        os.writeS("HP +50");
                        break;
                    case 7:
                        os.writeC(39);
                        os.writeS("MR +5");
                        os.writeC(39);
                        os.writeS("HP +50");
                        break;
                }
            }
            if (itemId == 222315) { // 知恵のエリクサー
                switch (_cha.getType()) {
                    case 0:
                        os.writeC(39);
                        os.writeS("ダメージリダクション+3");
                        os.writeC(39);
                        os.writeS("近距離命中+2");
                        break;
                    case 1:
                        os.writeC(39);
                        os.writeS("最大HP + 50");
                        os.writeC(39);
                        os.writeS("近距離ダメージ+1");
                        break;
                    case 2:
                        os.writeC(39);
                        os.writeS("最大MP + 50");
                        os.writeC(39);
                        os.writeS("遠距離ダメージ+1");
                        break;
                    case 3:
                        os.writeC(39);
                        os.writeS("MP回復+3");
                        os.writeC(39);
                        os.writeS("SP +1");
                        break;
                    case 4:
                        os.writeC(39);
                        os.writeS("AC-3");
                        os.writeC(39);
                        os.writeS("MP +30");
                        break;
                    case 5:
                        os.writeC(39);
                        os.writeS("近距離命中+3");
                        os.writeC(39);
                        os.writeS("ダメージリダクション+1");
                        break;
                    case 6:
                        os.writeC(39);
                        os.writeS("重量ゲージ+ 5％");
                        os.writeC(39);
                        os.writeS("HP +50");
                        break;
                    case 7:
                        os.writeC(39);
                        os.writeS("MR +5");
                        os.writeC(39);
                        os.writeS("HP +50");
                        break;
                }
            }
            if (itemId == 222316) { //力のエリクサー
                switch (_cha.getType()) {
                    case 0:
                        os.writeC(39);
                        os.writeS("ダメージリダクション+3");
                        os.writeC(39);
                        os.writeS("近距離命中+2");
                        break;
                    case 1:
                        os.writeC(39);
                        os.writeS("最大HP + 50");
                        os.writeC(39);
                        os.writeS("近距離ダメージ+1");
                        break;
                    case 2:
                        os.writeC(39);
                        os.writeS("最大MP + 50");
                        os.writeC(39);
                        os.writeS("遠距離ダメージ+1");
                        break;
                    case 3:
                        os.writeC(39);
                        os.writeS("MP回復+3");
                        os.writeC(39);
                        os.writeS("SP +1");
                        break;
                    case 4:
                        os.writeC(39);
                        os.writeS("AC-3");
                        os.writeC(39);
                        os.writeS("MP +30");
                        break;
                    case 5:
                        os.writeC(39);
                        os.writeS("近距離命中+3");
                        os.writeC(39);
                        os.writeS("ダメージリダクション+1");
                        break;
                    case 6:
                        os.writeC(39);
                        os.writeS("重量ゲージ+ 5％");
                        os.writeC(39);
                        os.writeS("HP +50");
                        break;
                    case 7:
                        os.writeC(39);
                        os.writeS("MR +5");
                        os.writeC(39);
                        os.writeS("HP +50");
                        break;
                }
            }

            /** スナップファーリングツタ表記 **/
            if (itemType2 == 2 && itemId >= 22224 && itemId <= 22228 && getEnchantLevel() > 4 || itemId == 222291 && getEnchantLevel() > 4) {
                os.writeC(6);
                os.writeC(getItem().getDmgModifier() + getEnchantLevel() - 4);
                /**祝福されたスナップパーリングツタ表記**/
            } else if (itemType2 == 2 && itemId >= 222330 && itemId <= 222334 && getEnchantLevel() > 3 || itemId == 222336 && getEnchantLevel() > 3) {
                os.writeC(6);
                os.writeC(getItem().getDmgModifier() + getEnchantLevel() - 3);
            } else if (getItem().getDmgModifier() != 0) {
                os.writeC(6);
                os.writeC(getItem().getDmgModifier());
            }
            /** スナップファーの勇士リング武器命中 **/
            if (itemType2 == 2 && itemId == 222291 && getEnchantLevel() > 4) {
                os.writeC(5);
                os.writeC(getItem().getHitModifier() + getEnchantLevel() - 4);
                /** 祝福されたスナップ広がりの勇士リング武器命中 **/
            } else if (itemType2 == 2 && itemId == 222336 && getEnchantLevel() > 3) {
                os.writeC(5);
                os.writeC(getItem().getHitModifier() + getEnchantLevel() - 3);
                /*** 激怒の手袋武器命中***/
            } else if (itemType2 == 2 && itemId == 222317 && getEnchantLevel() >= 4 && getEnchantLevel() <= 6) {
                os.writeC(5);
                os.writeC(getItem().getHitModifier() + getEnchantLevel() - 4);
            } else if (itemType2 == 2 && itemId == 222317 && getEnchantLevel() > 6) {
                os.writeC(5);
                os.writeC(getItem().getHitModifier() + getEnchantLevel() - 3);
                /** シールドウィングのパワーグローブ近距離命中**/
            } else if (itemType2 == 2 && itemId == 222345 && getEnchantLevel() > 4) {
                os.writeC(5);
                os.writeC(getItem().getHitModifier() + getEnchantLevel() - 4);
            } else if (getItem().getHitRate() != 0) { // 防具に付く衝突
                os.writeC(5);
                os.writeC(getItem().getHitRate());
            } else if (getItem().getHitModifier() != 0) { //武器につく命中
                if (itemType2 == 1 && getItem().getType1() != 20) {
                    os.writeC(5);
                    os.writeC(getItem().getHitModifier());
                } else {
                    os.writeC(24);
                    os.writeC(getItem().getHitModifier());
                }
            }

            if (getItem().getDmgRate() != 0) {
                os.writeC(6);// 追加ダメージ
                os.writeC(getItem().getDmgRate());
            }
            /** シールドウィングのブレイサー遠距離命中 **/
            if (itemType2 == 2 && itemId == 222343 && getEnchantLevel() > 4) {
                os.writeC(24);
                os.writeC(getItem().getBowHitRate() + getEnchantLevel() - 4);
            } else if (getItem().getBowHitRate() != 0) {
                os.writeC(24);
                os.writeC(getItem().getBowHitRate());
            }
            if (getItem().getBowDmgRate() != 0) {
                os.writeC(35);
                os.writeC(getItem().getBowDmgRate());
            }

            /** 火竜のTシャツ **/
            if (itemType2 == 2 && itemId == 491006) {
                os.writeC(6); //追加ダメージ
                if (getEnchantLevel() >= 9)
                    os.writeC(2);
                else
                    os.writeC(1);
            }

            /** リング5以上追加ダメージ **/
            if (itemType2 == 2 && getItem().getGrade() >= 0 && getItem().getGrade() <= 2 && getItem().getAccessoryProcess() == 9 || getItem().getAccessoryProcess() == 11) {
                if (getEnchantLevel() > 4) {
                    os.writeC(39);
                    os.writeS("近距離ダメージ+" + (getEnchantLevel() - 4));
                    os.writeC(39);
                    os.writeS("遠距離ダメージ+" + (getEnchantLevel() - 4));
                }
            }

            /** リング7以上のオプションスペルパワーリニューアル **/
            if (itemType2 == 2 && getItem().getGrade() >= 0 && getItem().getGrade() <= 2 && getItem().getAccessoryProcess() == 9 || getItem().getAccessoryProcess() == 11) {
                if (getEnchantLevel() >= 7) {
                    os.writeC(17);
                    os.writeC(getEnchantLevel() - 6);
                }
            }

            /** スナップファーリングPvP追加ダメージ表示（現全体と一般ユーザーの格差のために、実際にダメージは入っていない） **/
            if (itemType2 == 2 && getItem().getGrade() >= 0 && getItem().getGrade() <= 2 && getItem().getAccessoryProcess() == 9 || getItem().getAccessoryProcess() == 11 && getEnchantLevel() > 6) {
                if (getEnchantLevel() == 7) {
                    os.writeC(59);
                    os.writeC(getEnchantLevel() - 6);
                } else if (getEnchantLevel() == 8) {
                    os.writeC(59);
                    os.writeC(getEnchantLevel() - 6);
                } else if (getEnchantLevel() == 9) {
                    os.writeC(59);
                    os.writeC(getEnchantLevel() - 6);
                }

            } else if (itemType2 == 2 && itemId >= 22224 && itemId <= 22228 && getEnchantLevel() == 7
                    || itemId >= 222290 && itemId <= 222291 && getEnchantLevel() == 7
                    || itemId >= 222330 && itemId <= 222336 && getEnchantLevel() == 7) { // エンチャントが7であれば
                os.writeC(59);
                os.writeC(getEnchantLevel() - 6);
                // os.writeC(39);
                // os.writeS("PvP追加ダメージ+1 "）;
            } else if (itemType2 == 2 && itemId >= 22224 && itemId <= 22228 && getEnchantLevel() == 8
                    || itemId >= 222290 && itemId <= 222291 && getEnchantLevel() == 8
                    || itemId >= 222330 && itemId <= 222336 && getEnchantLevel() == 8) { // エンチャントが8であれば
                os.writeC(59);
                os.writeC(getEnchantLevel() - 6);
                // os.writeC(39);
                // os.writeS("PvP 追加ダメージ+2 "）;
            } else if (itemId >= 307 && itemId <= 314) {
                if (getEnchantLevel() == 7) {
                    os.writeC(59);
                    os.writeC(getEnchantLevel() - 4);
                } else if (getEnchantLevel() == 8) {
                    os.writeC(59);
                    os.writeC(getEnchantLevel() - 3);
                } else if (getEnchantLevel() == 9) {
                    os.writeC(59);
                    os.writeC(getEnchantLevel() - 2);
                } else if (getEnchantLevel() == 10) {
                    os.writeC(59);
                    os.writeC(getEnchantLevel());
                }
            }


            // STR~CHA
            if (getItem().get_addstr() != 0) {
                os.writeC(8);
                os.writeC(getItem().get_addstr());
            }
            if (getItem().get_adddex() != 0) {
                os.writeC(9);
                os.writeC(getItem().get_adddex());
            }
            if (getItem().get_addcon() != 0) {
                os.writeC(10);
                os.writeC(getItem().get_addcon());
            }
            if (getItem().get_addwis() != 0) {
                os.writeC(11);
                os.writeC(getItem().get_addwis());
            }
            if (getItem().get_addint() != 0) {
                os.writeC(12);
                os.writeC(getItem().get_addint());
            }
            if (getItem().get_addcha() != 0) {
                os.writeC(13);
                os.writeC(getItem().get_addcha());
            }

            /** スナップファーリング類HP増加表示 **/
            if (itemType2 == 2 && itemId >= 22224 && itemId <= 22228 && getEnchantLevel() > 0) {
                int snapperHpUp = getEnchantLevel() * 5 + 10;
                os.writeC(14);
                os.writeH(getItem().get_addhp() + snapperHpUp);

                /** 祝福されたスナップ広がりの体力リングHP増加表示 **/
            } else if (itemType2 == 2 && itemId == 222332 && getEnchantLevel() > 0) {
                switch (getEnchantLevel()) {
                    case 1:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 15);
                        break;
                    case 2:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 20);
                        break;
                    case 3:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 30);
                        break;
                    case 4:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 35);
                        break;
                    case 5:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 40);
                        break;
                    case 6:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 45);
                        break;
                    case 7:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 55);
                        break;
                    case 8:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 65);
                        break;
                    default:
                        break;
                }
                /**祝福されたスナップ広がりの魔法抵抗力、フォーカス、マナリングHP増加表示 **/
            } else if (itemType2 == 2 && itemId >= 222330 && itemId <= 222331 || itemId >= 222333 && itemId <= 222334 && getEnchantLevel() > 0) {
                switch (getEnchantLevel()) {
                    case 1:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 15);
                        break;
                    case 2:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 20);
                        break;
                    case 3:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 30);
                        break;
                    case 4:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 35);
                        break;
                    case 5:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 40);
                        break;
                    case 6:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 45);
                        break;
                    case 7:
                    case 8:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 50);
                        break;
                    default:
                        break;
                }
                /** スナップファーの知恵リングHP増加表示 **/
            } else if (itemType2 == 2 && itemId == 222290 && getEnchantLevel() > 0) {
                int wis_ringHpUp = (getEnchantLevel() * 5);
                os.writeC(14);
                os.writeH(getItem().get_addhp() + wis_ringHpUp);
                /** 祝福されたスナップ広がりの知恵リングHP増加表示 **/
            } else if (itemType2 == 2 && itemId == 222335 && getEnchantLevel() > 0) {
                switch (getEnchantLevel()) {
                    case 1:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 5);
                        break;
                    case 2:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 10);
                        break;
                    case 3:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 20);
                        break;
                    case 4:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 25);
                        break;
                    case 5:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 30);
                        break;
                    case 6:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 35);
                        break;
                    case 7:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 40);
                        break;
                    case 8:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 50);
                        break;
                    default:
                        break;
                }
                /** スナップファーの勇士リングHP増加表示 **/
            } else if (itemType2 == 2 && itemId == 222291 && getEnchantLevel() >= 3) {
                int brave_ringHpUP = (getEnchantLevel() - 2) * 5;
                os.writeC(14);
                os.writeH(getItem().get_addhp() + brave_ringHpUP);
                /** 祝福されたスナップ広がりの勇士リングHP増加表示**/
            } else if (itemType2 == 2 && itemId == 222336 && getEnchantLevel() > 2) {
                switch (getEnchantLevel()) {
                    case 3:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 10);
                        break;
                    case 4:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 15);
                        break;
                    case 5:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 20);
                        break;
                    case 6:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 25);
                        break;
                    case 7:
                    case 8:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 30);
                        break;
                    default:
                        break;
                }
                /** ルームティス赤い光のイヤリングHP増加表示 **/
            } else if (itemType2 == 2 && itemId == 22229 && getEnchantLevel() > 0) {
                int b_roomtisRingHPUp = (getEnchantLevel() * 10) + 10;
                os.writeC(14);
                os.writeH(getItem().get_addhp() + b_roomtisRingHPUp);
                /** 祝福されたルームティス赤い光のイヤリングHP増加表示**/
            } else if (itemType2 == 2 && itemId == 222337 && getEnchantLevel() > 0) {
                switch (getEnchantLevel()) {
                    case 1:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 20);
                        break;
                    case 2:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 30);
                        break;
                    case 3:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 50);
                        break;
                    case 4:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 60);
                        break;
                    case 5:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 70);
                        break;
                    case 6:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 80);
                        break;
                    case 7:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 90);
                        break;
                    case 8:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 140);
                        break;
                    default:
                        break;
                }
                /** 体力のガーダーHP増加表示 **/
            } else if (itemType2 == 2 && itemId == 22256 && getEnchantLevel() >= 5) {
                switch (getEnchantLevel()) {
                    case 5:
                    case 6:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 25);
                        break;
                    case 7:
                    case 8:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 50);
                        break;
                    case 9:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 75);
                        break;
                    default:
                        break;
                }
                /** リングピアスネックレスエンチャントHP増加表示**/
            } else if (itemType2 == 2 && getItem().getGrade() >= 0 && getItem().getGrade() <= 2 && getItem().getAccessoryProcess() != 10 && getEnchantLevel() > 0) {
                switch (getEnchantLevel()) {
                    case 1:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 5);
                        break;
                    case 2:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 10);
                        break;
                    case 3:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 20);
                        break;
                    case 4:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 30);
                        break;
                    case 5:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 40);
                        break;
                    case 6:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 40);
                        break;
                    case 7:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 50);
                        break;
                    case 8:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 50);
                        break;
                    case 9:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 60);
                        break;
                    default:
                        break;
                }
                /** ベルト6からHP表示 **/
            } else if (itemType2 == 2 && (getItem().getType() == 10 && getEnchantLevel() > 5)) {//ベルト
                switch (getEnchantLevel()) {
                    case 6:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 20);
                        break;
                    case 7:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 30);
                        break;
                    case 8:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 40);
                        break;
                    case 9:
                        os.writeC(14);
                        os.writeH(getItem().get_addhp() + 60);
                        break;
                    default:
                        break;
                }
            } else if (getItem().get_addhp() != 0) {
                os.writeC(14);
                os.writeH(getItem().get_addhp());
            }
            //ユニゲートル
            if (itemType2 == 2 && getEnchantLevel() >= 9 && (itemId >= 900027 && itemId <= 900029)) {
                switch (itemId) {
                    case 900027://アジャイル
                        os.writeC(35);//遠距離
                        os.writeC(1);
                        break;
                    case 900028://腕力
                        os.writeC(47);//近距離
                        os.writeC(1);
                        break;
                    case 900029://知識
                        os.writeC(17); //SP
                        os.writeC(1);
                        break;
                }
            }

            /** ベルトエンチャントMP増加表示 **/
            if (itemType2 == 2 && getItem().getType() == 10 && getEnchantLevel() > 0) {
                switch (getEnchantLevel()) {
                    case 1:
                        os.writeC(32);
                        os.writeH(getItem().get_addmp() + 5);
                        break;
                    case 2:
                        os.writeC(32);
                        os.writeH(getItem().get_addmp() + 10);
                        break;
                    case 3:
                        os.writeC(32);
                        os.writeH(getItem().get_addmp() + 20);
                        break;
                    case 4:
                        os.writeC(32);
                        os.writeH(getItem().get_addmp() + 30);
                        break;
                    case 5:
                        os.writeC(32);
                        os.writeH(getItem().get_addmp() + 40);
                        break;
                    case 6:
                        os.writeC(32);
                        os.writeH(getItem().get_addmp() + 40);
                        break;
                    case 7:
                        os.writeC(32);
                        os.writeH(getItem().get_addmp() + 50);
                        break;
                    case 8:
                        os.writeC(32);
                        os.writeH(getItem().get_addmp() + 50);
                        break;
                    case 9:
                        os.writeC(32);
                        os.writeH(getItem().get_addmp() + 60);
                        break;
                    default:
                        break;
                }
                /** ルームティスの紫色のイヤリングMP増加表示 **/
            } else if (itemType2 == 2 && itemId == 22231 && getEnchantLevel() > 0) {
                switch (getEnchantLevel()) {
                    case 1:
                        os.writeC(32);
                        os.writeH(getItem().get_addmp() + 10);
                        break;
                    case 2:
                        os.writeC(32);
                        os.writeH(getItem().get_addmp() + 15);
                        break;
                    case 3:
                        os.writeC(32);
                        os.writeH(getItem().get_addmp() + 30);
                        break;
                    case 4:
                        os.writeC(32);
                        os.writeH(getItem().get_addmp() + 35);
                        break;
                    case 5:
                        os.writeC(32);
                        os.writeH(getItem().get_addmp() + 50);
                        break;
                    case 6:
                        os.writeC(32);
                        os.writeH(getItem().get_addmp() + 55);
                        break;
                    case 7:
                        os.writeC(32);
                        os.writeH(getItem().get_addmp() + 70);
                        break;
                    case 8:
                        os.writeC(32);
                        os.writeH(getItem().get_addmp() + 95);
                        break;
                    default:
                        break;
                }
                /** 祝福されたルームティスの紫色のイヤリングMP増加表示 **/
            } else if (itemType2 == 2 && itemId == 222339 && getEnchantLevel() > 0) {
                switch (getEnchantLevel()) {
                    case 1:
                        os.writeC(32);
                        os.writeH(getItem().get_addmp() + 10);
                        break;
                    case 2:
                        os.writeC(32);
                        os.writeH(getItem().get_addmp() + 15);
                        break;
                    case 3:
                        os.writeC(32);
                        os.writeH(getItem().get_addmp() + 35);
                        break;
                    case 4:
                        os.writeC(32);
                        os.writeH(getItem().get_addmp() + 50);
                        break;
                    case 5:
                        os.writeC(32);
                        os.writeH(getItem().get_addmp() + 55);
                        break;
                    case 6:
                        os.writeC(32);
                        os.writeH(getItem().get_addmp() + 70);
                        break;
                    case 7:
                        os.writeC(32);
                        os.writeH(getItem().get_addmp() + 95);
                        break;
                    case 8:
                        os.writeC(39);
                        os.writeS("最大MP + 130");
                        break;
                    default:
                        break;
                }
                /** 大魔法使いの帽子エンチャントMP増加表示 **/
            } else if (itemId == 202022) {
                os.writeC(32);
                os.writeH(getItem().get_addmp() + (getEnchantLevel() * 10));
                /** MPパケット変更C / H **/
            } else if (addmp() != 0) {
                os.writeC(32);
                os.writeH(addmp());// mp部分パケット変更
            }

            // HPR表示
            if (getItem().get_addhpr() != 0) {
                os.writeC(37);
                os.writeC(getItem().get_addhpr());
            }

            // MPR表示
            if (itemId == 1134 || itemId == 101134) {
                os.writeC(38);
                os.writeC(getItem().get_addmpr() + getEnchantLevel()); // 瞑想の杖
            } else if (getItem().get_addmpr() != 0) {
                os.writeC(38);
                os.writeC(getItem().get_addmpr());
            }

            /**ルームティスの青い光のイヤリングポーション効率表示**/
            if (itemType2 == 2 && itemId == 22230 && getEnchantLevel() >= 0) {
                os.writeC(39);
                os.writeS(RoomtisHealingPotion());
                os.writeC(39);
                os.writeS(RoomtisHealingPotion11());
            }

            /**祝福されたルームティスの青い光のイヤリングポーション効率表示 **/
            if (itemType2 == 2 && itemId == 222338 && getEnchantLevel() >= 0) {
                os.writeC(39);
                os.writeS(RoomtisHealingPotion1());
                os.writeC(39);
                os.writeS(RoomtisHealingPotion12());
            }
            /**ネックレス5以上ポーション回復量**/
            if (itemType2 == 2 && getItem().getGrade() >= 0 && getItem().getGrade() <= 2 && getItem().getAccessoryProcess() == 8 || getItem().getAccessoryProcess() == 12) {
                if (getEnchantLevel() > 4) {
                    os.writeC(39);
                    os.writeS("ポーション回復量" + ((getEnchantLevel() - 4) * 2) + "% +0");
                }
            }
            if (itemType2 == 2 && itemId == 222340 || itemId == 222341) {
                if (getEnchantLevel() == 3) {
                    if (getItemId() == 222341) {
                        os.writeC(6);
                        os.writeC(getEnchantLevel() - 2);
                        os.writeC(35);
                        os.writeC(getEnchantLevel() - 2);
                    } else {
                        os.writeC(6);
                        os.writeC(getEnchantLevel() - 2);
                        os.writeC(35);
                        os.writeC(getEnchantLevel() - 2);
                    }
                } else if (getEnchantLevel() == 4) {
                    if (getItemId() == 222341) {
                        os.writeC(6);
                        os.writeC(getEnchantLevel() - 2);
                        os.writeC(35);
                        os.writeC(getEnchantLevel() - 2);
                        os.writeC(39);
                        os.writeS("追加ダメージ確率+20（2％）");
                    } else {
                        os.writeC(6);
                        os.writeC(getEnchantLevel() - 3);
                        os.writeC(35);
                        os.writeC(getEnchantLevel() - 3);
                    }
                } else if (getEnchantLevel() == 5) {
                    if (getItemId() == 222341) {
                        os.writeC(6);
                        os.writeC(getEnchantLevel() - 2);
                        os.writeC(35);
                        os.writeC(getEnchantLevel() - 2);
                        os.writeC(39);
                        os.writeS("追加ダメージ確率+20（3％）");
                    } else {
                        os.writeC(6);
                        os.writeC(getEnchantLevel() - 3);
                        os.writeC(35);
                        os.writeC(getEnchantLevel() - 3);
                        os.writeC(39);
                        os.writeS("追加ダメージ確率+20（2％）");
                    }
                } else if (getEnchantLevel() == 6) {
                    if (getItemId() == 222341) {
                        os.writeC(6);
                        os.writeC(getEnchantLevel() - 2);
                        os.writeC(35);
                        os.writeC(getEnchantLevel() - 2);
                        os.writeC(39);
                        os.writeS("追加ダメージ確率+ 20（4％）");
                    } else {
                        os.writeC(6);
                        os.writeC(getEnchantLevel() - 3);
                        os.writeC(35);
                        os.writeC(getEnchantLevel() - 3);
                        os.writeC(39);
                        os.writeS("追加ダメージ確率+20（3％）");
                    }
                } else if (getEnchantLevel() == 7) {
                    if (getItemId() == 222341) {
                        os.writeC(6);
                        os.writeC(getEnchantLevel() - 2);
                        os.writeC(35);
                        os.writeC(getEnchantLevel() - 2);
                        os.writeC(39);
                        os.writeS("追加ダメージ確率+ 20（5％）");
                    } else {
                        os.writeC(6);
                        os.writeC(getEnchantLevel() - 3);
                        os.writeC(35);
                        os.writeC(getEnchantLevel() - 3);
                        os.writeC(39);
                        os.writeS("追加ダメージ確率+ 20（4％）");
                    }
                } else if (getEnchantLevel() == 8) {
                    if (getItemId() == 222341) {
                        os.writeC(6);
                        os.writeC(getEnchantLevel() - 2);
                        os.writeC(35);
                        os.writeC(getEnchantLevel() - 2);
                        os.writeC(39);
                        os.writeS("追加ダメージ確率+ 20（6％）");
                    } else {
                        os.writeC(6);
                        os.writeC(getEnchantLevel() - 3);
                        os.writeC(35);
                        os.writeC(getEnchantLevel() - 3);
                        os.writeC(39);
                        os.writeS("追加ダメージ確率+ 20（5％）");
                    }
                }
            }
            /** 祝福されたスナップ広がりの魔法抵抗リングMR表示 **/
            if (itemType2 == 2 && itemId == 222334 && getEnchantLevel() > 5) {
                os.writeC(15);
                os.writeH(getMr() + (getEnchantLevel() - 5));


                /** ルームティスの紫色のイヤリング魔表示 **/
            } else if (itemType2 == 2 && itemId == 22231 && getEnchantLevel() > 0) {
                switch (getEnchantLevel()) {
                    case 1:
                        os.writeC(15);
                        os.writeH(getMr() + 3);
                        break;
                    case 2:
                        os.writeC(15);
                        os.writeH(getMr() + 4);
                        break;
                    case 3:
                        os.writeC(15);
                        os.writeH(getMr() + 5);
                        break;
                    case 4:
                        os.writeC(15);
                        os.writeH(getMr() + 6);
                        break;
                    case 5:
                        os.writeC(15);
                        os.writeH(getMr() + 7);
                        break;
                    case 6:
                        os.writeC(15);
                        os.writeH(getMr() + 8);
                        break;
                    case 7:
                        os.writeC(15);
                        os.writeH(getMr() + 10);
                        break;
                    case 8:
                        os.writeC(15);
                        os.writeH(getMr() + 13);
                        break;
                    default:
                        break;
                }
                /** 祝福されたルームティスの紫色のイヤリング魔表示 **/
            } else if (itemType2 == 2 && itemId == 222339 && getEnchantLevel() > 0) {
                switch (getEnchantLevel()) {
                    case 1:
                        os.writeC(15);
                        os.writeH(getMr() + 3);
                        break;
                    case 2:
                        os.writeC(15);
                        os.writeH(getMr() + 4);
                        break;
                    case 3:
                        os.writeC(15);
                        os.writeH(getMr() + 6);
                        break;
                    case 4:
                        os.writeC(15);
                        os.writeH(getMr() + 7);
                        break;
                    case 5:
                        os.writeC(15);
                        os.writeH(getMr() + 8);
                        break;
                    case 6:
                        os.writeC(15);
                        os.writeH(getMr() + 10);
                        break;
                    case 7:
                        os.writeC(15);
                        os.writeH(getMr() + 13);
                        break;
                    case 8:
                        os.writeC(15);
                        os.writeH(getMr() + 18);
                        break;
                    default:
                        break;
                }

                /** 地竜のTシャツ **/
            } else if (itemType2 == 2 && itemId == 900023 && getEnchantLevel() > 4) {
                switch (getEnchantLevel()) {
                    case 5:
                        os.writeC(15);
                        os.writeH(getMr() + 4);
                        break;
                    case 6:
                        os.writeC(15);
                        os.writeH(getMr() + 5);
                        break;
                    case 7:
                        os.writeC(15);
                        os.writeH(getMr() + 6);
                        break;
                    case 8:
                        os.writeC(15);
                        os.writeH(getMr() + 8);
                        break;
                    case 9:
                        os.writeC(15);
                        os.writeH(getMr() + 11);
                        break;
                    case 10:
                        os.writeC(15);
                        os.writeH(getMr() + 14);
                        break;
                    case 11:
                        os.writeC(15);
                        os.writeH(getMr() + 14);
                        break;
                    default:
                        break;
                }

                /**リング6から魔表示 **/
            } else if (itemType2 == 2 && getItem().getGrade() >= 0 && getItem().getGrade() <= 2 && getItem().getAccessoryProcess() == 9 || getItem().getAccessoryProcess() == 11) {
                switch (getEnchantLevel()) {
                    case 6:
                        os.writeC(15);
                        os.writeH(getMr() + 1);
                        break;
                    case 7:
                        os.writeC(15);
                        os.writeH(getMr() + 3);
                        break;
                    case 8:
                        os.writeC(15);
                        os.writeH(getMr() + 5);
                        break;
                    case 9:
                        os.writeC(15);
                        os.writeH(getMr() + 7);
                        break;
                    default:
                        break;
                }
                /** 黒騎士のバイザーMR表示 **/
                /**神聖なエルムの祝福MR表示 **/
            } else if (itemType2 == 2 && itemId == 900027 || itemId == 910025 && getEnchantLevel() > 4) {
                switch (getEnchantLevel()) {
                    case 5:
                        os.writeC(15);
                        os.writeH(getMr() + 4);
                        break;
                    case 6:
                        os.writeC(15);
                        os.writeH(getMr() + 8);
                        break;
                    case 7:
                        os.writeC(15);
                        os.writeH(getMr() + 12);
                        break;
                    case 8:
                        os.writeC(15);
                        os.writeH(getMr() + 16);
                        break;
                    case 9:
                        os.writeC(15);
                        os.writeH(getMr() + 20);
                        break;
                    case 10:
                        os.writeC(15);
                        os.writeH(getMr() + 24);
                        break;
                    case 11:
                        os.writeC(15);
                        os.writeH(getMr() + 28);
                        break;
                    default:
                        break;
                }
            } else if (getMr() != 0) { // MR
                os.writeC(15);
                os.writeH(getMr());
            }
            /** 地竜のTシャツ **/
            if (itemType2 == 2 && itemId == 900023) {
                os.writeC(63); //ダメージ減少
                if (getEnchantLevel() >= 9)
                    os.writeC(2);
                else
                    os.writeC(1);
            }
            /** 地竜のTシャツ **/
            if (itemType2 == 2 && itemId == 900024) {
                os.writeC(47); //近距離ダメージ
                if (getEnchantLevel() >= 9)
                    os.writeC(2);
                else
                    os.writeC(1);
            }

            /** 風竜のTシャツ **/
            if (itemType2 == 2 && itemId == 900025) {
                os.writeC(35);//遠距離
                if (getEnchantLevel() >= 9)
                    os.writeC(2);
                else
                    os.writeC(1);
            }

            /** 水竜のTシャツ **/
            if (itemType2 == 2 && itemId == 900026) {
                os.writeC(17); // sp
                if (getEnchantLevel() >= 9)
                    os.writeC(2);
                else
                    os.writeC(1);
            }


            /** 水竜/火竜/風竜Tシャツ **/
            if ((itemId == 900026 || itemId == 900024 || itemId == 900025) && getEnchantLevel() >= 5) { //水竜火竜風竜スタン耐性
                os.writeC(33);
                os.writeC(5);
                switch (getEnchantLevel()) {
                    case 5:
                        os.writeC(8);
                        break;
                    case 6:
                        os.writeC(9);
                        break;
                    case 7:
                        os.writeC(10);
                        break;
                    case 8:
                        os.writeC(12);
                        break;
                    case 9:
                        os.writeC(15);
                        break;
                    case 10:
                        os.writeC(18);
                        break;
                    case 11:
                        os.writeC(18);
                        break;
                    default:
                        break;
                }
            }

            /** 水竜/火竜/風竜Tシャツ **/
            if ((itemId == 900026 || itemId == 900024 || itemId == 900025 || itemId == 900023) && getEnchantLevel() >= 10) { //水竜火竜風竜スタン耐性
                os.writeC(39);
                os.writeS("HP +100");
                os.writeC(39);
                os.writeS("\\f2PVP ダメージ増加+ " + 1);
                os.writeC(39);
                os.writeS("\\f2PVP ダメージ減少+ " + 1);
            }

            /** スナップファーの知恵リングSP表示 **/
            if (itemType2 == 2 && itemId == 222290 && getEnchantLevel() > 4) {
                switch (getEnchantLevel()) {
                    case 5:
                        os.writeC(17);
                        os.writeC(getItem().get_addsp() + 1);
                        break;
                    case 6:
                        os.writeC(17);
                        os.writeC(getItem().get_addsp() + 2);
                        break;
                    case 7:
                        os.writeC(17);
                        os.writeC(getItem().get_addsp() + 2);
                        break;
                    case 8:
                        os.writeC(17);
                        os.writeC(getItem().get_addsp() + 3);
                        break;
                    default:
                        break;
                }
                /** 祝福されたスナップ広がりの知恵リングSP表示**/
            } else if (itemType2 == 2 && itemId == 222335 && getEnchantLevel() > 3) {
                switch (getEnchantLevel()) {
                    case 4:
                        os.writeC(17);
                        os.writeC(getItem().get_addsp() + 1);
                        break;
                    case 5:
                    case 6:
                        os.writeC(17);
                        os.writeC(getItem().get_addsp() + 2);
                        break;
                    case 7:
                        os.writeC(17);
                        os.writeC(getItem().get_addsp() + 3);
                        break;
                    case 8:
                        os.writeC(17);
                        os.writeC(getItem().get_addsp() + 4);
                        break;
                    default:
                        break;
                }
                /** ルームティスの紫色のイヤリングSP表示 **/
            } else if (itemType2 == 2 && itemId == 22231 && getEnchantLevel() > 2) {
                switch (getEnchantLevel()) {
                    case 3:
                        os.writeC(17);
                        os.writeC(getItem().get_addsp() + 1);
                        break;
                    case 4:
                        os.writeC(17);
                        os.writeC(getItem().get_addsp() + 1);
                        break;
                    case 5:
                        os.writeC(17);
                        os.writeC(getItem().get_addsp() + 2);
                        break;
                    case 6:
                        os.writeC(17);
                        os.writeC(getItem().get_addsp() + 2);
                        break;
                    case 7:
                        os.writeC(17);
                        os.writeC(getItem().get_addsp() + 3);
                        break;
                    case 8:
                        os.writeC(17);
                        os.writeC(getItem().get_addsp() + 3);
                        break;
                    default:
                        break;
                }
                /**祝福されたルームティスの紫色のイヤリングSP表示 **/
            } else if (itemType2 == 2 && itemId == 222339 && getEnchantLevel() > 2) {
                switch (getEnchantLevel()) {
                    case 3:
                        os.writeC(17);
                        os.writeC(getItem().get_addsp() + 1);
                        break;
                    case 4:
                    case 5:
                        os.writeC(17);
                        os.writeC(getItem().get_addsp() + 2);
                        break;
                    case 6:
                    case 7:
                        os.writeC(17);
                        os.writeC(getItem().get_addsp() + 3);
                        break;
                    case 8:
                        os.writeC(17);
                        os.writeC(getItem().get_addsp() + 4);
                        break;
                    default:
                        break;
                }
                /** ウィザードのガーダーSP表示 **/
            } else if (itemType2 == 2 && itemId == 22255 && getEnchantLevel() > 4) {
                switch (getEnchantLevel()) {
                    case 5:
                        os.writeC(17);
                        os.writeC(getItem().get_addsp() + 1);
                        break;
                    case 6:
                        os.writeC(17);
                        os.writeC(getItem().get_addsp() + 1);
                        break;
                    case 7:
                        os.writeC(17);
                        os.writeC(getItem().get_addsp() + 2);
                        break;
                    case 8:
                        os.writeC(17);
                        os.writeC(getItem().get_addsp() + 2);
                        break;
                    case 9:
                        os.writeC(17);
                        os.writeC(getItem().get_addsp() + 3);
                        break;
                    default:
                        break;
                }
                /** リッチローブSP表示 **/
            } else if (itemType2 == 2 && itemId == 20107 && getEnchantLevel() >= 3) {
                os.writeC(17);
                os.writeC(getItem().get_addsp() + getEnchantLevel() - 2);
            } else if (addsp() != 0) {
                if (itemType2 == 1 && itemId == 134) {
                    os.writeC(17);
                    os.writeC(addsp() + getEnchantLevel());
                } else {
                    os.writeC(17);
                    os.writeC(addsp());
                }
            }

            /** マンボコートのエンチャント+7でのCHA増加 **/
            if (itemType2 == 2 && (itemId == 20112 || itemId == 120112)) {
            	if (getEnchantLevel() <= 6) {
                    os.writeC(13);
                    os.writeC(getItem().get_addcha() + 2);
            	} else if (getEnchantLevel() >= 7) {
                    os.writeC(13);
                    os.writeC(getItem().get_addcha() + 3);
            	}
            }

            if (getItem().isHasteItem()) {
                os.writeC(18);
            }

            // マナ吸収
            if (itemId == 126 || itemId == 127) {
                os.writeC(16);
            }

            // 被吸収
            if (itemId == 12 || itemId == 601 || itemId == 1123 || itemId == 202013) {
                os.writeC(34);
            }

            /** ベルト5以上ダメージリダクション**/
            if (itemType2 == 2 && (getItem().getType() == 10 && getEnchantLevel() >= 5)) {//ベルト
                os.writeC(39);
                os.writeS("ダメージリダクション" + (getEnchantLevel() - 4));
            }
            /** ルームティスの赤い光のイヤリングダメージリダクション **/
            if (getItemId() == 22229 && getEnchantLevel() > 2) {
                switch (getEnchantLevel()) {
                    case 3:
                    case 4:
                        os.writeC(39);
                        os.writeS("ダメージリダクション" + (getItem().getDamageReduction() + 1));
                        break;
                    case 5:
                        os.writeC(39);
                        os.writeS("ダメージリダクション" + (getItem().getDamageReduction() + 2));
                        break;
                    case 6:
                        os.writeC(39);
                        os.writeS("ダメージリダクション" + (getItem().getDamageReduction() + 3));
                        break;
                    case 7:
                        os.writeC(39);
                        os.writeS("ダメージリダクション" + (getItem().getDamageReduction() + 4));
                        break;
                    case 8:
                        os.writeC(39);
                        os.writeS("ダメージリダクション" + (getItem().getDamageReduction() + 5));
                        break;
                    default:
                        break;
                }
            }
            /** 祝福されたルームティスの赤い光のイヤリングダメージリダクション **/
            if (itemId == 222337 && getEnchantLevel() > 2) {
                switch (getEnchantLevel()) {
                    case 3:
                        os.writeC(39);
                        os.writeS("ダメージリダクション" + (getItem().getDamageReduction() + 1));
                        break;
                    case 4:
                        os.writeC(39);
                        os.writeS("ダメージリダクション" + (getItem().getDamageReduction() + 2));
                        break;
                    case 5:
                        os.writeC(39);
                        os.writeS("ダメージリダクション" + (getItem().getDamageReduction() + 3));
                        break;
                    case 6:
                        os.writeC(39);
                        os.writeS("ダメージリダクション" + (getItem().getDamageReduction() + 4));
                        break;
                    case 7:
                        os.writeC(39);
                        os.writeS("ダメージリダクション" + (getItem().getDamageReduction() + 5));
                        break;
                    case 8:
                        os.writeC(39);
                        os.writeS("ダメージリダクション" + (getItem().getDamageReduction() + 6));
                        break;
                    default:
                        break;
                }
            }
            /** 守護のガーダーダメージリダクション **/
            if (itemId == 22254 && getEnchantLevel() > 4) {
                switch (getEnchantLevel()) {
                    case 5:
                    case 6:
                        os.writeC(39);
                        os.writeS("ダメージリダクション+" + (getItem().getDamageReduction() + 0));
                        break;
                    case 7:
                    case 8:
                        os.writeC(39);
                        os.writeS("ダメージリダクション+" + (getItem().getDamageReduction() + 1));
                        break;
                    case 9:
                        os.writeC(39);
                        os.writeS("ダメージリダクション+" + (getItem().getDamageReduction() + 2));
                        break;
                    default:
                        break;
                }
            }
            /** イヤリング、ネックレス+ 6からACセクションの処理**/
            if (itemType2 == 2 && getItem().getGrade() >= 0 && getItem().getGrade() <= 2 && getItem().getAccessoryProcess() == 8 || getItem().getAccessoryProcess() == 12 && getEnchantLevel() > 5) {
                switch (getEnchantLevel()) {
                    case 6:
                        os.writeC(39);
                        os.writeS("AC +" + 1);
                        break;
                    case 7:
                        os.writeC(39);
                        os.writeS("AC +" + 2);
                        break;
                    case 8:
                        os.writeC(39);
                        os.writeS("AC +" + 3);
                        break;
                    case 9:
                        os.writeC(39);
                        os.writeS("AC +" + 4);
                        break;
                    default:
                        break;
                }
            }

            /** ナイトバルドの両手剣スターン的中+5 **/
            if (itemId == 1121 || itemId == 11121) {
                switch (getEnchantLevel()) {
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
                        os.writeC(39);
                        os.writeS("スタン命中+" + (getItem().get_regist_stun() + 5));
                        break;
                    default:
                        break;
                }
            }

            /** 回復の文章**/
            if (itemId == 900021) {
                switch (getEnchantLevel()) {
                    case 0:
                        os.writeC(39);
                        os.writeS("ポーション回復量2％+ 2％");
                        os.writeC(39);
                        os.writeS("恐怖回復減少効果を相殺+ 2％");
                        break;
                    case 1:
                        os.writeC(39);
                        os.writeS("ポーション回復量4％+ 4％");
                        os.writeC(39);
                        os.writeS("恐怖回復減少効果を相殺+ 4％");
                        break;
                    case 2:
                        os.writeC(39);
                        os.writeS("ポーション回復量6％+6％");
                        os.writeC(39);
                        os.writeS("恐怖回復減少効果を相殺+ 6％");
                        break;
                    case 3:
                        os.writeC(39);
                        os.writeS("ポーション回復量8％+8％");
                        os.writeC(39);
                        os.writeS("恐怖回復減少効果を相殺+ 8％");
                        break;
                    case 4:
                        os.writeC(39);
                        os.writeS("ポーション回復量10％+ 10％");
                        os.writeC(39);
                        os.writeS("恐怖回復減少効果を相殺+ 10％");
                        break;
                    case 5:
                        os.writeC(39);
                        os.writeS("ポーション回復量12％+ 12％");
                        os.writeC(39);
                        os.writeS("恐怖回復減少効果を相殺+ 12％");
                        break;
                    case 6:
                        os.writeC(39);
                        os.writeS("ポーション回復量14％+14％");
                        os.writeC(39);
                        os.writeS("恐怖回復減少効果を相殺+ 14％");
                        break;
                    case 7:
                        os.writeC(39);
                        os.writeS("ポーション回復量16％+ 16％");
                        os.writeC(39);
                        os.writeS("恐怖回復減少効果を相殺+ 16％");
                        break;
                    case 8:
                        os.writeC(39);
                        os.writeS("ポーション回復量18％+18％");
                        os.writeC(39);
                        os.writeS("恐怖回復減少効果を相殺+ 18％");
                        break;
                    case 9:
                        os.writeC(39);
                        os.writeS("ポーション回復量20％+ 20％");
                        os.writeC(39);
                        os.writeS("恐怖回復減少効果を相殺+ 20％");
                        break;
                    case 10:
                        os.writeC(39);
                        os.writeS("ポーション回復量22％+22％");
                        os.writeC(39);
                        os.writeS("恐怖回復減少効果を相殺+ 22％");
                        break;
                    default:
                        break;
                }
            }

            /** 成長の文章 **/
            if (itemId == 900020) {
                switch (getEnchantLevel()) {
                    case 0:
                        os.writeC(39);
                        os.writeS("expボーナス" + (getItem().get_addexp1() + 1 + "%"));
                        break;
                    case 1:
                        os.writeC(39);
                        os.writeS("expボーナス" + (getItem().get_addexp1() + 2 + "%"));
                        break;
                    case 2:
                        os.writeC(39);
                        os.writeS("expボーナス" + (getItem().get_addexp1() + 3 + "%"));
                        break;
                    case 3:
                        os.writeC(39);
                        os.writeS("expボーナス" + (getItem().get_addexp1() + 4 + "%"));
                        break;
                    case 4:
                        os.writeC(39);
                        os.writeS("expボーナス" + (getItem().get_addexp1() + 5 + "%"));
                        break;
                    case 5:
                        os.writeC(39);
                        os.writeS("expボーナス" + (getItem().get_addexp1() + 6 + "%"));
                        break;
                    case 6:
                        os.writeC(39);
                        os.writeS("expボーナス" + (getItem().get_addexp1() + 7 + "%"));
                        break;
                    case 7:
                        os.writeC(39);
                        os.writeS("expボーナス" + (getItem().get_addexp1() + 9 + "%"));
                        break;
                    case 8:
                        os.writeC(39);
                        os.writeS("expボーナス" + (getItem().get_addexp1() + 11 + "%"));
                        break;
                    case 9:
                        os.writeC(39);
                        os.writeS("expボーナス" + (getItem().get_addexp1() + 13 + "%"));
                        break;
                    case 10:
                        os.writeC(39);
                        os.writeS("expボーナス" + (getItem().get_addexp1() + 15 + "%"));
                        break;
                    default:
                        break;
                }
            }
            /** 腕力の文章 **/
            if (itemId == 222352) {
                switch (getEnchantLevel()) {
                    case 1:
                        os.writeC(39);
                        os.writeS("ポーション回復量2％+ 2％");
                        os.writeC(39);
                        os.writeS("恐怖回復減少効果を相殺+ 2％");
                        break;
                    case 2:
                        os.writeC(39);
                        os.writeS("ポーション回復量4％+ 4％");
                        os.writeC(39);
                        os.writeS("恐怖回復減少効果を相殺+ 4％");
                        break;
                    case 3:
                        os.writeC(39);
                        os.writeS("ポーション回復量6％+6％");
                        os.writeC(39);
                        os.writeS("恐怖回復減少効果を相殺+ 6％");
                        break;
                    case 4:
                        os.writeC(39);
                        os.writeS("ポーション回復量8％+8％");
                        os.writeC(39);
                        os.writeS("恐怖回復減少効果を相殺+ 8％");
                        break;
                    case 5:
                        os.writeC(39);
                        os.writeS("近距離命中+1");
                        os.writeC(39);
                        os.writeS("ポーション回復量9％+ 9％");
                        os.writeC(39);
                        os.writeS("恐怖回復減少効果を相殺+ 9％");
                        break;
                    case 6:
                        os.writeC(39);
                        os.writeS("近距離命中+1");
                        os.writeC(39);
                        os.writeS("近距離ダメージ+1");
                        os.writeC(39);
                        os.writeS("ポーション回復量10％+ 10％");
                        os.writeC(39);
                        os.writeS("恐怖回復減少効果を相殺+ 10％");
                        break;
                    case 7:
                        os.writeC(39);
                        os.writeS("近距離命中+2");
                        os.writeC(39);
                        os.writeS("近距離ダメージ+2");
                        os.writeC(39);
                        os.writeS("ポーション回復量11％+ 11％");
                        os.writeC(39);
                        os.writeS("恐怖回復減少効果を相殺+ 11％");
                        break;
                    case 8:
                        os.writeC(39);
                        os.writeS("近距離命中+3");
                        os.writeC(39);
                        os.writeS("近距離ダメージ+3");
                        os.writeC(39);
                        os.writeS("ポーション回復量12％+ 12％");
                        os.writeC(39);
                        os.writeS("恐怖回復減少効果を相殺+ 12％");
                        break;
                    case 9:
                        os.writeC(39);
                        os.writeS("近距離命中+4");
                        os.writeC(39);
                        os.writeS("近距離ダメージ+4");
                        os.writeC(39);
                        os.writeS("ポーション回復量13％+13％");
                        os.writeC(39);
                        os.writeS("恐怖回復減少効果を相殺+ 13％");
                        break;
                    case 10:
                        os.writeC(39);
                        os.writeS("近距離命中+5");
                        os.writeC(39);
                        os.writeS("近距離ダメージ+5");
                        os.writeC(39);
                        os.writeS("ポーション回復量14％+14％");
                        os.writeC(39);
                        os.writeS("恐怖回復減少効果を相殺+ 14％");
                        break;
                    default:
                        break;
                }
            }
            /** 機敏の文章 **/
            if (itemId == 222353) {
                switch (getEnchantLevel()) {
                    case 1:
                        os.writeC(39);
                        os.writeS("ポーション回復量2％+ 2％");
                        os.writeC(39);
                        os.writeS("恐怖回復減少効果を相殺+ 2％");
                        break;
                    case 2:
                        os.writeC(39);
                        os.writeS("ポーション回復量4％+ 4％");
                        os.writeC(39);
                        os.writeS("恐怖回復減少効果を相殺+ 4％");
                        break;
                    case 3:
                        os.writeC(39);
                        os.writeS("ポーション回復量6％+6％");
                        os.writeC(39);
                        os.writeS("恐怖回復減少効果を相殺+ 6％");
                        break;
                    case 4:
                        os.writeC(39);
                        os.writeS("ポーション回復量8％+8％");
                        os.writeC(39);
                        os.writeS("恐怖回復減少効果を相殺+ 8％");
                        break;
                    case 5:
                        os.writeC(39);
                        os.writeS("遠距離命中+1");
                        os.writeC(39);
                        os.writeS("ポーション回復量9％+ 9％");
                        os.writeC(39);
                        os.writeS("恐怖回復減少効果を相殺+ 9％");
                        break;
                    case 6:
                        os.writeC(39);
                        os.writeS("遠距離命中+ 1％");
                        os.writeC(39);
                        os.writeS("遠距離ダメージ+1");
                        os.writeC(39);
                        os.writeS("ポーション回復量10％+ 10％");
                        os.writeC(39);
                        os.writeS("恐怖回復減少効果を相殺+ 10％");
                        break;
                    case 7:
                        os.writeC(39);
                        os.writeS("遠距離命中+2");
                        os.writeC(39);
                        os.writeS("遠距離ダメージ+2");
                        os.writeC(39);
                        os.writeS("ポーション回復量11％+ 11％");
                        os.writeC(39);
                        os.writeS("恐怖回復減少効果を相殺+ 11％");
                        break;
                    case 8:
                        os.writeC(39);
                        os.writeS("遠距離命中+3");
                        os.writeC(39);
                        os.writeS("遠距離ダメージ+3");
                        os.writeC(39);
                        os.writeS("ポーション回復量12％+ 12％");
                        os.writeC(39);
                        os.writeS("恐怖回復減少効果を相殺+ 12％");
                        break;
                    case 9:
                        os.writeC(39);
                        os.writeS("遠距離命中+4");
                        os.writeC(39);
                        os.writeS("遠距離ダメージ+4");
                        os.writeC(39);
                        os.writeS("ポーション回復量13％+13％");
                        os.writeC(39);
                        os.writeS("恐怖回復減少効果を相殺+ 13％");
                        break;
                    case 10:
                        os.writeC(39);
                        os.writeS("遠距離命中+5");
                        os.writeC(39);
                        os.writeS("遠距離ダメージ+5");
                        os.writeC(39);
                        os.writeS("ポーション回復量14％+14％");
                        os.writeC(39);
                        os.writeS("恐怖回復減少効果を相殺+ 14％");
                        break;
                    default:
                        break;
                }
            }
            /** 知識の文章 **/
            if (itemId == 222354) {
                switch (getEnchantLevel()) {
                    case 1:
                        os.writeC(39);
                        os.writeS("ポーション回復量2％+ 2％");
                        os.writeC(39);
                        os.writeS("恐怖回復減少効果を相殺+ 2％");
                        break;
                    case 2:
                        os.writeC(39);
                        os.writeS("ポーション回復量4％+ 4％");
                        os.writeC(39);
                        os.writeS("恐怖回復減少効果を相殺+ 4％");
                        break;
                    case 3:
                        os.writeC(39);
                        os.writeS("ポーション回復量6％+6％");
                        os.writeC(39);
                        os.writeS("恐怖回復減少効果を相殺+ 6％");
                        break;
                    case 4:
                        os.writeC(39);
                        os.writeS("ポーション回復量8％+8％");
                        os.writeC(39);
                        os.writeS("恐怖回復減少効果を相殺+ 8％");
                        break;
                    case 5:
                        os.writeC(39);
                        os.writeS("魔法命中+1");
                        os.writeC(39);
                        os.writeS("ポーション回復量9％+ 9％");
                        os.writeC(39);
                        os.writeS("恐怖回復減少効果を相殺+ 9％");
                        break;
                    case 6:
                        os.writeC(39);
                        os.writeS("魔法命中+1");
                        os.writeC(39);
                        os.writeS("SP +1");
                        os.writeC(39);
                        os.writeS("ポーション回復量10％+ 10％");
                        os.writeC(39);
                        os.writeS("恐怖回復減少効果を相殺+ 10％");
                        break;
                    case 7:
                        os.writeC(39);
                        os.writeS("魔法命中+2");
                        os.writeC(39);
                        os.writeS("SP +2");
                        os.writeC(39);
                        os.writeS("ポーション回復量11％+ 11％");
                        os.writeC(39);
                        os.writeS("恐怖回復減少効果を相殺+ 11％");
                        break;
                    case 8:
                        os.writeC(39);
                        os.writeS("魔法命中+3");
                        os.writeC(39);
                        os.writeS("SP +3");
                        os.writeC(39);
                        os.writeS("ポーション回復量12％+ 12％");
                        os.writeC(39);
                        os.writeS("恐怖回復減少効果を相殺+ 12％");
                        break;
                    case 9:
                        os.writeC(39);
                        os.writeS("魔法命中+4");
                        os.writeC(39);
                        os.writeS("SP +4");
                        os.writeC(39);
                        os.writeS("ポーション回復量13％+13％");
                        os.writeC(39);
                        os.writeS("恐怖回復減少効果を相殺+ 13％");
                        break;
                    case 10:
                        os.writeC(39);
                        os.writeS("魔法命中+5");
                        os.writeC(39);
                        os.writeS("SP +5");
                        os.writeC(39);
                        os.writeS("ポーション回復量14％+14％");
                        os.writeC(39);
                        os.writeS("恐怖回復減少効果を相殺+ 14％");
                        break;
                    default:
                        break;
                }
            }

            if (itemId == 900019) { //シルフのTシャツ
                switch (getEnchantLevel()) {
                    case 0:
                        os.writeC(39);
                        os.writeS("MR +7");
                        os.writeC(39);
                        os.writeS("スタン耐性+7");
                        break;
                    case 1:
                        os.writeC(39);
                        os.writeS("MR +8");
                        os.writeC(39);
                        os.writeS("スタン耐性+8");
                        break;
                    case 2:
                        os.writeC(39);
                        os.writeS("MR +9");
                        os.writeC(39);
                        os.writeS("スタン耐性+9");
                        break;
                    case 3:
                        os.writeC(39);
                        os.writeS("MR +10");
                        os.writeC(39);
                        os.writeS("スタン耐性+10");
                        break;
                    case 4:
                        os.writeC(39);
                        os.writeS("MR +11");
                        os.writeC(39);
                        os.writeS("スタン耐性+11");
                        break;
                    case 5:
                        os.writeC(39);
                        os.writeS("MR +12");
                        os.writeC(39);
                        os.writeS("スタン耐性+12");
                        break;
                    case 6:
                        os.writeC(39);
                        os.writeS("MR +13");
                        os.writeC(39);
                        os.writeS("スタン耐性+13");
                        break;
                    case 7:
                        os.writeC(39);
                        os.writeS("MR +14");
                        os.writeC(39);
                        os.writeS("スタン耐性+14");
                        break;
                    case 8:
                        os.writeC(39);
                        os.writeS("MR +15");
                        os.writeC(39);
                        os.writeS("スタン耐性+15");
                        break;
                    case 9:
                        os.writeC(39);
                        os.writeS("MR +16");
                        os.writeC(39);
                        os.writeS("スタン耐性+16");
                        break;
                    case 10:
                        os.writeC(39);
                        os.writeS("MR +17");
                        os.writeC(39);
                        os.writeS("スタン耐性+17");
                        break;
                    case 11:
                        os.writeC(39);
                        os.writeS("MR +18");
                        os.writeC(39);
                        os.writeS("スタン耐性+18");
                        break;
                    case 12:
                        os.writeC(39);
                        os.writeS("MR +19");
                        os.writeC(39);
                        os.writeS("スタン耐性+19");
                        break;
                    case 13:
                        os.writeC(39);
                        os.writeS("MR +20");
                        os.writeC(39);
                        os.writeS("スタン耐性+20");
                        break;
                    case 14:
                        os.writeC(39);
                        os.writeS("MR +21");
                        os.writeC(39);
                        os.writeS("スタン耐性+21");
                        break;
                    case 15:
                        os.writeC(39);
                        os.writeS("MR +22");
                        os.writeC(39);
                        os.writeS("スタン耐性+22");
                        break;
                }
            }
            /**魔物8エンチャン以上 **/
            if (itemId >= 900015 && itemId <= 900018) {
                if (getEnchantLevel() >= 8) {
                    os.writeC(39);
                    os.writeS("\\f3近距離ダメージ +1");
                    os.writeC(39);
                    os.writeS("\\f3遠距離ダメージ +1");
                }
            }
            /***************************/
            /**岩石8エンチャン以上 **/
            if (itemId >= 900011 && itemId <= 900014) {
                if (getEnchantLevel() >= 8) {
                    os.writeC(39);
                    os.writeS("\\f3PVPダメージ減少 +1");
                    os.writeC(39);
                    os.writeS("\\f3ダメージリダクション +1");
                }
            }
            /***************************/


            /** 真冥王の執行剣スターン的中+10 **/
            if (itemId == 61 || itemId == 7000061 || itemId == 202012) {
                switch (getEnchantLevel()) {
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
                        os.writeC(39);
                        os.writeS("スタン命中+" + (getItem().get_regist_stun() + 10));
                        break;
                    default:
                        break;
                }
            }

            /** 古代闘士のがより近距離ダメージ **/
            if (itemId == 22003 && getEnchantLevel() > 4) {
                switch (getEnchantLevel()) {
                    case 5:
                    case 6:
                        os.writeC(39);
                        os.writeS("近距離ダメージ+" + (getItem().getDmgRate() + 0));
                        break;
                    case 7:
                    case 8:
                        os.writeC(39);
                        os.writeS("近距離ダメージ+" + (getItem().getDmgRate() + 1));
                        break;
                    case 9:
                        os.writeC(39);
                        os.writeS("近距離ダメージ+" + (getItem().getDmgRate() + 2));
                        break;
                    default:
                        break;
                }
            }

            /** 古代名弓のがより遠距離ダメージ **/
            if (itemId == 22000 && getEnchantLevel() > 4) {
                switch (getEnchantLevel()) {
                    case 5:
                    case 6:
                        os.writeC(39);
                        os.writeS("遠距離ダメージ+" + (getItem().getBowDmgRate() + 0));
                        break;
                    case 7:
                    case 8:
                        os.writeC(39);
                        os.writeS("遠距離ダメージ+" + (getItem().getBowDmgRate() + 1));
                        break;
                    case 9:
                        os.writeC(39);
                        os.writeS("遠距離ダメージ+" + (getItem().getBowDmgRate() + 2));
                        break;
                    default:
                        break;
                }
            }
            /** マミーロードクラウン遠距離ダメージ **/
            if (itemId == 20017 && getEnchantLevel() > 6) {
                switch (getEnchantLevel()) {
                    case 7:
                        os.writeC(39);
                        os.writeS("遠距離ダメージ+" + (getItem().getBowDmgRate() + 1));
                        break;
                    case 8:
                        os.writeC(39);
                        os.writeS("遠距離ダメージ+" + (getItem().getBowDmgRate() + 2));
                        break;
                    case 9:
                        os.writeC(39);
                        os.writeS("遠距離ダメージ+" + (getItem().getBowDmgRate() + 3));
                        break;
                    default:
                        break;
                }
            }
            /** （知恵/機敏/知識/腕力）のブーツ *
             * :	+7から最大HP + 20 / + 40 / + 60増加
             * :	+9にダメージ減少+ 1を追加
             * */
            if (itemId == 22359 || itemId == 222308 || itemId == 222309 || itemId == 222307 && getEnchantLevel() > 6) {
                switch (getEnchantLevel()) {
                    case 7:
                        os.writeC(39);
                        os.writeS("HP +" + 20);
                        break;
                    case 8:
                        os.writeC(39);
                        os.writeS("HP +" + 40);
                        break;
                    case 9:
                        os.writeC(39);
                        os.writeS("HP +" + 60);
                        os.writeC(39);
                        os.writeS("ダメージ減少" + 1);
                        break;
                    default:
                        break;
                }
            }
            /** アンタラス上級リニューアル **/
            if (itemId >= 22196 && itemId <= 22199) {
                int dam = getEnchantLevel() - 6;
                int total = getItem().getDamageReduction() + dam;
                int total2 = getItem().getDamageReduction() + 3;
                if (getEnchantLevel() >= 7 && getEnchantLevel() <= 9) {
                    os.writeC(39);
                    os.writeS("ダメージリダクション" + total);
                } else if (getEnchantLevel() > 9) {
                    os.writeC(39);
                    os.writeS("ダメージリダクション" + total2);
                } else {
                    os.writeC(39);
                    os.writeS("ダメージリダクション" + getItem().getDamageReduction());
                }
            } else if (itemId == 491005) { // ダメージリダクション
                os.writeC(39);
                if (getEnchantLevel() >= 9)
                    os.writeS("ダメージリダクション+" + (getItem().getDamageReduction() + 1));
                else
                    os.writeS("ダメージリダクション+" + getItem().getDamageReduction());

            } else if (getItem().getDamageReduction() != 0) { // ダメージリダクション
                os.writeC(39);
                os.writeS("ダメージリダクション" + getItem().getDamageReduction());
            }
            /** ベルト7からpvpダメージ減少 **/
            if (itemType2 == 2 && (getItem().getType() == 10 && getEnchantLevel() > 6)) {// ベルト
                switch (getEnchantLevel()) {
                    case 7:
                        os.writeC(60);
                        os.writeC(getItem().get_regist_calcPcDefense() + getEnchantLevel() - 5);
                        break;
                    case 8:
                        os.writeC(60);
                        os.writeC(getItem().get_regist_calcPcDefense() + getEnchantLevel() - 5);
                        break;
                    case 9:
                        os.writeC(60);
                        os.writeC(getItem().get_regist_calcPcDefense() + getEnchantLevel() - 5);
                        break;
                    default:
                        break;
                }
                /** PVPダメージ減少 **/
            } else if (getItem().get_regist_calcPcDefense() != 0) {
                os.writeC(39);
                os.writeS("\\f2PVPダメージ減少+ " + getItem().get_regist_calcPcDefense());
            }
            /** PVP追加ダメージ **/
            if (getItem().get_regist_PVPweaponTotalDamage() != 0) {
                os.writeC(39);
                os.writeS("\\f2PVP追加ダメージ+ " + getItem().get_regist_PVPweaponTotalDamage());
            }

            /** ダメージリダクション武器表記付与 **/
            if (itemId == 202011) {
                if (getEnchantLevel() >= 0) {
                    os.writeC(39);
                    os.writeS("ダメージリダクション無視+12");
                }
            }
            if ((itemId >= 22208 && itemId <= 22211)) { // 塗っ鎧
                if (getEnchantLevel() < 7) {
                    os.writeC(39);
                    os.writeS("ダメージリダクション無視+3");
                } else if (getEnchantLevel() == 7) {
                    os.writeC(39);
                    os.writeS("ダメージリダクション無視+4");
                } else if (getEnchantLevel() == 8) {
                    os.writeC(39);
                    os.writeS("ダメージリダクション無視+ 5");
                } else if (getEnchantLevel() >= 9) {
                    os.writeC(39);
                    os.writeS("ダメージリダクション無視+6");
                }
            }
            if ((itemId == 203025 || itemId == 203026) // ジンサウルアビ銃剣
                    && getEnchantLevel() >= 10) {
                os.writeC(39);
                os.writeS("発動：戦う魂");
            }
            if ((itemId == 203006) // 台風の斧
                    && getEnchantLevel() >= 10) {
                os.writeC(39);
                os.writeS("発動：地獄");
            }
            if ((itemId == 1136) // 悪夢のロングボウ
                    && getEnchantLevel() >= 10) {
                os.writeC(39);
                os.writeS("発動：悪夢");
            }

            if ((itemId == 203017) // 殲滅者チェーンソード
                    && getEnchantLevel() >= 10) {
                os.writeC(39);
                os.writeS("発動：殲滅");
            }
            if (itemId == 202014) // タイタンの憤怒　
                     {
                os.writeC(39);
                os.writeS("タイタン系列発動区間 +5%");
            }
            if ((itemId >= 22208 && itemId <= 22211)
                    && getEnchantLevel() >= 7) {
                os.writeC(39);
                os.writeS("発動：ヴァラカスの一撃");
            }
            /** 神聖な知識のネックレス魔法命中**/
            if (itemId == 222348) {
                os.writeC(39);
                os.writeS("魔法命中+2");
            }
            /** 神聖な永遠のネックレススタン耐性**/
            if (itemId == 222349) {
                os.writeC(39);
                os.writeS("スタン耐性+7");
            }


            /** 台風の斧恐怖的中 **/
            if (itemId == 203006) {
                switch (getEnchantLevel()) {
                    case 8:
                        os.writeC(39);
                        os.writeS("恐怖的中+1");
                        break;
                    case 9:
                        os.writeC(39);
                        os.writeS("恐怖的中+2");
                        break;
                    case 10:
                        os.writeC(39);
                        os.writeS("恐怖的中+3");
                        break;
                    default:
                        break;
                }
            }
            /** タイタンの怒り恐怖的中 **/
            if (itemId == 202014) {
                switch (getEnchantLevel()) {
                    case 0:
                        os.writeC(39);
                        os.writeS("恐怖的中+5");
                        break;
                    case 1:
                        os.writeC(39);
                        os.writeS("恐怖的中+6");
                        break;
                    case 2:
                        os.writeC(39);
                        os.writeS("恐怖的中+7");
                        break;
                    case 3:
                        os.writeC(39);
                        os.writeS("恐怖的中+8");
                        break;
                    case 4:
                        os.writeC(39);
                        os.writeS("恐怖的中+9");
                        break;
                    case 5:
                        os.writeC(39);
                        os.writeS("恐怖的中+10");
                        break;
                    case 6:
                        os.writeC(39);
                        os.writeS("恐怖的中+11");
                        break;
                    case 7:
                        os.writeC(39);
                        os.writeS("恐怖的中+12");
                        break;
                    case 8:
                        os.writeC(39);
                        os.writeS("恐怖的中+13");
                        break;
                    case 9:
                        os.writeC(39);
                        os.writeS("恐怖的中+14");
                        break;
                    case 10:
                        os.writeC(39);
                        os.writeS("恐怖的中+15");
                        break;
                    default:
                        break;
                }
            }

            /** 輝く魔力の手袋 **/
            if (itemId == 20274 && getEnchantLevel() > 4) {
                int weightBonus = getEnchantLevel() - 4;
                os.writeC(39);
                os.writeS("重量ゲージ" + (getItem().getWeightReduction() + weightBonus));
            } else if (getItem().getWeightReduction() != 0) { // 重量ゲージ
                os.writeC(39);
                os.writeS("重量ゲージ" + getItem().getWeightReduction());
            }

            if (itemId == 22263) {
                os.writeC(39);
                os.writeS("あるチェンスx2の確率でダメージ減少50");
                switch (getEnchantLevel()) {
                    case 1:
                        os.writeC(39);
                        os.writeS("現在発動確率：2％");
                        break;
                    case 2:
                        os.writeC(39);
                        os.writeS("現在発動確率：4％");
                        break;
                    case 3:
                        os.writeC(39);
                        os.writeS("現在発動確率：6％");
                        break;
                    case 4:
                        os.writeC(39);
                        os.writeS("現在発動確率：8％");
                        break;
                    case 5:
                        os.writeC(39);
                        os.writeS("現在発動確率：10％");
                        break;
                    case 6:
                        os.writeC(39);
                        os.writeS("現在発動確率：12％");
                        break;
                    case 7:
                        os.writeC(39);
                        os.writeS("現在発動確率：14％");
                        break;
                    case 8:
                        os.writeC(39);
                        os.writeS("現在発動確率：16％");
                        break;
                    case 9:
                        os.writeC(39);
                        os.writeS("現在発動確率：18％");
                        break;
                    default:
                        break;
                }
            }
            /** ルームティスの赤い光のイヤリング確率 **/
            if (itemId == 22229) {
                switch (getEnchantLevel()) {
                    case 5:
                        os.writeC(39);
                        os.writeS("確率ダメージリダクション2％");
                        break;
                    case 6:
                        os.writeC(39);
                        os.writeS("確率ダメージリダクション3％");
                        break;
                    case 7:
                        os.writeC(39);
                        os.writeS("確率ダメージリダクション4％");
                        break;
                    case 8:
                        os.writeC(39);
                        os.writeS("確率ダメージリダクション5％");
                        break;
                }
                os.writeC(39);
            }
            /** 祝福されたルームティスの赤い光のイヤリング確率 **/
            if (itemId == 222337) {
                switch (getEnchantLevel()) {
                    case 4:
                        os.writeC(39);
                        os.writeS("確率ダメージリダクション2％");
                        break;
                    case 5:
                        os.writeC(39);
                        os.writeS("確率ダメージリダクション3％");
                        break;
                    case 6:
                        os.writeC(39);
                        os.writeS("確率ダメージリダクション4％");
                        break;
                    case 7:
                        os.writeC(39);
                        os.writeS("確率ダメージリダクション5％");
                        break;
                    case 8:
                        os.writeC(39);
                        os.writeS("確率ダメージリダクション6％");
                        break;
                }
                os.writeC(39);
            }


            if (getItem().get_penetration() == 1) {        //貫通
                os.writeC(39);
                os.writeS("貫通効果");

            }
            if (getItem().get_regist_freeze() != 0 && (getMr() == 0)) {
                os.writeC(33);
                os.writeC(1);
                os.writeC(getItem().get_regist_freeze());
            }

            if (getItem().get_regist_stone() != 0 && (getMr() == 0)) {
                os.writeC(33);
                os.writeC(2);
                os.writeH(getItem().get_regist_stone());
            }
            if (getItem().get_regist_sleep() != 0 && (getMr() == 0)) {
                os.writeC(33);
                os.writeC(3);
                os.writeH(getItem().get_regist_sleep());
            }
            if (getItem().get_regist_blind() != 0 && (getMr() == 0)) {
                os.writeC(33);
                os.writeC(4);
                os.writeH(getItem().get_regist_blind());
            }
            /** 恐怖耐性 **/
            if (getItem().get_regist_DESPERADO() != 0 && (getMr() == 0)) {
                os.writeC(39);
                os.writeS("恐怖耐性+" + getItem().get_regist_DESPERADO());
            }

            /** スナップファーリングスタン耐性表示**/ //県全体と一般ユーザーの格差を減らすためにスタン耐性579配置する必要が456に入っている。（ユーザーはわからない）
            if (itemType2 == 2 && itemId >= 22224 && itemId <= 22228 && getEnchantLevel() == 6
                    || itemId >= 222290 && itemId <= 222291 && getEnchantLevel() == 6
                    || itemId >= 222330 && itemId <= 222336 && getEnchantLevel() == 6) { // エンチャントが6であれば
                os.writeC(33);
                os.writeC(5);
                os.writeH(getItem().get_regist_stun() + 5);
            } else if (itemType2 == 2 && itemId >= 22224 && itemId <= 22228 && getEnchantLevel() == 7
                    || itemId >= 222290 && itemId <= 222291 && getEnchantLevel() == 7
                    || itemId >= 222330 && itemId <= 222336 && getEnchantLevel() == 7) { // エンチャントが7であれば
                os.writeC(33);
                os.writeC(5);
                os.writeH(getItem().get_regist_stun() + 7);
            } else if (itemType2 == 2 && itemId >= 22224 && itemId <= 22228 && getEnchantLevel() == 8
                    || itemId >= 222290 && itemId <= 222291 && getEnchantLevel() == 8
                    || itemId >= 222330 && itemId <= 222336 && getEnchantLevel() == 8) { //エンチャントが8であれば
                os.writeC(33);
                os.writeC(5);
                os.writeH(getItem().get_regist_stun() + 9);

                /** イヤリング、ネックレス+7からスタン耐性 **/
            } else if (itemType2 == 2 && getItem().getGrade() >= 0 && getItem().getGrade() <= 2 && getItem().getAccessoryProcess() == 8 || getItem().getAccessoryProcess() == 12 && getEnchantLevel() > 6) {
                switch (getEnchantLevel()) {
                    case 7:
                        os.writeC(33);
                        os.writeC(5);
                        os.writeH(getItem().get_regist_stun() + 2);
                        break;
                    case 8:
                        os.writeC(33);
                        os.writeC(5);
                        os.writeH(getItem().get_regist_stun() + 3);
                        break;
                    case 9:
                        os.writeC(33);
                        os.writeC(5);
                        os.writeH(getItem().get_regist_stun() + 4);
                        break;
                }


            } else if (getItem().get_regist_stun() != 0) {
                os.writeC(33);
                os.writeC(5);
                os.writeH(getItem().get_regist_stun());
            }
            if (getItem().get_regist_sustain() != 0 && (getMr() == 0)) {
                os.writeC(33);
                os.writeC(6);
                os.writeH(getItem().get_regist_sustain());
            }
            // 火属性
            if (getItem().get_defense_fire() != 0) {
                os.writeC(27);
                os.writeC(getItem().get_defense_fire());
            }
            // 水属性
            if (getItem().get_defense_water() != 0) {
                os.writeC(28);
                os.writeC(getItem().get_defense_water());
            }
            // 風属性
            if (getItem().get_defense_wind() != 0) {
                os.writeC(29);
                os.writeC(getItem().get_defense_wind());
            }
            // 地の属性
            if (getItem().get_defense_earth() != 0) {
                os.writeC(30);
                os.writeC(getItem().get_defense_earth());
            }

            if (itemType2 == 2 && (getItem().getType() == 8 || getItem().getType() == 12)) {// ネックレス、イヤリング
                os.writeC(39);
                os.writeS("$18956 : $18960"); // 根性
            } else if (itemType2 == 2 && (getItem().getType() == 9 || getItem().getType() == 11)) {// リング
                os.writeC(39);
                os.writeS("$18956 : $18961"); // 情熱
            } else if (itemType2 == 2 && (getItem().getType() == 10)) {// ベルト
                os.writeC(39);
                os.writeS("$18956 : $18962"); // 意志
            }
            if (itemId == 900032) { //カーツの投影幕
                int chp = getEnchantLevel() * 5 + 5;
                if (getEnchantLevel() == 8) chp = 50;
                os.writeC(39);
                os.writeS("最大HP +" + chp);
                switch (getEnchantLevel()) {
                    case 5:
                        os.writeC(39);
                        os.writeS("近距離ダメージ+1");
                        break;
                    case 6:
                        os.writeC(39);
                        os.writeS("近距離ダメージ+2");
                        os.writeC(39);
                        os.writeS("近距離クリティカル+ 1％");
                        break;
                    case 7:
                        os.writeC(39);
                        os.writeS("近距離ダメージ+3");
                        os.writeC(39);
                        os.writeS("近距離クリティカル+ 3％");
                        break;
                    case 8:
                        os.writeC(39);
                        os.writeS("近距離ダメージ+4");
                        os.writeC(39);
                        os.writeS("近距離クリティカル+ 5％");
                        break;
                }

            }
            if (itemId == 900033) { //カーツの名弓記章
                int chp = getEnchantLevel() * 5 + 5;
                if (getEnchantLevel() == 8) chp = 50;
                os.writeC(39);
                os.writeS("最大HP +" + chp);
                switch (getEnchantLevel()) {
                    case 5:
                        os.writeC(39);
                        os.writeS("遠距離ダメージ+1");
                        break;
                    case 6:
                        os.writeC(39);
                        os.writeS("遠距離ダメージ+2");
                        os.writeC(39);
                        os.writeS("遠距離クリティカル+ 1％");
                        break;
                    case 7:
                        os.writeC(39);
                        os.writeS("遠距離ダメージ+3");
                        os.writeC(39);
                        os.writeS("遠距離クリティカル+ 3％");
                        break;
                    case 8:
                        os.writeC(39);
                        os.writeS("遠距離ダメージ+4");
                        os.writeC(39);
                        os.writeS("遠距離クリティカル+ 5％");
                        break;
                }
            }

            if (itemId == 900034) { //カーツの賢者記章
                int chp = getEnchantLevel() * 5 + 5;
                if (getEnchantLevel() == 8) chp = 50;
                os.writeC(39);
                os.writeS("最大HP +" + chp);
                switch (getEnchantLevel()) {
                    case 5:
                        os.writeC(39);
                        os.writeS("近距離命中+1");
                        break;
                    case 6:
                        os.writeC(39);
                        os.writeS("近距離命中+2");
                        os.writeC(39);
                        os.writeS("魔法クリティカル+ 1％");
                        break;
                    case 7:
                        os.writeC(39);
                        os.writeS("近距離命中+3");
                        os.writeC(39);
                        os.writeS("魔法クリティカル+ 2％");
                        break;
                    case 8:
                        os.writeC(39);
                        os.writeS("近距離命中+4");
                        os.writeC(39);
                        os.writeS("魔法クリティカル+ 4％");
                        break;
                }
            }

            if (itemId == 900035) { //カーツの賢者記章
                int chp = getEnchantLevel() * 5 + 5;
                if (getEnchantLevel() == 8) chp = 50;
                os.writeC(39);
                os.writeS("最大HP +" + chp);
                switch (getEnchantLevel()) {
                    case 5:
                        os.writeC(39);
                        os.writeS("ダメージ減少+ 1");
                        break;
                    case 6:
                        os.writeC(39);
                        os.writeS("ダメージ減少+2");
                        os.writeC(39);
                        os.writeS("MR +3%");
                        break;
                    case 7:
                        os.writeC(39);
                        os.writeS("ダメージ減少+3");
                        os.writeC(39);
                        os.writeS("MR +5%");
                        break;
                    case 8:
                        os.writeC(39);
                        os.writeS("ダメージ減少+4");
                        os.writeC(39);
                        os.writeS("MR +7%");
                        break;
                }
            }
            if (getItem().getMagicName() != null && !getItem().getMagicName().equals("")) {
                os.writeC(74);
                os.writeS(getItem().getMagicName());
            }
        } // コメントする
        return os.getBytes();
    }

    private static final int _hit = 0x05;
    private static final int _dmg = 0x06;
    private static final int _bowhit = 0x18;
    private static final int _bowdmg = 0x23;
    private static final int _str = 0x08;
    private static final int _dex = 0x09;
    private static final int _con = 0x0a;
    private static final int _wis = 0x0b;
    private static final int _int = 0x0c;
    private static final int _cha1 = 0x0d;

    private static final int _mr = 0x0f;
    private static final int _sp = 0x11;

    private static final int _fire = 0x1B;
    private static final int _water = 0x1C;
    private static final int _wind = 0x1D;
    private static final int _earth = 0x1E;

    private static final int _maxhp = 0x0e;
    private static final int _maxmp = 0x20;
    private static final int _hpr = 0x25;
    private static final int _mpr = 0x26;
    private static final int _add_ac = 0x38;
    private static final int _poly = 0x47;

    public byte[] getStatusBytes(L1PcInstance pc, boolean check) {
        byte[] data = getStatusBytes();
        @SuppressWarnings("resource")
        BinaryOutputStream os = new BinaryOutputStream();
        try {
            os.write(data);

            os.writeC(0x45);

            if (check) {
                os.writeC(1);
            } else {
                os.writeC(2);
            }
            L1ArmorSets set = ArmorSetTable.getInstance().getArmorSets(getItem().getSetId());

            if (set.getAc() != 0) {
                os.writeC(_add_ac);
                os.writeC(set.getAc());
            }

            if (getItem().getItemId() == 20099) {
                os.writeC(_poly);
                os.writeH(1175);// デーモン
            } else if (getItem().getItemId() == 20100) {
                os.writeC(_poly);
                os.writeH(18692);// ジンデス
            } else if (getItem().getItemId() == 20151) {
                os.writeC(_poly);
                os.writeH(2118);//ケレニス
            } else if (getItem().getItemId() == 20118) {
                os.writeC(_poly);
                os.writeH(2117);// ケンラウヘル
            }

            if (set.getShortHitup() != 0) {
                os.writeC(_hit);
                os.writeC(set.getShortHitup());
            }
            if (set.getShortDmgup() != 0) {
                os.writeC(_dmg);
                os.writeC(set.getShortDmgup());
            }

            if (set.getLongHitup() != 0) {
                os.writeC(_bowhit);
                os.writeC(set.getLongHitup());
            }
            if (set.getLongDmgup() != 0) {
                os.writeC(_bowdmg);
                os.writeC(set.getLongDmgup());
            }

            if (set.getHpr() != 0) {
                os.writeC(_hpr);
                os.writeC(set.getHpr());
            }
            if (set.getMpr() != 0) {
                os.writeC(_mpr);
                os.writeC(set.getMpr());
            }

            if (set.getHp() != 0) {
                os.writeC(_maxhp);
                os.writeH(set.getHp());
            }
            if (set.getMp() != 0) {
                os.writeC(_maxmp);
                os.writeC(set.getMp());
            }

            if (set.getMr() != 0) {
                os.writeC(_mr);
                os.writeH(set.getMr());
            }

            if (set.getSp() != 0) {
                os.writeC(_sp);
                os.writeC(set.getSp());
            }

            if (set.getfire() != 0) {
                os.writeC(_fire);
                os.writeC(set.getfire());
            }
            if (set.getwater() != 0) {
                os.writeC(_water);
                os.writeC(set.getwater());
            }
            if (set.getwind() != 0) {
                os.writeC(_wind);
                os.writeC(set.getwind());
            }
            if (set.getearth() != 0) {
                os.writeC(_earth);
                os.writeC(set.getearth());
            }

            if (set.getStr() != 0) {
                os.writeC(_str);
                os.writeC(set.getStr());
            }
            if (set.getDex() != 0) {
                os.writeC(_dex);
                os.writeC(set.getDex());
            }
            if (set.getCon() != 0) {
                os.writeC(_con);
                os.writeC(set.getCon());
            }
            if (set.getWis() != 0) {
                os.writeC(_wis);
                os.writeC(set.getWis());
            }
            if (set.getIntl() != 0) {
                os.writeC(_int);
                os.writeC(set.getIntl());
            }
            if (set.getCha() != 0) {
                os.writeC(_cha1);
                os.writeC(set.getCha());
            }
            os.writeC(0x45);
            os.writeC(0);

            if (getItem().getType2() == 2) {
                if (getItem().getType() == 8 || getItem().getType() == 12) {
                    os.writeC(0x43);
                    os.writeC(0x2b);// 根性
                } else if (getItem().getType() == 9 || getItem().getType() == 11) {
                    os.writeC(0x43);
                    os.writeC(0x2c);// 情熱
                } else if (getItem().getType() == 10) {
                    os.writeC(0x43);
                    os.writeC(0x2d);// 意志
                } else {
                    os.writeC(0);
                    os.writeC(-1);
                }
            } else {
                os.writeC(0);
                os.writeC(0);
            }

        } catch (Exception e) {
        }
        return os.getBytes();
    }

    public byte[] getStatusBytes(L1PcInstance pc) {
        byte[] data = getStatusBytes();
        @SuppressWarnings("resource")
        BinaryOutputStream os = new BinaryOutputStream();
        try {
            os.write(data);

            L1ArmorSets set = ArmorSetTable.getInstance().getArmorSets(getItem().getSetId());

            if (set != null && getItem().getMainId() == getItem().getItemId()) {
                os.writeC(0x45);
                os.writeC(2);
                if (set.getAc() != 0) {
                    os.writeC(_add_ac);
                    os.writeC(set.getAc());
                }
                if (getItem().getItemId() == 20099) {
                    os.writeC(_poly);
                    os.writeH(1175);// デーモン
                } else if (getItem().getItemId() == 20100) {
                    os.writeC(_poly);
                    os.writeH(18692);// ジンデス
                } else if (getItem().getItemId() == 20151) {
                    os.writeC(_poly);
                    os.writeH(2118);// ケレニス
                } else if (getItem().getItemId() == 20118) {
                    os.writeC(_poly);
                    os.writeH(2117);// ケンラウヘル
                }

                if (set.getShortHitup() != 0) {
                    os.writeC(_hit);
                    os.writeC(set.getShortHitup());
                }
                if (set.getShortDmgup() != 0) {
                    os.writeC(_dmg);
                    os.writeC(set.getShortDmgup());
                }

                if (set.getLongHitup() != 0) {
                    os.writeC(_bowhit);
                    os.writeC(set.getLongHitup());
                }
                if (set.getLongDmgup() != 0) {
                    os.writeC(_bowdmg);
                    os.writeC(set.getLongDmgup());
                }

                if (set.getHpr() != 0) {
                    os.writeC(_hpr);
                    os.writeC(set.getHpr());
                }
                if (set.getMpr() != 0) {
                    os.writeC(_mpr);
                    os.writeC(set.getMpr());
                }

                if (set.getHp() != 0) {
                    os.writeC(_maxhp);
                    os.writeH(set.getHp());
                }
                if (set.getMp() != 0) {
                    os.writeC(_maxmp);
                    os.writeC(set.getMp());
                }

                if (set.getMr() != 0) {
                    os.writeC(_mr);
                    os.writeH(set.getMr());
                }

                if (set.getSp() != 0) {
                    os.writeC(_sp);
                    os.writeC(set.getSp());
                }

                if (set.getfire() != 0) {
                    os.writeC(_fire);
                    os.writeC(set.getfire());
                }
                if (set.getwater() != 0) {
                    os.writeC(_water);
                    os.writeC(set.getwater());
                }
                if (set.getwind() != 0) {
                    os.writeC(_wind);
                    os.writeC(set.getwind());
                }
                if (set.getearth() != 0) {
                    os.writeC(_earth);
                    os.writeC(set.getearth());
                }

                if (set.getStr() != 0) {
                    os.writeC(_str);
                    os.writeC(set.getStr());
                }
                if (set.getDex() != 0) {
                    os.writeC(_dex);
                    os.writeC(set.getDex());
                }
                if (set.getCon() != 0) {
                    os.writeC(_con);
                    os.writeC(set.getCon());
                }
                if (set.getWis() != 0) {
                    os.writeC(_wis);
                    os.writeC(set.getWis());
                }
                if (set.getIntl() != 0) {
                    os.writeC(_int);
                    os.writeC(set.getIntl());
                }
                if (set.getCha() != 0) {
                    os.writeC(_cha1);
                    os.writeC(set.getCha());
                }
                os.writeC(0x45);
                os.writeC(0);
            }

            if (getItem().getType2() == 2) {
                if (getItem().getType() == 8 || getItem().getType() == 12) {
                    os.writeC(0x43);
                    os.writeC(0x2b);// 根性
                } else if (getItem().getType() == 9 || getItem().getType() == 11) {
                    os.writeC(0x43);
                    os.writeC(0x2c);// 情熱
                } else if (getItem().getType() == 10) {
                    os.writeC(0x43);
                    os.writeC(0x2d);// 意志
                } else {
                    os.writeC(0);
                    os.writeC(-1);
                }
            } else {
                os.writeC(0);
                os.writeC(0);
            }

        } catch (Exception e) {
        }
        return os.getBytes();
    }

    private L1PcInstance _owner; // 実際には、装着した人.....

    class EnchantTimer implements Runnable {

        private int _skillId;
        private boolean _active = true;
        private boolean _effectClear = false;
        private long _expireTime;

        public EnchantTimer(int skillId, long expireTime) {
            _skillId = skillId;
            _expireTime = expireTime;
        }

        public int getRemainTime() {
            int remainTime = (int) (_expireTime - System.currentTimeMillis()) / 1000;

            if (remainTime < 1) {
                remainTime = 1;
            }

            return remainTime;
        }

        @Override
        public void run() {
            try {
                if (!_active) {
                    return;
                }

                ClearEffect();

            } catch (Exception e) {
            }
        }

        public void cancel() {
            _active = false;
            ClearEffect();
            // _owner.sendPackets(new S_ServerMessage(308, getLogName()));
            if (_owner != null)
                _owner.sendPackets(new S_ServerMessage(308, getLogName()));
        }

        public void ClearEffect() {
            synchronized (this) {
                if (_effectClear) {
                    return;
                }

                _effectClear = true;
            }

            switch (_skillId) {
                case L1SkillId.HOLY_WEAPON:
                    addHolyDmgByMagic(-1);
                    addHitByMagic(-1);
                    break;
                case L1SkillId.ENCHANT_WEAPON:
                    _owner.sendPackets(new S_PacketBox(S_PacketBox.BUFFICON, 747, 0, _isSecond, false));
                    addDmgByMagic(-2);
                    break;

                case L1SkillId.BLESS_WEAPON:
                    addDmgByMagic(-2);
                    addHitByMagic(-2);
                    break;

                case L1SkillId.SHADOW_FANG:
                    _owner.sendPackets(new S_PacketBox(S_PacketBox.BUFFICON, 2951, 0, false, false));
                    addDmgByMagic(-5);
                    break;
                case L1SkillId.BLESSED_ARMOR:
                    if (_owner != null && _owner.getInventory().getItem(L1ItemInstance.this.getId()) == L1ItemInstance.this
                            && isEquipped()) {
                        _owner.getAC().addAc(3);
                        _owner.sendPackets(new S_PacketBox(S_PacketBox.BUFFICON, 748, 0, false, false));
                        _owner.sendPackets(new S_OwnCharStatus(_owner));
                    }
                    addAcByMagic(-3);
                    break;

                default:
                    break;
            }

            removeSkillEffectTimer(_skillId);
        }
    }

    private int _acByMagic = 0;

    public int getAcByMagic() {
        return _acByMagic;
    }

    public void addAcByMagic(int i) {
        _acByMagic += i;
    }

    private int _dmgByMagic = 0;

    public int getDmgByMagic() {
        return _dmgByMagic;
    }

    public void addDmgByMagic(int i) {
        _dmgByMagic += i;
    }

    private int _holyDmgByMagic = 0;

    public int getHolyDmgByMagic() {
        return _holyDmgByMagic;
    }

    public void addHolyDmgByMagic(int i) {
        _holyDmgByMagic += i;
    }

    private int _hitByMagic = 0;

    public int getHitByMagic() {
        return _hitByMagic;
    }

    public void addHitByMagic(int i) {
        _hitByMagic += i;
    }

    public void setSkillArmorEnchant(L1PcInstance pc, int skillId, int skillTime) {

        if (getItem().getType2() != 2 || getItem().getType() != 2) {
            return;
        }

        L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);

        killSkillEffectTimer(skillId);

        switch (skillId) {
            case L1SkillId.BLESSED_ARMOR: {
                addAcByMagic(3);
                setEnchantMagic(skill.getCastGfx());

                if (_owner != null && _owner.getInventory().getItem(L1ItemInstance.this.getId()) == L1ItemInstance.this
                        && isEquipped()) {
                    _owner.getAC().addAc(-3);
                    _owner.sendPackets(new S_OwnCharStatus(_owner));
                }
            }
            break;
        }
        EnchantTimer timer = new EnchantTimer(skillId, System.currentTimeMillis() + skillTime);
        _skillEffect.put(skillId, timer);

        GeneralThreadPool.getInstance().schedule(timer, skillTime);
    }

    public void setSkillWeaponEnchant(L1PcInstance pc, int skillId, int skillTime) {

        if (getItem().getType2() != 1) {
            return;
        }

        L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);

        killSkillEffectTimer(skillId);

        if (skillId == L1SkillId.ENCHANT_WEAPON) {
            return;
        }

        switch (skillId) {
            case L1SkillId.HOLY_WEAPON:
                addHolyDmgByMagic(1);
                addHitByMagic(1);
                break;
            case L1SkillId.ENCHANT_WEAPON:
                addDmgByMagic(2);
                break;

            case L1SkillId.BLESS_WEAPON:
                addDmgByMagic(2);
                addHitByMagic(2);
                break;

            case L1SkillId.SHADOW_FANG:
                addDmgByMagic(5);
                break;

            default:
                break;
        }

        EnchantTimer timer = new EnchantTimer(skillId, System.currentTimeMillis() + skillTime);
        _skillEffect.put(skillId, timer);

        GeneralThreadPool.getInstance().schedule(timer, skillTime);

        setEnchantMagic(skill.getCastGfx());
        if (skillId == L1SkillId.HOLY_WEAPON) {
            setEnchantMagic(2165);
        }
    }

    private int _enchantmagic = 0;

    public int getEnchantMagic() {
        return _enchantmagic;
    }

    public void setEnchantMagic(int i) {
        _enchantmagic = i;
    }

    protected void removeSkillEffectTimer(int skillId) {
        _skillEffect.remove(skillId);
    }

    public boolean hasSkillEffectTimer(int skillId) {
        return _skillEffect.containsKey(skillId);
    }

    protected void killSkillEffectTimer(int skillId) {
        EnchantTimer timer = _skillEffect.remove(skillId);
        if (timer != null) {
            timer.cancel();
        }
    }

    public int getSkillEffectTimeSec(int skillId) {
        EnchantTimer timer = _skillEffect.get(skillId);
        if (timer == null) {
            return -1;
        }
        return timer.getRemainTime();
    }

    private L1PcInstance _itemOwner;

    public L1PcInstance getItemOwner() {
        return _itemOwner;
    }

    public void setItemOwner(L1PcInstance pc) {
        _itemOwner = pc;
    }

    public void startItemOwnerTimer(L1PcInstance pc) {
        setItemOwner(pc);
        L1ItemOwnerTimer timer = new L1ItemOwnerTimer(this, 10000);
        timer.begin();
    }

    private L1EquipmentTimer _equipmentTimer;

    public void startEquipmentTimer(L1PcInstance pc) {
        if (getRemainingTime() > 0) {
            _equipmentTimer = new L1EquipmentTimer(pc, this, 1000);
            GeneralThreadPool.getInstance().schedule(_equipmentTimer, 1000);
        }
    }

    public void stopEquipmentTimer(L1PcInstance pc) {
        if (getRemainingTime() > 0) {
            _equipmentTimer.cancel();
            _equipmentTimer = null;
        }
    }

    private boolean _isNowLighting = false;

    public boolean isNowLighting() {
        return _isNowLighting;
    }

    public void setNowLighting(boolean flag) {
        _isNowLighting = flag;
    }

    private int _DropMobId = 0;

    public int isDropMobId() {
        return _DropMobId;
    }

    public void setDropMobId(int i) {
        _DropMobId = i;
    }

    private int _keyId = 0;

    public int getKeyId() {
        return _keyId;
    }

    public void setKeyId(int i) {
        _keyId = i;
    }

    public void onEquip(L1PcInstance pc) {
        _owner = pc;
    }

    public void onUnEquip() {
        _owner = null;
    }

    public int dollSP() {
        int value = 0;
        int itemid = getItemId();
        switch (itemid) {
            case 447014:
            case 30022:
            case 30023:
            case 30024:
            case 30025:
            case 447015:
            case 510222:
                value = 1;
                break;
            case 447016:
            case 5991:
                value = 2;
                break;
            case 752:
                value = 3;
                break;
            default:
                break;
        }
        return value;
    }

    public int ShortDamage() {
        int value = 0;
        int itemid = getItemId();
        switch (itemid) {
            case 741:
                value = 1;
                break;
            case 743:
            case 500214:
            case 447012:
            case 30022:
            case 30023:
            case 30024:
            case 30025:
            case 447015:
            case 510220:
            case 5991:
                value = 2;
                break;
            case 750:
                value = 1;
                break;
            default:
                break;
        }
        return value;
    }

    public int ShortHit() {
        int value = 0;
        int itemid = getItemId();
        switch (itemid) {
            case 743:
            case 500215:
            case 510220:
            case 5991:
                value = 2;
                break;
            case 750:
                value = 1;
                break;
            default:
                break;
        }
        return value;
    }

    public int LongDamage() {
        int value = 0;
        int itemid = getItemId();
        switch (itemid) {
            case 744:
                value = 5;
                break;
            case 210105:
                value = 1;
                break;
            case 447013:
            case 30022:
            case 30023:
            case 30024:
            case 30025:
            case 447015:
            case 447017:
            case 5991:
                value = 2;
                break;
            default:
                break;
        }
        return value;
    }

    public int LongHit() {
        int value = 0;
        int itemid = getItemId();
        switch (itemid) {
            case 210070:
            case 210105:
                value = 1;
                break;
            case 500215:
            case 5991:
                value = 2;
                break;
            case 510216:
                value = 5;
                break;
            default:
                break;
        }
        return value;
    }

    public int MaxHP() {
        int value = 0;
        int itemid = getItemId();
        switch (itemid) {
            case 740:
            case 500215:
                value = 50;
                break;
            case 447012:
            case 447013:
            case 447014:
                value = 30;
                break;
            case 447016:
                value = 80;
                break;
            default:
                break;
        }
        return value;
    }

    public int MaxMP() {
        int value = 0;
        int itemid = getItemId();
        switch (itemid) {
            case 500215:
                value = 30;
                break;
            default:
                break;
        }
        return value;
    }

    public int EXPBonus() {
        int value = 0;
        int itemid = getItemId();
        switch (itemid) {
            case 746:
            case 410171:
                value = 20;
                break;
            case 510216:
            case 510217:
            case 510218:
            case 510219:
            case 751:
                value = 10;
                break;
            default:
                break;
        }
        return value;
    }

    public int HPRecovery() {
        int value = 0;
        int itemid = getItemId();
        switch (itemid) {
            case 744:
                value = 30;
                break;
            case 210086:
                value = 25;
                break;
            case 510218:
                value = 15;
                break;
            default:
                break;
        }
        return value;
    }

    public int MPRecovery() {
        int value = 0;
        int itemid = getItemId();
        switch (itemid) {
            case 41249:
            case 210071:
            case 447012:
            case 447013:
            case 447014:
            case 510217:
            case 510221:
            case 510222:
            case 751:
                value = 15;
                break;
            case 447015:
            case 410173:
                value = 10;
                break;
            case 447017:
                value = 6;
                break;
            default:
                break;
        }
        return value;
    }

    public int damageAdd() {
        int value = 0;
        int itemid = getItemId();
        switch (itemid) {
            case 41250:
            case 210072:
                value = 15;
                break;
            case 410172:
                value = 3;
                break;
            default:
                break;
        }
        return value;
    }

    public int damageReduction() {
        int value = 0;
        int itemid = getItemId();
        switch (itemid) {
            case 741:
            case 210070:
            case 510218:
            case 510219:
                value = 1;
                break;
            case 742:
            case 751:
                value = 2;
                break;
            case 3000152: // カーツ
                value = 3;
                break;
            case 746:
            case 410171:
                value = 5;
                break;
            default:
                break;
        }
        return value;
    }

    public int stunLevel() {
        int value = 0;
        int itemid = getItemId();
        switch (itemid) {
            case 743:
                value = 1;
                break;
            case 745:
                value = 2;
                break;
            default:
                break;
        }
        return value;
    }

    public int resistStun() {
        int value = 0;
        int itemid = getItemId();
        switch (itemid) {
            case 745:
            case 510220:
                value = 12;
                break;
            case 500214:
            case 752:
            case 3000150:
            case 3000151:
            case 3000152: // バポ、オルニョ、カーツ
                value = 10;
                break;
            case 410173:
                value = 8;
                break;
            default:
                break;
        }
        return value;
    }

    public String magicActivation() {
        String value = null;
        int itemid = getItemId();
        switch (itemid) {
            case 746:
            case 410171:
                value = "ヘルファイア";
                break;
            case 500213:
                value = "ヘイスト";
                break;
            case 30022:
                value = "アイスダガー";
                break;
            case 30023:
                value = "ファイアーアロー";
                break;
            case 30024:
                value = "スタラック";
                break;
            case 30025:
                value = "ウィンドカッター";
                break;
            case 510221:
                value = "コールライトニング";
                break;
            default:
                break;
        }
        return value;
    }

    public int weightGauge() {
        int value = 0;
        int itemid = getItemId();
        switch (itemid) {
            case 41248:
            case 210106:
            case 210107:
            case 210108:
            case 210109:
                value = 10;
                break;

            default:
                break;
        }
        return value;
    }

    public int dollArmor() {
        int value = 0;
        int itemid = getItemId();
        switch (itemid) {
            case 210096:
                value = 3;
                break;
            case 500213:
                value = 2;
                break;
            default:
                break;
        }
        return value;
    }

    public int dollResist_Freeze() {
        int value = 0;
        int itemid = getItemId();
        switch (itemid) {
            case 210096:
                value = 7;
                break;
            default:
                break;
        }
        return value;
    }

    public int dollResist_Hold() {
        int value = 0;
        int itemid = getItemId();
        switch (itemid) {
            case 500213:
                value = 10;
                break;
            default:
                break;
        }
        return value;
    }

    public int dollMpr() {
        int value = 0;
        int itemid = getItemId();
        switch (itemid) {
            case 210106:
            case 210107:
            case 210108:
            case 210109:
                value = 5;
                break;
            default:
                break;
        }
        return value;
    }

    public String itemGet() {
        String value = null;
        int itemid = getItemId();
        switch (itemid) {
            case 5991:
                value = "餅バスケット";
                break;

            default:
                break;
        }
        return value;
    }
}
