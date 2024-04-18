package me.blunivers.identity.Jobs;


import me.blunivers.identity.Health.HealthManager;
import me.blunivers.identity.Identity;
import me.blunivers.identity.Items.ItemManager;
import me.blunivers.identity.Jobs.Illegal.*;
import me.blunivers.identity.Jobs.Legal.*;
import me.blunivers.identity.Manager;
import me.blunivers.identity.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Set;


public class JobManager extends Manager implements Listener {


    private final static JobManager instance = new JobManager();
    public static ArrayList<OccupationRegistry> occupationRegistries = new ArrayList<>();

    public static final ArrayList<Job> jobs = new ArrayList<>();


    // Legal jobs
    public static final Job doctor = new Doctor("Doktor");

    // Illegal jobs
    public static final Job killer = new Killer("Vrah");


    protected static String path = "";

    @Override
    public void load() {
        path = "jobs.";
        Set<String> _jobStringList = file.getKeys(true);

        for (Job job : jobs)
            if (!_jobStringList.contains(path + "data." + job.name))
                file.set(path + "data." + job.name, new ArrayList<ArrayList<Occupation>>());

        Identity.plugin.saveConfig();
        loadOccupations();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        updateOccupations(player);
        ScoreboardManager.getInstance().updateScoreboard(player);
    }
    @EventHandler
    public void injectSyringe(PlayerInteractAtEntityEvent event) {
    }

    public static void employPlayer(Player player, Job job) {

        if (file.isSet(path + "data." + job.name + "." + player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are already employed as " + job.name + "!");
        }
        else if (getPlayerJobsFromFile(player).size() >= getPlayerOccupationRegistry(player).jobLimit) {
            player.sendMessage(ChatColor.RED + "You are have already reached the max amount of jobs!");
        }
        else {
            String directPath = path + "data." + job.name + "." + player.getUniqueId() + ".";

            file.set(directPath + "level", 1);

            Identity.plugin.saveConfig();

            player.sendMessage(ChatColor.GREEN + "You are now employed as " + job.name + "!");


            updateOccupations(player);
        }
    }

    public static void levelUp(Player player, Job job) {
        if (file.isSet(path + "data." + job.name + "." + player.getUniqueId())) {


            String directPath = path + "data." + job.name + "." + player.getUniqueId() + ".";

            if ((int) file.get(directPath + "level") >= job.maxLevel) return;

            file.set(directPath + "level",  1 + (int) file.get(directPath + "level"));

            Identity.plugin.saveConfig();

            updateOccupations(player);
        }
    }

    public static void leave(Player player, Job job) {

        if (file.isSet(path + "data." + job.name + "." + player.getUniqueId())) {

            file.set(path + "data." + job.name + "." + player.getUniqueId(), null);

            Identity.plugin.saveConfig();

            player.sendMessage(ChatColor.GREEN + "You've left the job " + job.name + "!");

            updateOccupations(player);
        }
        else {
            player.sendMessage(ChatColor.RED + "You already weren't " + job.name + "!");
        }
    }


    public static ArrayList<Occupation> getPlayerJobsFromFile(Player player) {
        ArrayList<Occupation> occupations = new ArrayList<>();
        for (Job job : jobs)
            if (file.isSet(path + "data." + job.name + "." + player.getUniqueId())) {
                String directPath = path + "data." + job.name + "." + player.getUniqueId() + ".";
                occupations.add(new Occupation(job, (int) file.get(directPath + "level")));
            }
        return occupations;
    }

    public static OccupationRegistry getPlayerOccupationRegistry(Player player) {
        for (OccupationRegistry registry : occupationRegistries) if (registry.player.getUniqueId().equals(player.getUniqueId())) return registry;
        return new OccupationRegistry(player);
    };
    public static Job getJob(String name) {
        for (Job job : jobs) if (job.name.equalsIgnoreCase(name)) return job;
        return null;
    }

    public static void loadOccupations() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateOccupations(player);
        }
    }

    private static void removeRegistry(Player player) {
        occupationRegistries.removeIf(oRegistry -> oRegistry.player.getUniqueId().equals(player.getUniqueId()));
    }
    public static void updateOccupations(Player player) {
        removeRegistry(player);
        OccupationRegistry occupationRegistry = new OccupationRegistry(player);
        for (Occupation occupation : JobManager.getPlayerJobsFromFile(player)) occupationRegistry.addOccupation(occupation); // Co to je za shitfest toto XDDd
        occupationRegistries.add(occupationRegistry);
    }
    public static JobManager getInstance() {
        return instance;
    }
}
