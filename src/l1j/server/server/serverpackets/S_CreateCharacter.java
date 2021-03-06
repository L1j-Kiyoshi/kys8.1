package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;

//by.lins
public class S_CreateCharacter extends ServerBasePacket {
    public static final String S_CREATE_CHARACTER = "[S] S_LoginResult";
    public static final int CHARACTER_OFF_DISPLAY = 51;
    public static final int CHARACTER_OFF_DISPLAY_SUCCESS = 22;

    private byte[] _byte = null;

    public S_CreateCharacter() {
        buildPacket();
    }

    private void buildPacket() {
        writeC(Opcodes.S_VOICE_CHAT);
        writeC(63);
        writeC(1);
    }

    public byte[] getContent() {
        if (this._byte == null) {
            this._byte = getBytes();
        }
        return this._byte;
    }

    public String getType() {
        return "[S] S_LoginResult";
    }
}