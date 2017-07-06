package l1j.server.server.clientpackets;

import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.server.GameClient;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1BoardInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1BoardPost;

public class C_BoardWrite extends ClientBasePacket {

	private static final String C_BOARD_WRITE = "[C] C_BoardWrite";
	private static Logger _log = Logger.getLogger(C_BoardWrite.class.getName());

	public C_BoardWrite(byte decrypt[], GameClient client) {
		super(decrypt);
		int id = readD();
		String title = readS();
		String content = readS();
		L1PcInstance pc = client.getActiveChar();
		if (pc == null)return;
		L1Object tg = L1World.getInstance().findObject(id);
		if (tg == null) {
			_log.warning("Invalid NPC ID: " + id);
			return;
		}
		if (title.length() > 16) {
			pc.sendPackets(new S_SystemMessage("게시판 제목 글자수가 초과하였습니다."));
			return;
		}
		if (tg instanceof L1BoardInstance) {
			L1BoardInstance board = (L1BoardInstance) tg;
			if (board != null) {
				if (pc.getLevel() < 30 && board.getNpcId() != 900006) {
					pc.sendPackets(new S_SystemMessage("\\aH글쓰기 레벨 : 30"));
					
					return;
				}
			}
			switch (board.getNpcId()) {
			case 900006:// 드키 board_posts_key
				if (pc.getInventory().checkItem(L1ItemId.DRAGON_KEY, 1)) {
					L1BoardPost.createKey(pc.getName(), title, content);
					pc.sendPackets(new S_SystemMessage("드래곤키 판매는 마을게시판을 이용해주세요"));
				} else {
					pc.sendPackets(new S_SystemMessage("드래곤 키를 소지하고 있지 않습니다."));
				}
				break;
			case 4200015: // GM 서버정보게시판 board_posts_notice
				if (pc.getAccessLevel() == Config.GMCODE) {
					L1BoardPost.createGM(pc.getName(), title, content);
				} else {
					pc.sendPackets(new S_SystemMessage("\\aH운영자 전용 게시판 입니다."));
					return;
				}
				break;
			case 4200020: // GM게시판1 board_notice1
				if (pc.getAccessLevel() == Config.GMCODE) {
					L1BoardPost.createGM1(pc.getName(), title, content);
				} else {
					pc.sendPackets(new S_SystemMessage("\\aH운영자 전용 게시판 입니다."));
					return;
				}
				break;
			case 4200021: // GM게시판2 board_notice2
				if (pc.getAccessLevel() == Config.GMCODE) {
					L1BoardPost.createGM2(pc.getName(), title, content);
				} else {
					pc.sendPackets(new S_SystemMessage("\\aH운영자 전용 게시판 입니다."));
					return;
				}
				break;
			case 4200022: // 패키지 GM게시판3 board_notice3
				if (pc.getAccessLevel() == Config.GMCODE) {
					L1BoardPost.createGM3(pc.getName(), title, content);
				} else {
					pc.sendPackets(new S_SystemMessage("\\aH운영자 전용 게시판 입니다."));
					return;
				}
				break;
			case 500002: // 건의 사항^건의 게시판 board_posts_fix
				if (pc.getInventory().checkItem(L1ItemId.ADENA, 300)) {
					pc.getInventory().consumeItem(L1ItemId.ADENA, 300);
					L1BoardPost.createPhone(pc.getName(), "-비밀글-", content);
					pc.sendPackets(new S_SystemMessage("\\aH등록 완료 : 글 제목은 운영자도 못보니 참고하세요."));
				} else {
					pc.sendPackets(new S_SystemMessage("아데나가 부족합니다."));
				}
				break;

			default:
				if (pc.getInventory().checkItem(L1ItemId.ADENA, 300)) {
					pc.getInventory().consumeItem(L1ItemId.ADENA, 300);
					L1BoardPost.create(pc.getName(), title, content);
					pc.sendPackets(new S_SystemMessage("게시글 등록이 완료되었습니다."));
				} else {
					pc.sendPackets(new S_SystemMessage("아데나가 부족합니다."));
				}
				break;
			}

		}

	}

	@Override
	public String getType() {
		return C_BOARD_WRITE;
	}
}
