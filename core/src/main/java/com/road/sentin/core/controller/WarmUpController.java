package com.road.sentin.core.controller;



import com.road.sentin.core.data.TimeUtil;
import com.road.sentin.core.node.Node;

import java.util.concurrent.atomic.AtomicLong;

public class WarmUpController implements TrafficShapingController {
    protected double count;
    private int coldFactor;
    protected int warningToken = 0;
    private int maxToken;
    protected double slope;

    protected AtomicLong storedTokens = new AtomicLong(0);
    protected AtomicLong lastFileedTime = new AtomicLong(0);

    public WarmUpController(double count, int warmUpPeriodInSec, int coldFactor) {
        construct(count, warmUpPeriodInSec, coldFactor);
    }

    private void construct(double count, int warmUpPeriodInSec, int coldFactor) {
        if (coldFactor <= 1) {
            throw new IllegalArgumentException("coldFactor应该大于1");
        }
        this.count = count;
        this.coldFactor = coldFactor;
        warningToken = (int)(warmUpPeriodInSec * count) / (coldFactor - 1);
        maxToken = warningToken + (int)(2 * warmUpPeriodInSec * count / (1.0 + coldFactor));
        slope = (coldFactor - 1.0) / count / (maxToken - warningToken);
    }

    @Override
    public boolean canPass(Node node, int acquireCount, boolean prioritized) {
        long passQps = (long)node.passQps();

        long previousQps = (long)node.previousPassQps();
        syncToken(previousQps);
        long restToken = storedTokens.get();
        if (restToken >= warningToken) {
            long aboveToken = restToken - warningToken;
            double warningQps = Math.nextUp(1.0 / (aboveToken * slope + 1.0 / count));
            if (passQps + acquireCount <= warningQps) {
                return true;
            }
        } else {
            if (passQps + acquireCount <= count) {
                return  true;
            }
        }
        return false;

    }

    protected void syncToken(long passQps) {
        long currentTime = TimeUtil.currentTimeMills();
        currentTime = currentTime - currentTime % 1000;
        long oldLastFillTime = lastFileedTime.get();
        if (currentTime <= oldLastFillTime) {
            return;
        }
        long oldValue = storedTokens.get();
        long newValue = coolDownTokens(currentTime, passQps);
        if (storedTokens.compareAndSet(oldValue, newValue)) {
            long currentValue = storedTokens.addAndGet(0-passQps);
            if (currentTime < 0) {
                storedTokens.set(0L);
            }
            lastFileedTime.set(currentTime);
        }
    }

    private long coolDownTokens(long currentTime, long passQps) {
        long oldValue = storedTokens.get();
        long newValue = oldValue;
        if (oldValue < warningToken) {
            newValue = (long)(oldValue + (currentTime - lastFileedTime.get()) * count / 1000);
        } else if (oldValue > warningToken) {
            if (passQps < (int)count / coldFactor) {
                newValue = (long)(oldValue + (currentTime- lastFileedTime.get()) * count / 1000);
            }
        }
        return Math.min(newValue, maxToken);
    }

    @Override
    public boolean canPass(Node node, int acquireCount) {
        return canPass(node, acquireCount, false);
    }
}
