package me.blunivers.identity;

import me.blunivers.identity.Health.HealthManager;
import me.blunivers.identity.Jobs.JobManager;
import me.blunivers.identity.Jobs.Occupation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;

public class ScoreboardManager extends Manager implements Runnable {

    private final static ScoreboardManager instance = new ScoreboardManager();


    @Override
    public void load() {

    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getScoreboard();
            if (player.getScoreboard().getObjective(Identity.plugin.getName()) != null) {
                updateScoreboard(player);
            }
            else {
                createNewScoreboard(player);
            }
        }
    }

    public void updateEverything( ){
        for (Player player : Bukkit.getOnlinePlayers()) {
            createNewScoreboard(player);
        }
    }

    private void createNewScoreboard(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective(Identity.plugin.getName(), Criteria.DUMMY,"identity_statistics");

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.BLUE + "" + ChatColor.BOLD + "IDENTITY");

        ArrayList<String> jobString = new ArrayList<>();

        for (Occupation occupation : JobManager.getPlayerOccupationRegistry(player).occupations) {
            jobString.add(occupation.job.displayName + " ["+ occupation.progression +"]");
        }

        if (jobString.isEmpty()) jobString.add("Nezaměstnaný [999]");

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

    public void updateScoreboard(Player player) {
        createNewScoreboard(player);
    }

    public static ScoreboardManager getInstance() {
        return instance;
    }
}
