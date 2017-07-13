package l1j.server.server.command.executor;

import java.util.StringTokenizer;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1Burf implements L1CommandExecutor {
	
	private L1Burf() {  }

	public static L1CommandExecutor getInstance() {
		return new L1Burf();
	}

	static class Burfskill implements Runnable {
		private L1PcInstance _pc = null;
		private int _sprid;
		private int _count;
		
		public Burfskill(L1PcInstance pc, int sprid, int count) {
			_pc = pc;
			_sprid = sprid;
			_count = count;
		}

		@Override
		public void run() {
			for (int i = 0; i < _count; i++) {
				try {
					Thread.sleep(500);
					int num = _sprid + i;
					_pc.sendPackets(new S_SystemMessage("スキル番号："+num+""));
					_pc.sendPackets(new S_SkillSound(_pc.getId(), _sprid+i));
					_pc.broadcastPacket(new S_SkillSound(_pc.getId(), _sprid+i));
				} catch (Exception exception) {
					break;
				}
			}

		}

	}
	
	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer st = new StringTokenizer(arg);
			int sprid = Integer.parseInt(st.nextToken(), 10);
			int count = Integer.parseInt(st.nextToken(), 10);
			
			Burfskill spr = new Burfskill(pc, sprid, count);
			GeneralThreadPool.getInstance().execute(spr);
			
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(cmdName + "【castgfx]と入力してください。"));
		}
	}
/*	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer stringtokenizer = new StringTokenizer(arg);
			int sprid = Integer.parseInt(stringtokenizer.nextToken());

			pc.sendPackets(new S_SkillSound(pc.getId(), sprid));
			pc.broadcastPacket(new S_SkillSound(pc.getId(), sprid));
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(cmdName + " [castgfx]と入力してください。 "））;
		}
	}*/
}
