package com.road.sentin.core.controller;


import com.road.sentin.core.node.Node;

public interface TrafficShapingController {
    boolean canPass(Node node, int acquireCount, boolean prioritized);
    boolean canPass(Node node, int acquireCount);
}
