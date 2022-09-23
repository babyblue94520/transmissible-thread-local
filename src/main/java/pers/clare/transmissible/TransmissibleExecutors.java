package pers.clare.transmissible;

import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.concurrent.*;

@SuppressWarnings("unused")
public class TransmissibleExecutors {

    /**
     * {@link Executors#newFixedThreadPool(int nThreads)}
     */
    public static ExecutorService newFixedThreadPool(int nThreads) {
        return proxy(Executors.newFixedThreadPool(nThreads));
    }

    /**
     * {@link Executors#newWorkStealingPool(int parallelism)}
     */
    public static ExecutorService newWorkStealingPool(int parallelism) {
        return proxy(Executors.newWorkStealingPool(parallelism));
    }

    /**
     * {@link Executors#newWorkStealingPool()}
     */
    public static ExecutorService newWorkStealingPool() {
        return proxy(Executors.newWorkStealingPool());
    }

    /**
     * {@link Executors#newFixedThreadPool(int nThreads, ThreadFactory threadFactory)}
     */
    public static ExecutorService newFixedThreadPool(int nThreads, ThreadFactory threadFactory) {
        return proxy(Executors.newFixedThreadPool(nThreads, threadFactory));
    }

    /**
     * {@link Executors#newSingleThreadExecutor()}
     */
    public static ExecutorService newSingleThreadExecutor() {
        return proxy(Executors.newSingleThreadExecutor());
    }

    /**
     * {@link Executors#newSingleThreadExecutor(ThreadFactory threadFactory)}
     */
    public static ExecutorService newSingleThreadExecutor(ThreadFactory threadFactory) {
        return proxy(Executors.newSingleThreadExecutor(threadFactory));
    }

    /**
     * {@link Executors#newCachedThreadPool()}
     */
    public static ExecutorService newCachedThreadPool() {
        return proxy(Executors.newCachedThreadPool());
    }

    /**
     * {@link Executors#newCachedThreadPool(ThreadFactory threadFactory)}
     */
    public static ExecutorService newCachedThreadPool(ThreadFactory threadFactory) {
        return proxy(Executors.newCachedThreadPool(threadFactory));
    }

    /**
     * {@link Executors#newSingleThreadScheduledExecutor()}
     */
    public static ScheduledExecutorService newSingleThreadScheduledExecutor() {
        return proxy(Executors.newSingleThreadScheduledExecutor());
    }

    /**
     * {@link Executors#newSingleThreadScheduledExecutor(ThreadFactory threadFactory)}
     */
    public static ScheduledExecutorService newSingleThreadScheduledExecutor(ThreadFactory threadFactory) {
        return proxy(Executors.newSingleThreadScheduledExecutor(threadFactory));
    }

    /**
     * {@link Executors#newScheduledThreadPool(int corePoolSize)}
     */
    public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize) {
        return proxy(Executors.newScheduledThreadPool(corePoolSize));
    }

    /**
     * {@link Executors#newScheduledThreadPool(int corePoolSize, ThreadFactory threadFactory)}
     */
    public static ScheduledExecutorService newScheduledThreadPool(
            int corePoolSize, ThreadFactory threadFactory) {
        return proxy(Executors.newScheduledThreadPool(corePoolSize,threadFactory));
    }

    /**
     * {@link Executors#unconfigurableExecutorService(ExecutorService executor)}
     */
    public static ExecutorService unconfigurableExecutorService(ExecutorService executor) {
        return proxy(Executors.unconfigurableExecutorService(executor));
    }

    /**
     * {@link Executors#unconfigurableScheduledExecutorService(ScheduledExecutorService executor)}
     */
    public static ScheduledExecutorService unconfigurableScheduledExecutorService(ScheduledExecutorService executor) {
        return proxy(Executors.unconfigurableScheduledExecutorService(executor));
    }

    /**
     * {@link Executors#defaultThreadFactory()}
     */
    public static ThreadFactory defaultThreadFactory() {
        return Executors.defaultThreadFactory();
    }

    /**
     * {@link Executors#privilegedThreadFactory()}
     */
    public static ThreadFactory privilegedThreadFactory() {
        return Executors.privilegedThreadFactory();
    }

    /**
     * {@link Executors#callable(Runnable task, T result)}
     */
    public static <T> Callable<T> callable(Runnable task, T result) {
        return Executors.callable(task, result);
    }

    /**
     * {@link Executors#callable(Runnable task)}
     */
    public static Callable<Object> callable(Runnable task) {
        return Executors.callable(task);
    }

    /**
     * {@link Executors#callable(PrivilegedAction action)}
     */
    public static Callable<Object> callable(final PrivilegedAction<?> action) {
        return Executors.callable(action);
    }

    /**
     * {@link Executors#callable(PrivilegedExceptionAction action)}
     */
    public static Callable<Object> callable(final PrivilegedExceptionAction<?> action) {
        return Executors.callable(action);
    }

    /**
     * {@link Executors#privilegedCallable(Callable callable)}
     */
    public static <T> Callable<T> privilegedCallable(Callable<T> callable) {
        return Executors.privilegedCallable(callable);
    }

    /**
     * {@link Executors#privilegedCallableUsingCurrentClassLoader(Callable callable)}
     */
    public static <T> Callable<T> privilegedCallableUsingCurrentClassLoader(Callable<T> callable) {
        return Executors.privilegedCallableUsingCurrentClassLoader(callable);
    }


    private static ExecutorService proxy(ExecutorService executorService) {
        if (executorService instanceof TransmissibleExecutorService) {
            return executorService;
        }
        return new TransmissibleExecutorService(executorService);
    }

    private static ScheduledExecutorService proxy(ScheduledExecutorService executorService) {
        if (executorService instanceof TransmissibleScheduledExecutorService) {
            return executorService;
        }
        return new TransmissibleScheduledExecutorService(executorService);
    }

    /**
     * Cannot instantiate.
     */
    private TransmissibleExecutors() {
    }
}
