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

package l1j.server.server.clientpackets;

import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.server.GameClient;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_CharTitle;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_Title extends ClientBasePacket {

    public static final int CLAN_RANK_GUARDIAN = 9;// 守護変更
    public static final int CLAN_RANK_SUBLEADER = 3; // 部君主追加
    
	private static final String C_TITLE = "[C] C_Title";
	private static Logger _log = Logger.getLogger(C_Title.class.getName());

	public C_Title(byte abyte0[], GameClient clientthread) {
		super(abyte0);
		L1PcInstance pc = clientthread.getActiveChar();
		if (pc == null) {
			return;
		}
		String charName = readS();
		String title = readS();
		if (title.length() > 16) {
			pc.sendPackets(new S_SystemMessage("呼称として使うことができる文字数を超えています。"));
			return;
		}

		if (charName.isEmpty() || title.isEmpty()) {
			// \f1 次のように入力してください：「/ title \\ f0キャラクター名呼称\\ f1」
			pc.sendPackets(new S_ServerMessage(196));
			return;
		}

		L1PcInstance target = L1World.getInstance().getPlayer(charName);
		if (target == null) {
			return;
		}

		if (pc.isGm()) {
			changeTitle(target, title);
			return;
		}

		if (isClanLeader(pc)||(pc.getClanid()==target.getClanid()&&
			(pc.getClanRank()==L1Clan.수호)||(pc.getClanRank()==L1Clan.부군주))) { // 血盟主
			if (pc.getId() == target.getId()) { // 自分
				if (pc.getLevel() < 10) {
					// \f1血盟員の場合には、呼称を持つためにはレベル10以上でなければなりません。
					pc.sendPackets(new S_ServerMessage(197));
					return;
				}
				changeTitle(pc, title);
			} else {
				if (pc.getClanid() != target.getClanid()) {
					// \f1血盟員でなければ他人に呼称を与えることができません。
					pc.sendPackets(new S_ServerMessage(199));
					return;
				}
				if (target.getLevel() < 10) {
					// \f1%0のレベルが10未満であるため、呼称を与えることができません。
					pc.sendPackets(new S_ServerMessage(202, charName));
					return;
				}
				changeTitle(target, title);
				L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
				if (clan != null) {
					for (L1PcInstance clanPc : clan.getOnlineClanMember()) {
						// \f1%0この％1に「％2という呼称を与えました。
						clanPc.sendPackets(new S_ServerMessage(203, pc
								.getName(), charName, title));
					}
				}
			}
			///////////血盟リニューアル//////////////
		} else if (pc.getClanRank() == 6||pc.getClanRank() == 3){
			if (pc.getId() == target.getId()) { // 自分
				if (pc.getLevel() < 10) {
					// \f1血盟員の場合には、呼称を持つためにはレベル10以上でなければなりません。
					pc.sendPackets(new S_ServerMessage(197));
					return;
				}
				changeTitle(pc, title);
			} else { 
				if (pc.getClanid() != target.getClanid()) {
					// \f1血盟員でなければ他人に呼称を与えることができません。
					pc.sendPackets(new S_ServerMessage(199));
					return;
				}
				if (target.getLevel() < 10) {
					// \f1%0のレベルが10未満であるため、呼称を与えることができません。
					pc.sendPackets(new S_ServerMessage(202, charName));
					return;
				}
				changeTitle(target, title);
				L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
				if (clan != null) {
					for (L1PcInstance clanPc : clan.getOnlineClanMember()) {
						// \f1%0この％1に「％2という呼称を与えました。
						clanPc.sendPackets(new S_ServerMessage(203, pc.getName(), charName, title));
					}
				}
			}
			///////////血盟リニューアル//////////////
		} else {
			if (pc.getId() == target.getId()) { // 自分
				if (pc.getClanid() != 0 && !Config.CHANGE_TITLE_BY_ONESELF) {
					// \f1血盟員に呼称が与えられるのは、王子とプリンセスだけです。
					pc.sendPackets(new S_ServerMessage(198));
					return;
				}
				if (target.getLevel() < 40) {
					// \f1血盟員ではないのに呼称を持つためには、レベル40以上でなければなりません。
					pc.sendPackets(new S_SystemMessage("初心者がないのに呼称を持つためには、レベル40以上でなければなりません。")); 
					return;
				}
				changeTitle(pc, title);
			} else { // 他人
				if (pc.isCrown()) { // 連合に所属した君主
					if (pc.getClanid() == target.getClanid()) {
						// \f1%0はあなたの血盟がありません。
						pc.sendPackets(new S_ServerMessage(201, pc
								.getClanname()));
						return;
					}
				}
			}
		}
	}

	private void changeTitle(L1PcInstance pc, String title) {
		int objectId = pc.getId();
		pc.setTitle(title);
		pc.sendPackets(new S_CharTitle(objectId, title));
		Broadcaster.broadcastPacket(pc, new S_CharTitle(objectId, title));
		try {
			pc.save(); // DB에 캐릭터 정보를 써 우
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}

	private boolean isClanLeader(L1PcInstance pc) {
		boolean isClanLeader = false;
		if (pc.getClanid() != 0) { // クランに所属
			L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
			if (clan != null) {
				if (pc.isCrown() && pc.getId() == clan.getLeaderId()) { // 君主、
					//一方、
					// 血盟主
					isClanLeader = true;
				}
			}
		}
		return isClanLeader;
	}


	@Override
	public String getType() {
		return C_TITLE;
	}

}