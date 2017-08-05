package l1j.server.GameSystem;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

import l1j.server.Config;
import l1j.server.server.model.L1World;
import l1j.server.server.serverpackets.S_PacketBox;

@SuppressWarnings("unused")
public class TimeEventController extends Thread {
    private static TimeEventController _instance;
    private boolean _TimeEventStart;
    private boolean _TimeEventOpen;
    private boolean _TimeEventTime;
    private int _TimeEventTing;
    private boolean Close;
    private static long sTime = 0L;
    private String NowTime = "";
    private static final int LOOP = 19; //夕方7時に始まる
    private static final SimpleDateFormat s = new SimpleDateFormat("HH",
            Locale.KOREA);

    private static final SimpleDateFormat ss = new SimpleDateFormat(
            "MM-dd HH:mm", Locale.KOREA);

    private static Random _random = new Random(System.nanoTime());

    public boolean getTimeEventStart() {
        return _TimeEventStart;
    }

    public void setTimeEventStart(boolean timeevent) {
        _TimeEventStart = timeevent;
    }

    public boolean getTimeEventOpen() {
        return _TimeEventOpen;
    }

    public void setTimeEventOpen(boolean timeevent) {
        _TimeEventOpen = timeevent;
    }

    public boolean getTimeEventTime() {
        return _TimeEventTime;
    }

    public void setTimeEventTime(boolean timeevent) {
        _TimeEventTime = timeevent;
    }

    public int getTimeEventTing() {
        return _TimeEventTing;
    }

    public void setTimeEventTing(int i) {
        _TimeEventTing = i;
    }

    public static TimeEventController getInstance() {
        if (_instance == null) {
            _instance = new TimeEventController();
        }
        return _instance;
    }


    public void run() {
        try {
            while (true) {
                try {
                    Thread.sleep(10000L);
                } catch (Exception e) {
                }
                if (!isOpen())
                    continue;
                if (L1World.getInstance().getAllPlayers().size() <= 0)
                    continue;
                L1World.getInstance().broadcastServerMessage(
                        "\\fW[イベントマネージャ]：こんにちは〜！すぐにタイムイベントが開始されます。");
                try {
                    Thread.sleep(2000L);
                } catch (Exception e) {
                }
                L1World.getInstance().broadcastPacketToAll(
                        new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
                                "\\fC[イベントマネージャ]：文字〜どのようなイベントがかかりますか？"));
                L1World.getInstance().broadcastServerMessage(
                        "\\fW[イベントマネージャ]：イベントを抽選しています！");
                try {
                    Thread.sleep(5000L);
                } catch (Exception e) {
                }
                setTimeEventTime(false);
                setTimeEventOpen(true);
                setTimeEventTime(true);
                setTimeEventStart(true);
                int i = TimeEventChoice();
                TimeEventGo(i);
                setTimeEventTing(i);
                int t = 0;
                while (t <= 10) {  //5時間30分進行させ、10回回そう
                    try {
                        Thread.sleep(1000 * 60 * 30L);
                    } catch (Exception e) {
                    }
                    TimeEventMent(i);
                    ++t;
                }
                L1World.getInstance().broadcastServerMessage(
                        "\\fW[イベントマネージャ]しばらくして、タイムイベントが変更されます。");
                L1World.getInstance().broadcastServerMessage(
                        "\\fW[イベントマネージャ]の一つのイベントが終わるとランダムに再実行されます。");
                try {
                    Thread.sleep(30000L);
                } catch (Exception e) {
                }
                TimeEventStop(i);
            }
        } catch (Exception e1) {
        }
    }


    public String OpenTime() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(sTime);
        return ss.format(c.getTime());
    }


    private boolean isOpen() {
        NowTime = getTime();
        if ((Integer.parseInt(NowTime) % LOOP) == 0) //毎晩7時に始まる
            return true;
        return false;
    }


    private String getTime() {
        return s.format(Calendar.getInstance().getTime());
    }


    private int TimeEventChoice() {
        int i = _random.nextInt(12);
        return i;
    }


//タイムイベント開始メッセージ+効果

    private void TimeEventGo(int i) {
        switch (i) {
            case 0:
                Config.RATE_XP *= 1.1;
                L1World.getInstance().broadcastPacketToAll(
                        new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
                                "\\fC[経験値イベント]：経験値10％上昇"));
                L1World.getInstance().broadcastServerMessage(
                        "\\fU[イベントマネージャ]今から経験値獲得量が10％増加します。");
                break;
            case 1:
                Config.RATE_KARMA *= 1.3;
                L1World.getInstance().broadcastPacketToAll(
                        new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
                                "\\fC[友好度イベント]：友好度30％上昇"));
                L1World.getInstance().broadcastServerMessage(
                        "\\fU[イベントマネージャ]今から友好獲得量が30％増加します。");
                break;
            case 2:
                Config.RATE_XP *= 1.2;
                L1World.getInstance().broadcastPacketToAll(
                        new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
                                "\\fC[経験値イベント]：経験値20％上昇"));
                L1World.getInstance().broadcastServerMessage(
                        "\\fU[イベントマネージャ]今から経験値獲得量が20％増加します。");
                break;
            case 3:
                Config.RATE_DROP_ADENA *= 1.1;
                L1World.getInstance().broadcastPacketToAll(
                        new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
                                "\\fC[アデナイベント]：アデナ10％上昇"));
                L1World.getInstance().broadcastServerMessage(
                        "\\fU[イベントマネージャ]今からアデナ獲得量が10％増加します。");
                break;
            case 4:
                Config.RATE_DROP_ADENA *= 1.2;
                L1World.getInstance().broadcastPacketToAll(
                        new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
                                "\\fC[アデナイベント]：アデナ20％上昇"));
                L1World.getInstance().broadcastServerMessage(
                        "\\fU[イベントマネージャ]今からアデナ獲得量が20％増加します。");
                break;
            case 5:
                Config.RATE_DROP_ADENA *= 1.3;
                L1World.getInstance().broadcastPacketToAll(
                        new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
                                "\\fC[アデナイベント]：アデナ30％上昇"));
                L1World.getInstance().broadcastServerMessage(
                        "\\fU[イベントマネージャ]今からアデナ獲得量が30％増加します。");
                break;
            case 6:
                Config.FEATHER_NUM *= 1.1;
                L1World.getInstance().broadcastPacketToAll(
                        new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
                                "\\fC[羽イベント]：フェザー10％追加支給"));
                L1World.getInstance().broadcastServerMessage(
                        "\\fU[イベントマネージャ]今から羽支給量が10％増加します。");
                break;
            case 7:
                Config.ENCHANT_CHANCE_WEAPON *= 1.1;
                L1World.getInstance().broadcastPacketToAll(
                        new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
                                "\\fC[エンチャンイベント]：武器エンチャン率10％増加"));
                L1World.getInstance().broadcastServerMessage(
                        "\\fU[イベントマネージャ]今から武器エンチャン率が10％増加します。");
                break;
            case 8:
                Config.FEATHER_NUM *= 1.2;
                L1World.getInstance().broadcastPacketToAll(
                        new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
                                "\\fC[羽イベント]：フェザー20％追加支給"));
                L1World.getInstance().broadcastServerMessage(
                        "\\fU[イベントマネージャ]今から羽支給量が20％増加します。");
                break;
            case 9:
                Config.RATE_DROP_ITEMS *= 1.2;
                L1World.getInstance().broadcastPacketToAll(
                        new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
                                "\\fC[ドゥクテムイベント]：ドロップ率20％増加"));
                L1World.getInstance().broadcastServerMessage(
                        "\\fU[イベントマネージャ]今からドロップ率が20％増加します。");
                break;
            case 10:
                Config.ENCHANT_CHANCE_ARMOR *= 1.1;
                L1World.getInstance().broadcastPacketToAll(
                        new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
                                "\\fC[エンチャンイベント]：防具エンチャン率10％増加]"));
                L1World.getInstance().broadcastServerMessage(
                        "\\fU[イベントマネージャ]今から防具エンチャン率が10％増加します。");
                break;
            case 11:
                Config.RATE_DROP_ITEMS *= 1.3;
                L1World.getInstance().broadcastPacketToAll(
                        new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
                                "\\fC[ドゥクテムイベント]：ドロップ率30％増加"));
                L1World.getInstance().broadcastServerMessage(
                        "\\fU[イベントマネージャ]今からドロップ率が30％増加します。");
                break;
        }
    }


// タイムイベント進行メッセージを送る

    private void TimeEventMent(int i) {
        switch (i) {
            case 0:
                L1World.getInstance().broadcastPacketToAll(
                        new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
                                "\\fC[経験値イベント：経験値10％増加]"));
                break;
            case 1:
                L1World.getInstance().broadcastPacketToAll(
                        new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
                                "\\fC[友好度イベント：友好30％追加取得]"));
                break;
            case 2:
                L1World.getInstance().broadcastPacketToAll(
                        new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
                                "\\fC[経験値イベント：経験値20％増加]"));
                break;
            case 3:
                L1World.getInstance().broadcastPacketToAll(
                        new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
                                "\\fC[アデナイベント：アデナ10％増加]"));
                break;
            case 4:
                L1World.getInstance().broadcastPacketToAll(
                        new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
                                "\\fC[アデナイベント：アデナ20％増加]"));
                break;
            case 5:
                L1World.getInstance().broadcastPacketToAll(
                        new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
                                "\\fC[アデナイベント：アデナ30％増加]"));
                break;
            case 6:
                L1World.getInstance().broadcastPacketToAll(
                        new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
                                "\\fC[羽イベント：フェザー10％追加支給]"));
                break;
            case 7:
                L1World.getInstance().broadcastPacketToAll(
                        new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
                                "\\fC[エンチャントイベント：武器エンチャン率10％増加]"));
                break;
            case 8:
                L1World.getInstance().broadcastPacketToAll(
                        new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
                                "\\fC[羽イベント：フェザー20％追加支給]"));
                break;
            case 9:
                L1World.getInstance().broadcastPacketToAll(
                        new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
                                "\\fC[ドロップイベント：ドロップ率20％増加]"));
                break;
            case 10:
                L1World.getInstance().broadcastPacketToAll(
                        new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
                                "\\fC[エンチャントイベント：防具エンチャン率10％増加]"));
                break;
            case 11:
                L1World.getInstance().broadcastPacketToAll(
                        new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
                                "\\fC[ドロップイベント：ドロップ率30％増加]"));
                break;
        }
    }


//タイムイベント終了メッセージ+効果

    private void TimeEventStop(int i) {
        switch (i) {
            case 0:
                Config.RATE_XP /= 1.1;
                L1World.getInstance().broadcastPacketToAll(
                        new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
                                "\\fC経験値増加効果が消えます。"));
                L1World.getInstance().broadcastServerMessage(
                        "\\fU[タイムイベント]経験値増加効果が消えます。");
                break;
            case 1:
                Config.RATE_KARMA /= 1.3;
                L1World.getInstance().broadcastPacketToAll(
                        new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
                                "\\fC友好度増加の効果が消えます。"));
                L1World.getInstance().broadcastServerMessage(
                        "\\fU[タイムイベント]友好増加効果が消えます。");
                break;
            case 2:
                Config.RATE_XP /= 1.2;
                L1World.getInstance().broadcastPacketToAll(
                        new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
                                "\\fC経験値増加効果が消えます。"));
                L1World.getInstance().broadcastServerMessage(
                        "\\fU[タイムイベント]経験値増加効果が消えます。");
                break;
            case 3:
                Config.RATE_DROP_ADENA /= 1.1;
                L1World.getInstance().broadcastPacketToAll(
                        new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
                                "\\fCアデナドロップ率増加効果が消えます。"));
                L1World.getInstance().broadcastServerMessage(
                        "\\fU[タイムイベント]アデナドロップ率増加効果が消えます。");
                break;
            case 4:
                Config.RATE_DROP_ADENA /= 1.2;
                L1World.getInstance().broadcastPacketToAll(
                        new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
                                "\\fCアデナドロップ率増加効果が消えます。"));
                L1World.getInstance().broadcastServerMessage(
                        "\\fU[タイムイベント]アデナドロップ率増加効果が消えます。");
                break;
            case 5:
                Config.RATE_DROP_ADENA /= 1.3;
                L1World.getInstance().broadcastPacketToAll(
                        new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
                                "\\fCアデナドロップ率増加効果が消えます。"));
                L1World.getInstance().broadcastServerMessage(
                        "\\fU[タイムイベント]アデナドロップ率増加効果が消えます。");
                break;
            case 6:
                Config.FEATHER_NUM /= 1.1;
                L1World.getInstance().broadcastPacketToAll(
                        new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
                                "\\fC羽追加支給効果が消えます。"));
                L1World.getInstance().broadcastServerMessage(
                        "\\fU[タイムイベント]の羽を追加支給効果が消えます。");
                break;
            case 7:
                Config.ENCHANT_CHANCE_WEAPON /= 1.1;
                L1World.getInstance().broadcastPacketToAll(
                        new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
                                "\\fC武器エンチャン率増加効果が消えます。"));
                L1World.getInstance().broadcastServerMessage(
                        "\\fU[タイムイベント]武器エンチャン率増加効果が消えます。");
                break;
            case 8:
                Config.FEATHER_NUM /= 1.2;
                L1World.getInstance().broadcastPacketToAll(
                        new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
                                "\\fC羽株価支給効果が消えます。"));
                L1World.getInstance().broadcastServerMessage(
                        "\\fU[タイムイベント]羽株価支給効果が消えます。");
                break;
            case 9:
                Config.RATE_DROP_ITEMS /= 1.2;
                L1World.getInstance().broadcastPacketToAll(
                        new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
                                "\\fCアイテムドロップ率増加効果が消えます"));
                L1World.getInstance().broadcastServerMessage(
                        "\\fU[タイムイベント]アイテムドロップ率増加効果が消えます。");
                break;
            case 10:
                Config.ENCHANT_CHANCE_ARMOR /= 1.1;
                L1World.getInstance().broadcastPacketToAll(
                        new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
                                "\\fC防具エンチャン率増加効果が消えます。"));
                L1World.getInstance().broadcastServerMessage(
                        "\\fU[タイムイベント]防具エンチャン率増加効果が消えます。");
                break;
            case 11:
                Config.RATE_DROP_ITEMS /= 1.3;
                L1World.getInstance().broadcastPacketToAll(
                        new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
                                "\\fCアイテムドロップ率増加効果が消えます"));
                L1World.getInstance().broadcastServerMessage(
                        "\\fU[タイムイベント]アイテムドロップ率増加効果が消えます。");
                break;
        }
        setTimeEventStart(false);
        Close = false;
    }
}

// リアルタイム倍率変動終わり

