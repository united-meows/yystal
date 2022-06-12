package pisi.unitedmeows.yystal.clazz;

import pisi.unitedmeows.yystal.parallel.Async;
import pisi.unitedmeows.yystal.parallel.Promise;

import java.util.function.Supplier;

public class living<X> {

    private X value;
    private Promise promise;
    private Supplier<X> supplier;
    private final long interval;

    public living(X _value, long _interval) {
        interval = _interval;
        value = _value;
        promise = Async.async_loop_w(() -> value = supplier.get(), interval);
    }

    public living(long _interval) {
        interval = _interval;
        promise = Async.async_loop(() -> value = supplier.get(), interval);
    }

    public living<X> update(Supplier<X> _supplier) {
        supplier = _supplier;
        return this;
    }

    public living<X> pause() {
        promise.stop();
        return this;
    }

    public living<X> resume() {
        promise = Async.async_loop_w(() -> value = supplier.get(), interval);
        return this;
    }


    public X get() {
        return value;
    }
}
