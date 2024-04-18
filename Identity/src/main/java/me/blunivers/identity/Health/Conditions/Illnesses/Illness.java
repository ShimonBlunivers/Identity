package me.blunivers.identity.Health.Conditions.Illnesses;

import me.blunivers.identity.Health.Conditions.Condition;
import org.bukkit.entity.Player;

public class Illness extends Condition {
    public int ticksToNextStage; // 24000
    public int lethalStage;

    public int symptomsTimer = 20;

    public Illness(String _name, String _displayName, int _ticksToNextStage, int _lethalStage) {
        super(_name, _displayName);
        ticksToNextStage = _ticksToNextStage;
        lethalStage = _lethalStage;
    }

    public void symptoms(Player player) {
    }
}
