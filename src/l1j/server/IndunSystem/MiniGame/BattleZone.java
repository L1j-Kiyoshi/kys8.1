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
package l1j.server.IndunSystem.MiniGame;

import java.util.ArrayList;
import java.util.Random;

import l1j.server.Config;
import l1j.server.server.Announcements;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.gametime.RealTimeClock;
import l1j.server.server.serverpackets.S_HPMeter;
import l1j.server.server.serverpackets.S_SystemMessage;

public class BattleZone implements Runnable {
    protected final Random _random = new Random();

    private static BattleZone _instance;


    //デュアル開始するかどうか
    private boolean _DuelStart;

    public boolean getDuelStart() {
        return _DuelStart;
    }

    public void setDuelStart(boolean duel) {
        _DuelStart = duel;
    }

    //デュアル立場かどうか
    private boolean _DuelOpen;

    public boolean getDuelOpen() {
        return _DuelOpen;
    }

    public void setDuelOpen(boolean duel) {
        _DuelOpen = duel;
    }

    //デュアル開始するかどうか
    private boolean _GetOn;

    public boolean battleZoneGetOn() {
        return _GetOn;
    }

    public void setGetOn(boolean flag) {
        _GetOn = flag;
    }


    private boolean _End;

    public boolean battleZoneEnd() {
        return _End;
    }

    public void setBattleZoneEnd(boolean flag) {
        _End = flag;
    }
    //public int DuelCount;

    private int enddueltime;

    private boolean Close;

    protected ArrayList<L1PcInstance> battleZoneUser = new ArrayList<L1PcInstance>();

    public void addBattleZoneUser(L1PcInstance pc) {
        battleZoneUser.add(pc);
    }

    public void removeBattleZoneUser(L1PcInstance pc) {
        battleZoneUser.remove(pc);
    }

    public void clearBattleZoneUser() {
        battleZoneUser.clear();
    }

    public boolean isBattleZoneUser(L1PcInstance pc) {
        return battleZoneUser.contains(pc);
    }

    public int getBattleZoneUserCount() {
        return battleZoneUser.size();
    }

    private boolean GmStart = false;

    public void setGmStart(boolean ck) {
        GmStart = ck;
    }

    public boolean getGmStart() {
        return GmStart;
    }


    public L1PcInstance[] toArrayBattleZoneUser() {
        return battleZoneUser.toArray(new L1PcInstance[battleZoneUser.size()]);
    }

    public static BattleZone getInstance() {
        if (_instance == null) {
            _instance = new BattleZone();
        }
        return _instance;
    }


    @Override
    public void run() {
        try {
            while (true) {
                try {
                    if (battleZoneEnd() == true) {
                        Thread.sleep(1000 * 60 * 60 * 2); //2時間の待機時間
                        setBattleZoneEnd(false);
                    } else {
                        checkDuelTime(); // デュアル可能時間をチェック
                        if (battleZoneGetOn() == true) {
                            checkUser();
                        }
                        Thread.sleep(1000);
                    }
                } catch (Exception e) {
                }
            }
        } catch (Exception e1) {
        }
    }

    private void checkUser() {
        L1PcInstance[] pc = toArrayBattleZoneUser();
        for (int i = 0; i < pc.length; i++) {
            if (pc[i] == null)
                continue;

            if (pc[i].getMapId() == 5001 || pc[i].getMapId() == 5153) {
                continue;
            } else {
                if (isBattleZoneUser(pc[i])) {
                    removeBattleZoneUser(pc[i]);
                }
                pc[i].set_DuelLine(0);
            }
        }
    }

    //デュアル時間チェック
    public void checkDuelTime() {
        //ゲームの時間を受けています。
        try {
            int servertime = RealTimeClock.getInstance().getRealTime().getSeconds();
            //現在の時刻
            int nowdueltime = servertime % 86400;
            int count1 = 0;
            int count2 = 0;
            int winLine = 4;
            if (getDuelStart() == false) {
                if (getGmStart()) {
                    setDuelOpen(true);
                    setDuelStart(true);
                    wait3MinuteEntry();
                }
                if (battleZoneGetOn() == true) {
                    L1PcInstance[] c = toArrayBattleZoneUser();
                    for (int i = 0; i < c.length; i++) {
                        if (c[i].getMapId() == 5001) {
                            if (!c[i].isDead()) {
                                entryBattleZone(c[i]);
                            }
                        }
                    }
                    setDuelStart(true);
                    //終了時間を指定
                    enddueltime = nowdueltime + 600; //10分後の終了終了時間定めるところ

                }
            } else {
                //終了時間または強制終了であれば、
                if (nowdueltime >= enddueltime || Close == true) {
                    L1PcInstance[] c1 = toArrayBattleZoneUser();
                    for (int i = 0; i < c1.length; i++) {
                        if (c1[i].getMapId() == 5153) {
                            if (!c1[i].isDead()) {
                                if (c1[i].get_DuelLine() == 1) {
                                    count1 += 1;
                                } else {
                                    count2 += 1;
                                }
                            }
                        }
                    }
                    //優勝チェック
                    String ment = null;
                    if (count1 > count2) {
                        //1行目の優勝
                        winLine = 1;
                        ment = "プレミアムバトルゾーン「ブルー」のラインの勝利です。";
                        L1World.getInstance().broadcastServerMessage("\\fW* バトルゾーン終了！ 「ブルー」のラインの勝利です *");
                    } else if (count1 < count2) {
                        //2行目の優勝
                        winLine = 2;
                        ment = "プレミアムバトルゾーン」レッド「ラインの勝利です。";
                        L1World.getInstance().broadcastServerMessage("\\fW* バトルゾーン終了！ 「レッド」の行の勝利です *");
                    } else {
                        winLine = 3;
                        ment = "プレミアムバトルゾーン「ブルー」のラインと「レッド」のラインが引き分けた。";
                        L1World.getInstance().broadcastServerMessage("\\fW* バトルゾーン終了！ 「ブルー」のラインと「レッド」のラインがタイです *");
                    }

                    L1PcInstance[] c2 = toArrayBattleZoneUser();
                    for (int i = 0; i < c2.length; i++) {
                        if (c2[i] == null) continue;
                        if (c2[i].get_DuelLine() != 0) {
                            c2[i].sendPackets(new S_SystemMessage(ment));//コメントの修正
                            //勝ったラインにアイテム支給
                            if (c2[i].get_DuelLine() == winLine) {
                                String[] itemIds = null;
                                try {
                                    int idx = Config.BATTLE_ZONE_ITEM.indexOf(",");
                                    //、である場合
                                    if (idx > -1) {
                                        itemIds = Config.BATTLE_ZONE_ITEM.split(",");
                                    } else {
                                        itemIds = new String[1];
                                        itemIds[0] = Config.BATTLE_ZONE_ITEM;
                                    }
                                } catch (Exception e) {
                                }
                                //支給するアイテムの数
                                String[] counts = null;
                                try {
                                    int idx = Config.BATTLE_ZONE_ITEM_COUNT.indexOf(",");
                                    // 、である場合
                                    if (idx > -1) {
                                        counts = Config.BATTLE_ZONE_ITEM_COUNT.split(",");
                                    } else {
                                        counts = new String[1];
                                        counts[0] = Config.BATTLE_ZONE_ITEM_COUNT;
                                    }
                                } catch (Exception e) {
                                }
                                // アイテム名やカウントがない場合
                                if (itemIds == null || counts == null)
                                    return;
                                for (int j = 0; j < itemIds.length; j++) {
                                    int itemId = 0;
                                    int count = 0;
                                    itemId = Integer.parseInt(itemIds[j]);
                                    count = Integer.parseInt(counts[j]);
                                    if (itemId <= 0 || count <= 0)
                                        continue;
                                    L1ItemInstance item = c2[i].getInventory().storeItem(itemId, count);
                                    if (item != null)
                                        c2[i].sendPackets(new S_SystemMessage(item.getName() + " (" + count + "）を獲得しました。"));
                                }
                                c2[i].sendPackets(new S_SystemMessage("\\fU* 勝利チームにアイテムが支給されました *"));
                            }


                            deleteMiniHp(c2[i]);
                            c2[i].set_DuelLine(0);
                            //バトルゾーンであれば、
                            if (c2[i].getMapId() == 5153 || c2[i].getMapId() == 5001) {
                                if (!c2[i].isDead()) {
                                    new L1Teleport().teleport(c2[i], 33090, 33402, (short) 4, 0, true);//
                                }
                            }
                        }
                    }
                    ment = null;
                    Announcements.getInstance().announceToAll("\\fW* プレミアムバトルゾーンに終了しました *");
                    //Announcements.getInstance().announceToAll("\\fW*バトルゾーンは3時間間隔で表示されます* "）;
                    setBattleZoneEnd(true);
                    setGetOn(false);
                    setDuelStart(false);
                    //	DuelCount = 0;
                    Close = false;
                    battleZoneUser.clear();
                    setGmStart(false);
                } else {
                    //立場が閉鎖された場合
                    if (!getDuelOpen()) {
                        int count3 = 0;
                        int count4 = 0;
                        L1PcInstance[] c3 = toArrayBattleZoneUser();
                        for (int i = 0; i < c3.length; i++) {
                            if (c3[i] == null) continue;
                            //バトルゾーンであれば、
                            if (c3[i].getMapId() == 5153) {
                                if (!c3[i].isDead()) {//死なないユーザチェック
                                    if (c3[i].get_DuelLine() == 1) {
                                        count3 += 1;
                                    } else if (c3[i].get_DuelLine() == 2) {
                                        count4 += 1;
                                    } else {
                                        removeBattleZoneUser(c3[i]);
                                    }
                                }
                            }
                        }

                        //残りのユーザが0名であるとき強制終了実行<<
                        if (count3 == 0 || count4 == 0) {
                            Close = true;
                        }
                    }

                }

            }
        } catch (Exception e) {
        }
    }

    private void createMiniHp(L1PcInstance pc) {
        // バトル時、お互いのHPを表示させる
        for (L1PcInstance member : BattleZone.getInstance().toArrayBattleZoneUser()) {
            // 同じラインにhp表示
            if (member != null) {
                if (pc.get_DuelLine() == member.get_DuelLine()) {
                    member.sendPackets(new S_HPMeter(pc));
                    pc.sendPackets(new S_HPMeter(member));
                }
            }
        }
    }

    ////バトルゾーン変身////////
    private void polyBattleZone(L1PcInstance pc) {
        if (pc == null)
            return;
        int DuelLine = pc.get_DuelLine();
        int polyid = 0;
        int time = 1800;
        if (pc != null) {
            if (pc.isKnight() || pc.isCrown() || pc.isDarkelf() || pc.isDragonknight() || pc.isWarrior()) {
                // ナイト君主ダークエルフのナイト
                if (DuelLine == 1) {
                    polyid = 11232;// <<1行目に変身ダーク>
                } else {
                    polyid = 11236;// 2行目アーク変身
                }
                L1PolyMorph.doPoly(pc, polyid, time, 2);
            }
            // ウィザードイリュージョニスト
            if (pc.isWizard() || pc.isBlackwizard()) {
                if (DuelLine == 1) {
                    polyid = 11232;
                } else {
                    polyid = 11236;
                }
                L1PolyMorph.doPoly(pc, polyid, time, 2);
            }
            // 妖精
            if (pc.isElf()) {
                if (DuelLine == 1) {
                    polyid = 11232;
                } else {
                    polyid = 11236;
                }
                L1PolyMorph.doPoly(pc, polyid, time, 2);
            }
        }
    }


    private void entryBattleZone(L1PcInstance pc) {
        try {
            polyBattleZone(pc);
            createMiniHp(pc);
            if (pc.get_DuelLine() == 1) {
                int ranx = 32628 + _random.nextInt(4);
                int rany = 32896 + _random.nextInt(5);
                new L1Teleport().teleport(pc, ranx, rany, (short) 5153, 1, true);
            } else {
                int ranx2 = 32650 - _random.nextInt(4);
                int rany2 = 32893 + _random.nextInt(5);
                new L1Teleport().teleport(pc, ranx2, rany2, (short) 5153, 5, true);
            }

            setGetOn(false);
        } catch (Exception e) {
        }
    }


    public void wait3MinuteEntry() {
        try {
            Announcements.getInstance().announceToAll("3分後にチームプレミアムバトルゾーンを開催します。");
            Announcements.getInstance().announceToAll("入場は先着順でギラン村ですることができます。");
            try {
                Thread.sleep(1000 * 120);
            } catch (Exception e) {
            }
            Announcements.getInstance().announceToAll("1分後にプレミアムバトルゾーン入場を終えています。");
            Announcements.getInstance().announceToAll("ギラン「プレミアムバトルゾーン」を通じた入場が可能です。");
            try {
                Thread.sleep(1000 * 50);
            } catch (Exception e) {
            }
            Announcements.getInstance().announceToAll("プレミアムバトルゾーン入場締め切り10秒残りました。");
            try {
                Thread.sleep(1000 * 10);
            } catch (Exception e) {
            }
            if (getDuelOpen()) {
                setDuelOpen(false);
            }
            Announcements.getInstance().announceToAll("プレミアムバトルゾーンの立場を終えました。");
            try {
                Thread.sleep(1000 * 5);
            } catch (Exception e) {
            }
            setGetOn(true);
            setGmStart(true);
        } catch (Exception e) {
        }
    }

    private void deleteMiniHp(L1PcInstance pc) {
        // バトル終了時、HPバーを削除する。
        for (L1PcInstance member : pc.getKnownPlayers()) {
            //同じラインにhp表示
            if (member != null) {
                if (pc.get_DuelLine() == member.get_DuelLine()) {
                    pc.sendPackets(new S_HPMeter(member.getId(), 0xff, 0xff));
                    member.sendPackets(new S_HPMeter(pc.getId(), 0xff, 0xff));
                }
            }
        }
    }


}
