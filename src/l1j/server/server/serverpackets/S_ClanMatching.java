/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.serverpackets;

import java.util.ArrayList;
import java.util.List;

import l1j.server.server.Opcodes;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1ClanMatching;
import l1j.server.server.model.L1ClanMatching.ClanMatchingList;
import l1j.server.server.model.L1War;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;

public class S_ClanMatching extends ServerBasePacket {
    private static final String S_ClanMatching = "[C] S_ClanMatching";

    /**
     * type
     * 0：登録、修正、完了」
     * 1：登録解除、君主のみ
     * 2：推奨血盟、リフレッシュ「完了」
     * 3：適用リスト、更新
     * 4：要求リスト、更新
     * 5：適用する。 8c bb 84 10
     * 6：適用を取り消します。
     */
    public S_ClanMatching(L1PcInstance pc, int type, int objid, String text1, int htype) {
        L1Clan clan = null;
        L1ClanMatching cml = L1ClanMatching.getInstance();
        String clanname = null;
        String text = null;

        writeC(Opcodes.S_MATCH_MAKING);
        writeC(type);
        if (type == 2) { // おすすめ血盟
            ArrayList<ClanMatchingList> _list = new ArrayList<ClanMatchingList>();
            String result = null;
            for (int i = 0; i < cml.getMatchingList().size(); i++) {
                result = cml.getMatchingList().get(i)._clanname;
                if (!pc.getCMAList().contains(result)) {
                    _list.add(cml.getMatchingList().get(i));
                }
            }
            int type2 = 0;
            int size = _list.size();
            writeC(0x00);
            writeC(size); // 本数。
            for (int i = 0; i < size; i++) {
                clanname = _list.get(i)._clanname;
                text = _list.get(i)._text;
                type2 = _list.get(i)._type;
                clan = L1World.getInstance().getClan(clanname);
                writeD(clan.getClanId()); // ヒョルマク
                writeS(clan.getClanName()); // 血盟の名前。
                writeS(clan.getLeaderName()); // 君主の名前
                writeD(clan.getOnlineMaxUser()); // 血盟の規模：週間最大接続者数

                writeC(type2); // 0：ハンティング、1：戦い、2：親睦

                if (clan.getHouseId() != 0) writeC(0x01); // アジト 0: X , 1: O
                else writeC(0x00);

                boolean inWar = false;
                List<L1War> warList = L1World.getInstance().getWarList(); // 戦争のリストを取得
                for (L1War war : warList) {
                    if (war.CheckClanInWar(clanname)) { //ジャックとは、すでに戦争中
                        inWar = true;
                        break;
                    }
                }

                if (inWar) writeC(0x01); // 戦争状態0：X、1：O
                else writeC(0x00);
                writeC(0x00); // 固定値。
                writeS(text);// 紹介コメント。
                writeD(clan.getClanId()); // 血盟objid
            }
            _list.clear();
            _list = null;
        } else if (type == 3) { // 申し込みリスト
            int size = pc.getCMAList().size();
            int type2 = 0;
            writeC(0x00);
            writeC(size); // 本数。

            for (int i = 0; i < size; i++) {
                clanname = pc.getCMAList().get(i);
                text = cml.getClanMatchingList(clanname)._text;
                type2 = cml.getClanMatchingList(clanname)._type;
                clan = L1World.getInstance().getClan(clanname);
                writeD(clan.getClanId()); // 削除クリックすると、浮きobj値
                writeC(0x00);
                writeD(clan.getClanId()); // ヒョルマク。
                writeS(clan.getClanName()); // 血盟の名前。
                writeS(clan.getLeaderName()); // 君主の名前
                writeD(clan.getOnlineMaxUser());// 血盟の規模：週間最大接続者数
                writeC(type2); // 0: 狩猟、1：戦い、2：親睦

                if (clan.getHouseId() != 0) writeC(0x01); // アジト0：X、1：O
                else writeC(0x00);

                boolean inWar = false;
                List<L1War> warList = L1World.getInstance().getWarList(); // 戦争のリストを取得
                for (L1War war : warList) {
                    if (war.CheckClanInWar(clanname)) { // ジャックとは、すでに戦争中
                        inWar = true;
                        break;
                    }
                }

                if (inWar) writeC(0x01); // 戦争状態0：X、1：O
                else writeC(0x00);
                writeC(0x00); // 固定値。
                writeS(text);// 紹介コメント。
                writeD(clan.getClanId()); // 血盟objid
            }
        } else if (type == 4) { // リクエストリスト

            if (!cml.isClanMatchingList(pc.getClanname())) {
                writeC(0x82); // リクエストリストが存在しない時はこれだけ飛ばす。
            } else {
                int size = pc.getCMAList().size();
                String username = null;
                writeC(0x00);
                writeC(0x02);
                writeC(0x00);// 固定
                writeC(size); // size
                L1PcInstance user = null;
                for (int i = 0; i < size; i++) {
                    username = pc.getCMAList().get(i);
                    user = L1World.getInstance().getPlayer(username);
                    if (user == null) {
                        try {
                            user = CharacterTable.getInstance().restoreCharacter(username);
                            if (user == null) {
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    writeD(user.getId()); // 申請者の objectid
                    writeC(0x00);
                    writeC(user.getOnlineStatus()); // 0x01：接続、0x00：非接続
                    writeS(username); // 申請者の名前。
                    writeC(user.getType()); // 文字クラス
                    writeH(user.getLawful()); // ラウフル
                    writeC(user.getLevel()); // レベル
                    writeC(0x01); // 名前の前に出てくる葉の変更
                }
            }
        } else if (type == 5 || type == 6) {
            writeC(0x00);
            writeD(objid);
            writeC(htype);
        }
    }

    @Override
    public byte[] getContent() {
        return getBytes();
    }

    @Override
    public String getType() {
        return S_ClanMatching;
    }
}
