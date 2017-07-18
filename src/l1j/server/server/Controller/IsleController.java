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
    

    /** ゲームオープンの有無 **/
    public boolean isgameStart = false;

    /** ゲームの状態 **/
    public int Status = 0;//進行状況
    private final int status_Wait = 0;//進行
    private final int status_Open = 1;
    private final int status_GetOn = 2;
    private final int status_End = 3;//

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
                case status_Wait:
                    Thread.sleep(10000);//10秒ごとに現在オープンされた状態であるかをチェック
                    /** オープンでなければ進行 **/
                    if (isgameStart == false) {//オープンまたは再戻し送信
                        continue;//元に戻す 
                    }
                    Status = status_Open;
                    L1World.getInstance().broadcastServerMessage("\\aH通知：しばらくして、[忘れられた島]入場が可能です。");
                    continue;
                case status_Open:
                	L1World.getInstance().broadcastServerMessage("\\aH通知：忘れられた島1時間狩り可能ので、注意してください。");
    				L1World.getInstance().broadcastServerMessage("\\aH通知：時間になると、強制的に帰還されます。");
                	System.out.println("......忘れられた島開");
                	Status = status_GetOn;
                    continue;
                case status_GetOn:
                   
                	/** 実行1時間開始**/
                	Thread.sleep(3800000L);  // 3800000L 1時間10分程度
    				/** 1時間後に自動テレポート **/
    				TelePort();
    				close(); //追加
    				Thread.sleep(5000L);
    				TelePort2();
                    Status = status_End;
                    continue;
                case status_End:
                	//L1World.getInstance（）broadcastServerMessage（ "\\\\ aG地獄狩り場は2時間狩りが可能です。"）;
                	L1World.getInstance().broadcastServerMessage("\\aH通知：忘れられた島が終了しました。");
                	System.out.println("......イッソム終了");
                    isgameStart = false;
                    Status = status_Wait;
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
				c.sendPackets(new S_SystemMessage("忘れられた島が閉じられました。"));
				break;
			default:
				break;
			}
		}
	}
	/**キャラクターが死亡した場合、終了させる**/
	 private void close() {
	  for(L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
	   if (pc.getMap().getId() >= 1700 && pc.getMap().getId() <= 1703 && pc.isDead()) {
	    pc.stopHpRegenerationByDoll();
	    pc.stopMpRegenerationByDoll();
	    pc.sendPackets(new S_Disconnect());
	   }
	  }
	 }

	/** アデン村にティンギが**/
	private void TelePort2() {
		for (L1PcInstance c : L1World.getInstance().getAllPlayers()) {
			switch (c.getMap().getId()) {
			case 1700:
			case 1703:
				c.stopHpRegenerationByDoll();
				c.stopMpRegenerationByDoll();
				new L1Teleport().teleport(c, 33430, 32797, (short) 4, 4, true);
				c.sendPackets(new S_SystemMessage("忘れられた島が閉じられました。"));
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
