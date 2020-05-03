package com.road.sentin.core.chain;

import com.road.sentin.core.context.Context;
import com.road.sentin.core.resource.ResourceWrapper;

public interface ProcessorSlot<T> {
    void entry(Context context, ResourceWrapper resourceWrapper, T param, int count, boolean prioritized, Object... args) throws Throwable;
    void fireEntry(Context context, ResourceWrapper resourceWrapper, Object obj, int count, boolean prioritized, Object... args) throws Throwable;
    void exit(Context context, ResourceWrapper resourceWrapper, int count, Object... args);
    void fireExit(Context context, ResourceWrapper resourceWrapper, int count, Object... args);
}

