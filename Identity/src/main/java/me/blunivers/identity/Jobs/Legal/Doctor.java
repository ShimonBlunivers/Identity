package me.blunivers.identity.Jobs.Legal;


import me.blunivers.identity.Health.Conditions.Vaccine;
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
    public Doctor(String _displayName) {
        super("Doctor");

        displayName = _displayName;
    }

    @Override
    public void work(Player player, Occupation occupation) {

    }

}
