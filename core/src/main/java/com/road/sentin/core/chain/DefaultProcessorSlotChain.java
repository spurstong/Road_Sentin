package com.road.sentin.core.chain;

import com.road.sentin.core.context.Context;
import com.road.sentin.core.resource.ResourceWrapper;

public class DefaultProcessorSlotChain extends ProcessorSlotChain {
    AbstracrLinkedProcessorSlot<?> first = new AbstracrLinkedProcessorSlot<Object>() {
        @Override
        public void entry(Context context, ResourceWrapper resourceWrapper, Object param, int count, boolean prioritized, Object... args) throws Throwable {
            super.fireEntry(context, resourceWrapper, param, count, prioritized, args);
        }

        @Override
        public void exit(Context context, ResourceWrapper resourceWrapper, int count, Object... args) {
            super.fireExit(context, resourceWrapper, count, args);
        }
    };

    AbstracrLinkedProcessorSlot<?> end = first;

    @Override
    public void addFirst(AbstracrLinkedProcessorSlot<?> protocolProcessor) {
        protocolProcessor.setNext(first.getNext());
        first.setNext(protocolProcessor);
        if (end == first) {
            end = protocolProcessor;
        }
    }

    @Override
    public void addLast(AbstracrLinkedProcessorSlot<?> protocolProcessor) {
        end.setNext(protocolProcessor);
        end = protocolProcessor;
    }

    @Override
    public void setNext(AbstracrLinkedProcessorSlot<?> next) {
        addLast(next);
    }

    @Override
    public AbstracrLinkedProcessorSlot<?> getNext() {
        return first.getNext();
    }

    @Override
    public void entry(Context context, ResourceWrapper resourceWrapper, Object param, int count, boolean prioritized, Object... args) throws Throwable {
        first.transformEntry(context, resourceWrapper, param, count, prioritized);
    }

    @Override
    public void exit(Context context, ResourceWrapper resourceWrapper, int count, Object... args) {
        first.exit(context, resourceWrapper, count, args);
    }
}
