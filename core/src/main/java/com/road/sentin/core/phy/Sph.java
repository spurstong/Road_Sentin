package com.road.sentin.core.phy;



import com.road.sentin.core.entry.Entry;
import com.road.sentin.core.entry.EntryType;
import com.road.sentin.core.except.BlockException;

import java.lang.reflect.Method;

public interface Sph extends SphResourceTypeSupport {

    Entry entry(String name) throws BlockException;


    Entry entry(String name, int count) throws BlockException;



    Entry entry(String name, EntryType type) throws BlockException;


    Entry entry(String name, EntryType type, int count) throws BlockException;


    Entry entry(String name, EntryType type, int count, Object... args) throws BlockException;


    Entry entryWithPriority(String name, EntryType type, int count, boolean prioritized) throws BlockException;


    Entry entryWithPriority(String name, EntryType type, int count, boolean prioritized, Object... args)
            throws BlockException;
}
