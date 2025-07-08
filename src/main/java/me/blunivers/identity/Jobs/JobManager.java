package me.blunivers.identity.Jobs;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.reflections.Reflections;

import me.blunivers.identity.Identity;
import me.blunivers.identity.Manager;
import me.blunivers.identity.ScoreboardManager;

import java.util.ArrayList;
import java.util.Set;

public class JobManager extends Manager implements Listener {
    private final static JobManager singleton = new JobManager();
    protected static String path = "";
    public static int jobLimit = 3;

    @Override
    public void load() {
        Reflections reflections = new Reflections("me.blunivers.identity");
        Set<Class<? extends JobType>> jobClasses = reflections.getSubTypesOf(JobType.class);
        for (Class<? extends JobType> jobClass : jobClasses) {
            try {
                jobClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                System.out.println("Failed to load job types! " + e.getMessage());
            }
        }
    }

    @EventHandler
    public void injectSyringe(PlayerInteractAtEntityEvent event) {
        // TODO implement -- looking back, I don't think it's supposed to be in JobManager
    }

    public static void employPlayer(Player player, JobType jobType) {
        ArrayList<JobType> playerJobTypes = Identity.database.jobs_getJobTypes(player);
        if (playerJobTypes.contains(jobType)) {
            player.sendMessage(
                    Component.text("You are already employed as " + jobType.name + "!", NamedTextColor.RED));
        } else if (playerJobTypes.size() >= jobLimit) {
            player.sendMessage(Component.text(
                    "You have already reached the max amount (" + String.valueOf(jobLimit) + ") of jobs!",
                    NamedTextColor.RED));
            player.sendMessage(Component.text("Leave a job to join another one.", NamedTextColor.RED));
        } else {
            Identity.database.jobs_employPlayer(player, jobType);
            ScoreboardManager.getInstance().updateScoreboard(player);
            player.sendMessage(
                    Component.text("You are now employed as " + jobType.name + "!", NamedTextColor.GREEN));
        }
    }

    public static boolean progress(Player player, JobType jobType) {
        JobInstance jobInstance = Identity.database.jobs_getJobInstance(player, jobType);
        if (jobInstance == null) {
            return false;
        }
        Identity.database.jobs_updateProgress(player, jobType, jobInstance.level + 1, 0);
        ScoreboardManager.getInstance().updateScoreboard(player);
        return true;
    }

    public static boolean leaveJob(Player player, JobType jobType) {
        ArrayList<JobType> playerJobTypes = Identity.database.jobs_getJobTypes(player);

        if (playerJobTypes.contains(jobType)) {
            Identity.database.jobs_leaveJob(player, jobType);
            ScoreboardManager.getInstance().updateScoreboard(player);
            return true;
        } 
        return false;
    }

    public static JobType getJob(String name) {
        return JobType.get(name);
    }

    public static JobManager getSingleton() {
        return singleton;
    }
}