package pers.clare.transmissible;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CacheThreadPoolTest extends AbstractExecutorTest<ExecutorService>{

    ExecutorService getExecutorService() {
        return TransmissibleExecutors.newCachedThreadPool();
    }

    ExecutorService getExecutorService(int thread) {
        return TransmissibleExecutors.newCachedThreadPool();
    }
}
