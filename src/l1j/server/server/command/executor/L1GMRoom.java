/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.command.executor;

import java.util.logging.Logger;

import l1j.server.server.GMCommandsConfig;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1GMRoom implements L1CommandExecutor {
	@SuppressWarnings("unused")
	private static Logger _log = Logger.getLogger(L1GMRoom.class.getName());

	private L1GMRoom() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1GMRoom();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			int i = 0;
			try {
				i = Integer.parseInt(arg);
			} catch (NumberFormatException e) {
			}

			if (i == 1) {
				new L1Teleport().teleport(pc, 32737, 32796, (short) 99, 5, false); // 英字部屋
			} else if (i == 2) {
				new L1Teleport().teleport(pc, 32736, 32796, (short) 16896, 4, false); // 相談所
			} else if (i == 3) {
				new L1Teleport().teleport(pc , 32638, 32955, (short) 0, 5, false); // パンドラ
			} else if (i == 4) {
				new L1Teleport().teleport(pc , 33440, 32805, (short) 4, 5, false); // ギラン
			} else if (i == 5) {
				new L1Teleport().teleport(pc , 32894, 32536, (short) 300, 5, false); // アデン耐性
			} else if (i == 6) {
				new L1Teleport().teleport(pc , 32614, 32788, (short) 4, 5, false); // グルーディン
			} else if (i == 7) {
				new L1Teleport().teleport(pc , 34055, 32281, (short) 4, 5, false); // オレン
			} else if (i == 8) {
				new L1Teleport().teleport(pc , 33515, 32858, (short) 4, 5, false); // バー軽装
			} else if (i == 9) {
				new L1Teleport().teleport(pc , 32763, 32817, (short) 622, 5, false); // 羽村
			} else if (i == 10) {
				new L1Teleport().teleport(pc , 32572, 32944, (short) 0, 5, false); // マルソム
			} else if (i == 11) {
				new L1Teleport().teleport(pc , 33723, 32495, (short) 4, 5, false); // ウェルダン
			} else if (i == 12) {
				new L1Teleport().teleport(pc , 32760, 32870, (short) 610, 5, false); // 桜の村
			} else if (i == 13) {
				new L1Teleport().teleport(pc , 32805, 32814, (short) 5490, 5, false); // 釣り
			} else if (i == 14) {
				new L1Teleport().teleport(pc , 32736, 32787, (short) 15, 5, false); // ケンソン
			} else if (i == 15) {
				new L1Teleport().teleport(pc , 32735, 32788, (short) 29, 5, false); //ウィンソン
			} else if (i == 16) {
				new L1Teleport().teleport(pc , 32730, 32802, (short) 52, 5, false); // ギラン
			} else if (i == 17) {
				new L1Teleport().teleport(pc , 32572, 32826, (short) 64, 5, false); // ハイネ城
			} else if (i == 18) {
				new L1Teleport().teleport(pc , 32895, 32533, (short) 300, 5, false); // アデン城
			} else if (i == 19) {
				new L1Teleport().teleport(pc , 33168, 32779, (short) 4, 5, false); // ケンソン守護塔
			} else if (i == 20) {
				new L1Teleport().teleport(pc , 32623, 33379, (short) 4, 5, false); // ウィンソン守護塔
			} else if (i == 21) {
				new L1Teleport().teleport(pc , 33630, 32677, (short) 4, 5, false); // ギラン守護塔
			} else if (i == 22) {
				new L1Teleport().teleport(pc , 33524, 33394, (short) 4, 5, false); // ハイネ守護塔
			} else if (i == 23) {
				new L1Teleport().teleport(pc , 34090, 33260, (short) 4, 5, false); // アデン守護塔
			} else if (i == 24) {
				new L1Teleport().teleport(pc , 32424, 33068, (short) 440, 5, false); // 海賊島
			} else if (i == 25) {
				new L1Teleport().teleport(pc , 32800, 32868, (short) 1001, 5, false); // ベヒモス
			} else if (i == 26) {
				new L1Teleport().teleport(pc , 32800, 32856, (short) 1000, 5, false); // シルベリア
			} else if (i == 27) {
				new L1Teleport().teleport(pc , 32630, 32903, (short) 780, 5, false); // テーベ砂漠
			} else if (i == 28) {
				new L1Teleport().teleport(pc , 32743, 32799, (short) 781, 5, false); // テーベピラミッド内部
			} else if (i == 29) {
				new L1Teleport().teleport(pc , 32735, 32830, (short) 782, 5, false); // テーベアヒルシステム祭壇
			} else if (i == 30) {
				new L1Teleport().teleport(pc , 32734, 32270, (short) 4, 5, false); // フェニックス
			} else if (i == 31) {
				new L1Teleport().teleport(pc , 32699, 32819, (short) 82, 5, false); // デーモン
			} else if (i == 32) {
				new L1Teleport().teleport(pc , 32769, 32770, (short) 56, 5, false); //技監4階
			} else if (i == 33) {
				new L1Teleport().teleport(pc , 32929, 32995, (short) 410, 5, false); // 魔族の神殿				
			} else if (i == 34) {
				new L1Teleport().teleport(pc , 32791, 32691, (short) 1005, 5, false); // レイドアンタラス
			} else if (i == 35) {
				new L1Teleport().teleport(pc , 32960, 32840, (short) 1011, 5, false); //レイドパプリオン
			} else if (i == 36) {
				new L1Teleport().teleport(pc , 32849, 32876, (short) 1017, 5, false); // リンドレイド
			} else if (i == 37) {
				new L1Teleport().teleport(pc , 32725, 32800, (short) 67, 5, false); // 塗って部屋
			} else if (i == 38) {
				new L1Teleport().teleport(pc , 32771, 32831, (short) 65, 5, false); // 波プバン
			} else if (i == 39) {
				new L1Teleport().teleport(pc , 32696, 32824, (short) 37, 5, false); // バーモス（ヨンドン7階）
			} else if (i == 40) {
				new L1Teleport().teleport(pc , 32922, 32812, (short) 430, 5, false); // 精霊の墓
			} else if (i == 41) {
				new L1Teleport().teleport(pc , 32737, 32834, (short) 2004, 5, false); // 高ラス
			} else if (i == 42) {
				new L1Teleport().teleport(pc , 32707, 32846, (short) 2, 5, false); // ソムドン2階
			} else if (i == 43) {
				new L1Teleport().teleport(pc , 32772, 32861, (short) 400, 5, false); //古代の墓
			} else if (i == 44) {
				new L1Teleport().teleport(pc , 32982, 32808, (short) 244, 5, false); // オタン
			} else if (i == 45) {
				new L1Teleport().teleport(pc , 32811, 32819, (short) 460, 5, false); // ラバー2階
			} else if (i == 46) {
				new L1Teleport().teleport(pc , 32724, 32792, (short) 536, 5, false); // といってい3階
			} else if (i == 47) {
				new L1Teleport().teleport(pc , 32847, 32793, (short) 532, 5, false); // といってい4階
			} else if (i == 48) {
				new L1Teleport().teleport(pc , 32843, 32693, (short) 550, 5, false); // 船の墓
			} else if (i == 49) {
				new L1Teleport().teleport(pc , 32781, 32801, (short) 558, 5, false); // 深海
			} else if (i == 50) {
				new L1Teleport().teleport(pc , 32731, 32862, (short) 784, 5, false); // ジェフ
			} else if (i == 51) {
				new L1Teleport().teleport(pc , 32728, 32704, (short) 4, 5, false); // 亀裂1
			} else if (i == 52) {
				new L1Teleport().teleport(pc , 32827, 32658, (short) 4, 5, false); //亀裂2
			} else if (i == 53) {
				new L1Teleport().teleport(pc , 32852, 32713, (short) 4, 5, false); // 亀裂3
			} else if (i == 54) {
				new L1Teleport().teleport(pc , 32914, 33427, (short) 4, 5, false); //亀裂4
			} else if (i == 55) {
				new L1Teleport().teleport(pc , 32962, 33251, (short) 4, 5, false); // 亀裂5
			} else if (i == 56) {
				new L1Teleport().teleport(pc , 32908, 33169, (short) 4, 5, false); // 亀裂6
			} else if (i == 57) {
				new L1Teleport().teleport(pc , 34272, 33361, (short) 4, 5, false); // 亀裂7
			} else if (i == 58) {
				new L1Teleport().teleport(pc , 34258, 33202, (short) 4, 5, false); // 亀裂8
			} else if (i == 59) {
				new L1Teleport().teleport(pc , 34225, 33313, (short) 4, 5, false); // 亀裂9
			} else if (i == 60) {
				new L1Teleport().teleport(pc , 32682, 32892, (short) 5167, 5, false); // 悪魔の領土
			} else if (i == 61) {
				new L1Teleport().teleport(pc , 32862, 32862, (short) 537, 5, false); // ギルタス
			} else if (i == 62) {
				new L1Teleport().teleport(pc , 32738, 32448, (short) 4, 5, false); // 火田民
			} else if (i == 63) {
				new L1Teleport().teleport(pc , 32797, 32285, (short) 4, 5, false); // オソンストップ
			} else if (i == 64) {
				new L1Teleport().teleport(pc , 33052, 32339, (short) 4, 5, false); // エルフの森
			} else if (i == 65) {
				new L1Teleport().teleport(pc, 32738, 32872, (short) 2236, 5, false); // サーバーられるアジト
			} else {
				L1Location loc = GMCommandsConfig.ROOMS.get(arg.toLowerCase());
				if (loc == null) {
					pc.sendPackets(new S_SystemMessage("==================<帰還場所>==================="));
					pc.sendPackets(new S_SystemMessage("\\aD1.GM1 2.GM2 3.パンドラ 4.ギラン 5.アデン耐性 6.グルーディン 7.オレン"));
					pc.sendPackets(new S_SystemMessage("\\aD8.バー軽装 9.羽言葉 10.マルソム 11.ウェルダン 12.桜 13.釣り"));
					pc.sendPackets(new S_SystemMessage("\\aL14.ケント城 15.ウィン和音 16.ギラン城 17.ハイ性 18.アデン城"));
					pc.sendPackets(new S_SystemMessage("\\aL19.守護塔 20.守護塔 21.守護塔 22.守護塔 23.守護塔"));
					pc.sendPackets(new S_SystemMessage("\\aH24.ヘソム 25.ベヒ某 26.シルベリア 27.テーベ 28.ピラミッド"));
					pc.sendPackets(new S_SystemMessage("\\aH29.ピラミッド 30.フェニックス 31.デーモン 32.技監4層 33.魔族の部屋"));
					pc.sendPackets(new S_SystemMessage("\\aD34.ヒット 35.パプ 36.リンド 37.塗って 38.旧派フー 39.バーモス"));
					pc.sendPackets(new S_SystemMessage("\\aL40.政務 41.高ラス 42.ソムドン2層 43.古代の墓 44.オタン"));
					pc.sendPackets(new S_SystemMessage("\\aL45.といってい2層 46.といってい3層 47.といってい4層 48.船舶 49.深海"));
					pc.sendPackets(new S_SystemMessage("\\aL50.第ブレキ 51~59.亀裂 60.悪魔の領土 61.ギルタス"));
					pc.sendPackets(new S_SystemMessage("\\aL62.火田村 63.オーク性 64.エルフの森 65.サーバーられるアジト"));
					return;
				}
				new L1Teleport().teleport(pc, loc.getX(), loc.getY(), (short) loc
						.getMapId(), 5, false);
			}
			if (i > 0 && i < 33) {
				pc.sendPackets(new S_SystemMessage("オペレータ帰還（" + i + "）番に移動しました。"));
			}
		} catch (Exception exception) {
			pc.sendPackets(new S_SystemMessage("帰還[ジャンソミョン]を入力してください。（ジャンソミョンはGMCommands.xmlを参照）"));
		}
	}
}
