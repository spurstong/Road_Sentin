package com.road.sentin.core.chain;


import com.road.sentin.core.context.Context;
import com.road.sentin.core.entry.EntryType;
import com.road.sentin.core.node.ClusterNode;
import com.road.sentin.core.node.DefaultNode;
import com.road.sentin.core.node.Node;
import com.road.sentin.core.resource.ResourceWrapper;
import com.road.sentin.core.resource.StringResourceWrapper;

import java.util.HashMap;
import java.util.Map;

public class ClusterBuilderSlot extends AbstracrLinkedProcessorSlot<DefaultNode> {
    private static volatile Map<ResourceWrapper, ClusterNode> clusterNodeMap = new HashMap<>();
    private static final Object lock = new Object();
    private volatile ClusterNode clusterNode = null;
    @Override
    public void entry(Context context, ResourceWrapper resourceWrapper, DefaultNode node, int count, boolean prioritized, Object... args) throws Throwable {
        if (clusterNode == null) {
            synchronized (lock) {
                if (clusterNode == null) {
                    clusterNode = new ClusterNode(resourceWrapper.getName());
                    HashMap<ResourceWrapper, ClusterNode> newMap = new HashMap<>(Math.max(clusterNodeMap.size(), 16));
                    newMap.putAll(clusterNodeMap);
                    newMap.put(node.getId(), clusterNode);
                    clusterNodeMap = newMap;
                }
            }
        }
        node.setClusterNode(clusterNode);
        if (!"".equals(context.getOrigin())){
            Node originNode = node.getClusterNode().getOrCreateOriginNode(context.getOrigin());
            context.getCurEntry().setOriginNode(originNode);
        }
        fireEntry(context, resourceWrapper, node, count, prioritized,args);
    }

    @Override
    public void exit(Context context, ResourceWrapper resourceWrapper, int count, Object... args) {
        fireExit(context, resourceWrapper, count, args);
    }
    public static ClusterNode getClusterNode(String id, EntryType type) {
        return clusterNodeMap.get(new StringResourceWrapper(id, type));
    }

    public static ClusterNode getClusterNode(String id) {
        if (id == null) {
            return null;
        }
        ClusterNode clusterNode = null;
        for(EntryType nodeType : EntryType.values()) {
            clusterNode = clusterNodeMap.get(new StringResourceWrapper(id, nodeType));
            if (clusterNode != null) {
                break;
            }
        }
        return clusterNode;
    }

    public static Map<ResourceWrapper, ClusterNode> getClusterNodeMap() {
        return clusterNodeMap;
    }

}
