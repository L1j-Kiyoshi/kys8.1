package l1j.server.server.clientpackets;

import java.util.Calendar;
import java.util.logging.Logger;

import l1j.server.server.GameClient;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_Unknown2;


public class C_NewCharSelect extends ClientBasePacket {
    private static final String C_NEW_CHAR_SELECT = "[C] C_NewCharSelect";
    private static Logger _log = Logger.getLogger(C_NewCharSelect.class.getName());
    /**
     * 日付と時刻の記録
     **/
    Calendar rightNow = Calendar.getInstance();
    int day = rightNow.get(Calendar.DATE);
    int hour = rightNow.get(Calendar.HOUR);
    int min = rightNow.get(Calendar.MINUTE);
    int sec = rightNow.get(Calendar.SECOND);
    int year = rightNow.get(Calendar.YEAR);
    int month = rightNow.get(Calendar.MONTH) + 1;
    String totime = "[" + year + ":" + month + ":" + day + "]";
    String totime1 = "[" + hour + ":" + min + ":" + sec + "]";
    String date = +year + "_" + month + "_" + day;

    public C_NewCharSelect(byte[] decrypt, GameClient client) throws Exception {
        super(decrypt);
        //client.CharReStart(true);
        //client.sendPacket(new S_PacketBox(S_PacketBox.LOGOUT));

        if (client.getActiveChar() != null) {
            L1PcInstance pc = client.getActiveChar();
            if (pc == null) {
                return;
            }
            //3.63アイテムパケット処理
            pc.isWorld = false;
            //3.63アイテムのパケット処理
            try {
                pc.save();
            } catch (Exception e) {
            }
            try {
                pc.saveInventory();
            } catch (Exception e) {
            }
            try {
                pc.getNetConnection().getAccount().updateTam();
            } catch (Exception e) {
            }
            try {
                pc.getNetConnection().getAccount().updateNcoin();
            } catch (Exception e) {
            }
            // デスペラードかかった状態であれば、リース不可
            if (pc.hasSkillEffect(L1SkillId.DESPERADO)) {
                return;
            }

            //オンライン知らせる。
            L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
            if (clan != null) {
                pc.setOnlineStatus(0);
                clan.updateClanMemberOnline(pc);
            }

            //
            _log.fine("Disconnect from: " + pc.getName());
            if (pc.isDead()) {
                return;
            }
            synchronized (pc) {
                pc.logout();
                client.setActiveChar(null);
                client.setLoginAvailable();
                client.sendPacket(new S_Unknown2(1)); //リースボタンのための構造を変更する// Episode U
            }
        } else {
            _log.fine("Disconnect Request from Account : " + client.getAccountName());
        }

    }

    @Override
    public String getType() {
        return C_NEW_CHAR_SELECT;
    }
}
