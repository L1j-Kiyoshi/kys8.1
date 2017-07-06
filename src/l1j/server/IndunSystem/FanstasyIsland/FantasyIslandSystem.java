package l1j.server.IndunSystem.FanstasyIsland;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.serverpackets.S_SystemMessage;

public class FantasyIslandSystem {
	//private static Logger _log = Logger.getLogger(FantasyIslandSystem.class.getName());

	private static FantasyIslandSystem _instance;
	//private final ArrayList<Integer> _map = new ArrayList<Integer>();
	private final Map<Integer, FantasyIsland> _list = new ConcurrentHashMap<Integer, FantasyIsland>();

	public static FantasyIslandSystem getInstance() {
		if (_instance == null) {
			_instance = new FantasyIslandSystem();
		}
		return _instance;
	}

	public void startRaid(L1PcInstance pc){
		if(countRaid() >= 49){
			pc.sendPackets(new S_SystemMessage("몽환의 섬 던전에 진입한 인원이 너무 많습니다"));
			return;
		}
		int id = blankMapId();
		if(id != 1936)
			L1WorldMap.getInstance().cloneMap(1936, id);
		FantasyIsland ar = new FantasyIsland(id, pc);
		new L1Teleport().teleport(pc, 32798, 32865, (short) id, pc.getHeading(), true);
		ar.BasicNpcList = FantasyIslandSpawn.getInstance().fillSpawnTable(id, 0, true);
		pc.isInFantasy = true;
		_list.put(id, ar);
		ar.Start();
	}

	/**
	 * 빈 맵 아이디를 가져온다
	 * @return
	 */
	public int blankMapId(){
		if(_list.size() == 0)
			return 1936;
		for(int i = 1936 ; i <= 2035; i++){
			FantasyIsland h = _list.get(i);
			if(h == null)
				return i;
		}  
		return 2035;
	}

	public void remove(int id){
		_list.remove(id);
	}

	public int countRaid(){
		return _list.size();
	}

}
