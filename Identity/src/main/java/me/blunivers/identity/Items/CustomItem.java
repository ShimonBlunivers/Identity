package me.blunivers.identity.Items;

import me.blunivers.identity.DataItem;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

public abstract class CustomItem extends DataItem {
    public Material material;
    public CustomItem(String _name) {
        ItemManager.customItems.add(this);
        name = _name;
        displayName = name;
    }
    public CustomItem(String _name, String _displayName) {
        ItemManager.customItems.add(this);
        name = _name;
        displayName = _displayName;
    }

    public abstract ItemStack getItem(String[] args);
    public abstract void useItem(Event event);
}
