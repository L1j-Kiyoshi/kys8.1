package l1j.server.server.serverpackets;

import java.util.List;
import java.util.logging.Logger;

import l1j.server.server.Opcodes;
import l1j.server.server.Controller.BugRaceController;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.templates.L1BoardPost;

public class S_Board extends ServerBasePacket {

	private static final String S_BOARD = "[S] S_Board";

	@SuppressWarnings("unused")
	private static Logger _log = Logger.getLogger(S_Board.class.getName());

	private static final int TOPIC_LIMIT = 8;

	private byte[] _byte = null;

	public S_Board(L1NpcInstance board) {
		switch (board.getNpcId()) {
		case 4200015:// サーバー情報掲示板
			buildPacketNotice(board, 0);
			break;
		case 4200020:// オペレータ1
			buildPacketNotice1(board, 0);
			break;
		case 4200021: // オペレータ2
			buildPacketNotice2(board, 0);
			break;
		case 4200022:// オペレータ3
			buildPacketNotice3(board, 0);
			break;
		case 500002:// 提案
			buildPacketPhone(board, 0);
			break;
		case 900006:// ドラゴンキー通知掲示板
			buildPacketKey(board, 0);
			break;
		case 999999:// バグベアーレース掲示板
			buildPacket1(board, 0);
			break;
		case 500001:// 全体ランキング
			buildPacket2(board, 0);
			break;
		case 4200013:// バグ掲示板
			buildPacket3(board, 0);
			break;
		default:// デフォルト
			buildPacket(board, 0);
			break;
		}
	}

	public S_Board(L1NpcInstance board, int number) {
		switch (board.getNpcId()) {
		case 4200015:// サーバー情報掲示板
			buildPacketNotice(board, number);
			break;
		case 42000162: // オペレータ1
			buildPacketNotice1(board, number);
			break;
		case 42000163:// オペレータ2
			buildPacketNotice2(board, number);
			break;
		case 4200099:// オペレータ3
			buildPacketNotice3(board, number);
			break;
		case 500002:// 提案
			buildPacketPhone(board, number);
			break;
		case 900006://ドラゴンキー通知掲示板
			buildPacketKey(board, number);
			break;
		case 999999:// バグベアーレース掲示板
			buildPacket1(board, number);
			break;
		case 500001:// 全体ランキング
			buildPacket2(board, number);
			break;
		case 4200013:// バグ掲示板
			buildPacket3(board, number);
			break;
		default:// デフォルト
			buildPacket(board, number);
			break;
		}
	}

	private void buildPacket1(L1NpcInstance board, int number) {	// バグベアーレース出場選手の状態
		writeC(Opcodes.S_HYPERTEXT);
		writeD(board.getId());
		writeS("maeno4");
		writeC(0);
		writeH(15);

		for( int i = 0; i < 5; ++i ) {
			writeS(BugRaceController.getInstance()._littleBugBear[i].getName()); //バグベア名前
			writeS(BugRaceController.getInstance()._bugCondition[i]); //状態
			writeS(Double.toString(BugRaceController.getInstance()._winRate[i]) + "%"); //勝率
		}
	}
	private void buildPacket2(L1NpcInstance board,int number) {
		int count = 0;
		String[][] db = null;
		int[] id = null;
		db = new String[9][3];
		id = new int[9];
		while (count < 9) {
			id[count] = count + 1;
			db[count][0] = "";// Ranking
			db[count][1] = "";
			count++;
		}
		//db[0][2] = "--------- 全体順位";
		db[0][2] = "--------- ウォリアー";
		db[1][2] = "--------- 君主";
		db[2][2] = "--------- ナイト";
		db[3][2] = "--------- エルフ";
		db[4][2] = "--------- ウィザード";
		db[5][2] = "--------- ダークエルフ";
		db[6][2] = "--------- ドラゴンナイト";
		db[7][2] = "--------- イリュージョニスト";
		writeC(Opcodes.S_BOARD_LIST);
		writeC(0);
		writeD(board.getId());
		writeC(0xFF); // ?
		writeC(0xFF); // ?
		writeC(0xFF); // ?
		writeC(0x7F); // ?
		writeH(9);
		writeH(300);
		for (int i = 0; i < 8; ++i) {
			writeD(id[i]);
			writeS(db[i][0]);
			writeS(db[i][1]);
			writeS(db[i][2]);
		}
	}
	private void buildPacket3(L1NpcInstance board,int number) {
		int count = 0;
		String[][] db = null;
		int[] id = null;
		db = new String[8][3];
		id = new int[8];
		while(count < 8) {
			id[count] = count + 1;
			db[count][0] = "バグ監視";
			db[count][1] = "";
			count++;
		}
		db[0][2] = "1.武器ランキング";
		db[1][2] = "2.防具ランキング";
		db[2][2] = "3.アデンランキング";
		db[3][2] = "4.レベルランキング";
		db[4][2] = "5.神秘羽ランキング";
		db[5][2] = "6.倉庫アデンランキング";
		db[6][2] = "7. HPランキング";
		db[7][2] = "8. MPランキング";


		writeC(Opcodes.S_BOARD_LIST);
		//writeC(0x00);//10月18日追加
		writeC(0);
		writeD(board.getId());
		writeC(0xFF); // ?
		writeC(0xFF); // ?
		writeC(0xFF); // ?
		writeC(0x7F); // ?
		writeH(8);
		writeH(300);
		for (int i = 0; i < 8; ++i) {
			writeD(id[i]);
			writeS(db[i][0]);
			writeS(db[i][1]);
			writeS(db[i][2]);
		}
  }



	private void buildPacket(L1NpcInstance board, int number) {
		List<L1BoardPost> topics = L1BoardPost.index(number, TOPIC_LIMIT);
		writeC(Opcodes.S_BOARD_LIST);
		writeC(0); // DragonKeybbs = 1
		writeD(board.getId());
		if (number == 0) {
			writeD(0x7FFFFFFF);
		} else {
			writeD(number);
		}
		writeC(topics.size());
		if (number == 0) {
			writeC(0);
			writeH(300);
		}
		for (L1BoardPost topic : topics) {
			writeD(topic.getId());
			writeS(topic.getName());
			writeS(topic.getDate());
			writeS(topic.getTitle());
		}
	}
	private void buildPacketNotice(L1NpcInstance board, int number) {
		List<L1BoardPost> topics = L1BoardPost.indexGM(number, TOPIC_LIMIT);
		writeC(Opcodes.S_BOARD_LIST);
		writeC(0); // DragonKeybbs = 1
		writeD(board.getId());
		if (number == 0) {
			writeD(0x7FFFFFFF);
		} else {
			writeD(number);
		}
		writeC(topics.size());
		if (number == 0) {
			writeC(0);
			writeH(300);
		}
		for (L1BoardPost topic : topics) {
			writeD(topic.getId());
			writeS(topic.getName());
			writeS(topic.getDate());
			writeS(topic.getTitle());
		}
	}
	private void buildPacketNotice1(L1NpcInstance board, int number) {
		List<L1BoardPost> topics = L1BoardPost.indexGM1(number, TOPIC_LIMIT);
		writeC(Opcodes.S_BOARD_LIST);
		writeC(0); // DragonKeybbs = 1
		writeD(board.getId());
		if (number == 0) {
			writeD(0x7FFFFFFF);
		} else {
			writeD(number);
		}
		writeC(topics.size());
		if (number == 0) {
			writeC(0);
			writeH(300);
		}
		for (L1BoardPost topic : topics) {
			writeD(topic.getId());
			writeS(topic.getName());
			writeS(topic.getDate());
			writeS(topic.getTitle());
		}
	}
	private void buildPacketNotice2(L1NpcInstance board, int number) {
		List<L1BoardPost> topics = L1BoardPost.indexGM2(number, TOPIC_LIMIT);
		writeC(Opcodes.S_BOARD_LIST);
		writeC(0); // DragonKeybbs = 1
		writeD(board.getId());
		if (number == 0) {
			writeD(0x7FFFFFFF);
		} else {
			writeD(number);
		}
		writeC(topics.size());
		if (number == 0) {
			writeC(0);
			writeH(300);
		}
		for (L1BoardPost topic : topics) {
			writeD(topic.getId());
			writeS(topic.getName());
			writeS(topic.getDate());
			writeS(topic.getTitle());
		}
	}
	private void buildPacketNotice3(L1NpcInstance board, int number) {
		List<L1BoardPost> topics = L1BoardPost.indexGM3(number, TOPIC_LIMIT);
		writeC(Opcodes.S_BOARD_LIST);
		writeC(0); // DragonKeybbs = 1
		writeD(board.getId());
		if (number == 0) {
			writeD(0x7FFFFFFF);
		} else {
			writeD(number);
		}
		writeC(topics.size());
		if (number == 0) {
			writeC(0);
			writeH(300);
		}
		for (L1BoardPost topic : topics) {
			writeD(topic.getId());
			writeS(topic.getName());
			writeS(topic.getDate());
			writeS(topic.getTitle());
		}
	}
	private void buildPacketPhone(L1NpcInstance board, int number) {
		List<L1BoardPost> topics = L1BoardPost.indexPhone(number, TOPIC_LIMIT);
		writeC(Opcodes.S_BOARD_LIST);
		writeC(0); // DragonKeybbs = 1
		writeD(board.getId());
		if (number == 0) {
			writeD(0x7FFFFFFF);
		} else {
			writeD(number);
		}
		writeC(topics.size());
		if (number == 0) {
			writeC(0);
			writeH(300);
		}
		for (L1BoardPost topic : topics) {
			writeD(topic.getId());
			writeS(topic.getName());
			writeS(topic.getDate());
			writeS(topic.getTitle());
		}
	}

	private void buildPacketKey(L1NpcInstance board, int number) {
		List<L1BoardPost> topics = L1BoardPost.indexKey(number, TOPIC_LIMIT);
		writeC(Opcodes.S_BOARD_LIST);
		writeC(0); // DragonKeybbs = 1
		writeD(board.getId());
		if (number == 0) {
			writeD(0x7FFFFFFF);
		} else {
			writeD(number);
		}
		writeC(topics.size());
		if (number == 0) {
			writeC(0);
			writeH(300);
		}
		for (L1BoardPost topic : topics) {
			writeD(topic.getId());
			writeS(topic.getName());
			writeS(topic.getDate());
			writeS(topic.getTitle());
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
		return S_BOARD;
	}
}

