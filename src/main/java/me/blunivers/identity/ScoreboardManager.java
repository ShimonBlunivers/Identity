package me.blunivers.identity;

import me.blunivers.identity.Health.HealthManager;
import me.blunivers.identity.Jobs.JobInstance;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;

public class ScoreboardManager extends Manager implements Listener {

    private final static ScoreboardManager instance = new ScoreboardManager();


    @Override
    public void load() {

    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ScoreboardManager.getInstance().updateScoreboard(player);
    }

    public void updateEverything( ){
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateScoreboard(player);
        }
    }

    public void updateScoreboard(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective(Identity.instance.getName(), Criteria.DUMMY,"identity_statistics");

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.BLUE + "" + ChatColor.BOLD + "IDENTITY");

        ArrayList<String> jobString = new ArrayList<>();

        for (JobInstance jobInstance : Identity.database.jobs_getJobInstances(player)) {
            jobString.add(jobInstance.jobType.displayName + " ["+ jobInstance.level +"]");
        }

        if (jobString.isEmpty()) jobString.add("Nezaměstnaný");

        int scoreindex = 10;

        ArrayList<String> healthString = HealthManager.getHealthConditions(player);
        boolean healthy = true;
        int maximumEntriesOfConditions = 6;
        if (!healthString.isEmpty()) {
            healthy = HealthManager.isHealthy(player);
            scoreindex += Math.min(maximumEntriesOfConditions, healthString.size());
        }

        objective.getScore(ChatColor.WHITE + "" + ChatColor.BOLD + "Jméno:")
                                                                                        .setScore(scoreindex);
        scoreindex--;
        objective.getScore(ChatColor.AQUA + player.getDisplayName())
                                                                                        .setScore(scoreindex);
        scoreindex--;
        objective.getScore(ChatColor.WHITE + "        ")
                                                                                        .setScore(scoreindex);
        scoreindex--;
        objective.getScore(ChatColor.WHITE + "" + ChatColor.BOLD +"Povolání:")
                                                                                        .setScore(scoreindex);
        scoreindex--;
        objective.getScore(ChatColor.GRAY + String.join(", ", jobString))
                                                                                        .setScore(scoreindex);
        scoreindex--;
        objective.getScore(ChatColor.WHITE + "              ")
                                                                                        .setScore(scoreindex);
        scoreindex--;
        objective.getScore(ChatColor.WHITE + "" + ChatColor.BOLD + "Stav: " + (healthy ? ChatColor.GREEN + "Zdravý" : ""))
                                                                                        .setScore(scoreindex);

        int numberOfEntriesOfConditions = Math.min(maximumEntriesOfConditions, healthString.size());
        for (int i = 1; i <= numberOfEntriesOfConditions; i++){
            int index = scoreindex - i;
            if (numberOfEntriesOfConditions == maximumEntriesOfConditions && i == numberOfEntriesOfConditions)
                objective.getScore(" . . . ")
                        .setScore(index);

            else
                objective.getScore(healthString.get(i - 1))
                                                                                           .setScore(index);
        }

        scoreindex -= numberOfEntriesOfConditions + 1;
        objective.getScore(ChatColor.WHITE + "                 ")
                                                                                        .setScore(scoreindex);
        scoreindex--;
        objective.getScore(ChatColor.WHITE + "" + ChatColor.BOLD + "Zůstatek:")
                                                                                        .setScore(scoreindex);
        scoreindex--;
        objective.getScore(ChatColor.GOLD + "0 Peněz")
                                                                                        .setScore(scoreindex);
        player.setScoreboard(scoreboard);

    }

    public static ScoreboardManager getInstance() {
        return instance;
    }
}
