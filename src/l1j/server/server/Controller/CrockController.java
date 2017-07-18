/**
 * 時間の割れ目コントローラ
*/

/**
 * 本サーバー情報の把握
 * 現実時間あたり2日に一回開かれる
 * 時間の割れ目の中にランダムヶ所のみオープン
 * オープン時間を基点に3時間のカウント
 * 時間の割れ目に入ると、テーベリース砂漠にテル
 * テレレス砂漠でダンジョンに移動も可能（ただしボス部屋は不可能）、リース：アデン、帰還：テーベレスでテル
 * オープン後2時間30分後からボス部屋攻略可能（ただしダンジョンで出てきたボス部屋の鍵を所持した者は、先着順20名）
 *3時間以内ボスを攻略していなかった場合は、電源アデンテル
 * ボスを攻略する時、24時間自由オープン
 * プリソプだマー1日3〜4回ジョンヅマン開かとなるだろう 
 * また、3〜4回だからカウントも約1時間程度しか指定。
*/

package l1j.server.server.Controller;

import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.IdFactory;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.L1NpcDeleteTimer;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1FieldObjectInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.utils.L1SpawnUtil;

public class CrockController implements Runnable{

	/** 時間の割れ目オープン時刻(ms) */
	private static long sTime = 0;

	/**時間の割れ目一時ランダム値 */
	private static int rnd = 0;
	
	private static int rnd2 = 0;
	
	/** 時間の亀裂ボス回数 */
	private static int dieCount = 0;

	/** 時間の亀裂ボス攻略判断 */
	private boolean boss = false;
	
	/** 時間の亀裂ボス攻略判断 */
	private boolean killBoss = false;

	/** 時間の割れ目移動時間判断 */
	private boolean move = false;

	/** 時間の割れ目オープンクローズて判断 */
	private boolean isTimeCrock = false;

	/** カウント時間：2時間30分 */
	private static final long TIME = 9000000L;
	
	/** カウント時間：12時間 */
	private static final long DAY = 1000L;
	
	private boolean gmopen = false;
	/** カウント時間：24時間*/
	//private static final long DAY = 86400000L;

	/** シングルトン単一のオブジェクト */
	private static CrockController instance;

	/** 時間の割れ目オブジェクト名 */
	private static final int[] ID = { 200 };
	private static final int[] crockID = { 0, 1 }; //0 ==テーベ、1 ==ティカル
	
	
	// 時間の亀裂 - テーベギフトアイテム番号
	private static final int[][] ItemId = {
		//{ 506, 1}, { 507, 1}, { 508, 1}, { 509, 1}, { 22007, 1}, 

		{ 40074, 1}, { 40087, 1}, { 40076, 1}, 
		{ 140074, 1}, { 140087, 1}, { 240074, 1}, { 240087, 1}, 
		{ 40052, 2 }, { 40053, 2 }, 
		{ 40054, 2 }, { 40055, 2 },

		{ 40074, 1}, { 40087, 1}, { 40076, 1}, 
		{ 140074, 1}, { 140087, 1}, { 240074, 1}, { 240087, 1}, 
		{ 40052, 2 }, { 40053, 2 }, 
		{ 40054, 2 }, { 40055, 2 }
	};
	
	// 時間の亀裂ギフト - ティカルアイテム番号
	private static final int[][] ItemIdTikal = {
		//{ 22194, 1}, { 22195, 1}, { 22007, 1},

		{ 40074, 1}, { 40087, 1}, { 40076, 1}, 
		{ 140074, 1}, { 140087, 1}, { 240074, 1}, { 240087, 1}, 
		{ 40052, 2 }, { 40053, 2 }, 
		{ 40054, 2 }, { 40055, 2 },

		{ 40074, 1}, { 40087, 1}, { 40076, 1}, 
		{ 140074, 1}, { 140087, 1}, { 240074, 1}, { 240087, 1}, 
		{ 40052, 2 }, { 40053, 2 }, 
		{ 40054, 2 }, { 40055, 2 }
	}; // 時間の亀裂 - ティカルのコメント

	/** 時間の割れ目座標 */
	// 1.華東正常2竜骨正常3.小さな竜骨4カオティック神殿5.グルーディン墓
	private static final int[][] loc = {
		{ 32873, 33257, 4 }, 
		{ 32873, 33257, 4 }, 
		{ 32873, 33257, 4 }, 
		{ 32873, 33257, 4 }, 
		{ 32873, 33257, 4 }, 
		{ 32873, 33257, 4 }, 
		{ 32873, 33257, 4 }, 
		{ 32873, 33257, 4 }
		};

	/** ボス部屋先着20名を追加のためのリスト */
	private static final ArrayList<L1PcInstance> sList = new ArrayList<L1PcInstance>();
	
	/** 時刻データフォーマット */
	private static final SimpleDateFormat s = new SimpleDateFormat("HH", Locale.KOREA);

	/** 時刻データフォーマット */
	private static final SimpleDateFormat ss = new SimpleDateFormat("MM-dd HH:mm", Locale.KOREA);

	public static final int EXECUTE_STATUS_NONE = 0;
	public static final int EXECUTE_STATUS_PREPARE = 1;
	public static final int EXECUTE_STATUS_READY = 2;
	public static final int EXECUTE_STATUS_STANDBY = 3;
	public static final int EXECUTE_STATUS_PROGRESS = 4;
	public static final int EXECUTE_STATUS_FINALIZE = 5;

	private int _executeStatus = EXECUTE_STATUS_NONE;

	/**
	 * CrockController オブジェクトリターン
	 * @return	(CrockController）単一のオブジェクト
	*/
	public static CrockController getInstance(){
		if(instance == null) instance = new CrockController();
		return instance;
	}
	/**
	 * 既定のコンストラクタ - シングルトンの実装にprivate
	*/
	private CrockController(){
	}

	/**
	 * Super class abstract method
	*/
	@Override
	public void run() {
		try {
			switch (_executeStatus) {
			case EXECUTE_STATUS_NONE: {
				if (!isOpen() || L1World.getInstance().getAllPlayers().size() <= 0) {
					GeneralThreadPool.getInstance().schedule(this, 1000L); // 1秒
				} else {
					setTimeCrock(true);
					L1World.getInstance().broadcastServerMessage("\\aD時間の亀裂がしばらく表示されます。");
					_executeStatus = EXECUTE_STATUS_PREPARE;
					GeneralThreadPool.getInstance().schedule(this, 15000L); // 15秒
					L1World.getInstance().broadcastServerMessage("\\aD亀裂の位置は、砂漠のオアシス右側です。");
				L1SpawnUtil.spawn2(32873, 33257, (short) 4, 200, 0, 86400 * 1000, 0);
				L1SpawnUtil.spawn2(32780, 32832, (short) 782, 400016, 0, 86400 * 1000, 0);
				L1SpawnUtil.spawn2(32793, 32832, (short) 782, 400017, 0, 86400 * 1000, 0);
				
				L1SpawnUtil.spawn2(32751, 32859, (short) 784, 800019, 0, 86400 * 1000, 0);//左
				L1SpawnUtil.spawn2(32751, 32867, (short) 784, 800018, 0, 86400 * 1000, 0);//右
				
				}
			}
				break;

			case EXECUTE_STATUS_PREPARE: {
				openCrock();

				_executeStatus = EXECUTE_STATUS_READY;
				GeneralThreadPool.getInstance().schedule(this, 15000L);
				// GeneralThreadPool.getInstance().schedule(this, 1000L);
			}
				break;

			case EXECUTE_STATUS_READY: {
				//L1NpcInstance crock = L1World.getInstance().findNpc(npcId());
				//ready(crock);
				_executeStatus = EXECUTE_STATUS_STANDBY;
				GeneralThreadPool.getInstance().schedule(this, TIME); // 2時間
				// GeneralThreadPool.getInstance().schedule(this, 60000); // 2時間
			}
				break;

			case EXECUTE_STATUS_STANDBY: {
				dieCount = 0;
				setBoss(true); // ボス攻略開始
				_executeStatus = EXECUTE_STATUS_PROGRESS;
				GeneralThreadPool.getInstance().schedule(this, 1800000L); // 30分
			}
				break;

			case EXECUTE_STATUS_PROGRESS: {
				
				setBoss(false);
				if (!isTeleport()) {
					setKillBoss(true);
					/** ボス攻略に成功した場合は、12時間後テル */
					if (crocktype() == 0) {// テーベ
						L1World.getInstance().broadcastServerMessage("オシリスの力が回復するまでの時間の亀裂が維持されます");
					} else {// ティカル
						L1World.getInstance().broadcastServerMessage("ククルカンの力が回復するまでの時間の亀裂が維持されます。");
					}
					TelePort();
					GeneralThreadPool.getInstance().schedule(this, 1000);
					_executeStatus = EXECUTE_STATUS_FINALIZE;
					break;
				}
			}

			case EXECUTE_STATUS_FINALIZE: {
				setKillBoss(false);
				L1World.getInstance().broadcastServerMessage("時間の亀裂：亀裂がしばらくして閉じ");
				TelePort();
				clear();
				_executeStatus = EXECUTE_STATUS_NONE;
				GeneralThreadPool.getInstance().schedule(this, 1000L);
			}
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setTimeCrock(boolean status) {
		isTimeCrock = status;
	}

	public boolean isTimeCrock() {
		return isTimeCrock;
	}
	


	private void ready(L1NpcInstance npc){
		for(L1PcInstance pc : L1World.getInstance().getVisiblePlayer(npc))
		pc.sendPackets(new S_DoActionGFX(npc.getId(), 50));
		setMove(true);
		sTime = System.currentTimeMillis();
	}

	/**
	 * 電源アデン村にテル
	*/
	private void TelePort(){
		for(L1PcInstance c : L1World.getInstance().getAllPlayers()){
			if(c.getMap().getId() == 780 || c.getMap().getId() == 781 || c.getMap().getId() == 782
			|| c.getMap().getId() == 783 || c.getMap().getId() == 784){ // 時間の亀裂 - ティカルのコメント
				new L1Teleport().teleport(c, 33970, 33246, (short) 4, 4, true);
			}
		}
	}

	public String OpenTime(){
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(sTime);
		return ss.format(c.getTime());
	}
	/**
	 * 現在時刻を持って来る
	 * @return	(String)	現在時刻（HH：mm）
	*/
	private String getTime(){
		return s.format(Calendar.getInstance().getTime());
	}
	/**
	 * 時間の亀裂が現在開いているかどうかを判断
	 * @return	(boolean)	開いている場合true閉じている場合false
	*/
	private boolean isOpen(){
		Calendar cal = Calendar.getInstance();
		int hour = Calendar.HOUR;
		int minute = Calendar.MINUTE;
		/** 0 午前、1午後 * */
		String ampm = "午後";
		if (cal.get(Calendar.AM_PM) == 0) {
			ampm = "午前";
		}
		if ((ampm.equals("午後") && cal.get(hour) == 6 && cal.get(minute) == 59) || isGmOpen()) {
//			System.out.println（「テーベオープン：「+午前午後+ "" + cal.get（時間）+ "時" + cal.get（分）+ "分"）;
	    	 return true;
	      } 
		return false;
	}

	/**
	 * 時間の割れ目移動状態
	 * @return	(boolean)	move移動するかどうか
	*/
	public boolean isMove(){
		return move;
	}

	/**
	 * 時間の割れ目移動状態設定
	 * @param	(boolean)	move	移動するかどうか
	*/
	private void setMove(boolean move){
		this.move = move;
	}

	/**
	 * 時間の割れ目ボス攻略時間の状態
	 * @return	(boolean)	boss	攻略するかどうか
	*/
	public boolean isBoss(){
		return boss;
	}

	public boolean isKillBoss() {
		return killBoss;
	}
	/**
	 * 時間の割れ目ボス攻略時間の通知
	 * @param	(boolean)	boss攻略するかどうか
	*/
	private void setBoss(boolean boss){
		this.boss = boss;
	}

	private void setKillBoss(boolean killBoss){
		this.killBoss = killBoss;
	}
	/**
	 * 先着20名の登録
	*/
	public boolean add(L1PcInstance c){
		synchronized(this)
		{
			/** 登録されていない */
			if(!sList.contains(c)){
				/** 先着20人以下であれば、 */
				if(sList.size() < 20)
				{
					sList.add(c);
					return true;
				}
			}
		}
		
		return false;
	}

	/**
	 * 先着順リストサイズ返却
	 * @return	(int)	sListのサイズ
	*/
	public int size(){
		return sList.size();
	}

	/**
	 * クリア（初期化）：システムが一回り終了時に再設定のために使われる。
	*/
	private void clear(){
		sList.clear();
		dieCount = 0;
		setBoss(false);
		setMove(false);
		setGmOpen(false);
	
		L1FieldObjectInstance Portal = null; 
		for (L1Object object : L1World.getInstance().getObject()) { 
			if (object instanceof L1FieldObjectInstance) { 
				Portal = (L1FieldObjectInstance) object;
				if (Portal.getNpcTemplate().get_npcId() == 200) { 
					Portal.deleteMe(); 
					Portal = null;
				}
			}
		}
		setTimeCrock(false);
	}

	/**
	 * 先着20名様にアイテム支給 - テーベ
	*/
	public void send() {
		for (L1PcInstance c : sList) {
			if (c == null)continue;
			int[] Item = Item();
			
			L1ItemInstance item = c.getInventory().storeItem(Item[0], Item[1]);
			c.sendPackets(new S_SystemMessage("テーベオシリス祭壇の神聖なアイテムを獲得しました。"));
			for (L1PcInstance partymember : c.getParty().getMembers()) {
				if (partymember != null && !c.isDead()) {
					partymember.sendPackets(new S_ServerMessage(813, "テーベオシリス祭壇", item.getLogName(), c.getName()));
				}
			}
		}
	}
	
	/**
	 *先着20名様にアイテム支給 - ティカル
	*/
	public void sendTikal() {
		for (L1PcInstance c : sList) {
			if (c == null)continue;
			int[] ItemTikal = ItemTikal();
			L1ItemInstance item = c.getInventory().storeItem(ItemTikal[0], ItemTikal[1]);
			c.sendPackets(new S_SystemMessage("ティカル祭壇の神聖なアイテムを獲得しました。"));
			for (L1PcInstance partymember : c.getParty().getMembers()) {
				if (partymember != null && !c.isDead()) {
				partymember.sendPackets(new S_ServerMessage(813, "ティカル祭壇", item.getLogName(), c.getName()));
			}
			}
		}
	} // 時間の亀裂 - ティカルのコメント

	/**
	 *時間の割れ目のいずれかのランダムのIDを返却
	 * @return	(int)	npcIdエンピシ名
	*/
	private void openCrock() {
		rnd = (int) (Math.random() * ID.length);
		rnd2 = (int)(Math.random() * crockID.length);
//		System.out.println("c opcode : " + rnd2);
	}
	
	public int crocktype() {
		return rnd2;
	}

	private int npcId() {
		return ID[rnd];
	}

	/**
	 * 指定されたnpcIdのlocを返却
	 * @return	(int[])	loc座標配列
	*/
	public int[] loc(){
		return loc[rnd];
	}

	/**
	 * 時間の亀裂ボス攻略確認
	 * @return	(boolean)	2ボスだ死んでfalse 1ボス以下殺したらtrue
	*/
	private boolean isTeleport(){
		boolean sTemp = true;
		switch(dieCount()){
			case 2: sTemp = false; break;
			default: sTemp = true; break;
		}
		return sTemp;
	}

	/**
	 * 時間の割れ目テーベボスダイ返却
	 * @return	(int)	dieCount	ボスダイ回数
	*/
	public int dieCount(){
		return dieCount;
	}

	/**
	 * 時間の割れ目テーベボスダイ設定
	 * @param	(int)	dieCount	ボスダイ回数
	*/
	public void dieCount(int dieCount){
		CrockController.dieCount = dieCount;
	}

	/**
	 * アイテム支給名ランダム返却 - テーベ
	 * @return	(int[]) Itemid	支給されるアイテムID、本数
	*/
	private int[] Item(){
		return ItemId[(int)(Math.random() * ItemId.length)];
	}
	
	/**
	 * アイテム支給名ランダム返却 - ティカル
	 * @return	(int[]) Itemid	支給されるアイテムID、本数
	*/
	private int[] ItemTikal(){
		return ItemIdTikal[(int)(Math.random() * ItemIdTikal.length)];
	} // 時間の亀裂 - ティカルのコメント
	/**
	 * Gmによるテーベオープン
	*/
	public boolean isGmOpen(){
		return gmopen;
	}

	public void setGmOpen(boolean gmopen1){
		gmopen = gmopen1;
	}


	public void BossSpawn(int mob, int x, int y, short map1, int time) {
		try {
			L1Npc l1npc = NpcTable.getInstance().getTemplate(mob);
			if (l1npc != null) {
				try {
					String s = l1npc.getImpl();
					Constructor constructor = Class.forName("l1j.server.server.model.Instance." + s + "Instance").getConstructors()[0];
					Object aobj[] = { l1npc };
					L1NpcInstance npc = (L1NpcInstance) constructor.newInstance(aobj);
					npc.setId(IdFactory.getInstance().nextId());
					npc.setMap(map1);
					npc.setX(x);
					npc.setY(y);
					npc.setHomeX(x);
					npc.setHomeY(y);
					npc.setHeading(5);
					
					L1World.getInstance().storeObject(npc);
					L1World.getInstance().addVisibleObject(npc);
					L1Object object = L1World.getInstance().findObject(npc.getId());
					L1NpcInstance newnpc = (L1NpcInstance) object;
					newnpc.onNpcAI();
					newnpc.getLight().turnOnOffLight();
					newnpc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE);
					L1NpcDeleteTimer timer = new L1NpcDeleteTimer(npc, time);
					timer.begin();
				} catch (Exception e) {
					// _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
				}
			}
		} catch (Exception exception) {
		}
	}
}