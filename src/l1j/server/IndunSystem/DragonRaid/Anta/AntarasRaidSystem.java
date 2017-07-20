package l1j.server.IndunSystem.DragonRaid.Anta;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.utils.L1SpawnUtil;

public class AntarasRaidSystem {	

	private static AntarasRaidSystem _instance;

	private final Map<Integer, AntarasRaid> _list = new ConcurrentHashMap<Integer, AntarasRaid>();

	private final ArrayList<Integer> _map = new ArrayList<Integer>();

	public static AntarasRaidSystem getInstance() {
		if (_instance == null) { _instance = new AntarasRaidSystem(); }
		return _instance;
	}

	public AntarasRaidSystem(){  _map.add(1005);  }

	static class AntarasMsgTimer implements Runnable {

		private int _mapid = 0; 
		private int _type = 0; 
		private int _stage = 0;

		public AntarasMsgTimer(int mapid, int type, int stage){ 
			_mapid = mapid; 
			_type = type; 
			_stage = stage;			
		}

		@Override
		public void run() {
			try {
				/**Lair = ヒット部屋、Romm1 = 1番の部屋、Room2 = 2番の部屋、Room3 = 3番の部屋Room4 = 4番の部屋**/
				ArrayList<L1PcInstance> Lair = AntarasRaidSystem.getInstance().getAR(_mapid).getRoomList(5);
				switch(_type){
				/**1次アンタラス出る
				 * 1570アンタラス：私の睡眠を目覚めさせるう！誰ですか？ 
				 * 1571クレイ：アンタラス！君追いかけ、ここ漆黒の闇まできた！ 
				 * 1572アンタラス：可塑ロプグン。もう一度殺してやる、クレイ！ 
				 * 1573アンタラス：愚かな者よ！私の怒りを刺激するな。 
				 * 1574クレイ：勇者たちよ君のナイフのアデンの運命がかかっている。 
				 *             アンタラスの黒い息を止めることは君だけだ！ 
				 *
				 *              */
				case 1:
					for(int i =0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){ pc.sendPackets(new S_ServerMessage(1570)); }
					} Thread.sleep(3000);
					for(int i =0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){ pc.sendPackets(new S_ServerMessage(1571)); }
					}Thread.sleep(3000);
					for(int i =0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){ pc.sendPackets(new S_ServerMessage(1572)); }
					}Thread.sleep(3000);
					for(int i =0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){ pc.sendPackets(new S_ServerMessage(1573)); }
					}Thread.sleep(3000);
					for(int i =0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){ pc.sendPackets(new S_ServerMessage(1574)); }
					}Thread.sleep(3000);
					for(int i =0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){ pc.sendPackets(new S_ChatPacket(pc,"システムメッセージ：アンタラスを攻略することができます。")); }
					}Thread.sleep(3000);
					AntarasRaidSpawn.getInstance().fillSpawnTable(_mapid, _stage);
					break;
					/**1次死んだ後のメッセージ
					 ** 1575アンタラス：このジョムレギたちには私を倒すことができるらしい！クハハハ。 
					 ** 1576アンタラス：今おいしい食事をするには？あなた血の匂いが私を狂わせるんだ。 * /
				case 2:
					for(int i =0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){ pc.sendPackets(new S_ServerMessage(1575)); }
					}Thread.sleep(20000);
					for(int i =0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){ pc.sendPackets(new S_ServerMessage(1576)); }
					}
					break;
					/**2次アンタラス死んだ後のメッセージ
					 * 1577クレイ：うおおおっ！被固まった怨霊の叫びが聞こえない！死んロット！ 
					 * 1578アンタラス：あえて私の相手しようとするなんて。それでも、あなたがたは生きる道を見たか？ 
					 * 1579アンタラス：私の怒りが天に届いた。もうすぐ私の父が出るだろう。*/
				case 3:
					for(int i =0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){ pc.sendPackets(new S_ServerMessage(1577)); }
					}Thread.sleep(3000);
					for(int i =0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){ pc.sendPackets(new S_ServerMessage(1578)); }
					}Thread.sleep(20000);
					for(int i =0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){ pc.sendPackets(new S_ServerMessage(1579)); } }
					break; 
					/**3次アンタラス死んだ後のメッセージ
					 * 1580アンタラス：夕暮れの呪いが君にあるように！シーレンよ、私の母よ、私の呼吸を。おさめソーサー... 
					 * 1581クレイ：おお...最強の勇者であることを証明した最高のナイトよ！ 
					 *巨大な試練を乗り越えて、あなたの手にアンタラスの血を埋葬された！いよいよこの恨みをフル得ない。 
					 * ウハハハハ！ありがとう。地上に最も強い勇士たちよ！
				 */
				case 4:
					for (int i = 0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						pc.removeSkillEffect(L1SkillId.ANTA_BUFF);
						pc.killSkillEffectTimer(L1SkillId.ANTA_BUFF);
						if (pc.isPrivateShop()){
							continue;
						}
						if (pc.isAutoClanjoin()){
							continue;
						}
						if (pc.getMapId() == _mapid){
							pc.setSkillEffect(L1SkillId.ANTA_BUFF, 10800 * 1000);
							pc.getAC().addAc(-2);
							pc.getResistance().addWind(50);
							pc.sendPackets(new S_OwnCharStatus(pc));
							pc.sendPackets(new S_SkillSound(pc.getId(), 7783));
							pc.broadcastPacket(new S_SkillSound(pc.getId(), 7783));
							pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONBLOOD, 82, 10800/60));
							pc.sendPackets(new S_ServerMessage(1628)); 						
						}
					}
					Thread.sleep(5000);
					for(int i =0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){ pc.sendPackets(new S_ServerMessage(1580)); }
					}Thread.sleep(3000);
					for(int i =0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){ pc.sendPackets(new S_ServerMessage(1581)); } 
					}Thread.sleep(5000);
					for (L1Object obj : L1World.getInstance().getVisibleObjects(_mapid).values()){
						if (obj instanceof L1MonsterInstance){
							L1MonsterInstance mob = (L1MonsterInstance) obj;
							if (mob.getNpcId() == 900013){
								Mapdrop(mob);
							}
						}
					}
					//					for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
					//						if(pc.isPrivateShop()){
					//							continue;
					//						}
					//						pc.removeSkillEffect(L1SkillId.ANTA_BUFF）; //すでにスキルがある場合は、削除した後
					//						if (pc.getMapId() == _mapid){
					//							pc.setSkillEffect(L1SkillId.ANTA_BUFF、18000 * 1000）; //再スキルの生成を実行してくれる。
					//							pc.getAC().addAc(-2);
					//							pc.getResistance().addWater(50);
					//							pc.sendPackets(new S_OwnCharStatus(pc));
					//							pc.sendPackets(new S_SkillSound(pc.getId(), 7783));
					//							pc.broadcastPacket(new S_SkillSound(pc.getId(), 7783));
					//							pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONBLOOD, 82, 18000/60)); 
					//							pc.sendPackets(new S_ServerMessage(1628)); 
					//						}
					//					} Thread.sleep(3000);
					for(int i =0; i < Lair.size(); i++){
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){ pc.sendPackets(new S_ChatPacket(pc,"システムメッセージ：しばらくして村にテレポートされます。"));
//						System.out.println("■■■■■■■■■■アンタラスレイド終了■■■■■■■■■■ "）;
						}
					} Thread.sleep(15000); 
					for(int i =0; i < Lair.size(); i++){ //[成功ギランテル]
						L1PcInstance pc = Lair.get(i);
						if(pc.getMapId() == _mapid){
							Random random = new Random();
							int Dragontel = random.nextInt(3)+ 1;
							if(Dragontel == 1) new L1Teleport().teleport(pc, 33440, 32817, (short)4, 5, true); //ギラン 
							else if(Dragontel == 2) new L1Teleport().teleport(pc, 33436, 32800, (short)4, 5, true); // ギラン 
							else new L1Teleport().teleport(pc, 33443, 32798, (short)4, 5, true); // ギラン						
						}
					}
					break;
					/**1582	ドワーフの呼出し：ウェルダン村に隠された龍らの土地に行くドアが開かれました。
					 * 1583ドワーフの呼出し：隠された龍らの土地に行くドアがすでにウェルダン村に開いている。
					 * 1593	ドワーフの呼出し：アンタラスの黒い息を止めるた勇士たち誕生しました。！
					 * 1608アンタラス：貴様の無謀さもここまでだ...！ここで終わりをマトイハラ！
					 * 1609	クレイ：もはや貴重な勇士を失うことはオプソ。最後に残った力で今君を召喚られるでしょう。**/
				default: 
					break;  //クラスファイルが..すべて負うて、同じデング？
				}
			} catch (Exception exception) {	}
		}
		
		// 自動分配
		private void Mapdrop(L1NpcInstance npc){
			L1Inventory inventory = npc.getInventory();
			L1ItemInstance item;
			L1Inventory targetInventory = null;
			L1PcInstance player;
			Random random = new Random();
			L1PcInstance acquisitor;
			ArrayList<L1PcInstance> acquisitorList = new ArrayList<L1PcInstance>();
			L1PcInstance[] pclist = L1World.getInstance().getAllPlayers3();
			for(L1PcInstance temppc : pclist){
				if(temppc.getMapId() == npc.getMapId())
					acquisitorList.add(temppc);
			}
			for (int i = inventory.getSize(); i > 0; i--) {
				item = inventory.getItems().get(0);

				if (item.getItem().getType2() == 0 && item.getItem().getType() == 2) {
					item.setNowLighting(false);
				}
				acquisitor = acquisitorList.get(random.nextInt(acquisitorList.size()));
				if (acquisitor.getInventory().checkAddItem(item,item.getCount()) == L1Inventory.OK) {
						targetInventory = acquisitor.getInventory();
						player = acquisitor;
						L1ItemInstance l1iteminstance = player.getInventory().findItemId(L1ItemId.ADENA); //所持
						if (l1iteminstance != null&& l1iteminstance.getCount() > 2000000000) {
								targetInventory = L1World.getInstance().getInventory(acquisitor.getX(),acquisitor.getY(),acquisitor.getMapId()); //持つことができ
								player.sendPackets(new S_ServerMessage(166,"所持しているアデナ "," 20億を超えています。 "));
						}else{
							for(L1PcInstance temppc : acquisitorList){
									temppc.sendPackets(new S_ServerMessage(813, npc.getName(), item.getLogName(), player.getName()));
							}
						}
				} else {
						targetInventory = L1World.getInstance().getInventory(acquisitor.getX(),acquisitor.getY(),acquisitor.getMapId()); //持つことができ
				}
				inventory.tradeItem(item, item.getCount(), targetInventory);
			}
			npc.getLight().turnOnOffLight();
		}
	}

	public void startRaid(L1PcInstance pc){
		int id = blankMapId();
		if (id != 1005) L1WorldMap.getInstance().cloneMap(1005, id);
		AntarasRaid ar = new AntarasRaid(id);
		AntarasRaidSpawn.getInstance().fillSpawnTable(id, 0);
//		System.out.println("■■■■■■■■■■ アンタラスレイド開始■■■■■■■■■■MAP  -  "+ _map）;
		L1SpawnUtil.spawn2(pc.getX(), pc.getY(), pc.getMapId(), 900007, 0, 3600 * 1000, id); // ヒットポータル出現
		L1SpawnUtil.spawn2(32703, 32669, (short)id, 900008, 0, 3600 * 1000, id);//[アンタラス待機部屋] =>ヒットレア
		L1SpawnUtil.spawn2(32703, 32670, (short)id, 900008, 0, 3600 * 1000, id);//[アンタラス待機部屋] =>ヒットレア
		L1SpawnUtil.spawn2(32623, 32725, (short)id, 810851, 0, 3600 * 1000, id);//[アンタラス待機部屋] =>ヒットレア
		_list.put(id, ar);
		AntarasRaidTimer RaidEndTime = new AntarasRaidTimer(ar, 5, 0, 3500 * 1000);// 2時間7200
		RaidEndTime.begin();
	}

	public AntarasRaid getAR(int id){ return _list.get(id); }

	/** 空のマップIDを持って来る @return */
	public int blankMapId(){
		int mapid = 0;
		if(_list.size() == 0) return 1005;
		mapid = 6000 + _list.size();
		return mapid;
	}
	/** ポータル本数 */
	public int countRaidPotal(){ return _list.size(); }

}
