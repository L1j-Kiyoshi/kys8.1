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
		case 1: // アンタラス1次出現
			AntarasMsgTimer Anta1 = new AntarasMsgTimer(_id, type, stage);
			GeneralThreadPool.getInstance().execute(Anta1);
			_isAntaras = true; 
			break;
		case 2: // アンタラス1次死んだ後、[メッセージのみ]
			AntarasMsgTimer Anta1Die = new AntarasMsgTimer(_id, type, stage);
			GeneralThreadPool.getInstance().execute(Anta1Die);
			break;
		case 3: // アンタラス2次死んだ後、[メッセージのみ]
			AntarasMsgTimer Anta2Die = new AntarasMsgTimer(_id, type, stage);
			GeneralThreadPool.getInstance().execute(Anta2Die);
			break;
		case 4: // アンタラス3次死んだ後、[メッセージのみ]
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
	/**レイド終了**/
	private void EndRaid(int map) {
		AntarasRaid ar = AntarasRaidSystem.getInstance().getAR(map);
		for(L1PcInstance pc : L1World.getInstance().getAllPlayers()){ 
			if(pc.getMapId() == map){ 
				Random random = new Random();
				int Dragontel = random.nextInt(3)+ 1;
				pc.sendPackets(new S_ChatPacket(pc,"システムメッセージ：レイドが終了しました。"));
//				System.out.println("■■■■■■■■■■ アンタラスレイド失敗■■■■■■■■■■ MAP - " + map);
				if(Dragontel == 1) new L1Teleport().teleport(pc, 33705, 32504, (short)4, 5, true); // ウェルダン 
				else if(Dragontel == 2) new L1Teleport().teleport(pc, 33744, 32499, (short)4, 5, true); // ウェルダン 
				else new L1Teleport().teleport(pc, 33742, 32483, (short)4, 5, true); // ウェルダン 
			}
			ar.RemoveLairUser(pc); // すべてのリストの初期化
		}
		_isAntaras = false;
		DelMonster(map); //マップ内のすべてのオブジェクトを削除
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
		case 5: return _antalist; default: return null; }
	}
	/** アンタラスレアに進入したユーザーの数を持って来る*/
	public int countLairUser() { return _antalist.size(); }
	/** アンタラスレアに進入するユーザを入れる */
	public void addLairUser(L1PcInstance pc) { if (!_antalist.contains(pc)) _antalist.add(pc); }
	/** アンタラスレアに進入していたユーザーかどうかを確認し */
	public boolean isLairUser(L1PcInstance pc) { return _antalist.contains(pc); }
	/** アンタラスレアに進入していたユーザーを消す */
	public void RemoveLairUser(L1PcInstance pc) { if (_antalist.contains(pc)) _antalist.remove(pc); }
	/**アンタラスが浮かび上がったのか教えてくれる */
	public boolean isAntaras() { return _isAntaras; }
	public int getAntaId() { return _id; }
}