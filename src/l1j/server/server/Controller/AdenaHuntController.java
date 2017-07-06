package l1j.server.server.Controller;

import java.util.Calendar;
import java.util.Locale;
import java.text.SimpleDateFormat;

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
  
  private static final int ADENTIME = Config.아덴사냥터시간;
  
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
			  /** 오픈 **/
			  if(!isOpen6() && !isGmOpen4)
				  continue;
			  if(L1World.getInstance().getAllPlayers().size() <= 0)
				  continue;
			  
			  isGmOpen4 = false;
			  
			  L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "[공지] 아덴사냥터가 열렸습니다."));
			  L1World.getInstance().broadcastServerMessage("\\aH............ 아덴사냥터가 열렸습니다. ............");
			  L1World.getInstance().broadcastServerMessage("\\aH............  서둘러 입장해주세요!  ............");
			  
			  setAdenaHuntStart(true);
			  
			  Thread.sleep(3800000L); //60분정도
			  
			  TelePort3();
			  Thread.sleep(5000L);
			  TelePort4();
			  
			  /** 종료 **/
			  End();
		  }
		  
	  } catch(Exception e){
		  e.printStackTrace();
	  }
  }

   /**
    *오픈 시각을 가져온다
    *
    *@return (Strind) 오픈 시각(MM-dd HH:mm)
    */
    public String AdenOpen() {
     Calendar c = Calendar.getInstance();
     c.setTimeInMillis(sTime);
     return ss.format(c.getTime());
    }

    /**
    *맵이 열려있는지 확인
    *
    *@return (boolean) 열려있다면 true 닫혀있다면 false
    */
    private boolean isOpen6() {
     NowTime = getTime();
     if((Integer.parseInt(NowTime) % ADENTIME) == 0) return true;
     return false;
    }
    
   //악마왕영토 소스 참조하여 추가함.
    public boolean isOpen7() {
		NowTime = getTime();
		if ((Integer.parseInt(NowTime) % ADENTIME) >= 2 
				&& (Integer.parseInt(NowTime) % ADENTIME) <= 8)
			return true;
		return false;
	}
    
    /**
    *실제 현재시각을 가져온다
    *
    *@return (String) 현재 시각(HH:mm)
    */
    private String getTime() {
     return s.format(Calendar.getInstance().getTime());
    }

    /**기란마을로 팅기게**/
    private void TelePort3() {
     for(L1PcInstance c : L1World.getInstance().getAllPlayers()) {
      switch(c.getMap().getId()) {
       case 701: //아덴사냥터
       c.stopHpRegenerationByDoll();
       c.stopMpRegenerationByDoll();
       new L1Teleport().teleport(c, 32617, 32773, (short) 4, c.getHeading(), true);
       c.sendPackets(new S_SystemMessage("아덴사냥터가 종료되었습니다."));
       break;
       default:
       break;
      }
     }
    }
    
    /**기란마을로 팅기게**/
    private void TelePort4() {
     for(L1PcInstance c : L1World.getInstance().getAllPlayers()) {
      switch(c.getMap().getId()) {
       case 701: //아덴사냥터
       c.stopHpRegenerationByDoll();
       c.stopMpRegenerationByDoll();
       new L1Teleport().teleport(c, 32617, 32773, (short) 4, c.getHeading(), true);
       c.sendPackets(new S_SystemMessage("아덴사냥터가 종료되었습니다."));
       break;
       default:
       break;
      }
     }
    }

    /** 종료 **/
    public void End() {
    	L1World.getInstance().broadcastServerMessage("\\fS아덴사냥터가 종료되었습니다.");
    	
    	L1World.getInstance().broadcastServerMessage("\\fS아덴사냥터는 ["+Config.아덴사냥터시간+" 시간]간격으로 진행됩니다.");
    	setAdenaHuntStart(false);
    }
}

