package l1j.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.utils.IntRange;

public final class Config {
    private static final Logger _log = Logger.getLogger(Config.class.getName());

    /**
     * ロボットシステム
     **/
    public static String ROBOT_NAME;
    /** ロボットシステム **/

    /**
     * 製作のテーブルアイテムの検索用
     **/
    public static int CRAFT_TABLE_ONE;
    public static int CRAFT_TABLE_TWO;
    public static int CRAFT_TABLE_THREE;
    public static int CRAFT_TABLE_FOUR;
    public static int CRAFT_TABLE_FIVE;
    public static int CRAFT_TABLE_SIX;
    public static int CRAFT_TABLE_SEVEN;
    public static int CRAFT_TABLE_EIGHT;
    public static int CRAFT_TABLE_NINE;
    public static int CRAFT_TABLE_TEN;
    public static int CRAFT_TABLE;
    public static int GLUDIN_DUNGEON_OPEN_CYCLE;

    public static String EVENTITEM_ITEMID;
    public static String EVENTITEM_NUMBER;

    public static int SHOCK_STUN;
    public static int ARMOR_BREAK;
    public static int COUNTER_BARRIER;
    public static int DESPERADO;
    public static int POWER_GRIP;

    public static int ERASE_MAGIC;

    public static int EARTH_BIND;
    public static int WIND_SHACKLE;
    public static int BONE_BREAK;
    public static int DEATH_HEAL;
    public static int CRAFTSMAN_WEAPON_SCROLL;
    public static int BLESS_SCROLL;
    public static int ORIM_SCROLL;
    public static int CREST_ENCHANT_CHANCE;

    // アーノルドイベント
    public static int ARNOLD_EVENT_TIME;
    public static boolean ARNOLD_EVENTS;
    public static boolean SUPPLY_SCARECROW_TAM;
    public static boolean PURE_WHITE_T;

    // ルームティス黒い光のイヤリングダメージ追加追加確率
    public static int ROOMTIECE_CHANCE; // 黒い光ピアス追加ダメージ確率
    public static int FEATHER_SHOP_NUM; // 羽店の使用本数制限
    public static int FEATHER_TIME; // 羽支給時間
    public static int FISH_EXP; // 成長釣りの基本経験値獲得量
    public static int FISH_TIME; // 成長釣り時間
    public static boolean FISH_COM; // 成長釣りギフト

    // 強化バフ（活力、攻撃、魔法、ホールド、スタン、防御）時間外部化
    // 強化バフ_活力、強化バフ_攻撃、強化バフ_防御、強化バフ_魔法、強化バフ_スターン、強化バフ_ホールド
    public static int ENCHANT_BUFF_TIME_VITALITY;
    public static int ENCHANT_BUFF_TIME_ATTACK;
    public static int ENCHANT_BUFF_TIME_DEFENCE;
    public static int ENCHANT_BUFF_TIME_MAGIC;
    public static int ENCHANT_BUFF_TIME_STUN;
    public static int ENCHANT_BUFF_TIME_HOLD;
    public static int ENCHANT_BUFF_TIME_STR;
    public static int ENCHANT_BUFF_TIME_DEX;
    public static int ENCHANT_BUFF_TIME_INT;

    public static int RAID_TIME;

    public static int STAGE_1;
    public static int STAGE_2;
    public static int STAGE_3;

    /**
     * NPC 物理ダメージ/魔法ダメージ
     **/
    public static int npcdmg;
    public static int npcmagicdmg;

    /**
     * ギラン監獄週末イベント用
     **/
    public static int mapid;
    public static int mapid1;
    public static int mapid2;
    public static int mapid3;
    public static int mapid4;
    public static int mapid5;
    public static int EXP;
    public static int EVENT_ITEMS;
    public static int EVENT_NUMBERS;

    // 血盟経験値
    public static int CLAN_EXP_ONE;
    public static int CLAN_EXP_TWO;
    public static int CLAN_EXP_THREE;
    public static int CLAN_EXP_FOUR;
    public static int CLAN_EXP_FIVE;
    public static int CLAN_EXP_SIX;
    public static int CLAN_EXP_SEVEN;

    public static int ADEN_SHOP_NPC;

    public static int Tam_Time;

    /**
     * 階級別アビスポイントの外部化
     **/
    public static int ABYSS_POINT; // ギラン監獄1〜4階狩りの時得るポイントの幅（ランダム）
    public static int CLASS_START_LEVEL; // 階級算定開始レベル
    public static int SUPREMECOMMANDER; // 総司令官
    public static int COMMANDER; // 司令官
    public static int IMPERATOR; // 大将軍
    public static int GENERAL; // 将軍
    public static int STAR_FIVE; // 5将校
    public static int STAR_FOUR; // 4将校
    public static int STAR_THREE; // 3将校
    public static int STAR_TWO; // 2将校
    public static int STAR_ONE; // 1将校
    public static int ONE_CLASS; // 1等兵
    public static int TWO_CLASS; // 2等兵
    public static int THREE_CLASS; // 3等兵
    public static int FOUR_CLASS; // 四等兵
    public static int FIVE_CLASS; // 五等兵
    public static int SIX_CLASS; // 六等兵
    public static int SEVEN_CLASS; // 七等兵
    public static int EIGHT_CLASS; // 八等兵
    public static int NINE_CLASS; // 九等兵

    public static int SUPREMECOMMANDER_DAMAGE; // 総司令官
    public static int COMMANDER_DAMAGE; // 司令官
    public static int IMPERATOR_DAMAGE; // 大将軍
    public static int GENERAL_DAMAGE; // 将軍
    public static int STAR_FIVE_DAMAGE; // 5将校

    /**
     * サーバーマネージャー
     **/
    public static boolean normal = false;
    public static boolean world = false;
    public static boolean whisper = false;
    public static boolean alliance = false;
    public static boolean party = false;
    public static boolean shout = false;
    public static boolean business = false;
    public static boolean shutdownCheck = false;
    /** サーバーマネージャー **/

    /**
     * Debug/release mode
     */
    public static final boolean DEBUG = false;
    public static boolean STANDBY_SERVER = false;

    /**
     * Thread pools size
     */
    public static int THREAD_P_EFFECTS;
    public static int THREAD_P_GENERAL;
    public static int AI_MAX_THREAD;
    public static int THREAD_P_SIZE_GENERAL;
    public static int THREAD_P_TYPE_GENERAL;
    public static int SELECT_THREAD_COUNT;

    /**
     * Server control
     */
    public static String GAME_SERVER_NAME;
    public static int GAME_SERVER_PORT;
    public static int AD_REPORT_SERVER_PORT;
    public static String DB_DRIVER;
    public static String DB_URL;
    public static String DB_LOGIN;
    public static String DB_PASSWORD;
    public static String TIME_ZONE;
    public static int CLIENT_LANGUAGE;
    public static boolean HOSTNAME_LOOKUPS;
    public static int AUTOMATIC_KICK;
    public static boolean AUTO_CREATE_ACCOUNTS;
    public static short MAX_ONLINE_USERS;
    public static boolean CACHE_MAP_FILES;
    public static boolean LOAD_V2_MAP_FILES;
    public static boolean CHECK_MOVE_INTERVAL;
    public static boolean CHECK_ATTACK_INTERVAL;
    public static boolean CHECK_SPELL_INTERVAL;
    public static byte LOGGING_WEAPON_ENCHANT;
    public static byte LOGGING_ARMOR_ENCHANT;
    public static boolean LOGGING_CHAT_NORMAL;
    public static boolean LOGGING_CHAT_WHISPER;
    public static boolean LOGGING_CHAT_SHOUT;
    public static boolean LOGGING_CHAT_WORLD;
    public static boolean LOGGING_CHAT_CLAN;
    public static boolean LOGGING_CHAT_PARTY;
    public static boolean LOGGING_CHAT_COMBINED;
    public static boolean LOGGING_CHAT_CHAT_PARTY;

    public static boolean BROADCAST_KILL_LOG;
    public static int BROADCAST_KILL_LOG_LEVEL;
    public static double ACCEL_ALLOW;

    public static String PRIVATE_SHOP_CHAT;
    public static int STORE_USAGE_LEVEL;

    public static int HAJA;

    public static int PC_RECOGNIZE_RANGE;
    public static boolean CHARACTER_CONFIG_IN_SERVER_SIDE;
    public static boolean ALLOW_2PC;
    public static boolean CHECK_AUTO;
    public static boolean CHECK_AUTO_ENCHANT;
    public static int LEVEL_DOWN_RANGE;
    public static boolean SEND_PACKET_BEFORE_TELEPORT;
    public static boolean DETECT_DB_RESOURCE_LEAKS;
    public static boolean AUTH_CONNECT; // TODO LINALL CONNECT
    public static int AUTH_KEY; // TODO LINALL CONNECT
    public static int AUTH_IP;

    /**
     * Rate control
     */

    public static int FG_ISVAL; // 忘れられた島レプジェ

    public static double RATE_XP;
    public static double RATE_LAWFUL;
    public static double RATE_KARMA;
    public static double RATE_DROP_ADENA;
    public static double RATE_DROP_ITEMS;
    public static double RATE_DROP_RABBIT;// 辛卯年のイベント
    public static int ENCHANT_CHANCE_WEAPON;
    public static int ENCHANT_CHANCE_ARMOR;
    public static int ENCHANT_CHANCE_ACCESSORY;
    public static int ARNOLD_WEAPON_CHANCE;
    public static double RATE_WEIGHT_LIMIT;
    public static double RATE_WEIGHT_LIMIT_PET;
    public static double RATE_SHOP_SELLING_PRICE;
    public static double RATE_SHOP_PURCHASING_PRICE;

    public static int CREATE_CHANCE_DIARY;
    public static int CREATE_CHANCE_RECOLLECTION;
    public static int CREATE_CHANCE_MYSTERIOUS;
    public static int CREATE_CHANCE_PROCESSING;
    public static int CREATE_CHANCE_PROCESSING_DIAMOND;
    public static int CREATE_CHANCE_DANTES;
    public static int CREATE_CHANCE_ANCIENT_AMULET;
    public static int CREATE_CHANCE_HISTORY_BOOK;

    public static int FEATHER_NUM;
    public static int FEATHER_NUM1;
    public static int FEATHER_NUM2;
    public static int FEATHER_NUM3;

    public static int TAM_COUNT;
    public static int TAM_CLAN_COUNT;
    public static int TAM_EX_COUNT;
    public static int TAM_EX2_COUNT;

    public static boolean BATTLE_ZONE_OPERATION;

    public static boolean TI_DUNGEON_FEATHER;
    public static boolean GLUDIO_DUNGEON_FEATHER;
    public static boolean SKT_DUNGEON_FEATHER;
    public static boolean FI_FEATHER;

    public static int BATTLE_ZONE_ENTRY_LEVEL;
    public static String BATTLE_ZONE_ITEM;
    public static String BATTLE_ZONE_ITEM_COUNT;

    public static String PIC_BOOK_1_ITEM;
    public static String PIC_BOOK_1_ITEM_COUNT;

    public static String PIC_BOOK_2_ITEM;
    public static String PIC_BOOK_2_ITEM_COUNT;

    public static String PIC_BOOK_3_ITEM;
    public static String PIC_BOOK_3_ITEM_COUNT;

    public static boolean DEMON_KING_OPERATION;
    public static boolean BOSS_SPAWN_OPERATION;
    public static boolean ALL_GIFT_OPERATION;
    public static int DEMON_KING_ENTRY_LEVEL;
    public static int DEMON_KING_TIME;

    public static boolean ADEN_HUNTING_OPERATION;
    public static int ADEN_HUNTING_ENTRY_LEVEL;
    public static int ADEN_HUNTING_TIME;

    /**
     * イベント靴下支給
     **/
    public static boolean SOCKS_OPERATION;
    public static boolean EVENT_A_OPERATION;
    public static int EVENT_TIME;
    public static int EVENT_NUMBER;
    public static int EVENT_ITEM;

    public static int systime;
    public static String sys1;
    public static String sys2;
    public static String sys3;
    public static String sys4;
    public static String sys5;
    public static String sys6;
    public static String sys7;
    public static String sys8;
    public static String sys9;
    public static String sys10;
    public static String sys11;
    public static String sys12;
    public static String sys13;
    public static String sys14;
    public static String sys15;
    public static String sys16;

    /**
     * モンスタージュクウェレベルの難易度
     **/
    public static int WeekLevel1;
    public static int WeekLevel2;

    /**
     * ミニ攻城補償段階
     **/
    public static int Tower;
    public static int MTower;
    public static int LTower;

    public static int TowerC;
    public static int MTowerC;
    public static int LTowerC;

    public static double RATE_7_DMG_RATE;// エンチャンツタ外部化
    public static int RATE_7_DMG_PER;
    public static double RATE_8_DMG_RATE;
    public static int RATE_8_DMG_PER;
    public static double RATE_9_DMG_RATE;
    public static int RATE_9_DMG_PER;
    public static double RATE_10_DMG_RATE;
    public static int RATE_10_DMG_PER;
    public static double RATE_11_DMG_RATE;
    public static int RATE_11_DMG_PER;
    public static double RATE_12_DMG_RATE;
    public static int RATE_12_DMG_PER;
    public static double RATE_13_DMG_RATE;
    public static int RATE_13_DMG_PER;
    public static double RATE_14_DMG_RATE;
    public static int RATE_14_DMG_PER;
    public static double RATE_15_DMG_RATE;
    public static int RATE_15_DMG_PER;
    public static double RATE_16_DMG_RATE;
    public static int RATE_16_DMG_PER;
    public static double RATE_17_DMG_RATE;
    public static int RATE_17_DMG_PER;
    public static double RATE_18_DMG_RATE;
    public static int RATE_18_DMG_PER;

    public static int DISCHARGE1;
    public static int DISCHARGE2;
    public static int DISCHARGE3;
    public static int DISCHARGE4;
    public static int DISCHARGE5;
    public static int DISCHARGE6;
    public static int DISCHARGE7;

    /**
     * AltSettings control
     */
    public static short GLOBAL_CHAT_LEVEL;
    public static short WHISPER_CHAT_LEVEL;
    public static byte AUTO_LOOT;
    public static int LOOTING_RANGE;
    public static boolean ALT_NONPVP;
    public static boolean ALT_ATKMSG;
    public static boolean CHANGE_TITLE_BY_ONESELF;
    public static int MAX_CLAN_MEMBER;
    public static boolean CLAN_ALLIANCE;
    public static int MAX_PT;
    public static int MAX_CHAT_PT;
    public static boolean SIM_WAR_PENALTY;
    public static boolean GET_BACK;
    public static String ALT_ITEM_DELETION_TYPE;
    public static int ALT_ITEM_DELETION_TIME;
    public static int ALT_ITEM_DELETION_RANGE;
    public static boolean ALT_GMSHOP;
    public static int ALT_GMSHOP_MIN_ID;
    public static int ALT_GMSHOP_MAX_ID;
    public static boolean ALT_BASETOWN;
    public static int ALT_BASETOWN_MIN_ID;
    public static int ALT_BASETOWN_MAX_ID;
    public static boolean ALT_HALLOWEENIVENT;
    public static int WHOIS_CONTER;
    public static boolean ALT_BEGINNER_BONUS;
    public static int NOTIS_TIME; // お知らせ
    public static int CLAN_COUNT; // 血盟バフ人員
    public static double EX_EXP;
    public static int AUTO_REMOVELEVEL;
    public static int BUFFLEVEL; // バフレベル以下
    // とりあえずわかんないから和訳まんまえローマ字に
    // 「つつくウィザード」
    public static int TSUTSUKUGEN;

    public static int NORMAL_PROTECTION;

    public static int LASTAVARD_ENTRY_LEVEL; // ラバー入場レベル
    public static double WIZARD_MAGIC_DAMAGE; // ウィザードツタ
    public static double WIZARD_MONSTER_DAMAGE; // ウィザードツタ

    /**
     * 新たに追加
     **/
    public static int useritem;
    public static int usercount;

    public static int KIRINGKU;
    public static int DEATH_KNIGHT_HELLFIRE;
    public static int DOLL_CHANCE;
    public static int LIGHT_OF_ETERNAL_LIFE;
    public static int LEAVES_OF_LIFE;
    public static double KNIGHT;
    public static double DRAGON_KNIGHT;
    public static double ELF;
    public static double CROWN;
    public static double WIZARD;
    public static double DARK_ELF;
    public static double ILLUSIONIST;
    public static double WARRIOR;
    public static double RECOVERY_EXP;
    public static boolean EXP_POT_LIMIT;

    public static int NEW_CLAN_PROTECTION_LEVEL;
    public static int NEW_CLAN;
    public static boolean NEW_CLAN_PROTECTION_PROCESS;

    public static int WEAPON_ENCHANT;
    public static int WEAPON_PREMIUM_ENCHANT;
    public static int ARMOR_ENCHANT;
    public static int ARMOR_PREMIUM_ENCHANT;
    public static int ROOMTIS;
    public static int SNAPPER;
    public static int ACCESSORIES;

    public static boolean SPAWN_ROBOT;

    /**
     * [幻想イベント本サーバー化]
     **/
    public static boolean ALT_FANTASYEVENT;
    public static boolean ALT_RABBITEVENT; // 神妙イベント（2011）
    public static boolean ALT_FISHEVENT;

    public static int EXP_PAYMENT_TEAM; // 経験値支給団
    public static int DECLARATION_LEVEL; // 宣言レベル
    public static int CLAN_CONNECT_COUNT; // 血盟接続人数

    public static boolean ALT_JPPRIVILEGED;
    public static boolean ALT_WHO_COMMAND;
    public static boolean ALT_REVIVAL_POTION;
    public static int ALT_WAR_TIME;
    public static int ALT_WAR_TIME_UNIT;
    public static int ALT_WAR_INTERVAL;
    public static int ALT_WAR_INTERVAL_UNIT;
    public static int ALT_RATE_OF_DUTY;
    public static boolean SPAWN_HOME_POINT;
    public static int SPAWN_HOME_POINT_RANGE;
    public static int SPAWN_HOME_POINT_COUNT;
    public static int SPAWN_HOME_POINT_DELAY;
    public static boolean INIT_BOSS_SPAWN;
    public static int ELEMENTAL_STONE_AMOUNT;
    public static int HOUSE_TAX_INTERVAL;
    public static int MAX_DOLL_COUNT;
    public static boolean RETURN_TO_NATURE;
    public static int MAX_NPC_ITEM;
    public static int MAX_PERSONAL_WAREHOUSE_ITEM;
    public static int MAX_CLAN_WAREHOUSE_ITEM;
    public static boolean DELETE_CHARACTER_AFTER_7DAYS;

    public static int GMCODE;
    public static int NEW_PLAYER;
    public static int ALT_DROPLEVELLIMIT;

    public static int DVC_ENTRY_LEVEL;
    public static int DVC_LIMIT_LEVEL;
    public static int HC_ENTRY_LEVEL;
    public static int HC_LIMIT_LEVEL;

    public static int TIC_ENTRY_LEVEL;
    public static int TIC_LIMIT_LEVEL;
    public static int SKTC_ENTRY_LEVEL;
    public static int SKTC_LIMIT_LEVEL;
    public static int DISCARDED_LAND_ENTRY_LEVEL;

    public static int ANCIENT_WEAPON;
    public static int ANCIENT_ARMOR;

    public static boolean Use_Show_Announcecycle; // 追加
    public static int Show_Announcecycle_Time; // 追加

    public static int HELL_TIME;
    public static int HELL_LEVEL;

    /**
     * CharSettings control
     */
    public static int PRINCE_MAX_HP;
    public static int PRINCE_MAX_MP;
    public static int KNIGHT_MAX_HP;
    public static int KNIGHT_MAX_MP;
    public static int ELF_MAX_HP;
    public static int ELF_MAX_MP;
    public static int WIZARD_MAX_HP;
    public static int WIZARD_MAX_MP;
    public static int DARKELF_MAX_HP;
    public static int DARKELF_MAX_MP;
    public static int DRAGONKNIGHT_MAX_HP;
    public static int DRAGONKNIGHT_MAX_MP;
    public static int BLACKWIZARD_MAX_HP;
    public static int BLACKWIZARD_MAX_MP;

    public static int WARRIOR_MAX_HP;
    public static int WARRIOR_MAX_MP;

    public static int PRINCE_ADD_DAMAGEPC;
    public static int KNIGHT_ADD_DAMAGEPC;
    public static int ELF_ADD_DAMAGEPC;
    public static int WIZARD_ADD_DAMAGEPC;
    public static int DARKELF_ADD_DAMAGEPC;
    public static int DRAGONKNIGHT_ADD_DAMAGEPC;
    public static int BLACKWIZARD_ADD_DAMAGEPC;
    public static int WARRIOR_ADD_DAMAGEPC;

    public static int LIMITLEVEL;
    public static int LV50_EXP;
    public static int LV51_EXP;
    public static int LV52_EXP;
    public static int LV53_EXP;
    public static int LV54_EXP;
    public static int LV55_EXP;
    public static int LV56_EXP;
    public static int LV57_EXP;
    public static int LV58_EXP;
    public static int LV59_EXP;
    public static int LV60_EXP;
    public static int LV61_EXP;
    public static int LV62_EXP;
    public static int LV63_EXP;
    public static int LV64_EXP;
    public static int LV65_EXP;
    public static int LV66_EXP;
    public static int LV67_EXP;
    public static int LV68_EXP;
    public static int LV69_EXP;
    public static int LV70_EXP;
    public static int LV71_EXP;
    public static int LV72_EXP;
    public static int LV73_EXP;
    public static int LV74_EXP;
    public static int LV75_EXP;
    public static int LV76_EXP;
    public static int LV77_EXP;
    public static int LV78_EXP;
    public static int LV79_EXP;
    public static int LV80_EXP;
    public static int LV81_EXP;
    public static int LV82_EXP;
    public static int LV83_EXP;
    public static int LV84_EXP;
    public static int LV85_EXP;
    public static int LV86_EXP;
    public static int LV87_EXP;
    public static int LV88_EXP;
    public static int LV89_EXP;
    public static int LV90_EXP;
    public static int LV91_EXP;
    public static int LV92_EXP;
    public static int LV93_EXP;
    public static int LV94_EXP;
    public static int LV95_EXP;
    public static int LV96_EXP;
    public static int LV97_EXP;
    public static int LV98_EXP;
    public static int LV99_EXP;

    /**
     * データベースフル関連
     */
    public static int min;
    public static int max;
    public static boolean run;

    // デバッグ用パラメータ
    public static boolean SHOW_CLIENT_PACKET;

    /**
     * Configuration files
     */
    public static final String SERVER_CONFIG_FILE = "./config/server.properties";
    public static final String RATES_CONFIG_FILE = "./config/rates.properties";
    public static final String ALT_SETTINGS_FILE = "./config/altsettings.properties";
    public static final String CHAR_SETTINGS_CONFIG_FILE = "./config/charsettings.properties";
    public static final String CHOLONG_SETTINGS_CONFIG_FILE = "./config/Eventlink.properties";
    /**
     * その他の設定
     */

    // NPCからは飲むことができるMPの限界
    public static final int MANA_DRAIN_LIMIT_PER_NPC = 40;

    // 1回の攻撃では、飲むことができるMP限界（SOM、鋼鉄SOM）
    public static final int MANA_DRAIN_LIMIT_PER_SOM_ATTACK = 9;

    public static void load() {
        _log.info("loading gameserver config");
        /** server.properties **/
        try {
            Properties serverSettings = new Properties();
            InputStream is = new FileInputStream(new File(SERVER_CONFIG_FILE));
            serverSettings.load(is);
            is.close();

            /** データベースフル */
            min = Integer.parseInt(serverSettings.getProperty("min"));
            max = Integer.parseInt(serverSettings.getProperty("max"));
            run = Boolean.parseBoolean(serverSettings.getProperty("run"));

            GAME_SERVER_NAME = serverSettings.getProperty("GameServerName", "フォアザサーバー");
            GAME_SERVER_PORT = Integer.parseInt(serverSettings.getProperty("GameserverPort", "2000"));

            System.out.println("G:" + GAME_SERVER_PORT);
            AD_REPORT_SERVER_PORT = Integer.parseInt(serverSettings.getProperty("AdReportServerPort", "18182"));
            DB_DRIVER = serverSettings.getProperty("Driver", "com.mysql.jdbc.Driver");
            DB_URL = serverSettings.getProperty("URL",
                    "jdbc:mysql://localhost/l1jdb?useUnicode=true&characterEncoding=utf8");
            DB_LOGIN = serverSettings.getProperty("Login", "root");
            DB_PASSWORD = serverSettings.getProperty("Password", "");
            THREAD_P_TYPE_GENERAL = Integer.parseInt(serverSettings.getProperty("GeneralThreadPoolType", "0"), 10);
            THREAD_P_SIZE_GENERAL = Integer.parseInt(serverSettings.getProperty("GeneralThreadPoolSize", "0"), 10);
            SELECT_THREAD_COUNT = Integer.parseInt(serverSettings.getProperty("IoThreadPoolSize", "4"));
            CLIENT_LANGUAGE = Integer.parseInt(serverSettings.getProperty("ClientLanguage", "4"));
            TIME_ZONE = serverSettings.getProperty("TimeZone", "JST");
            HOSTNAME_LOOKUPS = Boolean.parseBoolean(serverSettings.getProperty("HostnameLookups", "false"));
            AUTOMATIC_KICK = Integer.parseInt(serverSettings.getProperty("AutomaticKick", "10"));
            AUTO_CREATE_ACCOUNTS = Boolean.parseBoolean(serverSettings.getProperty("AutoCreateAccounts", "true"));
            MAX_ONLINE_USERS = Short.parseShort(serverSettings.getProperty("MaximumOnlineUsers", "30"));
            CACHE_MAP_FILES = Boolean.parseBoolean(serverSettings.getProperty("CacheMapFiles", "false"));
            LOAD_V2_MAP_FILES = Boolean.parseBoolean(serverSettings.getProperty("LoadV2MapFiles", "false"));
            LOGGING_WEAPON_ENCHANT = Byte.parseByte(serverSettings.getProperty("LoggingWeaponEnchant", "0"));
            LOGGING_ARMOR_ENCHANT = Byte.parseByte(serverSettings.getProperty("LoggingArmorEnchant", "0"));
            LOGGING_CHAT_NORMAL = Boolean.parseBoolean(serverSettings.getProperty("LoggingChatNormal", "false"));
            LOGGING_CHAT_WHISPER = Boolean.parseBoolean(serverSettings.getProperty("LoggingChatWhisper", "false"));
            LOGGING_CHAT_SHOUT = Boolean.parseBoolean(serverSettings.getProperty("LoggingChatShout", "false"));
            LOGGING_CHAT_WORLD = Boolean.parseBoolean(serverSettings.getProperty("LoggingChatWorld", "false"));
            LOGGING_CHAT_CLAN = Boolean.parseBoolean(serverSettings.getProperty("LoggingChatClan", "false"));
            LOGGING_CHAT_PARTY = Boolean.parseBoolean(serverSettings.getProperty("LoggingChatParty", "false"));
            LOGGING_CHAT_COMBINED = Boolean.parseBoolean(serverSettings.getProperty("LoggingChatCombined", "false"));
            LOGGING_CHAT_CHAT_PARTY = Boolean.parseBoolean(serverSettings.getProperty("LoggingChatChatParty", "false"));
            PC_RECOGNIZE_RANGE = Integer.parseInt(serverSettings.getProperty("PcRecognizeRange", "20"));
            BROADCAST_KILL_LOG = Boolean.parseBoolean(serverSettings.getProperty("BroadcastKillLog", "true"));
            BROADCAST_KILL_LOG_LEVEL = Integer.parseInt(serverSettings.getProperty("BroadcastKillLogLevel", "1"));

            ACCEL_ALLOW = ((double) (Integer.parseInt(serverSettings.getProperty("AccelAllow", "10")))) / 100.f;

            PRIVATE_SHOP_CHAT = new String(serverSettings.getProperty("PrivateShopChat").getBytes("ISO-8859-1"),
                    "MS932");

            HAJA = Integer.parseInt(serverSettings.getProperty("Haja", "2"));
            CHARACTER_CONFIG_IN_SERVER_SIDE = Boolean
                    .parseBoolean(serverSettings.getProperty("CharacterConfigInServerSide", "true"));
            ALLOW_2PC = Boolean.parseBoolean(serverSettings.getProperty("Allow2PC", "true"));
            CHECK_AUTO = Boolean.parseBoolean(serverSettings.getProperty("CheckAuto", "true"));
            CHECK_AUTO_ENCHANT = Boolean.parseBoolean(serverSettings.getProperty("CheckAutoEnchant", "true"));
            LEVEL_DOWN_RANGE = Integer.parseInt(serverSettings.getProperty("LevelDownRange", "0"));
            SEND_PACKET_BEFORE_TELEPORT = Boolean
                    .parseBoolean(serverSettings.getProperty("SendPacketBeforeTeleport", "true"));
            DETECT_DB_RESOURCE_LEAKS = Boolean
                    .parseBoolean(serverSettings.getProperty("EnableDatabaseResourceLeaksDetection", "false"));
            // TODO LINALL CONNECT
            AUTH_CONNECT = Boolean.parseBoolean(serverSettings.getProperty("AuthConnect", "false"));
            // TODO LINALL CONNECT
            AUTH_KEY = Integer.parseInt(serverSettings.getProperty("AuthKey", "136"));

            AUTH_IP = Integer.parseInt(serverSettings.getProperty("CheckIpCount", "2"));

            SHOW_CLIENT_PACKET = Boolean.parseBoolean(serverSettings.getProperty("ShowClientPacket", "false"));
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            throw new Error("Failed to Load " + SERVER_CONFIG_FILE + " File.");
        }

        /** rates.properties **/
        try {
            Properties rateSettings = new Properties();
            FileReader is = new FileReader(new File(RATES_CONFIG_FILE));
            rateSettings.load(is);
            is.close();

            systime = Integer.parseInt(rateSettings.getProperty("systime", "30"));
            sys1 = rateSettings.getProperty("sys1", "");
            sys2 = rateSettings.getProperty("sys2", "");
            sys3 = rateSettings.getProperty("sys3", "");
            sys4 = rateSettings.getProperty("sys4", "");
            sys5 = rateSettings.getProperty("sys5", "");
            sys6 = rateSettings.getProperty("sys6", "");
            sys7 = rateSettings.getProperty("sys7", "");
            sys8 = rateSettings.getProperty("sys8", "");
            sys9 = rateSettings.getProperty("sys9", "");
            sys10 = rateSettings.getProperty("sys10", "");
            sys11 = rateSettings.getProperty("sys11", "");
            sys12 = rateSettings.getProperty("sys12", "");
            sys13 = rateSettings.getProperty("sys13", "");
            sys14 = rateSettings.getProperty("sys14", "");
            sys15 = rateSettings.getProperty("sys15", "");
            sys16 = rateSettings.getProperty("sys16", "");
            RATE_XP = Double.parseDouble(rateSettings.getProperty("RateXp", "1.0"));
            EX_EXP = Double.parseDouble(rateSettings.getProperty("BloodBonus", "1.0"));
            RATE_LAWFUL = Double.parseDouble(rateSettings.getProperty("RateLawful", "1.0"));
            RATE_KARMA = Double.parseDouble(rateSettings.getProperty("RateKarma", "1.0"));
            RATE_DROP_ADENA = Double.parseDouble(rateSettings.getProperty("RateDropAdena", "1.0"));
            RATE_DROP_ITEMS = Double.parseDouble(rateSettings.getProperty("RateDropItems", "1.0"));
            RATE_DROP_RABBIT = Double.parseDouble(rateSettings.getProperty("RateDropRabbit", "10.0"));
            ENCHANT_CHANCE_WEAPON = Integer.parseInt(rateSettings.getProperty("EnchantChanceWeapon", "68"));
            ENCHANT_CHANCE_ARMOR = Integer.parseInt(rateSettings.getProperty("EnchantChanceArmor", "52"));
            ENCHANT_CHANCE_ACCESSORY = Integer.parseInt(rateSettings.getProperty("EnchantChanceAccessory", "5"));
            ARNOLD_WEAPON_CHANCE = Integer.parseInt(rateSettings.getProperty("ArnoldWeapon", "5"));
            CREST_ENCHANT_CHANCE = Integer.parseInt(rateSettings.getProperty("文章強化確率", "30"));
            FG_ISVAL = Integer.parseInt(rateSettings.getProperty("EnterLevel", "0"));

            WeekLevel1 = Integer.parseInt(rateSettings.getProperty("ジュクウェレベル1", "65"));
            WeekLevel2 = Integer.parseInt(rateSettings.getProperty("ジュクウェレベル2", "85"));

            RATE_WEIGHT_LIMIT = Double.parseDouble(rateSettings.getProperty("RateWeightLimit", "1"));
            RATE_WEIGHT_LIMIT_PET = Double.parseDouble(rateSettings.getProperty("RateWeightLimitforPet", "1"));
            RATE_SHOP_SELLING_PRICE = Double.parseDouble(rateSettings.getProperty("RateShopSellingPrice", "1.0"));
            RATE_SHOP_PURCHASING_PRICE = Double.parseDouble(rateSettings.getProperty("RateShopPurchasingPrice", "1.0"));
            CREATE_CHANCE_DIARY = Integer.parseInt(rateSettings.getProperty("CreateChanceDiary", "33"));
            CREATE_CHANCE_RECOLLECTION = Integer.parseInt(rateSettings.getProperty("CreateChanceRecollection", "90"));
            CREATE_CHANCE_MYSTERIOUS = Integer.parseInt(rateSettings.getProperty("CreateChanceMysterious", "90"));
            CREATE_CHANCE_PROCESSING = Integer.parseInt(rateSettings.getProperty("CreateChanceProcessing", "90"));
            CREATE_CHANCE_PROCESSING_DIAMOND = Integer
                    .parseInt(rateSettings.getProperty("CreateChanceProcessingDiamond", "90"));
            CREATE_CHANCE_DANTES = Integer.parseInt(rateSettings.getProperty("CreateChanceDantes", "90"));
            CREATE_CHANCE_ANCIENT_AMULET = Integer
                    .parseInt(rateSettings.getProperty("CreateChanceAncientAmulet", "90"));
            CREATE_CHANCE_HISTORY_BOOK = Integer.parseInt(rateSettings.getProperty("CreateChanceHistoryBook", "50"));
            FEATHER_NUM = Integer.parseInt(rateSettings.getProperty("FeatherNum", "6"));
            FEATHER_NUM1 = Integer.parseInt(rateSettings.getProperty("FeatherNum1", "2"));
            FEATHER_NUM2 = Integer.parseInt(rateSettings.getProperty("FeatherNum2", "3"));
            FEATHER_NUM3 = Integer.parseInt(rateSettings.getProperty("FeatherNum3", "12"));

            RATE_7_DMG_RATE = Double.parseDouble(rateSettings.getProperty("Rate_7_Dmg_Rate", "1.5"));
            RATE_8_DMG_RATE = Double.parseDouble(rateSettings.getProperty("Rate_8_Dmg_Rate", "1.5"));
            RATE_9_DMG_RATE = Double.parseDouble(rateSettings.getProperty("Rate_9_Dmg_Rate", "2.0"));
            RATE_10_DMG_RATE = Double.parseDouble(rateSettings.getProperty("Rate_10_Dmg_Rate", "2.0"));
            RATE_11_DMG_RATE = Double.parseDouble(rateSettings.getProperty("Rate_11_Dmg_Rate", "2.0"));
            RATE_12_DMG_RATE = Double.parseDouble(rateSettings.getProperty("Rate_12_Dmg_Rate", "2.0"));
            RATE_13_DMG_RATE = Double.parseDouble(rateSettings.getProperty("Rate_13_Dmg_Rate", "2.0"));
            RATE_14_DMG_RATE = Double.parseDouble(rateSettings.getProperty("Rate_14_Dmg_Rate", "2.0"));
            RATE_15_DMG_RATE = Double.parseDouble(rateSettings.getProperty("Rate_15_Dmg_Rate", "2.0"));
            RATE_16_DMG_RATE = Double.parseDouble(rateSettings.getProperty("Rate_16_Dmg_Rate", "2.5"));
            RATE_17_DMG_RATE = Double.parseDouble(rateSettings.getProperty("Rate_17_Dmg_Rate", "2.5"));
            RATE_18_DMG_RATE = Double.parseDouble(rateSettings.getProperty("Rate_18_Dmg_Rate", "2.5"));

            RATE_7_DMG_PER = Integer.parseInt(rateSettings.getProperty("Rate_7_Dmg_Per", "5"));
            RATE_8_DMG_PER = Integer.parseInt(rateSettings.getProperty("Rate_8_Dmg_Per", "10"));
            RATE_9_DMG_PER = Integer.parseInt(rateSettings.getProperty("Rate_9_Dmg_Per", "20"));
            RATE_10_DMG_PER = Integer.parseInt(rateSettings.getProperty("Rate_10_Dmg_Per", "30"));
            RATE_11_DMG_PER = Integer.parseInt(rateSettings.getProperty("Rate_11_Dmg_Per", "40"));
            RATE_12_DMG_PER = Integer.parseInt(rateSettings.getProperty("Rate_12_Dmg_Per", "50"));
            RATE_13_DMG_PER = Integer.parseInt(rateSettings.getProperty("Rate_13_Dmg_Per", "60"));
            RATE_14_DMG_PER = Integer.parseInt(rateSettings.getProperty("Rate_14_Dmg_Per", "70"));
            RATE_15_DMG_PER = Integer.parseInt(rateSettings.getProperty("Rate_15_Dmg_Per", "80"));
            RATE_16_DMG_PER = Integer.parseInt(rateSettings.getProperty("Rate_16_Dmg_Per", "90"));
            RATE_17_DMG_PER = Integer.parseInt(rateSettings.getProperty("Rate_17_Dmg_Per", "90"));
            RATE_18_DMG_PER = Integer.parseInt(rateSettings.getProperty("Rate_18_Dmg_Per", "100"));

            DISCHARGE1 = Integer.parseInt(rateSettings.getProperty("Discharge1", "1"));
            DISCHARGE2 = Integer.parseInt(rateSettings.getProperty("Discharge2", "2"));
            DISCHARGE3 = Integer.parseInt(rateSettings.getProperty("Discharge3", "3"));
            DISCHARGE4 = Integer.parseInt(rateSettings.getProperty("Discharge4", "4"));
            DISCHARGE5 = Integer.parseInt(rateSettings.getProperty("Discharge5", "5"));
            DISCHARGE6 = Integer.parseInt(rateSettings.getProperty("Discharge6", "6"));
            DISCHARGE7 = Integer.parseInt(rateSettings.getProperty("Discharge7", "7"));

            // 地獄
            HELL_TIME = Integer.parseInt(rateSettings.getProperty("HellTime", "6"));
            HELL_LEVEL = Integer.parseInt(rateSettings.getProperty("HellLevel", "70"));

            TAM_COUNT = Integer.parseInt(rateSettings.getProperty("TamNum", "6"));
            TAM_CLAN_COUNT = Integer.parseInt(rateSettings.getProperty("TamNum1", "6"));
            TAM_EX_COUNT = Integer.parseInt(rateSettings.getProperty("TamNum2", "6"));
            TAM_EX2_COUNT = Integer.parseInt(rateSettings.getProperty("TamNum3", "6"));

            SOCKS_OPERATION = Boolean.parseBoolean(rateSettings.getProperty("Eventof", "false"));
            EVENT_A_OPERATION = Boolean.parseBoolean(rateSettings.getProperty("BugRace", "false"));
            EVENT_TIME = Integer.parseInt(rateSettings.getProperty("EventTime", "1"));
            EVENT_NUMBER = Integer.parseInt(rateSettings.getProperty("EventNumber", "1"));
            EVENT_ITEM = Integer.parseInt(rateSettings.getProperty("EventItem", "1"));
            BATTLE_ZONE_ENTRY_LEVEL = Integer.parseInt(rateSettings.getProperty("BattleLevel", "55"));
            BATTLE_ZONE_OPERATION = Boolean.parseBoolean(rateSettings.getProperty("BattleZone", "true"));
            DEMON_KING_OPERATION = Boolean.parseBoolean(rateSettings.getProperty("DevilZone", "true"));
            BOSS_SPAWN_OPERATION = Boolean.parseBoolean(rateSettings.getProperty("BossZone", "true"));
            DEMON_KING_TIME = Integer.parseInt(rateSettings.getProperty("DevilTime", "3"));
            DEMON_KING_ENTRY_LEVEL = Integer.parseInt(rateSettings.getProperty("DevilLevel", "55"));

            TI_DUNGEON_FEATHER = Boolean.parseBoolean(rateSettings.getProperty("TIダンジョン羽", "true"));
            GLUDIO_DUNGEON_FEATHER = Boolean.parseBoolean(rateSettings.getProperty("メインランドのダンジョン羽", "true"));

            SKT_DUNGEON_FEATHER = Boolean.parseBoolean(rateSettings.getProperty("修練ケイブ羽", "true"));
            FI_FEATHER = Boolean.parseBoolean(rateSettings.getProperty("忘れられた島羽", "true"));

            BATTLE_ZONE_ITEM = rateSettings.getProperty("BattleItem", "");
            BATTLE_ZONE_ITEM_COUNT = rateSettings.getProperty("BattleCount", "");

            PIC_BOOK_1_ITEM = rateSettings.getProperty("Dogamone", "");
            PIC_BOOK_1_ITEM_COUNT = rateSettings.getProperty("DogamoneCount", "");

            PIC_BOOK_2_ITEM = rateSettings.getProperty("Dogamto", "");
            PIC_BOOK_2_ITEM_COUNT = rateSettings.getProperty("DogamtoCount", "");

            PIC_BOOK_3_ITEM = rateSettings.getProperty("Dogamthr", "");
            PIC_BOOK_3_ITEM_COUNT = rateSettings.getProperty("DogamthrCount", "");

            EVENTITEM_ITEMID = rateSettings.getProperty("Eventitemid", "");
            EVENTITEM_NUMBER = rateSettings.getProperty("Eventitemcount", "");

            RAID_TIME = Integer.parseInt(rateSettings.getProperty("Raidtime", "1"));

        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            throw new Error("Failed to Load " + RATES_CONFIG_FILE + " File.");
        }

        /** altsettings.properties **/
        try {
            Properties altSettings = new Properties();
            InputStream is = new FileInputStream(new File(ALT_SETTINGS_FILE));
            altSettings.load(is);
            is.close();

            GLOBAL_CHAT_LEVEL = Short.parseShort(altSettings.getProperty("GlobalChatLevel", "30"));
            WHISPER_CHAT_LEVEL = Short.parseShort(altSettings.getProperty("WhisperChatLevel", "7"));
            AUTO_LOOT = Byte.parseByte(altSettings.getProperty("AutoLoot", "2"));
            LOOTING_RANGE = Integer.parseInt(altSettings.getProperty("LootingRange", "3"));
            CLAN_COUNT = Integer.parseInt(altSettings.getProperty("Clancount", "5"));
            ALT_NONPVP = Boolean.parseBoolean(altSettings.getProperty("NonPvP", "true"));
            ALT_ATKMSG = Boolean.parseBoolean(altSettings.getProperty("AttackMessageOn", "true"));
            CHANGE_TITLE_BY_ONESELF = Boolean.parseBoolean(altSettings.getProperty("ChangeTitleByOneself", "false"));
            MAX_CLAN_MEMBER = Integer.parseInt(altSettings.getProperty("MaxClanMember", "0"));
            CLAN_ALLIANCE = Boolean.parseBoolean(altSettings.getProperty("ClanAlliance", "true"));
            MAX_PT = Integer.parseInt(altSettings.getProperty("MaxPT", "8"));
            MAX_CHAT_PT = Integer.parseInt(altSettings.getProperty("MaxChatPT", "8"));
            SIM_WAR_PENALTY = Boolean.parseBoolean(altSettings.getProperty("SimWarPenalty", "true"));
            GET_BACK = Boolean.parseBoolean(altSettings.getProperty("GetBack", "false"));
            ALT_ITEM_DELETION_TYPE = altSettings.getProperty("ItemDeletionType", "auto");
            ALT_ITEM_DELETION_TIME = Integer.parseInt(altSettings.getProperty("ItemDeletionTime", "10"));
            ALT_ITEM_DELETION_RANGE = Integer.parseInt(altSettings.getProperty("ItemDeletionRange", "5"));
            ALT_GMSHOP = Boolean.parseBoolean(altSettings.getProperty("GMshop", "false"));
            ALT_GMSHOP_MIN_ID = Integer.parseInt(altSettings.getProperty("GMshopMinID", "0xffffffff"));
            ALT_GMSHOP_MAX_ID = Integer.parseInt(altSettings.getProperty("GMshopMaxID", "0xffffffff"));
            ALT_BASETOWN = Boolean.parseBoolean(altSettings.getProperty("BaseTown", "false"));
            ALT_BASETOWN_MIN_ID = Integer.parseInt(altSettings.getProperty("BaseTownMinID", "0xffffffff"));
            ALT_BASETOWN_MAX_ID = Integer.parseInt(altSettings.getProperty("BaseTownMaxID", "0xffffffff"));
            ALT_HALLOWEENIVENT = Boolean.parseBoolean(altSettings.getProperty("HalloweenIvent", "true"));
            WHOIS_CONTER = Integer.parseInt(altSettings.getProperty("WhoisConter", "0")); //
            /** [幻想イベント本サーバー化] **/
            ALT_FANTASYEVENT = Boolean.parseBoolean(altSettings.getProperty("FantasyEvent", "true"));
            /** [幻想イベント本サーバー化] **/
            ALT_JPPRIVILEGED = Boolean.parseBoolean(altSettings.getProperty("JpPrivileged", "false"));
            ALT_WHO_COMMAND = Boolean.parseBoolean(altSettings.getProperty("WhoCommand", "false"));
            ALT_REVIVAL_POTION = Boolean.parseBoolean(altSettings.getProperty("RevivalPotion", "false"));
            ALT_BEGINNER_BONUS = Boolean.parseBoolean(altSettings.getProperty("BeginnerEvent", "false"));
            ALT_RABBITEVENT = Boolean.parseBoolean(altSettings.getProperty("RabbitEvent", "false"));
            ALT_FISHEVENT = Boolean.parseBoolean(altSettings.getProperty("FishEvent", "true"));
            ALT_DROPLEVELLIMIT = Integer.parseInt(altSettings.getProperty("DropLevelLimit", "90"));

            DVC_ENTRY_LEVEL = Integer.parseInt(altSettings.getProperty("dvdgate1", "75"));
            DVC_LIMIT_LEVEL = Integer.parseInt(altSettings.getProperty("dvdgate2", "80"));
            HC_ENTRY_LEVEL = Integer.parseInt(altSettings.getProperty("dvdgate3", "75"));
            HC_LIMIT_LEVEL = Integer.parseInt(altSettings.getProperty("dvdgate4", "80"));
            STORE_USAGE_LEVEL = Integer.parseInt(altSettings.getProperty("ShopLevel", "99"));

            TIC_ENTRY_LEVEL = Integer.parseInt(altSettings.getProperty("dvdgate5", "55"));
            TIC_LIMIT_LEVEL = Integer.parseInt(altSettings.getProperty("dvdgate6", "70"));
            SKTC_ENTRY_LEVEL = Integer.parseInt(altSettings.getProperty("dvdgate7", "70"));
            SKTC_LIMIT_LEVEL = Integer.parseInt(altSettings.getProperty("dvdgate8", "70"));

            DISCARDED_LAND_ENTRY_LEVEL = Integer.parseInt(altSettings.getProperty("dvdgate9", "70"));

            ANCIENT_WEAPON = Integer.parseInt(altSettings.getProperty("PopWeapon", "100"));
            ANCIENT_ARMOR = Integer.parseInt(altSettings.getProperty("PopArmor", "100"));

            AUTO_REMOVELEVEL = Integer.parseInt(altSettings.getProperty("AutoRemoveLevel", "75"));
            BUFFLEVEL = Integer.parseInt(altSettings.getProperty("BuffLevel", "75"));

            TSUTSUKUGEN = Integer.parseInt(altSettings.getProperty("BuffLevel1", "79"));

            NORMAL_PROTECTION = Integer.parseInt(altSettings.getProperty("TopGrace", "60"));

            LASTAVARD_ENTRY_LEVEL = Integer.parseInt(altSettings.getProperty("LarvaLevel", "70"));

            WIZARD_MAGIC_DAMAGE = Double.parseDouble(altSettings.getProperty("Wizdmg", "1.0"));

            WIZARD_MONSTER_DAMAGE = Double.parseDouble(altSettings.getProperty("Wizdmg1", "1.0"));

            NEW_CLAN_PROTECTION_LEVEL = Integer.parseInt(altSettings.getProperty("NewClanLevel", "60"));
            NEW_CLAN = Integer.parseInt(altSettings.getProperty("NewClanid", "1"));
            NEW_CLAN_PROTECTION_PROCESS = Boolean.parseBoolean(altSettings.getProperty("NewClanPvP", "true"));

            WEAPON_ENCHANT = Integer.parseInt(altSettings.getProperty("LimitWeapon", "13")); // 一般武器
            WEAPON_PREMIUM_ENCHANT = Integer.parseInt(altSettings.getProperty("LimitWeapon2", "5")); // 特殊武器
            ARMOR_ENCHANT = Integer.parseInt(altSettings.getProperty("LimitArmor", "11")); // 一般鎧
            ARMOR_PREMIUM_ENCHANT = Integer.parseInt(altSettings.getProperty("LimitArmor2", "7")); // 特殊アーマー
            ROOMTIS = Integer.parseInt(altSettings.getProperty("RoomT", "8"));
            SNAPPER = Integer.parseInt(altSettings.getProperty("Snapper", "8"));
            ACCESSORIES = Integer.parseInt(altSettings.getProperty("Accessory", "9"));
            CRAFTSMAN_WEAPON_SCROLL = Integer.parseInt(altSettings.getProperty("職人武器強化スクロール", "10"));
            BLESS_SCROLL = Integer.parseInt(altSettings.getProperty("祝福書", "15"));
            ORIM_SCROLL = Integer.parseInt(altSettings.getProperty("クリップボード書", "70"));

            String strWar;
            strWar = altSettings.getProperty("WarTime", "1h");
            if (strWar.indexOf("d") >= 0) {
                ALT_WAR_TIME_UNIT = Calendar.DATE;
                strWar = strWar.replace("d", "");
            } else if (strWar.indexOf("h") >= 0) {
                ALT_WAR_TIME_UNIT = Calendar.HOUR_OF_DAY;
                strWar = strWar.replace("h", "");
            } else if (strWar.indexOf("m") >= 0) {
                ALT_WAR_TIME_UNIT = Calendar.MINUTE;
                strWar = strWar.replace("m", "");
            }
            ALT_WAR_TIME = Integer.parseInt(strWar);
            strWar = altSettings.getProperty("WarInterval", "2d");
            if (strWar.indexOf("d") >= 0) {
                ALT_WAR_INTERVAL_UNIT = Calendar.DATE;
                strWar = strWar.replace("d", "");
            } else if (strWar.indexOf("h") >= 0) {
                ALT_WAR_INTERVAL_UNIT = Calendar.HOUR_OF_DAY;
                strWar = strWar.replace("h", "");
            } else if (strWar.indexOf("m") >= 0) {
                ALT_WAR_INTERVAL_UNIT = Calendar.MINUTE;
                strWar = strWar.replace("m", "");
            }
            ALT_WAR_INTERVAL = Integer.parseInt(strWar);
            SPAWN_HOME_POINT = Boolean.parseBoolean(altSettings.getProperty("SpawnHomePoint", "true"));
            SPAWN_HOME_POINT_COUNT = Integer.parseInt(altSettings.getProperty("SpawnHomePointCount", "2"));
            SPAWN_HOME_POINT_DELAY = Integer.parseInt(altSettings.getProperty("SpawnHomePointDelay", "100"));
            SPAWN_HOME_POINT_RANGE = Integer.parseInt(altSettings.getProperty("SpawnHomePointRange", "8"));
            INIT_BOSS_SPAWN = Boolean.parseBoolean(altSettings.getProperty("InitBossSpawn", "true"));
            ELEMENTAL_STONE_AMOUNT = Integer.parseInt(altSettings.getProperty("ElementalStoneAmount", "300"));
            HOUSE_TAX_INTERVAL = Integer.parseInt(altSettings.getProperty("HouseTaxInterval", "10"));
            MAX_DOLL_COUNT = Integer.parseInt(altSettings.getProperty("MaxDollCount", "1"));
            RETURN_TO_NATURE = Boolean.parseBoolean(altSettings.getProperty("ReturnToNature", "false"));
            MAX_NPC_ITEM = Integer.parseInt(altSettings.getProperty("MaxNpcItem", "8"));
            MAX_PERSONAL_WAREHOUSE_ITEM = Integer.parseInt(altSettings.getProperty("MaxPersonalWarehouseItem", "100"));
            MAX_CLAN_WAREHOUSE_ITEM = Integer.parseInt(altSettings.getProperty("MaxClanWarehouseItem", "200"));
            DELETE_CHARACTER_AFTER_7DAYS = Boolean
                    .parseBoolean(altSettings.getProperty("DeleteCharacterAfter7Days", "True"));
            GMCODE = Integer.parseInt(altSettings.getProperty("GMCODE", "9999"));

            EXP_PAYMENT_TEAM = Integer.parseInt(altSettings.getProperty("Expreturn", "75"));

            DECLARATION_LEVEL = Integer.parseInt(altSettings.getProperty("WarLevel", "60"));

            CLAN_CONNECT_COUNT = Integer.parseInt(altSettings.getProperty("WarPlayer", "60"));

            NEW_PLAYER = Integer.parseInt(altSettings.getProperty("NewPlayer", "0"));

            NOTIS_TIME = Integer.parseInt(altSettings.getProperty("NotisTime", "10"));

            SPAWN_ROBOT = Boolean.parseBoolean(altSettings.getProperty("SpawnRobot", "false"));
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            throw new Error("Failed to Load " + ALT_SETTINGS_FILE + " File.");
        }

        /** charsettings.properties **/
        try {
            Properties charSettings = new Properties();
            InputStream is = new FileInputStream(new File(CHAR_SETTINGS_CONFIG_FILE));
            charSettings.load(is);
            is.close();

            PRINCE_MAX_HP = Integer.parseInt(charSettings.getProperty("PrinceMaxHP", "1000"));
            PRINCE_MAX_MP = Integer.parseInt(charSettings.getProperty("PrinceMaxMP", "800"));
            KNIGHT_MAX_HP = Integer.parseInt(charSettings.getProperty("KnightMaxHP", "1400"));
            KNIGHT_MAX_MP = Integer.parseInt(charSettings.getProperty("KnightMaxMP", "600"));
            ELF_MAX_HP = Integer.parseInt(charSettings.getProperty("ElfMaxHP", "1000"));
            ELF_MAX_MP = Integer.parseInt(charSettings.getProperty("ElfMaxMP", "900"));
            WIZARD_MAX_HP = Integer.parseInt(charSettings.getProperty("WizardMaxHP", "800"));
            WIZARD_MAX_MP = Integer.parseInt(charSettings.getProperty("WizardMaxMP", "1200"));
            DARKELF_MAX_HP = Integer.parseInt(charSettings.getProperty("DarkelfMaxHP", "1000"));
            DARKELF_MAX_MP = Integer.parseInt(charSettings.getProperty("DarkelfMaxMP", "900"));
            DRAGONKNIGHT_MAX_HP = Integer.parseInt(charSettings.getProperty("DragonknightMaxHP", "1000"));
            DRAGONKNIGHT_MAX_MP = Integer.parseInt(charSettings.getProperty("DragonknightMaxMP", "900"));
            BLACKWIZARD_MAX_HP = Integer.parseInt(charSettings.getProperty("BlackwizardMaxHP", "900"));
            BLACKWIZARD_MAX_MP = Integer.parseInt(charSettings.getProperty("BlackwizardMaxMP", "1100"));
            WARRIOR_MAX_HP = Integer.parseInt(charSettings.getProperty("WarriorMaxHP", "1400"));
            WARRIOR_MAX_MP = Integer.parseInt(charSettings.getProperty("WarriorMaxMP", "600"));
            LIMITLEVEL = Integer.parseInt(charSettings.getProperty("LimitLevel", "99"));

            PRINCE_ADD_DAMAGEPC = Integer.parseInt(charSettings.getProperty("PrinceAddDamagePc", "0"));
            KNIGHT_ADD_DAMAGEPC = Integer.parseInt(charSettings.getProperty("KnightAddDamagePc", "0"));
            ELF_ADD_DAMAGEPC = Integer.parseInt(charSettings.getProperty("ElfAddDamagePc", "0"));
            WIZARD_ADD_DAMAGEPC = Integer.parseInt(charSettings.getProperty("WizardAddDamagePc", "0"));
            DARKELF_ADD_DAMAGEPC = Integer.parseInt(charSettings.getProperty("DarkelfAddDamagePc", "0"));
            DRAGONKNIGHT_ADD_DAMAGEPC = Integer.parseInt(charSettings.getProperty("DragonknightAddDamagePc", "0"));
            BLACKWIZARD_ADD_DAMAGEPC = Integer.parseInt(charSettings.getProperty("BlackwizardAddDamagePc", "0"));
            WARRIOR_ADD_DAMAGEPC = Integer.parseInt(charSettings.getProperty("WarriorAddDamagePc", "0"));

            LV50_EXP = Integer.parseInt(charSettings.getProperty("Lv50Exp", "1"));
            LV51_EXP = Integer.parseInt(charSettings.getProperty("Lv51Exp", "1"));
            LV52_EXP = Integer.parseInt(charSettings.getProperty("Lv52Exp", "1"));
            LV53_EXP = Integer.parseInt(charSettings.getProperty("Lv53Exp", "1"));
            LV54_EXP = Integer.parseInt(charSettings.getProperty("Lv54Exp", "1"));
            LV55_EXP = Integer.parseInt(charSettings.getProperty("Lv55Exp", "1"));
            LV56_EXP = Integer.parseInt(charSettings.getProperty("Lv56Exp", "1"));
            LV57_EXP = Integer.parseInt(charSettings.getProperty("Lv57Exp", "1"));
            LV58_EXP = Integer.parseInt(charSettings.getProperty("Lv58Exp", "1"));
            LV59_EXP = Integer.parseInt(charSettings.getProperty("Lv59Exp", "1"));
            LV60_EXP = Integer.parseInt(charSettings.getProperty("Lv60Exp", "1"));
            LV61_EXP = Integer.parseInt(charSettings.getProperty("Lv61Exp", "1"));
            LV62_EXP = Integer.parseInt(charSettings.getProperty("Lv62Exp", "1"));
            LV63_EXP = Integer.parseInt(charSettings.getProperty("Lv63Exp", "1"));
            LV64_EXP = Integer.parseInt(charSettings.getProperty("Lv64Exp", "1"));
            LV65_EXP = Integer.parseInt(charSettings.getProperty("Lv65Exp", "2"));
            LV66_EXP = Integer.parseInt(charSettings.getProperty("Lv66Exp", "2"));
            LV67_EXP = Integer.parseInt(charSettings.getProperty("Lv67Exp", "2"));
            LV68_EXP = Integer.parseInt(charSettings.getProperty("Lv68Exp", "2"));
            LV69_EXP = Integer.parseInt(charSettings.getProperty("Lv69Exp", "2"));
            LV70_EXP = Integer.parseInt(charSettings.getProperty("Lv70Exp", "4"));
            LV71_EXP = Integer.parseInt(charSettings.getProperty("Lv71Exp", "4"));
            LV72_EXP = Integer.parseInt(charSettings.getProperty("Lv72Exp", "4"));
            LV73_EXP = Integer.parseInt(charSettings.getProperty("Lv73Exp", "4"));
            LV74_EXP = Integer.parseInt(charSettings.getProperty("Lv74Exp", "4"));
            LV75_EXP = Integer.parseInt(charSettings.getProperty("Lv75Exp", "8"));
            LV76_EXP = Integer.parseInt(charSettings.getProperty("Lv76Exp", "8"));
            LV77_EXP = Integer.parseInt(charSettings.getProperty("Lv77Exp", "8"));
            LV78_EXP = Integer.parseInt(charSettings.getProperty("Lv78Exp", "8"));
            LV79_EXP = Integer.parseInt(charSettings.getProperty("Lv79Exp", "16"));
            LV80_EXP = Integer.parseInt(charSettings.getProperty("Lv80Exp", "32"));
            LV81_EXP = Integer.parseInt(charSettings.getProperty("Lv81Exp", "64"));
            LV82_EXP = Integer.parseInt(charSettings.getProperty("Lv82Exp", "128"));
            LV83_EXP = Integer.parseInt(charSettings.getProperty("Lv83Exp", "256"));
            LV84_EXP = Integer.parseInt(charSettings.getProperty("Lv84Exp", "512"));
            LV85_EXP = Integer.parseInt(charSettings.getProperty("Lv85Exp", "1024"));
            LV86_EXP = Integer.parseInt(charSettings.getProperty("Lv86Exp", "2048"));
            LV87_EXP = Integer.parseInt(charSettings.getProperty("Lv87Exp", "4096"));
            LV88_EXP = Integer.parseInt(charSettings.getProperty("Lv88Exp", "8192"));
            LV89_EXP = Integer.parseInt(charSettings.getProperty("Lv89Exp", "16384"));
            LV90_EXP = Integer.parseInt(charSettings.getProperty("Lv90Exp", "32768"));
            LV91_EXP = Integer.parseInt(charSettings.getProperty("Lv91Exp", "65536"));
            LV92_EXP = Integer.parseInt(charSettings.getProperty("Lv92Exp", "131072"));
            LV93_EXP = Integer.parseInt(charSettings.getProperty("Lv93Exp", "262144"));
            LV94_EXP = Integer.parseInt(charSettings.getProperty("Lv94Exp", "524288"));
            LV95_EXP = Integer.parseInt(charSettings.getProperty("Lv95Exp", "1048576"));
            LV96_EXP = Integer.parseInt(charSettings.getProperty("Lv96Exp", "2097152"));
            LV97_EXP = Integer.parseInt(charSettings.getProperty("Lv97Exp", "4194304"));
            LV98_EXP = Integer.parseInt(charSettings.getProperty("Lv98Exp", "8388608"));
            LV99_EXP = Integer.parseInt(charSettings.getProperty("Lv99Exp", "16777216"));
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            throw new Error("Failed to Load " + CHAR_SETTINGS_CONFIG_FILE + " File.");
        }

        try {
            Properties Eventlink = new Properties();
            // InputStream is = new FileInputStream(new
            // File(CHOLONG_SETTINGS_CONFIG_FILE));
            FileReader is = new FileReader(new File(CHOLONG_SETTINGS_CONFIG_FILE));
            Eventlink.load(is);
            is.close();

            /** イベントライン設定ファイル **/
            CLAN_EXP_ONE = Integer.parseInt(Eventlink.getProperty("ClanExpOne", "200"));
            CLAN_EXP_TWO = Integer.parseInt(Eventlink.getProperty("ClanExpTwo", "400"));
            CLAN_EXP_THREE = Integer.parseInt(Eventlink.getProperty("ClanExpThree", "600"));
            CLAN_EXP_FOUR = Integer.parseInt(Eventlink.getProperty("ClanExpFour", "900"));
            CLAN_EXP_FIVE = Integer.parseInt(Eventlink.getProperty("ClanExpFive", "1200"));
            CLAN_EXP_SIX = Integer.parseInt(Eventlink.getProperty("ClanExpSix", "1500"));
            CLAN_EXP_SEVEN = Integer.parseInt(Eventlink.getProperty("ClanExpSeven", "2000"));
            ADEN_SHOP_NPC = Integer.parseInt(Eventlink.getProperty("AdenShopNpc", "5"));
            NINE_CLASS = Integer.parseInt(Eventlink.getProperty("NineClass", "100"));
            EIGHT_CLASS = Integer.parseInt(Eventlink.getProperty("EightClass", "200"));
            SEVEN_CLASS = Integer.parseInt(Eventlink.getProperty("SevenClass", "300"));
            SIX_CLASS = Integer.parseInt(Eventlink.getProperty("SixClass", "400"));
            FIVE_CLASS = Integer.parseInt(Eventlink.getProperty("FiveClass", "500"));
            FOUR_CLASS = Integer.parseInt(Eventlink.getProperty("FourClass", "600"));
            THREE_CLASS = Integer.parseInt(Eventlink.getProperty("ThreeClass", "700"));
            TWO_CLASS = Integer.parseInt(Eventlink.getProperty("TwoClass", "800"));
            ONE_CLASS = Integer.parseInt(Eventlink.getProperty("OneClass", "900"));
            STAR_ONE = Integer.parseInt(Eventlink.getProperty("StarOne", "1000"));
            STAR_TWO = Integer.parseInt(Eventlink.getProperty("StarTwo", "1100"));
            STAR_THREE = Integer.parseInt(Eventlink.getProperty("StarThree", "1200"));
            STAR_FOUR = Integer.parseInt(Eventlink.getProperty("StarFour", "1300"));
            STAR_FIVE = Integer.parseInt(Eventlink.getProperty("StarFive", "1400"));
            SUPREMECOMMANDER = Integer.parseInt(Eventlink.getProperty("SupremeCommander", "1400"));
            ; // 総司令官
            COMMANDER = Integer.parseInt(Eventlink.getProperty("Commander", "1400")); // 司令官
            IMPERATOR = Integer.parseInt(Eventlink.getProperty("Imperator", "1400")); // 大将軍
            GENERAL = Integer.parseInt(Eventlink.getProperty("General", "1400")); // 将軍
            STAR_FIVE_DAMAGE = Integer.parseInt(Eventlink.getProperty("StarFiveDamege", "40"));
            SUPREMECOMMANDER_DAMAGE = Integer.parseInt(Eventlink.getProperty("SupremeCommanderDamege", "200"));
            ; // 総司令官
            COMMANDER_DAMAGE = Integer.parseInt(Eventlink.getProperty("CommanderDamege", "160")); // 司令官
            IMPERATOR_DAMAGE = Integer.parseInt(Eventlink.getProperty("ImperatorDamege", "120")); // 大将軍
            GENERAL_DAMAGE = Integer.parseInt(Eventlink.getProperty("GeneralDamege", "80")); // 将軍
            CLASS_START_LEVEL = Integer.parseInt(Eventlink.getProperty("ClassStartLevel", "99"));
            ABYSS_POINT = Integer.parseInt(Eventlink.getProperty("AbyssPoint", "10"));
            ROOMTIECE_CHANCE = Integer.parseInt(Eventlink.getProperty("RoomtieceChance", "9"));
            Tam_Time = Integer.parseInt(Eventlink.getProperty("TamTime", "15"));
            FEATHER_SHOP_NUM = Integer.parseInt(Eventlink.getProperty("FeatherShopNum", "100000"));
            ;
            FEATHER_TIME = Integer.parseInt(Eventlink.getProperty("FeatherTime", "15"));
            FISH_EXP = Integer.parseInt(Eventlink.getProperty("FishExp", "5000"));
            FISH_TIME = Integer.parseInt(Eventlink.getProperty("FishTime", "80"));
            FISH_COM = Boolean.parseBoolean(Eventlink.getProperty("FishCom", "false"));

            ADEN_HUNTING_OPERATION = Boolean.parseBoolean(Eventlink.getProperty("AdenZone", "true"));
            ADEN_HUNTING_TIME = Integer.parseInt(Eventlink.getProperty("AdenTime", "3"));
            ADEN_HUNTING_ENTRY_LEVEL = Integer.parseInt(Eventlink.getProperty("AdenLevel", "55"));
            ENCHANT_BUFF_TIME_VITALITY = Integer.parseInt(Eventlink.getProperty("強化バフ活力時間", "3"));
            ENCHANT_BUFF_TIME_ATTACK = Integer.parseInt(Eventlink.getProperty("強化バフ攻撃時間", "3"));
            ENCHANT_BUFF_TIME_DEFENCE = Integer.parseInt(Eventlink.getProperty("強化バフ防御時間", "3"));
            ENCHANT_BUFF_TIME_MAGIC = Integer.parseInt(Eventlink.getProperty("強化バフ魔法の時間", "3"));
            ENCHANT_BUFF_TIME_STUN = Integer.parseInt(Eventlink.getProperty("強化バフスタン時間", "3"));
            ENCHANT_BUFF_TIME_HOLD = Integer.parseInt(Eventlink.getProperty("強化バフホールド時間", "3"));
            ENCHANT_BUFF_TIME_STR = Integer.parseInt(Eventlink.getProperty("強化バフ力時間", "3"));
            ENCHANT_BUFF_TIME_DEX = Integer.parseInt(Eventlink.getProperty("強化バフデックス時間", "3"));
            ENCHANT_BUFF_TIME_INT = Integer.parseInt(Eventlink.getProperty("強化バフポイント時間", "3"));

            STAGE_1 = Integer.parseInt(Eventlink.getProperty("WantedONE", "20000000"));
            STAGE_2 = Integer.parseInt(Eventlink.getProperty("WantedToo", "40000000"));
            STAGE_3 = Integer.parseInt(Eventlink.getProperty("WantedThree", "60000000"));

            /** NPC物理ダメージ/魔法ダメージ修正 **/
            npcdmg = Integer.parseInt(Eventlink.getProperty("npcdmg", "14"));
            npcmagicdmg = Integer.parseInt(Eventlink.getProperty("npcmagicdmg", "10"));
            /** イベント用 **/
            mapid = Integer.parseInt(Eventlink.getProperty("mapid", "1"));
            mapid1 = Integer.parseInt(Eventlink.getProperty("mapid1", "1"));
            mapid2 = Integer.parseInt(Eventlink.getProperty("mapid2", "1"));
            mapid3 = Integer.parseInt(Eventlink.getProperty("mapid3", "1"));
            mapid4 = Integer.parseInt(Eventlink.getProperty("mapid4", "1"));
            mapid5 = Integer.parseInt(Eventlink.getProperty("mapid5", "1"));
            EXP = Integer.parseInt(Eventlink.getProperty("経験値", "1"));
            EVENT_NUMBERS = Integer.parseInt(Eventlink.getProperty("イベント本数", "1"));
            EVENT_ITEMS = Integer.parseInt(Eventlink.getProperty("イベントアイテム", "1"));
            GLUDIN_DUNGEON_OPEN_CYCLE = Integer.parseInt(Eventlink.getProperty("OldLastavard", "4"));
            CRAFT_TABLE_ONE = Integer.parseInt(Eventlink.getProperty("one", "0")); // 将軍;
            CRAFT_TABLE_TWO = Integer.parseInt(Eventlink.getProperty("two", "0")); // 将軍;;
            CRAFT_TABLE_THREE = Integer.parseInt(Eventlink.getProperty("three", "0")); // 将軍;;
            CRAFT_TABLE_FOUR = Integer.parseInt(Eventlink.getProperty("four", "0")); // 将軍;;
            CRAFT_TABLE_FIVE = Integer.parseInt(Eventlink.getProperty("five", "0")); // 将軍;;
            CRAFT_TABLE_SIX = Integer.parseInt(Eventlink.getProperty("six", "0")); // 将軍;;
            CRAFT_TABLE_SEVEN = Integer.parseInt(Eventlink.getProperty("seven", "0")); // 将軍;;
            CRAFT_TABLE_EIGHT = Integer.parseInt(Eventlink.getProperty("eight", "0")); // 将軍;;
            CRAFT_TABLE_NINE = Integer.parseInt(Eventlink.getProperty("nine", "0")); // 将軍;
            CRAFT_TABLE_TEN = Integer.parseInt(Eventlink.getProperty("ten", "0")); // 将軍;;
            CRAFT_TABLE = Integer.parseInt(Eventlink.getProperty("zero", "1")); // 将軍;;
            ARNOLD_EVENT_TIME = Integer.parseInt(Eventlink.getProperty("AnoldeEventTime", "1")); // 将軍;;
            ARNOLD_EVENTS = Boolean.parseBoolean(Eventlink.getProperty("アーノルドイベント", "false"));
            SUPPLY_SCARECROW_TAM = Boolean.parseBoolean(Eventlink.getProperty("かかし乗車支給するかどうか", "false"));

            PURE_WHITE_T = Boolean.parseBoolean(Eventlink.getProperty("TWhite", "false"));
            SHOCK_STUN = Integer.parseInt(Eventlink.getProperty("ShockStun", "60"));

            ARMOR_BREAK = Integer.parseInt(Eventlink.getProperty("ARMOR_BRAKE", "38"));

            COUNTER_BARRIER = Integer.parseInt(Eventlink.getProperty("COUNTER_BARRIER", "20"));

            DESPERADO = Integer.parseInt(Eventlink.getProperty("DESPERADO", "40"));

            POWER_GRIP = Integer.parseInt(Eventlink.getProperty("POWERRIP", "40"));

            ERASE_MAGIC = Integer.parseInt(Eventlink.getProperty("ERASE_MAGIC", "40"));

            EARTH_BIND = Integer.parseInt(Eventlink.getProperty("EARTH_BIND", "40"));

            WIND_SHACKLE = Integer.parseInt(Eventlink.getProperty("WIND_SHACKLE", "30"));

            BONE_BREAK = Integer.parseInt(Eventlink.getProperty("BONE_BREAK", "40"));

            DEATH_HEAL = Integer.parseInt(Eventlink.getProperty("DEATH_HEAL", "30"));
            /** 新たに追加 **/
            useritem = Integer.parseInt(Eventlink.getProperty("useritem", "1"));
            usercount = Integer.parseInt(Eventlink.getProperty("usercount", "1"));
            KIRINGKU = Integer.parseInt(Eventlink.getProperty("KingD", "12"));// キーリンク
            DEATH_KNIGHT_HELLFIRE = Integer.parseInt(Eventlink.getProperty("dethshellpa", "1"));
            DOLL_CHANCE = Integer.parseInt(Eventlink.getProperty("dollchance", "1"));
            LIGHT_OF_ETERNAL_LIFE = Integer.parseInt(Eventlink.getProperty("yungsang", "1"));
            LEAVES_OF_LIFE = Integer.parseInt(Eventlink.getProperty("Leafitem", "100"));
            KNIGHT = Double.parseDouble(Eventlink.getProperty("KK", "1.5"));
            DRAGON_KNIGHT = Double.parseDouble(Eventlink.getProperty("DK", "1.5"));
            ELF = Double.parseDouble(Eventlink.getProperty("EF", "1.5"));
            CROWN = Double.parseDouble(Eventlink.getProperty("KC", "1.5"));
            WIZARD = Double.parseDouble(Eventlink.getProperty("MM", "1.5"));
            DARK_ELF = Double.parseDouble(Eventlink.getProperty("DE", "1.5"));
            ILLUSIONIST = Double.parseDouble(Eventlink.getProperty("MB", "1.5"));
            WARRIOR = Double.parseDouble(Eventlink.getProperty("WR", "1.5"));
            RECOVERY_EXP = Double.parseDouble(Eventlink.getProperty("経験値復旧", "0.049"));
            EXP_POT_LIMIT = Boolean.parseBoolean(Eventlink.getProperty("ExpMax", "true"));
            ALL_GIFT_OPERATION = Boolean.parseBoolean(Eventlink.getProperty("GiftItem", "true"));

            Tower = Integer.parseInt(Eventlink.getProperty("タワー1段階補償", "40308"));
            MTower = Integer.parseInt(Eventlink.getProperty("タワー2段階補償", "40308"));
            LTower = Integer.parseInt(Eventlink.getProperty("タワー3段階の補償", "40308"));
            TowerC = Integer.parseInt(Eventlink.getProperty("タワー1段階補償本数", "1"));
            MTowerC = Integer.parseInt(Eventlink.getProperty("タワー2段階補償本数", "1"));
            LTowerC = Integer.parseInt(Eventlink.getProperty("タワー3段階補償本数", "1"));
        } catch (Exception e) {
            _log.log(Level.SEVERE, "Config.でエラーが発生しました。", e);
            throw new Error("Failed to Load " + CHOLONG_SETTINGS_CONFIG_FILE + " File.");
        }
        validate();
    }

    private static void validate() {
        if (!IntRange.includes(Config.ALT_ITEM_DELETION_RANGE, 0, 5)) {
            throw new IllegalStateException("ItemDeletionRangeの値が設定可能範囲外です。 ");
        }
        if (!IntRange.includes(Config.ALT_ITEM_DELETION_TIME, 1, 35791)) {
            throw new IllegalStateException("ItemDeletionTimeの値が設定可能範囲外です。 ");
        }
    }

    public static boolean setParameterValue(String pName, String pValue) {
        /** server.properties **/
        if (pName.equalsIgnoreCase("GameserverName")) {
            GAME_SERVER_NAME = pValue;
        } else if (pName.equalsIgnoreCase("GameserverPort")) {
            GAME_SERVER_PORT = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Driver")) {
            DB_DRIVER = pValue;
        } else if (pName.equalsIgnoreCase("URL")) {
            DB_URL = pValue;
        } else if (pName.equalsIgnoreCase("Login")) {
            DB_LOGIN = pValue;
        } else if (pName.equalsIgnoreCase("Password")) {
            DB_PASSWORD = pValue;
        } else if (pName.equalsIgnoreCase("ClientLanguage")) {
            CLIENT_LANGUAGE = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("TimeZone")) {
            TIME_ZONE = pValue;
        } else if (pName.equalsIgnoreCase("AutomaticKick")) {
            AUTOMATIC_KICK = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("AutoCreateAccounts")) {
            AUTO_CREATE_ACCOUNTS = Boolean.parseBoolean(pValue);
        } else if (pName.equalsIgnoreCase("MaximumOnlineUsers")) {
            MAX_ONLINE_USERS = Short.parseShort(pValue);
        } else if (pName.equalsIgnoreCase("LoggingWeaponEnchant")) {
            LOGGING_WEAPON_ENCHANT = Byte.parseByte(pValue);
        } else if (pName.equalsIgnoreCase("LoggingArmorEnchant")) {
            LOGGING_ARMOR_ENCHANT = Byte.parseByte(pValue);
        } else if (pName.equalsIgnoreCase("CharacterConfigInServerSide")) {
            CHARACTER_CONFIG_IN_SERVER_SIDE = Boolean.parseBoolean(pValue);
        } else if (pName.equalsIgnoreCase("Allow2PC")) {
            ALLOW_2PC = Boolean.parseBoolean(pValue);
        } else if (pName.equalsIgnoreCase("LevelDownRange")) {
            LEVEL_DOWN_RANGE = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("SendPacketBeforeTeleport")) {
            SEND_PACKET_BEFORE_TELEPORT = Boolean.parseBoolean(pValue);
        } else if (pName.equalsIgnoreCase("ShowClientPacket")) {
            SHOW_CLIENT_PACKET = Boolean.parseBoolean(pValue);
        } else if (pName.equalsIgnoreCase("RateXp")) {
            RATE_XP = Double.parseDouble(pValue);
        } else if (pName.equalsIgnoreCase("BloodBonus")) {
            EX_EXP = Double.parseDouble(pValue);
        } else if (pName.equalsIgnoreCase("RateLawful")) {
            RATE_LAWFUL = Double.parseDouble(pValue);
        } else if (pName.equalsIgnoreCase("RateKarma")) {
            RATE_KARMA = Double.parseDouble(pValue);
        } else if (pName.equalsIgnoreCase("RateDropAdena")) {
            RATE_DROP_ADENA = Double.parseDouble(pValue);
        } else if (pName.equalsIgnoreCase("RateDropItems")) {
            RATE_DROP_ITEMS = Double.parseDouble(pValue);
        } else if (pName.equalsIgnoreCase("RateDropRabbit")) {
            RATE_DROP_RABBIT = Double.parseDouble(pValue);
        } else if (pName.equalsIgnoreCase("EnchantChanceWeapon")) {
            ENCHANT_CHANCE_WEAPON = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("EnchantChanceArmor")) {
            ENCHANT_CHANCE_ARMOR = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("EnchantChanceAccessory")) {
            ENCHANT_CHANCE_ACCESSORY = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("文章強化確率")) {
            CREST_ENCHANT_CHANCE = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("ArnoldWeapon")) {
            ARNOLD_WEAPON_CHANCE = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("FreeLevel")) {
            FG_ISVAL = Integer.parseInt("pValue");

        } else if (pName.equalsIgnoreCase("Weightrate")) {
            RATE_WEIGHT_LIMIT = Byte.parseByte(pValue);
        } else if (pName.equalsIgnoreCase("FeatherNum")) {
            FEATHER_NUM = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("FeatherNum1")) {
            FEATHER_NUM1 = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("FeatherNum2")) {
            FEATHER_NUM2 = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("FeatherNum3")) {
            FEATHER_NUM3 = Integer.parseInt(pValue);

        } else if (pName.equalsIgnoreCase("HellTime")) { // 高ラス時間
            HELL_TIME = Integer.parseInt(pValue);

        } else if (pName.equalsIgnoreCase("HellLevel")) { // 高ラス入場レベル
            HELL_LEVEL = Integer.parseInt(pValue);

        } else if (pName.equalsIgnoreCase("TamNum")) {
            TAM_COUNT = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("TamNum1")) {
            TAM_CLAN_COUNT = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("TamNum2")) {
            TAM_EX_COUNT = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("TamNum3")) {
            TAM_EX2_COUNT = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Eventof")) {
            SOCKS_OPERATION = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("BugRace")) {
            EVENT_A_OPERATION = Boolean.valueOf(pValue);

        } else if (pName.equalsIgnoreCase("EventTime")) {
            EVENT_TIME = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("EventNumber")) {
            EVENT_NUMBER = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("EventItem")) {
            EVENT_ITEM = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("BattleZone")) {
            BATTLE_ZONE_OPERATION = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("BattleLevel")) {
            BATTLE_ZONE_ENTRY_LEVEL = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("DevilZone")) {
            DEMON_KING_OPERATION = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("BossZone")) {
            BOSS_SPAWN_OPERATION = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("DevilTime")) {
            DEMON_KING_TIME = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("DevilLevel")) {
            DEMON_KING_ENTRY_LEVEL = Integer.parseInt(pValue);

        }
        /** altsettings.properties **/
        else if (pName.equalsIgnoreCase("GlobalChatLevel")) {
            GLOBAL_CHAT_LEVEL = Short.parseShort(pValue);
        } else if (pName.equalsIgnoreCase("WhisperChatLevel")) {
            WHISPER_CHAT_LEVEL = Short.parseShort(pValue);
        } else if (pName.equalsIgnoreCase("AutoLoot")) {
            AUTO_LOOT = Byte.parseByte(pValue);
        } else if (pName.equalsIgnoreCase("LOOTING_RANGE")) {
            LOOTING_RANGE = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("CLAN_COUNT")) {
            CLAN_COUNT = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("AltNonPvP")) {
            ALT_NONPVP = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("AttackMessageOn")) {
            ALT_ATKMSG = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("ChangeTitleByOneself")) {
            CHANGE_TITLE_BY_ONESELF = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("MaxClanMember")) {
            MAX_CLAN_MEMBER = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("ClanAlliance")) {
            CLAN_ALLIANCE = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("MaxPT")) {
            MAX_PT = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("MaxChatPT")) {
            MAX_CHAT_PT = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("SimWarPenalty")) {
            SIM_WAR_PENALTY = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("GetBack")) {
            GET_BACK = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("AutomaticItemDeletionTime")) {
            ALT_ITEM_DELETION_TIME = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("AutomaticItemDeletionRange")) {
            ALT_ITEM_DELETION_RANGE = Byte.parseByte(pValue);
        } else if (pName.equalsIgnoreCase("GMshop")) {
            ALT_GMSHOP = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("GMshopMinID")) {
            ALT_GMSHOP_MIN_ID = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("GMshopMaxID")) {
            ALT_GMSHOP_MAX_ID = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("BaseTown")) {
            ALT_BASETOWN = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("BaseTownMinID")) {
            ALT_BASETOWN_MIN_ID = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("BaseTownMaxID")) {
            ALT_BASETOWN_MAX_ID = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("HalloweenIvent")) {
            ALT_HALLOWEENIVENT = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("Whoiscount")) {
            WHOIS_CONTER = Integer.valueOf(pValue); // 追加
            /** 幻想イベント **/
        } else if (pName.equalsIgnoreCase("FantasyEvent")) {
            ALT_FANTASYEVENT = Boolean.valueOf(pValue);
            /** 幻想イベント **/
        } else if (pName.equalsIgnoreCase("RabbitEvent")) {
            ALT_RABBITEVENT = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("FishEvent")) {
            ALT_FISHEVENT = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("JpPrivileged")) {
            ALT_JPPRIVILEGED = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("HouseTaxInterval")) {
            HOUSE_TAX_INTERVAL = Integer.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("MaxDollCount")) {
            MAX_DOLL_COUNT = Integer.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("ReturnToNature")) {
            RETURN_TO_NATURE = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("MaxNpcItem")) {
            MAX_NPC_ITEM = Integer.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("MaxPersonalWarehouseItem")) {
            MAX_PERSONAL_WAREHOUSE_ITEM = Integer.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("MaxClanWarehouseItem")) {
            MAX_CLAN_WAREHOUSE_ITEM = Integer.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("DeleteCharacterAfter7Days")) {
            DELETE_CHARACTER_AFTER_7DAYS = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("GMCODE")) {
            GMCODE = Integer.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("AutoRemoveLevel")) {
            AUTO_REMOVELEVEL = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("BuffLevel")) {
            BUFFLEVEL = Integer.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("BuffLevel1")) {
            TSUTSUKUGEN = Integer.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("TopGrace")) {
            NORMAL_PROTECTION = Integer.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("LarvaLevel")) {
            LASTAVARD_ENTRY_LEVEL = Integer.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("Wizdmg")) {
            WIZARD_MAGIC_DAMAGE = Double.parseDouble(pValue);
        } else if (pName.equalsIgnoreCase("Wizdmg1")) {
            WIZARD_MONSTER_DAMAGE = Double.parseDouble(pValue);

        } else if (pName.equalsIgnoreCase("NewClanLevel")) {
            NEW_CLAN_PROTECTION_LEVEL = Integer.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("NewClanid")) {
            NEW_CLAN = Integer.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("NewClanPvP")) {
            NEW_CLAN_PROTECTION_PROCESS = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("SpawnRobot")) {
            SPAWN_ROBOT = Boolean.valueOf(pValue);
        }

        /** charsettings.properties **/
        else if (pName.equalsIgnoreCase("PrinceMaxHP")) {
            PRINCE_MAX_HP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("PrinceMaxMP")) {
            PRINCE_MAX_MP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("KnightMaxHP")) {
            KNIGHT_MAX_HP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("KnightMaxMP")) {
            KNIGHT_MAX_MP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("ElfMaxHP")) {
            ELF_MAX_HP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("ElfMaxMP")) {
            ELF_MAX_MP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("WizardMaxHP")) {
            WIZARD_MAX_HP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("WizardMaxMP")) {
            WIZARD_MAX_MP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("DarkelfMaxHP")) {
            DARKELF_MAX_HP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("DarkelfMaxMP")) {
            DARKELF_MAX_MP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("DragonknightMaxHP")) {
            DRAGONKNIGHT_MAX_HP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("DragonknightMaxMP")) {
            DRAGONKNIGHT_MAX_MP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("BlackwizardMaxHP")) {
            BLACKWIZARD_MAX_HP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("BlackwizardMaxMP")) {
            BLACKWIZARD_MAX_MP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("LimitLevel")) {
            LIMITLEVEL = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("PrinceAddDamagePc")) {
            PRINCE_ADD_DAMAGEPC = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("KnightAddDamagePc")) {
            KNIGHT_ADD_DAMAGEPC = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("ElfAddDamagePc")) {
            ELF_ADD_DAMAGEPC = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("WizardAddDamagePc")) {
            WIZARD_ADD_DAMAGEPC = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("DarkelfAddDamagePc")) {
            DARKELF_ADD_DAMAGEPC = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("DragonknightAddDamagePc")) {
            DRAGONKNIGHT_ADD_DAMAGEPC = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("BlackwizardAddDamagePc")) {
            BLACKWIZARD_ADD_DAMAGEPC = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv50Exp")) {
            LV50_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv51Exp")) {
            LV51_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv52Exp")) {
            LV52_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv53Exp")) {
            LV53_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv54Exp")) {
            LV54_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv55Exp")) {
            LV55_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv56Exp")) {
            LV56_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv57Exp")) {
            LV57_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv58Exp")) {
            LV58_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv59Exp")) {
            LV59_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv60Exp")) {
            LV60_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv61Exp")) {
            LV61_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv62Exp")) {
            LV62_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv63Exp")) {
            LV63_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv64Exp")) {
            LV64_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv65Exp")) {
            LV65_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv66Exp")) {
            LV66_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv67Exp")) {
            LV67_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv68Exp")) {
            LV68_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv69Exp")) {
            LV69_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv70Exp")) {
            LV70_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv71Exp")) {
            LV71_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv72Exp")) {
            LV72_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv73Exp")) {
            LV73_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv74Exp")) {
            LV74_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv75Exp")) {
            LV75_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv76Exp")) {
            LV76_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv77Exp")) {
            LV77_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv78Exp")) {
            LV78_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv79Exp")) {
            LV79_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv80Exp")) {
            LV80_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv81Exp")) {
            LV81_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv82Exp")) {
            LV82_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv83Exp")) {
            LV83_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv84Exp")) {
            LV84_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv85Exp")) {
            LV85_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv86Exp")) {
            LV86_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv87Exp")) {
            LV87_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv88Exp")) {
            LV88_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv89Exp")) {
            LV89_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv90Exp")) {
            LV90_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv91Exp")) {
            LV91_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv92Exp")) {
            LV92_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv93Exp")) {
            LV93_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv94Exp")) {
            LV94_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv95Exp")) {
            LV95_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv96Exp")) {
            LV96_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv97Exp")) {
            LV97_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv98Exp")) {
            LV98_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv99Exp")) {
            LV99_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("UseShowAnnouncecycle")) {
            Use_Show_Announcecycle = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("ShowAnnouncecycleTime")) {
            Show_Announcecycle_Time = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Raidtime")) {
            RAID_TIME = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("タワー1段階補償")) {
            Tower = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("タワー2段階補償")) {
            MTower = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("タワー3段階の補償")) {
            LTower = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("タワー1段階補償本数")) {
            Tower = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("タワー1段階補償本数")) {
            Tower = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("タワー1段階補償本数")) {
            Tower = Integer.parseInt(pValue);
            // Eventlink set file
        } else if (pName.equalsIgnoreCase("AnoldeEventTime")) {
            ARNOLD_EVENT_TIME = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("OldLastavard")) {
            GLUDIN_DUNGEON_OPEN_CYCLE = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("one")) {
            CRAFT_TABLE_ONE = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("two")) {
            CRAFT_TABLE_TWO = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("three")) {
            CRAFT_TABLE_THREE = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("four")) {
            CRAFT_TABLE_FOUR = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("five")) {
            CRAFT_TABLE_FIVE = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("six")) {
            CRAFT_TABLE_SIX = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("seven")) {
            CRAFT_TABLE_SEVEN = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("eight")) {
            CRAFT_TABLE_EIGHT = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("nine")) {
            CRAFT_TABLE_NINE = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("ten")) {
            CRAFT_TABLE_TEN = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("zero")) {
            CRAFT_TABLE = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("ClanExpOne")) {
            CLAN_EXP_ONE = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("ClanExpTwo")) {
            CLAN_EXP_TWO = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("ClanExpThree")) {
            CLAN_EXP_THREE = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("ClanExpFour")) {
            CLAN_EXP_FOUR = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("ClanExpFive")) {
            CLAN_EXP_FIVE = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("ClanExpSix")) {
            CLAN_EXP_SIX = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("ClanExpSeven")) {
            CLAN_EXP_SEVEN = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("AdenShopNpc")) {
            ADEN_SHOP_NPC = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("NineClass")) {
            NINE_CLASS = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("EightClass")) {
            EIGHT_CLASS = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("SevenClass")) {
            SEVEN_CLASS = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("SixClass")) {
            SIX_CLASS = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("FiveClass")) {
            FIVE_CLASS = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("FourClass")) {
            FOUR_CLASS = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("ThreeClass")) {
            THREE_CLASS = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("TwoClass")) {
            TWO_CLASS = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("OneClass")) {
            ONE_CLASS = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("StarOne")) {
            STAR_ONE = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("StarTwo")) {
            STAR_TWO = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("StarThree")) {
            STAR_THREE = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("StarFour")) {
            STAR_FOUR = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("StarFive")) {
            STAR_FIVE = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("General")) {
            GENERAL = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Imperator")) {
            IMPERATOR = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Commander")) {
            COMMANDER = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("SupremeCommander")) {
            SUPREMECOMMANDER = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("StarFiveDamage")) {
            STAR_FIVE_DAMAGE = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("GeneralDamage")) {
            GENERAL_DAMAGE = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("ImperatorDamage")) {
            IMPERATOR_DAMAGE = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("CommanderDamage")) {
            COMMANDER_DAMAGE = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("SupremeCommanderDamage")) {
            SUPREMECOMMANDER_DAMAGE = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("ClassStartLevel")) {
            CLASS_START_LEVEL = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("AbyssPoint")) {
            ABYSS_POINT = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("FeatherShopNum")) {
            FEATHER_SHOP_NUM = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("RoomtieceChance")) {
            ROOMTIECE_CHANCE = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("TamTime")) {
            Tam_Time = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("FeatherTime")) {
            FEATHER_TIME = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("FishExp")) {
            FISH_EXP = Integer.parseInt(pValue);

        } else if (pName.equalsIgnoreCase("FishTime")) {
            FISH_TIME = Integer.parseInt(pValue);

        } else if (pName.equalsIgnoreCase("FishCom")) {
            FISH_COM = Boolean.valueOf(pValue);

        } else if (pName.equalsIgnoreCase("AdenZone")) {
            ADEN_HUNTING_OPERATION = Boolean.valueOf(pValue);

        } else if (pName.equalsIgnoreCase("AdenLevel")) {
            ADEN_HUNTING_ENTRY_LEVEL = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("AdenTime")) {
            ADEN_HUNTING_TIME = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("強化バフ活力時間")) {
            ENCHANT_BUFF_TIME_VITALITY = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("強化バフ攻撃時間")) {
            ENCHANT_BUFF_TIME_ATTACK = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("強化バフ防御時間")) {
            ENCHANT_BUFF_TIME_DEFENCE = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("強化バフ魔法の時間")) {
            ENCHANT_BUFF_TIME_MAGIC = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("強化バフスタン時間")) {
            ENCHANT_BUFF_TIME_STUN = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("強化バフホールド時間")) {
            ENCHANT_BUFF_TIME_HOLD = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("強化バフ力時間")) {
            ENCHANT_BUFF_TIME_STR = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("強化バフデックス時間")) {
            ENCHANT_BUFF_TIME_DEX = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("強化バフポイント時間")) {
            ENCHANT_BUFF_TIME_INT = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("WantedONE")) {
            STAGE_1 = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("WantedToo")) {
            STAGE_2 = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("WantedThree")) {
            STAGE_3 = Integer.parseInt(pValue);

        } else if (pName.equalsIgnoreCase("npcdmg")) {
            npcdmg = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("npcmagicdmg")) {
            npcmagicdmg = Integer.parseInt(pValue);
            /** イベント用 **/
        } else if (pName.equalsIgnoreCase("mapid")) {
            mapid = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("mapid1")) {
            mapid1 = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("mapid2")) {
            mapid2 = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("mapid3")) {
            mapid3 = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("mapid4")) {
            mapid4 = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("mapid5")) {
            mapid5 = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("経験値")) {
            EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("イベント本数")) {
            EVENT_NUMBERS = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("イベントアイテム")) {
            EVENT_ITEMS = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("useritem")) {
            useritem = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("usercount")) {
            usercount = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("ShockStun")) {
            SHOCK_STUN = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("TWhite")) {
            PURE_WHITE_T = Boolean.valueOf(pValue);

        } else if (pName.equalsIgnoreCase("ARMOR_BRAKE")) {
            ARMOR_BREAK = Integer.parseInt(pValue);

        } else if (pName.equalsIgnoreCase("COUNTER_BARRIER")) {
            COUNTER_BARRIER = Integer.parseInt(pValue);

        } else if (pName.equalsIgnoreCase("DESPERADO")) {
            DESPERADO = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("POWERRIP")) {
            POWER_GRIP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("ERASE_MAGIC")) {
            ERASE_MAGIC = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("EARTH_BIND")) {
            EARTH_BIND = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("WIND_SHACKLE")) {
            WIND_SHACKLE = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("BONE_BREAK")) {
            BONE_BREAK = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("DEATH_HEAL")) {
            DEATH_HEAL = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("ExpMax")) {
            EXP_POT_LIMIT = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("GiftItem")) {
            ALL_GIFT_OPERATION = Boolean.valueOf(pValue);
            /** 新たに追加 **/
        } else if (pName.equalsIgnoreCase("KingD")) {
            KIRINGKU = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("KK")) {
            KNIGHT = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("DK")) {
            DRAGON_KNIGHT = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("EF")) {
            ELF = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("KC")) {
            CROWN = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("MM")) {
            WIZARD = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("DE")) {
            DARK_ELF = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("MB")) {
            ILLUSIONIST = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("WR")) {
            WARRIOR = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("経験値復旧")) {
            RECOVERY_EXP = Integer.parseInt(pValue);

        } else {
            return false;
        }
        return true;
    }

    private Config() {
    }

    public final static int etc_arrow = 0;

    public final static int etc_wand = 1;

    public final static int etc_light = 2;

    public final static int etc_gem = 3;

    public final static int etc_potion = 6;

    public final static int etc_firecracker = 5;

    public final static int etc_food = 7;

    public final static int etc_scroll = 8;

    public final static int etc_questitem = 9;

    public final static int etc_spellbook = 10;

    public final static int etc_other = 12;

    public final static int etc_material = 13;

    public final static int etc_sting = 15;

    public final static int etc_treasurebox = 16;
}