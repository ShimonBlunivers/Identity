package me.blunivers.identity;

import me.blunivers.identity.Environment.BlockInstance;
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
            case "test": // /test
                testCommand();
                return true;
            case "idstick": // /idstick
                EnvironmentManager.givePlayerIdentityStick(player);
                return true;
            case "jobs": // /jobs
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
                    case "list": // /jobs list
                        jobsBrowseCommand(sender);
                        return true;

                    case "info": // /jobs info

                        if (targetJobType == null) {
                            sender.sendMessage(Component.text("The '/jobs info <job>' command requires a job argument!",
                                    NamedTextColor.RED));
                            return false;
                        }
                        jobsInfoCommand(sender, targetPlayer);
                        return true;

                    case "join": // /jobs join
                        if (targetJobType == null) {
                            sender.sendMessage(Component.text("The '/jobs join <job>' command requires a job argument!",
                                    NamedTextColor.RED));
                            return false;
                        }
                        jobsJoinCommand(sender, targetPlayer, targetJobType);
                        return true;

                    case "progress": // /jobs progress
                        if (targetJobType == null) {
                            sender.sendMessage(
                                    Component.text("The '/jobs progress <job>' command requires a job argument!",
                                            NamedTextColor.RED));
                            return false;
                        }
                        jobsProgressCommand(sender, targetPlayer, targetJobType);
                        return true;

                    case "leave": // /jobs leave
                        if (targetJobType == null) {
                            sender.sendMessage(
                                    Component.text("The '/jobs leave <job>' command requires a job argument!",
                                            NamedTextColor.RED));
                            return false;
                        }
                        jobsLeaveCommand(sender, targetPlayer, targetJobType);
                        return true;
                }

            case "environment": // /environment
                if (args.length == 0 || args.length > 4) {
                    return false;
                }
                subLabel = args[0].toLowerCase();
                switch (subLabel) {
                    case "blocklist": // /environment blocklist
                        BlockMenu.singleton.open(sender);
                        return true;
                    case "door": // /environment door
                        if (args.length < 2) {
                            return false;
                        }
                        subLabel = args[1].toLowerCase();
                        switch (subLabel) {
                            case "info": // /environment door info
                                environmentDoorInfoCommand(sender);
                                return true;
                            case "remove": // /environment door remove
                                if (args.length < 3) {
                                    return false;
                                }
                                targetJobType = JobManager.getJob(args[2]);
                                if (targetJobType == null) {
                                    sender.sendMessage(Component.text("Invalid job!", NamedTextColor.RED));
                                    return false;
                                }

                                environmentDoorRemoveCommand(sender, targetJobType);
                                return true;
                            case "add": // /environment door add
                                if (args.length < 4) {
                                    return false;
                                }
                                targetJobType = JobManager.getJob(args[2]);
                                if (targetJobType == null) {
                                    sender.sendMessage(Component.text("Invalid job!", NamedTextColor.RED));
                                    return false;
                                }

                                int requiredLevel;
                                try {
                                    requiredLevel = Integer.parseInt(args[3]);
                                }
                                catch(NumberFormatException e) {
                                    sender.sendMessage(Component.text("Invalid required-level!", NamedTextColor.RED));
                                    return false;
                                }

                                environmentDoorAddCommand(sender, targetJobType, requiredLevel);
                                return true;
                        }
                }

            case "health": // /health
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
                    case "infect": // /health infect
                        healthInfectCommand(sender, targetPlayer);
                        return true;
                    case "cure": // /health cure
                        healthCureCommand(sender, targetPlayer);
                        return true;
                    case "syringe": // /health syringe
                        healthSyringeCommand(sender, targetPlayer);
                }
        }
        return false;
    }

    private void environmentDoorInfoCommand(CommandSender sender) {
        Player player = (Player) sender;
        BlockInstance customBlock = EnvironmentManager.singleton.getCustomBlock(player.getTargetBlockExact(10));
        if (customBlock == null) {
            sender.sendMessage(Component.text("These doors are not custom!", NamedTextColor.RED));
            return;
        }
        if (customBlock.metadata.isEmpty()) {
            sender.sendMessage(Component.text("The door doesn't have any permissions set.", NamedTextColor.YELLOW));
        }
        sender.sendMessage(Component.text("The door has the following permissions set:", NamedTextColor.GREEN));
        sender.sendMessage(Component.text(customBlock.metadata, NamedTextColor.WHITE));
    }

    private void environmentDoorRemoveCommand(CommandSender sender, JobType targetJobType) {
    }

    private void environmentDoorAddCommand(CommandSender sender, JobType targetJobType, int requiredLevel) {

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
            ScoreboardManager.singleton.updateScoreboard(targetPlayer);
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