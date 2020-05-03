package com.road.sentin.core.metric;



import com.road.sentin.core.leap.*;

import javax.swing.*;
import java.util.List;

public class ArrayMetric implements Metric {
    private final LeapArray<MetricBucket> data;

    public ArrayMetric(int sampleCount, int intervalInMs) {
        this.data = new OccupiableBucketLeapArray(sampleCount, intervalInMs);
    }

    public ArrayMetric(int sampleCount, int intervalInMs, boolean enableOccupy) {
        if(enableOccupy) {
            this.data = new OccupiableBucketLeapArray(sampleCount, intervalInMs);
        } else {
            this.data = new BucketLeapArray(sampleCount, intervalInMs);
        }
    }

    public ArrayMetric(LeapArray<MetricBucket> array) {
        this.data = array;
    }

    @Override
    public long success() {
        data.currentWindow();
        long success = 0;
        List<MetricBucket> list = data.values();
        for(MetricBucket window : list) {
            success += window.success();
        }
        return success;
    }

    @Override
    public long exception() {
        data.currentWindow();
        long exception = 0;
        List<MetricBucket> list = data.values();
        for(MetricBucket window : list) {
            exception += window.exception();
        }
        return exception;
    }

    @Override
    public long block() {
        data.currentWindow();
        long block = 0;
        List<MetricBucket> list = data.values();
        for(MetricBucket window : list) {
            block += window.block();
        }
        return block;
    }

    @Override
    public long pass() {
        data.currentWindow();
        long pass = 0;
        List<MetricBucket> list = data.values();
        for(MetricBucket window : list) {
            pass += window.pass();
        }
        return pass;
    }

    @Override
    public long rt() {
        data.currentWindow();
        long rt = 0;
        List<MetricBucket> list = data.values();
        for(MetricBucket windows : list) {
            rt += windows.rt();
        }
        return rt;
    }

    @Override
    public void addException(int count) {
        WindowWrap<MetricBucket> wrap = data.currentWindow();
        wrap.value().addException(count);

    }

    @Override
    public void addBlock(int n) {
         WindowWrap<MetricBucket> wrap = data.currentWindow();
         wrap.value().addBlock(n);
    }

    @Override
    public void addSuccess(int n) {
        WindowWrap<MetricBucket> wrap = data.currentWindow();
        wrap.value().addSuccess(n);
    }

    @Override
    public void addPass(int n) {
        WindowWrap<MetricBucket> wrap = data.currentWindow();
        wrap.value().addPass(n);
    }

    @Override
    public void addRT(long rt) {
        WindowWrap<MetricBucket> wrap = data.currentWindow();
        wrap.value().addRT(rt);
    }

    @Override
    public double getWindowIntervalInsec() {
        return data.getIntervalInSecond();
    }

    @Override
    public long getWindowPass(long timeMillis) {
        MetricBucket bucket = data.getWindowValue(timeMillis);
        if (bucket == null) {
            return 0L;
        }
        return bucket.pass();
    }

    @Override
    public void addOccupiedPass(int acquireCount) {
        WindowWrap<MetricBucket> wrap = data.currentWindow();
        wrap.value().addOccupiedPass(acquireCount);

    }

    @Override
    public void addWaiting(long futureTime, int acquiereCount) {
           data.addWaiting(futureTime, acquiereCount);
    }

    @Override
    public long waiting() {
        return data.currentWaiting();
    }

    @Override
    public long occupiedPass() {
        data.currentWindow();
        long pass = 0;
        List<MetricBucket> list = data.values();
        for(MetricBucket window : list) {
            pass += window.occupiedPass();
        }
        return pass;
    }

    @Override
    public long previousWindowBlock() {
        data.currentWindow();
        WindowWrap<MetricBucket> wrap = data.getPreviousWindow();
        if(wrap == null) {
            return 0;
        }
        return wrap.value().block();
    }

    @Override
    public long perviousWindowPass() {
        data.currentWindow();
        WindowWrap<MetricBucket> wrap = data.getPreviousWindow();
        if(wrap == null) {
            return 0;
        }

        return wrap.value().pass();
    }
}
