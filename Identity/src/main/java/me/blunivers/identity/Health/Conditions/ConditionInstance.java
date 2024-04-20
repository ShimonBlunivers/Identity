package me.blunivers.identity.Health.Conditions;

import me.blunivers.identity.Health.Conditions.Illnesses.Illness;

import java.util.Random;

public class ConditionInstance {

    private int id;
    public ConditionType conditionType;
    public String playerID;
    public int stage;
    public boolean hidden;


    public ConditionInstance(int id, ConditionType conditionType, String playerID, int stage, boolean hidden) {
        this.id = id;
        this.conditionType = conditionType;
        this.playerID = playerID;
        this.stage = stage;
        this.hidden = hidden;
    }

    public void cure() {
//        HealthManager.removeConditionFromPlayer(healthRegistry.player, this);
    }

    @Override
    public String toString() {
        if (conditionType instanceof Illness) {
            Illness illness = (Illness) conditionType;
            return illness.displayName + " [" + stage + "/" + ConditionStatus.maximalStage + "]";
        }
        return conditionType.displayName;
    }
}
