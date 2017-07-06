package l1j.server.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.StreamUtil;

public class Announcements {
	
	public static final int SLEEP_TIME = 600000;
	
	private static Announcements _instance;
	private final List<String> _announcements = new ArrayList<String>();
	private static Logger _log = Logger.getLogger(Announcements.class.getName());

	private Announcements() {
		loadAnnouncements();
	}

	public static Announcements getInstance() {
		if (_instance == null) {
			_instance = new Announcements();
		}

		return _instance;
	}

	private void loadAnnouncements() {
		_announcements.clear();
		File file = new File("data/announcements.txt");
		if (file.exists()) {
			readFromDisk(file);
		} else {
			_log.config("data/announcements.txt doesn't exist");
		}
	}

	public void showAnnouncements(L1PcInstance showTo) {
		for (String msg : _announcements) {
			showTo.sendPackets(new S_SystemMessage(msg));
		}
	}

	private void readFromDisk(File file) {
		LineNumberReader lnr = null;
		try {
			int i = 0;
			String line = null;
			lnr = new LineNumberReader(new FileReader(file));
			StringTokenizer st = null;
			while ((line = lnr.readLine()) != null) {
				st = new StringTokenizer(line, "\n\r");
				if (st.hasMoreTokens()) {
					String announcement = st.nextToken();
					_announcements.add(announcement);

					i++;
				}
			}

			_log.config("공지사항" + i + "로드");
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			StreamUtil.close(lnr);
		}
	}

	public void announceToAll(String msg) {
		L1World.getInstance().broadcastServerMessage(msg);
	}
}