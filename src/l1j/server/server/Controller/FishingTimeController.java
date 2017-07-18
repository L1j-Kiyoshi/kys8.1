package l1j.server.server.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
//import java.util.logging.Level;

import l1j.server.Config;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_Fishing2;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;

public class FishingTimeController implements Runnable {

	public static final int SLEEP_TIME = 300;

	private static FishingTimeController _instance;

	private final List<L1PcInstance> _fishingList = new ArrayList<L1PcInstance>();

	private static Random _random = new Random(System.nanoTime());

	public static FishingTimeController getInstance() {
		if (_instance == null) {
			_instance = new FishingTimeController();
		}
		return _instance;
	}

	public void run() {
		try {
			fishing();
		} catch (Exception e1) {
		}
	}

	public void addMember(L1PcInstance pc) {
		if (pc == null || _fishingList.contains(pc)) {
			return;
		}
		_fishingList.add(pc);

	}

	public void removeMember(L1PcInstance pc) {
		if (pc == null || !_fishingList.contains(pc)) {
			return;
		}
		_fishingList.remove(pc);
	}
	
	public boolean growingFishing = false;

	private void fishing() {
		if (_fishingList.size() > 0) {
			long currentTime = System.currentTimeMillis();
			L1PcInstance pc = null;
			for (int i = 0; i < _fishingList.size(); i++) {
				pc = _fishingList.get(i);
				if (pc == null)
					continue;
				if (pc.getMapId() != 4 && pc.getMapId() != 5490)
					continue;
				if (pc.isFishing()) {
					long time = pc.getFishingTime();
					if (currentTime > (time + 1000)) {
						/** 餌がある場合 * */
						// 餌確認した。成長の釣り竿面餌なくてもされる。
						if(pc._fishingRod.getItemId()==600229 || pc.getInventory().consumeItem(41295, 1)){
							//リール装着高弾力釣り竿
							if(pc._fishingRod.getItemId() == 41294){
								L1ItemInstance item = pc._fishingRod;
								if (item != null) {
									if (item.getChargeCount() <= 0) {
										L1ItemInstance newfishingRod = null;
										pc.getInventory().removeItem(item, 1);
										newfishingRod = pc.getInventory().storeItem(41293, 1);
										pc._fishingRod = newfishingRod;
										endFishing(pc);
									} else {
										item.setChargeCount(item.getChargeCount() - 1);
										pc.getInventory().updateItem(item, L1PcInventory.COL_CHARGE_COUNT);
										pc.setFishingTime(System.currentTimeMillis() + Config.FISH_TIME * 1000);
										pc.sendPackets(new S_Fishing2(Config.FISH_TIME));
										reelHighFishingRod(pc);
									}
								}
							} else if(pc._fishingRod.getItemId() == 41305){ //リール装着銀色釣り竿
								L1ItemInstance item = pc._fishingRod;
								if (item != null) {
									if (item.getChargeCount() <= 0) {
										L1ItemInstance newfishingRod = null;
										pc.getInventory().removeItem(item, 1);
										newfishingRod = pc.getInventory().storeItem(41293, 1);
										pc._fishingRod = newfishingRod;
										endFishing(pc);
									} else {
										item.setChargeCount(item.getChargeCount() - 1);
										pc.getInventory().updateItem(item, L1PcInventory.COL_CHARGE_COUNT);
										pc.setFishingTime(System.currentTimeMillis() + Config.FISH_TIME * 1000);
										pc.sendPackets(new S_Fishing2(Config.FISH_TIME));
										reelSilverFishingRod(pc);
									}
								}
							} else if(pc._fishingRod.getItemId() == 41306){ //リール装着金色釣り竿
								L1ItemInstance item = pc._fishingRod;
								if (item != null) {
									if (item.getChargeCount() <= 0) {
										L1ItemInstance newfishingRod = null;
										pc.getInventory().removeItem(item, 1);
										newfishingRod = pc.getInventory().storeItem(41293, 1);
										pc._fishingRod = newfishingRod;
										endFishing(pc);
									} else {
										item.setChargeCount(item.getChargeCount() - 1);
										pc.getInventory().updateItem(item, L1PcInventory.COL_CHARGE_COUNT);
										pc.setFishingTime(System.currentTimeMillis() + Config.FISH_TIME * 1000);
										pc.sendPackets(new S_Fishing2(Config.FISH_TIME));
										reelGoldFishingRod(pc);
									}
								}
							} else if (pc._fishingRod.getItemId() == 600229){ // 成長の釣り竿
								L1ItemInstance item = pc._fishingRod;
								if (item != null) {
									if (item.getChargeCount() <= 0) {
										L1ItemInstance newfishingRod = null;
										pc.getInventory().removeItem(item, 1);
										newfishingRod = pc.getInventory().storeItem(41293, 1);
										pc._fishingRod = newfishingRod;
										endFishing(pc);
									} else {
										item.setChargeCount(item.getChargeCount() - 1);
										pc.getInventory().updateItem(item, L1PcInventory.COL_CHARGE_COUNT);
										pc.setFishingTime(System.currentTimeMillis() + Config.FISH_TIME * 1000);
										pc.sendPackets(new S_Fishing2(Config.FISH_TIME));
										growingFishing = true;
										if (Config.FISH_COM) {
											growingFishingRod1(pc);
										} else {
											growingFishingRod(pc);
										}
									}
								}
							} else if (pc._fishingRod.getItemId() == 9991){ //ウシガエル釣り竿
								L1ItemInstance item = pc._fishingRod;
								if (item != null) {
									if (item.getChargeCount() <= 0) {
										L1ItemInstance newfishingRod = null;
										pc.getInventory().removeItem(item, 1);
										newfishingRod = pc.getInventory().storeItem(9993, 1); //折れた釣り竿
										pc._fishingRod = newfishingRod;
										endFishing(pc);
									} else {
										item.setChargeCount(item.getChargeCount() - 1);
										pc.getInventory().updateItem(item, L1PcInventory.COL_CHARGE_COUNT);
										pc.setFishingTime(System.currentTimeMillis() + Config.FISH_TIME * 1000);
										pc.sendPackets(new S_Fishing2(Config.FISH_TIME));
										bullfrogFishingRod(pc);
									}
								}
								//一般釣り竿
							} else if (pc._fishingRod.getItemId() == 41293){
								pc.setFishingTime(System.currentTimeMillis() + 240000);
								pc.sendPackets(new S_Fishing2(240));
								highFishingRod(pc);
							} 
						} else {
							// 餌がなくて終了処理区間。
							endFishing(pc);
						}
					}
				}
			}
		}
	}	 

	private void endFishing(L1PcInstance pc) {
		pc.setFishingTime(0);
		pc.setFishingReady(false);
		pc.setFishing(false);
		pc._fishingRod = null;
		if(growingFishing){
			growingFishing = false;
		}
		pc.sendPackets(new S_CharVisualUpdate(pc));
		Broadcaster.broadcastPacket(pc, new S_CharVisualUpdate(pc));
		pc.sendPackets(new S_ServerMessage(1163));  // 釣りが終了しました。
		removeMember(pc);
	}

	private void growingFishingRod(L1PcInstance pc){
		int chance = _random.nextInt(10000) + 1;
		if (chance < 6000) { //ブルーベリーアナ
			successFishing(pc, 41297 , "$15565"); 
		} else if (chance < 8000) { //ベリーアナ
			successFishing(pc, 41296 , "$15564");
		} else if (chance < 8020) { //インコベリーアナ
			successFishing(pc, 41298 , "$15566");
		} else if (chance < 8350) { //ピュアエリクサー
			successFishing(pc, 820018, "$20462");
		} else if (chance < 8351) { //アインハザードのギフト
			successFishing(pc, 600230 , "$20909");
		} else {
			pc.sendPackets(new S_ServerMessage(1136));
			// 釣りに失敗しました。
		}
	}
	private void growingFishingRod1(L1PcInstance pc){
		int chance = _random.nextInt(10000) + 1;
		if (chance < 6000) { //ブルーベリーアナ
			successFishing(pc, 600231 , "成長の釣りのギフトボックス"); 
		} else if (chance < 8000) { //ベリーアナ
			successFishing(pc, 600231 , "成長の釣りのギフトボックス");
		} else if (chance < 8020) { //インコベリーアナ
			successFishing(pc, 600231 , "成長の釣りのギフトボックス");
		} else if (chance < 8350) { //ピュアエリクサー
			successFishing(pc, 600231, "成長の釣りのギフトボックス");
		} else if (chance < 8351) { //アインハザードのギフト
			successFishing(pc, 600231 , "成長の釣りのギフトボックス");
		} else {
			pc.sendPackets(new S_ServerMessage(1136));
			// 釣りに失敗しました。
		}
	}
	
	private void bullfrogFishingRod(L1PcInstance pc){
		int chance = _random.nextInt(10000) + 1;
		if (chance < 6000) { //ブルーベリーアナ
			successFishing(pc, 41297 , "$15565"); 
		} else if (chance < 8000) { //ベリーアナ
			successFishing(pc, 41296 , "$15564");
		} else if (chance < 8020) { //インコベリーアナ
			successFishing(pc, 41298 , "$15566");
		} else if (chance < 8150) { // ウシガエル
			successFishing(pc, 9992, "$22045"); 
		} else if (chance < 8350) { //湿った釣りバッグ
			successFishing(pc, 41301, "$15815");
		} else if (chance < 8351) { //小さな銀色のベリーアナ
			successFishing(pc, 41299 , "$17521");
		} else if (chance < 8352) { //小さな金色ベリーアナ
			successFishing(pc, 41300 , "$17523");
		} else {
			pc.sendPackets(new S_ServerMessage(1136));
			// 釣りに失敗しました。
		}
	}

	private void reelHighFishingRod(L1PcInstance pc){
		int chance = _random.nextInt(10000) + 1;
		if (chance < 6000) { //ブルーベリーアナ
			successFishing(pc, 41297 , "$15565"); 
		} else if (chance < 8000) { //ベリーアナ
			successFishing(pc, 41296 , "$15564");
		} else if (chance < 8020) { //インコベリーアナ
			successFishing(pc, 41298 , "$15566");
		} else if (chance < 8350) { //湿った釣りバッグ
			successFishing(pc, 41301, "$15815");
		} else if (chance < 8351) { //小さな銀色のベリーアナ
			successFishing(pc, 41299 , "$17521");
		} else if (chance < 8352) { //小さな金色ベリーアナ
			successFishing(pc, 41300 , "$17523");
		} else {
			pc.sendPackets(new S_ServerMessage(1136));
			// 釣りに失敗しました。
		}
	}

	private void reelSilverFishingRod(L1PcInstance pc){
		int chance = _random.nextInt(10000) + 1;
		if (chance < 4000) { //ブルーベリーアナ
			successFishing(pc, 41297 , "$15565"); 
		} else if (chance < 8000) { //ベリーアナ
			successFishing(pc, 41296 , "$15564");
		} else if (chance < 8040) { //インコベリーアナ
			successFishing(pc, 41298 , "$15566"); 
		} else if (chance < 8350) { //湿った釣りバッグ
			successFishing(pc, 41301, "$15815");
		} else if (chance < 8352) { //小さな銀色のベリーアナ
			successFishing(pc, 41299, "$17521"); 
		} else if (chance < 8353) { //大きな銀色のベリーアナ
			successFishing(pc, 41303, "$17522"); 
		} else {
			pc.sendPackets(new S_ServerMessage(1136));
			// 釣りに失敗しました。
		}
	}

	private void reelGoldFishingRod(L1PcInstance pc){
		int chance = _random.nextInt(10000) + 1;
		if (chance < 3500) { //ブルーベリーアナ
			successFishing(pc, 41297 , "$15565"); 
		} else if (chance < 8000) { //ベリーアナ
			successFishing(pc, 41296 , "$15564");
		} else if (chance < 8050) { //インコベリーアナ
			successFishing(pc, 41298 , "$15566"); 
		} else if (chance < 8350) { //湿った釣りバッグ
			successFishing(pc, 41301, "$15815");
		} else if (chance < 8352) { //小さな金色ベリーアナ
			successFishing(pc, 41300 , "$17523"); 
		} else if (chance < 8354) { //大きな金色ベリーアナ
			successFishing(pc, 41304 , "$17524"); // 
		} else {
			pc.sendPackets(new S_ServerMessage(1136));
			// 釣りに失敗しました。
		}
	}

	private void highFishingRod(L1PcInstance pc){
		int chance = _random.nextInt(10000) + 1; //100%
		//ベリーアナ
		if (chance < 4000) {
			successFishing(pc, 41296 , "$15564");
			//ブルーベリーアナ
		} else if (chance < 8000) {
			successFishing(pc, 41297 , "$15565"); 
			//インコベリーアナ
		} else if (chance < 8010) {
			successFishing(pc, 41298 , "$15566"); 
			//湿った釣りバッグ
		} else if (chance < 8350) {
			successFishing(pc, 41301, "$15815");
		} else {
			pc.sendPackets(new S_ServerMessage(1136)); // 16%
			// 釣りに失敗しました。
		}
		pc.sendPackets(new S_ServerMessage(1147));
	}


	private void successFishing(L1PcInstance pc, int itemid, String message){
		if(pc.getInventory().getSize() > (180 - 16)) {
			pc.sendPackets(new S_ServerMessage(263));
			return;
		}
		L1ItemInstance item = pc.getInventory().storeItem(itemid, 1);
		if (item != null) {
			pc.sendPackets(new S_ServerMessage(1185, message)); // 釣りに成功して％0％oを釣りました。
		}
		
		if (itemid == 41300) {
			for (L1Object obj : L1World.getInstance().getVisibleObjects(5490).values()) {
				if (obj instanceof L1PcInstance) {
					L1PcInstance player = (L1PcInstance) obj;
					String chatText = "誰かが小さな金色ベリア私釣ってアップしました！";
					player.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, chatText));
				}
			}
		} else if (itemid == 41304) {
			for (L1Object obj : L1World.getInstance().getVisibleObjects(5490).values()) {
				if (obj instanceof L1PcInstance) {
					L1PcInstance player = (L1PcInstance) obj;
					String chatText = "誰かが大きな金色ベリア私釣ってアップしました！";
					player.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, chatText));
				}
			}
		} else if (growingFishing) {
			int exp = Config.FISH_EXP;
			double dragon = 1;
			int settingEXP = (int) Config.RATE_XP;
			if (pc.PCRoom_Buff) {
				dragon += 0.20;
			}	
			if (pc.hasSkillEffect(L1SkillId.EMERALD_YES)) {
				dragon += 0.1805;
			} else if(pc.hasSkillEffect(L1SkillId.EMERALD_NO)) {
				if (pc.getEinhasad() < 1000000){
					dragon += 0.1165;
				} else if (pc.getEinhasad() >= 1000000 && pc.getEinhasad() <= 15000000){
					dragon += 0.1339;
				} else { 
					dragon += 0.0582 ;
				}
				pc.calEinhasad(-exp);
				pc.sendPackets(new S_PacketBox(S_PacketBox.EINHASAD, pc));
				
				double exppenalty = ExpTable.getPenaltyRate(pc.getLevel());
				int add_exp = (int) (exp * settingEXP * dragon * exppenalty);
				pc.addExp(add_exp);
			} else if (pc.hasSkillEffect(L1SkillId.DRAGON_PUPLE) && pc.getEinhasad() > 10000) {
				if (pc.getLevel() >= 49 && pc.getLevel() <= 54)
					dragon += 0.53;
				else if (pc.getLevel() >= 55 && pc.getLevel() <= 59)
					dragon += 0.43;
				else if (pc.getLevel() >= 60 && pc.getLevel() <= 64)
					dragon += 0.33;
				else if (pc.getLevel() >= 65)
					dragon += 0.23;
				pc.calEinhasad(-exp);
				pc.sendPackets(new S_PacketBox(S_PacketBox.EINHASAD, pc));
				if (pc.getEinhasad() <= 10000) {
					pc.removeSkillEffect(L1SkillId.DRAGON_PUPLE);
				}
			} else if (pc.hasSkillEffect(L1SkillId.DRAGON_TOPAZ) && pc.getEinhasad() > 10000) {
				dragon += 0.8;
				pc.calEinhasad(-exp);
				pc.sendPackets(new S_PacketBox(S_PacketBox.EINHASAD, pc));
				if (pc.getEinhasad() <= 10000) {
					pc.removeSkillEffect(L1SkillId.DRAGON_TOPAZ);
				}
			}
			
			double exppenalty = ExpTable.getPenaltyRate(pc.getLevel());
			int add_exp = (int) (exp * settingEXP * dragon * exppenalty);
			pc.addExp(add_exp);
		}

	}
}