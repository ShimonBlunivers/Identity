package me.blunivers.identity.Menus;

import me.blunivers.identity.Environment.CustomBlockInstance;
import me.blunivers.identity.Environment.EnvironmentManager;
import me.blunivers.identity.Identity;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static me.blunivers.identity.Environment.EnvironmentManager.getCustomItem;

public class Menu implements Listener, CommandExecutor {
    private final String menuName = "Výběr Identity Bloků";

    public Menu() {
        Bukkit.getPluginManager().registerEvents(this, Identity.instance);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if (!event.getView().getTitle().equals(menuName)) return;
        event.setCancelled(true);
        if (event.getSlot() > CustomBlockInstance.BLOCK_ID.values().length - 1) return;


        Player player = (Player) event.getWhoClicked();
        CustomBlockInstance.BLOCK_ID block_id = CustomBlockInstance.BLOCK_ID.values()[event.getSlot()];

        player.getInventory().addItem(getCustomItem(block_id));
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] ars) {
        if (!(sender instanceof Player player)){
            sender.sendMessage("Jenom hráči mohou používat tento příkaz!");
            return true;
        }

        int invSize = 9 + (CustomBlockInstance.BLOCK_ID.values().length / 9) * 9;
        Inventory inventory = Bukkit.createInventory(player, invSize, menuName);

        for (CustomBlockInstance.BLOCK_ID block_id : CustomBlockInstance.BLOCK_ID.values()) inventory.setItem(block_id.ordinal(), EnvironmentManager.getCustomItemWithoutLabel(block_id));

        player.openInventory(inventory);

        return true;
    }

    private ItemStack getItem(ItemStack item, String name, String ... lore) {
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        List<String> lores = new ArrayList<>();
        for (String s : lore){
            lores.add(ChatColor.translateAlternateColorCodes('&', s));
        }

        meta.setLore(lores);

        item.setItemMeta(meta);

        return item;
    }
}
