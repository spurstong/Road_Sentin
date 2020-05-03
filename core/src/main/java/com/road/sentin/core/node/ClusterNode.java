package com.road.sentin.core.node;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class ClusterNode extends StatisticNode {
    private final String name;

    public ClusterNode(String name) {
        this.name = name;
    }

    private Map<String, StatisticNode> originCountMap = new HashMap<>();
    private final ReentrantLock lock = new ReentrantLock();
    public String getName() {
        return name;
    }
    public Node getOrCreateOriginNode(String origin) {
        StatisticNode statisticNode = originCountMap.get(origin);
        if (statisticNode == null) {
            try {
                lock.lock();
                statisticNode = originCountMap.get(origin);
                if (statisticNode == null) {
                    statisticNode = new StatisticNode();
                    HashMap<String, StatisticNode> newMap = new HashMap<>(originCountMap.size()+1);
                    newMap.putAll(originCountMap);
                    newMap.put(origin, statisticNode);
                    originCountMap = newMap;
                }
            } finally {
                lock.unlock();
            }
        }
        return statisticNode;
    }

    public Map<String, StatisticNode> getOriginCountMap() {
        return originCountMap;
    }
}
