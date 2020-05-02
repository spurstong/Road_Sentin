package com.road.sentin.core.leap;


import com.road.sentin.core.data.LongAdder;
import com.road.sentin.core.metric.MetricEvent;

public class MetricBucket {
    private final LongAdder[] counters;
    public MetricBucket() {
        MetricEvent[] events = MetricEvent.values();
        this.counters = new LongAdder[events.length];

        for(MetricEvent event : events) {
            counters[event.ordinal()] = new LongAdder();
        }
    }

    public MetricBucket reset(MetricBucket bucket) {
        for(MetricEvent event : MetricEvent.values()) {
            counters[event.ordinal()].reset();
            counters[event.ordinal()].add(bucket.get(event));
        }
        return this;
    }

    public MetricBucket reset() {
        for(MetricEvent event : MetricEvent.values()) {
            counters[event.ordinal()].reset();
        }
        return this;
    }

    public long get(MetricEvent event) {
        return counters[event.ordinal()].sum();
    }

    public MetricBucket add(MetricEvent event, long n) {
        counters[event.ordinal()].add(n);
        return this;
    }

    public long pass() {
        return get(MetricEvent.PASS);
    }

    public long rt() {
        return get(MetricEvent.RT);
    }

    public long occupiedPass() {
        return get(MetricEvent.OCCUPIED_PASS);
    }

    public long block() {
        return get(MetricEvent.BLOCK);
    }

    public long exception() {
        return get(MetricEvent.EXCEPTION);
    }

    public long success() {
        return get(MetricEvent.SUCCESS);
    }

    public void addPass(int n) {
        add(MetricEvent.PASS, n);
    }

    public void addOccupiedPass(int n) {
        add(MetricEvent.OCCUPIED_PASS, n);
    }

    public void addException(int n) {
        add(MetricEvent.EXCEPTION, n);
    }

    public void addBlock(int n) {
        add(MetricEvent.BLOCK, n);
    }

    public void addSuccess(int n) {
        add(MetricEvent.SUCCESS, n);
    }

    public void addRT(long rt) {
        add(MetricEvent.RT, rt);
    }

}
