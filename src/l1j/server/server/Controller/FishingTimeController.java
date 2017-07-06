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
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_Fishing2;

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
	
	public boolean 성장낚시 = false;

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
						/** 미끼가 있을경우 * */
						// 미끼 확인. 성장의 낚싯대면 미끼없어도됨.
						if(pc._fishingRod.getItemId()==600229 || pc.getInventory().consumeItem(41295, 1)){
							//릴 장착 고탄력 낚싯대
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
										릴장착고탄력낚싯대(pc);
									}
								}
							} else if(pc._fishingRod.getItemId() == 41305){ //릴장착 은빛 낚싯대
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
										릴장착은빛낚싯대(pc);
									}
								}
							} else if(pc._fishingRod.getItemId() == 41306){ //릴장착 금빛 낚싯대
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
										릴장착금빛낚싯대(pc);
									}
								}
							} else if (pc._fishingRod.getItemId() == 600229){ // 성장의 낚시대
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
										성장낚시 = true;
										if (Config.FISH_COM) {
											성장의낚시대1(pc);
										} else {
											성장의낚시대(pc);
										}
									}
								}
							} else if (pc._fishingRod.getItemId() == 9991){ // 황소개구리 낚시대
								L1ItemInstance item = pc._fishingRod;
								if (item != null) {
									if (item.getChargeCount() <= 0) {
										L1ItemInstance newfishingRod = null;
										pc.getInventory().removeItem(item, 1);
										newfishingRod = pc.getInventory().storeItem(9993, 1); //부러진낚싯대
										pc._fishingRod = newfishingRod;
										endFishing(pc);
									} else {
										item.setChargeCount(item.getChargeCount() - 1);
										pc.getInventory().updateItem(item, L1PcInventory.COL_CHARGE_COUNT);
										pc.setFishingTime(System.currentTimeMillis() + Config.FISH_TIME * 1000);
										pc.sendPackets(new S_Fishing2(Config.FISH_TIME));
										황소개구리낚싯대(pc);
									}
								}
								//일반 낚싯대
							} else if (pc._fishingRod.getItemId() == 41293){
								pc.setFishingTime(System.currentTimeMillis() + 240000);
								pc.sendPackets(new S_Fishing2(240));
								고탄력낚싯대(pc);
							} 
						} else {
							// 미끼가 없어서 종료 처리 구간.
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
		if(성장낚시){
			성장낚시 = false;
		}
		pc.sendPackets(new S_CharVisualUpdate(pc));
		Broadcaster.broadcastPacket(pc, new S_CharVisualUpdate(pc));
		pc.sendPackets(new S_ServerMessage(1163));  // 낚시가 종료했습니다.
		removeMember(pc);
	}

	private void 성장의낚시대(L1PcInstance pc){
		int chance = _random.nextInt(10000) + 1;
		if (chance < 6000) { //블루베리아나
			successFishing(pc, 41297 , "$15565"); 
		} else if (chance < 8000) { //베리아나
			successFishing(pc, 41296 , "$15564");
		} else if (chance < 8020) { //앵무베리아나
			successFishing(pc, 41298 , "$15566");
		} else if (chance < 8350) { //퓨어 엘릭서
			successFishing(pc, 820018, "$20462");
		} else if (chance < 8351) { //아인하사드의 선물
			successFishing(pc, 600230 , "$20909");
		} else {
			pc.sendPackets(new S_ServerMessage(1136));
			// 낚시에 실패했습니다.
		}
	}
	private void 성장의낚시대1(L1PcInstance pc){
		int chance = _random.nextInt(10000) + 1;
		if (chance < 6000) { //블루베리아나
			successFishing(pc, 600231 , "성장의낚시선물상자"); 
		} else if (chance < 8000) { //베리아나
			successFishing(pc, 600231 , "성장의낚시선물상자");
		} else if (chance < 8020) { //앵무베리아나
			successFishing(pc, 600231 , "성장의낚시선물상자");
		} else if (chance < 8350) { //퓨어 엘릭서
			successFishing(pc, 600231, "성장의낚시선물상자");
		} else if (chance < 8351) { //아인하사드의 선물
			successFishing(pc, 600231 , "성장의낚시선물상자");
		} else {
			pc.sendPackets(new S_ServerMessage(1136));
			// 낚시에 실패했습니다.
		}
	}
	
	private void 황소개구리낚싯대(L1PcInstance pc){
		int chance = _random.nextInt(10000) + 1;
		if (chance < 6000) { //블루베리아나
			successFishing(pc, 41297 , "$15565"); 
		} else if (chance < 8000) { //베리아나
			successFishing(pc, 41296 , "$15564");
		} else if (chance < 8020) { //앵무베리아나
			successFishing(pc, 41298 , "$15566");
		} else if (chance < 8150) { // 황소 개구리
			successFishing(pc, 9992, "$22045"); 
		} else if (chance < 8350) { //축축한 낚시가방
			successFishing(pc, 41301, "$15815");
		} else if (chance < 8351) { //작은 은빛 베리아나
			successFishing(pc, 41299 , "$17521");
		} else if (chance < 8352) { //작은 금빛 베리아나
			successFishing(pc, 41300 , "$17523");
		} else {
			pc.sendPackets(new S_ServerMessage(1136));
			// 낚시에 실패했습니다.
		}
	}

	private void 릴장착고탄력낚싯대(L1PcInstance pc){
		int chance = _random.nextInt(10000) + 1;
		if (chance < 6000) { //블루베리아나
			successFishing(pc, 41297 , "$15565"); 
		} else if (chance < 8000) { //베리아나
			successFishing(pc, 41296 , "$15564");
		} else if (chance < 8020) { //앵무베리아나
			successFishing(pc, 41298 , "$15566");
		} else if (chance < 8350) { //축축한 낚시가방
			successFishing(pc, 41301, "$15815");
		} else if (chance < 8351) { //작은 은빛 베리아나
			successFishing(pc, 41299 , "$17521");
		} else if (chance < 8352) { //작은 금빛 베리아나
			successFishing(pc, 41300 , "$17523");
		} else {
			pc.sendPackets(new S_ServerMessage(1136));
			// 낚시에 실패했습니다.
		}
	}

	private void 릴장착은빛낚싯대(L1PcInstance pc){
		int chance = _random.nextInt(10000) + 1;
		if (chance < 4000) { //블루베리아나
			successFishing(pc, 41297 , "$15565"); 
		} else if (chance < 8000) { //베리아나
			successFishing(pc, 41296 , "$15564");
		} else if (chance < 8040) { //앵무베리아나
			successFishing(pc, 41298 , "$15566"); 
		} else if (chance < 8350) { //축축한 낚시가방
			successFishing(pc, 41301, "$15815");
		} else if (chance < 8352) { //작은 은빛 베리아나
			successFishing(pc, 41299, "$17521"); 
		} else if (chance < 8353) { //큰 은빛 베리아나
			successFishing(pc, 41303, "$17522"); 
		} else {
			pc.sendPackets(new S_ServerMessage(1136));
			// 낚시에 실패했습니다.
		}
	}

	private void 릴장착금빛낚싯대(L1PcInstance pc){
		int chance = _random.nextInt(10000) + 1;
		if (chance < 3500) { //블루베리아나
			successFishing(pc, 41297 , "$15565"); 
		} else if (chance < 8000) { //베리아나
			successFishing(pc, 41296 , "$15564");
		} else if (chance < 8050) { //앵무베리아나
			successFishing(pc, 41298 , "$15566"); 
		} else if (chance < 8350) { //축축한 낚시가방
			successFishing(pc, 41301, "$15815");
		} else if (chance < 8352) { //작은 금빛 베리아나
			successFishing(pc, 41300 , "$17523"); 
		} else if (chance < 8354) { //큰 금빛 베리아나
			successFishing(pc, 41304 , "$17524"); // 
		} else {
			pc.sendPackets(new S_ServerMessage(1136));
			// 낚시에 실패했습니다.
		}
	}

	private void 고탄력낚싯대(L1PcInstance pc){
		int chance = _random.nextInt(10000) + 1; //100%
		//베리아나
		if (chance < 4000) {
			successFishing(pc, 41296 , "$15564");
			//블루베리아나
		} else if (chance < 8000) {
			successFishing(pc, 41297 , "$15565"); 
			//앵무베리아나
		} else if (chance < 8010) {
			successFishing(pc, 41298 , "$15566"); 
			//축축한 낚시가방
		} else if (chance < 8350) {
			successFishing(pc, 41301, "$15815");
		} else {
			pc.sendPackets(new S_ServerMessage(1136)); // 16%
			// 낚시에 실패했습니다.
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
			pc.sendPackets(new S_ServerMessage(1185, message)); // 낚시에 성공해%0%o를 낚시했습니다.
		}
		
		if (itemid == 41300) {
			for (L1Object obj : L1World.getInstance().getVisibleObjects(5490).values()) {
				if (obj instanceof L1PcInstance) {
					L1PcInstance player = (L1PcInstance) obj;
					String chatText = "누군가가 작은 금빛 베리아나를 낚아 올렸습니다!";
					player.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, chatText));
				}
			}
		} else if (itemid == 41304) {
			for (L1Object obj : L1World.getInstance().getVisibleObjects(5490).values()) {
				if (obj instanceof L1PcInstance) {
					L1PcInstance player = (L1PcInstance) obj;
					String chatText = "누군가가 큰 금빛 베리아나를 낚아 올렸습니다!";
					player.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, chatText));
				}
			}
		} else if (성장낚시) {
			int exp = Config.FISH_EXP;
			double dragon = 1;
			int settingEXP = (int) Config.RATE_XP;
			if (pc.PC방_버프) {
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