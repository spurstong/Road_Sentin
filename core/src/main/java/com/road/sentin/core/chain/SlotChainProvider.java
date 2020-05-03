package com.road.sentin.core.chain;

public final class SlotChainProvider {
    private static volatile SlotChainBuilder builder = null;
    public static ProcessorSlotChain newSlotChain() {
        if (builder != null) {
            return builder.build();
        }
        builder = new DefaultSlotChainBuilder();
        return builder.build();

    }

    private SlotChainProvider() {

    }
}
