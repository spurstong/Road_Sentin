package com.road.sentin.core.rule;


import com.road.sentin.core.context.Context;
import com.road.sentin.core.except.BlockException;
import com.road.sentin.core.except.FlowException;
import com.road.sentin.core.node.DefaultNode;
import com.road.sentin.core.node.Node;
import com.road.sentin.core.resource.ResourceWrapper;
import com.sun.deploy.security.BlockedException;

import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.function.Function;

public class FlowRuleChecker {
    public void checkFlow(Function<String, Collection<FlowRule>> ruleProvider, ResourceWrapper resource,
                          Context context, DefaultNode node, int count, boolean prioritized) throws BlockException {
        if (ruleProvider == null || resource == null) {
            return;
        }
        Collection<FlowRule> rules = ruleProvider.apply(resource.getName());
        if (rules != null) {
            for(FlowRule rule : rules) {
                if (!canPassCheck(rule, context, node, count, prioritized)) {
                    throw new FlowException(rule.getLimitApp(), rule);
                }
            }
        }
    }

    public boolean canPassCheck(FlowRule rule, Context context, DefaultNode node, int acquireCount, boolean prioritized) {
        String limitApp = rule.getLimitApp();
        if (limitApp == null) {
            return true;
        }
        return passLocalCheck(rule, context, node, acquireCount, prioritized);
    }

    private static boolean passLocalCheck(FlowRule rule, Context context, DefaultNode node, int acquireCount, boolean prioritized) {
        Node selectNode = selectNodeByRequesterAndStrategy(rule, context, node);
        if (selectNode == null) {
            return true;
        }
        return rule.getRater().canPass(selectNode, acquireCount, prioritized);
    }

    static Node selectNodeByRequesterAndStrategy(FlowRule rule, Context context, DefaultNode node) {
        String limitApp = rule.getLimitApp();
        int strategy = rule.getStrategy();
        String origin = context.getOrigin();
        if (limitApp.equals(origin) && filterOrigin(origin)) {
            if (strategy == RuleConstant.STRATEGY_DIRECT) {
                return context.getOriginNode();
            }
            return selectReferenceNode(rule, context, node);
        } else if (RuleConstant.LIMIT_APP_DEFAULT.equals(limitApp)) {
            if (strategy == RuleConstant.STRATEGY_DIRECT) {
                return node.getClusterNode();
            }
            return selectReferenceNode(rule, context, node);
        }else if (RuleConstant.LIMIT_APP_OTHER.equals(limitApp) && FlowRuleManager.isOtherOrigin(origin, rule.getResource())) {
            if (strategy == RuleConstant.STRATEGY_DIRECT) {
                return context.getOriginNode();
            }
            return selectReferenceNode(rule, context, node);
        }
        return null;
    }

    private static boolean filterOrigin(String origin) {
        return !RuleConstant.LIMIT_APP_DEFAULT.equals(origin) && !RuleConstant.LIMIT_APP_OTHER.equals(origin);
    }

    static Node selectReferenceNode(FlowRule rule, Context context, DefaultNode node) {
        String refResource = rule.getRefResource();
        int strategy = rule.getStrategy();
        if(refResource == null || refResource.length() == 0) {
            return null;
        }
        if (strategy == RuleConstant.STRATEGY_CHAIN) {
            if (!refResource.equals(context.getName())) {
                return null;
            }
            return node;
        }
        return null;
    }

}
