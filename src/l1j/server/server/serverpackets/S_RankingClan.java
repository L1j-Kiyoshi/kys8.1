package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;
import l1j.server.server.model.L1ClanRanking.RankData;

public class S_RankingClan extends ServerBasePacket {

    private static final String S_RankingClan = "[S] S_RankingClan";

    private byte[] _byte = null;

    /*
     * @param Serverbasepacket
     * 修正補完：プリソプの関係で解散血盟は追加しません。
     */
    public S_RankingClan(RankData[] datas) {
        writeC(Opcodes.S_EXTENDED_PROTOBUF);
        writeH(147);
        for (int i = 0; i < datas.length; i++) {
            writeC(0x0a);// 着丈
            int currentRank = datas[i].getCurrentR();
            int oldRank = datas[i].getPastR();
            int clanId = datas[i].getClanid();
            String name = datas[i].getClanName();
            int time = datas[i].getComTime();
            int data = (int) (datas[i].getDate().getTime() / 1000L);
            int size = 9 + bitlengh(currentRank) + bitlengh(oldRank) + bitlengh(clanId) + name.getBytes().length + bitlengh(time) + bitlengh(data);
            writeC(size);
            writeC(0x08);// 現在ランキング
            writeBit(currentRank);
            writeC(0x10);// 過去のランキング
            writeBit(oldRank);
            writeC(0x18);// 血盟名
            writeBit(clanId);
            writeC(0x22);// 血盟の名前
            writeS2(name);
            writeC(0x28);//0：活動1：解散
            writeC(0);
            writeC(0x30);// クリア時間（単位：秒）
            writeBit(time);
            writeC(0x38);// クリア日
            writeBit(data);
        }
        writeH(0);
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
        return S_RankingClan;
    }
}