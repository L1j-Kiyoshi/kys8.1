package l1j.server.IndunSystem.FanstasyIsland;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.IdFactory;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.utils.SQLUtil;


public class FantasyIslandSpawn {
	private static Logger _log = Logger.getLogger(FantasyIslandSpawn.class.getName());
	private static FantasyIslandSpawn _instance;

	public static FantasyIslandSpawn getInstance() {
		if (_instance == null) {
			_instance = new FantasyIslandSpawn();
		}
		return _instance;
	}

	private FantasyIslandSpawn() {}

	public void fillSpawnTable(int mapid, int type) {
		fillSpawnTable(mapid, type, false);
	}

	public ArrayList<L1NpcInstance> fillSpawnTable(int mapid, int type, boolean RT) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		L1Npc l1npc = null;
		L1NpcInstance field = null;
		ArrayList<L1NpcInstance> list = null;
		if(RT)
			list = new ArrayList<L1NpcInstance>();

		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM spawnlist_fantasyIsland");
			rs = pstm.executeQuery();
			int count = 0;
			while(rs.next()){
				if (type != rs.getInt("type")) continue;
				if(type >= 2 && type <= 5)
					count = 1;
				else
					count = 0;
				for(int i = 0 ; i <= count; i++){

				l1npc = NpcTable.getInstance().getTemplate(rs.getInt("npc_id"));
				if (l1npc != null) {
					try {
						field = NpcTable.getInstance().newNpcInstance(rs.getInt("npc_id"));
						field.setId(IdFactory.getInstance().nextId());
						field.setX(rs.getInt("locx"));
						field.setY(rs.getInt("locy"));
						field.setMap((short) mapid);
						field.setHomeX(field.getX());
						field.setHomeY(field.getY());
						field.setHeading(rs.getInt("heading"));
						field.setLightSize(l1npc.getLightSize());
						field.getLight().turnOnOffLight();
						field.setSpawnLocation(rs.getString("location"));
						L1World.getInstance().storeObject(field);
						L1World.getInstance().addVisibleObject(field);
						if(RT)
							list.add(field);
					} catch (Exception e) {
						_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
					}
				}
				l1npc = null;
				}
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} catch (SecurityException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} catch (IllegalArgumentException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}

		return list;
	}
}
