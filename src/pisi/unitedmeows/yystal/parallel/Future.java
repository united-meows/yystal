package pisi.unitedmeows.yystal.parallel;

public class Future<X> {

	private X result;
	private boolean set;

	public void setResult(Object result) {
		this.result = (X) result;
		set = true;
	}

	public boolean hasSet() {
		return set;
	}

	public X result() {
		return result;
	}
	public Object resultRaw() {
		return result;
	}
}
