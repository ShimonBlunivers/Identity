package me.blunivers.identity.Health.Conditions;

import me.blunivers.identity.DataItem;
import me.blunivers.identity.Health.HealthManager;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public abstract class Condition extends DataItem {

    protected ArrayList<PotionEffectType> effects = new ArrayList<>();

    public Condition(String _name, String _displayName) {
        name = _name;
        displayName = _displayName;
        HealthManager.conditions.add(this);
    }
}
