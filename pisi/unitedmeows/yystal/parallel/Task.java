package pisi.unitedmeows.yystal.parallel;

import pisi.unitedmeows.yystal.utils.Stopwatch;

public class Task {

	private boolean executed;
	private IFunction<?> function;
	private long took;
	private long startTime;
	private Future<?> future;

	public Task(IFunction<?> function, Future<?> future) {
		this.function = function;
		this.future = future;
	}

	public void run() {
		startTime = System.currentTimeMillis();
		if (future != null) {
			future.setResult(function.run());
		} else {
			function.run();
		}
		took = System.currentTimeMillis() - startTime;
		executed = true;
	}

	public boolean isExecuted() {
		return executed;
	}

	public Future<?> future() {
		return future;
	}

	public long elapsed() {
		return isExecuted() ? took() : System.currentTimeMillis() - startTime;
	}

	public long took() {
		return took;
	}


}
