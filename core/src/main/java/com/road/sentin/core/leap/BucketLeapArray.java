package com.road.sentin.core.leap;

public class BucketLeapArray extends LeapArray<MetricBucket> {

    public BucketLeapArray(int sampleCount, int intervalInMs) {
        super(sampleCount, intervalInMs);
    }


    @Override
    public MetricBucket newEmptyBucket(long timeMills) {
        return new MetricBucket();
    }

    @Override
    protected WindowWrap<MetricBucket> resetWindowTo(WindowWrap<MetricBucket> windowWrap, long startTime) {
        windowWrap.resetTo(startTime);
        windowWrap.value().reset();
        return windowWrap;
    }

    @Override
    public boolean isWindowDeprecated(long time, WindowWrap<MetricBucket> windowWrap) {
        return super.isWindowDeprecated(time, windowWrap);
    }
}
