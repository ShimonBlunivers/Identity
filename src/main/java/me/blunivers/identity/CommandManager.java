package me.blunivers.identity;

import me.blunivers.identity.Environment.EnvironmentManager;
import me.blunivers.identity.Health.Conditions.ConditionType;
import me.blunivers.identity.Health.Conditions.MedicationType;
import me.blunivers.identity.Health.HealthManager;
import me.blunivers.identity.Items.ItemManager;
import me.blunivers.identity.Jobs.JobType;
import me.blunivers.identity.Menus.BlockMenu;
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
        if (!(sender instanceof Player player)) {
            return false;
        }

        Player targetPlayer = (Player) sender;
        if (args.length > 1) {
            Player argPlayer = Bukkit.getPlayerExact(args[1]);
            if (argPlayer != null)
                targetPlayer = argPlayer;
        }

        String subLabel;
        label = label.toLowerCase();
        switch (label) {
            case "idstick":
                EnvironmentManager.givePlayerIdentityStick(player);
                return true;
            case "jobs":
                if (args.length == 0 || args.length > 3) { // If args.length is between 1 and 3
                    return false;
                }

                JobType targetJobType = null;
                if (args.length > 1) {
                    targetJobType = JobManager.getJob(args[1]);
                    if (targetJobType == null) {
                        sender.sendMessage(Component.text("Invalid job!", NamedTextColor.RED));
                        return false;
                    }
                }
                targetPlayer = (Player) sender;
                if (args.length > 2) {
                    targetPlayer = Bukkit.getPlayerExact(args[2]);
                    if (targetPlayer == null) {
                        sender.sendMessage(Component.text("Invalid player!", NamedTextColor.RED));
                        return false;
                    }
                }

                subLabel = args[0].toLowerCase();
                switch (subLabel) {
                    case "list":
                        jobsBrowseCommand(sender);
                        return true;

                    case "info":

                        if (targetJobType == null) {
                            sender.sendMessage(Component.text("The '/jobs info <job>' command requires a job argument!",
                                    NamedTextColor.RED));
                            return false;
                        }
                        jobsInfoCommand(sender, targetPlayer);
                        return true;

                    case "join":
                        if (targetJobType == null) {
                            sender.sendMessage(Component.text("The '/jobs join <job>' command requires a job argument!",
                                    NamedTextColor.RED));
                            return false;
                        }
                        jobsJoinCommand(sender, targetPlayer, targetJobType);
                        return true;

                    case "progress":
                        if (targetJobType == null) {
                            sender.sendMessage(
                                    Component.text("The '/jobs progress <job>' command requires a job argument!",
                                            NamedTextColor.RED));
                            return false;
                        }
                        jobsProgressCommand(sender, targetPlayer, targetJobType);
                        return true;

                    case "leave":
                        if (targetJobType == null) {
                            sender.sendMessage(
                                    Component.text("The '/jobs leave <job>' command requires a job argument!",
                                            NamedTextColor.RED));
                            return false;
                        }
                        jobsLeaveCommand(sender, targetPlayer, targetJobType);
                        return true;

                    default:
                        sender.sendMessage(Component.text("Invalid '/jobs <command>' option!", NamedTextColor.RED));
                        return false;
                }

            case "test":
                testCommand();
                return true;

            case "environment":
                if (args.length != 1) {
                    return false;
                }
                subLabel = args[0].toLowerCase();
                switch (subLabel) {
                    case "blocklist":
                        BlockMenu.singleton.open(sender);
                        return true;
                }

            case "health":
                if (args.length == 0 || args.length > 2) { // If args.length is between 1 and 3
                    return false;
                }
                targetPlayer = (Player) sender;
                if (args.length > 2) {
                    targetPlayer = Bukkit.getPlayerExact(args[2]);
                    if (targetPlayer == null) {
                        sender.sendMessage(Component.text("Invalid player!", NamedTextColor.RED));
                        return false;
                    }
                }

                subLabel = args[0].toLowerCase();
                switch (subLabel) {
                    case "infect":
                        healthInfectCommand(sender, targetPlayer);
                        return true;
                    case "cure":
                        healthCureCommand(sender, targetPlayer);
                        return true;
                    case "syringe":
                        healthSyringeCommand(sender, targetPlayer);
                        return true;
                }
        }
        return false;
    }

    private void jobsBrowseCommand(CommandSender sender) {
        sender.sendMessage(Component.text(JobType.getJobs().keySet().toString(), NamedTextColor.WHITE));
    }

    private void jobsInfoCommand(CommandSender sender, Player targetPlayer) {
        sender.sendMessage(
                Component.text(Identity.database.jobs_getJobInstances(targetPlayer).toString(), NamedTextColor.WHITE));
    }

    private void jobsJoinCommand(CommandSender sender, Player targetPlayer, JobType targetJobType) {
        JobManager.employPlayer(targetPlayer, targetJobType);
    }

    private void jobsProgressCommand(CommandSender sender, Player targetPlayer, JobType targetJobType) {
        if (!JobManager.progress(targetPlayer, targetJobType)) {
            sender.sendMessage(
                    Component.text(targetPlayer.getName() + " is not employed as " + targetJobType.name + "!",
                            NamedTextColor.RED));
        } else {
            sender.sendMessage(Component.text(targetPlayer.getName() + " progressed in " + targetJobType.name + "!",
                    NamedTextColor.GREEN));
        }
    }

    private void jobsLeaveCommand(CommandSender sender, Player targetPlayer, JobType targetJobType) {
        if (JobManager.leaveJob(targetPlayer, targetJobType)) {
            ScoreboardManager.getInstance().updateScoreboard(targetPlayer);
            sender.sendMessage(
                    Component.text(targetPlayer.getName() + " has left the job " + targetJobType.name + "!",
                            NamedTextColor.GREEN));
            return;
        }

        sender.sendMessage(
                Component.text(targetPlayer.getName() + " already wasnt't employed as " + targetJobType.name + "!",
                        NamedTextColor.RED));
    }

    private void testCommand() {
        return; // TODO ?
    }

    private void healthInfectCommand(CommandSender sender, Player targetPlayer) {
        HealthManager.addConditionToPlayer(targetPlayer, ConditionType.get("tetanus"));
        sender.sendMessage(Component.text("Infected!", NamedTextColor.YELLOW));
    }

    private void healthCureCommand(CommandSender sender, Player targetPlayer) {
        Identity.database.health_addMedication(targetPlayer, MedicationType.medications.get("tetanus_vaccine"));
        sender.sendMessage(Component.text("Cured!", NamedTextColor.GREEN));
    }

    private void healthSyringeCommand(CommandSender sender, Player targetPlayer) {
        targetPlayer.getInventory().setItem(
                targetPlayer.getInventory().getHeldItemSlot(),
                ItemManager.syringe.getItem(new String[] { "ColdCure" }));
        sender.sendMessage(Component.text("Item added to inventory!", NamedTextColor.GREEN));
    }
}