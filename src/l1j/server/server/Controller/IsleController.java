package l1j.server.server.Controller;

import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_SystemMessage;

public class IsleController implements Runnable {

    private static IsleController _instance;
    

    /** 게임오픈유무 **/
    public boolean isgameStart = false;

    /** 게임상태 **/
    public int Status = 0;//진행 상태
    private final int 대기 = 0;//진행
    private final int 오픈 = 1;
    private final int 진행 = 2;
    private final int 종료 = 3;//

    public static IsleController getInstance() {
        if (_instance == null) {
            _instance = new IsleController();
        }
        return _instance;
    }

    @Override
    public void run() {
        try {
            while (true) {
                switch (Status) {
                case 대기:
                    Thread.sleep(10000);//10초마다 현재오픈된상태인지체크
                    /** 오픈이 아니면 진행 **/
                    if (isgameStart == false) {//오픈아니면 다시되돌려보냄
                        continue;//되돌리기 
                    }
                    Status = 오픈;
                    L1World.getInstance().broadcastServerMessage("\\aH알림: 잠시후 [잊혀진 섬] 입장이 가능합니다.");
                    continue;
                case 오픈:
                	L1World.getInstance().broadcastServerMessage("\\aH알림: 잊혀진 섬 1시간 사냥가능하오니, 조심하시오.");
    				L1World.getInstance().broadcastServerMessage("\\aH알림: 시간이 되면 강제귀환 됩니다.");
                	System.out.println("...... 잊혀진섬 열림");
                	Status = 진행;
                    continue;
                case 진행:
                   
                	/** 실행 1시간 시작 **/
                	Thread.sleep(3800000L);  // 3800000L 1시간 10분정도
    				/** 1시간 후 자동 텔레포트 **/
    				TelePort();
    				close(); //추가
    				Thread.sleep(5000L);
    				TelePort2();
                    Status = 종료;
                    continue;
                case 종료:
                	//L1World.getInstance().broadcastServerMessage("\\aG지옥사냥터는 2시간동안 사냥가능합니다.");
                	L1World.getInstance().broadcastServerMessage("\\aH알림: 잊혀진섬이 종료되었습니다.");
                	System.out.println("...... 잊섬 종료됨");
                    isgameStart = false;
                    Status = 대기;
            		//delenpc(1231231);
                    continue;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
	private void TelePort() {
		for (L1PcInstance c : L1World.getInstance().getAllPlayers()) {
			switch (c.getMap().getId()) {
			case 1700:
			case 1703:
				c.stopHpRegenerationByDoll();
				c.stopMpRegenerationByDoll();
				new L1Teleport().teleport(c, 33970, 33246, (short) 4, 4, true);
				c.sendPackets(new S_SystemMessage("잊혀진섬이 닫혔습니다."));
				break;
			default:
				break;
			}
		}
	}
	/**캐릭터가 죽었다면 종료시키기**/
	 private void close() {
	  for(L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
	   if (pc.getMap().getId() >= 1700 && pc.getMap().getId() <= 1703 && pc.isDead()) {
	    pc.stopHpRegenerationByDoll();
	    pc.stopMpRegenerationByDoll();
	    pc.sendPackets(new S_Disconnect());
	   }
	  }
	 }

	/** 아덴마을로 팅기게* */
	private void TelePort2() {
		for (L1PcInstance c : L1World.getInstance().getAllPlayers()) {
			switch (c.getMap().getId()) {
			case 1700:
			case 1703:
				c.stopHpRegenerationByDoll();
				c.stopMpRegenerationByDoll();
				new L1Teleport().teleport(c, 33430, 32797, (short) 4, 4, true);
				c.sendPackets(new S_SystemMessage("잊혀진섬이 닫혔습니다."));
				break;
			default:
				break;
			}
		}
	}
	
	private static void delenpc(int npcid) {
		L1NpcInstance npc = null;
		for (L1Object object : L1World.getInstance().getObject()) {
			if (object instanceof L1NpcInstance) {
				npc = (L1NpcInstance) object;
				if (npc.getNpcTemplate().get_npcId() == npcid) {
					npc.deleteMe();
					npc = null;
				}
			}
		}
	}
	
	
}
