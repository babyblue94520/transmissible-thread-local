package pers.clare.transmissible;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.TtlRunnable;
import org.junit.jupiter.api.*;
import pers.clare.transmissible.util.PerformanceUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;


@TestInstance(PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TransmissibleThreadLocalTest {

    @Test
    @Order(1)
    void submit() throws ExecutionException, InterruptedException {
        String name = Thread.currentThread().getName();
        TransmissibleThreadLocal<String> threadLocal = new TransmissibleThreadLocal<>();
        threadLocal.set(name);
        AtomicLong failCount = new AtomicLong();
        Executors.newSingleThreadExecutor().submit(TransmissibleRunnable.of(() -> {
            if (!Objects.equals(name, threadLocal.get())) {
                failCount.incrementAndGet();
            }
        })).get();
        assertEquals(0, failCount.get());
    }

    @Test
    @Order(1)
    void recursive() throws InterruptedException, ExecutionException {
        int max = 10;
        AtomicLong failCount = new AtomicLong();
        ExecutorService pool = Executors.newFixedThreadPool(5, new NameableThreadFactory("pool-"));
        ExecutorService executor = Executors.newFixedThreadPool(5, new NameableThreadFactory("executor-"));
        List<Callable<Void>> tasks = new ArrayList<>();
        for (int i = 0; i < max; i++) {
            tasks.add(() -> {
                AtomicLong count = new AtomicLong();
                TransmissibleThreadLocal<Integer> threadLocal = new TransmissibleThreadLocal<>();
                threadLocal.set(0);
                loopRunnable(executor, threadLocal, failCount, count, max, 0);
                while (count.get() <= max) ;
                return null;
            });
        }
        for (Future<Void> voidFuture : pool.invokeAll(tasks)) {
            voidFuture.get();
        }

        executor.shutdown();
        assertEquals(0, failCount.get());
    }

    void loopRunnable(
            ExecutorService executor
            , TransmissibleThreadLocal<Integer> threadLocal
            , AtomicLong failCount
            , AtomicLong count
            , int max
            , int level
    ) {
        count.incrementAndGet();
        if (level > max) return;
        if (!Objects.equals(level, threadLocal.get())) {
            failCount.incrementAndGet();
        }
        int nextLevel = level + 1;
        System.out.printf("[%10s] %d > %d\n", Thread.currentThread().getName(), level, threadLocal.get());
        threadLocal.set(nextLevel);
        executor.submit(TransmissibleRunnable.of(() -> loopRunnable(executor, threadLocal, failCount, count, max, nextLevel)));
    }

    @Test
    @Order(99)
    void performance() throws Exception {
        double per = performance(TransmissibleThreadLocal.class, TransmissibleRunnable::of);
        double per2 = performance(TransmittableThreadLocal.class, TtlRunnable::get);
        assertTrue(per < per2);
    }

    double performance(Class<? extends ThreadLocal> clazz, Function<Runnable, Runnable> converter) throws Exception {
        int poolSize = 200;
        int executorSize = 10;
        ExecutorService pool = Executors.newFixedThreadPool(poolSize, new NameableThreadFactory("pool-"));
        ExecutorService executor = Executors.newFixedThreadPool(executorSize, new NameableThreadFactory("executor-"));
        AtomicLong failCount = new AtomicLong();
        ThreadLocal<String> threadLocal = clazz.getConstructor().newInstance();
        ThreadLocal<Integer> deepThreadLocal = clazz.getConstructor().newInstance();

        double per = PerformanceUtil.asyncByCount(1000000, (ok) -> {
            pool.execute(() -> {
                String name = Thread.currentThread().getName();
                threadLocal.set(name);
                int deep = 1;
                deepThreadLocal.set(deep);
                Runnable r = () -> {
                    if (!Objects.equals(name, threadLocal.get())) {
                        failCount.incrementAndGet();
                        System.out.printf("error [%10s size: %d] %s %s\n", Thread.currentThread().getName(), TransmissibleThreadLocal.threadLocalMap.size(), name, threadLocal.get());
                    }
                };
                executor.execute(converter.apply(() -> {
                    r.run();
                    assertEquals(deep, deepThreadLocal.get());
                    int deep2 = 2;
                    deepThreadLocal.set(deep2);
                    executor.execute(converter.apply(() -> {
                        r.run();
                        assertEquals(deep2, deepThreadLocal.get());
                        ok.run();
                    }));
                }));
            });
        });

        pool.shutdownNow();
        executor.shutdownNow();

        assertEquals(0, failCount.get());
        return per;
    }
}
