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

		if (name.length() == 0) {
			return false;
		}

		int numOfNameBytes = 0;
		try {
			numOfNameBytes = name.getBytes("SJIS").length;
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
