package l1j.server.server.clientpackets;

import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.server.GameClient;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.MonsterBookTable;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_CommonNews;
import l1j.server.server.serverpackets.S_DeleteCharOK;

public class C_DeleteChar extends ClientBasePacket {

	private static final String C_DELETE_CHAR = "[C] RequestDeleteChar";

	private static Logger _log = Logger.getLogger(C_DeleteChar.class.getName());

	public C_DeleteChar(byte decrypt[], GameClient client) throws Exception {
		super(decrypt);
		String name = readS();
		try {

			L1PcInstance pc = CharacterTable.getInstance().restoreCharacter(name);

			if (pc == null) {
				client.sendPacket(new S_CommonNews("존재하지 않는 캐릭터 입니다."));
				return;
			}

			for (L1PcInstance target : L1World.getInstance().getAllPlayers3()) {
				if (target.getId() == pc.getId()) {
					client.sendPacket(new S_CommonNews("접속 중인 캐릭터는 삭제할 수 없습니다."));
					return;
				}
			}
			if (pc != null && pc.getLevel() >= 1 && Config.DELETE_CHARACTER_AFTER_7DAYS) {
				if (pc.getType() < 32) {
					if (pc.isCrown()) {
						pc.setType(32);
					} else if (pc.isKnight()) {
						pc.setType(33);
					} else if (pc.isElf()) {
						pc.setType(34);
					} else if (pc.isWizard()) {
						pc.setType(35);
					} else if (pc.isDarkelf()) {
						pc.setType(36);
					} else if (pc.isDragonknight()) {
						pc.setType(37);
					} else if (pc.isBlackwizard()) {
						pc.setType(38);
					} else if (pc.is전사()) {
						pc.setType(39);						
					}
					Timestamp deleteTime = new Timestamp(System.currentTimeMillis() + 86400000);
					pc.setDeleteTime(deleteTime);
					pc.save();
				} else {
					if (pc.isCrown()) {
						pc.setType(0);
					} else if (pc.isKnight()) {
						pc.setType(1);
					} else if (pc.isElf()) {
						pc.setType(2);
					} else if (pc.isWizard()) {
						pc.setType(3);
					} else if (pc.isDarkelf()) {
						pc.setType(4);
					} else if (pc.isDragonknight()) {
						pc.setType(5);
					} else if (pc.isBlackwizard()) {
						pc.setType(6);
					} else if (pc.is전사()) {
						pc.setType(7);
					}
					pc.setDeleteTime(null);
					pc.save();
				}
				client.sendPacket(new S_DeleteCharOK(S_DeleteCharOK.DELETE_CHAR_AFTER_7DAYS));
				return;
			}

			if (pc != null) {
				CharacterTable.getInstance().restoreInventory(pc);

				for (L1ItemInstance item : pc.getInventory().getItems()) {
					if (item.getBless() >= 128) {
						client.sendPacket(new S_CommonNews("봉인된 아이템을 가진 캐릭터는 삭제할 수 없습니다."));
						return;
					}
				}
			}

			if (pc != null) {
				L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
				if (clan != null) {
				}
			}
			CharacterTable.getInstance().deleteCharacter(client.getAccountName(), name);
			MonsterBookTable.getInstace().deleteMonsterBookList(pc.getId()); 
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			client.close();
			return;
		}

		client.sendPacket(new S_DeleteCharOK(S_DeleteCharOK.DELETE_CHAR_NOW));
	}

	@Override
	public String getType() {
		return C_DELETE_CHAR;
	}

}
