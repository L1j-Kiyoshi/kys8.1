package l1j.server.server.clientpackets;

import java.util.logging.Logger;

import l1j.server.server.GameClient;
import l1j.server.server.Controller.LoginController;

public class C_ReturnToLogin extends ClientBasePacket {

	private static final String C_RETURN_TO_LOGIN = "[C] C_ReturnToLogin";
	private static Logger _log = Logger.getLogger(C_ReturnToLogin.class.getName());

	public C_ReturnToLogin(byte decrypt[], GameClient client) throws Exception {
		super(decrypt);
		try {
			String account = client.getAccountName();
			StringBuilder sb = new StringBuilder();
			_log.finest(sb.append("account : ").append(account).toString());
			sb = null;
			
			if (client.getActiveChar() != null) {
				client.kick();
				client.close();
				System.out.println("─────────────────────────────────");
				System.out.println("バグの疑い - アカウント名" + client.getAccountName());
				System.out.println("─────────────────────────────────");
				return;
			}
			
			LoginController.getInstance().logout(client);
		} catch (Exception e) {
		} finally {
			clear();
		}
	}
	
	@Override
	public String getType() {
		return C_RETURN_TO_LOGIN;
	}

}
