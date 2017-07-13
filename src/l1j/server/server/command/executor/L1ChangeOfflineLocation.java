package l1j.server.server.command.executor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.SQLUtil;

public class L1ChangeOfflineLocation implements L1CommandExecutor  {
	private static Logger _log = Logger.getLogger(L1ChangeOfflineLocation.class
			. getName());
	
	private L1ChangeOfflineLocation() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1ChangeOfflineLocation();
	}
	
	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try
		{
			StringTokenizer st = new StringTokenizer(arg);
			String charname = st.nextToken();
	
			if (L1World.getInstance().getPlayer(charname) != null)
			{
				pc.sendPackets(new S_SystemMessage(charname + "キャラクターがワールドに存在します。"));
				return;
			}
			Connection conn = null;
			PreparedStatement pstm = null;
			
			try {
				conn = L1DatabaseFactory.getInstance().getConnection();
				pstm = conn.prepareStatement("update characters set LocX = 33429, LocY = 32807, MapID = 4 where char_name = ?");
				pstm.setString(1, charname);
				pstm.execute();
			} catch (Exception e) {
				_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			} finally {
				SQLUtil.close(pstm);
				SQLUtil.close(conn);
			}
			pc.sendPackets(new S_SystemMessage(charname + "キャラクターの座標を変更しました。"));
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(cmdName + "[ユーザ名]に入力してください。"));
		}
	}
}
