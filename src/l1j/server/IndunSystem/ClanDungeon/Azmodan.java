package l1j.server.IndunSystem.ClanDungeon;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.IdFactory;
import l1j.server.server.datatables.ClanTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_SystemMessage;


public class Azmodan implements Runnable {

	private static Logger _log = Logger.getLogger(Azmodan.class.getName());

	private int _map;

	private static Random _random = new Random(System.nanoTime());

	private boolean Running = false;

	public boolean Start = false;
	private boolean FirstLevel = false;
	private boolean SecondLevel = false;
	private boolean ThirdLevel = false;
	private boolean End = false;

	private int Time = 3600;
	
	public Azmodan(int id) {
		_map = id;
	}

	int[] _MonsterList1 = { 14901, 14902 };
	int[] _MonsterList2 = { 14900, 14901, 14902, 14903 };
	int[] _MonsterList3 = { 14901, 14902, 14904 };

	@Override
	public void run() {
		Running = true;
		FirstLevel = true;
		Time = 3600;
		
		SpawnMonster();
		while (Running) {
			try {
				Check();
				if (End) {
					reset();
					break;
				} else if (FirstLevel) {
					First();
				} else if (SecondLevel) {
					Second();
				} else if (ThirdLevel) {
					Third();
				} 
			} catch (Exception e) {
			} finally {
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
				}
			}
		}
		Azmodan_Delete();		
	}

	public void Start() {
		GeneralThreadPool.getInstance().schedule(this, 2000);
	}
	
	private void Check() {
		if (Time > 0) {
			Time--;
		}
		if (Time <= 0) {
			End();
		} else if (Time <= 3590) {
			CheckClanId();
		} 
		if (Time % 600 == 0) {
			int min = Time / 60;
			for (L1Object obj : L1World.getInstance().getVisibleObjects(_map).values()) {
				if (obj instanceof L1PcInstance) {
					L1PcInstance pc = (L1PcInstance) obj;
					pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, min+"分後ダンジョンは終了します。"));
				}
			}
		}
	}

	private void First() {
		if (_list1.size() > 0) {
			for (int i = _list1.size()-1; i >= 0; i--) {
				L1NpcInstance npc = _list1.get(i);
				if (npc.getNpcId() == 14905 && npc.getCurrentHp() <= 0) {
					remove(npc, 1);
					openDoor(14911);
					FirstLevel = false;
					SecondLevel = true;	
				}
			}
		} 
	}

	private void Second() {
		if (_list2.size() > 0) {
			for (int i = _list2.size()-1; i >= 0; i--) {
				L1NpcInstance npc = _list2.get(i);
				if (npc.getNpcId() == 14906 && npc.getCurrentHp() <= 0) {				
					remove(npc, 2);
					openDoor(14912); // 第二扉開放​​。
					SecondLevel = false;
					ThirdLevel = true;
				}
			}
		} 		
	}

	private void Third() {
		if (_list3.size() > 0) {
			for (int i = _list3.size()-1; i >= 0; i--) {
				L1NpcInstance npc = _list3.get(i);
				if (npc.getNpcId() == 14907 && npc.getCurrentHp() <= 0) {				
					remove(npc, 3);					
					ThirdLevel = false;				
				}
			}
		}		
	}

	private void Azmodan_Delete() {
		for (L1Object ob : L1World.getInstance().getVisibleObjects(_map).values()) {
			if (ob == null)
				continue;
			if (ob instanceof L1ItemInstance) {
				L1ItemInstance obj = (L1ItemInstance) ob;
				L1Inventory groundInventory = L1World.getInstance().getInventory(obj.getX(), obj.getY(), obj.getMapId());
				groundInventory.removeItem(obj);
			} else if (ob instanceof L1NpcInstance) {
				L1NpcInstance npc = (L1NpcInstance) ob;
				npc.deleteMe();
			}
		}
		for (L1Clan clan : L1World.getInstance().getAllClans()) {
			if (clan.getClanId() == AzmodanSystem.getInstance().getClanid(_map)) {	
				clan.setUnderDungeon(2);
				ClanTable.getInstance().updateUnderDungeon(clan.getClanId(), 2);	
				clan.setUnderMapid(0);			
			}
		}
		AzmodanSystem.getInstance().removeAzmodan(_map);
	}

	private void reset() {
		Running = false;
		ListClear(1);
		ListClear(2);
		ListClear(3);
		ListClear(4);		
		for (L1Object obj : L1World.getInstance().getVisibleObjects(_map).values()) {
			if (obj instanceof L1MonsterInstance) {
				L1MonsterInstance mon = (L1MonsterInstance) obj;
				mon.deleteMe();
			}
		}		
	}

	private void openDoor(int doorId) {
		L1DoorInstance door = null;
		for (L1Object object : L1World.getInstance().getVisibleObjects(_map).values()) {
			if (object instanceof L1DoorInstance) {
				door = (L1DoorInstance) object;
				if (door.getNpcTemplate().get_npcId() == doorId) {
					if (door.getOpenStatus() == ActionCodes.ACTION_Close) {
						door.open();
					}
				}
			}
		}
	}
	
	private void CheckClanId() {	
		for (L1Object obj : L1World.getInstance().getVisibleObjects(_map).values()) {
			if (obj instanceof L1NpcInstance) {
				if (((L1NpcInstance) obj).getNpcId() == 14907 && ((L1NpcInstance) obj).isDead() ) {
					for (L1Clan clan : L1World.getInstance().getAllClans()) {
						int clantime = 3600 - Time; //744
						int min = clantime / 60;    // 12
						int second = clantime - (min * 60);
						if (clan.getClanId() == AzmodanSystem.getInstance().getClanid(_map)) {
							if (clan.getRankTime() > clantime || clan.getRankTime() == 0) {
								clan.setRankTime(clantime);						
								ClanTable.getInstance().updateRankTime(clan.getClanId(), clantime);
								long now = System.currentTimeMillis();
								Timestamp time = new Timestamp(now);
								clan.setRankDate(time);
								ClanTable.getInstance().updateRankDate(clan.getClanId(), time);
								for (L1PcInstance member : clan.getOnlineClanMember()) {
									member.sendPackets(new S_SystemMessage("注意：記録を"+min+"分"+second+"超で更新しました。")); 
								}
							} else if (clan.getRankTime() < clantime) {
								for (L1PcInstance member : clan.getOnlineClanMember()) {
									member.sendPackets(new S_SystemMessage("通知：既存の記録を更新するのに失敗しました。")); 	
								}
							}
							End();
						}
					}
				}
			}
		}
	}
	
	private void End() {
		try {
			for (L1Object obj : L1World.getInstance().getVisibleObjects(_map).values()) {
				if (obj instanceof L1PcInstance) {
					L1PcInstance pc = (L1PcInstance) obj;					
					pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "30秒後、地下通路が閉じられます。"));					
				}
			}
			Thread.sleep(30000);
			for (L1Object obj : L1World.getInstance().getVisibleObjects(_map).values()) {
				if (obj instanceof L1PcInstance) {
					L1PcInstance pc = (L1PcInstance) obj;				
					new L1Teleport().teleport(pc, 33426, 32811, (short) 4, 5, true);
				}
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
		End = true;
	}

	private final ArrayList<L1NpcInstance> _list0 = new ArrayList<L1NpcInstance>();
	private final ArrayList<L1NpcInstance> _list1 = new ArrayList<L1NpcInstance>();
	private final ArrayList<L1NpcInstance> _list2 = new ArrayList<L1NpcInstance>();
	private final ArrayList<L1NpcInstance> _list3 = new ArrayList<L1NpcInstance>();	

	public void add(L1NpcInstance npc, int type) {
		switch (type) {
		case 0:
			if (npc == null || _list0.contains(npc)) {
				return;
			}
			_list0.add(npc);
			break;
		case 1:
			if (npc == null || _list1.contains(npc)) {
				return;
			}
			_list1.add(npc);
			break;
		case 2:
			if (npc == null || _list2.contains(npc)) {
				return;
			}
			_list2.add(npc);
			break;
		case 3:
			if (npc == null || _list3.contains(npc)) {
				return;
			}
			_list3.add(npc);
			break;	
		}
	}

	private void remove(L1NpcInstance npc, int type) {
		switch (type) {
		case 0:
			if (npc == null || !_list0.contains(npc)) {
				return;
			}
			_list0.remove(npc);
			break;
		case 1:
			if (npc == null || !_list1.contains(npc)) {
				return;
			}
			_list1.remove(npc);
			break;
		case 2:
			if (npc == null || !_list2.contains(npc)) {
				return;
			}
			_list2.remove(npc);
			break;
		case 3:
			if (npc == null || !_list3.contains(npc)) {
				return;
			}
			_list3.remove(npc);
			break;	
		}
	}

	private void ListClear(int type) {
		switch (type) {
		case 0:
			_list0.clear();
			break;
		case 1:
			_list1.clear();
			break;
		case 2:
			_list2.clear();
			break;
		case 3:
			_list3.clear();
			break;	
		}
	}

	private void SpawnMonster() {	
		// 扉出現
		spawn(32869, 32936, (short) _map, 0, 14911, 1, 0); //1チュンムン
		spawn(32809, 33066, (short) _map, 0, 14912, 1, 0); //2チュンムン	

		//地下1階
		spawn(32933, 32874, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32933, 32871, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32934, 32879, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32928, 32881, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32925, 32887, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32917, 32885, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32914, 32888, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32909, 32883, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32901, 32884, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32891, 32881, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32891, 32873, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32893, 32863, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32888, 32860, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32917, 32885, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32879, 32863, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32869, 32877, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32872, 32885, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32867, 32890, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32873, 32893, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32873, 32900, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32881, 32899, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32917, 32885, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32891, 32902, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32885, 32910, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32893, 32907, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32917, 32885, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32898, 32911, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32900, 32915, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32906, 32910, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32917, 32885, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32915, 32908, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32917, 32885, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32923, 32909, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32926, 32915, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32926, 32922, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32915, 32928, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32903, 32925, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32899, 32933, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32889, 32930, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32881, 32930, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32877, 32925, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32917, 32885, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32875, 32933, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32873, 32923, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32861, 32931, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32911, 32887, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32897, 32878, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32889, 32858, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32870, 32870, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		spawn(32866, 32921, (short) _map, 0, _MonsterList1[_random.nextInt(100) % _MonsterList1.length], 0, 1);
		
		spawn(32868, 32925, (short) _map, 6, 14905, 0, 1); //貪欲のアヴィシー
		
		//地下2階
		spawn(32866, 32982, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32863, 32985, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32872, 32985, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32868, 32984, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32872, 32996, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32868, 32990, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32862, 33005, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32870, 32998, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32844, 33006, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32865, 32997, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32863, 33003, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32831, 33001, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32858, 33002, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32819, 32990, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32843, 33005, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32840, 33000, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32814, 33007, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32831, 32999, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32812, 33016, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32834, 32993, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32831, 32989, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32824, 32996, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32811, 33027, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32819, 32996, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32810, 32994, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32805, 32999, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32828, 33032, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32806, 33006, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32804, 33016, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32813, 33019, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32815, 33027, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32825, 33027, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32827, 33030, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32836, 33030, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32842, 33033, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32855, 33028, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32847, 33033, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32853, 33041, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32859, 33032, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32862, 33049, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32866, 33032, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32855, 33053, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32857, 33041, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32839, 33055, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32862, 33045, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32851, 33054, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32840, 33052, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32834, 33058, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32827, 33056, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32821, 33053, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32813, 33060, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32805, 33059, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32811, 33049, (short) _map, 0, _MonsterList2[_random.nextInt(100) % _MonsterList2.length], 0, 2);
		spawn(32812, 33048, (short) _map, 5, 14903, 0, 2); //ドクコト
		spawn(32808, 33049, (short) _map, 5, 14903, 0, 2); //ドクコト
		spawn(32805, 33051, (short) _map, 5, 14903, 0, 2); //ドクコト
		spawn(32804, 33055, (short) _map, 5, 14903, 0, 2); //ドクコト
		spawn(32804, 33055, (short) _map, 5, 14903, 0, 2); //ドクコト
		spawn(32805, 33060, (short) _map, 5, 14903, 0, 2); //ドクコト
		spawn(32812, 33054, (short) _map, 5, 14906, 0, 2); // 貪欲の視バー
		
		// 地下3階
		spawn(32795, 33113, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32798, 33108, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32798, 33112, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32800, 33116, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32803, 33115, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32803, 32120, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32808, 33116, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32808, 33120, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32805, 33129, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32803, 33127, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32802, 33136, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32811, 33133, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32805, 33139, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32798, 33147, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32796, 33142, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32812, 33145, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32808, 33147, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32802, 33156, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32812, 33152, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32809, 33160, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32809, 33168, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32820, 33157, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32828, 33160, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32819, 33161, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32831, 33159, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32812, 33177, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32806, 33180, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32817, 33182, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32827, 33173, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32838, 33169, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32848, 33158, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32844, 33181, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32837, 33179, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32835, 33170, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32847, 33163, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32861, 33157, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32863, 33165, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32853, 33183, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32854, 33183, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32862, 33180, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32858, 33172, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32859, 33161, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32854, 33148, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32862, 33131, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32858, 33121, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32867, 33114, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32855, 33114, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32854, 33108, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32845, 33117, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32840, 33114, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32864, 33109, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32835, 33108, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32852, 33110, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32857, 33125, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32865, 33129, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32862, 33135, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32852, 33118, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32827, 33112, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32847, 33106, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32836, 33109, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32824, 33104, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);
		spawn(32817, 33101, (short) _map, 0, _MonsterList3[_random.nextInt(100) % _MonsterList3.length], 0, 3);		
		spawn(32866, 33173, (short) _map, 5, 14907, 0, 3); //貪欲のアズモダン
	}

	public static L1NpcInstance spawn(int x, int y, short MapId, int Heading, int npcId, int randomRange, int roomnumber) {
		L1NpcInstance npc = null;
		try { 
			npc = NpcTable.getInstance().newNpcInstance(npcId);
			npc.setId(IdFactory.getInstance().nextId());
			npc.setMap(MapId);
			if (randomRange == 0) {
				npc.getLocation().set(x, y, MapId);
				npc.getLocation().forward(Heading);
			} else {
				int tryCount = 0;
				do {
					tryCount++;
					npc.setX(x + (int) (Math.random() * randomRange) - (int) (Math.random() * randomRange));
					npc.setY(y + (int) (Math.random() * randomRange) - (int) (Math.random() * randomRange));
					if (npc.getMap().isInMap(npc.getLocation()) && npc.getMap().isPassable(npc.getLocation())) {
						break;
					}
					Thread.sleep(1);
				} while (tryCount < 50);
				if (tryCount >= 50) {
					npc.getLocation().forward(Heading);
				}
			}
			if (npc instanceof L1DoorInstance) {		
				if (npc.getNpcId() == 14911) {
					((L1DoorInstance) npc).setLeftEdgeLocation(32868);
					((L1DoorInstance) npc).setRightEdgeLocation(32870);
					((L1DoorInstance) npc).setDirection(0);
				} else if (npc.getNpcId() == 14912) {
					((L1DoorInstance) npc).setLeftEdgeLocation(32808);
					((L1DoorInstance) npc).setRightEdgeLocation(32810);
					((L1DoorInstance) npc).setDirection(0);			
				}
			}
			npc.setHomeX(npc.getX());
			npc.setHomeY(npc.getY());
			npc.getMoveState().setHeading(Heading);

			L1World.getInstance().storeObject(npc);
			L1World.getInstance().addVisibleObject(npc);

			Azmodan azmodan = AzmodanSystem.getInstance().getAzmodan(MapId);
			if (roomnumber == 0) {
				azmodan.add(npc, 0);
			} else if (roomnumber == 1) {
				azmodan.add(npc, 1);
			} else if (roomnumber == 2) {
				azmodan.add(npc, 2);
			} else if (roomnumber == 3) {
				azmodan.add(npc, 3);		
			}
			npc.getLight().turnOnOffLight();
			npc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE); 

		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
		return npc;
	}
}
