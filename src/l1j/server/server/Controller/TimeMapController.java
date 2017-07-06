/**
 * 타이머 관련 맵에 대한 컨트롤러
 * 2008. 12. 04
*/

package l1j.server.server.Controller;

import java.util.ArrayList;
import java.util.logging.Logger;

import l1j.server.server.datatables.DoorSpawnTable;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.templates.L1TimeMap;

public class TimeMapController implements Runnable{
	public static final int SLEEP_TIME = 1000;

	private static Logger _log = Logger.getLogger(TimeMapController.class
			. getName());

	private ArrayList<L1TimeMap> mapList;												// 맵 저장소
	private static TimeMapController instance;										// 단일 싱글톤 객체

	/**
	 * 싱글톤 구현 - 단일 객체 리턴
	 * @return	(TimeMapController)	단일객체
	*/
	public static TimeMapController getInstance(){
		if(instance == null) instance = new TimeMapController();
		return instance;
	}
	/**
	 * 기본생성자(싱글톤 구현으로 private)
	*/
	private TimeMapController(){
		mapList = new ArrayList<L1TimeMap>();
	}
	/**
	 * Thread abstract Method
	*/
	@Override
	public void run(){
		try{
			for(L1TimeMap timeMap : array()){
				if(timeMap.count()){
					for(L1PcInstance pc : L1World.getInstance().getAllPlayers()){
						if( timeMap.getId() != pc.getMapId())
						{
							continue;
						}

						switch(pc.getMapId()){
						case 72:
						case 73:
						case 74:
							new L1Teleport().teleport(pc, 34056, 32279, (short) 4, 5, true);
							break;
						case 460:
						case 461:
						case 462:
						case 463:
						case 464:
						case 465:
						case 466:
							new L1Teleport().teleport(pc, 32664, 32855, (short) 457, 5, true);
							break;
						case 470:
						case 471:
						case 472:
						case 473:
						case 474:
							new L1Teleport().teleport(pc, 32663, 32853, (short) 467, 5, true);
							break;
						case 475:
						case 476:
						case 477:
						case 478:
							new L1Teleport().teleport(pc, 32660, 32876, (short) 468, 5, true);
							break;
						default:
							break;
						}
					}
					DoorSpawnTable.getInstance().getDoor(timeMap.getDoor()).close();
					remove(timeMap);
				}
			}
		}catch(Exception e){
			_log.warning(e.getMessage());
		}
	}
	/**
	 * 타임 이벤트가 있는 맵 등록
	 * 중복 등록이 되지 않도록 이미 등록된 맵 아이디와 비교 없다면 등록
	 * 사이즈가 0 이라면 즉 초기라면 비교대상이 없기때문에 무조건 등록
	 * @param	(TimeMap)	등록할 맵 객체
	*/
	public void add(L1TimeMap map){
		if(mapList.size() > 0){
			boolean found = false;
			for(L1TimeMap m : array()){
				if(m.getId() == map.getId()){
					found = true;
					break;
				}
			}
			if(!found)
			{
				mapList.add(map);
			}
		}else mapList.add(map);
	}
	/**
	 * 타임 이벤트가 있는 맵 삭제
	 * 중복 삭제 또는 IndexOutOfBoundsException이 되지 않도록 이미 등록된 맵 아이디와 비교 있다면 삭제
	 * @param	(TimeMap)	삭제할 맵 객체
	*/
	private void remove(L1TimeMap map){
		for(L1TimeMap m : array()){
			if(m.getId() == map.getId()){
				mapList.remove(map);
				break;
			}
		}
		map = null;
	}
	/**
	 * 등록된 이벤트 맵 배열 리턴
	 * @return	(TimeMap[])	맵 객체 배열
	*/
	private L1TimeMap[] array(){
		return mapList.toArray(new L1TimeMap[mapList.size()]);
	}
}