package l1j.server.server.model;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.serverpackets.S_Message_YN;

public class L1Question implements Runnable{

	private static L1Question _instance;
	public static String maintext;
	public static int good;
	public static int bad;
	public static boolean mainstart;

	public static final int EXECUTE_STATUS_NONE = 0;
	public static final int EXECUTE_STATUS_PREPARE = 1;
	public static final int EXECUTE_STATUS_PROGRESS = 3;
	public static final int EXECUTE_STATUS_FINALIZE = 4;
	
	private int _executeStatus = EXECUTE_STATUS_NONE;

	public static L1Question getInstance(String text) {
		if (_instance == null) {
			_instance = new L1Question(text);			
		}
		return _instance;
	}

	private L1Question(String text){
		good = 0;
		bad = 0;
		maintext = text;

		GeneralThreadPool.getInstance().execute(this);
	}

	@Override
	public void run(){
		try{
			switch(_executeStatus)
			{
				case EXECUTE_STATUS_NONE:
				{
					mainstart = true;
					L1World.getInstance().broadcastServerMessage(" \\fYしばらくして調査を開始します。 （制限時間30秒）");
					L1World.getInstance().broadcastServerMessage("YES =賛成、NO =反対、その他無効〜！");

					_executeStatus = EXECUTE_STATUS_PREPARE;
					GeneralThreadPool.getInstance().schedule(this, 3000L);
				}
				break;

				case EXECUTE_STATUS_PREPARE:
				{
					L1World.getInstance().broadcastPacketToAll(new S_Message_YN(622, maintext));
					
					_executeStatus = EXECUTE_STATUS_PROGRESS;					
					GeneralThreadPool.getInstance().schedule(this, 30000L);
				}
				break;

				case EXECUTE_STATUS_PROGRESS:
				{
					L1World.getInstance().broadcastServerMessage("しばらくしてアンケート調査の結果が発表されます。");

					_executeStatus = EXECUTE_STATUS_FINALIZE;
					GeneralThreadPool.getInstance().schedule(this, 3000L);
				}
				break;

				case EXECUTE_STATUS_FINALIZE:
				{
					L1World.getInstance().broadcastServerMessage(" \\fW[結果] 賛成 : " + good + "票、反対：" + bad + "表");
					_instance = null;		
					mainstart = false;
					maintext = "";			
				}
				break;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
