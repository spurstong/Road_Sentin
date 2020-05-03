package com.road.sentin.core.leap;

public class FutureBucketLeapArray extends LeapArray<MetricBucket> {

    public FutureBucketLeapArray(int sampleCount, int intervalInMs) {
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
        return time >= windowWrap.windowStart();
    }
}
