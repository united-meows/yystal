package pisi.unitedmeows.yystal.parallel;

import pisi.unitedmeows.yystal.YSettings;
import pisi.unitedmeows.yystal.YYStal;
import pisi.unitedmeows.yystal.utils.Stopwatch;
import pisi.unitedmeows.yystal.utils.kThread;

public class TaskWorker extends Thread {

	private boolean busy;
	private Stopwatch stopwatch;
	private long elapsed;
	private long lastFinish;
	private Task currentTask;
	private boolean running;

	public TaskWorker() {
		stopwatch = new Stopwatch();
	}

	@Override
	public void run() {
		running = true;
		while (isRunning() && YYStal.mainThread().isAlive()) {
			currentTask = YYStal.taskPool().nextTask();
			if (currentTask != null) {
				busy = true;
				stopwatch.reset();
				currentTask.run();
				elapsed = stopwatch.elapsed();
				lastFinish = System.currentTimeMillis();
				busy = false;
			}
			kThread.sleep(YYStal.setting(YSettings.TASKWORKER_FETCH_DELAY));
		}
	}

	/* stops the worker */
	public void stopWorker() {
		running = false;
	}

	public void abortWorker() {
		stopWorker();
		try {
			this.stop();
		} catch (Exception ex) {}
	}

	public Task currentTask() {
		return currentTask;
	}

	public long lastTaskFinish() {
		return elapsed;
	}

	public boolean isRunning() {
		return running;
	}

	public boolean isBusy() {
		return busy;
	}
	public long busyTime() { return isBusy() ? stopwatch.elapsed() : 0; }
}
