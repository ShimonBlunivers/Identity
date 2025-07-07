package me.blunivers.identity.Items;

import me.blunivers.identity.Items.Syringe.Syringe;
import net.kyori.adventure.text.Component;
import me.blunivers.identity.Manager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class ItemManager extends Manager implements Listener {

    private final static ItemManager instance = new ItemManager();
    public static ArrayList<CustomItem> customItems = new ArrayList<>();

    public static Syringe syringe = new Syringe();

    public static String listTitleMark = ".";
    public static String listItemMark = " - ";

    @EventHandler
    public void useCustomItem(PlayerInteractAtEntityEvent event) {
        ItemStack item = event.getPlayer().getInventory().getItem(event.getPlayer().getInventory().getHeldItemSlot());
        if (item != null) {
            ItemMeta meta = item.getItemMeta();
            if (meta == null)
                return;

            Component displayName = meta.displayName();
            if (displayName == null)
                return;

            Material itemMaterial = item.getType();
            for (CustomItem customItem : customItems) {
                if (displayName.equals(customItem.displayName) && itemMaterial == customItem.material) {
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
