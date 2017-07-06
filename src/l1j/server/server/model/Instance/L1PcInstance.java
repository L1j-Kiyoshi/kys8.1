package l1j.server.server.model.Instance;

import static l1j.server.server.model.skill.L1SkillId.ABSOLUTE_BARRIER;
import static l1j.server.server.model.skill.L1SkillId.ANTA_MESSAGE_6;
import static l1j.server.server.model.skill.L1SkillId.ANTA_MESSAGE_7;
import static l1j.server.server.model.skill.L1SkillId.ANTA_MESSAGE_8;
import static l1j.server.server.model.skill.L1SkillId.ANTA_SHOCKSTUN;
import static l1j.server.server.model.skill.L1SkillId.BONE_BREAK;
import static l1j.server.server.model.skill.L1SkillId.EARTH_BIND;
import static l1j.server.server.model.skill.L1SkillId.ICE_LANCE;
import static l1j.server.server.model.skill.L1SkillId.MOB_RANGESTUN_19;
import static l1j.server.server.model.skill.L1SkillId.MOB_SHOCKSTUN_30;
import static l1j.server.server.model.skill.L1SkillId.OMAN_STUN;
import static l1j.server.server.model.skill.L1SkillId.SHOCK_STUN;

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
import l1j.server.server.datatables.RestoreItemTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.datatables.WeekQuestTable;
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
    /** 날짜 및 시간 기록 **/
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

    public int 상인찾기Objid = 0;
    public boolean war_zone = false;
    
    public boolean isSiege = false;
    private int SiegeTeam = -1;
    public void setTeam(int i){
    	SiegeTeam = i ;
    }
    public int getTeam(){ return SiegeTeam; }
    
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
	// 부가 아이템 단축키
	private boolean _PackegeWarehoue = false;
	public boolean isPackegeWarehouse() { return _PackegeWarehoue; }
	public boolean setPackegeWarehouse(boolean set) { return _PackegeWarehoue = set; }


    private ReportDeley _reportdeley; // /신고 추가

    public void startReportDeley() { // /신고 추가
        _reportdeley = new ReportDeley(this);
        _regenTimer.schedule(_reportdeley, 100000); // 딜레이 시간 10분
    }

    // /신고 추가
    private boolean _isReport = true;

    public void setReport(boolean _isreport) {
        _isReport = _isreport;
    }

    public boolean isReport() {
        return _isReport;
    }
    /** 로봇**/
    
	private boolean _isRobot = false;

	public boolean isRobot() {
		return _isRobot;
	}

	public void setRobot(boolean flag) {
		_isRobot = flag;
	}
    /**
     * 로봇 다이멘트
     */
	private String diement = null;
	private static String[] _diementArray = { "벌써죽냐", "ㅡㅡ", "다이ㅋㅋ", "ㅋㅋㅋㅋㅋㅋ",
			"", "개족밥ㅋ", "" };

	public void Delay(int delayTime) throws Exception {

		int mdelayTime;
		mdelayTime = delayTime;
		Robot robot = new Robot();
		robot.delay(mdelayTime);
	} // 로봇다이 멘트
	
    /**클랜 가입일자**/
	private Timestamp _clan_join_date=null;;

	public void setClanJoinDate(Timestamp date) {
		_clan_join_date = date;
	}

	public Timestamp getClanJoinDate() {
		return _clan_join_date;
	}
	/**								**/
    //주퀘
    private int WeekType = 1;
    private boolean LineClear[] = {false, false, false};
    private boolean Reward[] = {false,false,false};
    private ArrayList<Integer> wcount = new ArrayList<Integer>();
    private int QuestWeek=0;
    
    public int getQuestWeek(){
    	return QuestWeek;
    }
    public void setQuestWeek(int value){
    	QuestWeek =value;
    }
    public void setWcount(int value){
    	wcount.add(value);
    }
    public void setWcount(int i,int value){
    	wcount.set(i, value);
    }
    
  /*  public int getWcount(int i){
    	return wcount.get(i);
    } */
    
    public void setLineClear(int i,boolean a){
    	LineClear[i] = a;
    }
    public boolean isLineClear(int i){ return LineClear[i]; 
    }
    public void setReward(int i,boolean a){
    	Reward[i] = a;
    }
    public boolean getReward(int i){ return Reward[i]; }
    public void setWeekType(int i){
    	WeekType = i;
    }
    public int getWeekType(){ return WeekType; }
    
    
    /**클라우디아 아이템**/
    public int cL = 0;
    //복구권
    public boolean isRestore = false;
    /***장비변경권***/
    private boolean isChangeItem = false;
    public boolean getIsChangeItem(){ return isChangeItem; }
    public void setIsChangeItem(boolean a){
    	isChangeItem = a;
    }
    public int changeNpcId=90000; //default
    
    private L1ItemInstance defaultItem;
    public void setDefaultItem(L1ItemInstance i){
    	defaultItem = i;
    }
    public L1ItemInstance getDefaultItem(){
    	return defaultItem;
    }

    /***장비변경권**/
    /** 휘장 크리티컬 **/
    private int MagicCritical=0;
    public void addMagicCritical(int i){
    	MagicCritical = i;
    }
    public int getMagicCritical(){ return MagicCritical; }

    private int dmgCritical=0;
    public void addDmgCritical(int i){
    	dmgCritical = i;
    }
    public int getDmgCritical(){ return dmgCritical; }

    private int bowCritical=0;
    public void addBowDmgCritical(int i){
    	bowCritical = i;
    }
    public int getBowDmgCritical(){ return bowCritical; }

    
    /*************************/
    /***장비매입****/
    public boolean isNpcSell = false;
    
    public L1NpcInstance isNpcid = null;
    
    /**겜블러**/
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
    public static final int CLASSID_전사_MALE = 12490;
    public static final int CLASSID_전사_FEMALE = 12494;

    public static final int REGENSTATE_NONE = 4;
    public static final int REGENSTATE_MOVE = 2;
    public static final int REGENSTATE_ATTACK = 1;
    public int _npcnum = 0;
    public String _npcname = "";
    public long tamtime = 0;

    // 어비스포인트
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

    // 어비스포인트에 따른 계급 산출
    private int _peerage=0;
    private boolean _9급병;
    private boolean _8급병;
    private boolean _7급병;
    private boolean _6급병;
    private boolean _5급병;
    private boolean _4급병;
    private boolean _3급병;
    private boolean _2급병;
    private boolean _1급병;
    private boolean _1성장교;
    private boolean _2성장교;
    private boolean _3성장교;
    private boolean _4성장교;
    private boolean _5성장교;
    private boolean _장군;
    private boolean _대장군;
    private boolean _사령관;
    private boolean _총사령관;

    public int getPeerage() {
        return _peerage;
    }

    public void setPeerage(int i) {
        _peerage = i;
    }

    public boolean is9급병() {
        return _9급병;
    }

    public void set9급병(boolean flag) {
        _9급병 = flag;
    }

    public boolean is8급병() {
        return _8급병;
    }

    public void set8급병(boolean flag) {
        _8급병 = flag;
    }

    public boolean is7급병() {
        return _7급병;
    }

    public void set7급병(boolean flag) {
        _7급병 = flag;
    }

    public boolean is6급병() {
        return _6급병;
    }

    public void set6급병(boolean flag) {
        _6급병 = flag;
    }

    public boolean is5급병() {
        return _5급병;
    }

    public void set5급병(boolean flag) {
        _5급병 = flag;
    }

    public boolean is4급병() {
        return _4급병;
    }

    public void set4급병(boolean flag) {
        _4급병 = flag;
    }

    public boolean is3급병() {
        return _3급병;
    }

    public void set3급병(boolean flag) {
        _3급병 = flag;
    }

    public boolean is2급병() {
        return _2급병;
    }

    public void set2급병(boolean flag) {
        _2급병 = flag;
    }

    public boolean is1급병() {
        return _1급병;
    }

    public void set1급병(boolean flag) {
        _1급병 = flag;
    }

    public boolean is1성장교() {
        return _1성장교;
    }

    public void set1성장교(boolean flag) {
        _1성장교 = flag;
    }

    public boolean is2성장교() {
        return _2성장교;
    }

    public void set2성장교(boolean flag) {
        _2성장교 = flag;
    }

    public boolean is3성장교() {
        return _3성장교;
    }

    public void set3성장교(boolean flag) {
        _3성장교 = flag;
    }

    public boolean is4성장교() {
        return _4성장교;
    }

    public void set4성장교(boolean flag) {
        _4성장교 = flag;
    }

    public boolean is5성장교() {
        return _5성장교;
    }

    public void set5성장교(boolean flag) {
        _5성장교 = flag;
    }

    public boolean is장군() {
        return _장군;
    }

    public void set장군(boolean flag) {
        _장군 = flag;
    }

    public boolean is대장군() {
        return _대장군;
    }

    public void set대장군(boolean flag) {
        _대장군 = flag;
    }

    public boolean is사령관() {
        return _사령관;
    }

    public void set사령관(boolean flag) {
        _사령관 = flag;
    }

    public boolean is총사령관() {
        return _총사령관;
    }

    public void set총사령관(boolean flag) {
        _총사령관 = flag;
    }

    // 콤보시스템
    public int getComboCount() {
        return this.comboCount;
    }

    public void setComboCount(int comboCount) {
        this.comboCount = comboCount;
    }

    public boolean PC방_버프 = false;
    public boolean PC방_버프삭제중 = false;

    private boolean isSafetyZone;

    public boolean getSafetyZone() {
        return isSafetyZone;
    }

    public void setSafetyZone(boolean value) {
        isSafetyZone = value;
    }

    /** 레이드 Y_N **/
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
    
    private boolean _그림리퍼 = false;
    public void set그림리퍼(boolean flag) {	 _그림리퍼 = flag;  }
    public boolean get그림리퍼() {	return _그림리퍼;}
    
    private boolean _제니스 = false;
    public void set제니스(boolean flag) {	 _제니스 = flag;  }
    public boolean get제니스() {	return _제니스;}
    
    private boolean _시어 = false;
    public void set시어(boolean flag) {	 _시어 = flag;  }
    public boolean get시어() {	return _시어;}
    
    private boolean _뱀파이어 = false;
    public void set뱀파이어(boolean flag) {	 _뱀파이어 = flag;  }
    public boolean get뱀파이어() {	return _뱀파이어;}
    
    private boolean _좀비로드 = false;
    public void set좀비로드(boolean flag) {	 _좀비로드 = flag;  }
    public boolean get좀비로드() {	return _좀비로드;}
    
    private boolean _쿠거 = false;
    public void set쿠거(boolean flag) {	 _쿠거 = flag;  }
    public boolean get쿠거() {	return _쿠거;}
    
    private boolean _머미로드 = false;
    public void set머미로드(boolean flag) {	 _머미로드 = flag;  }
    public boolean get머미로드() {	return _머미로드;}
    
    private boolean _아이리스 = false;
    public void set아이리스(boolean flag) {	 _아이리스 = flag;  }
    public boolean get아이리스() {	return _아이리스;}
    
    private boolean _나이트발드 = false;
    public void set나이트발드(boolean flag) {	 _나이트발드 = flag;  }
    public boolean get나이트발드() {	return _나이트발드;}
    
    private boolean _리치 = false;
    public void set리치(boolean flag) {	 _리치 = flag;  }
    public boolean get리치() {	return _리치;}
    
    private boolean _우그니스 = false;
    public void set우그니스(boolean flag) {	 _우그니스 = flag;  }
    public boolean get우그니스() {	return _우그니스;}
    
    private boolean _발록 = false;
    public void set발록(boolean flag) {	 _발록 = flag;  }
    public boolean get발록() {	return _발록;}
    


    // 계정 정로 로드시 필요..
    public Account getAccount() {
        return this._netConnection.getAccount();
    }

    /** 포우 **/
    public boolean FouSlayer = false;
    
    /** 트리플 **/
	public boolean TRIPLE = false;

    // 문장주시 변수
    public boolean 문장주시 = false;
    
    public boolean 몹다운 = false;

    public int _x;

    // 성장의 낚시관련
    private L1ItemInstance _fishingitem;

    public L1ItemInstance getFishingItem() {
        return _fishingitem;
    }

    public void setFishingItem(L1ItemInstance item) {
        _fishingitem = item;
    }

    /** AttackController **/
    public long AttackControllerTime = 0;
    /** AttackController **/
    /** SPR체크 **/
    public int AttackSpeedCheck2 = 0;
    public int MoveSpeedCheck = 0;
    public int magicSpeedCheck = 0;
    public long AttackSpeed2;
    public long MoveSpeed;
    public long magicSpeed;
    /** SPR체크 **/

    public int dx = 0;
    public int dy = 0;
    public short dm = 0;
    public int dh = 0;
    public int 상점변신 = 0;
    /** 캐릭별 추가데미지, 추가리덕션, 확률 **/
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

    /** 캐릭별 추가데미지, 추가리덕션, 확률 **/

    private int _ubscore;

    public int getUbScore() {
        return _ubscore;
    }

    public void setUbScore(int i) {
        _ubscore = i;
    }

    public byte[] 페어리정보 = new byte[512];

    public void 페어리경험치보상(int lv) {
        int needExp = ExpTable.getNeedExpNextLevel(lv);
        int addexp = 0;
        addexp = (int) (needExp * 0.01);
        if (addexp != 0) {
            int level = ExpTable.getLevelByExp(getExp() + addexp);
            if (level > 60) {
                sendPackets(new S_SystemMessage("더이상 경험치를 획득 할 수 없습니다."));
            } else {
                addExp(addexp);
            }
        }
    }

    public void 페어리정보저장(int id) {
        int count = fairlycount(getId());
        페어리정보[id] = 1;
        if (count == 0) {
            fairlystore(getId(), 페어리정보);
        } else {

            fairlupdate(getId(), 페어리정보);
        }
    }

    public int fairlycount(int objectId) {
        int result = 0;
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT count(*) as cnt FROM character_Fairly_Config WHERE object_id=?");
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
            pstm = con.prepareStatement("INSERT INTO character_Fairly_Config SET object_id=?, data=?");
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
            pstm = con.prepareStatement("UPDATE character_Fairly_Config SET data=? WHERE object_id=?");
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

    /** 브레이브아바타 **/
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

    /** 브레이브아바타 **/

    public L1ItemInstance _fishingRod = null;

    private static Random _random = new Random(System.nanoTime());

    private L1ClassFeature _classFeature = null;
    private L1EquipmentSlot _equipSlot;
    private String _accountName;
    private int _classId;
    private int _type;
    private int _exp;
    private int _age;
    /** 나이설정 **/
    private short _accessLevel;

    private short _baseMaxHp = 0;
    private int _baseMaxMp = 0;
    private int _baseAc = 0;
    private int _originalMagicHit = 0;
    private int _baseBowDmgup = 0;
    private int _baseDmgup = 0;
    private int _baseHitup = 0;
    private int _baseBowHitup = 0;

    private int _baseMagicHitup = 0; // 베이스 스탯에 의한 마법 명중
    private int _baseMagicCritical = 0; // 베이스 스탯에 의한 마법 치명타(%)
    private int _baseMagicDmg = 0; // 베이스 스탯에 의한 마법 대미지
    private int _baseMagicDecreaseMp = 0; // 베이스 스탯에 의한 마법 대미지

    private int _DmgupByArmor = 0; // 방어용 기구에 의한 근접무기 추타율
    private int _bowDmgupByArmor = 0; // 방어용 기구에 의한 활의 추타율

    private int _PKcount;
    public int _fishingX = 0;
    public int _fishingY = 0;
    private int _clanid=0;
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
    public boolean isInFantasy = false; // 몽섬리뉴얼
    /** 화룡의 안식처 **/
    public boolean isInValakasBoss = false;
    public boolean isInValakas = false;
    private boolean _ghost = false;
    private boolean _ghostCanTalk = true;
    private boolean _isReserveGhost = false;
    private boolean _isShowTradeChat = true;
    private boolean _isCanWhisper = true;
    private boolean _isFishing = false;
    private boolean _isFishingReady = false;
    private boolean isDeathMatch = false; // 데스매치
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

    public boolean RootMent = true;// 루팅 멘트]

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
    
	/** 수배설정 **/
	private int huntCount;
	private int huntPrice;
	private String _reasontohunt;
	public String getReasonToHunt() {		return _reasontohunt;	}
	public void setReasonToHunt(String s) {		_reasontohunt = s;	}

	public int getHuntCount() {		  return huntCount;		 }
	public void setHuntCount(int i) {		  huntCount = i;		 }

	public int getHuntPrice() {		  return huntPrice;		 }
	public void setHuntPrice(int i) {		  huntPrice = i;		 }
	
	/** 수배설정 **/

    private int _weightReduction = 0;
    private int _hasteItemEquipped = 0;
    private int _damageReductionByArmor = 0;
    private int _regist_PVPweaponTotalDamage = 0;
    private int _DmgRate = 0; // 방어용 기구에 의한 근접무기 추타율
    private int _HitRate = 0; // 방어용 기구에 의한 근접무기 명중율
    private int _bowHitRate = 0; // 방어용 기구에 의한 활의 명중율
    private int _bowDmgRate = 0; // 방어용 기구에 의한 활의 추타율
    private int _MagicHitupByArmor = 0; // 방어용 기구에 의한 마법 확률 증가

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

    private int _currentWeapon; // 로봇 관련
    private final L1Karma _karma = new L1Karma();
    private final L1PcInventory _inventory;
    //private final L1DwarfForPackageInventory _dwarfForPackage;
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

    // //-- 수정 [ 추가된 소스 차단 ]
    // private final L1ExcludingList _excludingList = new L1ExcludingList();
    //
    // public L1ExcludingList getExcludingList() {
    // return _excludingList;
    // }


    private static Timer _regenTimer = new Timer(true);

    private boolean _isPrivateShop = false;
	private boolean _isAutoClanjoin = false;//무인가입
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

    /** 혈맹버프 **/
    private boolean _clanbuff = false;

    public boolean isClanBuff() {
        return _clanbuff;
    }

    public void setClanBuff(boolean c) {
        _clanbuff = c;
    }

    /** 혈맹버프 **/

    /** 배틀존 **/
    private int _DuelLine;

    public int get_DuelLine() {
        return _DuelLine;
    }

    public void set_DuelLine(int i) {
        _DuelLine = i;
    }

    // 생존의외침
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

    // 조우의 불골렘
    public int[] FireGolem = new int[18];
    public int[] FireEnchant = new int[18];

    // 피어스
    public int[] PiersItemId = new int[19];
    public int[] PiersEnchant = new int[19];

    private boolean _텔대기 = false;
    private boolean _isElrzabe = false;
    private boolean _isSandWarm = false;
    private boolean _is드레이크 = false;
    private boolean _is제로스 = false;
    private boolean _is기르타스 = false;
    private boolean _is대왕오징어 = false;
    private boolean _is발라카스 = false;
    private boolean _is파푸리온 = false;
    private boolean _is린드비오르 = false;
    private boolean _is안타라스 = false;
    
	public boolean setValakaseDmgDouble = false;
	
	//발라카스
	public static int valakasMapId = 0;

    private boolean _magicitem = false;
    private int _magicitemid;
    // 3.63아이템패킷추가
    public boolean isWorld = false;
    // 3.63아이템패킷추가
    public boolean isDanteasBuff = false;

    public boolean 서버다운중 = false;
    public boolean 바포방 = false;
    // 젠도르 제작 관련
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
        //_dwarfForPackage = new L1DwarfForPackageInventory(this);
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

    public void set드레이크(boolean flag) {
        _is드레이크 = flag;
    }

    public boolean is드레이크() {
        return _is드레이크;
    }

    public void set제로스(boolean flag) {
        _is제로스 = flag;
    }

    public boolean is제로스() {
        return _is제로스;
    }

    public void set기르타스(boolean flag) {
        _is기르타스 = flag;
    }

    public boolean is기르타스() {
        return _is기르타스;
    }

    public void set대왕오징어(boolean flag) {
        _is대왕오징어 = flag;
    }

    public boolean is대왕오징어() {
        return _is대왕오징어;
    }

    public void set발라카스(boolean flag) {
        _is발라카스 = flag;
    }

    public boolean is발라카스() {
        return _is발라카스;
    }

    public void set파푸리온(boolean flag) {
        _is파푸리온 = flag;
    }

    public boolean is파푸리온() {
        return _is파푸리온;
    }

    public void set린드비오르(boolean flag) {
        _is린드비오르 = flag;
    }

    public boolean is린드비오르() {
        return _is린드비오르;
    }

    public void set안타라스(boolean flag) {
        _is안타라스 = flag;
    }

    public boolean is안타라스() {
        return _is안타라스;
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

    public void startPapuBlessing() {// 파푸가호
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

    public void stopPapuBlessing() { // 파푸가호
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
                    removeSkillEffect(L1SkillId.레벨업보너스);
            } else if (char_level >= 65) {
                if (percent >= 5) {
                    removeSkillEffect(L1SkillId.레벨업보너스);
                }
            }
            return;
        }

        if (gap > 0) {
            levelUp(gap);
            if (getLevel() >= 60) {
                setSkillEffect(L1SkillId.레벨업보너스, 10800000);
                sendPackets(new S_PacketBox(10800, true, true), true);
            }
        } else if (gap < 0) {
            levelDown(gap);
            removeSkillEffect(L1SkillId.레벨업보너스);
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
                if (known.getMapId() == 631 && known.getMapId() == 514 && known.getMapId() == 515 && known.getMapId() == 516 && !isGm()) {
                    if (known instanceof L1PcInstance) {
                        continue;
                    }
    				/** 패키지상점 **/
    				if (known instanceof L1NpcInstance) {
    					L1NpcInstance npc = (L1NpcInstance) known;
    					if (getMapId() == 631 && getCashStep() == 0 && !isGm()) {
    						continue;
    					} else if (getMapId() == 631 || getMapId() == 514 && getCashStep() == 1 && !isGm()) {
    						if (!(npc.getNpcTemplate().get_npcId() >= 6100000 && npc.getNpcTemplate().get_npcId() <= 6100013 || 
    								npc.getNpcTemplate().get_npcId() == 4200022)) {
    							continue;
    						}
    					} else if (getMapId() == 631 || getMapId() == 515 && getCashStep() == 2 && !isGm()) {
    						if (!(npc.getNpcTemplate().get_npcId() >= 6100014 && npc.getNpcTemplate().get_npcId() <= 6100027 || 
    								npc.getNpcTemplate().get_npcId() == 4200022)) {
    							continue;
    						}
    					} else if (getMapId() == 631 || getMapId() == 516 && getCashStep() == 3 && !isGm()) {
    						if (!(npc.getNpcTemplate().get_npcId() >= 6100028 && npc.getNpcTemplate().get_npcId() <= 6100041 || 
    								npc.getNpcTemplate().get_npcId() == 4200022)) {
    							continue;
    						}
    					}
    				}
    				/** 패키지상점 **/
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
                    /** 패키지상점 **/
    				if (getMapId() == 631 && getCashStep() == 0 && !isGm()) {
    					continue;
    				} else if (getMapId() == 631 || getMapId() == 514 && getCashStep() == 1 && !isGm()) {
    					if (!(npc.getNpcTemplate().get_npcId() >= 6100000 && npc.getNpcTemplate().get_npcId() <= 6100013 || 
    							npc.getNpcTemplate().get_npcId() == 4200022)) {
    						continue;
    					}
    				} else if (getMapId() == 631 || getMapId() == 515 && getCashStep() == 2 && !isGm()) {
    					if (!(npc.getNpcTemplate().get_npcId() >= 6100014 && npc.getNpcTemplate().get_npcId() <= 6100027 || 
    							npc.getNpcTemplate().get_npcId() == 4200022)) {
    						continue;
    					}
    				} else if (getMapId() == 631 || getMapId() == 516 && getCashStep() == 3 && !isGm()) {
    					if (!(npc.getNpcTemplate().get_npcId() >= 6100028 && npc.getNpcTemplate().get_npcId() <= 6100041 || 
    							npc.getNpcTemplate().get_npcId() == 4200022)) {
    						continue;
    					}
    				}
    			}
    			/** 패키지상점 **/
                try {

                    if (!knownsObject(visible)) {
                        if (visible.getMapId() == 631 && visible.getMapId() == 514 && visible.getMapId() == 515 && visible.getMapId() == 516  && !isGm()) {
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
                        /** 패키지상점 **/
        				if (visible instanceof L1NpcCashShopInstance) {
        					L1NpcInstance npc = (L1NpcInstance) visible;
        					if (getMapId() == 631 && getCashStep() == 0 && !isGm()) {
        						continue;
        					} else if (getMapId() == 631 || getMapId() == 514 && getCashStep() == 1 && !isGm()) {
        						if (!(npc.getNpcTemplate().get_npcId() >= 6100000 && npc.getNpcTemplate().get_npcId() <= 6100013 || 
        								npc.getNpcTemplate().get_npcId() == 4200022)) {
        							continue;
        						}
        					} else if (getMapId() == 631 || getMapId() == 515 && getCashStep() == 2 && !isGm()) {
        						if (!(npc.getNpcTemplate().get_npcId() >= 6100014 && npc.getNpcTemplate().get_npcId() <= 6100027 || 
        								npc.getNpcTemplate().get_npcId() == 4200022)) {
        							continue;
        						}
        					} else if (getMapId() == 631 || getMapId() == 516 && getCashStep() == 3 && !isGm()) {
        						if (!(npc.getNpcTemplate().get_npcId() >= 6100028 && npc.getNpcTemplate().get_npcId() <= 6100041 || 
        								npc.getNpcTemplate().get_npcId() == 4200022)) {
        							continue;
        						}
        					} else if (getMapId() == 514 && getCashStep() == 4 && !isGm()) {
        						if (!(npc.getNpcTemplate().get_npcId() >= 6100000 && npc.getNpcTemplate().get_npcId() <= 6100011 || 
        								npc.getNpcTemplate().get_npcId() == 4200022)) {
        							continue;
        						}
        					}
        				}
        				/** 패키지상점 **/
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
        /** 배틀존 **/
        if (getMapId() == 5153 && get_DuelLine() != 0) {
            for (L1PcInstance member : BattleZone.getInstance().toArray배틀존유저()) {
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
        /** 배틀존 **/
        if (getMapId() == 5153 && get_DuelLine() != 0) {
            for (L1PcInstance member : BattleZone.getInstance().toArray배틀존유저()) {
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

//    public L1DwarfForPackageInventory getDwarfForPackageInventory() {
//        return _dwarfForPackage;
//    }

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
                // manager.LogServerAppend("종료", this,
                // getNetConnection().getIp(), -1);
            	LinAllManager.getInstance().LogLogOutAppend(getName(), getNetConnection().getHostname());
                /** 로그파일저장 **/
                LoggerInstance.getInstance().addConnection(
                        "종료 캐릭=" + getName() + " 계정=" + getAccountName() + " IP=" + getNetConnection().getHostname());
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
            System.out.println("캐릭터 : " + getName() + " Error Code = 1000");
        }
        try {
            if (L1Racing.getInstance().isMember(this)) {
                if (getMapId() == 5143) {
                    L1HauntedHouse.getInstance().clearBuff(this);
                }

                L1Racing.getInstance().removeMember(this);
            }
        } catch (Exception e) {
            System.out.println("캐릭터 : " + getName() + " Error Code = 1001");
        }
        try {
            getMap().setPassable(getLocation(), true);
            // 사망하고 있으면(자) 거리에 되돌려, 공복 상태로 한다
            if (isDead()) {
                int[] loc = Getback.GetBack_Location(this, true);
                setX(loc[0]);
                setY(loc[1]);
                setMap((short) loc[2]);
                setCurrentHp(getLevel());
                set_food(39); // 10%
            }else if(getMapId() == (short)10502){
            	 if(isSiege){
            		 if(MiniSiege.getInstance().running){
         			switch(getTeam()){
         			case 0:
         				setX(32771);
         				setY(32815);
         				setMap((short)10502);
         			break;

         			case 1:
         				setX(32691);
         				setY(32895);
         				setMap((short)10502);
         				break;
         			case 2:
         				setX(32771);
         				setY(32975);
         				setMap((short)10502);
         				break;
         				
         			}
            		 }else{
            			 isSiege = false;
            			setX(33437);
                 		setY(32810);
                 		setMap((short)4);
            		 }
         		}else{
         			setX(33437);
         			setY(32810);
         			setMap((short)4);
         			
         		}
            }
        } catch (Exception e) {
            System.out.println("캐릭터 : " + getName() + " Error Code = 1002");
        }
        // 트레이드를 중지한다
        try {
            if (getTradeID() != 0) { // 트레이드중
                L1Trade trade = new L1Trade();
                trade.TradeCancel(this);
            }
        } catch (Exception e) {
            System.out.println("캐릭터 : " + getName() + " Error Code = 1003");
        }
        // 결투중
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
            System.out.println("캐릭터 : " + getName() + " Error Code = 1004");
        }
        // 파티를 빠진다
        try {
            if (isInParty()) { // 파티중
                getParty().leaveMember(this);
            }
        } catch (Exception e) {
            System.out.println("캐릭터 : " + getName() + " Error Code = 1005");
        }
        // 채팅파티를 빠진다
        try {
            if (isInChatParty()) { // 채팅파티중
                getChatParty().leaveMember(this);
            }
        } catch (Exception e) {
            System.out.println("캐릭터 : " + getName() + " Error Code = 1006");
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
            System.out.println("캐릭터 : " + getName() + " Error Code = 1007");
        }
        // 애완동물을 월드 MAP상으로부터 지운다
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
                // 서먼
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
            System.out.println("캐릭터 : " + getName() + " Error Code = 1008");
        }
        // 마법인형을 월드 맵상으로부터 지운다
        try {
			if (getDollList() != null && getDollListSize() > 0) {
				for (L1DollInstance doll : getDollList()) {
					if (doll != null)
						doll.deleteDoll();
				}
			}
        } catch (Exception e) {
            System.out.println("캐릭터 : " + getName() + " Error Code = 1009");
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
            System.out.println("캐릭터 : " + getName() + " Error Code = 1010");
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
            System.out.println("캐릭터 : " + getName() + " Error Code = 1011");
        }
        // 엔챤트를 DB의 character_buff에 보존한다
        try {
            MonsterBookTable.getInstace().saveMonsterBookList(this.getId());
            CharBuffTable.DeleteBuff(this);
            CharBuffTable.SaveBuff(this);
            clearSkillEffectTimer();
            SkillCheck.getInstance().QuitDelSkill(this);//
        } catch (Exception e) {
            System.out.println("캐릭터 : " + getName() + " Error Code = 1012");
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
            System.out.println("캐릭터 : " + getName() + " Error Code = 1013");
        }

        // pc의 모니터를 stop 한다.
        stopEtcMonitor();
        // 온라인 상태를 OFF로 해, DB에 캐릭터 정보를 기입한다
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
    public void 상점아이템매입삭제(int objid, int itemid, int type2) {// 아이템아이디별 판매 구매
			// 구분후 삭제
	Connection con = null;
	PreparedStatement pstm = null;
	try {
	con = L1DatabaseFactory.getInstance().getConnection();
	pstm = con
	.prepareStatement("DELETE FROM character_shop WHERE obj_id=? AND item_id=? AND type=?");
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
	
	public void 상점아이템삭제(int objid, int itemid, int type2) {// 아이템아이디별 판매 구매 구분후
		// 삭제
	Connection con = null;
	PreparedStatement pstm = null;
	try {
	con = L1DatabaseFactory.getInstance().getConnection();
	pstm = con
	.prepareStatement("DELETE FROM character_shop WHERE obj_id=? AND item_objid=? AND type=?");
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
	
	public void 상점아이템매입업데이트(int objid, int itemid, int type2, int count1) {
	Connection con = null;
	PreparedStatement pstm = null;
	try {
	con = L1DatabaseFactory.getInstance().getConnection();
	pstm = con
	.prepareStatement("UPDATE character_shop SET count=? WHERE obj_id=? AND item_id=? AND type=?");
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
	
	public void 상점아이템업데이트(int objid, int itemid, int type2, int count1) {
	Connection con = null;
	PreparedStatement pstm = null;
	try {
	con = L1DatabaseFactory.getInstance().getConnection();
	pstm = con
	.prepareStatement("UPDATE character_shop SET count=? WHERE obj_id=? AND item_objid=? AND type=?");
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
	
	public void 상점아이템삭제(int objid) {
	Connection con = null;
	PreparedStatement pstm = null;
	try {
	con = L1DatabaseFactory.getInstance().getConnection();
	pstm = con
	.prepareStatement("DELETE FROM character_shop WHERE obj_id=?");
	pstm.setInt(1, objid);
	pstm.executeUpdate();
	} catch (SQLException e) {
	e.printStackTrace();
	} finally {
	SQLUtil.close(pstm);
	SQLUtil.close(con);
	}
}
    public void SaveShop(L1PcInstance pc, L1ItemInstance item, int price, int sellcount, int type)
	  {
	    Connection con = null;
	    int bless = item.getBless();
	    int attr = item.getAttrEnchantLevel();
	    PreparedStatement pstm = null;
	    try {
	      con = L1DatabaseFactory.getInstance().getConnection();

	      pstm = con
	        .prepareStatement("INSERT INTO character_shop SET obj_id=?, char_name=?, item_objid=?, item_id=?, Item_name=?, count=?, enchant=?, price=?, type=?, locx=?, locy=?, locm=?, iden=?, attr=?");

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
				w.delSpecialWarehouse(this.getName()); // 특수창고
				w.delPackageWarehouse(this.getAccountName());
              //  _dwarfForPackage.clearItems();
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
                stopPapuBlessing();// 파푸리온 블레싱
                addAc(1);
                _destroyed = true;
                
    			L1AccountAttendance acc = AttendanceController.findacc(getAccountName());
    			if(acc != null)
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

    public boolean 텔대기() {
        return _텔대기;
    }

    public void 텔대기(boolean flag) {
        _텔대기 = flag;
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



    private int birthday;// 생일

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

    public boolean SurvivalState; // 생존의 외침 상태
    private int SurvivalGauge; // 생존의 외침 게이지

    public int getSurvivalGauge() {
        return SurvivalGauge;
    }

    public void setSurvivalGauge(int SurvivalGauge) {
        this.SurvivalGauge = SurvivalGauge;
    }

    public int[] DragonPortalLoc = new int[3];// 드래곤 포탈

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
				if (serverbasepacket.getBytes()[1] == 2
						|| serverbasepacket.getBytes()[1] == 4
						|| serverbasepacket.getBytes()[1] == 10
						|| serverbasepacket.getBytes()[1] == 12
						|| serverbasepacket.getBytes()[1] == 22
						|| serverbasepacket.getBytes()[1] == 24) {
					this.setParalyzed(true);
				}
				if (serverbasepacket.getBytes()[1] == 3
						|| serverbasepacket.getBytes()[1] == 5
						|| serverbasepacket.getBytes()[1] == 11
						|| serverbasepacket.getBytes()[1] == 13
						|| serverbasepacket.getBytes()[1] == 23
						|| serverbasepacket.getBytes()[1] == 25) {
					this.setParalyzed(false);
				}
			}
			if (serverbasepacket.getType().equalsIgnoreCase("[S] S_Paralysis")) {
				if (serverbasepacket.getBytes()[1] == 2
						|| serverbasepacket.getBytes()[1] == 4
						|| serverbasepacket.getBytes()[1] == 10
						|| serverbasepacket.getBytes()[1] == 12
						|| serverbasepacket.getBytes()[1] == 22
						|| serverbasepacket.getBytes()[1] == 24) {
					this.setParalyzed(true);
				}
				if (serverbasepacket.getBytes()[1] == 3
						|| serverbasepacket.getBytes()[1] == 5
						|| serverbasepacket.getBytes()[1] == 11
						|| serverbasepacket.getBytes()[1] == 13
						|| serverbasepacket.getBytes()[1] == 23
						|| serverbasepacket.getBytes()[1] == 25) {
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
		if(TRIPLE){
			if(25 > random2.nextInt(100)+1){ // 40% 확률로 미스
				L1Attack attack_mortion = new L1Attack(attacker, this);
				attack_mortion._isHit = false;
				attack_mortion.action();
				TRIPLE = false;	
				return;
			}
			TRIPLE = false;			
		}else if(FouSlayer){
			if(25 > random2.nextInt(100)+1){ // 30% 확률로 미스
				L1Attack attack_mortion = new L1Attack(attacker, this);
				attack_mortion._isHit = false;
				attack_mortion.action();
				FouSlayer = false;	
				return;
			}
		}
		/** 로봇시스템 */
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
		/** 로봇시스템 */

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
                    /** 조우의 돌골렘 **/
                    attack.calcDrainOfMana();
                    /** 조우의 돌골렘 **/
                    attack.addPcPoisonAttack(attacker, this);

                    applySpecialEnchant(attacker);
                }
            }
            if (isCounterBarrier) {
                attack.actionCounterBarrier();
                attack.commitCounterBarrier();
                attack.commit();
                /** 시전자도 피가 달아지게 추가 **/
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
            // 여기 각 성능별 처리
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
                break; // 동시에 2개 이상은 발동 안됨.
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
			|| (hasSkillEffect(EARTH_BIND))	|| (hasSkillEffect(MOB_RANGESTUN_19))|| (hasSkillEffect(MOB_SHOCKSTUN_30))
			|| (hasSkillEffect(OMAN_STUN))   || (hasSkillEffect(ANTA_MESSAGE_6))  || (hasSkillEffect(ANTA_MESSAGE_7))
			|| (hasSkillEffect(L1SkillId.MOB_COCA)) || (hasSkillEffect(L1SkillId.MOB_CURSEPARALYZ_18)) 
			|| (hasSkillEffect(ANTA_MESSAGE_8)) || (hasSkillEffect(ANTA_SHOCKSTUN));
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
			//if (hasSkillEffect(L1SkillId.ASSASSIN))
			//	removeSkillEffect(L1SkillId.ASSASSIN);
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
            /** 카오경비인식 무적버그 **/
            /**
             * if (attacker instanceof L1PcInstance && ((L1PcInstance)
             * attacker).isPinkName()) { L1GuardInstance guard = null; for
             * (L1Object object :
             * L1World.getInstance().getVisibleObjects(attacker)) { if(object ==
             * null) continue; if (object instanceof L1GuardInstance) { guard =
             * (L1GuardInstance) object; if (rightNow.getTimeInMillis() <
             * ((L1PcInstance) attacker).getLastPk().getTime() + (1000 * 60 * 60
             * * 1)){ // 마지막 pk 시간이 1시간이 안지났다면 경비가 인식
             * guard.setTarget(((L1PcInstance) attacker)); } } } }
             **/
            /** 카오경비인식 **/
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
                        /** 카운터배리어, 타이탄데미지처리 **/
                        if (Config.BROADCAST_KILL_LOG && getLevel() >= Config.BROADCAST_KILL_LOG_LEVEL) {
                            L1World.getInstance().broadcastPacketToAll(new S_ChatPacket(
                            "\\aH[" + attacker.getName() + "]\\aA 님이 \\aG[" + getName() + "]\\aA 님을 죽였습니다.", Opcodes.S_MESSAGE));
                            attacker.setKills(attacker.getKills() + 1); // 이긴넘 킬수 +1
                            setDeaths(getDeaths() + 1); // 진넘 데스수 +1
                            LinAllManagerInfoThread.PvPCount += 1;
                        }
                        /** 카운터배리어, 타이탄데미지처리 **/
                        if(isSiege){
                        	if(getMapId()==10502){
                        		/*L1ItemInstance aden = ItemTable.getInstance().createItem(40308);
                    			aden.setCount(1000);
                        		L1World.getInstance().getInventory(getX(),getY(),getMapId()).insertItem(aden);*/
                        		System.out.println("getX():"+getX()+ " GETY L:: "+getY());
                				L1GroundInventory ground = L1World.getInstance().getInventory(getX(), getY(), (short)10502);
                				L1ItemInstance item =ItemTable.getInstance().createItem(40308);
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
                                    if(getAbysspoint() >= 100){
                                    	addAbysspoint(-100);
                                    	((L1PcInstance)attacker).addAbysspoint(100);
                                    	sendPackets(new S_SystemMessage("어비스포인트 100점을 잃으셨습니다"));
                                    	((L1PcInstance)attacker).sendPackets(new S_SystemMessage("어비스포인트 100점을 획득하셨습니다"));
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
            System.out.println("■■■■■■■ HP 감소가 올바르지 못한 캐릭발견.※혹은 최초부터 피가 0");
            death(attacker, false);
        }
    }

    public void receiveDamage(L1Character attacker, int damage) {
        if (getCurrentHp() > 0 && !isDead()) {
            /** 로봇시스템 */
            if (getRobotAi() != null && damage >= 0) {
                if (!(attacker instanceof L1EffectInstance)) {
                    getRobotAi().setHate(attacker, (int) damage);
                }
            }
            /** 로봇시스템 */
            if (attacker != this && !knownsObject(attacker) && attacker.getMapId() == this.getMapId()) {
                attacker.onPerceive(this);
            }

            if (damage > 0) {
    			if (this instanceof L1RobotInstance) {
					L1RobotInstance bot = (L1RobotInstance) this;
					if (bot.이동딜레이 == 0) {
						int sleepTime = bot
								.calcSleepTime(L1RobotInstance.DMG_MOTION_SPEED);
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
                    || getInventory().checkEquipped(22202) || getInventory().checkEquipped(22203)) {// 파푸
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
							if (this.getHuntCount() != 0){
								int price = this.getHuntPrice();
								this.setHuntCount(0);
								this.setHuntPrice(0);
								this.setReasonToHunt(null);
								huntoption(this);
								attacker.getInventory().storeItem(40308, price);
								sendPackets(new S_SystemMessage("\\fY수배가 풀려 추가 옵션이 사라졌습니다."));
								//((L1PcInstance) attacker).sendPackets(new S_SystemMessage("\\fTPvP승리로 클랜경험치 2 상승"));
								L1World.getInstance().broadcastPacketToAll(new S_SystemMessage(
										"\\fT[" + attacker.getName() + "]  " + this.getName() + "님 현상금 감사합니다."));
							}
                            L1Clan clan = L1World.getInstance().getClan(getClanname());
                            if (getClanid() != 0 && getClan().getClanExp() >= 2) {
                                clan.addClanExp(-2);
                                sendPackets(new S_SystemMessage("PvP패배로 클랜경험치 2 하락"));
                                ClanTable.getInstance().updateClan(this.getClan()); // 죽은사람 혈맹업데이트
                                for (L1PcInstance tc : getClan().getOnlineClanMember()) {
                                    tc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
                                            "[" + getName() + "]님 PvP 패배로 혈맹경험치 2하락. "));
                                }
                            }
                            if(isSiege){
                            	if(getMapId()==10502){
                            		/*L1ItemInstance aden = ItemTable.getInstance().createItem(40308);
                        			aden.setCount(1000);
                            		L1World.getInstance().getInventory(getX(),getY(),getMapId()).insertItem(aden);*/
                            		System.out.println("getX():"+getX()+ " GETY L:: "+getY());
                    				L1GroundInventory ground = L1World.getInstance().getInventory(getX(), getY(), (short)10502);
                    				L1ItemInstance item =ItemTable.getInstance().createItem(40308);
                    				item.setCount(1000);
                    				ground.storeItem(item);
                            	}
                            }
                            if (((L1PcInstance) attacker).getClanid() > 0) { // 어택자가 클랜이 있을경우에만 처리
                                ((L1PcInstance) attacker).getClan().addClanExp(2);
                                ((L1PcInstance) attacker).sendPackets(new S_SystemMessage("PvP승리로 클랜경험치 2 상승"));
                                ClanTable.getInstance().updateClan(((L1PcInstance) attacker).getClan()); // 죽인사람
                                // 혈맹업데이트
                                for (L1PcInstance tc : ((L1PcInstance) attacker).getClan().getOnlineClanMember()) {
                                    tc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
                                            "[" + ((L1PcInstance) attacker).getName() + "]님 PvP 승리로 혈맹경험치 2상승. "));
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
            System.out.println("■■■■■■■ HP 감소 처리가 올바르지 못한 캐릭발견.※혹은 최초부터 피가 0");
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

			// 로봇 킬
			// _atker._PlayPcKill++;
			if (getZoneType() == 0	) {
				if (this instanceof L1RobotInstance) { // 로봇이라면
					if (_atker instanceof L1PcInstance
							&& this.getClanid() != _atker.getClanid()) { // 일반유저라면
					}
				}

				if (_atker.isInParty()) {
					for (L1PcInstance atker_p : _atker.getParty().getMembers()) {
						atker_p.sendPackets(new S_ServerMessage(3690,
								lastAttacker.getName(), this.getName()), true);
					}
					if (this.isInParty()) {
						for (L1PcInstance defender_p : this.getParty()
								.getMembers()) {
							defender_p.sendPackets(new S_ServerMessage(3689,
									this.getName()), true);
						}
					}
				} else {
					_atker.sendPackets(
							new S_ServerMessage(3691, lastAttacker.getName(),
									this.getName()), true);
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
                    /** 죽였을경우 이팩트 날리기 **/
                    player.sendPackets(new S_SkillSound(player.getId(), 6354));
                    player.broadcastPacket(new S_SkillSound(player.getId(), 6354));
                    /** 죽였을경우 이팩트 날리기 **/
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

                /** 공성전에서만 처리함 **/
                if (player != null) {
 	
                    if (isInWarAreaAndWarTime(L1PcInstance.this, player)) {
                    	int price = getHuntPrice();
                        if (Config.BROADCAST_KILL_LOG && getLevel() >= Config.BROADCAST_KILL_LOG_LEVEL) {
                            L1World.getInstance().broadcastPacketToAll(new S_ChatPacket(
                            "\\aH[" + player.getName() + "]\\aA 님이 \\aG[" + getName() + "]\\aA 님을 죽였습니다.", Opcodes.S_MESSAGE));
                            player.setKills(player.getKills() + 1); // 이긴넘 킬수 +1
                            setDeaths(getDeaths() + 1); // 진넘 데스수 +1
                            LinAllManagerInfoThread.PvPCount += 1;
							if (getHuntCount() > 0) {
								player.getInventory().storeItem(40308, price);
								setHuntCount(0);
								setHuntPrice(0);
								setReasonToHunt(null);
								L1World.getInstance().broadcastPacketToAll(new S_SystemMessage(
										"\\fT[" + player.getName() + "]  " + getName() + " 님 현상금 감사합니다."));
								try {
									save();
								} catch (Exception e) {
									_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
								}
							}
						}
					}
				}
                /** 공성전에서만 처리함 **/

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

            /** 공성장 경험치하락안되게 **/
            boolean castle_ret1 = castleWarResult();
            if (lastAttacker instanceof L1PcInstance) {// 바포메트
                if (!castle_ret1 && getLevel() < Config.NEW_PLAYER && (lastAttacker.getLevel() - getLevel()) >= 10) {
                    isExpDrop = false;
                    isItemDrop = false;
                }
            }
            if (castle_ret1 == true) {
                sendPackets(new S_ServerMessage(3798));
                // 경험치 손실이 없는 지역 : 경험치가 손실되지 않았습니다.
                return;
            }

            
         // 고대의가호
            if (getInventory().checkEquipped(900022)) { 
                drop1();
                return;
            }
            
        	// 서큐버스퀸의 계약
            if (getInventory().checkEquipped(900040)) { 
                drop2();
                return;
            }
            
            if (Config.아놀드이벤트) {
                if (lastAttacker instanceof L1PcInstance) {
                    if (getInventory().checkEquipped(21095)) { // 착용한 아이템
                        drop();
                        return;
                    }
                } else {
                    int chance = _random.nextInt(100);
                    if (chance < 40) {
                        if (getInventory().checkEquipped(21095)) { // 착용한 아이템
                            drop();
                            return;
                        }
                    }
                }
            }

            deathPenalty();
            setGresValid(true);

            if (getExpRes() == 0) {// /조우의 가호로 경험치 회복 fix
                if (lastAttacker instanceof L1PcInstance && getLevel() < Config.NEW_PLAYER
                        && (lastAttacker.getLevel() - getLevel()) >= 10) {
                } else {
                    setExpRes(1);
                }
            }

            /** 칼질시 경비병 치도록 **/
            if (lastAttacker instanceof L1GuardInstance) {
                if (get_PKcount() > 0) {
                    set_PKcount(get_PKcount() - 1);
                }
                setLastPk(null);
            }
            /** 칼질시 경비병 치도록 **/
    		/** 로봇 다이 멘트 **/
			if (lastAttacker instanceof L1RobotInstance) {
				Random dierandom = new Random();
				L1PcInstance lastrob = (L1RobotInstance) lastAttacker;
				diement = _diementArray[dierandom
						.nextInt(_diementArray.length)];
				try {
					Delay(1000);
					Broadcaster.broadcastPacket(lastrob, new S_ChatPacket(
							lastrob, diement, Opcodes.S_SAY, 0));
					diement = null;
				} catch (Exception e) {
				}
			}
			/**사망시 템&마법 드랍확률. 바포시스템화. */
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
						//skillcount = random.nextInt(4) + 1;
					} else if (getLawful() <= -10000) {
						count = random.nextInt(3) + 1;
						//skillcount = random.nextInt(3) + 1;
					} else if (getLawful() <= -5000) {
						count = random.nextInt(2) + 1;
						//skillcount = random.nextInt(2) + 1;
					} else if (getLawful() < 0) {
						count = random.nextInt(1) + 1;
						//skillcount = 0;
					}
					caoPenaltyResult(count);
					//caoPenaltySkill(skillcount);
				}
			}
			/**사망시 템&마법 드랍확률. 바포시스템화 */
            boolean castle_ret = castleWarResult();
            if (castle_ret == true) {
                return;
            }

            L1PcInstance player = null;
            if (lastAttacker instanceof L1PcInstance) {
                player = (L1PcInstance) lastAttacker;
            }

            if (_deathPenalty && player != null) {// PK멘트
            	int price = getHuntPrice();
                if (Config.BROADCAST_KILL_LOG && getLevel() >= Config.BROADCAST_KILL_LOG_LEVEL) {
                    L1World.getInstance().broadcastPacketToAll(new S_ChatPacket(
                    "\\aH[" + player.getName() + "]\\aA 님이 \\aG[" + getName() + "]\\aA 님을 죽였습니다.", Opcodes.S_MESSAGE));
                    player.setKills(player.getKills() + 1); // 이긴넘 킬수 +1
                    setDeaths(getDeaths() + 1); // 진넘 데스수 +1
                    LinAllManagerInfoThread.PvPCount += 1;
                    String locname = MapsTable.getInstance().getMapName(player.getMapId());
                    L1World.getInstance().broadcastPacketToAll(new S_ChatPacket("\\aG[전투지역]: \\aA" + locname + "", Opcodes.S_MESSAGE));
					if (getHuntCount() > 0) {
						player.getInventory().storeItem(40308, price);
						setHuntCount(0);
						setHuntPrice(0);
						setReasonToHunt(null);
						L1World.getInstance().broadcastPacketToAll(
								new S_SystemMessage("\\fT[" + player.getName() + "]  " + getName() + " 님 현상금 감사합니다."));
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
                    player.sendPackets(new S_PacketBox(S_PacketBox.배틀샷, L1PcInstance.this.getId()));
                } else {
                    setPinkName(false);
                }
            }
        }
    }

    private void caoPenaltyResult(int count) {
        /** 공성존에서 드랍안되도록 **/
    	System.out.println("사망페널티");
        int castle_id = L1CastleLocation.getCastleIdByArea(this);
        if (castle_id != 0) {
            return;
        }

        /** 로봇시스템 **/
        if (getRobotAi() != null || getAccessLevel() == Config.GMCODE) {
            return;
        }

        for (int i = 0; i < count; i++) {
            L1ItemInstance item = getInventory().CaoPenalty();
            LinAllManagerInfoThread.PenaltyCount += 1;
            if (item != null) {
                if (item.getBless() > 3) {
                    getInventory().removeItem(item, item.isStackable() ? item.getCount() : 1);
                    sendPackets(new S_ServerMessage(158, item.getLogName()));// 증발
                    LinAllManager.getInstance().PenaltyAppend(item.getLogName(), getName(), count, 1);
                    /** 파일로그저장 **/
                    LoggerInstance.getInstance().addItemAction(ItemActionType.del, this, item, count);
                    /** DB+메모리에 날린거 저장 **/
                    if(item.getItem().getType2()==1 || item.getItem().getType2()==2){
                    //	RestoreItemTable.getInstance().AddRestoreItem(this.getId(), 
                    		//	new L1RestoreItemInstance(item.getItemId(),item.getEnchantLevel(),item.getAttrEnchantLevel(),item.getBless()));
                    }
                } else {
                    getInventory().tradeItem(item, item.isStackable() ? item.getCount() : 1, L1World.getInstance().getInventory(getX(), getY(), getMapId()));
                    sendPackets(new S_ServerMessage(638, item.getLogName()));// 증발
                    /** DB+메모리에 날린거 저장**/
                    if(item.getItem().getType2()==1 || item.getItem().getType2()==2){
                   // 	RestoreItemTable.getInstance().AddRestoreItem(this.getId(), 
                    		//	new L1RestoreItemInstance(item.getItemId(),item.getEnchantLevel(),item.getAttrEnchantLevel(),item.getBless()));
                    }
                    LinAllManager.getInstance().PenaltyAppend(item.getLogName(), getName(), count, 0);
                    /** 파일로그저장 **/
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

    // 군주 죽으면 혈원 자동 베르 //
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
                        if (war.GetWarType() == 1) {// 공성전일경우
                            L1PcInstance clan_member[] = clan.getOnlineClanMember();//
                            int castle_id = war.GetCastleId();
                            int[] loc = new int[3];
                            loc = L1CastleLocation.getGetBackLoc(castle_id);
                            int locx = loc[0];
                            int locy = loc[1];
                            short mapid = (short) loc[2];
                            for (int k = 0; k < clan_member.length; k++) {
                                if (L1CastleLocation.checkInWarArea(castle_id, clan_member[k])) {
                                    // 기내에있는혈원강제텔레포트
                                	new L1Teleport().teleport(clan_member[k], locx, locy, mapid, 5, true);
                                }
                            }
                        }
                        war.CeaseWar(getClanname(), enemyClanName); // 종결
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

    // 군주 죽으면 혈원 자동 베르 //
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
        	ratio = Config.경험치복구; // 79렙부터 4.9%복구0.049

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
    	sendPackets(new S_SystemMessage("서큐버스의 계약을 잃었습니다.")); 
    	L1World.getInstance().getInventory(getX(), getY(), getMapId())
    	.storeItem(ItemTable.getInstance().createItem(3000157));
    	return true; 
    	} 
    	return false; 
    	}

    
    public void deathPenalty() {
    	/* 서큐버스 추가 */ 
    	if (is_Succubus()) 
    	return;
	/* 서큐버스 추가 */
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

        int BaseEr = CalcStat.원거리회피(getAbility().getTotalDex());

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

    /*
     * public int getEr() { int er = 0; if (isKnight() || is전사()) { er =
     * getLevel() / 4; } else if (isCrown() || isElf()) { er = getLevel() / 8; }
     * else if (isDragonknight()) { er = getLevel() / 7; } else if (isDarkelf())
     * { er = getLevel() / 6; } else if (isBlackwizard()) { er = getLevel() / 9;
     * } else if (isWizard()) { er = getLevel() / 10; }
     * 
     * er += (getAbility().getTotalDex() - 8) / 2;
     * 
     * int BaseEr = CalcStat.원거리회피(getAbility().getBaseDex());
     * 
     * er += BaseEr;
     * 
     * if (hasSkillEffect(L1SkillId.STRIKER_GALE)){ er = er / 3; }
     * 
     * if (hasSkillEffect(L1SkillId.DRESS_EVASION)) { er += 18; // 12->18변경 } if
     * (hasSkillEffect(L1SkillId.SOLID_CARRIAGE)) { er += 15; } if
     * (hasSkillEffect(L1SkillId.MIRROR_IMAGE)) { er += 8; }
     * 
     * return er; }
     */

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
            return "군주";
        } else if (isKnight()) {
            return "기사";
        } else if (isElf()) {
            return "엘프";
        } else if (isWizard()) {
            return "마법사";
        } else if (isDarkelf()) {
            return "다크엘프";
        } else if (isDragonknight()) {
            return "용기사";
        } else if (isBlackwizard()) {
            return "환술사";
        } else if (is전사()) {
            return "전사";
        }

        return "직업명";
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

    public boolean is전사() {
        return (getClassId() == CLASSID_전사_MALE || getClassId() == CLASSID_전사_FEMALE);
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

    /** 로봇시스템 **/
    private L1RobotAI _robotAi = null;

    public L1RobotAI getRobotAi() {
        return _robotAi;
    }

    public void setRobotAi(L1RobotAI ai) {
        _robotAi = ai;
    }

    /** 로봇시스템 **/

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

    /** 생존의외침 **/
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

    /** 생존의외침 **/

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
   //     WeekQuestTable.getInstance().SaveQuestData(this);
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

    /** 해당레벨 이상시 조건을 출력한다 **/
    private void levelUp(int gap) {
        resetLevel();
        // Abyss(this);
        /*
         * if (getLevel() > 50) { sendPackets(new S_SystemMessage("\\aD힘:" +
         * getAbility().getStr() + "  덱스:" + getAbility().getDex() + "  콘:" +
         * getAbility().getCon() + "  인트:" + getAbility().getInt() + "  위즈:" +
         * getAbility().getWis() + "  카리:" + getAbility().getCha())); }
         */

        /** 특정렙 이상 초보혈맹 자동탈퇴 **/
        String BloodName = getClanname();
        if (getLevel() >= Config.신규혈맹보호레벨 && BloodName.equalsIgnoreCase("신규보호혈맹")) {
            try {
                L1Clan clan = L1World.getInstance().getClan("신규보호혈맹");
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
                    sendPackets(new S_SystemMessage("환생의 물약 입수에 실패했습니다."));
                }
            } catch (Exception e) {
                _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
                sendPackets(new S_SystemMessage("환생의 물약 입수에 실패했습니다."));
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
        // 레벨업(퀘스트 선물 외부화) 1~현재레벨까지 반복하면서 검색
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
                    sendPackets(new S_ChatPacket("Level(" + _lv + ")퀘스트를 완료 하였습니다."));
                    getQuest().set_end(_lv);
                }
            }
        }
        // **** 스냅퍼 개방 메시지 ****//
        int lv59_step = quest.get_step(L1Quest.QUEST_SLOT76);
        if (getLevel() == 59 && lv59_step != L1Quest.QUEST_END) {
            sendPackets(new S_SystemMessage("\\aA알림:당신은 [59]레벨 귀걸이개방이 가능해졌습니다."));
            this.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "축하: 당신은 [59]레벨 달성으로 스냅퍼에게 귀걸이개방이 가능합니다."));
            this.sendPackets(new S_NewCreateItem(S_NewCreateItem.신규패킷10, 0));
        }
        int lv76_step = quest.get_step(L1Quest.QUEST_SLOT76);
        if (getLevel() == 76 && lv76_step != L1Quest.QUEST_END) {
            sendPackets(new S_SystemMessage("\\aA알림:당신은 [76]레벨 반지개방이 가능해졌습니다."));
            this.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "축하: 당신은 [76]레벨 달성으로 스냅퍼에게 반지개방이 가능합니다."));
        }
        int lv81_step = quest.get_step(L1Quest.QUEST_SLOT81);
        if (getLevel() == 81 && lv81_step != L1Quest.QUEST_END) {
            sendPackets(new S_SystemMessage("\\aA알림:당신은 [81]레벨 반지개방이 가능해졌습니다."));
            this.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "축하: 당신은 [81]레벨 달성으로 스냅퍼에게 반지개방이 가능합니다."));

        }
        // **** 스냅퍼 개방 메시지 ****//

        if (getLevel() >= 51 && getLevel() - 50 > getBonusStats() && getAbility().getAmount() < 210) {
            int upstat = (getLevel() - 50) - (getBonusStats());
            String s = Integer.toString(upstat);
            sendPackets(new S_Message_YN(479, s));

        }
        if (getLevel() >= 99) { // 숨겨진 계곡 , 수련던전
            if (getMapId() == 2005 || getMapId() >= 25 && getMapId() <= 28) {
                int[] loc = L1TownLocation.getGetBackLoc(L1TownLocation.TOWNID_SILVER_KNIGHT_TOWN);
                new L1Teleport().teleport(this, loc[0], loc[1], (short) loc[2], this.getHeading(), true);
            }
        }
        if (getLevel() >= 60) { // 클라우디아 레벨 60까지만 그후 자동텔
            if (getMapId() == 7783 || getMapId() == 12146 || getMapId() == 12149 || getMapId() == 12147
                    || getMapId() == 12148) {
                int[] loc = L1TownLocation.getGetBackLoc(L1TownLocation.TOWNID_GIRAN);
                new L1Teleport().teleport(this, loc[0], loc[1], (short) loc[2], this.getHeading(), true);

            }
        }
        if (getLevel() >= Config.버땅제한레벨) { // 본섭은 52렙까지 가능하다.
            if (getMapId() == 777) { // 버림받은 사람들의 땅(그림자의 신전)
            	new L1Teleport().teleport(this, 34043, 32184, (short) 4, 5, true);
            } else if (getMapId() == 778 || getMapId() == 779) {
                // 버림받은 사람들의땅(욕망의 동굴)
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
                sendPackets(new S_SystemMessage("\\aG[경고] 한번 더 레벨 다운시 캐릭터가 압류 됩니다."));

            }
            if (!isGm() && getHighLevel() - getLevel() >= Config.LEVEL_DOWN_RANGE) {
                sendPackets(new S_ServerMessage(64));
                sendPackets(new S_Disconnect());
                _log.info(String.format("레벨 다운의 허용 범위를 넘었기 때문에 %s를 강제 절단 했습니다.", getName()));
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

    // UI DG표시
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

    /*public void resetBaseDmgup() {
        int newBaseDmgup = 0;
        int newBaseBowDmgup = 0;
        int newBaseStatDmgup = CalcStat.calcDmgup(getAbility().getBaseStr());
        int newBaseStatBowDmgup = CalcStat.calcBowDmgup(getAbility().getBaseDex());
        if (isKnight() || isDragonknight() || isDarkelf()) {
            newBaseDmgup = getLevel() / 10;
            newBaseBowDmgup = 0;
        } else if (isElf()) {
            newBaseDmgup = 0;
            newBaseBowDmgup = getLevel() / 10;
        }
        addDmgup((newBaseDmgup + newBaseStatDmgup) - _baseDmgup);
        addBowDmgup((newBaseBowDmgup + newBaseStatBowDmgup) - _baseBowDmgup);
        _baseDmgup = newBaseDmgup + newBaseStatDmgup;
        _baseBowDmgup = newBaseBowDmgup + newBaseStatBowDmgup;
    }*/

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
        } else if (is전사()) {
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
		final int lvlTable[] = new int[] { 30, 25, 20, 16, 14, 12, 11, 10, 9,3, 2 };

		int regenLvl = Math.min(10, getLevel());
		if (30 <= getLevel() && isKnight()) {
			regenLvl = 11;
		}

		synchronized (this) {//피틱속도조절
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

    // 범위외가 된 인식이 끝난 오브젝트를 제거(버경)
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
                        || (obj instanceof L1PcInstance && ((L1PcInstance) obj)._destroyed)) { // 범위외가 되는 거리
                    removeKnownObject(obj);
                    sendPackets(new S_RemoveObject(obj));
                }
            }
        } catch (Exception e) {
            System.out.println("removeOutOfRangeObjects 에러 : " + e);
        }
    }

    // 오브젝트 인식 처리(버경)
    public void UpdateObject() {
        try {
            try {
                removeOutOfRangeObjects(17);
            } catch (Exception e) {
                System.out.println("removeOutOfRangeObjects(17) 에러 : " + e);
            }

            // 화면내의 오브젝트 리스트를 작성
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
            System.out.println("UpdateObject() 에러 : " + e);
        }
    }

    public void CheckChangeExp() {
        int level = ExpTable.getLevelByExp(getExp());
        int char_level = CharacterTable.getInstance().PcLevelInDB(getId());
        if (char_level == 0) { // 0이라면..에러겟지?
            return; // 그럼 그냥 리턴
        }
        int gap = level - char_level;
        if (gap == 0) {
            sendPackets(new S_OwnCharStatus(this));
            // sendPackets(new S_Exp(this));
            int percent = ExpTable.getExpPercentage(char_level, getExp());
            if (char_level >= 60 && char_level <= 64) {
                if (percent >= 10)
                    removeSkillEffect(L1SkillId.레벨업보너스);
            } else if (char_level >= 65) {
                if (percent >= 5) {
                    removeSkillEffect(L1SkillId.레벨업보너스);
                }
            }
            return;
        }

        // 레벨이 변화했을 경우
        if (gap > 0) {
            levelUp(gap);
            if (getLevel() >= 60) {
                setSkillEffect(L1SkillId.레벨업보너스, 10800000);
                sendPackets(new S_PacketBox(10800, true, true), true);
            }
        } else if (gap < 0) {
            levelDown(gap);
            removeSkillEffect(L1SkillId.레벨업보너스);
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
                    "SELECT `TamEndTime` FROM `characters` WHERE account_name = ? ORDER BY `TamEndTime` ASC"); // 케릭터
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
            pstm = con.prepareStatement("SELECT * FROM `characters` WHERE account_name = ?"); // 케릭터 테이블에서 골라와서
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
                                deleteTime = new Timestamp(sysTime + (86400000 * (long) day) + 10000);// 7일
                                // deleteTime = new Timestamp(sysTime +
                                // 1000*60);//7일

                                if (getId() == char_objid) {
                                    setTamTime(deleteTime);
                                }
                                con2 = L1DatabaseFactory.getInstance().getConnection();
                                pstm2 = con2.prepareStatement(
                                        "UPDATE `characters` SET TamEndTime=? WHERE account_name = ? AND objid = ?"); // 케릭터 테이블에서 군주만 골라와서
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
            pstm = con.prepareStatement("SELECT day FROM `tam` WHERE objid = ? order by id asc limit 1"); // 케릭터 테이블에서 군주만 골라와서
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
 

    public void cancelAbsoluteBarrier() { // 아브소르트바리아의 해제
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

    /** 나이설정 **/
    public int getAge() {
        return _age;
    }

    public void setAge(int i) {
        _age = i;
    }

    /** 나이설정 **/

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

    /** 패키지상점 **/
    private int CashStep = 0;

    public int getCashStep() {
        return CashStep;
    }

    public void setCashStep(int cashStep) {
        CashStep = cashStep;
    }

    /** 패키지상점 **/

    /** 로봇 시작 **/
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

    // 콤보시스템
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

    /** 로봇 종료 **/

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

    /** 바포메트 시스템 **/
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

    /** 코마버프 시작 **/
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

    /** 코마버프 끝 **/
    /** 인챈트 버그 예외 처리 */
    private int _enchantitemid = 0;

    public int getLastEnchantItemid() {
        return _enchantitemid;
    }

    /** 장신구인첸트리뉴얼 **/
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

    public int _트루타켓 = 0;

    public int get트루타켓() {
        return _트루타켓;
    }

    public void set트루타켓(int i) {
        _트루타켓 = i;
    }

    public void setLastEnchantItemid(int i, L1ItemInstance item) {
        // 혹시모를 방지 임시추가
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

    // 얼음던전PC
    private int icedungeon;

    public int geticedungeonTime() {
        return icedungeon;
    }

    public void seticedungeonTime(int oren) {
        icedungeon = oren;
    }

    // 발록진영
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

    /** 정령의무덤 **/
    private int soulon;

    public int getSoulTime() {
        return soulon;
    }

    public void setSoulTime(int soul) {
        soulon = soul;
    }

    /** 물약 회복량 **/
    private int _potionRecoveryRate = 0;

    public int getPotionRecoveryRatePct() {
        return _potionRecoveryRate;
    }

    public void addPotionRecoveryRatePct(int i) {
        _potionRecoveryRate += i;
    }

    /** 물약 회복량 **/

    /** 미니게임 **/
    // 주사위
    private boolean _isGambling = false;
    private boolean _isGambleReady = false;
    public boolean isGambleReady(){
    	return _isGambleReady;
    }
    public void setGambleReady(boolean flag){
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

    // 소막
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
     * 클랜 매칭 신청,요청 목록 유저가 사용할땐 배열에 혈맹의 이름을 넣고 군주가 사용할땐 배열에 신청자의 이름을 넣는다.
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

    private String _clanMemberNotes="";

    public String getClanMemberNotes() {
        return _clanMemberNotes;
    }

    public void setClanMemberNotes(String s) {
        _clanMemberNotes = s;
    }

    // 아놀드 이벤트 드랍
    private void drop() {
        if (getInventory().checkEquipped(21095)) { // 착용한 아이템
            L1ItemInstance drop = ItemTable.getInstance().createItem(30145); // 드랍 시킬 아이템
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
    //고대의가호
    private void drop1() {
        if (getInventory().checkEquipped(900022)) { // 착용한 아이템
            for (L1ItemInstance item : getInventory().getItems()) {
                if (item.getItemId() == 900022 & item.isEquipped()) {
                    sendPackets(new S_ServerMessage(3802));
                    getInventory().removeItem(item, 1);
                    break;
                }
            }
        }
    }
    // 서큐버스퀸의 계약
    private void drop2() {
        if (getInventory().checkEquipped(900040)) { // 착용한 아이템
            L1ItemInstance drop = ItemTable.getInstance().createItem(3000158); // 드랍 시킬 아이템
            for (L1ItemInstance item : getInventory().getItems()) {
                if (item.getItemId() == 900040 & item.isEquipped()) {
                    sendPackets(new S_ServerMessage(3802));
                    sendPackets(new S_SystemMessage("서큐버스퀸의 계약을 잃었습니다."));
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
    
	private void huntoption(L1PcInstance pc) { //해당맵 이펙트 보여주기
		if(pc.getHuntCount() != 0){
			if(pc.isWizard() || pc.isBlackwizard()){
				if(pc.getHuntPrice() == Config.수배1단){
					pc.addSp(-1);
					pc.sendPackets(new S_SPMR(pc));
				} else if(pc.getHuntPrice() == Config.수배2단){
					pc.addSp(-2);
					pc.sendPackets(new S_SPMR(pc));
				} else if(pc.getHuntPrice() == Config.수배3단){
					pc.addSp(-3);
					pc.sendPackets(new S_SPMR(pc));
				}
			} else if (pc.isCrown() || pc.isKnight() || pc.isDarkelf() || pc.isDragonknight() || pc.isElf() || pc.is전사()){
				if(pc.getHuntPrice() == Config.수배1단){
					pc.addDmgup(-1);
					pc.addBowDmgup(-1);
				} else if(pc.getHuntPrice() == Config.수배2단){
					pc.addDmgup(-2);
					pc.addBowDmgup(-2);
				} else if(pc.getHuntPrice() == Config.수배3단){
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

    // 오토방지코드
    private void showAutoAuthDialog() {
        ++_autoCheckCount;
        Random random = new Random(System.nanoTime());
        _autoAuthCode = String.format("%01d", random.nextInt(10));
        sendPackets(new S_ChatPacket(this, "오토방지: " + _autoAuthCode + " + " + 3 + " = ??", Opcodes.S_SAY, 2));
        sendPackets(new S_SystemMessage("오토방지코드: " + _autoAuthCode + " + " + 3 + " = ?? 미입력시 압류 됩니다."));
        _lastAutoCheckTime = (int) (System.currentTimeMillis() / 1000) - getAutoCheckDuration() + 120;
        // 2분간유예기간.
    }

    public boolean waitAutoAuth() {
        return _autoCheckCount > 0;
    }

    public String getAutoAuthCode() {// 스트링
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
	
	public int getAttackLevelCount(){
		int level1 = getLevel();
		/*if (getGfxId().getGfxId() == 13153) {
			return 12;
		} else if (getGfxId().getGfxId() == 13152) {
			return 13;
		} else*/
		if(level1 < 30){
			return 1;
		}else if(level1 < 45){
			return 2;
		}else if(level1 < 50){
			return 3;
		}else if(level1 < 52){
			return 4;
		}else if(level1 < 55){
			return 5;
		}else if(level1 < 60){
			return 6;
		}else if(level1 < 65){
			return 7;
		}else if(level1 < 70){
			return 8;
		}else if(level1 < 75){
			return 9;
		}else if(level1 < 80){
			return 10;
		}else if(level1  >= 80){
			return 11;
		}
		return 0;
	}

		

}