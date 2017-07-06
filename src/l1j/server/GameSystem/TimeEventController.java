package l1j.server.GameSystem;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import l1j.server.Config;
import l1j.server.server.model.L1World;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.utils.L1SpawnUtil;

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
 private static final int LOOP = 19; // 저녁7시에 시작
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
      "\\fW[이벤트관리자] : 안녕하세요~! 곧 타임이벤트가 시작됩니다.");
    try {
     Thread.sleep(2000L);
    } catch (Exception e) {
    }
    L1World.getInstance().broadcastPacketToAll(
      new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
        "\\fC[이벤트관리자] : 자~ 어떤 이벤트가 걸릴까요?"));
    L1World.getInstance().broadcastServerMessage(
      "\\fW[이벤트관리자] : 이벤트를 추첨중입니다.!");
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
    while (t <= 10) {  //5시간동안 30분씩 진행시키고 10번 돌리자
     try {
      Thread.sleep(1000 * 60 * 30L);
     } catch (Exception e) {
     }
     TimeEventMent(i);
     ++t;
    }
    L1World.getInstance().broadcastServerMessage(
      "\\fW[이벤트관리자] 잠시후 타임이벤트가 변경됩니다.");
    L1World.getInstance().broadcastServerMessage(
      "\\fW[이벤트관리자] 한가지 이벤트가 끝나면  랜덤으로 다시 진행됩니다.");
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
  if ((Integer.parseInt(NowTime) % LOOP) == 0) //매일 저녁7시에 시작된다
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

 

// 타임이벤트 시작 메시지 + 효과

 private void TimeEventGo(int i) {
  switch (i) {
  case 0:
   Config.RATE_XP *= 1.1;
   L1World.getInstance().broadcastPacketToAll(
     new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
       "\\fC[경험치이벤트] : 경험치 10%상승"));
   L1World.getInstance().broadcastServerMessage(
     "\\fU[이벤트관리자] 지금부터 경험치획득량이 10% 증가됩니다.");
   break;
  case 1:
   Config.RATE_KARMA *= 1.3;
   L1World.getInstance().broadcastPacketToAll(
     new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
       "\\fC[우호도이벤트] : 우호도 30%상승"));
   L1World.getInstance().broadcastServerMessage(
     "\\fU[이벤트관리자] 지금부터 우호도획득량이 30% 증가됩니다.");
   break;
  case 2:
   Config.RATE_XP *= 1.2;
   L1World.getInstance().broadcastPacketToAll(
     new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
       "\\fC[경험치이벤트] : 경험치 20%상승"));
   L1World.getInstance().broadcastServerMessage(
     "\\fU[이벤트관리자] 지금부터 경험치획득량이 20% 증가됩니다.");
   break;
  case 3:
   Config.RATE_DROP_ADENA *= 1.1;
   L1World.getInstance().broadcastPacketToAll(
     new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
       "\\fC[아데나이벤트] : 아데나 10%상승"));
   L1World.getInstance().broadcastServerMessage(
     "\\fU[이벤트관리자] 지금부터 아데나획득량이 10% 증가됩니다.");
   break;
  case 4:
   Config.RATE_DROP_ADENA *= 1.2;
   L1World.getInstance().broadcastPacketToAll(
     new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
       "\\fC[아데나이벤트] : 아데나 20%상승"));
   L1World.getInstance().broadcastServerMessage(
     "\\fU[이벤트관리자] 지금부터 아데나획득량이 20% 증가됩니다.");
   break;
  case 5:
   Config.RATE_DROP_ADENA *= 1.3;
   L1World.getInstance().broadcastPacketToAll(
     new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
       "\\fC[아데나이벤트] : 아데나 30%상승"));
   L1World.getInstance().broadcastServerMessage(
     "\\fU[이벤트관리자] 지금부터 아데나획득량이 30% 증가됩니다.");
   break;
  case 6:
   Config.FEATHER_NUM *= 1.1;
   L1World.getInstance().broadcastPacketToAll(
     new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
       "\\fC[깃털이벤트] : 깃털 10%추가지급"));
   L1World.getInstance().broadcastServerMessage(
     "\\fU[이벤트관리자] 지금부터 깃털지급량이 10% 증가됩니다.");
   break;
  case 7:
   Config.ENCHANT_CHANCE_WEAPON *= 1.1;
   L1World.getInstance().broadcastPacketToAll(
     new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
       "\\fC[인첸이벤트] : 무기인첸률 10%증가"));
   L1World.getInstance().broadcastServerMessage(
     "\\fU[이벤트관리자] 지금부터 무기인첸률이 10% 증가합니다.");
   break;
  case 8:
   Config.FEATHER_NUM *= 1.2;
   L1World.getInstance().broadcastPacketToAll(
     new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
       "\\fC[깃털이벤트] : 깃털 20%추가지급"));
   L1World.getInstance().broadcastServerMessage(
     "\\fU[이벤트관리자] 지금부터 깃털지급량이 20% 증가됩니다.");
   break;
  case 9:
   Config.RATE_DROP_ITEMS *= 1.2;
   L1World.getInstance().broadcastPacketToAll(
     new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
       "\\fC[득템이벤트] : 드랍률 20%증가"));
   L1World.getInstance().broadcastServerMessage(
     "\\fU[이벤트관리자] 지금부터 드랍률이 20% 증가됩니다.");
   break;
  case 10:
   Config.ENCHANT_CHANCE_ARMOR *= 1.1;
   L1World.getInstance().broadcastPacketToAll(
     new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
       "\\fC[인첸이벤트] : 방어구인첸률 10%증가]"));
   L1World.getInstance().broadcastServerMessage(
     "\\fU[이벤트관리자] 지금부터 방어구인첸률이 10% 증가합니다.");
   break;
  case 11:
   Config.RATE_DROP_ITEMS *= 1.3;
   L1World.getInstance().broadcastPacketToAll(
     new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
       "\\fC[득템이벤트] : 드랍률 30%증가"));
   L1World.getInstance().broadcastServerMessage(
     "\\fU[이벤트관리자] 지금부터 드랍률이 30% 증가됩니다.");
   break;
  }
 }

 

// 타임이벤트 진행 메시지를 보낸다

 private void TimeEventMent(int i) {
  switch (i) {
  case 0:
   L1World.getInstance().broadcastPacketToAll(
     new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
       "\\fC[경험치이벤트 : 경험치10%증가]"));
   break;
  case 1:
   L1World.getInstance().broadcastPacketToAll(
     new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
       "\\fC[우호도이벤트 : 우호도30%추가획득]"));
   break;
  case 2:
   L1World.getInstance().broadcastPacketToAll(
     new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
       "\\fC[경험치이벤트 : 경험치20%증가]"));
   break;
  case 3:
   L1World.getInstance().broadcastPacketToAll(
     new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
       "\\fC[아데나이벤트 : 아데나10%증가]"));
   break;
  case 4:
   L1World.getInstance().broadcastPacketToAll(
     new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
       "\\fC[아데나이벤트 : 아데나20%증가]"));
   break;
  case 5:
   L1World.getInstance().broadcastPacketToAll(
     new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
       "\\fC[아데나이벤트 : 아데나30%증가]"));
   break;
  case 6:
   L1World.getInstance().broadcastPacketToAll(
     new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
       "\\fC[깃털이벤트 : 깃털10%추가지급]"));
   break;
  case 7:
   L1World.getInstance().broadcastPacketToAll(
     new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
       "\\fC[인첸트이벤트 : 무기인첸률10%증가]"));
   break;
  case 8:
   L1World.getInstance().broadcastPacketToAll(
     new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
       "\\fC[깃털이벤트 : 깃털20%추가지급]"));
   break;
  case 9:
   L1World.getInstance().broadcastPacketToAll(
     new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
       "\\fC[드랍이벤트 : 드랍률20%증가]"));
   break;
  case 10:
   L1World.getInstance().broadcastPacketToAll(
     new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
       "\\fC[인첸트이벤트 : 방어구인첸률10%증가]"));
   break;
  case 11:
   L1World.getInstance().broadcastPacketToAll(
     new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
       "\\fC[드랍이벤트 : 드랍률30%증가]"));
   break;
  }
 }

 

// 타임이벤트 종료 메시지 + 효과

 private void TimeEventStop(int i) {
  switch (i) {
  case 0:
   Config.RATE_XP /= 1.1;
   L1World.getInstance().broadcastPacketToAll(
     new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
       "\\fC경험치 증가 효과가 사라집니다."));
   L1World.getInstance().broadcastServerMessage(
     "\\fU[타임이벤트] 경험치 증가 효과가 사라집니다.");
   break;
  case 1:
   Config.RATE_KARMA /= 1.3;
   L1World.getInstance().broadcastPacketToAll(
     new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
       "\\fC우호도 증가 효과가 사라집니다."));
   L1World.getInstance().broadcastServerMessage(
     "\\fU[타임이벤트] 우호도 증가 효과가 사라집니다.");
   break;
  case 2:
   Config.RATE_XP /= 1.2;
   L1World.getInstance().broadcastPacketToAll(
     new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
       "\\fC경험치 증가 효과가 사라집니다."));
   L1World.getInstance().broadcastServerMessage(
     "\\fU[타임이벤트] 경험치 증가 효과가 사라집니다.");
   break;
  case 3:
   Config.RATE_DROP_ADENA /= 1.1;
   L1World.getInstance().broadcastPacketToAll(
     new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
       "\\fC아데나드랍률 증가 효과가 사라집니다."));
   L1World.getInstance().broadcastServerMessage(
     "\\fU[타임이벤트] 아데나드랍률 증가 효과가 사라집니다.");
   break;
  case 4:
   Config.RATE_DROP_ADENA /= 1.2;
   L1World.getInstance().broadcastPacketToAll(
     new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
       "\\fC아데나드랍률 증가 효과가 사라집니다."));
   L1World.getInstance().broadcastServerMessage(
     "\\fU[타임이벤트] 아데나드랍률 증가 효과가 사라집니다.");
   break;
  case 5:
   Config.RATE_DROP_ADENA /= 1.3;
   L1World.getInstance().broadcastPacketToAll(
     new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
       "\\fC아데나드랍률 증가 효과가 사라집니다."));
   L1World.getInstance().broadcastServerMessage(
     "\\fU[타임이벤트] 아데나드랍률 증가 효과가 사라집니다.");
   break;
  case 6:
   Config.FEATHER_NUM /= 1.1;
   L1World.getInstance().broadcastPacketToAll(
     new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
       "\\fC깃털 추가지급 효과가 사라집니다."));
   L1World.getInstance().broadcastServerMessage(
     "\\fU[타임이벤트] 깃털 추가지급 효과가 사라집니다.");
   break;
  case 7:
   Config.ENCHANT_CHANCE_WEAPON /= 1.1;
   L1World.getInstance().broadcastPacketToAll(
     new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
       "\\fC무기인첸률 증가 효과가 사라집니다."));
   L1World.getInstance().broadcastServerMessage(
     "\\fU[타임이벤트] 무기인첸률 증가 효과가 사라집니다.");
   break;
  case 8:
   Config.FEATHER_NUM /= 1.2;
   L1World.getInstance().broadcastPacketToAll(
     new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
       "\\fC깃털 주가지급 효과가 사라집니다."));
   L1World.getInstance().broadcastServerMessage(
     "\\fU[타임이벤트] 깃털 주가지급 효과가 사라집니다.");
   break;
  case 9:
   Config.RATE_DROP_ITEMS /= 1.2;
   L1World.getInstance().broadcastPacketToAll(
     new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
       "\\fC아이템드랍률 증가 효과가 사라집니다"));
   L1World.getInstance().broadcastServerMessage(
     "\\fU[타임이벤트] 아이템드랍률 증가 효과가 사라집니다.");
   break;
  case 10:
   Config.ENCHANT_CHANCE_ARMOR /= 1.1;
   L1World.getInstance().broadcastPacketToAll(
     new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
       "\\fC방어구인첸률 증가 효과가 사라집니다."));
   L1World.getInstance().broadcastServerMessage(
     "\\fU[타임이벤트] 방어구인첸률 증가 효과가 사라집니다.");
   break;
  case 11:
   Config.RATE_DROP_ITEMS /= 1.3;
   L1World.getInstance().broadcastPacketToAll(
     new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
       "\\fC아이템드랍률 증가 효과가 사라집니다"));
   L1World.getInstance().broadcastServerMessage(
     "\\fU[타임이벤트] 아이템드랍률 증가 효과가 사라집니다.");
   break;
  }
  setTimeEventStart(false);
  Close = false;
 }
}

// 실시간 배율 변동 끝

