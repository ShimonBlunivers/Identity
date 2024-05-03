package me.blunivers.identity.Items.Syringe;

import me.blunivers.identity.Health.Conditions.ConditionType;
import me.blunivers.identity.Health.Conditions.Illnesses.Illness;
import me.blunivers.identity.Health.Conditions.MedicationType;
import me.blunivers.identity.Health.HealthManager;
import me.blunivers.identity.Items.CustomItem;
import me.blunivers.identity.Items.ItemManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Syringe extends CustomItem {
    public Syringe() {
        super("Syringe", "Injekce");
        material = Material.PRISMARINE_SHARD;
    }
    @Override
    public ItemStack getItem(String[] content) {
        ArrayList<MedicationType> vaccines = new ArrayList<>();
        ArrayList<Illness> illnesses = new ArrayList<>();
        ArrayList<Illness> medications = new ArrayList<>();

        for (String entry : content) {
            MedicationType medicationType = MedicationType.get(entry);
            ConditionType conditionType = ConditionType.get(entry);

//            if (medicationType != null) for (ConditionType against : medicationType.protectionAgainst) vaccines.add(against.displayName);
            if (conditionType instanceof Illness) illnesses.add((Illness) conditionType);

            String[] meds = entry.split("Cure");

            if (meds.length > 0) {
                Illness cureAgainst = (Illness) ConditionType.get(meds[0]);
                if (cureAgainst != null) medications.add(cureAgainst);
            }
        }

        ItemStack itemStack;
        itemStack = new ItemStack(material, 1);

        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(displayName);

        ArrayList<String> lore = new ArrayList<>();

        if (!vaccines.isEmpty()) {
            lore.add(ItemManager.listTitleMark + ChatColor.GRAY + "Očkování proti:");
            for (MedicationType medicationType : vaccines) lore.add(ChatColor.GREEN + ItemManager.listItemMark + "Syringe.java dodelat -58");
        }
        if (!medications.isEmpty()) {
            lore.add(ItemManager.listTitleMark +  ChatColor.GRAY + "Protilátky proti:");
            for (Illness illness : medications) lore.add(ChatColor.DARK_GREEN + ItemManager.listItemMark  + illness.displayName);
        }
        if (!illnesses.isEmpty()) {
            lore.add(ItemManager.listTitleMark + ChatColor.GRAY + "Nemoci:");
            for (Illness illness : illnesses) lore.add(ChatColor.RED + ItemManager.listItemMark + illness.displayName);
        }
        if (lore.isEmpty()) lore.add(ChatColor.YELLOW + "" + ChatColor.ITALIC + "Prázdná");

        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

        itemStack.setItemMeta(meta);

        return itemStack;
    }


    @Override
    public void useItem(Event e) {
        if (!(e instanceof PlayerInteractAtEntityEvent)) return;
        PlayerInteractAtEntityEvent event = (PlayerInteractAtEntityEvent) e;
//        if (!(event.getRightClicked() instanceof Player)) return;

        Player sender = event.getPlayer();

//        Player target = (Player) event.getRightClicked();
        Player target = sender;
        ItemStack usedItem = sender.getInventory().getItem(sender.getInventory().getHeldItemSlot());
        ItemMeta meta = usedItem.getItemMeta();
        sender.getInventory().setItem(sender.getInventory().getHeldItemSlot(), ItemManager.syringe.getItem(new String[]{}));

        List<String> args = meta.getLore();

        String title = "";

        for (String listItem : args) {
            if (listItem.contains(ItemManager.listTitleMark)) title = listItem;
            else {
                listItem = ChatColor.stripColor(listItem.replace(ItemManager.listItemMark, ""));
                sender.chat(listItem);

                if (title.contains("Očkování")) {
                    HealthManager.addMedicationToPlayer(target, MedicationType.get(listItem));
                    target.chat("1");
                }

                if (title.contains("Nemoci")) {
                    HealthManager.addConditionToPlayer(target, ConditionType.get(listItem));
                    target.chat("2");
                }
            }
        }
    }
}
