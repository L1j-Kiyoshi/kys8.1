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
        if (pc == null) return;
        L1Object tg = L1World.getInstance().findObject(id);
        if (tg == null) {
            _log.warning("Invalid NPC ID: " + id);
            return;
        }
        if (title.length() > 16) {
            pc.sendPackets(new S_SystemMessage("掲示板タイトルの文字数を超えました。"));
            return;
        }
        if (tg instanceof L1BoardInstance) {
            L1BoardInstance board = (L1BoardInstance) tg;
            if (board != null) {
                if (pc.getLevel() < 30 && board.getNpcId() != 900006) {
                    pc.sendPackets(new S_SystemMessage("\\aH書き込みレベル : 30"));

                    return;
                }
            }
            switch (board.getNpcId()) {
                case 900006:// ドキboard_posts_key
                    if (pc.getInventory().checkItem(L1ItemId.DRAGON_KEY, 1)) {
                        L1BoardPost.createKey(pc.getName(), title, content);
                        pc.sendPackets(new S_SystemMessage("ドラゴンキー販売は村の掲示板をご利用ください"));
                    } else {
                        pc.sendPackets(new S_SystemMessage("ドラゴンキーを所持していません。"));
                    }
                    break;
                case 4200015: // GMサーバー情報掲示板board_posts_notice
                    if (pc.getAccessLevel() == Config.GMCODE) {
                        L1BoardPost.createGM(pc.getName(), title, content);
                    } else {
                        pc.sendPackets(new S_SystemMessage("\\aHオペレータ専用掲示板です。"));
                        return;
                    }
                    break;
                case 4200020: // GM掲示板1 board_notice1
                    if (pc.getAccessLevel() == Config.GMCODE) {
                        L1BoardPost.createGM1(pc.getName(), title, content);
                    } else {
                        pc.sendPackets(new S_SystemMessage("\\aHオペレータ専用掲示板です。"));
                        return;
                    }
                    break;
                case 4200021: // GM掲示板2 board_notice2
                    if (pc.getAccessLevel() == Config.GMCODE) {
                        L1BoardPost.createGM2(pc.getName(), title, content);
                    } else {
                        pc.sendPackets(new S_SystemMessage("\\aHオペレータ専用掲示板です。"));
                        return;
                    }
                    break;
                case 4200022: // パッケージGM掲示板3 board_notice3
                    if (pc.getAccessLevel() == Config.GMCODE) {
                        L1BoardPost.createGM3(pc.getName(), title, content);
                    } else {
                        pc.sendPackets(new S_SystemMessage("\\aHオペレータ専用掲示板です。"));
                        return;
                    }
                    break;
                case 500002: //提案^件掲示板board_posts_fix
                    if (pc.getInventory().checkItem(L1ItemId.ADENA, 300)) {
                        pc.getInventory().consumeItem(L1ItemId.ADENA, 300);
                        L1BoardPost.createPhone(pc.getName(), " - 非公開文 - ", content);
                        pc.sendPackets(new S_SystemMessage("\\aH登録完了：ナイトのタイトルは、運営者もいない見て参照してください。"));
                    } else {
                        pc.sendPackets(new S_SystemMessage("アデナが不足します。"));
                    }
                    break;

                default:
                    if (pc.getInventory().checkItem(L1ItemId.ADENA, 300)) {
                        pc.getInventory().consumeItem(L1ItemId.ADENA, 300);
                        L1BoardPost.create(pc.getName(), title, content);
                        pc.sendPackets(new S_SystemMessage("スレッドの登録が完了しました。"));
                    } else {
                        pc.sendPackets(new S_SystemMessage("アデナが不足します。"));
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
