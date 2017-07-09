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

	/** Server Manager 1 関連部分 **/
	public static boolean General = false;
	public static boolean Whisper = false;
	public static boolean Global = false;
	public static boolean Clan = false;
	public static boolean Party = false;
	public static boolean Business = false;	
	public static boolean Att = false;
	public static boolean NYEvent = false;

	
	public static boolean ServerDown = false;

	// -- レベル制限
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
