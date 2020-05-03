package com.road.sentin.core.chain;


import com.road.sentin.core.context.Context;
import com.road.sentin.core.node.DefaultNode;
import com.road.sentin.core.resource.ResourceWrapper;

import java.util.HashMap;
import java.util.Map;

// 每个资源Resource共享一个processorSlot,在全部的上下文中
public class NodeSelectorSlot extends AbstracrLinkedProcessorSlot<Object> {
    // key是上下文名，
    private volatile Map<String, DefaultNode> map = new HashMap<>();

    @Override
    public void entry(Context context, ResourceWrapper resourceWrapper, Object param, int count, boolean prioritized, Object... args) throws Throwable {
        DefaultNode node = map.get(context.getName());
        if (node == null) {
            synchronized (this) {
                node = map.get(context.getName());
                if (node == null) {

                    node = new DefaultNode(resourceWrapper, null);
                    HashMap<String, DefaultNode> cacheMap = new HashMap<>(map.size()+1);
                    cacheMap.putAll(map);
                    cacheMap.put(context.getName(), node);
                    map = cacheMap;
                    ((DefaultNode)context.getLastNode()).addChild(node);
                }
            }
        }
        context.setCurNode(node);
        fireEntry(context, resourceWrapper, node, count, prioritized, args);
    }

    @Override
    public void exit(Context context, ResourceWrapper resourceWrapper, int count, Object... args) {
        fireExit(context, resourceWrapper, count, args);
    }
}
