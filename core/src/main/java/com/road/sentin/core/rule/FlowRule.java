package com.road.sentin.core.rule;


import com.road.sentin.core.context.Context;
import com.road.sentin.core.controller.TrafficShapingController;
import com.road.sentin.core.node.DefaultNode;

public class FlowRule extends AbstractRule {
    public FlowRule() {
        super();
        setLimitApp(RuleConstant.LIMIT_APP_DEFAULT);
    }

    public FlowRule(String resourceName) {
        super();
        setResource(resourceName);
        setLimitApp(RuleConstant.LIMIT_APP_DEFAULT);
    }

    private int grade = RuleConstant.FLOW_GRADE_QPS;
    private double count;

    private int strategy = RuleConstant.STRATEGY_DIRECT;

    private String refResource;

    private int controlBehavior = RuleConstant.CONTROL_BEHAVIOR_DEFAULT;

    private int warmUpPeriodSec = 10;

    private int maxQueueingTimeMs = 500;

    private boolean clusterMode;

    private TrafficShapingController controller;

    public FlowRule setControlBehavior(int controlBehavior) {
        this.controlBehavior = controlBehavior;
        return this;
    }
    public int getControlBehavior() {
        return this.controlBehavior;
    }
    public int getMaxQueueingTimeMs() {
        return maxQueueingTimeMs;
    }
    public FlowRule setMaxQueueingTimeMs(int maxQueueingTimeMs) {
        this.maxQueueingTimeMs = maxQueueingTimeMs;
        return this;
    }


    public FlowRule setRater(TrafficShapingController rater) {
        this.controller = rater;
        return this;
    }
    public TrafficShapingController getRater() {
        return controller;
    }

    public int getWarmUpPeriodSec() {
        return warmUpPeriodSec;
    }

    public FlowRule setWarmUpPeriodSec(int warmUpPeriodSec) {
        this.warmUpPeriodSec = warmUpPeriodSec;
        return this;
    }

    public int getGrade() {
        return grade;
    }

    public FlowRule setGrade(int grade) {
        this.grade = grade;
        return this;
    }

    public FlowRule setRule(int grade) {
        this.grade = grade;
        return this;
    }

    public double getCount() {
        return count;
    }


    public FlowRule setCount(double count) {
        this.count = count;
        return this;
    }

    public int getStrategy() {
        return strategy;
    }

    public FlowRule setStrategy(int strategy) {
        this.strategy = strategy;
        return this;
    }

    public String getRefResource() {
        return refResource;
    }

    public FlowRule setRefResource() {
        this.refResource = refResource;
        return this;
    }

    @Override
    public boolean passCheck(Context context, DefaultNode node, int count, Object... args) {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {return true;}
        if (obj == null || getClass() != obj.getClass()) {return false;}
        if (!super.equals(obj)){return false;}

        FlowRule rule = (FlowRule)obj;
        if (grade != rule.grade) {return false;}
        if (Double.compare(rule.count, count) != 0) {return false;}
        if (controlBehavior != rule.controlBehavior) {return false;}
        if (warmUpPeriodSec != rule.warmUpPeriodSec) {return false;}
        if (maxQueueingTimeMs != rule.maxQueueingTimeMs){return false;}
        return (refResource != null ? refResource.equals(rule.refResource) : rule.refResource == null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        long temp;
        result = 31 * result + grade;
        temp = Double.doubleToLongBits(count);
        result = 31 * result + (int)(temp ^ (temp >>> 32));
        result = 31 * result + strategy;
        result = 31 * result + (refResource != null ? refResource.hashCode() : 0);
        result = 31 * result + controlBehavior;
        result = 31 * result + warmUpPeriodSec;
        result = 31 * result + maxQueueingTimeMs;
        return result;
    }
}
