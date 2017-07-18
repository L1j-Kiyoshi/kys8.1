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

import java.util.List;

import l1j.server.Config;
import l1j.server.server.GameClient;
import l1j.server.server.Controller.WarTimeController;
import l1j.server.server.model.Getback;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1War;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_War extends ClientBasePacket {

	private static final String C_WAR = "[C] C_War";

	public C_War(byte abyte0[], GameClient clientthread) throws Exception {
		super(abyte0);
		int type = readC();
		String s = readS();

		L1PcInstance player = clientthread.getActiveChar();
		if ( player == null)return;
		String playerName = player.getName();
		String clanName = player.getClanname();
		int clanId = player.getClanid();

		if (!player.isCrown()) { // 君主以外
			player.sendPackets(new S_ServerMessage(478));// \f1プリンスとプリンセスだけ戦争を布告することができます。
			return;
		}
		if (clanId == 0) { // クラン笑顔の中
			player.sendPackets(new S_ServerMessage(272)); // \f1戦争するためには、まず血盟を創設しなければなりません。
			return;
		}
		L1Clan clan = L1World.getInstance().getClan(clanName);
		if (clan == null) {
			S_SystemMessage sm = new S_SystemMessage("対象血盟が見つかりません。");
			player.sendPackets(sm);
			sm = null;
			return;
		}

		if (player.getId() != clan.getLeaderId()) { // 血盟主
			player.sendPackets(new S_ServerMessage(478)); // \f1プリンスとプリンセスだけ戦争を布告することができます。
			return;
		}

		if (clanName.toLowerCase().equals(s.toLowerCase())) { // ジャックとを指定
			return;
		}

		L1Clan enemyClan = null;
		String enemyClanName = null;
		for (L1Clan checkClan : L1World.getInstance().getAllClans()) { // クラン名をチェック
			if (checkClan.getClanName().toLowerCase().equals(s.toLowerCase())) {
				enemyClan = checkClan;
				enemyClanName = checkClan.getClanName();
				break;
			}
		}
		if (enemyClan == null) {
			S_SystemMessage sm = new S_SystemMessage("対象血盟が見つかりません。");
			player.sendPackets(sm);
			sm = null;
			return;
		}

		boolean inWar = false;
		List<L1War> warList = L1World.getInstance().getWarList(); // 戦争のリストを取得
		for (L1War war : warList) {
			if (war.CheckClanInWar(clanName)) { //ジャックとは、すでに戦争中
				if (type == 0) { // 宣戦布告
					player.sendPackets(new S_ServerMessage(234)); // \f1あなたの血盟はすでに戦争中です。
					return;
				}
				inWar = true;
				break;
			}
		}
		if (!inWar && (type == 2 || type == 3)) { // ジャックとは、この戦争中以外で、降伏または終結
			return;
		}

		if (clan.getCastleId() != 0) { // ジャックとは城主
			if (type == 0) { // 宣戦布告
				player.sendPackets(new S_ServerMessage(474)); // あなたはすでに城を所有しているので、他の城をキャッチすることができません。
				return;
			} else if (type == 2 || type == 3) { // 降伏、終結
				return;
			}
		}

		if (enemyClan.getCastleId() == 0 && //相手クランが城主ではなく、自キャラがLv15以下
				player.getLevel() <= 15) {
			player.sendPackets(new S_ServerMessage(232)); // \f1レベル15以下の君主は宣戦布告することはできません。
			return;
		}

		if (enemyClan.getCastleId() != 0 && // 相手クランが城主で、自キャラがLv25未満
			player.getLevel() <  Config.DECLARATION_LEVEL) {
			player.sendPackets(new S_SystemMessage("レベル" + Config.DECLARATION_LEVEL + "から宣言することができます。"));
			return;
		}
		
		if (clan.getOnlineClanMember().length <= Config.CLAN_CONNECT_COUNT) {   
			player.sendPackets(new S_SystemMessage("接続した血盟員が"+Config.CLAN_CONNECT_COUNT+"人以上であれば宣言が可能です。"));
			return;
		}

		if (enemyClan.getCastleId() != 0) { // 相手クランが城主
			int castle_id = enemyClan.getCastleId();
			if (WarTimeController.getInstance().isNowWar(castle_id)) { // 戦争の時間内
				L1PcInstance clanMember[] = clan.getOnlineClanMember();
				for (int k = 0; k < clanMember.length; k++) {
					if (L1CastleLocation.checkInWarArea(castle_id, clanMember[k])) {
						if (player != clanMember[k]) {
							player.sendPackets(new S_ServerMessage(477));
							// 攻城：宣言不可（城内に血盟員があります）
							int[] loc = Getback.GetBack_Location(clanMember[k], true);
							L1Location _loc = new L1Location(loc[0], loc[1], (short) loc[2]);
							L1Map map = _loc.getMap();
							L1Location loc2 = L1Location.randomLocation2(loc[0], loc[1], map, (short) loc[2], 1, 5,
									false);
							new L1Teleport().teleport(clanMember[k], loc2.getX(), loc2.getY(), (short) loc2.getMapId(), 5,
									true);
							return;
						}
					}
				}
				boolean enemyInWar = false;
				for (L1War war : warList) {
					if (war.CheckClanInWar(enemyClanName)) { // 相手クランが既に戦争中
						if (type == 0) { // 宣戦布告
							war.DeclareWar(clanName, enemyClanName);
							war.AddAttackClan(clanName);
						} else if (type == 2 || type == 3) {
							if (!war.CheckClanInSameWar(clanName, enemyClanName)) {
								// ジャックとの相対クランが他の戦争
								return;
							}
							if (type == 2) { // 降伏
								war.SurrenderWar(clanName, enemyClanName);
							} else if (type == 3) { // 終結
								war.CeaseWar(clanName, enemyClanName);
							}
						}
						enemyInWar = true;
						break;
					}
				}
				if (!enemyInWar && type == 0) { // 相手クランが戦争中以外で、宣戦布告
					L1War war = new L1War();
					war.handleCommands(1, clanName, enemyClanName); //包囲開始
				}
			} else { // 戦争時間外
				if (type == 0) { // 宣戦布告
					player.sendPackets(new S_ServerMessage(476)); // まだ攻城戦の時間がありません。
				}
			}
		} else { // 相手クランが城主ではない
			boolean enemyInWar = false;
			for (L1War war : warList) {
				if (war.CheckClanInWar(enemyClanName)) { // 相手クランが既に戦争中
					if (type == 0) { // 宣戦布告
						player.sendPackets(new S_ServerMessage(236,
								enemyClanName)); // %0血盟があなたの血盟との戦争を拒否しました。
						return;
					} else if (type == 2 || type == 3) { // 降伏または終結
						if (!war.CheckClanInSameWar(clanName, enemyClanName)) { // ジャックとの相対クランが他の戦争
							return;
						}
					}
					enemyInWar = true;
					break;
				}
			}
			if (!enemyInWar && (type == 2 || type == 3)) { // 相手クランが戦争中以外で、降伏または終結
				return;
			}

			// 攻城戦ではない場合には、相手の血盟注意承認が必要
			L1PcInstance enemyLeader = L1World.getInstance().getPlayer(
					enemyClan.getLeaderName());

			if (enemyLeader == null) { // 相手の血盟主発見されなかった
				player.sendPackets(new S_ServerMessage(218, enemyClanName)); // \f1%0血盟の君主は、現在の世界にありません。
				return;
			}

			if (type == 0) { // 宣戦布告
				enemyLeader.setTempID(player.getId()); // 相手のオブジェクトIDを保存しておく
				enemyLeader.sendPackets(new S_Message_YN(217, clanName,
						playerName)); // %0血盟の％1があなたの血盟との戦争を望んでいます。戦争に応じますか？ （Y / N）
			} else if (type == 2) { //降伏
				enemyLeader.setTempID(player.getId()); // 相手のオブジェクトIDを保存しておく
				enemyLeader.sendPackets(new S_Message_YN(221, clanName)); // %0血盟が降伏を望んでいます。受け入れですか？ （Y / N）
			} else if (type == 3) { //終結
				enemyLeader.setTempID(player.getId()); //相手のオブジェクトIDを保存しておく
				enemyLeader.sendPackets(new S_Message_YN(222, clanName)); // %0血盟戦争の終結を望んでいます。終結のですか？ （Y / N）
			}
		}
	}
	
	@Override
	public String getType() {
		return C_WAR;
	}

}
