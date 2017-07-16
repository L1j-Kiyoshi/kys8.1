package l1j.server.IndunSystem.Orim;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1EffectInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_EffectLocation;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.utils.CommonUtil;
import l1j.server.server.utils.L1SpawnUtil;

public class OrimController extends Thread {
	private static OrimController _instance;
	private boolean Close;
	private boolean _InDunStart = false;

	private boolean _InDunOpen = false;

	private final ArrayList<L1PcInstance> playmember = new ArrayList<L1PcInstance>();

	private static Random _random = new Random(System.nanoTime());
	private L1Location[] dependLoc;
	private L1Location attackLoc;
	private ArrayList<L1NpcInstance> _list = new ArrayList<L1NpcInstance>();
	private ArrayList<L1MonsterInstance> _list1 = new ArrayList<L1MonsterInstance>();
	private int spawnCount;
	private int shipType;
	private L1NpcInstance shellNpc1 = null;
	private L1NpcInstance shellNpc2 = null;
	private int totalScore;
	private int dependCount;
	private int attackCount;
	private int dangerCount;
	public boolean explain;
	int[][] _MonsterList = { 
			{ 91214, 91213, 91216, 91215, 91214 }, { 91202, 91203, 91204, 91205, 91206 },
			{ 91203, 91204, 91205, 91208, 91209 }, { 91209, 91204, 91208, 91205, 91202 },
			{ 91207, 91209, 91211, 91208, 91210 }, { 91211, 91212, 91218, 91217, 91219 },
			{ 91219, 91212, 91217, 91220, 91221 }, { 91220, 91212, 91213, 91207, 91218 },
			{ 91221, 91212, 91207, 91217, 91218 }, { 91211, 91212, 91217, 91217, 91218 },
			{ 91218, 91212, 91217, 91207, 91218 }, { 91219, 91212, 91217, 91207, 91218 },
			{ 91211, 91212, 91217, 91220, 91219 } };

	int[] _MonsterList1 = { 91202, 91203, 91204, 91205, 91206 };
	int[] _MonsterList2 = { 91207, 91204, 91205, 91208, 91209 };
	int[] _MonsterList3 = { 91209, 91204, 91208, 91205, 91206 };
	int[] _MonsterList4 = { 91210, 91209, 91211, 91208, 91207 };
	int[] _MonsterList5 = { 91211, 91212, 91213, 91213, 91215 };

	public OrimController() {
		Close = false;
	}

	public static OrimController getInstance() {
		if (_instance == null) {
			_instance = new OrimController();
		}
		return _instance;
	}

	public boolean getInDunStart() {
		return _InDunStart;
	}

	public void setInDunStart(boolean indun) {
		_InDunStart = indun;
	}

	public boolean getInDunOpen() {
		return _InDunOpen;
	}

	public void setInDunOpen(boolean indun) {
		_InDunOpen = indun;
	}

	public void addPlayMember(L1PcInstance pc) {
		playmember.add(pc);
	}

	public int getPlayMembersCount() {
		return playmember.size();
	}

	public void removePlayMember(L1PcInstance pc) {
		playmember.remove(pc);
	}

	public void clearPlayMember() {
		playmember.clear();
	}

	public boolean isPlayMember(L1PcInstance pc) {
		return playmember.contains(pc);
	}

	public L1PcInstance[] getPlayMemberArray() {
		return (L1PcInstance[]) playmember.toArray(new L1PcInstance[getPlayMembersCount()]);
	}

	private void checkMember() {
		for (L1PcInstance pc : getPlayMemberArray())
			try {
				if (((pc == null ? 1 : 0) | (pc.getMapId() != 9101 ? 1 : 0)) != 0) {
					removePlayMember(pc);
					if (pc.getParty().isLeader(pc)) Close = true;
				} else if (pc.getParty() == null) {
					Close = true;
				}
			} catch (Exception e) {
				
			}
	}

	public void attackFormat() {
		attackLoc = null;
	}

	public L1NpcInstance getShell1() {
		return shellNpc1;
	}

	public L1NpcInstance getShell2() {
		return shellNpc2;
	}

	public void setShell1(L1NpcInstance npc) {
		shellNpc1 = npc;
	}

	public void setShell2(L1NpcInstance npc) {
		shellNpc2 = npc;
	}

	public void spawnShell() {
		L1SpawnUtil.spawn5(32792, 32809, (short)9101, 4, 91240, 1, false);
		L1SpawnUtil.spawn5(32803, 32809, (short)9101, 5, 91240, 1, false);
		L1SpawnUtil.spawn5(32798, 32806, (short)9101, 5, 91232, 1, false);
	}

	public void addScore(int i) {
		totalScore += i;
		scoreMark();
	}

	public int getDeCount() {
		return dependCount;
	}

	public void addDeCount() {
		dependCount += 1;
	}

	public int getAtCount() {
		return attackCount;
	}

	public void addAtCount() {
		attackCount += 1;
	}

	public void run() {
		try {
			setInDunOpen(true);
			setInDunStart(true);
			spawnShell();
			totalScore = 0;
			explain = false;
			try {
				Thread.sleep(10000L);
			} catch (Exception e) {
				
			}
			spawnCount = getPlayMembersCount();

			MSGOrimAndPartyleader(0);
			if (!explain) {
				MSGOrimAndPartyleader(1);
			}

			MSGOrim(0, 0);
			SpawnMonster(0, 0);
			try {
				Thread.sleep(15000L);
			} catch (Exception e) {
				
			}
			getScore();
			int stage = 1;
			for (int stageNum = 1; stageNum <= 12; stageNum++) {
				dependCount = 0;
				attackCount = 0;
				shipType = 0;
				stage = stageNum;
				stageMark(stageNum);

				MSGOrim(1, stageNum);
				eventSpawn((stageNum - 1) % 3);
				try {
					Thread.sleep(15000L);
				} catch (Exception e) {
					
				}
				MSGOrim(2, stageNum);
				StarZone(1, 1);
				try {
					Thread.sleep(15000L);
				} catch (Exception e) {
					
				}
				attackFormat();

				MSGOrim(3, stageNum);
				StarZone(1, 1);
				try {
					Thread.sleep(15000L);
				} catch (Exception e) {
					
				}
				spawnShelling();

				if (Close) break;
				attackFormat();

				MSGOrim(4, stageNum);
				StarZone(1, 1);
				spawnShip(stageNum, 0);
				try {
					Thread.sleep(15000L);
				} catch (Exception e) {
					
				}
				attackFormat();

				MSGOrim(5, stageNum);
				try {
					Thread.sleep(10000L);
				} catch (Exception e) {
					
				}
				MSG1(stageNum, 0);

				MSG1(stageNum, 1);
				SpawnMonster(stageNum, 0);
				if ((stageNum == 4) || (stageNum == 8) || (stageNum == 12))
					Boss(stageNum);
				try {
					Thread.sleep(20000L);
				} catch (Exception e) {
					
				}
				getScore();
				MSG1(stageNum, 2);
				SpawnMonster(stageNum, 0);
				if (stageNum == 12)
					Boss(12);
				try {
					Thread.sleep(10000L);
				} catch (Exception e) {
					
				}
				getScore();
				if (_list1.size() > spawnCount + 1)
					MSGOrim(10, stageNum);
				try {
					Thread.sleep(10000L);
				} catch (Exception e) {
					
				}
				MSG1(stageNum, 3);
				SpawnMonster(stageNum, 0);

				if (stageNum == 12)
					Boss(12);
				try {
					Thread.sleep(15000L);
				} catch (Exception e) {
					
				}
				getScore();
				if (_list1.size() > spawnCount + 1) {
					MSGOrim(11, stageNum);
				}
				try {
					Thread.sleep(5000L);
				} catch (Exception e) {
					
				}
				getScore();

				for (int k = 0; k < 3; k++) {
					MSGOlden(1);
					SpawnMonster(stageNum, 1);
					try {
						Thread.sleep(10000L);
					} catch (Exception e) {
						
					}
					getScore();
				}

				MSGOlden(2);

				checkMember();
				Effect(3);
				try {
					Thread.sleep(2000L);
				} catch (Exception e) {
					
				}
				OrimFight summonTrd = OrimFight.getInstance(shipType);
				summonTrd.start();

				SpawnMonster(stageNum, 0);
				try {
					Thread.sleep(5000L);
				} catch (Exception e) {
					
				}
				MSGOrim(6, stageNum);
				try {
					Thread.sleep(20000L);
				} catch (Exception e) {
					
				}
				getScore();
				checkMember();

				if (Close) break;
			}
			if (stage == 12) {
				MSGOrim(12, stage);
				cuttleSpawn();
				try {
					Thread.sleep(30000L);
				} catch (Exception e) {
					
				}
			}
			End();
			_instance = null;
			_InDunStart = false;
			_InDunOpen = false;
		} catch (Exception e) {
			
		}
	}

	private void getScore() {
		checkMember();
		int subScore = 0;
		try{
		for (int i = 0; i < _list1.size(); i++) {
			L1MonsterInstance mon = (L1MonsterInstance) _list1.get(i);
			if ((mon.getCurrentHp() <= 0) || (mon.isDead())) {
				subScore += mon.getLevel() / getPlayMembersCount();
				_list1.remove(i);
			}
		}
		addScore(subScore);
		} catch (Exception e) {
			
		}
	}

	private void InDunclear() {
		_list.clear();
		_list1.clear();

		L1MonsterInstance mob = null;
		L1EffectInstance efNpc = null;
		L1NpcInstance Npc = null;

		Iterator<L1Object> localIterator = L1World.getInstance().getVisibleObjects(9101).values().iterator();
	
		while (localIterator.hasNext()) {
			Object obj = localIterator.next();
			if ((obj instanceof L1MonsterInstance)) {
				mob = (L1MonsterInstance) obj;
				if (!mob.isDead()) {
					mob.setDead(true);
					mob.setActionStatus(8);
					mob.setCurrentHp(0);
					mob.deleteMe();
				}
			} else if ((obj instanceof L1EffectInstance)) {
				efNpc = (L1EffectInstance) obj;
				if (efNpc.getNpcTemplate().get_npcId() != 91232)
					efNpc.deleteMe();
			} else if ((obj instanceof L1NpcInstance)) {
				Npc = (L1NpcInstance) obj;
				Npc.deleteMe2();
			}
		}
	}

	private void Boss(int i) {
		L1SpawnUtil.spawn3(32797, 32801, (short) 9101, 0, 91243, 1, false, 2000);
		switch (i) {
		case 4:
			int k1 = 91223 + CommonUtil.random(3);
			L1SpawnUtil.spawn5(32797, 32801, (short)9101, 4, k1, 1, false);
			break;
		case 8:
			int k2 = 91223 + CommonUtil.random(3);
			L1SpawnUtil.spawn5(32797, 32801, (short)9101, 4, k2, 1, false);
			break;
		case 12:
			for (int j = 0; j < 3; j++) {
				int bossId = 91226 + CommonUtil.random(6);
				L1SpawnUtil.spawn5(32797, 32801, (short)9101, 4, bossId, 1, false);
			}
		}
	}

	private void cuttleSpawn() {
		L1SpawnUtil.spawn5(32801, 32795, (short)9101, 4, 91200, 0, false);
		L1SpawnUtil.spawn5(32804, 32796, (short)9101, 4, 91201, 0, false);
		L1SpawnUtil.spawn5(32793, 32796, (short)9101, 4, 91201, 0, false);
	}

	private void eventSpawn(int i) {
		switch (i) {
		case 0:
			for (L1PcInstance pc : getPlayMemberArray())
				try {
					pc.sendPackets(new S_EffectLocation(32801, 32788, 8142));
					pc.sendPackets(new S_EffectLocation(32801, 32788, 8142));
				} catch (Exception e) {
					
				}
			break;
		case 1:
			for (int j = 0; j < 5; j++) {
				L1SpawnUtil.spawn6(32799, 32788, (short)9101, 91241, 15, 10000, 9101);
			}
			break;
		case 2:
			for (int j = 0; j < 5; j++)
				L1SpawnUtil.spawn6(32799, 32788, (short)9101, 91242, 20, 10000, 9101);
		}
	}

	private void spawnShip(int stageNum, int heading) {
		if ((stageNum == 4) && (totalScore > 1000)) {
			L1SpawnUtil.spawn3(32797, 32815, (short)9101, heading, 91236, 1, false, 125000);
			shipType = 1;
		} else if ((stageNum == 8) && (totalScore > 2000)) {
			L1SpawnUtil.spawn3(32797, 32819, (short)9101, heading, 91237, 1, false, 125000);
			shipType = 2;
		} else {
			L1SpawnUtil.spawn3(32797, 32819, (short)9101, heading, 91234, 1, false, 125000);
			shipType = 0;
		}
	}

	private void SpawnMonster(int stageNum, int j) {
		int ran = 0;

		if (j == 0) {
			for (int k = 0; k < spawnCount * 3 / 2; k++) {
				ran = CommonUtil.random(_MonsterList[stageNum].length);
				L1SpawnUtil.spawn5(32797, 32801, (short) 9101, 4, _MonsterList[stageNum][ran], 5, false);
			}
		} else {
			for (int k = 0; k < spawnCount; k++) {
				ran = CommonUtil.random(_MonsterList[stageNum].length);
				int x = 32789 + _random.nextInt(18);
				int y = 32796 + _random.nextInt(13);

				L1SpawnUtil.spawn3(x, y, (short)9101, 0, 91244, 1, false, 2000);

				L1SpawnUtil.spawn5(x, y, (short)9101, 4, _MonsterList[stageNum][ran], 1, false);
			}

			Effect(0);
		}
	}

	private void StarZone(int i, int j) {
		if (i == 1) {
			attackFormat();
		      int x = 32789 + _random.nextInt(18);
		      int y = 32796 + _random.nextInt(13);
			int time = j == 0 ? 10000 : 10000 + _random.nextInt(10) * 1000;
			L1SpawnUtil.spawn3(x, y, (short)9101, 0, 91238, 1, false, time);
			attackLoc = new L1Location(x, y, 9101);
		}

		if (j == 1) {
			if (spawnCount <= 2) {
				return;
			}
			dependLoc = new L1Location[spawnCount - 2];
			for (int k = 0; k < spawnCount - 2; k++) {
				dependLoc[k] = new L1Location();
			      int x1 = 32789 + _random.nextInt(18);
			      int y1 = 32796 + _random.nextInt(13);
				L1SpawnUtil.spawn3(x1, y1, (short)9101, 0, 91239, 1, false, 10000 + _random.nextInt(5) * 1000);
				dependLoc[k] = new L1Location(x1, y1, 9101);
			}
		}
	}

	private void sendMessage(String msg) {
		for (L1PcInstance pc : getPlayMemberArray())
			pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, msg));
	}

	private void sendMessage1(String msg) {
		for (L1PcInstance pc : getPlayMemberArray())
			pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, msg));
	}

	private void sendMessage2(String msg) {
		for (L1PcInstance pc : getPlayMemberArray())
			pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, msg));
	}

	private void talkPartyleader(String msg) {
		checkMember();
		L1PcInstance pc = getPlayMemberArray()[0];
		if (pc.getParty().isLeader(pc))
			for (L1PcInstance member : L1World.getInstance().getVisiblePlayer(pc)) {
				S_ChatPacket s_chatpacket = new S_ChatPacket(pc, msg, 0);
				pc.sendPackets(s_chatpacket);
				member.sendPackets(s_chatpacket);
			}
	}

	private void MSGOrimAndPartyleader(int i) {
		checkMember();
		switch (i) {
		case 0:
			sendMessage("クリップボード：あなた慌てないで聞いてください。");
			talkPartyleader("皆さん慌てないで聞いてください。");
			try {
				Thread.sleep(5000L);
			} catch (Exception e) {
			}
			sendMessage("クリップボード：マスターに会いに島を離れて本土に行っています。");
			talkPartyleader("クリップボードは今師匠に会いに島を離れて本土に行っています。");
			try {
				Thread.sleep(5000L);
			} catch (Exception e) {
			}
			sendMessage("クリップボード：私は今黒ウィザードオルディンに追われています。");
			talkPartyleader("クリップボードは今黒ウィザードオルディンにモリーシースます。");
			try {
				Thread.sleep(5000L);
			} catch (Exception e) {
			}
			sendMessage("クリップボード：状況説明が必要ない場合は、[alt + 1]に省略します。");
			talkPartyleader("状況説明が必要ない場合は、[alt + 1]に省略します。");
			try {
				Thread.sleep(5000L);
			} catch (Exception e) {
			}
		case 1:
			sendMessage("クリップボード：私たちの船を攻撃しているのは、オルディンと呼ばれるものです。");
			talkPartyleader("私たちの船を攻撃しているのは、オルディンと呼ばれるものです。");
			try {
				Thread.sleep(5000L);
			} catch (Exception e) {
			}
			sendMessage("クリップボード：まず砲を使用する方法をお知らせします。");
			talkPartyleader("まず艦砲を使用する方法をお知らせします。");
			try {
				Thread.sleep(5000L);
			} catch (Exception e) {
			}
			sendMessage("クリップボード：しばらくして床に赤い色員が生じた場合に中心に入ってください。");
			talkPartyleader("しばらくして、床に赤い色員が生じた場合に中心に入ってください。");
			try {
				Thread.sleep(3000L);
			} catch (Exception e) {
			}
			sendMessage("クリップボード：私は大砲の間にあるような柄の足場の上に立ち。");
			talkPartyleader("私は大砲の間にあるような柄の足場の上に立ち。");
			StarZone(1, 0);
			try {
				Thread.sleep(3000L);
			} catch (Exception e) {
			}
			sendMessage("クリップボード：そのあと、私[alt + 4]を押すとキャリッジ発射されます。");
			talkPartyleader("そんなの後、私[alt + 4]を押すとキャリッジ発射されます。");
			try {
				Thread.sleep(10000L);
			} catch (Exception e) {
			}
			attackFormat();
			sendMessage("クリップボード：時間がないから次のように進んでいきます。");
			talkPartyleader("時間がないから次のように進んでいきます。");
			try {
				Thread.sleep(3000L);
			} catch (Exception e) {
			}
			sendMessage("クリップボード：しかし、敵も攻撃をするので、防御も必要です。");
			talkPartyleader("しかし、敵も攻撃をするので、防御も必要です。");
			try {
				Thread.sleep(3000L);
			} catch (Exception e) {
			}
			sendMessage("クリップボード：防御のためには、黄色い丸の中に入ってください。");
			talkPartyleader("防御のためには、黄色い丸の中に入ってください。");
			StarZone(0, 1);
			try {
				Thread.sleep(3000L);
			} catch (Exception e) {
			}
			sendMessage("クリップボード：私は同じ場所で[alt + 2]で防御注文を詠唱します。");
			talkPartyleader("私は同じ場所で[alt + 2]で防御注文を詠唱します。");
			try {
				Thread.sleep(12000L);
			} catch (Exception e) {
			}
		}
	}

	private void MSGOrim(int i, int stageNum) {
		checkMember();
		switch (i) {
		case 0:
			sendMessage("オリム：今、敵の攻撃に備えてください。急いでください。");
			try {
				Thread.sleep(5000L);
			} catch (Exception e) {
			}
			sendMessage("クリップボード：船に隠れて聞いていたモンスターがいます。処置してください。");
			break;
		case 1:
			sendMessage("クリップボード：周辺に何かがぐるぐる回っている元気が検出されます。注意してください。");
			try {
				Thread.sleep(5000L);
			} catch (Exception e) {
			}
			switch (stageNum) {
			case 1:
			case 4:
			case 7:
			case 10:
				sendMessage("クリップボード：周辺にサメの群れがあります。注意してください。");
				break;
			case 2:
			case 5:
			case 8:
			case 11:
				sendMessage("クリップボード：近くに海ハーピーの生息地があるか表示します。注意してください。");
				break;
			case 3:
			case 6:
			case 9:
			case 12:
				sendMessage("クリップボード：海ドレイクね。不思議と近く行っていない。");
			}

			break;
		case 2:
			if (stageNum == 1)
				sendMessage("クリップボード：敵の船に近づいています！準備してください！");
			else if (stageNum == 4)
				sendMessage("クリップボード：的...！今回の船では、オルディンの機運が感じられます！");
			else if (stageNum == 8)
				sendMessage("クリップボード：注意してください！今回の船はいつもよりも強い力があります。");
			else {
				sendMessage("クリップボード：前より強いオーラが感じられます！注意してください！");
			}
			Effect(0);
			break;
		case 3:
			sendMessage("クリップボード：敵の攻撃に倍破損します。何度も破損した場合船沈没ので注意してください。");
			Effect(0);
			break;
		case 4:
			sendMessage("クリップボード：船はすぐに衝突し！海に陥らないように注意してください！");
			Effect(0);
			break;
		case 5:
			sendMessage("クリップボード：船の衝突すれば、敵が飛び出し者になります！注意してください！");
			Effect(0);
			break;
		case 6:
			sendMessage("クリップボード：さあ脱出してください！どこかを閉じ込めたものがあるでしょう！");
			break;
		case 7:
			sendMessage("クリップボード：船沈没する直前です。何とか船を死守する必要があります！");
			Effect(0);
			break;
		case 8:
			sendMessage("クリップボード：船沈没です！お早めに脱出してください！この状態では無理です！");
			Effect(1);
			break;
		case 9:
			sendMessage("クリップボード：極めて少ししか退けた。もう少し頑張ってください！");
			break;
		case 10:
			sendMessage("クリップボード：乱入したことが、私たち倍に損傷を与えています！急いでください！");
			break;
		case 11:
			sendMessage("クリップボード：時間があまりありません。急いでください。");
			break;
		case 12:
			sendMessage("クリップボード：私いやらしい件何！危険に見えます！");
			try {
				Thread.sleep(5000L);
			} catch (Exception e) {
			}
			sendMessage("クリップボード：ウウック！海の生き物まで攻撃してきています！よりによって、こんな時！");
			try {
				Thread.sleep(5000L);
			} catch (Exception e) {
			}
		}
	}

	private void MSG1(int stageNum, int j) {
		checkMember();
		switch (j) {
		case 0:
			sendMessage1("戦闘が開始されます[" + stageNum + "/12]");
			try {
				Thread.sleep(5000L);
			} catch (Exception e) {
			}
		case 1:
			sendMessage1("最初の部隊が乱入しました。");
			break;
		case 2:
			sendMessage1("第二部隊が乱入しました。");
			break;
		case 3:
			if ((stageNum == 4) || (stageNum == 8) || (stageNum == 12))
				MSGOrim(9, 0);
			else
				sendMessage1("第三部隊が乱入しました。");
		}
	}

	private void MSGOlden(int i) {
		checkMember();
		switch (i) {
		case 1:
			sendMessage2("オルディン：怠惰なもの！さあ私ファイト！すべてのことを一掃ちゃう！");
			break;
		case 2:
			sendMessage2("オルディン：あいつを保持つけよう！");
		}
	}

	private void stageMark(int stageNum) {
		checkMember();
		for (L1PcInstance pc : getPlayMemberArray())
			pc.sendPackets(new S_PacketBox(S_PacketBox.ROUND1, stageNum));
	}

	private void scoreMark() {
		checkMember();
		for (L1PcInstance pc : getPlayMemberArray())
		 pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,S_PacketBox.SCORE_MARK, Integer.toString(totalScore)));
}

	public void addMonList(L1NpcInstance npc) {
		if (npc == null) return;
		_list.add(npc);
	}

	private void EndTelePort() {
		checkMember();
		for (L1PcInstance c : getPlayMemberArray())
			new L1Teleport().teleport(c, 32596, 32916, (short)0, 5, true);
	}

	private void Effect(int i) {
		for (L1PcInstance c : getPlayMemberArray())
			try {
				switch (i) {
				case 0:
					c.sendPackets(new S_PacketBox(S_PacketBox.HADIN_DISPLAY, 1));
					c.sendPackets(new S_PacketBox(S_PacketBox.HADIN_DISPLAY, 2));
					break;
				case 1:
					c.sendPackets(new S_PacketBox(S_PacketBox.HADIN_DISPLAY, 1));
					break;
				case 2:
					c.sendPackets(new S_PacketBox(S_PacketBox.HADIN_DISPLAY, 2));
					break;
				case 3:
					c.sendPackets(new S_PacketBox(S_PacketBox.HADIN_DISPLAY, 4));
				}
			} catch (Exception e) {
				
			}
	}

	private void spawnShelling() {
		checkMember();
		for (int shellCount = 0; shellCount < 5 - attackCount / 2; shellCount++) {
			for (L1PcInstance pc : getPlayMemberArray())
				try {
					int x = 32789 + _random.nextInt(18);
					int y = 32796 + _random.nextInt(13);
					pc.sendPackets(new S_EffectLocation(x, y, 762));
					pc.sendPackets(new S_EffectLocation(x, 32815, 8233));
				} catch (Exception e) {
					
				}
			Effect(2);
			try {
				Thread.sleep(1000L);
			} catch (Exception e) {
				
			}
			if (shellCount + 1 > dependCount) {
				int x = 32789 + _random.nextInt(18);
				int y = 32796 + _random.nextInt(13);

				L1SpawnUtil.spawn5(x, y, (short)9101, 4, 91233, 1, false);
				dangerCount += 1;
				if (dangerCount == 15) {
					MSGOrim(7, 0);
					try {
						Thread.sleep(5000L);
					} catch (Exception e) {
						
					}
				} else if (dangerCount > 17) {
					MSGOrim(8, 0);
					try {
						Thread.sleep(10000L);
					} catch (Exception e) {
						
					}
					Close = true;
					break;
				}
			}
		}
	}

	public Boolean dependTrap() {
		int count = 0;
		if ( dependLoc.length == 0) {
			return Boolean.valueOf(false);
		}
		for (int i = 0; i < dependLoc.length; i++) {
			for (L1PcInstance pc : getPlayMemberArray())
				try {
					if ((pc.getX() == dependLoc[i].getX()) && (pc.getY() == dependLoc[i].getY()))
						count++;
				} catch (Exception e) {
					
				}
		}
		if (count == dependLoc.length) {
			return Boolean.valueOf(true);
		}
		return Boolean.valueOf(false);
	}

	public Boolean attackTrap() {
		checkMember();
		if (attackLoc == null)
			return Boolean.valueOf(false);
		boolean tr = false;
		for (L1PcInstance pc : getPlayMemberArray()) {
			try {
				if ((pc.getX() == attackLoc.getX()) && (pc.getY() == attackLoc.getY()))
					tr = true;
			} catch (Exception e) {
				
			}
		}
		return Boolean.valueOf(tr);
	}

	private void End() {
		setInDunOpen(false);
		setInDunStart(false);
		EndTelePort();
		InDunclear();
		clearPlayMember();
		L1World.getInstance().broadcastServerMessage("海上ダンジョンが初期化されました。");
	}
}