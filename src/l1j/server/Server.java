package l1j.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import l1j.server.server.GameServer;
import l1j.server.server.Controller.LoginController;
import l1j.server.server.utils.PerformanceTimer;
import server.CodecFactory;
import server.ProtocolHandler;

/**
 * l1j-jpのサーバーを起動する。
 */
public class Server {
	/**メッセージログの。 */
	private static Logger _log = Logger.getLogger(Server.class.getName());

	/** ログ設定ファイルのフォルダ。 */
	private static final String LOG_PROP = "./config/log.properties";

	static private ServerBootstrap sb;
	static private CodecFactory cf;
	@SuppressWarnings("unused")
	static private Channel channel;

	private void startLoginServer() {
		try {
			// 情報の読み込み？
			// GameServer.getInstance().initialize();
			// ゲームサーバープールの作成？
			LoginController.getInstance().setMaxAllowedOnlinePlayers(Config.MAX_ONLINE_USERS);
			sb = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),Executors.newCachedThreadPool()));
			cf = new CodecFactory(new ProtocolHandler());
			sb.setPipelineFactory(cf);

			// サーバーのパフォーマンスを高めるためにクライアントが接続を切断しても、セッションを維持するためのアルゴリズムである。必要ないので、false
			sb.setOption("child.keepAlive", false);
			// Naggle アクティブ。
			sb.setOption("child.tcpNoDelay", true);
			// 受信パケットの最大量。
			sb.setOption("child.receiveBufferSize", 2048);
			sb.setOption("connectTimeoutMillis", 300);
			// サーバーが有効。
			channel = sb.bind(new InetSocketAddress(Config.GAME_SERVER_PORT));
			//pla = "Netty";
			//System.out.println("Platform: " + pla + "  Port : "+ Config.GAME_SERVER_PORT + "   Server operation.");
		} catch (Exception e) { /* e.printStackTrace(); */
		}
		;
		// FIXME StrackTraceと error
	}

	public void shutdown() {
		// loginServer.shutdown();
		GameServer.getInstance().shutdown();
		// System.exit(0);
	}

	
	public static Calendar StartTime;
	/**
	 * サーバーメイン。
	 *
	 * @param args
	 *            コマンドライン引数
	 * @throws SQLException
	 * @throws Exception
	 */
	public Server() {
		initLogManager();
		initDBFactory();
		try {

			PerformanceTimer timer = new PerformanceTimer();
			System.out.println("──────────────────────────────────");
			System.out.print("■ [データベースの初期化] 1 データベースの初期化");
			// clearDB();
			System.out.println(" 完了 [" + timer.get() + " ms]");
			timer.reset();
			System.out.print("■ [データベースの初期化] 2データベースの初期化");
			// clearDB();
			System.out.println(" 完了 [" + timer.get() + " ms]");
			System.out.println("──────────────────────────────────");
			timer = null;
			startGameServer();
			startLoginServer();
			StartTime = Calendar.getInstance();
			StartTime.setTimeInMillis(System.currentTimeMillis());
			
		} catch (Exception e) {
		}

	}

	private void startGameServer() {
		try {
			GameServer.getInstance().initialize();
		} catch (Exception e) { /* e.printStackTrace(); */
		}
		;
		// FIXME StrackTraceと error
	}

	private void initLogManager() {
		File logFolder = new File("log");
		logFolder.mkdir();

		try {
			InputStream is = new BufferedInputStream(new FileInputStream(
					LOG_PROP));
			LogManager.getLogManager().readConfiguration(is);
			is.close();
		} catch (IOException e) {
			// _log.log(Level.SEVERE, "Failed to Load " + LOG_PROP + " File.",
			// e);
			System.exit(0);
		}
		try {
			Config.load();
		} catch (Exception e) {
			// _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			System.exit(0);
		}
	}

	private void initDBFactory() {// L1DatabaseFactory 初期設定
		L1DatabaseFactory.setDatabaseSettings(Config.DB_DRIVER, Config.DB_URL,
				Config.DB_LOGIN, Config.DB_PASSWORD);
		try {
			L1DatabaseFactory.getInstance();
		} catch (Exception e) { /* e.printStackTrace(); */
		}
		;
		// FIXME StrackTraceと error
	}

}
