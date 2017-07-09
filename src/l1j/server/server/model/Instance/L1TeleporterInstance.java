package l1j.server.server.model.Instance;

import java.util.Random;
import java.util.logging.Logger;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.datatables.NPCTalkDataTable;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1NpcTalkData;
import l1j.server.server.model.L1Quest;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.npc.L1NpcHtml;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.templates.L1Npc;

public class L1TeleporterInstance extends L1NpcInstance {

	private static final long serialVersionUID = 1L;

	public L1TeleporterInstance(L1Npc template) {
		super(template);
	}

	@Override
	public void onAction(L1PcInstance player) {
		L1Attack attack = new L1Attack(player, this);
		attack.calcHit();
		attack.action();
	}

	@Override
	public void onTalkAction(L1PcInstance player) {
        if (player == null || this == null)
            return;
		int objid = getId();
		L1NpcTalkData talking = NPCTalkDataTable.getInstance().getTemplate(
				getNpcTemplate().get_npcId());
		int npcid = getNpcTemplate().get_npcId();
		L1Quest quest = player.getQuest();
		String htmlid = null;

		if (talking != null) {
			switch(npcid){
			case 50014: // 디론
				if (player.isWizard()) { // 위저드
					if (quest.get_step(L1Quest.QUEST_LEVEL30) == 1
							&& !player.getInventory().checkItem(40579)) { // 안 데드의 뼈
						htmlid = "dilong1";
					} else {
						htmlid = "dilong3";
					}
				}
				break;
			case 70779: // 게이트 안트
				if (player.getTempCharGfx() == 1037) { // 쟈이안트안트 변신
					htmlid = "ants3";
				} else if (player.getTempCharGfx() == 1039) {// 쟈이안트안트소르쟈 변신
					if (player.isCrown()) { // 군주
						if (quest.get_step(L1Quest.QUEST_LEVEL30) == 1) {
							if (player.getInventory().checkItem(40547)) { // 주민들의 유품
								htmlid = "antsn";
							} else {
								htmlid = "ants1";
							}
						} else { // Step1 이외
							htmlid = "antsn";
						}
					} else { // 군주 이외
						htmlid = "antsn";
					}
				}
				break;
			case 70853: // 페어리 프린세스
				if (player.isElf()) { // 에르프
					if (quest.get_step(L1Quest.QUEST_LEVEL30) == 1) {
						if (!player.getInventory().checkItem(40592)) { // 저주해진 정령서
							Random random = new Random(System.nanoTime());
							if (random.nextInt(100) < 50) { // 50%로 다크마르단젼
								htmlid = "fairyp2";
							} else { // 다크 에르프 지하 감옥
								htmlid = "fairyp1";
							}
						}
					}
				}
				break;
			case 50031: // 세피아
				if (player.isElf()) { // 에르프
					if (quest.get_step(L1Quest.QUEST_LEVEL45) == 2) {
						if (!player.getInventory().checkItem(40602)) { // 블루 플룻
							htmlid = "sepia1";
						}
					}
				}
				break;
			case 50043:
				if (quest.get_step(L1Quest.QUEST_LEVEL50) == L1Quest.QUEST_END) {
					htmlid = "ramuda2";
				} else if (quest.get_step(L1Quest.QUEST_LEVEL50) == 1) { // 디가르딘 동의가 끝난 상태
					if (player.isCrown()) { // 군주
						if (_isNowDely) { // 텔레포트 지연중
							htmlid = "ramuda4";
						} else {
							htmlid = "ramudap1";
						}
					} else { // 군주 이외
						htmlid = "ramuda1";
					}
				} else {
					htmlid = "ramuda3";
				}
				break;
			case 50082: // 노래하는 섬 텔레포터
				if (player.getLevel() < 13) {
					htmlid = "en0221";
				} else {
					if (player.isElf()) {
						htmlid = "en0222e";
					} else if (player.isDarkelf()) {
						htmlid = "en0222d";
					} else {
						htmlid = "en0222";
					}
				}
				break;
			case 50001: // 바르니아
				if (player.isElf()) {
					htmlid = "barnia3";
				} else if (player.isKnight() || player.isCrown() || player.isWarrior()) {
					htmlid = "barnia2";
				} else if (player.isWizard() || player.isDarkelf()) {
					htmlid = "barnia1";
				}
				break;
			case 9271: // 도리아
				if (player.getLevel() < 5){
					htmlid = "doria2";
				} else if (player.getLevel() >= 10 && player.getLevel() <= 44){
					if (player.isElf()){
						htmlid = "doria4";
					} else {
						htmlid = "doria1";
					}
				} else {
					htmlid = "doria";
				}
				break;
			case 50056://메트
				if (player.getLevel() < 45){//숨계
					htmlid = "telesilver4";
				} else if (player.getLevel() >= 99 && player.getLevel() <= 99){//폭풍수련지역
					htmlid = "telesilver5";
				} else {
					htmlid = "telesilver1";
				}
				break;
			case 50020: // 스텐리
			case 50024: // 아스터
			case 50036: // 윌마
			case 50039: // 레슬리
			case 50044: // 시리우스
			case 50046: // 엘레리스
			case 50051: // 키리우스
			case 50054: // 트레이
			case 50066: // 리올
				if (player.getLevel() < 45){
					htmlid = "starttel1";
				} else if (player.getLevel() >= 45 && player.getLevel() <= 51){
					htmlid = "starttel2";
				} else {
					htmlid = "starttel3";
				}
				break;
			case 5069: // 린지
				if (player.getLevel() < 45){
					htmlid = "linge1";
				} else if (player.getLevel() >= 45 && player.getLevel() <= 51){
					htmlid = "linge2";
				} else {
					htmlid = "linge3";
				}
				break;
			default:
				break;
			}
			// html 표시
			if (htmlid != null) { // htmlid가 지정되고 있는 경우
				player.sendPackets(new S_NPCTalkReturn(objid, htmlid));
			} else {
				if (player.getLawful() < -1000) { // 플레이어가 카오틱
					player.sendPackets(new S_NPCTalkReturn(talking, objid, 2));
				} else {
					player.sendPackets(new S_NPCTalkReturn(talking, objid, 1));
				}
			}
		} else {
			_log.finest((new StringBuilder())
					.append("No actions for npc id : ").append(objid)
					.toString());
		}
	}

	@Override
	public void onFinalAction(L1PcInstance player, String action) {
        if (this == null || player == null)
            return;
		int objid = getId();
		L1NpcTalkData talking = NPCTalkDataTable.getInstance().getTemplate(getNpcTemplate().get_npcId());
		if (action.equalsIgnoreCase("teleportURL")) {
			L1NpcHtml html = new L1NpcHtml(talking.getTeleportURL());
			String[] price = null;
			int npcid = getNpcTemplate().get_npcId();
			switch(npcid){
			case 50015: // 말하는섬 루카
				price = new String[]{"1500"};
				break;
			case 50017: // 말하는 섬 케이스
				price = new String[]{"50"};
				break;
			case 50020: // 켄트 스탠리
				price = new String[] { "50", "50", "120", "120", "50", "180", "120", "120", "180", "200", "200", "420", "600", "1155", "7100" };
				break;
			case 50024: // 글루디오 아스터
				price = new String[] { "132", "55", "198", "55", "132", "264", "55", "7777", "7777", "198", "264", "220", "220", "420", "550", "1155", "7480" };
				break;
			case 50036: // 기란 윌마
				price = new String[] { "126", "126", "52", "189", "52", "52", "189", "126", "126", "315", "315", "420", "735", "1155", "875" };
				break;
			case 50039:  // 웰던 레슬리
				price = new String[] { "185", "185", "123", "247", "51", "123", "247", "51", "185", "420", "412", "412", "824", "1155", "7931" };
				break;
			case 50044:  // 아덴 시리우스
			case 50046:  // 아덴 에레리스
				price = new String[]{ "259","129","194","129","54","324","194","259","420", "450", "540","540","972","1155", "7992" };
				break;
			case 50051: // 오렌키리우스
				price = new String[] { "240", "240", "180", "300", "120", "180", "300", "50", "240", "420", "500", "500", "900", "1155", "8000" };
				break;
			case 50054: // 윈다우드트레이
				price = new String[] { "50", "50", "120", "120", "180", "180", "180", "240", "240", "300", "200", "200", "420", "500", "6500" };
				break;
			case 50056:  // 은기사마을 메트
				price = new String[]{"55","55","55","132","132","132","198","198","270","7777","7777","246","420","770", "7480" };
				break;
			case 50066: // 하이네리올
				price = new String[] { "180", "50", "120", "120", "50", "50", "240", "120", "180", "420", "400", "400",	"800", "1155", "7100" };
				break;
			case 50068:  // 디아노스
				price = new String[] { "1500", "800", "600", "1800", "1800", "1000", "300" };  /////////////////////
				break;
			case 50072: // 공간이동사 디아루즈
				price = new String[] { "2200", "1800", "1000", "1600", "2200", "1200", "1300", "2000", "2000" };
				break;
			case 50073: // 공간이동사 디아베스 // 사용안함
				price = new String[] { "380", "850", "290", "290", "290", "180", "480", "150", "150", "380", "480", "380", "850" };
				break;
			case 50079: // 마법사 다니엘
				price = new String[] { "550", "550", "600", "550", "700", "600", "600", "750", "750", "550", "550", "700", "650" };
				break;
			case 3000005: // 데카비아 베히모스
				price = new String[] { "50", "50", "50", "50", "120", "120", "180", "180", "180", "240", "240", "400", "400", "800", "7700" };
				break;
			case 3100005: // 실베리아 샤리엘
				price = new String[] { "50", "50", "50", "120", "180", "180", "240", "240", "240", "300", "300", "500", "500", "900", "8000" };
				break;
			case 50026: // 그르딘 시장⇒기란 시장, 오렌 시장, 실버 나이트 타운 시장
				price = new String[]{ "0","0","0"};
				break;
			case 50033: // 기란 시장⇒그르딘 시장, 오렌 시장, 실버 나이트 타운 시장
				price = new String[]{ "0","0","0"};
				break;
			case 50049:  // 오렌 시장⇒그르딘 시장, 기란 시장, 실버 나이트 타운 시장
				price = new String[]{ "0","0","0"};
				break;
			case 50059:  // 실버 나이트 타운 시장⇒그르딘 시장, 기란 시장, 오렌 시장
				price = new String[]{ "0","0","0"};
				break;
			case 6000014:  // 시종장 맘몬
				price = new String[]{"14000"};
				break;
			case 6000016:  // 신녀 플로라
				price = new String[]{"1000"};	
				break;
			case 900056: //상아탑 피터
				price = new String[]{"7000","7000","7000","14000","14000"};
				break;
			case 5069: // 린지
				price = new String[]{ "82", "82", "82", "198", "198", "198", "198", "297", "297", "495", "495", "495", "1155", "12210" };
				break;
			case 900057:
				break;
			case 5091: // 엘루나 [ 요정의숲 텔레포터 ]
				price = new String[]{ "57", "57", "57", "138", "138", "138", "138", "207", "207", "230", "230", "690" };
				break;
			default:
				price = new String[]{""};
				break;
			}
			player.sendPackets(new S_NPCTalkReturn(objid, html, price));
		} else if (action.equalsIgnoreCase("teleportURLA")) {

			String html = "";
			String[] price = null;
			int npcid = getNpcTemplate().get_npcId();
			switch(npcid){
			case 50079:  // 다니엘
				html = "telediad3";
				price = new String[]{ "700","800","800","1000" };
				break;
			case 3000005: // 데카비아
				html = "dekabia3";
				price = new String[]{ "100","220","220","220","330","330","330","330","440","440" };
				break;
			case 3100005:  // 샤리엘
				html = "sharial";
				price = new String[]{ "220","330","330","330","440","440","550","550","550","550" };
				break;
			default:
				price = new String[]{""};
				break;
			}
			player.sendPackets(new S_NPCTalkReturn(objid, html, price));
		} else if (action.equalsIgnoreCase("teleportURLB")) {
			String html = "guide_1_1";
			String[] price = null;

			price = new String[]{ "450","450","450","450" };			

			player.sendPackets(new S_NPCTalkReturn(objid, html, price));
		} else if (action.equalsIgnoreCase("teleportURLC")) {
			String html = "guide_1_2";
			String[] price = null;

			price = new String[]{ "465","465","465","465","1065","1065" };			

			player.sendPackets(new S_NPCTalkReturn(objid, html, price));
		}
		else if (action.equalsIgnoreCase("teleportURLD")) {
			String html = "guide_1_3";
			String[] price = null;

			price = new String[]{ "480","480","480","480","630","1080","630" };			

			player.sendPackets(new S_NPCTalkReturn(objid, html, price));
		}
		else if (action.equalsIgnoreCase("teleportURLE")) {
			String html = "guide_2_1";
			String[] price = null;

			price = new String[]{ "600","600","750","750" };			

			player.sendPackets(new S_NPCTalkReturn(objid, html, price));
		}
		else if (action.equalsIgnoreCase("teleportURLF")) {
			String html = "guide_2_2";
			String[] price = null;

			price = new String[]{ "615","615","915","765" };			

			player.sendPackets(new S_NPCTalkReturn(objid, html, price));
		}
		else if (action.equalsIgnoreCase("teleportURLG")) {
			String html = "guide_2_3";
			String[] price = null;

			price = new String[]{ "630","780","630","1080","930" };			

			player.sendPackets(new S_NPCTalkReturn(objid, html, price));
		}
		else if (action.equalsIgnoreCase("teleportURLH")) {
			String html = "guide_3_1";
			String[] price = null;

			price = new String[]{ "750","750","750","1200","1050" };			

			player.sendPackets(new S_NPCTalkReturn(objid, html, price));
		}
		else if (action.equalsIgnoreCase("teleportURLI")) {
			String html = "guide_3_2";
			String[] price = null;

			price = new String[]{ "765","765","765","765","1515","1215","915" };			

			player.sendPackets(new S_NPCTalkReturn(objid, html, price));
		}
		else if (action.equalsIgnoreCase("teleportURLJ")) {
			String html = "guide_3_3";
			String[] price = null;

			price = new String[]{ "780","780","780","780","780","1230","1080" };			

			player.sendPackets(new S_NPCTalkReturn(objid, html, price));
		}
		else if (action.equalsIgnoreCase("teleportURLK")) {
			String html = "guide_4";
			String[] price = null;

			price = new String[]{ "780","780","780","780","780","1230","1080" };			

			player.sendPackets(new S_NPCTalkReturn(objid, html, price));
		}
		else if (action.equalsIgnoreCase("teleportURLL")){
			int npcid = getNpcTemplate().get_npcId();
			String html = "";
			String[] price = null;
			switch(npcid){
			case 50056: // 메트
				html = "guide_0_1";
				price = new String[]{ "30","30","30", "70", "80", "90","100", "30" };
				break;
			case 50020: // 스텐리
			case 50024: // 아스터
			case 50036: // 윌마
			case 50039: // 레슬리
			case 50044: // 시리우스
			case 50046: // 엘레리스
			case 50051: // 키리우스
			case 50054: // 트레이
			case 50066: // 리올
			case 5069:
				html = "guide_6";
				price = new String[]{ "500","500" };
				break;
			default:
				html = "telesilver3";
				price = new String[] { "780","780","780","780","780","1230","1080","1080","1080","1080" };
				break;
			}
			player.sendPackets(new S_NPCTalkReturn(objid, html, price));
		
		} else if (action.equalsIgnoreCase("teleportURLO")) { 
			   String html = "guide_8";
			   String[] price = null;
			   price = new String[]{ "750" };   
			   player.sendPackets(new S_NPCTalkReturn(objid, html, price));
			
		} else if (action.equalsIgnoreCase("teleportURLM")){
			int npcid = getNpcTemplate().get_npcId();
			String html = "";
			String[] price = null;
			switch(npcid){
			case 50056: // 메트
				html = "hp_storm1"; // 폭풍 수련 지역
				break;
			case 50020: // 스텐리
			case 50024: // 아스터
			case 50036: // 윌마
			case 50039: // 레슬리
			case 50044: // 시리우스
			case 50046: // 엘레리스
			case 50051: // 키리우스
			case 50054: // 트레이
			case 50066: // 리올
			case 5069:
				html = "guide_7";
				price = new String[]{ "500","500","500","500","500","500","500","500","500","500","500" };
				break;
			default:
				break;
			}
			player.sendPackets(new S_NPCTalkReturn(objid, html, price));
		}
		if (action.startsWith("teleport")) {
			_log.finest((new StringBuilder()).append("Setting action to : ")
					.append(action).toString());
			doFinalAction(player, action);
		}
	}

	private void doFinalAction(L1PcInstance player, String action) {
        if (this == null || player == null)
            return;
		int objid = getId();

		int npcid = getNpcTemplate().get_npcId();
		String htmlid = null;
		boolean isTeleport = true;

		if (npcid == 50014) { // 디 론
			if (!player.getInventory().checkItem(40581)) { // 안 데드의 키
				isTeleport = false;
				htmlid = "dilongn";
			}
		} else if (npcid == 50043) { // Lambda
			if (_isNowDely) { // 텔레포트 지연중
				isTeleport = false;
			}
		} else if (npcid == 50625) { // 고대인(Lv50 퀘스트 고대의 공간 2 F)
			if (_isNowDely) { // 텔레포트 지연중
				isTeleport = false;
			}
		}

		if (isTeleport) { // 텔레포트 실행
			try {
				//  뮤탄트안트단젼(군주 Lv30 퀘스트)
				if (action.equalsIgnoreCase("teleport mutant-dungen_la")) {
					// 3 매스 이내의 Pc
					for (L1PcInstance otherPc : L1World.getInstance()
							.getVisiblePlayer(player, 3)) {
						if (otherPc.getClanid() == player.getClanid()
								&& otherPc.getId() != player.getId()) {
							new L1Teleport().teleport(otherPc, 32740, 32800, (short) 217, 5,
									true);
						}
					}
					new L1Teleport().teleport(player, 32740, 32800, (short) 217, 5,
							true);
				}
				// 시련의 지하 감옥(위저드 Lv30 퀘스트)
				else if (action.equalsIgnoreCase("teleport mage-quest-dungen_la")) {
					new L1Teleport().teleport(player, 32791, 32788, (short) 201, 5,true);
				} else if (action.equalsIgnoreCase("teleport 29_la")) { // Lambda
					L1PcInstance kni = null;
					L1PcInstance elf = null;
					L1PcInstance wiz = null;
					// 3 매스 이내의 Pc
					L1Quest quest = null;
					for (L1PcInstance otherPc : L1World.getInstance()
							.getVisiblePlayer(player, 3)) {
						quest = otherPc.getQuest();
						if (otherPc.isKnight() // 나이트
								&& quest.get_step(L1Quest.QUEST_LEVEL50) == 1) { // 디가르딘 동의가 끝난 상태
							if (kni == null) {
								kni = otherPc;
							}
						} else if (otherPc.isElf() // 요정
								&& quest.get_step(L1Quest.QUEST_LEVEL50) == 1) { // 디가르딘 동의가 끝난 상태
							if (elf == null) {
								elf = otherPc;
							}
						} else if (otherPc.isWizard() // 마법사
								&& quest.get_step(L1Quest.QUEST_LEVEL50) == 1) { // 디가르딘 동의가 끝난 상태
							if (wiz == null) {
								wiz = otherPc;
							}
						}
					}
					if (kni != null && elf != null && wiz != null) { // 전클래스 갖추어져 있다
						new L1Teleport().teleport(player, 32723, 32850, (short) 2000,	2, true);
						new L1Teleport().teleport(kni, 32750, 32851, (short) 2000, 6,	true);
						new L1Teleport().teleport(elf, 32878, 32980, (short) 2000, 6,	true);
						new L1Teleport().teleport(wiz, 32876, 33003, (short) 2000, 0,	true);
						_isNowDely = true;
						TeleportDelyTimer timer = new TeleportDelyTimer();
						GeneralThreadPool.getInstance().schedule(timer, 900000);
					}
				} else if (action.equalsIgnoreCase("teleport barlog_la")) { // 고대인(Lv50 퀘스트 고대의 공간 2 F)
					new L1Teleport().teleport(player, 32755, 32844, (short) 2002, 5, true);
					TeleportDelyTimer timer = new TeleportDelyTimer();
					GeneralThreadPool.getInstance().execute(timer);

				} else if (action.equalsIgnoreCase("teleport kentc-girdun")){ //켄트성 - 기감 2층
					if (player.getGirandungeonTime() >= 120){
						player.sendPackets(new S_ChatPacket(player, "기란 감옥 시간이 만료 되었습니다."));//켄트성 - 기란감옥 2층
						return;
					} else {
						new L1Teleport().teleport(player, 32809, 32793, (short) 54, 0, true);						
					}
				} else if (action.equalsIgnoreCase("teleport giranD")){ //기란 던전
					if (player.getGirandungeonTime() >= 120){
						player.sendPackets(new S_ChatPacket(player, "기란 감옥 시간이 만료 되었습니다."));
						return;
					} else {
						new L1Teleport().teleport(player, 32806, 32732, (short) 53, 0, true);						
					}
/*				} else if (action.equalsIgnoreCase("teleport ivorytower")) { //피터 발록진영
					if (player.getnewdodungeonTime() < 60) {
						player.getInventory().consumeItem(40308, 14000);
						new L1Teleport().teleport(player, 32936, 32804, (short) 280, 0, true);
					} else {
						player.sendPackets(new S_SystemMessage("\\aA경고: \\aG[발록진영]\\aA 던전 시간이 만료되었습니다."));
						return;
					}	
				} else if (action.equalsIgnoreCase("teleport ivorytower4")) { //피터 발록진영4층
					if (player.getInventory().checkItem(40308, 14000)) {
						if (player.getnewdodungeonTime() < 60) {
							player.getInventory().consumeItem(40308, 14000);
							new L1Teleport().teleport(player, 32936, 32804, (short) 280, 0, true);
						} else {
							player.sendPackets(new S_SystemMessage("\\aA경고: \\aG[발록진영]\\aA 던전 시간이 만료되었습니다."));
							return;
						}
					} else {
						player.sendPackets(new S_SystemMessage("아데나가 부족합니다."));
					}
				} else if (action.equalsIgnoreCase("teleport ivorytower7")){ //피터 발록진영7층
					if (player.getInventory().checkItem(40308, 14000)) {
						if (player.getnewdodungeonTime() < 60) {
							player.getInventory().consumeItem(40308, 14000);
							new L1Teleport().teleport(player, 32737, 32796, (short) 283, 0, true);		
						} else {
							player.sendPackets(new S_SystemMessage("\\aA경고: \\aG[발록진영]\\aA 던전 시간이 만료되었습니다."));
							return;
						}
					} else {
						player.sendPackets(new S_SystemMessage("아데나가 부족합니다."));
					}	*/
				}			

			} catch (Exception e) {
			}
		}
		if (htmlid != null) { // 표시하는 html가 있는 경우
			player.sendPackets(new S_NPCTalkReturn(objid, htmlid));
		}
	}

	class TeleportDelyTimer implements Runnable {

		public TeleportDelyTimer() {

		}

		public void run() {
			_isNowDely = false;
		}
	}

	private boolean _isNowDely = false;

	private static Logger _log = Logger.getLogger(l1j.server.server.model.Instance.L1TeleporterInstance.class.getName());

}