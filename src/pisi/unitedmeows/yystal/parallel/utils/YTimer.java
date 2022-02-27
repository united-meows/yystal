package pisi.unitedmeows.yystal.parallel.utils;

import pisi.unitedmeows.yystal.parallel.Async;
import pisi.unitedmeows.yystal.parallel.Promise;

/* runs the method every x milliseconds */
public abstract class YTimer {

	private Promise promise;
	private long interval;

	public YTimer(final long _interval) {
		setInterval(_interval);
	}

	public abstract void tick();

	public YTimer start() {
		if (promise == null || !promise.isValid()) {
			promise = Async.async_loop_w(this::tick, interval);
		}
		return this;
	}

	public YTimer stop() {
		promise.stop();
		return this;
	}

	public YTimer setInterval(final long _interval) {
		interval = _interval;
		if (promise != null && promise.isValid()) {
			promise.stop();
			promise = Async.async_loop_w(this::tick, interval);
		}
		return this;
	}

}
