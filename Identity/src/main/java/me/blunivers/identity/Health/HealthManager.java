package me.blunivers.identity.Health;


import me.blunivers.identity.Health.Conditions.ConditionType;
import me.blunivers.identity.Health.Conditions.ConditionInstance;
import me.blunivers.identity.Health.Conditions.Illnesses.Cold;
import me.blunivers.identity.Health.Conditions.Illnesses.Illness;
import me.blunivers.identity.Health.Conditions.Illnesses.Tetanus;
import me.blunivers.identity.Health.Conditions.MedicationType;
import me.blunivers.identity.Health.Conditions.MedicationInstance;
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

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class HealthManager extends Manager implements Runnable, Listener {
    private final static HealthManager instance = new HealthManager();
    // Conditions
    public static final ConditionType tetanus = new Tetanus();
    public static final ConditionType cold = new Cold();

    // Vaccines
    public static final MedicationType tetanusVaccine = new MedicationType("Očkování proti Tetanu", (Illness) tetanus);


    @Override
    public void load() {

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
    }

    public static String getHealthConditions(Player player) {
        ArrayList<ConditionInstance> conditionInstances = Identity.database.health_getConditionInstances(player, false);
        ArrayList<MedicationInstance> medicationInstances = Identity.database.health_getMedicationInstances(player);

        ArrayList<String> healthText = new ArrayList<>();

        for (ConditionInstance conditionInstance : conditionInstances){
            healthText.add(conditionInstance.conditionType.toString());
        }

        for (MedicationInstance medicationInstance : medicationInstances){
            healthText.add(ChatColor.BLUE + medicationInstance.toString());
        }
        return String.join(", ", healthText);
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
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (ConditionInstance conditionInstance : Identity.database.health_getConditionInstances(player, true))
                if (conditionInstance.conditionType instanceof Illness) {
                    Illness illness = (Illness) conditionInstance.conditionType;
                    if (random.nextInt(0, illness.symptomsChance) == 0) {
                        illness.symptoms(player);
                        Identity.database.health_showCondition(player, conditionInstance.conditionType);
                    }
                }
            for (ConditionInstance conditionInstance : Identity.database.health_getProgressedConditionInstances(player))
                Identity.database.health_progressCondition(player, conditionInstance.conditionType);
        }
        ScoreboardManager.getInstance().updateEverything();
    }

    @Override
    public void run() {
        update(Identity.healthManagerTimer);
    }

}
