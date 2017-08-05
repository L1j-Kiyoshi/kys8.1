/**
 * License
 * THE WORK (AS DEFINED BELOW) IS PROVIDED UNDER THE TERMS OF THIS
 * CREATIVE COMMONS PUBLIC LICENSE ("CCPL" OR "LICENSE").
 * THE WORK IS PROTECTED BY COPYRIGHT AND/OR OTHER APPLICABLE LAW.
 * ANY USE OF THE WORK OTHER THAN AS AUTHORIZED UNDER THIS LICENSE OR
 * COPYRIGHT LAW IS PROHIBITED.
 * <p>
 * BY EXERCISING ANY RIGHTS TO THE WORK PROVIDED HERE, YOU ACCEPT AND
 * AGREE TO BE BOUND BY THE TERMS OF THIS LICENSE. TO THE EXTENT THIS LICENSE
 * MAY BE CONSIDERED TO BE A CONTRACT, THE LICENSOR GRANTS YOU THE RIGHTS CONTAINED
 * HERE IN CONSIDERATION OF YOUR ACCEPTANCE OF SUCH TERMS AND CONDITIONS.
 */
package l1j.server.server.serverpackets;

import java.util.ArrayList;

import l1j.server.server.Opcodes;

public class S_ClanAttention extends ServerBasePacket {
    private static final String S_ClanAttention = "[S] S_ClanAttention";


    public S_ClanAttention() {
        writeC(Opcodes.S_PLEDGE_WATCH);
        writeD(2);
    }

    public S_ClanAttention(int i) {
        writeC(Opcodes.S_PLEDGE_WATCH);
        writeH(i);
    }

    /**
     * コメント
     */
    public S_ClanAttention(boolean onoff, String clanname) {
        writeC(Opcodes.S_PLEDGE_WATCH);
        writeC(onoff ? 32 : 31);
        writeH(269);
        writeS(clanname);
    }

    public S_ClanAttention(int count, ArrayList<String> attentionList) {
        writeC(Opcodes.S_PLEDGE_WATCH);
        writeH(2);
        writeD(count);
        for (String name : attentionList) {
            writeS(name);
        }
    }

    public S_ClanAttention(String name) {
        writeC(Opcodes.S_PLEDGE_WATCH);
        writeH(2);
        writeD(1);
        writeS(name);

    }


    @Override
    public byte[] getContent() {
        return getBytes();
    }

    @Override
    public String getType() {
        return S_ClanAttention;
    }
}
