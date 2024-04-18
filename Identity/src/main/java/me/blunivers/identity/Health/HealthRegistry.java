package me.blunivers.identity.Health;

import me.blunivers.identity.Health.Conditions.Condition;
import me.blunivers.identity.Health.Conditions.ConditionInstance;
import me.blunivers.identity.Health.Conditions.VaccineInstance;
import me.blunivers.identity.Identity;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class HealthRegistry {
    public Player player;
    public ArrayList<ConditionInstance> conditionInstances = new ArrayList<>();
    public ArrayList<VaccineInstance> vaccineInstances = new ArrayList<>();

    public HealthRegistry(Player _player) {
        player = _player;
    }


    public void addConditionInstance(ConditionInstance conditionInstance) {
        conditionInstances.add(conditionInstance);
    }

    public void removeConditionInstance(ConditionInstance conditionInstance) {
        conditionInstances.removeIf(cI -> cI == conditionInstance);
    }

    public void addVaccineInstance(VaccineInstance vaccineInstance) {
        vaccineInstances.add(vaccineInstance);
    }
    public void removeVaccineInstance(VaccineInstance vaccineInstance) {
        vaccineInstances.removeIf(vI -> vI == vaccineInstance);
    }

    public ConditionInstance getConditionInstance(Condition condition) {
        for (ConditionInstance conditionInstance : conditionInstances) if (conditionInstance.condition == condition) return conditionInstance;
        return null;
    }

    @Override
    public String toString() {
        ArrayList<String> healthString = new ArrayList<>();
        for (ConditionInstance conditionInstance : conditionInstances) if (!conditionInstance.hidden) healthString.add(conditionInstance.toString());
        for (VaccineInstance vaccineInstance : vaccineInstances) healthString.add(vaccineInstance.toString());

        return String.join(", ", healthString);
    }
}
