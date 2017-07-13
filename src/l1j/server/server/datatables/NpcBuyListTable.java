package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.model.NpcBuyList;
import l1j.server.server.utils.SQLUtil;

public class NpcBuyListTable {
	private static Logger _log = Logger.getLogger(NpcBuyListTable.class.getName());
	private static NpcBuyListTable ins;
	
	private HashMap<Integer,NpcBuyList> nblist = new HashMap<Integer,NpcBuyList>();
	private ArrayList<NpcBuyList> search = new ArrayList<NpcBuyList>();
	private ArrayList<NpcBuyList> search2 = new ArrayList<NpcBuyList>();
	private ArrayList<NpcBuyList> search3 = new ArrayList<NpcBuyList>();
	public static NpcBuyListTable getInstance(){
		if(ins==null)
			ins = new NpcBuyListTable();
		return ins;
	}
	public NpcBuyListTable(){
		LoadData();
	}
	
	public int getPrice(int id,int enchant,int attr,int bless){
		int price = 0;
		//System.out.println("nblist.s:"+nblist.size()+"/"+"nbid:"+nblist.get(0).itemid+"/"+id+"/ "+enchant+"/"+attr+"/"+bless);
		for(int i = 0 ; i < nblist.size();i++){
			if(nblist.get(i).itemid==id){
				search.add(nblist.get(i));
			}
		}
		for(int i=0; i<search.size();i++){
			if(search.get(i).EnchantLevel==enchant){
				search2.add(search.get(i));
			}
		}
		for(int i = 0 ; i <search2.size();i++){
			if(search2.get(i).AttrEnchantLevel==attr){
				search3.add(search2.get(i));
			}
		}

		for(int i = 0 ; i < search3.size();i++){
			if(search3.get(i).bless == bless){
				price=search3.get(i).price;
			}
		}
		//0軸1が通常2呪い
		search.clear();
		search2.clear();
		search3.clear();
		return price;
	}
	
	private void LoadData(){
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("select * from NpcBuyList");
			rs = pstm.executeQuery();
			NpcBuyList list = null;
			int i = 0;
			while (rs.next()) {
				int itemId = rs.getInt("itemid");
				int enchant = rs.getInt("EnchantLevel");
				int attr = rs.getInt("AttrEnchant");
				int bless= rs.getInt("bless");
				int price = rs.getInt("price");
				list = new NpcBuyList(itemId,enchant,attr,bless,price);
				
				nblist.put(i, list);
				i++;
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

}
