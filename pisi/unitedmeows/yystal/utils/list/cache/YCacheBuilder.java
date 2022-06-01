package pisi.unitedmeows.yystal.utils.list.cache;

import pisi.unitedmeows.yystal.utils.list.YCache;

public class YCacheBuilder<K, V> {

    private long deleteAfter;

    public static <K, V> YCacheBuilder<K, V> builder() {
        return new YCacheBuilder<K, V>();
    }

    public YCacheBuilder<K, V> deleteAfter(long _deleteAfter) {
        deleteAfter = _deleteAfter;
        return this;
    }

    public <K, V> YCache<K, V> build() {
        return new YCache<K, V>(deleteAfter);
    }
}
