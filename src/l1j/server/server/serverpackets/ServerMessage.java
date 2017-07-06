package l1j.server.server.serverpackets;

public class ServerMessage {
	public final static int DUPLICATED_IP_CONNECTION = 357;

	public final static int CANNOT_DROP_OR_TRADE = 210; // \f1%0은 버리거나 또는 타인에게

	// 양도 할 수 없습니다.

	public final static int HAVING_NEST_OF_CLAN = 665; // \f1성이나 아지트를 소유한 상태에서는

	// 혈맹을 해산할 수 없습니다.

	public final static int CANNOT_BREAK_CLAN = 302; // \f1해산할 수 없습니다.

	public final static int CANNOT_BREAK_CLAN_HAVING_FRIENDS = 1235; // 동맹이

	// 있는 경우
	// 혈맹을
	// 해산할 수
	// 없습니다.
	public final static int CANNOT_WAR_FROM_ALLIANCECLAN = 1205; // 동맹혈맹과는 전쟁을 할 수 없습니다.

	public final static int LEAVE_CLAN = 178;// \f1%0%s %1 혈맹을 탈퇴했습니다.
}
