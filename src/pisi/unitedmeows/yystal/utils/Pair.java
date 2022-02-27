package pisi.unitedmeows.yystal.utils;

public class Pair<F, S> {

	private F item1;
	private S item2;

	public Pair(F _item1, S _item2) {
		item1 = _item1;
		item2 = _item2;
	}

	public F item1() {
		return item1;
	}

	public S item2() {
		return item2;
	}

}
