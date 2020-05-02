package com.road.sentin.core.controller;



import com.road.sentin.core.data.TimeUtil;
import com.road.sentin.core.node.Node;

import java.util.concurrent.atomic.AtomicLong;

public class WarmUpRateLimiterController extends WarmUpController {
    private final int timeoutInMs;
    private final AtomicLong latestPassedTime = new AtomicLong(-1);
    public WarmUpRateLimiterController(double count, int warmUpperiodSec, int timeoutInMs, int coldFactor) {
        super(count, warmUpperiodSec, coldFactor);
        this.timeoutInMs = timeoutInMs;
    }

    @Override
    public boolean canPass(Node node, int acquireCount, boolean prioritized) {
        long previousQps = (long)node.previousPassQps();
        syncToken(previousQps);
        long currentTime = TimeUtil.currentTimeMills();
        long restToken = storedTokens.get();
        long costTime = 0;
        long expectedTime = 0;
        if (restToken >= warningToken) {
            long aboveToken = restToken - warningToken;
            double warningQps = Math.nextUp(1.0 / (aboveToken * slope + 1.0 / count) );
            costTime = Math.round(1.0 * (acquireCount) / warningQps * 1000);

        } else {
            costTime = Math.round(1.0 *(acquireCount) / count * 1000);
        }
        expectedTime = costTime + latestPassedTime.get();
        if (expectedTime <= currentTime) {
            latestPassedTime.set(currentTime);
            return true;
        } else {
            long waitTime = costTime + latestPassedTime.get() - currentTime;
            if (waitTime > timeoutInMs) {
                return false;
            } else {
                long oldTime = latestPassedTime.addAndGet(costTime);
                try {
                    waitTime = oldTime - TimeUtil.currentTimeMills();
                    if (waitTime > timeoutInMs) {
                        latestPassedTime.addAndGet(-costTime);
                        return false;
                    }
                    if (waitTime > 0) {
                        Thread.sleep(waitTime);
                    }
                    return true;
                } catch (InterruptedException e) {

                }
            }
        }
        return false;
    }
}
