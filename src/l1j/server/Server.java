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

import l1j.server.server.GameServer;
import l1j.server.server.Controller.LoginController;
import l1j.server.server.utils.PerformanceTimer;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import server.CodecFactory;
import server.ProtocolHandler;

/**
 * l1j-jp의 서버를 기동한다.
 */
public class Server {
	/** 메세지 로그용. */
	private static Logger _log = Logger.getLogger(Server.class.getName());

	/** 로그 설정 파일의 폴더. */
	private static final String LOG_PROP = "./config/log.properties";

	static private ServerBootstrap sb;
	static private CodecFactory cf;
	@SuppressWarnings("unused")
	static private Channel channel;

	private void startLoginServer() {
		try {
			// 정보로딩?
			// GameServer.getInstance().initialize();
			// 게임서버 풀 생성?
			LoginController.getInstance().setMaxAllowedOnlinePlayers(Config.MAX_ONLINE_USERS);
			sb = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),Executors.newCachedThreadPool()));
			cf = new CodecFactory(new ProtocolHandler());
			sb.setPipelineFactory(cf);

			// 서버 성능을 높이기위해 클라가 접속을 끊더라도 세션을 유지하는 알고리즘임..필요없으므로 false
			sb.setOption("child.keepAlive", false);
			// Naggle 비활성.
			sb.setOption("child.tcpNoDelay", true);
			// 받을 패킷의 최대양.
			sb.setOption("child.receiveBufferSize", 2048);
			sb.setOption("connectTimeoutMillis", 300);
			// 서버 활성화.
			channel = sb.bind(new InetSocketAddress(Config.GAME_SERVER_PORT));
			//pla = "Netty";
			//System.out.println("Platform: " + pla + "  Port : "+ Config.GAME_SERVER_PORT + "   Server operation.");
		} catch (Exception e) { /* e.printStackTrace(); */
		}
		;
		// FIXME StrackTrace하면 error
	}

	public void shutdown() {
		// loginServer.shutdown();
		GameServer.getInstance().shutdown();
		// System.exit(0);
	}

	
	public static Calendar StartTime;
	/**
	 * 서버 메인.
	 *
	 * @param args
	 *            커멘드 라인 인수
	 * @throws SQLException
	 * @throws Exception
	 */
	public Server() {
		initLogManager();
		initDBFactory();
		try {

			PerformanceTimer timer = new PerformanceTimer();
			System.out.println("──────────────────────────────────");
			System.out.print("■ [데이터 베이스 초기화] 1 데이터 베이스 초기화");
			// clearDB();
			System.out.println(" 완료 [" + timer.get() + " ms]");
			timer.reset();
			System.out.print("■ [데이터 베이스 초기화] 2 데이터 베이스 초기화");
			// clearDB();
			System.out.println(" 완료 [" + timer.get() + " ms]");
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
		// FIXME StrackTrace하면 error
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

	private void initDBFactory() {// L1DatabaseFactory 초기설정
		L1DatabaseFactory.setDatabaseSettings(Config.DB_DRIVER, Config.DB_URL,
				Config.DB_LOGIN, Config.DB_PASSWORD);
		try {
			L1DatabaseFactory.getInstance();
		} catch (Exception e) { /* e.printStackTrace(); */
		}
		;
		// FIXME StrackTrace하면 error
	}

}
