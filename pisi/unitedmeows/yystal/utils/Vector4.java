package pisi.unitedmeows.yystal.utils;

public class Vector4<F, S, T, Q> {
	private F first;
	private S second;
	private T third;
	private Q fourth;

	public Vector4(F _first, S _second, T _third, Q _fourth) {
		first = _first;
		second = _second;
		third = _third;
		fourth = _fourth;
	}

	public F first() {
		return first;
	}

	public S second() {
		return second;
	}

	public T third() {
		return third;
	}

	public Q fourth() {
		return fourth;
	}
}
