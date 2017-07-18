package l1j.server.IndunSystem.DragonRaid.Fafu;

import java.util.ArrayList;
import java.util.Random;

import l1j.server.IndunSystem.DragonRaid.Fafu.FafurionRaidSystem.FafurionMsgTimer;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1FieldObjectInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ChatPacket;

public class FafurionRaid {

	private final ArrayList<L1PcInstance> _fafulist = new ArrayList<L1PcInstance>();

	private int _id;
	private boolean _isFafurion = false;
	public FafurionRaid(int id){ _id = id; }

	public void timeOverRun(int type, int stage) {
		switch(type){
		case 1: // パプリオン1次出現
			FafurionMsgTimer Fafu1 = new FafurionMsgTimer(_id, type, stage);
			GeneralThreadPool.getInstance().execute(Fafu1);
			_isFafurion = true; 
			break;
		case 2: // パプリオン1次死んだ後、[メッセージのみ]
			FafurionMsgTimer Fafu1Die = new FafurionMsgTimer(_id, type, stage);
			GeneralThreadPool.getInstance().execute(Fafu1Die);
			break;
		case 3: // パプリオン2次死んだ後、[メッセージのみ]
			FafurionMsgTimer Fafu2Die = new FafurionMsgTimer(_id, type, stage);
			GeneralThreadPool.getInstance().execute(Fafu2Die);
			break;
		case 4: //パプリオン3次死んだ後、[メッセージのみ]
			FafurionMsgTimer Fafu3Die = new FafurionMsgTimer(_id, type, stage);
			GeneralThreadPool.getInstance().execute(Fafu3Die);
			break;
		case 5: // ポータルの作成後2時間後
			EndRaid(_id);
			break;
		}
	}

 	/** レイド終了 **/
	private void EndRaid(int map) {
		FafurionRaid ar = FafurionRaidSystem.getInstance().getAR(map);
		for(L1PcInstance pc : L1World.getInstance().getAllPlayers()){ 
			if(pc.getMapId() == map){ 
				Random random = new Random();
				int Dragontel = random.nextInt(3)+ 1;
				pc.sendPackets(new S_ChatPacket(pc,"システムメッセージ：レイドに失敗しました。"));
//				System.out.println("■■■■■■■■■■ パプリオンレイド失敗 ■■■■■■■■■■ MAP - " + map);
				if(Dragontel == 1) new L1Teleport().teleport(pc, 33705, 32504, (short)4, 5, true); // ウェルダン 
				else if(Dragontel == 2) new L1Teleport().teleport(pc, 33744, 32499, (short)4, 5, true); //ウェルダン 
				else new L1Teleport().teleport(pc, 33742, 32483, (short)4, 5, true); // ウェルダン 
			}
			ar.RemoveLairUser(pc); //すべてのリストの初期化
		}
		_isFafurion = false;
		DelMonster(map); // マップ内のすべてのオブジェクトを削除
	}

	/**レイド終了後マップ内のオブジェクトの削除 **/
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
	
	/** このリストを渡してくれる */
	public ArrayList<L1PcInstance> getRoomList(int num){
		switch(num){
		case 5: return _fafulist; default: return null; }
	}
	/** パプリオンレアに進入したユーザーの数を持って来る */
	public int countLairUser() { return _fafulist.size(); }
	/** パプリオンレアに進入するユーザを入れる */
	public void addLairUser(L1PcInstance pc) { if (!_fafulist.contains(pc)) _fafulist.add(pc); }
	/** パプリオンレアに進入していたユーザーかどうかを確認し */
	public boolean isLairUser(L1PcInstance pc) { return _fafulist.contains(pc); } 
	/** パプリオンレアに進入していたユーザーを消す */
	public void RemoveLairUser(L1PcInstance pc) { if (_fafulist.contains(pc)) _fafulist.remove(pc); } 
	/** 波プリ遠賀川浮かび上がったのか教えてくれる */
	public boolean isFafurion() { return _isFafurion; }
	public int getFafuId() { return _id; }
}