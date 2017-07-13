package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;

public class MonsterBookTable {
	
	private class Mbl {
		private HashMap<Integer, Integer> _monlist = new HashMap<Integer, Integer>();
		private HashMap<Integer, Integer> _monquest = new HashMap<Integer, Integer>();	
	}
	
	public HashMap<Integer, Integer> getMonBookList(int id) {
		Mbl mbl = _monsterBookLists.get(id);
		if (mbl == null) return null;
		return mbl._monlist;
	}
	
	public HashMap<Integer, Integer> getMonQuest(int id) {
		Mbl mbl = _monsterBookLists.get(id);
		if (mbl == null) return null;
		return mbl._monquest;
	}
	
	public static void reload() {
		MonsterBookTable oldInstance = _instance;
		_instance = new MonsterBookTable();
		oldInstance._monsterBookLists.clear();
	}
	
	private class Mblt {
		private int _monNum = 0;
		private int _monNpcid= 0;
		private int _locX= 0;
		private int _locY= 0;
		private int _mapId= 0;
		private int _typE= 0;
		private int _marteriaL= 0;
	}

	public int getMonNum(int num) {
		Mblt mblt = _monBookTellList.get(num);
		if (mblt == null) return 0;
		return mblt._monNum;
	}
	public int getMonsterId(int num) {
		Mblt mblt = _monBookTellList.get(num);
		if (mblt == null) return 0;
		return mblt._monNpcid;
	}
	public int getLocX(int num) {
		Mblt mblt = _monBookTellList.get(num);
		if (mblt == null) return 0;
		return mblt._locX;
	}
	public int getLocY(int num) {
		Mblt mblt = _monBookTellList.get(num);
		if (mblt == null) return 0;
		return mblt._locY;
	}
	public int getMapId(int num) {
		Mblt mblt = _monBookTellList.get(num);
		if (mblt == null) return 0;
		return mblt._mapId;
	}
	public int getType(int num) {
		Mblt mblt = _monBookTellList.get(num);
		if (mblt == null) return 0;
		return mblt._typE;
	}
	public int getMarterial(int num) {
		Mblt mblt = _monBookTellList.get(num);
		if (mblt == null) return 0;
		return mblt._marteriaL;
	}
	
	public int getQuest1(int num) {
		Mblt mblt = _monBookTellList.get(num);
		if (mblt == null) return 0;
		switch (num) {
		case 1: case 2: case 3: case 4: case 14: case 15: case 18: case 19: case 20: case 25: case 30: case 36: case 53: case 54: case 55:
		case 56: case 57: case 58: case 59: case 60: case 61: case 62: case 63: case 64: case 99: case 100: case 106: case 107: case 108: 
		case 126: case 133: case 134: case 135: case 136: case 137: case 138: case 139: case 149: case 151: case 158: case 159: case 160:
		case 162: case 163: case 176: case 177: case 178: case 189: case 209: case 210: case 222: case 223: case 224: case 225: case 235:
		case 236: case 237: case 240: case 254: case 255: case 263: case 267: case 271: case 280: case 281: case 284: case 291: case 294:
		case 295: case 296: case 297: case 304: case 309: case 322: case 328: case 331: case 348: case 355: case 360: case 368: case 371:
		case 390: case 393: case 398: case 408: case 411: case 420: case 425: case 431: case 432: case 443: case 449: case 458: case 464:
		case 465: case 471: case 478: case 484: case 491: case 496: case 501: case 506: case 511: case 516: case 525: case 526: case 527:
		case 533: case 549: case 552: 			
			return 10;
		default:
			return 500;
		}		
	}
	
	public int getQuest2(int num) {
		Mblt mblt = _monBookTellList.get(num);
		if (mblt == null) return 0;
		switch (num) {
		case 1: case 2: case 3: case 4: case 14: case 15: case 18: case 19: case 20: case 25: case 30: case 36: case 53: case 54: case 55:
		case 56: case 57: case 58: case 59: case 60: case 61: case 62: case 63: case 64: case 99: case 100: case 106: case 107: case 108: 
		case 126: case 133: case 134: case 135: case 136: case 137: case 138: case 139: case 149: case 151: case 158: case 159: case 160:
		case 162: case 163: case 176: case 177: case 178: case 189: case 209: case 210: case 222: case 223: case 224: case 225: case 235:
		case 236: case 237: case 240: case 254: case 255: case 263: case 267: case 271: case 280: case 281: case 284: case 291: case 294:
		case 295: case 296: case 297: case 304: case 309: case 322: case 328: case 331: case 348: case 355: case 360: case 368: case 371:
		case 390: case 393: case 398: case 408: case 411: case 420: case 425: case 431: case 432: case 443: case 449: case 458: case 464:
		case 465: case 471: case 478: case 484: case 491: case 496: case 501: case 506: case 511: case 516: case 525: case 526: case 527:
		case 533: case 549: case 552: 			
			return 100;
		default:
			return 10000;
		}		
	}
	
	public int getQuest3(int num) {
		Mblt mblt = _monBookTellList.get(num);
		if (mblt == null) return 0;
		switch (num) {
		case 1: case 2: case 3: case 4: case 14: case 15: case 18: case 19: case 20: case 25: case 30: case 36: case 53: case 54: case 55:
		case 56: case 57: case 58: case 59: case 60: case 61: case 62: case 63: case 64: case 99: case 100: case 106: case 107: case 108: 
		case 126: case 133: case 134: case 135: case 136: case 137: case 138: case 139: case 149: case 151: case 158: case 159: case 160:
		case 162: case 163: case 176: case 177: case 178: case 189: case 209: case 210: case 222: case 223: case 224: case 225: case 235:
		case 236: case 237: case 240: case 254: case 255: case 263: case 267: case 271: case 280: case 281: case 284: case 291: case 294:
		case 295: case 296: case 297: case 304: case 309: case 322: case 328: case 331: case 348: case 355: case 360: case 368: case 371:
		case 390: case 393: case 398: case 408: case 411: case 420: case 425: case 431: case 432: case 443: case 449: case 458: case 464:
		case 465: case 471: case 478: case 484: case 491: case 496: case 501: case 506: case 511: case 516: case 525: case 526: case 527:
		case 533: case 549: case 552: 			
			return 1000;
		default:
			return 100000;
		}		
	}
	
	public void addMon_Counter(int id, int num) {
		Mbl mbl = _monsterBookLists.get(id);
		if (mbl._monlist.get(num) != null) {
			int mon = mbl._monlist.get(num);
			if(mon > 100000) mon = 100000;
			mbl._monlist.put(num, mon += 1);
		} else { //最初の登録。
			mbl._monlist.put(num, 1); 			
		}
	}
	
	public int getMon_Conter(int id, int num) {
		Mbl mbl = _monsterBookLists.get(id);		
		return mbl._monlist.get(num);
	}
	
	public void setMon_Quest(int id, int quest, int value) {
		Mbl mbl = _monsterBookLists.get(id);
		mbl._monquest.put(quest, value);
	}
	
	public int getMon_Quest(int id, int quest) {
		Mbl mbl = _monsterBookLists.get(id);
		if (mbl._monquest.get(id) == null) 
			return 0;
		return mbl._monquest.get(quest);
	}
	
	private static Logger _log = Logger.getLogger(MonsterBookTable.class.getName());
	private static MonsterBookTable _instance;
	
	private ConcurrentHashMap<Integer, Mbl> _monsterBookLists = new ConcurrentHashMap<Integer, Mbl>();
	private ConcurrentHashMap<Integer, Mblt> _monBookTellList = new ConcurrentHashMap<Integer, Mblt>();
	private ConcurrentHashMap<Integer, Integer> _monsterList = new ConcurrentHashMap<Integer, Integer>();
	
	public int getMonsterList(int npcid) {
		Integer result = _monsterList.get(npcid);
		if (result == null) return 0;
		return result;
	}
	
	
	public static MonsterBookTable getInstace() {
		if (_instance == null) {
			_instance = new MonsterBookTable();
		}
		return _instance;
	}
	
	private MonsterBookTable() {
		loadMonsterBookList();
		loadMonsterBookTelList();
	}
	
	public void loadMonsterBookList() {
		try (Connection con = L1DatabaseFactory.getInstance().getConnection();
				PreparedStatement pstm = con.prepareStatement("SELECT * FROM character_monsterbooklist");
				ResultSet rs = pstm.executeQuery()) {
			Mbl mbl = null;
			while (rs.next()) {
				mbl = new Mbl();
				int id = rs.getInt("id");			
				StringTokenizer ml = new StringTokenizer(rs.getString("monsterlist"), "|");
				while (ml.hasMoreTokens()) {
					String monster = ml.nextToken();
					StringTokenizer ml1 = new StringTokenizer(monster, ",");
					while (ml1.hasMoreTokens()) {
						int monsterNumber = Integer.parseInt(ml1.nextToken(), 10);
						int monsterKillCount = Integer.parseInt(ml1.nextToken(), 10);
						mbl._monlist.put(monsterNumber, monsterKillCount);
					}
				}
				StringTokenizer mlq = new StringTokenizer(rs.getString("monquest"), "|");
				while (mlq.hasMoreTokens()) {
					String monquest = mlq.nextToken();
					StringTokenizer ml2 = new StringTokenizer(monquest, ",");
					while (ml2.hasMoreTokens()) {
						int questNum = Integer.parseInt(ml2.nextToken(), 10);
						int value = Integer.parseInt(ml2.nextToken(), 10);
						mbl._monquest.put(questNum, value);
					}
				}
				_monsterBookLists.put(id, mbl);			
			}
//			System.out.println("【MonsterBook List Data】  " + _monsterBookLists.size() + " 本loanding ... OK！ "）;
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}

	
	private void loadMonsterBookTelList() {
		try (Connection con = L1DatabaseFactory.getInstance().getConnection();
				PreparedStatement pstm = con.prepareStatement("SELECT * FROM monster_book");
				ResultSet rs = pstm.executeQuery()) {
			Mblt mblt = null;
			while (rs.next()) {
				mblt = new Mblt();
				int monsternumber = rs.getInt("monsternumber");
				mblt._monNum = monsternumber;
				int monsterid = rs.getInt("monster_id");
				mblt._monNpcid = monsterid;
				mblt._locX = rs.getInt("locx");
				mblt._locY = rs.getInt("locy");
				mblt._mapId = rs.getInt("mapid");
				mblt._typE = rs.getInt("type");
				mblt._marteriaL = rs.getInt("marterial");				
				_monBookTellList.put(monsternumber, mblt);
				_monsterList.put(monsterid, monsternumber);
				
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}
	
	public void createMonsterBookList(int id) {
		try (Connection con = L1DatabaseFactory.getInstance().getConnection();
				PreparedStatement pstm = con.prepareStatement("INSERT INTO character_monsterbooklist SET id=?, monsterList=?, monquest=?")) {
			Mbl mbl = new Mbl();
			pstm.setInt(1, id);
			pstm.setString(2, "");
			pstm.setString(3, "");
			pstm.execute();
			_monsterBookLists.put(id, mbl);			
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}
	
	public void deleteMonsterBookList(int id) {		
		try (Connection con = L1DatabaseFactory.getInstance().getConnection();
				PreparedStatement pstm = con.prepareStatement("DELETE FROM character_monsterbooklist WHERE id=?")) {			
			pstm.setInt(1, id);		
			pstm.execute();
			_monsterBookLists.remove(id);			
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}
	
	public void saveMonsterBookList(int id) {
		StringBuffer monsterlist = new StringBuffer();
		try (Connection con = L1DatabaseFactory.getInstance().getConnection();
				PreparedStatement pstm = con.prepareStatement("UPDATE character_monsterbooklist SET monsterList=? WHERE id=?")) {
			HashMap<Integer, Integer> mbl = getMonBookList(id);
			if (mbl != null) {
				TreeMap<Integer, Integer> tree = new TreeMap<Integer, Integer>(mbl);
				Iterator<Integer> iter = tree.keySet().iterator();
				while(iter.hasNext()){
					int monstertnumber = iter.next();
					int monsterkillcount = mbl.get(monstertnumber);
					monsterlist.append(monstertnumber + "," + monsterkillcount + "|");
				}
			}
			//System.out.println("savemonsterbooklist : " + id + " : " + monsterlist);
			pstm.setString(1, monsterlist.toString());
			pstm.setInt(2, id);
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}
	public void saveMonsterQuest(int id) {
		StringBuffer monsterlist = new StringBuffer();
		try (Connection con = L1DatabaseFactory.getInstance().getConnection();
				PreparedStatement pstm = con.prepareStatement("UPDATE character_monsterbooklist SET monquest=? WHERE id=?")) {
			HashMap<Integer, Integer> mbl = getMonQuest(id);
			if (mbl != null) {
				TreeMap<Integer, Integer> tree = new TreeMap<Integer, Integer>(mbl);
				Iterator<Integer> iter = tree.keySet().iterator();
				while(iter.hasNext()){
					int questNum = iter.next();
					int value = mbl.get(questNum);
					monsterlist.append(questNum + "," + value + "|");
				}
			}
			//System.out.println("savemonsterbooklist : " + id + " : " + monsterlist);
			pstm.setString(1, monsterlist.toString());
			pstm.setInt(2, id);
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}
}


