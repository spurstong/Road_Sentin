package com.road.sentin.core.chain;

import com.road.sentin.core.context.Context;
import com.road.sentin.core.except.BlockException;
import com.road.sentin.core.node.DefaultNode;
import com.road.sentin.core.resource.ResourceWrapper;
import com.road.sentin.core.rule.FlowRule;
import com.road.sentin.core.rule.FlowRuleChecker;
import com.road.sentin.core.rule.FlowRuleManager;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class FlowSlot extends AbstracrLinkedProcessorSlot<DefaultNode> {
    private final FlowRuleChecker checker;

    public FlowSlot() {
       this(new FlowRuleChecker());
    }

    FlowSlot(FlowRuleChecker checker) {
        this.checker = checker;
    }

    @Override
    public void entry(Context context, ResourceWrapper resourceWrapper, DefaultNode node, int count, boolean prioritized, Object... args) throws Throwable {
        checkFlow(resourceWrapper, context, node, count, prioritized);
        fireEntry(context, resourceWrapper, node, count, prioritized, args);
    }

    void checkFlow(ResourceWrapper resource, Context context, DefaultNode node, int count, boolean prioritized) throws BlockException {
        checker.checkFlow(ruleProvider, resource, context, node, count, prioritized);

    }

    @Override
    public void exit(Context context, ResourceWrapper resourceWrapper, int count, Object... args) {
        fireExit(context, resourceWrapper, count, args);
    }

    private final Function<String, Collection<FlowRule>> ruleProvider = new Function<String, Collection<FlowRule>>() {
        @Override
        public Collection<FlowRule> apply(String resource) {
            // Flow rule map should not be null.
            Map<String, Set<FlowRule>> flowRules = FlowRuleManager.getFlowRuleMap();
            return flowRules.get(resource);
        }
    };
}
