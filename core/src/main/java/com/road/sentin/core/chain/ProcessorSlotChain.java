package com.road.sentin.core.chain;

public abstract class ProcessorSlotChain extends AbstracrLinkedProcessorSlot<Object> {
      public abstract void addFirst(AbstracrLinkedProcessorSlot<?> protocolProcessor);
      public abstract void addLast(AbstracrLinkedProcessorSlot<?> protocolProcessor);
}
