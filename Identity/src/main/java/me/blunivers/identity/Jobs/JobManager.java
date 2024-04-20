package me.blunivers.identity.Jobs;


import me.blunivers.identity.Identity;
import me.blunivers.identity.Manager;
import me.blunivers.identity.ScoreboardManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.ArrayList;


public class JobManager extends Manager implements Listener {
    private final static JobManager instance = new JobManager();
    protected static String path = "";
    public static int jobLimit = 3;

    @Override
    public void load() {
        JobType.loadJobTypes();
    }


    @EventHandler
    public void injectSyringe(PlayerInteractAtEntityEvent event) {
    }

    public static void employPlayer(Player player, JobType jobType) {


        ArrayList<JobType> playerJobTypes = Identity.database.jobs_getJobTypes(player);
        if (playerJobTypes.contains(jobType)) {
            player.sendMessage(ChatColor.RED + "You are already employed as " + jobType.name + "!");
        }
        else if (playerJobTypes.size() >= jobLimit){
            player.sendMessage(ChatColor.RED + "You are have already reached the max amount of jobs!");
            player.sendMessage(ChatColor.RED + "Leave a job to join another one.");
        }
        else {
            Identity.database.jobs_employPlayer(player, jobType);
            ScoreboardManager.getInstance().updateScoreboard(player);
            player.sendMessage(ChatColor.GREEN + "You are now employed as " + jobType.name + "!");
        }
    }

    public static void progress(Player player, JobType jobType) {
        JobInstance jobInstance = Identity.database.jobs_getJobInstance(player, jobType);
        if (jobInstance != null) {
            Identity.database.jobs_updateProgress(player, jobType, jobInstance.level + 1, 0);
            ScoreboardManager.getInstance().updateScoreboard(player);
        }
    }

    public static void leaveJob(Player player, JobType jobType) {

        ArrayList<JobType> playerJobTypes = Identity.database.jobs_getJobTypes(player);

        if (playerJobTypes.contains(jobType)){
            Identity.database.jobs_leaveJob(player, jobType);
            ScoreboardManager.getInstance().updateScoreboard(player);
            player.sendMessage(ChatColor.GREEN + "You've left the job " + jobType.name + "!");
        }
        else {
            player.sendMessage(ChatColor.RED + "You already weren't " + jobType.name + "!");
        }
    }

    public static JobType getJob(String name) {
        return JobType.get(name);
    }
    public static JobManager getInstance() {
        return instance;
    }
}
