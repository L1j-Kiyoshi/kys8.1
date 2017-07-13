package l1j.server.server.command.executor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.GameClient;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.SQLUtil;

public class L1QueryCharacter implements L1CommandExecutor  {
	private static Logger _log = Logger.getLogger(L1QueryCharacter.class
			. getName());
	
	private L1QueryCharacter() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1QueryCharacter();
	}

	static private String getCClass( String ip )
	{
		return ip.substring(0, ip.lastIndexOf('.'));
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try
		{
			L1PcInstance target = L1World.getInstance(). getPlayer(arg);

			if (target != null) {
				long totalAdena = 0;
				GameClient client = target.getNetConnection();
				
				if (client == null)
				{
					pc.sendPackets(new S_SystemMessage("接続中でないキャラクターに対して照会することができません。"));
					return;
				}
				
				String cClass = getCClass(client.getIp());

				Collection<L1PcInstance> pcs = L1World.getInstance().getAllPlayers();

				for (L1PcInstance otherPc : pcs) {
					
					if (otherPc.getNetConnection() != null)
					{
						String otherPcIp = otherPc.getNetConnection().getIp();
						
						if(cClass.equals(getCClass(otherPcIp)))
						{
							totalAdena += printInfo(pc, otherPc, otherPcIp);
						}
					}
				}
				
				pc.sendPackets(new S_SystemMessage(
				"すべてのアカウント内のアデナ合計は[" + totalAdena + "]です。"));
				
			} else {
				pc.sendPackets(new S_SystemMessage(
						"そのような名前のキャラクターは、ワールド内には存在しません。"));
			}
			
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(cmdName + "[キャラクター名]で入力してください。"));
		}
	}
	
	private long printInfo(L1PcInstance master, L1PcInstance pc, String ip)
	{
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		long storageAdena = 0;
		long characterAdena = 0;
		try {

			conn = L1DatabaseFactory.getInstance().getConnection();
			pstm = conn.prepareStatement("select ifnull(sum(count), 0) as 'adena' from character_warehouse where item_id = 40308 and account_name = ?");
			pstm.setString(1, pc.getAccountName());
			rs = pstm.executeQuery();
			if (rs.next()) {
				storageAdena = rs.getInt("adena");
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(conn);
		}
		
		try {

			conn = L1DatabaseFactory.getInstance().getConnection();
			pstm = conn.prepareStatement("select ifnull(sum(count), 0) as 'adena' from character_items where item_id = 40308 and char_id IN (select objid FROM characters WHERE account_name = ?)");
			pstm.setString(1, pc.getAccountName());
			rs = pstm.executeQuery();
			if (rs.next()) {
				characterAdena = rs.getInt("adena");
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(conn);
		}
	
		master.sendPackets(new S_SystemMessage(ip + "で [" + pc.getName() + "]が現在の接続中のアカウント倉庫アデナは[" + storageAdena + "]キャラクターマイアデナ総合計は、[" + characterAdena + "]です。"));
		
		return storageAdena + characterAdena;
	}
}
