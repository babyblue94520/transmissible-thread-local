package pers.clare.transmissible;

import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ScheduledThreadPoolTest extends AbstractExecutorTest<ScheduledExecutorService> {

    final int max = 20;
    final int thread = 3;

    ScheduledExecutorService getExecutorService() {
        return TransmissibleExecutors.newScheduledThreadPool(thread);
    }

    ScheduledExecutorService getExecutorService(int thread) {
        return TransmissibleExecutors.newScheduledThreadPool(thread);
    }

    @Test
    void schedule() {
        String name = Thread.currentThread().getName();
        AtomicLong failCount = new AtomicLong();
        AtomicLong count = new AtomicLong();
        ScheduledExecutorService executor = getExecutorService(thread);
        for (int i = 0; i < max; i++) {
            executor.schedule(() -> {
                if (!Objects.equals(name, staticThreadLocal.get())) {
                    failCount.incrementAndGet();
                }
                count.incrementAndGet();
            }, 10, TimeUnit.MILLISECONDS);
        }
        while (count.get() < max) {
        }
        assertEquals(0, failCount.get());
        executor.shutdownNow();
    }

    @Test
    void schedule2() throws ExecutionException, InterruptedException {
        String name = Thread.currentThread().getName();
        AtomicLong failCount = new AtomicLong();
        AtomicLong count = new AtomicLong();
        ScheduledExecutorService executor = getExecutorService(thread);
        Map<Integer, ScheduledFuture<Integer>> futures = new HashMap<>();
        for (int i = 0; i < max; i++) {
            int finalI = i;
            futures.put(i, executor.schedule(() -> {
                if (!Objects.equals(name, staticThreadLocal.get())) {
                    failCount.incrementAndGet();
                }
                count.incrementAndGet();
                return finalI;
            }, 10, TimeUnit.MILLISECONDS));
        }
        while (count.get() < max) {
        }
        assertEquals(0, failCount.get());
        for (Map.Entry<Integer, ScheduledFuture<Integer>> entry : futures.entrySet()) {
            assertEquals(entry.getKey(), entry.getValue().get());
        }
        executor.shutdownNow();
    }

    @Test
    void scheduleAtFixedRate() {
        String name = Thread.currentThread().getName();
        AtomicLong failCount = new AtomicLong();
        AtomicLong count = new AtomicLong();
        ScheduledExecutorService executor = getExecutorService(thread);
        for (int i = 0; i < thread * 2; i++) {
            executor.scheduleAtFixedRate(() -> {
                if (!Objects.equals(name, staticThreadLocal.get())) {
                    failCount.incrementAndGet();
                }
                count.incrementAndGet();
            }, 0, 100, TimeUnit.MILLISECONDS);
        }
        while (count.get() < max) {
        }
        assertEquals(0, failCount.get());
        executor.shutdownNow();
    }

    @Test
    void scheduleWithFixedDelay() {
        int thread = 3;
        String name = Thread.currentThread().getName();
        AtomicLong failCount = new AtomicLong();
        AtomicLong count = new AtomicLong();
        ScheduledExecutorService executor = getExecutorService(thread);
        for (int i = 0; i < thread * 2; i++) {
            executor.scheduleWithFixedDelay(() -> {
                if (!Objects.equals(name, staticThreadLocal.get())) {
                    failCount.incrementAndGet();
                }
                count.incrementAndGet();
            }, 0, 100, TimeUnit.MILLISECONDS);
        }
        while (count.get() < max) {
        }
        assertEquals(0, failCount.get());
        executor.shutdownNow();
    }
}
