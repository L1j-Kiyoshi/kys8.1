package l1j.server.server.model.Instance;

import static l1j.server.server.model.skill.L1SkillId.*;

import java.awt.Robot;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.GameSystem.AttendanceController;
import l1j.server.GameSystem.Robot.L1RobotInstance;
import l1j.server.IndunSystem.MiniGame.BattleZone;
import l1j.server.IndunSystem.MiniGame.MiniSiege;
import l1j.server.RobotSystem.L1RobotAI;
import l1j.server.server.Account;
import l1j.server.server.ActionCodes;
import l1j.server.server.GameClient;
import l1j.server.server.GameServer;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.Opcodes;
import l1j.server.server.RepeatTask;
import l1j.server.server.SkillCheck;
import l1j.server.server.Controller.AttackController;
import l1j.server.server.Controller.FishingTimeController;
import l1j.server.server.Controller.GhostController;
import l1j.server.server.Controller.LoginController;
import l1j.server.server.Controller.WarTimeController;
import l1j.server.server.command.executor.L1HpBar;
import l1j.server.server.datatables.CharBuffTable;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.CharactersGiftItemTable;
import l1j.server.server.datatables.ClanTable;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.MapsTable;
import l1j.server.server.datatables.MonsterBookTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.AHRegeneration;
import l1j.server.server.model.Ability;
import l1j.server.server.model.AcceleratorChecker;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.Getback;
import l1j.server.server.model.HalloweenRegeneration;
import l1j.server.server.model.HelpBySupport;
import l1j.server.server.model.HpRegenerationByDoll;
import l1j.server.server.model.L1AccountAttendance;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1ChatParty;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1DeathMatch;
import l1j.server.server.model.L1EquipmentSlot;
import l1j.server.server.model.L1GroundInventory;
import l1j.server.server.model.L1HauntedHouse;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Karma;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Magic;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Party;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1PinkName;
import l1j.server.server.model.L1Quest;
import l1j.server.server.model.L1Racing;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1TownLocation;
import l1j.server.server.model.L1Trade;
import l1j.server.server.model.L1War;
import l1j.server.server.model.L1World;
import l1j.server.server.model.MpDecreaseByScales;
import l1j.server.server.model.MpRegenerationByDoll;
import l1j.server.server.model.PapuBlessing;
import l1j.server.server.model.ReportDeley;
import l1j.server.server.model.SHRegeneration;
import l1j.server.server.model.Warehouse.WarehouseManager;
import l1j.server.server.model.classes.L1ClassFeature;
import l1j.server.server.model.gametime.L1GameTimeCarrier;
import l1j.server.server.model.monitor.L1PcAutoUpdate;
import l1j.server.server.model.monitor.L1PcExpMonitor;
import l1j.server.server.model.monitor.L1PcGhostMonitor;
import l1j.server.server.model.monitor.L1PcHellMonitor;
import l1j.server.server.model.monitor.L1PcInvisDelay;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.monitor.Logger.ItemActionType;
import l1j.server.server.monitor.LoggerInstance;
import l1j.server.server.serverpackets.S_BlueMessage;
import l1j.server.server.serverpackets.S_CastleMaster;
import l1j.server.server.serverpackets.S_ChangeShape;
import l1j.server.server.serverpackets.S_CharTitle;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_ClanName;
import l1j.server.server.serverpackets.S_DelSkill;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_DoActionShop;
import l1j.server.server.serverpackets.S_Fishing;
import l1j.server.server.serverpackets.S_HPMeter;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_Invis;
import l1j.server.server.serverpackets.S_Lawful;
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_NewCreateItem;
import l1j.server.server.serverpackets.S_OtherCharPacks;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_OwnCharStatus2;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_PinkName;
import l1j.server.server.serverpackets.S_Poison;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_ReturnedStat;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillIconGFX;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SummonPack;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_Weight;
import l1j.server.server.serverpackets.ServerBasePacket;
import l1j.server.server.serverpackets.ServerMessage;
import l1j.server.server.templates.L1BookMark;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1PrivateShopBuyList;
import l1j.server.server.templates.L1PrivateShopSellList;
import l1j.server.server.templates.L1Skills;
import l1j.server.server.types.Point;
import l1j.server.server.utils.CalcStat;
import l1j.server.server.utils.SQLUtil;
import manager.LinAllManager;
import manager.LinAllManagerInfoThread;

public class L1PcInstance extends L1Character {
    /**
     * 日付と時刻の記録
     **/
    Calendar rightNow = Calendar.getInstance();
    int day = rightNow.get(Calendar.DATE);
    int hour = rightNow.get(Calendar.HOUR);
    int min = rightNow.get(Calendar.MINUTE);
    int sec = rightNow.get(Calendar.SECOND);
    int year = rightNow.get(Calendar.YEAR);
    int month = rightNow.get(Calendar.MONTH) + 1;
    String totime = "[" + year + ":" + month + ":" + day + "]";
    String totime1 = "[" + hour + ":" + min + ":" + sec + "]";
    String date = +year + "_" + month + "_" + day;
    private long FishingShopBuyTime_1;

    public int MerchantSearchObjid = 0;
    public boolean war_zone = false;

    public boolean isSiege = false;
    private int SiegeTeam = -1;

    public void setTeam(int i) {
        SiegeTeam = i;
    }

    public int getTeam() {
        return SiegeTeam;
    }

    private Random random2 = new Random(System.nanoTime());

    public int getNcoin() { // ????
        if (getAccount() != null) {
            return getNetConnection().getAccount().Ncoin_point;
        }
        return 0;
    }

    public void addNcoin(int coin) {
        if (getNetConnection() != null) {
            if (getNetConnection().getAccount() != null) {
                getNetConnection().getAccount().Ncoin_point += coin;
            }
        }
    }


	/**
  	 * @author xavie
  	 * 6.2c weapon range
  	 */
  	private int _range = 0;

  	public void setRange(int i) {
  		_range = i;
  	}

	public int getRange() {
  		return _range;
  	}

    // 付加アイテムのショートカット
    private boolean _PackegeWarehoue = false;

    public boolean isPackegeWarehouse() {
        return _PackegeWarehoue;
    }

    public boolean setPackegeWarehouse(boolean set) {
        return _PackegeWarehoue = set;
    }

    private ReportDeley _reportdeley; // /申告追加

    public void startReportDeley() { // /申告追加
        _reportdeley = new ReportDeley(this);
        _regenTimer.schedule(_reportdeley, 100000); // ディレイ時間10分
    }

    // /申告追加
    private boolean _isReport = true;

    public void setReport(boolean _isreport) {
        _isReport = _isreport;
    }

    public boolean isReport() {
        return _isReport;
    }

    /**
     * ロボット
     **/

    private boolean _isRobot = false;

    public boolean isRobot() {
        return _isRobot;
    }

    public void setRobot(boolean flag) {
        _isRobot = flag;
    }

    /**
     * ロボットダイメント
     */
    private String diement = null;
    private static String[] _diementArray = { "すでにジュクニャ", "ーー", "ダイ笑", "ふふふふふふ", "", "本ジョクバプ笑", "" };

    public void Delay(int delayTime) throws Exception {

        int mdelayTime;
        mdelayTime = delayTime;
        Robot robot = new Robot();
        robot.delay(mdelayTime);
    } // ロボットダイメント

    /**
     * クラン加入日
     **/
    private Timestamp _clan_join_date = null;;

    public void setClanJoinDate(Timestamp date) {
        _clan_join_date = date;
    }

    public Timestamp getClanJoinDate() {
        return _clan_join_date;
    }

    /**                                **/
    // ジュクウェ
    private int WeekType = 1;
    private boolean LineClear[] = { false, false, false };
    private boolean Reward[] = { false, false, false };
    private ArrayList<Integer> wcount = new ArrayList<Integer>();
    private int QuestWeek = 0;

    public int getQuestWeek() {
        return QuestWeek;
    }

    public void setQuestWeek(int value) {
        QuestWeek = value;
    }

    public void setWcount(int value) {
        wcount.add(value);
    }

    public void setWcount(int i, int value) {
        wcount.set(i, value);
    }

    /*
     * public int getWcount(int i){ return wcount.get(i); }
     */

    public void setLineClear(int i, boolean a) {
        LineClear[i] = a;
    }

    public boolean isLineClear(int i) {
        return LineClear[i];
    }

    public void setReward(int i, boolean a) {
        Reward[i] = a;
    }

    public boolean getReward(int i) {
        return Reward[i];
    }

    public void setWeekType(int i) {
        WeekType = i;
    }

    public int getWeekType() {
        return WeekType;
    }

    /**
     * クラウディアアイテム
     **/
    public int cL = 0;
    // 回復巻
    public boolean isRestore = false;
    /*** 機器変更巻 ***/
    private boolean isChangeItem = false;

    public boolean getIsChangeItem() {
        return isChangeItem;
    }

    public void setIsChangeItem(boolean a) {
        isChangeItem = a;
    }

    public int changeNpcId = 90000; // default

    private L1ItemInstance defaultItem;

    public void setDefaultItem(L1ItemInstance i) {
        defaultItem = i;
    }

    public L1ItemInstance getDefaultItem() {
        return defaultItem;
    }

    /*** 機器変更巻 **/
    /**
     * 記章クリティカル
     **/
    private int MagicCritical = 0;

    public void addMagicCritical(int i) {
        MagicCritical = i;
    }

    public int getMagicCritical() {
        return MagicCritical;
    }

    private int dmgCritical = 0;

    public void addDmgCritical(int i) {
        dmgCritical = i;
    }

    public int getDmgCritical() {
        return dmgCritical;
    }

    private int bowCritical = 0;

    public void addBowDmgCritical(int i) {
        bowCritical = i;
    }

    public int getBowDmgCritical() {
        return bowCritical;
    }

    /*************************/
    /*** 機器買取 ****/
    public boolean isNpcSell = false;

    public L1NpcInstance isNpcid = null;

    /**
     * ギャンブラー
     **/
    public boolean isJGembleing = false;
    public L1NpcInstance gembleNpc = null;

    private static final long serialVersionUID = 1L;

    public static final int CLASSID_PRINCE = 0;
    public static final int CLASSID_PRINCESS = 1;
    public static final int CLASSID_KNIGHT_MALE = 61;
    public static final int CLASSID_KNIGHT_FEMALE = 48;
    public static final int CLASSID_ELF_MALE = 138;
    public static final int CLASSID_ELF_FEMALE = 37;
    public static final int CLASSID_WIZARD_MALE = 734;
    public static final int CLASSID_WIZARD_FEMALE = 1186;
    public static final int CLASSID_DARK_ELF_MALE = 2786;
    public static final int CLASSID_DARK_ELF_FEMALE = 2796;
    public static final int CLASSID_DRAGONKNIGHT_MALE = 6658;
    public static final int CLASSID_DRAGONKNIGHT_FEMALE = 6661;
    public static final int CLASSID_BLACKWIZARD_MALE = 6671;
    public static final int CLASSID_BLACKWIZARD_FEMALE = 6650;
    public static final int CLASSID_WARRIOR_MALE = 12490;
    public static final int CLASSID_WARRIOR_FEMALE = 12494;

    public static final int REGENSTATE_NONE = 4;
    public static final int REGENSTATE_MOVE = 2;
    public static final int REGENSTATE_ATTACK = 1;
    public int _npcnum = 0;
    public String _npcname = "";
    public long tamtime = 0;

    // アビスポイント
    private int _Abysspoint;

    public synchronized int getAbysspoint() {
        return _Abysspoint;
    }

    public synchronized void setAbysspoint(int Abysspoint) {
        _Abysspoint = Abysspoint;
    }

    public synchronized void addAbysspoint(int Abysspoint) {
        _Abysspoint += Abysspoint;
    }

    // アビスポイントによる階級算出
    private int _peerage = 0;
    private boolean _9Militia;
    private boolean _8Militia;
    private boolean _7Militia;
    private boolean _6Militia;
    private boolean _5Militia;
    private boolean _4Militia;
    private boolean _3Militia;
    private boolean _2Militia;
    private boolean _1Militia;
    private boolean _1Officer;
    private boolean _2Officer;
    private boolean _3Officer;
    private boolean _4Officer;
    private boolean _5Officer;
    private boolean _General;
    private boolean _MajorGeneral;
    private boolean _Commander;
    private boolean _GeneralCommander;

    public int getPeerage() {
        return _peerage;
    }

    public void setPeerage(int i) {
        _peerage = i;
    }

    public boolean is9Militia() {
        return _9Militia;
    }

    public void set9Militia(boolean flag) {
        _9Militia = flag;
    }

    public boolean is8Militia() {
        return _8Militia;
    }

    public void set8Militia(boolean flag) {
        _8Militia = flag;
    }

    public boolean is7Militia() {
        return _7Militia;
    }

    public void set7Militia(boolean flag) {
        _7Militia = flag;
    }

    public boolean is6Militia() {
        return _6Militia;
    }

    public void set6Militia(boolean flag) {
        _6Militia = flag;
    }

    public boolean is5Militia() {
        return _5Militia;
    }

    public void set5Militia(boolean flag) {
        _5Militia = flag;
    }

    public boolean is4Militia() {
        return _4Militia;
    }

    public void set4Militia(boolean flag) {
        _4Militia = flag;
    }

    public boolean is3Militia() {
        return _3Militia;
    }

    public void set3Militia(boolean flag) {
        _3Militia = flag;
    }

    public boolean is2Militia() {
        return _2Militia;
    }

    public void set2Militia(boolean flag) {
        _2Militia = flag;
    }

    public boolean is1Militia() {
        return _1Militia;
    }

    public void set1Militia(boolean flag) {
        _1Militia = flag;
    }

    public boolean is1Officer() {
        return _1Officer;
    }

    public void set1Officer(boolean flag) {
        _1Officer = flag;
    }

    public boolean is2Officer() {
        return _2Officer;
    }

    public void set2Officer(boolean flag) {
        _2Officer = flag;
    }

    public boolean is3Officer() {
        return _3Officer;
    }

    public void set3Officer(boolean flag) {
        _3Officer = flag;
    }

    public boolean is4Officer() {
        return _4Officer;
    }

    public void set4Officer(boolean flag) {
        _4Officer = flag;
    }

    public boolean is5Officer() {
        return _5Officer;
    }

    public void set5Officer(boolean flag) {
        _5Officer = flag;
    }

    public boolean isGeneral() {
        return _General;
    }

    public void setGeneral(boolean flag) {
        _General = flag;
    }

    public boolean isMajorGeneral() {
        return _MajorGeneral;
    }

    public void setMajorGeneral(boolean flag) {
        _MajorGeneral = flag;
    }

    public boolean isCommander() {
        return _Commander;
    }

    public void setCommander(boolean flag) {
        _Commander = flag;
    }

    public boolean isGeneralCommander() {
        return _GeneralCommander;
    }

    public void setGeneralCommander(boolean flag) {
        _GeneralCommander = flag;
    }

    // コンボシステム
    public int getComboCount() {
        return this.comboCount;
    }

    public void setComboCount(int comboCount) {
        this.comboCount = comboCount;
    }

    public boolean PCRoom_Buff = false;
    public boolean PCRoom_Buff_Delete = false;

    private boolean isSafetyZone;

    public boolean getSafetyZone() {
        return isSafetyZone;
    }

    public void setSafetyZone(boolean value) {
        isSafetyZone = value;
    }

    /**
     * レイドY_N
     **/
    private boolean _raid = false;

    public void setRaidGame(boolean flag) {
        this._raid = flag;
    }

    public boolean getRaidGame() {
        return _raid;
    }

    private boolean _Mayo = false;

    public void setMayo(boolean flag) {
        this._Mayo = flag;
    }

    public boolean getMayo() {
        return _Mayo;
    }

    private boolean _Necross = false;

    public void setNecross(boolean flag) {
        this._Necross = flag;
    }

    public boolean getNecross() {
        return _Necross;
    }

    private boolean _Tebeboss = false;

    public void setTebeboss(boolean flag) {
        this._Tebeboss = flag;
    }

    public boolean getTebeboss() {
        return _Tebeboss;
    }

    private boolean _Curch = false;

    public void setCurch(boolean flag) {
        this._Curch = flag;
    }

    public boolean getCurch() {
        return _Curch;
    }

    private boolean _dtah = false;

    public void setDeat(boolean flag) {
        this._dtah = flag;
    }

    public boolean getDeat() {
        return _dtah;
    }

    private boolean _trac = false;

    public void setTrac(boolean flag) {
        this._trac = flag;
    }

    public boolean getTrac() {
        return _trac;
    }

    private boolean _girtas = false;

    public void setGirtas(boolean flag) {
        this._girtas = flag;
    }

    public boolean getGirtas() {
        return _girtas;
    }

    private boolean _orim = false;

    public void setOrim(boolean flag) {
        this._orim = flag;
    }

    public boolean getOrim() {
        return _orim;
    }

    private boolean _erzarbe = false;

    public void setErzarbe(boolean flag) {
        this._erzarbe = flag;
    }

    public boolean getErzarbe() {
        return _erzarbe;
    }

    private boolean _Hondon = false;

    public void setHondon(boolean flag) {
        this._Hondon = flag;
    }

    public boolean getHondon() {
        return _Hondon;
    }

    private boolean _Reper = false;

    public void setReper(boolean flag) {
        this._Reper = flag;
    }

    public boolean getReper() {
        return _Reper;
    }

    private boolean _GrimReaper = false;

    public void setGrimReaper(boolean flag) {
        _GrimReaper = flag;
    }

    public boolean getGrimReaper() {
        return _GrimReaper;
    }

    private boolean _Zenis = false;

    public void setZenis(boolean flag) {
        _Zenis = flag;
    }

    public boolean getZenis() {
        return _Zenis;
    }

    private boolean _Sia = false;

    public void setSia(boolean flag) {
        _Sia = flag;
    }

    public boolean getSia() {
        return _Sia;
    }

    private boolean _Vampire = false;

    public void setVampire(boolean flag) {
        _Vampire = flag;
    }

    public boolean getVampire() {
        return _Vampire;
    }

    private boolean _ZombieLoad = false;

    public void setZombieLoad(boolean flag) {
        _ZombieLoad = flag;
    }

    public boolean getZombieLoad() {
        return _ZombieLoad;
    }

    private boolean _Cougar = false;

    public void setCougar(boolean flag) {
        _Cougar = flag;
    }

    public boolean getCougar() {
        return _Cougar;
    }

    private boolean _MummyLord = false;

    public void setMummyLord(boolean flag) {
        _MummyLord = flag;
    }

    public boolean getMummyLord() {
        return _MummyLord;
    }

    private boolean _Iris = false;

    public void setIris(boolean flag) {
        _Iris = flag;
    }

    public boolean getIris() {
        return _Iris;
    }

    private boolean _KnightBald = false;

    public void setKnightBald(boolean flag) {
        _KnightBald = flag;
    }

    public boolean getKnightBald() {
        return _KnightBald;
    }

    private boolean _Lich = false;

    public void setLich(boolean flag) {
        _Lich = flag;
    }

    public boolean getLich() {
        return _Lich;
    }

    private boolean _Ugnus = false;

    public void setUgnus(boolean flag) {
        _Ugnus = flag;
    }

    public boolean getUgnus() {
        return _Ugnus;
    }

    private boolean _Balrog = false;

    public void setBalrog(boolean flag) {
        _Balrog = flag;
    }

    public boolean getBalrog() {
        return _Balrog;
    }

    // アカウントジョンロロード時に必要。
    public Account getAccount() {
        return this._netConnection.getAccount();
    }

    /**
     * ポー
     **/
    public boolean FouSlayer = false;

    /**
     * トリプル
     **/
    public boolean TRIPLE = false;

    // 文章注視変数
    public boolean watchCrest = false;

    public boolean monsterDown = false;

    public int _x;

    // 成長の釣り関連
    private L1ItemInstance _fishingitem;

    public L1ItemInstance getFishingItem() {
        return _fishingitem;
    }

    public void setFishingItem(L1ItemInstance item) {
        _fishingitem = item;
    }

    /**
     * AttackController
     **/
    public long AttackControllerTime = 0;
    /** AttackController **/
    /**
     * SPRチェック
     **/
    public int AttackSpeedCheck2 = 0;
    public int MoveSpeedCheck = 0;
    public int magicSpeedCheck = 0;
    public long AttackSpeed2;
    public long MoveSpeed;
    public long magicSpeed;
    /**
     * SPRチェック
     **/

    public int dx = 0;
    public int dy = 0;
    public short dm = 0;
    public int dh = 0;
    public int shopPoly = 0;
    /**
     * キャラクター別追加ダメージ、追加リダクション、確率
     **/
    private int _AddDamage = 0;
    private int _AddDamageRate = 0;
    private int _AddReduction = 0;
    private int _AddReductionRate = 0;

    public int getAddDamage() {
        return _AddDamage;
    }

    public void setAddDamage(int addDamage) {
        _AddDamage = addDamage;
    }

    public int getAddDamageRate() {
        return _AddDamageRate;
    }

    public void setAddDamageRate(int addDamageRate) {
        _AddDamageRate = addDamageRate;
    }

    public int getAddReduction() {
        return _AddReduction;
    }

    public void setAddReduction(int addReduction) {
        _AddReduction = addReduction;
    }

    public int getAddReductionRate() {
        return _AddReductionRate;
    }

    public void setAddReductionRate(int addReductionRate) {
        _AddReductionRate = addReductionRate;
    }

    /**
     * キャラクター別追加ダメージ、追加リダクション、確率
     **/

    private int _ubscore;

    public int getUbScore() {
        return _ubscore;
    }

    public void setUbScore(int i) {
        _ubscore = i;
    }

    public byte[] fairyInfo = new byte[512];

    public void fairyExpReward(int lv) {
        int needExp = ExpTable.getNeedExpNextLevel(lv);
        int addexp = 0;
        addexp = (int) (needExp * 0.01);
        if (addexp != 0) {
            int level = ExpTable.getLevelByExp(getExp() + addexp);
            if (level > 60) {
                sendPackets(new S_SystemMessage("もう経験値を獲得することができません。"));
            } else {
                addExp(addexp);
            }
        }
    }

    public void saveFairyInfo(int id) {
        int count = fairlycount(getId());
        fairyInfo[id] = 1;
        if (count == 0) {
            fairlystore(getId(), fairyInfo);
        } else {

            fairlupdate(getId(), fairyInfo);
        }
    }

    public int fairlycount(int objectId) {
        int result = 0;
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT count(*) as cnt FROM character_fairly_config WHERE object_id=?");
            pstm.setInt(1, objectId);
            rs = pstm.executeQuery();
            if (rs.next()) {
                result = rs.getInt("cnt");
            }
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return result;
    }

    public void fairlystore(int objectId, byte[] data) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("INSERT INTO character_fairly_config SET object_id=?, data=?");
            pstm.setInt(1, objectId);
            pstm.setBytes(2, data);
            pstm.executeUpdate();
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void fairlupdate(int objectId, byte[] data) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("UPDATE character_fairly_config SET data=? WHERE object_id=?");
            pstm.setBytes(1, data);
            pstm.setInt(2, objectId);
            pstm.executeUpdate();
        } catch (SQLException e) {
            // _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    /**
     * ブレイブアバター
     **/
    private boolean _Pbavatar = false;

    public boolean getPbavatar() {
        return _Pbavatar;
    }

    public void setPbavatar(boolean Pbavatar) {
        _Pbavatar = Pbavatar;
    }

    private boolean _Pbavataron = false;

    public boolean getPbavataron() {
        return _Pbavataron;
    }

    public void setPbavataron(boolean Pbavataron) {
        _Pbavataron = Pbavataron;
    }

    public int _Pbacount = 0;

    public int getPbacount() {
        return _Pbacount;
    }

    public void setPbacount(int i) {
        _Pbacount = i;
    }

    /**
     * ブレイブアバター
     **/

    public L1ItemInstance _fishingRod = null;

    private static Random _random = new Random(System.nanoTime());

    private L1ClassFeature _classFeature = null;
    private L1EquipmentSlot _equipSlot;
    private String _accountName;
    private int _classId;
    private int _type;
    private int _exp;
    private int _age;
    /**
     * 年齢設定
     **/
    private short _accessLevel;

    private short _baseMaxHp = 0;
    private int _baseMaxMp = 0;
    private int _baseAc = 0;
    private int _originalMagicHit = 0;
    private int _baseBowDmgup = 0;
    private int _baseDmgup = 0;
    private int _baseHitup = 0;
    private int _baseBowHitup = 0;

    private int _baseMagicHitup = 0; // ベースステータスによる魔法命中
    private int _baseMagicCritical = 0; // ベースステータスによる魔法クリティカル（％）
    private int _baseMagicDmg = 0; // ベースステータスによる魔法ダメージ
    private int _baseMagicDecreaseMp = 0; // ベースステータスによる魔法ダメージ

    private int _DmgupByArmor = 0; // 防具による近接武器追加打率
    private int _bowDmgupByArmor = 0; // 防具による弓の追加打率

    private int _PKcount;
    public int _fishingX = 0;
    public int _fishingY = 0;
    private int _clanid = 0;
    private String clanname;
    private int _clanRank;
    private byte _sex;
    private int _returnstat;
    private short _hpr = 0;
    private short _trueHpr = 0;
    private short _mpr = 0;
    private short _trueMpr = 0;

    private int _advenHp;
    private int _advenMp;
    private int _giganticHp;
    private int _highLevel;
    private int _bonusStats;
    public boolean isInFantasy = false; // モンソムリニューアル
    /**
     * 火竜の聖域
     **/
    public boolean isInValakasBoss = false;
    public boolean isInValakas = false;
    private boolean _ghost = false;
    private boolean _ghostCanTalk = true;
    private boolean _isReserveGhost = false;
    private boolean _isShowTradeChat = true;
    private boolean _isCanWhisper = true;
    private boolean _isFishing = false;
    private boolean _isFishingReady = false;
    private boolean isDeathMatch = false; // デスマッチ
    private boolean _isSupporting = false;
    private boolean _isShowWorldChat = true;
    private boolean _gm;
    private boolean _monitor;
    private boolean _gmInvis;
    private boolean _isTeleport = false;
    private boolean _isDrink = false;
    private boolean _isGres = false;
    private boolean _isPinkName = false;
    private boolean _banned;
    private boolean _gresValid;
    private boolean _tradeOk;
    private boolean _mpRegenActive;
    private boolean _mpRegenActiveByDoll;
    private boolean _mpDecreaseActiveByScales;
    private boolean _HelpActiveBySupport;
    private boolean _hpRegenActive;
    private boolean _AHRegenActive;
    private boolean _SHRegenActive;
    private boolean _HalloweenRegenActive;
    private boolean _hpRegenActiveByDoll;

    public boolean RootMent = true;// ルーティングメント]

    public boolean noPlayerck2 = false;
    public boolean noPlayerCK = false;
    public boolean noPlayerRobot = false;

    private int invisDelayCounter = 0;
    private Object _invisTimerMonitor = new Object();

    private int _ghostSaveLocX = 0;
    private int _ghostSaveLocY = 0;
    private short _ghostSaveMapId = 0;
    @SuppressWarnings("unused")
    private int _ghostSaveHeading = 0;
    public byte _ghostCount = 0;
    public long ghosttime = 0;

    private ScheduledFuture<?> _ghostFuture;
    private ScheduledFuture<?> _hellFuture;
    private ScheduledFuture<?> _autoUpdateFuture;
    private ScheduledFuture<?> _expMonitorFuture;

    private Timestamp _lastPk;
    private Timestamp _deleteTime;
    private Timestamp _lastLoginTime;
    private int _einhasad;

    private int _lastAutoCheckTime;
    private int _autoCheckDuration;
    private int _autoCheckCount;
    private String _autoAuthCode;

    /**
     * 手配の設定
     **/
    private int huntCount;
    private int huntPrice;
    private String _reasontohunt;

    public String getReasonToHunt() {
        return _reasontohunt;
    }

    public void setReasonToHunt(String s) {
        _reasontohunt = s;
    }

    public int getHuntCount() {
        return huntCount;
    }

    public void setHuntCount(int i) {
        huntCount = i;
    }

    public int getHuntPrice() {
        return huntPrice;
    }

    public void setHuntPrice(int i) {
        huntPrice = i;
    }

    /**
     * 手配の設定
     **/

    private int _weightReduction = 0;
    private int _hasteItemEquipped = 0;
    private int _damageReductionByArmor = 0;
    private int _regist_PVPweaponTotalDamage = 0;
    private int _DmgRate = 0; // 防具による近接武器追加打率
    private int _HitRate = 0; // 防具による近接武器命中率
    private int _bowHitRate = 0; // 防具による弓の命中率
    private int _bowDmgRate = 0; // 防具による弓の追加打率
    private int _MagicHitupByArmor = 0; // 防具による魔法確率増加

    private final AcceleratorChecker _acceleratorChecker = new AcceleratorChecker(this);

    private int _teleportY = 0;
    private int _teleportX = 0;
    private short _teleportMapId = 0;
    private int _teleportHeading = 0;

    private int _tempCharGfxAtDead;
    private int _fightId;
    private byte _chatCount = 0;
    private long _oldChatTimeInMillis = 0L;

    private int _elixirStats;
    private int _elfAttr;
    private int _expRes;

    private int _onlineStatus;
    private int _homeTownId;
    private int _contribution;
    private int _food;
    private int _hellTime;
    private int _partnerId;
    private long _fishingTime = 0;
    private int _dessertId = 0;
    private int _callClanId;
    private int _callClanHeading;

    private int _currentWeapon; // ロボット関連
    private final L1Karma _karma = new L1Karma();
    private final L1PcInventory _inventory;
    // private final L1DwarfForPackageInventory _dwarfForPackage;
    private final L1Inventory _tradewindow;

    private L1ItemInstance _weapon;
    private L1ItemInstance _secondweapon;
    private L1ItemInstance _armor;
    private L1Party _party;
    private L1ChatParty _chatParty;

    private int _cookingId = 0;
    private int _partyID;
    private int _partyType;
    private int _tradeID;
    private int _tempID;

    private L1Quest _quest;
    private HelpBySupport _HelpBySupport;

    private HpRegenerationByDoll _hpRegenByDoll;
    private MpRegenerationByDoll _mpRegenByDoll;
    private MpDecreaseByScales _mpDecreaseByScales;
    private AHRegeneration _AHRegen;
    private SHRegeneration _SHRegen;
    private HalloweenRegeneration _HalloweenRegen;

    // //-- 修正[追加されたソースブロック]
    // private final L1ExcludingList _excludingList = new L1ExcludingList();
    //
    // public L1ExcludingList getExcludingList() {
    // return _excludingList;
    // }

    private static Timer _regenTimer = new Timer(true);

    private boolean _isPrivateShop = false;
    private boolean _isAutoClanjoin = false;// 無人登録
    private int _partnersPrivateShopItemCount = 0;

    private long _lastPasswordChangeTime;
    private long _lastQuizChangeTime;
    private long _lastLocalTellTime;
    private boolean _needQuiz = false;
    private boolean _isQuizValidated = false;

    boolean isExpDrop;
    boolean isItemDrop;

    public final ArrayList<L1BookMark> _speedbookmarks;

    public L1BookMark[] getBookMarkArray() {
        return _bookmarks.toArray(new L1BookMark[_bookmarks.size()]);
    }

    public L1BookMark[] getSpeedBookMarkArray() {
        return _speedbookmarks.toArray(new L1BookMark[_speedbookmarks.size()]);
    }

    private int _markcount;

    public void setMark_count(int i) {
        _markcount = i;
    }

    public int getMark_count() {
        return _markcount;
    }

    /**
     * 血盟バフ
     **/
    private boolean _clanbuff = false;

    public boolean isClanBuff() {
        return _clanbuff;
    }

    public void setClanBuff(boolean c) {
        _clanbuff = c;
    }

    /** 血盟バフ **/

    /**
     * バトルゾーン
     **/
    private int _DuelLine;

    public int get_DuelLine() {
        return _DuelLine;
    }

    public void set_DuelLine(int i) {
        _DuelLine = i;
    }

    // 生存の叫び
    public int _getLive = 0;

    public int getLive() {
        return _getLive;
    }

    public void addLive(int Live) {
        _getLive += Live;
    }

    public void setLive(int Live) {
        _getLive = Live;
    }

    // ゾウの火ゴーレム
    public int[] FireGolem = new int[18];
    public int[] FireEnchant = new int[18];

    // ピアス
    public int[] PiersItemId = new int[19];
    public int[] PiersEnchant = new int[19];

    private boolean _TelWait = false;
    private boolean _isElrzabe = false;
    private boolean _isSandWarm = false;
    private boolean _isDrake = false;
    private boolean _isZeros = false;
    private boolean _isGirtas = false;
    private boolean _isKingSquid = false;
    private boolean _isValakas = false;
    private boolean _isFafurion = false;
    private boolean _isLindvior = false;
    private boolean _isAntaras = false;

    public boolean setValakaseDmgDouble = false;

    // ヴァラカス
    public static int valakasMapId = 0;

    private boolean _magicitem = false;
    private int _magicitemid;
    // 3.63アイテムパケット追加
    public boolean isWorld = false;
    // 3.63アイテムパケット追加
    public boolean isDanteasBuff = false;

    public boolean serverDown = false;
    public boolean baphomettRoom = false;
    // ジェンドール製作関連
    public int _getCount;

    private long _npcaction;

    public final ArrayList<L1BookMark> _bookmarks;
    private ArrayList<L1PrivateShopSellList> _sellList = new ArrayList<L1PrivateShopSellList>();
    private ArrayList<L1PrivateShopBuyList> _buyList = new ArrayList<L1PrivateShopBuyList>();
    private byte[] _shopChat;
    private AtomicInteger _pinkNameTime;
    private GameClient _netConnection;
    private static Logger _log = Logger.getLogger(L1PcInstance.class.getName());

    public L1PcInstance() {
        _accessLevel = 0;
        _currentWeapon = 0;
        _inventory = new L1PcInventory(this);
        // _dwarfForPackage = new L1DwarfForPackageInventory(this);
        _tradewindow = new L1Inventory();
        _bookmarks = new ArrayList<L1BookMark>();
        _speedbookmarks = new ArrayList<L1BookMark>();

        _quest = new L1Quest(this);
        _equipSlot = new L1EquipmentSlot(this);
        _pinkNameTime = new AtomicInteger(0);
    }

    public long get_lastQuizChangeTime() {
        return _lastQuizChangeTime;
    }

    public long get_lastPasswordChangeTime() {
        return _lastPasswordChangeTime;
    }

    public void setQuizValidated() {
        _isQuizValidated = true;
    }

    public boolean isQuizValidated() {
        return _isQuizValidated;
    }

    public long get_lastLocalTellTime() {
        return _lastLocalTellTime;
    }

    public void update_lastPasswordChangeTime() {
        _lastPasswordChangeTime = System.currentTimeMillis();
    }

    public void update_lastLocalTellTime() {
        _lastLocalTellTime = System.currentTimeMillis();
    }

    public void update_lastQuizChangeTime() {
        _lastQuizChangeTime = System.currentTimeMillis();
    }

    public void setNeedQuiz(boolean needQuiz) {
        _needQuiz = needQuiz;
    }

    public boolean isNeedQuiz() {
        return _needQuiz;
    }

    private long _lastShellUseTime;

    public long getlastShellUseTime() {
        return _lastShellUseTime;
    }

    public void updatelastShellUseTime() {
        _lastShellUseTime = System.currentTimeMillis();
    }

    public int getPinkNameTime() {
        return _pinkNameTime.get();
    }

    public int DecrementPinkNameTime() {
        return _pinkNameTime.decrementAndGet();
    }

    public int SetPinkNameTime(int timeValue) {
        return _pinkNameTime.getAndSet(timeValue);
    }

    public short getHpr() {
        return _hpr;
    }

    public void addHpr(int i) {
        _trueHpr += i;
        _hpr = (short) Math.max(0, _trueHpr);
    }

    public short getMpr() {
        return _mpr;
    }

    public void addMpr(int i) {
        _trueMpr += i;
        _mpr = (short) Math.max(0, _trueMpr);
    }

    public void setElrzabe(boolean flag) {
        _isElrzabe = flag;
    }

    public boolean isElrzabe() {
        return _isElrzabe;
    }

    public void setSandWarm(boolean flag) {
        _isSandWarm = flag;
    }

    public boolean isSandWarm() {
        return _isSandWarm;
    }

    public void setDrake(boolean flag) {
        _isDrake = flag;
    }

    public boolean isDrake() {
        return _isDrake;
    }

    public void setZeros(boolean flag) {
        _isZeros = flag;
    }

    public boolean isZeros() {
        return _isZeros;
    }

    public void setisGirtas(boolean flag) {
        _isGirtas = flag;
    }

    public boolean isGirtas() {
        return _isGirtas;
    }

    public void setKingSquid(boolean flag) {
        _isKingSquid = flag;
    }

    public boolean isKingSquid() {
        return _isKingSquid;
    }

    public void setValakas(boolean flag) {
        _isValakas = flag;
    }

    public boolean isValakas() {
        return _isValakas;
    }

    public void setFafurion(boolean flag) {
        _isFafurion = flag;
    }

    public boolean isFafurion() {
        return _isFafurion;
    }

    public void setLindvior(boolean flag) {
        _isLindvior = flag;
    }

    public boolean isLindvior() {
        return _isLindvior;
    }

    public void setAntaras(boolean flag) {
        _isAntaras = flag;
    }

    public boolean isAntaras() {
        return _isAntaras;
    }

    public long getNpcActionTime() {
        return _npcaction;
    }

    public void setNpcActionTime(long flag) {
        _npcaction = flag;
    }

    public boolean isMagicItem() {
        return _magicitem;
    }

    public void setMagicItem(boolean flag) {
        _magicitem = flag;
    }

    public int getMagicItemId() {
        return _magicitemid;
    }

    public void setMagicItemId(int itemid) {
        _magicitemid = itemid;
    }

    private PapuBlessing _PapuRegen;
    private boolean _PapuBlessingActive;

    public void startPapuBlessing() {// 波フーガ号
        final int RegenTime = 150000;
        if (!_PapuBlessingActive) {
            _PapuRegen = new PapuBlessing(this);
            _PapuBlessingActive = true;
            GeneralThreadPool.getInstance().schedule(_PapuRegen, RegenTime);
        }
    }

    public void startHpRegenerationByDoll() {
        final int INTERVAL_BY_DOLL = 32000;
        boolean isExistHprDoll = false;
        int hpRegenAmount = 0;
        for (L1DollInstance doll : getDollList()) {
            if (doll.isHpRegeneration()) {
                isExistHprDoll = true;
                if (hpRegenAmount < doll.getHpRegenAmount()) {
                    hpRegenAmount = doll.getHpRegenAmount();
                }
            }
        }
        if (!_hpRegenActiveByDoll && isExistHprDoll) {
            _hpRegenByDoll = new HpRegenerationByDoll(this, hpRegenAmount, INTERVAL_BY_DOLL);
            GeneralThreadPool.getInstance().schedule(_hpRegenByDoll, INTERVAL_BY_DOLL);
            _hpRegenActiveByDoll = true;
        }
    }

    public void startMpRegenerationByDoll() {
        final int INTERVAL_BY_DOLL = 64000;
        boolean isExistMprDoll = false;
        int mpRegenAmount = 0;
        for (L1DollInstance doll : getDollList()) {
            if (doll.isMpRegeneration()) {
                isExistMprDoll = true;
                if (mpRegenAmount < doll.getMpRegenAmount()) {
                    mpRegenAmount = doll.getMpRegenAmount();
                }
            }
        }
        if (!_mpRegenActiveByDoll && isExistMprDoll) {
            _mpRegenByDoll = new MpRegenerationByDoll(this, mpRegenAmount, INTERVAL_BY_DOLL);
            GeneralThreadPool.getInstance().schedule(_mpRegenByDoll, INTERVAL_BY_DOLL);
            _mpRegenActiveByDoll = true;
        }
    }

    public void startAHRegeneration() {
        final int INTERVAL = 600000;
        if (!_AHRegenActive) {
            _AHRegen = new AHRegeneration(this, INTERVAL);
            GeneralThreadPool.getInstance().schedule(_AHRegen, INTERVAL);
            _AHRegenActive = true;
        }
    }

    public void startSHRegeneration() {
        final int INTERVAL = 1800000;
        if (!_SHRegenActive) {
            _SHRegen = new SHRegeneration(this, INTERVAL);
            GeneralThreadPool.getInstance().schedule(_SHRegen, INTERVAL);
            _SHRegenActive = true;
        }
    }

    public void startHalloweenRegeneration() {
        final int INTERVAL = 900000;
        if (!_HalloweenRegenActive) {
            _HalloweenRegen = new HalloweenRegeneration(this, INTERVAL);
            GeneralThreadPool.getInstance().schedule(_HalloweenRegen, INTERVAL);
            _HalloweenRegenActive = true;
        }
    }

    public void stopPapuBlessing() { // 波フーガ号
        if (_PapuBlessingActive) {
            _PapuRegen.cancel();
            _PapuRegen = null;
            _PapuBlessingActive = false;
        }
    }

    public void stopHpRegenerationByDoll() {
        if (_hpRegenActiveByDoll) {
            _hpRegenByDoll.cancel();
            _hpRegenByDoll = null;
            _hpRegenActiveByDoll = false;
        }
    }

    public void startMpDecreaseByScales() {
        final int INTERVAL_BY_SCALES = 4000;
        _mpDecreaseByScales = new MpDecreaseByScales(this, INTERVAL_BY_SCALES);
        GeneralThreadPool.getInstance().schedule(_mpDecreaseByScales, INTERVAL_BY_SCALES);
        _mpDecreaseActiveByScales = true;
    }

    public void startHelpBySupport() {
        final int INTERVAL_BY_SUPPORT = 3000;
        _HelpBySupport = new HelpBySupport(this, INTERVAL_BY_SUPPORT);
        GeneralThreadPool.getInstance().schedule(_HelpBySupport, INTERVAL_BY_SUPPORT);
        _HelpActiveBySupport = true;
    }

    public void stopMpRegenerationByDoll() {
        if (_mpRegenActiveByDoll) {
            _mpRegenByDoll.cancel();
            _mpRegenByDoll = null;
            _mpRegenActiveByDoll = false;
        }
    }

    public void stopMpDecreaseByScales() {
        if (_mpDecreaseActiveByScales) {
            _mpDecreaseByScales.cancel();
            _mpDecreaseByScales = null;
            _mpDecreaseActiveByScales = false;
        }
    }

    public void stopHelpBySupport() {
        if (_HelpActiveBySupport) {
            _HelpBySupport.cancel();
            _HelpBySupport = null;
            _HelpActiveBySupport = false;
        }
    }

    public void stopAHRegeneration() {
        if (_AHRegenActive) {
            _AHRegen.cancel();
            _AHRegen = null;
            _AHRegenActive = false;
        }
    }

    public void stopSHRegeneration() {
        if (_SHRegenActive) {
            _SHRegen.cancel();
            _SHRegen = null;
            _SHRegenActive = false;
        }
    }

    public void stopHalloweenRegeneration() {
        if (_HalloweenRegenActive) {
            _HalloweenRegen.cancel();
            _HalloweenRegen = null;
            _HalloweenRegenActive = false;
        }
    }

    public void startObjectAutoUpdate() {
        final long INTERVAL_AUTO_UPDATE = 300;
        removeAllKnownObjects();
        _autoUpdateFuture = GeneralThreadPool.getInstance().scheduleAtFixedRate(new L1PcAutoUpdate(getId()), 0L,
                INTERVAL_AUTO_UPDATE);
    }

    public void stopEtcMonitor() {
        if (_autoUpdateFuture != null) {
            _autoUpdateFuture.cancel(true);
            _autoUpdateFuture = null;
        }
        if (_expMonitorFuture != null) {
            _expMonitorFuture.cancel(true);
            _expMonitorFuture = null;
        }
        if (_ghostFuture != null) {
            _ghostFuture.cancel(true);
            _ghostFuture = null;
        }

        if (_hellFuture != null) {
            _hellFuture.cancel(true);
            _hellFuture = null;
        }

    }

    public void stopEquipmentTimer() {
        List<L1ItemInstance> allItems = this.getInventory().getItems();
        for (L1ItemInstance item : allItems) {
            if (item == null)
                continue;
            if (item.isEquipped() && item.getRemainingTime() > 0) {
                item.stopEquipmentTimer(this);
            }
        }
    }

    public void onChangeExp() {
        int level = ExpTable.getLevelByExp(getExp());
        int char_level = getLevel();
        int gap = level - char_level;
        if (gap == 0) {
            sendPackets(new S_OwnCharStatus(this));
            // sendPackets(new S_Exp(this));
            int percent = ExpTable.getExpPercentage(char_level, getExp());
            if (char_level >= 60 && char_level <= 64) {
                if (percent >= 10)
                    removeSkillEffect(L1SkillId.LEVEL_UP_BONUS);
            } else if (char_level >= 65) {
                if (percent >= 5) {
                    removeSkillEffect(L1SkillId.LEVEL_UP_BONUS);
                }
            }
            return;
        }

        if (gap > 0) {
            levelUp(gap);
            if (getLevel() >= 60) {
                setSkillEffect(L1SkillId.LEVEL_UP_BONUS, 10800000);
                sendPackets(new S_PacketBox(10800, true, true), true);
            }
        } else if (gap < 0) {
            levelDown(gap);
            removeSkillEffect(L1SkillId.LEVEL_UP_BONUS);
        }
    }

    @Override
    public void onPerceive(L1PcInstance pc) {
        if (isGmInvis() /* || isGhost() || isInvisble() */) {
            return;
        }

        pc.addKnownObject(this);
        pc.sendPackets(new S_OtherCharPacks(this, pc), true);
        if (isPinkName()) {
            pc.sendPackets(new S_PinkName(pc.getId(), pc.getPinkNameTime()));
        }

        for (L1PcInstance target : L1World.getInstance().getVisiblePlayer(pc)) {
            if (target.isPinkName()) {
                pc.sendPackets(new S_PinkName(target.getId(), target.getPinkNameTime()));
            }
        }

        if (isInParty() && getParty().isMember(pc)) {
            pc.sendPackets(new S_HPMeter(this));
        }

        if (isPrivateShop()) {
            pc.sendPackets(new S_DoActionShop(getId(), ActionCodes.ACTION_Shop, getShopChat()));
        }
        if (isFishing()) {
            pc.sendPackets(new S_Fishing(getId(), ActionCodes.ACTION_Fishing, _fishingX, _fishingY));
        }

        if (isCrown()) {
            L1Clan clan = L1World.getInstance().getClan(getClanname());
            if (clan != null) {
                if (getId() == clan.getLeaderId() && clan.getCastleId() != 0) {
                    pc.sendPackets(new S_CastleMaster(clan.getCastleId(), getId()));
                }
            }
        }
    }

    public void broadcastRemoveAllKnownObjects() {
        for (L1Object known : getKnownObjects()) {
            if (known == null) {
                continue;
            }

            sendPackets(new S_RemoveObject(known));
        }
    }

    public void updateObject() {
        try {
            List<L1Object> _Alist = null;
            _Alist = getKnownObjects();
            for (L1Object known : _Alist) {
                if (known == null)
                    continue;
                if (known.getMapId() == 631 && known.getMapId() == 514 && known.getMapId() == 515
                        && known.getMapId() == 516 && !isGm()) {
                    if (known instanceof L1PcInstance) {
                        continue;
                    }
                    /** パッケージ店 **/
                    if (known instanceof L1NpcInstance) {
                        L1NpcInstance npc = (L1NpcInstance) known;
                        if (getMapId() == 631 && getCashStep() == 0 && !isGm()) {
                            continue;
                        } else if (getMapId() == 631 || getMapId() == 514 && getCashStep() == 1 && !isGm()) {
                            if (!(npc.getNpcTemplate().get_npcId() >= 6100000
                                    && npc.getNpcTemplate().get_npcId() <= 6100013
                                    || npc.getNpcTemplate().get_npcId() == 4200022)) {
                                continue;
                            }
                        } else if (getMapId() == 631 || getMapId() == 515 && getCashStep() == 2 && !isGm()) {
                            if (!(npc.getNpcTemplate().get_npcId() >= 6100014
                                    && npc.getNpcTemplate().get_npcId() <= 6100027
                                    || npc.getNpcTemplate().get_npcId() == 4200022)) {
                                continue;
                            }
                        } else if (getMapId() == 631 || getMapId() == 516 && getCashStep() == 3 && !isGm()) {
                            if (!(npc.getNpcTemplate().get_npcId() >= 6100028
                                    && npc.getNpcTemplate().get_npcId() <= 6100041
                                    || npc.getNpcTemplate().get_npcId() == 4200022)) {
                                continue;
                            }
                        }
                    }
                    /** パッケージ店 **/
                }

                if (Config.PC_RECOGNIZE_RANGE == -1) {
                    if (!getLocation().isInScreen(known.getLocation())) {
                        removeKnownObject(known);
                        sendPackets(new S_RemoveObject(known));
                    }
                } else {
                    if (getLocation().getTileLineDistance(known.getLocation()) > Config.PC_RECOGNIZE_RANGE) {
                        removeKnownObject(known);
                        sendPackets(new S_RemoveObject(known));
                    }
                }
            }

            ArrayList<L1Object> _Vlist = null;
            _Vlist = L1World.getInstance().getVisibleObjects(this, Config.PC_RECOGNIZE_RANGE);
            for (L1Object visible : _Vlist) {
                if (visible == null)
                    continue;
                if (visible instanceof L1NpcInstance) {
                    L1NpcInstance npc = (L1NpcInstance) visible;
                    /** パッケージ店 **/
                    if (getMapId() == 631 && getCashStep() == 0 && !isGm()) {
                        continue;
                    } else if (getMapId() == 631 || getMapId() == 514 && getCashStep() == 1 && !isGm()) {
                        if (!(npc.getNpcTemplate().get_npcId() >= 6100000 && npc.getNpcTemplate().get_npcId() <= 6100013
                                || npc.getNpcTemplate().get_npcId() == 4200022)) {
                            continue;
                        }
                    } else if (getMapId() == 631 || getMapId() == 515 && getCashStep() == 2 && !isGm()) {
                        if (!(npc.getNpcTemplate().get_npcId() >= 6100014 && npc.getNpcTemplate().get_npcId() <= 6100027
                                || npc.getNpcTemplate().get_npcId() == 4200022)) {
                            continue;
                        }
                    } else if (getMapId() == 631 || getMapId() == 516 && getCashStep() == 3 && !isGm()) {
                        if (!(npc.getNpcTemplate().get_npcId() >= 6100028 && npc.getNpcTemplate().get_npcId() <= 6100041
                                || npc.getNpcTemplate().get_npcId() == 4200022)) {
                            continue;
                        }
                    }
                }
                /** パッケージ店 **/
                try {

                    if (!knownsObject(visible)) {
                        if (visible.getMapId() == 631 && visible.getMapId() == 514 && visible.getMapId() == 515
                                && visible.getMapId() == 516 && !isGm()) {
                            if (visible instanceof L1PcInstance) {
                                continue;
                            }
                        }
                        visible.onPerceive(this);
                    } else {
                        if (visible instanceof L1NpcInstance) {
                            L1NpcInstance npc = (L1NpcInstance) visible;
                            if (getLocation().isInScreen(npc.getLocation()) && npc.getHiddenStatus() != 0) {
                                npc.approachPlayer(this);
                            }
                        }
                        /** パッケージ店 **/
                        if (visible instanceof L1NpcCashShopInstance) {
                            L1NpcInstance npc = (L1NpcInstance) visible;
                            if (getMapId() == 631 && getCashStep() == 0 && !isGm()) {
                                continue;
                            } else if (getMapId() == 631 || getMapId() == 514 && getCashStep() == 1 && !isGm()) {
                                if (!(npc.getNpcTemplate().get_npcId() >= 6100000
                                        && npc.getNpcTemplate().get_npcId() <= 6100013
                                        || npc.getNpcTemplate().get_npcId() == 4200022)) {
                                    continue;
                                }
                            } else if (getMapId() == 631 || getMapId() == 515 && getCashStep() == 2 && !isGm()) {
                                if (!(npc.getNpcTemplate().get_npcId() >= 6100014
                                        && npc.getNpcTemplate().get_npcId() <= 6100027
                                        || npc.getNpcTemplate().get_npcId() == 4200022)) {
                                    continue;
                                }
                            } else if (getMapId() == 631 || getMapId() == 516 && getCashStep() == 3 && !isGm()) {
                                if (!(npc.getNpcTemplate().get_npcId() >= 6100028
                                        && npc.getNpcTemplate().get_npcId() <= 6100041
                                        || npc.getNpcTemplate().get_npcId() == 4200022)) {
                                    continue;
                                }
                            } else if (getMapId() == 514 && getCashStep() == 4 && !isGm()) {
                                if (!(npc.getNpcTemplate().get_npcId() >= 6100000
                                        && npc.getNpcTemplate().get_npcId() <= 6100011
                                        || npc.getNpcTemplate().get_npcId() == 4200022)) {
                                    continue;
                                }
                            }
                        }
                        /** パッケージ店 **/
                    }
                    if (hasSkillEffect(L1SkillId.GMSTATUS_HPBAR) && L1HpBar.isHpBarTarget(visible)) {
                        sendPackets(new S_HPMeter((L1Character) visible));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendVisualEffect() {
        int poisonId = 0;
        if (getPoison() != null) {
            poisonId = getPoison().getEffectId();
        }
        if (getParalysis() != null) {
            poisonId = getParalysis().getEffectId();
        }
        if (poisonId != 0) {
            sendPackets(new S_Poison(getId(), poisonId));
            broadcastPacket(new S_Poison(getId(), poisonId));
        }
    }

    public void sendClanMarks() {
        // for (L1Clan clan : L1World.getInstance().getAllClans()) {
        // if (clan.getEmblem() != null) {
        // sendPackets(new S_Emblem(clan.getClanId()));
        // }
        // }

        if (getClanid() != 0) {
            L1Clan clan = L1World.getInstance().getClan(getClanname());
            if (clan != null) {
                if (isCrown() && getId() == clan.getLeaderId() && clan.getCastleId() != 0) {
                    sendPackets(new S_CastleMaster(clan.getCastleId(), getId()));
                }
            }
        }
    }

    public void sendVisualEffectAtLogin() {
        sendVisualEffect();
    }

    public void sendVisualEffectAtTeleport() {
        if (isDrink()) {
            // sendPackets(new S_Liquor(getId()));
        }

        sendVisualEffect();
    }

    @Override
    public void setCurrentHp(int i) {
        if (getCurrentHp() == i)
            return;
        super.setCurrentHp(i);
        sendPackets(new S_HPUpdate(getCurrentHp(), getMaxHp()));
        if (isInParty()) {
            getParty().updateMiniHP(this);
        }
        /** バトルゾーン **/
        if (getMapId() == 5153 && get_DuelLine() != 0) {
            for (L1PcInstance member : BattleZone.getInstance().toArrayBattleZoneUser()) {
                if (member != null) {
                    if (get_DuelLine() == member.get_DuelLine()) {
                        member.sendPackets(new S_HPMeter(this));
                    }
                }
            }
        }
    }

    @Override
    public void setCurrentMp(int i) {
        if (getCurrentMp() == i)
            return;
        if (isGm())
            i = getMaxMp();
        super.setCurrentMp(i);
        sendPackets(new S_MPUpdate(getCurrentMp(), getMaxMp()));
        if (isInParty()) {
            getParty().updateMiniHP(this);
        }
        /** バトルゾーン **/
        if (getMapId() == 5153 && get_DuelLine() != 0) {
            for (L1PcInstance member : BattleZone.getInstance().toArrayBattleZoneUser()) {
                if (member != null) {
                    if (get_DuelLine() == member.get_DuelLine()) {
                        member.sendPackets(new S_HPMeter(this));
                    }
                }
            }
        }
    }

    @Override
    public L1PcInventory getInventory() {
        return _inventory;
    }

    // public L1DwarfForPackageInventory getDwarfForPackageInventory() {
    // return _dwarfForPackage;
    // }

    public L1Inventory getTradeWindowInventory() {
        return _tradewindow;
    }

    public boolean isGmInvis() {
        return _gmInvis;
    }

    public void setGmInvis(boolean flag) {
        _gmInvis = flag;
    }

    public int CubeMr;

    public int getCurrentWeapon() {
        return _currentWeapon;
    }

    public void setCurrentWeapon(int i) {
        _currentWeapon = i;
    }

    private Ability pc;

    public int getType() {
        return _type;
    }

    public void setType(int i) {
        _type = i;
    }

    public short getAccessLevel() {
        return _accessLevel;
    }

    public void setAccessLevel(short i) {
        _accessLevel = i;
    }

    public int getClassId() {
        return _classId;
    }

    public void setClassId(int i) {
        _classId = i;
        _classFeature = L1ClassFeature.newClassFeature(i);
    }

    public L1ClassFeature getClassFeature() {
        return _classFeature;
    }

    @Override
    public int getExp() {
        return _exp;
    }

    @Override
    public void setExp(int i) {
        _exp = i;
    }

    public synchronized int getReturnStat() {
        return _returnstat;
    }

    public synchronized void setReturnStat(int i) {
        _returnstat = i;
    }

    private L1PcInstance getStat() {
        return null;
    }

    public void reduceCurrentHp(double d, L1Character l1character) {
        getStat().reduceCurrentHp(d, l1character);
    }

    private void notifyPlayersLogout(List<L1PcInstance> playersArray) {
        for (L1PcInstance player : playersArray) {
            if (player == null)
                continue;
            if (player.knownsObject(this)) {
                player.removeKnownObject(this);
                player.sendPackets(new S_RemoveObject(this));
            }
        }
    }

    private void quitGame() {
        try {
            if (!(noPlayerCK || noPlayerck2 || isPrivateShop() || pc != null)) {
                // manager.LogServerAppend("終了", this,
                // getNetConnection().getIp(), -1);
                LinAllManager.getInstance().LogLogOutAppend(getName(), getNetConnection().getHostname());
                /** ログファイルの保存 **/
                LoggerInstance.getInstance().addConnection(
                        "終了キャラ=" + getName() + "アカウント=" + getAccountName() + " IP=" + getNetConnection().getHostname());
            }
        } catch (Exception e) {
        }

        try {
            if (L1HauntedHouse.getInstance().isMember(this)) {
                if (getMapId() == 5140) {
                    L1HauntedHouse.getInstance().clearBuff(this);
                }

                L1HauntedHouse.getInstance().removeMember(this);
            }
        } catch (Exception e) {
            System.out.println("キャラクター : " + getName() + " Error Code = 1000");
        }
        try {
            if (L1Racing.getInstance().isMember(this)) {
                if (getMapId() == 5143) {
                    L1HauntedHouse.getInstance().clearBuff(this);
                }

                L1Racing.getInstance().removeMember(this);
            }
        } catch (Exception e) {
            System.out.println("キャラクター : " + getName() + " Error Code = 1001");
        }
        try {
            getMap().setPassable(getLocation(), true);
            // 死亡しているとの距離に戻し、空腹状態にする
            if (isDead()) {
                int[] loc = Getback.GetBack_Location(this, true);
                setX(loc[0]);
                setY(loc[1]);
                setMap((short) loc[2]);
                setCurrentHp(getLevel());
                set_food(39); // 10%
            } else if (getMapId() == (short) 10502) {
                if (isSiege) {
                    if (MiniSiege.getInstance().running) {
                        switch (getTeam()) {
                        case 0:
                            setX(32771);
                            setY(32815);
                            setMap((short) 10502);
                            break;

                        case 1:
                            setX(32691);
                            setY(32895);
                            setMap((short) 10502);
                            break;
                        case 2:
                            setX(32771);
                            setY(32975);
                            setMap((short) 10502);
                            break;

                        }
                    } else {
                        isSiege = false;
                        setX(33437);
                        setY(32810);
                        setMap((short) 4);
                    }
                } else {
                    setX(33437);
                    setY(32810);
                    setMap((short) 4);

                }
            }
        } catch (Exception e) {
            System.out.println("キャラクター：" + getName() + " Error Code = 1002");
        }
        // トレードを停止する
        try {
            if (getTradeID() != 0) { // トレード中
                L1Trade trade = new L1Trade();
                trade.TradeCancel(this);
            }
        } catch (Exception e) {
            System.out.println("キャラクター：" + getName() + " Error Code = 1003");
        }
        // 決闘中
        try {
            if (getFightId() != 0) {
                L1PcInstance fightPc = (L1PcInstance) L1World.getInstance().findObject(getFightId());
                if (fightPc != null) {
                    fightPc.setFightId(0);
                    fightPc.sendPackets(new S_PacketBox(S_PacketBox.MSG_DUEL, 0, 0));
                }
                setFightId(0);
            }
        } catch (Exception e) {
            System.out.println("キャラクター：" + getName() + " Error Code = 1004");
        }
        // パーティーを抜ける
        try {
            if (isInParty()) { // パーティー中
                getParty().leaveMember(this);
            }
        } catch (Exception e) {
            System.out.println("キャラクター：" + getName() + " Error Code = 1005");
        }
        // チャットパーティーを抜ける
        try {
            if (isInChatParty()) { // チャットパーティー中
                getChatParty().leaveMember(this);
            }
        } catch (Exception e) {
            System.out.println("キャラクター：" + getName() + " Error Code = 1006");
        }
        try {
            if (isFishing()) {
                try {
                    setFishing(false);
                    setFishingTime(0);
                    setFishingReady(false);
                    sendPackets(new S_CharVisualUpdate(this));
                    Broadcaster.broadcastPacket(this, new S_CharVisualUpdate(this));
                    FishingTimeController.getInstance().removeMember(this);
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

        try {
            if (L1DeathMatch.getInstance().isMember(this)) {
                L1DeathMatch.getInstance().removeMember(this);
            }
            if (L1HauntedHouse.getInstance().isMember(this)) {
                L1HauntedHouse.getInstance().removeMember(this);
            }
        } catch (Exception e) {
            System.out.println("キャラクター：" + getName() + " Error Code = 1007");
        }
        // ペットをワールドMAP上から消す
        try {
            Object[] petList = getPetList().values().toArray();
            L1PetInstance pet = null;
            // L1SummonInstance summon = null;
            for (Object petObject : petList) {
                if (petObject instanceof L1PetInstance) {
                    pet = (L1PetInstance) petObject;
                    pet.getMap().setPassable(pet.getLocation(), true);
                    pet.dropItem();
                    getPetList().remove(pet.getId());
                    pet.deleteMe();
                }
                // サモン
                if (petObject instanceof L1SummonInstance) {
                    L1SummonInstance summon = (L1SummonInstance) petObject;
                    for (L1PcInstance visiblePc : L1World.getInstance().getVisiblePlayer(summon)) {
                        visiblePc.sendPackets(new S_SummonPack(summon, visiblePc, false));
                        // summon.deleteMe();
                        summon.deleteMe2();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("キャラクター：" + getName() + " Error Code = 1008");
        }
        // マジックドールをワールドマップ上から消す
        try {
            if (getDollList() != null && getDollListSize() > 0) {
                for (L1DollInstance doll : getDollList()) {
                    if (doll != null)
                        doll.deleteDoll();
                }
            }
        } catch (Exception e) {
            System.out.println("キャラクター：" + getName() + " Error Code = 1009");
        }
        try {
            Object[] supportList = getSupportList().values().toArray();
            L1SupportInstance support = null;
            for (Object supportObject : supportList) {
                if (supportObject == null)
                    continue;
                support = (L1SupportInstance) supportObject;
                support.deleteSupport();
            }
        } catch (Exception e) {
            System.out.println("キャラクター：" + getName() + " Error Code = 1010");
        }
        try {
            Object[] followerList = getFollowerList().values().toArray();
            L1FollowerInstance follower = null;
            for (Object followerObject : followerList) {
                if (followerObject == null)
                    continue;
                follower = (L1FollowerInstance) followerObject;
                follower.setParalyzed(true);
                follower.spawn(follower.getNpcTemplate().get_npcId(), follower.getX(), follower.getY(),
                        follower.getHeading(), follower.getMapId());
                follower.deleteMe();
            }
        } catch (Exception e) {
            System.out.println("キャラクター：" + getName() + " Error Code = 1011");
        }
        // エンチャントをDBのcharacter_buffに保存する
        try {
            MonsterBookTable.getInstace().saveMonsterBookList(this.getId());
            CharBuffTable.DeleteBuff(this);
            CharBuffTable.SaveBuff(this);
            clearSkillEffectTimer();
            SkillCheck.getInstance().QuitDelSkill(this);//
        } catch (Exception e) {
            System.out.println("キャラクター：" + getName() + " Error Code = 1012");
        }
        try {
            for (L1ItemInstance item : getInventory().getItems()) {
                if (item == null)
                    continue;
                if (item.getCount() <= 0) {
                    getInventory().deleteItem(item);
                }
            }
        } catch (Exception e) {
            System.out.println("キャラクター：" + getName() + " Error Code = 1013");
        }

        // pcのモニターをstopする。
        stopEtcMonitor();
        // オンライン状態をOFFにし、DBに文字情報を記入する
        setOnlineStatus(0);
        try {
            save();
            saveInventory();
            L1BookMark.WriteBookmark(this);
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
    }

    private boolean _destroyed = false;

    public void removeShopItemPurchase(int objid, int itemid, int type2) {// アイテム名別販売購入
        // 区分し、削除
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("DELETE FROM character_shop WHERE obj_id=? AND item_id=? AND type=?");
            pstm.setInt(1, objid);
            pstm.setInt(2, itemid);
            pstm.setInt(3, type2);
            pstm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void deleteStoreItem(int objid, int itemid, int type2) {// アイテム名別販売購入区分した後
        // 削除
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("DELETE FROM character_shop WHERE obj_id=? AND item_objid=? AND type=?");
            pstm.setInt(1, objid);
            pstm.setInt(2, itemid);
            pstm.setInt(3, type2);
            pstm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void updateShopItemPurchase(int objid, int itemid, int type2, int count1) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("UPDATE character_shop SET count=? WHERE obj_id=? AND item_id=? AND type=?");
            pstm.setInt(1, count1);
            pstm.setInt(2, objid);
            pstm.setInt(3, itemid);
            pstm.setInt(4, type2);
            pstm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void updatestoreItem(int objid, int itemid, int type2, int count1) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("UPDATE character_shop SET count=? WHERE obj_id=? AND item_objid=? AND type=?");
            pstm.setInt(1, count1);
            pstm.setInt(2, objid);
            pstm.setInt(3, itemid);
            pstm.setInt(4, type2);
            pstm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void deleteStoreItem(int objid) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("DELETE FROM character_shop WHERE obj_id=?");
            pstm.setInt(1, objid);
            pstm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void SaveShop(L1PcInstance pc, L1ItemInstance item, int price, int sellcount, int type) {
        Connection con = null;
        int bless = item.getBless();
        int attr = item.getAttrEnchantLevel();
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();

            pstm = con.prepareStatement(
                    "INSERT INTO character_shop SET obj_id=?, char_name=?, item_objid=?, item_id=?, Item_name=?, count=?, enchant=?, price=?, type=?, locx=?, locy=?, locm=?, iden=?, attr=?");

            pstm.setInt(1, pc.getId());
            pstm.setString(2, pc.getName());
            pstm.setInt(3, item.getId());
            pstm.setInt(4, item.getItemId());
            pstm.setString(5, item.getItem().getName());
            pstm.setInt(6, sellcount);
            pstm.setInt(7, item.getEnchantLevel());
            pstm.setInt(8, price);
            pstm.setInt(9, type);
            pstm.setInt(10, pc.getX());
            pstm.setInt(11, pc.getY());
            pstm.setInt(12, pc.getMapId());

            if (!item.isIdentified())
                pstm.setInt(13, 0);
            else {
                switch (bless) {
                case 0:
                    pstm.setInt(13, 2);
                    break;
                case 1:
                    pstm.setInt(13, 1);
                    break;
                case 2:
                    pstm.setInt(13, 3);
                }
            }

            pstm.setInt(14, attr);

            pstm.executeUpdate();
        } catch (SQLException localSQLException) {
        } catch (Exception localException) {
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void logout() {
        try {
            synchronized (this) {
                if (_destroyed) {
                    return;
                }
                quitGame();
                L1World world = L1World.getInstance();
                notifyPlayersLogout(getKnownPlayers());
                world.removeVisibleObject(this);
                world.removeObject(this);
                notifyPlayersLogout(world.getRecognizePlayer(this));
                _inventory.clearItems();
                MonsterBookTable.getInstace().saveMonsterBookList(getId());
                WarehouseManager w = WarehouseManager.getInstance();
                w.delPrivateWarehouse(this.getAccountName());
                w.delElfWarehouse(this.getAccountName());
                // w.delPresentWarehouse(this.getAccountName());
                w.delSpecialWarehouse(this.getName()); // 特殊倉庫
                w.delPackageWarehouse(this.getAccountName());
                // _dwarfForPackage.clearItems();
                removeAllKnownObjects();
                stopHalloweenRegeneration();
                stopAHRegeneration();
                stopHelpBySupport();
                stopHpRegenerationByDoll();
                stopMpRegenerationByDoll();
                stopSHRegeneration();
                stopMpDecreaseByScales();
                stopEquipmentTimer();
                setDead(true);
                setNetConnection(null);
                stopEinhasadTimer();
                stopPapuBlessing();// パプリオンブレッシング
                addAc(1);
                _destroyed = true;

                L1AccountAttendance acc = AttendanceController.findacc(getAccountName());
                if (acc != null)
                    AttendanceController.accsetPc(null, getAccountName(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GameClient getNetConnection() {
        return _netConnection;
    }

    public void setNetConnection(GameClient clientthread) {
        _netConnection = clientthread;
    }

    public boolean isInParty() {
        return getParty() != null;
    }

    public L1Party getParty() {
        return _party;
    }

    public void setParty(L1Party p) {
        _party = p;
    }

    public boolean isInChatParty() {
        return getChatParty() != null;
    }

    public L1ChatParty getChatParty() {
        return _chatParty;
    }

    public void setChatParty(L1ChatParty cp) {
        _chatParty = cp;
    }

    public int getPartyID() {
        return _partyID;
    }

    public void setPartyID(int partyID) {
        _partyID = partyID;
    }

    public int getPartyType() {
        return _partyType;
    }

    public void setPartyType(int partyType) {
        _partyType = partyType;
    }

    public int getTradeID() {
        return _tradeID;
    }

    public void setTradeID(int tradeID) {
        _tradeID = tradeID;
    }

    public void setTradeOk(boolean tradeOk) {
        _tradeOk = tradeOk;
    }

    public boolean getTradeOk() {
        return _tradeOk;
    }

    public int getTempID() {
        return _tempID;
    }

    public void setTempID(int tempID) {
        _tempID = tempID;
    }

    public boolean isTeleport() {
        return _isTeleport;
    }

    public void setTeleport(boolean flag) {
        _isTeleport = flag;
        //
        if (flag)
            AttackController.stop(this);
    }

    public boolean getTelWait() {
        return _TelWait;
    }

    public void setTelWait(boolean flag) {
        _TelWait = flag;
    }

    public boolean isDrink() {
        return _isDrink;
    }

    public void setDrink(boolean flag) {
        _isDrink = flag;
    }

    public boolean isGres() {
        return _isGres;
    }

    public void setGres(boolean flag) {
        _isGres = flag;
    }

    public boolean isPinkName() {
        return _isPinkName;
    }

    public void setPinkName(boolean flag) {
        _isPinkName = flag;
    }

    public ArrayList<L1PrivateShopSellList> getSellList() {
        return _sellList;
    }

    public ArrayList<L1PrivateShopBuyList> getBuyList() {
        return _buyList;
    }

    public void setShopChat(byte[] chat) {
        _shopChat = chat;
    }

    public byte[] getShopChat() {
        return _shopChat;
    }

    public boolean isPrivateShop() {
        return _isPrivateShop;
    }

    public void setPrivateShop(boolean flag) {
        _isPrivateShop = flag;
    }

    public boolean isAutoClanjoin() {
        return _isAutoClanjoin;
    }

    public void setAutoClanjoin(boolean flag) {
        _isAutoClanjoin = flag;
    }

    private int _special_size;

    public int get_SpecialSize() {
        return _special_size;
    }

    public void set_SpecialSize(int special_size) {
        _special_size = special_size;
    }

    public int getPartnersPrivateShopItemCount() {
        return _partnersPrivateShopItemCount;
    }

    public void setPartnersPrivateShopItemCount(int i) {
        _partnersPrivateShopItemCount = i;
    }

    private int birthday;// 誕生日

    public int getBirthDay() {
        return birthday;
    }

    public void setBirthDay(int t) {
        birthday = t;
    }

    private int _TelType = 0;

    public int getTelType() {
        return _TelType;
    }

    public void setTelType(int i) {
        _TelType = i;
    }

    private int AinState = 0;

    public int getAinState() {
        return AinState;
    }

    public void setAinState(int AinState) {
        this.AinState = AinState;
    }

    public boolean SurvivalState; // 生存の叫び状態
    private int SurvivalGauge; // 生存の叫びゲージ

    public int getSurvivalGauge() {
        return SurvivalGauge;
    }

    public void setSurvivalGauge(int SurvivalGauge) {
        this.SurvivalGauge = SurvivalGauge;
    }

    public int[] DragonPortalLoc = new int[3];// ドラゴンポータル

    public void sendPackets(ServerBasePacket serverbasepacket, boolean clear) {
        try {
            if ((getMapId() == 2699 || getMapId() == 2100)
                    && serverbasepacket.getType().equalsIgnoreCase("[S] S_OtherCharPacks")) {
            } else
                sendPackets(serverbasepacket);
            if (clear) {
                serverbasepacket.clear();
                serverbasepacket = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendPackets(ServerBasePacket serverbasepacket) {
        if (this instanceof L1RobotInstance) {
            if (serverbasepacket.getType().equalsIgnoreCase("[S] S_Paralysis")) {
                if (serverbasepacket.getBytes()[1] == 2 || serverbasepacket.getBytes()[1] == 4
                        || serverbasepacket.getBytes()[1] == 10 || serverbasepacket.getBytes()[1] == 12
                        || serverbasepacket.getBytes()[1] == 22 || serverbasepacket.getBytes()[1] == 24) {
                    this.setParalyzed(true);
                }
                if (serverbasepacket.getBytes()[1] == 3 || serverbasepacket.getBytes()[1] == 5
                        || serverbasepacket.getBytes()[1] == 11 || serverbasepacket.getBytes()[1] == 13
                        || serverbasepacket.getBytes()[1] == 23 || serverbasepacket.getBytes()[1] == 25) {
                    this.setParalyzed(false);
                }
            }
            if (serverbasepacket.getType().equalsIgnoreCase("[S] S_Paralysis")) {
                if (serverbasepacket.getBytes()[1] == 2 || serverbasepacket.getBytes()[1] == 4
                        || serverbasepacket.getBytes()[1] == 10 || serverbasepacket.getBytes()[1] == 12
                        || serverbasepacket.getBytes()[1] == 22 || serverbasepacket.getBytes()[1] == 24) {
                    this.setParalyzed(true);
                }
                if (serverbasepacket.getBytes()[1] == 3 || serverbasepacket.getBytes()[1] == 5
                        || serverbasepacket.getBytes()[1] == 11 || serverbasepacket.getBytes()[1] == 13
                        || serverbasepacket.getBytes()[1] == 23 || serverbasepacket.getBytes()[1] == 25) {
                    this.setParalyzed(false);
                }
            }
            return;
        }
        if (getNetConnection() == null) {
            return;
        }

        try {
            getNetConnection().sendPacket(serverbasepacket);
        } catch (Exception e) {
        }
    }

    public boolean test = false;

    public void onAction(L1NpcInstance mon) {
        if (mon == null)
            return;
        if (getCurrentHp() > 0 && !isDead()) {
            L1Attack attack = new L1Attack(mon, this);
            if (attack.calcHit()) {
                attack.calcDamage();
                attack.addPcPoisonAttack(mon, this);
            }
            attack.action();
            attack.commit();
        }
    }

    @Override
    public void onAction(L1PcInstance attacker) {
        if (attacker == null) {
            return;
        }
        if (TRIPLE) {
            if (25 > random2.nextInt(100) + 1) { // 40％の確率でミス
                L1Attack attack_mortion = new L1Attack(attacker, this);
                attack_mortion._isHit = false;
                attack_mortion.action();
                TRIPLE = false;
                return;
            }
            TRIPLE = false;
        } else if (FouSlayer) {
            if (25 > random2.nextInt(100) + 1) { // 30％の確率でミス
                L1Attack attack_mortion = new L1Attack(attacker, this);
                attack_mortion._isHit = false;
                attack_mortion.action();
                FouSlayer = false;
                return;
            }
        }
        /** ロボットシステム */
        if (getRobotAi() != null && (noPlayerCK || isGm())) {
            if (attacker != null && getClanid() != 0 && !getMap().isSafetyZone(getLocation())) {
                getRobotAi().getAttackList().add(attacker, 0);
            } else if (!getMap().isSafetyZone(getLocation())) {
                if (getMap().isTeleportable()) {
                    L1Location newLocation = getLocation().randomLocation(200, true);
                    int newX = newLocation.getX();
                    int newY = newLocation.getY();
                    short mapId = (short) newLocation.getMapId();
                    new L1Teleport().teleport(this, newX, newY, mapId, getHeading(), false);
                }
            }
        }
        /** ロボットシステム */

        if (isTeleport()) {
            return;
        }

        if (getZoneType() == 1 || attacker.getZoneType() == 1) {
            L1Attack attack_mortion = new L1Attack(attacker, this);
            attack_mortion.action();
            return;
        }

        if (checkNonPvP(this, attacker) == true) {
            return;
        }

        if (attacker.isSupporting()) {
            L1SupportInstance support = null;
            Object[] supportList = attacker.getSupportList().values().toArray();
            for (Object supportObject : supportList) {
                support = (L1SupportInstance) supportObject;
            }
            support.deleteSupport();
            attacker.sendPackets(new S_OwnCharStatus(attacker));
            attacker.setSupporting(false);
        }

        if (getCurrentHp() > 0 && !isDead()) {
            attacker.delInvis();

            boolean isCounterBarrier = false;
            boolean isMortalBody = false;
            L1Attack attack = new L1Attack(attacker, this);
            L1Magic magic = null;

            if (attack.calcHit()) {
                if (hasSkillEffect(L1SkillId.COUNTER_BARRIER)) {
                    magic = new L1Magic(this, attacker);
                    boolean isProbability = magic.calcProbabilityMagic(L1SkillId.COUNTER_BARRIER);
                    boolean isShortDistance = attack.isShortDistance();
                    if (isProbability && isShortDistance) {
                        isCounterBarrier = true;
                    }
                } else if (hasSkillEffect(L1SkillId.MORTAL_BODY)) {
                    magic = new L1Magic(this, attacker);
                    boolean isProbability = magic.calcProbabilityMagic(L1SkillId.MORTAL_BODY);
                    boolean isShortDistance = attack.isShortDistance();
                    if (isProbability && isShortDistance) {
                        isMortalBody = true;
                    }
                }
                if (!isCounterBarrier && !isMortalBody) {
                    attacker.setPetTarget(this);
                    attack.calcDamage();
                    attack.calcStaffOfMana();
                    /** ゾウのストーンゴーレム **/
                    attack.calcDrainOfMana();
                    /** ゾウのストーンゴーレム **/
                    attack.addPcPoisonAttack(attacker, this);

                    applySpecialEnchant(attacker);
                }
            }
            if (isCounterBarrier) {
                attack.actionCounterBarrier();
                attack.commitCounterBarrier();
                attack.commit();
                /** 使用者も血がつけれるように追加 **/
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

    private void applySpecialEnchant(L1PcInstance attacker) {

        if (getWeapon() == null || !getWeapon().isSpecialEnchantable()) {
            return;
        }

        for (int i = 1; i <= 3; ++i) {
            int specialEnchant = getWeapon().getSpecialEnchant(i);

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
                new L1SkillUse().handleCommands(this, L1SkillId.COUNTER_MAGIC, getId(), getX(), getY(), null, 0,
                        L1SkillUse.TYPE_GMBUFF);
                break;
            case L1ItemInstance.ANTARAS_SPIRIT:
            case L1ItemInstance.BALLACAS_SPIRIT:
            case L1ItemInstance.LINDBIOR_SPIRIT:
                success = false;
                break;
            case L1ItemInstance.PAPURION_SPIRIT:
                if (attacker.hasSkillEffect(L1SkillId.STATUS_BRAVE) || attacker.hasSkillEffect(L1SkillId.STATUS_HASTE)
                        || attacker.hasSkillEffect(L1SkillId.HOLY_WALK)
                        || attacker.hasSkillEffect(L1SkillId.MOVING_ACCELERATION)
                        || attacker.hasSkillEffect(L1SkillId.WIND_WALK)) {
                    attacker.killSkillEffectTimer(L1SkillId.STATUS_BRAVE);
                    attacker.killSkillEffectTimer(L1SkillId.STATUS_HASTE);
                    attacker.killSkillEffectTimer(L1SkillId.HOLY_WALK);
                    attacker.killSkillEffectTimer(L1SkillId.MOVING_ACCELERATION);
                    attacker.killSkillEffectTimer(L1SkillId.WIND_WALK);
                    attacker.sendPackets(new S_SkillBrave(attacker.getId(), 0, 0));
                    attacker.broadcastPacket(new S_SkillBrave(attacker.getId(), 0, 0));
                    attacker.setBraveSpeed(0);
                    attacker.sendPackets(new S_SkillHaste(attacker.getId(), 0, 0));
                    attacker.broadcastPacket(new S_SkillHaste(attacker.getId(), 0, 0));
                    attacker.setMoveSpeed(0);
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
                break; // 同時に2つ以上は発動しない。
            }
        }
    }

    public boolean checkNonPvP(L1PcInstance pc, L1Character target) {
        L1PcInstance targetpc = null;
        if (target instanceof L1PcInstance) {
            targetpc = (L1PcInstance) target;
        } else if (target instanceof L1PetInstance) {
            targetpc = (L1PcInstance) ((L1PetInstance) target).getMaster();
        } else if (target instanceof L1SummonInstance) {
            targetpc = (L1PcInstance) ((L1SummonInstance) target).getMaster();
        }
        if (targetpc == null) {
            return false;
        }
        if (!Config.ALT_NONPVP) {
            if (getMap().isCombatZone(getLocation())) {
                return false;
            }

            for (L1War war : L1World.getInstance().getWarList()) {
                if (war == null)
                    continue;
                if (pc.getClanid() != 0 && targetpc.getClanid() != 0) {
                    boolean same_war = war.CheckClanInSameWar(pc.getClanname(), targetpc.getClanname());
                    if (same_war == true) {
                        return false;
                    }
                }
            }
            if (target instanceof L1PcInstance) {
                L1PcInstance targetPc = (L1PcInstance) target;
                if (isInWarAreaAndWarTime(pc, targetPc)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private boolean isInWarAreaAndWarTime(L1PcInstance pc, L1PcInstance target) {
        int castleId = L1CastleLocation.getCastleIdByArea(pc);
        int targetCastleId = L1CastleLocation.getCastleIdByArea(target);
        if (castleId != 0 && targetCastleId != 0 && castleId == targetCastleId) {
            if (WarTimeController.getInstance().isNowWar(castleId)) {
                return true;
            }
        }
        return false;
    }

    public void setPetTarget(L1Character target) {
        Object[] petList = getPetList().values().toArray();
        L1PetInstance pets = null;
        L1SummonInstance summon = null;
        for (Object pet : petList) {
            if (pet == null)
                continue;
            if (pet instanceof L1PetInstance) {
                pets = (L1PetInstance) pet;
                pets.setMasterTarget(target);
            } else if (pet instanceof L1SummonInstance) {
                summon = (L1SummonInstance) pet;
                summon.setMasterTarget(target);
            }
        }
    }

    public boolean isstop() {
        return (hasSkillEffect(SHOCK_STUN)) || (hasSkillEffect(ICE_LANCE)) || (hasSkillEffect(BONE_BREAK))
                || (hasSkillEffect(EARTH_BIND)) || (hasSkillEffect(MOB_RANGESTUN_19))
                || (hasSkillEffect(MOB_SHOCKSTUN_30)) || (hasSkillEffect(OMAN_STUN)) || (hasSkillEffect(ANTA_MESSAGE_6))
                || (hasSkillEffect(ANTA_MESSAGE_7)) || (hasSkillEffect(L1SkillId.MOB_COCA))
                || (hasSkillEffect(L1SkillId.MOB_CURSEPARALYZ_18)) || (hasSkillEffect(ANTA_MESSAGE_8))
                || (hasSkillEffect(ANTA_SHOCKSTUN));
    }

    public void delInvis() {
        if (isGmInvis())
            return;
        if (hasSkillEffect(L1SkillId.INVISIBILITY)) {
            killSkillEffectTimer(L1SkillId.INVISIBILITY);
            S_Invis iv = new S_Invis(getId(), 0);
            sendPackets(iv);
            Broadcaster.broadcastPacket(this, iv, true);
            for (L1PcInstance pc2 : L1World.getInstance().getVisiblePlayer(this)) {
                pc2.sendPackets(new S_OtherCharPacks(this, pc2));
            }
            if (isInParty()) {
                for (L1PcInstance tar : L1World.getInstance().getVisiblePlayer(this, -1)) {
                    if (getParty().isMember(tar)) {
                        tar.sendPackets(new S_HPMeter(this));
                    }
                }
            }

        }
        if (hasSkillEffect(L1SkillId.BLIND_HIDING)) {
            killSkillEffectTimer(L1SkillId.BLIND_HIDING);
            // if (hasSkillEffect(L1SkillId.ASSASSIN))
            // removeSkillEffect(L1SkillId.ASSASSIN);
            S_Invis iv = new S_Invis(getId(), 0);
            sendPackets(iv);
            Broadcaster.broadcastPacket(this, iv, true);
            for (L1PcInstance pc2 : L1World.getInstance().getVisiblePlayer(this)) {
                pc2.sendPackets(new S_OtherCharPacks(this, pc2));
            }
            if (isInParty()) {
                for (L1PcInstance tar : L1World.getInstance().getVisiblePlayer(this, 20)) {
                    if (getParty().isMember(tar)) {
                        tar.sendPackets(new S_HPMeter(this));
                    }
                }
            }
        }

    }

    public void delBlindHiding() {
        killSkillEffectTimer(L1SkillId.BLIND_HIDING);
        sendPackets(new S_Invis(getId(), 0));
        broadcastPacket(new S_Invis(getId(), 0));
        // System.out.println("3");
        // broadcastPacket(new S_OtherCharPacks(this));
    }

    public void receiveDamage(L1Character attacker, int damage, int attr) {
        if (damage == 0)
            return;
        Random random = new Random(System.nanoTime());
        int player_mr = getResistance().getEffectedMrBySkill();
        int rnd = random.nextInt(100) + 1;
        if (player_mr >= rnd) {
            damage /= 2;
        }
        receiveDamage(attacker, damage);
    }

    public void receiveManaDamage(L1Character attacker, int mpDamage) {
        if (mpDamage > 0 && !isDead()) {
            delInvis();
            if (attacker instanceof L1PcInstance) {
                L1PinkName.onAction(this, attacker);
            }
            /** カオ経費認識無敵バグ **/
            /**
             * if (attacker instanceof L1PcInstance && ((L1PcInstance)
             * attacker).isPinkName()) { L1GuardInstance guard = null; for
             * (L1Object object :
             * L1World.getInstance().getVisibleObjects(attacker)) { if(object ==
             * null) continue; if (object instanceof L1GuardInstance) { guard =
             * (L1GuardInstance) object; if (rightNow.getTimeInMillis() <
             * ((L1PcInstance) attacker).getLastPk().getTime() + (1000 * 60 * 60
             * * 1)){ // 最後pk時間が1時間にならない経ったら経費が認識
             * guard.setTarget(((L1PcInstance) attacker)); } } } }
             **/
            /** カオ経費認識 **/
            int newMp = getCurrentMp() - mpDamage;
            this.setCurrentMp(newMp);
        }
    }

    public boolean isInWarArea() {
        boolean isNowWar = false;
        int castleId = L1CastleLocation.getCastleIdByArea(this);

        if (castleId != 0) {
            isNowWar = WarTimeController.getInstance().isNowWar(castleId);
        }

        return isNowWar;
    }

    public void receiveCounterBarrierDamage(L1Character attacker, int damage) {
        if (getCurrentHp() > 0 && !isDead()) {
            if (attacker != this && !knownsObject(attacker) && attacker.getMapId() == this.getMapId()) {
                attacker.onPerceive(this);
            }

            if (damage > 0) {
                delInvis();
                if (hasSkillEffect(L1SkillId.FOG_OF_SLEEPING)) {
                    removeSkillEffect(L1SkillId.FOG_OF_SLEEPING);
                } else if (hasSkillEffect(L1SkillId.PHANTASM)) {
                    removeSkillEffect(L1SkillId.PHANTASM);
                }
            } else if (damage < 0) {
                return;
            }
            if (getInventory().checkEquipped(145) || getInventory().checkEquipped(149)) {
                damage *= 1.5;
            }
            int newHp = getCurrentHp() - damage;
            if (newHp > getMaxHp()) {
                newHp = getMaxHp();
            }
            if (newHp <= 10) {
                if (isElf() && hasSkillEffect(L1SkillId.SOUL_BARRIER)) {
                    int newMp = getCurrentMp() - damage;
                    this.setCurrentHp(10);
                    if (newMp <= 0) {
                        death(attacker, true);
                        this.setCurrentHp(0);
                    }
                    this.setCurrentMp(newMp);
                } else if (newHp <= 0) {
                    if (isGm()) {
                        this.setCurrentHp(getMaxHp());
                    } else {
                        /** カウンターバリアー、タイタンダメージ処理 **/
                        if (Config.BROADCAST_KILL_LOG && getLevel() >= Config.BROADCAST_KILL_LOG_LEVEL) {
                            L1World.getInstance().broadcastPacketToAll(new S_ChatPacket(
                                    "\\aH[" + attacker.getName() + "]\\aA 様が \\aG[" + getName() + "]\\aA 様を殺した。",
                                    Opcodes.S_MESSAGE));
                            attacker.setKills(attacker.getKills() + 1); // 勝った超えキルス+1
                            setDeaths(getDeaths() + 1); // ジンノムデス数+1
                            LinAllManagerInfoThread.PvPCount += 1;
                        }
                        /** カウンターバリアー、タイタンダメージ処理 **/
                        if (isSiege) {
                            if (getMapId() == 10502) {
                                /*
                                 * L1ItemInstance aden =
                                 * ItemTable.getInstance().createItem(40308);
                                 * aden.setCount(1000);
                                 * L1World.getInstance().getInventory(getX(),
                                 * getY(),getMapId()).insertItem(aden);
                                 */
                                System.out.println("getX():" + getX() + " GETY L:: " + getY());
                                L1GroundInventory ground = L1World.getInstance().getInventory(getX(), getY(),
                                        (short) 10502);
                                L1ItemInstance item = ItemTable.getInstance().createItem(40308);
                                item.setCount(1000);
                                ground.storeItem(item);
                            }
                        }
                        if (isDeathMatch()) {
                            if (getMapId() == 5153) {
                                try {
                                    save();
                                    beginGhost(getX(), getY(), (short) getMapId(), true);
                                    sendPackets(new S_ServerMessage(1271));
                                    if (getAbysspoint() >= 100) {
                                        addAbysspoint(-100);
                                        ((L1PcInstance) attacker).addAbysspoint(100);
                                        sendPackets(new S_SystemMessage("アビスポイント100点を失いました"));
                                        ((L1PcInstance) attacker)
                                                .sendPackets(new S_SystemMessage("アビスポイント100点を獲得しました"));
                                    }
                                } catch (Exception e) {
                                    _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
                                }
                                return;
                            }
                        } else {
                            death(attacker, false);
                        }
                    }
                }
            }
            if (newHp > 0) {
                this.setCurrentHp(newHp);
            }
        } else if (!isDead()) {
            System.out.println("■■■■■■■HP減少が正しくなかったキャラクターがわかった。※あるいは最初から血が0");
            death(attacker, false);
        }
    }

    public void receiveDamage(L1Character attacker, int damage) {
        if (getCurrentHp() > 0 && !isDead()) {
            /** ロボットシステム */
            if (getRobotAi() != null && damage >= 0) {
                if (!(attacker instanceof L1EffectInstance)) {
                    getRobotAi().setHate(attacker, (int) damage);
                }
            }
            /** ロボットシステム */
            if (attacker != this && !knownsObject(attacker) && attacker.getMapId() == this.getMapId()) {
                attacker.onPerceive(this);
            }

            if (damage > 0) {
                if (this instanceof L1RobotInstance) {
                    L1RobotInstance bot = (L1RobotInstance) this;
                    if (bot.delay_Bot == 0) {
                        int sleepTime = bot.calcSleepTime(L1RobotInstance.DMG_MOTION_SPEED);
                    }

                }
                delInvis();
                if (attacker instanceof L1PcInstance) {
                    L1PinkName.onAction(this, attacker);
                }

                if (hasSkillEffect(L1SkillId.FOG_OF_SLEEPING)) {
                    removeSkillEffect(L1SkillId.FOG_OF_SLEEPING);
                } else if (hasSkillEffect(L1SkillId.PHANTASM)) {
                    removeSkillEffect(L1SkillId.PHANTASM);
                }
            } else if (damage < 0) {
                if (attacker instanceof L1PcInstance) {
                    L1PinkName.onHelp(this, attacker);
                }
            }
            if (getInventory().checkEquipped(145) || getInventory().checkEquipped(149)) {
                damage *= 1.5;
            }
            int newHp = getCurrentHp() - damage;

            Random random = new Random();
            int chance = random.nextInt(100) + 1;
            int plus_hp = 60 + random.nextInt(15);

            if (getInventory().checkEquipped(22200) || getInventory().checkEquipped(22201)
                    || getInventory().checkEquipped(22202) || getInventory().checkEquipped(22203)) {// パプ
                if (chance <= 10 && (getCurrentHp() != getMaxHp())) {
                    if (hasSkillEffect(L1SkillId.WATER_LIFE)) {
                        plus_hp *= 2;
                    }
                    if (hasSkillEffect(L1SkillId.POLLUTE_WATER)) {
                        plus_hp /= 2;
                    }
                    newHp += plus_hp;
                    // healHp(plus_hp);
                    sendPackets(new S_SkillSound(getId(), 2187));
                    broadcastPacket(new S_SkillSound(getId(), 2187));
                }
            }

            if (newHp > getMaxHp()) {
                newHp = getMaxHp();
            }
            if (newHp <= 10) {
                if (isElf() && hasSkillEffect(L1SkillId.SOUL_BARRIER)) {
                    this.setCurrentHp(10);
                    newHp = 10;
                    int newMp = getCurrentMp() - damage;
                    if (newMp <= 0) {
                        this.setCurrentHp(0);
                        death(attacker, true);

                    }
                    this.setCurrentMp(newMp);
                } else if (newHp <= 0) {
                    if (isGm()) {
                        setCurrentHp(getMaxHp());
                    } else {
                        if (attacker instanceof L1PcInstance) {
                            if (this.getHuntCount() != 0) {
                                int price = this.getHuntPrice();
                                this.setHuntCount(0);
                                this.setHuntPrice(0);
                                this.setReasonToHunt(null);
                                huntoption(this);
                                attacker.getInventory().storeItem(40308, price);
                                sendPackets(new S_SystemMessage("\\fY手配が解け、追加のオプションが消えました。"));
                                // ((L1PcInstance) attacker).sendPackets(new
                                // S_SystemMessage("\\fTPvP勝利へのクラン経験値2上昇 "））;
                                L1World.getInstance().broadcastPacketToAll(new S_SystemMessage(
                                        "\\fT[" + attacker.getName() + "]  " + this.getName() + "様賞金ありがとうございます。"));
                            }
                            L1Clan clan = L1World.getInstance().getClan(getClanname());
                            if (getClanid() != 0 && getClan().getClanExp() >= 2) {
                                clan.addClanExp(-2);
                                sendPackets(new S_SystemMessage("PvP敗北でクラン経験値2下落"));
                                ClanTable.getInstance().updateClan(this.getClan()); // 死んだ人血盟の更新
                                for (L1PcInstance tc : getClan().getOnlineClanMember()) {
                                    tc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
                                            "[" + getName() + "]さんPvP敗北で血盟経験値2の低下。"));
                                }
                            }
                            if (isSiege) {
                                if (getMapId() == 10502) {
                                    /*
                                     * L1ItemInstance aden =
                                     * ItemTable.getInstance().createItem(40308)
                                     * ; aden.setCount(1000);
                                     * L1World.getInstance().getInventory(getX()
                                     * ,getY(),getMapId()).insertItem(aden);
                                     */
                                    System.out.println("getX():" + getX() + " GETY L:: " + getY());
                                    L1GroundInventory ground = L1World.getInstance().getInventory(getX(), getY(),
                                            (short) 10502);
                                    L1ItemInstance item = ItemTable.getInstance().createItem(40308);
                                    item.setCount(1000);
                                    ground.storeItem(item);
                                }
                            }
                            if (((L1PcInstance) attacker).getClanid() > 0) { // アタックがクランがある場合にのみ、処理
                                ((L1PcInstance) attacker).getClan().addClanExp(2);
                                ((L1PcInstance) attacker).sendPackets(new S_SystemMessage("PvP勝利でクラン経験値2上昇"));
                                ClanTable.getInstance().updateClan(((L1PcInstance) attacker).getClan()); // 殺した人
                                // 血盟の更新
                                for (L1PcInstance tc : ((L1PcInstance) attacker).getClan().getOnlineClanMember()) {
                                    tc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
                                            "[" + ((L1PcInstance) attacker).getName() + "]さんPvP勝利で血盟経験値2上昇。"));
                                }
                            }
                            death(attacker, true);
                        }

                        if (isDeathMatch()) {
                            if (getMapId() == 5153) {
                                try {
                                    save();
                                    beginGhost(getX(), getY(), (short) getMapId(), true);
                                    sendPackets(new S_ServerMessage(1271));
                                } catch (Exception e) {
                                    _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
                                }
                                return;
                            }

                        } else {
                            death(attacker, true);
                        }
                    }
                }
            }
            if (newHp > 0) {
                this.setCurrentHp(newHp);
            }
        } else if (!isDead()) {
            System.out.println("■■■■■■■HP減少処理が正しくなかったキャラクターがわかった。※あるいは最初から血が0");
            death(attacker, true);
        }
    }

    public void death(L1Character lastAttacker, boolean deathPenalty) {
        synchronized (this) {
            if (isDead()) {
                return;
            }
            if (hasSkillEffect(L1SkillId.STATUS_TOMAHAWK)) {
                return;
            }
            setCurrentHp(0);
            setDead(true);
            setStatus(ActionCodes.ACTION_Die);
        }
        if (lastAttacker instanceof L1PcInstance) {
            L1PcInstance _atker = (L1PcInstance) lastAttacker;

            // ロボットキル
            // _atker._PlayPcKill++;
            if (getZoneType() == 0) {
                if (this instanceof L1RobotInstance) { // ロボットなら
                    if (_atker instanceof L1PcInstance && this.getClanid() != _atker.getClanid()) { // 一般ユーザであれば、
                    }
                }

                if (_atker.isInParty()) {
                    for (L1PcInstance atker_p : _atker.getParty().getMembers()) {
                        atker_p.sendPackets(new S_ServerMessage(3690, lastAttacker.getName(), this.getName()), true);
                    }
                    if (this.isInParty()) {
                        for (L1PcInstance defender_p : this.getParty().getMembers()) {
                            defender_p.sendPackets(new S_ServerMessage(3689, this.getName()), true);
                        }
                    }
                } else {
                    _atker.sendPackets(new S_ServerMessage(3691, lastAttacker.getName(), this.getName()), true);
                }
            }
        }
        GeneralThreadPool.getInstance().execute(new Death(lastAttacker, deathPenalty));

    }

    private class Death implements Runnable {
        L1Character _lastAttacker;
        boolean _deathPenalty;

        Death(L1Character cha, boolean deathPenalty) {
            _lastAttacker = cha;
            _deathPenalty = deathPenalty;
        }

        public void run() {
            if (isTeleport()) {
                GeneralThreadPool.getInstance().schedule(this, 300);
                return;
            }

            L1Character lastAttacker = _lastAttacker;
            _lastAttacker = null;
            setCurrentHp(0);
            setGresValid(false);

            int targetobjid = getId();
            getMap().setPassable(getLocation(), true);

            int tempchargfx = 0;
            if (hasSkillEffect(L1SkillId.SHAPE_CHANGE)) {
                tempchargfx = getTempCharGfx();
                setTempCharGfxAtDead(tempchargfx);
            } else {
                setTempCharGfxAtDead(getClassId());
            }

            L1SkillUse l1skilluse = new L1SkillUse();
            l1skilluse.handleCommands(L1PcInstance.this, L1SkillId.CANCELLATION, getId(), getX(), getY(), null, 0,
                    L1SkillUse.TYPE_LOGIN);

            if (tempchargfx == 5727 || tempchargfx == 5730 || tempchargfx == 5733 || tempchargfx == 5736) {
                tempchargfx = 0;
            }
            if (tempchargfx != 0) {
                sendPackets(new S_ChangeShape(getId(), tempchargfx));
                broadcastPacket(new S_ChangeShape(getId(), tempchargfx));
            }

            sendPackets(new S_DoActionGFX(targetobjid, ActionCodes.ACTION_Die));
            broadcastPacket(new S_DoActionGFX(targetobjid, ActionCodes.ACTION_Die));

            if (lastAttacker != L1PcInstance.this) {
                L1PcInstance player = null;
                if (lastAttacker instanceof L1PcInstance) {
                    player = (L1PcInstance) lastAttacker;
                    /** 殺した場合エフェクト飛ばし **/
                    player.sendPackets(new S_SkillSound(player.getId(), 6354));
                    player.broadcastPacket(new S_SkillSound(player.getId(), 6354));
                    /** 殺した場合エフェクト飛ばし **/
                } else if (lastAttacker instanceof L1PetInstance) {
                    player = (L1PcInstance) ((L1PetInstance) lastAttacker).getMaster();
                } else if (lastAttacker instanceof L1SummonInstance) {
                    player = (L1PcInstance) ((L1SummonInstance) lastAttacker).getMaster();
                }
                if (player != null) {
                    if (getZoneType() == -1) {
                        return;
                    }
                }

                /** 攻城戦でのみ処理すること **/
                if (player != null) {

                    if (isInWarAreaAndWarTime(L1PcInstance.this, player)) {
                        int price = getHuntPrice();
                        if (Config.BROADCAST_KILL_LOG && getLevel() >= Config.BROADCAST_KILL_LOG_LEVEL) {
                            L1World.getInstance()
                                    .broadcastPacketToAll(new S_ChatPacket(
                                            "\\aH[" + player.getName() + "]\\aA 様が \\aG[" + getName() + "]\\aA 様を殺した。",
                                            Opcodes.S_MESSAGE));
                            player.setKills(player.getKills() + 1); // 勝った超えキルス+1
                            setDeaths(getDeaths() + 1); // ジンノムデス数+1
                            LinAllManagerInfoThread.PvPCount += 1;
                            if (getHuntCount() > 0) {
                                player.getInventory().storeItem(40308, price);
                                setHuntCount(0);
                                setHuntPrice(0);
                                setReasonToHunt(null);
                                L1World.getInstance().broadcastPacketToAll(new S_SystemMessage(
                                        "\\fT[" + player.getName() + "]  " + getName() + "様賞金ありがとうございます。"));
                                try {
                                    save();
                                } catch (Exception e) {
                                    _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
                                }
                            }
                        }
                    }
                }
                /** 攻城戦でのみ処理すること **/

                boolean sim_ret = simWarResult(lastAttacker);
                if (sim_ret == true) {
                    return;
                }
            }

            if (!getMap().isEnabledDeathPenalty()) {
                return;
            }

            L1PcInstance fightPc = null;
            if (lastAttacker instanceof L1PcInstance) {
                fightPc = (L1PcInstance) lastAttacker;
            }
            if (fightPc != null) {
                if (getFightId() == fightPc.getId() && fightPc.getFightId() == getId()) {
                    setFightId(0);
                    sendPackets(new S_PacketBox(S_PacketBox.MSG_DUEL, 0, 0));
                    fightPc.setFightId(0);
                    fightPc.sendPackets(new S_PacketBox(S_PacketBox.MSG_DUEL, 0, 0));
                    return;
                }
            }

            /** ボール成長経験値下落途方もなく **/
            boolean castle_ret1 = castleWarResult();
            if (lastAttacker instanceof L1PcInstance) {// バフォメット
                if (!castle_ret1 && getLevel() < Config.NEW_PLAYER && (lastAttacker.getLevel() - getLevel()) >= 10) {
                    isExpDrop = false;
                    isItemDrop = false;
                }
            }
            if (castle_ret1 == true) {
                sendPackets(new S_ServerMessage(3798));
                // 経験値の損失がない地域：経験値が失われていない。
                return;
            }

            // 古代のが号
            if (getInventory().checkEquipped(900022)) {
                drop1();
                return;
            }

            // サキュバスクイーンの契約
            if (getInventory().checkEquipped(900040)) {
                drop2();
                return;
            }

            if (Config.ARNOLD_EVENTS) {
                if (lastAttacker instanceof L1PcInstance) {
                    if (getInventory().checkEquipped(21095)) { // 着用したアイテム
                        drop();
                        return;
                    }
                } else {
                    int chance = _random.nextInt(100);
                    if (chance < 40) {
                        if (getInventory().checkEquipped(21095)) { // 着用したアイテム
                            drop();
                            return;
                        }
                    }
                }
            }

            deathPenalty();
            setGresValid(true);

            if (getExpRes() == 0) {// /遭遇の加護で経験値回復fix
                if (lastAttacker instanceof L1PcInstance && getLevel() < Config.NEW_PLAYER
                        && (lastAttacker.getLevel() - getLevel()) >= 10) {
                } else {
                    setExpRes(1);
                }
            }

            /** シャンク時ガード値よう **/
            if (lastAttacker instanceof L1GuardInstance) {
                if (get_PKcount() > 0) {
                    set_PKcount(get_PKcount() - 1);
                }
                setLastPk(null);
            }
            /** シャンク時ガード値よう **/
            /** ロボットダイメント **/
            if (lastAttacker instanceof L1RobotInstance) {
                Random dierandom = new Random();
                L1PcInstance lastrob = (L1RobotInstance) lastAttacker;
                diement = _diementArray[dierandom.nextInt(_diementArray.length)];
                try {
                    Delay(1000);
                    Broadcaster.broadcastPacket(lastrob, new S_ChatPacket(lastrob, diement, Opcodes.S_SAY, 0));
                    diement = null;
                } catch (Exception e) {
                }
            }
            /** 死亡時システム・魔法ドロップ確率。バポシステム化。 */
            int lostRate = (int) (((getLawful() + 32768D) / 1000D - 65D) * 6D);

            if (lostRate < 0) {
                lostRate *= -1;
                if (getLawful() < 0) {
                    lostRate *= 4;
                }
                Random random = new Random();
                int rnd = random.nextInt(1000) + 1;

                if (rnd <= lostRate) {
                    int count = 1;
                    int skillcount = 1;

                    if (getLawful() <= -20000) {
                        count = random.nextInt(4) + 1;
                        // skillcount = random.nextInt(4) + 1;
                    } else if (getLawful() <= -10000) {
                        count = random.nextInt(3) + 1;
                        // skillcount = random.nextInt(3) + 1;
                    } else if (getLawful() <= -5000) {
                        count = random.nextInt(2) + 1;
                        // skillcount = random.nextInt(2) + 1;
                    } else if (getLawful() < 0) {
                        count = random.nextInt(1) + 1;
                        // skillcount = 0;
                    }
                    caoPenaltyResult(count);
                    // caoPenaltySkill(skillcount);
                }
            }
            /** 死亡時システム・魔法ドロップ確率。バポシステム化 */
            boolean castle_ret = castleWarResult();
            if (castle_ret == true) {
                return;
            }

            L1PcInstance player = null;
            if (lastAttacker instanceof L1PcInstance) {
                player = (L1PcInstance) lastAttacker;
            }

            if (_deathPenalty && player != null) {// PKコメント
                int price = getHuntPrice();
                if (Config.BROADCAST_KILL_LOG && getLevel() >= Config.BROADCAST_KILL_LOG_LEVEL
                        && !(isRobot() && player.isRobot())) {
                    L1World.getInstance()
                            .broadcastPacketToAll(new S_ChatPacket(
                                    "\\aH[" + player.getName() + "]\\aA 様が \\aG[" + getName() + "]\\aA 様を殺した。",
                                    Opcodes.S_MESSAGE));
                    player.setKills(player.getKills() + 1); // 勝った超えキルス+1
                    setDeaths(getDeaths() + 1); // ジンノムデス数+1
                    LinAllManagerInfoThread.PvPCount += 1;
                    String locname = MapsTable.getInstance().getMapName(player.getMapId());
                    L1World.getInstance().broadcastPacketToAll(
                            new S_ChatPacket("\\aG[戦闘地域]: \\aA" + locname + "", Opcodes.S_MESSAGE));
                    if (getHuntCount() > 0) {
                        player.getInventory().storeItem(40308, price);
                        setHuntCount(0);
                        setHuntPrice(0);
                        setReasonToHunt(null);
                        L1World.getInstance().broadcastPacketToAll(
                                new S_SystemMessage("\\fT[" + player.getName() + "]  " + getName() + "様賞金ありがとうございます。"));
                        try {
                            save();
                        } catch (Exception e) {
                            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
                        }
                    }
                }

                if (getLawful() >= 0 && isPinkName() == false) {
                    boolean isChangePkCount = false;
                    if (player.getLawful() < 30000) {
                        player.set_PKcount(player.get_PKcount() + 1);
                        isChangePkCount = true;
                        player.setLastPk();
                    }

                    int lawful;

                    if (player.getLevel() < 50) {
                        lawful = -1 * (int) ((Math.pow(player.getLevel(), 2) * 4));
                    } else {
                        lawful = -1 * (int) ((Math.pow(player.getLevel(), 3) * 0.08));
                    }
                    if ((player.getLawful() - 1000) < lawful) {
                        lawful = player.getLawful() - 1000;
                    }

                    if (lawful <= -32768) {
                        lawful = -32768;
                    }
                    player.setLawful(lawful);

                    S_Lawful s_lawful = new S_Lawful(player.getId(), player.getLawful());
                    player.sendPackets(s_lawful);
                    player.broadcastPacket(s_lawful);
                    player.sendPackets(new S_PacketBox(S_PacketBox.BATTLE_SHOT, L1PcInstance.this.getId()));
                } else {
                    setPinkName(false);
                }
            }
        }
    }

    private void caoPenaltyResult(int count) {
        /** 攻城ゾーンでドロップしないように **/
        // System.out.println("死亡ペナルティ");
        int castle_id = L1CastleLocation.getCastleIdByArea(this);
        if (castle_id != 0) {
            return;
        }

        /** ロボットシステム **/
        if (getRobotAi() != null || getAccessLevel() == Config.GMCODE) {
            return;
        }

        for (int i = 0; i < count; i++) {
            L1ItemInstance item = getInventory().CaoPenalty();
            LinAllManagerInfoThread.PenaltyCount += 1;
            if (item != null) {
                if (item.getBless() > 3) {
                    getInventory().removeItem(item, item.isStackable() ? item.getCount() : 1);
                    sendPackets(new S_ServerMessage(158, item.getLogName()));// 蒸発
                    LinAllManager.getInstance().PenaltyAppend(item.getLogName(), getName(), count, 1);
                    /** ファイルログの保存 **/
                    LoggerInstance.getInstance().addItemAction(ItemActionType.del, this, item, count);
                    /** DB +メモリに飛ばしただろ保存 **/
                    if (item.getItem().getType2() == 1 || item.getItem().getType2() == 2) {
                        // RestoreItemTable.getInstance().AddRestoreItem(this.getId(),
                        // new
                        // L1RestoreItemInstance(item.getItemId(),item.getEnchantLevel(),item.getAttrEnchantLevel(),item.getBless()));
                    }
                } else {
                    getInventory().tradeItem(item, item.isStackable() ? item.getCount() : 1,
                            L1World.getInstance().getInventory(getX(), getY(), getMapId()));
                    sendPackets(new S_ServerMessage(638, item.getLogName()));// 蒸発
                    /** DB +メモリに飛ばしただろ保存 **/
                    if (item.getItem().getType2() == 1 || item.getItem().getType2() == 2) {
                        // RestoreItemTable.getInstance().AddRestoreItem(this.getId(),
                        // new
                        // L1RestoreItemInstance(item.getItemId(),item.getEnchantLevel(),item.getAttrEnchantLevel(),item.getBless()));
                    }
                    LinAllManager.getInstance().PenaltyAppend(item.getLogName(), getName(), count, 0);
                    /** ファイルログの保存 **/
                    LoggerInstance.getInstance().addItemAction(ItemActionType.del, this, item, count);
                }
            }
        }
    }

    private void caoPenaltySkill(int count) {
        int l = 0;
        int lv1 = 0;
        int lv2 = 0;
        int lv3 = 0;
        int lv4 = 0;
        int lv5 = 0;
        int lv6 = 0;
        int lv7 = 0;
        int lv8 = 0;
        int lv9 = 0;
        int lv10 = 0;
        Random random = new Random();
        int lostskilll = 0;
        for (int i = 0; i < count; i++) {
            if (isCrown()) {
                lostskilll = random.nextInt(16) + 1;
            } else if (isKnight()) {
                lostskilll = random.nextInt(8) + 1;
            } else if (isElf()) {
                lostskilll = random.nextInt(48) + 1;
            } else if (isDarkelf()) {
                lostskilll = random.nextInt(23) + 1;
            } else if (isWizard()) {
                lostskilll = random.nextInt(80) + 1;
            }

            if (!SkillsTable.getInstance().spellCheck(getId(), lostskilll)) {
                return;
            }

            L1Skills l1skills = null;
            l1skills = SkillsTable.getInstance().getTemplate(lostskilll);
            if (l1skills.getSkillLevel() == 1) {
                lv1 |= l1skills.getId();
            }
            if (l1skills.getSkillLevel() == 2) {
                lv2 |= l1skills.getId();
            }
            if (l1skills.getSkillLevel() == 3) {
                lv3 |= l1skills.getId();
            }
            if (l1skills.getSkillLevel() == 4) {
                lv4 |= l1skills.getId();
            }
            if (l1skills.getSkillLevel() == 5) {
                lv5 |= l1skills.getId();
            }
            if (l1skills.getSkillLevel() == 6) {
                lv6 |= l1skills.getId();
            }
            if (l1skills.getSkillLevel() == 7) {
                lv7 |= l1skills.getId();
            }
            if (l1skills.getSkillLevel() == 8) {
                lv8 |= l1skills.getId();
            }
            if (l1skills.getSkillLevel() == 9) {
                lv9 |= l1skills.getId();
            }
            if (l1skills.getSkillLevel() == 10) {
                lv10 |= l1skills.getId();
            }

            SkillsTable.getInstance().spellLost(getId(), lostskilll);
            l = lv1 + lv2 + lv3 + lv4 + lv5 + lv6 + lv7 + lv8 + lv9 + lv10;
        }
        if (l > 0) {
            sendPackets(new S_DelSkill(lv1, lv2, lv3, lv4, lv5, lv6, lv7, lv8, lv9, lv10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0));
        }
    }

    // 君主死ぬ血盟員の自動ベル//
    public boolean castleWarResult() {
        if (getClanid() != 0 && isCrown()) {
            L1Clan clan = L1World.getInstance().getClan(getClanname());
            for (L1War war : L1World.getInstance().getWarList()) {
                if (war == null)
                    continue;
                int warType = war.GetWarType();
                boolean isInWar = war.CheckClanInWar(getClanname());
                boolean isAttackClan = war.CheckAttackClan(getClanname());
                if (getId() == clan.getLeaderId() && warType == 1 && isInWar && isAttackClan) {
                    String enemyClanName = war.GetEnemyClanName(getClanname());
                    if (enemyClanName != null) {
                        if (war.GetWarType() == 1) {// 包囲場合
                            L1PcInstance clan_member[] = clan.getOnlineClanMember();//
                            int castle_id = war.GetCastleId();
                            int[] loc = new int[3];
                            loc = L1CastleLocation.getGetBackLoc(castle_id);
                            int locx = loc[0];
                            int locy = loc[1];
                            short mapid = (short) loc[2];
                            for (int k = 0; k < clan_member.length; k++) {
                                if (L1CastleLocation.checkInWarArea(castle_id, clan_member[k])) {
                                    // 機内の血盟員強制テレポート
                                    new L1Teleport().teleport(clan_member[k], locx, locy, mapid, 5, true);
                                }
                            }
                        }
                        war.CeaseWar(getClanname(), enemyClanName); // 終結
                    }
                    break;
                }
            }
        }

        int castleId = 0;
        boolean isNowWar = false;
        castleId = L1CastleLocation.getCastleIdByArea(this);
        if (castleId != 0) {
            isNowWar = WarTimeController.getInstance().isNowWar(castleId);
        }
        return isNowWar;
    }

    // 君主死ぬ血盟員の自動ベル//
    public boolean simWarResult(L1Character lastAttacker) {
        if (getClanid() == 0) {
            return false;
        }
        if (Config.SIM_WAR_PENALTY) {
            return false;
        }
        L1PcInstance attacker = null;
        String enemyClanName = null;
        boolean sameWar = false;

        if (lastAttacker instanceof L1PcInstance) {
            attacker = (L1PcInstance) lastAttacker;
        } else if (lastAttacker instanceof L1PetInstance) {
            attacker = (L1PcInstance) ((L1PetInstance) lastAttacker).getMaster();
        } else if (lastAttacker instanceof L1SummonInstance) {
            attacker = (L1PcInstance) ((L1SummonInstance) lastAttacker).getMaster();
        } else {
            return false;
        }
        L1Clan clan = null;
        for (L1War war : L1World.getInstance().getWarList()) {
            if (war == null)
                continue;
            clan = L1World.getInstance().getClan(getClanname());

            int warType = war.GetWarType();
            boolean isInWar = war.CheckClanInWar(getClanname());
            if (attacker != null && attacker.getClanid() != 0) {
                sameWar = war.CheckClanInSameWar(getClanname(), attacker.getClanname());
            }

            if (getId() == clan.getLeaderId() && warType == 2 && isInWar == true) {
                enemyClanName = war.GetEnemyClanName(getClanname());
                if (enemyClanName != null) {
                    war.CeaseWar(getClanname(), enemyClanName);
                }
            }

            if (warType == 2 && sameWar) {
                return true;
            }
        }
        return false;
    }

    public void resExp() {
        int oldLevel = getLevel();
        int needExp = ExpTable.getNeedExpNextLevel(oldLevel);
        int exp = 0;
        double ratio;

        if (oldLevel < 45)
            ratio = 0.05;
        else if (oldLevel >= 49)
            ratio = 0.025;
        else
            ratio = 0.05 - (oldLevel - 44) * 0.005;

        exp = (int) (needExp * ratio);

        if (exp == 0)
            return;

        addExp(exp);
    }

    public void resExpToTemple() {
        int oldLevel = getLevel();
        int needExp = ExpTable.getNeedExpNextLevel(oldLevel);
        int exp = 0;
        double ratio;

        if (oldLevel < 45)
            ratio = 0.05;
        else if (oldLevel >= 45 && oldLevel < 49)
            ratio = 0.05 - (oldLevel - 44) * 0.005;
        else if (oldLevel >= 49 && oldLevel < 52)
            ratio = 0.025;
        else if (oldLevel == 52)
            ratio = 0.026;
        else if (oldLevel > 52 && oldLevel < 74)
            ratio = 0.026 + (oldLevel - 52) * 0.001;
        else if (oldLevel >= 74 && oldLevel < 79)
            ratio = 0.025 - (oldLevel - 73) * 0.0005;
        else
            /* if (oldLevel >= 79) */
            ratio = Config.RECOVERY_EXP; // 79レップから4.9％の回復0.049

        exp = (int) (needExp * ratio);
        if (exp == 0)
            return;

        addExp(exp);
    }

    private boolean is_Succubus() {
        L1ItemInstance succubusItem = getInventory().getEquippedItem(900039);
        if (getInventory().checkEquipped(900039)) {
            getInventory().setEquipped(succubusItem, false);
            getInventory().removeItem(succubusItem, 1);
            sendPackets(new S_ServerMessage(3802));
            sendPackets(new S_SystemMessage("サキュバスの契約を失った。"));
            L1World.getInstance().getInventory(getX(), getY(), getMapId())
                    .storeItem(ItemTable.getInstance().createItem(3000157));
            return true;
        }
        return false;
    }

    public void deathPenalty() {
        /* サキュバス追加 */
        if (is_Succubus())
            return;
        /* サキュバス追加 */
        int oldLevel = getLevel();
        int needExp = ExpTable.getNeedExpNextLevel(oldLevel);
        int exp = 0;

        if (oldLevel >= 1 && oldLevel < 11)
            exp = 0;
        else if (oldLevel >= 11 && oldLevel < 45)
            exp = (int) (needExp * 0.1);
        else if (oldLevel == 45)
            exp = (int) (needExp * 0.09);
        else if (oldLevel == 46)
            exp = (int) (needExp * 0.08);
        else if (oldLevel == 47)
            exp = (int) (needExp * 0.07);
        else if (oldLevel == 48)
            exp = (int) (needExp * 0.06);
        else if (oldLevel >= 49)
            exp = (int) (needExp * 0.05);

        if (exp == 0)
            return;

        addExp(-exp);
    }

    public int getbase_Er() {
        int er = 0;
        er = (getAbility().getTotalDex() - 8) / 2;
        return er;
    }

    public int get_Er() {
        int er = 0;

        int BaseEr = CalcStat.calcLongRangeAvoid(getAbility().getTotalDex());

        er += BaseEr;
        return er;
    }

    int _add_er = 0;

    public int getAdd_Er() {
        return _add_er;
    }

    public void Add_Er(int i) {
        _add_er += i;
    }

    public int get_PlusEr() {
        int er = 0;
        er += get_Er();
        er += getAdd_Er();
        if (er < 0) {
            er = 0;
        } else {
            if (hasSkillEffect(L1SkillId.STRIKER_GALE)) {
                er = er / 3;
            }
        }
        return er;
    }

    public L1BookMark getBookMark(String name) {
        L1BookMark element = null;
        int size = _bookmarks.size();
        for (int i = 0; i < size; i++) {
            element = _bookmarks.get(i);
            if (element == null)
                continue;
            if (element.getName().equalsIgnoreCase(name)) {
                return element;
            }
        }
        return null;
    }

    public L1BookMark getBookMark(int id) {
        L1BookMark element = null;
        int size = _bookmarks.size();
        for (int i = 0; i < size; i++) {
            element = _bookmarks.get(i);
            if (element == null)
                continue;
            if (element.getId() == id) {
                return element;
            }
        }
        return null;
    }

    public int getBookMarkSize() {
        return _bookmarks.size();
    }

    public void addBookMark(L1BookMark book) {
        _bookmarks.add(book);
    }

    public void removeBookMark(L1BookMark book) {
        _bookmarks.remove(book);
    }

    public L1ItemInstance getWeapon() {
        return _weapon;
    }

    public void setWeapon(L1ItemInstance weapon) {
        _weapon = weapon;
    }

    // public L1ItemInstance getWeapon() {
    // return getEquipSlot().getWeapon();
    // }

    public L1ItemInstance getWeaponSwap() {
        return getEquipSlot().getWeaponSwap();
    }

    public L1ItemInstance getArmor() {
        return _armor;
    }

    public void setArmor(L1ItemInstance armor) {
        _armor = armor;
    }

    public L1ItemInstance getSecondWeapon() {
        return _secondweapon;
    }

    public void setSecondWeapon(L1ItemInstance weapon) {
        _secondweapon = weapon;
    }

    public L1Quest getQuest() {
        return _quest;
    }

    public String getClassName() {
        if (isCrown()) {
            return "君主";
        } else if (isKnight()) {
            return "ナイト";
        } else if (isElf()) {
            return "エルフ";
        } else if (isWizard()) {
            return "ウィザード";
        } else if (isDarkelf()) {
            return "ダークエルフ";
        } else if (isDragonknight()) {
            return "ドラゴンナイト";
        } else if (isBlackwizard()) {
            return "イリュージョニスト";
        } else if (isWarrior()) {
            return "ウォリアー";
        }

        return "職業人";
    }

    public int getClassNumber() {
        if (isCrown()) {
            return 0;
        } else if (isKnight()) {
            return 1;
        } else if (isElf()) {
            return 2;
        } else if (isWizard()) {
            return 3;
        } else if (isDarkelf()) {
            return 4;
        } else if (isDragonknight()) {
            return 5;
        } else if (isBlackwizard()) {
            return 6;
        } else {
            return 7;
        }
    }

    public boolean isCrown() {
        return (getClassId() == CLASSID_PRINCE || getClassId() == CLASSID_PRINCESS);
    }

    public boolean isKnight() {
        return (getClassId() == CLASSID_KNIGHT_MALE || getClassId() == CLASSID_KNIGHT_FEMALE);
    }

    public boolean isElf() {
        return (getClassId() == CLASSID_ELF_MALE || getClassId() == CLASSID_ELF_FEMALE);
    }

    public boolean isWizard() {
        return (getClassId() == CLASSID_WIZARD_MALE || getClassId() == CLASSID_WIZARD_FEMALE);
    }

    public boolean isDarkelf() {
        return (getClassId() == CLASSID_DARK_ELF_MALE || getClassId() == CLASSID_DARK_ELF_FEMALE);
    }

    public boolean isDragonknight() {
        return (getClassId() == CLASSID_DRAGONKNIGHT_MALE || getClassId() == CLASSID_DRAGONKNIGHT_FEMALE);
    }

    public boolean isBlackwizard() {
        return (getClassId() == CLASSID_BLACKWIZARD_MALE || getClassId() == CLASSID_BLACKWIZARD_FEMALE);
    }

    public boolean isWarrior() {
        return (getClassId() == CLASSID_WARRIOR_MALE || getClassId() == CLASSID_WARRIOR_FEMALE);
    }

    public String getAccountName() {
        return _accountName;
    }

    public void setAccountName(String s) {
        _accountName = s;
    }

    public short getBaseMaxHp() {
        return _baseMaxHp;
    }

    public void addBaseMaxHp(short i) {
        i += _baseMaxHp;
        if (i >= 32767) {
            i = 32767;
        } else if (i < 1) {
            i = 1;
        }
        addMaxHp(i - _baseMaxHp);
        _baseMaxHp = i;
    }

    public int getBaseMaxMp() {
        return _baseMaxMp;
    }

    public void addBaseMaxMp(int i) {
        i += _baseMaxMp;
        if (i >= 32767) {
            i = 32767;
        } else if (i < 0) {
            i = 0;
        }
        addMaxMp(i - _baseMaxMp);
        _baseMaxMp = i;
    }

    /**
     * ロボットシステム
     **/
    private L1RobotAI _robotAi = null;

    public L1RobotAI getRobotAi() {
        return _robotAi;
    }

    public void setRobotAi(L1RobotAI ai) {
        _robotAi = ai;
    }

    /**
     * ロボットシステム
     **/

    public int getOriginalMagicHit() {
        return _originalMagicHit;
    }

    public int getBaseAc() {
        return _baseAc;
    }

    public int getBaseDmgup() {
        return _baseDmgup;
    }

    public int getBaseBowDmgup() {
        return _baseBowDmgup;
    }

    public int getBaseHitup() {
        return _baseHitup;
    }

    public int getBaseBowHitup() {
        return _baseBowHitup;
    }

    public void setBaseMagicHitUp(int i) {
        _baseMagicHitup = i;
    }

    public int getBaseMagicHitUp() {
        return _baseMagicHitup;
    }

    public void setBaseMagicCritical(int i) {
        _baseMagicCritical = i;
    }

    public int getBaseMagicCritical() {
        return _baseMagicCritical;
    }

    public void setBaseMagicDmg(int i) {
        _baseMagicDmg = i;
    }

    public int getBaseMagicDmg() {
        return _baseMagicDmg;
    }

    public void setBaseMagicDecreaseMp(int i) {
        _baseMagicDecreaseMp = i;
    }

    public int getBaseMagicDecreaseMp() {
        return _baseMagicDecreaseMp;
    }

    public int getAdvenHp() {
        return _advenHp;
    }

    public void setAdvenHp(int i) {
        _advenHp = i;
    }

    public int getAdvenMp() {
        return _advenMp;
    }

    public void setAdvenMp(int i) {
        _advenMp = i;
    }

    public int getGiganticHp() {
        return _giganticHp;
    }

    public void setGiganticHp(int _giganticHp) {
        this._giganticHp = _giganticHp;
    }

    public int getHighLevel() {
        return _highLevel;
    }

    public void setHighLevel(int i) {
        _highLevel = i;
    }

    public int getBonusStats() {
        return _bonusStats;
    }

    public void setBonusStats(int i) {
        _bonusStats = i;
    }

    public int getElixirStats() {
        return _elixirStats;
    }

    public void setElixirStats(int i) {
        _elixirStats = i;
    }

    public int getElfAttr() {
        return _elfAttr;
    }

    public void setElfAttr(int i) {
        _elfAttr = i;
    }

    public int getExpRes() {
        return _expRes;
    }

    public void setExpRes(int i) {
        _expRes = i;
    }

    public int getPartnerId() {
        return _partnerId;
    }

    public void setPartnerId(int i) {
        _partnerId = i;
    }

    public int getOnlineStatus() {
        return _onlineStatus;
    }

    public void setOnlineStatus(int i) {
        _onlineStatus = i;
    }

    public int getHomeTownId() {
        return _homeTownId;
    }

    public void setHomeTownId(int i) {
        _homeTownId = i;
    }

    public int getContribution() {
        return _contribution;
    }

    public void setContribution(int i) {
        _contribution = i;
    }

    public int getHellTime() {
        return _hellTime;
    }

    public void setHellTime(int i) {
        _hellTime = i;
    }

    private boolean _morning = false;

    public void setMorning(boolean flag) {
        this._morning = flag;
    }

    public boolean getMorning() {
        return _morning;
    }

    public boolean isBanned() {
        return _banned;
    }

    public void setBanned(boolean flag) {
        _banned = flag;
    }

    public int get_food() {
        return _food;
    }

    public void set_food(int i) {
        _food = i;
    }

    /**
     * 生存の叫び
     **/
    public void add_food(int i) {
        _food += i;
        if (_food > 225) {
            _food = 225;
            if (getCryOfSurvivalTime() == 0)
                _cryofsurvivaltime = System.currentTimeMillis() / 1000;
        } else if (_food < 1) {
            _food = 1;
        }
    }

    private long _cryofsurvivaltime;

    public long getCryOfSurvivalTime() {
        return _cryofsurvivaltime;
    }

    public void setCryOfSurvivalTime() {
        if (get_food() >= 225) {
            _cryofsurvivaltime = System.currentTimeMillis() / 1000;

        }
    }

    /**
     * 生存の叫び
     **/

    public L1EquipmentSlot getEquipSlot() {
        return _equipSlot;
    }

    public static L1PcInstance load(String charName) {
        L1PcInstance result = null;
        try {
            result = CharacterTable.getInstance().loadCharacter(charName);
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
        return result;
    }

    public void save() throws Exception {
        if (this instanceof L1RobotInstance) {
            return;
        }
        if (isGhost()) {
            return;
        }
        // WeekQuestTable.getInstance().SaveQuestData(this);
        CharacterTable.getInstance().storeCharacter(this);
    }

    public void saveInventory() {
        for (L1ItemInstance item : getInventory().getItems()) {
            if (item != null)
                getInventory().saveItem(item, item.getRecordingColumns() != 0 ? L1PcInventory.COL_SAVE_ALL : 0);
        }
    }

    public void setRegenState(int state) {
        setHpRegenState(state);
        setMpRegenState(state);
    }

    public void setHpRegenState(int state) {
        if (_HpcurPoint < state)
            return;

        this._HpcurPoint = state;
    }

    public void setMpRegenState(int state) {
        if (_MpcurPoint < state)
            return;

        this._MpcurPoint = state;
    }

    public int getMaxWeight() {
        int str = getAbility().getTotalStr();
        int con = getAbility().getTotalCon();
        int maxWeight = CalcStat.getMaxWeight(str, con);
        double plusWeight = getWeightReduction();

        maxWeight += plusWeight;

        int dollWeight = 0;
        for (L1DollInstance doll : getDollList()) {
            dollWeight = doll.getWeightReductionByDoll();
        }
        maxWeight += dollWeight;
        int magicWeight = 0;

        if (hasSkillEffect(L1SkillId.DECREASE_WEIGHT)) {
            magicWeight = 180;
        }

        if (hasSkillEffect(L1SkillId.REDUCE_WEIGHT)) {
            magicWeight = 240;
        }

        maxWeight += magicWeight;
        maxWeight *= Config.RATE_WEIGHT_LIMIT;

        return maxWeight;
    }

    public boolean isFastMovable() {
        return (hasSkillEffect(L1SkillId.HOLY_WALK) || hasSkillEffect(L1SkillId.MOVING_ACCELERATION)
                || hasSkillEffect(L1SkillId.WIND_WALK));
    }

    public boolean isUgdraFruit() {
        return hasSkillEffect(L1SkillId.STATUS_FRUIT);
    }

    public boolean isBlood_lust() {
        return hasSkillEffect(L1SkillId.BLOOD_LUST);
    }

    public boolean isBrave() {
        return (hasSkillEffect(L1SkillId.STATUS_BRAVE));
    }

    public boolean isDancingBlades() {
        return (hasSkillEffect(L1SkillId.DANCING_BLADES));
    }

    public boolean isDragonPearl() {
        return (hasSkillEffect(L1SkillId.STATUS_DRAGON_PEARL) || getPearl() == 1);
    }

    public boolean isElfBrave() {
        return hasSkillEffect(L1SkillId.STATUS_ELFBRAVE);
    }

    public boolean isFruit() {
        return hasSkillEffect(L1SkillId.STATUS_FRUIT);
    }

    public boolean isHaste() {
        return (hasSkillEffect(L1SkillId.STATUS_HASTE) || hasSkillEffect(L1SkillId.HASTE)
                || hasSkillEffect(L1SkillId.GREATER_HASTE) || getMoveSpeed() == 1);
    }

    private int _pearl;

    public int getPearl() {
        return _pearl;
    }

    public void setPearl(int i) {
        _pearl = i;
    }

    public boolean isInvisDelay() {
        return (invisDelayCounter > 0);
    }

    public void addInvisDelayCounter(int counter) {
        synchronized (_invisTimerMonitor) {
            invisDelayCounter += counter;
        }
    }

    public void beginInvisTimer() {
        final long DELAY_INVIS = 3000L;
        addInvisDelayCounter(1);
        GeneralThreadPool.getInstance().schedule(new L1PcInvisDelay(getId()), DELAY_INVIS);
    }

    public synchronized void addExp(int exp) {
        _exp += exp;
        if (_exp > ExpTable.MAX_EXP) {
            _exp = ExpTable.MAX_EXP;
        }
    }

    public synchronized void addContribution(int contribution) {
        _contribution += contribution;
    }

    public void beginExpMonitor() {
        final long INTERVAL_EXP_MONITOR = 500;
        _expMonitorFuture = GeneralThreadPool.getInstance().scheduleAtFixedRate(new L1PcExpMonitor(getId()), 0L,
                INTERVAL_EXP_MONITOR);
    }

    /**
     * そのレベル以上の際の条件を出力する
     **/
    private void levelUp(int gap) {
        resetLevel();

        /** 特定のレップ以上の初心者血盟自動脱退 **/
        String BloodName = getClanname();
        if (getLevel() >= Config.NEW_CLAN_PROTECTION_LEVEL && BloodName.equalsIgnoreCase("新規保護血盟")) {
            try {
                L1Clan clan = L1World.getInstance().getClan("新規保護血盟");
                L1PcInstance clanMember[] = clan.getOnlineClanMember();
                String player_name = getName();
                String clan_name = getClanname();
                for (int i = 0; i < clanMember.length; i++) {
                    clanMember[i].sendPackets(new S_ServerMessage(ServerMessage.LEAVE_CLAN, player_name, clan_name));
                }
                ClearPlayerClanData(clan);
                clan.removeClanMember(player_name);
                new L1Teleport().teleport(this, getX(), getY(), getMapId(), getHeading(), false);
                save();
                saveInventory();
            } catch (Exception e) {
            }
        }

        if (getLevel() > 55 && getTitle().contains(Config.GAME_SERVER_NAME)) {
            setTitle("");
            sendPackets(new S_CharTitle(getId(), ""));
            broadcastPacket(new S_CharTitle(getId(), ""));
        }

        if (getLevel() == 99 && Config.ALT_REVIVAL_POTION) {
            try {
                L1Item l1item = ItemTable.getInstance().getTemplate(43000);
                if (l1item != null) {
                    getInventory().storeItem(43000, 1);
                    sendPackets(new S_ServerMessage(403, l1item.getName()));
                } else {
                    sendPackets(new S_SystemMessage("生まれ変わりのポーション入手に失敗しました。"));
                }
            } catch (Exception e) {
                _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
                sendPackets(new S_SystemMessage("生まれ変わりのポーション入手に失敗しました。"));
            }
        }

        for (int i = 0; i < gap; i++) {
            short randomHp = CalcStat.increaseHp(getType(), getAbility().getCon());
            int randomMp = CalcStat.increaseMp(getType(), getAbility().getWis());
            addBaseMaxHp(randomHp);
            addBaseMaxMp(randomMp);
        }

        this.setCurrentHp(getBaseMaxHp());
        this.setCurrentMp(getBaseMaxMp());
        resetBaseHitup();
        resetBaseAc();
        resetBaseMr();
        if (getLevel() > getHighLevel() && getReturnStat() == 0) {
            setHighLevel(getLevel());
        }

        try {
            save();
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }

        L1Quest quest = getQuest();
        // レベルアップ（クエストギフト外部化）1〜現在のレベルまで繰り返して検索
        int lv = getLevel();
        for (int _lv = 1; _lv <= lv; _lv++) {
            CharactersGiftItemTable.Item _levelItem = null;
            CharactersGiftItemTable.Item[] _levelItems = CharactersGiftItemTable.getInstance().getItems(_lv);
            if (_levelItems != null && _levelItems.length > 0) {
                int level_quest_step = quest.get_step(_lv);
                if (level_quest_step != L1Quest.QUEST_END) {
                    for (int i = 0; i < _levelItems.length; i++) {
                        _levelItem = _levelItems[i];
                        if (_levelItem == null)
                            continue;
                        if (_levelItem.getType() != getType())
                            continue;
                        createNewItem(this, _levelItem.getItemId(), _levelItem.getCount(), _levelItem.getEnchant(),
                                _levelItem.getAttrLevel(), _levelItem.getBless());
                    }
                    sendPackets(new S_ChatPacket("Level(" + _lv + ")クエストを完了しました。"));
                    getQuest().set_end(_lv);
                }
            }
        }
        // **** スナップファー開放メッセージ ****//
        int lv59_step = quest.get_step(L1Quest.QUEST_SLOT76);
        if (getLevel() == 59 && lv59_step != L1Quest.QUEST_END) {
            sendPackets(new S_SystemMessage("\\aA通知:あなたは [59]レベルイヤリング開放が可能となりました。"));
            this.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "お祝い：あなたは[59]レベル達成にスナップ広がりにピアス開放が可能です。"));
            this.sendPackets(new S_NewCreateItem(S_NewCreateItem.NEW_PACKET_10, 0));
        }
        int lv76_step = quest.get_step(L1Quest.QUEST_SLOT76);
        if (getLevel() == 76 && lv76_step != L1Quest.QUEST_END) {
            sendPackets(new S_SystemMessage("\\aA注意：あなたは、 [76]レベルリング開放が可能となりました。"));
            this.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "お祝い：あなたは[76]レベル達成にスナップ広がりにリング開放が可能です。"));
        }
        int lv81_step = quest.get_step(L1Quest.QUEST_SLOT81);
        if (getLevel() == 81 && lv81_step != L1Quest.QUEST_END) {
            sendPackets(new S_SystemMessage("\\aA注意：あなたは、 [81]レベルリング開放が可能となりました。"));
            this.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "お祝い：あなたは[81]レベル達成にスナップ広がりにリング開放が可能です。"));

        }
        // **** スナップファー開放メッセージ ****//

        if (getLevel() >= 51 && getLevel() - 50 > getBonusStats() && getAbility().getAmount() < 210) {
            int upstat = (getLevel() - 50) - (getBonusStats());
            String s = Integer.toString(upstat);
            sendPackets(new S_Message_YN(479, s));

        }
        if (getLevel() >= 99) { // 隠された渓谷、修練ケイブ
            if (getMapId() == 2005 || getMapId() >= 25 && getMapId() <= 28) {
                int[] loc = L1TownLocation.getGetBackLoc(L1TownLocation.TOWNID_SILVER_KNIGHT_TOWN);
                new L1Teleport().teleport(this, loc[0], loc[1], (short) loc[2], this.getHeading(), true);
            }
        }
        if (getLevel() >= 60) { // クラウディアレベル60まで、その後の自動テル
            if (getMapId() == 7783 || getMapId() == 12146 || getMapId() == 12149 || getMapId() == 12147
                    || getMapId() == 12148) {
                int[] loc = L1TownLocation.getGetBackLoc(L1TownLocation.TOWNID_GIRAN);
                new L1Teleport().teleport(this, loc[0], loc[1], (short) loc[2], this.getHeading(), true);

            }
        }
        if (getLevel() >= Config.DISCARDED_LAND_ENTRY_LEVEL) { // 本サーバーは、52レップまで可能である。
            if (getMapId() == 777) { // 捨てられた人々の地（影の神殿）
                new L1Teleport().teleport(this, 34043, 32184, (short) 4, 5, true);
            } else if (getMapId() == 778 || getMapId() == 779) {
                // 捨てられた人々の地（欲望の洞窟）
                new L1Teleport().teleport(this, 32608, 33178, (short) 4, 5, true); // WB
            } else if (getMapId() == 2010) {
                int[] loc = L1TownLocation.getGetBackLoc(L1TownLocation.TOWNID_SILVER_KNIGHT_TOWN);
                new L1Teleport().teleport(this, loc[0], loc[1], (short) loc[2], this.getHeading(), true);
            }
        }
        // CheckStatus();
        sendPackets(new S_OwnCharStatus(this));

        if (getLevel() == 1) {
            setEinhasad(2000000);
            sendPackets(new S_PacketBox(S_PacketBox.EINHASAD, getEinhasad()));
            if (getZoneType() == 1) {
                startEinhasadTimer();
            }
        }
    }

    private void levelDown(int gap) {
        resetLevel();

        for (int i = 0; i > gap; i--) {
            short randomHp = CalcStat.increaseHp(getType(), getAbility().getCon());
            int randomMp = CalcStat.increaseMp(getType(), getAbility().getWis());
            addBaseMaxHp((short) -randomHp);
            addBaseMaxMp((short) -randomMp);
        }
        resetBaseHitup();
        resetBaseAc();
        resetBaseMr();

        if (!isGm() && Config.LEVEL_DOWN_RANGE != 0) {
            if (getHighLevel() - getLevel() == Config.LEVEL_DOWN_RANGE - 1) {
                sendPackets(new S_SystemMessage("\\aG[警告]もう一度レベルダウン時のキャラクターが差し押さえされます。"));

            }
            if (!isGm() && getHighLevel() - getLevel() >= Config.LEVEL_DOWN_RANGE) {
                sendPackets(new S_ServerMessage(64));
                sendPackets(new S_Disconnect());
                _log.info(String.format("レベルダウンの許容範囲を超えたため％sを強制切断しました。", getName()));
            }
        }

        try {
            save();
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
        sendPackets(new S_OwnCharStatus(this));
    }

    public void beginGameTimeCarrier() {
        new L1GameTimeCarrier(this).start();
    }

    public boolean isGhost() {
        return _ghost;
    }

    private void setGhost(boolean flag) {
        _ghost = flag;
    }

    public boolean isGhostCanTalk() {
        return _ghostCanTalk;
    }

    private void setGhostCanTalk(boolean flag) {
        _ghostCanTalk = flag;
    }

    public boolean isReserveGhost() {
        return _isReserveGhost;
    }

    private void setReserveGhost(boolean flag) {
        _isReserveGhost = flag;
    }

    public void beginGhost() {
        if (!isGhost()) {
            setGhost(true);
            _ghostSaveLocX = getX();
            _ghostSaveLocY = getY();
            _ghostSaveMapId = getMapId();
            GhostController.getInstance().addMember(this);
        }
    }

    public void beginGhost(int locx, int locy, short mapid, boolean canTalk) {
        beginGhost(locx, locy, mapid, canTalk, 0);
    }

    public void beginGhost(int locx, int locy, short mapid, boolean canTalk, int sec) {
        if (isGhost()) {
            return;
        }
        _ghostSaveLocX = getX();
        _ghostSaveLocY = getY();
        _ghostSaveMapId = getMapId();
        _ghostSaveHeading = getHeading();
        setGhost(true);
        setGhostCanTalk(canTalk);
        setReserveGhost(false);
        new L1Teleport().teleport(this, locx, locy, mapid, 5, true);
        if (sec > 0) {
            _ghostFuture = GeneralThreadPool.getInstance().schedule(new L1PcGhostMonitor(getId()), sec * 1000);
        }
    }

    public void makeReadyEndGhost() {

        setGhost(false);
        setReserveGhost(true);
        new L1Teleport().teleport(this, _ghostSaveLocX, _ghostSaveLocY, (short) _ghostSaveMapId, 5, false);
        GhostController.getInstance().removeMember(this);

        // setReserveGhost(true);
        // L1Teleport.teleport(this, _ghostSaveLocX, _ghostSaveLocY,
        // _ghostSaveMapId, _ghostSaveHeading, true);
    }

    public void DeathMatchEndGhost() {
        setReserveGhost(true);
        new L1Teleport().teleport(this, 32614, 32735, (short) 4, 5, true);
    }

    public void endGhost() {
        setGhost(false);
        setGhostCanTalk(true);
        setReserveGhost(false);
    }

    public void beginHell(boolean isFirst) {
        if (getMapId() != 666) {
            int locx = 32701;
            int locy = 32777;
            short mapid = 666;
            new L1Teleport().teleport(this, locx, locy, mapid, 5, false);
        }

        if (isFirst) {
            if (get_PKcount() <= 10) {
                setHellTime(180);
            } else {
                setHellTime(300 * (get_PKcount() - 100) + 300);
            }
            sendPackets(new S_BlueMessage(552, String.valueOf(get_PKcount()), String.valueOf(getHellTime() / 60)));
        } else {
            sendPackets(new S_BlueMessage(637, String.valueOf(getHellTime())));
        }
        if (_hellFuture == null) {
            _hellFuture = GeneralThreadPool.getInstance().scheduleAtFixedRate(new L1PcHellMonitor(getId()), 0L, 1000L);
        }
    }

    public void endHell() {
        if (_hellFuture != null) {
            _hellFuture.cancel(false);
            _hellFuture = null;
        }
        int[] loc = L1TownLocation.getGetBackLoc(L1TownLocation.TOWNID_ORCISH_FOREST);
        new L1Teleport().teleport(this, loc[0], loc[1], (short) loc[2], 5, true);
        try {
            save();
        } catch (Exception ignore) {
        }
    }

    @Override
    public void setPoisonEffect(int effectId) {
        sendPackets(new S_Poison(getId(), effectId));
        if (!isGmInvis() && !isGhost() && !isInvisble()) {
            broadcastPacket(new S_Poison(getId(), effectId));
        }
    }

    @Override
    public void healHp(int pt) {
        super.healHp(pt);
        sendPackets(new S_HPUpdate(this));
    }

    // UI DG表示
    private int _Dg = 0;

    public void addDg(int i) {
        _Dg += i;
        sendPackets(new S_PacketBox(S_PacketBox.DODGE, _Dg));
    }

    public int getDg() {
        return _Dg;
    }

    @Override
    public int getKarma() {
        return _karma.get();
    }

    @Override
    public void setKarma(int i) {
        _karma.set(i);
    }

    public void addKarma(int i) {
        synchronized (_karma) {
            _karma.add(i);
        }
    }

    public int getKarmaLevel() {
        return _karma.getLevel();
    }

    public int getKarmaPercent() {
        return _karma.getPercent();
    }

    public Timestamp getLastPk() {
        return _lastPk;
    }

    public void setLastPk(Timestamp time) {
        _lastPk = time;
    }

    public void setLastPk() {
        _lastPk = new Timestamp(System.currentTimeMillis());
    }

    public boolean isWanted() {
        if (_lastPk == null) {
            return false;
        } else if (System.currentTimeMillis() - _lastPk.getTime() > 24 * 3600 * 1000) {
            setLastPk(null);
            return false;
        }
        return true;
    }

    public Timestamp getDeleteTime() {
        return _deleteTime;
    }

    public void setDeleteTime(Timestamp time) {
        _deleteTime = time;
    }

    public Timestamp getLastLoginTime() {
        return _lastLoginTime;
    }

    public void setLastLoginTime(Timestamp time) {
        _lastLoginTime = time;
    }

    public int getEinhasad() {
        return _einhasad;
    }

    public void calEinhasad(int i) {
        int calc = _einhasad + i;
        if (calc >= 15000000)
            calc = 15000000;
        _einhasad = calc;
    }

    public void setEinhasad(int einhasad) {
        _einhasad = einhasad;
    }

    private int _emerald;

    public int getEmerald() {
        return _emerald;
    }

    public void calEmerald(int i) {
        int calc = _emerald + i;
        if (calc >= 7000000)
            calc = 7000000;
        _emerald = calc;
    }

    public void setEmerald(int i) {
        _emerald = i;
    }

    @Override
    public int getMagicLevel() {
        return getClassFeature().getMagicLevel(getLevel());
    }

    public int getWeightReduction() {
        return _weightReduction;
    }

    public void addWeightReduction(int i) {
        _weightReduction += i;
        this.sendPackets(new S_Weight(this));
    }

    public int getHasteItemEquipped() {
        return _hasteItemEquipped;
    }

    public void addHasteItemEquipped(int i) {
        _hasteItemEquipped += i;
    }

    public void removeHasteSkillEffect() {
        if (hasSkillEffect(L1SkillId.SLOW))
            removeSkillEffect(L1SkillId.SLOW);
        if (hasSkillEffect(L1SkillId.GREATER_SLOW))
            removeSkillEffect(L1SkillId.GREATER_SLOW);
        if (hasSkillEffect(L1SkillId.ENTANGLE))
            removeSkillEffect(L1SkillId.ENTANGLE);
        if (hasSkillEffect(L1SkillId.HASTE))
            removeSkillEffect(L1SkillId.HASTE);
        if (hasSkillEffect(L1SkillId.GREATER_HASTE))
            removeSkillEffect(L1SkillId.GREATER_HASTE);
        if (hasSkillEffect(L1SkillId.STATUS_HASTE))
            removeSkillEffect(L1SkillId.STATUS_HASTE);
    }

    private Timestamp _tamTime;

    public Timestamp getTamTime() {
        return _tamTime;
    }

    public void setTamTime(Timestamp time) {
        _tamTime = time;
    }

    private int _tamreserve;

    public int getTamReserve() {
        return _tamreserve;
    }

    public void setTamReserve(int i) {
        _tamreserve = i;
    }

    private boolean returnStatus = false;
    private boolean returnStatus_Start = false;
    private boolean returnStatus_Levelup = false;

    public boolean isReturnStatus() {
        return returnStatus;
    }

    public void setReturnStatus(boolean returnStatus) {
        this.returnStatus = returnStatus;
    }

    public boolean isReturnStatus_Start() {
        return returnStatus_Start;
    }

    public void setReturnStatus_Start(boolean returnStatus_Start) {
        this.returnStatus_Start = returnStatus_Start;
    }

    public boolean isReturnStatus_Levelup() {
        return returnStatus_Levelup;
    }

    public void setReturnStatus_Levelup(boolean returnStatus_Levelup) {
        this.returnStatus_Levelup = returnStatus_Levelup;
    }

    /*
     * public void resetBaseDmgup() { int newBaseDmgup = 0; int newBaseBowDmgup
     * = 0; int newBaseStatDmgup =
     * CalcStat.calcDmgup(getAbility().getBaseStr()); int newBaseStatBowDmgup =
     * CalcStat.calcBowDmgup(getAbility().getBaseDex()); if (isKnight() ||
     * isDragonknight() || isDarkelf()) { newBaseDmgup = getLevel() / 10;
     * newBaseBowDmgup = 0; } else if (isElf()) { newBaseDmgup = 0;
     * newBaseBowDmgup = getLevel() / 10; } addDmgup((newBaseDmgup +
     * newBaseStatDmgup) - _baseDmgup); addBowDmgup((newBaseBowDmgup +
     * newBaseStatBowDmgup) - _baseBowDmgup); _baseDmgup = newBaseDmgup +
     * newBaseStatDmgup; _baseBowDmgup = newBaseBowDmgup + newBaseStatBowDmgup;
     * }
     */

    public void resetBaseHitup() {
        int newBaseHitup = 0;
        int newBaseBowHitup = 0;
        int newBaseStatHitup = CalcStat.calcHitup(getAbility().getBaseStr());
        int newBaseStatBowHitup = CalcStat.calcBowHitup(getAbility().getBaseDex());

        if (isCrown()) {
            newBaseHitup = getLevel() / 5;
            newBaseBowHitup = getLevel() / 5;
        } else if (isKnight()) {
            newBaseHitup = getLevel() / 3;
            newBaseBowHitup = getLevel() / 3;
        } else if (isElf()) {
            newBaseHitup = getLevel() / 5;
            newBaseBowHitup = getLevel() / 5;
        } else if (isDarkelf()) {
            newBaseHitup = getLevel() / 3;
            newBaseBowHitup = getLevel() / 3;
        } else if (isDragonknight()) {
            newBaseHitup = getLevel() / 3;
            newBaseBowHitup = getLevel() / 3;
        }
        addHitup((newBaseHitup + newBaseStatHitup) - _baseHitup);
        addBowHitup((newBaseBowHitup + newBaseStatBowHitup) - _baseBowHitup);
        _baseHitup = newBaseHitup + newBaseStatHitup;
        _baseBowHitup = newBaseBowHitup + newBaseStatBowHitup;
    }

    public void resetOriginalMagicHit() {
        int originalInt = pc.getTotalInt();
        if (isCrown()) {
            if (originalInt == 12 || originalInt == 13) {
                _originalMagicHit = 1;
            } else if (originalInt >= 14) {
                _originalMagicHit = 2;
            } else {
                _originalMagicHit = 0;
            }
        } else if (isKnight()) {
            if (originalInt == 10 || originalInt == 11) {
                _originalMagicHit = 1;
            } else if (originalInt == 12) {
                _originalMagicHit = 2;
            } else {
                _originalMagicHit = 0;
            }
        } else if (isElf()) {
            if (originalInt == 13 || originalInt == 14) {
                _originalMagicHit = 1;
            } else if (originalInt >= 15) {
                _originalMagicHit = 2;
            } else {
                _originalMagicHit = 0;
            }
        } else if (isDarkelf()) {
            if (originalInt == 12 || originalInt == 13) {
                _originalMagicHit = 1;
            } else if (originalInt >= 14) {
                _originalMagicHit = 2;
            } else {
                _originalMagicHit = 0;
            }
        } else if (isWizard()) {
            if (originalInt >= 14) {
                _originalMagicHit = 1;
            } else {
                _originalMagicHit = 0;
            }
        } else if (isDragonknight()) {
            if (originalInt == 12 || originalInt == 13) {
                _originalMagicHit = 2;
            } else if (originalInt == 14 || originalInt == 15) {
                _originalMagicHit = 3;
            } else if (originalInt >= 16) {
                _originalMagicHit = 4;
            } else {
                _originalMagicHit = 0;
            }
        } else if (isBlackwizard()) {
            if (originalInt >= 13) {
                _originalMagicHit = 1;
            } else {
                _originalMagicHit = 0;
            }
        } else if (isWarrior()) {
            if (originalInt == 12 || originalInt == 13) {
                _originalMagicHit = 1;
            } else if (originalInt == 14) {
                _originalMagicHit = 2;
            } else {
                _originalMagicHit = 0;
            }
        }
    }

    public void resetBaseAc() {
        int newAc = 10 + CalcStat.calcAc(getAbility().getDex());
        if (_type == 3)
            newAc -= getLevel() / 8;
        else if (_type == 4)
            newAc -= getLevel() / 6;
        else
            newAc -= getLevel() / 7;
        ac.addAc(newAc - _baseAc);
        _baseAc = newAc;
        sendPackets(new S_OwnCharAttrDef(this));
    }

    public void resetBaseMr() {
        resistance.setBaseMr(CalcStat.calcStatMr(_type, getAbility().getTotalWis()));
        sendPackets(new S_SPMR(this));
    }

    public void resetLevel() {
        setLevel(ExpTable.getLevelByExp(_exp));
        updateLevel();
    }

    public void updateLevel() {
        final int lvlTable[] = new int[] { 30, 25, 20, 16, 14, 12, 11, 10, 9, 3, 2 };

        int regenLvl = Math.min(10, getLevel());
        if (30 <= getLevel() && isKnight()) {
            regenLvl = 11;
        }

        synchronized (this) {// ピチク速度調節
            setHpregenMax(lvlTable[regenLvl - 1] * 12);
        }
    }

    public void refresh() {
        CheckChangeExp();
        resetLevel();
        resetBaseHitup();
        resetBaseMr();
        resetBaseAc();
    }

    public void checkChatInterval() {
        long nowChatTimeInMillis = System.currentTimeMillis();
        if (_chatCount == 0) {
            _chatCount++;
            _oldChatTimeInMillis = nowChatTimeInMillis;
            return;
        }

        long chatInterval = nowChatTimeInMillis - _oldChatTimeInMillis;
        if (chatInterval > 2000) {
            _chatCount = 0;
            _oldChatTimeInMillis = 0;
        } else {
            if (_chatCount >= 3) {
                setSkillEffect(L1SkillId.STATUS_CHAT_PROHIBITED, 120 * 1000);
                sendPackets(new S_SkillIconGFX(36, 120));
                sendPackets(new S_ServerMessage(153));
                _chatCount = 0;
                _oldChatTimeInMillis = 0;
            }
            _chatCount++;
        }
    }

    // 範囲外になった認識済みオブジェクトを削除（バグベアーレース）
    private void removeOutOfRangeObjects(int distance) {
        try {
            List<L1Object> known = getKnownObjects();
            for (int i = 0; i < known.size(); i++) {
                if (known.get(i) == null) {
                    continue;
                }

                L1Object obj = known.get(i);
                if (!getLocation().isInScreen(obj.getLocation())
                        || (obj instanceof L1NpcInstance && ((L1NpcInstance) obj).isDestroyed())
                        || (obj instanceof L1PcInstance && ((L1PcInstance) obj)._destroyed)) { // 範囲外となる距離
                    removeKnownObject(obj);
                    sendPackets(new S_RemoveObject(obj));
                }
            }
        } catch (Exception e) {
            System.out.println("removeOutOfRangeObjectsエラー：" + e);
        }
    }

    // オブジェクト認識処理（バグベアーレース）
    public void UpdateObject() {
        try {
            try {
                removeOutOfRangeObjects(17);
            } catch (Exception e) {
                System.out.println("removeOutOfRangeObjects（17）エラー：" + e);
            }

            // 画面内のオブジェクトのリストを作成
            ArrayList<L1Object> visible2 = L1World.getInstance().getVisibleObjects(this);
            L1NpcInstance npc = null;
            for (L1Object visible : visible2) {
                if (visible == null) {
                    continue;
                }
                if (!knownsObject(visible)) {
                    visible.onPerceive(this);
                } else {
                    if (visible instanceof L1NpcInstance) {
                        npc = (L1NpcInstance) visible;
                        if (npc.getHiddenStatus() != 0) {
                            npc.approachPlayer(this);
                        }
                    }

                }
            }
        } catch (Exception e) {
            System.out.println("UpdateObject（）エラー：" + e);
        }
    }

    public void CheckChangeExp() {
        int level = ExpTable.getLevelByExp(getExp());
        int char_level = CharacterTable.getInstance().PcLevelInDB(getId());
        if (char_level == 0) { // 0であれば、..エラーラゲッジ？
            return; // それからちょうどリターン
        }
        int gap = level - char_level;
        if (gap == 0) {
            sendPackets(new S_OwnCharStatus(this));
            // sendPackets(new S_Exp(this));
            int percent = ExpTable.getExpPercentage(char_level, getExp());
            if (char_level >= 60 && char_level <= 64) {
                if (percent >= 10)
                    removeSkillEffect(L1SkillId.LEVEL_UP_BONUS);
            } else if (char_level >= 65) {
                if (percent >= 5) {
                    removeSkillEffect(L1SkillId.LEVEL_UP_BONUS);
                }
            }
            return;
        }

        // レベルが変化した場合
        if (gap > 0) {
            levelUp(gap);
            if (getLevel() >= 60) {
                setSkillEffect(L1SkillId.LEVEL_UP_BONUS, 10800000);
                sendPackets(new S_PacketBox(10800, true, true), true);
            }
        } else if (gap < 0) {
            levelDown(gap);
            removeSkillEffect(L1SkillId.LEVEL_UP_BONUS);
        }
    }

    public void LoadCheckStatus() {
        int totalS = getAbility().getAmount();
        int bonusS = getHighLevel() - 50;
        if (bonusS < 0) {
            bonusS = 0;
        }

        int calst = totalS - (bonusS + getElixirStats() + 75);

        if (calst > 0 && !isGm()) {
            L1SkillUse l1skilluse = new L1SkillUse();
            l1skilluse.handleCommands(this, L1SkillId.CANCELLATION, getId(), getX(), getY(), null, 0,
                    L1SkillUse.TYPE_LOGIN);

            if (getWeapon() != null) {
                getInventory().setEquipped(getWeapon(), false, false, false, false);
            }

            sendPackets(new S_CharVisualUpdate(this));
            sendPackets(new S_OwnCharStatus2(this));

            for (L1ItemInstance armor : getInventory().getItems()) {
                for (int type = 0; type <= 12; type++) {
                    if (armor != null) {
                        getInventory().setEquipped(armor, false, false, false, false);
                    }
                }
            }

            setReturnStat(getExp());
            sendPackets(new S_SPMR(this));
            sendPackets(new S_OwnCharAttrDef(this));
            sendPackets(new S_OwnCharStatus2(this));
            sendPackets(new S_ReturnedStat(this, S_ReturnedStat.START));
            try {
                save();
            } catch (Exception e) {
                _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            }
        }
    }

    public void CheckStatus() {
        int totalS = ability.getAmount();
        int bonusS = getLevel() - 50;
        if (bonusS < 0) {
            bonusS = 0;
        }

        int calst = totalS - (bonusS + getElixirStats() + 75);

        if (calst > 0 && !isGm()) {
            L1SkillUse l1skilluse = new L1SkillUse();
            l1skilluse.handleCommands(this, L1SkillId.CANCELLATION, getId(), getX(), getY(), null, 0,
                    L1SkillUse.TYPE_LOGIN);

            if (getWeapon() != null) {
                getInventory().setEquipped(getWeapon(), false, false, false, false);
            }

            sendPackets(new S_CharVisualUpdate(this));
            sendPackets(new S_OwnCharStatus2(this));

            for (L1ItemInstance armor : getInventory().getItems()) {
                for (int type = 0; type <= 12; type++) {
                    if (armor != null) {
                        getInventory().setEquipped(armor, false, false, false, false);
                    }
                }
            }

            setReturnStat(getExp());
            sendPackets(new S_SPMR(this));
            sendPackets(new S_OwnCharAttrDef(this));
            sendPackets(new S_OwnCharStatus2(this));
            sendPackets(new S_ReturnedStat(this, S_ReturnedStat.START));
            try {
                save();
            } catch (Exception e) {
                _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            }
        }
    }

    public long TamTime() {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        Timestamp tamtime = null;
        long time = 0;
        long sysTime = System.currentTimeMillis();
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement(
                    "SELECT `TamEndTime` FROM `characters` WHERE account_name = ? ORDER BY `TamEndTime` ASC"); // キャラクター
            pstm.setString(1, getAccountName());
            rs = pstm.executeQuery();
            while (rs.next()) {
                tamtime = rs.getTimestamp("TamEndTime");
                if (tamtime != null) {
                    if (sysTime < tamtime.getTime()) {
                        time = tamtime.getTime() - sysTime;
                        break;
                    }
                }
            }
            return time;
        } catch (Exception e) {
            e.printStackTrace();
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            return time;
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    private int rankLevel;

    public int getRankLevel() {
        return rankLevel;
    }

    public void setRankLevel(int i) {
        rankLevel = i;
    }

    public int tamcount() {
        Connection con = null;
        Connection con2 = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        PreparedStatement pstm2 = null;
        Timestamp tamtime = null;
        int count = 0;
        long sysTime = System.currentTimeMillis();
        int char_objid = 0;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `characters` WHERE account_name = ?"); // キャラクターテーブルで選ん来
            pstm.setString(1, getAccountName());
            rs = pstm.executeQuery();
            while (rs.next()) {
                tamtime = rs.getTimestamp("TamEndTime");
                char_objid = rs.getInt("objid");
                if (tamtime != null) {
                    if (sysTime <= tamtime.getTime()) {
                        count++;
                    } else {
                        if (Tam_wait_count(char_objid) != 0) {
                            int day = Nexttam(char_objid);
                            if (day != 0) {
                                Timestamp deleteTime = null;
                                deleteTime = new Timestamp(sysTime + (86400000 * (long) day) + 10000);// 7日
                                // deleteTime = new Timestamp(sysTime +
                                // 1000*60);//7日

                                if (getId() == char_objid) {
                                    setTamTime(deleteTime);
                                }
                                con2 = L1DatabaseFactory.getInstance().getConnection();
                                pstm2 = con2.prepareStatement(
                                        "UPDATE `characters` SET TamEndTime=? WHERE account_name = ? AND objid = ?"); // キャラクターテーブルで君主だけ上がってきて
                                pstm2.setTimestamp(1, deleteTime);
                                pstm2.setString(2, getAccountName());
                                pstm2.setInt(3, char_objid);
                                pstm2.executeUpdate();
                                tamdel(char_objid);
                                count++;
                            }
                        }
                    }
                }
            }
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            return count;
        } finally {
            SQLUtil.close(pstm2);
            SQLUtil.close(con2);
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void tamdel(int objectId) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("delete from Tam where objid = ? order by id asc limit 1");
            pstm.setInt(1, objectId);
            pstm.executeUpdate();
        } catch (SQLException e) {
            // _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public int Nexttam(int objectId) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        int day = 0;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT day FROM `tam` WHERE objid = ? order by id asc limit 1"); // キャラクターテーブルで君主だけを選んで来て
            pstm.setInt(1, objectId);
            rs = pstm.executeQuery();
            while (rs.next()) {
                day = rs.getInt("Day");
            }
        } catch (SQLException e) {
            // _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return day;
    }

    public int Tam_wait_count(int charid) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        int count = 0;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `tam` WHERE objid = ?");
            pstm.setInt(1, charid);
            rs = pstm.executeQuery();
            while (rs.next()) {
                count = getId();
            }
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            return count;
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void cancelAbsoluteBarrier() { // アブ小ガルトバリアの解除
        if (hasSkillEffect(ABSOLUTE_BARRIER)) {
            killSkillEffectTimer(ABSOLUTE_BARRIER);
            this.startHpRegenerationByDoll();
            this.startMpRegenerationByDoll();
            this.startMpRegenerationByDoll();
        }
    }

    public int get_PKcount() {
        return _PKcount;
    }

    public void set_PKcount(int i) {
        _PKcount = i;
    }

    public int getClanid() {
        return _clanid;
    }

    public void setClanid(int i) {
        _clanid = i;
    }

    public String getClanname() {
        return clanname;
    }

    public void setClanname(String s) {
        clanname = s;
    }

    private String _sealingPW; //

    public String TempQuiz = "";

    public String getSealingPW() {
        return _sealingPW;
    }

    public void setSealingPW(String s) {
        _sealingPW = s;
    }

    int _sealScrollTime;

    public void setSealScrollTime(int sealScrollTime) {
        _sealScrollTime = sealScrollTime;
    }

    public int getSealScrollTime() {
        return _sealScrollTime;
    }

    int _sealScrollCount;

    public void setSealScrollCount(int sealScrollCount) {
        _sealScrollCount = sealScrollCount;
    }

    public int getSealScrollCount() {
        return _sealScrollCount;
    }

    public L1Clan getClan() {
        return L1World.getInstance().getClan(getClanname());
    }

    public int getClanRank() {
        return _clanRank;
    }

    public void setClanRank(int i) {
        _clanRank = i;
    }

    public byte get_sex() {
        return _sex;
    }

    public void set_sex(int i) {
        _sex = (byte) i;
    }

    /**
     * 年齢設定
     **/
    public int getAge() {
        return _age;
    }

    public void setAge(int i) {
        _age = i;
    }

    /**
     * 年齢設定
     **/

    public boolean isGm() {
        return _gm;
    }

    public void setGm(boolean flag) {
        _gm = flag;
    }

    public boolean isMonitor() {
        return _monitor;
    }

    public void setMonitor(boolean flag) {
        _monitor = flag;
    }

    public int getDamageReductionByArmor() {
        return _damageReductionByArmor;
    }

    public void addDamageReductionByArmor(int i) {
        _damageReductionByArmor += i;
    }

    public int get_regist_PVPweaponTotalDamage() {
        return _regist_PVPweaponTotalDamage;
    }

    public void addregist_PVPweaponTotalDamage(int i) {
        _regist_PVPweaponTotalDamage += i;
    }

    public int getBowDmgRate() {
        return _bowDmgRate;
    }

    public void addBowDmgRate(int i) {
        _bowDmgRate += i;
    }

    public int getDmgRate() {
        return _DmgRate;
    }

    public void addDmgRate(int i) {
        _DmgRate += i;
    }

    public int getBowHitRate() {
        return _bowHitRate;
    }

    public void addBowHitRate(int i) {
        _bowHitRate += i;
    }

    public int getHitRate() {
        return _HitRate;
    }

    public void addHitRate(int i) {
        _HitRate += i;
    }

    public int getDmgupByArmor() {
        return _DmgupByArmor;
    }

    public void addDmgupByArmor(int i) {
        _DmgupByArmor += i;
    }

    public int getBowDmgupByArmor() {
        return _bowDmgupByArmor;
    }

    public void addBowDmgupByArmor(int i) {
        _bowDmgupByArmor += i;
    }

    private void setGresValid(boolean valid) {
        _gresValid = valid;
    }

    public boolean isGresValid() {
        return _gresValid;
    }

    public int getMagicHitupByArmor() {
        return _MagicHitupByArmor;
    }

    public void addMagicHitupByArmor(int i) {
        _MagicHitupByArmor += i;
    }

    public long getFishingTime() {
        return _fishingTime;
    }

    public void setFishingTime(long i) {
        _fishingTime = i;
    }

    public boolean isFishing() {
        return _isFishing;
    }

    public boolean isFishingReady() {
        return _isFishingReady;
    }

    public void setFishing(boolean flag) {
        _isFishing = flag;
    }

    public void setFishingReady(boolean flag) {
        _isFishingReady = flag;
    }

    public int getCookingId() {
        return _cookingId;
    }

    public void setCookingId(int i) {
        _cookingId = i;
    }

    public int getDessertId() {
        return _dessertId;
    }

    public void setDessertId(int i) {
        _dessertId = i;
    }

    public AcceleratorChecker getAcceleratorChecker() {
        return _acceleratorChecker;
    }

    private int _HpregenMax = 0;

    public int getHpregenMax() {
        return _HpregenMax;
    }

    public void setHpregenMax(int num) {
        this._HpregenMax = num;
    }

    private int _HpregenPoint = 0;

    public int getHpregenPoint() {
        return _HpregenPoint;
    }

    public void setHpregenPoint(int num) {
        this._HpregenPoint = num;
    }

    public void addHpregenPoint(int num) {
        this._HpregenPoint += num;
    }

    private int _HpcurPoint = 4;

    public int getHpcurPoint() {
        return _HpcurPoint;
    }

    public void setHpcurPoint(int num) {
        this._HpcurPoint = num;
    }

    private int _MpregenMax = 0;

    public int getMpregenMax() {
        return _MpregenMax;
    }

    public void setMpregenMax(int num) {
        this._MpregenMax = num;
    }

    private int _MpregenPoint = 0;

    public int getMpregenPoint() {
        return _MpregenPoint;
    }

    public void setMpregenPoint(int num) {
        this._MpregenPoint = num;
    }

    public void addMpregenPoint(int num) {
        this._MpregenPoint += num;
    }

    private int _MpcurPoint = 4;

    public int getMpcurPoint() {
        return _MpcurPoint;
    }

    public void setMpcurPoint(int num) {
        this._MpcurPoint = num;
    }

    /**
     * パッケージ店
     **/
    private int CashStep = 0;

    public int getCashStep() {
        return CashStep;
    }

    public void setCashStep(int cashStep) {
        CashStep = cashStep;
    }

    /** パッケージ店 **/

    /**
     * ロボットを開始
     **/
    private int teleportTime = 0;
    private int teleportTime2 = 0;
    private int skillTime = 0;
    private int skillTime2 = 0;
    private long _quiztime = 0;
    private long _quiztime2 = 0;
    private long _quiztime3 = 0;
    // private int currentTeleportCount = 0;
    private int currentSkillCount = 0;
    private int currentSkillCount2 = 0;

    // コンボシステム
    private int comboCount;

    public long getQuizTime() {
        return _quiztime;
    }

    public void setQuizTime(long l) {
        _quiztime = l;
    }

    public long getQuizTime2() {
        return _quiztime2;
    }

    public void setQuizTime2(long l) {
        _quiztime2 = l;
    }

    public long getQuizTime3() {
        return _quiztime3;
    }

    public void setQuizTime3(long l) {
        _quiztime3 = l;
    }

    public int getTeleportTime() {
        return teleportTime;
    }

    public void setTeleportTime(int teleportTime) {
        this.teleportTime = teleportTime;
    }

    public int getTeleportTime2() {
        return teleportTime2;
    }

    public void setTeleportTime2(int teleportTime2) {
        this.teleportTime2 = teleportTime2;
    }

    public int getSkillTime2() {
        return skillTime2;
    }

    public void setSkillTime2(int skillTime2) {
        this.skillTime2 = skillTime2;
    }

    public int getSkillTime() {
        return skillTime;
    }

    public void setSkillTime(int skillTime) {
        this.skillTime = skillTime;
    }

    /*
     * public int getCurrentTeleportCount() { return currentTeleportCount; }
     *
     * public void setCurrentTeleportCount(int currentTeleportCount) {
     * this.currentTeleportCount = currentTeleportCount; }
     */

    public int getCurrentSkillCount() {
        return currentSkillCount;
    }

    public void setCurrentSkillCount(int currentSkillCount) {
        this.currentSkillCount = currentSkillCount;
    }

    public int getCurrentSkillCount2() {
        return currentSkillCount2;
    }

    public void setCurrentSkillCount2(int currentSkillCount2) {
        this.currentSkillCount2 = currentSkillCount2;
    }

    /**
     * ロボット終了
     **/

    public int getTeleportX() {
        return _teleportX;
    }

    public void setTeleportX(int i) {
        _teleportX = i;
    }

    public int getTeleportY() {
        return _teleportY;
    }

    public void setTeleportY(int i) {
        _teleportY = i;
    }

    public short getTeleportMapId() {
        return _teleportMapId;
    }

    public void setTeleportMapId(short i) {
        _teleportMapId = i;
    }

    public int getTeleportHeading() {
        return _teleportHeading;
    }

    public void setTeleportHeading(int i) {
        _teleportHeading = i;
    }

    public int getTempCharGfxAtDead() {
        return _tempCharGfxAtDead;
    }

    public void setTempCharGfxAtDead(int i) {
        _tempCharGfxAtDead = i;
    }

    public boolean isCanWhisper() {
        return _isCanWhisper;
    }

    public void setCanWhisper(boolean flag) {
        _isCanWhisper = flag;
    }

    public boolean isShowTradeChat() {
        return _isShowTradeChat;
    }

    public void setShowTradeChat(boolean flag) {
        _isShowTradeChat = flag;
    }

    public boolean isShowWorldChat() {
        return _isShowWorldChat;
    }

    public void setShowWorldChat(boolean flag) {
        _isShowWorldChat = flag;
    }

    public int getFightId() {
        return _fightId;
    }

    public void setFightId(int i) {
        _fightId = i;
    }

    public void setDeathMatch(boolean i) {
        this.isDeathMatch = i;
    }

    public boolean isDeathMatch() {
        return isDeathMatch;
    }

    public boolean isSupporting() {
        return _isSupporting;
    }

    public void setSupporting(boolean flag) {
        _isSupporting = flag;
    }

    public int getCallClanId() {
        return _callClanId;
    }

    public void setCallClanId(int i) {
        _callClanId = i;
    }

    public int getCallClanHeading() {
        return _callClanHeading;
    }

    public void setCallClanHeading(int i) {
        _callClanHeading = i;
    }

    /**
     * バフォメットシステム
     **/
    private int _nbapoLevel;
    private int _obapoLevel;
    private int _bapodmg;
    public int LawfulAC = 0;
    public int LawfulMR = 0;
    public int LawfulSP = 0;
    public int LawfulAT = 0;

    public int getBapodmg() {
        return _bapodmg;
    }

    public void setBapodmg(int i) {
        _bapodmg = i;
    }

    public int getNBapoLevel() {
        return _nbapoLevel;
    }

    public void setNBapoLevel(int i) {
        _nbapoLevel = i;
    }

    public int getOBapoLevel() {
        return _obapoLevel;
    }

    public void setOBapoLevel(int i) {
        _obapoLevel = i;
    }

    /**
     * コマバフ開始
     **/
    private int _deathmatch;

    public int getDeathMatchPiece() {
        return _deathmatch;
    }

    public void setDeathMatchPiece(int i) {
        _deathmatch = i;
    }

    private int _petrace;

    public int getPetRacePiece() {
        return _petrace;
    }

    public void setPetRacePiece(int i) {
        _petrace = i;
    }

    private int _ultimatebattle;

    public int getUltimateBattlePiece() {
        return _ultimatebattle;
    }

    public void setUltimateBattlePiece(int i) {
        _ultimatebattle = i;
    }

    private int _petmatch;

    public int getPetMatchPiece() {
        return _petmatch;
    }

    public void setPetMatchPiece(int i) {
        _petmatch = i;
    }

    private int _ghosthouse;

    public int getGhostHousePiece() {
        return _ghosthouse;
    }

    public void setGhostHousePiece(int i) {
        _ghosthouse = i;
    }

    /** コマバフ終わり **/
    /**
     * エンチャントバグ例外処理
     */
    private int _enchantitemid = 0;

    public int getLastEnchantItemid() {
        return _enchantitemid;
    }

    /**
     * アクセサリーエンチャントリニューアル
     **/
    public int _accessoryHeal = 0;

    public int getAccessoryHeal() {
        return _accessoryHeal;
    }

    public void setAccessoryHeal(int i) {
        _accessoryHeal = i;
    }

    public void addAccessoryHeal() {
        _accessoryHeal += 1;
    }

    public int _TrueTarget = 0;

    public int getTrueTarget() {
        return _TrueTarget;
    }

    public void setTrueTarget(int i) {
        _TrueTarget = i;
    }

    public void setLastEnchantItemid(int i, L1ItemInstance item) {
        // もし分からない防止臨時追加
        if (getLastEnchantItemid() == i && i != 0) {
            sendPackets(new S_Disconnect());
            getInventory().removeItem(item, item.getCount());
            return;
        }
        _enchantitemid = i;
    }

    private int girandungeon;

    public int getGirandungeonTime() {
        return girandungeon;
    }

    public void setGirandungeonTime(int giran) {
        girandungeon = giran;
    }

    private int orendungeon;

    public int getOrendungeonTime() {
        return orendungeon;
    }

    public void setOrendungeonTime(int oren) {
        orendungeon = oren;
    }

    // 氷のダンジョンPC
    private int icedungeon;

    public int geticedungeonTime() {
        return icedungeon;
    }

    public void seticedungeonTime(int oren) {
        icedungeon = oren;
    }

    // バルログ陣営
    private int newdodungeon;

    public int getnewdodungeonTime() {
        return newdodungeon;
    }

    public void setnewdodungeonTime(int oren) {
        newdodungeon = oren;
    }

    //
    private int radungeon;

    public int getRadungeonTime() {
        return radungeon;
    }

    public void setRadungeonTime(int ra) {
        radungeon = ra;
    }

    private int dragondungeon;

    public int getDrageonTime() {
        return dragondungeon;
    }

    public void setDrageonTime(int drageon) {
        dragondungeon = drageon;
    }

    private int islanddungeon;

    public int getislandTime() {
        return islanddungeon;
    }

    public void setislandTime(int oren) {
        islanddungeon = oren;
    }

    private int someon;

    public int getSomeTime() {
        return someon;
    }

    public void setSomeTime(int some) {
        someon = some;
    }

    /**
     * 精霊の墓
     **/
    private int soulon;

    public int getSoulTime() {
        return soulon;
    }

    public void setSoulTime(int soul) {
        soulon = soul;
    }

    /**
     * ポーション回復量
     **/
    private int _potionRecoveryRate = 0;

    public int getPotionRecoveryRatePct() {
        return _potionRecoveryRate;
    }

    public void addPotionRecoveryRatePct(int i) {
        _potionRecoveryRate += i;
    }

    /** ポーション回復量 **/

    /**
     * ミニゲーム
     **/
    // サイコロ
    private boolean _isGambling = false;
    private boolean _isGambleReady = false;

    public boolean isGambleReady() {
        return _isGambleReady;
    }

    public void setGambleReady(boolean flag) {
        _isGambleReady = flag;
    }

    public boolean isGambling() {
        return _isGambling;
    }

    public void setGambling(boolean flag) {
        _isGambling = flag;
    }

    private int _gamblingmoney = 0;

    public int getGamblingMoney() {
        return _gamblingmoney;
    }

    public void setGamblingMoney(int i) {
        _gamblingmoney = i;
    }

    // ソマク
    private boolean _isGambling3 = false;

    public boolean isGambling3() {
        return _isGambling3;
    }

    public void setGambling3(boolean flag) {
        _isGambling3 = flag;
    }

    private int _gamblingmoney3 = 0;

    public int getGamblingMoney3() {
        return _gamblingmoney3;
    }

    public void setGamblingMoney3(int i) {
        _gamblingmoney3 = i;
    }

    private int monsterkill = 0;

    public int getMonsterkill() {
        return monsterkill;
    }

    public void setMonsterkill(int monster) {
        monsterkill = monster;
        sendPackets(new S_OwnCharStatus(this));
    }

    public void addMonsterKill(int i) {
        monsterkill += i;
        sendPackets(new S_OwnCharStatus(this));
    }

    private ArrayList<String> _cmalist = new ArrayList<String>();

    /**
     * クランマッチングアプリケーションは、要求リストユーザが使用時の配列に血盟の名前を入れて君主が使用時の配列に申請者の名前を入れる。
     */
    public void addCMAList(String name) {
        if (_cmalist.contains(name)) {
            return;
        }
        _cmalist.add(name);
    }

    public void removeCMAList(String name) {
        if (!_cmalist.contains(name)) {
            return;
        }
        _cmalist.remove(name);
    }

    public ArrayList<String> getCMAList() {
        return _cmalist;
    }

    private int _clanMemberId;

    public int getClanMemberId() {
        return _clanMemberId;
    }

    public void setClanMemberId(int i) {
        _clanMemberId = i;
    }

    private String _clanMemberNotes = "";

    public String getClanMemberNotes() {
        return _clanMemberNotes;
    }

    public void setClanMemberNotes(String s) {
        _clanMemberNotes = s;
    }

    // アーノルドイベントドロップ
    private void drop() {
        if (getInventory().checkEquipped(21095)) { // 着用したアイテム
            L1ItemInstance drop = ItemTable.getInstance().createItem(30145); // ドロップさせるアイテム
            for (L1ItemInstance item : getInventory().getItems()) {
                if (item.getItemId() == 21095 & item.isEquipped()) {
                    sendPackets(new S_ServerMessage(3802));
                    sendPackets(new S_ServerMessage(158, "$22251"));
                    getInventory().removeItem(item, 1);
                    L1World.getInstance().getInventory(getX(), getY(), getMapId()).storeItem(drop);
                    break;
                }
            }
        }
    }

    // 古代のが号
    private void drop1() {
        if (getInventory().checkEquipped(900022)) { // 着用したアイテム
            for (L1ItemInstance item : getInventory().getItems()) {
                if (item.getItemId() == 900022 & item.isEquipped()) {
                    sendPackets(new S_ServerMessage(3802));
                    getInventory().removeItem(item, 1);
                    break;
                }
            }
        }
    }

    // サキュバスクイーンの契約
    private void drop2() {
        if (getInventory().checkEquipped(900040)) { // 着用したアイテム
            L1ItemInstance drop = ItemTable.getInstance().createItem(3000158); // ドロップさせるアイテム
            for (L1ItemInstance item : getInventory().getItems()) {
                if (item.getItemId() == 900040 & item.isEquipped()) {
                    sendPackets(new S_ServerMessage(3802));
                    sendPackets(new S_SystemMessage("サキュバスクイーンの契約を失った。"));
                    getInventory().removeItem(item, 1);
                    L1World.getInstance().getInventory(getX(), getY(), getMapId()).storeItem(drop);
                    break;
                }
            }
        }
    }

    public void ClearPlayerClanData(L1Clan clan) throws Exception {
        setClanid(0);
        setClanname("");
        setTitle("");
        setClanMemberId(0);
        setClanMemberNotes("");
        setClanJoinDate(null);
        if (this != null) {
            sendPackets(new S_CharTitle(getId(), null));
            Broadcaster.broadcastPacket(this, new S_CharTitle(getId(), null));
            sendPackets(new S_ReturnedStat(getId(), 0));
            Broadcaster.broadcastPacket(this, new S_ReturnedStat(getId(), 0));
            sendPackets(new S_ClanName(this, 0, 0));
        }

        setClanRank(0);
        // sendPackets(new S_PacketBox(S_PacketBox.MSG_RANK_CHANGED, 0,
        // getName()));
        save();
    }

    private void huntoption(L1PcInstance pc) { // このマップエフェクト示す
        if (pc.getHuntCount() != 0) {
            if (pc.isWizard() || pc.isBlackwizard()) {
                if (pc.getHuntPrice() == Config.STAGE_1) {
                    pc.addSp(-1);
                    pc.sendPackets(new S_SPMR(pc));
                } else if (pc.getHuntPrice() == Config.STAGE_2) {
                    pc.addSp(-2);
                    pc.sendPackets(new S_SPMR(pc));
                } else if (pc.getHuntPrice() == Config.STAGE_3) {
                    pc.addSp(-3);
                    pc.sendPackets(new S_SPMR(pc));
                }
            } else if (pc.isCrown() || pc.isKnight() || pc.isDarkelf() || pc.isDragonknight() || pc.isElf()
                    || pc.isWarrior()) {
                if (pc.getHuntPrice() == Config.STAGE_1) {
                    pc.addDmgup(-1);
                    pc.addBowDmgup(-1);
                } else if (pc.getHuntPrice() == Config.STAGE_2) {
                    pc.addDmgup(-2);
                    pc.addBowDmgup(-2);
                } else if (pc.getHuntPrice() == Config.STAGE_3) {
                    pc.addDmgup(-3);
                    pc.addBowDmgup(-3);
                }
            }
        }
    }

    public void startEinhasadTimer() {
        synchronized (this) {
            if (_einhasadTimer != null) {
                return;
            }
            _einhasadTimer = new EinhasadTimer();
            GeneralThreadPool.getInstance().schedule(_einhasadTimer, EinhasadTimer.INTERVAL);
        }
    }

    public void stopEinhasadTimer() {
        synchronized (this) {
            if (_einhasadTimer == null) {
                return;
            }
            _einhasadTimer.cancel();
            _einhasadTimer = null;
        }
    }

    EinhasadTimer _einhasadTimer;

    class EinhasadTimer extends RepeatTask {

        public static final int INTERVAL = 15 * 60 * 1000;

        public EinhasadTimer() {
            super(INTERVAL);
        }

        @Override
        public void execute() {
            /*
             * if (getLevel() < 49) { stopEinhasadTimer(); return; }
             */
            int einhasad = getEinhasad();
            if (einhasad == 2000000) {
                return;
            }
            ++einhasad;
            setEinhasad(einhasad);

            sendPackets(new S_PacketBox(S_PacketBox.EINHASAD, einhasad));
        }
    }

    private int getAutoCheckDuration() {
        return _autoCheckDuration;
    }

    // オート防止コード
    private void showAutoAuthDialog() {
        ++_autoCheckCount;
        Random random = new Random(System.nanoTime());
        _autoAuthCode = String.format("%01d", random.nextInt(10));
        sendPackets(new S_ChatPacket(this, "オート防止：" + _autoAuthCode + " + " + 3 + " = ??", Opcodes.S_SAY, 2));
        sendPackets(new S_SystemMessage("オート防止コード：" + _autoAuthCode + " + " + 3 + "=？未入力時差し押さえされます。"));
        _lastAutoCheckTime = (int) (System.currentTimeMillis() / 1000) - getAutoCheckDuration() + 120;
        // 2分間の猶予期間。
    }

    public boolean waitAutoAuth() {
        return _autoCheckCount > 0;
    }

    public String getAutoAuthCode() {// 文字列
        int temp1 = Integer.valueOf(_autoAuthCode);
        int code = temp1 + 3;
        return String.valueOf(code);
    }

    public void resetAutoInfo() {
        _autoCheckCount = 0;
        _lastAutoCheckTime = (int) (System.currentTimeMillis() / 1000);

        genAutoCheckDuration();
    }

    private void genAutoCheckDuration() {
        if (LoginController.getInstance().getCClassCount(_netConnection.getIp()) >= 12) {
            _autoCheckDuration = 600;
        } else if (LoginController.getInstance().getCClassCount(_netConnection.getIp()) > 2) {
            _autoCheckDuration = _random.nextInt(3600) + 600;
        } else {
            _autoCheckDuration = _random.nextInt(5000) + 1200;
        }
    }

    public synchronized boolean checkAuto() {
        if (_lastAutoCheckTime == 0) {
            _lastAutoCheckTime = (int) (System.currentTimeMillis() / 1000);
            genAutoCheckDuration();

            return false;
        } else if (LoginController.getInstance().getCClassCount(_netConnection.getIp()) >= 12) {
            if (_lastAutoCheckTime + getAutoCheckDuration() > (int) (System.currentTimeMillis() / 1000) + 600) {
                genAutoCheckDuration();
            }
        }

        if (_netConnection == null) {
            return false;
        }

        if (_autoCheckCount > 5) {
            GameServer.disconnectChar(this);
            return true;
        }

        if (_lastAutoCheckTime + getAutoCheckDuration() < (int) (System.currentTimeMillis() / 1000)) {
            showAutoAuthDialog();
        }

        return _autoCheckCount > 1;
    }

    public long getFishingShopBuyTime_1() {
        return FishingShopBuyTime_1;
    }

    public void setFishingShopBuyTime_1(long fishingShopBuyTime_1) {
        FishingShopBuyTime_1 = fishingShopBuyTime_1;
    }

    private boolean createNewItem(L1PcInstance pc, int item_id, int count, int EnchantLevel, int AttEnchantLevel,
            int Bless) {
        L1ItemInstance item = ItemTable.getInstance().createItem(item_id);
        if (item != null) {
            item.setCount(count);
            item.setEnchantLevel(EnchantLevel);
            item.setAttrEnchantLevel(AttEnchantLevel);
            item.setIdentified(true);
            pc.getInventory().storeItem(item);
            item.setBless(Bless);
            pc.getInventory().updateItem(item, L1PcInventory.COL_BLESS);
            pc.getInventory().saveItem(item, L1PcInventory.COL_BLESS);
            return true;
        } else {
            return false;
        }
    }

    private int risingUp = 0;

    public int getRisingUp() {
        return risingUp;
    }

    public void setRisingUp(int i) {
        risingUp = i;
    }

    private int impactUp = 0;

    public int getImpactUp() {
        return impactUp;
    }

    public void setImpactUp(int i) {
        impactUp = i;
    }

    private int graceLv = 0;

    public int getGraceLv() {
        return graceLv;
    }

    public void setGraceLv(int i) {
        graceLv = i - 80;
        if (graceLv < 0) {
            graceLv = 0;
        } else if (graceLv > 5) {
            graceLv = 5;
        }
    }

    private boolean _PresentWarehouse = false;

    public boolean isPresentWarehouse() {
        return _PresentWarehouse;
    }

    public boolean setPresentWarehouse(boolean set) {
        return _PresentWarehouse = set;
    }

    public Point MovePoint = new Point();

    public int getAttackLevelCount() {
        int level1 = getLevel();
        /*
         * if (getGfxId().getGfxId() == 13153) { return 12; } else if
         * (getGfxId().getGfxId() == 13152) { return 13; } else
         */
        if (level1 < 30) {
            return 1;
        } else if (level1 < 45) {
            return 2;
        } else if (level1 < 50) {
            return 3;
        } else if (level1 < 52) {
            return 4;
        } else if (level1 < 55) {
            return 5;
        } else if (level1 < 60) {
            return 6;
        } else if (level1 < 65) {
            return 7;
        } else if (level1 < 70) {
            return 8;
        } else if (level1 < 75) {
            return 9;
        } else if (level1 < 80) {
            return 10;
        } else if (level1 >= 80) {
            return 11;
        }
        return 0;
    }

    // スレイヤー攻撃順序 0=first 1=second
    private int _slayerSwich = 0;

    public int getSlayerSwich() {
        return _slayerSwich;
    }

    public void setSlayerSwich(int _slayerSwich) {
        this._slayerSwich = _slayerSwich;
    }

}