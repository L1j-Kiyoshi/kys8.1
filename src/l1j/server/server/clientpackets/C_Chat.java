package l1j.server.server.clientpackets;

import l1j.server.Config;
import l1j.server.server.GameClient;
import l1j.server.server.datatables.SpamTable;
import l1j.server.server.model.L1ExcludingList;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_NewChat;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;

public class C_Chat extends ClientBasePacket {

    private static final String C_CHAT = "[C] C_Chat";

    public static final int MACRO = 0x0c;

    public C_Chat(byte abyte0[], GameClient clientthread) {
        super(abyte0);
        if (clientthread == null)
            return;

        readC();
        L1PcInstance pc = clientthread.getActiveChar();

        if (pc == null)
            return;

        chatWorld(pc, readS());
    }

    private void chatWorld(L1PcInstance pc, String chatText) {
        if (pc.getLevel() >= Config.GLOBAL_CHAT_LEVEL) {

            if (pc.isGm() || L1World.getInstance().isWorldChatElabled()) {
                if (pc.get_food() >= 12) { // 5％ラゲッジ？
                    S_PacketBox pb = new S_PacketBox(S_PacketBox.FOOD, pc.get_food());
                    pc.sendPackets(pb, true);
                    S_PacketBox pb2 = new S_PacketBox(S_PacketBox.FOOD, pc.get_food());
                    pc.sendPackets(pb2, true);
                    if (pc.isGm()) {
                        L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\f=[******] " + chatText));
                        L1World.getInstance().broadcastPacketToAll(new S_NewChat(pc, 4, 3, chatText, "[******] "));
                        return;
                    }

                    for (L1PcInstance listner : L1World.getInstance().getAllPlayers()) {
                        L1ExcludingList spamList15 = SpamTable.getInstance().getExcludeTable(listner.getId());
                        if (!spamList15.contains(0, pc.getName())) {
                            S_NewChat cp = new S_NewChat(pc, 4, 12, chatText, ""); //商売チャット
                            //S_NewChat cp = new S_NewChat(pc, 4, 3, chatText, ""); //一般的なチャット
                            listner.sendPackets(cp, true);
                        }
                    }
                } else {
                    S_ServerMessage sm = new S_ServerMessage(462);
                    pc.sendPackets(sm, true);
                }
            } else {
                S_ServerMessage sm = new S_ServerMessage(510);
                pc.sendPackets(sm, true);
            }
        } else {
            S_ServerMessage sm = new S_ServerMessage(195,
                    String.valueOf(Config.GLOBAL_CHAT_LEVEL));
            pc.sendPackets(sm, true);
        }
    }

    @Override
    public String getType() {
        return C_CHAT;
    }
}
