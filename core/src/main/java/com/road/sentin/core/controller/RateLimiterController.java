package com.road.sentin.core.controller;



import com.road.sentin.core.data.TimeUtil;
import com.road.sentin.core.node.Node;

import java.util.concurrent.atomic.AtomicLong;

public class RateLimiterController implements TrafficShapingController {
    private final int maxQueueingTimeMs;
    private final double count;
    private final AtomicLong latestPassedTime = new AtomicLong(-1);

    public RateLimiterController(int timeOut, double count) {
        this.maxQueueingTimeMs = timeOut;
        this.count = count;
    }


    @Override
    public boolean canPass(Node node, int acquireCount, boolean prioritized) {
        if  (acquireCount <= 0) {
            return true;
        }
        if (count <= 0){
            return false;
        }
        long currentTime = TimeUtil.currentTimeMills();
        long costTime = Math.round(1.0 * (acquireCount) / count * 1000);
        long expectedTime = costTime + latestPassedTime.get();
        if (expectedTime <= currentTime) {
            latestPassedTime.set(currentTime);
            return true;
        } else {
            long waitTime = costTime + latestPassedTime.get() - TimeUtil.currentTimeMills();
            if (waitTime > maxQueueingTimeMs) {
                return false;
            } else {
                long oldTime = latestPassedTime.addAndGet(costTime);
                try {
                    waitTime = oldTime - TimeUtil.currentTimeMills();
                    if (waitTime > maxQueueingTimeMs) {
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

    @Override
    public boolean canPass(Node node, int acquireCount) {
        return canPass(node, acquireCount, false);
    }
}
