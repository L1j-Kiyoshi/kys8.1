package l1j.server.server.Controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import l1j.server.Config;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_SystemMessage;

public class AdenaHuntController extends Thread {

private static AdenaHuntController _instance;

private boolean _AdenaHuntStart;

public boolean getAdenaHuntStart() {
	return _AdenaHuntStart;
	}

public void setAdenaHuntStart(boolean AdenaHunt) {
	_AdenaHuntStart = AdenaHunt;
	}

  private static long sTime = 0;

  public boolean isGmOpen4 = false;

  private String NowTime = "";

  private static final int ADENTIME = Config.ADEN_HUNTING_TIME;

  private static final SimpleDateFormat s = new SimpleDateFormat("HH", Locale.KOREA);

  private static final SimpleDateFormat ss = new SimpleDateFormat("MM-dd HH:mm", Locale.KOREA);

  public static AdenaHuntController getInstance() {
	  if(_instance == null) {
		  _instance = new AdenaHuntController();
	  }
	  return _instance;
  }

  @Override
  public void run() {
	  try {
		  while (true) {
			  Thread.sleep(1000);
			  /** オープン **/
			  if(!isOpen6() && !isGmOpen4)
				  continue;
			  if(L1World.getInstance().getAllPlayers().size() <= 0)
				  continue;

			  isGmOpen4 = false;

			  L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "[お知らせ]アデン狩り場が開かれました。"));
			  L1World.getInstance().broadcastServerMessage("\\aH............ アデン狩り場が開かれました。 ............");
			  L1World.getInstance().broadcastServerMessage("\\aH............  急いで入場してください！  ............");

			  setAdenaHuntStart(true);

			  Thread.sleep(3800000L); //60分程度

			  TelePort3();
			  Thread.sleep(5000L);
			  TelePort4();

			  /** 終了**/
			  End();
		  }

	  } catch(Exception e){
		  e.printStackTrace();
	  }
  }

   /**
    *オープン時刻を持って来る
    *
    *@return (Strind) オープン時刻(MM-dd HH:mm)
    */
    public String AdenOpen() {
     Calendar c = Calendar.getInstance();
     c.setTimeInMillis(sTime);
     return ss.format(c.getTime());
    }

    /**
    *マップが開いていることを確認
    *
    *@return (boolean) 開いている場合true閉じている場合false
    */
    private boolean isOpen6() {
     NowTime = getTime();
     if((Integer.parseInt(NowTime) % ADENTIME) == 0) return true;
     return false;
    }

   //悪魔王の領土ソース参照して追加する。
    public boolean isOpen7() {
		NowTime = getTime();
		if ((Integer.parseInt(NowTime) % ADENTIME) >= 2
				&& (Integer.parseInt(NowTime) % ADENTIME) <= 8)
			return true;
		return false;
	}

    /**
    *実際、現在時刻を持って来る
    *
    *@return (String)現在時刻（HH：mm）
    */
    private String getTime() {
     return s.format(Calendar.getInstance().getTime());
    }

    /**ギラン村にティンギが**/
    private void TelePort3() {
     for(L1PcInstance c : L1World.getInstance().getAllPlayers()) {
      switch(c.getMap().getId()) {
       case 701: //アデン狩り場
       c.stopHpRegenerationByDoll();
       c.stopMpRegenerationByDoll();
       new L1Teleport().teleport(c, 32617, 32773, (short) 4, c.getHeading(), true);
       c.sendPackets(new S_SystemMessage("アデン狩り場が終了しました。"));
       break;
       default:
       break;
      }
     }
    }

    /**ギラン村にティンギが**/
    private void TelePort4() {
     for(L1PcInstance c : L1World.getInstance().getAllPlayers()) {
      switch(c.getMap().getId()) {
       case 701: //アデン狩り場
       c.stopHpRegenerationByDoll();
       c.stopMpRegenerationByDoll();
       new L1Teleport().teleport(c, 32617, 32773, (short) 4, c.getHeading(), true);
       c.sendPackets(new S_SystemMessage("アデン狩り場が終了しました。"));
       break;
       default:
       break;
      }
     }
    }

    /** 終了 **/
    public void End() {
    	L1World.getInstance().broadcastServerMessage("\\fSアデン狩り場が終了しました。");

    	L1World.getInstance().broadcastServerMessage("\\fSアデン狩り場は ["+Config.ADEN_HUNTING_TIME+"時間]間隔で行われます。");
    	setAdenaHuntStart(false);
    }
}

