package com.road.sentin.core.rule;


import com.road.sentin.core.context.Context;
import com.road.sentin.core.except.BlockException;
import com.road.sentin.core.except.DegradeException;
import com.road.sentin.core.node.DefaultNode;
import com.road.sentin.core.resource.ResourceWrapper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DegradeRuleManager {
    private static final Map<String, Set<DegradeRule>> degradeRules = new ConcurrentHashMap<>();
    public static void checkDegrade(ResourceWrapper resource, Context context, DefaultNode node, int count) throws BlockException {
        Set<DegradeRule> rules = degradeRules.get(resource.getName());
        if (rules == null) {
            return;
        }
        for(DegradeRule rule : rules) {
            if (!rule.passCheck(context, node, count)) {
                throw  new DegradeException(rule.getLimitApp(), rule);

            }
        }

    }
    public static List<DegradeRule> getRules() {
        List<DegradeRule> rules = new ArrayList<>();
        for(Map.Entry<String, Set<DegradeRule>> entry : degradeRules.entrySet()) {
            rules.addAll(entry.getValue());
        }
        return rules;
    }

    public static void loadRules(List<DegradeRule> rules) {

        for(DegradeRule rule: rules) {
            String resourceName = rule.getResource();
            Set<DegradeRule> old = degradeRules.get(resourceName);
            if (old == null) {
                old = new HashSet<DegradeRule>();
            }
            old.add(rule);
            degradeRules.put(resourceName, old);
        }

    }
}
