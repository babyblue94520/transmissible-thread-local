package pers.clare.transmissible;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TransmissibleScheduledExecutorService extends AbstractTransmissibleExecutorService<ScheduledExecutorService> implements ScheduledExecutorService {

    public TransmissibleScheduledExecutorService(ScheduledExecutorService executor) {
        super(executor);
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return executor.schedule(TransmissibleRunnable.of(command), delay, unit);
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        return executor.schedule(TransmissibleCallable.of(callable), delay, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return executor.scheduleAtFixedRate(TransmissibleRunnable.of(command), initialDelay, period, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return executor.scheduleWithFixedDelay(TransmissibleRunnable.of(command), initialDelay, delay, unit);
    }
}
