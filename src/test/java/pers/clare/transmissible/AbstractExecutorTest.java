package pers.clare.transmissible;

import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

public abstract class AbstractExecutorTest<T extends ExecutorService> {

    static final TransmissibleThreadLocal<String> staticThreadLocal = new TransmissibleThreadLocal<>();

    abstract ExecutorService getExecutorService();

    abstract ExecutorService getExecutorService(int thread);

    @BeforeEach
    void beforeEach() {
        staticThreadLocal.set(Thread.currentThread().getName());
    }

    @Test
    void execute() {
        String name = Thread.currentThread().getName();
        AtomicLong count = new AtomicLong();
        AtomicLong failCount = new AtomicLong();
        getExecutorService().execute(() -> {
            if (!Objects.equals(name, staticThreadLocal.get())) {
                failCount.incrementAndGet();
            }
            count.incrementAndGet();
        });
        while (count.get() == 0) {
        }
        assertEquals(0, failCount.get());
    }

    @Test
    void submit() throws ExecutionException, InterruptedException {
        String name = Thread.currentThread().getName();
        AtomicLong failCount = new AtomicLong();
        getExecutorService().submit(() -> {
            if (!Objects.equals(name, staticThreadLocal.get())) {
                failCount.incrementAndGet();
            }
        }).get();
        assertEquals(0, failCount.get());
    }

    @Test
    void submit2() throws ExecutionException, InterruptedException {
        String name = Thread.currentThread().getName();
        AtomicLong failCount = new AtomicLong();
        long time = System.currentTimeMillis();
        long result = getExecutorService().submit(() -> {
            if (!Objects.equals(name, staticThreadLocal.get())) {
                failCount.incrementAndGet();
            }
        }, time).get();

        assertEquals(0, failCount.get());
        assertEquals(time, result);
    }

    @Test
    void submit3() throws ExecutionException, InterruptedException {
        String name = Thread.currentThread().getName();
        boolean result = getExecutorService()
                .submit(() -> Objects.equals(name, staticThreadLocal.get())).get();
        assertTrue(result);
    }

    @Test
    void invokeAll() throws ExecutionException, InterruptedException {
        String name = Thread.currentThread().getName();
        AtomicLong failCount = new AtomicLong();
        int max = 5;
        Map<Integer, Callable<Integer>> taskMap = generateTasks(max, name, failCount);
        Collection<Callable<Integer>> tasks = taskMap.values();

        for (Future<Integer> future : getExecutorService(max / 2)
                .invokeAll(tasks)) {
            taskMap.remove(future.get());
        }
        assertEquals(0, taskMap.size());
        assertEquals(0, failCount.get());
    }

    @Test
    void invokeAll2() throws ExecutionException, InterruptedException {
        String name = Thread.currentThread().getName();
        AtomicLong failCount = new AtomicLong();
        int max = 5;
        Map<Integer, Callable<Integer>> taskMap = generateTasks(max, name, failCount);
        Collection<Callable<Integer>> tasks = taskMap.values();

        assertThrows(CancellationException.class, () -> {
            for (Future<Integer> future : getExecutorService(max / 2)
                    .invokeAll(tasks, 0, TimeUnit.SECONDS)) {
                taskMap.remove(future.get());
            }
        });
        for (Future<Integer> future : getExecutorService(max / 2)
                .invokeAll(tasks, 10, TimeUnit.SECONDS)) {
            taskMap.remove(future.get());
        }
        assertEquals(0, taskMap.size());
        assertEquals(0, failCount.get());

    }

    @Test
    void invokeAny() throws ExecutionException, InterruptedException {
        String name = Thread.currentThread().getName();
        AtomicLong failCount = new AtomicLong();
        int max = 5;
        Map<Integer, Callable<Integer>> taskMap = generateTasks(max, name, failCount);
        Collection<Callable<Integer>> tasks = taskMap.values();
        Set<Integer> keys = taskMap.keySet();
        Integer result = getExecutorService(max / 2)
                .invokeAny(tasks);

        assertTrue(keys.contains(result));
        assertEquals(0, failCount.get());
    }

    @Test
    void invokeAny2() throws Exception {
        String name = Thread.currentThread().getName();
        AtomicLong failCount = new AtomicLong();
        int max = 5;
        Map<Integer, Callable<Integer>> taskMap = generateTasks(max, name, failCount);
        Collection<Callable<Integer>> tasks = taskMap.values();
        Set<Integer> keys = taskMap.keySet();
        assertThrows(CancellationException.class, () -> {
            for (Future<Integer> future : getExecutorService(max / 2)
                    .invokeAll(tasks, 0, TimeUnit.SECONDS)) {
                taskMap.remove(future.get());
            }
        });

        Integer result = getExecutorService(max / 2)
                .invokeAny(tasks);

        assertTrue(keys.contains(result));
        assertEquals(0, failCount.get());
    }

    Map<Integer, Callable<Integer>> generateTasks(int max, String name, AtomicLong failCount) {
        Map<Integer, Callable<Integer>> tasks = new HashMap<>();
        for (int i = 0; i < max; i++) {
            int finalIndex = i;
            tasks.put(finalIndex, () -> {
                System.out.println(name+" "+staticThreadLocal.get());
                if (!Objects.equals(name, staticThreadLocal.get())) {
                    failCount.incrementAndGet();
                }
                return finalIndex;
            });
        }
        return tasks;
    }

    @Test
    @Order(1)
    void recursive() throws InterruptedException, ExecutionException {
        int max = 10;
        AtomicLong failCount = new AtomicLong();
        ExecutorService pool = getExecutorService(5);
        ExecutorService executor = getExecutorService(5);
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
        executor.submit(() -> loopRunnable(executor, threadLocal, failCount, count, max, nextLevel));
    }

}
