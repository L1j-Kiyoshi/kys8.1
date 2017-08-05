package l1j.server.server;

import java.util.Calendar;

import l1j.server.server.Controller.DungeonQuitController;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_SystemMessage;

public class DungeonTimer implements Runnable {

    private static DungeonTimer instance;

    public static final int SleepTime = 1 * 60 * 1000; //1分ごとにチェック

    public static DungeonTimer getInstance() {
        if (instance == null) {
            instance = new DungeonTimer();
        }
        return instance;
    }

    @Override
    public void run() {
        try {
            for (L1PcInstance use : L1World.getInstance().getAllPlayers()) {
                if (use == null || use.getNetConnection() == null || use.noPlayerCK || use.noPlayerck2) {
                    continue;
                } else {
                    try {
                        if (use.getMapId() >= 53 && use.getMapId() <= 56
                                || use.getMapId() >= 15403 && use.getMapId() <= 15404) { // ギラン
                            GiranTimeCheck(use);
                        }
                        if (use.getMapId() >= 78 && use.getMapId() <= 82) { //オーレン
                            OrenTimeCheck(use);
                        }
                        if (use.getMapId() >= 30 && use.getMapId() <= 33
                                || use.getMapId() >= 35 && use.getMapId() <= 37
                                || use.getMapId() == 814) { //DVC
                            DrageonTimeCheck(use);
                        }
                        /*if (use.getMapId() >= 451 && use.getMapId() <= 456
                                || use.getMapId() >= 460 && use.getMapId() <= 466
								|| use.getMapId() >= 470 && use.getMapId() <= 478
								|| use.getMapId() >= 490 && use.getMapId() <= 496
								|| use.getMapId() >= 530 && use.getMapId() <= 534
								|| use.getMapId() == 479){
							RadungeonTimeCheck(use);
						}*/
                        if (use.getMapId() == 303) { // モンソム
                            SomeTimeCheck(use);
                        }
                        if (use.getMapId() == 430 || use.getMapId() == 400) { // 精霊の墓、古代の墓
                            SoulTimeCheck(use);
                        }
                        if (use.getMapId() == 280 || use.getMapId() == 281 || use.getMapId() == 282
                                || use.getMapId() == 283 || use.getMapId() == 284) { //バルログ陣営
                            newdodungeonTimeCheck(use);
                        }
                        if (use.getMapId() == 285 || use.getMapId() == 286 || use.getMapId() == 287 || use.getMapId() == 288
                                || use.getMapId() == 289) { //ヤヒ陣営
                            OrenTimeCheck(use);
                        }
                        if (use.getMapId() == 5555 || use.getMapId() == 5556) { //オルドンPC
                            icedungeonTimeCheck(use);
                        }
                        if (use.getMapId() == 1 || use.getMapId() == 2) { //巻い
                            islanddungeonTimeCheck(use);
                        }
                        init();

                    } catch (Exception a) {
                        //not
                    }
                }
            }
        } catch (Exception a) {
            System.out.println("DungeonTimer エラー");
        }
    }

    private void init() {
        try {
            Calendar cal = Calendar.getInstance();
            int hour = Calendar.HOUR;
            int minute = Calendar.MINUTE;
            /** 0 午前 , 1 午後 * */
            String ampm = "午後";
            if (cal.get(Calendar.AM_PM) == 0) {
                ampm = "午前";
            }
            if (DungeonQuitController.getInstance().isgameStart == false) {
                if ((ampm.equals("午前") && cal.get(hour) == 8 && cal.get(minute) == 59)) {//毎日午前8時59分の初期化
                    DungeonQuitController.getInstance().isgameStart = true;
                    System.out.println("■ダンジョン初期化■: " + ampm + " " + cal.get(hour) + "時" + cal.get(minute) + "分初期化しました。");
                }
            }
        } catch (Exception e) {
            System.out.println("時間の初期化エラー" + e);
        }
    }

    private void GiranTimeCheck(L1PcInstance pc) {
        if (pc.getGirandungeonTime() == 119) {
            new L1Teleport().teleport(pc, 33419, 32810, (short) 4, 0, true);
            pc.sendPackets(new S_SystemMessage("ギラン監獄ダンジョン時間が経過しました。"));
        }
        pc.setGirandungeonTime(pc.getGirandungeonTime() + 1);
    }

    private void OrenTimeCheck(L1PcInstance pc) {
        if (pc.getOrendungeonTime() == 59) {
            new L1Teleport().teleport(pc, 33419, 32810, (short) 4, 0, true);
            pc.sendPackets(new S_SystemMessage("ダンジョン時間が経過しました。"));
        }
        pc.setOrendungeonTime(pc.getOrendungeonTime() + 1);

    }

    private void DrageonTimeCheck(L1PcInstance pc) {
        if (pc.getDrageonTime() == 119) {
            new L1Teleport().teleport(pc, 33419, 32810, (short) 4, 0, true);
            pc.sendPackets(new S_SystemMessage("\\aA警告: \\aG[用の]\\aA ダンジョン時間が経過しました。"));
        }
        pc.setDrageonTime(pc.getDrageonTime() + 1);
    }

    private void SomeTimeCheck(L1PcInstance pc) {
        if (pc.getSomeTime() == 29) {
            new L1Teleport().teleport(pc, 33419, 32810, (short) 4, 0, true);
            pc.sendPackets(new S_SystemMessage("\\aA警告: \\aG[夢幻の島]\\aA ダンジョン時間が経過しました。"));
        }
        pc.setSomeTime(pc.getSomeTime() + 1);
    }

    private void SoulTimeCheck(L1PcInstance pc) {
        if (pc.getSoulTime() == 29) {
            new L1Teleport().teleport(pc, 33419, 32810, (short) 4, 0, true);
            pc.sendPackets(new S_ChatPacket(pc, "墓滞留時間が経過しました。"));
        }
        pc.setSoulTime(pc.getSoulTime() + 1);
    }

    private void RadungeonTimeCheck(L1PcInstance pc) {
        if (pc.getRadungeonTime() == 119) {
            new L1Teleport().teleport(pc, 33419, 32810, (short) 4, 0, true);
            pc.sendPackets(new S_ChatPacket(pc, "ラスタバドケイブ時間が経過しました。"));
        }
        pc.setRadungeonTime(pc.getRadungeonTime() + 1);
    }

    private void newdodungeonTimeCheck(L1PcInstance pc) {
        if (pc.getnewdodungeonTime() == 59) {
            new L1Teleport().teleport(pc, 33419, 32810, (short) 4, 0, true);
            pc.sendPackets(new S_SystemMessage("\\aA警告: \\aG[バルログ陣営]\\aA ダンジョン時間が経過しました。"));
        }
        pc.setnewdodungeonTime(pc.getnewdodungeonTime() + 1);
    }

    private void icedungeonTimeCheck(L1PcInstance pc) {
        if (pc.geticedungeonTime() == 29) {
            new L1Teleport().teleport(pc, 33419, 32810, (short) 4, 0, true);
            pc.sendPackets(new S_SystemMessage("\\aA警告: \\aG[氷PC]\\aA ダンジョン時間が経過しました。"));
        }
        pc.seticedungeonTime(pc.geticedungeonTime() + 1);
    }

    private void islanddungeonTimeCheck(L1PcInstance pc) {
        if (pc.getislandTime() == 119) {
            new L1Teleport().teleport(pc, 32585, 32929, (short) 0, 0, true);
            pc.sendPackets(new S_SystemMessage("\\aA警告: \\aG[話せる島]\\aA ダンジョン時間が経過しました。"));
        }
        pc.setislandTime(pc.getislandTime() + 1);
    }
}