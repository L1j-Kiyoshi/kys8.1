package l1j.server.GameSystem.Robot;

import java.util.ArrayList;
import java.util.Random;

public class Robot_Location {

	private static Random _random = new Random(System.currentTimeMillis());

	private static ArrayList<Robot_Location_bean> 기란셋팅 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 기란셋팅2 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 기란셋팅3 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 기란셋팅4 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 기란셋팅5 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 오렌셋팅 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 글말셋팅 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 웰던셋팅 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 텔녀 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 용던입구 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 본던입구 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 기감입구 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 상아탑4층 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 상아탑5층 = new ArrayList<Robot_Location_bean>();
	

	private static ArrayList<Robot_Location_bean> 지저 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 용던1층 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 용던2층 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 용던3층 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 용던4층 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 용던5층 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 용던6층 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 용던7층 = new ArrayList<Robot_Location_bean>();

	private static ArrayList<Robot_Location_bean> 리뉴얼오만1층 = new ArrayList<Robot_Location_bean>(); // 160316
	private static ArrayList<Robot_Location_bean> 리뉴얼오만2층 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 리뉴얼오만3층 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 리뉴얼오만4층 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 리뉴얼오만5층 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 리뉴얼오만6층 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 리뉴얼오만7층 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 리뉴얼오만8층 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 리뉴얼오만9층 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 리뉴얼오만10층 = new ArrayList<Robot_Location_bean>();

	private static ArrayList<Robot_Location_bean> 본던1층 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 본던2층 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 본던3층 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 본던4층 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 본던5층 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 본던6층 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 본던7층 = new ArrayList<Robot_Location_bean>();

	private static ArrayList<Robot_Location_bean> 선박심해 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 선박심해2 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 선박심해3 = new ArrayList<Robot_Location_bean>();

	private static ArrayList<Robot_Location_bean> 잊섬 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 잊섬2 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 잊섬3 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 잊섬4 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 잊섬5 = new ArrayList<Robot_Location_bean>();

	private static ArrayList<Robot_Location_bean> 지배자1층 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 지배자2층 = new ArrayList<Robot_Location_bean>();

	private static ArrayList<Robot_Location_bean> 개미굴1 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 개미굴2 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 개미굴3 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 개미굴4 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 개미굴5 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 개미굴6 = new ArrayList<Robot_Location_bean>();

	private static ArrayList<Robot_Location_bean> 기감1층 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 기감2층 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 기감3층 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> 기감4층 = new ArrayList<Robot_Location_bean>();

	public static void 로케이션등록(int x, int y, int m) {

	}

	public static ArrayList<Robot_Location_bean> 로케이션(L1RobotInstance bot) {
		_random.setSeed(System.currentTimeMillis());
		if (bot.huntingBot_Type == L1RobotInstance.SETTING) {
			if (bot.huntingBot_Location.equalsIgnoreCase("용던1층")
					|| bot.huntingBot_Location.equalsIgnoreCase("용던2층")
					|| bot.huntingBot_Location.equalsIgnoreCase("용던3층")
					|| bot.huntingBot_Location.equalsIgnoreCase("용던4층")
					|| bot.huntingBot_Location.equalsIgnoreCase("용던5층")
					|| bot.huntingBot_Location.equalsIgnoreCase("용던6층")
					|| bot.huntingBot_Location.equalsIgnoreCase("용던7층")) {
				return _random.nextInt(1000) > 500 ? 기란셋팅 : 기란셋팅2;
			}
			int rr = _random.nextInt(16);
			if (rr == 15) {
				switch (_random.nextInt(2)) {
				case 0:
					rr = 0;
					break;
				case 1:
					rr = 7;
					break;
				}
			}
			switch (rr) {
			case 7:
			case 11:
			case 0:
				return _random.nextInt(1000) >= 500 ? 기란셋팅2 : 기란셋팅5;
			case 8:
			case 4:
			case 1:
				return 오렌셋팅;
			case 13:
			case 12:
				return 기란셋팅4;
			case 10:
			case 9:
			case 5:
			case 2:
				return _random.nextInt(1000) >= 500 ? 기란셋팅 : 기란셋팅3;
				// returnグルマル設定; //元のグルマル設定
			case 14:
			case 6:
			case 3:
				return 글말셋팅;
				// return ウェルダン設定;
			default:
				break;
			}
			return 기란셋팅;
		} else if (bot.huntingBot_Type == L1RobotInstance.TEL_NPC_MOVE) {
			if (bot.huntingBot_Location.equalsIgnoreCase("용던1층")
					|| bot.huntingBot_Location.equalsIgnoreCase("용던2층")
					|| bot.huntingBot_Location.equalsIgnoreCase("용던3층")
					|| bot.huntingBot_Location.equalsIgnoreCase("용던4층")
					|| bot.huntingBot_Location.equalsIgnoreCase("용던5층")
					|| bot.huntingBot_Location.equalsIgnoreCase("용던6층")
					|| bot.huntingBot_Location.equalsIgnoreCase("용던7층")) {
				return 용던입구;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("본던1층")
					|| bot.huntingBot_Location.equalsIgnoreCase("본던2층")
					|| bot.huntingBot_Location.equalsIgnoreCase("본던3층")
					|| bot.huntingBot_Location.equalsIgnoreCase("본던4층")
					|| bot.huntingBot_Location.equalsIgnoreCase("본던5층")
					|| bot.huntingBot_Location.equalsIgnoreCase("본던6층")
					|| bot.huntingBot_Location.equalsIgnoreCase("본던7층")
					|| bot.huntingBot_Location.equalsIgnoreCase("리뉴얼오만1층")// 160316
					|| bot.huntingBot_Location.equalsIgnoreCase("리뉴얼오만2층")
					|| bot.huntingBot_Location.equalsIgnoreCase("리뉴얼오만3층")
					|| bot.huntingBot_Location.equalsIgnoreCase("리뉴얼오만4층")
					|| bot.huntingBot_Location.equalsIgnoreCase("리뉴얼오만5층")
					|| bot.huntingBot_Location.equalsIgnoreCase("리뉴얼오만6층")
					|| bot.huntingBot_Location.equalsIgnoreCase("리뉴얼오만7층")
					|| bot.huntingBot_Location.equalsIgnoreCase("리뉴얼오만8층")
					|| bot.huntingBot_Location.equalsIgnoreCase("리뉴얼오만9층")
					|| bot.huntingBot_Location.equalsIgnoreCase("리뉴얼오만10층")
					|| bot.huntingBot_Location.startsWith("잊섬")
					|| bot.huntingBot_Location.startsWith("지배자1층")
					|| bot.huntingBot_Location.startsWith("지배자2층")) { //イッソム

				return 텔녀; // 元null
			} else if (bot.huntingBot_Location.startsWith("오만")
					|| bot.huntingBot_Location.startsWith("개미굴")) {
				return null;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("기감1층")
					|| bot.huntingBot_Location.equalsIgnoreCase("기감2층")
					|| bot.huntingBot_Location.equalsIgnoreCase("기감3층")
					|| bot.huntingBot_Location.equalsIgnoreCase("기감4층"))
				return 기감입구;
			if (bot.getX() >= 33410 && bot.getX() <= 33461
					&& bot.getY() >= 32788 && bot.getY() <= 32838
					&& bot.getMapId() == 4)//ギラン
				return 텔녀;
		} else if (bot.huntingBot_Type == L1RobotInstance.HUNT_MOVE) {
			bot.tel_Hunting = false;
			if (bot.huntingBot_Location.equalsIgnoreCase("선박심해")) {
				bot.tel_Hunting = true;
				switch (_random.nextInt(3)) {
				case 0:
					return 선박심해;
				case 1:
					return 선박심해2;
				case 2:
					return 선박심해3;
				default:
					break;
				}
			} else if (bot.huntingBot_Location.equalsIgnoreCase("잊섬")) {
				// bot.テル狩り= false;
				switch (_random.nextInt(5)) {
				case 0:
					return 잊섬;
				case 1:
					return 잊섬2;
				case 2:
					return 잊섬3;
				case 3:
					return 잊섬4;
				case 4:
					return 잊섬5;
				default:
					break;
				}
			} else if (bot.huntingBot_Location.equalsIgnoreCase("개미굴1")) {
				bot.tel_Hunting = true;
				return 개미굴1;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("개미굴2")) {
				bot.tel_Hunting = true;
				return 개미굴2;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("개미굴3")) {
				bot.tel_Hunting = true;
				return 개미굴3;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("개미굴4")) {
				bot.tel_Hunting = true;
				return 개미굴4;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("개미굴5")) {
				bot.tel_Hunting = true;
				return 개미굴5;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("개미굴6")) {
				bot.tel_Hunting = true;
				return 개미굴6;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("상아탑4층")) {
				bot.tel_Hunting = true;
				return 상아탑4층;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("상아탑5층")) {
				bot.tel_Hunting = true;
				return 상아탑5층;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("지저")) {
				bot.tel_Hunting = true;
				return 지저;
				// 160316
			} else if (bot.huntingBot_Location.equalsIgnoreCase("리뉴얼오만1층")) {
				bot.tel_Hunting = true;
				return 리뉴얼오만1층;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("리뉴얼오만2층")) {
				bot.tel_Hunting = true;
				return 리뉴얼오만2층;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("리뉴얼오만3층")) {
				bot.tel_Hunting = true;
				return 리뉴얼오만3층;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("리뉴얼오만4층")) {
				bot.tel_Hunting = true;
				return 리뉴얼오만4층;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("리뉴얼오만5층")) {
				bot.tel_Hunting = true;
				return 리뉴얼오만5층;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("리뉴얼오만6층")) {
				bot.tel_Hunting = true;
				return 리뉴얼오만6층;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("리뉴얼오만7층")) {
				bot.tel_Hunting = true;
				return 리뉴얼오만7층;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("리뉴얼오만8층")) {
				bot.tel_Hunting = true;
				return 리뉴얼오만8층;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("리뉴얼오만9층")) {
				bot.tel_Hunting = true;
				return 리뉴얼오만9층;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("리뉴얼오만10층")) {
				bot.tel_Hunting = true;
				return 리뉴얼오만10층;
				// 160316
			} else if (bot.huntingBot_Location.equalsIgnoreCase("용던1층")) {
				bot.tel_Hunting = true;
				return 용던1층;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("용던2층")) {
				bot.tel_Hunting = true;
				return 용던2층;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("용던3층")) {
				bot.tel_Hunting = true;
				return 용던3층;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("용던4층")) {
				bot.tel_Hunting = true;
				return 용던4층;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("용던5층")) {
				bot.tel_Hunting = true;
				return 용던5층;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("용던6층")) {
				bot.tel_Hunting = true;
				return 용던6층;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("용던7층")) {
				bot.tel_Hunting = true;
				return 용던7층;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("본던1층")) {
				bot.tel_Hunting = true;
				return 본던1층;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("본던2층")) {
				bot.tel_Hunting = true;
				return 본던2층;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("본던3층")) {
				bot.tel_Hunting = true;
				return 본던3층;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("본던4층")) {
				bot.tel_Hunting = true;
				return 본던4층;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("본던5층")) {
				bot.tel_Hunting = true;
				return 본던5층;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("본던6층")) {
				bot.tel_Hunting = true;
				return 본던6층;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("본던7층")) {
				bot.tel_Hunting = true;
				return 본던7층;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("기감1층")) {
				bot.tel_Hunting = true;
				return 기감1층;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("기감2층")) {
				bot.tel_Hunting = true;
				return 기감2층;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("기감3층")) {
				bot.tel_Hunting = true;
				return 기감3층;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("기감4층")) {
				bot.tel_Hunting = true;
				return 기감4층;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("지배자1층")) {
				bot.tel_Hunting = true;
				return 지배자1층;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("지배자2층")) {
				bot.tel_Hunting = true;
				return 지배자2층;
			}
		}
		return null;
	}

	public static void setRLOC() {
		//ポーション、倉庫、バフ
		기란셋팅.add(new Robot_Location_bean(33457, 32819, 4));
		기란셋팅.add(new Robot_Location_bean(33431, 32816, 4));
		기란셋팅.add(new Robot_Location_bean(33437, 32804, 4));
		기란셋팅2.add(new Robot_Location_bean(33432, 32815, 4));
		기란셋팅2.add(new Robot_Location_bean(33457, 32820, 4));
		기란셋팅2.add(new Robot_Location_bean(33437, 32804, 4));
		기란셋팅3.add(new Robot_Location_bean(33428, 32806, 4));
		기란셋팅3.add(new Robot_Location_bean(33422, 32813, 4));
		기란셋팅3.add(new Robot_Location_bean(33437, 32803, 4));
		기란셋팅4.add(new Robot_Location_bean(33437, 32803, 4));
		기란셋팅5.add(new Robot_Location_bean(33428, 32806, 4));
		기란셋팅5.add(new Robot_Location_bean(33440, 32801, 4));

		오렌셋팅.add(new Robot_Location_bean(34065, 32287, 4));
		오렌셋팅.add(new Robot_Location_bean(34053, 32287, 4));
		오렌셋팅.add(new Robot_Location_bean(34064, 32279, 4));
		글말셋팅.add(new Robot_Location_bean(32596, 32741, 4));
		글말셋팅.add(new Robot_Location_bean(32609, 32735, 4));
		웰던셋팅.add(new Robot_Location_bean(33738, 32494, 4));
		웰던셋팅.add(new Robot_Location_bean(33723, 32488, 4));
		웰던셋팅.add(new Robot_Location_bean(33714, 32498, 4));
		//ギランテルニョ移動
		텔녀.add(new Robot_Location_bean(33437, 32795, 4));
		// ヨンドン入口
		용던입구.add(new Robot_Location_bean(33446, 32828, 4));
		// 技監入口
		기감입구.add(new Robot_Location_bean(33428, 32820, 4));
		// ボンドン入口
		본던입구.add(new Robot_Location_bean(32727, 32929, 4));
		

		지저.add(new Robot_Location_bean(32800, 33051, 420));

		잊섬.add(new Robot_Location_bean(32645, 33009, 1700)); //口繊細プチジョン入口
		잊섬2.add(new Robot_Location_bean(32754, 32942, 1700)); //南ゴーレム研究所の入口
		잊섬3.add(new Robot_Location_bean(32694, 32716, 1700)); //西ゴーレムの入口
		잊섬4.add(new Robot_Location_bean(32926, 32890, 1700)); //東ゴーレム入口
		잊섬5.add(new Robot_Location_bean(32955, 32800, 1700)); //ヒドゥン店

		지배자1층.add(new Robot_Location_bean(32794, 32853, 15403)); //支配者の結界1階
		지배자2층.add(new Robot_Location_bean(32678, 32860, 15404)); //支配者の結界2階

		선박심해.add(new Robot_Location_bean(33011, 33011, 558));
		선박심해2.add(new Robot_Location_bean(33011, 33012, 558));
		선박심해3.add(new Robot_Location_bean(33011, 33013, 558));

		용던1층.add(new Robot_Location_bean(32799, 32742, 30));
		용던2층.add(new Robot_Location_bean(32761, 32788, 31));
		용던3층.add(new Robot_Location_bean(32703, 32833, 32));
		용던4층.add(new Robot_Location_bean(32677, 32860, 33));
		용던5층.add(new Robot_Location_bean(32742, 32794, 35));
		용던6층.add(new Robot_Location_bean(32666, 32862, 36));
		용던7층.add(new Robot_Location_bean(32664, 32838, 37));

		// 160316
		리뉴얼오만1층.add(new Robot_Location_bean(32725, 32794, 101));
		리뉴얼오만2층.add(new Robot_Location_bean(32730, 32802, 102));
		리뉴얼오만3층.add(new Robot_Location_bean(32726, 32802, 103));
		리뉴얼오만4층.add(new Robot_Location_bean(32620, 32858, 104));
		리뉴얼오만5층.add(new Robot_Location_bean(32602, 32866, 105));
		리뉴얼오만6층.add(new Robot_Location_bean(32611, 32862, 106));
		리뉴얼오만7층.add(new Robot_Location_bean(32618, 32865, 107));
		리뉴얼오만8층.add(new Robot_Location_bean(32602, 32866, 108));
		리뉴얼오만9층.add(new Robot_Location_bean(32613, 32866, 109));
		리뉴얼오만10층.add(new Robot_Location_bean(32730, 32802, 110));
		// 160316

		본던1층.add(new Robot_Location_bean(32812, 32726, 807));
		본던2층.add(new Robot_Location_bean(32750, 32798, 808));
		본던3층.add(new Robot_Location_bean(32801, 32754, 809));
		본던4층.add(new Robot_Location_bean(32763, 32773, 810));
		본던5층.add(new Robot_Location_bean(32728, 32723, 811));
		본던6층.add(new Robot_Location_bean(32804, 32725, 812));
		본던7층.add(new Robot_Location_bean(32727, 32725, 813));

		상아탑4층.add(new Robot_Location_bean(32901, 32765, 280));
		상아탑5층.add(new Robot_Location_bean(32810, 32865, 281));

		개미굴1.add(new Robot_Location_bean(32784, 32751, 43));
		개미굴2.add(new Robot_Location_bean(32798, 32754, 44));
		개미굴3.add(new Robot_Location_bean(32759, 32742, 45));
		개미굴4.add(new Robot_Location_bean(32750, 32764, 46));
		개미굴5.add(new Robot_Location_bean(32795, 32746, 47));
		개미굴6.add(new Robot_Location_bean(32768, 32805, 50));

		기감1층.add(new Robot_Location_bean(32805, 32738, 53));
		기감2층.add(new Robot_Location_bean(32808, 32796, 54));
		기감3층.add(new Robot_Location_bean(32736, 32729, 55));
		기감4층.add(new Robot_Location_bean(32768, 32820, 56));

	}

}