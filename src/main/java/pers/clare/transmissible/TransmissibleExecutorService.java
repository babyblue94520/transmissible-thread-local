package pers.clare.transmissible;

import java.util.concurrent.ExecutorService;

public class TransmissibleExecutorService extends AbstractTransmissibleExecutorService<ExecutorService> {

    public TransmissibleExecutorService(ExecutorService executor) {
        super(executor);
    }
}
