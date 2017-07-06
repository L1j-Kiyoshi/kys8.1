package l1j.server.GameSystem.Boss;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import l1j.server.server.utils.PerformanceTimer;

@XmlAccessorType(XmlAccessType.FIELD)
public class L1BossCycle {
	@XmlAttribute(name = "Name")
	private String name;
	@XmlElement(name = "Cycle")
	private Cycle cycle;

	@XmlAccessorType(XmlAccessType.FIELD)
	private static class Cycle {
		@XmlAttribute(name = "Base")
		private String base;
		@XmlAttribute(name = "Period")
		private String period;
		@XmlAttribute(name = "Start")
		private String start;
		@XmlAttribute(name = "End")
		private String end;
		@XmlAttribute(name = "SecondStart")
		private String start2nd;

		public String getBase() {
			return base;
		}

		public String getPeriod() {
			return period;
		}

		public String getStart() {
			return start;
		}

		public String getStart2nd() {
			return start2nd;
		}

		public String getEnd() {
			return end;
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "BossCycleList")
	static class L1BossCycleList {
		@XmlElement(name = "BossCycle")
		private List<L1BossCycle> bossCycles;

		public List<L1BossCycle> getBossCycles() {
			return bossCycles;
		}

		public void setBossCycles(List<L1BossCycle> bossCycles) {
			this.bossCycles = bossCycles;
		}
	}

	private int killHour;
	private int killMinute;
	private int nextSpawnDay;
	private int nextSpawnHour;
	private int nextSpawnMinute;
	private int baseDay;
	private int baseHour;
	private int baseMinute;
	private int periodDay;
	private int periodHour;
	private int periodMinute;
	private int startDay;
	private int startHour;
	private int startMinute;
	private int endDay;
	private int endHour;
	private int endMinute;
	private int start2ndDay;
	private int start2ndHour;
	private int start2ndMinute;
	private int newkillHour;
	private int newkillMinute;

	// private static HashMap<String, L1BossCycle> _cycleMap = new
	// HashMap<String, L1BossCycle>();
	private static ArrayList<L1BossCycle> bossCycleList = new ArrayList<L1BossCycle>();
	private static Logger _log = Logger.getLogger(L1BossCycle.class.getName());

	public void init() throws Exception {
		Cycle spawn = getCycle();
		if (spawn == null || spawn.getPeriod() == null) {
			throw new Exception("Cycle의 Period는 필수");
		}

		String base = spawn.getBase();
		baseDay = getTimeParse(base, "d");
		baseHour = getTimeParse(base, "h");
		baseMinute = getTimeParse(base, "m");

		String period = spawn.getPeriod();
		periodDay = getTimeParse(period, "d");
		periodHour = getTimeParse(period, "h");
		periodMinute = getTimeParse(period, "m");

		String start = spawn.getStart();
		startDay = getTimeParse(start, "d");
		startHour = getTimeParse(start, "h");
		startMinute = getTimeParse(start, "m");

		String end = spawn.getEnd();
		endDay = getTimeParse(end, "d");
		endHour = getTimeParse(end, "h");
		endMinute = getTimeParse(end, "m");

		String start2nd = spawn.getStart2nd();
		start2ndDay = getTimeParse(start2nd, "d");
		start2ndHour = getTimeParse(start2nd, "h");
		start2ndMinute = getTimeParse(start2nd, "m");
	}

	private static int getTimeParse(String target, String search) {
		if (target == null) {
			return 0;
		}
		int n = 0;
		Matcher matcher = Pattern.compile("\\d+" + search).matcher(target);
		if (matcher.find()) {
			String match = matcher.group();
			n = Integer.parseInt(match.replace(search, ""));
		}
		return n;
	}

	public int getBaseDay() {
		return baseDay;
	}

	public void setBaseDay(int i) {
		baseDay = i;
	}

	public int getBaseHour() {
		return baseHour;
	}

	public void setBaseHour(int i) {
		baseHour = i;
	}

	public int getBaseMinute() {
		return baseMinute;
	}

	public void setBaseMinute(int i) {
		baseMinute = i;
	}

	public int getPeriodDay() {
		return periodDay;
	}

	public int getPeriodHour() {
		return periodHour;
	}

	public int getPeriodMinute() {
		return periodMinute;
	}

	public int getStartDay() {
		return startDay;
	}

	public int getStartHour() {
		return startHour;
	}

	public int getStartMinute() {
		return startMinute;
	}

	public int getEndDay() {
		return endDay;
	}

	public int getEndHour() {
		return endHour;
	}

	public int getEndMinute() {
		return endMinute;
	}

	public int getStart2ndDay() {
		return start2ndDay;
	}

	public int getStart2ndHour() {
		return start2ndHour;
	}

	public int getStart2ndMinute() {
		return start2ndMinute;
	}

	public int getKillHour() {
		return killHour;
	}

	public int getKillMinute() {
		return killMinute;
	}

	public void setKillHour(int h) {
		killHour = h;
	}

	public void setKillMinute(int m) {
		killMinute = m;
	}

	public void setNewKillHour(int killHour) {
		newkillHour = killHour;
	}

	public void setNewKillMinute(int killMinute) {
		newkillMinute = killMinute;
	}

	public int getNewKillHour() {
		return newkillHour;
	}

	public int getNewKillMinute() {
		return newkillMinute;
	}

	public int getNextSpawnDay() {
		return nextSpawnDay;
	}

	public int getNextSpawnHour() {
		return nextSpawnHour;
	}

	public int getNextSpawnMinute() {
		return nextSpawnMinute;
	}

	public void setNextSpawnDay(int d) {
		nextSpawnDay = d;
	}

	public void setNextSpawnHour(int h) {
		nextSpawnHour = h;
	}

	public void setNextSpawnMinute(int m) {
		nextSpawnMinute = m;
	}

	public String getName() {
		return name;
	}

	public Cycle getCycle() {
		return cycle;
	}

	public static ArrayList<L1BossCycle> getBossCycleList() {
		return bossCycleList;
	}

	public static void load() {
		PerformanceTimer timer = new PerformanceTimer();
		System.out.print("[L1BossCycle] Loading boss cycle...");
		try {
			// BookOrder 클래스를 바인딩 하는 문맥을 생성
			JAXBContext context = JAXBContext.newInstance(L1BossCycleList.class);

			// XML -> POJO 변환을 실시하는 언마샬을 생성
			Unmarshaller um = context.createUnmarshaller();

			// XML -> POJO 변환
			File file = new File("./data/xml/Cycle/BossCycle.xml");
			L1BossCycleList bossList = (L1BossCycleList) um.unmarshal(file);

			for (L1BossCycle cycle : bossList.getBossCycles()) {
				cycle.init();
				bossCycleList.add(cycle);
			}

			// spawnlist_boss로부터 읽어들여 배치
			// BossSpawnTable.fillSpawnTable();
		} catch (Exception e) {
			_log.log(Level.SEVERE, "BossCycle를 읽어들일 수 없었습니다", e);
			System.exit(0);
		}
	}
}
