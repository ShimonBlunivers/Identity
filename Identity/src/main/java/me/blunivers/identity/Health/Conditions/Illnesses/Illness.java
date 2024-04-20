package me.blunivers.identity.Health.Conditions.Illnesses;

import me.blunivers.identity.Health.Conditions.ConditionType;
import org.bukkit.entity.Player;

public class Illness extends ConditionType {
    public int ticksToNextStage; // 24000
    public int symptomsChance = 10;

    public Illness(String _name, String _displayName, int _ticksToNextStage) {
        super(_name, _displayName);
        ticksToNextStage = _ticksToNextStage;
    }

    public void symptoms(Player player) {
    }
}
