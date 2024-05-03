package me.blunivers.identity.Health.Conditions.Illnesses;

import me.blunivers.identity.Health.Conditions.ConditionInstance;
import org.bukkit.entity.Player;

public class Tetanus extends Illness {
    public Tetanus() {
        super("tetanus","Tetanus", 500);
        symptomsChance = 40;
    }

    @Override
    public void symptoms(Player player, ConditionInstance conditionInstance) {
        player.damage(3 * conditionInstance.getSymptomMultiplier());
    }
}
