package me.blunivers.identity;


import me.blunivers.identity.Jobs.JobManager;
import org.bukkit.command.*;
import org.bukkit.plugin.java.JavaPlugin;


public class Identity extends JavaPlugin implements CommandExecutor {

    @Override
    public void onEnable() {
        getLogger().info("IdentityPlugin has been enabled!");

        getCommand("identities").setExecutor(new Identities());
    }

    @Override
    public void onDisable() {
        getLogger().info("IdentityPlugin has been disabled!");
    }
}
