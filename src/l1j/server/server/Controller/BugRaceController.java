package l1j.server.server.Controller;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.IdFactory;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.RaceTable;
import l1j.server.server.datatables.ShopTable;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.shop.L1Shop;
import l1j.server.server.serverpackets.S_AttackPacket;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1RaceTicket;
import l1j.server.server.templates.L1Racer;
import l1j.server.server.templates.L1ShopItem;

public class BugRaceController implements Runnable {
	private static BugRaceController _instance;	

	private static int RACE_INTERVAL = 3 * 60 * 1000;  //버경시작시간 젤앞에 숫자 1이면 1분

	public static final int EXECUTE_STATUS_NONE = 0;
	public static final int EXECUTE_STATUS_PREPARE = 1;
	public static final int EXECUTE_STATUS_READY = 2;
	public static final int EXECUTE_STATUS_STANDBY = 3;
	public static final int EXECUTE_STATUS_PROGRESS = 4;
	public static final int EXECUTE_STATUS_FINALIZE = 5;

	private int			_executeStatus = EXECUTE_STATUS_NONE;

	public int 			_raceCount = 0;
	long 				_nextRaceTime = System.currentTimeMillis() + 60 * 1000;
	public int 			_bugRaceState = 2;

	public int			_ticketSellRemainTime;
	public int			_raceWatingTime;
	public int			_currentBroadcastRacer;

	L1NpcInstance[] 	_npc = new L1NpcInstance[3];

	public int[]		_ticketCount = new int[5];
	public int[]		_ticketId = new int[5];
	private static Random			_rnd = new Random(System.nanoTime());
	private static DecimalFormat	_df = new DecimalFormat("#.#");

	public int			_ranking = 0;
	public boolean		_complete = false;

	List<L1ShopItem>	_purchasingList = new ArrayList<L1ShopItem>();
	public L1NpcInstance[]	_littleBugBear = new L1NpcInstance[5];
	
	int Lucky =  0;
	private static Random rnd = new Random(System.nanoTime());
	
	/** 버경 추가 **/
	private final Map<Integer, L1RaceTicket> _race = new HashMap<Integer, L1RaceTicket>();
	private L1Item _allTemplates[] = new L1Item[9000000];

	public L1Item[] getAllTemplates() {
		return _allTemplates;
	}

	private int Start_X[] = { 33522, 33520, 33518, 33516, 33514 };
	private int Start_Y[] = { 32861, 32863, 32865, 32867, 32869 };

	
//	private int[][] GFX = { 
//	{ 11095 ,11098 ,11094 ,11081 ,11082 },
//	{ 11083 ,11084 ,11085 ,11086 ,11087 },
//	{ 11088 ,11089 ,11090 ,11091 ,11092 },
//	{ 11093 ,11096 ,11097 ,11099 ,11100 }};
	
	private int[][] GFX = { 
			{ 3478, 3497, 3498, 3499, 3500 },
			{ 3479, 3501, 3502, 3503, 3504 }, 
			{ 3480, 3505, 3506, 3507, 3508 },
			{ 3481, 3509, 3510, 3511, 3512 } };



	public int[][] Number = { 
			{ 1, 2, 3, 4, 5 }, 
			{ 6, 7, 8, 9, 10 }, 
			{ 11, 12, 13, 14, 15 }, 
			{ 16, 17, 18, 19, 20 }, 
			{ 21, 22, 23, 24, 25 }, 
			{ 26, 27, 28, 29, 30 } 
	};
	public String[][] BugbearName = { 
			{ "제씨", "부르니", "버기", "큐밍", "써니" }, 
			{ "아노그", "아리", "셔나안", "헤이얀", "코키오" }, 
			{ "큐큐", "히도크", "히오스", "쿠오", "스누거" }, 
			{ "이너", "범도르", "가비", "제스퍼", "플루토" }, 
			{ "부카", "퀘니버", "그로돈", "투투", "버기" }, 
			{ "쿤두라", "투투", "쿠마토", "두렉", "두리바" }
	};

	public static int[] _time = new int[5];
	public static String _first = null;
	//티켓 초기화
	public int[] ticket = {0,0,0,0,0};
	//승률 초기화
	public double[] _winRate = {0,0,0,0,0};
	//상태 초기화
	public String[] _bugCondition = {"좋음","좋음","좋음","좋음","좋음"};
	//배율 초기화
	public double _ration[] = {0,0,0,0,0};

	public static BugRaceController getInstance() {
		if (_instance == null) {
			_instance = new BugRaceController();
		}
		return _instance;
	}

	public void run(){
		try{
			switch (_executeStatus) {
			case EXECUTE_STATUS_NONE: {
				if (checkStartRace()) {
					initRaceGame();
					_executeStatus = EXECUTE_STATUS_PREPARE;
					GeneralThreadPool.getInstance().schedule(this, 60 * 1000L);
				} else {
					GeneralThreadPool.getInstance().schedule(this, 1000L); // 1초
				}

			}
				break;

			case EXECUTE_STATUS_PREPARE: {
				startSellTicket();
				_executeStatus = EXECUTE_STATUS_READY;
				GeneralThreadPool.getInstance().schedule(this, 1000L);
			}
				break;

			case EXECUTE_STATUS_READY: {
				long remainTime = checkTicketSellTime();
				if (remainTime > 0) {
					GeneralThreadPool.getInstance().schedule(this, remainTime);
				} else {
					_executeStatus = EXECUTE_STATUS_STANDBY;
					GeneralThreadPool.getInstance().schedule(this, 1000L);
				}
			}
				break;

			case EXECUTE_STATUS_STANDBY: {
				if (checkWatingTime()) {
					startBugRace();
					_executeStatus = EXECUTE_STATUS_PROGRESS;
				}
				GeneralThreadPool.getInstance().schedule(this, 1000L);
			}
				break;

			case EXECUTE_STATUS_PROGRESS: {
				if (broadcastBettingRate()) {
					if (_complete) {
						_executeStatus = EXECUTE_STATUS_FINALIZE;
					}
				}
				GeneralThreadPool.getInstance().schedule(this, 1000L);
			}
				break;
			case EXECUTE_STATUS_FINALIZE: {
				wrapUpRace();
				_executeStatus = EXECUTE_STATUS_NONE;
				GeneralThreadPool.getInstance().schedule(this, 1000L);
			}
				break;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public boolean checkStartRace() {
		long currentTime = System.currentTimeMillis();
		if( _nextRaceTime < currentTime ) {
			_nextRaceTime = currentTime + RACE_INTERVAL;			
			return true;
		}		
		return false;
	}

	public void initRaceGame(){
		try{
			_ranking = 0;
			_complete = false;
			_first = null;
			Lucky =  rnd.nextInt(50);

			broadcastNpc("잠시 후 버그베어 경주가 시작됩니다.");
			//토탈 판매 장수 초기화 
			initTicketCount();
			//버그베어 경주 초기화 
			initNpc();
			//상점 Npc초기화
			initShopNpc();
			//버그베어 달리기 속도 지정
			sleepTime();
			//버그베어 초기화 및 로딩
			loadDog();
			//승률 초기화
			initWinRate();
			//게임시
			doorAction(false);
		} catch (Exception e) {
		}
	}

	public void initTicketCount() {
		for (int row = 0; row < 5; row++) {
			this._ticketCount[row] = 0;
		}
	}

	//생성된 Npc객체를 이니셜라이즈 한다.
	public void initNpc(){
		L1NpcInstance n = null;
		for (Object obj : L1World.getInstance().getVisibleObjects(4).values()) {
			if(obj instanceof L1NpcInstance){
				n = (L1NpcInstance) obj;
				if(n.getNpcTemplate().get_npcId() == 70041){
					_npc[0] = n;
				}else if(n.getNpcTemplate().get_npcId() == 70035){
					_npc[1] = n;
				}else if(n.getNpcTemplate().get_npcId() == 70042){
					_npc[2] = n;
				}
			}
		}
	}

	public void initShopNpc() {
		List<L1ShopItem> sellingList = new ArrayList<L1ShopItem>();

		L1Shop shop = new L1Shop(70035, sellingList, _purchasingList);
		ShopTable.getInstance().addShop(70035, shop);
		L1Shop shop1 = new L1Shop(70041, sellingList, _purchasingList);
		ShopTable.getInstance().addShop(70041, shop1);
		L1Shop shop2 = new L1Shop(70042, sellingList, _purchasingList);
		ShopTable.getInstance().addShop(70042, shop2);
	}

	private void sleepTime() {
		for (int i = 0; i < 5; i++){
			int bugState = _rnd.nextInt(5);
			int addValue = 0;

			switch(bugState) {
			case 0: _bugCondition[i] = "매우좋음"; addValue = -20; break;
			case 1: _bugCondition[i] = "좋음"; addValue = -10; break;
			case 2: _bugCondition[i] = "나쁨"; addValue = 10; break;
			case 3: _bugCondition[i] = "매우나쁨"; addValue = 20; break;
			default: _bugCondition[i] = "보통"; addValue = 0; break;
			}
			_time[i] = 260 + addValue;
		}
	}
	//승률처리 
	public void initWinRate(){
		L1Racer racer = null; 
		for(int i=0; i<5; i++){
			racer = RaceTable.getInstance().getTemplate(_littleBugBear[i].get_num());
			double rate = (double) racer.getWinCount() * 100.0 / (double) (racer.getWinCount() + racer.getLoseCount());
			_winRate[i] = Double.parseDouble(_df.format(rate));
		}
	}

	private void loadDog() {
		L1Npc dogs =null;
		List<L1PcInstance> players = null;
		for(int m = 0; m < 5; ++m){
			try{
				int randNum = _rnd.nextInt(6);
				dogs = new L1Npc();
				dogs.set_passispeed(_time[m]);
				dogs.set_family(0);
				dogs.set_agrofamily(0);
				dogs.set_picupitem(false);

				Object[] parameters = { dogs };

				_littleBugBear[m] = (L1NpcInstance) Class.forName("l1j.server.server.model.Instance.L1NpcInstance").getConstructors()[0].newInstance(parameters);
				_littleBugBear[m].setGfxId(GFX[_rnd.nextInt(4)][m]);
				_littleBugBear[m].setNameId(BugbearName[randNum][m]);
				_littleBugBear[m].setName(_littleBugBear[m].getNameId());
				_littleBugBear[m].set_num(Number[randNum][m]);
				_littleBugBear[m].setX(Start_X[m]);
				_littleBugBear[m].setY(Start_Y[m]);
				_littleBugBear[m].setMap((short) 4);
				_littleBugBear[m].setHeading(5);
				_littleBugBear[m].setId(IdFactory.getInstance().nextId());
				L1World.getInstance().storeObject(_littleBugBear[m]);
				L1World.getInstance().addVisibleObject(_littleBugBear[m]);
				players = L1World.getInstance().getVisiblePlayer(_littleBugBear[m]);
				for (L1PcInstance member : players){
					if (member != null){
						member.updateObject();
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	
	public void broadcastNpc(String msg) {
		for (int i = 0; i < 2; ++i) {
			if (_npc[i] != null) {
				_npc[i].broadcastPacket(new S_NpcChatPacket(_npc[i], msg, 2));
			}
		}
	}
	
	public void 우승자멘트(String msg) {
		for (int i = 0; i < 1; ++i) {
			if (_npc[i] != null) {
				_npc[i].broadcastPacket(new S_NpcChatPacket(_npc[i], msg, 2));
			}
		}
	}

	public void doorAction(boolean open) {
		L1DoorInstance door = null;
		for (Object object : L1World.getInstance().getObject()) {
			if (object instanceof L1DoorInstance) {
				door = (L1DoorInstance) object;
				if (door != null && door.getGfxId() == 1487) {
					if (open && door.getOpenStatus() == ActionCodes.ACTION_Close) {
						door.open();
					}
					if (!open && door.getOpenStatus() == ActionCodes.ACTION_Open) {
						door.close();
					}
				}
			}
		}
	}

	public void startSellTicket() {
		LoadNpcShopList();
		broadcastNpc("레이스표 판매를 시작하였습니다.");

		this.setBugState(0);

		_ticketSellRemainTime = 60 * 3;
	}

	public long checkTicketSellTime() {
		if (_ticketSellRemainTime == 3 * 60) {
			_ticketSellRemainTime -= 60;
			broadcastNpc("경기 시작 3분전!");
			return 30 * 1000;
		} else if (_ticketSellRemainTime == 2 * 60) { // 2
			_ticketSellRemainTime -= 60;
			broadcastNpc("경기 시작 2분전!");
			return 30 * 1000;
		} else if (_ticketSellRemainTime == 1 * 60) { // 1
			_ticketSellRemainTime -= 60;
			broadcastNpc("경기 시작 1분전!");
			return 30 * 1000;
		} else if (_ticketSellRemainTime == 1 * 30) { // 30초
			_ticketSellRemainTime = 0;
			broadcastNpc("30초 후 레이스표 판매가 마감됩니다.");
			return 30 * 1000;
		}
		initShopNpc();
		broadcastNpc("출발 준비!");
		SettingRate();
		_raceWatingTime = 5;
		return 0;
	}

	private boolean checkWatingTime() {
		setBugState(1);
		if (_raceWatingTime > 0) {
			broadcastNpc(_raceWatingTime + "초");
			--_raceWatingTime;

			return false;
		}

		return true;
	}

	private void startBugRace() {
		
		broadcastNpc("출발!");
		doorAction(true);

		StartGame();

		_currentBroadcastRacer = 0;
	}

	private boolean broadcastBettingRate() {
		if (_currentBroadcastRacer == 5) {
			return true;
		}

		if (_currentBroadcastRacer == 0) {
			broadcastNpc("배팅 배율을 발표하겠습니다.");
		}

		broadcastNpc(_littleBugBear[_currentBroadcastRacer].getNameId() + ": " + _ration[_currentBroadcastRacer] + " ");

		++_currentBroadcastRacer;

		return false;
	}

	
	public void SettingRate() {
		for (int row = 0; row < 5; row++) {
			double rate = 0;
			int total = this.getTotalTicketCount();
			int cnt = this._ticketCount[row];
			if (total == 0)
				total = 1;

			if (cnt != 0) {
				rate = (double) total / (double) cnt;
				if (Lucky == row) {
					rate *= 1.0;
				}
			}
			_ration[row] = Double.parseDouble(_df.format(rate));
			;
		}
	}
	
	
	
	
	public void AddWinCount(int j) {
		L1Racer racer = RaceTable.getInstance().getTemplate(_littleBugBear[j].get_num());
		racer.setWinCount(racer.getWinCount() + 1);
		racer.setLoseCount(racer.getLoseCount());
		SaveAllRacer(racer, _littleBugBear[j].get_num());
	}

	public void AddLoseCount(int j) {
		L1Racer racer = RaceTable.getInstance().getTemplate(_littleBugBear[j].get_num());
		racer.setWinCount(racer.getWinCount());
		racer.setLoseCount(racer.getLoseCount() + 1);
		SaveAllRacer(racer, _littleBugBear[j].get_num());
	}

	public void SaveAllRacer(L1Racer racer, int num) {
		java.sql.Connection con = null;
		PreparedStatement statement = null;
		try{
			con = L1DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE util_racer SET 승리횟수=?, 패횟수=? WHERE 레이서번호=" + num);
			statement.setInt(1, racer.getWinCount());
			statement.setInt(2, racer.getLoseCount());
			statement.execute();
		}catch(SQLException e){
			System.out.println("[::::::] SaveAllRacer 메소드 에러 발생");
		}finally{
			if(statement != null){try{statement.close();}catch(Exception e){}};
			if(con != null){try{con.close();}catch(Exception e){}};
		}
	}

	public void SetWinRaceTicketPrice(int id,double rate) {
		L1ShopItem newItem = new L1ShopItem(id, (int)(450 * rate), 1);//승리표 판매 리스트 // 레이스표 매입
		_purchasingList.add(newItem);
		initShopNpc();
	}
	
	public void SetLoseRaceTicketPrice(int id,double rate) {
		L1ShopItem newItem = new L1ShopItem(id, 0, 1);//승리표 판매 리스트 // 레이스표 매입
		_purchasingList.add(newItem);
		initShopNpc();
	}

	public void LoadNpcShopList() {
		try{
			List<L1ShopItem> sellingList = new ArrayList<L1ShopItem>();

			for(int i=0; i < 5; i++){
				//ticket[i] = 8000000 + ItemTable.getInstance().GetIssuedTicket() + 1;
				ticket[i] = 8000000 + GetIssuedTicket() + 1;
				SaveRace(ticket[i], "레이스표 #" + _littleBugBear[i].getNameId() + "-" + (i+1));
				L1ShopItem item = new L1ShopItem(ticket[i], 500, 1);//판매 리스트
				sellingList.add(item);
				this._ticketId[i] = ticket[i];
			}

			L1Shop shop = new L1Shop(70035, sellingList, _purchasingList);
			ShopTable.getInstance().addShop(70035, shop);
			L1Shop shop1 = new L1Shop(70041, sellingList, _purchasingList);
			ShopTable.getInstance().addShop(70041, shop1);
			L1Shop shop2 = new L1Shop(70042, sellingList, _purchasingList);
			ShopTable.getInstance().addShop(70042, shop2);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void reLoadNpcShopList() {
		try{
			List<L1ShopItem> sellingList = new ArrayList<L1ShopItem>();

			for(int i=0; i < 5; i++){
				SaveRace(ticket[i], "레이스표 #" + _littleBugBear[i].getNameId() + "-" + (i+1));
				L1ShopItem item = new L1ShopItem(ticket[i], 500, 1);//판매 리스트
				sellingList.add(item);
				this._ticketId[i] = ticket[i];
			}

			L1Shop shop = new L1Shop(70035, sellingList, _purchasingList);
			ShopTable.getInstance().addShop(70035, shop);
			L1Shop shop1 = new L1Shop(70041, sellingList, _purchasingList);
			ShopTable.getInstance().addShop(70041, shop1);
			L1Shop shop2 = new L1Shop(70042, sellingList, _purchasingList);
			ShopTable.getInstance().addShop(70042, shop2);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void SaveRace(int i, String j) {
		L1RaceTicket etcItem = new L1RaceTicket();
		etcItem.setType2(0);
		etcItem.setItemId(i);
		etcItem.setName(j);
		etcItem.setNameId(j);
		etcItem.setType(12);
		etcItem.setType1(12);
		etcItem.setMaterial(5);
		etcItem.setWeight(0);
		etcItem.set_price(1000);
		etcItem.setGfxId(143);
		etcItem.setGroundGfxId(151);
		etcItem.setMinLevel(0);
		etcItem.setMaxLevel(0);
		etcItem.setBless(1);
		etcItem.setTradable(false);
		etcItem.setDmgSmall(0);
		etcItem.setDmgLarge(0);
		etcItem.set_stackable(true);
		//ItemTable.getInstance().AddTicket(etcItem);
		AddTicket(etcItem);
	}

	public void goalIn(int i) {
		synchronized (this) {
			_ranking = _ranking + 1;
			//broadcastNpc(_ranking + "위 - " + _littleBugBear[i].getNameId());
			if (_ranking == 1) {
				_first = _littleBugBear[i].getName();
				SetWinRaceTicketPrice(ticket[i], _ration[i]);
				AddWinCount(i);
				우승자멘트("제 "+ _raceCount + " 회 우승자는 '" +_littleBugBear[i].getNameId() + "' 입니다.");
			} else {
				SetLoseRaceTicketPrice(ticket[i], _ration[i]);
				AddLoseCount(i);
			}
		}

		if( _ranking == 5) {
			_complete = true;
		}
	}


	public void wrapUpRace() throws Exception {
		_littleBugBear[0].deleteMe();
		_littleBugBear[1].deleteMe();
		_littleBugBear[2].deleteMe();
		_littleBugBear[3].deleteMe();
		_littleBugBear[4].deleteMe();
		_raceCount = _raceCount + 1;
		setBugState(2);
		broadcastNpc("다음 경기를 준비중입니다.");
	}

	public void BroadcastAllUser(String text) {
		for (L1PcInstance player : L1World.getInstance().getAllPlayers()) {
			try {
				player.sendPackets(new S_SystemMessage(text));
			} catch (Exception exception) {
			}
		}
	}

	private void StartGame() {
		for (int i = 0; i < 5; ++i) {
			RunBug bug = new RunBug(i);

			GeneralThreadPool.getInstance().schedule(bug, 100);
		}
	}


	public class RunBug implements Runnable
	{
		private int	_status = 0;

		private int[][] _BUG_INFO = {
				{ 45, 4, 5, 6, 50 },
				{ 42, 6, 5, 7, 50 }, 
				{ 39, 8, 5, 8, 50 }, 
				{ 36, 10, 5, 9, 50 },
				{ 33, 12, 5, 10, 50 } 
//				{46, 3, 6, 5},
//				{43, 5, 6, 7},
//				{40, 7, 6, 9},
//				{37, 9, 6, 11},
//				{34, 11, 6, 11},
		};

		private int _bugId;
		private int _remainRacingCount;
		private Random _rndGen = new Random(System.nanoTime());

		public RunBug( int bugId )
		{
			_bugId = bugId;
			_remainRacingCount = _BUG_INFO[_bugId][0];
		}

		@Override
		public void run() {
			try {
				switch (_status) {
				case 0: {
					if (_remainRacingCount == 0) {
						_remainRacingCount = _BUG_INFO[_bugId][1];
						_status = 1;
					} else {
						if (_rndGen.nextInt(100) < 1 && _rndGen.nextInt(100) > (int) (_winRate[_bugId])) {
							_littleBugBear[_bugId].broadcastPacket(new S_AttackPacket(_littleBugBear[_bugId],
									_littleBugBear[_bugId].getId(), 30));
							// GeneralThreadPool.getInstance().schedule(this,
							// 3500 - (int)(_winRate[_bugId]) * 5);
							GeneralThreadPool.getInstance().schedule(this, 2000 - (int) (_winRate[_bugId]) * 5);
						} else {
							_littleBugBear[_bugId].setDirectionMove(6);
							--_remainRacingCount;
							GeneralThreadPool.getInstance().schedule(this,
									_littleBugBear[_bugId].getNpcTemplate().get_passispeed());
						}
						break;
					}
				}
				case 1: {
					if (_remainRacingCount == 0) {
						_remainRacingCount = _BUG_INFO[_bugId][2];
						_status = 2;
					} else {
						if (_rndGen.nextInt(100) < 2 && _rndGen.nextInt(100) > (int) (_winRate[_bugId])) {
							_littleBugBear[_bugId].broadcastPacket(new S_AttackPacket(_littleBugBear[_bugId],
									_littleBugBear[_bugId].getId(), 30));
							// GeneralThreadPool.getInstance().schedule(this,
							// 3500 - (int)(_winRate[_bugId]) * 5);
							GeneralThreadPool.getInstance().schedule(this, 2000 - (int) (_winRate[_bugId]) * 5);
						} else {
							_littleBugBear[_bugId].setDirectionMove(7);
							--_remainRacingCount;

							GeneralThreadPool.getInstance().schedule(this,
									_littleBugBear[_bugId].getNpcTemplate().get_passispeed());
						}
						break;
					}
				}
				case 2: {
					if (_remainRacingCount == 0) {
						_remainRacingCount = _BUG_INFO[_bugId][3];
						_status = 3;
					} else {
						if (_rndGen.nextInt(100) < 2 && _rndGen.nextInt(100) > (int) (_winRate[_bugId])) {
							_littleBugBear[_bugId].broadcastPacket(new S_AttackPacket(_littleBugBear[_bugId],
									_littleBugBear[_bugId].getId(), 30));
							// GeneralThreadPool.getInstance().schedule(this,
							// 3500 - (int)(_winRate[_bugId]) * 5);
							GeneralThreadPool.getInstance().schedule(this, 2000 - (int) (_winRate[_bugId]) * 5);
						} else {
							_littleBugBear[_bugId].setDirectionMove(0);
							--_remainRacingCount;

							GeneralThreadPool.getInstance().schedule(this,
									_littleBugBear[_bugId].getNpcTemplate().get_passispeed());
						}
						break;
					}
				}
				case 3: {
					if (_remainRacingCount == 0) {
						_status = 4;
					} else {
						if (_rndGen.nextInt(100) < 2 && _rndGen.nextInt(100) > (int) (_winRate[_bugId])) {
							_littleBugBear[_bugId].broadcastPacket(new S_AttackPacket(_littleBugBear[_bugId],
									_littleBugBear[_bugId].getId(), 30));
							// GeneralThreadPool.getInstance().schedule(this,
							// 3500 - (int)(_winRate[_bugId]) * 5);
							GeneralThreadPool.getInstance().schedule(this, 2000 - (int) (_winRate[_bugId]) * 5);
						} else {
							_littleBugBear[_bugId].setDirectionMove(1);
							--_remainRacingCount;

							GeneralThreadPool.getInstance().schedule(this,
									_littleBugBear[_bugId].getNpcTemplate().get_passispeed());
						}
						break;
					}
				}
				case 4: {
					if (_littleBugBear[_bugId].getX() == 33527) {
						goalIn(_bugId);
					} else if (_littleBugBear[_bugId].getX() < 33522 && _rndGen.nextInt(100) < 2
							&& _rndGen.nextInt(100) > (int) (_winRate[_bugId])) {
						_littleBugBear[_bugId].broadcastPacket(new S_AttackPacket(_littleBugBear[_bugId],
								_littleBugBear[_bugId].getId(), 30));
						// GeneralThreadPool.getInstance().schedule(this, 3500 -
						// (int)(_winRate[_bugId]) * 5);
						GeneralThreadPool.getInstance().schedule(this, 2000 - (int) (_winRate[_bugId]) * 5);
					} else if (_bugId == 4
							&& (_littleBugBear[_bugId].getX() == 33496 || _littleBugBear[_bugId].getX() == 33512)) { // 4번
						_littleBugBear[_bugId].setDirectionMove(1);
						--_remainRacingCount;

						GeneralThreadPool.getInstance().schedule(this,
								_littleBugBear[_bugId].getNpcTemplate().get_passispeed());
					} else {
						_littleBugBear[_bugId].setDirectionMove(2);
						--_remainRacingCount;

						GeneralThreadPool.getInstance().schedule(this,
								_littleBugBear[_bugId].getNpcTemplate().get_passispeed());
					}
					break;
				}
				}
			}catch(Exception e){
				e.printStackTrace();
			}			
		}

	}

	public int getTotalTicketCount() {
		int total = 0;
		for(int row=0; row<5; row++) {
			total += this._ticketCount[row];
		}
		return total;
	}

	public int getBugState(){
		return this._bugRaceState;
	}

	public void setBugState(int state){
		this._bugRaceState = state;
	}

	public int getRaceCount(){
		return this._raceCount;
	}

	public void setRaceCount(int cnt){
		this._raceCount = cnt;
	}
	
	/*버경 추가*/
	public void AddTicket(L1RaceTicket race){
		_race.put(new Integer(race.getItemId()), race);
		ItemTable.getInstance().getAllTemplates()[race.getItemId()] = race;
		_allTemplates[race.getItemId()] = race;
	}

	public int GetIssuedTicket(){
		return _race.size();
	}
}
