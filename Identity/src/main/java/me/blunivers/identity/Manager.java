package me.blunivers.identity;

import org.bukkit.configuration.file.FileConfiguration;

public abstract class Manager {

    protected static String path = "";
    protected static final FileConfiguration file = Identity.plugin.getConfig();

    public abstract void load();
}
