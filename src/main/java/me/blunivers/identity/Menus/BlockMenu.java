package me.blunivers.identity.Menus;

import me.blunivers.identity.Environment.BlockType;
import me.blunivers.identity.Environment.EnvironmentManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import me.blunivers.identity.Identity;
import me.blunivers.identity.Utility;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BlockMenu implements Listener {
    public static final BlockMenu singleton = new BlockMenu();
    private final Component menuName = Component.text("Výběr Identity Bloků");

    public BlockMenu() {
        Bukkit.getPluginManager().registerEvents(this, Identity.singleton);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().title().equals(menuName)) {
            return;
        }

        event.setCancelled(true);
        if (event.getSlot() > BlockType.get().values().size() - 1) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getClickedInventory().getItem(event.getSlot());
        if (clickedItem == null) {
            return;
        }

        ItemMeta meta = clickedItem.getItemMeta();
        if (meta == null || meta.displayName() == null) {
            return;
        }

        BlockType blockType = BlockType.get(Utility.componentToString(meta.displayName()));
        if (blockType != null) {
            player.getInventory().addItem(EnvironmentManager.getCustomItem(blockType));
        }
    }

    public void open(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Jenom hráči mohou používat tento příkaz!").color(NamedTextColor.RED));
            return;
        }

        int invSize = 9 + (BlockType.get().values().size() / 9) * 9;
        Inventory inventory = Bukkit.createInventory(player, invSize, menuName);

        int i = 0;
        for (BlockType blockType : BlockType.get().values()) {
            inventory.setItem(i++, EnvironmentManager.getCustomItemWithoutLabel(blockType));
        }

        player.openInventory(inventory);
    }

    // private ItemStack getItem(ItemStack item, String name, String... lore) {
    //     ItemMeta meta = item.getItemMeta();
    //     if (meta == null)
    //         return item;

    //     // Set displayName as Component (converted from legacy &-codes)
    //     Component displayNameComp = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
    //             .legacyAmpersand().deserialize(name);
    //     meta.displayName(displayNameComp);

    //     List<Component> loreComponents = new ArrayList<>();
    //     for (String s : lore) {
    //         loreComponents.add(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand()
    //                 .deserialize(s));
    //     }
    //     meta.lore(loreComponents);

    //     item.setItemMeta(meta);
    //     return item;
    // }
}