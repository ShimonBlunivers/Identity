package me.blunivers.identity;


import me.blunivers.identity.Environment.EnvironmentManager;
import me.blunivers.identity.Health.HealthManager;
import me.blunivers.identity.Health.HealthRegistry;
import me.blunivers.identity.Jobs.Job;
import me.blunivers.identity.Jobs.JobManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;


public class Commands implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (label.equalsIgnoreCase("identities") || label.equalsIgnoreCase("ids")) {
                if (args.length > 0 && args.length < 4) {

                    Job targetJob = null;
                    if (args.length > 1) targetJob = JobManager.getJob(args[1]);

                    Player targetPlayer = null;
                    if (args.length > 2) targetPlayer = Bukkit.getPlayerExact(args[2]);

                    if (processCommand(args[0].toLowerCase(), player, targetPlayer, targetJob)) return true;
                    else {
                        sender.sendMessage(ChatColor.RED + "Invalid arguments!");
                        return false;
                    }
                }
                else {
                    sender.sendMessage(ChatColor.RED + "Invalid command!");
                    return false;
                }
            } else if (label.equalsIgnoreCase("idboard")) {
                HealthManager.addConditionToPlayer(player, HealthManager.cold);
                HealthManager.addConditionToPlayer(player, HealthManager.tetanus);
                sender.sendMessage(ChatColor.GREEN + "Infected!");
//                EnvironmentManager.givePlayerCustomBlock(player, EnvironmentManager.CustomBlockID.CanalLid, Material.IRON_TRAPDOOR);
            }
        }

        return false;
    }

    private boolean processCommand(String command, Player sender, Player targetPlayer, Job targetJob) {

        if (!command.equals("browse") && targetPlayer == null) targetPlayer = sender;

        switch (command) {
            case "browse":
                if (targetPlayer == null && targetJob == null) return browseCommand(sender);
                else return false;
            case "info":
                if (targetJob == null) return infoCommand(sender, targetPlayer);
                else return false;
            case "join":
                if (targetJob != null) return joinCommand(targetPlayer, targetJob);
                else return false;
            case "progress":
                if (targetJob != null) return progressCommand(targetPlayer, targetJob);
                else return false;
            case "leave":
                if (targetJob != null) return leaveCommand(targetPlayer, targetJob);
                else return false;
        }
        return false;
    }

    private boolean browseCommand(Player sender) {
        sender.sendMessage(ChatColor.WHITE + JobManager.jobs.toString());
        return true;
    }
    private boolean infoCommand(Player sender, Player targetPlayer) {
        sender.sendMessage(ChatColor.WHITE + JobManager.occupationRegistries.toString());
        sender.sendMessage(ChatColor.WHITE + JobManager.getPlayerJobsFromFile(targetPlayer).toString());
        return true;
    }
    private boolean joinCommand(Player targetPlayer, Job targetJob) {
        JobManager.employPlayer(targetPlayer, targetJob);
        ScoreboardManager.getInstance().updateEverything();
        return true;
    }
    private boolean progressCommand(Player targetPlayer, Job targetJob) {
        JobManager.levelUp(targetPlayer, targetJob);
        return true;
    }
    private boolean leaveCommand(Player targetPlayer, Job targetJob) {
        JobManager.leave(targetPlayer, targetJob);
        ScoreboardManager.getInstance().updateEverything();
        return true;
    }
}
