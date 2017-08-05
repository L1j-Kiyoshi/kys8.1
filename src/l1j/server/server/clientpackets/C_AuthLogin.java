
package l1j.server.server.clientpackets;

import java.io.IOException;
import java.util.logging.Logger;

import l1j.server.server.GameClient;

public class C_AuthLogin extends ClientBasePacket {
    private static final String C_AUTH_LOGIN = "[C] C_AuthLogin";
    private static Logger _log = Logger.getLogger(C_AuthLogin.class.getName());

    public C_AuthLogin(byte[] decrypt, GameClient client) throws IOException {
        super(decrypt);
        try {

            String accountName = readS().toLowerCase();
            String password = readS();
            String ip = client.getIp();
            String host = client.getHostname();
            _log.finest("Request AuthLogin from user : " + accountName);
        /*
         * accountName = "matis"; password = "apxlwm2";
	     */
            // accountName = Account.getIptoAccountName(ip);
            // password = Account.getAccountNametoPassword(accountName);
            Authorization.getInstance().auth(client, accountName, password, ip, host);
        } catch (Exception e) {
        }
    }

    @Override
    public String getType() {
        return C_AUTH_LOGIN;
    }
}