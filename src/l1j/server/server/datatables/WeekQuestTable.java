package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;

import l1j.server.server.model.L1Spawn;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.utils.SQLUtil;

public class WeekQuestTable {//주퀘
	
	public HashMap<Integer,Integer> WeekList = new HashMap<Integer,Integer>(); //초급 (index아이디) <npcid,index>
	public HashMap<Integer,Integer> WeekList2 = new HashMap<Integer,Integer>(); //중급
	public HashMap<Integer,Integer> WeekList3 = new HashMap<Integer,Integer>(); //고급
	
	public HashMap<Integer,Integer> NpcidList = new HashMap<Integer,Integer>(); //초급 (npcid) <index,npcid>
	public HashMap<Integer,Integer> NpcidList2 = new HashMap<Integer,Integer>(); //중급
	public HashMap<Integer,Integer> NpcidList3 = new HashMap<Integer,Integer>(); //고급
	
	public HashMap<Integer,L1Spawn> SpawnData = new HashMap<Integer,L1Spawn>(); //스폰리스트를 가져옴(텔레포트를위한).
	public ArrayList<Integer> maxcount = new ArrayList<Integer>();
	public int test = 0;
	public static WeekQuestTable ins;
	
	public static final int ClearCount = 40;
//	private static Logger _log = Logger.getLogger(WeekQuestTable.class.getName());
	
	public static WeekQuestTable getInstance(){
		if(ins==null)
			ins = new WeekQuestTable();
		return ins;
	}

	public WeekQuestTable(){
			loadQuestData();
			setMaxcount();
	}
	public void setMaxcount(){
		for(int i = 0 ; i < 9;i++)
			maxcount.add(40);
		}
	
	private void loadQuestData(){
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM monster_weekquest");
			rs = pstm.executeQuery();
			while(rs.next()){
			int type = rs.getInt("Type");
			
			for(int i = 0 ; i < 9 ; i++){
				switch(type){
					case 1:
						WeekList.put(rs.getInt(i+2), i);
						NpcidList.put(i,rs.getInt(i+2));
						break;
					case 2:
						WeekList2.put(rs.getInt(i+2), i);
						NpcidList2.put(i,rs.getInt(i+2));
						break;
					case 3:
						WeekList3.put(rs.getInt(i+2), i);
						NpcidList3.put(i,rs.getInt(i+2));
						break;
				}
				SpawnData.put(rs.getInt(i+2), null);
			}
		}

			

		} catch (SQLException e) {
			//_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}

	}
	
/*	public void SaveQuestData(L1PcInstance pc){
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("UPDATE character_weekquest SET quest_number_1=?, quest_number_2=?, quest_number_3=?, quest_number_4=?, quest_number_5=?, quest_number_6=?, quest_number_7=?, quest_number_8=?, quest_number_9=?, quest_week=?, lineclear1=?, lineclear2=?, lineclear3=? where char_name=?");

			pstm.setInt(1, pc.getWcount(0));
			pstm.setInt(2, pc.getWcount(1));
			pstm.setInt(3, pc.getWcount(2));
			pstm.setInt(4, pc.getWcount(3));
			pstm.setInt(5, pc.getWcount(4));
			pstm.setInt(6, pc.getWcount(5));
			pstm.setInt(7, pc.getWcount(6));
			pstm.setInt(8, pc.getWcount(7));
			pstm.setInt(9, pc.getWcount(8));
			pstm.setInt(10, pc.getQuestWeek());
			pstm.setInt(11, pc.getReward(0)? 1:0);
			pstm.setInt(12, pc.getReward(1)?1:0);
			pstm.setInt(13, pc.getReward(2)?1:0);
			pstm.setString(14, pc.getName());
			pstm.execute();
		} catch (SQLException e) {

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		
	} */
	public void CreateQuestData(String name) {
		try (Connection con = L1DatabaseFactory.getInstance().getConnection();
				PreparedStatement pstm = con.prepareStatement("INSERT INTO character_weekquest SET char_name=?, quest_number_1=?, quest_number_2=?, quest_number_3=?, quest_number_4=?, quest_number_5=?, quest_number_6=?, quest_number_7=?, quest_number_8=?, quest_number_9=?, quest_week=?, lineclear1=?, lineclear2=?, lineclear3=?")) {
			int i=1;
			pstm.setString(i++, name);
			pstm.setInt(i++, 0);
			pstm.setInt(i++, 0);
			pstm.setInt(i++, 0);
			pstm.setInt(i++, 0);
			pstm.setInt(i++, 0);
			pstm.setInt(i++, 0);
			pstm.setInt(i++, 0);
			pstm.setInt(i++, 0);
			pstm.setInt(i++, 0);
			pstm.setInt(i++, 0);
			pstm.setInt(i++, 0);
			pstm.setInt(i++, 0);
			pstm.setInt(i++, 0);
			pstm.execute();
		} catch (SQLException e) {
		}
	}
	public void loadCharacterQuestData(L1PcInstance pc){
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM character_weekquest where char_name=?");
			pstm.setString(1,pc.getName());
			rs = pstm.executeQuery();
			if(pc.getLevel() <= Config.WeekLevel1){
				pc.setWeekType(1);
			}else if(pc.getLevel() > Config.WeekLevel1 && pc.getLevel() <= Config.WeekLevel2){
				pc.setWeekType(2);
			}else{
				pc.setWeekType(3);
			}
			while(rs.next()){
				for(int i = 0 ; i < 9 ;i++)
					pc.setWcount(rs.getInt(i+2));
					
					pc.setQuestWeek(rs.getInt(11)); //추가 
				//2 3 4 5 6 7 8 9 10
				for(int i = 0 ; i < 3;i++){
					if(rs.getInt(i+12)==1)
					pc.setReward(i, true);
					else
						pc.setReward(i, false);
				}
			}

			
		} catch (SQLException e) {
			//_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}

	}
}
