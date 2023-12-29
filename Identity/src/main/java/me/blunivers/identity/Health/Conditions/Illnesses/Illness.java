package me.blunivers.identity.Health.Conditions.Illnesses;

import me.blunivers.identity.Health.Conditions.Condition;

public class Illness extends Condition {
    public int ticksToNextStage = 40; // 24000

    public int lethalStage;

    public Illness(String _displayName, int _lethalStage) {
        super(_displayName);
        lethalStage = _lethalStage;
    }
}
