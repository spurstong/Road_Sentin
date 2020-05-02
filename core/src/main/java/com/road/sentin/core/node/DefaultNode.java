package com.road.sentin.core.node;

import com.road.sentin.core.resource.ResourceWrapper;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.util.HashSet;
import java.util.Set;

public class DefaultNode extends StatisticNode {
    private ResourceWrapper id;
    private volatile Set<Node> childList = new HashSet<>();
    private ClusterNode clusterNode;
    public DefaultNode(ResourceWrapper id, ClusterNode clusterNode) {
        this.id = id;
        this.clusterNode = clusterNode;
    }

    public ResourceWrapper getId() {
        return id;
    }

    public ClusterNode getClusterNode(){
        return clusterNode;
    }

    public void setClusterNode(ClusterNode clusterNode) {
        this.clusterNode = clusterNode;
    }
    public void addChild(Node node) {
        if (node == null){
            return;
        }
        if (!childList.contains(node)) {
            synchronized (this) {
                Set<Node> newSet = new HashSet<>(childList.size() + 1);
                newSet.addAll(childList);
                newSet.add(node);
                childList = newSet;
            }
        }
    }

    public void removeChildList() {
        this.childList = new HashSet<>();
    }

    public Set<Node> getChildList() {
        return childList;
    }

    @Override
    public void increaseBlockQps(int count) {
        super.increaseBlockQps(count);
        this.clusterNode.increaseBlockQps(count);
    }

    @Override
    public void increaseExceptionQps(int count) {
        super.increaseExceptionQps(count);
        this.clusterNode.increaseBlockQps(count);
    }

    @Override
    public void addRtAndSuccess(long rt, int success) {
        super.addRtAndSuccess(rt, success);
        this.clusterNode.addRtAndSuccess(rt, success);
    }

    @Override
    public void increseThreadNum() {
        super.increseThreadNum();
        this.clusterNode.increseThreadNum();
    }

    @Override
    public void decreaseThreadNum() {
        super.decreaseThreadNum();
        this.clusterNode.decreaseThreadNum();
    }

    @Override
    public void addPassRequest(int count) {
        super.addPassRequest(count);
        this.clusterNode.addPassRequest(count);
    }

}
