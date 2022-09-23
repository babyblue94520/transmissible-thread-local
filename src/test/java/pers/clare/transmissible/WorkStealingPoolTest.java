package pers.clare.transmissible;

import org.junit.jupiter.api.*;

import java.util.concurrent.*;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WorkStealingPoolTest extends AbstractExecutorTest<ExecutorService>{
    ExecutorService getExecutorService() {
        return TransmissibleExecutors.newWorkStealingPool();
    }

    ExecutorService getExecutorService(int thread) {
        return TransmissibleExecutors.newWorkStealingPool(thread);
    }
}
