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

			if (inc.equalsIgnoreCase("초기화")) {
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
				pc.sendPackets(new S_SystemMessage("현재 뻥튀기 인원 : " + Config.WHOIS_CONTER));
				pc.sendPackets(new S_SystemMessage("전체 뻥튀기 인원 : " + _buffMaxCount));
				pc.sendPackets(new S_SystemMessage("남은 뻥튀기 인원 : " + (_buffMaxCount - Config.WHOIS_CONTER)));
				pc.sendPackets(new S_SystemMessage("전체 뻥튀기 시간 : " + _totalBuffTime + "초"));
				pc.sendPackets(new S_SystemMessage("남은 뻥튀기 시간 : " + _remainBuffTime + "초"));
				return;
			}

			int count = Integer.parseInt(st.nextToken());

			if (count < 0) {
				pc.sendPackets(new S_SystemMessage("0 이상의 숫자를 넣어주세요."));
				return;
			}

			if (inc.equalsIgnoreCase("~")) {
				int time = Integer.parseInt(st.nextToken());

				if (time < 1) {
					pc.sendPackets(new S_SystemMessage("0 이상의 시간를 넣어주세요."));
					return;
				}

				if (time < _totalBuffTime) {
					pc.sendPackets(new S_SystemMessage("입력된 시간이 현재 설정된 뻥튀기 시간보다 적습니다."));
					return;
				}

				if (count < _buffMaxCount) {
					pc.sendPackets(new S_SystemMessage("입력된 숫자가 현재 설정된 뻥튀기 인원보다 적습니다."));
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
			pc.sendPackets(new S_SystemMessage("+뻥 [0] 로 현재 상태 확인."));
			pc.sendPackets(new S_SystemMessage("+뻥 [~] [숫자] [시간] (시간 단위는 초)으로 자동 뻥."));
			pc.sendPackets(new S_SystemMessage("+뻥 [초기화] 로 초기화."));
		}
	}
}
