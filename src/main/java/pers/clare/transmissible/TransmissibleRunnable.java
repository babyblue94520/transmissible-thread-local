package pers.clare.transmissible;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class TransmissibleRunnable implements Runnable {
    private final Runnable runnable;

    private final Map<TransmissibleThreadLocal<Object>, Object> cache;

    private final String parent;

    public static Runnable of(Runnable runnable) {
        Map<TransmissibleThreadLocal<Object>, Object> cache = TransmissibleThreadLocal.export();
        if (cache == null) {
            return runnable;
        }
        return new TransmissibleRunnable(runnable, cache);
    }

    public TransmissibleRunnable(Runnable runnable, Map<TransmissibleThreadLocal<Object>, Object> cache) {
        if (runnable instanceof TransmissibleRunnable) {
            this.runnable = ((TransmissibleRunnable) runnable).getRunnable();
        } else {
            this.runnable = runnable;
        }
        this.cache = cache;
        this.parent = Thread.currentThread().getName();
    }

    @Override
    public void run() {
        TransmissibleThreadLocal.inject(cache);
        runnable.run();
    }

    public String getParent() {
        return this.parent;
    }

    public Runnable getRunnable() {
        return this.runnable;
    }
}
