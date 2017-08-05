package l1j.server.server.Controller;

import static l1j.server.server.model.skill.L1SkillId.*;

import java.util.Calendar;
import java.util.TimeZone;

import l1j.server.Config;
import l1j.server.GameSystem.AttendanceController;
import l1j.server.GameSystem.Robot.L1RobotInstance;
import l1j.server.server.Opcodes;
import l1j.server.server.datatables.SpamTable;
import l1j.server.server.model.L1AccountAttendance;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1ExcludingList;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ACTION_UI;
import l1j.server.server.serverpackets.S_Attendance;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_Restart;
import l1j.server.server.serverpackets.S_SystemMessage;


public class PremiumTimeController implements Runnable {

    public static final int SLEEP_TIME = Config.FEATHER_TIME * 60000; // ソース600秒

    private static PremiumTimeController _instance;

    public static PremiumTimeController getInstance() {
        if (_instance == null) {
            _instance = new PremiumTimeController();
        }
        return _instance;
    }

    @Override
    public void run() {
        try {

            //	checkDragonBlood();
            pcbuffPremiumTime();
            registComment();
            deleteDoll();
        } catch (Exception e1) {
        }
    }


    private void deleteDoll() {
        try {
            for (Object obj : L1World.getInstance().getObject()) {
                if (obj instanceof L1DollInstance) {
                    L1DollInstance doll = (L1DollInstance) obj;
                    if (doll.getMaster() == null) {
                        doll.deleteMe();
                    } else if (((L1PcInstance) doll.getMaster()).getNetConnection() == null) {
                        doll.deleteMe();
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private void pcbuffPremiumTime() {
        for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
            if (pc instanceof L1RobotInstance) {
                continue;
            }
            if (pc.PCRoom_Buff_Delete) {
                pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "【PC部屋の商品終了案内] PC部屋利用時間が終了して強制的にリスタートが行われます。"));
                pc.sendPackets(new S_SystemMessage("【PC部屋の商品終了案内]リスタートを進めていなくてもメリットはできません。"));
                pc.sendPackets(new S_Restart(pc.getId(), 1), true);
            }

            long sysTime = System.currentTimeMillis();
            if (pc.PCRoom_Buff) {
                if (pc.getAccount().getBuff_PCRoom() != null) {
                    if (sysTime <= pc.getAccount().getBuff_PCRoom().getTime()) {
                        long pcTime = pc.getAccount().getBuff_PCRoom().getTime() - sysTime;
                        TimeZone seoul = TimeZone.getTimeZone(Config.TIME_ZONE);
                        Calendar calendar = Calendar.getInstance(seoul);
                        calendar.setTimeInMillis(pcTime);
                        int d = calendar.get(Calendar.DATE) - 1;
                        int h = calendar.get(Calendar.HOUR_OF_DAY);
                        int m = calendar.get(Calendar.MINUTE);
                        int sc = calendar.get(Calendar.SECOND);
                        if (d == 0) {
                            if (h > 0) {
                                if (h == 1 && m == 0) {
                                    pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "【PC部屋利用時間】" + h + "時間 " + m + "分" + sc + "秒残りました。"));
                                }
                            } else {
                                if (m == 30) {
                                    pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "【PC部屋利用時間】" + m + "分" + sc + "秒残りました。"));
                                    pc.sendPackets(new S_SystemMessage("【PC部屋の商品終了案内]利用時間排出時に強制リスタートが行われます。"));
                                } else if (m == 20) {
                                    pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "【PC部屋利用時間】" + m + "分" + sc + "秒残りました。"));
                                    pc.sendPackets(new S_SystemMessage("【PC部屋の商品終了案内]利用時間排出時に強制リスタートが行われます。"));
                                } else if (m <= 10) {
                                    pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "【PC部屋利用時間】" + m + "分" + sc + "秒残りました。"));
                                    pc.sendPackets(new S_SystemMessage("【PC部屋の商品終了案内]終了後バフが残っていてもメリットはできません。終了時に自動的リスタートが行われます。"));
                                }
                            }
                        }
                    } else {
                        pc.PCRoom_Buff = false;
                        pc.PCRoom_Buff_Delete = true;
                        pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.PCBANG_SET, true));
                        pc.sendPackets(new S_Restart(pc.getId(), 1), true);
                    }
                }
            }
            L1AccountAttendance acc = AttendanceController.findacc(pc.getAccountName());
            if (acc != null)
                //acc.getPc().sendPackets(new S_Attendance(acc, 1, false));
                acc.getPc().sendPackets(new S_Attendance(acc, 0, false));
        }
    }

    private void checkPremiumTime() {//一定時間羽支給
        for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
            if (!pc.isPrivateShop() && !pc.isAutoClanjoin() && !pc.noPlayerCK && !pc.noPlayerck2 && pc != null && !pc.isDead()) {
                int FN = Config.FEATHER_NUM;
                int CLN = Config.FEATHER_NUM1;
                int CAN = Config.FEATHER_NUM2;
                int FN2 = Config.useritem;//商品番号
                int FN3 = Config.usercount;//本数
                L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
                /** 全ユーザーにプレゼントを支給する **/

                if (Config.ALL_GIFT_OPERATION) {
                    pc.getInventory().storeItem(FN2, FN3);
                    pc.sendPackets(new S_SystemMessage("\\aA通知：ギフトボックス (\\aG" + FN3 + "\\aA) 獲得されました。"));
                }

                if (pc.getClanid() == 0) { // 無血
                    pc.getInventory().storeItem(41159, FN);
                    pc.sendPackets(new S_SystemMessage("\\aA通知：ピクシーの羽 (\\aG" + FN + "\\aA) 獲得されました。"));
                }
                if (clan != null) {
                    if (clan.getCastleId() == 0 && pc.getClanid() != 0) { // 血盟
                        pc.getInventory().storeItem(41159, (CLN + FN));
                        pc.sendPackets(new S_SystemMessage("\\aA通知：ピクシーの羽 (\\aG" + FN + "+" + CLN + "\\aA)獲得されました。"));
                    }
                    if (clan.getCastleId() != 0) { // 腥血
                        pc.getInventory().storeItem(41159, (CAN + FN));
                        pc.sendPackets(new S_SystemMessage("\\aA通知：ピクシーの羽 (\\aG" + FN + "+" + CAN + "\\aA) 獲得されました."));
                    }
                }
            }

        }
    }

    private void registComment() {
        try {
            for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                if (pc.isAutoClanjoin()) {
                    S_ChatPacket s_chatpacket = new S_ChatPacket(pc, pc.getClanname() + "血盟で血盟員募集中です。前/登録パット", Opcodes.S_SAY, 0);
                    for (L1PcInstance listner : L1World.getInstance().getRecognizePlayer(pc)) {
                        L1ExcludingList spamList3 = SpamTable.getInstance().getExcludeTable(listner.getId());
                        if (!spamList3.contains(0, pc.getName())) {
                            listner.sendPackets(s_chatpacket);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void checkDragonBlood() {
        int time = 0;
        for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
            if (pc.hasSkillEffect(ANTA_BUFF)) {
                time = pc.getSkillEffectTimeSec(ANTA_BUFF) / 60;
                pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONBLOOD, 82, time));
            }
            if (pc.hasSkillEffect(FAFU_BUFF)) {
                time = pc.getSkillEffectTimeSec(FAFU_BUFF) / 60;
                pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONBLOOD, 85, time));
            }
            if (pc.hasSkillEffect(RIND_BUFF)) {
                time = pc.getSkillEffectTimeSec(RIND_BUFF) / 60;
                pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONBLOOD, 88, time));
            }
            if (pc.hasSkillEffect(VALA_BUFF)) {
                time = pc.getSkillEffectTimeSec(VALA_BUFF) / 60;
                pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONBLOOD, 88, time));
            }
        }
    }
}