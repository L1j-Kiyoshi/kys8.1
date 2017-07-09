package l1j.server.server.serverpackets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.TimeZone;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.Account;
import l1j.server.server.Opcodes;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.utils.SQLUtil;

/**
 * スキルアイコンやブロックリストの表示など、複数の用途に使用されるパケットのクラス
 */
public class S_PacketBox extends ServerBasePacket {
	
	private static final String S_PACKETBOX = "[S] S_PacketBox";

	private byte[] _byte = null;

	// *** S_107 sub code list ***

	// 1:Kent 2:Orc 3:WW 4:Giran 5:Heine 6:Dwarf 7:Aden 8:Diad 9:氏名9 ...
	/** C(id) H(?): %sの攻城戦が開始されました。 */
	public static final int MSG_WAR_BEGIN = 0;

	/** C(id) H(?): %sの攻城戦が終了しました。*/
	public static final int MSG_WAR_END = 1;

	/** C(id) H(?): %sの攻城戦が進行中です。 */
	public static final int MSG_WAR_GOING = 2;

	/** -: 性主導権を取った。 （音楽が変わる） */
	public static final int MSG_WAR_INITIATIVE = 3;

	/** -: 性を占拠しました。*/
	public static final int MSG_WAR_OCCUPY = 4;

	/** ?: 決闘が終わりました。 （音楽が変わる） */
	public static final int MSG_DUEL = 5;

	/** C(count): SMSの送信に失敗しました。 /すべて％d件送信されました。*/
	public static final int MSG_SMS_SENT = 6;

	/** -: 祝福の中、2人の夫婦として接続しました。 （音楽が変わる）*/
	public static final int MSG_MARRIED = 9;

	/** C(weight): 重量（30段階）*/
	public static final int WEIGHT = 10;

	/** C(food): 満腹度（30段階） */
	public static final int FOOD = 11;

	/** C(0) C(level): このアイテムは％dレベル以下のみ使用することができます。 （0〜49以外は表示されない）*/
	public static final int MSG_LEVEL_OVER = 12;

	/** UB情報 HTML */
	public static final int HTML_UB = 14;

	/**
	 * C(id)<br>
	 * 1:体に込められた精霊の力が空気中に溶けていくのを感じました。<br>
	 * 2:体の隅々に話のジョンリョンリョクが染みこんできます。<br>
	 * 3:体の隅々に水ジョンリョンリョクが染みこんできます。<br>
	 * 4:体の隅々に風のジョンリョンリョクが染みこんできます。<br>
	 * 5:体の隅々に土地のジョンリョンリョクが染みこんできます。<br>
	 */
	public static final int MSG_ELF = 15;

	/** C(count) S(name)...: ブロックリスト */
	public static final int SHOW_LIST_EXCLUDE = 17;

	/** S(name): ブロックリストに追加 */
	public static final int ADD_EXCLUDE = 18;

	/** S(name):ブロックを解除 */
	public static final int REM_EXCLUDE = 19;
	
	/** PC部屋バフ */
	public static final int PC_ROOM_BUFF = 127;
	
	/** スキルアイコン */
	public static final int ICONS1 = 20;

	/** スキルアイコン */
	public static final int ICONS2 = 21;

	/** オーラ系のスキルアイコンとイレースマジックアイコンを削除 */
	public static final int ICON_AURA = 22;

	/** S(name): タウンリーダーに％sが選択された。 */
	public static final int MSG_TOWN_LEADER = 23;

	/** 
	 * D（血盟員数）（S（血盟員の名前）C（血盟員階級））血盟員更新がされた状態での/血盟。
	 */
	public static final int PLEDGE_TWO = 24;//追加

	/** 
	 * D（血盟名）C（ランク）血盟に追加された人員があるとき送信するパケット
	 */
	public static final int PLEDGE_REFRESH_PLUS = 25;//追加

	/** 
	 * D（血盟名）C（ランク）血盟に削除された人員があるとき送信するパケット
	 */
	public static final int PLEDGE_REFRESH_MINUS = 26;//追加
	/**
	 * C(id): あなたのランクが％sに変更されました。<br>
	 * id  -  1：見習い2：一般的な3：ガーディアン
	 */
	public static final int MSG_RANK_CHANGED = 27;

	/** 
	 * D（血盟員数）（S（血盟員の名前）C（血盟員階級））血盟員更新が未状態での/血盟。
	 */
	//public static final int PLEDGE_ONE = 119;//追加

	/** D(?) S(name) S(clanname): %s血盟の％sがラスタバド軍を打ち続けた。 */
	public static final int MSG_WIN_LASTAVARD = 30;

	/** -: \f1気分が良くなりました。 */
	public static final int MSG_FEEL_GOOD = 31;

	/** 不明.C_30パケットが飛ぶ */
	public static final int SOMETHING1 = 33;

	/** H(time): ブルー一部のアイコンが表示される。 */
	public static final int ICON_BLUEPOTION = 34;

	/** H(time):変身のアイコンが表示される。 */
	public static final int ICON_POLYMORPH = 35;

	/** H(time): チャット禁止のアイコンが表示される。 */
	public static final int ICON_CHATBAN = 36;

	/** 不明.C_7パケットが出る.C_7はペットのメニューを開いたときにもする。 */
	public static final int SOMETHING2 = 37;

	/** 血盟情報のHTMLが表示される */
	public static final int HTML_CLAN1 = 38;

	/** H(time): 이뮤의 아이콘이 표시된다 */
	public static final int ICON_I2H = 40;

	/** キャラクターのゲームのオプションは、ショートカット情報などを送る */
	public static final int CHARACTER_CONFIG = 41;

	/** キャラクター選択画面に戻る */
	public static final int LOGOUT = 42;

	/** 戦闘中に再始動することができません。*/
	public static final int MSG_CANT_LOGOUT = 43;

	/**
	 * C(count) D(time) S(name) S(info):<br>
	 * [CALL] ボタンが付いたウィンドウが表示される。これは、BOTなどの不正者チェックに
	 * 使用されている機能しているようだ。名前をダブルクリックするとC_RequestWhoが飛び、クライアントの
	 * フォルダにbot_list.txtが生成される。名前を選択し、+キーを押すと、新しいウィンドウが開かれる。
	 */
	public static final int CALL_SOMETHING = 45;

	/**
	 * C(id): バトルコロシアム、カオス大戦 - <br>
	 * id -1：開始します2：削除された3：終了し
	 */
	public static final int MSG_COLOSSEUM = 49;

	// 血盟情報のHTML
	public static final int HTML_CLAN2 = 51;

	// 料理ウィンドウを開く
	public static final int COOK_WINDOW = 52;

	/** C(type) H(time): 料理のアイコンが表示される */
	public static final int ICON_COOKING = 53;

	/**魚マルチ揺れポッシュ */
	public static final int FISHING = 55;

	/**アイコンの削除 */
	public static final int DEL_ICON = 59;

	/** ドラゴンのパール（3単価の中） */
	public static final int DRAGON_PEARL = 60;

	/** 同盟リスト*/
	public static final int ALLIANCE_LIST = 62;
	
	/** ミニゲーム：5,4,3,2,1カウント */
	public static final int MINIGAME_START_COUNT = 64;
	
	/** ミニゲーム：タイム（0:00開始） */
	public static final int MINIGAME_TIME2 = 65;

	/** ミニゲーム：ゲーム者リスト */
	public static final int MINIGAME_LIST = 66;

	/** ミニゲーム：しばらくして村に移動されます（10秒音） * */
	public static final int MINIGAME_10SECOND_COUNT = 69;
	
	/** ミニゲーム：終了 */
	public static final int MINIGAME_END = 70;

	/** ミニゲーム：タイム */
	public static final int MINIGAME_TIME = 71;

	/** ミニゲーム：タイム削除 */
	public static final int MINIGAME_TIME_CLEAR = 72;

	/** 竜騎士：弱点露出 */
	public static final int SPOT = 75;

	public static final int aaaa1 = 78;// 攻城戦が開始されました。
	public static final int bbbb2 = 79;// 攻城戦が終了しました。
	public static final int cccc3 = 80;//攻城戦が進行中です。
	/** アインハザードバフ */
	public static final int EINHASAD = 82;
	/** 1:ピンクボーダー、2：揺れ、3：爆竹 **/
	public static final int HADIN_DISPLAY = 83;	
	/** インスタンスダンジョン緑メッセージ **/
	public static final int GREEN_MESSAGE = 84;
	/** インスタンスダンジョン黄色メッセージ **/
	public static final int YELLOW_MESSAGE = 61; // インスタンスダンジョンチャプター2待機
	/** インスタンスダンジョン赤いメッセージ **/
	public static final int RED_MESSAGE = 51; // レッドメッセージ
	/** インスタンスダンジョンスコアボード **/	
	public static final int SCORE_MARK = 4;
	/** エメラルドバフ **/
	public static final int EMERALD_ICON = 86;
	public static final int EMERALD_ICON_NEW = 860;

	/** 友好度UI表示
	 * + 欲望の洞窟
	 * - 影の神殿
	 */	
	public static final int KARMA = 87;//追加

	/** ステータスダッジ表示 */
	public static final int INIT_DODGE = 88;//追加

	/** ドラゴン血痕（ヒット：82、パプ：85）*/
	public static final int DRAGONBLOOD = 100;	

	public static final int DODGE = 101;

	public static final int DragonMenu = 102;

	/** 位置送信 **/
	public static final int MINI_MAP_SEND = 111;
	
	/** 血盟倉庫リスト */
	public static final int CLAN_WAREHOUSE_LIST = 117;// 
	
	/** バフォメットサーバーパケット*/
	public static final int BAPO = 114;

	public static final int ICON_SECURITY_SERVICES = 125; //セキュリティバフ
	
	/** PC部屋バフアイコン*/
	public static final int ICON_PC_BUFF = 127; 

	public static final int ER_UpDate = 132;

	public static final int BOOKMARK_SIZE_PLUS_10 = 141;// 記憶拡張

	/** アイコンを表示 **/
	public static final int UNLIMITED_ICON = 147;
	
	
	public static final int UNLIMITED_ICON1 = 180; //無制限のパケット
	public static final int NONE_TIME_ICON = 180;
	
	/** 封印リアルタイム */
    public static final int ITEM_STATUS = 149;
	
	public static final int MAP_TIMER = 153;
	
	/** 蝶キャットのcastgfx値のバフ画像をバフウィンドウに表示 **/
	public static final int BUFFICON = 154;
	
	public static final int ROUND = 156;
	
	public static final int ROUND1 = 156;
	
	public static final int DungeonTime = 159; //ダンジョンパケット
	
	/** 毒関連のアイコン表示 UI6 **/
	public static final int POSION_ICON = 161;
	
	/** 血盟バフアイコン */
    public static final int CLAN_BUFF_ICON = 165;

	/** UI6 3.80 血盟関連**/
	public static final int HTML_PLEDGE_ANNOUNCE = 167;							
	
	public static final int HTML_PLEDGE_REALEASE_ANNOUNCE = 168;							
								
	public static final int HTML_PLEDGE_WRITE_NOTES = 169;							
								
	public static final int HTML_PLEDGE_MEMBERS = 170;							
								
	public static final int HTML_PLEDGE_ONLINE_MEMBERS = 171;	
	
	public static final int ITEM_ENCHANT_UPDATE = 172;
	
	public static final int PLEDGE_EMBLEM_STATUS = 173; //文章注視
	
	public static final int TOWN_TELEPORT = 176;
	
	public static final int ATTACKABLE_DISTANCE = 160;
	public static final int UNKNOWN2 = 184;//主君のダメージバフ
	public static final int UNKNOWN3 = 188;//主君のダメージバフ
	public static final int INVENTORY_SAVE = 189;
	public static final int BATTLE_SHOT = 181;
	public static final int SHOP_OPEN_COUNT = 198;
	public static final int USER_BACK_STAB = 193;
	public static final int ICON_COMBO_BUFF = 204;
	public static final int DRAGON_RAID_BUFF = 179;


	public S_PacketBox(int subCode) {
		writeC(Opcodes.S_EVENT);
		writeC(subCode);

		switch (subCode) {
			case UNKNOWN3:
			writeD(0);
			writeD(0);
			break;
		case INVENTORY_SAVE:
			writeD(0x0d);
			break;
		case UNKNOWN2:
			writeH(0);
			break;
		case MSG_WAR_INITIATIVE:
		case MSG_WAR_OCCUPY:
		case MSG_MARRIED:
		case MSG_FEEL_GOOD:
		case MSG_CANT_LOGOUT:
		case LOGOUT:
		case ICON_SECURITY_SERVICES:
			break;
		case FISHING:
		case MINIGAME_TIME2:
			break;
		case CALL_SOMETHING:
			callSomething();
			break;
		case MINIGAME_10SECOND_COUNT:
			writeC(10);
			writeC(109);
			writeC(85);
			writeC(208);
			writeC(2);
			writeC(220);
			break;
		case DEL_ICON:
			writeH(0);
			break;
		case MINIGAME_END:
			writeC(147);
			writeC(92);
			writeC(151);
			writeC(220);
			writeC(42);
			writeC(74);
			break;
		case MINIGAME_START_COUNT:
			writeC(5);
			writeC(129);
			writeC(252);
			writeC(125);
			writeC(110);
			writeC(17);
			break;
		case ICON_AURA:
			writeC(0x98);
			writeC(0);
			writeC(0);
			writeC(0);
			writeC(0);
			writeC(0);
			break;			
		default:
			break;
		}
	}
	
	/** レベルアップバフ **/
	public S_PacketBox(int time, boolean ck, boolean ck2) {
		writeC(Opcodes.S_EVENT);
		writeC(0x56);
		writeC(0xAA);
		writeC(0x01);
		writeH(time / 16);
		writeH(0x00);
	}
	
	public S_PacketBox(int subCode, int range, int type, boolean bow) {
		writeC(Opcodes.S_EVENT);
		writeC(subCode);
		switch (subCode) {
		case ATTACKABLE_DISTANCE:
			writeC(range);
			writeC(type);
			if (bow)
				writeC(1);
			else {
				writeC(0);
			}
			break;
		}
	}

	public S_PacketBox(int subCode, int time1, int time2, int time3, int time4) {
		writeC(Opcodes.S_EVENT);
		writeC(subCode);
		switch (subCode) {
		case DungeonTime:// 12月14日に変更
			writeD(7);
			writeD(1);
			writeS("$12125");// 技監
			writeD(time1);
			writeD(2);
			writeS("$6081");// 象牙の塔
			writeD(time2);
			writeD(15);
			writeS("$13527");// PC部屋バルログ陣営
			writeD(time3);
			writeD(500);
			writeS("$19375");// PC部屋政務
			writeD(time4);
			writeD(49200);
			break;
		default:
			break;
		}
	}
	public S_PacketBox(int subCode, L1PcInstance pc){
		writeC(Opcodes.S_EVENT);
		writeC(subCode);
		switch(subCode){
		case TOWN_TELEPORT:
			writeC(0x01);
			writeH(pc.getX());
			writeH(pc.getY());
			break;
		case USER_BACK_STAB:
			writeH(pc.getX());
			writeH(pc.getY());
			break;
		}
	}
	public S_PacketBox(int subCode, int value) {
		writeC(Opcodes.S_EVENT);
		writeC(subCode);

		switch (subCode) {
		case DRAGON_RAID_BUFF:
			writeC(0x01);
			writeC(0x27);
			writeC(0x0E);
			writeD(value);// 残り秒
			writeH(0x63EF);
			break;
		case 204: //コンボシステム
		      writeH(value);
		      break;
		case PC_ROOM_BUFF:
			if (value == 1) {
				writeC(0x18);
			} else {
				writeC(0);
			}
			break;
		case SHOP_OPEN_COUNT:
			writeD(value);
			writeD(0x28);
			writeD(0x00);
			break;
		case ICON_BLUEPOTION:
		case ICON_CHATBAN:
		case ICON_I2H:
		case ICON_POLYMORPH:
		case MINIGAME_TIME:
		case INIT_DODGE:
			writeH(value); // time
			break;
		case MAP_TIMER://マップタイマーダンジョン
			writeD(value);
			break;
		case BATTLE_SHOT:
			writeD(value);
			break;
		case MSG_WAR_BEGIN:
		case MSG_WAR_END:
		case MSG_WAR_GOING:
			writeC(value); // castle id
			writeH(0); // ?
			break;
		case MSG_SMS_SENT:
		case WEIGHT:
		case FOOD:
		case DODGE:
			writeC(value);
			break;
		case MSG_ELF:
		case MSG_COLOSSEUM:
		case SPOT:
		case ER_UpDate:
			writeC(value); // msg id
			break;
		case MSG_LEVEL_OVER:
			writeC(0); // ?
			writeC(value); // 0-49以外は表示されない
			break;
		case COOK_WINDOW:
			writeC(0xdb); // ?
			writeC(0x31);
			writeC(0xdf);
			writeC(0x02);
			writeC(0x01);
			writeC(value); // level
			break;		
		case MINIGAME_LIST:
			writeH(0x00); // 参加者数
			writeH(0x00); // 等数
			break;
		case EINHASAD:
			value /= 10000;
			writeD(value);// % 数値 1~200
			writeH(0x10);
			writeC(0x27);
			writeD(0);
			writeH(0);
			break;
		case HADIN_DISPLAY:
			writeC(value);
			break;
		case BOOKMARK_SIZE_PLUS_10:
			writeC(value);
			break;		
		case PLEDGE_EMBLEM_STATUS: 
			writeC(1);
			if(value == 0){ // 0 : オフ1：オン
				writeC(0);
			} else if(value == 1){
				writeC(1);
			}
			writeD(0x00);
			break;
		case ROUND1:
		     writeD(value);
		     writeD(12);
			break;
		default:
			break;
		}
	}
	
	public S_PacketBox(int subCode, int type, int time, boolean second,boolean temp) {
		writeC(Opcodes.S_EVENT);
		writeC(subCode);
		switch (subCode) {
		case BUFFICON:
			writeH(time);
			writeH(type);
			writeH(0x00);
			writeH(second ? 0x01 : 0x00); // 削除追加
			break;
		}// b0 04 80 08 00 00 00 00
	}
	
	public S_PacketBox(int subCode, int time, int gfxid, int type) {
		writeC(Opcodes.S_EVENT);
		writeC(subCode);
		switch (subCode) {
		case BUFFICON:
			writeH(time); //時間
			writeD(gfxid); //アイコン
			writeC(type); //タイプ
			writeC(0x00);
			break;
		}
	}

	public S_PacketBox(int subCode, int type, int time) {
		writeC(Opcodes.S_EVENT);
		writeC(subCode==EMERALD_ICON_NEW ? EMERALD_ICON : subCode);

		switch (subCode) {
		
		case ICON_COOKING:
			if (type != 7) {
				writeC(0x0c);
				writeC(0x0c);
				writeC(0x0c);
				writeC(0x12);
				writeC(0x0c);
				writeC(0x09);
				writeC(0x00);
				writeC(0x00);
				writeC(type);
				writeC(0x24);
				writeH(time);
				writeH(0x00);
			} else {
				writeC(0x0c);
				writeC(0x0c);
				writeC(0x0c);
				writeC(0x12);
				writeC(0x0c);
				writeC(0x09);
				writeC(0xc8);
				writeC(0x00);
				writeC(type);
				writeC(0x26);
				writeH(time);
				writeC(0x3e);
				writeC(0x87);
			}
			break;
		case ICON_AURA:
			writeC(0xdd);
			writeH(time);
			writeC(type);
			break;
		case MSG_DUEL:
			writeD(type); 
			writeD(time);
			break;
		case BUFFICON:
			writeH(time);
		    writeH(type);       
		    writeH(0);
		      break;
		case DRAGONBLOOD:
			writeC(type);
			writeD(time);  
			break;
		case ROUND:
			writeD(type); // 現在のラウンド表示
			writeD(time); // 総ラウンド表示
			break;
		case DRAGON_PEARL:
			//writeC(time);
			//writeC(type);
			writeC((int)((time + 2) / 4));
			writeC(type);
			break;
		case EMERALD_ICON: // エメラルドのアイコン
			writeC(0x70);
			writeC(0x01);
			writeC(type);
			writeH(time); // time(秒)
			break;
		case EMERALD_ICON_NEW:
			// new
			writeC(0x3e);
			writeC(type);
			writeH(time);
			writeC(0x14);
			writeC(0x80);//pc部屋は86
			break;
		case NONE_TIME_ICON:
			writeC(type);// on/off
			writeD(time);// 166 exp30% 228 クール氷286 exp40％343ギルタス地域死亡ペナルティ
							// 409アーマーブレイク497赤の記事の証イベント攻城ゾーン// 477〜479
			writeD(0x00000D67);
			writeH(0x00);
			break;
		case 9278:
			writeC(time);
			writeC(type);
			break;
		default:
			break;
		}
	}
	
	/** アメジスト **/
	// public S_PacketBox(int time, int val, boolean ck, boolean ck2) {
	public S_PacketBox(int time, int val, boolean ck, boolean ck2) {
		writeC(Opcodes.S_EVENT);
		writeC(EMERALD_ICON);
		writeC(0x81);
		writeC(0x01);
		writeC(val);
		writeH(time);
	}// 7e 56 81 01 02 08 07

	public S_PacketBox(int i, int time, boolean ck, boolean ck2, boolean ck3) {
		writeC(Opcodes.S_EVENT);
		writeC(EMERALD_ICON);
		writeC(0x3e);
		writeC(i);
		writeH(time);
		writeC(0x14);
		writeC(0x86);
	}// 0f 56 3e 01 08 07 14 86

	public S_PacketBox(int subCode, String name) {
		writeC(Opcodes.S_EVENT);
		writeC(subCode);

		switch (subCode) {
		case MSG_TOWN_LEADER:
		case HTML_PLEDGE_REALEASE_ANNOUNCE:
			writeS(name);
			break;
		case GREEN_MESSAGE:
			writeC(2);
			writeS(name);
			break;
		default:
			break;
		}
	}
	/**
	 * ヴァラカスレイド
	 */
	
	  public S_PacketBox(int subCode1, int subCode2, String name, boolean ok) {
	    	writeC(Opcodes.S_EVENT);
	    	writeC(subCode1);
	    	switch (subCode2) {
	    	case RED_MESSAGE:
	    	case YELLOW_MESSAGE:
	    		writeC(2);
	    		writeH(26204);
	    		writeC(subCode2);
	    		writeS(name);
	    		break;
	    	case SCORE_MARK:
	    		writeC(subCode2);
	    		writeS(name);
	    		break;
	    	default: // ?
	    	switch (subCode1) {
	    	case MSG_RANK_CHANGED:
	    		writeC(subCode2);
	    		writeS(name);
	    		break;
	    	case ADD_EXCLUDE:
	    	case REM_EXCLUDE:
	    		writeS(name);
	    		writeC(subCode2);
	    		break;
	    	}
	    	break;
	    	}
	    }
	public S_PacketBox(int subCode, int id, String name, String clanName) {
		writeC(Opcodes.S_EVENT);
		writeC(subCode);

		switch (subCode) {
		case MSG_WIN_LASTAVARD:
			writeD(id); // クランIDか何か？
			writeS(name);
			writeS(clanName);
			break;
		default:
			break;
		}
	}
	
    public S_PacketBox(int subCode, L1ItemInstance item, int type) {
        writeC(Opcodes.S_EVENT);
        writeC(subCode);
        switch (subCode) {
        case ITEM_STATUS:
            writeD(item.getId());
            writeH(type);
            break;
        }
    }
    
    public S_PacketBox(int subCode1, int subCode2, String name) {
    	writeC(Opcodes.S_EVENT);
    	writeC(subCode1);
    	switch (subCode2) {
    	case RED_MESSAGE:
    	case YELLOW_MESSAGE:
    		writeC(2);
    		writeH(26204);
    		writeC(subCode2);
    		writeS(name);
    		break;
    	case SCORE_MARK:
    		writeC(subCode2);
    		writeS(name);
    		break;
    	default: // ?
    	switch (subCode1) {
    	case MSG_RANK_CHANGED:
    		writeC(subCode2);
    		writeS(name);
    		break;
    	case ADD_EXCLUDE:
    	case REM_EXCLUDE:
    		writeS(name);
    		writeC(subCode2);
    		break;
    	}
    	break;
    	}
    }
    
/*	public S_PacketBox(int subCode, String name, int type) {  
		writeC(Opcodes.S_EVENT);
		writeC(subCode);
		switch (subCode) {
		case ADD_EXCLUDE:
		case REM_EXCLUDE:
			writeS(name);
			writeC(type);
			break;
		default:
			break;
		}
	}*/
    

	public S_PacketBox(int subCode, Object[] names) {
		writeC(Opcodes.S_EVENT);
		writeC(subCode);

		switch (subCode) {
		case HTML_PLEDGE_ONLINE_MEMBERS:
			writeH(names.length);
			for (Object name : names) {
				if (name == null) continue;
				L1PcInstance pc = (L1PcInstance) name;
				writeS(pc.getName());
				writeC(0);
			}
			break;
		default:
			break;
		}
	}

	public S_PacketBox(int subCode, String[] names, int type) {
		writeC(Opcodes.S_EVENT);
		writeC(subCode);
		writeC(0);
		switch (subCode) {
		case SHOW_LIST_EXCLUDE:
			writeC(type);
			writeC(names.length);
			for (String name : names) {
				writeS(name);
			}
			writeH(0);
			break;
		}
	}
	
	public S_PacketBox(int subCode, L1ItemInstance item){   
		writeC(Opcodes.S_EVENT);
		writeC(subCode);
		switch (subCode) {
		case DragonMenu:
			writeD(item.getId());
			writeC(item.getItemId() == 490012 ? 0x01: 0x00);	// ヒット0
			writeC(item.getItemId() == 490013 ? 0x01: 0x00);	// パプ1
			writeC(item.getItemId() == 490014 ? 0x01: 0x00);	// リンド2
			writeC(0);
			break;
		case ITEM_ENCHANT_UPDATE:
			writeD(item.getId());
			writeC(0x18);
			writeC(0);
			writeH(0);
			writeH(0);
			if(item.getItem().getType2() == 0){
				writeC(0);
			} else {
				writeC(item.getEnchantLevel());
			}
			writeD(item.getId());
			writeD(0);
			writeD(0);
			writeD(item.getBless() >= 128 ? 3 : item.getItem().isTradable() ? 7 : 2);
			writeC(0);
			switch(item.getAttrEnchantLevel()){
			case 0:
				writeC(0);
				break;
			case 1:
				writeC(0x11);
				break;
			case 2:
				writeC(0x21);
				break;
			case 3:
				writeC(0x31);
				break;
			case 4:
				writeC(0x41);
				break;
			case 5:
				writeC(0x51);
				break;
			case 6:
				writeC(0x12);
				break;
			case 7:
				writeC(0x22);
				break;
			case 8:
				writeC(0x32);
				break;
			case 9:
				writeC(0x42);
				break;
			case 10:
				writeC(0x52);
				break;	
			case 11:
				writeC(0x13);
				break;
			case 12:
				writeC(0x23);
				break;
			case 13:
				writeC(0x33);
				break;
			case 14:
				writeC(0x43);
				break;
			case 15:
				writeC(0x53);
				break;
			case 16:
				writeC(0x14);
				break;
			case 17:
				writeC(0x24);
				break;
			case 18:
				writeC(0x34);
				break;
			case 19:
				writeC(0x44);
				break;
			case 20:
				writeC(0x54);
				break;
			}
			
			writeH(0);
			break;
		default:
			break;
		}
	}

	public S_PacketBox(int subCode, L1PcInstance pc, int value1, int value2) {
		writeC(Opcodes.S_EVENT);
		writeC(subCode);
		switch (subCode) {
		case POSION_ICON:
			writeC(value1); // type : 1.ポイズン6：サイレンス
			if(value1 == 2){
				writeH(0x00);
				writeH(value2);
				writeH(0x00);
			}else{
				writeD(value2); // time (초)
			}
			break;
		default:
			break;
		}
	}

	public S_PacketBox(L1PcInstance pc, int subCode) {
		String clanName = pc.getClanname();
		L1Clan clan = L1World.getInstance().getClan(clanName);
		
		writeC(Opcodes.S_EVENT);
		writeC(subCode);

		switch (subCode) {
		
		case CLAN_WAREHOUSE_LIST:
			int count = 0;
			Connection con = null;
			PreparedStatement pstm = null;
			PreparedStatement pstm2 = null;
			PreparedStatement pstm3 = null;
			ResultSet rs = null;
			ResultSet rs3 = null;
			try {
				con = L1DatabaseFactory.getInstance().getConnection();
				pstm = con.prepareStatement("SELECT id, time FROM clan_warehouse_log WHERE clan_name='"
						+ pc.getClanname() + "'");
				rs = pstm.executeQuery();
				while (rs.next()) {
					if (System.currentTimeMillis() - rs.getTimestamp(2).getTime() > 4320000) {// 3日
						pstm2 = con.prepareStatement("DELETE FROM clan_warehouse_log WHERE id='" + rs.getInt(1) + "'");
						pstm2.execute();
					} else
						count++;
				}
				writeD(count);
				pstm3 = con
						.prepareStatement("SELECT name, item_name, item_count, type, time FROM clan_warehouse_log WHERE clan_name='"
								+ pc.getClanname() + "'");
				rs3 = pstm3.executeQuery();
				while (rs3.next()) {
					writeS(rs3.getString(1));
					writeC(rs3.getInt(4));// 0：おまかせ1：捜す
					writeS(rs3.getString(2));
					writeD(rs3.getInt(3));
					writeD((int) (System.currentTimeMillis() - rs3.getTimestamp(5).getTime()) / 60000);				}
			} catch (SQLException e) {
			} finally {
				SQLUtil.close(rs, pstm, con);
				SQLUtil.close(pstm2);
				SQLUtil.close(rs3);
				SQLUtil.close(pstm3);
			}
			break;
		
		//case PLEDGE_REFRESH_PLUS:
		case PLEDGE_REFRESH_MINUS:
			writeS(pc.getName());
			writeC(pc.getClanRank());
			writeH(0);
			break;
		case KARMA:
			writeD(pc.getKarma());
			break;
//		case ALLIANCE_LIST:
//			StringBuffer sb = new StringBuffer();
//			for (int i : pc.getClan().Alliance()) {
//				if (i == 0)
//					continue;
//				L1Clan c = L1World.getInstance().getClan(i);
//				if (c == null)
//					continue;
//				sb.append(c.getClanName() + " ");
//			}
//			writeS(sb.toString());
//			break;
//		case PLEDGE_ONE:
//            writeD(clan.getOnlineMemberCount());
//            for (L1PcInstance targetPc : clan.getOnlineClanMember()) {
//                writeS(targetPc.getName());
//                writeC(targetPc.getClanRank());
//            }
//            writeD((int) (System.currentTimeMillis() / 1000L));
//            writeS(clan.getLeaderName());
//            break;
//		case PLEDGE_TWO:
//			writeD(clan.getClanMemberList().size());
//
//			ClanMember member;
//			ArrayList<ClanMember> clanMemberList = clan.getClanMemberList(); 
//			// すべての血盟員の名前と評価
//			for (int i = 0; i < clanMemberList.size(); i++) {
//				member = clanMemberList.get(i);
//				writeS(member.name);
//				writeC(member.rank);
//			}
//
//			writeD(clan.getOnlineMemberCount());
//			for (L1PcInstance targetPc : clan.getOnlineClanMember()) { // オンライン
//				writeS(targetPc.getName());
//			}
//			break;
		default:
			break;
		}
	}

	public S_PacketBox(int subCode, String name, int mapid, int x, int y, int Mid) {
		writeC(Opcodes.S_EVENT);
		writeC(subCode);
		switch (subCode) {
		case MINI_MAP_SEND:
			writeS(name);
			writeH(mapid);
			writeH(x);
			writeH(y);
			writeD(Mid);
			break;
		default: 
			break;
		}
	}

	public S_PacketBox(int subCode, int value, boolean show) {
		writeC(Opcodes.S_EVENT);
		writeC(subCode);
		switch (subCode) {
		case BAPO:
			writeD(value); // 1~7 旗
			writeD(show ? 0x01 : 0x00); // On Off
			break;
		case UNLIMITED_ICON: // 無制限のパケット
			writeC(show ? 0x01 : 0x00); // On Off // true false
			writeC(value); // 
			break;
		case UNLIMITED_ICON1:
			writeC(show ? 0x01 : 0x00); // On Off // true false
			writeD(value);
			writeD(0);
			writeH(0);
			break;
		default:
			break;
		}
	}
	
	public S_PacketBox(int subCode, boolean show) {
		writeC(Opcodes.S_EVENT);
		writeC(subCode);
		switch(subCode) {
		case CLAN_BUFF_ICON:
			writeC(show ? 0x01 : 0x00);
		break;
		}
	}
	

	private void callSomething() {
		Iterator<L1PcInstance> itr = L1World.getInstance().getAllPlayers().iterator();

		writeC(L1World.getInstance().getAllPlayers().size());
		L1PcInstance pc = null;
		Account acc = null;
		Calendar cal = null;
		while (itr.hasNext()) {
			pc = itr.next();
			acc = Account.load(pc.getAccountName());
			// 時間情報、まずログイン時間を入れてみる655
			if (acc == null) {
				writeD(0);
			} else {
				cal = Calendar.getInstance(TimeZone.getTimeZone(Config.TIME_ZONE));
				long lastactive = acc.getLastActive().getTime();
				cal.setTimeInMillis(lastactive);
				cal.set(Calendar.YEAR, 1970);
				int time = (int) (cal.getTimeInMillis() / 1000);
				writeD(time); // JST 1970 1/1 09:00 この基準
			}

			// キャラクター情報
			writeS(pc.getName()); // 半角12文字まで
			writeS(pc.getClanname()); // []内に表示される文字列。半角12文字まで
		}
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}

		return _byte;
	}
	@Override
	public String getType() {
		return S_PACKETBOX;
	}
}
