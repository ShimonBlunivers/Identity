package me.blunivers.identity.Health;

import me.blunivers.identity.Health.Conditions.*;
import me.blunivers.identity.Health.Conditions.Illnesses.Illness;
import me.blunivers.identity.Identity;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.Component;
import me.blunivers.identity.Manager;
import me.blunivers.identity.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class HealthManager extends Manager implements Runnable, Listener {
    private final static HealthManager singleton = new HealthManager();

    private boolean loaded = false;

    @Override
    public void load() {
        Reflections reflections = new Reflections("me.blunivers.identity"); // Specify the package to scan
        Set<Class<? extends ConditionType>> conditionClasses = reflections.getSubTypesOf(ConditionType.class);
        for (Class<? extends ConditionType> conditionClass : conditionClasses) {
            try {
                conditionClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                System.out.println("Failed to condition types! " + e.getMessage());
            }
        }

        // Medications
        new MedicationType("tetanus_vaccine", "Očkování proti Tetanu", (Illness) ConditionType.get("tetanus"));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Player player = event.getPlayer();
    }

    public static ArrayList<String> getHealthConditions(Player player) {
        ArrayList<ConditionInstance> conditionInstances = Identity.database.health_getConditionInstances(player, false);
        ArrayList<MedicationInstance> medicationInstances = Identity.database.health_getMedicationInstances(player);

        ArrayList<String> healthText = new ArrayList<>();

        for (ConditionInstance conditionInstance : conditionInstances) {
            healthText.add(ConditionStatus.getStatus(conditionInstance, medicationInstances)
                    + conditionInstance.conditionType.toString());
        }

        for (MedicationInstance medicationInstance : medicationInstances) {
            if (!medicationInstance.expired)
                healthText.add(NamedTextColor.AQUA + medicationInstance.toString());
        }
        return healthText;
    }

    public static boolean isHealthy(Player player) {
        return Identity.database.health_getConditionInstances(player, false).isEmpty();
    }

    public static HealthManager getSingleton() {
        return singleton;
    }

    public static void addConditionToPlayer(Player player, ConditionType conditionType) {
        Identity.database.health_addCondition(player, conditionType);
        ScoreboardManager.getInstance().updateScoreboard(player);
    }

    public static void removeConditionFromPlayer(Player player, ConditionType conditionType) {
        Identity.database.health_removeCondition(player, conditionType);
        ScoreboardManager.getInstance().updateScoreboard(player);
    }

    public static void addMedicationToPlayer(Player player, MedicationType medicationType) {
        Identity.database.health_addMedication(player, medicationType);
        ScoreboardManager.getInstance().updateScoreboard(player);
    }

    public static void removeMedicationFromPlayer(Player player, MedicationType medicationType) {
        Identity.database.health_removeMedication(player, medicationType);
        ScoreboardManager.getInstance().updateScoreboard(player);
    }

    public void killPlayer(Player player, Illness illness) {
        if (player.getGameMode().equals(GameMode.CREATIVE)) {
            if (illness != null)
                player.sendMessage(Component.text("<" + illness.displayName + ">", NamedTextColor.RED).append(
                        Component.text(" Máš štěstí, že máš creative, jinak by bylo po tobě!", NamedTextColor.WHITE)));
            else
                player.sendMessage(Component.text("Díky creativu se ti nic nestalo. Ostraňování všech efektů.",
                        NamedTextColor.WHITE));
        } else
            player.setHealth(0);
        Identity.database.health_resetEverything(player);
        ScoreboardManager.getInstance().updateScoreboard(player);
    }

    public static void curePlayer(Player player) {
        Identity.database.health_resetConditions(player);
        ScoreboardManager.getInstance().updateScoreboard(player);
    }

    public void update(int ticks) {
        Identity.database.health_updateConditions(ticks);
        Identity.database.health_updateMedications(ticks);
        Random random = new Random();

        Identity.database.health_progressConditions();

        for (Player player : Bukkit.getOnlinePlayers()) {
            for (ConditionInstance conditionInstance : Identity.database.health_getConditionInstances(player, true))
                if (conditionInstance.conditionType instanceof Illness illness) {
                    for (PotionEffectType effectType : conditionInstance.conditionType.effects)
                        player.addPotionEffect(new PotionEffect(effectType, Identity.healthManagerTimer + 2,
                                (1 + conditionInstance.stage / 10)));
                    if (random.nextInt(0, illness.symptomsChance) == 0) {
                        illness.symptoms(player, conditionInstance);
                        Identity.database.health_showCondition(player, conditionInstance.conditionType); // SHOWS THE
                                                                                                         // HIDDEN
                                                                                                         // CONDITION
                    }
                }
        }

        for (ConditionInstance conditionInstance : Identity.database.health_getLethalConditionInstances()) {
            Illness illness = (Illness) conditionInstance.conditionType;
            killPlayer(Bukkit.getPlayer(UUID.fromString(conditionInstance.playerID)), illness);
        }

        Identity.database.health_healConditions();

        ScoreboardManager.getInstance().updateEverything();
    }

    @Override
    public void run() {
        if (!loaded) {
            loaded = true;
            return;
        }
        update(Identity.healthManagerTimer);
    }

}
