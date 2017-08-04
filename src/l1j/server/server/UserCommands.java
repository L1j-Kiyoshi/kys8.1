package l1j.server.server;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.RobotSystem.RobotAIThread;
import l1j.server.server.datatables.CharacterTable;
//import l1j.server.server.model.CharPosUtil;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_Ability;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_HPMeter;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_OwnCharStatus2;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ReturnedStat;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_Unknown2;
import l1j.server.server.serverpackets.S_UserCommands4;
import l1j.server.server.serverpackets.S_UserCommands5;
import l1j.server.server.serverpackets.S_War;
import l1j.server.server.utils.SQLUtil;
import manager.LinAllManager;

public class UserCommands {

	private static Logger _log = Logger.getLogger(UserCommands.class.getName());

	boolean spawnTF = false;

	private static UserCommands _instance;

	private static Random _random = new Random(System.nanoTime());

	private UserCommands() {
	}

	public static UserCommands getInstance() {
		if (_instance == null) {
			_instance = new UserCommands();
		}
		return _instance;
	}

	/**
	 * @param pc
	 * @param cmdLine
	 */
	public void handleCommands(L1PcInstance pc, String cmdLine) {
		if (pc == null) {
			return;
		}
		// System.out.println(cmdLine);
		StringTokenizer token = new StringTokenizer(cmdLine);
		// System.out.println(token.hasMoreTokens());
		String cmd = "";
		if (token.hasMoreTokens())
			cmd = token.nextToken();
		else
			cmd = cmdLine;
		String param = "";
		// System.out.println(cmd);

		while (token.hasMoreTokens()) {
			param = new StringBuilder(param).append(token.nextToken()).append(' ').toString();
		}
		param = param.trim();
		try {
			switch (cmd) {
			case "help":
				showHelp(pc);
				break;
			case "tellag":
			case ".":
				tell(pc);
			case "autoclanjoin":
				autoclanjoin(pc);
				break;
			case "info":
				check(pc);
				break;
			case "statusreset":
				statusInitialization(pc);
				break;
			case "location":
				location(pc);
				break;
			case "age":
				age(pc, param);
				break;
			case "hunt":
				Hunt(pc, param);
				break;
			case "clanpt":
				BloodParty(pc);
				break;
			case "secure":
				changequiz(pc, param);
				break;
			case "unsecure":
				validateQuiz(pc, param);
				break;
			case "changepass":
				changepassword(pc, param);
				break;
			case "dropcomment":
			case "comment":
				Ment(pc, param);
				break;
			case "privateshop":
				privateShop(pc);
				break;
			case "privateshop1":
				privateShop1(pc);
				break;
			case "namechange":
			case "changename":
				changename(pc, param);
			case "light":
			case "maphack":
				maphack(pc, param);
				break;
			case "phone":
				phone(pc, param);
				break;
			case "mark1":
				Mark1(pc, param);
				break;
			case "doll":
				POPall(pc);
				break;
			case "killranking":
				pc.sendPackets(new S_UserCommands4(pc, 1));
				break;
			case "deathranking":
				pc.sendPackets(new S_UserCommands5(pc, 1));
				break;
			case "piva":
				execute(pc, param, param);
				break;
			case "abysspoint":
				String grade = "";

				switch (pc.getPeerage()) {
				case 0:
					grade = "見習い";
					break;
				case 1:
					grade = "9等兵";
					break;
				case 2:
					grade = "8等兵";
					break;
				case 3:
					grade = "7等兵";
					break;
				case 4:
					grade = "6等兵";
					break;
				case 5:
					grade = "5等兵";
					break;
				case 6:
					grade = "4等兵";
					break;
				case 7:
					grade = "3等兵";
					break;
				case 8:
					grade = "2等兵";
					break;
				case 9:
					grade = "1等兵";
					break;
				case 10:
					grade = "1将校";
					break;
				case 11:
					grade = "2将校";
					break;
				case 12:
					grade = "3将校";
					break;
				case 13:
					grade = "4将校";
					break;
				case 14:
					grade = "5将校";
					break;
				case 15:
					grade = "将軍";
					break;
				case 16:
					grade = "大将軍";
					break;
				case 17:
					grade = "司令官";
					break;
				case 18:
					grade = "総司令官";
					break;
				}
				int point = pc.getAbysspoint();
				pc.sendPackets(new S_SystemMessage(
						"[" + pc.getName() + "] さんのアビスポイントは " + point + "点" + " 階級は : " + grade + " です."));
				break;
			default:
				pc.sendPackets(new S_SystemMessage("そのようなコマンド " + cmd + " は存在しません。 "));
				break;

			/*
			 * } else if (cmd.equalsIgnoreCase("友好度")) { describe(pc);
			 */
			/*
			 * } else if (cmd.equalsIgnoreCase("アビスポイント")) {
			 */
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}

	private void execute(L1PcInstance pc, String param) {
		// TODO Auto-generated method stub

	}

	private void showHelp(L1PcInstance pc) {
		pc.sendPackets(new S_SystemMessage("\\aH===========< User Commands >==========="));
		pc.sendPackets(new S_SystemMessage("\\aA    .情報。テルレク固定適用。座標復元。血盟パーティー "));
		pc.sendPackets(new S_SystemMessage("\\aA    .パスワードの変更セキュリティ設定セキュリティを解除。デスランキング。キールランキング "));
		pc.sendPackets(new S_SystemMessage("\\aA    .無人登録名前変更します。無人店。ライト。年齢"));
		pc.sendPackets(new S_SystemMessage("\\aA    .ヒョルマク。ドロップメント（コメント）。手配人形情報"));
		pc.sendPackets(new S_SystemMessage("\\aH=========< Have a Good Time >================"));
	}

	private void Ment(L1PcInstance pc, String param) {
		if (param.equalsIgnoreCase("off")) {
			pc.sendPackets(new S_ChatPacket(pc, "アイテム獲得メント -  OFF  - "));
			pc.RootMent = false;
		} else if (param.equalsIgnoreCase("on")) {
			pc.sendPackets(new S_ChatPacket(pc, "アイテム獲得メント -  ON  - "));
			pc.RootMent = true;
		} else {
			pc.sendPackets(new S_ChatPacket(pc, ".ドロップコメント [オン/オフ]中に入力（アイテム獲得コメント設定）"));
		}
	}

	private void autoclanjoin(L1PcInstance pc) {
		try {
			// 失敗条件

			if (pc.getClanRank() != 10) {
				pc.sendPackets(new S_ServerMessage(92, pc.getName())); // \f1%0はプリンスやプリンセスがありません。
				return;
			}
			if (pc.isFishing()) {
				pc.sendPackets(new S_SystemMessage("釣りの時は、行動が制限されます。"));
				return;
			}
			if (pc.getClanid() == 0 || pc.getClanid() == 1) {// 新規血名
				pc.sendPackets(new S_SystemMessage("血盟創設状態がありません。"));
				return;
			}
			if (pc.isPrivateShop()) {
				pc.sendPackets(new S_SystemMessage("個人商店の中に使用することができません。"));
				return;
			}
			if (pc.isPinkName() || pc.isParalyzed() || pc.isSleeped()) {
				pc.sendPackets(new S_SystemMessage("見よ中麻痺の潜水中は使用できません。"));
				return;
			}
			if (pc.isDead()) {
				pc.sendPackets(new S_SystemMessage("死んだ状態で実行することができません。"));
				return;
			}

			// 失敗条件
			// ギラン旅館前
			if (pc.getX() >= 33426 && pc.getX() <= 33435 && pc.getY() >= 32795 && pc.getY() <= 32802
					&& pc.getMapId() == 4) {
				for (L1PcInstance target : L1World.getInstance().getAllPlayers3()) {
					if (target.getId() != pc.getId()
							&& target.getAccountName().toLowerCase().equals(pc.getAccountName().toLowerCase())
							&& target.isAutoClanjoin()) {
						pc.sendPackets(new S_ChatPacket(pc, "既にあなたの補助キャラクターが無人登録状態です。"));
						return;
					}
				}
				pc.setAutoClanjoin(true);
				L1PolyMorph.undoPolyAutoClanjoin(pc);
				LinAllManager.getInstance().LogLogOutAppend(pc.getName(), pc.getNetConnection().getHostname());
				GameClient client = pc.getNetConnection();
				pc.setNetConnection(null);
				try {
					pc.save();
					pc.saveInventory();
				} catch (Exception e) {
				}
				client.setActiveChar(null);
				client.setLoginAvailable();
				client.CharReStart(true);
				client.sendPacket(new S_Unknown2(1)); // リースボタンのための構造を変更する //
														// Episode U
			} else {
				pc.sendPackets(new S_ChatPacket(pc, "ギラン村旅館の前のスペースでのみ使用することができます。"));
			}
		} catch (Exception e) {
			System.out.println(pc.getName() + "無人登録処理エラー");
		}
	}

	public void execute(L1PcInstance pc, String cmdName, String arg) {
		if (arg.equalsIgnoreCase("on")) {
			pc.setSkillEffect(L1SkillId.GMSTATUS_HPBAR, 0);
		} else if (arg.equalsIgnoreCase("off")) {
			pc.removeSkillEffect(L1SkillId.GMSTATUS_HPBAR);

			for (L1Object obj : pc.getKnownObjects()) {
				if (isHpBarTarget(obj)) {
					pc.sendPackets(new S_HPMeter(obj.getId(), 0xFF, 0xff));
				}
			}
		} else {
			pc.sendPackets(new S_SystemMessage(cmdName + "[入、切]と入力してください。"));
		}
	}

	private void phone(L1PcInstance pc, String param) {
		try {
			long curtime = System.currentTimeMillis() / 1000;
			if (pc.getQuizTime() + 10 > curtime) {
				long sec = (pc.getQuizTime() + 10) - curtime;
				pc.sendPackets(new S_SystemMessage(sec + "秒後に使用することができます。"));
				return;
			}
			StringTokenizer tok = new StringTokenizer(param);
			String phone = tok.nextToken();
			Account account = Account.load(pc.getAccountName());
			if (param.length() < 10) {
				pc.sendPackets(new S_ChatPacket(pc, "ない番号です。再度入力してください。", 1));
				return;
			}
			if (param.length() > 11) {
				pc.sendPackets(new S_ChatPacket(pc, "不適切番号です。再度入力してください。"));
				return;
			}
			if (isDisitAlpha(phone) == false) {
				pc.sendPackets(new S_ChatPacket("数字のみを入力してください。"));
				return;
			}
			if (account.getphone() != null) {
				pc.sendPackets(new S_ChatPacket(pc, "すでに電話番号が設定されています。"));
				pc.sendPackets(new S_ChatPacket(pc, "番号変更時メティスに手紙で連絡先を送ってください。"));
				return;
			}
			account.setphone(phone);
			Account.updatePhone(account);
			pc.sendPackets(new S_ChatPacket(pc, " " + phone + "設定完了。初期化時に文字発送されます。"));
			pc.sendPackets(new S_PacketBox(pc, S_PacketBox.ICON_SECURITY_SERVICES));
			pc.sendPackets(new S_ChatPacket(pc, "セキュリティバフ（AC-1）は、リースと適用されます"));
			pc.setQuizTime(curtime);
		} catch (Exception e) {
			pc.sendPackets(new S_ChatPacket(pc, ".固定申し込み コンタクトの形式で入力（初期化する場合にのみ文字の転送）"));
		}
	}

	private void initStatus(L1PcInstance pc) {
		L1SkillUse l1skilluse = new L1SkillUse();
		l1skilluse.handleCommands(pc, L1SkillId.CANCELLATION, pc.getId(), pc.getX(), pc.getY(), null, 0,
				L1SkillUse.TYPE_LOGIN);

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
			System.out.println("ステータス初期化コマンドエラー");
		}
	}

	private void statusInitialization(L1PcInstance pc) {
		try {
			long curtime = System.currentTimeMillis() / 1000;
			if (pc.getQuizTime() + 10 > curtime) {
				pc.sendPackets(new S_SystemMessage("10秒間の遅延時間が必要です。"));
				return;
			}
			if (!pc.getMap().isSafetyZone(pc.getLocation())) {
				pc.sendPackets(new S_ChatPacket(pc, "安全な地域でのみ使用することができます。"));
				return;
			}
			if (pc.getInventory().checkItem(200000, 1)) {
				if (pc.getLevel() != pc.getHighLevel()) {
					pc.sendPackets(new S_SystemMessage("レベルがダウンしたキャラクターです。レベルアップした後ご利用下さい。"));
					return;
				}
				if (pc.getLevel() > 54) {
					pc.getInventory().consumeItem(200000, 1);
					new L1Teleport().teleport(pc, 32723 + _random.nextInt(10), 32851 + _random.nextInt(10),
							(short) 5166, 5, true);
					initStatus(pc);
				} else {
					pc.sendPackets(new S_SystemMessage("ステータス初期化は、レベル55以上のみ可能です。"));
				}
			} else {
				pc.sendPackets(new S_SystemMessage("回想のロウソクがありません。"));
				return;
			}

			pc.setQuizTime(curtime);
		} catch (Exception e) {
		}
	}

	private void check(L1PcInstance pc) {
		try {
			long curtime = System.currentTimeMillis() / 1000;
			if (pc.getQuizTime() + 10 > curtime) {
				pc.sendPackets(new S_SystemMessage("10秒間の遅延時間が必要です。"));
				return;
			}
			int hpr = pc.getHpr() + pc.getInventory().hpRegenPerTick();
			int mpr = pc.getMpr() + pc.getInventory().mpRegenPerTick();

			pc.sendPackets(new S_SystemMessage("===================( 私の情報 )===================="));
			pc.sendPackets(new S_SystemMessage("\\aD(ピチク: " + hpr + ')' + "(エムチク: " + mpr + ')' + "(PK回数: "
					+ pc.get_PKcount() + ')' + "(エルリック: " + pc.getElixirStats() + "本)"));
			pc.sendPackets(new S_SystemMessage("===================================================="));
			pc.setQuizTime(curtime);
		} catch (Exception e) {
		}
	}

	private void maphack(L1PcInstance pc, String param) {
		try {
			StringTokenizer st = new StringTokenizer(param);
			String on = st.nextToken();
			if (on.equalsIgnoreCase("on")) {
				pc.sendPackets(new S_Ability(3, true));
				pc.sendPackets(new S_SystemMessage("\\aAコマンド：ライトを \\aG[開始]\\aA しました。"));
			} else if (on.equals("off")) {
				pc.sendPackets(new S_Ability(3, false));
				pc.sendPackets(new S_SystemMessage("\\aAコマンド：ライトを \\aG[終了]\\aA しました。"));
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage("\\aAコマンド: .ライト \\aG[オン、オフ]"));
		}
	}

	private void location(L1PcInstance pc) {
		try {
			long curtime = System.currentTimeMillis() / 1000;
			if (pc.getQuizTime2() + 20 > curtime) {
				long time = (pc.getQuizTime2() + 20) - curtime;
				pc.sendPackets(new S_ChatPacket(pc, time + "秒後に使用することができます。"));
				return;
			}
			Connection connection = null;
			connection = L1DatabaseFactory.getInstance().getConnection();
			PreparedStatement preparedstatement = connection.prepareStatement(
					"UPDATE characters SET LocX=33432,LocY=32807,MapID=4 WHERE account_name=? and MapID not in (5001,99,997,5166,39,34,701,2000)"); //オペレータの部屋、刑務所、バトルゾーン控室
																																					// を除く
			preparedstatement.setString(1, pc.getAccountName());
			preparedstatement.execute();
			preparedstatement.close();
			connection.close();
			pc.sendPackets(new S_SystemMessage("アカウント内のすべてのキャラクターの座標がギラン村に移動されました"));

			pc.setQuizTime(curtime);
		} catch (Exception e) {
		}
	}

	private void tell(L1PcInstance pc) {

		long curtime = System.currentTimeMillis() / 1000;
		if (pc.getQuizTime2() + 20 > curtime) {
			long time = (pc.getQuizTime2() + 20) - curtime;
			pc.sendPackets(new S_ChatPacket(pc, time + "秒後に使用することができます。"));
			return;
		}
		try {
			if (pc.getMapId() == 781) {
				if (pc.getLocation().getX() <= 32998 && pc.getLocation().getX() >= 32988
						&& pc.getLocation().getY() <= 32758 && pc.getLocation().getY() >= 32736) {
					pc.sendPackets(new S_SystemMessage("使用することができない場所です。"));
					return;
				}
			}
			if (pc.isPinkName() || pc.isDead() || pc.isParalyzed() || pc.isSleeped() || pc.getMapId() == 800
					|| pc.getMapId() == 5302 || pc.getMapId() == 5153 || pc.getMapId() == 5490) {
				pc.sendPackets(new S_SystemMessage("使用できない状態です。"));
				return;
			}
			new L1Teleport().teleport(pc, pc.getX(), pc.getY(), pc.getMapId(), pc.getHeading(), false);
			pc.update_lastLocalTellTime();
			pc.setQuizTime2(curtime);
		} catch (Exception exception35) {
		}
	}

	private void Mark1(L1PcInstance pc, String param) {
		long curtime = System.currentTimeMillis() / 1000;
		if (pc.getQuizTime() + 30 > curtime) {
			long time = (pc.getQuizTime() + 30) - curtime;
			pc.sendPackets(new S_ChatPacket(pc, time + "秒後に使用することができます。"));
			return;
		}
		if (pc.isDead()) {
			pc.sendPackets(new S_SystemMessage("死んだ状態で使用することができません。"));
			return;
		}
		int i = 1;
		if (pc.watchCrest) {
			i = 3;
			pc.watchCrest = false;
		} else
			pc.watchCrest = true;
		for (L1Clan clan : L1World.getInstance().getAllClans()) {
			if (clan != null) {
				pc.sendPackets(new S_War(i, pc.getClanname(), clan.getClanName()));
			}
		}
		pc.setQuizTime(curtime);

	}

	public void BloodParty(L1PcInstance pc) {
		if (pc.isDead()) {
			pc.sendPackets(new S_SystemMessage("死んだ状態で使用することができません。"));
			return;
		}
		int ClanId = pc.getClanid();
		if (ClanId != 0 && pc.getClanRank() == L1Clan.MONARCH || pc.getClanRank() == L1Clan.GUARDIAN
				|| pc.getClanRank() == L1Clan.SUB_MONARCH) {
			for (L1PcInstance SearchBlood : L1World.getInstance().getAllPlayers()) {
				if (SearchBlood.getClanid() != ClanId || SearchBlood.isPrivateShop() || SearchBlood.isAutoClanjoin()
						|| SearchBlood.isInParty()) { // クランが同じ場合は、[X]、
														// すでにパーティー中であれば[X]、お店の中で[X]
					continue; // 砲門脱出
				} else if (SearchBlood.getName() != pc.getName()) {
					pc.setPartyType(1); // パーティータイプの設定
					SearchBlood.setPartyID(pc.getId()); // パーティー名の設定
					SearchBlood.sendPackets(new S_Message_YN(954, pc.getName()));
					pc.sendPackets(new S_ChatPacket(pc, SearchBlood.getName() + "様にパーティーを申請しました"));
				}
			}
		} else { // クランが存在しない君主や守護騎士[X]
			pc.sendPackets(new S_ChatPacket(pc, "血盟がありながら君主、部君主、守護騎士なら使用可能。"));
		}
	}

	private void age(L1PcInstance pc, String cmd) {
		try {
			StringTokenizer tok = new StringTokenizer(cmd);
			String AGE = tok.nextToken();
			int AGEint = Integer.parseInt(AGE);
			if (AGEint > 59 || AGEint < 14) {
				pc.sendPackets(new S_ChatPacket(pc, "自分の実際の年齢に設定します。"));
				return;
			}
			pc.setAge(AGEint);
			pc.save();
			pc.sendPackets(new S_ChatPacket(pc, "コマンド：あなたの年齢が[" + AGEint + "] 設定されている."));
		} catch (Exception e) {
			pc.sendPackets(new S_ChatPacket(pc, "。年齢の数値の形式で入力（血盟チャット時表示）"));
		}
	}

	private void Hunt(L1PcInstance pc, String cmd) {
		try {
			StringTokenizer st = new StringTokenizer(cmd);
			String char_name = st.nextToken();
			int price = Integer.parseInt(st.nextToken());
			String story = st.nextToken();

			L1PcInstance target = null;
			target = L1World.getInstance().getPlayer(char_name);
			if (target != null) {
				if (target.isGm()) {
					return;
				}
				if (target.getHuntCount() == 1) {
					pc.sendPackets(new S_SystemMessage("既に手配されています"));
					return;
				}
				if (price != Config.STAGE_1 && price != Config.STAGE_2 && price != Config.STAGE_3) {
					pc.sendPackets(new S_SystemMessage(
							"単位量は、" + Config.STAGE_1 + "/" + Config.STAGE_2 + "/" + Config.STAGE_3 + "万アデナです"));
					pc.sendPackets(new S_SystemMessage(
							"例) " + Config.STAGE_1 + ", " + Config.STAGE_2 + ", " + Config.STAGE_3 + "までです"));
					return;
				}
				if (price > Config.STAGE_3) {
					pc.sendPackets(new S_SystemMessage("最大量は" + Config.STAGE_3 + "万アデナです"));
					return;
				}
				if (!(pc.getInventory().checkItem(40308, price))) {
					pc.sendPackets(new S_SystemMessage("アデナが不足して"));
					return;
				}
				if (story.length() > 20) {
					pc.sendPackets(new S_SystemMessage("理由は短く、20文字で入力してください"));
					return;
				}
				if (target.getHuntPrice() > Config.STAGE_3) {
					pc.sendPackets(new S_SystemMessage("手配最大量" + Config.STAGE_3 + "のみです。"));
					return;
				}
				target.setHuntCount(1);
				target.setHuntPrice(target.getHuntPrice() + price);
				target.setReasonToHunt(story);
				target.save();
				L1World.getInstance().broadcastServerMessage("\\aD[" + target.getName() + "]の首に賞金がかかりました。");
				L1World.getInstance()
						.broadcastPacketToAll(new S_SystemMessage("\\aD[ 手配者 ]:  " + target.getName() + "  ]"));
				L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("\\aD[ 理由 ]: " + story + "  "));
				pc.getInventory().consumeItem(40308, price);
				huntoption(target);
			} else {
				pc.sendPackets(new S_SystemMessage("接続中でありません。"));
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage("。手配[キャラクター名] [金額] [理由]"));
			pc.sendPackets(new S_SystemMessage("====== 追加打撃範囲 ======"));
			pc.sendPackets(new S_SystemMessage("====== " + Config.STAGE_1 + "万ツタ1 ======"));
			pc.sendPackets(new S_SystemMessage("====== " + Config.STAGE_2 + "万ツタ2 ======"));
			pc.sendPackets(new S_SystemMessage("====== " + Config.STAGE_3 + "万ツタ3 ======"));
		}
	}

	private void huntoption(L1PcInstance pc) { // このマップエフェクト示す
		if (pc.getHuntCount() != 0) {
			if (pc.isWizard() || pc.isBlackwizard()) {
				if (pc.getHuntPrice() == Config.STAGE_1) {
					pc.addSp(1);
					pc.sendPackets(new S_SPMR(pc));
					pc.sendPackets(new S_OwnCharAttrDef(pc));
					pc.sendPackets(new S_OwnCharStatus2(pc));
					pc.sendPackets(new S_OwnCharStatus(pc));
				} else if (pc.getHuntPrice() == Config.STAGE_2) {
					pc.addSp(2);
					pc.sendPackets(new S_SPMR(pc));
					pc.sendPackets(new S_OwnCharAttrDef(pc));
					pc.sendPackets(new S_OwnCharStatus2(pc));
					pc.sendPackets(new S_OwnCharStatus(pc));
				} else if (pc.getHuntPrice() == Config.STAGE_3) {
					pc.addSp(3);
					pc.sendPackets(new S_SPMR(pc));
					pc.sendPackets(new S_OwnCharAttrDef(pc));
					pc.sendPackets(new S_OwnCharStatus2(pc));
					pc.sendPackets(new S_OwnCharStatus(pc));
				}
			} else if (pc.isCrown() || pc.isKnight() || pc.isDarkelf() || pc.isDragonknight() || pc.isWarrior()) {
				if (pc.getHuntPrice() == Config.STAGE_1) {
					pc.addDmgup(1);
					pc.addBowDmgup(1);
					pc.sendPackets(new S_OwnCharAttrDef(pc));
					pc.sendPackets(new S_OwnCharStatus2(pc));
					pc.sendPackets(new S_OwnCharStatus(pc));
				} else if (pc.getHuntPrice() == Config.STAGE_2) {
					pc.addDmgup(2);
					pc.addBowDmgup(2);
					pc.sendPackets(new S_OwnCharAttrDef(pc));
					pc.sendPackets(new S_OwnCharStatus2(pc));
					pc.sendPackets(new S_OwnCharStatus(pc));
				} else if (pc.getHuntPrice() == Config.STAGE_3) {
					pc.addDmgup(3);
					pc.addBowDmgup(3);
					pc.sendPackets(new S_OwnCharAttrDef(pc));
					pc.sendPackets(new S_OwnCharStatus2(pc));
					pc.sendPackets(new S_OwnCharStatus(pc));
				}
			}
		}
	}

	private static boolean isDisitAlpha(String str) {
		boolean check = true;
		for (int i = 0; i < str.length(); i++) {
			if (!Character.isDigit(str.charAt(i)) // 数字がない場合は
					&& !Character.isUpperCase(str.charAt(i)) // 大文字がない場合は
					&& !Character.isLowerCase(str.charAt(i))) { // 小文字がない場合は
				check = false;
				break;
			}
		}
		return check;
	}

	private boolean isValidQuiz(L1PcInstance pc, String quiz) {
		java.sql.Connection con = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		boolean result = false;

		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("select quiz from accounts where login='" + pc.getAccountName() + "'");
			rs = statement.executeQuery();

			String oldQuiz = "";
			if (rs.next()) {
				oldQuiz = rs.getString(1);
			}

			if (oldQuiz == null || oldQuiz.equalsIgnoreCase(quiz)) {
				result = true;
			}
		} catch (Exception e) {
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(statement);
			SQLUtil.close(con);
		}

		return result;
	}

	private void changequiz(L1PcInstance pc, String param) {
		boolean firstQuiz = false;
		try {
			StringTokenizer tok = new StringTokenizer(param);
			String oldquiz = "";

			if (isValidQuiz(pc, oldquiz)) {
				firstQuiz = true;
			} else {
				oldquiz = tok.nextToken();
			}
			String newquiz = tok.nextToken();

			if (newquiz.length() < 4) {
				pc.sendPackets(new S_ChatPacket(pc, "4文字〜12文字の間の英語や数字で入力してください。"));
				return;
			}
			if (newquiz.length() > 12) {
				pc.sendPackets(new S_ChatPacket(pc, "4文字〜12文字の間の英語や数字で入力してください。"));
				return;
			}

			if (isDisitAlpha(newquiz) == false) {
				pc.sendPackets(new S_ChatPacket(pc, "数字と英語のみ入力してください。"));
				return;
			}
			chkquiz(pc, oldquiz, newquiz);
		} catch (Exception e) {
			if (firstQuiz) {
				pc.sendPackets(new S_ChatPacket(pc, "セキュリティの設定、必要なセキュリティパスワード< - 形式で入力してください。"));
			} else {
				pc.sendPackets(new S_ChatPacket(pc, "あなたのアカウントはすでにセキュリティが設定されています。"));
			}
		}
	}

	private void chkquiz(L1PcInstance pc, String oldQuiz, String newQuiz) {
		java.sql.Connection con = null;
		PreparedStatement pstm = null;

		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			String sqlstr = "UPDATE accounts SET quiz = ? WHERE login=?";
			pstm = con.prepareStatement(sqlstr);
			pstm.setString(1, newQuiz);
			pstm.setString(2, pc.getAccountName());
			pstm.execute();
			pc.sendPackets(new S_SystemMessage("\\aDセキュリティの設定が正常に完了しました。"));
			pc.sendPackets(new S_SystemMessage("\\aDセキュリティパスワード：" + newQuiz + "（紛失時のアカウントのパスワードを変更できません）"));
			pc.setNeedQuiz(false);
			pc.update_lastQuizChangeTime();
		} catch (Exception e) {
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private void validateQuiz(L1PcInstance pc, String param) {
		try {
			StringTokenizer tok = new StringTokenizer(param);
			if (isValidQuiz(pc, "")) {
				pc.sendPackets(new S_ChatPacket(pc, "まず、セキュリティ設定が必要です。コマンド[セキュリティ設定]"));
				return;
			}
			String quiz = tok.nextToken();

			if (!isValidQuiz(pc, quiz)) {
				Accountsquiz(pc, quiz);
				return;
			}
			pc.setQuizValidated();
			pc.sendPackets(new S_ChatPacket(pc, "セキュリティがしばらくオフになりました。しばらくパスワードの変更が可能です。", 1));
		} catch (Exception e) {
			pc.sendPackets(new S_ChatPacket(pc, "セキュリティ解除セキュリティ設定されたパスワード< - の形式で入力。"));
		}
	}

	private void Accountsquiz(L1PcInstance pc, String quiz) {
		java.sql.Connection con = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("select quiz from accounts where login='" + pc.getAccountName() + "'");
			rs = statement.executeQuery();
			String oldQuiz = "";
			if (rs.next()) {
				oldQuiz = rs.getString(1);
				pc.sendPackets(new S_ChatPacket(pc, "セキュリティ設定されたパスワードと一致しません。 *ヒント：" + oldQuiz.length() + "文字。"));
			}
		} catch (Exception e) {
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(statement);
			SQLUtil.close(con);
		}
	}

	private void changepassword(L1PcInstance pc, String param) {
		// boolean firstQuiz = false;
		try {
			if (pc.get_lastPasswordChangeTime() + 10 * 60 * 1000 > System.currentTimeMillis()) {
				pc.sendPackets(new S_ChatPacket(pc, "パスワードを変更の決定から設置、10分過ぎていない。しばらくして戻って変更してください。"));
				return;
			}
			StringTokenizer tok = new StringTokenizer(param);
			String newpasswd = tok.nextToken();
			if (isValidQuiz(pc, "")) {
				pc.sendPackets(new S_ChatPacket(pc, "セキュリティ設定後、パスワードの変更が可能です。コマンド[セキュリティ設定]"));
				return;
			}
			if (!pc.isQuizValidated()) {
				pc.sendPackets(new S_ChatPacket(pc, "セキュリティ解除後にパスワードの変更が可能です。コマンド[セキュリティ解除]"));
				return;
			}
			if (newpasswd.length() < 6) {
				pc.sendPackets(new S_ChatPacket(pc, "6カ〜16文字の間の英語や数字で入力してください。"));
				return;
			}
			if (newpasswd.length() > 16) {
				pc.sendPackets(new S_ChatPacket(pc, "6カ〜16文字の間の英語や数字で入力してください。"));
				return;
			}
			if (isDisitAlpha(newpasswd) == false) {
				pc.sendPackets(new S_ChatPacket(pc, "英語と数字のみを入力してください。"));
				return;
			}
			to_Change_Passwd(pc, newpasswd);

		} catch (Exception e) {
			pc.sendPackets(new S_ChatPacket(pc, "パスワードの変更変更パスワード< - 形式で入力してください。"));
		}
	}

	private void to_Change_Passwd(L1PcInstance pc, String passwd) {
		try {
			String login = null;
			String password = null;
			java.sql.Connection con = null;
			con = L1DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = null;
			PreparedStatement pstm = null;

			password = passwd;

			statement = con.prepareStatement(
					"select account_name from characters where char_name Like '" + pc.getName() + "'");
			ResultSet rs = statement.executeQuery();

			while (rs.next()) {
				login = rs.getString(1);
				pstm = con.prepareStatement("UPDATE accounts SET password=? WHERE login Like '" + login + "'");
				pstm.setString(1, password);
				pstm.execute();

				pc.sendPackets(new S_ChatPacket(pc, "あなたのアカウントのパスワードが（" + passwd + "）に変更されました。"));
			}
			rs.close();
			pstm.close();
			statement.close();
			con.close();
		} catch (Exception e) {
		}
	}

	//パスワード正しいかどうかリターン
	public static boolean isPasswordTrue(String Password, String oldPassword) {
		String _rtnPwd = null;
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		boolean result = false;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT password(?) as pwd");

			pstm.setString(1, oldPassword);
			rs = pstm.executeQuery();
			if (rs.next()) {
				_rtnPwd = rs.getString("pwd");
			}
			if (_rtnPwd.equals(Password)) { // 同じであれば
				result = true;
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return result;
	}

	private void POPall(L1PcInstance pc) {
		pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "LINALL1"));
	}

	private void changename(L1PcInstance pc, String name) {
		if (BadNamesList.getInstance().isBadName(name)) {
			pc.sendPackets(new S_SystemMessage("生成禁止されたキャラクター名です。"));
			return;
		}
		if (CharacterTable.doesCharNameExist(name)) { // キャラクター
			pc.sendPackets(new S_SystemMessage("同じ名前が存在します。"));
			return;
		}
		if (pc.getClanid() != 0) {
			pc.sendPackets(new S_SystemMessage("血盟をしばらく脱退した後に変更することができます。"));
			return;
		}
		if (pc.isCrown()) {
			pc.sendPackets(new S_SystemMessage("君主はオペレータと相談後にのみ変更できます"));
			return;
		}
		if (pc.hasSkillEffect(1005) || pc.hasSkillEffect(2005)) {
			pc.sendPackets(new S_SystemMessage("金鉱の状態に変更することができません。"));
			return;
		}
		try {
			if (pc.getLevel() >= 60) {
				for (int i = 0; i < name.length(); i++) {
					if (!Character.isLetterOrDigit(name.charAt(i))) {
						pc.sendPackets(new S_SystemMessage("キャラクター名が正しくありません。"));
						return;
					}
				}
				int numOfNameBytes = 0;
				numOfNameBytes = name.getBytes("MS932").length;
				if (numOfNameBytes == 0) {
					pc.sendPackets(new S_SystemMessage("名前変更変更キャラクター名< - の形式で入力"));
					return;
				}
				if (numOfNameBytes < 2 || numOfNameBytes > 12) {
					pc.sendPackets(new S_SystemMessage("ハングル1文字〜6文字の間に入力してください。"));
					return;
				}

				if (BadNamesList.getInstance().isBadName(name)) {
					pc.sendPackets(new S_SystemMessage("生成禁止されたキャラクター名です。"));
					return;
				}
				if (RobotAIThread.doesCharNameExist(name)) { // ロボット
					pc.sendPackets(new S_SystemMessage("同じ名前が存在します。"));
					return;
				}

				if (pc.getInventory().checkItem(408990, 1)) { // インベントリアイテムチェック
					Connection con = null;
					PreparedStatement pstm = null;
					try {
						con = L1DatabaseFactory.getInstance().getConnection();
						pstm = con.prepareStatement("UPDATE characters SET char_name =? WHERE char_name = ?");
						pstm.setString(1, name); // 変更
						pstm.setString(2, pc.getName());
						pstm.execute();
					} catch (SQLException e) {
					} finally {
						SQLUtil.close(pstm);
						SQLUtil.close(con);
					}

					pc.save(); // 保存
					/****** ここのファイルにキャラクター名変更を作成部分 *******/

					/****** LogDB という名前のフォルダを事前に作成しておくください *******/
					Calendar rightNow = Calendar.getInstance();
					int year = rightNow.get(Calendar.YEAR);
					int month = rightNow.get(Calendar.MONTH) + 1;
					int date = rightNow.get(Calendar.DATE);
					int hour = rightNow.get(Calendar.HOUR);
					int min = rightNow.get(Calendar.MINUTE);
					String stryyyy = "";
					String strmmmm = "";
					String strDate = "";
					String strhour = "";
					String strmin = "";
					stryyyy = Integer.toString(year);
					strmmmm = Integer.toString(month);
					strDate = Integer.toString(date);
					strhour = Integer.toString(hour);
					strmin = Integer.toString(min);
					String str = "";
					str = new String("[" + stryyyy + "-" + strmmmm + "-" + strDate + " " + strhour + ":" + strmin
							+ "]  " + pc.getName() + "  --->  " + name);
					StringBuffer FileName = new StringBuffer("LogDB/ChangeCharacterName.txt");
					PrintWriter out = null;
					try {
						out = new PrintWriter(new FileWriter(FileName.toString(), true));
						out.println(str);
						out.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					str = "";// 初期化
					pc.getInventory().consumeItem(408990, 1); // 注文書の削除
					pc.sendPackets(new S_SystemMessage("再接続すると、新しい名前に変更されます。"));
					buddys(pc); // 友達を削除
					deleteMail(pc); // メールの削除
					Thread.sleep(500);
					pc.sendPackets(new S_Disconnect());

				} else {
					pc.sendPackets(new S_SystemMessage("名前の変更注文が不足します。"));
				}
			} else {
				pc.sendPackets(new S_SystemMessage("60レベル以上のみ可能です。"));
			}

		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage("名前変更変更キャラクター名で入力してください。"));
		}
	}

	/********* ディビ友達リストから変更されたユーザ名を消去 ************/

	private void buddys(L1PcInstance pc) {
		Connection con = null;
		PreparedStatement pstm = null;
		String aaa = pc.getName();
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("DELETE FROM character_buddys WHERE buddy_name=?");

			pstm.setString(1, aaa);
			pstm.execute();
		} catch (SQLException e) {
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private void deleteMail(L1PcInstance pc) {
		Connection con = null;
		PreparedStatement pstm = null;

		String aaa = pc.getName();

		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("DELETE FROM letter WHERE receiver=?");
			pstm.setString(1, aaa);
			pstm.execute();
			// System.out.println("....["+ aaa +"].....");
		} catch (SQLException e) {
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private void privateShop1(L1PcInstance pc) {
		try {
			if (!pc.isPrivateShop()) {
				pc.sendPackets(new S_ChatPacket(pc, "通知：個人商店状態での使用が可能です。"));
				return;
			}
			// manager.LogServerAppend("終了", pc, pc.getNetConnection().getIp(),
			// -1);
			LinAllManager.getInstance().LogLogOutAppend(pc.getName(), pc.getNetConnection().getHostname());
			GameClient client = pc.getNetConnection();
			pc.setNetConnection(null);
			// pc.stopMpRegeneration();
			try {
				pc.save();
				pc.saveInventory();
			} catch (Exception e) {
			}
			client.setActiveChar(null);
			client.setLoginAvailable();
			client.CharReStart(true);
			client.sendPacket(new S_Unknown2(1));
		} catch (Exception e) {
		}
	}

	private void privateShop(L1PcInstance pc) {
		try {
			if (!pc.isPrivateShop()) {
				pc.sendPackets(new S_ChatPacket(pc, "通知：個人商店状態での使用が可能です。"));
				return;
			}
			for (L1PcInstance target : L1World.getInstance().getAllPlayers3()) {
				if (target.getId() != pc.getId()
						&& target.getAccountName().toLowerCase().equals(pc.getAccountName().toLowerCase())
						&& target.isPrivateShop()) {
					pc.sendPackets(new S_ChatPacket(pc, "警告：すでにあなたの補助キャラクターが無人店舗の状態です。"));
					return;
				}
			}
			// manager.LogServerAppend("終了", pc, pc.getNetConnection().getIp(),
			// -1);
			LinAllManager.getInstance().LogLogOutAppend(pc.getName(), pc.getNetConnection().getHostname());
			GameClient client = pc.getNetConnection();
			pc.setNetConnection(null);
			// pc.stopMpRegeneration();
			try {
				pc.save();
				pc.saveInventory();
			} catch (Exception e) {
			}
			client.setActiveChar(null);
			client.setLoginAvailable();
			client.CharReStart(true);
			client.sendPacket(new S_Unknown2(1)); // リースボタンのための構造を変更する //
													// Episode U

		} catch (Exception e) {
		}
	}

	public static boolean isHpBarTarget(L1Object obj) {
		if (obj instanceof L1MonsterInstance) {
			return true;
		}
		if (obj instanceof L1PcInstance) {
			return true;
		}
		if (obj instanceof L1SummonInstance) {
			return true;
		}
		if (obj instanceof L1PetInstance) {
			return true;
		}
		return false;
	}
}
// }