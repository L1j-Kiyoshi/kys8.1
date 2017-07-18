package l1j.server.IndunSystem.ValakasRoom;


import java.util.ArrayList;
import java.util.Calendar;

import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_SystemMessage;

public class ValakasStart implements Runnable {
	private short _map;
	private int stage = 1;
	private static final int FIRST_STEP = 1;
	private static final int WAIT_RAID = 2;
	private static final int VALAKAS = 3;
	private static final int END = 4;

	private L1NpcInstance death;
	private L1NpcInstance ifrit;
	private L1NpcInstance phoenix;
	private L1NpcInstance valakas;
	private L1NpcInstance leo1;
	private L1NpcInstance leo2;
	private L1NpcInstance leo3;

	private L1DoorInstance briddge1;
	private L1DoorInstance briddge2;
	private L1DoorInstance briddge3;

	private L1PcInstance pc;

	private boolean Running = true;

	public ArrayList<L1NpcInstance> BasicNpcList;
	private ArrayList<L1NpcInstance> NpcList;
	public ArrayList<L1NpcInstance> BossList;

	public ValakasStart(int id, L1PcInstance pc){
		_map = (short)id;
		this.pc = pc;
	}

	@Override
	public void run() {
		setting();
		NpcList = ValakasRoomSpawn.getInstance().fillSpawnTable(_map, 1, true);
		while(Running){
			try {

				if(NpcList != null){
					for(L1NpcInstance npc : NpcList){
						if(npc == null || npc.isDead())
							NpcList.remove(npc);
					}
				}

				if(leo1 != null && leo1.isDead() 
						&& ((briddge1.getOpenStatus() == ActionCodes.ACTION_Close))) {
					NpcList.remove(leo1);
					briddge1.open();
				}
				if(leo2 != null && leo2.isDead()
						&& ((briddge2.getOpenStatus() == ActionCodes.ACTION_Close))) {
					NpcList.remove(leo2);
					briddge2.open();
				}
				if(leo3 != null && leo3.isDead()
						&& ((briddge3.getOpenStatus() == ActionCodes.ACTION_Close))) {
					NpcList.remove(leo3);
					briddge3.open();
				}
				
				if (BossList != null && valakas != null && pc.isInValakasBoss) {
					stage = VALAKAS;
				}

				if(ifrit != null && ifrit.isDead()) {
					BossList.remove(ifrit);
					stage = END;
				} 
				if(phoenix != null && phoenix.isDead()) {
					BossList.remove(phoenix);
					stage = END;
				} 
				if(valakas != null && valakas.isDead()) {
					BossList.remove(valakas);
					stage = END;
				}


				switch(stage){
				case FIRST_STEP:
					Thread.sleep(2000); 
					//その剣の力を利用しててこの場所を逃がす。
					Broadcaster.broadcastPacket(death, new S_NpcChatPacket(death, "$18645"));
					Thread.sleep(3000); 
					//使用すると、呪いの影響からか、過去出ヴァラカスの戦闘がどんどん浮上たよ...
					Broadcaster.broadcastPacket(death, new S_NpcChatPacket(death, "$18646"));
					Thread.sleep(3000);
					S_NpcChatPacket s_chatpacket = new S_NpcChatPacket(death, "$18647");
					//多分あなたもその戦闘を見ることができるでしょね。お店や..これ以上持ちこたえることができないようだね。
					Broadcaster.broadcastPacket(death, s_chatpacket);
					stage = WAIT_RAID;
					break;
				case WAIT_RAID:
					break;
				case VALAKAS:
					//ヴァラカス：誰が私ケオヌンガ？
					Broadcaster.broadcastPacket(valakas, new S_NpcChatPacket(valakas, "$18869"));
					Thread.sleep(2000);
					if(pc.getMapId() == _map){ 
						//デスナイト：ヴァラカス！いよいよ君会うんだ。
						pc.sendPackets(new S_NpcChatPacket(pc, "$18870"));
					}

					Thread.sleep(2000);
					//ヴァラカス：私の睡眠を起こした代価は...私の奴隷になって一生返済あろう。
					Broadcaster.broadcastPacket(valakas, new S_NpcChatPacket(valakas, "$18871"));
					Thread.sleep(2000);
					if(pc.getMapId() == _map){ 
						//デスナイト：そんな言葉は、私が負けたときも遅くない。
						pc.sendPackets(new S_NpcChatPacket(pc, "$18872"));
					}
					Thread.sleep(2000);
					//ヴァラカス：クク...自信に満ちね..
					Broadcaster.broadcastPacket(valakas, new S_NpcChatPacket(valakas, "$18873"));
					pc.isInValakasBoss = false;
					stage = WAIT_RAID;
					break;
				case END:
					Thread.sleep(2000);
					if(pc.getMapId() == _map){ 
						//pc.sendPackets(new S_ServerMessage(1480));  
						//システムメッセージ：5秒後にテレポートします。
						pc.sendPackets(new S_SystemMessage("しばらくして村に移動されます。"));
					}
					Thread.sleep(3000);
					if(pc.getMapId() == _map){ 
						new L1Teleport().teleport(pc, 33705, 32504, (short)4, 5, true);
						pc.getInventory().consumeItem(203003);
					}
					Running = false;
					break;
				default:
					break;
				}
				Thread.sleep(1000);
				checkPc();
			}catch(Exception e){
			}finally{
				try{
					Thread.sleep(1500);
				}catch(Exception e){}
			}
		}
		endRaid();
	}

	public void Start(){
		Calendar cal = Calendar.getInstance();
		int hour = Calendar.HOUR;
		int minute = Calendar.MINUTE;
		/** 0 午前、1午後 * */
		String AMorPM = "午後";
		if (cal.get(Calendar.AM_PM) == 0) {
			AMorPM = "午前";
		}
		GeneralThreadPool.getInstance().schedule(this,2000);
	        System.out.println(""+ AMorPM + " " + cal.get(hour) + "時" + cal.get(minute) + "分" + "   ■■■■■■ 火竜の避難所を開始 " +  _map+" ■■■■■■");
	}



	private void setting(){
		for(L1NpcInstance npc : BasicNpcList){
			if(npc != null){
				if(npc.getName().equalsIgnoreCase("デスナイト")){
					death = npc;
				}
				if(npc.getName().equalsIgnoreCase("火竜のレオ")){
					leo1 = npc;
				}
				if(npc.getName().equalsIgnoreCase("火竜のクリムゾン レオ")){
					leo2 = npc;
				}
				if(npc.getName().equalsIgnoreCase("火竜のバーニング レオ")){
					leo3 = npc;
				}
				if (npc instanceof L1DoorInstance) {
					L1DoorInstance door = (L1DoorInstance) npc;
					if(npc.getName().equalsIgnoreCase("足1")){
						briddge1 = door;
					}
					if(npc.getName().equalsIgnoreCase("足2")){
						briddge2 = door;
					}
					if(npc.getName().equalsIgnoreCase("足3")){
						briddge3 = door;
					}
				}
			}
		}
		
		for(L1NpcInstance npc : BossList){
			if(npc != null){
				if(npc.getName().equalsIgnoreCase("イフリート")){
					ifrit = npc;
				}
				if(npc.getName().equalsIgnoreCase("フェニックス")){
					phoenix = npc;
				}
				if(npc.getName().equalsIgnoreCase("ヴァラカス")){
					valakas = npc;
				}
			}
		}
	}

	private void checkPc() {
		int check = 0;
		for (L1Object obj : L1World.getInstance().getVisibleObjects(_map).values()) {
			if (obj instanceof L1PcInstance) {
				check = 1;
			}
		}
		if (check == 0) {
			if (pc != null) {
				pc = null;
			}
			Running = false;
		}
	}
	

	private void endRaid(){
		Calendar cal = Calendar.getInstance();
		int hour = Calendar.HOUR;
		int minute = Calendar.MINUTE;
		/** 0 午前、1午後 * */
		String AMorPM = "午後";
		if (cal.get(Calendar.AM_PM) == 0) {
			AMorPM = "午前";
		}
		for (L1Object ob : L1World.getInstance().getVisibleObjects(_map).values()) {
			if(ob == null)
				continue;
			if(ob instanceof L1ItemInstance){
				L1ItemInstance obj = (L1ItemInstance)ob;
				L1Inventory groundInventory = L1World.getInstance().getInventory(obj.getX(), obj.getY(), obj.getMapId());
				groundInventory.removeItem(obj);
			}else if(ob instanceof L1NpcInstance){
				L1NpcInstance npc = (L1NpcInstance)ob;
				npc.deleteMe();
			}
		}
		pc = null;
		death = null;
		ifrit = null;
		phoenix = null;
		valakas = null;
		leo1 = null;
		leo2 = null;
		leo3 = null;
		briddge1 = null;
		briddge2 = null;
		briddge3 = null;
		if (NpcList != null) NpcList.clear();
		if (BasicNpcList != null) BasicNpcList.clear();
		if (BossList != null) BossList.clear();
		ValakasRoomSystem.getInstance().removeStart(_map);
		        System.out.println(""+ AMorPM + " " + cal.get(hour) + "時" + cal.get(minute) + "分" + "   ■■■■■■ 火竜避難所終了 " +  _map+" ■■■■■■");

	}
}
