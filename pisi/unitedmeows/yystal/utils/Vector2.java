package pisi.unitedmeows.yystal.utils;

public class Vector2<F, S> {
	private F first;
	private S second;

	public Vector2(final F first, final S second) {
		this.first = first;
		this.second = second;
	}

	public F setX(final F val) { return this.first = val; }

	public F getX() { return first; }

	public S getY() { return second; }

	public S setY(final S val) { return this.second = val; }
}
