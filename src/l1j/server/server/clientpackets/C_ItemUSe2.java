package l1j.server.server.clientpackets;

import static l1j.server.server.model.skill.L1SkillId.ABSOLUTE_BARRIER;
import static l1j.server.server.model.skill.L1SkillId.EARTH_BIND;
import static l1j.server.server.model.skill.L1SkillId.ICE_LANCE;
import static l1j.server.server.model.skill.L1SkillId.SHOCK_STUN;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.ActionCodes;
import l1j.server.server.GameClient;
import l1j.server.server.datatables.ClanTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.Beginner;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.Getback;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1EffectSpawn;
import l1j.server.server.model.L1HouseLocation;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PinkName;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1TownLocation;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1EffectInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_ChangeShape;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_DelSkill;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_Karma;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_OwnCharStatus2;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_ReturnedStat;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.SQLUtil;

public class C_ItemUSe2 extends ClientBasePacket {

	private static final String C_ITEM_USE2 = "[C] C_ItemUSe2";
	
	private static Logger _log = Logger.getLogger(C_ItemUSe2.class.getName());
	
	private static Random _random = new Random(System.nanoTime());
	
	public C_ItemUSe2(byte abyte0[], GameClient client) throws Exception {
		super(abyte0);
		int itemObjid = readD();
		int l = 0;
		
		L1PcInstance pc = client.getActiveChar();
		if(pc == null)
			return;
		
		L1ItemInstance l1iteminstance = pc.getInventory().getItem(itemObjid);

		if (l1iteminstance == null || l1iteminstance.getItem() == null) {
			return;
		}
		
		int itemId;
		int spellsc_objid = 0;
		
		try {
			itemId = l1iteminstance.getItem().getItemId();
		} catch (Exception e) {
			return;
		}
		
	
		
		switch(itemId){
		
		/** 엔코인 아이템화 **/
		case 1000024:	//10
			pc.getNetConnection().getAccount().Ncoin_point += 10;
			pc.getNetConnection().getAccount().updateNcoin();
			pc.sendPackets(new S_SystemMessage("N코인 " + 10 + " 원 충전되었습니다."), true);
			pc.getInventory().removeItem(l1iteminstance, 1);
			break;
		case 1000025:	//100
			pc.getNetConnection().getAccount().Ncoin_point += 100;
			pc.getNetConnection().getAccount().updateNcoin();
			pc.sendPackets(new S_SystemMessage("N코인 " + 100 + " 원 충전되었습니다."), true);
			pc.getInventory().removeItem(l1iteminstance, 1);
			break;
		case 1000026:	//1000
			pc.getNetConnection().getAccount().Ncoin_point += 1000;
			pc.getNetConnection().getAccount().updateNcoin();
			pc.sendPackets(new S_SystemMessage("N코인 " + 1000 + " 원 충전되었습니다."), true);
			pc.getInventory().removeItem(l1iteminstance, 1);
			break;
		case 1000027:	//5000
			pc.getNetConnection().getAccount().Ncoin_point += 5000;
			pc.getNetConnection().getAccount().updateNcoin();
			pc.sendPackets(new S_SystemMessage("N코인 " + 5000 + " 원 충전되었습니다."), true);
			pc.getInventory().removeItem(l1iteminstance, 1);
			break;
		case 1000028:	//10000
			pc.getNetConnection().getAccount().Ncoin_point += 10000;
			pc.getNetConnection().getAccount().updateNcoin();
			pc.sendPackets(new S_SystemMessage("N코인 " + 10000 + " 원 충전되었습니다."), true);
			pc.getInventory().removeItem(l1iteminstance, 1);
			break;
		case 1000029:	//30000
			pc.getNetConnection().getAccount().Ncoin_point += 30000;
			pc.getNetConnection().getAccount().updateNcoin();
			pc.sendPackets(new S_SystemMessage("N코인 " + 30000 + " 원 충전되었습니다."), true);
			pc.getInventory().removeItem(l1iteminstance, 1);
			break;
		
		
		
		case 410057: {// 운세쪽지a
			int[] allBuffSkill = { L1SkillId.FEATHER_BUFF_A };
			L1SkillUse l1skilluse = new L1SkillUse();
			if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_B))
				pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_B);
			if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_C))
				pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_C);
			if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_D))
				pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_D);
			for (int i = 0; i < allBuffSkill.length; i++) {
				l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_SPELLSC);
				pc.broadcastPacket(new S_SkillSound(pc.getId(), 7947));
			}
			pc.getInventory().removeItem(l1iteminstance, 1);
		}
		break;
		
		case 410058: {// 운세쪽지b
			int[] allBuffSkill = { L1SkillId.FEATHER_BUFF_B };
			L1SkillUse l1skilluse = new L1SkillUse();
			if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_A))
				pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_A);
			if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_C))
				pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_C);
			if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_D))
				pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_D);
			for (int i = 0; i < allBuffSkill.length; i++) {
				l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_SPELLSC);
				pc.broadcastPacket(new S_SkillSound(pc.getId(), 7948));
			}
			pc.getInventory().removeItem(l1iteminstance, 1);
		}
		break;
		case 410059: {// 운세쪽지c
			int[] allBuffSkill = { L1SkillId.FEATHER_BUFF_C };
			L1SkillUse l1skilluse = new L1SkillUse();
			if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_B))
				pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_B);
			if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_A))
				pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_A);
			if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_D))
				pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_D);
			for (int i = 0; i < allBuffSkill.length; i++) {
				l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_SPELLSC);
				pc.broadcastPacket(new S_SkillSound(pc.getId(), 7949));
			}
			pc.getInventory().removeItem(l1iteminstance, 1);
		}
		break;
		case 410060: {// 운세쪽지d
			int[] allBuffSkill = { L1SkillId.FEATHER_BUFF_D };
			L1SkillUse l1skilluse = new L1SkillUse();
			if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_B))
				pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_B);
			if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_C))
				pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_C);
			if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_A))
				pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_A);
			for (int i = 0; i < allBuffSkill.length; i++) {
				l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_SPELLSC);
				pc.broadcastPacket(new S_SkillSound(pc.getId(), 7950));
			}
			pc.getInventory().removeItem(l1iteminstance, 1);
		}
		break;
		
		
		
		case 40304:{ // 마프르 유산
			int count = _random.nextInt(6) + 5;
			pc.getInventory().storeItem(40318, count); // 마돌
			pc.sendPackets(new S_SystemMessage("마력의 돌 (" + count + ") 획득"), true);
			pc.getInventory().removeItem(l1iteminstance, 1);
		}
		break;
		case 40305:{ // 파아그리오의 유산
			int count = _random.nextInt(6) + 5;
			pc.getInventory().storeItem(40320, count); // 흑마석
			pc.sendPackets(new S_SystemMessage("흑마석 (" + count + ") 획득"), true);
			pc.getInventory().removeItem(l1iteminstance, 1);
		}
		break;
		case 40306: { // 에바의 유산
			int count = _random.nextInt(6) + 5;
			pc.getInventory().storeItem(40319, count); // 정령옥
			pc.sendPackets(new S_SystemMessage("정령옥 (" + count + ") 획득"), true);
			pc.getInventory().removeItem(l1iteminstance, 1);
		}
			break;
		case 40307: { // 사이하의 유산
			int count = _random.nextInt(20) + 1;
			pc.getInventory().storeItem(40318, count); // 마돌
			pc.sendPackets(new S_SystemMessage("마력의 돌 (" + count + ") 획득"), true);
			count = _random.nextInt(30) + 1;
			pc.getInventory().storeItem(40319, count); // 정령옥
			pc.sendPackets(new S_SystemMessage("정령옥 (" + count + ") 획득"), true);
			count = _random.nextInt(20) + 1;
			pc.getInventory().storeItem(40320, count); // 흑마석
			pc.sendPackets(new S_SystemMessage("흑마석 (" + count + ") 획득"), true);
			count = _random.nextInt(5) + 1;
			pc.getInventory().storeItem(40031, count); // 악마의피
			pc.sendPackets(new S_SystemMessage("악마의 피 (" + count + ") 획득"), true);
			pc.getInventory().removeItem(l1iteminstance, 1);
		}
		break;
		case 410009: { // 성전환 물약
			int[] MALE_LIST = new int[] { 0, 61, 138, 734, 2786, 6658, 6671, 12490 };
			int[] FEMALE_LIST = new int[] { 1, 48, 37, 1186, 2796, 6661, 6650, 12494 };
			if (pc.get_sex() == 0) {
				pc.set_sex(1);
				pc.setClassId(FEMALE_LIST[pc.getType()]);
			} else {
				pc.set_sex(0);
				pc.setClassId(MALE_LIST[pc.getType()]);
			}
			pc.setTempCharGfx(pc.getClassId());
			pc.sendPackets(new S_ChangeShape(pc.getId(), pc.getClassId()));
			pc.broadcastPacket(new S_ChangeShape(pc.getId(), pc.getClassId()));
			pc.getInventory().removeItem(l1iteminstance, 1);
		}
		break;
		case 700021:// 봉인해제주문서 신청서
			if (!pc.isQuizValidated()) {
				pc.sendPackets(new S_ChatPacket(pc, "퀴즈 인증을 하지 않으셨습니다."));
				pc.sendPackets(new S_ChatPacket(pc, "먼저 [.퀴즈인증]으로 퀴즈 인증 후 다시 시도해주세요."));
				return;
			}
			if (pc.getInventory().checkItem(50021)) { //
				pc.sendPackets(new S_ChatPacket(pc, "이미 봉인해제 주문서를 갖고 있습니다."));
				return;
			}
			if (pc.getInventory().checkItem(700021, 1)) {
				pc.getInventory().consumeItem(700021, 1);
				pc.getInventory().storeItem(50021, 15);
			}
			break;
		case 30104: {// 코마의 축복 코인
			int[] allBuffSkill = { 50007 };
			L1SkillUse l1skilluse = new L1SkillUse();
			for (int i = 0; i < allBuffSkill.length; i++) {
				l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_SPELLSC);
			}
			pc.getInventory().removeItem(l1iteminstance, 1);
		}
		break;
		case 30124:// 포탄
			pc.sendPackets(new S_SystemMessage("공성전 시 투석기를 사용할 때 필요한 소모성 아이템"));
			
			break;
		case 800200:// 패키지 1차
			if (pc.getInventory().getSize() > 120) {
				pc.sendPackets(new S_ChatPacket(pc, "소지하고 있는 아이템이 너무 많습니다."));
				return;
			}
			if (pc.getInventory().getWeight100() > 82) { // 이부분을 수정하면 오류가 발생한다.
				pc.sendPackets(new S_ChatPacket(pc, "소지품이 너무 무거워서 사용 할 수 없습니다."));
				return;
			}
			if (pc.getInventory().checkItem(800200, 1)) { // 체크되는 아이템과 수량
				pc.getInventory().removeItem(l1iteminstance, 1);
				createNewItem2(pc, 800001, 1, 0); // 무기 코인
				createNewItem2(pc, 800002, 1, 0); // 투구 코인
				createNewItem2(pc, 800003, 1, 0); // 티셔츠 코인
				createNewItem2(pc, 800004, 1, 0); // 망토 코인
				createNewItem2(pc, 800005, 1, 0); // 갑옷 코인
				createNewItem2(pc, 800007, 1, 0); // 장갑 코인
				createNewItem2(pc, 800008, 1, 0); // 부츠 코인
				createNewItem2(pc, 800009, 1, 0); // 목걸이 코인
				createNewItem2(pc, 800010, 1, 0); // 귀걸이 코인
				createNewItem2(pc, 800011, 1, 0); // 반지 코인
				createNewItem2(pc, 800011, 1, 0); // 반지 코인
				createNewItem2(pc, 800012, 1, 0); // 벨트 코인
				createNewItem2(pc, 800013, 1, 0); // 인형 코인
				createNewItem2(pc, 800014, 1, 0); // 각반 코인	
				createNewItem2(pc, 40308, 50000000, 0); // 아데나
				createNewItem2(pc, 3000119, 1, 0); // 패키지 이동부적
				if (pc.isWarrior()) {
					createNewItem2(pc, 203006, 1, 9); // 태풍의 도끼
				}	
				if (!pc.isWarrior()) {
					createNewItem2(pc, 800006, 1, 0); // 방패 코인
				}
			}
			break;
		case 1000010:// 랜덤 무기상자
			int randomchance1 = _random.nextInt(17);
			int randomchance2 = _random.nextInt(14);
			int[] item1 = {1121, 47, 203006, 203018, 76, 1137, 203017, 1119, 1123, 202001,
						1120, 1136, 202003, 58, 54, 203025, 203023};
			int[] item2 = {212, 614, 604, 600, 57, 9, 11, 602, 74, 157, 205, 127, 1134, 603};
			if(_random.nextInt(100) + 1 <= 10){
				createNewItem2(pc, item1[randomchance1], 1, 5); // 랜덤무기 2천만등급
			}else{
				createNewItem2(pc, item2[randomchance2], 1, 5); // 랜덤무기 1천만등급
			}
			pc.getInventory().removeItem(l1iteminstance, 1);
			break;
		case 430507:// 4~6단계 법서
			if (pc.getInventory().getSize() > 120) {
				pc.sendPackets(new S_ChatPacket(pc, "소지하고 있는 아이템이 너무 많습니다."));
				return;
			}
			if (pc.getInventory().getWeight100() > 82) { // 이부분을 수정하면 오류가 발생한다.
				pc.sendPackets(new S_ChatPacket(pc, "소지품이 너무 무거워서 사용 할 수 없습니다."));
				return;
			}
			for(int i = 40170; i < 40194; i++){
				createNewItem2(pc, i, 1, 0);
			}
			pc.getInventory().removeItem(l1iteminstance, 1);
			break;
		case 1000050:// 4~10단계 법서(디스, 데스힐, 미티어, 앱솔 세이프 제외)
			if (pc.getInventory().getSize() > 120) {
				pc.sendPackets(new S_ChatPacket(pc, "소지하고 있는 아이템이 너무 많습니다."));
				return;
			}
			if (pc.getInventory().getWeight100() > 82) { // 이부분을 수정하면 오류가 발생한다.
				pc.sendPackets(new S_ChatPacket(pc, "소지품이 너무 무거워서 사용 할 수 없습니다."));
				return;
			}
			for(int i = 40170; i < 40226; i++){
				if(i == 40212 || i == 40219 || i == 40222 || i == 40223){
					i++;
				}
				createNewItem2(pc, i, 1, 0);
			}
			pc.getInventory().removeItem(l1iteminstance, 1);
			break;
		case 500219:// 발록봉헌주문서(우호도)
			if (pc.getKarma() <= 10000000) {
				pc.addKarma((int) (+15000 * Config.RATE_KARMA));
				pc.sendPackets(new S_Karma(pc));
				pc.sendPackets(new S_SystemMessage(pc.getName() + "님의 우호도가 향상되었습니다."));
				// pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,"Ctrl+A 누르고 4번째창에서 현재 우호도 상태를 확인할수 있습니다."));
				pc.getInventory().removeItem(l1iteminstance, 1);
			} else
				pc.sendPackets(new S_ServerMessage(79));
			break;
		case 500218:// 야히봉헌주문서(우호도)
			if (pc.getKarma() >= -10000000) {
				pc.addKarma((int) (-15000 * Config.RATE_KARMA));
				pc.sendPackets(new S_Karma(pc));
				pc.sendPackets(new S_SystemMessage(pc.getName() + "님의 우호도가 향상되었습니다."));
				// pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,"Ctrl+A 누르고 4번째창에서 현재 우호도 상태를 확인할수 있습니다."));

				pc.getInventory().removeItem(l1iteminstance, 1);
			} else
				pc.sendPackets(new S_ServerMessage(79));
			break;
		case 1000011:// 기란감옥 충전 코인
			if (pc.getGirandungeonTime() > 115) {
				pc.getInventory().removeItem(l1iteminstance, 1);
				pc.setGirandungeonTime(0);
				pc.save();
				pc.sendPackets(new S_ChatPacket(pc, "기감, 지배자의결계 던전 체류 시간이 2시간 충전 되었습니다."));
			} else {
				pc.sendPackets(new S_ChatPacket(pc, "기감, 지배자의결계 체류 시간이 아직 남아 있습니다."));
			}
			break;
		case 410062:// 상아탑 던전 충전 코인
			if (pc.getOrendungeonTime() > 55) {
				pc.getInventory().removeItem(l1iteminstance, 1);
				pc.setOrendungeonTime(0);
				pc.save();
				pc.sendPackets(new S_ChatPacket(pc, "상아탑 던전 체류 시간이 1시간 충전 되었습니다."));
			} else {
				pc.sendPackets(new S_ChatPacket(pc, "상아탑 던전 체류 시간이 아직 남아 있습니다."));
			}
			break;
		case 1000012:// 몽섬충전코인
			if (pc.getSomeTime() > 25) {
				pc.getInventory().removeItem(l1iteminstance, 1);
				pc.setSomeTime(0);
				pc.save();
				pc.sendPackets(new S_ChatPacket(pc, "몽환의 섬 체류 시간이 충전 되었습니다."));
			} else {
				pc.sendPackets(new S_ChatPacket(pc, "몽환의 섬 체류 시간이 아직 남아 있습니다"));
			}
			break;
		case 1000013:// 정무충전
			if (pc.getSoulTime() > 25) {
				pc.getInventory().removeItem(l1iteminstance, 1);
				pc.setSoulTime(0);
				pc.save();
				pc.sendPackets(new S_ChatPacket(pc, "무덤 체류 시간이 30분 충전 되었습니다."));
			} else {
				pc.sendPackets(new S_ChatPacket(pc, "무덤 체류 시간이 아직 남아 있습니다"));
			}
			break;

		case 410135:// 라스타바드 던전 충전 코인
			if (pc.getRadungeonTime() > 115) {
				pc.getInventory().removeItem(l1iteminstance, 1);
				pc.setRadungeonTime(0);
				pc.save();
				pc.sendPackets(new S_ChatPacket(pc, "라스타바드 체류 시간이 2시간 충전 되었습니다."));
			} else {
				pc.sendPackets(new S_ChatPacket(pc, "라스타바드 체류 시간이 아직 남아 있습니다"));
			}
			break;
		case 500216:// 용의 던전 충전 코인
			if (pc.getDrageonTime() > 115) {
				pc.getInventory().removeItem(l1iteminstance, 1);
				pc.setDrageonTime(0);
				pc.save();
				pc.sendPackets(new S_ChatPacket(pc, "용의 던전 체류 시간 2시간이 충전 되었습니다."));
			} else {
				pc.sendPackets(new S_ChatPacket(pc, "용의 던전 체류 시간이 아직 남아 있습니다."));
			}
			break;
		case 40867: {// 큐어포이즌 빈주문서
			int[] allBuffSkill = { 9 };
			L1SkillUse l1skilluse = null;
			l1skilluse = new L1SkillUse();
			pc.setBuffnoch(1);
			for (int i = 0; i < allBuffSkill.length; i++) {
				l1skilluse
						.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_SPELLSC);
			}
			pc.setBuffnoch(0);
			pc.getInventory().removeItem(l1iteminstance, 1);
		}
			break;
		case 40877: {// 익스트라힐
			int[] allBuffSkill = { 19 };
			L1SkillUse l1skilluse = null;
			l1skilluse = new L1SkillUse();
			pc.setBuffnoch(1);
			for (int i = 0; i < allBuffSkill.length; i++) {
				l1skilluse
						.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_SPELLSC);
			}
			pc.setBuffnoch(0);
			pc.getInventory().removeItem(l1iteminstance, 1);
		}
			break;
		case 40893: {// 그레이트힐
			int[] allBuffSkill = { 35 };
			L1SkillUse l1skilluse = null;
			l1skilluse = new L1SkillUse();
			pc.setBuffnoch(1);
			for (int i = 0; i < allBuffSkill.length; i++) {
				l1skilluse
						.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_SPELLSC);
			}
			pc.setBuffnoch(0);
			pc.getInventory().removeItem(l1iteminstance, 1);
		}
			break;

		/** 오만의탑 혼돈부적,변이된 부적 **/
		case 30106: {// 숨겨진 계곡 마을 귀환 부적
			if (pc.get_DuelLine() != 0) {
				pc.sendPackets(new S_SystemMessage("배틀존지역에서 사용할 수 없습니다."));
				return;
			}
			if(pc.isDead())
				return;
			if(pc.getCurrentHp()<1)
				return;
			
			int[] loc = L1TownLocation.getGetBackLoc(L1TownLocation.TOWNID_HIDDEN_VALLEY);
			new L1Teleport().teleport(pc, loc[0], loc[1], (short) loc[2], pc.getHeading(), true);
		}
		break;
		case 3000108:// 발록진영4층
			if (pc.getnewdodungeonTime() >= 59) {
				pc.sendPackets(new S_SystemMessage("\\aA경고: \\aG[발록진영]\\aA 던전 시간이 만료되었습니다."));
				return;
			}
			if (pc.getMap().isEscapable() || pc.isGm()) {
				int rx = _random.nextInt(2);
				int ux = 32901 + rx;
				int uy = 32765 + rx; // 상아탑
				if (itemId == 3000108) {
					new L1Teleport().teleport(pc, ux, uy, (short) 280, pc.getHeading(), true);
				}
				pc.setSkillEffect(L1SkillId.ABSOLUTE_BARRIER, 3000);
				pc.getInventory().removeItem(l1iteminstance, 1);
			} else {
				pc.sendPackets(new S_ServerMessage(647));
			}
			cancelAbsoluteBarrier(pc); // 아브소르트바리아의 해제
			break;
		case 3000120:// 글루디오 던전1층
			if (pc.getGirandungeonTime() >= 180) {
				pc.sendPackets(new S_SystemMessage("\\aA경고: \\aG[글루디오]\\aA 던전 시간이 만료되었습니다."));
				return;
			 }
			if (pc.getMap().isEscapable() || pc.isGm()) {
				int rx = _random.nextInt(2);
				int ux = 32811 + rx;
				int uy = 32726 + rx;
				if (itemId == 3000120) {
					new L1Teleport().teleport(pc, ux, uy, (short) 807, pc.getHeading(), true);
				}
				pc.setSkillEffect(L1SkillId.ABSOLUTE_BARRIER, 3000);
				pc.getInventory().removeItem(l1iteminstance, 1);
			} else {
				pc.sendPackets(new S_ServerMessage(647));
			}
			cancelAbsoluteBarrier(pc); // 아브소르트바리아의 해제
			break;
		case 42069:// 말던1층
			if (pc.getislandTime() >= 59) {
				pc.sendPackets(new S_SystemMessage("\\aA경고: \\aG[말하는섬]\\aA 던전 시간이 만료되었습니다."));
				return;
			}
			if (pc.getMap().isEscapable() || pc.isGm()) {
				int rx = _random.nextInt(2);
				int ux = 32668 + rx;
				int uy = 32804 + rx;
				if (itemId == 42069) {
					new L1Teleport().teleport(pc, ux, uy, (short) 1, pc.getHeading(), true);
				}
				pc.setSkillEffect(L1SkillId.ABSOLUTE_BARRIER, 3000);
				pc.getInventory().removeItem(l1iteminstance, 1);
			} else {
				pc.sendPackets(new S_ServerMessage(647));
			}
			cancelAbsoluteBarrier(pc); // 아브소르트바리아의 해제
			break;
		case 301067: {// 벚꽃마을
			if (pc.get_DuelLine() != 0) {
				pc.sendPackets(new S_SystemMessage("배틀존지역에서 사용할 수 없습니다."));
				return;
			}
			if(pc.isDead())
				return;
			if(pc.getCurrentHp()<1)
				return;
			
			if (pc.getMap().isEscapable() || pc.isGm()) {
				new L1Teleport().teleport(pc, 32756, 32867, (short) 610, pc.getHeading(), true);
			}
		}
		break;  
		case 600232: {// 잊섬탈출주문서
			if (pc.get_DuelLine() != 0) {
				pc.sendPackets(new S_SystemMessage("배틀존지역에서 사용할 수 없습니다."));
				return;
			}
			if(pc.isDead())
				return;
			if(pc.getCurrentHp()<1)
				return;
			
			if (!(pc.getMapId() >= 1700 && pc.getMapId() <= 1711)) {
				pc.sendPackets(new S_SystemMessage("잊혀진섬에서만 사용가능합니다."));
				return;
			}
			new L1Teleport().teleport(pc, 33452, 32788, (short) 4, pc.getHeading(), true);
		}
			break;
		
		
		case 202099: {// 클라우디아 마을
				if (pc.get_DuelLine() != 0) {
				pc.sendPackets(new S_SystemMessage("배틀존지역에서 사용할 수 없습니다."));
				return;
			}
				if(pc.isDead())
					return;
				if(pc.getCurrentHp()<1)
					return;
				
			if (pc.getMap().isEscapable() || pc.isGm()) {
				int[] loc = L1TownLocation.getGetBackLoc(L1TownLocation.TOWNID_claudia);
				new L1Teleport().teleport(pc, loc[0], loc[1], (short) loc[2], pc.getHeading(), true);
			}
		}
		break;
		case 40081: // 기란마을주문서
		case 301066:
			if (pc.get_DuelLine() != 0) {
				pc.sendPackets(new S_SystemMessage("배틀존지역에서 사용할 수 없습니다."));
				return;
			}
			if(pc.isDead())
				return;
			if(pc.getCurrentHp()<1)
				return;
			
			if (pc.getMap().isEscapable() || pc.isGm()) {
				int[] loc = L1TownLocation.getGetBackLoc(L1TownLocation.TOWNID_GIRAN);
				new L1Teleport().teleport(pc, loc[0], loc[1], (short) loc[2], pc.getHeading(), true);
				if (itemId == 40081) {
					pc.getInventory().removeItem(l1iteminstance, 1);
				}

			} else {
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
				pc.sendPackets(new S_ServerMessage(647));
			}
			cancelAbsoluteBarrier(pc);
			break;
		case 40117: // 은기사마을
			if (pc.get_DuelLine() != 0) {
				pc.sendPackets(new S_SystemMessage("배틀존지역에서 사용할 수 없습니다."));
				return;
			}
			if(pc.isDead())
				return;
			if(pc.getCurrentHp()<1)
				return;
			
			if (pc.getMap().isEscapable() || pc.isGm()) {
				int[] loc = L1TownLocation.getGetBackLoc(L1TownLocation.TOWNID_SILVER_KNIGHT_TOWN);
				new L1Teleport().teleport(pc, loc[0], loc[1], (short) loc[2], pc.getHeading(), true);
				pc.getInventory().removeItem(l1iteminstance, 1);
				cancelAbsoluteBarrier(pc);
			} else {
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
				pc.sendPackets(new S_ServerMessage(647));
			}
			break;

		case 41260:// 장착
			for (L1Object object : L1World.getInstance().getVisibleObjects(pc, 3)) {
				if (object instanceof L1EffectInstance) {
					if (((L1NpcInstance) object).getNpcTemplate().get_npcId() == 81170) {
						// 벌써 주위에 모닥불이 있습니다.
						pc.sendPackets(new S_ServerMessage(1162));
						return;
					}
				}
			}
			int[] loc1 = new int[2];
			loc1 = pc.getFrontLoc();
			L1EffectSpawn.getInstance().spawnEffect(81170, 300000, loc1[0], loc1[1], pc.getMapId());
			pc.getInventory().removeItem(l1iteminstance, 1);
			break;
		case 240100:// 저주해진 텔레포트 스크롤(오리지날 아이템)
			if (pc.get_DuelLine() != 0) {
				pc.sendPackets(new S_SystemMessage("배틀존지역에서 사용할 수 없습니다."));
				return;
			}
			if(pc.isDead())
				return;
			if(pc.getCurrentHp()<1)
				return;
			
			new L1Teleport().teleport(pc, pc.getX(), pc.getY(), pc.getMapId(), pc.getHeading(), true);
			pc.getInventory().removeItem(l1iteminstance, 1);
			pc.cancelAbsoluteBarrier(); // 아브소르트바리아의 해제
			break;
		case 748: // 잡화상점 이동부적
			new L1Teleport().teleport(pc, 33453, 32820, (short) 4, pc.getHeading(), true);
			break;
		case 749: // 장비상점 이동부적
			new L1Teleport().teleport(pc, 33435, 32754, (short) 4, pc.getHeading(), true);
			break;
			
		case 500076:{//근거리버프물약
			int[] allBuffSkill = { 14, 26, 42, 48,  168, 160, 206, 211, 216, 148, 158 };
			//디크리즈,덱,힘,블레스웨폰,어밴,어스,아쿠아,컨센트,페이션스,인사이트,파이어웨폰,아바타,네이쳐스
			pc.setBuffnoch(1);
			L1SkillUse l1skilluse = null;
			l1skilluse = new L1SkillUse();
			for (int i = 0; i < allBuffSkill.length; i++) {
				l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
				pc.sendPackets(new S_SkillSound(pc.getId(), 830));
				pc.curePoison();
			}
			pc.setBuffnoch(0);
			pc.getInventory().removeItem(l1iteminstance, 1);
		}
		break;
		case 500077:{//원거리버프물약
			int[] allBuffSkill = { 14,26,42,43,48,151,160,206,211,216,149 };
			//디크리즈,덱,힘,블레스웨폰,어밴,어스,아쿠아,컨센트,페이션스,인사이트,스톰샷,아바타,네이쳐스
			pc.setBuffnoch(1);
			L1SkillUse l1skilluse = null;
			l1skilluse = new L1SkillUse();
			for (int i = 0; i < allBuffSkill.length; i++) {
				l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
				pc.sendPackets(new S_SkillSound(pc.getId(), 830));
				pc.curePoison();
			}
			pc.setBuffnoch(0);
			pc.getInventory().removeItem(l1iteminstance, 1);
		}
		break;

		
		/** 패키지이동주문서 **/ 
		case 800100:
			pc.setCashStep(1);
			new L1Teleport().teleport(pc, 32672, 32793, (short) 514, 5, true);
			pc.getInventory().removeItem(l1iteminstance, 1);
			break;
		case 800101:
			pc.setCashStep(2);
			new L1Teleport().teleport(pc, 32672, 32793, (short) 515, 5, true);
			pc.getInventory().removeItem(l1iteminstance, 1);
			break;
		case 800102:
			pc.setCashStep(3);
			new L1Teleport().teleport(pc, 32672, 32793, (short) 516, 5, true);
			pc.getInventory().removeItem(l1iteminstance, 1);
			break;
		case 3000119: //패키지상점 이동주문서
			if (pc.getZoneType() != 1 || pc.getMapId() != 4) {
				pc.sendPackets(new S_SystemMessage("마을에서만 사용할 수 있습니다."));
				return;
			}
			else{
				pc.setCashStep(1);
				new L1Teleport().teleport(pc, 32672, 32785, (short) 514, 5, true);
			}
			break;
		case 40124:case 30086:// 혈맹 귀환 스크롤
			if (pc.get_DuelLine() != 0) {
				pc.sendPackets(new S_SystemMessage("배틀존지역에서 사용할 수 없습니다."));
				return;
			}
			if(pc.isDead())
				return;
			if(pc.getCurrentHp()<1)
				return;
			
			if (pc.getMap().isEscapable() || pc.isGm()) {
				int castle_id = 0;
				int house_id = 0;
				if (pc.getClanid() != 0) { // 크란 소속
					L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
					if (clan != null) {
						castle_id = clan.getCastleId();
						house_id = clan.getHouseId();
					}
				}
				if (castle_id != 0) { // 성주 크란원
					if (pc.getMap().isEscapable() || pc.isGm()) {
						int[] loc = new int[3];
						loc = L1CastleLocation.getCastleLoc(castle_id);
						int locx = loc[0];
						int locy = loc[1];
						short mapid = (short) (loc[2]);
						new L1Teleport().teleport(pc, locx, locy, mapid, pc.getHeading(), true);
						pc.getInventory().removeItem(l1iteminstance, 1);
					} else {
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
						pc.sendPackets(new S_ServerMessage(647));
					}
				} else if (house_id != 0) { // 아지트 소유 크란원
					if (pc.getMap().isEscapable() || pc.isGm()) {
						int[] loc = new int[3];
						loc = L1HouseLocation.getHouseLoc(house_id);
						int locx = loc[0];
						int locy = loc[1];
						short mapid = (short) (loc[2]);
						new L1Teleport().teleport(pc, locx, locy, mapid, 5, true);
						pc.getInventory().removeItem(l1iteminstance, 1);
					} else {
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
						pc.sendPackets(new S_ServerMessage(647));
					}
				} else {
					if (pc.getHomeTownId() > 0) {
						int[] loc = L1TownLocation.getGetBackLoc(pc.getHomeTownId());
						int locx = loc[0];
						int locy = loc[1];
						short mapid = (short) (loc[2]);
						new L1Teleport().teleport(pc, locx, locy, mapid, 5, true);
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
						pc.getInventory().removeItem(l1iteminstance, 1);
					} else {
						int[] loc = Getback.GetBack_Location(pc, true);
						new L1Teleport().teleport(pc, loc[0], loc[1], (short) loc[2], 5, true);
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
						pc.getInventory().removeItem(l1iteminstance, 1);
					}
				}
			} else {
				pc.sendPackets(new S_ServerMessage(647));
			}
			cancelAbsoluteBarrier(pc); // 아브소르트바리아의 해제
			break;
			
		case 40312:// 여관열쇠
			if (pc.isstop()){
				return;
			}
			if (pc.isParalyzed() || pc.isSleeped() || pc.isDead()) {
				return;
			}
			if (pc.getMap().isEscapable() || pc.isGm()) {
				Thread.sleep(500);
				new L1Teleport().teleport(pc, 32742, 32803, (short) 18432, 5, false);
			}
			break;
		case 5988: //어비스포인트물약
			pc.addAbysspoint(100);
			pc.sendPackets(new S_SystemMessage("어비스포인트 100점 획득"));	
			pc.getInventory().removeItem(l1iteminstance, 1);
			break;
		case 5989: //혈맹경험치 물약
			if(pc.getClanid() != 0){
				pc.getClan().addClanExp(100);
				pc.getInventory().removeItem(l1iteminstance, 1);
				ClanTable.getInstance().updateClan(pc.getClan()); // 죽인사람 혈맹업데이트
			} else {
				pc.sendPackets(new S_ServerMessage(4055));	
			}
			break;
		case 9990:
			어비스스턴스킬(pc);
			break;			
		case 9989:
			어비스데미지스킬(pc);
			break;
		case 9995:
			L1SkillUse l1skilluse = new L1SkillUse();
			l1skilluse.handleCommands(pc, L1SkillId.나루토감사캔디, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
			pc.getInventory().removeItem(l1iteminstance, 1);
			break;
		case 60256:
			if(pc.getInventory().checkItem(60255)){
				pc.sendPackets(new S_ServerMessage(939));
				pc.sendPackets(new S_SystemMessage("드래곤의 자수정 보유."));
				return;
			}
			pc.getInventory().storeItem(60255, 1);
			pc.getInventory().removeItem(l1iteminstance, 1);
			break;
		case 89136:
			if (pc.get_SpecialSize() == 40) {
				pc.sendPackets(new S_ServerMessage(1622));
				return;
			}
			if (pc.get_SpecialSize() == 0) {
				pc.set_SpecialSize(20);
				pc.getInventory().consumeItem(89136, 1);
				pc.sendPackets(new S_ServerMessage(1624, "20"));
			} else if (pc.get_SpecialSize() == 20) {
				pc.set_SpecialSize(40);
				pc.getInventory().consumeItem(89136, 1);
				pc.sendPackets(new S_ServerMessage(1624, "40"));
			}
			break;
		case 51093:case 51094:case 51095:case 51096:case 51097:case 51098:case 51099:case 51100:
			//클래스 변경물약 
			if (pc.getClanid() != 0) {
				pc.sendPackets(new S_ChatPacket(pc,"혈맹을 먼저 탈퇴하여 주시기 바랍니다."));
				return;
			} else if (itemId == 51093 && pc.getType() == 0) { // 자네 군주?
				pc.sendPackets(new S_ChatPacket(pc,"당신은 이미 군주 클래스 입니다."));
				return;
			} else if (itemId == 51094 && pc.getType() == 1) { // 자네 기사?
				pc.sendPackets(new S_ChatPacket(pc,"당신은 이미 기사 클래스 입니다."));
				return;
			} else if (itemId == 51095 && pc.getType() == 2) { // 자네 요정?
				pc.sendPackets(new S_ChatPacket(pc,"당신은 이미 요정 클래스 입니다."));
				return;
			} else if (itemId == 51096 && pc.getType() == 3) { // 자네 마법사?
				pc.sendPackets(new S_ChatPacket(pc,"당신은 이미 마법사 클래스 입니다."));
				return;
			} else if (itemId == 51097 && pc.getType() == 4) { // 자네 다크엘프?
				pc.sendPackets(new S_ChatPacket(pc,"당신은 이미 다크엘프 클래스 입니다."));
				return;
			} else if (itemId == 51098 && pc.getType() == 5) { // 자네 용기사?
				pc.sendPackets(new S_ChatPacket(pc,"당신은 이미 용기사 클래스 입니다."));
				return;
			} else if (itemId == 51099 && pc.getType() == 6) { // 자네 환술사?
				pc.sendPackets(new S_ChatPacket(pc,"당신은 이미 환술사 클래스 입니다."));
				return;
			} else if (itemId == 51100 && pc.getType() == 7) { // 자네 환술사?
				pc.sendPackets(new S_ChatPacket(pc,"당신은 이미 전사 클래스 입니다."));
				return;
			}
			int[] Mclass = new int[] { 0, 61, 138, 734, 2786, 6658, 6671, 12490 };
			int[] Wclass = new int[] { 1, 48, 37, 1186, 2796, 6661, 6650, 12494 };
			if (itemId == 51093 && pc.getType() != 0 && pc.get_sex() == 0) {
				pc.setType(0);
				pc.setClassId(Mclass[pc.getType()]);
			} else if (itemId == 51093 && pc.getType() != 0 && pc.get_sex() == 1) {//군주
				pc.setType(0);
				pc.setClassId(Wclass[pc.getType()]);
			} else if (itemId == 51094 && pc.getType() != 1 && pc.get_sex() == 0) { // 변경: 기사
				pc.setType(1);
				pc.setClassId(Mclass[pc.getType()]);
			} else if (itemId == 51094 && pc.getType() != 1 && pc.get_sex() == 1) {
				pc.setType(1);
				pc.setClassId(Wclass[pc.getType()]);
			} else if (itemId == 51095 && pc.getType() != 2 && pc.get_sex() == 0) { // 변경: 요정
				pc.setType(2);
				pc.setClassId(Mclass[pc.getType()]);
			} else if (itemId == 51095 && pc.getType() != 2 && pc.get_sex() == 1) {
				pc.setType(2);
				pc.setClassId(Wclass[pc.getType()]);
			} else if (itemId == 51096 && pc.getType() != 3 && pc.get_sex() == 0) { // 변경: 마법사
				pc.setType(3);
				pc.setClassId(Mclass[pc.getType()]);
			} else if (itemId == 51096 && pc.getType() != 3 && pc.get_sex() == 1) {
				pc.setType(3);
				pc.setClassId(Wclass[pc.getType()]);
			} else if (itemId == 51097 && pc.getType() != 4 && pc.get_sex() == 0) { // 변경: 다크엘프
				pc.setType(4);
				pc.setClassId(Mclass[pc.getType()]);
			} else if (itemId == 51097 && pc.getType() != 4 && pc.get_sex() == 1) {
				pc.setType(4);
				pc.setClassId(Wclass[pc.getType()]);
			} else if (itemId == 51098 && pc.getType() != 5 && pc.get_sex() == 0) { // 변경: 용기사
				pc.setType(5);
				pc.setClassId(Mclass[pc.getType()]);
			} else if (itemId == 51098 && pc.getType() != 5 && pc.get_sex() == 1) {
				pc.setType(5);
				pc.setClassId(Wclass[pc.getType()]);
			} else if (itemId == 51099 && pc.getType() != 6 && pc.get_sex() == 0) { // 변경: 환술사
				pc.setType(6);
				pc.setClassId(Mclass[pc.getType()]);
			} else if (itemId == 51099 && pc.getType() != 6 && pc.get_sex() == 1) {
				pc.setType(6);
				pc.setClassId(Wclass[pc.getType()]);
			} else if (itemId == 51100 && pc.getType() != 7 && pc.get_sex() == 0) { // 변경: 전사
				pc.setType(7);
				pc.setClassId(Mclass[pc.getType()]);
			} else if (itemId == 51100 && pc.getType() != 7 && pc.get_sex() == 1) {
				pc.setType(7);
				pc.setClassId(Wclass[pc.getType()]);
			}
			if (pc.getWeapon() != null)
				pc.getInventory().setEquipped(pc.getWeapon(), false, false, false, false);
			pc.getInventory().takeoffEquip(945);
			pc.sendPackets(new S_CharVisualUpdate(pc));
			for (L1ItemInstance armor : pc.getInventory().getItems()) {
				for (int type = 0; type <= 12; type++) {
					if (armor != null) {
						pc.getInventory().setEquipped(armor, false, false, false, false);
					}
				}
			}
			pc.sendPackets(new S_DelSkill(255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
					255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255));
			deleteSpell(pc);
			pc.setTempCharGfx(pc.getClassId());
			pc.sendPackets(new S_ChangeShape(pc.getId(), pc.getClassId()));
			Broadcaster.broadcastPacket(pc, new S_ChangeShape(pc.getId(), pc.getClassId()));
			pc.getInventory().removeItem(l1iteminstance, 1);
			try {
				pc.save();
			} catch (Exception e) { }
			pc.sendPackets(new S_SystemMessage("클래스변경으로 자동접속종료됩니다."));
			new L1Teleport().teleport(pc, 32723 + _random.nextInt(10), 32851 + _random.nextInt(10), (short) 5166, 5, true);
			StatInitialize(pc);
			Thread.sleep(500);
			pc.sendPackets(new S_Disconnect());
			break;
		}
	}
	
	private void deleteSpell(L1PcInstance pc) {
		int player = pc.getId();
		Connection con = null;
		PreparedStatement pstm = null;
		try {

			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("DELETE FROM character_skills WHERE char_obj_id=?");
			pstm.setInt(1, player);
			pstm.execute();
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	
	
	public static void 어비스데미지스킬(L1PcInstance pc) {
		pc.sendPackets(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Attack));
		 Broadcaster.broadcastPacket(pc, new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Damage));
		 if (pc.getPeerage() < 14){
			 pc.sendPackets(new S_SystemMessage("5성장교 이상 사용가능한 아이템 입니다."));
			 return;
		 }
		 if (pc.hasSkillEffect(L1SkillId.ABYSS_LIGHTNING_TIME)){
			 int time = pc.getSkillEffectTimeSec(L1SkillId.ABYSS_LIGHTNING_TIME);
			 pc.sendPackets(new S_SystemMessage("스킬딜레이로 사용이 중지됩니다.[남은시간 : "+ time +"초]"));
			 return;
		 }
		 if (pc.getZoneType() == 1){
			 pc.sendPackets(new S_SystemMessage("마을에선 사용할 수 없습니다."));
			 return;
		 }
		 L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
		 int dmg = 0;
			for (L1PcInstance tg : L1World.getInstance(). getVisiblePlayer(pc)) {
				if (clan != null){ // 혈맹이 있다면
					if (tg.getClanid() > 0){
						if(tg.getClanid() == pc.getClanid()){ //같은 혈맹이라면
							break;
						}
					}
				}
				if (tg.isInParty()){// 같은 파티원이라면 데미지x
					if(tg.getParty().getLeader() == pc.getParty().getLeader()){ 
						break;
					}
				}
				if (tg.isDead()){
					break;
				}
				if (tg.getId() == pc.getId()){
					break;
				}
				tg.sendPackets(new S_SystemMessage("심연의 폭풍으로 데미지를 입었습니다."));
				pc.sendPackets(new S_SkillSound(tg.getId(), 10407));// /이건 자기한데 보이게
				Broadcaster.broadcastPacket(pc, new S_SkillSound(tg.getId(), 10407));// 이거는 다른 사람도 보게...
				pc.sendPackets(new S_DoActionGFX(tg.getId(), ActionCodes.ACTION_Damage));
				Broadcaster.broadcastPacket(pc, new S_DoActionGFX(tg.getId(), ActionCodes.ACTION_Damage));
				if (pc.getPeerage() == 18){
					dmg = Config.SUPREMECOMMANDER_DAMAGE + _random.nextInt(100);
				} else if (pc.getPeerage() == 17){
					dmg = Config.COMMANDER_DAMAGE + _random.nextInt(100);
				} else if (pc.getPeerage() == 16){
					dmg = Config.IMPERATOR_DAMAGE + _random.nextInt(100);
				} else if (pc.getPeerage() == 15){
					dmg = Config.GENERAL_DAMAGE + _random.nextInt(100);
				} else if (pc.getPeerage() == 14){
					dmg = Config.STAR_FIVE_DAMAGE + _random.nextInt(100);
				}
				tg.receiveDamage(tg, dmg);
				pc.setSkillEffect(L1SkillId.ABYSS_LIGHTNING_TIME, 150000);
			}
			for (L1Object obj : L1World.getInstance().getVisibleObjects(pc)) { 
				if (obj instanceof L1MonsterInstance) { 
					L1NpcInstance npc = (L1NpcInstance) obj;
					if(npc.isDead()){
						break;
					}
					pc.sendPackets(new S_SkillSound(obj.getId(), 10407));// /이건 자기한데 보이게
					Broadcaster.broadcastPacket(pc, new S_SkillSound(obj.getId(), 10407));// 이거는 다른 사람도 보게...
					pc.sendPackets(new S_DoActionGFX(obj.getId(), ActionCodes.ACTION_Damage));
					Broadcaster.broadcastPacket(pc, new S_DoActionGFX(obj.getId(), ActionCodes.ACTION_Damage));
					if (pc.getPeerage() == 18){
						dmg = Config.SUPREMECOMMANDER_DAMAGE + _random.nextInt(100);
					} else if (pc.getPeerage() == 17){
						dmg = Config.COMMANDER_DAMAGE + _random.nextInt(100);
					} else if (pc.getPeerage() == 16){
						dmg = Config.IMPERATOR_DAMAGE + _random.nextInt(100);
					} else if (pc.getPeerage() == 15){
						dmg = Config.GENERAL_DAMAGE + _random.nextInt(100);
					} else if (pc.getPeerage() == 14){
						dmg = Config.STAR_FIVE_DAMAGE + _random.nextInt(100);
					}
					npc.receiveDamage(npc, dmg);
					pc.setSkillEffect(L1SkillId.ABYSS_LIGHTNING_TIME, 150000);
				}
			}
	}
	
	public static void 어비스스턴스킬(L1PcInstance pc) {
		if (pc.getAbysspoint() < Config.STAR_FIVE || pc.isGm()) {
			if (pc.hasSkillEffect(L1SkillId.DELAY)) { // 딜레이
				pc.sendPackets(new S_SystemMessage("아직 광역 스턴을 사용할 수 없습니다."));
				return;
			}
			if (pc.isInvisble()) {
				pc.sendPackets(new S_SystemMessage("광역 스턴은 투명상태로 시전이 불가능합니다."));
				return;
			}
			if (pc.getMapId() == 800) {
				pc.sendPackets(new S_SystemMessage("광역 스턴은 시장에서 시전이 불가능합니다."));
				return;
			}
			if (pc.getZoneType() == 1) {
				pc.sendPackets(new S_SystemMessage("광역 스턴은 마을에서 시전이 불가능합니다."));
				return;
			}
			
			pc.sendPackets(new S_SystemMessage("광역 스턴을 시전 합니다."));
			pc.setSkillEffect(L1SkillId.DELAY, 300000);
			
			int actionId = ActionCodes.ACTION_SkillBuff;
			S_DoActionGFX gfx = new S_DoActionGFX(pc.getId(), actionId);
			pc.sendPackets(gfx);
			Broadcaster.broadcastPacket(pc, gfx);

			for (L1Object obj : L1World.getInstance().getVisibleObjects(pc, 10)) {
				Random random = new Random();
				int[] stunTimeArray = { 2000, 2500, 3000, 3500, 4000 };
				int rnd = random.nextInt(stunTimeArray.length);
				int probability = random.nextInt(100) + 1;
				if (probability < 50) {
					int _shockStunDuration = stunTimeArray[rnd];
					if (obj instanceof L1PcInstance) {
						L1PcInstance target = (L1PcInstance) obj;
						L1PinkName.onAction(target, pc);
						if ((pc.getClanid() > 0 && (pc.getClanid() == target.getClanid())) || target.isGm()) {
						} else {
							L1Character cha = (L1Character) obj;
							if (!cha.hasSkillEffect(SHOCK_STUN) && !cha.hasSkillEffect(EARTH_BIND) && !cha.hasSkillEffect(ICE_LANCE)) {
								L1EffectSpawn.getInstance().spawnEffect(81162, _shockStunDuration, target.getX(), target.getY(), target.getMapId());
								target.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, true));
								target.setSkillEffect(SHOCK_STUN, _shockStunDuration);
								target.sendPackets(new S_SkillSound(target.getId(), 4434)); // 스턴
								Broadcaster.broadcastPacket(target, new S_SkillSound(target.getId(), 4434));
							}
						}
					} else if (obj instanceof L1MonsterInstance || obj instanceof L1SummonInstance
							|| obj instanceof L1PetInstance) {
						L1NpcInstance targetnpc = (L1NpcInstance) obj;
						L1EffectSpawn.getInstance().spawnEffect(81162, _shockStunDuration, targetnpc.getX(), targetnpc.getY(),
								targetnpc.getMapId());
						targetnpc.setParalyzed(true);
						targetnpc.setSkillEffect(SHOCK_STUN, _shockStunDuration);
						Broadcaster.broadcastPacket(targetnpc, new S_SkillSound(obj.getId(), 4434));
					}
				}
			}
		} else {
			pc.sendPackets(new S_SystemMessage("해당 기술은 5성장교이상만 시전이 가능합니다."));
		}
		System.currentTimeMillis();
		return;
	}
	
	private void StatInitialize(L1PcInstance pc) {
		L1SkillUse l1skilluse = new L1SkillUse();
		l1skilluse.handleCommands(pc, L1SkillId.CANCELLATION, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_LOGIN);

		if (pc.getWeapon() != null) {
			pc.getInventory().setEquipped(pc.getWeapon(), false, false, false, false);
		}

		pc.sendPackets(new S_CharVisualUpdate(pc));
		pc.sendPackets(new S_OwnCharStatus2(pc));

		for (L1ItemInstance armor : pc.getInventory().getItems()) {
			for (int type = 0; type <= 12; type++) {
				if (armor != null) {
					pc.getInventory().setEquipped(armor, false, false, false, false);
				}
			}
		}
		pc.setReturnStat(pc.getExp());
		pc.sendPackets(new S_SPMR(pc));
		pc.sendPackets(new S_OwnCharAttrDef(pc));
		pc.sendPackets(new S_OwnCharStatus2(pc));
		pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.START));
		try {
			pc.save();
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}
	private boolean createNewItem2(L1PcInstance pc, int item_id, int count, int EnchantLevel) {
		L1ItemInstance item = ItemTable.getInstance().createItem(item_id);
		if (item != null) {
			item.setCount(count);
			item.setEnchantLevel(EnchantLevel);
			item.setIdentified(true);
			if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
				pc.getInventory().storeItem(item);
			} else { // 가질 수 없는 경우는 지면에 떨어뜨리는 처리의 캔슬은 하지 않는다(부정 방지)
				L1World.getInstance().getInventory(pc.getX(), pc.getY(), pc.getMapId()).storeItem(item);
			}
			pc.sendPackets(new S_ServerMessage(403, item.getLogName())); // %0를
																			// 손에
																			// 넣었습니다.
			return true;
		} else {
			return false;
		}
	}
	private void cancelAbsoluteBarrier(L1PcInstance pc) { // 아브소르트바리아의 해제
		if (pc.hasSkillEffect(ABSOLUTE_BARRIER)) {
			pc.killSkillEffectTimer(ABSOLUTE_BARRIER);
			//pc.startMpRegeneration();
			pc.startMpRegenerationByDoll();
		}
	}

	@Override
	public String getType() {
		return C_ITEM_USE2;
	}
}