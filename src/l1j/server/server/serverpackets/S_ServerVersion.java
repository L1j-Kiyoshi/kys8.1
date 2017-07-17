package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;

public class S_ServerVersion extends ServerBasePacket {
    private static final String S_SERVER_VERSION = "[S] ServerVersion";
    private final int UPTIME = (int) (System.currentTimeMillis() / 1000L);

    public S_ServerVersion() {
	// int time = L1GameTimeClock.getInstance().getGameTime().getSeconds();
	// time = time - (time % 300);

	/** 20170124 **/ // by feel.
	// writeC(Opcodes.S_EXTENDED_PROTOBUF); // 20170106 から変更ダムby feel適用
	// // S_VERSION_CHECK

	// String s = "35 03 08 00 10 05 " + "18 89 BD 8F AC 06 " + "20 89 BD 8F
	// AC 06 " + "28 FD AC EF C0 07 "
	// + "30 89 BD 8F AC 06 " + "38 CF E0 A1 C6 05 " + "40 00 48 00 " + "50
	// 8B FB FF A7 03 ";
	// StringTokenizer st = new StringTokenizer(s);
	// while (st.hasMoreTokens()) {
	// writeC(Integer.parseInt(st.nextToken(), 16));
	// }
	// writeC(0x58);
	// long time = (int) (System.currentTimeMillis() / 1000);
	// write7B(time);
	// String s2 = "60 FC 97 87 48 " + "68 95 CC E4 4C " + "70 94 BD E9 4C "
	// + "78 DA D8 8E AB 06 "
	// + "80 01 F4 89 C6 4C " + "88 01 00 47 0B";
	// st = new StringTokenizer(s2);
	// while (st.hasMoreTokens()) {
	// writeC(Integer.parseInt(st.nextToken(), 16));
	// }

	// writeC(Opcodes.S_VERSION_CHECK);
	// writeC(0x00);
	// writeC(0x05);
	// writeD(0x65a2bce1); // F6 02 96 09
	// writeD(0x65a2bce1); // F6 02 96 09
	// writeD(0x781bd67d); // 7D D6 1B 78
	// writeD(0x65a2bce1); // f6 02 96 09
	// writeD(UPTIME);
	// writeC(0x00);
	// writeC(0x00);
	// writeC(0x04); // here
	// writeD(0x34ff7d0a); // 8B FD FF 34
	// writeD(UPTIME);
	// writeD(0x0995af5c); // 2C 52 91 09
	// writeD(0x0996054d); // 45 4E 91 09
	// writeD(0x0998f34c); // F4 84 91 09

	writeC(Opcodes.S_VERSION_CHECK);
	writeC(0x00);
	writeC(0x01);// 第幾個伺服器
	writeD(150924202);
	writeD(150912681);
	writeD(2013122601);
	writeD(150924201);
	writeD(0x0);// server start time
	writeC(0x00);// 未知封包
	writeC(0x00);// 未知封包
	writeC(0x04);
	writeD(2097118658);
	// writeD(Opcodes.UPTIME);
	writeD((int) (System.currentTimeMillis() / 1000L));
	writeH(0x01);
	// writeD(150316700);
	// writeD(150204901);
	// writeD(150306700);
    }

    @Override
    public byte[] getContent() {
	return getBytes();
    }

    @Override
    public String getType() {
	return S_SERVER_VERSION;
    }
}
