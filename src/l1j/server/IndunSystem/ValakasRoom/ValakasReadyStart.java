package l1j.server.IndunSystem.ValakasRoom;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.serverpackets.S_SystemMessage;

public class ValakasReadyStart {

	private static ValakasReadyStart _instance;

	private final Map<Integer, ValakasReady> _readylist = new ConcurrentHashMap<Integer, ValakasReady>();

	public static ValakasReadyStart getInstance() {
		if (_instance == null) {
			_instance = new ValakasReadyStart();
		}
		return _instance;
	}

	public void startReady(L1PcInstance pc) {
		if (countReadyRaid() >= 49) {
			pc.sendPackets(new S_SystemMessage("인스턴스 던전을 더 이상 생성할수 없습니다."));
			return;
		}
		int id = blankMapId();
		if (id != 2699)
			L1WorldMap.getInstance().cloneMap(2699, id);
		ValakasReady ar = new ValakasReady(id, pc);
		pc.isInValakas = true;
		new L1Teleport().teleport(pc, 32624, 33059, (short) id, 5, false);
		ar.BasicNpcList = ValakasRoomSpawn.getInstance().fillSpawnTable(id, 0, true);
		_readylist.put(id, ar);
		ar.Start();
	}

	/**
	 * 빈 맵 아이디를 가져온다
	 * 
	 * @return
	 */
	public int blankMapId() {
		if (_readylist.size() == 0)
			return 2699;
		for (int i = 2699; i <= 2798; i++) {
			ValakasReady h = _readylist.get(i);
			if (h == null)
				return i;
		}
		return 2798;
	}

	public void removeReady(int id) {
		_readylist.remove(id);
	}

	public int countReadyRaid() {
		return _readylist.size();
	}

}
