package me.blunivers.identity.Health;


import me.blunivers.identity.Health.Conditions.Condition;
import me.blunivers.identity.Health.Conditions.ConditionInstance;
import me.blunivers.identity.Health.Conditions.Illnesses.Cold;
import me.blunivers.identity.Health.Conditions.Illnesses.Illness;
import me.blunivers.identity.Health.Conditions.Illnesses.Tetanus;
import me.blunivers.identity.Health.Conditions.Vaccine;
import me.blunivers.identity.Health.Conditions.VaccineInstance;
import me.blunivers.identity.Identity;
import me.blunivers.identity.Manager;
import me.blunivers.identity.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;

public class HealthManager extends Manager implements Runnable, Listener {
    private final static HealthManager instance = new HealthManager();

    public static ArrayList<HealthRegistry> healthRegistries = new ArrayList<>();

    public static final ArrayList<Condition> conditions = new ArrayList<>();
    public static final ArrayList<Vaccine> vaccines = new ArrayList<>();

    protected static String path = "";

    // Conditions
    public static final Condition tetanus = new Tetanus();
    public static final Condition cold = new Cold();

    // Vaccines
    public static final Vaccine tetanusVaccine = new Vaccine("Očkování proti Tetanu", (Illness) tetanus);


    @Override
    public void load() {
        path = "health.";

        for (Player player : Bukkit.getOnlinePlayers()) loadPlayer(player);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        healthRegistries.removeIf(healthRegistry -> healthRegistry.player.getUniqueId().equals(player.getUniqueId()));
        loadPlayer(player);
    }

        public void loadPlayer(Player player) {
        HealthRegistry newHealthRegistry = new HealthRegistry(player);
        for (Condition condition : conditions) {
            if (file.isSet(path + "data." + player.getUniqueId() + ".conditions." + condition.name)){
                ConditionInstance newCondition = new ConditionInstance(newHealthRegistry, condition);
                newCondition.setStage((int) file.get(path + "data." + player.getUniqueId() + ".conditions." + condition.name));
                newHealthRegistry.addConditionInstance(newCondition);
            }
        }
        for (Vaccine vaccine : vaccines) {
            if (file.isSet(path + "data." + player.getUniqueId() + ".vaccines." + vaccine.name)) newHealthRegistry.addVaccineInstance(new VaccineInstance(newHealthRegistry, vaccine));
        }

        healthRegistries.add(newHealthRegistry);
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
                healthRegistry.addConditionInstance(new ConditionInstance(healthRegistry, condition));
                file.set(path + "data." + player.getUniqueId() + ".conditions." + condition.name, 1);
                Identity.plugin.saveConfig();
                return;
            }
        HealthRegistry newHealthRegistry = new HealthRegistry(player);
        newHealthRegistry.addConditionInstance(new ConditionInstance(newHealthRegistry, condition));
        healthRegistries.add(newHealthRegistry);

        file.set(path + "data." + player.getUniqueId() + ".conditions." + condition.name, 1);
        Identity.plugin.saveConfig();
    }

    public static void removeConditionFromPlayer(Player player, ConditionInstance conditionInstance) {
        for (HealthRegistry healthRegistry : healthRegistries)
            if (healthRegistry.player.getUniqueId().equals(player.getUniqueId())) {
                if (!healthRegistry.conditionInstances.contains(conditionInstance)) return;
                healthRegistry.removeConditionInstance(conditionInstance);
                file.set(path + "data." + player.getUniqueId() + ".conditions." + conditionInstance.condition.name, null);
                Identity.plugin.saveConfig();
                return;
            }
    }
    public static void addVaccineToPlayer(Player player, Vaccine vaccine) {
        for (HealthRegistry healthRegistry : healthRegistries)
            if (healthRegistry.player.getUniqueId().equals(player.getUniqueId())) {
                healthRegistry.addVaccineInstance(new VaccineInstance(healthRegistry, vaccine));
                file.set(path + "data." + player.getUniqueId() + ".vaccines." + vaccine.name, vaccine.ticksBeforeExpiration);
                Identity.plugin.saveConfig();
                return;
            }
        HealthRegistry newHealthRegistry = new HealthRegistry(player);
        newHealthRegistry.addVaccineInstance(new VaccineInstance(newHealthRegistry, vaccine));
        healthRegistries.add(newHealthRegistry);
        file.set(path + "data." + player.getUniqueId() + ".vaccines." + vaccine.name, vaccine.ticksBeforeExpiration);
        Identity.plugin.saveConfig();
    }

    public static void removeVaccineFromPlayer(Player player, VaccineInstance vaccineInstance) {
        for (HealthRegistry healthRegistry : healthRegistries)
            if (healthRegistry.player.getUniqueId().equals(player.getUniqueId())) {
                if (!healthRegistry.vaccineInstances.contains(vaccineInstance)) return;
                healthRegistry.removeVaccineInstance(vaccineInstance);
                file.set(path + "data." + player.getUniqueId() + ".vaccines." + vaccineInstance.vaccine.name, null);
                Identity.plugin.saveConfig();
                return;
            }
    }


    public void killPlayer(HealthRegistry healthRegistry, Illness illness){
        if (healthRegistry.player.getGameMode().equals(GameMode.CREATIVE)){
            if (illness != null) healthRegistry.player.sendMessage(ChatColor.RED + "<" + illness.displayName + ">" + ChatColor.WHITE + " Máš štěstí, že máš creative, jinak by bylo po tobě!");
            else healthRegistry.player.sendMessage(ChatColor.WHITE + "Díky creativu se ti nic nestalo. Ostraňování všech efektů.");
        } else healthRegistry.player.setHealth(0);

        healthRegistry.conditionInstances = new ArrayList<ConditionInstance>();
        healthRegistry.vaccineInstances = new ArrayList<VaccineInstance>();
        file.set(path + "data." + healthRegistry.player.getUniqueId(), null);
        Identity.plugin.saveConfig();

    }

    public static HealthRegistry getHealthRegistry(Player player) {
        for (HealthRegistry healthRegistry : healthRegistries) if (healthRegistry.player.getUniqueId().equals(player.getUniqueId())) return healthRegistry;
        return null;
    }

    public static void curePlayer(Player player) {
        HealthRegistry healthRegistry = getHealthRegistry(player);
        if (healthRegistry == null) return;

        healthRegistry.conditionInstances = new ArrayList<ConditionInstance>();
        file.set(path + "data." + healthRegistry.player.getUniqueId() + ".conditions", null);
        Identity.plugin.saveConfig();
    }

    public static Vaccine getVaccine(String name) {
        for (Vaccine vaccine : vaccines) if (vaccine.name.equals(name)) return vaccine;
        for (Vaccine vaccine : vaccines) if (vaccine.displayName.equals(name)) return vaccine;
        return null;
    }

    public static Condition getCondition(String name) {
        for (Condition condition : conditions) if (condition.name.equals(name)) return condition;
        for (Condition condition : conditions) if (condition.displayName.equals(name)) return condition;
        return null;
    }

    public void update(int ticks) {
        for (HealthRegistry healthRegistry : healthRegistries) {
            boolean isOnline = false;
            for (Player player : Bukkit.getOnlinePlayers()) if (player.getUniqueId().equals(healthRegistry.player.getUniqueId())) isOnline = true;
            if (!isOnline) continue;
            for (ConditionInstance conditionI : healthRegistry.conditionInstances) {
                int stage = conditionI.stage;
                conditionI.update(ticks);
                if (conditionI.finalStage) {
                    if (conditionI.condition instanceof Illness) killPlayer(healthRegistry, (Illness) conditionI.condition);
                    else killPlayer(healthRegistry, null);
                    break;
                }
                else if (stage != conditionI.stage) {
                    file.set(path + "data." + healthRegistry.player.getUniqueId() + ".conditions." + conditionI.condition.name, conditionI.stage);
                    Identity.plugin.saveConfig();
                }
            }
            for (VaccineInstance vaccineI : healthRegistry.vaccineInstances) {
                vaccineI.update(ticks);
                if (vaccineI.expired) {
                    healthRegistry.vaccineInstances.removeIf(vI -> vI == vaccineI);
                    file.set(path + "data." + healthRegistry.player.getUniqueId() + ".vaccines." + vaccineI.vaccine.name, null);
                    Identity.plugin.saveConfig();
                }
            }
            ScoreboardManager.getInstance().updateScoreboard(healthRegistry.player);
        }
    }

    @Override
    public void run() {
        update(Identity.healthManagerTimer);
    }

}
