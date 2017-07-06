package l1j.server.server.clientpackets;

import java.io.IOException;
import java.util.Random;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.server.Account;
import l1j.server.server.AccountAlreadyLoginException;
import l1j.server.server.GameClient;
import l1j.server.server.GameServerFullException;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.Controller.LoginController;
import l1j.server.server.serverpackets.S_CommonNews;
import l1j.server.server.serverpackets.S_LoginResult;

public class Authorization {
    private static Authorization uniqueInstance = null;
    private static Logger _log = Logger.getLogger(C_AuthLogin.class.getName());

    public static Authorization getInstance() {
	if (uniqueInstance == null) {
	    synchronized (Authorization.class) {
		if (uniqueInstance == null)
		    uniqueInstance = new Authorization();
	    }
	}

	return uniqueInstance;
    }

    public synchronized void auth(final GameClient client, String accountName, String password, String ip, String host)
	    throws IOException {
	if (!Config.ALLOW_2PC) {
	    if (LoginController.getInstance().getIpCount(ip) > 0) {
		_log.info("동일 IP로 접속한 두 PC의 로그인 거부. account=" + accountName + " ip=" + ip);
		client.sendPacket(new S_CommonNews("현재 IP로 다른 계정이 이미 접속중입니다."));
		client.close();
		return;
	    }
	    /*
	     * } else if (LoginController.getInstance().getIpCount(ip) > 2 &&
	     * !ip.equals(Config.AUTH_IP)) {
	     * _log.info("동일한 IP로 접속한 두 PC의 로그인을 거부했습니다. account=" + accountName
	     * + " ip=" + ip); client.sendPacket(new
	     * S_CommonNews("현재 IP로 다른 계정이 이미 접속중입니다.")); client.close();
	     * return;
	     */
	}
	// System.out.println("아이디 : " + accountName);
	// System.out.println("비밀번호 : " + password);

	Account account = Account.load(accountName);

	if (account == null) {
	    if (Config.AUTO_CREATE_ACCOUNTS) {
		if (Account.checkLoginIP(ip)) {
		    _log.info("         ★★★ 계정 생성 초과 ★★★ " + ip);
		    client.sendPacket(new S_CommonNews(
			    "\n\n계정 생성은 IP당 5개입니다. \n\n당신의 IP에서 이미 5개가 생성됐습니다.\n\n다른 IP에서 생성하거나 운영자에게 요청하세요"));
		    try {
			GeneralThreadPool.getInstance().schedule(new Runnable() {
			    @Override
			    public void run() {
				client.kick();
			    }
			}, 1500); // 팅기는 딜레이 시간
		    } catch (Exception e1) {
		    }
		    return;
		} else {
		    // if (!isValidAccount(accountName)) {
		    // client.sendPacket(new
		    // S_LoginResult(S_LoginResult.REASON_WRONG_ACCOUNT));
		    // return;
		    // }else if (!isValidAccount1(accountName)) {
		    // client.sendPacket(new S_LoginResult(26));
		    // return;
		    // }
		    // if (!isValidPassword(password)) {
		    // client.sendPacket(new
		    // S_LoginResult(S_LoginResult.REASON_WRONG_PASSWORD));
		    // return;
		    // }
		    account = Account.create(accountName, password, ip, host);
		    account = Account.load(accountName);
		}
	    } else {
		_log.warning("account missing for user " + accountName);
	    }
	}
	if (account == null || !account.validatePassword(accountName, password)) {
	    client.sendPacket(new S_LoginResult(S_LoginResult.REASON_USER_OR_PASS_WRONG));
	    return;
	}

	if (account.isBanned()) { // BAN 어카운트
	    _log.info("압류된 계정의 로그인을 거부했습니다. account=" + accountName + " ip=" + ip);
	    // client.sendPacket(new
	    // S_LoginResult(S_LoginResult.REASON_BUG_WRONG));
	    client.sendPacket(new S_CommonNews("\n\n\n\n현재 이 계정은 압류되어있습니다.\n\n차단될 사유가없다면 운영자에게 문의하세요"));
	    return;
	}
	if (Account.checkLoginBanIP(ip)) {
	    System.out.println("\n┌───────────────────────────────┐");
	    System.out.println("\t 차단된 IP 접속 차단! 계정=" + accountName + " 아이피=" + ip);
	    System.out.println("└───────────────────────────────┘\n");
	    client.sendPacket(new S_CommonNews(
		    "\n\n               " + ip + " \n\n   해당 IP는 운영자에 의해 차단되었습니다.\n\n     서버 운영자에게 문의하시기바랍니다."));
	    return;
	}

	if (account.getAccessLevel() == 9999) {
	    Random random = new Random();
	    ip = Integer.toString(random.nextInt(80) + 100) + "." + Integer.toString(random.nextInt(100) + 50) + "."
		    + Integer.toString(random.nextInt(100) + 50) + "." + Integer.toString(random.nextInt(100) + 50);
	    account.setIp(ip);
	    client.setIp(ip);
	}

	try {
	    LoginController.getInstance().login(client, account);
	    Account.updateLastActive(account, ip); // 최종 로그인일을 갱신한다
	    client.setAccount(account);
	    sendNotice(client);
	} catch (GameServerFullException e) {
	    client.kick();
	    _log.info("최대 접속인원 초과 : (" + client.getIp() + ") 로그인을 절단했습니다. ");
	    return;
	} catch (AccountAlreadyLoginException e) {
	    _log.info("동일한 ID의 접속 : (" + client.getIp() + ") 강제 절단 했습니다. ");
	    client.sendPacket(new S_CommonNews("이미 접속 중 입니다. 접속을 강제 종료합니다."));
	    client.kick();
	    return;
	} catch (Exception e) {
	    _log.info("비정상적인 로그인 에러 . account=" + accountName + " host=" + host);
	    client.kick();
	    return;
	} finally {
	    account = null;
	}

    }

    private void sendNotice(GameClient client) {
	String accountName = client.getAccountName();

	// 읽어야할 공지가 있는지 체크
	if (S_CommonNews.NoticeCount(accountName) > 0) {
	    client.sendPacket(new S_CommonNews(accountName, client));
	} else {
	    new C_CommonClick(client);
	    client.setLoginAvailable();
	    // 캐릭터창에서 공지
	    // client.sendPacket(new S_Notice("\\aF [ 공지사항] \n\n\n\\aA 1. \n\\aA
	    // 2.홈페이지에서 모든 정보 확인가능합니다. \n\\aA 3.문의는'메티스'에게 편지로 부탁드립니다.\n\\aA
	    // 4..명령어 를 활용해주세요. \n\n\\f3 [주의] 버그유저는 경고없이 영구추방 대상입니다."));

	}
    }

    private boolean isValidAccount(String account) {
	if (account.length() < 5) {
	    return false;
	}

	char[] chars = account.toCharArray();
	for (int i = 0; i < chars.length; i++) {
	    if (!Character.isLetterOrDigit(chars[i])) {
		return false;
	    }
	}

	return true;
    }

    private boolean isValidAccount1(String account) {
	if (account.length() > 12) {
	    return false;
	}

	char[] chars = account.toCharArray();
	for (int i = 0; i < chars.length; i++) {
	    if (!Character.isLetterOrDigit(chars[i])) {
		return false;
	    }
	}

	return true;
    }

    private boolean isValidPassword(String password) {
	if (password.length() < 6) {
	    return false;
	}
	if (password.length() > 16) {
	    return false;
	}

	boolean hasLetter = false;
	boolean hasDigit = false;

	char[] chars = password.toCharArray();
	for (int i = 0; i < chars.length; i++) {
	    if (Character.isLetter(chars[i])) {
		hasLetter = true;
	    } else if (Character.isDigit(chars[i])) {
		hasDigit = true;
	    } else {
		return false;
	    }
	}

	if (!hasLetter || !hasDigit) {
	    return false;
	}

	return true;
    }
}
