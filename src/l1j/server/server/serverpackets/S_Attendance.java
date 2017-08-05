package l1j.server.server.serverpackets;


import java.io.IOException;

import l1j.server.server.Opcodes;
import l1j.server.server.datatables.AttendanceTable;
import l1j.server.server.model.L1AccountAttendance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.templates.L1Attendance;

public class S_Attendance extends ServerBasePacket {

    private static final String S_Attendance = "[S] S_Attendance";

    private byte[] _byte = null;

    public static final int attended = 0x23;
    public static final int attendanceList = 0x24;
    public static final int attendanceCheckIcon = 0x20;
    public static final int attendanceCheck = 0x21;

    public S_Attendance(L1AccountAttendance acc, int location, boolean ispc) { //出席
        buildPacket(acc, location, ispc);
    }

    public S_Attendance(int type, int id, int d) { // チュルチェク
        buildPacket(type, id, d);
    }


    private void buildPacket(L1AccountAttendance acc, int location, boolean ispc) {
        int cctime = 3600 - acc.getTime();
        int ccpctime = 3600 - acc.getTimepc();
        writeC(Opcodes.S_EXTENDED_PROTOBUF);
        writeC(attendanceCheck);
        writeC(0x02);

        writeC(0x0a);
        writeH(cctime > 127 ? 734 : 733); //28個494：493、42 734：733
        /*writeC(0xdd);
        writeC(0x02);*/

        writeC(0x08);
        writeC(0x00);

        int idx = 1;
        for (int cc : acc.toArray()) {
            writeC(0x12);
            writeC(0x06);

            writeC(0x08);
            writeC(idx);

            writeC(0x10);
            writeC(cc);

            writeC(0x18);
            writeC(0x00);
            idx++;
        }
        int check = 0;
        if (acc.toArray().size() >= acc.getDay() - 1) {
            check = 0;
        } else {
            check = acc.toArray().get(acc.getDay() - 1);
        }

        writeC(0x18);
        if (check == 0)
            writeC(0x00);
        else
            writeC(0x01);

        writeC(0x20);
        writeC(0x00);

        writeC(0x28);
        writeC(0x01);

        writeC(0x30);
        writeBit(cctime);

        writeC(0x38);
        writeH(0x1c90);//?


        //pc部屋開始

        writeC(0x0a);
        writeH(ccpctime > 127 ? 734 : 733);
        writeC(0x08);
        writeC(0x01);

        idx = 1;
        for (int cc : acc.toArraypc()) {
            writeC(0x12);
            writeC(0x06);

            writeC(0x08);
            writeC(idx);

            writeC(0x10);
            writeC(cc);

            writeC(0x18);
            writeC(0x00);
            idx++;
        }

        if (acc.toArraypc().size() >= acc.getDaypc() - 1) {
            check = 0;
        } else {
            check = acc.toArraypc().get(acc.getDaypc() - 1);
        }

        writeC(0x18);
        if (check == 0)
            writeC(0x00);
        else
            writeC(0x01);

        writeC(0x20);
        writeC(0x00);

        writeC(0x28);
        writeC(0x01);

        writeC(0x30);
        writeBit(ccpctime);

        writeC(0x38);
        writeH(0x1c90);//?
        //pc部屋終了


        // ウィンドウの位置の値と表現部分
        writeC(0x10);
        writeC(location);//0にすると、通常のタイムが緑色に点灯1にするとpcタイプボルゴン火

        writeC(0x18);
        writeC(ispc == true ? 1 : 0); //1にする必要がインターネットカフェの時間が流れる。

        writeC(0x18);
        writeC(location);//チュルチェクをヌルロトとき表示される画面0一般的な1 pc部屋

        writeH(0x0000);

    }


    private void buildPacket(int type, int id, int d) {
        writeC(Opcodes.S_EXTENDED_PROTOBUF);
        writeC(type);
        switch (type) {
            case attended:
                writeC(0x02);
                writeC(0x08);
                writeC(d);
                writeC(0x10);
                writeC(0x02);
                writeC(0x18);
                writeC(id);//00一般的な01ピバン
                writeH(0x00);
                break;
            case attendanceCheckIcon:
                writeC(0x02);
                writeC(0x08);
                writeC(0x90);
                writeC(0x1c);
                writeC(0x10);
                writeC(0x80);
                writeC(0xa3);
                writeC(0x05);
                writeC(0x18);
                writeC(0x01);
                writeC(0x20);

                writeC(0x01);
                writeC(0x28);
                writeC(0x02);
                writeH(0);
                //	writeC(0x5c); //
                //	writeC(0xf9); //
                break;
            case attendanceList:
                if (id == 0) {
                    writeC(0x02);
                    writeC(0x08);
                    writeC(id);
                    ServerBasePacket detail = new ServerBasePacket() {
                        public byte[] getContent() throws IOException {
                            return getBytes();
                        }
                    };
                    ServerBasePacket sbp = new ServerBasePacket() {
                        public byte[] getContent() throws IOException {
                            return getBytes();
                        }
                    };
                    L1ItemInstance item = null;
                    for (L1Attendance cc : AttendanceTable.getInstance().toArray()) {
                        if (cc.getItem() == null)
                            continue;

                        detail.writeC(0x08);
                        detail.writeC(0x02);
                        detail.writeC(0x10);
                        detail.writeBit(cc.getItem().getItemDescId());
                        detail.writeC(0x18);
                        detail.writeBit(cc.getCount());
                        detail.writeC(0x22);
                        detail.writeS2("出席チェック");
                        detail.writeC(0x28);
                        detail.writeC(0);
                        detail.writeC(0x30);
                        detail.writeBit(cc.getItem().getGfxId());
                        detail.writeC(0x38)
                        ;
                        detail.writeC(1);
                        detail.writeC(0x42);
                        detail.writeS2(cc.getItem().getNameId());
                        item = new L1ItemInstance(cc.getItem());

                        detail.writeC(0x4a);
                        detail.writeC(item.getStatusBytes().length);
                        detail.writeByte(item.getStatusBytes());
                        detail.writeC(0x50);
                        detail.writeC(0x97);

                        detail.writeD(0xffffffff);
                        detail.writeD(0xffffffff);
                        detail.writeC(0x01);

                        sbp.writeC(0x08);
                        sbp.writeC(cc.getDay());
                        sbp.writeC(0x12);
                        sbp.writeC(detail.getLength() - 2);
                        sbp.writeByte(detail.getBytes());

                        writeC(0x12);
                        writeC(sbp.getLength() - 2);
                        writeByte(sbp.getBytes());

                        detail.reset();
                        sbp.reset();
                    }
                    writeH(0x00);
                    detail.clear();
                    sbp.clear();
                    item = null;
                } else if (id == 1) {
                    writeC(0x02);
                    writeC(0x08);
                    writeC(id);
                    ServerBasePacket detail = new ServerBasePacket() {
                        public byte[] getContent() throws IOException {
                            return getBytes();
                        }
                    };
                    ServerBasePacket sbp = new ServerBasePacket() {
                        public byte[] getContent() throws IOException {
                            return getBytes();
                        }
                    };
                    L1ItemInstance item = null;
                    for (L1Attendance cc : AttendanceTable.getInstance().toArray()) {
                        if (cc.getItempc() == null)
                            continue;
                        detail.writeC(0x08);
                        detail.writeC(0x02);
                        detail.writeC(0x10);
                        detail.writeBit(cc.getItempc().getItemDescId());
                        detail.writeC(0x18);
                        detail.writeBit(cc.getCountpc());
                        detail.writeC(0x22);
                        detail.writeLS("出席チェック");
                        detail.writeC(0x28);
                        detail.writeC(0);
                        detail.writeC(0x30);
                        detail.writeBit(cc.getItempc().getGfxId());
                        detail.writeC(0x38);
                        detail.writeC(1);
                        detail.writeC(0x42);
                        detail.writeLS(cc.getItempc().getNameId());

                        item = new L1ItemInstance(cc.getItem());
                        detail.writeC(0x4a);
                        detail.writeC(item.getStatusBytes().length);
                        detail.writeByte(item.getStatusBytes());
                        detail.writeC(0x50);
                        detail.writeC(0x97);

                        detail.writeD(0xffffffff);
                        detail.writeD(0xffffffff);
                        detail.writeC(0x01);

                        sbp.writeC(0x08);
                        sbp.writeC(cc.getDay());
                        sbp.writeC(0x12);
                        sbp.writeC(detail.getLength() - 2);
                        sbp.writeByte(detail.getBytes());
                        writeC(0x12);
                        writeC(sbp.getLength() - 2);
                        writeByte(sbp.getBytes());

                        detail.reset();
                        sbp.reset();
                    }
                    writeH(0x00);
                    detail.clear();
                    sbp.clear();
                    item = null;
                }
                break;
        }
    }

    @Override
    public byte[] getContent() {
        if (_byte == null) {
            _byte = getBytes();
        }
        return _byte;
    }

    public String getType() {
        return S_Attendance;
    }
}

