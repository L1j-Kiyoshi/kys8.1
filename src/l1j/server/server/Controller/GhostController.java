package l1j.server.server.Controller;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.model.Instance.L1PcInstance;

public class GhostController implements Runnable {
	private static Logger _log = Logger.getLogger(GhostController.class.getName());
	private static GhostController _instance;
	private final ArrayList<L1PcInstance> _list = new ArrayList<L1PcInstance>();

	public static synchronized GhostController getInstance() {
		if (_instance == null)
			_instance = new GhostController();
		return _instance;
	}

	private GhostController() {
	}

	@Override
	public void run() {
		while (true) {
			try {
				long now = System.currentTimeMillis();
				for (L1PcInstance pc : _list) {
					if (pc == null) {
						removeMember(pc);
						continue;
					}
					if (pc.ghosttime <= now) {
						if (pc._ghostCount < 16)
							pc._ghostCount++;
						else
							pc._ghostCount = 0;
						pc.makeReadyEndGhost();
					}
				}
				Thread.sleep(1000);
			} catch (Exception e) {
				_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			}
		}
	}

	public void addMember(L1PcInstance pc) {
		if (pc != null && !_list.contains(pc))
			_list.add(pc);
	}

	public void removeMember(L1PcInstance pc) {
		if (pc != null && _list.contains(pc))
			_list.remove(pc);
	}

	public boolean isPlayMember(L1PcInstance pc) {
		return _list.contains(pc);
	}
}