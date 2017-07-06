package l1j.server.GameSystem.MiniGame;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.text.SimpleDateFormat;

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
			System.out.println("■ 지옥사냥터 데이터 .......................... ■ 로딩 정상 완료");
		}
		
		@Override
			public void run() {
			try	{
					while (true) {
						Thread.sleep(1000); 
						/** 오픈 **/
						if(!isOpen6() && !isGmOpen3)
							continue;
						if(L1World.getInstance().getAllPlayers().size() <= 0)
							continue;
						
						isGmOpen3 = false;

						/** 오픈 메세지 **/
						L1World.getInstance().broadcastServerMessage("지옥입장권 추첨이 시작되었습니다. 모두 참여하세요.");
						L1World.getInstance().broadcastServerMessage("예) [.지옥참여]   채팅창에 치시면됩니다.");
						L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,"지옥이 열렸습니다. 오픈후부터 1시간동안 입장가능합니다."));

						/** 지옥 영토 시작**/
						setHellStart(true);

						/** 실행 1시간 시작**/
						L1World.getInstance().broadcastServerMessage("2분후 추첨이 종료됩니다.");
						L1World.getInstance().broadcastServerMessage("지옥입장권 참여하세요. 예) [.지옥참여] ");
						Thread.sleep(60000L); // 2분간 대기
						L1World.getInstance().broadcastServerMessage("1분후 추첨이 종료됩니다.");
						L1World.getInstance().broadcastServerMessage("지옥입장권 참여하세요. 예) [.지옥참여] ");
						Thread.sleep(60000L); // 2분간 대기
						 if(_Members.size() <= 4){
							 L1World.getInstance().broadcastServerMessage("입장권추첨이 인원이 적어 취소되었습니다.");
							 setRaidJoin(false); // 참여 가능시간 종료
							 _Members.clear();
						 } else {
							 setRaidJoin(false); // 참여 가능시간 종료
							 Choice(); // 당첨자 3명 추첨
						 }
						Thread.sleep(2080000L); //3800000L 1시간 10분정도
						Boss();
						Thread.sleep(1500000L); //3800000L 1시간 10분정도
						 
						/** 1시간뒤에 영토맵에 있는 유저 마을로 **/
						for(L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
							if (pc.getMapId() == 666){
								new L1Teleport().teleport(pc, 33970, 33246, (short) 4, pc.getMoveState().getHeading(), true);
								setHellStart(false);
							}
						}
						
						/** 5초 뒤에 다시한번 텔레포트 마을로 **/
						Thread.sleep(5000L);
						close();
						TelePort5();
						setHellStart(false);
						
						/** 5초 뒤에 다시한번 텔레포트 마을로 **/
						Thread.sleep(5000L);
						TelePort6();
						setHellStart(false);

						/** 종료메세지 출력 **/
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
			 RaidList[Cmem1].sendPackets(new S_SystemMessage("지옥 입장권 추첨에 당첨되셨습니다."));
			 RaidList[Cmem1].getInventory().storeItem(42050, 1);
			 RaidList[Cmem2].sendPackets(new S_SystemMessage("지옥 입장권 추첨에 당첨되셨습니다."));
			 RaidList[Cmem2].getInventory().storeItem(42050, 1);
			 RaidList[Cmem3].sendPackets(new S_SystemMessage("지옥 입장권 추첨에 당첨되셨습니다."));
			 RaidList[Cmem3].getInventory().storeItem(42050, 1);
			 }
		 
		
		 
			public void AddMember(L1PcInstance pc) {
				if (!_Members.contains(pc)) {
					_Members.add(pc);
					pc.sendPackets(new S_SystemMessage("\\aD신청 되었습니다. 잠시후 추첨이 시작됩니다."));
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
			 *오픈 시각을 가져온다
			 *
			 *@return (Strind) 오픈 시각(MM-dd HH:mm)
			 */
			 public String HellOpen() {
				 Calendar c = Calendar.getInstance();
				 c.setTimeInMillis(sTime);
				 return ss.format(c.getTime());
			 }
			 
			 private void Boss(){
				 	L1SpawnUtil.spawn2(32807, 32731, (short) 666, 40173, 0, 600*1000, 0);
					L1World.getInstance().broadcastServerMessage("\\aD지옥을 지배하는 구미호가 지옥에 나타났습니다.");
					L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, 
							"지옥을 지배하는 구미호가 지옥에 나타났습니다.."));
				 }
			 /**
			 *영토가 열려있는지 확인
			 *
			 *@return (boolean) 열려있다면 true 닫혀있다면 false
			 */
			 	// 열리는 시간을 정해주기 다른곳 응용 참조 할것!!
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
				 * 영토가 열려있는지 확인
				 * 
				 * @return (boolean) 열려있다면 true 닫혀있다면 false
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
			 *실제 현재시각을 가져온다
			 *
			 *@return (String) 현재 시각(HH:mm)
			 */
			 private String getTime() {
				 return s.format(Calendar.getInstance().getTime());
			 }
			 
				/**캐릭터가 죽었다면 종료시키기**/
			 private void close() {
			  for(L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			   if (pc.getMap().getId() == 666 && pc.isDead()) {
			    pc.stopHpRegenerationByDoll();
			    pc.stopMpRegenerationByDoll();
			    pc.sendPackets(new S_Disconnect());
			   }
			  }
			 }

			 /**아덴마을로 팅기게**/
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
			 
			 /**아덴마을로 팅기게**/
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

			 /** 종료 **/
			 public void End() {
				L1World.getInstance().broadcastServerMessage("지옥의 불꽃이 사라졌습니다. 3시간후 다시 개방됩니다.");
				L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,"지옥의 불꽃이 사라졌습니다. 3시간후 다시 개방됩니다."));
				setHellStart(false);
			 }
}