package l1j.server.server.command.executor;

import java.util.Random;
import java.util.StringTokenizer;

import l1j.server.server.IdFactory;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1FakeCharacter implements L1CommandExecutor {

	private static Random _random =  new Random(System.nanoTime());


	//	private static final int[] MALE_LIST = new int[] { 61, 138, 734, 2786, 6658, 6671, 12490 };
	//	private static final int[] FEMALE_LIST = new int[] { 48, 37, 1186, 2796, 6661, 6650, 12494 };

	private L1FakeCharacter() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1FakeCharacter();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer stringtokenizer = new StringTokenizer(arg);
			String name = stringtokenizer.nextToken();

			if (CharacterTable.doesCharNameExist(name) || L1World.getInstance().getPlayer(name) != null) {
				pc.sendPackets(new S_SystemMessage("이미 존재하는 캐릭터 이름입니다"));
				return;
			}
			L1PcInstance newPc = new L1PcInstance();
			newPc.setAccountName("");
			newPc.setId(IdFactory.getInstance().nextId());
			newPc.setName(name);
			newPc.setHighLevel(1);
			newPc.setExp(0);
			newPc.addBaseMaxHp((short)2000);//14
			newPc.setCurrentHp(2000);//14
			newPc.setDead(false);
			newPc.setStatus(0);
			newPc.addBaseMaxMp((short)2);
			newPc.setCurrentMp(2);			
			newPc.getAbility().setBaseStr(16);
			newPc.getAbility().setStr(16);
			newPc.getAbility().setBaseCon(16);
			newPc.getAbility().setCon(16);
			newPc.getAbility().setBaseDex(11);
			newPc.getAbility().setDex(11);
			newPc.getAbility().setBaseCha(13);
			newPc.getAbility().setCha(13);
			newPc.getAbility().setBaseInt(12);
			newPc.getAbility().setInt(12);
			newPc.getAbility().setBaseWis(11);
			newPc.getAbility().setWis(11);
			int ran = _random.nextInt(120);			
			if (ran >= 0 && ran < 15) { // 15 남기사가 15프로
				newPc.setClassId(61);
				newPc.setTempCharGfx(61);
				newPc.setGfxId(61);
				newPc.setType(61);
			} else if (ran >= 15 && ran < 20){ // 5// 여캐릭은 무조건 5프로 왜냐 프리섭은 여캐릭은 거의 안함.
				newPc.setClassId(48);
				newPc.setTempCharGfx(48);
				newPc.setGfxId(48);
				newPc.setType(48);
			} else if (ran >= 20 && ran < 30){ // 10
				newPc.setClassId(138);
				newPc.setTempCharGfx(138);
				newPc.setGfxId(138);
				newPc.setType(138);
			} else if (ran >= 30 && ran < 35){ // 5
				newPc.setClassId(37);
				newPc.setTempCharGfx(37);
				newPc.setGfxId(37);
				newPc.setType(37);
			} else if (ran >= 35 && ran < 45){ // 10
				newPc.setClassId(734);
				newPc.setTempCharGfx(734);
				newPc.setGfxId(734);
				newPc.setType(734);
			} else if (ran >= 45 && ran < 50){ // 5
				newPc.setClassId(1186);
				newPc.setTempCharGfx(1186);
				newPc.setGfxId(1186);
				newPc.setType(1186);
			} else if (ran >= 50 && ran < 60){// 10
				newPc.setClassId(2786);
				newPc.setTempCharGfx(2786);
				newPc.setGfxId(2786);
				newPc.setType(2786);
			} else if (ran >= 60 && ran < 65){ // 5
				newPc.setClassId(2796);
				newPc.setTempCharGfx(2796);
				newPc.setGfxId(2796);
				newPc.setType(2796);
			} else if (ran >= 65 && ran < 75){ // 10
				newPc.setClassId(6658);
				newPc.setTempCharGfx(6658);
				newPc.setGfxId(6658);
				newPc.setType(6658);
			} else if (ran >= 75 && ran < 80){// 5
				newPc.setClassId(6661);
				newPc.setTempCharGfx(6661);
				newPc.setGfxId(6661);
				newPc.setType(6661);
			} else if (ran >= 80 && ran < 90){
				newPc.setClassId(6671);
				newPc.setTempCharGfx(6671);
				newPc.setGfxId(6671);
				newPc.setType(6671);
			} else if (ran >= 90 && ran < 100){
				newPc.setClassId(6650);
				newPc.setTempCharGfx(6650);
				newPc.setGfxId(6650);
				newPc.setType(6650);
			} else if (ran >= 100 && ran < 110){
				newPc.setClassId(12490);
				newPc.setTempCharGfx(12490);
				newPc.setGfxId(12490);
				newPc.setType(12490);
			} else if (ran >= 110 && ran < 120){
				newPc.setClassId(12494);
				newPc.setTempCharGfx(12494);
				newPc.setGfxId(12494);
				newPc.setType(12494);
			}	

			newPc.setCurrentWeapon(0);
			newPc.setHeading(pc.getHeading());
			newPc.setX(pc.getX());
			newPc.setY(pc.getY());
			newPc.setMap(pc.getMap());

			newPc.set_food(39);
			newPc.setLawful(30000);

			newPc.setTitle("");
			newPc.setClanid(0);
			newPc.setClanname("");
			newPc.setClanRank(0);
			newPc.setBonusStats(0);
			newPc.setElixirStats(0);
			newPc.setElfAttr(0);
			newPc.set_PKcount(0);
			newPc.setExpRes(0);
			newPc.setPartnerId(0);
			newPc.setAccessLevel((short)0);
			newPc.setGm(false);
			newPc.setMonitor(false);
			newPc.setOnlineStatus(1);
			newPc.setHomeTownId(0);
			newPc.setContribution(0);
			newPc.setHellTime(0);
			newPc.setBanned(false);
			newPc.setKarma(0);
			newPc.setReturnStat(0);

			newPc.refresh();
			newPc.setMoveSpeed(0);
			newPc.setBraveSpeed(0);
			newPc.setGmInvis(false);
			newPc.noPlayerck2 = true;

			L1ItemInstance  item = ItemTable.getInstance().createItem(35);//수련자의 한손검
			L1ItemInstance item1 = ItemTable.getInstance().createItem(175);//수련자의 활
			L1ItemInstance item2 = ItemTable.getInstance().createItem(120);//수련자의 지팡이
			L1ItemInstance item3 = ItemTable.getInstance().createItem(73);// 수련자의 이도류
			L1ItemInstance item4 = ItemTable.getInstance().createItem(203012);// 수련자의 도끼

			if (newPc.isKnight() || newPc.isCrown() || newPc.isDragonknight()) {// 기사.군주.용기사
				newPc.getInventory().storeItem(item);
				newPc.getInventory().setEquipped(item, true);
			} else if (newPc.isElf()){ //요정
				newPc.getInventory().storeItem(item1);
				newPc.getInventory().setEquipped(item1, true);
			} else if (newPc.isWizard() || newPc.isBlackwizard()){//마법사. 환술사
				newPc.getInventory().storeItem(item2);
				newPc.getInventory().setEquipped(item2, true);
			} else if (newPc.isDarkelf()){ //다크엘프
				newPc.getInventory().storeItem(item3);
				newPc.getInventory().setEquipped(item3, true);
			} else if (newPc.isWarrior()){ //전사
				newPc.getInventory().storeItem(item4);
				newPc.getInventory().setEquipped(item4, true);
			}

			L1World.getInstance().storeObject(newPc);
			L1World.getInstance().addVisibleObject(newPc);

			newPc.setNetConnection(null);

			newPc.startObjectAutoUpdate();

		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage((new StringBuilder()). append(".무인 [캐릭이름]로 입력해 주세요. "). toString()));
		}
	}

}