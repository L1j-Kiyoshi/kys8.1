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
		_log.info("同じIPに接続した2台のPCのログインを拒否。 account =" + accountName + " ip=" + ip);
		client.sendPacket(new S_CommonNews("現在のIPで別のアカウントがすでに接続中です。"));
		client.close();
		return;
	    }
	    /*
	     * } else if (LoginController.getInstance().getIpCount(ip) > 2 &&
	     * !ip.equals(Config.AUTH_IP)) {
	     * _log.info("同じIPアドレスに接続した2台のPCのログインを拒否した。 account = "+ accountName
	     * + " ip=" + ip); client.sendPacket(new
	     * S_CommonNews("現在のIPで別のアカウントがすでに接続中です。 "））; client.close（）;
	     * return;
	     */
	}
	// System.out.println("ユーザ名： "+ accountName）;
	// System.out.println("パスワード： "+ password）;

	Account account = Account.load(accountName);

	if (account == null) {
	    if (Config.AUTO_CREATE_ACCOUNTS) {
		if (Account.checkLoginIP(ip)) {
		    _log.info("         ★★★ アカウントの作成を超え ★★★ " + ip);
		    client.sendPacket(new S_CommonNews(
			    "\n\nアカウントの作成は、 IPごとに5個です。 \n\nあなたのIPアドレスから既に5つ作成されました。\n\n別のIPで作成するか、オペレータに依頼してください"));
		    try {
			GeneralThreadPool.getInstance().schedule(new Runnable() {
			    @Override
			    public void run() {
				client.kick();
			    }
			}, 1500); // ティンギはディレイ時間
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

	if (account.isBanned()) { // BANアカウント
	    _log.info("差し押さえされたアカウントのログインを拒否した。 account =" + accountName + " ip=" + ip);
	    // client.sendPacket(new
	    // S_LoginResult(S_LoginResult.REASON_BUG_WRONG));
	    client.sendPacket(new S_CommonNews("\n\n\n\n現在、このアカウントは押収されています。\n\nブロックされる理由がない場合は、オペレーターにお問い合わせください"));
	    return;
	}
	if (Account.checkLoginBanIP(ip)) {
	    System.out.println("\n┌───────────────────────────────┐");
	    System.out.println("\t ブロックされたIP接続をブロック！アカウント=" + accountName + "アイピー=" + ip);
	    System.out.println("└───────────────────────────────┘\n");
	    client.sendPacket(new S_CommonNews(
		    "\n\n               " + ip + " \n\n   そのIPは、オペレーターによってブロックされました。\n\nサーバー管理者にお問い合わせください。"));
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
	    Account.updateLastActive(account, ip); // 最終ログイン日を更新する
	    client.setAccount(account);
	    sendNotice(client);
	} catch (GameServerFullException e) {
	    client.kick();
	    _log.info("最大接続人数を超え：（" + client.getIp() + "）ログインを切断しました。");
	    return;
	} catch (AccountAlreadyLoginException e) {
	    _log.info("同じIDの接続：（" + client.getIp() + "）強​​制切断しました。");
	    client.sendPacket(new S_CommonNews("すでに接続中です。接続を強制的に終了します。"));
	    client.kick();
	    return;
	} catch (Exception e) {
	    _log.info("異常なログインエラー。 account =" + accountName + " host=" + host);
	    client.kick();
	    return;
	} finally {
	    account = null;
	}

    }

    private void sendNotice(GameClient client) {
	String accountName = client.getAccountName();

	// 読むべきお知らせがあるかチェック
	if (S_CommonNews.NoticeCount(accountName) > 0) {
	    client.sendPacket(new S_CommonNews(accountName, client));
	} else {
	    new C_CommonClick(client);
	    client.setLoginAvailable();
	    //キャラクターウィンドウで公知
	    // client.sendPacket(new S_Notice("\\aF [お知らせ] \\ n \\ n \\ n \\\\ aA 1 \\ n \\\\ aA
	    // 2.ホームページでは、すべての情報を確認できます。 \\ n \\\\ aA 3.お問い合わせは、「メティス」に手紙でお願いします。\\ n \\\\ aA
	    // 4..コマンドを利用してください。 \\ n \\ n \\\\ f3【注意】バグユーザーは警告なしに永久追放対象です。 "））;

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
