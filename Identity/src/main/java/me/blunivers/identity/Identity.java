package me.blunivers.identity;


import me.blunivers.identity.Environment.EnvironmentManager;
import me.blunivers.identity.Health.HealthManager;
import me.blunivers.identity.Items.ItemManager;
import me.blunivers.identity.Jobs.JobManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.sql.SQLException;
import java.util.ArrayList;

public class Identity extends JavaPlugin implements CommandExecutor, Listener {
    public static Identity plugin;

    ArrayList<BukkitTask> tasks = new ArrayList<>();

    public static int healthManagerTimer = 100;

    public static NamespacedKey namespacedKey;

    public static final String identificator = " Identity-Plugin";

    public static Database database;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        database.players_join(player);
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        database.players_leave(player);
    }
    @Override
    public void onEnable() {

        plugin = this;
        namespacedKey = new NamespacedKey(plugin, "identity-plugin");

        saveDefaultConfig(); // CHANGE TO saveConfig();

        Commands commands = new Commands();

        try {
            database = new Database(getDataFolder().getAbsolutePath() + "/identity.sqlite.db");
        }catch (SQLException e){
            System.out.println("Failed to connect to the database! " + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
        }

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
        getServer().getPluginManager().registerEvents(HealthManager.getInstance(), this);
        getServer().getPluginManager().registerEvents(this, this);

        database.players_reset();
        for (Player player : Bukkit.getOnlinePlayers()) {
            database.players_join(player);
        }

        JobManager.getInstance().load();
        EnvironmentManager.getInstance().load();
        HealthManager.getInstance().load();
        ItemManager.getInstance().load();
        ScoreboardManager.getInstance().load();

        ScoreboardManager.getInstance().updateEverything();

        tasks.add(getServer().getScheduler().runTaskTimer(this, EnvironmentManager.getInstance(), 0, 200));
        tasks.add(getServer().getScheduler().runTaskTimer(this, HealthManager.getInstance(), 0, healthManagerTimer));



        getLogger().info("Identity has been enabled!");
    }

    @Override
    public void onDisable() {

        for (BukkitTask task : tasks) if (task != null && !task.isCancelled()) task.cancel();

        try{
            database.closeConnection();
        }catch (SQLException e){
            System.out.println("Failed to execute onDisable! " + e.getMessage());
        }

        getLogger().info("Identity has been disabled!");
    }

}
