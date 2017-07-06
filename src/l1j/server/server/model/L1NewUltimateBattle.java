package l1j.server.server.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.UBSpawnTable;
import l1j.server.server.datatables.UBTable;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Item;
import l1j.server.server.utils.IntRange;

// Referenced classes of package l1j.server.server.model:
// L1UltimateBattle

public class L1NewUltimateBattle {
	private int _locX;

	private int _locY;

	private L1Location _location;

	private short _mapId;

	private int _locX1;

	private int _locY1;

	private int _locX2;

	private int _locY2;

	private int _ubId;

	private int _pattern;

	private boolean _isNowUb;

	private boolean _active;

	private int _minLevel;

	private int _maxLevel;

	private int _maxPlayer;

	private boolean _enterRoyal;

	private boolean _enterKnight;

	private boolean _enterMage;

	private boolean _enterElf;

	private boolean _enterDarkelf;

	private boolean _enterDragonknight;

	private boolean _enterBlackwizard;

	private boolean _enter전사;

	private boolean _enterMale;

	private boolean _enterFemale;

	private boolean _usePot;

	private int _hpr;

	private int _mpr;

	// private static Random random = new Random(System.nanoTime());
	private static int BEFORE_MINUTE = 5;

	private static int ubcount = 0;

	private Set<Integer> _managers = new HashSet<Integer>();

	private SortedSet<Integer> _ubTimes = new TreeSet<Integer>();

	private static final Logger _log = Logger.getLogger(L1NewUltimateBattle.class.getName());

	private final ArrayList<L1PcInstance> _members = new ArrayList<L1PcInstance>();

	private void sendRoundMessage(int curRound) {
		switch (_ubId) {
		case 1:
		case 2:
		case 3:
		case 5:
			if (curRound == 1) {
				sendMessage("콜롯세움 관리인: 제 1 군 투입!"); // 스타트
			} else if (curRound == 2) {
				sendMessage("콜롯세움 관리인: 제 2 군 투입!"); // 스타트
			} else if (curRound == 3) {
				sendMessage("콜롯세움 관리인: 제 3 군 투입!"); // 스타트
			} else if (curRound == 4) {
				sendMessage("콜롯세움 관리인: 최종전 개시! 제한 시간은 5분 입니다"); // 스타트
			}
			break;
		case 4:
			if (curRound == 1) {
				sendMessage("콜롯세움 관리인: 제 1 군 투입!"); // 스타트
			} else if (curRound == 2) {
				sendMessage("콜롯세움 관리인: 제 2 군 투입!"); // 스타트
			} else if (curRound == 3) {
				sendMessage("콜롯세움 관리인: 최종전 개시! 제한 시간은 5분 입니다"); // 스타트
			}
			break;
		}
	}

	private void spawnSupplies(int curRound) {
		switch (_ubId) {
		case 1:
		case 2:
			if (curRound == 1) {
				spawnGroundItem(L1ItemId.ADENA, 1000, 60);
				spawnGroundItem(L1ItemId.POTION_OF_CURE_POISON, 3, 20);
				spawnGroundItem(L1ItemId.POTION_OF_EXTRA_HEALING, 5, 20);
				spawnGroundItem(L1ItemId.POTION_OF_GREATER_HEALING, 3, 20);
				spawnGroundItem(40317, 1, 5);
				spawnGroundItem(40079, 1, 10);
				sendMessage("콜롯세움 관리인: 제 1 군의 투입이 완료되었습니다.");
				sendMessage("콜롯세움 관리인: 1분 후에 제 2 군의 투입이 시작됩니다.");
			} else if (curRound == 2) {
				spawnGroundItem(L1ItemId.ADENA, 5000, 50);
				spawnGroundItem(L1ItemId.POTION_OF_CURE_POISON, 7, 20);
				spawnGroundItem(L1ItemId.POTION_OF_EXTRA_HEALING, 10, 20);
				spawnGroundItem(L1ItemId.POTION_OF_GREATER_HEALING, 5, 20);
				spawnGroundItem(40317, 1, 7);
				spawnGroundItem(40093, 1, 10);
				spawnGroundItem(40079, 1, 10);
				sendMessage("콜롯세움 관리인: 제 2 군의 투입이 완료되었습니다.");
				sendMessage("콜롯세움 관리인: 2분 후에 제 3 군의 투입이 시작됩니다.");
			} else if (curRound == 3) {
				spawnGroundItem(L1ItemId.ADENA, 10000, 30);
				spawnGroundItem(L1ItemId.POTION_OF_CURE_POISON, 7, 20);
				spawnGroundItem(L1ItemId.POTION_OF_EXTRA_HEALING, 20, 20);
				spawnGroundItem(L1ItemId.POTION_OF_GREATER_HEALING, 10, 10);
				spawnGroundItem(40317, 1, 10);
				spawnGroundItem(40094, 1, 10);
				spawnGroundItem(40079, 1, 10);
				sendMessage("콜롯세움 관리인: 제 3 군까지의 투입이 완료되었습니다.");
				sendMessage("콜롯세움 관리인: 6분 후에 최종전이 시작됩니다.");
			}
			break;
		case 3:
			if (curRound == 1) {
				spawnGroundItem(L1ItemId.ADENA, 1000, 60);
				spawnGroundItem(L1ItemId.POTION_OF_CURE_POISON, 3, 20);
				spawnGroundItem(L1ItemId.POTION_OF_EXTRA_HEALING, 5, 20);
				spawnGroundItem(L1ItemId.POTION_OF_GREATER_HEALING, 3, 20);
				spawnGroundItem(40317, 1, 5);
				spawnGroundItem(40079, 1, 10);
				sendMessage("콜롯세움 관리인: 제 1 군의 투입이 완료되었습니다.");
				sendMessage("콜롯세움 관리인: 1분 후에 제 2 군의 투입이 시작됩니다.");
			} else if (curRound == 2) {
				spawnGroundItem(L1ItemId.ADENA, 3000, 50);
				spawnGroundItem(L1ItemId.POTION_OF_CURE_POISON, 5, 20);
				spawnGroundItem(L1ItemId.POTION_OF_EXTRA_HEALING, 10, 20);
				spawnGroundItem(L1ItemId.POTION_OF_GREATER_HEALING, 5, 20);
				spawnGroundItem(40317, 1, 7);
				spawnGroundItem(40093, 1, 10);
				sendMessage("콜롯세움 관리인: 제 2 군의 투입이 완료되었습니다.");
				sendMessage("콜롯세움 관리인: 2분 후에 제 3 군의 투입이 시작됩니다.");
			} else if (curRound == 3) {
				spawnGroundItem(L1ItemId.ADENA, 5000, 30);
				spawnGroundItem(L1ItemId.POTION_OF_CURE_POISON, 10, 20);
				spawnGroundItem(L1ItemId.POTION_OF_EXTRA_HEALING, 15, 20);
				spawnGroundItem(L1ItemId.POTION_OF_GREATER_HEALING, 7, 10);
				spawnGroundItem(40317, 1, 10);
				spawnGroundItem(40094, 1, 10);
				sendMessage("콜롯세움 관리인: 제 3 군까지의 투입이 완료되었습니다.");
				sendMessage("콜롯세움 관리인: 6분 후에 최종전이 시작됩니다.");
			}
			break;
		case 4:
			if (curRound == 1) {
				spawnGroundItem(L1ItemId.ADENA, 200, 60);
				spawnGroundItem(L1ItemId.POTION_OF_CURE_POISON, 3, 20);
				spawnGroundItem(L1ItemId.POTION_OF_EXTRA_HEALING, 5, 20);
				spawnGroundItem(40317, 1, 5);
				spawnGroundItem(40079, 1, 10);
				sendMessage("콜롯세움 관리인: 제 1 군의 투입이 완료되었습니다.");
				sendMessage("콜롯세움 관리인: 2분 후에 제 2 군의 투입이 시작됩니다.");
			} else if (curRound == 2) {
				spawnGroundItem(L1ItemId.ADENA, 500, 50);
				spawnGroundItem(L1ItemId.POTION_OF_CURE_POISON, 7, 20);
				spawnGroundItem(L1ItemId.POTION_OF_EXTRA_HEALING, 12, 20);
				spawnGroundItem(L1ItemId.POTION_OF_GREATER_HEALING, 5, 20);
				spawnGroundItem(40317, 1, 7);
				spawnGroundItem(40093, 1, 10);
				sendMessage("콜롯세움 관리인: 제 2 군까지의 투입이 완료되었습니다.");
				sendMessage("콜롯세움 관리인: 6분 후에 최종전이 시작됩니다.");
			}
			break;
		case 5:
			if (curRound == 1) {
				spawnGroundItem(L1ItemId.ADENA, 1000, 60);
				spawnGroundItem(L1ItemId.POTION_OF_CURE_POISON, 3, 20);
				spawnGroundItem(L1ItemId.POTION_OF_EXTRA_HEALING, 5, 20);
				spawnGroundItem(L1ItemId.POTION_OF_GREATER_HEALING, 3, 20);
				spawnGroundItem(40317, 1, 5);
				spawnGroundItem(40079, 1, 10);
				sendMessage("콜롯세움 관리인: 제 1 군의 투입이 완료되었습니다.");
				sendMessage("콜롯세움 관리인: 1분 후에 제 2 군의 투입이 시작됩니다.");
			} else if (curRound == 2) {
				spawnGroundItem(L1ItemId.ADENA, 5000, 50);
				spawnGroundItem(L1ItemId.POTION_OF_CURE_POISON, 7, 20);
				spawnGroundItem(L1ItemId.POTION_OF_EXTRA_HEALING, 10, 20);
				spawnGroundItem(L1ItemId.POTION_OF_GREATER_HEALING, 5, 20);
				spawnGroundItem(40317, 1, 7);
				spawnGroundItem(40093, 1, 10);
				spawnGroundItem(40079, 1, 10);
				sendMessage("콜롯세움 관리인: 제 2 군의 투입이 완료되었습니다.");
				sendMessage("콜롯세움 관리인: 2분 후에 제 3 군의 투입이 시작됩니다.");
			} else if (curRound == 3) {
				spawnGroundItem(L1ItemId.ADENA, 10000, 30);
				spawnGroundItem(L1ItemId.POTION_OF_CURE_POISON, 7, 20);
				spawnGroundItem(L1ItemId.POTION_OF_EXTRA_HEALING, 20, 20);
				spawnGroundItem(L1ItemId.POTION_OF_GREATER_HEALING, 10, 10);
				spawnGroundItem(40317, 1, 10);
				spawnGroundItem(40094, 1, 10);
				spawnGroundItem(40079, 1, 10);
				sendMessage("콜롯세움 관리인: 제 3 군까지의 투입이 완료되었습니다.");
				sendMessage("콜롯세움 관리인: 6분 후에 최종전이 시작됩니다.");
			}
			break;
		}
	}

	private void removeRetiredMembers() {
		L1PcInstance[] temp = getMembersArray();
		for (int i = 0; i < temp.length; i++) {
			if (temp[i].getMapId() != _mapId) {
				removeMember(temp[i]);
			}
		}
	}

	private void sendMessage(String msg) {
		for (L1PcInstance pc : getMembersArray()) {
			pc.sendPackets(new S_SystemMessage(msg));
		}
	}

	private void spawnGroundItem(int itemId, int stackCount, int count) {
		L1Item temp = ItemTable.getInstance().getTemplate(itemId);
		if (temp == null) {
			return;
		}
		L1Location loc = null;
		L1ItemInstance item = null;
		L1GroundInventory ground = null;
		for (int i = 0; i < count; i++) {
			loc = _location.randomLocation((getLocX2() - getLocX1()) / 2, false);
			if (temp.isStackable()) {
				item = ItemTable.getInstance().createItem(itemId);
				item.setEnchantLevel(0);
				item.setCount(stackCount);
				ground = L1World.getInstance().getInventory(loc.getX(), loc.getY(), _mapId);
				if (ground.checkAddItem(item, stackCount) == L1Inventory.OK) {
					ground.storeItem(item);
				}
			} else {
				item = null;
				for (int createCount = 0; createCount < stackCount; createCount++) {
					item = ItemTable.getInstance().createItem(itemId);
					item.setEnchantLevel(0);
					ground = L1World.getInstance().getInventory(loc.getX(), loc.getY(), _mapId);
					if (ground.checkAddItem(item, stackCount) == L1Inventory.OK) {
						ground.storeItem(item);
					}
				}
			}
		}
	}

	private void clearColosseum() {
		L1MonsterInstance mob = null;
		L1Inventory inventory = null;
		for (Object obj : L1World.getInstance().getVisibleObjects(_mapId)
				.values()) {
			if (obj instanceof L1MonsterInstance) {
				mob = (L1MonsterInstance) obj;
				if (!mob.isDead()) {
					mob.setDead(true);
					mob.setActionStatus(ActionCodes.ACTION_Die);
					mob.setCurrentHp(0);
					mob.deleteMe();

				}
			} else if (obj instanceof L1Inventory) {
				inventory = (L1Inventory) obj;
				inventory.clearItems();
			}
		}
	}

	public L1NewUltimateBattle() {
	}

	class UbThread implements Runnable {
		private void countDown() throws InterruptedException {

			for (int loop = 0; loop < BEFORE_MINUTE * 60 - 15; loop++) {
				Thread.sleep(1000);
				// removeRetiredMembers();
			}
			removeRetiredMembers();

			sendMessage("콜롯세움 관리인: 이제 곧 몬스터들이 등장할 것입니다. 건투를 빕니다."); // 10초전
			Thread.sleep(5000);
			sendMessage("콜롯세움 관리인: 10초뒤에 경기를 시작 합니다."); // 10초전

			Thread.sleep(5000);
			sendMessage("콜롯세움 관리인: 5 !!"); // 5초전

			Thread.sleep(1000);
			sendMessage("콜롯세움 관리인: 4 !!"); // 4초전

			Thread.sleep(1000);
			sendMessage("콜롯세움 관리인: 3 !!"); // 3초전

			Thread.sleep(1000);
			sendMessage("콜롯세움 관리인: 2 !!"); // 2초전

			Thread.sleep(1000);
			sendMessage("콜롯세움 관리인: 1 !!"); // 1초전

			Thread.sleep(1000);
			removeRetiredMembers();
		}

		private void waitForNextRound(int curRound) throws InterruptedException {
			final int WAIT_TIME_TABLE[] = { 7, 12, 36, 18 };

			int wait = WAIT_TIME_TABLE[curRound - 1];
			if (_ubId == 4) {
				if (curRound == 1) {
					wait = 12;
				} else if (curRound == 2) {
					wait = 36;
				} else if (curRound == 3) {
					wait = 18;
				}
			}
			for (int i = 0; i < wait; i++) {
				Thread.sleep(10000);
				// removeRetiredMembers();
			}
			removeRetiredMembers();
		}

		public void run() {
			try {
				setActive(true);
				countDown();
				setNowUb(true);
				L1UbPattern pattern = null;
				ArrayList<L1UbSpawn> spawnList = null;
				for (int round = 1; round <= ubcount; round++) {
					sendRoundMessage(round);

					pattern = UBSpawnTable.getInstance().getPattern(_ubId, _pattern);

					spawnList = pattern.getSpawnList(round);

					for (L1UbSpawn spawn : spawnList) {
						if (getMembersCount() > 0) {
							spawn.spawnAll();
						}

						Thread.sleep(spawn.getSpawnDelay() * 1000);
					}

					if (getMembersCount() > 0) {
						spawnSupplies(round);
					}

					for (L1PcInstance pc : getMembersArray()) {
						UBTable.getInstance().writeUbScore(getUbId(), pc);
					}
					waitForNextRound(round);
				}

				for (L1PcInstance pc : getMembersArray()) {
					int[] loc = Getback.GetBack_Location(pc, true);
					new L1Teleport().teleport(pc, loc[0], loc[1], (short) loc[2], 5,
							true);
					removeMember(pc);
				}
				clearColosseum();
				setActive(false);
				setNowUb(false);
			} catch (Exception e) {
				_log.log(Level.SEVERE, "L1UltimateBattle[]Error", e);
			}
		}
	}

	public void start() {
		switch(getUbId()){
		case 1: L1World.getInstance().broadcastServerMessage("잠시후 기란 마을의 무한대전이 시작됩니다."); break;
		case 2: L1World.getInstance().broadcastServerMessage("잠시후 웰던 마을의 무한대전이 시작됩니다."); break;
		case 3: L1World.getInstance().broadcastServerMessage("잠시후 글루딘 마을의 무한대전이 시작됩니다."); break;
		case 4: L1World.getInstance().broadcastServerMessage("잠시후 말하는 섬의 무한대전이 시작됩니다."); break;
		case 5: L1World.getInstance().broadcastServerMessage("잠시후 은기사 마을의 무한대전이 시작됩니다."); break;
		default: break;				
		}
		// int patternsMax = UBSpawnTable.getInstance().getMaxPattern(_ubId);
		// _pattern = random.nextInt(patternsMax) + 1;
		_pattern = 1;
		if (_ubId == 4) {
			ubcount = 3;
		} else {
			ubcount = 4;
		}

		UbThread ub = new UbThread();
		GeneralThreadPool.getInstance().execute(ub);
	}

	public void addMember(L1PcInstance pc) {
		if (!_members.contains(pc)) {
			_members.add(pc);
		}
	}

	public void removeMember(L1PcInstance pc) {
		_members.remove(pc);
	}

	public void clearMembers() {
		_members.clear();
	}

	public boolean isMember(L1PcInstance pc) {
		return _members.contains(pc);
	}

	public L1PcInstance[] getMembersArray() {
		return _members.toArray(new L1PcInstance[_members.size()]);
	}

	public int getMembersCount() {
		return _members.size();
	}

	private void setNowUb(boolean i) {
		_isNowUb = i;
	}

	public boolean isNowUb() {
		return _isNowUb;
	}

	public int getUbId() {
		return _ubId;
	}

	public void setUbId(int id) {
		_ubId = id;
	}

	public short getMapId() {
		return _mapId;
	}

	public void setMapId(short mapId) {
		this._mapId = mapId;
	}

	public int getMinLevel() {
		return _minLevel;
	}

	public void setMinLevel(int level) {
		_minLevel = level;
	}

	public int getMaxLevel() {
		return _maxLevel;
	}

	public void setMaxLevel(int level) {
		_maxLevel = level;
	}

	public int getMaxPlayer() {
		return _maxPlayer;
	}

	public void setMaxPlayer(int count) {
		_maxPlayer = count;
	}

	public void setEnterRoyal(boolean enterRoyal) {
		this._enterRoyal = enterRoyal;
	}

	public void setEnterKnight(boolean enterKnight) {
		this._enterKnight = enterKnight;
	}

	public void setEnterMage(boolean enterMage) {
		this._enterMage = enterMage;
	}

	public void setEnterElf(boolean enterElf) {
		this._enterElf = enterElf;
	}

	public void setEnterDarkelf(boolean enterDarkelf) {
		this._enterDarkelf = enterDarkelf;
	}

	public void setEnterDragonknight(boolean enterDragonknight) {
		this._enterDragonknight = enterDragonknight;
	}

	public void setEnterBlackwizard(boolean enterBlackwizard) {
		this._enterBlackwizard = enterBlackwizard;
	}

	public void setEnter전사(boolean enter전사) {
		this._enter전사 = enter전사;
	}

	public void setEnterMale(boolean enterMale) {
		this._enterMale = enterMale;
	}

	public void setEnterFemale(boolean enterFemale) {
		this._enterFemale = enterFemale;
	}

	public boolean canUsePot() {
		return _usePot;
	}

	public void setUsePot(boolean usePot) {
		this._usePot = usePot;
	}

	public int getHpr() {
		return _hpr;
	}

	public void setHpr(int hpr) {
		this._hpr = hpr;
	}

	public int getMpr() {
		return _mpr;
	}

	public void setMpr(int mpr) {
		this._mpr = mpr;
	}

	public int getLocX1() {
		return _locX1;
	}

	public void setLocX1(int locX1) {
		this._locX1 = locX1;
	}

	public int getLocY1() {
		return _locY1;
	}

	public void setLocY1(int locY1) {
		this._locY1 = locY1;
	}

	public int getLocX2() {
		return _locX2;
	}

	public void setLocX2(int locX2) {
		this._locX2 = locX2;
	}

	public int getLocY2() {
		return _locY2;
	}

	public void setLocY2(int locY2) {
		this._locY2 = locY2;
	}

	public void resetLoc() {
		_locX = (_locX2 + _locX1) / 2;
		_locY = (_locY2 + _locY1) / 2;
		_location = new L1Location(_locX, _locY, _mapId);
	}

	public L1Location getLocation() {
		return _location;
	}

	public void addManager(int npcId) {
		_managers.add(npcId);
	}

	public boolean containsManager(int npcId) {
		return _managers.contains(npcId);
	}

	public void addUbTime(int time) {
		_ubTimes.add(time);
	}

	public String getNextUbTime() {
		return intToTimeFormat(nextUbTime());
	}

	private int nextUbTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
		int nowTime = Integer.valueOf(sdf.format(getRealTime().getTime()));
		SortedSet<Integer> tailSet = _ubTimes.tailSet(nowTime);
		if (tailSet.isEmpty()) {
			tailSet = _ubTimes;
		}
		return tailSet.first();
	}

	private static String intToTimeFormat(int n) {
		return n / 100 + ":" + n % 100 / 10 + "" + n % 10;
	}

	private static Calendar getRealTime() {
		TimeZone _tz = TimeZone.getTimeZone(Config.TIME_ZONE);
		Calendar cal = Calendar.getInstance(_tz);
		return cal;
	}

	public boolean checkUbTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
		Calendar realTime = getRealTime();
		realTime.add(Calendar.MINUTE, BEFORE_MINUTE);
		int nowTime = Integer.valueOf(sdf.format(realTime.getTime()));
		return _ubTimes.contains(nowTime);
	}

	private void setActive(boolean f) {
		_active = f;
	}

	public boolean isActive() {
		return _active;
	}

	public boolean canPcEnter(L1PcInstance pc) {
		_log.log(Level.FINE, "pcname=" + pc.getName() + " ubid=" + _ubId
				+ " minlvl=" + _minLevel + " maxlvl=" + _maxLevel);
		if (!IntRange.includes(pc.getLevel(), _minLevel, _maxLevel)) {
			return false;
		}

		if (!((pc.isCrown() && _enterRoyal) 
	|| (pc.isKnight() && _enterKnight)
	|| (pc.isWizard() && _enterMage)
	|| (pc.isElf() && _enterElf)
	|| (pc.isDarkelf() && _enterDarkelf)
	|| (pc.isDragonknight() && _enterDragonknight) 
	|| (pc.isBlackwizard() && _enterBlackwizard)
	|| (pc.is전사() && _enter전사))) {
			return false;
		}

		return true;
	}

	private String[] _ubInfo;

	public String[] makeUbInfoStrings() {
		if (_ubInfo != null) {
			return _ubInfo;
		}
		String nextUbTime = getNextUbTime();
		StringBuilder classesBuff = new StringBuilder();
		if (_enterBlackwizard) {
			classesBuff.append("환술사 ");
		}
		if (_enterDragonknight) {
			classesBuff.append("용기사 ");
		}
		if (_enterDarkelf) {
			classesBuff.append("다크엘프 ");
		}
		if (_enterMage) {
			classesBuff.append("마법사 ");
		}
		if (_enterElf) {
			classesBuff.append("요정 ");
		}
		if (_enterKnight) {
			classesBuff.append("기사 ");
		}
		if (_enterRoyal) {
			classesBuff.append("군주 ");
		}
		if (_enter전사) {
			classesBuff.append("전사 ");
		}
		String classes = classesBuff.toString().trim();

		StringBuilder sexBuff = new StringBuilder();
		if (_enterMale) {
			sexBuff.append("남자 ");
		}
		if (_enterFemale) {
			sexBuff.append("여자 ");
		}
		String sex = sexBuff.toString().trim();
		String loLevel = String.valueOf(_minLevel);
		String hiLevel = String.valueOf(_maxLevel);
		String teleport = _location.getMap().isEscapable() ? "가능" : "불가능";
		String res = _location.getMap().isUseResurrection() ? "가능" : "불가능";
		String pot = "가능";
		String hpr = String.valueOf(_hpr);
		String mpr = String.valueOf(_mpr);
		String summon = _location.getMap().isTakePets() ? "가능" : "불가능";
		String summon2 = _location.getMap().isRecallPets() ? "가능" : "불가능";
		_ubInfo = new String[] { nextUbTime, classes, sex, loLevel, hiLevel,
				teleport, res, pot, hpr, mpr, summon, summon2 };
		return _ubInfo;
	}
}
