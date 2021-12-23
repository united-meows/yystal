package pisi.unitedmeows.yystal;

import com.sun.jndi.cosnaming.ExceptionMapper;
import pisi.unitedmeows.yystal.clazz.function;
import pisi.unitedmeows.yystal.clazz.out;
import pisi.unitedmeows.yystal.clazz.ref;
import pisi.unitedmeows.yystal.clazz.valuelock;
import pisi.unitedmeows.yystal.exception.YEx;
import pisi.unitedmeows.yystal.exception.YExManager;
import pisi.unitedmeows.yystal.hook.YString;
import pisi.unitedmeows.yystal.parallel.ITaskPool;
import pisi.unitedmeows.yystal.parallel.pools.BasicTaskPool;
import pisi.unitedmeows.yystal.utils.Stopwatch;

import java.util.HashMap;

public class YYStal {

	private static ITaskPool currentPool;
	private static final HashMap<Thread, Stopwatch> stopWatchMap;
	private static final HashMap<YSettings, Object> settings;
	private static final Thread mainThread;
	private static final HashMap<String, valuelock<?>> valueLocks;

	static {
		mainThread = Thread.currentThread();
		valueLocks = new HashMap<>();
		stopWatchMap = new HashMap<>();
		settings = new HashMap<>();
		setCurrentPool(new BasicTaskPool(5, 12));
		settings.put(YSettings.TASKWORKER_FETCH_DELAY, 1L);
		settings.put(YSettings.TASKPOOL_CONTROL_CHECK_DELAY, 3L);
		settings.put(YSettings.TASK_AWAIT_DELAY, 1L);
		settings.put(YSettings.TCP_CLIENT_QUEUE_CHECK_DELAY, 1L);
		settings.put(YSettings.TCP_CLIENT_WRITE_DELAY, 12L);
	}

	public static void setCurrentPool(ITaskPool taskPool) {
		if (currentPool != null) {
			currentPool.unregister();
		}
		currentPool = taskPool;
		currentPool.register();
	}

	public static void pop(YEx ex) {
		YExManager.pop(ex);
	}

	public static void shot(function func, function pop) {
		YExManager.shot(func, pop);
	}

	public static YEx lastEx() {
		return YExManager.lastEx();
	}

	public static void startWatcher() {
		stopWatchMap.computeIfAbsent(Thread.currentThread(), x -> new Stopwatch()).reset();
	}

	public static long stopWatcher() {
		return stopWatchMap.computeIfAbsent(Thread.currentThread(), x -> new Stopwatch()).elapsed();
	}

	public static <X> ref<X> reference(X initValue) {
		return new ref<X>(initValue);
	}

	public static <X> valuelock<X> value_lock(final String name) {
		boolean status = valueLocks.containsKey(name);
		if (status)
			return (valuelock<X>) valueLocks.get(name);

		valuelock<X> lock = new valuelock<>();
		valueLocks.put(name, lock);
		lock.__setup();
		return lock;
	}

	public static void times(int count, Runnable runnable) {
		for (int i = 0; i < count; i++) {
			runnable.run();
		}
	}

	public static <X> out<X> out() {
		return new out<X>();
	}

	public static <X> X setting(YSettings setting) {
		return (X) settings.get(setting);
	}

	public static YString yString(String str) { return new YString(str); }

	public static Thread mainThread() {
		return mainThread;
	}

	public static ITaskPool taskPool() {
		return currentPool;
	}

}
