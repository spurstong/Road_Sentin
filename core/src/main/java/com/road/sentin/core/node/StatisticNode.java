package com.road.sentin.core.node;


import com.road.sentin.core.data.LongAdder;
import com.road.sentin.core.metric.ArrayMetric;
import com.road.sentin.core.metric.Metric;

public class StatisticNode implements Node {
    private transient volatile Metric rollingCounterInSecond = new ArrayMetric(2, 1000);
    private transient Metric rollingCounterInMinute = new ArrayMetric(60, 60 * 1000, false);
    private LongAdder curThreadNum = new LongAdder();
    @Override
    public long totalPass() {
        return rollingCounterInMinute.pass();
    }

    @Override
    public long totalSuccess() {
        return rollingCounterInMinute.success();
    }

    @Override
    public long blockRequest() {
        return rollingCounterInMinute.block();
    }

    @Override
    public long totalException() {
        return rollingCounterInMinute.exception();
    }

    @Override
    public double passQps() {
        return rollingCounterInSecond.pass() / rollingCounterInSecond.getWindowIntervalInsec();
    }

    @Override
    public double blockQps() {
        return rollingCounterInSecond.block() / rollingCounterInSecond.getWindowIntervalInsec();
    }

    @Override
    public double totalQps() {
        return passQps() + blockQps();
    }

    @Override
    public double successQps() {
        return rollingCounterInSecond.success() / rollingCounterInSecond.getWindowIntervalInsec();
    }

    @Override
    public double exceptionQps() {
        return rollingCounterInSecond.exception() / rollingCounterInSecond.getWindowIntervalInsec();
    }

    @Override
    public double avgRt() {
        long successCount = rollingCounterInSecond.success();
        if(successCount == 0) {
            return 0;
        }
        return rollingCounterInSecond.rt() * 1.0 / successCount;
    }

    @Override
    public int curThreadNum() {
        return (int) curThreadNum.sum();
    }

    @Override
    public double previousPassQps() {
        return this.rollingCounterInMinute.previousWindowBlock();
    }

    @Override
    public void addPassRequest(int count) {
        rollingCounterInSecond.addPass(count);
        rollingCounterInMinute.addPass(count);

    }

    @Override
    public void increaseBlockQps(int count) {
        rollingCounterInSecond.addPass(count);
        rollingCounterInMinute.addPass(count);
    }

    @Override
    public void increaseExceptionQps(int count) {
        rollingCounterInSecond.addException(count);
        rollingCounterInMinute.addException(count);
    }

    @Override
    public void addRtAndSuccess(long rt, int success) {
        rollingCounterInSecond.addSuccess(success);
        rollingCounterInSecond.addRT(rt);

        rollingCounterInMinute.addSuccess(success);
        rollingCounterInMinute.addRT(rt);
    }

    @Override
    public void increseThreadNum() {
        curThreadNum.increment();
    }

    @Override
    public void decreaseThreadNum() {
        curThreadNum.decrement();
    }

    @Override
    public void reset() {
       rollingCounterInSecond = new ArrayMetric(2, 1000);
    }

    @Override
    public long tryOccupyNext(long currentTime, long acquireCount, double threshold) {
        double maxCount = threshold * IntervalProperty.INTERVAL / 1000;
        long currentBorrow = rollingCounterInSecond.waiting();
        if (currentBorrow >= maxCount) {
            // 最大抢占超时时间（单位毫秒）
            return 500;
        }
        // 秒级下一般数组设置为2个
        int windowLength = IntervalProperty.INTERVAL / 2;
        long earliestTime = currentTime - currentTime % windowLength + windowLength - IntervalProperty.INTERVAL;
        int idx = 0;

        long currentPass = rollingCounterInSecond.pass();

        while(earliestTime < currentTime) {
            long waitInMs = idx * windowLength + windowLength - currentTime % windowLength;
            if (waitInMs >= 500) {
                break;
            }
            long windowPass = rollingCounterInSecond.getWindowPass(earliestTime);
            if (currentBorrow + currentPass + acquireCount - windowPass <= maxCount) {
                return waitInMs;
            }
            earliestTime += windowLength;
            currentBorrow -= windowPass;
            idx++;
        }
        return 500;
    }

    @Override
    public long waiting() {
        return rollingCounterInSecond.waiting();
    }

    @Override
    public void addWaitingRequest(long futureTime, int acquireCount) {
        rollingCounterInSecond.addWaiting(futureTime, acquireCount);
    }

    @Override
    public void addOccupiedPass(int acquireCount) {
        rollingCounterInMinute.addOccupiedPass(acquireCount);
        rollingCounterInMinute.addPass(acquireCount);
    }
}
