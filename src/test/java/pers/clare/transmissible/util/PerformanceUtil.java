package pers.clare.transmissible.util;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;

public class PerformanceUtil {

    public static double byCount(long max, Runnable runnable) throws ExecutionException, InterruptedException {
        return byCount(Runtime.getRuntime().availableProcessors(), max, runnable);
    }

    public static double byCount(int thread, long max, Runnable runnable) throws ExecutionException, InterruptedException {
        return byCondition(thread, (count) -> count <= max, runnable);
    }

    public static double byTime(long ms, Runnable runnable) throws ExecutionException, InterruptedException {
        return byTime(Runtime.getRuntime().availableProcessors(), ms, runnable);
    }

    public static double byTime(int thread, long ms, Runnable runnable) throws ExecutionException, InterruptedException {
        long endTime = System.currentTimeMillis() + ms;
        return byCondition(thread, (count) -> System.currentTimeMillis() < endTime, runnable);
    }

    public static double byCondition(Function<Long, Boolean> condition, Runnable runnable) throws ExecutionException, InterruptedException {
        return byCondition(Runtime.getRuntime().availableProcessors(), condition, runnable);
    }

    public static double byCondition(int thread, Function<Long, Boolean> condition, Runnable runnable) throws ExecutionException, InterruptedException {
        String format = "concurrency: %d, count: %d, took time(ms): %d, %d/s, time(ms):  %f";
        long startTime = System.currentTimeMillis();
        AtomicLong counter = new AtomicLong();

        Runnable shutdown = performance(thread, () -> {
            long currentTime;
            long printTime = 0;
            while (condition.apply(counter.incrementAndGet())) {
                currentTime = System.currentTimeMillis();
                if (currentTime > printTime) {
                    printTime = currentTime + 1000;
                    long time = System.currentTimeMillis() - startTime;
                    long c = counter.get();
                    System.out.printf(format + "\r", thread, c, time, rps(c, time), per(c, time));
                }
                runnable.run();
            }
            counter.decrementAndGet();
            return null;
        });
        shutdown.run();

        long time = System.currentTimeMillis() - startTime;
        long c = counter.get();
        double per = per(c, time);
        System.out.printf(format + "\n", thread, c, time, rps(c, time), per);
        return per;
    }

    public static double asyncByCount(long max, Consumer<Runnable> runnable) throws ExecutionException, InterruptedException {
        return asyncByCount(Runtime.getRuntime().availableProcessors(), max, runnable);
    }

    public static double asyncByCount(int thread, long max, Consumer<Runnable> runnable) throws ExecutionException, InterruptedException {
        return asyncByCondition(thread, (count) -> count <= max, runnable);
    }

    public static double asyncByTime(long ms, Consumer<Runnable> runnable) throws ExecutionException, InterruptedException {
        return asyncByTime(Runtime.getRuntime().availableProcessors(), ms, runnable);
    }

    public static double asyncByTime(int thread, long ms, Consumer<Runnable> runnable) throws ExecutionException, InterruptedException {
        long endTime = System.currentTimeMillis() + ms;
        return asyncByCondition(thread, (count) -> System.currentTimeMillis() < endTime, runnable);
    }

    public static double asyncByCondition(Function<Long, Boolean> condition, Consumer<Runnable> runnable) throws ExecutionException, InterruptedException {
        return asyncByCondition(Runtime.getRuntime().availableProcessors(), condition, runnable);
    }

    public static double asyncByCondition(int thread, Function<Long, Boolean> condition, Consumer<Runnable> runnable) throws ExecutionException, InterruptedException {
        String format = "concurrency: %d, count: %d, took time(ms): %d, %d/s, time(ms):  %f";
        long startTime = System.currentTimeMillis();
        AtomicLong counter = new AtomicLong();
        AtomicLong taskCount = new AtomicLong();

        Runnable shutdown = performance(thread, () -> {
            long currentTime;
            long printTime = 0;
            while (condition.apply(counter.incrementAndGet())) {
                currentTime = System.currentTimeMillis();
                if (currentTime > printTime) {
                    printTime = currentTime + 1000;
                    long time = System.currentTimeMillis() - startTime;
                    long c = counter.get();
                    System.out.printf(format + "\r", thread, c, time, rps(c, time), per(c, time));
                }
                taskCount.incrementAndGet();
                try {
                    runnable.accept(taskCount::decrementAndGet);
                } catch (Exception e) {
                    e.printStackTrace();
                    taskCount.decrementAndGet();
                }
            }
            counter.decrementAndGet();
            return null;
        });
        while (taskCount.get() > 0) {
        }

        shutdown.run();

        long time = System.currentTimeMillis() - startTime;
        long c = counter.get();
        double per = per(c, time);
        System.out.printf(format + "\n", thread, c, time, rps(c, time), per);
        return per;
    }

    public static Runnable performance(int thread, Callable<Void> callable) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(thread);
        List<Callable<Void>> tasks = new ArrayList<>();
        for (int i = 0; i < thread; i++) {
            tasks.add(callable);
        }
        for (Future<Void> future : executor.invokeAll(tasks)) {
            future.get();
        }
        return executor::shutdown;
    }

    private static long rps(long count, long ms) {
        ms = ms == 0 ? 1 : ms;
        return count * 1000 / ms;
    }


    private static double per(long count, long ms) {
        if (count == 0) return 0;
        return (double) ms * 1000 / count;
    }
}
