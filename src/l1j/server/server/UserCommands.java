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
import l1j.server.server.command.executor.L1HpBar;
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
			case "도움말":						showHelp(pc);	break;
			case "텔렉풀기":case "텔렉": case ".":	tell(pc);
			case "무인가입":						autoclanjoin(pc);break;
			case "정보":							check(pc);break;
			case "스텟초기화":						스초진행(pc);break;
			case "좌표복구":						좌표(pc);break;
			case "나이":							age(pc, param);break;
			case "수배":							Hunt(pc, param);break;
			case "혈맹파티":						BloodParty(pc);break;
			case "보안설정":						changequiz(pc, param);break;
			case "보안해제":						validateQuiz(pc, param);break;
			case "암호변경":case "비번변경":			changepassword(pc, param);break;
			case "드랍멘트":case "멘트":			Ment(pc, param);break;
			case "무인상점":						privateShop(pc);break;
			case "무인상점11":						privateShop1(pc);break;
			case "캐릭명변경":case "케릭명변경":case "이름변경":changename(pc, param);
			case "라이트":case "맵핵":				maphack(pc, param);break;
			case "고정":case "고정신청":			phone(pc, param);break;
			case "혈마크":						Mark1(pc, param);break;
			case "인형":case "인형정보":			POPall(pc);break;
			case "킬랭킹":						pc.sendPackets(new S_UserCommands4(pc,1));break;
			case "데스랭킹":						pc.sendPackets(new S_UserCommands5(pc,1));break;
			case "피바":                         execute(pc, param, param); break;
			case "어비스포인트":
				String grade = "";

				switch(pc.getPeerage()){
					case 0:
						grade="견습";
						break;
					case 1:
						grade="9급병";
						break;
					case 2:
						grade="8급병";
						break;
					case 3:
						grade="7급병";
						break;
					case 4:
						grade="6급병";
						break;
					case 5:
						grade="5급병";
						break;
					case 6:
						grade="4급병";
						break;
					case 7:
						grade="3급병";
						break;
					case 8:
						grade="2급병";
						break;
					case 9:
						grade="1급병";
						break;
					case 10:
						grade="1성장교";
						break;
					case 11:
						grade="2성장교";
						break;
					case 12:
						grade="3성장교";
						break;
					case 13:
						grade="4성장교";
						break;
					case 14:
						grade="5성장교";
						break;
					case 15:
						grade="장군";
						break;
					case 16:
						grade="대장군";
						break;
					case 17:
						grade="사령관";
						break;
					case 18:
						grade="총사령관";
						break;
				}
				int point = pc.getAbysspoint();
				pc.sendPackets(new S_SystemMessage("[" + pc.getName() + "] 님의 어비스포인트는 "+point+"점"+" 계급은 : "+grade+" 입니다."));
				break;
			default:
				pc.sendPackets(new S_SystemMessage("그러한 명령어 " + cmd + " 는 존재하지 않습니다. "));
				break;
		

/*			} else if (cmd.equalsIgnoreCase("우호도")) {
				describe(pc);*/
/*			} else if (cmd.equalsIgnoreCase("어비스포인트")) {
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
		pc.sendPackets(new S_SystemMessage("\\aA    .정보 .텔렉 .고정신청 .좌표복구 .혈맹파티 "));
		pc.sendPackets(new S_SystemMessage("\\aA    .암호변경 .보안설정 .보안해제 .데스랭킹 .킬랭킹 "));
		pc.sendPackets(new S_SystemMessage("\\aA    .무인가입 .이름변경 .무인상점 .라이트 .나이"));
		pc.sendPackets(new S_SystemMessage("\\aA    .혈마크 .드랍멘트(멘트) .수배  .인형정보"));
		pc.sendPackets(new S_SystemMessage("\\aH=========< Have a Good Time >================"));
	}

	private void Ment(L1PcInstance pc, String param) {
		if (param.equalsIgnoreCase("끔")) {
			pc.sendPackets(new S_ChatPacket(pc, "아이템 획득 멘트 - OFF -"));
			pc.RootMent = false;
		} else if (param.equalsIgnoreCase("켬")) {
			pc.sendPackets(new S_ChatPacket(pc, "아이템 획득 멘트 - ON -"));
			pc.RootMent = true;
		} else {
			pc.sendPackets(new S_ChatPacket(pc, ".드랍멘트 [켬/끔]중 입력 (아이템 획득 멘트 설정)"));
		}
	}
	
	private void autoclanjoin(L1PcInstance pc) {
		try {
			//실패조건
			
			if (pc.getClanRank() != 10) { 
			    pc.sendPackets(new S_ServerMessage(92, pc.getName())); // \f1%0은 프린스나 프린세스가 아닙니다.
			      return;
			}
			if (pc.isFishing()) {
				pc.sendPackets(new S_SystemMessage("낚시중 일때는 행동이 제한됩니다."));
				return;	
			}
			if (pc.getClanid()==0 || pc.getClanid()== 1) {//신규혈 아이디
				pc.sendPackets(new S_SystemMessage("혈맹창설 상태가 아닙니다."));
				return;	
			}
			if (pc.isPrivateShop()) {
				pc.sendPackets(new S_SystemMessage("개인상점중엔 사용할수 없습니다."));
				return;	
			}
			if (pc.isPinkName() || pc.isParalyzed() || pc.isSleeped()) {
				pc.sendPackets(new S_SystemMessage("보라중 마비중 잠수중에는 사용할 수 없습니다."));
				return;
			}
			if (pc.isDead()) {
				pc.sendPackets(new S_SystemMessage("죽은 상태에선 실행 할수없습니다."));
				return;
			}
				
			//실패조건
			//기란 여관 앞
			if(pc.getX()>=33426 && pc.getX()<=33435 && pc.getY()>=32795 && pc.getY()<=32802 && pc.getMapId()==4){
				for(L1PcInstance target : L1World.getInstance().getAllPlayers3()){
					if(target.getId() != pc.getId() && target.getAccountName().toLowerCase().equals(pc.getAccountName().toLowerCase()) && target.isAutoClanjoin() ){
						pc.sendPackets(new S_ChatPacket(pc,"이미 당신의 보조 캐릭터가 무인가입 상태입니다."));
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
				} catch(Exception e) {                    		
				}
				client.setActiveChar(null);
				client.setLoginAvailable();
				client.CharReStart(true);
				client.sendPacket(new S_Unknown2(1)); // 리스버튼을 위한 구조변경 // Episode U
			}else{
				pc.sendPackets(new S_ChatPacket(pc,"기란마을 여관 앞 공간에서만 사용할 수 있습니다."));
			}
		} catch (Exception e) {
			System.out.println(pc.getName()+"무인가입 처리 에러");
		}		
	}
	
	
	  public void execute(L1PcInstance pc, String cmdName, String arg) { 
		  if (arg.equalsIgnoreCase("켬")) { 
		  pc.setSkillEffect(L1SkillId.GMSTATUS_HPBAR, 0); 
		  } else if (arg.equalsIgnoreCase("끔")) { 
		  pc.removeSkillEffect(L1SkillId.GMSTATUS_HPBAR);

			for (L1Object obj : pc.getKnownObjects()) {
				if (isHpBarTarget(obj)) {
			  pc.sendPackets(new S_HPMeter(obj.getId(), 0xFF, 0xff)); 
		  } 
		  } 
		  } else { 
		  pc.sendPackets(new S_SystemMessage(cmdName + " [켬,끔] 라고 입력해 주세요. ")); 
		  } 
		  } 
	

	private void phone(L1PcInstance pc, String param) {
		try {
			long curtime = System.currentTimeMillis() / 1000;
			if (pc.getQuizTime() + 10 > curtime) {
				long sec = (pc.getQuizTime() + 10) - curtime;
				pc.sendPackets(new S_SystemMessage(sec + "초 후에 사용할 수 있습니다."));
				return;
			}
			StringTokenizer tok = new StringTokenizer(param);
			String phone = tok.nextToken();
			Account account = Account.load(pc.getAccountName());
			if (param.length() < 10) {
				pc.sendPackets(new S_ChatPacket(pc, "없는 번호입니다. 다시 입력해주세요.", 1));
				return;
			}
			if (param.length() > 11) {
				pc.sendPackets(new S_ChatPacket(pc, "잘못된 번호입니다. 다시 입력해주세요."));
				return;
			}
			if (isDisitAlpha(phone) == false) {
				pc.sendPackets(new S_ChatPacket("숫자로만 입력하세요."));
				return;
			}
			if (account.getphone() != null) {
				pc.sendPackets(new S_ChatPacket(pc, "이미 전화번호가 설정되어 있습니다."));
				pc.sendPackets(new S_ChatPacket(pc, "번호 변경시 메티스에게 편지로 연락처를 보내세요."));
				return;
			}
			account.setphone(phone);
			Account.updatePhone(account);
			pc.sendPackets(new S_ChatPacket(pc, " " + phone + " 설정 완료. 초기화 시 문자발송됩니다."));
			pc.sendPackets(new S_PacketBox(pc, S_PacketBox.ICON_SECURITY_SERVICES));
			pc.sendPackets(new S_ChatPacket(pc, "보안버프(AC-1)는 리스하시면 적용됩니다"));
			pc.setQuizTime(curtime);
		} catch (Exception e) {
			pc.sendPackets(new S_ChatPacket(pc, ".고정신청 연락처 형식으로 입력.(초기화 할때만 문자전송)"));
		}
	}

	private void 스텟초기화(L1PcInstance pc) {
		L1SkillUse l1skilluse = new L1SkillUse();
		l1skilluse.handleCommands(pc, L1SkillId.CANCELLATION, pc.getId(),
				pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_LOGIN);
		
		if (pc.getWeapon() != null) {
			pc.getInventory().setEquipped(pc.getWeapon(), false, false, false,
					false);
		}
		
		pc.sendPackets(new S_CharVisualUpdate(pc));
		pc.sendPackets(new S_OwnCharStatus2(pc));
		
		for (L1ItemInstance armor : pc.getInventory().getItems()) {
			for (int type = 0; type <= 12; type++) {
				if (armor != null) {
					pc.getInventory().setEquipped(armor, false, false, false,
							false);
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
			System.out.println("스텟초기화 명령어 에러");
		}
	}

	private void 스초진행(L1PcInstance pc) {
		try {
			long curtime = System.currentTimeMillis() / 1000;
			if (pc.getQuizTime() + 10 > curtime) {
				pc.sendPackets(new S_SystemMessage("10초간의 지연시간이 필요합니다."));
				return;
			}
			if (!pc.getMap().isSafetyZone(pc.getLocation())) {
				pc.sendPackets(new S_ChatPacket(pc, "안전한 지역에서만 사용할 수 있습니다."));
				return;
			}
			if (pc.getInventory().checkItem(200000, 1)) {
				if (pc.getLevel() != pc.getHighLevel()) {
					pc.sendPackets(new S_SystemMessage("레벨이 다운된 캐릭입니다. 레벨업 후 이용하세요."));
					return;
				}
				if (pc.getLevel() > 54) {
					pc.getInventory().consumeItem(200000, 1);
					new L1Teleport().teleport(pc, 32723 + _random.nextInt(10), 32851 + _random.nextInt(10), (short) 5166, 5,true);
					스텟초기화(pc);
				} else {
					pc.sendPackets(new S_SystemMessage("스텟초기화는 55레벨 이상만 가능합니다."));
				}
			} else {
				pc.sendPackets(new S_SystemMessage("회상의 촛불이 없습니다."));
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
				pc.sendPackets(new S_SystemMessage("10초간의 지연시간이 필요합니다."));
				return;
			}
			int hpr = pc.getHpr() + pc.getInventory().hpRegenPerTick();
			int mpr = pc.getMpr() + pc.getInventory().mpRegenPerTick();

			pc.sendPackets(new S_SystemMessage("===================( 나의 정보 )===================="));
			pc.sendPackets(new S_SystemMessage("\\aD(피틱: " + hpr + ')' + "(엠틱: " + mpr + ')' + "(PK횟수: " + pc.get_PKcount() + ')' + "(엘릭: " + pc.getElixirStats() + "개)"));
			pc.sendPackets(new S_SystemMessage("===================================================="));
			pc.setQuizTime(curtime);
		} catch (Exception e) {
		}
	}

/*	private void 전랭(L1PcInstance pc) {
		try {
			long curtime = System.currentTimeMillis() / 1000;
			if (pc.getQuizTime2() + 20 > curtime) {
				long time = (pc.getQuizTime2() + 20) - curtime;
				pc.sendPackets(new S_ChatPacket(pc, time + "초 후 사용할 수 있습니다."));
				return;
			}
			pc.sendPackets(new S_UserCommands4(pc, 1));
			pc.setQuizTime2(curtime);
		} catch (Exception e) {
		}
	}*/
	
	private void maphack(L1PcInstance pc, String param) {
		try {
			StringTokenizer st = new StringTokenizer(param);
			String on = st.nextToken();
			if (on.equalsIgnoreCase("켬")) {
				pc.sendPackets(new S_Ability(3, true));
				pc.sendPackets(new S_SystemMessage("\\aA명령어: 라이트를 \\aG[시작]\\aA 하였습니다."));
			} else if (on.equals("끔")) {
				pc.sendPackets(new S_Ability(3, false));
				pc.sendPackets(new S_SystemMessage("\\aA명령어: 라이트를 \\aG[종료]\\aA 하였습니다."));
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage("\\aA명령어: .라이트 \\aG[켬, 끔]"));
		}
	}

	private void 좌표(L1PcInstance pc) {
		try {
			long curtime = System.currentTimeMillis() / 1000;
			if (pc.getQuizTime2() + 20 > curtime) {
				long time = (pc.getQuizTime2() + 20) - curtime;
				pc.sendPackets(new S_ChatPacket(pc, time + "초 후 사용할 수 있습니다."));
				return;
			}
			Connection connection = null;
			connection = L1DatabaseFactory.getInstance().getConnection();
			PreparedStatement preparedstatement = connection
					.prepareStatement("UPDATE characters SET LocX=33432,LocY=32807,MapID=4 WHERE account_name=? and MapID not in (5001,99,997,5166,39,34,701,2000)"); // 운영자의방,감옥,배틀존대기실
																																										// 제외
			preparedstatement.setString(1, pc.getAccountName());
			preparedstatement.execute();
			preparedstatement.close();
			connection.close();
			pc.sendPackets(new S_SystemMessage("계정내 모든 캐릭터의 좌표가 기란마을로 이동되었습니다"));

			pc.setQuizTime(curtime);
		} catch (Exception e) {
		}
	}

	private void tell(L1PcInstance pc) {

		long curtime = System.currentTimeMillis() / 1000;
		if (pc.getQuizTime2() + 20 > curtime) {
			long time = (pc.getQuizTime2() + 20) - curtime;
			pc.sendPackets(new S_ChatPacket(pc, time + "초 후 사용할 수 있습니다."));
			return;
		}
		try {
			if (pc.getMapId() == 781) {
				if (pc.getLocation().getX() <= 32998 && pc.getLocation().getX() >= 32988 && pc.getLocation().getY() <= 32758 && pc.getLocation().getY() >= 32736) {
					pc.sendPackets(new S_SystemMessage("사용할 수 없는 장소입니다."));
					return;
				}
			}
			/*
			 * if (CharPosUtil.getZoneType(pc) == 0 && castle_id != 0) { // 공성장 주변에서 불가능 pc.sendPackets(new S_SystemMessage("사용할 수 없는 장소입니다.")); return; }
			 */
			if (pc.isPinkName() || pc.isDead() || pc.isParalyzed() || pc.isSleeped() || pc.getMapId() == 800 || pc.getMapId() == 5302 || pc.getMapId() == 5153
					|| pc.getMapId() == 5490) {
				pc.sendPackets(new S_SystemMessage("사용할 수 없는 상태입니다."));
				return;
			}
			new L1Teleport().teleport(pc, pc.getX(), pc.getY(), pc.getMapId(), pc.getHeading(), false);
			pc.update_lastLocalTellTime();
			pc.setQuizTime2(curtime);
		} catch (Exception exception35) {
		}
	}

	/*private void TargetLoc(L1PcInstance pc, String param) {
		try {
			StringTokenizer stringtokenizer = new StringTokenizer(param);
			String para1 = stringtokenizer.nextToken();
			L1PcInstance target = L1World.getInstance().getPlayer(para1);

			String msg = null;
			// 운영자 위치추적 금지.
			if (para1.equalsIgnoreCase("메티스") || para1.equalsIgnoreCase("운영자") || para1.equalsIgnoreCase("카시오페아") || para1.equalsIgnoreCase("도우미")
					|| para1.equalsIgnoreCase("미소피아")) {
				pc.sendPackets(new S_SystemMessage(param + "님은 위치찾기불가능합니다."));
				return;
			}
			if (target != null) {
				if (pc.getInventory().checkItem(40308, 100000)) {
					pc.getInventory().consumeItem(40308, 100000);
					int mapid = target.getMapId();
					if (mapid == 1) {
						msg = "말하는 섬 던전";
					} else if (target.getMap().isSafetyZone(target.getLocation())) {
						msg = "마을";
					} else if (mapid == 4 || mapid == 0 && target.getMap().isNormalZone(target.getLocation())) {
						msg = "필드";
					} else if (target.isPrivateShop()) {
						msg = "시장";
					} else if (mapid >= 7 && mapid <= 13) {
						msg = "글루디오 던전";
					} else if (mapid >= 18 && mapid <= 20) {
						msg = "요정족 던전";
					} else if (mapid >= 25 && mapid <= 28) {
						msg = "수련 던전";
					} else if (mapid >= 30 && mapid <= 33 || mapid >= 35 && mapid <= 36) {
						msg = "용의 계곡 던전";
					} else if (mapid >= 43 && mapid <= 51) {
						msg = "개미 던전";
					} else if (mapid >= 53 && mapid <= 56) {
						msg = "기란 감옥";
					} else if (mapid >= 59 && mapid <= 63) {
						msg = "에바 왕국";
					} else if (mapid == 70) {
						msg = "잊혀진 섬";
					} else if (mapid >= 271 && mapid <= 278) {
						msg = "수정 동굴";
					} else if (mapid >= 75 && mapid <= 82) {
						msg = "상아탑";
					} else if (mapid >= 101 && mapid <= 200) {
						msg = "오만의 탑";
					} else if (mapid == 301) {
						msg = "지하수로";
					} else if (mapid == 303) {
						msg = "몽환의 섬";
					} else if (mapid == 304) {
						msg = "침묵의 동굴";
					} else if (mapid >= 307 && mapid <= 309) {
						msg = "지하 침공로";
					} else if (mapid == 400) {
						msg = "대공동 저항군 지역";
					} else if (mapid == 401) {
						msg = "대공동 은둔자 지역";
					} else if (mapid == 410) {
						msg = "마족 신전";
					} else if (mapid == 420) {
						msg = "지저 호수";
					} else if (mapid == 430) {
						msg = "정령의 무덤";
					} else if (mapid == 5167) {
						msg = "악마왕의 영토";
					} else if (mapid == 5153) {
						msg = "배틀존";
					} else if (mapid >= 440 && mapid <= 444) {
						msg = "해적섬";
					} else if (mapid >= 450 && mapid <= 478 || mapid >= 490 && mapid <= 496 || mapid >= 530 && mapid <= 536) {
						msg = "라스타바드 성";
					} else if (mapid >= 521 && mapid <= 524) {
						msg = "그림자 신전";
					} else if (mapid >= 600 && mapid <= 607) {
						msg = "욕망의 동굴";
					} else if (mapid >= 777 && mapid <= 779) {
						msg = "버림받은 땅";
					} else if (mapid >= 780 && mapid <= 782) {
						msg = "테베";
					} else if (mapid >= 783 && mapid <= 784) {
						msg = "티칼";
					} else if (mapid == 5302 && mapid == 5490) {
						msg = "낚시터";
					} else {
						msg = "추적이 안되는 곳";
					}
					pc.sendPackets(new S_SystemMessage(target.getName() + "님의 위치는 " + msg + "입니다."));
				} else {
					pc.sendPackets(new S_SystemMessage("10만 아데나가 필요합니다."));
				}
			} else {
				pc.sendPackets(new S_SystemMessage(param + "님은 접속중이 아닙니다."));
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage("------------------< 위치추적 >------------------"));
			pc.sendPackets(new S_SystemMessage("10만 아데나를 소모하여 상대방의 위치를 추적하는 시스템입니다."));
			pc.sendPackets(new S_SystemMessage(".위치추적 캐릭명 형식으로 입력하세요."));
			pc.sendPackets(new S_SystemMessage("---------------------------------------------"));
		}
	}*/
	

	private void Mark1(L1PcInstance pc, String param) {
		long curtime = System.currentTimeMillis() / 1000;
		if (pc.getQuizTime() + 30 > curtime) {
			long time = (pc.getQuizTime() + 30) - curtime;
			pc.sendPackets(new S_ChatPacket(pc, time + " 초 후 사용할 수 있습니다."));
			return;
		}
		if (pc.isDead()) {
			pc.sendPackets(new S_SystemMessage("죽은 상태에선 사용할 수 없습니다."));
			return;
		}
		int i = 1;
		if (pc.문장주시) {
			i = 3;
			pc.문장주시 = false;
		} else
			pc.문장주시 = true;
		for (L1Clan clan : L1World.getInstance().getAllClans()) {
			if (clan != null) {
				pc.sendPackets(new S_War(i, pc.getClanname(), clan.getClanName()));
				// pc.sendPackets(new S_SystemMessage("모든 혈맹의 문장이 표시됩니다. 다시 실행하면 꺼집니다."));
			}
		}
		pc.setQuizTime(curtime);

	}

/*	private void Mark(L1PcInstance pc, String param) {
		long curtime = System.currentTimeMillis() / 1000;
		if (pc.getQuizTime() + 30 > curtime) {
			long time = (pc.getQuizTime() + 30) - curtime;
			pc.sendPackets(new S_ChatPacket(pc, time + " 초 후 사용할 수 있습니다."));
			return;
		}
		try {
			StringTokenizer tok = new StringTokenizer(param);
			String Kara = tok.nextToken();
			String clan_name = pc.getClanname();
			L1Clan clan = L1World.getInstance().getClan(clan_name);
			int castle_id;
			if (Kara.equalsIgnoreCase("켬")) {
				if (clan == null) {
					pc.sendPackets(new S_ChatPacket(pc, "혈맹이 있는 군주만 사용할 수 있습니다."));
					return;
				}
				if (pc.getId() != clan.getLeaderId()) {
					pc.sendPackets(new S_ChatPacket(pc, "군주만 사용이 가능합니다."));
					return;
				}
				for (castle_id = 1; castle_id <= 8; castle_id++) {
					if (WarTimeController.getInstance().isNowWar(castle_id)) {
						pc.sendPackets(new S_ChatPacket(pc, "공성 중에는 사용할 수 없습니다."));
						return;
					}
				}

				for (L1War war : L1World.getInstance().getWarList()) {
					if (war.CheckClanInSameWar(clan_name, clan_name) == true) {
						return;
					}
				}
				L1War war = new L1War();

				war.handleCommands(2, clan_name, clan_name); // 모의전 개시
				L1PcInstance clan_member[] = clan.getOnlineClanMember();
				for (int i = 0; i < clan_member.length; i++) {
					pc.sendPackets(new S_ChatPacket(pc, "군주 님이 혈맹마크를 띄웠습니다."));
				}
			} else if (Kara.equalsIgnoreCase("끔")) {
				for (L1War war : L1World.getInstance().getWarList()) {
					if (war.CheckClanInSameWar(clan_name, clan_name) == true) {
						war.CeaseWar(clan_name, clan_name);
						L1PcInstance clan_member[] = clan.getOnlineClanMember();
						for (int i = 0; i < clan_member.length; i++) {
							pc.sendPackets(new S_ChatPacket(pc, "군주 님이 혈맹마크를 삭제 하였습니다."));
						}
						return;
					}
				}
			}
			pc.setQuizTime(curtime);
		} catch (Exception e) {
			pc.sendPackets(new S_ChatPacket(pc, ".전쟁 켬/끔 으로 입력하세요."));
		}
	}*/

	public void BloodParty(L1PcInstance pc) {
		if (pc.isDead()) {
			pc.sendPackets(new S_SystemMessage("죽은 상태에선 사용할 수 없습니다."));
			return;
		}
		int ClanId = pc.getClanid();
		if (ClanId != 0 && pc.getClanRank() == L1Clan.군주 || pc.getClanRank() == L1Clan.수호 || pc.getClanRank() == L1Clan.부군주) {
			for (L1PcInstance SearchBlood : L1World.getInstance().getAllPlayers()) {
				if (SearchBlood.getClanid() != ClanId || SearchBlood.isPrivateShop() || SearchBlood.isAutoClanjoin() || SearchBlood.isInParty()) { // 클랜이 같지않다면[X], 이미파티중이면[X], 상점중[X]
					continue; // 포문탈출
				} else if (SearchBlood.getName() != pc.getName()) {
					pc.setPartyType(1); // 파티타입 설정
					SearchBlood.setPartyID(pc.getId()); // 파티아이디 설정
					SearchBlood.sendPackets(new S_Message_YN(954, pc.getName()));
					pc.sendPackets(new S_ChatPacket(pc, SearchBlood.getName() + " 님에게 파티를 신청했습니다"));
				}
			}
		} else { // 클랜이 없거나 군주 또는 수호기사 [X]
			pc.sendPackets(new S_ChatPacket(pc, "혈맹이 있으면서 군주, 부군주, 수호기사라면 사용가능."));
		}
	}
	private void age(L1PcInstance pc, String cmd) {
		try {
			StringTokenizer tok = new StringTokenizer(cmd);
			String AGE = tok.nextToken();
			int AGEint = Integer.parseInt(AGE);
			if (AGEint > 59 || AGEint < 14) {
				pc.sendPackets(new S_ChatPacket(pc, "자신의 실제 나이로 설정하세요."));
				return;
			}
			pc.setAge(AGEint);
			pc.save();
			pc.sendPackets(new S_ChatPacket(pc, "명령어: 당신의 나이가 ["+AGEint+"] 설정되었습니다."));
		} catch (Exception e) {
			pc.sendPackets(new S_ChatPacket(pc, ".나이 숫자 형식으로 입력.(혈맹 채팅 시 표시됨)"));
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
				// if (char_name.equals(pc.getName())) {
				// pc.sendPackets(new S_SystemMessage("자신에게 현상금을 걸수 없습니다."));
				// return;
				// }
				if (target.getHuntCount() == 1) {
					pc.sendPackets(new S_SystemMessage("이미 수배 되어있습니다"));
					return;
				}
				if (price != Config.수배1단 && price != Config.수배2단 && price != Config.수배3단) {
					pc.sendPackets(new S_SystemMessage("단위 금액은 "+ Config.수배1단 +"/"+ Config.수배2단 +"/"+ Config.수배3단 +" 만 아데나입니다"));
					pc.sendPackets(new S_SystemMessage("예) "+ Config.수배1단 +", "+ Config.수배2단 +", "+ Config.수배3단 +" 까지입니다"));
					return;
				}
				if (price > Config.수배3단) {
					pc.sendPackets(new S_SystemMessage("최대 금액은 "+ Config.수배3단 +"만 아데나입니다"));
					return;
				}
				if (!(pc.getInventory().checkItem(40308, price))) {
					pc.sendPackets(new S_SystemMessage("아데나가 부족합니다"));
					return;
				}
				if (story.length() > 20) {
					pc.sendPackets(new S_SystemMessage("이유는 짧게 20글자로 입력하세요"));
					return;
				}
				if (target.getHuntPrice() > Config.수배3단) {
					pc.sendPackets(new S_SystemMessage("수배최대금액 "+ Config.수배3단 +"만 입니다."));
					return;
				}
				target.setHuntCount(1);
				target.setHuntPrice(target.getHuntPrice() + price);
				target.setReasonToHunt(story);
				target.save();
				L1World.getInstance().broadcastServerMessage("\\aD[" + target.getName() + "]의 목에 현상금이 걸렸습니다.");
				L1World.getInstance()
						.broadcastPacketToAll(new S_SystemMessage("\\aD[ 수배자 ]:  " + target.getName() + "  ]"));
				L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("\\aD[ 이유 ]: " + story + "  "));
				pc.getInventory().consumeItem(40308, price);
				huntoption(target);
			} else {
				pc.sendPackets(new S_SystemMessage("접속중이지 않습니다."));
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".수배 [캐릭터명] [금액] [이유]"));
			pc.sendPackets(new S_SystemMessage("====== 추가 타격 범위 ======"));
			pc.sendPackets(new S_SystemMessage("====== "+ Config.수배1단 +"만 추타 1 ======"));
			pc.sendPackets(new S_SystemMessage("====== "+ Config.수배2단 +"만 추타 2 ======"));
			pc.sendPackets(new S_SystemMessage("====== "+ Config.수배3단 +"만 추타 3 ======"));
		}
	}
	
	private void huntoption(L1PcInstance pc) { //해당맵 이펙트 보여주기
		if(pc.getHuntCount() != 0){
			if(pc.isWizard() || pc.isBlackwizard()){
				if(pc.getHuntPrice() == Config.수배1단){
					pc.addSp(1);
					pc.sendPackets(new S_SPMR(pc));
					pc.sendPackets(new S_OwnCharAttrDef(pc));
					pc.sendPackets(new S_OwnCharStatus2(pc));
					pc.sendPackets(new S_OwnCharStatus(pc));
				} else if(pc.getHuntPrice() == Config.수배2단){
					pc.addSp(2);
					pc.sendPackets(new S_SPMR(pc));
					pc.sendPackets(new S_OwnCharAttrDef(pc));
					pc.sendPackets(new S_OwnCharStatus2(pc));
					pc.sendPackets(new S_OwnCharStatus(pc));
				} else if(pc.getHuntPrice() == Config.수배3단){
					pc.addSp(3);
					pc.sendPackets(new S_SPMR(pc));
					pc.sendPackets(new S_OwnCharAttrDef(pc));
					pc.sendPackets(new S_OwnCharStatus2(pc));
					pc.sendPackets(new S_OwnCharStatus(pc));
				}
			} else if (pc.isCrown() || pc.isKnight() || pc.isDarkelf() || pc.isDragonknight() || pc.is전사()){
				if(pc.getHuntPrice() == Config.수배1단){
					pc.addDmgup(1);
					pc.addBowDmgup(1);
					pc.sendPackets(new S_OwnCharAttrDef(pc));
					pc.sendPackets(new S_OwnCharStatus2(pc));
					pc.sendPackets(new S_OwnCharStatus(pc));
				} else if(pc.getHuntPrice() == Config.수배2단){
					pc.addDmgup(2);
					pc.addBowDmgup(2);
					pc.sendPackets(new S_OwnCharAttrDef(pc));
					pc.sendPackets(new S_OwnCharStatus2(pc));
					pc.sendPackets(new S_OwnCharStatus(pc));
				} else if(pc.getHuntPrice() == Config.수배3단){
					pc.addDmgup(3);
					pc.addBowDmgup(3);
					pc.sendPackets(new S_OwnCharAttrDef(pc));
					pc.sendPackets(new S_OwnCharStatus2(pc));
					pc.sendPackets(new S_OwnCharStatus(pc));
				}
			}
		}
	}

	/*private void describe(L1PcInstance pc) {
		try {
			StringBuilder msg = new StringBuilder();
			pc.sendPackets(new S_SystemMessage("나의 우호도: " + pc.getKarma() + ""));
			pc.sendPackets(new S_SystemMessage(msg.toString()));
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".우호도 명령어 에러"));
		}
	}*/

/*	private void buff(L1PcInstance pc) {
		int[] allBuffSkill = { PHYSICAL_ENCHANT_DEX, PHYSICAL_ENCHANT_STR, BLESS_WEAPON, IRON_SKIN, ADVANCE_SPIRIT };
		if (pc.isDead()) {
			pc.sendPackets(new S_ChatPacket(pc, "사용할 수 없는 상태입니다."));
			return;
		}
		long curtime = System.currentTimeMillis() / 1000;
		if (pc.getQuizTime() + 30 > curtime) {
			long time = (pc.getQuizTime() + 30) - curtime;
			pc.sendPackets(new S_ChatPacket(pc, time + " 초 후 사용할 수 있습니다."));
			return;
		}
		if (pc.getLevel() <= Config.BUFFLEVEL) { // 레벨 이하
			try {
				pc.setBuffnoch(1); // 스킬버그땜시 추가 올버프는 미작동
				L1SkillUse l1skilluse = new L1SkillUse();
				for (int i = 0; i < allBuffSkill.length; i++) {
					l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
				}
				pc.sendPackets(new S_ChatPacket(pc, "상쾌함을 느낍니다. " + Config.BUFFLEVEL + "레벨까지 사용됩니다."));
				pc.setBuffnoch(0); // 스킬버그땜시 추가 올버프는 미작동
				pc.setQuizTime(curtime);
			} catch (Exception exception19) {
				pc.sendPackets(new S_ChatPacket(pc, "버프 명령어 에러"));
			}
		} else {
			pc.sendPackets(new S_ChatPacket(pc, "레벨 " + Config.BUFFLEVEL + " 이상은 사용할 수 없습니다."));
		}
	}*/

	private static boolean isDisitAlpha(String str) {
		boolean check = true;
		for (int i = 0; i < str.length(); i++) {
			if (!Character.isDigit(str.charAt(i)) // 숫자가 아니라면
					&& !Character.isUpperCase(str.charAt(i)) // 대문자가 아니라면
					&& !Character.isLowerCase(str.charAt(i))) { // 소문자가 아니라면
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
				pc.sendPackets(new S_ChatPacket(pc, "4자 ~ 12자 사이의 영어나 숫자로 입력하세요."));
				return;
			}
			if (newquiz.length() > 12) {
				pc.sendPackets(new S_ChatPacket(pc, "4자 ~ 12자 사이의 영어나 숫자로 입력하세요."));
				return;
			}

			if (isDisitAlpha(newquiz) == false) {
				pc.sendPackets(new S_ChatPacket(pc, "숫자와 영어로만 입력하세요."));
				return;
			}
			chkquiz(pc, oldquiz, newquiz);
		} catch (Exception e) {
			if (firstQuiz) {
				pc.sendPackets(new S_ChatPacket(pc, ".보안설정 원하는보안암호 <-- 형식으로 입력하세요."));
			} else {
				pc.sendPackets(new S_ChatPacket(pc, "당신의 계정은 이미 보안이 설정되어 있습니다."));
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
			pc.sendPackets(new S_SystemMessage("\\aD보안 설정이 정상적으로 완료되었습니다."));
			pc.sendPackets(new S_SystemMessage("\\aD보안 암호: " + newQuiz + " (분실시 계정의 암호변경 불가능)"));
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
				pc.sendPackets(new S_ChatPacket(pc, "먼저 보안설정이 필요합니다. 명령어 [.보안설정]"));
				return;
			}
			String quiz = tok.nextToken();

			if (!isValidQuiz(pc, quiz)) {
				Accountsquiz(pc, quiz);
				return;
			}
			pc.setQuizValidated();
			pc.sendPackets(new S_ChatPacket(pc, "보안이 잠시 해제되었습니다. 잠시 암호변경이 가능합니다.", 1));
		} catch (Exception e) {
			pc.sendPackets(new S_ChatPacket(pc, ".보안해제 보안설정된암호 <-- 형식으로 입력."));
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
				pc.sendPackets(new S_ChatPacket(pc, "보안설정된 암호와 일치하지 않습니다. *힌트: " + oldQuiz.length() + " 글자."));
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
				pc.sendPackets(new S_ChatPacket(pc, "암호를 변경하신지 10분이 지나지 않았습니다. 잠시후 다시 변경하세요."));
				return;
			}
			StringTokenizer tok = new StringTokenizer(param);
			String newpasswd = tok.nextToken();
			if (isValidQuiz(pc, "")) {
				pc.sendPackets(new S_ChatPacket(pc, "보안설정 후에 암호변경이 가능합니다. 명령어 [.보안설정]"));
				return;
			}
			if (!pc.isQuizValidated()) {
				pc.sendPackets(new S_ChatPacket(pc, "보안해제 후에 암호변경이 가능합니다. 명령어 [.보안해제]"));
				return;
			}
			if (newpasswd.length() < 6) {
				pc.sendPackets(new S_ChatPacket(pc, "6자 ~ 16자 사이의 영어나 숫자로 입력하세요."));
				return;
			}
			if (newpasswd.length() > 16) {
				pc.sendPackets(new S_ChatPacket(pc, "6자 ~ 16자 사이의 영어나 숫자로 입력하세요."));
				return;
			}
			if (isDisitAlpha(newpasswd) == false) {
				pc.sendPackets(new S_ChatPacket(pc, "영어와 숫자로만 입력하세요."));
				return;
			}
			to_Change_Passwd(pc, newpasswd);

		} catch (Exception e) {
			pc.sendPackets(new S_ChatPacket(pc, ".암호변경 변경할암호 <-- 형식으로 입력하세요."));
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

			statement = con.prepareStatement("select account_name from characters where char_name Like '" + pc.getName() + "'");
			ResultSet rs = statement.executeQuery();

			while (rs.next()) {
				login = rs.getString(1);
				pstm = con.prepareStatement("UPDATE accounts SET password=? WHERE login Like '" + login + "'");
				pstm.setString(1, password);
				pstm.execute();

				pc.sendPackets(new S_ChatPacket(pc, "당신의 계정 암호가 (" + passwd + ") 로 변경되었습니다."));
			}
			rs.close();
			pstm.close();
			statement.close();
			con.close();
		} catch (Exception e) {
		}
	}

	// 패스워드 맞는지 여부 리턴
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
			if (_rtnPwd.equals(Password)) { // 동일하다면
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
			pc.sendPackets(new S_SystemMessage("생성 금지된 캐릭명입니다."));
			return;
		}
		if (CharacterTable.doesCharNameExist(name)) { // 케릭터
			pc.sendPackets(new S_SystemMessage("동일한 이름이 존재 합니다."));
			return;
		}
		if (pc.getClanid() != 0) {
			pc.sendPackets(new S_SystemMessage("혈맹을 잠시 탈퇴한 후 변경할 수 있습니다."));
			return;
		}
		if (pc.isCrown()) {
			pc.sendPackets(new S_SystemMessage("군주는 운영자와 상담 후에만 변경할 수 있습니다"));
			return;
		}
		if (pc.hasSkillEffect(1005) || pc.hasSkillEffect(2005)) {
			pc.sendPackets(new S_SystemMessage("채금 상태에는 변경할 수 없습니다."));
			return;
		}
		try {
			if (pc.getLevel() >= 60) {
				for (int i = 0; i < name.length(); i++) {
					if (name.charAt(i) == 'ㄱ' || name.charAt(i) == 'ㄲ' || name.charAt(i) == 'ㄴ' || name.charAt(i) == 'ㄷ' || // 한문자(char)단위로 비교.
							name.charAt(i) == 'ㄸ' || name.charAt(i) == 'ㄹ' || name.charAt(i) == 'ㅁ' || name.charAt(i) == 'ㅂ' || // 한문자(char)단위로 비교
							name.charAt(i) == 'ㅃ' || name.charAt(i) == 'ㅅ' || name.charAt(i) == 'ㅆ' || name.charAt(i) == 'ㅇ' || // 한문자(char)단위로 비교
							name.charAt(i) == 'ㅈ' || name.charAt(i) == 'ㅉ' || name.charAt(i) == 'ㅊ' || name.charAt(i) == 'ㅋ' || // 한문자(char)단위로 비교.
							name.charAt(i) == 'ㅌ' || name.charAt(i) == 'ㅍ' || name.charAt(i) == 'ㅎ' || name.charAt(i) == 'ㅛ' || // 한문자(char)단위로 비교.
							name.charAt(i) == 'ㅕ' || name.charAt(i) == 'ㅑ' || name.charAt(i) == 'ㅐ' || name.charAt(i) == 'ㅔ' || // 한문자(char)단위로 비교.
							name.charAt(i) == 'ㅗ' || name.charAt(i) == 'ㅓ' || name.charAt(i) == 'ㅏ' || name.charAt(i) == 'ㅣ' || // 한문자(char)단위로 비교.
							name.charAt(i) == 'ㅠ' || name.charAt(i) == 'ㅜ' || name.charAt(i) == 'ㅡ' || name.charAt(i) == 'ㅒ' || // 한문자(char)단위로 비교.
							name.charAt(i) == 'ㅖ' || name.charAt(i) == 'ㅢ' || name.charAt(i) == 'ㅟ' || name.charAt(i) == 'ㅝ' || // 한문자(char)단위로 비교.
							name.charAt(i) == 'ㅞ' || name.charAt(i) == 'ㅙ' || name.charAt(i) == 'ㅚ' || name.charAt(i) == 'ㅘ' || // 한문자(char)단위로 비교.
							name.charAt(i) == '씹' || name.charAt(i) == '좃' || name.charAt(i) == '좆' || name.charAt(i) == 'ㅤ') {
						pc.sendPackets(new S_SystemMessage("캐릭명이 올바르지 않습니다."));
						return;
					}
				}
				for (int i = 0; i < name.length(); i++) {
					if (!Character.isLetterOrDigit(name.charAt(i))) {
						pc.sendPackets(new S_SystemMessage("캐릭명이 올바르지 않습니다."));
						return;
					}
				}
				int numOfNameBytes = 0;
				numOfNameBytes = name.getBytes("MS949").length;
				if (numOfNameBytes == 0) {
					pc.sendPackets(new S_SystemMessage(".이름변경 바꿀캐릭명 <--형식으로 입력"));
					return;
				}
				if (numOfNameBytes < 2 || numOfNameBytes > 12) {
					pc.sendPackets(new S_SystemMessage("한글 1자 ~ 6자 사이로 입력하세요."));
					return;
				}

				if (BadNamesList.getInstance().isBadName(name)) {
					pc.sendPackets(new S_SystemMessage("생성 금지된 캐릭명입니다."));
					return;
				}
				if (RobotAIThread.doesCharNameExist(name)) { // 로봇 
					pc.sendPackets(new S_SystemMessage("동일한 이름이 존재 합니다."));
					return;
				}

				if (pc.getInventory().checkItem(408990, 1)) { // 인벤 아이템 체크 
					Connection con = null;
					PreparedStatement pstm = null;
					try {
						con = L1DatabaseFactory.getInstance().getConnection();
						pstm = con.prepareStatement("UPDATE characters SET char_name =? WHERE char_name = ?");
						pstm.setString(1, name); // 변경 
						pstm.setString(2, pc.getName());
						pstm.execute();
					} catch (SQLException e) {
					} finally {
						SQLUtil.close(pstm);
						SQLUtil.close(con);
					}

					pc.save(); // 저장
					/****** 여긴 파일로 캐릭명변경 내용 작성 부분 *******/

					/****** LogDB 라는 폴더를 미리 생성 해두세요 *******/
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
					str = new String("[" + stryyyy + "-" + strmmmm + "-" + strDate + " " + strhour + ":" + strmin + "]  " + pc.getName() + "  --->  " + name);
					StringBuffer FileName = new StringBuffer("LogDB/캐릭명변경.txt");
					PrintWriter out = null;
					try {
						out = new PrintWriter(new FileWriter(FileName.toString(), true));
						out.println(str);
						out.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					str = "";// 초기화
					pc.getInventory().consumeItem(408990, 1); // 주문서 삭제 
					pc.sendPackets(new S_SystemMessage("재접속하시면 새로운 이름으로 변경됩니다."));
					buddys(pc); // 친구 삭제
					편지삭제(pc); // 편지삭제
					Thread.sleep(500);
					pc.sendPackets(new S_Disconnect());

				} else {
					pc.sendPackets(new S_SystemMessage("이름 변경 주문서가 부족합니다."));
				}
			} else {
				pc.sendPackets(new S_SystemMessage("60레벨 이상만 가능합니다."));
			}

		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".이름변경 바꿀캐릭명 으로 입력해 주세요."));
		}
	}

	/********* 디비 친구목록에서 변경된 아이디 지우기 ************/

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

	private void 편지삭제(L1PcInstance pc) {
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
				pc.sendPackets(new S_ChatPacket(pc, "알림:개인상점 상태에서 사용이 가능합니다."));
				return;
			}
			//manager.LogServerAppend("종료", pc, pc.getNetConnection().getIp(), -1);
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
				pc.sendPackets(new S_ChatPacket(pc, "알림:개인상점 상태에서 사용이 가능합니다."));
				return;
			}
			for (L1PcInstance target : L1World.getInstance().getAllPlayers3()) {
				if (target.getId() != pc.getId() && target.getAccountName().toLowerCase().equals(pc.getAccountName().toLowerCase()) && target.isPrivateShop()) {
					pc.sendPackets(new S_ChatPacket(pc, "경고:이미 당신의 보조 캐릭터가 무인상점 상태입니다."));
					return;
				}
			}
			//manager.LogServerAppend("종료", pc, pc.getNetConnection().getIp(), -1);
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
			client.sendPacket(new S_Unknown2(1)); // 리스버튼을 위한 구조변경 // Episode U

		} catch (Exception e) {
		}
	}
	
	


	/*private void rank(L1PcInstance pc, String param) {
		Connection con = null;
		int q = 0;
		int i = 0;
		int j = 0;
		int n = pc.getType();
		// int p = 5;
		// int o = 1;
		int objid = pc.getId();

		String type = null;

		try {
			switch (pc.getType()) {
			case 0:
				type = "군주";
				break;
			case 1:
				type = "기사";
				break;
			case 2:
				type = "요정";
				break;
			case 3:
				type = "마법사";
				break;
			case 4:
				type = "다크엘프";
				break;
			case 5:
				type = "용기사";
				break;
			case 6:
				type = "환술사";
				break;
			case 7:
				type = "전사";
				break;
			}

			long curtime = System.currentTimeMillis() / 1000;
			if (pc.getQuizTime() + 20 > curtime) {
				long time = (pc.getQuizTime() + 20) - curtime;
				pc.sendPackets(new S_ChatPacket(pc, time + " 초 후 사용할 수 있습니다."));
				return;
			}

			
			 * if (!(pc.getInventory().checkItem(40308, 1000))) { pc.sendPackets(new S_ChatPacket(pc,"1000 아데나가 필요합니다.")); return; } pc.getInventory().consumeItem(40308, 1000);
			 

			con = L1DatabaseFactory.getInstance().getConnection();
			Statement pstm = con.createStatement();
			ResultSet rs = pstm.executeQuery("SELECT objid FROM characters WHERE AccessLevel = 0 order by Exp desc");
			Statement pstm2 = con.createStatement();
			ResultSet rs2 = pstm2.executeQuery("SELECT `Exp`,`char_name` FROM `characters` WHERE AccessLevel = 0 ORDER BY `Exp` DESC limit 1");

			if (pc.getType() == 0) {
				Statement pstm3 = con.createStatement();
				ResultSet rs3 = pstm3.executeQuery("SELECT objid FROM characters WHERE type = 0 and AccessLevel = 0 order by Exp desc");
				while (rs3.next()) {
					j++;
					if (objid == rs3.getInt(1))
						break;
				}
				rs3.close();
				pstm3.close();
			} else if (pc.getType() == 1) {
				Statement pstm3 = con.createStatement();
				ResultSet rs3 = pstm3.executeQuery("SELECT objid FROM characters WHERE type = 1 and AccessLevel = 0 order by Exp desc");
				while (rs3.next()) {
					j++;
					if (objid == rs3.getInt(1))
						break;
				}
				rs3.close();
				pstm3.close();
			} else if (pc.getType() == 2) {
				Statement pstm3 = con.createStatement();
				ResultSet rs3 = pstm3.executeQuery("SELECT objid FROM characters WHERE type = 2 and AccessLevel = 0 order by Exp desc");
				while (rs3.next()) {
					j++;
					if (objid == rs3.getInt(1))
						break;
				}
				rs3.close();
				pstm3.close();
			} else if (pc.getType() == 3) {
				Statement pstm3 = con.createStatement();
				ResultSet rs3 = pstm3.executeQuery("SELECT objid FROM characters WHERE type = 3 and AccessLevel = 0 order by Exp desc");
				while (rs3.next()) {
					j++;
					if (objid == rs3.getInt(1))
						break;
				}
				rs3.close();
				pstm3.close();
			} else if (pc.getType() == 4) {
				Statement pstm3 = con.createStatement();
				ResultSet rs3 = pstm3.executeQuery("SELECT objid FROM characters WHERE type = 4 and AccessLevel = 0 order by Exp desc");
				while (rs3.next()) {
					j++;
					if (objid == rs3.getInt(1))
						break;
				}
				rs3.close();
				pstm3.close();
			} else if (pc.getType() == 5) {
				Statement pstm3 = con.createStatement();
				ResultSet rs3 = pstm3.executeQuery("SELECT objid FROM characters WHERE type = 5 and AccessLevel = 0 order by Exp desc");
				while (rs3.next()) {
					j++;
					if (objid == rs3.getInt(1))
						break;
				}
				rs3.close();
				pstm3.close();
			} else if (pc.getType() == 6) {
				Statement pstm3 = con.createStatement();
				ResultSet rs3 = pstm3.executeQuery("SELECT objid FROM characters WHERE type = 6 and AccessLevel = 0 order by Exp desc");
				while (rs3.next()) {
					j++;
					if (objid == rs3.getInt(1))
						break;
				}
				rs3.close();
				pstm3.close();
			} else if (pc.getType() == 7) {
				Statement pstm3 = con.createStatement();
				ResultSet rs3 = pstm3.executeQuery("SELECT objid FROM characters WHERE type = 7 and AccessLevel = 0 order by Exp desc");
				while (rs3.next()) {
					j++;
					if (objid == rs3.getInt(1))
						break;
				}
				rs3.close();
				pstm3.close();
			}

			// while (rs2.next()) {
			// i++;
			// pc.sendPackets(new S_SystemMessage("서버 1위는 [" + rs2.getString("char_name") + "]"));
			// pc.setQuizTime(curtime);
			// }
			while (rs.next()) {
				q++;
				if (objid == rs.getInt(1))
					break;
			}
			int 서버랭킹 = q + 300; // 전체순위 뻥튀기
			int 개인랭킹 = j + 60; // 개인순위 뻥튀기
			if (pc.getLevel() < 51) {
				// pc.sendPackets(new S_SystemMessage("전체 순위: " + p +q+"위 / 클래스 순위: "+ o +j+"위"));
				pc.sendPackets(new S_SystemMessage("전체 순위: " + 서버랭킹 + "위 / 클래스 순위: " + 개인랭킹 + "위"));
			} else {
				pc.sendPackets(new S_SystemMessage("전체 순위: " + q + "위 / 클래스 순위: " + j + "위"));
			}
			rs.close();
			pstm.close();
			// rs2.close();
			// pstm2.close();
			con.close();
			pc.setQuizTime(curtime);
		} catch (Exception e) {
		}*/
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
//}