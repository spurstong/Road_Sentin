package com.road.sentin.core.context;

import com.road.sentin.core.entry.Entry;
import com.road.sentin.core.node.DefaultNode;
import com.road.sentin.core.node.Node;


public class Context {
    // 上下文姓名
    private final String name;
    private DefaultNode entranceNode;
    private Entry curEntry;
    private String origin = "";
    private final boolean async;

    public Context(DefaultNode entranceNode, String name) {
        this(name, entranceNode, false);
    }
    public Context(String name, DefaultNode entranceNode, boolean async) {
        this.name = name;
        this.entranceNode = entranceNode;
        this.async = async;
    }

    public String getName() {
        return name;
    }

    public Node getCurNode() {
        return curEntry == null ? null : curEntry.getCurNode();
    }

    public Context setCurNode(Node node) {
        this.curEntry.setCurNode(node);
        return this;
    }

    public Entry getCurEntry() {
        return curEntry;
    }

    public Context setCurEntry(Entry curEntry) {
        this.curEntry = curEntry;
        return this;
    }

    public String getOrigin() {
        return origin;
    }

    public Context setOrigin(String origin) {
        this.origin = origin;
        return this;
    }

    public DefaultNode getEntranceNode() {
        return entranceNode;
    }

    public Node getLastNode() {
        if (curEntry != null && curEntry.getLastNode() != null) {
            return curEntry.getLastNode();
        } else {
            return entranceNode;
        }
    }
    public Node getOriginNode() {
        return curEntry == null ? null : curEntry.getOriginNode();
    }

}
