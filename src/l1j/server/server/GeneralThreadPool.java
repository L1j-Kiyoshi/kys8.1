package l1j.server.server;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import l1j.server.Config;
import l1j.server.server.model.monitor.L1PcMonitor;

public class GeneralThreadPool {
	private static GeneralThreadPool _instance;

	private static final int SCHEDULED_CORE_POOL_SIZE = 50;

	private Executor _executor; // 범용 ExecutorService
	private ScheduledExecutorService _scheduler; // 범용 ScheduledExecutorService
	private ScheduledExecutorService _pcScheduler; // 플레이어의 모니터용 ScheduledExecutorService
	// 일단 L1J 디폴트 상태로, map:4에 있는 아무것도 하고 있지 않는 PC가 1초간에 점유 하는 실행 시간은 약
	// 6ms(AutoUpdate: 약 6ms, ExpMonitor:극소)
	private final int _pcSchedulerPoolSize = 1 + Config.MAX_ONLINE_USERS / 15; // 적당(20 User에 1개정도의 할당)

	public static GeneralThreadPool getInstance() {
		if (_instance == null) {
			_instance = new GeneralThreadPool();
		}
		return _instance;
	}

	private GeneralThreadPool() {
		if (Config.THREAD_P_TYPE_GENERAL == 1) {
			_executor = Executors.newFixedThreadPool(Config.THREAD_P_SIZE_GENERAL);
		} else if (Config.THREAD_P_TYPE_GENERAL == 2) {
			_executor = Executors.newCachedThreadPool();
		} else {
			_executor = null;
		}
		_scheduler = Executors.newScheduledThreadPool(SCHEDULED_CORE_POOL_SIZE, new PriorityThreadFactory("GerenalSTPool", Thread.NORM_PRIORITY));
		_pcScheduler = Executors.newScheduledThreadPool(_pcSchedulerPoolSize, new PriorityThreadFactory("PcMonitorSTPool", Thread.NORM_PRIORITY));
	}

	public void execute(Runnable r) {
		if (_executor == null) {
			Thread t = new Thread(r);
			t.start();
		} else {
			_executor.execute(r);
		}
	}

	public void execute(Thread t) {
		t.start();
	}

	public ScheduledFuture<?> schedule(Runnable r, long delay) {
		try {
			if (delay <= 0) {
				_executor.execute(r);
				return null;
			}
			return _scheduler.schedule(r, delay, TimeUnit.MILLISECONDS);
		} catch (RejectedExecutionException e) {
			return null;
		}
	}

	public ScheduledFuture<?> scheduleAtFixedRate(Runnable r, long initialDelay, long period) {
		return _scheduler.scheduleAtFixedRate(r, initialDelay, period, TimeUnit.MILLISECONDS);
	}

	public ScheduledFuture<?> scheduleAtFixedRateLong(Runnable r, long initialDelay, long period) {
		return _scheduler.scheduleAtFixedRate(r, initialDelay, period, TimeUnit.MILLISECONDS);
	}

	public ScheduledFuture<?> pcSchedule(L1PcMonitor r, long delay) {
		try {
			if (delay <= 0) {
				_executor.execute(r);
				return null;
			}
			return _pcScheduler.schedule(r, delay, TimeUnit.MILLISECONDS);
		} catch (RejectedExecutionException e) {
			return null;
		}
	}

	public ScheduledFuture<?> pcScheduleAtFixedRate(L1PcMonitor r, long initialDelay, long period) {
		return _pcScheduler.scheduleAtFixedRate(r, initialDelay, period, TimeUnit.MILLISECONDS);
	}

	// ThreadPoolManager 로부터 배차
	private class PriorityThreadFactory implements ThreadFactory {
		private final int _prio;
		private final String _name;
		private final AtomicInteger _threadNumber = new AtomicInteger(1);
		private final ThreadGroup _group;

		public PriorityThreadFactory(String name, int prio) {
			_prio = prio;
			_name = name;
			_group = new ThreadGroup(_name);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
		 */
		public Thread newThread(Runnable r) {
			Thread t = new Thread(_group, r);
			t.setName(_name + "-" + _threadNumber.getAndIncrement());
			t.setPriority(_prio);
			return t;
		}

		/*public ThreadGroup getGroup() {
			return _group;
		}*/
	}
}
