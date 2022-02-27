package pisi.unitedmeows.yystal.utils;

public class Vector3<F, S, T> {

    private F first;
    private S second;
    private T third;

    public Vector3(F _first, S _second, T _third) {
        first = _first;
        second = _second;
        third = _third;
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
}
