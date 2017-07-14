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

import l1j.server.server.GameClient;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1ClanJoin;
import l1j.server.server.model.L1ClanMatching;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ClanMatching;
import l1j.server.server.serverpackets.S_SystemMessage;


// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_ClanMatching extends ClientBasePacket {

	private static final String C_CLANMATCHING = "[C] C_ClanMatching";

	//private static Logger _log = Logger.getLogger(C_ClanMatching.class.getName());

	public C_ClanMatching(byte decrypt[], GameClient client) throws Exception {
		super(decrypt);
		L1PcInstance pc = client.getActiveChar();
		if (pc == null) {
			return;
		}
		/**
		 * type
		 * 0: 登録、修正、完了」 
		 * 1:登録解除、君主のみ「完了」
		 * 2: おすすめ血盟、リフレッシュ「完了」 
		 * 3: 申し込みリスト、更新[完了]
		 * 4: 欲しい物のリスト、更新[完了]
		 * 5: 適用する。 clanobjid「完了」
		 * 6: type 1：承認、2：拒絶、3：削除[完了]
		 */
		int type = readC();
		int objid = 0;
		String text = null;
		int htype = 0;
		if (type == 0) {
			L1ClanMatching cml = L1ClanMatching.getInstance();
			htype = readC();
			text = readS();
			if (!cml.isClanMatchingList(pc.getClanname())) {
				cml.writeClanMatching(pc.getClanname(), text, htype);
			} else {
				cml.updateClanMatching(pc.getClanname(), text, htype);
			}

		} else if (type == 1) {
			L1ClanMatching cml = L1ClanMatching.getInstance();
			if (cml.isClanMatchingList(pc.getClanname())) {
				cml.deleteClanMatching(pc);
			}
		} else if (type == 4) {
			L1ClanMatching cml = L1ClanMatching.getInstance();
			if (pc.getClanid() == 0) {
				if (!pc.isCrown()) {
					cml.loadClanMatchingApcList_User(pc);
				}
			} else {
				switch (pc.getClanRank()) {
					case 4:	case 6: case 9:case 10: // 部君主、血盟君主、守護騎士
						cml.loadClanMatchingApcList_Crown(pc);
					break; 
				}
			}
		
		} else if (type == 5) {
			objid = readD();
			L1Clan clan = getClan(objid);
			if (clan != null && !pc.getCMAList().contains(clan.getClanName())) {
				L1ClanMatching cml = L1ClanMatching.getInstance();
				cml.writeClanMatchingApcList_User(pc, clan);
			}
		} else if (type == 6) {
			objid = readD();
			htype = readC(); // 1: 承認、2：拒絶、3：削除
			L1ClanMatching cml = L1ClanMatching.getInstance();
			if (htype == 1) {
				L1Object target = L1World.getInstance().findObject(objid);
				if (target != null & target instanceof L1PcInstance) {
					L1PcInstance user = (L1PcInstance) target;
					if (!pc.getCMAList().contains(user.getName())) {
						pc.sendPackets(new S_SystemMessage("申請をキャンセルしたユーザーです。"));
					} else {
						if (L1ClanJoin.getInstance().ClanJoin(pc, user)) {
							cml.deleteClanMatchingApcList(user);
						}
					}
				} else if (target == null) {
					pc.sendPackets(new S_SystemMessage("非接続中のユーザです。"));
				}
			} else if (htype == 2) {
				L1Object target = L1World.getInstance().findObject(objid);
				if (target != null) {
					if (target instanceof L1PcInstance) {
						L1PcInstance user = (L1PcInstance) target;
						user.removeCMAList(pc.getName());
						pc.removeCMAList(user.getName());
						cml.deleteClanMatchingApcList(user, user.getId(), pc.getClan());
					}
				} else {
					cml.deleteClanMatchingApcList(null, objid, pc.getClan());
				}
			} else if (htype == 3) {
				L1Clan clan = getClan(objid);
				if (clan != null && pc.getCMAList().contains(clan.getClanName())) {
					cml.deleteClanMatchingApcList(pc, clan);
				}
			}
		}
		pc.sendPackets(new S_ClanMatching(pc, type, objid, text, htype));
	}

	private L1Clan getClan(int objid) {
		L1Clan clan = null;
		for (L1Clan c : L1World.getInstance().getAllClans()) {
			if (c.getClanId() == objid) {
				clan = c;
				break;
			}
		}
		return clan;
	}


	@Override
	public String getType() {
		return C_CLANMATCHING;
	}

}
