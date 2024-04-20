package me.blunivers.identity.Health.Conditions;

import me.blunivers.identity.DataItem;
import me.blunivers.identity.Health.HealthManager;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class ConditionType extends DataItem {



    public static final HashMap<String, ConditionType> conditions = new HashMap<>();
    protected ArrayList<PotionEffectType> effects = new ArrayList<>();

    public ConditionType(String _name, String _displayName) {
        name = _name;
        displayName = _displayName;
        conditions.put(name, this);
    }

    public static ConditionType get(String conditionName){
        return conditions.get(conditionName);
    }
}
