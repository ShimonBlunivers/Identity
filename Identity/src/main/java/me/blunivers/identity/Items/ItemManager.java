package me.blunivers.identity.Items;

import me.blunivers.identity.Items.Syringe.Syringe;
import me.blunivers.identity.Jobs.JobManager;
import me.blunivers.identity.Manager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class ItemManager extends Manager implements Listener {

    private final static ItemManager instance = new ItemManager();
    public static ArrayList<CustomItem> customItems = new ArrayList<>();

    public static Syringe syringe = new Syringe();


    public static String listTitleMark = ChatColor.BLACK + ".";
    public static String listItemMark = " - ";


    @EventHandler
    public void useCustomItem(PlayerInteractAtEntityEvent event) {
        ItemStack item = event.getPlayer().getInventory().getItem(event.getPlayer().getInventory().getHeldItemSlot());
        String itemName;
        Material itemMaterial;
        if (item != null) {
            itemName = item.getItemMeta().getDisplayName();
            if (!itemName.isEmpty()) {
                itemMaterial = item.getType();
                for (CustomItem customItem : customItems) if (itemName.equals(customItem.displayName) && itemMaterial == customItem.material) {
                    customItem.useItem(event);
                }
            }
        }
    }

    public static ItemManager getInstance() {
        return instance;
    }

    @Override
    public void load() {

    }
}
