package l1j.server.server.command.executor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1Search implements L1CommandExecutor{
	
	@SuppressWarnings("unused")
	private static Logger _log = Logger.getLogger(L1Summon.class.getName());
	
	private L1Search(){ }
	
	public static L1CommandExecutor getInstance(){
		return new L1Search();
	}
	
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer tok = new StringTokenizer(arg);
			int type = Integer.parseInt(tok.nextToken()) ;
			String name = tok.nextToken() ;
			searchObject(pc, type, "%" + name + "%") ;			
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage("。検索[0〜5] [name]を入力してください。")) ;
			pc.sendPackets(new S_SystemMessage("0 =ザブテム、1 =武器、2 =鎧、3 =エンピシ、4 =変身、5 =エンピシ（gfxid）、6 =スキル（skill）"));
		}
	}
	
	
	private void searchObject( L1PcInstance gm, int type, String name ) {
		try {
			String str1 = null ;
			String str2 = null ;
			String s_note = null ;
			java.sql.Connection con = null ;
			PreparedStatement statement = null ;
			int count = 0 ;
			con = L1DatabaseFactory.getInstance().getConnection() ;
			switch (type) {
			case 0://etcitem
				statement = con.prepareStatement("select item_id, name, name_id from etcitem where name Like '" + name + "'");
				break;
			case 1://weapon
				statement = con.prepareStatement("select item_id, name, name_id from weapon where name Like '" + name + "'");
				break;
			case 2: // armor
				statement = con.prepareStatement("select item_id, name, name_id from armor where name Like '" + name + "'");
				break;
			case 3: // npc
				statement = con.prepareStatement("select npcid, name, note from npc where name Like '" + name + "'");
				break;
			case 4: // polymorphs
				statement = con.prepareStatement("select polyid, name,id from polymorphs where name Like '" + name + "'");
				break;
			case 5: // npc(gfxid)
				statement = con.prepareStatement("select gfxid, name,note from npc where name Like '" + name + "'");
				break;
			case 6: //スキル番号
				statement = con.prepareStatement("select skill_id, name,skill_level from skills where name Like '" + name + "'");
				break;
			default:
				break;
			}
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				str1 = rs.getString(1);
				str2 = rs.getString(2);
				s_note = rs.getString(3);
				gm.sendPackets(new S_SystemMessage("[" + str1 + "]--[" + str2 + "]--" + s_note));
				count++ ;
			}
			rs.close();
			statement.close();
			con.close();
			gm.sendPackets(new S_SystemMessage("総[" + count + "]の検索がしました。"));
		} catch (Exception e) { }
	}

}
