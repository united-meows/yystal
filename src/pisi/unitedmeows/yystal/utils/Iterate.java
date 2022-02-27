package pisi.unitedmeows.yystal.utils;

@FunctionalInterface
public interface Iterate<X> {
	public boolean next(X object);
}