package l1j.server.IndunSystem.DragonRaid.Anta;

import java.util.ArrayList;
import java.util.Random;

import l1j.server.IndunSystem.DragonRaid.Anta.AntarasRaidSystem.AntarasMsgTimer;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1FieldObjectInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ChatPacket;

public class AntarasRaid {

	private final ArrayList<L1PcInstance> _antalist = new ArrayList<L1PcInstance>();

	private int _id;
	private boolean _isAntaras = false;

	public AntarasRaid(int id){ 
		_id = id;
	}

	public void timeOverRun(int type, int stage) {
		switch(type){
		case 1: // 안타라스 1차 스폰
			AntarasMsgTimer Anta1 = new AntarasMsgTimer(_id, type, stage);
			GeneralThreadPool.getInstance().execute(Anta1);
			_isAntaras = true; 
			break;
		case 2: // 안타라스 1차 죽은후 [ 메세지만]
			AntarasMsgTimer Anta1Die = new AntarasMsgTimer(_id, type, stage);
			GeneralThreadPool.getInstance().execute(Anta1Die);
			break;
		case 3: // 안타라스 2차 죽은후 [ 메세지만]
			AntarasMsgTimer Anta2Die = new AntarasMsgTimer(_id, type, stage);
			GeneralThreadPool.getInstance().execute(Anta2Die);
			break;
		case 4: // 안타라스 3차 죽은후 [ 메세지만]
			AntarasMsgTimer Anta3Die = new AntarasMsgTimer(_id, type, stage);
			GeneralThreadPool.getInstance().execute(Anta3Die);
			break;
		case 5: // 포탈 생성후 2시간후
			EndRaid(_id);
			break;
		default:
			break;
		}
	}
	/** 레이드 종료 **/
	private void EndRaid(int map) {
		AntarasRaid ar = AntarasRaidSystem.getInstance().getAR(map);
		for(L1PcInstance pc : L1World.getInstance().getAllPlayers()){ 
			if(pc.getMapId() == map){ 
				Random random = new Random();
				int Dragontel = random.nextInt(3)+ 1;
				pc.sendPackets(new S_ChatPacket(pc,"시스템 메시지: 레이드가 종료 되었습니다."));
//				System.out.println("■■■■■■■■■■ 안타라스 레이드 실패 ■■■■■■■■■■ MAP - " + map);
				if(Dragontel == 1) new L1Teleport().teleport(pc, 33705, 32504, (short)4, 5, true); // 웰던 
				else if(Dragontel == 2) new L1Teleport().teleport(pc, 33744, 32499, (short)4, 5, true); // 웰던 
				else new L1Teleport().teleport(pc, 33742, 32483, (short)4, 5, true); // 웰던 
			}
			ar.RemoveLairUser(pc); // 모든 리스트 초기화
		}
		_isAntaras = false;
		DelMonster(map); // 맵내 모든 오브젝트 삭제
	}

	/** 레이드 종료후 맵내 오브젝트들 삭제 **/
	private void DelMonster(int map) {
		L1MonsterInstance mon = null;
		L1FieldObjectInstance tel = null;
		L1NpcInstance Npc = null;
		for(L1Object object : L1World.getInstance().getVisibleObjects(map).values()){
			if(object instanceof L1MonsterInstance){  
				mon = (L1MonsterInstance) object; 
				mon.deleteMe();
				mon = null;
			}
			if(object instanceof L1FieldObjectInstance){  
				tel =(L1FieldObjectInstance) object; 
				tel.deleteMe();
				tel = null;
			}
			if(object instanceof L1NpcInstance){
				Npc =(L1NpcInstance) object; 
				Npc.deleteMe();
				Npc = null;
			}
		}
	}

	/** 해당 리스트를 넘겨준다 */
	public ArrayList<L1PcInstance> getRoomList(int num){
		switch(num){
		case 5: return _antalist; default: return null; }
	}
	/** 안타라스레어에 진입한 유저 수를 가져온다 */
	public int countLairUser() { return _antalist.size(); }
	/** 안타라스레어에 진입할 유저를 넣는다 */
	public void addLairUser(L1PcInstance pc) { if (!_antalist.contains(pc)) _antalist.add(pc); }
	/** 안타라스레어에 진입했던 유저인지 아닌지를 확인 */
	public boolean isLairUser(L1PcInstance pc) { return _antalist.contains(pc); }
	/** 안타라스레어에 진입했던 유저를 지운다 */
	public void RemoveLairUser(L1PcInstance pc) { if (_antalist.contains(pc)) _antalist.remove(pc); }
	/** 안타라스가 떴는지 알려준다 */
	public boolean isAntaras() { return _isAntaras; }
	public int getAntaId() { return _id; }
}