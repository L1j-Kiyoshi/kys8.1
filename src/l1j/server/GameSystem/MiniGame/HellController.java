package l1j.server.GameSystem.MiniGame;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

import l1j.server.Config;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.L1SpawnUtil;

public class HellController extends Thread {
	
		private static HellController _instance;
		
		private L1PcInstance[] RaidList;
		
		private boolean _HellStart;
		public boolean getHellStart() {	return _HellStart; }
		public void setHellStart(boolean Hell) { _HellStart = Hell; }
		
		private boolean _RaidJoin;
		public boolean getRaidJoin() { return _RaidJoin; }
		public void setRaidJoin(boolean Raid) {	_RaidJoin = Raid; }
		
		private static long sTime = 0;	
		private String NowTime = "";
		public boolean isGmOpen3;
		private static final int HELLTIME = Config.HELL_TIME;
		public final ArrayList<L1PcInstance> _Members = new ArrayList<L1PcInstance>();
		private static final SimpleDateFormat s = new SimpleDateFormat("HH", Locale.KOREA);
		private static final SimpleDateFormat ss = new SimpleDateFormat("MM-dd HH:mm", Locale.KOREA);

		
		public static HellController getInstance() {
			if(_instance == null) {
				_instance = new HellController();
			}
			return _instance;
		}
		
		private HellController() {
			System.out.println("■地獄狩り場データ..........................■ロード正常終了");
		}
		
		@Override
			public void run() {
			try	{
					while (true) {
						Thread.sleep(1000); 
						/** オープン **/
						if(!isOpen6() && !isGmOpen3)
							continue;
						if(L1World.getInstance().getAllPlayers().size() <= 0)
							continue;
						
						isGmOpen3 = false;

						/** オープンメッセージ **/
						L1World.getInstance().broadcastServerMessage("地獄入場券抽選が開始されました。すべての参加してください。");
						L1World.getInstance().broadcastServerMessage("例）[地獄参加]チャットウィンドウにチシミョンされます。");
						L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,"地獄が開かれました。オープン後から1時間の間に入場可能です。"));

						/** 地獄の領土を開始**/
						setHellStart(true);

						/** 実行1時間開始**/
						L1World.getInstance().broadcastServerMessage("2分後、抽選が終了します。");
						L1World.getInstance().broadcastServerMessage("地獄の入場券に参加してください。例）[地獄参加]");
						Thread.sleep(60000L); // 2分間待機
						L1World.getInstance().broadcastServerMessage("1分後、抽選が終了します。");
						L1World.getInstance().broadcastServerMessage("地獄の入場券に参加してください。例）[地獄参加]");
						Thread.sleep(60000L); // 2分間待機
						 if(_Members.size() <= 4){
							 L1World.getInstance().broadcastServerMessage("入場券抽選が人員が少なくキャンセルされました。");
							 setRaidJoin(false); // 参加可能時間終了
							 _Members.clear();
						 } else {
							 setRaidJoin(false); // 参加可能時間終了
							 Choice(); // 当選者3人抽選
						 }
						Thread.sleep(2080000L); //3800000L 1時間10分程度
						Boss();
						Thread.sleep(1500000L); //3800000L 1時間10分程度
						 
						/** 1時間後に領土マップのユーザー町に **/
						for(L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
							if (pc.getMapId() == 666){
								new L1Teleport().teleport(pc, 33970, 33246, (short) 4, pc.getMoveState().getHeading(), true);
								setHellStart(false);
							}
						}
						
						/** 5秒後、再びテレポート村で **/
						Thread.sleep(5000L);
						close();
						TelePort5();
						setHellStart(false);
						
						/** 5秒後、再びテレポート村で **/
						Thread.sleep(5000L);
						TelePort6();
						setHellStart(false);

						/** 終了メッセージ出力 **/
						End();
					
					}
				} catch(Exception e){
					e.printStackTrace();
				}
			}

		
		 private void Choice(){
			 Random rnd1 = new Random();
			 Random rnd2 = new Random();
			 Random rnd3 = new Random();
			 int Cmem1 = rnd1.nextInt(getMemberCount());
			 int Cmem2 = rnd2.nextInt(getMemberCount());
			 int Cmem3 = rnd3.nextInt(getMemberCount());
			 RaidList = getMemberArray();
			 RaidList[Cmem1].sendPackets(new S_SystemMessage("地獄入場券抽選に当たるました。"));
			 RaidList[Cmem1].getInventory().storeItem(42050, 1);
			 RaidList[Cmem2].sendPackets(new S_SystemMessage("地獄入場券抽選に当たるました。"));
			 RaidList[Cmem2].getInventory().storeItem(42050, 1);
			 RaidList[Cmem3].sendPackets(new S_SystemMessage("地獄入場券抽選に当たるました。"));
			 RaidList[Cmem3].getInventory().storeItem(42050, 1);
			 }
		 
		
		 
			public void AddMember(L1PcInstance pc) {
				if (!_Members.contains(pc)) {
					_Members.add(pc);
					pc.sendPackets(new S_SystemMessage("\\aD申し込みされた。しばらくして抽選が開始されます。"));
				}
			}
			public void removeMember(L1PcInstance pc) {
				_Members.remove(pc);
			}

			public void clearMembers() {
				_Members.clear();
			}

			public boolean isMember(L1PcInstance pc) {
				return _Members.contains(pc);
			}

			public L1PcInstance[] getMemberArray() {
				return _Members.toArray(new L1PcInstance[_Members.size()]);
			}

			public int getMemberCount() {
				return _Members.size();
			}
			
			/**
			 *オープン時刻を持って来る
			 *
			 *@return (Strind）オープン時刻（MM-dd HH：mm）
			 */
			 public String HellOpen() {
				 Calendar c = Calendar.getInstance();
				 c.setTimeInMillis(sTime);
				 return ss.format(c.getTime());
			 }
			 
			 private void Boss(){
				 	L1SpawnUtil.spawn2(32807, 32731, (short) 666, 40173, 0, 600*1000, 0);
					L1World.getInstance().broadcastServerMessage("\\aD地獄を支配する禁断のが地獄に気づいた。");
					L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, 
							"地獄を支配する禁断のが地獄に気づいた。"));
				 }
			 /**
			 *領土が開いていることを確認
			 *
			 *@return (boolean)開いている場合true閉じている場合false
			 */
			 	// 開催時間を決めて与える他の場所のアプリケーション参照すること！
			/* private boolean isOpen() {
				  Calendar calender = Calendar.getInstance();
				  int hour, minute;
				  hour = calender.get(Calendar.HOUR_OF_DAY);
				  minute = calender.get(Calendar.MINUTE);
				  if ((hour == 03 && minute == 00) || (hour == 11 && minute == 00)
				   || (hour == 19 && minute == 00)) {
				   return true;
				  }
				  return false;
				 }*/

			 
			 /**
				 * 領土が開いていることを確認
				 * 
				 * @return (boolean) 開いている場合true閉じている場合false
				 */
				private boolean isOpen6() {
					NowTime = getTime();
					if ((Integer.parseInt(NowTime) % HELLTIME) == 0)
						return true;
					return false;
				}
				
				public boolean isOpen7() {
					NowTime = getTime();
					if ((Integer.parseInt(NowTime) % HELLTIME) >= 2 
							&& (Integer.parseInt(NowTime) % HELLTIME) <= 8)
						return true;
					return false;
				}


			 /**
			 *実際、現在時刻を持って来る
			 *
			 *@return (String) 現在時刻（HH：mm）
			 */
			 private String getTime() {
				 return s.format(Calendar.getInstance().getTime());
			 }
			 
				/**キャラクターが死亡した場合、終了させる**/
			 private void close() {
			  for(L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			   if (pc.getMap().getId() == 666 && pc.isDead()) {
			    pc.stopHpRegenerationByDoll();
			    pc.stopMpRegenerationByDoll();
			    pc.sendPackets(new S_Disconnect());
			   }
			  }
			 }

			 /**アデン村にティンギが**/
			 private void TelePort5() {
				 for(L1PcInstance c : L1World.getInstance().getAllPlayers()) {
					 switch(c.getMap().getId()) {
						 case 666:
						 c.stopHpRegenerationByDoll();
						 c.stopMpRegenerationByDoll();
						 new L1Teleport().teleport(c, 33970, 33246, (short) 4, c.getMoveState().getHeading(), true);
						 break;
						 default:
						 break;
					 }
				 }
			 }
			 
			 /**アデン村にティンギが**/
			 private void TelePort6() {
				 for(L1PcInstance c : L1World.getInstance().getAllPlayers()) {
					 switch(c.getMap().getId()) {
						 case 666:
						 c.stopHpRegenerationByDoll();
						 c.stopMpRegenerationByDoll();
						 new L1Teleport().teleport(c, 33970, 33246, (short) 4, c.getMoveState().getHeading(), true);
						 break;
						 default:
						 break;
					 }
				 }
			 }

			 /** 終了 **/
			 public void End() {
				L1World.getInstance().broadcastServerMessage("地獄の炎が消えました。 3時間後に再び開放されます。");
				L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,"地獄の炎が消えました。 3時間後に再び開放されます。"));
				setHellStart(false);
			 }
}