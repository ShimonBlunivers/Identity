package me.blunivers.identity;

public abstract class DataItem {

    public String name;
    public String displayName = "";

    @Override
    public String toString() {
        return displayName;
    }
}
