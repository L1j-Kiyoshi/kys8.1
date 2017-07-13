package l1j.server.server.command.executor;

import java.util.StringTokenizer;

import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_Weather;

public class L1ChangeWeather implements L1CommandExecutor {

	private L1ChangeWeather() {	}

	public static L1CommandExecutor getInstance() {
		return new L1ChangeWeather();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer tok = new StringTokenizer(arg);
			
			int weather = Integer.parseInt(tok.nextToken());
			L1World.getInstance(). setWeather(weather);
			L1World.getInstance(). broadcastPacketToAll(new S_Weather(weather));
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(cmdName + "0〜3（目）、16〜19（非）と入力してください。"));
		}
	}
}
