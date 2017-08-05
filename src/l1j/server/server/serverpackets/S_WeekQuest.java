package l1j.server.server.serverpackets;

import java.util.StringTokenizer;

import l1j.server.server.Opcodes;
import l1j.server.server.datatables.WeekQuestTable;
import l1j.server.server.model.Instance.L1PcInstance;

public class S_WeekQuest extends ServerBasePacket {
    private static final String _TYPE = "[S] S_WeekQuest";

    private byte[] _byte = null;

    private String subP = "01 0a 55 12 10 08 01 10 9a 83 2c 18 00 22 06 08 d8 87 01 10 01 12 11 08 02 10 81 88 6e 18 c6 20 22 06 08 d8 87 01 10 02 12 12 08 03 10 84 a0 b8 03 18 9f 78 22 06 08 d8 87 01 10 05 1a 0b 08 01 10 84 a0 b8 03 18 c1 87 01 1a 0b 08 01 10 b5 bf f0 06 18 c0 87 01 20 37";

    public S_WeekQuest(int type) {
        writeC(Opcodes.S_EXTENDED_PROTOBUF);
        writeH(type);
        switch (type) {
            case 566:
                writeC(0x08);
                writeC(0x02);
                writeC(0x10);
                writeC(0x01);
                break;
        }
        writeH(0x00);
    }

    public S_WeekQuest(int line, int num, int count) {
        writeC(Opcodes.S_EXTENDED_PROTOBUF);
        writeC(0x2d);
        writeC(0x03);
        writeC(0x08);
        writeC(line);
        writeC(0x10);
        writeC(num);
        writeC(0x18);
        writeC(count);
        writeH(0x00);

    }

    public S_WeekQuest(L1PcInstance pc) {
        writeC(Opcodes.S_EXTENDED_PROTOBUF);
        writeC(0x2a); //type
        writeC(0x03);
        writeC(0x0a);
        writeC(0xe7); //total length
        StringTokenizer st = new StringTokenizer(subP);

        while (st.hasMoreTokens()) {
            writeC(Integer.parseInt(st.nextToken(), 16));
        }

        for (int i = 0; i < 3; i++) {
            writeC(0x12);
            writeC(0x2e); // したclearの長さ
            writeC(0x08);
            writeC(i);
            writeC(0x18);
            if (pc.isLineClear(i)) {
                if (pc.getReward(i)) {
                    writeC(0x05); //補償を受けた
                } else {
                    writeC(0x03); //受けなく完了状態
                }
            } else {
                writeC(0x01); //進行中
            }
            for (int j = 0; j < 3; j++) {
                writeC(0x22);
                writeC(0x0c);

                writeC(0x08);
                writeC(i % 3);
                writeC(0x10);
                    /*if(j==2){
                        writeC(0x32);
					}else{
						writeC(0x28);
					}*/
                int maxcount = WeekQuestTable.getInstance().maxcount.get(i * 3 + j);
                writeC(maxcount);
                writeC(0x18);

                switch (i) {
                    case 0: //1ボンジュル
                        switch (j) {
                            case 0:
                                //これに加えがpc.getWeekType（）によるモンスターを追加
                                switch (pc.getWeekType()) {
                                    case 1:
                                        writeC(0x96);
                                        writeC(0x08);
                                        break;
                                    case 2:
                                        writeC(0xbf);
                                        writeC(0x09);
                                        break;
                                    case 3:
                                        writeC(0x80);
                                        writeC(0x0c);
                                        break;
                                }
                                break;
                            case 1:
                                switch (pc.getWeekType()) {
                                    case 1:
                                        writeC(0x9c);
                                        writeC(0x08);
                                        break;
                                    case 2:
                                        writeC(0xc0);
                                        writeC(0x09);
                                        break;
                                    case 3:
                                        writeC(0x84);
                                        writeC(0x0c);
                                        break;
                                }
                                break;
                            case 2:
                                switch (pc.getWeekType()) {
                                    case 1:
                                        writeC(0x99);
                                        writeC(0x08);
                                        break;
                                    case 2:
                                        writeC(0xc3);
                                        writeC(0x09);
                                        break;
                                    case 3:
                                        writeC(0x87);
                                        writeC(0x0c);
                                        break;
                                }
                                break;
                        }
                        break;
                    case 1:
                        switch (j) {
                            case 0:
                                switch (pc.getWeekType()) {
                                    case 1:
                                        writeC(0x8c);
                                        writeC(0x07);
                                        break;
                                    case 2:
                                        writeC(0xef);
                                        writeC(0x0b);
                                        break;
                                    case 3:
                                        writeC(0xe7);
                                        writeC(0x0d);
                                        break;
                                }
                                break;
                            case 1:
                                switch (pc.getWeekType()) {
                                    case 1:
                                        writeC(0x9c);
                                        writeC(0x07);
                                        break;
                                    case 2:
                                        writeC(0xf3);
                                        writeC(0x0b);
                                        break;
                                    case 3:
                                        writeC(0xed);
                                        writeC(0x0d);
                                        break;
                                }
                                break;
                            case 2:
                                switch (pc.getWeekType()) {
                                    case 1:
                                        writeC(0x99);
                                        writeC(0x07);
                                        break;
                                    case 2:
                                        writeC(0xf6);
                                        writeC(0x0b);
                                        break;
                                    case 3:
                                        writeC(0xf0);
                                        writeC(0x0d);
                                        break;
                                }
                                break;
                        }
                        break;
                    case 2:
                        switch (j) {
                            case 0:
                                switch (pc.getWeekType()) {
                                    case 1:
                                        writeC(0x91);
                                        writeC(0x0a);
                                        break;
                                    case 2:
                                        writeC(0xe0);
                                        writeC(0x0b);
                                        break;
                                    case 3:
                                        writeC(0xa9);
                                        writeC(0x0c);
                                        break;
                                }
                                break;
                            case 1:
                                switch (pc.getWeekType()) {
                                    case 1:
                                        writeC(0x94);
                                        writeC(0x0a);
                                        break;
                                    case 2:
                                        writeC(0xe3);
                                        writeC(0x0b);
                                        break;
                                    case 3:
                                        writeC(0x9a);
                                        writeC(0x0c);
                                        break;
                                }
                                break;
                            case 2:
                                switch (pc.getWeekType()) {
                                    case 1:
                                        writeC(0xea);
                                        writeC(0x09);
                                        break;
                                    case 2:
                                        writeC(0xea);
                                        writeC(0x0b);
                                        break;
                                    case 3:
                                        writeC(0xa3);
                                        writeC(0x0c);
                                        break;
                                }
                                break;
                        }
                        break;

                }
                writeC(0x20);
                writeC(0xce); //もともとはここにモンスター図鑑のある番号が入ること
                writeC(0x01);

                writeC(0x28);
                //		writeC(pc.getWcount(i*3+j));
            }

        }
        writeH(0x00);

    }

    @Override
    public byte[] getContent() {
        if (_byte == null) {
            _byte = _bao.toByteArray();
        }
        return _byte;
    }

    @Override
    public String getType() {
        return _TYPE;
    }
}
