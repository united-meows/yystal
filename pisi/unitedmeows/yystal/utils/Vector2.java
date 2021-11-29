package pisi.unitedmeows.yystal.utils;

public class Vector2<F, S> {
	private F first;
	private S second;

	public Vector2(F first, S second) {
		this.first = first;
		this.second = second;
	}

	public F first() {
		return first;
	}

	public S second() {
		return second;
	}
}
