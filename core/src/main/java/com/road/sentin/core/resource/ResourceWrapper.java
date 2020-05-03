package com.road.sentin.core.resource;


import com.road.sentin.core.entry.EntryType;

public class ResourceWrapper {
    protected final String name;
    protected final EntryType entryType;

    public ResourceWrapper(String name, EntryType entryType) {
        this.name = name;
        this.entryType = entryType;
    }

    public String getName() {
        return name;
    }

    public EntryType getEntryType() {
        return entryType;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ResourceWrapper) {
            ResourceWrapper rw = (ResourceWrapper)obj;
            return rw.getName().equals(getName());
        }
        return false;
    }
}

