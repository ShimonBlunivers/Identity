package me.blunivers.identity.Items.Syringe;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.blunivers.identity.Health.HealthManager;
import me.blunivers.identity.Health.Conditions.ConditionType;
import me.blunivers.identity.Health.Conditions.MedicationType;
import me.blunivers.identity.Health.Conditions.Illnesses.Illness;
import me.blunivers.identity.Items.CustomItem;
import me.blunivers.identity.Items.ItemManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class Syringe extends CustomItem {
    public Syringe() {
        super("Syringe", "Injekce");
        material = Material.PRISMARINE_SHARD;
    }

    @Override
    public ItemStack getItem(String[] content) {
        List<MedicationType> vaccines = new ArrayList<>();
        List<Illness> illnesses = new ArrayList<>();
        List<Illness> medications = new ArrayList<>();

        for (String entry : content) {
            MedicationType medicationType = MedicationType.get(entry);
            ConditionType conditionType = ConditionType.get(entry);

            if (conditionType instanceof Illness illness) {
                illnesses.add(illness);
            }

            String[] meds = entry.split("Cure");
            if (meds.length > 0) {
                Illness cureAgainst = (Illness) ConditionType.get(meds[0]);
                if (cureAgainst != null) {
                    medications.add(cureAgainst);
                }
            }

            // Optional: Handle medicationType.protectionAgainst
            // if (medicationType != null) vaccines.add(...);
        }

        ItemStack itemStack = new ItemStack(material, 1);
        ItemMeta meta = itemStack.getItemMeta();

        meta.displayName(Component.text(displayName));

        List<Component> lore = new ArrayList<>();

        if (!vaccines.isEmpty()) {
            lore.add(Component.text(ItemManager.listTitleMark + "Očkování proti:", NamedTextColor.GRAY));
            for (MedicationType medicationType : vaccines) {
                lore.add(Component.text(ItemManager.listItemMark + "Syringe.java dodelat -58", NamedTextColor.GREEN));
            }
        }

        if (!medications.isEmpty()) {
            lore.add(Component.text(ItemManager.listTitleMark + "Protilátky proti:", NamedTextColor.GRAY));
            for (Illness illness : medications) {
                lore.add(Component.text(ItemManager.listItemMark + illness.displayName, NamedTextColor.DARK_GREEN));
            }
        }

        if (!illnesses.isEmpty()) {
            lore.add(Component.text(ItemManager.listTitleMark + "Nemoci:", NamedTextColor.GRAY));
            for (Illness illness : illnesses) {
                lore.add(Component.text(ItemManager.listItemMark + illness.displayName, NamedTextColor.RED));
            }
        }

        if (lore.isEmpty()) {
            lore.add(Component.text("Prázdná", NamedTextColor.YELLOW).decorate(TextDecoration.ITALIC));
        }

        meta.lore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES); // Maybe doesn't work?
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    @Override
    public void useItem(Event e) {
        if (!(e instanceof PlayerInteractAtEntityEvent))
            return;
        PlayerInteractAtEntityEvent event = (PlayerInteractAtEntityEvent) e;
        // if (!(event.getRightClicked() instanceof Player)) return;

        Player sender = event.getPlayer();

        // Player target = (Player) event.getRightClicked();
        Player target = sender;
        ItemStack usedItem = sender.getInventory().getItem(sender.getInventory().getHeldItemSlot());
        ItemMeta meta = usedItem.getItemMeta();
        sender.getInventory().setItem(sender.getInventory().getHeldItemSlot(),
                ItemManager.syringe.getItem(new String[] {}));

        List<Component> lore = meta.lore();
        String title = "";

        if (lore == null) {
            return;
        }
        for (Component lineComponent : lore) {
            String plainLine = PlainTextComponentSerializer.plainText().serialize(lineComponent);

            if (plainLine.contains(ItemManager.listTitleMark)) {
                title = plainLine;
                break;
            }
            String listItem = plainLine.replace(ItemManager.listItemMark, "");
            sender.sendMessage(Component.text(listItem));

            if (title.contains("Očkování")) {
                HealthManager.addMedicationToPlayer(target, MedicationType.get(listItem));
                target.sendMessage(Component.text("1"));
            }

            if (title.contains("Nemoci")) {
                HealthManager.addConditionToPlayer(target, ConditionType.get(listItem));
                target.sendMessage(Component.text("2"));
            }
        }
    }
}
