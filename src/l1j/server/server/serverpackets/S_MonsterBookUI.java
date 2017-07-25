package l1j.server.server.serverpackets;

import java.util.HashMap;

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;

public class S_MonsterBookUI extends ServerBasePacket {
    /*
     * @param Serverbasepacket
     * 
     * @param S_EXTENDED_PROTOBUF Opcode
     * 
     * @param Type H値に変更
     **/
    public static final int MONSTER_BOOK = 559; // モンスター図鑑有効
    public static final int MONSTER_LOAD = 560; // モンスター図鑑にモンスターロード
    public static final int MONSTER_END = 564; // モンスター図鑑終わり
    public static final int MONSTER_ADD = 567; // モンスター図鑑にモンスターを追加
    public static final int MONSTER_CLEAR = 568; // モンスター図鑑クエストクリア

    /** テスト用 **/
    public S_MonsterBookUI(int code, int value) {
	writeC(Opcodes.S_EXTENDED_PROTOBUF);
	writeH(code);
	switch (code) {
	case MONSTER_BOOK: {
	    writeC(0x08);
	    writeC(0x00);
	    writeC(0x10);
	    writeC(0x00);
	    writeH(0);
	    break;
	}
	case MONSTER_LOAD: {
	    writeC(8);
	    writeC(0);
	    writeC(16);
	    writeC(0);
	    for (int i = 0; i < 555; i++) {
		writeC(26);
		int j = size7B(i) * 2 + 2;
		write7B(j);
		writeC(8);
		write7B(i);
		writeC(16);
		write7B(i);
	    }
	    writeH(0);
	    break;
	}
	case MONSTER_ADD: {
	    writeC(0x08);
	    writeC(0x72);
	    writeC(0x10);
	    write7B(10000);
	    writeH(0);
	    break;
	}
	case MONSTER_CLEAR:
	    writeC(0x08);
	    write7B(value);
	    writeC(0x10);
	    write7B(System.currentTimeMillis() / 1000L);
	    writeH(0);
	    break;
	case MONSTER_END:
	    writeC(0x08);
	    writeC(0x00);
	    writeC(0x10);
	    write7B(value);
	    writeH(0);
	    break;
	}
    }

    public S_MonsterBookUI(L1PcInstance pc, int code) {
	writeC(Opcodes.S_EXTENDED_PROTOBUF);
	writeH(code);
	writeC(0x08);
	switch (code) {
	case MONSTER_BOOK:
	    writeC(0);
	    writeC(0x10);
	    writeC(0);
	    break;
	}
	writeH(0);
    }

    public S_MonsterBookUI(int code, int num, int counter) {
	writeC(Opcodes.S_EXTENDED_PROTOBUF);
	writeH(code);
	writeC(0x08);
	switch (code) {
	case MONSTER_ADD:
	    write7B(num); // モンスター番号
	    writeC(0x10);
	    write7B(counter); // キルス
	    break;
	}
	writeH(0);
    }

    public S_MonsterBookUI(int code, HashMap<Integer, Integer> monsterList) {
	writeC(Opcodes.S_EXTENDED_PROTOBUF);
	writeH(code);
	switch (code) {
	case MONSTER_LOAD:
	    writeC(0x08);
	    writeC(0);
	    writeC(0x10);
	    writeC(0);
	    if (monsterList != null) {
		monsterList.forEach((monstertnumber, monsterkillcount) -> {
		    writeC(0x1a); // 長さ
		    writeC(size7B(monstertnumber) + size7B(monsterkillcount) + 2);
		    writeC(0x08); // モンスターナンバー
		    write7B(monstertnumber);
		    writeC(0x10); // モンスターキルス
		    write7B(monsterkillcount);
		});
	    }
	    writeH(0);
	    break;
	case MONSTER_BOOK:
	    writeC(0x08);
	    writeC(0x00);
	    writeC(0x10);
	    writeC(0x00);
	    if (monsterList != null) {
		monsterList.forEach((questNum, value) -> {
		    if (value != 0) {
			long time = System.currentTimeMillis() / 1000L;
			writeC(0x1a); // 長さ
			writeC(size7B(questNum) + size7B((int) time) + 4);
			writeC(0x08); // クエスト番号
			write7B(questNum);
			writeC(0x10); // 保存時間
			write7B((int) time);
			writeC(0x18); // 完了するかどうか
			writeC(value);
		    }
		});
	    }
	    writeH(0);
	    break;
	}
    }

    @Override
    public byte[] getContent() {
	return getBytes();
    }
}
