package l1j.server.server.model.item.function;

import static l1j.server.server.model.skill.L1SkillId.BONE_BREAK;
import static l1j.server.server.model.skill.L1SkillId.EARTH_BIND;
import static l1j.server.server.model.skill.L1SkillId.ICE_LANCE;
import static l1j.server.server.model.skill.L1SkillId.SHOCK_STUN;
import static l1j.server.server.model.skill.L1SkillId.THUNDER_GRAB;

import java.util.Random;

import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;



public class TelBook {
	
	private static Random _random = new Random(System.nanoTime());

	
	public static void clickItem(L1PcInstance pc, int itemId, int BookTel, L1ItemInstance l1iteminstance) {
		//
		if (pc.get_DuelLine() != 0) {
			pc.sendPackets(new S_SystemMessage("배틀존에서 사용할 수 없습니다."));
			return;
		}
		if (pc.isParalyzed() || pc.isSleeped() || pc.isDead()) {
			return;
		}
		if (!pc.getMap().isEscapable()) {
			pc.sendPackets(new S_SystemMessage("주위의 알수없는 마력에의해 텔레포트를 할 수 없습니다."));
			return;
		}
		if ((pc.hasSkillEffect(SHOCK_STUN)) || (pc.hasSkillEffect(ICE_LANCE)) || (pc.hasSkillEffect(BONE_BREAK))
				|| (pc.hasSkillEffect(THUNDER_GRAB)) || (pc.hasSkillEffect(EARTH_BIND))) {
			return;
		}
		
		if (itemId == 560025) {
			try {
				final int[][] 마을기억책 = { 
						{ 34060, 32281, 4 }, // 오렌
						{ 33079, 33390, 4 }, // 은기사
						{ 32750, 32439, 4 }, // 오크숲
						{ 32612, 33188, 4 }, // 윈다우드
						{ 33720, 32492, 4 }, // 웰던
						{ 32872, 32912, 304 }, // 침묵의 동굴
						{ 32612, 32781, 4 }, // 글루디오
						{ 33067, 32803, 4 }, // 켄트
						{ 33933, 33358, 4 }, // 아덴
						{ 33601, 33232, 4 }, // 하이네
						{ 32574, 32942, 0 }, // 말하는 섬
						{ 33430, 32815, 4 }, }; // 기란
				int[] a = 마을기억책[BookTel];
				if (a != null) {
					new L1Teleport().teleport(pc, a[0], a[1], (short) a[2], pc.getHeading(), true);
					pc.getInventory().removeItem(l1iteminstance, 1);
				}
			} catch (Exception e) {}
		} else if (itemId == 560027) {
			try {
				final int[][] 던전기억 = { 
						{ 32791, 32800, 101 }, // 오만1
						{ 32764, 32842, 77 }, // 오렌상탑3
						{ 32676, 32859, 59 }, // 에바왕국1
						{ 34267, 32189, 4 }, // 그신
						{ 32760, 33461, 4 }, // 욕망
						{ 32841, 32695, 550 }, // 선박
				};
				int[] b = 던전기억[BookTel];
				if (b != null) {
					new L1Teleport().teleport(pc, b[0], b[1], (short) b[2], pc.getHeading(), true);
					pc.getInventory().removeItem(l1iteminstance, 1);
				}
			} catch (Exception e) {
				
			}

		} else if (itemId == 560028) {
			try {
				final int[][] 오만기억 = { 
						{ 32735, 32798, 101 }, // 오만1
						{ 32730, 32802, 102 }, // 오만2
						{ 32726, 32803, 103 }, // 오만3
						{ 32621, 32858, 104 }, // 오만4
						{ 32599, 32866, 105 }, // 오만5
						{ 32611, 32862, 106 }, // 오만6
						{ 32618, 32866, 107 }, // 오만7
						{ 32600, 32866, 108 }, // 오만8
						{ 32612, 32866, 109 }, // 오만9
						{ 32729, 32802, 110 }, // 오만10
						{ 32646, 32808, 111 }, 	// 오만정상 시작지점
						{ 32801, 32963, 111 },};// 오만정상 중간지점
				int[] c = 오만기억[BookTel];
				if (c != null) {
					new L1Teleport().teleport(pc, c[0], c[1], (short) c[2], pc.getHeading(), true);

				}
			} catch (Exception e) {}
			
		} else if (itemId == 560029) {
			try {
				final int[][] 조우기억 = { 
						 { 00000, 00000, 0 }, // 저 레벨 추천 사냥터
					      { 32680, 32862, 0 }, //말섬 북쪽섬
					      { 32477, 32857, 0 }, //말섬 던전 입구
					      { 32413, 32932, 0 }, //말섬 오크 망루 지대
					      { 32778, 32705, 4 }, //본토 죽음의 폐허
					      { 32850, 32943, 4 }, //본토 망자의 무덤
					      { 32812, 32725, 807 }, //글루디오던전1층
					      { 32761, 32839, 77 },  //상아탑4층입구  
					      { 32705, 33149, 0 }, //말섬 흑기사 전초지기
					      { 32580, 32301, 4 }, //본토 오크 부락
					      { 32903, 33232, 4 }, //본토 사막(에르자베)
					      { 32748, 33151, 4 }, //본토 사막(샌드웜) 
					      { 32805, 32724, 19 },//요숲 던전 1층
					      { 32800, 32754, 809 }, //글루디오 던전 3층
					      { 33430, 32821, 4 }, //기란 감옥 입구
					      { 32809, 32729, 25 }, //수련 던전 입구 ****
					      { 00000, 00000, 0 }, // 중 레벨 추천 사냥터
					      { 33782, 33405, 4 }, //본토 거울의 숲 
					      { 33789, 32988, 4 }, //본토 밀림지대
					      { 32746, 32852, 59 }, //에바 왕국 1층
					      { 34250, 33454, 4 }, //오만의 탑 입구
					      { 33231, 32540, 4 }, //본토 흑기사 출몰 지역
					      { 32767, 32796, 20 }, //요숲 던전 2층
					      { 32728, 32807, 61 }, //에바 왕국 3층
					      { 32809, 32810, 30 }, //용던 1층 입구
					      { 32809, 32767, 27 }, //수련던전 3층 입구 
				//	      { 34266, 32187, 4 }, //그림자 신전 입구  
				//	      { 32756, 33459, 4 }, //욕망의 동굴 입구
					      { 00000, 00000, 0 }, // 고 레벨 추천 사냥터
					      { 32707, 32818, 32 },//용의 던전 3층 
					      { 32427, 33500, 4 }, //하이네 잊혀진섬 배표소
					      { 33179, 33026, 4 }, //본토 암흑용의 상흔
					      { 34275, 32361, 4 }, //본토 얼음 설벽
					      { 34078, 32559, 4 }, //본토 엘모어 격전지 
					      { 33295, 32456, 4 }, //본토 용의 계곡 입구
					      { 33390, 32330, 4 }, //본토 용의 계곡 정상
					      { 33613, 32393, 4 }, //본토 화룡의 둥지 입구
			              { 33711, 32276, 4 }, //본토 화룡의 둥지 최상부
					      { 34116, 32940, 4 }, //본토 풍룡의 둥지 입구
					      { 34263, 32825, 4 },}; //본토 풍룡의 둥지 입구
				int[] c = 조우기억[BookTel];
				if (c != null) {
					new L1Teleport().teleport(pc, c[0], c[1], (short) c[2], pc.getHeading(), true);
					pc.getInventory().removeItem(l1iteminstance, 1);
				}
			} catch (Exception e) {}
		}
	}
	
}
