package pisi.unitedmeows.yystal.utils.list.cache;

import pisi.unitedmeows.yystal.utils.list.YCache;

public class YCacheNode<K, V> {

    private K key;
    private V value;
    private YCacheNode<K, V> next;
    private YCacheNode<K, V> before;

    public YCacheNode() {}

    public YCacheNode(K _key, V _value) {
        key = _key;
        value = _value;
    }

    public K key() {
        return key;
    }

    public V value() {
        return value;
    }

    public YCacheNode key(K _key) {
        key = _key;
        return this;
    }

    public YCacheNode value(V _value) {
        value = _value;
        return this;
    }

    public YCacheNode<K, V> next() {
        return next;
    }

    public YCacheNode next(YCacheNode<K, V> _next) {
        next = _next;
        return this;
    }

    public YCacheNode<K, V> before() {
        return before;
    }

    public YCacheNode before(YCacheNode<K, V> _before) {
        before = _before;
        return this;
    }
}
