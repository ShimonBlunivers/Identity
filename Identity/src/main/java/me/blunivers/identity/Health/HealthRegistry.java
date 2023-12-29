package me.blunivers.identity.Health;

import me.blunivers.identity.Health.Conditions.ConditionInstance;
import me.blunivers.identity.Health.Conditions.VaccineInstance;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class HealthRegistry {
    public Player player;
    private ArrayList<ConditionInstance> conditionInstances = new ArrayList<>();
    private ArrayList<VaccineInstance> vaccineInstances = new ArrayList<>();

    public HealthRegistry(Player _player) {
        player = _player;
    }


    // Přidat do databáze!!!!!!
    public void addConditionInstance(ConditionInstance conditionInstance) {
        conditionInstances.add(conditionInstance);


    }
    public void addVaccineInstance(VaccineInstance vaccineInstance) {
        vaccineInstances.add(vaccineInstance);
    }

    public void killPlayer() {
        player.setHealth(0);
        conditionInstances = new ArrayList<>();
        vaccineInstances = new ArrayList<>();
    }

    public void update(int ticks) {
        for (ConditionInstance conditionI : conditionInstances) {
            conditionI.update(ticks);
            if (conditionI.finalStage) killPlayer();
        }
        for (VaccineInstance vaccineI : vaccineInstances) {
            vaccineI.update(ticks);
            if (vaccineI.expired) vaccineInstances.remove(vaccineI);
        }
    }

    @Override
    public String toString() {
        ArrayList<String> healthString = new ArrayList<>();
        for (ConditionInstance conditionInstance : conditionInstances) healthString.add(conditionInstance.toString());
        for (VaccineInstance vaccineInstance : vaccineInstances) healthString.add(vaccineInstance.toString());

        return String.join(", ", healthString);
    }
}
