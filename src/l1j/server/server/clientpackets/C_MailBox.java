package l1j.server.server.clientpackets;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.GameClient;
import l1j.server.server.datatables.LetterTable;
import l1j.server.server.datatables.SpamTable;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Clan.ClanMember;
import l1j.server.server.model.L1ExcludingList;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_LetterList;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;

public class C_MailBox extends ClientBasePacket {

    private static Logger _log = Logger.getLogger(C_MailBox.class.getName());

    private static final int TYPE_PRIVATE_MAIL = 0; //個人的な手紙
    private static final int TYPE_BLOODPLEDGE_MAIL = 1; // 血盟メール
    private static final int TYPE_KEPT_MAIL = 2; // 保管メール

    private static final int READ_PRIVATE_MAIL = 16; // 個人的な手紙を読む
    private static final int READ_BLOODPLEDGE_MAIL = 17; // 血盟メールを読む
    private static final int READ_KEPT_MAIL_ = 18; //アーカイブメールを読む

    private static final int WRITE_PRIVATE_MAIL = 32; // 個人メールを送る
    private static final int WRITE_BLOODPLEDGE_MAIL = 33; // 血盟メールを送る

    private static final int DEL_PRIVATE_MAIL = 48; // 個人的な手紙の削除
    private static final int DEL_BLOODPLEDGE_MAIL = 49; // 血盟メールの削除
    private static final int DEL_KEPT_MAIL = 50; // アーカイブメールの削除

    private static final int TO_KEEP_MAIL = 64; // メール保管する

    private static final int PRICE_PRIVATEMAIL = 50; // 個人的な手紙価格

    private static final int DEL_PRIVATE_LIST_MAIL = 96; // 個人的な手紙リストの削除
    private static final int DEL_BLOODPLEDGE_LIST_MAIL = 97; //血盟メールリストの削除
    private static final int DEL_KEEP_LIST = 98; // 保管メールリストの削除

    private static final int PRICE_BLOODPLEDGEMAIL = 1000; // 血盟メール価格

    private static final int SIZE_PRIVATE_MAILBOX = 40; // 個人メールボックスのサイズ
    private static final int SIZE_BLOODPLEDGE_MAILBOX = 80; // 血盟メールボックスのサイズ
    private static final int SIZE_KEPTMAIL_MAILBOX = 10; // メールボックスサイズz

    private static final String C_MailBox = "[C] C_MailBox";

    public C_MailBox(byte abyte0[], GameClient client) {
        super(abyte0);
        int type = readC();
        L1PcInstance pc = client.getActiveChar();
//		if(pc == null)
//			return;
        switch (type) {

            case TYPE_PRIVATE_MAIL:
                if (pc.isGm())
                    LetterList(pc, TYPE_PRIVATE_MAIL, 1000);
                else
                    LetterList(pc, TYPE_PRIVATE_MAIL, SIZE_PRIVATE_MAILBOX);
                break;
            case TYPE_BLOODPLEDGE_MAIL:
                LetterList(pc, TYPE_BLOODPLEDGE_MAIL, SIZE_BLOODPLEDGE_MAILBOX);
                break;
            case TYPE_KEPT_MAIL:
                LetterList(pc, TYPE_KEPT_MAIL, SIZE_KEPTMAIL_MAILBOX);
                break;
            case READ_PRIVATE_MAIL:
                ReadLetter(pc, READ_PRIVATE_MAIL, 0);
                break;
            case READ_BLOODPLEDGE_MAIL:
                ReadLetter(pc, READ_BLOODPLEDGE_MAIL, 0);
                break;
            case READ_KEPT_MAIL_:
                ReadLetter(pc, READ_KEPT_MAIL_, 0);
                break;
            case WRITE_PRIVATE_MAIL:
                WritePrivateMail(pc);
                break;
            case WRITE_BLOODPLEDGE_MAIL:
                WriteBloodPledgeMail(pc);
                break;
            case DEL_PRIVATE_MAIL:
                DeleteLetter(pc, DEL_PRIVATE_MAIL, TYPE_PRIVATE_MAIL);
                break;
            case DEL_BLOODPLEDGE_MAIL:
                DeleteLetter(pc, DEL_BLOODPLEDGE_MAIL, TYPE_BLOODPLEDGE_MAIL);
                break;
            case DEL_KEPT_MAIL:
                DeleteLetter(pc, DEL_KEPT_MAIL, TYPE_KEPT_MAIL);
                break;
            case TO_KEEP_MAIL:
                SaveLetter(pc, TO_KEEP_MAIL, TYPE_KEPT_MAIL);
                break;
            case DEL_PRIVATE_LIST_MAIL:
                DeleteLetter_List(pc, DEL_PRIVATE_MAIL, SIZE_PRIVATE_MAILBOX);
                break;
            case DEL_BLOODPLEDGE_LIST_MAIL:
                DeleteLetter_List(pc, DEL_BLOODPLEDGE_MAIL, SIZE_BLOODPLEDGE_MAILBOX);
                break;
            case DEL_KEEP_LIST:
                DeleteLetter_List(pc, DEL_KEPT_MAIL, SIZE_KEPTMAIL_MAILBOX);
                break;
            default:
                // LetterList(pc,type);
        }
    }

    private void DeleteLetter_List(L1PcInstance pc, int deletetype, int type) {
        int delete_num = readD();
        for (int i = 0; i < delete_num; i++) {
            int id = readD();
            LetterTable.getInstance().deleteLetter(id);
            pc.sendPackets(new S_LetterList(pc, deletetype, id, true));
        }
    }

    private boolean payMailCost(final L1PcInstance RECEIVER, final int PRICE) {
        int AdenaCnt = RECEIVER.getInventory().countItems(L1ItemId.ADENA);
        if (AdenaCnt < PRICE) {
            RECEIVER.sendPackets(new S_ServerMessage(189, ""));
            return false;
        }

        RECEIVER.getInventory().consumeItem(L1ItemId.ADENA, PRICE);
        return true;
    }

    private void WritePrivateMail(L1PcInstance sender) {
        if (sender.getLevel() <= 29) {
            sender.sendPackets(new S_SystemMessage("30レベル以下はメールを送信することができません。"));
            return;
        }
        if (!payMailCost(sender, PRICE_PRIVATEMAIL))
            return;

        int paper = readH(); // 文房具

        Timestamp dTime = new Timestamp(System.currentTimeMillis());
        String receiverName = readS();
        String subject = readSS();
        String content = readSS();

        if (!checkCountMail(sender, receiverName, TYPE_PRIVATE_MAIL, SIZE_PRIVATE_MAILBOX))
            return;

        L1PcInstance target = L1World.getInstance().getPlayer(receiverName);

        if (target != null) {
            L1ExcludingList exList = SpamTable.getInstance().getExcludeTable(target.getId());
            if (exList.contains(1, sender.getName())) {
                sender.sendPackets(new S_ServerMessage(3082));
                return;
            }
        }
        int id = LetterTable.getInstance().writeLetter(paper, dTime, sender.getName(), receiverName, TYPE_PRIVATE_MAIL, subject, content);
        if (target != null && target.getOnlineStatus() != 0) {
            target.sendPackets(new S_LetterList(S_LetterList.WRITE_TYPE_PRIVATE_MAIL, id, S_LetterList.TYPE_RECEIVE, sender.getName(), subject)); // 受信者
        }
        // sender.sendPackets(new S_LetterList(S_LetterList.WRITE_TYPE_PRIVATE_MAIL, id, S_LetterList.TYPE_SEND, receiverName, subject)); //送信者
    }

    private void WriteBloodPledgeMail(L1PcInstance sender) {
        if (!payMailCost(sender, PRICE_BLOODPLEDGEMAIL))
            return;

        int paper = readH(); // 文房具

        Timestamp dTime = new Timestamp(System.currentTimeMillis());
        String receiverName = readS();
        String subject = readSS();
        String content = readSS();

        L1Clan targetClan = null;
        for (L1Clan clan : L1World.getInstance().getAllClans()) {
            if (clan.getClanName().toLowerCase().equals(receiverName.toLowerCase())) {
                targetClan = clan;
                break;
            }
        }
        String name;
        L1PcInstance target = null;
        ArrayList<ClanMember> clanMemberList = targetClan.getClanMemberList();
        try {
            for (int i = 0, a = clanMemberList.size(); i < a; i++) {
                name = clanMemberList.get(i).name;
                target = L1World.getInstance().getPlayer(name);
                if (!checkCountMail(sender, name, TYPE_BLOODPLEDGE_MAIL, SIZE_BLOODPLEDGE_MAILBOX))
                    continue;
                if (name.equalsIgnoreCase(sender.getName()))
                    continue;
                int id = LetterTable.getInstance().writeLetter(paper, dTime, sender.getName(), name, TYPE_BLOODPLEDGE_MAIL, subject, content);

                if (target != null && target.getOnlineStatus() != 0) {
                    target.sendPackets(new S_LetterList(S_LetterList.WRITE_TYPE_BLOODPLEDGE_MAIL, id, S_LetterList.TYPE_RECEIVE, sender.getName(), subject)); // 受信者
                }

            }
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
    }

    private void DeleteLetter(L1PcInstance pc, int type, int letterType) {
        int id = readD();
        LetterTable.getInstance().deleteLetter(id);
        pc.sendPackets(new S_LetterList(pc, type, id, true));
    }

    private void ReadLetter(L1PcInstance pc, int type, int read) {
        int id = readD();
        LetterTable.getInstance().CheckLetter(id);
        pc.sendPackets(new S_LetterList(pc, type, id, read));
    }

    private void LetterList(L1PcInstance pc, int type, int count) {
        pc.sendPackets(new S_LetterList(pc, type, count));
    }

    private void SaveLetter(L1PcInstance pc, int type, int letterType) {
        int id = readD();
        LetterTable.getInstance().SaveLetter(id, letterType);
        pc.sendPackets(new S_LetterList(pc, type, id, true));
    }

    private boolean checkCountMail(L1PcInstance from, String to, int type, int max) {
        int cntMailInMailBox = LetterTable.getInstance().getLetterCount(to, type);
        if (cntMailInMailBox >= max) { // トレイ満タン
            from.sendPackets(new S_SystemMessage(to + "様のメールボックスがいっぱいで、新しいメールを送信することができません。"));
            return false;
        }
        return true;
    }

    @Override
    public String getType() {
        return C_MailBox;
    }
}
