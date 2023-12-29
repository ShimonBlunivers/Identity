package me.blunivers.identity.Health;


import me.blunivers.identity.Health.Conditions.Condition;
import me.blunivers.identity.Health.Conditions.ConditionInstance;
import me.blunivers.identity.Health.Conditions.Illnesses.Illness;
import me.blunivers.identity.Health.Conditions.VaccineInstance;
import me.blunivers.identity.Identity;
import me.blunivers.identity.Manager;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class HealthManager extends Manager implements Runnable {
    private final static HealthManager instance = new HealthManager();

    public static ArrayList<HealthRegistry> healthRegistries = new ArrayList<>();

    public static final ArrayList<Condition> conditions = new ArrayList<>();
    public static final ArrayList<Vaccine> vaccines = new ArrayList<>();


    // Conditions
    public static final Condition tetanus = new Illness("Tetanus", 7);
    public static final Condition cold = new Illness("Rýma", 10);

    // Vaccines
    public static final Vaccine tetanusVaccine = new Vaccine("Očkování proti Tetanu", (Illness) tetanus);


    @Override
    public void load() {
        path = "health.";
    }

    public static String getHealthConditions(Player player) {
        for (HealthRegistry healthRegistry : healthRegistries)
            if (healthRegistry.player.getUniqueId().equals(player.getUniqueId())) {
                return healthRegistry.toString();
            }

        return "";
    }

    public static HealthManager getInstance() {
        return instance;
    }

    public static void addConditionToPlayer(Player player, Condition condition) {
        for (HealthRegistry healthRegistry : healthRegistries)
            if (healthRegistry.player.getUniqueId().equals(player.getUniqueId())) {
                healthRegistry.addConditionInstance(new ConditionInstance(condition));
                file.set(path + "data." + player.getUniqueId() + ".conditions." + condition.name, 1);
                return;
            }
        HealthRegistry newHealthRegistry = new HealthRegistry(player);
        newHealthRegistry.addConditionInstance(new ConditionInstance(condition));
        healthRegistries.add(newHealthRegistry);

        file.set(path + "data." + player.getUniqueId() + ".conditions." + condition.name, 1);

    }
    public static void addVaccineToPlayer(Player player, Vaccine vaccine) {
        for (HealthRegistry healthRegistry : healthRegistries)
            if (healthRegistry.player.getUniqueId().equals(player.getUniqueId())) {
                healthRegistry.addVaccineInstance(new VaccineInstance(vaccine));
                file.set(path + "data." + player.getUniqueId() + ".vaccines." + vaccine.name, vaccine.ticksBeforeExpiration);
                return;
            }
        HealthRegistry newHealthRegistry = new HealthRegistry(player);
        newHealthRegistry.addVaccineInstance(new VaccineInstance(vaccine));
        healthRegistries.add(newHealthRegistry);
        file.set(path + "data." + player.getUniqueId() + ".vaccines." + vaccine.name, vaccine.ticksBeforeExpiration);
        Identity.plugin.saveConfig();
    }

    public void update(int ticks) {
        for (HealthRegistry healthRegistry : healthRegistries) healthRegistry.update(ticks);
    }

    @Override
    public void run() {
        update(Identity.healthManagerTimer);
    }

}
