package pisi.unitedmeows.yystal.parallel;

public class Promise {

	public Promise() {
		valid = true;
	}

	private boolean valid;

	public void stop() {
		valid = false;
	}

	public boolean isValid() {
		return valid;
	}
}
