package pers.clare.transmissible;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public abstract class AbstractTransmissibleExecutorService<Service extends ExecutorService> implements ExecutorService {
    protected final Service executor;

    public AbstractTransmissibleExecutorService(Service executor) {
        this.executor = executor;
    }

    @Override
    public void shutdown() {
        executor.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return executor.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return executor.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return executor.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return executor.awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return executor.submit(TransmissibleCallable.of(task));
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return executor.submit(TransmissibleRunnable.of(task), result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return executor.submit(TransmissibleRunnable.of(task));
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return executor.invokeAll(TransmissibleCallable.of(tasks));
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return executor.invokeAll(TransmissibleCallable.of(tasks), timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return executor.invokeAny(TransmissibleCallable.of(tasks));
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return executor.invokeAny(TransmissibleCallable.of(tasks), timeout, unit);
    }

    @Override
    public void execute(Runnable command) {
        executor.execute(TransmissibleRunnable.of(command));
    }
}
