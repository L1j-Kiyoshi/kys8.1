package l1j.server.server.serverpackets;

import l1j.server.Config;
import l1j.server.server.Opcodes;

public class S_ServerVersion extends ServerBasePacket {
    private static final String S_SERVER_VERSION = "[S] ServerVersion";
    private final int UPTIME = (int) (System.currentTimeMillis() / 1000L);

    public S_ServerVersion() {
	// 8.xc?
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

	// 7.6c
	// writeC(Opcodes.S_VERSION_CHECK);
	// writeC(0x00);
	// writeC(0x01);// 第幾個伺服器
	// writeD(150924202);
	// writeD(150912681);
	// writeD(2013122601);
	// writeD(150924201);
	// writeD(0x0);// server start time
	// writeC(0x00);// 未知封包
	// writeC(0x00);// 未知封包
	// writeC(0x04);
	// writeD(2097118658);
	// // writeD(Opcodes.UPTIME);
	// writeD((int) (System.currentTimeMillis() / 1000L));
	// writeH(0x01);
	// // writeD(150316700);
	// // writeD(150204901);
	// // writeD(150306700);

	// 8.1c
	writeC(Opcodes.S_VERSION_CHECK);
	writeC(0);
	writeC(118);
	writeD(161006201);
	writeD(161006201);
	writeD(2015090301);
	writeD(161006201);
	writeD(0x0);
	writeC(0);
	writeC(0);
	writeC(Config.CLIENT_LANGUAGE);
	writeD(2097118658);
	writeD((int) (System.currentTimeMillis() / 1000L));
	writeD(150316700);
	writeD(150204901);
	writeD(151118701);
	writeD(160905701);
	writeD(160922701);

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
