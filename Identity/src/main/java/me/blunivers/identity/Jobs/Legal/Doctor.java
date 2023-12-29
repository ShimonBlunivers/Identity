package me.blunivers.identity.Jobs.Legal;


import me.blunivers.identity.Health.Conditions.Illnesses.Illness;
import me.blunivers.identity.Health.Vaccine;
import me.blunivers.identity.Jobs.LegalJob;
import me.blunivers.identity.Jobs.Occupation;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;


public class Doctor extends LegalJob {

    private static final Material emptySyringeMaterial = Material.ARROW;
    private static final Material syringeMaterial = Material.TIPPED_ARROW;

    public Doctor(String _displayName) {
        super("Doctor");

        displayName = _displayName;
    }

    @Override
    public void work(Player player, Occupation occupation) {

    }

    public static ItemStack getSyringe(Vaccine vaccine) {
        ItemStack itemStack;
        if (vaccine == null) itemStack = new ItemStack(emptySyringeMaterial, 1);
        else itemStack = new ItemStack(syringeMaterial, 1);

        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName("Injekce");

        ArrayList<String> lore = new ArrayList<>();

        if (vaccine != null) {
            lore.add(ChatColor.GRAY + "Očkování proti:");
            lore.add(ChatColor.GOLD + vaccine.protectionAgainst.displayName);
        }
        else lore.add(ChatColor.YELLOW + "Prázdná");

        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
