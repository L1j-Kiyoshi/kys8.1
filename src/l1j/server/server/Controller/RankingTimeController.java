package l1j.server.server.Controller;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import l1j.server.L1DatabaseFactory;
import l1j.server.GameSystem.AttendanceController;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.datatables.RankTable;
import l1j.server.server.model.L1Rank;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_Ranking2;
import l1j.server.server.utils.SQLUtil;

/**
 * 랭킹 시스템<br>
 * 랭킹에 대한 연산은 DB에 접근하지 않고 Map을 활용.
 * 
 * @author
 */
public class RankingTimeController {

	private static final RankingTimeController _instance = new RankingTimeController();
	public static RankingTimeController getInstance() { return _instance; }

	private SimpleDateFormat time;
	private int lastMin;

	private RankingTimeController() {
		time = new SimpleDateFormat("hh");
		lastMin = Integer.parseInt(time.format(new Date()));
		deleteItem();
		RankTable.getInstance().updateRank();
		GeneralThreadPool.getInstance().execute(new WaitCorrectTime());
	}
	
	private class WaitCorrectTime implements Runnable {
		@Override
		public void run() {
			try {
				while (true) {
					int hour = Integer.parseInt(time.format(new Date()));
					if (lastMin != hour) {
						lastMin = hour;
						break;
					}
					AttendanceController.toTimer(System.currentTimeMillis());
					Thread.sleep(1000L);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {//랭킹갱신 시간
				GeneralThreadPool.getInstance().scheduleAtFixedRate(new UpdateRank(), 0, 600 * 1000);//3600 1시간 600 10분 60초 30초
			}
		}
	}
	
	private class UpdateRank implements Runnable {
		@Override
		public void run() {
			try {
				update();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void update() {
		setBuff(-1);
		deleteItem();
		RankTable.getInstance().updateRank(); 
		setBuff(1);
	}

	private void setBuff(int rate) {
		List<L1Rank> all_list = RankTable.getInstance().getMapByClass(8);
		List<L1Rank> list = null;
		if (all_list.size() > 100) list = all_list.subList(0, 100);
		else list = all_list;
		for (L1Rank rank : list) {
			L1PcInstance pc = L1World.getInstance().getPlayer(rank.getName());
			if (pc == null) continue;
			pc.setRankLevel(rank.getRankLevel());
			setBuffEffect(pc, rank, rate);
		}
	}

	public void setBuffEffect(L1PcInstance pc, L1Rank rank, int rate) {
		if (rank.getRankLevel() == 4) {
			pc.addMaxHp(100 * rate);
			pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			pc.sendPackets(new S_Ranking2(L1SkillId.RANK_BUFF_4, rate > 0 ? true : false, rank.getType(), rate > 0 ? -1 : 0));
		} else if (rank.getRankLevel() == 3) {
			pc.setSkillEffect(L1SkillId.RANK_BUFF_3, -1);
			pc.sendPackets(new S_Ranking2(L1SkillId.RANK_BUFF_3, rate > 0 ? true : false, rank.getType(), rate > 0 ? -1 : 0));
		} else if (rank.getRankLevel() == 2) {
			pc.setSkillEffect(L1SkillId.RANK_BUFF_2, -1);
			pc.sendPackets(new S_Ranking2(L1SkillId.RANK_BUFF_2, rate > 0 ? true : false, rank.getType(), rate > 0 ? -1 : 0));
		} else if (rank.getRankLevel() == 1) {
			pc.setSkillEffect(L1SkillId.RANK_BUFF_1, -1);
			pc.sendPackets(new S_Ranking2(L1SkillId.RANK_BUFF_1, rate > 0 ? true : false, rank.getType(), rate > 0 ? -1 : 0));
		}
		if (pc.isCrown() || pc.isKnight() || pc.isDarkelf() || pc.isDragonknight() || pc.is전사()) {
			pc.getAbility().addAddedStr(1 * rate);
		} else if(pc.isElf()) {
			pc.getAbility().addAddedDex(1 * rate);
		} else if(pc.isWizard() || pc.isBlackwizard()) {
			pc.getAbility().addAddedInt(1 * rate);
		}
		
		if (pc.getNetConnection() != null) {
			RankTable.getInstance().sendRankStatusPacks(pc.getNetConnection(), pc.getName());
		}
		
		pc.sendPackets(new S_OwnCharStatus(pc));
		
	}

	private void deleteItem() {
		for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			if ( pc == null)continue;
			if (pc.getRankLevel() != 4) 
				pc.getInventory().consumeItem(5558);
		}
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			conn = L1DatabaseFactory.getInstance().getConnection();
			pstm = conn.prepareStatement("DELETE FROM character_items WHERE item_id=5558");
			pstm.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(conn);
		}
	}
}