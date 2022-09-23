package pers.clare.transmissible;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TransmissibleThreadPoolExecutorTest extends AbstractExecutorTest<ExecutorService> {

    ExecutorService getExecutorService() {
        return new TransmissibleThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>());
    }

    ExecutorService getExecutorService(int thread) {
        return new TransmissibleThreadPoolExecutor(thread, thread, 0, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>());
    }
}
