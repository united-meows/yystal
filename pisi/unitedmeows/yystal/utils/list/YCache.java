package pisi.unitedmeows.yystal.utils.list;

import pisi.unitedmeows.yystal.parallel.Async;
import pisi.unitedmeows.yystal.utils.list.cache.YCacheNode;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class YCache<K, V> implements Map<K, V> {

    private YCacheNode<K, V> head;
    private YCacheNode<K, V> tail;
    private long deleteAfter;

    public YCache(long _deleteAfter) {
        deleteAfter = _deleteAfter;
    }

    private void deleteLast() {
        final YCacheNode<K, V> newTail = tail.before();
        if (newTail != null)
            newTail.next(null);

        tail = newTail;
        if (size() == 0)
            head = tail;
    }

    @Override
    public int size() {
        int count = 0;

        YCacheNode<K, V> current = head;
        while (current != null) {
            count++;
            current = current.next();
        }

        return count;
    }

    @Override
    public boolean isEmpty() {
        return head == null;
    }

    @Override
    public boolean containsKey(Object key) {
        YCacheNode<K, V> current = head;

        while (current != null) {
            if (current.key().equals(key))
                return true;

            current = current.next();
        }

        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        YCacheNode<K, V> current = head;

        while (current != null) {
            if (current.value().equals(value))
                return true;

            current = current.next();
        }

        return false;
    }

    @Override
    public V get(Object key) {
        YCacheNode<K, V> current = head;
        while (current != null) {
            if (current.key().equals(key))
                return current.value();

            current = current.next();
        }

        return null;
    }

    @Override
    public V put(K key, V value) {

        YCacheNode<K, V> node = new YCacheNode<>();
        node.key(key).value(value);

        if (head == null)
            head = node;

        if (tail != null) {
            node.before(tail);
            tail.next(node);
        }
        tail = node;

        Async.async_w(() -> {
            YCacheNode<K, V> before = node.before();

            if (before != null)
                before.next(node.next());
        }, deleteAfter);
        return value;
    }

    @Override
    public V remove(Object key) {
        YCacheNode<K, V> current = head;

        while (current != null) {
            if (current.key().equals(key)) {
                YCacheNode<K, V> before = current.before();
                if (before != null)
                    before.next(current.next());

                return current.value();
            }

            current = current.next();
        }

        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<?, ?> k : m.entrySet()) {
            YCacheNode<K, V> node = new YCacheNode<>();
            node.key((K) k.getKey()).value(k.getValue());

            if (head == null)
                head = node;

            if (tail != null) {
                node.before(tail);
                tail.next(node);
            }
            tail = node;
        }

        Async.async_w(() -> {
            for (K k : m.keySet()) {
                remove(k);
            }
        }, deleteAfter);
    }

    @Override
    public void clear() {

    }

    @Override
    public Set<K> keySet() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return null;
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        return Map.super.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        Map.super.forEach(action);
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        Map.super.replaceAll(function);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return Map.super.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return Map.super.remove(key, value);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return Map.super.replace(key, oldValue, newValue);
    }

    @Override
    public V replace(K key, V value) {
        return Map.super.replace(key, value);
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return Map.super.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return Map.super.computeIfPresent(key, remappingFunction);
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return Map.super.compute(key, remappingFunction);
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return Map.super.merge(key, value, remappingFunction);
    }
}
