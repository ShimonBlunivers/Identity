package me.blunivers.identity.Health;


import me.blunivers.identity.Health.Conditions.*;
import me.blunivers.identity.Health.Conditions.Illnesses.Cold;
import me.blunivers.identity.Health.Conditions.Illnesses.Illness;
import me.blunivers.identity.Health.Conditions.Illnesses.Tetanus;
import me.blunivers.identity.Identity;
import me.blunivers.identity.Items.Syringe.Syringe;
import me.blunivers.identity.Manager;
import me.blunivers.identity.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class HealthManager extends Manager implements Runnable, Listener {
    private final static HealthManager instance = new HealthManager();

    @Override
    public void load() {
        // Conditions
        new Tetanus();
        new Cold();

        // Medications
        new MedicationType("tetanus_vaccine", "Očkování proti Tetanu", (Illness) ConditionType.get("tetanus"));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
    }

    public static ArrayList<String> getHealthConditions(Player player) {
        ArrayList<ConditionInstance> conditionInstances = Identity.database.health_getConditionInstances(player, false);
        ArrayList<MedicationInstance> medicationInstances = Identity.database.health_getMedicationInstances(player);

        ArrayList<String> healthText = new ArrayList<>();

        for (ConditionInstance conditionInstance : conditionInstances){
            healthText.add(ConditionStatus.getStatus(conditionInstance, medicationInstances) + conditionInstance.conditionType.toString());
        }

        for (MedicationInstance medicationInstance : medicationInstances){
            if (!medicationInstance.expired) healthText.add(ChatColor.AQUA + medicationInstance.toString());
        }
        return healthText;
    }

    public static boolean isHealthy(Player player){
        return Identity.database.health_getConditionInstances(player, false).isEmpty();
    }

    public static HealthManager getInstance() {
        return instance;
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


    public void killPlayer(Player player, Illness illness){
        if (player.getGameMode().equals(GameMode.CREATIVE)){
            if (illness != null) player.sendMessage(ChatColor.RED + "<" + illness.displayName + ">" + ChatColor.WHITE + " Máš štěstí, že máš creative, jinak by bylo po tobě!");
            else player.sendMessage(ChatColor.WHITE + "Díky creativu se ti nic nestalo. Ostraňování všech efektů.");
        } else player.setHealth(0);
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
                    for (PotionEffectType effectType : conditionInstance.conditionType.effects) player.addPotionEffect(new PotionEffect(effectType, Identity.healthManagerTimer + 2, (1 + conditionInstance.stage / 10)));
                    if (random.nextInt(0, illness.symptomsChance) == 0) {
                        illness.symptoms(player);
                        Identity.database.health_showCondition(player, conditionInstance.conditionType); // SHOWS THE HIDDEN CONDITION
                    }
                }
        }

        for (ConditionInstance conditionInstance : Identity.database.health_getLethalConditionInstances()){
            Illness illness = (Illness) conditionInstance.conditionType;
            killPlayer(Bukkit.getPlayer(UUID.fromString(conditionInstance.playerID)), illness);
        }


        Identity.database.health_healConditions();

        ScoreboardManager.getInstance().updateEverything();
    }

    @Override
    public void run() {
        update(Identity.healthManagerTimer);
    }

}
