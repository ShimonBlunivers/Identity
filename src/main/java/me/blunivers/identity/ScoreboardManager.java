package me.blunivers.identity;

import me.blunivers.identity.Health.HealthManager;
import me.blunivers.identity.Jobs.JobInstance;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardManager extends Manager implements Listener {
    public static final ScoreboardManager singleton = new ScoreboardManager();

    private static final LegacyComponentSerializer serializer = LegacyComponentSerializer.legacySection(); // for § formatting

    @Override
    public void load() {
        // No-op
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        updateScoreboard(event.getPlayer());
    }

    public void updateEverything() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateScoreboard(player);
        }
    }

    public void updateScoreboard(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("identity_stats", Criteria.DUMMY, Component.text("identity_statistics"));

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        Component title = Component.text("IDENTITY", NamedTextColor.BLUE).decorate(TextDecoration.BOLD);
        objective.displayName(title);

        List<String> jobString = new ArrayList<>();
        for (JobInstance jobInstance : Identity.database.jobs_getJobInstances(player)) {
            jobString.add(jobInstance.jobType.displayName + " [" + jobInstance.level + "]");
        }
        if (jobString.isEmpty()) jobString.add("Nezaměstnaný");

        int scoreindex = 10;

        List<String> healthString = HealthManager.getHealthConditions(player);
        boolean healthy = true;
        int maxConditions = 6;

        if (!healthString.isEmpty()) {
            healthy = HealthManager.isHealthy(player);
            scoreindex += Math.min(maxConditions, healthString.size());
        }

        objective.getScore(serializer.serialize(Component.text("Jméno:", NamedTextColor.WHITE).decorate(TextDecoration.BOLD)))
            .setScore(scoreindex--);
        objective.getScore(serializer.serialize(player.displayName()))
            .setScore(scoreindex--);
        objective.getScore(" ") // spacing
            .setScore(scoreindex--);

        objective.getScore(serializer.serialize(Component.text("Povolání:", NamedTextColor.WHITE).decorate(TextDecoration.BOLD)))
            .setScore(scoreindex--);
        objective.getScore(serializer.serialize(Component.text(String.join(", ", jobString), NamedTextColor.GRAY)))
            .setScore(scoreindex--);

        objective.getScore("  ") // spacing
            .setScore(scoreindex--);

        Component statusLine = Component.text("Stav: ", NamedTextColor.WHITE).decorate(TextDecoration.BOLD)
            .append(healthy ? Component.text("Zdravý", NamedTextColor.GREEN) : Component.empty());
        objective.getScore(serializer.serialize(statusLine)).setScore(scoreindex--);

        int shownConditions = Math.min(maxConditions, healthString.size());
        for (int i = 0; i < shownConditions; i++) {
            String condition = healthString.get(i);
            int score = scoreindex - i;
            if (shownConditions == maxConditions && i == shownConditions - 1) {
                objective.getScore(" . . . ").setScore(score);
            } else {
                objective.getScore(condition).setScore(score);
            }
        }

        scoreindex -= shownConditions + 1;

        objective.getScore("   ") // spacing
            .setScore(scoreindex--);

        objective.getScore(serializer.serialize(Component.text("Zůstatek:", NamedTextColor.WHITE).decorate(TextDecoration.BOLD)))
            .setScore(scoreindex--);
        objective.getScore(serializer.serialize(Component.text("0 Peněz", NamedTextColor.GOLD)))
            .setScore(scoreindex--);

        player.setScoreboard(scoreboard);
    }
}
