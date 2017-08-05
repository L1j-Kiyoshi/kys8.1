
package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;

public class S_GameStart extends ServerBasePacket {

    private static final String S_GameStart = "[S] S_GameStart";

    private byte[] _byte = null;

    public S_GameStart(L1PcInstance pc) {
        buildPacket1(pc);
    }

    //0000 : 7e 40 05 3c 0a ac b7 b6                            ~@.<....

    // 40 =起動すると、41 =時間、42 =リスト、43 =車輪の数、44 =ランキング、45 =オーバー、45 =エンド
    /*スタート*/
    private void buildPacket1(L1PcInstance pc) {
        writeC(Opcodes.S_EVENT);
        writeC(0x40);
        writeC(0x05); // 速度
        writeC(0);
        writeC(0);
        writeC(0);
        writeC(0);
        writeC(0); 

   /*     writeC(0x81); 
        writeC(0xfc); 
        writeC(0x7d); 
        writeC(0x6e); 
        writeC(0x11); */


    }

    @Override
    public byte[] getContent() {
        if (_byte == null) {
            _byte = getBytes();
        }
        return _byte;
    }

    public String getType() {
        return S_GameStart;
    }
}
