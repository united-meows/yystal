package pisi.unitedmeows.yystal.utils;

public class Stopwatch {

	private long ms;

	public Stopwatch() {
		reset();
	}

	public boolean isReached(long milliseconds) {
		return getTime() - ms >= milliseconds;
	}

	private long getTime()
	{
		return System.nanoTime() / 1000000L;
	}

	public void reset()
	{
		this.ms = getTime();
	}

	public Stopwatch resetBack(long time) {
		this.ms = getTime() - time;
		return this;
	}

	public long elapsed()
	{
		return getTime() - ms;
	}

}
