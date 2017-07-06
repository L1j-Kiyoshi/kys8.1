package l1j.server.server.serverpackets;

import java.util.StringTokenizer;

import l1j.server.Config;
import l1j.server.server.Opcodes;
import l1j.server.server.datatables.WeekQuestTable;
import l1j.server.server.model.Instance.L1NpcInstance;

public class S_ACTION_UI extends ServerBasePacket {
	
	private byte[] _byte = null;
	
	public static final int TAM = 0xc2;
	public static final int CRAFT_ITEM = 0x37; // 제작 아이템
	public static final int CRAFT_ITEMLIST = 0x39; // 제작 리스트
	public static final int CRAFT_OK = 0x3b; // 제작 완료
	public static final int CLAN_JOIN_MESSAGE = 0x43;
	public static final int TEST = 0xcc;
	public static final int TEST2 = 0x3A;
	public static final int SAFETYZONE = 0xcf; //세이프티존
	public static final int PCBANG_SET = 0x7e;
	public static final int CRAFT_GAUGEUI = 93;

	private static final String S_ACTION_UI = "S_ACTION_UI";
	

	/**
	 * 파티 표식 설정시 사용함.
	 */
	public S_ACTION_UI(byte[] flag) {
		writeC(Opcodes.S_EXTENDED_PROTOBUF);
		writeH(339);
		writeByte(flag);
		writeH(0);
	}
	
	public S_ACTION_UI(int type) {
		writeC(Opcodes.S_EXTENDED_PROTOBUF);
		writeH(type);
		switch (type) {
		case CRAFT_ITEM:
			writeH(0x08);
			writeC(0x08);
			writeC(0x03);
			break;
		case CRAFT_OK:
			writeH(0x08);
			break;	
		}
		writeH(0);
	}
	
	 /**
	  * @param PC방,세이프티존 설정 by 맞고
	  * @param isOpen on/off
	  **/
	public S_ACTION_UI(int code, boolean isOpen) {
		writeC(Opcodes.S_EXTENDED_PROTOBUF);
		writeC(code);
		switch (code) {
		case PCBANG_SET: {
			writeC(0x00);
			writeC(0x08);
			writeC(isOpen ? 1 : 0);
			writeC(0x10);
			writeC(0x01); //랭킹버튼활성화 패킷
			writeH(0);
			break;
			}
		case SAFETYZONE: {
			writeC(0x01);
			writeC(0x08);
			write7B(isOpen? 128 : 0);
			writeC(0x10);
			writeC(0x00);
			writeC(0x18);
			writeC(0x00);
			writeH(0);
			break;
		}
		case CRAFT_GAUGEUI:
			writeC(0x08);
			writeC(0x00);
			writeC(0x10);
			writeC(0);
		}
	}
	
	public S_ACTION_UI(L1NpcInstance npc) {
		writeC(Opcodes.S_EXTENDED_PROTOBUF);
		writeH(CRAFT_ITEMLIST);
		writeH(0x08);
		int craftlist[] = null;
		try{
		switch (npc.getNpcId()) { //69,70,71,72,73,74,75,76,77,78얇은판금 ~ 백금판금
/****************************************************************************************************/		
		/** 기란마을 - 란달 **/
		case 70028:  craftlist = new int[] { 264,265,266,267,268,269,270 };  break;
		/** 기란마을 - 허버트 **/
		case 70641: craftlist = new int[] { 199,200,201,570,571 };  break;
		/** 기란마을 - 헥터 **/
		case 70642:  craftlist = new int[] { 518,519,194,195,196,819,821,189,190,191,192,193,69,70,71,72,73,74,75,76,77,78 };  break;
		/** 기란마을 - 바무트 **/
		case 70690:  craftlist = new int[] { 2783,2784,2785,2786,2787,2862,232,233,234,255,256,257,258,259,260,261,262,263,820,481,482,483,484,485,486,487,488 };  break;
		/** 기란마을 - 액세서리 - 금속 - **/
		case 7210043:craftlist = new int[] { 2859, 2860, 2763, 2764, 2765, 2766, 2767, 718,719,720,721,722,723,724,725,2760, 2761, 2762 }; break;
		/** 기란마을 - 액세서리 - 보석 - **/
		case 7210044: craftlist = new int[] { 
				3456, 3457,3458,3459,3460,
				3461, 3462,3463,3464,3465,
				3466, 3467,3468,3469,3470,
				2775,2776,2777,2778,2779,2780,2781,2782,712,713,1729,1730,305,309,313,1734,1735,1736,1737}; break;
		
		

		/** 기란마을 - 액세서리 - 천 - **/
		case 7210045: craftlist = new int[] { 2863,702,703,704,705,708,709,714,715,716,717 }; break;
		/** 기란마을 - 액세서리 - 가죽 - **/
		case 7210046: craftlist = new int[] { 706,707,710,711 }; break;
		/** 기란마을 - 액세서리 - 룸티스 - **/
		case 7210052: craftlist = new int[] { 928,929,930,931,932,933,934,935,936,937,938,939,940,941,942,943,944,945,1539,1540,1541,1542,1543,1544}; break; 
		/** 기란마을 - 액세서리 - 스냅퍼 - **/
		case 7210053: craftlist = new int[] { 946,947,948,949,950,951,952,953,954,955,956,957,958,959,960,961,962,963,964,965,966,967,968,969,970,971,972,973,974,975,976,977,978,979,980,981, 982,983,984,985,986,987}; break;
        /** 기란마을 - 엘릭서 - **/
		case 7210054: craftlist = new int[] { 1043,1044,1045,1046,1047,1048 }; break;
/****************************************************************************************************/		
		/** 웰던마을 - 칼루아 **/
		case 3000006: craftlist = new int[] { 157,158 }; break;
		/** 웰던마을 - 슈에르메 **/
	//	case 900000: craftlist = new int[] { 103,104,105,106,107,108,109,496,497 }; break;
		/** 웰던마을 - 세심한 슈누 **/
		case 900001: craftlist = new int[] { 83,84,85,86 }; break;
		/** 웰던마을 - 끈질긴 도오호 **/
		case 900002: craftlist = new int[] { 87,88,89,90 }; break;
		/** 웰던마을 - 강인한 하이오스 **/
		case 900003: craftlist = new int[] { 79,80,81,82 }; break;
		/** 웰던마을 - 찬란한 바에미 **/
		case 900004: craftlist = new int[] { 91,92,93,94 }; break;
		/** 웰던마을 - 조우의 불골렘 **/
		case 5066: craftlist = new int[] { 515,514,516,517,116,123,159,160,161,162,271,272,273,274,275,276,277,278,279,280,281,282,283,284,285,286,287,288,289,290,423,424,425 }; break;
		/** 잊섬 - 럭키 **/
		case 7310086: craftlist = new int[] { 1771, 1772, 1773, 1774, 1775, 1776, 1777, 1778, }; break;
/****************************************************************************************************/		
		/** 용기사마을 - 대장장이 퓨알 **/
		case 3000001: craftlist = new int[] { 62,65,63,64,69,70,71,72,73,74,75,76,77,78 }; break;
		/** 환술사마을 - 대장장이 바트르 **/
		case 3100001: craftlist = new int[] { 67,68, 69,70,71,72,73,74,75,76,77,78 }; break;
/****************************************************************************************************/		
		/** 화전민마을 - 라이아 **/
		case 70811: craftlist = new int[] { 417,418,419,420 }; break;
/****************************************************************************************************/			
		/** 오렌마을3층 - 타라스 리뉴얼 **/
		case 70763: craftlist = new int[] { 214,2057,2058,2059,2060,2061,2062,2064,2063,2065 }; break;
/****************************************************************************************************/			
		/** 아덴마을 - 무브니 **/
		case 7210049: craftlist = new int[] { 823,824,825,826,827,828,829,830,831,832,833,834,835,836,837,838,839,840,841,842,843,844,845,846,847,848,849,850,851,852 }; break;
/****************************************************************************************************/			
		/** 연구실 - 네르바 **/
		case 7210042: craftlist = new int[] { 577,578,579,580,581 }; break;
/****************************************************************************************************/			
		/** 아타로제 **/
		case 71119: craftlist = new int[] { 56 }; break;
		/** 아델리오 **/
		case 71125: craftlist = new int[] { 46,47,48,49,50,51,52,53,54,55 }; break;
		
		
		/** 모리아 **/
		case 70598: craftlist = new int[] { 198,1810, }; break;//198 찾아야함
		/** 레옹 신규 **/
		case 11887: craftlist = new int[] { 1763,1764,1765,1766,1767,1768,1769,1770 }; break;
		/** 문장 강화 수정 **/
		//case 7310087: craftlist = new int[] { 2174,2175,2873,2874,2875,422,2747 }; break;
		case 7310087: craftlist = new int[] {
				3541, 3542, 3543, 3544, 3545, 3546, 3547, 3548,
				3549, 3550, 3551, 3552, 3553, 3554, 3555, 3556,
				3557, 3558, 3559, 3560, 3561, 3562, 3563, 3564,
				3566, 3567, 3568, 3569, 3570, 3571, 3572, 3573,
				3574, 3575, 3576, 3577, 3578, 3579, 3580, 3581,
				3582, 3583, 3584, 3585, 3586, 3587, 3588, 3589, 
				3590, 3591, 3592, 3593, 3594, 3595, 3596, 3597,
				2871, 2872, 2873, 2874, 2875, 2747};  break;
		/** 추가 시작**/			
		case 70838: craftlist = new int[] { 2864,2865,2857,2861,2773,2774,2759,173,183,184,185,211 }; break; //네루파
		
		/** 기란마을 - 드래곤수정구 **/
		case 7312087: craftlist = new int[]  { 3385, 3386, 3387,  2397, 497,  103,104,105,106,107,108,109, }; break;

		/** 레서 데몬(발록) **/
		case 80069: craftlist = new int[] { 35,36,37,38}; break; 
		/** 발록진영 - 발록의 분신 **/
		case 80068: craftlist = new int[] { 39,40,41,42 }; break;
		/** 래리 보좌관 **/
		case 70652: craftlist = new int[] { 10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33}; break;
		/** 엔트 **/
		case 70848: craftlist = new int[] {188, 189, 190}; break;
		/** 나르엔 **/
		case 70837:	craftlist = new int[] { 186 }; break;
		/** 주티스 (솔로부대이벤트상자) **/
		case 1: craftlist = new int[] { 360, 361, 362, 363 }; break;
		/** 가웨인 **/
		case 7000078: craftlist = new int[] { 159,160 }; break;
		/** 루디엘 **/
		case 70841: craftlist = new int[] { 187,188 }; break;
		/** 아라크네 **/
		case 70846: craftlist = new int[] { 163,164,165 }; break;
		/** 판 **/
		case 70850: craftlist = new int[] { 166,167,168 }; break;
		/** 페어리 **/
		case 70851: craftlist = new int[] { 169,170 }; break;
		/** 페어리 퀸**/
		case 70852: craftlist = new int[] { 169,170,171 }; break;
		/** 야히진영 - 야히의 대장장이 **/
		case 80053: craftlist = new int[] { 149, 150, 151, 152, 153, 154, 155, 156 }; break;
		/** 야히진영 - 연구원 **/
		case 80054: craftlist = new int[] { 7, 8, 9 }; break; 
		/** 야히진영 - 야히 **/
		case 80051: craftlist = new int[] { 6 }; break;
		/** 발록진영 - 발록의 대장장이 **/
		case 80072: craftlist = new int[] { 141, 142, 143, 144, 145, 146, 147, 148 }; break;
		//아놀드 이벤트 엔피씨
		case 6 : craftlist = new int[] { 1629,1630,1631,1632,1633,1634,1635,1636,1637,1638,1639,1640,1641,1642,1643,1644,1645,1646 }; break;
		case 8 : craftlist = new int[] { 1647,1648,1649,1650,1651,1652,1653,1654,1655 }; break;
		
		//바레트
		case 7210071:	craftlist = new int[] { 2528,1861,95,96,97,98,99,1960,1961 }; break;
		
		//고대 물품 연금술사^아만
		case 7210072:	craftlist = new int[] { 2652,2653 }; break;
		
		//보석상인_디오
		case 70027:	craftlist = new int[] { 2739, 2792, 2731, 2732, 2733, 2734, 2735, 2736, 2737, 2738, 2788, 2789, 2790, 2791 }; break;
		
		
		
		//이벨빈
		case 70662: craftlist = new int[] { 2626,2625,2624,2623,2622,2619,2858,203,204,205,206,207,208,209,210 }; break;
		//case 70662 :craftlist = new int[]{ 2877,2876,2875,2875,2879,2878,2879,2880,2881,2882,2883,2884,2885,2886,2887,2888,2889,2890};break;
/*		case 70904 : 
			System.out.println("테스트1");
		craftlist = new int[30];
		System.out.println("테스트2");
		int tte=0;
		System.out.println("테스트3");
		for(int i =1+30*WeekQuestTable.getInstance().test ; i <= 30+WeekQuestTable.getInstance().test;i++){
			craftlist[tte] = 2890+i;
			tte++;
			System.out.println("테스트7: "+tte);
		}
		WeekQuestTable.getInstance().test++;
		System.out.println(WeekQuestTable.getInstance().test);
		
		break;
	*/
		//고대 쿠프
		case 70904:	craftlist = new int[] { 2652,2653 }; break;
		
		case 460000128:
			 craftlist = new int[] { Config.CRAFT_TABLE_ONE,  Config.CRAFT_TABLE_TWO, Config.CRAFT_TABLE_THREE, Config.CRAFT_TABLE_FOUR,
					 Config.CRAFT_TABLE_FIVE,  Config.CRAFT_TABLE_SIX,  Config.CRAFT_TABLE_SEVEN,  Config.CRAFT_TABLE_EIGHT, Config.CRAFT_TABLE_NINE, Config.CRAFT_TABLE_TEN};
			break;
	
		case 460000127:
			int t = Config.CRAFT_TABLE;
			craftlist = new int[] { t,t+1,t+2,t+3,t+4,t+5,t+6,t+7,t+8,t+9 };
			break;
			
		}
		int num;
		for (int i = 0; i < craftlist.length; i++) {
			writeC(0x12);
			num = craftlist[i];
			if (num > 127) {
				writeC(0x07);
			} else {
				writeC(0x06);
			}
			writeC(0x08);
			write4bit(num);
			writeH(0x10);
			writeH(0x18);
		}
		writeH(0x00);
		} catch (Exception e) {
			
		}
	}
	
	/**
	 * 혈맹관련
	 */
	public S_ACTION_UI(String clanname, int rank) {
		writeC(Opcodes.S_EXTENDED_PROTOBUF);
		writeC(0x19);
		writeC(0x02);
		writeC(0x0a);
		int length = 0;
		if (clanname != null)
			length = clanname.getBytes().length;
		if (length > 0) {
			writeC(length); // 클랜명 SIZE
			writeByte(clanname.getBytes()); // 클랜명
			writeC(0x10);
			writeC(rank); // 클랜 랭크
		} else {
			writeC(0x00);
		}
		writeH(0x00);
	}

	/**
	 * 전사스킬을 위해 
	 */
	public S_ACTION_UI(int type, int skillnum) {
		writeC(Opcodes.S_EXTENDED_PROTOBUF);
		writeC(type);
		if (type == 145) { // 로그인
			// b3 91 01 0a 02 08 03 8c c7
			writeC(0x01);
			writeC(0x0a);
			writeC(skillnum != 5 ? 0x02 : 0x04);
			writeC(0x08);
			writeC(skillnum);
			if (skillnum == 5) { // 아머가드
				writeC(0x10);
				writeC(0x0a);
			}
			writeH(0xf18d);
		} else if (type == 146) { // 새로 생성시
			// b3 92 01 08 03 c2 33
			writeC(0x01);
			writeC(0x08);
			writeC(skillnum);
			if (skillnum == 5) { // 아머가드
				writeC(0x10);
				writeC(0x0a);
			}
			writeH(0x00);
		} else if (type == TAM) {
			writeC(0x01);
			writeC(0x08);
			write4bit(skillnum);
			writeH(0x00);
		} 
		//test
		else if(type ==TEST){
			writeC(0x0d);
			write4bit(skillnum);
		} else if(type == CLAN_JOIN_MESSAGE){
			writeH(0x0801);
			writeC(skillnum);
			writeH(0x00);
		}

	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	public String getType() {
		return S_ACTION_UI;
	}
}
