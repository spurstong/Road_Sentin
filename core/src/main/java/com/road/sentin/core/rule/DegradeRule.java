package com.road.sentin.core.rule;


import com.road.sentin.core.chain.ClusterBuilderSlot;
import com.road.sentin.core.context.Context;
import com.road.sentin.core.node.ClusterNode;
import com.road.sentin.core.node.DefaultNode;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class DegradeRule extends AbstractRule {
    private static ScheduledExecutorService pool = Executors.newScheduledThreadPool(
            Runtime.getRuntime().availableProcessors(), new NamedThreadFactory("sentinel-degrade-reset-task", true)
    );

    public DegradeRule() {

    }

    public DegradeRule(String resourceName) {

    }
    private double count;

    private int timeWindow;

    private int grade = RuleConstant.DEGRADE_GRADE_RT;

    private int rtSlowRequestAmount = RuleConstant.DEGRADE_DEFAULT_SLOW_REQUEST_AMOUNT;

    private int minRequestAmount = RuleConstant.DEGRADE_DEFAULT_MIN_REQUEST_AMOUNT;

    public int getGrade() {
        return grade;
    }

    public DegradeRule setGrade(int grade) {
        this.grade = grade;
        return this;
    }

    public double getCount() {
        return count;
    }

    public DegradeRule setCount(double count) {
        this.count = count;
        return this;
    }

    public int getTimeWindow() {
        return timeWindow;
    }

    public DegradeRule setTimeWindow(int timeWindow) {
        this.timeWindow = timeWindow;
        return this;
    }

    public int getRtSlowRequestAmount() {
        return rtSlowRequestAmount;
    }

    public DegradeRule setRtSlowRequestAmount(int rtSlowRequestAmount) {
        this.rtSlowRequestAmount = rtSlowRequestAmount;
        return this;
    }

    public int getMinRequestAmount() {
        return minRequestAmount;
    }

    public DegradeRule setMinRequestAmount(int minRequestAmount) {
        this.minRequestAmount = minRequestAmount;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {return true;}
        if (obj == null || getClass() != obj.getClass()) {return false;}
        if (!super.equals(obj)) {return false;}
        DegradeRule that = (DegradeRule) obj;
        return Double.compare(that.count, count) == 0 &&
                timeWindow == that.timeWindow && grade == that.grade &&
                rtSlowRequestAmount == that.rtSlowRequestAmount &&
                minRequestAmount == that.minRequestAmount;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + new Double(count).hashCode();
        result  = 31 * result + timeWindow;
        result = 31 * result + grade;
        result = 31 * result + rtSlowRequestAmount;
        result = 31 * result + minRequestAmount;
        return result;
    }

    private AtomicLong passCount = new AtomicLong(0);
    private final AtomicBoolean cut = new AtomicBoolean(false);

    @Override
    public boolean passCheck(Context context, DefaultNode node, int count, Object... args) {
        if (cut.get()) {
            return false;
        }

        ClusterNode clusterNode = ClusterBuilderSlot.getClusterNode(this.getResource());
        if (clusterNode == null) {
            return true;
        }
        if (grade == RuleConstant.DEGRADE_GRADE_RT) {
            double rt = clusterNode.avgRt();
            if (rt < this.count) {
                passCount.set(0);
                return true;
            }
            if(passCount.incrementAndGet() < rtSlowRequestAmount) {
                return true;
            }
        } else if (grade == RuleConstant.DEGRADE_GRADE_EXCEPTION_RATIO) {
            double exception = clusterNode.exceptionQps();
            double success = clusterNode.successQps();
            double total = clusterNode.totalQps();
            if (total < minRequestAmount) {
                return true;
            }
            double realSuccess = success - exception;
            if (realSuccess <= 0 && exception < minRequestAmount) {
                return true;
            }
            if (exception / success < count) {
                return true;
            }
        } else if (grade == RuleConstant.DEGRADE_GRADE_EXCEPTION_COUNT) {
            double exception = clusterNode.totalException();
            if (exception < count) {
                return true;
            }
        }
        if (cut.compareAndSet(false, true)) {
            ResetTask resetTask = new ResetTask(this);
            pool.schedule(resetTask, timeWindow, TimeUnit.SECONDS);
        }
        return  false;
    }

    private static final class ResetTask implements Runnable {
        private DegradeRule rule;
        ResetTask(DegradeRule rule) {
            this.rule = rule;
        }

        @Override
        public void run() {
            rule.passCount.set(0);
            rule.cut.set(false);
        }
    }
}
