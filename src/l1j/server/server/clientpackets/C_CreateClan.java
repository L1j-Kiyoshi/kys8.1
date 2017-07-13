package l1j.server.server.clientpackets;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.BadNamesList;
import l1j.server.server.GameClient;
import l1j.server.server.datatables.ClanTable;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_ACTION_UI;
import l1j.server.server.serverpackets.S_ClanName;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_CreateClan extends ClientBasePacket {
	private static Logger _log = Logger.getLogger(C_CreateClan.class.getName());

	private static final String C_CREATE_CLAN = "[C] C_CreateClan";

	private static boolean isAlphaNumeric(String s) {
		boolean flag = true;
		char ac[] = s.toCharArray();
		int i = 0;
		do {
			if (i >= ac.length) {
				break;
			}
			if (!Character.isLetterOrDigit(ac[i])) {
				flag = false;
				break;
			}
			i++;
		} while (true);
		return flag;
	}

	private static boolean isInvalidName(String name) {

		for (int i = 0; i < name.length(); i++) {
			if (name.charAt(i) == 'ㄱ' || name.charAt(i) == 'ㄲ' || name.charAt(i) == 'ㄴ' || name.charAt(i) == 'ㄷ' || // 一文字（char）単位で比較。
					name.charAt(i) == 'ㄸ' || name.charAt(i) == 'ㄹ' || name.charAt(i) == 'ㅁ' || name.charAt(i) == 'ㅂ' || // 一文字（char）単位で比較
					name.charAt(i) == 'ㅃ' || name.charAt(i) == 'ㅅ' || name.charAt(i) == 'ㅆ' || name.charAt(i) == 'ㅇ' || // 一文字（char）単位で比較
					name.charAt(i) == 'ㅈ' || name.charAt(i) == 'ㅉ' || name.charAt(i) == 'ㅊ' || name.charAt(i) == 'ㅋ' || // 一文字（char）単位で比較。
					name.charAt(i) == 'ㅌ' || name.charAt(i) == 'ㅍ' || name.charAt(i) == 'ㅎ' || name.charAt(i) == 'ㅛ' || // 一文字（char）単位で比較。
					name.charAt(i) == 'ㅕ' || name.charAt(i) == 'ㅑ' || name.charAt(i) == 'ㅐ' || name.charAt(i) == 'ㅔ' || // 一文字（char）単位で比較。
					name.charAt(i) == 'ㅗ' || name.charAt(i) == 'ㅓ' || name.charAt(i) == 'ㅏ' || name.charAt(i) == 'ㅣ' || // 一文字（char）単位で比較。
					name.charAt(i) == 'ㅠ' || name.charAt(i) == 'ㅜ' || name.charAt(i) == 'ㅡ' || name.charAt(i) == 'ㅒ' || // 一文字（char）単位で比較。
					name.charAt(i) == 'ㅖ' || name.charAt(i) == 'ㅢ' || name.charAt(i) == 'ㅟ' || name.charAt(i) == 'ㅝ' || // 一文字（char）単位で比較。
					name.charAt(i) == 'ㅞ' || name.charAt(i) == 'ㅙ' || name.charAt(i) == 'ㅚ' || name.charAt(i) == 'ㅘ' || //一文字（char）単位で比較。
					name.charAt(i) == '씹' || name.charAt(i) == '좃' || name.charAt(i) == '좆' || name.charAt(i) == 'ㅤ') {
				return false;
			}
		}

		if (name.length() == 0) {
			return false;
		}

		int numOfNameBytes = 0;
		try {
			numOfNameBytes = name.getBytes("MS949").length;
		} catch (UnsupportedEncodingException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			return false;
		}

		if (isAlphaNumeric(name)) {
			return false;
		}

		// XXX  - 本庁の仕様と同等未確認
		// 全角文字が5文字を超えるか、全体に12バイトを超えると無効な名前である
		if (5 < (numOfNameBytes - name.length()) || 12 < numOfNameBytes) {
			return false;
		}

		if (BadNamesList.getInstance().isBadName(name)) {
			return false;
		}
		return true;
	}

	public C_CreateClan(byte abyte0[], GameClient clientthread) throws Exception {
		super(abyte0);
		String s = readS();

		L1PcInstance l1pcinstance = clientthread.getActiveChar();

		if (l1pcinstance == null)
			return;
		if (isInvalidName(s)) {
			l1pcinstance.sendPackets(new S_SystemMessage("誤った血盟の名前です。"));
			return;
		}

		if (l1pcinstance.isCrown()) { // プリンスやプリンセス
			// if (l1pcinstance.getClanid() == 0) {
			if (l1pcinstance.getClanid() == 0 && l1pcinstance.getLevel() >= 55) { //レベル60以上の君主のみ創設可能
				if (!l1pcinstance.getInventory().checkItem(40308, 30000)) {
					l1pcinstance.sendPackets(new S_ServerMessage(337, "$4")); // \f1％0が不足します。
					return;
				}
				for (L1Clan clan : L1World.getInstance().getAllClans()) { // \f1 同じ名前の血盟が存在します。
					if (clan.getClanName().toLowerCase().equals(s.toLowerCase())) {
						l1pcinstance.sendPackets(new S_ServerMessage(99)); // \f1 同じ名前の血盟が存在します。
						return;
					}
				}
				L1Clan clan = ClanTable.getInstance().createClan(l1pcinstance, s); // クラン創設
				l1pcinstance.getInventory().consumeItem(L1ItemId.ADENA, 30000);
				if (clan != null) {
					l1pcinstance.sendPackets(new S_ServerMessage(84, s)); // \f1%0 血盟が創設されました。
					l1pcinstance.sendPackets(new S_ClanName(l1pcinstance, clan.getEmblemId(), l1pcinstance.getClanRank()));	
					l1pcinstance.sendPackets(new S_ACTION_UI(clan.getClanName(), l1pcinstance.getClanRank()));
					l1pcinstance.sendPackets(new S_PacketBox(S_PacketBox.PLEDGE_EMBLEM_STATUS, l1pcinstance.getClan().getEmblemStatus()));
					//l1pcinstance.sendPackets(new S_ClanAttention());
					new L1Teleport().teleport(l1pcinstance, l1pcinstance.getX(), l1pcinstance.getY(), l1pcinstance.getMapId(), l1pcinstance.getHeading(), false);
				}
			} else {
				// l1pcinstance.sendPackets(new S_ServerMessage(86)); // \f1すでに血盟が結成されているので作成できません。
				l1pcinstance.sendPackets(new S_SystemMessage("すでに血盟があるかレベル55以上万創設することができます。")); //追加
			}
		} else {
			l1pcinstance.sendPackets(new S_ServerMessage(85)); // \f1プリンスとプリンセスだけが血盟を創設することができます。
		}
	}

	@Override
	public String getType() {
		return C_CREATE_CLAN;
	}

}
