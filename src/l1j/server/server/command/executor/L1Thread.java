package l1j.server.server.command.executor;

import java.util.logging.Logger;

import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1Thread implements L1CommandExecutor {
	@SuppressWarnings("unused")
	private static Logger _log = Logger.getLogger(L1Burf.class.getName());

	private L1Thread() {}

	public static L1CommandExecutor getInstance() {
		return new L1Thread();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			Thread[] th = new Thread[Thread.activeCount()];
			Thread.enumerate(th); 
			for(int i=0; i<th.length; i++){
			pc.sendPackets(new S_SystemMessage("["+i+"]を使用スレッド：[" + th[i]+"]"));
			}
			pc.sendPackets(new S_SystemMessage("現在使用されているスレッドの数：["+Thread.activeCount()+"]"));

		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(cmdName + "本数と入力ください。"));
		}
	}
}
