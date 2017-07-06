package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.IdFactory;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.templates.L1Armor;
import l1j.server.server.templates.L1EtcItem;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1RaceTicket;
import l1j.server.server.templates.L1Weapon;
import l1j.server.server.utils.SQLUtil;

public class ItemTable {
	private static final long serialVersionUID = 1L;
	private static final int MAX_ITEM_ID = 9000000;

	private static Logger _log = Logger.getLogger(ItemTable.class.getName());

	private static final Map<String, Integer> _armorTypes = new HashMap<String, Integer>();

	private static final Map<String, Integer> _weaponTypes = new HashMap<String, Integer>();

	private static final Map<String, Integer> _weaponId = new HashMap<String, Integer>();

	private static final Map<String, Integer> _materialTypes = new HashMap<String, Integer>();

	private static final Map<String, Integer> _etcItemTypes = new HashMap<String, Integer>();

	private static final Map<String, Integer> _useTypes = new HashMap<String, Integer>();

	private static ItemTable _instance;

	private L1Item _allTemplates[];

	public L1Item[] getAllTemplates() {
		return _allTemplates;
	}

	public final Map<Integer, L1EtcItem> _etcitems;

	public final Map<Integer, L1Armor> _armors;

	public final Map<Integer, L1Weapon> _weapons;

	/** 버경 추가 **/
	private final Map<Integer, L1RaceTicket> _race = new HashMap<Integer, L1RaceTicket>();
	static {

		_etcItemTypes.put("arrow", new Integer(0));
		_etcItemTypes.put("wand", new Integer(1));
		_etcItemTypes.put("light", new Integer(2));
		_etcItemTypes.put("gem", new Integer(3));
		_etcItemTypes.put("totem", new Integer(4));
		_etcItemTypes.put("firecracker", new Integer(5));
		_etcItemTypes.put("potion", new Integer(6));
		_etcItemTypes.put("food", new Integer(7));
		_etcItemTypes.put("scroll", new Integer(8));
		_etcItemTypes.put("questitem", new Integer(9));
		_etcItemTypes.put("spellbook", new Integer(10));
		_etcItemTypes.put("petitem", new Integer(11));
		_etcItemTypes.put("other", new Integer(12));
		_etcItemTypes.put("material", new Integer(13));
		_etcItemTypes.put("event", new Integer(14));
		_etcItemTypes.put("sting", new Integer(15));
		_etcItemTypes.put("treasure_box", new Integer(16));
		_useTypes.put("none", new Integer(-1)); // 사용 불가능
		_useTypes.put("normal", new Integer(0));
		_useTypes.put("weapon", new Integer(1));
		_useTypes.put("armor", new Integer(2));
		// _useTypes.put("wand1", new Integer(3));
		// _useTypes.put("wand", new Integer(4));
		// wand를 거절하는 액션을 잡는다(C_RequestExtraCommand가 보내진다)
		_useTypes.put("spell_long", new Integer(5)); // 지면 / 오브젝트 선택(원거리)
		_useTypes.put("ntele", new Integer(6));
		_useTypes.put("identify", new Integer(7));
		_useTypes.put("res", new Integer(8));
		_useTypes.put("teleport", new Integer(9)); // 텔포땜에
		_useTypes.put("letter", new Integer(12));
		_useTypes.put("letter_w", new Integer(13));
		_useTypes.put("choice", new Integer(14));
		_useTypes.put("instrument", new Integer(15));
		_useTypes.put("sosc", new Integer(16));
		_useTypes.put("spell_short", new Integer(17)); // 지면 / 오브젝트 선택(근거리)
		_useTypes.put("T", new Integer(18));
		_useTypes.put("cloak", new Integer(19));
		_useTypes.put("glove", new Integer(20));
		_useTypes.put("boots", new Integer(21));
		_useTypes.put("helm", new Integer(22));
		_useTypes.put("ring", new Integer(23));
		_useTypes.put("amulet", new Integer(24));
		_useTypes.put("shield", new Integer(25));
		_useTypes.put("garder", new Integer(25));
		_useTypes.put("dai", new Integer(26));
		_useTypes.put("zel", new Integer(27));
		_useTypes.put("blank", new Integer(28));
		_useTypes.put("btele", new Integer(29));
		_useTypes.put("spell_buff", new Integer(30)); // 오브젝트 선택(원거리)
		// Ctrl를 누르지 않는다고 패킷이 날지 않아?
		_useTypes.put("ccard", new Integer(31));
		_useTypes.put("ccard_w", new Integer(32));
		_useTypes.put("vcard", new Integer(33));
		_useTypes.put("vcard_w", new Integer(34));
		_useTypes.put("wcard", new Integer(35));
		_useTypes.put("wcard_w", new Integer(36));
		_useTypes.put("belt", new Integer(37));
		_useTypes.put("spell_long2", new Integer(39)); // 지면 / 오브젝트 선택(원거리) 5로 같은?
		_useTypes.put("earring", new Integer(40));
		_useTypes.put("fishing_rod", new Integer(42));
		_useTypes.put("ron", new Integer(44));// 룬1
		_useTypes.put("ron2", new Integer(45));// 성장/회복의 문장
		_useTypes.put("acczel", new Integer(46));
		_useTypes.put("pair", new Integer(47));// 각반
		_useTypes.put("healing", new Integer(51));
		_useTypes.put("sentence", new Integer(74));//문장0x4a 74
		_useTypes.put("shoulder", new Integer(75));// 견갑
		_useTypes.put("badge", new Integer(76));// 휘장
		
					/** 방어구 타입 **/
		_armorTypes.put("none", new Integer(0));
		_armorTypes.put("helm", new Integer(1));
		_armorTypes.put("armor", new Integer(2));
		_armorTypes.put("T", new Integer(3));
		_armorTypes.put("cloak", new Integer(4));
		_armorTypes.put("glove", new Integer(5));
		_armorTypes.put("boots", new Integer(6));
		_armorTypes.put("shield", new Integer(7));
		_armorTypes.put("amulet", new Integer(8));
		_armorTypes.put("ring", new Integer(9));
		_armorTypes.put("belt", new Integer(10));
		_armorTypes.put("ring2", new Integer(11));
		_armorTypes.put("earring", new Integer(12));
		_armorTypes.put("garder", new Integer(13));
		_armorTypes.put("ron", new Integer(14));// 룬1
		_armorTypes.put("pair", new Integer(15));// 각반
		_armorTypes.put("sentence", new Integer(16));// 문장		
		_armorTypes.put("badge", new Integer(17));// 휘장
		_armorTypes.put("shoulder", new Integer(18));// 견갑
				/** 웨폰 타입 **/
		_weaponTypes.put("sword", new Integer(1));
		_weaponTypes.put("dagger", new Integer(2));
		_weaponTypes.put("tohandsword", new Integer(3));
		_weaponTypes.put("bow", new Integer(4));
		_weaponTypes.put("spear", new Integer(5));
		_weaponTypes.put("blunt", new Integer(6));
		_weaponTypes.put("staff", new Integer(7));
		_weaponTypes.put("throwingknife", new Integer(8));
		_weaponTypes.put("arrow", new Integer(9));
		_weaponTypes.put("gauntlet", new Integer(10));
		_weaponTypes.put("claw", new Integer(11));
		_weaponTypes.put("edoryu", new Integer(12));
		_weaponTypes.put("singlebow", new Integer(13));
		_weaponTypes.put("singlespear", new Integer(14));
		_weaponTypes.put("tohandblunt", new Integer(15));
		_weaponTypes.put("tohandstaff", new Integer(16));
		_weaponTypes.put("keyring", new Integer(17));
		_weaponTypes.put("chainsword", new Integer(18)); // 용기사 체인소드관련.

		_weaponId.put("sword", new Integer(4));
		_weaponId.put("dagger", new Integer(46));
		_weaponId.put("tohandsword", new Integer(50));
		_weaponId.put("bow", new Integer(20));
		_weaponId.put("blunt", new Integer(11));
		_weaponId.put("spear", new Integer(24));
		_weaponId.put("chainsword", new Integer(24));
		_weaponId.put("staff", new Integer(40));
		_weaponId.put("throwingknife", new Integer(2922));
		_weaponId.put("arrow", new Integer(66));
		_weaponId.put("gauntlet", new Integer(62));
		_weaponId.put("claw", new Integer(58));
		_weaponId.put("keyring", new Integer(58));
		_weaponId.put("edoryu", new Integer(54));
		_weaponId.put("singlebow", new Integer(20));
		_weaponId.put("singlespear", new Integer(24));
		_weaponId.put("tohandblunt", new Integer(11));
		_weaponId.put("tohandstaff", new Integer(40));

		_materialTypes.put("none", new Integer(0));
		_materialTypes.put("liquid", new Integer(1));
		_materialTypes.put("web", new Integer(2));
		_materialTypes.put("vegetation", new Integer(3));
		_materialTypes.put("animalmatter", new Integer(4));
		_materialTypes.put("paper", new Integer(5));
		_materialTypes.put("cloth", new Integer(6));
		_materialTypes.put("leather", new Integer(7));
		_materialTypes.put("wood", new Integer(8));
		_materialTypes.put("bone", new Integer(9));
		_materialTypes.put("dragonscale", new Integer(10));
		_materialTypes.put("iron", new Integer(11));
		_materialTypes.put("steel", new Integer(12));
		_materialTypes.put("copper", new Integer(13));
		_materialTypes.put("silver", new Integer(14));
		_materialTypes.put("gold", new Integer(15));
		_materialTypes.put("platinum", new Integer(16));
		_materialTypes.put("mithril", new Integer(17));
		_materialTypes.put("blackmithril", new Integer(18));
		_materialTypes.put("glass", new Integer(19));
		_materialTypes.put("gemstone", new Integer(20));
		_materialTypes.put("mineral", new Integer(21));
		_materialTypes.put("oriharukon", new Integer(22));
	}

	public static ItemTable getInstance() {
		if (_instance == null) {
			_instance = new ItemTable();
		}
		return _instance;
	}

	private ItemTable() {
		_etcitems = allEtcItem();
		_weapons = allWeapon();
		_armors = allArmor();
		// initRace();
		buildFastLookupTable();
	}

	public static void reload() {
		ItemTable oldInstance = _instance;
		_instance = new ItemTable();
		oldInstance._etcitems.clear();
		oldInstance._weapons.clear();
		oldInstance._armors.clear();
	}

	private Map<Integer, L1EtcItem> allEtcItem() {
		Map<Integer, L1EtcItem> result = new HashMap<Integer, L1EtcItem>();

		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		L1EtcItem item = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("select * from etcitem");

			rs = pstm.executeQuery();
			while (rs.next()) {
				item = new L1EtcItem();
				item.setItemId(rs.getInt("item_id"));
				item.setName(rs.getString("name"));
				item.setNameId(rs.getString("name_id"));
				item.setType((_etcItemTypes.get(rs.getString("item_type"))).intValue());
				item.setUseType(_useTypes.get(rs.getString("use_type")).intValue());
				// item.setType1(0); // 사용하지 않는다
				item.setType2(0);
				item.setMaterial((_materialTypes.get(rs.getString("material"))).intValue());
				item.setWeight(rs.getInt("weight"));
				item.setGfxId(rs.getInt("invgfx"));
				item.setGroundGfxId(rs.getInt("grdgfx"));
				item.setItemDescId(rs.getInt("itemdesc_id"));
				item.setMinLevel(rs.getInt("min_lvl"));
				item.setMaxLevel(rs.getInt("max_lvl"));
				item.setBless(rs.getInt("bless"));
				item.setTradable(rs.getInt("trade") == 0 ? true : false);
				item.setCantDelete(rs.getInt("cant_delete") == 1 ? true : false);
				item.setDmgSmall(rs.getInt("dmg_small"));
				item.setDmgLarge(rs.getInt("dmg_large"));
				item.set_stackable(rs.getInt("stackable") == 1 ? true : false);
				item.setMaxChargeCount(rs.getInt("max_charge_count"));
				item.set_locx(rs.getInt("locx"));
				item.set_locy(rs.getInt("locy"));
				item.set_mapid(rs.getShort("mapid"));
				item.set_delayid(rs.getInt("delay_id"));
				item.set_delaytime(rs.getInt("delay_time"));
				item.set_delayEffect(rs.getInt("delay_effect"));
				item.setFoodVolume(rs.getInt("food_volume"));
				item.setToBeSavedAtOnce((rs.getInt("save_at_once") == 1) ? true : false);
				// setAuto(item);//임시
				result.put(new Integer(item.getItemId()), item);
			}
		} catch (NullPointerException e) {
			_log.log(Level.SEVERE, new StringBuilder().append(item.getName()).append("(" + item.getItemId() + ")").append("의 읽어 들이기에 실패했습니다.").toString());
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return result;
	}

	private Map<Integer, L1Weapon> allWeapon() {
		Map<Integer, L1Weapon> result = new HashMap<Integer, L1Weapon>();

		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		L1Weapon weapon = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("select * from weapon");

			rs = pstm.executeQuery();
			while (rs.next()) {
				weapon = new L1Weapon();
				weapon.setItemId(rs.getInt("item_id"));
				weapon.setName(rs.getString("name"));
				weapon.setNameId(rs.getString("name_id"));
				weapon.setType((_weaponTypes.get(rs.getString("type"))).intValue());
				weapon.setType1((_weaponId.get(rs.getString("type"))).intValue());
				weapon.setType2(1);
				weapon.setUseType(1);
				weapon.setMaterial((_materialTypes.get(rs.getString("material"))).intValue());
				weapon.setWeight(rs.getInt("weight"));
				weapon.setGfxId(rs.getInt("invgfx"));
				weapon.setGroundGfxId(rs.getInt("grdgfx"));
				weapon.setItemDescId(rs.getInt("itemdesc_id"));
				weapon.setDmgSmall(rs.getInt("dmg_small"));
				weapon.setDmgLarge(rs.getInt("dmg_large"));
				weapon.set_safeenchant(rs.getInt("safenchant"));
				weapon.setUseRoyal(rs.getInt("use_royal") == 0 ? false : true);
				weapon.setUseKnight(rs.getInt("use_knight") == 0 ? false : true);
				weapon.setUseElf(rs.getInt("use_elf") == 0 ? false : true);
				weapon.setUseMage(rs.getInt("use_mage") == 0 ? false : true);
				weapon.setUseDarkelf(rs.getInt("use_darkelf") == 0 ? false : true);
				weapon.setUseDragonKnight(rs.getInt("use_dragonknight") == 0 ? false : true);
				weapon.setUseBlackwizard(rs.getInt("use_blackwizard") == 0 ? false : true);
				weapon.setUse전사(rs.getInt("use_warrior") == 0 ? false : true);
				weapon.setHitModifier(rs.getInt("hitmodifier"));
				weapon.setDmgModifier(rs.getInt("dmgmodifier"));
				weapon.set_addstr(rs.getByte("add_str"));
				weapon.set_adddex(rs.getByte("add_dex"));
				weapon.set_addcon(rs.getByte("add_con"));
				weapon.set_addint(rs.getByte("add_int"));
				weapon.set_addwis(rs.getByte("add_wis"));
				weapon.set_addcha(rs.getByte("add_cha"));
				weapon.set_addhp(rs.getInt("add_hp"));
				weapon.set_addmp(rs.getInt("add_mp"));
				weapon.set_addhpr(rs.getInt("add_hpr"));
				weapon.set_addmpr(rs.getInt("add_mpr"));
				weapon.set_addsp(rs.getInt("add_sp"));
				weapon.set_mdef(rs.getInt("m_def"));
				weapon.setDoubleDmgChance(rs.getInt("double_dmg_chance"));
				weapon.setIgnoreReductionByWeapon(rs.getInt("ignore_reduction_by_weapon"));
				weapon.setMagicDmgModifier(rs.getInt("magicdmgmodifier"));
				weapon.set_canbedmg(rs.getInt("canbedmg"));
				weapon.set_penetration(rs.getInt("penetration"));
				weapon.setMinLevel(rs.getInt("min_lvl"));
				weapon.setMaxLevel(rs.getInt("max_lvl"));
				weapon.setBless(rs.getInt("bless"));
				weapon.setTradable(rs.getInt("trade") == 0 ? true : false);
				weapon.setCantDelete(rs.getInt("cant_delete") == 1 ? true : false);
				weapon.setHasteItem(rs.getInt("haste_item") == 0 ? false : true);
				weapon.setMaxUseTime(rs.getInt("max_use_time"));
				weapon.setMagicName(rs.getString("Magic_name"));
				// setAuto(weapon);//임시
				result.put(new Integer(weapon.getItemId()), weapon);
			}
		} catch (NullPointerException e) {
			_log.log(Level.SEVERE, new StringBuilder().append(weapon.getName()).append("(" + weapon.getItemId() + ")").append("의 읽어 들이기에 실패했습니다.").toString());
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);

		}
		return result;
	}

	private Map<Integer, L1Armor> allArmor() {
		Map<Integer, L1Armor> result = new HashMap<Integer, L1Armor>();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		L1Armor armor = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("select * from armor");

			rs = pstm.executeQuery();
			while (rs.next()) {
				armor = new L1Armor();
				armor.setItemId(rs.getInt("item_id"));
				armor.setName(rs.getString("name"));
				armor.setNameId(rs.getString("name_id"));
				armor.setType((_armorTypes.get(rs.getString("type"))).intValue());
				// armor.setType1((_armorId
				// .get(rs.getString("armor_type"))).intValue()); // 사용하지 않는다
				armor.setType2(2);
				armor.setUseType((_useTypes.get(rs.getString("type"))).intValue());
				armor.set장신구처리(rs.getInt("acctype"));
				armor.setGrade(rs.getInt("grade"));
				armor.setMaterial((_materialTypes.get(rs.getString("material"))).intValue());
				armor.setWeight(rs.getInt("weight"));
				armor.setGfxId(rs.getInt("invgfx"));
				armor.setGroundGfxId(rs.getInt("grdgfx"));
				armor.setItemDescId(rs.getInt("itemdesc_id"));
				armor.set_ac(rs.getInt("ac"));
				armor.set_safeenchant(rs.getInt("safenchant"));
				armor.setUseRoyal(rs.getInt("use_royal") == 0 ? false : true);
				armor.setUseKnight(rs.getInt("use_knight") == 0 ? false : true);
				armor.setUseElf(rs.getInt("use_elf") == 0 ? false : true);
				armor.setUseMage(rs.getInt("use_mage") == 0 ? false : true);
				armor.setUseDarkelf(rs.getInt("use_darkelf") == 0 ? false : true);
				armor.setUseDragonKnight(rs.getInt("use_dragonknight") == 0 ? false : true);
				armor.setUseBlackwizard(rs.getInt("use_blackwizard") == 0 ? false : true);
				armor.setUse전사(rs.getInt("use_warrior") == 0 ? false : true);
				armor.set_addstr(rs.getByte("add_str"));
				armor.set_addcon(rs.getByte("add_con"));
				armor.set_adddex(rs.getByte("add_dex"));
				armor.set_addint(rs.getByte("add_int"));
				armor.set_addwis(rs.getByte("add_wis"));
				armor.set_addcha(rs.getByte("add_cha"));
				armor.set_addhp(rs.getInt("add_hp"));
				armor.set_addmp(rs.getInt("add_mp"));
				armor.set_addhpr(rs.getInt("add_hpr"));
				armor.set_addmpr(rs.getInt("add_mpr"));
				armor.set_addsp(rs.getInt("add_sp"));
				armor.setMinLevel(rs.getInt("min_lvl"));
				armor.setMaxLevel(rs.getInt("max_lvl"));
				armor.set_mdef(rs.getInt("m_def"));
				armor.setIgnoreReductionByArmor(rs.getInt("ignore_reduction_by_armor"));
				armor.setDamageReduction(rs.getInt("damage_reduction"));
				armor.setWeightReduction(rs.getInt("weight_reduction"));
				armor.setHitRate(rs.getInt("hit_rate"));
				armor.setDmgRate(rs.getInt("dmg_rate"));
				armor.setBowHitRate(rs.getInt("bow_hit_rate"));
				armor.setBowDmgRate(rs.getInt("bow_dmg_rate"));
				armor.setHasteItem(rs.getInt("haste_item") == 0 ? false : true);
				armor.setBless(rs.getInt("bless"));
				armor.setTradable(rs.getInt("trade") == 0 ? true : false);
				armor.setCantDelete(rs.getInt("cant_delete") == 1 ? true : false);
				armor.set_defense_earth(rs.getInt("defense_earth"));
				armor.set_defense_water(rs.getInt("defense_water"));
				armor.set_defense_wind(rs.getInt("defense_wind"));
				armor.set_defense_fire(rs.getInt("defense_fire"));
				armor.set_regist_stun(rs.getInt("regist_stun"));
				armor.set_regist_stone(rs.getInt("regist_stone"));
				armor.set_regist_sleep(rs.getInt("regist_sleep"));
				armor.set_regist_freeze(rs.getInt("regist_freeze"));
				armor.set_regist_sustain(rs.getInt("regist_sustain"));
				armor.set_regist_blind(rs.getInt("regist_blind"));
				armor.set_regist_DESPERADO(rs.getInt("regist_DESPERADO"));
				armor.set_regist_calcPcDefense(rs.getInt("PVPcalcPcDefense"));
				armor.set_regist_PVPweaponTotalDamage(rs.getInt("PVPweaponTotalDamage"));
				armor.setMaxUseTime(rs.getInt("max_use_time"));
				armor.setMainId(rs.getInt("MainId"));
				armor.setMainId2(rs.getInt("MainId2"));
				armor.setMainId3(rs.getInt("MainId3"));
				armor.setSetId(rs.getInt("Set_Id"));
				armor.setMagicName(rs.getString("Magic_name"));
				// setAuto(armor);//임시
				result.put(new Integer(armor.getItemId()), armor);
			}
		} catch (NullPointerException e) {
			_log.log(Level.SEVERE, new StringBuilder().append(armor.getName()).append("(" + armor.getItemId() + ")").append("의 읽어 들이기에 실패했습니다.").toString());
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);

		}
		return result;
	}

	/* 버경 추가 */
	public void initRace() {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			{
				pstm = con.prepareStatement("delete from character_items where item_id > 8000000");
				pstm.execute();
				pstm.close();
			}
			{
				pstm = con.prepareStatement("delete from character_elf_warehouse where item_id > 8000000");
				pstm.execute();
				pstm.close();
			}
			{
				pstm = con.prepareStatement("delete from character_warehouse where item_id > 8000000");
				pstm.execute();
				pstm.close();
			}
			{
				pstm = con.prepareStatement("delete from clan_warehouse where item_id > 8000000");
				pstm.execute();
				pstm.close();
			}
			{
				pstm = con.prepareStatement("delete from character_package_warehouse where item_id > 8000000");
				pstm.execute();
				pstm.close();
			}
			pstm = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);

		}
	}

	private void buildFastLookupTable() {
		_allTemplates = new L1Item[MAX_ITEM_ID];

		L1EtcItem etcitem = null;
		for (Iterator<Integer> iter = _etcitems.keySet().iterator(); iter.hasNext();) {
			Integer id = iter.next();
			etcitem = _etcitems.get(id);
			_allTemplates[id.intValue()] = etcitem;
		}
		L1Weapon Weaponitem = null;
		for (Iterator<Integer> iter = _weapons.keySet().iterator(); iter.hasNext();) {
			Integer id = iter.next();
			Weaponitem = _weapons.get(id);
			_allTemplates[id.intValue()] = Weaponitem;
		}

		L1Armor Amoritem = null;
		for (Iterator<Integer> iter = _armors.keySet().iterator(); iter.hasNext();) {
			Integer id = iter.next();
			Amoritem = _armors.get(id);
			_allTemplates[id.intValue()] = Amoritem;
		}

		L1RaceTicket RaceTicketitem = null;
		for (Iterator<Integer> iter = _race.keySet().iterator(); iter.hasNext();) {
			Integer id = iter.next();
			RaceTicketitem = _race.get(id);
			_allTemplates[id.intValue()] = RaceTicketitem;
		}
	}

	public L1Item getTemplate(int id) {
		return _allTemplates[id];
	}

	public L1ItemInstance createItem(int itemId) {
		L1Item temp = getTemplate(itemId);
		if (temp == null) {
			return null;
		}
		L1ItemInstance item = new L1ItemInstance();
		item.setId(IdFactory.getInstance().nextId());
		item.setItem(temp);
		item.setBless(temp.getBless());
		L1World.getInstance().storeObject(item);
		return item;
	}
	
	 public String findItemIdByName(int itemid) {
	        String name = null;
	        for (L1Item item : _allTemplates) {
	            if (item != null && item.getItemId() == itemid) {
	                name = item.getName();
	                return name;
	            }
	        }
	        return null;
	    }

	public int findItemIdByName(String name) {
		int itemid = 0;
		for (L1Item item : _allTemplates) {
			if (item != null && item.getName().equals(name)) {
				itemid = item.getItemId();
				break;
			}
		}
		return itemid;
	}

	public int findItemIdByNameWithoutSpace(String name) {
		int itemid = 0;
		for (L1Item item : _allTemplates) {
			if (item != null && item.getName().replace(" ", "").equals(name)) {
				itemid = item.getItemId();
				break;
			}
		}
		return itemid;
	}

	/* 버경 추가 */
	public void AddTicket(L1RaceTicket race) {
		_race.put(new Integer(race.getItemId()), race);
		_allTemplates[race.getItemId()] = race;
	}

	public int GetIssuedTicket() {
		return _race.size();
	}

	/** 새로운 Template 객체를 생성(복사) */
	public L1Item clone(L1Item item, String name) {
		// 이름때문에..
		// 무기
		if (item.getType2() == 1) {
			L1Weapon weapon = new L1Weapon();
			weapon.setItemId(item.getItemId());
			weapon.setName(item.getName());
			weapon.setNameId(item.getNameId());
			weapon.setType(item.getType());
			weapon.setType1(item.getType1());
			weapon.setType2(1);
			weapon.setUseType(1);
			weapon.setMaterial(item.getMaterial());
			weapon.setWeight(item.getWeight());
			weapon.setGfxId(item.getGfxId());
			weapon.setGroundGfxId(item.getGroundGfxId());
			weapon.setItemDescId(item.getItemDescId());
			weapon.setDmgSmall(item.getDmgSmall());
			weapon.setDmgLarge(item.getDmgLarge());
			weapon.set_safeenchant(item.get_safeenchant());
			weapon.setUseRoyal(item.isUseRoyal());
			weapon.setUseKnight(item.isUseKnight());
			weapon.setUseElf(item.isUseElf());
			weapon.setUseMage(item.isUseMage());
			weapon.setUseDarkelf(item.isUseDarkelf());
			weapon.setUseDragonKnight(item.isUseDragonKnight());
			weapon.setUseBlackwizard(item.isUseBlackwizard());
			weapon.setHitModifier(item.getHitModifier());
			weapon.setDmgModifier(item.getDmgModifier());
			weapon.set_addstr(item.get_addstr());
			weapon.set_adddex(item.get_adddex());
			weapon.set_addcon(item.get_addcon());
			weapon.set_addint(item.get_addint());
			weapon.set_addwis(item.get_addwis());
			weapon.set_addcha(item.get_addcha());
			weapon.set_addhp(item.get_addhp());
			weapon.set_addmp(item.get_addmp());
			weapon.set_addhpr(item.get_addhpr());
			weapon.set_addmpr(item.get_addmpr());
			weapon.set_addsp(item.get_addsp());
			weapon.set_mdef(item.get_mdef());
			weapon.setDoubleDmgChance(item.getDoubleDmgChance());
			weapon.setIgnoreReductionByWeapon(item.getIgnoreReductionByWeapon());
			weapon.setMagicDmgModifier(item.getMagicDmgModifier());
			weapon.set_canbedmg(item.get_canbedmg());
			weapon.set_penetration(item.get_penetration());
			weapon.setMinLevel(item.getMinLevel());
			weapon.setMaxLevel(item.getMaxLevel());
			weapon.setBless(item.getBless());
			weapon.setTradable(item.isTradable());
			weapon.setCantDelete(item.isCantDelete());
			weapon.setHasteItem(item.isHasteItem());
			weapon.setMaxUseTime(item.getMaxUseTime());
			return weapon;
			// 방어구
		} else if (item.getType2() == 2) {
			L1Armor armor = new L1Armor();
			armor.setItemId(item.getItemId());
			armor.setName(item.getName());
			armor.setNameId(item.getNameId());
			armor.setType(item.getType());
			armor.setType2(2);
			armor.setUseType(item.getUseType());
			armor.setMaterial(item.getMaterial());
			armor.setWeight(item.getWeight());
			armor.setGfxId(item.getGfxId());
			armor.setGroundGfxId(item.getGroundGfxId());
			armor.setItemDescId(item.getItemDescId());
			armor.set_ac(item.get_ac());
			armor.set_safeenchant(item.get_safeenchant());
			armor.setUseRoyal(item.isUseRoyal());
			armor.setUseKnight(item.isUseKnight());
			armor.setUseElf(item.isUseElf());
			armor.setUseMage(item.isUseMage());
			armor.setUseDarkelf(item.isUseDarkelf());
			armor.setUseDragonKnight(item.isUseDragonKnight());
			armor.setUseBlackwizard(item.isUseBlackwizard());
			armor.set_addstr(item.get_addstr());
			armor.set_addcon(item.get_addcon());
			armor.set_adddex(item.get_adddex());
			armor.set_addint(item.get_addint());
			armor.set_addwis(item.get_addwis());
			armor.set_addcha(item.get_addcha());
			armor.set_addhp(item.get_addhp());
			armor.set_addmp(item.get_addmp());
			armor.set_addhpr(item.get_addhpr());
			armor.set_addmpr(item.get_addmpr());
			armor.set_addsp(item.get_addsp());
			armor.setMinLevel(item.getMinLevel());
			armor.setMaxLevel(item.getMaxLevel());
			armor.set_mdef(item.get_mdef());
			armor.setDamageReduction(item.getDamageReduction());
			armor.setIgnoreReductionByArmor(item.getIgnoreReductionByArmor());
			armor.setWeightReduction(item.getWeightReduction());
			armor.setBowHitRate(item.getBowHitRate());
			armor.setHasteItem(item.isHasteItem());
			armor.setBless(item.getBless());
			armor.setTradable(item.isTradable());
			armor.setCantDelete(item.isCantDelete());
			armor.set_defense_earth(item.get_defense_earth());
			armor.set_defense_water(item.get_defense_water());
			armor.set_defense_wind(item.get_defense_wind());
			armor.set_defense_fire(item.get_defense_fire());
			armor.set_regist_stun(item.get_regist_stun());
			armor.set_regist_stone(item.get_regist_stone());
			armor.set_regist_sleep(item.get_regist_sleep());
			armor.set_regist_freeze(item.get_regist_freeze());
			armor.set_regist_sustain(item.get_regist_sustain());
			armor.set_regist_blind(item.get_regist_blind());
			armor.set_regist_DESPERADO(item.get_regist_DESPERADO());
			armor.set_regist_calcPcDefense(item.get_regist_calcPcDefense());
			armor.set_regist_PVPweaponTotalDamage(item.get_regist_PVPweaponTotalDamage());
			armor.setMaxUseTime(item.getMaxUseTime());
			return armor;
		} else if (item.getType2() == 0) {
			L1EtcItem etc = new L1EtcItem();
			etc.setItemId(item.getItemId());
			etc.setName(name);
			etc.setNameId(item.getNameId());
			etc.setType(item.getType());
			etc.setUseType(item.getUseType());
			etc.setType2(0);
			etc.setMaterial(item.getMaterial());
			etc.setWeight(item.getWeight());
			etc.setGfxId(item.getGfxId());
			etc.setGroundGfxId(item.getGroundGfxId());
			etc.setItemDescId(item.getItemDescId());
			etc.setMinLevel(item.getMinLevel());
			etc.setMaxLevel(item.getMaxLevel());
			etc.setBless(item.getBless());
			etc.setTradable(item.isTradable());
			etc.setCantDelete(item.isCantDelete());
			etc.setDmgSmall(item.getDmgSmall());
			etc.setDmgLarge(item.getDmgLarge());
			etc.set_stackable(item.isStackable());
			etc.setMaxChargeCount(item.getMaxChargeCount());
			etc.set_locx(item.get_locx());
			etc.set_locy(item.get_locy());
			etc.set_mapid(item.get_mapid());
			etc.set_delayid(item.get_delayid());
			etc.set_delaytime(item.get_delaytime());
			etc.set_delayEffect(item.get_delayEffect());
			etc.setFoodVolume(item.getFoodVolume());
			etc.setToBeSavedAtOnce(item.isToBeSavedAtOnce());
			return etc;
		}
		return null;
	}
}
