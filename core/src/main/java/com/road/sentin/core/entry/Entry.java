package com.road.sentin.core.entry;

import com.road.sentin.core.data.TimeUtil;
import com.road.sentin.core.except.ErrorEntryFreeException;
import com.road.sentin.core.node.Node;
import com.road.sentin.core.resource.ResourceWrapper;

public abstract class Entry implements AutoCloseable {
    private static final Object[] OBJECTS0 = new Object[0];
    private long createTime;
    private Node curNode;
    private Node originNode;
    private Throwable error;
    protected ResourceWrapper resourceWrapper;

    public Entry(ResourceWrapper resourceWrapper) {
        this.resourceWrapper = resourceWrapper;
        this.createTime = TimeUtil.currentTimeMills();
    }

    public ResourceWrapper getResourceWrapper() {
        return resourceWrapper;
    }

    public void exit() throws ErrorEntryFreeException {
        exit(1, OBJECTS0);
    }

    public void exit(int count) throws ErrorEntryFreeException {
        exit(count, OBJECTS0);
    }



    public void setError(Throwable error) {
        this.error = error;
    }

    @Override
    public void close() throws Exception {
        exit();
    }

    public abstract void exit(int count, Object... args) throws ErrorEntryFreeException;
    protected abstract Entry trueExit(int count, Object... args)throws ErrorEntryFreeException;
    public abstract Node getLastNode();
    public long getCreateTime() {
        return createTime;
    }
    public Node getCurNode() {
        return curNode;
    }
    public void setCurNode(Node node) {
        this.curNode = node;
    }

    public Throwable getError() {
        return error;
    }
    public Node getOriginNode() {
        return originNode;
    }
    public void setOriginNode(Node originNode) {
        this.originNode = originNode;
    }
}
