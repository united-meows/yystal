package pisi.unitedmeows.yystal.parallel;

public class Promise {
	private boolean valid = true;

	public void start() {
		valid = true;
	}

	public void stop() {
		valid = false;
	}

	public boolean isValid() {
		return valid;
	}
}
