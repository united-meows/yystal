package pisi.unitedmeows.yystal.utils;

public class Vector2<F, S> {
	private F first;
	private S second;

	public Vector2(final F first, final S second) {
		this.first = first;
		this.second = second;
	}

	public F first(final F val) { return this.first = val; }

	public F first() { return first; }

	public S second() { return second; }

	public S second(final S val) { return this.second = val; }
}
