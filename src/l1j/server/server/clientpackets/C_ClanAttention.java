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
package l1j.server.server.clientpackets;

import java.io.File;

import l1j.server.server.GameClient;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ClanAttention;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;

//Referenced classes of package l1j.server.server.clientpackets:
//ClientBasePacket

/**
 * 本一族から来る処理クライアントパケット
 */
public class C_ClanAttention extends ClientBasePacket {
    private static final String C_PledgeRecommendation = "[C] C_PledgeRecommendation";

    public C_ClanAttention(byte[] decrypt, GameClient client) {
        super(decrypt);

        L1PcInstance pc = client.getActiveChar();
        if (pc == null) {
            return;
        }

        int data = readC();
        //0血盟の追加、1リストの削除、2血盟リスト

        //System.out.println("C_ClanAttention - readCタイプ：[ "+ data +"] "）;
        L1Clan targetClan = null;
        L1Clan clan = null;
        switch (data) {
            case 0: // 文章注視血盟追加
                /**
                 * 3348	血盟注視：％0血盟の文章注視を承諾しますか？
                 * 3323	血盟注視：％s血盟の文章注視を解除しますか？ （Y / N）
                 * 3324	血盟注視：文章注視不可、対象血盟との戦争状態
                 */

                String pcClanName = pc.getClanname();
                String targetClanName = readS();
                clan = L1World.getInstance().getClan(pcClanName);
                if (clan == null) { // ジャックとは、この検出されない
                    pc.sendPackets(new S_SystemMessage("\\aGまず、血盟を創設願います。"));
                    return;
                }

                if (pcClanName.toLowerCase().equals(targetClanName.toLowerCase())) { // ジャックとを指定
                    pc.sendPackets(new S_SystemMessage("\\aG自分のクランに注視することができません。"));
                    return;
                }

                for (int i = 0; i < clan.getGazeList().size(); i++) {
                    if (clan.getGazeList().get(i).toLowerCase().equals(targetClanName.toLowerCase())) {
                        pc.sendPackets(new S_SystemMessage("\\aGすでに相手血盟と注視をしています。"));
                        return;
                    }
                }

                if (clan.getGazeList().size() >= 5) {
                    pc.sendPackets(new S_SystemMessage("\\aG文章いただく最大5個の血盟のみ可能です。"));
                    return;
                }


                for (L1Clan checkClan : L1World.getInstance().getAllClans()) { //クラン名をチェック
                    if (checkClan.getClanName().toLowerCase().equals(targetClanName.toLowerCase())) {
                        targetClan = checkClan;
                        break;
                    }
                }

                if (targetClan == null) { // 相手クランが発見されなかった
                    pc.sendPackets(new S_SystemMessage("\\aG相手血盟が存在しません。"));
                    return;
                }

                File file = new File(System.getProperty("user.dir") + "/emblem/" + clan.getEmblemId());

                if (!file.exists()) {
                    pc.sendPackets(new S_SystemMessage("ヒョルマクなし文章注視を要求することはできません。"));
                    return;
                }

                file = new File(System.getProperty("user.dir") + "/emblem/" + targetClan.getEmblemId());
                if (!file.exists()) {
                    pc.sendPackets(new S_SystemMessage("相手血盟にヒョルマクがありません。"));
                    return;
                }

                L1PcInstance target = L1World.getInstance().getPlayer(targetClan.getLeaderName());
                if (target != null) {
                    pc.sendPackets(new S_SystemMessage("血盟注視：要求中です。お待ちください。"));
                    target.setTempID(pc.getId());
                    target.sendPackets(new S_Message_YN(3348, pc.getClanname()));// %0血盟の文章注視を承諾しますか？
                } else {
                    pc.sendPackets(new S_ServerMessage(3349));// 文章注視不可、ない血盟あるいは連合血盟または君主がオフライン
                }

                break;
            case 1: //文章注視削除
                // 3323血盟注視：％s血盟の文章注視を解除しますか？ （Y / N）
                String targetClanName2 = readS();
                if (!pc.isCrown()) {
                    pc.sendPackets(new S_SystemMessage("\\aG君主だけが文章注視を解除することができます。"));
                    return;
                }
                clan = L1World.getInstance().getClan(pc.getClanname());
                if (clan == null) { // ジャックとは、この検出されない
                    return;
                }

                for (L1Clan checkClan : L1World.getInstance().getAllClans()) { // クラン名をチェック
                    if (checkClan.getClanName().toLowerCase().equals(targetClanName2.toLowerCase())) {
                        targetClan = checkClan;
                        break;
                    }
                }

                if (targetClan == null) { // 相手クランが発見されなかった
                    pc.sendPackets(new S_SystemMessage("\\aG相手血盟が存在しません。"));
                    return;
                }

                //注視リストから削除
                clan.removeGazelist(targetClan.getClanName());
                targetClan.removeGazelist(clan.getClanName());

                //文章注視リストの更新
                for (L1PcInstance member : clan.getOnlineClanMember()) {
                    member.sendPackets(new S_ClanAttention(clan.getGazeSize(), clan.getGazeList()));
                }

                for (L1PcInstance member : targetClan.getOnlineClanMember()) {
                    member.sendPackets(new S_ClanAttention(targetClan.getGazeSize(), targetClan.getGazeList()));
                }


                break;
            case 2: // 文章注視血盟リスト

                break;
        }


    }

    @Override
    public String getType() {
        return C_PledgeRecommendation;
    }
}
