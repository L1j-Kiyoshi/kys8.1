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
			case 50014: // ディロン
				if (player.isWizard()) { // ウィザード
					if (quest.get_step(L1Quest.QUEST_LEVEL30) == 1
							&& !player.getInventory().checkItem(40579)) { // アンデッドの骨
						htmlid = "dilong1";
					} else {
						htmlid = "dilong3";
					}
				}
				break;
			case 70779: //ゲートアント
				if (player.getTempCharGfx() == 1037) { // ジャイアントアント変身
					htmlid = "ants3";
				} else if (player.getTempCharGfx() == 1039) {// ジャイアントアントソールじゃ変身
					if (player.isCrown()) { // 君主
						if (quest.get_step(L1Quest.QUEST_LEVEL30) == 1) {
							if (player.getInventory().checkItem(40547)) { // 住民の遺品
								htmlid = "antsn";
							} else {
								htmlid = "ants1";
							}
						} else { // Step1以外
							htmlid = "antsn";
						}
					} else { // 君主以外
						htmlid = "antsn";
					}
				}
				break;
			case 70853: // フェアリープリンセス
				if (player.isElf()) { //エルフ
					if (quest.get_step(L1Quest.QUEST_LEVEL30) == 1) {
						if (!player.getInventory().checkItem(40592)) { // 呪われた精霊で
							Random random = new Random(System.nanoTime());
							if (random.nextInt(100) < 50) { // 50％でダーク・マルケスダンジョン
								htmlid = "fairyp2";
							} else { // ダークエルフの地下牢
								htmlid = "fairyp1";
							}
						}
					}
				}
				break;
			case 50031: // セピア
				if (player.isElf()) { // エルフ
					if (quest.get_step(L1Quest.QUEST_LEVEL45) == 2) {
						if (!player.getInventory().checkItem(40602)) { //ブルーフルート
							htmlid = "sepia1";
						}
					}
				}
				break;
			case 50043:
				if (quest.get_step(L1Quest.QUEST_LEVEL50) == L1Quest.QUEST_END) {
					htmlid = "ramuda2";
				} else if (quest.get_step(L1Quest.QUEST_LEVEL50) == 1) { // ディ教えディーン同意済み
					if (player.isCrown()) { // 君主
						if (_isNowDely) { // テレポート遅延中
							htmlid = "ramuda4";
						} else {
							htmlid = "ramudap1";
						}
					} else { // 君主以外
						htmlid = "ramuda1";
					}
				} else {
					htmlid = "ramuda3";
				}
				break;
			case 50082: // 歌う島テレポーター
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
			case 50001: // バルニア
				if (player.isElf()) {
					htmlid = "barnia3";
				} else if (player.isKnight() || player.isCrown() || player.isWarrior()) {
					htmlid = "barnia2";
				} else if (player.isWizard() || player.isDarkelf()) {
					htmlid = "barnia1";
				}
				break;
			case 9271: //ドリア
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
			case 50056://メット
				if (player.getLevel() < 45){//スムギェ
					htmlid = "telesilver4";
				} else if (player.getLevel() >= 99 && player.getLevel() <= 99){//嵐修練地域
					htmlid = "telesilver5";
				} else {
					htmlid = "telesilver1";
				}
				break;
			case 50020: // スタンレー
			case 50024: // アスター
			case 50036: // ウィルマ
			case 50039: // レスリー
			case 50044: // シリウス
			case 50046: // エレリース
			case 50051: // キーウス
			case 50054: // トレイ
			case 50066: // リオル
				if (player.getLevel() < 45){
					htmlid = "starttel1";
				} else if (player.getLevel() >= 45 && player.getLevel() <= 51){
					htmlid = "starttel2";
				} else {
					htmlid = "starttel3";
				}
				break;
			case 5069: // リンジー
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
			// html表示
			if (htmlid != null) { // htmlidが指定されている場合、
				player.sendPackets(new S_NPCTalkReturn(objid, htmlid));
			} else {
				if (player.getLawful() < -1000) { // プレイヤーがカオティック
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
			case 50015: // 話せる島ルカ
				price = new String[]{"1500"};
				break;
			case 50017: // 話せる島のケース
				price = new String[]{"50"};
				break;
			case 50020: // ケントスタンレー
				price = new String[] { "50", "50", "120", "120", "50", "180", "120", "120", "180", "200", "200", "420", "600", "1155", "7100" };
				break;
			case 50024: // グルーディオアスター
				price = new String[] { "132", "55", "198", "55", "132", "264", "55", "7777", "7777", "198", "264", "220", "220", "420", "550", "1155", "7480" };
				break;
			case 50036: // ギランウィルマ
				price = new String[] { "126", "126", "52", "189", "52", "52", "189", "126", "126", "315", "315", "420", "735", "1155", "875" };
				break;
			case 50039:  // ウェルダンレスリー
				price = new String[] { "185", "185", "123", "247", "51", "123", "247", "51", "185", "420", "412", "412", "824", "1155", "7931" };
				break;
			case 50044:  // アデンシリウス
			case 50046:  // アデンエレリース
				price = new String[]{ "259","129","194","129","54","324","194","259","420", "450", "540","540","972","1155", "7992" };
				break;
			case 50051: // オーレンキーウス
				price = new String[] { "240", "240", "180", "300", "120", "180", "300", "50", "240", "420", "500", "500", "900", "1155", "8000" };
				break;
			case 50054: // ウィンダウッドトレイ
				price = new String[] { "50", "50", "120", "120", "180", "180", "180", "240", "240", "300", "200", "200", "420", "500", "6500" };
				break;
			case 50056:  // はナイトの村メット
				price = new String[]{"55","55","55","132","132","132","198","198","270","7777","7777","246","420","770", "7480" };
				break;
			case 50066: // ハイネリオール
				price = new String[] { "180", "50", "120", "120", "50", "50", "240", "120", "180", "420", "400", "400",	"800", "1155", "7100" };
				break;
			case 50068:  // ディアノース
				price = new String[] { "1500", "800", "600", "1800", "1800", "1000", "300" };  /////////////////////
				break;
			case 50072: // スペースが同社ディアルージュ
				price = new String[] { "2200", "1800", "1000", "1600", "2200", "1200", "1300", "2000", "2000" };
				break;
			case 50073: // スペースが同社ディアベス//を使用しない
				price = new String[] { "380", "850", "290", "290", "290", "180", "480", "150", "150", "380", "480", "380", "850" };
				break;
			case 50079: // ウィザードダニエル
				price = new String[] { "550", "550", "600", "550", "700", "600", "600", "750", "750", "550", "550", "700", "650" };
				break;
			case 3000005: // デカビアベヒーモス
				price = new String[] { "50", "50", "50", "50", "120", "120", "180", "180", "180", "240", "240", "400", "400", "800", "7700" };
				break;
			case 3100005: // シルベリアシャリエル
				price = new String[] { "50", "50", "50", "120", "180", "180", "240", "240", "240", "300", "300", "500", "500", "900", "8000" };
				break;
			case 50026: // グルディーン市場⇒ギラン市場、オーレン市場、シルバーナイトタウンの市場
				price = new String[]{ "0","0","0"};
				break;
			case 50033: // ギラン市場⇒グルディーン市場、オーレン市場、シルバーナイトタウンの市場
				price = new String[]{ "0","0","0"};
				break;
			case 50049:  // オーレン市場⇒グルディーン市場、ギラン市場、シルバーナイトタウンの市場
				price = new String[]{ "0","0","0"};
				break;
			case 50059:  // シルバーナイトタウンの市場⇒グルディーン市場、ギラン市場、オーレン市場
				price = new String[]{ "0","0","0"};
				break;
			case 6000014:  //侍従長マンモン
				price = new String[]{"14000"};
				break;
			case 6000016:  // 信女フローラ
				price = new String[]{"1000"};	
				break;
			case 900056: //象牙の塔ピーター
				price = new String[]{"7000","7000","7000","14000","14000"};
				break;
			case 5069: // リンジー
				price = new String[]{ "82", "82", "82", "198", "198", "198", "198", "297", "297", "495", "495", "495", "1155", "12210" };
				break;
			case 900057:
				break;
			case 5091: // エルルナ[妖精の森テレポーター]
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
			case 50079:  // ダニエル
				html = "telediad3";
				price = new String[]{ "700","800","800","1000" };
				break;
			case 3000005: // デカビア
				html = "dekabia3";
				price = new String[]{ "100","220","220","220","330","330","330","330","440","440" };
				break;
			case 3100005:  // シャリエル
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
			case 50056: // メット
				html = "guide_0_1";
				price = new String[]{ "30","30","30", "70", "80", "90","100", "30" };
				break;
			case 50020: // スタンレー
			case 50024: // アスター
			case 50036: // ウィルマ
			case 50039: // レスリー
			case 50044: // シリウス
			case 50046: // エレリース
			case 50051: // キーウス
			case 50054: // トレイ
			case 50066: // リオル
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
			case 50056: // メット
				html = "hp_storm1"; // 嵐修練地域
				break;
			case 50020: // スタンレー
			case 50024: // アスター
			case 50036: // ウィルマ
			case 50039: // レスリー
			case 50044: // シリウス
			case 50046: // エレリース
			case 50051: // キーウス
			case 50054: // トレイ
			case 50066: // リオル
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

		if (npcid == 50014) { // ディローン
			if (!player.getInventory().checkItem(40581)) { // アンデッドのキー
				isTeleport = false;
				htmlid = "dilongn";
			}
		} else if (npcid == 50043) { // Lambda
			if (_isNowDely) { // テレポート遅延中
				isTeleport = false;
			}
		} else if (npcid == 50625) { // 古代人（Lv50クエスト古代の空間2 F）
			if (_isNowDely) { // テレポート遅延中
				isTeleport = false;
			}
		}

		if (isTeleport) { // テレポートの実行
			try {
				//  ミュータントアントダンジョン（君主Lv30クエスト）
				if (action.equalsIgnoreCase("teleport mutant-dungen_la")) {
					// 3マス以内のPc
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
				// 試練のダンジョン（ウィザードLv30クエスト）
				else if (action.equalsIgnoreCase("teleport mage-quest-dungen_la")) {
					new L1Teleport().teleport(player, 32791, 32788, (short) 201, 5,true);
				} else if (action.equalsIgnoreCase("teleport 29_la")) { // Lambda
					L1PcInstance kni = null;
					L1PcInstance elf = null;
					L1PcInstance wiz = null;
					// 3マス以内のPc
					L1Quest quest = null;
					for (L1PcInstance otherPc : L1World.getInstance()
							.getVisiblePlayer(player, 3)) {
						quest = otherPc.getQuest();
						if (otherPc.isKnight() // ナイト
								&& quest.get_step(L1Quest.QUEST_LEVEL50) == 1) { //ディ教えディーン同意済み
							if (kni == null) {
								kni = otherPc;
							}
						} else if (otherPc.isElf() // 妖精
								&& quest.get_step(L1Quest.QUEST_LEVEL50) == 1) { // ディ教えディーン同意済み
							if (elf == null) {
								elf = otherPc;
							}
						} else if (otherPc.isWizard() // ウィザード
								&& quest.get_step(L1Quest.QUEST_LEVEL50) == 1) { // ディ教えディーン同意済み
							if (wiz == null) {
								wiz = otherPc;
							}
						}
					}
					if (kni != null && elf != null && wiz != null) { // 前のクラス揃っている
						new L1Teleport().teleport(player, 32723, 32850, (short) 2000,	2, true);
						new L1Teleport().teleport(kni, 32750, 32851, (short) 2000, 6,	true);
						new L1Teleport().teleport(elf, 32878, 32980, (short) 2000, 6,	true);
						new L1Teleport().teleport(wiz, 32876, 33003, (short) 2000, 0,	true);
						_isNowDely = true;
						TeleportDelyTimer timer = new TeleportDelyTimer();
						GeneralThreadPool.getInstance().schedule(timer, 900000);
					}
				} else if (action.equalsIgnoreCase("teleport barlog_la")) { // 古代人（Lv50クエスト古代の空間2 F）
					new L1Teleport().teleport(player, 32755, 32844, (short) 2002, 5, true);
					TeleportDelyTimer timer = new TeleportDelyTimer();
					GeneralThreadPool.getInstance().execute(timer);

				} else if (action.equalsIgnoreCase("teleport kentc-girdun")){ //ケント城 - 技監2階
					if (player.getGirandungeonTime() >= 120){
						player.sendPackets(new S_ChatPacket(player, "ギラン監獄時間が経過しました。"));//ケント城 - ギラン監獄2階
						return;
					} else {
						new L1Teleport().teleport(player, 32809, 32793, (short) 54, 0, true);						
					}
				} else if (action.equalsIgnoreCase("teleport giranD")){ //ギランダンジョン
					if (player.getGirandungeonTime() >= 120){
						player.sendPackets(new S_ChatPacket(player, "ギラン監獄時間が経過しました。"));
						return;
					} else {
						new L1Teleport().teleport(player, 32806, 32732, (short) 53, 0, true);						
					}
				}			

			} catch (Exception e) {
			}
		}
		if (htmlid != null) { // 表示するhtmlがある場合
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