package l1j.server.server.model.item.function;

import static l1j.server.server.model.skill.L1SkillId.*;

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
			pc.sendPackets(new S_SystemMessage("バトルゾーンで使用することができません。"));
			return;
		}
		if (pc.isParalyzed() || pc.isSleeped() || pc.isDead()) {
			return;
		}
		if (!pc.getMap().isEscapable()) {
			pc.sendPackets(new S_SystemMessage("周囲のわからない魔力によってテレポートすることができません。"));
			return;
		}
		if ((pc.hasSkillEffect(SHOCK_STUN)) || (pc.hasSkillEffect(ICE_LANCE)) || (pc.hasSkillEffect(BONE_BREAK))
				|| (pc.hasSkillEffect(THUNDER_GRAB)) || (pc.hasSkillEffect(EARTH_BIND))) {
			return;
		}
		
		if (itemId == 560025) {
			try {
				final int[][] memory_Village = { 
						{ 34060, 32281, 4 }, // オーレン
						{ 33079, 33390, 4 }, // ナイト
						{ 32750, 32439, 4 }, // オークの森
						{ 32612, 33188, 4 }, // ウィンダウッド
						{ 33720, 32492, 4 }, // ウェルダン
						{ 32872, 32912, 304 }, // 沈黙の洞窟
						{ 32612, 32781, 4 }, // グルーディオ
						{ 33067, 32803, 4 }, // ケント
						{ 33933, 33358, 4 }, // アデン
						{ 33601, 33232, 4 }, // ハイネ
						{ 32574, 32942, 0 }, // 話せる島
						{ 33430, 32815, 4 }, }; // ギラン
				int[] a = memory_Village[BookTel];
				if (a != null) {
					new L1Teleport().teleport(pc, a[0], a[1], (short) a[2], pc.getHeading(), true);
					pc.getInventory().removeItem(l1iteminstance, 1);
				}
			} catch (Exception e) {}
		} else if (itemId == 560027) {
			try {
				final int[][] memory_Dungeon = { 
						{ 32791, 32800, 101 }, // 傲慢1
						{ 32764, 32842, 77 }, // オーレンサンタプ3
						{ 32676, 32859, 59 }, // ハイネ1
						{ 34267, 32189, 4 }, // グシン
						{ 32760, 33461, 4 }, // 欲望
						{ 32841, 32695, 550 }, // 船舶
				};
				int[] b = memory_Dungeon[BookTel];
				if (b != null) {
					new L1Teleport().teleport(pc, b[0], b[1], (short) b[2], pc.getHeading(), true);
					pc.getInventory().removeItem(l1iteminstance, 1);
				}
			} catch (Exception e) {
				
			}

		} else if (itemId == 560028) {
			try {
				final int[][] memory_TOI = { 
						{ 32735, 32798, 101 }, // 傲慢1
						{ 32730, 32802, 102 }, // 傲慢2
						{ 32726, 32803, 103 }, // 傲慢3
						{ 32621, 32858, 104 }, // 傲慢4
						{ 32599, 32866, 105 }, // 傲慢5
						{ 32611, 32862, 106 }, // 傲慢6
						{ 32618, 32866, 107 }, // 傲慢7
						{ 32600, 32866, 108 }, // 傲慢8
						{ 32612, 32866, 109 }, // 傲慢9
						{ 32729, 32802, 110 }, // 傲慢10
						{ 32646, 32808, 111 }, 	// 傲慢、通常の起動ポイント
						{ 32801, 32963, 111 },};// 傲慢正常中間地点
				int[] c = memory_TOI[BookTel];
				if (c != null) {
					new L1Teleport().teleport(pc, c[0], c[1], (short) c[2], pc.getHeading(), true);

				}
			} catch (Exception e) {}
			
		} else if (itemId == 560029) {
			try {
				final int[][] memory_Encounter = { 
						 { 00000, 00000, 0 }, // 低レベル推奨狩り場
					      { 32680, 32862, 0 }, //TI北の島
					      { 32477, 32857, 0 }, //TIダンジョン入口
					      { 32413, 32932, 0 }, //TIオーク櫓地帯
					      { 32778, 32705, 4 }, //本土死の廃墟
					      { 32850, 32943, 4 }, //本土亡者の墓
					      { 32812, 32725, 807 }, //メインランドのダンジョン1階
					      { 32761, 32839, 77 },  //象牙の塔4階入り口  
					      { 32705, 33149, 0 }, //TI黒騎士前哨れる
					      { 32580, 32301, 4 }, //本土オーク部落
					      { 32903, 33232, 4 }, //本土砂漠（エルジャベ）
					      { 32748, 33151, 4 }, //本土砂漠（サンドワーム）
					      { 32805, 32724, 19 },//ヨスプダンジョン1階
					      { 32800, 32754, 809 }, //メインランドのダンジョン3階
					      { 33430, 32821, 4 }, //ギラン監獄入口
					      { 32809, 32729, 25 }, //修練ダンジョン入口****
					      { 00000, 00000, 0 }, // 中レベル推薦狩り場
					      { 33782, 33405, 4 }, //本土鏡の森 
					      { 33789, 32988, 4 }, //本土密林地帯
					      { 32746, 32852, 59 }, //ハイネ1階
					      { 34250, 33454, 4 }, //傲慢の塔入口
					      { 33231, 32540, 4 }, //本土黒騎士出没地域
					      { 32767, 32796, 20 }, //ヨスプダンジョン2階
					      { 32728, 32807, 61 }, //ハイネ3階
					      { 32809, 32810, 30 }, //ヨンドン1階入り口
					      { 32809, 32767, 27 }, //修練ケイブ3階入口
				//	      { 34266, 32187, 4 }, //影の神殿入口  
				//	      { 32756, 33459, 4 }, //欲望の洞窟入り口
					      { 00000, 00000, 0 }, // 高レベル推奨狩り場
					      { 32707, 32818, 32 },//用のダンジョン3階
					      { 32427, 33500, 4 }, //ハイネ忘れられた島船の切符小
					      { 33179, 33026, 4 }, //本土暗黒竜の傷痕
					      { 34275, 32361, 4 }, //本土氷雪壁
					      { 34078, 32559, 4 }, //本土エルモアの激戦地
					      { 33295, 32456, 4 }, //本土ドラゴンバレー入口
					      { 33390, 32330, 4 }, //本土ドラゴンバレー正常
					      { 33613, 32393, 4 }, //本土火竜の巣の入り口
			              { 33711, 32276, 4 }, //本土火竜の巣最上部
					      { 34116, 32940, 4 }, //本土風竜の巣の入り口
					      { 34263, 32825, 4 },}; //本土風竜の巣の入り口
				int[] c = memory_Encounter[BookTel];
				if (c != null) {
					new L1Teleport().teleport(pc, c[0], c[1], (short) c[2], pc.getHeading(), true);
					pc.getInventory().removeItem(l1iteminstance, 1);
				}
			} catch (Exception e) {}
		}
	}
	
}
