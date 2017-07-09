package l1j.server.server.model;

import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.utils.Teleportation;

public class DoTelThread implements Runnable {

	private L1PcInstance pc;
	private int x;
	private int y;
	private short mapid;
	private int head;

	public DoTelThread(L1PcInstance _pc, int _x, int _y, short _mapid, int _head) {
		pc = _pc;
		x = _x;
		y = _y;
		mapid = _mapid;
		head = _head;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			pc.setTeleportX(x);
			pc.setTeleportY(y);
			pc.setTeleportMapId(mapid);
			pc.setTeleportHeading(head);
			try {
				new Teleportation().doTeleportation(pc);
			} catch (Exception e) {
				System.out.println("テル関連4回エラー");
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
