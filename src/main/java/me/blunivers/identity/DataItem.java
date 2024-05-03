package me.blunivers.identity;

public abstract class DataItem {

    public String name;
    public String displayName;

    @Override
    public String toString() {
        if (displayName.isEmpty()) return "dataItem#" + name;
        return displayName;
    }
}
