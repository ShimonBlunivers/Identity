package me.blunivers.identity;


import me.blunivers.identity.Environment.EnvironmentManager;
import me.blunivers.identity.Health.HealthManager;
import me.blunivers.identity.Items.ItemManager;
import me.blunivers.identity.Jobs.Job;
import me.blunivers.identity.Jobs.JobManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;


public class Identity extends JavaPlugin implements CommandExecutor {
    public static Identity plugin;

    ArrayList<BukkitTask> tasks = new ArrayList<>();

    public static int healthManagerTimer = 20;

    public static NamespacedKey namespacedKey;

    public static final String identificator = " Identity-Plugin";

    @Override
    public void onEnable() {
        plugin = this;
        namespacedKey = new NamespacedKey(plugin, "identity-plugin");

        saveDefaultConfig(); // CHANGE TO saveConfig();

        Commands commands = new Commands();

        getCommand("identities").setExecutor(commands);
        getCommand("idboard").setExecutor(commands);
        getCommand("idstick").setExecutor(commands);
        getCommand("infect").setExecutor(commands);
        getCommand("syringe").setExecutor(commands);
        getCommand("cure").setExecutor(commands);

        getServer().getPluginManager().registerEvents(JobManager.getInstance(), this);
        getServer().getPluginManager().registerEvents(EnvironmentManager.getInstance(), this);
        getServer().getPluginManager().registerEvents(HealthManager.getInstance(), this);
        getServer().getPluginManager().registerEvents(ItemManager.getInstance(), this);

        JobManager.getInstance().load();
        EnvironmentManager.getInstance().load();
        HealthManager.getInstance().load();
        ItemManager.getInstance().load();

        getLogger().info("Identity has been enabled!");

        tasks.add(getServer().getScheduler().runTaskTimer(this, ScoreboardManager.getInstance(), 0, 20));
        tasks.add(getServer().getScheduler().runTaskTimer(this, EnvironmentManager.getInstance(), 0, 20));
        tasks.add(getServer().getScheduler().runTaskTimer(this, HealthManager.getInstance(), 0, healthManagerTimer));
    }

    @Override
    public void onDisable() {

        for (BukkitTask task : tasks) if (task != null && !task.isCancelled()) task.cancel();

        getLogger().info("Identity has been disabled!");
    }

}
