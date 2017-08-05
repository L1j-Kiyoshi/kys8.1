package l1j.server.server.serverpackets;

public class ServerMessage {
    public final static int DUPLICATED_IP_CONNECTION = 357;

    public final static int CANNOT_DROP_OR_TRADE = 210; // \f1%0はしまったり、または他人に

    // 譲渡することはできません。

    public final static int HAVING_NEST_OF_CLAN = 665; // \f1性やアジトを所有した状態では、

    // 血盟を解散することができません。

    public final static int CANNOT_BREAK_CLAN = 302; // \f1解散することができません。

    public final static int CANNOT_BREAK_CLAN_HAVING_FRIENDS = 1235; // 同盟が

    // ある場合
    // 血盟を
    // 解散することができ
    // ありません。
    public final static int CANNOT_WAR_FROM_ALLIANCECLAN = 1205; // 同盟血盟とは、戦争をすることはできません。

    public final static int LEAVE_CLAN = 178;// \f1%0%s %1 血盟を脱退しました。
}
