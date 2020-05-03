package com.road.sentin.core.controller;


import com.road.sentin.core.data.TimeUtil;
import com.road.sentin.core.except.PriorityWaitException;
import com.road.sentin.core.node.Node;
import com.road.sentin.core.rule.RuleConstant;

public class DefaultController implements TrafficShapingController {
    private static final int DEFAULT_AVG_USED_TOKENS = 0;

    private double count = 0;
    private int grade;

    public DefaultController(double count, int grade) {
        this.count = count;
        this.grade = grade;
    }


    @Override
    public boolean canPass(Node node, int acquireCount, boolean prioritized) {
        int curCount = avgUsedTokens(node);
        if (curCount + acquireCount > count) {
            if (prioritized && grade == RuleConstant.FLOW_GRADE_QPS) {
                long currentTime;
                long waitInMs;
                currentTime = TimeUtil.currentTimeMills();
                waitInMs = node.tryOccupyNext(currentTime, acquireCount, count);
                if (waitInMs < 500) {
                    node.addWaitingRequest(currentTime+waitInMs, acquireCount);
                    node.addOccupiedPass(acquireCount);
                    sleep(waitInMs);
                    throw new PriorityWaitException(waitInMs);
                }
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean canPass(Node node, int acquireCount) {
        return canPass(node, acquireCount,  false);
    }

    private int avgUsedTokens(Node node) {
        if(node == null) {
            return DEFAULT_AVG_USED_TOKENS;
        }
        return grade == RuleConstant.FLOW_GRADE_THREAD ? node.curThreadNum() : (int)(node.passQps());
    }

    private void sleep(long timeMillis) {
        try{
            Thread.sleep(timeMillis);
        } catch (InterruptedException e) {

        }
    }

    @Override
    public String toString() {
        return this.count + " " + this.grade;
    }
}
