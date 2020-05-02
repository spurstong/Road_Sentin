package com.road.sentin.core.leap;



import com.road.sentin.core.metric.MetricEvent;

import java.util.List;

public class OccupiableBucketLeapArray extends LeapArray<MetricBucket>{
    private final FutureBucketLeapArray borrowArray;

    public OccupiableBucketLeapArray(int sampleCount, int intervalInMs) {
        super(sampleCount, intervalInMs);
        this.borrowArray = new FutureBucketLeapArray(sampleCount, intervalInMs);
    }

    @Override
    public MetricBucket newEmptyBucket(long timeMills) {
        MetricBucket newBucket = new MetricBucket();
        MetricBucket borrowBucket = borrowArray.getWindowValue(timeMills);
        if (borrowBucket != null){
            newBucket.reset(borrowBucket);
        }
        return newBucket;
    }

    @Override
    protected WindowWrap<MetricBucket> resetWindowTo(WindowWrap<MetricBucket> windowWrap, long startTime) {
        windowWrap.resetTo(startTime);
        MetricBucket borrowBucket = borrowArray.getWindowValue(startTime);
        if(borrowBucket != null) {
            windowWrap.value().reset();
            windowWrap.value().addPass((int)borrowBucket.pass());
        } else {
            windowWrap.value().reset();
        }
        return windowWrap;
    }

    @Override
    public long currentWaiting() {
        borrowArray.currentWindow();
        long currentWaiting = 0;
        List<MetricBucket> list = borrowArray.values();
        for(MetricBucket window : list) {
            currentWaiting += window.pass();
        }
        return currentWaiting;
    }

    @Override
    public void addWaiting(long time, int acquireCount) {
        WindowWrap<MetricBucket> window = borrowArray.currentWindow(time);
        window.value().add(MetricEvent.PASS, acquireCount);
    }
}
