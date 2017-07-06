package l1j.server.server.Controller;

import static l1j.server.server.model.skill.L1SkillId.ANTA_BUFF;
import static l1j.server.server.model.skill.L1SkillId.FAFU_BUFF;
import static l1j.server.server.model.skill.L1SkillId.RIND_BUFF;
import static l1j.server.server.model.skill.L1SkillId.VALA_BUFF;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Collection;

import l1j.server.Config;
import l1j.server.GameSystem.AttendanceController;
import l1j.server.GameSystem.Robot.L1RobotInstance;
import l1j.server.server.Opcodes;

import l1j.server.server.datatables.SpamTable;
import l1j.server.server.model.L1AccountAttendance;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1ExcludingList;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ACTION_UI;
import l1j.server.server.serverpackets.S_Attendance;

import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_Restart;
import l1j.server.server.serverpackets.S_SystemMessage;


public class PremiumTimeController implements Runnable {

	public static final int SLEEP_TIME = Config.FEATHER_TIME * 60000; // 원본 600초 

	private static PremiumTimeController _instance;

	public static PremiumTimeController getInstance() {
		if (_instance == null) {
			_instance = new PremiumTimeController();
		}
		return _instance;
	}

	@Override
	public void run() {
		try {

		//	checkDragonBlood();
			pcbuffPremiumTime();
			가입멘트();
			인형청소();
		} catch (Exception e1) { }
	}
	

	private void 인형청소() {
		try{
			for (Object obj : L1World.getInstance().getObject()) {
				if (obj instanceof L1DollInstance) {
					L1DollInstance 인형 = (L1DollInstance) obj;
					if (인형.getMaster() == null) {
						인형.deleteMe();
					} else if (((L1PcInstance) 인형.getMaster()).getNetConnection() == null) {
						인형.deleteMe();
					}
				}
			}
		} catch (Exception e) {	}
	}
	
	private void pcbuffPremiumTime() {
		for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			if (pc instanceof L1RobotInstance) {
				continue;
			}
			if (pc.PC방_버프삭제중) {
				pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,"[PC방 상품 종료 안내] PC방 이용 시간이 종료되어 강제 리스타트가 진행됩니다. "));
				pc.sendPackets(new S_SystemMessage("[PC방 상품 종료 안내] 리스타트를 진행하지 않아도 혜택은 받을수 없습니다."));
				pc.sendPackets(new S_Restart(pc.getId(), 1),true);
			}
			
			long sysTime = System.currentTimeMillis();
			if (pc.PC방_버프) {
				if (pc.getAccount().getBuff_PC방() != null) {
					if (sysTime <= pc.getAccount().getBuff_PC방().getTime()) {
						long 피씨타임 = pc.getAccount().getBuff_PC방().getTime() - sysTime;
						TimeZone seoul = TimeZone.getTimeZone(Config.TIME_ZONE);
						Calendar calendar = Calendar.getInstance(seoul);
						calendar.setTimeInMillis(피씨타임);
						int d = calendar.get(Calendar.DATE) - 1;
						int h = calendar.get(Calendar.HOUR_OF_DAY);
						int m = calendar.get(Calendar.MINUTE);
						int sc = calendar.get(Calendar.SECOND);
						if (d == 0) {
							if (h > 0) {
								if (h == 1 && m == 0) {
									pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,"[PC방 이용 시간] " + h + "시간 " + m + "분 " + sc+ "초 남았습니다."));
								}
							} else {
								if (m == 30) {
									pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,"[PC방 이용 시간] " + m + "분 " + sc + "초 남았습니다."));
									pc.sendPackets(new S_SystemMessage("[PC방 상품 종료 안내] 이용 시간 소진시 강제 리스타트가 진행 됩니다."));
								} else if (m == 20) {
									pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "[PC방 이용 시간] " + m + "분 " + sc + "초 남았습니다."));
									pc.sendPackets(new S_SystemMessage("[PC방 상품 종료 안내] 이용 시간 소진시 강제 리스타트가 진행 됩니다."));
								} else if (m <= 10) {
									pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,"[PC방 이용 시간] " + m + "분 " + sc + "초 남았습니다."));
									pc.sendPackets(new S_SystemMessage("[PC방 상품 종료 안내] 종료후 버프가 남아있어도 혜택은 받을수 없습니다. 종료시 자동 리스타트가 진행됩니다."));
								}
							}
						}
					} else {
						pc.PC방_버프 = false;
						pc.PC방_버프삭제중 = true;
						pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.PCBANG_SET, true));
						pc.sendPackets(new S_Restart(pc.getId(), 1), true);
					}
				}
			}
			L1AccountAttendance acc = AttendanceController.findacc(pc.getAccountName());
			if(acc != null)
				//acc.getPc().sendPackets(new S_Attendance(acc, 1, false));
				acc.getPc().sendPackets(new S_Attendance(acc, 0, false));
		}
	}
	
	private void checkPremiumTime() {//일정시간 깃털지급
		for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			if (!pc.isPrivateShop() && !pc.isAutoClanjoin() && !pc.noPlayerCK && !pc.noPlayerck2 && pc != null && !pc.isDead()) {
				int FN = Config.FEATHER_NUM;
				int CLN = Config.FEATHER_NUM1;
				int CAN = Config.FEATHER_NUM2;
				int FN2 = Config.useritem;//아이템번호
				int FN3 = Config.usercount;//갯수
				L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
				/** 전체유저에게 선물을 지급한다 **/
				
				if (Config.전체선물작동유무) {
				pc.getInventory().storeItem(FN2, FN3);
				pc.sendPackets(new S_SystemMessage("\\aA알림:선물상자 (\\aG" + FN3 + "\\aA) 획득 하셨습니다."));
				}
				
				if (pc.getClanid() == 0) { // 무혈
					pc.getInventory().storeItem(41159, FN);
					pc.sendPackets(new S_SystemMessage("\\aA알림:픽시의 깃털 (\\aG" + FN + "\\aA) 획득 하셨습니다."));
				}
				if (clan != null) {
				if (clan.getCastleId() == 0 && pc.getClanid() != 0) { // 혈맹
					pc.getInventory().storeItem(41159, (CLN + FN));
					pc.sendPackets(new S_SystemMessage("\\aA알림:픽시의 깃털 (\\aG" + FN + "+" + CLN + "\\aA) 획득 하셨습니다."));
				}
				if (clan.getCastleId() != 0) { // 성혈
					pc.getInventory().storeItem(41159, (CAN + FN));
					pc.sendPackets(new S_SystemMessage("\\aA알림:픽시의 깃털 (\\aG" + FN + "+" + CAN + "\\aA) 획득하셨습니다."));
				}
			}
			}

		}
	}
	private void 가입멘트() {
		try{
			for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
				if(pc.isAutoClanjoin()){
					S_ChatPacket s_chatpacket = new S_ChatPacket(pc, pc.getClanname() + " 혈맹에서 혈원 모집중입니다. 앞에서/가입 치세요", Opcodes.S_SAY, 0);			
						for (L1PcInstance listner : L1World.getInstance().getRecognizePlayer(pc)) {
							L1ExcludingList spamList3 = SpamTable.getInstance().getExcludeTable(listner.getId());
							if (!spamList3.contains(0, pc.getName())) {
								listner.sendPackets(s_chatpacket);
							}
						}
					}
				}
		} catch (Exception e) { 
			e.printStackTrace();
		}
	}
	
	
	
	private void checkDragonBlood() {
		int time = 0;
		for (L1PcInstance pc : L1World.getInstance().getAllPlayers()){
			if(pc.hasSkillEffect(ANTA_BUFF)){
				time = pc.getSkillEffectTimeSec(ANTA_BUFF) / 60;
				pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONBLOOD, 82, time));
			}
			if (pc.hasSkillEffect(FAFU_BUFF)){
				time = pc.getSkillEffectTimeSec(FAFU_BUFF) / 60;
				pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONBLOOD, 85, time));
			}
			if (pc.hasSkillEffect(RIND_BUFF)){
				time = pc.getSkillEffectTimeSec(RIND_BUFF) / 60;
				pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONBLOOD, 88, time));
			}
			if (pc.hasSkillEffect(VALA_BUFF)){
				time = pc.getSkillEffectTimeSec(VALA_BUFF) / 60;
				pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONBLOOD, 88, time));
			}
		}
	}
}