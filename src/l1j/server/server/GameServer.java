package l1j.server.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.GameSystem.AttendanceController;
import l1j.server.GameSystem.TimeEventController;
import l1j.server.GameSystem.Boss.BossTimeController;
import l1j.server.GameSystem.Boss.NewBossSpawnTable;
import l1j.server.GameSystem.Robot.Robot;
import l1j.server.GameSystem.Robot.Robot_ConnectAndRestart;
import l1j.server.GameSystem.Robot.Robot_Crown;
import l1j.server.GameSystem.Robot.Robot_Hunt;
import l1j.server.GameSystem.Robot.Robot_Location;
import l1j.server.IndunSystem.MiniGame.BattleZone;
import l1j.server.RobotSystem.RobotAIThread;
import l1j.server.server.Controller.AdenaHuntController;
import l1j.server.server.Controller.ArnoldBackEvent;
import l1j.server.server.Controller.AuctionTimeController;
import l1j.server.server.Controller.BraveavatarController;
import l1j.server.server.Controller.BugRaceController;
import l1j.server.server.Controller.ClanBuffController;
import l1j.server.server.Controller.CrockController;
import l1j.server.server.Controller.DevilController;
import l1j.server.server.Controller.DungeonQuitController;
import l1j.server.server.Controller.EventItemController;
import l1j.server.server.Controller.FishingTimeController;
import l1j.server.server.Controller.GhostController;
import l1j.server.server.Controller.HouseTaxTimeController;
import l1j.server.server.Controller.HpMpRegenController;
import l1j.server.server.Controller.InvSwapController;
import l1j.server.server.Controller.IsleController;
import l1j.server.server.Controller.LoginController;
import l1j.server.server.Controller.NpcChatTimeController;
import l1j.server.server.Controller.OneTimeController;
import l1j.server.server.Controller.PcInventoryDeleteController;
import l1j.server.server.Controller.PremiumTimeController;
import l1j.server.server.Controller.RankingTimeController;
import l1j.server.server.Controller.ShipTimeController;
import l1j.server.server.Controller.TamController;
import l1j.server.server.Controller.UbTimeController;
import l1j.server.server.Controller.WarTimeController;
import l1j.server.server.clientpackets.C_NPCAction2;
import l1j.server.server.datatables.AccessoryEnchantList;
import l1j.server.server.datatables.AccountAttendanceTable;
import l1j.server.server.datatables.ArmorEnchantList;
import l1j.server.server.datatables.AttendanceTable;
import l1j.server.server.datatables.CastleTable;
import l1j.server.server.datatables.CharacterBalance;
import l1j.server.server.datatables.CharacterHitRate;
import l1j.server.server.datatables.CharacterReduc;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.ChatLogTable;
import l1j.server.server.datatables.ClanTable;
import l1j.server.server.datatables.DoorSpawnTable;
import l1j.server.server.datatables.DropItemTable;
import l1j.server.server.datatables.DropTable;
import l1j.server.server.datatables.FurnitureSpawnTable;
import l1j.server.server.datatables.GetBackRestartTable;
import l1j.server.server.datatables.IpTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.KeyTable;
import l1j.server.server.datatables.LightSpawnTable;
import l1j.server.server.datatables.MapsTable;
import l1j.server.server.datatables.MobGroupTable;
import l1j.server.server.datatables.MonsterBookTable;
import l1j.server.server.datatables.NPCTalkDataTable;
import l1j.server.server.datatables.NpcActionTable;
import l1j.server.server.datatables.NpcCashShopSpawnTable;
import l1j.server.server.datatables.NpcChatTable;
import l1j.server.server.datatables.NpcShopTable;
import l1j.server.server.datatables.NpcSpawnTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.PetTable;
import l1j.server.server.datatables.PetTypeTable;
import l1j.server.server.datatables.PolyTable;
import l1j.server.server.datatables.RaceTable;
import l1j.server.server.datatables.RankTable;
import l1j.server.server.datatables.ResolventTable;
import l1j.server.server.datatables.RestoreItemTable;
import l1j.server.server.datatables.ShopTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.datatables.SpawnTable;
import l1j.server.server.datatables.SprTable;
import l1j.server.server.datatables.UBSpawnTable;
import l1j.server.server.datatables.UBTable;
import l1j.server.server.datatables.WeaponAddDamage;
import l1j.server.server.datatables.WeaponEnchantList;
import l1j.server.server.datatables.WeaponSkillTable;
import l1j.server.server.datatables.WeekQuestTable;
import l1j.server.server.model.Dungeon;
import l1j.server.server.model.ElementalStoneGenerator;
import l1j.server.server.model.Getback;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1ClanRanking;
import l1j.server.server.model.L1Cube;
import l1j.server.server.model.L1DeleteItemOnGround;
import l1j.server.server.model.L1HauntedHouse;
import l1j.server.server.model.L1Sys;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.gametime.L1GameTimeClock;
import l1j.server.server.model.gametime.RealTimeClock;
import l1j.server.server.model.item.L1TreasureBox;
import l1j.server.server.model.item.function.L1HealingPotion;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.model.trap.L1WorldTraps;
import l1j.server.server.monitor.LoggerInstance;
import l1j.server.server.utils.SQLUtil;
import l1j.server.server.utils.SystemUtil;
import manager.LinAllManagerInfoThread;

public class GameServer {
    private int chatlvl;
    private static GameServer _instance;
    private LoginController _loginController;
    private static Logger _log = Logger.getLogger(GameServer.class.getName());

    private GameServer() {
    }

    public static GameServer getInstance() {
	if (_instance == null) {
	    synchronized (GameServer.class) {
		if (_instance == null)
		    _instance = new GameServer();
	    }
	}
	return _instance;
    }

    public void initialize() throws Exception {
	double rateXp = Config.RATE_XP;
	double rateLawful = Config.RATE_LAWFUL;
	double rateKarma = Config.RATE_KARMA;
	double rateDropItems = Config.RATE_DROP_ITEMS;
	double rateDropAdena = Config.RATE_DROP_ADENA;
	double EnchantChanceWeapon = Config.ENCHANT_CHANCE_WEAPON;
	double EnchantChanceArmor = Config.ENCHANT_CHANCE_ARMOR;
	double EnchantChanceAccessory = Config.ENCHANT_CHANCE_ACCESSORY;
	chatlvl = Config.GLOBAL_CHAT_LEVEL;

	System.out.println("──────────────Server Setting────────────────");

	System.out.println(" ▶ 経験値:  " + (rateXp) + "倍");
	System.out.println(" ▶ ラウフル:  " + (rateLawful) + " 배");
	System.out.println(" ▶ 友好度:  " + (rateKarma) + "倍");
	System.out.println(" ▶ アイテム:  " + (rateDropItems) + " 倍 ");
	System.out.println(" ▶ アデナ:  " + (rateDropAdena) + "倍");
	System.out.println(" ▶ チャットLV:  " + (chatlvl) + " レベル");
	System.out.println(" ▶ 武器確率: [" + (EnchantChanceWeapon) + "] 倍");
	System.out.println(" ▶ 防具確率: [" + (EnchantChanceArmor) + "]倍");
	System.out.println(" ▶ アクセサリー確率: [" + (EnchantChanceAccessory) + "]倍");

	int maxOnlineUsers = Config.MAX_ONLINE_USERS;
	System.out.println(" ▶ 最大人数: [" + (maxOnlineUsers) + "]人");
	if (Config.ALT_NONPVP) { // Non-PvP 設定
	    System.out.println(" ▶ PK:  [可能]");
	} else {
	    System.out.println(" ▶ PK:  [不可能]");
	}
	GeneralThreadPool.getInstance();

	// 分割
	IdFactory.getInstance();
	L1WorldMap.getInstance();
	CharacterBalance.getInstance();
	CharacterHitRate.getInstance();
	CharacterReduc.getInstance();
	_loginController = LoginController.getInstance();
	_loginController.setMaxAllowedOnlinePlayers(maxOnlineUsers);

	CharacterTable.getInstance().loadAllCharName();

	// オンライン状態リセット
	CharacterTable.clearOnlineStatus();

	// ゲーム時間時計
	L1GameTimeClock.init();
	// 現在時刻の時計
	RealTimeClock.init();
	// お知らせ
	L1Sys.getInstance();
	L1Sys l1Sys = L1Sys.getInstance();
	GeneralThreadPool.getInstance().execute(l1Sys);

	C_NPCAction2.getInstance();

	KeyTable.initBossKey();

	AccessoryEnchantList.getInstance(); // アクセサリーエンチャント情報リスト
	ArmorEnchantList.getInstance(); // 防具エンチャント情報リスト
	WeaponEnchantList.getInstance(); // 武器エンチャント情報リスト

	// パッケージ店
	NpcCashShopSpawnTable.getInstance();
	NpcCashShopSpawnTable.getInstance().Start();

	// UBタイムコントローラ
	UbTimeController ubTimeContoroller = UbTimeController.getInstance();
	GeneralThreadPool.getInstance().scheduleAtFixedRate(ubTimeContoroller, 0, UbTimeController.SLEEP_TIME);

	AttendanceController.init(); // 出席
	AttendanceTable.getInstance();
	AccountAttendanceTable.getInstance();

	// 戦争タイムコントローラ
	WarTimeController warTimeController = WarTimeController.getInstance();
	GeneralThreadPool.getInstance().execute(warTimeController);

	// 精霊の石タイムコントローラ
	if (Config.ELEMENTAL_STONE_AMOUNT > 0) {
	    ElementalStoneGenerator elementalStoneGenerator = ElementalStoneGenerator.getInstance();
	    GeneralThreadPool.getInstance().scheduleAtFixedRate(elementalStoneGenerator, 0,
		    ElementalStoneGenerator.SLEEP_TIME);
	}

	// タイムイベントコントローラ（毎日）
	TimeEventController.getInstance();
	DevilController.getInstance().start(); //悪魔王の領土
	/** ログファイルの保存 **/
	LoggerInstance.getInstance();

	/** ロボットシステム **/
	RobotAIThread.init();// 追加
	/** ロボットシステム **/

	// npc shop
	NpcShopTable.getInstance();
	GeneralThreadPool.getInstance().execute(NpcShopSystem.getInstance());
	// npc shop

	// バトルゾーン
	if (Config.배틀존작동유무) {
	    BattleZone battleZone = BattleZone.getInstance();
	    GeneralThreadPool.getInstance().execute(battleZone);
	}

	/** 悪魔王狩り場 **/
	if (Config.악마왕작동유무) {
	    DevilController.getInstance().start();
	}

	/** アデン狩り場 **/
	if (Config.아덴사냥터작동유무) {
	    AdenaHuntController.getInstance().start();
	}

	/** 忘れられた島 **/
	GeneralThreadPool.getInstance().execute(IsleController.getInstance());

	OneTimeController.start();
	// プレミアムタイムコントローラ
	PremiumTimeController premiumTimeController = PremiumTimeController.getInstance();
	GeneralThreadPool.getInstance().scheduleAtFixedRate(premiumTimeController, 0, PremiumTimeController.SLEEP_TIME); // #

	// 乗車時間コントローラ
	TamController tamController = TamController.getInstance();
	GeneralThreadPool.getInstance().scheduleAtFixedRate(tamController, 0, TamController.SLEEP_TIME); // #

	// ダンジョンタイマー
	DungeonTimer dungeontimer = DungeonTimer.getInstance();
	GeneralThreadPool.getInstance().scheduleAtFixedRate(dungeontimer, 0, DungeonTimer.SleepTime);

	// イベントアイテムコントローラ
	if (Config.양말작동유무) {
	    EventItemController eventItemController = EventItemController.getInstance();
	    GeneralThreadPool.getInstance().scheduleAtFixedRate(eventItemController, 0, EventItemController.SleepTime);
	}

	// ブレイブアバター
	BraveavatarController braveavatarController = BraveavatarController.getInstance();
	GeneralThreadPool.getInstance().execute(braveavatarController);

	// アジト競売タイムコントローラ
	AuctionTimeController auctionTimeController = AuctionTimeController.getInstance();
	GeneralThreadPool.getInstance().scheduleAtFixedRate(auctionTimeController, 0, AuctionTimeController.SLEEP_TIME); // #

	// アジトの税金タイムコントローラ
	HouseTaxTimeController houseTaxTimeController = HouseTaxTimeController.getInstance();
	GeneralThreadPool.getInstance().scheduleAtFixedRate(houseTaxTimeController, 0,
		HouseTaxTimeController.SLEEP_TIME); // #

	// 釣りタイムコントローラ
	FishingTimeController fishingTimeController = FishingTimeController.getInstance();
	GeneralThreadPool.getInstance().scheduleAtFixedRate(fishingTimeController, 0, FishingTimeController.SLEEP_TIME); // #

	NpcChatTimeController npcChatTimeController = NpcChatTimeController.getInstance();
	GeneralThreadPool.getInstance().scheduleAtFixedRate(npcChatTimeController, 0, NpcChatTimeController.SLEEP_TIME); // #

	PcInventoryDeleteController pcInventoryDeleteController = PcInventoryDeleteController.getInstance();
	GeneralThreadPool.getInstance().scheduleAtFixedRate(pcInventoryDeleteController, 0,
		PcInventoryDeleteController.SLEEP_TIME); // #

	if (Config.버경작동유무) {
	    BugRaceController bugRaceController = BugRaceController.getInstance();
	    GeneralThreadPool.getInstance().execute(bugRaceController);
	}

	L1HauntedHouse hauntedHouse = L1HauntedHouse.getInstance();
	GeneralThreadPool.getInstance().execute(hauntedHouse);

	// L1Racing race = L1Racing.getInstance();
	// GeneralThreadPool.getInstance().execute(race);

	NpcTable.getInstance();
	L1DeleteItemOnGround deleteitem = new L1DeleteItemOnGround();
	deleteitem.initialize();

	if (!NpcTable.getInstance().isInitialized()) {
	    throw new Exception("Could not initialize the npc table");
	}
	WeekQuestTable.getInstance(); // ジュクウェ

	SpawnTable.getInstance();
	MobGroupTable.getInstance();
	SkillsTable.getInstance();
	PolyTable.getInstance();
	ItemTable.getInstance();
	ItemTable.getInstance().initRace();
	DropTable.getInstance();
	DropItemTable.getInstance();
	ShopTable.getInstance();
	NPCTalkDataTable.getInstance();
	L1World.getInstance();
	L1WorldTraps.getInstance();
	Dungeon.getInstance();
	NpcSpawnTable.getInstance();
	IpTable.getInstance();
	MapsTable.getInstance();
	UBSpawnTable.getInstance();
	PetTable.getInstance();
	ClanTable.getInstance();
	CastleTable.getInstance();
	L1CastleLocation.setCastleTaxRate(); // CastleTable 初期化は、次なければならない
	GetBackRestartTable.getInstance();
	DoorSpawnTable.getInstance();
	ChatLogTable.getInstance();
	WeaponSkillTable.getInstance();
	NpcActionTable.load();

	GMCommandsConfig.load();
	Getback.loadGetBack();
	PetTypeTable.load();

	// デバッグ
	L1TreasureBox.load();
	L1HealingPotion.load();

	SprTable.getInstance();
	RaceTable.getInstance();
	ResolventTable.getInstance();
	FurnitureSpawnTable.getInstance();
	NpcChatTable.getInstance();
	LightSpawnTable.getInstance();
	L1Cube.getInstance();
	Announcements.getInstance();
	WeaponAddDamage.getInstance();
	// ボス出現
	NewBossSpawnTable.getInstance();
	BossTimeController.getInstance();

	RestoreItemTable.getInstance().LoadRestoreItemTable();

	// 帰ってきたアーノルドイベント
	ArnoldBackEvent.getInstance();
	InvSwapController.getInstance();

	// 血盟ポイントバフ活性化
	ClanBuffController.getInstance();

	GeneralThreadPool.getInstance().execute(ShipTimeController.getInstance());

	GeneralThreadPool.getInstance().execute(CrockController.getInstance());

	GeneralThreadPool.getInstance().execute(GhostController.getInstance());

	DungeonQuitController dungeonquitcontroller = DungeonQuitController.getInstance();
	GeneralThreadPool.getInstance().scheduleAtFixedRate(dungeonquitcontroller, 0, DungeonQuitController.SLEEP_TIME);

	if (Config.ALT_HALLOWEENIVENT != true) {
	    Halloween();
	}
	if (Config.ALT_RABBITEVENT != true) { // 辛卯年のイベント
	    RabbitEvent();
	}
	if (Config.Use_Show_Announcecycle == true) { // 自動お知らせ
	    Announcecycle.getInstance();
	}
	L1ClanRanking.getInstance().start();
	MonsterBookTable.getInstace();
	RankTable.getInstance();
	RankingTimeController.getInstance(); // ランキングシステムを追加 - リアルタイム更新
	HpMpRegenController regen = new HpMpRegenController(1000);
	regen.start();
	Robot_ConnectAndRestart.getInstance().start_spawn();
	Robot_Hunt.getInstance().start_spawn();
	Robot_Location.setRLOC();
	TimerTask task = new TimerTask() {

	    @Override
	    public void run() {
		GMCommands.clanBot = true;
		Robot_Crown.getInstance().loadbot();
		Robot.인형 = !Robot.인형;
	    }
	};
	Timer timer = new Timer();
	timer.schedule(task, 2000);
	// ガベージコレクタの実行（Null）オブジェクトの解除
	System.gc();
	System.out.println("┌────────────────────────────────┐");
	System.out.println("│\t\tサーバーが正常に稼動しました。 - ON\t\t  │");
	System.out.println("│\t\t\tサーバーのポート : " + Config.GAME_SERVER_PORT + "\t\t\t  │");
	System.out.println("│\t\t\t  メモリ : " + SystemUtil.getUsedMemoryMB() + "M\t\t\t\t  │");
	System.out.println("│\t\t\t  プラットフォーム : Netty Base\t\t\t  │");
	System.out.println("└────────────────────────────────┘\n");
	Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());

	UBTable.getInstance().getUb(1).start();
    }

    /**
     * オンライン中のプレイヤーの両方に対してkick、文字情報の保存をする。
     */
    public void disconnectAllCharacters() {
	for (L1Clan clan : L1World.getInstance().getAllClans()) {
	    int clanid = clan.getClanId();
	    int[] time = clan.getBuffTime();
	    ClanTable.getInstance().updateBless(clanid, clan.getBless());
	    ClanTable.getInstance().updateBlessCount(clanid, clan.getBlessCount());
	    ClanTable.getInstance().updateBuffTime(time[0], time[1], time[2], time[3], clanid);
	}
	Collection<L1PcInstance> pcList = L1World.getInstance().getAllPlayers();
	for (L1PcInstance pc : pcList) {
	    if (pc == null || pc.noPlayerck2)
		continue;
	    try {
		if (pc.getNetConnection() != null) {
		    pc.getNetConnection().setActiveChar(null);
		    pc.getNetConnection().kick();
		}
		pc.logout();
	    } catch (Exception e) {

	    }
	}
    }

    public int saveAllCharInfo() {
	// exception 発生した場合-1リターン、または保存した人数リターン
	int cnt = 0;
	try {
	    for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
		cnt++;
		pc.save();
		pc.saveInventory();
	    }
	} catch (Exception e) {
	    return -1;
	}

	return cnt;
    }

    /**
     * オンライン中のプレイヤーに対してkick、文字情報の保存をする。
     */
    public static void disconnectChar(L1PcInstance pc) {
	if (pc.getNetConnection() != null) {
	    pc.getNetConnection().kick();
	}
	pc.logout();
    }

    public static void disconnectChar(String name) {
	L1PcInstance pc = L1World.getInstance().getPlayer(name);

	if (pc != null) {
	    disconnectChar(pc);
	}
    }

    private class ServerShutdownThread extends Thread {
	private final int _secondsCount;

	public ServerShutdownThread(int secondsCount) {
	    _secondsCount = secondsCount;
	}

	@Override
	public void run() {
	    L1World world = L1World.getInstance();
	    try {
		int secondsCount = _secondsCount;
		world.broadcastServerMessage("しばらくして、サーバーをシャットダウンします。");
		world.broadcastServerMessage("안전한 장소에서 로그아웃 해 주세요");
		while (0 < secondsCount) {
		    if (secondsCount <= 30) {
			System.out.println("ゲームが" + secondsCount + "秒後に終了します。ゲームを中断してください。");
			world.broadcastServerMessage("ゲームが" + secondsCount + "秒後に終了します。ゲームを中断してください。");
		    } else {
			if (secondsCount % 60 == 0) {
			    System.out.println("ゲームが" + secondsCount / 60 + "分後に終了します。");
			    world.broadcastServerMessage("ゲームが" + secondsCount / 60 + "分後に終了します。");
			}
		    }
		    Thread.sleep(1000);
		    secondsCount--;
		}
		shutdown();
	    } catch (InterruptedException e) {
		world.broadcastServerMessage("サーバーのシャットダウンが停止しました。サーバーは正常稼動中です。");
		return;
	    }
	}
    }

    private ServerShutdownThread _shutdownThread = null;

    public synchronized void shutdownWithCountdown(int secondsCount) {
	if (_shutdownThread != null) {
	    RobotAIThread.close();
	    // すでにシャットダウン要求をしている
	    // TODO エラー通知が必要かもしれない
	    return;
	}
	_shutdownThread = new ServerShutdownThread(secondsCount);
	_shutdownThread.start();
	Collection<L1PcInstance> pcList = L1World.getInstance().getAllPlayers();
	for (L1PcInstance pc : pcList) {
	    if (pc == null || pc.noPlayerck2)
		continue;
	    pc.서버다운중 = true;
	}
    }

    public void shutdown() {
	disconnectAllCharacters();
	// manager.savelog(); //サーバーダウン時にサーバーログを保存するかどうか、2014年7月12日logディビフォルダ利用時にコメントアウト
	LinAllManagerInfoThread.getInstance().ServerInfoUPDATE();
	InvSwapController.getInstance().initDB();
	RestoreItemTable.getInstance().SaveReStoreItem();
	System.exit(0);
    }

    public synchronized void abortShutdown() {
	if (_shutdownThread == null) {
	    // シャットダウン要求をしなかった
	    // TODOエラー通知が必要かもしれない
	    return;
	}

	_shutdownThread.interrupt();
	_shutdownThread = null;
    }

    public void Halloween() {
	Connection con = null;
	PreparedStatement pstm = null;
	PreparedStatement pstm1 = null;
	PreparedStatement pstm2 = null;
	PreparedStatement pstm3 = null;
	PreparedStatement pstm4 = null;
	try {
	    con = L1DatabaseFactory.getInstance().getConnection();
	    pstm = con.prepareStatement(
		    "DELETE FROM character_items WHERE item_id IN (20380, 21060, 256, 200172) AND enchantlvl < 0");
	    pstm1 = con.prepareStatement(
		    "DELETE FROM character_elf_warehouse WHERE item_id IN (20380, 21060, 256, 200172) AND enchantlvl < 0");
	    pstm2 = con.prepareStatement(
		    "DELETE FROM clan_warehouse WHERE item_id IN (20380, 21060, 256, 200172) AND enchantlvl < 0");
	    pstm3 = con.prepareStatement(
		    "DELETE FROM character_warehouse WHERE item_id IN (20380, 21060, 256, 200172) AND enchantlvl < 0");
	    pstm4 = con.prepareStatement(
		    "DELETE FROM character_package_warehouse WHERE item_id IN (20380, 21060, 256, 200172) AND enchantlvl < 0");

	    pstm4.execute();
	    pstm3.execute();
	    pstm2.execute();
	    pstm1.execute();
	    pstm.execute();
	} catch (SQLException e) {
	    _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
	} finally {
	    SQLUtil.close(pstm4);
	    SQLUtil.close(pstm3);
	    SQLUtil.close(pstm2);
	    SQLUtil.close(pstm1);
	    SQLUtil.close(pstm);
	    SQLUtil.close(con);
	}
    }

    public void RabbitEvent() { // 辛卯年のイベント
	Connection con = null;
	PreparedStatement pstm = null;
	PreparedStatement pstm1 = null;
	PreparedStatement pstm2 = null;
	PreparedStatement pstm3 = null;
	PreparedStatement pstm4 = null;
	PreparedStatement pstm5 = null;
	PreparedStatement pstm6 = null;
	PreparedStatement pstm7 = null;
	try {
	    con = L1DatabaseFactory.getInstance().getConnection();
	    pstm = con.prepareStatement(
		    "DELETE FROM character_items WHERE item_id IN (1115, 1116, 1117, 1118) AND enchantlvl < 0");
	    pstm1 = con.prepareStatement(
		    "DELETE FROM character_elf_warehouse WHERE item_id IN (1115, 1116, 1117, 1118) AND enchantlvl < 0");
	    pstm2 = con.prepareStatement(
		    "DELETE FROM clan_warehouse WHERE item_id IN (1115, 1116, 1117, 1118) AND enchantlvl < 0");
	    pstm3 = con.prepareStatement(
		    "DELETE FROM character_warehouse WHERE item_id IN (1115, 1116, 1117, 1118) AND enchantlvl < 0");
	    pstm4 = con.prepareStatement(
		    "DELETE FROM character_items WHERE item_id IN (22250, 22251, 22252) AND enchantlvl < 0");
	    pstm5 = con.prepareStatement(
		    "DELETE FROM character_elf_warehouse WHERE item_id IN (22250, 22251, 22252) AND enchantlvl < 0");
	    pstm6 = con.prepareStatement(
		    "DELETE FROM clan_warehouse WHERE item_id IN (22250, 22251, 22252) AND enchantlvl < 0");
	    pstm7 = con.prepareStatement(
		    "DELETE FROM character_warehouse WHERE item_id IN (22250, 22251, 22252) AND enchantlvl < 0");
	    pstm7.execute();
	    pstm6.execute();
	    pstm5.execute();
	    pstm4.execute();
	    pstm3.execute();
	    pstm2.execute();
	    pstm1.execute();
	    pstm.execute();
	} catch (SQLException e) {
	    _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
	} finally {
	    SQLUtil.close(pstm);
	    SQLUtil.close(pstm1);
	    SQLUtil.close(pstm2);
	    SQLUtil.close(pstm3);
	    SQLUtil.close(pstm4);
	    SQLUtil.close(pstm5);
	    SQLUtil.close(pstm6);
	    SQLUtil.close(pstm7);
	    SQLUtil.close(con);
	}
    }

}