package com.road.sentin.core.phy;



import com.road.sentin.core.chain.ProcessorSlot;
import com.road.sentin.core.chain.ProcessorSlotChain;
import com.road.sentin.core.chain.SlotChainProvider;
import com.road.sentin.core.context.Constants;
import com.road.sentin.core.context.Context;
import com.road.sentin.core.context.ContextUtil;
import com.road.sentin.core.context.NullContext;
import com.road.sentin.core.entry.CtEntry;
import com.road.sentin.core.entry.Entry;
import com.road.sentin.core.entry.EntryType;
import com.road.sentin.core.except.BlockException;
import com.road.sentin.core.resource.ResourceWrapper;
import com.road.sentin.core.resource.StringResourceWrapper;

import java.util.HashMap;
import java.util.Map;

public class CtSph implements Sph {
    private static final Object[] OBJECTS0 = new Object[0];
    private static volatile Map<ResourceWrapper, ProcessorSlotChain> chainMap = new HashMap<>();

    private static final Object LOCK = new Object();
    @Override
    public Entry entry(String name) throws BlockException {
        StringResourceWrapper resource = new StringResourceWrapper(name, EntryType.OUT);

        return entry(resource, 1, OBJECTS0);
    }

    @Override
    public Entry entry(String name, int count) throws BlockException {
        return null;
    }

    @Override
    public Entry entry(String name, EntryType type) throws BlockException {
        StringResourceWrapper resource = new StringResourceWrapper(name, type);
        return entry(resource, 1, OBJECTS0);
    }

    @Override
    public Entry entry(String name, EntryType type, int count) throws BlockException {
        StringResourceWrapper resource = new StringResourceWrapper(name, type);
        return entry(resource, count, OBJECTS0);
    }

    @Override
    public Entry entry(String name, EntryType type, int count, Object... args) throws BlockException {
        StringResourceWrapper resource = new StringResourceWrapper(name, type);
        return entry(resource, count, args);
    }

    private Entry entryWithPriority(ResourceWrapper resourceWrapper, int count, boolean prioritized, Object... args) throws BlockException{
        Context context = ContextUtil.getContext();
        if (context instanceof NullContext) {
            return new CtEntry(resourceWrapper, null, context);
        }
        if (context == null) {
            context  = InternalContextUtil.internalEnter(Constants.CONTEXT_DEFAULT_NAME);
        }
        if (!Constants.ON) {
            return new CtEntry(resourceWrapper, null, context);
        }
        ProcessorSlot<Object> chain = lookProcessChain(resourceWrapper);
        if (chain == null) {
            return new CtEntry(resourceWrapper, null, context);
        }

        Entry e = new CtEntry(resourceWrapper, chain, context);
        try {
            chain.entry(context, resourceWrapper, null, count, prioritized, args);

        } catch (BlockException e1) {
            e.exit(count, args);
            throw e1;
        } catch (Throwable e1) {
            System.out.println("Sentinel unexpected exception:" + e1);
        }
        return e;
    }


    private final static class InternalContextUtil extends ContextUtil {
        static Context internalEnter(String name) {
            return trueEnter(name, "");
        }
        static Context internalEnter(String name, String origin) {
            return trueEnter(name, origin);
        }
    }

    ProcessorSlot<Object> lookProcessChain(ResourceWrapper resourceWrapper) {
        ProcessorSlotChain chain = chainMap.get(resourceWrapper);
        if (chain == null) {
            synchronized (LOCK) {
                chain = chainMap.get(resourceWrapper);
                if (chain == null) {
                    if (chainMap.size() >= Constants.MAX_SLOT_CHAIN_SIZE) {
                        return null;
                    }
                    chain = SlotChainProvider.newSlotChain();
                    Map<ResourceWrapper, ProcessorSlotChain> newMap = new HashMap<>(chainMap.size() + 1);
                    newMap.putAll(chainMap);
                    newMap.put(resourceWrapper, chain);
                    chainMap = newMap;
                }
            }
        }
        return chain;
    }


    @Override
    public Entry entryWithPriority(String name, EntryType type, int count, boolean prioritized) throws BlockException {
        StringResourceWrapper resource = new StringResourceWrapper(name, type);
        return entryWithPriority(resource, count, prioritized);
    }

    static Map<ResourceWrapper, ProcessorSlotChain> getChainMap() {
        return chainMap;
    }

    @Override
    public Entry entryWithPriority(String name, EntryType type, int count, boolean prioritized, Object... args) throws BlockException {
        StringResourceWrapper resource = new StringResourceWrapper(name, type);

        return entryWithPriority(resource, count, prioritized, args);
    }

    @Override
    public Entry entryWithType(String name, EntryType entryType, int count, Object[] args) throws BlockException {
        StringResourceWrapper resource = new StringResourceWrapper(name, entryType);
        return entryWithPriority(resource, count, false, args);
    }

    @Override
    public Entry entryWithType(String name,  EntryType entryType, int count, boolean prioritized, Object[] args) throws BlockException {
        StringResourceWrapper resource = new StringResourceWrapper(name, entryType);
        return entryWithPriority(resource, count, prioritized, args);
    }

    public Entry entry(ResourceWrapper resourceWrapper, int count, Object... args) throws BlockException {
        return entryWithPriority(resourceWrapper, count, false, args);
    }
}
