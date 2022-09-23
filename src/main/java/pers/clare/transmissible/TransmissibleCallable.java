package pers.clare.transmissible;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;

public class TransmissibleCallable<V> implements Callable<V> {
    private final Callable<V> callable;

    private final Map<TransmissibleThreadLocal<Object>, Object> cache;

    private final String parent;

    public static <V> Callable<V> of(Callable<V> callable) {
        Map<TransmissibleThreadLocal<Object>, Object> cache = TransmissibleThreadLocal.export();
        if (cache == null) {
            return callable;
        }
        return new TransmissibleCallable<>(callable, cache);
    }

    public static <V> Collection<? extends Callable<V>> of(Collection<? extends Callable<V>> callables) {
        Collection<Callable<V>> tasks = new ArrayList<>();
        for (Callable<V> callable : callables) {
            tasks.add(of(callable));
        }
        return tasks;
    }

    public TransmissibleCallable(Callable<V> callable, Map<TransmissibleThreadLocal<Object>, Object> cache) {
        if (callable instanceof TransmissibleCallable) {
            this.callable = ((TransmissibleCallable<V>) callable).getCallable();
        } else {
            this.callable = callable;
        }
        this.cache = cache;
        this.parent = Thread.currentThread().getName();
    }

    @Override
    public V call() throws Exception {
        TransmissibleThreadLocal.inject(cache);
        return callable.call();
    }

    public String getParent() {
        return this.parent;
    }

    public Callable<V> getCallable() {
        return this.callable;
    }
}
