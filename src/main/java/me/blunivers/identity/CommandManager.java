package me.blunivers.identity;

import me.blunivers.identity.Environment.EnvironmentManager;
import me.blunivers.identity.Health.Conditions.ConditionType;
import me.blunivers.identity.Health.Conditions.MedicationType;
import me.blunivers.identity.Health.HealthManager;
import me.blunivers.identity.Items.ItemManager;
import me.blunivers.identity.Jobs.JobType;
import me.blunivers.identity.Jobs.JobManager;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class CommandManager implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (label.equalsIgnoreCase("jobs")) {
                if (args.length > 0 && args.length < 4) {

                    JobType targetJobType = null;
                    if (args.length > 1) targetJobType = JobManager.getJob(args[1]);

                    Player targetPlayer = null;
                    if (args.length > 2) targetPlayer = Bukkit.getPlayerExact(args[2]);

                    if (processCommand(args[0].toLowerCase(), player, targetPlayer, targetJobType)) return true;
                    else {
                        sender.sendMessage(Component.text("Invalid arguments!", NamedTextColor.RED));
                        return false;
                    }
                } else {
                    sender.sendMessage(Component.text("Invalid command!", NamedTextColor.RED));
                    return false;
                }

            } else if (label.equalsIgnoreCase("refreshidentityscoreboard")) {
                // EnvironmentManager.givePlayerCustomBlock(player, EnvironmentManager.CustomBlockID.CanalLid, Material.IRON_TRAPDOOR);

            } else if (label.equalsIgnoreCase("idstick")) {
                EnvironmentManager.givePlayerIdentityStick(player);

            } else if (label.equalsIgnoreCase("syringe")) {
                player.getInventory().setItem(
                    player.getInventory().getHeldItemSlot(),
                    ItemManager.syringe.getItem(new String[]{"ColdCure"})
                );

            } else if (label.equalsIgnoreCase("infect")) {
                HealthManager.addConditionToPlayer(player, ConditionType.get("tetanus"));
                sender.sendMessage(Component.text("Infikován!", NamedTextColor.YELLOW));

            } else if (label.equalsIgnoreCase("cure")) {
                Identity.database.health_addMedication(player, MedicationType.medications.get("tetanus_vaccine"));
                sender.sendMessage(Component.text("Vyléčen!", NamedTextColor.GREEN));
            }
        }
        return false;
    }

    boolean processCommand(String command, Player sender, Player targetPlayer, JobType targetJobType) {
        if (!command.equals("browse") && targetPlayer == null) targetPlayer = sender;

        switch (command) {
            case "browse":
                if (targetPlayer == null && targetJobType == null) return browseCommand(sender);
                break;
            case "info":
                if (targetJobType == null) return infoCommand(sender, targetPlayer);
                break;
            case "join":
                if (targetJobType != null) return joinCommand(targetPlayer, targetJobType);
                break;
            case "progress":
                if (targetJobType != null) return progressCommand(targetPlayer, targetJobType);
                break;
            case "leave":
                if (targetJobType != null) return leaveCommand(targetPlayer, targetJobType);
                break;
        }
        return false;
    }

    private boolean browseCommand(Player sender) {
        sender.sendMessage(Component.text(JobType.getJobs().keySet().toString(), NamedTextColor.WHITE));
        return true;
    }

    private boolean infoCommand(Player sender, Player targetPlayer) {
        sender.sendMessage(Component.text(Identity.database.jobs_getJobInstances(targetPlayer).toString(), NamedTextColor.WHITE));
        return true;
    }

    private boolean joinCommand(Player targetPlayer, JobType targetJobType) {
        JobManager.employPlayer(targetPlayer, targetJobType);
        return true;
    }

    private boolean progressCommand(Player targetPlayer, JobType targetJobType) {
        JobManager.progress(targetPlayer, targetJobType);
        return true;
    }

    private boolean leaveCommand(Player targetPlayer, JobType targetJobType) {
        JobManager.leaveJob(targetPlayer, targetJobType);
        ScoreboardManager.getInstance().updateScoreboard(targetPlayer);
        return true;
    }
}