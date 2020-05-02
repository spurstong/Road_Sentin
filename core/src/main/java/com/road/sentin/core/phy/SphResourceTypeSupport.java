package com.road.sentin.core.phy;


import com.road.sentin.core.entry.Entry;
import com.road.sentin.core.entry.EntryType;
import com.road.sentin.core.except.BlockException;

public interface SphResourceTypeSupport {

    Entry entryWithType(String name, EntryType entryType, int count, Object[] args)
            throws BlockException;


    Entry entryWithType(String name,  EntryType entryType, int count, boolean prioritized,
                        Object[] args) throws BlockException;
}
