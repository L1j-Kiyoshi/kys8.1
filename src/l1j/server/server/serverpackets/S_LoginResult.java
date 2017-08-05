package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;

public class S_LoginResult extends ServerBasePacket {
    public static final String S_LOGIN_RESULT = "[S] S_LoginResult";

    public static final int REASON_LOGIN_OK = 0x00; // 0x33

    public static final int REASON_ACCOUNT_IN_USE = 0x16;

    public static final int REASON_ACCOUNT_ALREADY_EXISTS = 0x26;

    public static final int REASON_ACCESS_FAILED = 0x08;

    public static final int REASON_USER_OR_PASS_WRONG = 0x08;

    public static final int REASON_BUG_WRONG = 0x39;

    public static final int REASON_WRONG_ACCOUNT = 0x09;
    public static final int REASON_WRONG_PASSWORD = 0x0A;

    // 2B盗用申告
    //	06-のようなキャラクターが既にある9-名前が間違って24-ipジョンリャンジェ26-仮想ip複数接続
    //	28-非番変更しろ29-質問の答え31-口座振替32時間残りがない34-この文字の使用が禁止さ
    //	35-ゲーム内非番変更不可36ユーロの問題に停止37-盗用申告38-バグ使用バン39-県取引バン

    // public static int REASON_SYSTEM_ERROR = 0x01;

    private byte[] _byte = null;

    public S_LoginResult(int reason) {
        buildPacket(reason);
    }

    private void buildPacket(int reason) {
        writeC(Opcodes.S_LOGIN_CHECK);
        writeC(reason);
        writeD(0x00000000);
        writeD(0x00000000);
        writeD(0x00000000);
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
        return S_LOGIN_RESULT;
    }
}
