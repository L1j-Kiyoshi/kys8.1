package l1j.server.GameSystem.Robot;

import java.util.ArrayList;
import java.util.Random;

public class Robot_Location {

	private static Random _random = new Random(System.currentTimeMillis());

	private static ArrayList<Robot_Location_bean> conf_Giran = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> conf_Giran2 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> conf_Giran3 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> conf_Giran4 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> conf_Giran5 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> conf_Oren = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> conf_Gludin = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> conf_Werldern = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> Teleporter = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> DVC_Entrance = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> MLC_Entrance = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> GiranPrizon_Entrance = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> IT4F = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> IT5F = new ArrayList<Robot_Location_bean>();

	private static ArrayList<Robot_Location_bean> Underground = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> DVC1F = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> DVC2F = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> DVC3F = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> DVC4F = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> DVC5F = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> DVC6F = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> DVC7F = new ArrayList<Robot_Location_bean>();

	private static ArrayList<Robot_Location_bean> Re_TOI1F = new ArrayList<Robot_Location_bean>(); // 160316
	private static ArrayList<Robot_Location_bean> Re_TOI2F = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> Re_TOI3F = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> Re_TOI4F = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> Re_TOI5F = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> Re_TOI6F = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> Re_TOI7F = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> Re_TOI8F = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> Re_TOI9F = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> Re_TOI10F = new ArrayList<Robot_Location_bean>();

	private static ArrayList<Robot_Location_bean> MLC1F = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> MLC2F = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> MLC3F = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> MLC4F = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> MLC5F = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> MLC6F = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> MLC7F = new ArrayList<Robot_Location_bean>();

	private static ArrayList<Robot_Location_bean> GraveyardShip_Seafloor = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> GraveyardShip_Seafloor2 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> GraveyardShip_Seafloor3 = new ArrayList<Robot_Location_bean>();

	private static ArrayList<Robot_Location_bean> FI = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> FI2 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> FI3 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> FI4 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> FI5 = new ArrayList<Robot_Location_bean>();

	private static ArrayList<Robot_Location_bean> DesireCave1F = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> DesireCave2F = new ArrayList<Robot_Location_bean>();

	private static ArrayList<Robot_Location_bean> AntCave1 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> AntCave2 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> AntCave3 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> AntCave4 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> AntCave5 = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> AntCave6 = new ArrayList<Robot_Location_bean>();

	private static ArrayList<Robot_Location_bean> GiranPrison1F = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> GiranPrison2F = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> GiranPrison3F = new ArrayList<Robot_Location_bean>();
	private static ArrayList<Robot_Location_bean> GiranPrison4F = new ArrayList<Robot_Location_bean>();

	public static void registLocation(int x, int y, int m) {

	}

	public static ArrayList<Robot_Location_bean> Location(L1RobotInstance bot) {
		_random.setSeed(System.currentTimeMillis());
		if (bot.huntingBot_Type == L1RobotInstance.SETTING) {
			if (bot.huntingBot_Location.equalsIgnoreCase("DVC1階") || bot.huntingBot_Location.equalsIgnoreCase("DVC2階")
					|| bot.huntingBot_Location.equalsIgnoreCase("DVC3階")
					|| bot.huntingBot_Location.equalsIgnoreCase("DVC4階")
					|| bot.huntingBot_Location.equalsIgnoreCase("DVC5階")
					|| bot.huntingBot_Location.equalsIgnoreCase("DVC6階")
					|| bot.huntingBot_Location.equalsIgnoreCase("DVC7階")) {
				return _random.nextInt(1000) > 500 ? conf_Giran : conf_Giran2;
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
				return _random.nextInt(1000) >= 500 ? conf_Giran2 : conf_Giran5;
			case 8:
			case 4:
			case 1:
				return conf_Oren;
			case 13:
			case 12:
				return conf_Giran4;
			case 10:
			case 9:
			case 5:
			case 2:
				return _random.nextInt(1000) >= 500 ? conf_Giran : conf_Giran3;
			case 14:
			case 6:
			case 3:
				return conf_Gludin;
			default:
				break;
			}
			return conf_Giran;
		} else if (bot.huntingBot_Type == L1RobotInstance.TEL_NPC_MOVE) {
			if (bot.huntingBot_Location.equalsIgnoreCase("DVC1階") || bot.huntingBot_Location.equalsIgnoreCase("DVC2階")
					|| bot.huntingBot_Location.equalsIgnoreCase("DVC3階")
					|| bot.huntingBot_Location.equalsIgnoreCase("DVC4階")
					|| bot.huntingBot_Location.equalsIgnoreCase("DVC5階")
					|| bot.huntingBot_Location.equalsIgnoreCase("DVC6階")
					|| bot.huntingBot_Location.equalsIgnoreCase("DVC7階")) {
				return DVC_Entrance;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("MLC1階")
					|| bot.huntingBot_Location.equalsIgnoreCase("MLC2階")
					|| bot.huntingBot_Location.equalsIgnoreCase("MLC3階")
					|| bot.huntingBot_Location.equalsIgnoreCase("MLC4階")
					|| bot.huntingBot_Location.equalsIgnoreCase("MLC5階")
					|| bot.huntingBot_Location.equalsIgnoreCase("MLC6階")
					|| bot.huntingBot_Location.equalsIgnoreCase("MLC7階")
					|| bot.huntingBot_Location.equalsIgnoreCase("リニューアル傲慢1階")// 160316
					|| bot.huntingBot_Location.equalsIgnoreCase("リニューアル傲慢2階")
					|| bot.huntingBot_Location.equalsIgnoreCase("リニューアル傲慢3階")
					|| bot.huntingBot_Location.equalsIgnoreCase("リニューアル傲慢4階")
					|| bot.huntingBot_Location.equalsIgnoreCase("リニューアル傲慢5階")
					|| bot.huntingBot_Location.equalsIgnoreCase("リニューアル傲慢6階")
					|| bot.huntingBot_Location.equalsIgnoreCase("リニューアル傲慢7階")
					|| bot.huntingBot_Location.equalsIgnoreCase("リニューアル傲慢8階")
					|| bot.huntingBot_Location.equalsIgnoreCase("リニューアル傲慢9階")
					|| bot.huntingBot_Location.equalsIgnoreCase("リニューアル傲慢10階")
					|| bot.huntingBot_Location.startsWith("忘れられた島") || bot.huntingBot_Location.startsWith("支配者1階")
					|| bot.huntingBot_Location.startsWith("支配者2階")) { // 忘れられた島

				return Teleporter; // 元null
			} else if (bot.huntingBot_Location.startsWith("傲慢") || bot.huntingBot_Location.startsWith("アリの巣")) {
				return null;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("技監1階")
					|| bot.huntingBot_Location.equalsIgnoreCase("技監2階")
					|| bot.huntingBot_Location.equalsIgnoreCase("技監3階")
					|| bot.huntingBot_Location.equalsIgnoreCase("技監4階"))
				return GiranPrizon_Entrance;
			if (bot.getX() >= 33410 && bot.getX() <= 33461 && bot.getY() >= 32788 && bot.getY() <= 32838
					&& bot.getMapId() == 4)// ギラン
				return Teleporter;
		} else if (bot.huntingBot_Type == L1RobotInstance.HUNT_MOVE) {
			bot.tel_Hunting = false;
			if (bot.huntingBot_Location.equalsIgnoreCase("船舶深海")) {
				bot.tel_Hunting = true;
				switch (_random.nextInt(3)) {
				case 0:
					return GraveyardShip_Seafloor;
				case 1:
					return GraveyardShip_Seafloor2;
				case 2:
					return GraveyardShip_Seafloor3;
				default:
					break;
				}
			} else if (bot.huntingBot_Location.equalsIgnoreCase("忘れられた島")) {
				// bot.テル狩り= false;
				switch (_random.nextInt(5)) {
				case 0:
					return FI;
				case 1:
					return FI2;
				case 2:
					return FI3;
				case 3:
					return FI4;
				case 4:
					return FI5;
				default:
					break;
				}
			} else if (bot.huntingBot_Location.equalsIgnoreCase("アリの巣1")) {
				bot.tel_Hunting = true;
				return AntCave1;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("アリの巣2")) {
				bot.tel_Hunting = true;
				return AntCave2;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("アリの巣3")) {
				bot.tel_Hunting = true;
				return AntCave3;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("蟻のダンジョン4")) {
				bot.tel_Hunting = true;
				return AntCave4;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("アリの巣5")) {
				bot.tel_Hunting = true;
				return AntCave5;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("アリの巣6")) {
				bot.tel_Hunting = true;
				return AntCave6;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("象牙の塔4階")) {
				bot.tel_Hunting = true;
				return IT4F;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("象牙の塔5階")) {
				bot.tel_Hunting = true;
				return IT5F;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("地底")) {
				bot.tel_Hunting = true;
				return Underground;
				// 160316
			} else if (bot.huntingBot_Location.equalsIgnoreCase("リニューアル傲慢1階")) {
				bot.tel_Hunting = true;
				return Re_TOI1F;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("リニューアル傲慢2階")) {
				bot.tel_Hunting = true;
				return Re_TOI2F;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("リニューアル傲慢3階")) {
				bot.tel_Hunting = true;
				return Re_TOI3F;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("リニューアル傲慢4階")) {
				bot.tel_Hunting = true;
				return Re_TOI4F;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("リニューアル傲慢5階")) {
				bot.tel_Hunting = true;
				return Re_TOI5F;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("リニューアル傲慢6階")) {
				bot.tel_Hunting = true;
				return Re_TOI6F;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("リニューアル傲慢7階")) {
				bot.tel_Hunting = true;
				return Re_TOI7F;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("リニューアル傲慢8階")) {
				bot.tel_Hunting = true;
				return Re_TOI8F;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("リニューアル傲慢9階")) {
				bot.tel_Hunting = true;
				return Re_TOI9F;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("リニューアル傲慢10階")) {
				bot.tel_Hunting = true;
				return Re_TOI10F;
				// 160316
			} else if (bot.huntingBot_Location.equalsIgnoreCase("DVC1階")) {
				bot.tel_Hunting = true;
				return DVC1F;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("DVC2階")) {
				bot.tel_Hunting = true;
				return DVC2F;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("DVC3階")) {
				bot.tel_Hunting = true;
				return DVC3F;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("DVC4階")) {
				bot.tel_Hunting = true;
				return DVC4F;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("DVC5階")) {
				bot.tel_Hunting = true;
				return DVC5F;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("DVC6階")) {
				bot.tel_Hunting = true;
				return DVC6F;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("DVC7階")) {
				bot.tel_Hunting = true;
				return DVC7F;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("MLC1階")) {
				bot.tel_Hunting = true;
				return MLC1F;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("MLC2階")) {
				bot.tel_Hunting = true;
				return MLC2F;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("MLC3階")) {
				bot.tel_Hunting = true;
				return MLC3F;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("MLC4階")) {
				bot.tel_Hunting = true;
				return MLC4F;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("MLC5階")) {
				bot.tel_Hunting = true;
				return MLC5F;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("MLC6階")) {
				bot.tel_Hunting = true;
				return MLC6F;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("MLC7階")) {
				bot.tel_Hunting = true;
				return MLC7F;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("ギラン監獄1階")) {
				bot.tel_Hunting = true;
				return GiranPrison1F;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("ギラン監獄2階")) {
				bot.tel_Hunting = true;
				return GiranPrison2F;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("ギラン監獄3階")) {
				bot.tel_Hunting = true;
				return GiranPrison3F;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("ギラン監獄4階")) {
				bot.tel_Hunting = true;
				return GiranPrison4F;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("支配者1階")) {
				bot.tel_Hunting = true;
				return DesireCave1F;
			} else if (bot.huntingBot_Location.equalsIgnoreCase("支配者2階")) {
				bot.tel_Hunting = true;
				return DesireCave2F;
			}
		}
		return null;
	}

	public static void setRLOC() {
		// ポーション、倉庫、バフ
		conf_Giran.add(new Robot_Location_bean(33457, 32819, 4));
		conf_Giran.add(new Robot_Location_bean(33431, 32816, 4));
		conf_Giran.add(new Robot_Location_bean(33437, 32804, 4));
		conf_Giran2.add(new Robot_Location_bean(33432, 32815, 4));
		conf_Giran2.add(new Robot_Location_bean(33457, 32820, 4));
		conf_Giran2.add(new Robot_Location_bean(33437, 32804, 4));
		conf_Giran3.add(new Robot_Location_bean(33428, 32806, 4));
		conf_Giran3.add(new Robot_Location_bean(33422, 32813, 4));
		conf_Giran3.add(new Robot_Location_bean(33437, 32803, 4));
		conf_Giran4.add(new Robot_Location_bean(33437, 32803, 4));
		conf_Giran5.add(new Robot_Location_bean(33428, 32806, 4));
		conf_Giran5.add(new Robot_Location_bean(33440, 32801, 4));

		conf_Oren.add(new Robot_Location_bean(34065, 32287, 4));
		conf_Oren.add(new Robot_Location_bean(34053, 32287, 4));
		conf_Oren.add(new Robot_Location_bean(34064, 32279, 4));
		conf_Gludin.add(new Robot_Location_bean(32596, 32741, 4));
		conf_Gludin.add(new Robot_Location_bean(32609, 32735, 4));
		conf_Werldern.add(new Robot_Location_bean(33738, 32494, 4));
		conf_Werldern.add(new Robot_Location_bean(33723, 32488, 4));
		conf_Werldern.add(new Robot_Location_bean(33714, 32498, 4));
		// ギランテレポーター移動
		Teleporter.add(new Robot_Location_bean(33437, 32795, 4));
		// DVC入口
		DVC_Entrance.add(new Robot_Location_bean(33446, 32828, 4));
		// ギラン監獄入口
		GiranPrizon_Entrance.add(new Robot_Location_bean(33428, 32820, 4));
		// MLC入口
		MLC_Entrance.add(new Robot_Location_bean(32727, 32929, 4));

		Underground.add(new Robot_Location_bean(32800, 33051, 420));

		FI.add(new Robot_Location_bean(32645, 33009, 1700)); // 口繊細プチジョン入口
		FI2.add(new Robot_Location_bean(32754, 32942, 1700)); // 南ゴーレム研究所の入口
		FI3.add(new Robot_Location_bean(32694, 32716, 1700)); // 西ゴーレムの入口
		FI4.add(new Robot_Location_bean(32926, 32890, 1700)); // 東ゴーレム入口
		FI5.add(new Robot_Location_bean(32955, 32800, 1700)); // ヒドゥン店

		DesireCave1F.add(new Robot_Location_bean(32794, 32853, 15403)); // 支配者の結界1階
		DesireCave2F.add(new Robot_Location_bean(32678, 32860, 15404)); // 支配者の結界2階

		GraveyardShip_Seafloor.add(new Robot_Location_bean(33011, 33011, 558));
		GraveyardShip_Seafloor2.add(new Robot_Location_bean(33011, 33012, 558));
		GraveyardShip_Seafloor3.add(new Robot_Location_bean(33011, 33013, 558));

		DVC1F.add(new Robot_Location_bean(32799, 32742, 30));
		DVC2F.add(new Robot_Location_bean(32761, 32788, 31));
		DVC3F.add(new Robot_Location_bean(32703, 32833, 32));
		DVC4F.add(new Robot_Location_bean(32677, 32860, 33));
		DVC5F.add(new Robot_Location_bean(32742, 32794, 35));
		DVC6F.add(new Robot_Location_bean(32666, 32862, 36));
		DVC7F.add(new Robot_Location_bean(32664, 32838, 37));

		// 160316
		Re_TOI1F.add(new Robot_Location_bean(32725, 32794, 101));
		Re_TOI2F.add(new Robot_Location_bean(32730, 32802, 102));
		Re_TOI3F.add(new Robot_Location_bean(32726, 32802, 103));
		Re_TOI4F.add(new Robot_Location_bean(32620, 32858, 104));
		Re_TOI5F.add(new Robot_Location_bean(32602, 32866, 105));
		Re_TOI6F.add(new Robot_Location_bean(32611, 32862, 106));
		Re_TOI7F.add(new Robot_Location_bean(32618, 32865, 107));
		Re_TOI8F.add(new Robot_Location_bean(32602, 32866, 108));
		Re_TOI9F.add(new Robot_Location_bean(32613, 32866, 109));
		Re_TOI10F.add(new Robot_Location_bean(32730, 32802, 110));
		// 160316

		MLC1F.add(new Robot_Location_bean(32812, 32726, 807));
		MLC2F.add(new Robot_Location_bean(32750, 32798, 808));
		MLC3F.add(new Robot_Location_bean(32801, 32754, 809));
		MLC4F.add(new Robot_Location_bean(32763, 32773, 810));
		MLC5F.add(new Robot_Location_bean(32728, 32723, 811));
		MLC6F.add(new Robot_Location_bean(32804, 32725, 812));
		MLC7F.add(new Robot_Location_bean(32727, 32725, 813));

		IT4F.add(new Robot_Location_bean(32901, 32765, 280));
		IT5F.add(new Robot_Location_bean(32810, 32865, 281));

		AntCave1.add(new Robot_Location_bean(32784, 32751, 43));
		AntCave2.add(new Robot_Location_bean(32798, 32754, 44));
		AntCave3.add(new Robot_Location_bean(32759, 32742, 45));
		AntCave4.add(new Robot_Location_bean(32750, 32764, 46));
		AntCave5.add(new Robot_Location_bean(32795, 32746, 47));
		AntCave6.add(new Robot_Location_bean(32768, 32805, 50));

		GiranPrison1F.add(new Robot_Location_bean(32805, 32738, 53));
		GiranPrison2F.add(new Robot_Location_bean(32808, 32796, 54));
		GiranPrison3F.add(new Robot_Location_bean(32736, 32729, 55));
		GiranPrison4F.add(new Robot_Location_bean(32768, 32820, 56));
	}
}