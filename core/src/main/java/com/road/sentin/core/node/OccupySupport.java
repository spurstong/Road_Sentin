package com.road.sentin.core.node;

public interface OccupySupport {
    long tryOccupyNext(long currentTime, long acquireCount, double threadold);
    long waiting();
    void addWaitingRequest(long futureTime, int acquireCount);
    void addOccupiedPass(int acquireCount);
}
