package pisi.unitedmeows.yystal.parallel;

public class Future<X> {

	private boolean completed;
	private X result;

	public Future() { }

	public void end(Object value) {
		result = (X) value;
	}

	public X result() {
		return result;
	}

	public X await() {
		while (!isCompleted()) {
			try {
				Thread.sleep(1 /*TODO: AWAIT_WAIT_DELAY */);
			} catch (InterruptedException e) {

			}
		}
		return result();
	}

	public void markCompleted() {
		completed = true;
	}

	public boolean isCompleted() {
		return completed;
	}
}
