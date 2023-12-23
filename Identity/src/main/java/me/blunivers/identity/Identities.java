package me.blunivers.identity;


import me.blunivers.identity.Jobs.JobManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;


public class Identities implements CommandExecutor {


    static ArrayList<Player> identitiesList = new ArrayList<>();


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (label.equalsIgnoreCase("identities") || label.equalsIgnoreCase("ids")) {
                if (args.length > 0)
                {
                    if (args[0].equalsIgnoreCase("add")){
                        Player target;
                        if (args.length == 1) target = player;
                        else {
                            target = Bukkit.getPlayerExact(args[1]);
                            if (target == null) {
                                sender.sendMessage(ChatColor.RED + "Player '" + args[1] + "' doesn't exist!");
                                return false;
                            }
                        }
                        if (identitiesList.contains(target)) {
                            sender.sendMessage(ChatColor.YELLOW + "Player '" + target + "' is already added!");
                        }
                        else {
                            identitiesList.add(target);
                            sender.sendMessage(ChatColor.GREEN + "Player '" + target + "' successfully added.");
                        }
                        return true;
                    }
                }
            }
        }


//        Bukkit.getServer().dispatchCommand(sender, "say haha");

        return false;
    }
}
