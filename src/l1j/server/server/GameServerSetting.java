package l1j.server.server;

import l1j.server.Config;

public class GameServerSetting
{
	private static GameServerSetting _instance;
	
	public static GameServerSetting getInstance(){
		if (_instance == null){
			_instance = new GameServerSetting();
		}
		return _instance;
	}

	/** Server Manager 1 관련 부분 **/
	public static boolean 일반 = false;
	public static boolean 귓속말 = false;
	public static boolean 글로벌 = false;
	public static boolean 혈맹 = false;
	public static boolean 파티 = false;
	public static boolean 장사 = false;	
	public static boolean Att = false;
	public static boolean NYEvent = false;

	
	public static boolean ServerDown = false;

	// -- 레벨제한
		private int maxLevel = Config.LIMITLEVEL;
		public int get_maxLevel() {
			return maxLevel;
		}
		public void set_maxLevel(int maxLevel) {
			this.maxLevel = maxLevel;
		}
		
	private GameServerSetting(){
	}
}
