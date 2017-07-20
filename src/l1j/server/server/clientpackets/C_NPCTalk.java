package l1j.server.server.clientpackets;

import l1j.server.IndunSystem.MiniGame.L1Gambling;
import l1j.server.server.GameClient;
import l1j.server.server.datatables.NpcActionTable;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.npc.L1NpcHtml;
import l1j.server.server.model.npc.action.L1NpcAction;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_TelePortUi;

public class C_NPCTalk extends ClientBasePacket {

	private static final String C_NPC_TALK = "[C] C_NPCTalk";
	//private static Logger _log = Logger.getLogger(C_NPCTalk.class.getName());
	
	private static String[] action = new String[] { "T_talk island", "T_gludio", "T_orc", "T_woodbec",
			"T_silver knight", "T_kent", "T_giran", "T_heine", "T_werldern", "T_oren", "T_aden", "T_scave",
			"T_behemoth", "T_silveria", "T_pcbang", "D_talk island", "D_gludio", "D_elven", "D_training", "D_barlog",
			"D_dragon valley", "D_eva king", "D_ivory tower", "D_yahee", "F_shelob", "F_orc forest", "F_ruin of death",
			"F_desert", "F_dragon valley", "F_halpas", "F_valakas", "F_jungle", "F_heine", "F_mirror", "F_elmor",
			"F_lindvior", "F_giant" };

	private static String[] claudiaact = new String[] { "Tel_talking", "Tel_watera", "Tel_waterb", "Tel_spidercave",
			"Tel_field", "Tel_underworld", "Tel_orcvil", "Tel_deathcastle", "Tel_swamp" };

	private static int[] T_talk_island = new int[] { 0, 1175, 1481, 1458, 1884, 1461, 1727, 2123, 2087, 2470, 2429,
			2024, 2107, 2470, 0, 140, 1343, 1717, 1905, 1717, 1945, 2328, 2532, 2663, 47, 1447, 1327, 1647, 1799, 1662,
			2085, 1928, 2060, 2251, 2247, 2155, 2493 };

	private static int[] T_gludio = new int[] { 1175, 0, 321, 283, 709, 298, 552, 948, 927, 1310, 1254, 864, 947, 1405,
			0, 1315, 169, 557, 731, 542, 785, 1153, 1372, 1503, 1207, 287, 167, 473, 639, 487, 925, 768, 885, 1077,
			1087, 981, 1318 };

	private static int[] T_orc = new int[] { 1481, 321, 0, 567, 845, 421, 687, 1083, 671, 989, 1389, 543, 627, 1085, 0,
			1554, 340, 237, 866, 677, 464, 1289, 1051, 1182, 1527, 76, 153, 608, 328, 623, 605, 874, 1021, 1212, 979,
			1116, 1453 };

	private static int[] T_woodbec = new int[] { 1458, 283, 567, 0, 426, 544, 794, 665, 1173, 1556, 971, 1110, 1193,
			1651, 0, 1598, 227, 803, 447, 259, 1031, 870, 1618, 1749, 1411, 533, 413, 189, 885, 453, 1171, 1014, 602,
			793, 1333, 1147, 1137 };

	private static int[] T_silver_knight = new int[] { 1884, 709, 845, 426, 0, 423, 629, 447, 1008, 1391, 590, 945,
			1029, 1487, 0, 2024, 541, 832, 59, 266, 866, 444, 1453, 1584, 1837, 921, 735, 237, 721, 289, 1007, 849, 224,
			471, 1169, 982, 973 };

	private static int[] T_kent = new int[] { 1461, 298, 421, 544, 423, 0, 266, 662, 629, 1012, 968, 566, 649, 1107, 0,
			1601, 317, 409, 445, 645, 487, 867, 1074, 1205, 1505, 497, 312, 440, 341, 201, 627, 470, 599, 791, 789, 695,
			1032 };

	private static int[] T_giran = new int[] { 1727, 552, 687, 794, 629, 266, 0, 396, 379, 762, 702, 380, 399, 857, 0,
			1867, 567, 675, 571, 895, 275, 601, 824, 855, 1755, 763, 578, 690, 359, 341, 377, 220, 405, 525, 539, 429,
			766 };

	private static int[] T_heine = new int[] { 2123, 948, 1083, 665, 447, 662, 396, 0, 561, 945, 306, 776, 582, 1040, 0,
			2263, 779, 1071, 388, 713, 671, 205, 1007, 1137, 2076, 1159, 974, 507, 755, 461, 560, 403, 223, 129, 772,
			535, 526 };

	private static int[] T_werldern = new int[] { 2087, 927, 671, 1173, 1008, 629, 379, 561, 0, 383, 718, 364, 111, 479,
			0, 2160, 946, 659, 949, 1274, 259, 725, 445, 576, 2133, 747, 759, 1069, 343, 719, 123, 203, 784, 541, 307,
			445, 782 };

	private static int[] T_oren = new int[] { 2470, 1310, 989, 1556, 1391, 1012, 762, 945, 383, 0, 801, 453, 363, 95, 0,
			2543, 1329, 753, 133, 1657, 525, 1109, 91, 193, 2517, 1023, 1143, 1452, 671, 1103, 385, 542, 1167, 920, 223,
			409, 693 };

	private static int[] T_aden = new int[] { 2429, 1254, 1389, 971, 590, 968, 702, 306, 718, 801, 0, 1082, 829, 897, 0,
			2569, 1085, 1377, 531, 856, 977, 307, 863, 994, 2382, 1465, 1280, 781, 1061, 767, 841, 515, 369, 177, 579,
			392, 383 };

	private static int[] T_scave = new int[] { 2024, 864, 543, 1110, 945, 566, 380, 776, 364, 453, 1082, 0, 253, 541, 0,
			2097, 883, 307, 887, 1211, 105, 971, 508, 639, 2071, 577, 697, 1006, 225, 657, 241, 567, 721, 905, 671, 809,
			1146 };

	private static int[] T_behemoth = new int[] { 2107, 947, 627, 1193, 1029, 649, 399, 582, 111, 363, 829, 253, 0, 458,
			0, 2181, 967, 547, 970, 1295, 163, 746, 425, 555, 2154, 660, 780, 1089, 308, 740, 22, 314, 805, 652, 419,
			556, 893 };

	private static int[] T_silveria = new int[] { 2565, 1405, 1085, 1651, 1487, 1107, 857, 1040, 479, 95, 897, 541, 458,
			0, 0, 2639, 1425, 848, 1428, 1753, 621, 1204, 37, 129, 2612, 1118, 1238, 1547, 766, 1198, 480, 637, 1263,
			1015, 318, 505, 747 };

	private static int[] T_claudia = new int[] { 100, 0, 0, 0, 0, 0, 0, 0, 0 };
	
	public C_NPCTalk(byte abyte0[], GameClient client) throws Exception {
		super(abyte0);
		int objid = readD();
		L1Object obj = L1World.getInstance().findObject(objid);
		L1PcInstance pc = client.getActiveChar();	
		if (pc == null) return;		
		
		//機器変更権を使ってキャンセルしたときに備えて、行を追加
		pc.setIsChangeItem(false);
		
		int npcid = ((L1NpcInstance) obj).getNpcTemplate().get_npcId();		
		int mapvalue = 0;
		mapvalue = action.length;
		switch (npcid) {
		case 50015:// 話せる島（ルーカス）
			pc.sendPackets(new S_TelePortUi(objid, action, T_talk_island, mapvalue));
			break;
		case 50024:// グルーディン村（アスター）
			pc.sendPackets(new S_TelePortUi(objid, action, T_gludio, mapvalue));
			break;
		case 50082:// 火田村（ジープとは）
			pc.sendPackets(new S_TelePortUi(objid, action, T_orc, mapvalue));
			break;
		case 50054:// ウッドベック村（トレイ）
			pc.sendPackets(new S_TelePortUi(objid, action, T_woodbec, mapvalue));
			break;
		case 50056: // はナイトの村（メタ）
			pc.sendPackets(new S_TelePortUi(objid, action, T_silver_knight, mapvalue));
			break;
		case 50020:// ケント城の村（スタンレー）
			pc.sendPackets(new S_TelePortUi(objid, action, T_kent, mapvalue));
			break;
		case 50036:// ギラン村（ウィルマ）
			pc.sendPackets(new S_TelePortUi(objid, action, T_giran, mapvalue));
			break;
		case 50066://ハイネ（リオル）
			pc.sendPackets(new S_TelePortUi(objid, action, T_heine, mapvalue));
			break;
		case 50039:// ウェルダン村（レスリー）
			pc.sendPackets(new S_TelePortUi(objid, action, T_werldern, mapvalue));
			break;
		case 50051:// 象牙の塔の村（キーウス）
			pc.sendPackets(new S_TelePortUi(objid, action, T_oren, mapvalue));
			break;
		case 50044:
		case 50046:// アデン（エレレス）
			pc.sendPackets(new S_TelePortUi(objid, action, T_aden, mapvalue));
			break;
		case 50079:// 沈黙の洞窟の村（ダニエル）
			pc.sendPackets(new S_TelePortUi(objid, action, T_scave, mapvalue));
			break;
		case 3000005:// ベヒーモス（デカビア）
			pc.sendPackets(new S_TelePortUi(objid, action, T_behemoth, mapvalue));
			break;
		case 3100005:// シルベリア（シャーリーエル）
			pc.sendPackets(new S_TelePortUi(objid, action, T_silveria, mapvalue));
			break;
	      case 5069: //リンジー
	          pc.sendPackets(new S_TelePortUi(objid, action, T_giran, mapvalue));
	          break;
		case 202055: // クラウディア（ソフィー）
			mapvalue = claudiaact.length;
			pc.sendPackets(new S_TelePortUi(objid, claudiaact, T_claudia, mapvalue));
			break;
		}
		
		L1Gambling gam = new L1Gambling();		
		if (obj != null && pc != null) {
			L1NpcAction action = NpcActionTable.getInstance().get(pc, obj);
			if (action != null) {
				L1NpcHtml html = action.execute("", pc, obj, new byte[0]);
				if (html != null) {
					pc.sendPackets(new S_NPCTalkReturn(obj.getId(), html));
				}
				return;
			}
			/** ミニゲーム **/
			if (obj instanceof L1NpcInstance) {
				L1NpcInstance npc = (L1NpcInstance) obj;
				if (npc.getNpcTemplate().get_npcId() == 400064 || npc.getNpcTemplate().get_npcId() == 400065 ||npc.getNpcTemplate().get_npcId() == 400066) {
					pc.gembleNpc = npc;
					gam.dealerTrade(pc);
				}
			}
			obj.onTalkAction(pc);
		} else {
		//	 _log.severe(pc.getName() + " x:" + pc.getX() + " y:" + pc.getY() + " map:" + pc.getMapId() + " objid=" + objid);
		}
	}



	@Override
	public String getType() {
		return C_NPC_TALK;
	}
}