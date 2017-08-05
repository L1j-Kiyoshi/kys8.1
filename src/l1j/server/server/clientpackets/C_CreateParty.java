package l1j.server.server.clientpackets;

import l1j.server.server.GameClient;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_ServerMessage;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_CreateParty extends ClientBasePacket {

    private static final String C_CREATE_PARTY = "[C] C_CreateParty";

    public C_CreateParty(byte decrypt[], GameClient client) throws Exception {
        super(decrypt);

        L1PcInstance pc = client.getActiveChar();
        if (pc == null) return;

        int type = readC();
        if (type == 0 || type == 1 || type == 4 || type == 5) {// 0。一般的な1分配
            int targetId = 0;
            L1Object temp = null;
            if (type == 4 || type == 5) {
                String name = readS();
                L1PcInstance tar = L1World.getInstance().getPlayer(name);
                if (tar == null) return;
                temp = tar;
                targetId = tar.getId();
            } else {
                targetId = readD();
                temp = L1World.getInstance().findObject(targetId);
            }
            if (temp instanceof L1PcInstance) {
                L1PcInstance targetPc = (L1PcInstance) temp;
                if (pc.getId() == targetPc.getId()) {
                    return;
                }
                if (targetPc.isInParty()) {
                    // すでに他のパーティーに所属しているため招待することができません
                    pc.sendPackets(new S_ServerMessage(415));
                    return;
                }
                if (pc.isInParty()) {
                    if (pc.getParty().isLeader(pc)) {
                        targetPc.setPartyID(pc.getId());
                        // \f2%0\f>%sから\\ fUパーティー\\ f>に招待されました。うんですか？ （Y / N）
                        targetPc
                                .sendPackets(new S_Message_YN(953, pc.getName()));
                    } else {
                        // パーティーのリーダーだけ招待することができます。
                        pc.sendPackets(new S_ServerMessage(416));
                    }
                } else {
                    targetPc.setPartyID(pc.getId());
                    switch (type) {
                        case 4:
                        case 0:
                            pc.setPartyType(0);
                            // \f2%0\f>%sから\\ fUパーティー\\ f>に招待されました。うんですか？ （Y / N）
                            targetPc.sendPackets(new S_Message_YN(953, pc.getName()));
                            break;
                        case 5:
                        case 1:
                            pc.setPartyType(1);
                            // \f2%0\f>%s \fU自動分配パーティー\\ f>招待しました。許可しますか？ （Y / N）
                            targetPc.sendPackets(new S_Message_YN(954, pc.getName()));
                            break;
                    }
                }
            }
        } else if (type == 2) { // チャットパーティー
            String name = readS();
            L1PcInstance targetPc = L1World.getInstance().getPlayer(name);
            if (targetPc == null) {
                // %0という名前の人はありません。
                pc.sendPackets(new S_ServerMessage(109));
                return;
            }
            if (pc.getId() == targetPc.getId()) {
                return;
            }
            if (targetPc.isInChatParty()) {
                // すでに他のパーティーに所属しているため招待することができません
                pc.sendPackets(new S_ServerMessage(415));
                return;
            }

            if (pc.isInChatParty()) {
                if (pc.getChatParty().isLeader(pc)) {
                    targetPc.setPartyID(pc.getId());
                    // \f2%0\f>%sから\\ fUチャットパーティー\\ f>に招待されました。うんですか？ （Y / N）
                    targetPc.sendPackets(new S_Message_YN(951, pc.getName()));
                } else {
                    // パーティーのリーダーだけ招待することができます。
                    pc.sendPackets(new S_ServerMessage(416));
                }
            } else {
                targetPc.setPartyID(pc.getId());
                // \f2%0\f>%sから\\ fUチャットパーティー\\ f>に招待されました。うんですか？ （Y / N）
                targetPc.sendPackets(new S_Message_YN(951, pc.getName()));
            }
        } else if (type == 3) {
            int targetId = readD();
            L1Object temp = L1World.getInstance().findObject(targetId);
            if (temp instanceof L1PcInstance) {
                L1PcInstance targetPc = (L1PcInstance) temp;
                if (pc.getId() == targetPc.getId()) {
                    return;
                }

                if (pc.isInParty()) {
                    if (targetPc.isInParty()) {
                        if (pc.getParty().isLeader(pc)) {
                            if (pc.getLocation().getTileLineDistance(targetPc.getLocation()) < 16) {
                                pc.getParty().passLeader(targetPc);
                            } else {
                                // パーティーを委任させる仲間が近くにありません
                                pc.sendPackets(new S_ServerMessage(1695));
                            }


                        } else {
                            // パーティーリーダーがなくて権限を行使することができません
                            pc.sendPackets(new S_ServerMessage(1697));
                        }
                    } else {
                        //現在のパーティーのメンバーではない
                        pc.sendPackets(new S_ServerMessage(1696));
                    }
                }
            }
        }
    }

    @Override
    public String getType() {
        return C_CREATE_PARTY;
    }

}
