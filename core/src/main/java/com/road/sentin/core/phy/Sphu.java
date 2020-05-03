package com.road.sentin.core.phy;


import com.road.sentin.core.entry.Entry;
import com.road.sentin.core.entry.EntryType;
import com.road.sentin.core.except.BlockException;
import com.sun.deploy.security.BlockedException;

public class Sphu {
    private static final Object[] OBJECTS0 = new Object[0];
    private Sphu(){}

    public static Entry entry(String name) throws BlockException {
        return Env.sph.entry(name, EntryType.OUT, 1, OBJECTS0);
    }

    public static Entry entry(String name, int count) throws BlockException {
        return Env.sph.entry(name, EntryType.OUT, count, OBJECTS0);
    }

    public static Entry entry(String name, EntryType type) throws BlockException {
        return Env.sph.entry(name, type, 1, OBJECTS0);
    }

    public static Entry entry(String name, EntryType type, int count) throws BlockException {
        return Env.sph.entry(name, type, count, OBJECTS0);
    }

    public static Entry entry(String name, EntryType type, int count, Object...  args) throws BlockException {
        return Env.sph.entry(name, type, count, args);
    }
}
