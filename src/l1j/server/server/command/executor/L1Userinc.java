package l1j.server.server.command.executor;

import java.util.Random;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.RepeatTask;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1Userinc implements L1CommandExecutor {
	@SuppressWarnings("unused")
	private static Logger _log = Logger.getLogger(L1Userinc.class.getName());

	private L1Userinc() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Userinc();
	}

	private static int _buffMaxCount = 0;
	private static int _remainBuffTime = 0;
	private static int _totalBuffTime = 0;

	private static Random _random = new Random(System.nanoTime());

	class UserCountBuffTimer extends RepeatTask {
		public UserCountBuffTimer() {
			super(2000);
		}

		@Override
		public void execute() {

			_remainBuffTime = _remainBuffTime - 2;

			if (_remainBuffTime < 1) {
				_remainBuffTime = 0;

				Config.WHOIS_CONTER = _buffMaxCount;
				cancel();
				_UserCountBuffTimer = null;
			} else {
				int incCount = (_buffMaxCount * 2 / _totalBuffTime);

				int additionalBuffRatio = ((_buffMaxCount * 1000) / _totalBuffTime) % 1000;

				if (_random.nextInt(1000) < additionalBuffRatio) {
					incCount += 2;
				}

				Config.WHOIS_CONTER += incCount;
			}
		}
	}

	private static UserCountBuffTimer _UserCountBuffTimer = null;

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer st = new StringTokenizer(arg);
			String inc = st.nextToken();

			if (inc.equalsIgnoreCase("reset")) {
				if (_UserCountBuffTimer != null) {
					_UserCountBuffTimer.cancel();
					_UserCountBuffTimer = null;
				}

				Config.WHOIS_CONTER = 0;
				_buffMaxCount = 0;
				_remainBuffTime = 0;
				_totalBuffTime = 0;
				return;
			} else if (inc.equalsIgnoreCase("0")) {
				pc.sendPackets(new S_SystemMessage("現在あら人員：" + Config.WHOIS_CONTER));
				pc.sendPackets(new S_SystemMessage("全体あら人数：" + _buffMaxCount));
				pc.sendPackets(new S_SystemMessage("残りあら人員：" + (_buffMaxCount - Config.WHOIS_CONTER)));
				pc.sendPackets(new S_SystemMessage("全体あら時間：" + _totalBuffTime + "超"));
				pc.sendPackets(new S_SystemMessage("残りあら時間：" + _remainBuffTime + "超"));
				return;
			}

			int count = Integer.parseInt(st.nextToken());

			if (count < 0) {
				pc.sendPackets(new S_SystemMessage("0以上の数字を入れてください。"));
				return;
			}

			if (inc.equalsIgnoreCase("~")) {
				int time = Integer.parseInt(st.nextToken());

				if (time < 1) {
					pc.sendPackets(new S_SystemMessage("0以上の時間を入れてください。"));
					return;
				}

				if (time < _totalBuffTime) {
					pc.sendPackets(new S_SystemMessage("入力された時刻が現在設定されてあら時間よりも少なくなります。"));
					return;
				}

				if (count < _buffMaxCount) {
					pc.sendPackets(new S_SystemMessage("入力された数字が、現在設定されてあら人員より少なくなります。"));
					return;
				}

				if (_UserCountBuffTimer != null) {
					_UserCountBuffTimer.cancel();
					_UserCountBuffTimer = null;
				}

				_remainBuffTime += time - _totalBuffTime;
				_totalBuffTime = time;
				_buffMaxCount = count;

				_UserCountBuffTimer = new UserCountBuffTimer();
				GeneralThreadPool.getInstance().execute(_UserCountBuffTimer);

				return;

			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage("+ポン[0]に現在の状態を確認。"));
			pc.sendPackets(new S_SystemMessage("+ポン[〜] [数字] [時間]（時間単位は秒）で自動的にぽっかり。"));
			pc.sendPackets(new S_SystemMessage("+ポン[初期化]で初期化。"));
		}
	}
}
