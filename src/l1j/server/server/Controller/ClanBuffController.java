package l1j.server.server.Controller;

import static l1j.server.server.model.skill.L1SkillId.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.GameSystem.Robot.L1RobotInstance;
import l1j.server.server.datatables.ClanTable;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_PacketBox;

public class ClanBuffController extends Thread {

	private static ClanBuffController _instance;

	private static Logger _log = Logger.getLogger(ClanBuffController.class.getName());

	public static ClanBuffController getInstance() {
		if (_instance == null) {
			_instance = new ClanBuffController();
			_instance.start();
		}
		return _instance;
	}

	public ClanBuffController() {
	}

	public void run() {
//		System.out.println("ClanBuffThread start real time thread...loanding Ok!");
		while (true) {
			try {				
				/** 血盟バフ **/
				Clanbuff();	
				checkDragonBlood();
				Thread.sleep(60000);
			} catch (Exception e) {
				_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			}
		}
	}
	private void checkDragonBlood() {
		int time = 0;
		for (L1PcInstance pc : L1World.getInstance().getAllPlayers()){
			if (pc instanceof L1RobotInstance) {
				continue;
			}
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
	private void Clanbuff() {
		try {
			for (L1Clan clan : L1World.getInstance().getAllClans()) {
				int bless = clan.getBless();			
				if (bless != 0) {					
					int[] times = clan.getBuffTime();
					ClanTable.getInstance().updateBuffTime(times[0], times[1], times[2], times[3], clan.getClanId());
				}				
				ClanTable.getInstance().updateBlessCount(clan.getClanId(), clan.getBlessCount());				
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}
}
