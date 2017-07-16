package l1j.server.IndunSystem.Ice.Queen;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.serverpackets.S_ChatPacket;

public class QueenSystem {
	
	private static QueenSystem _instance;
	private final ArrayList<Integer> _map = new ArrayList<Integer>();
	private final Map<Integer, Queen> _list = new ConcurrentHashMap<Integer, Queen>();

	public static QueenSystem getInstance() {
		if (_instance == null) {
			_instance = new QueenSystem();
		}
		return _instance;
	}

	public QueenSystem(){
		_map.add(2101);
	}

	public void startQueen(L1PcInstance pc){
		if(countQueen() >= 49){
			pc.sendPackets(new S_ChatPacket(pc,"インスタンスダンジョンに進入した人員が多すぎます"));
			return;
		}
		int id = blankMapId();
		if(id != 2101)
			L1WorldMap.getInstance().cloneMap(2151, id);
		Queen queen = new Queen(id);
		new L1Teleport().teleport(pc, 32728, 32819, (short) id, 5, true);
		_list.put(id, queen);
		queen.Start();
	}

	public int blankMapId(){
		if(_list.size() == 0)
			return 2101;
		for(int i = 2101 ; i <= 2150; i++){
			Queen h = _list.get(i);
			if(h == null)
				return i;
		}  
		return 2150;
	}

	public Queen getQueen(int id){
		return _list.get(id);
	}

	public void removeQueen(int id){
		_list.remove(id);
	}

	public int countQueen(){
		return _list.size();
	}
}
