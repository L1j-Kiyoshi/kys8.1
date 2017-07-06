package l1j.server.GameSystem.valakas;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javolution.util.FastTable;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.utils.L1SpawnUtil;

public class ValaRaidSystem {
	
	//private static Logger _log = Logger.getLogger(HadinSystem.class.getName());

	private static ValaRaidSystem _instance;
	public static L1NpcInstance portal = null;
	private static FastTable<L1PcInstance> pcList;
	private final ArrayList<Integer> _map = new ArrayList<Integer>();
	private final Map<Integer, ValaRaid> _list = new ConcurrentHashMap<Integer, ValaRaid>();

	private int ValaKasMapID = 1161;
	public static ValaRaidSystem getInstance() {
		if (_instance == null) {
			_instance = new ValaRaidSystem();
		}
		return _instance;
	}

	public ValaRaidSystem(){
		_map.add(ValaKasMapID);
	}
	private static Random _random = new Random(System.nanoTime());

	public void random_spawn(int x, int y, int mapid)
	{
		int randx = _random.nextInt(2)+1;
		int randy = _random.nextInt(2)+1;
		
		for(int i = 0 ; i < 2 ; i ++)
		{
			int rr = _random.nextInt(2);
			if(rr == 1)
				L1SpawnUtil.spawn2(x + randx, y + randy, (short) mapid, 5000100, 0, 7200 * 1000, mapid);
			else
				L1SpawnUtil.spawn2(x - randx, y - randy, (short) mapid, 5000100, 0, 7200 * 1000, mapid);
		}
	}
	public void startValakas(L1PcInstance pc){
		if(countVala() >= 6){
			pc.sendPackets(new S_ChatPacket(pc,"인스턴스 던전을 더 이상 생성할수 없습니다."));
			return;
		}
		int id = blankMapId();
		if(id != ValaKasMapID)
			L1WorldMap.getInstance().cloneMap(ValaKasMapID, id);
		ValaRaid vala = new ValaRaid(id);
		pc.valakasMapId = id;

		int fire_delay = 3200;
		L1SpawnUtil.spawn2(pc.getX(), pc.getY(), pc.getMapId(), 910008, 0, 3600 * 1000, id); // 포탈
		L1SpawnUtil.spawn2(32731, 32922, (short) id, 3310015, 0, fire_delay * 1000, id); // 데스나이트 이펙트
		L1SpawnUtil.spawn2(32732, 32922, (short) id, 3310018, 0, fire_delay * 1000, id); // 데스나이트 버프
		L1SpawnUtil.spawn2(32733, 32937, (short) id, 60032, 0, fire_delay * 1000, id); // 창고지기
		L1SpawnUtil.spawn2(32727, 32937, (short) id, 3310016, 0, fire_delay * 1000, id); // 잡화상인
		L1SpawnUtil.spawn2(32751, 32928, (short) id, 3310017, 0, fire_delay * 1000, id); // 레어 입구
		L1SpawnUtil.spawn2(32738, 32925, (short) id, 170017, 0, fire_delay * 1000, id); // 오림
		spawn_fire(id);
		
		_list.put(id, vala);
	}

	public void spawn_fire(int mapid)
	{
		int spawn_list[][] = {
				{	32763,	32897,	mapid,	5000100	},
				{	32764,	32898,	mapid,	5000100	},
				{	32762,	32899,	mapid,	5000100	},
				{	32762,	32899,	mapid,	5000100	},
				{	32761,	32898,	mapid,	5000100	},
				{	32762,	32901,	mapid,	5000100	},
				{	32763,	32901,	mapid,	5000100	},
				{	32765,	32903,	mapid,	5000100	},
				{	32767,	32902,	mapid,	5000100	},
				{	32766,	32903,	mapid,	5000100	},
				{	32763,	32902,	mapid,	5000100	},
				{	32768,	32903,	mapid,	5000100	},
				{	32770,	32902,	mapid,	5000100	},
				{	32770,	32902,	mapid,	5000100	},
				{	32771,	32903,	mapid,	5000100	},
				{	32772,	32902,	mapid,	5000100	},
				{	32774,	32903,	mapid,	5000100	},
				{	32775,	32903,	mapid,	5000100	},
				{	32776,	32903,	mapid,	5000100	},
				{	32776,	32903,	mapid,	5000100	},
				{	32777,	32901,	mapid,	5000100	},
				{	32779,	32903,	mapid,	5000100	},
				{	32779,	32903,	mapid,	5000100	},
				{	32777,	32899,	mapid,	5000100	},
				{	32779,	32897,	mapid,	5000100	},
				{	32781,	32897,	mapid,	5000100	},
				{	32781,	32897,	mapid,	5000100	},
				{	32781,	32897,	mapid,	5000100	},
				{	32778,	32900,	mapid,	5000100	},
				{	32783,	32899,	mapid,	5000100	},
				{	32784,	32901,	mapid,	5000100	},
				{	32780,	32895,	mapid,	5000100	},
				{	32782,	32892,	mapid,	5000100	},
				{	32780,	32891,	mapid,	5000100	},
				{	32783,	32892,	mapid,	5000100	},
				{	32783,	32892,	mapid,	5000100	},
				{	32781,	32887,	mapid,	5000100	},
				{	32781,	32887,	mapid,	5000100	},
				{	32780,	32886,	mapid,	5000100	},
				{	32781,	32884,	mapid,	5000100	},
				{	32780,	32883,	mapid,	5000100	},
				{	32779,	32882,	mapid,	5000100	},
				{	32778,	32882,	mapid,	5000100	},
				{	32781,	32882,	mapid,	5000100	},
				{	32782,	32882,	mapid,	5000100	},
				{	32778,	32880,	mapid,	5000100	},
				{	32776,	32880,	mapid,	5000100	},
				{	32774,	32880,	mapid,	5000100	},
				{	32774,	32880,	mapid,	5000100	},
				{	32773,	32879,	mapid,	5000100	},
				{	32773,	32879,	mapid,	5000100	},
				{	32773,	32878,	mapid,	5000100	},
				{	32770,	32881,	mapid,	5000100	},
				{	32770,	32881,	mapid,	5000100	},
				{	32770,	32881,	mapid,	5000100	},
				{	32767,	32883,	mapid,	5000100	},
				{	32765,	32884,	mapid,	5000100	},
				{	32763,	32885,	mapid,	5000100	},
				{	32760,	32887,	mapid,	5000100	},
				{	32760,	32888,	mapid,	5000100	},
				{	32760,	32888,	mapid,	5000100	},
				{	32758,	32891,	mapid,	5000100	},
				{	32758,	32891,	mapid,	5000100	},
				{	32760,	32892,	mapid,	5000100	},
				{	32760,	32892,	mapid,	5000100	},
				{	32758,	32894,	mapid,	5000100	},
				{	32759,	32895,	mapid,	5000100	},
				{	32759,	32895,	mapid,	5000100	},
				{	32758,	32899,	mapid,	5000100	},
				{	32757,	32898,	mapid,	5000100	},
				{	32758,	32885,	mapid,	5000100	},
				{	32758,	32884,	mapid,	5000100	},
				{	32758,	32883,	mapid,	5000100	},
				{	32758,	32883,	mapid,	5000100	},
				{	32759,	32882,	mapid,	5000100	},
				{	32762,	32882,	mapid,	5000100	},
				{	32763,	32881,	mapid,	5000100	},
				{	32765,	32880,	mapid,	5000100	},
				{	32774,	32902,	mapid,	5000100	},
				{	32777,	32901,	mapid,	5000100	},
				{	32769,	32876,	mapid,	5000100	},
				{	32776,	32876,	mapid,	5000100	},
				{	32780,	32877,	mapid,	5000100	},
				};
		
		for(int[] spawn_dat : spawn_list)
		{
			L1SpawnUtil.spawn2(spawn_dat[0], spawn_dat[1], (short) spawn_dat[2], spawn_dat[3], 0, 70  * 1000,  (short)spawn_dat[2]);
		}
		
		
	}
	public int blankMapId(){
		if(_list.size() == 0)
			return ValaKasMapID;
		for(int i = ValaKasMapID ; i <= ValaKasMapID+5; i++){
			ValaRaid h = _list.get(i);
			if(h == null)
				return i;
		}  
		return ValaKasMapID;
	}

	public ValaRaid getVala(int id){
		
		return _list.get(id);
	}

	public void removeVala(int id){
		_list.remove(id);
	}

	public int countVala(){
		return _list.size();
	}
	public static void clear() {
		if (portal != null)
			portal.deleteMe();
		if (pcList.size() > 0)
			pcList.clear();
	}
}
