package com.road.sentin.core.chain;

import com.road.sentin.core.context.Context;
import com.road.sentin.core.resource.ResourceWrapper;


public abstract class AbstracrLinkedProcessorSlot<T> implements ProcessorSlot<T> {
    private AbstracrLinkedProcessorSlot<?> next = null;


    @Override
    public void fireEntry(Context context, ResourceWrapper resourceWrapper, Object obj, int count, boolean prioritized, Object... args) throws Throwable {
        if (next != null) {
            next.transformEntry(context, resourceWrapper, obj, count,
                    prioritized, args);
        }
    }

    void transformEntry(Context context, ResourceWrapper resourceWrapper, Object o, int count, boolean prioritized, Object... args) throws Throwable{
        T t = (T) o;
        entry(context, resourceWrapper, t, count, prioritized, args);
    }



    @Override
    public void fireExit(Context context, ResourceWrapper resourceWrapper, int count, Object... args) {
        if (next != null) {
            next.exit(context, resourceWrapper, count, args);
        }
    }
    public AbstracrLinkedProcessorSlot<?> getNext() {
        return next;
    }
    public void setNext(AbstracrLinkedProcessorSlot<?> next) {
        this.next = next;
    }
}
