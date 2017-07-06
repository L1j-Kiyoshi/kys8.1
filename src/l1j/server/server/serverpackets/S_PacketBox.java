package l1j.server.server.serverpackets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.TimeZone;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.Account;
import l1j.server.server.Opcodes;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.utils.SQLUtil;

/**
 * 스킬 아이콘이나 차단 리스트의 표시 등 복수의 용도에 사용되는 패킷의 클래스
 */
public class S_PacketBox extends ServerBasePacket {
	
	private static final String S_PACKETBOX = "[S] S_PacketBox";

	private byte[] _byte = null;

	// *** S_107 sub code list ***

	// 1:Kent 2:Orc 3:WW 4:Giran 5:Heine 6:Dwarf 7:Aden 8:Diad 9:성명 9 ...
	/** C(id) H(?): %s의 공성전이 시작되었습니다. */
	public static final int MSG_WAR_BEGIN = 0;

	/** C(id) H(?): %s의 공성전이 종료했습니다. */
	public static final int MSG_WAR_END = 1;

	/** C(id) H(?): %s의 공성전이 진행중입니다. */
	public static final int MSG_WAR_GOING = 2;

	/** -: 성의 주도권을 잡았습니다. (음악이 바뀐다) */
	public static final int MSG_WAR_INITIATIVE = 3;

	/** -: 성을 점거했습니다. */
	public static final int MSG_WAR_OCCUPY = 4;

	/** ?: 결투가 끝났습니다. (음악이 바뀐다) */
	public static final int MSG_DUEL = 5;

	/** C(count): SMS의 송신에 실패했습니다. / 전부%d건송신되었습니다. */
	public static final int MSG_SMS_SENT = 6;

	/** -: 축복안, 2명은 부부로서 연결되었습니다. (음악이 바뀐다) */
	public static final int MSG_MARRIED = 9;

	/** C(weight): 중량(30 단계) */
	public static final int WEIGHT = 10;

	/** C(food): 만복도(30 단계) */
	public static final int FOOD = 11;

	/** C(0) C(level): 이 아이템은%d레벨 이하만 사용할 수 있습니다. (0~49이외는 표시되지 않는다) */
	public static final int MSG_LEVEL_OVER = 12;

	/** UB정보 HTML */
	public static final int HTML_UB = 14;

	/**
	 * C(id)<br>
	 * 1:몸에 담겨져 있던 정령의 힘이 공기안에 녹아 가는 것을 느꼈습니다.<br>
	 * 2:몸의 구석구석에 화의 정령력이 스며들어 옵니다.<br>
	 * 3:몸의 구석구석에 물의 정령력이 스며들어 옵니다.<br>
	 * 4:몸의 구석구석에 바람의 정령력이 스며들어 옵니다.<br>
	 * 5:몸의 구석구석에 땅의 정령력이 스며들어 옵니다.<br>
	 */
	public static final int MSG_ELF = 15;

	/** C(count) S(name)...: 차단 리스트 */
	public static final int SHOW_LIST_EXCLUDE = 17;

	/** S(name): 차단 리스트 추가 */
	public static final int ADD_EXCLUDE = 18;

	/** S(name): 차단 해제 */
	public static final int REM_EXCLUDE = 19;
	
	/** PC방버프 */
	public static final int PC방버프 = 127;
	
	/** 스킬 아이콘 */
	public static final int ICONS1 = 20;

	/** 스킬 아이콘 */
	public static final int ICONS2 = 21;

	/** 아우라계의 스킬 아이콘 및 이레이즈매직 아이콘 삭제 */
	public static final int ICON_AURA = 22;

	/** S(name): 타운 리더에게%s가 선택되었습니다. */
	public static final int MSG_TOWN_LEADER = 23;

	/** 
	 * D(혈맹원수) (S(혈원이름) C(혈원계급)) 혈맹원 갱신이 된 상태에서의 /혈맹.
	 */
	public static final int PLEDGE_TWO = 24;//추가

	/** 
	 * D(혈맹원이름) C(랭크) 혈맹에 추가된 인원이 있을때 보내주는 패킷
	 */
	public static final int PLEDGE_REFRESH_PLUS = 25;//추가

	/** 
	 * D(혈맹원이름) C(랭크) 혈맹에 삭제된 인원이 있을때 보내주는 패킷
	 */
	public static final int PLEDGE_REFRESH_MINUS = 26;//추가
	/**
	 * C(id): 당신의 랭크가%s로 변경되었습니다.<br>
	 * id - 1:견습 2:일반 3:가디안
	 */
	public static final int MSG_RANK_CHANGED = 27;

	/** 
	 * D(혈맹원수) (S(혈원이름) C(혈원계급)) 혈맹원 갱신이 안된 상태에서의 /혈맹.
	 */
	//public static final int PLEDGE_ONE = 119;//추가

	/** D(?) S(name) S(clanname): %s혈맹의%s가 라스타바드군을 치웠습니다. */
	public static final int MSG_WIN_LASTAVARD = 30;

	/** -: \f1기분이 좋아졌습니다. */
	public static final int MSG_FEEL_GOOD = 31;

	/** 불명.C_30 패킷이 난다 */
	public static final int SOMETHING1 = 33;

	/** H(time): 블루 일부의 아이콘이 표시된다. */
	public static final int ICON_BLUEPOTION = 34;

	/** H(time): 변신의 아이콘이 표시된다. */
	public static final int ICON_POLYMORPH = 35;

	/** H(time): 채팅 금지의 아이콘이 표시된다. */
	public static final int ICON_CHATBAN = 36;

	/** 불명.C_7 패킷이 난다.C_7은 애완동물의 메뉴를 열었을 때에도 난다. */
	public static final int SOMETHING2 = 37;

	/** 혈맹 정보의 HTML가 표시된다 */
	public static final int HTML_CLAN1 = 38;

	/** H(time): 이뮤의 아이콘이 표시된다 */
	public static final int ICON_I2H = 40;

	/** 캐릭터의 게임 옵션, 쇼트 컷 정보등을 보낸다 */
	public static final int CHARACTER_CONFIG = 41;

	/** 캐릭터 선택 화면으로 돌아간다 */
	public static final int LOGOUT = 42;

	/** 전투중에 재시 동요할 수 없습니다. */
	public static final int MSG_CANT_LOGOUT = 43;

	/**
	 * C(count) D(time) S(name) S(info):<br>
	 * [CALL] 버튼이 붙은 윈도우가 표시된다.이것은 BOT등의 부정자 체크에
	 * 사용되는 기능한 것같다.이름을 더블 클릭 하면(자) C_RequestWho가 날아, 클라이언트의
	 * 폴더에 bot_list.txt가 생성된다.이름을 선택해+키를 누르면(자) 새로운 윈도우가 열린다.
	 */
	public static final int CALL_SOMETHING = 45;

	/**
	 * C(id): 배틀 콜롯세움, 카오스 대전이―<br>
	 * id - 1:개시합니다 2:삭제되었던 3:종료합니다
	 */
	public static final int MSG_COLOSSEUM = 49;

	// 혈맹 정보의 HTML
	public static final int HTML_CLAN2 = 51;

	// 요리 윈도우를 연다
	public static final int COOK_WINDOW = 52;

	/** C(type) H(time): 요리 아이콘이 표시된다 */
	public static final int ICON_COOKING = 53;

	/** 물고기찌 흔들림포시 */
	public static final int FISHING = 55;

	/** 아이콘 삭제 */
	public static final int DEL_ICON = 59;

	/** 드래곤의 진주 (3단가속) */
	public static final int DRAGON_PEARL = 60;

	/** 동맹 목록 */
	public static final int ALLIANCE_LIST = 62;
	
	/** 미니게임 : 5,4,3,2,1 카운트 */
	public static final int MINIGAME_START_COUNT = 64;
	
	/** 미니게임 : 타임(0:00시작) */
	public static final int MINIGAME_TIME2 = 65;

	/** 미니게임 : 게임자 리스트 */
	public static final int MINIGAME_LIST = 66;

	/** 미니게임 : 잠시 후 마을로 이동됩니다(10초 음) * */
	public static final int MINIGAME_10SECOND_COUNT = 69;
	
	/** 미니게임 : 종료 */
	public static final int MINIGAME_END = 70;

	/** 미니게임 : 타임 */
	public static final int MINIGAME_TIME = 71;

	/** 미니게임 : 타임삭제 */
	public static final int MINIGAME_TIME_CLEAR = 72;

	/** 용기사 : 약점 노출 */
	public static final int SPOT = 75;

	public static final int aaaa1 = 78;// 공성전이 시작 되었습니다.
	public static final int bbbb2 = 79;// 공성전이 종료 되었습니다.
	public static final int cccc3 = 80;// 공성전이 진행중 입니다.
	/** 아인하사드 버프 */
	public static final int EINHASAD = 82;
	/** 1:분홍색테두리, 2:흔들림, 3:폭죽 **/
	public static final int HADIN_DISPLAY = 83;	
	/** 인던 녹색 메세지 **/
	public static final int GREEN_MESSAGE = 84;
	/** 인던 노랑색 메세지 **/
	public static final int YELLOW_MESSAGE = 61; // 인던 챕터2 대기
	/** 인던 빨간 메세지 **/
	public static final int RED_MESSAGE = 51; // 레드메세지
	/** 인던 점수판 **/	
	public static final int SCORE_MARK = 4;
	/** 에메랄드 버프 **/
	public static final int EMERALD_ICON = 86;
	public static final int EMERALD_ICON_NEW = 860;

	/** 우호도 UI 표시 
	 * + 욕망의 동굴
	 * - 그림자 신전 
	 */	
	public static final int KARMA = 87;//추가

	/** 스테이터스 닷지 표시 */
	public static final int INIT_DODGE = 88;//추가

	/** 드래곤 혈흔 (안타:82 , 파푸:85) */
	public static final int DRAGONBLOOD = 100;	

	public static final int DODGE = 101;

	public static final int DragonMenu = 102;

	/** 위치 전송 **/
	public static final int MINI_MAP_SEND = 111;
	
	/** 혈맹 창고리스트 */
	public static final int CLAN_WAREHOUSE_LIST = 117;// 
	
	/** 바포메트서버 패킷*/
	public static final int BAPO = 114;

	public static final int ICON_SECURITY_SERVICES = 125; //보안버프
	
	/** PC방버프 아이콘*/
	public static final int ICON_PC_BUFF = 127; 

	public static final int ER_UpDate = 132;

	public static final int BOOKMARK_SIZE_PLUS_10 = 141;// 기억 확장

	/** 아이콘 표시 **/
	public static final int UNLIMITED_ICON = 147;
	
	
	public static final int UNLIMITED_ICON1 = 180; //무제한패킷
	public static final int NONE_TIME_ICON = 180;
	
	/** 봉인 실시간 */
    public static final int ITEM_STATUS = 149;
	
	public static final int MAP_TIMER = 153;
	
	/** 나비켓의 castgfx 값의 버프이미지를 버프창에 표시 **/
	public static final int BUFFICON = 154;
	
	public static final int ROUND = 156;
	
	public static final int ROUND1 = 156;
	
	public static final int DungeonTime = 159; //던전 패킷
	
	/** 독관련 아이콘 표시 UI6 **/
	public static final int POSION_ICON = 161;
	
	/** 혈맹 버프 아이콘 */
    public static final int CLAN_BUFF_ICON = 165;

	/** UI6 3.80 혈맹관련**/
	public static final int HTML_PLEDGE_ANNOUNCE = 167;							
	
	public static final int HTML_PLEDGE_REALEASE_ANNOUNCE = 168;							
								
	public static final int HTML_PLEDGE_WRITE_NOTES = 169;							
								
	public static final int HTML_PLEDGE_MEMBERS = 170;							
								
	public static final int HTML_PLEDGE_ONLINE_MEMBERS = 171;	
	
	public static final int ITEM_ENCHANT_UPDATE = 172;
	
	public static final int PLEDGE_EMBLEM_STATUS = 173; //문장주시
	
	public static final int TOWN_TELEPORT = 176;
	
	public static final int 공격가능거리 = 160;
	public static final int 몰라2 = 184;//주군의대미지버프
	public static final int 몰라3 = 188;//주군의대미지버프
	public static final int 인벤저장 = 189;
	public static final int 배틀샷 = 181;
	public static final int 상점개설횟수 = 198;
	public static final int 유저빽스탭 = 193;
	public static final int ICON_COMBO_BUFF = 204;
	public static final int 드래곤레이드버프 = 179;


	public S_PacketBox(int subCode) {
		writeC(Opcodes.S_EVENT);
		writeC(subCode);

		switch (subCode) {
			case 몰라3:
			writeD(0);
			writeD(0);
			break;
		case 인벤저장:
			writeD(0x0d);
			break;
		case 몰라2:
			writeH(0);
			break;
		case MSG_WAR_INITIATIVE:
		case MSG_WAR_OCCUPY:
		case MSG_MARRIED:
		case MSG_FEEL_GOOD:
		case MSG_CANT_LOGOUT:
		case LOGOUT:
		case ICON_SECURITY_SERVICES:
			break;
		case FISHING:
		case MINIGAME_TIME2:
			break;
		case CALL_SOMETHING:
			callSomething();
			break;
		case MINIGAME_10SECOND_COUNT:
			writeC(10);
			writeC(109);
			writeC(85);
			writeC(208);
			writeC(2);
			writeC(220);
			break;
		case DEL_ICON:
			writeH(0);
			break;
		case MINIGAME_END:
			writeC(147);
			writeC(92);
			writeC(151);
			writeC(220);
			writeC(42);
			writeC(74);
			break;
		case MINIGAME_START_COUNT:
			writeC(5);
			writeC(129);
			writeC(252);
			writeC(125);
			writeC(110);
			writeC(17);
			break;
		case ICON_AURA:
			writeC(0x98);
			writeC(0);
			writeC(0);
			writeC(0);
			writeC(0);
			writeC(0);
			break;			
		default:
			break;
		}
	}
	
	/** 레벨업 버프 **/
	public S_PacketBox(int time, boolean ck, boolean ck2) {
		writeC(Opcodes.S_EVENT);
		writeC(0x56);
		writeC(0xAA);
		writeC(0x01);
		writeH(time / 16);
		writeH(0x00);
	}
	
	public S_PacketBox(int subCode, int range, int type, boolean bow) {
		writeC(Opcodes.S_EVENT);
		writeC(subCode);
		switch (subCode) {
		case 공격가능거리:
			writeC(range);
			writeC(type);
			if (bow)
				writeC(1);
			else {
				writeC(0);
			}
			break;
		}
	}

	public S_PacketBox(int subCode, int time1, int time2, int time3, int time4) {
		writeC(Opcodes.S_EVENT);
		writeC(subCode);
		switch (subCode) {
		case DungeonTime:// 12월14일변경
			writeD(7);
			writeD(1);
			writeS("$12125");// 기감
			writeD(time1);
			writeD(2);
			writeS("$6081");// 상아탑
			writeD(time2);
			writeD(15);
			writeS("$13527");// PC방 발록진영
			writeD(time3);
			writeD(500);
			writeS("$19375");// PC방 정무
			writeD(time4);
			writeD(49200);
			break;
		default:
			break;
		}
	}
	public S_PacketBox(int subCode, L1PcInstance pc){
		writeC(Opcodes.S_EVENT);
		writeC(subCode);
		switch(subCode){
		case TOWN_TELEPORT:
			writeC(0x01);
			writeH(pc.getX());
			writeH(pc.getY());
			break;
		case 유저빽스탭:
			writeH(pc.getX());
			writeH(pc.getY());
			break;
		}
	}
	public S_PacketBox(int subCode, int value) {
		writeC(Opcodes.S_EVENT);
		writeC(subCode);

		switch (subCode) {
		case 드래곤레이드버프:
			writeC(0x01);
			writeC(0x27);
			writeC(0x0E);
			writeD(value);// 남은초
			writeH(0x63EF);
			break;
		case 204: //콤보시스템
		      writeH(value);
		      break;
		case PC방버프:
			if (value == 1) {
				writeC(0x18);
			} else {
				writeC(0);
			}
			break;
		case 상점개설횟수:
			writeD(value);
			writeD(0x28);
			writeD(0x00);
			break;
		case ICON_BLUEPOTION:
		case ICON_CHATBAN:
		case ICON_I2H:
		case ICON_POLYMORPH:
		case MINIGAME_TIME:
		case INIT_DODGE:
			writeH(value); // time
			break;
		case MAP_TIMER://맵타이머 던전
			writeD(value);
			break;
		case 배틀샷:
			writeD(value);
			break;
		case MSG_WAR_BEGIN:
		case MSG_WAR_END:
		case MSG_WAR_GOING:
			writeC(value); // castle id
			writeH(0); // ?
			break;
		case MSG_SMS_SENT:
		case WEIGHT:
		case FOOD:
		case DODGE:
			writeC(value);
			break;
		case MSG_ELF:
		case MSG_COLOSSEUM:
		case SPOT:
		case ER_UpDate:
			writeC(value); // msg id
			break;
		case MSG_LEVEL_OVER:
			writeC(0); // ?
			writeC(value); // 0-49이외는 표시되지 않는다
			break;
		case COOK_WINDOW:
			writeC(0xdb); // ?
			writeC(0x31);
			writeC(0xdf);
			writeC(0x02);
			writeC(0x01);
			writeC(value); // level
			break;		
		case MINIGAME_LIST:
			writeH(0x00); // 참여자수
			writeH(0x00); // 등수
			break;
		case EINHASAD:
			value /= 10000;
			writeD(value);// % 수치 1~200
			writeH(0x10);
			writeC(0x27);
			writeD(0);
			writeH(0);
			break;
		case HADIN_DISPLAY:
			writeC(value);
			break;
		case BOOKMARK_SIZE_PLUS_10:
			writeC(value);
			break;		
		case PLEDGE_EMBLEM_STATUS: 
			writeC(1);
			if(value == 0){ // 0 : 해제 1 : 켜짐
				writeC(0);
			} else if(value == 1){
				writeC(1);
			}
			writeD(0x00);
			break;
		case ROUND1:
		     writeD(value);
		     writeD(12);
			break;
		default:
			break;
		}
	}
	
	public S_PacketBox(int subCode, int type, int time, boolean second,boolean temp) {
		writeC(Opcodes.S_EVENT);
		writeC(subCode);
		switch (subCode) {
		case BUFFICON:
			writeH(time);
			writeH(type);
			writeH(0x00);
			writeH(second ? 0x01 : 0x00); // 삭제 추가
			break;
		}// b0 04 80 08 00 00 00 00
	}
	
	public S_PacketBox(int subCode, int time, int gfxid, int type) {
		writeC(Opcodes.S_EVENT);
		writeC(subCode);
		switch (subCode) {
		case BUFFICON:
			writeH(time); //시간
			writeD(gfxid); //아이콘
			writeC(type); //타입
			writeC(0x00);
			break;
		}
	}

	public S_PacketBox(int subCode, int type, int time) {
		writeC(Opcodes.S_EVENT);
		writeC(subCode==EMERALD_ICON_NEW ? EMERALD_ICON : subCode);

		switch (subCode) {
		
		case ICON_COOKING:
			if (type != 7) {
				writeC(0x0c);
				writeC(0x0c);
				writeC(0x0c);
				writeC(0x12);
				writeC(0x0c);
				writeC(0x09);
				writeC(0x00);
				writeC(0x00);
				writeC(type);
				writeC(0x24);
				writeH(time);
				writeH(0x00);
			} else {
				writeC(0x0c);
				writeC(0x0c);
				writeC(0x0c);
				writeC(0x12);
				writeC(0x0c);
				writeC(0x09);
				writeC(0xc8);
				writeC(0x00);
				writeC(type);
				writeC(0x26);
				writeH(time);
				writeC(0x3e);
				writeC(0x87);
			}
			break;
		case ICON_AURA:
			writeC(0xdd);
			writeH(time);
			writeC(type);
			break;
		case MSG_DUEL:
			writeD(type); 
			writeD(time);
			break;
		case BUFFICON:
			writeH(time);
		    writeH(type);       
		    writeH(0);
		      break;
		case DRAGONBLOOD:
			writeC(type);
			writeD(time);  
			break;
		case ROUND:
			writeD(type); // 현재 라운드 표시
			writeD(time); // 총 라운드 표시
			break;
		case DRAGON_PEARL:
			//writeC(time);
			//writeC(type);
			writeC((int)((time + 2) / 4));
			writeC(type);
			break;
		case EMERALD_ICON: // 에메랄드 아이콘
			writeC(0x70);
			writeC(0x01);
			writeC(type);
			writeH(time); // time(초)
			break;
		case EMERALD_ICON_NEW:
			// new
			writeC(0x3e);
			writeC(type);
			writeH(time);
			writeC(0x14);
			writeC(0x80);//pc방은 86
			break;
		case NONE_TIME_ICON:
			writeC(type);// on/off
			writeD(time);// 166 exp30% 228 시원한얼음조각286 exp40% 343 기르타스지역사망패널티
							// 409아머브레이크 497붉은기사의증표 이벤트공성존 //477~479
			writeD(0x00000D67);
			writeH(0x00);
			break;
		case 9278:
			writeC(time);
			writeC(type);
			break;
		default:
			break;
		}
	}
	
	/** 자수정 **/
	// public S_PacketBox(int time, int val, boolean ck, boolean ck2) {
	public S_PacketBox(int time, int val, boolean ck, boolean ck2) {
		writeC(Opcodes.S_EVENT);
		writeC(EMERALD_ICON);
		writeC(0x81);
		writeC(0x01);
		writeC(val);
		writeH(time);
	}// 7e 56 81 01 02 08 07

	public S_PacketBox(int i, int time, boolean ck, boolean ck2, boolean ck3) {
		writeC(Opcodes.S_EVENT);
		writeC(EMERALD_ICON);
		writeC(0x3e);
		writeC(i);
		writeH(time);
		writeC(0x14);
		writeC(0x86);
	}// 0f 56 3e 01 08 07 14 86

	public S_PacketBox(int subCode, String name) {
		writeC(Opcodes.S_EVENT);
		writeC(subCode);

		switch (subCode) {
		case MSG_TOWN_LEADER:
		case HTML_PLEDGE_REALEASE_ANNOUNCE:
			writeS(name);
			break;
		case GREEN_MESSAGE:
			writeC(2);
			writeS(name);
			break;
		default:
			break;
		}
	}
	/**
	 * 발라카스레이드
	 */
	
	  public S_PacketBox(int subCode1, int subCode2, String name, boolean ok) {
	    	writeC(Opcodes.S_EVENT);
	    	writeC(subCode1);
	    	switch (subCode2) {
	    	case RED_MESSAGE:
	    	case YELLOW_MESSAGE:
	    		writeC(2);
	    		writeH(26204);
	    		writeC(subCode2);
	    		writeS(name);
	    		break;
	    	case SCORE_MARK:
	    		writeC(subCode2);
	    		writeS(name);
	    		break;
	    	default: // ?
	    	switch (subCode1) {
	    	case MSG_RANK_CHANGED:
	    		writeC(subCode2);
	    		writeS(name);
	    		break;
	    	case ADD_EXCLUDE:
	    	case REM_EXCLUDE:
	    		writeS(name);
	    		writeC(subCode2);
	    		break;
	    	}
	    	break;
	    	}
	    }
	public S_PacketBox(int subCode, int id, String name, String clanName) {
		writeC(Opcodes.S_EVENT);
		writeC(subCode);

		switch (subCode) {
		case MSG_WIN_LASTAVARD:
			writeD(id); // 크란 ID인가 무엇인가?
			writeS(name);
			writeS(clanName);
			break;
		default:
			break;
		}
	}
	
    public S_PacketBox(int subCode, L1ItemInstance item, int type) {
        writeC(Opcodes.S_EVENT);
        writeC(subCode);
        switch (subCode) {
        case ITEM_STATUS:
            writeD(item.getId());
            writeH(type);
            break;
        }
    }
    
    public S_PacketBox(int subCode1, int subCode2, String name) {
    	writeC(Opcodes.S_EVENT);
    	writeC(subCode1);
    	switch (subCode2) {
    	case RED_MESSAGE:
    	case YELLOW_MESSAGE:
    		writeC(2);
    		writeH(26204);
    		writeC(subCode2);
    		writeS(name);
    		break;
    	case SCORE_MARK:
    		writeC(subCode2);
    		writeS(name);
    		break;
    	default: // ?
    	switch (subCode1) {
    	case MSG_RANK_CHANGED:
    		writeC(subCode2);
    		writeS(name);
    		break;
    	case ADD_EXCLUDE:
    	case REM_EXCLUDE:
    		writeS(name);
    		writeC(subCode2);
    		break;
    	}
    	break;
    	}
    }
    
/*	public S_PacketBox(int subCode, String name, int type) {  
		writeC(Opcodes.S_EVENT);
		writeC(subCode);
		switch (subCode) {
		case ADD_EXCLUDE:
		case REM_EXCLUDE:
			writeS(name);
			writeC(type);
			break;
		default:
			break;
		}
	}*/
    

	public S_PacketBox(int subCode, Object[] names) {
		writeC(Opcodes.S_EVENT);
		writeC(subCode);

		switch (subCode) {
		case HTML_PLEDGE_ONLINE_MEMBERS:
			writeH(names.length);
			for (Object name : names) {
				if (name == null) continue;
				L1PcInstance pc = (L1PcInstance) name;
				writeS(pc.getName());
				writeC(0);
			}
			break;
		default:
			break;
		}
	}

	public S_PacketBox(int subCode, String[] names, int type) {
		writeC(Opcodes.S_EVENT);
		writeC(subCode);
		writeC(0);
		switch (subCode) {
		case SHOW_LIST_EXCLUDE:
			writeC(type);
			writeC(names.length);
			for (String name : names) {
				writeS(name);
			}
			writeH(0);
			break;
		}
	}
	
	public S_PacketBox(int subCode, L1ItemInstance item){   
		writeC(Opcodes.S_EVENT);
		writeC(subCode);
		switch (subCode) {
		case DragonMenu:
			writeD(item.getId());
			writeC(item.getItemId() == 490012 ? 0x01: 0x00);	// 안타0
			writeC(item.getItemId() == 490013 ? 0x01: 0x00);	// 파푸1
			writeC(item.getItemId() == 490014 ? 0x01: 0x00);	// 린드2
			writeC(0);
			break;
		case ITEM_ENCHANT_UPDATE:
			writeD(item.getId());
			writeC(0x18);
			writeC(0);
			writeH(0);
			writeH(0);
			if(item.getItem().getType2() == 0){
				writeC(0);
			} else {
				writeC(item.getEnchantLevel());
			}
			writeD(item.getId());
			writeD(0);
			writeD(0);
			writeD(item.getBless() >= 128 ? 3 : item.getItem().isTradable() ? 7 : 2);
			writeC(0);
			switch(item.getAttrEnchantLevel()){
			case 0:
				writeC(0);
				break;
			case 1:
				writeC(0x11);
				break;
			case 2:
				writeC(0x21);
				break;
			case 3:
				writeC(0x31);
				break;
			case 4:
				writeC(0x41);
				break;
			case 5:
				writeC(0x51);
				break;
			case 6:
				writeC(0x12);
				break;
			case 7:
				writeC(0x22);
				break;
			case 8:
				writeC(0x32);
				break;
			case 9:
				writeC(0x42);
				break;
			case 10:
				writeC(0x52);
				break;	
			case 11:
				writeC(0x13);
				break;
			case 12:
				writeC(0x23);
				break;
			case 13:
				writeC(0x33);
				break;
			case 14:
				writeC(0x43);
				break;
			case 15:
				writeC(0x53);
				break;
			case 16:
				writeC(0x14);
				break;
			case 17:
				writeC(0x24);
				break;
			case 18:
				writeC(0x34);
				break;
			case 19:
				writeC(0x44);
				break;
			case 20:
				writeC(0x54);
				break;
			}
			
			writeH(0);
			break;
		default:
			break;
		}
	}

	public S_PacketBox(int subCode, L1PcInstance pc, int value1, int value2) {
		writeC(Opcodes.S_EVENT);
		writeC(subCode);
		switch (subCode) {
		case POSION_ICON:
			writeC(value1); // type : 1.포이즌  6:사일런스 
			if(value1 == 2){
				writeH(0x00);
				writeH(value2);
				writeH(0x00);
			}else{
				writeD(value2); // time (초)
			}
			break;
		default:
			break;
		}
	}

	public S_PacketBox(L1PcInstance pc, int subCode) {
		String clanName = pc.getClanname();
		L1Clan clan = L1World.getInstance().getClan(clanName);
		
		writeC(Opcodes.S_EVENT);
		writeC(subCode);

		switch (subCode) {
		
		case CLAN_WAREHOUSE_LIST:
			int count = 0;
			Connection con = null;
			PreparedStatement pstm = null;
			PreparedStatement pstm2 = null;
			PreparedStatement pstm3 = null;
			ResultSet rs = null;
			ResultSet rs3 = null;
			try {
				con = L1DatabaseFactory.getInstance().getConnection();
				pstm = con.prepareStatement("SELECT id, time FROM clan_warehouse_log WHERE clan_name='"
						+ pc.getClanname() + "'");
				rs = pstm.executeQuery();
				while (rs.next()) {
					if (System.currentTimeMillis() - rs.getTimestamp(2).getTime() > 4320000) {// 3일
						pstm2 = con.prepareStatement("DELETE FROM clan_warehouse_log WHERE id='" + rs.getInt(1) + "'");
						pstm2.execute();
					} else
						count++;
				}
				writeD(count);
				pstm3 = con
						.prepareStatement("SELECT name, item_name, item_count, type, time FROM clan_warehouse_log WHERE clan_name='"
								+ pc.getClanname() + "'");
				rs3 = pstm3.executeQuery();
				while (rs3.next()) {
					writeS(rs3.getString(1));
					writeC(rs3.getInt(4));// 0:맡김 1:찾음
					writeS(rs3.getString(2));
					writeD(rs3.getInt(3));
					writeD((int) (System.currentTimeMillis() - rs3.getTimestamp(5).getTime()) / 60000);				}
			} catch (SQLException e) {
			} finally {
				SQLUtil.close(rs, pstm, con);
				SQLUtil.close(pstm2);
				SQLUtil.close(rs3);
				SQLUtil.close(pstm3);
			}
			break;
		
		//case PLEDGE_REFRESH_PLUS:
		case PLEDGE_REFRESH_MINUS:
			writeS(pc.getName());
			writeC(pc.getClanRank());
			writeH(0);
			break;
		case KARMA:
			writeD(pc.getKarma());
			break;
//		case ALLIANCE_LIST:
//			StringBuffer sb = new StringBuffer();
//			for (int i : pc.getClan().Alliance()) {
//				if (i == 0)
//					continue;
//				L1Clan c = L1World.getInstance().getClan(i);
//				if (c == null)
//					continue;
//				sb.append(c.getClanName() + " ");
//			}
//			writeS(sb.toString());
//			break;
//		case PLEDGE_ONE:
//            writeD(clan.getOnlineMemberCount());
//            for (L1PcInstance targetPc : clan.getOnlineClanMember()) {
//                writeS(targetPc.getName());
//                writeC(targetPc.getClanRank());
//            }
//            writeD((int) (System.currentTimeMillis() / 1000L));
//            writeS(clan.getLeaderName());
//            break;
//		case PLEDGE_TWO:
//			writeD(clan.getClanMemberList().size());
//
//			ClanMember member;
//			ArrayList<ClanMember> clanMemberList = clan.getClanMemberList(); 
//			// 모든혈맹원의이름과등급
//			for (int i = 0; i < clanMemberList.size(); i++) {
//				member = clanMemberList.get(i);
//				writeS(member.name);
//				writeC(member.rank);
//			}
//
//			writeD(clan.getOnlineMemberCount());
//			for (L1PcInstance targetPc : clan.getOnlineClanMember()) { // 온라인
//				writeS(targetPc.getName());
//			}
//			break;
		default:
			break;
		}
	}

	public S_PacketBox(int subCode, String name, int mapid, int x, int y, int Mid) {
		writeC(Opcodes.S_EVENT);
		writeC(subCode);
		switch (subCode) {
		case MINI_MAP_SEND:
			writeS(name);
			writeH(mapid);
			writeH(x);
			writeH(y);
			writeD(Mid);
			break;
		default: 
			break;
		}
	}

	public S_PacketBox(int subCode, int value, boolean show) {
		writeC(Opcodes.S_EVENT);
		writeC(subCode);
		switch (subCode) {
		case BAPO:
			writeD(value); // 1~7 깃발
			writeD(show ? 0x01 : 0x00); // On Off
			break;
		case UNLIMITED_ICON: // 무제한 패킷 
			writeC(show ? 0x01 : 0x00); // On Off // true false
			writeC(value); // 
			break;
		case UNLIMITED_ICON1:
			writeC(show ? 0x01 : 0x00); // On Off // true false
			writeD(value);
			writeD(0);
			writeH(0);
			break;
		default:
			break;
		}
	}
	
	public S_PacketBox(int subCode, boolean show) {
		writeC(Opcodes.S_EVENT);
		writeC(subCode);
		switch(subCode) {
		case CLAN_BUFF_ICON:
			writeC(show ? 0x01 : 0x00);
		break;
		}
	}
	

	private void callSomething() {
		Iterator<L1PcInstance> itr = L1World.getInstance().getAllPlayers().iterator();

		writeC(L1World.getInstance().getAllPlayers().size());
		L1PcInstance pc = null;
		Account acc = null;
		Calendar cal = null;
		while (itr.hasNext()) {
			pc = itr.next();
			acc = Account.load(pc.getAccountName());
			// 시간 정보 우선 로그인 시간을 넣어 본다655
			if (acc == null) {
				writeD(0);
			} else {
				cal = Calendar.getInstance(TimeZone.getTimeZone(Config.TIME_ZONE));
				long lastactive = acc.getLastActive().getTime();
				cal.setTimeInMillis(lastactive);
				cal.set(Calendar.YEAR, 1970);
				int time = (int) (cal.getTimeInMillis() / 1000);
				writeD(time); // JST 1970 1/1 09:00 이 기준
			}

			// 캐릭터 정보
			writeS(pc.getName()); // 반각 12자까지
			writeS(pc.getClanname()); // []내에 표시되는 캐릭터 라인.반각 12자까지
		}
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}

		return _byte;
	}
	@Override
	public String getType() {
		return S_PACKETBOX;
	}
}
