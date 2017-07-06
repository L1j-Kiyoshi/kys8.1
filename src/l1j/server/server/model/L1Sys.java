package l1j.server.server.model;

import l1j.server.Config;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ChatPacket;



public class L1Sys implements Runnable {
	private boolean loop = true;


	private static L1Sys _instance;

	public static L1Sys getInstance() {
		if (_instance == null) {
			_instance = new L1Sys();
		}
		return _instance;
	}
	

	private void sendMessage(String msg) {
		for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			pc.sendPackets(new S_ChatPacket(pc, msg));
		}
	}

	
	@Override 
	public void run() {
		while (loop) {
			try {
				String sys1 = String.format("%s", Config.sys1);
				String sys2 = String.format("%s", Config.sys2);
				String sys3 = String.format("%s", Config.sys3);
				String sys4 = String.format("%s", Config.sys4);
				String sys5 = String.format("%s", Config.sys5);
				String sys6 = String.format("%s", Config.sys6);
				String sys7 = String.format("%s", Config.sys7);
//				String sys8 = String.format("%s", Config.sys8);
//				String sys9 = String.format("%s", Config.sys9);
//				String sys10 = String.format("%s", Config.sys10);
//				String sys11 = String.format("%s", Config.sys11);
//				String sys12 = String.format("%s", Config.sys12);
//				String sys13 = String.format("%s", Config.sys13);
//				String sys14 = String.format("%s", Config.sys14);
//				String sys15 = String.format("%s", Config.sys15);
//				String sys16 = String.format("%s", Config.sys16);

				Thread.sleep(10000*Config.systime);   			
				sendMessage(sys1);
				Thread.sleep(100000*Config.systime);
				sendMessage(sys2);
				Thread.sleep(100000*Config.systime);
				sendMessage(sys3);
				Thread.sleep(100000*Config.systime);
				sendMessage(sys4);
				Thread.sleep(100000*Config.systime);
				sendMessage(sys5);
				Thread.sleep(100000*Config.systime);
				sendMessage(sys6);
				Thread.sleep(100000*Config.systime);
				sendMessage(sys7);
				Thread.sleep(100000*Config.systime);
//				sendMessage(sys8);
//				Thread.sleep(100000*Config.systime);
//				sendMessage(sys9);
//				Thread.sleep(100000*Config.systime);
//				sendMessage(sys10);
//				Thread.sleep(100000*Config.systime);
//				sendMessage(sys11);
//				Thread.sleep(100000*Config.systime);
//				sendMessage(sys12);
//				Thread.sleep(100000*Config.systime);
//				sendMessage(sys13);
//				Thread.sleep(100000*Config.systime);
//				sendMessage(sys14);
//				Thread.sleep(100000*Config.systime);
//				sendMessage(sys15);
//				Thread.sleep(100000*Config.systime);
//				sendMessage(sys16);
			} catch (Exception exception) {
			}
		}
	}
}

