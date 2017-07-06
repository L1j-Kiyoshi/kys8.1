package l1j.server.IndunSystem.DragonRaid.Fafu;

import java.util.ArrayList;
import java.util.Random;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1FieldObjectInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.IndunSystem.DragonRaid.Fafu.FafurionRaidSystem.FafurionMsgTimer;

public class FafurionRaid {

	private final ArrayList<L1PcInstance> _fafulist = new ArrayList<L1PcInstance>();

	private int _id;
	private boolean _isFafurion = false;
	public FafurionRaid(int id){ _id = id; }

	public void timeOverRun(int type, int stage) {
		switch(type){
		case 1: // 파푸리온 1차 스폰
			FafurionMsgTimer Fafu1 = new FafurionMsgTimer(_id, type, stage);
			GeneralThreadPool.getInstance().execute(Fafu1);
			_isFafurion = true; 
			break;
		case 2: // 파푸리온 1차 죽은후 [ 메세지만]
			FafurionMsgTimer Fafu1Die = new FafurionMsgTimer(_id, type, stage);
			GeneralThreadPool.getInstance().execute(Fafu1Die);
			break;
		case 3: // 파푸리온 2차 죽은후 [ 메세지만]
			FafurionMsgTimer Fafu2Die = new FafurionMsgTimer(_id, type, stage);
			GeneralThreadPool.getInstance().execute(Fafu2Die);
			break;
		case 4: // 파푸리온 3차 죽은후 [ 메세지만]
			FafurionMsgTimer Fafu3Die = new FafurionMsgTimer(_id, type, stage);
			GeneralThreadPool.getInstance().execute(Fafu3Die);
			break;
		case 5: // 포탈 생성후 2시간후
			EndRaid(_id);
			break;
		}
	}

 	/** 레이드 종료 **/
	private void EndRaid(int map) {
		FafurionRaid ar = FafurionRaidSystem.getInstance().getAR(map);
		for(L1PcInstance pc : L1World.getInstance().getAllPlayers()){ 
			if(pc.getMapId() == map){ 
				Random random = new Random();
				int Dragontel = random.nextInt(3)+ 1;
				pc.sendPackets(new S_ChatPacket(pc,"시스템 메시지: 레이드에 실패 하였습니다."));
//				System.out.println("■■■■■■■■■■ 파푸리온 레이드 실패 ■■■■■■■■■■ MAP - " + map);
				if(Dragontel == 1) new L1Teleport().teleport(pc, 33705, 32504, (short)4, 5, true); // 웰던 
				else if(Dragontel == 2) new L1Teleport().teleport(pc, 33744, 32499, (short)4, 5, true); // 웰던 
				else new L1Teleport().teleport(pc, 33742, 32483, (short)4, 5, true); // 웰던 
			}
			ar.RemoveLairUser(pc); // 모든 리스트 초기화
		}
		_isFafurion = false;
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
		case 5: return _fafulist; default: return null; }
	}
	/** 파푸리온레어에 진입한 유저 수를 가져온다 */
	public int countLairUser() { return _fafulist.size(); }
	/** 파푸리온레어에 진입할 유저를 넣는다 */
	public void addLairUser(L1PcInstance pc) { if (!_fafulist.contains(pc)) _fafulist.add(pc); }
	/** 파푸리온레어에 진입했던 유저인지 아닌지를 확인 */
	public boolean isLairUser(L1PcInstance pc) { return _fafulist.contains(pc); } 
	/** 파푸리온레어에 진입했던 유저를 지운다 */
	public void RemoveLairUser(L1PcInstance pc) { if (_fafulist.contains(pc)) _fafulist.remove(pc); } 
	/** 파푸리온가 떴는지 알려준다 */
	public boolean isFafurion() { return _isFafurion; }
	public int getFafuId() { return _id; }
}