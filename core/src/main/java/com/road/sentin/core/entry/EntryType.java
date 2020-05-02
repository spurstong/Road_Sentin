package com.road.sentin.core.entry;

public enum EntryType {
    IN("IN"),
    OUT("OUT");

    private final String name;
    EntryType(String s) {
        name = s;
    }
    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    @Override
    public String toString() {
        return name;
    }
}
