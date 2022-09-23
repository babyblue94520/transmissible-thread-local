package pers.clare.transmissible;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NameableThreadFactory implements ThreadFactory {
    private final String threadNamePrefix;

    private final AtomicInteger threadCount = new AtomicInteger();

    public NameableThreadFactory(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        return new Thread(runnable, getNextName());
    }

    private String getNextName() {
        return threadNamePrefix + threadCount.incrementAndGet();
    }

}
