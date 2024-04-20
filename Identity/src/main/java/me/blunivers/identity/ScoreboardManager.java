package me.blunivers.identity;

import me.blunivers.identity.Health.HealthManager;
import me.blunivers.identity.Jobs.JobInstance;
import me.blunivers.identity.Jobs.JobManager;
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
        Objective objective = scoreboard.registerNewObjective(Identity.plugin.getName(), Criteria.DUMMY,"identity_statistics");

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.BLUE + "" + ChatColor.BOLD + "IDENTITY");

        ArrayList<String> jobString = new ArrayList<>();

        for (JobInstance jobInstance : Identity.database.jobs_getJobInstances(player)) {
            jobString.add(jobInstance.jobType.displayName + " ["+ jobInstance.level +"]");
        }

        if (jobString.isEmpty()) jobString.add("Nezaměstnaný");

        String healthString = "";
        if (HealthManager.getHealthConditions(player).isEmpty()) healthString = ChatColor.GREEN + "Zdravý";
        else healthString = ChatColor.RED + HealthManager.getHealthConditions(player);


        objective.getScore(ChatColor.WHITE + "                         ")
                                                                                        .setScore(11);
        objective.getScore(ChatColor.WHITE + "" + ChatColor.BOLD + "Jméno:")
                                                                                        .setScore(10);
        objective.getScore(ChatColor.AQUA + player.getDisplayName())
                                                                                        .setScore(9);
        objective.getScore(ChatColor.WHITE + "        ")
                                                                                        .setScore(8);
        objective.getScore(ChatColor.WHITE + "" + ChatColor.BOLD +"Povolání:")
                                                                                        .setScore(7);
        objective.getScore(ChatColor.GRAY + String.join(", ", jobString))
                                                                                        .setScore(6);
        objective.getScore(ChatColor.WHITE + "              ")
                                                                                        .setScore(5);
        objective.getScore(ChatColor.WHITE + "" + ChatColor.BOLD + "Stav:")
                                                                                        .setScore(4);
        objective.getScore(healthString)
                                                                                        .setScore(3);
        objective.getScore(ChatColor.WHITE + "                 ")
                                                                                        .setScore(2);
        objective.getScore(ChatColor.WHITE + "" + ChatColor.BOLD + "Zůstatek:")
                                                                                        .setScore(1);
        objective.getScore(ChatColor.GOLD + "0 Peněz")

                                                                                        .setScore(0);
        player.setScoreboard(scoreboard);

    }

    public static ScoreboardManager getInstance() {
        return instance;
    }
}
