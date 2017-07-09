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
			int[] 금빛날개 = new int[] { 20049 };		//巨大女王金色の翼
			int[] 은빛날개 = new int[] { 20050 };		//巨大女王銀翼
			int[] 극한체인소드 = new int[] { 1119 };	//極限のチェーンソード
			int[] 기백반지 = new int[] { 22009 };		//気迫のリング
			int[] 카배 = new int[] { 41148 };			//技術書（カウンターバリア）
			int[] 나발 = new int[] { 1121 };			//ナイトバルドの両手剣
			int[] 냉키 = new int[] { 1120 };			//冷え性のキーリンク
			int[] 데지 = new int[] { 119 };			//デーモンの杖
			int[] 데스갑옷 = new int[] { 20100 };		//デスナイトの鎧
			int[] 데스부츠 = new int[] { 20198 };		//デスナイトのブーツ
			int[] 데불 = new int[] { 58 };			//デスナイトのフレイムブレード
			int[] 데스장갑 = new int[] { 20166 };		//デスナイトの手袋
			int[] 데스투구 = new int[] { 20010 };		//デスナイトの投球
			int[] 도펠목 = new int[] { 20250 };		//ドッペルゲンガーボスのネックレス
			int[] 도펠오른 = new int[] { 20277 };		//ドッペルゲンガーボスの右リング
			int[] 도펠왼쪽 = new int[] { 20278 };		//ドッペルゲンガーボスの左リング
			int[] 돌장 = new int[] { 22261 };			//石の手袋
			int[] 라이아반지 = new int[] { 20279 };		//ライアーのリング
			int[] 론드이도류 = new int[] { 76 };		//ロンドデュアルブレード
			int[] 리치로브 = new int[] { 20107 };		//リッチローブ
			int[] 마나지팡 = new int[] { 126 };		//マナの杖
			int[] 마법각반 = new int[] { 222328 };		//魔法防御ゲートル
			int[] 디스 = new int[] { 40222 };			//魔法書（ディスインテグレート）
			int[] 미티어 = new int[] { 40219 };		//魔法書（メテオストライク）
			int[] 마왕반지 = new int[] { 22008 };		//魔王のリング
			int[] 머미왕관 = new int[] { 20017 };		//マミーロードクラウン
			int[] 메르모자 = new int[] { 20018 };		//メルキオールの帽子
			int[] 민첩반지 = new int[] { 22364 };		//アジャイルのリング
			int[] 바지 = new int[] { 124 };			//バフォメットスタッフ
			int[] 반역자 = new int[] { 22263 };		//反逆者の盾
			int[] 발터모자 = new int[] { 20025 };		//バルタザールの帽子
			int[] 뱀망 = new int[] { 20079 };			//ヴァンパイアのマント
			int[] 오만10 = new int[] { 830041};		//封印された傲慢の塔10階テレポートアミュレット
			int[] 오만1 = new int[] { 830032 };		//封印された傲慢の塔1階テレポートアミュレット
			int[] 오만2 = new int[] { 830033 };		//封印された傲慢の塔2階テレポートアミュレット
			int[] 오만3 = new int[] { 830034 };		//封印された傲慢の塔3階テレポートアミュレット
			int[] 오만4 = new int[] { 830035 };		//封印された傲慢の塔4階テレポートアミュレット
			int[] 오만5 = new int[] { 830036 };		//封印された傲慢の塔5階テレポートアミュレット
			int[] 오만6 = new int[] { 830037 };		//封印された傲慢の塔6階テレポートアミュレット
			int[] 오만7 = new int[] { 830038 };		//封印された傲慢の塔7階テレポートアミュレット
			int[] 오만8 = new int[] { 830039 };		//封印された傲慢の塔8階テレポートアミュレット
			int[] 오만9 = new int[] { 830040 };		//封印された傲慢の塔9階テレポートアミュレット
			int[] 블서 = new int[] { 1123 };			//기운을 잃은 블러드서커
			int[] 빛목 = new int[] { 20271 };			//輝くサイハネックレス
			int[] 빛반 = new int[] { 20272 };			//輝くサイハリング
			int[] 세모 = new int[] { 20029 };			//セマの帽子
			int[] 세반 = new int[] { 222311};			//セマのリング
			int[] 심안 = new int[] { 22214 };			//シアバターの心眼
			int[] 실티 = new int[] { 900019 };		//シルフのTシャツ
			int[] 얼지 = new int[] { 121 };			//アイスクイーンスタッフ
			int[] 에반 = new int[] { 20314 };			//エンシェントジャイアントリング
			int[] 오단 = new int[] { 9 };				//オリハルコンダガー
			int[] 오림목 = new int[] { 222310 };		//クリップボードのネックレス
			int[] 완력반 = new int[] { 22363 };		//腕力の指輪
			int[] 용심 = new int[] { 40466 };			//ドラゴンの心臓
			int[] 은망 = new int[] { 20074 };			//銀のマント
			int[] 데페 = new int[] { 210125 };		//戦士の引張（デスペラード）
			int[] 타이탄락 = new int[] { 210130 };		//戦士の引張（タイタンロック）
			int[] 타이탄매직 = new int[] { 210132 };	//戦士の引張（タイタンマジック）
			int[] 타이탄블릿 = new int[] { 210131 };	//戦士の引張（タイタンウェーブレット）
			int[] 소프 = new int[] { 41149 };			//精霊の水晶（ソウルオブフレーム）
			int[] 게일 = new int[] { 41153 };			//精霊の水晶（ストライクゲイル）
			int[] 어바 = new int[] { 40249 };			//精霊の水晶（アースバインド）
			int[] 워터 = new int[] { 41152 };			//精霊の水晶（ポールルートウォーター）
			int[] 제니스반 = new int[] { 20298 };		//ゼニスのリング
			int[] 지식반 = new int[] { 22358 };		//知識のリング
			int[] 지휘관 = new int[] { 22360 };		//指揮官の投球
			int[] 카스파모 = new int[] { 20040 };		//カスパーの帽子
			int[] 커츠갑 = new int[] { 20150 };		//カーツの鎧
			int[] 커검 = new int[] { 54 };			//カーツの剣
			int[] 커츠부 = new int[] { 20214 };		//カーツのブーツ
			int[] 커츠장 = new int[] { 20184 };		//カーツの手袋
			int[] 커츠투 = new int[] { 20041 };		//カーツの投球
			int[] 크로벨 = new int[] { 900007 };		//クロノスのベルト
			int[] 타락부 = new int[] { 20216 };		//堕落のブーツ
			int[] 타락장 = new int[] { 20186 };		//堕落のグローブ
			int[] 타벨 = new int[] { 20320 };			//タイタンのベルト
			int[] 투망 = new int[] { 20077 };			//透明マント
			int[] 투사목 = new int[] { 222304 };		//投影のネックレス
			int[] 파장 = new int[] { 1122 };			//破壊のロングボウ
			int[] 현목 = new int[] { 222306 };		//賢者のネックレス
			int[] 혼투 = new int[] { 20048 };			//混沌の投球
			int[] 흑장로브 = new int[] { 20160 };		//ブラックエルダーローブ
			int[] 흑장샌달 = new int[] { 20218 };		//ブラックエルダーのサンダル
			int[] 아머브 = new int[] { 5559 };		//闇精霊の水晶（アーマーブレイク）
			int[] 힘지 = new int[] { 131 };			//力の杖
			int[] temp = null;

			switch(targetItem) {
			case 31000:		// 元気を失った巨大女王金色の翼
				temp = 금빛날개;	break;
			case 31001:		//元気を失った巨大女王銀翼
				temp = 은빛날개;	break;
			case 31002:		// 元気を失った極限のチェーンソード
				temp = 극한체인소드;break;
			case 31003:		// 元気を失った気迫のリング
				temp = 기백반지;	break;
			case 31004:		// 元気を失った技術書（カウンターバリア）
				temp = 카배;		break;
			case 31005:	// 元気を失ったナイトバルドの両手剣
				temp = 나발;break;
			case 31006:	// 元気を失った冷え性のキーリンク
				temp = 냉키;break;
			case 31007:	// 元気を失ったデーモンスタッフ
				temp = 데지;break;
			case 31008:	// 元気を失ったデスナイトの鎧
				temp = 데스갑옷;break;
			case 31009:	// 元気を失ったデスナイトのブーツ
				temp = 데스부츠;break;
			case 31010:	// 元気を失ったデスナイトのフレイムブレード
				temp = 데불;break;
			case 31011:	//元気を失ったデスナイトの手袋
				temp = 데스장갑;break;
			case 31012:	// 元気を失ったデスナイトの投球
				temp = 데스투구;break;
			case 31013:	// 元気を失ったドッペルゲンガーボスのネックレス
				temp = 도펠목;break;
			case 31014:	// 元気を失ったドッペルゲンガーボスの右リング
				temp = 도펠오른;break;
			case 31015:	// 元気を失ったドッペルゲンガーボスの左リング
				temp = 도펠왼쪽;break;
			case 31016:	// 元気を失った石の手袋
				temp = 돌장;break;
			case 31017:	// 元気を失ったライアのリング
				temp = 라이아반지;break;
			case 31018:	// 기운을 잃은 론드의 이도류
				temp = 론드이도류;break;
			case 31019:	// 元気を失ったリッチローブ
				temp = 리치로브;break;
			case 31020:	// オーラを失ったマナスタッフ
				temp = 마나지팡;break;
			case 31021:	// 元気を失った魔法防御ゲートル
				temp = 마법각반;break;
			case 31022:	// 元気を失った魔法書（ディスインテグレート）
				temp = 디스;break;
			case 31023:	// 元気を失った魔法書（メテオストライク）
				temp = 미티어;break;
			case 31024:	// 元気を失った魔王のリング
				temp = 마왕반지;break;
			case 31025:	// 元気を失ったマミーロードクラウン
				temp = 머미왕관;break;
			case 31026:	// 元気を失ったメルキオールの帽子
				temp = 메르모자;break;
			case 31027:	// 元気を失ったアジャイルのリング
				temp = 민첩반지;break;
			case 31028:	// オーラを失ったバフォメットスタッフ
				temp = 바지;break;
			case 31029:	// 元気を失った反逆者の盾
				temp = 반역자;break;
			case 31030:	// 元気を失ったバルタザールの帽子
				temp = 발터모자;break;
			case 31031:	// 元気を失ったヴァンパイアのマント
				temp = 뱀망;break;
			case 31032:	// 元気を失った封印された傲慢の塔10階テレポートアミュレット
				temp = 오만10;break;
			case 31033:	// 元気を失った封印された傲慢の塔1階テレポートアミュレット
				temp = 오만1;break;
			case 31034:	// 元気を失った封印された傲慢の塔2階テレポートアミュレット
				temp = 오만2;break;
			case 31035:	// 元気を失った封印された傲慢の塔3階テレポートアミュレット
				temp = 오만3;break;
			case 31036:	// 元気を失った封印された傲慢の塔4階テレポートアミュレット
				temp = 오만4;break;
			case 31037:	// 元気を失った封印された傲慢の塔5階テレポートアミュレット
				temp = 오만5;break;
			case 31038:	// 元気を失った封印された傲慢の塔6階テレポートアミュレット
				temp = 오만6;break;
			case 31039:	// 元気を失った封印された傲慢の塔7階テレポートアミュレット
				temp = 오만7;break;
			case 31040:	// 元気を失った封印された傲慢の塔8階テレポートアミュレット
				temp = 오만8;break;
			case 31041:	// 元気を失った封印された傲慢の塔9階テレポートアミュレット
				temp = 오만9;break;
			case 31042:	// 元気を失ったブラッドサッカー
				temp = 블서;break;
			case 31043:	// 元気を失った輝くサイハネックレス
				temp = 빛목;break;
			case 31044:	// 기운을 잃은 빛나는 사이하의 반지
				temp = 빛반;break;
			case 31045:	// 元気を失ったセマの帽子
				temp = 세모;break;
			case 31046:	// 元気を失ったセマのリング
				temp = 세반;break;
			case 31047:	// 元気を失った語の心眼
				temp = 심안;break;
			case 31048:	// 元気を失ったシルフのTシャツ
				temp = 실티;break;
			case 31049:	// 元気を失ったアイスクイーンスタッフ
				temp = 얼지;break;
			case 31050:	// 元気を失ったエンシェントジャイアントリング
				temp = 에반;break;
			case 31051:	// 元気を失ったオリハルコンダガー
				temp = 오단;break;
			case 31052:	// 元気を失ったクリップボードのネックレス
				temp = 오림목;break;
			case 31053:	// 元気を失った腕力の指輪
				temp = 완력반;break;
			case 31054:	// 元気を失ったドラゴンの心臓
				temp = 용심;break;
			case 31055:	// 元気を失った銀のマント
				temp = 은망;break;
			case 31056:	// 元気を失った戦士の引張（デスペラード）
				temp = 데페;break;
			case 31057:	// 元気を失った戦士の引張（タイタンロック）
				temp = 타이탄락;break;
			case 31058:	// 元気を失った戦士の引張（タイタンマジック）
				temp = 타이탄매직;break;
			case 31059:	// 元気を失った戦士の引張（タイタンウェーブレット）
				temp = 타이탄블릿;break;
			case 31060:	// 元気を失った精霊の水晶（ソウルオブフレーム）
				temp = 소프;break;
			case 31061:	// 元気を失った精霊の水晶（ストライクゲイル）
				temp = 게일;break;
			case 31062:	// 元気を失った精霊の水晶（アースバインド）
				temp = 어바;break;
			case 31063:	// 元気を失った精霊の水晶（ポールルートウォーター）
				temp = 워터;break;
			case 31064:	// 元気を失ったゼニスのリング
				temp = 제니스반;break;
			case 31065:	// 元気を失った知識のリング
				temp = 지식반;break;
			case 31066:	// 元気を失った指揮官の投球
				temp = 지휘관;break;
			case 31067:	// 元気を失ったカスパーの帽子
				temp = 카스파모;break;
			case 31068:	// 元気を失ったカーツの鎧
				temp = 커츠갑;break;
			case 31069:	// 元気を失ったカーツの剣
				temp = 커검;break;
			case 31070:	// 元気を失ったカーツのブーツ
				temp = 커츠부;break;
			case 31071:	// 元気を失ったカーツの手袋
				temp = 커츠장;break;
			case 31072:	// 元気を失ったカーツヘルム
				temp = 커츠투;break;
			case 31073:	// 元気を失ったクロノスのベルト
				temp = 크로벨;break;
			case 31074:	// 元気を失った堕落のブーツ
				temp = 타락부;break;
			case 31075:	// 元気を失った堕落のグローブ
				temp = 타락장;break;
			case 31076:	//元気を失ったタイタンのベルト
				temp = 타벨;break;
			case 31077:	// 元気を失った透明マント
				temp = 투망;break;
			case 31078:	// 元気を失った投影のネックレス
				temp = 투사목;break;
			case 31079:	// 元気を失った破壊のロングボウ
				temp = 파장;break;
			case 31080:	// 元気を失った賢者のネックレス
				temp = 현목;break;
			case 31081:	// 元気を失った混沌の投球
				temp = 혼투;break;
			case 31082:	// 기운을 잃은 흑장로의 로브
				temp = 흑장로브;break;
			case 31083:	// 元気を失ったブラックエルダーのサンダル
				temp = 흑장샌달;break;
			case 31084:	// 元気を失った闇精霊の水晶（アーマーブレイク）
				temp = 아머브;break;
			case 31085:	// 기운을 잃은 힘의 지팡이
				temp = 힘지;break;
			default:
				pc.sendPackets(new S_SystemMessage("\\aA通知：オーラを失ったアイテムのみが可能です。"));
				break;
			}
			if(temp != null) {
				boolean chance = false;
				for (int i = 0 ; i < item.length; i++){
					if (l1iteminstance.getItemId() == item[i]) {
						if(_random.nextInt(99) + 1 <= Config.생명의나뭇잎) {
							chance = true;
							// 支払い処理。
							createNewItem2(pc, temp[i], 1, l1iteminstance1.getEnchantLevel());
							pc.sendPackets(new S_SystemMessage(""+l1iteminstance1.getName()+"は、新しい命が与えられました。"));
							break;
						}
						if (pc.isGm()){
							pc.sendPackets(new S_SystemMessage("葉の確率 >> " + Config.생명의나뭇잎));
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