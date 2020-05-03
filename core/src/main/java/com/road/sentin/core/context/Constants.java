package com.road.sentin.core.context;

import com.road.sentin.core.entry.EntryType;
import com.road.sentin.core.node.ClusterNode;
import com.road.sentin.core.node.DefaultNode;
import com.road.sentin.core.node.EntranceNode;
import com.road.sentin.core.resource.StringResourceWrapper;

public final class Constants {
    public final static int MAX_CONTEXT_NAME_SIZE = 2000;
    public final static int MAX_SLOT_CHAIN_SIZE = 6000;

    public final static String ROOT_ID = "machine-root";
    public final static String CONTEXT_DEFAULT_NAME = "sentinel_default_context";

    public final static DefaultNode ROOT = new EntranceNode(new StringResourceWrapper(ROOT_ID, EntryType.IN), new ClusterNode(ROOT_ID));

    public static volatile boolean ON = true;

    private Constants() {

    }
}
