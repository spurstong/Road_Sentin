package com.road.sentin.core.chain;

import com.road.sentin.core.context.Context;
import com.road.sentin.core.node.DefaultNode;
import com.road.sentin.core.resource.ResourceWrapper;
import com.road.sentin.core.rule.DegradeRuleManager;


public class DegradeSlot extends AbstracrLinkedProcessorSlot<DefaultNode> {
    @Override
    public void entry(Context context, ResourceWrapper resourceWrapper, DefaultNode node, int count, boolean prioritized, Object... args) throws Throwable {
        DegradeRuleManager.checkDegrade(resourceWrapper, context, node, count);
        fireEntry(context, resourceWrapper, node, count, prioritized, args);
    }

    @Override
    public void exit(Context context, ResourceWrapper resourceWrapper, int count, Object... args) {
        fireExit(context, resourceWrapper, count, args);
    }
}
