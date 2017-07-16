package l1j.server.IndunSystem.ValakasRoom;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.serverpackets.S_SystemMessage;

public class ValakasRoomSystem {

	private static ValakasRoomSystem _instance;

	private final Map<Integer, ValakasStart> _startlist = new ConcurrentHashMap<Integer, ValakasStart>();

	public static ValakasRoomSystem getInstance() {
		if (_instance == null) {
			_instance = new ValakasRoomSystem();
		}
		return _instance;
	}
		
	public void startRaid(L1PcInstance pc){
		if(countStartRaid() >= 49){
			pc.sendPackets(new S_SystemMessage("インスタンスダンジョンをこれ以上作成できません。"));
			return;
		}
		int id = blankMapId();
		if(id != 2600)
			L1WorldMap.getInstance().cloneMap(2600, id);
		
		ValakasStart ar = new ValakasStart(id, pc);
		pc.isInValakas = true;
		new L1Teleport().teleport(pc, 32624, 33059 , (short) id, 5, false);
	
		int rnd = new Random().nextInt(3);
		if (rnd == 0) {
			ar.BasicNpcList = ValakasRoomSpawn.getInstance().fillSpawnTable(id, 2, true);
			ar.BossList = ValakasRoomSpawn.getInstance().fillSpawnTable(id, 1000, true);
		} else if (rnd == 1) {
			ar.BasicNpcList = ValakasRoomSpawn.getInstance().fillSpawnTable(id, 2, true);
			ar.BossList = ValakasRoomSpawn.getInstance().fillSpawnTable(id, 1001, true);
		} else {   
			ar.BasicNpcList = ValakasRoomSpawn.getInstance().fillSpawnTable(id, 2, true);
			ar.BossList = ValakasRoomSpawn.getInstance().fillSpawnTable(id, 1002, true);
		}
		
		_startlist.put(id, ar);
		ar.Start();
	}
		
	public int blankMapId(){
		if(_startlist.size() == 0)
			return 2600;
		for(int i = 2600 ; i <= 2698; i++){
			ValakasStart h = _startlist.get(i);
			if(h == null)
				return i;
		}  
		return 2698;
	}

	public void removeStart(int id){
		_startlist.remove(id);
	}

	public int countStartRaid(){
		return _startlist.size();
	}

}
