package pisi.unitedmeows.yystal.clazz;

import pisi.unitedmeows.yystal.parallel.Async;
import pisi.unitedmeows.yystal.parallel.Promise;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

public abstract class soulbound<I, X> {

	protected HashMap<I, X> boundMap;
	protected Promise promise;
	private long checkTimer;

	public soulbound(long _checkTimer) {
		checkTimer = _checkTimer;
		boundMap = new HashMap<>();
		promise = Async.async_loop(() -> {
			Iterator<Map.Entry<I, X>> entryIterator = boundMap.entrySet().iterator();
			while (entryIterator.hasNext()) {
				Map.Entry<I, X> entry = entryIterator.next();
				if (breakBound(entry.getKey(), entry.getValue())) {
					onRemove(entry.getKey(), entry.getValue());
					entryIterator.remove();
				}
			}
		}, _checkTimer);
	}

	public soulbound() {
		this(1000L);
	}

	public X get(I instance) {
		return boundMap.get(instance);
	}

	public void add(I instance, X value) {
		boundMap.put(instance, value);
	}

	public void remove(I instance) {
		boundMap.remove(instance);
	}

	public void onRemove(I instance, X value) {

	}

	public X computeIfAbsent(I instance, Function<I, X> function) {
		return boundMap.computeIfAbsent(instance, function);
	}

	public void stop() {
		promise.stop();
		promise = null;
	}

	public void start() {
		promise = Async.async_loop(() -> {
			boundMap.entrySet().removeIf(entry -> breakBound(entry.getKey(), entry.getValue()));
		}, checkTimer);
	}

	public abstract boolean breakBound(I instance, X value);
}
