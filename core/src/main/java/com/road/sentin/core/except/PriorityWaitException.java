package com.road.sentin.core.except;

public class PriorityWaitException extends RuntimeException {
    private final long waitInMs;
    public PriorityWaitException(long waitInMs) {
        this.waitInMs = waitInMs;
    }
    public long getWaitInMs() {
        return waitInMs;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
