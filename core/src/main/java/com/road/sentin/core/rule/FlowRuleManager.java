package com.road.sentin.core.rule;

import com.road.sentin.core.controller.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FlowRuleManager {
    private static final Map<String, Set<FlowRule>> flowRules = new ConcurrentHashMap<>();
    public static boolean isOtherOrigin(String origin, String resourceName) {
        if (origin == null || origin.length() == 0) {
            return false;
        }
        Set<FlowRule> rules = flowRules.get(resourceName);
        if (rules != null) {
            for(FlowRule rule: rules) {
                if (origin.equals(rule.getLimitApp())) {
                    return false;
                }
            }
        }
        return true;
    }

    public static List<FlowRule> getRules() {
        List<FlowRule> rules = new ArrayList<FlowRule>();
        for (Map.Entry<String, Set<FlowRule>> entry : flowRules.entrySet()) {
            rules.addAll(entry.getValue());
        }
        return rules;
    }

    public static void loadRules(List<FlowRule> rules) {
        for(FlowRule rule : rules) {
            String resourceName = rule.getResource();
            Set<FlowRule> old = flowRules.get(resourceName);
            if (old == null) {
                old = new HashSet<FlowRule>();
            }
            int behavior = rule.getControlBehavior();
            TrafficShapingController controller = null;
            if (behavior == 0) {
                controller = new DefaultController(rule.getCount(), rule.getGrade());
            } else if (behavior == 1) {
                controller = new RateLimiterController(rule.getMaxQueueingTimeMs(), rule.getCount());
            } else if (behavior == 2) {
                controller = new WarmUpController(rule.getCount(), rule.getWarmUpPeriodSec(), 3);
            } else {
                controller = new WarmUpRateLimiterController(rule.getCount(), rule.getWarmUpPeriodSec(), rule.getMaxQueueingTimeMs(), 3);
            }
            rule.setRater(controller);
            old.add(rule);
            flowRules.put(resourceName, old);
        }
    }

    public static Map<String, Set<FlowRule>> getFlowRuleMap() {
        return flowRules;
    }


}
