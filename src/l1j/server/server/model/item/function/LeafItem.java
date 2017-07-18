package l1j.server.server.model.item.function;

import java.util.Random;

import l1j.server.Config;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;

public class LeafItem {

	private static Random _random = new Random(System.nanoTime());

	public static void clickItem(L1PcInstance pc, int itemId, L1ItemInstance l1iteminstance, L1ItemInstance l1iteminstance1) {


		switch (itemId) {
		case 31086:{//生命の葉
			int targetItem = l1iteminstance1.getItemId();
			int[] item = new int[] { 31086 };//必要な材料
			int[] AQGW = new int[] { 20049 };		//巨大女王金色の翼
			int[] AQSW = new int[] { 20050 };		//巨大女王銀翼
			int[] exCS = new int[] { 1119 };	//極限のチェーンソード
			int[] RoS = new int[] { 22009 };		//気迫のリング
			int[] CB = new int[] { 41148 };			//技術書（カウンターバリア）
			int[] k2hs = new int[] { 1121 };			//ナイトバルドの両手剣
			int[] coldKiringku = new int[] { 1120 };			//冷え性のキーリンク
			int[] SoD = new int[] { 119 };			//デーモンの杖
			int[] AoDK = new int[] { 20100 };		//デスナイトの鎧
			int[] BoDK = new int[] { 20198 };		//デスナイトのブーツ
			int[] DKFB = new int[] { 58 };			//デスナイトのフレイムブレード
			int[] GoDK = new int[] { 20166 };		//デスナイトの手袋
			int[] HoDK = new int[] { 20010 };		//デスナイトの投球
			int[] NoDoppeBoss = new int[] { 20250 };		//ドッペルゲンガーボスのネックレス
			int[] RoDoppeBossR = new int[] { 20277 };		//ドッペルゲンガーボスの右リング
			int[] RoDoppeBossL = new int[] { 20278 };		//ドッペルゲンガーボスの左リング
			int[] GoS = new int[] { 22261 };			//石の手袋
			int[] RoL = new int[] { 20279 };		//ライアーのリング
			int[] RDB = new int[] { 76 };		//ロンドデュアルブレード
			int[] lichRobe = new int[] { 20107 };		//リッチローブ
			int[] SoF = new int[] { 126 };		//マナの杖
			int[] magicGaiter = new int[] { 222328 };		//魔法防御ゲートル
			int[] DIG = new int[] { 40222 };			//魔法書（ディスインテグレート）
			int[] meteo = new int[] { 40219 };		//魔法書（メテオストライク）
			int[] devilRing = new int[] { 22008 };		//魔王のリング
			int[] CoML = new int[] { 20017 };		//マミーロードクラウン
			int[] hatMel = new int[] { 20018 };		//メルキオールの帽子
			int[] agilityRing = new int[] { 22364 };		//機敏のリング
			int[] SoB = new int[] { 124 };			//バフォメットスタッフ
			int[] rebelShield = new int[] { 22263 };		//反逆者の盾
			int[] hatBal = new int[] { 20025 };		//バルタザールの帽子
			int[] CoV = new int[] { 20079 };			//ヴァンパイアのマント
			int[] TOI10 = new int[] { 830041};		//封印された傲慢の塔10階テレポートアミュレット
			int[] TOI1 = new int[] { 830032 };		//封印された傲慢の塔1階テレポートアミュレット
			int[] TOI2 = new int[] { 830033 };		//封印された傲慢の塔2階テレポートアミュレット
			int[] TOI3 = new int[] { 830034 };		//封印された傲慢の塔3階テレポートアミュレット
			int[] TOI4 = new int[] { 830035 };		//封印された傲慢の塔4階テレポートアミュレット
			int[] TOI5 = new int[] { 830036 };		//封印された傲慢の塔5階テレポートアミュレット
			int[] TOI6 = new int[] { 830037 };		//封印された傲慢の塔6階テレポートアミュレット
			int[] TOI7 = new int[] { 830038 };		//封印された傲慢の塔7階テレポートアミュレット
			int[] TOI8 = new int[] { 830039 };		//封印された傲慢の塔8階テレポートアミュレット
			int[] TOI9 = new int[] { 830040 };		//封印された傲慢の塔9階テレポートアミュレット
			int[] lostForceBS = new int[] { 1123 };			//元気を失ったブラッドサッカー
			int[] sayhaNeck = new int[] { 20271 };			//輝くサイハネックレス
			int[] sayhaRing = new int[] { 20272 };			//輝くサイハリング
			int[] hatSema = new int[] { 20029 };			//セマの帽子
			int[] semaRing = new int[] { 222311};			//セマのリング
			int[] siaEye = new int[] { 22214 };			//シアーの心眼
			int[] sirufTS = new int[] { 900019 };		//シルフのTシャツ
			int[] SoIQ = new int[] { 121 };			//アイスクイーンスタッフ
			int[] RoAG = new int[] { 20314 };			//エンシェントジャイアントリング
			int[] OD = new int[] { 9 };				//オリハルコンダガー
			int[] orimNeck = new int[] { 222310 };		//クリップボードのネックレス
			int[] RoSTR = new int[] { 22363 };		//腕力の指輪
			int[] dragonHeart = new int[] { 40466 };			//ドラゴンの心臓
			int[] CoSilber = new int[] { 20074 };			//銀のマント
			int[] desperado = new int[] { 210125 };		//戦士の印章（デスペラード）
			int[] titan = new int[] { 210130 };		//戦士の印章（タイタンロック）
			int[] titanMagic = new int[] { 210132 };	//戦士の印章（タイタンマジック）
			int[] titanW = new int[] { 210131 };	//戦士の印章（タイタンウェーブレット）
			int[] SoulofFlame = new int[] { 41149 };			//精霊の水晶（ソウルオブフレイム）
			int[] strikerGale = new int[] { 41153 };			//精霊の水晶（ストライカーゲイル）
			int[] bind = new int[] { 40249 };			//精霊の水晶（アースバインド）
			int[] poluto = new int[] { 41152 };			//精霊の水晶（ポルートウォーター）
			int[] zenisRing = new int[] { 20298 };		//ゼニスのリング
			int[] IntRing = new int[] { 22358 };		//知識のリング
			int[] commanderGaiter = new int[] { 22360 };		//指揮官のヘルム
			int[] hatCas = new int[] { 20040 };		//カスパーの帽子
			int[] AoK = new int[] { 20150 };		//カーツの鎧
			int[] kartsSword = new int[] { 54 };			//カーツの剣
			int[] BoK = new int[] { 20214 };		//カーツのブーツ
			int[] GoK = new int[] { 20184 };		//カーツの手袋
			int[] HoK = new int[] { 20041 };		//カーツのヘルム
			int[] cronosBelt = new int[] { 900007 };		//クロノスのベルト
			int[] BoCorrupt = new int[] { 20216 };		//堕落のブーツ
			int[] GoCorrupt = new int[] { 20186 };		//堕落のグローブ
			int[] titanBelt = new int[] { 20320 };			//タイタンのベルト
			int[] COI = new int[] { 20077 };			//透明マント
			int[] projectionNeck = new int[] { 222304 };		//投影のネックレス
			int[] DestroyLB = new int[] { 1122 };			//破壊のロングボウ
			int[] WiseNeck = new int[] { 222306 };		//賢者のネックレス
			int[] HoChaos = new int[] { 20048 };			//混沌のヘルム
			int[] BER = new int[] { 20160 };		//ブラックエルダーローブ
			int[] BES = new int[] { 20218 };		//ブラックエルダーのサンダル
			int[] AB = new int[] { 5559 };		//闇精霊の水晶（アーマーブレイク）
			int[] SoForce = new int[] { 131 };			//力の杖
			int[] temp = null;

			switch(targetItem) {
			case 31000:		// 元気を失った巨大女王金色の翼
				temp = AQGW;	break;
			case 31001:		//元気を失った巨大女王銀翼
				temp = AQSW;	break;
			case 31002:		// 元気を失った極限のチェーンソード
				temp = exCS;break;
			case 31003:		// 元気を失った気迫のリング
				temp = RoS;	break;
			case 31004:		// 元気を失った技術書（カウンターバリア）
				temp = CB;		break;
			case 31005:	// 元気を失ったナイトバルドの両手剣
				temp = k2hs;break;
			case 31006:	// 元気を失った冷え性のキーリンク
				temp = coldKiringku;break;
			case 31007:	// 元気を失ったデーモンスタッフ
				temp = SoD;break;
			case 31008:	// 元気を失ったデスナイトの鎧
				temp = AoDK;break;
			case 31009:	// 元気を失ったデスナイトのブーツ
				temp = BoDK;break;
			case 31010:	// 元気を失ったデスナイトのフレイムブレード
				temp = DKFB;break;
			case 31011:	//元気を失ったデスナイトの手袋
				temp = GoDK;break;
			case 31012:	// 元気を失ったデスナイトの投球
				temp = HoDK;break;
			case 31013:	// 元気を失ったドッペルゲンガーボスのネックレス
				temp = NoDoppeBoss;break;
			case 31014:	// 元気を失ったドッペルゲンガーボスの右リング
				temp = RoDoppeBossR;break;
			case 31015:	// 元気を失ったドッペルゲンガーボスの左リング
				temp = RoDoppeBossL;break;
			case 31016:	// 元気を失った石の手袋
				temp = GoS;break;
			case 31017:	// 元気を失ったライアのリング
				temp = RoL;break;
			case 31018:	// 元気を失ったロンドデュアルブレード
				temp = RDB;break;
			case 31019:	// 元気を失ったリッチローブ
				temp = lichRobe;break;
			case 31020:	// オーラを失ったマナスタッフ
				temp = SoF;break;
			case 31021:	// 元気を失った魔法防御ゲートル
				temp = magicGaiter;break;
			case 31022:	// 元気を失った魔法書（ディスインテグレート）
				temp = DIG;break;
			case 31023:	// 元気を失った魔法書（メテオストライク）
				temp = meteo;break;
			case 31024:	// 元気を失った魔王のリング
				temp = devilRing;break;
			case 31025:	// 元気を失ったマミーロードクラウン
				temp = CoML;break;
			case 31026:	// 元気を失ったメルキオールの帽子
				temp = hatMel;break;
			case 31027:	// 元気を失ったアジャイルのリング
				temp = agilityRing;break;
			case 31028:	// オーラを失ったバフォメットスタッフ
				temp = SoB;break;
			case 31029:	// 元気を失った反逆者の盾
				temp = rebelShield;break;
			case 31030:	// 元気を失ったバルタザールの帽子
				temp = hatBal;break;
			case 31031:	// 元気を失ったヴァンパイアのマント
				temp = CoV;break;
			case 31032:	// 元気を失った封印された傲慢の塔10階テレポートアミュレット
				temp = TOI10;break;
			case 31033:	// 元気を失った封印された傲慢の塔1階テレポートアミュレット
				temp = TOI1;break;
			case 31034:	// 元気を失った封印された傲慢の塔2階テレポートアミュレット
				temp = TOI2;break;
			case 31035:	// 元気を失った封印された傲慢の塔3階テレポートアミュレット
				temp = TOI3;break;
			case 31036:	// 元気を失った封印された傲慢の塔4階テレポートアミュレット
				temp = TOI4;break;
			case 31037:	// 元気を失った封印された傲慢の塔5階テレポートアミュレット
				temp = TOI5;break;
			case 31038:	// 元気を失った封印された傲慢の塔6階テレポートアミュレット
				temp = TOI6;break;
			case 31039:	// 元気を失った封印された傲慢の塔7階テレポートアミュレット
				temp = TOI7;break;
			case 31040:	// 元気を失った封印された傲慢の塔8階テレポートアミュレット
				temp = TOI8;break;
			case 31041:	// 元気を失った封印された傲慢の塔9階テレポートアミュレット
				temp = TOI9;break;
			case 31042:	// 元気を失ったブラッドサッカー
				temp = lostForceBS;break;
			case 31043:	// 元気を失った輝くサイハネックレス
				temp = sayhaNeck;break;
			case 31044:	//元気を失った輝くサイハリング
				temp = sayhaRing;break;
			case 31045:	// 元気を失ったセマの帽子
				temp = hatSema;break;
			case 31046:	// 元気を失ったセマのリング
				temp = semaRing;break;
			case 31047:	// 元気を失った語の心眼
				temp = siaEye;break;
			case 31048:	// 元気を失ったシルフのTシャツ
				temp = sirufTS;break;
			case 31049:	// 元気を失ったアイスクイーンスタッフ
				temp = SoIQ;break;
			case 31050:	// 元気を失ったエンシェントジャイアントリング
				temp = RoAG;break;
			case 31051:	// 元気を失ったオリハルコンダガー
				temp = OD;break;
			case 31052:	// 元気を失ったクリップボードのネックレス
				temp = orimNeck;break;
			case 31053:	// 元気を失った腕力の指輪
				temp = RoSTR;break;
			case 31054:	// 元気を失ったドラゴンの心臓
				temp = dragonHeart;break;
			case 31055:	// 元気を失った銀のマント
				temp = CoSilber;break;
			case 31056:	// 元気を失った戦士の印章（デスペラード）
				temp = desperado;break;
			case 31057:	// 元気を失った戦士の印章（タイタンロック）
				temp = titan;break;
			case 31058:	// 元気を失った戦士の印章（タイタンマジック）
				temp = titanMagic;break;
			case 31059:	// 元気を失った戦士の印章（タイタンウェーブレット）
				temp = titanW;break;
			case 31060:	// 元気を失った精霊の水晶（ソウルオブフレーム）
				temp = SoulofFlame;break;
			case 31061:	// 元気を失った精霊の水晶（ストライクゲイル）
				temp = strikerGale;break;
			case 31062:	// 元気を失った精霊の水晶（アースバインド）
				temp = bind;break;
			case 31063:	// 元気を失った精霊の水晶（ポールルートウォーター）
				temp = poluto;break;
			case 31064:	// 元気を失ったゼニスのリング
				temp = zenisRing;break;
			case 31065:	// 元気を失った知識のリング
				temp = IntRing;break;
			case 31066:	// 元気を失った指揮官の投球
				temp = commanderGaiter;break;
			case 31067:	// 元気を失ったカスパーの帽子
				temp = hatCas;break;
			case 31068:	// 元気を失ったカーツの鎧
				temp = AoK;break;
			case 31069:	// 元気を失ったカーツの剣
				temp = kartsSword;break;
			case 31070:	// 元気を失ったカーツのブーツ
				temp = BoK;break;
			case 31071:	// 元気を失ったカーツの手袋
				temp = GoK;break;
			case 31072:	// 元気を失ったカーツヘルム
				temp = HoK;break;
			case 31073:	// 元気を失ったクロノスのベルト
				temp = cronosBelt;break;
			case 31074:	// 元気を失った堕落のブーツ
				temp = BoCorrupt;break;
			case 31075:	// 元気を失った堕落のグローブ
				temp = GoCorrupt;break;
			case 31076:	//元気を失ったタイタンのベルト
				temp = titanBelt;break;
			case 31077:	// 元気を失った透明マント
				temp = COI;break;
			case 31078:	// 元気を失った投影のネックレス
				temp = projectionNeck;break;
			case 31079:	// 元気を失った破壊のロングボウ
				temp = DestroyLB;break;
			case 31080:	// 元気を失った賢者のネックレス
				temp = WiseNeck;break;
			case 31081:	// 元気を失った混沌の投球
				temp = HoChaos;break;
			case 31082:	// 元気を失ったブラックエルダーローブ
				temp = BER;break;
			case 31083:	// 元気を失ったブラックエルダーのサンダル
				temp = BES;break;
			case 31084:	// 元気を失った闇精霊の水晶（アーマーブレイク）
				temp = AB;break;
			case 31085:	// オーラを失った力の杖
				temp = SoForce;break;
			default:
				pc.sendPackets(new S_SystemMessage("\\aA通知：オーラを失ったアイテムのみが可能です。"));
				break;
			}
			if(temp != null) {
				boolean chance = false;
				for (int i = 0 ; i < item.length; i++){
					if (l1iteminstance.getItemId() == item[i]) {
						if(_random.nextInt(99) + 1 <= Config.LEAVES_OF_LIFE) {
							chance = true;
							// 支払い処理。
							createNewItem2(pc, temp[i], 1, l1iteminstance1.getEnchantLevel());
							pc.sendPackets(new S_SystemMessage(""+l1iteminstance1.getName()+"は、新しい命が与えられました。"));
							break;
						}
						if (pc.isGm()){
							pc.sendPackets(new S_SystemMessage("葉の確率 >> " + Config.LEAVES_OF_LIFE));
						}
					}
				}
				//確率失敗ハットをするときのメッセージ処理。
				if(chance == false) {
					pc.sendPackets(new S_SystemMessage(""+l1iteminstance1.getName()+"はオーラを吸収できず消滅しました。"));
				}
				// 材料除去処理。
				pc.getInventory().DeleteEnchant(l1iteminstance1.getItemId(), l1iteminstance1.getEnchantLevel());
				pc.getInventory().removeItem(l1iteminstance, 1);
			}
		}
		break;

		}
	}

	private static boolean createNewItem2(L1PcInstance pc, int item_id, int count, int EnchantLevel) {
		L1ItemInstance item = ItemTable.getInstance().createItem(item_id);
		if (item != null) {
			item.setCount(count);
			item.setEnchantLevel(EnchantLevel);
			item.setIdentified(true);
			if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
				pc.getInventory().storeItem(item);
			} else {
				pc.sendPackets(new S_ServerMessage(82));
				// 重量ゲージが不足したり、インベントリがいっぱいよりにできません。
				return false;
			}
			//pc.sendPackets(new S_ServerMessage(403, item.getLogName())); // %0を手に入れました。
			return true;
		} else {
			return false;
		}
	}


}