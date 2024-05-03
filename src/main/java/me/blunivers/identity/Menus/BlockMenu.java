package me.blunivers.identity.Menus;

import me.blunivers.identity.Environment.BlockType;
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

public class BlockMenu implements Listener, CommandExecutor {
    private final String menuName = "Výběr Identity Bloků";

    public BlockMenu() {
        Bukkit.getPluginManager().registerEvents(this, Identity.instance);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if (!event.getView().getTitle().equals(menuName)) return;
        event.setCancelled(true);
        if (event.getSlot() > BlockType.get().values().size() - 1) return;

        Player player = (Player) event.getWhoClicked();
        BlockType blockType = BlockType.get(event.getClickedInventory().getItem(event.getSlot()).getItemMeta().getDisplayName());
        if (blockType != null) player.getInventory().addItem(getCustomItem(blockType));
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] ars) {
        if (!(sender instanceof Player player)){
            sender.sendMessage("Jenom hráči mohou používat tento příkaz!");
            return true;
        }

        int invSize = 9 + (BlockType.get().values().size() / 9) * 9;
        Inventory inventory = Bukkit.createInventory(player, invSize, menuName);

        int i = 0;
        for (BlockType blockType : BlockType.get().values()) {
            inventory.setItem(i++, EnvironmentManager.getCustomItemWithoutLabel(blockType));
        }


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
