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

	/** 패키지상점 **/
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

	/** 패키지상점 **/
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

	/** 룸티스 푸른빛귀걸이 물약효율표시 **/
	private String RoomtisHealingPotion() {
		int lvl = getEnchantLevel();
		String in = "";
		switch(lvl){
		case 0:
			in = "물약 회복량 2% +2";
			break;
		case 1:
			in = "물약 회복량 6% +6";
			break;
		case 2:
			in = "물약 회복량 8% +8";
			break;
		case 3:
			in = "물약 회복량 10% +10";
			break;
		case 4:
			in = "물약 회복량 12% +12";
			break;
		case 5:
			in = "물약 회복량 14% +14";
			break;
		case 6:
			in = "물약 회복량 16% +16";
			break;
		case 7:
			in = "물약 회복량 18% +18";
			break;
		case 8:
			in = "물약 회복량 20% +20";
			break;
		default:
			break;
		}
		return in;
	}
	/** 룸티스 푸른빛귀걸이 물약효율표시 **/
	private String RoomtisHealingPotion11() {
		int lvl = getEnchantLevel();
		String in = "";
		switch(lvl){
		case 0:
			in = "공포 회복감소 효과 상쇄 +2%";
			break;
		case 1:
			in = "공포 회복감소 효과 상쇄 +6%";
			break;
		case 2:
			in = "공포 회복감소 효과 상쇄 +8%";
			break;
		case 3:
			in = "공포 회복감소 효과 상쇄 +10%";
			break;
		case 4:
			in = "공포 회복감소 효과 상쇄 +12%";
			break;
		case 5:
			in = "공포 회복감소 효과 상쇄 +14%";
			break;
		case 6:
			in = "공포 회복감소 효과 상쇄 +16%";
			break;
		case 7:
			in = "공포 회복감소 효과 상쇄 +18%";
			break;
		case 8:
			in = "공포 회복감소 효과 상쇄 +20%";
			break;
		default:
			break;
		}
		return in;
	}

	/** 축복받은 룸티스 푸른빛귀걸이 물약효율표시 **/
	private String RoomtisHealingPotion1() {
		int lvl = getEnchantLevel();
		String in = "";
		switch (lvl) {
		case 0:
			in = "물약 회복량 2% +2";
			break;
		case 1:
			in = "물약 회복량 6% +6";
			break;
		case 2:
			in = "물약 회복량 8% +8";
			break;
		case 3:
			in = "물약 회복량 12% +12";
			break;
		case 4:
			in = "물약 회복량 14% +14";
			break;
		case 5:
			in = "물약 회복량 16% +16";
			break;
		case 6:
			in = "물약 회복량 18% +18";
			break;
		case 7:
			in = "물약 회복량 20% +20";
			break;
		case 8:
			in = "물약 회복량 22% +22";
			break;
		default:
			break;
		}
		return in;
	}
	
	/** 룸티스 푸른빛귀걸이 물약효율표시 **/
	private String RoomtisHealingPotion12() {
		int lvl = getEnchantLevel();
		String in = "";
		switch(lvl){
		case 0:
			in = "공포 회복감소 효과 상쇄 +2%";
			break;
		case 1:
			in = "공포 회복감소 효과 상쇄 +6%";
			break;
		case 2:
			in = "공포 회복감소 효과 상쇄 +8%";
			break;
		case 3:
			in = "공포 회복감소 효과 상쇄 +12%";
			break;
		case 4:
			in = "공포 회복감소 효과 상쇄 +14%";
			break;
		case 5:
			in = "공포 회복감소 효과 상쇄 +16%";
			break;
		case 6:
			in = "공포 회복감소 효과 상쇄 +18%";
			break;
		case 7:
			in = "공포 회복감소 효과 상쇄 +20%";
			break;
		case 8:
			in = "공포 회복감소 효과 상쇄 +22%";
			break;
		default:
			break;
		}
		return in;
	}

	public int getMr() {
		int mr = _item.get_mdef();
		int itemid = getItemId();
		if (itemid == 20011 || itemid == 20110 || itemid == 120011 || itemid == 22223 || itemid == 20117 // 바포갑빠
				|| getItemId() == 22204 || itemid == 22205 || itemid == 22206 || itemid == 22207 // 린드비오르 마갑주
				|| getItemId() == 22213 || itemid == 120110 || itemid == 93001 || itemid == 490008 || itemid == 22365 || itemid == 222328
				|| getItemId() >= 222300 && getItemId() <= 222303 || itemid == 222328) {
			mr += getEnchantLevel();
		} else if (itemid == 20056 || itemid == 120056 || itemid == 220056 || itemid == 93002 || itemid == 222324 || itemid == 222325) {
			mr += getEnchantLevel() * 2;
		} else if (itemid == 20079 || itemid == 20078 || itemid == 20074 || itemid == 120074 || itemid == 20049 || itemid == 20050) {
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
				name.append("[Lv." + pet.get_level() + " " + pet.get_name() + "]HP" + pet.get_hp() + " " + npc.get_nameid());
			}
		}
	/*	if(getItem().getType2() ==1 &&getAttrEnchantLevel()>0){
			switch(getAttrEnchantLevel()){
			case 1:
				name.append("$6115");
				break;
			case 2:
				name.append("$6116");
				break;
			case 3:
				name.append("$6117");
				break;
			}
		}*/
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

	/** 속성 인챈트 **/
	public String getNumberedName(int count) {
		StringBuilder name = new StringBuilder();

		if (isIdentified()) {
			if (getItem().getType2() == 1 || getItem().getType2() == 2) {
				switch (getAttrEnchantLevel()) {
				case 1:
					name.append("$6115");
					break; // 화령1단
				case 2:
					name.append("$6116");
					break; // 화령2단
				case 3:
					name.append("$6117");
					break; // 화령3단 (불의속성)
				case 4:
					name.append("$14361");
					break; // 화령4단
				case 5:
					name.append("$14365");
					break; // 화령5단

				case 6:
					name.append("$6118");
					break; // 수령1단
				case 7:
					name.append("$6119");
					break; // 수령2단
				case 8:
					name.append("$6120");
					break; // 수령3단 (물의속성)
				case 9:
					name.append("$14362");
					break; // 수령4단
				case 10:
					name.append("$14366");
					break; // 수령5단

				case 11:
					name.append("$6121");
					break; // 풍령1단
				case 12:
					name.append("$6122");
					break; // 풍령2단
				case 13:
					name.append("$6123");
					break; // 풍령3단 (바람의속성)
				case 14:
					name.append("$14363");
					break; // 풍령4단
				case 15:
					name.append("$14367");
					break; // 풍령5단

				case 16:
					name.append("$6124");
					break; // 지령1단
				case 17:
					name.append("$6125");
					break; // 지령2단
				case 18:
					name.append("$6126");
					break; // 지령3단 (땅의속성)
				case 19:
					name.append("$14364");
					break; // 지령4단
				case 20:
					name.append("$14368");
					break; // 지령5단
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
		// 매니저
		// name.append(_item.getNameId());
		name.append(_item.getName());

//		if (isSpecialEnchantable()) {
//			for (int i = 1; i <= 3; ++i) {
//				if (getSpecialEnchant(i) == 0) {
//					break;
//				}
//
//				switch (getSpecialEnchant(i)) {
//				case CHAOS_SPIRIT:
//					name.append("[혼돈] ");
//					break;
//				case CORRUPT_SPIRIT:
//					name.append("[타락] ");
//					break;
//				case BALLACAS_SPIRIT:
//					name.append("[발라카스] ");
//					break;
//				case ANTARAS_SPIRIT:
//					name.append("[안타라스] ");
//					break;
//				case LINDBIOR_SPIRIT:
//					name.append("[린드비오르] ");
//					break;
//				case PAPURION_SPIRIT:
//					name.append("[파푸리온] ");
//					break;
//				case DEATHKNIGHT_SPIRIT:
//					name.append("[데스나이트] ");
//					break;
//				case BAPPOMAT_SPIRIT:
//					name.append("[바포메트] ");
//					break;
//				case BALLOG_SPIRIT:
//					name.append("[발록] ");
//					break;
//				case ARES_SPIRIT:
//					name.append("[아레스] ");
//					break;
//				}
//			}
//		}

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
	 * 아이템 상태로부터 서버 패킷으로 이용하는 형식의 바이트열을 생성해, 돌려준다. 1: 타격치 , 2: 인챈트 레벨, 3: 손상도, 4: 양손검, 5: 공격 성공, 6: 추가 타격 7: 왕자/공주 , 8: Str, 9: Dex, 10: Con, 11: Wiz,
	 * 12: Int, 13: Cha, 14: Hp,Mp 15: Mr, 16: 마나흡수, 17: 주술력, 18: 헤이스트효과, 19: Ac, 20: 행운, 21: 영양, 22: 밝기, 23: 재질, 24: 활 명중치, 25: 종류[writeH], 26:
	 * 레벨[writeH], 27: 불속성 28: 물속성, 29: 바람속성, 30: 땅속성, 31: 최대Hp, 32: 최대Mp, 33: 내성, 34: 생명흡수, 35: 활 타격치, 36: branch용dummy, 37: 체력회복률, 38: 마나회복률, 39: `,
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
		if(itemId==66&&itemType2==1){
			/** 클래스 착용 부분 **/
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
			os.writeS("대미지 "+getItem().getDmgSmall()+"+"+(getEnchantLevel()*2)+"/"+getItem().getDmgLarge()+"+"+(getEnchantLevel()*2));
			os.writeC(39);
			os.writeS("양손 무기");
			os.writeC(39);
			os.writeS("STR +"+getItem().get_addstr());
			os.writeC(39);
			os.writeS("추가 대미지 +"+getItem().getDmgModifier());
			os.writeC(39);
			os.writeS("근거리 명중 +"+getItem().getHitModifier());
			os.writeC(39);
			os.writeS("마법 발동: 드래곤의 일격");
			os.writeC(23);
			os.writeC(getItem().getMaterial());
			os.writeD(getWeight());
			return os.getBytes();
		}
		if (itemType2 == 0) { // etcitem
			if(인형SP() != 0){
				os.writeC(39);
				os.writeS("SP +" + 인형SP());
			}
			if(근거리대미지() != 0){
				os.writeC(39);
				os.writeS("근거리 대미지 +" + 근거리대미지());
			}
			if(근거리명중() != 0){
				os.writeC(39);
				os.writeS("근거리 명중 +" + 근거리명중());
			}
			if(원거리대미지() != 0){
				os.writeC(39);
				os.writeS("원거리 대미지 +" + 원거리대미지());
			}
			if(원거리명중() != 0){
				os.writeC(39);
				os.writeS("원거리 명중 +" + 원거리명중());
			}
			if(추가데미지() != 0){
				os.writeC(39);
				os.writeS("확률추가데미지 +" + 추가데미지());
			}
			if(데미지리덕션() != 0){
				os.writeC(39);
				os.writeS("대미지 리덕션 +" + 데미지리덕션());
			}
			if(스턴레벨() != 0){
				os.writeC(39);
				os.writeS("스턴 레벨 +" + 스턴레벨());
			}
			if(최대HP() != 0){
				os.writeC(39);
				os.writeS("최대 HP +" + 최대HP());
			}
			if(최대MP() != 0){
				os.writeC(39);
				os.writeS("최대 MP +" + 최대MP());
			}
			if(경험치보너스() != 0){
				os.writeC(39);
				os.writeS("경험치보너스 +" + 경험치보너스()+"%");
			}
			if(HP회복() != 0){
				os.writeC(39);
				os.writeS("32초마다 HP +" + HP회복()+"회복");
			}
			if(MP회복() != 0){
				os.writeC(39);
				os.writeS("64초마다 MP +" + MP회복()+"회복");
			}
			if(마법발동() != null){
				os.writeC(39);
				os.writeS("발동: " + 마법발동());
			}
			if(아이템획득() != null){
				os.writeC(39);
				os.writeS("일정시간마다 " + 아이템획득() + "획득");
			}
			if(무게게이지() != 0){
				os.writeC(39);
				os.writeS("무게게이지 +" + 무게게이지()+"%");
			}
			if(인형방어구() != 0){
				os.writeC(39);
				os.writeS("AC-" + 인형방어구());
			}
			if(인형Mpr() != 0){
				os.writeC(39);
				os.writeS("MP 회복률 +" + 인형Mpr());
			}
			if(스턴내성() != 0){
				os.writeC(39);
				os.writeS("스턴 내성 +" + 스턴내성());
			}
			if(인형홀드내성() != 0){
				os.writeC(39);
				os.writeS("홀드 내성 +" + 인형홀드내성());
			}
			if(인형동빙내성() != 0){
				os.writeC(39);
				os.writeS("동빙 내성 +" + 인형동빙내성());
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
			case 41288: // 환상 개미다리 치즈구이
				os.writeC(39);
				os.writeS("AC-1 대미지감소+5");
				break;
			case 41280: // 개미다리 치즈구이
				os.writeC(39);
				os.writeS("AC-1 대미지감소+5");
				break;
			case 41286: // 환상 곰 고기 구이
				os.writeC(39);
				os.writeS("HP+30,대미지감소+5");
				break;
			case 41278: // 곰 고기 구이
				os.writeC(39);
				os.writeS("HP+30,대미지감소+5");
				break;
			case 41289: // 환상 과일 샐러드
				os.writeC(39);
				os.writeS("MP+20");
				break;
			case 41281: // 과일 샐러드
				os.writeC(39);
				os.writeS("MP+20");
				break;
			case 41290: // 환상 과일 탕수육
				os.writeC(39);
				os.writeS("HP 회복+3");
				break;
			case 41282: // 과일 탕수육
				os.writeC(39);
				os.writeS("HP 회복+3");
				break;
			case 41285: // 환상 괴물 눈 스테이크
				os.writeC(39);
				os.writeS("모든속성 저항력+10");
				break;
			case 41277: // 괴물 눈 스테이크
				os.writeC(39);
				os.writeS("모든속성 저항력+10");
				break;
			case 41291: // 환상 멧돼지 꼬치 구이
				os.writeC(39);
				os.writeS("MR+5");
				break;
			case 41283: // 멧돼지 꼬치 구이
				os.writeC(39);
				os.writeS("MR+5");
				break;
			case 41292: // 환상 버섯스프
				os.writeC(39);
				os.writeS("경험치 획득량+1%");
				break;
			case 41284: // 버섯스프
				os.writeC(39);
				os.writeS("경험치 획득량+1%");
				break;
			case 41287: // 환상 씨호떡
				os.writeC(39);
				os.writeS("MP 회복+3");
				break;
			case 41279: // 씨호떡
				os.writeC(39);
				os.writeS("MP 회복+3");
				break;
			case 49063: // 환상 거미 다리 꼬치 구이
				os.writeC(39);
				os.writeS("SP+1");
				break;
			case 49055: // 거미 다리 꼬치 구이
				os.writeC(39);
				os.writeS("SP+1");
				break;
			case 49061: // 환상 스콜피온 구이
				os.writeC(39);
				os.writeS("HP 회복+2, MP 회복+2");
				break;
			case 49053: // 스콜피온 구이
				os.writeC(39);
				os.writeS("HP 회복+2, MP 회복+2");
				break;
			case 49058: // 환상 악어 스테이크
				os.writeC(39);
				os.writeS("최대 HP+30, 최대 MP+30");
				break;
			case 49050: // 악어 스테이크
				os.writeC(39);
				os.writeS("최대 HP+30, 최대 MP+30");
				break;
			case 49062: // 환상 일렉카둠 스튜
				os.writeC(39);
				os.writeS("MR+10");
				break;
			case 49054: // 일렉카둠 스튜
				os.writeC(39);
				os.writeS("MR+10");
				break;
			case 49057: // 환상 캐비어 카나페
				os.writeC(39);
				os.writeS("근거리 대미지+1,근거리 명중+1");
				break;
			case 49049: // 캐비어 카나페
				os.writeC(39);
				os.writeS("근거리 대미지+1,근거리 명중+1");
				break;
			case 49064: // 환상 크랩살스프
				os.writeC(39);
				os.writeS("경험치 획득량+2%");
				break;
			case 49056: // 크랩살스프
				os.writeC(39);
				os.writeS("경험치 획득량+2%");
				break;
			case 49060: // 환상 키위 패롯 구이
				os.writeC(39);
				os.writeS("원거리 대미지+1,원거리 명중+1");
				break;
			case 49052: // 키위 패롯 구이
				os.writeC(39);
				os.writeS("원거리 대미지+1,원거리 명중+1");
				break;
			case 49059: // 환상 터틀 드래곤 과자
				os.writeC(39);
				os.writeS("AC-2");
				break;
			case 49051: // 터틀 드래곤 과자
				os.writeC(39);
				os.writeS("AC-2");
				break;
			case 210057: // 환상 그리폰 구이
				os.writeC(39);
				os.writeS("최대 HP+50, 최대 MP+50");
				break;
			case 210049: // 그리폰 구이
				os.writeC(39);
				os.writeS("최대 HP+50, 최대 MP+50");
				break;
			case 210059: // 환상 대왕거북 구이
				os.writeC(39);
				os.writeS("AC-3");
				break;
			case 210051: // 대왕거북 구이
				os.writeC(39);
				os.writeS("AC-3");
				break;
			case 210061: // 환상 드레이크 구이
				os.writeC(39);
				os.writeS("SP+2, MP 회복+2");
				break;
			case 210053: // 드레이크 구이
				os.writeC(39);
				os.writeS("SP+2, MP 회복+2");
				break;
			case 210060: // 환상 레서 드래곤 날개 꼬치
				os.writeC(39);
				os.writeS("MR+15,모든속성 저항력+10");
				break;
			case 210052: // 레서 드래곤 날개 꼬치
				os.writeC(39);
				os.writeS("MR+15,모든속성 저항력+10");
				break;
			case 210063: // 환상 바실리스크 알 스프
				os.writeC(39);
				os.writeS("경험치 획득량+3%");
				break;
			case 210055: // 바실리스크 알 스프
				os.writeC(39);
				os.writeS("경험치 획득량+3%");
				break;
			case 210062: // 환상 심해어 스튜
				os.writeC(39);
				os.writeS("최대 HP+30,HP 회복+2");
				break;
			case 210054: // 심해어 스튜
				os.writeC(39);
				os.writeS("최대 HP+30,HP 회복+2");
				break;
			case 210058: // 환상 코카트리스 스테이크
				os.writeC(39);
				os.writeS("근거리 명중+2, 근거리 대미지+1");
				break;
			case 210050: // 코카트리스 스테이크
				os.writeC(39);
				os.writeS("근거리 명중+2, 근거리 대미지+1");
				break;
			case 210056: // 환상 크러스트시안 집게발 구이
				os.writeC(39);
				os.writeS("원거리 명중+2, 원거리 대미지+1");
				break;
			case 210048: // 크러스트시안 집게발 구이
				os.writeC(39);
				os.writeS("원거리 명중+2, 원거리 대미지+1");
				break;
				
			case 3000159://스프
				os.writeC(39);
				os.writeS("경험치 획득량+10%");
				os.writeC(39);
				os.writeS("공포,마법,스턴 저항+1");
				break;
			case 3000160://메티스의 요리
				os.writeC(39);
				os.writeS("근,원거리 대미지 +3");
				os.writeC(39);
				os.writeS("근,원거리 명중 +3");
				os.writeC(39);
				os.writeS("SP +3");
				os.writeC(39);
				os.writeS("근,원거리 치명타 +3");
				break;
			case 3000161://메티스의 주문서
				os.writeC(39);
				os.writeS("\\f2메티스의 풀업");
				break;
			/** 속성 화살 **/
			case 820014:
				os.writeC(39);
				os.writeS("물 속성 대미지 +3");
				break;
			case 820015:
				os.writeC(39);
				os.writeS("바람 속성 대미지 +3");
				break;
			case 820016:
				os.writeC(39);
				os.writeS("땅 속성 대미지 +3");
				break;
			case 820017:
				os.writeC(39);
				os.writeS("불 속성 대미지 +3");
				break;
			/** 속성 화살 **/
			default:
				break;
			}

		} else if (itemType2 == 1 || itemType2 == 2) { // weapon | armor
			int op_addAc = 0;
			/** 아이템 안전인챈 표시 추가 **/
			/*int SafeEnchant = getItem().get_safeenchant();
			os.writeC(39);
			if (SafeEnchant < 0) {
				SafeEnchant = 0;
			}
			os.writeS("\\fY[안전인챈 : +" + SafeEnchant + "]");*/
			
			if (itemType2 == 1) { // weapon 무기 타격치
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
								
				/** 룸티스 검은빛 귀걸이 AC표현처리 부분 **/
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
				
				/** 스냅퍼의 반지 AC부분 처리 **/
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
				/** 스냅퍼의 용사 반지 AC부분 처리 **/
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
				/** 축복받은 스냅퍼 체력,마법저항 반지 AC부분 처리 **/
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
				/** 축복받은 스냅퍼의 회복,집중,마나 반지 AC부분 처리 **/
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
				/** 축복받은 스냅퍼 용사의반지 AC부분 처리 **/
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
				/** 룸티스의 푸른빛 귀걸이 AC부분 처리 **/
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
				/** 축복받은 룸티스의 푸른빛 귀걸이 AC부분 처리 **/
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

			}else if(itemType2 == 2 && (itemId == 900032|| itemId == 900033 || itemId == 900034)){ //휘장 
				os.writeC(2);
				switch(getEnchantLevel()){
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
			}else if(itemType2 == 2 && itemId == 900035){ //휘장
				os.writeC(2);
				switch(getEnchantLevel()){
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
			


			if (getItem().isTwohandedWeapon()) { // 양손무기
				os.writeC(4);
			}

			if (get_durability() != 0) { // 손상도
				os.writeC(3);
				os.writeC(get_durability());
				// os.writeC(39);
				// os.writeS("\\aG손상도 "+get_durability());
			}

			/** 클래스 착용 부분 **/
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
					os.writeS("\\f2특화 : 추가데미지 + 5");
				}
				if (itemType2 == 2 && getSpecialEnchant() == 1 && (!(getItem().getType() >= 8 && getItem().getType() <= 12))) {
					os.writeC(39);
					os.writeS("\\f2특화 : 대미지 리덕션 + 1");
				}
			}

			/** 55레벨 엘릭서 룬 옵션 표시 **/
			if (itemId == 222295) { // 민첩의 엘릭서
				switch (_cha.getType()) {
				case 0:
					os.writeC(39);
					os.writeS("대미지 리덕션 +3");
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
					os.writeS("MP 회복 +3");
					break;
				case 4:
					os.writeC(39);
					os.writeS("AC-3");
					break;
				case 5:
					os.writeC(39);
					os.writeS("근거리 명중 +3");
					break;
				case 6:
					os.writeC(39);
					os.writeS("무게 게이지 +5%");
					break;
				}
			}
			if (itemId == 222296) { // 체력의 엘릭서
				switch (_cha.getType()) {
				case 0:
					os.writeC(39);
					os.writeS("대미지 리덕션 +3");
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
					os.writeS("MP 회복 +3");
					break;
				case 4:
					os.writeC(39);
					os.writeS("AC-3");
					break;
				case 5:
					os.writeC(39);
					os.writeS("근거리 명중 +3");
					break;
				case 6:
					os.writeC(39);
					os.writeS("무게 게이지 +5%");
					break;
				}
			}
			if (itemId == 222297) { // 지식의 엘릭서
				switch (_cha.getType()) {
				case 0:
					os.writeC(39);
					os.writeS("대미지 리덕션 +3");
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
					os.writeS("MP 회복 +3");
					break;
				case 4:
					os.writeC(39);
					os.writeS("AC-3");
					break;
				case 5:
					os.writeC(39);
					os.writeS("근거리 명중 +3");
					break;
				case 6:
					os.writeC(39);
					os.writeS("무게 게이지 +5%");
					break;
				}
			}
			if (itemId == 222298) { // 지혜의 엘릭서
				switch (_cha.getType()) {
				case 0:
					os.writeC(39);
					os.writeS("대미지 리덕션 +3");
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
					os.writeS("MP 회복 +3");
					break;
				case 4:
					os.writeC(39);
					os.writeS("AC-3");
					break;
				case 5:
					os.writeC(39);
					os.writeS("근거리 명중 +3");
					break;
				case 6:
					os.writeC(39);
					os.writeS("무게 게이지 +5%");
					break;
				}
			}
			if (itemId == 222299) { // 힘의 엘릭서
				switch (_cha.getType()) {
				case 0:
					os.writeC(39);
					os.writeS("대미지 리덕션 +3");
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
					os.writeS("MP 회복 +3");
					break;
				case 4:
					os.writeC(39);
					os.writeS("AC-3");
					break;
				case 5:
					os.writeC(39);
					os.writeS("근거리 명중 +3");
					break;
				case 6:
					os.writeC(39);
					os.writeS("무게 게이지 +5%");
					break;
				}
			}

			/** 70레벨 엘릭서 룬 옵션 표시 **/
			if (itemId == 222312) { // 민첩의 엘릭서
				switch (_cha.getType()) {
				case 0:
					os.writeC(39);
					os.writeS("대미지 리덕션 +3");
					os.writeC(39);
					os.writeS("근거리 명중+2");
					break;
				case 1:
					os.writeC(39);
					os.writeS("최대 HP +50");
					os.writeC(39);
					os.writeS("근거리 대미지+1");
					break;
				case 2:
					os.writeC(39);
					os.writeS("최대 MP +50");
					os.writeC(39);
					os.writeS("원거리 대미지+1");
					break;
				case 3:
					os.writeC(39);
					os.writeS("MP 회복 +3");
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
					os.writeS("근거리 명중 +3");
					os.writeC(39);
					os.writeS("대미지 리덕션+1");
					break;
				case 6:
					os.writeC(39);
					os.writeS("무게 게이지 +5%");
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
			if (itemId == 222313) { // 체력의 엘릭서
				switch (_cha.getType()) {
				case 0:
					os.writeC(39);
					os.writeS("대미지 리덕션 +3");
					os.writeC(39);
					os.writeS("근거리 명중+2");
					break;
				case 1:
					os.writeC(39);
					os.writeS("최대 HP +50");
					os.writeC(39);
					os.writeS("근거리 대미지+1");
					break;
				case 2:
					os.writeC(39);
					os.writeS("최대 MP +50");
					os.writeC(39);
					os.writeS("원거리 대미지+1");
					break;
				case 3:
					os.writeC(39);
					os.writeS("MP 회복 +3");
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
					os.writeS("근거리 명중 +3");
					os.writeC(39);
					os.writeS("대미지 리덕션+1");
					break;
				case 6:
					os.writeC(39);
					os.writeS("무게 게이지 +5%");
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
			if (itemId == 222314) { // 지식의 엘릭서
				switch (_cha.getType()) {
				case 0:
					os.writeC(39);
					os.writeS("대미지 리덕션 +3");
					os.writeC(39);
					os.writeS("근거리 명중+2");
					break;
				case 1:
					os.writeC(39);
					os.writeS("최대 HP +50");
					os.writeC(39);
					os.writeS("근거리 대미지+1");
					break;
				case 2:
					os.writeC(39);
					os.writeS("최대 MP +50");
					os.writeC(39);
					os.writeS("원거리 대미지+1");
					break;
				case 3:
					os.writeC(39);
					os.writeS("MP 회복 +3");
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
					os.writeS("근거리 명중 +3");
					os.writeC(39);
					os.writeS("대미지 리덕션+1");
					break;
				case 6:
					os.writeC(39);
					os.writeS("무게 게이지 +5%");
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
			if (itemId == 222315) { // 지혜의 엘릭서
				switch (_cha.getType()) {
				case 0:
					os.writeC(39);
					os.writeS("대미지 리덕션 +3");
					os.writeC(39);
					os.writeS("근거리 명중+2");
					break;
				case 1:
					os.writeC(39);
					os.writeS("최대 HP +50");
					os.writeC(39);
					os.writeS("근거리 대미지+1");
					break;
				case 2:
					os.writeC(39);
					os.writeS("최대 MP +50");
					os.writeC(39);
					os.writeS("원거리 대미지+1");
					break;
				case 3:
					os.writeC(39);
					os.writeS("MP 회복 +3");
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
					os.writeS("근거리 명중 +3");
					os.writeC(39);
					os.writeS("대미지 리덕션+1");
					break;
				case 6:
					os.writeC(39);
					os.writeS("무게 게이지 +5%");
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
			if (itemId == 222316) { // 힘의 엘릭서
				switch (_cha.getType()) {
				case 0:
					os.writeC(39);
					os.writeS("대미지 리덕션 +3");
					os.writeC(39);
					os.writeS("근거리 명중+2");
					break;
				case 1:
					os.writeC(39);
					os.writeS("최대 HP +50");
					os.writeC(39);
					os.writeS("근거리 대미지+1");
					break;
				case 2:
					os.writeC(39);
					os.writeS("최대 MP +50");
					os.writeC(39);
					os.writeS("원거리 대미지+1");
					break;
				case 3:
					os.writeC(39);
					os.writeS("MP 회복 +3");
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
					os.writeS("근거리 명중 +3");
					os.writeC(39);
					os.writeS("대미지 리덕션+1");
					break;
				case 6:
					os.writeC(39);
					os.writeS("무게 게이지 +5%");
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

			/** 스냅퍼의 반지 추타 표기 **/
			if (itemType2 == 2 && itemId >= 22224 && itemId <= 22228 && getEnchantLevel() > 4 || itemId == 222291 && getEnchantLevel() > 4) {
				os.writeC(6);
				os.writeC(getItem().getDmgModifier() + getEnchantLevel() - 4);
				/** 축복받은 스냅퍼의 반지 추타 표기 **/
			} else if (itemType2 == 2 && itemId >= 222330 && itemId <= 222334 && getEnchantLevel() > 3 || itemId == 222336 && getEnchantLevel() > 3) {
				os.writeC(6);
				os.writeC(getItem().getDmgModifier() + getEnchantLevel() - 3);
			} else if (getItem().getDmgModifier() != 0) {
				os.writeC(6);
				os.writeC(getItem().getDmgModifier());
			}
			/** 스냅퍼의 용사 반지 무기 명중 **/
			if (itemType2 == 2 && itemId == 222291 && getEnchantLevel() > 4) {
				os.writeC(5);
				os.writeC(getItem().getHitModifier() + getEnchantLevel() - 4);
				/** 축복받은 스냅퍼의 용사 반지 무기 명중 **/
			} else if (itemType2 == 2 && itemId == 222336 && getEnchantLevel() > 3) {
				os.writeC(5);
				os.writeC(getItem().getHitModifier() + getEnchantLevel() - 3);
				/*** 격분의 장갑 무기 명중 ***/
			} else if (itemType2 == 2 && itemId == 222317 && getEnchantLevel() >= 4 && getEnchantLevel() <= 6) {
				os.writeC(5);
				os.writeC(getItem().getHitModifier() + getEnchantLevel() - 4);
			} else if (itemType2 == 2 && itemId == 222317 && getEnchantLevel() > 6) {
				os.writeC(5);
				os.writeC(getItem().getHitModifier() + getEnchantLevel() - 3);
				/** 수호성의 파워 글로브 근거리 명중 **/
			} else if (itemType2 == 2 && itemId == 222345 && getEnchantLevel() > 4) {
				os.writeC(5);
				os.writeC(getItem().getHitModifier() + getEnchantLevel() - 4);
			} else if (getItem().getHitRate() != 0) { // 방어구 에 붙는 명중
				os.writeC(5);
				os.writeC(getItem().getHitRate());
			} else if (getItem().getHitModifier() != 0) { // 무기에 붙는 명중
				if (itemType2 == 1 && getItem().getType1() != 20) {
					os.writeC(5);
					os.writeC(getItem().getHitModifier());
				} else {
					os.writeC(24);
					os.writeC(getItem().getHitModifier());
				}
			}

			if (getItem().getDmgRate() != 0) {
				os.writeC(6);// 추가데미지
				os.writeC(getItem().getDmgRate());
			}
			/** 수호성의 활 골무 원거리 명중 **/
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
			
			/** 화룡의 티셔츠 **/
			if (itemType2 == 2 && itemId == 491006) {
				os.writeC(6); // 추가데미지
				if (getEnchantLevel() >= 9)
					os.writeC(2);
				else
					os.writeC(1);
			}

			/** 반지 5이상 추가 대미지 **/
			if (itemType2 == 2 && getItem().getGrade() >= 0 && getItem().getGrade() <= 2 && getItem().getAccessoryProcess() == 9 || getItem().getAccessoryProcess() == 11) {
				if (getEnchantLevel() > 4) {
					os.writeC(39);
					os.writeS("근거리 대미지 +" + (getEnchantLevel() - 4));
					os.writeC(39);
					os.writeS("원거리 대미지 +" + (getEnchantLevel() - 4));
				}
			}

			/** 반지 7이상 옵션 스펠파워 리뉴얼 **/
			if (itemType2 == 2 && getItem().getGrade() >= 0 && getItem().getGrade() <= 2 && getItem().getAccessoryProcess() == 9 || getItem().getAccessoryProcess() == 11) {
				if (getEnchantLevel() >= 7) {
					os.writeC(17);
					os.writeC(getEnchantLevel() - 6);
				}
			}

			/** 스냅퍼의 반지 PvP추가대미지 표시 (현질러와 일반유저의 격차때문에 실제로 대미지는 들어있지않음) **/
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
					|| itemId >= 222330 && itemId <= 222336 && getEnchantLevel() == 7) { // 인챈트가 7이면
				os.writeC(59);
				os.writeC(getEnchantLevel() - 6);
				// os.writeC(39);
				// os.writeS("PvP 추가 대미지 +1");
			} else if (itemType2 == 2 && itemId >= 22224 && itemId <= 22228 && getEnchantLevel() == 8
					|| itemId >= 222290 && itemId <= 222291 && getEnchantLevel() == 8
					|| itemId >= 222330 && itemId <= 222336 && getEnchantLevel() == 8) { // 인챈트가 8이면
				os.writeC(59);
				os.writeC(getEnchantLevel() - 6);
				// os.writeC(39);
				// os.writeS("PvP 추가 대미지 +2");
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

			/** 스냅퍼의 반지류 HP증가 표시 **/
			if (itemType2 == 2 && itemId >= 22224 && itemId <= 22228 && getEnchantLevel() > 0) {
				int 스냅퍼HP증가 = getEnchantLevel() * 5 + 10;
				os.writeC(14);
				os.writeH(getItem().get_addhp() + 스냅퍼HP증가);

				/** 축복받은 스냅퍼의 체력 반지 HP증가 표시 **/
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
				/** 축복받은 스냅퍼의 마법저항,집중,마나 반지 HP증가 표시 **/
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
				/** 스냅퍼의 지혜 반지 HP증가 표시 **/
			} else if (itemType2 == 2 && itemId == 222290 && getEnchantLevel() > 0) {
				int 지혜반지HP증가 = (getEnchantLevel() * 5);
				os.writeC(14);
				os.writeH(getItem().get_addhp() + 지혜반지HP증가);
				/** 축복받은 스냅퍼의 지혜 반지 HP증가 표시 **/
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
				/** 스냅퍼의 용사 반지 HP증가 표시 **/
			} else if (itemType2 == 2 && itemId == 222291 && getEnchantLevel() >= 3) {
				int 용사반지HP증가 = (getEnchantLevel() - 2) * 5;
				os.writeC(14);
				os.writeH(getItem().get_addhp() + 용사반지HP증가);
				/** 축복받은 스냅퍼의 용사 반지 HP증가 표시 **/
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
				/** 룸티스 붉은빛 귀걸이 HP증가 표시 **/
			} else if (itemType2 == 2 && itemId == 22229 && getEnchantLevel() > 0) {
				int 붉귀HP증가 = (getEnchantLevel() * 10) + 10;
				os.writeC(14);
				os.writeH(getItem().get_addhp() + 붉귀HP증가);
				/** 축복받은 룸티스 붉은빛 귀걸이 HP증가 표시 **/
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
				/** 체력의 가더 HP증가 표시 **/
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
				/** 반지 귀걸이 목걸이 인챈트 HP증가 표시 **/
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
				/** 벨트 6부터 HP 표시 **/
			} else if (itemType2 == 2 && (getItem().getType() == 10 && getEnchantLevel() > 5)) {// 벨트
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
			//유니 각반
			if (itemType2 == 2 &&  getEnchantLevel() >= 9 && (itemId >= 900027 && itemId <= 900029)) {		
				switch(itemId){
				case 900027://민첩
					os.writeC(35);//원거리
					os.writeC(1);
					break;
				case 900028://완력
					os.writeC(47);//근거리
					os.writeC(1);
					break;
				case 900029://지식
					os.writeC(17); //SP
					os.writeC(1);
					break;
				}
			}

			/** 벨트 인챈트 MP증가 표시 **/
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
				/** 룸티스의 보랏빛 귀걸이 MP증가 표시 **/
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
				/** 축복받은 룸티스의 보랏빛 귀걸이 MP증가 표시 **/
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
					os.writeS("최대 MP +130");
					break;
				default:
					break;
				}
				/** 대마법사의 모자 인챈트 MP증가 표시 **/
			} else if (itemId == 202022) {
				os.writeC(32);
				os.writeH(getItem().get_addmp() + (getEnchantLevel() * 10));
				/** MP패킷변경됨 C/H **/
			} else if (addmp() != 0) {
				os.writeC(32);
				os.writeH(addmp());// mp부분패킷변경됨
			}

			// 피틱 표시
			if (getItem().get_addhpr() != 0) {
				os.writeC(37);
				os.writeC(getItem().get_addhpr());
			}

			// 엠틱 표시
			if (itemId == 1134 || itemId == 101134) {
				os.writeC(38);
				os.writeC(getItem().get_addmpr() + getEnchantLevel()); // 명상의 지팡이
			} else if (getItem().get_addmpr() != 0) {
				os.writeC(38);
				os.writeC(getItem().get_addmpr());
			}

			/**룸티스의 푸른빛 귀걸이 물약효율 표시**/
			if(itemType2 == 2 && itemId == 22230 && getEnchantLevel() >= 0){
				os.writeC(39); 
				os.writeS(RoomtisHealingPotion());
				os.writeC(39); 
				os.writeS(RoomtisHealingPotion11());
			}
			
			/** 축복받은 룸티스의 푸른빛 귀걸이 물약효율 표시 **/
			if (itemType2 == 2 && itemId == 222338 && getEnchantLevel() >= 0) {
				os.writeC(39);
				os.writeS(RoomtisHealingPotion1());
				os.writeC(39); 
				os.writeS(RoomtisHealingPotion12());
			}
			/** 목걸이 5이상 물약 회복량 **/
			if (itemType2 == 2 && getItem().getGrade() >= 0 && getItem().getGrade() <= 2 && getItem().getAccessoryProcess() == 8 || getItem().getAccessoryProcess() == 12) {
				if (getEnchantLevel() > 4) {
					os.writeC(39);
					os.writeS("물약 회복량 " + ((getEnchantLevel() - 4) * 2) + "% +0");
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
						os.writeS("추가 대미지 확률+20(2%)");
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
						os.writeS("추가 대미지 확률+20(3%)");
					} else {
						os.writeC(6);
						os.writeC(getEnchantLevel() - 3);
						os.writeC(35);
						os.writeC(getEnchantLevel() - 3);
						os.writeC(39);
						os.writeS("추가 대미지 확률+20(2%)");
					}
				} else if (getEnchantLevel() == 6) {
					if (getItemId() == 222341) {
						os.writeC(6);
						os.writeC(getEnchantLevel() - 2);
						os.writeC(35);
						os.writeC(getEnchantLevel() - 2);
						os.writeC(39);
						os.writeS("추가 대미지 확률+20(4%)");
					} else {
						os.writeC(6);
						os.writeC(getEnchantLevel() - 3);
						os.writeC(35);
						os.writeC(getEnchantLevel() - 3);
						os.writeC(39);
						os.writeS("추가 대미지 확률+20(3%)");
					}
				} else if (getEnchantLevel() == 7) {
					if (getItemId() == 222341) {
						os.writeC(6);
						os.writeC(getEnchantLevel() - 2);
						os.writeC(35);
						os.writeC(getEnchantLevel() - 2);
						os.writeC(39);
						os.writeS("추가 대미지 확률+20(5%)");
					} else {
						os.writeC(6);
						os.writeC(getEnchantLevel() - 3);
						os.writeC(35);
						os.writeC(getEnchantLevel() - 3);
						os.writeC(39);
						os.writeS("추가 대미지 확률+20(4%)");
					}
				} else if (getEnchantLevel() == 8) {
					if (getItemId() == 222341) {
						os.writeC(6);
						os.writeC(getEnchantLevel() - 2);
						os.writeC(35);
						os.writeC(getEnchantLevel() - 2);
						os.writeC(39);
						os.writeS("추가 대미지 확률+20(6%)");
					} else {
						os.writeC(6);
						os.writeC(getEnchantLevel() - 3);
						os.writeC(35);
						os.writeC(getEnchantLevel() - 3);
						os.writeC(39);
						os.writeS("추가 대미지 확률+20(5%)");
					}
				}
			}
			/** 축복받은 스냅퍼의 마법 저항 반지 MR표시 **/
			if (itemType2 == 2 && itemId == 222334 && getEnchantLevel() > 5) {
				os.writeC(15);
				os.writeH(getMr() + (getEnchantLevel() - 5));
				
			
				/** 룸티스의 보랏빛 귀걸이 마방 표시 **/
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
				/** 축복받은 룸티스의 보랏빛 귀걸이 마방 표시 **/
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

				/** 지룡의 티셔츠 **/
			}else if (itemType2 == 2 &&itemId == 900023 && getEnchantLevel() > 4) { 
				switch(getEnchantLevel()){
				case 5: os.writeC(15); os.writeH(getMr() + 4);break;
				case 6: os.writeC(15); os.writeH(getMr() + 5);break;
				case 7: os.writeC(15); os.writeH(getMr() + 6);break;
				case 8: os.writeC(15); os.writeH(getMr() + 8);break;
				case 9: os.writeC(15); os.writeH(getMr() + 11);break;
				case 10: os.writeC(15); os.writeH(getMr() + 14);break;
				case 11: os.writeC(15); os.writeH(getMr() + 14);break;
				default: break;
				}
				
				/** 반지 6부터 마방표시 **/
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
				/** 흑기사의 면갑 MR표시 **/
				/** 신성한 엘름의 축복 MR표시 **/
			} else if (itemType2 == 2 && itemId == 900027 || itemId == 910025 && getEnchantLevel() > 4) {
				switch(getEnchantLevel()){
				case 5: os.writeC(15); os.writeH(getMr() + 4);break;
				case 6: os.writeC(15); os.writeH(getMr() + 8);break;
				case 7: os.writeC(15); os.writeH(getMr() + 12);break;
				case 8: os.writeC(15); os.writeH(getMr() + 16);break;
				case 9: os.writeC(15); os.writeH(getMr() + 20);break;
				case 10: os.writeC(15); os.writeH(getMr() + 24);break;
				case 11: os.writeC(15); os.writeH(getMr() + 28);break;
				default: break;
				}
			} else if (getMr() != 0) { // MR
				os.writeC(15);
				os.writeH(getMr());
			}	
			/** 지룡의 티셔츠 **/
			if(itemType2 == 2 && itemId == 900023){ 
				os.writeC(63); //대미지감소
				if(getEnchantLevel() >= 9)
					os.writeC(2);
				else
					os.writeC(1);
			}
			/** 지룡의 티셔츠 **/
			if(itemType2 == 2 && itemId == 900024){ 
				os.writeC(47); //근거리대미지
				if(getEnchantLevel() >= 9)
					os.writeC(2);
				else
					os.writeC(1);
			}

			/** 풍룡의 티셔츠 **/
			if(itemType2 == 2 && itemId == 900025){ 
				os.writeC(35);//원거리
				if(getEnchantLevel() >= 9)
					os.writeC(2);
				else
					os.writeC(1);
			}
			
			/** 수룡의 티셔츠 **/
			if(itemType2 == 2 && itemId == 900026){ 
				os.writeC(17); // sp
				if(getEnchantLevel() >= 9)
					os.writeC(2);
				else
					os.writeC(1);
			}
			


			/** 수룡/화룡/풍룡 티셔츠 **/
			if((itemId == 900026 || itemId == 900024 || itemId == 900025) && getEnchantLevel() >= 5) { //수룡 화룡 풍룡 스턴 내성
				os.writeC(33);
				os.writeC(5);
				switch(getEnchantLevel()){
				case 5: 	os.writeC(8);break;
				case 6:  	os.writeC(9);break;
				case 7: 	os.writeC(10);break;
				case 8:  	os.writeC(12);break;
				case 9: 	os.writeC(15);break;
				case 10: 	os.writeC(18);break;
				case 11: 	os.writeC(18);break;
				default: break;
				}
			}

			/** 수룡/화룡/풍룡 티셔츠 **/
			if((itemId == 900026 || itemId == 900024 || itemId == 900025|| itemId == 900023) && getEnchantLevel() >= 10) { //수룡 화룡 풍룡 스턴 내성
				os.writeC(39);
				os.writeS("HP +100");	
				os.writeC(39);
				os.writeS("\\f2PVP 대미지 증가+ " + 1);
				os.writeC(39);
				os.writeS("\\f2PVP 대미지 감소+ " + 1);
			}

			/** 스냅퍼의 지혜 반지 SP표시 **/
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
				/** 축복받은 스냅퍼의 지혜 반지 SP표시 **/
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
				/** 룸티스의 보랏빛 귀걸이 SP표시 **/
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
				/** 축복받은 룸티스의 보랏빛 귀걸이 SP표시 **/
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
				/** 마법사의 가더 SP표시 **/
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
				/** 리치 로브 SP표시 **/
			} else if (itemType2 == 2 && itemId == 20107 && getEnchantLevel() >= 3) {
				os.writeC(17);
				os.writeC(getItem().get_addsp() + getEnchantLevel() - 2);
			} else if (addsp() != 0) {
				if(itemType2 == 1 && itemId == 134){
					os.writeC(17);
					os.writeC(addsp()+getEnchantLevel());
				}else{
				os.writeC(17);
				os.writeC(addsp());
				}
			}

			if (getItem().isHasteItem()) {
				os.writeC(18);
			}

			// 마나 흡수
			if (itemId == 126 || itemId == 127) {
				os.writeC(16);
			}

			// 피 흡수
			if (itemId == 12 || itemId == 601 || itemId == 1123 || itemId == 202013) {
				os.writeC(34);
			}

			/** 벨트 5이상 대미지 리덕션 **/
			if (itemType2 == 2 && (getItem().getType() == 10 && getEnchantLevel() >= 5)) {// 벨트
				os.writeC(39);
				os.writeS("대미지 리덕션 " + (getEnchantLevel() - 4));
			}
			/** 룸티스의 붉은빛 귀걸이 대미지 리덕션 **/
			if (getItemId() == 22229 && getEnchantLevel() > 2) {
				switch (getEnchantLevel()) {
				case 3:
				case 4:
					os.writeC(39);
					os.writeS("대미지 리덕션 " + (getItem().getDamageReduction() + 1));
					break;
				case 5:
					os.writeC(39);
					os.writeS("대미지 리덕션 " + (getItem().getDamageReduction() + 2));
					break;
				case 6:
					os.writeC(39);
					os.writeS("대미지 리덕션 " + (getItem().getDamageReduction() + 3));
					break;
				case 7:
					os.writeC(39);
					os.writeS("대미지 리덕션 " + (getItem().getDamageReduction() + 4));
					break;
				case 8:
					os.writeC(39);
					os.writeS("대미지 리덕션 " + (getItem().getDamageReduction() + 5));
					break;
				default:
					break;
				}
			}
			/** 축복받은 룸티스의 붉은빛 귀걸이 대미지 리덕션 **/
			if (itemId == 222337 && getEnchantLevel() > 2) {
				switch (getEnchantLevel()) {
				case 3:
					os.writeC(39);
					os.writeS("대미지 리덕션 " + (getItem().getDamageReduction() + 1));
					break;
				case 4:
					os.writeC(39);
					os.writeS("대미지 리덕션 " + (getItem().getDamageReduction() + 2));
					break;
				case 5:
					os.writeC(39);
					os.writeS("대미지 리덕션 " + (getItem().getDamageReduction() + 3));
					break;
				case 6:
					os.writeC(39);
					os.writeS("대미지 리덕션 " + (getItem().getDamageReduction() + 4));
					break;
				case 7:
					os.writeC(39);
					os.writeS("대미지 리덕션 " + (getItem().getDamageReduction() + 5));
					break;
				case 8:
					os.writeC(39);
					os.writeS("대미지 리덕션 " + (getItem().getDamageReduction() + 6));
					break;
				default:
					break;
				}
			}
			/** 수호의 가더 대미지 리덕션 **/
			if (itemId == 22254 && getEnchantLevel() > 4) {
				switch (getEnchantLevel()) {
				case 5: case 6:
					os.writeC(39);
					os.writeS("대미지 리덕션 +" + (getItem().getDamageReduction() + 0));
					break;
				case 7:	case 8:
					os.writeC(39);
					os.writeS("대미지 리덕션 +" + (getItem().getDamageReduction() + 1));
					break;
				case 9:
					os.writeC(39);
					os.writeS("대미지 리덕션 +" + (getItem().getDamageReduction() + 2));
					break;
				default:
					break;
				}
			}
			/** 귀걸이,목걸이 +6부터 AC부분처리**/
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
			
			/** 나이트발드의 양손검 스턴적중+5 **/
			if (itemId == 1121 || itemId == 11121) {
				switch (getEnchantLevel()) {
				case 0: case 1: case 2: case 3: case 4: case 5: case 6:
				case 7: case 8: case 9: case 10: case 11:
				case 12: case 13: case 14: case 15:
					os.writeC(39);
					os.writeS("스턴 적중 +" + (getItem().get_regist_stun() + 5));
					break;
				default:
					break;
				}
			}
			
			/** 회복의 문장 **/
			if (itemId == 900021) {
				switch (getEnchantLevel()) {
				case 0:
					os.writeC(39);
					os.writeS("물약 회복량 2% +2 %");
					os.writeC(39);
					os.writeS("공포 회복감소 효과 상쇄 + 2%");
					break;
				case 1:
					os.writeC(39);
					os.writeS("물약 회복량 4% +4 %");
					os.writeC(39);
					os.writeS("공포 회복감소 효과 상쇄 + 4%");
					break;
				case 2:
					os.writeC(39);
					os.writeS("물약 회복량 6% +6 %");
					os.writeC(39);
					os.writeS("공포 회복감소 효과 상쇄 + 6%");
					break;
				case 3:
					os.writeC(39);
					os.writeS("물약 회복량 8% +8 %");
					os.writeC(39);
					os.writeS("공포 회복감소 효과 상쇄 + 8%");
					break;
				case 4:
					os.writeC(39);
					os.writeS("물약 회복량 10% +10 %");
					os.writeC(39);
					os.writeS("공포 회복감소 효과 상쇄 + 10%");
					break;
				case 5:
					os.writeC(39);
					os.writeS("물약 회복량 12% +12 %");
					os.writeC(39);
					os.writeS("공포 회복감소 효과 상쇄 + 12%");
					break;
				case 6:
					os.writeC(39);
					os.writeS("물약 회복량 14% +14 %");
					os.writeC(39);
					os.writeS("공포 회복감소 효과 상쇄 + 14%");
					break;
				case 7:
					os.writeC(39);
					os.writeS("물약 회복량 16% +16 %");
					os.writeC(39);
					os.writeS("공포 회복감소 효과 상쇄 + 16%");
					break;
				case 8:
					os.writeC(39);
					os.writeS("물약 회복량 18% +18 %");
					os.writeC(39);
					os.writeS("공포 회복감소 효과 상쇄 + 18%");
					break;
				case 9:
					os.writeC(39);
					os.writeS("물약 회복량 20% +20 %");
					os.writeC(39);
					os.writeS("공포 회복감소 효과 상쇄 + 20%");
					break;
				case 10:
					os.writeC(39);
					os.writeS("물약 회복량 22% +22 %");
					os.writeC(39);
					os.writeS("공포 회복감소 효과 상쇄 + 22%");
					break;
				default:
					break;
				}
			}
				
			/** 성장의 문장 **/
			if (itemId == 900020) {
				switch (getEnchantLevel()) {
				case 0:
				os.writeC(39);
				os.writeS("exp 보너스 " + (getItem().get_addexp1() + 1+"%"));
				break;
				case 1:
				os.writeC(39);
				os.writeS("exp 보너스 " + (getItem().get_addexp1() + 2+"%"));
				break;
				case 2:
				os.writeC(39);
				os.writeS("exp 보너스 " + (getItem().get_addexp1() + 3+"%"));
				break;
				case 3:
				os.writeC(39);
				os.writeS("exp 보너스 " + (getItem().get_addexp1() + 4+"%"));
				break;
				case 4:
				os.writeC(39);
				os.writeS("exp 보너스 " + (getItem().get_addexp1() + 5+"%"));
				break;
				case 5:
				os.writeC(39);
				os.writeS("exp 보너스 " + (getItem().get_addexp1() + 6+"%"));
				break;
				case 6:
				os.writeC(39);
				os.writeS("exp 보너스 " + (getItem().get_addexp1() + 7+"%"));
				break;
				case 7:
				os.writeC(39);
				os.writeS("exp 보너스 " + (getItem().get_addexp1() + 9+"%"));
				break;
				case 8:
				os.writeC(39);
				os.writeS("exp 보너스 " + (getItem().get_addexp1() + 11+"%"));
				break;
				case 9:
				os.writeC(39);
				os.writeS("exp 보너스 " + (getItem().get_addexp1() + 13+"%"));
				break;
				case 10:
				os.writeC(39);
				os.writeS("exp 보너스 " + (getItem().get_addexp1() + 15+"%"));
				break;
				default:
					break;
				}
			}
			/** 완력의 문장 **/
			if (itemId == 222352) {
				switch (getEnchantLevel()) {
				case 1:
					os.writeC(39);
					os.writeS("물약 회복량 2% +2 %");
					os.writeC(39);
					os.writeS("공포 회복감소 효과 상쇄 + 2%");
					break;
				case 2:
					os.writeC(39);
					os.writeS("물약 회복량 4% +4 %");
					os.writeC(39);
					os.writeS("공포 회복감소 효과 상쇄 + 4%");
					break;
				case 3:
					os.writeC(39);
					os.writeS("물약 회복량 6% +6 %");
					os.writeC(39);
					os.writeS("공포 회복감소 효과 상쇄 + 6%");
					break;
				case 4:
					os.writeC(39);
					os.writeS("물약 회복량 8% +8 %");
					os.writeC(39);
					os.writeS("공포 회복감소 효과 상쇄 + 8%");
					break;
				case 5:
					os.writeC(39);
					os.writeS("근거리 명중 +1");
					os.writeC(39);
					os.writeS("물약 회복량 9% +9 %");
					os.writeC(39);
					os.writeS("공포 회복감소 효과 상쇄 + 9%");
					break;
				case 6:
					os.writeC(39);
					os.writeS("근거리 명중 +1");
					os.writeC(39);
					os.writeS("근거리 대미지 +1");
					os.writeC(39);
					os.writeS("물약 회복량 10% +10 %");
					os.writeC(39);
					os.writeS("공포 회복감소 효과 상쇄 + 10%");
					break;
				case 7:
					os.writeC(39);
					os.writeS("근거리 명중 +2");
					os.writeC(39);
					os.writeS("근거리 대미지 +2");
					os.writeC(39);
					os.writeS("물약 회복량 11% +11 %");
					os.writeC(39);
					os.writeS("공포 회복감소 효과 상쇄 + 11%");
					break;
				case 8:
					os.writeC(39);
					os.writeS("근거리 명중 +3");
					os.writeC(39);
					os.writeS("근거리 대미지 +3");
					os.writeC(39);
					os.writeS("물약 회복량 12% +12 %");
					os.writeC(39);
					os.writeS("공포 회복감소 효과 상쇄 + 12%");
					break;
				case 9:
					os.writeC(39);
					os.writeS("근거리 명중 +4");
					os.writeC(39);
					os.writeS("근거리 대미지 +4");
					os.writeC(39);
					os.writeS("물약 회복량 13% +13 %");
					os.writeC(39);
					os.writeS("공포 회복감소 효과 상쇄 + 13%");
					break;
				case 10:
					os.writeC(39);
					os.writeS("근거리 명중 +5");
					os.writeC(39);
					os.writeS("근거리 대미지 +5");
					os.writeC(39);
					os.writeS("물약 회복량 14% +14 %");
					os.writeC(39);
					os.writeS("공포 회복감소 효과 상쇄 + 14%");
					break;
				default:
					break;
				}
			}
			/** 민첩의 문장 **/
			if (itemId == 222353) {
				switch (getEnchantLevel()) {
				case 1:
					os.writeC(39);
					os.writeS("물약 회복량 2% +2 %");
					os.writeC(39);
					os.writeS("공포 회복감소 효과 상쇄 + 2%");
					break;
				case 2:
					os.writeC(39);
					os.writeS("물약 회복량 4% +4 %");
					os.writeC(39);
					os.writeS("공포 회복감소 효과 상쇄 + 4%");
					break;
				case 3:
					os.writeC(39);
					os.writeS("물약 회복량 6% +6 %");
					os.writeC(39);
					os.writeS("공포 회복감소 효과 상쇄 + 6%");
					break;
				case 4:
					os.writeC(39);
					os.writeS("물약 회복량 8% +8 %");
					os.writeC(39);
					os.writeS("공포 회복감소 효과 상쇄 + 8%");
					break;
				case 5:
					os.writeC(39);
					os.writeS("원거리 명중 +1");
					os.writeC(39);
					os.writeS("물약 회복량 9% +9 %");
					os.writeC(39);
					os.writeS("공포 회복감소 효과 상쇄 + 9%");
					break;
				case 6:
					os.writeC(39);
					os.writeS("원거리 명중 +1%");
					os.writeC(39);
					os.writeS("원거리 대미지 +1");
					os.writeC(39);
					os.writeS("물약 회복량 10% +10 %");
					os.writeC(39);
					os.writeS("공포 회복감소 효과 상쇄 + 10%");
					break;
				case 7:
					os.writeC(39);
					os.writeS("원거리 명중 +2");
					os.writeC(39);
					os.writeS("원거리 대미지 +2");
					os.writeC(39);
					os.writeS("물약 회복량 11% +11 %");
					os.writeC(39);
					os.writeS("공포 회복감소 효과 상쇄 + 11%");
					break;
				case 8:
					os.writeC(39);
					os.writeS("원거리 명중 +3");
					os.writeC(39);
					os.writeS("원거리 대미지 +3");
					os.writeC(39);
					os.writeS("물약 회복량 12% +12 %");
					os.writeC(39);
					os.writeS("공포 회복감소 효과 상쇄 + 12%");
					break;
				case 9:
					os.writeC(39);
					os.writeS("원거리 명중 +4");
					os.writeC(39);
					os.writeS("원거리 대미지 +4");
					os.writeC(39);
					os.writeS("물약 회복량 13% +13 %");
					os.writeC(39);
					os.writeS("공포 회복감소 효과 상쇄 + 13%");
					break;
				case 10:
					os.writeC(39);
					os.writeS("원거리 명중 +5");
					os.writeC(39);
					os.writeS("원거리 대미지 +5");
					os.writeC(39);
					os.writeS("물약 회복량 14% +14 %");
					os.writeC(39);
					os.writeS("공포 회복감소 효과 상쇄 + 14%");
					break;
				default:
					break;
				}
			}
			/** 지식의 문장 **/
			if (itemId == 222354) {
				switch (getEnchantLevel()) {
				case 1:
					os.writeC(39);
					os.writeS("물약 회복량 2% +2 %");
					os.writeC(39);
					os.writeS("공포 회복감소 효과 상쇄 + 2%");
					break;
				case 2:
					os.writeC(39);
					os.writeS("물약 회복량 4% +4 %");
					os.writeC(39);
					os.writeS("공포 회복감소 효과 상쇄 + 4%");
					break;
				case 3:
					os.writeC(39);
					os.writeS("물약 회복량 6% +6 %");
					os.writeC(39);
					os.writeS("공포 회복감소 효과 상쇄 + 6%");
					break;
				case 4:
					os.writeC(39);
					os.writeS("물약 회복량 8% +8 %");
					os.writeC(39);
					os.writeS("공포 회복감소 효과 상쇄 + 8%");
					break;
				case 5:
					os.writeC(39);
					os.writeS("마법 적중+1");
					os.writeC(39);
					os.writeS("물약 회복량 9% +9 %");
					os.writeC(39);
					os.writeS("공포 회복감소 효과 상쇄 + 9%");
					break;
				case 6:
					os.writeC(39);
					os.writeS("마법 적중 +1");
					os.writeC(39);
					os.writeS("SP +1");
					os.writeC(39);
					os.writeS("물약 회복량 10% +10 %");
					os.writeC(39);
					os.writeS("공포 회복감소 효과 상쇄 + 10%");
					break;
				case 7:
					os.writeC(39);
					os.writeS("마법 적중 +2");
					os.writeC(39);
					os.writeS("SP +2");
					os.writeC(39);
					os.writeS("물약 회복량 11% +11 %");
					os.writeC(39);
					os.writeS("공포 회복감소 효과 상쇄 + 11%");
					break;
				case 8:
					os.writeC(39);
					os.writeS("마법 적중 +3");
					os.writeC(39);
					os.writeS("SP +3");
					os.writeC(39);
					os.writeS("물약 회복량 12% +12 %");
					os.writeC(39);
					os.writeS("공포 회복감소 효과 상쇄 + 12%");
					break;
				case 9:
					os.writeC(39);
					os.writeS("마법 적중 +4");
					os.writeC(39);
					os.writeS("SP +4");
					os.writeC(39);
					os.writeS("물약 회복량 13% +13 %");
					os.writeC(39);
					os.writeS("공포 회복감소 효과 상쇄 + 13%");
					break;
				case 10:
					os.writeC(39);
					os.writeS("마법 적중 +5");
					os.writeC(39);
					os.writeS("SP +5");
					os.writeC(39);
					os.writeS("물약 회복량 14% +14 %");
					os.writeC(39);
					os.writeS("공포 회복감소 효과 상쇄 + 14%");
					break;
				default:
					break;
				}
			}
			
			if (itemId == 900019) { //실프의 티셔츠
				switch(getEnchantLevel()){
				case 0: os.writeC(39); os.writeS("MR +7");os.writeC(39); os.writeS("스턴 내성 +7");break;
				case 1: os.writeC(39); os.writeS("MR +8");os.writeC(39); os.writeS("스턴 내성 +8");break;
				case 2: os.writeC(39); os.writeS("MR +9");os.writeC(39); os.writeS("스턴 내성 +9");break;
				case 3: os.writeC(39); os.writeS("MR +10");os.writeC(39); os.writeS("스턴 내성 +10");break;
				case 4: os.writeC(39); os.writeS("MR +11");os.writeC(39); os.writeS("스턴 내성 +11");break;
				case 5: os.writeC(39); os.writeS("MR +12");os.writeC(39); os.writeS("스턴 내성 +12");break;
				case 6: os.writeC(39); os.writeS("MR +13");os.writeC(39); os.writeS("스턴 내성 +13");break;
				case 7: os.writeC(39); os.writeS("MR +14");os.writeC(39); os.writeS("스턴 내성 +14");break;
				case 8: os.writeC(39); os.writeS("MR +15");os.writeC(39); os.writeS("스턴 내성 +15");break;
				case 9: os.writeC(39); os.writeS("MR +16");os.writeC(39); os.writeS("스턴 내성 +16");break;
				case 10: os.writeC(39); os.writeS("MR +17");os.writeC(39); os.writeS("스턴 내성 +17");break;
				case 11: os.writeC(39); os.writeS("MR +18");os.writeC(39); os.writeS("스턴 내성 +18");break;
				case 12: os.writeC(39); os.writeS("MR +19");os.writeC(39); os.writeS("스턴 내성 +19");break;
				case 13: os.writeC(39); os.writeS("MR +20");os.writeC(39); os.writeS("스턴 내성 +20");break;
				case 14: os.writeC(39); os.writeS("MR +21");os.writeC(39); os.writeS("스턴 내성 +21");break;
				case 15: os.writeC(39); os.writeS("MR +22");os.writeC(39); os.writeS("스턴 내성 +22");break;
				}
			}
			/**마물 8인첸이상 **/
			if(itemId >=900015 && itemId <= 900018){
				if(getEnchantLevel() >=8){
					os.writeC(39);
					os.writeS("\\f3근거리 대미지 +1");
					os.writeC(39);
					os.writeS("\\f3원거리 대미지 +1");
				}
			}
			/***************************/
			/**암석 8인첸이상 **/
			if(itemId >=900011 && itemId <= 900014){
				if(getEnchantLevel() >=8){
					os.writeC(39);
					os.writeS("\\f3PVP 대미지 감소 +1");
					os.writeC(39);
					os.writeS("\\f3대미지 리덕션 +1");
				}
			}
			/***************************/
			
			
			/** 진명황의 집행검 스턴적중+10 **/
			if (itemId == 61 || itemId == 7000061 || itemId == 202012) {
				switch (getEnchantLevel()) {
				case 0: case 1: case 2: case 3: case 4: case 5: case 6:
				case 7: case 8: case 9: case 10: case 11:
				case 12: case 13: case 14: case 15:
					os.writeC(39);
					os.writeS("스턴 적중 +" + (getItem().get_regist_stun() + 10));
					break;
				default:
					break;
				}
			}
			
			/** 고대투사의가더 근거리 대미지 **/
			if (itemId == 22003 && getEnchantLevel() > 4) {
				switch (getEnchantLevel()) {
				case 5: case 6:
					os.writeC(39);
					os.writeS("근거리 대미지 +" + (getItem().getDmgRate() + 0));
					break;
				case 7:	case 8:
					os.writeC(39);
					os.writeS("근거리 대미지 +" + (getItem().getDmgRate() + 1));
					break;
				case 9:
					os.writeC(39);
					os.writeS("근거리 대미지 +" + (getItem().getDmgRate() + 2));
					break;
				default:
					break;
				}
			}
			
			/** 고대명궁의가더 원거리 대미지 **/
			if (itemId == 22000 && getEnchantLevel() > 4) {
				switch (getEnchantLevel()) {
				case 5: case 6:
					os.writeC(39);
					os.writeS("원거리 대미지 +" + (getItem().getBowDmgRate() + 0));
					break;
				case 7:	case 8:
					os.writeC(39);
					os.writeS("원거리 대미지 +" + (getItem().getBowDmgRate() + 1));
					break;
				case 9:
					os.writeC(39);
					os.writeS("원거리 대미지 +" + (getItem().getBowDmgRate() + 2));
					break;
				default:
					break;
				}
			}
			/** 머머로드왕관 원거리 대미지 **/
			if (itemId == 20017 && getEnchantLevel() > 6) {
				switch (getEnchantLevel()) {
				case 7:
					os.writeC(39);
					os.writeS("원거리 대미지 +" + (getItem().getBowDmgRate() + 1));
					break;
				case 8:
					os.writeC(39);
					os.writeS("원거리 대미지 +" + (getItem().getBowDmgRate() + 2));
					break;
				case 9:
					os.writeC(39);
					os.writeS("원거리 대미지 +" + (getItem().getBowDmgRate() + 2));
					break;
				default:
					break;
				}
			}
			/** (지혜/민첩/지식/완력)의 부츠 *
			 * :	+7부터 최대 HP +20/+40/+60 증가
			 * :	+9에 대미지 감소+1 추가
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
					os.writeS("대미지 감소 " + 1);
					break;
				default:
					break;
				}
			}
			/** 안타라스 상급 리뉴얼 **/
			if (itemId >= 22196 && itemId <= 22199) {
				int dam = getEnchantLevel() - 6;
				int total = getItem().getDamageReduction() + dam;
				int total2 = getItem().getDamageReduction() + 3;
				if (getEnchantLevel() >= 7 && getEnchantLevel() <= 9) {
					os.writeC(39);
					os.writeS("대미지 리덕션 " + total);
				} else if (getEnchantLevel() > 9) {
					os.writeC(39);
					os.writeS("대미지 리덕션 " + total2);
				} else {
					os.writeC(39);
					os.writeS("대미지 리덕션 " + getItem().getDamageReduction());
				}
			} else if (itemId == 491005) { // 대미지 리덕션
				os.writeC(39);
				if(getEnchantLevel() >= 9)
					os.writeS("대미지 리덕션 +" + (getItem().getDamageReduction()+1));
				else
					os.writeS("대미지 리덕션 +" + getItem().getDamageReduction());
		 
			} else if (getItem().getDamageReduction() != 0) { // 대미지 리덕션
				os.writeC(39);
				os.writeS("대미지 리덕션 " + getItem().getDamageReduction());
			}
			/** 벨트 7부터 pvp대미지감소 **/
			if (itemType2 == 2 && (getItem().getType() == 10 && getEnchantLevel() > 6)) {// 벨트
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
			/** PVP 데미지 감소 **/
			} else if (getItem().get_regist_calcPcDefense() != 0) {
				os.writeC(39);
				os.writeS("\\f2PVP 대미지 감소+ " + getItem().get_regist_calcPcDefense());
			}
				/** PVP 추가 데미지 **/
			if (getItem().get_regist_PVPweaponTotalDamage() != 0) {
				os.writeC(39);
				os.writeS("\\f2PVP 추가 대미지+ " + getItem().get_regist_PVPweaponTotalDamage());
			}

			/** 대미지 리덕션 무기 표기 부여 **/
			if (itemId == 202011) {
				if (getEnchantLevel() >= 0) {
					os.writeC(39);
					os.writeS("대미지 리덕션 무시 +12");
				}
			}if ((itemId >= 22208 && itemId <= 22211)) { // 발라갑옷
				if (getEnchantLevel() < 7) {
					os.writeC(39);
					os.writeS("대미지 리덕션 무시 +3");
				}else if (getEnchantLevel() == 7) {
					os.writeC(39);
					os.writeS("대미지 리덕션 무시 +4");
				}else if (getEnchantLevel() == 8) {
					os.writeC(39);
					os.writeS("대미지 리덕션 무시 +5");
				}else if (getEnchantLevel() >= 9) {
					os.writeC(39);
					os.writeS("대미지 리덕션 무시 +6");
				}
			}
			if ((itemId == 203025 || itemId == 203026) // 진 싸울아비 대검
					&& getEnchantLevel() >= 10) { 				
					os.writeC(39);
					os.writeS("발동:싸울 혼");
				}
				if ((itemId == 203006) // 태풍의 도끼
						&& getEnchantLevel() >= 10) { 				
						os.writeC(39);
						os.writeS("발동:나락");
				}
				if ((itemId == 1136) // 악몽의 장궁
						&& getEnchantLevel() >= 10) { 				
						os.writeC(39);
						os.writeS("발동:악몽");
				}
				
				if ((itemId == 203017) // 섬멸자의 체인소드
						&& getEnchantLevel() >= 10) { 				
						os.writeC(39);
						os.writeS("발동:섬멸");
				}		
				if ((itemId >= 22208 && itemId <= 22211)
						&& getEnchantLevel() >= 7) { 				
						os.writeC(39);
						os.writeS("발동:발라카스의 일격");
				}
			/** 신성한 지식의 목걸이 마법적중**/
			if (itemId == 222348) {
				os.writeC(39);
				os.writeS("마법적중 +2");
			}
			/** 신성한 영생의 목걸이 스턴내성**/
			if (itemId == 222349) {
				os.writeC(39);
				os.writeS("스턴내성 +7");
			}
			
			
			/** 태풍의도끼 공포적중 **/
			if (itemId == 203006) {
				switch (getEnchantLevel()) {
				case 8:
					os.writeC(39);
					os.writeS("공포적중 +1");
					break;
				case 9:
					os.writeC(39);
					os.writeS("공포적중 +2");
					break;
				case 10:
					os.writeC(39);
					os.writeS("공포적중 +3");
					break;
				default:
					break;
				}
			}
			/** 타이탄의분노 공포적중 **/
			if (itemId == 202014) {
				switch (getEnchantLevel()) {
				case 0:
					os.writeC(39);
					os.writeS("공포적중 +5");
					break;
				case 1:
					os.writeC(39);
					os.writeS("공포적중 +6");
					break;
				case 2:
					os.writeC(39);
					os.writeS("공포적중 +7");
					break;
				case 3:
					os.writeC(39);
					os.writeS("공포적중 +8");
					break;
				case 4:
					os.writeC(39);
					os.writeS("공포적중 +9");
					break;
				case 5:
					os.writeC(39);
					os.writeS("공포적중 +10");
					break;
				case 6:
					os.writeC(39);
					os.writeS("공포적중 +11");
					break;
				case 7:
					os.writeC(39);
					os.writeS("공포적중 +12");
					break;
				case 8:
					os.writeC(39);
					os.writeS("공포적중 +13");
					break;
				case 9:
					os.writeC(39);
					os.writeS("공포적중 +14");
					break;
				case 10:
					os.writeC(39);
					os.writeS("공포적중 +15");
					break;
				default:
					break;
				}
			}

			/** 빛나는 마력의 장갑 **/
			if (itemId == 20274 && getEnchantLevel() > 4) {
				int 무게보너스 = getEnchantLevel() - 4;
				os.writeC(39);
				os.writeS("무게 게이지 " + (getItem().getWeightReduction() + 무게보너스));
			} else if (getItem().getWeightReduction() != 0) { // 무게 게이지
				os.writeC(39);
				os.writeS("무게 게이지 " + getItem().getWeightReduction());
			}

			if (itemId == 22263) {
				os.writeC(39);
				os.writeS("인챈수x2 확률로 대미지감소 50");
				switch (getEnchantLevel()) {
				case 1:
					os.writeC(39);
					os.writeS("현재 발동확률: 2%");
					break;
				case 2:
					os.writeC(39);
					os.writeS("현재 발동확률: 4%");
					break;
				case 3:
					os.writeC(39);
					os.writeS("현재 발동확률: 6%");
					break;
				case 4:
					os.writeC(39);
					os.writeS("현재 발동확률: 8%");
					break;
				case 5:
					os.writeC(39);
					os.writeS("현재 발동확률: 10%");
					break;
				case 6:
					os.writeC(39);
					os.writeS("현재 발동확률: 12%");
					break;
				case 7:
					os.writeC(39);
					os.writeS("현재 발동확률: 14%");
					break;
				case 8:
					os.writeC(39);
					os.writeS("현재 발동확률: 16%");
					break;
				case 9:
					os.writeC(39);
					os.writeS("현재 발동확률: 18%");
					break;
				default:
					break;
				}
			}
			/** 룸티스의 붉은빛 귀걸이 확률 **/
			if (itemId == 22229) {
				switch (getEnchantLevel()) {
				case 5:
					os.writeC(39);
					os.writeS("확률 대미지 리덕션 2%");
					break;
				case 6:
					os.writeC(39);
					os.writeS("확률 대미지 리덕션 3%");
					break;
				case 7:
					os.writeC(39);
					os.writeS("확률 대미지 리덕션 4%");
					break;
				case 8:
					os.writeC(39);
					os.writeS("확률 대미지 리덕션 5%");
					break;
				}
				os.writeC(39);
			}
			/** 축복받은 룸티스의 붉은빛 귀걸이 확률 **/
			if (itemId == 222337) {
				switch (getEnchantLevel()) {
				case 4:
					os.writeC(39);
					os.writeS("확률 대미지 리덕션 2%");
					break;
				case 5:
					os.writeC(39);
					os.writeS("확률 대미지 리덕션 3%");
					break;
				case 6:
					os.writeC(39);
					os.writeS("확률 대미지 리덕션 4%");
					break;
				case 7:
					os.writeC(39);
					os.writeS("확률 대미지 리덕션 5%");
					break;
				case 8:
					os.writeC(39);
					os.writeS("확률 대미지 리덕션 6%");
					break;
				}
				os.writeC(39);
			}

			
			if(getItem().get_penetration() == 1){        //관통
				   os.writeC(39);
				   os.writeS("관통 효과");
				
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
			/** 공포 내성 **/
			if (getItem().get_regist_DESPERADO() != 0 && (getMr() == 0)) {
				os.writeC(39);
				os.writeS("공포 내성 +" + getItem().get_regist_DESPERADO());
			}

			/** 스냅퍼의 반지 스턴내성 표시 **/ // 현질러와 일반유저의 격차를 줄이기위해 스턴내성 579 넣어야하는데 456으로 들어가있음.(유저들은 모름)
			if (itemType2 == 2 && itemId >= 22224 && itemId <= 22228 && getEnchantLevel() == 6
					|| itemId >= 222290 && itemId <= 222291 && getEnchantLevel() == 6
					|| itemId >= 222330 && itemId <= 222336 && getEnchantLevel() == 6) { // 인챈트가 6이면
				os.writeC(33);
				os.writeC(5);
				os.writeH(getItem().get_regist_stun() + 5);
			} else if (itemType2 == 2 && itemId >= 22224 && itemId <= 22228 && getEnchantLevel() == 7
					|| itemId >= 222290 && itemId <= 222291 && getEnchantLevel() == 7
					|| itemId >= 222330 && itemId <= 222336 && getEnchantLevel() == 7) { // 인챈트가 7이면
				os.writeC(33);
				os.writeC(5);
				os.writeH(getItem().get_regist_stun() + 7);
			} else if (itemType2 == 2 && itemId >= 22224 && itemId <= 22228 && getEnchantLevel() == 8
					|| itemId >= 222290 && itemId <= 222291 && getEnchantLevel() == 8
					|| itemId >= 222330 && itemId <= 222336 && getEnchantLevel() == 8) { // 인챈트가 8이면
				os.writeC(33);
				os.writeC(5);
				os.writeH(getItem().get_regist_stun() + 9);
				
				/** 귀걸이,목걸이 +7부터 스턴내성 **/
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
			// 불의 속성
			if (getItem().get_defense_fire() != 0) {
				os.writeC(27);
				os.writeC(getItem().get_defense_fire());
			}
			// 물의 속성
			if (getItem().get_defense_water() != 0) {
				os.writeC(28);
				os.writeC(getItem().get_defense_water());
			}
			// 바람 속성
			if (getItem().get_defense_wind() != 0) {
				os.writeC(29);
				os.writeC(getItem().get_defense_wind());
			}
			// 땅의 속성
			if (getItem().get_defense_earth() != 0) {
				os.writeC(30);
				os.writeC(getItem().get_defense_earth());
			}

			if (itemType2 == 2 && (getItem().getType() == 8 || getItem().getType() == 12)) {// 목걸이, 귀걸이
				os.writeC(39);
				os.writeS("$18956 : $18960"); // 근성
			} else if (itemType2 == 2 && (getItem().getType() == 9 || getItem().getType() == 11)) {// 반지
				os.writeC(39);
				os.writeS("$18956 : $18961"); // 열정
			} else if (itemType2 == 2 && (getItem().getType() == 10)) {// 벨트
				os.writeC(39);
				os.writeS("$18956 : $18962"); // 의지
			}
			if(itemId==900032){ //커츠의 투사 휘장 
				int chp = getEnchantLevel()*5+5;
				if(getEnchantLevel()==8) chp = 50;
				os.writeC(39);
				os.writeS("최대 HP +"+chp);
				switch(getEnchantLevel()){
					case 5:
						os.writeC(39);
						os.writeS("근거리 대미지 +1");
						break;
					case 6:
						os.writeC(39);
						os.writeS("근거리 대미지 +2");
						os.writeC(39);
						os.writeS("근거리 치명타 +1%");
						break;
					case 7:
						os.writeC(39);
						os.writeS("근거리 대미지 +3");
						os.writeC(39);
						os.writeS("근거리 치명타 +3%");
						break;
					case 8:
						os.writeC(39);
						os.writeS("근거리 대미지 +4");
						os.writeC(39);
						os.writeS("근거리 치명타 +5%");
						break;
				}
				
			}
			if(itemId == 900033){ //커츠의 명궁 휘장
				int chp = getEnchantLevel()*5+5;
				if(getEnchantLevel()==8) chp = 50;
				os.writeC(39);
				os.writeS("최대 HP +"+chp);
				switch(getEnchantLevel()){
				case 5:
					os.writeC(39);
					os.writeS("원거리 대미지 +1");
					break;
				case 6:
					os.writeC(39);
					os.writeS("원거리 대미지 +2");
					os.writeC(39);
					os.writeS("원거리 치명타 +1%");
					break;
				case 7:
					os.writeC(39);
					os.writeS("원거리 대미지 +3");
					os.writeC(39);
					os.writeS("원거리 치명타 +3%");
					break;
				case 8:
					os.writeC(39);
					os.writeS("원거리 대미지 +4");
					os.writeC(39);
					os.writeS("원거리 치명타 +5%");
					break;
				}
			}
			
			if(itemId == 900034){ //커츠의 현자 휘장 
				int chp = getEnchantLevel()*5+5;
				if(getEnchantLevel()==8) chp = 50;
				os.writeC(39);
				os.writeS("최대 HP +"+chp);
				switch(getEnchantLevel()){
				case 5:
					os.writeC(39);
					os.writeS("근거리 명중 +1");
					break;
				case 6:
					os.writeC(39);
					os.writeS("근거리 명중 +2");
					os.writeC(39);
					os.writeS("마법 치명타 +1%");
					break;
				case 7:
					os.writeC(39);
					os.writeS("근거리 명중 +3");
					os.writeC(39);
					os.writeS("마법 치명타 +2%");
					break;
				case 8:
					os.writeC(39);
					os.writeS("근거리 명중 +4");
					os.writeC(39);
					os.writeS("마법 치명타 +4%");
					break;
				}
			}
			
			if(itemId == 900035){ //커츠의 현자 휘장 
				int chp = getEnchantLevel()*5+5;
				if(getEnchantLevel()==8) chp = 50;
				os.writeC(39);
				os.writeS("최대 HP +"+chp);
				switch(getEnchantLevel()){
				case 5:
					os.writeC(39);
					os.writeS("대미지 감소 +1");
					break;
				case 6:
					os.writeC(39);
					os.writeS("대미지 감소 +2");
					os.writeC(39);
					os.writeS("MR +3%");
					break;
				case 7:
					os.writeC(39);
					os.writeS("대미지 감소 +3");
					os.writeC(39);
					os.writeS("MR +5%");
					break;
				case 8:
					os.writeC(39);
					os.writeS("대미지 감소 +4");
					os.writeC(39);
					os.writeS("MR +7%");
					break;
				}
			}
			if (getItem().getMagicName() != null && !getItem().getMagicName().equals("")) {
				os.writeC(74);
				os.writeS(getItem().getMagicName());
			}
		} // 주석함
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
				os.writeH(1175);// 데몬
			} else if (getItem().getItemId() == 20100) {
				os.writeC(_poly);
				os.writeH(18692);// 진데스
			} else if (getItem().getItemId() == 20151) {
				os.writeC(_poly);
				os.writeH(2118);// 케레니스
			} else if (getItem().getItemId() == 20118) {
				os.writeC(_poly);
				os.writeH(2117);// 켄라우헬
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
					os.writeC(0x2b);// 근성
				} else if (getItem().getType() == 9
						|| getItem().getType() == 11) {
					os.writeC(0x43);
					os.writeC(0x2c);// 열정
				} else if (getItem().getType() == 10) {
					os.writeC(0x43);
					os.writeC(0x2d);// 의지
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
					os.writeH(1175);// 데몬
				} else if (getItem().getItemId() == 20100) {
					os.writeC(_poly);
					os.writeH(18692);// 진데스
				} else if (getItem().getItemId() == 20151) {
					os.writeC(_poly);
					os.writeH(2118);// 케레니스
				} else if (getItem().getItemId() == 20118) {
					os.writeC(_poly);
					os.writeH(2117);// 켄라우헬
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
					os.writeC(0x2b);// 근성
				} else if (getItem().getType() == 9
						|| getItem().getType() == 11) {
					os.writeC(0x43);
					os.writeC(0x2c);// 열정
				} else if (getItem().getType() == 10) {
					os.writeC(0x43);
					os.writeC(0x2d);// 의지
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


	private L1PcInstance _owner; // 실제로는 장착한 사람.....

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
				if (_owner != null && _owner.getInventory().getItem(L1ItemInstance.this.getId()) == L1ItemInstance.this && isEquipped()) {
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

			if (_owner != null && _owner.getInventory().getItem(L1ItemInstance.this.getId()) == L1ItemInstance.this && isEquipped()) {
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
		if (skillId == L1SkillId.HOLY_WEAPON){
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
	
	
	public int 인형SP() {
		int value = 0;
		int itemid = getItemId();
		switch (itemid) {
		case 447014: case 30022: case 30023: case 30024: case 30025: case 447015:
		case 510222:
		value = 1; break; 
		case 447016: case 5991: value = 2; break;
		case 752: value = 3; break;
		default: break;
		}
		return value;
	}
	
	public int 근거리대미지() {
		int value = 0;
		int itemid = getItemId();
		switch (itemid) {
		case 741: value = 1; break; 
		case 743: case 500214: case 447012: case 30022: case 30023: case 30024: case 30025: 
		case 447015: case 510220: case 5991:
			value = 2; break; 
		case 750: value = 1; break; 
		default: break;
		}
		return value;
	}
	public int 근거리명중() {
		int value = 0;
		int itemid = getItemId();
		switch (itemid) {
		case 743: case 500215: case 510220: case 5991: value = 2; break; 
		case 750: value = 1; break; 
		default: break;
		}
		return value;
	}
	public int 원거리대미지() {
		int value = 0;
		int itemid = getItemId();
		switch (itemid) {
		case 744: value = 5; break;
		case 210105: value = 1; break;
		case 447013: case 30022: case 30023: case 30024: case 30025: case 447015:
		case 447017: case 5991:
			value = 2; break;
		default: break;
		}
		return value;
	}
	public int 원거리명중() {
		int value = 0;
		int itemid = getItemId();
		switch (itemid) {
		case 210070: case 210105: value = 1; break;
		case 500215: case 5991: value = 2; break;
		case 510216: value = 5; break;
		default: break;
		}
		return value;
	}
	public int 최대HP() {
		int value = 0;
		int itemid = getItemId();
		switch (itemid) {
		case 740: case 500215: value = 50; break;
		case 447012: case 447013: case 447014: value = 30; break;
		case 447016: value = 80; break;
		default: break;
		}
		return value;
	}
	public int 최대MP() {
		int value = 0;
		int itemid = getItemId();
		switch (itemid) {
		case 500215: value = 30; break;
		default: break;
		}
		return value;
	}
	public int 경험치보너스() {
		int value = 0;
		int itemid = getItemId();
		switch (itemid) {
		case 746: case 410171: value = 20; break;
		case 510216: case 510217: case 510218: case 510219: case 751: value = 10; break;
		default:
			break;
		}
		return value;
	}
	public int HP회복() {
		int value = 0;
		int itemid = getItemId();
		switch (itemid) {
		case 744: value = 30; break;
		case 210086: value = 25; break;
		case 510218: value = 15; break;
		default: break;
		}
		return value;
	}
	public int MP회복() {
		int value = 0;
		int itemid = getItemId();
		switch (itemid) {
		case 41249: case 210071: case 447012: case 447013: case 447014: case 510217:
		case 510221: case 510222: case 751:
			value = 15; break;
		case 447015: case 410173: value = 10; break;
		case 447017: value = 6; break;
		default: break;
		}
		return value;
	}
	public int 추가데미지(){
		int value = 0;
		int itemid = getItemId();
		switch (itemid) {
		case 41250: case 210072: value = 15; break; 
		case 410172: value = 3; break;
		default: break;
		}
		return value;
	}
	public int 데미지리덕션(){
		int value = 0;
		int itemid = getItemId();
		switch (itemid) {
		case 741: case 210070: case 510218: case 510219: 
			value = 1; 
			break;
		case 742: case 751: 
			value = 2; 
			break;
		case 3000152: //커츠
			value = 3; 
			break;
		case 746: case 410171: 
			value = 5; 
			break;
		default: break;
		}
		return value;
	}
	public int 스턴레벨(){
		int value = 0;
		int itemid = getItemId();
		switch (itemid) {
		case 743: value = 1; break;
		case 745: value = 2; break;
		default: break;
		}
		return value;
	}
	public int 스턴내성(){
		int value = 0;
		int itemid = getItemId();
		switch (itemid) {
		case 745: case 510220: 
			value = 12; 
			break;
		case 500214: case 752:  
		case 3000150: case 3000151: case 3000152: //바포, 얼녀,커츠
			value = 10; 
			break;
		case 410173: 
			value = 8; 
			break;
		default: break;
		}
		return value;
	}
	public String 마법발동(){
		String value = null;
		int itemid = getItemId();
		switch (itemid) {
		case 746: case 410171: value = "헬 파이어"; break;
		case 500213: value = "헤이스트"; break;
		case 30022: value = "아이스 대거"; break;
		case 30023: value = "파이어 애로우"; break; 
		case 30024: value = "스탈락"; break;
		case 30025: value = "윈드 커터"; break;
		case 510221: value = "콜 라이트닝"; break;
		default: break;
		}
		return value;
	}
	public int 무게게이지(){
		int value = 0;
		int itemid = getItemId();
		switch (itemid) {
		case 41248: case 210106:case 210107:case 210108:case 210109: value = 10; break;
		
		default: break;
		}
		return value;
	}
	public int 인형방어구(){
		int value = 0;
		int itemid = getItemId();
		switch (itemid) {
		case 210096: value = 3; break;
		case 500213: value = 2; break;
		default: break;
		}
		return value;
	}
	public int 인형동빙내성(){
		int value = 0;
		int itemid = getItemId();
		switch (itemid) {
		case 210096: value = 7; break;
		default: break;
		}
		return value;
	}
	public int 인형홀드내성(){
		int value = 0;
		int itemid = getItemId();
		switch (itemid) {
		case 500213: value = 10; break;
		default: break;
		}
		return value;
	}
	public int 인형Mpr(){
		int value = 0;
		int itemid = getItemId();
		switch (itemid) {
		case 210106:case 210107:case 210108:case 210109: value = 5; break;
		default: break;
		}
		return value;
	}
	public String 아이템획득(){
		String value = null;
		int itemid = getItemId();
		switch (itemid) {
		case 5991: value = "떡 바구니"; break;
		
		default: break;
		}
		return value;
	}
}
