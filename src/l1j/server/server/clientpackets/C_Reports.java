package l1j.server.server.clientpackets;

import java.util.logging.Logger;

import l1j.server.server.GameClient;
import l1j.server.server.datatables.ReportTable;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;

public class C_Reports extends ClientBasePacket {

    private static final String C_REPORTS = "[C] C_Reports";
    private static Logger _log = Logger.getLogger(C_Reports.class.getName());


    public C_Reports(byte abyte0[], GameClient client)
            throws Exception {
        super(abyte0);
        if (client == null) {
            return;
        }
        int type = readC(); // タイプ
        int objid = readD(); // キャラクターオブジェクト
        L1PcInstance pc = client.getActiveChar();
        if (pc == null)
            return;
        L1PcInstance target = (L1PcInstance) L1World.getInstance().findObject(objid);

        if (!pc.isReport()) {
            pc.sendPackets(new S_ServerMessage(1021)); // しばらくして戻って報告してください。
            return;
        }

        if (target != null) { // objidがnullでない場合
            if (!ReportTable.getInstance().name.contains(target.getName())) {
                ReportTable.getInstance().name.add(target.getName());
                ReportTable.getInstance().report(target.getName(), pc.getName()); // DBに登録
                pc.sendPackets(new S_ServerMessage(1019)); // 登録された。
                pc.startReportDeley();
            } else {
                pc.sendPackets(new S_ServerMessage(1020)); // 既に登録された。
            }
        }
    }

    @Override
    public String getType() {
        return C_REPORTS;
    }
}







