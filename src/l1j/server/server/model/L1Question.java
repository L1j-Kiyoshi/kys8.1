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
					L1World.getInstance().broadcastServerMessage(" \\fY잠시 후 설문조사가 시작됩니다. (제한시간 30초)");
					L1World.getInstance().broadcastServerMessage(" YES = 찬성, NO = 반대, 그외 무효~!");

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
					L1World.getInstance().broadcastServerMessage(" 잠시 후 설문조사 결과가 발표됩니다.");

					_executeStatus = EXECUTE_STATUS_FINALIZE;
					GeneralThreadPool.getInstance().schedule(this, 3000L);
				}
				break;

				case EXECUTE_STATUS_FINALIZE:
				{
					L1World.getInstance().broadcastServerMessage(" \\fW[결과] 찬성 : " + good + "표, 반대 : " + bad + "표");
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
