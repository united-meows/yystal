package pisi.unitedmeows.yystal.parallel.repeaters;

import pisi.unitedmeows.yystal.YYStal;
import pisi.unitedmeows.yystal.clazz.ref;
import pisi.unitedmeows.yystal.parallel.Async;
import pisi.unitedmeows.yystal.parallel.Promise;
import pisi.unitedmeows.yystal.utils.IDisposable;

import java.util.*;

public class RepeaterPool implements IDisposable {

    private Map<Integer, List<Repeater>> repeaterMap;
    private List<Promise> promiseList;

    {
        repeaterMap = new HashMap<>();
        promiseList = new LinkedList<>();
    }

    public Repeater createRepeater(final int interval) {
        return registerRepeater(interval, new Repeater(interval));
    }

    public Repeater registerRepeater(final int interval, final Repeater repeater) {
        repeaterMap.computeIfAbsent(interval, (k) -> {
            final LinkedList<Repeater> list = new LinkedList<Repeater>();
            createAsyncPool(list, interval);
            return list;
        }).add(repeater);
        return repeater;
    }

    public boolean unregisterRepeater(final int interval, final Repeater repeater) {
        List<Repeater> repeaters = repeaterMap.getOrDefault(interval, null);
        return repeaters.remove(repeater);
    }

    private void createAsyncPool(LinkedList<Repeater> repeaters, int interval) {
        final ref<Promise> promise = YYStal.reference();

        promise.set(Async.async_loop_w(() -> {
            repeaters.forEach(Repeater::_tick);

            if (repeaters.isEmpty()) {
                promise.get().stop();
            }

        }, interval));

        promiseList.add(promise.get());
    }


    @Override
    public void close() {
        promiseList.forEach(Promise::stop);
        repeaterMap.values().forEach(x -> x.forEach(Repeater::pause));
        repeaterMap.clear();
    }
}
