package l1j.server.IndunSystem.ClanDungeon;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.serverpackets.S_SystemMessage;

public class AzmodanSystem {
	
	private static AzmodanSystem _instance;
	private final ArrayList<Integer> _map = new ArrayList<Integer>();
	private final Map<Integer, Azmodan> _list = new ConcurrentHashMap<Integer, Azmodan>();
	private final Map<Integer, Integer> _clanlist = new ConcurrentHashMap<Integer, Integer>();
	private final Map<Integer, Integer> _clanid = new ConcurrentHashMap<Integer, Integer>();
	
	public static AzmodanSystem getInstance() {
		if (_instance == null) {
			_instance = new AzmodanSystem();
		}
		return _instance;
	}

	public AzmodanSystem(){
		_map.add(12014);
	}

	public void startAzmodan(int clanid, L1PcInstance pc){
		if(countAzmodan() >= 100){
			pc.sendPackets(new S_SystemMessage("現在オープン中のダンジョンが多すぎます。しばらくご利用ください"));
			return;
		}
		int id = blankMapId();
		if(id != 12014)
			L1WorldMap.getInstance().cloneMap(12014, id);
		Azmodan clandun = new Azmodan(id);
	
		_list.put(id, clandun);
		_clanlist.put(clanid, id);
		_clanid.put(id, clanid);
		clandun.Start();
	}

	public int blankMapId(){
		if(_list.size() == 0)
			return 12014;
		for(int i = 12014 ; i <= 12114; i++){
			Azmodan h = _list.get(i);
			if(h == null)
				return i;
		}  
		return 12114;
	}

	public Azmodan getAzmodan(int id){
		return _list.get(id);
	}
	
	public int getAzmodanClanid(int id) {
		return _clanlist.get(id);
	}

	public int getClanid(int id) {
		return _clanid.get(id);
	}
	
	public void removeAzmodan(int id){
		_list.remove(id);		
		_clanid.remove(id);
	}

	public void removeClan(int clanid) {
		_clanlist.remove(clanid);
	}
	
	public int countAzmodan(){
		return _list.size();
	}
}
