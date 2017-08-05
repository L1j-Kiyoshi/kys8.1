
package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;

// 最初は
// S_SkillSound(7382):Aタイプ
// S_SkillSound(7383): Bタイプ送信する場合される。
// coin インベントリ画像：3565
public class S_Coma extends ServerBasePacket {

    // jが40時、A、41時B
    public S_Coma(int j, int time) {
        writeC(Opcodes.S_EVENT);
        writeC(0x14);
        for (int i = 0; i < 64; i++) writeC(0x00);
        writeC((int) (time + 16) / 32);
        writeC(j);
        writeC(0x14);
        writeD(0x00000000);
        writeC(0x00);
    }

    @Override
    public byte[] getContent() {
        return getBytes();
    }
}
